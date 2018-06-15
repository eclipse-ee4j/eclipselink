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
package org.eclipse.persistence.testing.jaxb;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.testing.jaxb.casesensitivity.JAXBCaseInsensitivityTestCase;
import org.eclipse.persistence.testing.jaxb.collections.CollectionsTestSuite;
import org.eclipse.persistence.testing.jaxb.defaultvalue.DefaultValueTestSuite;
import org.eclipse.persistence.testing.jaxb.employee.JAXBEmployeeNSTestCases;
import org.eclipse.persistence.testing.jaxb.employee.JAXBEmployeeNoWrapperTestCases;
import org.eclipse.persistence.testing.jaxb.employee.JAXBEmployeeTestCases;
import org.eclipse.persistence.testing.jaxb.inheritance.override.InheritanceOverrideTestCases;
import org.eclipse.persistence.testing.jaxb.inheritance.typeElem.InheritanceTypeElementTestCases;
import org.eclipse.persistence.testing.jaxb.jaxbfragment.JAXBFragmentTestCases;
import org.eclipse.persistence.testing.jaxb.map.MapElementWrapperExternalTestCases;
import org.eclipse.persistence.testing.jaxb.map.MapElementWrapperTestCases;
import org.eclipse.persistence.testing.jaxb.map.MapNamespaceBarTestCases;
import org.eclipse.persistence.testing.jaxb.map.MapNamespaceFooTestCases;
import org.eclipse.persistence.testing.jaxb.map.MapTestCases;
import org.eclipse.persistence.testing.jaxb.objectgraph.ObjectGraphAttributeTestCases;
import org.eclipse.persistence.testing.jaxb.objectgraph.ObjectGraphBasicTestCases;
import org.eclipse.persistence.testing.jaxb.objectgraph.ObjectGraphBindingsTestCases;
import org.eclipse.persistence.testing.jaxb.objectgraph.ObjectGraphDynamicTestCases;
import org.eclipse.persistence.testing.jaxb.objectgraph.ObjectGraphInheritanceTestCases;
import org.eclipse.persistence.testing.jaxb.objectgraph.ObjectGraphRefSubTestCases;
import org.eclipse.persistence.testing.jaxb.objectgraph.ObjectGraphXmlAnyLaxCollectionTestCases;
import org.eclipse.persistence.testing.jaxb.objectgraph.ObjectGraphXmlAnyLaxTestCases;
import org.eclipse.persistence.testing.jaxb.refresh.RefreshTestSuite;
import org.eclipse.persistence.testing.jaxb.typevariable.TypeVariableTestSuite;
import org.eclipse.persistence.testing.jaxb.xmlelementrefs.adapter.XmlElementRefsAdapterTestCases;
import org.eclipse.persistence.testing.jaxb.xmlgregoriancalendar.XMLGregorianCalendarObjectTestCases;
import org.eclipse.persistence.testing.jaxb.xmlinverseref.XmlInverseRefBidirectionalTestSuite;
import org.eclipse.persistence.testing.jaxb.xmlschematype.XSDSchemaTypesTestCases;
import org.eclipse.persistence.testing.jaxb.xmlvariablenode.AllVariableElementTestCases;
import org.eclipse.persistence.testing.jaxb.xsitype.OtherRootTestCases;
import org.eclipse.persistence.testing.jaxb.xsitype.TypeAttributeInheritanceTestCases;
import org.eclipse.persistence.testing.jaxb.xsitype.TypeAttributeTestCases;

public class JAXBTestSuite4 {

    public static Test suite() {
        TestSuite suite = new TestSuite("JAXB20 Compiler Test Suite4");
        suite.addTest(SunCompatibilityTestSuite.suite());
        suite.addTest(RefreshTestSuite.suite());
        suite.addTest(org.eclipse.persistence.testing.jaxb.substitution.SubstitutionTestSuite.suite());
        suite.addTest(org.eclipse.persistence.testing.jaxb.innerclasses.InnerClassTestSuite.suite());
        suite.addTest(CollectionsTestSuite.suite());
        suite.addTest(DefaultValueTestSuite.suite());
        suite.addTestSuite(JAXBEmployeeTestCases.class);
        suite.addTestSuite(JAXBEmployeeNoWrapperTestCases.class);
        suite.addTestSuite(JAXBEmployeeNSTestCases.class);
        suite.addTestSuite(JAXBFragmentTestCases.class);
        suite.addTest(org.eclipse.persistence.testing.jaxb.eventhandler.EventHandlerTestSuite.suite());

        suite.addTestSuite(MapTestCases.class);
        suite.addTestSuite(MapElementWrapperTestCases.class);
        suite.addTestSuite(MapElementWrapperExternalTestCases.class);
        suite.addTestSuite(MapNamespaceBarTestCases.class);
        suite.addTestSuite(MapNamespaceFooTestCases.class);
        suite.addTestSuite(XMLGregorianCalendarObjectTestCases.class);
        suite.addTestSuite(XSDSchemaTypesTestCases.class);
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
        String[] arguments = { "-c", "org.eclipse.persistence.testing.jaxb.JAXBTestSuite4" };
        junit.textui.TestRunner.main(arguments);
    }
}
