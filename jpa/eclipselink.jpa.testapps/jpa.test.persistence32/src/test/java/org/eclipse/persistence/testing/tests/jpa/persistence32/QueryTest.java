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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
import java.util.stream.Stream;
import junit.framework.Test;
import org.eclipse.persistence.testing.models.jpa.persistence32.Pokemon;
import org.junit.Assert;

/**
 * Verify jakarta.persistence 3.2 API changes in queries.
 */
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

    static final long POKEMONS_COUNT = POKEMONS.length -1; // we ignore the first one with index 0

    public static Test suite() {
        return suite(
                "QueryTest",
                new QueryTest("testGetSingleResultWithEmptyResult"),
                new QueryTest("testGetSingleResultWithSingleResult"),
                new QueryTest("testGetSingleResultWithMultipleResults"),
                new QueryTest("testGetSingleResultOrNullWithEmptyResult"),
                new QueryTest("testGetSingleResultOrNullWithSingleResult"),
                new QueryTest("testGetSingleResultOrNullWithMultipleResults"),
                new QueryTest("testUpdateQueryLengthInAssignmentAndExpression"),
                new QueryTest("tesUpdateQueryWithThisVariable"),
                new QueryTest("testSelectQueryLengthInAssignmentAndExpression"),
                new QueryTest("testDeleteQueryLengthInExpressionOnLeft"),
                new QueryTest("testDeleteQueryLengthInExpressionOnRight")
        );
    }

    public QueryTest() {
    }

    public QueryTest(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
    }

    // Initialize data for each test
    @Override
    public void setUp() {
        super.setUp();
        emf.runInTransaction(em -> {
            em.createQuery("DELETE FROM Pokemon").executeUpdate();
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

    public void testUpdateQueryLengthInAssignmentAndExpression() {
        testUpdateAllPokemons("UPDATE Pokemon SET length = length + 1");
    }

    public void tesUpdateQueryWithThisVariable() {
        testUpdateAllPokemons("UPDATE Pokemon SET length = this.length + 1");
    }

    private void testUpdateAllPokemons(String query) {
        long numberOfChanges = emf.callInTransaction(em -> em.createQuery(query).executeUpdate());
        assertThat("All pokemons should be updated", numberOfChanges, is(equalTo(POKEMONS_COUNT)));

        long numberOfPokemonsWithLengthChanged = getAllPokemons()
                .filter(pokemon -> pokemon.getLength() == 1)
                .count();
        assertThat("All pokemons should have increased length", numberOfPokemonsWithLengthChanged, is(equalTo(POKEMONS_COUNT)));
    }

    public void testSelectQueryLengthInAssignmentAndExpression() {
        List<Pokemon> pokemonsWithIdOne  = emf.callInTransaction(em -> em.createQuery(
                "SELECT this FROM Pokemon WHERE id + length = length + 1", Pokemon.class).getResultList());
        assertThat("Number of pokemons with ID = 1", pokemonsWithIdOne.size(), is(equalTo(1)));
    }

    public void testDeleteQueryLengthInExpressionOnLeft() {
        assertThat("Number of remaining pokemons", getAllPokemons().count(), is(equalTo(POKEMONS_COUNT)));
        int numberOfChanges = emf.callInTransaction(em -> em.createQuery(
                "DELETE FROM Pokemon WHERE length = id - 1").executeUpdate());
        assertThat("Number of pokemons with ID = 1 deleted", numberOfChanges, is(equalTo(1)));
        assertThat("Number of remaining pokemons", getAllPokemons().count(), is(equalTo(POKEMONS_COUNT - 1)));
    }

    public void testDeleteQueryLengthInExpressionOnRight() {
        assertThat("Number of remaining pokemons", getAllPokemons().count(), is(equalTo(POKEMONS_COUNT)));
        int numberOfChanges = emf.callInTransaction(em -> em.createQuery(
                "DELETE FROM Pokemon WHERE id = length + 1").executeUpdate());
        assertThat("Number of pokemons with ID = 1 deleted", numberOfChanges, is(equalTo(1)));
        assertThat("Number of remaining pokemons", getAllPokemons().count(), is(equalTo(POKEMONS_COUNT - 1)));
    }

    private Stream<Pokemon> getAllPokemons() {
        return emf.callInTransaction(em -> em.createQuery(
                "SELECT p FROM Pokemon p", Pokemon.class).getResultStream());
    }

}
