package net.cmr.util;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;

public class Log {

    public enum LogLevel {
        DEBUG(Application.LOG_DEBUG), 
        INFO(Application.LOG_INFO), 
        ERROR(Application.LOG_ERROR),
        NONE(Application.LOG_NONE);

        public final int logLevel;
        LogLevel(int logLevel) {
            this.logLevel = logLevel;
        }
    }

    public static final String DEBUG_HEADER = "DEBUG";
    public static final String INFO_HEADER = "INFO";
    public static final String WARNING_HEADER = "WARNING";
    public static final String ERROR_HEADER = "ERROR";

    private LogLevel logLevel = LogLevel.INFO;
    private final boolean gdxAvailable;

    private Log() {
        if(Gdx.files == null) {
            gdxAvailable = false;
        } else {
            gdxAvailable = true;
        }
    }

    public static void info(String message) {
        if(getInstance().gdxAvailable) {
            Gdx.app.log(INFO_HEADER, message);
        } else {
            if (getInstance().logLevel == LogLevel.NONE || getInstance().logLevel == LogLevel.ERROR) {
                return;
            }
            System.out.println("[" + INFO_HEADER + "] " + message);
        }
    }

    public static void warning(String message) {
        if(getInstance().gdxAvailable) {
            Gdx.app.log(WARNING_HEADER, message);
        } else {
            if (getInstance().logLevel == LogLevel.NONE || getInstance().logLevel == LogLevel.ERROR) {
                return;
            }
            System.out.println("[" + WARNING_HEADER + "] " + message);
        }
    }

    public static void error(String message, Throwable throwable) {
        if(getInstance().gdxAvailable) {
            Gdx.app.error(ERROR_HEADER, message, throwable);
        } else {
            if (getInstance().logLevel == LogLevel.NONE) {
                return;
            }
            System.out.println("[" + ERROR_HEADER + "] " + message);
            throwable.printStackTrace();
        }
    }

    public static void debug(String message, Object...debugVariables) {
        StringBuilder messageBuilder = new StringBuilder(message);
        for(Object object : debugVariables) {
            if(object == null) {
                continue;
            }
            messageBuilder.append("\n- ");
            messageBuilder.append(object.getClass().getSimpleName());
            messageBuilder.append(" = ");
            messageBuilder.append(object.toString());
        }
        if(getInstance().gdxAvailable) {
            Gdx.app.debug(DEBUG_HEADER, messageBuilder.toString());
        } else {
            if (getInstance().logLevel != LogLevel.DEBUG) {
                return;
            }
            System.out.println("[" + DEBUG_HEADER + "] " + messageBuilder.toString());
        }
    }

    /**
     * Sets the log level of the application.
     * @see Application#LOG_DEBUG Application.LOG_DEBUG logs all messages
     * @see Application#LOG_INFO Application.LOG_INFO logs only info, warning, and error messages
     * @see Application#LOG_ERROR Application.LOG_ERROR logs only error messages
     * @see Application#LOG_NONE Application.LOG_NONE mutes all logging
     * @param logLevel The log level to set the application to.
     */
    public static void setLogLevel(LogLevel logLevel) {
        if(getInstance().gdxAvailable) {
            Gdx.app.setLogLevel(logLevel.logLevel);
        } else {
            Log log = getInstance();
            log.logLevel = logLevel;
        }
    }

    private static Log instance;
    public static Log getInstance() {
        if (instance == null) {
            instance = new Log();
        }
        return instance;
    }

    public static void initializeLog() {
        getInstance();
    }
    
}
