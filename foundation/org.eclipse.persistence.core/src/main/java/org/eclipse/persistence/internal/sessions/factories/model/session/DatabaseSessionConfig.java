/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.sessions.factories.model.session;

import java.util.Vector;
import org.eclipse.persistence.internal.sessions.factories.model.login.*;
import org.eclipse.persistence.internal.sessions.factories.model.project.*;

/**
 * INTERNAL:
 */
public class DatabaseSessionConfig extends SessionConfig {
    private LoginConfig m_loginConfig;
    private Vector<ProjectConfig> m_additionalProjects;
    private ProjectConfig m_primaryProject;

    public DatabaseSessionConfig() {
        super();
    }

    public Vector<ProjectConfig> getAdditionalProjects() {
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

    public void setAdditionalProjects(Vector<ProjectConfig> additionalProjects) {
        m_additionalProjects = additionalProjects;
    }
}
