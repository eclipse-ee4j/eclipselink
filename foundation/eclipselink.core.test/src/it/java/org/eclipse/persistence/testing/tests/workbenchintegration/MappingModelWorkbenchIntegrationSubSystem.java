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
