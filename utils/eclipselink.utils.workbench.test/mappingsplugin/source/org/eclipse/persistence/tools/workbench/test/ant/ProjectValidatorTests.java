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
package org.eclipse.persistence.tools.workbench.test.ant;

import java.io.File;
import java.util.Vector;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.ant.ProjectValidator;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.EmployeeProject;

public class ProjectValidatorTests extends RelationalProjectRunnerTests {
    
	public static Test suite() {
		return new TestSuite( ProjectValidatorTests.class);
	}

	public ProjectValidatorTests( String name) {
        super( name);
    }
	/**
	 * Verifies the Employee MWP project and displays the existing problems.
	 */
	public void testProjectValidator() throws Exception {
	    
	    ProjectValidator validator = new ProjectValidator( this.log);

	    File reportfile = new File( this.tempDir, "problem-report.html");
	    String reportformat = "html";
		// Ignoring: 2 problem(s) ( 0233)
		// 0233 - LargeProject - Number of primary keys does not match the number of primary keys on the parent.
		// 0233 - SmallProject - Number of primary keys does not match the number of primary keys on the parent.
	    Vector ignoreErrorCodes = new Vector();
	    ignoreErrorCodes.add( "0233"); 
	    
	    int status = validator.execute( 
	    								this.projectFileName, 
	    								reportfile.getAbsolutePath(), 
	    								reportformat, 
	    								ignoreErrorCodes);
	    assertEquals( status, 0);
	}

	protected MWProject buildProject() throws Exception {
	    
	    return new EmployeeProject().getProject();
	}	
}
