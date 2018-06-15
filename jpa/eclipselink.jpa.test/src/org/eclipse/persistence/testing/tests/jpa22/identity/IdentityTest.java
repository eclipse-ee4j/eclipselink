/*
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     09/29/2016-2.7 Tomas Kraus
//       - 426852: @GeneratedValue(strategy=GenerationType.IDENTITY) support in Oracle 12c
package org.eclipse.persistence.testing.tests.jpa22.identity;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;

import org.eclipse.persistence.internal.databaseaccess.DatasourcePlatform;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa22.identity.Person;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test proper identity column value generation.
 */
public class IdentityTest {

    /** Name of persistence unit used in test. */
    private static final String PU_NAME = "identity-pu";

    /** Entity manager factory. */
    private static EntityManagerFactory EMF;

    /** Database platform. */
    private static DatabasePlatform DBP;

    /** Entity manager. */
    private EntityManager em;

    /**
     * Initialize test static content.
     */
    @BeforeClass
    public static void setupClass() {
        EMF = JUnitTestCase.getEntityManagerFactory(PU_NAME);
        DBP = EMF.<ServerSession>unwrap(ServerSession.class).getPlatform();
    }

    /**
     * Destroy test static content.
     */
    @AfterClass
    public static void  cleanupClass() {
        EMF = null;
        DBP = null;
    }

    /**
     * Initialize test environment.
     */
    @Before
    public void setup() {
        em = EMF.createEntityManager();
    }

    /**
     * Destroy test environment.
     */
    @After
    public void cleanup() {
        if (em != null) {
            em.close();
        }
    }

    /**
     * Test identity column value generation.
     */
    @Test
    public void testIdentity() {
        if (!DBP.supportsIdentity()) {
            return;
        }
        EntityTransaction t = em.getTransaction();
        final Person p1 = new Person("John", "Smith");
        final Person p2 = new Person("Bob", "Brown");
        t.begin();
        try {
            em.persist(p1);
            em.persist(p2);
            t.commit();
        } catch (PersistenceException | IllegalArgumentException ex) {
            if (t.isActive()) {
                t.rollback();
            }
            ex.printStackTrace();
            throw ex;
        }
        final Map<String, Person> pMap = new HashMap<>(2);
        pMap.put(p1.getSecondName(), p1);
        pMap.put(p2.getSecondName(), p2);
        final TypedQuery<Person> pQuery = em.createQuery("SELECT p FROM Person p", Person.class);
        final List<Person> pList = pQuery.getResultList();
        for (final Person p : pList) {
            final Person pV = pMap.get(p.getSecondName());
            assertEquals(p.getId(), pV.getId());
        }
    }

}
