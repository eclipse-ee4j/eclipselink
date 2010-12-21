/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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


package org.eclipse.persistence.testing.tests.jpa.fieldaccess.advanced;

import junit.framework.*;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.AdvancedTableCreator;

public class NamedNativeQueryJUnitTest extends JUnitTestCase {
    public NamedNativeQueryJUnitTest() {
        super();
    }
    
    public NamedNativeQueryJUnitTest(String name) {
        super(name);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("NamedNativeQueryJUnitTest (fieldaccess)");
        suite.addTest(new NamedNativeQueryJUnitTest("testSetup"));
        suite.addTest(new NamedNativeQueryJUnitTest("testNamedNativeQuery"));
        
        return suite;
    }
    
    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new AdvancedTableCreator().replaceTables(JUnitTestCase.getServerSession("fieldaccess"));
        clearCache("fieldaccess");
    }
    
    public void testNamedNativeQuery() {
        Exception exception = null;
        
        try {
            createEntityManager("fieldaccess").createNamedQuery("findAllFieldAccessSQLAddresses").getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            exception = e;
        }
        
        assertNull("Exception was caught: " + exception, exception);
    }
}
