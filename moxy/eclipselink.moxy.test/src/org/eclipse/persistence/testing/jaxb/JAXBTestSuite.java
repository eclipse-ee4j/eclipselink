/*******************************************************************************
 * Copyright (c) 1998, 2016 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.testing.jaxb.file.FileTestCases;
import org.eclipse.persistence.testing.jaxb.idresolver.IDResolverTestCases;
import org.eclipse.persistence.testing.jaxb.jaxbcontext.JAXBContextMediaTypeTestCases;
import org.eclipse.persistence.testing.jaxb.namespaceuri.xml.XMLNamespaceTestCases;
import org.eclipse.persistence.testing.jaxb.namespaceuri.xml.XMLNamespaceXmlPathTestCases;
import org.eclipse.persistence.testing.jaxb.nomappings.NoMappingsTestCases;
import org.eclipse.persistence.testing.jaxb.xmlelementref.nills.XmlElementRefNillStringTestCases;
import org.eclipse.persistence.testing.jaxb.xmlelementref.nills.XmlElementRefNillWithAttributesTestCases;
import org.eclipse.persistence.testing.jaxb.xmlelementref.ns.XmlElementRefWithNamespaceTests;
import org.eclipse.persistence.testing.jaxb.xmlelementref.prefix.XmlElementRefPrefixesTestCases;
import org.eclipse.persistence.testing.jaxb.xmlvalue.XmlValueByteArrayTestCases;
import org.eclipse.persistence.testing.jaxb.xmlvalue.XmlValueByteArrayWithIdTestCases;
import org.eclipse.persistence.testing.jaxb.xmlvalue.XmlValueListTestCases;
import org.eclipse.persistence.testing.jaxb.xmlvalue.XmlValueTestCases;
import org.eclipse.persistence.testing.jaxb.xmlvalue.XmlValueWithAttributesTestCases;
import org.eclipse.persistence.testing.jaxb.xmlvalue.XmlValueWithAttributesXpathTestCases;
import org.eclipse.persistence.testing.jaxb.xmlvalue.XmlValueWithNullAttrTestCases;
import org.eclipse.persistence.testing.jaxb.xmlvalue.none.InvalidTestCases;
import org.eclipse.persistence.testing.jaxb.xmlvalue.none.ValidTestCases;

public class JAXBTestSuite extends TestCase {

    public static Test suite() {
        TestSuite suite = new TestSuite("JAXB20 Compiler Test Suite");
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
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlschema.attributeformdefault.qualified.AttributeFormDefaultQualifiedTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlschema.attributeformdefault.unqualified.AttributeFormDefaultUnqualifiedTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlschema.attributeformdefault.unset.AttributeFormDefaultUnsetTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlschema.attributeformdefault.qualified.AttributeNSQualifiedTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlschema.attributeformdefault.qualified.AttributeQualifiedTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlschema.attributeformdefault.qualified.NoAttributeQualifiedTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlschema.attributeformdefault.unqualified.AttributeNSUnqualifiedTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlschema.attributeformdefault.unqualified.AttributeUnqualifiedTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlschema.attributeformdefault.unqualified.NoAttributeUnqualifiedTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlschema.defaultns.emptyprefix.DefaultNamespaceEmptyStringTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlschema.defaultns.singleemptyprefix.DefaultNamespaceSingleEmptyStringTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelements.XmlElementsStringTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelements.XmlElementsAdapterTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelements.XmlElementsIntegerTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelements.XmlElementsComplexTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelements.XmlElementsInheritanceTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelements.XmlElementsCollectionTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelements.XmlElementsArrayTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelements.SameFieldAddressTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelements.SameFieldLinkTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelements.SameFieldCollectionAddressTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelements.SameFieldCollectionLinkTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelements.PredicateAddressTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelements.PredicateLinkTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelements.CircularTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlanyelement.XmlAnyElementTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlanyelement.XmlAnyElementArrayTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlanyelement.XmlAnyElementLaxTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlanyelement.XmlAnyElementLaxSingleTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlanyelement.XmlAnyElementNoDomTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlanyelement.XmlAnyElementNSTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlanyelement.XmlAnyElementLaxMixedTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlanyelement.XmlAnyElementLaxMixedEmptyTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlanyelement.XmlAnyJAXBElementTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlanyelement.domhandler.DOMHandlerTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlanyelement.ns.DefaultNamespaceTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlanyelement.ns.DefaultNamespaceCollectionTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlanyelement.ns.qualified.XMLAnyElementNamespaceTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlanyelement.ns.qualified.DefaultNamespaceTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlanyelement.ns2.DefaultNamespace2TestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelementref.collections.ChoiceCollectionTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelementref.collections.ChoiceCollectionNullTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelementref.EmployeeCollectionTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelementref.ByteArrayCollectionTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelementref.EmployeeSingleTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelementref.XMLElementRefConverterTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelementref.attachment.XMLElementRefAttachmentTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelementref.attachment.XMLElementRefAttachmentEmptyTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelementref.attachment.XMLElementRefAttachmentNullTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelementref.attachment.XMLElementRefTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelementref.attachment.XMLElementRefNullTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelementref.duplicatename.DuplicateNameTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelementref.enums.EnumTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelementref.inheritance1.Inheritance1TestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelementref.inheritance2.Inheritance2TestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelementref.missingref.MissingRefTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelementref.notincontext.XmlElementRefNotGivenToContextTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelementwrapper.XmlElementWrapperTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.multiplepackage.MultiplePackageTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.multiplepackage.MultiplePackageInfoTestCases.class);
        suite.addTestSuite(IDResolverTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.idresolver.collection.IDResolverTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.attachments.contenttype.ContentTypeTestCases.class);
        suite.addTestSuite(XmlElementRefWithNamespaceTests.class);
        suite.addTestSuite(XmlElementRefPrefixesTestCases.class);
        suite.addTestSuite(FileTestCases.class);
        suite.addTestSuite(XmlElementRefNillWithAttributesTestCases.class);
        suite.addTestSuite(XmlElementRefNillStringTestCases.class);
        return suite;

    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.jaxb.JAXBTestSuite" };
        // junit.swingui.TestRunner.main(arguments);
        // System.setProperty("useLogging", "true");
        junit.textui.TestRunner.main(arguments);
    }

}