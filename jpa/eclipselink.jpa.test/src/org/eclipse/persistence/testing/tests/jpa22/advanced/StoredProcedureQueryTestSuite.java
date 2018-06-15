/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     01/23/2013-2.5 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
package org.eclipse.persistence.testing.tests.jpa22.advanced;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.TransactionRequiredException;

import junit.framework.TestSuite;
import junit.framework.Test;

import org.eclipse.persistence.queries.ResultSetMappingQuery;
import org.eclipse.persistence.queries.ColumnResult;
import org.eclipse.persistence.queries.ConstructorResult;
import org.eclipse.persistence.queries.EntityResult;
import org.eclipse.persistence.queries.FieldResult;
import org.eclipse.persistence.queries.SQLResultSetMapping;
import org.eclipse.persistence.queries.StoredProcedureCall;

import org.eclipse.persistence.testing.models.jpa22.advanced.Address;
import org.eclipse.persistence.testing.models.jpa22.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa22.advanced.EmployeeDetails;
import org.eclipse.persistence.testing.models.jpa22.advanced.EmployeePopulator;
import org.eclipse.persistence.testing.models.jpa22.advanced.LargeProject;
import org.eclipse.persistence.testing.models.jpa22.advanced.Project;
import org.eclipse.persistence.testing.models.jpa22.advanced.SmallProject;
import org.eclipse.persistence.testing.models.jpa22.advanced.Employee;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;

public class StoredProcedureQueryTestSuite extends JUnitTestCase {
    public StoredProcedureQueryTestSuite() {}

    public StoredProcedureQueryTestSuite(String name) {
        super(name);
        setPuName("MulitPU-1");
    }

    @Override
    public String getPersistenceUnitName() {
       return "MulitPU-1";
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("StoredProcedureQueryTestSuite");

        suite.addTest(new StoredProcedureQueryTestSuite("testSetup"));

        // Add the EM level stored procedure query tests.
        suite.addTest(new StoredProcedureQueryTestSuite("testQueryExecute1"));
        suite.addTest(new StoredProcedureQueryTestSuite("testQueryExecute2"));
        suite.addTest(new StoredProcedureQueryTestSuite("testQueryExecuteUpdate"));
        suite.addTest(new StoredProcedureQueryTestSuite("testQueryGetResultList"));
        suite.addTest(new StoredProcedureQueryTestSuite("testQueryWithMultipleResultsFromCode"));
        suite.addTest(new StoredProcedureQueryTestSuite("testQueryWithNamedFieldResult"));
        suite.addTest(new StoredProcedureQueryTestSuite("testQueryWithNumberedFieldResult"));
        suite.addTest(new StoredProcedureQueryTestSuite("testQueryWithResultClass"));
        suite.addTest(new StoredProcedureQueryTestSuite("testQueryWithOutParam"));
        suite.addTest(new StoredProcedureQueryTestSuite("testStoredProcedureParameterAPI"));
        suite.addTest(new StoredProcedureQueryTestSuite("testStoredProcedureQuerySysCursor_Named"));
        suite.addTest(new StoredProcedureQueryTestSuite("testStoredProcedureQuerySysCursor_Positional"));
        suite.addTest(new StoredProcedureQueryTestSuite("testStoredProcedureQuerySysCursor2"));
        suite.addTest(new StoredProcedureQueryTestSuite("testStoredProcedureQueryExceptionWrapping1"));
        suite.addTest(new StoredProcedureQueryTestSuite("testStoredProcedureQueryExceptionWrapping2"));

        // Add the named Annotation query tests.
        suite.addTest(NamedStoredProcedureQueryTestSuite.suite());

        // These are EM API validation tests. These tests delete and update so
        // be careful where you introduce new tests.
        suite.addTest(new StoredProcedureQueryTestSuite("testClassCastExceptionOnExecuteWithNoOutputParameters"));
        suite.addTest(new StoredProcedureQueryTestSuite("testGetResultListOnDeleteQuery"));
        suite.addTest(new StoredProcedureQueryTestSuite("testGetResultListOnUpdateQuery"));
        suite.addTest(new StoredProcedureQueryTestSuite("testGetSingleResultOnDeleteQuery"));
        suite.addTest(new StoredProcedureQueryTestSuite("testGetSingleResultOnUpdateQuery"));

        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new AdvancedTableCreator().replaceTables(getPersistenceUnitServerSession());
        EmployeePopulator employeePopulator = new EmployeePopulator();
        employeePopulator.buildExamples();
        employeePopulator.persistExample(getPersistenceUnitServerSession());
        clearCache();
    }

    /**
     * Test a class cast exception.
     */
    public void testClassCastExceptionOnExecuteWithNoOutputParameters() {
        if (supportsStoredProcedures() && getPlatform().isMySQL()) {
            EntityManager em = createEntityManager();

            try {
                StoredProcedureQuery query = em.createStoredProcedureQuery("Read_All_Employees");
                boolean returnValue = query.execute();
                assertTrue("Execute didn't return true", returnValue);
                List<Employee> employees = query.getResultList();
                assertFalse("No employees were returned", employees.isEmpty());
            } catch (ClassCastException e) {
                if (isTransactionActive(em)){
                    rollbackTransaction(em);
                }

                fail("ClassCastException caught" + e);
            } finally {
                closeEntityManager(em);
            }
        }
    }

    /**
     * Test an expected exception.
     */
    public void testGetResultListOnDeleteQuery() {
        if (supportsStoredProcedures() && getPlatform().isMySQL()) {
            EntityManager em = createEntityManager();

            boolean exceptionCaught = false;

            try {
                em.createStoredProcedureQuery("Delete_All_Responsibilities").getResultList();
            } catch (IllegalStateException e) {
                if (isTransactionActive(em)){
                    rollbackTransaction(em);
                }

                exceptionCaught = true;
            } finally {
                closeEntityManager(em);
            }

            assertTrue("Expected Illegal state exception was not caught", exceptionCaught);
        }
    }

    /**
     * Test an expected exception.
     */
    public void testGetResultListOnUpdateQuery() {
        if (supportsStoredProcedures() && getPlatform().isMySQL()) {
            EntityManager em = createEntityManager();

            boolean exceptionCaught = false;

            try {
                String postalCodeTypo = "R3 1B9";
                String postalCodeCorrection = "R3B 1B9";

                StoredProcedureQuery query = em.createStoredProcedureQuery("Update_Address_Postal_Code");
                query.registerStoredProcedureParameter("new_p_code_v", String.class, ParameterMode.IN);
                query.registerStoredProcedureParameter("old_p_code_v", String.class, ParameterMode.IN);

                query.setParameter("new_p_code_v", postalCodeCorrection).setParameter("old_p_code_v", postalCodeTypo).getResultList();
            } catch (IllegalStateException e) {
                if (isTransactionActive(em)){
                    rollbackTransaction(em);
                }

                exceptionCaught = true;
            } finally {
                closeEntityManager(em);
            }

            assertTrue("Expected Illegal state exception was not caught", exceptionCaught);
        }
    }

    /**
     * Test an expected exception.
     */
    public void testGetSingleResultOnDeleteQuery() {
        if (supportsStoredProcedures() && getPlatform().isMySQL()) {
            EntityManager em = createEntityManager();

            boolean exceptionCaught = false;

            try {
                em.createStoredProcedureQuery("Delete_All_Responsibilities").getSingleResult();
            } catch (IllegalStateException e) {
                if (isTransactionActive(em)){
                    rollbackTransaction(em);
                }

                exceptionCaught = true;
            } finally {
                closeEntityManager(em);
            }

            assertTrue("Expected Illegal state exception was not caught", exceptionCaught);
        }
    }

    /**
     * Test an expected exception.
     */
    public void testGetSingleResultOnUpdateQuery() {
        if (supportsStoredProcedures() && getPlatform().isMySQL()) {
            EntityManager em = createEntityManager();

            boolean exceptionCaught = false;

            try {
                String postalCodeTypo = "R3 1B9";
                String postalCodeCorrection = "R3B 1B9";

                StoredProcedureQuery query = em.createStoredProcedureQuery("Update_Address_Postal_Code");
                query.registerStoredProcedureParameter("new_p_code_v", String.class, ParameterMode.IN);
                query.registerStoredProcedureParameter("old_p_code_v", String.class, ParameterMode.IN);

                query.setParameter("new_p_code_v", postalCodeCorrection);
                query.setParameter("old_p_code_v", postalCodeTypo);

                // Make these calls to test the getParameter call with a name.
                query.getParameter("new_p_code_v");
                query.getParameter("old_p_code_v");

                query.getSingleResult();
            } catch (IllegalStateException e) {
                if (isTransactionActive(em)){
                    rollbackTransaction(em);
                }

                exceptionCaught = true;
            } finally {
                closeEntityManager(em);
            }

            assertTrue("Expected Illegal state exception was not caught", exceptionCaught);
        }
    }

    /**
     * Tests a StoredProcedureQuery that does an update though EM API
     */
    public void testQueryExecute1() {
        if (supportsStoredProcedures() && getPlatform().isMySQL()) {
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

                assertTrue("Parameter list was not empty.", query.getParameters().size() == 0);

                query.registerStoredProcedureParameter("new_p_code_v", String.class, ParameterMode.IN);
                query.registerStoredProcedureParameter("old_p_code_v", String.class, ParameterMode.IN);
                query.registerStoredProcedureParameter("employee_count_v", Integer.class, ParameterMode.OUT);

                query.setParameter("new_p_code_v", postalCodeCorrection);
                query.setParameter("old_p_code_v", postalCodeTypo);

                boolean result = query.execute();

                assertTrue("Parameter list size was incorrect, actual: " + query.getParameters().size() + ", expecting 3.", query.getParameters().size() == 3);

                Object parameterValue = query.getParameterValue("new_p_code_v");
                assertTrue("The IN parameter value was not preserved, expected: " + postalCodeCorrection + ", actual: " + parameterValue, parameterValue.equals(postalCodeCorrection));

                assertTrue("Result did not return true for a result set.", result);

                query.getResultList();

                assertTrue("The query didn't have any more results.", query.hasMoreResults());
                List<Employee> employeeResults = query.getResultList();
                int numberOfEmployes = employeeResults.size();

                // Should return false (no more results)
                assertFalse("The query had more results.", query.hasMoreResults());

                // Get the update count.
                int updateCount = query.getUpdateCount();
                assertTrue("Update count incorrect: " + updateCount, updateCount == 2);

                // Update count should return -1 now.
                assertTrue("Update count should be -1.", query.getUpdateCount() == -1);

                // Check output parameters by name.
                Object outputParamValueFromName = query.getOutputParameterValue("employee_count_v");
                assertNotNull("The output parameter was null.", outputParamValueFromName);
                // TODO: to investigate. This little bit is hacky. For some
                // reason MySql returns a Long here. By position is ok, that is,
                // it returns an Integer (as we registered)
                if (outputParamValueFromName instanceof Long) {
                    assertTrue("Incorrect value returned, expected " + numberOfEmployes + ", got: " + outputParamValueFromName, outputParamValueFromName.equals(new Long(numberOfEmployes)));
                } else if (outputParamValueFromName instanceof Integer) {
                    assertTrue("Incorrect value returned, expected " + numberOfEmployes + ", got: " + outputParamValueFromName, outputParamValueFromName.equals(numberOfEmployes));
                }

                // TODO: else, don't worry about it for now ...

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

                // Just because we don't trust anyone ... :-)
                Address a1 = em.find(Address.class, address1.getId());
                assertTrue("The postal code was not updated for address 1.", a1.getPostalCode().equals(postalCodeCorrection));
                Address a2 = em.find(Address.class, address2.getId());
                assertTrue("The postal code was not updated for address 2.", a2.getPostalCode().equals(postalCodeCorrection));
                Address a3 = em.find(Address.class, address3.getId());
                assertTrue("The postal code was not updated for address 3.", a3.getPostalCode().equals(postalCodeCorrection));
            } finally {
                // The open statement/connection will be closed here.
                closeEntityManagerAndTransaction(em);
            }
        }
    }

    /**
     * Tests a StoredProcedureQuery that does an update though EM API
     * This is the same test as above except different retrieval path.
     */
    public void testQueryExecute2() {
        if (supportsStoredProcedures() && getPlatform().isMySQL()) {
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

                boolean result = query.setParameter("new_p_code_v", postalCodeCorrection).setParameter("old_p_code_v", postalCodeTypo).execute();
                assertTrue("Result did not return true for a result set.", result);

                // This shouldn't affect where we are in the retrieval of the query.
                assertTrue("We have didn't have any more results", query.hasMoreResults());
                assertTrue("We have didn't have any more results", query.hasMoreResults());
                assertTrue("We have didn't have any more results", query.hasMoreResults());
                assertTrue("We have didn't have any more results", query.hasMoreResults());
                assertTrue("We have didn't have any more results", query.hasMoreResults());
                assertTrue("We have didn't have any more results", query.hasMoreResults());
                assertTrue("We have didn't have any more results", query.hasMoreResults());

                query.getResultList();

                // We know there should be more results so ask for them without checking for has more results.
                List<Employee> employeeResults = query.getResultList();
                int numberOfEmployes = employeeResults.size();

                // Should return false (no more results)
                assertFalse("The query had more results.", query.hasMoreResults());
                assertFalse("The query had more results.", query.hasMoreResults());
                assertFalse("The query had more results.", query.hasMoreResults());
                assertFalse("The query had more results.", query.hasMoreResults());

                assertNull("getResultList after no results did not return null", query.getResultList());

                // Get the update count.
                int updateCount = query.getUpdateCount();
                assertTrue("Update count incorrect: " + updateCount, updateCount == 2);

                // Update count should return -1 now.
                assertTrue("Update count should be -1.", query.getUpdateCount() == -1);
                assertTrue("Update count should be -1.", query.getUpdateCount() == -1);
                assertTrue("Update count should be -1.", query.getUpdateCount() == -1);
                assertTrue("Update count should be -1.", query.getUpdateCount() == -1);
                assertTrue("Update count should be -1.", query.getUpdateCount() == -1);

                // Check output parameters by name.
                Object outputParamValueFromName = query.getOutputParameterValue("employee_count_v");
                assertNotNull("The output parameter was null.", outputParamValueFromName);
                // TODO: to investigate. This little bit is hacky. For some
                // reason MySql returns a Long here. By position is ok, that is,
                // it returns an Integer (as we registered)
                if (outputParamValueFromName instanceof Long) {
                    assertTrue("Incorrect value returned, expected " + numberOfEmployes + ", got: " + outputParamValueFromName, outputParamValueFromName.equals(new Long(numberOfEmployes)));
                } else if (outputParamValueFromName instanceof Integer) {
                    assertTrue("Incorrect value returned, expected " + numberOfEmployes + ", got: " + outputParamValueFromName, outputParamValueFromName.equals(numberOfEmployes));
                }
                // TODO: else, don't worry about it for now ...

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

                // Just because we don't trust anyone ... :-)
                Address a1 = em.find(Address.class, address1.getId());
                assertTrue("The postal code was not updated for address 1.", a1.getPostalCode().equals(postalCodeCorrection));
                Address a2 = em.find(Address.class, address2.getId());
                assertTrue("The postal code was not updated for address 2.", a2.getPostalCode().equals(postalCodeCorrection));
                Address a3 = em.find(Address.class, address3.getId());
                assertTrue("The postal code was not updated for address 3.", a3.getPostalCode().equals(postalCodeCorrection));
            } finally {
                // The open statement/connection will be closed here.
                closeEntityManagerAndTransaction(em);
            }
        }
    }

    /**
     * Tests a StoredProcedureQuery that does an update though EM API
     */
    public void testQueryExecuteUpdate() {
        if (supportsStoredProcedures() && getPlatform().isMySQL()) {
            EntityManager em = createEntityManager();

            try {
                String postalCodeTypo = "R3 1B9";
                String postalCodeCorrection = "R3B 1B9";

                StoredProcedureQuery query = em.createStoredProcedureQuery("Update_Address_Postal_Code");
                query.registerStoredProcedureParameter("new_p_code_v", String.class, ParameterMode.IN);
                query.registerStoredProcedureParameter("old_p_code_v", String.class, ParameterMode.IN);

                try {
                    query.setParameter("new_p_code_v", postalCodeCorrection).setParameter("old_p_code_v", postalCodeTypo).executeUpdate();
                    fail("TransactionRequiredException not caught");
                } catch (TransactionRequiredException e) {
                   // ignore since expected exception.
                }

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

                em.flush();

                // Clear the cache
                em.clear();
                clearCache();

                query = em.createStoredProcedureQuery("Update_Address_Postal_Code");
                query.registerStoredProcedureParameter("new_p_code_v", String.class, ParameterMode.IN);
                query.registerStoredProcedureParameter("old_p_code_v", String.class, ParameterMode.IN);

                int results = query.setParameter("new_p_code_v", postalCodeCorrection).setParameter("old_p_code_v", postalCodeTypo).executeUpdate();

                assertTrue("Update count incorrect.", results == 2);

                // Just because we don't trust anyone ... :-)
                Address a1 = em.find(Address.class, address1.getId());
                assertTrue("The postal code was not updated for address 1.", a1.getPostalCode().equals(postalCodeCorrection));
                Address a2 = em.find(Address.class, address2.getId());
                assertTrue("The postal code was not updated for address 2.", a2.getPostalCode().equals(postalCodeCorrection));
            } finally {
                closeEntityManagerAndTransaction(em);
            }
        }
    }

    /**
     * Tests a StoredProcedureQuery that does an select though EM API
     */
    public void testQueryGetResultList() {
        if (supportsStoredProcedures() && getPlatform().isMySQL()) {
            EntityManager em = createEntityManager();

            try {
                beginTransaction(em);

                Address address1 = new Address();
                address1.setCity("Edmonton");
                address1.setPostalCode("T5B 4M9");
                address1.setProvince("AB");
                address1.setStreet("7424 118 Avenue");
                address1.setCountry("Canada");
                em.persist(address1);
                em.flush();

                // Clear the cache
                em.clear();
                clearCache();

                StoredProcedureQuery query = em.createStoredProcedureQuery("Read_Address");
                query.registerStoredProcedureParameter("address_id_v", Integer.class, ParameterMode.IN);

                List addresses = query.setParameter("address_id_v", address1.getId()).getResultList();
                assertTrue("Incorrect number of addresses returned", addresses.size() == 1);
                Object[] addressContent = (Object[]) addresses.get(0);
                assertTrue("Incorrect data content size", addressContent.length == 6);
                assertTrue("Id content incorrect", addressContent[0].equals((long) address1.getId()));
                assertTrue("Steet content incorrect", addressContent[1].equals(address1.getStreet()));
                assertTrue("City content incorrect", addressContent[2].equals(address1.getCity()));
                assertTrue("Country content incorrect", addressContent[3].equals(address1.getCountry()));
                assertTrue("Province content incorrect", addressContent[4].equals(address1.getProvince()));
                assertTrue("Postal Code content incorrect", addressContent[5].equals(address1.getPostalCode()));
            } finally {
                closeEntityManagerAndTransaction(em);
            }
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

            List allResults = (List)getPersistenceUnitServerSession().executeQuery(query);
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
     * Tests a StoredProcedureQuery using a result-set mapping though EM API
     */
    public void testQueryWithNamedFieldResult() {
        if (supportsStoredProcedures() && getPlatform().isMySQL()) {
            EntityManager em = createEntityManager();

            try {
                beginTransaction(em);

                Address address = new Address();
                address.setCity("Winnipeg");
                address.setPostalCode("R3B 1B9");
                address.setProvince("MB");
                address.setStreet("510 Main Street");
                address.setCountry("Canada");
                em.persist(address);
                em.flush();

                // Clear the cache
                em.clear();
                clearCache();

                StoredProcedureQuery query = em.createStoredProcedureQuery("Read_Address_Mapped_Named", "address-column-result-map");
                query.registerStoredProcedureParameter("address_id_v", Integer.class, ParameterMode.IN);

                Object[] values = (Object[]) query.setParameter("address_id_v", address.getId()).getSingleResult();
                assertTrue("Address data not found or returned using stored procedure", ((values != null) && (values.length == 6)));
                assertNotNull("No results returned from store procedure call", values[1]);
                assertTrue("Address not found using stored procedure", address.getStreet().equals(values[1]));
            } finally {
                closeEntityManagerAndTransaction(em);
            }
        }
    }

    /**
     * Tests a NamedStoredProcedureQuery using a result-set-mapping using
     * positional parameters (and more than the procedure expects).
     */
    public void testQueryWithNumberedFieldResult() {
        if (supportsStoredProcedures() && getPlatform().isMySQL()) {
            EntityManager em = createEntityManager();

            try {
                beginTransaction(em);

                Address address1 = new Address();
                address1.setCity("Ottawa");
                address1.setPostalCode("K2J 0L7");
                address1.setProvince("ON");
                address1.setStreet("321 Main");
                address1.setCountry("Canada");
                em.persist(address1);
                em.flush();

                // Clear the cache
                em.clear();
                clearCache();

                StoredProcedureQuery query = em.createNamedStoredProcedureQuery("ReadAddressMappedNumberedFieldResult");
                List<Address> addresses = query.setParameter(1, address1.getId()).getResultList();

                assertTrue("Too many addresses returned", addresses.size() == 1);

                Address address2 = addresses.get(0);

                assertNotNull("Address returned from stored procedure is null", address2);
                assertTrue("Address didn't build correctly using stored procedure", (address1.getId() == address2.getId()));
                assertTrue("Address didn't build correctly using stored procedure", (address1.getStreet().equals(address2.getStreet())));
                assertTrue("Address didn't build correctly using stored procedure", (address1.getCountry().equals(address2.getCountry())));
                assertTrue("Address didn't build correctly using stored procedure", (address1.getProvince().equals(address2.getProvince())));
                assertTrue("Address didn't build correctly using stored procedure", (address1.getPostalCode().equals(address2.getPostalCode())));
            } finally {
                closeEntityManagerAndTransaction(em);
            }
        }
    }

    /**
     * Tests a StoredProcedureQuery using a class though EM API
     */
    public void testQueryWithResultClass() {
        if (supportsStoredProcedures() && getPlatform().isMySQL()) {
            EntityManager em = createEntityManager();

            try {
                beginTransaction(em);

                Address address1 = new Address();
                address1.setCity("Victoria");
                address1.setPostalCode("V9A 6A9");
                address1.setProvince("BC");
                address1.setStreet("785 Lampson Street");
                address1.setCountry("Canada");
                em.persist(address1);
                em.flush();

                // Clear the cache
                em.clear();
                clearCache();

                StoredProcedureQuery query = em.createStoredProcedureQuery("Read_Address", org.eclipse.persistence.testing.models.jpa22.advanced.Address.class);
                query.registerStoredProcedureParameter("address_id_v", Integer.class, ParameterMode.IN);

                Address address2 = (Address) query.setParameter("address_id_v", address1.getId()).getSingleResult();
                assertNotNull("Address returned from stored procedure is null", address2);
                assertTrue("Address didn't build correctly using stored procedure", (address1.getId() == address2.getId()));
                assertTrue("Address didn't build correctly using stored procedure", (address1.getStreet().equals(address2.getStreet())));
                assertTrue("Address didn't build correctly using stored procedure", (address1.getCountry().equals(address2.getCountry())));
                assertTrue("Address didn't build correctly using stored procedure", (address1.getProvince().equals(address2.getProvince())));
                assertTrue("Address didn't build correctly using stored procedure", (address1.getPostalCode().equals(address2.getPostalCode())));
            } finally {
                closeEntityManagerAndTransaction(em);
            }
        }
    }

    /**
     * Tests a StoredProcedureQuery using a class though EM API
     */
    public void testQueryWithOutParam() {
        if (supportsStoredProcedures() && getPlatform().isMySQL()) {
            EntityManager em = createEntityManager();

            try {
                beginTransaction(em);

                Address address = new Address();
                address.setCity("TestCity");
                address.setPostalCode("V4U 1P2");
                address.setProvince("Nunavut");
                address.setStreet("269 Lust Lane");
                address.setCountry("Canada");
                em.persist(address);
                em.flush();

                // Clear the cache
                em.clear();
                clearCache();

                StoredProcedureQuery query = em.createStoredProcedureQuery("Read_Address_City");
                query.registerStoredProcedureParameter("address_id_v", Integer.class, ParameterMode.IN);
                query.registerStoredProcedureParameter("city_v", String.class, ParameterMode.OUT);

                query.setParameter("address_id_v", address.getId()).execute();
                String city = (String) query.getOutputParameterValue("city_v");
                assertTrue("Incorrect city was returned.", (address.getCity().equals(city)));
            } finally {
                closeEntityManagerAndTransaction(em);
            }
        }
    }

    /**
     * Test stored procedure parameter API.
     */
    public void testStoredProcedureParameterAPI() {
        if (supportsStoredProcedures() && getPlatform().isMySQL()) {
            EntityManager em = createEntityManager();

            try {
                StoredProcedureQuery query = em.createStoredProcedureQuery("Parameter_Testing");
                query.registerStoredProcedureParameter(1, String.class, ParameterMode.IN);
                query.registerStoredProcedureParameter(2, Integer.class, ParameterMode.INOUT);
                query.registerStoredProcedureParameter(3, Integer.class, ParameterMode.OUT);

                query.setParameter(1, "1");
                query.setParameter(2, 2);

                // Make this call to test the getParameter call with a position.
                query.getParameter(1);
                query.getParameter(2);
                query.getParameter(3);

            } catch (IllegalArgumentException e) {
                if (isTransactionActive(em)){
                    rollbackTransaction(em);
                }

                fail("IllegalArgumentException was caught");
            } finally {
                closeEntityManager(em);
            }
        }
    }

    /**
     * Tests a StoredProcedureQuery using a system cursor. Also tests
     * getParameters call BEFORE query execution.
     */
    public void testStoredProcedureQuerySysCursor_Named() {
        if (supportsStoredProcedures() && getPlatform().isOracle() ) {
            EntityManager em = createEntityManager();

            try {
                StoredProcedureQuery query = em.createStoredProcedureQuery("Read_Using_Sys_Cursor", Employee.class);
                query.registerStoredProcedureParameter("f_name_v", String.class, ParameterMode.IN);
                query.registerStoredProcedureParameter("p_recordset", void.class, ParameterMode.REF_CURSOR);

                // Test the getParameters call BEFORE query execution.
                assertTrue("The number of parameters returned was incorrect, actual: " + query.getParameters().size() + ", expected 2", query.getParameters().size() == 2);

                query.setParameter("f_name_v", "Fred");

                boolean execute = query.execute();

                assertTrue("Execute returned false.", execute);

                List<Employee> employees = (List<Employee>) query.getOutputParameterValue("p_recordset");
                assertFalse("No employees were returned", employees.isEmpty());
            } finally {
                closeEntityManagerAndTransaction(em);
            }
        }
    }

    /**
     * Tests a StoredProcedureQuery using a system cursor. Also tests
     * getParameters call BEFORE query execution.
     */
    public void testStoredProcedureQuerySysCursor_Positional() {
        if (supportsStoredProcedures() && getPlatform().isOracle() ) {
            EntityManager em = createEntityManager();

            try {
                StoredProcedureQuery query = em.createStoredProcedureQuery("Read_Using_Sys_Cursor", Employee.class);
                query.registerStoredProcedureParameter(1, String.class, ParameterMode.IN);
                query.registerStoredProcedureParameter(2, void.class, ParameterMode.REF_CURSOR);

                // Test the getParameters call BEFORE query execution.
                assertTrue("The number of parameters returned was incorrect, actual: " + query.getParameters().size() + ", expected 2", query.getParameters().size() == 2);

                query.setParameter(1, "Fred");

                boolean execute = query.execute();

                assertTrue("Execute returned false.", execute);

                List<Employee> employees = (List<Employee>) query.getOutputParameterValue(2);
                assertFalse("No employees were returned", employees.isEmpty());
            } finally {
                closeEntityManager(em);
            }
        }
    }

    /**
     * Tests a StoredProcedureQuery using a system cursor. Also tests
     * getParameters call AFTER query execution.
     */
    public void testStoredProcedureQuerySysCursor2() {
        if (supportsStoredProcedures() && getPlatform().isOracle() ) {
            EntityManager em = createEntityManager();

            try {
                // Test stored procedure query created through API. //
                beginTransaction(em);

                StoredProcedureQuery query = em.createStoredProcedureQuery("Read_Using_Sys_Cursor");
                query.registerStoredProcedureParameter("f_name_v", String.class, ParameterMode.IN);
                query.registerStoredProcedureParameter("p_recordset", void.class, ParameterMode.REF_CURSOR);

                query.setParameter("f_name_v", "Fred");

                boolean execute = query.execute();

                assertTrue("Execute returned false.", execute);

                // Test the getParameters call AFTER query execution.
                assertTrue("The number of paramters returned was incorrect, actual: " + query.getParameters().size() + ", expected 2", query.getParameters().size() == 2);

                List<Object[]> employees = (List<Object[]>) query.getOutputParameterValue("p_recordset");
                assertFalse("No employees were returned", employees.isEmpty());

                commitTransaction(em);

                // Test now with the named stored procedure. //
                beginTransaction(em);

                StoredProcedureQuery query2 = em.createNamedStoredProcedureQuery("read_using_sys_cursor");
                query2.setParameter("f_name_v", "Fred");
                query2.getParameterValue("f_name_v");

                query2.execute();

                List<Object[]> employees2 = (List<Object[]>) query2.getOutputParameterValue("p_recordset");
                assertFalse("No employees were returned from name stored procedure query.", employees2.isEmpty());

                commitTransaction(em);
            } finally {
                closeEntityManagerAndTransaction(em);
            }
        }
    }

    /**
     * Tests StoredProcedureQuery exception wrapping.
     */
    public void testStoredProcedureQueryExceptionWrapping1() {
        EntityManager em = createEntityManager();
        try {
            javax.persistence.Query query = em.createNativeQuery("DoesNotExist", Employee.class);

            Object execute = query.getResultList();
            fail("Executing a bad native SQL query did not throw a PersistenceException and instead returned: "+execute);
        } catch (javax.persistence.PersistenceException pe) {
            //expected.
        } catch (RuntimeException re) {
            fail("Executing a bad native SQL query did not throw a PersistenceException and instead threw: "+re);
        } finally {
            closeEntityManager(em);
        }
    }

    /**
     * Tests StoredProcedureQuery exception wrapping.
     */
    public void testStoredProcedureQueryExceptionWrapping2() {
        EntityManager em = createEntityManager();
        try {
            StoredProcedureQuery query = em.createStoredProcedureQuery("DoesNotExist", Employee.class);

            boolean execute = query.execute();
            fail("Executing a non-existent stored procedure did not throw a PersistenceException and instead returned: "+execute);
        } catch (javax.persistence.PersistenceException pe) {
            //expected.
        } catch (RuntimeException re) {
            fail("Executing a non-existent stored procedure did not throw a PersistenceException and instead threw: "+re);
        } finally {
            closeEntityManager(em);
        }
    }
}
