package com.zhangwei.jtree;

import java.util.Hashtable;
import java.util.Vector;

import com.zhangwei.smali.api.SmaliEntry;

/**
 *  代表JTree上的一个条目，它可能有多个元素（子节点）
 * */
public class EntryVector<K, E> extends Hashtable<K, E> {

	public String fullpath;
	public int type; // 0 未定义 1 目录  2smali文件 3函数


	public EntryVector(String fullpath, int type) {
		// TODO Auto-generated constructor stub
		this.fullpath = fullpath;
		this.type = type;
	}
	

	public EntryVector(SmaliEntry smaliEntry) {
		// TODO Auto-generated constructor stub
		this.fullpath = smaliEntry.file.getAbsolutePath();
		this.type = smaliEntry.file.isDirectory()?1:2;
	}




	public String toString() {
		String ret = "unkonw";
		if(DirLoader.rootpath!=null){
			String[] array_str = this.fullpath.split(DirLoader.rootpath);
			ret = array_str[array_str.length-1];
		}
		
		return this.type + ret;

		
	}

}
