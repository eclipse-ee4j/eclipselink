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

import junit.framework.Test;
import org.eclipse.persistence.testing.models.jpa.persistence32.Pokemon;
import org.junit.Assert;

import java.util.List;

/**
 * Verify jakarta.persistence 3.2 API changes in queries.
 */
public class NamedNativeQueryTest extends AbstractPokemonSuite {

    // Pokemons. Array index is ID value.
    // Value of ID = 0 does not exist so it's array instance is set to null.
    static final Pokemon[] POKEMONS = new Pokemon[]{
            null, // Skip array index 0
            new Pokemon(1, "Pidgey", List.of(TYPES[1], TYPES[3])),
            new Pokemon(2, "Beedrill", List.of(TYPES[7], TYPES[4])),
            new Pokemon(3, "Squirtle", List.of(TYPES[11])),
            new Pokemon(4, "Caterpie", List.of(TYPES[7]))
    };

    public static Test suite() {
        return suite(
                "QueryTest",
                new NamedNativeQueryTest("testNamedNativeQueryExternalResultSetMapping"),
                new NamedNativeQueryTest("testNamedNativeQueryInternalEntitiesProperty"),
                new NamedNativeQueryTest("testNamedNativeQueryInternalColumnsProperty"),
                new NamedNativeQueryTest("testNamedNativeQueryInternalClassesProperty")
        );
    }

    public NamedNativeQueryTest() {
    }

    public NamedNativeQueryTest(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
    }

    @Override
    public String getPersistenceUnitName() {
        return "persistence32";
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

    public void testNamedNativeQueryExternalResultSetMapping() {
        Pokemon pokemon = (Pokemon) emf.callInTransaction(em -> em
                .createNamedQuery("Pokemon.selectByIdResultSetMapping")
                .setParameter(1, POKEMONS[1].getId())
                .getSingleResult());
        Assert.assertEquals(POKEMONS[1], pokemon);
    }

    public void testNamedNativeQueryInternalEntitiesProperty() {
        Pokemon pokemon = (Pokemon) emf.callInTransaction(em -> em
                .createNamedQuery("Pokemon.selectByIdEntitiesProperty")
                .setParameter(1, POKEMONS[1].getId())
                .getSingleResult());
        Assert.assertEquals(POKEMONS[1], pokemon);
    }

    public void testNamedNativeQueryInternalColumnsProperty() {
        Object[] results = (Object[]) emf.callInTransaction(em -> em
                .createNamedQuery("Pokemon.selectByIdColumnsProperty")
                .setParameter(1, POKEMONS[1].getId())
                .getSingleResult());
        //Converted to String as various databases should return int, long or BigDecimal or other numeric types
        Assert.assertEquals(String.valueOf(POKEMONS[1].getId()), results[0].toString());
        Assert.assertEquals(POKEMONS[1].getName(), results[1]);
    }

    public void testNamedNativeQueryInternalClassesProperty() {
        Pokemon pokemon = (Pokemon) emf.callInTransaction(em -> em
                .createNamedQuery("Pokemon.selectByIdClassesProperty")
                .setParameter(1, POKEMONS[1].getId())
                .getSingleResult());
        Assert.assertEquals(POKEMONS[1].getId(), pokemon.getId());
        Assert.assertEquals(POKEMONS[1].getName(), pokemon.getName());
    }
}
