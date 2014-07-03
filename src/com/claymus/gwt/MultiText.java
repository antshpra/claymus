package com.claymus.gwt;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.TextArea;

public class MultiText extends Composite implements Focusable, HasEnabled {

	private TextArea textarea;

	/*
	 * Constructors
	 */

	public MultiText() {
		this.textarea = new TextArea();

		initWidget(this.textarea);

		setStyleName("claymus-gwt-MultiText");
	}

	/*
	 * Helper Methods
	 */

	public String[] getTexts() {
		String text = this.textarea.getText().trim();
		return text.length() == 0 ? new String[0] : text.split("\r\n|\r|\n");
	}

	public void addText(String text) {
		this.textarea.setText((this.textarea.getText() == null ? "" : this.textarea.getText()) + text);
	}

	public void setTexts(String[] texts) {
		String text = "";
		for(String txt : texts)
			text += txt + "\n";
		this.textarea.setText(text);
	}

	/*
	 * Inherited Methods
	 */

	@Override
	public void setFocus(boolean focused) {
		this.textarea.setFocus(focused);
	}

	@Override
	public int getTabIndex() {
		return this.textarea.getTabIndex();
	}

	@Override
	public void setTabIndex(int index) {
		this.textarea.setTabIndex(index);
	}

	@Override
	public void setAccessKey(char key) {
		this.textarea.setAccessKey(key);
	}

	@Override
	public boolean isEnabled() {
		return this.textarea.isEnabled();
	}

	@Override
	public void setEnabled(boolean enabled) {
		this.textarea.setEnabled(enabled);
	}

}
