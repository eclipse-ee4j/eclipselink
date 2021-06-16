/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
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
