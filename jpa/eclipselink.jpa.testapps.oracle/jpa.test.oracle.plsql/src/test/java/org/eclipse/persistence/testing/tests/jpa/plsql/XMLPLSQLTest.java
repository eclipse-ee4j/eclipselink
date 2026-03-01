/*
 * Copyright (c) 2011, 2023 Oracle and/or its affiliates. All rights reserved.
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
//     James Sutherland (Oracle) - initial impl
package org.eclipse.persistence.testing.tests.jpa.plsql;

import junit.framework.Test;
import junit.framework.TestSuite;

public class XMLPLSQLTest extends PLSQLTest {
    public static boolean validDatabase = true;

    public static Test suite() {
        TestSuite suite = new TestSuite("XMLPLSQLTest");
        suite.addTest(new XMLPLSQLTest("testSetup"));
        suite.addTest(new XMLPLSQLTest("testSimpleProcedure"));
        suite.addTest(new XMLPLSQLTest("testSimpleFunction"));
        suite.addTest(new XMLPLSQLTest("testRecordOut"));
        suite.addTest(new XMLPLSQLTest("testTableOut"));
        suite.addTest(new XMLPLSQLTest("testEmpRecordInOut"));
        suite.addTest(new XMLPLSQLTest("testConsultant"));
        suite.addTest(new XMLPLSQLTest("testOracleTypeProcessing"));
        return suite;
    }

    public XMLPLSQLTest(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
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
