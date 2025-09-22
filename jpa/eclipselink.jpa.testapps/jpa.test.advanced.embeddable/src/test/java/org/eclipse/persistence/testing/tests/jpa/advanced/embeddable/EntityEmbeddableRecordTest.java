/*
 * Copyright (c) 2024, 2025 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - Initial API and implementation.
package org.eclipse.persistence.testing.tests.jpa.advanced.embeddable;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.embeddable.EmbeddableRecordTableCreator;
import org.eclipse.persistence.testing.models.jpa.advanced.embeddable.Point;
import org.eclipse.persistence.testing.models.jpa.advanced.embeddable.RecordAttribute;
import org.eclipse.persistence.testing.models.jpa.advanced.embeddable.RecordEntity;
import org.eclipse.persistence.testing.models.jpa.advanced.embeddable.RecordNestedAttribute;
import org.eclipse.persistence.testing.models.jpa.advanced.embeddable.RecordPK;
import org.eclipse.persistence.testing.models.jpa.advanced.embeddable.Segment;

public class EntityEmbeddableRecordTest extends JUnitTestCase {

    private final int ID_INT1 = 1;
    private final String ID_STRING1 = "a";
    private final int ID_INT2 = 2;
    private final String ID_STRING2 = "bb";
    private final String NAME1 = "abcde";
    private final String NAME2 = "ijkl";
    private final String NAME3 = "opq";
    private final String NAME4 = "xyz";


    /**
     * Constructs an instance of <code>EntityEmbeddableTest</code> class.
     */
    public EntityEmbeddableRecordTest() {
        super();
        setPuName(getPersistenceUnitName());
    }

    /**
     * Constructs an instance of <code>EntityEmbeddableTest</code> class with given test case name.
     * @param name Test case name.
     */
    public EntityEmbeddableRecordTest(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
    }

    @Override
    public String getPersistenceUnitName() {
        return "embeddable-record";
    }

    /**
     * Build collection of test cases for this jUnit test instance.
     * @return {@link Test} class containing collection of test cases for this jUnit test instance.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new EntityEmbeddableRecordTest("testSetup"));
        return addEntityEmbeddableRecordTest(suite);
    }

    /**
     * Adds test, similar to suite() but without adding a setup. Used from <code>suite()</code> to add individual tests.
     * @param suite Target {@link TestSuite} class where to store tests.
     * @return {@link Test} class containing collection of test cases for this jUnit test instance.
     */
    public static Test addEntityEmbeddableRecordTest(TestSuite suite){
        suite.setName("EntityEmbeddableRecordTest");
        suite.addTest(new EntityEmbeddableRecordTest("testPersist"));
        suite.addTest(new EntityEmbeddableRecordTest("testMerge"));
        suite.addTest(new EntityEmbeddableRecordTest("testFind"));
        suite.addTest(new EntityEmbeddableRecordTest("testQuery1"));
        suite.addTest(new EntityEmbeddableRecordTest("testQuery2"));
        suite.addTest(new EntityEmbeddableRecordTest("testQuery3"));
        suite.addTest(new EntityEmbeddableRecordTest("testPersistSegment"));
        suite.addTest(new EntityEmbeddableRecordTest("testQuerySegment1"));
        suite.addTest(new EntityEmbeddableRecordTest("testQuerySegment2"));
        return suite;
    }

    /**
     * Initial setup is done as first test in collection, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        System.out.println("testSetup");
        new EmbeddableRecordTableCreator().replaceTables(getPersistenceUnitServerSession());
        clearCache();
    }

    public void testPersist() {
        EntityManager em = createEntityManager();

        try {
            em.getTransaction().begin();

            em.createNativeQuery("DELETE FROM CMP3_EMBED_REC_VISITOR").executeUpdate();

            RecordEntity entity = new RecordEntity();
            entity.setId(new RecordPK(ID_INT1, ID_STRING1));
            entity.setRecordAttribute(new RecordAttribute(new RecordNestedAttribute(NAME1, NAME2), NAME3));
            entity.setName4(NAME4);
            em.persist(entity);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected exception occurred: " + e.getMessage());
        }
        finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            closeEntityManager(em);
        }
    }

    public void testMerge() {
        EntityManager em = createEntityManager();

        try {
            em.getTransaction().begin();

            RecordEntity entity = new RecordEntity();
            entity.setId(new RecordPK(ID_INT2, ID_STRING2));
            entity.setRecordAttribute(new RecordAttribute(new RecordNestedAttribute(NAME1, null), NAME3));
            entity.setName4(NAME4);
            em.merge(entity);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected exception occurred: " + e.getMessage());
        }
        finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            closeEntityManager(em);
        }
    }

    public void testFind() {
        EntityManager em =createEntityManager();

        try {
            RecordEntity entity = em.find(RecordEntity.class, new RecordPK(ID_INT1, ID_STRING1));
            assertNotNull(entity);

            assertEquals(ID_INT1, entity.getId().id_int());
            assertEquals(NAME1, entity.getRecordAttribute().nestedAttribute().name1());
            assertEquals(NAME2, entity.getRecordAttribute().nestedAttribute().name2());
            assertEquals(NAME3, entity.getRecordAttribute().name3());
            assertEquals(NAME4, entity.getName4());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected exception occurred: " + e.getMessage());
        }
        finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            closeEntityManager(em);
        }
    }

    public void testQuery1() {
        EntityManager em = createEntityManager();

        try {
            Query query = em.createNamedQuery("findRecordEntityById", RecordEntity.class);
            query.setParameter("id", new RecordPK(ID_INT1, ID_STRING1));
            RecordEntity result = (RecordEntity)query.getSingleResult();
            assertEquals(ID_INT1, result.getId().id_int());
            assertEquals(ID_STRING1, result.getId().id_string());
            assertEquals(NAME1, result.getRecordAttribute().nestedAttribute().name1());
            assertEquals(NAME2, result.getRecordAttribute().nestedAttribute().name2());
            assertEquals(NAME3, result.getRecordAttribute().name3());
            assertEquals(NAME4, result.getName4());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected exception occurred: " + e.getMessage());
        }
        finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            closeEntityManager(em);
        }
    }

    public void testQuery2() {
        EntityManager em = createEntityManager();

        try {
            Query query = em.createNamedQuery("findRecordEntityById", RecordEntity.class);
            query.setParameter("id", new RecordPK(ID_INT2, ID_STRING2));
            RecordEntity result = (RecordEntity)query.getSingleResult();
            assertEquals(ID_INT2, result.getId().id_int());
            assertEquals(ID_STRING2, result.getId().id_string());
            assertEquals(NAME1, result.getRecordAttribute().nestedAttribute().name1());
            assertNull(result.getRecordAttribute().nestedAttribute().name2());
            assertEquals(NAME3, result.getRecordAttribute().name3());
            assertEquals(NAME4, result.getName4());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected exception occurred: " + e.getMessage());
        }
        finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            closeEntityManager(em);
        }
    }

    public void testQuery3() {
        EntityManager em = createEntityManager();

        try {
            Query query = em.createNamedQuery("findRecordEntityByRecordAttribute", RecordEntity.class);
            query.setParameter("recordAttribute", new RecordAttribute(new RecordNestedAttribute(NAME1, NAME2), NAME3));
            RecordEntity result = (RecordEntity)query.getSingleResult();
            assertEquals(ID_INT1, result.getId().id_int());
            assertEquals(ID_STRING1, result.getId().id_string());
            assertEquals(NAME1, result.getRecordAttribute().nestedAttribute().name1());
            assertEquals(NAME2, result.getRecordAttribute().nestedAttribute().name2());
            assertEquals(NAME3, result.getRecordAttribute().name3());
            assertEquals(NAME4, result.getName4());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected exception occurred: " + e.getMessage());
        }
        finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            closeEntityManager(em);
        }
    }

    // Issue #2245 - store single record with @Embedded record into database
    public void testPersistSegment() {
        EntityManager em = createEntityManager();
        try {
            Segment segment = new Segment(-1L, new Point(1,1), new Point(2,2));
            em.getTransaction().begin();
            em.persist(segment);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected exception occurred: " + e.getMessage());
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            closeEntityManager(em);
        }
    }

    // Issue #2245 - database query with old syntax
    public void testQuerySegment1() {
        EntityManager em = createEntityManager();
        try {
            List<Segment> result = em.createQuery(
                    "SELECT s FROM Segment s WHERE s.pointB.y < :yMax ORDER BY s.pointB.y ASC, s.id ASC",
                    Segment.class)
                    .setParameter("yMax", 10)
                    .getResultList();
            assertEquals(1, result.size());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected exception occurred: " + e.getMessage());
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            closeEntityManager(em);
        }
    }

    // Issue #2245 - database query with new simplified syntax
    public void testQuerySegment2() {
        EntityManager em = createEntityManager();
        try {
            List<Segment> result = em.createQuery(
                            "FROM Segment WHERE this.pointB.y < :yMax ORDER BY this.pointB.y ASC, this.id ASC",
                            Segment.class)
                    .setParameter("yMax", 10)
                    .getResultList();
            assertEquals(1, result.size());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected exception occurred: " + e.getMessage());
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            closeEntityManager(em);
        }
    }

}
