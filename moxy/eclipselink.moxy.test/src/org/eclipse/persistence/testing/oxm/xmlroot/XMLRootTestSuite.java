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
package org.eclipse.persistence.testing.oxm.xmlroot;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.oxm.xmlroot.complex.MarshalToNodeTestCases;
import org.eclipse.persistence.testing.oxm.xmlroot.complex.XMLRootComplexDifferentPrefixTestCases;
import org.eclipse.persistence.testing.oxm.xmlroot.complex.XMLRootComplexDifferentPrefixWithDRTestCases;
import org.eclipse.persistence.testing.oxm.xmlroot.complex.XMLRootComplexNoNRWithPrefixTestCases;
import org.eclipse.persistence.testing.oxm.xmlroot.complex.XMLRootComplexNoNamespaceResolverTestCases;
import org.eclipse.persistence.testing.oxm.xmlroot.complex.XMLRootComplexNoPrefixTestCases;
import org.eclipse.persistence.testing.oxm.xmlroot.complex.XMLRootComplexNullUriTestCases;
import org.eclipse.persistence.testing.oxm.xmlroot.complex.XMLRootComplexTestCases;
import org.eclipse.persistence.testing.oxm.xmlroot.complex.XMLRootMultipleMarshalTestCases;
import org.eclipse.persistence.testing.oxm.xmlroot.complex.XMLRootNullSchemaReferenceTestCases;
import org.eclipse.persistence.testing.oxm.xmlroot.nil.XMLRootNilForceWrapTestCases;
import org.eclipse.persistence.testing.oxm.xmlroot.nil.XMLRootNilTestCases;
import org.eclipse.persistence.testing.oxm.xmlroot.simple.XMLRootDurationTestCases;
import org.eclipse.persistence.testing.oxm.xmlroot.simple.XMLRootSimpleCollectionTestCases;
import org.eclipse.persistence.testing.oxm.xmlroot.simple.XMLRootSimpleTestCases;
import org.eclipse.persistence.testing.oxm.xmlroot.simple.XMLRootNoPrefixTestCases;
import org.eclipse.persistence.testing.oxm.xmlroot.simple.XMLRootNullUriTestCases;
import org.eclipse.persistence.testing.oxm.xmlroot.simple.XMLRootXMLGregorianCalendarTestCases;

public class XMLRootTestSuite extends TestCase {
    public XMLRootTestSuite(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.xmlroot.XMLRootTestSuite" };
        //System.setProperty("useDeploymentXML", "true");
        //System.setProperty("useDocPres", "true");
        //System.setProperty("useLogging", "true");
        //System.setProperty("useSAXParsing", "true");
        junit.textui.TestRunner.main(arguments);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("XMLRoot Test Cases");
        suite.addTestSuite(MarshalToNodeTestCases.class);
        suite.addTestSuite(XMLRootComplexTestCases.class);
        suite.addTestSuite(XMLRootComplexDifferentPrefixWithDRTestCases.class);
        suite.addTestSuite(XMLRootComplexNoPrefixTestCases.class);
        suite.addTestSuite(XMLRootComplexNoNamespaceResolverTestCases.class);
        suite.addTestSuite(XMLRootComplexNullUriTestCases.class);
        suite.addTestSuite(XMLRootComplexNoNRWithPrefixTestCases.class);
        suite.addTestSuite(XMLRootComplexDifferentPrefixTestCases.class);
        suite.addTestSuite(XMLRootSimpleTestCases.class);
        suite.addTestSuite(XMLRootNoPrefixTestCases.class);
        suite.addTestSuite(XMLRootNullUriTestCases.class);
        suite.addTestSuite(XMLRootNullSchemaReferenceTestCases.class);
        suite.addTestSuite(XMLRootMultipleMarshalTestCases.class);
        suite.addTestSuite(XMLRootSimpleCollectionTestCases.class);
        suite.addTestSuite(XMLRootXMLGregorianCalendarTestCases.class);
        suite.addTestSuite(XMLRootDurationTestCases.class);
        suite.addTestSuite(XMLRootNilTestCases.class);
        suite.addTestSuite(XMLRootNilForceWrapTestCases.class);
        return suite;
    }
}
