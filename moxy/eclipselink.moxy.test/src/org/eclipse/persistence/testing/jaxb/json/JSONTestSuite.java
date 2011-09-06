/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - August 2011 - 2.4 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.json;

import org.eclipse.persistence.testing.jaxb.json.attribute.JSONAttributePrefixEmptyStringTestCases;
import org.eclipse.persistence.testing.jaxb.json.attribute.JSONAttributePrefixOnContextTestCases;
import org.eclipse.persistence.testing.jaxb.json.attribute.JSONAttributePrefixOnMarshallerTestCases;

import junit.framework.Test;
import junit.framework.TestSuite;

public class JSONTestSuite extends TestSuite {
    public static Test suite() {
    	  TestSuite suite = new TestSuite("JSONTestSuite");
          suite.addTestSuite(JSONAttributePrefixOnContextTestCases.class);
          suite.addTestSuite(JSONAttributePrefixEmptyStringTestCases.class);
          suite.addTestSuite(JSONAttributePrefixOnMarshallerTestCases.class);
          return suite;
	}
}
	