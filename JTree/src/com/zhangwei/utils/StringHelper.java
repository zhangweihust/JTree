package com.zhangwei.utils;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

public class StringHelper {

	
	/**
	 * @param className  La/b/c/d;
	 * @return packageName La/b/c
	 * 
	 * */
	public static String getPackageNameFromCLz(String className){
		if(className!=null && className.length()>0){
			int index = className.lastIndexOf("/");
			if(index>0){
				return className.substring(0, index);
			}else{
				return "";
			}

		}else{
			return null;
		}
	}
	
	/**
	 *  @param className eg: Lcom/a/b/c;
	 *  @return c
	 * */
	public static String getShortNameOfSmali(String className){
		String ret = null;
		if(className!=null){
			String sub = className.substring(1, className.length()-1); // com/a/b/c
			String[] array = sub.split("/");
			ret = array[array.length-1];
		}
 
		return ret;
	}
	
	public static String escapeExprSpecialWord(String keyword) {  
	    if (keyword!=null && !"".equals(keyword)) {  
	        String[] fbsArr = { "\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|" };  
	        for (String key : fbsArr) {  
	            if (keyword.contains(key)) {  
	                keyword = keyword.replace(key, "\\" + key);  
	            }  
	        }  
	    }  
	    return keyword;  
	} 
	
	private static boolean checkHunXiaoName(String name){
		if(name.length()<=2){
			return true;
		}
		
		return false;
	}
	
	/**
	 * @param str : a, 1, ab, 12
	 * */
	private static String transSimpleStr(String prefix, String str, String postfix){
		if(str.length()<=2){
			return prefix + str.toUpperCase() + postfix; 
		}else{
			return str;
		}
	}
	
	public static String GetAutoRenameSmali(String basicName){
		if(basicName.contains("$")){
			String[] array = basicName.split("\\$");
			if(array.length>1){
				StringBuilder sb = new StringBuilder();
				for(int index=0; index<array.length; index++){
					if(index==0){
						sb.append(transSimpleStr("", array[index], "clz"));
						sb.append("$");
					}else if(index==array.length-1){
						sb.append(transSimpleStr("Inner", array[index], ""));
					}else{
						sb.append(transSimpleStr("Inner", array[index], ""));
						sb.append("$");
					}

				}
				return sb.toString();
			}else{
				return basicName;
			}
		}else{
			if (basicName.length()<=2){
				return transSimpleStr("", basicName, "clz");
			}else{
				return basicName;
			}
		}
	}
	
	public static boolean isInnerClass(String name){
		return name.contains("$");
	}
	
	public static boolean needRename(String name){
		String newName = GetAutoRenameSmali(name);
		if(newName.equals(name)){
			return false;
		}else{
			return true;
		}
	}
	
	/**
	 * 得到本类名
	 * @param clzNameSelf Lcom/tencent/mm/ui/transmit/MsgRetransmitUI$Inner8$Inner1; / Lcom/tencent/mm/ui/transmit/MsgRetransmitUI$8$12;
	 * @return OutClzNameSef Inner1 / 12
	 * */
	public static String getBasicClzNameStr(String clzNameSelf){
		if(clzNameSelf.contains("$")){
			int index = clzNameSelf.lastIndexOf("$");
			String old_tmp_name_left = clzNameSelf.substring(index+1, clzNameSelf.length()); //Inner1
			return old_tmp_name_left;
		}else{
			return clzNameSelf;
		}
	}
	
	/**
	 * 往外一级，得到外部类名
	 * @param clzNameSelf Lcom/tencent/mm/ui/transmit/MsgRetransmitUI$8$1;
	 * @return OutClzNameSef Lcom/tencent/mm/ui/transmit/MsgRetransmitUI$8;
	 * */
	public static String getOutClzNameSef(String clzNameSelf){
		if(clzNameSelf.contains("$")){
			int index = clzNameSelf.lastIndexOf("$");
			String old_tmp_name_left = clzNameSelf.substring(0, index); //Lcom/tencent/mm/ui/transmit/MsgRetransmitUI$8
			return old_tmp_name_left + ";";
		}else{
			return clzNameSelf;
		}
	}
	
	/**
	 * 往外一级，得到外部类名
	 * @param oldClassNameSelf Lcom/tencent/mm/ui/transmit/MsgRetransmitUI$8$1;
	 * @return newClassNameSelf Lcom/tencent/mm/ui/transmit/MsgRetransmitUI$Inner8$Inner1;
	 * */
	public static String getNewClassNameSelf(String oldClassNameSelf){
    	int index = oldClassNameSelf.lastIndexOf("/");
    	String old_tmp_name_left = oldClassNameSelf.substring(0, index+1); //La/b/
    	String old_tmp_name_right = oldClassNameSelf.substring(index+1, oldClassNameSelf.length()-1); //去掉; c
    	
    	String new_name_right = GetAutoRenameSmali(old_tmp_name_right);
    	String newClzNameSelf = old_tmp_name_left + new_name_right + ";";
    	
    	return newClzNameSelf;
	}
	
	public static String innerClzNameRegexPatten = "(\\s+)name(\\s+)=(\\s+)([0-9|a-z|A-Z|$|_|-|\"]+)";
	
	/**
	 * .annotation system Ldalvik/annotation/InnerClass;
	 *     accessFlags = 0x8
	 *     name = null
	 * .end annotation
	 * */
	public static String RenameInnerClassName(String content, String name){
		int startIndex = content.indexOf(".annotation system Ldalvik/annotation/InnerClass;");
		if(startIndex<0){
			return content;
		}
		
		
		int endIndex = content.substring(startIndex).indexOf(".end annotation") + startIndex;
		if(endIndex<0){
			return content;
		}
		
		String content_mid = content.substring(startIndex, endIndex);
		
		Pattern p = Pattern.compile(innerClzNameRegexPatten);
		
    	Matcher match = p.matcher(content_mid);
		while(match.find()){
			int startIndex2 = match.start();
			int endIndex2 = match.end();
			String foundStr = content_mid.substring(startIndex2, endIndex2+1);
//			System.out.println("foundStr:" + foundStr);
			String contentLeft = content.substring(0, startIndex);
			String content_mid_left = content_mid.substring(0, startIndex2);
			String content_mid_right = content_mid.substring(endIndex2);
			String contentRight = content.substring(endIndex);
			return  contentLeft + 
					content_mid_left + 
					"\r\n    name = \"" + name + "\"" + 
					content_mid_right + 
					contentRight;
		}
		
		return content;
		

	}
	
	public static String getMD5OfStr(String inStr){
		MessageDigest md5 = null;  
        try{  
            md5 = MessageDigest.getInstance("MD5");  
        }catch (Exception e){  
            System.out.println(e.toString());  
            e.printStackTrace();  
            return "";  
        }  
        char[] charArray = inStr.toCharArray();  
        byte[] byteArray = new byte[charArray.length];  
  
        for (int i = 0; i < charArray.length; i++)  
            byteArray[i] = (byte) charArray[i];  
        byte[] md5Bytes = md5.digest(byteArray);  
        StringBuffer hexValue = new StringBuffer();  
        for (int i = 0; i < md5Bytes.length; i++){  
            int val = ((int) md5Bytes[i]) & 0xff;  
            if (val < 16)  
                hexValue.append("0");  
            hexValue.append(Integer.toHexString(val));  
        }  
        return hexValue.toString();
	}
	
	public static void main(String[] args) throws IOException{
		String key = "Lcom/tencent/mm/d/a/cx$a;";
		String regStr = "Lcom/tencent/mm/d/a/cx\\$";
		String replcaseStr = "Lcom/tencent/mm/d/a/cx_b\\$";
//		String out = key.replaceAll(regStr, replcaseStr);
		
//		System.out.println("out - " + getNewClassNameSelf("Lcom/tencent/mm/ui/transmit/MsgRetransmitUI$8$1;"));
		
//		System.out.println("out -" + getOutClzNameSef("Lcom/tencent/mm/ui/transmit/MsgRetransmitUI;"));
		
//		System.out.println("out - " + getBasicClzNameStr("Lcom/tencent/mm/ui/transmit/MsgRetransmitUI$8$1;"));
		
		File file  = new File("D:\\test\\LauncherUI$InnerB$Inner1.smali");
		
		String content = FileUtils.readFileToString(file);
		
		///Lcom/tencent/mm/ui/Uclz$Inner1;

		String content2 = RenameInnerClassName(content, "Inner8");
		
		FileUtils.write(file, content2);
	}
	

}
