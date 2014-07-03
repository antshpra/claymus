package com.claymus.site.module.administer.gwt;

import com.claymus.gwt.ServerException;
import com.claymus.gwt.UserException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("../_ah/administer/gwtrpc")
public interface AdministerService extends RemoteService {

	void setModuleAccessLevel(String moduleId, String encoded, int accessLevel) throws ServerException, UserException;

}