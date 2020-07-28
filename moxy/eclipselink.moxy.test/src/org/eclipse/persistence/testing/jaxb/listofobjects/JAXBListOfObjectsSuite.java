/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith  June 05, 2009 - Initial implementation
package org.eclipse.persistence.testing.jaxb.listofobjects;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.testing.jaxb.listofobjects.externalizedmetadata.JAXBMultipleMapsNamespaceTestCases;
import org.eclipse.persistence.testing.jaxb.listofobjects.externalizedmetadata.JAXBMultipleMapsTestCases;
import org.eclipse.persistence.testing.jaxb.listofobjects.externalizedmetadata.JAXBTypedListNoXsiTypeTestCases;
import org.eclipse.persistence.testing.jaxb.listofobjects.externalizedmetadata.JAXBTypedListTestCases;
import org.eclipse.persistence.testing.jaxb.listofobjects.ns.MapNamespaceTestCases;

public class JAXBListOfObjectsSuite extends TestCase {
    public JAXBListOfObjectsSuite(String name) {
        super(name);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.main(new String[] { "-c",
                        "org.eclipse.persistence.testing.jaxb.listofobjects.JAXBListOfObjectsSuite" });
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("JAXBListOfObjectsSuite Test Suite");
        suite.addTestSuite(JAXBBigDecimalStackTestCases.class);
        suite.addTestSuite(JAXBEmployeeArrayTestCases.class);
        suite.addTestSuite(JAXBEmployeeListTestCases.class);
        suite.addTestSuite(JAXBIntegerArrayTestCases.class);
        suite.addTestSuite(JAXBInteger3DArrayTestCases.class);
        suite.addTestSuite(JAXBIntegerListTestCases.class);
        suite.addTestSuite(JAXBIntegerMyListTestCases.class);
        suite.addTestSuite(JAXBIntegerLinkedListTestCases.class);
        suite.addTestSuite(JAXBIntArrayTestCases.class);
        suite.addTestSuite(JAXBInt2DArrayTestCases.class);
        suite.addTestSuite(JAXBIntListTestCases.class);
        suite.addTestSuite(JAXBCharArrayTestCases.class);
        suite.addTestSuite(JAXBBooleanArrayTestCases.class);
        suite.addTestSuite(JAXBListNameCollisionEmployeeTestCases.class);
        suite.addTestSuite(JAXBListNameCollisionEmployee2TestCases.class);
        suite.addTestSuite(JAXBEmployeesAndIntegersTestCases.class);
        suite.addTestSuite(JAXBStringIntegerHashMapTestCases.class);
        suite.addTestSuite(JAXBStringEmployeeMapTestCases.class);
        suite.addTestSuite(JAXBStringEmployeeHashtableTestCases.class);
        suite.addTestSuite(JAXBListOfObjectsNonRootTestCases.class);
        suite.addTestSuite(JAXBObjectCollectionsTestCases.class);
        suite.addTestSuite(JAXBMultipleMapsTestCases.class);
        suite.addTestSuite(JAXBMultipleMapsNamespaceTestCases.class);
        suite.addTestSuite(JAXBTypedListTestCases.class);
        suite.addTestSuite(JAXBTypedListNoXsiTypeTestCases.class);
        suite.addTestSuite(JAXBListOfInnerClassTestCases.class);
        suite.addTestSuite(JAXBArrayOfInnerClassTestCases.class);
        suite.addTestSuite(JAXBArrayOfInnerEnumTestCases.class);
        suite.addTestSuite(JAXBByteArrayWithDataHandlerTestCases.class);
        suite.addTestSuite(JAXBMapOfInnerClassTestCases.class);
        suite.addTestSuite(MultiDimensionalArrayNonRootTestCases.class);
        suite.addTestSuite(MultiDimensionalArrayNonRootNonItemNamesTestCases.class);
        suite.addTestSuite(MapNamespaceTestCases.class);
        suite.addTestSuite(JAXBArrayTestCases.class);
        suite.addTestSuite(NullIteratorListTestCases.class);
        suite.addTestSuite(NullSetsMapTestCases.class);
        suite.addTestSuite(NullContainerTestCases.class);
        suite.addTestSuite(ListOfStringArrayTestCases.class);
        return suite;
    }
}
