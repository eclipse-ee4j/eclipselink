/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.schemareference;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.oxm.schemareference.unmarshal.EmployeeWithDefaultRootElementTestCases;
import org.eclipse.persistence.testing.oxm.schemareference.unmarshal.EmployeeWithoutDefaultRootElementTestCases;

public class XMLSchemaReferenceTestSuite extends TestCase {
    public XMLSchemaReferenceTestSuite(String name) {
        super(name);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.main(new String[] { "-c", "org.eclipse.persistence.testing.oxm.schemareference.XMLSchemaReferenceTestSuite" });
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("XMLSchemaReference Test Suite");
        String platform = System.getProperty("eclipselink.xml.platform");
        if (null == platform) {
            platform = "org.eclipse.persistence.platform.xml.jaxp.JAXPPlatform";
        }
        boolean jaxpPlatform = platform.equalsIgnoreCase("org.eclipse.persistence.platform.xml.jaxp.JAXPPlatform");

        if (!jaxpPlatform) {
            suite.addTestSuite(XMLSchemaURLReferenceTests.class);
            suite.addTestSuite(XMLSchemaFileReferenceTests.class);
            suite.addTestSuite(XMLSchemaCPReferenceTests.class);
            suite.addTestSuite(SchemaReferenceErrorHandlerTests.class);
            suite.addTestSuite(EmployeeWithoutDefaultRootElementTestCases.class);
            suite.addTestSuite(EmployeeWithDefaultRootElementTestCases.class);
        }
        return suite;
    }
}
