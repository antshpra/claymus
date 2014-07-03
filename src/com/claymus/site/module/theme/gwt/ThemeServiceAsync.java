package com.claymus.site.module.theme.gwt;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ThemeServiceAsync {

	void setActive(String className, AsyncCallback<Void> callback);

}