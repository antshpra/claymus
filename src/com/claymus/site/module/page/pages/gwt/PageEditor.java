package com.claymus.site.module.page.pages.gwt;

import com.claymus.gwt.Alert;
import com.claymus.gwt.AsyncCallbackWithMsg;
import com.claymus.gwt.form.Form;
import com.claymus.gwt.form.FormField;
import com.claymus.gwt.form.fields.ListBoxFormField;
import com.claymus.gwt.form.fields.TextBoxFormField;
import com.claymus.site.module.page.gwt.PageService;
import com.claymus.site.module.page.gwt.PageServiceAsync;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

public class PageEditor implements EntryPoint {

	private final PageServiceAsync pageService = GWT.create(PageService.class);

	private final RootPanel rootPanel = RootPanel.get("claymus-PageEditor");

	private Form form;

	private TextBoxFormField titleField;
	private TextBoxFormField uriField;
	private ListBoxFormField layoutField;


	@Override
	public void onModuleLoad() {
		this.form = new Form();

		this.titleField = new TextBoxFormField (
				"Page Title", false, null);

		this.uriField = new TextBoxFormField (
				"Page Uri", true,
				"Must start with a '/' and should not have a triling '/'. Allowed charaters are [a-z A-Z 0-9 - /].",
				FormField.PUBLIC_URI_REGEX);

		this.layoutField = new ListBoxFormField (
				"Layout", true, null);

		this.form.addField(this.titleField);
		this.form.addField(this.uriField);
		this.form.addField(this.layoutField);

		this.rootPanel.add(this.form);

		if(Window.Location.getPath().equals("/_ah/page/new"))
			add();
		else if(Window.Location.getPath().equals("/_ah/page/edit"))
			update(Window.Location.getParameter("key"));
	}


	private void add() {
		this.form.addResetButton();

		this.form.addButton("Add Page", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				PageEditor.this.form.setEnabled(false);

				if(! PageEditor.this.form.validate()) {
					PageEditor.this.form.setEnabled(true);
					return;
				}

				PageEditor.this.pageService.add(PageEditor.this.getDTO(), new AsyncCallbackWithMsg<String>() {

					@Override
					public void onCallSuccess(final String encoded) {
						String title = PageEditor.this.titleField.getText();
						Alert alert = new Alert("Success !", (title == null ? "" : "\"" + title + "\" ") + "Page Created Successfully !");
						alert.addButton("Add Another Page", new ClickHandler() {

							@Override
							public void onClick(ClickEvent event) {
								PageEditor.this.form.reset();
							}

						}, true);
						alert.addButton("Add Content to " + (title == null ? "Page" : "\"" + title + "\""), new ClickHandler() {

							@Override
							public void onClick(ClickEvent event) {
								Window.Location.assign("/_ah/content?page=" + encoded);
							}

						});
						alert.addButton("Done", new ClickHandler() {

							@Override
							public void onClick(ClickEvent event) {
								Window.Location.assign("/_ah/page");
							}

						});
						alert.show();
					}

					@Override
					public void onCallFailure(Throwable caught) {
						Alert alert = new Alert(caught);
						alert.addHideButton();
						alert.show();
						PageEditor.this.form.setEnabled(true);
					}

				});
			}

		});

		this.pageService.getLayouts(new AsyncCallbackWithMsg<String[][]>() {

			@Override
			public void onCallSuccess(String[][] layoutArr) {
				for(String[] layout : layoutArr)
					PageEditor.this.layoutField.addItem(layout[0], layout[1]);
				PageEditor.this.layoutField.setStateAsDefault();
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
				PageEditor.this.form.setEnabled(true);
			}

		});
	}

	private void update(final String encoded) {
		this.form.addResetButton();

		this.form.addButton("Update Page", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				PageEditor.this.form.setEnabled(false);

				if(! PageEditor.this.form.validate()) {
					PageEditor.this.form.setEnabled(true);
					return;
				}

				PageEditor.this.pageService.update(PageEditor.this.getDTO(), new AsyncCallbackWithMsg<Void>() {

					@Override
					public void onCallSuccess(Void result) {
						String title = PageEditor.this.titleField.getText();
						Alert alert = new Alert("Success !", (title == null ? "" : "\"" + title + "\" ") + "Page Updated Successfully !");
						alert.addButton("Add Content to " + (title == null ? "Page" : "\"" + title + "\""), new ClickHandler() {

							@Override
							public void onClick(ClickEvent event) {
								Window.Location.assign("/_ah/content?page=" + encoded);
							}

						});
						alert.addButton("Done", new ClickHandler() {

							@Override
							public void onClick(ClickEvent event) {
								Window.Location.assign("/_ah/page");
							}

						});
						alert.show();
					}

					@Override
					public void onCallFailure(Throwable caught) {
						Alert alert = new Alert(caught);
						alert.addHideButton();
						alert.show();
						PageEditor.this.form.setEnabled(true);
					}

				});
			}

		});

		this.pageService.get(encoded, new AsyncCallbackWithMsg<PageDTO>() {

			@Override
			public void onCallSuccess(PageDTO pageDTO) {
				for(String[] layout : pageDTO.getLayouts())
					PageEditor.this.layoutField.addItem(layout[0], layout[1]);
				PageEditor.this.putDTO(pageDTO);
				PageEditor.this.uriField.setEnabled(false);
				PageEditor.this.form.setStateAsDefault();
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
				PageEditor.this.form.setEnabled(true);
			}

		});
	}


	private PageDTO getDTO() {
		PageDTO pageDTO = new PageDTO();
		pageDTO.setUri(this.uriField.getText());
		pageDTO.setTitle(this.titleField.getText());
		pageDTO.setLayout(this.layoutField.getValue());
		return pageDTO;
	}

	private void putDTO(PageDTO pageDTO) {
		this.uriField.setText(pageDTO.getUri());
		this.titleField.setText(pageDTO.getTitle());
		this.layoutField.setValue(pageDTO.getLayout());
	}

}
