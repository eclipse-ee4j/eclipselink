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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceConfiguration;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.PersistenceUnitTransactionType;
import junit.framework.Test;
import org.eclipse.persistence.testing.models.jpa.persistence32.Pokemon;
import org.eclipse.persistence.testing.models.jpa.persistence32.Type;

public class EntityManagerFactoryTest extends AbstractPokemon {

    public static Test suite() {
        return suite(
                "EntityManagerFactoryTest",
                new EntityManagerFactoryTest("testCallInTransaction"),
                new EntityManagerFactoryTest("testRunInTransaction"),
                new EntityManagerFactoryTest("testRunWithConnection"),
                new EntityManagerFactoryTest("testCallWithConnection"),
                new EntityManagerFactoryTest("testCreateCustomEntityManagerFactory"),
                new EntityManagerFactoryTest("testCreateConflictingCustomEntityManagerFactory"),
                new EntityManagerFactoryTest("testCreateConflictingConfiguredEntityManagerFactory"));
    }

    public EntityManagerFactoryTest() {
    }

    public EntityManagerFactoryTest(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
    }

    public void testCallInTransaction() {
        Pokemon pokemon = emf.callInTransaction(em -> {
            Map<Integer, Type> types = pokemonTypes(em);
            Pokemon newPokemon = new Pokemon(1, "Pidgey", List.of(types.get(1), types.get(3)));
            em.persist(newPokemon);
            return newPokemon;
        });
        verifyObjectInEntityManager(pokemon, getPersistenceUnitName());
    }

    public void testRunInTransaction() {
        Pokemon[] pokemon = new Pokemon[1];
        emf.runInTransaction(em -> {
            Map<Integer, Type> types = pokemonTypes(em);
            Pokemon newPokemon = new Pokemon(2, "Beedrill", List.of(types.get(7), types.get(4)));
            em.persist(newPokemon);
            pokemon[0] = newPokemon;
        });
        verifyObjectInEntityManager(pokemon[0], getPersistenceUnitName());
    }

    public void testRunWithConnection() {
        Pokemon[] pokemon = new Pokemon[1];
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction et = em.getTransaction();
            try {
                et.begin();
                em.<Connection>runWithConnection(
                        connection -> {
                            Pokemon newPokemon = new Pokemon(3, "Squirtle", List.of(TYPES[11]));
                            try (PreparedStatement stmt = connection.prepareStatement(
                                    "INSERT INTO PERSISTENCE32_POKEMON (ID, NAME) VALUES(?, ?)")) {
                                stmt.setInt(1, newPokemon.getId());
                                stmt.setString(2, newPokemon.getName());
                                stmt.executeUpdate();
                            }
                            try (PreparedStatement stmt = connection.prepareStatement(
                                    "INSERT INTO PERSISTENCE32_POKEMON_TYPE (POKEMON_ID, TYPE_ID) VALUES(?, ?)")) {
                                for (Type type : newPokemon.getTypes()) {
                                    stmt.setInt(1, newPokemon.getId());
                                    stmt.setInt(2, type.getId());
                                    stmt.executeUpdate();
                                }
                            }
                            pokemon[0] = newPokemon;
                        }
                );
                et.commit();
            } catch (Exception e) {
                et.rollback();
                throw e;
            }
        }
        try (EntityManager em = createEntityManager()) {
            Pokemon dbPokemon = em.find(Pokemon.class, 3);
            assertEquals(pokemon[0], dbPokemon);
        }
    }

    public void testCallWithConnection() {
        Pokemon pokemon;
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction et = em.getTransaction();
            try {
                et.begin();
                pokemon = em.<Connection, Pokemon>callWithConnection(
                        connection -> {
                            Pokemon newPokemon = new Pokemon(4, "Caterpie", List.of(TYPES[7]));
                            try (PreparedStatement stmt = connection.prepareStatement(
                                    "INSERT INTO PERSISTENCE32_POKEMON (ID, NAME) VALUES(?, ?)")) {
                                stmt.setInt(1, newPokemon.getId());
                                stmt.setString(2, newPokemon.getName());
                                stmt.executeUpdate();
                            }
                            try (PreparedStatement stmt = connection.prepareStatement(
                                    "INSERT INTO PERSISTENCE32_POKEMON_TYPE (POKEMON_ID, TYPE_ID) VALUES(?, ?)")) {
                                for (Type type : newPokemon.getTypes()) {
                                    stmt.setInt(1, newPokemon.getId());
                                    stmt.setInt(2, type.getId());
                                    stmt.executeUpdate();
                                }
                            }
                            return newPokemon;
                        }
                );
                et.commit();
            } catch (Exception e) {
                et.rollback();
                throw e;
            }
        }
        try (EntityManager em = createEntityManager()) {
            Pokemon dbPokemon = em.find(Pokemon.class, 4);
            assertEquals(pokemon, dbPokemon);
        }
    }

    // Test Persistence.createEntityManagerFactory(PersistenceConfiguration)
    public void testCreateCustomEntityManagerFactory() {
        PersistenceConfiguration configuration = new PersistenceConfiguration("persistence32_custom");
        configuration.properties(emf.getProperties());
        configuration.managedClass(org.eclipse.persistence.testing.models.jpa.persistence32.Trainer.class);
        configuration.managedClass(org.eclipse.persistence.testing.models.jpa.persistence32.Pokemon.class);
        configuration.managedClass(org.eclipse.persistence.testing.models.jpa.persistence32.Type.class);
        configuration.transactionType(PersistenceUnitTransactionType.RESOURCE_LOCAL);
        configuration.provider("org.eclipse.persistence.jpa.PersistenceProvider");
        Pokemon pokemon = new Pokemon(5, "Primeape", List.of(TYPES[2]));
        try (EntityManagerFactory emfFromConfig = Persistence.createEntityManagerFactory(configuration)) {
            try (EntityManager em = emfFromConfig.createEntityManager()) {
                EntityTransaction et = em.getTransaction();
                try {
                    et.begin();
                    em.persist(pokemon);
                    et.commit();
                } catch (Exception e) {
                    et.rollback();
                    throw e;
                }
            }
        }
        try (EntityManager em = createEntityManager()) {
            Pokemon dbPokemon = em.find(Pokemon.class, 5);
            assertEquals(pokemon, dbPokemon);
        }
    }

    // Test Persistence.createEntityManagerFactory(PersistenceConfiguration) with already configured PU name
    public void testCreateConflictingCustomEntityManagerFactory() {
        try {
            // Create custom PU name to be in conflict with already configured one
            PersistenceConfiguration configuration = new PersistenceConfiguration("persistence32");
            configuration.properties(emf.getProperties());
            configuration.managedClass(org.eclipse.persistence.testing.models.jpa.persistence32.Trainer.class);
            configuration.managedClass(org.eclipse.persistence.testing.models.jpa.persistence32.Pokemon.class);
            configuration.managedClass(org.eclipse.persistence.testing.models.jpa.persistence32.Type.class);
            configuration.transactionType(PersistenceUnitTransactionType.RESOURCE_LOCAL);
            configuration.provider("org.eclipse.persistence.jpa.PersistenceProvider");
            // Attempt to create configured PU with conflicting name to trigger validation failure
            Persistence.createEntityManagerFactory(configuration);
            fail("Persistence.createEntityManagerFactory(PersistenceConfiguration) with already existing PU name shall throw PersistenceException");
        } catch (PersistenceException pe) {
            assertTrue(
                    "Unexpected exception message: " + pe.getLocalizedMessage(),
                    pe.getLocalizedMessage().contains("Cannot create custom persistence unit with name"));
            assertTrue(
                    "Unexpected exception message: " + pe.getLocalizedMessage(),
                    pe.getLocalizedMessage().contains("This name was found in xml configuration."));
        }
    }

    // Test Persistence.createEntityManagerFactory(String, Map) with already used custom PU name
    public void testCreateConflictingConfiguredEntityManagerFactory() {
        try {
            // Create custom PU name to be in conflict with later used PU name from config
            PersistenceConfiguration configuration = new PersistenceConfiguration("persistence32_custom");
            configuration.properties(emf.getProperties());
            configuration.managedClass(org.eclipse.persistence.testing.models.jpa.persistence32.Trainer.class);
            configuration.managedClass(org.eclipse.persistence.testing.models.jpa.persistence32.Pokemon.class);
            configuration.managedClass(org.eclipse.persistence.testing.models.jpa.persistence32.Type.class);
            configuration.transactionType(PersistenceUnitTransactionType.RESOURCE_LOCAL);
            configuration.provider("org.eclipse.persistence.jpa.PersistenceProvider");
            // Make sure custom PU is cached
            Persistence.createEntityManagerFactory(configuration).close();
            // Attempt to create configured PU with the same name to trigger validation failure
            Persistence.createEntityManagerFactory("persistence32_custom", Collections.emptyMap());
            fail("Persistence.createEntityManagerFactory(String, Map) with already existing PU name shall throw PersistenceException");
        } catch (PersistenceException pe) {
            assertTrue(
                    "Unexpected exception message: " + pe.getLocalizedMessage(),
                    pe.getLocalizedMessage().contains("Cannot create configured persistence unit with name"));
            assertTrue(
                    "Unexpected exception message: " + pe.getLocalizedMessage(),
                    pe.getLocalizedMessage().contains("This name was found in custom persistence units."));
        }
    }

}
