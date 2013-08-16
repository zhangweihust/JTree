package com.zhangwei.smali.api;

public class FieldEntry extends CommonEntry {
	//classField = optPadding dirField padding *(accessMode padding) classFieldName COLON classFieldType optPadding [EQ optPadding value] optPadding CRLF [dirEndField optPadding CRLF];
	//classField = *accessMode classFieldName classFieldType
	public FieldEntry(){
		super(2);
	}
	
	String classFieldName;
	String classFieldType;
}
