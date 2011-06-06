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
 *     09/21/2010-2.2 Frank Schwarz and ailitchev - Bug 325684 - QueryHints.BATCH combined with QueryHints.FETCH_GROUP_LOAD will cause NPE 
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.jpa.fetchgroups;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import junit.framework.TestSuite;

import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.internal.helper.IdentityHashSet;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.internal.queries.EntityFetchGroup;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.queries.FetchGroup;
import org.eclipse.persistence.queries.FetchGroupTracker;
import org.eclipse.persistence.queries.LoadGroup;
import org.eclipse.persistence.testing.models.jpa.advanced.Address;
import org.eclipse.persistence.testing.models.jpa.advanced.Department;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.advanced.PhoneNumber;
import org.eclipse.persistence.testing.models.jpa.advanced.Project;
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
//**temp        suite.addTest(new NestedFetchGroupTests("dynamicHierarchicalFetchGroup_JOIN_FETCH"));
        suite.addTest(new NestedFetchGroupTests("dynamicHierarchicalFetchGroup_JOIN_FETCH_Copy"));
//**temp        suite.addTest(new NestedFetchGroupTests("managerDoubleNestedFetchGroupWithJoinFetch"));
        suite.addTest(new NestedFetchGroupTests("managerTripleNestedFetchGroupWithJoinFetch"));
        suite.addTest(new NestedFetchGroupTests("allNestedFetchGroupWithJoinFetch"));
        suite.addTest(new NestedFetchGroupTests("joinFetchDefaultFetchGroup"));
        suite.addTest(new NestedFetchGroupTests("joinFetchOutsideOfFetchGroup"));
        suite.addTest(new NestedFetchGroupTests("simpleNestedFetchGroupWithBatch"));
        suite.addTest(new NestedFetchGroupTests("simpleLoadGroup"));
        suite.addTest(new NestedFetchGroupTests("simpleFetchGroupLoadWithBatch"));
        
        return suite;
    }
    
    public void setUp() {
        super.setUp();
        
        defaultPhoneFG = new FetchGroup();
        defaultPhoneFG.addAttribute("number");
        phoneDescriptor.getFetchGroupManager().setDefaultFetchGroup(defaultPhoneFG);
        
        reprepareReadQueries(phoneDescriptor);
        reprepareReadQueries(employeeDescriptor);

        // We'll put a default FetchGroup on Phone
        assertNotNull(phoneDescriptor.getDefaultFetchGroup());
        assertNotNull(phoneDescriptor.getDescriptorQueryManager().getReadObjectQuery().getExecutionFetchGroup());
        assertTrue(phoneDescriptor.getFetchGroupManager().getFetchGroups().isEmpty());
    }

    @Test
    public void dynamicFetchGroup_EmployeeAddress() throws Exception {
        EntityManager em = createEntityManager();
        try {
            beginTransaction(em);

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
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    @Test
    public void dynamicFetchGroup_Employee_NullAddress() throws Exception {
        EntityManager em = createEntityManager();
        try {
            beginTransaction(em);

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
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    @Test
    public void dynamicFetchGroup_EmployeeAddressNullPhone() throws Exception {
        EntityManager em = createEntityManager();
        try {
            beginTransaction(em);

            Query query = em.createQuery("SELECT e FROM Employee e WHERE e.gender = :GENDER");
            query.setParameter("GENDER", Gender.Male);

            // Define the fields to be fetched on Employee
            FetchGroup empGroup = new FetchGroup();
            empGroup.addAttribute("firstName");
            empGroup.addAttribute("lastName");
            empGroup.addAttribute("address");
            empGroup.addAttribute("address.city");
            empGroup.addAttribute("address.postalCode");

            //empGroup.addAttribute("phoneNumbers").setUseDefaultFetchGroup(false);
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
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    @Test
    public void dynamicFetchGroup_EmployeeAddressEmptyPhone() {
        
    }
    public void dynamicFetchGroup_EmployeeAddressEmptyPhoneLoad() {
    
    }
    void internal_dynamicFetchGroup_EmployeeAddressEmptyPhone(boolean shouldLoad) {
        EntityManager em = createEntityManager();
        try {
            beginTransaction(em);

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
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
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
        internalDynamicHierarchicalFetchGroup_JOIN_FETCH(false);
    }

    @Test
    public void dynamicHierarchicalFetchGroup_JOIN_FETCH_Copy() throws Exception {
        internalDynamicHierarchicalFetchGroup_JOIN_FETCH(true);
    }

    void internalDynamicHierarchicalFetchGroup_JOIN_FETCH(boolean useCopy) throws Exception {

        EntityManager em = createEntityManager();
        try {
            beginTransaction(em);

            Query query = em.createQuery("SELECT e FROM Employee e JOIN FETCH e.manager WHERE e.lastName LIKE :LNAME AND e.manager.lastName <> e.lastName");
            query.setParameter("LNAME", "%");

            // Define the fields to be fetched on Employee
            FetchGroup fg = new FetchGroup();
            fg.addAttribute("firstName");
            fg.addAttribute("lastName");
            fg.addAttribute("manager.firstName");
            fg.addAttribute("manager.salary");
            fg.addAttribute("manager.manager");
            query.setHint(QueryHints.FETCH_GROUP, fg);
            
            // applied to the selected Employee who is not a manager of some other selected Employee
            FetchGroup employeeFG = new EntityFetchGroup(new String[]{"id", "version", "firstName", "lastName", "manager"}); 
            // applied to the manager of a selected Employee who is not selected as an Employee
            FetchGroup managerFG = new EntityFetchGroup(new String[]{"id", "version", "firstName", "salary", "manager"});
            // applied to the object which is both selected as an Employee and the manager of another selected Employee
            FetchGroup employeeManagerFG = employeeDescriptor.getFetchGroupManager().flatUnionFetchGroups(employeeFG, managerFG); 
            
            // used in useCopy case only
            FetchGroup employeeManagerManagerFG = null;
            if(useCopy) {
                employeeManagerManagerFG = employeeDescriptor.getFetchGroupManager().flatUnionFetchGroups(new EntityFetchGroup("manager"), employeeDescriptor.getFetchGroupManager().getNonReferenceEntityFetchGroup()); 
            }
            
            /*
             * These are the first names of Employees involved; --> means "managed by".
             * All the employees here are returned by the query except Jill and Sarah-loo (they got no manager).
             * 
             * Sarah ------>Bob -------> John ----> Jim-bob ---> Jill ---> null
             * Charles -----^   Marius ----^
             * 
             * Nancy ------> Sarah-loo ---> null
             * 
             * Sarah, Charles, Nancy should have employeeFG;
             * Sarah-loo - managerFG;
             * Bob, Marius - employeeManagerFG;
             * John, Jim-bob should have a union of three fetch groups: {firstName,lastName,manager}, {firstName,salary,manager}, {manager}
             * Jill should have a union of two groups:  {firstName,salary,manager}, {manager}
             * The result for all three of them is the same:
             *   in read case (useCopy == false) it should be null (no fetch group), because defaultFetchGroup is null;
             *   in copy case (useCopy == true) it should be a union of "manager" and all non relational attributes (NonReferenceEntityFetchGroup).
             * That's how leaf reference attribute is treated: 
             *   default fetch group for read;
             *   NonReferenceEntityFetchGroup (see FetchGroupManager) for copy.
             * In this test defaultFetchGroup (null) / NonReferenceEntityFetchGroup comes from {manager},
             *   in useCopy == true case additional manager comes from another fetch group (they all contain manager).
             */
            
            List<Employee> emps = query.getResultList();
            
            if(useCopy) {
                /*for(Employee emp : emps) {
                    int idHashCode =  System.identityHashCode(emp);
                    System.out.println(emp.getFirstName() + '\t' + idHashCode);
                }*/
                emps = (List)JpaHelper.getEntityManager(em).copy(emps, fg);
            }

            // Sets of managed Employees keyed by their manager
            Map<Employee, Set<Employee>> managedEmployeesByManager = new IdentityHashMap();
            for (Employee emp : emps) {
                Employee manager = emp.getManager(); 
                Set<Employee> managedEmployees = managedEmployeesByManager.get(manager);
                if(managedEmployees == null) {
                    managedEmployees = new IdentityHashSet();
                    managedEmployeesByManager.put(manager, managedEmployees);
                }
                managedEmployees.add(emp);
            }

            for (Employee emp : emps) {
                Set<Employee> managedEmployees = managedEmployeesByManager.get(emp);
                Employee manager = emp.getManager();
                if(managedEmployees == null) {
                    // employee is NOT a manager of any of the selected employees:
                    assertFetched(emp, employeeFG);

                    Set<Employee> managedByManagerEmployees = managedEmployeesByManager.get(manager); 
                    // indicates whether one of manager's managed employees is a manager itself
                    boolean isManagersManager = false;
                    for(Employee managedEmp : managedByManagerEmployees) {
                        if(managedEmployeesByManager.containsKey(managedEmp)) {
                            isManagersManager = true;
                            break;
                        }
                    }
                    if(isManagersManager) {
                        if(useCopy) {
                            // for at least one of the selected employees manager is manager's manager:
                            //   someSelectedEmp.getManager().getManager() == manager
                            // That means for someSelectedEmp emp is defined by {manager.manager} FetchGroup's item,
                            // which means NonReferenceEntityFetchGroup (only non-reference attributes + pk)
                            // for another employee it's just a manager - which means it should include "manager":
                            // employeeManagerManagerFG is the union of these two EntityFetchGroups.
                            assertFetched(manager, employeeManagerManagerFG);
                        } else {
                            // for at least one of the selected employees manager is manager's manager:
                            //   someSelectedEmp.getManager().getManager() == manager
                            // That means for someSelectedEmp emp is defined by {manager.manager} FetchGroup's item,
                            // which means no fetch group should be used. 
                            assertNoFetchGroup(manager);
                        }
                    } else {
                        // it's not manager's manager
                        if(emps.contains(manager)) {
                            // it's a manager of one of the selected Employees, and selected itself.
                            assertFetched(manager, employeeManagerFG);
                        } else {
                            // it's a manager of one of the selected Employees, but not selected itself.
                            assertFetched(manager, managerFG);
                        }
                    }
                } else {
                    // employee is a manager of at least one of the selected employees
                    // indicates whether one of emp's managed employees is a manager itself
                    boolean isManagersManager = false;
                    for(Employee managedEmp : managedEmployees) {
                        if(managedEmployeesByManager.containsKey(managedEmp)) {
                            isManagersManager = true;
                            break;
                        }
                    }
                    
                    if(isManagersManager) {
                        if(useCopy) {
                            // for at least one of the selected employees manager is manager's manager:
                            //   someSelectedEmp.getManager().getManager() == manager
                            // That means for someSelectedEmp emp is defined by {manager.manager} FetchGroup's item,
                            // which means NonReferenceEntityFetchGroup (only non-reference attributes + pk)
                            // for another employee it's just a manager - which means it should include "manager":
                            // employeeManagerManagerFG is the union of these two EntityFetchGroups.
                            assertFetched(emp, employeeManagerManagerFG);
                        } else {
                            // for at least one of the selected employees emp is manager's manager:
                            //   someSelectedEmp.getManager().getManager() == emp
                            // That means for someSelectedEmp emp is defined by {manager.manager} FetchGroup's item,
                            // which means no fetch group should be used. 
                            assertNoFetchGroup(emp);
                        }
                    } else {
                        // it's selected employee, manager of some selected employee, but not manager's manager
                        assertFetched(emp, employeeManagerFG);
                    }
                    
                    if(useCopy) {
                        // for at least one of the selected employees manager is manager's manager:
                        //   someSelectedEmp.getManager().getManager() == manager
                        // That means for someSelectedEmp emp is defined by {manager.manager} FetchGroup's item,
                        // which means NonReferenceEntityFetchGroup (only non-reference attributes + pk)
                        // for another employee it's just a manager - which means it should include "manager":
                        // employeeManagerManagerFG is the union of these two EntityFetchGroups.
                        assertFetched(manager, employeeManagerManagerFG);
                    } else {
                        // for at least one of the selected employees manager is manager's manager:
                        //   someSelectedEmp.getManager().getManager() == manager
                        // That means for someSelectedEmp emp.getManager() is defined by {manager.manager} FetchGroup's item,
                        // which means no fetch group should be used. 
                        assertNoFetchGroup(manager);
                    }
                }
            }
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }
    
   @Test
   public void managerDoubleNestedFetchGroupWithJoinFetch() {
       managerNestedFetchGroupWithJoinFetch(true);
   }

   @Test
   public void managerTripleNestedFetchGroupWithJoinFetch() {
       managerNestedFetchGroupWithJoinFetch(false);
   }

   void managerNestedFetchGroupWithJoinFetch(boolean isDouble) {
        EntityManager em = createEntityManager();
        try {
            beginTransaction(em);

            Query query = em.createQuery("SELECT e FROM Employee e WHERE e.manager.manager IS NOT NULL");
            FetchGroup managerFG = new FetchGroup();
            if(isDouble) {
                // Double
                managerFG.addAttribute("manager.manager");
            } else {
                // Triple
                managerFG.addAttribute("manager.manager.manager");
            }

            query.setHint(QueryHints.FETCH_GROUP, managerFG);
            query.setHint(QueryHints.LEFT_FETCH, "e.manager");

            assertNotNull(getFetchGroup(query));
            assertSame(managerFG, getFetchGroup(query));

            List<Employee> employees = query.getResultList();

            int nSql;
            if(isDouble) {
                // In this case the number of generated sqls is unpredictable.
                // Additional sql generated for every object that 
                // has been first fetched as manager.manager
                // and then is selected as an employee - getting its manger
                // performed without fetch group therefore triggering reading of the whole object
                nSql = getQuerySQLTracker(em).getTotalSQLSELECTCalls();
            } else {
                assertEquals(1, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
                nSql = 1;
            }
            
            Employee emp = employees.get(0);
            assertFetched(emp, managerFG);
            
            // manager (if not null) is instantiated by the fetch group, before emp.getManager call.
            Employee manager = emp.getManager();
            assertEquals(nSql, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
            assertFetched(manager, managerFG);
            
            // instantiates the whole object
            emp.getLastName();
            nSql++;
            assertNoFetchGroup(emp);
            assertEquals(nSql, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
            
            assertFetched(manager, managerFG);
            // instantiates the whole object
            manager.getLastName();
            nSql++;
            assertNoFetchGroup(manager);
            assertEquals(nSql, getQuerySQLTracker(em).getTotalSQLSELECTCalls());

            nSql++;
            for (PhoneNumber phone : emp.getPhoneNumbers()) {
                assertFetched(phone, this.defaultPhoneFG);
                phone.getAreaCode();
                nSql++;
                assertNoFetchGroup(phone);
            }
            assertEquals(nSql, getQuerySQLTracker(em).getTotalSQLSELECTCalls());

            nSql++;
            for (PhoneNumber phone : manager.getPhoneNumbers()) {
                assertFetched(phone, this.defaultPhoneFG);
                phone.getAreaCode();
                nSql++;
                assertNoFetchGroup(phone);
            }
            assertEquals(nSql, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

   @Test
   public void allNestedFetchGroupWithJoinFetch() {
        EntityManager em = createEntityManager();
        try {
            beginTransaction(em);

            // select employees who are neither managers nor team leaders
            Query query = em.createQuery("SELECT e FROM Employee e WHERE NOT EXISTS(SELECT p.id FROM Project p WHERE p.teamLeader = e) AND NOT EXISTS(SELECT e2.id FROM Employee e2 WHERE e2.manager = e)");
            FetchGroup employeeFG = new FetchGroup("employee");
            employeeFG.addAttribute("lastName");
            
            employeeFG.addAttribute("address.country");
            employeeFG.addAttribute("address.city");
            query.setHint(QueryHints.LEFT_FETCH, "e.address");
            
            employeeFG.addAttribute("phoneNumbers");
            query.setHint(QueryHints.LEFT_FETCH, "e.phoneNumbers");
            
            employeeFG.addAttribute("projects.name");

            employeeFG.addAttribute("projects.teamLeader.firstName");
            //employeeFG.addAttribute("projects.teamLeader.address.street");
            //employeeFG.addAttribute("projects.teamLeader.address.postalCode");
            employeeFG.addAttribute("projects.teamLeader.phoneNumbers.owner");
            employeeFG.addAttribute("projects.teamLeader.phoneNumbers.type");
            employeeFG.addAttribute("projects.teamLeader.phoneNumbers.areaCode");
            query.setHint(QueryHints.LEFT_FETCH, "e.projects.teamLeader.phoneNumbers");
            
            employeeFG.addAttribute("manager.firstName");
            //employeeFG.addAttribute("manager.address.street");
            //employeeFG.addAttribute("manager.address.postalCode");
            employeeFG.addAttribute("manager.phoneNumbers.owner");
            employeeFG.addAttribute("manager.phoneNumbers.type");
            employeeFG.addAttribute("manager.phoneNumbers.areaCode");
            query.setHint(QueryHints.LEFT_FETCH, "e.manager.phoneNumbers");

            // department attribute defined with JoinFetchType.OUTER
            employeeFG.addAttribute("department.name");
            
            query.setHint(QueryHints.FETCH_GROUP, employeeFG);

            List<Employee> employees = query.getResultList();
            assertEquals(1, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
            
            for(Employee emp :employees) {
                assertFetched(emp, employeeFG);
                
                Address address = emp.getAddress();
                if(address != null) {
                    assertFetched(address, employeeFG.getGroup("address"));
                }

                for (PhoneNumber phone : emp.getPhoneNumbers()) {
                    assertFetched(phone, defaultPhoneFG);
                }
                
                for (Project project : emp.getProjects()) {
                    assertFetched(project, employeeFG.getGroup("projects"));
                    Employee teamLeader = project.getTeamLeader();
                    if(teamLeader != null) {
                        assertFetched(teamLeader, employeeFG.getGroup("projects.teamLeader"));
                        for (PhoneNumber phone : teamLeader.getPhoneNumbers()) {
                            assertFetched(phone, employeeFG.getGroup("projects.teamLeader.phoneNumbers"));
                        }
                    }
                }
                
                Employee manager = emp.getManager();
                if(manager != null) {
                    assertFetched(manager, employeeFG.getGroup("manager"));
                    for (PhoneNumber phone : manager.getPhoneNumbers()) {
                        assertFetched(phone, employeeFG.getGroup("manager.phoneNumbers"));
                    }
                }
                
                Department department = emp.getDepartment();
                if(department != null) {
                    assertFetched(department, employeeFG.getGroup("department"));
                }
            }
            assertEquals(1, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

   @Test
   public void joinFetchDefaultFetchGroup() throws Exception {
        EntityManager em = createEntityManager();

        Query query = em.createQuery("SELECT e FROM Employee e");
        query.setHint(QueryHints.LEFT_FETCH, "e.phoneNumbers");

        List<Employee> employees = query.getResultList();
        
        for(Employee emp : employees) {
            assertNoFetchGroup(emp);
            for (PhoneNumber phone : emp.getPhoneNumbers()) {
                assertFetched(phone, defaultPhoneFG);
            }
        }
    }

   @Test
   public void joinFetchOutsideOfFetchGroup() throws Exception {
        EntityManager em = createEntityManager();

        Query query = em.createQuery("SELECT e FROM Employee e");
        // Define the fields to be fetched on Employee
        FetchGroup fg = new FetchGroup();
        fg.addAttribute("firstName");
        fg.addAttribute("lastName");
        query.setHint(QueryHints.FETCH_GROUP, fg);
        query.setHint(QueryHints.LEFT_FETCH, "e.address");

        List<Employee> employees = query.getResultList();        
    }

   @Test
   public void simpleNestedFetchGroupWithBatch() {
       EntityManager em = createEntityManager();
        try {
            beginTransaction(em);

            Query query = em.createQuery("SELECT e FROM Employee e");

            // Define the fields to be fetched on Employee
            FetchGroup employeeFG = new FetchGroup();
            employeeFG.setShouldLoad(true);
            employeeFG.addAttribute("firstName");
            employeeFG.addAttribute("lastName");
            employeeFG.addAttribute("address.country");
            employeeFG.addAttribute("address.city");
            
            FetchGroup phonesFG = defaultPhoneFG.clone();
            // to preclude PhoneNumber from triggering owner's full read
            phonesFG.addAttribute("owner.id");
            employeeFG.addAttribute("phoneNumbers", phonesFG);
            
            FetchGroup projectsFG = new FetchGroup("projects");
            projectsFG.addAttribute("name");
            projectsFG.addAttribute("name");
            // to preclude Project from triggering full read of the referenced Employee(s)
            projectsFG.addAttribute("teamMembers.id");
            projectsFG.addAttribute("teamLeader.id");
            employeeFG.addAttribute("projects", projectsFG);

            query.setHint(QueryHints.FETCH_GROUP, employeeFG);
            
            query.setHint(QueryHints.BATCH, "e.address");
            query.setHint(QueryHints.BATCH, "e.phoneNumbers");
            query.setHint(QueryHints.BATCH, "e.projects");
            
            // A single sql will be used to read all Project subclasses.
            query.setHint(QueryHints.INHERITANCE_OUTER_JOIN, "true");

            List<Employee> employees = query.getResultList();

            // Employee, Address, PhoneNumbers, Projects - an sql per class.
            // Address, PhoneNumbers and Projects are already loaded because
            // employeeFG.shouldLoad is set to true.
            assertEquals(4, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
            
            // verify fetch groups
            for(Employee emp : employees) {
                assertFetched(emp, employeeFG);

                Address address = emp.getAddress();
                if(address != null) {
                         assertFetched(address, employeeFG.getGroup("address"));
                }

                for (PhoneNumber phone : emp.getPhoneNumbers()) {
                         assertFetched(phone, phonesFG);
                }
                
                for (Project project : emp.getProjects()) {
                         assertFetched(project, projectsFG);
                }
            }

            // Now let's access an attribute outside of the fetch group.
            // That triggers loading of the whole object.
            for(Employee emp : employees) {
                emp.getSalary();
                assertNoFetchGroup(emp);

                Address address = emp.getAddress();
                if(address != null) {
                   address.getStreet();
                   assertNoFetchGroup(address);
                }

                for (PhoneNumber phone : emp.getPhoneNumbers()) {
                    phone.getAreaCode();
                    assertNoFetchGroup(phone);
                }
                
                for (Project project : emp.getProjects()) {
                    project.getDescription();
                    assertNoFetchGroup(project);
                }
            }
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
   }
   
   @Test
    public void simpleLoadGroup() {
        EntityManager em = createEntityManager();
        try {
            beginTransaction(em);

             Query query = em.createQuery("SELECT e FROM Employee e WHERE e.gender = :GENDER");
             query.setParameter("GENDER", Gender.Female);
             List<Employee> employees = query.getResultList();
             
             LoadGroup group = new LoadGroup();
             group.addAttribute("address");
             group.addAttribute("phoneNumbers");
             group.addAttribute("manager.projects");
             ((AbstractSession)((EntityManagerImpl)em.getDelegate()).getActiveSession()).load(employees, group);

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
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }
   
   @Test
   // Bug 325684 - QueryHints.BATCH combined with QueryHints.FETCH_GROUP_LOAD will cause NPE 
    public void simpleFetchGroupLoadWithBatch() {
        EntityManager em = createEntityManager();
        try {
            beginTransaction(em);

             FetchGroup projectGroup = new FetchGroup();
             projectGroup.addAttribute("name");

             FetchGroup employeeGroup = new FetchGroup();
             employeeGroup.addAttribute("firstName");
             employeeGroup.addAttribute("lastName");
             employeeGroup.addAttribute("projects", projectGroup);
                          
             Query query = em.createQuery("SELECT e FROM Employee e WHERE e.gender = :GENDER");
             query.setParameter("GENDER", Gender.Female);
             query.setHint(QueryHints.FETCH_GROUP, employeeGroup);
             query.setHint(QueryHints.FETCH_GROUP_LOAD, "true");
             query.setHint(QueryHints.BATCH, "e.projects");
             List<Employee> employees = query.getResultList();

             int numSelectBefore = getQuerySQLTracker(em).getTotalSQLSELECTCalls();
             
             // All indirections specified in the plan should have been already triggered.
             for (Employee e : employees) {
                 e.getProjects().size();
             }
             
             int numSelectAfter = getQuerySQLTracker(em).getTotalSQLSELECTCalls();
             assertEquals(numSelectBefore, numSelectAfter);
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }
}
