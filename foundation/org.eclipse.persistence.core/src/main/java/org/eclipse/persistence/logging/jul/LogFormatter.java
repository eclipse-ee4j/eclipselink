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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

/**
 * Print a brief summary of a TopLink LogRecord in a human-readable format.
 * The summary will typically be 1 or 2 lines.
 */
public class LogFormatter extends SimpleFormatter {

    // Copy of AbstractSessionLog.CONNECTION_STRING to keep source visibility
    private static final String CONNECTION_STRING = "Connection";

    // Copy of AbstractSessionLog.THREAD_STRING to keep source visibility
    private static final String THREAD_STRING = "Thread";

    Date dat = new Date();
    private static final String format = "{0,date} {0,time}";
    private MessageFormat formatter;
    private final Object[] args = new Object[1];

    // Line separator string.  This is the value of the line.separator
    // property at the moment that the SimpleFormatter was created.
    private final String lineSeparator = System.lineSeparator();

    /**
     * Creates a new instance of TopLink LogRecord human-readable formatter.
     */
    public LogFormatter() {
        super();
    }

    /**
     * Format the given LogRecord.
     * @param record0 the log record to be formatted.
     * @return a formatted log record
     */
    @Override
    public synchronized String format(LogRecord record0) {
        if (!(record0 instanceof EclipseLinkLogRecord record)) {
            return super.format(record0);
        } else {

            //method is synchronized
            StringBuilder sb = new StringBuilder();

            if (record.shouldPrintDate()) {
                // Minimize memory allocations here.
                dat.setTime(record.getMillis());
                args[0] = dat;
                StringBuffer text = new StringBuffer();
                if (formatter == null) {
                    formatter = new MessageFormat(format);
                }
                formatter.format(args, text, null);
                sb.append(text);
                sb.append(" ");
            }
            if (record.getSourceClassName() != null) {
                sb.append(record.getSourceClassName());
            } else {
                sb.append(record.getLoggerName());
            }
            if (record.getSourceMethodName() != null) {
                sb.append(" ");
                sb.append(record.getSourceMethodName());
            }
            if (record.getSessionString() != null) {
                sb.append(" ");
                sb.append(record.getSessionString());
            }
            // Keep backwards compatibility in 4.x
            if (record.getConnection() != null) {
                sb.append(" ");
                sb.append(CONNECTION_STRING + "(").append(System.identityHashCode(record.getConnection())).append(")");
            }
            else if (record.getConnectionId() != null) {
                sb.append(" ");
                sb.append(CONNECTION_STRING + "(").append(record.getConnectionId()).append(")");
            }
            if (record.shouldPrintThread()) {
                sb.append(" ");
                sb.append(THREAD_STRING + "(").append(record.getThreadID()).append(")");
            }
            sb.append(lineSeparator);
            String message = formatMessage(record);
            sb.append(record.getLevel().getLocalizedName());
            sb.append(": ");
            sb.append(message);
            sb.append(lineSeparator);
            if (record.getThrown() != null) {
                StringWriter sw = new StringWriter();
                try (PrintWriter pw = new PrintWriter(sw)) {
                    if (record.getLevel().intValue() == Level.SEVERE.intValue()) {
                        record.getThrown().printStackTrace(pw);
                    } else if (record.getLevel().intValue() <= Level.WARNING.intValue()) {
                        if (record.shouldLogExceptionStackTrace()) {
                            record.getThrown().printStackTrace(pw);
                        } else {
                            pw.write(record.getThrown().toString());
                            pw.write(lineSeparator);
                        }
                    }
                    sb.append(sw);
                }
            }
            return sb.toString();
        }
    }
}
