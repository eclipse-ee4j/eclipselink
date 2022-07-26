/*
 * Copyright (c) 2021, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.jpa.test.mapping;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.mapping.model.ChildMultitenant;
import org.eclipse.persistence.jpa.test.mapping.model.ParentMultitenant;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Test for the fix issue #1161
 * Multitenancy schema discriminator with OneToMany relationships - Wrong tenant reference leading to QueryException
 */
@RunWith(EmfRunner.class)
public class TestMultitenantOneToMany {

    @Emf(
            createTables = DDLGen.NONE,
            classes = {ChildMultitenant.class, ParentMultitenant.class}
    )
    private EntityManagerFactory emf;

    private boolean supportedPlatform = false;

    @Before
    public void setup() {
        EntityManager em = emf.createEntityManager();
        //MySQL only due permissions for CREATE SCHEMA command.
        if (((EntityManagerImpl)em).getDatabaseSession().getPlatform().isMySQL()) {
            supportedPlatform = true;
            try {
                em.getTransaction().begin();
                em.createNativeQuery("DROP SCHEMA IF EXISTS tenant_1").executeUpdate();
                em.createNativeQuery("DROP SCHEMA IF EXISTS tenant_2").executeUpdate();
                em.createNativeQuery("CREATE SCHEMA tenant_1").executeUpdate();
                em.createNativeQuery("CREATE SCHEMA tenant_2").executeUpdate();
                em.createNativeQuery("CREATE TABLE tenant_1.parent(id bigint primary key)").executeUpdate();
                em.createNativeQuery("CREATE TABLE tenant_2.parent(id bigint primary key)").executeUpdate();
                em.createNativeQuery("CREATE TABLE tenant_1.children(id bigint NOT NULL, parent_id bigint, PRIMARY KEY " +
                                "(id), CONSTRAINT parent_fkey FOREIGN KEY (parent_id) REFERENCES tenant_1.parent (id))")
                        .executeUpdate();
                em.createNativeQuery("CREATE TABLE tenant_2.children(id bigint NOT NULL, parent_id bigint, PRIMARY KEY " +
                                "(id), CONSTRAINT parent_fkey FOREIGN KEY (parent_id) REFERENCES tenant_2.parent (id))")
                        .executeUpdate();
                em.createNativeQuery("INSERT INTO tenant_1.parent(id) VALUES(1)").executeUpdate();
                em.createNativeQuery("INSERT INTO tenant_2.parent(id) VALUES(2)").executeUpdate();
                em.createNativeQuery("INSERT INTO tenant_1.children(id, parent_id) VALUES(10, 1)").executeUpdate();
                em.createNativeQuery("INSERT INTO tenant_2.children(id, parent_id) VALUES(11, 2)").executeUpdate();
                em.getTransaction().commit();
            } finally {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                if (em.isOpen()) {
                    em.close();
                }
            }
        }
    }

    @Test
    public void testMultitenancySchemaDescriminatorWithOneToMany() {
        if (supportedPlatform) {
            boolean awaitTermination = false;
            List<Future<ParentMultitenant>> parent1Results = new ArrayList<>();
            List<Future<ParentMultitenant>> parent2Results = new ArrayList<>();
            try {
                ExecutorService es = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
                for (int i = 1; i <= 10000; i++) {
                    parent1Results.add(es.submit(() -> load("tenant_1", 1L)));
                    parent2Results.add(es.submit(() -> load("tenant_2", 2L)));
                }
                es.shutdown();
                awaitTermination = es.awaitTermination(10, TimeUnit.MINUTES);
                for (Future<ParentMultitenant> parentFuture : parent1Results) {
                    ParentMultitenant parent = parentFuture.get();
                    assertEquals(1L, (long) parent.getId());
                    assertEquals(10L, (long) parent.getChildren().get(0).getId());
                }
                for (Future<ParentMultitenant> parentFuture : parent2Results) {
                    ParentMultitenant parent = parentFuture.get();
                    assertEquals(2L, (long) parent.getId());
                    assertEquals(11L, (long) parent.getChildren().get(0).getId());
                }
            } catch (Exception e) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                fail("Exception was caught: " + sw.toString());
            }
            if (!awaitTermination) {
                fail("timeout elapsed before termination of the threads");
            }
        }
    }

    private ParentMultitenant load(String tenant, Long id) {
        HashMap<String, String> properties = new HashMap<>();
        properties.put(PersistenceUnitProperties.MULTITENANT_PROPERTY_DEFAULT, tenant);
        EntityManager em;
        if (!properties.isEmpty()) {
            em = emf.createEntityManager(properties);
        } else {
            em = emf.createEntityManager();
        }
        ParentMultitenant parent = em.find(ParentMultitenant.class, id);
        List<ChildMultitenant> children = parent.getChildren();
        em.close();
        return parent;

    }

}
