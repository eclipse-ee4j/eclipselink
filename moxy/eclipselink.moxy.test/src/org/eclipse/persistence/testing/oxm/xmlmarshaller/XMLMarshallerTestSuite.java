/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.xmlmarshaller;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.eclipse.persistence.testing.oxm.xmlmarshaller.setschemas.SetSchemasTestCases;
import org.eclipse.persistence.testing.oxm.xmlmarshaller.setschemas.SetXmlSchemaTestCases;
import org.eclipse.persistence.testing.oxm.xmlmarshaller.stax.UnmarshalLevelTestCases;
import org.eclipse.persistence.testing.oxm.xmlmarshaller.structureValidation.StructureValidationTestSuite;

public class XMLMarshallerTestSuite extends TestCase {
    public XMLMarshallerTestSuite(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("XMLMarshaller Test Suite");
        String platform = System.getProperty("eclipselink.xml.platform");
        if (null == platform) {
            platform = "org.eclipse.persistence.platform.xml.jaxp.JAXPPlatform";
        }
        boolean jaxpPlatform = platform.equalsIgnoreCase("org.eclipse.persistence.platform.xml.jaxp.JAXPPlatform");

        suite.addTestSuite(XMLMarshallerCreateTestCases.class);
        suite.addTestSuite(XMLMarshalTestCases.class);
        suite.addTestSuite(XMLMarshalNSTestCases.class);
        suite.addTestSuite(XMLMarshalExceptionTestCases.class);
        suite.addTestSuite(XMLMarshalFragmentTestCases.class);
        suite.addTestSuite(XMLUnmarshalTestCases.class);
        if (!jaxpPlatform) {
            suite.addTestSuite(XMLMarshallerValidateRootTestCases.class);
        }
        suite.addTestSuite(XMLMarshallerValidationModeTestCases.class);
        suite.addTestSuite(SetSchemasTestCases.class);
        suite.addTestSuite(SetXmlSchemaTestCases.class);
        suite.addTest(StructureValidationTestSuite.suite());
        suite.addTestSuite(XMLUnmarshallerHandlerTestCases.class);
        suite.addTestSuite(XMLMarshallerNoDefaultRootTestCases.class);
        suite.addTestSuite(XMLContextConstructorUsingXMLSessionConfigLoader.class);
        suite.addTestSuite(XMLContextStoreXMLDescriptorSimpleType.class);
        suite.addTestSuite(UnmarshalChildElementNSTestCases.class);
        suite.addTestSuite(UnmarshalLevelTestCases.class);
        suite.addTestSuite(XMLMarshallerContentHandlerTestCases.class);
        return suite;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.xmlmarshaller.XMLMarshallerTestSuite" };
        TestRunner.main(arguments);
    }
}
