/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.logging;

import java.util.logging.LogRecord;
import java.util.logging.Level;
import org.eclipse.persistence.internal.databaseaccess.Accessor;

/**
 * INTERNAL:
 * <p>
 * Used to include TopLink's own logging properties that will be formatted by a TopLink Formatter
 * </p>
 */
public class EclipseLinkLogRecord extends LogRecord {
    private String sessionString;
    private Accessor connection;
    private boolean shouldLogExceptionStackTrace;
    private boolean shouldPrintDate;
    private boolean shouldPrintThread;

    public EclipseLinkLogRecord(Level level, String msg) {
        super(level, msg);
    }

    public String getSessionString() {
        return sessionString;
    }

    public void setSessionString(String sessionString) {
        this.sessionString = sessionString;
    }

    public Accessor getConnection() {
        return connection;
    }

    public void setConnection(Accessor connection) {
        this.connection = connection;
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
