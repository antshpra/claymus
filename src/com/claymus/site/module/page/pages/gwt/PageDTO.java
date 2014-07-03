package com.claymus.site.module.page.pages.gwt;

import com.google.gwt.user.client.rpc.IsSerializable;

public class PageDTO implements IsSerializable {

	private String[][] layouts;


	private String uri;

	private String title;

	private String layout;

	/*
	 * Getter and Setter methods
	 */

	public String[][] getLayouts() {
		return this.layouts;
	}

	public void setLayouts(String[][] layouts) {
		this.layouts = layouts;
	}


	public String getUri() {
		return this.uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}


	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}


	public String getLayout() {
		return this.layout;
	}

	public void setLayout(String layout) {
		this.layout = layout;
	}

}
