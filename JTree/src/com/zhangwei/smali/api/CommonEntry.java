package com.zhangwei.smali.api;

public abstract class CommonEntry {
	public String id;
	
	public int type; //1 head  2 field  3 head
	public String content;
	
	public CommonEntry(int type){
		this.type = type;
	}
	
	//.class public final Lcom/b/f/a;
	//.super Ljava/lang/Object;
	//.method static constructor <clinit>()V
	//.method public static final b(Lcom/b/f/a;)Lcom/b/f/a;	
	//.field public static final c:[I
	public void Rename(String content_before, String content_after){
		content = content.replace(content_before, content_after);
	}
	
	public String toString(){
		return id;
	}
}
