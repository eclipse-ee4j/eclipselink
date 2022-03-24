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

import org.eclipse.persistence.testing.jaxb.interfaces.xmltransient.InvalidTransientInterfaceTestCases;
import org.eclipse.persistence.testing.jaxb.stax.XMLStreamReaderEndEventTestCases;
import org.eclipse.persistence.testing.jaxb.stax.XMLStreamWriterDefaultNamespaceTestCases;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class JAXBTestSuite3A extends TestCase {

    public static Test suite() {
        TestSuite suite = new TestSuite("JAXB20 Compiler Test Suite 3A");
        suite.addTest(org.eclipse.persistence.testing.jaxb.singleobject.JAXBSingleObjectTestSuite.suite());
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.classloader.DifferentClassLoaderTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.classloader.InnerClassTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.classloader.XmlElementsEnumTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.helper.JAXBHelperTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.any.AnyWithJAXBElementTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.interfaces.InterfaceTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.interfaces.choice.InterfaceChoiceTestCases.class);
        suite.addTestSuite(InvalidTransientInterfaceTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.inheritance.interfaces.InterfacesTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlmarshaller.MarshalSchemaValidationTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlmarshaller.NoSchemaRefTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlmarshaller.UnmarshalSchemaValidationTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlmarshaller.UnmarshallerNullTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlmarshaller.NoSchemaRefTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlmarshaller.HandleListenerExceptionsTestCases.class);
        suite.addTestSuite(XMLStreamWriterDefaultNamespaceTestCases.class);
        suite.addTestSuite(XMLStreamReaderEndEventTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.qname.QNameTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.qname.defaultnamespace.QNameTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlmarshaller.locator.AnyTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlmarshaller.locator.AnyCollectionTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlmarshaller.locator.ElementTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlmarshaller.locator.ElementCollectionTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmltype.XmlTypeTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmltype.XmlTypeNameTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmltype.proporder.NonTransientTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmltype.proporder.TransientTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmltype.proporder.ExtraPropTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmltype.proporder.MissingPropTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlaccessortype.none.NoneTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlaccessortype.FieldAndPropertyTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlaccessortype.IgnoreInvalidNonPublicFieldTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlvirtualaccessmethods.XmlVirtualAccessMethodsTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlschematype.XmlSchemaTypeDateTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlschematype.XmlSchemaTypeDateEmptyTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlschematype.XmlSchemaTypeTwoDatesTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlschematype.NonNegativeIntegerSchemaTypeTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlschematype.CharTestCases.class);
        return suite;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.jaxb.JAXBTestSuite3A" };
        // junit.swingui.TestRunner.main(arguments);
        // System.setProperty("useLogging", "true");
        junit.textui.TestRunner.main(arguments);
    }

}
