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

/**
 * Extend the MappingSystem to allow us to compile/read the mapping projects
 * to/from deployment source
 *
 * @author Guy Pelletier
 */
public class MappingModelWorkbenchIntegrationSubSystem extends MappingModelWorkbenchIntegrationSystem {
    protected void buildProjects() {
        // Mapping project
        project = WorkbenchIntegrationSystemHelper.buildProjectClass(project, PROJECT_FILE);

        // Legacy project.
        legacyProject = WorkbenchIntegrationSystemHelper.buildProjectClass(legacyProject, LEGACY_PROJECT_FILE);

        // Multiple table project.
        multipleTableProject = WorkbenchIntegrationSystemHelper.buildProjectClass(multipleTableProject, MULTIPLE_TABLE_PROJECT_FILE);

        // Keyboard project.
        keyboardProject = WorkbenchIntegrationSystemHelper.buildProjectClass(keyboardProject, KEYBOARD_PROJECT_FILE);

        // Bi-directional insert order project.
        bidirectionalProject = WorkbenchIntegrationSystemHelper.buildProjectClass(bidirectionalProject, BIDIRECTIONAL_PROJECT_FILE);
    }
}
