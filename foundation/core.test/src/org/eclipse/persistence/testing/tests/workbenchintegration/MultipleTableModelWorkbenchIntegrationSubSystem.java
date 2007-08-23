/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.workbenchintegration;

/**
 * Extend the MultipleTableSystem to allow us to compile/read the mapping 
 * projects to/from deployment source
 * 
 * @author Guy Pelletier
 */
public class MultipleTableModelWorkbenchIntegrationSubSystem extends MultipleTableModelWorkbenchIntegrationSystem {    
    protected void buildProject() {
        project = WorkbenchIntegrationSystemHelper.buildProjectClass(project, PROJECT_FILE);
    }
}
