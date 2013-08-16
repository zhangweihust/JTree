package com.zhangwei.smali.api;

/**
 * classMethod = optPadding dirMethod padding *(accessMode padding) (classConstructorName / clssMethodName) optPadding classMethodProto optPadding CRLF methodBody optPadding dirEndMethod optPadding CRLF;
 * classMethod = *accessMode [classConstructorName] classMethodProto methodBody
 * 
 * */
public class MethodEntry extends CommonEntry {
	
	public MethodEntry(){
		super(3);
	}
	
	String classConstructorName; //用来标识该函数是否为构造函数
	String classMethodName; //函数名
	String classMethodProto;    //用来表示该函数的原型，参数及返回值
	
	public void close() {
		// TODO Auto-generated method stub
		if(classConstructorName!=null){
			super.id = classConstructorName + "_" + classMethodProto;
		}else{
			super.id = classMethodName + "_" + classMethodProto;
		}

	}
}
