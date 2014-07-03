package com.claymus.gwt;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.StatusCodeException;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;

public class Alert {

	private DialogBox dialogBox;
	private Panel dialogButtons;

	/*
	 * Constructors
	 */

	public Alert(Throwable caught) {
		String title;
		String html;

		if(caught instanceof StatusCodeException) {
			StatusCodeException ex = (StatusCodeException) caught;
			switch(ex.getStatusCode()) {
				case 0:
					title = "Connection Error !";
					html = "An error occurred while attempting to contact the server. Please check your network connection and try again.";
					break;

				case 500:
					title = "Server Error !";
					html = "An error occured at server. Please try again. If problem persists, contact administrator.";
					break;

				default:
					title = "Error " + ex.getStatusCode() + " !";
					html = ex.getEncodedResponse();
			}

		} else if(caught instanceof ServerException) {
			title = "Server Error !";
			html = caught.getMessage();
		} else if(caught instanceof UserException) {
			title = "User Error !";
			html = caught.getMessage();
		} else {
			title = caught.getClass().getName();
			html = caught.getMessage();
		}

		this.dialogBox = createDialog(title, html);
	}

	public Alert(String html) {
		this.dialogBox = createDialog("Error !", html);
	}

	public Alert(String title, String html) {
		this.dialogBox = createDialog(title, html);
	}

	/*
	 * Helper Methods
	 */

	private DialogBox createDialog(String title, String html) {
		DialogBox dialogBox = new DialogBox();
		dialogBox.setGlassEnabled(true);
		dialogBox.setAnimationEnabled(true);
		dialogBox.addStyleName("claymus-gwt-Alert");

		dialogBox.setText(title);

		FlowPanel panel = new FlowPanel();
		panel.addStyleName("claymus-t2");
		dialogBox.setWidget(panel);

		HTML message = new HTML(html);
//		message.addStyleName("claymus-t2");
		panel.add(message);

		FlowPanel toolbar = new FlowPanel();
		toolbar.addStyleName("claymus-toolbar");
		panel.add(toolbar);

		this.dialogButtons = toolbar;

		return dialogBox;
	}

	public void addHideButton() {
		addHideButton("Close");
	}

	public void addHideButton(String label) {
		Button button = new Button(label, new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Alert.this.dialogBox.hide();
			}

		});
		this.dialogButtons.add(button);
	}

	public void addButton(String label, ClickHandler clickHandler) {
		addButton(label, clickHandler, false);
	}

	public void addButton(String label, ClickHandler clickHandler, boolean hideDialogOnClick) {
		Button button = new Button(label, clickHandler);
		if(hideDialogOnClick)
			button.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					Alert.this.dialogBox.hide();
				}

			});
		this.dialogButtons.add(button);
	}

	public void show() {
		this.dialogBox.show();
		this.dialogBox.center();
	}

}
