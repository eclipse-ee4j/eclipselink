/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      tware - initial 
 ******************************************************************************/
package org.eclipse.persistence.jpa.rs.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.persistence.jpa.rs.DataStorage;
import org.eclipse.persistence.jpa.rs.logging.LoggingLocalization;

public class JPARSLogger {

    static final Logger logger = Logger.getLogger("org.eclipse.persistence.jpars");

    /**
     * INTERNAL:
     * Logging utility method.
     */
    public static void log(String message, Level level, Object[] params) {
        logger.log(level, LoggingLocalization.buildMessage(message, params));
    }

    /**
     * Entering.
     *
     * @param sourceClass the source class
     * @param sourceMethod the source method
     * @param params the params
     */
    public static void entering(String sourceClass, String sourceMethod, Object[] params) {
        // Logger logs enter/exit logs when log level <= FINER. However, we want to get these logs logged only when the log level is FINEST.
        // see java.util.logging.Logger::entering and java.util.logging.Logger::exiting
        if (logger.isLoggable(Level.FINEST)) {
            try {
                logger.entering(sourceClass, sourceMethod, getParamsWithRequestId(params));
            } catch (Throwable throwable) {
            }
        }
    }

    /**
     * Exiting.
     *
     * @param sourceClass the source class
     * @param sourceMethod the source method
     * @param methodExitLogData the method exit log data
     */
    public static void exiting(String sourceClass, String sourceMethod, MethodExitLogData methodExitLogData) {
        // Logger logs enter/exit logs when log level <= FINER. However, we want to get these logs logged only when the log level is FINEST.
        // see java.util.logging.Logger::entering and java.util.logging.Logger::exiting
        if (logger.isLoggable(Level.FINEST)) {
            try {
                String requestId = (String) DataStorage.get(DataStorage.REQUEST_ID);
                if (methodExitLogData == null) {
                    methodExitLogData = new MethodExitLogData(new Object[] { requestId });
                } else {
                    methodExitLogData.setRequestId(requestId);
                }
                logger.exiting(sourceClass, sourceMethod, methodExitLogData);
            } catch (Throwable throwable) {
            }
        }
    }

    /**
     * Finest.
     *
     * @param message the message
     * @param params the params
     */
    public static void finest(String message, Object[] params) {
        log(message, Level.FINEST, params);
    }

    /**
     * Fine.
     *
     * @param message the message
     * @param params the params
     */
    public static void fine(String message, Object[] params) {
        log(message, Level.FINE, params);
    }

    /**
     * Warning.
     *
     * @param message the message
     * @param params the params
     */
    public static void warning(String message, Object[] params) {
        log(message, Level.WARNING, params);
    }

    /**
     * Error.
     *
     * @param message the message
     * @param params the params
     */
    public static void error(String message, Object[] params) {
        log(message, Level.SEVERE, params);
    }

    /**
     * Exception.
     *
     * @param message the message
     * @param params the params
     * @param exc the exc
     */
    public static void exception(String message, Object[] params, Exception exc) {
        logger.log(Level.SEVERE, LoggingLocalization.buildMessage(message, params), exc);
    }

    /**
     * Sets the log level.
     *
     * @param level the new log level
     */
    public static void setLogLevel(Level level) {
        logger.setLevel(level);
    }

    private static Object[] getParamsWithRequestId(Object[] params) {
        String requestId = (String) DataStorage.get(DataStorage.REQUEST_ID);
        if (params != null) {
            Object[] paramsWithRequestId = new Object[params.length + 1];
            paramsWithRequestId[0] = requestId;
            for (int i = 0; i < params.length; i++) {
                paramsWithRequestId[i + 1] = params[i];
            }
            return paramsWithRequestId;
        }
        return new Object[] { requestId };
    }
}
