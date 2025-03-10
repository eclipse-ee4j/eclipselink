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
package org.eclipse.persistence.testing.tests.jpa.jpql.advanced;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.CarProductionCompany;
import org.eclipse.persistence.testing.models.jpa.advanced.Continent;
import org.eclipse.persistence.testing.models.jpa.advanced.Country;
import org.eclipse.persistence.testing.models.jpa.advanced.Company;
import org.eclipse.persistence.testing.models.jpa.advanced.ProductionCompany;

import org.junit.Assert;

public class JUnitJPQLExpressionTest extends JUnitTestCase {

    private Country country1, country2, country3;

    public JUnitJPQLExpressionTest() {
    }

    public JUnitJPQLExpressionTest(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
    }

    @Override
    public String getPersistenceUnitName() {
        return "pu-jpql";
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("JUnitJPQLExpressionTest");
        suite.addTest(new JUnitJPQLExpressionTest("testWithCompany"));
        suite.addTest(new JUnitJPQLExpressionTest("testWithProductionCompany"));
        suite.addTest(new JUnitJPQLExpressionTest("testWithCarProductionCompany"));
        return suite;
    }

    @Override
    public void setUp() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            country1 = new Country();
            country1.setId("France");
            em.persist(country1);

            country2 = new Country();
            country2.setId("Germany");
            em.persist(country2);

            country3 = new Country();
            country3.setId("USA");
            em.persist(country3);

            Continent continent1 = new Continent();
            continent1.setId("Europe");
            List<Country> countries  = new ArrayList<>();
            countries.add(country1);
            countries.add(country2);
            continent1.setCountries(countries);
            em.persist(continent1);

            Continent continent2 = new Continent();
            continent2.setId("North America");
            List<Country> country  = new ArrayList<>();
            country.add(country3);
            continent2.setCountries(country);
            em.persist(continent2);

            commitTransaction(em);
        } finally {
            if (this.isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    public void testWithCompany() {
        EntityManagerFactory emf = getEntityManagerFactory();
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Company company = new Company();
            company.setId("FrenchCompany");
            company.setCountry(country1);
            em.persist(company);

            String jpql = "SELECT continent"
                + " FROM Continent continent "
                + " WHERE NOT EXISTS (SELECT c FROM Company c WHERE c.originCountry MEMBER OF continent.countries)";
            List<?> result = em.createQuery(jpql, Object[].class).getResultList();
            Assert.assertEquals(1, result.size());
            Continent continent = (Continent) result.get(0);
            Assert.assertEquals("North America", continent.getId());

        } catch (Exception e) {
            fail("Error occurred in test: " + e);
        } finally {
            if (this.isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    public void testWithProductionCompany() {
        EntityManagerFactory emf = getEntityManagerFactory();
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            ProductionCompany productionCompany = new ProductionCompany();
            productionCompany.setProductionCompanyName("USAProductionCompany");
            productionCompany.setId("USAProductionCompany");
            productionCompany.setCountry(country3);
            em.persist(productionCompany);

            String jpql = "SELECT continent"
                + " FROM Continent continent "
                + " WHERE NOT EXISTS (SELECT c FROM ProductionCompany c WHERE c.originCountry MEMBER OF continent.countries)";
            List<?> result = em.createQuery(jpql, Object[].class).getResultList();
            Assert.assertEquals(1, result.size());
            Continent continent = (Continent) result.get(0);
            Assert.assertEquals("Europe", continent.getId());

        } catch (Exception e) {
            fail("Error occurred in test: " + e);
        } finally {
            if (this.isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    public void testWithCarProductionCompany() {
        EntityManagerFactory emf = getEntityManagerFactory();
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            CarProductionCompany carProductionCompany = new CarProductionCompany();
            carProductionCompany.setCarProductionCompanyName("GermanCarProductionCompany");
            carProductionCompany.setProductionCompanyName("GermanCarProductionCompany");
            carProductionCompany.setId("GermanCarProductionCompany");
            carProductionCompany.setCountry(country2);
            em.persist(carProductionCompany);

            String jpql = "SELECT continent"
                + " FROM Continent continent "
                + " WHERE NOT EXISTS (SELECT c FROM CarProductionCompany c WHERE c.originCountry MEMBER OF continent.countries)";
            List<?> result = em.createQuery(jpql, Object[].class).getResultList();
            Assert.assertEquals(1, result.size());
            Continent continent = (Continent) result.get(0);
            Assert.assertEquals("North America", continent.getId());

        } catch (Exception e) {
            fail("Error occurred in test: " + e);
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
