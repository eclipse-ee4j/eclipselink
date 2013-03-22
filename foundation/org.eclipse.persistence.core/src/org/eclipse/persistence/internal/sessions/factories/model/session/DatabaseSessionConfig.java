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

import java.util.Vector;
import org.eclipse.persistence.internal.sessions.factories.model.login.*;
import org.eclipse.persistence.internal.sessions.factories.model.project.*;

/**
 * INTERNAL:
 */
public class DatabaseSessionConfig extends SessionConfig {
    private LoginConfig m_loginConfig;
    private Vector m_additionalProjects;
    private ProjectConfig m_primaryProject;

    public DatabaseSessionConfig() {
        super();
    }

    public Vector getAdditionalProjects() {
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

    public void setAdditionalProjects(Vector additionalProjects) {
        m_additionalProjects = additionalProjects;
    }
}
