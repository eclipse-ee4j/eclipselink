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
package org.eclipse.persistence.testing.tests.jpa.fetchgroups;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.eclipse.persistence.config.DescriptorCustomizer;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.FetchGroupManager;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.queries.FetchGroup;
import org.eclipse.persistence.queries.FetchGroupTracker;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.server.ServerSession;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.advanced.EmployeePopulator;
import org.eclipse.persistence.testing.models.jpa.advanced.EquipmentCode;
import org.eclipse.persistence.testing.tests.jpa.dynamic.QuerySQLTracker;

import org.junit.Test;

/**
 * Simple set of tests that verify the {@link FetchGroup} API. Need to verify
 * that the nesting and default behaves as expected.
 * 
 * @author dclarke
 * @since EclipseLink 2.1
 */
public abstract class BaseFetchGroupTests extends JUnitTestCase {
    
    ClassDescriptor employeeDescriptor;
    boolean employeeDescriptorIsIsolatedOriginal;
    ClassDescriptor phoneDescriptor;
    ClassDescriptor addressDescriptor;
    int sessionLogLevelOriginal;

    public static FetchGroup defaultEmployeeFG;
    public static FetchGroup defaultPhoneFG;

    public BaseFetchGroupTests() {
        super();
    }

    public BaseFetchGroupTests(String name) {
        super(name);
    }

    /*
     * Fetch Group tests require weaving.
     */
    public void runBare() throws Throwable {
        if (this.shouldRunTestOnServer()) {
            super.runBare();
        } else {
           if (isWeavingEnabled()) {
                super.runBare();
            }
        }
    }

    /**
     * Any FetchGroups setup in test cases are removed.
     * Descriptors should not be isolated.
     * Clear cache, install QuerySQLTracker. 
     */
    public void setUp() {
        Session session = getServerSession(); 
        
        employeeDescriptor = getDescriptor("Employee");
        phoneDescriptor = getDescriptor("PhoneNumber");
        addressDescriptor = getDescriptor("Address");

        // this causes recreation of the cache removing the previously cached queries
        session.getProject().setJPQLParseCacheMaxSize(session.getProject().getJPQLParseCacheMaxSize());
        
        clearFetchGroups(employeeDescriptor);
        clearFetchGroups(phoneDescriptor);
        clearFetchGroups(addressDescriptor);
        // reprepare read queries after all fetch groups are cleared for all descriptors
        reprepareReadQueries(employeeDescriptor);
        reprepareReadQueries(phoneDescriptor);
        reprepareReadQueries(addressDescriptor);

        assertConfig(employeeDescriptor, null, 0);
        assertConfig(addressDescriptor, null, 0);
        assertConfig(phoneDescriptor, null, 0);

        employeeDescriptorIsIsolatedOriginal = employeeDescriptor.isIsolated();
        if(employeeDescriptorIsIsolatedOriginal) {
            employeeDescriptor.setIsIsolated(false);
        }
        
        clearCache();
        QuerySQLTracker.install(session);
        sessionLogLevelOriginal = session.getLogLevel();
        if(sessionLogLevelOriginal > SessionLog.FINE) {
            session.setLogLevel(SessionLog.FINE);
        }
    }
    
    /**
     * Any FetchGroups setup in test cases are removed.
     * Reset isolated flag on Employee descriptor.
     * Clear cache, uninstall QuerySQLTracker.  
     */
    public void tearDown() {
        Session session = getServerSession(); 
        
        // this causes recreation of the cache removing the previously cached queries
        session.getProject().setJPQLParseCacheMaxSize(session.getProject().getJPQLParseCacheMaxSize());
        
        clearFetchGroups(employeeDescriptor);
        clearFetchGroups(phoneDescriptor);
        clearFetchGroups(addressDescriptor);
        // reprepare read queries after all fetch groups are cleared for all descriptors
        clearReadQueries(employeeDescriptor);
        clearReadQueries(phoneDescriptor);
        clearReadQueries(addressDescriptor);
        if(employeeDescriptorIsIsolatedOriginal) {
            employeeDescriptor.setIsIsolated(true);
        }
        
        clearCache();        
        if(sessionLogLevelOriginal != session.getLogLevel()) {
            session.setLogLevel(sessionLogLevelOriginal);
        }
        QuerySQLTracker.uninstall(session);
    }
    
    void clearFetchGroups(ClassDescriptor descriptor) {
        FetchGroupManager manager = descriptor.getFetchGroupManager();
        if(manager != null) {
            if(manager.getDefaultFetchGroup() != null) {
                manager.setDefaultFetchGroup(null);
            }
            if(manager.getFetchGroups().size() > 0) {
                manager.getFetchGroups().clear();
            }
        }
    }
    
    void reprepareReadQueries(ClassDescriptor descriptor) {
        reprepareReadQueriesInternal(descriptor, false);
    }
    void clearReadQueries(ClassDescriptor descriptor) {
        reprepareReadQueriesInternal(descriptor, true);
    }
    private void reprepareReadQueriesInternal(ClassDescriptor descriptor, boolean shouldClear) {
        ObjectLevelReadQuery olrQuery;
        AbstractSession session = getServerSession();
        if(descriptor.getQueryManager().hasReadObjectQuery()) {
            // this un-prePrepares the query, causes executionFetchGroup to be rebuilt
            olrQuery = descriptor.getQueryManager().getReadObjectQuery();
            if(shouldClear) {
                olrQuery.setFetchGroupName(null);
                olrQuery.setShouldUseDefaultFetchGroup(true);
            } else {
                olrQuery.setShouldUseDefaultFetchGroup(olrQuery.shouldUseDefaultFetchGroup());
            }
            descriptor.getQueryManager().getReadObjectQuery().checkPrepare(session, null);
        }
        if(descriptor.getQueryManager().hasReadAllQuery()) {
            // this un-prePrepares the query, causes executionFetchGroup to be rebuilt
            olrQuery = descriptor.getQueryManager().getReadAllQuery(); 
            if(shouldClear) {
                olrQuery.setFetchGroupName(null);
                olrQuery.setShouldUseDefaultFetchGroup(true);
            } else {
                olrQuery.setShouldUseDefaultFetchGroup(olrQuery.shouldUseDefaultFetchGroup());
            }
            olrQuery.checkPrepare(session, null);
        }
        // this causes recreation of the cache removing the previously cached queries
        descriptor.getQueryManager().setExpressionQueryCacheMaxSize(descriptor.getQueryManager().getExpressionQueryCacheMaxSize());
        for(DatabaseMapping mapping : descriptor.getMappings()) {
            if(mapping.isForeignReferenceMapping()) {
                if(((ForeignReferenceMapping)mapping).getSelectionQuery().isObjectLevelReadQuery()) {                    
                    olrQuery = (ObjectLevelReadQuery)((ForeignReferenceMapping)mapping).getSelectionQuery();
                    // this un-prePrepares the query, causes executionFetchGroup to be rebuilt 
                    if(shouldClear) {
                        olrQuery.setFetchGroupName(null);
                        olrQuery.setShouldUseDefaultFetchGroup(true);
                    } else {
                        olrQuery.setShouldUseDefaultFetchGroup(olrQuery.shouldUseDefaultFetchGroup());
                    }
                    olrQuery.checkPrepare(session, null);
                }
            }
        }
    }
    
    @Test
    public void testSetup() {
        ServerSession session = getServerSession();
        new AdvancedTableCreator().replaceTables(session);

        // Force uppercase for Postgres.
        if (session.getPlatform().isPostgreSQL()) {
            session.getLogin().setShouldForceFieldNamesToUpperCase(true);
        }

        // The EquipmentCode class 'should' be set to read only. We want 
        // to be able to create a couple in the Employee populator, so 
         // force the read only to false. If EquipmentCode is not 
        // actually read only, don't worry, we set the original read
        // only value back on the descriptor and the error will be 
        // caught in a later test in this suite.
        ClassDescriptor descriptor = session.getDescriptor(EquipmentCode.class);
        boolean shouldBeReadOnly = descriptor.shouldBeReadOnly();
        descriptor.setShouldBeReadOnly(false);
        
        // Populate the database with our examples.
        EmployeePopulator employeePopulator = new EmployeePopulator();         
        employeePopulator.buildExamples();
        employeePopulator.persistExample(session);
        
        descriptor.setShouldBeReadOnly(shouldBeReadOnly);

        clearCache();
    }
       
    public void managerFetchGroup() throws Exception {
        EntityManager em = createEntityManager();

        // Use q query since find will only use default fetch group
        Query query = em.createQuery("SELECT e FROM Employee e WHERE e.id = :ID");
        query.setParameter("ID", minimumEmployeeId(em));

        FetchGroup managerFG = new FetchGroup();
        managerFG.addAttribute("manager");
        query.setHint(QueryHints.FETCH_GROUP, managerFG);

        assertNotNull(getFetchGroup(query));
        assertSame(managerFG, getFetchGroup(query));

        Employee emp = (Employee) query.getSingleResult();

        assertEquals(3, getQuerySQLTracker(em).getTotalSQLSELECTCalls());

        assertFetchedAttribute(emp, "id");
        assertNotFetchedAttribute(emp, "firstName");
        assertFetchedAttribute(emp, "version");
        assertFetchedAttribute(emp, "manager");
        assertFetchedAttribute(emp, "address");
        assertFetchedAttribute(emp, "phoneNumbers");
        assertFetchedAttribute(emp, "projects");

        emp.getManager();
        assertEquals(3, getQuerySQLTracker(em).getTotalSQLSELECTCalls());

        emp.getLastName();

        assertEquals(3, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
    }

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

        assertFetchedAttribute(emp, "id");
        assertFetchedAttribute(emp, "firstName");
        assertFetchedAttribute(emp, "lastName");
        assertFetchedAttribute(emp, "gender");
        assertFetchedAttribute(emp, "salary");
        assertFetchedAttribute(emp, "version");
        assertFetchedAttribute(emp, "manager");
        assertFetchedAttribute(emp, "address");
        assertFetchedAttribute(emp, "phoneNumbers");
        assertFetchedAttribute(emp, "projects");

        emp.getId();
        emp.getFirstName();
        emp.getLastName();
        emp.getVersion();

        assertEquals(2, getQuerySQLTracker(em).getTotalSQLSELECTCalls());

        emp.getGender();
        assertEquals(3, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
        assertFetchedAttribute(emp, "gender");
        assertFetchedAttribute(emp, "salary");

        emp.getSalary();

        assertEquals(3, getQuerySQLTracker(em).getTotalSQLSELECTCalls());

        emp.getManager();

        assertEquals(4, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
        assertFetchedAttribute(emp, "manager");
    }

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

    protected Employee findMinimumEmployee(EntityManager em) {
        List<Employee> emps = em.createQuery("SELECT e FROM Employee e WHERE e.id in (SELECT MIN(ee.id) FROM Employee ee)").getResultList();

        assertNotNull("Null returned for min employee query", emps);
        assertEquals("No results returned for Mmin employee query", 1, emps.size());

        return emps.get(0);
    }

    public FetchGroup assertHasFetchGroup(Object entity) {
        assertNotNull("Entity is null", entity);
        assertTrue("Entity does not implement FetchGroupTracker", entity instanceof FetchGroupTracker);
        assertNotNull("Entity does not have FetchGroup", ((FetchGroupTracker) entity)._persistence_getFetchGroup());

        return ((FetchGroupTracker) entity)._persistence_getFetchGroup();
    }

    protected FetchGroup getFetchGroup(Object object) {
        assertNotNull("Cannot get a FetchGroup from null", object);

        if (object instanceof Query) {
            return getFetchGroup((Query) object);
        }
        if (object instanceof ObjectLevelReadQuery) {
            return getExecutionFetchGroup((ObjectLevelReadQuery) object);
        }
        assertTrue("Entity " + object + " does not implement FetchGroupTracker", object instanceof FetchGroupTracker);

        FetchGroupTracker tracker = (FetchGroupTracker) object;
        return tracker._persistence_getFetchGroup();
    }

    protected FetchGroup getFetchGroup(Query query) {
        return getFetchGroup((ObjectLevelReadQuery)JpaHelper.getDatabaseQuery(query));
    }

    protected FetchGroup getFetchGroup(ObjectLevelReadQuery readQuery) {
        return readQuery.getFetchGroup();
    }

    protected FetchGroup getExecutionFetchGroup(Query query) {
        return getExecutionFetchGroup((ObjectLevelReadQuery)JpaHelper.getDatabaseQuery(query));
    }

    protected FetchGroup getExecutionFetchGroup(ObjectLevelReadQuery readQuery) {
        return readQuery.getExecutionFetchGroup();
    }

    public void assertFetchedAttribute(Object entity, String... attribute) {
        FetchGroupAssert.assertFetchedAttribute(getEntityManagerFactory(), entity, attribute);
    }

    public void assertNotFetchedAttribute(Object entity, String... attribute) {
        FetchGroupAssert.assertNotFetchedAttribute(getEntityManagerFactory(), entity, attribute);
    }

    public void assertFetched(Object entity, FetchGroup fetchGroup) {
        FetchGroupAssert.assertFetched(getEntityManagerFactory(), entity, fetchGroup);
    }
    
    public void assertDefaultFetched(Object entity) {
        FetchGroupAssert.assertDefaultFetched(getEntityManagerFactory(), entity);
    }
    
    public void assertFetched(Object entity, String fetchGroupName) {
        FetchGroupAssert.assertFetched(getEntityManagerFactory(), entity, fetchGroupName);    
    }
    
    public void assertNoFetchGroup(Object entity) {
        FetchGroupAssert.assertNoFetchGroup(getEntityManagerFactory(), entity);
    }
    
    public void assertConfig(String entityName, FetchGroup defaultFetchGroup) {
        FetchGroupAssert.assertConfig(getEntityManagerFactory(), entityName, defaultFetchGroup);
    }
    
    public void assertConfig(String entityName, FetchGroup defaultFetchGroup, int numNamedFetchGroups) {
        FetchGroupAssert.assertConfig(getEntityManagerFactory(), entityName, defaultFetchGroup, numNamedFetchGroups);
    }
    
    public void assertConfig(ClassDescriptor descriptor, FetchGroup defaultFetchGroup) {
        FetchGroupAssert.assertConfig(descriptor, defaultFetchGroup);
    }
    
    public void assertConfig(ClassDescriptor descriptor, FetchGroup defaultFetchGroup, int numNamedFetchGroups) {
        FetchGroupAssert.assertConfig(descriptor, defaultFetchGroup, numNamedFetchGroups);
    }
    
    protected QuerySQLTracker getQuerySQLTracker(EntityManager em) {
        return QuerySQLTracker.getTracker(getServerSession());
    }

    ClassDescriptor getDescriptor(String entityName) {
        return getServerSession().getClassDescriptorForAlias(entityName);
    }

    public static int minimumEmployeeId(EntityManager em) {
        return ((Number) em.createQuery("SELECT MIN(e.id) FROM Employee e").getSingleResult()).intValue();
    }

    public static int minimumEmployeeWithoutDepartmentId(EntityManager em) {
        return ((Number) em.createQuery("SELECT MIN(e.id) FROM Employee e WHERE e.department IS NULL").getSingleResult()).intValue();
    }

    public static Employee minimumEmployee(EntityManager em) {
        Query q = em.createQuery("SELECT e FROM Employee e WHERE e.id in (SELECT MIN(ee.id) FROM Employee ee)");
        
        return (Employee) q.getSingleResult();
    }

    public static Employee minimumEmployee(EntityManager em, Map<String, Object> hints) {
        Query q = em.createQuery("SELECT e FROM Employee e WHERE e.id in (SELECT MIN(ee.id) FROM Employee ee)");
        Iterator<Map.Entry<String, Object>> it = hints.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry<String, Object> entry = it.next();
            q.setHint(entry.getKey(), entry.getValue());
        }
        
        return (Employee) q.getSingleResult();
    }

    public static Employee minEmployeeWithAddressAndPhones(EntityManager em) {
        return (Employee) em.createQuery("SELECT e FROM Employee e JOIN FETCH e.address WHERE e.id IN (SELECT MIN(p.id) FROM PhoneNumber p)").getSingleResult();
    }

    public Employee minEmployeeWithManagerWithAddress(EntityManager em) {
        List<Employee> emps = em.createQuery("SELECT e FROM Employee e JOIN FETCH e.manager WHERE e.manager.address IS NOT NULL ORDER BY e.id").getResultList();
        return emps.get(0);
    }

    public static int minEmployeeIdWithAddressAndPhones(EntityManager em) {
        return ((Number) em.createQuery("SELECT e.id FROM Employee e JOIN FETCH e.address WHERE e.id IN (SELECT MIN(p.id) FROM PhoneNumber p)").getSingleResult()).intValue();
    }

    public static class PhoneCustomizer implements DescriptorCustomizer {

        public void customize(ClassDescriptor descriptor) throws Exception {
            defaultPhoneFG = new FetchGroup("PhoneNumber.default");
            defaultPhoneFG.addAttribute("number");
            descriptor.getFetchGroupManager().setDefaultFetchGroup(defaultPhoneFG);
        }
    }    
}
