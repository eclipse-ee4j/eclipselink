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

import org.eclipse.persistence.tools.workbench.test.models.projects.EmployeeOXProject;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.swingui.TestRunner;
import org.eclipse.persistence.tools.workbench.ant.ProjectValidator;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;

public class OXProjectValidatorTests extends XmlProjectRunnerTests {
    
	public static void main( String[] args) {
	    
		TestRunner.main( new String[] { "-c", OXProjectValidatorTests.class.getName()});
	}

	public static Test suite() {
		return new TestSuite( OXProjectValidatorTests.class);
	}

	public OXProjectValidatorTests( String name) {
        super( name);
    }
	/**
	 * Verifies the Employee MWP project and displays the existing problems.
	 */
	public void testProjectValidator() throws Exception {
	    
	    ProjectValidator validator = new ProjectValidator( this.log);

	    File reportfile = new File( this.tempDir, "problem-report.html");
	    String reportformat = "html";

	    Vector ignoreErrorCodes = new Vector();
	    
	    int status = validator.execute( 
	    								this.projectFileName, 
	    								reportfile.getAbsolutePath(), 
	    								reportformat, 
	    								ignoreErrorCodes);
	    assertEquals( status, 0);
	}

	protected MWProject buildProject() throws Exception {
	    
	    return new EmployeeOXProject().getProject();
	}	

}
