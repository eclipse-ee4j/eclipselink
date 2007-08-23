/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.platform;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class PlatformTestSuite extends TestCase {

	public static Test suite() {
		TestSuite suite = new TestSuite("Platform Test Suite");
		suite.addTestSuite(PlatformCreateDocumentTestCases.class);
		suite.addTestSuite(PlatformNamespaceTestCases.class);
		suite.addTestSuite(PlatformTransformerTestCases.class);
		suite.addTestSuite(PlatformWhitespaceNodeTestCases.class);
		suite.addTestSuite(XMLComparerTestCases.class);
		suite.addTestSuite(PlatformAdvancedXPathTestCases.class);
		suite.addTestSuite(PlatformValidateDocumentTestCases.class);

		return suite;
	}

}