/*
 * Copyright (c) 2018, 2021 IBM Corporation, Oracle, and/or affiliates. All rights reserved.
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
//     IBM - Bug 537795: CASE THEN and ELSE scalar expression Constants should not be casted to CASE operand type
package org.eclipse.persistence.jpa.test.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.SimpleCase;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;

import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.jpa.test.query.model.Dto01;
import org.eclipse.persistence.jpa.test.query.model.EntityTbl01;
import org.eclipse.persistence.jpa.test.query.model.EntityTbl01_;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestQueryCase {
    @Emf(createTables = DDLGen.DROP_CREATE, classes = { EntityTbl01.class }, 
            properties = { @Property(name="eclipselink.logging.level", value="FINE")})
    private EntityManagerFactory emf;

    private static boolean POPULATED = false;

    @Test
    public void testQueryCase1() {
        if (emf == null)
            return;

        if(!POPULATED) 
            populate();

        EntityManager em = emf.createEntityManager();

        try {
            TypedQuery<EntityTbl01> query = em.createQuery(""
                    + "SELECT t FROM EntityTbl01 t "
                        + "WHERE t.itemString1 = ( "
                            + "CASE t.itemInteger1 "
                                + "WHEN 1000 THEN '047010' "
                                + "WHEN 100 THEN '023010' "
                                + "ELSE '033020' "
                            + "END )", EntityTbl01.class);

            List<EntityTbl01> dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(0, dto01.size());

            query = em.createQuery(""
                    + "SELECT t FROM EntityTbl01 t "
                        + "WHERE t.itemString1 = ( "
                            + "CASE t.itemInteger1 "
                                + "WHEN 1 THEN 'A' "
                                + "WHEN 100 THEN 'B' "
                                + "ELSE 'C' "
                            + "END )", EntityTbl01.class);
            dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(1, dto01.size());

            assertEquals("A", dto01.get(0).getItemString1());
            assertEquals("B", dto01.get(0).getItemString2());
            assertEquals("C", dto01.get(0).getItemString3());
            assertEquals("D", dto01.get(0).getItemString4());
            assertEquals(new Integer(1), dto01.get(0).getItemInteger1());

            // test 1 equivalent CriteriaBuilder
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<EntityTbl01> cquery = cb.createQuery(EntityTbl01.class);
            Root<EntityTbl01> root = cquery.from(EntityTbl01.class);
            cquery.select(root);

            Expression<Object> selectCase = cb.selectCase(root.get(EntityTbl01_.itemInteger1))
                .when(1000, "047010")
                .when(100, "023010")
                .otherwise("033020");
            cquery.where(cb.equal(root.get(EntityTbl01_.itemString1), selectCase));

            query = em.createQuery(cquery);
            dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(0, dto01.size());

            // test 2 equivalent CriteriaBuilder
            cb = em.getCriteriaBuilder();
            cquery = cb.createQuery(EntityTbl01.class);
            root = cquery.from(EntityTbl01.class);
            cquery.select(root);

            selectCase = cb.selectCase(root.get(EntityTbl01_.itemInteger1))
                .when(1, "A")
                .when(100, "B")
                .otherwise("C");
            cquery.where(cb.equal(root.get(EntityTbl01_.itemString1), selectCase));

            query = em.createQuery(cquery);
            dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(1, dto01.size());

            assertEquals("A", dto01.get(0).getItemString1());
            assertEquals("B", dto01.get(0).getItemString2());
            assertEquals("C", dto01.get(0).getItemString3());
            assertEquals("D", dto01.get(0).getItemString4());
            assertEquals(new Integer(1), dto01.get(0).getItemInteger1());
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }
    }

    @Test
    public void testQueryCase2() {
        if (emf == null)
            return;

        if(!POPULATED) 
            populate();

        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<EntityTbl01> query = em.createQuery(""
                    + "SELECT t FROM EntityTbl01 t "
                        + "WHERE t.itemString1 = ( "
                            + "CASE "
                                + "WHEN t.itemInteger1 = 1000 THEN '047010' "
                                + "WHEN t.itemInteger1 = 100 THEN '023010' "
                                + "ELSE '033020' "
                            + "END )", EntityTbl01.class);

            List<EntityTbl01> dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(0, dto01.size());

            query = em.createQuery(""
                    + "SELECT t FROM EntityTbl01 t "
                    + "WHERE t.itemString1 = ( "
                        + "CASE "
                            + "WHEN t.itemInteger1 = 1 THEN 'A' "
                            + "WHEN t.itemInteger1 = 100 THEN 'B' "
                            + "ELSE 'C' "
                        + "END )", EntityTbl01.class);

            dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(1, dto01.size());

            assertEquals("A", dto01.get(0).getItemString1());
            assertEquals("B", dto01.get(0).getItemString2());
            assertEquals("C", dto01.get(0).getItemString3());
            assertEquals("D", dto01.get(0).getItemString4());
            assertEquals(new Integer(1), dto01.get(0).getItemInteger1());

            query = em.createQuery(""
                    + "SELECT t FROM EntityTbl01 t "
                    + "WHERE t.itemString1 = ( "
                        + "CASE "
                        + "WHEN t.itemInteger1 = 1 AND t.KeyString = 'Key01' THEN 'A' "
                        + "WHEN t.itemInteger1 = 100 THEN 'B' "
                        + "ELSE 'C' "
                        + "END )", EntityTbl01.class);

            // test 1 equivalent CriteriaBuilder
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<EntityTbl01> cquery = cb.createQuery(EntityTbl01.class);
            Root<EntityTbl01> root = cquery.from(EntityTbl01.class);
            cquery.select(root);

            Expression<String> selectCase = cb.<String>selectCase()
                .when(cb.equal(root.get(EntityTbl01_.itemInteger1), 1000), "047010")
                .when(cb.equal(root.get(EntityTbl01_.itemInteger1), 100), "023010")
                .otherwise("033020");
            cquery.where(cb.equal(root.get(EntityTbl01_.itemString1), selectCase));

            query = em.createQuery(cquery);
            dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(0, dto01.size());

            // test 2 equivalent CriteriaBuilder
            cb = em.getCriteriaBuilder();
            cquery = cb.createQuery(EntityTbl01.class);
            root = cquery.from(EntityTbl01.class);
            cquery.select(root);

            selectCase = cb.<String>selectCase()
                .when(cb.and(
                        cb.equal(root.get(EntityTbl01_.itemInteger1), 1), 
                        cb.equal(root.get(EntityTbl01_.KeyString), "Key01")), "A")
                .when(cb.equal(root.get(EntityTbl01_.itemInteger1), 100), "B")
                .otherwise("C");
            cquery.where(cb.equal(root.get(EntityTbl01_.itemString1), selectCase));

            query = em.createQuery(cquery);
            dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(1, dto01.size());

            assertEquals("A", dto01.get(0).getItemString1());
            assertEquals("B", dto01.get(0).getItemString2());
            assertEquals("C", dto01.get(0).getItemString3());
            assertEquals("D", dto01.get(0).getItemString4());
            assertEquals(new Integer(1), dto01.get(0).getItemInteger1());
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }
    }

    @Test
    public void testQueryCase3() {
        if (emf == null)
            return;

        if(!POPULATED) 
            populate();

        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Dto01> query = em.createQuery(""
                    + "SELECT new org.eclipse.persistence.jpa.test.query.model.Dto01("
                        + "t.itemString1, "               // String
                        + "CASE t.itemString2 "           // String
                            + "WHEN 'J' THEN 'Japan' "
                            + "ELSE 'Other' "
                        + "END  "
                        + ", "
                          + "SUM("      // Returns Long (4.8.5)
                              + "CASE "
                                  + "WHEN t.itemString3 = 'C' "
                                  + "THEN 1 ELSE 0 "
                              + "END" 
                          +") "
                        + ", "
                          + "SUM("      // Returns Long (4.8.5)
                              + "CASE "
                                  + "WHEN t.itemString4 = 'D' "
                                  + "THEN 1 ELSE 0 "
                              + "END" 
                          + ") " 
                        + ") "
                    + "FROM EntityTbl01 t "
                    + "GROUP BY t.itemString1, t.itemString2", Dto01.class);

            List<Dto01> dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(1, dto01.size());
            assertEquals("A", dto01.get(0).getStr1());
            assertEquals("Other", dto01.get(0).getStr2());
            assertNull(dto01.get(0).getStr3());
            assertNull(dto01.get(0).getStr4());
            assertEquals(new Integer(2), dto01.get(0).getInteger1());
            assertEquals(new Integer(2), dto01.get(0).getInteger2());

            // test equivalent CriteriaBuilder
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Dto01> cquery = cb.createQuery(Dto01.class);
            Root<EntityTbl01> root = cquery.from(EntityTbl01.class);

            SimpleCase<String, Object> selectCase = cb.selectCase(root.get(EntityTbl01_.itemString2));
                selectCase.when("J", "Japan")
                .otherwise("Other");

            Expression<Long> selectCase2 = cb.<Long>selectCase()
                .when(cb.equal(root.get(EntityTbl01_.itemString3), "C"), new Long(1))
                .otherwise(new Long(0));

            Expression<Long> selectCase3 = cb.<Long>selectCase()
                .when(cb.equal(root.get(EntityTbl01_.itemString4), "D"), new Long(1))
                .otherwise(new Long(0));

            cquery.select(cb.construct(Dto01.class, 
                    root.get(EntityTbl01_.itemString1),
                    selectCase,
                    cb.sum(selectCase2),
                    cb.sum(selectCase3)));

            cquery.groupBy(root.get(EntityTbl01_.itemString1), root.get(EntityTbl01_.itemString2));

            query = em.createQuery(cquery);

            dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(1, dto01.size());
            assertEquals("A", dto01.get(0).getStr1());
            assertEquals("Other", dto01.get(0).getStr2());
            assertNull(dto01.get(0).getStr3());
            assertNull(dto01.get(0).getStr4());
            assertEquals(new Integer(2), dto01.get(0).getInteger1());
            assertEquals(new Integer(2), dto01.get(0).getInteger2());
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }
    }

    @Test
    public void testQueryCase4() {
        if (emf == null)
            return;

        if(!POPULATED) 
            populate();

        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Integer> query = em.createQuery(""
                    + "SELECT ("
                       + "CASE t.itemString2 "
                       + "WHEN 'A' THEN 42 "
                       + "WHEN 'B' THEN 100 "
                       + "ELSE 0 "
                       + "END "
                    + ") "
                    + "FROM EntityTbl01 t", Integer.class);

            List<Integer> intList = query.getResultList();
            assertNotNull(intList);
            assertEquals(2, intList.size());
            assertEquals(new Integer(100), intList.get(0));
            assertEquals(new Integer(100), intList.get(1));

            // test equivalent CriteriaBuilder
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer> cquery = cb.createQuery(Integer.class);
            Root<EntityTbl01> root = cquery.from(EntityTbl01.class);

            SimpleCase<String, Integer> selectCase = cb.selectCase(root.get(EntityTbl01_.itemString2));
                selectCase.when("A", 42)
                .when("B", 100)
                .otherwise(0);

            cquery.select(selectCase);

            query = em.createQuery(cquery);

            intList = query.getResultList();
            assertNotNull(intList);
            assertEquals(2, intList.size());
            assertEquals(new Integer(100), intList.get(0));
            assertEquals(new Integer(100), intList.get(1));
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }
    }

    @Test
    public void testQueryCase5() {
        if (emf == null)
            return;

        if(!POPULATED) 
            populate();

        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Boolean> query = em.createQuery(""
                    + "SELECT ("
                       + "CASE "
                       + "WHEN t.itemInteger1 = 1 THEN TRUE "
                       + "ELSE FALSE "
                       + "END "
                    + ") "
                    + "FROM EntityTbl01 t ORDER BY t.itemInteger1 ASC", Boolean.class);

            List<Boolean> boolList = query.getResultList();
            assertNotNull(boolList);
            assertEquals(2, boolList.size());
            assertEquals(true, boolList.get(0));
            assertEquals(false, boolList.get(1));

            // test equivalent CriteriaBuilder
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Boolean> cquery = cb.createQuery(Boolean.class);
            Root<EntityTbl01> root = cquery.from(EntityTbl01.class);

            SimpleCase<Integer, Boolean> selectCase = cb.selectCase(root.get(EntityTbl01_.itemInteger1));
                selectCase.when(1, true)
                .otherwise(false);

            cquery.select(selectCase);
            cquery.orderBy(cb.asc(root.get(EntityTbl01_.itemInteger1)));

            query = em.createQuery(cquery);

            boolList = query.getResultList();
            assertNotNull(boolList);
            assertEquals(2, boolList.size());
            assertEquals(true, boolList.get(0));
            assertEquals(false, boolList.get(1));
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }
    }

    private void populate() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            EntityTbl01 tbl1 = new EntityTbl01();
            tbl1.setKeyString("Key01");
            tbl1.setItemString1("A");
            tbl1.setItemString2("B");
            tbl1.setItemString3("C");
            tbl1.setItemString4("D");
            tbl1.setItemInteger1(1);
            em.persist(tbl1);

            EntityTbl01 tbl2 = new EntityTbl01();
            tbl2.setKeyString("Key02");
            tbl2.setItemString1("A");
            tbl2.setItemString2("B");
            tbl2.setItemString3("C");
            tbl2.setItemString4("D");
            tbl2.setItemInteger1(2);
            em.persist(tbl2);

            em.getTransaction().commit();

            POPULATED = true;
        } finally {
            if(em.isOpen()) {
                em.close();
            }
        }
    }
}