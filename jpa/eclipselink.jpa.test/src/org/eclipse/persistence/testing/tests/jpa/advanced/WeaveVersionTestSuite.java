/*
 * Copyright (c) 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.testing.tests.jpa.advanced;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.weave.IsolatedEntity;
import org.eclipse.persistence.testing.models.jpa.weave.Location;
import org.eclipse.persistence.testing.models.jpa.weave.Node;
import org.eclipse.persistence.testing.models.jpa.weave.Order;

public class WeaveVersionTestSuite extends JUnitTestCase {

    private static final String NODE_01 = "NODE_01";
    private static final String LOCATION_01 = "LOCATION_01";
    private static final String PROCESS_01 = "PROCESS_01";

    public WeaveVersionTestSuite() {
    }

    public WeaveVersionTestSuite(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
    }

    @Override
    public String getPersistenceUnitName() {
        return "pu-with-dynamic-weaving";
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("WeaveVersionTestSuite");
        suite.addTest(new WeaveVersionTestSuite("testWeavedEntitiesWithVersionL2Cache"));
        return suite;
    }

    @Override
    public void setUp() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Node node = new Node(NODE_01);
            em.persist(node);
            Location destinationLocation = new Location(1L, LOCATION_01);
            destinationLocation.setNode(node);
            em.persist(destinationLocation);
            commitTransaction(em);
        } finally {
            if (this.isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    //Sequence of steps which triggered org.eclipse.persistence.exceptions.OptimisticLockException before related fix was applied.
    //Required conditions are: JPA L2 cache enabled, Weaving enabled and @Version field annotation is used
    public void testWeavedEntitiesWithVersionL2Cache() {
        EntityManagerFactory emf = getEntityManagerFactory();
        cleanup();
        emf.getCache().evictAll();

        //Step 01
        {
            final long toKey = 1L;

            EntityManager em = createEntityManager();
            beginTransaction(em);
            try {
                Order order = new Order(toKey);
                em.persist(order);
                em.flush();
                Location location = em
                        .createQuery("SELECT l FROM Location l WHERE l.locationId=:locationId", Location.class)
                        .setParameter("locationId", LOCATION_01)
                        .getSingleResult();
                order.setNode(location.getNode());
                commitTransaction(em);
            } finally {
                if (this.isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                closeEntityManager(em);
            }
        }

        //Step 02
        {
            final long toKey = 1L;

            EntityManager em = createEntityManager();
            beginTransaction(em);
            try {
                IsolatedEntity serialProcess = new IsolatedEntity(PROCESS_01);
                em.persist(serialProcess);
                em.flush();
                Order order = em.find(Order.class, toKey);
                order.getNode().reserveBufferCapacity();
                em.merge(order);
                em.merge(order.getNode());
                commitTransaction(em);
            } finally {
                if (this.isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                closeEntityManager(em);
            }

        }
        cleanup();
        emf.getCache().evictAll();

        //Step 03
        {
            final long toKey = 2L;

            EntityManager em = createEntityManager();
            beginTransaction(em);
            try {
                Order order = new Order(toKey);
                em.persist(order);
                em.flush();
                Location destinationLocation = em
                        .createQuery("SELECT l FROM Location l WHERE l.locationId=:locationId", Location.class)
                        .setParameter("locationId", LOCATION_01)
                        .getSingleResult();
                order.setNode(destinationLocation.getNode());
                commitTransaction(em);
            } finally {
                if (this.isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                closeEntityManager(em);
            }
        }

        //Step 04
        {
            final long toKey = 2L;

            EntityManager em = createEntityManager();
            beginTransaction(em);
            try {
                IsolatedEntity serialProcess = new IsolatedEntity(PROCESS_01);
                em.persist(serialProcess);
                em.flush();
                Order order = em.find(Order.class, toKey);
                order.getNode().reserveBufferCapacity();
                em.merge(order);
                em.merge(order.getNode());
            } finally {
                if (this.isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                closeEntityManager(em);
            }
        }
    }

    public void cleanup() {
        EntityManager em = createEntityManager();
        em.getTransaction().begin();
        try {
            em.createQuery("delete from IsolatedEntity s").executeUpdate();
            em.createQuery("delete from Order t").executeUpdate();
            em.createQuery("UPDATE Node tn SET tn.availableBufferCapacity =10").executeUpdate();
            commitTransaction(em);
        } finally {
            if (this.isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    @Override
    public void tearDown() {
        EntityManager em = createEntityManager();
        em.getTransaction().begin();
        try {
            em.createQuery("delete from Location l").executeUpdate();
            em.createQuery("delete from Order t").executeUpdate();
            em.createQuery("delete from Node t").executeUpdate();
            em.createQuery("delete from IsolatedEntity s").executeUpdate();
            commitTransaction(em);
        } finally {
            if (this.isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }
}
