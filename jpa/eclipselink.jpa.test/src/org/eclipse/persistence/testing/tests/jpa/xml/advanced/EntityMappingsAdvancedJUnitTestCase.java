/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     09/23/2008-1.1 Guy Pelletier 
 *       - 241651: JPA 2.0 Access Type support
 *     03/08/2010-2.1 Guy Pelletier 
 *       - 303632: Add attribute-type for mapping attributes to EclipseLink-ORM
 *     03/29/2010-2.1 Guy Pelletier 
 *       - 267217: Add Named Access Type to EclipseLink-ORM
 *     04/09/2010-2.1 Guy Pelletier 
 *       - 307050: Add defaults for access methods of a VIRTUAL access type
 *     06/16/2010-2.2 Guy Pelletier 
 *       - 247078: eclipselink-orm.xml schema should allow lob and enumerated on version and id mappings
 *     10/15/2010-2.2 Guy Pelletier 
 *       - 322008: Improve usability of additional criteria applied to queries at the session/EM
 *     07/03/2011-2.3.1 Guy Pelletier 
 *       - 348756: m_cascadeOnDelete boolean should be changed to Boolean
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.xml.advanced;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.persistence.Query;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;

import junit.framework.*;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.SelectedFieldsLockingPolicy;
import org.eclipse.persistence.descriptors.invalidation.CacheInvalidationPolicy; 
import org.eclipse.persistence.descriptors.invalidation.TimeToLiveCacheInvalidationPolicy; 
import org.eclipse.persistence.internal.descriptors.OptimisticLockingPolicy;  
import org.eclipse.persistence.internal.helper.ClassConstants; 
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EntityAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.XMLAttributes;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.BasicAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.BasicCollectionAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.OneToManyAccessor;
import org.eclipse.persistence.internal.jpa.metadata.mappings.CascadeMetadata;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappingsWriter;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLPersistenceUnitMetadata;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLPersistenceUnitDefaults;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.mappings.converters.ObjectTypeConverter;
import org.eclipse.persistence.queries.DoesExistQuery;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.Bungalow;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.Confidant;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.Department;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.EmployeePopulator;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.Address;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.Customer;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.Dealer;
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
import org.eclipse.persistence.testing.models.jpa.xml.advanced.SmallProject;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.ShovelSections.MaterialType;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.Violation;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.ViolationCode;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.Violation.ViolationID;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.ViolationCode.ViolationCodeId;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.additionalcriteria.Bolt;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.additionalcriteria.Nut;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.additionalcriteria.School;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.additionalcriteria.Student;
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
    private static long rbc = 4783;
    private static long scotia = 8732;
    private static long td = 839362;
    private static long cibc = 948274;
    private String m_persistenceUnit;
    
    public EntityMappingsAdvancedJUnitTestCase() {
        super();
    }
    
    public EntityMappingsAdvancedJUnitTestCase(String name) {
        super(name);
    }
    
    public EntityMappingsAdvancedJUnitTestCase(String name, String persistenceUnit) {
        super(name);
        
        m_persistenceUnit = persistenceUnit;
    }
    
    public static Test suite() {
        String ormTesting = TestingProperties.getProperty(TestingProperties.ORM_TESTING, TestingProperties.JPA_ORM_TESTING);
        final String persistenceUnit = ormTesting.equals(TestingProperties.JPA_ORM_TESTING)? "default" : "extended-advanced";

        TestSuite suite = new TestSuite("Advanced Model - " + persistenceUnit);
        suite.addTest(new EntityMappingsAdvancedJUnitTestCase("testSetup", persistenceUnit));
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
        suite.addTest(new EntityMappingsAdvancedJUnitTestCase("testUnidirectionalTargetLocking_AddRemoveTarget", persistenceUnit));
        suite.addTest(new EntityMappingsAdvancedJUnitTestCase("testUnidirectionalTargetLocking_DeleteSource", persistenceUnit));
        suite.addTest(new EntityMappingsAdvancedJUnitTestCase("testXMLEntityMappingsWriteOut", persistenceUnit));
        
        if (persistenceUnit.equals("extended-advanced")) {
            suite.addTest(new EntityMappingsAdvancedJUnitTestCase("testSexObjectTypeConverterDefaultValue", persistenceUnit));
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
            
            // These are dynamic persistence tests.
            suite.addTest(new EntityMappingsAdvancedJUnitTestCase("testAttributeTypeSpecifications", persistenceUnit));
            suite.addTest(new EntityMappingsAdvancedJUnitTestCase("testMockDynamicClassCRUD", persistenceUnit));
            
            suite.addTest(new EntityMappingsAdvancedJUnitTestCase("testEnumeratedPrimaryKeys", persistenceUnit));

            suite.addTest(new EntityMappingsAdvancedJUnitTestCase("testAdditionalCriteriaModelPopulate", persistenceUnit));
            suite.addTest(new EntityMappingsAdvancedJUnitTestCase("testAdditionalCriteria", persistenceUnit));
            suite.addTest(new EntityMappingsAdvancedJUnitTestCase("testAdditionalCriteriaWithParameterFromEM1", persistenceUnit));
            suite.addTest(new EntityMappingsAdvancedJUnitTestCase("testAdditionalCriteriaWithParameterFromEM2", persistenceUnit));
            suite.addTest(new EntityMappingsAdvancedJUnitTestCase("testAdditionalCriteriaWithParameterFromEMF", persistenceUnit));
            suite.addTest(new EntityMappingsAdvancedJUnitTestCase("testComplexAdditionalCriteria", persistenceUnit));
            suite.addTest(new EntityMappingsAdvancedJUnitTestCase("testDeleteAll", persistenceUnit));
        }
        
        return suite;
    }
    
    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        DatabaseSession session = JUnitTestCase.getServerSession(m_persistenceUnit);   
        AdvancedTableCreator atc = new AdvancedTableCreator();
        atc.replaceTables(session);
        // Populate the database with our examples.
        EmployeePopulator employeePopulator = new EmployeePopulator();
        employeePopulator.buildExamples();
        employeePopulator.persistExample(session);
        clearCache(m_persistenceUnit);
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
     * Test user defined additional criteria with no parameters.
     */
    public void testAdditionalCriteriaModelPopulate() {
        EntityManager em = createEntityManager(m_persistenceUnit);
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
    }
    
    /**
     * Test user defined additional criteria with no parameters. The additional
     * criteria on school filters for Ottawa named schools.
     */
    public void testAdditionalCriteria() {
        EntityManager em = createEntityManager(m_persistenceUnit);

        try {           
            List schools = em.createNamedQuery("findJPQLXMLSchools").getResultList();
            assertTrue("Incorrect number of schools were returned [" + schools.size() + "], expected [2]",  schools.size() == 2);
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
        EntityManager em = createEntityManager(m_persistenceUnit);

        try {
            // This should override the EMF property of Montreal%
            em.setProperty("NAME", "Ottawa%");
            
            // Find the schools, because of our additional criteria on Student
            // and the property above, we should only return Ottawa students.
            
            List students = em.createQuery("SELECT s from XMLStudent s").getResultList();
            assertTrue("Incorrect number of students were returned [" + students.size() + "], expected [8]",  students.size() == 8);        
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
        EntityManager em = createEntityManager(m_persistenceUnit);

        try {
            // This should override the EMF property of Montreal%
            em.setProperty("NAME", "Toronto%");
            
            // Find the schools, because of our additional criteria on Student
            // and the property above, we should only return Toronto students.
            // However, they should not have any schools loaded.
            
            List students = em.createQuery("SELECT s from XMLStudent s").getResultList();
            assertTrue("Incorrect number of students were returned [" + students.size() + "], expected [18]",  students.size() == 18);           
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
        EntityManager em = createEntityManager(m_persistenceUnit);

        try {
            // This should use the EMF NAME property of Montreal%
            List students = em.createQuery("SELECT s from XMLStudent s").getResultList();
            assertTrue("Incorrect number of students were returned [" + students.size() + "], expected [5]",  students.size() == 5); 
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
        EntityManager em = createEntityManager(m_persistenceUnit);

        try {
            em.setProperty("NUT_SIZE", 8);
            em.setProperty("NUT_COLOR", "Grey");
            
            List bolts = em.createQuery("SELECT b from XMLBolt b").getResultList();
            assertTrue("Incorrect number of bolts were returned [" + bolts.size() + "], expected [2]",  bolts.size() == 2);
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
        ServerSession session = JUnitTestCase.getServerSession(m_persistenceUnit);
        
        ClassDescriptor employeeDescriptor = session.getDescriptor(Employee.class);
        DirectToFieldMapping mapping = (DirectToFieldMapping) employeeDescriptor.getMappingForAttributeName("gender");
        assertNotNull("Gender mapping from Employee not found.", mapping);
        assertTrue("No object type converter found on the gender mapping.", ObjectTypeConverter.class.isAssignableFrom(mapping.getConverter().getClass()));
        assertTrue("Default object value on the object type converter for gender was not set to Male.", ((ObjectTypeConverter) mapping.getConverter()).getDefaultAttributeValue().equals(Employee.Gender.Male.name()));
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
        if (!JUnitTestCase.isWeavingEnabled(m_persistenceUnit)) {
            return;
        }
        
        ServerSession session = JUnitTestCase.getServerSession(m_persistenceUnit);
        ClassDescriptor descriptor = session.getDescriptor(PhoneNumber.class);
     
        assertFalse("PhoneNumber descriptor was not found in the PU [" + m_persistenceUnit + "]", descriptor == null);
        assertTrue("PhoneNumber descriptor has incorrect object change policy", descriptor.getObjectChangePolicy().isAttributeChangeTrackingPolicy());
    }
    
    /**
     * Verifies that the change tracking metadata is correctly processed.
     */
    public void testProjectChangeTrackingPolicy() {
        if (!JUnitTestCase.isWeavingEnabled(m_persistenceUnit)) {
            return;
        }
        
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
                List<DatabaseField> lockFields = ((SelectedFieldsLockingPolicy) policy).getLockFields();
                
                if (lockFields.isEmpty() || lockFields.size() > 1) {
                    fail("Invalid amount of lock fields were set on Project's selected fields locking policy.");
                } else {
                    DatabaseField lockField = lockFields.get(0);
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
            assertTrue("RBC credit line did not persist correctly.", emp.hasRoyalBankCreditLine(rbc));
            assertTrue("Scotia credit line did not persist correctly.", emp.hasScotiabankCreditLine(scotia));
            assertTrue("TD credit line did not persist correctly.", emp.hasTorontoDominionCreditLine(td));
            assertTrue("CIBC credit line did not persist correctly.", emp.hasCanadianImperialCreditLine(cibc));
            
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
            maleEmp.setGivenName("Girl");
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
        Query query = em.createNamedQuery("findAllXMLAddresses");
        List addresses = query.getResultList();
        assertTrue("Error executing named native query 'findAllXMLAddresses'", addresses != null);
    }

    public void testNamedQueryOnEmployee() {
        Query query = createEntityManager(m_persistenceUnit).createNamedQuery("findAllXMLEmployeesByFirstName");
        query.setParameter("firstname", "Brady");
        Employee employee = (Employee) query.getSingleResult();
        assertTrue("Error executing named query 'findAllXMLEmployeesByFirstName'", employee != null);
    }

    public void testReadEmployee() {
        Employee employee = createEntityManager(m_persistenceUnit).find(Employee.class, employeeId);
        assertTrue("Error reading Employee", employee.getId() == employeeId.intValue());
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
        EntityManager em = createEntityManager(m_persistenceUnit);
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
                
            if (getServerSession(m_persistenceUnit).getPlatform().isSymfoware()){
                // Symfoware does not support deleteall with multiple table
                em.createNativeQuery("DELETE FROM CMP3_XML_EMPLOYEE WHERE F_NAME = '"+firstName+"'").executeUpdate();
            } else {
                // delete the Employee from the db
                em.createQuery("DELETE FROM XMLEmployee e WHERE e.firstName = '"+firstName+"'").executeUpdate();
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
        EntityManager em = createEntityManager(m_persistenceUnit);
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
        EntityManager em = createEntityManager(m_persistenceUnit);
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
        if (!supportsStoredProcedures(m_persistenceUnit)) {
            return;
        }
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
            
            // 260263 and 302316: clear the cache or we will end up with a false positive when comparing the entity to itself later
            em.clear();

            Query aQuery = em.createNamedQuery("SProcXMLAddress").setParameter("ADDRESS_ID", address1.getId());
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
     * Tests a named-stored-procedure-query setting
     */
    public void testNamedStoredProcedureQueryInOut() {
        if (!supportsStoredProcedures(m_persistenceUnit)) {
            return;
        }
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
            // 260263 and 302316: clear the cache or we will end up with a false positive when comparing the entity to itself later
            em.clear();

            Query aQuery = em.createNamedQuery("SProcXMLInOut").setParameter("ADDRESS_ID", address1.getId());
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
        ServerSession session;
        if (isOnServer()) {
            session = getServerSession();
        } else {
            session = ((EntityManagerImpl)em).getServerSession();
        }
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
            closeEntityManager(em);
        }
        
        // clear cache
        clearCache(m_persistenceUnit);
        
        em = createEntityManager(m_persistenceUnit);
        // read the updated employees back
        List<Employee> employeesRead = em.createQuery("SELECT OBJECT(e) FROM XMLEmployee e WHERE e.lastName = '"+lastName+"'").getResultList();
        // while em is open, cache ServerSession that will be used later for verification
        ServerSession session;
        if (isOnServer()) {
            session = getServerSession();
        } else {
            session = ((EntityManagerImpl)em).getServerSession();
        }
        
        // verify number persisted and read is the same
        if(employeesPersisted.size() != employeesRead.size()) {
            // clean-up
            deleteEmployeesWithUnidirectionalMappings(lastName);
            fail("Updated " + employeesPersisted.size() + " employees, but read back " + employeesRead.size());
        }
        
        // verify that the persisted and read objects are equal
        beginTransaction(em);
        String errorMsg = "";
        try{
            for(int i=0; i<employeesPersisted.size(); i++) {
                for(int j=0; j<employeesRead.size(); j++) {
                    Employee emp1 = em.find(Employee.class, employeesPersisted.get(i).getId());
                    Employee emp2 = em.find(Employee.class, employeesRead.get(j).getId());
                    if(emp1.getFirstName().equals(emp2.getFirstName())) {
                        if(!session.compareObjects(emp1, emp2)) {
                            errorMsg += "Employee " + emp1.getFirstName() +"  was not updated correctly.";
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
        ServerSession session;
        if (isOnServer()) {
            session = getServerSession();
        } else {
            session = ((EntityManagerImpl)em).getServerSession();
        }

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
    
    public void testUnidirectionalTargetLocking_AddRemoveTarget() {
        String lastName = "testUnidirectionalTargetLocking_ART";
        
        EntityManager em = createEntityManager(m_persistenceUnit);
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
        ArrayList<Integer> dealersIds = new ArrayList<Integer>();
        for(int i=0; i<persistedEmployees.size(); i++) {
            Employee emp = persistedEmployees.get(i);
            for(int j=0; j<emp.getDealers().size(); j++) {
                dealersIds.add(emp.getDealers().get(j).getId());
            }
        }
        
        // clear cache
        clearCache(m_persistenceUnit);
        
        EntityManager em = createEntityManager(m_persistenceUnit);
        beginTransaction(em);
        // read the persisted employees
        List<Employee> readEmployees = em.createQuery("SELECT OBJECT(e) FROM XMLEmployee e WHERE e.lastName = '"+lastName+"'").getResultList();
        
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
        
        if (isOnServer()) {
            return ((Integer)getServerSession().getDescriptor(Dealer.class).getOptimisticLockingPolicy().getWriteLockValue(dealer, pk, getServerSession())).intValue();
        } else {
            return ((Integer)((EntityManagerImpl)em).getServerSession().getDescriptor(Dealer.class).getOptimisticLockingPolicy().getWriteLockValue(dealer, pk, ((EntityManagerImpl)em).getServerSession())).intValue();
        }
    }

    protected List<Employee> createEmployeesWithUnidirectionalMappings(String lastName) {
        int n = 2;
        List<Employee> employees = new ArrayList<Employee>(n);
        for(int i=0; i<n; i++) {
            Employee emp = new Employee();
            emp.setGivenName(Integer.toString(i+1));
            emp.setFamilyName(lastName);
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
        EntityManager em = createEntityManager(m_persistenceUnit);
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
            clearCache(m_persistenceUnit);
        }
    }    
    
    /**
     * Verifies that attribute-types are correctly processed and used.
     */
    public void testAttributeTypeSpecifications() {
        EntityManager em = createEntityManager(m_persistenceUnit);
        
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
            
            clearCache(m_persistenceUnit);
            em.clear();
            Loner refreshedLoner = em.find(Loner.class, lonerId);
            assertTrue("Loner objects didn't match", getServerSession(m_persistenceUnit).compareObjects(loner, refreshedLoner));
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
        EntityManager em = createEntityManager(m_persistenceUnit);
        
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
            
            clearCache(m_persistenceUnit);
            em.clear();
            
            Shovel refreshedShovel = em.find(Shovel.class, shovelId);
            assertTrue("Shovel didn't match after write/read", getServerSession(m_persistenceUnit).compareObjects(shovel, refreshedShovel));
            
            // Do an update
            beginTransaction(em);
            
            em.merge(refreshedShovel);
            refreshedShovel.setMy("cost", Double.valueOf(7.99));
            
            commitTransaction(em);
            
            clearCache(m_persistenceUnit);
            em.clear();
            
            Shovel refreshedUpdatedShovel = em.find(Shovel.class, shovelId);
            assertTrue("Shovel didn't match after update", getServerSession(m_persistenceUnit).compareObjects(refreshedShovel, refreshedUpdatedShovel));
            
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
    }   
    
    /**
     * Fix for bug 247078: eclipselink-orm.xml schema should allow lob and 
     * enumerated on version and id mappings 
     */
    public void testEnumeratedPrimaryKeys(){
        EntityManager em = createEntityManager(m_persistenceUnit);
        
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
            clearCache(m_persistenceUnit);
            em.clear();
            Violation refreshedViolation = em.find(Violation.class, violation.getId());
            assertNotNull("Unable to read back the violation", refreshedViolation);
            assertTrue("Violation object did not match after refresh", getServerSession(m_persistenceUnit).compareObjects(violation, refreshedViolation));
        } catch (Exception e) {
            fail("An error occurred: " + e.getMessage());
        } finally {
            closeEntityManager(em);
        }
    }
    
    public void testXMLEntityMappingsWriteOut() {
        try {
            XMLEntityMappings mappings = new XMLEntityMappings();
            mappings.setPersistenceUnitMetadata(new XMLPersistenceUnitMetadata());
            mappings.getPersistenceUnitMetadata().setPersistenceUnitDefaults(new XMLPersistenceUnitDefaults());
            XMLPersistenceUnitMetadata persistenceUnitMetadata = new XMLPersistenceUnitMetadata();
            persistenceUnitMetadata.setPersistenceUnitDefaults(new XMLPersistenceUnitDefaults());
            mappings.setPersistenceUnitMetadata(persistenceUnitMetadata);
            EntityAccessor entity = new EntityAccessor();
            mappings.setEntities(new ArrayList<EntityAccessor>());
            entity.setAttributes(new XMLAttributes());
            entity.getAttributes().setBasicCollections(new ArrayList<BasicCollectionAccessor>());
            entity.getAttributes().getBasicCollections().add(new BasicCollectionAccessor());
            entity.getAttributes().setOneToManys(new ArrayList<OneToManyAccessor>());
            OneToManyAccessor oneToMany = new OneToManyAccessor();
            oneToMany.setCascade(new CascadeMetadata());
            entity.getAttributes().getOneToManys().add(oneToMany);
            entity.getAttributes().setBasics(new ArrayList<BasicAccessor>());
            entity.getAttributes().getBasics().add(new BasicAccessor());
            
            mappings.getEntities().add(entity);
            
            XMLEntityMappingsWriter writer = new XMLEntityMappingsWriter();
            FileOutputStream fileOut = new FileOutputStream("XMLWriteOutTest.xml");
            writer.write(mappings, fileOut);
            fileOut.close();
            
            FileInputStream fileIn = new FileInputStream(fileOut.getFD()); 
            BufferedReader in = new BufferedReader(new InputStreamReader(fileIn));
            HashSet<String> readStrings = new HashSet<String>();
            while (in.ready()) {
                readStrings.add(in.readLine());
            }
            in.close();
                
            // Now look for those empty types that we should not see when not explicitly set ...
            assertFalse("Found cascade on delete element", readStrings.contains("<cascade-on-delete"));
            assertFalse("Found non cacheable element", readStrings.contains("<noncacheable"));
            assertFalse("Found private owned element", readStrings.contains("<private-owned"));
            assertFalse("Found xml mapping metadata complete element", readStrings.contains("<xml-mapping-metadata-complete"));
            assertFalse("Found exclude default mappings element", readStrings.contains("<exclude-default-mappings"));
            assertFalse("Found delimeted identifiers element", readStrings.contains("<delimited-identifiers"));
            assertFalse("Found exclude default listeners element", readStrings.contains("<exclude-default-listeners"));
            assertFalse("Found exclude superclass listeners element", readStrings.contains("<exclude-superclass-listeners"));
            assertFalse("Found return update element", readStrings.contains("<return-update"));            
            assertFalse("Found cascade all element", readStrings.contains("<cascade-all"));
            assertFalse("Found cascade persist element", readStrings.contains("<cascade-persist"));
            assertFalse("Found cascade merge element", readStrings.contains("<cascade-merge"));
            assertFalse("Found cascade remove element", readStrings.contains("<cascade-remove"));
            assertFalse("Found cascade refresh element", readStrings.contains("<cascade-refresh"));
            assertFalse("Found cascade detach element", readStrings.contains("<cascade-detach"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("An error occurred: " + e.getMessage());
        }
    }

    public void testDeleteAll(){
        Map descriptors = getServerSession(m_persistenceUnit).getDescriptors();
        ClassDescriptor descriptor = (ClassDescriptor)descriptors.get(Department.class);
        OneToManyMapping mapping = (OneToManyMapping)descriptor.getMappingForAttributeName("equipment");
        assertFalse("<delete-all> xml did not work correctly", mapping.mustDeleteReferenceObjectsOneByOne());
    }
}
