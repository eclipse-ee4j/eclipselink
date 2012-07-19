/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.testing.jaxb.events.ExternalMetadataTestCases;
import org.eclipse.persistence.testing.jaxb.events.RootWithCompositeObjectTestCases;
import org.eclipse.persistence.testing.jaxb.inheritance.JAXBInheritanceTestCases;
import org.eclipse.persistence.testing.jaxb.inheritance.ns.JAXBInheritanceNSSeparatorTestCases;
import org.eclipse.persistence.testing.jaxb.inheritance.ns.JAXBInheritanceNSTestCases;
import org.eclipse.persistence.testing.jaxb.inheritance.ns.JAXBInheritanceSubTypeNoParentRootTestCases;
import org.eclipse.persistence.testing.jaxb.inheritance.ns.JAXBInheritanceSubTypeParentRootOnlyTestCases;
import org.eclipse.persistence.testing.jaxb.inheritance.ns.JAXBInheritanceSubTypeTestCases;
import org.eclipse.persistence.testing.jaxb.jaxbelement.complex.JAXBElementComplexTestCases;
import org.eclipse.persistence.testing.jaxb.jaxbelement.nested.JAXBElementNestedTestCases;
import org.eclipse.persistence.testing.jaxb.jaxbelement.nil.JAXBElementNilTestCases;
import org.eclipse.persistence.testing.jaxb.jaxbelement.simple.JAXBElementBase64TestCases;
import org.eclipse.persistence.testing.jaxb.jaxbelement.simple.JAXBElementDataHandlerTestCases;
import org.eclipse.persistence.testing.jaxb.jaxbelement.simple.JAXBElementSimpleTestCases;
import org.eclipse.persistence.testing.jaxb.jaxbelement.subclass.JAXBElementSubclassEnumTestCases;
import org.eclipse.persistence.testing.jaxb.jaxbelement.subclass.JAXBElementSubclassTestCases;
import org.eclipse.persistence.testing.jaxb.jaxbelement.enumeration.JAXBElementEnumTestCases;
import org.eclipse.persistence.testing.jaxb.xmlenum.InvalidEnumValueTestCases;
import org.eclipse.persistence.testing.jaxb.xmlenum.XmlEnumRootElemTestCases;
import org.eclipse.persistence.testing.jaxb.xmlidref.XmlIdRefTestCases;
import org.eclipse.persistence.testing.jaxb.xmlidref.inheritance.XmlIdRefInheritanceTestCases;
import org.eclipse.persistence.testing.jaxb.xmlidref.object.XmlIdRefObjectTestCases;
import org.eclipse.persistence.testing.jaxb.xmlidref.object.XmlIdRefObjectWhitespaceTestCases;
import org.eclipse.persistence.testing.jaxb.xmlidref.xmlelements.XmlElementsIdRefTestCases;
import org.eclipse.persistence.testing.jaxb.xmlidref.xmlelements.XmlElementsSingleIdRefTestCases;
import org.eclipse.persistence.testing.jaxb.xmlidrefs.XmlIdRefsTestCases;
import org.eclipse.persistence.testing.jaxb.xmlidrefs.object.XmlIdRefsObjectTestCases;
import org.eclipse.persistence.testing.jaxb.xmlinlinebinary.InlineDataHandlerCollectionTestCases;
import org.eclipse.persistence.testing.jaxb.xmlinlinebinary.LargeInlineBinaryTestCases;
import org.eclipse.persistence.testing.jaxb.xmlinlinebinary.XmlInlineBinaryDataTestCases;
import org.eclipse.persistence.testing.jaxb.xmlmarshaller.JAXBDOMTestSuite;
import org.eclipse.persistence.testing.jaxb.xmlmarshaller.JAXBSAXTestSuite;

public class JAXBTestSuite2 extends TestCase {

    public static Test suite() {
        TestSuite suite = new TestSuite("JAXB20 Compiler Test Suite2");

        // jaxb-mats bug due to time/timestamp changes to TopLink after 070609
        // suite.addTestSuite(XmlSchemaTypeDateTestCases.class);
        // suite.addTestSuite(XmlSchemaTypeTwoDatesTestCases.class);
        suite.addTestSuite(XmlIdRefTestCases.class);
        suite.addTestSuite(XmlIdRefObjectTestCases.class);
        suite.addTestSuite(XmlIdRefInheritanceTestCases.class);
        suite.addTestSuite(XmlIdRefsTestCases.class);
        suite.addTestSuite(XmlIdRefsObjectTestCases.class);
        suite.addTestSuite(XmlIdRefObjectWhitespaceTestCases.class);
        suite.addTestSuite(XmlElementsIdRefTestCases.class);
        suite.addTestSuite(XmlElementsSingleIdRefTestCases.class);
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
        suite.addTestSuite(JAXBElementSimpleTestCases.class);
        suite.addTestSuite(JAXBElementNestedTestCases.class);
        suite.addTestSuite(JAXBElementComplexTestCases.class);
        suite.addTestSuite(JAXBElementNilTestCases.class);
        suite.addTestSuite(JAXBElementBase64TestCases.class);
        suite.addTestSuite(JAXBElementDataHandlerTestCases.class);
        suite.addTestSuite(JAXBElementSubclassTestCases.class);
        suite.addTestSuite(JAXBElementSubclassEnumTestCases.class);
        suite.addTestSuite(RootWithCompositeObjectTestCases.class);
        suite.addTestSuite(JAXBElementEnumTestCases.class);
        suite.addTestSuite(JAXBInheritanceTestCases.class);
        suite.addTestSuite(JAXBInheritanceNSTestCases.class);
        suite.addTestSuite(JAXBInheritanceSubTypeTestCases.class);
        suite.addTestSuite(JAXBInheritanceSubTypeNoParentRootTestCases.class);
        suite.addTestSuite(JAXBInheritanceSubTypeParentRootOnlyTestCases.class);
        suite.addTestSuite(JAXBInheritanceNSSeparatorTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.events.RootWithCompositeCollectionTestCases.class);
        suite.addTestSuite(ExternalMetadataTestCases.class);
        suite.addTest(JAXBDOMTestSuite.suite());
        suite.addTest(JAXBSAXTestSuite.suite());

        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.simpledocument.SimpleDocumentStringTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.simpledocument.StringNilTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.simpledocument.StringEmptyElementTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.simpledocument.SimpleDocumentIntegerTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.simpledocument.SimpleDocumentDateTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.simpledocument.SimpleDocumentByteArrayTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.simpledocument.SimpleDocumentWhitespaceNullTestCases.class);

        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.namespaceuri.schemacontext.SchemaContextAsQNameTest.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.namespaceuri.splitpackage.qualified.QualifiedTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.namespaceuri.splitpackage.unqualified.UnqualifiedTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.namespaceuri.twopackages.ABTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.namespaceuri.twopackages.BATestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.events.sessionevents.SessionEventTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.jaxbcontext.JaxbContextCreationTests.class);        
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.jaxbcontext.JaxbContextReturnTypesTests.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.jaxbcontext.JaxbTypeToSchemaTypeTestCases.class);
        suite.addTest(org.eclipse.persistence.testing.jaxb.jaxbcontext.empty.EmptyTestCases.suite());
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.jaxbcontext.sessioneventlistener.SessionEventListenerTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.binder.nscollison.NamespaceCollisionTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.binder.adapter.BinderWithAdapterTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.binder.nullpolicy.BinderWithNullPolicyTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.binder.nullpolicy.BinderWithNullPolicyCompositeTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.binder.hashcode.BinderWithHashCodeTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.binder.jaxbelement.BinderWithJAXBElementTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.namespaceuri.inheritance.InheritanceRootChoiceOnlyTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.namespaceuri.inheritance.InheritanceRootCompositeCollectionObjectOnlyTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.namespaceuri.inheritance.InheritanceRootCompositeCollectionObjectOnlyNSTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.namespaceuri.inheritance.InheritanceRootCompositeCollectionOnlyTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.namespaceuri.inheritance.InheritanceWithMultiplePackagesTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.namespaceuri.inheritance.InheritanceWithMultiplePackagesNSTestCases.class);

        return suite;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.jaxb.JAXBTestSuite2" };
        // junit.swingui.TestRunner.main(arguments);
        // System.setProperty("useLogging", "true");
        junit.textui.TestRunner.main(arguments);
    }

}