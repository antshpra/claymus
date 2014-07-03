package com.claymus.site.module.administer.pages.gwt;

import com.claymus.gwt.Alert;
import com.claymus.gwt.AsyncCallbackWithMsg;
import com.claymus.site.module.administer.gwt.AdministerService;
import com.claymus.site.module.administer.gwt.AdministerServiceAsync;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;

public class ManageModules implements EntryPoint {

	private final static AdministerServiceAsync administerService = GWT.create(AdministerService.class);

	@Override
	public void onModuleLoad() {
		ManageModules.exportStaticMethods();
    }

	public static void setModuleAccessLevel(String moduleId, String encoded, String accessLevel) {
		ManageModules.administerService.setModuleAccessLevel(moduleId, encoded, Integer.parseInt(accessLevel), new AsyncCallbackWithMsg<Void>() {

			@Override
			public void onCallSuccess(Void result) {}

			@Override
			public void onCallFailure(Throwable caught) {
				Alert alert = new Alert(caught);
				alert.addHideButton();
				alert.show();
			}

		});
	}

	public static native void exportStaticMethods() /*-{
		$wnd.claymus_administer_setModuleAccessLevel = $entry(@com.claymus.site.module.administer.pages.gwt.ManageModules::setModuleAccessLevel(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;));
	}-*/;

}
