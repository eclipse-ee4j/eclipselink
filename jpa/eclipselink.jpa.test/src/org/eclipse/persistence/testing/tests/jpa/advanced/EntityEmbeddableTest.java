/*
 * Copyright (c) 2007, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - Initial API and implementation.
//     07/07/2014-2.6 Tomas Kraus
//       - 439127: Modified as jUnit test.
//     03/19/2018-2.7.2 Lukas Jungmann
//       - 413120: Nested Embeddable Null pointer
//       - 496836: NullPointerException on ObjectChangeSet.mergeObjectChanges
package org.eclipse.persistence.testing.tests.jpa.advanced;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.advanced.embeddable.Continent;
import org.eclipse.persistence.testing.models.jpa.advanced.embeddable.Country;
import org.eclipse.persistence.testing.models.jpa.advanced.embeddable.Description;
import org.eclipse.persistence.testing.models.jpa.advanced.embeddable.Visitor;
import org.eclipse.persistence.testing.models.jpa.advanced.embeddable.VisitorPopulator;

public class EntityEmbeddableTest extends JUnitTestCase {

    /** Persistence unit name. */
    protected String PUName = "embeddable";

    /**
     * Constructs an instance of <code>EntityEmbeddableTest</code> class.
     */
    public EntityEmbeddableTest() {
        super();
        setPuName(PUName);
    }

    /**
     * Constructs an instance of <code>EntityEmbeddableTest</code> class with given test case name.
     * @param name Test case name.
     */
    public EntityEmbeddableTest(String name) {
        super(name);
        setPuName(PUName);
    }

    /**
     * Build collection of test cases for this jUnit test instance.
     * @return {@link Test} class containing collection of test cases for this jUnit test instance.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new EntityEmbeddableTest("testSetup"));
        return addEntityEmbeddableTest(suite);
    }

    /**
     * Adds test, similar to suite() but without adding a setup. Used from <code>suite()</code> to add individual tests.
     * @param suite Target {@link TestSuite} class where to store tests.
     * @return {@link Test} class containing collection of test cases for this jUnit test instance.
     */
    public static Test addEntityEmbeddableTest(TestSuite suite){
        suite.setName("EntityEmbeddableTest");
        suite.addTest(new EntityEmbeddableTest("testNativeQueryWithSqlResultSetMappings"));
        suite.addTest(new EntityEmbeddableTest("testRootEmbeddable"));
        suite.addTest(new EntityEmbeddableTest("testNestedEmbeddable"));
        suite.addTest(new EntityEmbeddableTest("testMixedEmbeddable"));
        return suite;
    }

    /**
     * Initial setup is done as first test in collection, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        System.out.println("testSetup");
        new AdvancedTableCreator().replaceTables(getServerSession(PUName));
        clearCache(PUName);
    }

    /**
     * Initialize, persist and retrieve some <code>Visitor</code> entities for testing.
     * @param em An <code>EntityManager</code> instance used to persist sample data.
     * @return Collection of initialized and persisted <code>Visitor</code> entities.
     */
    private Collection<Visitor> createVisitorEntities(EntityManager em) {
        Collection<Visitor> visitors = new LinkedList<Visitor>();
        beginTransaction(em);
        try {
            Visitor v1 = VisitorPopulator.visitorExample1();
            Visitor v2 = VisitorPopulator.visitorExample2();
            em.persist(v1);
            em.persist(v2);
            visitors.add(v1);
            visitors.add(v2);
        } catch (RuntimeException ex) {
            rollbackTransaction(em);
            throw ex;
        }
        em.flush();
        commitTransaction(em);
        return visitors;
    }

    /**
     * Delete provided <code>Visitor</code> entities.
     * @param em       An <code>EntityManager</code> instance used to delete sample data.
     * @param visitors Collection of <code>Visitor</code> entities to be deleted.
     */
    private void deleteVisitorEntities(EntityManager em, Collection<Visitor> visitors) {
        beginTransaction(em);
        try {
            for (Visitor visitor : visitors) {
                em.remove(visitor);
            }
        } catch (RuntimeException ex) {
            rollbackTransaction(em);
            throw ex;
        }
           em.flush();
        commitTransaction(em);
    }

    /**
     * Test native SQL query that returns result as object defined in <code>SqlResultSetMapping</code> annotation.
     */
    public void testNativeQueryWithSqlResultSetMappings() {
        List q;
        int passCounter = 0;
        EntityManager em = createEntityManager(PUName);
        Collection<Visitor> visitors = null;
        try {
            visitors = createVisitorEntities(em);
            q = em.createNativeQuery(
                    "Select o.ID AS OID, o.NAME AS ONAME, o.COUNTRY AS OCOUNTRY, " +
                    "o.CODE AS OCODE from CMP3_EMBED_VISITOR o WHERE (o.ID = '1')", "VisitorResults")
                    .getResultList();
            assertEquals("Size of returned list with query results should be 1.", q.size(), 1);
            for (Object obj : q) {
                if (obj instanceof Visitor) {
                    Visitor custReturned = (Visitor) obj;
                    assertTrue("Visitor object primary key is invalid.", "1".equals(custReturned.getId()));
                    assertTrue("Country code is invalid.", "USA".equals(custReturned.getCountry().getCode()));
                } else {
                    fail("Received unexpected instance: " + obj != null ? obj.getClass().getName() : "null");
                }
            }
        } catch (Exception e) {
            fail("Unexpected exception occurred: " + e.getMessage());
        } finally {
            if (visitors != null) {
                deleteVisitorEntities(em, visitors);
                em.close();
            }
        }
    }

    public void testRootEmbeddable() {
        EntityManager em = createEntityManager(PUName);
        beginTransaction(em);
        Visitor v = null;
        try {
            v = VisitorPopulator.visitorExample3();
            em.persist(v);
            em.flush();
            commitTransaction(em);
        } catch (RuntimeException ex) {
            rollbackTransaction(em);
            throw ex;
        }
        beginTransaction(em);
        try {
            v.setName("fixed name");
            em.merge(v);
            em.flush();
            v.setCountry(null);
            em.merge(v);
            em.flush();
            v.setCountry(new Country("HU", "Hungary", "EUR"));
            v.getCountry().setCode("OTH");
            em.merge(v);
            em.flush();
            v.getCountry().setCode(null);
            em.merge(v);
            em.flush();
        } finally {
            closeEntityManagerAndTransaction(em);
        }
    }

    public void testNestedEmbeddable() {
        EntityManager em = createEntityManager(PUName);
        beginTransaction(em);
        Visitor v = null;
        try {
            v = VisitorPopulator.visitorExample4();
            em.persist(v);
            em.flush();
            v.getCountry().setContinent(null);
            em.merge(v);
            em.flush();
            Continent c = new Continent("Africa");
            v.getCountry().setContinent(c);
            em.merge(v);
            em.flush();
            v.getCountry().setContinent(new Continent("AFR"));
            em.merge(v);
            em.flush();
            v.getCountry().setContinent(null);
            em.merge(v);
            em.flush();
        } finally {
            closeEntityManagerAndTransaction(em);
        }
    }

    public void testMixedEmbeddable() {
        EntityManager em = createEntityManager(PUName);
        beginTransaction(em);
        Visitor v = null;
        try {
            v = VisitorPopulator.visitorExample5();
            em.persist(v);
            em.flush();
            commitTransaction(em);
        } catch (RuntimeException ex) {
            rollbackTransaction(em);
            throw ex;
        }
        beginTransaction(em);
        try {
            v.getCountry().getContinent().setDescription(null);
            em.merge(v);
            em.flush();
            v.getCountry().setContinent(null);
            em.merge(v);
            em.flush();
            v.setCountry(null);
            em.merge(v);
            em.flush();
            v.setCountry(new Country("JAR", "South Africa", "EUR"));
            Continent c = new Continent("Africa");
            v.getCountry().setContinent(c);
            em.merge(v);
            em.flush();
            v.getCountry().getContinent().setContinentCode("AFR");
            em.merge(v);
            em.flush();
            v.getCountry().getContinent().setDescription(new Description("Africa is nice continent"));
            em.merge(v);
            em.flush();
            v.getCountry().getContinent().setDescription(null);
            em.merge(v);
            em.flush();
            v.getCountry().setContinent(null);
            em.merge(v);
            em.flush();
            v.setCountry(null);
            em.merge(v);
            em.flush();
            v.setCountry(new Country("ALB", "ALBANIA", "EUR"));
            v.getCountry().getContinent().setDescription(new Description("Small continent"));
            em.merge(v);
            em.flush();
        } finally {
            closeEntityManagerAndTransaction(em);
        }
    }
}
