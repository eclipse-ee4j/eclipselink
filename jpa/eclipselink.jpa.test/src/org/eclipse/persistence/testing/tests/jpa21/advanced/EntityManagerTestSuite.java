/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     tware - initial implementation
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa21.advanced;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.persistence.TransactionRequiredException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa21.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa21.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa21.advanced.EmployeePopulator;

public class EntityManagerTestSuite extends JUnitTestCase {
    
    public EntityManagerTestSuite() {}
    
    public EntityManagerTestSuite(String name) {
        super(name);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("EntityManagerTestSuite");
        
        suite.addTest(new EntityManagerTestSuite("testSetup"));
        suite.addTest(new EntityManagerTestSuite("testGetLockModeForObject"));
        
        return suite;
    }    
    
    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new AdvancedTableCreator().replaceTables(JUnitTestCase.getServerSession());
        EmployeePopulator employeePopulator = new EmployeePopulator();
        employeePopulator.buildExamples();
        employeePopulator.persistExample(getServerSession());
        clearCache();
    }
    
    public void testGetLockModeForObject(){
        EntityManager em = createEntityManager();
        Query query = em.createQuery("select e from Employee e where e.firstName = 'Sarah' and e.lastName = 'Way'");
        query.setLockMode(LockModeType.OPTIMISTIC);
        beginTransaction(em);
        Employee emp = (Employee)query.getSingleResult();
        commitTransaction(em);
        try{
            em.getLockMode(emp);
            fail("TransactionRequiredException not thrown for getLockMode() with no transction open.");
        } catch (TransactionRequiredException e){}
        clearCache();
        try{
            em.find(Employee.class, emp.getId(), LockModeType.OPTIMISTIC);
            fail("TransactionRequiredException not thrown for find(Class, Object, LockModeType) with no transction open.");

        } catch (TransactionRequiredException e){}
    }
}
