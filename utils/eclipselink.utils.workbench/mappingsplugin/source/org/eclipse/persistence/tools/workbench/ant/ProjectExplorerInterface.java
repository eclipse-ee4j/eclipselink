/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.ant;

import java.util.Vector;

/**
 * Defines the interface supported by the ProjectExplorer. 
 */
public interface ProjectExplorerInterface {

    /**
     * Generates TopLink deployment descriptor XML or the ejb-jar.xml 
     * depending on the specified Workbench project.
     * Returns 0 if the generation is successful.
     */
    int execute( String projectFile, 
			            String deploymentFile, 
			            String ejbJarXMLDir, 
			            Vector ignoreErrorCodes, 
			            Boolean failOnError, 
			            String url, 
			            String driverclass, 
			            String user, 
			            String password);
}
