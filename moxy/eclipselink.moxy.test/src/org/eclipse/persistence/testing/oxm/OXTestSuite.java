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
package org.eclipse.persistence.testing.oxm;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.eclipse.persistence.testing.oxm.descriptor.primarykey.PrimaryKeyTestSuite;
import org.eclipse.persistence.testing.oxm.descriptor.rootelement.RootElementTestSuite;
import org.eclipse.persistence.testing.oxm.mappings.DeploymentXMLMappingTestSuite;
import org.eclipse.persistence.testing.oxm.mappings.MappingTestSuite;
import org.eclipse.persistence.testing.oxm.xpathengine.XPathEngineTestSuite;
import org.eclipse.persistence.testing.oxm.xmllogin.XMLLoginTestSuite;
import org.eclipse.persistence.testing.oxm.xmlmarshaller.XMLMarshallerTestSuite;
import org.eclipse.persistence.testing.oxm.schemamodelgenerator.GenerateSchemaTestSuite;
import org.eclipse.persistence.testing.oxm.schemareference.XMLSchemaReferenceTestSuite;
import org.eclipse.persistence.testing.oxm.inheritance.InheritanceTestSuite;
import org.eclipse.persistence.testing.oxm.converter.ConverterTestSuite;
import org.eclipse.persistence.testing.oxm.documentpreservation.DocumentPreservationTestSuite;
import org.eclipse.persistence.testing.oxm.readonly.ReadOnlyTestSuite;
import org.eclipse.persistence.testing.oxm.platform.PlatformTestSuite;
import org.eclipse.persistence.testing.oxm.xmlbinder.XMLBinderTestSuite;
import org.eclipse.persistence.testing.oxm.xmlbinder.basictests.XMLBinderBasicTestCases;
import org.eclipse.persistence.testing.oxm.xmlcontext.byxpath.XMLContextByXPathTestSuite;
import org.eclipse.persistence.testing.oxm.xmlconversionmanager.XMLConversionManagerTestSuite;
import org.eclipse.persistence.testing.oxm.xmlroot.XMLRootTestSuite;

public class OXTestSuite extends TestCase {
    public OXTestSuite(String name) {
        super(name);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.main(new String[] { "-c", "org.eclipse.persistence.testing.oxm.OXTestSuite" });
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("OX Test Suite");
        String platform = System.getProperty("eclipselink.xml.platform");
        if (null == platform) {
            platform = "org.eclipse.persistence.platform.xml.jaxp.JAXPPlatform";
        }
        boolean jaxpPlatform = platform.equalsIgnoreCase("org.eclipse.persistence.platform.xml.jaxp.JAXPPlatform");

        suite.addTest(RootElementTestSuite.suite());
        suite.addTest(XPathEngineTestSuite.suite());
        suite.addTest(XMLMarshallerTestSuite.suite());
        if (!jaxpPlatform) {
            suite.addTest(XMLSchemaReferenceTestSuite.suite());
        }
        suite.addTest(InheritanceTestSuite.suite());
        suite.addTest(ConverterTestSuite.suite());
        suite.addTest(DocumentPreservationTestSuite.suite());
        suite.addTest(ReadOnlyTestSuite.suite());
        suite.addTest(PlatformTestSuite.suite());
        suite.addTest(XMLConversionManagerTestSuite.suite());
        suite.addTest(XMLBinderTestSuite.suite());

        suite.addTest(XMLRootTestSuite.suite());

        suite.addTest(XMLLoginTestSuite.suite());
        suite.addTest(GenerateSchemaTestSuite.suite());

        suite.addTest(XMLContextByXPathTestSuite.suite());

        suite.addTest(PrimaryKeyTestSuite.suite());

        //this suite does not include the JAXBDOM and JAXBSAX test suites as they need system properties set to run.
        return suite;
    }
}
