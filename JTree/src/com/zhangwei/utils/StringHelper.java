package com.zhangwei.utils;

import java.security.MessageDigest;

public class StringHelper {
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
	
	public static boolean needRename(String name){
		String basicName = name.replaceAll(".smali", "");
		if(basicName.contains("$")){
			String[] array = basicName.split("\\$");
			if(array.length==2 && array[1].length()<=2){
				return true;
			}
		}else{
			if (basicName.length()<=2){
				return true;
			}
		}
		
		return false;
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
	
	public static void main(String[] args){
		String key = "Lcom/tencent/mm/d/a/cx$a";
		String regStr = "Lcom/tencent/mm/d/a/cx\\$";
		String replcaseStr = "Lcom/tencent/mm/d/a/cx_b\\$";
		String out = key.replaceAll(regStr, replcaseStr);
		System.out.println("out - " + out);
	}
	

}
