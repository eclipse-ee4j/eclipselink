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
package org.eclipse.persistence.testing.tests.workbenchintegration;

import org.eclipse.persistence.testing.models.multipletable.MultipleTableSystem;

/**
 * Extend the MultipleTableSystem to allow us to read and write XML projects.
 *
 * @author Guy Pelletier
 */
public class MultipleTableModelWorkbenchIntegrationSystem extends MultipleTableSystem {
    protected static final String PROJECT_FILE = "MWIntegrationTestMultipleTableModelProject";

    public MultipleTableModelWorkbenchIntegrationSystem() {
        super();
        buildProject();
    }

    protected void buildProject() {
        project = WorkbenchIntegrationSystemHelper.buildProjectXML(project, PROJECT_FILE);
    }
}
