/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Blaise Doughan - 2.4 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmlelement;

import org.eclipse.persistence.testing.jaxb.xmlelement.model.EmptyCollectionTestCases;
import org.eclipse.persistence.testing.jaxb.xmlelement.model.EmptyJSONArrayTestCases;
import org.eclipse.persistence.testing.jaxb.xmlelement.model.FullTestCases;
import org.eclipse.persistence.testing.jaxb.xmlelement.model.FullTestCasesNS;
import org.eclipse.persistence.testing.jaxb.xmlelement.model.SpecialCharacterTestCases;
import org.eclipse.persistence.testing.jaxb.xmlelement.nulls.NullTestCases;
import org.eclipse.persistence.testing.jaxb.xmlelement.order.ElementOrderingTestCases;
import org.eclipse.persistence.testing.jaxb.xmlelement.self.XmlElementSelfTestCases;
import org.eclipse.persistence.testing.jaxb.xmlelement.type.TypeBarTestCases;
import org.eclipse.persistence.testing.jaxb.xmlelement.type.TypeBarXmlValueTestCases;
import org.eclipse.persistence.testing.jaxb.xmlelement.type.TypeNoTypeTestCases;
import org.eclipse.persistence.testing.jaxb.xmlelement.type.TypeSimpleTestCases;

import junit.framework.Test;
import junit.framework.TestSuite;

public class XmlElementTestSuite extends TestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite("XmlElement Test Suite");

        suite.addTestSuite(XmlElementNamespaceTestCases.class);
        suite.addTestSuite(XmlElementNoNamespaceTestCases.class);
        suite.addTestSuite(XmlElementCollectionTestCases.class);
        suite.addTestSuite(XmlElementNillableTestCases.class);
        suite.addTestSuite(XmlElementNillableTextTestCases.class);
        suite.addTestSuite(FullTestCases.class);
        suite.addTestSuite(FullTestCasesNS.class);
        suite.addTestSuite(NullTestCases.class);
        suite.addTestSuite(EmptyCollectionTestCases.class);
        suite.addTestSuite(EmptyJSONArrayTestCases.class);
        suite.addTestSuite(SpecialCharacterTestCases.class);
        suite.addTestSuite(ElementOrderingTestCases.class);
        suite.addTestSuite(XmlElementConstantsTestCases.class);
        suite.addTestSuite(XmlElementDefaultValueTestCases.class);
        suite.addTestSuite(EmpytElementObjectTestCases.class);
        suite.addTestSuite(SameElementAttributeNameTestCases.class);
        suite.addTestSuite(TypeNoTypeTestCases.class);
        suite.addTestSuite(TypeSimpleTestCases.class);
        suite.addTestSuite(TypeBarTestCases.class);
        suite.addTestSuite(TypeBarXmlValueTestCases.class);
        suite.addTestSuite(XmlElementSelfTestCases.class);
        return suite;
    }

}
