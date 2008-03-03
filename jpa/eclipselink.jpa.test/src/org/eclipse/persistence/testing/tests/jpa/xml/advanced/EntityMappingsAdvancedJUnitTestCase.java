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

import java.sql.Date;
import java.util.ArrayList;
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
import org.eclipse.persistence.internal.jpa.EJBQueryImpl;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.EmployeePopulator;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.Address;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.EmploymentPeriod;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.ModelExamples;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.PhoneNumber;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.Project;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.ReadOnlyClass;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
 
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
    
    public static Test suite(final String persistenceUnit) {
        TestSuite suite = new TestSuite("Advanced Model - " + persistenceUnit);
        suite.addTest(new EntityMappingsAdvancedJUnitTestCase("testCreateEmployee", persistenceUnit));
        suite.addTest(new EntityMappingsAdvancedJUnitTestCase("testReadEmployee", persistenceUnit));
        suite.addTest(new EntityMappingsAdvancedJUnitTestCase("testNamedNativeQueryOnAddress", persistenceUnit));
        suite.addTest(new EntityMappingsAdvancedJUnitTestCase("testNamedQueryOnEmployee", persistenceUnit));
        suite.addTest(new EntityMappingsAdvancedJUnitTestCase("testUpdateEmployee", persistenceUnit));
        suite.addTest(new EntityMappingsAdvancedJUnitTestCase("testRefreshNotManagedEmployee", persistenceUnit));
        suite.addTest(new EntityMappingsAdvancedJUnitTestCase("testRefreshRemovedEmployee", persistenceUnit));
        suite.addTest(new EntityMappingsAdvancedJUnitTestCase("testDeleteEmployee", persistenceUnit));
        
        if (persistenceUnit.equals("extended")) {
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
     * Employee has an AUTO setting which means internally an
     * AttributeChangePolicy should be set since the class has been weaved.
     */
    public void testEmployeeChangeTrackingPolicy() {
        ServerSession session = JUnitTestCase.getServerSession(m_persistenceUnit);
        ClassDescriptor descriptor = session.getDescriptor(Employee.class);
     
        assertFalse("Employee descriptor was not found in the PU [" + m_persistenceUnit + "]", descriptor == null);
        assertTrue("Employee descriptor has incorrect object change policy", descriptor.getObjectChangePolicy().isAttributeChangeTrackingPolicy());
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

}
