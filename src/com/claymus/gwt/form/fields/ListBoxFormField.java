package com.claymus.gwt.form.fields;

import com.claymus.gwt.form.FormField;
import com.google.gwt.user.client.ui.ListBox;

public class ListBoxFormField extends FormField<String> {

	private String[][] listItems = new String[0][0];

	/*
	 * Constructors
	 */

	public ListBoxFormField(String label, boolean required, String helpText) {
		super(label, new ListBox(), required, helpText);
		addStyleName("claymus-gwt-FormField-ListBox");
	}

	/*
	 * Helper Methods
	 */

	public String getValue() {
		int selectedIndex = ((ListBox) this.widget).getSelectedIndex();
		if(selectedIndex < 0)
			return null;
		String value = ((ListBox) this.widget).getValue(selectedIndex);
		return value.length() == 0 ? null : value;
	}

	public void addItem(String item) {
		((ListBox) this.widget).addItem(item);
	}

	public void addItem(String item, String value) {
		((ListBox) this.widget).addItem(item, value);
	}

	public void setValue(String value) {
		ListBox listBox = (ListBox) this.widget;
		for(int i = 0; i < listBox.getItemCount(); i++) {
			if(listBox.getValue(i).equals(value)) {
				listBox.setSelectedIndex(i);
				return;
			}
		}
	}

	/*
	 * Inherited Methods
	 */

	@Override
	public void setStateAsDefault() {
		ListBox listBox = (ListBox) this.widget;
		this.listItems = new String[listBox.getItemCount()][2];
		for(int i = 0; i < listBox.getItemCount(); i++) {
			this.listItems[i][0] = listBox.getItemText(i);
			this.listItems[i][1] = listBox.getValue(i);
		}
		super.setStateAsDefault();
	}

	@Override
	public void reset() {
		while(((ListBox) this.widget).getItemCount() > 0)
			((ListBox) this.widget).removeItem(0);

		for(int i = 0; i < this.listItems.length; i++)
			addItem(this.listItems[i][0], this.listItems[i][1]);

		super.reset();
	}

	@Override
	protected String getData() {
		return getValue();
	}

	@Override
	protected void setData(String data) {
		setValue(data);
	}

}
