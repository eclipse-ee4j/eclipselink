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

// Contributors:
//     10/25/2023: Tomas Kraus
//       - New Jakarta Persistence 3.2 Features
package org.eclipse.persistence.testing.tests.jpa.persistence32;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import junit.framework.Test;
import org.eclipse.persistence.testing.models.jpa.persistence32.Pokemon;
import org.eclipse.persistence.testing.models.jpa.persistence32.Trainer;

/**
 * Verify jakarta.persistence 3.2 API changes in {@link CriteriaBuilder} for union expressions.
 */
public class UnionCriteriaQueryTest extends AbstractPokemonSuite {

    // Pokemons. Array index is ID value.
    // Value of ID = 0 does not exist so it's array instance is set to null.
    static final Pokemon[] POKEMONS = new Pokemon[] {
            null, // Skip array index 0
            new Pokemon(1, TRAINERS[1], "Pidgey", List.of(TYPES[1], TYPES[3])),
            new Pokemon(2, TRAINERS[1], "Beedrill", List.of(TYPES[7], TYPES[4])),
            new Pokemon(3, TRAINERS[1], "Squirtle", List.of(TYPES[11])),
            new Pokemon(4, TRAINERS[1], "Caterpie", List.of(TYPES[7])),
            new Pokemon(5, TRAINERS[1], "Charmander", List.of(TYPES[10])),
            new Pokemon(6, TRAINERS[2], "Rattata", List.of(TYPES[1])),
            new Pokemon(7, TRAINERS[2], "Raticate", List.of(TYPES[1])),
            new Pokemon(8, TRAINERS[2], "Spearow", List.of(TYPES[1], TYPES[3])),
            new Pokemon(9, TRAINERS[1], "Pikachu", List.of(TYPES[13]))
    };

    public static Test suite() {
        return suite(
                "QueryTest",
                new UnionCriteriaQueryTest("testUnionWithNoSelection"),
                new UnionCriteriaQueryTest("testUnionAllWithNoSelection"),
                new UnionCriteriaQueryTest("testIntersectWithNoSelection"),
                new UnionCriteriaQueryTest("testIntersectAllWithNoSelection"),
                new UnionCriteriaQueryTest("testExceptWithNoSelection"),
                new UnionCriteriaQueryTest("testExceptAllWithNoSelection"),
                new UnionCriteriaQueryTest("testUnionWithEntityParameterInSelection"),
                new UnionCriteriaQueryTest("testUnionWithMultiselectEntityParametersInSelection"),
                new UnionCriteriaQueryTest("testUnionWithMultiselectEntityParametersInSelectionResultTypePartial"),
                new UnionCriteriaQueryTest("testUnionWithMultiselectEntityInSelection")
        );
    }

    public UnionCriteriaQueryTest() {
    }

    public UnionCriteriaQueryTest(String name) {
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

    // Test UNION: Shall return distinct values from both queries: 1x Pokemon[1..3]
    // Pokemon[1] matches WHERE clause in both selects
    public void testUnionWithNoSelection() {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction et = em.getTransaction();
            try {
                et.begin();
                CriteriaBuilder cb = em.getCriteriaBuilder();

                CriteriaQuery<Pokemon> q1 = cb.createQuery(Pokemon.class);
                Root<Pokemon> root1 = q1.from(Pokemon.class);
                q1.where(cb.or(
                        cb.equal(root1.get("name"), cb.parameter(String.class, "name1")),
                        cb.equal(root1.get("name"), cb.parameter(String.class, "name2"))));

                CriteriaQuery<Pokemon> q2 = cb.createQuery(Pokemon.class);
                Root<Pokemon> root2 = q2.from(Pokemon.class);
                q2.where(cb.or(
                        cb.equal(root2.get("name"), cb.parameter(String.class, "name1")),
                        cb.equal(root2.get("name"), cb.parameter(String.class, "name3"))));

                TypedQuery<Pokemon> query = em.createQuery(cb.union(q1, q2));
                query.setParameter("name1", POKEMONS[1].getName());
                query.setParameter("name2", POKEMONS[2].getName());
                query.setParameter("name3", POKEMONS[3].getName());
                List<Pokemon> pokemons = query.getResultList();
                assertEquals(3, pokemons.size());
                verifyValuesOnce(Set.of(POKEMONS[1], POKEMONS[2], POKEMONS[3]), pokemons);
            } catch (Exception e) {
                et.rollback();
                throw e;
            }
        }
    }

    // Test UNION ALL: Shall return all values from both queries: 2x Pokemon[1], 1x Pokemon[2..3]
    // Pokemon[1] matches WHERE clause in both selects
    public void testUnionAllWithNoSelection() {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction et = em.getTransaction();
            try {
                et.begin();
                CriteriaBuilder cb = em.getCriteriaBuilder();

                CriteriaQuery<Pokemon> q1 = cb.createQuery(Pokemon.class);
                Root<Pokemon> root1 = q1.from(Pokemon.class);
                q1.where(cb.or(
                        cb.equal(root1.get("name"), cb.parameter(String.class, "name1")),
                        cb.equal(root1.get("name"), cb.parameter(String.class, "name2"))));

                CriteriaQuery<Pokemon> q2 = cb.createQuery(Pokemon.class);
                Root<Pokemon> root2 = q2.from(Pokemon.class);
                q2.where(cb.or(
                        cb.equal(root2.get("name"), cb.parameter(String.class, "name1")),
                        cb.equal(root2.get("name"), cb.parameter(String.class, "name3"))));

                TypedQuery<Pokemon> query = em.createQuery(cb.unionAll(q1, q2));
                query.setParameter("name1", POKEMONS[1].getName());
                query.setParameter("name2", POKEMONS[2].getName());
                query.setParameter("name3", POKEMONS[3].getName());
                List<Pokemon> pokemons = query.getResultList();
                assertEquals(4, pokemons.size());
                verifyValuesMultiple(Set.of(POKEMONS[1], POKEMONS[2], POKEMONS[3]), pokemons);
            } catch (Exception e) {
                et.rollback();
                throw e;
            }
        }
    }

    // Test INTERSECT: Shall return the same distinct values from both queries: 1x Pokemon[1]
    // Pokemon[1] matches WHERE clause in both selects
    public void testIntersectWithNoSelection() {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction et = em.getTransaction();
            try {
                et.begin();
                CriteriaBuilder cb = em.getCriteriaBuilder();

                CriteriaQuery<Pokemon> q1 = cb.createQuery(Pokemon.class);
                Root<Pokemon> root1 = q1.from(Pokemon.class);
                q1.where(cb.or(
                        cb.equal(root1.get("name"), cb.parameter(String.class, "name1")),
                        cb.equal(root1.get("name"), cb.parameter(String.class, "name2"))));

                CriteriaQuery<Pokemon> q2 = cb.createQuery(Pokemon.class);
                Root<Pokemon> root2 = q2.from(Pokemon.class);
                q2.where(cb.or(
                        cb.equal(root2.get("name"), cb.parameter(String.class, "name1")),
                        cb.equal(root2.get("name"), cb.parameter(String.class, "name3"))));

                TypedQuery<Pokemon> query = em.createQuery(cb.intersect(q1, q2));
                query.setParameter("name1", POKEMONS[1].getName());
                query.setParameter("name2", POKEMONS[2].getName());
                query.setParameter("name3", POKEMONS[3].getName());
                List<Pokemon> pokemons = query.getResultList();
                assertEquals(1, pokemons.size());
                verifyValuesOnce(Set.of(POKEMONS[1]), pokemons);
            } catch (Exception e) {
                et.rollback();
                throw e;
            }
        }
    }

    // Test INTERSECT ALL: Shall return the same values from both queries including duplicates: 2x Pokemon[2..3]
    // q1 and q2 are UNION ALL to produce duplicated records 2x Pokemon[1..3]
    // q3 and q4 are UNION ALL to produce duplicated records 2x Pokemon[1, 4]
    // (q1 UNION ALL q2) INTERSECT ALL (q3 UNION ALL q4) shall produce 2x Pokemon[2..3] - q1, q2 values not in q3, q4
    // (
    //   (
    //     SELECT t0.ID, t0.NAME FROM PERSISTENCE32_POKEMON t0 WHERE (t0.NAME IN (?, ?, ?))
    //     UNION ALL (SELECT t1.ID, t1.NAME FROM PERSISTENCE32_POKEMON t1 WHERE (t1.NAME IN (?, ?, ?)))
    //   )
    //   INTERSECT ALL (
    //         (SELECT t2.ID, t2.NAME FROM PERSISTENCE32_POKEMON t2 WHERE ((t2.NAME = ?) OR (t2.NAME = ?))
    //         UNION ALL (SELECT t3.ID, t3.NAME FROM PERSISTENCE32_POKEMON t3 WHERE ((t3.NAME = ?) OR (t3.NAME = ?))))
    //   )
    // )
    public void testIntersectAllWithNoSelection() {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction et = em.getTransaction();
            try {
                et.begin();
                CriteriaBuilder cb = em.getCriteriaBuilder();

                CriteriaQuery<Pokemon> q1 = cb.createQuery(Pokemon.class);
                Root<Pokemon> root1 = q1.from(Pokemon.class);
                q1.where(root1.get("name")
                                 .in(cb.parameter(String.class, "name1"),
                                     cb.parameter(String.class, "name2"),
                                     cb.parameter(String.class, "name3")));

                CriteriaQuery<Pokemon> q2 = cb.createQuery(Pokemon.class);
                Root<Pokemon> root2 = q2.from(Pokemon.class);
                q2.where(root2.get("name")
                                 .in(cb.parameter(String.class, "name1"),
                                     cb.parameter(String.class, "name2"),
                                     cb.parameter(String.class, "name3")));

                CriteriaQuery<Pokemon> q3 = cb.createQuery(Pokemon.class);
                Root<Pokemon> root3 = q3.from(Pokemon.class);
                q3.where(cb.or(
                        cb.equal(root3.get("name"), cb.parameter(String.class, "name1")),
                        cb.equal(root3.get("name"), cb.parameter(String.class, "name4"))));

                CriteriaQuery<Pokemon> q4 = cb.createQuery(Pokemon.class);
                Root<Pokemon> root4 = q4.from(Pokemon.class);
                q4.where(cb.or(
                        cb.equal(root4.get("name"), cb.parameter(String.class, "name1")),
                        cb.equal(root4.get("name"), cb.parameter(String.class, "name4"))));

                TypedQuery<Pokemon> query = em.createQuery(cb.intersectAll(cb.unionAll(q1, q2), cb.unionAll(q3, q4)));
                query.setParameter("name1", POKEMONS[1].getName());
                query.setParameter("name2", POKEMONS[2].getName());
                query.setParameter("name3", POKEMONS[3].getName());
                query.setParameter("name4", POKEMONS[4].getName());
                List<Pokemon> pokemons = query.getResultList();
                assertEquals(2, pokemons.size());
                verifyValuesMultiple(Set.of(POKEMONS[1]), pokemons);
            } catch (Exception e) {
                et.rollback();
                throw e;
            }
        }
    }

    // Test EXCEPT: Shall return distinct values from q1 not in q2: 1x Pokemon[2..3]
    // Pokemon[1] matches WHERE clause in both selects
    public void testExceptWithNoSelection() {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction et = em.getTransaction();
            try {
                et.begin();
                CriteriaBuilder cb = em.getCriteriaBuilder();

                CriteriaQuery<Pokemon> q1 = cb.createQuery(Pokemon.class);
                Root<Pokemon> root1 = q1.from(Pokemon.class);
                q1.where(root1.get("name")
                                 .in(cb.parameter(String.class, "name1"),
                                     cb.parameter(String.class, "name2"),
                                     cb.parameter(String.class, "name3")));

                CriteriaQuery<Pokemon> q2 = cb.createQuery(Pokemon.class);
                Root<Pokemon> root2 = q2.from(Pokemon.class);
                q2.where(cb.or(
                        cb.equal(root2.get("name"), cb.parameter(String.class, "name1")),
                        cb.equal(root2.get("name"), cb.parameter(String.class, "name4"))));

                TypedQuery<Pokemon> query = em.createQuery(cb.except(q1, q2));
                query.setParameter("name1", POKEMONS[1].getName());
                query.setParameter("name2", POKEMONS[2].getName());
                query.setParameter("name3", POKEMONS[3].getName());
                query.setParameter("name4", POKEMONS[4].getName());
                List<Pokemon> pokemons = query.getResultList();
                assertEquals(2, pokemons.size());
                verifyValuesOnce(Set.of(POKEMONS[2], POKEMONS[3]), pokemons);
            } catch (Exception e) {
                et.rollback();
                throw e;
            }
        }
    }

    // Test EXCEPT: Shall return values from q1 not in q2 including duplicates: 2x Pokemon[1..3]
    // q1 and q2 are UNION ALL to produce duplicated records 2x Pokemon[1..3]
    // q3 produces 1x Pokemon[1, 4]
    // (
    //   (
    //     SELECT t0.ID, t0.NAME FROM PERSISTENCE32_POKEMON t0 WHERE (t0.NAME IN (?, ?, ?))
    //     UNION ALL (SELECT t1.ID, t1.NAME FROM PERSISTENCE32_POKEMON t1 WHERE (t1.NAME IN (?, ?, ?)))
    //   )
    //   EXCEPT ALL (SELECT t2.ID, t2.NAME FROM PERSISTENCE32_POKEMON t2 WHERE ((t2.NAME = ?) OR (t2.NAME = ?)))
    // )
    public void testExceptAllWithNoSelection() {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction et = em.getTransaction();
            try {
                et.begin();
                CriteriaBuilder cb = em.getCriteriaBuilder();

                CriteriaQuery<Pokemon> q1 = cb.createQuery(Pokemon.class);
                Root<Pokemon> root1 = q1.from(Pokemon.class);
                q1.where(root1.get("name")
                                 .in(cb.parameter(String.class, "name1"),
                                     cb.parameter(String.class, "name2"),
                                     cb.parameter(String.class, "name3")));

                CriteriaQuery<Pokemon> q2 = cb.createQuery(Pokemon.class);
                Root<Pokemon> root2 = q2.from(Pokemon.class);
                q2.where(root2.get("name")
                                 .in(cb.parameter(String.class, "name1"),
                                     cb.parameter(String.class, "name2"),
                                     cb.parameter(String.class, "name3")));

                CriteriaQuery<Pokemon> q3 = cb.createQuery(Pokemon.class);
                Root<Pokemon> root3 = q3.from(Pokemon.class);
                q3.where(cb.or(
                        cb.equal(root3.get("name"), cb.parameter(String.class, "name1")),
                        cb.equal(root3.get("name"), cb.parameter(String.class, "name4"))));

                TypedQuery<Pokemon> query = em.createQuery(cb.exceptAll(cb.unionAll(q1, q2), q3));
                query.setParameter("name1", POKEMONS[1].getName());
                query.setParameter("name2", POKEMONS[2].getName());
                query.setParameter("name3", POKEMONS[3].getName());
                query.setParameter("name4", POKEMONS[4].getName());
                List<Pokemon> pokemons = query.getResultList();
                assertEquals(5, pokemons.size());
                verifyValuesMultiple(Set.of(POKEMONS[1], POKEMONS[2], POKEMONS[3]), pokemons);
            } catch (Exception e) {
                et.rollback();
                throw e;
            }
        }
    }

    public void testUnionWithEntityParameterInSelection() {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction et = em.getTransaction();
            try {
                et.begin();
                CriteriaBuilder cb = em.getCriteriaBuilder();

                CriteriaQuery<Pokemon> q1 = cb.createQuery(Pokemon.class);
                Root<Pokemon> root1 = q1.from(Pokemon.class);
                q1.select(root1.get("name"));
                q1.where(cb.equal(root1.get("name"), cb.parameter(String.class, "name1")));

                CriteriaQuery<Pokemon> q2 = cb.createQuery(Pokemon.class);
                Root<Pokemon> root2 = q2.from(Pokemon.class);
                q2.select(root2.get("name"));
                q2.where(cb.equal(root2.get("name"), cb.parameter(String.class, "name2")));

                TypedQuery<Pokemon> query = em.createQuery(cb.union(q1, q2));
                query.setParameter("name1", POKEMONS[1].getName());
                query.setParameter("name2", POKEMONS[2].getName());
                List<Pokemon> pokemons = query.getResultList();
                assertEquals(2, pokemons.size());
            } catch (Exception e) {
                et.rollback();
                throw e;
            }
        }
    }

    // Requires Pokemon(int id, String name) constructor to set ResultType.CONSTRUCTOR in multiselect call
    @SuppressWarnings("deprecation")
    public void testUnionWithMultiselectEntityParametersInSelection() {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction et = em.getTransaction();
            try {
                et.begin();
                CriteriaBuilder cb = em.getCriteriaBuilder();

                CriteriaQuery<Pokemon> q1 = cb.createQuery(Pokemon.class);
                Root<Pokemon> root1 = q1.from(Pokemon.class);
                q1.multiselect(root1.get("id"), root1.get("name"));
                q1.where(cb.equal(root1.get("name"), cb.parameter(String.class, "name1")));

                CriteriaQuery<Pokemon> q2 = cb.createQuery(Pokemon.class);
                Root<Pokemon> root2 = q2.from(Pokemon.class);
                q2.multiselect(root2.get("id"), root2.get("name"));
                q2.where(cb.equal(root2.get("name"), cb.parameter(String.class, "name2")));

                TypedQuery<Pokemon> query = em.createQuery(cb.union(q1, q2));
                query.setParameter("name1", POKEMONS[1].getName());
                query.setParameter("name2", POKEMONS[2].getName());
                List<Pokemon> pokemons = query.getResultList();
                assertEquals(2, pokemons.size());
            } catch (Exception e) {
                et.rollback();
                throw e;
            }
        }
    }

    // Selects partial content of existing Trainer constructor prototype
    // This shall trigger compound query builder with ResultType.PARTIAL.
    @SuppressWarnings("deprecation")
    public void testUnionWithMultiselectEntityParametersInSelectionResultTypePartial() {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction et = em.getTransaction();
            try {
                et.begin();
                CriteriaBuilder cb = em.getCriteriaBuilder();

                CriteriaQuery<Trainer> q1 = cb.createQuery(Trainer.class);
                Root<Trainer> root1 = q1.from(Trainer.class);
                q1.multiselect(root1.get("id"), root1.get("name"));
                q1.where(cb.equal(root1.get("name"), cb.parameter(String.class, "name1")));

                CriteriaQuery<Trainer> q2 = cb.createQuery(Trainer.class);
                Root<Trainer> root2 = q2.from(Trainer.class);
                q2.multiselect(root2.get("id"), root2.get("name"));
                q2.where(cb.equal(root2.get("name"), cb.parameter(String.class, "name2")));

                TypedQuery<Trainer> query = em.createQuery(cb.union(q1, q2));
                query.setParameter("name1", TRAINERS[1].getName());
                query.setParameter("name2", TRAINERS[2].getName());
                List<Trainer> trainers = query.getResultList();
                assertEquals(2, trainers.size());
            } catch (Exception e) {
                et.rollback();
                throw e;
            }
        }
    }

    public void testUnionWithMultiselectEntityInSelection() {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction et = em.getTransaction();
            try {
                et.begin();
                CriteriaBuilder cb = em.getCriteriaBuilder();

                CriteriaQuery<Trainer> q1 = cb.createQuery(Trainer.class);
                Root<Pokemon> root1 = q1.from(Pokemon.class);
                Join<Pokemon, Trainer> trainer1 = root1.join(Trainer.class);
                q1.select(trainer1);
                q1.where(cb.equal(root1.get("name"), cb.parameter(String.class, "name1")));

                CriteriaQuery<Trainer> q2 = cb.createQuery(Trainer.class);
                Root<Pokemon> root2 = q2.from(Pokemon.class);
                Join<Pokemon, Trainer> trainer2 = root2.join(Trainer.class);
                q2.select(trainer2);
                q2.where(cb.equal(root2.get("name"), cb.parameter(String.class, "name2")));

                TypedQuery<Trainer> query = em.createQuery(cb.union(q1, q2));
                query.setParameter("name1", POKEMONS[8].getName());
                query.setParameter("name2", POKEMONS[9].getName());
                List<Trainer> pokemons = query.getResultList();
                assertEquals(2, pokemons.size());
            } catch (Exception e) {
                et.rollback();
                throw e;
            }
        }
    }

    private static void verifyValuesOnce(Set<Pokemon> expected, List<Pokemon> queryResult) {
        Set<Pokemon> check = new HashSet<>(expected);
        for (Pokemon pokemon : queryResult) {
            assertTrue(String.format("Pokemon %d:%s was not found in Set %s", pokemon.getId(), pokemon.getName(), expected),
                       check.contains(pokemon));
            check.remove(pokemon);
        }
    }

    private static void verifyValuesMultiple(Set<Pokemon> expected, List<Pokemon> queryResult) {
        for (Pokemon pokemon : queryResult) {
            assertTrue(String.format("Pokemon %d:%s was not found in Set %s", pokemon.getId(), pokemon.getName(), expected),
                       expected.contains(pokemon));
        }
    }

}
