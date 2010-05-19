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
 *     05/19/2010-2.1 ailitchev - Bug 244124 - Add Nested FetchGroup 
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.jpa.fetchgroups;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import junit.framework.TestSuite;

import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.internal.queries.AttributeItem;
import org.eclipse.persistence.queries.FetchGroup;
import org.eclipse.persistence.queries.FetchGroupTracker;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.advanced.PhoneNumber;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee.Gender;

import org.junit.Test;

/**
 * @author dclarke
 * @since EclipseLink 2.1
 */
public class NestedNamedFetchGroupTests extends BaseFetchGroupTests {

    public NestedNamedFetchGroupTests() {
        super();
    }

    public NestedNamedFetchGroupTests(String name) {
        super(name);
    }

    public static junit.framework.Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("NestedNamedFetchGroupTests");
        
        suite.addTest(new NestedNamedFetchGroupTests("testSetup"));
        suite.addTest(new NestedNamedFetchGroupTests("dynamicFetchGroup_EmployeeAddress"));
        suite.addTest(new NestedNamedFetchGroupTests("dynamicFetchGroup_Employee_NullAddress"));
        suite.addTest(new NestedNamedFetchGroupTests("dynamicFetchGroup_EmployeeAddressNullPhone"));
        suite.addTest(new NestedNamedFetchGroupTests("dynamicFetchGroup_EmployeeAddressEmptyPhone"));
        suite.addTest(new NestedNamedFetchGroupTests("dynamicHierarchicalFetchGroup"));
        
        return suite;
    }
    
    /*
     * Set default fetch groups. 
     * 
     * @see PhoneCustomizer
     */
    public void setUp() {
        super.setUp();
        
        try {
            (new PhoneCustomizer()).customize(phoneDescriptor);
            // reprepare read queries after all fetch groups set into all descriptors.
            reprepareReadQueries(employeeDescriptor);
            reprepareReadQueries(phoneDescriptor);
        } catch (RuntimeException rtEx) {
            throw rtEx;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        assertConfig(phoneDescriptor, defaultPhoneFG, 0);
    }

    @Test
    public void dynamicFetchGroup_EmployeeAddress() throws Exception {
        EntityManager em = createEntityManager();

        Query query = em.createQuery("SELECT e FROM Employee e WHERE e.gender = :GENDER");
        query.setParameter("GENDER", Gender.Male);

        // Define the fields to be fetched on Employee
        FetchGroup fg = new FetchGroup();
        fg.addAttribute("firstName");
        fg.addAttribute("lastName");
        fg.addAttribute("address");
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

        // Define the fields to be fetched on Address
        FetchGroup addressGroup = new FetchGroup();
        addressGroup.addAttribute("city");
        addressGroup.addAttribute("postalCode");

        empGroup.addAttribute("address", addressGroup);

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
    public void dynamicFetchGroup_EmployeeAddressEmptyPhone() throws Exception {
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

        empGroup.addAttribute("address", addressGroup);
        // to preclude Employee from being loaded by phoneNumber.owner add it to the fetch group
        FetchGroup ownerId = new FetchGroup();
        ownerId.addAttribute("owner.id");
        empGroup.addAttribute("phoneNumbers", ownerId);

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

        Query query = em.createQuery("SELECT e FROM Employee e WHERE e.gender = :GENDER");
        query.setParameter("GENDER", Gender.Male);

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

        AttributeItem mgrItem = fg.getItem("manager");
        assertNotNull(mgrItem);
        assertNotNull(mgrItem.getGroup());
        AttributeItem mgrMgrItem = fg.getItem("manager.manager");
        assertNotNull(mgrMgrItem);
        assertNotNull(mgrMgrItem.getGroup());

        query.setHint(QueryHints.FETCH_GROUP, fg);

        List<Employee> emps = query.getResultList();

        int numSelect = getQuerySQLTracker(em).getTotalSQLSELECTCalls();

        List<Employee> loadedEmps = new ArrayList<Employee>();
        loadedEmps.addAll(emps);

        for (Employee emp : emps) {
            if (!loadedEmps.contains(emp)) {
                assertFetched(emp, fg);
            }

            assertNotFetchedAttribute(emp, "startDate");
            loadedEmps.add(emp);
        }
        // TODO assertEquals(1 + loadedEmps.size() - emps.size(), numSelect);
    }
}
