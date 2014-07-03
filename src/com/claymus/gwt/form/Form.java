package com.claymus.gwt.form;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class Form extends Composite {

	private FlowPanel fields = new FlowPanel();
	private FlowPanel buttons = new FlowPanel();

	private boolean[] fields_enabled;
	private boolean[] buttons_enabled;

	private boolean[] buttons_default = new boolean[0];

	/*
	 * Constructors
	 */

	public Form() {
		this(null);
	}

	public Form(String title) {
		FlowPanel panel = new FlowPanel();

		if(title != null) {
			Label formTitle = new Label(title);
			formTitle.addStyleName("claymus-h2");
			panel.add(formTitle);
		}

		panel.add(this.fields);

		this.buttons.addStyleName("claymus-toolbar");
		panel.add(this.buttons);

		initWidget(panel);

		setStyleName("claymus-gwt-Form");
	}

	/*
	 * Helper Methods
	 */

	public void addField(FormField<?> formField) {
		this.fields.add(formField);
	}

	public void addButton(String label, ClickHandler clickHandler) {
		this.buttons.add(new Button(label, clickHandler));
	}

	public void addResetButton() {
		addResetButton("Reset");
	}

	public void addResetButton(String label) {
		this.buttons.add(new Button(label, new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				reset();
			}

		}));
	}

	public void setEnabled(boolean enabled) {
		if(this.fields_enabled == null)
			this.fields_enabled = new boolean[this.fields.getWidgetCount()];

		if(this.buttons_enabled == null)
			this.buttons_enabled = new boolean[this.buttons.getWidgetCount()];

		if(enabled) {
			for(int i = 0; i < this.fields.getWidgetCount(); i++)
				if(this.fields_enabled[i])
					((FormField<?>) this.fields.getWidget(i)).setEnabled(true);

			for(int i = 0; i < this.buttons.getWidgetCount(); i++)
				if(this.buttons_enabled[i])
					((Button) this.buttons.getWidget(i)).setEnabled(true);

		} else {
			for(int i = 0; i < this.fields.getWidgetCount(); i++) {
				this.fields_enabled[i] = ((FormField<?>) this.fields.getWidget(i)).isEnabled();
				((FormField<?>) this.fields.getWidget(i)).setEnabled(false);
			}

			for(int i = 0; i < this.buttons.getWidgetCount(); i++) {
				this.buttons_enabled[i] = ((Button) this.buttons.getWidget(i)).isEnabled();
				((Button) this.buttons.getWidget(i)).setEnabled(false);
			}
		}
	}

	public void setStateAsDefault() {
		for(int i = 0; i < this.fields.getWidgetCount(); i++)
			((FormField<?>) this.fields.getWidget(i)).setStateAsDefault();

		this.buttons_default = new boolean[this.buttons.getWidgetCount()];
		for(int i = 0; i < this.buttons.getWidgetCount(); i++)
			this.buttons_default[i] = ((Button) this.buttons.getWidget(i)).isEnabled();
	}

	public void reset() {
		for(int i = 0; i < this.fields.getWidgetCount(); i++)
			((FormField<?>) this.fields.getWidget(i)).reset();

		for(int i = 0; i < this.buttons_default.length; i++)
			((Button) this.buttons.getWidget(i)).setEnabled(this.buttons_default[i]);
	}

	public boolean validate() {
		boolean validated = true;
		for(int i = 0; i < this.fields.getWidgetCount(); i++)
			validated = ((FormField<?>) this.fields.getWidget(i)).validate() && validated;
		return validated;
	}

}