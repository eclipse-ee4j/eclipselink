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
 *     Oracle - initial API and implementation from Oracle TopLink
 *     03/03/2010 - 2.1 Michael O'Brien  
 *       - 302316: clear the object cache when testing stored procedure returns on SQLServer 
 *         to avoid false positives visible only when debugging in DatabaseCall.buildOutputRow()
 *       - 260263: SQLServer 2005/2008 requires stored procedure creation select clause variable and column name matching
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.advanced;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.Bindable.BindableType;
import javax.persistence.metamodel.Type.PersistenceType;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.invalidation.CacheInvalidationPolicy;
import org.eclipse.persistence.descriptors.invalidation.TimeToLiveCacheInvalidationPolicy;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.jpa.EJBQueryImpl;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.internal.jpa.metamodel.MapAttributeImpl;
import org.eclipse.persistence.internal.jpa.metamodel.SingularAttributeImpl;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.queries.DoesExistQuery;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.framework.JoinedAttributeTestHelper;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.Address;
import org.eclipse.persistence.testing.models.jpa.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.advanced.Buyer;
import org.eclipse.persistence.testing.models.jpa.advanced.Customer;
import org.eclipse.persistence.testing.models.jpa.advanced.Dealer;
import org.eclipse.persistence.testing.models.jpa.advanced.Department;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.advanced.EmployeePopulator;
import org.eclipse.persistence.testing.models.jpa.advanced.EmploymentPeriod;
import org.eclipse.persistence.testing.models.jpa.advanced.Equipment;
import org.eclipse.persistence.testing.models.jpa.advanced.EquipmentCode;
import org.eclipse.persistence.testing.models.jpa.advanced.GoldBuyer;
import org.eclipse.persistence.testing.models.jpa.advanced.LargeProject;
import org.eclipse.persistence.testing.models.jpa.advanced.PhoneNumber;
import org.eclipse.persistence.testing.models.jpa.advanced.Project;
import org.eclipse.persistence.testing.models.jpa.advanced.SmallProject;

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
    private static long rbc = 4783;
    private static long scotia = 8732;
    private static long td = 839362;
    private static long cibc = 948274;
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

        suite.addTest(new AdvancedJPAJunitTest("testSetup"));
        // This test runs only on a JEE6 / JPA 2.0 capable server
        suite.addTest(new AdvancedJPAJunitTest("testMetamodelMinimalSanityTest"));
        suite.addTest(new AdvancedJPAJunitTest("testExistenceCheckingSetting"));
        
        suite.addTest(new AdvancedJPAJunitTest("testJoinFetchAnnotation"));
        suite.addTest(new AdvancedJPAJunitTest("testVerifyEmployeeCacheSettings"));
        suite.addTest(new AdvancedJPAJunitTest("testVerifyEmployeeCustomizerSettings"));
        
        suite.addTest(new AdvancedJPAJunitTest("testUpdateEmployee"));
        suite.addTest(new AdvancedJPAJunitTest("testVerifyUpdatedEmployee"));
        
        suite.addTest(new AdvancedJPAJunitTest("testCreateNewBuyer"));
        suite.addTest(new AdvancedJPAJunitTest("testVerifyNewBuyer"));
        suite.addTest(new AdvancedJPAJunitTest("testBuyerOptimisticLocking"));
        suite.addTest(new AdvancedJPAJunitTest("testOptimisticLockExceptionOnMerge"));
        suite.addTest(new AdvancedJPAJunitTest("testOptimisticLockExceptionOnMergeWithAssumeExists"));
        suite.addTest(new AdvancedJPAJunitTest("testVersionUpdateForOwnedMappings"));

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
        suite.addTest(new AdvancedJPAJunitTest("testModifyNamedStoredProcedureQueryWithRawData"));
        suite.addTest(new AdvancedJPAJunitTest("testNamedStoredProcedureQueryWithResultSetMapping"));
        suite.addTest(new AdvancedJPAJunitTest("testNamedStoredProcedureQueryWithResultSetFieldMapping"));

        suite.addTest(new AdvancedJPAJunitTest("testMethodBasedTransformationMapping"));
        suite.addTest(new AdvancedJPAJunitTest("testClassBasedTransformationMapping"));

        suite.addTest(new AdvancedJPAJunitTest("testProperty"));
        
        suite.addTest(new AdvancedJPAJunitTest("testBackpointerOnMerge"));
                
        suite.addTest(new AdvancedJPAJunitTest("testUnidirectionalPersist"));
        suite.addTest(new AdvancedJPAJunitTest("testUnidirectionalUpdate"));
        suite.addTest(new AdvancedJPAJunitTest("testUnidirectionalFetchJoin"));
        suite.addTest(new AdvancedJPAJunitTest("testUnidirectionalTargetLocking_AddRemoveTarget"));
        suite.addTest(new AdvancedJPAJunitTest("testUnidirectionalTargetLocking_DeleteSource"));
        
        return suite;
    }
    
    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
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

        clearCache();
    }
    
    /**
     * This test performs minimal sanity testing on the advanced JPA model
     * in order to verify metamodel creation.<p>
     * See the metamodel test package suite for full regression tests.
     * See SVN rev# 5124
     * http://fisheye2.atlassian.com/changelog/~author=mobrien/eclipselink/?cs=5124
     */
    public void testMetamodelMinimalSanityTest() {
        // Run test only when the JPA 2.0 specification is enabled on the server, or we are in SE mode with JPA 2.0 capability
        if(!this.isJPA10()) {
            EntityManager em = createEntityManager();
            // pre-clear metamodel to enable test reentry (SE only - not EE)
            if(!this.isOnServer()) {
                ((EntityManagerFactoryImpl)((EntityManagerImpl)em).getEntityManagerFactory()).setMetamodel(null);
            }
            Metamodel metamodel = em.getMetamodel();
            // get declared attributes
            EntityType<LargeProject> entityLargeProject = metamodel.entity(LargeProject.class);
            Set<Attribute<LargeProject, ?>> declaredAttributes = entityLargeProject.getDeclaredAttributes();
            assertTrue(declaredAttributes.size() > 0); // instead of a assertEquals(1, size) for future compatibility with changes to Buyer
        
            // check that getDeclaredAttribute and getDeclaredAttributes return the same attribute        
            Attribute<LargeProject, ?> budgetAttribute = entityLargeProject.getDeclaredAttribute("budget");
            assertNotNull(budgetAttribute);
            Attribute<LargeProject, ?> budgetSingularAttribute = entityLargeProject.getDeclaredSingularAttribute("budget");
            assertNotNull(budgetSingularAttribute);
            assertEquals(budgetSingularAttribute, budgetAttribute);
            assertTrue(declaredAttributes.contains(budgetSingularAttribute));        
            // check the type
            Class budgetClass = budgetSingularAttribute.getJavaType();
            // Verify whether we expect a boxed class or not 
            assertEquals(double.class, budgetClass);
            //assertEquals(Double.class, budgetClass);
        
            // Test LargeProject.budget.buyingDays
        
            // Check an EnumSet on an Entity
            EntityType<Buyer> entityBuyer = metamodel.entity(Buyer.class);
            // public enum Weekdays { SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY }
            // private EnumSet<Weekdays> buyingDays;
            assertNotNull(entityBuyer);
            // check persistence type
            assertEquals(PersistenceType.ENTITY, entityBuyer.getPersistenceType());
            assertEquals(Buyer.class, entityBuyer.getJavaType());
            // verify EnumSet is a SingularAttribute
            Attribute buyingDaysAttribute = entityBuyer.getAttribute("buyingDays");
            assertNotNull(buyingDaysAttribute);
            // Check persistent attribute type
            assertEquals(PersistentAttributeType.BASIC, buyingDaysAttribute.getPersistentAttributeType());
            // Non-spec check on the attribute impl type
            // EnumSet is not a Set in the Metamodel - it is a treated as a BasicType single object (SingularAttributeType)
            // BasicTypeImpl@8980685:EnumSet [ javaType: class java.util.EnumSet]
            assertFalse(((SingularAttributeImpl)buyingDaysAttribute).isPlural());
            BindableType buyingDaysElementBindableType = ((SingularAttributeImpl)buyingDaysAttribute).getBindableType();
            assertEquals(BindableType.SINGULAR_ATTRIBUTE, buyingDaysElementBindableType);
            SingularAttribute<? super Buyer, EnumSet> buyingDaysSingularAttribute = entityBuyer.getSingularAttribute("buyingDays", EnumSet.class);
            assertNotNull(buyingDaysSingularAttribute);
            assertFalse(buyingDaysSingularAttribute.isCollection());
        
            // http://wiki.eclipse.org/EclipseLink/Development/JPA_2.0/metamodel_api#DI_74:_20090909:_Implement_IdentifiableType.hasSingleIdAttribute.28.29
            // Check for Id that exists
            boolean expectedIAExceptionThrown = false;
            boolean hasSingleIdAttribute = false;
            try {
                hasSingleIdAttribute = entityBuyer.hasSingleIdAttribute();
            } catch (IllegalArgumentException iae) {
                //iae.printStackTrace();
                expectedIAExceptionThrown = true;            
            }
            assertFalse(expectedIAExceptionThrown);            
            assertTrue(hasSingleIdAttribute);

            // Verify that the BasicMap Buyer.creditCards is picked up properly
            //* @param <X> The type the represented Map belongs to
            //* @param <K> The type of the key of the represented Map
            //* @param <V> The type of the value of the represented Map
            //public class MapAttributeImpl<X, K, V> extends PluralAttributeImpl<X, java.util.Map<K, V>, V>
            Attribute buyerCreditCards = entityBuyer.getAttribute("creditCards");
            assertNotNull(buyerCreditCards);
            assertTrue(buyerCreditCards.isCollection());
            assertTrue(buyerCreditCards instanceof MapAttributeImpl);
            MapAttribute<? super Buyer, ?, ?> buyerCreditCardsMap = entityBuyer.getMap("creditCards");
        
            // Verify owning type
            assertNotNull(buyerCreditCardsMap);
            assertEquals(entityBuyer, buyerCreditCardsMap.getDeclaringType());
        
            // Verify Map Key
            assertEquals(String.class, buyerCreditCardsMap.getKeyJavaType());
        
            // Verify Map Value
            assertEquals(Long.class, buyerCreditCardsMap.getElementType().getJavaType());
        
        }
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
        ClassDescriptor descriptor;
        if (isOnServer()) {
            descriptor = getServerSession("default1").getDescriptorForAlias("Employee");
        } else {
            descriptor = ((EntityManagerImpl) em).getServerSession().getDescriptorForAlias("Employee");
        }
        
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
            assertTrue("Incorrect cache expiry() setting.", ((TimeToLiveCacheInvalidationPolicy) policy).getTimeToLive() == 100000);
            
            assertTrue("Incorrect cache coordinationType() settting.", descriptor.getCacheSynchronizationType() == ClassDescriptor.INVALIDATE_CHANGED_OBJECTS);
        }
        
        closeEntityManager(em);
    }
    
    /**
     * Verifies that settings from the EmployeeCustomizer have been set.
     */
    public void testVerifyEmployeeCustomizerSettings() {
        EntityManager em = createEntityManager();
        
        ClassDescriptor descriptor = getServerSession().getDescriptorForAlias("Employee");
        
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
    
    public void testVersionUpdateForOwnedMappings(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        
        try {
            Employee emp = em.find(Employee.class, empId);
            int currentVersion = emp.getVersion();
            assertNotNull("The employee was not found for Id.", empId);
            
            emp.getResponsibilities().add("UpdateVersionField");
            commitTransaction(em);
            assertTrue("Did not increment version for change to direct collection", emp.getVersion() == ++currentVersion);
            beginTransaction(em);
            emp = em.find(Employee.class, empId);
            emp.getDealers().add(em.merge(new Dealer("update", "version")));
            commitTransaction(em);
            assertTrue("Did not increment version for change to uni-directional one to many with join table", emp.getVersion() == ++currentVersion);
            beginTransaction(em);
            emp = em.find(Employee.class, empId);
            emp.getProjects().add(em.merge(new LargeProject("versionUpdate")));
            commitTransaction(em);
            assertTrue("Did not increment version for change to owned ManyToMany", emp.getVersion() == ++currentVersion);
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
            
            buyer.addRoyalBankCreditLine(rbc);
            buyer.addScotiabankCreditLine(scotia);
            buyer.addTorontoDominionCreditLine(td);
            buyer.addCanadianImperialCreditLine(cibc);
            
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
            
            assertTrue("RBC credit line did not persist correctly.", buyer.hasRoyalBankCreditLine(rbc));
            assertTrue("Scotia credit line did not persist correctly.", buyer.hasScotiabankCreditLine(scotia));
            assertTrue("TD credit line did not persist correctly.", buyer.hasTorontoDominionCreditLine(td));
            assertTrue("CIBC credit line did not persist correctly.", buyer.hasCanadianImperialCreditLine(cibc));
            
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
    public void testGiveFredAnObjectTypeConverterChange() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        
        try {
            EJBQueryImpl query = (EJBQueryImpl) em.createNamedQuery("findAllEmployeesByFirstName");
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
                clearCache();    
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
     * Tests that backpointers are not changed after a merge operation.
     */
    public void testBackpointerOnMerge() {
        EntityManager em = createEntityManager();

        try {            
            beginTransaction(em);
            
            // create a new department
            Department department = new Department();
            department.setName("Football");
            // persist the department
            em.persist(department);
            commitTransaction(em);
            closeEntityManager(em);
            
            // add equipment to the department
            em = createEntityManager();
            beginTransaction(em);
            Equipment equipment = new Equipment();
            equipment.setDescription("Shields & Dummies");
            department.addEquipment(equipment);
            em.merge(department);
            commitTransaction(em);
            closeEntityManager(em);

            assertTrue(department.getEquipment().get(0) == equipment);
            assertEquals(System.identityHashCode(department.getEquipment().get(0)), System.identityHashCode(equipment));
            assertEquals(department.getId(), equipment.getDepartment().getId());
            assertTrue("The department instance (backpointer) from equipment was modified after merge.", department == equipment.getDepartment());
            assertEquals("The department instance (backpointer) from equipment was modified after merge.", System.identityHashCode(department), System.identityHashCode(equipment.getDepartment()));
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            
            closeEntityManager(em);
            throw e;
        }
    }
    
    /**
     * Tests a @NamedStoredProcedureQuery.
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

            Address address2 = (Address) em.createNamedQuery("SProcAddress").setParameter("ADDRESS_ID", address1.getId()).getSingleResult();
            assertNotNull("Address returned from stored procedure is null", address2);
            assertFalse("Address returned is the same cached instance that was persisted - the cache must be disabled for this test", address1 == address2); // new 
            assertTrue("Address not found using stored procedure", (address2.getId() == address1.getId()));
            
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
     * Tests a @NamedStoredProcedureQuery using a result-set mapping.
     * 304400: Note: this test does not actually test a ResultSet return - it uses output parameters.
     *              To enable ResultSet testing - set returnsResultSet=true via orm.xml, annotation or via program call like the the core tests. 
     */
    public void testNamedStoredProcedureQueryWithResultSetMapping() {
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
            
            Address address2 = (Address) em.createNamedQuery("SProcAddressWithResultSetMapping").setParameter("address_id_v", address1.getId()).getSingleResult();
            assertNotNull("Address returned from stored procedure is null", address2);
            assertFalse("Address returned is the same cached instance that was persisted - the cache must be disabled for this test", address1 == address2); // new 
            assertTrue("Address not found using stored procedure", (address2.getId() == address1.getId()));
            
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
     * Tests a @NamedStoredProcedureQuery using a result-set mapping.
     * 304400: Note: this test does not actually test a ResultSet return - it uses output parameters.
     *              To enable ResultSet testing - set returnsResultSet=true via orm.xml, annotation or via program call like the the core tests. 
     */
    public void testNamedStoredProcedureQueryWithResultSetFieldMapping() {
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
            
            // 260263: we do not need to clear the cache for non-entity returns
            Object[] values = (Object[]) em.createNamedQuery("SProcAddressWithResultSetFieldMapping").setParameter("address_id_v", address1.getId()).getSingleResult();
            assertTrue("Address data not found or returned using stored procedure", ((values!=null) && (values.length==6)) );
            assertNotNull("No results returned from store procedure call", values[1]);
            assertTrue("Address not found using stored procedure", address1.getStreet().equals(values[1]));
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
            
            Query aQuery = em.createNamedQuery("SProcInOut").setParameter("ADDRESS_ID", address1.getId());
            Address address2 = (Address) aQuery.getSingleResult();
        
            assertNotNull("Address returned from stored procedure is null", address2);
            assertFalse("Address returned is the same cached instance that was persisted - the cache must be disabled for this test", address1 == address2); // new 
            assertTrue("Address not found using stored procedure", address1.getId() == address2.getId());
            assertTrue("Address.street data returned doesn't match persisted address.street", address1.getStreet().equalsIgnoreCase(address2.getStreet()));
            
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

            // 260263: we do not need to clear the cache for non-entity returns
            Query aQuery = em.createNamedQuery("SProcInOutReturningRawData").setParameter("ADDRESS_ID", address1.getId());
            Object[] objectdata = (Object[])aQuery.getSingleResult();
            
            assertTrue("Address data not found or returned using stored procedure", ((objectdata!=null)&& (objectdata.length==2)) );
            assertTrue("Address Id data returned doesn't match persisted address", (address1.getId() == ((Integer)objectdata[0]).intValue()) );
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
     * Tests an OptimisticLockingException is thrown when calling merge a detached and removed object.
     * bug 272704
     */
    public void testOptimisticLockExceptionOnMerge() {
        EntityManager em1 = createEntityManager();
        beginTransaction(em1);

        RuntimeException caughtException = null;
        try {
            Employee emp = new Employee();
            emp.setLastName("OptimisticLockExceptionOnMerge");
            emp.setVersion(10);
            em1.persist(emp);
            commitTransaction(em1);
            closeEntityManager(em1);

            em1 = createEntityManager();
            beginTransaction(em1);

            emp = em1.find(Employee.class, emp.getId());
            em1.remove(emp);
            commitTransaction(em1);
            closeEntityManager(em1);

            em1 = createEntityManager();
            beginTransaction(em1);
            //this is expected to throw an OptimisticLockException, because the version is >1
            em1.merge(emp);

            commitTransaction(em1);
            
        } catch (RuntimeException e) {
            caughtException = e;
            if (isTransactionActive(em1)){
                rollbackTransaction(em1);
            }
        }
        closeEntityManager(em1);
        if (caughtException == null) {
            fail("Optimistic lock exception was not thrown.");
        } else if (!(caughtException instanceof javax.persistence.OptimisticLockException)) {
            // Re-throw exception to ensure stacktrace appears in test result.
            throw caughtException;
        }        
    }

    /** 
     * Tests an OptimisticLockingException is thrown when calling merge a detached and removed object.
     * bug 272704
     */
    public void testOptimisticLockExceptionOnMergeWithAssumeExists() {
        org.eclipse.persistence.sessions.Project project = getServerSession().getProject();
        ClassDescriptor descriptor = project.getDescriptor(Employee.class);
        int existencePolicy = descriptor.getQueryManager().getDoesExistQuery().getExistencePolicy();

        descriptor.getQueryManager().assumeExistenceForDoesExist();

        try {
            testOptimisticLockExceptionOnMerge();
        } finally {
            descriptor.getQueryManager().getDoesExistQuery().setExistencePolicy(existencePolicy);
        }
    }

    /**
     * Tests a @NamedStoredProcedureQuery that returns raw data using executeUpdate().
     */
    public void testModifyNamedStoredProcedureQueryWithRawData() {
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
            beginTransaction(em);

            em.createNamedQuery("SProcInOutReturningRawData").setParameter("ADDRESS_ID", address1.getId()).executeUpdate();
       } catch (RuntimeException exception) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            // Re-throw exception to ensure stacktrace appears in test result.
            throw exception;
        }
        commitTransaction(em);
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
            employee = em.find(Employee.class, employee.getId());
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
        ClassDescriptor descriptor = getServerSession().getDescriptorForAlias("Employee");
        ClassDescriptor aggregateDescriptor = getServerSession().getDescriptor(EmploymentPeriod.class);
        
        closeEntityManager(em);
        
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
    
    public void testUnidirectionalPersist() {
        String lastName = "testUnidirectionalPersist";
        
        // persist employees
        List<Employee> employeesPersisted = persistEmployeesWithUnidirectionalMappings(lastName);
        
        // clear cache
        clearCache();
        
        // read the persisted employees back
        EntityManager em = createEntityManager();
        List<Employee> employeesRead = em.createQuery("SELECT OBJECT(e) FROM Employee e WHERE e.lastName = '"+lastName+"'").getResultList();
        closeEntityManager(em);

        // verify number persisted and read is the same
        if(employeesPersisted.size() != employeesRead.size()) {
            //clean-up
            deleteEmployeesWithUnidirectionalMappings(lastName);
            fail("Persisted " + employeesPersisted.size() + " employees, but read back " + employeesRead.size());
        }

        // verify that the persisted and read objects are equal
        ServerSession session = JUnitTestCase.getServerSession();
        String errorMsg = "";
        for(int i=0; i<employeesPersisted.size(); i++) {
            for(int j=0; j<employeesRead.size(); j++) {
                if(employeesPersisted.get(i).getFirstName().equals(employeesRead.get(j).getFirstName())) {
                    if(!session.compareObjects(employeesPersisted.get(i), employeesRead.get(j))) {
                        errorMsg += "Employee " + employeesPersisted.get(i).getFirstName() +"  was not persisted correctly.";
                    }
                }
            }
        }
        
        // clean-up
        deleteEmployeesWithUnidirectionalMappings(lastName);
        
        // non-empty error message means the test has failed
        if(errorMsg.length() > 0) {
            fail(errorMsg);
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
        List<Employee> employeesRead = em.createQuery("SELECT OBJECT(e) FROM Employee e WHERE e.lastName = '"+lastName+"'").getResultList();
        
        // verify number persisted and read is the same
        if(employeesPersisted.size() != employeesRead.size()) {
            // clean-up
            deleteEmployeesWithUnidirectionalMappings(lastName);
            fail("Updated " + employeesPersisted.size() + " employees, but read back " + employeesRead.size());
        }
        
        // verify that the persisted and read objects are equal
        ServerSession session = JUnitTestCase.getServerSession();
        beginTransaction(em);
        String errorMsg = "";
        try{
            for(int i=0; i<employeesPersisted.size(); i++) {
                for(int j=0; j<employeesRead.size(); j++) {
                    if (isOnServer()) {
                        Employee emp1 = em.find(Employee.class, employeesPersisted.get(i).getId());
                        Employee emp2 = em.find(Employee.class, employeesRead.get(j).getId());
                        if(emp1.getFirstName().equals(emp2.getFirstName())) {
                            if(!session.compareObjects(emp1, emp2)) {
                                errorMsg += "Employee " + emp1.getFirstName() +"  was not updated correctly.";
                            }
                        }
                    } else {
                        if(employeesPersisted.get(i).getFirstName().equals(employeesRead.get(j).getFirstName())) {
                            if(!session.compareObjects(employeesPersisted.get(i), employeesRead.get(j))) {
                                errorMsg += "Employee " + employeesPersisted.get(i).getFirstName() +"  was not updated correctly.";
                            }
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
            fail(errorMsg);
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
        List<Employee> employeesRead = em.createQuery("SELECT OBJECT(e) FROM Employee e WHERE e.lastName = '"+lastName+"'").getResultList();
        closeEntityManager(em);
        
        // clear cache
        clearCache();
        
        // read the persisted employees back - with fetch join. 
        em = createEntityManager();
        List<Employee> employeesReadWithFetchJoin = em.createQuery("SELECT e FROM Employee e JOIN FETCH e.dealers WHERE e.lastName = '"+lastName+"'").getResultList();
        closeEntityManager(em);
        
        // verify that the persisted and read employees are the same.
        // The comparison cascades to all references and requires the same state of indirection:
        // it fails in case an object has triggered indirection for particular attribute and compared object's indirection for this attribute is not triggered.
        // The expected result of join fetch query is Employee.dealers being triggered - so need to trigger it on the control collection (getDealers.size() does that);
        // also the expected result should have an object for each row returned - therefore number of inclusions of each Employee equals its dealers.size()
        List<Employee> employeesControl = new ArrayList<Employee>();
        for(int i=0; i<employeesRead.size(); i++) {
            int nDialers = employeesRead.get(i).getDealers().size(); 
            for(int j=0; j<nDialers; j++) {
                employeesControl.add(employeesRead.get(i));
            }
        }
        ServerSession session = JUnitTestCase.getServerSession();
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
        ArrayList<Integer> dealersIds = new ArrayList<Integer>();
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
        List<Employee> readEmployees = em.createQuery("SELECT OBJECT(e) FROM Employee e WHERE e.lastName = '"+lastName+"'").getResultList();
        
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
        String errorMsg = "";
        try{
            for(int i=0; i<dealersIds.size(); i++) {
                Dealer dealer = em.find(Dealer.class, dealersIds.get(i));

                // verify the version both in the cache and in the db
                int version2 = getVersion(em, dealer);
                if(version2 != 2) {
                    errorMsg += "In the cache dealer "+dealer.getFirstName()+"'s version is " + version2 + " (2 was expected); ";
                }
                em.refresh(dealer);
                
                version2 = getVersion(em, dealer);
                if(version2 != 2) {
                    errorMsg += "In the db dealer "+dealer.getFirstName()+"'s version is " + version2 + " (2 was expected); ";
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
            fail(errorMsg);
        }
    }
    
    protected int getVersion(EntityManager em, Dealer dealer) {
        Vector pk = new Vector(1);
        pk.add(dealer.getId());
        
        return ((Integer)getServerSession().getDescriptor(Dealer.class).getOptimisticLockingPolicy().getWriteLockValue(dealer, pk, getServerSession())).intValue();
    }

    protected List<Employee> createEmployeesWithUnidirectionalMappings(String lastName) {
        int n = 2;
        List<Employee> employees = new ArrayList<Employee>(n);
        for(int i=0; i<n; i++) {
            Employee emp = new Employee();
            emp.setFirstName(Integer.toString(i+1));
            emp.setLastName(lastName);
            employees.add(emp);
            for(int j=0; j<n; j++) {
                Dealer dealer = new Dealer();
                dealer.setFirstName(emp.getFirstName() + "_" + Integer.toString(j+1));
                dealer.setLastName(lastName);
                emp.addDealer(dealer);
                for(int k=0; k<n; k++) {
                    Customer customer = new Customer();
                    customer.setFirstName(dealer.getFirstName() + "_" + Integer.toString(k+1));
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
            em.createQuery("DELETE FROM AdvancedCustomer c WHERE c.lastName = '"+lastName+"'").executeUpdate();
            em.createQuery("DELETE FROM Dealer d WHERE d.lastName = '"+lastName+"'").executeUpdate();
            Query q = em.createQuery("SELECT e FROM Employee e WHERE e.lastName = '"+lastName+"'");
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
}
