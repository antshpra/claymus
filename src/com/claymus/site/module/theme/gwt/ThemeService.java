package com.claymus.site.module.theme.gwt;

import com.claymus.gwt.ServerException;
import com.claymus.gwt.UserException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("../_ah/theme/gwtrpc")
public interface ThemeService extends RemoteService {

	void setActive(String className) throws ServerException, UserException;

}