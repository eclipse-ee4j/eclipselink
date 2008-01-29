/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.ant;

import java.io.File;
import java.util.Vector;

import org.eclipse.persistence.tools.workbench.test.models.projects.EmployeeEisProject;
import org.eclipse.persistence.tools.workbench.test.utility.TestTools;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.swingui.TestRunner;
import org.eclipse.persistence.tools.workbench.ant.ProjectExporter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml.MWEisLoginSpec;

public class EisProjectExporterTests extends XmlProjectRunnerTests {

	public static void main( String[] args) {
	    
		TestRunner.main( new String[] { "-c", EisProjectExporterTests.class.getName()});
	}

	public static Test suite() {
		TestTools.setUpJUnitThreadContextClassLoader();
		return new TestSuite( EisProjectExporterTests.class);
	}

	public EisProjectExporterTests( String name) {
        super( name);
    }
	/**
	 * Generates of a TopLink deployment descriptor XML
	 */
	public void testProjectExporter() throws Exception {

	    ProjectExporter exporter = new ProjectExporter( this.log);

	    File deploymentFile = new File( this.tempDir, MW + "/EmployeeEisProject.xml");

	    Vector ignoreErrorCodes = new Vector();

	    int status = exporter.execute( 
	    							this.projectFileName, 						// mwp fileName
	    							deploymentFile.getAbsolutePath(),	// deploymentFileName
	    							"", 
	    							ignoreErrorCodes, 
	    							new Boolean( true), 	// failOnErrorObject
	    							"JMS.URL", 
	    							MWEisLoginSpec.JMS_ADAPTER_NAME, 
	    							"tle", "");
	    assertEquals( status, 0);
	}

	protected MWProject buildProject() throws Exception {
	    
	    return new EmployeeEisProject().getProject();
	}	

}
