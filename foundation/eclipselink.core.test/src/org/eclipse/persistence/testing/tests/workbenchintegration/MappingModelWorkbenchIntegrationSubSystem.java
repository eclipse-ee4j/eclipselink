/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
