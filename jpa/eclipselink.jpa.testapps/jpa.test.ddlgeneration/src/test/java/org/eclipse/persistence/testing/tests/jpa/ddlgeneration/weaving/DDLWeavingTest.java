/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.testing.tests.jpa.ddlgeneration.weaving;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.weaving.Equipment;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.weaving.Port;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.weaving.impl.EquipmentDAO;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.weaving.impl.PortDAO;
import org.eclipse.persistence.testing.tests.jpa.ddlgeneration.DDLGenerationTest;

public class DDLWeavingTest extends DDLGenerationTest {

    public DDLWeavingTest() {
    }

    public DDLWeavingTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("DDLWeavingTest");
        suite.addTest(new DDLWeavingTest("testSetup"));
        suite.addTest(new DDLWeavingTest("testCascadeMergeOnManagedEntityWithOrderedList"));
        return suite;
    }

    public void testCascadeMergeOnManagedEntityWithOrderedList() {
        EntityManagerFactory factory = getEntityManagerFactory();

        // Clean up first
        cleanupEquipmentAndPorts(factory);

        // Create a piece equipment with one port.
        createEquipment(factory);

        // Add two ports to the equipment
        addPorts(factory);

        // Fetch the equipment and validate there is no null elements in
        // the ArrayList of Port.
        verifyPorts(factory);
    }

    protected void cleanupEquipmentAndPorts(EntityManagerFactory factory) {
        EntityManager em = null;

        try {
            em = factory.createEntityManager();
            beginTransaction(em);

            em.createQuery("DELETE FROM PortDAO").executeUpdate();
            em.createQuery("DELETE FROM EquipmentDAO").executeUpdate();

            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            throw e;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    protected void createEquipment(EntityManagerFactory factory) {
        EntityManager em = null;

        try {
            em = factory.createEntityManager();

            beginTransaction(em);

            Equipment eq = new EquipmentDAO();
            eq.setId("eq");

            Port port = new PortDAO();
            port.setId("p1");
            port.setPortOrder(0);

            eq.addPort(port);

            em.persist(eq);
            commitTransaction(em);
        } catch (Exception e) {
            if (em != null && isTransactionActive(em)) {
                rollbackTransaction(em);
            }

            fail("En error occurred creating new equipment: " + e.getMessage());
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    protected void addPorts(EntityManagerFactory factory) {
        EntityManager em = null;

        try {
            em = factory.createEntityManager();
            beginTransaction(em);
            Query query = em.createNamedQuery("Equipment.findEquipmentById");
            query.setParameter("id", "eq");
            Equipment eq = (Equipment) query.getResultList().get(0);
            commitTransaction(em);

            em = factory.createEntityManager();
            beginTransaction(em);
            eq = em.merge(eq);

            Port port = new PortDAO();
            port.setId("p2");
            port.setPortOrder(1);
            eq.addPort(port);

            port = new PortDAO();
            port.setId("p3");
            port.setPortOrder(2);
            eq.addPort(port);

            eq = em.merge(eq);
            commitTransaction(em);
        } catch (Exception e) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }

            fail("En error occurred adding new ports: " + e.getMessage());
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    protected void verifyPorts(EntityManagerFactory factory) {
        EntityManager em = null;

        try {
            em = factory.createEntityManager();
            beginTransaction(em);
            Query query = em.createNamedQuery("Equipment.findEquipmentById");
            query.setParameter("id", "eq");
            Equipment eq = (Equipment) query.getResultList().get(0);
            commitTransaction(em);

            for (Port port: eq.getPorts()) {
                if (port == null) {
                    fail("A null PORT was found in the collection of ports.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }

            fail("En error occurred fetching the results to verify: " + e.getMessage());
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
}
