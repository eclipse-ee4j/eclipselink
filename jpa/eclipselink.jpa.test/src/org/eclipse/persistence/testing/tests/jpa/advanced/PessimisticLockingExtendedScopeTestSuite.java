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
    
    //Entity relationships for which the locked entity contains the foreign key will be locked with bidirectional one-to-one mapping without mappedBy (Scenario 1.1)
    public void testPESSMISTIC_ES1() throws Exception{
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
                a.setName("test");
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
            properties.put(QueryHints.PESSIMISTIC_LOCK_SCOPE, PessimisticLockScope.EXTENDED);
            EntityManager em1= createEntityManager();

            try{
                beginTransaction(em1);
                a = em1.find(EntyA.class, a.getId());
                em1.lock(a, lockMode, properties);
                EntityManager em2 = createEntityManager();
                try{
                    beginTransaction(em2);
                    a = em2.find(EntyA.class, a.getId());
                    a.setEntyC(null);
                    commitTransaction(em2);
                }catch(javax.persistence.RollbackException ex){
                    ex.printStackTrace();
                    if (ex.getMessage().indexOf("org.eclipse.persistence.exceptions.DatabaseException") == -1){
                        ex.printStackTrace();
                        fail("it's not the right exception");
                    }
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
    
    //Entity relationships for which the locked entity contains the foreign key will be locked with unidirectional one-to-one mapping(Scenario 1.2)
    public void testPESSMISTIC_ES2() throws Exception{
        // Cannot create parallel entity managers in the server.
        if (! isOnServer() && isSelectForUpateSupported()) {
            EntityManager em = createEntityManager();
            EntyA a = null;
            EntyB b = null;
            try{
                beginTransaction(em);
                a = new EntyA();
                b = new EntyB();
                a.setEntyB(b);
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
            properties.put(QueryHints.PESSIMISTIC_LOCK_SCOPE, PessimisticLockScope.EXTENDED);
            EntityManager em1= createEntityManager();

            try{
                beginTransaction(em1);
                a = em1.find(EntyA.class, a.getId());
                em1.lock(a, lockMode, properties);
                EntityManager em2 = createEntityManager();
                try{
                    beginTransaction(em2);
                    a = em2.find(EntyA.class, a.getId());
                    a.setEntyB(null);
                    commitTransaction(em2);
                }catch(javax.persistence.RollbackException ex){
                    if (ex.getMessage().indexOf("org.eclipse.persistence.exceptions.DatabaseException") == -1){
                        ex.printStackTrace();
                        fail("it's not the right exception");
                    }
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

    //Entity relationships for which the locked entity contains the foreign key will be locked with unidirectional many-to-one mapping(Scenario 1.3)
    public void testPESSMISTIC_ES3() throws Exception{
        // Cannot create parallel entity managers in the server.
        if (! isOnServer() && isSelectForUpateSupported()) {
            EntityManager em = createEntityManager();
            Equipment eq = null;
            EquipmentCode eqCode = null;
            try{
                beginTransaction(em);
                eq = new Equipment();
                eqCode = new EquipmentCode();
                eqCode.setCode("A");
                em.persist(eqCode);
                eq.setEquipmentCode(eqCode);
                em.persist(eq);
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
            properties.put(QueryHints.PESSIMISTIC_LOCK_SCOPE, PessimisticLockScope.EXTENDED);
            EntityManager em1= createEntityManager();

            try{
                beginTransaction(em1);
                eq = em1.find(Equipment.class, eq.getId());
                em1.lock(eq, lockMode, properties);
                EntityManager em2 = createEntityManager();
                try{
                    beginTransaction(em2);
                    eq = em2.find(Equipment.class, eq.getId());
                    eq.setEquipmentCode(null);
                    commitTransaction(em2);
                }catch(javax.persistence.RollbackException ex){
                    if (ex.getMessage().indexOf("org.eclipse.persistence.exceptions.DatabaseException") == -1){
                        ex.printStackTrace();
                        fail("it's not the right exception");
                    }
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

    //Entity relationships for which the locked entity contains the foreign key will be locked with bidirectional many-to-one mapping(Scenario 1.4)
    public void testPESSMISTIC_ES4() throws Exception{
        // Cannot create parallel entity managers in the server.
        if (! isOnServer() && isSelectForUpateSupported()) {
            EntityManager em = createEntityManager();
            Employee emp = null;
            Address ads = null;
            try{
                beginTransaction(em);
                emp = new Employee();
                ads = new Address("SomeStreet", "somecity", "province", "country", "postalcode");
                emp.setAddress(ads);
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
            properties.put(QueryHints.PESSIMISTIC_LOCK_SCOPE, PessimisticLockScope.EXTENDED);
            EntityManager em1= createEntityManager();

            try{
                beginTransaction(em1);
                emp = em1.find(Employee.class, emp.getId());
                em1.lock(emp, lockMode, properties);
                EntityManager em2 = createEntityManager();
                try{
                    beginTransaction(em2);
                    emp = em2.find(Employee.class, emp.getId());
                    Address address = null;
                    emp.setAddress(address);
                    commitTransaction(em2);
                }catch(javax.persistence.RollbackException ex){
                    if (ex.getMessage().indexOf("org.eclipse.persistence.exceptions.DatabaseException") == -1){
                        ex.printStackTrace();
                        fail("it's not the right exception");
                    }
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

    //Relationships owned by the entity that are contained in join tables will be locked with Unidirectional OneToMany mapping (Scenario 2.2)
    public void testPESSMISTIC_ES5() throws Exception{
        // Cannot create parallel entity managers in the server.
        if (! isOnServer() && isSelectForUpateSupported()) {
            EntityManager em = createEntityManager();
            EntyA entyA = null;
            EntyD entyD = null;
            try{
                beginTransaction(em);
                entyA = new EntyA();
                em.persist(entyA);
                entyA.getEntyDs().add(new EntyD());
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
            properties.put(QueryHints.PESSIMISTIC_LOCK_SCOPE, PessimisticLockScope.EXTENDED);
            EntityManager em1= createEntityManager();
            try{
                beginTransaction(em1);
                entyA = em1.find(EntyA.class, entyA.getId());
                em1.lock(entyA, lockMode, properties);
                EntityManager em2 = createEntityManager();
                try{
                    beginTransaction(em2);
                    entyA = em2.find(EntyA.class, entyA.getId());
                    entyA.setEntyDs(null);
                    commitTransaction(em2);
                }catch(javax.persistence.RollbackException ex){
                    if (ex.getMessage().indexOf("org.eclipse.persistence.exceptions.DatabaseException") == -1){
                        ex.printStackTrace();
                        fail("it's not the right exception");
                    }
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

    //Relationships owned by the entity that are contained in join tables will be locked with Unidirectional ManyToMany mapping (Scenario 2.3)
    public void testPESSMISTIC_ES6() throws Exception{
        // Cannot create parallel entity managers in the server.
        if (! isOnServer() && isSelectForUpateSupported()) {
            EntityManager em = createEntityManager();
            EntyA entyA = null;
            EntyE entyE1, entyE2 = null;
            try{
                beginTransaction(em);
                Collection entyEs = new ArrayList();
                entyA = new EntyA();
                entyE1 = new EntyE();
                entyE2 = new EntyE();
                entyEs.add(entyE1);
                entyEs.add(entyE2);
                entyA.setEntyEs(entyEs);
                em.persist(entyA);
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
            properties.put(QueryHints.PESSIMISTIC_LOCK_SCOPE, PessimisticLockScope.EXTENDED);
            EntityManager em1= createEntityManager();
            try{
                beginTransaction(em1);
                entyA = em1.find(EntyA.class, entyA.getId());
                em1.lock(entyA, lockMode, properties);
                EntityManager em2 = createEntityManager();
                try{
                    beginTransaction(em2);
                    entyA = em2.find(EntyA.class, entyA.getId());
                    entyA.setEntyEs(null);
                    commitTransaction(em2);
                }catch(javax.persistence.RollbackException ex){
                    if (ex.getMessage().indexOf("org.eclipse.persistence.exceptions.DatabaseException") == -1){
                        ex.printStackTrace();
                        fail("it's not the right exception");
                    }
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
                }catch(javax.persistence.RollbackException ex){
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
