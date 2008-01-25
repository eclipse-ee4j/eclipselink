/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.query;


import org.eclipse.persistence.tools.workbench.test.mappingsmodel.query.xml.MWEisInteractionTests;

import junit.framework.*;

/**
 * decentralize test creation code
 */
public class AllModelQueryTests {

public static void main(String[] args) {
	junit.swingui.TestRunner.main(new String[] {"-c", AllModelQueryTests.class.getName()});
}

public static Test suite() {
	TestSuite suite = new TestSuite("test.org.eclipse.persistence.tools.workbench.mappingsmodel.query");

	suite.addTest(MWUserDefinedQueryKeyTests.suite());
	suite.addTest(MWQueryableTests.suite());
	suite.addTest(MWQueryTests.suite());
	suite.addTest(MWExpressionTests.suite());
	suite.addTest(MWExpressionUndoableTests.suite());
	suite.addTest(MWEisInteractionTests.suite());

	return suite;
}

/**
 * suppress instantiation
 */
private AllModelQueryTests(String name) {
	super();
}
	
}
