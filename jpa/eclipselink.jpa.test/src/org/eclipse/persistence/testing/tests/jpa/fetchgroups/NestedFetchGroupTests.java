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
 *     05/14/2010-2.1 ailitchev - Bug 244124 - Add Nested FetchGroup 
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.jpa.fetchgroups;

import java.util.HashSet;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import junit.framework.TestSuite;

import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.FetchGroup;
import org.eclipse.persistence.queries.FetchGroupTracker;
import org.eclipse.persistence.queries.LoadGroup;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.advanced.PhoneNumber;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee.Gender;

import org.junit.Test;

/**
 * @author dclarke
 * @since EclipseLink 2.1
 */
public class NestedFetchGroupTests extends BaseFetchGroupTests {

    public NestedFetchGroupTests() {
        super();
    }

    public NestedFetchGroupTests(String name) {
        super(name);
    }

    public static junit.framework.Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("NestedFetchGroupTests");
        
        suite.addTest(new NestedFetchGroupTests("testSetup"));
        suite.addTest(new NestedFetchGroupTests("dynamicFetchGroup_EmployeeAddress"));
        suite.addTest(new NestedFetchGroupTests("dynamicFetchGroup_Employee_NullAddress"));
        suite.addTest(new NestedFetchGroupTests("dynamicFetchGroup_EmployeeAddressNullPhone"));
        suite.addTest(new NestedFetchGroupTests("dynamicFetchGroup_EmployeeAddressEmptyPhone"));
        suite.addTest(new NestedFetchGroupTests("dynamicFetchGroup_EmployeeAddressEmptyPhoneLoad"));
        suite.addTest(new NestedFetchGroupTests("dynamicHierarchicalFetchGroup"));
        suite.addTest(new NestedFetchGroupTests("dynamicHierarchicalFetchGroup_JOIN_FETCH"));
        suite.addTest(new NestedFetchGroupTests("loadPlan"));
        
        return suite;
    }
    
    public void setUp() {
        super.setUp();
        
        FetchGroup phoneFG = new FetchGroup();
        phoneFG.addAttribute("number");
        phoneDescriptor.getFetchGroupManager().setDefaultFetchGroup(phoneFG);
        phoneDescriptor.getDescriptorQueryManager().getReadObjectQuery().setFetchGroup(phoneFG);
        
        reprepareReadQueries(phoneDescriptor);
        reprepareReadQueries(employeeDescriptor);

        // We'll put a default FetchGroup on Phone
        assertNotNull(phoneDescriptor.getDefaultFetchGroup());
        assertNotNull(phoneDescriptor.getDescriptorQueryManager().getReadObjectQuery().getFetchGroup());
        assertTrue(phoneDescriptor.getFetchGroupManager().getFetchGroups().isEmpty());
    }

    @Test
    public void dynamicFetchGroup_EmployeeAddress() throws Exception {
        EntityManager em = createEntityManager();

        Query query = em.createQuery("SELECT e FROM Employee e WHERE e.gender = :GENDER");
        query.setParameter("GENDER", Gender.Male);

        // Define the fields to be fetched on Employee
        FetchGroup fg = new FetchGroup();
        fg.addAttribute("id");
        fg.addAttribute("version");
        fg.addAttribute("firstName");
        fg.addAttribute("lastName");
        fg.addAttribute("address.city");
        fg.addAttribute("address.postalCode");

        // Configure the dynamic FetchGroup
        query.setHint(QueryHints.FETCH_GROUP, fg);

        List<Employee> emps = query.getResultList();

        assertNotNull(emps);
        for (Employee emp : emps) {
            FetchGroupTracker tracker = (FetchGroupTracker) emp;

            assertNotNull(tracker._persistence_getFetchGroup());

            // Verify specified fields plus mandatory ones are loaded
            assertTrue(tracker._persistence_isAttributeFetched("firstName"));
            assertTrue(tracker._persistence_isAttributeFetched("lastName"));
            assertTrue(tracker._persistence_isAttributeFetched("address"));
            FetchGroupTracker addrTracker = (FetchGroupTracker) emp.getAddress();
            assertTrue(addrTracker._persistence_isAttributeFetched("city"));
            assertTrue(addrTracker._persistence_isAttributeFetched("postalCode"));
            assertFalse(addrTracker._persistence_isAttributeFetched("street"));

            // Verify the other fields are not loaded
            assertFalse(tracker._persistence_isAttributeFetched("salary"));
            assertFalse(tracker._persistence_isAttributeFetched("startTime"));
            assertFalse(tracker._persistence_isAttributeFetched("endTime"));

            // Force the loading of lazy fields and verify
            emp.getSalary();

            assertTrue(tracker._persistence_isAttributeFetched("firstName"));
            assertTrue(tracker._persistence_isAttributeFetched("lastName"));
            assertTrue(tracker._persistence_isAttributeFetched("address"));
            assertTrue(tracker._persistence_isAttributeFetched("salary"));
            assertTrue(tracker._persistence_isAttributeFetched("startTime"));
            assertTrue(tracker._persistence_isAttributeFetched("endTime"));

            // Now we'll check the address uses the provided dynamic fetch-group
            addrTracker = (FetchGroupTracker) emp.getAddress();
            assertNotNull("Address does not have a FetchGroup", addrTracker._persistence_getFetchGroup());
            assertTrue(addrTracker._persistence_isAttributeFetched("city"));
            assertTrue(addrTracker._persistence_isAttributeFetched("postalCode"));
            assertFalse(addrTracker._persistence_isAttributeFetched("street"));
            assertFalse(addrTracker._persistence_isAttributeFetched("country"));

            // Now we'll check the phoneNumbers use of the default fetch group
            for (PhoneNumber phone : emp.getPhoneNumbers()) {
                FetchGroupTracker phoneTracker = (FetchGroupTracker) phone;
                assertNotNull("PhoneNumber does not have a FetchGroup", phoneTracker._persistence_getFetchGroup());
                assertTrue(phoneTracker._persistence_isAttributeFetched("number"));
                assertFalse(phoneTracker._persistence_isAttributeFetched("areaCode"));
            }
        }
    }

    @Test
    public void dynamicFetchGroup_Employee_NullAddress() throws Exception {
        EntityManager em = createEntityManager();

        Query query = em.createQuery("SELECT e FROM Employee e WHERE e.gender = :GENDER");
        query.setParameter("GENDER", Gender.Male);

        // Define the fields to be fetched on Employee
        FetchGroup empGroup = new FetchGroup();
        empGroup.addAttribute("firstName");
        empGroup.addAttribute("lastName");
        empGroup.addAttribute("address");

        // Define the fields to be fetched on Address
        FetchGroup addressGroup = new FetchGroup();
        addressGroup.addAttribute("city");
        addressGroup.addAttribute("postalCode");

        empGroup.addAttribute("address");

        // Configure the dynamic FetchGroup
        query.setHint(QueryHints.FETCH_GROUP, empGroup);

        List<Employee> emps = query.getResultList();

        assertNotNull(emps);
        for (Employee emp : emps) {
            FetchGroupTracker tracker = (FetchGroupTracker) emp;

            assertNotNull(tracker._persistence_getFetchGroup());

            // Verify specified fields plus mandatory ones are loaded
            assertTrue(tracker._persistence_isAttributeFetched("id"));
            assertTrue(tracker._persistence_isAttributeFetched("firstName"));
            assertTrue(tracker._persistence_isAttributeFetched("lastName"));
            assertTrue(tracker._persistence_isAttributeFetched("version"));

            // Verify the other fields are not loaded
            assertFalse(tracker._persistence_isAttributeFetched("salary"));
            assertFalse(tracker._persistence_isAttributeFetched("startTime"));
            assertFalse(tracker._persistence_isAttributeFetched("endTime"));

            // Force the loading of lazy fields and verify
            emp.getSalary();

            assertTrue(tracker._persistence_isAttributeFetched("salary"));
            assertTrue(tracker._persistence_isAttributeFetched("startTime"));
            assertTrue(tracker._persistence_isAttributeFetched("endTime"));

            // Now we'll check the address uses the provided dynamic fetch-group
            FetchGroupTracker addrTracker = (FetchGroupTracker) emp.getAddress();
            assertNull("Address has an unexpected FetchGroup", addrTracker._persistence_getFetchGroup());

            // Now we'll check the phoneNumbers use of the default fetch group
            for (PhoneNumber phone : emp.getPhoneNumbers()) {
                FetchGroupTracker phoneTracker = (FetchGroupTracker) phone;
                assertNotNull("PhoneNumber does not have a FetchGroup", phoneTracker._persistence_getFetchGroup());
                assertTrue(phoneTracker._persistence_isAttributeFetched("number"));
                assertFalse(phoneTracker._persistence_isAttributeFetched("areaCode"));
            }
        }
    }

    @Test
    public void dynamicFetchGroup_EmployeeAddressNullPhone() throws Exception {
        EntityManager em = createEntityManager();

        Query query = em.createQuery("SELECT e FROM Employee e WHERE e.gender = :GENDER");
        query.setParameter("GENDER", Gender.Male);

        // Define the fields to be fetched on Employee
        FetchGroup empGroup = new FetchGroup();
        empGroup.addAttribute("firstName");
        empGroup.addAttribute("lastName");
        empGroup.addAttribute("address");
        empGroup.addAttribute("address.city");
        empGroup.addAttribute("address.postalCode");

//        empGroup.addAttribute("phoneNumbers").setUseDefaultFetchGroup(false);
        FetchGroup fullPhone = this.phoneDescriptor.getFetchGroupManager().createFullFetchGroup();
        // to preclude Employee from being loaded by phoneNumber.owner add it to the fetch group
        fullPhone.addAttribute("owner.id");
        empGroup.addAttribute("phoneNumbers", fullPhone);

        // Configure the dynamic FetchGroup
        query.setHint(QueryHints.FETCH_GROUP, empGroup);

        List<Employee> emps = query.getResultList();

        assertNotNull(emps);
        for (Employee emp : emps) {
            FetchGroupTracker tracker = (FetchGroupTracker) emp;

            assertNotNull(tracker._persistence_getFetchGroup());

            // Verify specified fields plus mandatory ones are loaded
            assertTrue(tracker._persistence_isAttributeFetched("id"));
            assertTrue(tracker._persistence_isAttributeFetched("firstName"));
            assertTrue(tracker._persistence_isAttributeFetched("lastName"));
            assertTrue(tracker._persistence_isAttributeFetched("version"));

            // Verify the other fields are not loaded
            assertFalse(tracker._persistence_isAttributeFetched("salary"));
            assertFalse(tracker._persistence_isAttributeFetched("startTime"));
            assertFalse(tracker._persistence_isAttributeFetched("endTime"));

            // Force the loading of lazy fields and verify
            emp.getSalary();

            assertTrue(tracker._persistence_isAttributeFetched("salary"));
            assertTrue(tracker._persistence_isAttributeFetched("startTime"));
            assertTrue(tracker._persistence_isAttributeFetched("endTime"));

            // Now we'll check the address uses the provided dynamic fetch-group
            FetchGroupTracker addrTracker = (FetchGroupTracker) emp.getAddress();
            assertNotNull("Address does not have a FetchGroup", addrTracker._persistence_getFetchGroup());
            assertTrue(addrTracker._persistence_isAttributeFetched("city"));
            assertTrue(addrTracker._persistence_isAttributeFetched("postalCode"));
            assertFalse(addrTracker._persistence_isAttributeFetched("street"));
            assertFalse(addrTracker._persistence_isAttributeFetched("country"));

            // Now we'll check the phoneNumbers use of the default fetch group
            for (PhoneNumber phone : emp.getPhoneNumbers()) {
                FetchGroupTracker phoneTracker = (FetchGroupTracker) phone;
                assertNull("PhoneNumber has a FetchGroup", phoneTracker._persistence_getFetchGroup());
            }
        }
    }

    @Test
    public void dynamicFetchGroup_EmployeeAddressEmptyPhone() {
        
    }
    public void dynamicFetchGroup_EmployeeAddressEmptyPhoneLoad() {
    
    }
    void internal_dynamicFetchGroup_EmployeeAddressEmptyPhone(boolean shouldLoad) {
        EntityManager em = createEntityManager();

        Query query = em.createQuery("SELECT e FROM Employee e WHERE e.gender = :GENDER");
        query.setParameter("GENDER", Gender.Male);

        // Define the fields to be fetched on Employee
        FetchGroup fg = new FetchGroup();
        fg.addAttribute("firstName");
        fg.addAttribute("lastName");
        fg.addAttribute("address.city");
        fg.addAttribute("address.postalCode");
        // to preclude Employee from being loaded by phoneNumber.owner add it to the fetch group
        FetchGroup ownerId = new FetchGroup();
        ownerId.addAttribute("owner.id");
        fg.addAttribute("phoneNumbers", ownerId);
        
        if(shouldLoad) {
            fg.setShouldLoad(true);
        }

        // Configure the dynamic FetchGroup
        query.setHint(QueryHints.FETCH_GROUP, fg);

        List<Employee> emps = query.getResultList();

        assertNotNull(emps);
        assertEquals(1 + (shouldLoad ? 0 : (emps.size() * 2)), getQuerySQLTracker(em).getTotalSQLSELECTCalls());
        
        for (Employee emp : emps) {
            FetchGroupTracker tracker = (FetchGroupTracker) emp;

            assertNotNull(tracker._persistence_getFetchGroup());

            // Verify specified fields plus mandatory ones are loaded
            assertTrue(tracker._persistence_isAttributeFetched("id"));
            assertTrue(tracker._persistence_isAttributeFetched("firstName"));
            assertTrue(tracker._persistence_isAttributeFetched("lastName"));
            assertTrue(tracker._persistence_isAttributeFetched("version"));

            // Verify the other fields are not loaded
            assertFalse(tracker._persistence_isAttributeFetched("salary"));
            assertFalse(tracker._persistence_isAttributeFetched("startTime"));
            assertFalse(tracker._persistence_isAttributeFetched("endTime"));

            // Force the loading of lazy fields and verify
            emp.getSalary();

            assertTrue(tracker._persistence_isAttributeFetched("salary"));
            assertTrue(tracker._persistence_isAttributeFetched("startTime"));
            assertTrue(tracker._persistence_isAttributeFetched("endTime"));

            // Now we'll check the address uses the provided dynamic fetch-group
            FetchGroupTracker addrTracker = (FetchGroupTracker) emp.getAddress();
            assertNotNull("Address does not have a FetchGroup", addrTracker._persistence_getFetchGroup());
            assertTrue(addrTracker._persistence_isAttributeFetched("city"));
            assertTrue(addrTracker._persistence_isAttributeFetched("postalCode"));
            assertFalse(addrTracker._persistence_isAttributeFetched("street"));
            assertFalse(addrTracker._persistence_isAttributeFetched("country"));

            // Now we'll check the phoneNumbers use of the default fetch group
            for (PhoneNumber phone : emp.getPhoneNumbers()) {
                FetchGroupTracker phoneTracker = (FetchGroupTracker) phone;
                assertNotNull("PhoneNumber does not have a FetchGroup", phoneTracker._persistence_getFetchGroup());
                assertFalse(phoneTracker._persistence_isAttributeFetched("number"));
                assertFalse(phoneTracker._persistence_isAttributeFetched("areaCode"));

                phone.getNumber();

                assertTrue(phoneTracker._persistence_isAttributeFetched("number"));
                assertTrue(phoneTracker._persistence_isAttributeFetched("areaCode"));
            }
        }
    }

    @Test
    public void dynamicHierarchicalFetchGroup() throws Exception {

        EntityManager em = createEntityManager();

        Query query = em.createQuery("SELECT e FROM Employee e WHERE e.lastName LIKE :LNAME AND e.manager.lastName <> e.lastName");
        query.setParameter("LNAME", "%");

        // Define the fields to be fetched on Employee
        FetchGroup fg = new FetchGroup();
        fg.addAttribute("firstName");
        fg.addAttribute("lastName");
        fg.addAttribute("salary");
        fg.addAttribute("gender");
        fg.addAttribute("manager.firstName");
        fg.addAttribute("manager.lastName");
        fg.addAttribute("manager.salary");
        fg.addAttribute("manager.gender");
        fg.addAttribute("manager.manager.firstName");
        fg.addAttribute("manager.manager.lastName");
        fg.addAttribute("manager.manager.salary");
        fg.addAttribute("manager.manager.gender");
        query.setHint(QueryHints.FETCH_GROUP, fg);

        List<Employee> emps = query.getResultList();

        int numSelect = getQuerySQLTracker(em).getTotalSQLSELECTCalls();

        for (Employee emp : emps) {
            assertFetched(emp, fg);
        }
        assertEquals(numSelect, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
    }

    @Test
    public void dynamicHierarchicalFetchGroup_JOIN_FETCH() throws Exception {

        EntityManager em = createEntityManager();

        Query query = em.createQuery("SELECT e FROM Employee e JOIN FETCH e.manager WHERE e.lastName LIKE :LNAME AND e.manager.lastName <> e.lastName");
        query.setParameter("LNAME", "%");

        // Define the fields to be fetched on Employee
        FetchGroup fg = new FetchGroup();
        fg.addAttribute("firstName");
        fg.addAttribute("lastName");
        fg.addAttribute("manager.firstName");
        fg.addAttribute("manager.salary");
        fg.addAttribute("manager.manager.gender");
        query.setHint(QueryHints.FETCH_GROUP, fg);

        List<Employee> emps = query.getResultList();

        int numSelect = getQuerySQLTracker(em).getTotalSQLSELECTCalls();

        HashSet<Integer> managerIds = new HashSet();
        for (Employee emp : emps) {
            managerIds.add(emp.getManager().getId());
        }
        int nManagersWithDepartment = 0;
        for (Employee emp : emps) {
            if(managerIds.contains(emp.getId())) {
                // this employee is also a manager of one of the emps, therefore it doesn't have fetch group
                assertNull(((FetchGroupTracker)emp)._persistence_getFetchGroup());
                if(emp.getDepartment() != null) {
                    // non-null department is selected in a separate sql due to:
                    // Bug 307881 - Join attribute defined on mapping is not fetched in nested query 
                    nManagersWithDepartment++;
                }
            } else {
                assertFetched(emp, fg);
            }
        }
        assertEquals(numSelect, 1 + nManagersWithDepartment);
    }
    
    @Test
    public void loadPlan() {
        EntityManager em = createEntityManager();
        
        Query query = em.createQuery("SELECT e FROM Employee e WHERE e.gender = :GENDER");
        query.setParameter("GENDER", Gender.Female);        
        List<Employee> employees = query.getResultList();
        
        LoadGroup plan = new LoadGroup();
        plan.addAttribute("address");
        plan.addAttribute("phoneNumbers");
        plan.addAttribute("manager.projects");
        plan.load(employees, (AbstractSession)((EntityManagerImpl)em.getDelegate()).getActiveSession());

        int numSelectBefore = getQuerySQLTracker(em).getTotalSQLSELECTCalls();
        
        // All indirections specified in the plan should have been already triggered.
        for(Employee emp : employees) {
            emp.getAddress();
            emp.getPhoneNumbers().size();
            if(emp.getManager() != null) {
                emp.getManager().getProjects().size();
            }
        }
        
        int numSelectAfter = getQuerySQLTracker(em).getTotalSQLSELECTCalls();
        assertEquals(numSelectBefore, numSelectAfter);
    }
}
