/*
 * Copyright (c) 2005, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2005, 2015 SAP. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     SAP - initial API and implementation

package org.eclipse.persistence.testing.tests.wdf.jpa1.generator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import junit.framework.Assert;

import org.eclipse.persistence.platform.database.DerbyPlatform;
import org.eclipse.persistence.platform.database.MySQLPlatform;
import org.eclipse.persistence.platform.database.SQLServerPlatform;
import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.framework.wdf.ServerInfoHolder;
import org.eclipse.persistence.testing.framework.wdf.Skip;
import org.eclipse.persistence.testing.models.wdf.jpa1.fancy.Animal;
import org.eclipse.persistence.testing.models.wdf.jpa1.fancy.Element;
import org.eclipse.persistence.testing.models.wdf.jpa1.fancy.Plant;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.junit.Test;

public class TestSequence extends JPA1Base {

    @Test
    @Skip(databaseNames = "org.eclipse.persistence.platform.database.MySQLPlatform", databases= {SQLServerPlatform.class, DerbyPlatform.class})
    public void testPersist() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            em.getTransaction().begin();
            Animal zebra = new Animal("zebra");
            em.persist(zebra);
            Assert.assertEquals("wrong id", Integer.valueOf(1), zebra.getId());
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    @Test
    @Skip(databases = {MySQLPlatform.class, SQLServerPlatform.class, DerbyPlatform.class})
    public void testPersistFlock() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            em.getTransaction().begin();
            final Animal firstDove = new Animal("dove ");
            em.persist(firstDove);
            final int offset = firstDove.getId().intValue();
            for (int i = 1; i < 100; i++) {
                final Animal dove = new Animal("dove " + i);
                em.persist(dove);
                Assert.assertEquals("wrong id", Integer.valueOf(offset + i), dove.getId());
            }
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    @Test
    @Skip(databases = {MySQLPlatform.class, SQLServerPlatform.class, DerbyPlatform.class})
    public void testAllocSize() {
        if (ServerInfoHolder.isOnServer()) {
            // skip the test
            return;
        }

        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();

        EntityManagerFactory emf2 = Persistence.createEntityManagerFactory("jpa1testmodel-2", EMF_PROPERTIES);
        EntityManager em2 = emf2.createEntityManager();

        try {
            em.getTransaction().begin();
            em2.getTransaction().begin();

            final Plant tree = new Plant("tree");
            final Element water = new Element("water");
            em.persist(tree); // id 1
            em2.persist(water); // id 4
            Assert.assertEquals("wrong allocation", ((int) tree.getId() + 3), ((int) water.getId()));
            final Plant flower = new Plant("flower");
            final Plant grass = new Plant("grass");
            final Plant bush = new Plant("bush");
            em.persist(flower); // id 2
            em.persist(grass); // id 3
            em.persist(bush); // id 7
            Assert.assertEquals("wrong allocation", ((int) water.getId() + 3), ((int) bush.getId()));
            em.getTransaction().commit();
            em2.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
            if (em2.getTransaction().isActive()) {
                em2.getTransaction().rollback();
            }
            em2.close();
            emf2.close();
        }
    }

}
