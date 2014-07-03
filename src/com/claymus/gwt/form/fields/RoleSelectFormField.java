package com.claymus.gwt.form.fields;

import java.util.LinkedList;

import com.claymus.gwt.RoleSelect;
import com.claymus.gwt.form.FormField;

public class RoleSelectFormField extends FormField<Integer> {

	private Object[][] roleData = new Object[0][0];

	/*
	 * Constructors
	 */

	public RoleSelectFormField(String label, boolean required, String helpText) {
		super(label, new RoleSelect(), required, helpText);
		addStyleName("claymus-gwt-FormField-RoleSelect");
	}

	/*
	 * Helper Methods
	 */

	public int getSelection() {
		return ((RoleSelect) this.widget).getSelection();
	}

	public void setSelection(int value) {
		((RoleSelect) this.widget).setSelection(value);
	}

	public void addRole(String name, String encoded) {
		addRole(name, encoded, false);
	}

	public void addRole(String name, String encoded, boolean selected) {
		((RoleSelect) this.widget).addRole(name, encoded, selected);
	}

	public LinkedList<String> getSelectedRoles() {
		return ((RoleSelect) this.widget).getSelectedRoles();
	}

	public void setSelectedRoles(LinkedList<String> encodeds) {
		((RoleSelect) this.widget).setSelectedRoles(encodeds);
	}

	/*
	 * Inherited Methods
	 */

	@Override
	public void setStateAsDefault() {
		RoleSelect roleSelect = (RoleSelect) this.widget;
		this.roleData = new Object[roleSelect.getRoleCount()][3];
		for(int i = 0; i < roleSelect.getRoleCount(); i++) {
			this.roleData[i][0] = roleSelect.getRoleName(i);
			this.roleData[i][1] = roleSelect.getRoleEncoded(i);
			this.roleData[i][2] = roleSelect.isRoleSelected(i);
		}
		super.setStateAsDefault();
	}

	@Override
	public void reset() {
		while(((RoleSelect) this.widget).getRoleCount() > 0)
			((RoleSelect) this.widget).removeRole(0);

		for(int i = 0; i < this.roleData.length; i++)
			addRole((String) this.roleData[i][0], (String) this.roleData[i][1], (Boolean) this.roleData[i][2]);

		super.reset();
	}

	@Override
	protected Integer getData() {
		return getSelection();
	}

	@Override
	protected void setData(Integer data) {
		setSelection(data);
	}

}
