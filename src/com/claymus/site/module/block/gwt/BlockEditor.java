package com.claymus.site.module.block.gwt;

import java.util.Arrays;
import java.util.LinkedList;

import com.claymus.gwt.Alert;
import com.claymus.gwt.AsyncCallbackWithMsg;
import com.claymus.gwt.form.Form;
import com.claymus.gwt.form.FormField;
import com.claymus.gwt.form.fields.ListBoxFormField;
import com.claymus.gwt.form.fields.MultiTextFormField;
import com.claymus.gwt.form.fields.RoleSelectFormField;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

public abstract class BlockEditor implements EntryPoint {

	private BlockServiceAsync blockService = GWT.create(BlockService.class);

	private RootPanel rootPanel = RootPanel.get("claymus-BlockEditor");

	private Form form;

	private ListBoxFormField location;
	private RoleSelectFormField visibleTo;
	private MultiTextFormField visibleAt;
	private MultiTextFormField notVisibleAt;


	@Override
	public final void onModuleLoad() {
		this.form = new Form();

		this.location = new ListBoxFormField("Location", true, null);
		this.visibleTo = new RoleSelectFormField("Visible To", false, null);

		this.visibleAt = new MultiTextFormField("Visible At", false, "Enter one URL per line.");
		this.notVisibleAt = new MultiTextFormField("Not Visible At", false, "Enter one URL per line.");

		for(FormField<?> field : getFields())
			this.form.addField(field);

		this.form.addField(this.location);
		this.form.addField(this.visibleTo);

		this.form.addField(this.visibleAt);
		this.form.addField(this.notVisibleAt);

		this.rootPanel.add(this.form);

		if(Window.Location.getPath().equals("/_ah/block/new"))
			add();
		else if(Window.Location.getPath().equals("/_ah/block/edit"))
			update(Window.Location.getParameter("key"));
	}

	/*
	 * Methods to be implemented by inheriting class
	 */

	public abstract FormField<?>[] getFields();

	public abstract BlockDTO getDTO();

	public abstract void putDTO(BlockDTO moduleDTO);

	/*
	 * Helper Methods
	 */

	private void add() {
		this.form.addResetButton();

		this.form.addButton("Add Block", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				BlockEditor.this.form.setEnabled(false);

				if(! BlockEditor.this.form.validate()) {
					BlockEditor.this.form.setEnabled(true);
					return;
				}

				BlockDTO blockDTO = BlockEditor.this.getDTO();
				blockDTO.setLocation(BlockEditor.this.location.getValue());
				blockDTO.setVisibleTo(BlockEditor.this.visibleTo.getSelection());
				blockDTO.setRoleList(BlockEditor.this.visibleTo.getSelectedRoles());
				blockDTO.setVisibleAt(new LinkedList<String>(Arrays.asList(BlockEditor.this.visibleAt.getTexts())));
				blockDTO.setNotVisibleAt(new LinkedList<String>(Arrays.asList(BlockEditor.this.notVisibleAt.getTexts())));
				BlockEditor.this.blockService.add(blockDTO, new AsyncCallbackWithMsg<Void>(){

					@Override
					public void onCallSuccess(Void result) {
						Alert alert = new Alert("Success !", "Block Created Successfully !");
						alert.addButton("Add Another Block", new ClickHandler() {

							@Override
							public void onClick(ClickEvent event) {
								Window.Location.assign("/_ah/block/add");
							}

						});
						alert.addButton("Done", new ClickHandler() {

							@Override
							public void onClick(ClickEvent event) {
								Window.Location.assign("/_ah/block");
							}

						});
						alert.show();
					}

					@Override
					public void onCallFailure(Throwable caught) {
						Alert alert = new Alert(caught);
						alert.addHideButton();
						alert.show();
						BlockEditor.this.form.setEnabled(true);
					}

				});
			}

		});

		this.blockService.getLocations(new AsyncCallbackWithMsg<String[][]>() {

			@Override
			public void onCallSuccess(String[][] locations) {
				for(String[] loc : locations)
					BlockEditor.this.location.addItem(loc[0], loc[1]);
				BlockEditor.this.location.setStateAsDefault();
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

		this.blockService.getRoles(new AsyncCallbackWithMsg<String[][]>() {

			@Override
			public void onCallSuccess(String[][] roleList) {
				for(String[] role : roleList)
					BlockEditor.this.visibleTo.addRole(role[1], role[0]);
				BlockEditor.this.visibleTo.setSelection(0);
				BlockEditor.this.visibleTo.setStateAsDefault();
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

	private void update(final String encoded) {
		this.form.addResetButton();

		BlockEditor.this.form.addButton("Update Block", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				BlockEditor.this.form.setEnabled(false);

				if(! BlockEditor.this.form.validate()) {
					BlockEditor.this.form.setEnabled(true);
					return;
				}

				BlockDTO blockDTO = BlockEditor.this.getDTO();
				blockDTO.setLocation(BlockEditor.this.location.getValue());
				blockDTO.setVisibleTo(BlockEditor.this.visibleTo.getSelection());
				blockDTO.setRoleList(BlockEditor.this.visibleTo.getSelectedRoles());
				blockDTO.setVisibleAt(new LinkedList<String>(Arrays.asList(BlockEditor.this.visibleAt.getTexts())));
				blockDTO.setNotVisibleAt(new LinkedList<String>(Arrays.asList(BlockEditor.this.notVisibleAt.getTexts())));
				BlockEditor.this.blockService.update(encoded, blockDTO, new AsyncCallbackWithMsg<Void>(){

					@Override
					public final void onCallSuccess(Void result) {
						Alert alert = new Alert("Success !", "Block Updated Successfully !");
						alert.addButton("Done", new ClickHandler() {

							@Override
							public void onClick(ClickEvent event) {
								Window.Location.assign("/_ah/block");
							}

						});
						alert.show();
					};

					@Override
					public final void onCallFailure(Throwable caught) {
						Alert alert = new Alert(caught);
						alert.addHideButton();
						alert.show();
						BlockEditor.this.form.setEnabled(true);
					}

				});
			}
		});

		this.blockService.get(encoded, new AsyncCallbackWithMsg<BlockDTO>() {

			@Override
			public final void onCallSuccess(BlockDTO moduleDTO) {
				BlockEditor.this.putDTO(moduleDTO);
				for(String[] loc : moduleDTO.getLocations())
					BlockEditor.this.location.addItem(loc[0], loc[1]);
				BlockEditor.this.location.setValue(moduleDTO.getLocation());
				for(String[] role : moduleDTO.getRoles())
					BlockEditor.this.visibleTo.addRole(role[1], role[0]);
				BlockEditor.this.visibleTo.setSelection(moduleDTO.getVisibleTo());
				BlockEditor.this.visibleTo.setSelectedRoles(moduleDTO.getRoleList());
				BlockEditor.this.visibleAt.setTexts(moduleDTO.getVisibleAt().toArray(new String[0]));
				BlockEditor.this.notVisibleAt.setTexts(moduleDTO.getNotVisibleAt().toArray(new String[0]));
				BlockEditor.this.form.setStateAsDefault();
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
