/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.jpa.xml.advanced;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;

import junit.framework.*;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.OptimisticLockException;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.RepeatableWriteUnitOfWork;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.AdvancedTableCreator;

import org.eclipse.persistence.testing.models.jpa.xml.advanced.Address;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.CacheAuditor;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.Man;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.Woman;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.PartnerLink;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.LargeProject;
import org.eclipse.persistence.testing.tests.jpa.TestingProperties;

public class AdvancedJunitTest extends JUnitTestCase {
    String m_persistenceUnit = "default";
    
    public AdvancedJunitTest() {
        super();
    }
    
    public AdvancedJunitTest(String name) {
        super(name);
    }
    
    public AdvancedJunitTest(String name, String persistenceUnit) {
        super(name);
        
        m_persistenceUnit = persistenceUnit;
    }
    
    
    public static Test suite() {
        String ormTesting = TestingProperties.getProperty(TestingProperties.ORM_TESTING, TestingProperties.JPA_ORM_TESTING);
        final String persistenceUnit = ormTesting.equals(TestingProperties.JPA_ORM_TESTING)? "default" : "extended-advanced";
        TestSuite suite = new TestSuite("AdvancedJunitTest - " + persistenceUnit);

        suite.addTest(new AdvancedJunitTest("testSetup", persistenceUnit));
        suite.addTest(new AdvancedJunitTest("testEL254937", persistenceUnit));
        suite.addTest(new AdvancedJunitTest("testGF1894", persistenceUnit));
        suite.addTest(new AdvancedJunitTest("testManAndWoman", persistenceUnit));
        if (persistenceUnit.equals("extended-advanced")) {            
            suite.addTest(new AdvancedJunitTest("testForRedirectorsAndInterceptors", persistenceUnit));
            suite.addTest(new AdvancedJunitTest("testForExceptionsFromInterceptors", persistenceUnit));
        }
        
        return suite;
    }
    
    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new AdvancedTableCreator().replaceTables(JUnitTestCase.getServerSession(m_persistenceUnit));

        clearCache(m_persistenceUnit);
    }
    
    /*public static EntityManager createEntityManager() {
        if (persistenceUnit==null){
            String ormTesting = TestingProperties.getProperty(TestingProperties.ORM_TESTING, TestingProperties.JPA_ORM_TESTING);
            persistenceUnit = ormTesting.equals(TestingProperties.JPA_ORM_TESTING)? "default" : "extended-advanced";
        }
    }*/
    
    public void testEL254937(){
        EntityManager em = createEntityManager(m_persistenceUnit);
        beginTransaction(em);
        LargeProject lp1 = new LargeProject();
        lp1.setName("one");
        em.persist(lp1);
        commitTransaction(em);
        em = createEntityManager(m_persistenceUnit);
        beginTransaction(em);
        em.remove(em.find(LargeProject.class, lp1.getId()));
        em.flush();
        JpaEntityManager eclipselinkEm = (JpaEntityManager)em.getDelegate();
        RepeatableWriteUnitOfWork uow = 
            (RepeatableWriteUnitOfWork)eclipselinkEm.getActiveSession();
        //duplicate the beforeCompletion call
        uow.issueSQLbeforeCompletion();
        //commit the transaction
        uow.setShouldTerminateTransaction(true);
        uow.commitTransaction();
        //duplicate the AfterCompletion call.  This should merge, removing the LargeProject from the shared cache
        uow.mergeClonesAfterCompletion();
        em = createEntityManager(m_persistenceUnit);
        LargeProject cachedLargeProject = em.find(LargeProject.class, lp1.getId());
        closeEntityManager(em);
        assertTrue("Entity removed during flush was not removed from the shared cache on commit", cachedLargeProject==null);
    }
    
    public void testGF1894() {
        EntityManager em = createEntityManager(m_persistenceUnit);
        beginTransaction(em);
        Employee emp = new Employee("Guy", "Pelletier");
        
        Address address = new Address();
        address.setCity("College Town");
        
        emp.setAddress(address);
            
        try {   
            Employee empClone = em.merge(emp);
            assertNotNull("The id field for the merged new employee object was not generated.", empClone.getId());
            commitTransaction(em);
            
            Employee empFromDB = em.find(Employee.class, empClone.getId());
            assertNotNull("The version locking field for the merged new employee object was not updated after commit.", empFromDB.getVersion());
            
            beginTransaction(em);
            Employee empClone2 = em.merge(empFromDB);
            assertTrue("The id field on a existing merged employee object was modified on a subsequent merge.", empFromDB.getId().equals(empClone2.getId()));
            commitTransaction(em);
        } catch (javax.persistence.OptimisticLockException e) {
            fail("An optimistic locking exception was caught on the merge of a new object. An insert should of occurred instead.");
        }
        
        closeEntityManager(em);
    }
    
    
    
    public void testManAndWoman() {
        EntityManager em = createEntityManager(m_persistenceUnit);
        beginTransaction(em);
        
        try {
            PartnerLink pLink1 = new PartnerLink();
            pLink1.setMan(new Man());
            em.persist(pLink1);
            
            PartnerLink pLink2 = new PartnerLink();
            pLink2.setWoman(new Woman());
            em.persist(pLink2);
            
            PartnerLink pLink3 = new PartnerLink();
            pLink3.setMan(new Man());
            pLink3.setWoman(new Woman());
            em.persist(pLink3);
            
            commitTransaction(em);
            
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            
            closeEntityManager(em);
            fail("An exception was caught: [" + e.getMessage() + "]");
        }
        
        closeEntityManager(em);
    }
    
    public void testForRedirectorsAndInterceptors() {
        ClassDescriptor descriptor = getServerSession(m_persistenceUnit).getDescriptor(Address.class);
        assertTrue("CacheInterceptor was not set on decriptor", descriptor.getCacheInterceptorClassName() != null);
        assertTrue("All queries default redirector was not set on decriptor", descriptor.getDefaultQueryRedirector() != null);
        assertTrue("Read All queries default redirector was not set on decriptor", descriptor.getDefaultReadAllQueryRedirector() != null);
        assertTrue("Read Object queries default redirector was not set on decriptor", descriptor.getDefaultReadObjectQueryRedirector() != null);
        assertTrue("Report queries default redirector was not set on decriptor", descriptor.getDefaultReportQueryRedirector() != null);
        assertTrue("Insert queries default redirector was not set on decriptor", descriptor.getDefaultInsertObjectQueryRedirector() != null);
        assertTrue("Update queries default redirector was not set on decriptor", descriptor.getDefaultUpdateObjectQueryRedirector() != null);
        assertTrue("Delete queries default redirector was not set on decriptor", descriptor.getDefaultDeleteObjectQueryRedirector() != null);
    }
    public void testForExceptionsFromInterceptors() {
        ClassDescriptor descriptor = getServerSession(m_persistenceUnit).getDescriptor(Address.class);
        CacheAuditor interceptor = (CacheAuditor) getServerSession(m_persistenceUnit).getIdentityMapAccessorInstance().getIdentityMap(descriptor);
        interceptor.setShouldThrow(true);
        EntityManager em = createEntityManager(m_persistenceUnit);
        beginTransaction(em);
        
        try {
            Address addr = new Address();
            addr.setCity("WhaHa");
            addr.setProvince("NFLD");
            em.persist(addr);
            commitTransaction(em);
            beginTransaction(em);
            em.remove(addr);
            commitTransaction(em);
            fail("There was no Optimistic Lock Exception");
        } catch (RollbackException e) {
            assertTrue("Not caused by OptimisticLockException", (e.getCause() instanceof javax.persistence.OptimisticLockException));
            
        }finally{
            interceptor.setShouldThrow(false);
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            
            closeEntityManager(em);
        }
    }
}
