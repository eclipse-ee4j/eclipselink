/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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

import org.eclipse.persistence.testing.jaxb.events.RootWithCompositeObjectTestCases;
import org.eclipse.persistence.testing.jaxb.inheritance.JAXBInheritanceTestCases;
import org.eclipse.persistence.testing.jaxb.jaxbelement.complex.JAXBElementComplexTestCases;
import org.eclipse.persistence.testing.jaxb.jaxbelement.nested.JAXBElementNestedTestCases;
import org.eclipse.persistence.testing.jaxb.jaxbelement.nil.JAXBElementNilTestCases;
import org.eclipse.persistence.testing.jaxb.jaxbelement.simple.JAXBElementBase64TestCases;
import org.eclipse.persistence.testing.jaxb.jaxbelement.simple.JAXBElementDataHandlerTestCases;
import org.eclipse.persistence.testing.jaxb.jaxbelement.simple.JAXBElementSimpleTestCases;
import org.eclipse.persistence.testing.jaxb.jaxbelement.enumeration.JAXBElementEnumTestCases;
import org.eclipse.persistence.testing.jaxb.refresh.RefreshTestSuite;
import org.eclipse.persistence.testing.jaxb.schemagen.SchemaGenTestSuite;
import org.eclipse.persistence.testing.jaxb.stax.XMLStreamWriterDefaultNamespaceTestCases;
import org.eclipse.persistence.testing.jaxb.typemappinginfo.TypeMappingInfoTestSuite;
import org.eclipse.persistence.testing.jaxb.xmladapter.XmlAdapterTestSuite;
import org.eclipse.persistence.testing.jaxb.xmlidref.XmlIdRefTestCases;
import org.eclipse.persistence.testing.jaxb.xmlidref.object.XmlIdRefObjectTestCases;
import org.eclipse.persistence.testing.jaxb.xmlidref.xmlelements.XmlElementsIdRefTestCases;
import org.eclipse.persistence.testing.jaxb.xmlidref.xmlelements.XmlElementsSingleIdRefTestCases;
import org.eclipse.persistence.testing.jaxb.xmlidrefs.XmlIdRefsTestCases;
import org.eclipse.persistence.testing.jaxb.xmlidrefs.object.XmlIdRefsObjectTestCases;
import org.eclipse.persistence.testing.jaxb.xmlinlinebinary.XmlInlineBinaryDataTestCases;
import org.eclipse.persistence.testing.jaxb.xmlmarshaller.JAXBDOMTestSuite;
import org.eclipse.persistence.testing.jaxb.xmlmarshaller.JAXBSAXTestSuite;
import org.eclipse.persistence.testing.jaxb.xmlvalue.XmlValueByteArrayTestCases;
import org.eclipse.persistence.testing.jaxb.xmlvalue.XmlValueListTestCases;
import org.eclipse.persistence.testing.jaxb.xmlvalue.XmlValueTestCases;
import org.eclipse.persistence.testing.jaxb.xmlvalue.XmlValueWithAttributesTestCases;
import org.eclipse.persistence.testing.jaxb.xmlvalue.none.InvalidTestCases;
import org.eclipse.persistence.testing.jaxb.xmlvalue.none.ValidTestCases;

public class JAXBTestSuite extends TestCase {

    public static Test suite() {
        TestSuite suite = new TestSuite("JAXB20 Compiler Test Suite");
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.employee.JAXBEmployeeTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.employee.EmployeeNamespaceCancellationTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.emptystring.EmptyStringTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.emptystring.ListsTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlrootelement.InnerClassTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlrootelement.XmlRootElementNamespaceTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlrootelement.XmlRootElementNoNamespaceTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlrootelement.XmlRootElementNilTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelement.XmlElementNamespaceTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelement.XmlElementNoNamespaceTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelement.XmlElementCollectionTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelement.XmlElementNillableTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlattribute.XmlAttributeNamespaceTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlattribute.XmlAttributeNoNamespaceTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlattribute.XmlAttributeCollectionTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlattribute.ObjectTestCases.class);
        suite.addTestSuite(XmlValueTestCases.class);
        suite.addTestSuite(XmlValueByteArrayTestCases.class);
        suite.addTestSuite(XmlValueWithAttributesTestCases.class);
        suite.addTestSuite(XmlValueListTestCases.class);
        suite.addTestSuite(InvalidTestCases.class);
        suite.addTestSuite(ValidTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlschema.attributeformdefault.qualified.AttributeFormDefaultQualifiedTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlschema.attributeformdefault.unqualified.AttributeFormDefaultUnqualifiedTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlschema.attributeformdefault.unset.AttributeFormDefaultUnsetTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelements.XmlElementsStringTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelements.XmlElementsIntegerTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelements.XmlElementsComplexTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelements.XmlElementsCollectionTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlanyelement.XmlAnyElementTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlanyelement.XmlAnyElementLaxTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlanyelement.domhandler.DOMHandlerTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlanyelement.ns.DefaultNamespaceTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlanyelement.ns.DefaultNamespaceCollectionTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelementref.EmployeeCollectionTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelementref.EmployeeSingleTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelementref.XMLElementRefConverterTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelementref.duplicatename.DuplicateNameTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelementref.inheritance1.Inheritance1TestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelementref.inheritance2.Inheritance2TestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelementref.missingref.MissingRefTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.multiplepackage.MultiplePackageTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.multiplepackage.MultiplePackageInfoTestCases.class);

        // jaxb-mats bug due to time/timestamp changes to TopLink after 070609
        // suite.addTestSuite(XmlSchemaTypeDateTestCases.class);
        // suite.addTestSuite(XmlSchemaTypeTwoDatesTestCases.class);
        suite.addTestSuite(XmlIdRefTestCases.class);
        suite.addTestSuite(XmlIdRefObjectTestCases.class);
        suite.addTestSuite(XmlIdRefsTestCases.class);
        suite.addTestSuite(XmlIdRefsObjectTestCases.class);
        suite.addTestSuite(XmlElementsIdRefTestCases.class);
        suite.addTestSuite(XmlElementsSingleIdRefTestCases.class);
        suite.addTestSuite(XmlInlineBinaryDataTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlenum.XmlEnumElementTestCases.class);
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
        suite.addTestSuite(RootWithCompositeObjectTestCases.class);
        suite.addTestSuite(JAXBElementEnumTestCases.class);
        suite.addTestSuite(JAXBInheritanceTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.events.RootWithCompositeCollectionTestCases.class);
        suite.addTest(XmlAdapterTestSuite.suite());
        suite.addTest(SchemaGenTestSuite.suite());
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
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.events.sessionevents.SessionEventTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.jaxbcontext.JaxbContextCreationTests.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.jaxbcontext.JaxbContextReturnTypesTests.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.jaxbcontext.JaxbTypeToSchemaTypeTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.binder.adapter.BinderWithAdapterTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.binder.nullpolicy.BinderWithNullPolicyTestCases.class);
        suite.addTest(org.eclipse.persistence.testing.jaxb.listofobjects.JAXBListOfObjectsSuite.suite());
        suite.addTest(TypeMappingInfoTestSuite.suite());

        suite.addTest(org.eclipse.persistence.testing.jaxb.singleobject.JAXBSingleObjectTestSuite.suite());
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.classloader.DifferentClassLoaderTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.classloader.InnerClassTestCases.class);

        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.helper.JAXBHelperTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.any.AnyWithJAXBElementTestCases.class);
        
        suite.addTest(org.eclipse.persistence.testing.jaxb.dynamic.DynamicJAXBTestSuite.suite());
        suite.addTest(org.eclipse.persistence.testing.jaxb.substitution.SubstitutionTestSuite.suite());

        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.interfaces.InterfaceTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlmarshaller.MarshalSchemaValidationTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlmarshaller.NoSchemaRefTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlmarshaller.UnmarshalSchemaValidationTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlmarshaller.UnmarshallerNullTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlmarshaller.NoSchemaRefTestCases.class);
        suite.addTestSuite(XMLStreamWriterDefaultNamespaceTestCases.class);
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
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlaccessortype.none.NoneTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlaccessortype.FieldAndPropertyTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlvirtualaccessmethods.XmlVirtualAccessMethodsTestCases.class);
        suite.addTest(RefreshTestSuite.suite());
        return suite;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.jaxb.JAXBTestSuite" };
        // junit.swingui.TestRunner.main(arguments);
        // System.setProperty("useLogging", "true");
        junit.textui.TestRunner.main(arguments);
    }

}