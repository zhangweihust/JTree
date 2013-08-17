package com.zhangwei.ui.jtree;

import java.io.File;

import com.zhangwei.smali.api.MyParser;

public class TestMain {
	private static MyParser myParser;
	
	public static void main(final String args[]) {
		try{
			myParser = new MyParser();
			File file = new File("D:\\android\\crack\\guosen3.6\\smali\\com\\a\\a.smali");
			if(file.exists() && file.isFile()){
				myParser.paser(file);
			}
		}catch(Exception e){
			e.printStackTrace();
		}

	}
}
