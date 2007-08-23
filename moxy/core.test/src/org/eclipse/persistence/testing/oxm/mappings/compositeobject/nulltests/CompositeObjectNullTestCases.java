/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.compositeobject.nulltests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class CompositeObjectNullTestCases{ 

	public static Test suite() {
    TestSuite suite = new TestSuite("Composite Object Null Test Cases");
		suite.addTestSuite(CompositeObjectNullObjectTests.class);		
		suite.addTestSuite(CompositeObjectEmptyObjectTests.class);
		suite.addTestSuite(CompositeObjectNullElementsTests.class);
    return suite;
  }

}
