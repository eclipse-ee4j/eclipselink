/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 1998, 2025 IBM Corporation. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
//     03/03/2010 - 2.1 Michael O'Brien
//       - 302316: clear the object cache when testing stored procedure returns on SQLServer
//         to avoid false positives visible only when debugging in DatabaseCall.buildOutputRow()
//       - 260263: SQLServer 2005/2008 requires stored procedure creation select clause variable and column name matching
//     06/16/2010-2.2 Guy Pelletier
//       - 247078: eclipselink-orm.xml schema should allow lob and enumerated on version and id mappings
//     09/03/2010-2.2 Guy Pelletier
//       - 317286: DB column lenght not in sync between @Column and @JoinColumn
//     10/15/2010-2.2 Guy Pelletier
//       - 322008: Improve usability of additional criteria applied to queries at the session/EM
//     10/27/2010-2.2 Guy Pelletier
//       - 328114: @AttributeOverride does not work with nested embeddables having attributes of the same name
//     11/01/2010-2.2 Guy Pelletier
//       - 322916: getParameter on Query throws NPE
//     08/07/2016-2.7 Dalia Abo Sheasha
//       - 499335: Multiple embeddable fields can't reference same object
//     09/04/2018-3.0 Ravi Babu Tummuru
//       - 538183: SETTING QUERYHINTS.CURSOR ON A NAMEDQUERY THROWS QUERYEXCEPTION

package org.eclipse.persistence.testing.tests.jpa.advanced.additionalcriteria;

import jakarta.persistence.EntityManager;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.additionalcriteria.Bolt;
import org.eclipse.persistence.testing.models.jpa.advanced.additionalcriteria.Eater;
import org.eclipse.persistence.testing.models.jpa.advanced.additionalcriteria.Nut;
import org.eclipse.persistence.testing.models.jpa.advanced.additionalcriteria.Rabbit;
import org.eclipse.persistence.testing.models.jpa.advanced.additionalcriteria.RabbitFoot;
import org.eclipse.persistence.testing.models.jpa.advanced.additionalcriteria.Sandwich;
import org.eclipse.persistence.testing.models.jpa.advanced.additionalcriteria.School;
import org.eclipse.persistence.testing.models.jpa.advanced.additionalcriteria.Student;

import java.util.List;


/**
 * This test suite tests EclipseLink JPA annotations extensions.
 */
public class AdvancedJPAJunitTest extends JUnitTestCase {

    public AdvancedJPAJunitTest() {
        super();
    }

    public AdvancedJPAJunitTest(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
    }

    @Override
    public String getPersistenceUnitName() {
        return "additional-criteria";
    }

    @Override
    public void setUp() {
        super.setUp();
        clearCache();
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("AdvancedJPAJunitTest - additional-criteria");

        // These tests use JPA 2.0 entity manager API
        suite.addTest(new AdvancedJPAJunitTest("testAdditionalCriteriaModelPopulate"));
        suite.addTest(new AdvancedJPAJunitTest("testAdditionalCriteria"));
        suite.addTest(new AdvancedJPAJunitTest("testAdditionalCriteriaWithParameterFromEM1"));
        suite.addTest(new AdvancedJPAJunitTest("testAdditionalCriteriaWithParameterFromEM2"));
        suite.addTest(new AdvancedJPAJunitTest("testAdditionalCriteriaWithParameterFromEMF"));
        suite.addTest(new AdvancedJPAJunitTest("testComplexAdditionalCriteria"));
        suite.addTest(new AdvancedJPAJunitTest("testAdditionalCriteriaBetweenEntities"));
        suite.addTest(new AdvancedJPAJunitTest("testAdditionalCriteriaWithSubQuery"));

        return suite;
    }

    /**
     * Test user defined additional criteria with no parameters.
     */
    public void testAdditionalCriteriaModelPopulate() {
        EntityManager em = createEntityManager();
        beginTransaction(em);

        try {
            // Persist some schools
            School school1 = new School();
            school1.setName("Ottawa Junior High");
            school1.addStudent(new Student("OttawaJRStud1"));
            school1.addStudent(new Student("OttawaJRStud2"));
            school1.addStudent(new Student("OttawaJRStud3"));
            em.persist(school1);

            School school2 = new School();
            school2.setName("Ottawa Senior High");
            school2.addStudent(new Student("OttawaSRStud1"));
            school2.addStudent(new Student("OttawaSRStud2"));
            school2.addStudent(new Student("OttawaSRStud3"));
            school2.addStudent(new Student("OttawaSRStud4"));
            school2.addStudent(new Student("OttawaSRStud5"));
            em.persist(school2);

            School school3 = new School();
            school3.setName("Toronto Junior High");
            school3.addStudent(new Student("TorontoJRStud1"));
            school3.addStudent(new Student("TorontoJRStud2"));
            school3.addStudent(new Student("TorontoJRStud3"));
            school3.addStudent(new Student("TorontoJRStud4"));
            school3.addStudent(new Student("TorontoJRStud5"));
            school3.addStudent(new Student("TorontoJRStud6"));
            school3.addStudent(new Student("TorontoJRStud7"));
            em.persist(school3);

            School school4 = new School();
            school4.setName("Toronto Senior High");
            school4.addStudent(new Student("TorontoSRStud1"));
            school4.addStudent(new Student("TorontoSRStud2"));
            school4.addStudent(new Student("TorontoSRStud3"));
            school4.addStudent(new Student("TorontoSRStud4"));
            school4.addStudent(new Student("TorontoSRStud5"));
            school4.addStudent(new Student("TorontoSRStud6"));
            school4.addStudent(new Student("TorontoSRStud7"));
            school4.addStudent(new Student("TorontoSRStud8"));
            school4.addStudent(new Student("TorontoSRStud9"));
            school4.addStudent(new Student("TorontoSRStud10"));
            school4.addStudent(new Student("TorontoSRStud11"));
            em.persist(school4);

            School school5 = new School();
            school5.setName("Montreal Senior High");
            school5.addStudent(new Student("MontrealSRStud1"));
            school5.addStudent(new Student("MontrealSRStud2"));
            school5.addStudent(new Student("MontrealSRStud3"));
            school5.addStudent(new Student("MontrealSRStud4"));
            school5.addStudent(new Student("MontrealSRStud5"));
            em.persist(school5);

            Bolt bolt1 = new Bolt();
            Nut nut1 = new Nut();
            nut1.setColor("Grey");
            nut1.setSize(8);
            bolt1.setNut(nut1);
            em.persist(bolt1);

            Bolt bolt2 = new Bolt();
            Nut nut2 = new Nut();
            nut2.setColor("Black");
            nut2.setSize(8);
            bolt2.setNut(nut2);
            em.persist(bolt2);

            Bolt bolt3 = new Bolt();
            Nut nut3 = new Nut();
            nut3.setColor("Grey");
            nut3.setSize(6);
            bolt3.setNut(nut3);
            em.persist(bolt3);

            Bolt bolt4 = new Bolt();
            Nut nut4 = new Nut();
            nut4.setColor("Black");
            nut4.setSize(6);
            bolt4.setNut(nut4);
            em.persist(bolt4);

            Bolt bolt5 = new Bolt();
            Nut nut5 = new Nut();
            nut5.setColor("Grey");
            nut5.setSize(2);
            bolt5.setNut(nut5);
            em.persist(bolt5);

            Bolt bolt6 = new Bolt();
            Nut nut6 = new Nut();
            nut6.setColor("Grey");
            nut6.setSize(8);
            bolt6.setNut(nut6);
            em.persist(bolt6);

            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            closeEntityManager(em);
            // Re-throw exception to ensure stacktrace appears in test result.
            throw e;
        }

        closeEntityManager(em);
    }

    /**
     * Test user defined additional criteria with no parameters. The additional
     * criteria on school filters for Ottawa named schools.
     */
    public void testAdditionalCriteria() {
        EntityManager em = createEntityManager();

        try {
            List<?> schools = em.createNamedQuery("findJPQLSchools").getResultList();
            assertEquals("Incorrect number of schools were returned [" + schools.size() + "], expected [2]", 2, schools.size());
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            closeEntityManager(em);
            // Re-throw exception to ensure stacktrace appears in test result.
            throw e;
        }

        closeEntityManager(em);
    }

    /**
     * Test user defined additional criteria with parameter.
     */
    public void testAdditionalCriteriaWithParameterFromEM1() {
        EntityManager em = createEntityManager();

        try {
            beginTransaction(em);
            // This should override the EMF property of Montreal%
            em.setProperty("NAME", "Ottawa%");

            // Find the schools, because of our additional criteria on Student
            // and the property above, we should only return Ottawa students.

            List<?> students = em.createQuery("SELECT s from Student s").getResultList();
            assertEquals("Incorrect number of students were returned [" + students.size() + "], expected [8]", 8, students.size());
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            closeEntityManager(em);
            // Re-throw exception to ensure stacktrace appears in test result.
            throw e;
        }

        closeEntityManager(em);
    }

    /**
     * Test user defined additional criteria with parameter.
     */
    public void testAdditionalCriteriaWithParameterFromEM2() {
        EntityManager em = createEntityManager();

        try {
            beginTransaction(em);
            // This should override the EMF property of Montreal%
            em.setProperty("NAME", "Toronto%");

            // Find the schools, because of our additional criteria on Student
            // and the property above, we should only return Toronto students.
            // However, they should not have any schools loaded.

            List<?> students = em.createQuery("SELECT s from Student s").getResultList();
            assertEquals("Incorrect number of students were returned [" + students.size() + "], expected [18]", 18, students.size());
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            closeEntityManager(em);
            // Re-throw exception to ensure stacktrace appears in test result.
            throw e;
        }

        closeEntityManager(em);
    }

    /**
     * Test user defined additional criteria with parameters.
     */
    public void testAdditionalCriteriaWithParameterFromEMF() {
        EntityManager em = createEntityManager();

        try {
            // This should use the EMF NAME property of Montreal%
            List<?> students = em.createQuery("SELECT s from Student s").getResultList();
            assertEquals("Incorrect number of students were returned [" + students.size() + "], expected [5]", 5, students.size());
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            closeEntityManager(em);
            // Re-throw exception to ensure stacktrace appears in test result.
            throw e;
        }

        closeEntityManager(em);
    }

    /**
     * Test user defined additional criteria with parameter.
     */
    public void testComplexAdditionalCriteria() {
        EntityManager em = createEntityManager();

        try {
            beginTransaction(em);
            em.setProperty("NUT_SIZE", 8);
            em.setProperty("NUT_COLOR", "Grey");

            List<?> bolts = em.createQuery("SELECT b from Bolt b").getResultList();
            assertEquals("Incorrect number of bolts were returned [" + bolts.size() + "], expected [2]", 2, bolts.size());
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            closeEntityManager(em);
            // Re-throw exception to ensure stacktrace appears in test result.
            throw e;
        }

        closeEntityManager(em);
    }

    /**
     * Test additional criteria when used on two entities that have a
     * relationship between the two.
     */
    public void testAdditionalCriteriaBetweenEntities() {
        EntityManager em = createEntityManager();

        try {
            beginTransaction(em);

            em.setProperty("SANDWICH_DESCRIPTION", "%hot%");
            em.setProperty("EATER_NAME", "%Glutton%");

            Sandwich sandwich = new Sandwich();
            sandwich.setName("The Inferno");
            sandwich.setDescription("A hot and spicy crazy concoction");
            em.persist(sandwich);

            Eater eater = new Eater();
            eater.setName("Glutton for spicy");
            eater.setSandwhich(sandwich);
            em.persist(eater);

            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            // Re-throw exception to ensure stacktrace appears in test result.
            throw e;
        } finally {
            closeEntityManager(em);
        }
    }

    /**
     * Test additional criteria when using a sub query.
     */
    public void testAdditionalCriteriaWithSubQuery() {
        EntityManager em = createEntityManager();

        try {
            beginTransaction(em);

            Rabbit rabbit = new Rabbit();
            rabbit.setName("Bugs");
            em.persist(rabbit);

            RabbitFoot rabbitFoot = new RabbitFoot();
            rabbitFoot.setCaption("Caption of Bugs");
            rabbitFoot.setRabbitId(rabbit.getId());
            em.persist(rabbitFoot);

            commitTransaction(em);

            em.clear();
            clearCache();

            List<Eater> rabbits = em.createQuery("select this from Rabbit this", Eater.class).getResultList();
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            // Re-throw exception to ensure stacktrace appears in test result.
            throw e;
        } finally {
            closeEntityManager(em);
        }
    }
}
