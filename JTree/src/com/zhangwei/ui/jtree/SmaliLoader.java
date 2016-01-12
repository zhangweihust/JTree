package com.zhangwei.ui.jtree;

import java.awt.Component;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.ProgressMonitor;

import com.google.gson.Gson;
import com.zhangwei.smali.api.FieldEntry;
import com.zhangwei.smali.api.MethodEntry;
import com.zhangwei.smali.api.MyParser;
import com.zhangwei.smali.api.SmaliEntry;
import com.zhangwei.smali.json.SaveState;
import com.zhangwei.utils.StringHelper;

public class SmaliLoader {
	private static SmaliLoader ins=null;
	
	private String confPath;

	private Set<SmaliEntry> globeClassSet = new HashSet<SmaliEntry>(); //path --> smaliEntry , 只用到了value，key没有使用到

	
	private MyParser myParser;
	
	private ProgressMonitor monitor;
	private int progress;
	private SmaliEntry root;
	private ArrayList<File> fileList;
	private Map<String, SmaliEntry> packageSet = new HashMap<String, SmaliEntry>();
	

	
	private SmaliLoader(){
		fileList = new ArrayList<File>();
		myParser = new MyParser();
	}
	
	public static SmaliLoader getInstance(){
		if(ins==null){
			ins = new SmaliLoader();
		}
		
		return ins;
	}
	
	/**
	 * 注意：重命名该类时，需要同步将子类的it.classHeader.classNameSuper也改过来
	 * 子类的smali一定有父类名，但是父类不一定有子类名
	 * @throws IOException 
	 * */
	public boolean renameClass(SmaliEntry se, String src_className, String dst_className, boolean needSyncDisk) {
		boolean succ = false;
		try {
			succ = se.renameClassFile(src_className, dst_className);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(succ){
			//谁使用(引用)了它
			Set<SmaliEntry> reItfList = se.getRefItClassList();
			for(SmaliEntry se_item : reItfList) {
				if(se_item!=null && se_item!=se){
					if(se_item.fatherClass == se){
						//如果 ‘谁/se_item’ 是子类
						String src_className2 = src_className; //StringHelper.escapeExprSpecialWord(src_className);
						String dst_className2 = dst_className; //StringHelper.escapeExprSpecialWord(dst_className);
						se_item.classHeader.classSuper.replace(src_className2, dst_className2);
						se_item.classHeader.classNameSuper = dst_className;
					}
					se_item.renameClassContent(src_className, dst_className);
					if(needSyncDisk){
						se_item.setFileContent();
					}
					
				}
			}
			
			
			se.renameClass(src_className, dst_className);
			se.renameClassContent(src_className, dst_className);
			
			if(needSyncDisk){
				se.setFileContent();
			}

					
		}

		return succ;
	}
	
	public void renameField(SmaliEntry se, FieldEntry fe, String s) {
		// TODO Auto-generated method stub
		String className = se.classHeader.classNameSelf; // Lcom/b/a/a;
		String oldFieldName = fe.classFieldName;
		String newFieldName = s;
		
		Iterator<SmaliEntry> iterator = globeClassSet.iterator();
		while(iterator.hasNext()){
			SmaliEntry se_item = iterator.next();
			se_item.renameFieldContent(className, oldFieldName, newFieldName, fe.classFieldType);
		}
		
		
		fe.RenameName(className, oldFieldName, newFieldName, fe.classFieldType);
		
		se.setFileContent();
	}
	
	public void renameMethod(SmaliEntry se, MethodEntry me, String s) {
		// TODO Auto-generated method stub
		String className = se.classHeader.classNameSelf; // Lcom/b/a/a;
		String oldMethodName = me.classMethodName;
		String newMethodName = me.classConstructorName!=null?null:s;
		

		
		if(newMethodName!=null){
			Iterator<SmaliEntry> iterator = globeClassSet.iterator();
			while(iterator.hasNext()){
				SmaliEntry se_item = iterator.next();
				se_item.renameMethodContent(className, oldMethodName, newMethodName, me.classMethodProto);
			}
			
			me.RenameName(className, oldMethodName, newMethodName, me.classMethodProto);
		}

		se.setFileContent();
	}
	
	private void upPublic(SmaliEntry se) {
		// TODO Auto-generated method stub
		se.upPublic();
		se.setFileContent();
	}
	
	/**
	 * @return 返回SmaliEntry ,  已应用新的命名 Lx/y/z;
	 * @throws IOException 
	 * */
	private void autoRename(SmaliEntry it) throws IOException{

    	if(monitor.isCanceled()){
    		return ;
    	}
    	

    	if(StringHelper.needRename(it.toString())){//only rename the a,b,c,d ...or ab
        	if(it.classHeader==null || it.classHeader.classNameSelf==null){
        		System.err.println("class parse null:" + it.toString() + ", classHeader:" + it.classHeader);
        		return ;
        	}
        	
        	
        	String old_base_className = it.classHeader.classNameSelf; //  La/b/c;
        	String old_tmp_name = old_base_className.substring(0, old_base_className.length()-1); //去掉; La/b/c
        	String new_tmp_name = old_tmp_name + "_Class";//La/b/c_Class // + StringHelper.getShortNameOfSmali(it.classHeader.classNameSuper); //La/b/c_Object
			String new_dst_className = new_tmp_name + ";"; //La/b/c_Object;
			
        	String old_base_className_prefix = old_tmp_name + "$";  //La/b/c$
        	String new_base_className_prefix = new_tmp_name + "$";  //La/b/c_Object$
        	
			//1. rename本身
			renameClass(it, it.classHeader.classNameSelf, new_dst_className, true);
			
			//2. rename本类内部实现的匿名类
			Iterator<SmaliEntry> iterator = globeClassSet.iterator();
			while(iterator.hasNext()){
				SmaliEntry value = iterator.next();
				String clzName = value.classHeader.classNameSelf;

				String regStr = null;
				String new_base_className_prefix2 = null;
				if(clzName.startsWith(old_base_className_prefix)){
					try{
						regStr = old_base_className_prefix; //StringHelper.escapeExprSpecialWord(old_base_className_prefix);
						new_base_className_prefix2 = new_base_className_prefix; //StringHelper.escapeExprSpecialWord(new_base_className_prefix);
						String newKey = clzName.replace(regStr, new_base_className_prefix2);
						renameClass(value, clzName, newKey, false);
					}catch (Exception e){
					  e.printStackTrace();
					  System.out.println("value:" + value.toString() + ", clzName:" + clzName + ", old_base_className_prefix:" + old_base_className_prefix + ", new_base_className_prefix2:" + new_base_className_prefix2 + ", regStr:" + regStr);
					}

					
				}
			}
			
			
	    	progress++;
	    	monitor.setProgress(progress);
	    	monitor.setNote("Renamed num:" + progress + "\n file:" + it.file.getAbsolutePath());
    	}else{
//    		new_classMap.put(it.classHeader.classNameSelf, it);
	    	progress++;
	    	monitor.setProgress(progress);
	    	monitor.setNote("No Need to rename \n file:" + it.file.getAbsolutePath());
    	}

	}
	
	
	public void autoRename(Component parent) {
		if(root!=null && root.leafChildren!=null && root.leafChildren.size()>0){
			if(globeClassSet!=null && globeClassSet.size()>0){
				monitor = new ProgressMonitor(parent, "Renaming Progress", "Getting Started...", 0, globeClassSet.size());
				progress = 0;
				
				Iterator<SmaliEntry> iterator = globeClassSet.iterator();
				while(iterator.hasNext()){
					SmaliEntry se_item = iterator.next();
					if(se_item.classHeader.classNameSelf.contains("$")){
						continue;
					}
					try {
						autoRename(se_item);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					//sync map after every rename
//					refacoterGlobeClassMap(parent);
				}
				
				monitor.close();
				
			}
		}
		

		System.out.println("autoRename done");
	}
	
	public void autoPublic(Component parent){
		if(root!=null && root.leafChildren!=null && root.leafChildren.size()>0){
			if(globeClassSet!=null && globeClassSet.size()>0){
				monitor = new ProgressMonitor(parent, "Renaming Progress", "Getting Started...", 0, globeClassSet.size());
				progress = 0;
				
				Iterator<SmaliEntry> iterator = globeClassSet.iterator();
				while(iterator.hasNext()){
					SmaliEntry se_item = iterator.next();
					progress++;
	            	monitor.setProgress(progress);
	            	monitor.setNote("Up-Public num:" + progress + "\n file:" + se_item.file.getAbsolutePath());
	            	if(monitor.isCanceled()){
	            		break;
	            	}	            	
	            	upPublic(se_item);
				}
				

			}
		}
	}
	
	public void loadState(Component parent, SaveState state) {
		// TODO Auto-generated method stub
		globeClassSet = state.globeClassSet;
		this.root = state.root;

		monitor = new ProgressMonitor(parent, "Loading Progress", "Getting Started...", 0, globeClassSet.size());

		if (globeClassSet.size() > 0 && !monitor.isCanceled()) {

			monitor = new ProgressMonitor(parent, "Buld smali relationship", "Getting Started...", 0, globeClassSet.size());
			progress = 0;

			Iterator<SmaliEntry> iterator = globeClassSet.iterator();
			iterator = globeClassSet.iterator();

			while (iterator.hasNext()) {
				SmaliEntry se = iterator.next();

				progress++;
				monitor.setProgress(progress);
				monitor.setNote("loadState num:" + progress + "\n SmaliEntry:" + se.toString());
				if (monitor.isCanceled()) {
					break;
				}

				if (se == null || se.classHeader == null) {
					continue;
				}
				
				se.postConstructFromGson();
				
        		SmaliEntry packageEntry = findEntry(se.packageName); //查找该节点的父节点，即包
        		
        		//add non-leaf package if not exist
            	if(packageEntry==null){
            		packageEntry = new SmaliEntry(null, se.packageName, false);
            		root.leafChildren.add(packageEntry);
            		packageSet.put(se.packageName, packageEntry);
            	}
            	
            	//add leaf
            	packageEntry.leafChildren.add(se);
            	
            	
            	

				Set<SmaliEntry> tmpClassSet = new HashSet<SmaliEntry>();
				tmpClassSet.addAll(globeClassSet);

				Iterator<SmaliEntry> iterator2 = tmpClassSet.iterator();
				while (iterator2.hasNext()) {
					SmaliEntry se2 = iterator2.next();
					if (se2 != null && se != se2) {
						if (se2.classHeader == null || se2.content == null) {
							continue;
						}

						if (se2.content.contains(se.classHeader.classNameSelf)) {
							// se在 se2的文件中，说明 se被se2使用， se2使用了se
							se.putRefItClass(se2);
							se2.putItRefClass(se);
						}

						// 构建父类关系
						if (se2.classHeader.classNameSuper != null && se2.classHeader.classNameSuper.equals(se.classHeader.classNameSelf)) {
							// se2's father == se
							se2.setFatherClass(se);
						}
					}
				}

			}

			monitor.close();
		}
	}
	
	public void saveState(Component parent) {
		// TODO Auto-generated method stub
		Gson gson = new Gson();
		
		FileWriter writer = null;
		try {
			SaveState state = new SaveState();
			state.globeClassSet = globeClassSet;
			state.root = root;
			state.confPath = confPath;
			
			monitor = new ProgressMonitor(parent, "saveState Progress", "Getting Started...", 0, 1);
			
			writer = new FileWriter(state.confPath);
			gson.toJson(state, writer);
			
			writer.close();
			
			monitor.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}

	public void loadRoot(Component parent, SmaliEntry root, File[] dirs){
		this.root = root;
		String root_path = root.file.getAbsolutePath();
		
		if(root_path!=null){
			if(!root_path.endsWith(File.separator/*"\\"*/)){
				root_path = root_path + File.separator/*'\\'*/;
			}
		}else{
			return ;
		}
		
		this.confPath = root_path + "saveState.conf";
		System.out.println("loadRoot - File.separator : " + File.separator + ", root_path:" + root_path + ", confPath:" + confPath);
		
		if(dirs.length<=0){
			return;
		}
		

		
		SmaliEntry.rootPath = root_path;
		
		if(fileList!=null){
			fileList.clear();
		}

		for(File dir : dirs){
			loadChildren(dir/*root.file*/);
		}

		
		String errFileName = null;
		
		try{
			
			if(fileList.size()>0){
				monitor = new ProgressMonitor(parent, "Loading Progress", "Getting Started...", 0, fileList.size());

				progress = 0;
				
	            for (File file : fileList) {
	            	progress++;
	            	monitor.setProgress(progress);
	            	monitor.setNote("Loaded num:" + progress + "\n file:" + file.getAbsolutePath());

	            	if(monitor.isCanceled()){
	            		break;
	            	}
	            	
	            	errFileName = file.getAbsolutePath();
	            	
                	SmaliEntry packageEntry = null;
                	SmaliEntry smaliEntry = new SmaliEntry(file, null, true);
                	boolean Parse_success = myParser.paser(smaliEntry);
                	
                	
                	if(Parse_success){
                		smaliEntry.postProcess();
                		
                		packageEntry = findEntry(smaliEntry.packageName); //查找该节点的父节点，即包
                		
                		//add non-leaf package if not exist
    	            	if(packageEntry==null){
    	            		packageEntry = new SmaliEntry(null, smaliEntry.packageName, false);
    	            		root.leafChildren.add(packageEntry);
    	            		packageSet.put(smaliEntry.packageName, packageEntry);
    	            	}
    	            	
    	            	//add leaf
    	            	packageEntry.leafChildren.add(smaliEntry);
    	            	globeClassSet.add(smaliEntry);
                	}else{
                		break; //abort if has error in parsing
                	}

               
	            }

			}
		}catch(Exception e){
			e.printStackTrace();
			monitor.setProgress(0);
        	monitor.setNote("Loaded fail:" + progress + "\n errFileName:" + errFileName);
		} 
		
		System.out.println("Load root Done and build refItClassName Map...");

		if(globeClassSet.size()>0 && !monitor.isCanceled()){
			
			monitor = new ProgressMonitor(parent, "Buld smali relationship", "Getting Started...", 0, globeClassSet.size());
			progress = 0;
			
			Iterator<SmaliEntry> iterator = globeClassSet.iterator();
			iterator = globeClassSet.iterator();
			
			while(iterator.hasNext()){
				SmaliEntry se = iterator.next();
				
				progress++;
            	monitor.setProgress(progress);
            	monitor.setNote("buld relation(1) num:" + progress + "\n SmaliEntry:" + se.toString());
            	if(monitor.isCanceled()){
            		break;
            	}
            	
            	if(se==null || se.classHeader==null){
            		continue;
            	}
    			
            	Set<SmaliEntry> tmpClassSet = new HashSet<SmaliEntry>();
    			tmpClassSet.addAll(globeClassSet);
    			
    			Iterator<SmaliEntry> iterator2 = tmpClassSet.iterator();
    			while(iterator2.hasNext()){
    				SmaliEntry se2 = iterator2.next();
    				if(se2!=null && se!=se2){
    					if(se2.classHeader==null || se2.content==null){
    						continue;
    					}
    					
    					if(se2.content.contains(se.classHeader.classNameSelf)){
    						//se在 se2的文件中，说明 se被se2使用， se2使用了se
    						se.putRefItClass(se2);
    						se2.putItRefClass(se);
    					}
    					

    					//构建父类关系
    					if(se2.classHeader.classNameSuper!=null && se2.classHeader.classNameSuper.equals(se.classHeader.classNameSelf)){
    						//se2's father == se
    						se2.setFatherClass(se);
    					}
    				}
    			}
    			

			}
			

			
			monitor.close();
		}
		

		
		System.out.println("All Done!");
	}
	
	private void loadChildren(File parent){
		if(parent==null){
			return;
		}
		
		if(parent.isDirectory()){
			File[] filelist = parent.listFiles();
			if(filelist!=null && filelist.length>0){
				for (File f : filelist) {
					if(f.isFile() && f.getName().endsWith(".smali")){
						//EntryVector 里装的必定是文件或函数
						fileList.add(f);

					}else{
						//递归遍历目录
						loadChildren(f);
					}
					
				}
			}
		}
	}

	public void insertPackage(String packageName) {
		// TODO Auto-generated method stub
		SmaliEntry packageEntry = findEntry(packageName);
		
		if(packageEntry==null){//not found, insert
    		packageEntry = new SmaliEntry(null, packageName, false);
    		root.leafChildren.add(packageEntry);
    		
    		packageSet.put(packageName, packageEntry);

    	}
	}

	public void removePackage(String packageName) {
		// TODO Auto-generated method stub
		SmaliEntry packageEntry = findEntry(packageName);
		
		if(packageEntry!=null && packageEntry.leafChildren.size()==0){ //empty package can remove
			root.leafChildren.remove(packageEntry);
			
			packageSet.remove(packageName);
		}

	}

	public void changePackage(SmaliEntry son, String parentOfOld, String parentOfNew) {
		// TODO Auto-generated method stub
		insertPackage(parentOfNew);
		
		SmaliEntry newParent = findEntry(parentOfNew);
		SmaliEntry oldParent = findEntry(parentOfOld);
		
		newParent.leafChildren.add(son);
		oldParent.leafChildren.remove(son);
		
		
		removePackage(parentOfOld);
	}
	
	
	
	public SmaliEntry findEntry(String packageName){

		return packageSet.get(packageName);
	}


	


}
