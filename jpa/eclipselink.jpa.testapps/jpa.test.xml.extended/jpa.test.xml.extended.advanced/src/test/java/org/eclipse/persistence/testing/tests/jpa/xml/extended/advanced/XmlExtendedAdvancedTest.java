/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
//     09/23/2008-1.1 Guy Pelletier
//       - 241651: JPA 2.0 Access Type support
//     03/08/2010-2.1 Guy Pelletier
//       - 303632: Add attribute-type for mapping attributes to EclipseLink-ORM
//     03/29/2010-2.1 Guy Pelletier
//       - 267217: Add Named Access Type to EclipseLink-ORM
//     04/09/2010-2.1 Guy Pelletier
//       - 307050: Add defaults for access methods of a VIRTUAL access type
//     06/16/2010-2.2 Guy Pelletier
//       - 247078: eclipselink-orm.xml schema should allow lob and enumerated on version and id mappings
//     10/15/2010-2.2 Guy Pelletier
//       - 322008: Improve usability of additional criteria applied to queries at the session/EM
//     07/03/2011-2.3.1 Guy Pelletier
//       - 348756: m_cascadeOnDelete boolean should be changed to Boolean
package org.eclipse.persistence.testing.tests.jpa.xml.extended.advanced;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.SelectedFieldsLockingPolicy;
import org.eclipse.persistence.descriptors.invalidation.CacheInvalidationPolicy;
import org.eclipse.persistence.descriptors.invalidation.TimeToLiveCacheInvalidationPolicy;
import org.eclipse.persistence.internal.descriptors.OptimisticLockingPolicy;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.mappings.converters.ObjectTypeConverter;
import org.eclipse.persistence.queries.DoesExistQuery;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.Address;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.Bungalow;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.Confidant;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.Customer;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.Dealer;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.Department;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.EmploymentPeriod;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.LargeProject;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.Loner;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.ModelExamples;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.Name;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.PhoneNumber;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.Project;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.ReadOnlyClass;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.Shovel;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.ShovelDigger;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.ShovelOwner;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.ShovelProject;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.ShovelSections;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.ShovelSections.MaterialType;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.SmallProject;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.Violation;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.Violation.ViolationID;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.ViolationCode;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.ViolationCode.ViolationCodeId;
import org.eclipse.persistence.testing.tests.jpa.xml.advanced.XmlAdvancedTest;

import java.lang.reflect.Array;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * JUnit test case(s) for the TopLink EntityMappingsXMLProcessor.
 */
public class XmlExtendedAdvancedTest extends XmlAdvancedTest {
    private static Integer employeeId, extendedEmployeeId;
    private static long visa = 1234567890;
    private static long amex = 1987654321;
    private static long diners = 1192837465;
    private static long mastercard = 1647382910;
    private static long rbc = 4783;
    private static long scotia = 8732;
    private static long td = 839362;
    private static long cibc = 948274;

    public XmlExtendedAdvancedTest() {
        super();
    }

    public XmlExtendedAdvancedTest(String name) {
        super(name);
    }

    @Override
    public String getPersistenceUnitName() {
        return "extended-advanced";
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Advanced Model - extended-advanced");
        suite.addTest(new XmlExtendedAdvancedTest("testSetup"));
        suite.addTest(new XmlExtendedAdvancedTest("testCreateEmployee"));
        suite.addTest(new XmlExtendedAdvancedTest("testReadEmployee"));
        suite.addTest(new XmlExtendedAdvancedTest("testNamedNativeQueryOnAddress"));
        suite.addTest(new XmlExtendedAdvancedTest("testNamedQueryOnEmployee"));
        suite.addTest(new XmlExtendedAdvancedTest("testUpdateEmployee"));
        suite.addTest(new XmlExtendedAdvancedTest("testRefreshNotManagedEmployee"));
        suite.addTest(new XmlExtendedAdvancedTest("testRefreshRemovedEmployee"));
        suite.addTest(new XmlExtendedAdvancedTest("testDeleteEmployee"));
        suite.addTest(new XmlExtendedAdvancedTest("testUnidirectionalPersist"));
        suite.addTest(new XmlExtendedAdvancedTest("testUnidirectionalUpdate"));
        suite.addTest(new XmlExtendedAdvancedTest("testUnidirectionalFetchJoin"));
        suite.addTest(new XmlExtendedAdvancedTest("testUnidirectionalTargetLocking_AddRemoveTarget"));
        suite.addTest(new XmlExtendedAdvancedTest("testUnidirectionalTargetLocking_DeleteSource"));
        suite.addTest(new XmlExtendedAdvancedTest("testXMLEntityMappingsWriteOut"));
        suite.addTest(new XmlExtendedAdvancedTest("testInheritFromNonMapped"));

        suite.addTest(new XmlExtendedAdvancedTest("testSexObjectTypeConverterDefaultValue"));
        suite.addTest(new XmlExtendedAdvancedTest("testExistenceCheckingSetting"));
        suite.addTest(new XmlExtendedAdvancedTest("testReadOnlyClassSetting"));
        suite.addTest(new XmlExtendedAdvancedTest("testEmployeeChangeTrackingPolicy"));
        suite.addTest(new XmlExtendedAdvancedTest("testAddressChangeTrackingPolicy"));
        suite.addTest(new XmlExtendedAdvancedTest("testPhoneNumberChangeTrackingPolicy"));
        suite.addTest(new XmlExtendedAdvancedTest("testProjectChangeTrackingPolicy"));
        suite.addTest(new XmlExtendedAdvancedTest("testJoinFetchSetting"));
        suite.addTest(new XmlExtendedAdvancedTest("testEmployeeOptimisticLockingSettings"));
        suite.addTest(new XmlExtendedAdvancedTest("testEmployeeCacheSettings"));
        suite.addTest(new XmlExtendedAdvancedTest("testProjectOptimisticLockingSettings"));
        suite.addTest(new XmlExtendedAdvancedTest("testExtendedEmployee"));
        suite.addTest(new XmlExtendedAdvancedTest("testGiveExtendedEmployeeASexChange"));
        suite.addTest(new XmlExtendedAdvancedTest("testNamedStoredProcedureQuery"));
        suite.addTest(new XmlExtendedAdvancedTest("testNamedStoredProcedureQueryInOut"));
        suite.addTest(new XmlExtendedAdvancedTest("testMethodBasedTransformationMapping"));
        suite.addTest(new XmlExtendedAdvancedTest("testClassBasedTransformationMapping"));
        suite.addTest(new XmlExtendedAdvancedTest("testClassInstanceConverter"));
        suite.addTest(new XmlExtendedAdvancedTest("testProperty"));
        suite.addTest(new XmlExtendedAdvancedTest("testAccessorMethods"));
        suite.addTest(new XmlExtendedAdvancedTest("testIfMultipleBasicCollectionMappingsExistForEmployeeResponsibilites"));

        // These are dynamic persistence tests.
        suite.addTest(new XmlExtendedAdvancedTest("testAttributeTypeSpecifications"));
        suite.addTest(new XmlExtendedAdvancedTest("testMockDynamicClassCRUD"));

        suite.addTest(new XmlExtendedAdvancedTest("testEnumeratedPrimaryKeys"));

        suite.addTest(new XmlExtendedAdvancedTest("testDeleteAll"));

        return suite;

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
     * Verifies a default object type value is set from metadata processing.
     */
    public void testSexObjectTypeConverterDefaultValue() {
        ServerSession session = getPersistenceUnitServerSession();

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
        ServerSession session = getPersistenceUnitServerSession();

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
        ServerSession session = getPersistenceUnitServerSession();

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
    public void testReadOnlyClassSetting() {
        ServerSession session = getPersistenceUnitServerSession();
        ClassDescriptor descriptor = session.getDescriptor(ReadOnlyClass.class);

        assertNotNull("ReadOnlyClass descriptor was not found in the PU [" + getPersistenceUnitName() + "]", descriptor);
        assertTrue("ReadOnlyClass descriptor is not set to read only.", descriptor.shouldBeReadOnly());
    }

    /**
     * Verifies that the change tracking metadata is correctly processed.
     * Employee has an AUTO setting, but has EAGER collections and transformation mapping
     * so should not be change tracked.
     */
    public void testEmployeeChangeTrackingPolicy() {
        ServerSession session = getPersistenceUnitServerSession();
        ClassDescriptor descriptor = session.getDescriptor(Employee.class);

        assertNotNull("Employee descriptor was not found in the PU [" + getPersistenceUnitName() + "]", descriptor);
        assertFalse("Employee descriptor has incorrect object change policy", descriptor.getObjectChangePolicy().isAttributeChangeTrackingPolicy());
    }

    /**
     * Verifies that the change tracking metadata is correctly processed.
     */
    public void testAddressChangeTrackingPolicy() {
        ServerSession session = getPersistenceUnitServerSession();
        ClassDescriptor descriptor = session.getDescriptor(Address.class);

        assertNotNull("Address descriptor was not found in the PU [" + getPersistenceUnitName() + "]", descriptor);
        assertTrue("Address descriptor has incorrect object change policy", descriptor.getObjectChangePolicyInternal().isDeferredChangeDetectionPolicy());
    }

    /**
     * Verifies that the change tracking metadata is correctly processed.
     */
    public void testPhoneNumberChangeTrackingPolicy() {
        if (!JUnitTestCase.isWeavingEnabled(getPersistenceUnitName())) {
            return;
        }

        ServerSession session = getPersistenceUnitServerSession();
        ClassDescriptor descriptor = session.getDescriptor(PhoneNumber.class);

        assertNotNull("PhoneNumber descriptor was not found in the PU [" + getPersistenceUnitName() + "]", descriptor);
        assertTrue("PhoneNumber descriptor has incorrect object change policy", descriptor.getObjectChangePolicy().isAttributeChangeTrackingPolicy());
    }

    /**
     * Verifies that the change tracking metadata is correctly processed.
     */
    public void testProjectChangeTrackingPolicy() {
        if (!JUnitTestCase.isWeavingEnabled(getPersistenceUnitName())) {
            return;
        }

        ServerSession session = getPersistenceUnitServerSession();
        ClassDescriptor descriptor = session.getDescriptor(Project.class);

        assertNotNull("Project descriptor was not found in the PU [" + getPersistenceUnitName() + "]", descriptor);
        assertTrue("Project descriptor has incorrect object change policy", descriptor.getObjectChangePolicy().isObjectChangeTrackingPolicy());
    }

    /**
     * Verifies that the join-fetch setting was read correctly from XML.
     */
    public void testJoinFetchSetting() {
        ServerSession session = getPersistenceUnitServerSession();
        ClassDescriptor descriptor = session.getDescriptor(Employee.class);

        if (descriptor == null) {
            fail("Employee descriptor was not found in the PU [" + getPersistenceUnitName() + "]");
        } else if (((ForeignReferenceMapping)descriptor.getMappingForAttributeName("address")).getJoinFetch() != ForeignReferenceMapping.OUTER_JOIN) {
            fail("join-fetch setting from XML was not read correctly for Employee's address.");
        }
    }

    /**
     * Verifies that the optimistic-locking settings were read correctly from XML.
     */
    public void testEmployeeOptimisticLockingSettings() {
        ServerSession session = getPersistenceUnitServerSession();
        ClassDescriptor descriptor = session.getDescriptor(Employee.class);

        if (descriptor == null) {
            fail("Employee descriptor was not found in the PU [" + getPersistenceUnitName() + "]");
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
        ServerSession session = getPersistenceUnitServerSession();
        ClassDescriptor descriptor = session.getDescriptor(Employee.class);

        if (descriptor == null) {
            fail("A descriptor for the Employee alias was not found in the PU [" + getPersistenceUnitName() + "]");
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
        ServerSession session = getPersistenceUnitServerSession();
        ClassDescriptor descriptor = session.getDescriptor(Project.class);

        if (descriptor == null) {
            fail("Project descriptor was not found in the PU [" + getPersistenceUnitName() + "]");
        } else {
            OptimisticLockingPolicy policy = descriptor.getOptimisticLockingPolicy();

            if (policy instanceof SelectedFieldsLockingPolicy) {
                List<DatabaseField> lockFields = ((SelectedFieldsLockingPolicy) policy).getLockFields();

                if (lockFields.size() != 1) {
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

    public void testContainsRemovedEmployee() {
        // find an existing or create a new Employee
        String firstName = "testContainsRemovedEmployee";
        Employee emp;
        EntityManager em = createEntityManager();
        List<Employee> result = em.createQuery("SELECT OBJECT(e) FROM XMLEmployee e WHERE e.firstName = '"+firstName+"'", Employee.class).getResultList();
        if(!result.isEmpty()) {
            emp = result.get(0);
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
        List<Employee> result = em.createQuery("SELECT OBJECT(e) FROM XMLEmployee e WHERE e.firstName = '"+firstName+"'", Employee.class).getResultList();
        if(!result.isEmpty()) {
            emp = result.get(0);
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
        List<?> employees = em.createQuery("SELECT object(e) FROM XMLEmployee e where e.firstName = substring(:p1, :p2, :p3)").
            setParameter("p1", firstName).
            setParameter("p2", firstIndex).
            setParameter("p3", lastIndex).
            getResultList();

        // clean up
        beginTransaction(em);
        try{
            em.createQuery("DELETE FROM XMLEmployee e WHERE e.firstName = '"+firstName+"'").executeUpdate();
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
        clearCache();
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
            if (isOnServer()) {
                employee = em.find(Employee.class, employee.getId());
            }
            em.remove(employee);
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
        getPersistenceUnitServerSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        add = em.find(Address.class, assignedSequenceNumber);

        assertTrue("Did not correctly persist a mapping using a class-instance converter", (add.getType() instanceof Bungalow));

        beginTransaction(em);
        if (isOnServer()) {
            add = em.find(Address.class, assignedSequenceNumber);
        }
        em.remove(add);
        commitTransaction(em);
    }


    /**
     * Tests Property and Properties annotations
     */
    public void testProperty() {
        EntityManager em = createEntityManager();
        ClassDescriptor descriptor = (em.unwrap(org.eclipse.persistence.sessions.Session.class)).getDescriptorForAlias("XMLEmployee");
        ClassDescriptor aggregateDescriptor = (em.unwrap(org.eclipse.persistence.sessions.Session.class)).getDescriptor(EmploymentPeriod.class);
        closeEntityManager(em);

        String errorMsg = "";

        if (descriptor == null) {
            errorMsg += " Descriptor for XMLEmployee alias was not found;";
        }
        if (aggregateDescriptor == null) {
            errorMsg += " Descriptor for EmploymentPeriod.class was not found;";
        }
        if(errorMsg.length() > 0) {
            fail(errorMsg);
        }

        // verify properties set on Employee instance
        errorMsg += verifyPropertyValue(descriptor, "entityName", String.class, "XMLEmployee");
        errorMsg += verifyPropertyValue(descriptor, "entityIntegerProperty", Integer.class, 1);
        errorMsg += verifyPropertyValue(descriptor, "ToBeOverriddenByXml", Boolean.class, Boolean.TRUE);
        errorMsg += verifyPropertyValue(descriptor, "ToBeProcessed", Boolean.class, Boolean.TRUE);

        // each attribute of Employee was assigned a property attributeName with the value attribute name.
        for(DatabaseMapping mapping : descriptor.getMappings()) {
            errorMsg += verifyPropertyValue(mapping, "attributeName", String.class, mapping.getAttributeName());
        }

        // attribute m_lastName has many properties of different types
        DatabaseMapping mapping = descriptor.getMappingForAttributeName("lastName");
        errorMsg += verifyPropertyValue(mapping, "BooleanProperty", Boolean.class, Boolean.TRUE);
        errorMsg += verifyPropertyValue(mapping, "ByteProperty", Byte.class, (byte) 1);
        errorMsg += verifyPropertyValue(mapping, "CharacterProperty", Character.class, 'A');
        errorMsg += verifyPropertyValue(mapping, "DoubleProperty", Double.class, 1.0);
        errorMsg += verifyPropertyValue(mapping, "FloatProperty", Float.class, 1F);
        errorMsg += verifyPropertyValue(mapping, "IntegerProperty", Integer.class, 1);
        errorMsg += verifyPropertyValue(mapping, "LongProperty", Long.class, 1L);
        errorMsg += verifyPropertyValue(mapping, "ShortProperty", Short.class, (short) 1);
        errorMsg += verifyPropertyValue(mapping, "BigDecimalProperty", java.math.BigDecimal.class, java.math.BigDecimal.ONE);
        errorMsg += verifyPropertyValue(mapping, "BigIntegerProperty", java.math.BigInteger.class, java.math.BigInteger.ONE);
        errorMsg += verifyPropertyValue(mapping, "TimeProperty", java.sql.Time.class, Helper.timeFromString("13:59:59"));
        errorMsg += verifyPropertyValue(mapping, "TimeStampProperty", java.sql.Timestamp.class, Helper.timestampFromString("2008-04-10 13:59:59"));
        errorMsg += verifyPropertyValue(mapping, "DateProperty", Date.class, Helper.dateFromString("2008-04-10"));

        errorMsg += verifyPropertyValue(mapping, "ToBeIgnored", null, null);

        // verify property set on EmploymentPeriod embeddable
        errorMsg += verifyPropertyValue(aggregateDescriptor, "embeddableClassName", String.class, "EmploymentPeriod");
        errorMsg += verifyPropertyValue(aggregateDescriptor, "ToBeOverriddenByXml", Boolean.class, Boolean.TRUE);
        errorMsg += verifyPropertyValue(aggregateDescriptor, "ToBeProcessed", Boolean.class, Boolean.TRUE);

        if(errorMsg.length() > 0) {
            fail(errorMsg);
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
            em.createQuery("DELETE FROM XMLAdvancedCustomer c WHERE c.lastName = '"+lastName+"'").executeUpdate();
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

    /**
     * Verifies that attribute-types are correctly processed and used.
     */
    public void testAttributeTypeSpecifications() {
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
            assertTrue("Loner objects didn't match", getPersistenceUnitServerSession().compareObjects(loner, refreshedLoner));
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            throw e;
        } finally {
            closeEntityManager(em);
        }
    }

    /**
     * Verifies that attribute-types and name access are correctly processed
     * and used.
     */
    public void testMockDynamicClassCRUD() {
        EntityManager em = createEntityManager();

        try {
            beginTransaction(em);

            // Cost
            Shovel shovel = new Shovel();
            shovel.setMy("cost", 9.99);

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

            List<ShovelDigger> operators = new ArrayList<>();
            operators.add(shovelDigger1);
            operators.add(shovelDigger2);
            shovel.setMy("operators", operators);

            // Projects
            ShovelProject shovelProject = new ShovelProject();
            shovelProject.set("description", "One lousy shovelling project");

            List<Shovel> shovels = new ArrayList<>();
            shovels.add(shovel);
            shovelProject.set("shovels", shovels);

            List<ShovelProject> projects = new ArrayList<>();
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
            assertTrue("Shovel didn't match after write/read", getPersistenceUnitServerSession().compareObjects(shovel, refreshedShovel));

            // Do an update
            beginTransaction(em);

            refreshedShovel.setMy("cost", 7.99);
            em.merge(refreshedShovel);

            commitTransaction(em);

            clearCache();
            em.clear();

            Shovel refreshedUpdatedShovel = em.find(Shovel.class, shovelId);
            assertTrue("Shovel didn't match after update", equalsShovelBasic(refreshedShovel, refreshedUpdatedShovel));

            // Now delete it
            beginTransaction(em);
            refreshedUpdatedShovel = em.merge(refreshedUpdatedShovel);
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
    }

    private boolean equalsShovelBasic(Shovel shovel1, Shovel shovel2) {
        return Objects.equals(shovel1.getMy("id"), shovel2.getMy("id"))
                && equalsShovelSections((ShovelSections)shovel1.getMy("sections"), (ShovelSections)shovel2.getMy("sections"))
                && Objects.equals(shovel1.getMy("cost"), shovel2.getMy("cost"));
    }

    private boolean equalsShovelSections(ShovelSections shovelSections1, ShovelSections shovelSections2) {
        return Objects.equals(shovelSections1.getMaterial("handle"), shovelSections2.getMaterial("handle"))
                && Objects.equals(shovelSections1.getMaterial("shaft"), shovelSections2.getMaterial("shaft"))
                && Objects.equals(shovelSections1.getMaterial("scoop"), shovelSections2.getMaterial("scoop"));
    }

    /**
     * Fix for bug 247078: eclipselink-orm.xml schema should allow lob and
     * enumerated on version and id mappings
     */
    public void testEnumeratedPrimaryKeys(){
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
            assertTrue("Violation object did not match after refresh", getPersistenceUnitServerSession().compareObjects(violation, refreshedViolation));
        } catch (Exception e) {
            fail("An error occurred: " + e.getMessage());
        } finally {
            closeEntityManager(em);
        }
    }

    public void testDeleteAll(){
        Map<Class<?>, ClassDescriptor> descriptors = getPersistenceUnitServerSession().getDescriptors();
        ClassDescriptor descriptor = descriptors.get(Department.class);
        OneToManyMapping mapping = (OneToManyMapping)descriptor.getMappingForAttributeName("equipment");
        assertFalse("<delete-all> xml did not work correctly", mapping.mustDeleteReferenceObjectsOneByOne());
    }

}
