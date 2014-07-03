package com.claymus.site.module.page.gwt;

import com.claymus.site.module.page.pages.gwt.PageDTO;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface PageServiceAsync {

	void getLayouts(AsyncCallback<String[][]> callback);


	void get(String encoded, AsyncCallback<PageDTO> callback);


	void add(PageDTO pageDTO, AsyncCallback<String> callback);

	void update(PageDTO pageDTO, AsyncCallback<Void> callback);

}