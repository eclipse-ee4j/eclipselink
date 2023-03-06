/*
 * Copyright (c) 2022, 2023 IBM Corporation. All rights reserved.
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

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.ParameterExpression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

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
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestQuerySyntaxComparisonTests {

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
    public void testEquals1_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = (?1)");
            query.setParameter(1, 4);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = (4)");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));
            ParameterExpression<Long> longValue = cb.parameter(Long.class);
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.intVal1), longValue));

            query = em.createQuery(cquery);
            query.setParameter(longValue, (long)4);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), 4));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));
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
    public void testEquals1_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = (?1)");
            query.setParameter(1, 4);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = (4)");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));
            ParameterExpression<Long> longValue = cb.parameter(Long.class);
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.intVal1), longValue));

            query = em.createQuery(cquery);
            query.setParameter(longValue, (long)4);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), 4));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
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
    public void testEquals1_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = (?1)");
            query.setParameter(1, 4);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = (4)");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));
            ParameterExpression<Long> longValue = cb.parameter(Long.class);
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.intVal1), longValue));

            query = em.createQuery(cquery);
            query.setParameter(longValue, (long)4);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), 4));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
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
    public void testEquals2_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT s.intVal1, ?1 + ?2 FROM QuerySyntaxEntity s WHERE ?1 = (s.intVal1 + ABS(?2))");
            query.setParameter(1, 4);
            query.setParameter(2, -2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, (4 + -2) FROM QUERYSYNTAXENTITY WHERE (4 = (INTVAL1 + ABS(-2)))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, (? + ?) FROM QUERYSYNTAXENTITY WHERE (? = (INTVAL1 + ABS(?)))", _sql.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1, 4 + -2 FROM QuerySyntaxEntity s WHERE 4 = (s.intVal1 + ABS(-2))");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, (4 + -2) FROM QUERYSYNTAXENTITY WHERE (4 = (INTVAL1 + ABS(-2)))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, (? + ?) FROM QUERYSYNTAXENTITY WHERE (? = (INTVAL1 + ABS(?)))", _sql.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1, 4 + ?2 FROM QuerySyntaxEntity s WHERE 4 = (s.intVal1 + ABS(?2))");
            query.setParameter(2, -2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, (4 + -2) FROM QUERYSYNTAXENTITY WHERE (4 = (INTVAL1 + ABS(-2)))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, (? + ?) FROM QUERYSYNTAXENTITY WHERE (? = (INTVAL1 + ABS(?)))", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1), cb.sum(intParam1, intParam2));
            cquery.where(cb.equal(intParam1, cb.sum(root.get(QuerySyntaxEntity_.intVal1), cb.abs(intParam2))));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 4);
            query.setParameter(intParam2, -2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, (4 + -2) FROM QUERYSYNTAXENTITY WHERE (4 = (INTVAL1 + ABS(-2)))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, (? + ?) FROM QUERYSYNTAXENTITY WHERE (? = (INTVAL1 + ABS(?)))", _sql.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1), cb2.sum(4, cb2.literal(-2)));
            cquery2.where(cb2.equal(cb2.literal(4), cb2.sum(root2.get(QuerySyntaxEntity_.intVal1), cb2.abs(cb2.literal(-2)))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, (4 + -2) FROM QUERYSYNTAXENTITY WHERE (4 = (INTVAL1 + ABS(-2)))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, (? + ?) FROM QUERYSYNTAXENTITY WHERE (? = (INTVAL1 + ABS(?)))", _sql.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam6 = cb3.parameter(Integer.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1), cb3.sum(4, intParam6));
            cquery3.where(cb3.equal(cb3.literal(4), cb3.sum(root3.get(QuerySyntaxEntity_.intVal1), cb3.abs(intParam6))));

            query = em.createQuery(cquery3);
            query.setParameter(intParam6, -2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, (4 + -2) FROM QUERYSYNTAXENTITY WHERE (4 = (INTVAL1 + ABS(-2)))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, (? + ?) FROM QUERYSYNTAXENTITY WHERE (? = (INTVAL1 + ABS(?)))", _sql.remove(0));
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
    public void testEquals2_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT s.intVal1, ?1 + ?2 FROM QuerySyntaxEntity s WHERE ?1 = (s.intVal1 + ABS(?2))");
            query.setParameter(1, 4);
            query.setParameter(2, -2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1, (? + ?) FROM QUERYSYNTAXENTITY WHERE (? = (INTVAL1 + ABS(-2)))", _sql2.remove(0));
            } else if(platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, (? + -2) FROM QUERYSYNTAXENTITY WHERE (? = (INTVAL1 + ABS(?)))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, (? + ?) FROM QUERYSYNTAXENTITY WHERE (? = (INTVAL1 + ABS(?)))", _sql2.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1, 4 + -2 FROM QuerySyntaxEntity s WHERE 4 = (s.intVal1 + ABS(-2))");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1, (? + ?) FROM QUERYSYNTAXENTITY WHERE (? = (INTVAL1 + ABS(-2)))", _sql2.remove(0));
            } else if(platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, (? + -2) FROM QUERYSYNTAXENTITY WHERE (? = (INTVAL1 + ABS(?)))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, (? + ?) FROM QUERYSYNTAXENTITY WHERE (? = (INTVAL1 + ABS(?)))", _sql2.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1, 4 + ?2 FROM QuerySyntaxEntity s WHERE 4 = (s.intVal1 + ABS(?2))");
            query.setParameter(2, -2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1, (? + ?) FROM QUERYSYNTAXENTITY WHERE (? = (INTVAL1 + ABS(-2)))", _sql2.remove(0));
            } else if(platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, (? + -2) FROM QUERYSYNTAXENTITY WHERE (? = (INTVAL1 + ABS(?)))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, (? + ?) FROM QUERYSYNTAXENTITY WHERE (? = (INTVAL1 + ABS(?)))", _sql2.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1), cb.sum(intParam1, intParam2));
            cquery.where(cb.equal(intParam1, cb.sum(root.get(QuerySyntaxEntity_.intVal1), cb.abs(intParam2))));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 4);
            query.setParameter(intParam2, -2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1, (? + ?) FROM QUERYSYNTAXENTITY WHERE (? = (INTVAL1 + ABS(-2)))", _sql2.remove(0));
            } else if(platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, (? + -2) FROM QUERYSYNTAXENTITY WHERE (? = (INTVAL1 + ABS(?)))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, (? + ?) FROM QUERYSYNTAXENTITY WHERE (? = (INTVAL1 + ABS(?)))", _sql2.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1), cb2.sum(4, cb2.literal(-2)));
            cquery2.where(cb2.equal(cb2.literal(4), cb2.sum(root2.get(QuerySyntaxEntity_.intVal1), cb2.abs(cb2.literal(-2)))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1, (? + ?) FROM QUERYSYNTAXENTITY WHERE (? = (INTVAL1 + ABS(-2)))", _sql2.remove(0));
            } else if(platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, (? + -2) FROM QUERYSYNTAXENTITY WHERE (? = (INTVAL1 + ABS(?)))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, (? + ?) FROM QUERYSYNTAXENTITY WHERE (? = (INTVAL1 + ABS(?)))", _sql2.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam6 = cb3.parameter(Integer.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1), cb3.sum(4, intParam6));
            cquery3.where(cb3.equal(cb3.literal(4), cb3.sum(root3.get(QuerySyntaxEntity_.intVal1), cb3.abs(intParam6))));

            query = em.createQuery(cquery3);
            query.setParameter(intParam6, -2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1, (? + ?) FROM QUERYSYNTAXENTITY WHERE (? = (INTVAL1 + ABS(-2)))", _sql2.remove(0));
            } else if(platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, (? + -2) FROM QUERYSYNTAXENTITY WHERE (? = (INTVAL1 + ABS(?)))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, (? + ?) FROM QUERYSYNTAXENTITY WHERE (? = (INTVAL1 + ABS(?)))", _sql2.remove(0));
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
    public void testEquals2_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("SELECT s.intVal1, ?1 + ?2 FROM QuerySyntaxEntity s WHERE ?1 = (s.intVal1 + ABS(?2))");
            query.setParameter(1, 4);
            query.setParameter(2, -2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1, (? + ?) FROM QUERYSYNTAXENTITY WHERE (? = (INTVAL1 + ABS(-2)))", _sql3.remove(0));
            } else if(platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, (? + -2) FROM QUERYSYNTAXENTITY WHERE (? = (INTVAL1 + ABS(?)))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, (? + ?) FROM QUERYSYNTAXENTITY WHERE (? = (INTVAL1 + ABS(?)))", _sql3.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1, 4 + -2 FROM QuerySyntaxEntity s WHERE 4 = (s.intVal1 + ABS(-2))");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1, (? + ?) FROM QUERYSYNTAXENTITY WHERE (? = (INTVAL1 + ABS(-2)))", _sql3.remove(0));
            } else if(platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, (? + -2) FROM QUERYSYNTAXENTITY WHERE (? = (INTVAL1 + ABS(?)))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, (? + ?) FROM QUERYSYNTAXENTITY WHERE (? = (INTVAL1 + ABS(?)))", _sql3.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1, 4 + ?2 FROM QuerySyntaxEntity s WHERE 4 = (s.intVal1 + ABS(?2))");
            query.setParameter(2, -2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1, (? + ?) FROM QUERYSYNTAXENTITY WHERE (? = (INTVAL1 + ABS(-2)))", _sql3.remove(0));
            } else if(platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, (? + -2) FROM QUERYSYNTAXENTITY WHERE (? = (INTVAL1 + ABS(?)))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, (? + ?) FROM QUERYSYNTAXENTITY WHERE (? = (INTVAL1 + ABS(?)))", _sql3.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1), cb.sum(intParam1, intParam2));
            cquery.where(cb.equal(intParam1, cb.sum(root.get(QuerySyntaxEntity_.intVal1), cb.abs(intParam2))));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 4);
            query.setParameter(intParam2, -2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1, (? + ?) FROM QUERYSYNTAXENTITY WHERE (? = (INTVAL1 + ABS(-2)))", _sql3.remove(0));
            } else if(platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, (? + -2) FROM QUERYSYNTAXENTITY WHERE (? = (INTVAL1 + ABS(?)))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, (? + ?) FROM QUERYSYNTAXENTITY WHERE (? = (INTVAL1 + ABS(?)))", _sql3.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1), cb2.sum(4, cb2.literal(-2)));
            cquery2.where(cb2.equal(cb2.literal(4), cb2.sum(root2.get(QuerySyntaxEntity_.intVal1), cb2.abs(cb2.literal(-2)))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1, (? + ?) FROM QUERYSYNTAXENTITY WHERE (? = (INTVAL1 + ABS(-2)))", _sql3.remove(0));
            } else if(platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, (? + -2) FROM QUERYSYNTAXENTITY WHERE (? = (INTVAL1 + ABS(?)))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, (? + ?) FROM QUERYSYNTAXENTITY WHERE (? = (INTVAL1 + ABS(?)))", _sql3.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam6 = cb3.parameter(Integer.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1), cb3.sum(4, intParam6));
            cquery3.where(cb3.equal(cb3.literal(4), cb3.sum(root3.get(QuerySyntaxEntity_.intVal1), cb3.abs(intParam6))));

            query = em.createQuery(cquery3);
            query.setParameter(intParam6, -2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1, (? + ?) FROM QUERYSYNTAXENTITY WHERE (? = (INTVAL1 + ABS(-2)))", _sql3.remove(0));
            } else if(platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, (? + -2) FROM QUERYSYNTAXENTITY WHERE (? = (INTVAL1 + ABS(?)))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, (? + ?) FROM QUERYSYNTAXENTITY WHERE (? = (INTVAL1 + ABS(?)))", _sql3.remove(0));
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
    public void testEquals3_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s GROUP BY s.intVal1 HAVING s.intVal1 = (?1)");
            query.setParameter(1, 4);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (INTVAL1 = ?)", _sql.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s GROUP BY s.intVal1 HAVING s.intVal1 = (4)");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (INTVAL1 = ?)", _sql.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));
            ParameterExpression<Long> longValue = cb.parameter(Long.class);
            cquery.groupBy(root.get(QuerySyntaxEntity_.intVal1));
            cquery.having(cb.equal(root.get(QuerySyntaxEntity_.intVal1), longValue));

            query = em.createQuery(cquery);
            query.setParameter(longValue, (long)4);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (INTVAL1 = ?)", _sql.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.groupBy(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.having(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), 4));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (INTVAL1 = ?)", _sql.remove(0));
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
    public void testEquals3_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s GROUP BY s.intVal1 HAVING s.intVal1 = (?1)");
            query.setParameter(1, 4);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (INTVAL1 = ?)", _sql2.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s GROUP BY s.intVal1 HAVING s.intVal1 = (4)");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (INTVAL1 = ?)", _sql2.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));
            ParameterExpression<Long> longValue = cb.parameter(Long.class);
            cquery.groupBy(root.get(QuerySyntaxEntity_.intVal1));
            cquery.having(cb.equal(root.get(QuerySyntaxEntity_.intVal1), longValue));

            query = em.createQuery(cquery);
            query.setParameter(longValue, (long)4);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (INTVAL1 = ?)", _sql2.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.groupBy(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.having(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), 4));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (INTVAL1 = ?)", _sql2.remove(0));
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
    public void testEquals3_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s GROUP BY s.intVal1 HAVING s.intVal1 = (?1)");
            query.setParameter(1, 4);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (INTVAL1 = ?)", _sql3.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s GROUP BY s.intVal1 HAVING s.intVal1 = (4)");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (INTVAL1 = ?)", _sql3.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));
            ParameterExpression<Long> longValue = cb.parameter(Long.class);
            cquery.groupBy(root.get(QuerySyntaxEntity_.intVal1));
            cquery.having(cb.equal(root.get(QuerySyntaxEntity_.intVal1), longValue));

            query = em.createQuery(cquery);
            query.setParameter(longValue, (long)4);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (INTVAL1 = ?)", _sql3.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.groupBy(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.having(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), 4));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (INTVAL1 = ?)", _sql3.remove(0));
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
    public void testLessThan1_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE (?1 + ?2) < (s.intVal1)");
            query.setParameter(1, 5);
            query.setParameter(2, 10);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE ((5 + 10) < INTVAL1)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE ((? + ?) < INTVAL1)", _sql.remove(0));
            }

            query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE (5 + 10) < (s.intVal1)");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE ((5 + 10) < INTVAL1)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE ((? + ?) < INTVAL1)", _sql.remove(0));
            }

            query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE (5 + ?2) < (s.intVal1)");
            query.setParameter(2, 10);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE ((5 + 10) < INTVAL1)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE ((? + ?) < INTVAL1)", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.strVal1));
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            cquery.where(cb.lessThan(cb.sum(intParam1, intParam2), root.get(QuerySyntaxEntity_.intVal1)));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 5);
            query.setParameter(intParam2, 10);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE ((5 + 10) < INTVAL1)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE ((? + ?) < INTVAL1)", _sql.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.strVal1));
            cquery2.where(cb2.lessThan(cb2.sum(cb2.literal(5), 10), root2.get(QuerySyntaxEntity_.intVal1)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE ((5 + 10) < INTVAL1)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE ((? + ?) < INTVAL1)", _sql.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.strVal1));
            ParameterExpression<Integer> intParam3 = cb3.parameter(Integer.class);
            cquery3.where(cb3.lessThan(cb3.sum(5, intParam3), root3.get(QuerySyntaxEntity_.intVal1)));

            query = em.createQuery(cquery3);
            query.setParameter(intParam3, 10);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE ((5 + 10) < INTVAL1)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE ((? + ?) < INTVAL1)", _sql.remove(0));
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
    public void testLessThan1_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE (?1 + ?2) < (s.intVal1)");
            query.setParameter(1, 5);
            query.setParameter(2, 10);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE ((? + 10) < INTVAL1)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE ((? + ?) < INTVAL1)", _sql2.remove(0));
            }

            query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE (5 + 10) < (s.intVal1)");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE ((? + 10) < INTVAL1)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE ((? + ?) < INTVAL1)", _sql2.remove(0));
            }

            query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE (5 + ?2) < (s.intVal1)");
            query.setParameter(2, 10);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE ((? + 10) < INTVAL1)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE ((? + ?) < INTVAL1)", _sql2.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.strVal1));
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            cquery.where(cb.lessThan(cb.sum(intParam1, intParam2), root.get(QuerySyntaxEntity_.intVal1)));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 5);
            query.setParameter(intParam2, 10);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE ((? + 10) < INTVAL1)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE ((? + ?) < INTVAL1)", _sql2.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.strVal1));
            cquery2.where(cb2.lessThan(cb2.sum(cb2.literal(5), 10), root2.get(QuerySyntaxEntity_.intVal1)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE ((? + 10) < INTVAL1)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE ((? + ?) < INTVAL1)", _sql2.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.strVal1));
            ParameterExpression<Integer> intParam3 = cb3.parameter(Integer.class);
            cquery3.where(cb3.lessThan(cb3.sum(5, intParam3), root3.get(QuerySyntaxEntity_.intVal1)));

            query = em.createQuery(cquery3);
            query.setParameter(intParam3, 10);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE ((? + 10) < INTVAL1)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE ((? + ?) < INTVAL1)", _sql2.remove(0));
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
    public void testLessThan1_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE (?1 + ?2) < (s.intVal1)");
            query.setParameter(1, 5);
            query.setParameter(2, 10);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE ((? + 10) < INTVAL1)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE ((? + ?) < INTVAL1)", _sql3.remove(0));
            }

            query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE (5 + 10) < (s.intVal1)");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE ((? + 10) < INTVAL1)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE ((? + ?) < INTVAL1)", _sql3.remove(0));
            }

            query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE (5 + ?2) < (s.intVal1)");
            query.setParameter(2, 10);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE ((? + 10) < INTVAL1)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE ((? + ?) < INTVAL1)", _sql3.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.strVal1));
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            cquery.where(cb.lessThan(cb.sum(intParam1, intParam2), root.get(QuerySyntaxEntity_.intVal1)));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 5);
            query.setParameter(intParam2, 10);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE ((? + 10) < INTVAL1)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE ((? + ?) < INTVAL1)", _sql3.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.strVal1));
            cquery2.where(cb2.lessThan(cb2.sum(cb2.literal(5), 10), root2.get(QuerySyntaxEntity_.intVal1)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE ((? + 10) < INTVAL1)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE ((? + ?) < INTVAL1)", _sql3.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.strVal1));
            ParameterExpression<Integer> intParam3 = cb3.parameter(Integer.class);
            cquery3.where(cb3.lessThan(cb3.sum(5, intParam3), root3.get(QuerySyntaxEntity_.intVal1)));

            query = em.createQuery(cquery3);
            query.setParameter(intParam3, 10);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE ((? + 10) < INTVAL1)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE ((? + ?) < INTVAL1)", _sql3.remove(0));
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
    public void testLIKE1_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 LIKE ?1");
            query.setParameter(1, "%ORL%");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE STRVAL1 LIKE ?", _sql.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 LIKE '%ORL%'");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE STRVAL1 LIKE ?", _sql.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            cquery.where(cb.like(root.get(QuerySyntaxEntity_.strVal1), strParam1));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "%ORL%");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE STRVAL1 LIKE ?", _sql.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            cquery2.where(cb2.like(root2.get(QuerySyntaxEntity_.strVal1), "%ORL%"));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE STRVAL1 LIKE ?", _sql.remove(0));
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
    public void testLike1_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 LIKE ?1");
            query.setParameter(1, "%ORL%");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE STRVAL1 LIKE ?", _sql2.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 LIKE '%ORL%'");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE STRVAL1 LIKE ?", _sql2.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            cquery.where(cb.like(root.get(QuerySyntaxEntity_.strVal1), strParam1));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "%ORL%");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE STRVAL1 LIKE ?", _sql2.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            cquery2.where(cb2.like(root2.get(QuerySyntaxEntity_.strVal1), "%ORL%"));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE STRVAL1 LIKE ?", _sql2.remove(0));
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
    public void testLike1_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 LIKE ?1");
            query.setParameter(1, "%ORL%");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE STRVAL1 LIKE ?", _sql3.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 LIKE '%ORL%'");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE STRVAL1 LIKE ?", _sql3.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            cquery.where(cb.like(root.get(QuerySyntaxEntity_.strVal1), strParam1));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "%ORL%");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE STRVAL1 LIKE ?", _sql3.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            cquery2.where(cb2.like(root2.get(QuerySyntaxEntity_.strVal1), "%ORL%"));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE STRVAL1 LIKE ?", _sql3.remove(0));
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
    public void testLike2_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?1 LIKE ?2");
            query.setParameter(1, "WORLD");
            query.setParameter(2, "%ORL%");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE 'WORLD' LIKE '%ORL%'", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ? LIKE ?", _sql.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 'WORLD' LIKE '%ORL%'");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE 'WORLD' LIKE '%ORL%'", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ? LIKE ?", _sql.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 'WORLD' LIKE ?1");
            query.setParameter(1, "%ORL%");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE 'WORLD' LIKE '%ORL%'", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ? LIKE ?", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            ParameterExpression<String> strParam2 = cb.parameter(String.class);
            cquery.where(cb.like(strParam1, strParam2));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "WORLD");
            query.setParameter(strParam2, "%ORL%");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE 'WORLD' LIKE '%ORL%'", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ? LIKE ?", _sql.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            cquery2.where(cb2.like(cb2.literal("WORLD"), cb2.literal("%ORL%")));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE 'WORLD' LIKE '%ORL%'", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ? LIKE ?", _sql.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<String> strParam3 = cb3.parameter(String.class);
            cquery3.where(cb3.like(cb3.literal("WORLD"), strParam3));

            query = em.createQuery(cquery3);
            query.setParameter(strParam3, "%ORL%");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE 'WORLD' LIKE '%ORL%'", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ? LIKE ?", _sql.remove(0));
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
    public void testLike2_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?1 LIKE ?2");
            query.setParameter(1, "WORLD");
            query.setParameter(2, "%ORL%");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ? LIKE ?", _sql2.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 'WORLD' LIKE '%ORL%'");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ? LIKE ?", _sql2.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 'WORLD' LIKE ?1");
            query.setParameter(1, "%ORL%");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ? LIKE ?", _sql2.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            ParameterExpression<String> strParam2 = cb.parameter(String.class);
            cquery.where(cb.like(strParam1, strParam2));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "WORLD");
            query.setParameter(strParam2, "%ORL%");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ? LIKE ?", _sql2.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.where(cb2.like(cb2.literal("WORLD"), cb2.literal("%ORL%")));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ? LIKE ?", _sql2.remove(0));

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<String> strParam3 = cb3.parameter(String.class);
            cquery3.where(cb3.like(cb3.literal("WORLD"), strParam3));

            query = em.createQuery(cquery3);
            query.setParameter(strParam3, "%ORL%");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ? LIKE ?", _sql2.remove(0));
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
    public void testLike2_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?1 LIKE ?2");
            query.setParameter(1, "WORLD");
            query.setParameter(2, "%ORL%");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ? LIKE ?", _sql3.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 'WORLD' LIKE '%ORL%'");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ? LIKE ?", _sql3.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 'WORLD' LIKE ?1");
            query.setParameter(1, "%ORL%");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ? LIKE ?", _sql3.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            ParameterExpression<String> strParam2 = cb.parameter(String.class);
            cquery.where(cb.like(strParam1, strParam2));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "WORLD");
            query.setParameter(strParam2, "%ORL%");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ? LIKE ?", _sql3.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.where(cb2.like(cb2.literal("WORLD"), cb2.literal("%ORL%")));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ? LIKE ?", _sql3.remove(0));

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));
            ParameterExpression<String> strParam3 = cb3.parameter(String.class);
            cquery3.where(cb3.like(cb3.literal("WORLD"), strParam3));

            query = em.createQuery(cquery3);
            query.setParameter(strParam3, "%ORL%");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ? LIKE ?", _sql3.remove(0));
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
    public void testNotLike1_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 NOT LIKE ?1");
            query.setParameter(1, "%ORL%");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT (STRVAL1 LIKE ?)", _sql.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 NOT LIKE '%ORL%'");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT (STRVAL1 LIKE ?)", _sql.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            cquery.where(cb.notLike(root.get(QuerySyntaxEntity_.strVal1), strParam1));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "%ORL%");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE STRVAL1 NOT LIKE ?", _sql.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.where(cb2.notLike(root2.get(QuerySyntaxEntity_.strVal1), "%ORL%"));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE STRVAL1 NOT LIKE ?", _sql.remove(0));
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
    public void testNotLike1_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 NOT LIKE ?1");
            query.setParameter(1, "%ORL%");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT (STRVAL1 LIKE ?)", _sql2.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 NOT LIKE '%ORL%'");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT (STRVAL1 LIKE ?)", _sql2.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            cquery.where(cb.notLike(root.get(QuerySyntaxEntity_.strVal1), strParam1));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "%ORL%");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE STRVAL1 NOT LIKE ?", _sql2.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            cquery2.where(cb2.notLike(root2.get(QuerySyntaxEntity_.strVal1), "%ORL%"));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE STRVAL1 NOT LIKE ?", _sql2.remove(0));
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
    public void testNotLike1_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 NOT LIKE ?1");
            query.setParameter(1, "%ORL%");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT (STRVAL1 LIKE ?)", _sql3.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 NOT LIKE '%ORL%'");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT (STRVAL1 LIKE ?)", _sql3.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            cquery.where(cb.notLike(root.get(QuerySyntaxEntity_.strVal1), strParam1));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "%ORL%");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE STRVAL1 NOT LIKE ?", _sql3.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            cquery2.where(cb2.notLike(root2.get(QuerySyntaxEntity_.strVal1), "%ORL%"));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE STRVAL1 NOT LIKE ?", _sql3.remove(0));
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
    public void testLikeEscape1_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        /* Disabling the test for DB2 zOS because support for the ESCAPE clause is dependent on the database configuration.
         * Since we can't determine the configured support, disabling the test for now.
         * 
         * https://www.ibm.com/docs/en/db2-for-zos/11?topic=predicates-like-predicate
         * An escape clause is allowed for Unicode mixed (UTF-8) data, but is restricted for ASCII and EBCDIC mixed data.
         */
        if(platform.isDB2Z()) {
            Assume.assumeTrue("Test disabled for Platform " + platform, false);
        }

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 LIKE ?1 ESCAPE ?2");
            query.setParameter(1, "HELLO");
            query.setParameter(2, 'R');
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE STRVAL1 LIKE 'HELLO' ESCAPE 'R'", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE STRVAL1 LIKE ? ESCAPE ?", _sql.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 LIKE 'HELLO' ESCAPE 'R'");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE STRVAL1 LIKE 'HELLO' ESCAPE 'R'", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE STRVAL1 LIKE ? ESCAPE ?", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            ParameterExpression<Character> chaParam1 = cb.parameter(Character.class);
            cquery.where(cb.like(root.get(QuerySyntaxEntity_.strVal1), strParam1, chaParam1));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "HELLO");
            query.setParameter(chaParam1, 'R');
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE STRVAL1 LIKE 'HELLO' ESCAPE 'R'", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE STRVAL1 LIKE ? ESCAPE ?", _sql.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.where(cb2.like(root2.get(QuerySyntaxEntity_.strVal1), "HELLO", 'R'));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE STRVAL1 LIKE 'HELLO' ESCAPE 'R'", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE STRVAL1 LIKE ? ESCAPE ?", _sql.remove(0));
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
    public void testLikeEscape1_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        /* Disabling the test for DB2 zOS because support for the ESCAPE clause is dependent on the database configuration.
         * Since we can't determine the configured support, disabling the test for now.
         * 
         * https://www.ibm.com/docs/en/db2-for-zos/11?topic=predicates-like-predicate
         * An escape clause is allowed for Unicode mixed (UTF-8) data, but is restricted for ASCII and EBCDIC mixed data.
         */
        if(platform.isDB2Z()) {
            Assume.assumeTrue("Test disabled for Platform " + platform, false);
        }

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 LIKE ?1 ESCAPE ?2");
            query.setParameter(1, "HELLO");
            query.setParameter(2, 'R');
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE STRVAL1 LIKE ? ESCAPE ?", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE STRVAL1 LIKE ? ESCAPE ?", _sql2.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 LIKE 'HELLO' ESCAPE 'R'");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE STRVAL1 LIKE ? ESCAPE ?", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE STRVAL1 LIKE ? ESCAPE ?", _sql2.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            ParameterExpression<Character> chaParam1 = cb.parameter(Character.class);
            cquery.where(cb.like(root.get(QuerySyntaxEntity_.strVal1), strParam1, chaParam1));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "HELLO");
            query.setParameter(chaParam1, 'R');
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE STRVAL1 LIKE ? ESCAPE ?", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE STRVAL1 LIKE ? ESCAPE ?", _sql2.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.where(cb2.like(root2.get(QuerySyntaxEntity_.strVal1), "HELLO", 'R'));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE STRVAL1 LIKE ? ESCAPE ?", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE STRVAL1 LIKE ? ESCAPE ?", _sql2.remove(0));
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
    public void testLikeEscape1_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        /* Disabling the test for DB2 zOS because support for the ESCAPE clause is dependent on the database configuration.
         * Since we can't determine the configured support, disabling the test for now.
         * 
         * https://www.ibm.com/docs/en/db2-for-zos/11?topic=predicates-like-predicate
         * An escape clause is allowed for Unicode mixed (UTF-8) data, but is restricted for ASCII and EBCDIC mixed data.
         */
        if(platform.isDB2Z()) {
            Assume.assumeTrue("Test disabled for Platform " + platform, false);
        }

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 LIKE ?1 ESCAPE ?2");
            query.setParameter(1, "HELLO");
            query.setParameter(2, 'R');
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE STRVAL1 LIKE ? ESCAPE ?", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE STRVAL1 LIKE ? ESCAPE ?", _sql3.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 LIKE 'HELLO' ESCAPE 'R'");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE STRVAL1 LIKE ? ESCAPE ?", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE STRVAL1 LIKE ? ESCAPE ?", _sql3.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            ParameterExpression<Character> chaParam1 = cb.parameter(Character.class);
            cquery.where(cb.like(root.get(QuerySyntaxEntity_.strVal1), strParam1, chaParam1));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "HELLO");
            query.setParameter(chaParam1, 'R');
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE STRVAL1 LIKE ? ESCAPE ?", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE STRVAL1 LIKE ? ESCAPE ?", _sql3.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.where(cb2.like(root2.get(QuerySyntaxEntity_.strVal1), "HELLO", 'R'));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE STRVAL1 LIKE ? ESCAPE ?", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE STRVAL1 LIKE ? ESCAPE ?", _sql3.remove(0));
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
    public void testLikeEscape2_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?1 LIKE ?2 ESCAPE ?3");
            query.setParameter(1, "HELLO");
            query.setParameter(2, "WORLD");
            query.setParameter(3, 'A');
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE 'HELLO' LIKE 'WORLD' ESCAPE 'A'", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ? LIKE ? ESCAPE ?", _sql.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 'HELLO' LIKE 'WORLD' ESCAPE 'A'");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE 'HELLO' LIKE 'WORLD' ESCAPE 'A'", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ? LIKE ? ESCAPE ?", _sql.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 'HELLO' LIKE ?1 ESCAPE ?2");
            query.setParameter(1, "WORLD");
            query.setParameter(2, 'A');
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE 'HELLO' LIKE 'WORLD' ESCAPE 'A'", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ? LIKE ? ESCAPE ?", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            ParameterExpression<String> strParam2 = cb.parameter(String.class);
            ParameterExpression<Character> chaParam1 = cb.parameter(Character.class);
            cquery.where(cb.like(strParam1, strParam2, chaParam1));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "HELLO");
            query.setParameter(strParam2, "WORLD");
            query.setParameter(chaParam1, 'A');
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE 'HELLO' LIKE 'WORLD' ESCAPE 'A'", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ? LIKE ? ESCAPE ?", _sql.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.where(cb2.like(cb2.literal("HELLO"), cb2.literal("WORLD"), cb2.literal('A')));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE 'HELLO' LIKE 'WORLD' ESCAPE 'A'", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ? LIKE ? ESCAPE ?", _sql.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<String> strParam3 = cb3.parameter(String.class);
            ParameterExpression<Character> chaParam2 = cb.parameter(Character.class);
            cquery3.where(cb3.like(cb3.literal("HELLO"), strParam3, chaParam2));

            query = em.createQuery(cquery3);
            query.setParameter(strParam3, "WORLD");
            query.setParameter(chaParam2, 'A');
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE 'HELLO' LIKE 'WORLD' ESCAPE 'A'", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ? LIKE ? ESCAPE ?", _sql.remove(0));
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
    public void testLikeEscape2_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?1 LIKE ?2 ESCAPE ?3");
            query.setParameter(1, "HELLO");
            query.setParameter(2, "WORLD");
            query.setParameter(3, 'A');
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ? LIKE ? ESCAPE 'A'", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ? LIKE ? ESCAPE ?", _sql2.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 'HELLO' LIKE 'WORLD' ESCAPE 'A'");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ? LIKE ? ESCAPE 'A'", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ? LIKE ? ESCAPE ?", _sql2.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 'HELLO' LIKE ?1 ESCAPE ?2");
            query.setParameter(1, "WORLD");
            query.setParameter(2, 'A');
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ? LIKE ? ESCAPE 'A'", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ? LIKE ? ESCAPE ?", _sql2.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            ParameterExpression<String> strParam2 = cb.parameter(String.class);
            ParameterExpression<Character> chaParam1 = cb.parameter(Character.class);
            cquery.where(cb.like(strParam1, strParam2, chaParam1));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "HELLO");
            query.setParameter(strParam2, "WORLD");
            query.setParameter(chaParam1, 'A');
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ? LIKE ? ESCAPE 'A'", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ? LIKE ? ESCAPE ?", _sql2.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.where(cb2.like(cb2.literal("HELLO"), cb2.literal("WORLD"), cb2.literal('A')));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ? LIKE ? ESCAPE 'A'", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ? LIKE ? ESCAPE ?", _sql2.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<String> strParam3 = cb3.parameter(String.class);
            ParameterExpression<Character> chaParam2 = cb.parameter(Character.class);
            cquery3.where(cb3.like(cb3.literal("HELLO"), strParam3, chaParam2));

            query = em.createQuery(cquery3);
            query.setParameter(strParam3, "WORLD");
            query.setParameter(chaParam2, 'A');
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ? LIKE ? ESCAPE 'A'", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ? LIKE ? ESCAPE ?", _sql2.remove(0));
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
    public void testLikeEscape2_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?1 LIKE ?2 ESCAPE ?3");
            query.setParameter(1, "HELLO");
            query.setParameter(2, "WORLD");
            query.setParameter(3, 'A');
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ? LIKE ? ESCAPE 'A'", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ? LIKE ? ESCAPE ?", _sql3.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 'HELLO' LIKE 'WORLD' ESCAPE 'A'");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ? LIKE ? ESCAPE 'A'", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ? LIKE ? ESCAPE ?", _sql3.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 'HELLO' LIKE ?1 ESCAPE ?2");
            query.setParameter(1, "WORLD");
            query.setParameter(2, 'A');
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ? LIKE ? ESCAPE 'A'", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ? LIKE ? ESCAPE ?", _sql3.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            ParameterExpression<String> strParam2 = cb.parameter(String.class);
            ParameterExpression<Character> chaParam1 = cb.parameter(Character.class);
            cquery.where(cb.like(strParam1, strParam2, chaParam1));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "HELLO");
            query.setParameter(strParam2, "WORLD");
            query.setParameter(chaParam1, 'A');
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ? LIKE ? ESCAPE 'A'", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ? LIKE ? ESCAPE ?", _sql3.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.where(cb2.like(cb2.literal("HELLO"), cb2.literal("WORLD"), cb2.literal('A')));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ? LIKE ? ESCAPE 'A'", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ? LIKE ? ESCAPE ?", _sql3.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<String> strParam3 = cb3.parameter(String.class);
            ParameterExpression<Character> chaParam2 = cb.parameter(Character.class);
            cquery3.where(cb3.like(cb3.literal("HELLO"), strParam3, chaParam2));

            query = em.createQuery(cquery3);
            query.setParameter(strParam3, "WORLD");
            query.setParameter(chaParam2, 'A');
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ? LIKE ? ESCAPE 'A'", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ? LIKE ? ESCAPE ?", _sql3.remove(0));
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
    public void testInCollection1_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 IN ?1");
            query.setParameter(1, java.util.Arrays.asList(1, 2, 3, 5, 8, 13, 21));
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 IN (?,?,?,?,?,?,?))", _sql.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 IN (1, 2, 3, 5, 8, 13, 21)");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 IN (?, ?, ?, ?, ?, ?, ?))", _sql.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 IN (1, ?1, 3, 5, 8, ?3, ?2)");
            query.setParameter(1, 2);
            query.setParameter(2, 21);
            query.setParameter(3, 13);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 IN (?, ?, ?, ?, ?, ?, ?))", _sql.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));
            ParameterExpression<List> colValue = cb.parameter(List.class);
            cquery.where(root.get(QuerySyntaxEntity_.intVal1).in(colValue));

            query = em.createQuery(cquery);
            List<Integer> intCollection2 = java.util.Arrays.asList(1, 2, 3, 5, 8, 13, 21);
            query.setParameter(colValue, intCollection2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 IN (?,?,?,?,?,?,?))", _sql.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery2 = cb2.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            List<Integer> intCollection3 = java.util.Arrays.asList(1, 2, 3, 5, 8, 13, 21);
            cquery2.where(root2.get(QuerySyntaxEntity_.intVal1).in(intCollection3));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 IN (?, ?, ?, ?, ?, ?, ?))", _sql.remove(0));

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery3 = cb3.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));
            In<Integer> in = cb3.in(root3.get(QuerySyntaxEntity_.intVal1)).value(1).value(3).value(5).value(8);
            ParameterExpression<List> colValue2 = cb3.parameter(List.class);
            cquery3.where(cb3.or(in, root3.get(QuerySyntaxEntity_.intVal1).in(colValue2)));

            query = em.createQuery(cquery3);
            query.setParameter(colValue2, java.util.Arrays.asList(2, 13, 21));
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ((INTVAL1 IN (?, ?, ?, ?)) OR (INTVAL1 IN (?,?,?)))", _sql.remove(0));
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
    public void testInCollection1_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 IN ?1");
            query.setParameter(1, java.util.Arrays.asList(1, 2, 3, 5, 8, 13, 21));
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 IN (?,?,?,?,?,?,?))", _sql2.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 IN (1, 2, 3, 5, 8, 13, 21)");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 IN (?, ?, ?, ?, ?, ?, ?))", _sql2.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 IN (1, ?1, 3, 5, 8, ?3, ?2)");
            query.setParameter(1, 2);
            query.setParameter(2, 21);
            query.setParameter(3, 13);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 IN (?, ?, ?, ?, ?, ?, ?))", _sql2.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));
            ParameterExpression<List> colValue = cb.parameter(List.class);
            cquery.where(root.get(QuerySyntaxEntity_.intVal1).in(colValue));

            query = em.createQuery(cquery);
            List<Integer> intCollection2 = java.util.Arrays.asList(1, 2, 3, 5, 8, 13, 21);
            query.setParameter(colValue, intCollection2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 IN (?,?,?,?,?,?,?))", _sql2.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery2 = cb2.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            List<Integer> intCollection3 = java.util.Arrays.asList(1, 2, 3, 5, 8, 13, 21);
            cquery2.where(root2.get(QuerySyntaxEntity_.intVal1).in(intCollection3));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 IN (?, ?, ?, ?, ?, ?, ?))", _sql2.remove(0));

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery3 = cb3.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));
            In<Integer> in = cb3.in(root3.get(QuerySyntaxEntity_.intVal1)).value(1).value(3).value(5).value(8);
            ParameterExpression<List> colValue2 = cb3.parameter(List.class);
            cquery3.where(cb3.or(in, root3.get(QuerySyntaxEntity_.intVal1).in(colValue2)));

            query = em.createQuery(cquery3);
            query.setParameter(colValue2, java.util.Arrays.asList(2, 13, 21));
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ((INTVAL1 IN (?, ?, ?, ?)) OR (INTVAL1 IN (?,?,?)))", _sql2.remove(0));
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
    public void testInCollection1_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 IN ?1");
            query.setParameter(1, java.util.Arrays.asList(1, 2, 3, 5, 8, 13, 21));
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 IN (?,?,?,?,?,?,?))", _sql3.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 IN (1, 2, 3, 5, 8, 13, 21)");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 IN (?, ?, ?, ?, ?, ?, ?))", _sql3.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 IN (1, ?1, 3, 5, 8, ?3, ?2)");
            query.setParameter(1, 2);
            query.setParameter(2, 21);
            query.setParameter(3, 13);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 IN (?, ?, ?, ?, ?, ?, ?))", _sql3.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));
            ParameterExpression<List> colValue = cb.parameter(List.class);
            cquery.where(root.get(QuerySyntaxEntity_.intVal1).in(colValue));

            query = em.createQuery(cquery);
            List<Integer> intCollection2 = java.util.Arrays.asList(1, 2, 3, 5, 8, 13, 21);
            query.setParameter(colValue, intCollection2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 IN (?,?,?,?,?,?,?))", _sql3.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery2 = cb2.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            List<Integer> intCollection3 = java.util.Arrays.asList(1, 2, 3, 5, 8, 13, 21);
            cquery2.where(root2.get(QuerySyntaxEntity_.intVal1).in(intCollection3));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 IN (?, ?, ?, ?, ?, ?, ?))", _sql3.remove(0));

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery3 = cb3.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));
            In<Integer> in = cb3.in(root3.get(QuerySyntaxEntity_.intVal1)).value(1).value(3).value(5).value(8);
            ParameterExpression<List> colValue2 = cb3.parameter(List.class);
            cquery3.where(cb3.or(in, root3.get(QuerySyntaxEntity_.intVal1).in(colValue2)));

            query = em.createQuery(cquery3);
            query.setParameter(colValue2, java.util.Arrays.asList(2, 13, 21));
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ((INTVAL1 IN (?, ?, ?, ?)) OR (INTVAL1 IN (?,?,?)))", _sql3.remove(0));
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
    public void testInCollection2_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 IN (?1, ?2, ?3, ?4, ?5)");
            query.setParameter(1, 1);
            query.setParameter(2, 2);
            query.setParameter(3, 3);
            query.setParameter(4, 5);
            query.setParameter(5, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 IN (?, ?, ?, ?, ?))", _sql.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 IN (1, 2, 3, 5, 8)");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 IN (?, ?, ?, ?, ?))", _sql.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 IN (1, ?1, 3, 5, ?3)");
            query.setParameter(1, 2);
            query.setParameter(3, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 IN (?, ?, ?, ?, ?))", _sql.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));
            ParameterExpression<Integer> intValue1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intValue2 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intValue3 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intValue4 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intValue5 = cb.parameter(Integer.class);
            cquery.where(root.get(QuerySyntaxEntity_.intVal1).in(intValue1, intValue2, intValue3, intValue4, intValue5));

            query = em.createQuery(cquery);
            query.setParameter(intValue1, 1);
            query.setParameter(intValue2, 2);
            query.setParameter(intValue3, 3);
            query.setParameter(intValue4, 5);
            query.setParameter(intValue5, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 IN (?, ?, ?, ?, ?))", _sql.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery2 = cb2.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.where(root2.get(QuerySyntaxEntity_.intVal1).in(1, 2, 3, 5, 8));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 IN (?, ?, ?, ?, ?))", _sql.remove(0));

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery3 = cb3.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intValue6 = cb3.parameter(Integer.class);
            ParameterExpression<Integer> intValue7 = cb3.parameter(Integer.class);

            In<Integer> in = cb3.in(root3.get(QuerySyntaxEntity_.intVal1));
            in = in.value(1);
            in = in.value(intValue6);
            in = in.value(3).value(5);
            in = in.value(intValue7);
            cquery3.where(in);

            query = em.createQuery(cquery3);
            query.setParameter(intValue6, 2);
            query.setParameter(intValue7, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 IN (?, ?, ?, ?, ?))", _sql.remove(0));
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
    public void testInCollection2_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 IN (?1, ?2, ?3, ?4, ?5)");
            query.setParameter(1, 1);
            query.setParameter(2, 2);
            query.setParameter(3, 3);
            query.setParameter(4, 5);
            query.setParameter(5, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 IN (?, ?, ?, ?, ?))", _sql2.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 IN (1, 2, 3, 5, 8)");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 IN (?, ?, ?, ?, ?))", _sql2.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 IN (1, ?1, 3, 5, ?3)");
            query.setParameter(1, 2);
            query.setParameter(3, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 IN (?, ?, ?, ?, ?))", _sql2.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));
            ParameterExpression<Integer> intValue1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intValue2 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intValue3 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intValue4 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intValue5 = cb.parameter(Integer.class);
            cquery.where(root.get(QuerySyntaxEntity_.intVal1).in(intValue1, intValue2, intValue3, intValue4, intValue5));

            query = em.createQuery(cquery);
            query.setParameter(intValue1, 1);
            query.setParameter(intValue2, 2);
            query.setParameter(intValue3, 3);
            query.setParameter(intValue4, 5);
            query.setParameter(intValue5, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 IN (?, ?, ?, ?, ?))", _sql2.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery2 = cb2.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.where(root2.get(QuerySyntaxEntity_.intVal1).in(1, 2, 3, 5, 8));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 IN (?, ?, ?, ?, ?))", _sql2.remove(0));

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery3 = cb3.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intValue6 = cb3.parameter(Integer.class);
            ParameterExpression<Integer> intValue7 = cb3.parameter(Integer.class);

            In<Integer> in = cb3.in(root3.get(QuerySyntaxEntity_.intVal1));
            in = in.value(1);
            in = in.value(intValue6);
            in = in.value(3).value(5);
            in = in.value(intValue7);
            cquery3.where(in);

            query = em.createQuery(cquery3);
            query.setParameter(intValue6, 2);
            query.setParameter(intValue7, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 IN (?, ?, ?, ?, ?))", _sql2.remove(0));
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
    public void testInCollection2_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 IN (?1, ?2, ?3, ?4, ?5)");
            query.setParameter(1, 1);
            query.setParameter(2, 2);
            query.setParameter(3, 3);
            query.setParameter(4, 5);
            query.setParameter(5, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 IN (?, ?, ?, ?, ?))", _sql3.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 IN (1, 2, 3, 5, 8)");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 IN (?, ?, ?, ?, ?))", _sql3.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 IN (1, ?1, 3, 5, ?3)");
            query.setParameter(1, 2);
            query.setParameter(3, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 IN (?, ?, ?, ?, ?))", _sql3.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));
            ParameterExpression<Integer> intValue1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intValue2 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intValue3 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intValue4 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intValue5 = cb.parameter(Integer.class);
            cquery.where(root.get(QuerySyntaxEntity_.intVal1).in(intValue1, intValue2, intValue3, intValue4, intValue5));

            query = em.createQuery(cquery);
            query.setParameter(intValue1, 1);
            query.setParameter(intValue2, 2);
            query.setParameter(intValue3, 3);
            query.setParameter(intValue4, 5);
            query.setParameter(intValue5, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 IN (?, ?, ?, ?, ?))", _sql3.remove(0));

            // IN function test #2 with CriteriaBuilder literal values
            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery2 = cb2.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.where(root2.get(QuerySyntaxEntity_.intVal1).in(1, 2, 3, 5, 8));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 IN (?, ?, ?, ?, ?))", _sql3.remove(0));

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery3 = cb3.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intValue6 = cb3.parameter(Integer.class);
            ParameterExpression<Integer> intValue7 = cb3.parameter(Integer.class);

            In<Integer> in = cb3.in(root3.get(QuerySyntaxEntity_.intVal1));
            in = in.value(1);
            in = in.value(intValue6);
            in = in.value(3).value(5);
            in = in.value(intValue7);
            cquery3.where(in);

            query = em.createQuery(cquery3);
            query.setParameter(intValue6, 2);
            query.setParameter(intValue7, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 IN (?, ?, ?, ?, ?))", _sql3.remove(0));
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
    public void testInCollection3_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s GROUP BY s.intVal1 HAVING s.intVal1 IN ?1");
            query.setParameter(1, java.util.Arrays.asList(1, 2, 3, 5, 8, 13, 21));
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (INTVAL1 IN (?,?,?,?,?,?,?))", _sql.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s GROUP BY s.intVal1 HAVING s.intVal1 IN (1, 2, 3, 5, 8, 13, 21)");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (INTVAL1 IN (?, ?, ?, ?, ?, ?, ?))", _sql.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s GROUP BY s.intVal1 HAVING s.intVal1 IN (1, ?1, 3, 5, 8, ?3, ?2)");
            query.setParameter(1, 2);
            query.setParameter(2, 21);
            query.setParameter(3, 13);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (INTVAL1 IN (?, ?, ?, ?, ?, ?, ?))", _sql.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));
            ParameterExpression<List> colValue = cb.parameter(List.class);
            cquery.groupBy(root.get(QuerySyntaxEntity_.intVal1));
            cquery.having(root.get(QuerySyntaxEntity_.intVal1).in(colValue));

            query = em.createQuery(cquery);
            List<Integer> intCollection2 = java.util.Arrays.asList(1, 2, 3, 5, 8, 13, 21);
            query.setParameter(colValue, intCollection2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (INTVAL1 IN (?,?,?,?,?,?,?))", _sql.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery2 = cb2.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            List<Integer> intCollection3 = java.util.Arrays.asList(1, 2, 3, 5, 8, 13, 21);
            cquery2.groupBy(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.having(root2.get(QuerySyntaxEntity_.intVal1).in(intCollection3));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (INTVAL1 IN (?, ?, ?, ?, ?, ?, ?))", _sql.remove(0));

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery3 = cb3.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));
            In<Integer> in = cb3.in(root3.get(QuerySyntaxEntity_.intVal1)).value(1).value(3).value(5).value(8);
            ParameterExpression<List> colValue2 = cb3.parameter(List.class);
            cquery3.groupBy(root3.get(QuerySyntaxEntity_.intVal1));
            cquery3.having(cb3.or(in, root3.get(QuerySyntaxEntity_.intVal1).in(colValue2)));

            query = em.createQuery(cquery3);
            query.setParameter(colValue2, java.util.Arrays.asList(2, 13, 21));
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING ((INTVAL1 IN (?, ?, ?, ?)) OR (INTVAL1 IN (?,?,?)))", _sql.remove(0));
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
    public void testInCollection3_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s GROUP BY s.intVal1 HAVING s.intVal1 IN ?1");
            query.setParameter(1, java.util.Arrays.asList(1, 2, 3, 5, 8, 13, 21));
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (INTVAL1 IN (?,?,?,?,?,?,?))", _sql2.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s GROUP BY s.intVal1 HAVING s.intVal1 IN (1, 2, 3, 5, 8, 13, 21)");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (INTVAL1 IN (?, ?, ?, ?, ?, ?, ?))", _sql2.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s GROUP BY s.intVal1 HAVING s.intVal1 IN (1, ?1, 3, 5, 8, ?3, ?2)");
            query.setParameter(1, 2);
            query.setParameter(2, 21);
            query.setParameter(3, 13);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (INTVAL1 IN (?, ?, ?, ?, ?, ?, ?))", _sql2.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));
            ParameterExpression<List> colValue = cb.parameter(List.class);
            cquery.groupBy(root.get(QuerySyntaxEntity_.intVal1));
            cquery.having(root.get(QuerySyntaxEntity_.intVal1).in(colValue));

            query = em.createQuery(cquery);
            List<Integer> intCollection2 = java.util.Arrays.asList(1, 2, 3, 5, 8, 13, 21);
            query.setParameter(colValue, intCollection2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (INTVAL1 IN (?,?,?,?,?,?,?))", _sql2.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery2 = cb2.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            List<Integer> intCollection3 = java.util.Arrays.asList(1, 2, 3, 5, 8, 13, 21);
            cquery2.groupBy(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.having(root2.get(QuerySyntaxEntity_.intVal1).in(intCollection3));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (INTVAL1 IN (?, ?, ?, ?, ?, ?, ?))", _sql2.remove(0));

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery3 = cb3.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));
            In<Integer> in = cb3.in(root3.get(QuerySyntaxEntity_.intVal1)).value(1).value(3).value(5).value(8);
            ParameterExpression<List> colValue2 = cb3.parameter(List.class);
            cquery3.groupBy(root3.get(QuerySyntaxEntity_.intVal1));
            cquery3.having(cb3.or(in, root3.get(QuerySyntaxEntity_.intVal1).in(colValue2)));

            query = em.createQuery(cquery3);
            query.setParameter(colValue2, java.util.Arrays.asList(2, 13, 21));
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING ((INTVAL1 IN (?, ?, ?, ?)) OR (INTVAL1 IN (?,?,?)))", _sql2.remove(0));
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
    public void testInCollection3_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s GROUP BY s.intVal1 HAVING s.intVal1 IN ?1");
            query.setParameter(1, java.util.Arrays.asList(1, 2, 3, 5, 8, 13, 21));
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (INTVAL1 IN (?,?,?,?,?,?,?))", _sql3.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s GROUP BY s.intVal1 HAVING s.intVal1 IN (1, 2, 3, 5, 8, 13, 21)");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (INTVAL1 IN (?, ?, ?, ?, ?, ?, ?))", _sql3.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s GROUP BY s.intVal1 HAVING s.intVal1 IN (1, ?1, 3, 5, 8, ?3, ?2)");
            query.setParameter(1, 2);
            query.setParameter(2, 21);
            query.setParameter(3, 13);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (INTVAL1 IN (?, ?, ?, ?, ?, ?, ?))", _sql3.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));
            ParameterExpression<List> colValue = cb.parameter(List.class);
            cquery.groupBy(root.get(QuerySyntaxEntity_.intVal1));
            cquery.having(root.get(QuerySyntaxEntity_.intVal1).in(colValue));

            query = em.createQuery(cquery);
            List<Integer> intCollection2 = java.util.Arrays.asList(1, 2, 3, 5, 8, 13, 21);
            query.setParameter(colValue, intCollection2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (INTVAL1 IN (?,?,?,?,?,?,?))", _sql3.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery2 = cb2.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            List<Integer> intCollection3 = java.util.Arrays.asList(1, 2, 3, 5, 8, 13, 21);
            cquery2.groupBy(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.having(root2.get(QuerySyntaxEntity_.intVal1).in(intCollection3));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (INTVAL1 IN (?, ?, ?, ?, ?, ?, ?))", _sql3.remove(0));

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery3 = cb3.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));
            In<Integer> in = cb3.in(root3.get(QuerySyntaxEntity_.intVal1)).value(1).value(3).value(5).value(8);
            ParameterExpression<List> colValue2 = cb3.parameter(List.class);
            cquery3.groupBy(root3.get(QuerySyntaxEntity_.intVal1));
            cquery3.having(cb3.or(in, root3.get(QuerySyntaxEntity_.intVal1).in(colValue2)));

            query = em.createQuery(cquery3);
            query.setParameter(colValue2, java.util.Arrays.asList(2, 13, 21));
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING ((INTVAL1 IN (?, ?, ?, ?)) OR (INTVAL1 IN (?,?,?)))", _sql3.remove(0));
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
    public void testInCollection4_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s GROUP BY s.intVal1 HAVING s.intVal1 IN (?1, ?2, ?3, ?4, ?5)");
            query.setParameter(1, 1);
            query.setParameter(2, 2);
            query.setParameter(3, 3);
            query.setParameter(4, 5);
            query.setParameter(5, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (INTVAL1 IN (?, ?, ?, ?, ?))", _sql.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s GROUP BY s.intVal1 HAVING s.intVal1 IN (1, 2, 3, 5, 8)");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (INTVAL1 IN (?, ?, ?, ?, ?))", _sql.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s GROUP BY s.intVal1 HAVING s.intVal1 IN (1, ?1, 3, 5, ?3)");
            query.setParameter(1, 2);
            query.setParameter(3, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (INTVAL1 IN (?, ?, ?, ?, ?))", _sql.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));
            ParameterExpression<Integer> intValue1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intValue2 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intValue3 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intValue4 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intValue5 = cb.parameter(Integer.class);
            cquery.groupBy(root.get(QuerySyntaxEntity_.intVal1));
            cquery.having(root.get(QuerySyntaxEntity_.intVal1).in(intValue1, intValue2, intValue3, intValue4, intValue5));

            query = em.createQuery(cquery);
            query.setParameter(intValue1, 1);
            query.setParameter(intValue2, 2);
            query.setParameter(intValue3, 3);
            query.setParameter(intValue4, 5);
            query.setParameter(intValue5, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (INTVAL1 IN (?, ?, ?, ?, ?))", _sql.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery2 = cb2.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.groupBy(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.having(root2.get(QuerySyntaxEntity_.intVal1).in(1, 2, 3, 5, 8));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (INTVAL1 IN (?, ?, ?, ?, ?))", _sql.remove(0));

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery3 = cb3.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));
            cquery3.groupBy(root3.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intValue6 = cb3.parameter(Integer.class);
            ParameterExpression<Integer> intValue7 = cb3.parameter(Integer.class);

            In<Integer> in = cb3.in(root3.get(QuerySyntaxEntity_.intVal1));
            in = in.value(1);
            in = in.value(intValue6);
            in = in.value(3).value(5);
            in = in.value(intValue7);
            cquery3.having(in);

            query = em.createQuery(cquery3);
            query.setParameter(intValue6, 2);
            query.setParameter(intValue7, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (INTVAL1 IN (?, ?, ?, ?, ?))", _sql.remove(0));
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
    public void testInCollection4_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s GROUP BY s.intVal1 HAVING s.intVal1 IN (?1, ?2, ?3, ?4, ?5)");
            query.setParameter(1, 1);
            query.setParameter(2, 2);
            query.setParameter(3, 3);
            query.setParameter(4, 5);
            query.setParameter(5, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (INTVAL1 IN (?, ?, ?, ?, ?))", _sql2.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s GROUP BY s.intVal1 HAVING s.intVal1 IN (1, 2, 3, 5, 8)");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (INTVAL1 IN (?, ?, ?, ?, ?))", _sql2.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s GROUP BY s.intVal1 HAVING s.intVal1 IN (1, ?1, 3, 5, ?3)");
            query.setParameter(1, 2);
            query.setParameter(3, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (INTVAL1 IN (?, ?, ?, ?, ?))", _sql2.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));
            ParameterExpression<Integer> intValue1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intValue2 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intValue3 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intValue4 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intValue5 = cb.parameter(Integer.class);
            cquery.groupBy(root.get(QuerySyntaxEntity_.intVal1));
            cquery.having(root.get(QuerySyntaxEntity_.intVal1).in(intValue1, intValue2, intValue3, intValue4, intValue5));

            query = em.createQuery(cquery);
            query.setParameter(intValue1, 1);
            query.setParameter(intValue2, 2);
            query.setParameter(intValue3, 3);
            query.setParameter(intValue4, 5);
            query.setParameter(intValue5, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (INTVAL1 IN (?, ?, ?, ?, ?))", _sql2.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery2 = cb2.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.groupBy(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.having(root2.get(QuerySyntaxEntity_.intVal1).in(1, 2, 3, 5, 8));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (INTVAL1 IN (?, ?, ?, ?, ?))", _sql2.remove(0));

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery3 = cb3.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));
            cquery3.groupBy(root3.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intValue6 = cb3.parameter(Integer.class);
            ParameterExpression<Integer> intValue7 = cb3.parameter(Integer.class);

            In<Integer> in = cb3.in(root3.get(QuerySyntaxEntity_.intVal1));
            in = in.value(1);
            in = in.value(intValue6);
            in = in.value(3).value(5);
            in = in.value(intValue7);
            cquery3.having(in);

            query = em.createQuery(cquery3);
            query.setParameter(intValue6, 2);
            query.setParameter(intValue7, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (INTVAL1 IN (?, ?, ?, ?, ?))", _sql2.remove(0));
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
    public void testInCollection4_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s GROUP BY s.intVal1 HAVING s.intVal1 IN (?1, ?2, ?3, ?4, ?5)");
            query.setParameter(1, 1);
            query.setParameter(2, 2);
            query.setParameter(3, 3);
            query.setParameter(4, 5);
            query.setParameter(5, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (INTVAL1 IN (?, ?, ?, ?, ?))", _sql3.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s GROUP BY s.intVal1 HAVING s.intVal1 IN (1, 2, 3, 5, 8)");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (INTVAL1 IN (?, ?, ?, ?, ?))", _sql3.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s GROUP BY s.intVal1 HAVING s.intVal1 IN (1, ?1, 3, 5, ?3)");
            query.setParameter(1, 2);
            query.setParameter(3, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (INTVAL1 IN (?, ?, ?, ?, ?))", _sql3.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));
            ParameterExpression<Integer> intValue1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intValue2 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intValue3 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intValue4 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intValue5 = cb.parameter(Integer.class);
            cquery.groupBy(root.get(QuerySyntaxEntity_.intVal1));
            cquery.having(root.get(QuerySyntaxEntity_.intVal1).in(intValue1, intValue2, intValue3, intValue4, intValue5));

            query = em.createQuery(cquery);
            query.setParameter(intValue1, 1);
            query.setParameter(intValue2, 2);
            query.setParameter(intValue3, 3);
            query.setParameter(intValue4, 5);
            query.setParameter(intValue5, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (INTVAL1 IN (?, ?, ?, ?, ?))", _sql3.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery2 = cb2.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.groupBy(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.having(root2.get(QuerySyntaxEntity_.intVal1).in(1, 2, 3, 5, 8));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (INTVAL1 IN (?, ?, ?, ?, ?))", _sql3.remove(0));

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery3 = cb3.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));
            cquery3.groupBy(root3.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intValue6 = cb3.parameter(Integer.class);
            ParameterExpression<Integer> intValue7 = cb3.parameter(Integer.class);

            In<Integer> in = cb3.in(root3.get(QuerySyntaxEntity_.intVal1));
            in = in.value(1);
            in = in.value(intValue6);
            in = in.value(3).value(5);
            in = in.value(intValue7);
            cquery3.having(in);

            query = em.createQuery(cquery3);
            query.setParameter(intValue6, 2);
            query.setParameter(intValue7, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (INTVAL1 IN (?, ?, ?, ?, ?))", _sql3.remove(0));
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
    public void testInSubquery1_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?1 IN (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 = ?2)");
            query.setParameter(1, 5);
            query.setParameter(2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?))", _sql.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 5 IN (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 = 9)");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?))", _sql.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 5 IN (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 = ?1)");
            query.setParameter(1, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?))", _sql.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intValue2 = cb.parameter(Integer.class);
            Subquery<Integer> subquery = cquery.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot = subquery.from(QuerySyntaxEntity.class);
            subquery.select(subroot.get(QuerySyntaxEntity_.intVal2));
            subquery.where(cb.equal(subroot.get(QuerySyntaxEntity_.intVal2), intValue2));

            ParameterExpression<Integer> intValue1 = cb.parameter(Integer.class);
            cquery.where(intValue1.in(subquery));

            query = em.createQuery(cquery);
            query.setParameter(intValue1, 5);
            query.setParameter(intValue2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?))", _sql.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery2 = cb2.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            Subquery<Integer> subquery2 = cquery2.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot2 = subquery2.from(QuerySyntaxEntity.class);
            subquery2.select(subroot2.get(QuerySyntaxEntity_.intVal2));
            subquery2.where(cb2.equal(subroot2.get(QuerySyntaxEntity_.intVal2), 9));

            cquery2.where(cb2.literal(5).in(subquery2));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?))", _sql.remove(0));

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery3 = cb3.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intValue3 = cb3.parameter(Integer.class);
            Subquery<Integer> subquery3 = cquery3.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot3 = subquery3.from(QuerySyntaxEntity.class);
            subquery3.select(subroot3.get(QuerySyntaxEntity_.intVal2));
            subquery3.where(cb3.equal(subroot3.get(QuerySyntaxEntity_.intVal2), intValue3));

            cquery3.where(cb3.literal(5).in(subquery3));

            query = em.createQuery(cquery3);
            query.setParameter(intValue3, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?))", _sql.remove(0));
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
    public void testInSubquery1_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?1 IN (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 = ?2)");
            query.setParameter(1, 5);
            query.setParameter(2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?))", _sql2.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 5 IN (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 = 9)");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?))", _sql2.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 5 IN (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 = ?1)");
            query.setParameter(1, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?))", _sql2.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intValue2 = cb.parameter(Integer.class);
            Subquery<Integer> subquery = cquery.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot = subquery.from(QuerySyntaxEntity.class);
            subquery.select(subroot.get(QuerySyntaxEntity_.intVal2));
            subquery.where(cb.equal(subroot.get(QuerySyntaxEntity_.intVal2), intValue2));

            ParameterExpression<Integer> intValue1 = cb.parameter(Integer.class);
            cquery.where(intValue1.in(subquery));

            query = em.createQuery(cquery);
            query.setParameter(intValue1, 5);
            query.setParameter(intValue2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?))", _sql2.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery2 = cb2.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            Subquery<Integer> subquery2 = cquery2.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot2 = subquery2.from(QuerySyntaxEntity.class);
            subquery2.select(subroot2.get(QuerySyntaxEntity_.intVal2));
            subquery2.where(cb2.equal(subroot2.get(QuerySyntaxEntity_.intVal2), 9));

            cquery2.where(cb2.literal(5).in(subquery2));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?))", _sql2.remove(0));

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery3 = cb3.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intValue3 = cb3.parameter(Integer.class);
            Subquery<Integer> subquery3 = cquery3.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot3 = subquery3.from(QuerySyntaxEntity.class);
            subquery3.select(subroot3.get(QuerySyntaxEntity_.intVal2));
            subquery3.where(cb3.equal(subroot3.get(QuerySyntaxEntity_.intVal2), intValue3));

            cquery3.where(cb3.literal(5).in(subquery3));

            query = em.createQuery(cquery3);
            query.setParameter(intValue3, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?))", _sql2.remove(0));
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
    public void testInSubquery1_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?1 IN (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 = ?2)");
            query.setParameter(1, 5);
            query.setParameter(2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?))", _sql3.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 5 IN (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 = 9)");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?))", _sql3.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 5 IN (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 = ?1)");
            query.setParameter(1, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?))", _sql3.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intValue2 = cb.parameter(Integer.class);
            Subquery<Integer> subquery = cquery.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot = subquery.from(QuerySyntaxEntity.class);
            subquery.select(subroot.get(QuerySyntaxEntity_.intVal2));
            subquery.where(cb.equal(subroot.get(QuerySyntaxEntity_.intVal2), intValue2));

            ParameterExpression<Integer> intValue1 = cb.parameter(Integer.class);
            cquery.where(intValue1.in(subquery));

            query = em.createQuery(cquery);
            query.setParameter(intValue1, 5);
            query.setParameter(intValue2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?))", _sql3.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery2 = cb2.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            Subquery<Integer> subquery2 = cquery2.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot2 = subquery2.from(QuerySyntaxEntity.class);
            subquery2.select(subroot2.get(QuerySyntaxEntity_.intVal2));
            subquery2.where(cb2.equal(subroot2.get(QuerySyntaxEntity_.intVal2), 9));

            cquery2.where(cb2.literal(5).in(subquery2));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?))", _sql3.remove(0));

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery3 = cb3.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intValue3 = cb3.parameter(Integer.class);
            Subquery<Integer> subquery3 = cquery3.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot3 = subquery3.from(QuerySyntaxEntity.class);
            subquery3.select(subroot3.get(QuerySyntaxEntity_.intVal2));
            subquery3.where(cb3.equal(subroot3.get(QuerySyntaxEntity_.intVal2), intValue3));

            cquery3.where(cb3.literal(5).in(subquery3));

            query = em.createQuery(cquery3);
            query.setParameter(intValue3, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?))", _sql3.remove(0));
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
    public void testInSubquery2_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?1 IN (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 = ?2)");
            query.setParameter(1, 5);
            query.setParameter(2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?))", _sql.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 5 IN (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 = 9)");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?))", _sql.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?1 IN (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 = 9)");
            query.setParameter(1, 5);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?))", _sql.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intValue2 = cb.parameter(Integer.class);
            Subquery<Integer> subquery = cquery.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot = subquery.from(QuerySyntaxEntity.class);
            subquery.select(subroot.get(QuerySyntaxEntity_.intVal2));
            subquery.where(cb.equal(subroot.get(QuerySyntaxEntity_.intVal2), intValue2));

            ParameterExpression<Integer> intValue1 = cb.parameter(Integer.class);
            cquery.where(cb.in(intValue1).value(subquery));

            query = em.createQuery(cquery);
            query.setParameter(intValue1, 5);
            query.setParameter(intValue2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?))", _sql.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery2 = cb2.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            Subquery<Integer> subquery2 = cquery2.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot2 = subquery2.from(QuerySyntaxEntity.class);
            subquery2.select(subroot2.get(QuerySyntaxEntity_.intVal2));
            subquery2.where(cb2.equal(subroot2.get(QuerySyntaxEntity_.intVal2), 9));

            cquery2.where(cb2.in(cb2.literal(5)).value(subquery2));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?))", _sql.remove(0));

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery3 = cb3.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));

            Subquery<Integer> subquery3 = cquery3.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot3 = subquery3.from(QuerySyntaxEntity.class);
            subquery3.select(subroot3.get(QuerySyntaxEntity_.intVal2));
            subquery3.where(cb3.equal(subroot3.get(QuerySyntaxEntity_.intVal2), 9));

            ParameterExpression<Integer> intValue3 = cb3.parameter(Integer.class);
            cquery3.where(cb3.in(intValue3).value(subquery3));

            query = em.createQuery(cquery3);
            query.setParameter(intValue3, 5);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?))", _sql.remove(0));
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
    public void testInSubquery2_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?1 IN (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 = ?2)");
            query.setParameter(1, 5);
            query.setParameter(2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?))", _sql2.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 5 IN (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 = 9)");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?))", _sql2.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?1 IN (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 = 9)");
            query.setParameter(1, 5);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?))", _sql2.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intValue2 = cb.parameter(Integer.class);
            Subquery<Integer> subquery = cquery.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot = subquery.from(QuerySyntaxEntity.class);
            subquery.select(subroot.get(QuerySyntaxEntity_.intVal2));
            subquery.where(cb.equal(subroot.get(QuerySyntaxEntity_.intVal2), intValue2));

            ParameterExpression<Integer> intValue1 = cb.parameter(Integer.class);
            cquery.where(cb.in(intValue1).value(subquery));

            query = em.createQuery(cquery);
            query.setParameter(intValue1, 5);
            query.setParameter(intValue2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?))", _sql2.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery2 = cb2.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            Subquery<Integer> subquery2 = cquery2.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot2 = subquery2.from(QuerySyntaxEntity.class);
            subquery2.select(subroot2.get(QuerySyntaxEntity_.intVal2));
            subquery2.where(cb2.equal(subroot2.get(QuerySyntaxEntity_.intVal2), 9));

            cquery2.where(cb2.in(cb2.literal(5)).value(subquery2));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?))", _sql2.remove(0));

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery3 = cb3.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));

            Subquery<Integer> subquery3 = cquery3.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot3 = subquery3.from(QuerySyntaxEntity.class);
            subquery3.select(subroot3.get(QuerySyntaxEntity_.intVal2));
            subquery3.where(cb3.equal(subroot3.get(QuerySyntaxEntity_.intVal2), 9));

            ParameterExpression<Integer> intValue3 = cb3.parameter(Integer.class);
            cquery3.where(cb3.in(intValue3).value(subquery3));

            query = em.createQuery(cquery3);
            query.setParameter(intValue3, 5);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?))", _sql2.remove(0));
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
    public void testInSubquery2_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?1 IN (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 = ?2)");
            query.setParameter(1, 5);
            query.setParameter(2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?))", _sql3.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 5 IN (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 = 9)");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?))", _sql3.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?1 IN (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 = 9)");
            query.setParameter(1, 5);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?))", _sql3.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intValue2 = cb.parameter(Integer.class);
            Subquery<Integer> subquery = cquery.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot = subquery.from(QuerySyntaxEntity.class);
            subquery.select(subroot.get(QuerySyntaxEntity_.intVal2));
            subquery.where(cb.equal(subroot.get(QuerySyntaxEntity_.intVal2), intValue2));

            ParameterExpression<Integer> intValue1 = cb.parameter(Integer.class);
            cquery.where(cb.in(intValue1).value(subquery));

            query = em.createQuery(cquery);
            query.setParameter(intValue1, 5);
            query.setParameter(intValue2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?))", _sql3.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery2 = cb2.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            Subquery<Integer> subquery2 = cquery2.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot2 = subquery2.from(QuerySyntaxEntity.class);
            subquery2.select(subroot2.get(QuerySyntaxEntity_.intVal2));
            subquery2.where(cb2.equal(subroot2.get(QuerySyntaxEntity_.intVal2), 9));

            cquery2.where(cb2.in(cb2.literal(5)).value(subquery2));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?))", _sql3.remove(0));

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery3 = cb3.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));

            Subquery<Integer> subquery3 = cquery3.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot3 = subquery3.from(QuerySyntaxEntity.class);
            subquery3.select(subroot3.get(QuerySyntaxEntity_.intVal2));
            subquery3.where(cb3.equal(subroot3.get(QuerySyntaxEntity_.intVal2), 9));

            ParameterExpression<Integer> intValue3 = cb3.parameter(Integer.class);
            cquery3.where(cb3.in(intValue3).value(subquery3));

            query = em.createQuery(cquery3);
            query.setParameter(intValue3, 5);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?))", _sql3.remove(0));
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
    public void testInSubquery3_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?1 IN (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 IN (?2, ?3, ?4))");
            query.setParameter(1, 5);
            query.setParameter(2, 6);
            query.setParameter(3, 7);
            query.setParameter(4, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 IN (?, ?, ?)))", _sql.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 5 IN (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 IN (6, 7, 8))");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 IN (?, ?, ?)))", _sql.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?1 IN (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 IN (6, 7, ?4))");
            query.setParameter(1, 5);
            query.setParameter(4, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 IN (?, ?, ?)))", _sql.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intValue2 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intValue3 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intValue4 = cb.parameter(Integer.class);
            Subquery<Integer> subquery = cquery.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot = subquery.from(QuerySyntaxEntity.class);
            subquery.select(subroot.get(QuerySyntaxEntity_.intVal2));
            subquery.where(cb.in(subroot.get(QuerySyntaxEntity_.intVal2)).value(intValue2).value(intValue3).value(intValue4));

            ParameterExpression<Integer> intValue1 = cb.parameter(Integer.class);
            cquery.where(cb.in(intValue1).value(subquery));

            query = em.createQuery(cquery);
            query.setParameter(intValue1, 5);
            query.setParameter(intValue2, 6);
            query.setParameter(intValue3, 7);
            query.setParameter(intValue4, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 IN (?, ?, ?)))", _sql.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery2 = cb2.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            Subquery<Integer> subquery2 = cquery2.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot2 = subquery2.from(QuerySyntaxEntity.class);
            subquery2.select(subroot2.get(QuerySyntaxEntity_.intVal2));
            subquery2.where(cb2.in(subroot2.get(QuerySyntaxEntity_.intVal2)).value(6).value(7).value(8));

            cquery2.where(cb2.in(cb2.literal(5)).value(subquery2));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 IN (?, ?, ?)))", _sql.remove(0));

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery3 = cb3.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intValue6 = cb3.parameter(Integer.class);
            Subquery<Integer> subquery3 = cquery3.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot3 = subquery3.from(QuerySyntaxEntity.class);
            subquery3.select(subroot3.get(QuerySyntaxEntity_.intVal2));
            subquery3.where(cb3.equal(subroot3.get(QuerySyntaxEntity_.intVal2), 9));
            subquery3.where(cb3.in(subroot3.get(QuerySyntaxEntity_.intVal2)).value(6).value(7).value(intValue6));

            ParameterExpression<Integer> intValue5 = cb3.parameter(Integer.class);
            cquery3.where(cb3.in(intValue5).value(subquery3));

            query = em.createQuery(cquery3);
            query.setParameter(intValue5, 5);
            query.setParameter(intValue6, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 IN (?, ?, ?)))", _sql.remove(0));
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
    public void testInSubquery3_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?1 IN (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 IN (?2, ?3, ?4))");
            query.setParameter(1, 5);
            query.setParameter(2, 6);
            query.setParameter(3, 7);
            query.setParameter(4, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 IN (?, ?, ?)))", _sql2.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 5 IN (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 IN (6, 7, 8))");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 IN (?, ?, ?)))", _sql2.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?1 IN (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 IN (6, 7, ?4))");
            query.setParameter(1, 5);
            query.setParameter(4, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 IN (?, ?, ?)))", _sql2.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intValue2 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intValue3 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intValue4 = cb.parameter(Integer.class);
            Subquery<Integer> subquery = cquery.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot = subquery.from(QuerySyntaxEntity.class);
            subquery.select(subroot.get(QuerySyntaxEntity_.intVal2));
            subquery.where(cb.in(subroot.get(QuerySyntaxEntity_.intVal2)).value(intValue2).value(intValue3).value(intValue4));

            ParameterExpression<Integer> intValue1 = cb.parameter(Integer.class);
            cquery.where(cb.in(intValue1).value(subquery));

            query = em.createQuery(cquery);
            query.setParameter(intValue1, 5);
            query.setParameter(intValue2, 6);
            query.setParameter(intValue3, 7);
            query.setParameter(intValue4, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 IN (?, ?, ?)))", _sql2.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery2 = cb2.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            Subquery<Integer> subquery2 = cquery2.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot2 = subquery2.from(QuerySyntaxEntity.class);
            subquery2.select(subroot2.get(QuerySyntaxEntity_.intVal2));
            subquery2.where(cb2.in(subroot2.get(QuerySyntaxEntity_.intVal2)).value(6).value(7).value(8));

            cquery2.where(cb2.in(cb2.literal(5)).value(subquery2));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 IN (?, ?, ?)))", _sql2.remove(0));

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery3 = cb3.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intValue6 = cb3.parameter(Integer.class);
            Subquery<Integer> subquery3 = cquery3.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot3 = subquery3.from(QuerySyntaxEntity.class);
            subquery3.select(subroot3.get(QuerySyntaxEntity_.intVal2));
            subquery3.where(cb3.equal(subroot3.get(QuerySyntaxEntity_.intVal2), 9));
            subquery3.where(cb3.in(subroot3.get(QuerySyntaxEntity_.intVal2)).value(6).value(7).value(intValue6));

            ParameterExpression<Integer> intValue5 = cb3.parameter(Integer.class);
            cquery3.where(cb3.in(intValue5).value(subquery3));

            query = em.createQuery(cquery3);
            query.setParameter(intValue5, 5);
            query.setParameter(intValue6, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 IN (?, ?, ?)))", _sql2.remove(0));
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
    public void testInSubquery3_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?1 IN (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 IN (?2, ?3, ?4))");
            query.setParameter(1, 5);
            query.setParameter(2, 6);
            query.setParameter(3, 7);
            query.setParameter(4, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 IN (?, ?, ?)))", _sql3.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 5 IN (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 IN (6, 7, 8))");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 IN (?, ?, ?)))", _sql3.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?1 IN (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 IN (6, 7, ?4))");
            query.setParameter(1, 5);
            query.setParameter(4, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 IN (?, ?, ?)))", _sql3.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intValue2 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intValue3 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intValue4 = cb.parameter(Integer.class);
            Subquery<Integer> subquery = cquery.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot = subquery.from(QuerySyntaxEntity.class);
            subquery.select(subroot.get(QuerySyntaxEntity_.intVal2));
            subquery.where(cb.in(subroot.get(QuerySyntaxEntity_.intVal2)).value(intValue2).value(intValue3).value(intValue4));

            ParameterExpression<Integer> intValue1 = cb.parameter(Integer.class);
            cquery.where(cb.in(intValue1).value(subquery));

            query = em.createQuery(cquery);
            query.setParameter(intValue1, 5);
            query.setParameter(intValue2, 6);
            query.setParameter(intValue3, 7);
            query.setParameter(intValue4, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 IN (?, ?, ?)))", _sql3.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery2 = cb2.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            Subquery<Integer> subquery2 = cquery2.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot2 = subquery2.from(QuerySyntaxEntity.class);
            subquery2.select(subroot2.get(QuerySyntaxEntity_.intVal2));
            subquery2.where(cb2.in(subroot2.get(QuerySyntaxEntity_.intVal2)).value(6).value(7).value(8));

            cquery2.where(cb2.in(cb2.literal(5)).value(subquery2));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 IN (?, ?, ?)))", _sql3.remove(0));

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery3 = cb3.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intValue6 = cb3.parameter(Integer.class);
            Subquery<Integer> subquery3 = cquery3.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot3 = subquery3.from(QuerySyntaxEntity.class);
            subquery3.select(subroot3.get(QuerySyntaxEntity_.intVal2));
            subquery3.where(cb3.equal(subroot3.get(QuerySyntaxEntity_.intVal2), 9));
            subquery3.where(cb3.in(subroot3.get(QuerySyntaxEntity_.intVal2)).value(6).value(7).value(intValue6));

            ParameterExpression<Integer> intValue5 = cb3.parameter(Integer.class);
            cquery3.where(cb3.in(intValue5).value(subquery3));

            query = em.createQuery(cquery3);
            query.setParameter(intValue5, 5);
            query.setParameter(intValue6, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 IN (?, ?, ?)))", _sql3.remove(0));
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
    public void testNotInCollection1_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 NOT IN ?1");
            query.setParameter(1, java.util.Arrays.asList(1, 2, 3, 5, 8, 13, 21));
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 NOT IN (?,?,?,?,?,?,?))", _sql.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 NOT IN (1, 2, 3, 5, 8, 13, 21)");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 NOT IN (?, ?, ?, ?, ?, ?, ?))", _sql.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 NOT IN (1, ?1, 3, 5, 8, ?3, ?2)");
            query.setParameter(1, 2);
            query.setParameter(2, 21);
            query.setParameter(3, 13);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 NOT IN (?, ?, ?, ?, ?, ?, ?))", _sql.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));
            ParameterExpression<List> colValue = cb.parameter(List.class);
            cquery.where(root.get(QuerySyntaxEntity_.intVal1).in(colValue).not());

            query = em.createQuery(cquery);
            List<Integer> intCollection2 = java.util.Arrays.asList(1, 2, 3, 5, 8, 13, 21);
            query.setParameter(colValue, intCollection2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((INTVAL1 IN (?,?,?,?,?,?,?)))", _sql.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery2 = cb2.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            List<Integer> intCollection3 = java.util.Arrays.asList(1, 2, 3, 5, 8, 13, 21);
            cquery2.where(root2.get(QuerySyntaxEntity_.intVal1).in(intCollection3).not());

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((INTVAL1 IN (?, ?, ?, ?, ?, ?, ?)))", _sql.remove(0));

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery3 = cb3.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));
            In<Integer> in = cb3.in(root3.get(QuerySyntaxEntity_.intVal1)).value(1).value(3).value(5).value(8);
            ParameterExpression<List> colValue2 = cb3.parameter(List.class);
            cquery3.where(cb3.or(cb3.not(in), root3.get(QuerySyntaxEntity_.intVal1).in(colValue2).not()));

            query = em.createQuery(cquery3);
            query.setParameter(colValue2, java.util.Arrays.asList(2, 13, 21));
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (NOT ((INTVAL1 IN (?, ?, ?, ?))) OR NOT ((INTVAL1 IN (?,?,?))))", _sql.remove(0));
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
    public void testNotInCollection1_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 NOT IN ?1");
            query.setParameter(1, java.util.Arrays.asList(1, 2, 3, 5, 8, 13, 21));
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 NOT IN (?,?,?,?,?,?,?))", _sql2.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 NOT IN (1, 2, 3, 5, 8, 13, 21)");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 NOT IN (?, ?, ?, ?, ?, ?, ?))", _sql2.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 NOT IN (1, ?1, 3, 5, 8, ?3, ?2)");
            query.setParameter(1, 2);
            query.setParameter(2, 21);
            query.setParameter(3, 13);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 NOT IN (?, ?, ?, ?, ?, ?, ?))", _sql2.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));
            ParameterExpression<List> colValue = cb.parameter(List.class);
            cquery.where(root.get(QuerySyntaxEntity_.intVal1).in(colValue).not());

            query = em.createQuery(cquery);
            List<Integer> intCollection2 = java.util.Arrays.asList(1, 2, 3, 5, 8, 13, 21);
            query.setParameter(colValue, intCollection2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((INTVAL1 IN (?,?,?,?,?,?,?)))", _sql2.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery2 = cb2.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            List<Integer> intCollection3 = java.util.Arrays.asList(1, 2, 3, 5, 8, 13, 21);
            cquery2.where(root2.get(QuerySyntaxEntity_.intVal1).in(intCollection3).not());

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((INTVAL1 IN (?, ?, ?, ?, ?, ?, ?)))", _sql2.remove(0));

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery3 = cb3.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));
            In<Integer> in = cb3.in(root3.get(QuerySyntaxEntity_.intVal1)).value(1).value(3).value(5).value(8);
            ParameterExpression<List> colValue2 = cb3.parameter(List.class);
            cquery3.where(cb3.or(cb3.not(in), root3.get(QuerySyntaxEntity_.intVal1).in(colValue2).not()));

            query = em.createQuery(cquery3);
            query.setParameter(colValue2, java.util.Arrays.asList(2, 13, 21));
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (NOT ((INTVAL1 IN (?, ?, ?, ?))) OR NOT ((INTVAL1 IN (?,?,?))))", _sql2.remove(0));
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
    public void testNotInCollection1_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 NOT IN ?1");
            query.setParameter(1, java.util.Arrays.asList(1, 2, 3, 5, 8, 13, 21));
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 NOT IN (?,?,?,?,?,?,?))", _sql3.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 NOT IN (1, 2, 3, 5, 8, 13, 21)");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 NOT IN (?, ?, ?, ?, ?, ?, ?))", _sql3.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 NOT IN (1, ?1, 3, 5, 8, ?3, ?2)");
            query.setParameter(1, 2);
            query.setParameter(2, 21);
            query.setParameter(3, 13);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 NOT IN (?, ?, ?, ?, ?, ?, ?))", _sql3.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));
            ParameterExpression<List> colValue = cb.parameter(List.class);
            cquery.where(root.get(QuerySyntaxEntity_.intVal1).in(colValue).not());

            query = em.createQuery(cquery);
            List<Integer> intCollection2 = java.util.Arrays.asList(1, 2, 3, 5, 8, 13, 21);
            query.setParameter(colValue, intCollection2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((INTVAL1 IN (?,?,?,?,?,?,?)))", _sql3.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery2 = cb2.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            List<Integer> intCollection3 = java.util.Arrays.asList(1, 2, 3, 5, 8, 13, 21);
            cquery2.where(root2.get(QuerySyntaxEntity_.intVal1).in(intCollection3).not());

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((INTVAL1 IN (?, ?, ?, ?, ?, ?, ?)))", _sql3.remove(0));

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery3 = cb3.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));
            In<Integer> in = cb3.in(root3.get(QuerySyntaxEntity_.intVal1)).value(1).value(3).value(5).value(8);
            ParameterExpression<List> colValue2 = cb3.parameter(List.class);
            cquery3.where(cb3.or(cb3.not(in), root3.get(QuerySyntaxEntity_.intVal1).in(colValue2).not()));

            query = em.createQuery(cquery3);
            query.setParameter(colValue2, java.util.Arrays.asList(2, 13, 21));
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (NOT ((INTVAL1 IN (?, ?, ?, ?))) OR NOT ((INTVAL1 IN (?,?,?))))", _sql3.remove(0));
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
    public void testNotInCollection2_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 NOT IN (?1, ?2, ?3, ?4, ?5)");
            query.setParameter(1, 1);
            query.setParameter(2, 2);
            query.setParameter(3, 3);
            query.setParameter(4, 5);
            query.setParameter(5, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 NOT IN (?, ?, ?, ?, ?))", _sql.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 NOT IN (1, 2, 3, 5, 8)");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 NOT IN (?, ?, ?, ?, ?))", _sql.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 NOT IN (1, ?1, 3, 5, ?3)");
            query.setParameter(1, 2);
            query.setParameter(3, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 NOT IN (?, ?, ?, ?, ?))", _sql.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));
            ParameterExpression<Integer> intValue1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intValue2 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intValue3 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intValue4 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intValue5 = cb.parameter(Integer.class);
            cquery.where(root.get(QuerySyntaxEntity_.intVal1).in(intValue1, intValue2, intValue3, intValue4, intValue5).not());

            query = em.createQuery(cquery);
            query.setParameter(intValue1, 1);
            query.setParameter(intValue2, 2);
            query.setParameter(intValue3, 3);
            query.setParameter(intValue4, 5);
            query.setParameter(intValue5, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((INTVAL1 IN (?, ?, ?, ?, ?)))", _sql.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery2 = cb2.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.where(root2.get(QuerySyntaxEntity_.intVal1).in(1, 2, 3, 5, 8).not());

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((INTVAL1 IN (?, ?, ?, ?, ?)))", _sql.remove(0));

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery3 = cb3.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intValue6 = cb3.parameter(Integer.class);
            ParameterExpression<Integer> intValue7 = cb3.parameter(Integer.class);

            In<Integer> in = cb3.in(root3.get(QuerySyntaxEntity_.intVal1));
            in = in.value(1);
            in = in.value(intValue6);
            in = in.value(3).value(5);
            in = in.value(intValue7);
            cquery3.where(in.not());

            query = em.createQuery(cquery3);
            query.setParameter(intValue6, 2);
            query.setParameter(intValue7, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((INTVAL1 IN (?, ?, ?, ?, ?)))", _sql.remove(0));
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
    public void testNotInCollection2_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 NOT IN (?1, ?2, ?3, ?4, ?5)");
            query.setParameter(1, 1);
            query.setParameter(2, 2);
            query.setParameter(3, 3);
            query.setParameter(4, 5);
            query.setParameter(5, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 NOT IN (?, ?, ?, ?, ?))", _sql2.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 NOT IN (1, 2, 3, 5, 8)");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 NOT IN (?, ?, ?, ?, ?))", _sql2.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 NOT IN (1, ?1, 3, 5, ?3)");
            query.setParameter(1, 2);
            query.setParameter(3, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 NOT IN (?, ?, ?, ?, ?))", _sql2.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));
            ParameterExpression<Integer> intValue1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intValue2 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intValue3 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intValue4 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intValue5 = cb.parameter(Integer.class);
            cquery.where(root.get(QuerySyntaxEntity_.intVal1).in(intValue1, intValue2, intValue3, intValue4, intValue5).not());

            query = em.createQuery(cquery);
            query.setParameter(intValue1, 1);
            query.setParameter(intValue2, 2);
            query.setParameter(intValue3, 3);
            query.setParameter(intValue4, 5);
            query.setParameter(intValue5, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((INTVAL1 IN (?, ?, ?, ?, ?)))", _sql2.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery2 = cb2.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.where(root2.get(QuerySyntaxEntity_.intVal1).in(1, 2, 3, 5, 8).not());

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((INTVAL1 IN (?, ?, ?, ?, ?)))", _sql2.remove(0));

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery3 = cb3.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intValue6 = cb3.parameter(Integer.class);
            ParameterExpression<Integer> intValue7 = cb3.parameter(Integer.class);

            In<Integer> in = cb3.in(root3.get(QuerySyntaxEntity_.intVal1));
            in = in.value(1);
            in = in.value(intValue6);
            in = in.value(3).value(5);
            in = in.value(intValue7);
            cquery3.where(in.not());

            query = em.createQuery(cquery3);
            query.setParameter(intValue6, 2);
            query.setParameter(intValue7, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((INTVAL1 IN (?, ?, ?, ?, ?)))", _sql2.remove(0));
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
    public void testNotInCollection2_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 NOT IN (?1, ?2, ?3, ?4, ?5)");
            query.setParameter(1, 1);
            query.setParameter(2, 2);
            query.setParameter(3, 3);
            query.setParameter(4, 5);
            query.setParameter(5, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 NOT IN (?, ?, ?, ?, ?))", _sql3.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 NOT IN (1, 2, 3, 5, 8)");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 NOT IN (?, ?, ?, ?, ?))", _sql3.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 NOT IN (1, ?1, 3, 5, ?3)");
            query.setParameter(1, 2);
            query.setParameter(3, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 NOT IN (?, ?, ?, ?, ?))", _sql3.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));
            ParameterExpression<Integer> intValue1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intValue2 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intValue3 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intValue4 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intValue5 = cb.parameter(Integer.class);
            cquery.where(root.get(QuerySyntaxEntity_.intVal1).in(intValue1, intValue2, intValue3, intValue4, intValue5).not());

            query = em.createQuery(cquery);
            query.setParameter(intValue1, 1);
            query.setParameter(intValue2, 2);
            query.setParameter(intValue3, 3);
            query.setParameter(intValue4, 5);
            query.setParameter(intValue5, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((INTVAL1 IN (?, ?, ?, ?, ?)))", _sql3.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery2 = cb2.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.where(root2.get(QuerySyntaxEntity_.intVal1).in(1, 2, 3, 5, 8).not());

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((INTVAL1 IN (?, ?, ?, ?, ?)))", _sql3.remove(0));

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery3 = cb3.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intValue6 = cb3.parameter(Integer.class);
            ParameterExpression<Integer> intValue7 = cb3.parameter(Integer.class);

            In<Integer> in = cb3.in(root3.get(QuerySyntaxEntity_.intVal1));
            in = in.value(1);
            in = in.value(intValue6);
            in = in.value(3).value(5);
            in = in.value(intValue7);
            cquery3.where(in.not());

            query = em.createQuery(cquery3);
            query.setParameter(intValue6, 2);
            query.setParameter(intValue7, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((INTVAL1 IN (?, ?, ?, ?, ?)))", _sql3.remove(0));
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
    public void testNotInSubquery1_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?1 NOT IN (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 = ?2)");
            query.setParameter(1, 5);
            query.setParameter(2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? NOT IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?))", _sql.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 5 NOT IN (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 = 9)");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? NOT IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?))", _sql.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 5 NOT IN (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 = ?1)");
            query.setParameter(1, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? NOT IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?))", _sql.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intValue2 = cb.parameter(Integer.class);
            Subquery<Integer> subquery = cquery.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot = subquery.from(QuerySyntaxEntity.class);
            subquery.select(subroot.get(QuerySyntaxEntity_.intVal2));
            subquery.where(cb.equal(subroot.get(QuerySyntaxEntity_.intVal2), intValue2));

            ParameterExpression<Integer> intValue1 = cb.parameter(Integer.class);
            cquery.where(cb.not(intValue1.in(subquery)));

            query = em.createQuery(cquery);
            query.setParameter(intValue1, 5);
            query.setParameter(intValue2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE NOT (? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery2 = cb2.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            Subquery<Integer> subquery2 = cquery2.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot2 = subquery2.from(QuerySyntaxEntity.class);
            subquery2.select(subroot2.get(QuerySyntaxEntity_.intVal2));
            subquery2.where(cb2.equal(subroot2.get(QuerySyntaxEntity_.intVal2), 9));

            cquery2.where(cb2.not(cb2.literal(5).in(subquery2)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE NOT (? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql.remove(0));

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery3 = cb3.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intValue3 = cb3.parameter(Integer.class);
            Subquery<Integer> subquery3 = cquery3.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot3 = subquery3.from(QuerySyntaxEntity.class);
            subquery3.select(subroot3.get(QuerySyntaxEntity_.intVal2));
            subquery3.where(cb3.equal(subroot3.get(QuerySyntaxEntity_.intVal2), intValue3));

            cquery3.where(cb3.not(cb3.literal(5).in(subquery3)));

            query = em.createQuery(cquery3);
            query.setParameter(intValue3, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE NOT (? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql.remove(0));
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
    public void testNotInSubquery1_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?1 NOT IN (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 = ?2)");
            query.setParameter(1, 5);
            query.setParameter(2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? NOT IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?))", _sql2.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 5 NOT IN (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 = 9)");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? NOT IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?))", _sql2.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 5 NOT IN (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 = ?1)");
            query.setParameter(1, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? NOT IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?))", _sql2.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intValue2 = cb.parameter(Integer.class);
            Subquery<Integer> subquery = cquery.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot = subquery.from(QuerySyntaxEntity.class);
            subquery.select(subroot.get(QuerySyntaxEntity_.intVal2));
            subquery.where(cb.equal(subroot.get(QuerySyntaxEntity_.intVal2), intValue2));

            ParameterExpression<Integer> intValue1 = cb.parameter(Integer.class);
            cquery.where(cb.not(intValue1.in(subquery)));

            query = em.createQuery(cquery);
            query.setParameter(intValue1, 5);
            query.setParameter(intValue2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE NOT (? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql2.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery2 = cb2.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            Subquery<Integer> subquery2 = cquery2.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot2 = subquery2.from(QuerySyntaxEntity.class);
            subquery2.select(subroot2.get(QuerySyntaxEntity_.intVal2));
            subquery2.where(cb2.equal(subroot2.get(QuerySyntaxEntity_.intVal2), 9));

            cquery2.where(cb2.not(cb2.literal(5).in(subquery2)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE NOT (? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql2.remove(0));

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery3 = cb3.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intValue3 = cb3.parameter(Integer.class);
            Subquery<Integer> subquery3 = cquery3.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot3 = subquery3.from(QuerySyntaxEntity.class);
            subquery3.select(subroot3.get(QuerySyntaxEntity_.intVal2));
            subquery3.where(cb3.equal(subroot3.get(QuerySyntaxEntity_.intVal2), intValue3));

            cquery3.where(cb3.not(cb3.literal(5).in(subquery3)));

            query = em.createQuery(cquery3);
            query.setParameter(intValue3, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE NOT (? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql2.remove(0));
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
    public void testNotInSubquery1_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?1 NOT IN (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 = ?2)");
            query.setParameter(1, 5);
            query.setParameter(2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? NOT IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?))", _sql3.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 5 NOT IN (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 = 9)");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? NOT IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?))", _sql3.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 5 NOT IN (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 = ?1)");
            query.setParameter(1, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? NOT IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?))", _sql3.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intValue2 = cb.parameter(Integer.class);
            Subquery<Integer> subquery = cquery.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot = subquery.from(QuerySyntaxEntity.class);
            subquery.select(subroot.get(QuerySyntaxEntity_.intVal2));
            subquery.where(cb.equal(subroot.get(QuerySyntaxEntity_.intVal2), intValue2));

            ParameterExpression<Integer> intValue1 = cb.parameter(Integer.class);
            cquery.where(cb.not(intValue1.in(subquery)));

            query = em.createQuery(cquery);
            query.setParameter(intValue1, 5);
            query.setParameter(intValue2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE NOT (? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql3.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery2 = cb2.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            Subquery<Integer> subquery2 = cquery2.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot2 = subquery2.from(QuerySyntaxEntity.class);
            subquery2.select(subroot2.get(QuerySyntaxEntity_.intVal2));
            subquery2.where(cb2.equal(subroot2.get(QuerySyntaxEntity_.intVal2), 9));

            cquery2.where(cb2.not(cb2.literal(5).in(subquery2)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE NOT (? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql3.remove(0));

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery3 = cb3.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intValue3 = cb3.parameter(Integer.class);
            Subquery<Integer> subquery3 = cquery3.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot3 = subquery3.from(QuerySyntaxEntity.class);
            subquery3.select(subroot3.get(QuerySyntaxEntity_.intVal2));
            subquery3.where(cb3.equal(subroot3.get(QuerySyntaxEntity_.intVal2), intValue3));

            cquery3.where(cb3.not(cb3.literal(5).in(subquery3)));

            query = em.createQuery(cquery3);
            query.setParameter(intValue3, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE NOT (? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql3.remove(0));
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
    public void testNotInSubquery2_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?1 NOT IN (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 = ?2)");
            query.setParameter(1, 5);
            query.setParameter(2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? NOT IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?))", _sql.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 5 NOT IN (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 = 9)");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? NOT IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?))", _sql.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?1 NOT IN (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 = 9)");
            query.setParameter(1, 5);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? NOT IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?))", _sql.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intValue2 = cb.parameter(Integer.class);
            Subquery<Integer> subquery = cquery.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot = subquery.from(QuerySyntaxEntity.class);
            subquery.select(subroot.get(QuerySyntaxEntity_.intVal2));
            subquery.where(cb.equal(subroot.get(QuerySyntaxEntity_.intVal2), intValue2));

            ParameterExpression<Integer> intValue1 = cb.parameter(Integer.class);
            cquery.where(cb.not(cb.in(intValue1).value(subquery)));

            query = em.createQuery(cquery);
            query.setParameter(intValue1, 5);
            query.setParameter(intValue2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE NOT (? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery2 = cb2.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            Subquery<Integer> subquery2 = cquery2.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot2 = subquery2.from(QuerySyntaxEntity.class);
            subquery2.select(subroot2.get(QuerySyntaxEntity_.intVal2));
            subquery2.where(cb2.equal(subroot2.get(QuerySyntaxEntity_.intVal2), 9));

            cquery2.where(cb2.not(cb2.in(cb2.literal(5)).value(subquery2)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE NOT (? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql.remove(0));

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery3 = cb3.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));

            Subquery<Integer> subquery3 = cquery3.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot3 = subquery3.from(QuerySyntaxEntity.class);
            subquery3.select(subroot3.get(QuerySyntaxEntity_.intVal2));
            subquery3.where(cb3.equal(subroot3.get(QuerySyntaxEntity_.intVal2), 9));

            ParameterExpression<Integer> intValue3 = cb3.parameter(Integer.class);
            cquery3.where(cb3.not(cb3.in(intValue3).value(subquery3)));

            query = em.createQuery(cquery3);
            query.setParameter(intValue3, 5);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE NOT (? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql.remove(0));
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
    public void testNotInSubquery2_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?1 NOT IN (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 = ?2)");
            query.setParameter(1, 5);
            query.setParameter(2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? NOT IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?))", _sql2.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 5 NOT IN (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 = 9)");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? NOT IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?))", _sql2.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?1 NOT IN (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 = 9)");
            query.setParameter(1, 5);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? NOT IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?))", _sql2.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intValue2 = cb.parameter(Integer.class);
            Subquery<Integer> subquery = cquery.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot = subquery.from(QuerySyntaxEntity.class);
            subquery.select(subroot.get(QuerySyntaxEntity_.intVal2));
            subquery.where(cb.equal(subroot.get(QuerySyntaxEntity_.intVal2), intValue2));

            ParameterExpression<Integer> intValue1 = cb.parameter(Integer.class);
            cquery.where(cb.not(cb.in(intValue1).value(subquery)));

            query = em.createQuery(cquery);
            query.setParameter(intValue1, 5);
            query.setParameter(intValue2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE NOT (? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql2.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery2 = cb2.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            Subquery<Integer> subquery2 = cquery2.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot2 = subquery2.from(QuerySyntaxEntity.class);
            subquery2.select(subroot2.get(QuerySyntaxEntity_.intVal2));
            subquery2.where(cb2.equal(subroot2.get(QuerySyntaxEntity_.intVal2), 9));

            cquery2.where(cb2.not(cb2.in(cb2.literal(5)).value(subquery2)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE NOT (? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql2.remove(0));

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery3 = cb3.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));

            Subquery<Integer> subquery3 = cquery3.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot3 = subquery3.from(QuerySyntaxEntity.class);
            subquery3.select(subroot3.get(QuerySyntaxEntity_.intVal2));
            subquery3.where(cb3.equal(subroot3.get(QuerySyntaxEntity_.intVal2), 9));

            ParameterExpression<Integer> intValue3 = cb3.parameter(Integer.class);
            cquery3.where(cb3.not(cb3.in(intValue3).value(subquery3)));

            query = em.createQuery(cquery3);
            query.setParameter(intValue3, 5);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE NOT (? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql2.remove(0));
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
    public void testNotInSubquery2_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?1 NOT IN (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 = ?2)");
            query.setParameter(1, 5);
            query.setParameter(2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? NOT IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?))", _sql3.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 5 NOT IN (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 = 9)");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? NOT IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?))", _sql3.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?1 NOT IN (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 = 9)");
            query.setParameter(1, 5);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? NOT IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?))", _sql3.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intValue2 = cb.parameter(Integer.class);
            Subquery<Integer> subquery = cquery.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot = subquery.from(QuerySyntaxEntity.class);
            subquery.select(subroot.get(QuerySyntaxEntity_.intVal2));
            subquery.where(cb.equal(subroot.get(QuerySyntaxEntity_.intVal2), intValue2));

            ParameterExpression<Integer> intValue1 = cb.parameter(Integer.class);
            cquery.where(cb.not(cb.in(intValue1).value(subquery)));

            query = em.createQuery(cquery);
            query.setParameter(intValue1, 5);
            query.setParameter(intValue2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE NOT (? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql3.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery2 = cb2.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            Subquery<Integer> subquery2 = cquery2.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot2 = subquery2.from(QuerySyntaxEntity.class);
            subquery2.select(subroot2.get(QuerySyntaxEntity_.intVal2));
            subquery2.where(cb2.equal(subroot2.get(QuerySyntaxEntity_.intVal2), 9));

            cquery2.where(cb2.not(cb2.in(cb2.literal(5)).value(subquery2)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE NOT (? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql3.remove(0));

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery3 = cb3.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));

            Subquery<Integer> subquery3 = cquery3.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot3 = subquery3.from(QuerySyntaxEntity.class);
            subquery3.select(subroot3.get(QuerySyntaxEntity_.intVal2));
            subquery3.where(cb3.equal(subroot3.get(QuerySyntaxEntity_.intVal2), 9));

            ParameterExpression<Integer> intValue3 = cb3.parameter(Integer.class);
            cquery3.where(cb3.not(cb3.in(intValue3).value(subquery3)));

            query = em.createQuery(cquery3);
            query.setParameter(intValue3, 5);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE NOT (? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql3.remove(0));
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
    public void testNotInSubquery3_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?1 NOT IN (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 IN (?2, ?3, ?4))");
            query.setParameter(1, 5);
            query.setParameter(2, 6);
            query.setParameter(3, 7);
            query.setParameter(4, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? NOT IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 IN (?, ?, ?)))", _sql.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 5 NOT IN (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 IN (6, 7, 8))");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? NOT IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 IN (?, ?, ?)))", _sql.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?1 NOT IN (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 IN (6, 7, ?4))");
            query.setParameter(1, 5);
            query.setParameter(4, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? NOT IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 IN (?, ?, ?)))", _sql.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intValue2 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intValue3 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intValue4 = cb.parameter(Integer.class);
            Subquery<Integer> subquery = cquery.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot = subquery.from(QuerySyntaxEntity.class);
            subquery.select(subroot.get(QuerySyntaxEntity_.intVal2));
            subquery.where(cb.not(cb.in(subroot.get(QuerySyntaxEntity_.intVal2)).value(intValue2).value(intValue3).value(intValue4)));

            ParameterExpression<Integer> intValue1 = cb.parameter(Integer.class);
            cquery.where(cb.in(intValue1).value(subquery).not());

            query = em.createQuery(cquery);
            query.setParameter(intValue1, 5);
            query.setParameter(intValue2, 6);
            query.setParameter(intValue3, 7);
            query.setParameter(intValue4, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE NOT (? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE NOT ((t1.INTVAL2 IN (?, ?, ?)))))", _sql.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery2 = cb2.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            Subquery<Integer> subquery2 = cquery2.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot2 = subquery2.from(QuerySyntaxEntity.class);
            subquery2.select(subroot2.get(QuerySyntaxEntity_.intVal2));
            subquery2.where(cb2.not(cb2.in(subroot2.get(QuerySyntaxEntity_.intVal2)).value(6).value(7).value(8)));

            cquery2.where(cb2.in(cb2.literal(5)).value(subquery2).not());

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE NOT (? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE NOT ((t1.INTVAL2 IN (?, ?, ?)))))", _sql.remove(0));

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery3 = cb3.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intValue6 = cb3.parameter(Integer.class);
            Subquery<Integer> subquery3 = cquery3.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot3 = subquery3.from(QuerySyntaxEntity.class);
            subquery3.select(subroot3.get(QuerySyntaxEntity_.intVal2));
            subquery3.where(cb3.equal(subroot3.get(QuerySyntaxEntity_.intVal2), 9));
            subquery3.where(cb3.not(cb3.in(subroot3.get(QuerySyntaxEntity_.intVal2)).value(6).value(7).value(intValue6)));

            ParameterExpression<Integer> intValue5 = cb3.parameter(Integer.class);
            cquery3.where(cb3.in(intValue5).value(subquery3).not());

            query = em.createQuery(cquery3);
            query.setParameter(intValue5, 5);
            query.setParameter(intValue6, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE NOT (? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE NOT ((t1.INTVAL2 IN (?, ?, ?)))))", _sql.remove(0));
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
    public void testNotInSubquery3_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?1 NOT IN (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 IN (?2, ?3, ?4))");
            query.setParameter(1, 5);
            query.setParameter(2, 6);
            query.setParameter(3, 7);
            query.setParameter(4, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? NOT IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 IN (?, ?, ?)))", _sql2.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 5 NOT IN (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 IN (6, 7, 8))");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? NOT IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 IN (?, ?, ?)))", _sql2.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?1 NOT IN (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 IN (6, 7, ?4))");
            query.setParameter(1, 5);
            query.setParameter(4, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? NOT IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 IN (?, ?, ?)))", _sql2.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intValue2 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intValue3 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intValue4 = cb.parameter(Integer.class);
            Subquery<Integer> subquery = cquery.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot = subquery.from(QuerySyntaxEntity.class);
            subquery.select(subroot.get(QuerySyntaxEntity_.intVal2));
            subquery.where(cb.not(cb.in(subroot.get(QuerySyntaxEntity_.intVal2)).value(intValue2).value(intValue3).value(intValue4)));

            ParameterExpression<Integer> intValue1 = cb.parameter(Integer.class);
            cquery.where(cb.in(intValue1).value(subquery).not());

            query = em.createQuery(cquery);
            query.setParameter(intValue1, 5);
            query.setParameter(intValue2, 6);
            query.setParameter(intValue3, 7);
            query.setParameter(intValue4, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE NOT (? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE NOT ((t1.INTVAL2 IN (?, ?, ?)))))", _sql2.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery2 = cb2.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            Subquery<Integer> subquery2 = cquery2.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot2 = subquery2.from(QuerySyntaxEntity.class);
            subquery2.select(subroot2.get(QuerySyntaxEntity_.intVal2));
            subquery2.where(cb2.not(cb2.in(subroot2.get(QuerySyntaxEntity_.intVal2)).value(6).value(7).value(8)));

            cquery2.where(cb2.in(cb2.literal(5)).value(subquery2).not());

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE NOT (? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE NOT ((t1.INTVAL2 IN (?, ?, ?)))))", _sql2.remove(0));

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery3 = cb3.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intValue6 = cb3.parameter(Integer.class);
            Subquery<Integer> subquery3 = cquery3.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot3 = subquery3.from(QuerySyntaxEntity.class);
            subquery3.select(subroot3.get(QuerySyntaxEntity_.intVal2));
            subquery3.where(cb3.equal(subroot3.get(QuerySyntaxEntity_.intVal2), 9));
            subquery3.where(cb3.not(cb3.in(subroot3.get(QuerySyntaxEntity_.intVal2)).value(6).value(7).value(intValue6)));

            ParameterExpression<Integer> intValue5 = cb3.parameter(Integer.class);
            cquery3.where(cb3.in(intValue5).value(subquery3).not());

            query = em.createQuery(cquery3);
            query.setParameter(intValue5, 5);
            query.setParameter(intValue6, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE NOT (? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE NOT ((t1.INTVAL2 IN (?, ?, ?)))))", _sql2.remove(0));
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
    public void testNotInSubquery3_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?1 NOT IN (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 IN (?2, ?3, ?4))");
            query.setParameter(1, 5);
            query.setParameter(2, 6);
            query.setParameter(3, 7);
            query.setParameter(4, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? NOT IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 IN (?, ?, ?)))", _sql3.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 5 NOT IN (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 IN (6, 7, 8))");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? NOT IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 IN (?, ?, ?)))", _sql3.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?1 NOT IN (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 IN (6, 7, ?4))");
            query.setParameter(1, 5);
            query.setParameter(4, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE ? NOT IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 IN (?, ?, ?)))", _sql3.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intValue2 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intValue3 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intValue4 = cb.parameter(Integer.class);
            Subquery<Integer> subquery = cquery.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot = subquery.from(QuerySyntaxEntity.class);
            subquery.select(subroot.get(QuerySyntaxEntity_.intVal2));
            subquery.where(cb.not(cb.in(subroot.get(QuerySyntaxEntity_.intVal2)).value(intValue2).value(intValue3).value(intValue4)));

            ParameterExpression<Integer> intValue1 = cb.parameter(Integer.class);
            cquery.where(cb.in(intValue1).value(subquery).not());

            query = em.createQuery(cquery);
            query.setParameter(intValue1, 5);
            query.setParameter(intValue2, 6);
            query.setParameter(intValue3, 7);
            query.setParameter(intValue4, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE NOT (? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE NOT ((t1.INTVAL2 IN (?, ?, ?)))))", _sql3.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery2 = cb2.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            Subquery<Integer> subquery2 = cquery2.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot2 = subquery2.from(QuerySyntaxEntity.class);
            subquery2.select(subroot2.get(QuerySyntaxEntity_.intVal2));
            subquery2.where(cb2.not(cb2.in(subroot2.get(QuerySyntaxEntity_.intVal2)).value(6).value(7).value(8)));

            cquery2.where(cb2.in(cb2.literal(5)).value(subquery2).not());

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE NOT (? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE NOT ((t1.INTVAL2 IN (?, ?, ?)))))", _sql3.remove(0));

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery3 = cb3.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intValue6 = cb3.parameter(Integer.class);
            Subquery<Integer> subquery3 = cquery3.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot3 = subquery3.from(QuerySyntaxEntity.class);
            subquery3.select(subroot3.get(QuerySyntaxEntity_.intVal2));
            subquery3.where(cb3.equal(subroot3.get(QuerySyntaxEntity_.intVal2), 9));
            subquery3.where(cb3.not(cb3.in(subroot3.get(QuerySyntaxEntity_.intVal2)).value(6).value(7).value(intValue6)));

            ParameterExpression<Integer> intValue5 = cb3.parameter(Integer.class);
            cquery3.where(cb3.in(intValue5).value(subquery3).not());

            query = em.createQuery(cquery3);
            query.setParameter(intValue5, 5);
            query.setParameter(intValue6, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE NOT (? IN (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE NOT ((t1.INTVAL2 IN (?, ?, ?)))))", _sql3.remove(0));
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
    public void testBetween1_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 BETWEEN ?1 AND ?2");
            query.setParameter(1, 0);
            query.setParameter(2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 BETWEEN ? AND ?)", _sql.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 BETWEEN 0 AND 9");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 BETWEEN ? AND ?)", _sql.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 BETWEEN 0 AND ?1");
            query.setParameter(1, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 BETWEEN ? AND ?)", _sql.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            cquery.where(cb.between(root.get(QuerySyntaxEntity_.intVal1), intParam1, intParam2));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 0);
            query.setParameter(intParam2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 BETWEEN ? AND ?)", _sql.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            cquery2.where(cb2.between(root2.get(QuerySyntaxEntity_.intVal1), 0, 9));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 BETWEEN ? AND ?)", _sql.remove(0));

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intParam3 = cb3.parameter(Integer.class);
            cquery3.where(cb3.between(root3.get(QuerySyntaxEntity_.intVal1), cb3.literal(0), intParam3));

            query = em.createQuery(cquery3);
            query.setParameter(intParam3, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 BETWEEN ? AND ?)", _sql.remove(0));
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
    public void testBetween1_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 BETWEEN ?1 AND ?2");
            query.setParameter(1, 0);
            query.setParameter(2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 BETWEEN ? AND ?)", _sql2.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 BETWEEN 0 AND 9");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 BETWEEN ? AND ?)", _sql2.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 BETWEEN 0 AND ?1");
            query.setParameter(1, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 BETWEEN ? AND ?)", _sql2.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            cquery.where(cb.between(root.get(QuerySyntaxEntity_.intVal1), intParam1, intParam2));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 0);
            query.setParameter(intParam2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 BETWEEN ? AND ?)", _sql2.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            cquery2.where(cb2.between(root2.get(QuerySyntaxEntity_.intVal1), 0, 9));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 BETWEEN ? AND ?)", _sql2.remove(0));

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intParam3 = cb3.parameter(Integer.class);
            cquery3.where(cb3.between(root3.get(QuerySyntaxEntity_.intVal1), cb3.literal(0), intParam3));

            query = em.createQuery(cquery3);
            query.setParameter(intParam3, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 BETWEEN ? AND ?)", _sql2.remove(0));
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
    public void testBetween1_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 BETWEEN ?1 AND ?2");
            query.setParameter(1, 0);
            query.setParameter(2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 BETWEEN ? AND ?)", _sql3.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 BETWEEN 0 AND 9");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 BETWEEN ? AND ?)", _sql3.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 BETWEEN 0 AND ?1");
            query.setParameter(1, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 BETWEEN ? AND ?)", _sql3.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            cquery.where(cb.between(root.get(QuerySyntaxEntity_.intVal1), intParam1, intParam2));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 0);
            query.setParameter(intParam2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 BETWEEN ? AND ?)", _sql3.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            cquery2.where(cb2.between(root2.get(QuerySyntaxEntity_.intVal1), 0, 9));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 BETWEEN ? AND ?)", _sql3.remove(0));

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intParam3 = cb3.parameter(Integer.class);
            cquery3.where(cb3.between(root3.get(QuerySyntaxEntity_.intVal1), cb3.literal(0), intParam3));

            query = em.createQuery(cquery3);
            query.setParameter(intParam3, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 BETWEEN ? AND ?)", _sql3.remove(0));
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
    public void testBetween2_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?1 BETWEEN s.intVal1 AND ?2");
            query.setParameter(1, 4);
            query.setParameter(2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? BETWEEN INTVAL1 AND ?)", _sql.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 4 BETWEEN s.intVal1 AND 9");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? BETWEEN INTVAL1 AND ?)", _sql.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 4 BETWEEN s.intVal1 AND ?1");
            query.setParameter(1, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? BETWEEN INTVAL1 AND ?)", _sql.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            cquery.where(cb.between(intParam1, root.get(QuerySyntaxEntity_.intVal1), intParam2));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 4);
            query.setParameter(intParam2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? BETWEEN INTVAL1 AND ?)", _sql.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            cquery2.where(cb2.between(cb2.literal(4), root2.get(QuerySyntaxEntity_.intVal1), cb2.literal(9)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? BETWEEN INTVAL1 AND ?)", _sql.remove(0));

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intParam3 = cb3.parameter(Integer.class);
            cquery3.where(cb3.between(cb3.literal(4), root3.get(QuerySyntaxEntity_.intVal1), intParam3));

            query = em.createQuery(cquery3);
            query.setParameter(intParam3, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? BETWEEN INTVAL1 AND ?)", _sql.remove(0));
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
    public void testBetween2_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?1 BETWEEN s.intVal1 AND ?2");
            query.setParameter(1, 4);
            query.setParameter(2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? BETWEEN INTVAL1 AND ?)", _sql2.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 4 BETWEEN s.intVal1 AND 9");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? BETWEEN INTVAL1 AND ?)", _sql2.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 4 BETWEEN s.intVal1 AND ?1");
            query.setParameter(1, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? BETWEEN INTVAL1 AND ?)", _sql2.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            cquery.where(cb.between(intParam1, root.get(QuerySyntaxEntity_.intVal1), intParam2));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 4);
            query.setParameter(intParam2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? BETWEEN INTVAL1 AND ?)", _sql2.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            cquery2.where(cb2.between(cb2.literal(4), root2.get(QuerySyntaxEntity_.intVal1), cb2.literal(9)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? BETWEEN INTVAL1 AND ?)", _sql2.remove(0));

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intParam3 = cb3.parameter(Integer.class);
            cquery3.where(cb3.between(cb3.literal(4), root3.get(QuerySyntaxEntity_.intVal1), intParam3));

            query = em.createQuery(cquery3);
            query.setParameter(intParam3, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? BETWEEN INTVAL1 AND ?)", _sql2.remove(0));
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
    public void testBetween2_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?1 BETWEEN s.intVal1 AND ?2");
            query.setParameter(1, 4);
            query.setParameter(2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? BETWEEN INTVAL1 AND ?)", _sql3.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 4 BETWEEN s.intVal1 AND 9");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? BETWEEN INTVAL1 AND ?)", _sql3.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 4 BETWEEN s.intVal1 AND ?1");
            query.setParameter(1, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? BETWEEN INTVAL1 AND ?)", _sql3.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            cquery.where(cb.between(intParam1, root.get(QuerySyntaxEntity_.intVal1), intParam2));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 4);
            query.setParameter(intParam2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? BETWEEN INTVAL1 AND ?)", _sql3.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            cquery2.where(cb2.between(cb2.literal(4), root2.get(QuerySyntaxEntity_.intVal1), cb2.literal(9)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? BETWEEN INTVAL1 AND ?)", _sql3.remove(0));

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intParam3 = cb3.parameter(Integer.class);
            cquery3.where(cb3.between(cb3.literal(4), root3.get(QuerySyntaxEntity_.intVal1), intParam3));

            query = em.createQuery(cquery3);
            query.setParameter(intParam3, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? BETWEEN INTVAL1 AND ?)", _sql3.remove(0));
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
    public void testBetween3_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?1 BETWEEN ?2 AND ?3");
            query.setParameter(1, 4);
            query.setParameter(2, 2);
            query.setParameter(3, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (4 BETWEEN 2 AND 9)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? BETWEEN ? AND ?)", _sql.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 4 BETWEEN 2 AND 9");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (4 BETWEEN 2 AND 9)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? BETWEEN ? AND ?)", _sql.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 4 BETWEEN ?1 AND ?2");
            query.setParameter(1, 2);
            query.setParameter(2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (4 BETWEEN 2 AND 9)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? BETWEEN ? AND ?)", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam3 = cb.parameter(Integer.class);
            cquery.where(cb.between(intParam1, intParam2, intParam3));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 4);
            query.setParameter(intParam2, 2);
            query.setParameter(intParam3, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (4 BETWEEN 2 AND 9)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? BETWEEN ? AND ?)", _sql.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.where(cb2.between(cb2.literal(4), cb2.literal(2), cb2.literal(9)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (4 BETWEEN 2 AND 9)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? BETWEEN ? AND ?)", _sql.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intParam4 = cb3.parameter(Integer.class);
            ParameterExpression<Integer> intParam5 = cb3.parameter(Integer.class);
            cquery3.where(cb3.between(cb3.literal(4), intParam4, intParam5));

            query = em.createQuery(cquery3);
            query.setParameter(intParam4, 2);
            query.setParameter(intParam5, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (4 BETWEEN 2 AND 9)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? BETWEEN ? AND ?)", _sql.remove(0));
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
    public void testBetween3_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?1 BETWEEN ?2 AND ?3");
            query.setParameter(1, 4);
            query.setParameter(2, 2);
            query.setParameter(3, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? BETWEEN ? AND 9)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? BETWEEN ? AND ?)", _sql2.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 4 BETWEEN 2 AND 9");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? BETWEEN ? AND 9)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? BETWEEN ? AND ?)", _sql2.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 4 BETWEEN ?1 AND ?2");
            query.setParameter(1, 2);
            query.setParameter(2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? BETWEEN ? AND 9)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? BETWEEN ? AND ?)", _sql2.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam3 = cb.parameter(Integer.class);
            cquery.where(cb.between(intParam1, intParam2, intParam3));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 4);
            query.setParameter(intParam2, 2);
            query.setParameter(intParam3, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? BETWEEN ? AND 9)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? BETWEEN ? AND ?)", _sql2.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.where(cb2.between(cb2.literal(4), cb2.literal(2), cb2.literal(9)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? BETWEEN ? AND 9)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? BETWEEN ? AND ?)", _sql2.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intParam4 = cb3.parameter(Integer.class);
            ParameterExpression<Integer> intParam5 = cb3.parameter(Integer.class);
            cquery3.where(cb3.between(cb3.literal(4), intParam4, intParam5));

            query = em.createQuery(cquery3);
            query.setParameter(intParam4, 2);
            query.setParameter(intParam5, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? BETWEEN ? AND 9)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? BETWEEN ? AND ?)", _sql2.remove(0));
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
    public void testBetween3_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?1 BETWEEN ?2 AND ?3");
            query.setParameter(1, 4);
            query.setParameter(2, 2);
            query.setParameter(3, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? BETWEEN ? AND 9)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? BETWEEN ? AND ?)", _sql3.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 4 BETWEEN 2 AND 9");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? BETWEEN ? AND 9)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? BETWEEN ? AND ?)", _sql3.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 4 BETWEEN ?1 AND ?2");
            query.setParameter(1, 2);
            query.setParameter(2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? BETWEEN ? AND 9)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? BETWEEN ? AND ?)", _sql3.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam3 = cb.parameter(Integer.class);
            cquery.where(cb.between(intParam1, intParam2, intParam3));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 4);
            query.setParameter(intParam2, 2);
            query.setParameter(intParam3, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? BETWEEN ? AND 9)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? BETWEEN ? AND ?)", _sql3.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.where(cb2.between(cb2.literal(4), cb2.literal(2), cb2.literal(9)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? BETWEEN ? AND 9)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? BETWEEN ? AND ?)", _sql3.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intParam4 = cb3.parameter(Integer.class);
            ParameterExpression<Integer> intParam5 = cb3.parameter(Integer.class);
            cquery3.where(cb3.between(cb3.literal(4), intParam4, intParam5));

            query = em.createQuery(cquery3);
            query.setParameter(intParam4, 2);
            query.setParameter(intParam5, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? BETWEEN ? AND 9)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? BETWEEN ? AND ?)", _sql3.remove(0));
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
    public void testNotBetween1_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 NOT BETWEEN ?1 AND ?2");
            query.setParameter(1, 0);
            query.setParameter(2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((INTVAL1 BETWEEN ? AND ?))", _sql.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 NOT BETWEEN 0 AND 9");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((INTVAL1 BETWEEN ? AND ?))", _sql.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 NOT BETWEEN 0 AND ?1");
            query.setParameter(1, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((INTVAL1 BETWEEN ? AND ?))", _sql.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            cquery.where(cb.between(root.get(QuerySyntaxEntity_.intVal1), intParam1, intParam2).not());

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 0);
            query.setParameter(intParam2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((INTVAL1 BETWEEN ? AND ?))", _sql.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            cquery2.where(cb2.between(root2.get(QuerySyntaxEntity_.intVal1), 0, 9).not());

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((INTVAL1 BETWEEN ? AND ?))", _sql.remove(0));

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intParam3 = cb3.parameter(Integer.class);
            cquery3.where(cb3.between(root3.get(QuerySyntaxEntity_.intVal1), cb3.literal(0), intParam3).not());

            query = em.createQuery(cquery3);
            query.setParameter(intParam3, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((INTVAL1 BETWEEN ? AND ?))", _sql.remove(0));
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
    public void testNotBetween1_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 NOT BETWEEN ?1 AND ?2");
            query.setParameter(1, 0);
            query.setParameter(2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((INTVAL1 BETWEEN ? AND ?))", _sql2.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 NOT BETWEEN 0 AND 9");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((INTVAL1 BETWEEN ? AND ?))", _sql2.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 NOT BETWEEN 0 AND ?1");
            query.setParameter(1, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((INTVAL1 BETWEEN ? AND ?))", _sql2.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            cquery.where(cb.between(root.get(QuerySyntaxEntity_.intVal1), intParam1, intParam2).not());

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 0);
            query.setParameter(intParam2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((INTVAL1 BETWEEN ? AND ?))", _sql2.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            cquery2.where(cb2.between(root2.get(QuerySyntaxEntity_.intVal1), 0, 9).not());

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((INTVAL1 BETWEEN ? AND ?))", _sql2.remove(0));

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intParam3 = cb3.parameter(Integer.class);
            cquery3.where(cb3.between(root3.get(QuerySyntaxEntity_.intVal1), cb3.literal(0), intParam3).not());

            query = em.createQuery(cquery3);
            query.setParameter(intParam3, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((INTVAL1 BETWEEN ? AND ?))", _sql2.remove(0));
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
    public void testNotBetween1_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 NOT BETWEEN ?1 AND ?2");
            query.setParameter(1, 0);
            query.setParameter(2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((INTVAL1 BETWEEN ? AND ?))", _sql3.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 NOT BETWEEN 0 AND 9");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((INTVAL1 BETWEEN ? AND ?))", _sql3.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 NOT BETWEEN 0 AND ?1");
            query.setParameter(1, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((INTVAL1 BETWEEN ? AND ?))", _sql3.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            cquery.where(cb.between(root.get(QuerySyntaxEntity_.intVal1), intParam1, intParam2).not());

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 0);
            query.setParameter(intParam2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((INTVAL1 BETWEEN ? AND ?))", _sql3.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            cquery2.where(cb2.between(root2.get(QuerySyntaxEntity_.intVal1), 0, 9).not());

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((INTVAL1 BETWEEN ? AND ?))", _sql3.remove(0));

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intParam3 = cb3.parameter(Integer.class);
            cquery3.where(cb3.between(root3.get(QuerySyntaxEntity_.intVal1), cb3.literal(0), intParam3).not());

            query = em.createQuery(cquery3);
            query.setParameter(intParam3, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((INTVAL1 BETWEEN ? AND ?))", _sql3.remove(0));
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
    public void testNotBetween2_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?1 NOT BETWEEN s.intVal1 AND ?2");
            query.setParameter(1, 4);
            query.setParameter(2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((? BETWEEN INTVAL1 AND ?))", _sql.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 4 NOT BETWEEN s.intVal1 AND 9");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((? BETWEEN INTVAL1 AND ?))", _sql.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 4 NOT BETWEEN s.intVal1 AND ?1");
            query.setParameter(1, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((? BETWEEN INTVAL1 AND ?))", _sql.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            cquery.where(cb.not(cb.between(intParam1, root.get(QuerySyntaxEntity_.intVal1), intParam2)));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 4);
            query.setParameter(intParam2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((? BETWEEN INTVAL1 AND ?))", _sql.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            cquery2.where(cb2.not(cb2.between(cb2.literal(4), root2.get(QuerySyntaxEntity_.intVal1), cb2.literal(9))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((? BETWEEN INTVAL1 AND ?))", _sql.remove(0));

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intParam3 = cb3.parameter(Integer.class);
            cquery3.where(cb3.not(cb3.between(cb3.literal(4), root3.get(QuerySyntaxEntity_.intVal1), intParam3)));

            query = em.createQuery(cquery3);
            query.setParameter(intParam3, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((? BETWEEN INTVAL1 AND ?))", _sql.remove(0));
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
    public void testNotBetween2_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?1 NOT BETWEEN s.intVal1 AND ?2");
            query.setParameter(1, 4);
            query.setParameter(2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((? BETWEEN INTVAL1 AND ?))", _sql2.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 4 NOT BETWEEN s.intVal1 AND 9");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((? BETWEEN INTVAL1 AND ?))", _sql2.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 4 NOT BETWEEN s.intVal1 AND ?1");
            query.setParameter(1, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((? BETWEEN INTVAL1 AND ?))", _sql2.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            cquery.where(cb.not(cb.between(intParam1, root.get(QuerySyntaxEntity_.intVal1), intParam2)));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 4);
            query.setParameter(intParam2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((? BETWEEN INTVAL1 AND ?))", _sql2.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            cquery2.where(cb2.not(cb2.between(cb2.literal(4), root2.get(QuerySyntaxEntity_.intVal1), cb2.literal(9))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((? BETWEEN INTVAL1 AND ?))", _sql2.remove(0));

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intParam3 = cb3.parameter(Integer.class);
            cquery3.where(cb3.not(cb3.between(cb3.literal(4), root3.get(QuerySyntaxEntity_.intVal1), intParam3)));

            query = em.createQuery(cquery3);
            query.setParameter(intParam3, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((? BETWEEN INTVAL1 AND ?))", _sql2.remove(0));
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
    public void testNotBetween2_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?1 NOT BETWEEN s.intVal1 AND ?2");
            query.setParameter(1, 4);
            query.setParameter(2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((? BETWEEN INTVAL1 AND ?))", _sql3.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 4 NOT BETWEEN s.intVal1 AND 9");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((? BETWEEN INTVAL1 AND ?))", _sql3.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 4 NOT BETWEEN s.intVal1 AND ?1");
            query.setParameter(1, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((? BETWEEN INTVAL1 AND ?))", _sql3.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            cquery.where(cb.not(cb.between(intParam1, root.get(QuerySyntaxEntity_.intVal1), intParam2)));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 4);
            query.setParameter(intParam2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((? BETWEEN INTVAL1 AND ?))", _sql3.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            cquery2.where(cb2.not(cb2.between(cb2.literal(4), root2.get(QuerySyntaxEntity_.intVal1), cb2.literal(9))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((? BETWEEN INTVAL1 AND ?))", _sql3.remove(0));

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intParam3 = cb3.parameter(Integer.class);
            cquery3.where(cb3.not(cb3.between(cb3.literal(4), root3.get(QuerySyntaxEntity_.intVal1), intParam3)));

            query = em.createQuery(cquery3);
            query.setParameter(intParam3, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((? BETWEEN INTVAL1 AND ?))", _sql3.remove(0));
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
    public void testNotBetween3_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?1 NOT BETWEEN ?2 AND ?3");
            query.setParameter(1, 4);
            query.setParameter(2, 2);
            query.setParameter(3, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((4 BETWEEN 2 AND 9))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((? BETWEEN ? AND ?))", _sql.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 4 NOT BETWEEN 2 AND 9");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((4 BETWEEN 2 AND 9))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((? BETWEEN ? AND ?))", _sql.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 4 NOT BETWEEN ?1 AND ?2");
            query.setParameter(1, 2);
            query.setParameter(2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((4 BETWEEN 2 AND 9))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((? BETWEEN ? AND ?))", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam3 = cb.parameter(Integer.class);
            cquery.where(cb.between(intParam1, intParam2, intParam3).not());

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 4);
            query.setParameter(intParam2, 2);
            query.setParameter(intParam3, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((4 BETWEEN 2 AND 9))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((? BETWEEN ? AND ?))", _sql.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            cquery2.where(cb2.between(cb2.literal(4), cb2.literal(2), cb2.literal(9)).not());

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((4 BETWEEN 2 AND 9))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((? BETWEEN ? AND ?))", _sql.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intParam4 = cb3.parameter(Integer.class);
            ParameterExpression<Integer> intParam5 = cb3.parameter(Integer.class);
            cquery3.where(cb3.between(cb3.literal(4), intParam4, intParam5).not());

            query = em.createQuery(cquery3);
            query.setParameter(intParam4, 2);
            query.setParameter(intParam5, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((4 BETWEEN 2 AND 9))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((? BETWEEN ? AND ?))", _sql.remove(0));
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
    public void testNotBetween3_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?1 NOT BETWEEN ?2 AND ?3");
            query.setParameter(1, 4);
            query.setParameter(2, 2);
            query.setParameter(3, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((? BETWEEN ? AND 9))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((? BETWEEN ? AND ?))", _sql2.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 4 NOT BETWEEN 2 AND 9");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((? BETWEEN ? AND 9))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((? BETWEEN ? AND ?))", _sql2.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 4 NOT BETWEEN ?1 AND ?2");
            query.setParameter(1, 2);
            query.setParameter(2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((? BETWEEN ? AND 9))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((? BETWEEN ? AND ?))", _sql2.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam3 = cb.parameter(Integer.class);
            cquery.where(cb.between(intParam1, intParam2, intParam3).not());

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 4);
            query.setParameter(intParam2, 2);
            query.setParameter(intParam3, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((? BETWEEN ? AND 9))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((? BETWEEN ? AND ?))", _sql2.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            cquery2.where(cb2.between(cb2.literal(4), cb2.literal(2), cb2.literal(9)).not());

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((? BETWEEN ? AND 9))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((? BETWEEN ? AND ?))", _sql2.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intParam4 = cb3.parameter(Integer.class);
            ParameterExpression<Integer> intParam5 = cb3.parameter(Integer.class);
            cquery3.where(cb3.between(cb3.literal(4), intParam4, intParam5).not());

            query = em.createQuery(cquery3);
            query.setParameter(intParam4, 2);
            query.setParameter(intParam5, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((? BETWEEN ? AND 9))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((? BETWEEN ? AND ?))", _sql2.remove(0));
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
    public void testNotBetween3_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?1 NOT BETWEEN ?2 AND ?3");
            query.setParameter(1, 4);
            query.setParameter(2, 2);
            query.setParameter(3, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((? BETWEEN ? AND 9))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((? BETWEEN ? AND ?))", _sql3.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 4 NOT BETWEEN 2 AND 9");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((? BETWEEN ? AND 9))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((? BETWEEN ? AND ?))", _sql3.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 4 NOT BETWEEN ?1 AND ?2");
            query.setParameter(1, 2);
            query.setParameter(2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((? BETWEEN ? AND 9))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((? BETWEEN ? AND ?))", _sql3.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam3 = cb.parameter(Integer.class);
            cquery.where(cb.between(intParam1, intParam2, intParam3).not());

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 4);
            query.setParameter(intParam2, 2);
            query.setParameter(intParam3, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((? BETWEEN ? AND 9))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((? BETWEEN ? AND ?))", _sql3.remove(0));
            }

            // CASE test #1 with CriteriaBuilder literal values
            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            cquery2.where(cb2.between(cb2.literal(4), cb2.literal(2), cb2.literal(9)).not());

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((? BETWEEN ? AND 9))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((? BETWEEN ? AND ?))", _sql3.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intParam4 = cb3.parameter(Integer.class);
            ParameterExpression<Integer> intParam5 = cb3.parameter(Integer.class);
            cquery3.where(cb3.between(cb3.literal(4), intParam4, intParam5).not());

            query = em.createQuery(cquery3);
            query.setParameter(intParam4, 2);
            query.setParameter(intParam5, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((? BETWEEN ? AND 9))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE NOT ((? BETWEEN ? AND ?))", _sql3.remove(0));
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
    public void testIsNull1_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?1 IS NULL");
            query.setParameter(1, "HELLO");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ('HELLO' IS NULL)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? IS NULL)", _sql.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 'HELLO' IS NULL");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ('HELLO' IS NULL)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? IS NULL)", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            cquery.where(cb.isNull(strParam1));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "HELLO");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ('HELLO' IS NULL)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? IS NULL)", _sql.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            cquery2.where(cb2.isNull(cb2.literal("HELLO")));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ('HELLO' IS NULL)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? IS NULL)", _sql.remove(0));
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
    public void testIsNull1_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?1 IS NULL");
            query.setParameter(1, "HELLO");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ('HELLO' IS NULL)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? IS NULL)", _sql2.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 'HELLO' IS NULL");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ('HELLO' IS NULL)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? IS NULL)", _sql2.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            cquery.where(cb.isNull(strParam1));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "HELLO");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ('HELLO' IS NULL)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? IS NULL)", _sql2.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            cquery2.where(cb2.isNull(cb2.literal("HELLO")));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ('HELLO' IS NULL)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? IS NULL)", _sql2.remove(0));
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
    public void testIsNull1_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?1 IS NULL");
            query.setParameter(1, "HELLO");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ('HELLO' IS NULL)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? IS NULL)", _sql3.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 'HELLO' IS NULL");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ('HELLO' IS NULL)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? IS NULL)", _sql3.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            cquery.where(cb.isNull(strParam1));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "HELLO");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ('HELLO' IS NULL)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? IS NULL)", _sql3.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            cquery2.where(cb2.isNull(cb2.literal("HELLO")));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ('HELLO' IS NULL)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? IS NULL)", _sql3.remove(0));
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
    public void testIsNotNull1_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?1 IS NOT NULL");
            query.setParameter(1, "HELLO");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ('HELLO' IS NOT NULL)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? IS NOT NULL)", _sql.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 'HELLO' IS NOT NULL");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ('HELLO' IS NOT NULL)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? IS NOT NULL)", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            cquery.where(cb.isNotNull(strParam1));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "HELLO");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ('HELLO' IS NOT NULL)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? IS NOT NULL)", _sql.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            cquery2.where(cb2.isNotNull(cb2.literal("HELLO")));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ('HELLO' IS NOT NULL)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? IS NOT NULL)", _sql.remove(0));
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
    public void testIsNotNull1_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?1 IS NOT NULL");
            query.setParameter(1, "HELLO");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ('HELLO' IS NOT NULL)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? IS NOT NULL)", _sql2.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 'HELLO' IS NOT NULL");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ('HELLO' IS NOT NULL)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? IS NOT NULL)", _sql2.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            cquery.where(cb.isNotNull(strParam1));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "HELLO");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ('HELLO' IS NOT NULL)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? IS NOT NULL)", _sql2.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            cquery2.where(cb2.isNotNull(cb2.literal("HELLO")));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ('HELLO' IS NOT NULL)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? IS NOT NULL)", _sql2.remove(0));
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
    public void testIsNotNull1_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?1 IS NOT NULL");
            query.setParameter(1, "HELLO");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ('HELLO' IS NOT NULL)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? IS NOT NULL)", _sql3.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 'HELLO' IS NOT NULL");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ('HELLO' IS NOT NULL)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? IS NOT NULL)", _sql3.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            cquery.where(cb.isNotNull(strParam1));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "HELLO");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ('HELLO' IS NOT NULL)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? IS NOT NULL)", _sql3.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            cquery2.where(cb2.isNotNull(cb2.literal("HELLO")));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE ('HELLO' IS NOT NULL)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? IS NOT NULL)", _sql3.remove(0));
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
    public void testExists1_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE EXISTS (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.strVal1 = ?1)");
            query.setParameter(1, "HELLO");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE EXISTS (SELECT 1 FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = 'HELLO'))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE EXISTS (SELECT ? FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?))", _sql.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE EXISTS (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.strVal1 = 'HELLO')");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE EXISTS (SELECT 1 FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = 'HELLO'))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE EXISTS (SELECT ? FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?))", _sql.remove(0));
            }
            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<String> strValue1 = cb.parameter(String.class);
            Subquery<Integer> subquery = cquery.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot = subquery.from(QuerySyntaxEntity.class);
            subquery.select(subroot.get(QuerySyntaxEntity_.intVal2));
            subquery.where(cb.equal(subroot.get(QuerySyntaxEntity_.strVal1), strValue1));

            cquery.where(cb.exists(subquery));

            query = em.createQuery(cquery);
            query.setParameter(strValue1, "HELLO");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE EXISTS (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?))", _sql.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery2 = cb2.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            Subquery<Integer> subquery2 = cquery2.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot2 = subquery2.from(QuerySyntaxEntity.class);
            subquery2.select(subroot2.get(QuerySyntaxEntity_.intVal2));
            subquery2.where(cb2.equal(subroot2.get(QuerySyntaxEntity_.strVal1), "HELLO"));

            cquery2.where(cb2.exists(subquery2));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE EXISTS (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?))", _sql.remove(0));
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
    public void testExists1_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE EXISTS (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.strVal1 = ?1)");
            query.setParameter(1, "HELLO");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE EXISTS (SELECT 1 FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE EXISTS (SELECT ? FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?))", _sql2.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE EXISTS (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.strVal1 = 'HELLO')");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE EXISTS (SELECT 1 FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE EXISTS (SELECT ? FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?))", _sql2.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<String> strValue1 = cb.parameter(String.class);
            Subquery<Integer> subquery = cquery.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot = subquery.from(QuerySyntaxEntity.class);
            subquery.select(subroot.get(QuerySyntaxEntity_.intVal2));
            subquery.where(cb.equal(subroot.get(QuerySyntaxEntity_.strVal1), strValue1));

            cquery.where(cb.exists(subquery));

            query = em.createQuery(cquery);
            query.setParameter(strValue1, "HELLO");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE EXISTS (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?))", _sql2.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery2 = cb2.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            Subquery<Integer> subquery2 = cquery2.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot2 = subquery2.from(QuerySyntaxEntity.class);
            subquery2.select(subroot2.get(QuerySyntaxEntity_.intVal2));
            subquery2.where(cb2.equal(subroot2.get(QuerySyntaxEntity_.strVal1), "HELLO"));

            cquery2.where(cb2.exists(subquery2));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE EXISTS (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?))", _sql2.remove(0));
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
    public void testExists1_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE EXISTS (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.strVal1 = ?1)");
            query.setParameter(1, "HELLO");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE EXISTS (SELECT 1 FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE EXISTS (SELECT ? FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?))", _sql3.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE EXISTS (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.strVal1 = 'HELLO')");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE EXISTS (SELECT 1 FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE EXISTS (SELECT ? FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?))", _sql3.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<String> strValue1 = cb.parameter(String.class);
            Subquery<Integer> subquery = cquery.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot = subquery.from(QuerySyntaxEntity.class);
            subquery.select(subroot.get(QuerySyntaxEntity_.intVal2));
            subquery.where(cb.equal(subroot.get(QuerySyntaxEntity_.strVal1), strValue1));

            cquery.where(cb.exists(subquery));

            query = em.createQuery(cquery);
            query.setParameter(strValue1, "HELLO");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE EXISTS (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?))", _sql3.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery2 = cb2.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            Subquery<Integer> subquery2 = cquery2.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot2 = subquery2.from(QuerySyntaxEntity.class);
            subquery2.select(subroot2.get(QuerySyntaxEntity_.intVal2));
            subquery2.where(cb2.equal(subroot2.get(QuerySyntaxEntity_.strVal1), "HELLO"));

            cquery2.where(cb2.exists(subquery2));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE EXISTS (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?))", _sql3.remove(0));
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
    public void testExists2_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT CASE WHEN EXISTS (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.strVal1 = ?1) THEN ?2 ELSE ?3 END FROM QuerySyntaxEntity s");
            query.setParameter(1, "HELLO");
            query.setParameter(2, true);
            query.setParameter(3, false);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT CASE  WHEN EXISTS (SELECT 1 FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = 'HELLO')) THEN 1 ELSE 0 END FROM QUERYSYNTAXENTITY t0", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT CASE  WHEN EXISTS (SELECT ? FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?)) THEN ? ELSE ? END FROM QUERYSYNTAXENTITY t0", _sql.remove(0));
            }

            query = em.createQuery("SELECT CASE WHEN EXISTS (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.strVal1 = 'HELLO') THEN TRUE ELSE FALSE END FROM QuerySyntaxEntity s");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT CASE  WHEN EXISTS (SELECT 1 FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = 'HELLO')) THEN 1 ELSE 0 END FROM QUERYSYNTAXENTITY t0", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT CASE  WHEN EXISTS (SELECT ? FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?)) THEN ? ELSE ? END FROM QUERYSYNTAXENTITY t0", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object> cquery = cb.createQuery(Object.class);
            cquery.from(QuerySyntaxEntity.class);

            ParameterExpression<String> strValue1 = cb.parameter(String.class);
            Subquery<Integer> subquery = cquery.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot = subquery.from(QuerySyntaxEntity.class);
            subquery.select(subroot.get(QuerySyntaxEntity_.intVal2));
            subquery.where(cb.equal(subroot.get(QuerySyntaxEntity_.strVal1), strValue1));

            ParameterExpression<Boolean> resultParam1 = cb.parameter(Boolean.class);
            ParameterExpression<Boolean> resultParam2 = cb.parameter(Boolean.class);

            Expression<Object> selectCase = cb.selectCase()
                .when(cb.exists(subquery), resultParam1)
                .otherwise(resultParam2);

            cquery.select(selectCase);

            query = em.createQuery(cquery);
            query.setParameter(strValue1, "HELLO");
            query.setParameter(resultParam1, true);
            query.setParameter(resultParam2, false);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT CASE WHEN EXISTS (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = 'HELLO')) THEN 1 ELSE 0 END  FROM QUERYSYNTAXENTITY t0", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT CASE WHEN EXISTS (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?)) THEN ? ELSE ? END  FROM QUERYSYNTAXENTITY t0", _sql.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object> cquery2 = cb2.createQuery(Object.class);
            cquery2.from(QuerySyntaxEntity.class);

            Subquery<Integer> subquery2 = cquery2.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot2 = subquery2.from(QuerySyntaxEntity.class);
            subquery2.select(subroot2.get(QuerySyntaxEntity_.intVal2));
            subquery2.where(cb2.equal(subroot2.get(QuerySyntaxEntity_.strVal1), "HELLO"));

            Expression<Object> selectCase2 = cb2.selectCase()
                .when(cb2.exists(subquery2), true)
                .otherwise(false);

            cquery2.select(selectCase2);

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT CASE WHEN EXISTS (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = 'HELLO')) THEN 1 ELSE 0 END  FROM QUERYSYNTAXENTITY t0", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT CASE WHEN EXISTS (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?)) THEN ? ELSE ? END  FROM QUERYSYNTAXENTITY t0", _sql.remove(0));
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
    public void testExists2_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT CASE WHEN EXISTS (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.strVal1 = ?1) THEN ?2 ELSE ?3 END FROM QuerySyntaxEntity s");
            query.setParameter(1, "HELLO");
            query.setParameter(2, true);
            query.setParameter(3, false);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT CASE  WHEN EXISTS (SELECT 1 FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?)) THEN 1 ELSE 0 END FROM QUERYSYNTAXENTITY t0", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT CASE  WHEN EXISTS (SELECT ? FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?)) THEN ? ELSE ? END FROM QUERYSYNTAXENTITY t0", _sql2.remove(0));
            }

            query = em.createQuery("SELECT CASE WHEN EXISTS (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.strVal1 = 'HELLO') THEN TRUE ELSE FALSE END FROM QuerySyntaxEntity s");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT CASE  WHEN EXISTS (SELECT 1 FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?)) THEN 1 ELSE 0 END FROM QUERYSYNTAXENTITY t0", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT CASE  WHEN EXISTS (SELECT ? FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?)) THEN ? ELSE ? END FROM QUERYSYNTAXENTITY t0", _sql2.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object> cquery = cb.createQuery(Object.class);
            cquery.from(QuerySyntaxEntity.class);

            ParameterExpression<String> strValue1 = cb.parameter(String.class);
            Subquery<Integer> subquery = cquery.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot = subquery.from(QuerySyntaxEntity.class);
            subquery.select(subroot.get(QuerySyntaxEntity_.intVal2));
            subquery.where(cb.equal(subroot.get(QuerySyntaxEntity_.strVal1), strValue1));

            ParameterExpression<Boolean> resultParam1 = cb.parameter(Boolean.class);
            ParameterExpression<Boolean> resultParam2 = cb.parameter(Boolean.class);

            Expression<Object> selectCase = cb.selectCase()
                .when(cb.exists(subquery), resultParam1)
                .otherwise(resultParam2);

            cquery.select(selectCase);

            query = em.createQuery(cquery);
            query.setParameter(strValue1, "HELLO");
            query.setParameter(resultParam1, true);
            query.setParameter(resultParam2, false);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT CASE WHEN EXISTS (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?)) THEN 1 ELSE 0 END  FROM QUERYSYNTAXENTITY t0", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT CASE WHEN EXISTS (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?)) THEN ? ELSE ? END  FROM QUERYSYNTAXENTITY t0", _sql2.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object> cquery2 = cb2.createQuery(Object.class);
            cquery2.from(QuerySyntaxEntity.class);

            Subquery<Integer> subquery2 = cquery2.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot2 = subquery2.from(QuerySyntaxEntity.class);
            subquery2.select(subroot2.get(QuerySyntaxEntity_.intVal2));
            subquery2.where(cb2.equal(subroot2.get(QuerySyntaxEntity_.strVal1), "HELLO"));

            Expression<Object> selectCase2 = cb2.selectCase()
                .when(cb2.exists(subquery2), true)
                .otherwise(false);

            cquery2.select(selectCase2);

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT CASE WHEN EXISTS (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?)) THEN 1 ELSE 0 END  FROM QUERYSYNTAXENTITY t0", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT CASE WHEN EXISTS (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?)) THEN ? ELSE ? END  FROM QUERYSYNTAXENTITY t0", _sql2.remove(0));
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
    public void testExists2_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("SELECT CASE WHEN EXISTS (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.strVal1 = ?1) THEN ?2 ELSE ?3 END FROM QuerySyntaxEntity s");
            query.setParameter(1, "HELLO");
            query.setParameter(2, true);
            query.setParameter(3, false);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT CASE  WHEN EXISTS (SELECT 1 FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?)) THEN 1 ELSE 0 END FROM QUERYSYNTAXENTITY t0", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT CASE  WHEN EXISTS (SELECT ? FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?)) THEN ? ELSE ? END FROM QUERYSYNTAXENTITY t0", _sql3.remove(0));
            }

            query = em.createQuery("SELECT CASE WHEN EXISTS (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.strVal1 = 'HELLO') THEN TRUE ELSE FALSE END FROM QuerySyntaxEntity s");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT CASE  WHEN EXISTS (SELECT 1 FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?)) THEN 1 ELSE 0 END FROM QUERYSYNTAXENTITY t0", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT CASE  WHEN EXISTS (SELECT ? FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?)) THEN ? ELSE ? END FROM QUERYSYNTAXENTITY t0", _sql3.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object> cquery = cb.createQuery(Object.class);
            cquery.from(QuerySyntaxEntity.class);

            ParameterExpression<String> strValue1 = cb.parameter(String.class);
            Subquery<Integer> subquery = cquery.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot = subquery.from(QuerySyntaxEntity.class);
            subquery.select(subroot.get(QuerySyntaxEntity_.intVal2));
            subquery.where(cb.equal(subroot.get(QuerySyntaxEntity_.strVal1), strValue1));

            ParameterExpression<Boolean> resultParam1 = cb.parameter(Boolean.class);
            ParameterExpression<Boolean> resultParam2 = cb.parameter(Boolean.class);

            Expression<Object> selectCase = cb.selectCase()
                .when(cb.exists(subquery), resultParam1)
                .otherwise(resultParam2);

            cquery.select(selectCase);

            query = em.createQuery(cquery);
            query.setParameter(strValue1, "HELLO");
            query.setParameter(resultParam1, true);
            query.setParameter(resultParam2, false);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT CASE WHEN EXISTS (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?)) THEN 1 ELSE 0 END  FROM QUERYSYNTAXENTITY t0", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT CASE WHEN EXISTS (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?)) THEN ? ELSE ? END  FROM QUERYSYNTAXENTITY t0", _sql3.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object> cquery2 = cb2.createQuery(Object.class);
            cquery2.from(QuerySyntaxEntity.class);

            Subquery<Integer> subquery2 = cquery2.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot2 = subquery2.from(QuerySyntaxEntity.class);
            subquery2.select(subroot2.get(QuerySyntaxEntity_.intVal2));
            subquery2.where(cb2.equal(subroot2.get(QuerySyntaxEntity_.strVal1), "HELLO"));

            Expression<Object> selectCase2 = cb2.selectCase()
                .when(cb2.exists(subquery2), true)
                .otherwise(false);

            cquery2.select(selectCase2);

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT CASE WHEN EXISTS (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?)) THEN 1 ELSE 0 END  FROM QUERYSYNTAXENTITY t0", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT CASE WHEN EXISTS (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?)) THEN ? ELSE ? END  FROM QUERYSYNTAXENTITY t0", _sql3.remove(0));
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
    public void testNotExists1_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE NOT EXISTS (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.strVal1 = ?1)");
            query.setParameter(1, "HELLO");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE NOT EXISTS (SELECT 1 FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = 'HELLO'))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE NOT EXISTS (SELECT ? FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?))", _sql.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE NOT EXISTS (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.strVal1 = 'HELLO')");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE NOT EXISTS (SELECT 1 FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = 'HELLO'))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE NOT EXISTS (SELECT ? FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?))", _sql.remove(0));
            }

            // -----------------------
            // TODO: https://bugs.eclipse.org/bugs/show_bug.cgi?id=464833

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<String> strValue1 = cb.parameter(String.class);
            Subquery<Integer> subquery = cquery.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot = subquery.from(QuerySyntaxEntity.class);
            subquery.select(subroot.get(QuerySyntaxEntity_.intVal2));
            subquery.where(cb.equal(subroot.get(QuerySyntaxEntity_.strVal1), strValue1));

            cquery.where(cb.not(cb.exists(subquery)));

            query = em.createQuery(cquery);
            query.setParameter(strValue1, "HELLO");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE NOT (EXISTS (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?)))", _sql.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery2 = cb2.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            Subquery<Integer> subquery2 = cquery2.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot2 = subquery2.from(QuerySyntaxEntity.class);
            subquery2.select(subroot2.get(QuerySyntaxEntity_.intVal2));
            subquery2.where(cb2.equal(subroot2.get(QuerySyntaxEntity_.strVal1), "HELLO"));

            cquery2.where(cb2.not(cb2.exists(subquery2)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE NOT (EXISTS (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?)))", _sql.remove(0));
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
    public void testNotExists1_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE NOT EXISTS (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.strVal1 = ?1)");
            query.setParameter(1, "HELLO");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE NOT EXISTS (SELECT 1 FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE NOT EXISTS (SELECT ? FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?))", _sql2.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE NOT EXISTS (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.strVal1 = 'HELLO')");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE NOT EXISTS (SELECT 1 FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE NOT EXISTS (SELECT ? FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?))", _sql2.remove(0));
            }

            // -----------------------
            // TODO: https://bugs.eclipse.org/bugs/show_bug.cgi?id=464833

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<String> strValue1 = cb.parameter(String.class);
            Subquery<Integer> subquery = cquery.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot = subquery.from(QuerySyntaxEntity.class);
            subquery.select(subroot.get(QuerySyntaxEntity_.intVal2));
            subquery.where(cb.equal(subroot.get(QuerySyntaxEntity_.strVal1), strValue1));

            cquery.where(cb.not(cb.exists(subquery)));

            query = em.createQuery(cquery);
            query.setParameter(strValue1, "HELLO");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE NOT (EXISTS (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?)))", _sql2.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery2 = cb2.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            Subquery<Integer> subquery2 = cquery2.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot2 = subquery2.from(QuerySyntaxEntity.class);
            subquery2.select(subroot2.get(QuerySyntaxEntity_.intVal2));
            subquery2.where(cb2.equal(subroot2.get(QuerySyntaxEntity_.strVal1), "HELLO"));

            cquery2.where(cb2.not(cb2.exists(subquery2)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE NOT (EXISTS (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?)))", _sql2.remove(0));
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
    public void testNotExists1_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE NOT EXISTS (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.strVal1 = ?1)");
            query.setParameter(1, "HELLO");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE NOT EXISTS (SELECT 1 FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE NOT EXISTS (SELECT ? FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?))", _sql3.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE NOT EXISTS (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.strVal1 = 'HELLO')");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE NOT EXISTS (SELECT 1 FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE NOT EXISTS (SELECT ? FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?))", _sql3.remove(0));
            }

            // -----------------------
            // TODO: https://bugs.eclipse.org/bugs/show_bug.cgi?id=464833

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<String> strValue1 = cb.parameter(String.class);
            Subquery<Integer> subquery = cquery.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot = subquery.from(QuerySyntaxEntity.class);
            subquery.select(subroot.get(QuerySyntaxEntity_.intVal2));
            subquery.where(cb.equal(subroot.get(QuerySyntaxEntity_.strVal1), strValue1));

            cquery.where(cb.not(cb.exists(subquery)));

            query = em.createQuery(cquery);
            query.setParameter(strValue1, "HELLO");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE NOT (EXISTS (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?)))", _sql3.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery2 = cb2.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            Subquery<Integer> subquery2 = cquery2.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot2 = subquery2.from(QuerySyntaxEntity.class);
            subquery2.select(subroot2.get(QuerySyntaxEntity_.intVal2));
            subquery2.where(cb2.equal(subroot2.get(QuerySyntaxEntity_.strVal1), "HELLO"));

            cquery2.where(cb2.not(cb2.exists(subquery2)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE NOT (EXISTS (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?)))", _sql3.remove(0));
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
    public void testNotExists2_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT CASE WHEN NOT EXISTS (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.strVal1 = ?1) THEN ?2 ELSE ?3 END FROM QuerySyntaxEntity s");
            query.setParameter(1, "HELLO");
            query.setParameter(2, true);
            query.setParameter(3, false);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT CASE  WHEN NOT EXISTS (SELECT 1 FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = 'HELLO')) THEN 1 ELSE 0 END FROM QUERYSYNTAXENTITY t0", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT CASE  WHEN NOT EXISTS (SELECT ? FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?)) THEN ? ELSE ? END FROM QUERYSYNTAXENTITY t0", _sql.remove(0));
            }

            query = em.createQuery("SELECT CASE WHEN NOT EXISTS (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.strVal1 = 'HELLO') THEN TRUE ELSE FALSE END FROM QuerySyntaxEntity s");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT CASE  WHEN NOT EXISTS (SELECT 1 FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = 'HELLO')) THEN 1 ELSE 0 END FROM QUERYSYNTAXENTITY t0", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT CASE  WHEN NOT EXISTS (SELECT ? FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?)) THEN ? ELSE ? END FROM QUERYSYNTAXENTITY t0", _sql.remove(0));
            }

            // -----------------------
            // TODO: https://bugs.eclipse.org/bugs/show_bug.cgi?id=464833

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object> cquery = cb.createQuery(Object.class);
            cquery.from(QuerySyntaxEntity.class);

            ParameterExpression<String> strValue1 = cb.parameter(String.class);
            Subquery<Integer> subquery = cquery.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot = subquery.from(QuerySyntaxEntity.class);
            subquery.select(subroot.get(QuerySyntaxEntity_.intVal2));
            subquery.where(cb.equal(subroot.get(QuerySyntaxEntity_.strVal1), strValue1));

            ParameterExpression<Boolean> resultParam1 = cb.parameter(Boolean.class);
            ParameterExpression<Boolean> resultParam2 = cb.parameter(Boolean.class);

            Expression<Object> selectCase = cb.selectCase()
                .when(cb.not(cb.exists(subquery)), resultParam1)
                .otherwise(resultParam2);

            cquery.select(selectCase);

            query = em.createQuery(cquery);
            query.setParameter(strValue1, "HELLO");
            query.setParameter(resultParam1, true);
            query.setParameter(resultParam2, false);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT CASE WHEN NOT (EXISTS (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = 'HELLO'))) THEN 1 ELSE 0 END  FROM QUERYSYNTAXENTITY t0", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT CASE WHEN NOT (EXISTS (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?))) THEN ? ELSE ? END  FROM QUERYSYNTAXENTITY t0", _sql.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object> cquery2 = cb2.createQuery(Object.class);
            cquery2.from(QuerySyntaxEntity.class);

            Subquery<Integer> subquery2 = cquery2.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot2 = subquery2.from(QuerySyntaxEntity.class);
            subquery2.select(subroot2.get(QuerySyntaxEntity_.intVal2));
            subquery2.where(cb2.equal(subroot2.get(QuerySyntaxEntity_.strVal1), "HELLO"));

            Expression<Object> selectCase2 = cb2.selectCase()
                .when(cb2.not(cb2.exists(subquery2)), true)
                .otherwise(false);

            cquery2.select(selectCase2);

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT CASE WHEN NOT (EXISTS (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = 'HELLO'))) THEN 1 ELSE 0 END  FROM QUERYSYNTAXENTITY t0", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT CASE WHEN NOT (EXISTS (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?))) THEN ? ELSE ? END  FROM QUERYSYNTAXENTITY t0", _sql.remove(0));
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
    public void testNotExists2_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT CASE WHEN NOT EXISTS (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.strVal1 = ?1) THEN ?2 ELSE ?3 END FROM QuerySyntaxEntity s");
            query.setParameter(1, "HELLO");
            query.setParameter(2, true);
            query.setParameter(3, false);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT CASE  WHEN NOT EXISTS (SELECT 1 FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?)) THEN 1 ELSE 0 END FROM QUERYSYNTAXENTITY t0", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT CASE  WHEN NOT EXISTS (SELECT ? FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?)) THEN ? ELSE ? END FROM QUERYSYNTAXENTITY t0", _sql2.remove(0));
            }

            query = em.createQuery("SELECT CASE WHEN NOT EXISTS (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.strVal1 = 'HELLO') THEN TRUE ELSE FALSE END FROM QuerySyntaxEntity s");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT CASE  WHEN NOT EXISTS (SELECT 1 FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?)) THEN 1 ELSE 0 END FROM QUERYSYNTAXENTITY t0", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT CASE  WHEN NOT EXISTS (SELECT ? FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?)) THEN ? ELSE ? END FROM QUERYSYNTAXENTITY t0", _sql2.remove(0));
            }

            // -----------------------
            // TODO: https://bugs.eclipse.org/bugs/show_bug.cgi?id=464833

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object> cquery = cb.createQuery(Object.class);
            cquery.from(QuerySyntaxEntity.class);

            ParameterExpression<String> strValue1 = cb.parameter(String.class);
            Subquery<Integer> subquery = cquery.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot = subquery.from(QuerySyntaxEntity.class);
            subquery.select(subroot.get(QuerySyntaxEntity_.intVal2));
            subquery.where(cb.equal(subroot.get(QuerySyntaxEntity_.strVal1), strValue1));

            ParameterExpression<Boolean> resultParam1 = cb.parameter(Boolean.class);
            ParameterExpression<Boolean> resultParam2 = cb.parameter(Boolean.class);

            Expression<Object> selectCase = cb.selectCase()
                .when(cb.not(cb.exists(subquery)), resultParam1)
                .otherwise(resultParam2);

            cquery.select(selectCase);

            query = em.createQuery(cquery);
            query.setParameter(strValue1, "HELLO");
            query.setParameter(resultParam1, true);
            query.setParameter(resultParam2, false);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT CASE WHEN NOT (EXISTS (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?))) THEN 1 ELSE 0 END  FROM QUERYSYNTAXENTITY t0", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT CASE WHEN NOT (EXISTS (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?))) THEN ? ELSE ? END  FROM QUERYSYNTAXENTITY t0", _sql2.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object> cquery2 = cb2.createQuery(Object.class);
            cquery2.from(QuerySyntaxEntity.class);

            Subquery<Integer> subquery2 = cquery2.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot2 = subquery2.from(QuerySyntaxEntity.class);
            subquery2.select(subroot2.get(QuerySyntaxEntity_.intVal2));
            subquery2.where(cb2.equal(subroot2.get(QuerySyntaxEntity_.strVal1), "HELLO"));

            Expression<Object> selectCase2 = cb2.selectCase()
                .when(cb2.not(cb2.exists(subquery2)), true)
                .otherwise(false);

            cquery2.select(selectCase2);

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT CASE WHEN NOT (EXISTS (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?))) THEN 1 ELSE 0 END  FROM QUERYSYNTAXENTITY t0", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT CASE WHEN NOT (EXISTS (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?))) THEN ? ELSE ? END  FROM QUERYSYNTAXENTITY t0", _sql2.remove(0));
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
    public void testNotExists2_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("SELECT CASE WHEN NOT EXISTS (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.strVal1 = ?1) THEN ?2 ELSE ?3 END FROM QuerySyntaxEntity s");
            query.setParameter(1, "HELLO");
            query.setParameter(2, true);
            query.setParameter(3, false);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT CASE  WHEN NOT EXISTS (SELECT 1 FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?)) THEN 1 ELSE 0 END FROM QUERYSYNTAXENTITY t0", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT CASE  WHEN NOT EXISTS (SELECT ? FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?)) THEN ? ELSE ? END FROM QUERYSYNTAXENTITY t0", _sql3.remove(0));
            }

            query = em.createQuery("SELECT CASE WHEN NOT EXISTS (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.strVal1 = 'HELLO') THEN TRUE ELSE FALSE END FROM QuerySyntaxEntity s");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT CASE  WHEN NOT EXISTS (SELECT 1 FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?)) THEN 1 ELSE 0 END FROM QUERYSYNTAXENTITY t0", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT CASE  WHEN NOT EXISTS (SELECT ? FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?)) THEN ? ELSE ? END FROM QUERYSYNTAXENTITY t0", _sql3.remove(0));
            }

            // -----------------------
            // TODO: https://bugs.eclipse.org/bugs/show_bug.cgi?id=464833

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object> cquery = cb.createQuery(Object.class);
            cquery.from(QuerySyntaxEntity.class);

            ParameterExpression<String> strValue1 = cb.parameter(String.class);
            Subquery<Integer> subquery = cquery.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot = subquery.from(QuerySyntaxEntity.class);
            subquery.select(subroot.get(QuerySyntaxEntity_.intVal2));
            subquery.where(cb.equal(subroot.get(QuerySyntaxEntity_.strVal1), strValue1));

            ParameterExpression<Boolean> resultParam1 = cb.parameter(Boolean.class);
            ParameterExpression<Boolean> resultParam2 = cb.parameter(Boolean.class);

            Expression<Object> selectCase = cb.selectCase()
                .when(cb.not(cb.exists(subquery)), resultParam1)
                .otherwise(resultParam2);

            cquery.select(selectCase);

            query = em.createQuery(cquery);
            query.setParameter(strValue1, "HELLO");
            query.setParameter(resultParam1, true);
            query.setParameter(resultParam2, false);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT CASE WHEN NOT (EXISTS (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?))) THEN 1 ELSE 0 END  FROM QUERYSYNTAXENTITY t0", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT CASE WHEN NOT (EXISTS (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?))) THEN ? ELSE ? END  FROM QUERYSYNTAXENTITY t0", _sql3.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object> cquery2 = cb2.createQuery(Object.class);
            cquery2.from(QuerySyntaxEntity.class);

            Subquery<Integer> subquery2 = cquery2.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot2 = subquery2.from(QuerySyntaxEntity.class);
            subquery2.select(subroot2.get(QuerySyntaxEntity_.intVal2));
            subquery2.where(cb2.equal(subroot2.get(QuerySyntaxEntity_.strVal1), "HELLO"));

            Expression<Object> selectCase2 = cb2.selectCase()
                .when(cb2.not(cb2.exists(subquery2)), true)
                .otherwise(false);

            cquery2.select(selectCase2);

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT CASE WHEN NOT (EXISTS (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?))) THEN 1 ELSE 0 END  FROM QUERYSYNTAXENTITY t0", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT CASE WHEN NOT (EXISTS (SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.STRVAL1 = ?))) THEN ? ELSE ? END  FROM QUERYSYNTAXENTITY t0", _sql3.remove(0));
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
    public void testCase1_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = CASE s.intVal2 WHEN ?1 THEN ?2 WHEN ?3 THEN ?4 ELSE ?5 END");
            query.setParameter(1, 5);
            query.setParameter(2, 6);
            query.setParameter(3, 15);
            query.setParameter(4, 16);
            query.setParameter(5, 26);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE INTVAL2 WHEN 5 THEN 6 WHEN 15 THEN 16 ELSE 26 END)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE INTVAL2 WHEN ? THEN ? WHEN ? THEN ? ELSE ? END)", _sql.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = CASE s.intVal2 WHEN 5 THEN 6 WHEN 15 THEN 16 ELSE 26 END");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE INTVAL2 WHEN 5 THEN 6 WHEN 15 THEN 16 ELSE 26 END)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE INTVAL2 WHEN ? THEN ? WHEN ? THEN ? ELSE ? END)", _sql.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = CASE s.intVal2 WHEN ?1 THEN ?2 WHEN 15 THEN 16 ELSE 26 END");
            query.setParameter(1, 5);
            query.setParameter(2, 6);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE INTVAL2 WHEN 5 THEN 6 WHEN 15 THEN 16 ELSE 26 END)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE INTVAL2 WHEN ? THEN ? WHEN ? THEN ? ELSE ? END)", _sql.remove(0));
            }

            // -----------------------

            /*
             * JPA 3.1 Criteria API includes support setting parameters in the CASE select WHEN condition
             *     TODO: https://github.com/eclipse-ee4j/jpa-api/issues/315
             */

//            CriteriaBuilder cb = em.getCriteriaBuilder();
//            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
//            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
//            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));
//
//            ParameterExpression<Integer> checkParam1 = cb.parameter(Integer.class);
//            ParameterExpression<Integer> checkParam2 = cb.parameter(Integer.class);
//            ParameterExpression<Integer> resultParam1 = cb.parameter(Integer.class);
//            ParameterExpression<Integer> resultParam2 = cb.parameter(Integer.class);
//            ParameterExpression<Integer> resultParam3 = cb.parameter(Integer.class);
//
//            Expression<Object> selectCase = cb.selectCase(root.get(QuerySyntaxEntity_.intVal2))
//                .when(checkParam1, resultParam1)
//                .when(checkParam2, resultParam2)
//                .otherwise(resultParam3);
//
//            Predicate pred = cb.equal(root.get(QuerySyntaxEntity_.intVal1), selectCase);
//            cquery.where(pred);
//
//            query = em.createQuery(cquery);
//            query.setParameter(checkParam1, 5);
//            query.setParameter(resultParam1, 6);
//            query.setParameter(checkParam2, 15);
//            query.setParameter(resultParam2, 16);
//            query.setParameter(resultParam3, 26);
//            query.getResultList();
//            Assert.assertEquals(1, _sql.size());
//            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
//                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE INTVAL2 WHEN ? THEN 6 WHEN ? THEN 16 ELSE 26 END)", _sql.remove(0));
//            } else {
//                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE INTVAL2 WHEN ? THEN ? WHEN ? THEN ? ELSE ? END)", _sql.remove(0));
//            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            Expression<Object> selectCase2 = cb2.selectCase(root2.get(QuerySyntaxEntity_.intVal2))
                .when(5, 6)
                .when(15, 16)
                .otherwise(26);

            Predicate pred2 = cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), selectCase2);
            cquery2.where(pred2);

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE INTVAL2 WHEN 5 THEN 6 WHEN 15 THEN 16 ELSE 26 END)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE INTVAL2 WHEN ? THEN ? WHEN ? THEN ? ELSE ? END)", _sql.remove(0));
            }

            /*
             * JPA 3.1 Criteria API includes support setting parameters in the CASE select WHEN condition
             *     TODO: https://github.com/eclipse-ee4j/jpa-api/issues/315
             */

//            CriteriaBuilder cb3 = em.getCriteriaBuilder();
//            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
//            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
//            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));
//
//            ParameterExpression<Integer> checkParam3 = cb3.parameter(Integer.class);
//            ParameterExpression<Integer> resultParam4 = cb3.parameter(Integer.class);
//
//            Expression<Object> selectCase3 = cb3.selectCase(root3.get(QuerySyntaxEntity_.intVal2))
//                .when(5, resultParam4)
//                .when(checkParam3, 16)
//                .otherwise(26);
//
//            Predicate pred3 = cb3.equal(root3.get(QuerySyntaxEntity_.intVal1), selectCase3);
//            cquery3.where(pred3);
//
//            query = em.createQuery(cquery3);
//            query.setParameter(resultParam4, 6);
//            query.setParameter(checkParam3, 15);
//            query.getResultList();
//            Assert.assertEquals(1, _sql.size());
//            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
//                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE INTVAL2 WHEN ? THEN 6 WHEN 15 THEN 16 ELSE 26 END)", _sql.remove(0));
//            } else {
//                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE INTVAL2 WHEN ? THEN ? WHEN ? THEN ? ELSE ? END)", _sql.remove(0));
//            }
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
    public void testCase1_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = CASE s.intVal2 WHEN ?1 THEN ?2 WHEN ?3 THEN ?4 ELSE ?5 END");
            query.setParameter(1, 5);
            query.setParameter(2, 6);
            query.setParameter(3, 15);
            query.setParameter(4, 16);
            query.setParameter(5, 26);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE INTVAL2 WHEN ? THEN 6 WHEN ? THEN 16 ELSE 26 END)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE INTVAL2 WHEN ? THEN ? WHEN ? THEN ? ELSE ? END)", _sql2.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = CASE s.intVal2 WHEN 5 THEN 6 WHEN 15 THEN 16 ELSE 26 END");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE INTVAL2 WHEN ? THEN 6 WHEN ? THEN 16 ELSE 26 END)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE INTVAL2 WHEN ? THEN ? WHEN ? THEN ? ELSE ? END)", _sql2.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = CASE s.intVal2 WHEN ?1 THEN ?2 WHEN 15 THEN 16 ELSE 26 END");
            query.setParameter(1, 5);
            query.setParameter(2, 6);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE INTVAL2 WHEN ? THEN 6 WHEN ? THEN 16 ELSE 26 END)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE INTVAL2 WHEN ? THEN ? WHEN ? THEN ? ELSE ? END)", _sql2.remove(0));
            }

            // -----------------------

            /*
             * JPA 3.1 Criteria API includes support setting parameters in the CASE select WHEN condition
             *     TODO: https://github.com/eclipse-ee4j/jpa-api/issues/315
             */

//            CriteriaBuilder cb = em.getCriteriaBuilder();
//            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
//            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
//            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));
//
//            ParameterExpression<Integer> checkParam1 = cb.parameter(Integer.class);
//            ParameterExpression<Integer> checkParam2 = cb.parameter(Integer.class);
//            ParameterExpression<Integer> resultParam1 = cb.parameter(Integer.class);
//            ParameterExpression<Integer> resultParam2 = cb.parameter(Integer.class);
//            ParameterExpression<Integer> resultParam3 = cb.parameter(Integer.class);
//
//            Expression<Object> selectCase = cb.selectCase(root.get(QuerySyntaxEntity_.intVal2))
//                .when(checkParam1, resultParam1)
//                .when(checkParam2, resultParam2)
//                .otherwise(resultParam3);
//
//            Predicate pred = cb.equal(root.get(QuerySyntaxEntity_.intVal1), selectCase);
//            cquery.where(pred);
//
//            query = em.createQuery(cquery);
//            query.setParameter(checkParam1, 5);
//            query.setParameter(resultParam1, 6);
//            query.setParameter(checkParam2, 15);
//            query.setParameter(resultParam2, 16);
//            query.setParameter(resultParam3, 26);
//            query.getResultList();
//            Assert.assertEquals(1, _sql2.size());
//            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
//                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE INTVAL2 WHEN ? THEN 6 WHEN ? THEN 16 ELSE 26 END)", _sql2.remove(0));
//            } else {
//                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE INTVAL2 WHEN ? THEN ? WHEN ? THEN ? ELSE ? END)", _sql2.remove(0));
//            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            Expression<Object> selectCase2 = cb2.selectCase(root2.get(QuerySyntaxEntity_.intVal2))
                .when(5, 6)
                .when(15, 16)
                .otherwise(26);

            Predicate pred2 = cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), selectCase2);
            cquery2.where(pred2);

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE INTVAL2 WHEN ? THEN 6 WHEN ? THEN 16 ELSE 26 END)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE INTVAL2 WHEN ? THEN ? WHEN ? THEN ? ELSE ? END)", _sql2.remove(0));
            }

            /*
             * JPA 3.1 Criteria API includes support setting parameters in the CASE select WHEN condition
             *     TODO: https://github.com/eclipse-ee4j/jpa-api/issues/315
             */

//            CriteriaBuilder cb3 = em.getCriteriaBuilder();
//            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
//            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
//            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));
//
//            ParameterExpression<Integer> checkParam3 = cb3.parameter(Integer.class);
//            ParameterExpression<Integer> resultParam4 = cb3.parameter(Integer.class);
//
//            Expression<Object> selectCase3 = cb3.selectCase(root3.get(QuerySyntaxEntity_.intVal2))
//                .when(5, resultParam4)
//                .when(checkParam3, 16)
//                .otherwise(26);
//
//            Predicate pred3 = cb3.equal(root3.get(QuerySyntaxEntity_.intVal1), selectCase3);
//            cquery3.where(pred3);
//
//            query = em.createQuery(cquery3);
//            query.setParameter(resultParam4, 6);
//            query.setParameter(checkParam3, 15);
//            query.getResultList();
//            Assert.assertEquals(1, _sql2.size());
//            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE INTVAL2 WHEN ? THEN ? WHEN 15 THEN 16 ELSE 26 END)", _sql2.remove(0));
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
    public void testCase1_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = CASE s.intVal2 WHEN ?1 THEN ?2 WHEN ?3 THEN ?4 ELSE ?5 END");
            query.setParameter(1, 5);
            query.setParameter(2, 6);
            query.setParameter(3, 15);
            query.setParameter(4, 16);
            query.setParameter(5, 26);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE INTVAL2 WHEN ? THEN 6 WHEN ? THEN 16 ELSE 26 END)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE INTVAL2 WHEN ? THEN ? WHEN ? THEN ? ELSE ? END)", _sql3.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = CASE s.intVal2 WHEN 5 THEN 6 WHEN 15 THEN 16 ELSE 26 END");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE INTVAL2 WHEN ? THEN 6 WHEN ? THEN 16 ELSE 26 END)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE INTVAL2 WHEN ? THEN ? WHEN ? THEN ? ELSE ? END)", _sql3.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = CASE s.intVal2 WHEN ?1 THEN ?2 WHEN 15 THEN 16 ELSE 26 END");
            query.setParameter(1, 5);
            query.setParameter(2, 6);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE INTVAL2 WHEN ? THEN 6 WHEN ? THEN 16 ELSE 26 END)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE INTVAL2 WHEN ? THEN ? WHEN ? THEN ? ELSE ? END)", _sql3.remove(0));
            }

            // -----------------------

            /*
             * JPA 3.1 Criteria API includes support setting parameters in the CASE select WHEN condition
             *     TODO: https://github.com/eclipse-ee4j/jpa-api/issues/315
             */

//            CriteriaBuilder cb = em.getCriteriaBuilder();
//            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
//            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
//            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));
//
//            ParameterExpression<Integer> checkParam1 = cb.parameter(Integer.class);
//            ParameterExpression<Integer> checkParam2 = cb.parameter(Integer.class);
//            ParameterExpression<Integer> resultParam1 = cb.parameter(Integer.class);
//            ParameterExpression<Integer> resultParam2 = cb.parameter(Integer.class);
//            ParameterExpression<Integer> resultParam3 = cb.parameter(Integer.class);
//
//            Expression<Object> selectCase = cb.selectCase(root.get(QuerySyntaxEntity_.intVal2))
//                .when(checkParam1, resultParam1)
//                .when(checkParam2, resultParam2)
//                .otherwise(resultParam3);
//
//            Predicate pred = cb.equal(root.get(QuerySyntaxEntity_.intVal1), selectCase);
//            cquery.where(pred);
//
//            query = em.createQuery(cquery);
//            query.setParameter(checkParam1, 5);
//            query.setParameter(resultParam1, 6);
//            query.setParameter(checkParam2, 15);
//            query.setParameter(resultParam2, 16);
//            query.setParameter(resultParam3, 26);
//            query.getResultList();
//            Assert.assertEquals(1, _sql3.size());
//            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
//                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE INTVAL2 WHEN ? THEN 6 WHEN ? THEN 16 ELSE 26 END)", _sql3.remove(0));
//            } else {
//                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE INTVAL2 WHEN ? THEN ? WHEN ? THEN ? ELSE ? END)", _sql3.remove(0));
//            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            Expression<Object> selectCase2 = cb2.selectCase(root2.get(QuerySyntaxEntity_.intVal2))
                .when(5, 6)
                .when(15, 16)
                .otherwise(26);

            Predicate pred2 = cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), selectCase2);
            cquery2.where(pred2);

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE INTVAL2 WHEN ? THEN 6 WHEN ? THEN 16 ELSE 26 END)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE INTVAL2 WHEN ? THEN ? WHEN ? THEN ? ELSE ? END)", _sql3.remove(0));
            }

            /*
             * JPA 3.1 Criteria API includes support setting parameters in the CASE select WHEN condition
             *     TODO: https://github.com/eclipse-ee4j/jpa-api/issues/315
             */

//            CriteriaBuilder cb3 = em.getCriteriaBuilder();
//            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
//            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
//            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));
//
//            ParameterExpression<Integer> checkParam3 = cb3.parameter(Integer.class);
//            ParameterExpression<Integer> resultParam4 = cb3.parameter(Integer.class);
//
//            Expression<Object> selectCase3 = cb3.selectCase(root3.get(QuerySyntaxEntity_.intVal2))
//                .when(5, resultParam4)
//                .when(checkParam3, 16)
//                .otherwise(26);
//
//            Predicate pred3 = cb3.equal(root3.get(QuerySyntaxEntity_.intVal1), selectCase3);
//            cquery3.where(pred3);
//
//            query = em.createQuery(cquery3);
//            query.setParameter(resultParam4, 6);
//            query.setParameter(checkParam3, 15);
//            query.getResultList();
//            Assert.assertEquals(1, _sql3.size());
//            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
//                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE INTVAL2 WHEN ? THEN 6 WHEN ? THEN 16 ELSE 26 END)", _sql3.remove(0));
//            } else {
//                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE INTVAL2 WHEN ? THEN ? WHEN ? THEN ? ELSE ? END)", _sql3.remove(0));
//            }
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
    public void testCase2_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = CASE WHEN s.intVal2 = ?1 THEN ?2 WHEN s.intVal2 = ?3 THEN ?4 ELSE ?5 END");
            query.setParameter(1, 5);
            query.setParameter(2, 6);
            query.setParameter(3, 15);
            query.setParameter(4, 16);
            query.setParameter(5, 26);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE  WHEN (INTVAL2 = 5) THEN 6 WHEN (INTVAL2 = 15) THEN 16 ELSE 26 END)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE  WHEN (INTVAL2 = ?) THEN ? WHEN (INTVAL2 = ?) THEN ? ELSE ? END)", _sql.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = CASE WHEN s.intVal2 = 5 THEN 6 WHEN s.intVal2 = 15 THEN 16 ELSE 26 END");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE  WHEN (INTVAL2 = 5) THEN 6 WHEN (INTVAL2 = 15) THEN 16 ELSE 26 END)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE  WHEN (INTVAL2 = ?) THEN ? WHEN (INTVAL2 = ?) THEN ? ELSE ? END)", _sql.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = CASE WHEN s.intVal2 = ?1 THEN ?2 WHEN s.intVal2 = 15 THEN 16 ELSE 26 END");
            query.setParameter(1, 5);
            query.setParameter(2, 6);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE  WHEN (INTVAL2 = 5) THEN 6 WHEN (INTVAL2 = 15) THEN 16 ELSE 26 END)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE  WHEN (INTVAL2 = ?) THEN ? WHEN (INTVAL2 = ?) THEN ? ELSE ? END)", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> checkParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> checkParam2 = cb.parameter(Integer.class);
            ParameterExpression<Integer> resultParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> resultParam2 = cb.parameter(Integer.class);
            ParameterExpression<Integer> resultParam3 = cb.parameter(Integer.class);

            Expression<Object> selectCase = cb.selectCase()
                .when(cb.equal(root.get(QuerySyntaxEntity_.intVal2), checkParam1), resultParam1)
                .when(cb.equal(root.get(QuerySyntaxEntity_.intVal2), checkParam2), resultParam2)
                .otherwise(resultParam3);

            Predicate pred = cb.equal(root.get(QuerySyntaxEntity_.intVal1), selectCase);
            cquery.where(pred);

            query = em.createQuery(cquery);
            query.setParameter(checkParam1, 5);
            query.setParameter(resultParam1, 6);
            query.setParameter(checkParam2, 15);
            query.setParameter(resultParam2, 16);
            query.setParameter(resultParam3, 26);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE WHEN (INTVAL2 = 5) THEN 6 WHEN (INTVAL2 = 15) THEN 16 ELSE 26 END )", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE WHEN (INTVAL2 = ?) THEN ? WHEN (INTVAL2 = ?) THEN ? ELSE ? END )", _sql.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            Expression<Object> selectCase2 = cb2.selectCase()
                    .when(cb2.equal(root2.get(QuerySyntaxEntity_.intVal2), 5), 6)
                    .when(cb2.equal(root2.get(QuerySyntaxEntity_.intVal2), 15), 16)
                    .otherwise(26);

            Predicate pred2 = cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), selectCase2);
            cquery2.where(pred2);

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE WHEN (INTVAL2 = 5) THEN 6 WHEN (INTVAL2 = 15) THEN 16 ELSE 26 END )", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE WHEN (INTVAL2 = ?) THEN ? WHEN (INTVAL2 = ?) THEN ? ELSE ? END )", _sql.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> checkParam3 = cb3.parameter(Integer.class);
            ParameterExpression<Integer> resultParam4 = cb3.parameter(Integer.class);

            Expression<Object> selectCase3 = cb3.selectCase()
                .when(cb3.equal(root3.get(QuerySyntaxEntity_.intVal2), checkParam3), resultParam4)
                .when(cb3.equal(root3.get(QuerySyntaxEntity_.intVal2), 15), 16)
                .otherwise(26);

            Predicate pred3 = cb3.equal(root3.get(QuerySyntaxEntity_.intVal1), selectCase3);
            cquery3.where(pred3);

            query = em.createQuery(cquery3);
            query.setParameter(checkParam3, 5);
            query.setParameter(resultParam4, 6);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE WHEN (INTVAL2 = 5) THEN 6 WHEN (INTVAL2 = 15) THEN 16 ELSE 26 END )", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE WHEN (INTVAL2 = ?) THEN ? WHEN (INTVAL2 = ?) THEN ? ELSE ? END )", _sql.remove(0));
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
    public void testCase2_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = CASE WHEN s.intVal2 = ?1 THEN ?2 WHEN s.intVal2 = ?3 THEN ?4 ELSE ?5 END");
            query.setParameter(1, 5);
            query.setParameter(2, 6);
            query.setParameter(3, 15);
            query.setParameter(4, 16);
            query.setParameter(5, 26);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE  WHEN (INTVAL2 = ?) THEN 6 WHEN (INTVAL2 = ?) THEN 16 ELSE 26 END)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE  WHEN (INTVAL2 = ?) THEN ? WHEN (INTVAL2 = ?) THEN ? ELSE ? END)", _sql2.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = CASE WHEN s.intVal2 = 5 THEN 6 WHEN s.intVal2 = 15 THEN 16 ELSE 26 END");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE  WHEN (INTVAL2 = ?) THEN 6 WHEN (INTVAL2 = ?) THEN 16 ELSE 26 END)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE  WHEN (INTVAL2 = ?) THEN ? WHEN (INTVAL2 = ?) THEN ? ELSE ? END)", _sql2.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = CASE WHEN s.intVal2 = ?1 THEN ?2 WHEN s.intVal2 = 15 THEN 16 ELSE 26 END");
            query.setParameter(1, 5);
            query.setParameter(2, 6);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE  WHEN (INTVAL2 = ?) THEN 6 WHEN (INTVAL2 = ?) THEN 16 ELSE 26 END)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE  WHEN (INTVAL2 = ?) THEN ? WHEN (INTVAL2 = ?) THEN ? ELSE ? END)", _sql2.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> checkParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> checkParam2 = cb.parameter(Integer.class);
            ParameterExpression<Integer> resultParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> resultParam2 = cb.parameter(Integer.class);
            ParameterExpression<Integer> resultParam3 = cb.parameter(Integer.class);

            Expression<Object> selectCase = cb.selectCase()
                .when(cb.equal(root.get(QuerySyntaxEntity_.intVal2), checkParam1), resultParam1)
                .when(cb.equal(root.get(QuerySyntaxEntity_.intVal2), checkParam2), resultParam2)
                .otherwise(resultParam3);

            Predicate pred = cb.equal(root.get(QuerySyntaxEntity_.intVal1), selectCase);
            cquery.where(pred);

            query = em.createQuery(cquery);
            query.setParameter(checkParam1, 5);
            query.setParameter(resultParam1, 6);
            query.setParameter(checkParam2, 15);
            query.setParameter(resultParam2, 16);
            query.setParameter(resultParam3, 26);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE WHEN (INTVAL2 = ?) THEN 6 WHEN (INTVAL2 = ?) THEN 16 ELSE 26 END )", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE WHEN (INTVAL2 = ?) THEN ? WHEN (INTVAL2 = ?) THEN ? ELSE ? END )", _sql2.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            Expression<Object> selectCase2 = cb2.selectCase()
                    .when(cb2.equal(root2.get(QuerySyntaxEntity_.intVal2), 5), 6)
                    .when(cb2.equal(root2.get(QuerySyntaxEntity_.intVal2), 15), 16)
                    .otherwise(26);

            Predicate pred2 = cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), selectCase2);
            cquery2.where(pred2);

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE WHEN (INTVAL2 = ?) THEN 6 WHEN (INTVAL2 = ?) THEN 16 ELSE 26 END )", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE WHEN (INTVAL2 = ?) THEN ? WHEN (INTVAL2 = ?) THEN ? ELSE ? END )", _sql2.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> checkParam3 = cb3.parameter(Integer.class);
            ParameterExpression<Integer> resultParam4 = cb3.parameter(Integer.class);

            Expression<Object> selectCase3 = cb3.selectCase()
                .when(cb3.equal(root3.get(QuerySyntaxEntity_.intVal2), checkParam3), resultParam4)
                .when(cb3.equal(root3.get(QuerySyntaxEntity_.intVal2), 15), 16)
                .otherwise(26);

            Predicate pred3 = cb3.equal(root3.get(QuerySyntaxEntity_.intVal1), selectCase3);
            cquery3.where(pred3);

            query = em.createQuery(cquery3);
            query.setParameter(checkParam3, 5);
            query.setParameter(resultParam4, 6);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE WHEN (INTVAL2 = ?) THEN 6 WHEN (INTVAL2 = ?) THEN 16 ELSE 26 END )", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE WHEN (INTVAL2 = ?) THEN ? WHEN (INTVAL2 = ?) THEN ? ELSE ? END )", _sql2.remove(0));
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
    public void testCase2_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = CASE WHEN s.intVal2 = ?1 THEN ?2 WHEN s.intVal2 = ?3 THEN ?4 ELSE ?5 END");
            query.setParameter(1, 5);
            query.setParameter(2, 6);
            query.setParameter(3, 15);
            query.setParameter(4, 16);
            query.setParameter(5, 26);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE  WHEN (INTVAL2 = ?) THEN 6 WHEN (INTVAL2 = ?) THEN 16 ELSE 26 END)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE  WHEN (INTVAL2 = ?) THEN ? WHEN (INTVAL2 = ?) THEN ? ELSE ? END)", _sql3.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = CASE WHEN s.intVal2 = 5 THEN 6 WHEN s.intVal2 = 15 THEN 16 ELSE 26 END");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE  WHEN (INTVAL2 = ?) THEN 6 WHEN (INTVAL2 = ?) THEN 16 ELSE 26 END)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE  WHEN (INTVAL2 = ?) THEN ? WHEN (INTVAL2 = ?) THEN ? ELSE ? END)", _sql3.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = CASE WHEN s.intVal2 = ?1 THEN ?2 WHEN s.intVal2 = 15 THEN 16 ELSE 26 END");
            query.setParameter(1, 5);
            query.setParameter(2, 6);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE  WHEN (INTVAL2 = ?) THEN 6 WHEN (INTVAL2 = ?) THEN 16 ELSE 26 END)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE  WHEN (INTVAL2 = ?) THEN ? WHEN (INTVAL2 = ?) THEN ? ELSE ? END)", _sql3.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> checkParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> checkParam2 = cb.parameter(Integer.class);
            ParameterExpression<Integer> resultParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> resultParam2 = cb.parameter(Integer.class);
            ParameterExpression<Integer> resultParam3 = cb.parameter(Integer.class);

            Expression<Object> selectCase = cb.selectCase()
                .when(cb.equal(root.get(QuerySyntaxEntity_.intVal2), checkParam1), resultParam1)
                .when(cb.equal(root.get(QuerySyntaxEntity_.intVal2), checkParam2), resultParam2)
                .otherwise(resultParam3);

            Predicate pred = cb.equal(root.get(QuerySyntaxEntity_.intVal1), selectCase);
            cquery.where(pred);

            query = em.createQuery(cquery);
            query.setParameter(checkParam1, 5);
            query.setParameter(resultParam1, 6);
            query.setParameter(checkParam2, 15);
            query.setParameter(resultParam2, 16);
            query.setParameter(resultParam3, 26);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE WHEN (INTVAL2 = ?) THEN 6 WHEN (INTVAL2 = ?) THEN 16 ELSE 26 END )", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE WHEN (INTVAL2 = ?) THEN ? WHEN (INTVAL2 = ?) THEN ? ELSE ? END )", _sql3.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            Expression<Object> selectCase2 = cb2.selectCase()
                    .when(cb2.equal(root2.get(QuerySyntaxEntity_.intVal2), 5), 6)
                    .when(cb2.equal(root2.get(QuerySyntaxEntity_.intVal2), 15), 16)
                    .otherwise(26);

            Predicate pred2 = cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), selectCase2);
            cquery2.where(pred2);

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE WHEN (INTVAL2 = ?) THEN 6 WHEN (INTVAL2 = ?) THEN 16 ELSE 26 END )", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE WHEN (INTVAL2 = ?) THEN ? WHEN (INTVAL2 = ?) THEN ? ELSE ? END )", _sql3.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> checkParam3 = cb3.parameter(Integer.class);
            ParameterExpression<Integer> resultParam4 = cb3.parameter(Integer.class);

            Expression<Object> selectCase3 = cb3.selectCase()
                .when(cb3.equal(root3.get(QuerySyntaxEntity_.intVal2), checkParam3), resultParam4)
                .when(cb3.equal(root3.get(QuerySyntaxEntity_.intVal2), 15), 16)
                .otherwise(26);

            Predicate pred3 = cb3.equal(root3.get(QuerySyntaxEntity_.intVal1), selectCase3);
            cquery3.where(pred3);

            query = em.createQuery(cquery3);
            query.setParameter(checkParam3, 5);
            query.setParameter(resultParam4, 6);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE WHEN (INTVAL2 = ?) THEN 6 WHEN (INTVAL2 = ?) THEN 16 ELSE 26 END )", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = CASE WHEN (INTVAL2 = ?) THEN ? WHEN (INTVAL2 = ?) THEN ? ELSE ? END )", _sql3.remove(0));
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
    public void testCase3_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT CASE s.intVal2 WHEN ?1 THEN ?2 WHEN ?3 THEN ?4 ELSE ?5 END FROM QuerySyntaxEntity s WHERE s.intVal1 = ?6");
            query.setParameter(1, 5);
            query.setParameter(2, 6);
            query.setParameter(3, 15);
            query.setParameter(4, 16);
            query.setParameter(5, 26);
            query.setParameter(6, 99);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT CASE INTVAL2 WHEN 5 THEN 6 WHEN 15 THEN 16 ELSE 26 END FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 99)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT CASE INTVAL2 WHEN ? THEN ? WHEN ? THEN ? ELSE ? END FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));
            }

            query = em.createQuery("SELECT CASE s.intVal2 WHEN 5 THEN 6 WHEN 15 THEN 16 ELSE 26 END FROM QuerySyntaxEntity s WHERE s.intVal1 = 99");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT CASE INTVAL2 WHEN 5 THEN 6 WHEN 15 THEN 16 ELSE 26 END FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 99)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT CASE INTVAL2 WHEN ? THEN ? WHEN ? THEN ? ELSE ? END FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));
            }

            query = em.createQuery("SELECT CASE s.intVal2 WHEN ?1 THEN ?2 WHEN 15 THEN 16 ELSE 26 END FROM QuerySyntaxEntity s WHERE s.intVal1 = ?3");
            query.setParameter(1, 5);
            query.setParameter(2, 6);
            query.setParameter(3, 99);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT CASE INTVAL2 WHEN 5 THEN 6 WHEN 15 THEN 16 ELSE 26 END FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 99)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT CASE INTVAL2 WHEN ? THEN ? WHEN ? THEN ? ELSE ? END FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));
            }

            // -----------------------

            /*
             * JPA 3.1 Criteria API includes support setting parameters in the CASE select WHEN condition
             *     TODO: https://github.com/eclipse-ee4j/jpa-api/issues/315
             */

//            CriteriaBuilder cb = em.getCriteriaBuilder();
//            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
//            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
//
//            ParameterExpression<Integer> checkParam1 = cb.parameter(Integer.class);
//            ParameterExpression<Integer> checkParam2 = cb.parameter(Integer.class);
//            ParameterExpression<Integer> resultParam1 = cb.parameter(Integer.class);
//            ParameterExpression<Integer> resultParam2 = cb.parameter(Integer.class);
//            ParameterExpression<Integer> resultParam3 = cb.parameter(Integer.class);
//
//            Expression<Object> selectCase = cb.selectCase(root.get(QuerySyntaxEntity_.intVal2))
//                .when(checkParam1, resultParam1)
//                .when(checkParam2, resultParam2)
//                .otherwise(resultParam3);
//            cquery.multiselect(selectCase);
//
//            ParameterExpression<Integer> checkParam3 = cb.parameter(Integer.class);
//            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.intVal1), checkParam3));
//
//            query = em.createQuery(cquery);
//            query.setParameter(checkParam1, 5);
//            query.setParameter(resultParam1, 6);
//            query.setParameter(checkParam2, 15);
//            query.setParameter(resultParam2, 16);
//            query.setParameter(resultParam3, 26);
//            query.setParameter(checkParam3, 99);
//            query.getResultList();
//            Assert.assertEquals(1, _sql.size());
//            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
//                Assert.assertEquals("SELECT CASE INTVAL2 WHEN ? THEN 6 WHEN ? THEN 16 ELSE 26 END FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));
//            } else {
//                Assert.assertEquals("SELECT CASE INTVAL2 WHEN ? THEN ? WHEN ? THEN ? ELSE ? END FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));
//            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);

            Expression<Object> selectCase2 = cb2.selectCase(root2.get(QuerySyntaxEntity_.intVal2))
                .when(5, 6)
                .when(15, 16)
                .otherwise(26);
            cquery2.multiselect(selectCase2);

            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), 99));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT CASE INTVAL2 WHEN 5 THEN 6 WHEN 15 THEN 16 ELSE 26 END FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 99)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT CASE INTVAL2 WHEN ? THEN ? WHEN ? THEN ? ELSE ? END FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));
            }

            /*
             * JPA 3.1 Criteria API includes support setting parameters in the CASE select WHEN condition
             *     TODO: https://github.com/eclipse-ee4j/jpa-api/issues/315
             */

//            CriteriaBuilder cb3 = em.getCriteriaBuilder();
//            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
//            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
//
//            ParameterExpression<Integer> checkParam4 = cb3.parameter(Integer.class);
//            ParameterExpression<Integer> resultParam4 = cb3.parameter(Integer.class);
//
//            Expression<Object> selectCase3 = cb3.selectCase(root3.get(QuerySyntaxEntity_.intVal2))
//                .when(5, resultParam4)
//                .when(checkParam4, 16)
//                .otherwise(26);
//            cquery3.multiselect(selectCase3);
//
//            ParameterExpression<Integer> checkParam5 = cb3.parameter(Integer.class);
//            cquery3.where(cb3.equal(root3.get(QuerySyntaxEntity_.intVal1), checkParam5));
//
//            query = em.createQuery(cquery3);
//            query.setParameter(resultParam4, 6);
//            query.setParameter(checkParam4, 15);
//            query.setParameter(checkParam5, 99);
//            query.getResultList();
//            Assert.assertEquals(1, _sql.size());
//            Assert.assertEquals("SELECT CASE INTVAL2 WHEN ? THEN ? WHEN 15 THEN 16 ELSE 26 END FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));
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
    public void testCase3_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT CASE s.intVal2 WHEN ?1 THEN ?2 WHEN ?3 THEN ?4 ELSE ?5 END FROM QuerySyntaxEntity s WHERE s.intVal1 = ?6");
            query.setParameter(1, 5);
            query.setParameter(2, 6);
            query.setParameter(3, 15);
            query.setParameter(4, 16);
            query.setParameter(5, 26);
            query.setParameter(6, 99);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT CASE INTVAL2 WHEN ? THEN 6 WHEN ? THEN 16 ELSE 26 END FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT CASE INTVAL2 WHEN ? THEN ? WHEN ? THEN ? ELSE ? END FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            }

            query = em.createQuery("SELECT CASE s.intVal2 WHEN 5 THEN 6 WHEN 15 THEN 16 ELSE 26 END FROM QuerySyntaxEntity s WHERE s.intVal1 = 99");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT CASE INTVAL2 WHEN ? THEN 6 WHEN ? THEN 16 ELSE 26 END FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT CASE INTVAL2 WHEN ? THEN ? WHEN ? THEN ? ELSE ? END FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            }

            query = em.createQuery("SELECT CASE s.intVal2 WHEN ?1 THEN ?2 WHEN 15 THEN 16 ELSE 26 END FROM QuerySyntaxEntity s WHERE s.intVal1 = ?3");
            query.setParameter(1, 5);
            query.setParameter(2, 6);
            query.setParameter(3, 99);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT CASE INTVAL2 WHEN ? THEN 6 WHEN ? THEN 16 ELSE 26 END FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT CASE INTVAL2 WHEN ? THEN ? WHEN ? THEN ? ELSE ? END FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            }

            // -----------------------

            /*
             * JPA 3.1 Criteria API includes support setting parameters in the CASE select WHEN condition
             *     TODO: https://github.com/eclipse-ee4j/jpa-api/issues/315
             */

//            CriteriaBuilder cb = em.getCriteriaBuilder();
//            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
//            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
//
//            ParameterExpression<Integer> checkParam1 = cb.parameter(Integer.class);
//            ParameterExpression<Integer> checkParam2 = cb.parameter(Integer.class);
//            ParameterExpression<Integer> resultParam1 = cb.parameter(Integer.class);
//            ParameterExpression<Integer> resultParam2 = cb.parameter(Integer.class);
//            ParameterExpression<Integer> resultParam3 = cb.parameter(Integer.class);
//
//            Expression<Object> selectCase = cb.selectCase(root.get(QuerySyntaxEntity_.intVal2))
//                .when(checkParam1, resultParam1)
//                .when(checkParam2, resultParam2)
//                .otherwise(resultParam3);
//            cquery.multiselect(selectCase);
//
//            ParameterExpression<Integer> checkParam3 = cb.parameter(Integer.class);
//            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.intVal1), checkParam3));
//
//            query = em.createQuery(cquery);
//            query.setParameter(checkParam1, 5);
//            query.setParameter(resultParam1, 6);
//            query.setParameter(checkParam2, 15);
//            query.setParameter(resultParam2, 16);
//            query.setParameter(resultParam3, 26);
//            query.setParameter(checkParam3, 99);
//            query.getResultList();
//            Assert.assertEquals(1, _sql2.size());
//            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
//                Assert.assertEquals("SELECT CASE INTVAL2 WHEN ? THEN 6 WHEN ? THEN 16 ELSE 26 END FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
//            } else {
//                Assert.assertEquals("SELECT CASE INTVAL2 WHEN ? THEN ? WHEN ? THEN ? ELSE ? END FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
//            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);

            Expression<Object> selectCase2 = cb2.selectCase(root2.get(QuerySyntaxEntity_.intVal2))
                .when(5, 6)
                .when(15, 16)
                .otherwise(26);
            cquery2.multiselect(selectCase2);

            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), 99));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT CASE INTVAL2 WHEN ? THEN 6 WHEN ? THEN 16 ELSE 26 END FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT CASE INTVAL2 WHEN ? THEN ? WHEN ? THEN ? ELSE ? END FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            }

            /*
             * JPA 3.1 Criteria API includes support setting parameters in the CASE select WHEN condition
             *     TODO: https://github.com/eclipse-ee4j/jpa-api/issues/315
             */

//            CriteriaBuilder cb3 = em.getCriteriaBuilder();
//            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
//            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
//
//            ParameterExpression<Integer> checkParam4 = cb3.parameter(Integer.class);
//            ParameterExpression<Integer> resultParam4 = cb3.parameter(Integer.class);
//
//            Expression<Object> selectCase3 = cb3.selectCase(root3.get(QuerySyntaxEntity_.intVal2))
//                .when(5, resultParam4)
//                .when(checkParam4, 16)
//                .otherwise(26);
//            cquery3.multiselect(selectCase3);
//
//            ParameterExpression<Integer> checkParam5 = cb3.parameter(Integer.class);
//            cquery3.where(cb3.equal(root3.get(QuerySyntaxEntity_.intVal1), checkParam5));
//
//            query = em.createQuery(cquery3);
//            query.setParameter(resultParam4, 6);
//            query.setParameter(checkParam4, 15);
//            query.setParameter(checkParam5, 99);
//            query.getResultList();
//            Assert.assertEquals(1, _sql2.size());
//            Assert.assertEquals("SELECT CASE INTVAL2 WHEN ? THEN ? WHEN 15 THEN 16 ELSE 26 END FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
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
    public void testCase3_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("SELECT CASE s.intVal2 WHEN ?1 THEN ?2 WHEN ?3 THEN ?4 ELSE ?5 END FROM QuerySyntaxEntity s WHERE s.intVal1 = ?6");
            query.setParameter(1, 5);
            query.setParameter(2, 6);
            query.setParameter(3, 15);
            query.setParameter(4, 16);
            query.setParameter(5, 26);
            query.setParameter(6, 99);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT CASE INTVAL2 WHEN ? THEN 6 WHEN ? THEN 16 ELSE 26 END FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT CASE INTVAL2 WHEN ? THEN ? WHEN ? THEN ? ELSE ? END FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            }

            query = em.createQuery("SELECT CASE s.intVal2 WHEN 5 THEN 6 WHEN 15 THEN 16 ELSE 26 END FROM QuerySyntaxEntity s WHERE s.intVal1 = 99");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT CASE INTVAL2 WHEN ? THEN 6 WHEN ? THEN 16 ELSE 26 END FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT CASE INTVAL2 WHEN ? THEN ? WHEN ? THEN ? ELSE ? END FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            }

            query = em.createQuery("SELECT CASE s.intVal2 WHEN ?1 THEN ?2 WHEN 15 THEN 16 ELSE 26 END FROM QuerySyntaxEntity s WHERE s.intVal1 = ?3");
            query.setParameter(1, 5);
            query.setParameter(2, 6);
            query.setParameter(3, 99);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT CASE INTVAL2 WHEN ? THEN 6 WHEN ? THEN 16 ELSE 26 END FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT CASE INTVAL2 WHEN ? THEN ? WHEN ? THEN ? ELSE ? END FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            }

            // -----------------------

            /*
             * JPA 3.1 Criteria API includes support setting parameters in the CASE select WHEN condition
             *     TODO: https://github.com/eclipse-ee4j/jpa-api/issues/315
             */

//            CriteriaBuilder cb = em.getCriteriaBuilder();
//            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
//            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
//
//            ParameterExpression<Integer> checkParam1 = cb.parameter(Integer.class);
//            ParameterExpression<Integer> checkParam2 = cb.parameter(Integer.class);
//            ParameterExpression<Integer> resultParam1 = cb.parameter(Integer.class);
//            ParameterExpression<Integer> resultParam2 = cb.parameter(Integer.class);
//            ParameterExpression<Integer> resultParam3 = cb.parameter(Integer.class);
//
//            Expression<Object> selectCase = cb.selectCase(root.get(QuerySyntaxEntity_.intVal2))
//                .when(checkParam1, resultParam1)
//                .when(checkParam2, resultParam2)
//                .otherwise(resultParam3);
//            cquery.multiselect(selectCase);
//
//            ParameterExpression<Integer> checkParam3 = cb.parameter(Integer.class);
//            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.intVal1), checkParam3));
//
//            query = em.createQuery(cquery);
//            query.setParameter(checkParam1, 5);
//            query.setParameter(resultParam1, 6);
//            query.setParameter(checkParam2, 15);
//            query.setParameter(resultParam2, 16);
//            query.setParameter(resultParam3, 26);
//            query.setParameter(checkParam3, 99);
//            query.getResultList();
//            Assert.assertEquals(1, _sql3.size());
//            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
//                Assert.assertEquals("SELECT CASE INTVAL2 WHEN ? THEN 6 WHEN ? THEN 16 ELSE 26 END FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
//            } else {
//                Assert.assertEquals("SELECT CASE INTVAL2 WHEN ? THEN ? WHEN ? THEN ? ELSE ? END FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
//            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);

            Expression<Object> selectCase2 = cb2.selectCase(root2.get(QuerySyntaxEntity_.intVal2))
                .when(5, 6)
                .when(15, 16)
                .otherwise(26);
            cquery2.multiselect(selectCase2);

            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), 99));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT CASE INTVAL2 WHEN ? THEN 6 WHEN ? THEN 16 ELSE 26 END FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT CASE INTVAL2 WHEN ? THEN ? WHEN ? THEN ? ELSE ? END FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            }

            /*
             * JPA 3.1 Criteria API includes support setting parameters in the CASE select WHEN condition
             *     TODO: https://github.com/eclipse-ee4j/jpa-api/issues/315
             */

//            CriteriaBuilder cb3 = em.getCriteriaBuilder();
//            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
//            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
//
//            ParameterExpression<Integer> checkParam4 = cb3.parameter(Integer.class);
//            ParameterExpression<Integer> resultParam4 = cb3.parameter(Integer.class);
//
//            Expression<Object> selectCase3 = cb3.selectCase(root3.get(QuerySyntaxEntity_.intVal2))
//                .when(5, resultParam4)
//                .when(checkParam4, 16)
//                .otherwise(26);
//            cquery3.multiselect(selectCase3);
//
//            ParameterExpression<Integer> checkParam5 = cb3.parameter(Integer.class);
//            cquery3.where(cb3.equal(root3.get(QuerySyntaxEntity_.intVal1), checkParam5));
//
//            query = em.createQuery(cquery3);
//            query.setParameter(resultParam4, 6);
//            query.setParameter(checkParam4, 15);
//            query.setParameter(checkParam5, 99);
//            query.getResultList();
//            Assert.assertEquals(1, _sql3.size());
//            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
//                Assert.assertEquals("SELECT CASE INTVAL2 WHEN ? THEN 6 WHEN ? THEN 16 ELSE 26 END FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
//            } else {
//                Assert.assertEquals("SELECT CASE INTVAL2 WHEN ? THEN ? WHEN ? THEN ? ELSE ? END FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
//            }
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
    public void testCase4_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT CASE WHEN s.intVal2 = ?1 THEN ?2 WHEN s.intVal2 = ?3 THEN ?4 ELSE ?5 END FROM QuerySyntaxEntity s WHERE s.intVal1 = ?6");
            query.setParameter(1, 5);
            query.setParameter(2, 6);
            query.setParameter(3, 15);
            query.setParameter(4, 16);
            query.setParameter(5, 26);
            query.setParameter(6, 99);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT CASE  WHEN (INTVAL2 = 5) THEN 6 WHEN (INTVAL2 = 15) THEN 16 ELSE 26 END FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 99)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT CASE  WHEN (INTVAL2 = ?) THEN ? WHEN (INTVAL2 = ?) THEN ? ELSE ? END FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));
            }

            query = em.createQuery("SELECT CASE WHEN s.intVal2 = 5 THEN 6 WHEN s.intVal2 = 15 THEN 16 ELSE 26 END FROM QuerySyntaxEntity s WHERE s.intVal1 = 99");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT CASE  WHEN (INTVAL2 = 5) THEN 6 WHEN (INTVAL2 = 15) THEN 16 ELSE 26 END FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 99)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT CASE  WHEN (INTVAL2 = ?) THEN ? WHEN (INTVAL2 = ?) THEN ? ELSE ? END FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));
            }

            query = em.createQuery("SELECT CASE WHEN s.intVal2 = ?1 THEN ?2 WHEN s.intVal2 = 15 THEN 16 ELSE 26 END FROM QuerySyntaxEntity s WHERE s.intVal1 = ?3");
            query.setParameter(1, 5);
            query.setParameter(2, 6);
            query.setParameter(3, 99);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT CASE  WHEN (INTVAL2 = 5) THEN 6 WHEN (INTVAL2 = 15) THEN 16 ELSE 26 END FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 99)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT CASE  WHEN (INTVAL2 = ?) THEN ? WHEN (INTVAL2 = ?) THEN ? ELSE ? END FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);

            ParameterExpression<Integer> checkParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> checkParam2 = cb.parameter(Integer.class);
            ParameterExpression<Integer> resultParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> resultParam2 = cb.parameter(Integer.class);
            ParameterExpression<Integer> resultParam3 = cb.parameter(Integer.class);

            Expression<Object> selectCase = cb.selectCase()
                .when(cb.equal(root.get(QuerySyntaxEntity_.intVal2), checkParam1), resultParam1)
                .when(cb.equal(root.get(QuerySyntaxEntity_.intVal2), checkParam2), resultParam2)
                .otherwise(resultParam3);
            cquery.multiselect(selectCase);

            ParameterExpression<Integer> checkParam3 = cb.parameter(Integer.class);
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.intVal1), checkParam3));

            query = em.createQuery(cquery);
            query.setParameter(checkParam1, 5);
            query.setParameter(resultParam1, 6);
            query.setParameter(checkParam2, 15);
            query.setParameter(resultParam2, 16);
            query.setParameter(resultParam3, 26);
            query.setParameter(checkParam3, 99);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT CASE WHEN (INTVAL2 = 5) THEN 6 WHEN (INTVAL2 = 15) THEN 16 ELSE 26 END  FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 99)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT CASE WHEN (INTVAL2 = ?) THEN ? WHEN (INTVAL2 = ?) THEN ? ELSE ? END  FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);

            Expression<Object> selectCase2 = cb2.selectCase()
                    .when(cb2.equal(root2.get(QuerySyntaxEntity_.intVal2), 5), 6)
                    .when(cb2.equal(root2.get(QuerySyntaxEntity_.intVal2), 15), 16)
                    .otherwise(26);
            cquery2.multiselect(selectCase2);

            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), 99));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT CASE WHEN (INTVAL2 = 5) THEN 6 WHEN (INTVAL2 = 15) THEN 16 ELSE 26 END  FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 99)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT CASE WHEN (INTVAL2 = ?) THEN ? WHEN (INTVAL2 = ?) THEN ? ELSE ? END  FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);

            ParameterExpression<Integer> checkParam4 = cb3.parameter(Integer.class);
            ParameterExpression<Integer> resultParam4 = cb3.parameter(Integer.class);

            Expression<Object> selectCase3 = cb3.selectCase()
                .when(cb3.equal(root3.get(QuerySyntaxEntity_.intVal2), checkParam4), resultParam4)
                .when(cb3.equal(root3.get(QuerySyntaxEntity_.intVal2), 15), 16)
                .otherwise(26);
            cquery3.multiselect(selectCase3);

            ParameterExpression<Integer> checkParam5 = cb3.parameter(Integer.class);
            cquery3.where(cb3.equal(root3.get(QuerySyntaxEntity_.intVal1), checkParam5));

            query = em.createQuery(cquery3);
            query.setParameter(checkParam4, 5);
            query.setParameter(resultParam4, 6);
            query.setParameter(checkParam5, 99);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT CASE WHEN (INTVAL2 = 5) THEN 6 WHEN (INTVAL2 = 15) THEN 16 ELSE 26 END  FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 99)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT CASE WHEN (INTVAL2 = ?) THEN ? WHEN (INTVAL2 = ?) THEN ? ELSE ? END  FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));
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
    public void testCase4_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT CASE WHEN s.intVal2 = ?1 THEN ?2 WHEN s.intVal2 = ?3 THEN ?4 ELSE ?5 END FROM QuerySyntaxEntity s WHERE s.intVal1 = ?6");
            query.setParameter(1, 5);
            query.setParameter(2, 6);
            query.setParameter(3, 15);
            query.setParameter(4, 16);
            query.setParameter(5, 26);
            query.setParameter(6, 99);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT CASE  WHEN (INTVAL2 = ?) THEN 6 WHEN (INTVAL2 = ?) THEN 16 ELSE 26 END FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT CASE  WHEN (INTVAL2 = ?) THEN ? WHEN (INTVAL2 = ?) THEN ? ELSE ? END FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            }

            query = em.createQuery("SELECT CASE WHEN s.intVal2 = 5 THEN 6 WHEN s.intVal2 = 15 THEN 16 ELSE 26 END FROM QuerySyntaxEntity s WHERE s.intVal1 = 99");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT CASE  WHEN (INTVAL2 = ?) THEN 6 WHEN (INTVAL2 = ?) THEN 16 ELSE 26 END FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT CASE  WHEN (INTVAL2 = ?) THEN ? WHEN (INTVAL2 = ?) THEN ? ELSE ? END FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            }

            query = em.createQuery("SELECT CASE WHEN s.intVal2 = ?1 THEN ?2 WHEN s.intVal2 = 15 THEN 16 ELSE 26 END FROM QuerySyntaxEntity s WHERE s.intVal1 = ?3");
            query.setParameter(1, 5);
            query.setParameter(2, 6);
            query.setParameter(3, 99);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT CASE  WHEN (INTVAL2 = ?) THEN 6 WHEN (INTVAL2 = ?) THEN 16 ELSE 26 END FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT CASE  WHEN (INTVAL2 = ?) THEN ? WHEN (INTVAL2 = ?) THEN ? ELSE ? END FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);

            ParameterExpression<Integer> checkParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> checkParam2 = cb.parameter(Integer.class);
            ParameterExpression<Integer> resultParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> resultParam2 = cb.parameter(Integer.class);
            ParameterExpression<Integer> resultParam3 = cb.parameter(Integer.class);

            Expression<Object> selectCase = cb.selectCase()
                .when(cb.equal(root.get(QuerySyntaxEntity_.intVal2), checkParam1), resultParam1)
                .when(cb.equal(root.get(QuerySyntaxEntity_.intVal2), checkParam2), resultParam2)
                .otherwise(resultParam3);
            cquery.multiselect(selectCase);

            ParameterExpression<Integer> checkParam3 = cb.parameter(Integer.class);
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.intVal1), checkParam3));

            query = em.createQuery(cquery);
            query.setParameter(checkParam1, 5);
            query.setParameter(resultParam1, 6);
            query.setParameter(checkParam2, 15);
            query.setParameter(resultParam2, 16);
            query.setParameter(resultParam3, 26);
            query.setParameter(checkParam3, 99);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT CASE WHEN (INTVAL2 = ?) THEN 6 WHEN (INTVAL2 = ?) THEN 16 ELSE 26 END  FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT CASE WHEN (INTVAL2 = ?) THEN ? WHEN (INTVAL2 = ?) THEN ? ELSE ? END  FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);

            Expression<Object> selectCase2 = cb2.selectCase()
                    .when(cb2.equal(root2.get(QuerySyntaxEntity_.intVal2), 5), 6)
                    .when(cb2.equal(root2.get(QuerySyntaxEntity_.intVal2), 15), 16)
                    .otherwise(26);
            cquery2.multiselect(selectCase2);

            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), 99));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT CASE WHEN (INTVAL2 = ?) THEN 6 WHEN (INTVAL2 = ?) THEN 16 ELSE 26 END  FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT CASE WHEN (INTVAL2 = ?) THEN ? WHEN (INTVAL2 = ?) THEN ? ELSE ? END  FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);

            ParameterExpression<Integer> checkParam4 = cb3.parameter(Integer.class);
            ParameterExpression<Integer> resultParam4 = cb3.parameter(Integer.class);

            Expression<Object> selectCase3 = cb3.selectCase()
                .when(cb3.equal(root3.get(QuerySyntaxEntity_.intVal2), checkParam4), resultParam4)
                .when(cb3.equal(root3.get(QuerySyntaxEntity_.intVal2), 15), 16)
                .otherwise(26);
            cquery3.multiselect(selectCase3);

            ParameterExpression<Integer> checkParam5 = cb3.parameter(Integer.class);
            cquery3.where(cb3.equal(root3.get(QuerySyntaxEntity_.intVal1), checkParam5));

            query = em.createQuery(cquery3);
            query.setParameter(checkParam4, 5);
            query.setParameter(resultParam4, 6);
            query.setParameter(checkParam5, 99);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT CASE WHEN (INTVAL2 = ?) THEN 6 WHEN (INTVAL2 = ?) THEN 16 ELSE 26 END  FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT CASE WHEN (INTVAL2 = ?) THEN ? WHEN (INTVAL2 = ?) THEN ? ELSE ? END  FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
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
    public void testCase4_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("SELECT CASE WHEN s.intVal2 = ?1 THEN ?2 WHEN s.intVal2 = ?3 THEN ?4 ELSE ?5 END FROM QuerySyntaxEntity s WHERE s.intVal1 = ?6");
            query.setParameter(1, 5);
            query.setParameter(2, 6);
            query.setParameter(3, 15);
            query.setParameter(4, 16);
            query.setParameter(5, 26);
            query.setParameter(6, 99);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT CASE  WHEN (INTVAL2 = ?) THEN 6 WHEN (INTVAL2 = ?) THEN 16 ELSE 26 END FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT CASE  WHEN (INTVAL2 = ?) THEN ? WHEN (INTVAL2 = ?) THEN ? ELSE ? END FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            }

            query = em.createQuery("SELECT CASE WHEN s.intVal2 = 5 THEN 6 WHEN s.intVal2 = 15 THEN 16 ELSE 26 END FROM QuerySyntaxEntity s WHERE s.intVal1 = 99");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT CASE  WHEN (INTVAL2 = ?) THEN 6 WHEN (INTVAL2 = ?) THEN 16 ELSE 26 END FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT CASE  WHEN (INTVAL2 = ?) THEN ? WHEN (INTVAL2 = ?) THEN ? ELSE ? END FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            }

            query = em.createQuery("SELECT CASE WHEN s.intVal2 = ?1 THEN ?2 WHEN s.intVal2 = 15 THEN 16 ELSE 26 END FROM QuerySyntaxEntity s WHERE s.intVal1 = ?3");
            query.setParameter(1, 5);
            query.setParameter(2, 6);
            query.setParameter(3, 99);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT CASE  WHEN (INTVAL2 = ?) THEN 6 WHEN (INTVAL2 = ?) THEN 16 ELSE 26 END FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT CASE  WHEN (INTVAL2 = ?) THEN ? WHEN (INTVAL2 = ?) THEN ? ELSE ? END FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);

            ParameterExpression<Integer> checkParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> checkParam2 = cb.parameter(Integer.class);
            ParameterExpression<Integer> resultParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> resultParam2 = cb.parameter(Integer.class);
            ParameterExpression<Integer> resultParam3 = cb.parameter(Integer.class);

            Expression<Object> selectCase = cb.selectCase()
                .when(cb.equal(root.get(QuerySyntaxEntity_.intVal2), checkParam1), resultParam1)
                .when(cb.equal(root.get(QuerySyntaxEntity_.intVal2), checkParam2), resultParam2)
                .otherwise(resultParam3);
            cquery.multiselect(selectCase);

            ParameterExpression<Integer> checkParam3 = cb.parameter(Integer.class);
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.intVal1), checkParam3));

            query = em.createQuery(cquery);
            query.setParameter(checkParam1, 5);
            query.setParameter(resultParam1, 6);
            query.setParameter(checkParam2, 15);
            query.setParameter(resultParam2, 16);
            query.setParameter(resultParam3, 26);
            query.setParameter(checkParam3, 99);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT CASE WHEN (INTVAL2 = ?) THEN 6 WHEN (INTVAL2 = ?) THEN 16 ELSE 26 END  FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT CASE WHEN (INTVAL2 = ?) THEN ? WHEN (INTVAL2 = ?) THEN ? ELSE ? END  FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);

            Expression<Object> selectCase2 = cb2.selectCase()
                    .when(cb2.equal(root2.get(QuerySyntaxEntity_.intVal2), 5), 6)
                    .when(cb2.equal(root2.get(QuerySyntaxEntity_.intVal2), 15), 16)
                    .otherwise(26);
            cquery2.multiselect(selectCase2);

            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), 99));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT CASE WHEN (INTVAL2 = ?) THEN 6 WHEN (INTVAL2 = ?) THEN 16 ELSE 26 END  FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT CASE WHEN (INTVAL2 = ?) THEN ? WHEN (INTVAL2 = ?) THEN ? ELSE ? END  FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);

            ParameterExpression<Integer> checkParam4 = cb3.parameter(Integer.class);
            ParameterExpression<Integer> resultParam4 = cb3.parameter(Integer.class);

            Expression<Object> selectCase3 = cb3.selectCase()
                .when(cb3.equal(root3.get(QuerySyntaxEntity_.intVal2), checkParam4), resultParam4)
                .when(cb3.equal(root3.get(QuerySyntaxEntity_.intVal2), 15), 16)
                .otherwise(26);
            cquery3.multiselect(selectCase3);

            ParameterExpression<Integer> checkParam5 = cb3.parameter(Integer.class);
            cquery3.where(cb3.equal(root3.get(QuerySyntaxEntity_.intVal1), checkParam5));

            query = em.createQuery(cquery3);
            query.setParameter(checkParam4, 5);
            query.setParameter(resultParam4, 6);
            query.setParameter(checkParam5, 99);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT CASE WHEN (INTVAL2 = ?) THEN 6 WHEN (INTVAL2 = ?) THEN 16 ELSE 26 END  FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT CASE WHEN (INTVAL2 = ?) THEN ? WHEN (INTVAL2 = ?) THEN ? ELSE ? END  FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
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
    public void testNullIf1_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT 1 FROM QuerySyntaxEntity s WHERE ?1 = NULLIF(s.strVal1, ?2)");
            query.setParameter(1, "HELLO");
            query.setParameter(2, "WORLD");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE ('HELLO' = NULLIF(STRVAL1, 'WORLD'))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY WHERE (? = NULLIF(STRVAL1, ?))", _sql.remove(0));
            }

            query = em.createQuery("SELECT 1 FROM QuerySyntaxEntity s WHERE 'HELLO' = NULLIF(s.strVal1, 'WORLD')");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE ('HELLO' = NULLIF(STRVAL1, 'WORLD'))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY WHERE (? = NULLIF(STRVAL1, ?))", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            ParameterExpression<String> strParam2 = cb.parameter(String.class);
            cquery.multiselect(cb.literal(1));
            cquery.where(cb.equal(strParam1, cb.nullif(root.get(QuerySyntaxEntity_.strVal1), strParam2)));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "HELLO");
            query.setParameter(strParam2, "WORLD");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE ('HELLO' = NULLIF(STRVAL1, 'WORLD'))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY WHERE (? = NULLIF(STRVAL1, ?))", _sql.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(cb2.literal(1));
            cquery2.where(cb2.equal(cb2.literal("HELLO"), cb2.nullif(root2.get(QuerySyntaxEntity_.strVal1), "WORLD")));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE ('HELLO' = NULLIF(STRVAL1, 'WORLD'))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY WHERE (? = NULLIF(STRVAL1, ?))", _sql.remove(0));
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
    public void testNullIf1_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT 1 FROM QuerySyntaxEntity s WHERE ?1 = NULLIF(s.strVal1, ?2)");
            query.setParameter(1, "HELLO");
            query.setParameter(2, "WORLD");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE (? = NULLIF(STRVAL1, ?))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY WHERE (? = NULLIF(STRVAL1, ?))", _sql2.remove(0));
            }

            query = em.createQuery("SELECT 1 FROM QuerySyntaxEntity s WHERE 'HELLO' = NULLIF(s.strVal1, 'WORLD')");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE (? = NULLIF(STRVAL1, ?))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY WHERE (? = NULLIF(STRVAL1, ?))", _sql2.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            ParameterExpression<String> strParam2 = cb.parameter(String.class);
            cquery.multiselect(cb.literal(1));
            cquery.where(cb.equal(strParam1, cb.nullif(root.get(QuerySyntaxEntity_.strVal1), strParam2)));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "HELLO");
            query.setParameter(strParam2, "WORLD");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE (? = NULLIF(STRVAL1, ?))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY WHERE (? = NULLIF(STRVAL1, ?))", _sql2.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(cb2.literal(1));
            cquery2.where(cb2.equal(cb2.literal("HELLO"), cb2.nullif(root2.get(QuerySyntaxEntity_.strVal1), "WORLD")));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE (? = NULLIF(STRVAL1, ?))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY WHERE (? = NULLIF(STRVAL1, ?))", _sql2.remove(0));
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
    public void testNullIf1_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("SELECT 1 FROM QuerySyntaxEntity s WHERE ?1 = NULLIF(s.strVal1, ?2)");
            query.setParameter(1, "HELLO");
            query.setParameter(2, "WORLD");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE (? = NULLIF(STRVAL1, ?))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY WHERE (? = NULLIF(STRVAL1, ?))", _sql3.remove(0));
            }

            query = em.createQuery("SELECT 1 FROM QuerySyntaxEntity s WHERE 'HELLO' = NULLIF(s.strVal1, 'WORLD')");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE (? = NULLIF(STRVAL1, ?))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY WHERE (? = NULLIF(STRVAL1, ?))", _sql3.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            ParameterExpression<String> strParam2 = cb.parameter(String.class);
            cquery.multiselect(cb.literal(1));
            cquery.where(cb.equal(strParam1, cb.nullif(root.get(QuerySyntaxEntity_.strVal1), strParam2)));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "HELLO");
            query.setParameter(strParam2, "WORLD");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE (? = NULLIF(STRVAL1, ?))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY WHERE (? = NULLIF(STRVAL1, ?))", _sql3.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(cb2.literal(1));
            cquery2.where(cb2.equal(cb2.literal("HELLO"), cb2.nullif(root2.get(QuerySyntaxEntity_.strVal1), "WORLD")));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE (? = NULLIF(STRVAL1, ?))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY WHERE (? = NULLIF(STRVAL1, ?))", _sql3.remove(0));
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
    public void testNullIf2_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT 1 FROM QuerySyntaxEntity s WHERE ?1 = NULLIF(?2, s.strVal1)");
            query.setParameter(1, "HELLO");
            query.setParameter(2, "WORLD");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE ('HELLO' = NULLIF('WORLD', STRVAL1))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY WHERE (? = NULLIF(?, STRVAL1))", _sql.remove(0));
            }

            query = em.createQuery("SELECT 1 FROM QuerySyntaxEntity s WHERE 'HELLO' = NULLIF('WORLD', s.strVal1)");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE ('HELLO' = NULLIF('WORLD', STRVAL1))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY WHERE (? = NULLIF(?, STRVAL1))", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            ParameterExpression<String> strParam2 = cb.parameter(String.class);
            cquery.multiselect(cb.literal(1));
            cquery.where(cb.equal(strParam1, cb.nullif(strParam2, root.get(QuerySyntaxEntity_.strVal1))));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "HELLO");
            query.setParameter(strParam2, "WORLD");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE ('HELLO' = NULLIF('WORLD', STRVAL1))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY WHERE (? = NULLIF(?, STRVAL1))", _sql.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(cb2.literal(1));
            cquery2.where(cb2.equal(cb2.literal("HELLO"), cb2.nullif((Expression<String>) cb2.literal("WORLD"), root2.get(QuerySyntaxEntity_.strVal1))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE ('HELLO' = NULLIF('WORLD', STRVAL1))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY WHERE (? = NULLIF(?, STRVAL1))", _sql.remove(0));
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
    public void testNullIf2_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT 1 FROM QuerySyntaxEntity s WHERE ?1 = NULLIF(?2, s.strVal1)");
            query.setParameter(1, "HELLO");
            query.setParameter(2, "WORLD");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE (? = NULLIF(?, STRVAL1))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY WHERE (? = NULLIF(?, STRVAL1))", _sql2.remove(0));
            }

            query = em.createQuery("SELECT 1 FROM QuerySyntaxEntity s WHERE 'HELLO' = NULLIF('WORLD', s.strVal1)");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE (? = NULLIF(?, STRVAL1))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY WHERE (? = NULLIF(?, STRVAL1))", _sql2.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            ParameterExpression<String> strParam2 = cb.parameter(String.class);
            cquery.multiselect(cb.literal(1));
            cquery.where(cb.equal(strParam1, cb.nullif(strParam2, root.get(QuerySyntaxEntity_.strVal1))));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "HELLO");
            query.setParameter(strParam2, "WORLD");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE (? = NULLIF(?, STRVAL1))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY WHERE (? = NULLIF(?, STRVAL1))", _sql2.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(cb2.literal(1));
            cquery2.where(cb2.equal(cb2.literal("HELLO"), cb2.nullif((Expression<String>) cb2.literal("WORLD"), root2.get(QuerySyntaxEntity_.strVal1))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE (? = NULLIF(?, STRVAL1))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY WHERE (? = NULLIF(?, STRVAL1))", _sql2.remove(0));
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
    public void testNullIf2_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("SELECT 1 FROM QuerySyntaxEntity s WHERE ?1 = NULLIF(?2, s.strVal1)");
            query.setParameter(1, "HELLO");
            query.setParameter(2, "WORLD");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE (? = NULLIF(?, STRVAL1))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY WHERE (? = NULLIF(?, STRVAL1))", _sql3.remove(0));
            }

            query = em.createQuery("SELECT 1 FROM QuerySyntaxEntity s WHERE 'HELLO' = NULLIF('WORLD', s.strVal1)");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE (? = NULLIF(?, STRVAL1))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY WHERE (? = NULLIF(?, STRVAL1))", _sql3.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            ParameterExpression<String> strParam2 = cb.parameter(String.class);
            cquery.multiselect(cb.literal(1));
            cquery.where(cb.equal(strParam1, cb.nullif(strParam2, root.get(QuerySyntaxEntity_.strVal1))));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "HELLO");
            query.setParameter(strParam2, "WORLD");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE (? = NULLIF(?, STRVAL1))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY WHERE (? = NULLIF(?, STRVAL1))", _sql3.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(cb2.literal(1));
            cquery2.where(cb2.equal(cb2.literal("HELLO"), cb2.nullif((Expression<String>) cb2.literal("WORLD"), root2.get(QuerySyntaxEntity_.strVal1))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE (? = NULLIF(?, STRVAL1))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY WHERE (? = NULLIF(?, STRVAL1))", _sql3.remove(0));
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
    public void testNullIf3_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT NULLIF(?1, ?2) FROM QuerySyntaxEntity s");
            query.setParameter(1, "HELLO");
            query.setParameter(2, "WORLD");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT NULLIF('HELLO', 'WORLD') FROM QUERYSYNTAXENTITY", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT NULLIF(?, ?) FROM QUERYSYNTAXENTITY", _sql.remove(0));
            }

            query = em.createQuery("SELECT NULLIF('HELLO', 'WORLD') FROM QuerySyntaxEntity s");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT NULLIF('HELLO', 'WORLD') FROM QUERYSYNTAXENTITY", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT NULLIF(?, ?) FROM QUERYSYNTAXENTITY", _sql.remove(0));
            }

            query = em.createQuery("SELECT NULLIF(?1, 'WORLD') FROM QuerySyntaxEntity s");
            query.setParameter(1, "HELLO");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT NULLIF('HELLO', 'WORLD') FROM QUERYSYNTAXENTITY", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT NULLIF(?, ?) FROM QUERYSYNTAXENTITY", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            ParameterExpression<String> strParam2 = cb.parameter(String.class);
            cquery.multiselect(cb.nullif(strParam1, strParam2));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "HELLO");
            query.setParameter(strParam2, "WORLD");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT NULLIF('HELLO', 'WORLD') FROM QUERYSYNTAXENTITY", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT NULLIF(?, ?) FROM QUERYSYNTAXENTITY", _sql.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(cb2.nullif((Expression<String>) cb2.literal("HELLO"), cb2.literal("WORLD")));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT NULLIF('HELLO', 'WORLD') FROM QUERYSYNTAXENTITY", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT NULLIF(?, ?) FROM QUERYSYNTAXENTITY", _sql.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            cquery3.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam3 = cb3.parameter(String.class);
            cquery3.multiselect(cb3.nullif(strParam3, cb3.literal("WORLD")));

            query = em.createQuery(cquery3);
            query.setParameter(strParam3, "HELLO");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT NULLIF('HELLO', 'WORLD') FROM QUERYSYNTAXENTITY", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT NULLIF(?, ?) FROM QUERYSYNTAXENTITY", _sql.remove(0));
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
    public void testNullIf3_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT NULLIF(?1, ?2) FROM QuerySyntaxEntity s");
            query.setParameter(1, "HELLO");
            query.setParameter(2, "WORLD");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT NULLIF(?, 'WORLD') FROM QUERYSYNTAXENTITY", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT NULLIF(?, ?) FROM QUERYSYNTAXENTITY", _sql2.remove(0));
            }

            query = em.createQuery("SELECT NULLIF('HELLO', 'WORLD') FROM QuerySyntaxEntity s");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT NULLIF(?, 'WORLD') FROM QUERYSYNTAXENTITY", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT NULLIF(?, ?) FROM QUERYSYNTAXENTITY", _sql2.remove(0));
            }

            query = em.createQuery("SELECT NULLIF(?1, 'WORLD') FROM QuerySyntaxEntity s");
            query.setParameter(1, "HELLO");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT NULLIF(?, 'WORLD') FROM QUERYSYNTAXENTITY", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT NULLIF(?, ?) FROM QUERYSYNTAXENTITY", _sql2.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            ParameterExpression<String> strParam2 = cb.parameter(String.class);
            cquery.multiselect(cb.nullif(strParam1, strParam2));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "HELLO");
            query.setParameter(strParam2, "WORLD");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT NULLIF(?, 'WORLD') FROM QUERYSYNTAXENTITY", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT NULLIF(?, ?) FROM QUERYSYNTAXENTITY", _sql2.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(cb2.nullif((Expression<String>) cb2.literal("HELLO"), cb2.literal("WORLD")));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT NULLIF(?, 'WORLD') FROM QUERYSYNTAXENTITY", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT NULLIF(?, ?) FROM QUERYSYNTAXENTITY", _sql2.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            cquery3.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam3 = cb3.parameter(String.class);
            cquery3.multiselect(cb3.nullif(strParam3, cb3.literal("WORLD")));

            query = em.createQuery(cquery3);
            query.setParameter(strParam3, "HELLO");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT NULLIF(?, 'WORLD') FROM QUERYSYNTAXENTITY", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT NULLIF(?, ?) FROM QUERYSYNTAXENTITY", _sql2.remove(0));
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
    public void testNullIf3_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("SELECT NULLIF(?1, ?2) FROM QuerySyntaxEntity s");
            query.setParameter(1, "HELLO");
            query.setParameter(2, "WORLD");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT NULLIF(?, 'WORLD') FROM QUERYSYNTAXENTITY", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT NULLIF(?, ?) FROM QUERYSYNTAXENTITY", _sql3.remove(0));
            }

            query = em.createQuery("SELECT NULLIF('HELLO', 'WORLD') FROM QuerySyntaxEntity s");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT NULLIF(?, 'WORLD') FROM QUERYSYNTAXENTITY", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT NULLIF(?, ?) FROM QUERYSYNTAXENTITY", _sql3.remove(0));
            }

            query = em.createQuery("SELECT NULLIF(?1, 'WORLD') FROM QuerySyntaxEntity s");
            query.setParameter(1, "HELLO");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT NULLIF(?, 'WORLD') FROM QUERYSYNTAXENTITY", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT NULLIF(?, ?) FROM QUERYSYNTAXENTITY", _sql3.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            ParameterExpression<String> strParam2 = cb.parameter(String.class);
            cquery.multiselect(cb.nullif(strParam1, strParam2));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "HELLO");
            query.setParameter(strParam2, "WORLD");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT NULLIF(?, 'WORLD') FROM QUERYSYNTAXENTITY", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT NULLIF(?, ?) FROM QUERYSYNTAXENTITY", _sql3.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(cb2.nullif((Expression<String>) cb2.literal("HELLO"), cb2.literal("WORLD")));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT NULLIF(?, 'WORLD') FROM QUERYSYNTAXENTITY", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT NULLIF(?, ?) FROM QUERYSYNTAXENTITY", _sql3.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            cquery3.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam3 = cb3.parameter(String.class);
            cquery3.multiselect(cb3.nullif(strParam3, cb3.literal("WORLD")));

            query = em.createQuery(cquery3);
            query.setParameter(strParam3, "HELLO");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT NULLIF(?, 'WORLD') FROM QUERYSYNTAXENTITY", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT NULLIF(?, ?) FROM QUERYSYNTAXENTITY", _sql3.remove(0));
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
    public void testCoalesce1_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.strVal2 FROM QuerySyntaxEntity s WHERE COALESCE(s.intVal1, ?3) = s.intVal2");
            query.setParameter(3, 5);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT STRVAL2 FROM QUERYSYNTAXENTITY WHERE (COALESCE(INTVAL1, ?) = INTVAL2)", _sql.remove(0));

            query = em.createQuery("SELECT s.strVal2 FROM QuerySyntaxEntity s WHERE COALESCE(s.intVal1, 5) = s.intVal2");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT STRVAL2 FROM QUERYSYNTAXENTITY WHERE (COALESCE(INTVAL1, ?) = INTVAL2)", _sql.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.strVal2));

            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            CriteriaBuilder.Coalesce<Integer> coalesce = cb.coalesce();
            coalesce.value(root.get(QuerySyntaxEntity_.intVal1));
            coalesce.value(intParam1);
            cquery.where(cb.equal(coalesce, root.get(QuerySyntaxEntity_.intVal2)));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 5);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT STRVAL2 FROM QUERYSYNTAXENTITY WHERE (COALESCE(INTVAL1, ?) = INTVAL2)", _sql.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.strVal2));

            ParameterExpression<Integer> intParam2 = cb2.parameter(Integer.class);
            Expression<Integer> coalesce2 = cb2.coalesce(root2.get(QuerySyntaxEntity_.intVal1), intParam2);
            cquery2.where(cb2.equal(coalesce2, root2.get(QuerySyntaxEntity_.intVal2)));

            query = em.createQuery(cquery2);
            query.setParameter(intParam2, 5);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT STRVAL2 FROM QUERYSYNTAXENTITY WHERE (COALESCE(INTVAL1, ?) = INTVAL2)", _sql.remove(0));

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.strVal2));

            CriteriaBuilder.Coalesce<Integer> coalesce3 = cb3.coalesce();
            coalesce3.value(root3.get(QuerySyntaxEntity_.intVal1));
            coalesce3.value(cb3.literal(5));
            cquery3.where(cb3.equal(coalesce3, root3.get(QuerySyntaxEntity_.intVal2)));

            query = em.createQuery(cquery3);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT STRVAL2 FROM QUERYSYNTAXENTITY WHERE (COALESCE(INTVAL1, ?) = INTVAL2)", _sql.remove(0));

            CriteriaBuilder cb4 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery4 = cb4.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root4 = cquery4.from(QuerySyntaxEntity.class);
            cquery4.multiselect(root4.get(QuerySyntaxEntity_.strVal2));

            Expression<Integer> coalesce4 = cb4.coalesce(root4.get(QuerySyntaxEntity_.intVal1), cb4.literal(5));
            cquery4.where(cb4.equal(coalesce4, root4.get(QuerySyntaxEntity_.intVal2)));

            query = em.createQuery(cquery4);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT STRVAL2 FROM QUERYSYNTAXENTITY WHERE (COALESCE(INTVAL1, ?) = INTVAL2)", _sql.remove(0));
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
    public void testCoalesce1_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.strVal2 FROM QuerySyntaxEntity s WHERE COALESCE(s.intVal1, ?3) = s.intVal2");
            query.setParameter(3, 5);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT STRVAL2 FROM QUERYSYNTAXENTITY WHERE (COALESCE(INTVAL1, ?) = INTVAL2)", _sql2.remove(0));

            query = em.createQuery("SELECT s.strVal2 FROM QuerySyntaxEntity s WHERE COALESCE(s.intVal1, 5) = s.intVal2");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT STRVAL2 FROM QUERYSYNTAXENTITY WHERE (COALESCE(INTVAL1, ?) = INTVAL2)", _sql2.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.strVal2));

            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            CriteriaBuilder.Coalesce<Integer> coalesce = cb.coalesce();
            coalesce.value(root.get(QuerySyntaxEntity_.intVal1));
            coalesce.value(intParam1);
            cquery.where(cb.equal(coalesce, root.get(QuerySyntaxEntity_.intVal2)));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 5);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT STRVAL2 FROM QUERYSYNTAXENTITY WHERE (COALESCE(INTVAL1, ?) = INTVAL2)", _sql2.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.strVal2));

            ParameterExpression<Integer> intParam2 = cb2.parameter(Integer.class);
            Expression<Integer> coalesce2 = cb2.coalesce(root2.get(QuerySyntaxEntity_.intVal1), intParam2);
            cquery2.where(cb2.equal(coalesce2, root2.get(QuerySyntaxEntity_.intVal2)));

            query = em.createQuery(cquery2);
            query.setParameter(intParam2, 5);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT STRVAL2 FROM QUERYSYNTAXENTITY WHERE (COALESCE(INTVAL1, ?) = INTVAL2)", _sql2.remove(0));

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.strVal2));

            CriteriaBuilder.Coalesce<Integer> coalesce3 = cb3.coalesce();
            coalesce3.value(root3.get(QuerySyntaxEntity_.intVal1));
            coalesce3.value(cb3.literal(5));
            cquery3.where(cb3.equal(coalesce3, root3.get(QuerySyntaxEntity_.intVal2)));

            query = em.createQuery(cquery3);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT STRVAL2 FROM QUERYSYNTAXENTITY WHERE (COALESCE(INTVAL1, ?) = INTVAL2)", _sql2.remove(0));

            CriteriaBuilder cb4 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery4 = cb4.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root4 = cquery4.from(QuerySyntaxEntity.class);
            cquery4.multiselect(root4.get(QuerySyntaxEntity_.strVal2));

            Expression<Integer> coalesce4 = cb4.coalesce(root4.get(QuerySyntaxEntity_.intVal1), cb4.literal(5));
            cquery4.where(cb4.equal(coalesce4, root4.get(QuerySyntaxEntity_.intVal2)));

            query = em.createQuery(cquery4);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT STRVAL2 FROM QUERYSYNTAXENTITY WHERE (COALESCE(INTVAL1, ?) = INTVAL2)", _sql2.remove(0));
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
    public void testCoalesce1_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.strVal2 FROM QuerySyntaxEntity s WHERE COALESCE(s.intVal1, ?3) = s.intVal2");
            query.setParameter(3, 5);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT STRVAL2 FROM QUERYSYNTAXENTITY WHERE (COALESCE(INTVAL1, ?) = INTVAL2)", _sql3.remove(0));

            query = em.createQuery("SELECT s.strVal2 FROM QuerySyntaxEntity s WHERE COALESCE(s.intVal1, 5) = s.intVal2");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT STRVAL2 FROM QUERYSYNTAXENTITY WHERE (COALESCE(INTVAL1, ?) = INTVAL2)", _sql3.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.strVal2));

            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            CriteriaBuilder.Coalesce<Integer> coalesce = cb.coalesce();
            coalesce.value(root.get(QuerySyntaxEntity_.intVal1));
            coalesce.value(intParam1);
            cquery.where(cb.equal(coalesce, root.get(QuerySyntaxEntity_.intVal2)));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 5);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT STRVAL2 FROM QUERYSYNTAXENTITY WHERE (COALESCE(INTVAL1, ?) = INTVAL2)", _sql3.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.strVal2));

            ParameterExpression<Integer> intParam2 = cb2.parameter(Integer.class);
            Expression<Integer> coalesce2 = cb2.coalesce(root2.get(QuerySyntaxEntity_.intVal1), intParam2);
            cquery2.where(cb2.equal(coalesce2, root2.get(QuerySyntaxEntity_.intVal2)));

            query = em.createQuery(cquery2);
            query.setParameter(intParam2, 5);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT STRVAL2 FROM QUERYSYNTAXENTITY WHERE (COALESCE(INTVAL1, ?) = INTVAL2)", _sql3.remove(0));

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.strVal2));

            CriteriaBuilder.Coalesce<Integer> coalesce3 = cb3.coalesce();
            coalesce3.value(root3.get(QuerySyntaxEntity_.intVal1));
            coalesce3.value(cb3.literal(5));
            cquery3.where(cb3.equal(coalesce3, root3.get(QuerySyntaxEntity_.intVal2)));

            query = em.createQuery(cquery3);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT STRVAL2 FROM QUERYSYNTAXENTITY WHERE (COALESCE(INTVAL1, ?) = INTVAL2)", _sql3.remove(0));

            CriteriaBuilder cb4 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery4 = cb4.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root4 = cquery4.from(QuerySyntaxEntity.class);
            cquery4.multiselect(root4.get(QuerySyntaxEntity_.strVal2));

            Expression<Integer> coalesce4 = cb4.coalesce(root4.get(QuerySyntaxEntity_.intVal1), cb4.literal(5));
            cquery4.where(cb4.equal(coalesce4, root4.get(QuerySyntaxEntity_.intVal2)));

            query = em.createQuery(cquery4);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT STRVAL2 FROM QUERYSYNTAXENTITY WHERE (COALESCE(INTVAL1, ?) = INTVAL2)", _sql3.remove(0));
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
    public void testCoalesce2_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();

        try {
            Query query = em.createQuery("SELECT COALESCE(s.intVal1, ?3) FROM QuerySyntaxEntity s");
            query.setParameter(3, 5);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT COALESCE(INTVAL1, ?) FROM QUERYSYNTAXENTITY", _sql.remove(0));

            query = em.createQuery("SELECT COALESCE(s.intVal1, 5) FROM QuerySyntaxEntity s");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT COALESCE(INTVAL1, ?) FROM QUERYSYNTAXENTITY", _sql.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);

            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            CriteriaBuilder.Coalesce<Integer> coalesce = cb.coalesce();
            coalesce.value(root.get(QuerySyntaxEntity_.intVal1));
            coalesce.value(intParam1);
            cquery.multiselect(coalesce);

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 5);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT COALESCE(INTVAL1, ?) FROM QUERYSYNTAXENTITY", _sql.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery2 = cb2.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);

            ParameterExpression<Integer> intParam2 = cb2.parameter(Integer.class);
            Expression<Integer> coalesce2 = cb2.coalesce(root2.get(QuerySyntaxEntity_.intVal1), intParam2);
            cquery2.multiselect(coalesce2);

            query = em.createQuery(cquery2);
            query.setParameter(intParam2, 5);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT COALESCE(INTVAL1, ?) FROM QUERYSYNTAXENTITY", _sql.remove(0));

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery3 = cb3.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);

            CriteriaBuilder.Coalesce<Integer> coalesce3 = cb3.coalesce();
            coalesce3.value(root3.get(QuerySyntaxEntity_.intVal1));
            coalesce3.value(cb3.literal(5));
            cquery3.multiselect(coalesce3);

            query = em.createQuery(cquery3);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT COALESCE(INTVAL1, ?) FROM QUERYSYNTAXENTITY", _sql.remove(0));

            CriteriaBuilder cb4 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery4 = cb4.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root4 = cquery4.from(QuerySyntaxEntity.class);

            Expression<Integer> coalesce4 = cb4.coalesce(root4.get(QuerySyntaxEntity_.intVal1), cb4.literal(5));
            cquery4.multiselect(coalesce4);

            query = em.createQuery(cquery4);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT COALESCE(INTVAL1, ?) FROM QUERYSYNTAXENTITY", _sql.remove(0));
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
    public void testCoalesce2_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();

        try {
            Query query = em.createQuery("SELECT COALESCE(s.intVal1, ?3) FROM QuerySyntaxEntity s");
            query.setParameter(3, 5);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT COALESCE(INTVAL1, ?) FROM QUERYSYNTAXENTITY", _sql2.remove(0));

            query = em.createQuery("SELECT COALESCE(s.intVal1, 5) FROM QuerySyntaxEntity s");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT COALESCE(INTVAL1, ?) FROM QUERYSYNTAXENTITY", _sql2.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);

            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            CriteriaBuilder.Coalesce<Integer> coalesce = cb.coalesce();
            coalesce.value(root.get(QuerySyntaxEntity_.intVal1));
            coalesce.value(intParam1);
            cquery.multiselect(coalesce);

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 5);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT COALESCE(INTVAL1, ?) FROM QUERYSYNTAXENTITY", _sql2.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery2 = cb2.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);

            ParameterExpression<Integer> intParam2 = cb2.parameter(Integer.class);
            Expression<Integer> coalesce2 = cb2.coalesce(root2.get(QuerySyntaxEntity_.intVal1), intParam2);
            cquery2.multiselect(coalesce2);

            query = em.createQuery(cquery2);
            query.setParameter(intParam2, 5);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT COALESCE(INTVAL1, ?) FROM QUERYSYNTAXENTITY", _sql2.remove(0));

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery3 = cb3.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);

            CriteriaBuilder.Coalesce<Integer> coalesce3 = cb3.coalesce();
            coalesce3.value(root3.get(QuerySyntaxEntity_.intVal1));
            coalesce3.value(cb3.literal(5));
            cquery3.multiselect(coalesce3);

            query = em.createQuery(cquery3);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT COALESCE(INTVAL1, ?) FROM QUERYSYNTAXENTITY", _sql2.remove(0));

            CriteriaBuilder cb4 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery4 = cb4.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root4 = cquery4.from(QuerySyntaxEntity.class);

            Expression<Integer> coalesce4 = cb4.coalesce(root4.get(QuerySyntaxEntity_.intVal1), cb4.literal(5));
            cquery4.multiselect(coalesce4);

            query = em.createQuery(cquery4);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT COALESCE(INTVAL1, ?) FROM QUERYSYNTAXENTITY", _sql2.remove(0));
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
    public void testCoalesce2_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();

        try {
            Query query = em.createQuery("SELECT COALESCE(s.intVal1, ?3) FROM QuerySyntaxEntity s");
            query.setParameter(3, 5);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT COALESCE(INTVAL1, ?) FROM QUERYSYNTAXENTITY", _sql3.remove(0));

            query = em.createQuery("SELECT COALESCE(s.intVal1, 5) FROM QuerySyntaxEntity s");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT COALESCE(INTVAL1, ?) FROM QUERYSYNTAXENTITY", _sql3.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery = cb.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);

            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            CriteriaBuilder.Coalesce<Integer> coalesce = cb.coalesce();
            coalesce.value(root.get(QuerySyntaxEntity_.intVal1));
            coalesce.value(intParam1);
            cquery.multiselect(coalesce);

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 5);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT COALESCE(INTVAL1, ?) FROM QUERYSYNTAXENTITY", _sql3.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery2 = cb2.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);

            ParameterExpression<Integer> intParam2 = cb2.parameter(Integer.class);
            Expression<Integer> coalesce2 = cb2.coalesce(root2.get(QuerySyntaxEntity_.intVal1), intParam2);
            cquery2.multiselect(coalesce2);

            query = em.createQuery(cquery2);
            query.setParameter(intParam2, 5);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT COALESCE(INTVAL1, ?) FROM QUERYSYNTAXENTITY", _sql3.remove(0));

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery3 = cb3.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);

            CriteriaBuilder.Coalesce<Integer> coalesce3 = cb3.coalesce();
            coalesce3.value(root3.get(QuerySyntaxEntity_.intVal1));
            coalesce3.value(cb3.literal(5));
            cquery3.multiselect(coalesce3);

            query = em.createQuery(cquery3);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT COALESCE(INTVAL1, ?) FROM QUERYSYNTAXENTITY", _sql3.remove(0));

            CriteriaBuilder cb4 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery4 = cb4.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root4 = cquery4.from(QuerySyntaxEntity.class);

            Expression<Integer> coalesce4 = cb4.coalesce(root4.get(QuerySyntaxEntity_.intVal1), cb4.literal(5));
            cquery4.multiselect(coalesce4);

            query = em.createQuery(cquery4);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT COALESCE(INTVAL1, ?) FROM QUERYSYNTAXENTITY", _sql3.remove(0));
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
    public void testCoalesce3_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT s.strVal2 FROM QuerySyntaxEntity s WHERE COALESCE(?1, ?2, ?3, ?4, ?5) = s.intVal2");
            query.setParameter(1, 2);
            query.setParameter(2, 7);
            query.setParameter(3, 5);
            query.setParameter(4, 9);
            query.setParameter(5, 12);
            Exception exception = null;
            try {
                query.getResultList();
                Assert.assertEquals(1, _sql.size());
            } catch(Exception e) {
                exception = e;
            } finally {
                if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                    Assert.assertNotNull("Expected query '" + _sql.remove(0) + "' to fail", exception);
                } else {
                    Assert.assertNull(exception);
                    Assert.assertEquals("SELECT STRVAL2 FROM QUERYSYNTAXENTITY WHERE (COALESCE(?, ?, ?, ?, ?) = INTVAL2)", _sql.remove(0));
                }
            }

            query = em.createQuery("SELECT s.strVal2 FROM QuerySyntaxEntity s WHERE COALESCE(NULL, 7, 5, 9, 12) = s.intVal2");
            exception = null;
            try {
                query.getResultList();
                Assert.assertEquals(1, _sql.size());
            } catch(Exception e) {
                exception = e;
            } finally {
                if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                    // DB2 z throws `DB2 SQL Error: SQLCODE=-206, SQLSTATE=42703` because a "NULL" value is being passed
                    // Derby throws `ERROR 42X01: Syntax error: Encountered "NULL" at line 1, column 50.`
                    Assert.assertNotNull("Expected query '" + _sql.remove(0) + "' to fail", exception);
                } else {
                    Assert.assertNull(exception);
                    Assert.assertEquals("SELECT STRVAL2 FROM QUERYSYNTAXENTITY WHERE (COALESCE(?, ?, ?, ?, ?) = INTVAL2)", _sql.remove(0));
                }
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.strVal2));

            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam3 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam4 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam5 = cb.parameter(Integer.class);
            CriteriaBuilder.Coalesce<Integer> coalesce = cb.coalesce();
            coalesce.value(intParam1);
            coalesce.value(intParam2);
            coalesce.value(intParam3);
            coalesce.value(intParam4);
            coalesce.value(intParam5);
            cquery.where(cb.equal(coalesce, root.get(QuerySyntaxEntity_.intVal2)));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, null);
            query.setParameter(intParam2, 7);
            query.setParameter(intParam3, 5);
            query.setParameter(intParam4, 9);
            query.setParameter(intParam5, 12);
            exception = null;
            try {
                query.getResultList();
                Assert.assertEquals(1, _sql.size());
            } catch(Exception e) {
                exception = e;
            } finally {
                if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                    Assert.assertNotNull("Expected query '" + _sql.remove(0) + "' to fail", exception);
                } else {
                    Assert.assertNull(exception);
                    Assert.assertEquals("SELECT STRVAL2 FROM QUERYSYNTAXENTITY WHERE (COALESCE(?, ?, ?, ?, ?) = INTVAL2)", _sql.remove(0));
                }
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.strVal2));

            CriteriaBuilder.Coalesce<Integer> coalesce3 = cb3.coalesce();
            coalesce3.value(cb3.nullLiteral(Integer.class));
            coalesce3.value(cb3.literal(7));
            coalesce3.value(cb3.literal(5));
            coalesce3.value(cb3.literal(9));
            coalesce3.value(cb3.literal(12));
            cquery3.where(cb3.equal(coalesce3, root3.get(QuerySyntaxEntity_.intVal2)));

            query = em.createQuery(cquery3);
            exception = null;
            try {
                query.getResultList();
                Assert.assertEquals(1, _sql.size());
            } catch(Exception e) {
                exception = e;
            } finally {
                if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                    // DB2 z throws `DB2 SQL Error: SQLCODE=-206, SQLSTATE=42703` because a "NULL" value is being passed
                    // Derby throws `ERROR 42X01: Syntax error: Encountered "NULL" at line 1, column 50.`
                    Assert.assertNotNull("Expected query '" + _sql.remove(0) + "' to fail", exception);
                } else {
                    Assert.assertNull(exception);
                    Assert.assertEquals("SELECT STRVAL2 FROM QUERYSYNTAXENTITY WHERE (COALESCE(?, ?, ?, ?, ?) = INTVAL2)", _sql.remove(0));
                }
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
    public void testCoalesce3_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT s.strVal2 FROM QuerySyntaxEntity s WHERE COALESCE(?1, ?2, ?3, ?4, ?5) = s.intVal2");
            query.setParameter(1, 2);
            query.setParameter(2, 7);
            query.setParameter(3, 5);
            query.setParameter(4, 9);
            query.setParameter(5, 12);
            Exception exception = null;
            try {
                query.getResultList();
                Assert.assertEquals(1, _sql2.size());
            } catch(Exception e) {
                exception = e;
            } finally {
                if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                    Assert.assertNull(exception);
                    Assert.assertEquals("SELECT STRVAL2 FROM QUERYSYNTAXENTITY WHERE (COALESCE(?, ?, ?, ?, 12) = INTVAL2)", _sql2.remove(0));
                } else {
                    Assert.assertNull(exception);
                    Assert.assertEquals("SELECT STRVAL2 FROM QUERYSYNTAXENTITY WHERE (COALESCE(?, ?, ?, ?, ?) = INTVAL2)", _sql2.remove(0));
                }
            }

            query = em.createQuery("SELECT s.strVal2 FROM QuerySyntaxEntity s WHERE COALESCE(NULL, 7, 5, 9, 12) = s.intVal2");
            exception = null;
            try {
                query.getResultList();
                Assert.assertEquals(1, _sql2.size());
            } catch(Exception e) {
                exception = e;
            } finally {
                if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                    Assert.assertNull(exception);
                    Assert.assertEquals("SELECT STRVAL2 FROM QUERYSYNTAXENTITY WHERE (COALESCE(?, ?, ?, ?, 12) = INTVAL2)", _sql2.remove(0));
                } else {
                    Assert.assertNull(exception);
                    Assert.assertEquals("SELECT STRVAL2 FROM QUERYSYNTAXENTITY WHERE (COALESCE(?, ?, ?, ?, ?) = INTVAL2)", _sql2.remove(0));
                }
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.strVal2));

            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam3 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam4 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam5 = cb.parameter(Integer.class);
            CriteriaBuilder.Coalesce<Integer> coalesce = cb.coalesce();
            coalesce.value(intParam1);
            coalesce.value(intParam2);
            coalesce.value(intParam3);
            coalesce.value(intParam4);
            coalesce.value(intParam5);
            cquery.where(cb.equal(coalesce, root.get(QuerySyntaxEntity_.intVal2)));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, null);
            query.setParameter(intParam2, 7);
            query.setParameter(intParam3, 5);
            query.setParameter(intParam4, 9);
            query.setParameter(intParam5, 12);
            exception = null;
            try {
                query.getResultList();
                Assert.assertEquals(1, _sql2.size());
            } catch(Exception e) {
                exception = e;
            } finally {
                if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                    Assert.assertNull(exception);
                    Assert.assertEquals("SELECT STRVAL2 FROM QUERYSYNTAXENTITY WHERE (COALESCE(?, ?, ?, ?, 12) = INTVAL2)", _sql2.remove(0));
                } else {
                    Assert.assertNull(exception);
                    Assert.assertEquals("SELECT STRVAL2 FROM QUERYSYNTAXENTITY WHERE (COALESCE(?, ?, ?, ?, ?) = INTVAL2)", _sql2.remove(0));
                }
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.strVal2));

            CriteriaBuilder.Coalesce<Integer> coalesce3 = cb3.coalesce();
            coalesce3.value(cb3.nullLiteral(Integer.class));
            coalesce3.value(cb3.literal(7));
            coalesce3.value(cb3.literal(5));
            coalesce3.value(cb3.literal(9));
            coalesce3.value(cb3.literal(12));
            cquery3.where(cb3.equal(coalesce3, root3.get(QuerySyntaxEntity_.intVal2)));

            query = em.createQuery(cquery3);
            exception = null;
            try {
                query.getResultList();
                Assert.assertEquals(1, _sql2.size());
            } catch(Exception e) {
                exception = e;
            } finally {
                if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                    Assert.assertNull(exception);
                    Assert.assertEquals("SELECT STRVAL2 FROM QUERYSYNTAXENTITY WHERE (COALESCE(?, ?, ?, ?, 12) = INTVAL2)", _sql2.remove(0));
                } else {
                    Assert.assertNull(exception);
                    Assert.assertEquals("SELECT STRVAL2 FROM QUERYSYNTAXENTITY WHERE (COALESCE(?, ?, ?, ?, ?) = INTVAL2)", _sql2.remove(0));
                }
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
    public void testCoalesce3_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("SELECT s.strVal2 FROM QuerySyntaxEntity s WHERE COALESCE(?1, ?2, ?3, ?4, ?5) = s.intVal2");
            query.setParameter(1, 2);
            query.setParameter(2, 7);
            query.setParameter(3, 5);
            query.setParameter(4, 9);
            query.setParameter(5, 12);
            Exception exception = null;
            try {
                query.getResultList();
                Assert.assertEquals(1, _sql3.size());
            } catch(Exception e) {
                exception = e;
            } finally {
                if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                    Assert.assertNull(exception);
                    Assert.assertEquals("SELECT STRVAL2 FROM QUERYSYNTAXENTITY WHERE (COALESCE(?, ?, ?, ?, 12) = INTVAL2)", _sql3.remove(0));
                } else {
                    Assert.assertNull(exception);
                    Assert.assertEquals("SELECT STRVAL2 FROM QUERYSYNTAXENTITY WHERE (COALESCE(?, ?, ?, ?, ?) = INTVAL2)", _sql3.remove(0));
                }
            }

            query = em.createQuery("SELECT s.strVal2 FROM QuerySyntaxEntity s WHERE COALESCE(NULL, 7, 5, 9, 12) = s.intVal2");
            exception = null;
            try {
                query.getResultList();
                Assert.assertEquals(1, _sql3.size());
            } catch(Exception e) {
                exception = e;
            } finally {
                if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                    Assert.assertNull(exception);
                    Assert.assertEquals("SELECT STRVAL2 FROM QUERYSYNTAXENTITY WHERE (COALESCE(?, ?, ?, ?, 12) = INTVAL2)", _sql3.remove(0));
                } else {
                    Assert.assertNull(exception);
                    Assert.assertEquals("SELECT STRVAL2 FROM QUERYSYNTAXENTITY WHERE (COALESCE(?, ?, ?, ?, ?) = INTVAL2)", _sql3.remove(0));
                }
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.strVal2));

            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam3 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam4 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam5 = cb.parameter(Integer.class);
            CriteriaBuilder.Coalesce<Integer> coalesce = cb.coalesce();
            coalesce.value(intParam1);
            coalesce.value(intParam2);
            coalesce.value(intParam3);
            coalesce.value(intParam4);
            coalesce.value(intParam5);
            cquery.where(cb.equal(coalesce, root.get(QuerySyntaxEntity_.intVal2)));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, null);
            query.setParameter(intParam2, 7);
            query.setParameter(intParam3, 5);
            query.setParameter(intParam4, 9);
            query.setParameter(intParam5, 12);
            exception = null;
            try {
                query.getResultList();
                Assert.assertEquals(1, _sql3.size());
            } catch(Exception e) {
                exception = e;
            } finally {
                if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                    Assert.assertNull(exception);
                    Assert.assertEquals("SELECT STRVAL2 FROM QUERYSYNTAXENTITY WHERE (COALESCE(?, ?, ?, ?, 12) = INTVAL2)", _sql3.remove(0));
                } else {
                    Assert.assertNull(exception);
                    Assert.assertEquals("SELECT STRVAL2 FROM QUERYSYNTAXENTITY WHERE (COALESCE(?, ?, ?, ?, ?) = INTVAL2)", _sql3.remove(0));
                }
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.strVal2));

            CriteriaBuilder.Coalesce<Integer> coalesce3 = cb3.coalesce();
            coalesce3.value(cb3.nullLiteral(Integer.class));
            coalesce3.value(cb3.literal(7));
            coalesce3.value(cb3.literal(5));
            coalesce3.value(cb3.literal(9));
            coalesce3.value(cb3.literal(12));
            cquery3.where(cb3.equal(coalesce3, root3.get(QuerySyntaxEntity_.intVal2)));

            query = em.createQuery(cquery3);
            exception = null;
            try {
                query.getResultList();
                Assert.assertEquals(1, _sql3.size());
            } catch(Exception e) {
                exception = e;
            } finally {
                if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                    Assert.assertNull(exception);
                    Assert.assertEquals("SELECT STRVAL2 FROM QUERYSYNTAXENTITY WHERE (COALESCE(?, ?, ?, ?, 12) = INTVAL2)", _sql3.remove(0));
                } else {
                    Assert.assertNull(exception);
                    Assert.assertEquals("SELECT STRVAL2 FROM QUERYSYNTAXENTITY WHERE (COALESCE(?, ?, ?, ?, ?) = INTVAL2)", _sql3.remove(0));
                }
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
    public void testRound1_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT ROUND(?1, ?2) FROM QuerySyntaxEntity s WHERE s.intVal1 = ?3");
            query.setParameter(1, 1.1);
            query.setParameter(2, 3);
            query.setParameter(3, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if (platform.isDerby()) {
                Assert.assertEquals("SELECT FLOOR((1.1)*1e3+0.5)/1e3 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 9)", _sql.remove(0));
            } else if (platform.isPostgreSQL()) {
                Assert.assertEquals("SELECT FLOOR((?)*10^(?)+0.5)/10^(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));
            } else if (platform.isDB2() || platform.isDB2Z()) {
                Assert.assertEquals("SELECT ROUND(1.1, 3) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 9)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT ROUND(?, ?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));
            }

            query = em.createQuery("SELECT ROUND(1.1, 3) FROM QuerySyntaxEntity s WHERE s.intVal1 = 9");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if (platform.isDerby()) {
                Assert.assertEquals("SELECT FLOOR((1.1)*1e3+0.5)/1e3 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 9)", _sql.remove(0));
            } else if (platform.isPostgreSQL()) {
                Assert.assertEquals("SELECT FLOOR((?)*10^(?)+0.5)/10^(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));
            } else if (platform.isDB2() || platform.isDB2Z()) {
                Assert.assertEquals("SELECT ROUND(1.1, 3) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 9)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT ROUND(?, ?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));
            }

            query = em.createQuery("SELECT ROUND(1.1, ?1) FROM QuerySyntaxEntity s WHERE s.intVal1 = 9");
            query.setParameter(1, 3);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if (platform.isDerby()) {
                Assert.assertEquals("SELECT FLOOR((1.1)*1e3+0.5)/1e3 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 9)", _sql.remove(0));
            } else if (platform.isPostgreSQL()) {
                Assert.assertEquals("SELECT FLOOR((?)*10^(?)+0.5)/10^(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));
            } else if (platform.isDB2() || platform.isDB2Z()) {
                Assert.assertEquals("SELECT ROUND(1.1, 3) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 9)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT ROUND(?, ?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object> cquery = cb.createQuery(Object.class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Float> floatParam1 = cb.parameter(Float.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            cquery.select(cb.round(floatParam1, 3));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.intVal1), intParam2));

            query = em.createQuery(cquery);
            query.setParameter(floatParam1, 1.1f);
            query.setParameter(intParam2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if (platform.isDerby()) {
                Assert.assertEquals("SELECT FLOOR((1.1)*1e3+0.5)/1e3 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 9)", _sql.remove(0));
            } else if (platform.isPostgreSQL()) {
                Assert.assertEquals("SELECT FLOOR((?)*10^(?)+0.5)/10^(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));
            } else if (platform.isDB2() || platform.isDB2Z()) {
                Assert.assertEquals("SELECT ROUND(1.1, 3) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 9)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT ROUND(?, ?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object> cquery2 = cb2.createQuery(Object.class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.select(cb2.round(cb2.literal(1.1f), 3));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), cb2.literal(9)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if (platform.isDerby()) {
                Assert.assertEquals("SELECT FLOOR((1.1)*1e3+0.5)/1e3 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 9)", _sql.remove(0));
            } else if (platform.isPostgreSQL()) {
                Assert.assertEquals("SELECT FLOOR((?)*10^(?)+0.5)/10^(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));
            } else if (platform.isDB2() || platform.isDB2Z()) {
                Assert.assertEquals("SELECT ROUND(1.1, 3) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 9)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT ROUND(?, ?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object> cquery3 = cb3.createQuery(Object.class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            ParameterExpression<Float> floatParam2 = cb.parameter(Float.class);
            cquery3.select(cb3.round(floatParam2, 3));
            cquery3.where(cb3.equal(root3.get(QuerySyntaxEntity_.intVal1), cb3.literal(9)));

            query = em.createQuery(cquery3);
            query.setParameter(floatParam2, 1.1f);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if (platform.isDerby()) {
                Assert.assertEquals("SELECT FLOOR((1.1)*1e3+0.5)/1e3 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 9)", _sql.remove(0));
            } else if (platform.isPostgreSQL()) {
                Assert.assertEquals("SELECT FLOOR((?)*10^(?)+0.5)/10^(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));
            } else if (platform.isDB2() || platform.isDB2Z()) {
                Assert.assertEquals("SELECT ROUND(1.1, 3) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 9)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT ROUND(?, ?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));
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
    public void testRound1_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT ROUND(?1, ?2) FROM QuerySyntaxEntity s WHERE s.intVal1 = ?3");
            query.setParameter(1, 1.1);
            query.setParameter(2, 3);
            query.setParameter(3, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if (platform.isDerby()) {
                Assert.assertEquals("SELECT FLOOR((1.1)*1e3+0.5)/1e3 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            } else if (platform.isPostgreSQL()) {
                Assert.assertEquals("SELECT FLOOR((?)*10^(?)+0.5)/10^(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));
            } else if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT ROUND(1.1, 3) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            } else if (platform.isDB2()) {
                Assert.assertEquals("SELECT ROUND(?, 3) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT ROUND(?, ?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            }

            query = em.createQuery("SELECT ROUND(1.1, 3) FROM QuerySyntaxEntity s WHERE s.intVal1 = 9");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if (platform.isDerby()) {
                Assert.assertEquals("SELECT FLOOR((1.1)*1e3+0.5)/1e3 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            } else if (platform.isPostgreSQL()) {
                Assert.assertEquals("SELECT FLOOR((?)*10^(?)+0.5)/10^(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));
            } else if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT ROUND(1.1, 3) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            } else if (platform.isDB2()) {
                Assert.assertEquals("SELECT ROUND(?, 3) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT ROUND(?, ?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            }

            query = em.createQuery("SELECT ROUND(1.1, ?1) FROM QuerySyntaxEntity s WHERE s.intVal1 = 9");
            query.setParameter(1, 3);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if (platform.isDerby()) {
                Assert.assertEquals("SELECT FLOOR((1.1)*1e3+0.5)/1e3 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            } else if (platform.isPostgreSQL()) {
                Assert.assertEquals("SELECT FLOOR((?)*10^(?)+0.5)/10^(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));
            } else if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT ROUND(1.1, 3) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            } else if (platform.isDB2()) {
                Assert.assertEquals("SELECT ROUND(?, 3) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT ROUND(?, ?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object> cquery = cb.createQuery(Object.class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Float> floatParam1 = cb.parameter(Float.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            cquery.select(cb.round(floatParam1, 3));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.intVal1), intParam2));

            query = em.createQuery(cquery);
            query.setParameter(floatParam1, 1.1f);
            query.setParameter(intParam2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if (platform.isDerby()) {
                Assert.assertEquals("SELECT FLOOR((1.1)*1e3+0.5)/1e3 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            } else if (platform.isPostgreSQL()) {
                Assert.assertEquals("SELECT FLOOR((?)*10^(?)+0.5)/10^(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));
            } else if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT ROUND(1.1, 3) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            } else if (platform.isDB2()) {
                Assert.assertEquals("SELECT ROUND(?, 3) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT ROUND(?, ?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object> cquery2 = cb2.createQuery(Object.class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.select(cb2.round(cb2.literal(1.1f), 3));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), cb2.literal(9)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if (platform.isDerby()) {
                Assert.assertEquals("SELECT FLOOR((1.1)*1e3+0.5)/1e3 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            } else if (platform.isPostgreSQL()) {
                Assert.assertEquals("SELECT FLOOR((?)*10^(?)+0.5)/10^(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));
            } else if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT ROUND(1.1, 3) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            } else if (platform.isDB2()) {
                Assert.assertEquals("SELECT ROUND(?, 3) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT ROUND(?, ?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object> cquery3 = cb3.createQuery(Object.class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            ParameterExpression<Float> floatParam2 = cb.parameter(Float.class);
            cquery3.select(cb3.round(floatParam2, 3));
            cquery3.where(cb3.equal(root3.get(QuerySyntaxEntity_.intVal1), cb3.literal(9)));

            query = em.createQuery(cquery3);
            query.setParameter(floatParam2, 1.1f);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if (platform.isDerby()) {
                Assert.assertEquals("SELECT FLOOR((1.1)*1e3+0.5)/1e3 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            } else if (platform.isPostgreSQL()) {
                Assert.assertEquals("SELECT FLOOR((?)*10^(?)+0.5)/10^(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));
            } else if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT ROUND(1.1, 3) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            } else if (platform.isDB2()) {
                Assert.assertEquals("SELECT ROUND(?, 3) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT ROUND(?, ?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
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
    public void testRound1_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("SELECT ROUND(?1, ?2) FROM QuerySyntaxEntity s WHERE s.intVal1 = ?3");
            query.setParameter(1, 1.1);
            query.setParameter(2, 3);
            query.setParameter(3, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if (platform.isDerby()) {
                Assert.assertEquals("SELECT FLOOR((1.1)*1e3+0.5)/1e3 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            } else if (platform.isPostgreSQL()) {
                Assert.assertEquals("SELECT FLOOR((?)*10^(?)+0.5)/10^(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));
            } else if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT ROUND(1.1, 3) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            } else if (platform.isDB2()) {
                Assert.assertEquals("SELECT ROUND(?, 3) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT ROUND(?, ?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            }

            query = em.createQuery("SELECT ROUND(1.1, 3) FROM QuerySyntaxEntity s WHERE s.intVal1 = 9");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if (platform.isDerby()) {
                Assert.assertEquals("SELECT FLOOR((1.1)*1e3+0.5)/1e3 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            } else if (platform.isPostgreSQL()) {
                Assert.assertEquals("SELECT FLOOR((?)*10^(?)+0.5)/10^(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));
            } else if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT ROUND(1.1, 3) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            } else if (platform.isDB2()) {
                Assert.assertEquals("SELECT ROUND(?, 3) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT ROUND(?, ?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            }

            query = em.createQuery("SELECT ROUND(1.1, ?1) FROM QuerySyntaxEntity s WHERE s.intVal1 = 9");
            query.setParameter(1, 3);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if (platform.isDerby()) {
                Assert.assertEquals("SELECT FLOOR((1.1)*1e3+0.5)/1e3 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            } else if (platform.isPostgreSQL()) {
                Assert.assertEquals("SELECT FLOOR((?)*10^(?)+0.5)/10^(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));
            } else if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT ROUND(1.1, 3) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            } else if (platform.isDB2()) {
                Assert.assertEquals("SELECT ROUND(?, 3) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT ROUND(?, ?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            }
            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object> cquery = cb.createQuery(Object.class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Float> floatParam1 = cb.parameter(Float.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            cquery.select(cb.round(floatParam1, 3));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.intVal1), intParam2));

            query = em.createQuery(cquery);
            query.setParameter(floatParam1, 1.1f);
            query.setParameter(intParam2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if (platform.isDerby()) {
                Assert.assertEquals("SELECT FLOOR((1.1)*1e3+0.5)/1e3 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            } else if (platform.isPostgreSQL()) {
                Assert.assertEquals("SELECT FLOOR((?)*10^(?)+0.5)/10^(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));
            } else if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT ROUND(1.1, 3) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            } else if (platform.isDB2()) {
                Assert.assertEquals("SELECT ROUND(?, 3) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT ROUND(?, ?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object> cquery2 = cb2.createQuery(Object.class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.select(cb2.round(cb2.literal(1.1f), 3));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), cb2.literal(9)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if (platform.isDerby()) {
                Assert.assertEquals("SELECT FLOOR((1.1)*1e3+0.5)/1e3 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            } else if (platform.isPostgreSQL()) {
                Assert.assertEquals("SELECT FLOOR((?)*10^(?)+0.5)/10^(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));
            } else if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT ROUND(1.1, 3) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            } else if (platform.isDB2()) {
                Assert.assertEquals("SELECT ROUND(?, 3) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT ROUND(?, ?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object> cquery3 = cb3.createQuery(Object.class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            ParameterExpression<Float> floatParam2 = cb.parameter(Float.class);
            cquery3.select(cb3.round(floatParam2, 3));
            cquery3.where(cb3.equal(root3.get(QuerySyntaxEntity_.intVal1), cb3.literal(9)));

            query = em.createQuery(cquery3);
            query.setParameter(floatParam2, 1.1f);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if (platform.isDerby()) {
                Assert.assertEquals("SELECT FLOOR((1.1)*1e3+0.5)/1e3 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            } else if (platform.isPostgreSQL()) {
                Assert.assertEquals("SELECT FLOOR((?)*10^(?)+0.5)/10^(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));
            } else if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT ROUND(1.1, 3) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            } else if (platform.isDB2()) {
                Assert.assertEquals("SELECT ROUND(?, 3) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT ROUND(?, ?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
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
    public void testCeiling1_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT CEILING(?1) FROM QuerySyntaxEntity s WHERE s.intVal1 = ?2");
            query.setParameter(1, 1.1);
            query.setParameter(2, 3);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if (platform.isDB2() || platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT CEIL(1.1) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 3)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT CEIL(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));
            }

            query = em.createQuery("SELECT CEILING(1.1) FROM QuerySyntaxEntity s WHERE s.intVal1 = 3");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if (platform.isDB2() || platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT CEIL(1.1) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 3)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT CEIL(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object> cquery = cb.createQuery(Object.class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Float> floatParam1 = cb.parameter(Float.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            cquery.select(cb.ceiling(floatParam1));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.intVal1), intParam2));

            query = em.createQuery(cquery);
            query.setParameter(floatParam1, 1.1f);
            query.setParameter(intParam2, 3);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if (platform.isDB2() || platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT CEIL(1.1) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 3)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT CEIL(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object> cquery2 = cb2.createQuery(Object.class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.select(cb2.ceiling(cb2.literal(1.1f)));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), cb2.literal(3)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if (platform.isDB2() || platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT CEIL(1.1) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 3)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT CEIL(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));
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
    public void testCeiling1_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT CEILING(?1) FROM QuerySyntaxEntity s WHERE s.intVal1 = ?2");
            query.setParameter(1, 1.1);
            query.setParameter(2, 3);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT CEIL(1.1) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT CEIL(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            }

            query = em.createQuery("SELECT CEILING(1.1) FROM QuerySyntaxEntity s WHERE s.intVal1 = 3");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT CEIL(1.1) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT CEIL(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object> cquery = cb.createQuery(Object.class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Float> floatParam1 = cb.parameter(Float.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            cquery.select(cb.ceiling(floatParam1));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.intVal1), intParam2));

            query = em.createQuery(cquery);
            query.setParameter(floatParam1, 1.1f);
            query.setParameter(intParam2, 3);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT CEIL(1.1) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT CEIL(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object> cquery2 = cb2.createQuery(Object.class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.select(cb2.ceiling(cb2.literal(1.1f)));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), cb2.literal(3)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT CEIL(1.1) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT CEIL(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
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
    public void testCeiling1_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("SELECT CEILING(?1) FROM QuerySyntaxEntity s WHERE s.intVal1 = ?2");
            query.setParameter(1, 1.1);
            query.setParameter(2, 3);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT CEIL(1.1) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT CEIL(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            }

            query = em.createQuery("SELECT CEILING(1.1) FROM QuerySyntaxEntity s WHERE s.intVal1 = 3");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT CEIL(1.1) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT CEIL(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object> cquery = cb.createQuery(Object.class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Float> floatParam1 = cb.parameter(Float.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            cquery.select(cb.ceiling(floatParam1));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.intVal1), intParam2));

            query = em.createQuery(cquery);
            query.setParameter(floatParam1, 1.1f);
            query.setParameter(intParam2, 3);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT CEIL(1.1) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT CEIL(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object> cquery2 = cb2.createQuery(Object.class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.select(cb2.ceiling(cb2.literal(1.1f)));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), cb2.literal(3)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT CEIL(1.1) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT CEIL(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
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
    public void testFloor1_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT FLOOR(?1) FROM QuerySyntaxEntity s WHERE s.intVal1 = ?2");
            query.setParameter(1, 1.1);
            query.setParameter(2, 3);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if (platform.isDB2() || platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT FLOOR(1.1) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 3)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT FLOOR(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));
            }

            query = em.createQuery("SELECT FLOOR(1.1) FROM QuerySyntaxEntity s WHERE s.intVal1 = 3");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if (platform.isDB2() || platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT FLOOR(1.1) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 3)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT FLOOR(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object> cquery = cb.createQuery(Object.class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Float> floatParam1 = cb.parameter(Float.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            cquery.select(cb.floor(floatParam1));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.intVal1), intParam2));

            query = em.createQuery(cquery);
            query.setParameter(floatParam1, 1.1f);
            query.setParameter(intParam2, 3);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if (platform.isDB2() || platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT FLOOR(1.1) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 3)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT FLOOR(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object> cquery2 = cb2.createQuery(Object.class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.select(cb2.floor(cb2.literal(1.1f)));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), cb2.literal(3)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if (platform.isDB2() || platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT FLOOR(1.1) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 3)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT FLOOR(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));
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
    public void testFloor1_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT FLOOR(?1) FROM QuerySyntaxEntity s WHERE s.intVal1 = ?2");
            query.setParameter(1, 1.1);
            query.setParameter(2, 3);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT FLOOR(1.1) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT FLOOR(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            }

            query = em.createQuery("SELECT FLOOR(1.1) FROM QuerySyntaxEntity s WHERE s.intVal1 = 3");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT FLOOR(1.1) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT FLOOR(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object> cquery = cb.createQuery(Object.class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Float> floatParam1 = cb.parameter(Float.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            cquery.select(cb.floor(floatParam1));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.intVal1), intParam2));

            query = em.createQuery(cquery);
            query.setParameter(floatParam1, 1.1f);
            query.setParameter(intParam2, 3);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT FLOOR(1.1) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT FLOOR(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object> cquery2 = cb2.createQuery(Object.class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.select(cb2.floor(cb2.literal(1.1f)));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), cb2.literal(3)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT FLOOR(1.1) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT FLOOR(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
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
    public void testFloor1_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("SELECT FLOOR(?1) FROM QuerySyntaxEntity s WHERE s.intVal1 = ?2");
            query.setParameter(1, 1.1);
            query.setParameter(2, 3);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT FLOOR(1.1) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT FLOOR(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            }

            query = em.createQuery("SELECT FLOOR(1.1) FROM QuerySyntaxEntity s WHERE s.intVal1 = 3");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT FLOOR(1.1) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT FLOOR(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object> cquery = cb.createQuery(Object.class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Float> floatParam1 = cb.parameter(Float.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            cquery.select(cb.floor(floatParam1));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.intVal1), intParam2));

            query = em.createQuery(cquery);
            query.setParameter(floatParam1, 1.1f);
            query.setParameter(intParam2, 3);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT FLOOR(1.1) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT FLOOR(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object> cquery2 = cb2.createQuery(Object.class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.select(cb2.floor(cb2.literal(1.1f)));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), cb2.literal(3)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT FLOOR(1.1) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT FLOOR(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
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
    public void testExp1_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT EXP(?1) FROM QuerySyntaxEntity s WHERE s.intVal1 = ?2");
            query.setParameter(1, 1.1);
            query.setParameter(2, 3);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if (platform.isDB2() || platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT EXP(1.1) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 3)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT EXP(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));
            }

            query = em.createQuery("SELECT EXP(1.1) FROM QuerySyntaxEntity s WHERE s.intVal1 = 3");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if (platform.isDB2() || platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT EXP(1.1) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 3)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT EXP(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object> cquery = cb.createQuery(Object.class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Float> floatParam1 = cb.parameter(Float.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            cquery.select(cb.exp(floatParam1));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.intVal1), intParam2));

            query = em.createQuery(cquery);
            query.setParameter(floatParam1, 1.1f);
            query.setParameter(intParam2, 3);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if (platform.isDB2() || platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT EXP(1.1) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 3)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT EXP(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object> cquery2 = cb2.createQuery(Object.class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.select(cb2.exp(cb2.literal(1.1f)));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), cb2.literal(3)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if (platform.isDB2() || platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT EXP(1.1) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 3)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT EXP(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));
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
    public void testExp1_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT EXP(?1) FROM QuerySyntaxEntity s WHERE s.intVal1 = ?2");
            query.setParameter(1, 1.1);
            query.setParameter(2, 3);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT EXP(1.1) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT EXP(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            }

            query = em.createQuery("SELECT EXP(1.1) FROM QuerySyntaxEntity s WHERE s.intVal1 = 3");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT EXP(1.1) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT EXP(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object> cquery = cb.createQuery(Object.class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Float> floatParam1 = cb.parameter(Float.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            cquery.select(cb.exp(floatParam1));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.intVal1), intParam2));

            query = em.createQuery(cquery);
            query.setParameter(floatParam1, 1.1f);
            query.setParameter(intParam2, 3);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT EXP(1.1) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT EXP(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object> cquery2 = cb2.createQuery(Object.class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.select(cb2.exp(cb2.literal(1.1f)));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), cb2.literal(3)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT EXP(1.1) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT EXP(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
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
    public void testExp1_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("SELECT EXP(?1) FROM QuerySyntaxEntity s WHERE s.intVal1 = ?2");
            query.setParameter(1, 1.1);
            query.setParameter(2, 3);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT EXP(1.1) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT EXP(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            }

            query = em.createQuery("SELECT EXP(1.1) FROM QuerySyntaxEntity s WHERE s.intVal1 = 3");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT EXP(1.1) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT EXP(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object> cquery = cb.createQuery(Object.class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Float> floatParam1 = cb.parameter(Float.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            cquery.select(cb.exp(floatParam1));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.intVal1), intParam2));

            query = em.createQuery(cquery);
            query.setParameter(floatParam1, 1.1f);
            query.setParameter(intParam2, 3);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT EXP(1.1) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT EXP(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object> cquery2 = cb2.createQuery(Object.class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.select(cb2.exp(cb2.literal(1.1f)));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), cb2.literal(3)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT EXP(1.1) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT EXP(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
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
    public void testLn1_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT LN(?1) FROM QuerySyntaxEntity s WHERE s.intVal1 = ?2");
            query.setParameter(1, 1.1);
            query.setParameter(2, 3);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if (platform.isDB2() || platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT LN(1.1) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 3)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT LN(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));
            }

            query = em.createQuery("SELECT LN(1.1) FROM QuerySyntaxEntity s WHERE s.intVal1 = 3");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if (platform.isDB2() || platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT LN(1.1) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 3)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT LN(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object> cquery = cb.createQuery(Object.class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Float> floatParam1 = cb.parameter(Float.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            cquery.select(cb.ln(floatParam1));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.intVal1), intParam2));

            query = em.createQuery(cquery);
            query.setParameter(floatParam1, 1.1f);
            query.setParameter(intParam2, 3);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if (platform.isDB2() || platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT LN(1.1) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 3)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT LN(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object> cquery2 = cb2.createQuery(Object.class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.select(cb2.ln(cb2.literal(1.1f)));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), cb2.literal(3)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if (platform.isDB2() || platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT LN(1.1) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 3)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT LN(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));
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
    public void testLn1_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT LN(?1) FROM QuerySyntaxEntity s WHERE s.intVal1 = ?2");
            query.setParameter(1, 1.1);
            query.setParameter(2, 3);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT LN(1.1) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT LN(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            }

            query = em.createQuery("SELECT LN(1.1) FROM QuerySyntaxEntity s WHERE s.intVal1 = 3");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT LN(1.1) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT LN(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object> cquery = cb.createQuery(Object.class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Float> floatParam1 = cb.parameter(Float.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            cquery.select(cb.ln(floatParam1));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.intVal1), intParam2));

            query = em.createQuery(cquery);
            query.setParameter(floatParam1, 1.1f);
            query.setParameter(intParam2, 3);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT LN(1.1) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT LN(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object> cquery2 = cb2.createQuery(Object.class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.select(cb2.ln(cb2.literal(1.1f)));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), cb2.literal(3)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT LN(1.1) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT LN(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
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
    public void testLn1_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("SELECT LN(?1) FROM QuerySyntaxEntity s WHERE s.intVal1 = ?2");
            query.setParameter(1, 1.1);
            query.setParameter(2, 3);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT LN(1.1) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT LN(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            }

            query = em.createQuery("SELECT LN(1.1) FROM QuerySyntaxEntity s WHERE s.intVal1 = 3");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT LN(1.1) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT LN(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object> cquery = cb.createQuery(Object.class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Float> floatParam1 = cb.parameter(Float.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            cquery.select(cb.ln(floatParam1));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.intVal1), intParam2));

            query = em.createQuery(cquery);
            query.setParameter(floatParam1, 1.1f);
            query.setParameter(intParam2, 3);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT LN(1.1) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT LN(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object> cquery2 = cb2.createQuery(Object.class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.select(cb2.ln(cb2.literal(1.1f)));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), cb2.literal(3)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT LN(1.1) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT LN(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
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
    public void testSign1_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT SIGN(?1) FROM QuerySyntaxEntity s WHERE s.intVal1 = ?2");
            query.setParameter(1, 1.1);
            query.setParameter(2, 3);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if (platform.isDB2() || platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT SIGN(1.1) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 3)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT SIGN(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));
            }

            query = em.createQuery("SELECT SIGN(1.1) FROM QuerySyntaxEntity s WHERE s.intVal1 = 3");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if (platform.isDB2() || platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT SIGN(1.1) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 3)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT SIGN(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object> cquery = cb.createQuery(Object.class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Float> floatParam1 = cb.parameter(Float.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            cquery.select(cb.sign(floatParam1));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.intVal1), intParam2));

            query = em.createQuery(cquery);
            query.setParameter(floatParam1, 1.1f);
            query.setParameter(intParam2, 3);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if (platform.isDB2() || platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT SIGN(1.1) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 3)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT SIGN(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object> cquery2 = cb2.createQuery(Object.class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.select(cb2.sign(cb2.literal(1.1f)));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), cb2.literal(3)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if (platform.isDB2() || platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT SIGN(1.1) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 3)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT SIGN(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));
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
    public void testSign1_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT SIGN(?1) FROM QuerySyntaxEntity s WHERE s.intVal1 = ?2");
            query.setParameter(1, 1.1);
            query.setParameter(2, 3);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT SIGN(1.1) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT SIGN(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            }

            query = em.createQuery("SELECT SIGN(1.1) FROM QuerySyntaxEntity s WHERE s.intVal1 = 3");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT SIGN(1.1) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT SIGN(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object> cquery = cb.createQuery(Object.class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Float> floatParam1 = cb.parameter(Float.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            cquery.select(cb.sign(floatParam1));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.intVal1), intParam2));

            query = em.createQuery(cquery);
            query.setParameter(floatParam1, 1.1f);
            query.setParameter(intParam2, 3);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT SIGN(1.1) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT SIGN(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object> cquery2 = cb2.createQuery(Object.class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.select(cb2.sign(cb2.literal(1.1f)));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), cb2.literal(3)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT SIGN(1.1) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT SIGN(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
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
    public void testSign1_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("SELECT SIGN(?1) FROM QuerySyntaxEntity s WHERE s.intVal1 = ?2");
            query.setParameter(1, 1.1);
            query.setParameter(2, 3);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT SIGN(1.1) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT SIGN(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            }

            query = em.createQuery("SELECT SIGN(1.1) FROM QuerySyntaxEntity s WHERE s.intVal1 = 3");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT SIGN(1.1) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT SIGN(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object> cquery = cb.createQuery(Object.class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Float> floatParam1 = cb.parameter(Float.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            cquery.select(cb.sign(floatParam1));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.intVal1), intParam2));

            query = em.createQuery(cquery);
            query.setParameter(floatParam1, 1.1f);
            query.setParameter(intParam2, 3);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT SIGN(1.1) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT SIGN(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object> cquery2 = cb2.createQuery(Object.class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.select(cb2.sign(cb2.literal(1.1f)));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), cb2.literal(3)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT SIGN(1.1) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT SIGN(?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
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
    public void testRound2_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT ?1 FROM QuerySyntaxEntity s WHERE s.intVal1 = ROUND(?2, ?3)");
            query.setParameter(1, 1);
            query.setParameter(2, 3.3);
            query.setParameter(3, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if (platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = FLOOR((3.3)*1e9+0.5)/1e9)", _sql.remove(0));
            } else if (platform.isPostgreSQL()) {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = FLOOR((?)*10^(?)+0.5)/10^(?))", _sql.remove(0));
            } else if (platform.isDB2() || platform.isDB2Z()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ROUND(3.3, 9))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ROUND(?, ?))", _sql.remove(0));
            }

            query = em.createQuery("SELECT 1 FROM QuerySyntaxEntity s WHERE s.intVal1 = ROUND(3.3, 9)");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if (platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = FLOOR((3.3)*1e9+0.5)/1e9)", _sql.remove(0));
            } else if (platform.isPostgreSQL()) {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = FLOOR((?)*10^(?)+0.5)/10^(?))", _sql.remove(0));
            } else if (platform.isDB2() || platform.isDB2Z()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ROUND(3.3, 9))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ROUND(?, ?))", _sql.remove(0));
            }

            query = em.createQuery("SELECT 1 FROM QuerySyntaxEntity s WHERE s.intVal1 = ROUND(?1, 9)");
            query.setParameter(1, 3.3);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if (platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = FLOOR((3.3)*1e9+0.5)/1e9)", _sql.remove(0));
            } else if (platform.isPostgreSQL()) {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = FLOOR((?)*10^(?)+0.5)/10^(?))", _sql.remove(0));
            } else if (platform.isDB2() || platform.isDB2Z()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ROUND(3.3, 9))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ROUND(?, ?))", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object> cquery = cb.createQuery(Object.class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Float> floatParam1 = cb.parameter(Float.class);
            cquery.select(intParam1);
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.intVal1), cb.round(floatParam1, 9)));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 1);
            query.setParameter(floatParam1, 3.3f);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if (platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = FLOOR((3.3)*1e9+0.5)/1e9)", _sql.remove(0));
            } else if (platform.isPostgreSQL()) {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = FLOOR((?)*10^(?)+0.5)/10^(?))", _sql.remove(0));
            } else if (platform.isDB2() || platform.isDB2Z()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ROUND(3.3, 9))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ROUND(?, ?))", _sql.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object> cquery2 = cb2.createQuery(Object.class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.select(cb2.literal(1));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), cb2.round(cb2.literal(3.3f), 9)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if (platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = FLOOR((3.3)*1e9+0.5)/1e9)", _sql.remove(0));
            } else if (platform.isPostgreSQL()) {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = FLOOR((?)*10^(?)+0.5)/10^(?))", _sql.remove(0));
            } else if (platform.isDB2() || platform.isDB2Z()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ROUND(3.3, 9))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ROUND(?, ?))", _sql.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object> cquery3 = cb3.createQuery(Object.class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            ParameterExpression<Float> floatParam2 = cb3.parameter(Float.class);
            cquery3.select(cb3.literal(1));
            cquery3.where(cb3.equal(root3.get(QuerySyntaxEntity_.intVal1), cb3.round(floatParam2, 9)));

            query = em.createQuery(cquery3);
            query.setParameter(floatParam2, 3.3f);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if (platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = FLOOR((3.3)*1e9+0.5)/1e9)", _sql.remove(0));
            } else if (platform.isPostgreSQL()) {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = FLOOR((?)*10^(?)+0.5)/10^(?))", _sql.remove(0));
            } else if (platform.isDB2() || platform.isDB2Z()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ROUND(3.3, 9))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ROUND(?, ?))", _sql.remove(0));
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
    public void testRound2_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT ?1 FROM QuerySyntaxEntity s WHERE s.intVal1 = ROUND(?2, ?3)");
            query.setParameter(1, 1);
            query.setParameter(2, 3.3);
            query.setParameter(3, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if (platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = FLOOR((3.3)*1e9+0.5)/1e9)", _sql2.remove(0));
            } else if (platform.isPostgreSQL()) {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = FLOOR((?)*10^(?)+0.5)/10^(?))", _sql2.remove(0));
            } else if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ROUND(3.3, 9))", _sql2.remove(0));
            } else if (platform.isDB2()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ROUND(?, 9))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ROUND(?, ?))", _sql2.remove(0));
            }

            query = em.createQuery("SELECT 1 FROM QuerySyntaxEntity s WHERE s.intVal1 = ROUND(3.3, 9)");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if (platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = FLOOR((3.3)*1e9+0.5)/1e9)", _sql2.remove(0));
            } else if (platform.isPostgreSQL()) {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = FLOOR((?)*10^(?)+0.5)/10^(?))", _sql2.remove(0));
            } else if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ROUND(3.3, 9))", _sql2.remove(0));
            } else if (platform.isDB2()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ROUND(?, 9))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ROUND(?, ?))", _sql2.remove(0));
            }

            query = em.createQuery("SELECT 1 FROM QuerySyntaxEntity s WHERE s.intVal1 = ROUND(?1, 9)");
            query.setParameter(1, 3.3);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if (platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = FLOOR((3.3)*1e9+0.5)/1e9)", _sql2.remove(0));
            } else if (platform.isPostgreSQL()) {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = FLOOR((?)*10^(?)+0.5)/10^(?))", _sql2.remove(0));
            } else if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ROUND(3.3, 9))", _sql2.remove(0));
            } else if (platform.isDB2()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ROUND(?, 9))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ROUND(?, ?))", _sql2.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object> cquery = cb.createQuery(Object.class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Float> floatParam1 = cb.parameter(Float.class);
            cquery.select(intParam1);
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.intVal1), cb.round(floatParam1, 9)));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 1);
            query.setParameter(floatParam1, 3.3f);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if (platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = FLOOR((3.3)*1e9+0.5)/1e9)", _sql2.remove(0));
            } else if (platform.isPostgreSQL()) {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = FLOOR((?)*10^(?)+0.5)/10^(?))", _sql2.remove(0));
            } else if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ROUND(3.3, 9))", _sql2.remove(0));
            } else if (platform.isDB2()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ROUND(?, 9))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ROUND(?, ?))", _sql2.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object> cquery2 = cb2.createQuery(Object.class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.select(cb2.literal(1));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), cb2.round(cb2.literal(3.3f), 9)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if (platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = FLOOR((3.3)*1e9+0.5)/1e9)", _sql2.remove(0));
            } else if (platform.isPostgreSQL()) {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = FLOOR((?)*10^(?)+0.5)/10^(?))", _sql2.remove(0));
            } else if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ROUND(3.3, 9))", _sql2.remove(0));
            } else if (platform.isDB2()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ROUND(?, 9))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ROUND(?, ?))", _sql2.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object> cquery3 = cb3.createQuery(Object.class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            ParameterExpression<Float> floatParam2 = cb3.parameter(Float.class);
            cquery3.select(cb3.literal(1));
            cquery3.where(cb3.equal(root3.get(QuerySyntaxEntity_.intVal1), cb3.round(floatParam2, 9)));

            query = em.createQuery(cquery3);
            query.setParameter(floatParam2, 3.3f);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if (platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = FLOOR((3.3)*1e9+0.5)/1e9)", _sql2.remove(0));
            } else if (platform.isPostgreSQL()) {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = FLOOR((?)*10^(?)+0.5)/10^(?))", _sql2.remove(0));
            } else if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ROUND(3.3, 9))", _sql2.remove(0));
            } else if (platform.isDB2()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ROUND(?, 9))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ROUND(?, ?))", _sql2.remove(0));
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
    public void testRound2_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("SELECT ?1 FROM QuerySyntaxEntity s WHERE s.intVal1 = ROUND(?2, ?3)");
            query.setParameter(1, 1);
            query.setParameter(2, 3.3);
            query.setParameter(3, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if (platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = FLOOR((3.3)*1e9+0.5)/1e9)", _sql3.remove(0));
            } else if (platform.isPostgreSQL()) {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = FLOOR((?)*10^(?)+0.5)/10^(?))", _sql3.remove(0));
            } else if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ROUND(3.3, 9))", _sql3.remove(0));
            } else if (platform.isDB2()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ROUND(?, 9))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ROUND(?, ?))", _sql3.remove(0));
            }

            query = em.createQuery("SELECT 1 FROM QuerySyntaxEntity s WHERE s.intVal1 = ROUND(3.3, 9)");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if (platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = FLOOR((3.3)*1e9+0.5)/1e9)", _sql3.remove(0));
            } else if (platform.isPostgreSQL()) {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = FLOOR((?)*10^(?)+0.5)/10^(?))", _sql3.remove(0));
            } else if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ROUND(3.3, 9))", _sql3.remove(0));
            } else if (platform.isDB2()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ROUND(?, 9))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ROUND(?, ?))", _sql3.remove(0));
            }

            query = em.createQuery("SELECT 1 FROM QuerySyntaxEntity s WHERE s.intVal1 = ROUND(?1, 9)");
            query.setParameter(1, 3.3);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if (platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = FLOOR((3.3)*1e9+0.5)/1e9)", _sql3.remove(0));
            } else if (platform.isPostgreSQL()) {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = FLOOR((?)*10^(?)+0.5)/10^(?))", _sql3.remove(0));
            } else if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ROUND(3.3, 9))", _sql3.remove(0));
            } else if (platform.isDB2()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ROUND(?, 9))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ROUND(?, ?))", _sql3.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object> cquery = cb.createQuery(Object.class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Float> floatParam1 = cb.parameter(Float.class);
            cquery.select(intParam1);
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.intVal1), cb.round(floatParam1, 9)));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 1);
            query.setParameter(floatParam1, 3.3f);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if (platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = FLOOR((3.3)*1e9+0.5)/1e9)", _sql3.remove(0));
            } else if (platform.isPostgreSQL()) {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = FLOOR((?)*10^(?)+0.5)/10^(?))", _sql3.remove(0));
            } else if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ROUND(3.3, 9))", _sql3.remove(0));
            } else if (platform.isDB2()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ROUND(?, 9))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ROUND(?, ?))", _sql3.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object> cquery2 = cb2.createQuery(Object.class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.select(cb2.literal(1));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), cb2.round(cb2.literal(3.3f), 9)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if (platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = FLOOR((3.3)*1e9+0.5)/1e9)", _sql3.remove(0));
            } else if (platform.isPostgreSQL()) {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = FLOOR((?)*10^(?)+0.5)/10^(?))", _sql3.remove(0));
            } else if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ROUND(3.3, 9))", _sql3.remove(0));
            } else if (platform.isDB2()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ROUND(?, 9))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ROUND(?, ?))", _sql3.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object> cquery3 = cb3.createQuery(Object.class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            ParameterExpression<Float> floatParam2 = cb3.parameter(Float.class);
            cquery3.select(cb3.literal(1));
            cquery3.where(cb3.equal(root3.get(QuerySyntaxEntity_.intVal1), cb3.round(floatParam2, 9)));

            query = em.createQuery(cquery3);
            query.setParameter(floatParam2, 3.3f);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if (platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = FLOOR((3.3)*1e9+0.5)/1e9)", _sql3.remove(0));
            } else if (platform.isPostgreSQL()) {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = FLOOR((?)*10^(?)+0.5)/10^(?))", _sql3.remove(0));
            } else if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ROUND(3.3, 9))", _sql3.remove(0));
            } else if (platform.isDB2()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ROUND(?, 9))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ROUND(?, ?))", _sql3.remove(0));
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
    public void testRound3_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT ?1 FROM QuerySyntaxEntity s HAVING ?2 < ROUND(?3, ?4)");
            query.setParameter(1, 1);
            query.setParameter(2, 0);
            query.setParameter(3, 9.9);
            query.setParameter(4, 2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if (platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY HAVING (0 < FLOOR((9.9)*1e2+0.5)/1e2)", _sql.remove(0));
            } else if (platform.isPostgreSQL()) {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY HAVING (? < FLOOR((?)*10^(?)+0.5)/10^(?))", _sql.remove(0));
            } else if (platform.isDB2() || platform.isDB2Z()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY HAVING (0 < ROUND(9.9, 2))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY HAVING (? < ROUND(?, ?))", _sql.remove(0));
            }

            query = em.createQuery("SELECT 1 FROM QuerySyntaxEntity s HAVING 0 < ROUND(9.9, 2)");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if (platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY HAVING (0 < FLOOR((9.9)*1e2+0.5)/1e2)", _sql.remove(0));
            } else if (platform.isPostgreSQL()) {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY HAVING (? < FLOOR((?)*10^(?)+0.5)/10^(?))", _sql.remove(0));
            } else if (platform.isDB2() || platform.isDB2Z()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY HAVING (0 < ROUND(9.9, 2))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY HAVING (? < ROUND(?, ?))", _sql.remove(0));
            }

            query = em.createQuery("SELECT 1 FROM QuerySyntaxEntity s HAVING ?1 < ROUND(?2, 2)");
            query.setParameter(1, 0);
            query.setParameter(2, 9.9);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if (platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY HAVING (0 < FLOOR((9.9)*1e2+0.5)/1e2)", _sql.remove(0));
            } else if (platform.isPostgreSQL()) {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY HAVING (? < FLOOR((?)*10^(?)+0.5)/10^(?))", _sql.remove(0));
            } else if (platform.isDB2() || platform.isDB2Z()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY HAVING (0 < ROUND(9.9, 2))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY HAVING (? < ROUND(?, ?))", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Double> doubleParam1 = cb.parameter(Double.class);
            ParameterExpression<Double> doubleParam2 = cb.parameter(Double.class);
            cquery.multiselect(intParam1);
            cquery.having(cb.lessThan(doubleParam1, cb.round(doubleParam2, 2)));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 1);
            query.setParameter(doubleParam1, 0d);
            query.setParameter(doubleParam2, 9.9d);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if (platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY HAVING (0.0 < FLOOR((9.9)*1e2+0.5)/1e2)", _sql.remove(0));
            } else if (platform.isPostgreSQL()) {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY HAVING (? < FLOOR((?)*10^(?)+0.5)/10^(?))", _sql.remove(0));
            } else if (platform.isDB2() || platform.isDB2Z()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY HAVING (0.0 < ROUND(9.9, 2))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY HAVING (? < ROUND(?, ?))", _sql.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(cb2.literal(1));
            cquery2.having(cb2.lessThan(cb2.literal(0d), cb2.round(cb2.literal(9.9d), 2)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if (platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY HAVING (0.0 < FLOOR((9.9)*1e2+0.5)/1e2)", _sql.remove(0));
            } else if (platform.isPostgreSQL()) {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY HAVING (? < FLOOR((?)*10^(?)+0.5)/10^(?))", _sql.remove(0));
            } else if (platform.isDB2() || platform.isDB2Z()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY HAVING (0.0 < ROUND(9.9, 2))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY HAVING (? < ROUND(?, ?))", _sql.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(cb3.literal(1));
            ParameterExpression<Double> doubleParam3 = cb3.parameter(Double.class);
            ParameterExpression<Double> doubleParam4 = cb3.parameter(Double.class);
            cquery3.having(cb3.lessThan(doubleParam3, cb3.round(doubleParam4, 2)));

            query = em.createQuery(cquery3);
            query.setParameter(doubleParam3, 0d);
            query.setParameter(doubleParam4, 9.9d);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if (platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY HAVING (0.0 < FLOOR((9.9)*1e2+0.5)/1e2)", _sql.remove(0));
            } else if (platform.isPostgreSQL()) {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY HAVING (? < FLOOR((?)*10^(?)+0.5)/10^(?))", _sql.remove(0));
            } else if (platform.isDB2() || platform.isDB2Z()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY HAVING (0.0 < ROUND(9.9, 2))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY HAVING (? < ROUND(?, ?))", _sql.remove(0));
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
    public void testRound3_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT ?1 FROM QuerySyntaxEntity s HAVING ?2 < ROUND(?3, ?4)");
            query.setParameter(1, 1);
            query.setParameter(2, 0);
            query.setParameter(3, 9.9);
            query.setParameter(4, 2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if (platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY HAVING (? < FLOOR((9.9)*1e2+0.5)/1e2)", _sql2.remove(0));
            } else if (platform.isPostgreSQL()) {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY HAVING (? < FLOOR((?)*10^(?)+0.5)/10^(?))", _sql2.remove(0));
            } else if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY HAVING (? < ROUND(9.9, 2))", _sql2.remove(0));
            } else if (platform.isDB2()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY HAVING (? < ROUND(?, 2))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY HAVING (? < ROUND(?, ?))", _sql2.remove(0));
            }

            query = em.createQuery("SELECT 1 FROM QuerySyntaxEntity s HAVING 0 < ROUND(9.9, 2)");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if (platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY HAVING (? < FLOOR((9.9)*1e2+0.5)/1e2)", _sql2.remove(0));
            } else if (platform.isPostgreSQL()) {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY HAVING (? < FLOOR((?)*10^(?)+0.5)/10^(?))", _sql2.remove(0));
            } else if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY HAVING (? < ROUND(9.9, 2))", _sql2.remove(0));
            } else if (platform.isDB2()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY HAVING (? < ROUND(?, 2))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY HAVING (? < ROUND(?, ?))", _sql2.remove(0));
            }

            query = em.createQuery("SELECT 1 FROM QuerySyntaxEntity s HAVING ?1 < ROUND(?2, 2)");
            query.setParameter(1, 0);
            query.setParameter(2, 9.9);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if (platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY HAVING (? < FLOOR((9.9)*1e2+0.5)/1e2)", _sql2.remove(0));
            } else if (platform.isPostgreSQL()) {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY HAVING (? < FLOOR((?)*10^(?)+0.5)/10^(?))", _sql2.remove(0));
            } else if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY HAVING (? < ROUND(9.9, 2))", _sql2.remove(0));
            } else if (platform.isDB2()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY HAVING (? < ROUND(?, 2))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY HAVING (? < ROUND(?, ?))", _sql2.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Double> doubleParam1 = cb.parameter(Double.class);
            ParameterExpression<Double> doubleParam2 = cb.parameter(Double.class);
            cquery.multiselect(intParam1);
            cquery.having(cb.lessThan(doubleParam1, cb.round(doubleParam2, 2)));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 1);
            query.setParameter(doubleParam1, 0d);
            query.setParameter(doubleParam2, 9.9d);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if (platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY HAVING (? < FLOOR((9.9)*1e2+0.5)/1e2)", _sql2.remove(0));
            } else if (platform.isPostgreSQL()) {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY HAVING (? < FLOOR((?)*10^(?)+0.5)/10^(?))", _sql2.remove(0));
            } else if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY HAVING (? < ROUND(9.9, 2))", _sql2.remove(0));
            } else if (platform.isDB2()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY HAVING (? < ROUND(?, 2))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY HAVING (? < ROUND(?, ?))", _sql2.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(cb2.literal(1));
            cquery2.having(cb2.lessThan(cb2.literal(0d), cb2.round(cb2.literal(9.9d), 2)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if (platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY HAVING (? < FLOOR((9.9)*1e2+0.5)/1e2)", _sql2.remove(0));
            } else if (platform.isPostgreSQL()) {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY HAVING (? < FLOOR((?)*10^(?)+0.5)/10^(?))", _sql2.remove(0));
            } else if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY HAVING (? < ROUND(9.9, 2))", _sql2.remove(0));
            } else if (platform.isDB2()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY HAVING (? < ROUND(?, 2))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY HAVING (? < ROUND(?, ?))", _sql2.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(cb3.literal(1));
            ParameterExpression<Double> doubleParam3 = cb3.parameter(Double.class);
            ParameterExpression<Double> doubleParam4 = cb3.parameter(Double.class);
            cquery3.having(cb3.lessThan(doubleParam3, cb3.round(doubleParam4, 2)));

            query = em.createQuery(cquery3);
            query.setParameter(doubleParam3, 0d);
            query.setParameter(doubleParam4, 9.9d);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if (platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY HAVING (? < FLOOR((9.9)*1e2+0.5)/1e2)", _sql2.remove(0));
            } else if (platform.isPostgreSQL()) {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY HAVING (? < FLOOR((?)*10^(?)+0.5)/10^(?))", _sql2.remove(0));
            } else if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY HAVING (? < ROUND(9.9, 2))", _sql2.remove(0));
            } else if (platform.isDB2()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY HAVING (? < ROUND(?, 2))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY HAVING (? < ROUND(?, ?))", _sql2.remove(0));
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
    public void testRound3_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("SELECT ?1 FROM QuerySyntaxEntity s HAVING ?2 < ROUND(?3, ?4)");
            query.setParameter(1, 1);
            query.setParameter(2, 0);
            query.setParameter(3, 9.9);
            query.setParameter(4, 2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if (platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY HAVING (? < FLOOR((9.9)*1e2+0.5)/1e2)", _sql3.remove(0));
            } else if (platform.isPostgreSQL()) {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY HAVING (? < FLOOR((?)*10^(?)+0.5)/10^(?))", _sql3.remove(0));
            } else if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY HAVING (? < ROUND(9.9, 2))", _sql3.remove(0));
            } else if (platform.isDB2()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY HAVING (? < ROUND(?, 2))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY HAVING (? < ROUND(?, ?))", _sql3.remove(0));
            }

            query = em.createQuery("SELECT 1 FROM QuerySyntaxEntity s HAVING 0 < ROUND(9.9, 2)");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if (platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY HAVING (? < FLOOR((9.9)*1e2+0.5)/1e2)", _sql3.remove(0));
            } else if (platform.isPostgreSQL()) {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY HAVING (? < FLOOR((?)*10^(?)+0.5)/10^(?))", _sql3.remove(0));
            } else if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY HAVING (? < ROUND(9.9, 2))", _sql3.remove(0));
            } else if (platform.isDB2()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY HAVING (? < ROUND(?, 2))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY HAVING (? < ROUND(?, ?))", _sql3.remove(0));
            }

            query = em.createQuery("SELECT 1 FROM QuerySyntaxEntity s HAVING ?1 < ROUND(?2, 2)");
            query.setParameter(1, 0);
            query.setParameter(2, 9.9);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if (platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY HAVING (? < FLOOR((9.9)*1e2+0.5)/1e2)", _sql3.remove(0));
            } else if (platform.isPostgreSQL()) {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY HAVING (? < FLOOR((?)*10^(?)+0.5)/10^(?))", _sql3.remove(0));
            } else if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY HAVING (? < ROUND(9.9, 2))", _sql3.remove(0));
            } else if (platform.isDB2()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY HAVING (? < ROUND(?, 2))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY HAVING (? < ROUND(?, ?))", _sql3.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Double> doubleParam1 = cb.parameter(Double.class);
            ParameterExpression<Double> doubleParam2 = cb.parameter(Double.class);
            cquery.multiselect(intParam1);
            cquery.having(cb.lessThan(doubleParam1, cb.round(doubleParam2, 2)));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 1);
            query.setParameter(doubleParam1, 0d);
            query.setParameter(doubleParam2, 9.9d);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if (platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY HAVING (? < FLOOR((9.9)*1e2+0.5)/1e2)", _sql3.remove(0));
            } else if (platform.isPostgreSQL()) {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY HAVING (? < FLOOR((?)*10^(?)+0.5)/10^(?))", _sql3.remove(0));
            } else if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY HAVING (? < ROUND(9.9, 2))", _sql3.remove(0));
            } else if (platform.isDB2()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY HAVING (? < ROUND(?, 2))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY HAVING (? < ROUND(?, ?))", _sql3.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(cb2.literal(1));
            cquery2.having(cb2.lessThan(cb2.literal(0d), cb2.round(cb2.literal(9.9d), 2)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if (platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY HAVING (? < FLOOR((9.9)*1e2+0.5)/1e2)", _sql3.remove(0));
            } else if (platform.isPostgreSQL()) {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY HAVING (? < FLOOR((?)*10^(?)+0.5)/10^(?))", _sql3.remove(0));
            } else if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY HAVING (? < ROUND(9.9, 2))", _sql3.remove(0));
            } else if (platform.isDB2()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY HAVING (? < ROUND(?, 2))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY HAVING (? < ROUND(?, ?))", _sql3.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(cb3.literal(1));
            ParameterExpression<Double> doubleParam3 = cb3.parameter(Double.class);
            ParameterExpression<Double> doubleParam4 = cb3.parameter(Double.class);
            cquery3.having(cb3.lessThan(doubleParam3, cb3.round(doubleParam4, 2)));

            query = em.createQuery(cquery3);
            query.setParameter(doubleParam3, 0d);
            query.setParameter(doubleParam4, 9.9d);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if (platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY HAVING (? < FLOOR((9.9)*1e2+0.5)/1e2)", _sql3.remove(0));
            } else if (platform.isPostgreSQL()) {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY HAVING (? < FLOOR((?)*10^(?)+0.5)/10^(?))", _sql3.remove(0));
            } else if (platform.isDB2Z()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY HAVING (? < ROUND(9.9, 2))", _sql3.remove(0));
            } else if (platform.isDB2()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY HAVING (? < ROUND(?, 2))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY HAVING (? < ROUND(?, ?))", _sql3.remove(0));
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
