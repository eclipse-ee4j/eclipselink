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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class JAXBTestSuite2C extends TestCase {

    public static Test suite() {
        TestSuite suite = new TestSuite("JAXB20 Compiler Test Suite 2C");
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
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.jaxbcontext.notext.NoTextMappingErrorTests.class);
        suite.addTest(org.eclipse.persistence.testing.jaxb.jaxbcontext.ByXPathTestSuite.suite());
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
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.binder.xmlanyelement.XMLBinderTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.namespaceuri.inheritance.InheritanceRootChoiceOnlyTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.namespaceuri.inheritance.InheritanceRootCompositeCollectionObjectOnlyTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.namespaceuri.inheritance.InheritanceRootCompositeCollectionObjectOnlyXMLTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.namespaceuri.inheritance.InheritanceRootCompositeCollectionObjectOnlyNSTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.namespaceuri.inheritance.InheritanceRootCompositeCollectionOnlyTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.namespaceuri.inheritance.InheritanceWithMultiplePackagesTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.namespaceuri.inheritance.InheritanceWithMultiplePackagesXMLTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.namespaceuri.inheritance.InheritanceWithMultiplePackagesNSTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.namespaceuri.inheritance.InheritanceWithMultiplePackagesBackwardCompatibilityTestCases.class);
        return suite;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.jaxb.JAXBTestSuite2C" };
        // junit.swingui.TestRunner.main(arguments);
        // System.setProperty("useLogging", "true");
        junit.textui.TestRunner.main(arguments);
    }

}
