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
package org.eclipse.persistence.testing.tests.jpa.persistence32;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;

import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.testing.models.jpa.persistence32.InstantVersionEntity;
import org.eclipse.persistence.testing.models.jpa.persistence32.LocalDateTimeVersionEntity;
import org.eclipse.persistence.testing.models.jpa.persistence32.TimestampVersionEntity;

public abstract class AbstractVersionSuite extends AbstractSuite {

    // LdtVersionEntity instances
    static final LocalDateTimeVersionEntity[] LDT_ENTITIES = new LocalDateTimeVersionEntity[] {
            null, // Skip array index 0
            new LocalDateTimeVersionEntity(1, LocalDateTime.now(), "First"),
            new LocalDateTimeVersionEntity(2, LocalDateTime.now(), "Second"),
            new LocalDateTimeVersionEntity(3, LocalDateTime.now(), "Third")
    };

    // InstantVersionEntity
    static final InstantVersionEntity[] INSTANT_ENTITIES = new InstantVersionEntity[] {
            null, // Skip array index 0
            new InstantVersionEntity(1, Instant.now(), "First"),
            new InstantVersionEntity(2, Instant.now(), "Second"),
            new InstantVersionEntity(3, Instant.now(), "Third")
    };

    // TimestampVersionEntity
    static final TimestampVersionEntity[] TS_ENTITIES = new TimestampVersionEntity[] {
            null, // Skip array index 0
            new TimestampVersionEntity(1, Timestamp.from(Instant.now()), "First"),
            new TimestampVersionEntity(2, Timestamp.from(Instant.now()), "Second"),
            new TimestampVersionEntity(3, Timestamp.from(Instant.now()), "Third")
    };

    /**
     * Creates an instance of {@link AbstractPokemonSuite}.
     */
    public AbstractVersionSuite() {
        super();
    }

    /**
     * Creates an instance of {@link AbstractPokemonSuite} with custom test case name.
     *
     * @param name name of the test case
     */
    public AbstractVersionSuite(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
    }

    @Override
    public String getPersistenceUnitName() {
        return "persistence32_version";
    }


    // Initialize data
    @Override
    protected void suiteSetUp() {
        super.suiteSetUp();
        clearCache();
        emf.runInTransaction(em -> {
            for (int i = 1; i < LDT_ENTITIES.length; i++) {
                em.persist(LDT_ENTITIES[i]);
            }
            for (int i = 1; i < INSTANT_ENTITIES.length; i++) {
                em.persist(INSTANT_ENTITIES[i]);
            }
            for (int i = 1; i < TS_ENTITIES.length; i++) {
                em.persist(TS_ENTITIES[i]);
            }
        });
        // MySQL platform has version precision set to 1 sec. Make sure it gets incremented
        // before running the tests.
        if (emf.getDatabaseSession().getPlatform().isMySQL()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                emf.getDatabaseSession().logThrowable(SessionLog.FINEST, SessionLog.JPA, ex);
            }
        }
    }

}
