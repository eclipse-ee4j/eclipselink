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
package org.eclipse.persistence.testing.tests.jpa.ddlgeneration;

import java.time.LocalDateTime;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.internal.databaseaccess.Platform;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.Event;

public class FractionalSecondsPrecisionTest extends JUnitTestCase {

    /** Default persistence unit name. */
    private static final String PU_NAME = "fractional";

    /**
     * List of {@code Event}s. Array index matches ID.
     */
    private static final Event[] EVENTS = new Event[] {
            null, // Skip index 0
            new Event(1L,
                      LocalDateTime.of(2025, 6, 11, 12, 0, 0, 123_451_234)),
    };

    public FractionalSecondsPrecisionTest() {
        super();
    }

    public FractionalSecondsPrecisionTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Null Binding DateTime");
        suite.addTest(new FractionalSecondsPrecisionTest("testSetup"));

        return suite;
    }

    @Override
    public String getPersistenceUnitName() {
        return PU_NAME;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow
     * execution in the server.
     */
    public void testSetup() {

    }

    // Test that LocalDateTime returned from the database has precision set in @Column annotation.
    // Event column timestamp has secondPrecision set to 5.
    public void testLocalDateTimeCustomPrecision() {
        // This test makes sense only when platform supports seconds precision in time SQL types
        if (supportsFractionalTime()) {
            try (EntityManager em = createEntityManager()) {
                EntityTransaction t = em.getTransaction();
                t.begin();
                try {
                    em.persist(EVENTS[1]);
                    t.commit();
                } catch (Exception e) {
                    t.rollback();
                }
            }
            getEntityManagerFactory().getCache().evictAll();
            try (EntityManager em = createEntityManager()) {
                Event event = em.createQuery("SELECT e FROM Event e WHERE e.id = :id", Event.class)
                        .setParameter("id", 1L)
                        .getSingleResult();
                assertEquals(123_450_000, event.getTimestamp().getNano());
            }
        }
    }

    // JUnitTestCase returns common Platform so additional check is required to get
    // fractional seconds precision of DatabasePlatform.
    private boolean supportsFractionalTime() {
        Platform platform = getPlatform();
        if (platform instanceof DatabasePlatform databasePlatform) {
            return databasePlatform.supportsFractionalTime();
        }
        return false;
    }

}
