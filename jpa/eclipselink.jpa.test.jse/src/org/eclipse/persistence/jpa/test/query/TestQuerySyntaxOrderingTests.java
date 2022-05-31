/*
 * Copyright (c) 2022 IBM Corporation. All rights reserved.
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
//     05/04/2022 - Will Dazey
//       - Add support for partial parameter binding for DB2
package org.eclipse.persistence.jpa.test.query;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;

import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.jpa.test.framework.SQLCallListener;
import org.eclipse.persistence.jpa.test.query.model.QuerySyntaxEntity;
import org.eclipse.persistence.jpa.test.query.model.QuerySyntaxEntity_;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestQuerySyntaxOrderingTests {

    @Emf(name = "defaultEMF",
         createTables = DDLGen.DROP_CREATE, 
         classes = { QuerySyntaxEntity.class }, 
         properties = {
             @Property(name="eclipselink.logging.level", value="FINE")
         })
    private EntityManagerFactory emf;

    @SQLCallListener(name = "defaultEMF")
    private List<String> _sql;

    @Emf(name = "allowPartialBindParametersEMF",
         createTables = DDLGen.DROP_CREATE, 
         classes = { QuerySyntaxEntity.class }, 
         properties = {
             @Property(name="eclipselink.logging.level", value="FINE"),
             @Property(name="eclipselink.jdbc.allow-partial-bind-parameters", value="true")
         })
    private EntityManagerFactory emf2;

    @SQLCallListener(name = "allowPartialBindParametersEMF")
    private List<String> _sql2;

    @Emf(name = "allowPartialBindWithBindLiteralsEMF",
         createTables = DDLGen.DROP_CREATE, 
         classes = { QuerySyntaxEntity.class }, 
         properties = {
             @Property(name="eclipselink.logging.level", value="FINE"),
             @Property(name="eclipselink.target-database-properties", value = "shouldBindLiterals=true"),
             @Property(name="eclipselink.jdbc.allow-partial-bind-parameters", value="true")
         })
    private EntityManagerFactory emf3;

    @SQLCallListener(name = "allowPartialBindWithBindLiteralsEMF")
    private List<String> _sql3;

    @Test
    public void testAscending1_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s ORDER BY ?1 ASC");
            query.setParameter(1, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY 1 ASC", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY ? ASC", _sql.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s ORDER BY 1 ASC");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY 1 ASC", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY ? ASC", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));
            cquery.orderBy(cb.asc(intParam1));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY 1 ASC", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY ? ASC", _sql.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.orderBy(cb2.asc(cb2.literal(1)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY 1 ASC", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY ? ASC", _sql.remove(0));
            }
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
    public void testAscending1_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s ORDER BY ?1 ASC");
            query.setParameter(1, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY 1 ASC", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY ? ASC", _sql2.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s ORDER BY 1 ASC");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY 1 ASC", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY ? ASC", _sql2.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));
            cquery.orderBy(cb.asc(intParam1));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY 1 ASC", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY ? ASC", _sql2.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.orderBy(cb2.asc(cb2.literal(1)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY 1 ASC", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY ? ASC", _sql2.remove(0));
            }
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
    public void testAscending1_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s ORDER BY ?1 ASC");
            query.setParameter(1, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY 1 ASC", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY ? ASC", _sql3.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s ORDER BY 1 ASC");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY 1 ASC", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY ? ASC", _sql3.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));
            cquery.orderBy(cb.asc(intParam1));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY 1 ASC", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY ? ASC", _sql3.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.orderBy(cb2.asc(cb2.literal(1)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY 1 ASC", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY ? ASC", _sql3.remove(0));
            }
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
    public void testAscending2_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT s.intVal1, s.intVal2 FROM QuerySyntaxEntity s WHERE s.intVal1 = ?1 ORDER BY ?2 ASC");
            query.setParameter(1, 36);
            query.setParameter(2, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 36) ORDER BY 1 ASC", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY ? ASC", _sql.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1, s.intVal2 FROM QuerySyntaxEntity s WHERE s.intVal1 = 36 ORDER BY 1 ASC");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 36) ORDER BY 1 ASC", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY ? ASC", _sql.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1, s.intVal2 FROM QuerySyntaxEntity s WHERE s.intVal1 = ?1 ORDER BY 1 ASC");
            query.setParameter(1, 36);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 36) ORDER BY 1 ASC", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY ? ASC", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1), root.get(QuerySyntaxEntity_.intVal2));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.intVal1), intParam1));
            cquery.orderBy(cb.desc(intParam2));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 36);
            query.setParameter(intParam2, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 36) ORDER BY 1 DESC", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY ? DESC", _sql.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1), root2.get(QuerySyntaxEntity_.intVal2));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), cb2.literal(36)));
            cquery2.orderBy(cb2.desc(cb2.literal(1)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 36) ORDER BY 1 DESC", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY ? DESC", _sql.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam4 = cb3.parameter(Integer.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1), root3.get(QuerySyntaxEntity_.intVal2));
            cquery3.where(cb3.equal(root3.get(QuerySyntaxEntity_.intVal1), intParam4));
            cquery3.orderBy(cb3.desc(cb3.literal(1)));

            query = em.createQuery(cquery3);
            query.setParameter(intParam4, 36);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 36) ORDER BY 1 DESC", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY ? DESC", _sql.remove(0));
            }
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
    public void testAscending2_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT s.intVal1, s.intVal2 FROM QuerySyntaxEntity s WHERE s.intVal1 = ?1 ORDER BY ?2 ASC");
            query.setParameter(1, 36);
            query.setParameter(2, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY 1 ASC", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY ? ASC", _sql2.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1, s.intVal2 FROM QuerySyntaxEntity s WHERE s.intVal1 = 36 ORDER BY 1 ASC");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 36) ORDER BY 1 ASC", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY ? ASC", _sql2.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1, s.intVal2 FROM QuerySyntaxEntity s WHERE s.intVal1 = ?1 ORDER BY 1 ASC");
            query.setParameter(1, 36);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY 1 ASC", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY ? ASC", _sql2.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1), root.get(QuerySyntaxEntity_.intVal2));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.intVal1), intParam1));
            cquery.orderBy(cb.desc(intParam2));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 36);
            query.setParameter(intParam2, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY 1 DESC", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY ? DESC", _sql2.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1), root2.get(QuerySyntaxEntity_.intVal2));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), cb2.literal(36)));
            cquery2.orderBy(cb2.desc(cb2.literal(1)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 36) ORDER BY 1 DESC", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY ? DESC", _sql2.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam4 = cb3.parameter(Integer.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1), root3.get(QuerySyntaxEntity_.intVal2));
            cquery3.where(cb3.equal(root3.get(QuerySyntaxEntity_.intVal1), intParam4));
            cquery3.orderBy(cb3.desc(cb3.literal(1)));

            query = em.createQuery(cquery3);
            query.setParameter(intParam4, 36);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY 1 DESC", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY ? DESC", _sql2.remove(0));
            }
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
    public void testAscending2_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("SELECT s.intVal1, s.intVal2 FROM QuerySyntaxEntity s WHERE s.intVal1 = ?1 ORDER BY ?2 ASC");
            query.setParameter(1, 36);
            query.setParameter(2, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY 1 ASC", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY ? ASC", _sql3.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1, s.intVal2 FROM QuerySyntaxEntity s WHERE s.intVal1 = 36 ORDER BY 1 ASC");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY 1 ASC", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY ? ASC", _sql3.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1, s.intVal2 FROM QuerySyntaxEntity s WHERE s.intVal1 = ?1 ORDER BY 1 ASC");
            query.setParameter(1, 36);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY 1 ASC", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY ? ASC", _sql3.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1), root.get(QuerySyntaxEntity_.intVal2));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.intVal1), intParam1));
            cquery.orderBy(cb.desc(intParam2));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 36);
            query.setParameter(intParam2, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY 1 DESC", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY ? DESC", _sql3.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1), root2.get(QuerySyntaxEntity_.intVal2));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), cb2.literal(36)));
            cquery2.orderBy(cb2.desc(cb2.literal(1)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY 1 DESC", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY ? DESC", _sql3.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam4 = cb3.parameter(Integer.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1), root3.get(QuerySyntaxEntity_.intVal2));
            cquery3.where(cb3.equal(root3.get(QuerySyntaxEntity_.intVal1), intParam4));
            cquery3.orderBy(cb3.desc(cb3.literal(1)));

            query = em.createQuery(cquery3);
            query.setParameter(intParam4, 36);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY 1 DESC", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY ? DESC", _sql3.remove(0));
            }
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
    public void testDescending1_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s ORDER BY ?1 DESC");
            query.setParameter(1, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY 1 DESC", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY ? DESC", _sql.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s ORDER BY 1 DESC");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY 1 DESC", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY ? DESC", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));
            cquery.orderBy(cb.desc(intParam1));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY 1 DESC", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY ? DESC", _sql.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.orderBy(cb2.desc(cb2.literal(1)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY 1 DESC", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY ? DESC", _sql.remove(0));
            }
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
    public void testDescending1_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s ORDER BY ?1 DESC");
            query.setParameter(1, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY 1 DESC", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY ? DESC", _sql2.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s ORDER BY 1 DESC");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY 1 DESC", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY ? DESC", _sql2.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));
            cquery.orderBy(cb.desc(intParam1));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY 1 DESC", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY ? DESC", _sql2.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.orderBy(cb2.desc(cb2.literal(1)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY 1 DESC", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY ? DESC", _sql2.remove(0));
            }
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
    public void testDescending1_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s ORDER BY ?1 DESC");
            query.setParameter(1, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY 1 DESC", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY ? DESC", _sql3.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s ORDER BY 1 DESC");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY 1 DESC", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY ? DESC", _sql3.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));
            cquery.orderBy(cb.desc(intParam1));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY 1 DESC", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY ? DESC", _sql3.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.orderBy(cb2.desc(cb2.literal(1)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY 1 DESC", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY ? DESC", _sql3.remove(0));
            }
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
    public void testDescending2_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT s.intVal1, s.intVal2 FROM QuerySyntaxEntity s WHERE s.intVal1 = ?1 ORDER BY ?2 DESC");
            query.setParameter(1, 36);
            query.setParameter(2, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 36) ORDER BY 1 DESC", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY ? DESC", _sql.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1, s.intVal2 FROM QuerySyntaxEntity s WHERE s.intVal1 = 36 ORDER BY 1 DESC");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 36) ORDER BY 1 DESC", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY ? DESC", _sql.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1, s.intVal2 FROM QuerySyntaxEntity s WHERE s.intVal1 = ?1 ORDER BY 1 DESC");
            query.setParameter(1, 36);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 36) ORDER BY 1 DESC", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY ? DESC", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1), root.get(QuerySyntaxEntity_.intVal2));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.intVal1), intParam1));
            cquery.orderBy(cb.desc(intParam2));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 36);
            query.setParameter(intParam2, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 36) ORDER BY 1 DESC", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY ? DESC", _sql.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1), root2.get(QuerySyntaxEntity_.intVal2));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), cb2.literal(36)));
            cquery2.orderBy(cb2.desc(cb2.literal(1)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 36) ORDER BY 1 DESC", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY ? DESC", _sql.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam4 = cb3.parameter(Integer.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1), root3.get(QuerySyntaxEntity_.intVal2));
            cquery3.where(cb3.equal(root3.get(QuerySyntaxEntity_.intVal1), intParam4));
            cquery3.orderBy(cb3.desc(cb3.literal(1)));

            query = em.createQuery(cquery3);
            query.setParameter(intParam4, 36);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 36) ORDER BY 1 DESC", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY ? DESC", _sql.remove(0));
            }
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
    public void testDescending2_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT s.intVal1, s.intVal2 FROM QuerySyntaxEntity s WHERE s.intVal1 = ?1 ORDER BY ?2 DESC");
            query.setParameter(1, 36);
            query.setParameter(2, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY 1 DESC", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY ? DESC", _sql2.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1, s.intVal2 FROM QuerySyntaxEntity s WHERE s.intVal1 = 36 ORDER BY 1 DESC");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 36) ORDER BY 1 DESC", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY ? DESC", _sql2.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1, s.intVal2 FROM QuerySyntaxEntity s WHERE s.intVal1 = ?1 ORDER BY 1 DESC");
            query.setParameter(1, 36);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY 1 DESC", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY ? DESC", _sql2.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1), root.get(QuerySyntaxEntity_.intVal2));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.intVal1), intParam1));
            cquery.orderBy(cb.desc(intParam2));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 36);
            query.setParameter(intParam2, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY 1 DESC", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY ? DESC", _sql2.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1), root2.get(QuerySyntaxEntity_.intVal2));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), cb2.literal(36)));
            cquery2.orderBy(cb2.desc(cb2.literal(1)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 36) ORDER BY 1 DESC", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY ? DESC", _sql2.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam4 = cb3.parameter(Integer.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1), root3.get(QuerySyntaxEntity_.intVal2));
            cquery3.where(cb3.equal(root3.get(QuerySyntaxEntity_.intVal1), intParam4));
            cquery3.orderBy(cb3.desc(cb3.literal(1)));

            query = em.createQuery(cquery3);
            query.setParameter(intParam4, 36);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY 1 DESC", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY ? DESC", _sql2.remove(0));
            }
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
    public void testDescending2_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("SELECT s.intVal1, s.intVal2 FROM QuerySyntaxEntity s WHERE s.intVal1 = ?1 ORDER BY ?2 DESC");
            query.setParameter(1, 36);
            query.setParameter(2, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY 1 DESC", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY ? DESC", _sql3.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1, s.intVal2 FROM QuerySyntaxEntity s WHERE s.intVal1 = 36 ORDER BY 1 DESC");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY 1 DESC", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY ? DESC", _sql3.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1, s.intVal2 FROM QuerySyntaxEntity s WHERE s.intVal1 = ?1 ORDER BY 1 DESC");
            query.setParameter(1, 36);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY 1 DESC", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY ? DESC", _sql3.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1), root.get(QuerySyntaxEntity_.intVal2));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.intVal1), intParam1));
            cquery.orderBy(cb.desc(intParam2));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 36);
            query.setParameter(intParam2, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY 1 DESC", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY ? DESC", _sql3.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1), root2.get(QuerySyntaxEntity_.intVal2));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), cb2.literal(36)));
            cquery2.orderBy(cb2.desc(cb2.literal(1)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY 1 DESC", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY ? DESC", _sql3.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam4 = cb3.parameter(Integer.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1), root3.get(QuerySyntaxEntity_.intVal2));
            cquery3.where(cb3.equal(root3.get(QuerySyntaxEntity_.intVal1), intParam4));
            cquery3.orderBy(cb3.desc(cb3.literal(1)));

            query = em.createQuery(cquery3);
            query.setParameter(intParam4, 36);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY 1 DESC", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY ? DESC", _sql3.remove(0));
            }
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
    public void testOrderBy1_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s ORDER BY ?1");
            query.setParameter(1, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY 1", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY ?", _sql.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s ORDER BY 1");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY 1", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY ?", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));
            cquery.orderBy(cb.desc(intParam1));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY 1 DESC", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY ? DESC", _sql.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.orderBy(cb2.desc(cb2.literal(1)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY 1 DESC", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY ? DESC", _sql.remove(0));
            }
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
    public void testOrderBy1_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s ORDER BY ?1");
            query.setParameter(1, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY 1", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY ?", _sql2.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s ORDER BY 1");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY 1", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY ?", _sql2.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));
            cquery.orderBy(cb.desc(intParam1));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY 1 DESC", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY ? DESC", _sql2.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.orderBy(cb2.desc(cb2.literal(1)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY 1 DESC", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY ? DESC", _sql2.remove(0));
            }
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
    public void testOrderBy1_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s ORDER BY ?1");
            query.setParameter(1, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY 1", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY ?", _sql3.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s ORDER BY 1");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY 1", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY ?", _sql3.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));
            cquery.orderBy(cb.desc(intParam1));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY 1 DESC", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY ? DESC", _sql3.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.orderBy(cb2.desc(cb2.literal(1)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY 1 DESC", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY ? DESC", _sql3.remove(0));
            }
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
    public void testOrderBy2_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT s.intVal1, s.intVal2 FROM QuerySyntaxEntity s ORDER BY ?1, ?2");
            query.setParameter(1, 1);
            query.setParameter(2, 2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY ORDER BY 1, 2", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY ORDER BY ?, ?", _sql.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1, s.intVal2 FROM QuerySyntaxEntity s ORDER BY 1, 2");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY ORDER BY 1, 2", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY ORDER BY ?, ?", _sql.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1, s.intVal2 FROM QuerySyntaxEntity s ORDER BY 1, ?1");
            query.setParameter(1, 2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY ORDER BY 1, 2", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY ORDER BY ?, ?", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1), root.get(QuerySyntaxEntity_.intVal2));
            cquery.orderBy(cb.desc(intParam1), cb.asc(intParam2));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 1);
            query.setParameter(intParam2, 2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY ORDER BY 1 DESC, 2 ASC", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY ORDER BY ? DESC, ? ASC", _sql.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1), root2.get(QuerySyntaxEntity_.intVal2));
            cquery2.orderBy(cb2.desc(cb2.literal(1)), cb2.asc(cb2.literal(2)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY ORDER BY 1 DESC, 2 ASC", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY ORDER BY ? DESC, ? ASC", _sql.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam3 = cb3.parameter(Integer.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1), root3.get(QuerySyntaxEntity_.intVal2));
            cquery3.orderBy(cb3.desc(cb3.literal(1)), cb3.asc(intParam3));

            query = em.createQuery(cquery3);
            query.setParameter(intParam3, 2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY ORDER BY 1 DESC, 2 ASC", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY ORDER BY ? DESC, ? ASC", _sql.remove(0));
            }
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
    public void testOrderBy2_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT s.intVal1, s.intVal2 FROM QuerySyntaxEntity s ORDER BY ?1, ?2");
            query.setParameter(1, 1);
            query.setParameter(2, 2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY ORDER BY 1, 2", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY ORDER BY ?, ?", _sql2.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1, s.intVal2 FROM QuerySyntaxEntity s ORDER BY 1, 2");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY ORDER BY 1, 2", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY ORDER BY ?, ?", _sql2.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1, s.intVal2 FROM QuerySyntaxEntity s ORDER BY 1, ?1");
            query.setParameter(1, 2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY ORDER BY 1, 2", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY ORDER BY ?, ?", _sql2.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1), root.get(QuerySyntaxEntity_.intVal2));
            cquery.orderBy(cb.desc(intParam1), cb.asc(intParam2));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 1);
            query.setParameter(intParam2, 2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY ORDER BY 1 DESC, 2 ASC", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY ORDER BY ? DESC, ? ASC", _sql2.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1), root2.get(QuerySyntaxEntity_.intVal2));
            cquery2.orderBy(cb2.desc(cb2.literal(1)), cb2.asc(cb2.literal(2)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY ORDER BY 1 DESC, 2 ASC", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY ORDER BY ? DESC, ? ASC", _sql2.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam3 = cb3.parameter(Integer.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1), root3.get(QuerySyntaxEntity_.intVal2));
            cquery3.orderBy(cb3.desc(cb3.literal(1)), cb3.asc(intParam3));

            query = em.createQuery(cquery3);
            query.setParameter(intParam3, 2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY ORDER BY 1 DESC, 2 ASC", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY ORDER BY ? DESC, ? ASC", _sql2.remove(0));
            }
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
    public void testOrderBy2_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("SELECT s.intVal1, s.intVal2 FROM QuerySyntaxEntity s ORDER BY ?1, ?2");
            query.setParameter(1, 1);
            query.setParameter(2, 2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY ORDER BY 1, 2", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY ORDER BY ?, ?", _sql3.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1, s.intVal2 FROM QuerySyntaxEntity s ORDER BY 1, 2");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY ORDER BY 1, 2", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY ORDER BY ?, ?", _sql3.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1, s.intVal2 FROM QuerySyntaxEntity s ORDER BY 1, ?1");
            query.setParameter(1, 2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY ORDER BY 1, 2", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY ORDER BY ?, ?", _sql3.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1), root.get(QuerySyntaxEntity_.intVal2));
            cquery.orderBy(cb.desc(intParam1), cb.asc(intParam2));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 1);
            query.setParameter(intParam2, 2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY ORDER BY 1 DESC, 2 ASC", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY ORDER BY ? DESC, ? ASC", _sql3.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1), root2.get(QuerySyntaxEntity_.intVal2));
            cquery2.orderBy(cb2.desc(cb2.literal(1)), cb2.asc(cb2.literal(2)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY ORDER BY 1 DESC, 2 ASC", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY ORDER BY ? DESC, ? ASC", _sql3.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam3 = cb3.parameter(Integer.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1), root3.get(QuerySyntaxEntity_.intVal2));
            cquery3.orderBy(cb3.desc(cb3.literal(1)), cb3.asc(intParam3));

            query = em.createQuery(cquery3);
            query.setParameter(intParam3, 2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY ORDER BY 1 DESC, 2 ASC", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY ORDER BY ? DESC, ? ASC", _sql3.remove(0));
            }
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
    public void testOrderBy3_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT s.intVal1, s.intVal2 FROM QuerySyntaxEntity s WHERE s.intVal1 = ?1 ORDER BY s.intVal2");
            query.setParameter(1, 5);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY INTVAL2", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY INTVAL2", _sql.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1, s.intVal2 FROM QuerySyntaxEntity s WHERE s.intVal1 = 5 ORDER BY s.intVal2");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 5) ORDER BY INTVAL2", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY INTVAL2", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1), root.get(QuerySyntaxEntity_.intVal2));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.intVal1), intParam1));
            cquery.orderBy(cb.desc(root.get(QuerySyntaxEntity_.intVal2)));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 5);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY INTVAL2 DESC", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY INTVAL2 DESC", _sql.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1), root2.get(QuerySyntaxEntity_.intVal2));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), 5));
            cquery2.orderBy(cb2.desc(root2.get(QuerySyntaxEntity_.intVal2)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 5) ORDER BY INTVAL2 DESC", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY INTVAL2 DESC", _sql.remove(0));
            }
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
    public void testOrderBy3_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT s.intVal1, s.intVal2 FROM QuerySyntaxEntity s WHERE s.intVal1 = ?1 ORDER BY s.intVal2");
            query.setParameter(1, 5);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY INTVAL2", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY INTVAL2", _sql2.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1, s.intVal2 FROM QuerySyntaxEntity s WHERE s.intVal1 = 5 ORDER BY s.intVal2");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 5) ORDER BY INTVAL2", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY INTVAL2", _sql2.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1), root.get(QuerySyntaxEntity_.intVal2));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.intVal1), intParam1));
            cquery.orderBy(cb.desc(root.get(QuerySyntaxEntity_.intVal2)));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 5);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY INTVAL2 DESC", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY INTVAL2 DESC", _sql2.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1), root2.get(QuerySyntaxEntity_.intVal2));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), 5));
            cquery2.orderBy(cb2.desc(root2.get(QuerySyntaxEntity_.intVal2)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 5) ORDER BY INTVAL2 DESC", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY INTVAL2 DESC", _sql2.remove(0));
            }
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
    public void testOrderBy3_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("SELECT s.intVal1, s.intVal2 FROM QuerySyntaxEntity s WHERE s.intVal1 = ?1 ORDER BY s.intVal2");
            query.setParameter(1, 5);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY INTVAL2", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY INTVAL2", _sql3.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1, s.intVal2 FROM QuerySyntaxEntity s WHERE s.intVal1 = 5 ORDER BY s.intVal2");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY INTVAL2", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY INTVAL2", _sql3.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1), root.get(QuerySyntaxEntity_.intVal2));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.intVal1), intParam1));
            cquery.orderBy(cb.desc(root.get(QuerySyntaxEntity_.intVal2)));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 5);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY INTVAL2 DESC", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY INTVAL2 DESC", _sql3.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1), root2.get(QuerySyntaxEntity_.intVal2));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), 5));
            cquery2.orderBy(cb2.desc(root2.get(QuerySyntaxEntity_.intVal2)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY INTVAL2 DESC", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?) ORDER BY INTVAL2 DESC", _sql3.remove(0));
            }
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }
    }

    protected DatabasePlatform getPlatform(EntityManagerFactory emf) {
        return ((EntityManagerFactoryImpl)emf).getServerSession().getPlatform();
    }
}
