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

import org.eclipse.persistence.testing.jaxb.casesensitivity.JAXBCaseInsensitivityTestCase;
import org.eclipse.persistence.testing.jaxb.inheritance.override.InheritanceOverrideTestCases;
import org.eclipse.persistence.testing.jaxb.inheritance.typeElem.InheritanceTypeElementTestCases;
import org.eclipse.persistence.testing.jaxb.objectgraph.ObjectGraphAttributeTestCases;
import org.eclipse.persistence.testing.jaxb.objectgraph.ObjectGraphBasicTestCases;
import org.eclipse.persistence.testing.jaxb.objectgraph.ObjectGraphBindingsTestCases;
import org.eclipse.persistence.testing.jaxb.objectgraph.ObjectGraphDynamicTestCases;
import org.eclipse.persistence.testing.jaxb.objectgraph.ObjectGraphInheritanceTestCases;
import org.eclipse.persistence.testing.jaxb.objectgraph.ObjectGraphRefSubTestCases;
import org.eclipse.persistence.testing.jaxb.objectgraph.ObjectGraphXmlAnyLaxCollectionTestCases;
import org.eclipse.persistence.testing.jaxb.objectgraph.ObjectGraphXmlAnyLaxTestCases;
import org.eclipse.persistence.testing.jaxb.typevariable.TypeVariableTestSuite;
import org.eclipse.persistence.testing.jaxb.xmlelementrefs.adapter.XmlElementRefsAdapterTestCases;
import org.eclipse.persistence.testing.jaxb.xmlinverseref.XmlInverseRefBidirectionalTestSuite;
import org.eclipse.persistence.testing.jaxb.xmlvariablenode.AllVariableElementTestCases;
import org.eclipse.persistence.testing.jaxb.xsitype.OtherRootTestCases;
import org.eclipse.persistence.testing.jaxb.xsitype.TypeAttributeInheritanceTestCases;
import org.eclipse.persistence.testing.jaxb.xsitype.TypeAttributeTestCases;

import junit.framework.Test;
import junit.framework.TestSuite;

public class JAXBTestSuite4B {

    public static Test suite() {
        TestSuite suite = new TestSuite("JAXB20 Compiler Test Suite 4B");
        suite.addTest(XmlInverseRefBidirectionalTestSuite.suite());
        suite.addTestSuite(ObjectGraphBasicTestCases.class);
        suite.addTestSuite(ObjectGraphBindingsTestCases.class);
        suite.addTestSuite(ObjectGraphDynamicTestCases.class);
        suite.addTestSuite(ObjectGraphRefSubTestCases.class);
        suite.addTestSuite(ObjectGraphInheritanceTestCases.class);
        suite.addTestSuite(ObjectGraphAttributeTestCases.class);
        suite.addTestSuite(ObjectGraphXmlAnyLaxTestCases.class);
        suite.addTestSuite(ObjectGraphXmlAnyLaxCollectionTestCases.class);
        suite.addTestSuite(XmlElementRefsAdapterTestCases.class);
        suite.addTest(AllVariableElementTestCases.suite());
        suite.addTestSuite(InheritanceOverrideTestCases.class);
        suite.addTestSuite(InheritanceTypeElementTestCases.class);
        suite.addTestSuite(TypeAttributeTestCases.class);
        suite.addTestSuite(TypeAttributeInheritanceTestCases.class);
        suite.addTestSuite(OtherRootTestCases.class);
        suite.addTest(TypeVariableTestSuite.suite());
        suite.addTestSuite(JAXBCaseInsensitivityTestCase.class);
        return suite;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.jaxb.JAXBTestSuite4B" };
        junit.textui.TestRunner.main(arguments);
    }
}
