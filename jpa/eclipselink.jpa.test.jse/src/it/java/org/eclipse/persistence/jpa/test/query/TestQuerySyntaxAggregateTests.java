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

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.ParameterExpression;
import jakarta.persistence.criteria.Root;

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
public class TestQuerySyntaxAggregateTests {

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
    public void testAvg1_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT AVG(?1) FROM QuerySyntaxEntity s");
            query.setParameter(1, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() ||  platform.isDerby()) {
                Assert.assertEquals("SELECT AVG(1) FROM QUERYSYNTAXENTITY", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT AVG(?) FROM QUERYSYNTAXENTITY", _sql.remove(0));
            }

            query = em.createQuery("SELECT AVG(1) FROM QuerySyntaxEntity s");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() ||  platform.isDerby()) {
                Assert.assertEquals("SELECT AVG(1) FROM QUERYSYNTAXENTITY", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT AVG(?) FROM QUERYSYNTAXENTITY", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            cquery.multiselect(cb.avg(intParam1));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() ||  platform.isDerby()) {
                Assert.assertEquals("SELECT AVG(1) FROM QUERYSYNTAXENTITY", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT AVG(?) FROM QUERYSYNTAXENTITY", _sql.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(cb2.avg(cb2.literal(1)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() ||  platform.isDerby()) {
                Assert.assertEquals("SELECT AVG(1) FROM QUERYSYNTAXENTITY", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT AVG(?) FROM QUERYSYNTAXENTITY", _sql.remove(0));
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
    public void testAvg1_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT AVG(?1) FROM QuerySyntaxEntity s");
            query.setParameter(1, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT AVG(1) FROM QUERYSYNTAXENTITY", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT AVG(?) FROM QUERYSYNTAXENTITY", _sql2.remove(0));
            }

            query = em.createQuery("SELECT AVG(1) FROM QuerySyntaxEntity s");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT AVG(1) FROM QUERYSYNTAXENTITY", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT AVG(?) FROM QUERYSYNTAXENTITY", _sql2.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            cquery.multiselect(cb.avg(intParam1));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT AVG(1) FROM QUERYSYNTAXENTITY", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT AVG(?) FROM QUERYSYNTAXENTITY", _sql2.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(cb2.avg(cb2.literal(1)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT AVG(1) FROM QUERYSYNTAXENTITY", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT AVG(?) FROM QUERYSYNTAXENTITY", _sql2.remove(0));
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
    public void testAvg1_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("SELECT AVG(?1) FROM QuerySyntaxEntity s");
            query.setParameter(1, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT AVG(1) FROM QUERYSYNTAXENTITY", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT AVG(?) FROM QUERYSYNTAXENTITY", _sql3.remove(0));
            }

            query = em.createQuery("SELECT AVG(1) FROM QuerySyntaxEntity s");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT AVG(1) FROM QUERYSYNTAXENTITY", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT AVG(?) FROM QUERYSYNTAXENTITY", _sql3.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            cquery.multiselect(cb.avg(intParam1));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT AVG(1) FROM QUERYSYNTAXENTITY", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT AVG(?) FROM QUERYSYNTAXENTITY", _sql3.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(cb2.avg(cb2.literal(1)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT AVG(1) FROM QUERYSYNTAXENTITY", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT AVG(?) FROM QUERYSYNTAXENTITY", _sql3.remove(0));
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
    public void testAvg2_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT AVG(?1) FROM QuerySyntaxEntity s HAVING ?2 < AVG(?3)");
            query.setParameter(1, 1);
            query.setParameter(2, 0);
            query.setParameter(3, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() ||  platform.isDerby()) {
                Assert.assertEquals("SELECT AVG(1) FROM QUERYSYNTAXENTITY HAVING (0 < AVG(1))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT AVG(?) FROM QUERYSYNTAXENTITY HAVING (? < AVG(?))", _sql.remove(0));
            }

            query = em.createQuery("SELECT AVG(1) FROM QuerySyntaxEntity s HAVING 0 < AVG(1)");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() ||  platform.isDerby()) {
                Assert.assertEquals("SELECT AVG(1) FROM QUERYSYNTAXENTITY HAVING (0 < AVG(1))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT AVG(?) FROM QUERYSYNTAXENTITY HAVING (? < AVG(?))", _sql.remove(0));
            }

            query = em.createQuery("SELECT AVG(1) FROM QuerySyntaxEntity s HAVING ?1 < AVG(?2)");
            query.setParameter(1, 0);
            query.setParameter(2, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() ||  platform.isDerby()) {
                Assert.assertEquals("SELECT AVG(1) FROM QUERYSYNTAXENTITY HAVING (0 < AVG(1))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT AVG(?) FROM QUERYSYNTAXENTITY HAVING (? < AVG(?))", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Double> intParam2 = cb.parameter(Double.class);
            ParameterExpression<Integer> intParam3 = cb.parameter(Integer.class);
            cquery.multiselect(cb.avg(intParam1));
            cquery.having(cb.lessThan(intParam2, cb.avg(intParam3)));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 1);
            query.setParameter(intParam2, 0d);
            query.setParameter(intParam3, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() ||  platform.isDerby()) {
                Assert.assertEquals("SELECT AVG(1) FROM QUERYSYNTAXENTITY HAVING (0.0 < AVG(1))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT AVG(?) FROM QUERYSYNTAXENTITY HAVING (? < AVG(?))", _sql.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(cb2.avg(cb2.literal(1)));
            cquery2.having(cb2.lessThan(cb2.literal(0d), cb2.avg(cb2.literal(1))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() ||  platform.isDerby()) {
                Assert.assertEquals("SELECT AVG(1) FROM QUERYSYNTAXENTITY HAVING (0.0 < AVG(1))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT AVG(?) FROM QUERYSYNTAXENTITY HAVING (? < AVG(?))", _sql.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(cb3.avg(cb3.literal(1)));
            ParameterExpression<Double> intParam4 = cb3.parameter(Double.class);
            ParameterExpression<Integer> intParam5 = cb3.parameter(Integer.class);
            cquery3.having(cb3.lessThan(intParam4, cb3.avg(intParam5)));

            query = em.createQuery(cquery3);
            query.setParameter(intParam4, 0d);
            query.setParameter(intParam5, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT AVG(1) FROM QUERYSYNTAXENTITY HAVING (0.0 < AVG(1))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT AVG(?) FROM QUERYSYNTAXENTITY HAVING (? < AVG(?))", _sql.remove(0));
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
    public void testAvg2_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT AVG(?1) FROM QuerySyntaxEntity s HAVING ?2 < AVG(?3)");
            query.setParameter(1, 1);
            query.setParameter(2, 0);
            query.setParameter(3, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT AVG(1) FROM QUERYSYNTAXENTITY HAVING (? < AVG(1))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT AVG(?) FROM QUERYSYNTAXENTITY HAVING (? < AVG(?))", _sql2.remove(0));
            }

            query = em.createQuery("SELECT AVG(1) FROM QuerySyntaxEntity s HAVING 0 < AVG(1)");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT AVG(1) FROM QUERYSYNTAXENTITY HAVING (? < AVG(1))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT AVG(?) FROM QUERYSYNTAXENTITY HAVING (? < AVG(?))", _sql2.remove(0));
            }

            query = em.createQuery("SELECT AVG(1) FROM QuerySyntaxEntity s HAVING ?1 < AVG(?2)");
            query.setParameter(1, 0);
            query.setParameter(2, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT AVG(1) FROM QUERYSYNTAXENTITY HAVING (? < AVG(1))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT AVG(?) FROM QUERYSYNTAXENTITY HAVING (? < AVG(?))", _sql2.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Double> intParam2 = cb.parameter(Double.class);
            ParameterExpression<Integer> intParam3 = cb.parameter(Integer.class);
            cquery.multiselect(cb.avg(intParam1));
            cquery.having(cb.lessThan(intParam2, cb.avg(intParam3)));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 1);
            query.setParameter(intParam2, 0d);
            query.setParameter(intParam3, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT AVG(1) FROM QUERYSYNTAXENTITY HAVING (? < AVG(1))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT AVG(?) FROM QUERYSYNTAXENTITY HAVING (? < AVG(?))", _sql2.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(cb2.avg(cb2.literal(1)));
            cquery2.having(cb2.lessThan(cb2.literal(0d), cb2.avg(cb2.literal(1))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT AVG(1) FROM QUERYSYNTAXENTITY HAVING (? < AVG(1))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT AVG(?) FROM QUERYSYNTAXENTITY HAVING (? < AVG(?))", _sql2.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(cb3.avg(cb3.literal(1)));
            ParameterExpression<Double> intParam4 = cb3.parameter(Double.class);
            ParameterExpression<Integer> intParam5 = cb3.parameter(Integer.class);
            cquery3.having(cb3.lessThan(intParam4, cb3.avg(intParam5)));

            query = em.createQuery(cquery3);
            query.setParameter(intParam4, 0d);
            query.setParameter(intParam5, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT AVG(1) FROM QUERYSYNTAXENTITY HAVING (? < AVG(1))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT AVG(?) FROM QUERYSYNTAXENTITY HAVING (? < AVG(?))", _sql2.remove(0));
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
    public void testAvg2_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("SELECT AVG(?1) FROM QuerySyntaxEntity s HAVING ?2 < AVG(?3)");
            query.setParameter(1, 1);
            query.setParameter(2, 0);
            query.setParameter(3, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT AVG(1) FROM QUERYSYNTAXENTITY HAVING (? < AVG(1))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT AVG(?) FROM QUERYSYNTAXENTITY HAVING (? < AVG(?))", _sql3.remove(0));
            }

            query = em.createQuery("SELECT AVG(1) FROM QuerySyntaxEntity s HAVING 0 < AVG(1)");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT AVG(1) FROM QUERYSYNTAXENTITY HAVING (? < AVG(1))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT AVG(?) FROM QUERYSYNTAXENTITY HAVING (? < AVG(?))", _sql3.remove(0));
            }

            query = em.createQuery("SELECT AVG(1) FROM QuerySyntaxEntity s HAVING ?1 < AVG(?2)");
            query.setParameter(1, 0);
            query.setParameter(2, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT AVG(1) FROM QUERYSYNTAXENTITY HAVING (? < AVG(1))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT AVG(?) FROM QUERYSYNTAXENTITY HAVING (? < AVG(?))", _sql3.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Double> intParam2 = cb.parameter(Double.class);
            ParameterExpression<Integer> intParam3 = cb.parameter(Integer.class);
            cquery.multiselect(cb.avg(intParam1));
            cquery.having(cb.lessThan(intParam2, cb.avg(intParam3)));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 1);
            query.setParameter(intParam2, 0d);
            query.setParameter(intParam3, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT AVG(1) FROM QUERYSYNTAXENTITY HAVING (? < AVG(1))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT AVG(?) FROM QUERYSYNTAXENTITY HAVING (? < AVG(?))", _sql3.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(cb2.avg(cb2.literal(1)));
            cquery2.having(cb2.lessThan(cb2.literal(0d), cb2.avg(cb2.literal(1))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT AVG(1) FROM QUERYSYNTAXENTITY HAVING (? < AVG(1))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT AVG(?) FROM QUERYSYNTAXENTITY HAVING (? < AVG(?))", _sql3.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(cb3.avg(cb3.literal(1)));
            ParameterExpression<Double> intParam4 = cb3.parameter(Double.class);
            ParameterExpression<Integer> intParam5 = cb3.parameter(Integer.class);
            cquery3.having(cb3.lessThan(intParam4, cb3.avg(intParam5)));

            query = em.createQuery(cquery3);
            query.setParameter(intParam4, 0d);
            query.setParameter(intParam5, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT AVG(1) FROM QUERYSYNTAXENTITY HAVING (? < AVG(1))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT AVG(?) FROM QUERYSYNTAXENTITY HAVING (? < AVG(?))", _sql3.remove(0));
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
    public void testAvgDistinct1_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT AVG(DISTINCT ?1) FROM QuerySyntaxEntity s");
            query.setParameter(1, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() ||  platform.isDerby()) {
                Assert.assertEquals("SELECT AVG(DISTINCT(1)) FROM QUERYSYNTAXENTITY", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT AVG(DISTINCT(?)) FROM QUERYSYNTAXENTITY", _sql.remove(0));
            }

            query = em.createQuery("SELECT AVG(DISTINCT 1) FROM QuerySyntaxEntity s");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT AVG(DISTINCT(1)) FROM QUERYSYNTAXENTITY", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT AVG(DISTINCT(?)) FROM QUERYSYNTAXENTITY", _sql.remove(0));
            }

            // -----------------------

            /*
             * Currently, the JPA Criteria API does not support DISTINCT for Aggregates
             *     TODO: https://github.com/eclipse-ee4j/jpa-api/issues/326
             */

//            CriteriaBuilder cb = em.getCriteriaBuilder();
//            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
//            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
//            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
//            cquery.multiselect(cb.avgDistinct(intParam1));
//
//            query = em.createQuery(cquery);
//            query.setParameter(intParam1, 1);
//            query.getResultList();
//            Assert.assertEquals(1, _sql.size());
//            if(platform.isDB2Z() || platform.isDB2() ||  platform.isDerby()) {
//                Assert.assertEquals("SELECT AVG(DISTINCT(1)) FROM QUERYSYNTAXENTITY", _sql.remove(0));
//            } else {
//                Assert.assertEquals("SELECT AVG(DISTINCT(?)) FROM QUERYSYNTAXENTITY", _sql.remove(0));
//            }
//
//            CriteriaBuilder cb2 = em.getCriteriaBuilder();
//            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
//            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
//            cquery2.multiselect(cb2.avgDistinct(cb2.literal(1)));
//
//            query = em.createQuery(cquery2);
//            query.getResultList();
//            Assert.assertEquals(1, _sql.size());
//            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
//                Assert.assertEquals("SELECT AVG(DISTINCT(1)) FROM QUERYSYNTAXENTITY", _sql.remove(0));
//            } else {
//                Assert.assertEquals("SELECT AVG(DISTINCT(?)) FROM QUERYSYNTAXENTITY", _sql.remove(0));
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
    public void testAvgDistinct1_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT AVG(DISTINCT ?1) FROM QuerySyntaxEntity s");
            query.setParameter(1, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT AVG(DISTINCT(1)) FROM QUERYSYNTAXENTITY", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT AVG(DISTINCT(?)) FROM QUERYSYNTAXENTITY", _sql2.remove(0));
            }

            query = em.createQuery("SELECT AVG(DISTINCT 1) FROM QuerySyntaxEntity s");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT AVG(DISTINCT(1)) FROM QUERYSYNTAXENTITY", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT AVG(DISTINCT(?)) FROM QUERYSYNTAXENTITY", _sql2.remove(0));
            }

            // -----------------------

            /*
             * Currently, the JPA Criteria API does not support DISTINCT for Aggregates
             *     TODO: https://github.com/eclipse-ee4j/jpa-api/issues/326
             */

//            CriteriaBuilder cb = em.getCriteriaBuilder();
//            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
//            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
//            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
//            cquery.multiselect(cb.avgDistinct(intParam1));
//
//            query = em.createQuery(cquery);
//            query.setParameter(intParam1, 1);
//            query.getResultList();
//            Assert.assertEquals(1, _sql2.size());
//            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
//                Assert.assertEquals("SELECT AVG(DISTINCT(1)) FROM QUERYSYNTAXENTITY", _sql2.remove(0));
//            } else {
//                Assert.assertEquals("SELECT AVG(DISTINCT(?)) FROM QUERYSYNTAXENTITY", _sql2.remove(0));
//            }
//
//            CriteriaBuilder cb2 = em.getCriteriaBuilder();
//            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
//            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
//            cquery2.multiselect(cb2.avgDistinct(cb2.literal(1)));
//
//            query = em.createQuery(cquery2);
//            query.getResultList();
//            Assert.assertEquals(1, _sql2.size());
//            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
//                Assert.assertEquals("SELECT AVG(DISTINCT(1)) FROM QUERYSYNTAXENTITY", _sql2.remove(0));
//            } else {
//                Assert.assertEquals("SELECT AVG(DISTINCT(?)) FROM QUERYSYNTAXENTITY", _sql2.remove(0));
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
    public void testAvgDistinct1_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("SELECT AVG(DISTINCT ?1) FROM QuerySyntaxEntity s");
            query.setParameter(1, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT AVG(DISTINCT(1)) FROM QUERYSYNTAXENTITY", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT AVG(DISTINCT(?)) FROM QUERYSYNTAXENTITY", _sql3.remove(0));
            }

            query = em.createQuery("SELECT AVG(DISTINCT 1) FROM QuerySyntaxEntity s");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT AVG(DISTINCT(1)) FROM QUERYSYNTAXENTITY", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT AVG(DISTINCT(?)) FROM QUERYSYNTAXENTITY", _sql3.remove(0));
            }

            // -----------------------

            /*
             * Currently, the JPA Criteria API does not support DISTINCT for Aggregates
             *     TODO: https://github.com/eclipse-ee4j/jpa-api/issues/326
             */

//            CriteriaBuilder cb = em.getCriteriaBuilder();
//            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
//            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
//            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
//            cquery.multiselect(cb.avgDistinct(intParam1));
//
//            query = em.createQuery(cquery);
//            query.setParameter(intParam1, 1);
//            query.getResultList();
//            Assert.assertEquals(1, _sql3.size());
//            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
//                Assert.assertEquals("SELECT AVG(DISTINCT(1)) FROM QUERYSYNTAXENTITY", _sql3.remove(0));
//            } else {
//                Assert.assertEquals("SELECT AVG(DISTINCT(?)) FROM QUERYSYNTAXENTITY", _sql3.remove(0));
//            }
//
//            CriteriaBuilder cb2 = em.getCriteriaBuilder();
//            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
//            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
//            cquery2.multiselect(cb2.avgDistinct(cb2.literal(1)));
//
//            query = em.createQuery(cquery2);
//            query.getResultList();
//            Assert.assertEquals(1, _sql3.size());
//            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
//                Assert.assertEquals("SELECT AVG(DISTINCT(1)) FROM QUERYSYNTAXENTITY", _sql3.remove(0));
//            } else {
//                Assert.assertEquals("SELECT AVG(DISTINCT(?)) FROM QUERYSYNTAXENTITY", _sql3.remove(0));
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
    public void testAvgDistinct2_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT AVG(?1) FROM QuerySyntaxEntity s HAVING ?2 < AVG(DISTINCT ?3)");
            query.setParameter(1, 1);
            query.setParameter(2, 0);
            query.setParameter(3, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() ||  platform.isDerby()) {
                Assert.assertEquals("SELECT AVG(1) FROM QUERYSYNTAXENTITY HAVING (0 < AVG(DISTINCT(1)))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT AVG(?) FROM QUERYSYNTAXENTITY HAVING (? < AVG(DISTINCT(?)))", _sql.remove(0));
            }

            query = em.createQuery("SELECT AVG(1) FROM QuerySyntaxEntity s HAVING 0 < AVG(DISTINCT 1)");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() ||  platform.isDerby()) {
                Assert.assertEquals("SELECT AVG(1) FROM QUERYSYNTAXENTITY HAVING (0 < AVG(DISTINCT(1)))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT AVG(?) FROM QUERYSYNTAXENTITY HAVING (? < AVG(DISTINCT(?)))", _sql.remove(0));
            }

            query = em.createQuery("SELECT AVG(1) FROM QuerySyntaxEntity s HAVING ?1 < AVG(DISTINCT ?2)");
            query.setParameter(1, 0);
            query.setParameter(2, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() ||  platform.isDerby()) {
                Assert.assertEquals("SELECT AVG(1) FROM QUERYSYNTAXENTITY HAVING (0 < AVG(DISTINCT(1)))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT AVG(?) FROM QUERYSYNTAXENTITY HAVING (? < AVG(DISTINCT(?)))", _sql.remove(0));
            }

            // -----------------------

            /*
             * Currently, the JPA Criteria API does not support DISTINCT for Aggregates
             *     TODO: https://github.com/eclipse-ee4j/jpa-api/issues/326
             */

//            CriteriaBuilder cb = em.getCriteriaBuilder();
//            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
//            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
//            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
//            ParameterExpression<Double> intParam2 = cb.parameter(Double.class);
//            ParameterExpression<Integer> intParam3 = cb.parameter(Integer.class);
//            cquery.multiselect(cb.avg(intParam1));
//            cquery.having(cb.lessThan(intParam2, cb.avgDistinct(intParam3)));
//
//            query = em.createQuery(cquery);
//            query.setParameter(intParam1, 1);
//            query.setParameter(intParam2, 0d);
//            query.setParameter(intParam3, 1);
//            query.getResultList();
//            Assert.assertEquals(1, _sql.size());
//            if(platform.isDB2Z() || platform.isDB2() ||  platform.isDerby()) {
//                Assert.assertEquals("SELECT AVG(1) FROM QUERYSYNTAXENTITY HAVING (0 < AVG(DISTINCT(1)))", _sql.remove(0));
//            } else {
//                Assert.assertEquals("SELECT AVG(?) FROM QUERYSYNTAXENTITY HAVING (? < AVG(DISTINCT(?)))", _sql.remove(0));
//            }
//
//            CriteriaBuilder cb2 = em.getCriteriaBuilder();
//            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
//            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
//            cquery2.multiselect(cb2.avg(cb2.literal(1)));
//            cquery2.having(cb2.lessThan(cb2.literal(0d), cb2.avgDistinct(cb2.literal(1))));
//
//            query = em.createQuery(cquery2);
//            query.getResultList();
//            Assert.assertEquals(1, _sql.size());
//            if(platform.isDB2Z() || platform.isDB2() ||  platform.isDerby()) {
//                Assert.assertEquals("SELECT AVG(1) FROM QUERYSYNTAXENTITY HAVING (0 < AVG(DISTINCT(1)))", _sql.remove(0));
//            } else {
//                Assert.assertEquals("SELECT AVG(?) FROM QUERYSYNTAXENTITY HAVING (? < AVG(DISTINCT(?)))", _sql.remove(0));
//            }
//
//            CriteriaBuilder cb3 = em.getCriteriaBuilder();
//            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
//            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
//            cquery3.multiselect(cb3.avg(cb3.literal(1)));
//            ParameterExpression<Double> intParam4 = cb3.parameter(Double.class);
//            ParameterExpression<Integer> intParam5 = cb3.parameter(Integer.class);
//            cquery3.having(cb3.lessThan(intParam4, cb3.avgDistinct(intParam5)));
//
//            query = em.createQuery(cquery3);
//            query.setParameter(intParam4, 0d);
//            query.setParameter(intParam5, 1);
//            query.getResultList();
//            Assert.assertEquals(1, _sql.size());
//            if(platform.isDB2Z() || platform.isDB2() ||  platform.isDerby()) {
//                Assert.assertEquals("SELECT AVG(1) FROM QUERYSYNTAXENTITY HAVING (0 < AVG(DISTINCT(1)))", _sql.remove(0));
//            } else {
//                Assert.assertEquals("SELECT AVG(?) FROM QUERYSYNTAXENTITY HAVING (? < AVG(DISTINCT(?)))", _sql.remove(0));
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
    public void testAvgDistinct2_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT AVG(?1) FROM QuerySyntaxEntity s HAVING ?2 < AVG(DISTINCT ?3)");
            query.setParameter(1, 1);
            query.setParameter(2, 0);
            query.setParameter(3, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT AVG(1) FROM QUERYSYNTAXENTITY HAVING (? < AVG(DISTINCT(1)))", _sql2.remove(0));
            } else if( platform.isDB2()) {
                Assert.assertEquals("SELECT AVG(?) FROM QUERYSYNTAXENTITY HAVING (? < AVG(DISTINCT(1)))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT AVG(?) FROM QUERYSYNTAXENTITY HAVING (? < AVG(DISTINCT(?)))", _sql2.remove(0));
            }

            query = em.createQuery("SELECT AVG(1) FROM QuerySyntaxEntity s HAVING 0 < AVG(DISTINCT 1)");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT AVG(1) FROM QUERYSYNTAXENTITY HAVING (? < AVG(DISTINCT(1)))", _sql2.remove(0));
            } else if(platform.isDB2()) {
                Assert.assertEquals("SELECT AVG(?) FROM QUERYSYNTAXENTITY HAVING (? < AVG(DISTINCT(1)))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT AVG(?) FROM QUERYSYNTAXENTITY HAVING (? < AVG(DISTINCT(?)))", _sql2.remove(0));
            }

            query = em.createQuery("SELECT AVG(1) FROM QuerySyntaxEntity s HAVING ?1 < AVG(DISTINCT ?2)");
            query.setParameter(1, 0);
            query.setParameter(2, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT AVG(1) FROM QUERYSYNTAXENTITY HAVING (? < AVG(DISTINCT(1)))", _sql2.remove(0));
            } else if(platform.isDB2()) {
                Assert.assertEquals("SELECT AVG(?) FROM QUERYSYNTAXENTITY HAVING (? < AVG(DISTINCT(1)))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT AVG(?) FROM QUERYSYNTAXENTITY HAVING (? < AVG(DISTINCT(?)))", _sql2.remove(0));
            }

            // -----------------------

            /*
             * Currently, the JPA Criteria API does not support DISTINCT for Aggregates
             *     TODO: https://github.com/eclipse-ee4j/jpa-api/issues/326
             */

//            CriteriaBuilder cb = em.getCriteriaBuilder();
//            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
//            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
//            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
//            ParameterExpression<Double> intParam2 = cb.parameter(Double.class);
//            ParameterExpression<Integer> intParam3 = cb.parameter(Integer.class);
//            cquery.multiselect(cb.avg(intParam1));
//            cquery.having(cb.lessThan(intParam2, cb.avgDistinct(intParam3)));
//
//            query = em.createQuery(cquery);
//            query.setParameter(intParam1, 1);
//            query.setParameter(intParam2, 0d);
//            query.setParameter(intParam3, 1);
//            query.getResultList();
//            Assert.assertEquals(1, _sql2.size());
//            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
//                Assert.assertEquals("SELECT AVG(1) FROM QUERYSYNTAXENTITY HAVING (? < AVG(DISTINCT(1)))", _sql2.remove(0));
//            } else {
//                Assert.assertEquals("SELECT AVG(?) FROM QUERYSYNTAXENTITY HAVING (? < AVG(DISTINCT(?)))", _sql2.remove(0));
//            }
//
//            CriteriaBuilder cb2 = em.getCriteriaBuilder();
//            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
//            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
//            cquery2.multiselect(cb2.avg(cb2.literal(1)));
//            cquery2.having(cb2.lessThan(cb2.literal(0d), cb2.avgDistinct(cb2.literal(1))));
//
//            query = em.createQuery(cquery2);
//            query.getResultList();
//            Assert.assertEquals(1, _sql2.size());
//            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
//                Assert.assertEquals("SELECT AVG(1) FROM QUERYSYNTAXENTITY HAVING (0 < AVG(DISTINCT(1)))", _sql2.remove(0));
//            } else {
//                Assert.assertEquals("SELECT AVG(?) FROM QUERYSYNTAXENTITY HAVING (? < AVG(DISTINCT(?)))", _sql2.remove(0));
//            }
//
//            CriteriaBuilder cb3 = em.getCriteriaBuilder();
//            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
//            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
//            cquery3.multiselect(cb3.avg(cb3.literal(1)));
//            ParameterExpression<Double> intParam4 = cb3.parameter(Double.class);
//            ParameterExpression<Integer> intParam5 = cb3.parameter(Integer.class);
//            cquery3.having(cb3.lessThan(intParam4, cb3.avgDistinct(intParam5)));
//
//            query = em.createQuery(cquery3);
//            query.setParameter(intParam4, 0d);
//            query.setParameter(intParam5, 1);
//            query.getResultList();
//            Assert.assertEquals(1, _sql.size());
//            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
//                Assert.assertEquals("SELECT AVG(1) FROM QUERYSYNTAXENTITY HAVING (? < AVG(DISTINCT(1)))", _sql2.remove(0));
//            } else {
//                Assert.assertEquals("SELECT AVG(?) FROM QUERYSYNTAXENTITY HAVING (? < AVG(DISTINCT(?)))", _sql2.remove(0));
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
    public void testAvgDistinct2_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("SELECT AVG(?1) FROM QuerySyntaxEntity s HAVING ?2 < AVG(DISTINCT ?3)");
            query.setParameter(1, 1);
            query.setParameter(2, 0);
            query.setParameter(3, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT AVG(1) FROM QUERYSYNTAXENTITY HAVING (? < AVG(DISTINCT(1)))", _sql3.remove(0));
            } else if(platform.isDB2()) {
                Assert.assertEquals("SELECT AVG(?) FROM QUERYSYNTAXENTITY HAVING (? < AVG(DISTINCT(1)))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT AVG(?) FROM QUERYSYNTAXENTITY HAVING (? < AVG(DISTINCT(?)))", _sql3.remove(0));
            }

            query = em.createQuery("SELECT AVG(1) FROM QuerySyntaxEntity s HAVING 0 < AVG(DISTINCT 1)");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT AVG(1) FROM QUERYSYNTAXENTITY HAVING (? < AVG(DISTINCT(1)))", _sql3.remove(0));
            } else if(platform.isDB2()) {
                Assert.assertEquals("SELECT AVG(?) FROM QUERYSYNTAXENTITY HAVING (? < AVG(DISTINCT(1)))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT AVG(?) FROM QUERYSYNTAXENTITY HAVING (? < AVG(DISTINCT(?)))", _sql3.remove(0));
            }

            query = em.createQuery("SELECT AVG(1) FROM QuerySyntaxEntity s HAVING ?1 < AVG(DISTINCT ?2)");
            query.setParameter(1, 0);
            query.setParameter(2, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT AVG(1) FROM QUERYSYNTAXENTITY HAVING (? < AVG(DISTINCT(1)))", _sql3.remove(0));
            } else if(platform.isDB2()) {
                Assert.assertEquals("SELECT AVG(?) FROM QUERYSYNTAXENTITY HAVING (? < AVG(DISTINCT(1)))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT AVG(?) FROM QUERYSYNTAXENTITY HAVING (? < AVG(DISTINCT(?)))", _sql3.remove(0));
            }

            // -----------------------

            /*
             * Currently, the JPA Criteria API does not support DISTINCT for Aggregates
             *     TODO: https://github.com/eclipse-ee4j/jpa-api/issues/326
             */

//            CriteriaBuilder cb = em.getCriteriaBuilder();
//            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
//            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
//            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
//            ParameterExpression<Double> intParam2 = cb.parameter(Double.class);
//            ParameterExpression<Integer> intParam3 = cb.parameter(Integer.class);
//            cquery.multiselect(cb.avg(intParam1));
//            cquery.having(cb.lessThan(intParam2, cb.avgDistinct(intParam3)));
//
//            query = em.createQuery(cquery);
//            query.setParameter(intParam1, 1);
//            query.setParameter(intParam2, 0d);
//            query.setParameter(intParam3, 1);
//            query.getResultList();
//            Assert.assertEquals(1, _sql3.size());
//            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
//                Assert.assertEquals("SELECT AVG(1) FROM QUERYSYNTAXENTITY HAVING (? < AVG(DISTINCT(1)))", _sql3.remove(0));
//            } else {
//                Assert.assertEquals("SELECT AVG(?) FROM QUERYSYNTAXENTITY HAVING (? < AVG(DISTINCT(?)))", _sql3.remove(0));
//            }
//
//            CriteriaBuilder cb2 = em.getCriteriaBuilder();
//            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
//            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
//            cquery2.multiselect(cb2.avg(cb2.literal(1)));
//            cquery2.having(cb2.lessThan(cb2.literal(0d), cb2.avgDistinct(cb2.literal(1))));
//
//            query = em.createQuery(cquery2);
//            query.getResultList();
//            Assert.assertEquals(1, _sql3.size());
//            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
//                Assert.assertEquals("SELECT AVG(1) FROM QUERYSYNTAXENTITY HAVING (? < AVG(DISTINCT(1)))", _sql3.remove(0));
//            } else {
//                Assert.assertEquals("SELECT AVG(?) FROM QUERYSYNTAXENTITY HAVING (? < AVG(DISTINCT(?)))", _sql3.remove(0));
//            }
//
//            CriteriaBuilder cb3 = em.getCriteriaBuilder();
//            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
//            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
//            cquery3.multiselect(cb3.avg(cb3.literal(1)));
//            ParameterExpression<Double> intParam4 = cb3.parameter(Double.class);
//            ParameterExpression<Integer> intParam5 = cb3.parameter(Integer.class);
//            cquery3.having(cb3.lessThan(intParam4, cb3.avgDistinct(intParam5)));
//
//            query = em.createQuery(cquery3);
//            query.setParameter(intParam4, 0d);
//            query.setParameter(intParam5, 1);
//            query.getResultList();
//            Assert.assertEquals(1, _sql3.size());
//            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
//                Assert.assertEquals("SELECT AVG(1) FROM QUERYSYNTAXENTITY HAVING (? < AVG(DISTINCT(1)))", _sql3.remove(0));
//            } else {
//                Assert.assertEquals("SELECT AVG(?) FROM QUERYSYNTAXENTITY HAVING (? < AVG(DISTINCT(?)))", _sql3.remove(0));
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
    public void testCount1_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT COUNT(?1) FROM QuerySyntaxEntity s");
            query.setParameter(1, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() ||  platform.isDerby()) {
                Assert.assertEquals("SELECT COUNT(1) FROM QUERYSYNTAXENTITY", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT COUNT(?) FROM QUERYSYNTAXENTITY", _sql.remove(0));
            }

            query = em.createQuery("SELECT COUNT(1) FROM QuerySyntaxEntity s");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() ||  platform.isDerby()) {
                Assert.assertEquals("SELECT COUNT(1) FROM QUERYSYNTAXENTITY", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT COUNT(?) FROM QUERYSYNTAXENTITY", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            cquery.multiselect(cb.count(intParam1));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() ||  platform.isDerby()) {
                Assert.assertEquals("SELECT COUNT(1) FROM QUERYSYNTAXENTITY", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT COUNT(?) FROM QUERYSYNTAXENTITY", _sql.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(cb2.count(cb2.literal(1)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() ||  platform.isDerby()) {
                Assert.assertEquals("SELECT COUNT(1) FROM QUERYSYNTAXENTITY", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT COUNT(?) FROM QUERYSYNTAXENTITY", _sql.remove(0));
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
    public void testCount1_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT COUNT(?1) FROM QuerySyntaxEntity s");
            query.setParameter(1, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT COUNT(1) FROM QUERYSYNTAXENTITY", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT COUNT(?) FROM QUERYSYNTAXENTITY", _sql2.remove(0));
            }

            query = em.createQuery("SELECT COUNT(1) FROM QuerySyntaxEntity s");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT COUNT(1) FROM QUERYSYNTAXENTITY", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT COUNT(?) FROM QUERYSYNTAXENTITY", _sql2.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            cquery.multiselect(cb.count(intParam1));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT COUNT(1) FROM QUERYSYNTAXENTITY", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT COUNT(?) FROM QUERYSYNTAXENTITY", _sql2.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(cb2.count(cb2.literal(1)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT COUNT(1) FROM QUERYSYNTAXENTITY", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT COUNT(?) FROM QUERYSYNTAXENTITY", _sql2.remove(0));
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
    public void testCount1_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("SELECT COUNT(?1) FROM QuerySyntaxEntity s");
            query.setParameter(1, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT COUNT(1) FROM QUERYSYNTAXENTITY", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT COUNT(?) FROM QUERYSYNTAXENTITY", _sql3.remove(0));
            }

            query = em.createQuery("SELECT COUNT(1) FROM QuerySyntaxEntity s");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT COUNT(1) FROM QUERYSYNTAXENTITY", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT COUNT(?) FROM QUERYSYNTAXENTITY", _sql3.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            cquery.multiselect(cb.count(intParam1));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT COUNT(1) FROM QUERYSYNTAXENTITY", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT COUNT(?) FROM QUERYSYNTAXENTITY", _sql3.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(cb2.count(cb2.literal(1)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT COUNT(1) FROM QUERYSYNTAXENTITY", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT COUNT(?) FROM QUERYSYNTAXENTITY", _sql3.remove(0));
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
    public void testCount2_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT COUNT(?1) FROM QuerySyntaxEntity s HAVING ?2 < COUNT(?3)");
            query.setParameter(1, 1);
            query.setParameter(2, 0);
            query.setParameter(3, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT COUNT(1) FROM QUERYSYNTAXENTITY HAVING (0 < COUNT(1))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT COUNT(?) FROM QUERYSYNTAXENTITY HAVING (? < COUNT(?))", _sql.remove(0));
            }

            query = em.createQuery("SELECT COUNT(1) FROM QuerySyntaxEntity s HAVING 0 < COUNT(1)");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT COUNT(1) FROM QUERYSYNTAXENTITY HAVING (0 < COUNT(1))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT COUNT(?) FROM QUERYSYNTAXENTITY HAVING (? < COUNT(?))", _sql.remove(0));
            }

            query = em.createQuery("SELECT COUNT(1) FROM QuerySyntaxEntity s HAVING ?1 < COUNT(?2)");
            query.setParameter(1, 0);
            query.setParameter(2, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() ||  platform.isDerby()) {
                Assert.assertEquals("SELECT COUNT(1) FROM QUERYSYNTAXENTITY HAVING (0 < COUNT(1))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT COUNT(?) FROM QUERYSYNTAXENTITY HAVING (? < COUNT(?))", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Long> intParam2 = cb.parameter(Long.class);
            ParameterExpression<Integer> intParam3 = cb.parameter(Integer.class);
            cquery.multiselect(cb.count(intParam1));
            cquery.having(cb.lessThan(intParam2, cb.count(intParam3)));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 1);
            query.setParameter(intParam2, 0L);
            query.setParameter(intParam3, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() ||  platform.isDerby()) {
                Assert.assertEquals("SELECT COUNT(1) FROM QUERYSYNTAXENTITY HAVING (0 < COUNT(1))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT COUNT(?) FROM QUERYSYNTAXENTITY HAVING (? < COUNT(?))", _sql.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(cb2.count(cb2.literal(1)));
            cquery2.having(cb2.lessThan(cb2.literal(0L), cb2.count(cb2.literal(1))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT COUNT(1) FROM QUERYSYNTAXENTITY HAVING (0 < COUNT(1))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT COUNT(?) FROM QUERYSYNTAXENTITY HAVING (? < COUNT(?))", _sql.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(cb3.count(cb3.literal(1)));
            ParameterExpression<Long> intParam4 = cb3.parameter(Long.class);
            ParameterExpression<Integer> intParam5 = cb3.parameter(Integer.class);
            cquery3.having(cb3.lessThan(intParam4, cb3.count(intParam5)));

            query = em.createQuery(cquery3);
            query.setParameter(intParam4, 0L);
            query.setParameter(intParam5, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT COUNT(1) FROM QUERYSYNTAXENTITY HAVING (0 < COUNT(1))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT COUNT(?) FROM QUERYSYNTAXENTITY HAVING (? < COUNT(?))", _sql.remove(0));
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
    public void testCount2_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT COUNT(?1) FROM QuerySyntaxEntity s HAVING ?2 < COUNT(?3)");
            query.setParameter(1, 1);
            query.setParameter(2, 0);
            query.setParameter(3, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT COUNT(1) FROM QUERYSYNTAXENTITY HAVING (? < COUNT(1))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT COUNT(?) FROM QUERYSYNTAXENTITY HAVING (? < COUNT(?))", _sql2.remove(0));
            }

            query = em.createQuery("SELECT COUNT(1) FROM QuerySyntaxEntity s HAVING 0 < COUNT(1)");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT COUNT(1) FROM QUERYSYNTAXENTITY HAVING (? < COUNT(1))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT COUNT(?) FROM QUERYSYNTAXENTITY HAVING (? < COUNT(?))", _sql2.remove(0));
            }

            query = em.createQuery("SELECT COUNT(1) FROM QuerySyntaxEntity s HAVING ?1 < COUNT(?2)");
            query.setParameter(1, 0);
            query.setParameter(2, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT COUNT(1) FROM QUERYSYNTAXENTITY HAVING (? < COUNT(1))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT COUNT(?) FROM QUERYSYNTAXENTITY HAVING (? < COUNT(?))", _sql2.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Long> intParam2 = cb.parameter(Long.class);
            ParameterExpression<Integer> intParam3 = cb.parameter(Integer.class);
            cquery.multiselect(cb.count(intParam1));
            cquery.having(cb.lessThan(intParam2, cb.count(intParam3)));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 1);
            query.setParameter(intParam2, 0L);
            query.setParameter(intParam3, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT COUNT(1) FROM QUERYSYNTAXENTITY HAVING (? < COUNT(1))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT COUNT(?) FROM QUERYSYNTAXENTITY HAVING (? < COUNT(?))", _sql2.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(cb2.count(cb2.literal(1)));
            cquery2.having(cb2.lessThan(cb2.literal(0L), cb2.count(cb2.literal(1))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT COUNT(1) FROM QUERYSYNTAXENTITY HAVING (? < COUNT(1))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT COUNT(?) FROM QUERYSYNTAXENTITY HAVING (? < COUNT(?))", _sql2.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(cb3.count(cb3.literal(1)));
            ParameterExpression<Long> intParam4 = cb3.parameter(Long.class);
            ParameterExpression<Integer> intParam5 = cb3.parameter(Integer.class);
            cquery3.having(cb3.lessThan(intParam4, cb3.count(intParam5)));

            query = em.createQuery(cquery3);
            query.setParameter(intParam4, 0L);
            query.setParameter(intParam5, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT COUNT(1) FROM QUERYSYNTAXENTITY HAVING (? < COUNT(1))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT COUNT(?) FROM QUERYSYNTAXENTITY HAVING (? < COUNT(?))", _sql2.remove(0));
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
    public void testCount2_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("SELECT COUNT(?1) FROM QuerySyntaxEntity s HAVING ?2 < COUNT(?3)");
            query.setParameter(1, 1);
            query.setParameter(2, 0);
            query.setParameter(3, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT COUNT(1) FROM QUERYSYNTAXENTITY HAVING (? < COUNT(1))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT COUNT(?) FROM QUERYSYNTAXENTITY HAVING (? < COUNT(?))", _sql3.remove(0));
            }

            query = em.createQuery("SELECT COUNT(1) FROM QuerySyntaxEntity s HAVING 0 < COUNT(1)");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT COUNT(1) FROM QUERYSYNTAXENTITY HAVING (? < COUNT(1))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT COUNT(?) FROM QUERYSYNTAXENTITY HAVING (? < COUNT(?))", _sql3.remove(0));
            }

            query = em.createQuery("SELECT COUNT(1) FROM QuerySyntaxEntity s HAVING ?1 < COUNT(?2)");
            query.setParameter(1, 0);
            query.setParameter(2, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT COUNT(1) FROM QUERYSYNTAXENTITY HAVING (? < COUNT(1))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT COUNT(?) FROM QUERYSYNTAXENTITY HAVING (? < COUNT(?))", _sql3.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Long> intParam2 = cb.parameter(Long.class);
            ParameterExpression<Integer> intParam3 = cb.parameter(Integer.class);
            cquery.multiselect(cb.count(intParam1));
            cquery.having(cb.lessThan(intParam2, cb.count(intParam3)));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 1);
            query.setParameter(intParam2, 0L);
            query.setParameter(intParam3, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT COUNT(1) FROM QUERYSYNTAXENTITY HAVING (? < COUNT(1))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT COUNT(?) FROM QUERYSYNTAXENTITY HAVING (? < COUNT(?))", _sql3.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(cb2.count(cb2.literal(1)));
            cquery2.having(cb2.lessThan(cb2.literal(0L), cb2.count(cb2.literal(1))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT COUNT(1) FROM QUERYSYNTAXENTITY HAVING (? < COUNT(1))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT COUNT(?) FROM QUERYSYNTAXENTITY HAVING (? < COUNT(?))", _sql3.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(cb3.count(cb3.literal(1)));
            ParameterExpression<Long> intParam4 = cb3.parameter(Long.class);
            ParameterExpression<Integer> intParam5 = cb3.parameter(Integer.class);
            cquery3.having(cb3.lessThan(intParam4, cb3.count(intParam5)));

            query = em.createQuery(cquery3);
            query.setParameter(intParam4, 0L);
            query.setParameter(intParam5, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT COUNT(1) FROM QUERYSYNTAXENTITY HAVING (? < COUNT(1))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT COUNT(?) FROM QUERYSYNTAXENTITY HAVING (? < COUNT(?))", _sql3.remove(0));
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
    public void testCountDistinct1_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT COUNT(DISTINCT ?1) FROM QuerySyntaxEntity s");
            query.setParameter(1, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() ||  platform.isDerby()) {
                Assert.assertEquals("SELECT COUNT(DISTINCT(1)) FROM QUERYSYNTAXENTITY", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT COUNT(DISTINCT(?)) FROM QUERYSYNTAXENTITY", _sql.remove(0));
            }

            query = em.createQuery("SELECT COUNT(DISTINCT 1) FROM QuerySyntaxEntity s");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() ||  platform.isDerby()) {
                Assert.assertEquals("SELECT COUNT(DISTINCT(1)) FROM QUERYSYNTAXENTITY", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT COUNT(DISTINCT(?)) FROM QUERYSYNTAXENTITY", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            cquery.multiselect(cb.countDistinct(intParam1));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() ||  platform.isDerby()) {
                Assert.assertEquals("SELECT COUNT(DISTINCT(1)) FROM QUERYSYNTAXENTITY", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT COUNT(DISTINCT(?)) FROM QUERYSYNTAXENTITY", _sql.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(cb2.countDistinct(cb2.literal(1)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() ||  platform.isDerby()) {
                Assert.assertEquals("SELECT COUNT(DISTINCT(1)) FROM QUERYSYNTAXENTITY", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT COUNT(DISTINCT(?)) FROM QUERYSYNTAXENTITY", _sql.remove(0));
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
    public void testCountDistinct1_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT COUNT(DISTINCT ?1) FROM QuerySyntaxEntity s");
            query.setParameter(1, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT COUNT(DISTINCT(1)) FROM QUERYSYNTAXENTITY", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT COUNT(DISTINCT(?)) FROM QUERYSYNTAXENTITY", _sql2.remove(0));
            }

            query = em.createQuery("SELECT COUNT(DISTINCT 1) FROM QuerySyntaxEntity s");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT COUNT(DISTINCT(1)) FROM QUERYSYNTAXENTITY", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT COUNT(DISTINCT(?)) FROM QUERYSYNTAXENTITY", _sql2.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            cquery.multiselect(cb.countDistinct(intParam1));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT COUNT(DISTINCT(1)) FROM QUERYSYNTAXENTITY", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT COUNT(DISTINCT(?)) FROM QUERYSYNTAXENTITY", _sql2.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(cb2.countDistinct(cb2.literal(1)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT COUNT(DISTINCT(1)) FROM QUERYSYNTAXENTITY", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT COUNT(DISTINCT(?)) FROM QUERYSYNTAXENTITY", _sql2.remove(0));
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
    public void testCountDistinct1_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("SELECT COUNT(DISTINCT ?1) FROM QuerySyntaxEntity s");
            query.setParameter(1, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT COUNT(DISTINCT(1)) FROM QUERYSYNTAXENTITY", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT COUNT(DISTINCT(?)) FROM QUERYSYNTAXENTITY", _sql3.remove(0));
            }

            query = em.createQuery("SELECT COUNT(DISTINCT 1) FROM QuerySyntaxEntity s");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT COUNT(DISTINCT(1)) FROM QUERYSYNTAXENTITY", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT COUNT(DISTINCT(?)) FROM QUERYSYNTAXENTITY", _sql3.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            cquery.multiselect(cb.countDistinct(intParam1));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT COUNT(DISTINCT(1)) FROM QUERYSYNTAXENTITY", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT COUNT(DISTINCT(?)) FROM QUERYSYNTAXENTITY", _sql3.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(cb2.countDistinct(cb2.literal(1)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT COUNT(DISTINCT(1)) FROM QUERYSYNTAXENTITY", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT COUNT(DISTINCT(?)) FROM QUERYSYNTAXENTITY", _sql3.remove(0));
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
    public void testCountDistinct2_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT COUNT(?1) FROM QuerySyntaxEntity s HAVING ?2 < COUNT(DISTINCT ?3)");
            query.setParameter(1, 1);
            query.setParameter(2, 0);
            query.setParameter(3, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT COUNT(1) FROM QUERYSYNTAXENTITY HAVING (0 < COUNT(DISTINCT(1)))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT COUNT(?) FROM QUERYSYNTAXENTITY HAVING (? < COUNT(DISTINCT(?)))", _sql.remove(0));
            }

            query = em.createQuery("SELECT COUNT(1) FROM QuerySyntaxEntity s HAVING 0 < COUNT(DISTINCT 1)");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT COUNT(1) FROM QUERYSYNTAXENTITY HAVING (0 < COUNT(DISTINCT(1)))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT COUNT(?) FROM QUERYSYNTAXENTITY HAVING (? < COUNT(DISTINCT(?)))", _sql.remove(0));
            }

            query = em.createQuery("SELECT COUNT(1) FROM QuerySyntaxEntity s HAVING ?1 < COUNT(DISTINCT ?2)");
            query.setParameter(1, 0);
            query.setParameter(2, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() ||  platform.isDerby()) {
                Assert.assertEquals("SELECT COUNT(1) FROM QUERYSYNTAXENTITY HAVING (0 < COUNT(DISTINCT(1)))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT COUNT(?) FROM QUERYSYNTAXENTITY HAVING (? < COUNT(DISTINCT(?)))", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Long> intParam2 = cb.parameter(Long.class);
            ParameterExpression<Integer> intParam3 = cb.parameter(Integer.class);
            cquery.multiselect(cb.count(intParam1));
            cquery.having(cb.lessThan(intParam2, cb.countDistinct(intParam3)));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 1);
            query.setParameter(intParam2, 0L);
            query.setParameter(intParam3, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() ||  platform.isDerby()) {
                Assert.assertEquals("SELECT COUNT(1) FROM QUERYSYNTAXENTITY HAVING (0 < COUNT(DISTINCT(1)))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT COUNT(?) FROM QUERYSYNTAXENTITY HAVING (? < COUNT(DISTINCT(?)))", _sql.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(cb2.count(cb2.literal(1)));
            cquery2.having(cb2.lessThan(cb2.literal(0L), cb2.countDistinct(cb2.literal(1))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT COUNT(1) FROM QUERYSYNTAXENTITY HAVING (0 < COUNT(DISTINCT(1)))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT COUNT(?) FROM QUERYSYNTAXENTITY HAVING (? < COUNT(DISTINCT(?)))", _sql.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(cb3.count(cb3.literal(1)));
            ParameterExpression<Long> intParam4 = cb3.parameter(Long.class);
            ParameterExpression<Integer> intParam5 = cb3.parameter(Integer.class);
            cquery3.having(cb3.lessThan(intParam4, cb3.countDistinct(intParam5)));

            query = em.createQuery(cquery3);
            query.setParameter(intParam4, 0L);
            query.setParameter(intParam5, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT COUNT(1) FROM QUERYSYNTAXENTITY HAVING (0 < COUNT(DISTINCT(1)))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT COUNT(?) FROM QUERYSYNTAXENTITY HAVING (? < COUNT(DISTINCT(?)))", _sql.remove(0));
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
    public void testCountDistinct2_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT COUNT(?1) FROM QuerySyntaxEntity s HAVING ?2 < COUNT(DISTINCT ?3)");
            query.setParameter(1, 1);
            query.setParameter(2, 0);
            query.setParameter(3, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT COUNT(1) FROM QUERYSYNTAXENTITY HAVING (? < COUNT(DISTINCT(1)))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT COUNT(?) FROM QUERYSYNTAXENTITY HAVING (? < COUNT(DISTINCT(?)))", _sql2.remove(0));
            }

            query = em.createQuery("SELECT COUNT(1) FROM QuerySyntaxEntity s HAVING 0 < COUNT(DISTINCT 1)");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT COUNT(1) FROM QUERYSYNTAXENTITY HAVING (? < COUNT(DISTINCT(1)))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT COUNT(?) FROM QUERYSYNTAXENTITY HAVING (? < COUNT(DISTINCT(?)))", _sql2.remove(0));
            }

            query = em.createQuery("SELECT COUNT(1) FROM QuerySyntaxEntity s HAVING ?1 < COUNT(DISTINCT ?2)");
            query.setParameter(1, 0);
            query.setParameter(2, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT COUNT(1) FROM QUERYSYNTAXENTITY HAVING (? < COUNT(DISTINCT(1)))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT COUNT(?) FROM QUERYSYNTAXENTITY HAVING (? < COUNT(DISTINCT(?)))", _sql2.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Long> intParam2 = cb.parameter(Long.class);
            ParameterExpression<Integer> intParam3 = cb.parameter(Integer.class);
            cquery.multiselect(cb.count(intParam1));
            cquery.having(cb.lessThan(intParam2, cb.countDistinct(intParam3)));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 1);
            query.setParameter(intParam2, 0L);
            query.setParameter(intParam3, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT COUNT(1) FROM QUERYSYNTAXENTITY HAVING (? < COUNT(DISTINCT(1)))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT COUNT(?) FROM QUERYSYNTAXENTITY HAVING (? < COUNT(DISTINCT(?)))", _sql2.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(cb2.count(cb2.literal(1)));
            cquery2.having(cb2.lessThan(cb2.literal(0L), cb2.countDistinct(cb2.literal(1))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT COUNT(1) FROM QUERYSYNTAXENTITY HAVING (? < COUNT(DISTINCT(1)))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT COUNT(?) FROM QUERYSYNTAXENTITY HAVING (? < COUNT(DISTINCT(?)))", _sql2.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(cb3.count(cb3.literal(1)));
            ParameterExpression<Long> intParam4 = cb3.parameter(Long.class);
            ParameterExpression<Integer> intParam5 = cb3.parameter(Integer.class);
            cquery3.having(cb3.lessThan(intParam4, cb3.countDistinct(intParam5)));

            query = em.createQuery(cquery3);
            query.setParameter(intParam4, 0L);
            query.setParameter(intParam5, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT COUNT(1) FROM QUERYSYNTAXENTITY HAVING (? < COUNT(DISTINCT(1)))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT COUNT(?) FROM QUERYSYNTAXENTITY HAVING (? < COUNT(DISTINCT(?)))", _sql2.remove(0));
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
    public void testCountDistinct2_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("SELECT COUNT(?1) FROM QuerySyntaxEntity s HAVING ?2 < COUNT(DISTINCT ?3)");
            query.setParameter(1, 1);
            query.setParameter(2, 0);
            query.setParameter(3, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT COUNT(1) FROM QUERYSYNTAXENTITY HAVING (? < COUNT(DISTINCT(1)))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT COUNT(?) FROM QUERYSYNTAXENTITY HAVING (? < COUNT(DISTINCT(?)))", _sql3.remove(0));
            }

            query = em.createQuery("SELECT COUNT(1) FROM QuerySyntaxEntity s HAVING 0 < COUNT(DISTINCT 1)");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT COUNT(1) FROM QUERYSYNTAXENTITY HAVING (? < COUNT(DISTINCT(1)))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT COUNT(?) FROM QUERYSYNTAXENTITY HAVING (? < COUNT(DISTINCT(?)))", _sql3.remove(0));
            }

            query = em.createQuery("SELECT COUNT(1) FROM QuerySyntaxEntity s HAVING ?1 < COUNT(DISTINCT ?2)");
            query.setParameter(1, 0);
            query.setParameter(2, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT COUNT(1) FROM QUERYSYNTAXENTITY HAVING (? < COUNT(DISTINCT(1)))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT COUNT(?) FROM QUERYSYNTAXENTITY HAVING (? < COUNT(DISTINCT(?)))", _sql3.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Long> intParam2 = cb.parameter(Long.class);
            ParameterExpression<Integer> intParam3 = cb.parameter(Integer.class);
            cquery.multiselect(cb.count(intParam1));
            cquery.having(cb.lessThan(intParam2, cb.countDistinct(intParam3)));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 1);
            query.setParameter(intParam2, 0L);
            query.setParameter(intParam3, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT COUNT(1) FROM QUERYSYNTAXENTITY HAVING (? < COUNT(DISTINCT(1)))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT COUNT(?) FROM QUERYSYNTAXENTITY HAVING (? < COUNT(DISTINCT(?)))", _sql3.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(cb2.count(cb2.literal(1)));
            cquery2.having(cb2.lessThan(cb2.literal(0L), cb2.countDistinct(cb2.literal(1))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT COUNT(1) FROM QUERYSYNTAXENTITY HAVING (? < COUNT(DISTINCT(1)))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT COUNT(?) FROM QUERYSYNTAXENTITY HAVING (? < COUNT(DISTINCT(?)))", _sql3.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(cb3.count(cb3.literal(1)));
            ParameterExpression<Long> intParam4 = cb3.parameter(Long.class);
            ParameterExpression<Integer> intParam5 = cb3.parameter(Integer.class);
            cquery3.having(cb3.lessThan(intParam4, cb3.countDistinct(intParam5)));

            query = em.createQuery(cquery3);
            query.setParameter(intParam4, 0L);
            query.setParameter(intParam5, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT COUNT(1) FROM QUERYSYNTAXENTITY HAVING (? < COUNT(DISTINCT(1)))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT COUNT(?) FROM QUERYSYNTAXENTITY HAVING (? < COUNT(DISTINCT(?)))", _sql3.remove(0));
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
    public void testSum1_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT SUM(?1) FROM QuerySyntaxEntity s");
            query.setParameter(1, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() ||  platform.isDerby()) {
                Assert.assertEquals("SELECT SUM(1) FROM QUERYSYNTAXENTITY", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT SUM(?) FROM QUERYSYNTAXENTITY", _sql.remove(0));
            }

            query = em.createQuery("SELECT SUM(1) FROM QuerySyntaxEntity s");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() ||  platform.isDerby()) {
                Assert.assertEquals("SELECT SUM(1) FROM QUERYSYNTAXENTITY", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT SUM(?) FROM QUERYSYNTAXENTITY", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            cquery.multiselect(cb.sum(intParam1));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() ||  platform.isDerby()) {
                Assert.assertEquals("SELECT SUM(1) FROM QUERYSYNTAXENTITY", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT SUM(?) FROM QUERYSYNTAXENTITY", _sql.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(cb2.sum(cb2.literal(1)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() ||  platform.isDerby()) {
                Assert.assertEquals("SELECT SUM(1) FROM QUERYSYNTAXENTITY", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT SUM(?) FROM QUERYSYNTAXENTITY", _sql.remove(0));
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
    public void testSum1_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT SUM(?1) FROM QuerySyntaxEntity s");
            query.setParameter(1, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT SUM(1) FROM QUERYSYNTAXENTITY", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT SUM(?) FROM QUERYSYNTAXENTITY", _sql2.remove(0));
            }

            query = em.createQuery("SELECT SUM(1) FROM QuerySyntaxEntity s");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT SUM(1) FROM QUERYSYNTAXENTITY", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT SUM(?) FROM QUERYSYNTAXENTITY", _sql2.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            cquery.multiselect(cb.sum(intParam1));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT SUM(1) FROM QUERYSYNTAXENTITY", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT SUM(?) FROM QUERYSYNTAXENTITY", _sql2.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(cb2.sum(cb2.literal(1)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT SUM(1) FROM QUERYSYNTAXENTITY", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT SUM(?) FROM QUERYSYNTAXENTITY", _sql2.remove(0));
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
    public void testSum1_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("SELECT SUM(?1) FROM QuerySyntaxEntity s");
            query.setParameter(1, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT SUM(1) FROM QUERYSYNTAXENTITY", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT SUM(?) FROM QUERYSYNTAXENTITY", _sql3.remove(0));
            }

            query = em.createQuery("SELECT SUM(1) FROM QuerySyntaxEntity s");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT SUM(1) FROM QUERYSYNTAXENTITY", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT SUM(?) FROM QUERYSYNTAXENTITY", _sql3.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            cquery.multiselect(cb.sum(intParam1));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT SUM(1) FROM QUERYSYNTAXENTITY", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT SUM(?) FROM QUERYSYNTAXENTITY", _sql3.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(cb2.sum(cb2.literal(1)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT SUM(1) FROM QUERYSYNTAXENTITY", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT SUM(?) FROM QUERYSYNTAXENTITY", _sql3.remove(0));
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
    public void testSum2_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT SUM(?1) FROM QuerySyntaxEntity s HAVING ?2 < SUM(?3)");
            query.setParameter(1, 1);
            query.setParameter(2, 0);
            query.setParameter(3, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() ||  platform.isDerby()) {
                Assert.assertEquals("SELECT SUM(1) FROM QUERYSYNTAXENTITY HAVING (0 < SUM(1))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT SUM(?) FROM QUERYSYNTAXENTITY HAVING (? < SUM(?))", _sql.remove(0));
            }

            query = em.createQuery("SELECT SUM(1) FROM QuerySyntaxEntity s HAVING 0 < SUM(1)");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() ||  platform.isDerby()) {
                Assert.assertEquals("SELECT SUM(1) FROM QUERYSYNTAXENTITY HAVING (0 < SUM(1))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT SUM(?) FROM QUERYSYNTAXENTITY HAVING (? < SUM(?))", _sql.remove(0));
            }

            query = em.createQuery("SELECT SUM(1) FROM QuerySyntaxEntity s HAVING ?1 < SUM(?2)");
            query.setParameter(1, 0);
            query.setParameter(2, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() ||  platform.isDerby()) {
                Assert.assertEquals("SELECT SUM(1) FROM QUERYSYNTAXENTITY HAVING (0 < SUM(1))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT SUM(?) FROM QUERYSYNTAXENTITY HAVING (? < SUM(?))", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam3 = cb.parameter(Integer.class);
            cquery.multiselect(cb.sum(intParam1));
            cquery.having(cb.lessThan(intParam2, cb.sum(intParam3)));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 1);
            query.setParameter(intParam2, 0);
            query.setParameter(intParam3, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() ||  platform.isDerby()) {
                Assert.assertEquals("SELECT SUM(1) FROM QUERYSYNTAXENTITY HAVING (0 < SUM(1))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT SUM(?) FROM QUERYSYNTAXENTITY HAVING (? < SUM(?))", _sql.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(cb2.sum(cb2.literal(1)));
            cquery2.having(cb2.lessThan(cb2.literal(0), cb2.sum(cb2.literal(1))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() ||  platform.isDerby()) {
                Assert.assertEquals("SELECT SUM(1) FROM QUERYSYNTAXENTITY HAVING (0 < SUM(1))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT SUM(?) FROM QUERYSYNTAXENTITY HAVING (? < SUM(?))", _sql.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(cb3.sum(cb3.literal(1)));
            ParameterExpression<Integer> intParam4 = cb3.parameter(Integer.class);
            ParameterExpression<Integer> intParam5 = cb3.parameter(Integer.class);
            cquery3.having(cb3.lessThan(intParam4, cb3.sum(intParam5)));

            query = em.createQuery(cquery3);
            query.setParameter(intParam4, 0);
            query.setParameter(intParam5, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT SUM(1) FROM QUERYSYNTAXENTITY HAVING (0 < SUM(1))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT SUM(?) FROM QUERYSYNTAXENTITY HAVING (? < SUM(?))", _sql.remove(0));
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
    public void testSum2_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT SUM(?1) FROM QuerySyntaxEntity s HAVING ?2 < SUM(?3)");
            query.setParameter(1, 1);
            query.setParameter(2, 0);
            query.setParameter(3, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT SUM(1) FROM QUERYSYNTAXENTITY HAVING (? < SUM(1))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT SUM(?) FROM QUERYSYNTAXENTITY HAVING (? < SUM(?))", _sql2.remove(0));
            }

            query = em.createQuery("SELECT SUM(1) FROM QuerySyntaxEntity s HAVING 0 < SUM(1)");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT SUM(1) FROM QUERYSYNTAXENTITY HAVING (? < SUM(1))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT SUM(?) FROM QUERYSYNTAXENTITY HAVING (? < SUM(?))", _sql2.remove(0));
            }

            query = em.createQuery("SELECT SUM(1) FROM QuerySyntaxEntity s HAVING ?1 < SUM(?2)");
            query.setParameter(1, 0);
            query.setParameter(2, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT SUM(1) FROM QUERYSYNTAXENTITY HAVING (? < SUM(1))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT SUM(?) FROM QUERYSYNTAXENTITY HAVING (? < SUM(?))", _sql2.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam3 = cb.parameter(Integer.class);
            cquery.multiselect(cb.sum(intParam1));
            cquery.having(cb.lessThan(intParam2, cb.sum(intParam3)));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 1);
            query.setParameter(intParam2, 0);
            query.setParameter(intParam3, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT SUM(1) FROM QUERYSYNTAXENTITY HAVING (? < SUM(1))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT SUM(?) FROM QUERYSYNTAXENTITY HAVING (? < SUM(?))", _sql2.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(cb2.sum(cb2.literal(1)));
            cquery2.having(cb2.lessThan(cb2.literal(0), cb2.sum(cb2.literal(1))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT SUM(1) FROM QUERYSYNTAXENTITY HAVING (? < SUM(1))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT SUM(?) FROM QUERYSYNTAXENTITY HAVING (? < SUM(?))", _sql2.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(cb3.sum(cb3.literal(1)));
            ParameterExpression<Integer> intParam4 = cb3.parameter(Integer.class);
            ParameterExpression<Integer> intParam5 = cb3.parameter(Integer.class);
            cquery3.having(cb3.lessThan(intParam4, cb3.sum(intParam5)));

            query = em.createQuery(cquery3);
            query.setParameter(intParam4, 0);
            query.setParameter(intParam5, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT SUM(1) FROM QUERYSYNTAXENTITY HAVING (? < SUM(1))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT SUM(?) FROM QUERYSYNTAXENTITY HAVING (? < SUM(?))", _sql2.remove(0));
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
    public void testSum2_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("SELECT SUM(?1) FROM QuerySyntaxEntity s HAVING ?2 < SUM(?3)");
            query.setParameter(1, 1);
            query.setParameter(2, 0);
            query.setParameter(3, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT SUM(1) FROM QUERYSYNTAXENTITY HAVING (? < SUM(1))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT SUM(?) FROM QUERYSYNTAXENTITY HAVING (? < SUM(?))", _sql3.remove(0));
            }

            query = em.createQuery("SELECT SUM(1) FROM QuerySyntaxEntity s HAVING 0 < SUM(1)");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT SUM(1) FROM QUERYSYNTAXENTITY HAVING (? < SUM(1))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT SUM(?) FROM QUERYSYNTAXENTITY HAVING (? < SUM(?))", _sql3.remove(0));
            }

            query = em.createQuery("SELECT SUM(1) FROM QuerySyntaxEntity s HAVING ?1 < SUM(?2)");
            query.setParameter(1, 0);
            query.setParameter(2, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT SUM(1) FROM QUERYSYNTAXENTITY HAVING (? < SUM(1))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT SUM(?) FROM QUERYSYNTAXENTITY HAVING (? < SUM(?))", _sql3.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam3 = cb.parameter(Integer.class);
            cquery.multiselect(cb.sum(intParam1));
            cquery.having(cb.lessThan(intParam2, cb.sum(intParam3)));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 1);
            query.setParameter(intParam2, 0);
            query.setParameter(intParam3, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT SUM(1) FROM QUERYSYNTAXENTITY HAVING (? < SUM(1))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT SUM(?) FROM QUERYSYNTAXENTITY HAVING (? < SUM(?))", _sql3.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(cb2.sum(cb2.literal(1)));
            cquery2.having(cb2.lessThan(cb2.literal(0), cb2.sum(cb2.literal(1))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT SUM(1) FROM QUERYSYNTAXENTITY HAVING (? < SUM(1))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT SUM(?) FROM QUERYSYNTAXENTITY HAVING (? < SUM(?))", _sql3.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(cb3.sum(cb3.literal(1)));
            ParameterExpression<Integer> intParam4 = cb3.parameter(Integer.class);
            ParameterExpression<Integer> intParam5 = cb3.parameter(Integer.class);
            cquery3.having(cb3.lessThan(intParam4, cb3.sum(intParam5)));

            query = em.createQuery(cquery3);
            query.setParameter(intParam4, 0);
            query.setParameter(intParam5, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT SUM(1) FROM QUERYSYNTAXENTITY HAVING (? < SUM(1))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT SUM(?) FROM QUERYSYNTAXENTITY HAVING (? < SUM(?))", _sql3.remove(0));
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
    public void testMax1_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT MAX(?1) FROM QuerySyntaxEntity s HAVING ?2 < MAX(?3)");
            query.setParameter(1, 1);
            query.setParameter(2, 0);
            query.setParameter(3, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT MAX(1) FROM QUERYSYNTAXENTITY HAVING (0 < MAX(1))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT MAX(?) FROM QUERYSYNTAXENTITY HAVING (? < MAX(?))", _sql.remove(0));
            }

            query = em.createQuery("SELECT MAX(1) FROM QuerySyntaxEntity s HAVING 0 < MAX(1)");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT MAX(1) FROM QUERYSYNTAXENTITY HAVING (0 < MAX(1))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT MAX(?) FROM QUERYSYNTAXENTITY HAVING (? < MAX(?))", _sql.remove(0));
            }

            query = em.createQuery("SELECT MAX(1) FROM QuerySyntaxEntity s HAVING ?1 < MAX(?2)");
            query.setParameter(1, 0);
            query.setParameter(2, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() ||  platform.isDerby()) {
                Assert.assertEquals("SELECT MAX(1) FROM QUERYSYNTAXENTITY HAVING (0 < MAX(1))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT MAX(?) FROM QUERYSYNTAXENTITY HAVING (? < MAX(?))", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam3 = cb.parameter(Integer.class);
            cquery.multiselect(cb.max(intParam1));
            cquery.having(cb.lessThan(intParam2, cb.max(intParam3)));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 1);
            query.setParameter(intParam2, 0);
            query.setParameter(intParam3, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() ||  platform.isDerby()) {
                Assert.assertEquals("SELECT MAX(1) FROM QUERYSYNTAXENTITY HAVING (0 < MAX(1))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT MAX(?) FROM QUERYSYNTAXENTITY HAVING (? < MAX(?))", _sql.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(cb2.max(cb2.literal(1)));
            cquery2.having(cb2.lessThan(cb2.literal(0), cb2.max(cb2.literal(1))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT MAX(1) FROM QUERYSYNTAXENTITY HAVING (0 < MAX(1))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT MAX(?) FROM QUERYSYNTAXENTITY HAVING (? < MAX(?))", _sql.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(cb3.max(cb3.literal(1)));
            ParameterExpression<Integer> intParam4 = cb3.parameter(Integer.class);
            ParameterExpression<Integer> intParam5 = cb3.parameter(Integer.class);
            cquery3.having(cb3.lessThan(intParam4, cb3.max(intParam5)));

            query = em.createQuery(cquery3);
            query.setParameter(intParam4, 0);
            query.setParameter(intParam5, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT MAX(1) FROM QUERYSYNTAXENTITY HAVING (0 < MAX(1))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT MAX(?) FROM QUERYSYNTAXENTITY HAVING (? < MAX(?))", _sql.remove(0));
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
    public void testMax1_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT MAX(?1) FROM QuerySyntaxEntity s HAVING ?2 < MAX(?3)");
            query.setParameter(1, 1);
            query.setParameter(2, 0);
            query.setParameter(3, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT MAX(1) FROM QUERYSYNTAXENTITY HAVING (? < MAX(1))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT MAX(?) FROM QUERYSYNTAXENTITY HAVING (? < MAX(?))", _sql2.remove(0));
            }

            query = em.createQuery("SELECT MAX(1) FROM QuerySyntaxEntity s HAVING 0 < MAX(1)");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT MAX(1) FROM QUERYSYNTAXENTITY HAVING (? < MAX(1))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT MAX(?) FROM QUERYSYNTAXENTITY HAVING (? < MAX(?))", _sql2.remove(0));
            }

            query = em.createQuery("SELECT MAX(1) FROM QuerySyntaxEntity s HAVING ?1 < MAX(?2)");
            query.setParameter(1, 0);
            query.setParameter(2, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT MAX(1) FROM QUERYSYNTAXENTITY HAVING (? < MAX(1))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT MAX(?) FROM QUERYSYNTAXENTITY HAVING (? < MAX(?))", _sql2.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam3 = cb.parameter(Integer.class);
            cquery.multiselect(cb.max(intParam1));
            cquery.having(cb.lessThan(intParam2, cb.max(intParam3)));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 1);
            query.setParameter(intParam2, 0);
            query.setParameter(intParam3, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT MAX(1) FROM QUERYSYNTAXENTITY HAVING (? < MAX(1))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT MAX(?) FROM QUERYSYNTAXENTITY HAVING (? < MAX(?))", _sql2.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(cb2.max(cb2.literal(1)));
            cquery2.having(cb2.lessThan(cb2.literal(0), cb2.max(cb2.literal(1))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT MAX(1) FROM QUERYSYNTAXENTITY HAVING (? < MAX(1))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT MAX(?) FROM QUERYSYNTAXENTITY HAVING (? < MAX(?))", _sql2.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(cb3.max(cb3.literal(1)));
            ParameterExpression<Integer> intParam4 = cb3.parameter(Integer.class);
            ParameterExpression<Integer> intParam5 = cb3.parameter(Integer.class);
            cquery3.having(cb3.lessThan(intParam4, cb3.max(intParam5)));

            query = em.createQuery(cquery3);
            query.setParameter(intParam4, 0);
            query.setParameter(intParam5, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT MAX(1) FROM QUERYSYNTAXENTITY HAVING (? < MAX(1))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT MAX(?) FROM QUERYSYNTAXENTITY HAVING (? < MAX(?))", _sql2.remove(0));
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
    public void testMax1_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("SELECT MAX(?1) FROM QuerySyntaxEntity s HAVING ?2 < MAX(?3)");
            query.setParameter(1, 1);
            query.setParameter(2, 0);
            query.setParameter(3, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT MAX(1) FROM QUERYSYNTAXENTITY HAVING (? < MAX(1))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT MAX(?) FROM QUERYSYNTAXENTITY HAVING (? < MAX(?))", _sql3.remove(0));
            }

            query = em.createQuery("SELECT MAX(1) FROM QuerySyntaxEntity s HAVING 0 < MAX(1)");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT MAX(1) FROM QUERYSYNTAXENTITY HAVING (? < MAX(1))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT MAX(?) FROM QUERYSYNTAXENTITY HAVING (? < MAX(?))", _sql3.remove(0));
            }

            query = em.createQuery("SELECT MAX(1) FROM QuerySyntaxEntity s HAVING ?1 < MAX(?2)");
            query.setParameter(1, 0);
            query.setParameter(2, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT MAX(1) FROM QUERYSYNTAXENTITY HAVING (? < MAX(1))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT MAX(?) FROM QUERYSYNTAXENTITY HAVING (? < MAX(?))", _sql3.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam3 = cb.parameter(Integer.class);
            cquery.multiselect(cb.max(intParam1));
            cquery.having(cb.lessThan(intParam2, cb.max(intParam3)));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 1);
            query.setParameter(intParam2, 0);
            query.setParameter(intParam3, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT MAX(1) FROM QUERYSYNTAXENTITY HAVING (? < MAX(1))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT MAX(?) FROM QUERYSYNTAXENTITY HAVING (? < MAX(?))", _sql3.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(cb2.max(cb2.literal(1)));
            cquery2.having(cb2.lessThan(cb2.literal(0), cb2.max(cb2.literal(1))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT MAX(1) FROM QUERYSYNTAXENTITY HAVING (? < MAX(1))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT MAX(?) FROM QUERYSYNTAXENTITY HAVING (? < MAX(?))", _sql3.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(cb3.max(cb3.literal(1)));
            ParameterExpression<Integer> intParam4 = cb3.parameter(Integer.class);
            ParameterExpression<Integer> intParam5 = cb3.parameter(Integer.class);
            cquery3.having(cb3.lessThan(intParam4, cb3.max(intParam5)));

            query = em.createQuery(cquery3);
            query.setParameter(intParam4, 0);
            query.setParameter(intParam5, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT MAX(1) FROM QUERYSYNTAXENTITY HAVING (? < MAX(1))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT MAX(?) FROM QUERYSYNTAXENTITY HAVING (? < MAX(?))", _sql3.remove(0));
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
    public void testMax2_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s GROUP BY s.intVal1 HAVING MAX(s.intVal1) > ?2");
            query.setParameter(2, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (MAX(INTVAL1) > ?)", _sql.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s GROUP BY s.intVal1 HAVING MAX(s.intVal1) > 8");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (MAX(INTVAL1) > ?)", _sql.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> longValue = cb.parameter(Integer.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));
            cquery.groupBy(root.get(QuerySyntaxEntity_.intVal1));
            cquery.having(cb.greaterThan(cb.max(root.get(QuerySyntaxEntity_.intVal1)), longValue));

            query = em.createQuery(cquery);
            query.setParameter(longValue, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (MAX(INTVAL1) > ?)", _sql.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.groupBy(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.having(cb2.greaterThan(cb2.max(root2.get(QuerySyntaxEntity_.intVal1)), 8));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (MAX(INTVAL1) > ?)", _sql.remove(0));
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
    public void testMax2_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s GROUP BY s.intVal1 HAVING MAX(s.intVal1) > ?2");
            query.setParameter(2, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (MAX(INTVAL1) > ?)", _sql2.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s GROUP BY s.intVal1 HAVING MAX(s.intVal1) > 8");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (MAX(INTVAL1) > ?)", _sql2.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> longValue = cb.parameter(Integer.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));
            cquery.groupBy(root.get(QuerySyntaxEntity_.intVal1));
            cquery.having(cb.greaterThan(cb.max(root.get(QuerySyntaxEntity_.intVal1)), longValue));

            query = em.createQuery(cquery);
            query.setParameter(longValue, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (MAX(INTVAL1) > ?)", _sql2.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.groupBy(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.having(cb2.greaterThan(cb2.max(root2.get(QuerySyntaxEntity_.intVal1)), 8));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (MAX(INTVAL1) > ?)", _sql2.remove(0));
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
    public void testMax2_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s GROUP BY s.intVal1 HAVING MAX(s.intVal1) > ?2");
            query.setParameter(2, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (MAX(INTVAL1) > ?)", _sql3.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s GROUP BY s.intVal1 HAVING MAX(s.intVal1) > 8");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (MAX(INTVAL1) > ?)", _sql3.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> longValue = cb.parameter(Integer.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));
            cquery.groupBy(root.get(QuerySyntaxEntity_.intVal1));
            cquery.having(cb.greaterThan(cb.max(root.get(QuerySyntaxEntity_.intVal1)), longValue));

            query = em.createQuery(cquery);
            query.setParameter(longValue, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (MAX(INTVAL1) > ?)", _sql3.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.groupBy(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.having(cb2.greaterThan(cb2.max(root2.get(QuerySyntaxEntity_.intVal1)), 8));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (MAX(INTVAL1) > ?)", _sql3.remove(0));
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
    public void testMin1_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT MIN(?1) FROM QuerySyntaxEntity s HAVING ?2 < MIN(?3)");
            query.setParameter(1, 1);
            query.setParameter(2, 0);
            query.setParameter(3, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT MIN(1) FROM QUERYSYNTAXENTITY HAVING (0 < MIN(1))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT MIN(?) FROM QUERYSYNTAXENTITY HAVING (? < MIN(?))", _sql.remove(0));
            }

            query = em.createQuery("SELECT MIN(1) FROM QuerySyntaxEntity s HAVING 0 < MIN(1)");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT MIN(1) FROM QUERYSYNTAXENTITY HAVING (0 < MIN(1))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT MIN(?) FROM QUERYSYNTAXENTITY HAVING (? < MIN(?))", _sql.remove(0));
            }

            query = em.createQuery("SELECT MIN(1) FROM QuerySyntaxEntity s HAVING ?1 < MIN(?2)");
            query.setParameter(1, 0);
            query.setParameter(2, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() ||  platform.isDerby()) {
                Assert.assertEquals("SELECT MIN(1) FROM QUERYSYNTAXENTITY HAVING (0 < MIN(1))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT MIN(?) FROM QUERYSYNTAXENTITY HAVING (? < MIN(?))", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam3 = cb.parameter(Integer.class);
            cquery.multiselect(cb.min(intParam1));
            cquery.having(cb.lessThan(intParam2, cb.min(intParam3)));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 1);
            query.setParameter(intParam2, 0);
            query.setParameter(intParam3, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() ||  platform.isDerby()) {
                Assert.assertEquals("SELECT MIN(1) FROM QUERYSYNTAXENTITY HAVING (0 < MIN(1))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT MIN(?) FROM QUERYSYNTAXENTITY HAVING (? < MIN(?))", _sql.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(cb2.min(cb2.literal(1)));
            cquery2.having(cb2.lessThan(cb2.literal(0), cb2.min(cb2.literal(1))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT MIN(1) FROM QUERYSYNTAXENTITY HAVING (0 < MIN(1))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT MIN(?) FROM QUERYSYNTAXENTITY HAVING (? < MIN(?))", _sql.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(cb3.min(cb3.literal(1)));
            ParameterExpression<Integer> intParam4 = cb3.parameter(Integer.class);
            ParameterExpression<Integer> intParam5 = cb3.parameter(Integer.class);
            cquery3.having(cb3.lessThan(intParam4, cb3.min(intParam5)));

            query = em.createQuery(cquery3);
            query.setParameter(intParam4, 0);
            query.setParameter(intParam5, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT MIN(1) FROM QUERYSYNTAXENTITY HAVING (0 < MIN(1))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT MIN(?) FROM QUERYSYNTAXENTITY HAVING (? < MIN(?))", _sql.remove(0));
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
    public void testMin1_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT MIN(?1) FROM QuerySyntaxEntity s HAVING ?2 < MIN(?3)");
            query.setParameter(1, 1);
            query.setParameter(2, 0);
            query.setParameter(3, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT MIN(1) FROM QUERYSYNTAXENTITY HAVING (? < MIN(1))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT MIN(?) FROM QUERYSYNTAXENTITY HAVING (? < MIN(?))", _sql2.remove(0));
            }

            query = em.createQuery("SELECT MIN(1) FROM QuerySyntaxEntity s HAVING 0 < MIN(1)");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT MIN(1) FROM QUERYSYNTAXENTITY HAVING (? < MIN(1))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT MIN(?) FROM QUERYSYNTAXENTITY HAVING (? < MIN(?))", _sql2.remove(0));
            }

            query = em.createQuery("SELECT MIN(1) FROM QuerySyntaxEntity s HAVING ?1 < MIN(?2)");
            query.setParameter(1, 0);
            query.setParameter(2, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT MIN(1) FROM QUERYSYNTAXENTITY HAVING (? < MIN(1))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT MIN(?) FROM QUERYSYNTAXENTITY HAVING (? < MIN(?))", _sql2.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam3 = cb.parameter(Integer.class);
            cquery.multiselect(cb.min(intParam1));
            cquery.having(cb.lessThan(intParam2, cb.min(intParam3)));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 1);
            query.setParameter(intParam2, 0);
            query.setParameter(intParam3, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT MIN(1) FROM QUERYSYNTAXENTITY HAVING (? < MIN(1))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT MIN(?) FROM QUERYSYNTAXENTITY HAVING (? < MIN(?))", _sql2.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(cb2.min(cb2.literal(1)));
            cquery2.having(cb2.lessThan(cb2.literal(0), cb2.min(cb2.literal(1))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT MIN(1) FROM QUERYSYNTAXENTITY HAVING (? < MIN(1))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT MIN(?) FROM QUERYSYNTAXENTITY HAVING (? < MIN(?))", _sql2.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(cb3.min(cb3.literal(1)));
            ParameterExpression<Integer> intParam4 = cb3.parameter(Integer.class);
            ParameterExpression<Integer> intParam5 = cb3.parameter(Integer.class);
            cquery3.having(cb3.lessThan(intParam4, cb3.min(intParam5)));

            query = em.createQuery(cquery3);
            query.setParameter(intParam4, 0);
            query.setParameter(intParam5, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT MIN(1) FROM QUERYSYNTAXENTITY HAVING (? < MIN(1))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT MIN(?) FROM QUERYSYNTAXENTITY HAVING (? < MIN(?))", _sql2.remove(0));
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
    public void testMin1_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("SELECT MIN(?1) FROM QuerySyntaxEntity s HAVING ?2 < MIN(?3)");
            query.setParameter(1, 1);
            query.setParameter(2, 0);
            query.setParameter(3, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT MIN(1) FROM QUERYSYNTAXENTITY HAVING (? < MIN(1))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT MIN(?) FROM QUERYSYNTAXENTITY HAVING (? < MIN(?))", _sql3.remove(0));
            }

            query = em.createQuery("SELECT MIN(1) FROM QuerySyntaxEntity s HAVING 0 < MIN(1)");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT MIN(1) FROM QUERYSYNTAXENTITY HAVING (? < MIN(1))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT MIN(?) FROM QUERYSYNTAXENTITY HAVING (? < MIN(?))", _sql3.remove(0));
            }

            query = em.createQuery("SELECT MIN(1) FROM QuerySyntaxEntity s HAVING ?1 < MIN(?2)");
            query.setParameter(1, 0);
            query.setParameter(2, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT MIN(1) FROM QUERYSYNTAXENTITY HAVING (? < MIN(1))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT MIN(?) FROM QUERYSYNTAXENTITY HAVING (? < MIN(?))", _sql3.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam3 = cb.parameter(Integer.class);
            cquery.multiselect(cb.min(intParam1));
            cquery.having(cb.lessThan(intParam2, cb.min(intParam3)));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 1);
            query.setParameter(intParam2, 0);
            query.setParameter(intParam3, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT MIN(1) FROM QUERYSYNTAXENTITY HAVING (? < MIN(1))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT MIN(?) FROM QUERYSYNTAXENTITY HAVING (? < MIN(?))", _sql3.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(cb2.min(cb2.literal(1)));
            cquery2.having(cb2.lessThan(cb2.literal(0), cb2.min(cb2.literal(1))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT MIN(1) FROM QUERYSYNTAXENTITY HAVING (? < MIN(1))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT MIN(?) FROM QUERYSYNTAXENTITY HAVING (? < MIN(?))", _sql3.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(cb3.min(cb3.literal(1)));
            ParameterExpression<Integer> intParam4 = cb3.parameter(Integer.class);
            ParameterExpression<Integer> intParam5 = cb3.parameter(Integer.class);
            cquery3.having(cb3.lessThan(intParam4, cb3.min(intParam5)));

            query = em.createQuery(cquery3);
            query.setParameter(intParam4, 0);
            query.setParameter(intParam5, 1);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT MIN(1) FROM QUERYSYNTAXENTITY HAVING (? < MIN(1))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT MIN(?) FROM QUERYSYNTAXENTITY HAVING (? < MIN(?))", _sql3.remove(0));
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
    public void testMin2_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s GROUP BY s.intVal1 HAVING MIN(s.intVal1) > ?2");
            query.setParameter(2, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (MIN(INTVAL1) > ?)", _sql.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s GROUP BY s.intVal1 HAVING MIN(s.intVal1) > 8");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (MIN(INTVAL1) > ?)", _sql.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> longValue = cb.parameter(Integer.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));
            cquery.groupBy(root.get(QuerySyntaxEntity_.intVal1));
            cquery.having(cb.greaterThan(cb.min(root.get(QuerySyntaxEntity_.intVal1)), longValue));

            query = em.createQuery(cquery);
            query.setParameter(longValue, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (MIN(INTVAL1) > ?)", _sql.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.groupBy(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.having(cb2.greaterThan(cb2.min(root2.get(QuerySyntaxEntity_.intVal1)), 8));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (MIN(INTVAL1) > ?)", _sql.remove(0));
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
    public void testMin2_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s GROUP BY s.intVal1 HAVING MIN(s.intVal1) > ?2");
            query.setParameter(2, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (MIN(INTVAL1) > ?)", _sql2.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s GROUP BY s.intVal1 HAVING MIN(s.intVal1) > 8");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (MIN(INTVAL1) > ?)", _sql2.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> longValue = cb.parameter(Integer.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));
            cquery.groupBy(root.get(QuerySyntaxEntity_.intVal1));
            cquery.having(cb.greaterThan(cb.min(root.get(QuerySyntaxEntity_.intVal1)), longValue));

            query = em.createQuery(cquery);
            query.setParameter(longValue, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (MIN(INTVAL1) > ?)", _sql2.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.groupBy(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.having(cb2.greaterThan(cb2.min(root2.get(QuerySyntaxEntity_.intVal1)), 8));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (MIN(INTVAL1) > ?)", _sql2.remove(0));
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
    public void testMin2_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s GROUP BY s.intVal1 HAVING MIN(s.intVal1) > ?2");
            query.setParameter(2, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (MIN(INTVAL1) > ?)", _sql3.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s GROUP BY s.intVal1 HAVING MIN(s.intVal1) > 8");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (MIN(INTVAL1) > ?)", _sql3.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> longValue = cb.parameter(Integer.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));
            cquery.groupBy(root.get(QuerySyntaxEntity_.intVal1));
            cquery.having(cb.greaterThan(cb.min(root.get(QuerySyntaxEntity_.intVal1)), longValue));

            query = em.createQuery(cquery);
            query.setParameter(longValue, 8);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (MIN(INTVAL1) > ?)", _sql3.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.groupBy(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.having(cb2.greaterThan(cb2.min(root2.get(QuerySyntaxEntity_.intVal1)), 8));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY GROUP BY INTVAL1 HAVING (MIN(INTVAL1) > ?)", _sql3.remove(0));
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
    public void testDistinct1_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT DISTINCT(?1) FROM QuerySyntaxEntity s WHERE s.strVal1 = ?2");
            query.setParameter(1, "HELLO");
            query.setParameter(2, "WORLD");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() ||  platform.isDerby()) {
                Assert.assertEquals("SELECT DISTINCT 'HELLO' FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = 'WORLD')", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT DISTINCT ? FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = ?)", _sql.remove(0));
            }

            query = em.createQuery("SELECT DISTINCT('HELLO') FROM QuerySyntaxEntity s WHERE s.strVal1 = 'WORLD'");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() ||  platform.isDerby()) {
                Assert.assertEquals("SELECT DISTINCT 'HELLO' FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = 'WORLD')", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT DISTINCT ? FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = ?)", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            ParameterExpression<String> strParam2 = cb.parameter(String.class);
            cquery.multiselect(strParam1).distinct(true);
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.strVal1), strParam2));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "HELLO");
            query.setParameter(strParam2, "WORLD");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() ||  platform.isDerby()) {
                Assert.assertEquals("SELECT DISTINCT 'HELLO' FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = 'WORLD')", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT DISTINCT ? FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = ?)", _sql.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(cb2.literal("HELLO")).distinct(true);
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.strVal1), "WORLD"));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() ||  platform.isDerby()) {
                Assert.assertEquals("SELECT DISTINCT 'HELLO' FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = 'WORLD')", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT DISTINCT ? FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = ?)", _sql.remove(0));
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
    public void testDistinct1_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT DISTINCT(?1) FROM QuerySyntaxEntity s WHERE s.strVal1 = ?2");
            query.setParameter(1, "HELLO");
            query.setParameter(2, "WORLD");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT DISTINCT 'HELLO' FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = ?)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT DISTINCT ? FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = ?)", _sql2.remove(0));
            }

            query = em.createQuery("SELECT DISTINCT('HELLO') FROM QuerySyntaxEntity s WHERE s.strVal1 = 'WORLD'");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT DISTINCT 'HELLO' FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = ?)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT DISTINCT ? FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = ?)", _sql2.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            ParameterExpression<String> strParam2 = cb.parameter(String.class);
            cquery.multiselect(strParam1).distinct(true);
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.strVal1), strParam2));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "HELLO");
            query.setParameter(strParam2, "WORLD");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT DISTINCT 'HELLO' FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = ?)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT DISTINCT ? FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = ?)", _sql2.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(cb2.literal("HELLO")).distinct(true);
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.strVal1), "WORLD"));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT DISTINCT 'HELLO' FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = ?)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT DISTINCT ? FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = ?)", _sql2.remove(0));
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
    public void testDistinct1_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("SELECT DISTINCT(?1) FROM QuerySyntaxEntity s WHERE s.strVal1 = ?2");
            query.setParameter(1, "HELLO");
            query.setParameter(2, "WORLD");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT DISTINCT 'HELLO' FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = ?)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT DISTINCT ? FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = ?)", _sql3.remove(0));
            }

            query = em.createQuery("SELECT DISTINCT('HELLO') FROM QuerySyntaxEntity s WHERE s.strVal1 = 'WORLD'");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT DISTINCT 'HELLO' FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = ?)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT DISTINCT ? FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = ?)", _sql3.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            ParameterExpression<String> strParam2 = cb.parameter(String.class);
            cquery.multiselect(strParam1).distinct(true);
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.strVal1), strParam2));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "HELLO");
            query.setParameter(strParam2, "WORLD");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT DISTINCT 'HELLO' FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = ?)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT DISTINCT ? FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = ?)", _sql3.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(cb2.literal("HELLO")).distinct(true);
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.strVal1), "WORLD"));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT DISTINCT 'HELLO' FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = ?)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT DISTINCT ? FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = ?)", _sql3.remove(0));
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
