/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
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
