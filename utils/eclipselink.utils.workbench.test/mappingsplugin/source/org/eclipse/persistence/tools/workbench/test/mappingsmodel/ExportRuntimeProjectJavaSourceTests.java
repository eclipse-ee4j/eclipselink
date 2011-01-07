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
package org.eclipse.persistence.tools.workbench.test.mappingsmodel;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.CrimeSceneProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.EmployeeProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.QueryProject;
import org.eclipse.persistence.tools.workbench.test.utility.TestTools;


public class ExportRuntimeProjectJavaSourceTests extends AbstractExportRuntimeProjectJavaSourceTests {

	public static Test suite() {
		TestTools.setUpJUnitThreadContextClassLoader();
		return new TestSuite(ExportRuntimeProjectJavaSourceTests.class);
	}

	public ExportRuntimeProjectJavaSourceTests(String name) {
		super(name);
	}


	public void testCrimeSceneProject() throws Exception {
		this.verifyJavaExport(new CrimeSceneProject().getProject());	
	}

	public void testEmployeeProject() throws Exception {
		this.verifyJavaExport(new EmployeeProject().getProject());
	}

	public void testQueryProject() throws Exception {
		this.verifyJavaExport(new QueryProject().getProject());
	}
    
	//MySQL doesn't support returning
    //public void testReturningProject() throws Exception {
    //    this.verifyJavaExport(new ReturningPolicyProject().getProject());
    //}
    
	private void verifyJavaExport(MWRelationalProject project) throws Exception {
		this.configureDeploymentLogin(project);
		project.setProjectSourceDirectoryName(this.tempDir.getPath());
		project.setProjectSourceClassName(this.className(project));
		project.exportProjectSource();
		this.compileAndCheckJavaExport(project);
	}

}
