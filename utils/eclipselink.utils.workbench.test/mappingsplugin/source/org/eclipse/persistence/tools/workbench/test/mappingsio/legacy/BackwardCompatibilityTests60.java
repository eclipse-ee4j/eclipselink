/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.test.mappingsio.legacy;

import org.eclipse.persistence.tools.workbench.test.models.projects.ComplexAggregateProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.LegacyEmployeeOXProject;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.swingui.TestRunner;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml.MWXmlProject;

public class BackwardCompatibilityTests60 extends
		BackwardCompatibilityInternalTestCase {

	public static void main(String[] args) {
		TestRunner.main(new String[] {"-c", BackwardCompatibilityTests60.class.getName()});
	
	//	to run a single test, comment out the line of code above
	//	and uncomment the following line of code:
//		System.out.println(TestTools.execute(new BackwardCompatibilityTests50("testComplexAggregate")));

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
    //      testCorruptArrayProject
	}

	public static Test suite() {
		return new TestSuite(BackwardCompatibilityTests60.class);
	}
	
	public BackwardCompatibilityTests60(String name) {
		super(name);
	}

	public void testXmlEmployee() throws Exception {
		this.compareToOldProject(this.buildLegacyEmployeeXmlProject());
	}

	protected MWXmlProject buildLegacyEmployeeXmlProject() throws Exception {
		return new LegacyEmployeeOXProject().getProject();
	}

	@Override
	protected MWRelationalProject buildComplexAggregateProject()
			throws Exception {
		return new ComplexAggregateProject().getProject();
	}

	@Override
	protected String version() {
		return "6.0";
	}

}
