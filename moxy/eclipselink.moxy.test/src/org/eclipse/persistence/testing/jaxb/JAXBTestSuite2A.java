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

import org.eclipse.persistence.testing.jaxb.xmlenum.InvalidEnumValueTestCases;
import org.eclipse.persistence.testing.jaxb.xmlenum.XmlEnumRootElemTestCases;
import org.eclipse.persistence.testing.jaxb.xmlidref.XmlIdRefTestCases;
import org.eclipse.persistence.testing.jaxb.xmlidref.array.XmlIdRefArrayTestCases;
import org.eclipse.persistence.testing.jaxb.xmlidref.inheritance.XmlIdRefInheritanceTestCases;
import org.eclipse.persistence.testing.jaxb.xmlidref.object.XmlIdRefObjectTestCases;
import org.eclipse.persistence.testing.jaxb.xmlidref.object.XmlIdRefObjectWhitespaceTestCases;
import org.eclipse.persistence.testing.jaxb.xmlidref.self.XmlIdRefSelfObjectTestCases;
import org.eclipse.persistence.testing.jaxb.xmlidref.xmlelements.XmlElementsIdRefTestCases;
import org.eclipse.persistence.testing.jaxb.xmlidref.xmlelements.XmlElementsSingleIdRefTestCases;
import org.eclipse.persistence.testing.jaxb.xmlidref.xmlelements.interfaces.XmlElementsIdRefInterfaceTestCases;
import org.eclipse.persistence.testing.jaxb.xmlidref.xmlelements.wrapper.XmlElementsWrapperIdRefTestCases;
import org.eclipse.persistence.testing.jaxb.xmlidrefs.XmlIdRefsTestCases;
import org.eclipse.persistence.testing.jaxb.xmlidrefs.object.XmlIdRefsObjectTestCases;
import org.eclipse.persistence.testing.jaxb.xmlinlinebinary.InlineDataHandlerCollectionTestCases;
import org.eclipse.persistence.testing.jaxb.xmlinlinebinary.LargeInlineBinaryTestCases;
import org.eclipse.persistence.testing.jaxb.xmlinlinebinary.XmlInlineBinaryDataTestCases;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class JAXBTestSuite2A extends TestCase {

    public static Test suite() {
        TestSuite suite = new TestSuite("JAXB20 Compiler Test Suite 2A");
        // jaxb-mats bug due to time/timestamp changes to TopLink after 070609
        // suite.addTestSuite(XmlSchemaTypeDateTestCases.class);
        // suite.addTestSuite(XmlSchemaTypeTwoDatesTestCases.class);
        suite.addTestSuite(XmlIdRefTestCases.class);
        suite.addTestSuite(XmlIdRefArrayTestCases.class);
        suite.addTestSuite(XmlIdRefObjectTestCases.class);
        suite.addTestSuite(XmlIdRefObjectWhitespaceTestCases.class);
        suite.addTestSuite(XmlIdRefInheritanceTestCases.class);
        suite.addTestSuite(XmlIdRefSelfObjectTestCases.class);
        suite.addTestSuite(XmlIdRefsTestCases.class);
        suite.addTestSuite(XmlIdRefsObjectTestCases.class);
        suite.addTestSuite(XmlElementsIdRefTestCases.class);
        suite.addTestSuite(XmlElementsSingleIdRefTestCases.class);
        suite.addTestSuite(XmlElementsIdRefInterfaceTestCases.class);
        suite.addTestSuite(XmlElementsWrapperIdRefTestCases.class);
        suite.addTestSuite(XmlInlineBinaryDataTestCases.class);
        suite.addTestSuite(InlineDataHandlerCollectionTestCases.class);
        suite.addTestSuite(LargeInlineBinaryTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlenum.EnumSwitchTestCases.class);
        suite.addTestSuite(InvalidEnumValueTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlenum.XmlEnumElementTestCases.class);
        suite.addTestSuite(XmlEnumRootElemTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlenum.XmlEnumAttributeTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlenum.XmlEnumElementCollectionTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlenum.XmlEnumElementArrayTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlenum.XmlEnumAttributeCollectionTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlenum.XmlValueAnnotationWithEnumTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlenum.XmlEnumChoiceObjectTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlenum.XmlEnumChoiceCollectionTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlenum.SpaceTestCases.class);
        return suite;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.jaxb.JAXBTestSuite2A" };
        // junit.swingui.TestRunner.main(arguments);
        // System.setProperty("useLogging", "true");
        junit.textui.TestRunner.main(arguments);
    }

}
