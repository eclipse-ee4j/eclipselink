/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     rbarkhouse - 2010-03-04 12:22:11 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.dynamic;

import org.eclipse.persistence.testing.jaxb.dynamic.sessioneventlistener.SessionEventListenerTestCases;
import org.eclipse.persistence.testing.jaxb.dynamic.withstatic.DynamicWithStaticAddressNoPropsTestCases;
import org.eclipse.persistence.testing.jaxb.dynamic.withstatic.DynamicWithStaticOverrideTestCases;
import org.eclipse.persistence.testing.jaxb.dynamic.withstatic.DynamicWithStaticTestCases;
import org.eclipse.persistence.testing.jaxb.dynamic.withstatic.StaticWithDynamicTestCases;

import junit.framework.Test;
import junit.framework.TestSuite;

public class DynamicJAXBTestSuite extends TestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite("Dynamic JAXB Test Suite");
        suite.addTestSuite(DynamicJAXBContextCreationTestCases.class);
        suite.addTestSuite(DynamicJAXBFromSessionsXMLTestCases.class);
        suite.addTestSuite(DynamicJAXBFromXSDTestCases.class);
        suite.addTestSuite(DynamicJAXBFromOXMTestCases.class);
        suite.addTestSuite(DynamicJAXBCollectionTestCases.class);
        suite.addTestSuite(DynamicJAXBUsingXMLNamesTestCases.class);
        suite.addTestSuite(DynamicJAXBRefreshTestCases.class);
        suite.addTestSuite(EmptyContextTestCases.class);
        suite.addTestSuite(DynamicWithStaticTestCases.class);
        suite.addTestSuite(DynamicWithStaticAddressNoPropsTestCases.class);
        suite.addTestSuite(DynamicWithStaticOverrideTestCases.class);
        suite.addTestSuite(StaticWithDynamicTestCases.class);
        suite.addTestSuite(SessionEventListenerTestCases.class);
        return suite;
    }

}