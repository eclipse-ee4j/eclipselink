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
package org.eclipse.persistence.testing.tests.jpa.fieldaccess.fetchgroups;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
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
import org.eclipse.persistence.internal.helper.IdentityHashSet;
import org.eclipse.persistence.internal.helper.SerializationHelper;
import org.eclipse.persistence.internal.queries.AttributeItem;
import org.eclipse.persistence.internal.queries.EntityFetchGroup;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.queries.AttributeGroup;
import org.eclipse.persistence.queries.FetchGroup;
import org.eclipse.persistence.sessions.CopyGroup;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.PhoneNumber;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.Project;

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
        suite.addTest(new SimpleSerializeFetchGroupTests("partialMerge"));
        suite.addTest(new SimpleSerializeFetchGroupTests("copyGroupMerge"));
        suite.addTest(new SimpleSerializeFetchGroupTests("copyGroupMerge2"));
        suite.addTest(new SimpleSerializeFetchGroupTests("copyWithPk"));
        suite.addTest(new SimpleSerializeFetchGroupTests("copyWithPkUseFullGroup"));
        suite.addTest(new SimpleSerializeFetchGroupTests("copyWithoutPk"));
        suite.addTest(new SimpleSerializeFetchGroupTests("copyWithoutPkUseFullGroup"));
        suite.addTest(new SimpleSerializeFetchGroupTests("copyNoCascade"));
        suite.addTest(new SimpleSerializeFetchGroupTests("copyCascadePrivateParts"));
        suite.addTest(new SimpleSerializeFetchGroupTests("copyCascadeAllParts"));
        
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
        EntityManager em = createEntityManager("fieldaccess");
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
        EntityManager em = createEntityManager("fieldaccess");
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
        EntityManager em = createEntityManager("fieldaccess");

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
        EntityManager em = createEntityManager("fieldaccess");

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
        EntityManager em = createEntityManager("fieldaccess");

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
        EntityManager em = createEntityManager("fieldaccess");

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
        EntityManager em = createEntityManager("fieldaccess");

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
        EntityManager em = createEntityManager("fieldaccess");

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

        TypedQuery<Employee> q = em.createQuery("SELECT e FROM Employee e WHERE e.id IN (SELECT MIN(p.owner.id) FROM PhoneNumber p)", Employee.class);
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
    public void simpleSerializeAndMerge() throws Exception {
        EntityManager em = createEntityManager("fieldaccess");
        int id = minEmployeeIdWithAddressAndPhones(em);
        HashMap<String, PhoneNumber> phonesOriginal = new HashMap();
        // save the original Employee for clean up
        Employee empOriginal = em.find(Employee.class, id);
        for(PhoneNumber phone : empOriginal.getPhoneNumbers()) {
            phonesOriginal.put(phone.getType(), phone);
        }
        closeEntityManager(em);
        clearCache("fieldaccess");
        int newSalary = empOriginal.getSalary() * 2;
        if(newSalary == 0) {
            newSalary = 100;
        }
        
        em = createEntityManager("fieldaccess");
        Query query = em.createQuery("SELECT e FROM Employee e WHERE e.id = "+id);        
        FetchGroup fetchGroup = new FetchGroup("names");
        fetchGroup.addAttribute("firstName");
        fetchGroup.addAttribute("lastName");
        fetchGroup.addAttribute("address.country");
        fetchGroup.addAttribute("phoneNumbers.areaCode");
        fetchGroup.addAttribute("phoneNumbers.owner.id");
        fetchGroup.setShouldLoad(true);
        query.setHint(QueryHints.FETCH_GROUP, fetchGroup);
        Employee emp = (Employee)query.getSingleResult();
        
        Employee empSerialized;
        Employee empDeserialized;
        Employee empMerged;
        beginTransaction(em);
        try {
            empSerialized = serialize(emp);
            
            assertFetched(empSerialized, fetchGroup);
            empSerialized.setFirstName("newFirstName");
            empSerialized.setLastName("newLastName");
            
            // salary is not in the original fetchGroup
            empSerialized.setSalary(newSalary);
            FetchGroup extendedFetchGroup = (FetchGroup)fetchGroup.clone();
            extendedFetchGroup.addAttribute("salary");
            assertFetched(empSerialized, extendedFetchGroup);
            
            empSerialized.getAddress().setCountry("newCountry");
            assertFetched(empSerialized.getAddress(), fetchGroup.getGroup("address"));
            // address.city is not in the original fetchGroup
            empSerialized.getAddress().setCity("newCity");
            extendedFetchGroup.addAttribute("address.city");
            assertFetched(empSerialized.getAddress(), extendedFetchGroup.getGroup("address"));
            
            // phoneNumbers.number is not in the original fetchGroup
            extendedFetchGroup.addAttribute("phoneNumbers.number");            
            for(PhoneNumber phone : empSerialized.getPhoneNumbers()) {
                phone.setAreaCode("000");
                assertFetched(phone, fetchGroup.getGroup("phoneNumbers"));
                // phoneNumbers.number is not in the original fetchGroup
                phone.setNumber("0000000");
                assertFetched(phone, extendedFetchGroup.getGroup("phoneNumbers"));
            }

            empDeserialized = serialize(empSerialized);
            assertFetched(empDeserialized, extendedFetchGroup);
            assertFetched(empDeserialized.getAddress(), extendedFetchGroup.getGroup("address"));
            for(PhoneNumber phone : empDeserialized.getPhoneNumbers()) {
                assertFetched(phone, extendedFetchGroup.getGroup("phoneNumbers"));
            }

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
            
            // verify that the attributes outside of the fetch group not nullified.
            assertEquals(empOriginal.getGender(), empMerged.getGender());
            if(empOriginal.getDepartment() != null) {
                assertEquals(empOriginal.getDepartment().getId(), empMerged.getDepartment().getId());
            }
            if(empOriginal.getPeriod() != null) {
                assertEquals(empOriginal.getPeriod().getStartDate(), empMerged.getPeriod().getStartDate());
                assertEquals(empOriginal.getPeriod().getEndDate(), empMerged.getPeriod().getEndDate());
            }
            assertEquals(empOriginal.getPayScale(), empMerged.getPayScale());
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
    
        // clean up
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
        EntityManager em = createEntityManager("fieldaccess");
        int id = minimumEmployeeId(em);
        // save the original Employee for clean up
        Employee empOriginal = em.find(Employee.class, id);
        closeEntityManager(em);
        clearCache();
        int newSalary = empOriginal.getSalary() * 2;
        if(newSalary == 0) {
            newSalary = 100;
        }
        
        em = createEntityManager("fieldaccess");
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
    
    public void partialMerge() throws Exception {
        EntityManager em = createEntityManager("fieldaccess");
        // Search for an Employee with an Address and Phone Numbers 
        TypedQuery<Employee> query = em.createQuery("SELECT e FROM Employee e WHERE e.address IS NOT NULL AND e.id IN (SELECT MIN(p.owner.id) FROM PhoneNumber p)", Employee.class);
        
        // Load only its names and phone Numbers
        FetchGroup fetchGroup = new FetchGroup();
        fetchGroup.addAttribute("firstName");
        fetchGroup.addAttribute("lastName");
        FetchGroup phonesFG = phoneDescriptor.getFetchGroupManager().createFullFetchGroup();
        // that ensures the owner is not instantiated
        phonesFG.addAttribute("owner.id");
        phonesFG.removeAttribute("status");
        phonesFG.setShouldLoad(true);
        fetchGroup.addAttribute("phoneNumbers", phonesFG);
        
        // Make sure the FetchGroup also forces the relationships to be loaded
        fetchGroup.setShouldLoad(true);
        query.setHint(QueryHints.FETCH_GROUP, fetchGroup);
        
        Employee emp = query.getSingleResult();
        
        // Detach Employee through Serialization
        Employee detachedEmp = (Employee) SerializationHelper.clone(emp);
        // Modify the detached Employee inverting the names, adding a phone number, and setting the salary
        detachedEmp.setFirstName(emp.getLastName());
        detachedEmp.setLastName(emp.getFirstName());
        detachedEmp.addPhoneNumber(new PhoneNumber("TEST", "999", "999999"));
        // NOte that salary was not part of the original FetchGroupdetachedEmp.setSalary(1);
        detachedEmp.setSalary(1);
        
        beginTransaction(em);
        // Merge the detached employee
        em.merge(detachedEmp);
        // Flush the changes to the database
        em.flush();
        rollbackTransaction(em);
    }

    public void copyGroupMerge() {
        // Search for an Employee with an Address and Phone Numbers
        EntityManager em = createEntityManager("fieldaccess");
        TypedQuery<Employee> query = em.createQuery("SELECT e FROM Employee e WHERE e.id IN (SELECT MIN(ee.id) FROM Employee ee)", Employee.class);
        Employee emp = query.getSingleResult();
        assertEquals(1, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
        System.out.println(">>> Employee retrieved");
        
        // Copy only its names and phone Numbers
        AttributeGroup group = new CopyGroup();
        group.addAttribute("firstName");
        group.addAttribute("lastName");
        group.addAttribute("address");
        
        Employee empCopy = (Employee) em.unwrap(JpaEntityManager.class).copy(emp, group);
        assertEquals(2, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
        System.out.println(">>> Employee copied");
        
        // Modify the detached Employee inverting the names, adding a phone number, and setting the salary
        empCopy.setFirstName(emp.getLastName());
        empCopy.setLastName(emp.getFirstName());
        
        // Note that salary was not part of the original FetchGroup
        //empCopy.setSalary(1);
        
        beginTransaction(em);
        // Merge the detached employee
        em.merge(empCopy);
        System.out.println(">>> Sparse merge complete");
        
        // Flush the changes to the database
        em.flush();
        System.out.println(">>> Flush complete");
        
        rollbackTransaction(em);
    }
    
    public void copyGroupMerge2() {
        // Search for an Employee with an Address and Phone Numbers
        EntityManager em = createEntityManager("fieldaccess");
        TypedQuery<Employee> query = em.createQuery("SELECT e FROM Employee e", Employee.class);
        query.setHint(QueryHints.BATCH, "e.address");
        List<Employee> employees = query.getResultList();
        assertEquals(1, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
        System.out.println(">>> Employees retrieved");
        
        // Copy only its names and phone Numbers
        AttributeGroup group = new CopyGroup();
        group.addAttribute("firstName");
        group.addAttribute("lastName");
        group.addAttribute("address");
        
        List<Employee> employeesCopy = (List<Employee>) em.unwrap(JpaEntityManager.class).copy(employees, group);
        assertEquals(2, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
        System.out.println(">>> Employees copied");
        
        beginTransaction(em);
        for(Employee empCopy : employeesCopy) {
            // Modify the detached Employee inverting the names, adding a phone number, and setting the salary
            String firstName = empCopy.getFirstName();
            String lastName = empCopy.getLastName();
            empCopy.setFirstName(lastName);
            empCopy.setLastName(firstName);
    
            // Note that salary was not part of the original FetchGroup
            //empCopy.setSalary(1);        
            
            // Merge the detached employee
            em.merge(empCopy);
        }
        System.out.println(">>> Sparse merge complete");
        
        // Flush the changes to the database
        em.flush();
        System.out.println(">>> Flush complete");
        
        rollbackTransaction(em);
    }
    
    @Test
    public void copyWithPk() {
        copyWithOrWithoutPk(false, false);
    }

    @Test
    public void copyWithPkUseFullGroup() {
        copyWithOrWithoutPk(false, true);
    }

    @Test
    public void copyWithoutPk() {
        copyWithOrWithoutPk(true, false);
    }

    @Test
    public void copyWithoutPkUseFullGroup() {
        copyWithOrWithoutPk(true, true);
    }

    void copyWithOrWithoutPk(boolean noPk, boolean useFullGroup) {        
        CopyGroup group = new CopyGroup();
        if(noPk) {
            // setShouldResetPrimaryKey set to true means that:
            //  pk would not be copied - unless explicitly specified in the group;
            //  copy would not have a fetch group
            group.setShouldResetPrimaryKey(true);
            // setShouldResetVersion set to true means that
            // version would not be copied - unless explicitly specified in the group;
            group.setShouldResetVersion(true);
        }
        group.cascadeTree(); // Copy only the attributes specified
        // note that 
        // default value shouldResetPrimaryKey==false causes pk to copied, too;
        // default value shouldResetVersion==false causes version to be copied, too.
        group.addAttribute("firstName");
        group.addAttribute("lastName");
        group.addAttribute("gender");
        group.addAttribute("period");
        group.addAttribute("salary");

        if(useFullGroup) {
            // copy group contains all the attributes defined in Address class
            CopyGroup address = addressDescriptor.getFetchGroupManager().createFullFetchGroup().toCopyGroup();
            // attribute "employees" removed from the group
            address.removeAttribute("employees");
            if(noPk) {
                // pk attribute "ID" removed from the group - not that it's necessary to explicitly remove it:
                // setShouldResetPrimaryKey(true) would not remove explicitly specified in the group pk.
                address.removeAttribute("id");
                // note that default value shouldResetPrimaryKey==false would have resulted in copying the pk (that would be equivalent of adding the removed "ID" attribute back to the group).
                address.setShouldResetPrimaryKey(true);
                address.setShouldResetVersion(true);
            }
            group.addAttribute("address", address);
        
            // copy group contains all the attributes defined in PhoneNumber class
            CopyGroup phones = phoneDescriptor.getFetchGroupManager().createFullFetchGroup().toCopyGroup();
            if(noPk) {
                // the only goal of setting shouldResetPrimaryKey to true here is to avoid a FetchGroup being assigned to phone's copy.
                // Note that because both pk components ("owner" and "type") are part of the copy group they  still will be copied:
                // the phone's copy will have the same type as original and it's owner will be original's owner's copy.
                phones.setShouldResetPrimaryKey(true);
            }
            if(!noPk) {
                // to avoid instantiating the whole owner
                phones.addAttribute("owner.id");
            }
            group.addAttribute("phoneNumbers", phones);
        } else {
            // implicitly created sub CopyGroups address and phoneNumbers will have the same shouldReset flags values as their master CopyGroup.
            
            group.addAttribute("address.country");
            group.addAttribute("address.province");
            group.addAttribute("address.street");
            group.addAttribute("address.postalCode");
            group.addAttribute("address.city");

            if(noPk) {
                group.addAttribute("phoneNumbers.owner");
            } else {
                // to avoid instantiating the whole owner
                group.addAttribute("phoneNumbers.owner.id");
            }
            group.addAttribute("phoneNumbers.type");
            group.addAttribute("phoneNumbers.areaCode");
            group.addAttribute("phoneNumbers.number");
        }
        
        EntityManager em = createEntityManager("fieldaccess");
        Employee emp = minimumEmployee(em);        
        Employee empCopy = (Employee) em.unwrap(JpaEntityManager.class).copy(emp, group);
        
        beginTransaction(em);
        try {
            if(noPk) {
                assertNoFetchGroup(empCopy);
                assertNoFetchGroup(empCopy.getAddress());
                for(PhoneNumber phoneCopy : empCopy.getPhoneNumbers()) {
                    assertNoFetchGroup(phoneCopy);
                }
                // Persist the employee copy
                em.persist(empCopy);
            } else {
                FetchGroup fetchGroup = group.toFetchGroup();
                // the following call adds pk and version, verifies that all attribute names correct.
                employeeDescriptor.getFetchGroupManager().prepareAndVerify(fetchGroup);
                // copyEmp, its address and phones each should have an EntityFetchGroup corresponding to the respective copyGroup.
                assertFetched(empCopy, fetchGroup);
                
                EntityFetchGroup addressEntityFetchGroup = addressDescriptor.getFetchGroupManager().getEntityFetchGroup(fetchGroup.getGroup("address")); 
                if(addressEntityFetchGroup == null) { 
                    assertNoFetchGroup(empCopy.getAddress());
                } else {
                    assertFetched(empCopy.getAddress(), addressEntityFetchGroup);
                }

                EntityFetchGroup phonesEntityFetchGroup = phoneDescriptor.getFetchGroupManager().getEntityFetchGroup(fetchGroup.getGroup("phoneNumbers")); 
                for(PhoneNumber phoneCopy : empCopy.getPhoneNumbers()) {
                    if(phonesEntityFetchGroup == null) { 
                        assertNoFetchGroup(phoneCopy);
                    } else {
                        assertFetched(phoneCopy, phonesEntityFetchGroup);
                    }
                }

                // to cause updates let's change something:
                //   in Employee table
                empCopy.setFirstName((empCopy.getFirstName() != null ? empCopy.getFirstName() : "") + "_NEW");
                //   in Salary table
                empCopy.setSalary(empCopy.getSalary() * 2 + 1);
                //   in Address
                empCopy.getAddress().setCountry((empCopy.getAddress().getCountry() != null ? empCopy.getAddress().getCountry() : "") + "_NEW");
                //   in each Phone
                for(PhoneNumber phoneCopy : empCopy.getPhoneNumbers()) {
                    if(phoneCopy.getAreaCode() != null && phoneCopy.getAreaCode().equals("000")) {
                        phoneCopy.setAreaCode("111");
                    } else {
                        phoneCopy.setAreaCode("000");
                    }
                }
                em.merge(empCopy);
            }
    
            // Insert a new row into Employee, Salary, Address, and a row for each Phone
            int nExpectedInsertsOrUpdates = 3 + empCopy.getPhoneNumbers().size(); 
            int nExpectedInserts, nExpectedUpdates;
            if(noPk) {
                nExpectedInserts = nExpectedInsertsOrUpdates; 
                // table sequence might have been updated
                nExpectedUpdates = getQuerySQLTracker(em).getTotalSQLUPDATECalls();
            } else {
                nExpectedInserts = 0; 
                nExpectedUpdates = nExpectedInsertsOrUpdates;
            }
            
            // Flush the changes to the database
            em.flush();
            
            assertEquals(nExpectedInserts, getQuerySQLTracker(em).getTotalSQLINSERTCalls());
            assertEquals(nExpectedUpdates, getQuerySQLTracker(em).getTotalSQLUPDATECalls());
        } finally {
            if(isTransactionActive(em)) {
                rollbackTransaction(em);
            }
        }
    }

    @Test
    public void copyNoCascade() {
        copyCascade(CopyGroup.NO_CASCADE);
    }
    
    @Test
    public void copyCascadePrivateParts() {
        copyCascade(CopyGroup.CASCADE_PRIVATE_PARTS);
    }

    @Test
    public void copyCascadeAllParts() {
        copyCascade(CopyGroup.CASCADE_ALL_PARTS);
    }
    
    void copyCascade(int cascadeDepth) {
        EntityManager em = createEntityManager("fieldaccess");
        Query query = em.createQuery("SELECT e FROM Employee e");
        List<Employee> employees = query.getResultList();

        CopyGroup group = new CopyGroup();
        if(cascadeDepth == CopyGroup.NO_CASCADE) {
            group.dontCascade();
        } else if(cascadeDepth == CopyGroup.CASCADE_PRIVATE_PARTS) {
            // default cascade depth setting
            group.cascadePrivateParts();
        } else if(cascadeDepth == CopyGroup.CASCADE_ALL_PARTS) {
            group.cascadeAllParts();
        } else {
            fail("Invalid cascadeDepth = " + cascadeDepth);
        }
        group.setShouldResetPrimaryKey(true);
        
        List<Employee> employeesCopy;
        if(cascadeDepth == CopyGroup.NO_CASCADE || cascadeDepth == CopyGroup.CASCADE_PRIVATE_PARTS) {
            // In this case the objects should be copied one by one - each one using a new CopyGroup.
            // That ensures most referenced object are original ones (not copies) -
            // the only exception is privately owned objects in CASCADE_PRIVATE_PARTS case.
            employeesCopy = new ArrayList(employees.size());
            for(Employee emp : employees) {
                CopyGroup groupClone = group.clone();
                employeesCopy.add((Employee)em.unwrap(JpaEntityManager.class).copy(emp, groupClone));
            }
        } else {
            // cascadeDepth == CopyGroup.CASCADE_ALL_PARTS
            // In this case all objects should be copied using a single CopyGroup.
            // That ensures identities of the copies:
            // for instance if several employees referenced the same project,
            // then all copies of these employees will reference the single copy of the project. 
            employeesCopy = (List)em.unwrap(JpaEntityManager.class).copy(employees, group);
        }
        
        // IdentityHashSets will be used to verify copy identities
        IdentityHashSet originalEmployees = new IdentityHashSet();
        IdentityHashSet copyEmployees = new IdentityHashSet();
        IdentityHashSet originalAddresses = new IdentityHashSet();
        IdentityHashSet copyAddresses = new IdentityHashSet();
        IdentityHashSet originalProjects = new IdentityHashSet();
        IdentityHashSet copyProjects = new IdentityHashSet();
        IdentityHashSet originalPhones = new IdentityHashSet();
        IdentityHashSet copyPhones = new IdentityHashSet();
        
        int size = employees.size();
        for(int i=0; i < size; i++) {
            Employee emp = employees.get(i);
            Employee empCopy = employeesCopy.get(i);
            if(cascadeDepth == CopyGroup.CASCADE_ALL_PARTS) {
                originalEmployees.add(emp);
                copyEmployees.add(empCopy);
            } else {
                // cascadeDepth == CopyGroup.NO_CASCADE || cascadeDepth == CopyGroup.CASCADE_PRIVATE_PARTS
                // In this case all Employees referenced by empCopyes are originals (manager and managed employees).
                // Therefore if we add here each emp and empCopy to originalEmployees and copyEmployees respectively
                // then copyEmployees will always contain all original managers and managed + plus all copies.
                // Therefore in this case originalEmployees and copyEmployees will contain only references (managers and managed employees).
            }

            if(emp.getAddress() == null) {
                assertTrue("emp.getAddress() == null, but empCopy.getAddress() != null", empCopy.getAddress() == null);
            } else {
                originalAddresses.add(emp.getAddress());
                copyAddresses.add(empCopy.getAddress());
                if(cascadeDepth == CopyGroup.NO_CASCADE || cascadeDepth == CopyGroup.CASCADE_PRIVATE_PARTS) {
                    assertTrue("address has been copied", emp.getAddress() == empCopy.getAddress());
                } else {
                    // cascadeDepth == CopyGroup.CASCADE_ALL_PARTS
                    assertFalse("address has not been copied", emp.getAddress() == empCopy.getAddress());
                }
            }

            boolean same;
            for(Project project : emp.getProjects()) {
                originalProjects.add(project);
                same = false;
                for(Project projectCopy : empCopy.getProjects()) {
                    copyProjects.add(projectCopy);
                    if(!same && project == projectCopy) {
                        same = true;
                    }
                }
                if(cascadeDepth == CopyGroup.NO_CASCADE || cascadeDepth == CopyGroup.CASCADE_PRIVATE_PARTS) {
                    assertTrue("project has been copied", same);
                } else {
                    // cascadeDepth == CopyGroup.CASCADE_ALL_PARTS
                    assertFalse("project has not been copied", same);
                }
            }
            
            for(Employee managedEmp : emp.getManagedEmployees()) {
                originalEmployees.add(managedEmp);
                same = false;
                for(Employee managedEmpCopy : empCopy.getManagedEmployees()) {
                    copyEmployees.add(managedEmpCopy);
                    if(!same && managedEmp == managedEmpCopy) {
                        same = true;
                    }
                }
                if(cascadeDepth == CopyGroup.NO_CASCADE || cascadeDepth == CopyGroup.CASCADE_PRIVATE_PARTS) {
                    assertTrue("managedEmployee has been copied", same);
                } else {
                    // cascadeDepth == CopyGroup.CASCADE_ALL_PARTS
                    assertFalse("managedEmployee has not been copied", same);
                }
            }
            
            if(emp.getManager() == null) {
                assertTrue("emp.getManager() == null, but empCopy.getManager() != null", empCopy.getManager() == null);
            } else {
                originalEmployees.add(emp.getManager());
                copyEmployees.add(empCopy.getManager());
                if(cascadeDepth == CopyGroup.NO_CASCADE || cascadeDepth == CopyGroup.CASCADE_PRIVATE_PARTS) {
                    assertTrue("manager has been copied", emp.getManager() == empCopy.getManager());
                } else {
                    // cascadeDepth == CopyGroup.CASCADE_ALL_PARTS
                    assertFalse("manager has not been copied", emp.getManager() == empCopy.getManager());
                }
            }

            // phoneNumbers is privately owned
            for(PhoneNumber phone : emp.getPhoneNumbers()) {
                originalPhones.add(phone);
                same = false;
                for(PhoneNumber phoneCopy : empCopy.getPhoneNumbers()) {
                    copyPhones.add(phoneCopy);
                    if(!same && phone == phoneCopy) {
                        same = true;
                    }
                }
                if(cascadeDepth == CopyGroup.NO_CASCADE) {
                    assertTrue("phone has been copied", same);
                } else {
                    // cascadeDepth == CopyGroup.CASCADE_ALL_PARTS || cascadeDepth == CopyGroup.CASCADE_PRIVATE_PARTS
                    assertFalse("phone has not been copied", same);
                }
            }
        }
        
        assertTrue("copyEmployees.size() == " + copyEmployees.size() + "; was expected " + originalEmployees.size(), originalEmployees.size() == copyEmployees.size());
        assertTrue("copyAddresses.size() == " + copyAddresses.size() + "; was expected " + originalAddresses.size(), originalAddresses.size() == copyAddresses.size());
        assertTrue("copyProjects.size() == " + copyProjects.size() + "; was expected " + originalProjects.size(), originalProjects.size() == copyProjects.size());
        assertTrue("copyPhones.size() == " + copyPhones.size() + "; was expected " + originalPhones.size(), originalPhones.size() == copyPhones.size());
    }
    
    private <T> T serialize(Serializable entity) throws IOException, ClassNotFoundException {
        byte[] bytes = SerializationHelper.serialize(entity);
        return (T) SerializationHelper.deserialize(bytes);
    }
}
