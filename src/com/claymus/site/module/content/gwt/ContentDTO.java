package com.claymus.site.module.content.gwt;

import java.util.LinkedList;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ContentDTO implements IsSerializable {

	private String[][] locations;

	private String[][] roles;


	private String location;

	private int visibleTo;

	private LinkedList<String> roleList;

	/*
	 * Getter and Setter methods
	 */

	public String[][] getRoles() {
		return this.roles;
	}

	public void setRoles(String[][] roles) {
		this.roles = roles;
	}


	public String[][] getLocations() {
		return this.locations;
	}

	public void setLocations(String[][] locations) {
		this.locations = locations;
	}


	public String getLocation() {
		return this.location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public int getVisibleTo() {
		return this.visibleTo;
	}

	public void setVisibleTo(int visibleTo) {
		this.visibleTo = visibleTo;
	}

	public LinkedList<String> getRoleList() {
		return this.roleList;
	}

	public void setRoleList(LinkedList<String> roleList) {
		this.roleList = roleList;
	}

}
