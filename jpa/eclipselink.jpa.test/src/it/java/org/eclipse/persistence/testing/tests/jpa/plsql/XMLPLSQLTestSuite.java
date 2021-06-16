/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     James Sutherland (Oracle) - initial impl
package org.eclipse.persistence.testing.tests.jpa.plsql;

import junit.framework.*;

public class XMLPLSQLTestSuite extends PLSQLTestSuite {
    public static boolean validDatabase = true;

    public static Test suite() {
        TestSuite suite = new TestSuite("XMLPLSQLTestSuite");
        suite.addTest(new XMLPLSQLTestSuite("testSetup"));
        suite.addTest(new XMLPLSQLTestSuite("testSimpleProcedure"));
        suite.addTest(new XMLPLSQLTestSuite("testSimpleFunction"));
        suite.addTest(new XMLPLSQLTestSuite("testRecordOut"));
        suite.addTest(new XMLPLSQLTestSuite("testTableOut"));
        suite.addTest(new XMLPLSQLTestSuite("testEmpRecordInOut"));
        suite.addTest(new XMLPLSQLTestSuite("testConsultant"));
        suite.addTest(new PLSQLTestSuite("testOracleTypeProcessing"));
        return suite;
    }

    public XMLPLSQLTestSuite(String name) {
        super(name);
    }

    /**
     * Return the name of the persistence context this test uses.
     * This allow a subclass test to set this only in one place.
     */
    @Override
    public String getPersistenceUnitName() {
        return "plsql-xml";
    }

}
