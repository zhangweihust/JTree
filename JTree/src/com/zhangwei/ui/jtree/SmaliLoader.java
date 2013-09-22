package com.zhangwei.ui.jtree;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.ProgressMonitor;
import javax.swing.Timer;
import javax.swing.tree.DefaultMutableTreeNode;

import com.zhangwei.smali.api.MyParser;
import com.zhangwei.smali.api.SmaliEntry;

public class SmaliLoader {
	private static SmaliLoader ins=null;
	public static String rootpath = null;
	
	private ArrayList<File> list;
	private HashMap<String, SmaliEntry> smailMap;
	
	private MyParser myParser;
	
	private ProgressMonitor monitor;
	private int progress;
	
	private SmaliLoader(){
		list = new ArrayList<File>();
		smailMap = new HashMap<String, SmaliEntry>();
		myParser = new MyParser();
	}
	
	public static SmaliLoader getInstance(){
		if(ins==null){
			ins = new SmaliLoader();
		}
		
		return ins;
	}
	
	public void renameClass(SmaliEntry se, String src_className, String dst_className){
		
		for(Entry<String, SmaliEntry>  item: smailMap.entrySet()){
			SmaliEntry se_item = item.getValue();
			se_item.renameClassVar(src_className, dst_className);
		}
		
		se.renameClassFile(src_className, dst_className);
	}
	
	public void loadRoot(Component parent, SmaliEntry root){
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
				int i = 0;
				
	            for (File file : list) {
	            	i++;
	            	progress = i;
	            	monitor.setProgress(progress);
	            	monitor.setNote("Loaded num:" + progress + " file:" + file.getAbsolutePath());
	            	if(monitor.isCanceled()){
	            		break;
	            	}
                	//add non-leaf package if not exist
                	SmaliEntry packageEntry = null;
                	SmaliEntry smaliEntry = new SmaliEntry(file, true, file.getName());
                	boolean Parse_success = myParser.paser(smaliEntry);
                	if(Parse_success){
    	            	boolean found = false;
    	            	for(SmaliEntry v : root.children){
    	            		if(v.file.getAbsolutePath()!=null && v.file.getAbsolutePath().equals(file.getParentFile().getAbsolutePath())){
    	            			found = true; //找到该file的父目录(package)
    	            			packageEntry = v;
    	            			break;
    	            		}
    	            	}
    	            	
    	            	if(!found){
    						String pathOfParent = file.getParent();
    						if(!pathOfParent.endsWith("\\")){
    							pathOfParent = pathOfParent + '\\';
    						}
    						String packageName = pathOfParent.replace(SmaliEntry.rootPath, "").replace("\\", ".");
    						if(packageName.equals("")){
    							packageName = ".";
    						}else if(packageName.endsWith(".")){
    							packageName = packageName.substring(0, packageName.length()-1);
    						}
    	            		packageEntry = new SmaliEntry(file.getParentFile(), false, packageName);
    	            		root.children.add(packageEntry);
    	            		//smailMap.put(packageEntry.file.getAbsolutePath(), packageEntry);
    	            	}
    	            	
    	            	//add leaf
    	            	packageEntry.children.add(smaliEntry);
    	            	smailMap.put(file.getAbsolutePath(), smaliEntry);
                	}else{
                		break;
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
	

}
