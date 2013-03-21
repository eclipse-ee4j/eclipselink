/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.logging;


import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.*;
import java.io.*;
import java.util.logging.*;
import org.eclipse.persistence.sessions.Session;

/**
 * PUBLIC:
 * <p>
 * This is a wrapper class for java.util.logging.  It is used when messages need to
 * be logged through java.util.logging.
 * </p>
 *  @see SessionLog
 *  @see AbstractSessionLog
 *  @see SessionLogEntry
 *  @see Session
 */
public class JavaLog extends AbstractSessionLog {

    /**
     * Stores the default session name in case there is the session name is missing.
     */
    public static final String TOPLINK_NAMESPACE = "org.eclipse.persistence";
    protected static final String LOGGING_LOCALIZATION_STRING = "org.eclipse.persistence.internal.localization.i18n.LoggingLocalizationResource";
    protected static final String TRACE_LOCALIZATION_STRING = "org.eclipse.persistence.internal.localization.i18n.TraceLocalizationResource";
    public static final String DEFAULT_TOPLINK_NAMESPACE = TOPLINK_NAMESPACE + ".default";
    public static final String SESSION_TOPLINK_NAMESPACE = TOPLINK_NAMESPACE + ".session";

    /**
     * Stores all the java.util.logging.Levels.  The indexes are TopLink logging levels.
     */
    private static final Level[] levels = new Level[] { Level.ALL, Level.FINEST, Level.FINER, Level.FINE, Level.CONFIG, Level.INFO, Level.WARNING, Level.SEVERE, Level.OFF };

    /**
     * Represents the HashMap that stores all the name space strings.
     * The keys are category names.  The values are namespace strings.
     */
    private Map nameSpaceMap  = new HashMap();

    /**
     * Stores the namespace for session, i.e."org.eclipse.persistence.session.<sessionname>".
     */
    private String sessionNameSpace;

    /**
     * Stores the Logger for session namespace, i.e. "org.eclipse.persistence.session.<sessionname>".
     */
    private Logger sessionLogger;

    private Map categoryloggers = new HashMap<String, Logger>();

    /**
     * INTERNAL:
     */
    
    public JavaLog(){
    	super();
    	addLogger(DEFAULT_TOPLINK_NAMESPACE, DEFAULT_TOPLINK_NAMESPACE);
    }

    /**
     * INTERNAL:
     * Add Logger to the catagoryloggers.
     */
    protected void addLogger(String loggerCategory, String loggerNameSpace) {
        categoryloggers.put(loggerCategory, Logger.getLogger(loggerNameSpace));
    }

    /**
     * INTERNAL:
     * Return catagoryloggers
     */
     public Map getCategoryLoggers() {
         return categoryloggers;
     }
     
    /**
     * PUBLIC:
     * <p>
     * Return the effective log level for the name space extracted from session and category.
     * If a Logger's level is set to be null then the Logger will use an effective Level that will
     * be obtained by walking up the parent tree and using the first non-null Level.
     * </p><p>
     *
     * @return the effective log level.
     * </p>
     */
    public int getLevel(String category) {
        Logger logger = getLogger(category);
        while ((logger != null) && (logger.getLevel() == null)) {
            logger = logger.getParent();
        }

        if (logger == null) {
            return OFF;
        }

        //For a given java.util.logging.Level, return the index (ie, TopLink logging level)
        int logLevel = logger.getLevel().intValue();
        for (int i = 0; i < levels.length ; i++) {
            if (logLevel == levels[i].intValue()) {
                return i;
            }
        }
        return OFF;
    }

    /**
     * PUBLIC:
     * <p>
     * Set the log level to a logger with name space extracted from the given category.
     * </p>
     */
    public void setLevel(final int level, String category) {
        final Logger logger = getLogger(category);
        if (logger == null) {
            return;
        }
        
        AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                logger.setLevel(getJavaLevel(level));
                return null; // nothing to return
            }
        });
    }

    /**
     * PUBLIC:
     * <p>
     * Set the output stream  that will receive the formatted log entries.
     * </p><p>
     *
     * @param fileOutputStream the file output stream will receive the formatted log entries.
     * </p>
     */
    public void setWriter(OutputStream fileOutputStream){
        StreamHandler sh = new StreamHandler(fileOutputStream,new LogFormatter());
        ((Logger)categoryloggers.get(DEFAULT_TOPLINK_NAMESPACE)).addHandler(sh);
        if(sessionLogger!=null){
            sessionLogger.addHandler(sh);
        }
    }

    /**
     * INTERNAL:
     * Return the name space for the given category from the map.
     */
    protected String getNameSpaceString(String category) {
        if (session == null) {
            return DEFAULT_TOPLINK_NAMESPACE;
        } else if ((category == null) || (category.length() == 0)) {
            return sessionNameSpace;
        } else {
            return  (String)nameSpaceMap.get(category);
        }
    }

    /**
     * INTERNAL:
     * Return the Logger for the given category
     */
    protected Logger getLogger(String category) {
        if (session == null) {
            return (Logger)categoryloggers.get(DEFAULT_TOPLINK_NAMESPACE);
        } else if ((category == null) || (category.length() == 0) || !this.categoryloggers.containsKey(category)) {
            return (Logger) categoryloggers.get(sessionNameSpace);
        } else {
            Logger logger = (Logger) categoryloggers.get(category);
            // If session != null, categoryloggers should have an entry for this category
            assert logger != null;
            return logger;
        }
    }

    /**
     * PUBLIC:
     * <p>
     * Set the session and session namespace.
     * </p>
     *
     * @param session  a Session
     * </p>
     */
    public void setSession(Session session) {
        super.setSession(session);
        if (session != null) {
            String sessionName = session.getName();
            if ((sessionName != null) && (sessionName.length() != 0)) {
                sessionNameSpace = SESSION_TOPLINK_NAMESPACE + "." + sessionName;
            } else {
                sessionNameSpace = DEFAULT_TOPLINK_NAMESPACE;
            }

            //Initialize loggers eagerly
            addLogger(sessionNameSpace, sessionNameSpace);
             for (int i = 0; i < loggerCatagories.length; i++) {
                String loggerCategory =  loggerCatagories[i]; 
                String loggerNameSpace = sessionNameSpace + "." + loggerCategory;
                nameSpaceMap.put(loggerCategory, loggerNameSpace);
                addLogger(loggerCategory, loggerNameSpace);
            }
        }
    }

    /**
     * INTERNAL:
     * Return the corresponding java.util.logging.Level for a given TopLink level.
     */
    protected Level getJavaLevel(int level) {
        return levels[level];
    }

    /**
     * PUBLIC:
     * <p>
     * Check if a message of the given level would actually be logged by the logger
     * with name space built from the given session and category.
     * Return the shouldLog for the given category from
     * </p><p>
     * @return true if the given message level will be logged
     * </p>
     */
    public boolean shouldLog(int level, String category) {
        Logger logger = getLogger(category);
        return logger.isLoggable(getJavaLevel(level));
    }

    /**
     * PUBLIC:
     * <p>
     * Log a SessionLogEntry
     * </p><p>
     * @param entry SessionLogEntry that holds all the information for a TopLink logging event
     * </p>
     */
    public void log(SessionLogEntry entry) {
        if (!shouldLog(entry.getLevel(), entry.getNameSpace())) {
            return;
        }

        Logger logger = getLogger(entry.getNameSpace());
        Level javaLevel = getJavaLevel(entry.getLevel());

        internalLog(entry, javaLevel, logger);
    }

    /**
     * INTERNAL:
     * <p>
     * Build a LogRecord
     * </p><p>
     * @param entry SessionLogEntry that holds all the information for a TopLink logging event
     * @param javaLevel the message level
     * </p>
     */
    protected void internalLog(SessionLogEntry entry, Level javaLevel, Logger logger) {
        // Format message so that we do not depend on the bundle
        EclipseLinkLogRecord lr = new EclipseLinkLogRecord(javaLevel, formatMessage(entry)); 

        lr.setSourceClassName(null);
        lr.setSourceMethodName(null);
        lr.setLoggerName(getNameSpaceString(entry.getNameSpace()));
        if (shouldPrintSession()) {
            lr.setSessionString(getSessionString(entry.getSession()));
        }
        if (shouldPrintConnection()) {
            lr.setConnection(entry.getConnection());
        }
        lr.setThrown(entry.getException());
        lr.setShouldLogExceptionStackTrace(shouldLogExceptionStackTrace());
        lr.setShouldPrintDate(shouldPrintDate());
        lr.setShouldPrintThread(shouldPrintThread());
        logger.log(lr);
    }

    /**
     * PUBLIC:
     * <p>
     * Log a throwable.
     * </p><p>
     * @param throwable a throwable
     * </p>
     */
    public void throwing(Throwable throwable) {
        getLogger(null).throwing(null, null, throwable);
    }

    /**
     * INTERNAL:
     * Each session owns its own session log because session is stored in the session log
     */
    public Object clone() {
        // There is no special treatment required for cloning here
        // The state of this object is described  by member variables sessionLogger and categoryLoggers.
        // This state depends on session.
        // If session for the clone is going to be the same as session for this there is no
        // need to do "deep" cloning.
        // If not, the session being cloned should call setSession() on its JavaLog object to initialize it correctly.
        JavaLog cloneLog = (JavaLog)super.clone();
        return cloneLog;
    }
}
