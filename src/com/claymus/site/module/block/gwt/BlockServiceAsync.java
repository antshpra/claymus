package com.claymus.site.module.block.gwt;

import java.util.LinkedList;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface BlockServiceAsync {

	void getLocations(AsyncCallback<String[][]> callback);

	void getRoles(AsyncCallback<String[][]> callback);


	void get(String encoded, AsyncCallback<BlockDTO> callback);

	void add(BlockDTO blockDTO, AsyncCallback<Void> callback);

	void update(String encoded, BlockDTO blockDTO, AsyncCallback<Void> callback);


	void saveOrder(LinkedList<String> locations, LinkedList<String> encodedList, AsyncCallback<Void> callback);

}