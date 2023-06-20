/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
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
//     02/01/2022: Tomas Kraus
//       - Issue 1442: Implement New Jakarta Persistence 3.1 Features
package org.eclipse.persistence.jpa.test.query;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import org.eclipse.persistence.jpa.test.query.model.StringEntity;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test math functions in CriteriaBuilder.
 * Added to Jakarta Persistence API as PR #351
 */
@RunWith(EmfRunner.class)
public class TestQuerySyntaxStringTests {

    @Emf(createTables = DDLGen.DROP_CREATE,
            classes = {
                    StringEntity.class
            },
            properties = {
                    @Property(name = "eclipselink.cache.shared.default", value = "false"),
                    // This property remaps String from VARCHAR->NVARCHAR(or db equivalent)
                    @Property(name = "eclipselink.target-database-properties",
                            value = "UseNationalCharacterVaryingTypeForString=true"),
            })
    private EntityManagerFactory emf;

    private final StringEntity[] INITIAL_DATA = {
            new StringEntity(1, "John", "Smith"),
    };

    @Before
    public void setup() {
        final EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            for (StringEntity e : INITIAL_DATA) {
                em.persist(e);
            }
            em.flush();
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    @After
    public void cleanup() {
        final EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM StringEntity e").executeUpdate();
            em.flush();
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    @Test
    public void testString_concatOperatorSelect01() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();

        try {
            TypedQuery<String> query = em.createQuery("SELECT s.firstName || s.lastName FROM StringEntity s WHERE s.id = 1", String.class);
            String result = query.getSingleResult();
            Assert.assertEquals("JohnSmith", result);
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }
    }

    @Test
    public void testString_concatOperatorSelect02() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();

        try {
            TypedQuery<String> query = em.createQuery("SELECT s.firstName || s.lastName || s.firstName || s.lastName FROM StringEntity s WHERE s.id = 1", String.class);
            String result = query.getSingleResult();
            Assert.assertEquals("JohnSmithJohnSmith", result);
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }
    }

    @Test
    public void testString_concatOperatorSelect03() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();

        try {
            TypedQuery<String> query = em.createQuery("SELECT s.firstName || (s.lastName || s.firstName) || s.lastName FROM StringEntity s WHERE s.id = 1", String.class);
            String result = query.getSingleResult();
            Assert.assertEquals("JohnSmithJohnSmith", result);
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }
    }

    @Test
    public void testString_concatOperatorSelect04() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();

        try {
            TypedQuery<String> query = em.createQuery("SELECT s.firstName || CAST(s.id AS CHAR(1)) FROM StringEntity s WHERE s.id = 1", String.class);
            String result = query.getSingleResult();
            Assert.assertEquals("John1", result);
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }
    }

    @Test
    public void testString_concatOperatorWhere01() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();

        try {
            TypedQuery<String> query = em.createQuery("SELECT s.firstName || s.lastName FROM StringEntity s WHERE s.firstName || s.lastName = 'JohnSmith'", String.class);
            String result = query.getSingleResult();
            Assert.assertEquals("JohnSmith", result);
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }
    }

    @Test
    public void testString_concatOperatorWhere02() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();

        try {
            TypedQuery<String> query = em.createQuery("SELECT s.firstName || s.lastName FROM StringEntity s WHERE s.firstName || s.lastName || s.firstName = 'JohnSmithJohn'", String.class);
            String result = query.getSingleResult();
            Assert.assertEquals("JohnSmith", result);
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }
    }
}
