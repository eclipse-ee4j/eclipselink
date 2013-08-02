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

import java.io.ByteArrayOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.MediaType;

import org.eclipse.persistence.internal.weaving.PersistenceWeavedRest;
import org.eclipse.persistence.jpa.rs.DataStorage;
import org.eclipse.persistence.jpa.rs.PersistenceContext;
import org.eclipse.persistence.jpa.rs.logging.LoggingLocalization;

public class JPARSLogger {

    static final Logger logger = Logger.getLogger("org.eclipse.persistence.jpars");

    /**
     * Entering
     *
     * @param sourceClass the source class
     * @param sourceMethod the source method
     * @param params the params
     * {@link java.util.logging.Logger#entering(String sourceClass, String sourceMethod)}
     */
    public static void entering(String sourceClass, String sourceMethod, Object[] params) {
        // Logger logs enter logs when log level <= FINER. But, we want to get these logs created only when the log level is FINEST.
        if (logger.isLoggable(Level.FINEST)) {
            try {
                logger.entering(sourceClass, sourceMethod, getParamsWithAdditionalInfo(params));
            } catch (Throwable throwable) {
            }
        }
    }

    /**
     * Exiting
     *
     * @param sourceClass the source class
     * @param sourceMethod the source method
     * @param params the params
     * {@link java.util.logging.Logger#exiting(String sourceClass, String sourceMethod)}
     */
    public static void exiting(String sourceClass, String sourceMethod, Object[] params) {
        // Logger logs exit logs when log level <= FINER. But, we want to get these logs created only when the log level is FINEST.
        if (logger.isLoggable(Level.FINEST)) {
            try {
                logger.exiting(sourceClass, sourceMethod, new MethodExitLogData(getParamsWithAdditionalInfo(params)));
            } catch (Throwable throwable) {
            }
        }
    }

    /**
     * Exiting 
     *
     * @param sourceClass the source class
     * @param sourceMethod the source method
     * @param context the context
     * @param object the object
     * @param mediaType the media type
     */
    public static void exiting(String sourceClass, String sourceMethod, PersistenceContext context, Object object, MediaType mediaType) {
        // Log marshaled object only when the log level is FINEST
        if (logger.isLoggable(Level.FINEST) && (context != null) && (object != null) && (mediaType != null)) {
            try {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                context.marshallEntity(object, mediaType, outputStream, true);
                if (object instanceof PersistenceWeavedRest) {
                    exiting(sourceClass, sourceMethod, new Object[] { object.getClass().getName(), outputStream.toString("UTF-8") });
                } else {
                    exiting(sourceClass, sourceMethod, new Object[] { outputStream.toString("UTF-8") });
                }
            } catch (Throwable throwable) {
            }
        }
    }

    /**
     * Finest
     *
     * @param message the message
     * @param params the params
     */
    public static void finest(String message, Object[] params) {
        log(message, Level.FINEST, getParamsWithAdditionalInfo(params));
    }

    /**
     * Fine
     *
     * @param message the message
     * @param params the params
     */
    public static void fine(String message, Object[] params) {
        log(message, Level.FINE, getParamsWithAdditionalInfo(params));
    }

    /**
     * Warning
     *
     * @param message the message
     * @param params the params
     */
    public static void warning(String message, Object[] params) {
        log(message, Level.WARNING, getParamsWithAdditionalInfo(params));
    }

    /**
     * Error
     *
     * @param message the message
     * @param params the params
     */
    public static void error(String message, Object[] params) {
        log(message, Level.SEVERE, getParamsWithAdditionalInfo(params));
    }

    /**
     * Exception
     *
     * @param message the message
     * @param params the params
     * @param exc the exc
     */
    public static void exception(String message, Object[] params, Exception exc) {
        logger.log(Level.SEVERE, LoggingLocalization.buildMessage(message, getParamsWithAdditionalInfo(params)), exc);
    }

    /**
     * Sets the log level
     *
     * @param level the new log level
     */
    public static void setLogLevel(Level level) {
        logger.setLevel(level);
    }

    private static Object[] getParamsWithAdditionalInfo(Object[] params) {
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

    private static void log(String message, Level level, Object[] params) {
        logger.log(level, LoggingLocalization.buildMessage(message, params));
    }
}
