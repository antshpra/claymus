package com.claymus;

import javax.servlet.http.HttpServlet;

public class HostMapping {

	public enum Type {HOME, FORWARD}

	private Type type;
	private String fwdURL;
	private String namespace;
	private HttpServlet servlet;

	/*
	 * Constructor(s)
	 */

	public HostMapping(String fwdURL) {
		this.type = Type.FORWARD;
		this.fwdURL = fwdURL;
	}

	public HostMapping(String namespace, HttpServlet servlet) {
		this.type = Type.HOME;
		this.namespace = namespace;
		this.servlet = servlet;
	}

	/*
	 * Getters
	 */

	public Type getType() {
		return this.type;
	}

	public String getFwdURL() {
		return this.fwdURL;
	}

	public String getNamespace() {
		return this.namespace;
	}

	public HttpServlet getServlet() {
		return this.servlet;
	}

}
