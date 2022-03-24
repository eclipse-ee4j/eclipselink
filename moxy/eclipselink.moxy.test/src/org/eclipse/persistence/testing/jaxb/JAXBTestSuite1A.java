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

import org.eclipse.persistence.testing.jaxb.jaxbcontext.JAXBContextMediaTypeTestCases;
import org.eclipse.persistence.testing.jaxb.namespaceuri.xml.XMLNamespaceTestCases;
import org.eclipse.persistence.testing.jaxb.namespaceuri.xml.XMLNamespaceXmlPathTestCases;
import org.eclipse.persistence.testing.jaxb.nomappings.NoMappingsTestCases;
import org.eclipse.persistence.testing.jaxb.xmlvalue.XmlValueByteArrayTestCases;
import org.eclipse.persistence.testing.jaxb.xmlvalue.XmlValueByteArrayWithIdTestCases;
import org.eclipse.persistence.testing.jaxb.xmlvalue.XmlValueListTestCases;
import org.eclipse.persistence.testing.jaxb.xmlvalue.XmlValueTestCases;
import org.eclipse.persistence.testing.jaxb.xmlvalue.XmlValueWithAttributesTestCases;
import org.eclipse.persistence.testing.jaxb.xmlvalue.XmlValueWithAttributesXpathTestCases;
import org.eclipse.persistence.testing.jaxb.xmlvalue.XmlValueWithNullAttrTestCases;
import org.eclipse.persistence.testing.jaxb.xmlvalue.none.InvalidTestCases;
import org.eclipse.persistence.testing.jaxb.xmlvalue.none.ValidTestCases;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class JAXBTestSuite1A extends TestCase {

    public static Test suite() {
        TestSuite suite = new TestSuite("JAXB20 Compiler Test Suite 1A");
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.employee.JAXBEmployeeTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.employee.EmployeeNamespaceCancellationTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.employee.EmployeeNullInCollectionTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.emptystring.EmptyStringTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.emptystring.ListsTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlrootelement.InnerClassTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlrootelement.XmlRootElementNamespaceTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlrootelement.XmlRootElementNoNamespaceTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlrootelement.XmlRootElementNilTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlrootelement.DotTestCases.class);
        suite.addTest(org.eclipse.persistence.testing.jaxb.xmlelement.XmlElementTestSuite.suite());
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlattribute.XmlAttributeNamespaceTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlattribute.XmlAttributeNoNamespaceTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlattribute.XmlAttributeCollectionTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlattribute.emptynamespace.XmlAttributeEmptyNamespaceTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlattribute.imports.XmlAttributeImportsTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlattribute.unqualified.AttributeFormUnqualifiedTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlattribute.ObjectTestCases.class);
        suite.addTestSuite(XmlValueTestCases.class);
        suite.addTestSuite(XmlValueByteArrayTestCases.class);
        suite.addTestSuite(XmlValueByteArrayWithIdTestCases.class);
        suite.addTestSuite(XmlValueWithAttributesTestCases.class);
        suite.addTestSuite(XmlValueWithAttributesXpathTestCases.class);
        suite.addTestSuite(XmlValueWithNullAttrTestCases.class);
        suite.addTestSuite(XmlValueListTestCases.class);
        suite.addTestSuite(InvalidTestCases.class);
        suite.addTestSuite(ValidTestCases.class);
        suite.addTestSuite(XMLNamespaceTestCases.class);
        suite.addTestSuite(XMLNamespaceXmlPathTestCases.class);
        suite.addTestSuite(NoMappingsTestCases.class);
        suite.addTestSuite(JAXBContextMediaTypeTestCases.class);
        return suite;

    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.jaxb.JAXBTestSuite1A" };
        // junit.swingui.TestRunner.main(arguments);
        // System.setProperty("useLogging", "true");
        junit.textui.TestRunner.main(arguments);
    }

}
