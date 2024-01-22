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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.AttributeNode;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceUnitUtil;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.ManagedType;
import junit.framework.Test;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.testing.models.jpa.persistence32.Pokemon;

/**
 * Verify jakarta.persistence 3.2 API changes in {@link EntityGraph}.
 */
public class EntityGraphTest extends AbstractPokemon {

    // Pokemons. Array index is ID value.
    // Value of ID = 0 does not exist so it's array instance is set to null.
    static final Pokemon[] POKEMONS = new Pokemon[] {
            null, // Skip array index 0
            new Pokemon(1, "Pidgey", List.of(TYPES[1], TYPES[3])),
            new Pokemon(2, "Beedrill", List.of(TYPES[7], TYPES[4])),
            new Pokemon(3, "Squirtle", List.of(TYPES[11])),
            new Pokemon(4, "Caterpie", List.of(TYPES[7]))
    };

    public static Test suite() {
        return suite(
                "EntityGraphTest",
                new EntityGraphTest("testRemoveAttributeNodesByPersistentAttributeType")
        );
    }

    public EntityGraphTest() {
    }

    public EntityGraphTest(String name) {
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

    // Test removeAttributeNodes(Attribute.PersistentAttributeType)
    // This also tests addAttributeNode(Attribute<? super X, Y>), addAttributeNode(String)
    // and removeAttributeNode(Attribute<? super X, ?>)
    public void testRemoveAttributeNodesByPersistentAttributeType() {
        try (EntityManager em = emf.createEntityManager()) {
            EntityGraph<Pokemon> pokemonGraph = em.createEntityGraph(Pokemon.class);
            ManagedType<Pokemon> managedType = emf.getMetamodel().managedType(Pokemon.class);
            pokemonGraph.addAttributeNode(managedType.getAttribute("id"));
            pokemonGraph.addAttributeNode(managedType.getAttribute("name"));
            pokemonGraph.addSubgraph("types").addAttributeNode("name");
            pokemonGraph.addSubgraph("trainer").addAttributeNode("name");
            pokemonGraph.removeAttributeNodes(Attribute.PersistentAttributeType.MANY_TO_ONE);
            // Trainer node, which is many-to-one, shall be gone now. Verify nodes content.
            List<AttributeNode<?>> nodes = pokemonGraph.getAttributeNodes();
            Set<String> checkNodes = new HashSet<>(List.of("id", "name", "types"));
            assertEquals(3, nodes.size());
            for (AttributeNode<?> node : nodes) {
                if (checkNodes.contains(node.getAttributeName())) {
                    checkNodes.remove(node.getAttributeName());
                } else {
                    fail(String.format("Node %s was not found in the EntityGraph.", node.getAttributeName()));
                }
            }
            assertTrue(checkNodes.isEmpty());
            // Verify attributes load status with database query
            PersistenceUnitUtil util = em.getEntityManagerFactory().getPersistenceUnitUtil();
            Pokemon pokemon = em.createQuery("SELECT p FROM Pokemon p WHERE p.id = :id", Pokemon.class)
                    .setParameter("id", POKEMONS[1].getId())
                    .setHint(QueryHints.JPA_FETCH_GRAPH, pokemonGraph)
                    .getSingleResult();
            assertTrue("Attribute id is present in EntityGraph but not in result", util.isLoaded(pokemon, "id"));
            assertTrue("Attribute name is present in EntityGraph but not in result", util.isLoaded(pokemon, "name"));
            pokemon.getTypes().stream().findFirst().ifPresent(
                    type -> assertTrue("Attribute types is present in EntityGraph but not in result", util.isLoaded(type, "name")));
            assertFalse("Attribute trainer is not present in EntityGraph but is present in result", util.isLoaded(pokemon.getTrainer(), "name"));
        }
    }

}
