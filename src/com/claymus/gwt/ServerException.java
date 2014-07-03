package com.claymus.gwt;

import com.google.gwt.user.client.rpc.IsSerializable;

@SuppressWarnings("serial")
public class ServerException extends Exception implements IsSerializable {

	public ServerException() {
		super("An error occured at server. Please try again. If problem persists, contact administrator.");
	}

	public ServerException(String msg) {
		super(msg);
	}

}
