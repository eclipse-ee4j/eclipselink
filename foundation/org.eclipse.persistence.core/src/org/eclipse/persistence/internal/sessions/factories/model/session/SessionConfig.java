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
package org.eclipse.persistence.internal.sessions.factories.model.session;

import org.eclipse.persistence.internal.sessions.factories.model.log.*;
import org.eclipse.persistence.internal.sessions.factories.model.rcm.*;
import org.eclipse.persistence.internal.sessions.factories.model.event.*;
import org.eclipse.persistence.internal.sessions.factories.model.platform.*;

/**
 * INTERNAL:
 */
public abstract class SessionConfig {
    private String m_name;
    private ServerPlatformConfig m_serverPlatformConfig;
    private RemoteCommandManagerConfig m_remoteCommandManagerConfig;
    private SessionEventManagerConfig m_sessionEventManagerConfig;
    private String m_profiler;
    private String m_externalTransactionControllerClass;
    private String m_exceptionHandlerClass;
    private LogConfig m_logConfig;
    private String m_sessionCustomizerClass;

    public SessionConfig() {
    }

    public void setName(String name) {
        m_name = name;
    }

    public String getName() {
        return m_name;
    }

    public void setServerPlatformConfig(ServerPlatformConfig serverPlatformConfig) {
        m_serverPlatformConfig = serverPlatformConfig;
    }

    public ServerPlatformConfig getServerPlatformConfig() {
        return m_serverPlatformConfig;
    }

    public void setRemoteCommandManagerConfig(RemoteCommandManagerConfig remoteCommandManagerConfig) {
        m_remoteCommandManagerConfig = remoteCommandManagerConfig;
    }

    public RemoteCommandManagerConfig getRemoteCommandManagerConfig() {
        return m_remoteCommandManagerConfig;
    }

    public void setSessionEventManagerConfig(SessionEventManagerConfig sessionEventManagerConfig) {
        m_sessionEventManagerConfig = sessionEventManagerConfig;
    }

    public SessionEventManagerConfig getSessionEventManagerConfig() {
        return m_sessionEventManagerConfig;
    }

    public void setProfiler(String profiler) {
        m_profiler = profiler;
    }

    public String getProfiler() {
        return m_profiler;
    }

    public void setExceptionHandlerClass(String exceptionHandlerClass) {
        m_exceptionHandlerClass = exceptionHandlerClass;
    }

    public String getExceptionHandlerClass() {
        return m_exceptionHandlerClass;
    }

    public void setLogConfig(LogConfig logConfig) {
        m_logConfig = logConfig;
    }

    public LogConfig getLogConfig() {
        return m_logConfig;
    }

    public void setSessionCustomizerClass(String sessionCustomizerClass) {
        m_sessionCustomizerClass = sessionCustomizerClass;
    }

    public String getSessionCustomizerClass() {
        return m_sessionCustomizerClass;
    }
}
