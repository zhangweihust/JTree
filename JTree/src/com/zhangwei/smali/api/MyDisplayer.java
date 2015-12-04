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
import com.zhangwei.parser.Rule_classMethodProtoOfMethod;
import com.zhangwei.parser.Rule_className;
import com.zhangwei.parser.Rule_classNameSelf;
import com.zhangwei.parser.Rule_classNameSuper;
import com.zhangwei.parser.Rule_classSuper;
import com.zhangwei.parser.Rule_smali;
import com.zhangwei.parser.Terminal_NumericValue;
import com.zhangwei.parser.Terminal_StringValue;
import com.zhangwei.ui.jtree.SmaliLoader;

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

		int begin = DisplayDumper.getInstance().getIndex();
		smali_entry.new_classHeader(begin);
		

		Object ret = visitRules(rule.rules);

		Log("</classHeader>");
		int end = DisplayDumper.getInstance().getIndex();
		
		String value = DisplayDumper.getInstance().getSubStr(begin, end);

		smali_entry.add_classHeader_content(rule.toString());
		
		smali_entry.close_classHeader();
		return ret;
	}

	public Object visit(Rule_classClass rule) {
		Log("<classClass>");
		int begin = DisplayDumper.getInstance().getIndex();

		Object ret = visitRules(rule.rules);

		Log("</classClass>");
		int end = DisplayDumper.getInstance().getIndex();

		String value = DisplayDumper.getInstance().getSubStr(begin, end);
		
		smali_entry.add_classHeader_classClass(rule.toString());

		return ret;
	}
	
	public Object visit(Rule_classNameSelf rule) {
		Log("<classNameSelf>");
		int begin = DisplayDumper.getInstance().getIndex();

		Object ret = visitRules(rule.rules);

		Log("</classNameSelf>");
		int end = DisplayDumper.getInstance().getIndex();

		String value = DisplayDumper.getInstance().getSubStr(begin, end);
		
		smali_entry.add_classHeader_classNameSelf(rule.toString());

		return ret;
	}

	public Object visit(Rule_classSuper rule) {
		Log("<classSuper>");
		int begin = DisplayDumper.getInstance().getIndex();

		Object ret = visitRules(rule.rules);

		Log("</classSuper>");
		int end = DisplayDumper.getInstance().getIndex();
		
		String value = DisplayDumper.getInstance().getSubStr(begin, end);
		
		smali_entry.add_classHeader_classSuper(rule.toString());

		return ret;
	}
	
	public Object visit(Rule_classNameSuper rule) {
		Log("<classNameSuper>");
		int begin = DisplayDumper.getInstance().getIndex();

		Object ret = visitRules(rule.rules);

		Log("</classNameSuper>");
		int end = DisplayDumper.getInstance().getIndex();

		String value = DisplayDumper.getInstance().getSubStr(begin, end);
		
		smali_entry.add_classHeader_classNameSuper(rule.toString());

		return ret;
	}



	//-----------------------------classField----------------------------
	@Override
	public Object visit(Rule_classField rule) {
		Log("<classField>");
		int begin = DisplayDumper.getInstance().getIndex();
		smali_entry.new_classField(begin);

		Object ret =  visitRules(rule.rules);

		Log("</classField>");
		int end = DisplayDumper.getInstance().getIndex();
		
		String value = DisplayDumper.getInstance().getSubStr(begin, end);
		
		smali_entry.add_classField_content(rule.toString());

		smali_entry.close_classField();
		return ret;
	}

	public Object visit(Rule_classFieldName rule) {
		Log("<classFieldName>");
		int begin = DisplayDumper.getInstance().getIndex();
		Object ret = visitRules(rule.rules);
		int end = DisplayDumper.getInstance().getIndex();
		Log("</classFieldName>");
		
		String value = DisplayDumper.getInstance().getSubStr(begin, end);
		
		smali_entry.put_classField_Name(rule.toString());
		
		return ret;
	}

	public Object visit(Rule_classFieldType rule) {
		Log("<classFieldType>");
		int begin = DisplayDumper.getInstance().getIndex();
		Object ret = visitRules(rule.rules);
		int end = DisplayDumper.getInstance().getIndex();
		Log("</classFieldType>");
		
		String value = DisplayDumper.getInstance().getSubStr(begin, end);
		
		smali_entry.put_classField_Type(rule.toString());
		
		return ret;
	}
	
	@Override
	public Object visit(Rule_className rule){
		Log("<className>");
		Object ret = visitRules(rule.rules);
		Log("</className>");
		
		SmaliEntry refClz = SmaliLoader.globeClassMap.get(rule.toString());
		if(refClz!=null){
			smali_entry.putItRefClassName(refClz);
		}
		
		
		
		
		return ret;
	}
	
	

	//------------------------------classMethod----------------------------
	@Override
	public Object visit(Rule_classMethod rule) {
		Log("<classMethod>");

		int begin = DisplayDumper.getInstance().getIndex();
		smali_entry.new_classMethod(begin);

		Object ret = visitRules(rule.rules);

		Log("</classMethod>");
		int end = DisplayDumper.getInstance().getIndex();
		
		String value = DisplayDumper.getInstance().getSubStr(begin, end);
		
		smali_entry.add_classMethod_content(rule.toString());

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
		
		String value = DisplayDumper.getInstance().getSubStr(begin, end);
		
		smali_entry.put_classMethod_classConstructorName(rule.toString());
		
		return ret;
	}

	public Object visit(Rule_classMethodProtoOfMethod rule) {
		Log("<classMethodProtoOfMethod>");
		int begin = DisplayDumper.getInstance().getIndex();
		Object ret = visitRules(rule.rules);
		int end = DisplayDumper.getInstance().getIndex();
		Log("</classMethodProtoOfMethod>");
		
		String value = DisplayDumper.getInstance().getSubStr(begin, end);
		
		smali_entry.put_classMethod_classMethodProto(rule.toString());
		
		return ret;
	}

	public Object visit(Rule_classMethodName rule) {
		Log("<classMethodName>");
		int begin = DisplayDumper.getInstance().getIndex();
		Object ret = visitRules(rule.rules);
		int end = DisplayDumper.getInstance().getIndex();
		Log("</classMethodName>");
		
		String value = DisplayDumper.getInstance().getSubStr(begin, end);
		
		smali_entry.put_classMethod_classMethodName(rule.toString());
		
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
//		System.out.println(str);
	}

}
