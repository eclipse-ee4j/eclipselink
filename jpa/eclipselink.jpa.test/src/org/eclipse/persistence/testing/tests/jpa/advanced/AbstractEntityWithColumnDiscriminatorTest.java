/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     jlamande    - Initial API and implementation
//     Tomas Kraus - EclipseLink jUnit tests integration
package org.eclipse.persistence.testing.tests.jpa.advanced;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.advanced.customer.CustomerAddress;
import org.eclipse.persistence.testing.models.jpa.advanced.customer.AddressType1;
import org.eclipse.persistence.testing.models.jpa.advanced.customer.AddressType2;
import org.eclipse.persistence.testing.models.jpa.advanced.customer.RegisteredCustomer;

/**
 * Runs tests on advanced customer model with {@link RegisteredCustomer} entity having two abstract
 * {@link CustomerAddress} class instances as attributes.
 * Each of those addresses may contain instances of different offspring. Address entities are mapped into a single
 * table using {@code DiscriminatorColumn} to distinguish them.
 */
public class AbstractEntityWithColumnDiscriminatorTest extends JUnitTestCase {

    /** {@link RegisteredCustomer} entity name attribute value. */
    private static final String CUSTOMER_NAME = "JONES";

    /** Value of joined {@code billingAddress} entity street attribute. */
    private static final String CUSTOMER_ADDR_BILLING = "STREET1";

    /** Value of joined {@code deliveryAddress} entity street attribute. */
    private static final String CUSTOMER_ADDR_DELIVERY = "STREET2";

    /** Persistence unit name. */
    private static final String PU_NAME = "customer-pu";

    /**
     * Build collection of test cases for this jUnit test instance.
     * @return {@link Test} class containing collection of test cases for this jUnit test instance.
     */
    public static Test suite() {
        final TestSuite suite = new TestSuite();
        suite.addTest(new AbstractEntityWithColumnDiscriminatorTest("testSetup"));
        addTests(suite);
        suite.addTest(new AbstractEntityWithColumnDiscriminatorTest("testCleanup"));
        return suite;
    }

    /**
     * Adds test, similar to suite() but without adding a setup and cleanup. Used from {@code suite()} to add individual
     * tests.
     * @param suite Target {@link TestSuite} class where to store tests.
     * @return {@link Test} class containing collection of test cases for this jUnit test instance.
     */
    public static Test addTests(final TestSuite suite){
        suite.setName("NamedQueryJUnitTest");
        suite.addTest(new AbstractEntityWithColumnDiscriminatorTest("testJpqlNoFetch"));
        suite.addTest(new AbstractEntityWithColumnDiscriminatorTest("testJpqlFetch"));
        suite.addTest(new AbstractEntityWithColumnDiscriminatorTest("testCriteriaNoFetch"));
        suite.addTest(new AbstractEntityWithColumnDiscriminatorTest("testCriteriaFetch"));
        return suite;
    }

    /**
     * Create {@link RegisteredCustomer} entity instance with {@code billingAddress} set to {@link AddressType1}
     * instance and {@code deliveryAddress} set to {@link AddressType2} instance. Entity is persisted.
     * @param em An {@link EntityManager} instance used to persist entities.
     */
    private static void createCustomer(final EntityManager em) {
        em.getTransaction().begin();
        try {
            final RegisteredCustomer cus = new RegisteredCustomer(CUSTOMER_NAME, new AddressType1(
                    CUSTOMER_ADDR_BILLING), new AddressType2(CUSTOMER_ADDR_DELIVERY));
            em.persist(cus);
            em.getTransaction().commit();
        } catch (Throwable e) {
            em.getTransaction().rollback();
            throw e;
        }
    }

    /**
     * Delete all {@link RegisteredCustomer} entities.
     * @param em An {@link EntityManager} instance used to delete entities.
     */
    private static void deleteAllCustomers(final EntityManager em) {
        List<RegisteredCustomer> customers = em.createNamedQuery(
                "RegisteredCustomer.selecAll", RegisteredCustomer.class).getResultList();
        em.getTransaction().begin();
        try {
            for (RegisteredCustomer cus : customers) {
                em.remove(cus);
            }
            em.getTransaction().commit();
        } catch (Throwable e) {
            em.getTransaction().rollback();
            throw e;
        }
    }

    /**
     * Validate {@link RegisteredCustomer} entity instance.
     * @param customer {@link RegisteredCustomer} entity instance to be validated.
     */
    private static void assertCustomer(final RegisteredCustomer customer) {
        assertEquals(CUSTOMER_NAME, customer.getName());
        assertTrue(customer.getBillingAddress() instanceof AddressType1);
        assertEquals(CUSTOMER_ADDR_BILLING, ((AddressType1) customer.getBillingAddress()).getStreet());
        assertTrue(customer.getDeliveryAddress() instanceof AddressType2);
        assertEquals(CUSTOMER_ADDR_DELIVERY, ((AddressType2) customer.getDeliveryAddress()).getStreet());
    }

    /**
     * Constructs an instance of {@code AbstractEntityWithColumnDiscriminatorTest} class.
     */
    public AbstractEntityWithColumnDiscriminatorTest() {
        super();
        setPuName(PU_NAME);
    }

    /**
     * Constructs an instance of {@code AbstractEntityWithColumnDiscriminatorTest} class with given test case name.
     * @param name Test case name.
     */
    public AbstractEntityWithColumnDiscriminatorTest(final String name) {
        super(name);
        setPuName(PU_NAME);
    }

    /**
     * Initial setup is done as first test in collection, both to record its failure, and to allow execution
     * in the server.
     */
    public void testSetup() {
        clearCache(PU_NAME);
        final EntityManager em = createEntityManager(PU_NAME);
        try {
            createCustomer(em);
        } finally {
            em.close();
        }
    }

    /**
     * Final cleanup is done as last test in collection, both to record its failure, and to allow execution
     * in the server.
     */
    public void testCleanup() {
        final EntityManager em = createEntityManager(PU_NAME);
        try {
            deleteAllCustomers(em);
        } finally {
            em.close();
        }
    }

    /**
     * Test JPQL query on {@link RegisteredCustomer} without {@code LEFT JOIN FETCH} of {@link CustomerAddress}
     * entities.
     */
    public void testJpqlNoFetch() {
        final EntityManager em = createEntityManager(PU_NAME);
        try {
            final Query query = em.createNamedQuery(
                    "RegisteredCustomer.selecByName",
                    RegisteredCustomer.class).setParameter("name", CUSTOMER_NAME);
            final RegisteredCustomer customer = (RegisteredCustomer) query.getSingleResult();
            assertCustomer(customer);
        } finally {
            em.close();
        }
    }

    /**
     * Test JPQL query on {@link RegisteredCustomer} with {@code LEFT JOIN FETCH} of {@link CustomerAddress} entities.
     */
    public void testJpqlFetch() {
        final EntityManager em = createEntityManager(PU_NAME);
        try {
            final Query query = em.createNamedQuery(
                    "RegisteredCustomer.selecByNameFetch",
                    RegisteredCustomer.class).setParameter("name", CUSTOMER_NAME);
            final RegisteredCustomer customer = (RegisteredCustomer) query.getSingleResult();
            assertCustomer(customer);
        } finally {
            em.close();
        }
    }

    /**
     * Test criteria query on {@link RegisteredCustomer} without {@code LEFT JOIN FETCH} of {@link CustomerAddress}
     * entities.
     */
    public void testCriteriaNoFetch() {
        final EntityManager em = createEntityManager(PU_NAME);
        try {
            final CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            final CriteriaQuery<RegisteredCustomer> criteriaQuery
                    = criteriaBuilder.createQuery(RegisteredCustomer.class);
            final Root<RegisteredCustomer> from = criteriaQuery.from(RegisteredCustomer.class);

            criteriaQuery.where(criteriaBuilder.equal(from.get("name"), CUSTOMER_NAME));
            criteriaQuery.distinct(true);

            final TypedQuery<RegisteredCustomer> query = em.createQuery(criteriaQuery);
            final RegisteredCustomer customer = query.getSingleResult();
            assertCustomer(customer);
        } finally {
            em.close();
        }
    }

    /**
     * Test criteria query on {@link RegisteredCustomer} with {@code LEFT JOIN FETCH} of {@link CustomerAddress}
     * entities.
     */
    public void testCriteriaFetch() {
        final EntityManager em = createEntityManager(PU_NAME);
        try {
            final CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            final CriteriaQuery<RegisteredCustomer> criteriaQuery
                    = criteriaBuilder.createQuery(RegisteredCustomer.class);
            final Root<RegisteredCustomer> from = criteriaQuery.from(RegisteredCustomer.class);

            from.fetch("billingAddress", JoinType.LEFT);
            from.fetch("deliveryAddress", JoinType.LEFT);

            criteriaQuery.where(criteriaBuilder.equal(from.get("name"), CUSTOMER_NAME));
            criteriaQuery.distinct(true);

            final TypedQuery<RegisteredCustomer> query = em.createQuery(criteriaQuery);
            final RegisteredCustomer customer = query.getSingleResult();
            assertCustomer(customer);
        } finally {
            em.close();
        }
    }

}
