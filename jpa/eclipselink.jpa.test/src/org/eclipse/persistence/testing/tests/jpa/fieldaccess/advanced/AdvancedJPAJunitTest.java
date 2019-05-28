/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
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
//     03/03/2010 - 2.1 Michael O'Brien
//       - 302316: clear the object cache when testing stored procedure returns on SQLServer
//         to avoid false positives visible only when debugging in DatabaseCall.buildOutputRow()
//       - 260263: SQLServer 2005/2008 requires stored procedure creation select clause variable and column name matching
//     05/03/2009-1.2.1 Guy Pelletier
//       - 307547:  Exception in order by clause after migrating to eclipselink 1.2 release
package org.eclipse.persistence.testing.tests.jpa.fieldaccess.advanced;

import java.util.Collection;

import javax.persistence.LockModeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import junit.framework.*;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.internal.jpa.EJBQueryImpl;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.Address;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.Department;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.EmployeePopulator;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.Equipment;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.EquipmentCode;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.GoldBuyer;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.PhoneNumber;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.LargeProject;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.Project;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.SmallProject;

/**
 * This test suite tests EclipseLink JPA annotations extensions.
 */
public class AdvancedJPAJunitTest extends JUnitTestCase {
    private static int empId;
    private static int penelopeId;
    private static int deptId;
    private static int buyerId;
    private static long visa = 1234567890;
    private static long amex = 1987654321;
    private static long diners = 1192837465;
    private static long mastercard = 1647382910;
    private static String newResponsibility = "The most useless responsibility ever.";

    public AdvancedJPAJunitTest() {
        super();
    }

    public AdvancedJPAJunitTest(String name) {
        super(name);
    }

    public void setUp() {
        super.setUp();
        clearCache("fieldaccess");
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("AdvancedJPAJunitTest (fieldaccess)");
        suite.addTest(new AdvancedJPAJunitTest("testSetup"));

        suite.addTest(new AdvancedJPAJunitTest("testJoinFetchAnnotation"));
        suite.addTest(new AdvancedJPAJunitTest("testVerifyEmployeeCustomizerSettings"));
        suite.addTest(new AdvancedJPAJunitTest("testVerifyEmployeeManagerMappings"));

        suite.addTest(new AdvancedJPAJunitTest("testUpdateEmployee"));
        suite.addTest(new AdvancedJPAJunitTest("testVerifyUpdatedEmployee"));

        suite.addTest(new AdvancedJPAJunitTest("testCreateNewBuyer"));
        suite.addTest(new AdvancedJPAJunitTest("testVerifyNewBuyer"));
        suite.addTest(new AdvancedJPAJunitTest("testBuyerOptimisticLocking"));

        suite.addTest(new AdvancedJPAJunitTest("testGiveFredAnObjectTypeConverterChange"));
        suite.addTest(new AdvancedJPAJunitTest("testUpdatePenelopesPhoneNumberStatus"));
        suite.addTest(new AdvancedJPAJunitTest("testRemoveJillWithPrivateOwnedPhoneNumbers"));

        suite.addTest(new AdvancedJPAJunitTest("testCreateNewEquipment"));
        suite.addTest(new AdvancedJPAJunitTest("testAddNewEquipmentToDepartment"));
        suite.addTest(new AdvancedJPAJunitTest("testRemoveDepartmentWithPrivateOwnedEquipment"));
        suite.addTest(new AdvancedJPAJunitTest("testUpdateReadOnlyEquipmentCode"));

        suite.addTest(new AdvancedJPAJunitTest("testNamedStoredProcedureQuery"));
        suite.addTest(new AdvancedJPAJunitTest("testNamedStoredProcedureQueryInOut"));
        suite.addTest(new AdvancedJPAJunitTest("testNamedStoredProcedureQueryWithRawData"));
        suite.addTest(new AdvancedJPAJunitTest("testTransientConstructorSetFields"));
        suite.addTest(new AdvancedJPAJunitTest("testPessimisticLockingNamedQuery"));

        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        ServerSession session = JUnitTestCase.getServerSession("fieldaccess");
        new AdvancedTableCreator().replaceTables(session);
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

        clearCache("fieldaccess");
    }

    /** test for bug 292385 - Transient fields not initialized for Entities */
    public void testTransientConstructorSetFields(){
        EntityManager em = createEntityManager("fieldaccess");

        Project project = new Project();
        LargeProject lproject = new LargeProject();
        SmallProject sproject = new SmallProject();
        project.setName("TCSetFieldsProject");
        lproject.setName("TCSetFieldsLProject");
        sproject.setName("TCSetFieldsSProject");

        beginTransaction(em);
        project = em.merge(project);
        lproject = em.merge(lproject);
        sproject = em.merge(sproject);
        this.assertTrue("Project's default constructor wasn't used to create merged entity", project.getFieldOnlySetThroughConstructor()==1);
        this.assertTrue("LargeProject's default constructor wasn't used to create merged entity", lproject.getFieldOnlySetThroughConstructor()==2);
        this.assertTrue("Project's default constructor wasn't used to create merged SmallProject", sproject.getFieldOnlySetThroughConstructor()==1);

        try{
            em.flush();
            this.commitTransaction(em);
            closeEntityManager(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            closeEntityManager(em);
            // Re-throw exception to ensure stacktrace appears in test result.
            throw e;
        }
        em = createEntityManager("fieldaccess");

        project = em.find(Project.class, project.getId());
        lproject = em.find(LargeProject.class, lproject.getId());
        sproject = em.find(SmallProject.class, sproject.getId());

        closeEntityManager(em);

        this.assertTrue("Project's default constructor wasn't used to create managed entity", project.getFieldOnlySetThroughConstructor()==1);
        this.assertTrue("LargeProject's default constructor wasn't used to create managed entity", lproject.getFieldOnlySetThroughConstructor()==2);
        this.assertTrue("Project's default constructor wasn't used to create managed SmallProject", sproject.getFieldOnlySetThroughConstructor()==1);


    }

    /**
     * Verifies that settings from the EmployeeCustomizer have been set.
     */
    public void testVerifyEmployeeCustomizerSettings() {
        EntityManager em = createEntityManager("fieldaccess");

        ClassDescriptor descriptor = getServerSession("fieldaccess").getDescriptorForAlias("Employee");

        if (descriptor == null) {
            fail("A descriptor for the Employee alias was not found.");
        } else {
            assertFalse("Disable cache hits was true. Customizer should have made it false.", descriptor.shouldDisableCacheHits());
        }

        closeEntityManager(em);
    }

    /**
     * Bug 307547: Verifies that a mapping is not omitted from the descriptor after metadata processing.
     */
    public void testVerifyEmployeeManagerMappings() {
        EntityManager em = createEntityManager("fieldaccess");

        ClassDescriptor descriptor = getServerSession("fieldaccess").getDescriptorForAlias("Employee");

        if (descriptor == null) {
            fail("A descriptor for the Employee alias was not found.");
        } else {
            assertNotNull("The isManager mapping was not found on the Employee descriptor.", descriptor.getMappingForAttributeName("isManager"));
            assertNotNull("The getManager mapping was not found on the Employee descriptor.", descriptor.getMappingForAttributeName("getManager"));
            assertNotNull("The setManager mapping was not found on the Employee descriptor.", descriptor.getMappingForAttributeName("setManager"));
        }

        closeEntityManager(em);
    }

    /**
     * Verifies that the join-fetch annotation was read correctly.
     */
    public void testJoinFetchAnnotation() {
        ServerSession session = JUnitTestCase.getServerSession("fieldaccess");
        ClassDescriptor descriptor = session.getDescriptor(Employee.class);
        if (((ForeignReferenceMapping)descriptor.getMappingForAttributeName("department")).getJoinFetch() != ForeignReferenceMapping.OUTER_JOIN) {
            fail("JoinFetch annotation not read correctly for Employee.department.");
        }
    }

    /**
     * Tests:
     * - BasicCollection mapping
     * - Serialized Basic of type EnumSet.
     * - BasicCollection that uses an Enum converter (by detection).
     */
    public void testUpdateEmployee() {
        EntityManager em = createEntityManager("fieldaccess");
        beginTransaction(em);

        try {
            EJBQueryImpl query = (EJBQueryImpl) em.createNamedQuery("findAllFieldAccessSQLEmployees");
            Collection<Employee> employees = query.getResultCollection();

            if (employees.isEmpty()) {
                fail("No Employees were found. Test requires at least one Employee to be created in the EmployeePopulator.");
            } else {
                Employee emp = employees.iterator().next();
                emp.addResponsibility(newResponsibility);
                emp.setMondayToFridayWorkWeek();
                empId = emp.getId();
                commitTransaction(em);
            }
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
     * Verifies:
     * - a BasicCollection mapping.
     * - a BasicCollection that uses an Enum converter (by detection).
     */
    public void testVerifyUpdatedEmployee() {
        EntityManager em = createEntityManager("fieldaccess");
        beginTransaction(em);

        try {
            Employee emp = em.find(Employee.class, empId);

            assertNotNull("The updated employee was not found.", emp);

            boolean found = false;
            for (String responsibility : (Collection<String>) emp.getResponsibilities()) {
                if (responsibility.equals(newResponsibility)) {
                    found = true;
                    break;
                }
            }
            commitTransaction(em);
            assertTrue("The new responsibility was not added.", found);
            assertTrue("The basic collection using enums was not persisted correctly.", emp.worksMondayToFriday());
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
     * Tests:
     * - BasicMap mapping with a TypeConverter on the map value and an
     *   ObjectTypeConverter on the map key.
     * - Basic with a custom converter
     * - Serialized Basic of type EnumSet.
     */
    public void testCreateNewBuyer() {
        EntityManager em = createEntityManager("fieldaccess");
        beginTransaction(em);

        try {
            GoldBuyer buyer = new GoldBuyer();

            buyer.setName("Guy Pelletier");
            buyer.setGender("Made of testosterone");
            buyer.setDescription("Loves to spend");
            buyer.addVisa(visa);
            buyer.addAmex(amex);
            buyer.addDinersClub(diners);
            buyer.addMastercard(mastercard);
            buyer.setSaturdayToSundayBuyingDays();
            em.persist(buyer);
            commitTransaction(em);
            buyerId = buyer.getId();
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
     * Verifies:
     * - BasicMap mapping with a TypeConverter on the map value and an
     *   ObjectTypeConverter on the map key.
     * - Basic with a custom converter
     * - Serialized Basic of type EnumSet.
     */
    public void testVerifyNewBuyer() {
        EntityManager em = createEntityManager("fieldaccess");
        beginTransaction(em);

        try {
            GoldBuyer buyer = em.find(GoldBuyer.class, buyerId);

            assertNotNull("The new buyer was not found", buyer);
            assertTrue("Gender was not persisted correctly.", buyer.isMale());
            assertTrue("Visa card did not persist correctly.", buyer.hasVisa(visa));
            assertTrue("Amex card did not persist correctly.", buyer.hasAmex(amex));
            assertTrue("Diners Club card did not persist correctly.", buyer.hasDinersClub(diners));
            assertTrue("Mastercard card did not persist correctly.", buyer.hasMastercard(mastercard));
            assertTrue("The serialized enum set was not persisted correctly.", buyer.buysSaturdayToSunday());
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
    }

    /**
     * Tests an OptimisticLocking policy set on Buyer.
     */
    public void testBuyerOptimisticLocking() {
        // Cannot create parallel entity managers in the server.
        if (isOnServer()) {
            return;
        }

        EntityManager em1 = createEntityManager("fieldaccess");
        EntityManager em2 = createEntityManager("fieldaccess");
        em1.getTransaction().begin();
        em2.getTransaction().begin();
        RuntimeException caughtException = null;
        try {
            GoldBuyer buyer1 = em1.find(GoldBuyer.class, buyerId);
            GoldBuyer buyer2 = em2.find(GoldBuyer.class, buyerId);

            buyer1.setName("Geezer");
            buyer2.setName("Guyzer");
            // Uses field locking, so need to update version.
            buyer1.setVersion(buyer1.getVersion() + 1);
            buyer2.setVersion(buyer2.getVersion() + 1);

            em1.getTransaction().commit();
            em2.getTransaction().commit();

            em1.close();
            em2.close();
        } catch (RuntimeException e) {
            caughtException = e;
            if (em1.getTransaction().isActive()){
                em1.getTransaction().rollback();
            }

            if (em2.getTransaction().isActive()){
                em2.getTransaction().rollback();
            }

            em1.close();
            em2.close();
        }
        if (caughtException == null) {
            fail("Optimistic lock exception was not thrown.");
        } else if (!(caughtException.getCause() instanceof javax.persistence.OptimisticLockException)) {
            // Re-throw exception to ensure stacktrace appears in test result.
            throw caughtException;
        }
    }

    /**
     * Tests an ObjectTypeConverter on a direct to field mapping.
     */
    public void testGiveFredAnObjectTypeConverterChange() {
        EntityManager em = createEntityManager("fieldaccess");
        beginTransaction(em);

        try {
            EJBQueryImpl query = (EJBQueryImpl) em.createNamedQuery("findAllFieldAccessEmployeesByFirstName");
            query.setParameter("firstname", "Fred");
            Collection<Employee> employees = query.getResultCollection();

            if (employees.isEmpty()) {
                fail("No employees named Fred were found. Test requires at least one Fred to be created in the EmployeePopulator.");
            } else {
                Employee fred = employees.iterator().next();
                fred.setFemale();
                fred.setFirstName("Penelope");
                penelopeId = fred.getId();

                commitTransaction(em);

                // Clear cache and clear the entity manager
                clearCache("fieldaccess");
                em.clear();

                Employee penelope = em.find(Employee.class, penelopeId);
                assertTrue("Fred's ObjectTypeConverter change to Penelope didn't occur.", penelope.isFemale());
            }
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
     * Tests a BasicCollection on an entity that uses a composite primary key.
     */
    public void testUpdatePenelopesPhoneNumberStatus() {
        EntityManager em = createEntityManager("fieldaccess");
        beginTransaction(em);

        try {
            Employee emp = em.find(Employee.class, penelopeId);
            assertNotNull("The employee with id: [" + penelopeId + "] was not found.", emp);

            for (PhoneNumber phoneNumber : emp.getPhoneNumbers()) {
                phoneNumber.addStatus(PhoneNumber.PhoneStatus.ACTIVE);
                phoneNumber.addStatus(PhoneNumber.PhoneStatus.ASSIGNED);
            }

            commitTransaction(em);

            // Clear cache and clear the entity manager
            clearCache("fieldaccess");
            em.clear();

            Employee emp2 = em.find(Employee.class, penelopeId);

            for (PhoneNumber phone : emp2.getPhoneNumbers()) {
                assertTrue("", phone.getStatus().contains(PhoneNumber.PhoneStatus.ACTIVE));
                assertTrue("", phone.getStatus().contains(PhoneNumber.PhoneStatus.ASSIGNED));
            }
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
     * Tests a @PrivateOwned @OneToMany mapping.
     */
    public void testRemoveJillWithPrivateOwnedPhoneNumbers() {
        if (getServerSession("fieldaccess").getPlatform().isSymfoware()) {
            getServerSession("fieldaccess").logMessage("Test testRemoveJillWithPrivateOwnedPhoneNumbers skipped for this platform, "
            + "The CascadeOnDelete doesn't work on a relation where CascadeType.Remove or CascadeType.All is specified on Symfoware Platform.");
            return;
        }
        EntityManager em = createEntityManager("fieldaccess");
        beginTransaction(em);

        try {
            EJBQueryImpl query = (EJBQueryImpl) em.createNamedQuery("findAllFieldAccessEmployeesByFirstName");
            query.setParameter("firstname", "Jill");
            Collection<Employee> employees = query.getResultCollection();

            if (employees.isEmpty()) {
                fail("No employees named Jill were found. Test requires at least one Jill to be created in the EmployeePopulator.");
            } else {
                Employee jill = employees.iterator().next();
                Collection<PhoneNumber> phoneNumbers = jill.getPhoneNumbers();

                if (phoneNumbers.isEmpty()) {
                    fail("Jill does not have any phone numbers. Test requires that Jill have atleast one phone number created in the EmployeePopulator.");
                }

                // Re-assign her managed employees and remove from her list.
                for (Employee employee : jill.getManagedEmployees()) {
                    employee.setManager(jill.getManager());
                }
                jill.getManagedEmployees().clear();

                int jillId = jill.getId();

                em.remove(jill);
                commitTransaction(em);

                assertNull("Jill herself was not removed.", em.find(Employee.class, jillId));

                for (PhoneNumber phoneNumber : phoneNumbers) {
                    assertNull("Jill's phone numbers were not deleted.", em.find(PhoneNumber.class, phoneNumber.buildPK()));
                }
            }
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
     * Creates some new equipment objects.
     */
    public void testCreateNewEquipment() {
        EntityManager em = createEntityManager("fieldaccess");
        beginTransaction(em);

        try {
            // Persist some equipment.
            Equipment equip1 = new Equipment();
            equip1.setDescription("Toaster");
            em.persist(equip1);

            Equipment equip2 = new Equipment();
            equip1.setDescription("Bucket");
            em.persist(equip2);

            Equipment equip3 = new Equipment();
            equip1.setDescription("Broom");
            em.persist(equip3);

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
    }

    /**
     * Tests adding objects to a 1-M mapping that uses a map.
     */
    public void testAddNewEquipmentToDepartment() {
        EntityManager em = createEntityManager("fieldaccess");
        beginTransaction(em);

        try {
            EJBQueryImpl query = (EJBQueryImpl) em.createNamedQuery("findAllSQLEquipment");
            Collection<Equipment> equipment = query.getResultCollection();

            if (equipment.isEmpty()) {
                fail("No Equipment was found. testCreateNewEquipment should have created new equipment and should have run before this test.");
            } else {
                Department department = new Department();
                department.setName("Department with equipment");

                for (Equipment e : equipment) {
                    department.addEquipment(e);
                }

                em.persist(department);
                deptId = department.getId();
                commitTransaction(em);
            }
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
     * Tests a @NamedStoredProcedureQuery.
     */
    public void testNamedStoredProcedureQuery() {
        if (!supportsStoredProcedures("fieldaccess")) {
            return;
        }
        EntityManager em = createEntityManager("fieldaccess");
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

            Query aQuery = em.createNamedQuery("SProcAddress").setParameter("ADDRESS_ID", address1.getId());
            Address address2 = (Address) aQuery.getSingleResult();

            assertNotNull("Address returned from stored procedure is null", address2);
            assertFalse("Address returned is the same cached instance that was persisted - the cache must be disabled for this test", address1 == address2); // new
            // Integer address handled differently than int
            assertTrue("Address not found using stored procedure", address1.getId().intValue() == address2.getId().intValue());
            assertTrue("Address.street data returned doesn't match persisted address.street", address1.getStreet().endsWith(address2.getStreet()));
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
     * Tests a @NamedStoredProcedureQuery.
     */
    public void testNamedStoredProcedureQueryInOut() {
        if (!supportsStoredProcedures("fieldaccess")) {
            return;
        }
        EntityManager em = createEntityManager("fieldaccess");
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

            Query aQuery = em.createNamedQuery("SProcInOut").setParameter("ADDRESS_ID", address1.getId());
            Address address2 = (Address) aQuery.getSingleResult();

            assertNotNull("Address returned from stored procedure is null", address2);
            assertFalse("Address returned is the same cached instance that was persisted - the cache must be disabled for this test", address1 == address2); // new
            // Integer address handled differently than int
            assertTrue("Address not found using stored procedure", address1.getId().intValue() == address2.getId().intValue());
            assertTrue("Address.street data returned doesn't match persisted address.street", address1.getStreet().equals(address2.getStreet()));
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
     * Tests a @NamedStoredProcedureQuery that returns raw data
     * bug 254946
     */
    public void testNamedStoredProcedureQueryWithRawData() {
        if (!supportsStoredProcedures("fieldaccess")) {
            return;
        }
        EntityManager em = createEntityManager("fieldaccess");
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

            // 260263: we do not need to clear the cache for non-entity returns
            Query aQuery = em.createNamedQuery("SProcInOutReturningRawData").setParameter("ADDRESS_ID", address1.getId());
            Object[] objectdata = (Object[])aQuery.getSingleResult();

            assertTrue("Address data not found or returned using stored procedure", ((objectdata!=null) && (objectdata.length==2)) );
            assertTrue("Address Id data returned doesn't match persisted address", (address1.getId() == ((Long)objectdata[0]).longValue()));
            assertTrue("Address Street data returned doesn't match persisted address", ( address1.getStreet().equals(objectdata[1] )) );
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
     * Tests a @PrivateOwned @OneToMany mapping.
     */
    public void testRemoveDepartmentWithPrivateOwnedEquipment() {
        EntityManager em = createEntityManager("fieldaccess");
        beginTransaction(em);

        try {
            Department department = em.find(Department.class, deptId);

            if (department == null) {
                fail("Department with id="+deptId+", was not found.");
            } else {
                Collection<Equipment> equipment = department.getEquipment().values();

                if (equipment.isEmpty()){
                    fail("Department with id="+deptId+", did not have any equipment.");
                } else {
                    em.remove(department);
                    commitTransaction(em);

                    assertNull("Department itself was not removed.", em.find(Department.class, deptId));

                    for (Equipment e : equipment) {
                        assertNull("New equipment was not deleted.", em.find(Equipment.class, e.getId()));
                    }
                }
            }
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
     * Tests trying to update a read only class.
     */
    public void testUpdateReadOnlyEquipmentCode() {
        EntityManager em = createEntityManager("fieldaccess");
        beginTransaction(em);

        try {
            Query query = em.createNamedQuery("findSQLEquipmentCodeA");
            EquipmentCode equipmentCode = (EquipmentCode) query.getSingleResult();

            equipmentCode.setCode("Z");
            commitTransaction(em);

            // Nothing should have been written to the database. Query for
            // EquipmentCode A again. If an exception is caught, then it was
            // not found, therefore, updated on the db.
            try {
                query = em.createNamedQuery("findSQLEquipmentCodeA");
                query.getSingleResult();
            } catch (Exception e) {
                fail("The read only EquipmentA was modified");
            }
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
     * This tests verifies two things:
     * 1 - That a metadata named query is processed correctly when it uses a
     * lock mode type.
     * 2 - That a default persistence unit lock timeout value is correctly
     * processed and utilized.
     */
    public void testPessimisticLockingNamedQuery() {
        ServerSession session = JUnitTestCase.getServerSession("fieldaccess");

        // Cannot create parallel entity managers in the server. Skip on SQLServer since dead lock.
        if (! isOnServer() && isSelectForUpateNoWaitSupported("fieldaccess") && !(session.getPlatform().isSQLServer())) {
            EntityManager em = createEntityManager("fieldaccess");
            Employee employee;

            try {
                beginTransaction(em);
                employee = new Employee();
                employee.setFirstName("Lucie");
                employee.setLastName("Ogdensburgh");
                em.persist(employee);
                commitTransaction(em);
            } catch (RuntimeException ex) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }

                closeEntityManager(em);
                throw ex;
            }

            Exception lockTimeoutException = null;

            try {
                beginTransaction(em);
                // This query is configured as a pessimistic locking query.
                employee = (Employee) em.createNamedQuery("findFieldAccessEmployeeByPK").setParameter("id", employee.getId()).getSingleResult();
                EntityManager em2 = createEntityManager("fieldaccess");

                try {
                    beginTransaction(em2);
                    Employee emp2 = em2.find(Employee.class, employee.getId(), LockModeType.PESSIMISTIC_READ);
                } catch (PersistenceException ex) {
                    if (ex instanceof javax.persistence.LockTimeoutException) {
                        lockTimeoutException = ex;
                    } else {
                        throw ex;
                    }
                } finally {
                    closeEntityManagerAndTransaction(em2);
                }

                commitTransaction(em);
            } catch (RuntimeException ex) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }

                throw ex;
            } finally {
                closeEntityManager(em);
            }

            assertFalse("A lock timeout exception was not thrown (likely because the persistence unit lock timeout default property was not processed).", lockTimeoutException == null);
        }

    }
}
