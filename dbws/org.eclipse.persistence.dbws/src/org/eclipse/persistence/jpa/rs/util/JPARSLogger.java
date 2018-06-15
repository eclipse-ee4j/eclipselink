/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//      tware - initial
package org.eclipse.persistence.jpa.rs.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.logging.Level;

import javax.ws.rs.core.MediaType;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.weaving.PersistenceWeavedRest;
import org.eclipse.persistence.jpa.rs.DataStorage;
import org.eclipse.persistence.jpa.rs.PersistenceContext;
import org.eclipse.persistence.jpa.rs.logging.LoggingLocalization;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.logging.SessionLogEntry;
import org.eclipse.persistence.sessions.Session;

/**
 * Logger for EclipseLink JPA-RS related functionality.
 * Publishes messages under the {@link SessionLog#JPARS} category.
 */
public class JPARSLogger {

    private static final SessionLog defaultLog = AbstractSessionLog.getLog();

    /**
     * Entering
     *
     * @param sourceClass the source class
     * @param sourceMethod the source method
     * @param params parameters
     */
    public static void entering(String sourceClass, String sourceMethod, Object[] params) {
        entering(defaultLog, sourceClass, sourceMethod, params);
    }

    /**
     * Entering
     *
     * @param sessionLog the log
     * @param sourceClass the source class
     * @param sourceMethod the source method
     * @param params parameters
     */
    public static void entering(SessionLog sessionLog, String sourceClass, String sourceMethod, Object[] params) {
        // Logger logs entering logs when log level <= FINER. But, we want to get these logs created only when the log level is FINEST.
        if (isLoggableFinest(sessionLog)) {
            SessionLogEntry sle = newLogEntry(sessionLog.getSession());
            sle.setSourceClassName(sourceClass);
            sle.setSourceMethodName(sourceMethod);
            sle.setMessage("ENTRY {0}");
            sle.setParameters(getParamsWithAdditionalInfo(params));
            sessionLog.log(sle);
        }
    }

    /**
     * Entering
     *
     * @param sourceClass the source class
     * @param sourceMethod the source method
     * @param in the input stream
     */
    public static void entering(String sourceClass, String sourceMethod, InputStream in) {
        entering(defaultLog, sourceClass, sourceMethod, in);
    }

    /**
     * Entering
     *
     * @param sessionLog log receiving the message
     * @param sourceClass the source class
     * @param sourceMethod the source method
     * @param in the input stream
     */
    public static void entering(SessionLog sessionLog, String sourceClass, String sourceMethod, InputStream in) {
        // Logger logs entering logs when log level <= FINER. But, we want to get these logs created only when the log level is FINEST.

        // make sure input stream supports mark so that the or create a new BufferedInputStream which supports mark.
        // when mark is supported, the stream remembers all the bytes read after the call to mark and
        // stands ready to supply those same bytes again if and whenever the method reset is called.
        if (isLoggableFinest(sessionLog) && (in.markSupported())) {
            try {
                String data = readData(in);
                in.reset();
                if (data != null) {
                    SessionLogEntry sle = newLogEntry(sessionLog.getSession());
                    sle.setSourceClassName(sourceClass);
                    sle.setSourceMethodName(sourceMethod);
                    sle.setMessage("ENTRY {0}");
                    sle.setParameters(getParamsWithAdditionalInfo(new Object[] { data }));
                    sessionLog.log(sle);
                }
            } catch (Throwable throwable) {
                exception(throwable.getMessage(), new Object[] {}, throwable);
            }
        }
    }

    /**
     * Exiting
     *
     * @param sourceClass the source class
     * @param sourceMethod the source method
     * @param params parameters
     */
    public static void exiting(String sourceClass, String sourceMethod, Object[] params) {
        exiting(defaultLog, sourceClass, sourceMethod, params);
    }

    /**
     * Exiting
     *
     * @param sessionLog the log
     * @param sourceClass the source class
     * @param sourceMethod the source method
     * @param params parameters
     */
    public static void exiting(SessionLog sessionLog, String sourceClass, String sourceMethod, Object[] params) {
        // Logger logs exiting logs when log level <= FINER. But, we want to get these logs created only when the log level is FINEST.
        if (isLoggableFinest()) {
            try {
                SessionLogEntry sle = newLogEntry(sessionLog.getSession());
                sle.setSourceClassName(sourceClass);
                sle.setSourceMethodName(sourceMethod);
                sle.setMessage("RETURN {0}");
                sle.setParameters(new Object[] {new MethodExitLogData(getParamsWithAdditionalInfo(params))});
                sessionLog.log(sle);
            } catch (Throwable throwable) {
                exception(throwable.getMessage(), new Object[] {}, throwable);
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
        exiting(defaultLog, sourceClass, sourceMethod, context, object, mediaType);
    }

    /**
     * Exiting
     *
     * @param sessionLog the log
     * @param sourceClass the source class
     * @param sourceMethod the source method
     * @param context the context
     * @param object the object
     * @param mediaType the media type
     */
    public static void exiting(SessionLog sessionLog, String sourceClass, String sourceMethod, PersistenceContext context, Object object, MediaType mediaType) {
        // Log marshaled object only when the log level is FINEST
        if (isLoggableFinest(sessionLog) && (context != null) && (object != null) && (mediaType != null)) {
            try {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                context.marshall(object, mediaType, outputStream, true);
                if (object instanceof PersistenceWeavedRest) {
                    exiting(sessionLog, sourceClass, sourceMethod, new Object[] { object.getClass().getName(), outputStream.toString(StandardCharsets.UTF_8.name())});
                } else {
                    exiting(sessionLog, sourceClass, sourceMethod, new Object[] { outputStream.toString(StandardCharsets.UTF_8.name()) });
                }
            } catch (Throwable throwable) {
                exception(throwable.getMessage(), new Object[] {}, throwable);
            }
        }
    }

    /**
     * Finest
     *
     * @param message the message
     * @param params parameters
     */
    public static void finest(String message, Object[] params) {
        finest(defaultLog, message, params);
    }

    /**
     * Finest
     *
     * @param sessionLog the log
     * @param message the message
     * @param params parameters
     */
    public static void finest(SessionLog sessionLog, String message, Object[] params) {
        log(sessionLog, SessionLog.FINEST, message, getParamsWithAdditionalInfo(params));
    }

    /**
     * Fine
     *
     * @param message the message
     * @param params parameters
     */
    public static void fine(String message, Object[] params) {
        fine(defaultLog, message, params);
    }

    /**
     * Fine
     *
     * @param sessionLog the log
     * @param message the message
     * @param params parameters
     */
    public static void fine(SessionLog sessionLog, String message, Object[] params) {
        log(sessionLog, SessionLog.FINE, message, getParamsWithAdditionalInfo(params));
    }

    /**
     * Warning
     *
     * @param message the message
     * @param params parameters
     */
    public static void warning(String message, Object[] params) {
        warning(defaultLog, message, params);
    }

    /**
     * Warning
     *
     * @param sessionLog the log
     * @param message the message
     * @param params parameters
     */
    public static void warning(SessionLog sessionLog, String message, Object[] params) {
        log(sessionLog, SessionLog.WARNING, message, getParamsWithAdditionalInfo(params));
    }

    /**
     * Error
     *
     * @param message the message
     * @param params parameters
     */
    public static void error(String message, Object[] params) {
        error(defaultLog, message, params);
    }

    /**
     * Error
     *
     * @param sessionLog the log
     * @param message the message
     * @param params parameters
     */
    public static void error(SessionLog sessionLog, String message, Object[] params) {
        log(sessionLog, SessionLog.SEVERE, message, getParamsWithAdditionalInfo(params));
    }

    /**
     * Exception
     *
     * @param message the message
     * @param params parameters
     * @param exc the throwable
     */
    public static void exception(String message, Object[] params, Throwable exc) {
        exception(defaultLog, message, params, exc);
    }

    /**
     * Exception
     *
     * @param sessionLog the log
     * @param message the message
     * @param params parameters
     * @param exc the throwable
     */
    public static void exception(SessionLog sessionLog, String message, Object[] params, Throwable exc) {
        log(sessionLog, SessionLog.SEVERE, message, getParamsWithAdditionalInfo(params), exc);
    }

    /**
     * Sets the log level
     *
     * @param level the new log level
     */
    public static void setLogLevel(Level level) {
        setLogLevel(defaultLog, AbstractSessionLog.translateStringToLoggingLevel(level.getName()));
    }

    /**
     * Sets the log level
     *
     * @param sessionLog the log
     * @param level the new log level
     */
    public static void setLogLevel(SessionLog sessionLog, int level) {
        sessionLog.setLevel(level, SessionLog.JPARS);
    }

    /**
     * @return true if log level is set to {@link SessionLog#FINEST}
     */
    public static boolean isLoggableFinest() {
        return isLoggableFinest(defaultLog);
    }

    /**
     * @param sessionLog the log
     *
     * @return true if log level is set to {@link SessionLog#FINEST}
     */
    public static boolean isLoggableFinest(SessionLog sessionLog) {
        return sessionLog.shouldLog(SessionLog.FINEST, SessionLog.JPARS);
    }

    private static Object[] getParamsWithAdditionalInfo(Object[] params) {
        String requestId = (String) DataStorage.get(DataStorage.REQUEST_ID);
        if (params != null) {
            Object[] paramsWithRequestId = new Object[params.length + 1];
            paramsWithRequestId[0] = requestId;
            System.arraycopy(params, 0, paramsWithRequestId, 1, params.length);
            return paramsWithRequestId;
        }
        return new Object[] { requestId };
    }

    private static void log(SessionLog sessionLog, int level, String message, Object[] params) {
        log(sessionLog, level, message, params, null);
    }

    private static void log(SessionLog sessionLog, int level, String message, Object[] params, Throwable t) {
        Objects.requireNonNull(sessionLog);
        if (sessionLog.shouldLog(level, SessionLog.JPARS)) {
            SessionLogEntry sle = newLogEntry(sessionLog.getSession());
            sle.setLevel(level);
            sle.setMessage(LoggingLocalization.buildMessage(message, params));
            sle.setParameters(params);
            sle.setException(t);
            sessionLog.log(sle);
        }
    }

    private static SessionLogEntry newLogEntry(Session session) {
        SessionLogEntry entry = session instanceof AbstractSession
                ? new SessionLogEntry((AbstractSession) session)
                : new SessionLogEntry(null);
        entry.setLevel(SessionLog.FINEST);
        entry.setNameSpace(SessionLog.JPARS);
        entry.setShouldTranslate(false);
        return entry;
    }

    private static String readData(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ByteArrayInputStream bais = null;
        int nRead;
        byte[] data = new byte[16384];
        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        byte[] bytes = buffer.toByteArray();
        bais = new ByteArrayInputStream(bytes);
        return getDataFromInputStream(bais);
    }

    private static String getDataFromInputStream(InputStream is) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            // ignore
        }
        return sb.toString();
    }
}
