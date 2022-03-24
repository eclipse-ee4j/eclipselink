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

public class JAXBTestSuite1B extends TestCase {

    public static Test suite() {
        TestSuite suite = new TestSuite("JAXB20 Compiler Test Suite 1B");
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
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlanyelement.XmlAnyElementLaxCDATATestCases.class);
        return suite;

    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.jaxb.JAXBTestSuite1B" };
        // junit.swingui.TestRunner.main(arguments);
        // System.setProperty("useLogging", "true");
        junit.textui.TestRunner.main(arguments);
    }

}
