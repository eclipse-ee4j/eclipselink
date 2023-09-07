/*
 * Copyright (c) 2019, 2021 Oracle, and/or affiliates. All rights reserved.
 * Copyright (c) 2018, 2021 IBM Corporation. All rights reserved.
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
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;

import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.jpa.test.query.model.Dto01;
import org.eclipse.persistence.jpa.test.query.model.EntityTbl01;
import org.eclipse.persistence.jpa.test.query.model.EntityTbl01_;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestQueryCase {
    @Emf(createTables = DDLGen.DROP_CREATE, classes = { EntityTbl01.class }, 
            properties = { @Property(name="eclipselink.logging.level", value="FINE") })
    private EntityManagerFactory emf;

    private static boolean POPULATED = false;

    @Test
    public void testQuery_JPQL_Case_Literals_1() {
        if (emf == null)
            return;

        if(!POPULATED) 
            populate();

        EntityManager em = emf.createEntityManager();
        try {
            // test 1
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

            // test 2
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
            assertEquals(Integer.valueOf(1), dto01.get(0).getItemInteger1());
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
    public void testQuery_Criteria_Case_Literals_1() {
        if (emf == null)
            return;

        if(!POPULATED) 
            populate();

        EntityManager em = emf.createEntityManager();
        try {
            // test 1
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<EntityTbl01> cquery = cb.createQuery(EntityTbl01.class);
            Root<EntityTbl01> root = cquery.from(EntityTbl01.class);
            cquery.select(root);

            Expression<Object> selectCase = cb.selectCase(root.get(EntityTbl01_.itemInteger1))
                .when(1000, "047010")
                .when(100, "023010")
                .otherwise("033020");
            cquery.where(cb.equal(root.get(EntityTbl01_.itemString1), selectCase));

            TypedQuery<EntityTbl01> query = em.createQuery(cquery);
            List<EntityTbl01> dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(0, dto01.size());

            // test 2
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
            assertEquals(Integer.valueOf(1), dto01.get(0).getItemInteger1());
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
    public void testQuery_JPQL_Case_Parameters_1() {
        if (emf == null)
            return;

        if(!POPULATED) 
            populate();

        EntityManager em = emf.createEntityManager();

        try {
            // test 1
            TypedQuery<EntityTbl01> query = em.createQuery(""
                    + "SELECT t FROM EntityTbl01 t "
                        + "WHERE t.itemString1 = ( "
                            + "CASE t.itemInteger1 "
                                + "WHEN ?1 THEN ?2 "
                                + "WHEN ?3 THEN ?4 "
                                + "ELSE ?5 "
                            + "END )", EntityTbl01.class);
            query.setParameter(1, 1000);
            query.setParameter(2, "047010");
            query.setParameter(3, 100);
            query.setParameter(4, "023010");
            query.setParameter(5, "033020");

            List<EntityTbl01> dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(0, dto01.size());

            // test 2
            query = em.createQuery(""
                    + "SELECT t FROM EntityTbl01 t "
                        + "WHERE t.itemString1 = ( "
                            + "CASE t.itemInteger1 "
                                + "WHEN ?1 THEN ?2 "
                                + "WHEN ?3 THEN ?4 "
                                + "ELSE ?5 "
                            + "END )", EntityTbl01.class);
            query.setParameter(1, 1);
            query.setParameter(2, "A");
            query.setParameter(3, 100);
            query.setParameter(4, "B");
            query.setParameter(5, "C");

            dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(1, dto01.size());

            assertEquals("A", dto01.get(0).getItemString1());
            assertEquals("B", dto01.get(0).getItemString2());
            assertEquals("C", dto01.get(0).getItemString3());
            assertEquals("D", dto01.get(0).getItemString4());
            assertEquals(Integer.valueOf(1), dto01.get(0).getItemInteger1());
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
    // Test disabled because it makes use of currently unsupported CriteriaBuilder API calls.
    @Ignore
    public void testQuery_Criteria_Case_Parameters_1() {
        if (emf == null)
            return;

        if(!POPULATED) 
            populate();

        EntityManager em = emf.createEntityManager();

        try {
            // test 1
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<EntityTbl01> cquery = cb.createQuery(EntityTbl01.class);
            Root<EntityTbl01> root = cquery.from(EntityTbl01.class);
            cquery.multiselect(root);

            ParameterExpression<Integer> checkParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> checkParam2 = cb.parameter(Integer.class);
            ParameterExpression<String> resultParam1 = cb.parameter(String.class);
            ParameterExpression<String> resultParam2 = cb.parameter(String.class);
            ParameterExpression<String> resultParam3 = cb.parameter(String.class);

            // Currently unsupported by the JPA API
            // https://github.com/eclipse-ee4j/jpa-api/issues/315
//            Expression<Object> selectCase = cb.selectCase(root.get(EntityTbl01_.itemInteger1))
//                .when(checkParam1, resultParam1)
//                .when(checkParam2, resultParam2)
//                .otherwise(resultParam3);
//            Predicate pred = cb.equal(root.get(EntityTbl01_.itemString1), selectCase);
//            cquery.where(pred);

            TypedQuery<EntityTbl01> query = em.createQuery(cquery);
            query.setParameter(checkParam1, 1000);
            query.setParameter(resultParam1, "047010");
            query.setParameter(checkParam2, 100);
            query.setParameter(resultParam2, "023010");
            query.setParameter(resultParam3, "033020");

            List<EntityTbl01> dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(0, dto01.size());

            // test 2
            cb = em.getCriteriaBuilder();
            cquery = cb.createQuery(EntityTbl01.class);
            root = cquery.from(EntityTbl01.class);
            cquery.multiselect(root);

            checkParam1 = cb.parameter(Integer.class);
            checkParam2 = cb.parameter(Integer.class);
            resultParam1 = cb.parameter(String.class);
            resultParam2 = cb.parameter(String.class);
            resultParam3 = cb.parameter(String.class);

            // Currently unsupported by the JPA API
            // https://github.com/eclipse-ee4j/jpa-api/issues/315
//            selectCase = cb.selectCase(root.get(EntityTbl01_.itemInteger1))
//                .when(checkParam1, resultParam1)
//                .when(checkParam2, resultParam2)
//                .otherwise(resultParam3);
//            pred = cb.equal(root.get(EntityTbl01_.itemString1), selectCase);
//            cquery.where(pred);

            query = em.createQuery(cquery);
            query.setParameter(checkParam1, 1);
            query.setParameter(resultParam1, "A");
            query.setParameter(checkParam2, 100);
            query.setParameter(resultParam2, "B");
            query.setParameter(resultParam3, "C");

            dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(1, dto01.size());

            assertEquals("A", dto01.get(0).getItemString1());
            assertEquals("B", dto01.get(0).getItemString2());
            assertEquals("C", dto01.get(0).getItemString3());
            assertEquals("D", dto01.get(0).getItemString4());
            assertEquals(Integer.valueOf(1), dto01.get(0).getItemInteger1());
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
    public void testQuery_JPQL_Case_Literals_2() {
        if (emf == null)
            return;

        if(!POPULATED) 
            populate();

        EntityManager em = emf.createEntityManager();
        try {
            // test 1
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

            // test 2
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
            assertEquals(Integer.valueOf(1), dto01.get(0).getItemInteger1());
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
    public void testQuery_Criteria_Case_Literals_2() {
        if (emf == null)
            return;

        if(!POPULATED) 
            populate();

        EntityManager em = emf.createEntityManager();
        try {
            // test 1
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<EntityTbl01> cquery = cb.createQuery(EntityTbl01.class);
            Root<EntityTbl01> root = cquery.from(EntityTbl01.class);
            cquery.select(root);

            Expression<String> selectCase = cb.<String>selectCase()
                .when(cb.equal(root.get(EntityTbl01_.itemInteger1), 1000), "047010")
                .when(cb.equal(root.get(EntityTbl01_.itemInteger1), 100), "023010")
                .otherwise("033020");
            cquery.where(cb.equal(root.get(EntityTbl01_.itemString1), selectCase));

            TypedQuery<EntityTbl01> query = em.createQuery(cquery);
            List<EntityTbl01> dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(0, dto01.size());

            // test 2
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
            assertEquals(Integer.valueOf(1), dto01.get(0).getItemInteger1());
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
    public void testQuery_JPQL_Case_Parameters_2() {
        if (emf == null)
            return;

        if(!POPULATED) 
            populate();

        EntityManager em = emf.createEntityManager();
        try {
            // test 1
            TypedQuery<EntityTbl01> query = em.createQuery(""
                    + "SELECT t FROM EntityTbl01 t "
                        + "WHERE t.itemString1 = ( "
                            + "CASE "
                                + "WHEN t.itemInteger1 = ?1 THEN ?2 "
                                + "WHEN t.itemInteger1 = ?3 THEN ?4 "
                                + "ELSE ?5 "
                            + "END )", EntityTbl01.class);
            query.setParameter(1, 1000);
            query.setParameter(2, "047010");
            query.setParameter(3, 100);
            query.setParameter(4, "023010");
            query.setParameter(5, "033020");

            List<EntityTbl01> dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(0, dto01.size());

            // test 2
            query = em.createQuery(""
                    + "SELECT t FROM EntityTbl01 t "
                    + "WHERE t.itemString1 = ( "
                        + "CASE "
                            + "WHEN t.itemInteger1 = ?1 THEN ?2 "
                            + "WHEN t.itemInteger1 = ?3 THEN ?4 "
                            + "ELSE ?5 "
                        + "END )", EntityTbl01.class);
            query.setParameter(1, 1);
            query.setParameter(2, "A");
            query.setParameter(3, 100);
            query.setParameter(4, "B");
            query.setParameter(5, "C");

            dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(1, dto01.size());

            assertEquals("A", dto01.get(0).getItemString1());
            assertEquals("B", dto01.get(0).getItemString2());
            assertEquals("C", dto01.get(0).getItemString3());
            assertEquals("D", dto01.get(0).getItemString4());
            assertEquals(Integer.valueOf(1), dto01.get(0).getItemInteger1());
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
    public void testQuery_Criteria_Case_Parameters_2() {
        if (emf == null)
            return;

        if(!POPULATED) 
            populate();

        EntityManager em = emf.createEntityManager();
        try {
            // test 1
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<EntityTbl01> cquery = cb.createQuery(EntityTbl01.class);
            Root<EntityTbl01> root = cquery.from(EntityTbl01.class);
            cquery.select(root);

            ParameterExpression<Integer> checkParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> checkParam2 = cb.parameter(Integer.class);
            ParameterExpression<String> resultParam1 = cb.parameter(String.class);
            ParameterExpression<String> resultParam2 = cb.parameter(String.class);
            ParameterExpression<String> resultParam3 = cb.parameter(String.class);

            Expression<String> selectCase = cb.<String>selectCase()
                    .when(cb.equal(root.get(EntityTbl01_.itemInteger1), checkParam1), resultParam1)
                    .when(cb.equal(root.get(EntityTbl01_.itemInteger1), checkParam2), resultParam2)
                    .otherwise(resultParam3);
            cquery.where(cb.equal(root.get(EntityTbl01_.itemString1), selectCase));

            TypedQuery<EntityTbl01> query = em.createQuery(cquery);
            query.setParameter(checkParam1, 1000);
            query.setParameter(resultParam1, "047010");
            query.setParameter(checkParam2, 100);
            query.setParameter(resultParam2, "023010");
            query.setParameter(resultParam3, "033020");
            List<EntityTbl01> dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(0, dto01.size());

            // test 2
            cb = em.getCriteriaBuilder();
            cquery = cb.createQuery(EntityTbl01.class);
            root = cquery.from(EntityTbl01.class);
            cquery.select(root);

            checkParam1 = cb.parameter(Integer.class);
            checkParam2 = cb.parameter(Integer.class);
            resultParam1 = cb.parameter(String.class);
            resultParam2 = cb.parameter(String.class);
            resultParam3 = cb.parameter(String.class);

            selectCase = cb.<String>selectCase()
                    .when(cb.equal(root.get(EntityTbl01_.itemInteger1), checkParam1), resultParam1)
                    .when(cb.equal(root.get(EntityTbl01_.itemInteger1), checkParam2), resultParam2)
                    .otherwise(resultParam3);
            cquery.where(cb.equal(root.get(EntityTbl01_.itemString1), selectCase));

            query = em.createQuery(cquery);
            query.setParameter(checkParam1, 1);
            query.setParameter(resultParam1, "A");
            query.setParameter(checkParam2, 100);
            query.setParameter(resultParam2, "B");
            query.setParameter(resultParam3, "C");
            dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(1, dto01.size());

            assertEquals("A", dto01.get(0).getItemString1());
            assertEquals("B", dto01.get(0).getItemString2());
            assertEquals("C", dto01.get(0).getItemString3());
            assertEquals("D", dto01.get(0).getItemString4());
            assertEquals(Integer.valueOf(1), dto01.get(0).getItemInteger1());
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
    public void testQuery_JPQL_Case_Literals_3() {
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
            assertEquals(Integer.valueOf(2), dto01.get(0).getInteger1());
            assertEquals(Integer.valueOf(2), dto01.get(0).getInteger2());
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
    // This test is disabled because it fails with a Constructor type issue
    @Ignore
    public void testQuery_Criteria_Case_Literals_3() {
        if (emf == null)
            return;

        if(!POPULATED) 
            populate();

        EntityManager em = emf.createEntityManager();
        try {
            // test equivalent CriteriaBuilder
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Dto01> cquery = cb.createQuery(Dto01.class);
            Root<EntityTbl01> root = cquery.from(EntityTbl01.class);

            SimpleCase<String, Object> selectCase = cb.selectCase(root.get(EntityTbl01_.itemString2));
            selectCase.when("J", "Japan")
                .otherwise("Other");

            Expression<Long> selectCase2 = cb.<Long>selectCase()
                .when(cb.equal(root.get(EntityTbl01_.itemString3), "C"), Long.valueOf(1))
                .otherwise(Long.valueOf(0));

            Expression<Long> selectCase3 = cb.<Long>selectCase()
                .when(cb.equal(root.get(EntityTbl01_.itemString4), "D"), Long.valueOf(1))
                .otherwise(Long.valueOf(0));

            cquery.select(cb.construct(Dto01.class, 
                    root.get(EntityTbl01_.itemString1),
                    selectCase,
                    cb.sum(selectCase2),
                    cb.sum(selectCase3)));

            cquery.groupBy(root.get(EntityTbl01_.itemString1), root.get(EntityTbl01_.itemString2));

            TypedQuery<Dto01> query = em.createQuery(cquery);

            List<Dto01> dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(1, dto01.size());
            assertEquals("A", dto01.get(0).getStr1());
            assertEquals("Other", dto01.get(0).getStr2());
            assertNull(dto01.get(0).getStr3());
            assertNull(dto01.get(0).getStr4());
            assertEquals(Integer.valueOf(2), dto01.get(0).getInteger1());
            assertEquals(Integer.valueOf(2), dto01.get(0).getInteger2());
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
    // This test is disabled because it fails with a Constructor type issue
    @Ignore
    public void testQuery_JPQL_Case_Parameters_3() {
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
                            + "WHEN ?1 THEN ?2 "
                            + "ELSE ?3 "
                        + "END  "
                        + ", "
                          + "SUM("      // Returns Long (4.8.5)
                              + "CASE "
                                  + "WHEN t.itemString3 = ?4 "
                                  + "THEN ?5 ELSE ?6 "
                              + "END" 
                          +") "
                        + ", "
                          + "SUM("      // Returns Long (4.8.5)
                              + "CASE "
                                  + "WHEN t.itemString4 = ?7 "
                                  + "THEN ?8 ELSE ?9 "
                              + "END" 
                          + ") " 
                        + ") "
                    + "FROM EntityTbl01 t "
                    + "GROUP BY t.itemString1, t.itemString2", Dto01.class);
            query.setParameter(1, "J");
            query.setParameter(2, "Japan");
            query.setParameter(3, "Other");
            query.setParameter(4, "C");
            query.setParameter(5, 1);
            query.setParameter(6, 0);
            query.setParameter(7, "D");
            query.setParameter(8, 1);
            query.setParameter(9, 0);

            List<Dto01> dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(1, dto01.size());
            assertEquals("A", dto01.get(0).getStr1());
            assertEquals("Other", dto01.get(0).getStr2());
            assertNull(dto01.get(0).getStr3());
            assertNull(dto01.get(0).getStr4());
            assertEquals(Integer.valueOf(2), dto01.get(0).getInteger1());
            assertEquals(Integer.valueOf(2), dto01.get(0).getInteger2());
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
    // This test is disabled because it fails with a Constructor type issue
    @Ignore
    public void testQuery_Criteria_Case_Parameters_3() {
        if (emf == null)
            return;

        if(!POPULATED) 
            populate();

        EntityManager em = emf.createEntityManager();
        try {
            // test equivalent CriteriaBuilder
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Dto01> cquery = cb.createQuery(Dto01.class);
            Root<EntityTbl01> root = cquery.from(EntityTbl01.class);

            ParameterExpression<String> checkParam1 = cb.parameter(String.class);
            ParameterExpression<String> checkParam2 = cb.parameter(String.class);
            ParameterExpression<String> checkParam3 = cb.parameter(String.class);
            ParameterExpression<String> resultParam1 = cb.parameter(String.class);
            ParameterExpression<String> resultParam2 = cb.parameter(String.class);
            ParameterExpression<Long> resultParam3 = cb.parameter(Long.class);
            ParameterExpression<Long> resultParam4 = cb.parameter(Long.class);

            // Currently unsupported by the JPA API
            // https://github.com/eclipse-ee4j/jpa-api/issues/315
//            SimpleCase<String, String> selectCase = cb.selectCase(root.get(EntityTbl01_.itemString2));
//            selectCase.when(checkParam1, resultParam1)
//                .otherwise(resultParam2);

            Expression<Long> selectCase2 = cb.<Long>selectCase()
                .when(cb.equal(root.get(EntityTbl01_.itemString3), checkParam2), resultParam3)
                .otherwise(resultParam4);

            Expression<Long> selectCase3 = cb.<Long>selectCase()
                .when(cb.equal(root.get(EntityTbl01_.itemString4), checkParam3), resultParam3)
                .otherwise(resultParam4);

            cquery.select(cb.construct(Dto01.class, 
                    root.get(EntityTbl01_.itemString1),
//                    selectCase,
                    cb.sum(selectCase2),
                    cb.sum(selectCase3)));

            cquery.groupBy(root.get(EntityTbl01_.itemString1), root.get(EntityTbl01_.itemString2));

            TypedQuery<Dto01> query = em.createQuery(cquery);
            query.setParameter(checkParam1, "J");
            query.setParameter(resultParam1, "Japan");
            query.setParameter(resultParam2, "Other");
            query.setParameter(checkParam2, "C");
            query.setParameter(checkParam3, "D");
            query.setParameter(resultParam3, Long.valueOf(1));
            query.setParameter(resultParam4, Long.valueOf(1));
            List<Dto01> dto01 = query.getResultList();

            assertNotNull(dto01);
            assertEquals(1, dto01.size());
            assertEquals("A", dto01.get(0).getStr1());
            assertEquals("Other", dto01.get(0).getStr2());
            assertNull(dto01.get(0).getStr3());
            assertNull(dto01.get(0).getStr4());
            assertEquals(Integer.valueOf(2), dto01.get(0).getInteger1());
            assertEquals(Integer.valueOf(2), dto01.get(0).getInteger2());
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
    public void testQuery_JPQL_Case_Literals_4() {
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
            assertEquals(Integer.valueOf(100), intList.get(0));
            assertEquals(Integer.valueOf(100), intList.get(1));
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
    public void testQuery_Criteria_Case_Literals_4() {
        if (emf == null)
            return;

        if(!POPULATED) 
            populate();

        EntityManager em = emf.createEntityManager();
        try {
            // test equivalent CriteriaBuilder
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer> cquery = cb.createQuery(Integer.class);
            Root<EntityTbl01> root = cquery.from(EntityTbl01.class);

            SimpleCase<String, Integer> selectCase = cb.selectCase(root.get(EntityTbl01_.itemString2));
                selectCase.when("A", 42)
                .when("B", 100)
                .otherwise(0);

            cquery.select(selectCase);

            TypedQuery<Integer> query = em.createQuery(cquery);

            List<Integer> intList = query.getResultList();
            assertNotNull(intList);
            assertEquals(2, intList.size());
            assertEquals(Integer.valueOf(100), intList.get(0));
            assertEquals(Integer.valueOf(100), intList.get(1));
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
    public void testQuery_JPQL_Case_Parameters_4() {
        if (emf == null)
            return;

        if(!POPULATED) 
            populate();

        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Number> query = em.createQuery(""
                    + "SELECT ("
                       + "CASE t.itemString2 "
                       + "WHEN ?1 THEN ?2 "
                       + "WHEN ?3 THEN ?4 "
                       + "ELSE ?5 "
                       + "END "
                    + ") "
                    + "FROM EntityTbl01 t", Number.class);
            query.setParameter(1, "A");
            query.setParameter(2, 42);
            query.setParameter(3, "B");
            query.setParameter(4, 100);
            query.setParameter(5, 0);

            List<Number> intList = query.getResultList();
            assertNotNull(intList);
            assertEquals(2, intList.size());
            assertEquals(Integer.valueOf(100).intValue(), intList.get(0).intValue());
            assertEquals(Integer.valueOf(100).intValue(), intList.get(1).intValue());
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
    // Test disabled because it makes use of currently unsupported CriteriaBuilder API calls.
    @Ignore
    public void testQuery_Criteria_Case_Parameters_4() {
        if (emf == null)
            return;

        if(!POPULATED) 
            populate();

        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Number> cquery = cb.createQuery(Number.class);
            Root<EntityTbl01> root = cquery.from(EntityTbl01.class);

            ParameterExpression<String> checkParam1 = cb.parameter(String.class);
            ParameterExpression<String> checkParam2 = cb.parameter(String.class);
            ParameterExpression<Integer> resultParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> resultParam2 = cb.parameter(Integer.class);
            ParameterExpression<Integer> resultParam3 = cb.parameter(Integer.class);

            // Currently unsupported by the JPA API
            // https://github.com/eclipse-ee4j/jpa-api/issues/315
//            SimpleCase<String, Integer> selectCase = cb.selectCase(root.get(EntityTbl01_.itemString2));
//            selectCase.when(checkParam1, resultParam1)
//                .when(checkParam2, resultParam2)
//                .otherwise(resultParam3);
//
//            cquery.select(selectCase);

            TypedQuery<Number> query = em.createQuery(cquery);
            query.setParameter(checkParam1, "A");
            query.setParameter(resultParam1, 42);
            query.setParameter(checkParam2, "B");
            query.setParameter(resultParam2, 100);
            query.setParameter(resultParam3, 0);

            List<Number> intList = query.getResultList();
            assertNotNull(intList);
            assertEquals(2, intList.size());
            assertEquals(Integer.valueOf(100).intValue(), intList.get(0).intValue());
            assertEquals(Integer.valueOf(100).intValue(), intList.get(1).intValue());
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
    public void testQuery_JPQL_Case_Literals_5() {
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
    public void testQuery_Criteria_Case_Literals_5() {
        if (emf == null)
            return;

        if(!POPULATED) 
            populate();

        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Boolean> cquery = cb.createQuery(Boolean.class);
            Root<EntityTbl01> root = cquery.from(EntityTbl01.class);

            SimpleCase<Integer, Boolean> selectCase = cb.selectCase(root.get(EntityTbl01_.itemInteger1));
            selectCase.when(1, true)
                .otherwise(false);

            cquery.select(selectCase);
            cquery.orderBy(cb.asc(root.get(EntityTbl01_.itemInteger1)));

            TypedQuery<Boolean> query = em.createQuery(cquery);

            List<Boolean> boolList = query.getResultList();
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

    @Test
    public void testQuery_JPQL_Case_Parameters_5() {
        if (emf == null)
            return;

        if(!POPULATED) 
            populate();

        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Boolean> query = em.createQuery(""
                    + "SELECT ("
                       + "CASE "
                       + "WHEN t.itemInteger1 = ?1 THEN ?2 "
                       + "ELSE ?3 "
                       + "END "
                    + ") "
                    + "FROM EntityTbl01 t ORDER BY t.itemInteger1 ASC", Boolean.class);
            query.setParameter(1, 1);
            query.setParameter(2, true);
            query.setParameter(3, false);

            List<Boolean> boolList = query.getResultList();
            assertNotNull(boolList);
            assertEquals(2, boolList.size());
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
    // Test disabled because it makes use of currently unsupported CriteriaBuilder API calls.
    @Ignore
    public void testQuery_Criteria_Case_Parameters_5() {
        if (emf == null)
            return;

        if(!POPULATED) 
            populate();

        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Boolean> cquery = cb.createQuery(Boolean.class);
            Root<EntityTbl01> root = cquery.from(EntityTbl01.class);

            ParameterExpression<Integer> checkParam1 = cb.parameter(Integer.class);
            ParameterExpression<Boolean> resultParam1 = cb.parameter(Boolean.class);
            ParameterExpression<Boolean> resultParam2 = cb.parameter(Boolean.class);

            // Currently unsupported by the JPA API
            // https://github.com/eclipse-ee4j/jpa-api/issues/315
//            SimpleCase<Integer, Boolean> selectCase = cb.selectCase(root.get(EntityTbl01_.itemInteger1));
//            selectCase.when(checkParam1, resultParam1)
//                .otherwise(resultParam2);
//
//            cquery.select(selectCase);
            cquery.orderBy(cb.asc(root.get(EntityTbl01_.itemInteger1)));

            TypedQuery<Boolean> query = em.createQuery(cquery);
            query.setParameter(checkParam1, 1);
            query.setParameter(resultParam1, true);
            query.setParameter(resultParam2, false);

            List<Boolean> boolList = query.getResultList();
            assertNotNull(boolList);
            assertEquals(2, boolList.size());
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