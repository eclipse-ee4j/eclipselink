/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
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

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import junit.framework.TestSuite;

import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.queries.FetchGroup;
import org.eclipse.persistence.queries.FetchGroupTracker;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.PhoneNumber;

import org.junit.Test;

/**
 * Simple tests to verify the functionality of single level FetchGroup usage
 * 
 * @author dclarke
 * @since EclipseLink 2.1
 */
public class SimpleNamedFetchGroupTests extends BaseFetchGroupTests {

    public SimpleNamedFetchGroupTests() {
        super();
    }

    public SimpleNamedFetchGroupTests(String name) {
        super(name);
    }

    public static junit.framework.Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("SimpleNamedFetchGroupTests");
        
        suite.addTest(new SimpleNamedFetchGroupTests("testSetup"));
        suite.addTest(new SimpleNamedFetchGroupTests("findDefaultFetchGroup"));
        suite.addTest(new SimpleNamedFetchGroupTests("singleResultDefaultFetchGroup"));
        suite.addTest(new SimpleNamedFetchGroupTests("resultListDefaultFetchGroup"));
        suite.addTest(new SimpleNamedFetchGroupTests("singleResultNoFetchGroup"));
        suite.addTest(new SimpleNamedFetchGroupTests("resultListNoFetchGroup"));
        suite.addTest(new SimpleNamedFetchGroupTests("managerFetchGroup"));
        suite.addTest(new SimpleNamedFetchGroupTests("namedEmptyFetchGroupUsingGetSingleResult"));
        suite.addTest(new SimpleNamedFetchGroupTests("namedNamesFetchGroupUsingGetSingleResult"));
        suite.addTest(new SimpleNamedFetchGroupTests("joinFetchEmployeeAddressWithDynamicFetchGroup"));
        suite.addTest(new SimpleNamedFetchGroupTests("joinFetchEmployeeAddressPhoneWithDynamicFetchGroup"));
        
        return suite;
    }

    public void setUp() {
        super.setUp();

        FetchGroup namedEmpFG = new FetchGroup("Employee.test");
        namedEmpFG.addAttribute("firstName");
        namedEmpFG.addAttribute("lastName");
        employeeDescriptor.getFetchGroupManager().addFetchGroup(namedEmpFG);

        FetchGroup namedPhoneFG = new FetchGroup("Phone.test");
        namedPhoneFG.addAttribute("number");
        phoneDescriptor.getFetchGroupManager().addFetchGroup(namedPhoneFG);

        FetchGroup namedAddressFG = new FetchGroup("Address.test");
        namedAddressFG.addAttribute("city");
        addressDescriptor.getFetchGroupManager().addFetchGroup(namedAddressFG);

        assertConfig(employeeDescriptor, null, 1);
        assertConfig(phoneDescriptor, null, 1);
        assertConfig(addressDescriptor, null, 1);
    }
    
    @Test
    public void findDefaultFetchGroup() throws Exception {
        EntityManager em = createEntityManager("fieldaccess");

        Employee emp = minimumEmployee(em);

        assertNotNull(emp);
        assertEquals(1, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
        assertNoFetchGroup(emp);

        emp.getSalary();

        assertEquals(1, getQuerySQLTracker(em).getTotalSQLSELECTCalls());

        assertNoFetchGroup(emp.getAddress());
        assertEquals(2, getQuerySQLTracker(em).getTotalSQLSELECTCalls());

        for (PhoneNumber phone : emp.getPhoneNumbers()) {
            assertNoFetchGroup(phone);
        }

        assertEquals(3, getQuerySQLTracker(em).getTotalSQLSELECTCalls());

        if (emp.getManager() != null) {
            assertNoFetchGroup(emp.getManager());
            assertEquals(4, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
        } else {
            // If manager_id field is null then getManager() does not trigger an sql.
            assertEquals(3, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
        }
    }

    @Test
    public void singleResultDefaultFetchGroup() throws Exception {
        EntityManager em = createEntityManager("fieldaccess");

        Query query = em.createQuery("SELECT e FROM Employee e WHERE e.id = :ID");
        query.setParameter("ID", minimumEmployeeId(em));

        Employee emp = (Employee) query.getSingleResult();

        assertNotNull(emp);
        assertEquals(2, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
        assertNoFetchGroup(emp);

        emp.getSalary();

        assertEquals(2, getQuerySQLTracker(em).getTotalSQLSELECTCalls());

        assertNoFetchGroup(emp.getAddress());
        assertEquals(3, getQuerySQLTracker(em).getTotalSQLSELECTCalls());

        for (PhoneNumber phone : emp.getPhoneNumbers()) {
            assertNoFetchGroup(phone);
        }

        assertEquals(4, getQuerySQLTracker(em).getTotalSQLSELECTCalls());

        if (emp.getManager() != null) {
            assertNoFetchGroup(emp.getManager());
            assertEquals(5, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
        } else {
            // If manager_id field is null then getManager() does not trigger an sql.
            assertEquals(4, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
        }
    }

    @Test
    public void resultListDefaultFetchGroup() throws Exception {
        EntityManager em = createEntityManager("fieldaccess");

        Query query = em.createQuery("SELECT e FROM Employee e WHERE e.id = :ID");
        query.setParameter("ID", minimumEmployeeId(em));

        List<Employee> emps = query.getResultList();

        assertNotNull(emps);
        assertEquals(1, emps.size());

        Employee emp = emps.get(0);

        assertNotNull(emp);
        assertEquals(2, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
        assertNoFetchGroup(emp);

        emp.getSalary();

        assertEquals(2, getQuerySQLTracker(em).getTotalSQLSELECTCalls());

        assertNoFetchGroup(emp.getAddress());
        assertEquals(3, getQuerySQLTracker(em).getTotalSQLSELECTCalls());

        for (PhoneNumber phone : emp.getPhoneNumbers()) {
            assertNoFetchGroup(phone);
        }

        assertEquals(4, getQuerySQLTracker(em).getTotalSQLSELECTCalls());

        if (emp.getManager() != null) {
            assertNoFetchGroup(emp.getManager());
            assertEquals(5, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
        } else {
            // If manager_id field is null then getManager() does not trigger an sql.
            assertEquals(4, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
        }
    }

    @Test
    public void singleResultNoFetchGroup() throws Exception {
        EntityManager em = createEntityManager("fieldaccess");

        Query query = em.createQuery("SELECT e FROM Employee e WHERE e.id = :ID");
        query.setParameter("ID", minimumEmployeeId(em));

        Employee emp = (Employee) query.getSingleResult();

        assertNotNull(emp);
        assertEquals(2, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
        assertNoFetchGroup(emp);

        emp.getSalary();

        assertEquals(2, getQuerySQLTracker(em).getTotalSQLSELECTCalls());

        assertNoFetchGroup(emp.getAddress());
        assertEquals(3, getQuerySQLTracker(em).getTotalSQLSELECTCalls());

        for (PhoneNumber phone : emp.getPhoneNumbers()) {
            assertNoFetchGroup(phone);
        }

        assertEquals(4, getQuerySQLTracker(em).getTotalSQLSELECTCalls());

        if (emp.getManager() != null) {
            assertNoFetchGroup(emp.getManager());
            assertEquals(5, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
        } else {
            // If manager_id field is null then getManager() does not trigger an sql.
            assertEquals(4, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
        }
    }

    @Test
    public void resultListNoFetchGroup() throws Exception {
        EntityManager em = createEntityManager("fieldaccess");

        Query query = em.createQuery("SELECT e FROM Employee e WHERE e.id = :ID");
        query.setParameter("ID", minimumEmployeeId(em));

        List<Employee> emps = query.getResultList();

        assertNotNull(emps);
        assertEquals(1, emps.size());

        Employee emp = emps.get(0);

        assertNotNull(emp);
        assertEquals(2, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
        assertNoFetchGroup(emp);

        emp.getSalary();

        assertEquals(2, getQuerySQLTracker(em).getTotalSQLSELECTCalls());

        assertNoFetchGroup(emp.getAddress());
        assertEquals(3, getQuerySQLTracker(em).getTotalSQLSELECTCalls());

        for (PhoneNumber phone : emp.getPhoneNumbers()) {
            assertNoFetchGroup(phone);
        }

        assertEquals(4, getQuerySQLTracker(em).getTotalSQLSELECTCalls());

        if (emp.getManager() != null) {
            assertNoFetchGroup(emp.getManager());
            assertEquals(5, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
        } else {
            // If manager_id field is null then getManager() does not trigger an sql.
            assertEquals(4, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
        }
    }

    @Test
    public void managerFetchGroup() throws Exception {
        EntityManager em = createEntityManager("fieldaccess");
        try {
            beginTransaction(em);
            // Use q query since find will only use default fetch group
            // Query query = em.createQuery("SELECT e FROM Employee e WHERE e.id = :ID");
            // query.setParameter("ID", minimumEmployeeId(em));

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
            assertEquals(1, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
            
            int nSqlToAdd = 0;
            if (emp.getManager() != null) {
                assertFetchedAttribute(emp, "manager");
                // additional sql to select the manager
                nSqlToAdd++;
            }
            
            assertEquals(1 + nSqlToAdd, getQuerySQLTracker(em).getTotalSQLSELECTCalls());

            // instantiates the whole object
            emp.getLastName();

            assertEquals(2 + nSqlToAdd, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
            assertNoFetchGroup(emp);

            for (PhoneNumber phone : emp.getPhoneNumbers()) {
                assertNoFetchGroup(phone);
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
    public void namedEmptyFetchGroupUsingGetSingleResult() throws Exception {
        ClassDescriptor descriptor = getDescriptor("Employee");

        FetchGroup fetchGroup = new FetchGroup("test");
        descriptor.getFetchGroupManager().addFetchGroup(fetchGroup);
        assertTrue(fetchGroup.getItems().isEmpty());

        assertEquals(2, descriptor.getFetchGroupManager().getFetchGroups().size());

        EntityManager em = createEntityManager("fieldaccess");

        Query query = em.createQuery("SELECT e FROM Employee e WHERE e.id = :ID");
        query.setParameter("ID", minimumEmployeeId(em));
        query.setHint(QueryHints.FETCH_GROUP_NAME, "test");

        Employee emp = (Employee) query.getSingleResult();
        assertNotNull(emp);

        FetchGroupTracker tracker = (FetchGroupTracker) emp;
        assertNotNull(tracker);

        FetchGroup usedFG = tracker._persistence_getFetchGroup();

        assertNotNull("No FetchGroup found on read Employee", usedFG);
        // No longer can make any assumptions about a name of FetchGroup on the entity
        // What's guaranteed is that attribute sets are the same
        assertTrue(fetchGroup.getAttributeNames().equals(usedFG.getAttributeNames()));
//        assertEquals(fetchGroup.getName(), usedFG.getName());
//        assertSame(fetchGroup, ((EntityFetchGroup) usedFG).getParent());
        assertEquals(2, fetchGroup.getItems().size());
        assertTrue(tracker._persistence_isAttributeFetched("id"));
        assertTrue(tracker._persistence_isAttributeFetched("version"));
        assertFalse(tracker._persistence_isAttributeFetched("salary"));
        assertFalse(tracker._persistence_isAttributeFetched("firstName"));
        assertFalse(tracker._persistence_isAttributeFetched("lastName"));
    }

    @Test
    public void namedNamesFetchGroupUsingGetSingleResult() throws Exception {
        ClassDescriptor descriptor = getDescriptor("Employee");

        FetchGroup fetchGroup = new FetchGroup("names");
        fetchGroup.addAttribute("firstName");
        fetchGroup.addAttribute("lastName");

        descriptor.getFetchGroupManager().addFetchGroup(fetchGroup);
        assertEquals(2, fetchGroup.getItems().size());

        assertEquals(2, descriptor.getFetchGroupManager().getFetchGroups().size());

        EntityManager em = createEntityManager("fieldaccess");

        Query query = em.createQuery("SELECT e FROM Employee e WHERE e.id = :ID");
        query.setParameter("ID", minimumEmployeeId(em));
        query.setHint(QueryHints.FETCH_GROUP_NAME, "names");

        Employee emp = (Employee) query.getSingleResult();
        assertNotNull(emp);

        FetchGroupTracker tracker = (FetchGroupTracker) emp;
        assertNotNull(tracker);

        FetchGroup usedFG = tracker._persistence_getFetchGroup();

        assertNotNull("No FetcGroup found on read Employee", fetchGroup);
//        assertSame(fetchGroup, ((EntityFetchGroup) usedFG).getParent());
        assertEquals(4, fetchGroup.getItems().size());
        assertTrue(tracker._persistence_isAttributeFetched("id"));
        assertTrue(tracker._persistence_isAttributeFetched("version"));
        assertFalse(tracker._persistence_isAttributeFetched("salary"));
        assertTrue(tracker._persistence_isAttributeFetched("firstName"));
        assertTrue(tracker._persistence_isAttributeFetched("lastName"));
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
}
