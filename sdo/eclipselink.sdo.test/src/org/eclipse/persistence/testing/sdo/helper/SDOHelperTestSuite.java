/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.sdo.helper;

import junit.framework.*;
import org.eclipse.persistence.sdo.helper.SDOTypeHelper;
import org.eclipse.persistence.testing.sdo.helper.datafactory.SDODataFactoryTestSuite;
import org.eclipse.persistence.testing.sdo.helper.typehelper.SDOTypeHelperTestSuite;
import org.eclipse.persistence.testing.sdo.helper.xmlhelper.SDOXMLHelperTestSuite;
import org.eclipse.persistence.testing.sdo.helper.xsdhelper.define.XSDHelperDefineTestSuite;
import org.eclipse.persistence.testing.sdo.helper.xsdhelper.generate.XSDHelperGenerateTestSuite;
import org.eclipse.persistence.testing.sdo.helper.pluggable.PluggableTestSuite;

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
        suite.addTest(new XSDHelperDefineTestSuite().suite());
        suite.addTest(new XSDHelperGenerateTestSuite().suite());
        suite.addTest(new SDOTypeHelperTestSuite().suite());
        suite.addTest(new SDODataFactoryTestSuite().suite());
        //suite.addTest(new SDOClassGenTestSuite().suite());
        suite.addTest(new SDOXMLHelperTestSuite().suite());
        suite.addTest(new PluggableTestSuite().suite());
        return suite;
    }
}