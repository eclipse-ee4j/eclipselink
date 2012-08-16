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
 *     07/05/2010-2.1.1 Guy Pelletier 
 *       - 317708: Exception thrown when using LAZY fetch on VIRTUAL mapping
 *     11/17/2010-2.2 Guy Pelletier 
 *       - 329008: Support dynamic context creation without persistence.xml
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.xml.advanced;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.EntityManager;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.RelationalDescriptor;

import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.dynamic.DynamicEntity;

import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.jpa.dynamic.JPADynamicHelper;

import org.eclipse.persistence.queries.DoesExistQuery;

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.server.ServerSession;

import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCaseHelper;

import org.eclipse.persistence.testing.models.jpa.xml.advanced.dynamic.DynamicTableCreator;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.dynamic.DynamicWalkerPK;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.dynamic.MyDynamicEntity;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.dynamic.AdvancedDynamicTableCreator;

import org.eclipse.persistence.tools.schemaframework.SchemaManager;
import org.eclipse.persistence.tools.schemaframework.StoredProcedureDefinition;
 
/**
 * JUnit test case(s) for testing extended jpa dynamic persistence.
 */
public class EntityMappingsDynamicAdvancedJUnitTestCase extends JUnitTestCase {
    public enum MaterialType { Wood, Plastic, Composite, Steel }
            
    private static final String AMEX = "Amex";
    private static final String DINERS = "DinersClub";
    private static final String MASTERCARD = "Mastercard";
    private static final String VISA = "Visa";
    
    private static final String ROYAL_BANK = "RoyalBank";
    private static final String CANADIAN_IMPERIAL = "CanadianImperial";
    private static final String SCOTIABANK = "Scotiabank";
    private static final String TORONTO_DOMINION = "TorontoDominion";
    
    private static Integer employeeId;
    private static long visa = 1234567890;
    private static long amex = 1987654321;
    private static long diners = 1192837465;
    private static long mastercard = 1647382910;
    private static long rbc = 4783;
    private static long scotia = 8732;
    private static long td = 839362;
    private static long cibc = 948274;
    
    public EntityMappingsDynamicAdvancedJUnitTestCase(String name) {
        super(name);
    }
    
    /**
     * Build stored procedure used in tests.
     */
    protected StoredProcedureDefinition buildOracleStoredProcedureReadFromAddress(DatabaseSession session) {
        StoredProcedureDefinition proc = new StoredProcedureDefinition();
        proc.setName("SProc_Read_DynamicAddress");
        
        proc.addInOutputArgument("address_id_v", Integer.class);
        proc.addOutputArgument("street_v", String.class);
        proc.addOutputArgument("city_v", String.class);
        proc.addOutputArgument("country_v", String.class);
        proc.addOutputArgument("province_v", String.class);
        proc.addOutputArgument("p_code_v", String.class);
                
        /**
         * SQLServer 2008 requires extra prefix and delimiting chars in select statements in stored procedures
         */
        String statement = null;
        if(session.getPlatform().isSQLServer()  || session.getPlatform().isSybase()) {
            // 260263: SQLServer 2005/2008 requires parameter matching in the select clause for stored procedures
            statement = "SELECT @street_v=STREET, @city_v=CITY, @country_v=COUNTRY, @province_v=PROVINCE, @p_code_v=P_CODE FROM DYNAMIC_ADDRESS WHERE (ADDRESS_ID = @address_id_v)";
        } else {
            statement = "SELECT STREET, CITY, COUNTRY, PROVINCE, P_CODE INTO street_v, city_v, country_v, province_v, p_code_v FROM DYNAMIC_ADDRESS WHERE (ADDRESS_ID = address_id_v)";
        }
        
        proc.addStatement(statement);
        return proc;
    }
    
    /**
     * Build stored procedure used in tests.
     */
    protected StoredProcedureDefinition buildOracleStoredProcedureReadInOut(DatabaseSession session) {
        StoredProcedureDefinition proc = new StoredProcedureDefinition();
        proc.setName("SProc_Read_DynamicInOut");
        
        proc.addInOutputArgument("address_id_v", Long.class);
        proc.addOutputArgument("street_v", String.class);
        
        /**
         * SQLServer 2008 requires extra prefix and delimiting chars in select statements in stored procedures
         */
        String statement = null;
        if(session.getPlatform().isSQLServer() || session.getPlatform().isSybase()) {
            // 260263: SQLServer 2005/2008 requires parameter matching in the select clause for stored procedures
            statement = "SELECT @address_id_v=ADDRESS_ID, @street_v=STREET from DYNAMIC_ADDRESS where (ADDRESS_ID = @address_id_v)";
        } else {
            statement = "SELECT ADDRESS_ID, STREET into address_id_v, street_v from DYNAMIC_ADDRESS where (ADDRESS_ID = address_id_v)";
        }
        
        proc.addStatement(statement);
        return proc;
    }
    
    /**
     * Clear the cache of the dynamic persistence unit.
     */
    protected void clearDynamicPUCache() {
        clearCache("extended-dynamic-advanced");
    }
    
    /**
     * Create our dynamic persistence unit entity manager.
     */
    protected EntityManager createDynamicPUEntityManager() {
        return createEntityManager("extended-dynamic-advanced", getDynamicProperties());
    }
    
    
    /**
     * Create a dynamic entity manager from no persistence.xml file.
     */
    protected EntityManager createDynamicEntityManager(String persistenceUnitName, List<ClassDescriptor> descriptors) {
        return createEntityManager(persistenceUnitName, getDynamicProperties(), descriptors);
    }
    
    /**
     * Return the class descriptor for the given dynamic entity alias.
     */
    protected ClassDescriptor getDescriptor(String alias) {
        ClassDescriptor descriptor = getDynamicPUServerSession().getDescriptorForAlias(alias);
        assertFalse("Descriptor for alias: " + alias + ", was not found.", descriptor == null);
        return descriptor;
    }
    
    /**
     * Return the properties needed for dynamic persistence.
     */
    protected Map getDynamicProperties() {
        Map properties = JUnitTestCaseHelper.getDatabaseProperties();
        properties.put(PersistenceUnitProperties.CLASSLOADER, new DynamicClassLoader(Thread.currentThread().getContextClassLoader()));
        return properties;
    }
    
    /**
     * Return the dynamic persistence unit server session.
     */
    protected ServerSession getDynamicPUServerSession() {
        return getServerSession("extended-dynamic-advanced", getDynamicProperties());
    }
    
    /**
     * Verifies that the change tracking metadata is correctly processed. In 
     * this case a DEFERRED setting is overridden by a hard force to attribute
     * change tracking for VIRTUAL classes.
     */
    public void testAddressChangeTrackingPolicy() {
        assertTrue("Address descriptor has incorrect object change policy", getDescriptor("DynamicAddress").getObjectChangePolicyInternal().isAttributeChangeTrackingPolicy());
    }
    
    /**
     * Return the dynamic test suite.
     * @return
     */
    public static Test suite() {
        // This test suite should only be called when we are using the
        // extended-dynamic-advanced test suite.
        TestSuite suite = new TestSuite("Advanced Dynamic Model");
        
        suite.addTest(new EntityMappingsDynamicAdvancedJUnitTestCase("testSetup"));
             
        suite.addTest(new EntityMappingsDynamicAdvancedJUnitTestCase("testExistenceCheckingSetting"));
        suite.addTest(new EntityMappingsDynamicAdvancedJUnitTestCase("testReadOnlyClassSetting"));
        suite.addTest(new EntityMappingsDynamicAdvancedJUnitTestCase("testEmployeeChangeTrackingPolicy"));
        suite.addTest(new EntityMappingsDynamicAdvancedJUnitTestCase("testAddressChangeTrackingPolicy"));
        suite.addTest(new EntityMappingsDynamicAdvancedJUnitTestCase("testProjectChangeTrackingPolicy"));
        
        suite.addTest(new EntityMappingsDynamicAdvancedJUnitTestCase("testCreateEmployee"));
        suite.addTest(new EntityMappingsDynamicAdvancedJUnitTestCase("testUpdateEmployee"));
        suite.addTest(new EntityMappingsDynamicAdvancedJUnitTestCase("testDynamicShovel"));
        
        suite.addTest(new EntityMappingsDynamicAdvancedJUnitTestCase("testRefreshNotManagedEmployee"));
        suite.addTest(new EntityMappingsDynamicAdvancedJUnitTestCase("testRefreshRemovedEmployee"));
        
        suite.addTest(new EntityMappingsDynamicAdvancedJUnitTestCase("testNamedNativeQueryOnAddress"));
        suite.addTest(new EntityMappingsDynamicAdvancedJUnitTestCase("testNamedQueryOnEmployee"));
        suite.addTest(new EntityMappingsDynamicAdvancedJUnitTestCase("testNamedStoredProcedureQuery"));
        suite.addTest(new EntityMappingsDynamicAdvancedJUnitTestCase("testNamedStoredProcedureQueryInOut"));
        
        suite.addTest(new EntityMappingsDynamicAdvancedJUnitTestCase("testDeleteEmployee"));
        
        suite.addTest(new EntityMappingsDynamicAdvancedJUnitTestCase("testDynamicWithNoPersistenceXML"));
        
        suite.addTest(new EntityMappingsDynamicAdvancedJUnitTestCase("testDynamicEmbeddedId"));
        suite.addTest(new EntityMappingsDynamicAdvancedJUnitTestCase("testDynamicCompositeId"));
        
        return suite;
    }
    
    /**
     * Test create dynamic employee
     */
    public void testCreateEmployee() {
        EntityManager em = createDynamicPUEntityManager();
        JPADynamicHelper helper = new JPADynamicHelper(em);
        
        try {
            beginTransaction(em);
            
            DynamicEntity employee = helper.newDynamicEntity("DynamicEmployee");
            employee.set("firstName", "Boy");
            employee.set("lastName", "Pelletier");
            employee.set("salary", 20000);
            
            Map<String, Long> creditCards = new HashMap<String, Long>();
            creditCards.put(VISA, visa);
            creditCards.put(AMEX, amex);
            creditCards.put(DINERS, diners);
            creditCards.put(MASTERCARD, mastercard);
            employee.set("creditCards", creditCards);
            
            Map<String, Long> creditLines = new HashMap<String, Long>();
            creditLines.put(ROYAL_BANK, rbc);
            creditLines.put(SCOTIABANK, scotia);
            creditLines.put(TORONTO_DOMINION, td);
            creditLines.put(CANADIAN_IMPERIAL, cibc);
            employee.set("creditLines", creditLines);
            
            Collection<String> responsibilities = new ArrayList<String>();
            responsibilities.add("A very important responsibility");
            employee.set("responsibilities", responsibilities);
            
            DynamicEntity address = helper.newDynamicEntity("DynamicAddress");
            address.set("city", "Nepean");
            address.set("country", "Canada");
            address.set("postalCode", "K2J 6T3");
            // One it goes through the custom converter this should be set to Ontario.
            address.set("province", "oNtArIo");
            address.set("street", "321 Crestway");
            employee.set("address", address);
            
            DynamicEntity employmentPeriod = helper.newDynamicEntity("EmploymentPeriod");
            employmentPeriod.set("startDate", new Date(System.currentTimeMillis()-1000000));
            employmentPeriod.set("endDate", new Date(System.currentTimeMillis()+1000000));
            employee.set("period", employmentPeriod);
            
            ArrayList<DynamicEntity> projects = new ArrayList<DynamicEntity>();
            DynamicEntity project = helper.newDynamicEntity("DynamicProject");
            project.set("description", "A Project");
            project.set("name", "Project");
            projects.add(project);
            
            DynamicEntity largeProject = helper.newDynamicEntity("DynamicLargeProject");
            largeProject.set("description", "A LargeProject");
            largeProject.set("name", "LargeProject");
            largeProject.set("budget", 3654563.0);
            projects.add(largeProject);
            employee.set("projects", projects);
            
            em.persist(employee);
            employeeId = employee.get("id");
            commitTransaction(em);
            
            clearDynamicPUCache();
            em.clear();
            
            // Re-read the employee and verify the data.
            DynamicEntity emp = (DynamicEntity) em.find(helper.getType("DynamicEmployee").getJavaClass(), employeeId);
            //assertTrue("Employee didn't match after create", getDynamicPUServerSession().compareObjects(emp, employee));
            assertNotNull("The employee was not found", emp);
            
            // Compare all the data we persisted.
            DynamicEntity addr = emp.get("address");
            assertTrue("The address province value was not converted", addr.get("province").equals("Ontario"));
            
            Map<String, Long> ccs = emp.get("creditCards");
            assertTrue("Visa card did not persist correctly.", ccs.containsKey(VISA));
            assertTrue("Visa card value did not persist correctly.", ccs.get(VISA).equals(visa));
            assertTrue("Amex card did not persist correctly.", ccs.containsKey(AMEX));
            assertTrue("Amex card value did not persist correctly.", ccs.get(AMEX).equals(amex));
            assertTrue("Diners Club card did not persist correctly.", ccs.containsKey(DINERS));
            assertTrue("Diners Club card value did not persist correctly.", ccs.get(DINERS).equals(diners));
            assertTrue("Mastercard card did not persist correctly.", ccs.containsKey(MASTERCARD));
            assertTrue("Mastercard card value did not persist correctly.", ccs.get(MASTERCARD).equals(mastercard));
            
            Map<String, Long> cls = emp.get("creditLines");
            assertTrue("RBC credit line did not persist correctly.", cls.containsKey(ROYAL_BANK));
            assertTrue("RBC credit line value did not persist correctly.", cls.get(ROYAL_BANK).equals(rbc));
            assertTrue("Scotia credit line did not persist correctly.", cls.containsKey(SCOTIABANK));
            assertTrue("Scotia credit line value did not persist correctly.", cls.get(SCOTIABANK).equals(scotia));
            assertTrue("TD credit line did not persist correctly.", cls.containsKey(TORONTO_DOMINION));
            assertTrue("TD credit line value did not persist correctly.", cls.get(TORONTO_DOMINION).equals(td));
            assertTrue("CIBC credit line did not persist correctly.", cls.containsKey(CANADIAN_IMPERIAL));
            assertTrue("CIBC credit line value did not persist correctly.", cls.get(CANADIAN_IMPERIAL).equals(cibc));
            
            boolean found = false;
            for (String responsibility : (Collection<String>) emp.get("responsibilities")) {
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
            
            throw e;
        } finally {
            closeEntityManager(em);
        }
    }
    
    /**
     * Test delete dynamic employee
     */
    public void testDeleteEmployee() {
        EntityManager em = createDynamicPUEntityManager();
        JPADynamicHelper helper = new JPADynamicHelper(em);
        
        try {
            beginTransaction(em);
            em.remove(em.find(helper.getType("DynamicEmployee").getJavaClass(), employeeId));
            commitTransaction(em);
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
    public void testDynamicShovel() {
        EntityManager em = createDynamicPUEntityManager();
        JPADynamicHelper helper = new JPADynamicHelper(em);
        
        try {
            beginTransaction(em);
            
            // Cost
            DynamicEntity shovel = helper.newDynamicEntity("DynamicShovel");
            shovel.set("cost", Double.valueOf(9.99));
            
            // Sections
            DynamicEntity shovelSections = helper.newDynamicEntity("ShovelSections");
            shovelSections.set("handle", MaterialType.Plastic.name());
            shovelSections.set("shaft", MaterialType.Wood.name());
            shovelSections.set("scoop", MaterialType.Plastic.name());
            shovel.set("sections", shovelSections);
            
            // Owner
            DynamicEntity shovelOwner = helper.newDynamicEntity("DynamicShovelOwner");
            shovelOwner.set("name", "Mr. Shovel");
            shovel.set("owner", shovelOwner);
            
            // Operators
            DynamicEntity shovelDigger1 = helper.newDynamicEntity("DynamicShovelDigger");
            shovelDigger1.set("name", "Digging Plebe 1");
            shovelDigger1.set("shovel", shovel);
            
            DynamicEntity shovelDigger2 = helper.newDynamicEntity("DynamicShovelDigger");
            shovelDigger2.set("name", "Digging Plebe 2");
            shovelDigger2.set("shovel", shovel);
            
            List<DynamicEntity> operators = new ArrayList<DynamicEntity>();
            operators.add(shovelDigger1);
            operators.add(shovelDigger2);
            shovel.set("operators", operators);
            
            // Projects
            DynamicEntity shovelProject = helper.newDynamicEntity("DynamicShovelProject");
            shovelProject.set("description", "One lousy shovelling project");
            
            List<DynamicEntity> shovels = new ArrayList<DynamicEntity>();
            shovels.add(shovel);
            shovelProject.set("shovels", shovels);
            
            List<DynamicEntity> projects = new ArrayList<DynamicEntity>();
            projects.add(shovelProject);
            shovel.set("projects", projects);
            
            em.persist(shovel);
            
            // Grab id's for ease of lookup.
            Object shovelId = shovel.get("id");
            Object shovelOwnerId = shovelOwner.get("id");
            Object shovelDigger1Id = shovelDigger1.get("id");
            Object shovelDigger2Id = shovelDigger2.get("id");
            Object shovelProjectId = shovelProject.get("id");
            
            commitTransaction(em);
            
            clearDynamicPUCache();
            em.clear();
            
            DynamicEntity refreshedShovel = (DynamicEntity) em.find(helper.getType("DynamicShovel").getJavaClass(), shovelId);
            assertTrue("Shovel didn't match after write/read", getDynamicPUServerSession().compareObjects(shovel, refreshedShovel));
            
            // Do an update
            beginTransaction(em);
            
            em.merge(refreshedShovel);
            refreshedShovel.set("cost", Double.valueOf(7.99));
            
            commitTransaction(em);
            
            clearDynamicPUCache();
            em.clear();
            
            DynamicEntity refreshedUpdatedShovel = (DynamicEntity) em.find(helper.getType("DynamicShovel").getJavaClass(), shovelId);
            assertTrue("Shovel didn't match after update", getDynamicPUServerSession().compareObjects(refreshedShovel, refreshedUpdatedShovel));
            
            // Now delete it            
            beginTransaction(em);
            em.merge(refreshedUpdatedShovel);
            em.remove(refreshedUpdatedShovel);
            commitTransaction(em);
            
            // Check what's left
            assertNull("Shovel wasn't removed", em.find(helper.getType("DynamicShovel").getJavaClass(), shovelId));
            assertNull("Shovel owner wasn't removed", em.find(helper.getType("DynamicShovelOwner").getJavaClass(), shovelOwnerId));
            assertNull("Shovel digger 1 wasn't removed", em.find(helper.getType("DynamicShovelDigger").getJavaClass(), shovelDigger1Id));
            assertNull("Shovel digger 2 wasn't removed", em.find(helper.getType("DynamicShovelDigger").getJavaClass(), shovelDigger2Id));
            assertNotNull("Shovel project was removed",  em.find(helper.getType("DynamicShovelProject").getJavaClass(), shovelProjectId));
            
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
     * Test a dynamic persistence unit using no persistence.xml.
     */
    public void testDynamicWithNoPersistenceXML() {
        // This is a Java SE feature only.
        if (! isOnServer()) {
            List<ClassDescriptor> descriptors = new ArrayList<ClassDescriptor>();
            RelationalDescriptor descriptor = new RelationalDescriptor();
            descriptor.setJavaClassName("org.eclipse.persistence.testing.models.jpa.xml.advanced.dynamic.MyDynamicEntity");
            descriptor.setAlias("MyDynamicEntity");
            descriptor.setTableName("JPA_DYNAMIC_ENTITY");
            descriptor.addPrimaryKeyFieldName("ID");
            descriptor.setSequenceNumberFieldName("ID");
            descriptor.setSequenceNumberName("DYNAMIC_SEQ");
            descriptor.addDirectMapping("id", "ID");
            descriptor.addDirectMapping("firstName", "F_NAME");
            descriptor.addDirectMapping("lastName", "L_NAME");
            descriptors.add(descriptor);
        
            EntityManager em = createDynamicEntityManager("dynamic-test", descriptors);
            new DynamicTableCreator().replaceTables(((JpaEntityManager) em).getServerSession());
        
            try {
                beginTransaction(em);
                
                em.persist(new MyDynamicEntity("Doug", "Clarke"));
                em.persist(new MyDynamicEntity("Peter", "Krogh"));
                
                commitTransaction(em);
                
                clearCache("dynamic-test");
                em.clear();
                
                List<MyDynamicEntity> results = em.createQuery("SELECT d FROM MyDynamicEntity d").getResultList();
                assertFalse("No dynamic entities were returned from the query", results.isEmpty());
            } catch (RuntimeException e) {
                if (isTransactionActive(em)){
                    rollbackTransaction(em);
                }
    
                throw e;
            } finally {
                closeEntityManager(em);
            }
        }
    }
    
    /**
     * Verifies that existence-checking metadata is correctly processed.
     */
    public void testExistenceCheckingSetting() {
        assertTrue("Employee existence checking was incorrect", getDescriptor("DynamicEmployee").getQueryManager().getDoesExistQuery().getExistencePolicy() == DoesExistQuery.CheckDatabase);
        assertTrue("Project existence checking was incorrect", getDescriptor("DynamicProject").getQueryManager().getDoesExistQuery().getExistencePolicy() == DoesExistQuery.CheckCache);
        assertTrue("SmallProject existence checking was incorrect", getDescriptor("DynamicSmallProject").getQueryManager().getDoesExistQuery().getExistencePolicy() == DoesExistQuery.AssumeExistence);
        assertTrue("LargeProject existence checking was incorrect", getDescriptor("DynamicLargeProject").getQueryManager().getDoesExistQuery().getExistencePolicy() == DoesExistQuery.AssumeNonExistence);
    }
    
    /**
     * Verifies that the change tracking metadata is correctly processed.
     * Employee has an AUTO setting, but is virtual so it should have an
     * object change policy.
     */
    public void testEmployeeChangeTrackingPolicy() {
        assertTrue("Employee descriptor has incorrect object change policy", getDescriptor("DynamicEmployee").getObjectChangePolicy().isAttributeChangeTrackingPolicy());
    }
    
    /**
     * Query test
     */
    public void testNamedNativeQueryOnAddress() {
        EntityManager em = createDynamicPUEntityManager();
        List<DynamicEntity> addresses = em.createNamedQuery("findAllDynamicAddressesByPostalCode", DynamicEntity.class).setParameter("postalCode", "K2J 6T3").getResultList();
        assertFalse("No addresses were returned, expecting 1", addresses.isEmpty());
        assertTrue("Incorrect number of addresses returned, expected 1, got: " + addresses.size(), addresses.size() == 1);
        closeEntityManager(em);
    }

    /**
     * Query test
     */
    public void testNamedQueryOnEmployee() {
        EntityManager em = createDynamicPUEntityManager();
        DynamicEntity employee = em.createNamedQuery("findAllDynamicEmployeesByFirstName", DynamicEntity.class).setParameter("firstName", "Boy").getSingleResult();
        assertTrue("Error executing named query 'findAllDynamicEmployeesByFirstName'", employee != null);
        closeEntityManager(em);
    }
    
    /**
     * Tests a named-stored-procedure-query setting
     */
    public void testNamedStoredProcedureQuery() {
        if (supportsStoredProcedures("extended-dynamic-advanced")) {
            EntityManager em = createDynamicPUEntityManager();
            JPADynamicHelper helper = new JPADynamicHelper(em);
            
            try {
                beginTransaction(em);
                
                DynamicEntity address1 = helper.newDynamicEntity("DynamicAddress");
                address1.set("city", "Ottawa");
                address1.set("country", "Canada");
                address1.set("postalCode", "K1G 6P3");
                address1.set("province", "ON");
                address1.set("street", "123 Street");
                em.persist(address1);
                commitTransaction(em);
            
                clearDynamicPUCache();
                em.clear();

                Query aQuery = em.createNamedQuery("SProcDynamicAddress").setParameter("ADDRESS_ID", address1.get("id"));
                DynamicEntity address2 = (DynamicEntity) aQuery.getSingleResult();
                assertNotNull("Address returned from stored procedure is null", address2);
                assertFalse("Address returned is the same cached instance that was persisted - the cache must be disabled for this test", address1 == address2); 
                assertTrue("Address not found using stored procedure", address1.get("id").equals(address2.get("id")));
                assertTrue("Address.street data returned doesn't match persisted address.street", address1.get("street").equals(address2.get("street")));
            } catch (RuntimeException e) {
                if (isTransactionActive(em)){
                    rollbackTransaction(em);
                }
            
                // Re-throw exception to ensure stacktrace appears in test result.
                throw e;
            } finally {
                closeEntityManager(em);
            }
        }
    }
    
    /**
     * Tests a named-stored-procedure-query setting
     */
    public void testNamedStoredProcedureQueryInOut() {
        if (supportsStoredProcedures("extended-dynamic-advanced")) {
            EntityManager em = createDynamicPUEntityManager();
            JPADynamicHelper helper = new JPADynamicHelper(em);
        
            try {
                beginTransaction(em);
            
                DynamicEntity address1 = helper.newDynamicEntity("DynamicAddress");
        
                address1.set("city", "Ottawa");
                address1.set("postalCode", "K1G6P3");
                address1.set("province", "ON");
                address1.set("street", "123 Street");
                address1.set("country", "Canada");

                em.persist(address1);
                commitTransaction(em);
                em.clear();

                Query aQuery = em.createNamedQuery("SProcDynamicInOut").setParameter("ADDRESS_ID", address1.get("id"));
                DynamicEntity address2 = (DynamicEntity) aQuery.getSingleResult();
            
                assertNotNull("Address returned from stored procedure is null", address2);
                assertFalse("Address returned is the same cached instance that was persisted - the cache must be disabled for this test", address1 == address2); // new 
                assertTrue("Address not found using stored procedure", address1.get("id").equals(address2.get("id")));
                assertTrue("Address.street data returned doesn't match persisted address.street", address1.get("street").equals(address2.get("street")));
            } catch (RuntimeException e) {
                if (isTransactionActive(em)){
                    rollbackTransaction(em);
                }
            
                // Re-throw exception to ensure stacktrace appears in test result.
                throw e;
            } finally {
                closeEntityManager(em);
            }
        }
    }
    
    /**
     * Verifies that the change tracking metadata is correctly processed.
     */
    public void testProjectChangeTrackingPolicy() {
        assertTrue("Project descriptor has incorrect object change policy", getDescriptor("DynamicProject").getObjectChangePolicy().isObjectChangeTrackingPolicy());
    }
    
    /**
     * Verifies that the read-only metadata is correctly processed.
     */
    public void testReadOnlyClassSetting() {
        assertTrue("ReadOnlyClass descriptor is not set to read only.", getDescriptor("DynamicReadOnlyClass").shouldBeReadOnly());
    }
    
    /**
     * Test
     */
    public void testRefreshNotManagedEmployee() {
        EntityManager em = createDynamicPUEntityManager();
        JPADynamicHelper helper = new JPADynamicHelper(em);
        
        try {
            beginTransaction(em);
            DynamicEntity employee = helper.newDynamicEntity("DynamicEmployee");
            employee.set("firstName", "NotManaged");
            em.refresh(employee);
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
        
        DynamicEntity emp;
        EntityManager em = createDynamicPUEntityManager();
        JPADynamicHelper helper = new JPADynamicHelper(em);
        
        List<DynamicEntity> result = em.createQuery("SELECT OBJECT(e) FROM DynamicEmployee e WHERE e.firstName = '"+firstName+"'").getResultList();
        
        if (! result.isEmpty()) {
            emp = result.get(0);
        } else {
            emp = helper.newDynamicEntity("DynamicEmployee");
            emp.set("firstName", firstName);

            try {
                // persist the Employee
                beginTransaction(em);
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
        
        try {
            beginTransaction(em);
            emp = (DynamicEntity) em.find(helper.getType("DynamicEmployee").getJavaClass(), emp.get("id"));
                
            if (getDynamicPUServerSession().getPlatform().isSymfoware()) {
                // Symfoware does not support deleteall with multiple table
                em.createNativeQuery("DELETE FROM DYNAMIC_EMPLOYEE WHERE F_NAME = '"+firstName+"'").executeUpdate();
            } else {
                // delete the Employee from the db
                em.createQuery("DELETE FROM DynamicEmployee e WHERE e.firstName = '"+firstName+"'").executeUpdate();
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
    
    /**
     * The setup is done as a test, both to record its failure, and to allow 
     * execution in the server.
     */
    public void testSetup() {
        ServerSession session = getDynamicPUServerSession();
        new AdvancedDynamicTableCreator().replaceTables(session);
        
        if (TestCase.supportsStoredProcedures(session)) {
            SchemaManager schema = new SchemaManager((DatabaseSession) session);
            schema.replaceObject(buildOracleStoredProcedureReadFromAddress((DatabaseSession) session));
            schema.replaceObject(buildOracleStoredProcedureReadInOut((DatabaseSession) session));
        }
    }
    
    /**
     * Test update dynamic employee
     */
    public void testUpdateEmployee() {
        EntityManager em = createDynamicPUEntityManager();
        beginTransaction(em);
        Integer version = 0;
        
        try {
            DynamicEntity employee = em.createNamedQuery("findDynamicEmployeeById", DynamicEntity.class).setParameter("id", employeeId).getSingleResult();
            assertNotNull("The employee was not found", employee);
            
            version = employee.get("version");
            employee.set("salary", 50000);
            em.merge(employee);
            commitTransaction(em);
            
            // Clear cache and clear the entity manager
            clearDynamicPUCache();    
            em.clear();
            
            List<DynamicEntity> emps = em.createNamedQuery("findDynamicEmployeeById", DynamicEntity.class).setParameter("id", employeeId).getResultList();
            DynamicEntity emp = (DynamicEntity) emps.get(0);
            assertNotNull("The employee was not found", emp);

            assertTrue("Error updating Employee", emp.get("salary").equals(50000));
            assertTrue("Version field not updated", emp.get("version").equals(version + 1));            
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
     * Test create and read of dynamic entity using an embedded id.
     */
    public void testDynamicEmbeddedId() {
        EntityManager em = createDynamicPUEntityManager();
        JPADynamicHelper helper = new JPADynamicHelper(em);
        
        try {
            beginTransaction(em);
            
            DynamicEntity runner = helper.newDynamicEntity("DynamicRunner");
            runner.set("name", "Ryan Hill");
            
            DynamicEntity runnerPK = helper.newDynamicEntity("DynamicRunnerPK");
            runnerPK.set("bib", 1);
            runnerPK.set("worldRank", 13);
            
            runner.set("id", runnerPK);
            
            em.persist(runner);
            commitTransaction(em);
            
            clearDynamicPUCache();
            em.clear();
            
            // Re-read the runner and verify the data.
            DynamicEntity r = (DynamicEntity) em.find(helper.getType("DynamicRunner").getJavaClass(), runnerPK);
            assertTrue("Runner didn't match after create", getDynamicPUServerSession().compareObjects(r, runner));
            assertNotNull("The runner was not found", r);
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
     * Test create and read of dynamic entity using an embedded id.
     */
    public void testDynamicCompositeId() {
        EntityManager em = createDynamicPUEntityManager();
        JPADynamicHelper helper = new JPADynamicHelper(em);
        
        try {
            beginTransaction(em);
            
            DynamicEntity walker = helper.newDynamicEntity("DynamicWalker");
            walker.set("name", "Sam");
            walker.set("style", "Speed");
            
            em.persist(walker);
            commitTransaction(em);
            
            clearDynamicPUCache();
            em.clear();
            
            // Re-read the walker and see if an exception occurs.
            DynamicWalkerPK pk = new DynamicWalkerPK();
            pk.setId( (Integer) walker.get("id"));
            pk.setStyle((String) walker.get("style"));
            DynamicEntity w = (DynamicEntity) em.find(helper.getType("DynamicWalker").getJavaClass(), pk);
            //assertTrue("Runner didn't match after create", getDynamicPUServerSession().compareObjects(r, runner));
            assertNotNull("The walker was not found", w);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            
            fail("Error occurred reading the walker with composite id" + e);
            //throw e;
        } finally {
            closeEntityManager(em);
        }
    }
}
