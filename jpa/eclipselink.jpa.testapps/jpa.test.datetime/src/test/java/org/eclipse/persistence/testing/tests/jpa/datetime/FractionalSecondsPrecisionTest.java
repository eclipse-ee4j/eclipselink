/*
 * Copyright (c) 2025 Oracle and/or its affiliates.
 * Copyright (c) 2025 IBM Corporation. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eclipse.persistence.testing.tests.jpa.datetime;

import java.time.LocalDateTime;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.datetime.Event;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test fractional seconds precision in time SQL types.
 */
public class FractionalSecondsPrecisionTest {

    /** Name of persistence unit used in test. */
    private static final String PU_NAME = "fractional";

    /**
     * List of {@code Event}s. Array index matches ID.
     */
    public static final Event[] EVENTS = new Event[] {
            null, // Skip index 0
            new Event(1L,
                      LocalDateTime.of(2025, 6, 11, 12, 0, 0, 123_451_234)),
    };

    /** Entity manager factory. */
    private static EntityManagerFactory EMF;

    /** Database platform. */
    private static DatabasePlatform DBP;

    /**
     * Initialize test static content.
     */
    @BeforeClass
    public static void setupClass() {
        EMF = JUnitTestCase.getEntityManagerFactory(PU_NAME);
        DBP = EMF.unwrap(ServerSession.class).getPlatform();
    }

    /**
     * Destroy test static content.
     */
    @AfterClass
    public static void  cleanupClass() {
        if (EMF != null) {
            EMF.close();
            EMF = null;
        }
        DBP = null;
    }

    // Test that LocalDateTime returned from the database has precision set in @Column annotation.
    // Event column timestamp has secondPrecision set to 5.
    @Test
    public void testLocalDateTimeCustomPrecision() {
        // This test makes sense only when platform supports seconds precision in time SQL types
        if (DBP.supportsFractionalTime()) {
            try (EntityManager em = EMF.createEntityManager()) {
                EntityTransaction t = em.getTransaction();
                t.begin();
                try {
                    em.persist(EVENTS[1]);
                    t.commit();
                } catch (Exception e) {
                    t.rollback();
                }
            }
            EMF.getCache().evictAll();
            try (EntityManager em = EMF.createEntityManager()) {
                Event event = em.createQuery("SELECT e FROM Event e WHERE e.id = :id", Event.class)
                        .setParameter("id", 1L)
                        .getSingleResult();
                assertEquals(123_450_000, event.getTimestamp().getNano());
            }
        }
    }

}
