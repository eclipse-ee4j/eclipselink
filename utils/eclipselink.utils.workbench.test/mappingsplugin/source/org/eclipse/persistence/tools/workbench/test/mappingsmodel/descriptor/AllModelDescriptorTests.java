/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.descriptor;

import org.eclipse.persistence.tools.workbench.test.mappingsmodel.descriptor.xml.MWEisDescriptorTests;
import org.eclipse.persistence.tools.workbench.test.mappingsmodel.descriptor.xml.MWOXDescriptorTests;
import org.eclipse.persistence.tools.workbench.test.mappingsmodel.descriptor.xml.MWXmlDescriptorTests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;


/**
 * decentralize test creation code
 */
public class AllModelDescriptorTests {

	public static void main(String[] args) {
		junit.swingui.TestRunner.main(new String[] {"-c", AllModelDescriptorTests.class.getName()});
	}
	
	public static Test suite() {
		TestSuite suite = new TestSuite(ClassTools.packageNameFor(AllModelDescriptorTests.class));

		suite.addTest(MWAggregateDescriptorTests.suite());
		suite.addTest(MWDescriptorTests.suite());
		suite.addTest(MWMappingDescriptorTests.suite());
		suite.addTest(MWTableDescriptorTests.suite());
		suite.addTest(MWXmlDescriptorTests.suite());
		suite.addTest(MWEisDescriptorTests.suite());
		suite.addTest(MWOXDescriptorTests.suite());
		suite.addTest(MWInterfaceDescriptorTests.suite());
		suite.addTest(MWDescriptorReturningPolicyTests.suite());
	
		return suite;
	}
	
	/**
	 * suppress instantiation
	 */
	private AllModelDescriptorTests() {
		super();
	}
	
}
