/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
import org.eclipse.persistence.testing.models.jpa.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.advanced.EmployeeListener;
import org.eclipse.persistence.testing.models.jpa.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.advanced.Project;

import java.util.Vector;

import javax.persistence.EntityManager;

import junit.framework.TestSuite;
import junit.framework.Test;


/*
 * Suite used to contain Entity callback tests
 */
public class CallbackEventJUnitTestSuite extends JUnitTestCase {
    protected boolean m_reset = false;    // reset gets called twice on error
    protected Employee new_emp =null;
    protected int m_beforeEvent, m_afterEvent;
    
        
    public CallbackEventJUnitTestSuite() {
    }
    
    public CallbackEventJUnitTestSuite(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("CallbackEventJUnitTestSuite");
        
        suite.addTest(new CallbackEventJUnitTestSuite("testSetup"));
        suite.addTest(new CallbackEventJUnitTestSuite("testPersistThenRemoveCalls"));
        suite.addTest(new CallbackEventJUnitTestSuite("testRemoveUnmanagedNewEntity"));
        suite.addTest(new CallbackEventJUnitTestSuite("testPersistOnRegisteredObject"));
        suite.addTest(new CallbackEventJUnitTestSuite("testPreUpdateEvent_UpdateAltered"));
        suite.addTest(new CallbackEventJUnitTestSuite("testPreUpdateEvent_UpdateReverted"));
        suite.addTest(new CallbackEventJUnitTestSuite("testMergeCascadeTriggersPrePersist"));
        
        return suite;
    }
    
    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new AdvancedTableCreator().replaceTables(JUnitTestCase.getServerSession());
        
        clearCache();
    }
    
    public void setUp () {
        m_reset = true;
        super.setUp();
        //populate
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
            new_emp = new Employee();
            new_emp.setFirstName("New");
            new_emp.setLastName("Guy");
            em.persist(new_emp);
            commitTransaction(em);
        }catch (RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw ex;
        }
            
        clearCache();
    }
    
    /*
     * test for bug 4568370:TopLink should perform an unregister on the remove call on a new object
     *   Calls persist/remove on an existing object which will cause a DB exception if the insert
     *   is generated.  It also checks that the prepersist/preremove callbacks get issued
     */
    public void testPersistThenRemoveCalls() throws Exception {
        clearCache();
        m_beforeEvent = EmployeeListener.PRE_REMOVE_COUNT;
        int m_beforePrePersistEvent = EmployeeListener.PRE_PERSIST_COUNT;
        
        EntityManager em = createEntityManager();
        beginTransaction(em);
        Employee employee = new Employee();
        try {
            employee = new Employee();
            em.persist(employee);
            em.remove(employee);
            commitTransaction(em);
        } catch (RuntimeException ex) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw ex;
        }
        m_afterEvent = EmployeeListener.PRE_REMOVE_COUNT;
        int m_afterPrePersistEvent = EmployeeListener.PRE_PERSIST_COUNT;
        if (em.find(Employee.class, employee.getId()) != null) {
            fail("Employee was inserted.");
        }
        closeEntityManager(em);
        assertTrue("The prePersist callback method was not called.", m_beforePrePersistEvent != m_afterPrePersistEvent);
        assertTrue("The preRemove callback method was not called.", m_beforeEvent != m_afterEvent);
    }
    
    
    public void testRemoveUnmanagedNewEntity() throws Exception {
        m_beforeEvent = EmployeeListener.PRE_REMOVE_COUNT;
        clearCache();
        EntityManager em = createEntityManager();
        beginTransaction(em);
        //new_emp should exist only in db
        try{
            Employee newEmp = new Employee();
            newEmp.setFirstName("new");
            newEmp.setLastName("guy2");
            em.remove(newEmp);
            commitTransaction(em);
        }catch (RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw ex;
        }
        m_afterEvent = EmployeeListener.PRE_REMOVE_COUNT;
        closeEntityManager(em);
        
        this.assertTrue("The preRemove callback method was called, remove should have been ignored.", m_beforeEvent == m_afterEvent);
        //Employee emp = (Employee)em.find(Employee.class, new_emp.getId());
        
        //this.assertTrue("The remove should have been ignored.", m_beforeEvent == m_afterEvent);
    }
    
    public void testPersistOnRegisteredObject() {
        clearCache();
        EntityManager em = createEntityManager();
        beginTransaction(em);
        //create new employee and persist it
        try{
            Employee newEmp = new Employee();
            newEmp.setFirstName("new2");
            newEmp.setLastName("guy2");
            em.persist(newEmp);
            m_beforeEvent = EmployeeListener.POST_PERSIST_COUNT;
            em.persist(newEmp);
            m_afterEvent = EmployeeListener.POST_PERSIST_COUNT;        
            rollbackTransaction(em);
        }catch (RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw ex;
        }

        
        this.assertTrue("Calling persist on a managed object should be ignored", m_beforeEvent==m_afterEvent);
    }
    
    public void testPreUpdateEvent_UpdateAltered() {
        internalTestPreUpdateEvent(false);
    }
    
    public void testPreUpdateEvent_UpdateReverted() {
        internalTestPreUpdateEvent(true);
    }
    
    // PreUpdate event support must change to allow data modifications in event[PreUpdate]
    protected void internalTestPreUpdateEvent(boolean shouldUseOriginalName) {
        EntityManager em = createEntityManager();
        Employee emp = null;
        int originalVersion = 0;
        String firstNameExpectedAfterCommit = "";
        beginTransaction(em);
        try{
            emp = em.find(Employee.class, new_emp.getId());
            originalVersion = getVersion(emp);
            if(shouldUseOriginalName) {
                firstNameExpectedAfterCommit = emp.getFirstName();
            } else {
                firstNameExpectedAfterCommit = "Updated" + emp.getFirstName();
            }
            // Assign a new first name to the employee:
            // it consists of a EmployeeListener.PRE_UPDATE_NAME_PREFIX
            // and either original first name or an updated one.
            String firstNameAssigned = EmployeeListener.PRE_UPDATE_NAME_PREFIX + firstNameExpectedAfterCommit;
            emp.setFirstName(firstNameAssigned);
            commitTransaction(em);
        }catch (RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw ex;
        }

        // On commit, EmployeeListener will process PreUpdate event removing
        // EmployeeListener.PRE_UPDATE_NAME_PREFIX from firstName - therefore after commit
        // employee's first name should be firstNameExpectedAfterCommit.
        // In case shouldUseOriginalName==true, there should've been no update performed at all -
        // because by removing the prefix EmployeeListener reverts the object to its original state -
        // therefore in this case employee's versions before and after commit should be the same.
        // However change tracking will perform the update...
        
        // Verify the employee object from the cache first.
        emp = em.find(Employee.class, new_emp.getId());
        if(!emp.getFirstName().equals(firstNameExpectedAfterCommit)) {
            fail("In cache: wrong firstName = " + emp.getFirstName() + "; " + firstNameExpectedAfterCommit + " was expected");
        }
        int version = getVersion(emp);
        // Only check version if it was changed, because if it was reverted back change tracking will still detect change.
        if(!shouldUseOriginalName) {
            if(originalVersion >= version) {
                fail("In cache: wrong version = " + version + "; version > " + originalVersion + " was expected");
            }
        }

        // Verify the employee object from the db.
        emp = (Employee) em.createQuery("SELECT OBJECT(e) FROM Employee e WHERE e.id = "+new_emp.getId()).setHint("eclipselink.refresh", Boolean.TRUE).getSingleResult();
        if(!emp.getFirstName().equals(firstNameExpectedAfterCommit)) {
            fail("In DB: wrong firstName = " + emp.getFirstName() + "; " + firstNameExpectedAfterCommit + " was expected");
        }
        version = getVersion(emp);
        if(!shouldUseOriginalName) {
            if(originalVersion >= version) {
                fail("In DB: wrong version = " + version + "; version > " + originalVersion + " was expected");
            }
        }
    }
    
    // helper method, used by internalTestPreUpdate
    protected int getVersion(Employee emp) {
        Vector pk = new Vector();
        pk.add(emp.getId());
        return ((Integer)getServerSession().getDescriptor(Employee.class).getOptimisticLockingPolicy().getWriteLockValue(emp, pk, getServerSession())).intValue();
    }
    
    // gf 2894:  merge does not trigger prePersist callbacks
    public void testMergeCascadeTriggersPrePersist() {
        clearCache();
        EntityManager em = createEntityManager();

        try {
            beginTransaction(em);
            // create an employee and persist it
            Employee emp = new Employee();
            emp.setFirstName("James");
            emp.setLastName("Dean");
            em.persist(emp);

            // create a new project and associate to persistent employee
            Project newProj  = new Project();
            newProj.setName("Rebel Without a Cause");
            emp.addProject(newProj);
            newProj.addTeamMember(emp);
            // merge should trigger prePersist callback on project
            em.merge(emp);
            // merge returns a managed copy of an unmanaged instance
            Project managedProj = emp.getProjects().iterator().next();
            this.assertTrue("Cascading merge to a new object should trigger prePersist callback", managedProj.pre_persist_count == 1);        
            em.merge(emp);
            // second merge should be ignored
            this.assertTrue("prePersist callback should only be triggered once", managedProj.pre_persist_count == 1);        
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }
    
}
