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
package org.eclipse.persistence.tools.workbench.test.mappingsmodel;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.ComplexAggregateProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.ComplexAggregateRuntimeProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.CrimeSceneProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.CrimeSceneRuntimeProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.CurrencyProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.CurrencyRuntimeProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.EmployeeEisProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.EmployeeEisRuntimeProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.EmployeeOXProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.EmployeeOXRuntimeProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.IdentityPolicyProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.IdentityPolicyRuntimeProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.InsuranceProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.InsuranceRuntimeProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.LockingPolicyProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.LockingPolicyRuntimeProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.MultipleTableProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.MultipleTableRuntimeProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.PhoneCompanyProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.PhoneCompanyRuntimeProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.QueryProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.QueryRuntimeProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.SimpleAggregateProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.SimpleAggregateRuntimeProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.SimpleContactProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.SimpleContactRuntimeProject;
import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.utility.diff.Diff;
import org.eclipse.persistence.tools.workbench.utility.diff.DiffEngine;


/**
 * test converting a mapping workbench to a run-time project
 */
public class ProjectConverterTests 
	extends TestCase 
{
	
	public static Test suite() {
		TestTools.setUpJUnitThreadContextClassLoader();
		return new TestSuite(ProjectConverterTests.class);
	}
	
	public ProjectConverterTests(String name) {
		super(name);
	}
	
	protected void convertAndVerifyRuntime(MWProject original, Project originalRuntimeProject) {
		Project convertedRuntimeProject = original.buildRuntimeProject();
		DiffEngine diffEngine = MappingsModelTestTools.buildRuntimeDiffEngine();
		Diff diff = diffEngine.diff(originalRuntimeProject, convertedRuntimeProject);
		assertTrue(diff.getDescription(), diff.identical());
	}
	

	//TODO create a runtime project
//	public void testEjb20AccountConversion() {
//		this.convertAndVerify(new Ejb20AccountProject().getProject(), Ejb20AccountProject.emptyProject());
//	}
	
	public void testLockingPolicyProjectConversion() {
		this.convertAndVerifyRuntime(new LockingPolicyProject().getProject(), new LockingPolicyRuntimeProject().getRuntimeProject());
	}
	
	//MySQL database doesn't support returning
//	public void testReturningPolicyProjectConversion() {
//		this.convertAndVerifyRuntime(new ReturningPolicyProject().getProject(), new ReturningPolicyRuntimeProject().getRuntimeProject());
//	}
	
//	public void testReturningPolicyEisProjectConversion() {
//		this.convertAndVerifyRuntime(new ReturningPolicyEisProject().getProject(), new ReturningPolicyEisRuntimeProject().getRuntimeProject());
//	}
	
	public void testIdentityPolicyProjectConversion() {
		this.convertAndVerifyRuntime(new IdentityPolicyProject().getProject(), new IdentityPolicyRuntimeProject().getRuntimeProject());
	}
	
	public void testSimpleContactConversion() {
		this.convertAndVerifyRuntime(new SimpleContactProject().getProject(), new SimpleContactRuntimeProject().getRuntimeProject());
	}
	
	public void testInsuranceConversion() {
		this.convertAndVerifyRuntime(new InsuranceProject().getProject(), new InsuranceRuntimeProject().getRuntimeProject());
	}
	
	public void testCrimeSceneConversion() {
		this.convertAndVerifyRuntime(new CrimeSceneProject().getProject(), new CrimeSceneRuntimeProject().getRuntimeProject());
	}
	
	public void testCurrencyConversion() {
		this.convertAndVerifyRuntime(new CurrencyProject().getProject(), new CurrencyRuntimeProject().getRuntimeProject());
	}
	
	public void testPhoneCompanyConversion() {	
		this.convertAndVerifyRuntime(new PhoneCompanyProject(false).getProject(), new PhoneCompanyRuntimeProject(false).getRuntimeProject());
	}
	
	public void testPhoneCompanySharedAggregatesConversion() {
		this.convertAndVerifyRuntime(new PhoneCompanyProject(true).getProject(), new PhoneCompanyRuntimeProject(true).getRuntimeProject());
	}
	
	public void testQueryProjectConversion() {	
		this.convertAndVerifyRuntime(new QueryProject().getProject(), new QueryRuntimeProject().getRuntimeProject());
	}
    
	public void testEmployeeEisProjectConversion() {
		this.convertAndVerifyRuntime(new EmployeeEisProject().getProject(), new EmployeeEisRuntimeProject().getRuntimeProject());
	}


	public void testEmployeeOXProjectConversion() {
		this.convertAndVerifyRuntime(new EmployeeOXProject().getProject(), new EmployeeOXRuntimeProject().getRuntimeProject());
	}

	public void testComplexAggregateProjectConversion() {	
		this.convertAndVerifyRuntime(new ComplexAggregateProject().getProject(), new ComplexAggregateRuntimeProject().getRuntimeProject());
	}
	
	public void testSimpleAggregateProjectConversion() {	
		this.convertAndVerifyRuntime(new SimpleAggregateProject().getProject(), new SimpleAggregateRuntimeProject().getRuntimeProject());
	}

	public void testMultipleTableProjectConversion() {
		this.convertAndVerifyRuntime(new MultipleTableProject().getProject(), new MultipleTableRuntimeProject().getRuntimeProject());
	}
}
