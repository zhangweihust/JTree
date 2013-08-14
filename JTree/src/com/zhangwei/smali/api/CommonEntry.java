package com.zhangwei.smali.api;

public abstract class CommonEntry {
	public String content;
	
	//.class public final Lcom/b/f/a;
	//.super Ljava/lang/Object;
	//.method static constructor <clinit>()V
	//.method public static final b(Lcom/b/f/a;)Lcom/b/f/a;	
	//.field public static final c:[I
	public void Rename(String content_before, String content_after){
		content = content.replace(content_before, content_after);
	}
}
