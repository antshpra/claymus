package com.claymus.site.module.theme;

import java.io.File;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import com.claymus.Logger;
import com.claymus.site.Module;
import com.claymus.site.Property;
import com.claymus.site.module.theme.types.PixelPerfect;

public class ThemeData {

	public static Theme getTheme() {
		Theme theme = Property.get("Theme");
		if(theme == null) {
			theme = new PixelPerfect();
			ThemeData.setTheme(theme);
		}
		return theme;
	}

	public static Theme getTheme(String className) {
		try {
			return (Theme) Class.forName(Module.MODULE_PACKAGE + ".theme.types." + className).newInstance();
		} catch(Exception ex) {
			Logger.get().log(Level.SEVERE, null, ex);
			return null;
		}
	}

	public static List<Theme> getThemes() {
		LinkedList<Theme> themes = new LinkedList<Theme>();
		URL url = ThemeData.class.getResource(Module.MODULE_PATH + "theme/types");
		File dir = new File(url.getPath());
		File[] list = dir.listFiles();
		for(File file : list) {
			if(file.isFile()) {
				String className = file.getName().split("[.]")[0];
				Theme theme = ThemeData.getTheme(className);
				if(theme != null)
					themes.add(theme);
			}
		}
		return themes;
	}

	public static boolean setTheme(Theme theme) {
		return Property.put("Theme", theme);
	}

}
