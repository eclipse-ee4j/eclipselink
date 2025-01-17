/*
 * Copyright (c) 2023, 2025 Oracle and/or its affiliates. All rights reserved.
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

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import jakarta.persistence.CacheRetrieveMode;
import jakarta.persistence.CacheStoreMode;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.LockModeType;
import jakarta.persistence.LockOption;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Timeout;
import junit.framework.Test;
import org.eclipse.persistence.internal.descriptors.PersistenceEntity;
import org.eclipse.persistence.testing.models.jpa.persistence32.Pokemon;

/**
 * Verify jakarta.persistence 3.2 API changes in {@link jakarta.persistence.EntityManager}.
 */
public class EntityManagerTest extends AbstractPokemonSuite {

    // Pokemons. Array index is ID value.
    // Value of ID = 0 does not exist so it's array instance is set to null.
    static final Pokemon[] POKEMONS = new Pokemon[] {
            null, // Skip array index 0
            new Pokemon(1, "Pidgey", List.of(TYPES[1], TYPES[3])),
            null, // Array index 2 is reserved for testGetReferenceForNotExistingEntity
            new Pokemon(3, "Squirtle", List.of(TYPES[11])),
            new Pokemon(4, "Caterpie", List.of(TYPES[7]))
    };

    public static Test suite() {
        return suite(
                "EntityManagerTest",
                new EntityManagerTest("testGetReferenceForExistingEntity"),
                new EntityManagerTest("testGetReferenceForNotExistingEntity"),
                new EntityManagerTest("testLockOptionUtilsUnknownClass"),
                new EntityManagerTest("testLockPessimisticWriteWithTimeout"),
                new EntityManagerTest("testSetCacheRetrieveMode"),
                new EntityManagerTest("testSetCacheStoreMode")
        );
    }

    public EntityManagerTest() {
    }

    public EntityManagerTest(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
    }

    // Initialize data
    @Override
    protected void suiteSetUp() {
        super.suiteSetUp();
        emf.runInTransaction(em -> {
            for (int i = 1; i < POKEMONS.length; i++) {
                if (POKEMONS[i] != null) {
                    em.persist(POKEMONS[i]);
                }
            }
        });
    }

    // Create new instance of Pokemon that exisxts in the database and obtain its reference.
    public void testGetReferenceForExistingEntity() {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction et = em.getTransaction();
            try {
                et.begin();
                Pokemon pokemon = new Pokemon(1, "Pidgey", List.of(TYPES[1], TYPES[3]));
                Pokemon reference = em.getReference(pokemon);
                assertTrue(reference instanceof PersistenceEntity);
                // Verify that access to entity attribute works
                reference.getName();
                et.commit();
            } catch (Exception e) {
                et.rollback();
                throw e;
            }
        }
    }

    // Create new instance of Pokemon that does not exist in the database and obtain its reference.
    public void testGetReferenceForNotExistingEntity() {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction et = em.getTransaction();
            try {
                et.begin();
                Pokemon pokemon = new Pokemon(2, "Beedrill", List.of(TYPES[7], TYPES[4]));
                Pokemon reference = em.getReference(pokemon);
                assertTrue(reference instanceof PersistenceEntity);
                try {
                    // Verify that access to entity attribute fails
                    reference.getName();
                    fail("Accessing non-existing entity shall throw EntityNotFoundException");
                // EntityNotFoundException shall be thrown on non-existing entity access
                } catch (EntityNotFoundException enfe) {
                    assertTrue("Unexpected exception message: " + enfe.getMessage(),
                               enfe.getMessage().contains("Could not find Entity"));
                }
                et.commit();
            } catch (Exception e) {
                et.rollback();
                throw e;
            }
        }
    }

    // Call lock(Object, LockModeType, LockOption...) with unsupported LockOption instance
    // Shall throw PersistenceException
    public void testLockOptionUtilsUnknownClass() {
        emf.runInTransaction(em -> {
            Pokemon pokemon = em.find(Pokemon.class, POKEMONS[4].getId());
            try {
                em.lock(pokemon, LockModeType.PESSIMISTIC_WRITE, new LockOption() { });
                fail("Calling lock(Object, LockModeType, LockOption...) with unsupported LockOption shall throw PersistenceException");
            } catch (PersistenceException pe) {
                assertTrue("Unexpected exception message: " + pe.getMessage(),
                           pe.getMessage().contains("The LockOption implementing class"));
                assertTrue("Unexpected exception message: " + pe.getMessage(),
                           pe.getMessage().contains("is not supported"));
            }
        });
    }

    // Test lock(Object, LockModeType, LockOption...) with LockModeType.PESSIMISTIC_WRITE and specific timeout
    // Parallel attempt to lock the entity shall fail.
    public void testLockPessimisticWriteWithTimeout() {
        if (isSelectForUpateNoWaitSupported()) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    emf.runInTransaction(em -> {
                        Pokemon pokemon = em.find(Pokemon.class, POKEMONS[3].getId());
                        em.lock(pokemon, LockModeType.PESSIMISTIC_WRITE, Timeout.ms(0));
                    });
                }
            });
            AtomicBoolean hanging = new AtomicBoolean(true);
            emf.runInTransaction(em -> {
                Pokemon pokemon = em.find(Pokemon.class, POKEMONS[3].getId());
                em.lock(pokemon, LockModeType.PESSIMISTIC_WRITE, Timeout.s(60));
                thread.start();
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                // Thread should have failed to get a lock with NOWAIT and hence should have finished by now
                hanging.set(thread.isAlive());
                if (hanging.get()) {
                    thread.interrupt();
                }
            });
            assertFalse("Pessimistic lock with NOWAIT on entity causes concurrent thread to wait", hanging.get());
        } else {
            warning("Skipping testLockPessimisticWriteWithTimeout because SELECT FOR UPDATE NO WAIT is not supported on "
                            + getPlatform().getClass().getSimpleName());
        }
    }

    public void testSetCacheRetrieveMode() {
        try (EntityManager em = emf.createEntityManager();) {
            em.setCacheRetrieveMode(CacheRetrieveMode.BYPASS);
            assertEquals(CacheRetrieveMode.BYPASS, em.getCacheRetrieveMode());
        } catch (Exception e) {
            throw e;
        }
    }

    public void testSetCacheStoreMode() {
        try (EntityManager em = emf.createEntityManager();) {
            em.setCacheStoreMode(CacheStoreMode.USE);
            assertEquals(CacheStoreMode.USE, em.getCacheStoreMode());
        } catch (Exception e) {
            throw e;
        }
    }
}
