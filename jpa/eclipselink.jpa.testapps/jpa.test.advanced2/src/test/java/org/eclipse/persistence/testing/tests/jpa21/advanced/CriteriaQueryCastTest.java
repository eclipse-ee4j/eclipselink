/*
 * Copyright (c) 2011, 2022 Oracle and/or its affiliates. All rights reserved.
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
//     tware - initial API and implementation as part of Query Downcast feature
//     02/08/2013-2.5 Chris Delahunt
//       - 374771 - JPA 2.1 TREAT support
package org.eclipse.persistence.testing.tests.jpa21.advanced;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa21.advanced.animals.Animal;
import org.eclipse.persistence.testing.models.jpa21.advanced.animals.Beaver;
import org.eclipse.persistence.testing.models.jpa21.advanced.animals.Rodent;

import java.util.List;

public class CriteriaQueryCastTest extends JUnitTestCase {

    public CriteriaQueryCastTest() {
        super();
    }

    public CriteriaQueryCastTest(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
    }


    @Override
    public String getPersistenceUnitName() {
        return "AnimalsPU";
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("CriteriaQueryCastTest");

        suite.addTest(new CriteriaQueryCastTest("testSetup"));
// Bug 532018 - Can't use org.eclipse.persistence.testing.models.jpa entities in JPA 2.1 test

        suite.addTest(new CriteriaQueryCastTest("testTreatOverInheritance"));
        suite.addTest(new CriteriaQueryCastTest("testTreatOverInheritanceWithCount"));
        suite.addTest(new CriteriaQueryCastTest("testTreatOverInheritanceWithCountJPQL"));
        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        // Force uppercase for Postgres.
        if (getPersistenceUnitServerSession().getPlatform().isPostgreSQL()) {
            getPersistenceUnitServerSession().getLogin().setShouldForceFieldNamesToUpperCase(true);
        }

// Bug 532018 - Can't use org.eclipse.persistence.testing.models.jpa entities in JPA 2.1 test
//        new AdvancedTableCreator().replaceTables(getPersistenceUnitServerSession());
//        new InheritanceTableCreator().replaceTables(getPersistenceUnitServerSession());
//        new InheritedTableManager().replaceTables(getPersistenceUnitServerSession());
//        // Force uppercase for Postgres.
//        if (getPersistenceUnitServerSession().getPlatform().isPostgreSQL()) {
//            getPersistenceUnitServerSession().getLogin().setShouldForceFieldNamesToUpperCase(true);
//        }

        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Animal.initAnimals(em);
            commitTransaction(em);
        } finally {
            if (this.isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }


    /**
     * Test scenario from bug 531726: Treat over 2 subsequent inheritance using
     * direct {@code select}. Using CriteriaQuery to build the query.
     */
    public void testTreatOverInheritance() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<Animal> createQuery = criteriaBuilder.createQuery(Animal.class);
            Root<Animal> from = createQuery.from(Animal.class);

            Root<Rodent> treat1 = criteriaBuilder.treat(from, Rodent.class);
            Root<Beaver> treat2 = criteriaBuilder.treat(from, Beaver.class);

            Predicate equal1 = criteriaBuilder.isNotNull(treat1.get("name"));
            Predicate equal2 = criteriaBuilder.isNotNull(treat2.get("name"));

            createQuery.select(from);
            createQuery.where(equal1, equal2);
            TypedQuery<Animal> query = em.createQuery(createQuery);
            List<Animal> result = query.getResultList();
            assertEquals("Animals count:", 2, result.size());
        } finally {
            if (this.isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    /**
     * Test scenario from bug 531726: Treat over 2 subsequent inheritance using
     * {@code select count}. Using CriteriaQuery to build the query.
     */
    public void testTreatOverInheritanceWithCount() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {

            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<Long> createQuery = criteriaBuilder.createQuery(Long.class);
            Root<Animal> from = createQuery.from(Animal.class);

            Root<Rodent> treat1 = criteriaBuilder.treat(from, Rodent.class);
            Root<Beaver> treat2 = criteriaBuilder.treat(from, Beaver.class);

            Predicate equal1 = criteriaBuilder.isNotNull(treat1.get("name"));
            Predicate equal2 = criteriaBuilder.isNotNull(treat2.get("name"));

            createQuery.select(criteriaBuilder.count(from));
            createQuery.where(equal1, equal2);

            TypedQuery<Long> query = em.createQuery(createQuery);
            Long result = query.getSingleResult();
            assertEquals("Animals count:", 2, result.longValue());
        } finally {
            if (this.isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    /**
     * Test scenario from bug 531726: Treat over 2 subsequent inheritance using {@code select count}.
     * Using JPQL to build the query.
     */
    public void testTreatOverInheritanceWithCountJPQL() {
        if (isOnServer()) {
            return;
        }

        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT count(a.name) " +
                      "FROM Animal a " +
                      "JOIN TREAT (a AS Rodent) r " +
                      "JOIN TREAT (a AS Beaver) b " +
                     "WHERE r IS NOT null " +
                       "AND b IS NOT null ",
                      Long.class);
            Long result = query.getSingleResult();
            assertEquals("Animals count:", 2, result.longValue());
        } finally {
            if (this.isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

}

