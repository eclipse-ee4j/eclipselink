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
package org.eclipse.persistence.tools.workbench.test.mappingsio;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.mappingsio.ProjectIOManager;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethod;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.test.mappingsmodel.MappingsModelTestTools;
import org.eclipse.persistence.tools.workbench.test.models.projects.ComplexAggregateProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.ComplexInheritanceProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.ComplexMappingProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.CrimeSceneProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.CurrencyProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.EmployeeEisProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.EmployeeJAXBProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.EmployeeOXProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.EmployeeProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.IdentityPolicyProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.InsuranceProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.PhoneCompanyProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.QueryProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.ReturningPolicyEisProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.ReturningPolicyProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.SimpleContactProject;
import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.NullPreferences;
import org.eclipse.persistence.tools.workbench.utility.diff.Diff;
import org.eclipse.persistence.tools.workbench.utility.diff.DiffEngine;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;


public class ReadWriteTests extends TestCase {

	public static void main(String[] args) {
//		TestRunner.main(new String[] {"-c", ReadWriteTests.class.getName()});
		System.out.println(TestTools.execute(new ReadWriteTests("testRelationalEmployeeProject")));
	}

	public static Test suite() {
		return new TestSuite(ReadWriteTests.class);
	}
	
	public ReadWriteTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		TestTools.setUpOracleProxy();		// we need this for the EJB projects
	}
	
	public void testReturningPolicyProject() throws Exception {
		this.verifyReadWrite(new ReturningPolicyProject().getProject(), new ReturningPolicyProject().getProject());
	}

	public void testEisReturningPolicyProject() throws Exception {
		this.verifyReadWrite(new ReturningPolicyEisProject().getProject(), new ReturningPolicyEisProject().getProject());
	}

	public void testIdentityPolicyProject() throws Exception {
		this.verifyReadWrite(new IdentityPolicyProject().getProject(), new IdentityPolicyProject().getProject());
	}
	
	public void testComplexAggregateSystem() throws Exception {
		this.verifyReadWrite(new ComplexAggregateProject().getProject(), new ComplexAggregateProject().getProject());
	}
	
	public void testComplexInheritanceSystem() throws Exception {
		this.verifyReadWrite(new ComplexInheritanceProject().getProject(), new ComplexInheritanceProject().getProject());
	}
	
	public void testComplexMappingSystem() throws Exception {
		this.verifyReadWrite(new ComplexMappingProject().getProject(), new ComplexMappingProject().getProject());
	}
	
	public void testContactProject() throws Exception {
		this.verifyReadWrite(new SimpleContactProject().getProject(), new SimpleContactProject().getProject());
	}
	
	public void testCrimeSceneProject() throws Exception {
		this.verifyReadWrite(new CrimeSceneProject().getProject(), new CrimeSceneProject().getProject());
	}
	
	public void testCurrencyProject() throws Exception {
		this.verifyReadWrite(new CurrencyProject().getProject(), new CurrencyProject().getProject());
	}
	
	public void testInsuranceDemo() throws Exception {
		this.verifyReadWrite(new InsuranceProject().getProject(), new InsuranceProject().getProject());
	}
	
	public void testPhoneCompanyProject() throws Exception {
		this.verifyReadWrite(new PhoneCompanyProject(false).getProject(), new PhoneCompanyProject(false).getProject());
	}
	
	public void testPhoneCompanyProjectWithSharedAggregates() throws Exception {
		this.verifyReadWrite(new PhoneCompanyProject(true).getProject(), new PhoneCompanyProject(true).getProject());
	}
	
	public void testQueryProject() throws Exception {
		this.verifyReadWrite(new QueryProject().getProject(), new QueryProject().getProject());
	}
	
	public void testRelationalEmployeeProject() throws Exception {
		this.verifyReadWrite(new EmployeeProject().getProject(), new EmployeeProject().getProject());
	}
	
	public void testEmployeeOXProject() throws Exception {
		this.verifyReadWrite(new EmployeeOXProject().getProject(), new EmployeeOXProject().getProject());
	}
	
	public void testEmployeeEisProject() throws Exception {
		this.verifyReadWrite(new EmployeeEisProject().getProject(), new EmployeeEisProject().getProject());
	}
	
	public void testEmployeeJaxbProject() throws Exception {
		this.verifyReadWrite(new EmployeeJAXBProject().getProject(), new EmployeeJAXBProject().getProject());
	}
	
	private void verifyReadWrite(MWProject project1, MWProject project2) throws Exception {
		project1.buildBasicTypes();
		project2.buildBasicTypes();
		// set the save directory to an empty temporary directory
		project1.setSaveDirectory(FileTools.emptyTemporaryDirectory(ClassTools.shortClassNameForObject(this) + "." + this.getName()));
		project2.setSaveDirectory(project1.getSaveDirectory());

		ProjectIOManager ioMgr = new ProjectIOManager();
		ioMgr.write(project2);

		// re-read the project and compare it to the original
		project2 = ioMgr.read(project2.saveFile(), NullPreferences.instance());

		DiffEngine diffEngine = MappingsModelTestTools.buildDiffEngine();
		diffEngine.setLog(this.buildDiffEngineLog());
		Diff diff = diffEngine.diff(project1, project2);

		// sometimes we dump the branch size so we can compare it to the
		// number of objects compared by the diff engine
		// @see DiffEngine.RecordingDifferentiator#tearDown()
		// System.out.println("project size: " + org.eclipse.persistence.tools.workbench.utility.CollectionTools.size(project1.allNodes()));

		assertTrue(diff.getDescription(), diff.identical());
	}

	/**
	 * build a log that can help with debugging breakpoints
	 */
	private DiffEngine.Log buildDiffEngineLog() {
		return new DiffEngine.Log() {
			public void log(Diff diff) {
				Object object = diff.getObject1();
				if (object instanceof MWMethod) {
					MWMethod method = (MWMethod) object;
					if (method.getName().equals("setImage")) {
						System.getSecurityManager();
					}
				}
			}
			public void close() {
				// do nothing
			}
		};
	}

}
