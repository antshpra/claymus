package com.claymus.site;

import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import com.claymus.Datastore;
import com.claymus.Logger;
import com.google.appengine.api.datastore.KeyFactory;

/*
 * Thread-Safe: No Global Data
 */
public class ModuleData {

	@SuppressWarnings("unchecked")
	public static <T extends Module> T getModule(String moduleId) {
		Class<T> klass;
		try {
			klass = (Class<T>) Class.forName(Module.MODULE_PACKAGE + "." + moduleId + "." + Module.MODULE_SETTINGS_CLASS);
		} catch (ClassNotFoundException ex) {
			Logger.get().log(Level.SEVERE, null, ex);
			return null;
		}

		T module = Datastore.getEntity(klass, KeyFactory.createKey(Module.class.getSimpleName(), moduleId));
		if(module == null) {
			try {
				module = klass.newInstance();
				ModuleData.saveModule(module);
			} catch (Exception ex) {
				Logger.get().log(Level.SEVERE, null, ex);
				return null;
			}
		}

		return module;
	}

	public static <T extends Module> T getModule(Class<T> klass) {
		String moduleId = klass.getPackage().getName().substring(Module.MODULE_PACKAGE.length() + 1);

		T module = Datastore.getEntity(klass, KeyFactory.createKey(Module.class.getSimpleName(), moduleId));
		if(module == null) {
			try {
				module = klass.newInstance();
				ModuleData.saveModule(module);
			} catch (Exception ex) {
				Logger.get().log(Level.SEVERE, null, ex);
				return null;
			}
		}

		return module;
	}


	public static List<Module> getModules() {
		List<Module> modules = new LinkedList<Module>();
		URL url = ModuleData.class.getResource(Module.MODULE_PATH);
		File dir = new File(url.getPath());
		File[] list = dir.listFiles();
		for(File file : list) {
			if(file.isDirectory()) {
				String moduleId = file.getName();
				Module module = ModuleData.getModule(moduleId);
				if(module != null)
					modules.add(module);
			}
		}
		Collections.sort(modules);
		return modules;
	}


	public static boolean saveModule(Module module) {
		return Datastore.makePersistent(module) != null;
	}

}
