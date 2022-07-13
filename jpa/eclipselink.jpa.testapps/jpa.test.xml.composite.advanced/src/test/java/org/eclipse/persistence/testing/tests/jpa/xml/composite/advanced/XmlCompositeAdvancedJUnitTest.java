/*
 * Copyright (c) 1998, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
//     04/29/2011 - 2.3 Andrei Ilitchev
//       - Bug 328404 - JPA Persistence Unit Composition
//         Adapted org.eclipse.persistence.testing.tests.jpa.xml.advanced.XmlCompositeAdvancedJUnitTest
//         for composite persistence unit.
//         Try to keep one-to-one correspondence between the two in the future, too.
//         The tests that could not (or not yet) adapted for composite persistence unit
//         are commented out, the quick explanation why the test can't run is provided.
package org.eclipse.persistence.testing.tests.jpa.xml.composite.advanced;

import java.lang.reflect.Array;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.SelectedFieldsLockingPolicy;
import org.eclipse.persistence.descriptors.invalidation.CacheInvalidationPolicy;
import org.eclipse.persistence.descriptors.invalidation.TimeToLiveCacheInvalidationPolicy;
import org.eclipse.persistence.internal.descriptors.OptimisticLockingPolicy;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.mappings.converters.ObjectTypeConverter;
import org.eclipse.persistence.queries.DoesExistQuery;
import org.eclipse.persistence.sessions.broker.SessionBroker;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.models.jpa.xml.composite.advanced.member_1.Address;
import org.eclipse.persistence.testing.models.jpa.xml.composite.advanced.member_1.AdvancedTableCreator_1;
import org.eclipse.persistence.testing.models.jpa.xml.composite.advanced.member_1.Bungalow;
import org.eclipse.persistence.testing.models.jpa.xml.composite.advanced.member_1.Customer;
import org.eclipse.persistence.testing.models.jpa.xml.composite.advanced.member_2.AdvancedTableCreator_2;
import org.eclipse.persistence.testing.models.jpa.xml.composite.advanced.member_2.Employee;
import org.eclipse.persistence.testing.models.jpa.xml.composite.advanced.member_2.EmploymentPeriod;
import org.eclipse.persistence.testing.models.jpa.xml.composite.advanced.member_3.AdvancedTableCreator_3;
import org.eclipse.persistence.testing.models.jpa.xml.composite.advanced.member_3.Dealer;
import org.eclipse.persistence.testing.models.jpa.xml.composite.advanced.member_3.LargeProject;
import org.eclipse.persistence.testing.models.jpa.xml.composite.advanced.member_3.PhoneNumber;
import org.eclipse.persistence.testing.models.jpa.xml.composite.advanced.member_3.Project;
import org.eclipse.persistence.testing.models.jpa.xml.composite.advanced.member_3.SmallProject;
/*import org.eclipse.persistence.testing.models.jpa.xml.advanced.Confidant;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.Loner;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.Name;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.ReadOnlyClass;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.Shovel;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.ShovelDigger;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.ShovelOwner;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.ShovelProject;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.ShovelSections;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.SmallProject;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.ShovelSections.MaterialType;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.Violation;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.ViolationCode;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.Violation.ViolationID;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.ViolationCode.ViolationCodeId;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.additionalcriteria.Bolt;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.additionalcriteria.Nut;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.additionalcriteria.School;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.additionalcriteria.Student;*/
import org.eclipse.persistence.testing.framework.JoinedAttributeTestHelper;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.tools.schemaframework.TableCreator;

/**
 * JUnit test case(s) for the TopLink EntityMappingsXMLProcessor.
 */
public class XmlCompositeAdvancedJUnitTest extends JUnitTestCase {
    private static Integer employeeId, extendedEmployeeId;
    private static long visa = 1234567890;
    private static long amex = 1987654321;
    private static long diners = 1192837465;
    private static long mastercard = 1647382910;
    private static long rbc = 4783;
    private static long scotia = 8732;
    private static long td = 839362;
    private static long cibc = 948274;
    private String m_persistenceUnit;

    public XmlCompositeAdvancedJUnitTest() {
        super();
    }

    public XmlCompositeAdvancedJUnitTest(String name) {
        super(name);
        m_persistenceUnit = getPersistenceUnitName();
    }

    @Override
    public String getPersistenceUnitName() {
        return "xml-composite-advanced";
    }

    /*
     * n is a value between 1 and 3
     */
    public String getComponentMemberPuName(int n) {
        return "xml-composite-advanced-member_" + n;
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("Advanced Model - xml-composite-advanced");
        suite.addTest(new XmlCompositeAdvancedJUnitTest("testSetup"));
        suite.addTest(new XmlCompositeAdvancedJUnitTest("testCreateEmployee"));
        suite.addTest(new XmlCompositeAdvancedJUnitTest("testReadEmployee"));
        suite.addTest(new XmlCompositeAdvancedJUnitTest("testNamedNativeQueryOnAddress"));
        suite.addTest(new XmlCompositeAdvancedJUnitTest("testNamedQueryOnEmployee"));
        suite.addTest(new XmlCompositeAdvancedJUnitTest("testUpdateEmployee"));
        suite.addTest(new XmlCompositeAdvancedJUnitTest("testRefreshNotManagedEmployee"));
        suite.addTest(new XmlCompositeAdvancedJUnitTest("testRefreshRemovedEmployee"));
        suite.addTest(new XmlCompositeAdvancedJUnitTest("testDeleteEmployee"));
        suite.addTest(new XmlCompositeAdvancedJUnitTest("testUnidirectionalPersist"));
        suite.addTest(new XmlCompositeAdvancedJUnitTest("testUnidirectionalUpdate"));
// Can't join different data bases        suite.addTest(new XmlCompositeAdvancedJUnitTest("testUnidirectionalFetchJoin"));
        suite.addTest(new XmlCompositeAdvancedJUnitTest("testUnidirectionalTargetLocking_AddRemoveTarget"));
        suite.addTest(new XmlCompositeAdvancedJUnitTest("testUnidirectionalTargetLocking_DeleteSource"));
        suite.addTest(new XmlCompositeAdvancedJUnitTest("testMustBeCompositeMember"));

        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        SessionBroker broker = getSessionBroker();
        // member sessions
        ServerSession[] sessions = {
                (ServerSession)broker.getSessionForName(getComponentMemberPuName(1)),
                (ServerSession)broker.getSessionForName(getComponentMemberPuName(2)),
                (ServerSession)broker.getSessionForName(getComponentMemberPuName(3))
        };
        // table creators for each member session
        TableCreator[] tableCreators = {
                new AdvancedTableCreator_1(),
                new AdvancedTableCreator_2(),
                new AdvancedTableCreator_3()
        };
        for (int i = 0; i < sessions.length; i++) {
            ServerSession ss = sessions[i];
            for (int k = 0; k < tableCreators.length; k++) {
                TableCreator tableCreator = tableCreators[k];
                if (k == i) {
                    tableCreator.replaceTables(ss);
                } else {
                    if (!usingTheSameDatabase(ss, sessions[k])) {
                        // Drop tables from databases corresponding to the other sessions
                        // so that each table only defined in a single database.
                        // If a wrong session is queried then the query always fails - good for diagnostics.
                        tableCreator.dropTables(ss);
                    }
                }
            }
            // Force uppercase for Postgres.
            if (ss.getPlatform().isPostgreSQL()) {
                ss.getLogin().setShouldForceFieldNamesToUpperCase(true);
            }
        }

        // Populate the database with our examples.
        EmployeePopulator employeePopulator = new EmployeePopulator(TestCase.supportsStoredProcedures(getDatabaseSession()));
        employeePopulator.buildExamples();
        employeePopulator.persistExample(broker);
        clearCache();
    }

    /**
     * Verifies that access-methods are correctly processed and used.
     */
    public void testAccessorMethods() {
        EntityManager em = createEntityManager();
        String testSin = "123456";
        beginTransaction(em);
        try {
            Employee employee = ModelExamples.employeeExample1();
            employee.enterSIN(testSin);
            em.persist(employee);
            Integer employeeId = employee.getId();
            commitTransaction(em);

            clearCache();
            em.clear();

            // Re-read the employee and verify the data.
            Employee emp = em.find(Employee.class, employeeId);

            assertEquals("The SIN value was not persisted.", testSin, emp.returnSIN());
            assertEquals("The enterSIN accessor on Employee was not used to set the value", 1, emp.returnSinChangeCounter());
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
    }

    /**
     * Test user defined additional criteria with no parameters.
     */
/*    public void testAdditionalCriteriaModelPopulate() {
        EntityManager em = createEntityManager();
        beginTransaction(em);

        try {
            // Persist some schools
            School school1 = new School();
            school1.setName("Ottawa Junior High");
            school1.addStudent(new Student("OttawaJRStud1"));
            school1.addStudent(new Student("OttawaJRStud2"));
            school1.addStudent(new Student("OttawaJRStud3"));
            em.persist(school1);

            School school2 = new School();
            school2.setName("Ottawa Senior High");
            school2.addStudent(new Student("OttawaSRStud1"));
            school2.addStudent(new Student("OttawaSRStud2"));
            school2.addStudent(new Student("OttawaSRStud3"));
            school2.addStudent(new Student("OttawaSRStud4"));
            school2.addStudent(new Student("OttawaSRStud5"));
            em.persist(school2);

            School school3 = new School();
            school3.setName("Toronto Junior High");
            school3.addStudent(new Student("TorontoJRStud1"));
            school3.addStudent(new Student("TorontoJRStud2"));
            school3.addStudent(new Student("TorontoJRStud3"));
            school3.addStudent(new Student("TorontoJRStud4"));
            school3.addStudent(new Student("TorontoJRStud5"));
            school3.addStudent(new Student("TorontoJRStud6"));
            school3.addStudent(new Student("TorontoJRStud7"));
            em.persist(school3);

            School school4 = new School();
            school4.setName("Toronto Senior High");
            school4.addStudent(new Student("TorontoSRStud1"));
            school4.addStudent(new Student("TorontoSRStud2"));
            school4.addStudent(new Student("TorontoSRStud3"));
            school4.addStudent(new Student("TorontoSRStud4"));
            school4.addStudent(new Student("TorontoSRStud5"));
            school4.addStudent(new Student("TorontoSRStud6"));
            school4.addStudent(new Student("TorontoSRStud7"));
            school4.addStudent(new Student("TorontoSRStud8"));
            school4.addStudent(new Student("TorontoSRStud9"));
            school4.addStudent(new Student("TorontoSRStud10"));
            school4.addStudent(new Student("TorontoSRStud11"));
            em.persist(school4);

            School school5 = new School();
            school5.setName("Montreal Senior High");
            school5.addStudent(new Student("MontrealSRStud1"));
            school5.addStudent(new Student("MontrealSRStud2"));
            school5.addStudent(new Student("MontrealSRStud3"));
            school5.addStudent(new Student("MontrealSRStud4"));
            school5.addStudent(new Student("MontrealSRStud5"));
            em.persist(school5);

            Bolt bolt1 = new Bolt();
            Nut nut1 = new Nut();
            nut1.setColor("Grey");
            nut1.setSize(8);
            bolt1.setNut(nut1);
            em.persist(bolt1);

            Bolt bolt2 = new Bolt();
            Nut nut2 = new Nut();
            nut2.setColor("Black");
            nut2.setSize(8);
            bolt2.setNut(nut2);
            em.persist(bolt2);

            Bolt bolt3 = new Bolt();
            Nut nut3 = new Nut();
            nut3.setColor("Grey");
            nut3.setSize(6);
            bolt3.setNut(nut3);
            em.persist(bolt3);

            Bolt bolt4 = new Bolt();
            Nut nut4 = new Nut();
            nut4.setColor("Black");
            nut4.setSize(6);
            bolt4.setNut(nut4);
            em.persist(bolt4);

            Bolt bolt5 = new Bolt();
            Nut nut5 = new Nut();
            nut5.setColor("Grey");
            nut5.setSize(2);
            bolt5.setNut(nut5);
            em.persist(bolt5);

            Bolt bolt6 = new Bolt();
            Nut nut6 = new Nut();
            nut6.setColor("Grey");
            nut6.setSize(8);
            bolt6.setNut(nut6);
            em.persist(bolt6);

            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            closeEntityManager(em);
            // Re-throw exception to ensure stacktrace appears in test result.
            throw e;
        }

        closeEntityManager(em);
    }*/

    /**
     * Test user defined additional criteria with no parameters. The additional
     * criteria on school filters for Ottawa named schools.
     */
    public void testAdditionalCriteria() {
        EntityManager em = createEntityManager();

        try {
            List schools = em.createNamedQuery("findJPQLXMLSchools").getResultList();
            assertEquals("Incorrect number of schools were returned [" + schools.size() + "], expected [2]", 2, schools.size());
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            closeEntityManager(em);
            // Re-throw exception to ensure stacktrace appears in test result.
            throw e;
        }

        closeEntityManager(em);
    }

    /**
     * Test user defined additional criteria with parameter.
     */
    public void testAdditionalCriteriaWithParameterFromEM1() {
        EntityManager em = createEntityManager();

        try {
            // This should override the EMF property of Montreal%
            em.setProperty("NAME", "Ottawa%");

            // Find the schools, because of our additional criteria on Student
            // and the property above, we should only return Ottawa students.

            List students = em.createQuery("SELECT s from XMLStudent s").getResultList();
            assertEquals("Incorrect number of students were returned [" + students.size() + "], expected [8]", 8, students.size());
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            closeEntityManager(em);
            // Re-throw exception to ensure stacktrace appears in test result.
            throw e;
        }

        closeEntityManager(em);
    }

    /**
     * Test user defined additional criteria with parameter.
     */
    public void testAdditionalCriteriaWithParameterFromEM2() {
        EntityManager em = createEntityManager();

        try {
            // This should override the EMF property of Montreal%
            em.setProperty("NAME", "Toronto%");

            // Find the schools, because of our additional criteria on Student
            // and the property above, we should only return Toronto students.
            // However, they should not have any schools loaded.

            List students = em.createQuery("SELECT s from XMLStudent s").getResultList();
            assertEquals("Incorrect number of students were returned [" + students.size() + "], expected [18]", 18, students.size());
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            closeEntityManager(em);
            // Re-throw exception to ensure stacktrace appears in test result.
            throw e;
        }

        closeEntityManager(em);
    }

    /**
     * Test user defined additional criteria with parameters.
     */
    public void testAdditionalCriteriaWithParameterFromEMF() {
        EntityManager em = createEntityManager();

        try {
            // This should use the EMF NAME property of Montreal%
            List students = em.createQuery("SELECT s from XMLStudent s").getResultList();
            assertEquals("Incorrect number of students were returned [" + students.size() + "], expected [5]", 5, students.size());
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            closeEntityManager(em);
            // Re-throw exception to ensure stacktrace appears in test result.
            throw e;
        }

        closeEntityManager(em);
    }

    /**
     * Test user defined additional criteria with parameter.
     */
    public void testComplexAdditionalCriteria() {
        EntityManager em = createEntityManager();

        try {
            em.setProperty("NUT_SIZE", 8);
            em.setProperty("NUT_COLOR", "Grey");

            List bolts = em.createQuery("SELECT b from XMLBolt b").getResultList();
            assertEquals("Incorrect number of bolts were returned [" + bolts.size() + "], expected [2]", 2, bolts.size());
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            closeEntityManager(em);
            // Re-throw exception to ensure stacktrace appears in test result.
            throw e;
        }

        closeEntityManager(em);
    }

    /**
     * Verifies a default object type value is set from metadata processing.
     */
    public void testSexObjectTypeConverterDefaultValue() {
        DatabaseSessionImpl session = getDatabaseSession();

        ClassDescriptor employeeDescriptor = session.getDescriptor(Employee.class);
        DirectToFieldMapping mapping = (DirectToFieldMapping) employeeDescriptor.getMappingForAttributeName("gender");
        assertNotNull("Gender mapping from Employee not found.", mapping);
        assertTrue("No object type converter found on the gender mapping.", ObjectTypeConverter.class.isAssignableFrom(mapping.getConverter().getClass()));
        assertEquals("Default object value on the object type converter for gender was not set to Male.", ((ObjectTypeConverter) mapping.getConverter()).getDefaultAttributeValue(), Employee.Gender.Male.name());
    }

    /**
     * Verifies that existence-checking metadata is correctly processed.
     */
    public void testExistenceCheckingSetting() {
        DatabaseSessionImpl session = getDatabaseSession();

        ClassDescriptor employeeDescriptor = session.getDescriptor(Employee.class);
        assertEquals("Employee existence checking was incorrect", DoesExistQuery.CheckDatabase, employeeDescriptor.getQueryManager().getDoesExistQuery().getExistencePolicy());

        ClassDescriptor projectDescriptor = session.getDescriptor(Project.class);
        assertEquals("Project existence checking was incorrect", DoesExistQuery.CheckCache, projectDescriptor.getQueryManager().getDoesExistQuery().getExistencePolicy());

        ClassDescriptor smallProjectDescriptor = session.getDescriptor(SmallProject.class);
        assertEquals("SmallProject existence checking was incorrect", DoesExistQuery.AssumeExistence, smallProjectDescriptor.getQueryManager().getDoesExistQuery().getExistencePolicy());

        ClassDescriptor largeProjectDescriptor = session.getDescriptor(LargeProject.class);
        assertEquals("LargeProject existence checking was incorrect", DoesExistQuery.AssumeNonExistence, largeProjectDescriptor.getQueryManager().getDoesExistQuery().getExistencePolicy());
    }

    /**
     * Verifies that a basic collection is not added to the employee descriptor
     * twice.
     */
    public void testIfMultipleBasicCollectionMappingsExistForEmployeeResponsibilites() {
        DatabaseSessionImpl session = getDatabaseSession();

        ClassDescriptor employeeDescriptor = session.getDescriptor(Employee.class);

        int foundCount = 0;
        for (DatabaseMapping mapping : employeeDescriptor.getMappings()) {
            if (mapping.isDirectCollectionMapping()) {
                if (mapping.getAttributeName().equals("responsibilities")) {
                    foundCount++;
                }
            }
        }

        assertFalse("We found multiple mappings for Employee responsibilities", foundCount == 2);
    }

    /**
     * Verifies that the read-only metadata is correctly processed.
     */
/*    public void testReadOnlyClassSetting() {
        DatabaseSessionImpl session = getDatabaseSession();
        ClassDescriptor descriptor = session.getDescriptor(ReadOnlyClass.class);

        assertFalse("ReadOnlyClass descriptor was not found in the PU [" + m_persistenceUnit + "]", descriptor == null);
        assertTrue("ReadOnlyClass descriptor is not set to read only.", descriptor.shouldBeReadOnly());
    */

    /**
     * Verifies that the change tracking metadata is correctly processed.
     * Employee has an AUTO setting, but has EAGER collections and transformation mapping
     * so should not be change tracked.
     */
    public void testEmployeeChangeTrackingPolicy() {
        DatabaseSessionImpl session = getDatabaseSession();
        ClassDescriptor descriptor = session.getDescriptor(Employee.class);

        assertNotNull("Employee descriptor was not found in the PU [" + m_persistenceUnit + "]", descriptor);
        assertFalse("Employee descriptor has incorrect object change policy", descriptor.getObjectChangePolicy().isAttributeChangeTrackingPolicy());
    }

    /**
     * Verifies that the change tracking metadata is correctly processed.
     */
    public void testAddressChangeTrackingPolicy() {
        DatabaseSessionImpl session = getDatabaseSession();
        ClassDescriptor descriptor = session.getDescriptor(Address.class);

        assertNotNull("Address descriptor was not found in the PU [" + m_persistenceUnit + "]", descriptor);
        assertTrue("Address descriptor has incorrect object change policy", descriptor.getObjectChangePolicyInternal().isDeferredChangeDetectionPolicy());
    }

    /**
     * Verifies that the change tracking metadata is correctly processed.
     */
    public void testPhoneNumberChangeTrackingPolicy() {
        if (!isWeavingEnabled()) {
            return;
        }

        DatabaseSessionImpl session = getDatabaseSession();
        ClassDescriptor descriptor = session.getDescriptor(PhoneNumber.class);

        assertNotNull("PhoneNumber descriptor was not found in the PU [" + m_persistenceUnit + "]", descriptor);
        assertTrue("PhoneNumber descriptor has incorrect object change policy", descriptor.getObjectChangePolicy().isAttributeChangeTrackingPolicy());
    }

    /**
     * Verifies that the change tracking metadata is correctly processed.
     */
    public void testProjectChangeTrackingPolicy() {
        if (!isWeavingEnabled()) {
            return;
        }

        DatabaseSessionImpl session = getDatabaseSession();
        ClassDescriptor descriptor = session.getDescriptor(Project.class);

        assertNotNull("Project descriptor was not found in the PU [" + m_persistenceUnit + "]", descriptor);
        assertTrue("Project descriptor has incorrect object change policy", descriptor.getObjectChangePolicy().isObjectChangeTrackingPolicy());
    }

    /**
     * Verifies that the join-fetch setting was read correctly from XML.
     */
    public void testJoinFetchSetting() {
        DatabaseSessionImpl session = getDatabaseSession();
        ClassDescriptor descriptor = session.getDescriptor(Employee.class);

        if (descriptor == null) {
            fail("Employee descriptor was not found in the PU [" + m_persistenceUnit + "]");
        } else if (((ForeignReferenceMapping)descriptor.getMappingForAttributeName("address")).getJoinFetch() != ForeignReferenceMapping.OUTER_JOIN) {
            fail("join-fetch setting from XML was not read correctly for Employee's address.");
        }
    }

    /**
     * Verifies that the optimistic-locking settings were read correctly from XML.
     */
    public void testEmployeeOptimisticLockingSettings() {
        DatabaseSessionImpl session = getDatabaseSession();
        ClassDescriptor descriptor = session.getDescriptor(Employee.class);

        if (descriptor == null) {
            fail("Employee descriptor was not found in the PU [" + m_persistenceUnit + "]");
        } else {
            assertTrue("Incorrect optimistic locking policy set on the Employee descriptor.", descriptor.usesVersionLocking());
            assertTrue("Incorrect cascade option set on the optimistic locking poluicy.", descriptor.getOptimisticLockingPolicy().isCascaded());
        }
    }

    /**
     * Verifies that settings from the Employee cache annotation have been set.
     * Also verifies that that employee customizer sets the disable cache hits
     * back to false. It is true in XML.
     */
    public void testEmployeeCacheSettings() {
        DatabaseSessionImpl session = getDatabaseSession();
        ClassDescriptor descriptor = session.getDescriptor(Employee.class);

        if (descriptor == null) {
            fail("A descriptor for the Employee alias was not found in the PU [" + m_persistenceUnit + "]");
        } else {
            assertEquals("Incorrect cache type() setting.", descriptor.getIdentityMapClass(), ClassConstants.SoftCacheWeakIdentityMap_Class);
            assertEquals("Incorrect cache size() setting.", 730, descriptor.getIdentityMapSize());
            assertFalse("Incorrect cache isolated() setting.", descriptor.isIsolated());
            assertFalse("Incorrect cache alwaysRefresh() setting.", descriptor.shouldAlwaysRefreshCache());
            assertFalse("Incorrect disable hits setting.", descriptor.shouldDisableCacheHits());
            CacheInvalidationPolicy policy = descriptor.getCacheInvalidationPolicy();
            assertTrue("Incorrect cache expiry() policy setting.", policy instanceof TimeToLiveCacheInvalidationPolicy);
            assertEquals("Incorrect cache expiry() setting.", 1000, ((TimeToLiveCacheInvalidationPolicy) policy).getTimeToLive());
            assertEquals("Incorrect cache coordinationType() settting.", ClassDescriptor.INVALIDATE_CHANGED_OBJECTS, descriptor.getCacheSynchronizationType());
        }
    }

    /**
     * Verifies that the optimistic-locking settings were read correctly from XML.
     */
    public void testProjectOptimisticLockingSettings() {
        DatabaseSessionImpl session = getDatabaseSession();
        ClassDescriptor descriptor = session.getDescriptor(Project.class);

        if (descriptor == null) {
            fail("Project descriptor was not found in the PU [" + m_persistenceUnit + "]");
        } else {
            OptimisticLockingPolicy policy = descriptor.getOptimisticLockingPolicy();

            if (policy instanceof SelectedFieldsLockingPolicy) {
                List<DatabaseField> lockFields = ((SelectedFieldsLockingPolicy) policy).getLockFields();

                if (lockFields.isEmpty() || lockFields.size() > 1) {
                    fail("Invalid amount of lock fields were set on Project's selected fields locking policy.");
                } else {
                    DatabaseField lockField = lockFields.get(0);
                    assertEquals("Incorrect lock field was set on Project's selected fields locking policy.", "VERSION", lockField.getName());
                }
            } else {
            fail("A SelectedFieldsLockingPolicy was not set on the Project descriptor.");
            }
        }
    }

    public void testCreateEmployee() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Employee employee = ModelExamples.employeeExample1();
            List<Project> projects = new ArrayList<>();
            projects.add(ModelExamples.projectExample1());
            projects.add(ModelExamples.projectExample2());
            employee.setProjects(projects);
            em.persist(employee);
            employeeId = employee.getId();
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }

    }

    /**
     * Tests:
     * - BasicMap mapping with a TypeConverter on the map value and an
     *   ObjectTypeConverter on the map key.
     * - Basic with a converter
     * - Basic with a custom converter
     */
    public void testExtendedEmployee() {
        EntityManager em = createEntityManager();
        beginTransaction(em);

        try {
            Address address = new Address();
            address.setCity("Nepean");
            address.setCountry("Canada");
            address.setPostalCode("K2J 6T3");
            // One it goes through the custom converter this should be
            // set to Ontario.
            address.setProvince("oNtArIo");
            address.setStreet("321 Crestway");

            Employee employee = new Employee();
            employee.setGivenName("Boy");
            employee.setFamilyName("Pelletier");
            employee.setMale();
            employee.addVisa(visa);
            employee.addAmex(amex);
            employee.addDinersClub(diners);
            employee.addMastercard(mastercard);
            employee.addRoyalBankCreditLine(rbc);
            employee.addScotiabankCreditLine(scotia);
            employee.addTorontoDominionCreditLine(td);
            employee.addCanadianImperialCreditLine(cibc);
            employee.setAddress(address);
            employee.setSalary(20000);
            employee.setPeriod(new EmploymentPeriod());
            employee.getPeriod().setStartDate(new Date(System.currentTimeMillis()-1000000));
            employee.getPeriod().setEndDate(new Date(System.currentTimeMillis()+1000000));
            employee.addResponsibility("A very important responsibility");

            em.persist(employee);
            extendedEmployeeId = employee.getId();
            commitTransaction(em);

            clearCache();
            em.clear();

            // Re-read the employee and verify the data.
            Employee emp = em.find(Employee.class, extendedEmployeeId);
            assertNotNull("The employee was not found", emp);
            assertEquals("The address province value was not converted", "Ontario", emp.getAddress().getProvince());
            assertTrue("Gender was not persisted correctly.", emp.isMale());
            assertTrue("Visa card did not persist correctly.", emp.hasVisa(visa));
            assertTrue("Amex card did not persist correctly.", emp.hasAmex(amex));
            assertTrue("Diners Club card did not persist correctly.", emp.hasDinersClub(diners));
            assertTrue("Mastercard card did not persist correctly.", emp.hasMastercard(mastercard));
            assertTrue("RBC credit line did not persist correctly.", emp.hasRoyalBankCreditLine(rbc));
            assertTrue("Scotia credit line did not persist correctly.", emp.hasScotiabankCreditLine(scotia));
            assertTrue("TD credit line did not persist correctly.", emp.hasTorontoDominionCreditLine(td));
            assertTrue("CIBC credit line did not persist correctly.", emp.hasCanadianImperialCreditLine(cibc));

            boolean found = false;
            for (String responsibility : emp.getResponsibilities()) {
                if (responsibility.equals("A very important responsibility")) {
                    found = true;
                    break;
                }
            }

            assertTrue("The new responsibility was not added.", found);

        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            closeEntityManager(em);
            throw e;
        }

        closeEntityManager(em);
    }

    /**
     * Tests an ObjectTypeConverter on a direct to field mapping.
     */
    public void testGiveExtendedEmployeeASexChange() {
        EntityManager em = createEntityManager();
        beginTransaction(em);

        try {
            Employee maleEmp = em.find(Employee.class, extendedEmployeeId);
            maleEmp.setFemale();
            maleEmp.setGivenName("Girl");
            commitTransaction(em);

            // Clear cache and clear the entity manager
            clearCache();
            em.clear();

            Employee girlEmp = em.find(Employee.class, extendedEmployeeId);
            assertTrue("The extended employee's change didn't occur.", girlEmp.isFemale());
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            closeEntityManager(em);
            throw e;
        }

        closeEntityManager(em);
    }

    public void testDeleteEmployee() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            em.remove(em.find(Employee.class, employeeId));
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
        assertNull("Error deleting Employee", em.find(Employee.class, employeeId));
    }

    public void testNamedNativeQueryOnAddress() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Address address1 = ModelExamples.addressExample1();
            em.persist(address1);
            Address address2 = ModelExamples.addressExample2();
            em.persist(address2);
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
        Query query = em.createNamedQuery("findAllXMLAddresses");
        List addresses = query.getResultList();
        assertNotNull("Error executing named native query 'findAllXMLAddresses'", addresses);
    }

    public void testNamedQueryOnEmployee() {
        Query query = createEntityManager().createNamedQuery("findAllXMLEmployeesByFirstName");
        query.setParameter("firstname", "Brady");
        Employee employee = (Employee) query.getSingleResult();
        assertNotNull("Error executing named query 'findAllXMLEmployeesByFirstName'", employee);
    }

    public void testReadEmployee() {
        Employee employee = createEntityManager().find(Employee.class, employeeId);
        assertEquals("Error reading Employee", (int) employee.getId(), employeeId.intValue());
    }

    public void testUpdateEmployee() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        int version = 0;

        try {
            Employee employee = em.find(Employee.class, employeeId);
            version = employee.getVersion();
            employee.setSalary(50000);

            em.merge(employee);
            commitTransaction(em);

            // Clear cache and clear the entity manager
            clearCache();
            em.clear();

            Employee emp = em.find(Employee.class, employeeId);
            assertEquals("Error updating Employee", 50000, emp.getSalary());
            assertEquals("Version field not updated", emp.getVersion(), version + 1);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            closeEntityManager(em);
            throw e;
        }

        closeEntityManager(em);
    }

    public void testRefreshNotManagedEmployee() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Employee emp = new Employee();
            emp.setGivenName("NotManaged");
            em.refresh(emp);
            fail("entityManager.refresh(notManagedObject) didn't throw exception");
        } catch (IllegalArgumentException illegalArgumentException) {
            // expected behavior
        } catch (RuntimeException e ) {
            throw e;
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    public void testRefreshRemovedEmployee() {
        // find an existing or create a new Employee
        String firstName = "testRefreshRemovedEmployee";
        Employee emp;
        EntityManager em = createEntityManager();
        List result = em.createQuery("SELECT OBJECT(e) FROM XMLEmployee e WHERE e.firstName = '"+firstName+"'").getResultList();
        if(!result.isEmpty()) {
            emp = (Employee)result.get(0);
        } else {
            emp = new Employee();
            emp.setGivenName(firstName);
            // persist the Employee
            beginTransaction(em);
            try {
                em.persist(emp);
                commitTransaction(em);
            } catch (RuntimeException e) {
                if (isTransactionActive(em)){
                    rollbackTransaction(em);
                }
                closeEntityManager(em);
                throw e;
            }
        }

        beginTransaction(em);
        try{
            emp = em.find(Employee.class, emp.getId());

            // Currently composite persistence unit doesn't support DeleteAll if there is a ManyToMany or DirectCollection with target in another session.
            // That situation causes attempt to join between two data bases, which is not possible.
            // The following JPQL
            //    em.createQuery("DELETE FROM XMLEmployee e WHERE e.firstName = '"+firstName+"'").executeUpdate();
            // generates:
            //    DELETE FROM XML_BR3_PROJ_EMP WHERE EXISTS
            //      (SELECT t0.EMP_ID FROM XML_BR2_EMPLOYEE t0, XML_BR2_SALARY t1
            //           WHERE ((t0.F_NAME = ?) AND (t1.E_ID = t0.EMP_ID)) AND t0.EMP_ID = XML_BR3_PROJ_EMP.EMP_ID)
            // In the sql above tables prefixed with XML_BR2_ and XML_BR3_ are in different data bases.
            List<Employee> employeesToBeDeleted = em.createQuery("SELECT e FROM XMLEmployee e WHERE e.firstName = '"+firstName+"'", Employee.class).getResultList();
            for(Employee empToBeDeleted : employeesToBeDeleted) {
                int id = empToBeDeleted.getId();
                // Note that for native query the user need to specify targetPU using COMPOSITE_MEMBER query hint.
                em.createNativeQuery("DELETE FROM XML_MBR2_SALARY WHERE E_ID = "+id).setHint(QueryHints.COMPOSITE_UNIT_MEMBER, getComponentMemberPuName(2)).executeUpdate();
                em.createNativeQuery("DELETE FROM XML_MBR2_EMPLOYEE WHERE EMP_ID = "+id).setHint(QueryHints.COMPOSITE_UNIT_MEMBER, getComponentMemberPuName(2)).executeUpdate();
            }

            // refresh the Employee - should fail with EntityNotFoundException
            em.refresh(emp);
            fail("entityManager.refresh(removedObject) didn't throw exception");
        } catch (EntityNotFoundException entityNotFoundException) {
            // expected behavior
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    public void testContainsRemovedEmployee() {
        // find an existing or create a new Employee
        String firstName = "testContainsRemovedEmployee";
        Employee emp;
        EntityManager em = createEntityManager();
        List result = em.createQuery("SELECT OBJECT(e) FROM XMLEmployee e WHERE e.firstName = '"+firstName+"'").getResultList();
        if(!result.isEmpty()) {
            emp = (Employee)result.get(0);
        } else {
            emp = new Employee();
            emp.setGivenName(firstName);
            // persist the Employee
            beginTransaction(em);
            try{
                em.persist(emp);
                commitTransaction(em);
            } catch (RuntimeException e) {
                if (isTransactionActive(em)){
                    rollbackTransaction(em);
                }
                closeEntityManager(em);
                throw e;
            }
        }

        boolean containsRemoved = true;
        beginTransaction(em);
        try{
            emp = em.find(Employee.class, emp.getId());
            em.remove(emp);
            containsRemoved = em.contains(emp);
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }

        assertFalse("entityManager.contains(removedObject)==true ", containsRemoved);
    }

    public void testSubString() {
        // find an existing or create a new Employee
        String firstName = "testSubString";
        Employee emp;
        EntityManager em = createEntityManager();
        List result = em.createQuery("SELECT OBJECT(e) FROM XMLEmployee e WHERE e.firstName = '"+firstName+"'").getResultList();
        if(!result.isEmpty()) {
            emp = (Employee)result.get(0);
        } else {
            emp = new Employee();
            emp.setGivenName(firstName);
            // persist the Employee
            beginTransaction(em);
            try{
                em.persist(emp);
                commitTransaction(em);
            } catch (RuntimeException e) {
                if (isTransactionActive(em)){
                    rollbackTransaction(em);
                }
                closeEntityManager(em);
                throw e;
            }
        }

        int firstIndex = 1;
        int lastIndex = firstName.length();
        List employees = em.createQuery("SELECT object(e) FROM XMLEmployee e where e.firstName = substring(:p1, :p2, :p3)").
            setParameter("p1", firstName).
            setParameter("p2", firstIndex).
            setParameter("p3", lastIndex).
            getResultList();

        // clean up
        beginTransaction(em);
        try{
            em.createQuery("DELETE FROM XML_BR2_Employee e WHERE e.firstName = '"+firstName+"'").executeUpdate();
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }

        assertFalse("employees.isEmpty()==true ", employees.isEmpty());
    }


    /**
     * Tests a named-stored-procedure-query setting
     */
    public void testNamedStoredProcedureQuery() {
        if (!supportsStoredProcedures()) {
            return;
        }
        EntityManager em = createEntityManager();
        beginTransaction(em);

        try {
            Address address1 = new Address();

            address1.setCity("Ottawa");
            address1.setPostalCode("K1G6P3");
            address1.setProvince("ON");
            address1.setStreet("123 Street");
            address1.setCountry("Canada");

            em.persist(address1);
            commitTransaction(em);

            // 260263 and 302316: clear the cache or we will end up with a false positive when comparing the entity to itself later
            em.clear();

            Query aQuery = em.createNamedQuery("SProcXMLAddress").setParameter("ADDRESS_ID", address1.getId());
            Address address2 = (Address) aQuery.getSingleResult();

            assertNotNull("Address returned from stored procedure is null", address2);
            assertNotSame("Address returned is the same cached instance that was persisted - the cache must be disabled for this test", address1, address2); // new
            // Integer address handled differently than int
            assertEquals("Address not found using stored procedure", address1.getId().intValue(), address2.getId().intValue());
            assertEquals("Address.street data returned doesn't match persisted address.street", address1.getStreet(), address2.getStreet());
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            closeEntityManager(em);

            // Re-throw exception to ensure stacktrace appears in test result.
            throw e;
        }

        closeEntityManager(em);
    }

    /**
     * Tests a named-stored-procedure-query setting
     */
    public void testNamedStoredProcedureQueryInOut() {
        if (!supportsStoredProcedures()) {
            return;
        }
        EntityManager em = createEntityManager();
        beginTransaction(em);

        try {
            Address address1 = new Address();

            address1.setCity("Ottawa");
            address1.setPostalCode("K1G6P3");
            address1.setProvince("ON");
            address1.setStreet("123 Street");
            address1.setCountry("Canada");

            em.persist(address1);
            commitTransaction(em);
            // 260263 and 302316: clear the cache or we will end up with a false positive when comparing the entity to itself later
            em.clear();

            Query aQuery = em.createNamedQuery("SProcXMLInOut").setParameter("ADDRESS_ID", address1.getId());
            Address address2 = (Address) aQuery.getSingleResult();

            assertNotNull("Address returned from stored procedure is null", address2);
            assertNotSame("Address returned is the same cached instance that was persisted - the cache must be disabled for this test", address1, address2); // new
            // Integer address handled differently than int
            assertEquals("Address not found using stored procedure", address1.getId().intValue(), address2.getId().intValue());
            assertEquals("Address.street data returned doesn't match persisted address.street", address1.getStreet(), address2.getStreet());
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            closeEntityManager(em);

            // Re-throw exception to ensure stacktrace appears in test result.
            throw e;
        }

        closeEntityManager(em);
    }

    public void testMethodBasedTransformationMapping() {
        internalTestTransformationMapping("normalHours");
    }

    public void testClassBasedTransformationMapping() {
        internalTestTransformationMapping("overtimeHours");
    }

    protected void internalTestTransformationMapping(String attributeName) {
        // setup: create an Employee, insert into db
        int startHour = 8, startMin = 30, startSec = 15;
        int endHour = 17, endMin = 15, endSec = 45;
        String firstName = attributeName;
        Employee employee = new Employee();
        employee.setGivenName(firstName);
        if(attributeName.equals("normalHours")) {
            employee.setStartTime(Helper.timeFromHourMinuteSecond(startHour, startMin, startSec));
            employee.setEndTime(Helper.timeFromHourMinuteSecond(endHour, endMin, endSec));
        } else if(attributeName.equals("overtimeHours")) {
            employee.setStartOvertime(Helper.timeFromHourMinuteSecond(startHour, startMin, startSec));
            employee.setEndOvertime(Helper.timeFromHourMinuteSecond(endHour, endMin, endSec));
        } else {
            throw new RuntimeException("Unknown attributeName");
        }
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            em.persist(employee);
            commitTransaction(em);
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
        int id = employee.getId();

        // test
        // clear cache
        this.clearCache();
        // read the employee from the db
        em = createEntityManager();
        employee = em.find(Employee.class,id);

        // verify
        Calendar calendarStart = Calendar.getInstance();
        Calendar calendarEnd = Calendar.getInstance();
        if(attributeName.equals("normalHours")) {
            calendarStart.setTime(employee.getStartTime());
            calendarEnd.setTime(employee.getEndTime());
        } else if(attributeName.equals("overtimeHours")) {
            calendarStart.setTime(employee.getStartOvertime());
            calendarEnd.setTime(employee.getEndOvertime());
        }
        String errorMsg = "";
        if(calendarStart.get(Calendar.HOUR_OF_DAY) != startHour || calendarStart.get(Calendar.MINUTE) != startMin || calendarStart.get(Calendar.SECOND) != startSec) {
            if(attributeName.equals("normalHours")) {
                errorMsg = "startTime = " + employee.getStartTime().toString() + " is wrong";
            } else if(attributeName.equals("overtimeHours")) {
                errorMsg = "startOvertime = " + employee.getStartOvertime().toString() + " is wrong";
            }
        }
        if(calendarEnd.get(Calendar.HOUR_OF_DAY) != endHour || calendarEnd.get(Calendar.MINUTE) != endMin || calendarEnd.get(Calendar.SECOND) != endSec) {
            if(errorMsg.length() > 0) {
                errorMsg = errorMsg + "; ";
            }
            if(attributeName.equals("normalHours")) {
                errorMsg = "endTime = " + employee.getEndTime().toString() + " is wrong";
            } else if(attributeName.equals("overtimeHours")) {
                errorMsg = "endOvertime = " + employee.getEndOvertime().toString() + " is wrong";
            }
        }

        // clean up
        beginTransaction(em);
        try {
            em.remove(em.merge(employee));
            commitTransaction(em);
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }

        if(errorMsg.length() > 0) {
            fail(errorMsg);
        }
    }

    public void testClassInstanceConverter(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        Address add = new Address();
        add.setCity("St. Louis");
        add.setType(new Bungalow());
        em.persist(add);
        commitTransaction(em);
        int assignedSequenceNumber = add.getId();

        em.clear();
        getDatabaseSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        try {
            add = em.find(Address.class, assignedSequenceNumber);

            assertTrue("Did not correctly persist a mapping using a class-instance converter", (add.getType() instanceof Bungalow));

            beginTransaction(em);
            em.remove(em.merge(add));
            commitTransaction(em);
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }


    /**
     * Tests Property and Properties annotations
     */
    public void testProperty() {
        DatabaseSessionImpl session = getDatabaseSession();
        ClassDescriptor descriptor = session.getDescriptorForAlias("XMLEmployee");
        ClassDescriptor aggregateDescriptor = session.getDescriptor(EmploymentPeriod.class);

        StringBuilder errorMsg = new StringBuilder();

        if (descriptor == null) {
            errorMsg.append(" Descriptor for XMLEmployee alias was not found;");
        }
        if (aggregateDescriptor == null) {
            errorMsg.append(" Descriptor for EmploymentPeriod.class was not found;");
        }
        if(errorMsg.length() > 0) {
            fail(errorMsg.toString());
        }

        // verify properties set on Employee instance
        errorMsg.append(verifyPropertyValue(descriptor, "entityName", String.class, "XMLEmployee"));
        errorMsg.append(verifyPropertyValue(descriptor, "entityIntegerProperty", Integer.class, 1));
        errorMsg.append(verifyPropertyValue(descriptor, "ToBeOverriddenByXml", Boolean.class, Boolean.TRUE));
        errorMsg.append(verifyPropertyValue(descriptor, "ToBeProcessed", Boolean.class, Boolean.TRUE));

        // each attribute of Employee was assigned a property attributeName with the value attribute name.
        for(DatabaseMapping mapping : descriptor.getMappings()) {
            errorMsg.append(verifyPropertyValue(mapping, "attributeName", String.class, mapping.getAttributeName()));
        }

        // attribute m_lastName has many properties of different types
        DatabaseMapping mapping = descriptor.getMappingForAttributeName("lastName");
        errorMsg.append(verifyPropertyValue(mapping, "BooleanProperty", Boolean.class, Boolean.TRUE));
        errorMsg.append(verifyPropertyValue(mapping, "ByteProperty", Byte.class, (byte) 1));
        errorMsg.append(verifyPropertyValue(mapping, "CharacterProperty", Character.class, 'A'));
        errorMsg.append(verifyPropertyValue(mapping, "DoubleProperty", Double.class, 1.0));
        errorMsg.append(verifyPropertyValue(mapping, "FloatProperty", Float.class, 1F));
        errorMsg.append(verifyPropertyValue(mapping, "IntegerProperty", Integer.class, 1));
        errorMsg.append(verifyPropertyValue(mapping, "LongProperty", Long.class, 1L));
        errorMsg.append(verifyPropertyValue(mapping, "ShortProperty", Short.class, (short) 1));
        errorMsg.append(verifyPropertyValue(mapping, "BigDecimalProperty", java.math.BigDecimal.class, java.math.BigDecimal.ONE));
        errorMsg.append(verifyPropertyValue(mapping, "BigIntegerProperty", java.math.BigInteger.class, java.math.BigInteger.ONE));
        errorMsg.append(verifyPropertyValue(mapping, "TimeProperty", java.sql.Time.class, Helper.timeFromString("13:59:59")));
        errorMsg.append(verifyPropertyValue(mapping, "TimeStampProperty", java.sql.Timestamp.class, Helper.timestampFromString("2008-04-10 13:59:59")));
        errorMsg.append(verifyPropertyValue(mapping, "DateProperty", Date.class, Helper.dateFromString("2008-04-10")));

        errorMsg.append(verifyPropertyValue(mapping, "ToBeIgnored", null, null));

        // verify property set on EmploymentPeriod embeddable
        errorMsg.append(verifyPropertyValue(aggregateDescriptor, "embeddableClassName", String.class, "EmploymentPeriod"));
        errorMsg.append(verifyPropertyValue(aggregateDescriptor, "ToBeOverriddenByXml", Boolean.class, Boolean.TRUE));
        errorMsg.append(verifyPropertyValue(aggregateDescriptor, "ToBeProcessed", Boolean.class, Boolean.TRUE));

        if(errorMsg.length() > 0) {
            fail(errorMsg.toString());
        }
    }
    protected String verifyPropertyValue(ClassDescriptor descriptor, String propertyName, Class<?> expectedPropertyValueType, Object expectedPropertyValue) {
        return verifyPropertyValue(propertyName, descriptor.getProperty(propertyName), expectedPropertyValueType, expectedPropertyValue, Helper.getShortClassName(descriptor.getJavaClass()) + " descriptor");
    }
    protected String verifyPropertyValue(DatabaseMapping mapping, String propertyName, Class<?> expectedPropertyValueType, Object expectedPropertyValue) {
        return verifyPropertyValue(propertyName, mapping.getProperty(propertyName), expectedPropertyValueType, expectedPropertyValue, mapping.getAttributeName() + " attribute");
    }
    protected String verifyPropertyValue(String propertyName, Object propertyValue, Class<?> expectedPropertyValueType, Object expectedPropertyValue, String masterName) {
        String errorMsg = "";
        String errorPrefix = " property " + propertyName + " for " + masterName;
        if(expectedPropertyValueType == null) {
            if(propertyValue != null) {
                errorMsg = errorPrefix + " value is " + propertyValue + " , was expected to be null.";
            }
            return errorMsg;
        }
        if(propertyValue == null) {
            errorMsg = errorPrefix + " is missing;";
        } else if(!expectedPropertyValueType.isInstance(propertyValue)) {
            errorMsg = errorPrefix + " is instance of " + propertyValue.getClass().getName() + ", " + expectedPropertyValueType.getName() + " was expected;";
        } else {
            if(propertyValue.getClass().isArray()) {
                if(Array.getLength(propertyValue) != Array.getLength(expectedPropertyValue)) {
                    errorMsg = errorPrefix + " has array value of size " + Array.getLength(propertyValue) + ", " + Array.getLength(expectedPropertyValue) + " was expected;";
                } else {
                    for(int i=0; i < Array.getLength(propertyValue); i++) {
                        if(!Array.get(propertyValue, i).equals(Array.get(expectedPropertyValue, i))) {
                            errorMsg = errorPrefix + " has array with "+i+"th element value " + Array.get(propertyValue, i).toString() + ", " + Array.get(expectedPropertyValue, i).toString() + " was expected;";
                        }
                    }
                }
            } else if (!propertyValue.equals(expectedPropertyValue)) {
                errorMsg = errorPrefix + " has value " + propertyValue + ", " + expectedPropertyValue.toString() + " was expected;";
            }
        }
        return errorMsg;
    }

    public void testUnidirectionalPersist() {
        String lastName = "testUnidirectionalPersist";

        // persist employees
        List<Employee> employeesPersisted = persistEmployeesWithUnidirectionalMappings(lastName);

        // clear cache
        clearCache();

        // read the persisted employees back
        EntityManager em = createEntityManager();
        List<Employee> employeesRead = em.createQuery("SELECT OBJECT(e) FROM XMLEmployee e WHERE e.lastName = '"+lastName+"'", Employee.class).getResultList();
        // while em is open, cache ServerSession that will be used later for verification
        DatabaseSessionImpl session = getDatabaseSession();
        closeEntityManager(em);

        // verify number persisted and read is the same
        if(employeesPersisted.size() != employeesRead.size()) {
            //clean-up
            deleteEmployeesWithUnidirectionalMappings(lastName);
            fail("Persisted " + employeesPersisted.size() + " employees, but read back " + employeesRead.size());
        }

        // verify that the persisted and read objects are equal
        StringBuilder errorMsg = new StringBuilder();
        for(int i=0; i<employeesPersisted.size(); i++) {
            for(int j=0; j<employeesRead.size(); j++) {
                if(employeesPersisted.get(i).getFirstName().equals(employeesRead.get(j).getFirstName())) {
                    if(!session.compareObjects(employeesPersisted.get(i), employeesRead.get(j))) {
                        errorMsg.append("Employee ").append(employeesPersisted.get(i).getFirstName()).append("  was not persisted correctly.");
                    }
                }
            }
        }

        // clean-up
        deleteEmployeesWithUnidirectionalMappings(lastName);

        // non-empty error message means the test has failed
        if(errorMsg.length() > 0) {
            fail(errorMsg.toString());
        }
    }

    public void testUnidirectionalUpdate() {
        String lastName = "testUnidirectionalUpdate";
        // em used for both persist and update
        EntityManager em = createEntityManager();
        // persist employees
        List<Employee> employeesPersisted = persistEmployeesWithUnidirectionalMappings(lastName, em);
        // update persisted employees:
        beginTransaction(em);
        try {
            // add a new dealer to the first employee
            employeesPersisted.get(0).addDealer(new Dealer("New", lastName));
            // remove a dealer from the second employee
            employeesPersisted.get(1).getDealers().remove(1);
            // move a dealer from the first to the second employee
            employeesPersisted.get(1).addDealer(employeesPersisted.get(0).getDealers().remove(0));
            commitTransaction(em);
        } finally {
            if(this.isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }

        // clear cache
        clearCache();

        em = createEntityManager();
        // read the updated employees back
        List<Employee> employeesRead = em.createQuery("SELECT OBJECT(e) FROM XMLEmployee e WHERE e.lastName = '"+lastName+"'", Employee.class).getResultList();
        // while em is open, cache ServerSession that will be used later for verification
        DatabaseSessionImpl session = getDatabaseSession();

        // verify number persisted and read is the same
        if(employeesPersisted.size() != employeesRead.size()) {
            // clean-up
            deleteEmployeesWithUnidirectionalMappings(lastName);
            fail("Updated " + employeesPersisted.size() + " employees, but read back " + employeesRead.size());
        }

        // verify that the persisted and read objects are equal
        beginTransaction(em);
        StringBuilder errorMsg = new StringBuilder();
        try{
            for(int i=0; i<employeesPersisted.size(); i++) {
                for(int j=0; j<employeesRead.size(); j++) {
                    Employee emp1 = em.find(Employee.class, employeesPersisted.get(i).getId());
                    Employee emp2 = em.find(Employee.class, employeesRead.get(j).getId());
                    if(emp1.getFirstName().equals(emp2.getFirstName())) {
                        if(!session.compareObjects(emp1, emp2)) {
                            errorMsg.append("Employee ").append(emp1.getFirstName()).append("  was not updated correctly.");
                        }
                    }
                }
            }
        } finally {
           if(this.isTransactionActive(em)) {
               rollbackTransaction(em);
              }
               closeEntityManager(em);
        }

        // clean-up
        deleteEmployeesWithUnidirectionalMappings(lastName);

        // non-empty error message means the test has failed
        if(errorMsg.length() > 0) {
            fail(errorMsg.toString());
        }
    }

    public void testUnidirectionalFetchJoin() {
        String lastName = "testUnidirectionalFetchJoin";

        // persist employees
        persistEmployeesWithUnidirectionalMappings(lastName);

        // clear cache
        clearCache();

        EntityManager em = createEntityManager();
        // read the persisted employees back - without fetch join
        List<Employee> employeesRead = em.createQuery("SELECT OBJECT(e) FROM XMLEmployee e WHERE e.lastName = '"+lastName+"'", Employee.class).getResultList();
        closeEntityManager(em);

        // clear cache
        clearCache();

        // read the persisted employees back - with fetch join.
        em = createEntityManager();
        List<Employee> employeesReadWithFetchJoin = em.createQuery("SELECT e FROM XMLEmployee e JOIN FETCH e.dealers WHERE e.lastName = '"+lastName+"'", Employee.class).getResultList();
        // while em is open, cache ServerSession that will be used later for verification
        DatabaseSessionImpl session = getDatabaseSession();

        closeEntityManager(em);

        // verify that the persisted and read employees are the same.
        // The comparison cascades to all references and requires the same state of indirection:
        // it fails in case an object has triggered indirection for particular attribute and compared object's indirection for this attribute is not triggered.
        // The expected result of join fetch query is Employee.dealers being triggered - so need to trigger it on the control collection (getDealers.size() does that);
        // also the expected result should have an object for each row returned - therefore number of inclusions of each Employee equals its dealers.size()
        List<Employee> employeesControl = new ArrayList<>();
        for(int i=0; i<employeesRead.size(); i++) {
            int nDialers = employeesRead.get(i).getDealers().size();
            for(int j=0; j<nDialers; j++) {
                employeesControl.add(employeesRead.get(i));
            }
        }
        String errorMsg = JoinedAttributeTestHelper.compareCollections(employeesControl, employeesReadWithFetchJoin, session.getClassDescriptor(Employee.class), session);

        // clean-up
        deleteEmployeesWithUnidirectionalMappings(lastName);

        // non-empty error message means the test has failed
        if(errorMsg.length() > 0) {
            fail(errorMsg);
        }
    }

    public void testUnidirectionalTargetLocking_AddRemoveTarget() {
        String lastName = "testUnidirectionalTargetLocking_ART";

        EntityManager em = createEntityManager();
        // persist employees
        List<Employee> employeesPersisted = persistEmployeesWithUnidirectionalMappings(lastName, em);

        // remove a dealer from the second employee:
        Dealer dealer;
        beginTransaction(em);
    try {
            Employee emp1 = em.find(Employee.class, employeesPersisted.get(1).getId());
            dealer = emp1.getDealers().remove(1);
            commitTransaction(em);
        } finally {
            if(this.isTransactionActive(em)) {
                rollbackTransaction(em);
                closeEntityManager(em);
            }
        }

        String errorMsg = "";

        // verify the version both in the cache and in the db
        int version2 = getVersion(em, dealer);
        if(version2 != 2) {
            errorMsg += "In the cache the removed dealer's version is " + version2 + " (2 was expected); ";
        }
        // add the dealer to the first employee:
        //version2 = getDealerVersionFromDB(em, dealer);
        beginTransaction(em);
        try {
            Dealer dealer2 = em.find(Dealer.class, dealer.getId());
            em.refresh(dealer2);
            version2 = getVersion(em, dealer2);
        } finally {
            if(this.isTransactionActive(em)) {
                rollbackTransaction(em);
            }
        }
        if(version2 != 2) {
            errorMsg += "In the db the removed dealer's version is " + version2 + " (2 was expected); ";
        }
        beginTransaction(em);
        Dealer dealer3;
        try {
            Employee emp2 = em.find(Employee.class, employeesPersisted.get(1).getId());
            dealer3 = em.find(Dealer.class, dealer.getId());
            emp2.getDealers().add(dealer3);
            commitTransaction(em);
        } finally {
            if(this.isTransactionActive(em)) {
                rollbackTransaction(em);
                closeEntityManager(em);
            }
        }

        // verify the version both in the cache and in the db
        int version3 = getVersion(em, dealer3);
        if(version3 != 3) {
            errorMsg += "In the cache the added dealer's version is " + version3 + " (3 was expected); ";
        }

        beginTransaction(em);
        try {
            Dealer dealer4 = em.find(Dealer.class, dealer3.getId());
            em.refresh(dealer4);
            version3 = getVersion(em, dealer4);
        } finally {
            if(this.isTransactionActive(em)) {
                rollbackTransaction(em);
            }
        }
        if(version3 != 3) {
            errorMsg += "In the db the added dealer's version is " + version3 + " (3 was expected)";
        }

        closeEntityManager(em);

        // clean-up
        deleteEmployeesWithUnidirectionalMappings(lastName);

        // non-empty error message means the test has failed
        if(errorMsg.length() > 0) {
            fail(errorMsg);
        }
    }

    public void testUnidirectionalTargetLocking_DeleteSource() {
        String lastName = "testUnidirectionalTargetLocking_DS";

        // persist employees (there should be two of them)
        List<Employee> persistedEmployees = persistEmployeesWithUnidirectionalMappings(lastName);
        // cache their dealers' ids
        ArrayList<Integer> dealersIds = new ArrayList<>();
        for(int i=0; i<persistedEmployees.size(); i++) {
            Employee emp = persistedEmployees.get(i);
            for(int j=0; j<emp.getDealers().size(); j++) {
                dealersIds.add(emp.getDealers().get(j).getId());
            }
        }

        // clear cache
        clearCache();

        EntityManager em = createEntityManager();
        beginTransaction(em);
        // read the persisted employees
        List<Employee> readEmployees = em.createQuery("SELECT OBJECT(e) FROM XMLEmployee e WHERE e.lastName = '"+lastName+"'", Employee.class).getResultList();

        // trigger indirection on the second employee's dealers
        readEmployees.get(1).getDealers().size();

        // delete the Employees (there should be two of them).

        try {
            for(Employee emp:  readEmployees) {
                em.remove(emp);
            }
            commitTransaction(em);
        } finally {
            if(this.isTransactionActive(em)) {
                rollbackTransaction(em);
                closeEntityManager(em);
            }
        }

        // find employees' dealers and verify their versions - all should be 2.
        beginTransaction(em);
        StringBuilder errorMsg = new StringBuilder();
        try{
            for(int i=0; i<dealersIds.size(); i++) {
                Dealer dealer = em.find(Dealer.class, dealersIds.get(i));

                // verify the version both in the cache and in the db
                int version2 = getVersion(em, dealer);
                if(version2 != 2) {
                    errorMsg.append("In the cache dealer ").append(dealer.getFirstName()).append("'s version is ").append(version2).append(" (2 was expected); ");
                }
                em.refresh(dealer);

                version2 = getVersion(em, dealer);
                if(version2 != 2) {
                    errorMsg.append("In the db dealer ").append(dealer.getFirstName()).append("'s version is ").append(version2).append(" (2 was expected); ");
                }
            }
        } finally {
           if(this.isTransactionActive(em)) {
               rollbackTransaction(em);
           }
        }

        closeEntityManager(em);

        // clean-up
        deleteEmployeesWithUnidirectionalMappings(lastName);

        // non-empty error message means the test has failed
        if(errorMsg.length() > 0) {
            fail(errorMsg.toString());
        }
    }

    protected int getVersion(EntityManager em, Dealer dealer) {
        List<Integer> pk = new Vector<>(1);
        pk.add(dealer.getId());

        DatabaseSessionImpl session = getDatabaseSession();
        return session.getDescriptor(Dealer.class).getOptimisticLockingPolicy().getWriteLockValue(dealer, pk, session);
    }

    protected List<Employee> createEmployeesWithUnidirectionalMappings(String lastName) {
        int n = 2;
        List<Employee> employees = new ArrayList<>(n);
        for(int i=0; i<n; i++) {
            Employee emp = new Employee();
            emp.setGivenName(Integer.toString(i+1));
            emp.setFamilyName(lastName);
            employees.add(emp);
            for(int j=0; j<n; j++) {
                Dealer dealer = new Dealer();
                dealer.setFirstName(emp.getFirstName() + "_" + (j + 1));
                dealer.setLastName(lastName);
                emp.addDealer(dealer);
                for(int k=0; k<n; k++) {
                    Customer customer = new Customer();
                    customer.setFirstName(dealer.getFirstName() + "_" + (k + 1));
                    customer.setLastName(lastName);
                    dealer.addCustomer(customer);
                }
            }
        }
        return employees;
    }

    protected List<Employee> persistEmployeesWithUnidirectionalMappings(String lastName) {
        EntityManager em = createEntityManager();
        try {
            return persistEmployeesWithUnidirectionalMappings(lastName, em);
        } finally {
            closeEntityManager(em);
        }
    }

    protected List<Employee> persistEmployeesWithUnidirectionalMappings(String lastName, EntityManager em) {
        List<Employee> employees = createEmployeesWithUnidirectionalMappings(lastName);
        beginTransaction(em);
        try {
            for(int i=0; i<employees.size(); i++) {
                em.persist(employees.get(i));
            }
            commitTransaction(em);
        } finally {
            if(this.isTransactionActive(em)) {
                rollbackTransaction(em);
            }
        }
        return employees;
    }

    protected void deleteEmployeesWithUnidirectionalMappings(String lastName) {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            em.createQuery("DELETE FROM XMLCustomer c WHERE c.lastName = '"+lastName+"'").executeUpdate();
            em.createQuery("DELETE FROM XMLDealer d WHERE d.lastName = '"+lastName+"'").executeUpdate();
            Query q = em.createQuery("SELECT e FROM XMLEmployee e WHERE e.lastName = '"+lastName+"'");
            for (Object oldData : q.getResultList()) {
                em.remove(oldData);
            }
            commitTransaction(em);
        } finally {
            if(this.isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            clearCache();
        }
    }

    public void testMustBeCompositeMember() {
        for(int i=1; i<=3; i++) {
            String puName = getComponentMemberPuName(i);
            try {
                //creation of the factory is allowed
                EntityManagerFactory emf = getEntityManagerFactory(puName);
                //creation of the manager itself is expected to fail
                EntityManager em = null;
                try {
                    em = emf.createEntityManager();
                } finally {
                    if (em != null) {
                        closeEntityManager(em);
                    }
                }
                if (i != 1) {
                    // "xml-composite-advanced-member_1" is not marked as mustBeCompositeMember - should work standalone, too.
                    fail("createEntityManager(" + puName +") succeeded - should have failed");
                }
            } catch (IllegalStateException ex){
                if (i == 1) {
                    fail("createEntityManager(" + puName +") failed - should have succeeded");
                } else {
                    // expected exception
                }
            } catch (Exception exWrong) {
                throw exWrong;
            } finally {
                closeEntityManagerFactory(puName);
            }
        }
    }

    /**
     * Verifies that attribute-types are correctly processed and used.
     */
/*    public void testAttributeTypeSpecifications() {
        EntityManager em = createEntityManager();

        try {
            beginTransaction(em);

            Loner loner = new Loner();
            Name name = new Name();
            name.firstName = "Joe";
            name.lastName = "Schmo";
            loner.setName(name);

            Confidant confidant = new Confidant();
            loner.addConfidant(confidant);

            loner.addCharacteristic("Keeps to himself");
            loner.addCharacteristic("Shy's away from conversation");

            em.persist(loner);

            Object lonerId = loner.getId();
            commitTransaction(em);

            clearCache();
            em.clear();
            Loner refreshedLoner = em.find(Loner.class, lonerId);
            assertTrue("Loner objects didn't match", getDatabaseSession().compareObjects(loner, refreshedLoner));
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            throw e;
        } finally {
            closeEntityManager(em);
        }
    }*/

    /**
     * Verifies that attribute-types and name access are correctly processed
     * and used.
     */
/*    public void testMockDynamicClassCRUD() {
        EntityManager em = createEntityManager();

        try {
            beginTransaction(em);

            // Cost
            Shovel shovel = new Shovel();
            shovel.setMy("cost", Double.valueOf(9.99));

            // Sections
            ShovelSections shovelSections = new ShovelSections();
            shovelSections.setMaterial("handle", MaterialType.Plastic);
            shovelSections.setMaterial("shaft", MaterialType.Wood);
            shovelSections.setMaterial("scoop", MaterialType.Plastic);
            shovel.setMy("sections", shovelSections);

            // Owner
            ShovelOwner shovelOwner = new ShovelOwner();
            shovelOwner.set("name", "Mr. Shovel");
            shovel.setMy("owner", shovelOwner);

            // Operators
            ShovelDigger shovelDigger1 = new ShovelDigger();
            shovelDigger1.set("name", "Digging Plebe 1");
            shovelDigger1.setX("shovel", shovel);

            ShovelDigger shovelDigger2 = new ShovelDigger();
            shovelDigger2.set("name", "Digging Plebe 2");
            shovelDigger2.setX("shovel", shovel);

            List<ShovelDigger> operators = new ArrayList<ShovelDigger>();
            operators.add(shovelDigger1);
            operators.add(shovelDigger2);
            shovel.setMy("operators", operators);

            // Projects
            ShovelProject shovelProject = new ShovelProject();
            shovelProject.set("description", "One lousy shovelling project");

            List<Shovel> shovels = new ArrayList<Shovel>();
            shovels.add(shovel);
            shovelProject.set("shovels", shovels);

            List<ShovelProject> projects = new ArrayList<ShovelProject>();
            projects.add(shovelProject);
            shovel.setMy("projects", projects);

            em.persist(shovel);

            // Grab id's for ease of lookup.
            Object shovelId = shovel.getMy("id");
            Object shovelOwnerId = shovelOwner.get("id");
            Object shovelDigger1Id = shovelDigger1.get("id");
            Object shovelDigger2Id = shovelDigger2.get("id");
            Object shovelProjectId = shovelProject.get("id");

            commitTransaction(em);

            clearCache();
            em.clear();

            Shovel refreshedShovel = em.find(Shovel.class, shovelId);
            assertTrue("Shovel didn't match after write/read", getDatabaseSession().compareObjects(shovel, refreshedShovel));

            // Do an update
            beginTransaction(em);

            em.merge(refreshedShovel);
            refreshedShovel.setMy("cost", Double.valueOf(7.99));

            commitTransaction(em);

            clearCache();
            em.clear();

            Shovel refreshedUpdatedShovel = em.find(Shovel.class, shovelId);
            assertTrue("Shovel didn't match after update", getDatabaseSession().compareObjects(refreshedShovel, refreshedUpdatedShovel));

            // Now delete it
            beginTransaction(em);
            em.merge(refreshedUpdatedShovel);
            em.remove(refreshedUpdatedShovel);
            commitTransaction(em);

            // Check what's left
            assertNull("Shovel wasn't removed", em.find(Shovel.class, shovelId));
            assertNull("Shovel owner wasn't removed", em.find(ShovelOwner.class, shovelOwnerId));
            assertNull("Shovel digger 1 wasn't removed", em.find(ShovelDigger.class, shovelDigger1Id));
            assertNull("Shovel digger 2 wasn't removed", em.find(ShovelDigger.class, shovelDigger2Id));
            assertNotNull("Shovel project was removed",  em.find(ShovelProject.class, shovelProjectId));

        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            throw e;
        } finally {
            closeEntityManager(em);
        }
    }*/

    /**
     * Fix for bug 247078: eclipselink-orm.xml schema should allow lob and
     * enumerated on version and id mappings
     */
/*    public void testEnumeratedPrimaryKeys(){
        EntityManager em = createEntityManager();

        try {
            beginTransaction(em);

            ViolationCode codeA = new ViolationCode();
            codeA.setId(ViolationCodeId.A);
            codeA.setDescription("Violation A");
            em.persist(codeA);

            ViolationCode codeB = new ViolationCode();
            codeB.setId(ViolationCodeId.B);
            codeB.setDescription("Violation B");
            em.persist(codeB);

            ViolationCode codeC = new ViolationCode();
            codeC.setId(ViolationCodeId.C);
            codeC.setDescription("Violation C");
            em.persist(codeC);

            ViolationCode codeD = new ViolationCode();
            codeD.setId(ViolationCodeId.D);
            codeD.setDescription("Violation D");
            em.persist(codeD);

            Violation violation = new Violation();
            violation.setId(ViolationID.V1);
            violation.getViolationCodes().add(codeA);
            violation.getViolationCodes().add(codeC);
            violation.getViolationCodes().add(codeD);
            em.persist(violation);

            commitTransaction(em);

            // Force the read to hit the database and make sure the violation is read back.
            clearCache();
            em.clear();
            Violation refreshedViolation = em.find(Violation.class, violation.getId());
            assertNotNull("Unable to read back the violation", refreshedViolation);
            assertTrue("Violation object did not match after refresh", getDatabaseSession().compareObjects(violation, refreshedViolation));
        } catch (Exception e) {
            fail("An error occurred: " + e.getMessage());
        } finally {
            closeEntityManager(em);
        }
    }*/
}
