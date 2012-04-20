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

import org.eclipse.persistence.testing.jaxb.idresolver.IDResolverTestCases;
import org.eclipse.persistence.testing.jaxb.idresolver.NonELIDResolverTestCases;
import org.eclipse.persistence.testing.jaxb.jaxbcontext.JAXBContextMediaTypeTestCases;
import org.eclipse.persistence.testing.jaxb.namespaceuri.xml.XMLNamespaceTestCases;
import org.eclipse.persistence.testing.jaxb.xmlelementref.ns.XmlElementRefWithNamespaceTests;
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
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlattribute.unqualified.AttributeFormUnqualifiedTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlattribute.ObjectTestCases.class);
        suite.addTestSuite(XmlValueTestCases.class);
        suite.addTestSuite(XmlValueByteArrayTestCases.class);
        suite.addTestSuite(XmlValueWithAttributesTestCases.class);
        suite.addTestSuite(XmlValueListTestCases.class);
        suite.addTestSuite(InvalidTestCases.class);
        suite.addTestSuite(ValidTestCases.class);
        suite.addTestSuite(XMLNamespaceTestCases.class);
        suite.addTestSuite(JAXBContextMediaTypeTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlschema.attributeformdefault.qualified.AttributeFormDefaultQualifiedTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlschema.attributeformdefault.unqualified.AttributeFormDefaultUnqualifiedTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlschema.attributeformdefault.unset.AttributeFormDefaultUnsetTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelements.XmlElementsStringTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelements.XmlElementsIntegerTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelements.XmlElementsComplexTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelements.XmlElementsCollectionTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlanyelement.XmlAnyElementTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlanyelement.XmlAnyElementArrayTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlanyelement.XmlAnyElementLaxTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlanyelement.XmlAnyElementNoDomTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlanyelement.XmlAnyElementNSTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlanyelement.XmlAnyElementLaxMixedTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlanyelement.XmlAnyElementLaxMixedEmptyTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlanyelement.domhandler.DOMHandlerTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlanyelement.ns.DefaultNamespaceTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlanyelement.ns.DefaultNamespaceCollectionTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlanyelement.ns.qualified.XMLAnyElementNamespaceTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelementref.collections.ChoiceCollectionTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelementref.EmployeeCollectionTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelementref.EmployeeSingleTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelementref.XMLElementRefConverterTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelementref.duplicatename.DuplicateNameTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelementref.inheritance1.Inheritance1TestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelementref.inheritance2.Inheritance2TestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelementref.missingref.MissingRefTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelementref.notincontext.XmlElementRefNotGivenToContextTestCases.class);        
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.multiplepackage.MultiplePackageTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.multiplepackage.MultiplePackageInfoTestCases.class);
        suite.addTestSuite(IDResolverTestCases.class);
        suite.addTestSuite(NonELIDResolverTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.attachments.contenttype.ContentTypeTestCases.class);
        suite.addTestSuite(XmlElementRefWithNamespaceTests.class);
        return suite;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.jaxb.JAXBTestSuite" };
        // junit.swingui.TestRunner.main(arguments);
        // System.setProperty("useLogging", "true");
        junit.textui.TestRunner.main(arguments);
    }

}