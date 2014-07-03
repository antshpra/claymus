package com.claymus.site.module.administer.gwt;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AdministerServiceAsync {

	void setModuleAccessLevel(String moduleId, String encoded, int accessLevel, AsyncCallback<Void> callback);

}