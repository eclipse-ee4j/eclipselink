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
package org.eclipse.persistence.testing.sdo.helper;

import junit.framework.*;
import org.eclipse.persistence.testing.sdo.helper.datafactory.SDODataFactoryTestSuite;
import org.eclipse.persistence.testing.sdo.helper.sdohelper.SDOHelperTestCases;
import org.eclipse.persistence.testing.sdo.helper.typehelper.SDOTypeHelperTestSuite;
import org.eclipse.persistence.testing.sdo.helper.xmlhelper.SDOXMLHelperTestSuite;
import org.eclipse.persistence.testing.sdo.helper.xsdhelper.SDOXSDHelperTestSuite;

/**
 *  The general location where we perform all corresponding testing based on
 *  different SDO classes such as Type, TypeHelper, and SDOType etc
 */
public class SDOHelperTestSuite extends TestCase {
    public SDOHelperTestSuite() {
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    /**
     *  Inherited suite method for generating all test cases.
     * @return
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("All Helper Tests");
        suite.addTest(new SDOXSDHelperTestSuite().suite());
        suite.addTest(new SDOTypeHelperTestSuite().suite());
        suite.addTest(new SDODataFactoryTestSuite().suite());
        //suite.addTest(new SDOClassGenTestSuite().suite());
        suite.addTest(new SDOXMLHelperTestSuite().suite());

        suite.addTestSuite(SDOHelperTestCases.class);
        suite.addTestSuite(SDOHelperContextTest.class);
        return suite;
    }
}
