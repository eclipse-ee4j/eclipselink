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
package org.eclipse.persistence.testing.tests.jpa.xml.advanced;

import java.lang.reflect.Array;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;

import junit.framework.*;
import junit.extensions.TestSetup;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.SelectedFieldsLockingPolicy;
import org.eclipse.persistence.descriptors.invalidation.CacheInvalidationPolicy; 
import org.eclipse.persistence.descriptors.invalidation.TimeToLiveCacheInvalidationPolicy; 
import org.eclipse.persistence.internal.descriptors.OptimisticLockingPolicy;  
import org.eclipse.persistence.internal.helper.ClassConstants; 
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.jpa.EJBQueryImpl;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.queries.DoesExistQuery;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.Bungalow;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.EmployeePopulator;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.Address;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.Customer;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.Dealer;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.EmploymentPeriod;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.LargeProject;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.ModelExamples;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.PhoneNumber;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.Project;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.ReadOnlyClass;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.SmallProject;
import org.eclipse.persistence.testing.framework.JoinedAttributeTestHelper;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.tests.jpa.TestingProperties;
 
/**
 * JUnit test case(s) for the TopLink EntityMappingsXMLProcessor.
 */
public class EntityMappingsAdvancedJUnitTestCase extends JUnitTestCase {
    private static Integer employeeId, extendedEmployeeId;
    private static long visa = 1234567890;
    private static long amex = 1987654321;
    private static long diners = 1192837465;
    private static long mastercard = 1647382910;
    private String m_persistenceUnit;
    
    public EntityMappingsAdvancedJUnitTestCase() {
        super();
    }
    
    public EntityMappingsAdvancedJUnitTestCase(String name, String persistenceUnit) {
        super(name);
        
        m_persistenceUnit = persistenceUnit;
    }
    
    public static Test suite() {
        String ormTesting = TestingProperties.getProperty(TestingProperties.ORM_TESTING, TestingProperties.JPA_ORM_TESTING);
        final String persistenceUnit = ormTesting.equals(TestingProperties.JPA_ORM_TESTING)? "default" : "extended-advanced";

        TestSuite suite = new TestSuite("Advanced Model - " + persistenceUnit);
        
        suite.addTest(new EntityMappingsAdvancedJUnitTestCase("testCreateEmployee", persistenceUnit));
        suite.addTest(new EntityMappingsAdvancedJUnitTestCase("testReadEmployee", persistenceUnit));
        suite.addTest(new EntityMappingsAdvancedJUnitTestCase("testNamedNativeQueryOnAddress", persistenceUnit));
        suite.addTest(new EntityMappingsAdvancedJUnitTestCase("testNamedQueryOnEmployee", persistenceUnit));
        suite.addTest(new EntityMappingsAdvancedJUnitTestCase("testUpdateEmployee", persistenceUnit));
        suite.addTest(new EntityMappingsAdvancedJUnitTestCase("testRefreshNotManagedEmployee", persistenceUnit));
        suite.addTest(new EntityMappingsAdvancedJUnitTestCase("testRefreshRemovedEmployee", persistenceUnit));
        suite.addTest(new EntityMappingsAdvancedJUnitTestCase("testDeleteEmployee", persistenceUnit));
        suite.addTest(new EntityMappingsAdvancedJUnitTestCase("testUnidirectionalPersist", persistenceUnit));
        suite.addTest(new EntityMappingsAdvancedJUnitTestCase("testUnidirectionalUpdate", persistenceUnit));
        suite.addTest(new EntityMappingsAdvancedJUnitTestCase("testUnidirectionalFetchJoin", persistenceUnit));
        
        if (persistenceUnit.equals("extended-advanced")) {            
            suite.addTest(new EntityMappingsAdvancedJUnitTestCase("testExistenceCheckingSetting", persistenceUnit));
            suite.addTest(new EntityMappingsAdvancedJUnitTestCase("testReadOnlyClassSetting", persistenceUnit));
            suite.addTest(new EntityMappingsAdvancedJUnitTestCase("testEmployeeChangeTrackingPolicy", persistenceUnit));
            suite.addTest(new EntityMappingsAdvancedJUnitTestCase("testAddressChangeTrackingPolicy", persistenceUnit));
            suite.addTest(new EntityMappingsAdvancedJUnitTestCase("testPhoneNumberChangeTrackingPolicy", persistenceUnit));
            suite.addTest(new EntityMappingsAdvancedJUnitTestCase("testProjectChangeTrackingPolicy", persistenceUnit));
            suite.addTest(new EntityMappingsAdvancedJUnitTestCase("testJoinFetchSetting", persistenceUnit));
            suite.addTest(new EntityMappingsAdvancedJUnitTestCase("testEmployeeOptimisticLockingSettings", persistenceUnit));
            suite.addTest(new EntityMappingsAdvancedJUnitTestCase("testEmployeeCacheSettings", persistenceUnit));            
            suite.addTest(new EntityMappingsAdvancedJUnitTestCase("testProjectOptimisticLockingSettings", persistenceUnit));
            suite.addTest(new EntityMappingsAdvancedJUnitTestCase("testExtendedEmployee", persistenceUnit));
            suite.addTest(new EntityMappingsAdvancedJUnitTestCase("testGiveExtendedEmployeeASexChange", persistenceUnit));
            suite.addTest(new EntityMappingsAdvancedJUnitTestCase("testNamedStoredProcedureQuery", persistenceUnit));
            suite.addTest(new EntityMappingsAdvancedJUnitTestCase("testNamedStoredProcedureQueryInOut", persistenceUnit));            
            suite.addTest(new EntityMappingsAdvancedJUnitTestCase("testMethodBasedTransformationMapping", persistenceUnit));
            suite.addTest(new EntityMappingsAdvancedJUnitTestCase("testClassBasedTransformationMapping", persistenceUnit));
            suite.addTest(new EntityMappingsAdvancedJUnitTestCase("testClassInstanceConverter", persistenceUnit));
            suite.addTest(new EntityMappingsAdvancedJUnitTestCase("testProperty", persistenceUnit));
            suite.addTest(new EntityMappingsAdvancedJUnitTestCase("testAccessorMethods", persistenceUnit));
            suite.addTest(new EntityMappingsAdvancedJUnitTestCase("testIfMultipleBasicCollectionMappingsExistForEmployeeResponsibilites", persistenceUnit));
        }
        
        return new TestSetup(suite) {
            
            protected void setUp() {
            	DatabaseSession session = JUnitTestCase.getServerSession(persistenceUnit);   
            	new AdvancedTableCreator().replaceTables(session);
            	
            	// Populate the database with our examples.
                EmployeePopulator employeePopulator = new EmployeePopulator();         
                employeePopulator.buildExamples();
                employeePopulator.persistExample(session);
            }
        
            protected void tearDown() {
                clearCache(persistenceUnit);
            }
        };
    }
    
    /**
     * Verifies that access-methods are correctly processed and used.
     */
    public void testAccessorMethods() {
        EntityManager em = createEntityManager(m_persistenceUnit);
        String testSin = "123456";
        beginTransaction(em);
        try {
            Employee employee = ModelExamples.employeeExample1();
            employee.enterSIN(testSin);
            em.persist(employee);
            Integer employeeId = employee.getId();
            commitTransaction(em);
            
            clearCache(m_persistenceUnit);
            em.clear();
           
            // Re-read the employee and verify the data.
            Employee emp = em.find(Employee.class, employeeId);
            
            assertTrue("The SIN value was not persisted.", testSin.equals( emp.returnSIN() ) );
            assertTrue("The enterSIN accessor on Employee was not used to set the value", emp.returnSinChangeCounter()==1 );
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
        
    }
    
    /**
     * Verifies that existence-checking metadata is correctly processed.
     */
    public void testExistenceCheckingSetting() {
        ServerSession session = JUnitTestCase.getServerSession(m_persistenceUnit);
        
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
     * Verifies that a basic collection is not added to the employee descriptor
     * twice.
     */
    public void testIfMultipleBasicCollectionMappingsExistForEmployeeResponsibilites() {
        ServerSession session = JUnitTestCase.getServerSession(m_persistenceUnit);
        
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
        ServerSession session = JUnitTestCase.getServerSession(m_persistenceUnit);
        ClassDescriptor descriptor = session.getDescriptor(ReadOnlyClass.class);
     
        assertFalse("ReadOnlyClass descriptor was not found in the PU [" + m_persistenceUnit + "]", descriptor == null);
        assertTrue("ReadOnlyClass descriptor is not set to read only.", descriptor.shouldBeReadOnly());
    }
    
    /**
     * Verifies that the change tracking metadata is correctly processed.
     * Employee has an AUTO setting, but has EAGER collections and transformation mapping
     * so should not be change tracked.
     */
    public void testEmployeeChangeTrackingPolicy() {
        ServerSession session = JUnitTestCase.getServerSession(m_persistenceUnit);
        ClassDescriptor descriptor = session.getDescriptor(Employee.class);
     
        assertFalse("Employee descriptor was not found in the PU [" + m_persistenceUnit + "]", descriptor == null);
        assertFalse("Employee descriptor has incorrect object change policy", descriptor.getObjectChangePolicy().isAttributeChangeTrackingPolicy());
    }
    
    /**
     * Verifies that the change tracking metadata is correctly processed.
     */
    public void testAddressChangeTrackingPolicy() {
        ServerSession session = JUnitTestCase.getServerSession(m_persistenceUnit);
        ClassDescriptor descriptor = session.getDescriptor(Address.class);
     
        assertFalse("Address descriptor was not found in the PU [" + m_persistenceUnit + "]", descriptor == null);
        assertTrue("Address descriptor has incorrect object change policy", descriptor.getObjectChangePolicyInternal().isDeferredChangeDetectionPolicy());
    }
    
    /**
     * Verifies that the change tracking metadata is correctly processed.
     */
    public void testPhoneNumberChangeTrackingPolicy() {
        ServerSession session = JUnitTestCase.getServerSession(m_persistenceUnit);
        ClassDescriptor descriptor = session.getDescriptor(PhoneNumber.class);
     
        assertFalse("PhoneNumber descriptor was not found in the PU [" + m_persistenceUnit + "]", descriptor == null);
        assertTrue("PhoneNumber descriptor has incorrect object change policy", descriptor.getObjectChangePolicy().isAttributeChangeTrackingPolicy());
    }
    
    /**
     * Verifies that the change tracking metadata is correctly processed.
     */
    public void testProjectChangeTrackingPolicy() {
        ServerSession session = JUnitTestCase.getServerSession(m_persistenceUnit);
        ClassDescriptor descriptor = session.getDescriptor(Project.class);
     
        assertFalse("Project descriptor was not found in the PU [" + m_persistenceUnit + "]", descriptor == null);
        assertTrue("Project descriptor has incorrect object change policy", descriptor.getObjectChangePolicy().isObjectChangeTrackingPolicy());
    }
    
    /**
     * Verifies that the join-fetch setting was read correctly from XML.
     */
    public void testJoinFetchSetting() {
        ServerSession session = JUnitTestCase.getServerSession(m_persistenceUnit);
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
    	ServerSession session = JUnitTestCase.getServerSession(m_persistenceUnit);
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
        ServerSession session = JUnitTestCase.getServerSession(m_persistenceUnit); 
        ClassDescriptor descriptor = session.getDescriptor(Employee.class); 
             
        if (descriptor == null) { 
            fail("A descriptor for the Employee alias was not found in the PU [" + m_persistenceUnit + "]"); 
        } else {             
            assertTrue("Incorrect cache type() setting.", descriptor.getIdentityMapClass().equals(ClassConstants.SoftCacheWeakIdentityMap_Class)); 
            assertTrue("Incorrect cache size() setting.", descriptor.getIdentityMapSize() == 730); 
            assertFalse("Incorrect cache isolated() setting.", descriptor.isIsolated()); 
            assertFalse("Incorrect cache alwaysRefresh() setting.", descriptor.shouldAlwaysRefreshCache()); 
            assertFalse("Incorrect disable hits setting.", descriptor.shouldDisableCacheHits()); 
            CacheInvalidationPolicy policy = descriptor.getCacheInvalidationPolicy(); 
            assertTrue("Incorrect cache expiry() policy setting.", policy instanceof TimeToLiveCacheInvalidationPolicy); 
            assertTrue("Incorrect cache expiry() setting.", ((TimeToLiveCacheInvalidationPolicy) policy).getTimeToLive() == 1000); 
            assertTrue("Incorrect cache coordinationType() settting.", descriptor.getCacheSynchronizationType() == ClassDescriptor.INVALIDATE_CHANGED_OBJECTS); 
        } 
    } 

    /**
     * Verifies that the optimistic-locking settings were read correctly from XML.
     */
    public void testProjectOptimisticLockingSettings() {
    	ServerSession session = JUnitTestCase.getServerSession(m_persistenceUnit);
        ClassDescriptor descriptor = session.getDescriptor(Project.class);
            
        if (descriptor == null) {
            fail("Project descriptor was not found in the PU [" + m_persistenceUnit + "]");
        } else {
        	OptimisticLockingPolicy policy = descriptor.getOptimisticLockingPolicy();
        	
        	if (policy instanceof SelectedFieldsLockingPolicy) {
        		Vector<DatabaseField> lockFields = ((SelectedFieldsLockingPolicy) policy).getLockFields();
        		
        		if (lockFields.isEmpty() || lockFields.size() > 1) {
        			fail("Invalid amount of lock fields were set on Project's selected fields locking policy.");
        		} else {
        			DatabaseField lockField = lockFields.firstElement();
        			assertTrue("Incorrect lock field was set on Project's selected fields locking policy.", lockField.getName().equals("VERSION"));
        		}
        	} else {
        		fail("A SelectedFieldsLockingPolicy was not set on the Project descriptor.");
        	}
        }
    }
    
    public void testCreateEmployee() {
        EntityManager em = createEntityManager(m_persistenceUnit);
        beginTransaction(em);
        try {
            Employee employee = ModelExamples.employeeExample1();
            ArrayList projects = new ArrayList();
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
        EntityManager em = createEntityManager(m_persistenceUnit);
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
            employee.setFirstName("Boy");
            employee.setLastName("Pelletier");
            employee.setMale();
            employee.addVisa(visa);
            employee.addAmex(amex);
            employee.addDinersClub(diners);
            employee.addMastercard(mastercard);
            employee.setAddress(address);
            employee.setSalary(20000);
            employee.setPeriod(new EmploymentPeriod());
            employee.getPeriod().setStartDate(new Date(System.currentTimeMillis()-1000000));
            employee.getPeriod().setEndDate(new Date(System.currentTimeMillis()+1000000));
            employee.addResponsibility("A very important responsibility");
            
            em.persist(employee);
            extendedEmployeeId = employee.getId();
            commitTransaction(em);
            
            clearCache(m_persistenceUnit);
            em.clear();
            
            // Re-read the employee and verify the data.
            Employee emp = em.find(Employee.class, extendedEmployeeId);
            assertNotNull("The employee was not found", emp);
            assertTrue("The address province value was not converted", emp.getAddress().getProvince().equals("Ontario"));
            assertTrue("Gender was not persisted correctly.", emp.isMale());
            assertTrue("Visa card did not persist correctly.", emp.hasVisa(visa));
            assertTrue("Amex card did not persist correctly.", emp.hasAmex(amex));
            assertTrue("Diners Club card did not persist correctly.", emp.hasDinersClub(diners));
            assertTrue("Mastercard card did not persist correctly.", emp.hasMastercard(mastercard));
            
        	boolean found = false;
        	for (String responsibility : (Collection<String>) emp.getResponsibilities()) {
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
        EntityManager em = createEntityManager(m_persistenceUnit);
        beginTransaction(em);
        
        try {
        	Employee maleEmp = em.find(Employee.class, extendedEmployeeId);    
        	maleEmp.setFemale();
        	maleEmp.setFirstName("Girl");
            commitTransaction(em);
                
            // Clear cache and clear the entity manager
            clearCache(m_persistenceUnit);    
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
        EntityManager em = createEntityManager(m_persistenceUnit);
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
        assertTrue("Error deleting Employee", em.find(Employee.class, employeeId) == null);
    }

    public void testNamedNativeQueryOnAddress() {
        EntityManager em = createEntityManager(m_persistenceUnit);
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
        EJBQueryImpl query = (EJBQueryImpl) em.createNamedQuery("findAllXMLAddresses");
        List addresses = query.getResultList();
        assertTrue("Error executing named native query 'findAllXMLAddresses'", addresses != null);
    }

    public void testNamedQueryOnEmployee() {
        EJBQueryImpl query = (EJBQueryImpl) createEntityManager(m_persistenceUnit).createNamedQuery("findAllXMLEmployeesByFirstName");
        query.setParameter("firstname", "Brady");
        Employee employee = (Employee) query.getSingleResult();
        assertTrue("Error executing named query 'findAllXMLEmployeesByFirstName'", employee != null);
    }

    public void testReadEmployee() {
        Employee employee = createEntityManager(m_persistenceUnit).find(Employee.class, employeeId);
        assertTrue("Error reading Employee", employee.getId() == employeeId);
    }

    public void testUpdateEmployee() {
        EntityManager em = createEntityManager(m_persistenceUnit);
        beginTransaction(em);
        int version = 0;
        
        try {
            Employee employee = em.find(Employee.class, employeeId);
            version = employee.getVersion();
            employee.setSalary(50000);
            
            em.merge(employee);
            commitTransaction(em);
            
            // Clear cache and clear the entity manager
            clearCache(m_persistenceUnit);    
            em.clear();
            
            Employee emp = em.find(Employee.class, employeeId);
            assertTrue("Error updating Employee", emp.getSalary() == 50000);
            assertTrue("Version field not updated", emp.getVersion() == version + 1);            
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
        EntityManager em = createEntityManager(m_persistenceUnit);
        beginTransaction(em);
        try {
            Employee emp = new Employee();
            emp.setFirstName("NotManaged");
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
        EntityManager em = createEntityManager(m_persistenceUnit);
        List result = em.createQuery("SELECT OBJECT(e) FROM XMLEmployee e WHERE e.firstName = '"+firstName+"'").getResultList();
        if(!result.isEmpty()) {
            emp = (Employee)result.get(0);
        } else {
            emp = new Employee();
            emp.setFirstName(firstName);
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
                
            // delete the Employee from the db
            em.createQuery("DELETE FROM XMLEmployee e WHERE e.firstName = '"+firstName+"'").executeUpdate();

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
        EntityManager em = createEntityManager(m_persistenceUnit);
        List result = em.createQuery("SELECT OBJECT(e) FROM XMLEmployee e WHERE e.firstName = '"+firstName+"'").getResultList();
        if(!result.isEmpty()) {
            emp = (Employee)result.get(0);
        } else {
            emp = new Employee();
            emp.setFirstName(firstName);
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
        EntityManager em = createEntityManager(m_persistenceUnit);
        List result = em.createQuery("SELECT OBJECT(e) FROM XMLEmployee e WHERE e.firstName = '"+firstName+"'").getResultList();
        if(!result.isEmpty()) {
            emp = (Employee)result.get(0);
        } else {
            emp = new Employee();
            emp.setFirstName(firstName);
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
            setParameter("p2", new Integer(firstIndex)).
            setParameter("p3", new Integer(lastIndex)).
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
        EntityManager em = createEntityManager(m_persistenceUnit);
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
            
            Address address2 = (Address) em.createNamedQuery("SProcXMLAddress").setParameter("ADDRESS_ID", address1.getId()).getSingleResult();
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
     * Tests a named-stored-procedure-query setting
     */
    public void testNamedStoredProcedureQueryInOut() {
        EntityManager em = createEntityManager(m_persistenceUnit);
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

            Address address2 = (Address)em.createNamedQuery("SProcXMLInOut").setParameter("ADDRESS_ID", address1.getId()).getSingleResult();
        
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
        EntityManager em = createEntityManager(m_persistenceUnit);
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
        this.clearCache(m_persistenceUnit);        
        // read the employee from the db
        em = createEntityManager(m_persistenceUnit);
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

    public void testClassInstanceConverter(){
        EntityManager em = createEntityManager(m_persistenceUnit);
        beginTransaction(em);
        Address add = new Address();
        add.setCity("St. Louis");
        add.setType(new Bungalow());
        em.persist(add);
        commitTransaction(em);
        int assignedSequenceNumber = add.getId();
        
        em.clear();
        getServerSession(m_persistenceUnit).getIdentityMapAccessor().initializeAllIdentityMaps();

        add = em.find(Address.class, assignedSequenceNumber);

        assertTrue("Did not correctly persist a mapping using a class-instance converter", (add.getType() instanceof Bungalow));

        beginTransaction(em);
        em.remove(add);
        commitTransaction(em);
    }
    

    /**
     * Tests Property and Properties annotations
     */
    public void testProperty() {
        EntityManager em = createEntityManager(m_persistenceUnit);
        ClassDescriptor descriptor = ((EntityManagerImpl) em).getServerSession().getDescriptorForAlias("XMLEmployee");
        ClassDescriptor aggregateDescriptor = ((EntityManagerImpl) em).getServerSession().getDescriptor(EmploymentPeriod.class);
        em.close();
        
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
        errorMsg += verifyPropertyValue(descriptor, "entityIntegerProperty", Integer.class, new Integer(1));
        errorMsg += verifyPropertyValue(descriptor, "ToBeOverriddenByXml", Boolean.class, Boolean.TRUE);
        errorMsg += verifyPropertyValue(descriptor, "ToBeProcessed", Boolean.class, Boolean.TRUE);
        
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
        errorMsg += verifyPropertyValue(mapping, "TimeProperty", java.sql.Time.class, Helper.timeFromString("13:59:59"));
        errorMsg += verifyPropertyValue(mapping, "TimeStampProperty", java.sql.Timestamp.class, Helper.timestampFromString("2008-04-10 13:59:59"));
        errorMsg += verifyPropertyValue(mapping, "DateProperty", java.sql.Date.class, Helper.dateFromString("2008-04-10"));
        
        errorMsg += verifyPropertyValue(mapping, "ToBeIgnored", null, null);
        
        // verify property set on EmploymentPeriod embeddable
        errorMsg += verifyPropertyValue(aggregateDescriptor, "embeddableClassName", String.class, "EmploymentPeriod");
        errorMsg += verifyPropertyValue(aggregateDescriptor, "ToBeOverriddenByXml", Boolean.class, Boolean.TRUE);
        errorMsg += verifyPropertyValue(aggregateDescriptor, "ToBeProcessed", Boolean.class, Boolean.TRUE);
        
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
        clearCache(m_persistenceUnit);
        
        // read the persisted employees back
        EntityManager em = createEntityManager(m_persistenceUnit);
        List<Employee> employeesRead = em.createQuery("SELECT OBJECT(e) FROM XMLEmployee e WHERE e.lastName = '"+lastName+"'").getResultList();
        // while em is open, cache ServerSession that will be used later for verification
        ServerSession session = ((EntityManagerImpl)em).getServerSession();
        closeEntityManager(em);

        // verify number persisted and read is the same
        if(employeesPersisted.size() != employeesRead.size()) {
            //clean-up
            deleteEmployeesWithUnidirectionalMappings(lastName);
            fail("Persisted " + employeesPersisted.size() + " employees, but read back " + employeesRead.size());
        }

        // verify that the persisted and read objects are equal
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
        EntityManager em = createEntityManager(m_persistenceUnit);
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
            em.close();
        }
        
        // clear cache
        clearCache(m_persistenceUnit);
        
        em = createEntityManager(m_persistenceUnit);
        // read the updated employees back
        List<Employee> employeesRead = em.createQuery("SELECT OBJECT(e) FROM XMLEmployee e WHERE e.lastName = '"+lastName+"'").getResultList();
        // while em is open, cache ServerSession that will be used later for verification
        ServerSession session = ((EntityManagerImpl)em).getServerSession();
        closeEntityManager(em);
        
        // verify number persisted and read is the same
        if(employeesPersisted.size() != employeesRead.size()) {
            // clean-up
            deleteEmployeesWithUnidirectionalMappings(lastName);
            fail("Updated " + employeesPersisted.size() + " employees, but read back " + employeesRead.size());
        }
        
        // verify that the persisted and read objects are equal
        String errorMsg = "";
        for(int i=0; i<employeesPersisted.size(); i++) {
            for(int j=0; j<employeesRead.size(); j++) {
                if(employeesPersisted.get(i).getFirstName().equals(employeesRead.get(j).getFirstName())) {
                    if(!session.compareObjects(employeesPersisted.get(i), employeesRead.get(j))) {
                        errorMsg += "Employee " + employeesPersisted.get(i).getFirstName() +"  was not updated correctly.";
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
    
    public void testUnidirectionalFetchJoin() {
        String lastName = "testUnidirectionalFetchJoin";

        // persist employees
        persistEmployeesWithUnidirectionalMappings(lastName);
        
        // clear cache
        clearCache(m_persistenceUnit);
        
        EntityManager em = createEntityManager(m_persistenceUnit);
        // read the persisted employees back - without fetch join
        List<Employee> employeesRead = em.createQuery("SELECT OBJECT(e) FROM XMLEmployee e WHERE e.lastName = '"+lastName+"'").getResultList();
        closeEntityManager(em);
        
        // clear cache
        clearCache(m_persistenceUnit);
        
        // read the persisted employees back - with fetch join. 
        em = createEntityManager(m_persistenceUnit);
        List<Employee> employeesReadWithFetchJoin = em.createQuery("SELECT e FROM XMLEmployee e JOIN FETCH e.dealers WHERE e.lastName = '"+lastName+"'").getResultList();
        // while em is open, cache ServerSession that will be used later for verification
        ServerSession session = ((EntityManagerImpl)em).getServerSession();
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
        String errorMsg = JoinedAttributeTestHelper.compareCollections(employeesControl, employeesReadWithFetchJoin, session.getClassDescriptor(Employee.class), session);
        
        // clean-up
        deleteEmployeesWithUnidirectionalMappings(lastName);
        
        // non-empty error message means the test has failed
        if(errorMsg.length() > 0) {
            fail(errorMsg);
        }
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
        EntityManager em = createEntityManager(m_persistenceUnit);
        try {
            return persistEmployeesWithUnidirectionalMappings(lastName, em);
        } finally {
            em.close();
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
        EntityManager em = createEntityManager(m_persistenceUnit);
        beginTransaction(em);
        try {
            em.createQuery("DELETE FROM XMLAdvancedCustomer c WHERE c.lastName = '"+lastName+"'").executeUpdate();
            em.createQuery("DELETE FROM XMLDealer d WHERE d.lastName = '"+lastName+"'").executeUpdate();
            em.createQuery("DELETE FROM XMLEmployee e WHERE e.lastName = '"+lastName+"'").executeUpdate();
            commitTransaction(em);
        } finally {
            if(this.isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            em.close();
            clearCache(m_persistenceUnit);
        }
    }    
}
