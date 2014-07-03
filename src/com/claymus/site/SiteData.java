package com.claymus.site;

public class SiteData {

	public static String getSiteVersion() {
		String userDir = System.getProperty("user.dir");
		return userDir.substring(userDir.lastIndexOf('/') + 1);
	}


	public static String getSiteTitle() {
		String title = Property.get("Site.Title");
		if(title == null) {
			title = "Claymus";
			SiteData.setSiteTitle(title);
		}
		return title;
	}

	public static boolean setSiteTitle(String title) {
		return Property.put("Site.Title", title);
	}


	public static String getSiteTitleSeparator() {
		String titleSeparator = Property.get("Site.TitleSeparator");
		if(titleSeparator == null) {
			titleSeparator = " | ";
			SiteData.setSiteTitleSeparator(titleSeparator);
		}
		return (String) Property.get("Site.TitleSeparator");
	}

	public static boolean setSiteTitleSeparator(String titleSeparator) {
		return Property.put("Site.TitleSeparator", titleSeparator);
	}


	public static String getSiteDescription() {
		String siteDescription = Property.get("Site.Description");
		if(siteDescription == null) {
			siteDescription = "Powered By Claymus !";
			SiteData.setSiteDescription(siteDescription);
		}
		return siteDescription;
	}

	public static boolean setSiteDescription(String siteDescription) {
		return Property.put("Site.Description", siteDescription);
	}

}
