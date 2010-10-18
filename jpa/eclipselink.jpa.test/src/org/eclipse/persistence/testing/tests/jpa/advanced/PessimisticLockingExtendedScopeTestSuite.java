/*******************************************************************************
 * Copyright (c) 2010 Oracle, SAP. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     SAP    - tests rewritten
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.advanced;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.*;
import org.eclipse.persistence.testing.models.jpa.advanced.entities.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import junit.framework.Test;
import junit.framework.TestSuite;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PessimisticLockScope;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.sessions.server.ServerSession;

/**
 * <p>
 * <b>Purpose</b>: Test Pessimistic Locking Extended Scope functionality.
 * <p>
 * <b>Description</b>: Test the relationship will be locked or unlocked under different situations
 * <p>
 */
 public class PessimisticLockingExtendedScopeTestSuite extends JUnitTestCase {

    public PessimisticLockingExtendedScopeTestSuite() {
        super();
    }

    public PessimisticLockingExtendedScopeTestSuite(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("PessimisticLocking ExtendedScope TestSuite");
        suite.addTest(new PessimisticLockingExtendedScopeTestSuite("testSetup"));
        suite.addTest(new PessimisticLockingExtendedScopeTestSuite("testPESSMISTIC_ES1"));
        suite.addTest(new PessimisticLockingExtendedScopeTestSuite("testPESSMISTIC_ES2"));
        suite.addTest(new PessimisticLockingExtendedScopeTestSuite("testPESSMISTIC_ES3"));
        suite.addTest(new PessimisticLockingExtendedScopeTestSuite("testPESSMISTIC_ES4"));
        suite.addTest(new PessimisticLockingExtendedScopeTestSuite("testPESSMISTIC_ES5"));
        suite.addTest(new PessimisticLockingExtendedScopeTestSuite("testPESSMISTIC_ES6"));
        suite.addTest(new PessimisticLockingExtendedScopeTestSuite("testPESSMISTIC_ES7"));
        suite.addTest(new PessimisticLockingExtendedScopeTestSuite("testPESSMISTIC_ES8"));
        suite.addTest(new PessimisticLockingExtendedScopeTestSuite("testPESSMISTIC_ES9"));
        return suite;
    }
    
    public void testSetup() {
        ServerSession session = JUnitTestCase.getServerSession();
        new AdvancedTableCreator().replaceTables(session);
        //make the entity EquipmentCode read-write for the following tests
        ClassDescriptor descriptor = session.getDescriptor(EquipmentCode.class);
        boolean shouldBeReadOnly = descriptor.shouldBeReadOnly();
        descriptor.setShouldBeReadOnly(false);
        clearCache();
    }
    
    interface Actor<X> {
        void setup(EntityManager em);
        
        X getEntityToLock(EntityManager em);
        
        void modify(EntityManager em);
        
        void check(EntityManager em, X lockedEntity);
    }
    
    // Entity relationships for which the locked entity contains the foreign key
    // will be locked with bidirectional one-to-one mapping without mappedBy
    // (Scenario 1.1)
    public void testPESSMISTIC_ES1() throws Exception {
        final EntyA a = new EntyA();
        
        final Actor actor = new Actor<EntyA>() {

            public void setup(EntityManager em) {
                EntyC c = new EntyC();
                em.persist(c);
                a.setName("test");
                a.setEntyC(c);
                em.persist(a);
            }

            public EntyA getEntityToLock(EntityManager em) {
                return em.find(EntyA.class, a.getId());
            }

            public void modify(EntityManager em) {
                EntyA a2 = em.find(EntyA.class, a.getId());
                a2.setEntyC(null);
            }

            public void check(EntityManager em, EntyA lockedEntity) {
                em.refresh(lockedEntity);
                assertNotNull("other transaction modified row concurrently", lockedEntity.getEntyC());
            }
            
        };
        
        testNonrepeatableRead(actor);
    }

    // Entity relationships for which the locked entity contains the foreign key
    // will be locked with unidirectional one-to-one mapping(Scenario 1.2)
    public void testPESSMISTIC_ES2() throws Exception {
        final EntyA a = new EntyA();
        
        final Actor actor = new Actor<EntyA>() {

            public void setup(EntityManager em) {
                EntyB b = new EntyB();
                a.setEntyB(b);
                em.persist(a);
            }

            public EntyA getEntityToLock(EntityManager em1) {
                return em1.find(EntyA.class, a.getId());
            }

            public void modify(EntityManager em2) {
                EntyA a2 = em2.find(EntyA.class, a.getId());
                a2.setEntyB(null);
            }

            public void check(EntityManager em1, EntyA lockedEntity) {
                em1.refresh(lockedEntity);
                assertNotNull("other transaction modified row concurrently", lockedEntity.getEntyB());
            }
            
        };
        
        testNonrepeatableRead(actor);
    }


    // Entity relationships for which the locked entity contains the foreign key
    // will be locked with unidirectional many-to-one mapping(Scenario 1.3)
    public void testPESSMISTIC_ES3() throws Exception {
        final Equipment eq = new Equipment();
        
        final Actor actor = new Actor<Equipment>() {

            public void setup(EntityManager em) {
                EquipmentCode eqCode = new EquipmentCode();
                eqCode.setCode("A");
                em.persist(eqCode);
                eq.setEquipmentCode(eqCode);
                em.persist(eq);
            }

            public Equipment getEntityToLock(EntityManager em1) {
                return em1.find(Equipment.class, eq.getId());
            }

            public void modify(EntityManager em2) {
                Equipment eq2 = em2.find(Equipment.class, eq.getId());
                eq2.setEquipmentCode(null);
            }

            public void check(EntityManager em1, Equipment lockedEntity) {
                em1.refresh(lockedEntity);
                assertNotNull("other transaction modified row concurrently", lockedEntity.getEquipmentCode());
            }
            
        };
        
        testNonrepeatableRead(actor);
    }
    

    // Entity relationships for which the locked entity contains the foreign key
    // will be locked with bidirectional many-to-one mapping(Scenario 1.4)
    public void testPESSMISTIC_ES4() throws Exception {
        final Employee emp = new Employee();
        
        final Actor actor = new Actor<Employee>() {

            public void setup(EntityManager em) {
                Address ads = new Address("SomeStreet", "somecity", "province", "country", "postalcode");
                emp.setAddress(ads);
                em.persist(emp);
            }

            public Employee getEntityToLock(EntityManager em1) {
                return em1.find(Employee.class, emp.getId());
            }

            public void modify(EntityManager em2) {
                Employee emp2 = em2.find(Employee.class, emp.getId());
                emp2.setAddress((Address)null);
            }

            public void check(EntityManager em1, Employee lockedEntity) {
                em1.refresh(lockedEntity);
                assertNotNull("other transaction modified row concurrently", lockedEntity.getAddress());
            }
            
        };
        
        testNonrepeatableRead(actor);
    }

    
    // Relationships owned by the entity that are contained in join tables will
    // be locked with Unidirectional OneToMany mapping (Scenario 2.2)
    public void testPESSMISTIC_ES5() throws Exception {
        final EntyA entyA = new EntyA();
        
        final Actor actor = new Actor<EntyA>() {

            public void setup(EntityManager em) {
                em.persist(entyA);
                entyA.getEntyDs().add(new EntyD());
            }

            public EntyA getEntityToLock(EntityManager em1) {
                return em1.find(EntyA.class, entyA.getId());
            }

            public void modify(EntityManager em2) {
                EntyA entyA2 = em2.find(EntyA.class, entyA.getId());
                entyA2.setEntyDs(null);
            }

            public void check(EntityManager em1, EntyA lockedEntity) {
                em1.refresh(lockedEntity);
                assertNotNull("other transaction modified row concurrently", lockedEntity.getEntyDs());
                assertFalse("other transaction modified row concurrently", lockedEntity.getEntyDs().isEmpty());
            }
            
        };
        
        testNonrepeatableRead(actor);
    }
    
    
    //Relationships owned by the entity that are contained in join tables will be locked with Unidirectional ManyToMany mapping (Scenario 2.3)
    public void testPESSMISTIC_ES6() throws Exception {
        final EntyA entyA = new EntyA();
        
        final Actor actor = new Actor<EntyA>() {

            public void setup(EntityManager em) {
                Collection entyEs = new ArrayList();
                EntyE entyE1 = new EntyE();
                EntyE entyE2 = new EntyE();
                entyEs.add(entyE1);
                entyEs.add(entyE2);
                entyA.setEntyEs(entyEs);
                em.persist(entyA);
            }

            public EntyA getEntityToLock(EntityManager em1) {
                return em1.find(EntyA.class, entyA.getId());
            }

            public void modify(EntityManager em2) {
                EntyA entyA2 = em2.find(EntyA.class, entyA.getId());
                entyA2.setEntyEs(null);
            }

            public void check(EntityManager em1, EntyA lockedEntity) {
                em1.refresh(lockedEntity);
                assertNotNull("other transaction modified row concurrently", lockedEntity.getEntyEs());
                assertFalse("other transaction modified row concurrently", lockedEntity.getEntyEs().isEmpty());
            }
            
        };
        
        testNonrepeatableRead(actor);
    }


    
        /*
     * The test should assert that the following phenomenon does not occur
     * after a row has been locked by T1:
     * 
     * - P2 (Non-repeatable read): Transaction T1 reads a row. Another
     * transaction T2 then modifies or deletes that row, before T1 has
     * committed or rolled back.
     */
    private <X> void testNonrepeatableRead(final Actor<X> actor) throws InterruptedException {
        // Cannot create parallel entity managers in the server.
        if (isOnServer() || !isSelectForUpateSupported()) {
            return;
        }
        
        EntityManager em = createEntityManager();
        EntyC c = null;
        try {
            beginTransaction(em);
            actor.setup(em);
            commitTransaction(em);
        } catch (RuntimeException ex) {
            throw ex;
        } finally {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }

        Exception lockTimeOutException = null;
        LockModeType lockMode = LockModeType.PESSIMISTIC_WRITE;
        Map<String, Object> properties = new HashMap();
        properties.put(QueryHints.PESSIMISTIC_LOCK_SCOPE, PessimisticLockScope.EXTENDED);
        properties.put(QueryHints.PESSIMISTIC_LOCK_TIMEOUT, 10);

        EntityManager em1 = createEntityManager();
        try {
            beginTransaction(em1);
            X locked = actor.getEntityToLock(em1); 
            em1.lock(locked, lockMode, properties);

            final EntityManager em2 = createEntityManager();
            try {
                // P2 (Non-repeatable read)
                Runnable runnable = new Runnable() {
                    public void run() {
                        try {
                            beginTransaction(em2);
                            actor.modify(em2);
                            commitTransaction(em2); // might wait for lock to be released
                        } catch (javax.persistence.RollbackException ex) {
                            if (ex.getMessage().indexOf("org.eclipse.persistence.exceptions.DatabaseException") == -1) {
                                ex.printStackTrace();
                                fail("it's not the right exception");
                            }
                        }
                    }
                };

                Thread t2 = new Thread(runnable);
                t2.start();
                Thread.sleep(1000);       // allow t2 to atempt update
                actor.check(em1, locked); // assert repeatable read
                rollbackTransaction(em1); // release lock
                t2.join();                // wait until t2 finished
            } finally {
                if (isTransactionActive(em2)) {
                    rollbackTransaction(em2);
                }
                closeEntityManager(em2);
            }
        } finally {
            if (isTransactionActive(em1)) {
                rollbackTransaction(em1);
            }
            closeEntityManager(em1);
        }
    }
    


    //Bidirectional OneToOne Relationship with target entity has foreign key, entity does not contain the foreign key will not be locked (Scenario 3.1)
    public void testPESSMISTIC_ES7() throws Exception{
        // Cannot create parallel entity managers in the server.
        if (! isOnServer() && isSelectForUpateSupported()) {
            EntityManager em = createEntityManager();
            EntyA a = null;
            EntyC c = null;
            try{
                beginTransaction(em);
                a = new EntyA();
                c = new EntyC();
                em.persist(c);
                a.setEntyC(c);
                em.persist(a);
                commitTransaction(em);
            }catch (RuntimeException ex){
                throw ex;
            }finally{
                if (isTransactionActive(em)){
                    rollbackTransaction(em);
                }
                closeEntityManager(em);
            }

            Exception lockTimeOutException = null;
            LockModeType lockMode = LockModeType.PESSIMISTIC_WRITE;
            Map<String, Object> properties = new HashMap();
            properties.put(QueryHints.PESSIMISTIC_LOCK_SCOPE, PessimisticLockScope.NORMAL);
            EntityManager em1= createEntityManager();

            try{
                beginTransaction(em1);
                c = em1.find(EntyC.class, c.getId());
                em1.lock(c, lockMode, properties);
                EntityManager em2 = createEntityManager();
                try{
                    beginTransaction(em2);
                    c = em2.find(EntyC.class, c.getId());
                    c.setEntyA(null);
                    commitTransaction(em2);
                } catch(javax.persistence.RollbackException ex){
                    fail("it should not throw the exception!!!");
                }finally{
                    if (isTransactionActive(em2)){
                        rollbackTransaction(em2);
                    }
                    closeEntityManager(em2);
                }
            }catch (Exception ex){
                throw ex;
            }finally{
                if (isTransactionActive(em1)){
                    rollbackTransaction(em1);
                }
                closeEntityManager(em1);
            }
        }
    }

    //Unidirectional OneToMany Relationship, in which entity does not contain the foreign key will not be locked (Scenario 3.2)
    public void testPESSMISTIC_ES8() throws Exception{
        // Cannot create parallel entity managers in the server.
        if (! isOnServer() && isSelectForUpateSupported()) {
            EntityManager em = createEntityManager();
            Employee emp = null;
            try{
                beginTransaction(em);
                emp = new Employee();
                emp.getDealers().add(new Dealer("Honda", "Kanata"));
                em.persist(emp);
                commitTransaction(em);
            }catch (RuntimeException ex){
                throw ex;
            }finally{
                if (isTransactionActive(em)){
                    rollbackTransaction(em);
                }
                closeEntityManager(em);
            }

            Exception lockTimeOutException = null;
            LockModeType lockMode = LockModeType.PESSIMISTIC_WRITE;
            Map<String, Object> properties = new HashMap();
            properties.put(QueryHints.PESSIMISTIC_LOCK_SCOPE, PessimisticLockScope.NORMAL);
            EntityManager em1= createEntityManager();

            try{
                beginTransaction(em1);
                emp = em1.find(Employee.class, emp.getId());
                em1.lock(emp, lockMode, properties);
                EntityManager em2 = createEntityManager();
                try{
                    beginTransaction(em2);
                    emp = em1.find(Employee.class, emp.getId());
                    emp.setDealers(null);
                    commitTransaction(em2);
                }catch(javax.persistence.RollbackException ex){
                    fail("it should not throw the exception!!!");
                }finally{
                    if (isTransactionActive(em2)){
                        rollbackTransaction(em2);
                    }
                    closeEntityManager(em2);
                }
            }catch (Exception ex){
                fail("it should not throw the exception!!!");
                throw ex;
            }finally{
                if (isTransactionActive(em1)){
                    rollbackTransaction(em1);
                }
                closeEntityManager(em1);
            }
        }
    }

    //Bidirectional ManyToMany Relationship, in which entity does not contain the foreign key will not be locked by default (Scenario 3.3)
    public void testPESSMISTIC_ES9() throws Exception{
        // Cannot create parallel entity managers in the server.
        if (! isOnServer() && isSelectForUpateSupported()) {
            EntityManager em = createEntityManager();
            Employee emp = null;
            try{
                beginTransaction(em);
                emp = new Employee();
                SmallProject smallProject = new SmallProject();
                smallProject.setName("New High School Set Up");
                emp.addProject(smallProject);
                LargeProject largeProject = new LargeProject();
                largeProject.setName("Downtown Light Rail");
                largeProject.setBudget(5000);
                emp.addProject(largeProject);
                em.persist(emp);
                commitTransaction(em);
            }catch (RuntimeException ex){
                throw ex;
            }finally{
                if (isTransactionActive(em)){
                    rollbackTransaction(em);
                }
                closeEntityManager(em);
            }

            Exception lockTimeOutException = null;
            LockModeType lockMode = LockModeType.PESSIMISTIC_WRITE;
            Map<String, Object> properties = new HashMap();
            properties.put(QueryHints.PESSIMISTIC_LOCK_SCOPE, PessimisticLockScope.NORMAL);
            EntityManager em1= createEntityManager();

            try{
                beginTransaction(em1);
                emp = em1.find(Employee.class, emp.getId());
                em1.lock(emp, lockMode, properties);
                EntityManager em2 = createEntityManager();
                try{
                    beginTransaction(em2);
                    emp = em1.find(Employee.class, emp.getId());
                    emp.setProjects(null);
                    commitTransaction(em2);
                }catch(javax.persistence.RollbackException ex){
                    fail("it should not throw the exception!!!");
                }finally{
                    if (isTransactionActive(em2)){
                        rollbackTransaction(em2);
                    }
                    closeEntityManager(em2);
                }
            }catch (Exception ex){
                fail("it should not throw the exception!!!");
                throw ex;
            }finally{
                if (isTransactionActive(em1)){
                    rollbackTransaction(em1);
                }
                closeEntityManager(em1);
            }
        }
    }
}
