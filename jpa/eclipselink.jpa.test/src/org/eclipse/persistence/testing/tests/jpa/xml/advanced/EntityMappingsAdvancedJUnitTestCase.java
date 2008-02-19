/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.xml.advanced;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;

import junit.framework.*;
import junit.extensions.TestSetup;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.jpa.EJBQueryImpl;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.Address;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.EmploymentPeriod;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.ModelExamples;
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
        	suite.addTest(new EntityMappingsAdvancedJUnitTestCase("testJoinFetchSetting", persistenceUnit));
        	suite.addTest(new EntityMappingsAdvancedJUnitTestCase("testExtendedEmployee", persistenceUnit));
        	suite.addTest(new EntityMappingsAdvancedJUnitTestCase("testGiveExtendedEmployeeASexChange", persistenceUnit));
        }
        
        return new TestSetup(suite) {
            
            protected void setUp() {
            	DatabaseSession session = JUnitTestCase.getServerSession(persistenceUnit);   
            	new AdvancedTableCreator().replaceTables(session);
            }
        
            protected void tearDown() {
                clearCache(persistenceUnit);
            }
        };
    }
    
    /**
     * Verifies that the join-fetch setting was read correctly from XML.
     */
    public void testJoinFetchSetting() {
        ServerSession session = JUnitTestCase.getServerSession(m_persistenceUnit);
        ClassDescriptor descriptor = session.getDescriptor(Employee.class);
        
        if (((ForeignReferenceMapping)descriptor.getMappingForAttributeName("address")).getJoinFetch() != ForeignReferenceMapping.OUTER_JOIN) {
            fail("join-fetch setting from XML was not read correctly for Employee's address.");
        }
    }
    
    public void testCreateEmployee() {
        EntityManager em = createEntityManager(m_persistenceUnit);
        em.getTransaction().begin();
        try {
            Employee employee = ModelExamples.employeeExample1();
            ArrayList projects = new ArrayList();
            projects.add(ModelExamples.projectExample1());
            projects.add(ModelExamples.projectExample2());
            employee.setProjects(projects);
            em.persist(employee);
            employeeId = employee.getId();
            em.getTransaction().commit();    
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()){
                em.getTransaction().rollback();
            }
            em.close();
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
        em.getTransaction().begin();
        
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
            em.getTransaction().commit();
            
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
            if (em.getTransaction().isActive()){
                em.getTransaction().rollback();
            }
            
            em.close();
            throw e;
        }
        
        em.close();
    }
    
    /**
     * Tests an ObjectTypeConverter on a direct to field mapping.
     */
    public void testGiveExtendedEmployeeASexChange() {
        EntityManager em = createEntityManager(m_persistenceUnit);
        em.getTransaction().begin();
        
        try {
        	Employee maleEmp = em.find(Employee.class, extendedEmployeeId);    
        	maleEmp.setFemale();
        	maleEmp.setFirstName("Girl");
            em.getTransaction().commit();
                
            // Clear cache and clear the entity manager
            clearCache(m_persistenceUnit);    
            em.clear();
                
            Employee girlEmp = em.find(Employee.class, extendedEmployeeId);
            assertTrue("The extended employee's change didn't occur.", girlEmp.isFemale());
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()){
                em.getTransaction().rollback();
            }
            
            em.close();
            throw e;
        }
        
        em.close();
    }
    
    public void testDeleteEmployee() {
        EntityManager em = createEntityManager(m_persistenceUnit);
        em.getTransaction().begin();
        try {
            em.remove(em.find(Employee.class, employeeId));
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()){
                em.getTransaction().rollback();
            }
            em.close();
            throw e;
        }
        assertTrue("Error deleting Employee", em.find(Employee.class, employeeId) == null);
    }

    public void testNamedNativeQueryOnAddress() {
        EntityManager em = createEntityManager(m_persistenceUnit);
        em.getTransaction().begin();
        try {
            Address address1 = ModelExamples.addressExample1();
            em.persist(address1);
            Address address2 = ModelExamples.addressExample2();
            em.persist(address2);
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()){
                em.getTransaction().rollback();
            }
            em.close();
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
        em.getTransaction().begin();
        int version = 0;
        
        try {
            Employee employee = em.find(Employee.class, employeeId);
            version = employee.getVersion();
            employee.setSalary(50000);
            
            em.merge(employee);
            em.getTransaction().commit();
            
            // Clear cache and clear the entity manager
            clearCache(m_persistenceUnit);    
            em.clear();
            
            Employee emp = em.find(Employee.class, employeeId);
            assertTrue("Error updating Employee", emp.getSalary() == 50000);
            assertTrue("Version field not updated", emp.getVersion() == version + 1);            
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()){
                em.getTransaction().rollback();
            }
            
            em.close();
            throw e;
        }
        
        em.close();
    }

    public void testRefreshNotManagedEmployee() {
        EntityManager em = createEntityManager(m_persistenceUnit);
        em.getTransaction().begin();
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
            if (em.getTransaction().isActive()){
                em.getTransaction().rollback();
            }
            em.close();
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
            em.getTransaction().begin();
            try {
                em.persist(emp);
                em.getTransaction().commit();
            } catch (RuntimeException e) {
                if (em.getTransaction().isActive()){
                    em.getTransaction().rollback();
                }
                em.close();
                throw e;
            }
        }
        
        em.getTransaction().begin();
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
            if (em.getTransaction().isActive()){
                em.getTransaction().rollback();
            }
            em.close();
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
            em.getTransaction().begin();
            try{
                em.persist(emp);
                em.getTransaction().commit();
            } catch (RuntimeException e) {
                if (em.getTransaction().isActive()){
                    em.getTransaction().rollback();
                }
                em.close();
                throw e;
            }
        }
        
        boolean containsRemoved = true;
        em.getTransaction().begin();
        try{
            emp = em.find(Employee.class, emp.getId());
            em.remove(emp);
            containsRemoved = em.contains(emp);
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()){
                em.getTransaction().rollback();
            }
            em.close();
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
            em.getTransaction().begin();
            try{
                em.persist(emp);
                em.getTransaction().commit();
            } catch (RuntimeException e) {
                if (em.getTransaction().isActive()){
                    em.getTransaction().rollback();
                }
                em.close();
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
        em.getTransaction().begin();
        try{
            em.createQuery("DELETE FROM XMLEmployee e WHERE e.firstName = '"+firstName+"'").executeUpdate();
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()){
                em.getTransaction().rollback();
            }
            em.close();
            throw e;
        }

        assertFalse("employees.isEmpty()==true ", employees.isEmpty());
    }

}
