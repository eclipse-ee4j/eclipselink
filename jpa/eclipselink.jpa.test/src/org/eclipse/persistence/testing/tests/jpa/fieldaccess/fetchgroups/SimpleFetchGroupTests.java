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
 *     05/19/2010-2.1 ailitchev - Bug 244124 - Add Nested FetchGroup 
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.jpa.fieldaccess.fetchgroups;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import junit.framework.TestSuite;

import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.indirection.IndirectList;
import org.eclipse.persistence.queries.FetchGroup;

import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.PhoneNumber;

import org.junit.Test;

/**
 * Simple tests to verify the functionality of single level FetchGroup usage
 * 
 * @author dclarke
 * @since EclipseLink 2.1
 */
public class SimpleFetchGroupTests extends BaseFetchGroupTests {

    public SimpleFetchGroupTests() {
        super();
    }

    public SimpleFetchGroupTests(String name) {
        super(name);
    }

    public static junit.framework.Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("SimpleFetchGroupTests");
        
        suite.addTest(new SimpleFetchGroupTests("testSetup"));
        suite.addTest(new SimpleFetchGroupTests("findNoFetchGroup"));
        suite.addTest(new SimpleFetchGroupTests("singleResultNoFetchGroup"));
        suite.addTest(new SimpleFetchGroupTests("resultListNoFetchGroup"));
        suite.addTest(new SimpleFetchGroupTests("singleResultEmptyFetchGroup"));
        suite.addTest(new SimpleFetchGroupTests("resultListEmptyFetchGroup"));
        suite.addTest(new SimpleFetchGroupTests("resultListPeriodFetchGroup"));
        suite.addTest(new SimpleFetchGroupTests("managerFetchGroup"));
        suite.addTest(new SimpleFetchGroupTests("managerFetchGroupWithJoinFetch"));
        suite.addTest(new SimpleFetchGroupTests("employeeNamesFetchGroup"));
        suite.addTest(new SimpleFetchGroupTests("joinFetchEmployeeAddressWithDynamicFetchGroup"));
        suite.addTest(new SimpleFetchGroupTests("joinFetchEmployeeAddressPhoneWithDynamicFetchGroup"));
        suite.addTest(new SimpleFetchGroupTests("verifyFetchedRelationshipAttributes"));
        suite.addTest(new SimpleFetchGroupTests("detachedByClosingEntityManagerObjectWithFetchGroup"));
        if (!isJPA10()) {
            suite.addTest(new SimpleFetchGroupTests("findEmptyFetchGroup"));
            suite.addTest(new SimpleFetchGroupTests("findEmptyFetchGroup_setUnfetchedSalary"));
            suite.addTest(new SimpleFetchGroupTests("verifyUnfetchedAttributes"));
            suite.addTest(new SimpleFetchGroupTests("explicitlyDetachedObjectWithFetchGroup"));
        }
        return suite;
    }
    
    @Test
    public void findNoFetchGroup() throws Exception {
        EntityManager em = createEntityManager("fieldaccess");
        int minId = minimumEmployeeId(em);

        Employee emp = em.find(Employee.class, minId);

        assertNotNull(emp);

        // Check Basics
        assertFetchedAttribute(emp, "id");
        assertFetchedAttribute(emp, "version");
        assertFetchedAttribute(emp, "firstName");
        assertFetchedAttribute(emp, "lastName");
        assertFetchedAttribute(emp, "gender");
        assertFetchedAttribute(emp, "salary");
        assertFetchedAttribute(emp, "startTime");
        assertFetchedAttribute(emp, "endTime");
        if (emp.getPeriod() != null) {
            assertFetchedAttribute(emp.getPeriod(), "startDate");
            assertFetchedAttribute(emp.getPeriod(), "endDate");
        }

        // Check Relationships
        assertNotFetchedAttribute(emp, "address");
        assertNotFetchedAttribute(emp, "manager");
        assertNotFetchedAttribute(emp, "phoneNumbers");
        assertNotFetchedAttribute(emp, "projects");

        assertEquals(2, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
    }

    @Test
    public void singleResultNoFetchGroup() throws Exception {
        EntityManager em = createEntityManager("fieldaccess");

        Query query = em.createQuery("SELECT e FROM Employee e WHERE e.id = :ID");
        query.setParameter("ID", minimumEmployeeId(em));

        Employee emp = (Employee) query.getSingleResult();

        assertNotNull(emp);

        // Check Basics
        assertFetchedAttribute(emp, "id");
        assertFetchedAttribute(emp, "version");
        assertFetchedAttribute(emp, "firstName");
        assertFetchedAttribute(emp, "lastName");
        assertFetchedAttribute(emp, "gender");
        assertFetchedAttribute(emp, "salary");
        assertFetchedAttribute(emp, "startTime");
        assertFetchedAttribute(emp, "endTime");
        if (emp.getPeriod() != null) {
            assertFetchedAttribute(emp.getPeriod(), "startDate");
            assertFetchedAttribute(emp.getPeriod(), "endDate");
        }

        // Check Relationships
        assertNotFetchedAttribute(emp, "address");
        assertNotFetchedAttribute(emp, "manager");
        assertNotFetchedAttribute(emp, "phoneNumbers");
        assertNotFetchedAttribute(emp, "projects");

        assertEquals(2, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
    }

    public void resultListNoFetchGroup() throws Exception {
        EntityManager em = createEntityManager("fieldaccess");

        Query query = em.createQuery("SELECT e FROM Employee e WHERE e.id = :ID");
        query.setParameter("ID", minimumEmployeeId(em));

        List<Employee> emps = query.getResultList();

        assertNotNull(emps);
        assertEquals(1, emps.size());

        Employee emp = emps.get(0);

        // Check Basics
        assertFetchedAttribute(emp, "id");
        assertFetchedAttribute(emp, "version");
        assertFetchedAttribute(emp, "firstName");
        assertFetchedAttribute(emp, "lastName");
        assertFetchedAttribute(emp, "gender");
        assertFetchedAttribute(emp, "salary");
        assertFetchedAttribute(emp, "startTime");
        assertFetchedAttribute(emp, "endTime");
        if (emp.getPeriod() != null) {
            assertFetchedAttribute(emp.getPeriod(), "startDate");
            assertFetchedAttribute(emp.getPeriod(), "endDate");
        }

        // Check Relationships
        assertNotFetchedAttribute(emp, "address");
        assertNotFetchedAttribute(emp, "manager");
        assertNotFetchedAttribute(emp, "phoneNumbers");
        assertNotFetchedAttribute(emp, "projects");
    }

    @Test
    public void findEmptyFetchGroup() throws Exception {
        EntityManager em = createEntityManager("fieldaccess");
        try {
            beginTransaction(em);

            int minId = minimumEmployeeId(em);

            assertEquals(1, getQuerySQLTracker(em).getTotalSQLSELECTCalls());

            Map<String, Object> properties = new HashMap<String, Object>();
            FetchGroup emptyFG = new FetchGroup();
            properties.put(QueryHints.FETCH_GROUP, emptyFG);

            Employee emp = em.find(Employee.class, minId, properties);

            assertNotNull(emp);
            assertFetched(emp, emptyFG);
            assertEquals(2, getQuerySQLTracker(em).getTotalSQLSELECTCalls());

            // Check Basics
            assertFetchedAttribute(emp, "id");
            assertFetchedAttribute(emp, "version");
            assertNotFetchedAttribute(emp, "firstName");
            assertNotFetchedAttribute(emp, "lastName");
            assertNotFetchedAttribute(emp, "gender");
            assertNotFetchedAttribute(emp, "salary");
            assertNotFetchedAttribute(emp, "startTime");
            assertNotFetchedAttribute(emp, "endTime");
            if (emp.getPeriod() != null) {
                assertFetchedAttribute(emp.getPeriod(), "startDate");
                assertFetchedAttribute(emp.getPeriod(), "endDate");
            }

            // Check Relationships
            assertNotFetchedAttribute(emp, "address");
            assertNotFetchedAttribute(emp, "manager");
            assertNotFetchedAttribute(emp, "phoneNumbers");
            assertNotFetchedAttribute(emp, "projects");

            emp.getSalary();

            assertFetchedAttribute(emp, "salary");

            assertEquals(3, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
            assertNoFetchGroup(emp);

            emp.getAddress();

            assertEquals(4, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
            assertNoFetchGroup(emp.getAddress());

            emp.getPhoneNumbers().size();

            assertEquals(5, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
            for (PhoneNumber phone : emp.getPhoneNumbers()) {
                assertNoFetchGroup(phone);
            }

            if (emp.getManager() != null) {
                assertEquals(6, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
            } else {
                // If manager_id field is null then getManager() does not trigger an sql.
                assertEquals(5, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
            } 
        }finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    @Test
    public void findEmptyFetchGroup_setUnfetchedSalary() throws Exception {
        EntityManager em = createEntityManager("fieldaccess");
        try {
            beginTransaction(em);

            int minId = minimumEmployeeId(em);

            assertEquals(1, getQuerySQLTracker(em).getTotalSQLSELECTCalls());

            Map<String, Object> properties = new HashMap<String, Object>();
            FetchGroup emptyFG = new FetchGroup();
            properties.put(QueryHints.FETCH_GROUP, emptyFG);

            Employee emp = em.find(Employee.class, minId, properties);

            assertNotNull(emp);
            assertFetched(emp, emptyFG);
            assertEquals(2, getQuerySQLTracker(em).getTotalSQLSELECTCalls());

            // Check Basics
            assertFetchedAttribute(emp, "id");
            assertFetchedAttribute(emp, "version");
            assertNotFetchedAttribute(emp, "firstName");
            assertNotFetchedAttribute(emp, "lastName");
            assertNotFetchedAttribute(emp, "gender");
            assertNotFetchedAttribute(emp, "salary");
            assertNotFetchedAttribute(emp, "startTime");
            assertNotFetchedAttribute(emp, "endTime");
            if (emp.getPeriod() != null) {
                assertFetchedAttribute(emp.getPeriod(), "startDate");
                assertFetchedAttribute(emp.getPeriod(), "endDate");
            }
            assertEquals(3, getQuerySQLTracker(em).getTotalSQLSELECTCalls());

            // Check Relationships
            assertNotFetchedAttribute(emp, "address");
            assertNotFetchedAttribute(emp, "manager");
            assertNotFetchedAttribute(emp, "phoneNumbers");
            assertNotFetchedAttribute(emp, "projects");

            emp.setSalary(1);

            assertFetchedAttribute(emp, "salary");

            assertEquals(3, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
            assertNoFetchGroup(emp);

            emp.getAddress();

            assertEquals(4, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
            assertNoFetchGroup(emp.getAddress());

            emp.getPhoneNumbers().size();

            assertEquals(5, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
            for (PhoneNumber phone : emp.getPhoneNumbers()) {
                assertNoFetchGroup(phone);
            }
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    @Test
    public void singleResultEmptyFetchGroup() throws Exception {
        EntityManager em = createEntityManager("fieldaccess");
        try {
            beginTransaction(em);

            Query query = em.createQuery("SELECT e FROM Employee e WHERE e.id = :ID");
            query.setParameter("ID", minimumEmployeeId(em));
            FetchGroup emptyFG = new FetchGroup();
            query.setHint(QueryHints.FETCH_GROUP, emptyFG);

            Employee emp = (Employee) query.getSingleResult();

            assertNotNull(emp);
            assertFetched(emp, emptyFG);
            assertEquals(2, getQuerySQLTracker(em).getTotalSQLSELECTCalls());

            // Check Basics
            assertFetchedAttribute(emp, "id");
            assertFetchedAttribute(emp, "version");
            assertNotFetchedAttribute(emp, "firstName");
            assertNotFetchedAttribute(emp, "lastName");
            assertNotFetchedAttribute(emp, "gender");
            assertNotFetchedAttribute(emp, "salary");
            assertNotFetchedAttribute(emp, "startTime");
            assertNotFetchedAttribute(emp, "endTime");
            if (emp.getPeriod() != null) {
                assertFetchedAttribute(emp.getPeriod(), "startDate");
                assertFetchedAttribute(emp.getPeriod(), "endDate");
            }

            // Check Relationships
            assertNotFetchedAttribute(emp, "address");
            assertNotFetchedAttribute(emp, "manager");
            assertNotFetchedAttribute(emp, "phoneNumbers");
            assertNotFetchedAttribute(emp, "projects");

            emp.getSalary();

            assertFetchedAttribute(emp, "salary");

            assertEquals(3, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
            assertNoFetchGroup(emp);

            emp.getAddress();

            assertEquals(4, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
            assertNoFetchGroup(emp.getAddress());

            emp.getPhoneNumbers().size();

            assertEquals(5, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
            for (PhoneNumber phone : emp.getPhoneNumbers()) {
                assertNoFetchGroup(phone);
            }
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    /**
     * 
     */
    @Test
    public void resultListEmptyFetchGroup() throws Exception {
        EntityManager em = createEntityManager("fieldaccess");
        try {
            beginTransaction(em);

            Query query = em.createQuery("SELECT e FROM Employee e WHERE e.id = :ID");
            query.setParameter("ID", minimumEmployeeId(em));
            FetchGroup emptyFG = new FetchGroup();
            query.setHint(QueryHints.FETCH_GROUP, emptyFG);

            List<Employee> emps = query.getResultList();

            assertNotNull(emps);
            assertEquals(1, emps.size());

            Employee emp = emps.get(0);

            assertEquals(2, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
            assertFetched(emp, emptyFG);

            // Check Basics
            assertFetchedAttribute(emp, "id");
            assertFetchedAttribute(emp, "version");
            assertNotFetchedAttribute(emp, "firstName");
            assertNotFetchedAttribute(emp, "lastName");
            assertNotFetchedAttribute(emp, "gender");
            assertNotFetchedAttribute(emp, "salary");
            assertNotFetchedAttribute(emp, "startTime");
            assertNotFetchedAttribute(emp, "endTime");
            assertEquals(2, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
            if (emp.getPeriod() != null) {
                assertFetchedAttribute(emp.getPeriod(), "startDate");
                assertFetchedAttribute(emp.getPeriod(), "endDate");
            }

            // Check Relationships
            assertNotFetchedAttribute(emp, "address");
            assertNotFetchedAttribute(emp, "manager");
            assertNotFetchedAttribute(emp, "phoneNumbers");
            assertNotFetchedAttribute(emp, "projects");

            assertEquals(3, getQuerySQLTracker(em).getTotalSQLSELECTCalls());

            emp.getSalary();

            assertFetchedAttribute(emp, "salary");
            assertNoFetchGroup(emp);
            assertEquals(3, getQuerySQLTracker(em).getTotalSQLSELECTCalls());

            assertNoFetchGroup(emp.getAddress());

            assertEquals(4, getQuerySQLTracker(em).getTotalSQLSELECTCalls());

            for (PhoneNumber phone : emp.getPhoneNumbers()) {
                assertNoFetchGroup(phone);
            }
            assertEquals(5, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    /**
     * 
     */
    @Test
    public void resultListPeriodFetchGroup() throws Exception {
        EntityManager em = createEntityManager("fieldaccess");
        try {
            beginTransaction(em);

            Query query = em.createQuery("SELECT e FROM Employee e WHERE e.id = :ID");
            query.setParameter("ID", minimumEmployeeId(em));
            FetchGroup fg = new FetchGroup();
            fg.addAttribute("period");
            query.setHint(QueryHints.FETCH_GROUP, fg);

            List<Employee> emps = query.getResultList();

            assertNotNull(emps);
            assertEquals(1, emps.size());

            Employee emp = emps.get(0);

            assertEquals(2, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
            assertFetched(emp, fg);

            // Check Basics
            assertFetchedAttribute(emp, "id");
            assertFetchedAttribute(emp, "version");
            assertNotFetchedAttribute(emp, "firstName");
            assertNotFetchedAttribute(emp, "lastName");
            assertNotFetchedAttribute(emp, "gender");
            assertNotFetchedAttribute(emp, "salary");
            assertNotFetchedAttribute(emp, "startTime");
            assertNotFetchedAttribute(emp, "endTime");
            if (emp.getPeriod() != null) {
                assertFetchedAttribute(emp.getPeriod(), "startDate");
                assertFetchedAttribute(emp.getPeriod(), "endDate");
            }

            // Check Relationships
            assertNotFetchedAttribute(emp, "address");
            assertNotFetchedAttribute(emp, "manager");
            assertNotFetchedAttribute(emp, "phoneNumbers");
            assertNotFetchedAttribute(emp, "projects");

            assertEquals(2, getQuerySQLTracker(em).getTotalSQLSELECTCalls());

            emp.getSalary();

            assertFetchedAttribute(emp, "salary");
            assertNoFetchGroup(emp);

            assertEquals(3, getQuerySQLTracker(em).getTotalSQLSELECTCalls());

            assertNoFetchGroup(emp.getAddress());

            assertEquals(4, getQuerySQLTracker(em).getTotalSQLSELECTCalls());

            for (PhoneNumber phone : emp.getPhoneNumbers()) {
                assertNoFetchGroup(phone);
            }
            assertEquals(5, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    @Test
    public void managerFetchGroup() throws Exception {
        EntityManager em = createEntityManager("fieldaccess");
        try {
            beginTransaction(em);

            // Use q query since find will only use default fetch group
            //Query query = em.createQuery("SELECT e FROM Employee e WHERE e.id = :ID");
            //query.setParameter("ID", minimumEmployeeId(em));
            
            // Complex where clause used to avoid triggering employees and their departments:
            //   Don't include employees who are managers themselves - otherwise if first selected as employee, then as e.manager the full read will be triggered;
            //   Don't include managers with departments - because there is no fetch group on e.manager its (non-null) department will trigger an extra sql
            Query query = em.createQuery("SELECT e FROM Employee e WHERE e.manager IS NOT NULL AND NOT EXISTS(SELECT e2 FROM Employee e2 WHERE e2.manager = e) AND e.manager.department IS NULL");

            FetchGroup managerFG = new FetchGroup();
            managerFG.addAttribute("manager");

            query.setHint(QueryHints.FETCH_GROUP, managerFG);

            assertNotNull(getFetchGroup(query));
            assertSame(managerFG, getFetchGroup(query));

            Employee emp = (Employee) query.getSingleResult();

            assertFetched(emp, managerFG);
            //int nSqlBeforeGetManager = getQuerySQLTracker(em).getTotalSQLSELECTCalls();
            // manager hasn't been instantiated yet
            assertEquals(1, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
            
            int nSqlToAdd = 0;
            if (emp.getManager() != null) {
                assertFetchedAttribute(emp, "manager");
                // additional sql to select the manager
                nSqlToAdd++;
            }
            assertEquals(1 + nSqlToAdd, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
            
            // acuses instantioation of the whole object
            emp.getLastName();

            assertEquals(2 + nSqlToAdd, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
            assertNoFetchGroup(emp);

            for (PhoneNumber phone : emp.getPhoneNumbers()) {
                assertNoFetchGroup(phone);
                phone.getAreaCode();
            }

            assertEquals(3 + nSqlToAdd, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    @Test
    public void managerFetchGroupWithJoinFetch() throws Exception {
        EntityManager em = createEntityManager("fieldaccess");
        try {
            beginTransaction(em);

            //int minId = minimumEmployeeId(em);
            //assertEquals(1, getQuerySQLTracker(em).getTotalSQLSELECTCalls());

            // Use q query since find will only use default fetch group
            //Query query = em.createQuery("SELECT e FROM Employee e WHERE e.id = :ID");
            //query.setParameter("ID", minId);

            // Complex where clause used to avoid triggering employees and their departments:
            //   Don't include employees who are managers themselves - otherwise if first selected as employee, then as e.manager the full read will be triggered;
            //   Don't include managers with departments - because there is no fetch group on e.manager its (non-null) department will trigger an extra sql
            Query query = em.createQuery("SELECT e FROM Employee e WHERE e.manager IS NOT NULL AND NOT EXISTS(SELECT e2 FROM Employee e2 WHERE e2.manager = e) AND e.manager.department IS NULL");
            FetchGroup managerFG = new FetchGroup();
            managerFG.addAttribute("manager");

            query.setHint(QueryHints.FETCH_GROUP, managerFG);
            query.setHint(QueryHints.LEFT_FETCH, "e.manager");

            assertNotNull(getFetchGroup(query));
            assertSame(managerFG, getFetchGroup(query));

            Employee emp = (Employee) query.getSingleResult();

            assertFetched(emp, managerFG);
            assertEquals(1, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
            
            // manager (if not null) is instantiated by the fetch group, before emp.getManager call.
            emp.getManager();
            assertEquals(1, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
            
            // instantiates the whole object
            emp.getLastName();

            assertEquals(2, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
            assertNoFetchGroup(emp);

            for (PhoneNumber phone : emp.getPhoneNumbers()) {
                assertNoFetchGroup(phone);
                phone.getAreaCode();
            }

            assertEquals(3, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    @Test
    public void employeeNamesFetchGroup() throws Exception {
        EntityManager em = createEntityManager("fieldaccess");
        try {
            beginTransaction(em);

            int minId = minimumEmployeeId(em);
            assertEquals(1, getQuerySQLTracker(em).getTotalSQLSELECTCalls());

            // Use q query since find will only use default fetch group
            Query query = em.createQuery("SELECT e FROM Employee e WHERE e.id = :ID");
            query.setParameter("ID", minId);
            FetchGroup namesFG = new FetchGroup();
            namesFG.addAttribute("firstName");
            namesFG.addAttribute("lastName");

            query.setHint(QueryHints.FETCH_GROUP, namesFG);

            assertNotNull(getFetchGroup(query));
            assertSame(namesFG, getFetchGroup(query));

            Employee emp = (Employee) query.getSingleResult();

            assertEquals(2, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
            assertFetched(emp, namesFG);

            emp.getId();
            emp.getFirstName();
            emp.getLastName();
            emp.getVersion();

            assertEquals(2, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
            assertFetched(emp, namesFG);

            emp.getGender();
            emp.getSalary();

            assertEquals(3, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
            assertNoFetchGroup(emp);

            for (PhoneNumber phone : emp.getPhoneNumbers()) {
                assertNoFetchGroup(phone);
                phone.getAreaCode();
            }
            assertEquals(4, getQuerySQLTracker(em).getTotalSQLSELECTCalls());

            if (emp.getManager() != null) {
                assertEquals(5, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
                assertNoFetchGroup(emp.getManager());
            } else {
                // If manager_id field is null then getManager() does not trigger an sql.
                assertEquals(4, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
            }
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    @Test
    public void joinFetchEmployeeAddressWithDynamicFetchGroup() {
        EntityManager em = createEntityManager("fieldaccess");

        Query query = em.createQuery("SELECT e FROM Employee e JOIN FETCH e.address");

        FetchGroup fetchGroup = new FetchGroup("names");
        fetchGroup.addAttribute("firstName");
        fetchGroup.addAttribute("lastName");
        query.setHint(QueryHints.FETCH_GROUP, fetchGroup);

        List<Employee> emps = query.getResultList();

        assertNotNull(emps);
    }

    @Test
    public void joinFetchEmployeeAddressPhoneWithDynamicFetchGroup() {
        EntityManager em = createEntityManager("fieldaccess");

        Query query = em.createQuery("SELECT e FROM Employee e JOIN FETCH e.address WHERE e.id IN (SELECT p.owner.id FROM PhoneNumber p)");

        FetchGroup fetchGroup = new FetchGroup("names");
        fetchGroup.addAttribute("firstName");
        fetchGroup.addAttribute("lastName");
        query.setHint(QueryHints.FETCH_GROUP, fetchGroup);

        List<Employee> emps = query.getResultList();

        assertNotNull(emps);
    }

    @Test
    public void verifyUnfetchedAttributes() throws Exception {
        EntityManager em = createEntityManager("fieldaccess");
        try {
            beginTransaction(em);
            TypedQuery<Employee> q = em.createQuery("SELECT e FROM Employee e WHERE e.id IN (SELECT MIN(p.owner.id) FROM PhoneNumber p)", Employee.class);
            FetchGroup fg = new FetchGroup("Employee.empty");
            q.setHint(QueryHints.FETCH_GROUP, fg);
            Employee emp = q.getSingleResult();

            assertNotNull(emp);
            assertEquals(1, getQuerySQLTracker(em).getTotalSQLSELECTCalls());

            // This check using the mapping returns a default (empty) IndirectList
            /*OneToManyMapping phoneMapping = (OneToManyMapping) employeeDescriptor.getMappingForAttributeName("phoneNumbers");
            IndirectList phones = (IndirectList) phoneMapping.getAttributeValueFromObject(emp);
            assertNotNull(phones);
            assertTrue(phones.isInstantiated());
            assertEquals(0, phones.size());
            assertEquals(1, getQuerySQLTracker(em).getTotalSQLSELECTCalls());*/

            IndirectList phonesIL = (IndirectList) emp.getPhoneNumbers();
            assertFalse(phonesIL.isInstantiated());
            assertEquals(2, getQuerySQLTracker(em).getTotalSQLSELECTCalls());

            assertTrue(emp.getPhoneNumbers().size() > 0);
            assertEquals(3, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    @Test
    public void verifyFetchedRelationshipAttributes() throws Exception {
        EntityManager em = createEntityManager("fieldaccess");

        FetchGroup fg = new FetchGroup("Employee.relationships");
        fg.addAttribute("address");
        fg.addAttribute("phoneNumbers");
        fg.addAttribute("manager");
        fg.addAttribute("projects");

        Map<String, Object> hints = new HashMap<String, Object>();
        hints.put(QueryHints.FETCH_GROUP, fg);

        Employee emp = minimumEmployee(em, hints);

        assertNotNull(emp);

    }

    @Test
    public void detachedByClosingEntityManagerObjectWithFetchGroup() {
        EntityManager em = createEntityManager("fieldaccess");

        FetchGroup fg = new FetchGroup();
        fg.addAttribute("firstName");
        fg.addAttribute("lastName");

        Map<String, Object> hints = new HashMap<String, Object>();
        hints.put(QueryHints.FETCH_GROUP, fg);

        Employee emp = minimumEmployee(em, hints);
        assertFetched(emp, fg);
        closeEntityManager(em);
        
        // trigger the fetch group
        emp.getSalary();
        assertNoFetchGroup(emp);
    }

    @Test
    public void explicitlyDetachedObjectWithFetchGroup() {
        EntityManager em = createEntityManager("fieldaccess");
        try {
            beginTransaction(em);
            FetchGroup fg = new FetchGroup();
            fg.addAttribute("firstName");
            fg.addAttribute("lastName");

            Map<String, Object> hints = new HashMap<String, Object>();
            hints.put(QueryHints.FETCH_GROUP, fg);

            Employee emp = minimumEmployee(em, hints);
            em.detach(emp);
            assertFetched(emp, fg);
            
            // trigger the fetch group
            emp.getSalary();
            assertNoFetchGroup(emp);
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }
}
