/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2015 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     02/19/2015 - Rick Curtis
//       - 458877 : Add national character support
package org.eclipse.persistence.jpa.test.basic;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.jpa.test.basic.model.NvarcharEntity;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestNVarChar {

    @Emf(createTables = DDLGen.DROP_CREATE, classes = { NvarcharEntity.class }, properties = {
            @Property(name = "eclipselink.cache.shared.default", value = "false"),
            // This property remaps String from VARCHAR->NVARCHAR(or db equivalent)
            @Property(name = "eclipselink.target-database-properties",
                    value = "UseNationalCharacterVaryingTypeForString=true"), })
    private EntityManagerFactory emf;

    final String latinChars = new String("\u0274\u0275");

    @Test
    public void testPersist() {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            NvarcharEntity e = new NvarcharEntity(latinChars);
            em.persist(e);
            em.getTransaction().commit();

            String payload = em.createQuery("SELECT n.dataField FROM NvarcharEntity n WHERE n.id=:id", String.class)
                    .setParameter("id", e.getId()).getSingleResult();
            Assert.assertEquals(latinChars, payload);

        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    @Test
    public void testNoUnicodePersist() {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            NvarcharEntity e = new NvarcharEntity("basic");
            em.persist(e);
            em.getTransaction().commit();

            String payload = em.createQuery("SELECT n.dataField FROM NvarcharEntity n WHERE n.id=:id", String.class)
                    .setParameter("id", e.getId()).getSingleResult();
            Assert.assertEquals("basic", payload);

        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    @Test
    public void testJPQL() {
        // Latin characters
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            NvarcharEntity e = new NvarcharEntity(latinChars);
            em.persist(e);
            em.getTransaction().commit();

            Long count = em.createQuery("SELECT COUNT(n) FROM NvarcharEntity n WHERE n.dataField=:data", Long.class)
                    .setParameter("data", latinChars).getSingleResult();
            Assert.assertTrue(count.longValue() > 0);

        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    @Test
    public void testJPQLNoLiteral() {
        // Latin characters
        String str = new String("\u0274\u0275");
        EntityManager em = emf.createEntityManager();
        AbstractSession as = em.unwrap(AbstractSession.class);
        DatabasePlatform db = as.getPlatform();

        db.setShouldBindAllParameters(false);
        try {
            em.getTransaction().begin();
            NvarcharEntity e = new NvarcharEntity(str);
            em.persist(e);
            em.getTransaction().commit();

            Long count = em.createQuery("SELECT count(n) FROM NvarcharEntity n WHERE n.dataField=\"" + str + "\"", Long.class).getSingleResult();
            Assert.assertTrue(count.longValue() >= 1);

        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }
}
