package com.zhangwei.smali.api;

import com.zhangwei.parser.Displayer;
import com.zhangwei.parser.Rule_classClass;
import com.zhangwei.parser.Rule_classConstructorName;
import com.zhangwei.parser.Rule_classField;
import com.zhangwei.parser.Rule_classFieldName;
import com.zhangwei.parser.Rule_classFieldType;
import com.zhangwei.parser.Rule_classHeader;
import com.zhangwei.parser.Rule_classMethod;
import com.zhangwei.parser.Rule_classMethodName;
import com.zhangwei.parser.Rule_classMethodProto;
import com.zhangwei.parser.Rule_classSuper;
import com.zhangwei.parser.Rule_smali;
import com.zhangwei.parser.Terminal_NumericValue;
import com.zhangwei.parser.Terminal_StringValue;

public class MyDisplayer extends Displayer {
	private SmaliEntry smali_entry;

	public MyDisplayer(SmaliEntry a) {
		this.smali_entry = a;
		DisplayDumper.getInstance().reset();
	}

	//-------------------------classHeader-----------------------

	@Override
	public Object visit(Rule_classHeader rule) {
		Log("<classHeader>");
		smali_entry.new_classHeader();
		
		int begin = DisplayDumper.getInstance().getIndex();

		Object ret = visitRules(rule.rules);

		Log("</classHeader>");
		int end = DisplayDumper.getInstance().getIndex();

		smali_entry.add_classHeader_content(DisplayDumper.getInstance().getSubStr(begin, end));
		
		smali_entry.close_classHeader();
		return ret;
	}

	public Object visit(Rule_classClass rule) {
		Log("<classClass>");
		int begin = DisplayDumper.getInstance().getIndex();

		Object ret = visitRules(rule.rules);

		Log("</classClass>");
		int end = DisplayDumper.getInstance().getIndex();

		smali_entry.add_classHeader_classClass(DisplayDumper.getInstance().getSubStr(begin, end));

		return ret;
	}

	public Object visit(Rule_classSuper rule) {
		Log("<classSuper>");
		int begin = DisplayDumper.getInstance().getIndex();

		Object ret = visitRules(rule.rules);

		Log("</classSuper>");
		int end = DisplayDumper.getInstance().getIndex();

		smali_entry.add_classHeader_classSuper(DisplayDumper.getInstance().getSubStr(begin, end));

		return ret;
	}



	//-----------------------------classField----------------------------
	@Override
	public Object visit(Rule_classField rule) {
		Log("<classField>");
		smali_entry.new_classField();
		
		int begin = DisplayDumper.getInstance().getIndex();

		Object ret =  visitRules(rule.rules);

		Log("</classField>");
		int end = DisplayDumper.getInstance().getIndex();
		
		smali_entry.add_classField_content(DisplayDumper.getInstance().getSubStr(begin, end));

		smali_entry.close_classField();
		return ret;
	}

	public Object visit(Rule_classFieldName rule) {
		Log("<classFieldName>");
		int begin = DisplayDumper.getInstance().getIndex();
		Object ret = visitRules(rule.rules);
		int end = DisplayDumper.getInstance().getIndex();
		Log("</classFieldName>");
		smali_entry.put_classField_Name(DisplayDumper.getInstance().getSubStr(begin, end));
		
		return ret;
	}

	public Object visit(Rule_classFieldType rule) {
		Log("<classFieldType>");
		int begin = DisplayDumper.getInstance().getIndex();
		Object ret = visitRules(rule.rules);
		int end = DisplayDumper.getInstance().getIndex();
		Log("</classFieldType>");
		smali_entry.put_classField_Type(DisplayDumper.getInstance().getSubStr(begin, end));
		
		return ret;
	}

	//------------------------------classMethod----------------------------
	@Override
	public Object visit(Rule_classMethod rule) {
		Log("<classMethod>");
		smali_entry.new_classMethod();
		
		int begin = DisplayDumper.getInstance().getIndex();

		Object ret = visitRules(rule.rules);

		Log("</classMethod>");
		int end = DisplayDumper.getInstance().getIndex();
		
		smali_entry.add_classMethod_content(DisplayDumper.getInstance().getSubStr(begin, end));

		smali_entry.close_classMethod();
		return ret;

	}
	
	@Override
	public Object visit(Rule_classConstructorName rule) {
		Log("<classConstructorName>");
		int begin = DisplayDumper.getInstance().getIndex();
		Object ret = visitRules(rule.rules);
		int end = DisplayDumper.getInstance().getIndex();
		Log("</classConstructorName>");
		smali_entry.put_classMethod_classConstructorName(DisplayDumper.getInstance().getSubStr(begin, end));
		
		return ret;
	}

	public Object visit(Rule_classMethodProto rule) {
		Log("<classMethodProto>");
		int begin = DisplayDumper.getInstance().getIndex();
		Object ret = visitRules(rule.rules);
		int end = DisplayDumper.getInstance().getIndex();
		Log("</classMethodProto>");
		smali_entry.put_classMethod_classMethodProto(DisplayDumper.getInstance().getSubStr(begin, end));
		
		return ret;
	}

	public Object visit(Rule_classMethodName rule) {
		Log("<classMethodName>");
		int begin = DisplayDumper.getInstance().getIndex();
		Object ret = visitRules(rule.rules);
		int end = DisplayDumper.getInstance().getIndex();
		Log("</classMethodName>");
		smali_entry.put_classMethod_classMethodName(DisplayDumper.getInstance().getSubStr(begin, end));
		
		return ret;
	}
	
	//--------------------------------------------------------

	@Override
	public Object visit(Terminal_StringValue value) {
		DisplayDumper.getInstance().writeStr(value.spelling);
		return null;
	}

	@Override
	public Object visit(Terminal_NumericValue value) {
		DisplayDumper.getInstance().writeStr(value.spelling);
		return null;
	}
	
	private void Log(String str){
		//System.out.println(str);
	}

}
