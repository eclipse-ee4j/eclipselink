/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//      tware - initial
package org.eclipse.persistence.jpars.test.crud;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.jpa.rs.PersistenceContext;
import org.eclipse.persistence.jpa.rs.PersistenceFactoryBase;
import org.eclipse.persistence.jpars.test.model.auction.StaticUser;
import org.eclipse.persistence.jpars.test.util.ExamplePropertiesLoader;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test basic CRUD operations on a dynamically created PersistenceContext
 * @author tware
 *
 */
public class StaticCrudTests {

    private static PersistenceContext persistenceContext;
    private static PersistenceFactoryBase factory;

    @BeforeClass
    public static void setup() {
        Map<String, Object> properties = new HashMap<>();
        ExamplePropertiesLoader.loadProperties(properties);
        properties.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, null);
        properties.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.DROP_AND_CREATE);
        properties.put(PersistenceUnitProperties.CLASSLOADER, new DynamicClassLoader(Thread.currentThread().getContextClassLoader()));
        factory = null;
        try {
            factory = new PersistenceFactoryBase();
            persistenceContext = factory.bootstrapPersistenceContext("jpars_auction-static-local", Persistence.createEntityManagerFactory("jpars_auction-static-local", properties), new URI(
                    "http://localhost:9090/JPA-RS/"), null, true);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
        EntityManager em = persistenceContext.getEmf().createEntityManager();
        em.getTransaction().begin();
        em.createQuery("delete from StaticBid b").executeUpdate();
        em.createQuery("delete from StaticAuction a").executeUpdate();
        em.createQuery("delete from StaticUser u").executeUpdate();
        em.getTransaction().commit();

    }

    @AfterClass
    public static void tearDown() {
        EntityManager em = persistenceContext.getEmf().createEntityManager();
        em.getTransaction().begin();
        em.createQuery("delete from StaticBid b").executeUpdate();
        em.createQuery("delete from StaticAuction a").executeUpdate();
        em.createQuery("delete from StaticUser u").executeUpdate();
        em.getTransaction().commit();
    }

    @Test
    public void testCreateAndDelete() throws Exception {
        StaticUser user = new StaticUser();
        user.setName("Jim");
        user.setId(1);
        persistenceContext.create(null, user);
        user = (StaticUser) persistenceContext.find("StaticUser", user.getId());

        assertNotNull("Entity was not persisted", user);
        assertTrue("Entity Name was incorrect", user.getName().equals("Jim"));

        persistenceContext.delete(null, "StaticUser", user.getId());

        user = (StaticUser) persistenceContext.find("StaticUser", user.getId());

        assertNull("Entity was not deleted", user);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testQuery() throws Exception {
        StaticUser user = new StaticUser();
        user.setName("Jill");
        user.setId(2);
        persistenceContext.create(null, user);

        user = new StaticUser();
        user.setName("Arthur");
        user.setId(3);
        persistenceContext.create(null, user);

        user = new StaticUser();
        user.setName("Judy");
        user.setId(4);
        persistenceContext.create(null, user);

        List<StaticUser> users = (List<StaticUser>) persistenceContext.queryMultipleResults(null, "User.all", null, null);
        assertTrue(users.size() == 3);
    }

    @Test
    public void testUpdate() throws Exception {
        StaticUser user = new StaticUser();
        user.setName("Tom");
        user.setId(5);
        persistenceContext.create(null, user);
        user = (StaticUser) persistenceContext.find("StaticUser", user.getId());
        user.setName("Thomas");
        persistenceContext.merge(null, user);
        user = (StaticUser) persistenceContext.find("StaticUser", user.getId());
        assertTrue("Entity name was not correctly updated.", user.getName().equals("Thomas"));
        persistenceContext.delete(null, "StaticUser", user.getId());
    }

}
