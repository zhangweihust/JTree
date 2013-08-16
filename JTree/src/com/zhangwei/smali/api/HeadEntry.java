package com.zhangwei.smali.api;

public class HeadEntry extends CommonEntry{
	//classHeader = *skipLine classClass *skipLine classSuper *skipLine [classSource] *skipLine *classImplements *skipLine;
    //classHeader = classClass classSuper
	String classClass;
	String classSuper;
	
	public HeadEntry(){
		super(1);
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
