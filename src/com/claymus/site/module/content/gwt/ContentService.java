package com.claymus.site.module.content.gwt;


import java.util.LinkedList;

import com.claymus.gwt.ServerException;
import com.claymus.gwt.UserException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("../_ah/content/gwtrpc")
public interface ContentService extends RemoteService {

	String[][] getLocations(String page);

	String[][] getRoles();


	ContentDTO get(String pageEncoded, String encoded) throws ServerException, UserException;

	void add(String pageEncoded, ContentDTO contentDTO) throws ServerException, UserException;

	void update(String encoded, ContentDTO contentDTO) throws ServerException, UserException;


	void saveOrder(String pageEncoded, LinkedList<String> locations, LinkedList<String> encodedList) throws ServerException, UserException;

}