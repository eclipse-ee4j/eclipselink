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
import org.eclipse.persistence.tools.workbench.test.models.projects.LegacyComplexAggregateProject50;
import org.eclipse.persistence.tools.workbench.test.models.projects.LegacyQueryProject50;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.swingui.TestRunner;

import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;


public class BackwardCompatibilityTests50 extends BackwardCompatibilityInternalTestCase {

	public static void main(String[] args) {
		TestRunner.main(new String[] {"-c", BackwardCompatibilityTests50.class.getName()});
	
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
		return new TestSuite(BackwardCompatibilityTests50.class);
	}
	
	public BackwardCompatibilityTests50(String name) {
		super(name);
	}
	
	protected String version() {
		return "5.0";
	}

	protected MWRelationalProject buildComplexAggregateProject() throws Exception {
		return new LegacyComplexAggregateProject50().getProject();
	}
	
	public void testQueryProject() throws Exception {
		this.compareToOldProject(this.buildQueryProject());
	}
	
	protected MWRelationalProject buildQueryProject() throws Exception {
		return new LegacyQueryProject50().getProject();
	}
    
    public void testCorruptArrayProject() throws Exception {
        this.compareToOldProject(new CorruptArrayProject().getProject());
    }
}
