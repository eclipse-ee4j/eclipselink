/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.mappingsio.legacy;

import org.eclipse.persistence.tools.workbench.test.models.projects.CorruptArrayProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.LegacyComplexAggregateProject45;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.swingui.TestRunner;

import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWDatabase;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.utility.diff.DiffEngine;
import org.eclipse.persistence.tools.workbench.utility.diff.ReflectiveDifferentiator;


public class BackwardCompatibilityTests45 extends BackwardCompatibilityInternalTestCase {

	public static void main(String[] args) {
		TestRunner.main(new String[] {"-c", BackwardCompatibilityTests45.class.getName()});
	
	//	to run a single test, comment out the line of code above
	//	and uncomment the following line of code:
//		System.out.println(TestTools.execute(new BackwardCompatibilityTests45("testComplexAggregate")));
	
	//	current tests:
	//		testComplexAggregate
	//		testComplexInheritance
	//		testComplexMapping
	//		testEjb20Employee
	//		testEmployee
	//		testInsurance
	//		testPhoneCompany
	//		testReadOnly
	//		testSimpleAggregate
	//		testSimpleContact
	//		testCorruptArrayProject
	}
	
	public static Test suite() {
		return new TestSuite(BackwardCompatibilityTests45.class);
	}
	
	public BackwardCompatibilityTests45(String name) {
		super(name);
	}
	
	protected String version() {
		return "4.5";
	}

	protected MWRelationalProject buildComplexAggregateProject() throws Exception {
		return new LegacyComplexAggregateProject45().getProject();
	}

	public void testCorruptArrayProject() throws Exception {
		MWProject expectedProject = new CorruptArrayProject().getProject();
		MWProject oldProject = this.readOldProjectFor(expectedProject);

		MWTableDescriptor descriptor = (MWTableDescriptor) oldProject.descriptorNamed("test.oracle.models.corruptarray.CorruptArray");
		descriptor.getMWClass().attributeNamed("blobField").generateAllAccessors();
		descriptor.getMWClass().attributeNamed("clobField").generateAllAccessors();

		this.compareProjects(expectedProject, oldProject);
	}

	protected DiffEngine buildDiffEngine() {
		DiffEngine de = super.buildDiffEngine();

		ReflectiveDifferentiator rd;

		rd = (ReflectiveDifferentiator) de.getUserDifferentiator(MWDatabase.class);
			// we convert 4.5 (9.0.3) Oracle platform to Oracle8i
			rd.ignoreFieldsNamed("databasePlatform");

		return de;
	}

}
