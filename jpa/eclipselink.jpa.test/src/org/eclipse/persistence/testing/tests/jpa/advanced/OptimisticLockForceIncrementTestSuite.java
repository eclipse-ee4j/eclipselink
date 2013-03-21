/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     dminsky - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.advanced;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.Query;

import junit.framework.TestSuite;

import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.sessions.server.ServerSession;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.Address;
import org.eclipse.persistence.testing.models.jpa.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.advanced.EmployeePopulator;

/**
 * Bug 389265 - OptimisticLock Force Increment increments version both on flush and on commit
 * 
 * - Test LockModeType.OPTIMISTIC_FORCE_INCREMENT with combinations of flush/commit/changes/no-op
 */
public class OptimisticLockForceIncrementTestSuite extends JUnitTestCase {
 
    public OptimisticLockForceIncrementTestSuite() {
        super();
    }
    
    public OptimisticLockForceIncrementTestSuite(String name) {
        super(name);
    }
    
    public static TestSuite suite() {
        TestSuite suite = new TestSuite("OptimisticLockForceIncrementTestSuite");
        suite.addTest(new OptimisticLockForceIncrementTestSuite("testSetup"));
        suite.addTest(new OptimisticLockForceIncrementTestSuite("testVersionIncrementCommitNoChanges"));
        suite.addTest(new OptimisticLockForceIncrementTestSuite("testVersionIncrementFlushCommitNoChanges"));
        suite.addTest(new OptimisticLockForceIncrementTestSuite("testVersionIncrementNoChanges"));
        suite.addTest(new OptimisticLockForceIncrementTestSuite("testVersionIncrementPostFlushChanges"));
        suite.addTest(new OptimisticLockForceIncrementTestSuite("testVersionIncrementPreAndPostFlushChanges"));
        suite.addTest(new OptimisticLockForceIncrementTestSuite("testVersionIncrementPreCommitChanges"));
        suite.addTest(new OptimisticLockForceIncrementTestSuite("testVersionIncrementPreFlushChanges"));
        return suite;
    }
    
    public void testSetup() {
        new AdvancedTableCreator().replaceTables(JUnitTestCase.getServerSession());
        
        EmployeePopulator employeePopulator = new EmployeePopulator();
        employeePopulator.buildExamples();
        employeePopulator.persistExample(JUnitTestCase.getServerSession());

        clearCache();
    }

    public void testVersionIncrementNoChanges() {
        EntityManager em = getEntityManagerFactory().createEntityManager();
        em.getTransaction().begin();
        
        List<Address> addresses = em.createQuery("select a from Address a").getResultList();
        assertNotNull("Null query results returned", addresses);
        assertNotSame("No query results returned", addresses.size(), 0);
        Address entity = addresses.get(0);
        assertNotNull("Entity: Address cannot be null", entity);

        int startVersion = entity.getVersion();
        em.lock(entity, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
        
        em.flush();
        
        em.getTransaction().commit();
        int expectedVersion = (startVersion + 1);
        int actualVersion = entity.getVersion();

        em.close();
        
        assertEquals("Version number incremented incorrectly: ", expectedVersion, actualVersion);
    }

    public void testVersionIncrementPreFlushChanges() {
        EntityManager em = getEntityManagerFactory().createEntityManager();
        
        em.getTransaction().begin();

        List<Address> addresses = em.createQuery("select a from Address a").getResultList();
        assertNotNull("Null query results returned", addresses);
        assertNotSame("No query results returned", addresses.size(), 0);
        Address entity = addresses.get(0);
        assertNotNull("Entity: Address cannot be null", entity);

        int startVersion = entity.getVersion();
        em.lock(entity, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
        entity.setCity("Vancouver");
        entity.setProvince("BC");
        entity.setCountry("Canada");
        
        em.flush();
        
        em.getTransaction().commit();
        int expectedVersion = (startVersion + 1);
        int actualVersion = entity.getVersion();

        em.close();
        
        assertEquals("Version number incremented incorrectly: ", expectedVersion, actualVersion);
        assertEquals("Entity's name should be changed", "Vancouver", entity.getCity());
    }
    
    public void testVersionIncrementPostFlushChanges() {
        EntityManager em = getEntityManagerFactory().createEntityManager();
        
        em.getTransaction().begin();

        List<Address> addresses = em.createQuery("select a from Address a").getResultList();
        assertNotNull("Null query results returned", addresses);
        assertNotSame("No query results returned", addresses.size(), 0);
        Address entity = addresses.get(0);
        assertNotNull("Entity: Address cannot be null", entity);

        int startVersion = entity.getVersion();
        em.lock(entity, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
        
        em.flush();
        
        entity.setCity("Toronto");
        entity.setProvince("ON");
        entity.setCountry("Canada");
        
        em.getTransaction().commit();
        int expectedVersion = (startVersion + 1);
        int actualVersion = entity.getVersion();

        em.close();
        
        assertEquals("Version number incremented incorrectly: ", expectedVersion, actualVersion);
        assertEquals("Entity's name should be changed", "Toronto", entity.getCity());
    }
    
    public void testVersionIncrementPreAndPostFlushChanges() {
        EntityManager em = getEntityManagerFactory().createEntityManager();
        
        em.getTransaction().begin();

        List<Address> addresses = em.createQuery("select a from Address a").getResultList();
        assertNotNull("Null query results returned", addresses);
        assertNotSame("No query results returned", addresses.size(), 0);
        Address entity = addresses.get(0);
        assertNotNull("Entity: Address cannot be null", entity);

        int startVersion = entity.getVersion();
        em.lock(entity, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
        
        entity.setCity("Moncton");
        entity.setProvince("NB");
        entity.setCountry("Canada");
        
        em.flush();
        
        entity.setCity("Halifax");
        entity.setProvince("NS");
        entity.setCountry("Canada");
        
        em.getTransaction().commit();
        int expectedVersion = (startVersion + 1);
        int actualVersion = entity.getVersion();

        em.close();
        
        assertEquals("Version number incremented incorrectly: ", expectedVersion, actualVersion);
        assertEquals("Entity's name should be changed", "Halifax", entity.getCity());
    }

    public void testVersionIncrementCommitNoChanges() {
        EntityManager em = getEntityManagerFactory().createEntityManager();
        
        em.getTransaction().begin();

        List<Address> addresses = em.createQuery("select a from Address a").getResultList();
        assertNotNull("Null query results returned", addresses);
        assertNotSame("No query results returned", addresses.size(), 0);
        Address entity = addresses.get(0);
        assertNotNull("Entity: Address cannot be null", entity);
        
        int startVersion = entity.getVersion();
        em.lock(entity, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
        
        em.getTransaction().commit();
        int expectedVersion = (startVersion + 1);
        int actualVersion = entity.getVersion();

        em.close();
        
        assertEquals("Version number incremented incorrectly: ", expectedVersion, actualVersion);
    }
    
    public void testVersionIncrementPreCommitChanges() {
        EntityManager em = getEntityManagerFactory().createEntityManager();
        
        em.getTransaction().begin();

        List<Address> addresses = em.createQuery("select a from Address a").getResultList();
        assertNotNull("Null query results returned", addresses);
        assertNotSame("No query results returned", addresses.size(), 0);
        Address entity = addresses.get(0);
        assertNotNull("Entity: Address cannot be null", entity);

        int startVersion = entity.getVersion();
        em.lock(entity, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
        
        entity.setCity("Calgary");
        entity.setProvince("AB");
        entity.setCountry("Canada");
        
        em.getTransaction().commit();
        
        int expectedVersion = (startVersion + 1);
        int actualVersion = entity.getVersion();

        em.close();
        
        assertEquals("Version number incremented incorrectly: ", expectedVersion, actualVersion);
        assertEquals("Entity's name should be changed", "Calgary", entity.getCity());
    }
    
    public void testVersionIncrementFlushCommitNoChanges() {
        EntityManager em = getEntityManagerFactory().createEntityManager();
        em.getTransaction().begin();

        List<Address> addresses = em.createQuery("select a from Address a").getResultList();
        assertNotNull("Null query results returned", addresses);
        assertNotSame("No query results returned", addresses.size(), 0);
        Address entity = addresses.get(0);
        assertNotNull("Entity: Address cannot be null", entity);

        int startVersion = entity.getVersion();
        em.lock(entity, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
        
        em.flush();
        
        em.getTransaction().commit();
        int expectedVersion = (startVersion + 1);
        int actualVersion = entity.getVersion();

        em.close();
        
        assertEquals("Version number incremented incorrectly: ", expectedVersion, actualVersion);
    }

}
