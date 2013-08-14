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
		// System.out.println("<classHeader>");
		smali_entry.new_classHeader();
		
		int begin = DisplayDumper.getInstance().getIndex();

		Object ret = visitRules(rule.rules);

		// System.out.println("</classHeader>");
		int end = DisplayDumper.getInstance().getIndex();

		smali_entry.add_classHeader_content(DisplayDumper.getInstance().getSubStr(begin, end));

		return ret;
	}

	public Object visit(Rule_classClass rule) {
		// System.out.println("<classHeader>");
		int begin = DisplayDumper.getInstance().getIndex();

		Object ret = visitRules(rule.rules);

		// System.out.println("</classHeader>");
		int end = DisplayDumper.getInstance().getIndex();

		smali_entry.add_classHeader_classClass(DisplayDumper.getInstance().getSubStr(begin, end));

		return ret;
	}

	public Object visit(Rule_classSuper rule) {
		// System.out.println("<classHeader>");
		int begin = DisplayDumper.getInstance().getIndex();

		Object ret = visitRules(rule.rules);

		// System.out.println("</classHeader>");
		int end = DisplayDumper.getInstance().getIndex();

		smali_entry.add_classHeader_classSuper(DisplayDumper.getInstance().getSubStr(begin, end));

		return ret;
	}



	//-----------------------------classField----------------------------
	@Override
	public Object visit(Rule_classField rule) {
		// System.out.println("<classField>");
		smali_entry.new_classField();
		
		int begin = DisplayDumper.getInstance().getIndex();

		Object ret =  visitRules(rule.rules);

		// System.out.println("</classField>");
		int end = DisplayDumper.getInstance().getIndex();
		
		smali_entry.add_classField_content(DisplayDumper.getInstance().getSubStr(begin, end));

		return ret;
	}

	public Object visit(Rule_classFieldName rule) {
		int begin = DisplayDumper.getInstance().getIndex();
		Object ret = visitRules(rule.rules);
		int end = DisplayDumper.getInstance().getIndex();
		
		smali_entry.put_classField_Name(DisplayDumper.getInstance().getSubStr(begin, end));
		
		return ret;
	}

	public Object visit(Rule_classFieldType rule) {
		int begin = DisplayDumper.getInstance().getIndex();
		Object ret = visitRules(rule.rules);
		int end = DisplayDumper.getInstance().getIndex();
		
		smali_entry.put_classField_Type(DisplayDumper.getInstance().getSubStr(begin, end));
		
		return ret;
	}

	//------------------------------classMethod----------------------------
	@Override
	public Object visit(Rule_classMethod rule) {
		//System.out.println("<classMethod>");
		smali_entry.new_classMethod();
		
		int begin = DisplayDumper.getInstance().getIndex();

		Object ret = visitRules(rule.rules);

		//System.out.println("</classMethod>");
		int end = DisplayDumper.getInstance().getIndex();
		
		smali_entry.add_classMethod_content(DisplayDumper.getInstance().getSubStr(begin, end));

		return ret;

	}
	
	@Override
	public Object visit(Rule_classConstructorName rule) {
		
		int begin = DisplayDumper.getInstance().getIndex();
		Object ret = visitRules(rule.rules);
		int end = DisplayDumper.getInstance().getIndex();
		
		smali_entry.put_classMethod_classConstructorName(DisplayDumper.getInstance().getSubStr(begin, end));
		
		return ret;
	}

	public Object visit(Rule_classMethodProto rule) {
		int begin = DisplayDumper.getInstance().getIndex();
		Object ret = visitRules(rule.rules);
		int end = DisplayDumper.getInstance().getIndex();
		
		smali_entry.put_classMethod_classMethodProto(DisplayDumper.getInstance().getSubStr(begin, end));
		
		return ret;
	}

	public Object visit(Rule_classMethodName rule) {
		int begin = DisplayDumper.getInstance().getIndex();
		Object ret = visitRules(rule.rules);
		int end = DisplayDumper.getInstance().getIndex();
		
		smali_entry.put_classMethod_classMethodName(DisplayDumper.getInstance().getSubStr(begin, end));
		
		return ret;
	}
	
	//--------------------------------------------------------

	@Override
	public Object visit(Terminal_StringValue value) {
		// System.out.print(value.spelling);
		DisplayDumper.getInstance().writeStr(value.spelling);
		return null;
	}

	@Override
	public Object visit(Terminal_NumericValue value) {
		// System.out.print(value.spelling);
		DisplayDumper.getInstance().writeStr(value.spelling);
		return null;
	}

}
