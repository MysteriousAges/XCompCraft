package nil.simpledim;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogHelper {

	public static final Logger logger = LogManager.getLogger(SimpleDim.NAME);
	
	public static void log(Level level, Throwable e, String message) {
		log(level, message);
		e.printStackTrace();
	}
	
	public static void error(String message) {
		log(Level.ERROR, message);
	}
	
	public static void warn(String message) {
		log(Level.WARN, message);
	}
	
	public static void info(String message) {
		log(Level.INFO, message);
	}
	
	public static void log(Level level, String message) {
		logger.log(level, message);
	}
}
