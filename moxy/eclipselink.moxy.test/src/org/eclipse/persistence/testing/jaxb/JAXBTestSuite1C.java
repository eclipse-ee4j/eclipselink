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

import org.eclipse.persistence.testing.jaxb.file.FileTestCases;
import org.eclipse.persistence.testing.jaxb.idresolver.IDResolverTestCases;
import org.eclipse.persistence.testing.jaxb.xmlelementref.nills.XmlElementRefNillStringTestCases;
import org.eclipse.persistence.testing.jaxb.xmlelementref.nills.XmlElementRefNillWithAttributesTestCases;
import org.eclipse.persistence.testing.jaxb.xmlelementref.nills2.XmlElementRefNillStringRootNamespaceTestCases;
import org.eclipse.persistence.testing.jaxb.xmlelementref.ns.XmlElementRefWithNamespaceTests;
import org.eclipse.persistence.testing.jaxb.xmlelementref.prefix.XmlElementRefPrefixesTestCases;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class JAXBTestSuite1C extends TestCase {

    public static Test suite() {
        TestSuite suite = new TestSuite("JAXB20 Compiler Test Suite 1C");
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
        suite.addTestSuite(XmlElementRefNillStringRootNamespaceTestCases.class);
        return suite;

    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.jaxb.JAXBTestSuite1C" };
        // junit.swingui.TestRunner.main(arguments);
        // System.setProperty("useLogging", "true");
        junit.textui.TestRunner.main(arguments);
    }

}
