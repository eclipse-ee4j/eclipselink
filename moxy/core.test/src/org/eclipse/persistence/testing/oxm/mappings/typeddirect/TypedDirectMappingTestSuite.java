/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.typeddirect;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class TypedDirectMappingTestSuite extends TestCase {

	public static Test suite() {
		TestSuite suite = new TestSuite("Typed Direct Mappings Suite");
		suite.addTestSuite(TypedDirectMappingTestCases.class);
		suite.addTestSuite(TypedDirectErrorTestCases.class);

		return suite;
	}

}