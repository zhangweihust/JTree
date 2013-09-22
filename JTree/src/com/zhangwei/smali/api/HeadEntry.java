package com.zhangwei.smali.api;

public class HeadEntry extends CommonEntry{
	//classHeader = *skipLine classClass *skipLine classSuper *skipLine [classSource] *skipLine *classImplements *skipLine;
    //classHeader = classClass classSuper
	public String classClass;
	public String classSuper;  //.super Lcom/b/a/b;
	public String classNameSelf; // Lcom/b/a/a;
	public String classNameSuper; // Lcom/b/a/b;
	
	public HeadEntry(int offset){
		super(1, offset);
	}
	
	public void add_content(String content) {
		// TODO Auto-generated method stub
		super.content = content;
	}

	public void close() {
		// TODO Auto-generated method stub
		super.id = classClass;
	}
}
