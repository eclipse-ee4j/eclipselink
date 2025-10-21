/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.logging.jul;


import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLogEntry;

import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

/**
 * Provides {@linkplain java.util.logging} specific logging functions.
 *
 *  @see org.eclipse.persistence.logging.SessionLog
 *  @see org.eclipse.persistence.logging.AbstractSessionLog
 *  @see org.eclipse.persistence.logging.SessionLogEntry
 */
public class JavaLog extends AbstractSessionLog {

    /**
     * Stores the default session name in case there is the session name is missing.
     */
    public static final String TOPLINK_NAMESPACE = "org.eclipse.persistence";
    public static final String DEFAULT_TOPLINK_NAMESPACE = TOPLINK_NAMESPACE + ".default";
    public static final String SESSION_TOPLINK_NAMESPACE = TOPLINK_NAMESPACE + ".session";

    /**
     * Stores all the java.util.logging.Levels.  The indexes are TopLink logging levels.
     */
    private static final Level[] levels = new Level[] { Level.ALL, Level.FINEST, Level.FINER, Level.FINE, Level.CONFIG, Level.INFO, Level.WARNING, Level.SEVERE, Level.OFF };

    /**
     * Represents the HashMap that stores all the name space strings.
     * The keys are category names.  The values are namespace strings.
     * Only initLoggers() method can change content of this Map.
     */
    private final Map<String, String> nameSpaceMap  = new HashMap<>();

    /**
     * Stores the namespace for session, i.e."{@code org.eclipse.persistence.session.<sessionname>}".
     */
    private String sessionNameSpace;

    /**
     * Stores the Logger for session namespace, i.e. "{@code org.eclipse.persistence.session.<sessionname>}".
     */
    private Logger sessionLogger;

    private final Map<String, Logger> categoryloggers = new HashMap<>();

    /**
     * Creates a new instance of wrapper class for java.util.logging.
     */
    public JavaLog() {
        super();
        addLogger(DEFAULT_TOPLINK_NAMESPACE, DEFAULT_TOPLINK_NAMESPACE);
    }

    /**
     * INTERNAL:
     * Add Logger to the categoryloggers.
     */
    protected void addLogger(String loggerCategory, String loggerNameSpace) {
        categoryloggers.put(loggerCategory, Logger.getLogger(loggerNameSpace));
    }

    /**
     * INTERNAL:
     * Return catagoryloggers
     */
     public Map<String, Logger> getCategoryLoggers() {
         // Lazy initialization of loggers, sessionNameSpace and nameSpaceMap
         if (nameSpaceMap.isEmpty()) {
             initLoggers();
         }
         return categoryloggers;
     }

    /**
     * PUBLIC:
     * <p>
     * Return the effective log level for the name space extracted from session and category.
     * If a Logger's level is set to be null then the Logger will use an effective Level that will
     * be obtained by walking up the parent tree and using the first non-null Level.
     * </p>
     *
     * @return the effective log level.
     */
    @Override
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
    @Override
    public void setLevel(final int level, String category) {
        final Logger logger = getLogger(category);
        if (logger == null) {
            return;
        }
        if (System.getSecurityManager() != null) {
            AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
                logger.setLevel(getJavaLevel(level));
                return null; // nothing to return
            });
        } else {
            logger.setLevel(getJavaLevel(level));
        }
    }

    /**
     * PUBLIC:
     * <p>
     * Set the output stream  that will receive the formatted log entries.
     * </p>
     *
     * @param fileOutputStream the file output stream will receive the formatted log entries.
     */
    @Override
    public void setWriter(OutputStream fileOutputStream){
        StreamHandler sh = new StreamHandler(fileOutputStream,new LogFormatter());
        categoryloggers.get(DEFAULT_TOPLINK_NAMESPACE).addHandler(sh);
        if(sessionLogger!=null){
            sessionLogger.addHandler(sh);
        }
    }

    /**
     * INTERNAL:
     * Return the name space for the given category from the map.
     */
    protected String getNameSpaceString(String category) {
        if (getSessionName() == null) {
            return DEFAULT_TOPLINK_NAMESPACE;
        } else {
            // Lazy initialization of loggers, sessionNameSpace and nameSpaceMap
            if (nameSpaceMap.isEmpty()) {
                initLoggers();
            }
            if ((category == null) || (category.isEmpty())) {
                return sessionNameSpace;
            } else {
                return nameSpaceMap.get(category);
            }
        }
    }

    /**
     * INTERNAL:
     * Return the Logger for the given category
     */
    protected Logger getLogger(String category) {
        // Lazy initialization of loggers, sessionNameSpace and nameSpaceMap
        if (nameSpaceMap.isEmpty()) {
            initLoggers();
        }
        if (getSessionName() == null) {
            return categoryloggers.get(DEFAULT_TOPLINK_NAMESPACE);
        } else if ((category == null) || (category.isEmpty()) || !this.categoryloggers.containsKey(category)) {
            return categoryloggers.get(sessionNameSpace);
        } else {
            Logger logger = categoryloggers.get(category);
            // If session != null, categoryloggers should have an entry for this category
            assert logger != null;
            return logger;
        }
    }

    // Initialize loggers, sessionNameSpace and nameSpaceMap
    private void initLoggers() {
        if (getSessionName() != null) {
            sessionNameSpace = SESSION_TOPLINK_NAMESPACE + "." + getSessionName();
        } else {
            sessionNameSpace = DEFAULT_TOPLINK_NAMESPACE;
        }
        addLogger(sessionNameSpace, sessionNameSpace);
        for (String loggerCategory : loggerCatagories) {
            String loggerNameSpace = sessionNameSpace + "." + loggerCategory;
            // Content of nameSpaceMap should not be changed anywhere else
            nameSpaceMap.put(loggerCategory, loggerNameSpace);
            addLogger(loggerCategory, loggerNameSpace);
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
     * </p>
     * @return true if the given message level will be logged
     */
    @Override
    public boolean shouldLog(int level, String category) {
        // Lazy initialization of loggers, sessionNameSpace and nameSpaceMap
        if (nameSpaceMap.isEmpty()) {
            initLoggers();
        }
        Logger logger = getLogger(category);
        return logger.isLoggable(getJavaLevel(level));
    }

    /**
     * PUBLIC:
     * <p>
     * Log a SessionLogEntry
     * </p>
     * @param entry SessionLogEntry that holds all the information for a TopLink logging event
     */
    @Override
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
     * </p>
     * @param entry SessionLogEntry that holds all the information for a TopLink logging event
     * @param javaLevel the message level
     */
    protected void internalLog(SessionLogEntry entry, Level javaLevel, Logger logger) {
        // Format message so that we do not depend on the bundle
        EclipseLinkLogRecord lr = new EclipseLinkLogRecord(javaLevel, formatMessage(entry));

        lr.setSourceClassName(entry.getSourceClassName());
        lr.setSourceMethodName(entry.getSourceMethodName());
        lr.setLoggerName(getNameSpaceString(entry.getNameSpace()));
        if (shouldPrintSession()) {
            lr.setSessionString(entry.getSessionId());
        }
        if (shouldPrintConnection()) {
            lr.setConnectionId(entry.getConnectionId());
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
     * </p>
     * @param throwable a throwable
     */
    @Override
    public void throwing(Throwable throwable) {
        getLogger(null).throwing(null, null, throwable);
    }

    /**
     * INTERNAL:
     * Each session owns its own session log because session is stored in the session log
     */
    @Override
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
