/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
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

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import junit.framework.TestSuite;

import org.eclipse.persistence.config.CacheUsage;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.indirection.IndirectList;
import org.eclipse.persistence.internal.helper.SerializationHelper;
import org.eclipse.persistence.internal.queries.AttributeItem;
import org.eclipse.persistence.internal.queries.EntityFetchGroup;
import org.eclipse.persistence.queries.FetchGroup;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.advanced.PhoneNumber;

import org.junit.Test;

/**
 * Simple tests to verify the functionality of {@link FetchGroup} when the
 * entities are detached through serialization.
 * 
 * @author dclarke
 * @since EclipseLink 2.1
 */
public class SimpleSerializeFetchGroupTests extends BaseFetchGroupTests {

    public SimpleSerializeFetchGroupTests() {
        super();
    }

    public SimpleSerializeFetchGroupTests(String name) {
        super(name);
    }

    public static junit.framework.Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("SimpleSerializeFetchGroupTests");
        
        suite.addTest(new SimpleSerializeFetchGroupTests("testSetup"));
        suite.addTest(new SimpleSerializeFetchGroupTests("verifyWriteReplaceOnFetchGroup"));
        suite.addTest(new SimpleSerializeFetchGroupTests("findMinimalFetchGroup"));
        suite.addTest(new SimpleSerializeFetchGroupTests("findEmptyFetchGroup_setUnfetchedSalary"));
        suite.addTest(new SimpleSerializeFetchGroupTests("verifyAddAttributeInDetachedEntityFetchGroup"));
        suite.addTest(new SimpleSerializeFetchGroupTests("singleResultEmptyFetchGroup"));
        suite.addTest(new SimpleSerializeFetchGroupTests("resultListEmptyFetchGroup"));
        suite.addTest(new SimpleSerializeFetchGroupTests("resultListPeriodFetchGroup"));
        suite.addTest(new SimpleSerializeFetchGroupTests("managerFetchGroup"));
        suite.addTest(new SimpleSerializeFetchGroupTests("managerFetchGroupWithJoinFetch"));
        suite.addTest(new SimpleSerializeFetchGroupTests("employeeNamesFetchGroup"));
        suite.addTest(new SimpleSerializeFetchGroupTests("joinFetchEmployeeAddressWithDynamicFetchGroup"));
        suite.addTest(new SimpleSerializeFetchGroupTests("joinFetchEmployeeAddressPhoneWithDynamicFetchGroup"));
        suite.addTest(new SimpleSerializeFetchGroupTests("verifyUnfetchedAttributes"));
        suite.addTest(new SimpleSerializeFetchGroupTests("verifyFetchedRelationshipAttributes"));
        suite.addTest(new SimpleSerializeFetchGroupTests("simpleSerializeAndMerge"));
        
        return suite;
    }

    @Test
    public void verifyWriteReplaceOnFetchGroup() throws Exception {
        EntityFetchGroup fg = new EntityFetchGroup(new String[]{"basic", "a"});
//        fg.addAttribute("basic");
//        fg.addAttribute("a.b");

//        assertTrue(fg.getClass() == FetchGroup.class);

        FetchGroup serFG = serialize(fg);

        assertNotNull(serFG);
        assertTrue(serFG.getClass() == EntityFetchGroup.class);
        assertTrue(serFG.hasItems());

        AttributeItem basicFI = serFG.getItem("basic");

        assertNotNull(basicFI);
//        assertTrue(basicFI instanceof DetachedFetchItem);

        AttributeItem aFI = serFG.getItem("a");

        assertNotNull(aFI);
//        assertTrue(aFI instanceof DetachedFetchItem);
        // serialized EntityFetchGroup is always flat - doesn't have nested groups.
        assertNull(aFI.getGroup());
/*        assertNotNull(aFI.getGroup());
        assertTrue(aFI.getGroup() instanceof EntityFetchGroup);
        EntityFetchGroup aEFG = (EntityFetchGroup) aFI.getGroup();
        assertNull(aEFG.getParent());
        assertTrue(aEFG.hasItems());

        AttributeItem bFI = aEFG.getItem("b");

        assertNotNull(bFI);
//        assertTrue(bFI instanceof DetachedFetchItem);
        assertNull(bFI.getGroup());*/
    }

    @Test
    public void findMinimalFetchGroup() throws Exception {
        EntityManager em = createEntityManager();
        int minId = minimumEmployeeId(em);

        assertEquals(1, getQuerySQLTracker(em).getTotalSQLSELECTCalls());

        Map<String, Object> properties = new HashMap<String, Object>();

        FetchGroup fg = new FetchGroup();
        fg.addAttribute("id");
        fg.addAttribute("version");

        properties.put(QueryHints.FETCH_GROUP, fg);

        Employee emp = em.find(Employee.class, minId, properties);

        assertNotNull(emp);
        assertFetched(emp, fg);
        assertEquals(2, getQuerySQLTracker(em).getTotalSQLSELECTCalls());

        assertFetchedAttribute(emp, "id");
        assertFetchedAttribute(emp, "version");
        assertNotFetchedAttribute(emp, "firstName");
        assertNotFetchedAttribute(emp, "lastName");
        assertNotFetchedAttribute(emp, "gender");
        assertNotFetchedAttribute(emp, "salary");
        assertNotFetchedAttribute(emp, "startTime");
        assertNotFetchedAttribute(emp, "endTime");
        assertNotFetchedAttribute(emp, "period");
        assertNotFetchedAttribute(emp, "address");
        assertNotFetchedAttribute(emp, "manager");
        assertNotFetchedAttribute(emp, "phoneNumbers");
        assertNotFetchedAttribute(emp, "projects");

        assertTrue(getFetchGroup(emp).getClass() == EntityFetchGroup.class);
        Employee serEmp = serialize(emp);

        assertNotNull(serEmp);
        assertFetchedAttribute(serEmp, "id");
        assertFetchedAttribute(serEmp, "version");
        assertNotFetchedAttribute(serEmp, "firstName");
        assertNotFetchedAttribute(serEmp, "lastName");
        assertNotFetchedAttribute(serEmp, "gender");
        assertNotFetchedAttribute(serEmp, "salary");
        assertNotFetchedAttribute(serEmp, "startTime");
        assertNotFetchedAttribute(serEmp, "endTime");
        assertNotFetchedAttribute(serEmp, "period");
        assertNotFetchedAttribute(serEmp, "address");
        assertNotFetchedAttribute(serEmp, "manager");
        assertNotFetchedAttribute(serEmp, "phoneNumbers");
        assertNotFetchedAttribute(serEmp, "projects");

        assertTrue(getFetchGroup(serEmp) instanceof EntityFetchGroup);

        serEmp.setFirstName("Doug");
        assertFetchedAttribute(serEmp, "firstName");
    }

    @Test
    public void findEmptyFetchGroup_setUnfetchedSalary() throws Exception {
        EntityManager em = createEntityManager();
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
    }

    /**
     * Verify that attributes added to detached EntityFetchGroup are added using
     * DetachedFetchItem
     */
    @Test
    public void verifyAddAttributeInDetachedEntityFetchGroup() {
        EntityFetchGroup detFG = new EntityFetchGroup(new String[]{"basic", "a"});

//        detFG.addAttribute("basic");
//        detFG.addAttribute("a.b");

//        assertNull(detFG.getParent());
        assertEquals(2, detFG.getItems().size());

        AttributeItem basicItem = detFG.getItem("basic");
        assertNotNull(basicItem);
        assertEquals("basic", basicItem.getAttributeName());
//        assertTrue(basicItem instanceof DetachedFetchItem);
        assertNull(basicItem.getGroup());
        assertSame(detFG, basicItem.getParent());
//        assertFalse(basicItem.useDefaultFetchGroup());

        AttributeItem aItem = detFG.getItem("a");
        assertNotNull(aItem);
        assertEquals("a", aItem.getAttributeName());
//        assertTrue(aItem instanceof DetachedFetchItem);
        // serialized EntityFetchGroup is always flat - doesn't have nested groups.
////        assertNull(aItem.getGroup());
        assertNull(aItem.getGroup());
        assertSame(detFG, aItem.getParent());
//        assertFalse(aItem.useDefaultFetchGroup());
//        assertTrue(aItem.getGroup() instanceof EntityFetchGroup);

//        EntityFetchGroup aFG = (EntityFetchGroup) aItem.getGroup();

//        assertEquals(1, aFG.getItems().size());

//        AttributeItem bItem = aFG.getItem("b");
//        assertNotNull(bItem);
//        assertEquals("b", bItem.getAttributeName());
//        assertTrue(bItem instanceof DetachedFetchItem);
  //      assertNull(bItem.getGroup());
//        assertSame(aFG, bItem.getParent());
//        assertFalse(bItem.useDefaultFetchGroup());
    }

    @Test
    public void singleResultEmptyFetchGroup() throws Exception {
        EntityManager em = createEntityManager();

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
    }

    /**
     * 
     */
    @Test
    public void resultListEmptyFetchGroup() throws Exception {
        EntityManager em = createEntityManager();

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
    }

    /**
     * 
     */
    @Test
    public void resultListPeriodFetchGroup() throws Exception {
        EntityManager em = createEntityManager();

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
    }

    @Test
    public void managerFetchGroup() throws Exception {
        EntityManager em = createEntityManager();

        // Use q query since find will only use default fetch group
//        Query query = em.createQuery("SELECT e FROM Employee e WHERE e.id = :ID");
//        query.setParameter("ID", minimumEmployeeId(em));

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
        
        // manager (if not null) hasn't been instantiated yet.
        int nSqlToAdd = 0;
        if (emp.getManager() != null) {
            assertFetchedAttribute(emp, "manager");
            // additional sql to select the manager
            nSqlToAdd++;
        }
        
        assertEquals(1 + nSqlToAdd, getQuerySQLTracker(em).getTotalSQLSELECTCalls());

        emp.getLastName();

        assertEquals(2 + nSqlToAdd, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
        assertNoFetchGroup(emp);

        for (PhoneNumber phone : emp.getPhoneNumbers()) {
            assertNoFetchGroup(phone);
            phone.getAreaCode();
        }

        assertEquals(3 + nSqlToAdd, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
    }

    @Test
    public void managerFetchGroupWithJoinFetch() throws Exception {
        EntityManager em = createEntityManager();

//        int minId = minimumEmployeeId(em);
//        assertEquals(1, getQuerySQLTracker(em).getTotalSQLSELECTCalls());

        // Use q query since find will only use default fetch group
//        Query query = em.createQuery("SELECT e FROM Employee e WHERE e.id = :ID");
//        query.setParameter("ID", minId);

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
        
        // manager has been already instantiated by the query.
        emp.getManager();
        assertEquals(1, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
        
        // instantiates the whiole object
        emp.getLastName();

        assertEquals(2, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
        assertNoFetchGroup(emp);

        for (PhoneNumber phone : emp.getPhoneNumbers()) {
            assertNoFetchGroup(phone);
            phone.getAreaCode();
        }

        assertEquals(3, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
    }

    @Test
    public void employeeNamesFetchGroup() throws Exception {
        EntityManager em = createEntityManager();

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
    }

    @Test
    public void joinFetchEmployeeAddressWithDynamicFetchGroup() {
        EntityManager em = createEntityManager();

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
        EntityManager em = createEntityManager();

        Query query = em.createQuery("SELECT e FROM Employee e JOIN FETCH e.address WHERE e.id IN (SELECT p.id FROM PhoneNumber p)");

        FetchGroup fetchGroup = new FetchGroup("names");
        fetchGroup.addAttribute("firstName");
        fetchGroup.addAttribute("lastName");
        query.setHint(QueryHints.FETCH_GROUP, fetchGroup);

        List<Employee> emps = query.getResultList();

        assertNotNull(emps);
    }

    @Test
    public void verifyUnfetchedAttributes() throws Exception {
        EntityManager em = createEntityManager();

        TypedQuery<Employee> q = em.createQuery("SELECT e FROM Employee e WHERE e.id IN (SELECT MIN(p.id) FROM PhoneNumber p)", Employee.class);
        FetchGroup fg = new FetchGroup("Employee.empty");
        q.setHint(QueryHints.FETCH_GROUP, fg);
        Employee emp = q.getSingleResult();

        assertNotNull(emp);
        assertEquals(1, getQuerySQLTracker(em).getTotalSQLSELECTCalls());

        // This check using the mapping returns a default (empty) IndirectList
/*        OneToManyMapping phoneMapping = (OneToManyMapping) employeeDescriptor.getMappingForAttributeName("phoneNumbers");
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
    }

    @Test
    public void verifyFetchedRelationshipAttributes() throws Exception {
        EntityManager em = createEntityManager();

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
    public void simpleSerializeAndMerge() throws Exception {
        EntityManager em = createEntityManager();
        int id = minEmployeeIdWithAddressAndPhones(em);
        HashMap<String, PhoneNumber> phonesOriginal = new HashMap();
        // save the original Employee for clean up
        Employee empOriginal = em.find(Employee.class, id);
        for(PhoneNumber phone : empOriginal.getPhoneNumbers()) {
            phonesOriginal.put(phone.getType(), phone);
        }
        closeEntityManager(em);
        clearCache();
        int newSalary = empOriginal.getSalary() * 2;
        if(newSalary == 0) {
            newSalary = 100;
        }
        
        em = createEntityManager();
        Query query = em.createQuery("SELECT e FROM Employee e WHERE e.id = "+id);        
        FetchGroup fetchGroup = new FetchGroup("names");
        fetchGroup.addAttribute("firstName");
        fetchGroup.addAttribute("lastName");
        fetchGroup.addAttribute("address.country");
        fetchGroup.addAttribute("phoneNumbers.areaCode");
        fetchGroup.setShouldLoad(true);
        query.setHint(QueryHints.FETCH_GROUP, fetchGroup);
        Employee emp = (Employee)query.getSingleResult();
        
        Employee empSerialized;
        Employee empDeserialized;
        Employee empMerged;
        beginTransaction(em);
        try {
            empSerialized = serialize(emp);
            empSerialized.setFirstName("newFirstName");
            empSerialized.setLastName("newLastName");
            empSerialized.setSalary(newSalary);
            empSerialized.getAddress().setCountry("newCountry");
            empSerialized.getAddress().setCity("newCity");
            for(PhoneNumber phone : empSerialized.getPhoneNumbers()) {
                phone.setAreaCode("000");
                phone.setNumber("0000000");
            }
            empDeserialized = serialize(empSerialized);
            empMerged = em.merge(empDeserialized);
    
            // verify merged in em
            assertEquals("newFirstName", empMerged.getFirstName());
            assertEquals("newLastName", empMerged.getLastName());
            assertEquals(newSalary, empMerged.getSalary());
            assertEquals("newCountry", empMerged.getAddress().getCountry());
            assertEquals("newCity", empMerged.getAddress().getCity());
            for(PhoneNumber phone : empSerialized.getPhoneNumbers()) {
                assertEquals("000", phone.getAreaCode());
                assertEquals("0000000", phone.getNumber());
            }
            commitTransaction(em);                
        } finally {
            if(isTransactionActive(em)) {
               rollbackTransaction(em); 
            }
        }
        
        // verify merged in the shared cache - clear em cache, query using cache only.
        em.clear();
        HashMap hints = new HashMap(2);
        hints.put(QueryHints.CACHE_USAGE, CacheUsage.CheckCacheOnly);
//        hints.put(QueryHints.FETCH_GROUP, fetchGroup);
        Employee empShared = em.find(Employee.class, id, hints);
        assertEquals("newFirstName", empShared.getFirstName());
        assertEquals("newLastName", empShared.getLastName());
        assertEquals(newSalary, empShared.getSalary());
        assertEquals("newCountry", empShared.getAddress().getCountry());
        assertEquals("newCity", empShared.getAddress().getCity());
        for(PhoneNumber phone : empShared.getPhoneNumbers()) {
            assertEquals("000", phone.getAreaCode());
            assertEquals("0000000", phone.getNumber());
        }
    
        // verify merged in shared the db - clear both em and shared caches.
        // Must read through the old EntityManager - the changes haven't been committed in the db.
        clearCache();
        em.clear();
        Employee empDb = em.find(Employee.class, id);
        assertEquals("newFirstName", empDb.getFirstName());
        assertEquals("newLastName", empDb.getLastName());
        assertEquals(newSalary, empDb.getSalary());
        assertEquals("newCountry", empDb.getAddress().getCountry());
        assertEquals("newCity", empDb.getAddress().getCity());
        for(PhoneNumber phone : empDb.getPhoneNumbers()) {
            assertEquals("000", phone.getAreaCode());
            assertEquals("0000000", phone.getNumber());
        }
    
        beginTransaction(em);
        try {
            empDb.setFirstName(empOriginal.getFirstName());
            empDb.setLastName(empOriginal.getLastName());
            empDb.setSalary(empOriginal.getSalary());
            empDb.getAddress().setCountry(empOriginal.getAddress().getCountry());
            empDb.getAddress().setCity(empOriginal.getAddress().getCity());
            for(PhoneNumber phone : empDb.getPhoneNumbers()) {
                PhoneNumber phoneOriginal = phonesOriginal.get(phone.getType());
                phone.setAreaCode(phoneOriginal.getAreaCode());
                phone.setNumber(phoneOriginal.getNumber());
            }
            commitTransaction(em);
        } finally {
            if(isTransactionActive(em)) {
                rollbackTransaction(em); 
             }
            closeEntityManager(em);
        }
    }
/*    public void simpleSerializeAndMerge() throws Exception {
        EntityManager em = createEntityManager();
        int id = minimumEmployeeId(em);
        // save the original Employee for clean up
        Employee empOriginal = em.find(Employee.class, id);
        closeEntityManager(em);
        clearCache();
        int newSalary = empOriginal.getSalary() * 2;
        if(newSalary == 0) {
            newSalary = 100;
        }
        
        em = createEntityManager();
        Query query = em.createQuery("SELECT e FROM Employee e WHERE e.id = "+id);        
        FetchGroup fetchGroup = new FetchGroup("names");
        fetchGroup.addAttribute("firstName");
        fetchGroup.addAttribute("lastName");
        query.setHint(QueryHints.FETCH_GROUP, fetchGroup);
        Employee emp = (Employee)query.getSingleResult();
        
        Employee empSerialized;
        Employee empDeserialized;
        Employee empMerged;
        beginTransaction(em);
        try {
            empSerialized = serialize(emp);
            empSerialized.setFirstName("newFirstName");
            empSerialized.setLastName("newLastName");
            empSerialized.setSalary(newSalary);
            empDeserialized = serialize(empSerialized);
            empMerged = em.merge(empDeserialized);

            // verify merged in em
            assertEquals("newFirstName", empMerged.getFirstName());
            assertEquals("newLastName", empMerged.getLastName());
            assertEquals(newSalary, empMerged.getSalary());
            commitTransaction(em);                
        } finally {
            if(isTransactionActive(em)) {
               rollbackTransaction(em); 
            }
        }
        
        // verify merged in the shared cache - clear em cache, query using cache only.
        em.clear();
        HashMap hints = new HashMap(2);
        hints.put(QueryHints.CACHE_USAGE, CacheUsage.CheckCacheOnly);
        hints.put(QueryHints.FETCH_GROUP, fetchGroup);
        Employee empShared = em.find(Employee.class, id, hints);
        assertEquals("newFirstName", empShared.getFirstName());
        assertEquals("newLastName", empShared.getLastName());
        assertEquals(newSalary, empShared.getSalary());

        // verify merged in shared the db - clear both em and shared caches.
        // Must read through the old EntityManager - the changes haven't been committed in the db.
        clearCache();
        em.clear();
        Employee empDb = em.find(Employee.class, id);
        assertEquals("newFirstName", empDb.getFirstName());
        assertEquals("newLastName", empDb.getLastName());
        assertEquals(newSalary, empDb.getSalary());

        beginTransaction(em);
        try {
            empDb.setFirstName(empOriginal.getFirstName());
            empDb.setLastName(empOriginal.getLastName());
            empDb.setSalary(empOriginal.getSalary());
            commitTransaction(em);
        } finally {
            if(isTransactionActive(em)) {
                rollbackTransaction(em); 
             }
            closeEntityManager(em);
        }
    }*/
    
    private <T> T serialize(Serializable entity) throws IOException, ClassNotFoundException {
        byte[] bytes = SerializationHelper.serialize(entity);
        return (T) SerializationHelper.deserialize(bytes);
    }
}
