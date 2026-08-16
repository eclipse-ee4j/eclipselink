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

package org.eclipse.persistence.jpa.test.record;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.record.model.UserAccount;
import org.eclipse.persistence.jpa.test.record.model.UserId;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for a {@code java.lang.Record} used as a JPA {@code @IdClass}.
 */
@RunWith(EmfRunner.class)
public class TestRecordIdClassExample {

    @Emf(createTables = DDLGen.DROP_CREATE, classes = { UserAccount.class })
    private EntityManagerFactory emf;

    @Test
    public void testPersistAndFind() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            UserAccount user = new UserAccount("test@example.com", "Test User");
            em.persist(user);
            em.getTransaction().commit();
            em.clear();

            UserId userId = new UserId(user.getId(), user.getEmail());
            UserAccount found = em.find(UserAccount.class, userId);
            assertNotNull("User should be found after persist", found);
            assertEquals("Test User", found.getName());
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    @Test
    public void testGetIdentifier() {
        UserAccount user = new UserAccount("test@example.com", "Test User");

        Object identifier = emf.getPersistenceUnitUtil().getIdentifier(user);

        assertNotNull("Identifier should not be null", identifier);
        assertTrue("Identifier should be a UserId", identifier instanceof UserId);
        UserId userId = (UserId) identifier;
        assertEquals(user.getId(), userId.id());
        assertEquals("test@example.com", userId.email());
    }
}
