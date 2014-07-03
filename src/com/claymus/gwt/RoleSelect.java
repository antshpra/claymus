package com.claymus.gwt;

import java.util.LinkedList;

import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.ListBox;

public class RoleSelect extends Composite implements Focusable, HasEnabled  {

	public static final int ALL_ROLES = 0;
	public static final int SELECTED_ROLES_ONLY = 1;
	public static final int ALL_EXCEPT_SELECTED_ROLES = -1;

	private ListBox listBox;
	private FlowPanel rolesPanel;
	private LinkedList<CheckBox> roleCheckBoxs;

	/*
	 * Constructors
	 */

	public RoleSelect() {
		FlowPanel panel = new FlowPanel();

		this.listBox = new ListBox();
		this.listBox.addItem("All Roles");
		this.listBox.addItem("Selected Roles Only");
		this.listBox.addItem("All Except Selected Roles");

		this.listBox.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				int select = RoleSelect.this.getSelection();
				if(select == RoleSelect.SELECTED_ROLES_ONLY || select == RoleSelect.ALL_EXCEPT_SELECTED_ROLES)
					RoleSelect.this.rolesPanel.setVisible(true);
				else
					RoleSelect.this.rolesPanel.setVisible(false);
			}

		});

		this.rolesPanel = new FlowPanel();
		this.roleCheckBoxs = new LinkedList<CheckBox>();

		panel.add(this.listBox);
		panel.add(this.rolesPanel);

		initWidget(panel);

		setStyleName("claymus-gwt-RoleSelect");
	}

	/*
	 * Helper Methods
	 */

	public int getSelection() {
		switch(this.listBox.getSelectedIndex()) {
			case  0: return RoleSelect.ALL_ROLES;
			case  1: return RoleSelect.SELECTED_ROLES_ONLY;
			case  2: return RoleSelect.ALL_EXCEPT_SELECTED_ROLES;
			default: return ALL_ROLES;
		}
	}

	public void setSelection(int value) {
		switch(value) {
			case RoleSelect.ALL_ROLES:
					this.listBox.setSelectedIndex(0);
					break;
			case RoleSelect.SELECTED_ROLES_ONLY:
					this.listBox.setSelectedIndex(1);
					break;
			case RoleSelect.ALL_EXCEPT_SELECTED_ROLES:
					this.listBox.setSelectedIndex(2);
					break;
			default:
					this.listBox.setSelectedIndex(0);
					break;
		}

		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), this.listBox);
	}

	public void addRole(String name, String encoded) {
		addRole(name, encoded, false);
	}

	public void addRole(String name, String encoded, boolean selected) {
		CheckBox check = new CheckBox(name);
		check.setFormValue(encoded);
		check.setValue(selected);
		this.roleCheckBoxs.add(check);
		this.rolesPanel.add(check);
	}

	public void removeRole(int index) {
		this.rolesPanel.remove(this.roleCheckBoxs.remove(index));
	}

	public int getRoleCount() {
		return this.roleCheckBoxs.size();
	}

	public String getRoleName(int index) {
		return this.roleCheckBoxs.get(index).getText();
	}

	public String getRoleEncoded(int index) {
		return this.roleCheckBoxs.get(index).getFormValue();
	}

	public boolean isRoleSelected(int index) {
		return this.roleCheckBoxs.get(index).getValue();
	}

	public LinkedList<String> getSelectedRoles() {
		LinkedList<String> roleEncodeds = new LinkedList<String>();
		for(int i = 0; i < this.roleCheckBoxs.size(); i++)
			if(this.roleCheckBoxs.get(i).getValue())
				roleEncodeds.add(this.roleCheckBoxs.get(i).getFormValue());
		return roleEncodeds;
	}

	public void setSelectedRoles(LinkedList<String> roleEncodeds) {
		for(int i = 0; i < this.roleCheckBoxs.size(); i++) {
			CheckBox check = this.roleCheckBoxs.get(i);
			check.setValue(roleEncodeds.contains(check.getFormValue()));
		}
	}

	/*
	 * Inherited Methods
	 */

	@Override
	public void setFocus(boolean focused) {
		this.listBox.setFocus(focused);
	}

	@Override
	public int getTabIndex() {
		return this.listBox.getTabIndex();
	}

	@Override
	public void setTabIndex(int index) {
		this.listBox.setTabIndex(index);
	}

	@Override
	public void setAccessKey(char key) {
		this.listBox.setAccessKey(key);
	}

	@Override
	public boolean isEnabled() {
		return this.listBox.isEnabled();
	}

	@Override
	public void setEnabled(boolean enabled) {
		this.listBox.setEnabled(enabled);

		for(CheckBox check : this.roleCheckBoxs)
			check.setEnabled(enabled);
	}

}
