package com.zhangwei.ui.jtree;

import java.awt.Component;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ProgressMonitor;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.zhangwei.smali.api.FieldEntry;
import com.zhangwei.smali.api.MethodEntry;
import com.zhangwei.smali.api.MyParser;
import com.zhangwei.smali.api.SmaliEntry;
import com.zhangwei.smali.json.Command;
import com.zhangwei.smali.json.SaveState;
import com.zhangwei.smali.json.ShrinkCommand;
import com.zhangwei.utils.StringHelper;

public class SmaliLoader {
	private static SmaliLoader ins=null;
	
	private String confPath;

	private Set<SmaliEntry> globeClassSet = new HashSet<SmaliEntry>(); //path --> smaliEntry , 只用到了value，key没有使用到

	
	private MyParser myParser;
	
	private ProgressMonitor monitor;
	private int progress;
	private SmaliEntry root;
//	private List<File> fileList;
	private Map<String, SmaliEntry> packageSet = new HashMap<String, SmaliEntry>();
	

	public static String clzRegexPatten = "L[0-9|a-z|A-Z|$|/|_|-]+;";
	
	private SmaliLoader(){
//		fileList = new ArrayList<File>();
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
					se_item.renameClassContent(src_className, dst_className, false);
					if(needSyncDisk){
						se_item.setFileContent();
					}
					
				}
			}
			
			
			se.renameClass(src_className, dst_className);
			se.renameClassContent(src_className, dst_className, true);
			
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
			se_item.setFileContent();
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
				se_item.setFileContent();
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
	 * (1)
	 * com.tencent.mm.protocal.Hclz$a;
	 * # annotations
	 * .annotation system Ldalvik/annotation/EnclosingClass;
	 *     value = Lcom/tencent/mm/protocal/Hclz;
	 * .end annotation
	 * 
	 * .annotation system Ldalvik/annotation/InnerClass;
	 *     accessFlags = 0x609
	 *     name = "a"
	 * .end annotation
	 * 
	 * (2)
	 * .class public Lcom/tencent/smtt/export/external/interfaces/IX5WebViewBase$HitTestResult$AnchorData;
	 * .super Ljava/lang/Object;
	 * .source "SourceFile"
	 * 
	 * # annotations
	 * .annotation system Ldalvik/annotation/EnclosingClass;
	 *      value = Lcom/tencent/smtt/export/external/interfaces/IX5WebViewBase$HitTestResult;
	 * .end annotation
	 *       
	 * .annotation system Ldalvik/annotation/InnerClass;
	 *      accessFlags = 0x1
	 *      name = "AnchorData"
	 * .end annotation
	 * 
	 * (3)
	 * .class final Lcom/tencent/mm/ui/transmit/MsgRetransmitUI$8;
	 * .super Ljava/lang/Object;
	 * .source "SourceFile"
	 * 
	 * # interfaces
	 * .implements Landroid/content/DialogInterface$OnCancelListener;
	 * 
	 * # annotations
	 * .annotation system Ldalvik/annotation/EnclosingMethod;
	 *     value = Lcom/tencent/mm/ui/transmit/MsgRetransmitUI;->bcf()V
	 * .end annotation
	 * 
	 * .annotation system Ldalvik/annotation/InnerClass;
	 *    accessFlags = 0x0
	 *    name = null
	 * .end annotation
	 * 
	 * (4)
	 * .class final Lcom/tencent/mm/ui/transmit/MsgRetransmitUI$8$1;
	 * .super Ljava/lang/Object;
	 * .source "SourceFile"
	 * 
	 * # interfaces
	 * .implements Landroid/content/DialogInterface$OnClickListener;
	 * 
	 * 
	 * # annotations
	 * .annotation system Ldalvik/annotation/EnclosingMethod;
	 *     value = Lcom/tencent/mm/ui/transmit/MsgRetransmitUI$8;->onCancel(Landroid/content/DialogInterface;)V
	 * .end annotation
	 * 
	 * .annotation system Ldalvik/annotation/InnerClass;
	 *     accessFlags = 0x0
	 *     name = null
	 * .end annotation
	 * 
	 * 
	 * @return 返回SmaliEntry ,  已应用新的命名 Lx/y/z;
	 * @throws IOException 
	 * */
	private void autoRenameStandAndInnerClass(SmaliEntry it) throws IOException{

    	if(monitor.isCanceled()){
    		return ;
    	}
    	

    	if(StringHelper.needRename(it)){//only rename the a,b,c,d ...or ab
        	if(it.classHeader==null || it.classHeader.classNameSelf==null){
        		System.err.println("class parse null:" + it.toString() + ", classHeader:" + it.classHeader);
        		return ;
        	}
        	

			String new_dst_className = StringHelper.getNewClassNameSelf(it.classHeader.classNameSelf);
//			it.classHeader.classNameSelf.startsWith("Lcom/tencent/mm/sdk/platformtools/Vclz");
			
			//2. rename本身
			renameClass(it, it.classHeader.classNameSelf, new_dst_className, true);
			
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
					

					
//					if(!se_item.classHeader.classNameSelf.contains("$")){
//				    	progress++;
//				    	monitor.setProgress(progress);
//				    	monitor.setNote("Renamed num:" + progress + "\n ingore:" + se_item.classHeader.classNameSelf);
//						continue;
//					}
					
					if(se_item.classHeader.classNameSelf.contains("google")){
				    	progress++;
				    	monitor.setProgress(progress);
				    	monitor.setNote("Renamed num:" + progress + "\n ingore:" + se_item.classHeader.classNameSelf);
						continue;
					}
					
					if(se_item.classHeader.classNameSelf.contains("support")){
				    	progress++;
				    	monitor.setProgress(progress);
				    	monitor.setNote("Renamed num:" + progress + "\n ingore:" + se_item.classHeader.classNameSelf);
						continue;
					}
					
					if(se_item.classHeader.classNameSelf.contains("ui")){
				    	progress++;
				    	monitor.setProgress(progress);
				    	monitor.setNote("Renamed num:" + progress + "\n ingore:" + se_item.classHeader.classNameSelf);
						continue;
					}
					
//					if(se_item.classHeader.classNameSelf.contains("plugin")){
//				    	progress++;
//				    	monitor.setProgress(progress);
//				    	monitor.setNote("Renamed num:" + progress + "\n ingore:" + se_item.classHeader.classNameSelf);
//						continue;
//					}
					
//					if(se_item.classHeader.classNameSelf.startsWith("Lcom/tencent/mm/protocal")){
					
//					if(se_item.classHeader.classNameSelf.startsWith("Lcom/tencent/mm/q/") ||  
//							se_item.classHeader.classNameSelf.startsWith("Lcom/tencent/mm/remoteservice/") || 
//							se_item.classHeader.classNameSelf.startsWith("Lcom/tencent/mm/s/") ||
//							se_item.classHeader.classNameSelf.startsWith("Lcom/tencent/mm/r/") ){
					
//					if(se_item.classHeader.classNameSelf.startsWith("Lcom/tencent/b/a/a/") ||  
//							se_item.classHeader.classNameSelf.startsWith("Lcom/tencent/a/a/a") || 
//							se_item.classHeader.classNameSelf.startsWith("Lcom/tencent/c") ||
//							se_item.classHeader.classNameSelf.startsWith("Lcom/tencent/mm/booter/") ){
						
//					if(se_item.classHeader.classNameSelf.startsWith("Lcom/tencent/mm/network/") ||  
//							se_item.classHeader.classNameSelf.startsWith("Lcom/tencent/mm/model/") || 
//							se_item.classHeader.classNameSelf.startsWith("Lcom/tencent/mm/storage/") ||
//							se_item.classHeader.classNameSelf.startsWith("Lcom/tencent/pb/") ){		
					
					
					if(se_item.classHeader.classNameSelf.startsWith("Lcom/tencent/mm/")){
					
//					if(se_item.classHeader.classNameSelf.startsWith("Lcom/tencent/mm/pluginsdk")){		
						
						try {
							if(se_item.classHeader.classNameSelf.contains("$")){
								autoRenameStandAndInnerClass(se_item);
							}else{
//								autoRenameStandClass(se_item);
								autoRenameStandAndInnerClass(se_item);
							}
							
							
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}else{
				    	progress++;
				    	monitor.setProgress(progress);
				    	monitor.setNote("Renamed num:" + progress + "\n ingore:" + se_item.classHeader.classNameSelf);
					}
					

					
					//sync map after every rename
//					refacoterGlobeClassMap(parent);
				}
				
				monitor.close();
				
			}
		}
		

		System.out.println("autoRename done");
	}
	
	public void autoRename(Component parent, String path) {
		
		System.out.println("autoRename path:" + path);
		
		if(path!=null && root!=null && root.leafChildren!=null && root.leafChildren.size()>0){
			if(globeClassSet!=null && globeClassSet.size()>0){
				monitor = new ProgressMonitor(parent, "Renaming Progress", "path prefix - " + path, 0, globeClassSet.size());
				progress = 0;
				
				Iterator<SmaliEntry> iterator = globeClassSet.iterator();
				while(iterator.hasNext()){
					SmaliEntry se_item = iterator.next();
					

					if(se_item.classHeader.classNameSelf.startsWith(path)){
	
						
						try {
							if(se_item.classHeader.classNameSelf.contains("$")){
								autoRenameStandAndInnerClass(se_item);
							}else{
//								autoRenameStandClass(se_item);
								autoRenameStandAndInnerClass(se_item);
							}
							
							
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}else{
				    	progress++;
				    	monitor.setProgress(progress);
					}
					
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
		this.confPath = state.confPath;

		monitor = new ProgressMonitor(parent, "Loading Progress", "Getting Started...", 0, globeClassSet.size());

		if (globeClassSet.size() > 0 && !monitor.isCanceled()) {
			
			//init from saveConf
			Iterator<SmaliEntry> iterator0 = globeClassSet.iterator();
			iterator0 = globeClassSet.iterator();
			Map<String, SmaliEntry> map = new HashMap<String, SmaliEntry>();

			boolean loadGsonSuc = true;
			while (iterator0.hasNext()) {
				SmaliEntry se = iterator0.next();
				if(se!=null && se.classHeader!=null && se.classHeader.classNameSelf!=null){
					map.put(se.classHeader.classNameSelf, se);
					
					try {
						se.postConstructFromGson();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						loadGsonSuc = false;
						break;
					}
				}

			}
			
			if(!loadGsonSuc){
				return;
			}
			
			//build relation names
			Iterator<SmaliEntry> iterator1 = globeClassSet.iterator();
			iterator1 = globeClassSet.iterator();

			while (iterator1.hasNext()) {
				Pattern p = Pattern.compile(clzRegexPatten);
				SmaliEntry se = iterator1.next();
				if(se!=null && se.classHeader!=null && se.classHeader.classNameSelf!=null){

					Matcher match = p.matcher(se.content);
					while(match.find()){

						int startIndex = match.start();
						int endIndex = match.end();
						String matchStr = se.content.substring(startIndex, endIndex);
						if(matchStr!=null && map.containsKey(matchStr)){
							if(!se.itRefClassNames.contains(matchStr)){
								se.itRefClassNames.add(matchStr);
							}
							
						}

					
					}
				}

			}
			


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
				

				
        		SmaliEntry packageEntry = findEntry(se.packageName); //查找该节点的父节点，即包
        		
        		//add non-leaf package if not exist
            	if(packageEntry==null){
            		packageEntry = new SmaliEntry(null, se.packageName, false);
            		root.leafChildren.add(packageEntry);
            		packageSet.put(se.packageName, packageEntry);
            	}
            	
            	//add leaf
            	packageEntry.leafChildren.add(se);
            	
            	//build relation2
            	if(se.itRefClassNames!=null && se.itRefClassNames.size()>0){
                	for(String clzName : se.itRefClassNames){
                		SmaliEntry one = map.get(clzName);
                		if(one!=null){
                    		se.itRefClass.add(one);
                    		one.refItClass.add(se);
                		}

                	}
            	}

            	
            	if(se.fatherClassName!=null){
            		SmaliEntry fe = map.get(se.fatherClassName);
            		se.setFatherClass(fe);
            	}
        

			}
			
//			sortTree();

			monitor.close();
			
			printPackageChildren();
		}
	}
	
	private void printPackageChildren(){
		if(packageSet!=null && packageSet.size()>0){
			for(SmaliEntry item : packageSet.values()){
				if(item!=null && item.leafChildren!=null && item.leafChildren.size()>0){
					System.out.println(item.toString() + " - has " + item.leafChildren.size() + " children");
				}
			}
		}
	}
	
	public void sortTree(){
		for(SmaliEntry p : root.leafChildren){
			
			Collections.sort(p.leafChildren, new Comparator<SmaliEntry>() {


				@Override
				public int compare(SmaliEntry o1, SmaliEntry o2) {
					// TODO Auto-generated method stub
					
					return o1.toString().compareTo(o2.toString());
				}
			});
		}
		
		Collections.sort(root.leafChildren, new Comparator<SmaliEntry>() {


			@Override
			public int compare(SmaliEntry o1, SmaliEntry o2) {
				// TODO Auto-generated method stub
				
				return o1.toString().compareTo(o2.toString());
			}
		});
		

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
			System.out.println("saveState - confPath:" + state.confPath);
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
		
		
		Map<String, SmaliEntry> map = new HashMap<String, SmaliEntry>();
		
		List<File> fileList = new ArrayList<File>();

		for(File dir : dirs){
			loadChildren(dir, fileList);
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
    	            	if(smaliEntry.classHeader!=null && smaliEntry.classHeader.classNameSelf!=null){
        	            	map.put(smaliEntry.classHeader.classNameSelf, smaliEntry);
    	            	}

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
			
			Pattern p = Pattern.compile(clzRegexPatten);

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
            	
            	Matcher match = p.matcher(se.content);
				while(match.find()){
					int startIndex = match.start();
					int endIndex = match.end();
					String matchStr = se.content.substring(startIndex, endIndex);
					if(matchStr!=null && map.containsKey(matchStr)){
						if(!se.itRefClassNames.contains(matchStr)){
							se.itRefClassNames.add(matchStr);
						}
						
						SmaliEntry se2 = map.get(matchStr);
						if(se2!=null){
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
	
	public void execShrink(Component parent, File commandFile){
		if(commandFile==null || !commandFile.isFile()){
			return;
		}
		

		
		Map<String, SmaliEntry> map = new HashMap<String, SmaliEntry>();
		Iterator<SmaliEntry> iterator = globeClassSet.iterator();
		while (iterator.hasNext()) {
			SmaliEntry se = iterator.next();
	    	map.put(se.classHeader.classNameSelf, se);
		}
		
		HashSet<SmaliEntry> beginSet = new HashSet<SmaliEntry>();
		
		
		String commandContent = null;
		try {
			commandContent = FileUtils.readFileToString(commandFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(commandContent==null){
			return;
		}
		
		ShrinkCommand comm = new Gson().fromJson(commandContent, ShrinkCommand.class);
		
		HashSet<SmaliEntry> endClzNameSet = new HashSet<SmaliEntry>();
		
		for(String javaClzNameItem : comm.beginClzName){
			String clzNameSelf = StringHelper.getClzNameFromJavaClzName(javaClzNameItem);
			SmaliEntry item = map.get(clzNameSelf);
			if(item!=null){
				beginSet.add(item);
			}

		}
		
		for(String javaClzNameItem : comm.endClzName){
			String clzNameSelf = StringHelper.getClzNameFromJavaClzName(javaClzNameItem);
			SmaliEntry item = map.get(clzNameSelf);
			if(item!=null){
				endClzNameSet.add(item);
			}

		}
		
		System.out.println("beginSet.size:" + beginSet.size() + ", endClzNameSet.size:" + endClzNameSet.size());
		
		HashSet<SmaliEntry> refSet = new HashSet<SmaliEntry>();
		refSet.addAll(beginSet);
		
		while(true){
			int refClzNumber1 = refSet.size();

			HashSet<SmaliEntry> refSet2 = new HashSet<SmaliEntry>();
			
			for(SmaliEntry item : refSet){
				for(SmaliEntry refItem :item.itRefClass){
					if(!endClzNameSet.contains(refItem)){
						refSet2.add(refItem);
					}
				}
			}
			
			refSet.addAll(refSet2);
			
			int refClzNumber2 = refSet.size();
			
			if(refClzNumber1==refClzNumber2){
				break;
			}
		}
		
		refSet.addAll(endClzNameSet);
		
		System.out.println("All size :" + globeClassSet.size() + ", After Shrink size:" + refSet.size());
		
		for(SmaliEntry refItem : refSet){
			System.out.println(refItem.classHeader.classNameSelf);
		}
		
		System.out.println("All size :" + globeClassSet.size() + ", After Shrink size:" + refSet.size());
		
	}
	
	public void execCommand(Component parent, File commandFile){

		if(commandFile==null || !commandFile.isFile()){
			return;
		}
		

		
		Map<String, SmaliEntry> map = new HashMap<String, SmaliEntry>();
		Iterator<SmaliEntry> iterator = globeClassSet.iterator();
		while (iterator.hasNext()) {
			SmaliEntry se = iterator.next();
	    	map.put(se.classHeader.classNameSelf, se);
		}
		
		String commandContent = null;
		try {
			commandContent = FileUtils.readFileToString(commandFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(commandContent==null){
			return;
		}
		
		Command comm = new Gson().fromJson(commandContent, Command.class);
		
		if(comm.Type.equals(Command.ItRefClzType)){
			String clzName = comm.srcClzName;
			SmaliEntry se = map.get(clzName);
			if(se!=null && se.itRefClass!=null && se.itRefClass.size()>0){
				System.out.println("-------------------");
				System.out.println(clzName + "' itRefClass :");
				for(SmaliEntry s: se.itRefClass){
					System.out.println(s.classHeader.classNameSelf);
				}
				System.out.println("-------------------");
			}
		}else if(comm.Type.equals(Command.RefItClzType)){
			String clzName = comm.srcClzName;
			SmaliEntry se = map.get(clzName);
			if(se!=null && se.refItClass!=null && se.refItClass.size()>0){
				System.out.println("-------------------");
				System.out.println(clzName + "' refItClass :");
				for(SmaliEntry s: se.refItClass){
					System.out.println(s.classHeader.classNameSelf);
				}
				System.out.println("-------------------");
			}
			
		}else if(comm.Type.equals(Command.RenameType)){
			if(globeClassSet.size()>0 && !monitor.isCanceled()){
				
				monitor = new ProgressMonitor(parent, "Buld smali relationship", "Getting Started...", 0, globeClassSet.size());
				progress = 0;
				
				iterator = globeClassSet.iterator();
				while(iterator.hasNext()){
					SmaliEntry se = iterator.next();
					
					progress++;
	            	monitor.setProgress(progress);
	            	
	            	
	            	monitor.setNote("Rename content only, num:" + progress + "\n SmaliEntry:" + se.toString());
	            	if(monitor.isCanceled()){
	            		break;
	            	}
	            	
	            	if(se==null || se.classHeader==null){
	            		continue;
	            	}
	            	
	            	if(se.classHeader.classNameSelf.startsWith(comm.startClzPattern)){
	            		if(comm.exceptClzPattern!=null && comm.exceptClzPattern.length>0){
	            			for(String exceptP : comm.exceptClzPattern){
	            				if(se.classHeader.classNameSelf.startsWith(exceptP)){
	            					continue;
	            				}
	            			}
	            		}
	            		se.renameClassContent(comm.srcClzName, comm.dstClzName, true);
	            	}
	            	
				}
				
				monitor.close();
			}
		}
		
		System.out.println("All Done!");
	}
	
	public void addRoot(Component parent, SmaliEntry root, File[] dirs){
		
		if(dirs.length<=0){
			return;
		}
		
		
		Map<String, SmaliEntry> mapAll = new HashMap<String, SmaliEntry>();
		
		List<File> fileList = new ArrayList<File>();

		for(File dir : dirs){
			loadChildren(dir, fileList);
		}

		
		String errFileName = null;
		
		try{

			
			if(fileList.size()>0){
				monitor = new ProgressMonitor(parent, "Adding Progress", "Getting Started...", 0, fileList.size());

				progress = 0;
				
				Map<String, SmaliEntry> mapOld = new HashMap<String, SmaliEntry>();
				if(globeClassSet.size()>0){
					for(SmaliEntry se : globeClassSet){
						mapOld.put(se.classHeader.classNameSelf, se);
					}
				}
				
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
                		if(mapOld.containsKey(smaliEntry.classHeader.classNameSelf)){
                			continue;
                		}
                		
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
    	            	if(smaliEntry.classHeader!=null && smaliEntry.classHeader.classNameSelf!=null){
        	            	mapAll.put(smaliEntry.classHeader.classNameSelf, smaliEntry);
    	            	}

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
		
		System.out.println("Add Done and build refItClassName Map...");

		if(globeClassSet.size()>0 && !monitor.isCanceled()){
			
			monitor = new ProgressMonitor(parent, "Buld smali relationship", "Getting Started...", 0, globeClassSet.size());
			progress = 0;
			
			Iterator<SmaliEntry> iterator = globeClassSet.iterator();
			iterator = globeClassSet.iterator();
			
			Pattern p = Pattern.compile(clzRegexPatten);

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
            	
            	Matcher match = p.matcher(se.content);
				while(match.find()){
					int startIndex = match.start();
					int endIndex = match.end();
					String matchStr = se.content.substring(startIndex, endIndex);
					if(matchStr!=null && mapAll.containsKey(matchStr)){
						if(!se.itRefClassNames.contains(matchStr)){
							se.itRefClassNames.add(matchStr);
						}
						
						SmaliEntry se2 = mapAll.get(matchStr);
						if(se2!=null){
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
	
	/**
	 * 可以overwrite，并且将不同root目录的smali，
	 * */
	public void add2Root(Component parent, SmaliEntry root, File[] dirs){
	
		if(dirs.length<=0){
			return;
		}
		
		
		Map<String, SmaliEntry> mapAll = new HashMap<String, SmaliEntry>();
		
		List<File> fileList = new ArrayList<File>();

		for(File dir : dirs){
			loadChildren(dir, fileList);
		}

		
		String errFileName = null;
		
		try{

			
			if(fileList.size()>0){
				monitor = new ProgressMonitor(parent, "Adding Progress", "Getting Started...", 0, fileList.size());

				progress = 0;
				
				Map<String, SmaliEntry> mapOld = new HashMap<String, SmaliEntry>();
				if(globeClassSet.size()>0){
					for(SmaliEntry se : globeClassSet){
						mapOld.put(se.classHeader.classNameSelf, se);
					}
				}
				
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
                		if(mapOld.containsKey(smaliEntry.classHeader.classNameSelf)){
                			continue;
                		}
                		
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
    	            	if(smaliEntry.classHeader!=null && smaliEntry.classHeader.classNameSelf!=null){
        	            	mapAll.put(smaliEntry.classHeader.classNameSelf, smaliEntry);
    	            	}

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
		
		System.out.println("Add Done and build refItClassName Map...");

		if(globeClassSet.size()>0 && !monitor.isCanceled()){
			
			monitor = new ProgressMonitor(parent, "Buld smali relationship", "Getting Started...", 0, globeClassSet.size());
			progress = 0;
			
			Iterator<SmaliEntry> iterator = globeClassSet.iterator();
			iterator = globeClassSet.iterator();
			
			Pattern p = Pattern.compile(clzRegexPatten);

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
            	
            	Matcher match = p.matcher(se.content);
				while(match.find()){
					int startIndex = match.start();
					int endIndex = match.end();
					String matchStr = se.content.substring(startIndex, endIndex);
					if(matchStr!=null && mapAll.containsKey(matchStr)){
						if(!se.itRefClassNames.contains(matchStr)){
							se.itRefClassNames.add(matchStr);
						}
						
						SmaliEntry se2 = mapAll.get(matchStr);
						if(se2!=null){
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

	public void delRoot(Component parent, SmaliEntry root, File[] dirs){

		if(dirs.length<=0){
			return;
		}
		
		if(globeClassSet.size()<=0){
			System.out.println("delRoot - globeClassSet.size()<0");
			return;
		}
		
		Map<String, SmaliEntry> map = new HashMap<String, SmaliEntry>();
		
		
		System.out.println("CleanUp globeClassSet ...");
		
		Iterator<SmaliEntry> iterator = globeClassSet.iterator();
		while (iterator.hasNext()) {
			SmaliEntry se = iterator.next();
	    	se.itRefClassNames.clear();
	    	se.itRefClass.clear();
	    	se.refItClass.clear();
	    	se.fatherClass = null;
	    	
	    	map.put(se.classHeader.classNameSelf, se);
		}
		
		
		List<File> fileList = new ArrayList<File>();

		for(File dir : dirs){
			loadChildren(dir, fileList);
		}

		
		String errFileName = null;
		
		try{
			if(fileList.size()>0){
				monitor = new ProgressMonitor(parent, "Del Progress", "Getting Started...", 0, fileList.size());

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
                	
                	if(!Parse_success){
                		System.out.println("delRoot - myParser error:" + smaliEntry.toString());
                		continue;
                	}
                	

            		if(!map.containsKey(smaliEntry.classHeader.classNameSelf)){
            			continue;
            		}
            		
            		smaliEntry = map.get(smaliEntry.classHeader.classNameSelf);
            		
            		globeClassSet.remove(smaliEntry);
            		map.remove(smaliEntry.classHeader.classNameSelf);
            		
            		packageEntry = findEntry(smaliEntry.packageName); //查找该节点的父节点，即包
            		
            		//del leaf and none-leaf package
	            	if(packageEntry!=null){
	            		packageEntry.leafChildren.remove(smaliEntry);
	            		
	            		if(packageEntry.leafChildren.isEmpty()){
    	            		root.leafChildren.remove(packageEntry);
    	            		packageSet.remove(smaliEntry.packageName);
	            		}

	            	}

               
	            }

			}
		}catch(Exception e){
			e.printStackTrace();
			monitor.setProgress(0);
        	monitor.setNote("Loaded fail:" + progress + "\n errFileName:" + errFileName);
		} 
		

		
		System.out.println("ReBuild refItClassName Map...");

		if(!monitor.isCanceled()){
			
			monitor = new ProgressMonitor(parent, "Buld smali relationship", "Getting Started...", 0, globeClassSet.size());
			progress = 0;
			
			iterator = globeClassSet.iterator();
			
			Pattern p = Pattern.compile(clzRegexPatten);

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
            	

            	
            	Matcher match = p.matcher(se.content);
				while(match.find()){
					int startIndex = match.start();
					int endIndex = match.end();
					String matchStr = se.content.substring(startIndex, endIndex);
					if(matchStr!=null && map.containsKey(matchStr)){
						if(!se.itRefClassNames.contains(matchStr)){
							se.itRefClassNames.add(matchStr);
						}
						
						SmaliEntry se2 = map.get(matchStr);
						if(se2!=null){
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
	
	private void loadChildren(File parent, List<File> fileList){
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
						loadChildren(f, fileList);
					}
					
				}
			}
		}else if(parent.isFile() && parent.getName().endsWith(".smali")){
			//EntryVector 里装的必定是文件或函数
			fileList.add(parent);

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
