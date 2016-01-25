package com.zhangwei.smali.json;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ShrinkCommand {
	public String[] beginClzName; //"com.tencent.com.ui.loginActivity"
	public String[] endClzName; //"com.tencent.com.ui.helpActivity"

	public static void main(String[] args) throws IOException{
		ShrinkCommand c = new ShrinkCommand();
		
		File service_nall_file = new File("service_name_need.txt");
		File activity_all_file = new File("activity_name_all.txt");
		File activity_need_file = new File("activity_name_need.txt");
		
		List<String> t1 = FileUtils.readLines(service_nall_file);
		
		List<String> serviceList = new ArrayList<String>();
		for(String str : t1){
			if(!str.equals("")){
				serviceList.add(str);
			}
		}
		
		
		List<String> t2 = FileUtils.readLines(activity_all_file);
		List<String> activityList = new ArrayList<String>();
		for(String str : t2){
			if(!str.equals("")){
				activityList.add(str);
			}
		}
	

		List<String> t3 = FileUtils.readLines(activity_need_file);
		List<String> activityNeedList = new ArrayList<String>();
		for(String str : t3){
			if(!str.equals("")){
				activityNeedList.add(str);
			}
		}
	
		
		for(String needStr : activityNeedList){
			Iterator<String> iterator = activityList.iterator();
			while(iterator.hasNext()){
				String item = iterator.next();
				if(item.equals(needStr)){
					iterator.remove();
					break;
				}
			}
		}
		
		activityNeedList.addAll(serviceList);
		
		c.beginClzName = activityNeedList.toArray(new String[activityNeedList.size()]);
		c.endClzName = activityList.toArray(new String[activityList.size()]);
		
		GsonBuilder gb = new GsonBuilder();
		gb.setPrettyPrinting();
		Gson gson = gb.create();
		
		FileUtils.write(new File("shrink.json"), gson.toJson(c));
	}
}
