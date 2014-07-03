package com.claymus.site.module.content.gwt;

import java.util.LinkedList;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ContentServiceAsync {

	void getLocations(String page, AsyncCallback<String[][]> callback);

	void getRoles(AsyncCallback<String[][]> callback);


	void get(String pageEncoded, String encoded, AsyncCallback<ContentDTO> callback);

	void add(String pageEncoded, ContentDTO contentDTO, AsyncCallback<Void> callback);

	void update(String encoded, ContentDTO contentDTO, AsyncCallback<Void> callback);


	void saveOrder(String pageEncoded, LinkedList<String> locations, LinkedList<String> encodedList, AsyncCallback<Void> callback);

}