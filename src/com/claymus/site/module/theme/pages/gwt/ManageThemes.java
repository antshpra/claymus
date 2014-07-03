package com.claymus.site.module.theme.pages.gwt;

import com.claymus.gwt.Alert;
import com.claymus.gwt.AsyncCallbackWithMsg;
import com.claymus.site.module.theme.gwt.ThemeService;
import com.claymus.site.module.theme.gwt.ThemeServiceAsync;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;

public class ManageThemes implements EntryPoint {

	private final static ThemeServiceAsync themeService = GWT.create(ThemeService.class);

	@Override
	public void onModuleLoad() {
		ManageThemes.exportStaticMethods();
	}

	public static void setActive(String className) {
		ManageThemes.themeService.setActive(className, new AsyncCallbackWithMsg<Void>() {

			@Override
			public void onCallSuccess(Void result) {
				Window.Location.reload();
			}

			@Override
			public void onCallFailure(Throwable caught) {
				Alert alert = new Alert(caught);
				alert.addHideButton();
				alert.show();
			}

		});
	}

	public static native void exportStaticMethods() /*-{
		$wnd.claymus_theme_setActive = $entry(@com.claymus.site.module.theme.pages.gwt.ManageThemes::setActive(Ljava/lang/String;));
	}-*/;

}
