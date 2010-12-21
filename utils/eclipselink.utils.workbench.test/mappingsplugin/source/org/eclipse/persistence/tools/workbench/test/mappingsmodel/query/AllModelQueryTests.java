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
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.query;


import org.eclipse.persistence.tools.workbench.test.mappingsmodel.query.xml.MWEisInteractionTests;

import junit.framework.*;

/**
 * decentralize test creation code
 */
public class AllModelQueryTests {

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
