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
package org.eclipse.persistence.testing.tests.advanced2.jpql;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa21.advanced.jpql.CarProductionCompany;
import org.eclipse.persistence.testing.models.jpa21.advanced.jpql.Company;
import org.eclipse.persistence.testing.models.jpa21.advanced.jpql.Continent;
import org.eclipse.persistence.testing.models.jpa21.advanced.jpql.ProductionCompany;

public class JPQLTest extends JUnitTestCase {

    public JPQLTest() {
    }

    public JPQLTest(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
    }

    @Override
    public String getPersistenceUnitName() {
        return "pu-jpql";
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("JPQLTest");
        suite.addTest(new JPQLTest("testJPQLs"));
        return suite;
    }

    public void testJPQLs() {
        EntityManagerFactory emf = getEntityManagerFactory();
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            String jpql = "SELECT continent"
                + " FROM Continent continent "
                + " WHERE NOT EXISTS (SELECT c FROM Company c WHERE c.originCountry MEMBER OF continent.countries)";
            em.createQuery(jpql, Object[].class).getResultList();

            jpql = "SELECT continent"
                + " FROM Continent continent "
                + " WHERE NOT EXISTS (SELECT c FROM ProductionCompany c WHERE c.originCountry MEMBER OF continent.countries)";
            em.createQuery(jpql, Object[].class).getResultList();

            jpql = "SELECT continent"
                + " FROM Continent continent "
                + " WHERE NOT EXISTS (SELECT c FROM CarProductionCompany c WHERE c.originCountry MEMBER OF continent.countries)";
            em.createQuery(jpql, Object[].class).getResultList();
        } finally {
            if (this.isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    @Override
    public void tearDown() {
        EntityManager em = createEntityManager();
        em.getTransaction().begin();
        try {
            em.createQuery("delete from CarProductionCompany c").executeUpdate();
            em.createQuery("delete from Company d").executeUpdate();
            em.createQuery("delete from Continent e").executeUpdate();
            em.createQuery("delete from Country f").executeUpdate();
            em.createQuery("delete from ProductionCompany p").executeUpdate();
            commitTransaction(em);
        } finally {
            if (this.isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }
}
