package com.zhangwei.ui.jtree;

import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.ProgressMonitor;

import com.zhangwei.smali.api.FieldEntry;
import com.zhangwei.smali.api.MethodEntry;
import com.zhangwei.smali.api.MyParser;
import com.zhangwei.smali.api.SmaliEntry;
import com.zhangwei.utils.StringHelper;

public class SmaliLoader {
	private static SmaliLoader ins=null;
	public static String rootpath = null;
	
	private ArrayList<File> list;
	private Set<SmaliEntry> smailMap; //path --> smaliEntry , 只用到了value，key没有使用到
	private Map<String, SmaliEntry> old_classMap; //className --> smaliEntry
	private Map<String, SmaliEntry> new_classMap; //className --> smaliEntry
	
	private MyParser myParser;
	
	private ProgressMonitor monitor;
	private int progress;
	private SmaliEntry root;
	
	private SmaliLoader(){
		list = new ArrayList<File>();
		smailMap = new HashSet<SmaliEntry>();
		old_classMap = new HashMap<String, SmaliEntry>();
		new_classMap = new HashMap<String, SmaliEntry>();
		myParser = new MyParser();
	}
	
	public static SmaliLoader getInstance(){
		if(ins==null){
			ins = new SmaliLoader();
		}
		
		return ins;
	}
	
	public void renameClass(SmaliEntry se, String src_className, String dst_className){
		boolean ret = se.renameClassFile(src_className, dst_className);
		
		if(ret){
//			for(Entry<String, SmaliEntry>  item: smailMap.entrySet()){
//				SmaliEntry se_item = item.getValue();
//				se_item.renameClassContent(src_className, dst_className);
//			}
			
			Iterator<SmaliEntry> iterator = smailMap.iterator();
			while(iterator.hasNext()){
				SmaliEntry se_item = iterator.next();
				se_item.renameClassContent(src_className, dst_className);
			}
			
			se.renameClass(src_className, dst_className);
//			classMap.remove(src_className); // 旧key不删
			new_classMap.put(dst_className, se);
			
		}

	}
	
	public void renameField(SmaliEntry se, FieldEntry fe, String s) {
		// TODO Auto-generated method stub
		String className = se.classHeader.classNameSelf; // Lcom/b/a/a;
		String oldFieldName = fe.classFieldName;
		String newFieldName = s;
		
		Iterator<SmaliEntry> iterator = smailMap.iterator();
		while(iterator.hasNext()){
			SmaliEntry se_item = iterator.next();
			se_item.renameFieldContent(className, oldFieldName, newFieldName, fe.classFieldType);
		}
		
//		for(Entry<String, SmaliEntry>  item: smailMap.entrySet()){
//			SmaliEntry se_item = item.getValue();
//			se_item.renameFieldContent(className, oldFieldName, newFieldName, fe.classFieldType);
//		}
		
		fe.RenameName(className, oldFieldName, newFieldName, fe.classFieldType);
	}
	
	public void renameMethod(SmaliEntry se, MethodEntry me, String s) {
		// TODO Auto-generated method stub
		String className = se.classHeader.classNameSelf; // Lcom/b/a/a;
		String oldMethodName = me.classMethodName;
		String newMethodName = me.classConstructorName!=null?null:s;
		

		
		if(newMethodName!=null){
			Iterator<SmaliEntry> iterator = smailMap.iterator();
			while(iterator.hasNext()){
				SmaliEntry se_item = iterator.next();
				se_item.renameMethodContent(className, oldMethodName, newMethodName, me.classMethodProto);
			}
//			for(Entry<String, SmaliEntry>  item: smailMap.entrySet()){
//				SmaliEntry se_item = item.getValue();
//				se_item.renameMethodContent(className, oldMethodName, newMethodName, me.classMethodProto);
//			}
			
			me.RenameName(className, oldMethodName, newMethodName, me.classMethodProto);
		}

	}
	
	private void upPublic(SmaliEntry se) {
		// TODO Auto-generated method stub
		se.upPublic();
		
	}
	
	/**
	 * @return 返回SmaliEntry ,  已应用新的命名 Lx/y/z;
	 * */
	private void autoRename(SmaliEntry it){

    	if(monitor.isCanceled()){
    		return ;
    	}
    	

//    	String dst_className = null;
//    	String parent_className = null;
    	if(StringHelper.needRename(it.name)){//only rename the a,b,c,d ...or ab
        	if(it.classHeader==null || it.classHeader.classNameSelf==null){
        		System.err.println("class parse null:" + it.name + ", classHeader:" + it.classHeader);
        		return ;
        	}
        	
        	String superShortName = StringHelper.getShortNameOfSmali(it.classHeader.classNameSuper);
        	
        	//0. 父类需要rename，先处理父类，然后同步到本类的结构
        	if(StringHelper.needRename(superShortName)){
        		SmaliEntry parent = old_classMap.get(it.classHeader.classNameSuper);
        		
        		//如果父类找不到，则不处理
        		if(parent!=null){
        			autoRename(parent);
        			it.classHeader.classSuper = it.classHeader.classSuper.replace(it.classHeader.classNameSuper, parent.classHeader.classNameSelf);
        			it.classHeader.classNameSuper = parent.classHeader.classNameSelf;
        		}

        	}
        	
        	String old_base_className = it.classHeader.classNameSelf; //  La/b/c;
        	String old_tmp_name = old_base_className.substring(0, old_base_className.length()-1); //去掉; La/b/c
        	String new_tmp_name = old_tmp_name + "_" + StringHelper.getShortNameOfSmali(it.classHeader.classNameSuper); //La/b/c_Object
			String new_dst_className = new_tmp_name + ";"; //La/b/c_Object;
			
        	String old_base_className_prefix = old_tmp_name + "$";  //La/b/c$
        	String new_base_className_prefix = new_tmp_name + "$";  //La/b/c_Object$
        	
			//1. rename本身
			renameClass(it, it.classHeader.classNameSelf, new_dst_className);
			
			//2. rename本类内部实现的匿名类
			Iterator<Entry<String, SmaliEntry>> iterator = old_classMap.entrySet().iterator();
			while(iterator.hasNext()){
				Entry<String, SmaliEntry> entry = iterator.next();
				String key = entry.getKey();
				SmaliEntry value = entry.getValue();
				
				if(key.startsWith(old_base_className_prefix)){
					try{
						String newKey = key.replaceAll(StringHelper.escapeExprSpecialWord(old_base_className_prefix), new_base_className_prefix);
						renameClass(value, key, newKey);
					}catch (Exception e){
					  e.printStackTrace();
					  System.out.println("value:" + value.name + "key:"+key + ", old_base_className_prefix:" + old_base_className_prefix + ", new_base_className_prefix:" + new_base_className_prefix);
					}

					
				}
			}
			
	    	progress++;
	    	monitor.setProgress(progress);
	    	monitor.setNote("Renamed num:" + progress + "\n file:" + it.file.getAbsolutePath());
    	}else{
	    	progress++;
	    	monitor.setProgress(progress);
	    	monitor.setNote("No Need to rename \n file:" + it.file.getAbsolutePath());
    	}
    	
    	//upPublic(it);

//    	return it;
	}
	
	public void autoRename(Component parent){
		if(root!=null && root.children!=null && root.children.size()>0){
			if(smailMap!=null && smailMap.size()>0){
				monitor = new ProgressMonitor(parent, "Renaming Progress", "Getting Started...", 0, smailMap.size());
				progress = 0;
				
				Iterator<SmaliEntry> iterator = smailMap.iterator();
				while(iterator.hasNext()){
					SmaliEntry se_item = iterator.next();
					autoRename(se_item);
				}
				
			}
		}
		
		old_classMap = new_classMap;
		new_classMap = new HashMap<String, SmaliEntry>();
		
		System.out.println("autoRename done");
	}
	
	public void autoPublic(Component parent){
		if(root!=null && root.children!=null && root.children.size()>0){
			if(smailMap!=null && smailMap.size()>0){
				monitor = new ProgressMonitor(parent, "Renaming Progress", "Getting Started...", 0, smailMap.size());
				progress = 0;
				
				Iterator<SmaliEntry> iterator = smailMap.iterator();
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
	

	public void loadRoot(Component parent, SmaliEntry root){
		this.root = root;
		String root_path = root.file.getAbsolutePath();
		
		if(root_path!=null ){
			if(!root_path.endsWith("\\")){
				root_path = root_path + '\\';
			}
		}else{
			return ;
		}
		
		SmaliEntry.rootPath = root_path;
		
		if(list!=null){
			list.clear();
		}

		if(smailMap!=null){
			smailMap.clear();
		}
		
		//
		loadChildren(root.file);
		
		try{
			if(list.size()>0){
				monitor = new ProgressMonitor(parent, "Loading Progress", "Getting Started...", 0, list.size());

				progress = 0;
				
	            for (File file : list) {
	            	progress++;
	            	monitor.setProgress(progress);
	            	monitor.setNote("Loaded num:" + progress + "\n file:" + file.getAbsolutePath());
	            	if(monitor.isCanceled()){
	            		break;
	            	}
	            	
                	
                	SmaliEntry packageEntry = null;
                	SmaliEntry smaliEntry = new SmaliEntry(file, true, file.getName());
                	boolean Parse_success = myParser.paser(smaliEntry);
                	if(Parse_success){
                		packageEntry = findEntry(root, file.getParentFile()); //查找该节点的父节点，即包
                		
                		//add non-leaf package if not exist
    	            	if(packageEntry==null){
    	            		String packageName = getPackageName(file.getParentFile());
    	            		packageEntry = new SmaliEntry(file.getParentFile(), false, packageName);
    	            		root.children.add(packageEntry);
    	            	}
    	            	
    	            	//add leaf
    	            	packageEntry.children.add(smaliEntry);
    	            	smailMap.add(smaliEntry);
    	            	old_classMap.put(smaliEntry.classHeader.classNameSelf, smaliEntry);
                	}else{
                		break; //abort if has error in parsing
                	}

               
	            }

			}
		}catch(Exception e){
			e.printStackTrace();
		}
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
						list.add(f);

					}else{
						//递归遍历目录
						loadChildren(f);
					}
					
				}
			}
		}
	}

	public void insertPackage(File packageFile) {
		// TODO Auto-generated method stub
		SmaliEntry packageEntry = findEntry(root, packageFile);
		
		if(packageEntry==null){//not found, insert
			String packageName = getPackageName(packageFile);
    		packageEntry = new SmaliEntry(packageFile, false, packageName);
    		root.children.add(packageEntry);
    		//smailMap.put(packageEntry.file.getAbsolutePath(), packageEntry);
    	}
	}

	public void removePackage(File packageFile) {
		// TODO Auto-generated method stub
		SmaliEntry packageEntry = findEntry(root, packageFile);
		
		if(packageEntry!=null && packageEntry.children.size()==0){ //empty package can remove
			root.children.remove(packageEntry);
		}

	}

	public void changePackage(SmaliEntry son, File parentOfOld, File parentOfNew) {
		// TODO Auto-generated method stub
		insertPackage(parentOfNew);
		
		SmaliEntry newParent = findEntry(root, parentOfNew);
		SmaliEntry oldParent = findEntry(root, parentOfOld);
		
		newParent.children.add(son);
		oldParent.children.remove(son);
		
		
		removePackage(parentOfOld);
	}
	
	public SmaliEntry findEntry(SmaliEntry root, File file){
    	SmaliEntry ret = null;
		for(SmaliEntry v : root.children){
    		if(v.file.getAbsolutePath()!=null && v.file.getAbsolutePath().equals(file.getAbsolutePath())){
    			ret  = v;
    			break;
    		}
    	}
		
		return ret; 
	}
	
	public static String getPackageName(File packageFile){
		String pathOfParent = packageFile.getAbsolutePath();
		if(!pathOfParent.endsWith("\\")){
			pathOfParent = pathOfParent + '\\';
		}
		
		String packageName = pathOfParent.replace(SmaliEntry.rootPath, "").replace("\\", ".");
		
		if(packageName.equals("")){
			packageName = ".";
		}else if(packageName.endsWith(".")){
			packageName = packageName.substring(0, packageName.length()-1);
		}
		
		return packageName;
	}





}
