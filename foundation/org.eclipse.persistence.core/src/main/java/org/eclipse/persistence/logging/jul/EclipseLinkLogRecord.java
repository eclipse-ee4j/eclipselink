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

import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * TopLink's own logging properties that will be formatted by a TopLink Formatter.
 */
public class EclipseLinkLogRecord extends LogRecord {
    private String sessionString;
    private Integer connectionId;
    private boolean shouldLogExceptionStackTrace;
    private boolean shouldPrintDate;
    private boolean shouldPrintThread;

    /**
     * Creates a new instance of TopLink's own logging properties.
     *
     * @param level the log request level value
     * @param msg the message to be logged
     */
    public EclipseLinkLogRecord(Level level, String msg) {
        super(level, msg);
    }

    public String getSessionString() {
        return sessionString;
    }

    public void setSessionString(String sessionString) {
        this.sessionString = sessionString;
    }

    /**
     * Return the datasource connection identifier.
     *
     * @return the datasource connection identifier
     */
    public Integer getConnectionId() {
        return connectionId;
    }

    /**
     * Set the datasource connection identifier.
     *
     * @param connectionId the datasource connection identifier
     */
    public void setConnectionId(Integer connectionId) {
        this.connectionId = connectionId;
    }

    public boolean shouldLogExceptionStackTrace() {
        return shouldLogExceptionStackTrace;
    }

    public void setShouldLogExceptionStackTrace(boolean shouldLogExceptionStackTrace) {
        this.shouldLogExceptionStackTrace = shouldLogExceptionStackTrace;
    }

    public boolean shouldPrintDate() {
        return shouldPrintDate;
    }

    public void setShouldPrintDate(boolean shouldPrintDate) {
        this.shouldPrintDate = shouldPrintDate;
    }

    public boolean shouldPrintThread() {
        return shouldPrintThread;
    }

    public void setShouldPrintThread(boolean shouldPrintThread) {
        this.shouldPrintThread = shouldPrintThread;
    }
}
