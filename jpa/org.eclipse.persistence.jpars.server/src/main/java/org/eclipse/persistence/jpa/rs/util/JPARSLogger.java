/*
 * Copyright (c) 2011, 2025 Oracle and/or its affiliates. All rights reserved.
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

import jakarta.ws.rs.core.MediaType;

import org.eclipse.persistence.internal.jpa.rs.weaving.PersistenceWeavedRest;
import org.eclipse.persistence.jpa.rs.DataStorage;
import org.eclipse.persistence.jpa.rs.PersistenceContext;
import org.eclipse.persistence.jpa.rs.logging.LoggingLocalization;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.logging.SessionLogEntry;

/**
 * Logger for EclipseLink JPA-RS related functionality.
 * Publishes messages under the {@link SessionLog#JPARS} category.
 */
public class JPARSLogger {

    public static final JPARSLogger DEFAULT_LOGGER = new JPARSLogger(AbstractSessionLog.getLog());

    private SessionLog sessionLog;

    public JPARSLogger(SessionLog sessionLog) {
        this.sessionLog = sessionLog;
    }


    public void setSessionLog(SessionLog sessionLog) {
        this.sessionLog = sessionLog;
    }

    /**
     * Entering
     *
     * @param sourceClass the source class
     * @param sourceMethod the source method
     * @param sessionId the identifier of the session that generated the log entry or {@code null} when no session is available
     * @param params an array of parameters associated with the log message
     */
    public void entering(String sourceClass, String sourceMethod, String sessionId, Object[] params) {
        // Logger logs entering logs when log level <= FINER. But, we want to get these logs created only when the log level is FINEST.
        if (isLoggableFinest()) {
            SessionLogEntry sle = new SessionLogEntry(SessionLog.FINEST, SessionLog.JPARS, sessionId, "ENTRY {0}", getParamsWithAdditionalInfo(params), null, false);
            sle.setSourceClassName(sourceClass);
            sle.setSourceMethodName(sourceMethod);
            sessionLog.log(sle);
        }
    }

    /**
     * Entering
     *
     * @param sourceClass the source class
     * @param sourceMethod the source method
     * @param sessionId the identifier of the session that generated the log entry or {@code null} when no session is available
     * @param in the input stream
     */
    public void entering(String sourceClass, String sourceMethod, String sessionId, InputStream in) {
        // Logger logs entering logs when log level <= FINER. But, we want to get these logs created only when the log level is FINEST.

        // make sure input stream supports mark so that the or create a new BufferedInputStream which supports mark.
        // when mark is supported, the stream remembers all the bytes read after the call to mark and
        // stands ready to supply those same bytes again if and whenever the method reset is called.
        if (isLoggableFinest() && (in.markSupported())) {
            try {
                String data = readData(in);
                in.reset();
                if (data != null) {
                    Object[] logParams = getParamsWithAdditionalInfo(new Object[] { data });
                    SessionLogEntry sle = new SessionLogEntry(SessionLog.FINEST, SessionLog.JPARS, sessionId, "ENTRY {0}", logParams, null, false);
                    sle.setSourceClassName(sourceClass);
                    sle.setSourceMethodName(sourceMethod);
                    sessionLog.log(sle);
                }
            } catch (Throwable throwable) {
                exception(sessionId, throwable.getMessage(), new Object[] {}, throwable);
            }
        }
    }

    /**
     * Exiting
     *
     * @param sourceClass the source class
     * @param sourceMethod the source method
     * @param sessionId the identifier of the session that generated the log entry or {@code null} when no session is available
     * @param params an array of parameters associated with the log message
     */
    public void exiting(String sourceClass, String sourceMethod, String sessionId, Object[] params) {
        // Logger logs exiting logs when log level <= FINER. But, we want to get these logs created only when the log level is FINEST.
        if (isLoggableFinest()) {
            try {
                Object[] logParams = new Object[] {new MethodExitLogData(getParamsWithAdditionalInfo(params))};
                SessionLogEntry sle = new SessionLogEntry(SessionLog.FINEST, SessionLog.JPARS, sessionId, "RETURN {0}", logParams, null, false);
                sle.setSourceClassName(sourceClass);
                sle.setSourceMethodName(sourceMethod);
                sessionLog.log(sle);
            } catch (Throwable throwable) {
                exception(sessionId, throwable.getMessage(), new Object[] {}, throwable);
            }
        }
    }

    /**
     * Exiting
     *
     * @param sourceClass the source class
     * @param sourceMethod the source method
     * @param sessionId the identifier of the session that generated the log entry or {@code null} when no session is available
     * @param context the context
     * @param object the object
     * @param mediaType the media type
     */
    public void exiting(String sourceClass, String sourceMethod, String sessionId, PersistenceContext context, Object object, MediaType mediaType) {
        // Log marshaled object only when the log level is FINEST
        if (isLoggableFinest() && (context != null) && (object != null) && (mediaType != null)) {
            try {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                context.marshall(object, mediaType, outputStream, true);
                if (object instanceof PersistenceWeavedRest) {
                    exiting(sourceClass, sourceMethod, sessionId, new Object[] { object.getClass().getName(), outputStream.toString(StandardCharsets.UTF_8)});
                } else {
                    exiting(sourceClass, sourceMethod, sessionId, new Object[] { outputStream.toString(StandardCharsets.UTF_8) });
                }
            } catch (Throwable throwable) {
                exception(sessionId, throwable.getMessage(), new Object[] {}, throwable);
            }
        }
    }

    /**
     * Finest
     *
     * @param sessionId the identifier of the session that generated the log entry or {@code null} when no session is available
     * @param message the message
     * @param params parameters
     */
    public void finest(String sessionId, String message, Object[] params) {
        log(SessionLog.FINEST, sessionId, message, getParamsWithAdditionalInfo(params));
    }

    /**
     * Fine
     *
     * @param sessionId the identifier of the session that generated the log entry or {@code null} when no session is available
     * @param message the message
     * @param params parameters
     */
    public void fine(String sessionId, String message, Object[] params) {
        log(SessionLog.FINE, sessionId, message, getParamsWithAdditionalInfo(params));
    }

    /**
     * Warning
     *
     * @param sessionId the identifier of the session that generated the log entry or {@code null} when no session is available
     * @param message the message
     * @param params parameters
     */
    public void warning(String sessionId, String message, Object[] params) {
        log(SessionLog.WARNING, sessionId, message, getParamsWithAdditionalInfo(params));
    }

    /**
     * Error
     *
     * @param sessionId the identifier of the session that generated the log entry or {@code null} when no session is available
     * @param message the message
     * @param params parameters
     */
    public void error(String sessionId, String message, Object[] params) {
        log(SessionLog.SEVERE, sessionId, message, getParamsWithAdditionalInfo(params));
    }

    /**
     * Exception
     *
     * @param sessionId the identifier of the session that generated the log entry or {@code null} when no session is available
     * @param message the message
     * @param params parameters
     * @param exc the throwable
     */
    public void exception(String sessionId, String message, Object[] params, Throwable exc) {
        log(SessionLog.SEVERE, sessionId, message, getParamsWithAdditionalInfo(params), exc);
    }

    /**
     * Sets the log level
     *
     * @param level the new log level
     */
    public void setLogLevel(int level) {
        sessionLog.setLevel(level, SessionLog.JPARS);
    }

    /**
     * @return true if log level is set to {@link SessionLog#FINEST}
     */
    public boolean isLoggableFinest() {
        return sessionLog.shouldLog(SessionLog.FINEST, SessionLog.JPARS);
    }

    private Object[] getParamsWithAdditionalInfo(Object[] params) {
        String requestId = (String) DataStorage.get(DataStorage.REQUEST_ID);
        if (params != null) {
            Object[] paramsWithRequestId = new Object[params.length + 1];
            paramsWithRequestId[0] = requestId;
            System.arraycopy(params, 0, paramsWithRequestId, 1, params.length);
            return paramsWithRequestId;
        }
        return new Object[] { requestId };
    }

    private void log(int level, String sessionId, String message, Object[] params) {
        log(level, sessionId, message, params, null);
    }

    private void log(int level, String sessionId, String message, Object[] params, Throwable t) {
        Objects.requireNonNull(sessionLog);
        if (sessionLog.shouldLog(level, SessionLog.JPARS)) {
            SessionLogEntry sle = new SessionLogEntry(level, SessionLog.JPARS, sessionId, LoggingLocalization.buildMessage(message, params), t);
            sle.setParameters(params);
            sessionLog.log(sle);
        }
    }

    private String readData(InputStream is) throws IOException {
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
