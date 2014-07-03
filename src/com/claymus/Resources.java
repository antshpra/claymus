package com.claymus;

import javax.servlet.http.HttpServlet;

@SuppressWarnings("serial")
public class Resources extends HttpServlet {

	public static final String getCKEditor() { // version 3.6.1
		return "<script type='text/javascript' src='/com.claymus/ckeditor/ckeditor.js'></script>";
	}

	public static final String getJQuery(String lib) {
		return "<script type='text/javascript' src='/com.claymus/jquery/" + lib + "'></script>";
	}

}