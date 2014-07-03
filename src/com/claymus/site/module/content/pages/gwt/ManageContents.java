package com.claymus.site.module.content.pages.gwt;

import java.util.LinkedList;

import com.claymus.gwt.Alert;
import com.claymus.gwt.AsyncCallbackWithMsg;
import com.claymus.site.module.content.gwt.ContentService;
import com.claymus.site.module.content.gwt.ContentServiceAsync;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.Window;

public class ManageContents implements EntryPoint {

	private final static ContentServiceAsync contentService = GWT.create(ContentService.class);

	private final static String tableId = "claymus-Contents";
	private final static String prefix = "claymus-Contents-";

	private static Element saveButton;

	private static int locationCount;
	private static String[] locations;
	private static String[] orders;

	@Override
	public void onModuleLoad() {
		NodeList<Element> ulNodes = Document.get().getElementById(ManageContents.tableId).getElementsByTagName("ul");
		ManageContents.locationCount = ulNodes.getLength() + 1;

		ManageContents.locations = new String[ManageContents.locationCount];
		ManageContents.orders = new String[ManageContents.locationCount];

		for(int i = 0; i < ManageContents.locationCount - 1; i++) {
			String location = ulNodes.getItem(i).getId();
			ManageContents.locations[i] = location.substring(ManageContents.prefix.length());
			ManageContents.orders[i] = getOrder(ManageContents.locations[i]);
		}
		ManageContents.locations[ManageContents.locationCount - 1] = "DISABLED";
		ManageContents.orders[ManageContents.locationCount - 1] = getOrder("DISABLED");


		ManageContents.exportStaticMethods();

		ManageContents.saveButton = Document.get().getElementById(ManageContents.prefix + "SaveOrderButton");
		ManageContents.saveButton.removeAttribute("disabled");
		ManageContents.saveButton.setAttribute("onClick", "claymus_content_saveOrder()");
	}

	public static void saveOrder() {
		ManageContents.saveButton.setAttribute("disabled", "disabled");
		ManageContents.saveButton.setInnerText("Saving Contents Order ...");

		LinkedList<String> changedLocations = new LinkedList<String>();
		LinkedList<String> changedOrders = new LinkedList<String>();

		for(int i = 0; i < ManageContents.locationCount; i++) {
			String order = ManageContents.getOrder(ManageContents.locations[i]);
			if(! order.equals(ManageContents.orders[i]) && ! order.equals("")) {
				changedLocations.add(ManageContents.locations[i]);
				changedOrders.add(order);
			}
		}

		if(changedOrders.size() == 0) {
			ManageContents.saveButton.setInnerText("Save Contents Order");
			ManageContents.saveButton.removeAttribute("disabled");
			return;
		}

		ManageContents.contentService.saveOrder(Window.Location.getParameter("page"), changedLocations, changedOrders, new AsyncCallbackWithMsg<Void>() {

			@Override
			public void onCallSuccess(Void result) {
				for(int i = 0; i < ManageContents.locationCount; i++)
					ManageContents.orders[i] = getOrder(ManageContents.locations[i]);
				ManageContents.saveButton.setInnerText("Save Contents Order");
				ManageContents.saveButton.removeAttribute("disabled");
			}

			@Override
			public void onCallFailure(Throwable caught) {
				Alert alert = new Alert(caught);
				alert.addHideButton();
				alert.show();
				ManageContents.saveButton.setInnerText("Save Contents Order");
				ManageContents.saveButton.removeAttribute("disabled");
			}

		});

	}

	private static String getOrder(String location) {
		String id = ManageContents.prefix + location;
    	Element ulNode = Document.get().getElementById(id);
    	if(ulNode == null)
    		return "";
    	String order = "";
    	NodeList<Element> liNodes = ulNode.getElementsByTagName("li");
    	int count = liNodes.getLength();
    	for(int i = 0; i < count; i++)
    		order += "," + liNodes.getItem(i).getId().substring(ManageContents.prefix.length());
    	if(count > 0)
    		order = order.substring(1);
    	return order;
    }

	public static native void exportStaticMethods() /*-{
		$wnd.claymus_content_saveOrder = $entry(@com.claymus.site.module.content.pages.gwt.ManageContents::saveOrder());
	}-*/;

}
