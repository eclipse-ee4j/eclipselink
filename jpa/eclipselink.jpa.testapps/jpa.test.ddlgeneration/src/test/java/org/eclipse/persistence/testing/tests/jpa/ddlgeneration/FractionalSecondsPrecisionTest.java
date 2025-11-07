/*
 * Copyright (c) 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.testing.tests.jpa.ddlgeneration;

import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.internal.databaseaccess.Platform;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.DateTimeEntity;

import junit.framework.Test;
import junit.framework.TestSuite;

public class FractionalSecondsPrecisionTest extends JUnitTestCase {

    /** Default persistence unit name. */
    private static final String PU_NAME = "fractional";

    /**
     * List of {@link DateTimeEntity}. Array index matches ID.
     */
    private static final DateTimeEntity[] DATE_TIME_ENTITIES = new DateTimeEntity[] {
            null, // Skip index 0
            new DateTimeEntity(1L,
                               LocalDateTime.of(1967, 3, 18, 10, 53, 21, 12345_1234),
                               null,
                               null,
                               null),
            new DateTimeEntity(2L,
                               null,
                               LocalTime.of(9, 38, 42, 1234_12345),
                               null,
                               null)
    };

    public FractionalSecondsPrecisionTest() {
        super();
    }

    public FractionalSecondsPrecisionTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("FractionalSecondsPrecisionTest");
        suite.addTest(new FractionalSecondsPrecisionTest("testLocalDateTimeCustomPrecision"));
        suite.addTest(new FractionalSecondsPrecisionTest("testLocalTimeCustomPrecision"));
        suite.addTest(new FractionalSecondsPrecisionTest("testCleanup"));
        return suite;
    }

    @Override
    public String getPersistenceUnitName() {
        return PU_NAME;
    }

    // Test that LocalDateTime returned from the database has precision set in @Column annotation.
    // Event column timestamp has secondPrecision set to 5.
    public void testLocalDateTimeCustomPrecision() {
        // This test makes sense only when platform supports seconds precision in time SQL type TIMESTAMP
        if (!supportsFractionalTime()) {
            return;
        }

        try (EntityManager em = createEntityManager()) {
            beginTransaction(em);
            try {
                em.persist(DATE_TIME_ENTITIES[1]);
                commitTransaction(em);
            } catch (Exception e) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                throw e;
            }
        }

        getEntityManagerFactory().getCache().evictAll();

        try (EntityManager em = createEntityManager()) {
            DateTimeEntity dateTimeEntity =
                em.createQuery("SELECT e FROM DateTimeEntity e WHERE e.id = :id", DateTimeEntity.class)
                  .setParameter("id", 1L)
                  .getSingleResult();

            assertEquals(12345_0000, dateTimeEntity.getLocalDateTime().getNano());
        }

    }

    // Test that LocalTime returned from the database has precision set in @Column annotation.
    // Event column timestamp has secondPrecision set to 5.
    public void testLocalTimeCustomPrecision() {
        // This test makes sense only when platform supports seconds precision in time SQL type TIME
        if (!supportsFractionalTime() || getPlatform().isDB2()) {
            // DB2 is a special case. It supports FractionalTime, but only for TIMESTAMP, not for TIME
            return;
        }

        try (EntityManager em = createEntityManager()) {
            beginTransaction(em);
            try {
                em.persist(DATE_TIME_ENTITIES[2]);
                commitTransaction(em);
            } catch (Exception e) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                throw e;
            }
        }

        getEntityManagerFactory().getCache().evictAll();

        try (EntityManager em = createEntityManager()) {
            DateTimeEntity dateTimeEntity =
                em.createQuery("SELECT e FROM DateTimeEntity e WHERE e.id = :id", DateTimeEntity.class)
                  .setParameter("id", 2L)
                  .getSingleResult();

            assertEquals(1234_00000, dateTimeEntity.getLocalTime().getNano());
        }
    }

    // Cleanup DateTimeEntity related database content
    public void testCleanup() {
        try (EntityManager em = createEntityManager()) {
            beginTransaction(em);
            try {
                em.createQuery("DELETE FROM DateTimeEntity")
                        .executeUpdate();
                commitTransaction(em);
            } catch (Exception e) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                throw e;
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
