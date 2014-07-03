package com.claymus.gwt.form.fields;

import com.claymus.gwt.RichTextArea;
import com.claymus.gwt.form.FormField;

public class RichTextAreaFormField extends FormField<String> {

	/*
	 * Constructors
	 */

	public RichTextAreaFormField(String label, boolean required, String helpText, String... regex) {
		super(label, new RichTextArea(), required, helpText, regex);
		addStyleName("claymus-gwt-FormField-RichTextArea");
	}

	/*
	 * Helper Methods
	 */

	public String getHTML() {
		String html = ((RichTextArea) this.widget).getHTML().trim();
		return html.length() == 0 ? null : html;
	}

	public void setHTML(String html) {
		((RichTextArea) this.widget).setHTML(html == null ? "" : html);
	}

	/*
	 * Inherited Methods
	 */

	@Override
	protected String getData() {
		return getHTML();
	}

	@Override
	protected void setData(String data) {
		setHTML(data);
	}

}
