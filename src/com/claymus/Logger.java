package com.claymus;

/*
 * Only following levels/methods works with appengine -
 * Level.INFO		logger.info()		logged with severity "Info". GenericServlet.log() is same as this.
 * Level.WARNING	logger.warning()	logged with severity "Warning".
 * Level.SEVERE		logger.severe()		logged with severity "Error".
*/

public class Logger {

	private static final java.util.logging.Logger loggerInstance = java.util.logging.Logger.getAnonymousLogger();

	private Logger() {}

	public static java.util.logging.Logger get() {
		return Logger.loggerInstance;
	}

}
