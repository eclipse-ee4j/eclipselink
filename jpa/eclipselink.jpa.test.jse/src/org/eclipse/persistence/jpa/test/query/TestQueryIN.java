/*
 * Copyright (c) 2021 IBM Corporation. All rights reserved.
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
//     IBM - Bug 573794: Add support for Parameters in CriteriaBuilder for IN expressions
package org.eclipse.persistence.jpa.test.query;

import java.util.Arrays;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.jpa.test.query.model.EntityTbl01;
import org.eclipse.persistence.jpa.test.query.model.EntityTbl01_;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestQueryIN {
    @Emf(createTables = DDLGen.DROP_CREATE, classes = { EntityTbl01.class }, 
            properties = { @Property(name="eclipselink.logging.level", value="FINE")})
    private EntityManagerFactory emf;

    private static boolean POPULATED = false;

    @Test
    public void testQuery_JPQL_IN_Literals_1() {
        if (emf == null)
            return;

        if(!POPULATED) 
            populate();

        EntityManager em = emf.createEntityManager();

        try {
            TypedQuery<EntityTbl01> query = em.createQuery(""
                    + "SELECT t FROM EntityTbl01 t "
                        + "WHERE t.itemString1 IN ('HELLO', 'ONE', 'WORLD', 'PEOPLE')", EntityTbl01.class);
            List<EntityTbl01> dto01 = query.getResultList();
            Assert.assertNotNull(dto01);
            Assert.assertEquals(1, dto01.size());

            Assert.assertEquals("ONE", dto01.get(0).getItemString1());
            Assert.assertEquals("TWO", dto01.get(0).getItemString2());
            Assert.assertEquals("THREE", dto01.get(0).getItemString3());
            Assert.assertEquals("FIVE", dto01.get(0).getItemString4());
            Assert.assertEquals(Integer.valueOf(1), dto01.get(0).getItemInteger1());
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
    public void testQuery_JPQL_IN_Parameters_1() {
        if (emf == null)
            return;

        if(!POPULATED) 
            populate();

        EntityManager em = emf.createEntityManager();

        try {
            // test 1
            TypedQuery<EntityTbl01> query = em.createQuery(""
                    + "SELECT t FROM EntityTbl01 t "
                        + "WHERE t.itemString1 IN (?1, ?2, ?3, ?4)", EntityTbl01.class);
            query.setParameter(1, "HELLO");
            query.setParameter(2, "ONE");
            query.setParameter(3, "WORLD");
            query.setParameter(4, "PEOPLE");
            List<EntityTbl01> dto01 = query.getResultList();
            Assert.assertNotNull(dto01);
            Assert.assertEquals(1, dto01.size());

            Assert.assertEquals("ONE", dto01.get(0).getItemString1());
            Assert.assertEquals("TWO", dto01.get(0).getItemString2());
            Assert.assertEquals("THREE", dto01.get(0).getItemString3());
            Assert.assertEquals("FIVE", dto01.get(0).getItemString4());
            Assert.assertEquals(Integer.valueOf(1), dto01.get(0).getItemInteger1());

            // equivalent test 2
            query = em.createQuery(""
                    + "SELECT t FROM EntityTbl01 t "
                        + "WHERE t.itemString1 IN ?1", EntityTbl01.class);
            query.setParameter(1, Arrays.asList("HELLO", "ONE", "WORLD", "PEOPLE"));
            dto01 = query.getResultList();
            Assert.assertNotNull(dto01);
            Assert.assertEquals(1, dto01.size());

            Assert.assertEquals("ONE", dto01.get(0).getItemString1());
            Assert.assertEquals("TWO", dto01.get(0).getItemString2());
            Assert.assertEquals("THREE", dto01.get(0).getItemString3());
            Assert.assertEquals("FIVE", dto01.get(0).getItemString4());
            Assert.assertEquals(Integer.valueOf(1), dto01.get(0).getItemInteger1());
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
    public void testQuery_Criteria_IN_Literals_1() {
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

            cquery.where(root.get(EntityTbl01_.itemString1).in("HELLO", "ONE", "WORLD", "PEOPLE"));

            TypedQuery<EntityTbl01> query = em.createQuery(cquery);

            List<EntityTbl01> dto01 = query.getResultList();
            Assert.assertNotNull(dto01);
            Assert.assertEquals(1, dto01.size());

            Assert.assertEquals("ONE", dto01.get(0).getItemString1());
            Assert.assertEquals("TWO", dto01.get(0).getItemString2());
            Assert.assertEquals("THREE", dto01.get(0).getItemString3());
            Assert.assertEquals("FIVE", dto01.get(0).getItemString4());
            Assert.assertEquals(Integer.valueOf(1), dto01.get(0).getItemInteger1());

            // equivalent test 2
            cb = em.getCriteriaBuilder();
            cquery = cb.createQuery(EntityTbl01.class);
            root = cquery.from(EntityTbl01.class);
            cquery.select(root);

            In<String> in = cb.in(root.get(EntityTbl01_.itemString1));
            cquery.where(in.value("HELLO").value("ONE").value("WORLD").value("PEOPLE"));

            query = em.createQuery(cquery);

            dto01 = query.getResultList();
            Assert.assertNotNull(dto01);
            Assert.assertEquals(1, dto01.size());

            Assert.assertEquals("ONE", dto01.get(0).getItemString1());
            Assert.assertEquals("TWO", dto01.get(0).getItemString2());
            Assert.assertEquals("THREE", dto01.get(0).getItemString3());
            Assert.assertEquals("FIVE", dto01.get(0).getItemString4());
            Assert.assertEquals(Integer.valueOf(1), dto01.get(0).getItemInteger1());

            // equivalent test 3
            cb = em.getCriteriaBuilder();
            cquery = cb.createQuery(EntityTbl01.class);
            root = cquery.from(EntityTbl01.class);
            cquery.select(root);

            List<String> strCollection = Arrays.asList("HELLO", "ONE", "WORLD", "PEOPLE");
            cquery.where(root.get(EntityTbl01_.itemString1).in(strCollection));

            query = em.createQuery(cquery);

            dto01 = query.getResultList();
            Assert.assertNotNull(dto01);
            Assert.assertEquals(1, dto01.size());

            Assert.assertEquals("ONE", dto01.get(0).getItemString1());
            Assert.assertEquals("TWO", dto01.get(0).getItemString2());
            Assert.assertEquals("THREE", dto01.get(0).getItemString3());
            Assert.assertEquals("FIVE", dto01.get(0).getItemString4());
            Assert.assertEquals(Integer.valueOf(1), dto01.get(0).getItemInteger1());
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
    public void testQuery_Criteria_IN_Parameters_1() {
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

            ParameterExpression<String> strValue1 = cb.parameter(String.class);
            ParameterExpression<String> strValue2 = cb.parameter(String.class);
            ParameterExpression<String> strValue3 = cb.parameter(String.class);
            ParameterExpression<String> strValue4 = cb.parameter(String.class);
            cquery.where(root.get(EntityTbl01_.itemString1).in(strValue1, strValue2, strValue3, strValue4));

            TypedQuery<EntityTbl01> query = em.createQuery(cquery);
            query.setParameter(strValue1, "HELLO");
            query.setParameter(strValue2, "ONE");
            query.setParameter(strValue3, "WORLD");
            query.setParameter(strValue4, "PEOPLE");

            List<EntityTbl01> dto01 = query.getResultList();
            Assert.assertNotNull(dto01);
            Assert.assertEquals(1, dto01.size());

            Assert.assertEquals("ONE", dto01.get(0).getItemString1());
            Assert.assertEquals("TWO", dto01.get(0).getItemString2());
            Assert.assertEquals("THREE", dto01.get(0).getItemString3());
            Assert.assertEquals("FIVE", dto01.get(0).getItemString4());
            Assert.assertEquals(Integer.valueOf(1), dto01.get(0).getItemInteger1());

            // equivalent test 2
            cb = em.getCriteriaBuilder();
            cquery = cb.createQuery(EntityTbl01.class);
            root = cquery.from(EntityTbl01.class);
            cquery.select(root);

            strValue1 = cb.parameter(String.class);
            strValue2 = cb.parameter(String.class);
            strValue3 = cb.parameter(String.class);
            strValue4 = cb.parameter(String.class);
            In<String> in = cb.in(root.get(EntityTbl01_.itemString1));
            cquery.where(in.value(strValue1).value(strValue2).value(strValue3).value(strValue4));

            query = em.createQuery(cquery);
            query.setParameter(strValue1, "HELLO");
            query.setParameter(strValue2, "ONE");
            query.setParameter(strValue3, "WORLD");
            query.setParameter(strValue4, "PEOPLE");

            dto01 = query.getResultList();
            Assert.assertNotNull(dto01);
            Assert.assertEquals(1, dto01.size());

            Assert.assertEquals("ONE", dto01.get(0).getItemString1());
            Assert.assertEquals("TWO", dto01.get(0).getItemString2());
            Assert.assertEquals("THREE", dto01.get(0).getItemString3());
            Assert.assertEquals("FIVE", dto01.get(0).getItemString4());
            Assert.assertEquals(Integer.valueOf(1), dto01.get(0).getItemInteger1());

            // equivalent test 3
            cb = em.getCriteriaBuilder();
            cquery = cb.createQuery(EntityTbl01.class);
            cquery.from(EntityTbl01.class);
            cquery.select(root);

            ParameterExpression<List> colValue = cb.parameter(List.class);
            cquery.where(root.get(EntityTbl01_.itemString1).in(colValue));

            query = em.createQuery(cquery);
            query.setParameter(colValue, Arrays.asList("HELLO", "ONE", "WORLD", "PEOPLE"));

            dto01 = query.getResultList();
            Assert.assertNotNull(dto01);
            Assert.assertEquals(1, dto01.size());

            Assert.assertEquals("ONE", dto01.get(0).getItemString1());
            Assert.assertEquals("TWO", dto01.get(0).getItemString2());
            Assert.assertEquals("THREE", dto01.get(0).getItemString3());
            Assert.assertEquals("FIVE", dto01.get(0).getItemString4());
            Assert.assertEquals(Integer.valueOf(1), dto01.get(0).getItemInteger1());
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
    public void testQuery_JPQL_IN_SUBQUERY_Literals_1() {
        if (emf == null)
            return;

        if(!POPULATED) 
            populate();

        EntityManager em = emf.createEntityManager();

        try {
            TypedQuery<EntityTbl01> query = em.createQuery(""
                    + "SELECT t FROM EntityTbl01 t "
                        + "WHERE t.itemString1 IN ("
                            + "SELECT u.itemString1 FROM EntityTbl01 u "
                            + "WHERE u.itemString2 = 'SEVEN')", EntityTbl01.class);
            List<EntityTbl01> dto01 = query.getResultList();
            Assert.assertNotNull(dto01);
            Assert.assertEquals(1, dto01.size());

            Assert.assertEquals("SIX", dto01.get(0).getItemString1());
            Assert.assertEquals("SEVEN", dto01.get(0).getItemString2());
            Assert.assertEquals("EIGHT", dto01.get(0).getItemString3());
            Assert.assertEquals("NINE", dto01.get(0).getItemString4());
            Assert.assertEquals(Integer.valueOf(2), dto01.get(0).getItemInteger1());
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
    public void testQuery_JPQL_IN_SUBQUERY_Parameters_1() {
        if (emf == null)
            return;

        if(!POPULATED) 
            populate();

        EntityManager em = emf.createEntityManager();

        try {
            TypedQuery<EntityTbl01> query = em.createQuery(""
                    + "SELECT t FROM EntityTbl01 t "
                        + "WHERE t.itemString1 IN ("
                            + "SELECT u.itemString1 FROM EntityTbl01 u "
                            + "WHERE u.itemString2 = ?2)", EntityTbl01.class);
            query.setParameter(2, "SEVEN");
            List<EntityTbl01> dto01 = query.getResultList();
            Assert.assertNotNull(dto01);
            Assert.assertEquals(1, dto01.size());

            Assert.assertEquals("SIX", dto01.get(0).getItemString1());
            Assert.assertEquals("SEVEN", dto01.get(0).getItemString2());
            Assert.assertEquals("EIGHT", dto01.get(0).getItemString3());
            Assert.assertEquals("NINE", dto01.get(0).getItemString4());
            Assert.assertEquals(Integer.valueOf(2), dto01.get(0).getItemInteger1());
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
    public void testQuery_Criteria_IN_SUBQUERY_Literals_1() {
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

            Subquery<String> subquery = cquery.subquery(String.class);
            Root<EntityTbl01> subroot = subquery.from(EntityTbl01.class);
            subquery.select(subroot.get(EntityTbl01_.itemString1));
            subquery.where(cb.equal(subroot.get(EntityTbl01_.itemString2), "SEVEN"));

            cquery.where(root.get(EntityTbl01_.itemString1).in(subquery));

            TypedQuery<EntityTbl01> query = em.createQuery(cquery);

            List<EntityTbl01> dto01 = query.getResultList();
            Assert.assertNotNull(dto01);
            Assert.assertEquals(1, dto01.size());

            Assert.assertEquals("SIX", dto01.get(0).getItemString1());
            Assert.assertEquals("SEVEN", dto01.get(0).getItemString2());
            Assert.assertEquals("EIGHT", dto01.get(0).getItemString3());
            Assert.assertEquals("NINE", dto01.get(0).getItemString4());
            Assert.assertEquals(Integer.valueOf(2), dto01.get(0).getItemInteger1());

            // equivalent test 2
            cb = em.getCriteriaBuilder();
            cquery = cb.createQuery(EntityTbl01.class);
            root = cquery.from(EntityTbl01.class);
            cquery.select(root);

            subquery = cquery.subquery(String.class);
            subroot = subquery.from(EntityTbl01.class);
            subquery.select(subroot.get(EntityTbl01_.itemString1));
            subquery.where(cb.equal(subroot.get(EntityTbl01_.itemString2), "SEVEN"));

            In<String> in2 = cb.in(root.get(EntityTbl01_.itemString1));
            cquery.where(in2.value(subquery));

            query = em.createQuery(cquery);

            dto01 = query.getResultList();
            Assert.assertNotNull(dto01);
            Assert.assertEquals(1, dto01.size());

            Assert.assertEquals("SIX", dto01.get(0).getItemString1());
            Assert.assertEquals("SEVEN", dto01.get(0).getItemString2());
            Assert.assertEquals("EIGHT", dto01.get(0).getItemString3());
            Assert.assertEquals("NINE", dto01.get(0).getItemString4());
            Assert.assertEquals(Integer.valueOf(2), dto01.get(0).getItemInteger1());
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
    public void testQuery_Criteria_IN_SUBQUERY_Parameters_1() {
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

            ParameterExpression<String> strValue1 = cb.parameter(String.class);
            Subquery<String> subquery = cquery.subquery(String.class);
            Root<EntityTbl01> subroot = subquery.from(EntityTbl01.class);
            subquery.select(subroot.get(EntityTbl01_.itemString1));
            subquery.where(cb.equal(subroot.get(EntityTbl01_.itemString2), strValue1));

            cquery.where(root.get(EntityTbl01_.itemString1).in(subquery));

            TypedQuery<EntityTbl01> query = em.createQuery(cquery);
            query.setParameter(strValue1, "SEVEN");

            List<EntityTbl01> dto01 = query.getResultList();
            Assert.assertNotNull(dto01);
            Assert.assertEquals(1, dto01.size());

            Assert.assertEquals("SIX", dto01.get(0).getItemString1());
            Assert.assertEquals("SEVEN", dto01.get(0).getItemString2());
            Assert.assertEquals("EIGHT", dto01.get(0).getItemString3());
            Assert.assertEquals("NINE", dto01.get(0).getItemString4());
            Assert.assertEquals(Integer.valueOf(2), dto01.get(0).getItemInteger1());

            // equivalent test 2
            cb = em.getCriteriaBuilder();
            cquery = cb.createQuery(EntityTbl01.class);
            root = cquery.from(EntityTbl01.class);
            cquery.select(root);

            strValue1 = cb.parameter(String.class);
            subquery = cquery.subquery(String.class);
            subroot = subquery.from(EntityTbl01.class);
            subquery.select(subroot.get(EntityTbl01_.itemString1));
            subquery.where(cb.equal(subroot.get(EntityTbl01_.itemString2), strValue1));

            In<String> in2 = cb.in(root.get(EntityTbl01_.itemString1));
            cquery.where(in2.value(subquery));

            query = em.createQuery(cquery);
            query.setParameter(strValue1, "SEVEN");

            dto01 = query.getResultList();
            Assert.assertNotNull(dto01);
            Assert.assertEquals(1, dto01.size());

            Assert.assertEquals("SIX", dto01.get(0).getItemString1());
            Assert.assertEquals("SEVEN", dto01.get(0).getItemString2());
            Assert.assertEquals("EIGHT", dto01.get(0).getItemString3());
            Assert.assertEquals("NINE", dto01.get(0).getItemString4());
            Assert.assertEquals(Integer.valueOf(2), dto01.get(0).getItemInteger1());
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
    public void testQuery_JPQL_IN_SUBQUERY_Literals_2() {
        if (emf == null)
            return;

        if(!POPULATED) 
            populate();

        EntityManager em = emf.createEntityManager();

        try {
            TypedQuery<EntityTbl01> query = em.createQuery(""
                    + "SELECT t FROM EntityTbl01 t "
                        + "WHERE t.itemString1 IN ("
                            + "SELECT u.itemString1 FROM EntityTbl01 u "
                            + "WHERE u.itemString2 IN ('TEN', 'SEVEN', 'ELEVEN'))", EntityTbl01.class);
            List<EntityTbl01> dto01 = query.getResultList();
            Assert.assertNotNull(dto01);
            Assert.assertEquals(1, dto01.size());

            Assert.assertEquals("SIX", dto01.get(0).getItemString1());
            Assert.assertEquals("SEVEN", dto01.get(0).getItemString2());
            Assert.assertEquals("EIGHT", dto01.get(0).getItemString3());
            Assert.assertEquals("NINE", dto01.get(0).getItemString4());
            Assert.assertEquals(Integer.valueOf(2), dto01.get(0).getItemInteger1());
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
    public void testQuery_JPQL_IN_SUBQUERY_Parameters_2() {
        if (emf == null)
            return;

        if(!POPULATED) 
            populate();

        EntityManager em = emf.createEntityManager();

        try {
            TypedQuery<EntityTbl01> query = em.createQuery(""
                    + "SELECT t FROM EntityTbl01 t "
                        + "WHERE t.itemString1 IN ("
                            + "SELECT u.itemString1 FROM EntityTbl01 u "
                            + "WHERE u.itemString2 IN (?2, ?3, ?4))", EntityTbl01.class);
            query.setParameter(2, "TEN");
            query.setParameter(3, "SEVEN");
            query.setParameter(4, "ELEVEN");
            List<EntityTbl01> dto01 = query.getResultList();
            Assert.assertNotNull(dto01);
            Assert.assertEquals(1, dto01.size());

            Assert.assertEquals("SIX", dto01.get(0).getItemString1());
            Assert.assertEquals("SEVEN", dto01.get(0).getItemString2());
            Assert.assertEquals("EIGHT", dto01.get(0).getItemString3());
            Assert.assertEquals("NINE", dto01.get(0).getItemString4());
            Assert.assertEquals(Integer.valueOf(2), dto01.get(0).getItemInteger1());
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
    public void testQuery_Criteria_IN_SUBQUERY_Literals_2() {
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

            Subquery<String> subquery = cquery.subquery(String.class);
            Root<EntityTbl01> subroot = subquery.from(EntityTbl01.class);
            subquery.select(subroot.get(EntityTbl01_.itemString1));
            subquery.where(subroot.get(EntityTbl01_.itemString2).in("TEN", "SEVEN", "ELEVEN"));

            cquery.where(root.get(EntityTbl01_.itemString1).in(subquery));

            TypedQuery<EntityTbl01> query = em.createQuery(cquery);

            List<EntityTbl01> dto01 = query.getResultList();
            Assert.assertNotNull(dto01);
            Assert.assertEquals(1, dto01.size());

            Assert.assertEquals("SIX", dto01.get(0).getItemString1());
            Assert.assertEquals("SEVEN", dto01.get(0).getItemString2());
            Assert.assertEquals("EIGHT", dto01.get(0).getItemString3());
            Assert.assertEquals("NINE", dto01.get(0).getItemString4());
            Assert.assertEquals(Integer.valueOf(2), dto01.get(0).getItemInteger1());

            // equivalent test 2
            cb = em.getCriteriaBuilder();
            cquery = cb.createQuery(EntityTbl01.class);
            root = cquery.from(EntityTbl01.class);
            cquery.select(root);

            subquery = cquery.subquery(String.class);
            subroot = subquery.from(EntityTbl01.class);
            subquery.select(subroot.get(EntityTbl01_.itemString1));
            In<String> in = cb.in(subroot.get(EntityTbl01_.itemString2));
            subquery.where(in.value("TEN").value("SEVEN").value("ELEVEN"));

            In<String> in2 = cb.in(root.get(EntityTbl01_.itemString1));
            cquery.where(in2.value(subquery));

            query = em.createQuery(cquery);

            dto01 = query.getResultList();
            Assert.assertNotNull(dto01);
            Assert.assertEquals(1, dto01.size());

            Assert.assertEquals("SIX", dto01.get(0).getItemString1());
            Assert.assertEquals("SEVEN", dto01.get(0).getItemString2());
            Assert.assertEquals("EIGHT", dto01.get(0).getItemString3());
            Assert.assertEquals("NINE", dto01.get(0).getItemString4());
            Assert.assertEquals(Integer.valueOf(2), dto01.get(0).getItemInteger1());
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
    public void testQuery_Criteria_IN_SUBQUERY_Parameters_2() {
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

            ParameterExpression<String> strValue1 = cb.parameter(String.class);
            ParameterExpression<String> strValue2 = cb.parameter(String.class);
            ParameterExpression<String> strValue3 = cb.parameter(String.class);
            Subquery<String> subquery = cquery.subquery(String.class);
            Root<EntityTbl01> subroot = subquery.from(EntityTbl01.class);
            subquery.select(subroot.get(EntityTbl01_.itemString1));
            subquery.where(root.get(EntityTbl01_.itemString2).in(strValue1, strValue2, strValue3));

            cquery.where(root.get(EntityTbl01_.itemString1).in(subquery));

            TypedQuery<EntityTbl01> query = em.createQuery(cquery);
            query.setParameter(strValue1, "TEN");
            query.setParameter(strValue2, "SEVEN");
            query.setParameter(strValue3, "ELEVEN");

            List<EntityTbl01> dto01 = query.getResultList();
            Assert.assertNotNull(dto01);
            Assert.assertEquals(1, dto01.size());

            Assert.assertEquals("SIX", dto01.get(0).getItemString1());
            Assert.assertEquals("SEVEN", dto01.get(0).getItemString2());
            Assert.assertEquals("EIGHT", dto01.get(0).getItemString3());
            Assert.assertEquals("NINE", dto01.get(0).getItemString4());
            Assert.assertEquals(Integer.valueOf(2), dto01.get(0).getItemInteger1());

            // equivalent test 2
            cb = em.getCriteriaBuilder();
            cquery = cb.createQuery(EntityTbl01.class);
            root = cquery.from(EntityTbl01.class);
            cquery.select(root);

            strValue1 = cb.parameter(String.class);
            strValue2 = cb.parameter(String.class);
            strValue3 = cb.parameter(String.class);
            subquery = cquery.subquery(String.class);
            subroot = subquery.from(EntityTbl01.class);
            subquery.select(subroot.get(EntityTbl01_.itemString1));
            In<String> in = cb.in(subroot.get(EntityTbl01_.itemString2));
            subquery.where(in.value(strValue1).value(strValue2).value(strValue3));

            In<String> in2 = cb.in(root.get(EntityTbl01_.itemString1));
            cquery.where(in2.value(subquery));

            query = em.createQuery(cquery);
            query.setParameter(strValue1, "TEN");
            query.setParameter(strValue2, "SEVEN");
            query.setParameter(strValue3, "ELEVEN");

            dto01 = query.getResultList();
            Assert.assertNotNull(dto01);
            Assert.assertEquals(1, dto01.size());

            Assert.assertEquals("SIX", dto01.get(0).getItemString1());
            Assert.assertEquals("SEVEN", dto01.get(0).getItemString2());
            Assert.assertEquals("EIGHT", dto01.get(0).getItemString3());
            Assert.assertEquals("NINE", dto01.get(0).getItemString4());
            Assert.assertEquals(Integer.valueOf(2), dto01.get(0).getItemInteger1());
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
            tbl1.setKeyString("Key05");
            tbl1.setItemString1("ONE");
            tbl1.setItemString2("TWO");
            tbl1.setItemString3("THREE");
            tbl1.setItemString4("FIVE");
            tbl1.setItemInteger1(1);
            em.persist(tbl1);

            EntityTbl01 tbl2 = new EntityTbl01();
            tbl2.setKeyString("Key06");
            tbl2.setItemString1("SIX");
            tbl2.setItemString2("SEVEN");
            tbl2.setItemString3("EIGHT");
            tbl2.setItemString4("NINE");
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
