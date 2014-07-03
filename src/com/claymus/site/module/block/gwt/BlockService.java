package com.claymus.site.module.block.gwt;

import java.util.LinkedList;

import com.claymus.gwt.ServerException;
import com.claymus.gwt.UserException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("../_ah/block/gwtrpc")
public interface BlockService extends RemoteService {

	String[][] getLocations();

	String[][] getRoles();


	BlockDTO get(String encoded) throws UserException;

	void add(BlockDTO blockDTO) throws ServerException, UserException;

	void update(String encoded, BlockDTO blockDTO) throws ServerException, UserException;


	void saveOrder(LinkedList<String> locations, LinkedList<String> encodedList) throws ServerException, UserException;

}