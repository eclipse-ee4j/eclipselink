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
import java.util.Map;

import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
import junit.framework.Test;
import org.eclipse.persistence.testing.models.jpa.persistence32.Pokemon;
import org.junit.Assert;

public class QueryTest extends AbstractPokemon {

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
                "QueryTest",
                new QueryTest("testSetup"),
                new QueryTest("testGetSingleResultWithEmptyResult"),
                new QueryTest("testGetSingleResultWithSingleResult"),
                new QueryTest("testGetSingleResultWithMultipleResults"),
                new QueryTest("testGetSingleResultOrNullWithEmptyResult"),
                new QueryTest("testGetSingleResultOrNullWithSingleResult"),
                new QueryTest("testGetSingleResultOrNullWithMultipleResults")
        );
    }

    Map<Integer, Pokemon> pokemons = null;

    public QueryTest() {
    }

    public QueryTest(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow
     * execution in the server.
     */
    public void testSetup() {
        emf.runInTransaction(em -> {
            for (int i = 1; i < POKEMONS.length; i++) {
                em.persist(POKEMONS[i]);
            }
        });
    }

    public void testGetSingleResultWithEmptyResult() {
        Assert.assertThrows(
                "Query pokemon with not existing name shall throw an exception",
                NoResultException.class,
                () -> emf.runInTransaction(em -> em.createQuery(
                        "SELECT p FROM Pokemon p WHERE p.name = 'Charizard'", Pokemon.class).getSingleResult())
        );
    }

    public void testGetSingleResultWithSingleResult() {
        Pokemon pokemon = emf.callInTransaction(em -> em.createQuery(
                "SELECT p FROM Pokemon p WHERE p.name = 'Pidgey'", Pokemon.class).getSingleResult());
        Assert.assertNotNull("Query pokemon with existing name shall not return null", pokemon);
    }

    public void testGetSingleResultWithMultipleResults() {
        Assert.assertThrows(
                "Query all pokemons shall throw an exception",
                NonUniqueResultException.class,
                () -> emf.callInTransaction(em -> em.createQuery(
                        "SELECT p FROM Pokemon p ", Pokemon.class).getSingleResult()));
    }

    public void testGetSingleResultOrNullWithEmptyResult() {
        Pokemon pokemon = emf.callInTransaction(em -> em.createQuery(
                "SELECT p FROM Pokemon p WHERE p.name = 'Charizard'", Pokemon.class).getSingleResultOrNull());
        Assert.assertNull("Query pokemon with not existing name shall return null", pokemon);
    }

    public void testGetSingleResultOrNullWithSingleResult() {
        Pokemon pokemon = emf.callInTransaction(em -> em.createQuery(
                "SELECT p FROM Pokemon p WHERE p.name = 'Pidgey'", Pokemon.class).getSingleResultOrNull());
        Assert.assertNotNull("Query pokemon with existing name shall not return null", pokemon);
    }

    public void testGetSingleResultOrNullWithMultipleResults() {
        Assert.assertThrows(
                "Query all pokemons shall throw an exception",
                NonUniqueResultException.class,
                () -> emf.callInTransaction(em -> em.createQuery(
                        "SELECT p FROM Pokemon p ", Pokemon.class).getSingleResultOrNull()));
    }

}
