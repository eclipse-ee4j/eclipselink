/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.sdo;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.sdo.externalizable.SDOResolvableTestSuite;
import org.eclipse.persistence.testing.sdo.helper.SDOHelperTestSuite;
import org.eclipse.persistence.testing.sdo.model.SDOModelTestSuite;

public class SDOTestSuite {
    public SDOTestSuite() {
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    /**
    *  Inherited suite method for generating all test cases.
    *  When not running via Ant
    * @return
    */
    public static Test suite() {
        TestSuite suite = new TestSuite("All SDO Tests");
        SDOHelperTestSuite htsu = new SDOHelperTestSuite();
        SDOModelTestSuite mtsu = new SDOModelTestSuite();
        SDOResolvableTestSuite rtsu = new SDOResolvableTestSuite();
        suite.addTest(htsu.suite());
        suite.addTest(mtsu.suite());
        suite.addTest(rtsu.suite());
        return suite;
    }
}
