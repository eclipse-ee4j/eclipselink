/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  


package org.eclipse.persistence.testing.tests.jpa.fieldaccess.advanced;

import junit.framework.*;
import junit.extensions.TestSetup;
import org.eclipse.persistence.sessions.DatabaseSession;
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
        suite.addTest(new NamedNativeQueryJUnitTest("testNamedNativeQuery"));
        
        return new TestSetup(suite) {
        
            protected void setUp(){               
                DatabaseSession session = JUnitTestCase.getServerSession("fieldaccess");
                
                new AdvancedTableCreator().replaceTables(session);
            }

            protected void tearDown() {
                clearCache("fieldaccess");
            }
        };
    }
    
    public void testNamedNativeQuery() {
        Exception exception = null;
        
        try {
            createEntityManager("fieldaccess").createNamedQuery("findAllFieldAccessSQLAddresses").getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            exception = e;
        }
        
        assertNull("Exception was caught", exception);
    }
    
    public static void main(String[] args) {
        // Now run JUnit.
        junit.swingui.TestRunner.main(args);
    }
}
