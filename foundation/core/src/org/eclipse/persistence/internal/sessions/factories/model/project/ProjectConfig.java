/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.sessions.factories.model.project;


/**
 *  INTERNAL:
 */
public class ProjectConfig {
    private String m_projectString;

    public ProjectConfig() {
    }

    public void setProjectString(String projectString) {
        m_projectString = projectString;
    }

    public String getProjectString() {
        return m_projectString;
    }

    public boolean isProjectXMLConfig() {
        return false;
    }

    public boolean isProjectClassConfig() {
        return false;
    }
}