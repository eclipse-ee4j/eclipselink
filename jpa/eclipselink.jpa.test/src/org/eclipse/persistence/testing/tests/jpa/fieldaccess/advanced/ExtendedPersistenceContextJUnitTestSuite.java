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

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import junit.framework.*;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.EmployeePopulator;

 
public class ExtendedPersistenceContextJUnitTestSuite extends JUnitTestCase {
        
    public ExtendedPersistenceContextJUnitTestSuite() {
        super();
    }
    
    public ExtendedPersistenceContextJUnitTestSuite(String name) {
        super(name);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("ExtendedPersistenceContextJUnitTestSuite (fieldaccess)");
        
        suite.addTest(new ExtendedPersistenceContextJUnitTestSuite("testSetup"));
        suite.addTest(new ExtendedPersistenceContextJUnitTestSuite("testExtendedPersistenceContext"));

        return suite;
    }
    
    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new AdvancedTableCreator().replaceTables(JUnitTestCase.getServerSession("fieldaccess"));
        //create a new EmployeePopulator
        EmployeePopulator employeePopulator = new EmployeePopulator();
        
        //Populate the tables
        employeePopulator.buildExamples();
        
        //Persist the examples in the database
        employeePopulator.persistExample(JUnitTestCase.getServerSession("fieldaccess"));

        clearCache("fieldaccess");
    }
    
    // JUnit framework will automatically execute all methods starting with test...    
    public void testExtendedPersistenceContext() {
        EntityManager em = createEntityManager("fieldaccess");
        beginTransaction(em);
        Query query = em.createQuery("select e from Employee e");
        List result = query.getResultList();
        if (result.isEmpty()){
            fail("Database not setup correctly");
        }
        Object obj = result.get(0);
        commitTransaction(em);
        if (isOnServer()) {
            assertFalse("Extended PersistenceContext did not continue to maintain object after commit.", em.contains(obj));
        } else {
            assertTrue("Extended PersistenceContext did not continue to maintain object after commit.", em.contains(obj));
        }
        closeEntityManager(em);
    }
}
