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

import java.util.*;
import java.util.logging.XMLFormatter;
import java.util.logging.LogRecord;
import java.util.logging.Level;

/**
 * <p>
 * Format a TopLink LogRecord into a standard XML format.
 * </p>
 */
@SuppressWarnings("deprecation")
public class XMLLogFormatter extends XMLFormatter {
    // Append a two digit number.
    private void a2(StringBuffer sb, int x) {
        if (x < 10) {
            sb.append('0');
        }
        sb.append(x);
    }

    // Append the time and date in ISO 8601 format
	private void appendISO8601(StringBuffer sb, long millis) {
        Date date = new Date(millis);
        sb.append(date.getYear() + 1900);
        sb.append('-');
        a2(sb, date.getMonth() + 1);
        sb.append('-');
        a2(sb, date.getDate());
        sb.append('T');
        a2(sb, date.getHours());
        sb.append(':');
        a2(sb, date.getMinutes());
        sb.append(':');
        a2(sb, date.getSeconds());
    }

    // Append to the given StringBuffer an escaped version of the
    // given text string where XML special characters have been escaped.
    // For a null string we appebd "<null>"
    private void escape(StringBuffer sb, String text) {
        if (text == null) {
            text = "<null>";
        }
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch == '<') {
                sb.append("&lt;");
            } else if (ch == '>') {
                sb.append("&gt;");
            } else if (ch == '&') {
                sb.append("&amp;");
            } else {
                sb.append(ch);
            }
        }
    }

    /**
     * Format the given message to XML.
     * @param record the log record to be formatted.
     * @return a formatted log record
     */
    public String format(LogRecord record0) {
        if (!(record0 instanceof EclipseLinkLogRecord)) {
            return super.format(record0);
        } else {
            EclipseLinkLogRecord record = (EclipseLinkLogRecord)record0;

            StringBuffer sb = new StringBuffer(500);
            sb.append("<record>\n");

            if (record.shouldPrintDate()) {
                sb.append("  <date>");
                appendISO8601(sb, record.getMillis());
                sb.append("</date>\n");
    
                sb.append("  <millis>");
                sb.append(record.getMillis());
                sb.append("</millis>\n");
            }

            sb.append("  <sequence>");
            sb.append(record.getSequenceNumber());
            sb.append("</sequence>\n");

            String name = record.getLoggerName();
            if (name != null) {
                sb.append("  <logger>");
                escape(sb, name);
                sb.append("</logger>\n");
            }

            sb.append("  <level>");
            escape(sb, record.getLevel().toString());
            sb.append("</level>\n");

            if (record.getSourceClassName() != null) {
                sb.append("  <class>");
                escape(sb, record.getSourceClassName());
                sb.append("</class>\n");
            }

            if (record.getSourceMethodName() != null) {
                sb.append("  <method>");
                escape(sb, record.getSourceMethodName());
                sb.append("</method>\n");
            }

            if (record.getSessionString() != null) {
                sb.append("  <session>");
                sb.append(record.getSessionString());
                sb.append("</session>\n");
            }

            if (record.getConnection() != null) {
                sb.append("  <connection>");
                sb.append(String.valueOf(System.identityHashCode(record.getConnection())));
                sb.append("</connection>\n");
            }

            if (record.shouldPrintThread()) {
                sb.append("  <thread>");
                sb.append(record.getThreadID());
                sb.append("</thread>\n");
            }

            if (record.getMessage() != null) {
                // Format the message string and its accompanying parameters.
                String message = formatMessage(record);
                sb.append("  <message>");
                escape(sb, message);
                sb.append("</message>");
                sb.append("\n");
            }

            // If the message is being localized, output the key, resource
            // bundle name, and params.
            ResourceBundle bundle = record.getResourceBundle();
            try {
                if ((bundle != null) && (bundle.getString(record.getMessage()) != null)) {
                    sb.append("  <key>");
                    escape(sb, record.getMessage());
                    sb.append("</key>\n");
                    sb.append("  <catalog>");
                    escape(sb, record.getResourceBundleName());
                    sb.append("</catalog>\n");
                    Object[] parameters = record.getParameters();
                    for (int i = 0; i < parameters.length; i++) {
                        sb.append("  <param>");
                        try {
                            escape(sb, parameters[i].toString());
                        } catch (Exception ex) {
                            sb.append("???");
                        }
                        sb.append("</param>\n");
                    }
                }
            } catch (Exception ex) {
                // The message is not in the catalog.  Drop through.
            }

            if (record.getThrown() != null) {
                // Report on the state of the throwable.
                Throwable th = record.getThrown();
                sb.append("  <exception>\n");
                sb.append("    <message>");
                escape(sb, th.toString());
                sb.append("</message>\n");

                if ((record.getLevel().intValue() == Level.SEVERE.intValue()) || 
                        ((record.getLevel().intValue() <= Level.WARNING.intValue()) && record.shouldLogExceptionStackTrace())) {
                    StackTraceElement[] trace = th.getStackTrace();
                    for (int i = 0; i < trace.length; i++) {
                        StackTraceElement frame = trace[i];
                        sb.append("    <frame>\n");
                        sb.append("      <class>");
                        escape(sb, frame.getClassName());
                        sb.append("</class>\n");
                        sb.append("      <method>");
                        escape(sb, frame.getMethodName());
                        sb.append("</method>\n");
                        // Check for a line number.
                        if (frame.getLineNumber() >= 0) {
                            sb.append("      <line>");
                            sb.append(frame.getLineNumber());
                            sb.append("</line>\n");
                        }
                        sb.append("    </frame>\n");
                    }
                }
                
                sb.append("  </exception>\n");
            }

            sb.append("</record>\n");
            return sb.toString();
        }
    }
}
