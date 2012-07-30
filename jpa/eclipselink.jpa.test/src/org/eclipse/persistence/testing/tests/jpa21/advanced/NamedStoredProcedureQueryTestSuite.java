/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     02/08/2012-2.4 Guy Pelletier 
 *       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
 *     06/20/2012-2.5 Guy Pelletier 
 *       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls       
 *     07/13/2012-2.5 Guy Pelletier 
 *       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa21.advanced;

import static javax.persistence.ParameterMode.INOUT;
import static javax.persistence.ParameterMode.OUT;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.Query;
import javax.persistence.StoredProcedureParameter;
import javax.persistence.StoredProcedureQuery;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.queries.ResultSetMappingQuery;
import org.eclipse.persistence.queries.SQLCall;

import org.eclipse.persistence.testing.models.jpa21.advanced.Address;
import org.eclipse.persistence.testing.models.jpa21.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa21.advanced.EmployeeDetails;
import org.eclipse.persistence.testing.models.jpa21.advanced.EmployeePopulator;
import org.eclipse.persistence.testing.models.jpa21.advanced.LargeProject;
import org.eclipse.persistence.testing.models.jpa21.advanced.Project;
import org.eclipse.persistence.testing.models.jpa21.advanced.SmallProject;
import org.eclipse.persistence.testing.models.jpa21.advanced.Employee;
import org.eclipse.persistence.testing.tests.jpa.advanced.AdvancedJPAJunitTest;

import org.eclipse.persistence.queries.ColumnResult;
import org.eclipse.persistence.queries.ConstructorResult;
import org.eclipse.persistence.queries.EntityResult;
import org.eclipse.persistence.queries.FieldResult;
import org.eclipse.persistence.queries.SQLResultSetMapping;
import org.eclipse.persistence.queries.StoredProcedureCall;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import junit.framework.TestSuite;
import junit.framework.Test;

public class NamedStoredProcedureQueryTestSuite extends JUnitTestCase {
    protected boolean m_reset = false;

    public NamedStoredProcedureQueryTestSuite() {}
    
    public NamedStoredProcedureQueryTestSuite(String name) {
        super(name);
    }
    
    public void setUp () {
        m_reset = true;
        super.setUp();
        clearCache();
    }
    
    public void tearDown () {
        if (m_reset) {
            m_reset = false;
        }
        
        super.tearDown();
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("NamedStoredProcedureQueryTestSuite");
        
        suite.addTest(new NamedStoredProcedureQueryTestSuite("testSetup"));
        
        // TODO: Number of tests currently only run on MySQL. That is check for 
        // getPlatform().isMySQL() otherwise they skip the test. Should look to 
        // add my platform tests.
        
        // These tests call stored procedures that return a result set. 
        suite.addTest(new NamedStoredProcedureQueryTestSuite("testQueryWithMultipleResultsFromCode"));
        suite.addTest(new NamedStoredProcedureQueryTestSuite("testQueryWithMultipleResultsFromAnnotations"));
        suite.addTest(new NamedStoredProcedureQueryTestSuite("testQueryUsingPositionalParameterAndSingleResultSet"));
        
        // These tests call stored procedures that write into OUT parameters.
        suite.addTest(new NamedStoredProcedureQueryTestSuite("testQueryWithResultClass"));
        suite.addTest(new NamedStoredProcedureQueryTestSuite("testQueryWithResultClassPositional"));
        suite.addTest(new NamedStoredProcedureQueryTestSuite("testQueryWithResultSetMapping"));
        suite.addTest(new NamedStoredProcedureQueryTestSuite("testQueryWithResultSetFieldMapping"));
        
        // These tests call stored procedures from EM API
        suite.addTest(new NamedStoredProcedureQueryTestSuite("testEMCreateStoredProcedureQuery"));
        suite.addTest(new NamedStoredProcedureQueryTestSuite("testEMCreateStoredProcedureQueryWithPositionalParameters"));
        suite.addTest(new NamedStoredProcedureQueryTestSuite("testEMCreateStoredProcedureQueryWithResultClass"));
        suite.addTest(new NamedStoredProcedureQueryTestSuite("testEMCreateStoredProcedureQueryWithSqlResultSetMapping"));
        suite.addTest(new NamedStoredProcedureQueryTestSuite("testEMCreateStoredProcedureUpdateQuery"));
        suite.addTest(new NamedStoredProcedureQueryTestSuite("testEMCreateStoredProcedureExecuteQuery"));
        
        return suite;
    }
    
    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new AdvancedTableCreator().replaceTables(JUnitTestCase.getServerSession());
        EmployeePopulator employeePopulator = new EmployeePopulator();
        employeePopulator.buildExamples();
        employeePopulator.persistExample(getServerSession());
        clearCache();
    }
    
    /**
     * Tests a StoredProcedureQuery using only a procedure name though EM API 
     */
    public void testEMCreateStoredProcedureQuery() {
        if (supportsStoredProcedures() && getPlatform().isMySQL()) {
            EntityManager em = createEntityManager();
            beginTransaction(em);
            
            try {
                Address address1 = new Address();
                address1.setCity("Edmonton");
                address1.setPostalCode("T5B 4M9");
                address1.setProvince("AB");
                address1.setStreet("7424 118 Avenue");
                address1.setCountry("Canada");
                em.persist(address1);
                commitTransaction(em);
                
                // Clear the cache
                em.clear();
                clearCache();
    
                StoredProcedureQuery query = em.createStoredProcedureQuery("Read_Address");
                query.registerStoredProcedureParameter("ADDRESS_ID", Integer.class, ParameterMode.INOUT);
                query.registerStoredProcedureParameter("STREET", String.class, ParameterMode.OUT);
                query.registerStoredProcedureParameter("CITY", String.class, ParameterMode.OUT);
                query.registerStoredProcedureParameter("COUNTRY", String.class, ParameterMode.OUT);
                query.registerStoredProcedureParameter("PROVINCE", String.class, ParameterMode.OUT);
                query.registerStoredProcedureParameter("P_CODE", String.class, ParameterMode.OUT);

                List addresses = query.setParameter("ADDRESS_ID", address1.getId()).getResultList();
                assertTrue("Incorrect number of addresses returned", addresses.size() == 1);
                Object[] addressContent = (Object[]) addresses.get(0);
                assertTrue("Incorrect data content size", addressContent.length == 6);
                assertTrue("Id content incorrect", addressContent[0].equals(address1.getId()));
                assertTrue("Steet content incorrect", addressContent[1].equals(address1.getStreet()));
                assertTrue("City content incorrect", addressContent[2].equals(address1.getCity()));
                assertTrue("Country content incorrect", addressContent[3].equals(address1.getCountry()));
                assertTrue("Province content incorrect", addressContent[4].equals(address1.getProvince()));
                assertTrue("Postal Code content incorrect", addressContent[5].equals(address1.getPostalCode()));
            } catch (RuntimeException e) {
                if (isTransactionActive(em)){
                    rollbackTransaction(em);
                }
                
                closeEntityManager(em);
                throw e;
            }
            
            closeEntityManager(em);
        }
    }
    
    /**
     * Tests a NamedStoredProcedureQuery using a result-class. 
     */
    public void testEMCreateStoredProcedureQueryWithPositionalParameters() {
        if (supportsStoredProcedures() && getPlatform().isMySQL()) {
            EntityManager em = createEntityManager();
            beginTransaction(em);
            
            try {
                Address address1 = new Address();
                address1.setCity("Ottawa");
                address1.setPostalCode("K1G 6P3");
                address1.setProvince("ON");
                address1.setStreet("123 Street");
                address1.setCountry("Canada");
                em.persist(address1);
                commitTransaction(em);
                
                // Clear the cache
                em.clear();
                clearCache();
    
                // Should be able to mix the order here I think.
                StoredProcedureQuery query = em.createStoredProcedureQuery("Read_Address", "address-field-result-map-positional");
                query.registerStoredProcedureParameter(1, Integer.class, ParameterMode.INOUT);
                query.registerStoredProcedureParameter(2, String.class, ParameterMode.OUT);
                query.registerStoredProcedureParameter(3, String.class, ParameterMode.OUT);
                query.registerStoredProcedureParameter(4, String.class, ParameterMode.OUT);
                query.registerStoredProcedureParameter(5, String.class, ParameterMode.OUT);
                query.registerStoredProcedureParameter(6, String.class, ParameterMode.OUT);
                
                Address address2 = (Address) em.createNamedStoredProcedureQuery("ReadAddressWithPositionalParameters").setParameter(1, address1.getId()).getSingleResult();
                assertNotNull("Address returned from stored procedure is null", address2); 
                assertTrue("Address didn't build correctly using stored procedure", (address1.getId() == address2.getId()));
                assertTrue("Address didn't build correctly using stored procedure", (address1.getStreet().equals(address2.getStreet())));
                assertTrue("Address didn't build correctly using stored procedure", (address1.getCountry().equals(address2.getCountry())));
                assertTrue("Address didn't build correctly using stored procedure", (address1.getProvince().equals(address2.getProvince())));
                assertTrue("Address didn't build correctly using stored procedure", (address1.getPostalCode().equals(address2.getPostalCode())));
            } catch (RuntimeException e) {
                if (isTransactionActive(em)){
                    rollbackTransaction(em);
                }
                
                closeEntityManager(em);
                throw e;
            }
            
            closeEntityManager(em);
        }
    }  
    
    /**
     * Tests a StoredProcedureQuery using a class though EM API 
     */
    public void testEMCreateStoredProcedureQueryWithResultClass() {
        if (supportsStoredProcedures() && getPlatform().isMySQL()) {
            EntityManager em = createEntityManager();
            beginTransaction(em);
            
            try {
                Address address1 = new Address();
                address1.setCity("Victoria");
                address1.setPostalCode("V9A 6A9");
                address1.setProvince("BC");
                address1.setStreet("785 Lampson Street");
                address1.setCountry("Canada");
                em.persist(address1);
                commitTransaction(em);
                
                // Clear the cache
                em.clear();
                clearCache();
    
                StoredProcedureQuery query = em.createStoredProcedureQuery("Read_Address", org.eclipse.persistence.testing.models.jpa21.advanced.Address.class);
                query.registerStoredProcedureParameter("ADDRESS_ID", Integer.class, ParameterMode.INOUT);
                query.registerStoredProcedureParameter("STREET", String.class, ParameterMode.OUT);
                query.registerStoredProcedureParameter("CITY", String.class, ParameterMode.OUT);
                query.registerStoredProcedureParameter("COUNTRY", String.class, ParameterMode.OUT);
                query.registerStoredProcedureParameter("PROVINCE", String.class, ParameterMode.OUT);
                query.registerStoredProcedureParameter("P_CODE", String.class, ParameterMode.OUT);
                
                Address address2 = (Address) query.setParameter("ADDRESS_ID", address1.getId()).getSingleResult();
                assertNotNull("Address returned from stored procedure is null", address2); 
                assertTrue("Address didn't build correctly using stored procedure", (address1.getId() == address2.getId()));
                assertTrue("Address didn't build correctly using stored procedure", (address1.getStreet().equals(address2.getStreet())));
                assertTrue("Address didn't build correctly using stored procedure", (address1.getCountry().equals(address2.getCountry())));
                assertTrue("Address didn't build correctly using stored procedure", (address1.getProvince().equals(address2.getProvince())));
                assertTrue("Address didn't build correctly using stored procedure", (address1.getPostalCode().equals(address2.getPostalCode())));
            } catch (RuntimeException e) {
                if (isTransactionActive(em)){
                    rollbackTransaction(em);
                }
                
                closeEntityManager(em);
                throw e;
            }
            
            closeEntityManager(em);
        }
    }
    
    /**
     * Tests a StoredProcedureQuery using a result-set mapping though EM API 
     */
    public void testEMCreateStoredProcedureQueryWithSqlResultSetMapping() {
        if (supportsStoredProcedures()) {
            EntityManager em = createEntityManager();
            beginTransaction(em);
            
            try {
                Address address = new Address();
                address.setCity("Winnipeg");
                address.setPostalCode("R3B 1B9");
                address.setProvince("MB");
                address.setStreet("510 Main Street");
                address.setCountry("Canada");
                em.persist(address);
                commitTransaction(em);
                
                // Clear the cache
                em.clear();
                clearCache();
                
                StoredProcedureQuery query = em.createStoredProcedureQuery("Read_Address_Mapped", "address-column-result-map");
                query.registerStoredProcedureParameter("address_id_v", String.class, ParameterMode.INOUT);
                query.registerStoredProcedureParameter("street_v", String.class, ParameterMode.OUT);
                query.registerStoredProcedureParameter("city_v", String.class, ParameterMode.OUT);
                query.registerStoredProcedureParameter("country_v", String.class, ParameterMode.OUT);
                query.registerStoredProcedureParameter("province_v", String.class, ParameterMode.OUT);
                query.registerStoredProcedureParameter("p_code_v", String.class, ParameterMode.OUT);
                
                Object[] values = (Object[]) query.setParameter("address_id_v", address.getId()).getSingleResult();
                assertTrue("Address data not found or returned using stored procedure", ((values != null) && (values.length == 6)));
                assertNotNull("No results returned from store procedure call", values[1]);
                assertTrue("Address not found using stored procedure", address.getStreet().equals(values[1]));
            } catch (RuntimeException e) {
                if (isTransactionActive(em)){
                    rollbackTransaction(em);
                }
                
                closeEntityManager(em);
                throw e;
            }
            
            closeEntityManager(em);
        }
    }
    
    /**
     * Tests a StoredProcedureQuery that does an update though EM API 
     */
    public void testEMCreateStoredProcedureUpdateQuery() {
        if (supportsStoredProcedures()) {
            EntityManager em = createEntityManager();
            
            try {
                String postalCodeTypo = "R3 1B9";
                String postalCodeCorrection = "R3B 1B9";
                
                beginTransaction(em);
                
                Address address1 = new Address();
                address1.setCity("Winnipeg");
                address1.setPostalCode(postalCodeTypo);
                address1.setProvince("MB");
                address1.setStreet("510 Main Street");
                address1.setCountry("Canada");
                em.persist(address1);
                
                Address address2 = new Address();
                address2.setCity("Winnipeg");
                address2.setPostalCode(postalCodeTypo);
                address2.setProvince("MB");
                address2.setStreet("512 Main Street");
                address2.setCountry("Canada");
                em.persist(address2);
                
                commitTransaction(em);
                
                // Clear the cache
                em.clear();
                clearCache();
                
                StoredProcedureQuery query = em.createStoredProcedureQuery("Update_Address_Postal_Code");
                query.registerStoredProcedureParameter("new_p_code_v", String.class, ParameterMode.IN);
                query.registerStoredProcedureParameter("old_p_code_v", String.class, ParameterMode.IN);
                
                beginTransaction(em);
                int results = query.setParameter("new_p_code_v", postalCodeCorrection).setParameter("old_p_code_v", postalCodeTypo).executeUpdate();
                commitTransaction(em);
                
                assertTrue("Update count incorrect.", results == 2);
                
                // Just because we don't trust anyone ... :-)
                Address a1 = em.find(Address.class, address1.getId());
                assertTrue("The postal code was not updated for address 1.", a1.getPostalCode().equals(postalCodeCorrection));
                Address a2 = em.find(Address.class, address2.getId());
                assertTrue("The postal code was not updated for address 2.", a2.getPostalCode().equals(postalCodeCorrection));
            } catch (RuntimeException e) {
                if (isTransactionActive(em)){
                    rollbackTransaction(em);
                }
                
                closeEntityManager(em);
                throw e;
            }
            
            closeEntityManager(em);
        }
    }
    
    /**
     * Tests a StoredProcedureQuery that does an update though EM API 
     */
    public void testEMCreateStoredProcedureExecuteQuery() {
        if (supportsStoredProcedures()) {
            EntityManager em = createEntityManager();
            
            try {
                // Create some data (with errors)
                String postalCodeTypo = "K2J 0L8";
                String postalCodeCorrection = "K2G 6W2";
                
                beginTransaction(em);
                
                Address address1 = new Address();
                address1.setCity("Winnipeg");
                address1.setPostalCode(postalCodeTypo);
                address1.setProvince("MB");
                address1.setStreet("510 Main Street");
                address1.setCountry("Canada");
                em.persist(address1);
                
                Address address2 = new Address();
                address2.setCity("Winnipeg");
                address2.setPostalCode(postalCodeTypo);
                address2.setProvince("MB");
                address2.setStreet("512 Main Street");
                address2.setCountry("Canada");
                em.persist(address2);
                
                Address address3 = new Address();
                address3.setCity("Winnipeg");
                address3.setPostalCode(postalCodeCorrection);
                address3.setProvince("MB");
                address3.setStreet("514 Main Street");
                address3.setCountry("Canada");
                em.persist(address3);
                
                commitTransaction(em);
                
                // Clear the cache
                em.clear();
                clearCache();
                
                // Build the named stored procedure query, execute and test.
                StoredProcedureQuery query = em.createStoredProcedureQuery("Result_Set_And_Update_Address", Address.class, Employee.class);
                query.registerStoredProcedureParameter("new_p_code_v", String.class, ParameterMode.IN);
                query.registerStoredProcedureParameter("old_p_code_v", String.class, ParameterMode.IN);
                query.registerStoredProcedureParameter("employee_count_v", Integer.class, ParameterMode.OUT);
                
                beginTransaction(em);
                
                boolean result = query.setParameter("new_p_code_v", postalCodeCorrection).setParameter("old_p_code_v", postalCodeTypo).execute();
                assertTrue("Result did not return true for a result set.", result);
                
                List<Address> addressResults = query.getResultList();
                
                // Should return true (employees to return)
                assertTrue("The query did not have more results (Employees).", query.hasMoreResults());
                List<Employee> employeeResults = query.getResultList();
                int numberOfEmployes = employeeResults.size();
                
                // Should return false (no more results)
                assertFalse("The query had more results.", query.hasMoreResults());
                
                // Get the update count.
                int updateCount = query.getUpdateCount();
                assertTrue("Update count incorrect: " + updateCount, updateCount == 2);
                
                // Check output parameters by name.
                // TODO: to investigate. This little bit is hacky. For some 
                // reason MySql returns a Long here. By position is ok, that is, 
                // it returns an Integer (as we registered)
                Long outputParamValueFromName;
                if (getPlatform().isMySQL()) {
                    outputParamValueFromName = (Long) query.getOutputParameterValue("employee_count_v");
                } else {
                    outputParamValueFromName = new Long((Integer) query.getOutputParameterValue("employee_count_v"));
                }
                assertNotNull("The output parameter was null.", outputParamValueFromName);
                assertTrue("Incorrect value returned, expected " + numberOfEmployes + ", got: " + outputParamValueFromName, outputParamValueFromName.equals(new Long(numberOfEmployes)));
                
                // Do some negative tests ...                
                try {
                    query.getOutputParameterValue(null);
                    fail("No IllegalArgumentException was caught with a null parameter name.");
                } catch (IllegalArgumentException e) {
                    // Expected, swallow.
                }
                
                try {
                    query.getOutputParameterValue("emp_count");
                    fail("No IllegalArgumentException was caught with invalid parameter name.");
                } catch (IllegalArgumentException e) {
                    // Expected, swallow.
                }
                
                try {
                    query.getOutputParameterValue("new_p_code_v");
                    fail("No IllegalArgumentException was caught with IN parameter name.");
                } catch (IllegalArgumentException e) {
                    // Expected, swallow.
                }
                
                // Check output parameters by position.
                Integer outputParamValueFromPosition = (Integer) query.getOutputParameterValue(3);
                assertNotNull("The output parameter was null.", outputParamValueFromPosition);
                assertTrue("Incorrect value returned, expected " + numberOfEmployes + ", got: " + outputParamValueFromPosition, outputParamValueFromPosition.equals(numberOfEmployes));

                // Do some negative tests ...
                try {
                    query.getOutputParameterValue(8);
                    fail("No IllegalArgumentException was caught with position out of bounds.");
                } catch (IllegalArgumentException e) {
                    // Expected, swallow.
                }
                
                try {
                    query.getOutputParameterValue(1);
                    fail("No IllegalArgumentException was caught with an IN parameter position.");
                } catch (IllegalArgumentException e) {
                    // Expected, swallow.
                }
                
                // This will commit all the changes and update the cache. Will
                // also close all open calls from execute methods.
                commitTransaction(em);
                
                // Just because we don't trust anyone ... :-)
                Address a1 = em.find(Address.class, address1.getId());
                assertTrue("The postal code was not updated for address 1.", a1.getPostalCode().equals(postalCodeCorrection));
                Address a2 = em.find(Address.class, address2.getId());
                assertTrue("The postal code was not updated for address 2.", a2.getPostalCode().equals(postalCodeCorrection));
                Address a3 = em.find(Address.class, address3.getId());
                assertTrue("The postal code was not updated for address 3.", a3.getPostalCode().equals(postalCodeCorrection));
            } catch (RuntimeException e) {
                if (isTransactionActive(em)){
                    rollbackTransaction(em);
                }
                
                closeEntityManager(em);
                throw e;
            }
            
            closeEntityManager(em);
        }
    }

    /**
     * Tests a NamedStoredProcedureQuery using a positional parameter returning 
     * a single result set. 
     */
    public void testQueryUsingPositionalParameterAndSingleResultSet() {
        if (supportsStoredProcedures() && getPlatform().isMySQL()) {
            EntityManager em = createEntityManager();
            beginTransaction(em);
            
            try {
                Address address1 = new Address();
                address1.setCity("Ottawa");
                address1.setPostalCode("K1G 6P3");
                address1.setProvince("ON");
                address1.setStreet("123 Street");
                address1.setCountry("Canada");
                em.persist(address1);
                commitTransaction(em);
                
                // Clear the cache
                em.clear();
                clearCache();
    
                Address address2 = (Address) em.createNamedStoredProcedureQuery("ReadAddressUsingPositionalParameterAndSingleResultSet").setParameter("1", address1.getId()).getSingleResult();
                assertNotNull("Address returned from stored procedure is null", address2); 
                assertTrue("Address didn't build correctly using stored procedure", (address1.getId() == address2.getId()));
                assertTrue("Address didn't build correctly using stored procedure", (address1.getStreet().equals(address2.getStreet())));
                assertTrue("Address didn't build correctly using stored procedure", (address1.getCountry().equals(address2.getCountry())));
                assertTrue("Address didn't build correctly using stored procedure", (address1.getProvince().equals(address2.getProvince())));
            } catch (RuntimeException e) {
                if (isTransactionActive(em)){
                    rollbackTransaction(em);
                }
                
                closeEntityManager(em);
                throw e;
            }
            
            closeEntityManager(em);
        }
    }
    
    /**
     * Test multiple result sets by setting the SQL results set mapping from code.
     */
    public void testQueryWithMultipleResultsFromCode() throws Exception {
        if (supportsStoredProcedures() && getPlatform().isMySQL()) {
            // SQL result set mapping for employee.
            SQLResultSetMapping employeeResultSetMapping = new SQLResultSetMapping("EmployeeResultSetMapping");
            employeeResultSetMapping.addResult(new EntityResult(Employee.class));
             
            // SQL result set mapping for address.
            SQLResultSetMapping addressResultSetMapping = new SQLResultSetMapping("AddressResultSetMapping");
            addressResultSetMapping.addResult(new EntityResult(Address.class));
            
            // SQL result set mapping for project (using inheritance and more complex result)
            SQLResultSetMapping projectResultSetMapping = new SQLResultSetMapping("ProjectResultSetMapping");
            EntityResult projectEntityResult = new EntityResult(Project.class);
            projectResultSetMapping.addResult(projectEntityResult);
            projectEntityResult = new EntityResult(SmallProject.class);
            projectEntityResult.addFieldResult(new FieldResult("id", "SMALL_ID"));
            projectEntityResult.addFieldResult(new FieldResult("name", "SMALL_NAME"));
            projectEntityResult.addFieldResult(new FieldResult("description", "SMALL_DESCRIPTION"));
            projectEntityResult.addFieldResult(new FieldResult("teamLeader", "SMALL_TEAMLEAD"));
            projectEntityResult.addFieldResult(new FieldResult("version", "SMALL_VERSION"));
            projectEntityResult.setDiscriminatorColumn("SMALL_DESCRIM");
            projectResultSetMapping.addResult(projectEntityResult);
            projectResultSetMapping.addResult(new ColumnResult("BUDGET_SUM"));
             
            // SQL result set mapping for employee using constructor results.
            SQLResultSetMapping employeeConstrustorResultSetMapping = new SQLResultSetMapping("EmployeeConstructorResultSetMapping");
            ConstructorResult constructorResult = new ConstructorResult(EmployeeDetails.class);
            ColumnResult columnResult = new ColumnResult("EMP_ID");
            columnResult.getColumn().setType(Integer.class);
            constructorResult.addColumnResult(columnResult);
            columnResult = new ColumnResult("F_NAME");
            columnResult.getColumn().setType(String.class);
            constructorResult.addColumnResult(columnResult);
            columnResult = new ColumnResult("L_NAME");
            columnResult.getColumn().setType(String.class);
            constructorResult.addColumnResult(columnResult);
            columnResult = new ColumnResult("R_COUNT");
            columnResult.getColumn().setType(Integer.class);
            constructorResult.addColumnResult(columnResult);
            employeeConstrustorResultSetMapping.addResult(constructorResult);
            
            StoredProcedureCall call = new StoredProcedureCall();
            call.setProcedureName("Read_Multiple_Result_Sets");
            call.setHasMultipleResultSets(true);
            call.setReturnMultipleResultSetCollections(true);
             
            ResultSetMappingQuery query = new ResultSetMappingQuery(call);
            query.addSQLResultSetMapping(employeeResultSetMapping);
            query.addSQLResultSetMapping(addressResultSetMapping);
            query.addSQLResultSetMapping(projectResultSetMapping);
            query.addSQLResultSetMapping(employeeConstrustorResultSetMapping);
             
            List allResults = (List)getServerSession().executeQuery(query);
            assertNotNull("No results returned", allResults);
            assertTrue("Incorrect number of results returned", allResults.size() == 4);
            
            // Verify first result set mapping --> Employee
            List results0 = (List) allResults.get(0);
            assertNotNull("No Employee results returned", results0);
            assertTrue("Empty Employee results returned", results0.size() > 0);
    
            // Verify second result set mapping --> Address
            List results1 = (List) allResults.get(1);
            assertNotNull("No Address results returned", results1);
            assertTrue("Empty Address results returned", results1.size() > 0);
    
            // Verify third result set mapping --> Project
            List results2 = (List) allResults.get(2);
            assertNotNull("No Project results returned", results2);
            assertTrue("Empty Project results returned", results2.size() > 0);
    
            for (Object result2 : results2) {
                Object[] result2Element = (Object[]) result2;
                assertTrue("Failed to Return 3 items", (result2Element.length == 3));
                // Using Number as Different db/drivers  can return different types
                // e.g. Oracle with ijdbc14.jar returns BigDecimal where as Derby 
                // with derbyclient.jar returns Double. NOTE: the order of checking 
                // here is valid and as defined by the spec.
                assertTrue("Failed to return LargeProject", (result2Element[0] instanceof LargeProject));
                assertTrue("Failed To Return SmallProject", (result2Element[1] instanceof SmallProject));
                assertTrue("Failed to return column",(result2Element[2] instanceof Number));
                assertFalse("Returned same data in both result elements",((SmallProject) result2Element[1]).getName().equals(((LargeProject) result2Element[0]).getName()));
            }
            
            // Verify fourth result set mapping --> Employee Constructor Result
            List results3 = (List) allResults.get(3);
            assertNotNull("No Employee constructor results returned", results3);
            assertTrue("Empty Employee constructor results returned", results3.size() > 0);
        }
    }
    
    /**
     * Test multiple result sets by setting the SQL results set mapping from annotation.
     */
    public void testQueryWithMultipleResultsFromAnnotations() throws Exception {
        if (supportsStoredProcedures() && getPlatform().isMySQL()) {
            StoredProcedureQuery multipleResultSetQuery = createEntityManager().createNamedStoredProcedureQuery("ReadUsingMultipleResultSetMappings");
            
            // Verify first result set mapping --> Employee
            List results = multipleResultSetQuery.getResultList();
            assertNotNull("No Employee results returned", results);
            assertTrue("Empty Employee results returned", results.size() > 0);
    
            // Verify second result set mapping --> Address
            assertTrue("Address results not available", multipleResultSetQuery.hasMoreResults());
            results = multipleResultSetQuery.getResultList();
            assertNotNull("No Address results returned", results);
            assertTrue("Empty Address results returned", results.size() > 0);
            
            // Verify third result set mapping --> Project
            assertTrue("Projects results not available", multipleResultSetQuery.hasMoreResults());
            results = multipleResultSetQuery.getResultList();
            assertNotNull("No Project results returned", results);
            assertTrue("Empty Project results returned", results.size() > 0);
    
            for (Object result : results) {
                Object[] resultElement = (Object[]) result;
                assertTrue("Failed to Return 3 items", (resultElement.length == 3));
                // Using Number as Different db/drivers  can return different types
                // e.g. Oracle with ijdbc14.jar returns BigDecimal where as Derby 
                // with derbyclient.jar returns Double. NOTE: the order of checking 
                // here is valid and as defined by the spec.
                assertTrue("Failed to return LargeProject", (resultElement[0] instanceof LargeProject) );
                assertTrue("Failed To Return SmallProject", (resultElement[1] instanceof SmallProject) );
                assertTrue("Failed to return column",(resultElement[2] instanceof Number) );
                assertFalse("Returned same data in both result elements",((SmallProject)resultElement[1]).getName().equals(((LargeProject)resultElement[0]).getName()));
            }
            
            // Verify fourth result set mapping --> Employee Constructor Result
            assertTrue("Employee constructor results not available", multipleResultSetQuery.hasMoreResults());
            results = multipleResultSetQuery.getResultList();
            assertNotNull("No Employee constructor results returned", results);
            assertTrue("Empty Employee constructor results returned", results.size() > 0);
            
            // Verify there as no more results available
            assertFalse("More results available", multipleResultSetQuery.hasMoreResults());
        }
    }
    
    /**
     * Tests a NamedStoredProcedureQuery using a result-class. 
     */
    public void testQueryWithResultClass() {
        if (supportsStoredProcedures() && getPlatform().isMySQL()) {
            EntityManager em = createEntityManager();
            beginTransaction(em);
            
            try {
                Address address1 = new Address();
                address1.setCity("Ottawa");
                address1.setPostalCode("K1G 6P3");
                address1.setProvince("ON");
                address1.setStreet("123 Street");
                address1.setCountry("Canada");
                em.persist(address1);
                commitTransaction(em);
                
                // Clear the cache
                em.clear();
                clearCache();
    
                Address address2 = (Address) em.createNamedStoredProcedureQuery("ReadAddressWithResultClass").setParameter("ADDRESS_ID", address1.getId()).getSingleResult();
                assertNotNull("Address returned from stored procedure is null", address2); 
                assertTrue("Address didn't build correctly using stored procedure", (address1.getId() == address2.getId()));
                assertTrue("Address didn't build correctly using stored procedure", (address1.getStreet().equals(address2.getStreet())));
                assertTrue("Address didn't build correctly using stored procedure", (address1.getCountry().equals(address2.getCountry())));
                assertTrue("Address didn't build correctly using stored procedure", (address1.getProvince().equals(address2.getProvince())));
                assertTrue("Address didn't build correctly using stored procedure", (address1.getPostalCode().equals(address2.getPostalCode())));
            } catch (RuntimeException e) {
                if (isTransactionActive(em)){
                    rollbackTransaction(em);
                }
                
                closeEntityManager(em);
                throw e;
            }
            
            closeEntityManager(em);
        }
    }
    
    /**
     * Tests a NamedStoredProcedureQuery using a result-class. 
     */
    public void testQueryWithResultClassPositional() {
        if (supportsStoredProcedures() && getPlatform().isMySQL()) {
            EntityManager em = createEntityManager();
            beginTransaction(em);
            
            try {
                Address address1 = new Address();
                address1.setCity("Ottawa");
                address1.setPostalCode("K1G 6P3");
                address1.setProvince("ON");
                address1.setStreet("123 Street");
                address1.setCountry("Canada");
                em.persist(address1);
                commitTransaction(em);
                
                // Clear the cache
                em.clear();
                clearCache();
    
                Address address2 = (Address) em.createNamedStoredProcedureQuery("ReadAddressWithPositionalParameters").setParameter(1, address1.getId()).getSingleResult();
                assertNotNull("Address returned from stored procedure is null", address2); 
                assertTrue("Address didn't build correctly using stored procedure", (address1.getId() == address2.getId()));
                assertTrue("Address didn't build correctly using stored procedure", (address1.getStreet().equals(address2.getStreet())));
                assertTrue("Address didn't build correctly using stored procedure", (address1.getCountry().equals(address2.getCountry())));
                assertTrue("Address didn't build correctly using stored procedure", (address1.getProvince().equals(address2.getProvince())));
                assertTrue("Address didn't build correctly using stored procedure", (address1.getPostalCode().equals(address2.getPostalCode())));
            } catch (RuntimeException e) {
                if (isTransactionActive(em)){
                    rollbackTransaction(em);
                }
                
                closeEntityManager(em);
                throw e;
            }
            
            closeEntityManager(em);
        }
    }    
    
    /**
     * Tests a NamedStoredProcedureQuery using a result-set mapping. 
     */
    public void testQueryWithResultSetMapping() {
        if (supportsStoredProcedures()) {
            EntityManager em = createEntityManager();
            beginTransaction(em);
            
            try {
                Address address1 = new Address();
                address1.setCity("Ottawa");
                address1.setPostalCode("K1G 6P3");
                address1.setProvince("ON");
                address1.setStreet("123 Street");
                address1.setCountry("Canada");
    
                em.persist(address1);
                commitTransaction(em);
                
                // Clear the cache
                em.clear();
                clearCache();
                
                Address address2 = (Address) em.createNamedStoredProcedureQuery("ReadAddressWithResultSetMapping").setParameter("address_id_v", address1.getId()).getSingleResult();
                assertNotNull("Address returned from stored procedure is null", address2); 
                assertTrue("Address not found using stored procedure", (address1.getId() == address2.getId()));
            } catch (RuntimeException e) {
                if (isTransactionActive(em)){
                    rollbackTransaction(em);
                }
                
                closeEntityManager(em);
                throw e;
            }
            
            closeEntityManager(em);
        }
    }
    
    /**
     * Tests a NamedStoredProcedureQuery annotation using a result-set mapping. 
     */
    public void testQueryWithResultSetFieldMapping() {
        if (supportsStoredProcedures()) {
            EntityManager em = createEntityManager();
            beginTransaction(em);
            
            try {
                Address address = new Address();
                address.setCity("Ottawa");
                address.setPostalCode("K1G 6P3");
                address.setProvince("ON");
                address.setStreet("123 Street");
                address.setCountry("Canada");
                em.persist(address);
                commitTransaction(em);
                
                // Clear the cache
                em.clear();
                clearCache();
                
                Object[] values = (Object[]) em.createNamedStoredProcedureQuery("ReadAddressWithResultSetFieldMapping").setParameter("address_id_v", address.getId()).getSingleResult();
                assertTrue("Address data not found or returned using stored procedure", ((values != null) && (values.length == 6)));
                assertNotNull("No results returned from store procedure call", values[1]);
                assertTrue("Address not found using stored procedure", address.getStreet().equals(values[1]));
            } catch (RuntimeException e) {
                if (isTransactionActive(em)){
                    rollbackTransaction(em);
                }
                
                closeEntityManager(em);
                throw e;
            }
            
            closeEntityManager(em);
        }
    }
}
