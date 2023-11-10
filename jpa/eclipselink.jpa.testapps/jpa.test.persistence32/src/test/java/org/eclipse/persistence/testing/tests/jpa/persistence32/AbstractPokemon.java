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

import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.EntityManager;
import junit.framework.TestSuite;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.jpa.JpaEntityManagerFactory;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.persistence32.Persistence32TableCreator;
import org.eclipse.persistence.testing.models.jpa.persistence32.Trainer;
import org.eclipse.persistence.testing.models.jpa.persistence32.Type;

/**
 * Abstract jUnit test suite with Pokemon model.
 */
public abstract class AbstractPokemon extends JUnitTestCase {

    // Pokemon trainers. Array index is ID value.
    // Value of ID = 0 does not exist so it's array instance is set to null.
    static final Trainer[] TRAINERS = new Trainer[] {
            null, // Skip array index 0
            new Trainer(1, "Ash"),
            new Trainer(2, "Brock")
    };

    // Pokemon types. Array index is ID value.
    // Value of ID = 0 does not exist so it's array instance is set to null.
    static final Type[] TYPES = new Type[] {
            null, // Skip array index 0
            new Type( 1, "Normal"),
            new Type( 2, "Fighting"),
            new Type( 3, "Flying"),
            new Type( 4, "Poison"),
            new Type( 5, "Ground"),
            new Type( 6, "Rock"),
            new Type( 7, "Bug"),
            new Type( 8, "Ghost"),
            new Type( 9, "Steel"),
            new Type(10, "Fire"),
            new Type(11, "Water"),
            new Type(12, "Grass"),
            new Type(13, "Electric"),
            new Type(14, "Psychic"),
            new Type(15, "Ice"),
            new Type(16, "Dragon"),
            new Type(17, "Dark"),
            new Type(18, "Fairy")
    };

    /**
     * Build test suite.
     * Adds model test setup as first and model test cleanup as last test
     * in the returned tests collection.
     *
     * @param name name of the suite
     * @param tests tests to add to the suite
     * @return collection of tests to execute
     */
    static TestSuite suite(String name, AbstractPokemon... tests) {
        TestSuite suite = new TestSuite();
        suite.setName(name);
        suite.addTest(new AbstractPokemon("testSetup") {});
        for (AbstractPokemon test : tests) {
            suite.addTest(test);
        }
        suite.addTest(new AbstractPokemon("testCleanup") {});
        return suite;
    }

    JpaEntityManagerFactory emf = null;

    public AbstractPokemon() {
    }

    public AbstractPokemon(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
    }

    @Override
    public String getPersistenceUnitName() {
        return "persistence32";
    }

    @Override
    public void setUp() {
        super.setUp();
        emf = getEntityManagerFactory(getPersistenceUnitName())
                .unwrap(EntityManagerFactoryImpl.class);
    }

    /**
     * Return all pokemon types as ID {@link Map}.
     *
     * @param em {@link EntityManager} instance to execute the query
     * @return {@link Map} with pokemon types
     */
    Map<Integer, Type> pokemonTypes(EntityManager em) {
        Map<Integer, Type> types = new HashMap<>(TYPES.length);
        em.createNamedQuery("Type.all", Type.class)
                .getResultList()
                .forEach(type -> types.put(type.getId(), type));
        return types;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow
     * execution in the server.
     */
    public void testSetup() {
        new Persistence32TableCreator().replaceTables(JUnitTestCase.getServerSession(getPersistenceUnitName()));
        clearCache();
        emf.runInTransaction(em -> {
            for (int i = 1; i < TRAINERS.length; i++) {
                em.persist(TRAINERS[i]);
            }
            for (int i = 1; i < TYPES.length; i++) {
                em.persist(TYPES[i]);
            }
        });
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow
     * execution in the server.
     */
    public void testCleanup() {
        emf.runInTransaction(em -> {
            em.createNamedQuery("Pokemon.deleteAllTypes").executeUpdate();
            em.createNamedQuery("Pokemon.deleteAll").executeUpdate();
            em.createNamedQuery("Type.deleteAll").executeUpdate();
            em.createNamedQuery("Trainer.deleteAll").executeUpdate();
        });
    }

}
