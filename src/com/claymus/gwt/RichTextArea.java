package com.claymus.gwt;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasEnabled;

public class RichTextArea extends Composite implements Focusable, HasEnabled {

	private String ckEditor;
	private HTML htmlWidget;

	/*
	 * Constructors
	 */

	public RichTextArea() {
		FlowPanel panel = new FlowPanel();

		this.htmlWidget = new HTML();
		this.htmlWidget.setStyleName("claymus-t1");
		panel.add(this.htmlWidget);

		initWidget(panel);

		setStyleName("claymus-gwt-RichTextArea");
	}

	/*
	 * Inherited Methods
	 */

	@Override
	protected void onLoad() {
		super.onLoad();
		if(this.ckEditor == null)
			this.ckEditor = loadEditor(this.htmlWidget.getElement());
	}

	@Override
	public void setFocus(boolean focused) {
		if(focused)
			if(this.ckEditor != null)
				setFocus(this.ckEditor);
//		else TODO
//			super.setFocus(focused);
	}

	@Override
	public int getTabIndex() {
		return 0; // TODO Auto-generated method stub
	}

	@Override
	public void setTabIndex(int index) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setAccessKey(char key) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isEnabled() {
		return this.ckEditor != null;
	}

	@Override
	public void setEnabled(boolean enabled) {
		if(enabled) {
			if(this.ckEditor == null)
				this.ckEditor = loadEditor(this.htmlWidget.getElement());
		} else {
			if(this.ckEditor != null) {
				destroyEditor(this.ckEditor);
				this.ckEditor = null;
			}
		}
	}

	/*
	 * Helper Methods
	 */

	public String getHTML() {
		String html;
		if(this.ckEditor != null)
			html = getEditorHTML(this.ckEditor).trim();
		else
			html = this.htmlWidget.getHTML().trim();
		return html.length() == 0 ? null : html;
	};

	public void setHTML(String html) {
		if(this.ckEditor != null)
			setEditorHTML(this.ckEditor, html == null ? "" : html);
		else
			this.htmlWidget.setHTML(html);
	}

	/*
	 * JavaScript Native methods to call CKEditor's methods
	 */

	private native String loadEditor(Element element)/*-{
		return $wnd.CKEDITOR.replace(element).name;
	}-*/;

	private native void destroyEditor(String ckEditor)/*-{
		if($wnd.CKEDITOR.instances[ckEditor])
			$wnd.CKEDITOR.instances[ckEditor].destroy();
	}-*/;

	private native String getEditorHTML(String ckEditor)/*-{
		if($wnd.CKEDITOR.instances[ckEditor])
			return $wnd.CKEDITOR.instances[ckEditor].getData();
		else
			return null;
	}-*/;

	private native String setEditorHTML(String ckEditor, String html)/*-{
		if($wnd.CKEDITOR.instances[ckEditor])
			$wnd.CKEDITOR.instances[ckEditor].setData(html);
	}-*/;

	private native void setFocus(String ckEditor)/*-{
		if($wnd.CKEDITOR.instances[ckEditor])
			$wnd.CKEDITOR.instances[ckEditor].focus();
	}-*/;

}
