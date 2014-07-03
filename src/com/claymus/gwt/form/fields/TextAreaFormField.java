package com.claymus.gwt.form.fields;

import com.claymus.gwt.form.FormField;
import com.google.gwt.user.client.ui.TextArea;

public class TextAreaFormField extends FormField<String> {

	/*
	 * Constructors
	 */

	public TextAreaFormField(String label, boolean required, String helpText, String... regex) {
		super(label, new TextArea(), required, helpText, regex);
		addStyleName("claymus-gwt-FormField-TextArea");
	}

	/*
	 * Helper Methods
	 */

	public String getText() {
		String text = ((TextArea) this.widget).getText().trim();
		return text.length() == 0 ? null : text;
	}

	public void setText(String text) {
		((TextArea) this.widget).setText(text == null ? "" : text);
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
