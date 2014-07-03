package com.claymus.site.module.content.gwt;

import com.claymus.gwt.Alert;
import com.claymus.gwt.AsyncCallbackWithMsg;
import com.claymus.gwt.form.Form;
import com.claymus.gwt.form.FormField;
import com.claymus.gwt.form.fields.ListBoxFormField;
import com.claymus.gwt.form.fields.RoleSelectFormField;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

public abstract class ContentEditor implements EntryPoint {

	private ContentServiceAsync contentService = GWT.create(ContentService.class);

	private RootPanel rootPanel = RootPanel.get("claymus-ContentEditor");

	private Form form;

	private ListBoxFormField location;
	private RoleSelectFormField visibleTo;


	@Override
	public final void onModuleLoad(){
		this.form = new Form();

		this.location = new ListBoxFormField("Location", true, null);
		this.visibleTo = new RoleSelectFormField("Visible To", true, null);

		for(FormField<?> field : getFields())
			this.form.addField(field);

		this.form.addField(this.location);
		this.form.addField(this.visibleTo);

		this.rootPanel.add(this.form);

		if(Window.Location.getPath().equals("/_ah/content/new"))
			add(Window.Location.getParameter("page"));
		else if(Window.Location.getPath().equals("/_ah/content/edit"))
			update(Window.Location.getParameter("page"), Window.Location.getParameter("key"));
	}

	/*
	 * Methods to be implemented by inheriting class
	 */

	public abstract FormField<?>[] getFields();

	public abstract ContentDTO getDTO();

	public abstract void putDTO(ContentDTO moduleDTO);

	/*
	 * Helper Methods
	 */

	private void add(final String pageEncoded){
		this.form.addResetButton();

		this.form.addButton("Add Content", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				ContentEditor.this.form.setEnabled(false);

				if(! ContentEditor.this.form.validate()) {
					ContentEditor.this.form.setEnabled(true);
					return;
				}

				ContentDTO contentDTO = ContentEditor.this.getDTO();
				contentDTO.setLocation(ContentEditor.this.location.getValue());
				contentDTO.setVisibleTo(ContentEditor.this.visibleTo.getSelection());
				contentDTO.setRoleList(ContentEditor.this.visibleTo.getSelectedRoles());
				ContentEditor.this.contentService.add(pageEncoded, contentDTO, new AsyncCallbackWithMsg<Void>(){

					@Override
					public void onCallSuccess(Void result) {
						Alert alert = new Alert("Success !", "Content Created Successfully !");
						alert.addButton("Add Another Content", new ClickHandler() {

							@Override
							public void onClick(ClickEvent event) {
								Window.Location.assign("/_ah/content/add?page=" + pageEncoded);
							}

						});
						alert.addButton("Done", new ClickHandler() {

							@Override
							public void onClick(ClickEvent event) {
								Window.Location.assign("/_ah/content?page=" + pageEncoded);
							}

						});
						alert.show();
					};

					@Override
					public void onCallFailure(Throwable caught) {
						Alert alert = new Alert(caught);
						alert.addHideButton();
						alert.show();
						ContentEditor.this.form.setEnabled(true);
					}

				});
			}

		});

		this.contentService.getLocations(pageEncoded, new AsyncCallback<String[][]>() {

			@Override
			public void onSuccess(String[][] locations) {
				for(String[] loc : locations)
					ContentEditor.this.location.addItem(loc[0], loc[1]);
				ContentEditor.this.location.setStateAsDefault();
			}

			@Override
			public void onFailure(Throwable caught) {
				Alert alert = new Alert(caught);
				alert.addButton("Reload Page", new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						Window.Location.reload();
					}

				});
				alert.addHideButton();
				alert.show();
			}

		});

		this.contentService.getRoles(new AsyncCallbackWithMsg<String[][]>() {

			@Override
			public void onCallSuccess(String[][] roleList) {
				for(String[] role : roleList)
					ContentEditor.this.visibleTo.addRole(role[1], role[0]);
				ContentEditor.this.visibleTo.setSelection(0);
				ContentEditor.this.visibleTo.setStateAsDefault();
			}

			@Override
			public void onCallFailure(Throwable caught) {
				Alert alert = new Alert(caught);
				alert.addButton("Reload Page", new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						Window.Location.reload();
					}

				});
				alert.addHideButton();
				alert.show();
			}

		});
	}

	private void update(final String pageEncoded, final String encoded) {
		this.form.addResetButton();

		this.form.addButton("Update Content", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				ContentEditor.this.form.setEnabled(false);

				if(! ContentEditor.this.form.validate()) {
					ContentEditor.this.form.setEnabled(true);
					return;
				}

				ContentDTO contentDTO = ContentEditor.this.getDTO();
				contentDTO.setLocation(ContentEditor.this.location.getValue());
				contentDTO.setVisibleTo(ContentEditor.this.visibleTo.getSelection());
				contentDTO.setRoleList(ContentEditor.this.visibleTo.getSelectedRoles());
				ContentEditor.this.contentService.update(encoded, contentDTO, new AsyncCallbackWithMsg<Void>(){

					@Override
					public void onCallSuccess(Void result) {
						Alert alert = new Alert("Success !", "Content Updated Successfully !");
						alert.addButton("Done", new ClickHandler() {

							@Override
							public void onClick(ClickEvent event) {
								Window.Location.assign("/_ah/content?page=" + pageEncoded);
							}

						});
						alert.show();
					};

					@Override
					public void onCallFailure(Throwable caught) {
						Alert alert = new Alert(caught);
						alert.addHideButton();
						alert.show();
						ContentEditor.this.form.setEnabled(true);
					}

				});
			}
		});

		this.contentService.get(pageEncoded, encoded, new AsyncCallbackWithMsg<ContentDTO>(){

			@Override
			public final void onCallSuccess(ContentDTO moduleDTO) {
				ContentEditor.this.putDTO(moduleDTO);
				for(String[] loc : moduleDTO.getLocations())
					ContentEditor.this.location.addItem(loc[0], loc[1]);
				ContentEditor.this.location.setValue(moduleDTO.getLocation());
				for(String[] role : moduleDTO.getRoles())
					ContentEditor.this.visibleTo.addRole(role[1], role[0]);
				ContentEditor.this.visibleTo.setSelection(moduleDTO.getVisibleTo());
				ContentEditor.this.visibleTo.setSelectedRoles(moduleDTO.getRoleList());
				ContentEditor.this.form.setStateAsDefault();
			}

			@Override
			public void onCallFailure(Throwable caught)  {
				Alert alert = new Alert(caught);
				alert.addButton("Reload Page", new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						Window.Location.reload();
					}

				});
				alert.addHideButton();
				alert.show();
			}

		});
	}

}
