package gf.photoviewer;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PVLogger {
	private static final String LOG_PATH = "%h\\PhotoViewerLog.log";
	private static Logger logger;

	public static Logger getLogger() {
		if (logger == null) {
			logger = Logger.getLogger(PVLogger.class.getName());
			logger.setLevel(Level.ALL);
			
			try {
				FileHandler fileHandler = new FileHandler(LOG_PATH, true);
				fileHandler.setLevel(Level.ALL);
				logger.addHandler(fileHandler);
			} catch (Exception e) {
				e.printStackTrace();
			}		
		}

		return logger;
	}
}
