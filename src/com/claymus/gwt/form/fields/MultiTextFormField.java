package com.claymus.gwt.form.fields;

import com.claymus.gwt.MultiText;
import com.claymus.gwt.form.FormField;

public class MultiTextFormField extends FormField<String[]> {

	/*
	 * Constructors
	 */

	public MultiTextFormField(String label, boolean required, String helpText) {
		super(label, new MultiText(), required, helpText);
		addStyleName("claymus-gwt-FormField-MultiText");
	}

	/*
	 * Helper Methods
	 */

	public String[] getTexts() {
		return ((MultiText) this.widget).getTexts();
	}

	public void setTexts(String[] texts) {
		((MultiText) this.widget).setTexts(texts);
	}

	/*
	 * Inherited Methods
	 */

	@Override
	protected String[] getData() {
		return getTexts();
	}

	@Override
	protected void setData(String[] data) {
		setTexts(data);
	}

}
