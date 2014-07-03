package com.claymus.site.module.block.types.gwt;

import com.claymus.site.module.block.gwt.BlockDTO;

public class HTMLJavaScriptDTO extends BlockDTO {

	private String title;

	private String content;

	/*
	 * Getter and Setter Methods
	 */

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