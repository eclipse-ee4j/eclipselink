/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.test.ant;

import java.io.File;
import java.util.Vector;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.ant.ProjectValidator;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.EmployeeEisProject;

public class EisProjectValidatorTests extends XmlProjectRunnerTests {
    
	public static Test suite() {
		return new TestSuite( EisProjectValidatorTests.class);
	}

	public EisProjectValidatorTests( String name) {
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
	    
	    return new EmployeeEisProject().getProject();
	}	

}
