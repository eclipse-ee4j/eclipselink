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
package org.eclipse.persistence.internal.sessions.factories.model.log;

/**
 *  INTERNAL:
 */
public class LoggingOptionsConfig  {

    private Boolean m_logExceptionStacktrace;
    private Boolean m_printDate;
    private Boolean m_printSession;
    private Boolean m_printThread;
    private Boolean m_printConnection;

    public LoggingOptionsConfig() {
    }

    public void setShouldLogExceptionStackTrace(Boolean shouldLogExceptionStackTrace) {
        m_logExceptionStacktrace = shouldLogExceptionStackTrace;
    }

    public Boolean getShouldLogExceptionStackTrace() {
        return m_logExceptionStacktrace;
    }
    
    public void setShouldPrintDate(Boolean shouldPrintDate) {
        m_printDate = shouldPrintDate;
    }

    public Boolean getShouldPrintDate() {
        return m_printDate;
    }

    public void setShouldPrintSession(Boolean shouldPrintSession) {
        m_printSession = shouldPrintSession;
    }

    public Boolean getShouldPrintSession() {
        return m_printSession;
    }

    public void setShouldPrintThread(Boolean shouldPrintThread) {
        m_printThread = shouldPrintThread;
    }

    public Boolean getShouldPrintThread() {
        return m_printThread;
    }

    public void setShouldPrintConnection(Boolean shouldPrintConnection) {
        m_printConnection = shouldPrintConnection;
    }

    public Boolean getShouldPrintConnection() {
        return m_printConnection;
    }
}
