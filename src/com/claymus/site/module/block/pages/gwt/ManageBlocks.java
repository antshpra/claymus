package com.claymus.site.module.block.pages.gwt;

import java.util.LinkedList;

import com.claymus.gwt.Alert;
import com.claymus.gwt.AsyncCallbackWithMsg;
import com.claymus.site.module.block.gwt.BlockService;
import com.claymus.site.module.block.gwt.BlockServiceAsync;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;

public class ManageBlocks implements EntryPoint {

	private final static BlockServiceAsync blockService = GWT.create(BlockService.class);

	private final static String tableId = "claymus-Blocks";
	private final static String prefix = "claymus-Blocks-";

	private static Element saveButton;

	private static int locationCount;
	private static String[] locations;
	private static String[] orders;

	@Override
	public void onModuleLoad() {
		NodeList<Element> ulNodes = Document.get().getElementById(ManageBlocks.tableId).getElementsByTagName("ul");
		ManageBlocks.locationCount = ulNodes.getLength() + 1;

		ManageBlocks.locations = new String[ManageBlocks.locationCount];
		ManageBlocks.orders = new String[ManageBlocks.locationCount];

		for(int i = 0; i < ManageBlocks.locationCount - 1; i++) {
			String location = ulNodes.getItem(i).getId();
			ManageBlocks.locations[i] = location.substring(ManageBlocks.prefix.length());
			ManageBlocks.orders[i] = getOrder(ManageBlocks.locations[i]);
		}
		ManageBlocks.locations[ManageBlocks.locationCount - 1] = "DISABLED";
		ManageBlocks.orders[ManageBlocks.locationCount - 1] = getOrder("DISABLED");


		ManageBlocks.exportStaticMethods();

		ManageBlocks.saveButton = Document.get().getElementById(ManageBlocks.prefix + "SaveOrderButton");
		ManageBlocks.saveButton.removeAttribute("disabled");
		ManageBlocks.saveButton.setAttribute("onClick", "claymus_block_saveOrder()");
	}

	public static void saveOrder() {
		ManageBlocks.saveButton.setAttribute("disabled", "disabled");
		ManageBlocks.saveButton.setInnerText("Saving Blocks Order ...");

		LinkedList<String> changedLocations = new LinkedList<String>();
		LinkedList<String> changedOrders = new LinkedList<String>();

		for(int i = 0; i < ManageBlocks.locationCount; i++) {
			String order = ManageBlocks.getOrder(ManageBlocks.locations[i]);
			if(! order.equals(ManageBlocks.orders[i]) && ! order.equals("")) {
				changedLocations.add(ManageBlocks.locations[i]);
				changedOrders.add(order);
			}
		}

		if(changedOrders.size() == 0) {
			ManageBlocks.saveButton.setInnerText("Save Blocks Order");
			ManageBlocks.saveButton.removeAttribute("disabled");
			return;
		}

		ManageBlocks.blockService.saveOrder(changedLocations, changedOrders, new AsyncCallbackWithMsg<Void>() {

			@Override
			public void onCallSuccess(Void result) {
				for(int i = 0; i < ManageBlocks.locationCount; i++)
					ManageBlocks.orders[i] = getOrder(ManageBlocks.locations[i]);
				ManageBlocks.saveButton.setInnerText("Save Blocks Order");
				ManageBlocks.saveButton.removeAttribute("disabled");
			}

			@Override
			public void onCallFailure(Throwable caught) {
				Alert alert = new Alert(caught);
				alert.addHideButton();
				alert.show();
				ManageBlocks.saveButton.setInnerText("Save Blocks Order");
				ManageBlocks.saveButton.removeAttribute("disabled");
			}

		});
	}

	private static String getOrder(String location) {
		String id = ManageBlocks.prefix + location;
    	Element ulNode = Document.get().getElementById(id);
    	if(ulNode == null)
    		return "";
    	String order = "";
    	NodeList<Element> liNodes = ulNode.getElementsByTagName("li");
    	int count = liNodes.getLength();
    	for(int i = 0; i < count; i++)
    		order += "," + liNodes.getItem(i).getId().substring(ManageBlocks.prefix.length());
    	if(count > 0)
    		order = order.substring(1);
    	return order;
    }

	public static native void exportStaticMethods() /*-{
		$wnd.claymus_block_saveOrder = $entry(@com.claymus.site.module.block.pages.gwt.ManageBlocks::saveOrder());
	}-*/;

}
