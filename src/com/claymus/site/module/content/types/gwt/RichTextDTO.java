package com.claymus.site.module.content.types.gwt;

import com.claymus.site.module.content.gwt.ContentDTO;

public class RichTextDTO extends ContentDTO {

	private String title;
	
	private String content;
	
	public String getTitle() {
		return this.title;
	}
	
	public void setTitle(String title){
		this.title = title;
	}
	
	public String getContent() {
		return this.content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}

}