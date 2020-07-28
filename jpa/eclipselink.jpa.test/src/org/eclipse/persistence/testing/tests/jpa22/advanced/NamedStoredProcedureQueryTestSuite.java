/*
 * Copyright (c) 2012, 2020 Oracle and/or its affiliates. All rights reserved.
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
//     02/08/2012-2.4 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
//     06/20/2012-2.5 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
//     07/13/2012-2.5 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
//     08/24/2012-2.5 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
//     09/13/2013-2.5 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
//     09/27/2012-2.5 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
//     11/05/2012-2.5 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
//     01/23/2013-2.5 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
package org.eclipse.persistence.testing.tests.jpa22.advanced;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.StoredProcedureQuery;

import junit.framework.TestSuite;
import junit.framework.Test;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.internal.jpa.StoredProcedureQueryImpl;
import org.eclipse.persistence.testing.framework.QuerySQLTracker;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;

import org.eclipse.persistence.testing.models.jpa22.advanced.Address;
import org.eclipse.persistence.testing.models.jpa22.advanced.LargeProject;
import org.eclipse.persistence.testing.models.jpa22.advanced.SmallProject;
import org.eclipse.persistence.testing.models.jpa22.advanced.Employee;

public class NamedStoredProcedureQueryTestSuite extends JUnitTestCase {
    public NamedStoredProcedureQueryTestSuite() {}

    public NamedStoredProcedureQueryTestSuite(String name) {
        super(name);
        setPuName("MulitPU-1");
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("NamedStoredProcedureQueryTestSuite");

        suite.addTest(new NamedStoredProcedureQueryTestSuite("testQueryExecuteUpdateOnSelectQueryWithNoResultClass"));
        suite.addTest(new NamedStoredProcedureQueryTestSuite("testQueryExecuteUpdateOnSelectQueryWithResultClass"));
        suite.addTest(new NamedStoredProcedureQueryTestSuite("testQueryExecuteWithNamedCursors"));
        suite.addTest(new NamedStoredProcedureQueryTestSuite("testQueryExecuteWithUnNamedCursor"));
        suite.addTest(new NamedStoredProcedureQueryTestSuite("testQueryGetResultListWithNamedCursors"));
        suite.addTest(new NamedStoredProcedureQueryTestSuite("testQueryWithMultipleResults"));
        suite.addTest(new NamedStoredProcedureQueryTestSuite("testQueryWithNamedColumnResult"));
        suite.addTest(new NamedStoredProcedureQueryTestSuite("testQueryWithNamedColumnResultTranslationIntoNumbered"));
        suite.addTest(new NamedStoredProcedureQueryTestSuite("testQueryWithNamedFieldResult"));
        suite.addTest(new NamedStoredProcedureQueryTestSuite("testQueryWithNumberedFieldResult"));
        suite.addTest(new NamedStoredProcedureQueryTestSuite("testQueryWithResultClass"));
        suite.addTest(new NamedStoredProcedureQueryTestSuite("testQueryExecuteOnStoredProcQueryBuiltFromJPAThatDoesNothing"));

        return suite;
    }

    /**
     * Tests an execute update on a named stored procedure that does a select.
     * NamedStoredProcedure defines a result class.
     */
    public void testQueryExecuteOnStoredProcQueryBuiltFromJPAThatDoesNothing() {
        if (supportsStoredProcedures() && getPlatform().isMySQL()) {
            EntityManager em = createEntityManager();

            try {
                getServerSession(getPersistenceUnitName()).executeQuery(em.createNamedStoredProcedureQuery("ReadNoAddresses").unwrap(StoredProcedureQueryImpl.class).getDatabaseQuery());
            } catch (Exception e) {
                if (isTransactionActive(em)){
                    rollbackTransaction(em);
                }

                fail("Exception was caught: " + e);
            } finally {
                closeEntityManager(em);
            }
        }
    }

    /**
     * Tests an execute update on a named stored procedure that does a select.
     * NamedStoredProcedure defines a result class.
     */
    public void testQueryExecuteUpdateOnSelectQueryWithNoResultClass() {
        if (supportsStoredProcedures() && getPlatform().isMySQL()) {
            EntityManager em = createEntityManager();

            try {
                beginTransaction(em);
                em.createNamedStoredProcedureQuery("ReadAllAddressesWithNoResultClass").executeUpdate();
                commitTransaction(em);
            } catch (IllegalStateException e) {
                if (isTransactionActive(em)){
                    rollbackTransaction(em);
                }

                return;
            } finally {
                closeEntityManager(em);
            }

            fail("Expected Illegal state exception was not thrown.");
        }
    }

    /**
     * Tests an execute update on a named stored procedure that does a select.
     * NamedStoredProcedure defines a result class.
     */
    public void testQueryExecuteUpdateOnSelectQueryWithResultClass() {
        if (supportsStoredProcedures() && getPlatform().isMySQL()) {
            EntityManager em = createEntityManager();

            try {
                beginTransaction(em);
                em.createNamedStoredProcedureQuery("ReadAddressWithResultClass").setParameter("address_id_v", 1).executeUpdate();
                commitTransaction(em);
            } catch (IllegalStateException e) {
                if (isTransactionActive(em)){
                    rollbackTransaction(em);
                }

                return;
            } finally {
                closeEntityManager(em);
            }

            fail("Expected Illegal state exception was not thrown.");
        }
    }

    /**
     * Tests a StoredProcedureQuery using multiple names cursors.
     */
    public void testQueryExecuteWithNamedCursors() {
        if (supportsStoredProcedures() && getPlatform().isOracle() ) {
            EntityManager em = createEntityManager();

            try {
                StoredProcedureQuery query = em.createNamedStoredProcedureQuery("ReadUsingNamedRefCursors");

                boolean returnVal = query.execute();

                List<Employee> employees = (List<Employee>) query.getOutputParameterValue("CUR1");
                assertFalse("No employees were returned", employees.isEmpty());
                List<Address> addresses = (List<Address>) query.getOutputParameterValue("CUR2");
                assertFalse("No addresses were returned", addresses.isEmpty());
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
     * Tests a StoredProcedureQuery with an unnamed cursor.
     */
    public void testQueryExecuteWithUnNamedCursor() {
        if (supportsStoredProcedures() && getPlatform().isOracle() ) {
            EntityManager em = createEntityManager();

            try {
                StoredProcedureQuery query = em.createNamedStoredProcedureQuery("ReadUsingUnNamedRefCursor");

                boolean returnVal = query.execute();

                List<Employee> employees = (List<Employee>) query.getOutputParameterValue(1);
                assertFalse("No employees were returned", employees.isEmpty());
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
     * Tests a StoredProcedureQuery on Oracle using named ref cursors.
     */
    public void testQueryGetResultListWithNamedCursors() {
        if (supportsStoredProcedures() && getPlatform().isOracle() ) {
            EntityManager em = createEntityManager();

            try {
                StoredProcedureQuery query = em.createNamedStoredProcedureQuery("ReadUsingNamedRefCursors");

                List<Employee> employees = (List<Employee>) query.getResultList();
                assertFalse("No employees were returned", employees.isEmpty());

                List<Address> addresses = (List<Address>) query.getResultList();
                assertFalse("No addresses were returned", addresses.isEmpty());
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
     * Test multiple result sets by setting the SQL results set mapping from annotation.
     */
    public void testQueryWithMultipleResults() throws Exception {
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
     * Tests a NamedStoredProcedureQuery annotation using a result-set mapping.
     */
    public void testQueryWithNamedColumnResult() {
        if (supportsStoredProcedures() && getPlatform().isMySQL()) {
            EntityManager em = createEntityManager();

            try {
                beginTransaction(em);

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

                Object[] values = (Object[]) em.createNamedStoredProcedureQuery("ReadAddressMappedNamedColumnResult").setParameter("address_id_v", address.getId()).getSingleResult();
                assertTrue("Address data not found or returned using stored procedure", ((values != null) && (values.length == 6)));
                assertNotNull("No results returned from store procedure call", values[1]);
                assertTrue("Address not found using stored procedure", address.getStreet().equals(values[1]));
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
     * Tests a NamedStoredProcedureQuery using positional paramters transalated from named.
     */
    public void testQueryWithNamedColumnResultTranslationIntoNumbered() {
        if (supportsStoredProcedures() && getPlatform().isMySQL()) {
            Map<String, String> properties = new HashMap<String, String>();
            properties.put(PersistenceUnitProperties.NAMING_INTO_INDEXED, "true");
            EntityManager em = createEntityManager(properties);
            QuerySQLTracker querySQLTracker = new QuerySQLTracker(((org.eclipse.persistence.jpa.JpaEntityManager)em).getServerSession());

            try {
                beginTransaction(em);

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

                Object[] values = (Object[]) em.createNamedStoredProcedureQuery("ReadAddressMappedNamedColumnResult").setParameter("address_id_v", address.getId()).getSingleResult();
                assertTrue("Address data not found or returned using stored procedure", ((values != null) && (values.length == 6)));
                assertNotNull("No results returned from store procedure call", values[1]);
                assertTrue("Address not found using stored procedure", address.getStreet().equals(values[1]));

                String sqlStatement = querySQLTracker.getSqlStatements().get(0);
                int count = (sqlStatement.split("=>", -1).length) - 1;
                assertEquals("Transformation into numbered parameters was not called", 1, count);
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
     * Tests a NamedStoredProcedureQuery using a result-set mapping.
     */
    public void testQueryWithNamedFieldResult() {
        if (supportsStoredProcedures() && getPlatform().isMySQL()) {
            EntityManager em = createEntityManager();

            try {
                beginTransaction(em);

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

                Address address2 = (Address) em.createNamedStoredProcedureQuery("ReadAddressMappedNamedFieldResult").setParameter("address_id_v", address1.getId()).getSingleResult();
                assertNotNull("Address returned from stored procedure is null", address2);
                assertTrue("Address not found using stored procedure", (address1.getId() == address2.getId()));
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
     * Tests a NamedStoredProcedureQuery using positional paramters.
     */
    public void testQueryWithNumberedFieldResult() {
        if (supportsStoredProcedures() && getPlatform().isMySQL()) {
            EntityManager em = createEntityManager();

            try {
                beginTransaction(em);

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

                Address address2 = (Address) em.createNamedStoredProcedureQuery("ReadAddressMappedNumberedFieldResult").setParameter(1, address1.getId()).getSingleResult();
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

                throw e;
            } finally {
                closeEntityManager(em);
            }
        }
    }

    /**
     * Tests a NamedStoredProcedureQuery using a result-class.
     */
    public void testQueryWithResultClass() {
        if (supportsStoredProcedures() && getPlatform().isMySQL()) {
            EntityManager em = createEntityManager();

            try {
                beginTransaction(em);

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

                Address address2 = (Address) em.createNamedStoredProcedureQuery("ReadAddressWithResultClass").setParameter("address_id_v", address1.getId()).getSingleResult();
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

                throw e;
            } finally {
                closeEntityManager(em);
            }
        }
    }
    @Override
    public String getPersistenceUnitName() {
       return "MulitPU-1";
    }
}
