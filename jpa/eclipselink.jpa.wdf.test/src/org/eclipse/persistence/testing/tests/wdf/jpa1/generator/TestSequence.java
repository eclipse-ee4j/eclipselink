/*******************************************************************************
 * Copyright (c) 2005, 2009 SAP. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     SAP - initial API and implementation
 ******************************************************************************/

package org.eclipse.persistence.testing.tests.wdf.jpa1.generator;

import javax.persistence.EntityManager;

import junit.framework.Assert;

import org.eclipse.persistence.platform.database.MySQLPlatform;
import org.eclipse.persistence.platform.database.OraclePlatform;
import org.eclipse.persistence.platform.database.SQLServerPlatform;
import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.framework.wdf.Skip;
import org.eclipse.persistence.testing.framework.wdf.ToBeInvestigated;
import org.eclipse.persistence.testing.models.wdf.jpa1.fancy.Animal;
import org.eclipse.persistence.testing.models.wdf.jpa1.fancy.Element;
import org.eclipse.persistence.testing.models.wdf.jpa1.fancy.Plant;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.junit.Test;

public class TestSequence extends JPA1Base {

    @Test
    @Skip(databaseNames = "org.eclipse.persistence.platform.database.MySQLPlatform", databases=SQLServerPlatform.class)
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
    @Skip(databases = {MySQLPlatform.class, SQLServerPlatform.class})
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
    @Skip(databases = {MySQLPlatform.class, SQLServerPlatform.class})
    @ToBeInvestigated(databases = OraclePlatform.class, databaseNames = "org.eclipse.persistence.platform.database.MaxDBPlatform")
    // adjust test
    public void testAllocSize() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            em.getTransaction().begin();
            final Plant tree = new Plant("tree");
            final Element water = new Element("water");
            em.persist(tree); // id 1
            em.persist(water); // id 4
            Assert.assertEquals("wrong allocation", ((int) tree.getId() + 3), ((int) water.getId()));
            final Plant flower = new Plant("flower");
            final Plant grass = new Plant("grass");
            final Plant bush = new Plant("bush");
            em.persist(flower); // id 2
            em.persist(grass); // id 3
            em.persist(bush); // id 7
            Assert.assertEquals("wrong allocation", ((int) water.getId() + 3), ((int) bush.getId()));
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }
}
