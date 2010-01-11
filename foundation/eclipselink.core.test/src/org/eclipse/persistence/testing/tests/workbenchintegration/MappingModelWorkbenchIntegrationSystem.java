/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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

import org.eclipse.persistence.testing.models.mapping.MappingSystem;

/**
 * Extend the MappingSystem to allow us to read and write XML projects.
 * 
 * @author Guy Pelletier
 */
public class MappingModelWorkbenchIntegrationSystem extends MappingSystem {
    protected static final String PROJECT_FILE = "MWIntegrationTestMappingModelProject";
    protected static final String LEGACY_PROJECT_FILE = "MWIntegrationTestLegacyModelProject";
    protected static final String MULTIPLE_TABLE_PROJECT_FILE = "MWIntegrationTestMappingMultipleTableModelProject";
    protected static final String KEYBOARD_PROJECT_FILE = "MWIntegrationTestKeyboardModelProject";
    protected static final String BIDIRECTIONAL_PROJECT_FILE = "MWIntegrationTestBiDirectionalInsertOrderModelProject";
    
    public MappingModelWorkbenchIntegrationSystem() {
        super();
        buildProjects();        
    }
    
    protected void buildProjects() {
        // Mapping project
        project = WorkbenchIntegrationSystemHelper.buildProjectXML(project, PROJECT_FILE);
        
        // Legacy project.
        legacyProject = WorkbenchIntegrationSystemHelper.buildProjectXML(legacyProject, LEGACY_PROJECT_FILE);
        
        // Multiple table project.
        multipleTableProject = WorkbenchIntegrationSystemHelper.buildProjectXML(multipleTableProject, MULTIPLE_TABLE_PROJECT_FILE);
        
        // Keyboard project.
        keyboardProject = WorkbenchIntegrationSystemHelper.buildProjectXML(keyboardProject, KEYBOARD_PROJECT_FILE);
        
        // Bi-directional insert order project.
        bidirectionalProject = WorkbenchIntegrationSystemHelper.buildProjectXML(bidirectionalProject, BIDIRECTIONAL_PROJECT_FILE);
    }
}
