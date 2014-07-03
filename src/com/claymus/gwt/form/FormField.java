package com.claymus.gwt.form;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public abstract class FormField<T> extends Composite {

	public static final String PUBLIC_URI_REGEX = "[/]([a-zA-Z0-9]+([-][a-zA-Z0-9]+)*([/][a-zA-Z0-9]+([-][a-zA-Z0-9]+)*)*)?";

	protected Widget widget;
	private boolean required;
	private String[] regex;

	private T data = null;
	private boolean enabled = true;

	/*
	 * Constructors
	 */

	protected FormField(String label, Widget widget, boolean required, String helpText, String... regex) {
		this.widget = widget;
		this.required = required;
		this.regex = regex;

		FlowPanel panel = new FlowPanel();

		if(label != null) {
			Label fieldLabel = new Label(label);
			fieldLabel.addStyleName("claymus-h3");
			panel.add(fieldLabel);
		}

		panel.add(widget);

		if(helpText != null) {
			Label fieldLabel = new Label(helpText);
			fieldLabel.addStyleName("claymus-sub-text");
			fieldLabel.addStyleName("claymus-faded-text");
			panel.add(fieldLabel);
		}

		initWidget(panel);

		setStyleName("claymus-gwt-FormField");
		if(required)
			addStyleName("claymus-required");
	}

	/*
	 * Helper Methods
	 */

	public boolean isEnabled() {
		if(this.widget instanceof HasEnabled)
			return ((HasEnabled) this.widget).isEnabled();
		return true;
	}

	public void setEnabled(boolean enabled) {
		if(this.widget instanceof HasEnabled)
			((HasEnabled) this.widget).setEnabled(enabled);
	}

	public void setFocus(boolean focused) {
		if(this.widget instanceof Focusable)
			((Focusable) this.widget).setFocus(focused);
	}

	public void setStateAsDefault() {
		this.data = getData();
		this.enabled = isEnabled();
	}

	public void reset() {
		setData(this.data);
		setEnabled(this.enabled);
		if(this.required) {
			addStyleName("claymus-required");
			removeStyleName("claymus-valid");
		}
		removeStyleName("claymus-invalid");

	}

	public boolean validate() {
		boolean validated = true;
		T data = getData();

		if(this.required && data == null) {
			removeStyleName("claymus-required");
			removeStyleName("claymus-valid");
			addStyleName("claymus-invalid");
			validated = false;

		} else if(data != null && this.regex != null) {

			if(data.getClass().getName().equals(String.class.getName())) {
				String str = (String) data;
				for(String expr : this.regex)
					validated = str.matches(expr) && validated;
			} else {
				String str = data.toString();
				for(String expr : this.regex)
					validated = str.matches(expr) && validated;
			}

			if(validated) {
				if(this.required) {
					removeStyleName("claymus-required");
					addStyleName("claymus-valid");
				}
				removeStyleName("claymus-invalid");
			} else {
				if(this.required)
					removeStyleName("claymus-required");
				removeStyleName("claymus-valid");
				addStyleName("claymus-invalid");
			}

		}

		return validated;
	}

	/*
	 * Methods to be implemented by inheriting class
	 */

	protected abstract T getData();

	protected abstract void setData(T data);

}
