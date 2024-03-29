/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.internal.sessions.factories.model.login.LoginConfig;
import org.eclipse.persistence.internal.sessions.factories.model.project.ProjectConfig;

import java.util.List;

/**
 * INTERNAL:
 */
public class DatabaseSessionConfig extends SessionConfig {
    private LoginConfig m_loginConfig;
    private List<ProjectConfig> m_additionalProjects;
    private ProjectConfig m_primaryProject;

    public DatabaseSessionConfig() {
        super();
    }

    public List<ProjectConfig> getAdditionalProjects() {
        return m_additionalProjects;
    }

    public LoginConfig getLoginConfig() {
        return m_loginConfig;
    }

    public ProjectConfig getPrimaryProject() {
        return m_primaryProject;
    }

    public void setLoginConfig(LoginConfig loginConfig) {
        m_loginConfig = loginConfig;
    }

    public void setPrimaryProject(ProjectConfig primaryProject) {
        m_primaryProject = primaryProject;
    }

    public void setAdditionalProjects(List<ProjectConfig> additionalProjects) {
        m_additionalProjects = additionalProjects;
    }
}
