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

import jakarta.persistence.OptimisticLockException;
import junit.framework.Test;
import org.eclipse.persistence.testing.models.jpa.persistence32.InstantVersionEntity;
import org.eclipse.persistence.testing.models.jpa.persistence32.LdtVersionEntity;
import org.eclipse.persistence.testing.models.jpa.persistence32.TimestampVersionEntity;

/**
 * Verify jakarta.persistence 3.2 @Version attribute types
 */
public class VersionTest extends AbstractVersionSuite {

    // LdtVersionEntity instances
    private static final LdtVersionEntity[] LDT_ENTITIES_INSERT = new LdtVersionEntity[] {
            new LdtVersionEntity(4, LocalDateTime.now(), "Fourth"),
    };

    // LdtVersionEntity instances
    private static final InstantVersionEntity[] INSTANT_ENTITIES_INSERT = new InstantVersionEntity[] {
            new InstantVersionEntity(4, Instant.now(), "Fourth"),
    };

    // TimestampVersionEntity instances
    private static final TimestampVersionEntity[] TS_ENTITIES_INSERT = new TimestampVersionEntity[] {
            new TimestampVersionEntity(4, Timestamp.from(Instant.now()), "Fourth"),
    };

    public static Test suite() {
        return suite(
                "VersionTest",
                new VersionTest("testFindLdtVersionEntity"),
                new VersionTest("testPersistLdtVersionEntity"),
                new VersionTest("testMergeLdtVersionEntity"),
                new VersionTest("testUpdateCollisionLdtVersionEntity"),
                new VersionTest("testFindInstantVersionEntity"),
                new VersionTest("testPersistInstantVersionEntity"),
                new VersionTest("testMergeInstantVersionEntity"),
                new VersionTest("testUpdateCollisionInstantVersionEntity"),
                new VersionTest("testFindTimestampVersionEntity"),
                new VersionTest("testPersistTimestampVersionEntity"),
                new VersionTest("testMergeTimestampVersionEntity"),
                new VersionTest("testUpdateCollisionTimestampVersionEntity")
        );
    }

    public VersionTest() {
    }

    public VersionTest(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
    }

    public void testFindLdtVersionEntity() {
        LdtVersionEntity entity = emf.callInTransaction(em -> em.find(LdtVersionEntity.class, 1));
        assertEquals(LDT_ENTITIES[1], entity);
    }

    public void testPersistLdtVersionEntity() {
        emf.runInTransaction(em -> em.persist(LDT_ENTITIES_INSERT[0]));
        LdtVersionEntity entity = emf.callInTransaction(em -> em
                .createQuery("SELECT e FROM LdtVersionEntity e WHERE e.id=:id",
                             LdtVersionEntity.class)
                .setParameter("id", 4)
                .getSingleResult());
        assertEquals(LDT_ENTITIES_INSERT[0], entity);
    }

    public void testMergeLdtVersionEntity() {
        LdtVersionEntity entity = LDT_ENTITIES[2];
        entity.setName(entity.getName() + "Modified");
        emf.runInTransaction(em -> em.merge(entity));
        LdtVersionEntity dbEntity = emf.callInTransaction(em -> em.find(LdtVersionEntity.class, entity.getId()));
        // Version will differ so only ID and modified attribute is checked
        assertEquals(entity.getId(), dbEntity.getId());
        assertEquals(entity.getName(), dbEntity.getName());
    }

    public void testUpdateCollisionLdtVersionEntity() {
        LdtVersionEntity entity1 = emf.callInTransaction(em -> em.find(LdtVersionEntity.class, LDT_ENTITIES[3].getId()));
        LdtVersionEntity entity2 = emf.callInTransaction(em -> em.find(LdtVersionEntity.class, LDT_ENTITIES[3].getId()));
        // 1st attempt to persist entity1 shall pass, it will make entity2 invalid (old version)
        emf.runInTransaction(em -> {
            LdtVersionEntity e = em.merge(entity1);
            e.setName(entity1.getName() + "1st");
        });
        // 2nd attempt to persist entity2 shall fail
        try {
            emf.runInTransaction(em -> {
                LdtVersionEntity e = em.merge(entity2);
                e.setName(entity1.getName() + "1st");
            });
            fail("Attempt to persist instance with old version shall fail.");
        } catch (OptimisticLockException ex) {
            // This exception is expected be thrown
        }
    }

    public void testFindInstantVersionEntity() {
        InstantVersionEntity entity = emf.callInTransaction(em -> em.find(InstantVersionEntity.class, 1));
        assertEquals(INSTANT_ENTITIES[1], entity);
    }

    public void testPersistInstantVersionEntity() {
        emf.runInTransaction(em -> em.persist(INSTANT_ENTITIES_INSERT[0]));
        InstantVersionEntity entity = emf.callInTransaction(em -> em
                .createQuery("SELECT e FROM InstantVersionEntity e WHERE e.id=:id",
                             InstantVersionEntity.class)
                .setParameter("id", 4)
                .getSingleResult());
        assertEquals(INSTANT_ENTITIES_INSERT[0], entity);
    }

    public void testMergeInstantVersionEntity() {
        InstantVersionEntity entity = INSTANT_ENTITIES[2];
        entity.setName(entity.getName() + "Modified");
        emf.runInTransaction(em -> em.merge(entity));
        InstantVersionEntity dbEntity = emf.callInTransaction(em -> em.find(InstantVersionEntity.class, entity.getId()));
        // Version will differ so only ID and modified attribute is checked
        assertEquals(entity.getId(), dbEntity.getId());
        assertEquals(entity.getName(), dbEntity.getName());
    }

    public void testUpdateCollisionInstantVersionEntity() {
        InstantVersionEntity entity1 = emf.callInTransaction(em -> em.find(InstantVersionEntity.class, INSTANT_ENTITIES[3].getId()));
        InstantVersionEntity entity2 = emf.callInTransaction(em -> em.find(InstantVersionEntity.class, INSTANT_ENTITIES[3].getId()));
        // 1st attempt to persist entity1 shall pass, it will make entity2 invalid (old version)
        emf.runInTransaction(em -> {
            InstantVersionEntity e = em.merge(entity1);
            e.setName(entity1.getName() + "1st");
        });
        // 2nd attempt to persist entity2 shall fail
        try {
            emf.runInTransaction(em -> {
                InstantVersionEntity e = em.merge(entity2);
                e.setName(entity1.getName() + "1st");
            });
            fail("Attempt to persist instance with old version shall fail.");
        } catch (OptimisticLockException ex) {
            // This exception is expected be thrown
        }
    }

    public void testFindTimestampVersionEntity() {
        TimestampVersionEntity entity = emf.callInTransaction(em -> em.find(TimestampVersionEntity.class, 1));
        assertEquals(TS_ENTITIES[1], entity);
    }

    public void testPersistTimestampVersionEntity() {
        emf.runInTransaction(em -> {
            em.persist(TS_ENTITIES_INSERT[0]);
        });
        TimestampVersionEntity entity = emf.callInTransaction(em -> em
                .createQuery("SELECT e FROM TimestampVersionEntity e WHERE e.id=:id",
                             TimestampVersionEntity.class)
                .setParameter("id", 4)
                .getSingleResult());
        assertEquals(TS_ENTITIES_INSERT[0], entity);
    }

    public void testMergeTimestampVersionEntity() {
        TimestampVersionEntity entity = TS_ENTITIES[2];
        entity.setName(entity.getName() + "Modified");
        emf.runInTransaction(em -> em.merge(entity));
        TimestampVersionEntity dbEntity = emf.callInTransaction(em -> em.find(TimestampVersionEntity.class, entity.getId()));
        // Version will differ so only ID and modified attribute is checked
        assertEquals(entity.getId(), dbEntity.getId());
        assertEquals(entity.getName(), dbEntity.getName());
    }

    public void testUpdateCollisionTimestampVersionEntity() {
        TimestampVersionEntity entity1 = emf.callInTransaction(em -> em.find(TimestampVersionEntity.class, TS_ENTITIES[3].getId()));
        TimestampVersionEntity entity2 = emf.callInTransaction(em -> em.find(TimestampVersionEntity.class, TS_ENTITIES[3].getId()));
        // 1st attempt to persist entity1 shall pass, it will make entity2 invalid (old version)
        emf.runInTransaction(em -> {
            TimestampVersionEntity e = em.merge(entity1);
            e.setName(entity1.getName() + "1st");
        });
        // 2nd attempt to persist entity2 shall fail
        try {
            emf.runInTransaction(em -> {
                TimestampVersionEntity e = em.merge(entity2);
                e.setName(entity1.getName() + "1st");
            });
            fail("Attempt to persist instance with old version shall fail.");
        } catch (OptimisticLockException ex) {
            // This exception is expected be thrown
        }
    }

}
