/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     James Sutherland (Oracle) - initial impl
 ******************************************************************************/ 
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