package com.zhangwei.jtree;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.zhangwei.smali.api.SmaliEntry;

public class DirLoader {
	private static DirLoader ins=null;
	public static String rootpath = null;
	
	private ArrayList<File> list;
	private HashMap<String, SmaliEntry> smailMap;
	
	
	private DirLoader(){
		list = new ArrayList<File>();
		smailMap = new HashMap<String, SmaliEntry>();
	};
	

	
	public static DirLoader getInstance(){
		if(ins==null){
			ins = new DirLoader();
		}
		
		return ins;
	}
	
	
	public void Load(EntryVector<String, Object> entryVector, String root_path){
		
		if(root_path!=null ){
			if(!root_path.endsWith("\\")){
				root_path = root_path + '\\';
			}
		}else{
			return ;
		}

		if(list!=null){
			list.clear();
		}

		if(smailMap!=null){
			smailMap.clear();
		}

		if(entryVector!=null){
			entryVector.clear();
		}
		
		try{
			File rootdir = new File(root_path);
			if(rootdir.exists() && rootdir.isDirectory()){
				entryVector.put(rootdir.getAbsolutePath(), new SmaliEntry(rootdir, false));
				//root = new EntryVector<String, Object>(new SmaliEntry(rootdir, false));
				
				loadChildren(rootdir);
				
				for(File file : list){
					if(file.isFile()){
						String pathOfParent = file.getParent();
						if(!pathOfParent.endsWith("\\")){
							pathOfParent = pathOfParent + '\\';
						}
						pathOfParent = pathOfParent.replace(root_path, "").replace('\\', '.');
						if(pathOfParent==null || pathOfParent.length()==0 || pathOfParent.equals("")){
							pathOfParent = "(default)";
						}
						
						EntryVector<String, Object> packageitem = (EntryVector<String, Object>) entryVector.get(pathOfParent);

						if(packageitem==null){
							packageitem =  new EntryVector<String, Object>(new SmaliEntry(file.getParentFile(), false));
							entryVector.put(pathOfParent, packageitem);

						}
						SmaliEntry item = new SmaliEntry(file, true);
						packageitem.put(file.getName(), item);
						smailMap.put(file.getName(), item);
					}
				}
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		
		

		return;
	}
	
	private void loadChildren(File parent){
		if(parent==null){
			return;
		}
		
		if(parent.isDirectory()){
			File[] filelist = parent.listFiles();
			if(filelist!=null && filelist.length>0){
				for (File f : filelist) {
					if(f.isFile()){
						//EntryVector 里装的必定是文件或函数
						list.add(f);

					}else{
						//递归遍历目录
						loadChildren(f);
					}
					
				}
			}else{
				
			}

		}else{
			//EntryVector 里装的必定是文件或函数， 以后加入smali解析的功能
			
		}
	}

}
