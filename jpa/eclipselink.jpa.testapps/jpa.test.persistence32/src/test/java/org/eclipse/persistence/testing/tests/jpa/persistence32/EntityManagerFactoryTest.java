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
import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.jpa.JpaEntityManagerFactory;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.persistence32.Persistence32TableCreator;
import org.eclipse.persistence.testing.models.jpa.persistence32.Pokemon;
import org.eclipse.persistence.testing.models.jpa.persistence32.Type;

public class EntityManagerFactoryTest extends JUnitTestCase {

    // Pokemon types. Array index is ID value. Value of ID = 0 does not exist,
    // so it's array instance is set to null.
    private static final Type[] TYPES = new Type[] {
        null,
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
    } ;

    private JpaEntityManagerFactory emf = null;

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("EntityManagerFactoryTest");
        suite.addTest(new EntityManagerFactoryTest("testSetup"));
        suite.addTest(new EntityManagerFactoryTest("testWithApplicationManagedTransaction"));
        suite.addTest(new EntityManagerFactoryTest("testWithApplicationManagedTransactionVoid"));
        suite.addTest(new EntityManagerFactoryTest("testWithUserManagedTransaction"));
        suite.addTest(new EntityManagerFactoryTest("testWithUserManagedTransactionVoid"));
        return suite;
    }

    public EntityManagerFactoryTest() {}

    public EntityManagerFactoryTest(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
    }


    @Override
    public String getPersistenceUnitName() {
        return "persistence32";
    }

    @Override
    public void setUp () {
        super.setUp();
        emf = getEntityManagerFactory(getPersistenceUnitName()).unwrap(EntityManagerFactoryImpl.class);
    }

    @Override
    public void tearDown () {
        super.tearDown();
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow
     * execution in the server.
     */
    public void testSetup() {
        new Persistence32TableCreator().replaceTables(JUnitTestCase.getServerSession(getPersistenceUnitName()));
        clearCache();
        //        emf = getEntityManagerFactory(getPersistenceUnitName());
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction et = em.getTransaction();
            try {
                et.begin();

                for (int i = 1; i < TYPES.length; i++) {
                    em.persist(TYPES[i]);
                }
                et.commit();
            } catch (Exception e) {
                et.rollback();
            }
        }
    }

    public void testWithApplicationManagedTransaction() {
        Pokemon pokemon = emf.withTransaction((em -> {
            Map<Integer, Type> types = new HashMap<>(24);
            em.createNamedQuery("Type.all", Type.class)
                    .getResultList()
                    .forEach(type -> types.put(type.getId(), type));
            Pokemon newPokemon = new Pokemon("Pidgey", List.of(types.get(1), types.get(3)));
            em.persist(newPokemon);
            return newPokemon;
        }));
        verifyObjectInEntityManager(pokemon, getPersistenceUnitName());
    }

    public void testWithApplicationManagedTransactionVoid() {
        Pokemon[] pokemon = new Pokemon[1];
        emf.withTransaction((em -> {
            Map<Integer, Type> types = new HashMap<>(24);
            em.createNamedQuery("Type.all", Type.class)
                    .getResultList()
                    .forEach(type -> types.put(type.getId(), type));
            Pokemon newPokemon = new Pokemon("Beedrill", List.of(types.get(7), types.get(4)));
            em.persist(newPokemon);
            pokemon[0] = newPokemon;
        }));
        verifyObjectInEntityManager(pokemon[0], getPersistenceUnitName());
    }

    public void testWithUserManagedTransaction() {
        Pokemon pokemon = emf.withTransaction(((em, et) -> {
            try {
                Map<Integer, Type> types = new HashMap<>(24);
                em.createNamedQuery("Type.all", Type.class)
                        .getResultList()
                        .forEach(type -> types.put(type.getId(), type));
                Pokemon newPokemon = new Pokemon("Caterpie", List.of(types.get(7)));
                em.persist(newPokemon);
                et.commit();
                return newPokemon;
            } catch (Exception e) {
                et.rollback();
                throw e;
            }
        }));
        verifyObjectInEntityManager(pokemon, getPersistenceUnitName());
    }


    public void testWithUserManagedTransactionVoid() {
        Pokemon[] pokemon = new Pokemon[1];
        emf.withTransaction(((em, et) -> {
            try {
                Map<Integer, Type> types = new HashMap<>(24);
                em.createNamedQuery("Type.all", Type.class)
                        .getResultList()
                        .forEach(type -> types.put(type.getId(), type));
                Pokemon newPokemon = new Pokemon("Squirtle", List.of(types.get(11)));
                em.persist(newPokemon);
                et.commit();
                pokemon[0] = newPokemon;
            } catch (Exception e) {
                et.rollback();
                throw e;
            }
        }));
        verifyObjectInEntityManager(pokemon[0], getPersistenceUnitName());
    }

}
