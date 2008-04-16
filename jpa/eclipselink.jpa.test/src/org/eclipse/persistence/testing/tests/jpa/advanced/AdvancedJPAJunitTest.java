/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.advanced;

import java.lang.reflect.Array;
import java.util.Calendar;
import java.util.Collection;

import javax.persistence.EntityManager;

import junit.framework.*;
import junit.extensions.TestSetup;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.invalidation.TimeToLiveCacheInvalidationPolicy;
import org.eclipse.persistence.descriptors.invalidation.CacheInvalidationPolicy;
import org.eclipse.persistence.queries.DoesExistQuery;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.internal.jpa.EJBQueryImpl;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.Helper;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.Address;
import org.eclipse.persistence.testing.models.jpa.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.advanced.Department;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.advanced.EmploymentPeriod;
import org.eclipse.persistence.testing.models.jpa.advanced.EmployeePopulator;
import org.eclipse.persistence.testing.models.jpa.advanced.Equipment;
import org.eclipse.persistence.testing.models.jpa.advanced.EquipmentCode;
import org.eclipse.persistence.testing.models.jpa.advanced.GoldBuyer;
import org.eclipse.persistence.testing.models.jpa.advanced.PhoneNumber;
import org.eclipse.persistence.testing.models.jpa.advanced.LargeProject;
import org.eclipse.persistence.testing.models.jpa.advanced.Project;
import org.eclipse.persistence.testing.models.jpa.advanced.SmallProject;

/**
 * This test suite tests TopLink JPA annotations extensions.
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
        clearCache();
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("AdvancedJPAJunitTest");

        suite.addTest(new AdvancedJPAJunitTest("testExistenceCheckingSetting"));
        
        suite.addTest(new AdvancedJPAJunitTest("testJoinFetchAnnotation"));
        suite.addTest(new AdvancedJPAJunitTest("testVerifyEmployeeCacheSettings"));
        suite.addTest(new AdvancedJPAJunitTest("testVerifyEmployeeCustomizerSettings"));
        
        suite.addTest(new AdvancedJPAJunitTest("testUpdateEmployee"));
        suite.addTest(new AdvancedJPAJunitTest("testVerifyUpdatedEmployee"));
        
        suite.addTest(new AdvancedJPAJunitTest("testCreateNewBuyer"));
        suite.addTest(new AdvancedJPAJunitTest("testVerifyNewBuyer"));
        suite.addTest(new AdvancedJPAJunitTest("testBuyerOptimisticLocking"));
        
        suite.addTest(new AdvancedJPAJunitTest("testGiveFredASexChange"));
        suite.addTest(new AdvancedJPAJunitTest("testUpdatePenelopesPhoneNumberStatus"));
        suite.addTest(new AdvancedJPAJunitTest("testRemoveJillWithPrivateOwnedPhoneNumbers"));
        
        suite.addTest(new AdvancedJPAJunitTest("testCreateNewEquipment"));
        suite.addTest(new AdvancedJPAJunitTest("testAddNewEquipmentToDepartment"));
        suite.addTest(new AdvancedJPAJunitTest("testRemoveDepartmentWithPrivateOwnedEquipment"));
        suite.addTest(new AdvancedJPAJunitTest("testUpdateReadOnlyEquipmentCode"));
        
        suite.addTest(new AdvancedJPAJunitTest("testNamedStoredProcedureQuery"));
        suite.addTest(new AdvancedJPAJunitTest("testNamedStoredProcedureQueryInOut"));

        suite.addTest(new AdvancedJPAJunitTest("testMethodBasedTransformationMapping"));
        suite.addTest(new AdvancedJPAJunitTest("testClassBasedTransformationMapping"));

        suite.addTest(new AdvancedJPAJunitTest("testProperty"));
                
        return new TestSetup(suite) {
            protected void setUp() { 
                ServerSession session = JUnitTestCase.getServerSession();
                
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
            }

            protected void tearDown() {
                clearCache();
            }
        };
    }    

    /**
     * Verifies that existence-checking metadata is correctly processed.
     */
    public void testExistenceCheckingSetting() {
        ServerSession session = JUnitTestCase.getServerSession();
        
        ClassDescriptor employeeDescriptor = session.getDescriptor(Employee.class);
        assertTrue("Employee existence checking was incorrect", employeeDescriptor.getQueryManager().getDoesExistQuery().getExistencePolicy() == DoesExistQuery.CheckDatabase);
        
        ClassDescriptor projectDescriptor = session.getDescriptor(Project.class);
        assertTrue("Project existence checking was incorrect", projectDescriptor.getQueryManager().getDoesExistQuery().getExistencePolicy() == DoesExistQuery.CheckCache);
        
        ClassDescriptor smallProjectDescriptor = session.getDescriptor(SmallProject.class);
        assertTrue("SmallProject existence checking was incorrect", smallProjectDescriptor.getQueryManager().getDoesExistQuery().getExistencePolicy() == DoesExistQuery.AssumeExistence);
        
        ClassDescriptor largeProjectDescriptor = session.getDescriptor(LargeProject.class);
        assertTrue("LargeProject existence checking was incorrect", largeProjectDescriptor.getQueryManager().getDoesExistQuery().getExistencePolicy() == DoesExistQuery.AssumeNonExistence);
    }
    
    /**
     * Verifies that settings from the Employee cache annotation have been set.
     */
    public void testVerifyEmployeeCacheSettings() {
        EntityManager em = createEntityManager("default1");
        ClassDescriptor descriptor = ((EntityManagerImpl) em).getServerSession().getDescriptorForAlias("Employee");
            
        if (descriptor == null) {
            fail("A descriptor for the Employee alias was not found in the default1 PU.");
        } else {            
            assertTrue("Incorrect cache type() setting.", descriptor.getIdentityMapClass().equals(ClassConstants.SoftCacheWeakIdentityMap_Class));
            assertTrue("Incorrect cache size() setting.", descriptor.getIdentityMapSize() == 730);
            assertFalse("Incorrect cache isolated() setting.", descriptor.isIsolated());
            assertFalse("Incorrect cache alwaysRefresh() setting.", descriptor.shouldAlwaysRefreshCache());
            
            // The diableHits() setting gets changed in the employee customizer. 
            // Its setting is checked in the test below.
            
            CacheInvalidationPolicy policy = descriptor.getCacheInvalidationPolicy();
            assertTrue("Incorrect cache expiry() policy setting.", policy instanceof TimeToLiveCacheInvalidationPolicy);
            assertTrue("Incorrect cache expiry() setting.", ((TimeToLiveCacheInvalidationPolicy) policy).getTimeToLive() == 1000);
            
            assertTrue("Incorrect cache coordinationType() settting.", descriptor.getCacheSynchronizationType() == ClassDescriptor.INVALIDATE_CHANGED_OBJECTS);
        }
        
        closeEntityManager(em);
    }
    
    /**
     * Verifies that settings from the EmployeeCustomizer have been set.
     */
    public void testVerifyEmployeeCustomizerSettings() {
        EntityManager em = createEntityManager();
        
        ClassDescriptor descriptor = ((EntityManagerImpl) em).getServerSession().getDescriptorForAlias("Employee");
            
        if (descriptor == null) {
            fail("A descriptor for the Employee alias was not found.");    
        } else {
            assertFalse("Disable cache hits was true. Customizer should have made it false.", descriptor.shouldDisableCacheHits());
        }
        
        closeEntityManager(em);
    }
    
    /**
     * Verifies that the join-fetch annotation was read correctly.
     */
    public void testJoinFetchAnnotation() {
        ServerSession session = JUnitTestCase.getServerSession();
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
        EntityManager em = createEntityManager();
        beginTransaction(em);
        
        try {
            EJBQueryImpl query = (EJBQueryImpl) em.createNamedQuery("findAllSQLEmployees");
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
        EntityManager em = createEntityManager();
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
        EntityManager em = createEntityManager();
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
        EntityManager em = createEntityManager();
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
        EntityManager em1 = createEntityManager();
        EntityManager em2 = createEntityManager();
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
    public void testGiveFredASexChange() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        
        try {
            EJBQueryImpl query = (EJBQueryImpl) em.createNamedQuery("findAllEmployeesByFirstName");
            query.setParameter("firstname", "Fred");
            Collection<Employee> employees = query.getResultCollection();
            
            if (employees.isEmpty()) {
                // Did Fred chicken out?
                fail("No employees named Fred were found. Test requires at least one Fred to be created in the EmployeePopulator.");
            } else {
                Employee fred = employees.iterator().next();    
                fred.setFemale();
                fred.setFirstName("Penelope");
                penelopeId = fred.getId();
                
                commitTransaction(em);
                
                // Clear cache and clear the entity manager
                clearCache();    
                em.clear();
                
                Employee penelope = em.find(Employee.class, penelopeId);
                assertTrue("Fred's sex change to Penelope didn't occur.", penelope.isFemale());
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
        EntityManager em = createEntityManager();
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
            clearCache();    
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
        EntityManager em = createEntityManager();
        beginTransaction(em);
        
        try {
            EJBQueryImpl query = (EJBQueryImpl) em.createNamedQuery("findAllEmployeesByFirstName");
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
        EntityManager em = createEntityManager();
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
        EntityManager em = createEntityManager();
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
            
            Address address2 = (Address) em.createNamedQuery("SProcAddress").setParameter("ADDRESS_ID", address1.getId()).getSingleResult();
            assertTrue("Address not found using stored procedure", address2.equals(address1));
            
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

            Address address2 = (Address)em.createNamedQuery("SProcInOut").setParameter("ADDRESS_ID", address1.getId()).getSingleResult();
        
            assertTrue("Address not found using stored procedure", (address1.getId() == address2.getId()) && (address1.getStreet().equals(address2.getStreet())));
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
        EntityManager em = createEntityManager();
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
        EntityManager em = createEntityManager();
        beginTransaction(em);
        
        try {    
            EJBQueryImpl query = (EJBQueryImpl) em.createNamedQuery("findSQLEquipmentCodeA");
            EquipmentCode equipmentCode = (EquipmentCode) query.getSingleResult();
            
            equipmentCode.setCode("Z");
            commitTransaction(em);
            
            // Nothing should have been written to the database. Query for 
            // EquipmentCode A again. If an exception is caught, then it was 
            // not found, therefore, updated on the db.
            try {
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
        employee.setFirstName(firstName);
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

    /**
     * Tests Property and Properties annotations
     */
    public void testProperty() {
        EntityManager em = createEntityManager();
        ClassDescriptor descriptor = ((EntityManagerImpl) em).getServerSession().getDescriptorForAlias("Employee");
        ClassDescriptor aggregateDescriptor = ((EntityManagerImpl) em).getServerSession().getDescriptor(EmploymentPeriod.class);
        em.close();
        
        String errorMsg = "";
        
        if (descriptor == null) {
            errorMsg += " Descriptor for Employee alias was not found;";
        }
        if (aggregateDescriptor == null) {
            errorMsg += " Descriptor for EmploymentPeriod.class was not found;";
        }
        if(errorMsg.length() > 0) {
            fail(errorMsg);
        }

        // verify properties set on Employee instance
        errorMsg += verifyPropertyValue(descriptor, "entityName", String.class, "Employee");
        errorMsg += verifyPropertyValue(descriptor, "entityIntegerProperty", Integer.class, new Integer(1));
        
        // each attribute of Employee was assigned a property attributeName with the value attribute name.
        for(DatabaseMapping mapping : descriptor.getMappings()) {
            errorMsg += verifyPropertyValue(mapping, "attributeName", String.class, mapping.getAttributeName());
        }
        
        // attribute m_lastName has many properties of different types
        DatabaseMapping mapping = descriptor.getMappingForAttributeName("lastName");
        errorMsg += verifyPropertyValue(mapping, "BooleanProperty", Boolean.class, Boolean.TRUE);
        errorMsg += verifyPropertyValue(mapping, "ByteProperty", Byte.class, new Byte((byte)1));
        errorMsg += verifyPropertyValue(mapping, "CharacterProperty", Character.class, new Character('A'));
        errorMsg += verifyPropertyValue(mapping, "DoubleProperty", Double.class, new Double(1));
        errorMsg += verifyPropertyValue(mapping, "FloatProperty", Float.class, new Float(1));
        errorMsg += verifyPropertyValue(mapping, "IntegerProperty", Integer.class, new Integer(1));
        errorMsg += verifyPropertyValue(mapping, "LongProperty", Long.class, new Long(1));
        errorMsg += verifyPropertyValue(mapping, "ShortProperty", Short.class, new Short((short)1));
        errorMsg += verifyPropertyValue(mapping, "BigDecimalProperty", java.math.BigDecimal.class, java.math.BigDecimal.ONE);
        errorMsg += verifyPropertyValue(mapping, "BigIntegerProperty", java.math.BigInteger.class, java.math.BigInteger.ONE);
        errorMsg += verifyPropertyValue(mapping, "byte[]Property", byte[].class, new byte[]{1, 2, 3, 4});
        errorMsg += verifyPropertyValue(mapping, "char[]Property", char[].class, new char[]{'a', 'b', 'c'});
        errorMsg += verifyPropertyValue(mapping, "Byte[]Property", Byte[].class, new byte[]{1, 2, 3, 4});
        errorMsg += verifyPropertyValue(mapping, "Character[]Property", Character[].class, new char[]{'a', 'b', 'c'});
        errorMsg += verifyPropertyValue(mapping, "TimeProperty", java.sql.Time.class, Helper.timeFromString("13:59:59"));
        errorMsg += verifyPropertyValue(mapping, "TimeStampProperty", java.sql.Timestamp.class, Helper.timestampFromString("2008-04-10 13:59:59"));
        errorMsg += verifyPropertyValue(mapping, "DateProperty", java.sql.Date.class, Helper.dateFromString("2008-04-10"));
        
        // verify property set on EmploymentPeriod embeddable
        errorMsg += verifyPropertyValue(aggregateDescriptor, "embeddableClassName", String.class, "EmploymentPeriod");
        
        if(errorMsg.length() > 0) {
            fail(errorMsg);
        }
    }
    protected String verifyPropertyValue(ClassDescriptor descriptor, String propertyName, Class expectedPropertyValueType, Object expectedPropertyValue) {
        return verifyPropertyValue(propertyName, descriptor.getProperty(propertyName), expectedPropertyValueType, expectedPropertyValue, Helper.getShortClassName(descriptor.getJavaClass()) + " descriptor");
    }
    protected String verifyPropertyValue(DatabaseMapping mapping, String propertyName, Class expectedPropertyValueType, Object expectedPropertyValue) {
        return verifyPropertyValue(propertyName, mapping.getProperty(propertyName), expectedPropertyValueType, expectedPropertyValue, mapping.getAttributeName() + " attribute");
    }
    protected String verifyPropertyValue(String propertyName, Object propertyValue, Class expectedPropertyValueType, Object expectedPropertyValue, String masterName) {
        String errorMsg = "";
        String errorPrefix = " property " + propertyName + " for " + masterName;
        if(expectedPropertyValueType == null) {
            if(propertyValue != null) {
                errorMsg = errorPrefix + " value is " + propertyValue.toString() + " , was expected to be null.";
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
                errorMsg = errorPrefix + " has value " + propertyValue.toString() + ", " + expectedPropertyValue.toString() + " was expected;"; 
            }
        }
        return errorMsg;
    }
}
