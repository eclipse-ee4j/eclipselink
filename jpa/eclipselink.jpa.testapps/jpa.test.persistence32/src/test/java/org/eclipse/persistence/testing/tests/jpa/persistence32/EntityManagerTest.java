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
package org.eclipse.persistence.testing.tests.jpa.persistence32;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.EntityTransaction;
import junit.framework.Test;
import org.eclipse.persistence.internal.descriptors.PersistenceEntity;
import org.eclipse.persistence.testing.models.jpa.persistence32.Pokemon;

/**
 * Verify jakarta.persistence 3.2 API changes in {@link jakarta.persistence.EntityManager}.
 */
public class EntityManagerTest extends AbstractPokemon {

    // Pokemons. Array index is ID value.
    // Value of ID = 0 does not exist so it's array instance is set to null.
    static final Pokemon[] POKEMONS = new Pokemon[] {
            null, // Skip array index 0
            new Pokemon(1, "Pidgey", List.of(TYPES[1], TYPES[3])),
    };

    public static Test suite() {
        return suite(
                "EntityManagerTest",
                new EntityManagerTest("testGetReferenceForExistingEntity"),
                new EntityManagerTest("testGetReferenceForNotExistingEntity")
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
                em.persist(POKEMONS[i]);
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
                    reference.getName();
                    fail("Accessing non-existing entity shall throw EntityNotFoundException");
                // EntityNotFoundException shall be thrown on non-existing entity access
                } catch (EntityNotFoundException enfe) {
                    assertTrue("Unexpected exception message: " + enfe.getLocalizedMessage(),
                               enfe.getLocalizedMessage().contains("Could not find Entity"));
                }
                et.commit();
            } catch (Exception e) {
                et.rollback();
                throw e;
            }
        }
    }

}
