package com.zhangwei.smali.json;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Command {
	public transient static String RenameType = "rename";
	public transient static String ItRefClzType = "itRefClz";
	public transient static String RefItClzType = "RefItClz";
	
	public String Type; //"rename" "itRefClz" "RefItClz"
	public String srcClzName; //Lcom/tencent/mm/pointers/PString; (*)
	public String dstClzName; // Lcom/tencent/mm/pointers/PString2; 
	
	public String startClzPattern; //Lcom/tencent/mm/
	public String[] exceptClzPattern; //Lcom/tencent/mm/pointers/
	
	public static void main(String[] args){
		Command c = new Command();
		c.Type = "rename";
		c.srcClzName = "Lcom/tencent/mm/pointers/PString;";
		c.dstClzName = "Lcom/tencent/mm/pointers/PString2;";
		c.startClzPattern = "Lcom/tencent/mm/";
		c.exceptClzPattern = new String[2];
		c.exceptClzPattern[0] = "Lcom/tencent/mm/pointers/";
		c.exceptClzPattern[1] = "Lcom/tencent/mm/o/";
		

		GsonBuilder gb = new GsonBuilder();
		gb.setPrettyPrinting();
		Gson gson = gb.create();
		
		try {
			FileUtils.write(new File("test.json"), gson.toJson(c));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
