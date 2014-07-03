package com.claymus.site.module.page.gwt;

import com.claymus.gwt.ServerException;
import com.claymus.gwt.UserException;
import com.claymus.site.module.page.pages.gwt.PageDTO;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("../_ah/page/gwtrpc")
public interface PageService extends RemoteService {

	String[][] getLayouts();


	PageDTO get(String encoded) throws UserException;


	String add(PageDTO pageDTO) throws ServerException, UserException;

	void update(PageDTO pageDTO) throws ServerException, UserException;

}