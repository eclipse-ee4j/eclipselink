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
package org.eclipse.persistence.internal.sessions.factories.model.session;

import org.eclipse.persistence.internal.sessions.factories.model.event.SessionEventManagerConfig;
import org.eclipse.persistence.internal.sessions.factories.model.log.LogConfig;
import org.eclipse.persistence.internal.sessions.factories.model.platform.ServerPlatformConfig;
import org.eclipse.persistence.internal.sessions.factories.model.rcm.RemoteCommandManagerConfig;

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
