package com.claymus.gwt.form.fields;

import com.claymus.gwt.form.FormField;
import com.google.gwt.user.client.ui.TextBox;

public class TextBoxFormField extends FormField<String> {

	/*
	 * Constructors
	 */
	
	public TextBoxFormField(String label, boolean required, String helpText, String... regex) {
		super(label, new TextBox(), required, helpText, regex);
		addStyleName("claymus-gwt-FormField-TextBox");
	}
	
	/*
	 * Helper Methods
	 */
	
	public String getText() {
		String text = ((TextBox) this.widget).getText().trim();
		return text.length() == 0 ? null : text;
	}

	public void setText(String text) {
		((TextBox) this.widget).setText(text == null ? "" : text);
	}
	
	/*
	 * Inherited Methods
	 */
	
	@Override
	protected String getData() {
		return getText();
	}
	
	@Override
	protected void setData(String data) {
		setText(data);
	}

}
