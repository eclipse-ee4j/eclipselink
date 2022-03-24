/*
 * Copyright (c) 1998, 2022 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.jaxb;

import org.eclipse.persistence.testing.jaxb.events.ExternalMetadataTestCases;
import org.eclipse.persistence.testing.jaxb.events.JAXBElementTestCases;
import org.eclipse.persistence.testing.jaxb.events.RootWithCompositeObjectTestCases;
import org.eclipse.persistence.testing.jaxb.inheritance.JAXBInheritanceTestCases;
import org.eclipse.persistence.testing.jaxb.inheritance.dot.InheritanceDotTestCases;
import org.eclipse.persistence.testing.jaxb.inheritance.id.JAXBInheritanceIdTestCases;
import org.eclipse.persistence.testing.jaxb.inheritance.ns.JAXBInheritanceNSSeparatorTestCases;
import org.eclipse.persistence.testing.jaxb.inheritance.ns.JAXBInheritanceNSTestCases;
import org.eclipse.persistence.testing.jaxb.inheritance.ns.JAXBInheritanceSubTypeNoParentRootTestCases;
import org.eclipse.persistence.testing.jaxb.inheritance.ns.JAXBInheritanceSubTypeParentRootOnlyTestCases;
import org.eclipse.persistence.testing.jaxb.inheritance.ns.JAXBInheritanceSubTypeTestCases;
import org.eclipse.persistence.testing.jaxb.inheritance.simple.XmlValueInheritanceTestCases;
import org.eclipse.persistence.testing.jaxb.jaxbelement.complex.JAXBElementComplexTestCases;
import org.eclipse.persistence.testing.jaxb.jaxbelement.dom.ElementTestCases;
import org.eclipse.persistence.testing.jaxb.jaxbelement.dom.TextNodeTestCases;
import org.eclipse.persistence.testing.jaxb.jaxbelement.dom.nofactory.DocumentTestCases;
import org.eclipse.persistence.testing.jaxb.jaxbelement.dom.nofactory.ElementEmptyTestCases;
import org.eclipse.persistence.testing.jaxb.jaxbelement.dom.nofactory.ElementFragmentTestCases;
import org.eclipse.persistence.testing.jaxb.jaxbelement.dom.nofactory.ElementNoNamespaceTestCases;
import org.eclipse.persistence.testing.jaxb.jaxbelement.dom.nofactory.ElementSameNamespaceTestCases;
import org.eclipse.persistence.testing.jaxb.jaxbelement.enumeration.JAXBElementEnumTestCases;
import org.eclipse.persistence.testing.jaxb.jaxbelement.nested.JAXBElementNestedTestCases;
import org.eclipse.persistence.testing.jaxb.jaxbelement.nil.JAXBElementNilTestCases;
import org.eclipse.persistence.testing.jaxb.jaxbelement.simple.JAXBElementBase64TestCases;
import org.eclipse.persistence.testing.jaxb.jaxbelement.simple.JAXBElementDataHandlerTestCases;
import org.eclipse.persistence.testing.jaxb.jaxbelement.simple.JAXBElementSimpleTestCases;
import org.eclipse.persistence.testing.jaxb.jaxbelement.subclass.JAXBElementSubclassEnumTestCases;
import org.eclipse.persistence.testing.jaxb.jaxbelement.subclass.JAXBElementSubclassObjectTestCases;
import org.eclipse.persistence.testing.jaxb.jaxbelement.subclass.JAXBElementSubclassTestCases;
import org.eclipse.persistence.testing.jaxb.xmlenum.xmlvalue.XmlEnumXmlValueTestCases;
import org.eclipse.persistence.testing.jaxb.xmlmarshaller.JAXBDOMTestSuite;
import org.eclipse.persistence.testing.jaxb.xmlmarshaller.JAXBSAXTestSuite;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class JAXBTestSuite2B extends TestCase {

    public static Test suite() {
        TestSuite suite = new TestSuite("JAXB20 Compiler Test Suite 2B");
        suite.addTestSuite(XmlEnumXmlValueTestCases.class);
        suite.addTestSuite(TextNodeTestCases.class);
        suite.addTestSuite(ElementTestCases.class);
        suite.addTestSuite(JAXBElementSimpleTestCases.class);
        suite.addTestSuite(JAXBElementNestedTestCases.class);
        suite.addTestSuite(JAXBElementComplexTestCases.class);
        suite.addTestSuite(ElementTestCases.class);
        suite.addTestSuite(ElementEmptyTestCases.class);
        suite.addTestSuite(ElementNoNamespaceTestCases.class);
        suite.addTestSuite(ElementSameNamespaceTestCases.class);
        suite.addTestSuite(ElementFragmentTestCases.class);
        suite.addTestSuite(DocumentTestCases.class);
        suite.addTestSuite(JAXBElementNilTestCases.class);
        suite.addTestSuite(JAXBElementBase64TestCases.class);
        suite.addTestSuite(JAXBElementDataHandlerTestCases.class);
        suite.addTestSuite(JAXBElementSubclassTestCases.class);
        suite.addTestSuite(JAXBElementSubclassEnumTestCases.class);
        suite.addTestSuite(JAXBElementSubclassObjectTestCases.class);
        suite.addTestSuite(RootWithCompositeObjectTestCases.class);
        suite.addTestSuite(JAXBElementEnumTestCases.class);
        suite.addTestSuite(JAXBInheritanceTestCases.class);
        suite.addTestSuite(InheritanceDotTestCases.class);
        suite.addTestSuite(XmlValueInheritanceTestCases.class);
        suite.addTestSuite(JAXBInheritanceNSTestCases.class);
        suite.addTestSuite(JAXBInheritanceSubTypeTestCases.class);
        suite.addTestSuite(JAXBInheritanceSubTypeNoParentRootTestCases.class);
        suite.addTestSuite(JAXBInheritanceSubTypeParentRootOnlyTestCases.class);
        suite.addTestSuite(JAXBInheritanceNSSeparatorTestCases.class);
        suite.addTestSuite(JAXBInheritanceIdTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.events.RootWithCompositeCollectionTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.events.ClassLoaderTestCases.class);
        suite.addTestSuite(ExternalMetadataTestCases.class);
        suite.addTestSuite(JAXBElementTestCases.class);
        suite.addTest(JAXBDOMTestSuite.suite());
        suite.addTest(JAXBSAXTestSuite.suite());
        return suite;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.jaxb.JAXBTestSuite2B" };
        // junit.swingui.TestRunner.main(arguments);
        // System.setProperty("useLogging", "true");
        junit.textui.TestRunner.main(arguments);
    }

}
