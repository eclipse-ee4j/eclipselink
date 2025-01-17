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

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceConfiguration;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.PersistenceUnitTransactionType;
import jakarta.persistence.PersistenceUnitUtil;
import jakarta.persistence.TypedQueryReference;
import junit.framework.Test;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.FetchGroupManager;
import org.eclipse.persistence.internal.jpa.EntityGraphImpl;
import org.eclipse.persistence.jpa.JpaEntityManagerFactory;
import org.eclipse.persistence.testing.models.jpa.persistence32.Pokemon;
import org.eclipse.persistence.testing.models.jpa.persistence32.Team;
import org.eclipse.persistence.testing.models.jpa.persistence32.Trainer;
import org.eclipse.persistence.testing.models.jpa.persistence32.Trainer_;
import org.eclipse.persistence.testing.models.jpa.persistence32.Type;
import org.eclipse.persistence.testing.models.jpa.persistence32.VersionEntity;

/**
 * Verify jakarta.persistence 3.2 API changes in {@link jakarta.persistence.EntityManagerFactory}.
 */
public class EntityManagerFactoryTest extends AbstractPokemonSuite {

    public static Test suite() {
        return suite(
                "EntityManagerFactoryTest",
                new EntityManagerFactoryTest("testCallInTransaction"),
                new EntityManagerFactoryTest("testRunInTransaction"),
                new EntityManagerFactoryTest("testRunWithConnection"),
                new EntityManagerFactoryTest("testCallWithConnection"),
                new EntityManagerFactoryTest("testCreateCustomEntityManagerFactory"),
                new EntityManagerFactoryTest("testCreateConflictingCustomEntityManagerFactory"),
                new EntityManagerFactoryTest("testCreateConflictingConfiguredEntityManagerFactory"),
                new EntityManagerFactoryTest("testIsLoadedEntityAttribute"),
                new EntityManagerFactoryTest("testLoadEntityAttribute"),
                new EntityManagerFactoryTest("testIsLoadedEntityNamedAttribute"),
                new EntityManagerFactoryTest("testLoadEntityNamedAttribute"),
                new EntityManagerFactoryTest("testVerifyPokemonFetchGroups"),
                new EntityManagerFactoryTest("testIsLoadedEntity"),
                new EntityManagerFactoryTest("testLoadEntity"),
                new EntityManagerFactoryTest("testGetVersionOnEntityWithVersion"),
                new EntityManagerFactoryTest("testGetVersionOnEntityWithoutVersion"),
                new EntityManagerFactoryTest("testGetNamedPokemonQueries"),
                new EntityManagerFactoryTest("testGetNamedAllQueries"),
                new EntityManagerFactoryTest("testGetNamedPokemonEntityGraphs"),
                new EntityManagerFactoryTest("testGetNamedAllEntityGraphs"),
                new EntityManagerFactoryTest("testGetName")
        );
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
        PersistenceConfiguration configuration = createPersistenceConfiguration(emf, "persistence32_custom");
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
            PersistenceConfiguration configuration = createPersistenceConfiguration(emf, "persistence32");
            // Attempt to create configured PU with conflicting name to trigger validation failure
            Persistence.createEntityManagerFactory(configuration);
            fail("Persistence.createEntityManagerFactory(PersistenceConfiguration) with already existing PU name shall throw PersistenceException");
        } catch (PersistenceException pe) {
            assertTrue(
                    "Unexpected exception message: " + pe.getMessage(),
                    pe.getMessage().contains("Cannot create a custom persistence unit with the name"));
            assertTrue(
                    "Unexpected exception message: " + pe.getMessage(),
                    pe.getMessage().contains("This name was found in the xml configuration."));
        }
    }

    // Test Persistence.createEntityManagerFactory(String, Map) with already used custom PU name
    public void testCreateConflictingConfiguredEntityManagerFactory() {
        try {
            // Create custom PU name to be in conflict with later used PU name from config
            PersistenceConfiguration configuration = createPersistenceConfiguration(emf, "persistence32_custom");
            // Make sure custom PU is cached
            Persistence.createEntityManagerFactory(configuration).close();
            // Attempt to create configured PU with the same name to trigger validation failure
            Persistence.createEntityManagerFactory("persistence32_custom", Collections.emptyMap());
            fail("Persistence.createEntityManagerFactory(String, Map) with already existing PU name shall throw PersistenceException");
        } catch (PersistenceException pe) {
            assertTrue(
                    "Unexpected exception message: " + pe.getMessage(),
                    pe.getMessage().contains("Cannot create a configured persistence unit with the name"));
            assertTrue(
                    "Unexpected exception message: " + pe.getMessage(),
                    pe.getMessage().contains("This name was found in custom persistence units."));
        }
    }

    // Test <E> boolean PersistenceUnitUtil#isLoaded(E, Attribute<? super E, ?>) on lazy entity reference
    public void testIsLoadedEntityAttribute() {
        Trainer t = emf.callInTransaction(
                em -> em.createQuery("SELECT t FROM Trainer t WHERE t.name = :name", Trainer.class)
                        .setParameter("name", "Ash")
                        .getSingleResult());
        PersistenceUnitUtil util = emf.getPersistenceUnitUtil();
        // This mapping is lazy, so it shall not be loaded
        assertFalse("Entity lazy attribute Trainer.team should not be loaded", util.isLoaded(t, Trainer_.team));
    }

    // Test <E> void PersistenceUnitUtil#load(E, Attribute<? super E, ?>) on lazy entity reference
    public void testLoadEntityAttribute() {
        Trainer t = emf.callInTransaction(
                em -> em.createQuery("SELECT t FROM Trainer t WHERE t.name = :name", Trainer.class)
                        .setParameter("name", "Ash")
                        .getSingleResult());
        PersistenceUnitUtil util = emf.getPersistenceUnitUtil();
        // This mapping is lazy, so it shall not be loaded
        assertFalse("Entity lazy attribute Trainer.team should not be loaded", util.isLoaded(t, Trainer_.team));
        util.load(t, Trainer_.team);
        assertTrue("Entity lazy attribute Trainer.team should be loaded after load call", util.isLoaded(t, Trainer_.team));
    }

    // Test <E> boolean PersistenceUnitUtil#isLoaded(Object, String) on lazy entity reference
    public void testIsLoadedEntityNamedAttribute() {
        Trainer t = emf.callInTransaction(
                em -> em.createQuery("SELECT t FROM Trainer t WHERE t.name = :name", Trainer.class)
                        .setParameter("name", "Ash")
                        .getSingleResult());
        PersistenceUnitUtil util = emf.getPersistenceUnitUtil();
        // This mapping is lazy, so it shall not be loaded
        assertFalse("Entity lazy attribute Trainer.team should not be loaded", util.isLoaded(t, "team"));
    }

    // Test <E> void PersistenceUnitUtil#load(Object, String) on lazy entity reference
    public void testLoadEntityNamedAttribute() {
        Trainer t = emf.callInTransaction(
                em -> em.createQuery("SELECT t FROM Trainer t WHERE t.name = :name", Trainer.class)
                        .setParameter("name", "Ash")
                        .getSingleResult());
        PersistenceUnitUtil util = emf.getPersistenceUnitUtil();
        // This mapping is lazy, so it shall not be loaded
        assertFalse("Entity lazy attribute Trainer.team should not be loaded",
                    util.isLoaded(t, "team"));
        util.load(t, "team");
        assertTrue("Entity lazy attribute Trainer.team should be loaded after load call",
                   util.isLoaded(t, "team"));
    }

    // Verify Pokemon entity fetch groups: custom FetchTypes FetchGroup must be defined
    public void testVerifyPokemonFetchGroups() {
        if (isWeavingEnabled()) {
            ClassDescriptor pokemonsDescriptor = getPersistenceUnitServerSession().getDescriptor(Pokemon.class);
            FetchGroupManager pokemonsFetchGroupManager = pokemonsDescriptor.getFetchGroupManager();
            assertEquals("Wrong number of fetch groups for Pokemon", 1, pokemonsFetchGroupManager.getFetchGroups().size());
            assertNotNull("The 'FetchTypes' fetch group was not found for Pokemon", pokemonsFetchGroupManager.getFetchGroup("FetchTypes"));
        }
    }

    // Test boolean PersistenceUnitUtil#isLoaded(Object) on non fully loaded entity using FetchTypes FetchGroup
    public void testIsLoadedEntity() {
        if (isWeavingEnabled()) {
            Pokemon ekans = new Pokemon(6, TRAINERS[2], "Ekans", List.of(TYPES[4]));
            emf.runInTransaction(em -> em.persist(ekans));
            clearCache();
            Map<String, Object> properties = new HashMap<>();
            properties.put(QueryHints.FETCH_GROUP_NAME, "FetchTypes");
            Class<Pokemon> pokemonClass = Pokemon.class;
            EntityManager em = createEntityManager();
            try {
                Pokemon pokemon = em.find(pokemonClass, ekans.getId(), properties);
                verifyFetchedField(pokemonClass.getDeclaredField("types"), pokemon, ekans.getTypes());
                verifyNonFetchedField(pokemonClass.getDeclaredField("name"), pokemon);
                verifyNonFetchedField(pokemonClass.getDeclaredField("trainer"), pokemon);
                PersistenceUnitUtil util = em.getEntityManagerFactory().getPersistenceUnitUtil();
                // This mapping is lazy, so it shall not be loaded
                assertFalse("Pokemon lazy attributes name and trainer should not be loaded", util.isLoaded(pokemon));
            } catch (Exception e) {
                fail("Error verifying field content: " + e.getMessage());
            } finally {
                closeEntityManager(em);
            }
        }
    }

    // Test boolean PersistenceUnitUtil#load(Object) on non fully loaded entity using FetchTypes FetchGroup
    public void testLoadEntity() {
        if (isWeavingEnabled()) {
            Pokemon arbok = new Pokemon(7, TRAINERS[2], "Arbok", List.of(TYPES[4]));
            emf.runInTransaction(em -> em.persist(arbok));
            clearCache();
            Map<String, Object> properties = new HashMap<>();
            properties.put(QueryHints.FETCH_GROUP_NAME, "FetchTypes");
            Class<Pokemon> pokemonClass = Pokemon.class;
            EntityManager em = createEntityManager();
            try {
                Pokemon pokemon = em.find(pokemonClass, arbok.getId(), properties);
                verifyFetchedField(pokemonClass.getDeclaredField("types"), pokemon, arbok.getTypes());
                verifyNonFetchedField(pokemonClass.getDeclaredField("name"), pokemon);
                verifyNonFetchedField(pokemonClass.getDeclaredField("trainer"), pokemon);
                PersistenceUnitUtil util = em.getEntityManagerFactory().getPersistenceUnitUtil();
                // This mapping is lazy, so it shall not be loaded
                assertFalse("Pokemon lazy attributes name and trainer should not be loaded", util.isLoaded(pokemon));
                util.load(pokemon);
                assertTrue("Pokemon lazy attributes name and trainer should be loaded after load call", util.isLoaded(pokemon));
            } catch (Exception e) {
                fail("Error verifying field content: " + e.getMessage());
            } finally {
                closeEntityManager(em);
            }
        }
    }

    // Test Object PersistenceUnitUtil#getVersion(Object) on entity with version attribute
    public void testGetVersionOnEntityWithVersion() {
        VersionEntity entity = new VersionEntity(1, 1, "Entity");
        emf.runInTransaction(em -> em.persist(entity));
        Integer version = (Integer) emf.getPersistenceUnitUtil().getVersion(entity);
        assertNotNull(version);
        assertEquals(entity.getVersion(), version.intValue());
    }

    // Test Object PersistenceUnitUtil#getVersion(Object) on entity without version attribute
    // Shall throw IllegalArgumentException
    public void testGetVersionOnEntityWithoutVersion() {
        Team entity = new Team(100, "Team");
        emf.runInTransaction(em -> em.persist(entity));
        try {
            emf.getPersistenceUnitUtil().getVersion(entity);
            fail("PersistenceUnitUtil#getVersion(Entity) on entity without version shall throw IllegalArgumentException");
        } catch (IllegalArgumentException iae) {
            assertTrue(
                    "Unexpected exception message: " + iae.getMessage(),
                    iae.getMessage().contains("which has no version attribute"));
        }
    }

    // All named queries returning Pokemon in current PU
    public void testGetNamedPokemonQueries() {
        Map<String, TypedQueryReference<Pokemon>> queries = emf.getNamedQueries(Pokemon.class);
        assertEquals(1, queries.size());
        TypedQueryReference<Pokemon> reference = queries.get("Pokemon.get");
        assertEquals("Pokemon.get", reference.getName());
    }

    // All named queries defined in current PU
    public void testGetNamedAllQueries() {
        Map<String, TypedQueryReference<Object>> queries = emf.getNamedQueries(Object.class);
        assertEquals(4, queries.size());
        for (TypedQueryReference<Object> query : queries.values()) {
            switch (query.getName()) {
                case "Team.get":
                    assertEquals(Team.class, query.getResultType());
                    assertEquals(2, query.getHints().size());
                    assertTrue(String.format("Named query Team.get shall contain %s hint", QueryHints.CACHE_USAGE),
                               query.getHints().containsKey(QueryHints.CACHE_USAGE));
                    assertTrue(String.format("Named query Team.get shall contain %s hint", QueryHints.QUERY_TIMEOUT),
                               query.getHints().containsKey(QueryHints.QUERY_TIMEOUT));
                    break;
                case "Trainer.get":
                    assertEquals(Trainer.class, query.getResultType());
                    assertEquals(0, query.getHints().size());
                    break;
                case "Type.all":
                    assertEquals(Type.class, query.getResultType());
                    assertEquals(0, query.getHints().size());
                    break;
                case "Pokemon.get":
                    assertEquals(Pokemon.class, query.getResultType());
                    assertEquals(0, query.getHints().size());
                    break;
                default:
                    fail(String.format("Unknown named query %s found", query.getName()));
            }
        }
    }

    // All EntityGraphs for Pokemon entity in current PU
    public void testGetNamedPokemonEntityGraphs() {
        Map<String, EntityGraph<? extends Pokemon>> entityGraphs = emf.getNamedEntityGraphs(Pokemon.class);
        assertEquals(1, entityGraphs.size());
        EntityGraph<? extends Pokemon> pokemonGraph = entityGraphs.get("Pokemon.fetchGraph");
        assertNotNull(pokemonGraph);
    }

    // All EntityGraphs in current PU
    public void testGetNamedAllEntityGraphs() {
        Map<String, EntityGraph<?>> entityGraphs = emf.getNamedEntityGraphs(Object.class);
        assertEquals(2, entityGraphs.size());
        for (EntityGraph<?> entityGraph : entityGraphs.values()) {
            switch (entityGraph.getName()) {
            case "Pokemon.fetchGraph":
                assertEquals(Pokemon.class, ((EntityGraphImpl<?>)entityGraph).getClassType());
                break;
            case "Trainer.fetchGraph":
                assertEquals(Trainer.class, ((EntityGraphImpl<?>)entityGraph).getClassType());
                break;
            default:
                fail(String.format("Unknown EntityGraph %s found", entityGraph.getName()));
            }
        }
    }

    public void testGetName() {
        assertEquals(this.getPersistenceUnitName(), emf.getName());
    }

    private static PersistenceConfiguration createPersistenceConfiguration(JpaEntityManagerFactory emf, String puName) {
        PersistenceConfiguration configuration = new PersistenceConfiguration(puName);
        configuration.properties(emf.getProperties());
        configuration.managedClass(org.eclipse.persistence.testing.models.jpa.persistence32.Team.class);
        configuration.managedClass(org.eclipse.persistence.testing.models.jpa.persistence32.Trainer.class);
        configuration.managedClass(org.eclipse.persistence.testing.models.jpa.persistence32.Pokemon.class);
        configuration.managedClass(org.eclipse.persistence.testing.models.jpa.persistence32.Type.class);
        configuration.transactionType(PersistenceUnitTransactionType.RESOURCE_LOCAL);
        configuration.provider("org.eclipse.persistence.jpa.PersistenceProvider");
        return configuration;
    }

    private static void verifyFetchedField(Field field, Object obj, Object value) {
        try {
            field.setAccessible(true);
            assertEquals("The field [" + field.getName() + "] was not fetched", field.get(obj), value);
        } catch (IllegalAccessException e) {
            fail("Error verifying field content: " + e.getMessage());
        }
    }

    private static void verifyNonFetchedField(Field field, Object obj) {
        try {
            field.setAccessible(true);
            assertNull("The field [" + field.getName() + "] was fetched", field.get(obj));
        } catch (IllegalAccessException e) {
            fail("Error verifying field content: " + e.getMessage());
        }
    }

}
