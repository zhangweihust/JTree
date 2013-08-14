package com.zhangwei.smali.api;

import java.io.File;
import java.util.ArrayList;

/**
 *  代表一个smali文件
 * */
public class SmaliEntry {

	public File file;
	public boolean isFile;
	
	//smali = classHeader *(classField / skipLine) *(classMethod / skipLine);
	HeadEntry classHeader;
	ArrayList<FieldEntry> entry_field_array;
	ArrayList<MethodEntry> entry_method_array;
	
	public SmaliEntry(File file, boolean isFile){
		this.file = file;	
		this.isFile = isFile;
	}

	// ------------------------classHeader --------------------- 
	public void new_classHeader() {
		// TODO Auto-generated method stub
		if(classHeader==null){
			classHeader = new HeadEntry();
		}
	}
		
	
	public void add_classHeader_content(String content) {
		// TODO Auto-generated method stub

		classHeader.add_content(content);
	}
	
	public void add_classHeader_classClass(String classClass) {
		// TODO Auto-generated method stub
		classHeader.classClass = classClass;
	}
	
	public void add_classHeader_classSuper(String classSuper) {
		// TODO Auto-generated method stub
		classHeader.classSuper = classSuper;
	}


	
	// ---------------      classField ---------------------   
	public void new_classField(){
		if(entry_field_array==null){
			entry_field_array = new ArrayList<FieldEntry>();
		}
		
		entry_field_array.add(new FieldEntry());
	}

	public void add_classField_content(String content) {
		// TODO Auto-generated method stub
		FieldEntry last = entry_field_array.get(entry_field_array.size()-1);
		last.content = content;

	}
	
	public void put_classField_Name(String classFieldName) {
		// TODO Auto-generated method stub
		FieldEntry last = entry_field_array.get(entry_field_array.size()-1);
		last.classFieldName = classFieldName;
	}
	
	public void put_classField_Type(String classFieldType) {
		// TODO Auto-generated method stub
		FieldEntry last = entry_field_array.get(entry_field_array.size()-1);
		last.classFieldType= classFieldType;
	}

	// ---------------      classMethod ---------------------   
	public void new_classMethod() {
		// TODO Auto-generated method stub
		if(entry_method_array==null){
			entry_method_array = new ArrayList<MethodEntry>();
		}
		
		entry_method_array.add(new MethodEntry());
	}

	public void add_classMethod_content(String content) {
		// TODO Auto-generated method stub
		MethodEntry last = entry_method_array.get(entry_method_array.size()-1);
		last.content = content;

	}

	public void put_classMethod_classConstructorName(String classConstructorName) {
		// TODO Auto-generated method stub
		MethodEntry last = entry_method_array.get(entry_method_array.size()-1);
		last.classConstructorName = classConstructorName;
	}

	public void put_classMethod_classMethodProto(String classMethodProto) {
		// TODO Auto-generated method stub
		MethodEntry last = entry_method_array.get(entry_method_array.size()-1);
		last.classMethodProto = classMethodProto;
	}

	public void put_classMethod_classMethodName(String classMethodName) {
		// TODO Auto-generated method stub
		MethodEntry last = entry_method_array.get(entry_method_array.size()-1);
		last.classMethodName = classMethodName;
	}








}
