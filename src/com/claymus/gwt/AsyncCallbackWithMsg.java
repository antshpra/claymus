package com.claymus.gwt;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public abstract class AsyncCallbackWithMsg<T> implements AsyncCallback<T> {

	private Panel asyncLoadMsg;

	public AsyncCallbackWithMsg() {
		Label label = new Label("Loading...");
		label.setStyleName("claymus-h3");

		this.asyncLoadMsg = new SimplePanel();
		this.asyncLoadMsg.add(label);

		this.asyncLoadMsg.setStyleName("claymus-gwt-AsyncLoadMsg");

		RootPanel.get().add(this.asyncLoadMsg);
	}


	@Override
	public final void onSuccess(T result) {
		this.asyncLoadMsg.removeFromParent();
		onCallSuccess(result);
	}

	@Override
	public final void onFailure(Throwable caught) {
		this.asyncLoadMsg.removeFromParent();
		onCallFailure(caught);
	}


	public abstract void onCallSuccess(T result);

	public abstract void onCallFailure(Throwable caught);

}
