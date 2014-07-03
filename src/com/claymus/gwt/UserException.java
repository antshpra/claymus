package com.claymus.gwt;

import com.google.gwt.user.client.rpc.IsSerializable;

@SuppressWarnings("serial")
public class UserException extends Exception implements IsSerializable {

	public UserException() {
		super("You do not have permission to perform this operation. Login if you are not logged or contact administrator.");
	}

	public UserException(String msg) {
		super(msg);
	}

}
