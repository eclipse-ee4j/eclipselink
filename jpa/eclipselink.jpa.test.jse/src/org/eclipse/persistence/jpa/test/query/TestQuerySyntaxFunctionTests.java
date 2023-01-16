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
import javax.persistence.criteria.CriteriaBuilder.Trimspec;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

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
public class TestQuerySyntaxFunctionTests {

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
    public void testUpper1_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = UPPER(?1)");
            query.setParameter(1, "HELLO WORLD");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = UCASE('HELLO WORLD'))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = UPPER(?))", _sql.remove(0));
            }

            query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = UPPER('HELLO WORLD')");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = UCASE('HELLO WORLD'))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = UPPER(?))", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.strVal1));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.strVal1), cb.upper(strParam1)));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "HELLO WORLD");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = UCASE('HELLO WORLD'))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = UPPER(?))", _sql.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.strVal1));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.strVal1), cb2.upper(cb2.literal("HELLO WORLD"))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = UCASE('HELLO WORLD'))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = UPPER(?))", _sql.remove(0));
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
    public void testUpper1_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = UPPER(?1)");
            query.setParameter(1, "HELLO WORLD");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = UCASE(?))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = UPPER(?))", _sql2.remove(0));
            }

            query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = UPPER('HELLO WORLD')");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = UCASE('HELLO WORLD'))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = UPPER(?))", _sql2.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.strVal1));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.strVal1), cb.upper(strParam1)));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "HELLO WORLD");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = UCASE(?))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = UPPER(?))", _sql2.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.strVal1));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.strVal1), cb2.upper(cb2.literal("HELLO WORLD"))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = UCASE('HELLO WORLD'))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = UPPER(?))", _sql2.remove(0));
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
    public void testUpper1_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = UPPER(?1)");
            query.setParameter(1, "HELLO WORLD");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = UCASE(?))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = UPPER(?))", _sql3.remove(0));
            }

            query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = UPPER('HELLO WORLD')");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = UCASE(?))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = UPPER(?))", _sql3.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.strVal1));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.strVal1), cb.upper(strParam1)));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "HELLO WORLD");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = UCASE(?))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = UPPER(?))", _sql3.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.strVal1));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.strVal1), cb2.upper(cb2.literal("HELLO WORLD"))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = UCASE(?))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = UPPER(?))", _sql3.remove(0));
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
    public void testLower1_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = LOWER(?1)");
            query.setParameter(1, "HELLO WORLD");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LCASE('HELLO WORLD'))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LOWER(?))", _sql.remove(0));
            }

            query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = LOWER('HELLO WORLD')");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LCASE('HELLO WORLD'))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LOWER(?))", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.strVal1));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.strVal1), cb.lower(strParam1)));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "HELLO WORLD");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LCASE('HELLO WORLD'))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LOWER(?))", _sql.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.strVal1));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.strVal1), cb2.lower(cb2.literal("HELLO WORLD"))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LCASE('HELLO WORLD'))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LOWER(?))", _sql.remove(0));
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
    public void testLower1_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = LOWER(?1)");
            query.setParameter(1, "HELLO WORLD");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LCASE(?))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LOWER(?))", _sql2.remove(0));
            }

            query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = LOWER('HELLO WORLD')");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LCASE('HELLO WORLD'))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LOWER(?))", _sql2.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.strVal1));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.strVal1), cb.lower(strParam1)));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "HELLO WORLD");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LCASE(?))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LOWER(?))", _sql2.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.strVal1));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.strVal1), cb2.lower(cb2.literal("HELLO WORLD"))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LCASE('HELLO WORLD'))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LOWER(?))", _sql2.remove(0));
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
    public void testLower1_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = LOWER(?1)");
            query.setParameter(1, "HELLO WORLD");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LCASE(?))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LOWER(?))", _sql3.remove(0));
            }

            query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = LOWER('HELLO WORLD')");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LCASE(?))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LOWER(?))", _sql3.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.strVal1));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.strVal1), cb.lower(strParam1)));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "HELLO WORLD");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LCASE(?))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LOWER(?))", _sql3.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.strVal1));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.strVal1), cb2.lower(cb2.literal("HELLO WORLD"))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LCASE(?))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LOWER(?))", _sql3.remove(0));
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
    public void testConcat1_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = CONCAT(?1, ?2, ?3)");
            query.setParameter(1, "HELLO");
            query.setParameter(2, " ");
            query.setParameter(3, "WORLD");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = VARCHAR(VARCHAR('HELLO' || ' ') || 'WORLD'))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = CONCAT(CONCAT(?, ?), ?))", _sql.remove(0));
            }

            query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = CONCAT('HELLO', ' ', 'WORLD')");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = VARCHAR(VARCHAR('HELLO' || ' ') || 'WORLD'))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = CONCAT(CONCAT(?, ?), ?))", _sql.remove(0));
            }

            query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = CONCAT(?1, ' ', s.strVal2)");
            query.setParameter(1, "HELLO");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = VARCHAR(VARCHAR('HELLO' || ' ') || STRVAL2))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = CONCAT(CONCAT(?, ?), STRVAL2))", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            ParameterExpression<String> strParam2 = cb.parameter(String.class);
            ParameterExpression<String> strParam3 = cb.parameter(String.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.strVal1));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.strVal1), cb.concat(cb.concat(strParam1, strParam2), strParam3)));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "HELLO");
            query.setParameter(strParam2, " ");
            query.setParameter(strParam3, "WORLD");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = VARCHAR(VARCHAR('HELLO' || ' ') || 'WORLD'))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = CONCAT(CONCAT(?, ?), ?))", _sql.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.strVal1));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.strVal1), cb2.concat(cb2.concat("HELLO", cb2.literal(" ")), "WORLD")));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = VARCHAR(VARCHAR('HELLO' || ' ') || 'WORLD'))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = CONCAT(CONCAT(?, ?), ?))", _sql.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.strVal1));
            ParameterExpression<String> strParam4 = cb3.parameter(String.class);
            cquery3.where(cb3.equal(root3.get(QuerySyntaxEntity_.strVal1), cb3.concat(strParam4, cb3.concat(" ", root3.get(QuerySyntaxEntity_.strVal2)))));

            query = em.createQuery(cquery3);
            query.setParameter(strParam4, "HELLO");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = VARCHAR(? || VARCHAR(' ' || STRVAL2)))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = CONCAT(?, CONCAT(?, STRVAL2)))", _sql.remove(0));
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
    public void testConcat1_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = CONCAT(?1, ?2, ?3)");
            query.setParameter(1, "HELLO");
            query.setParameter(2, " ");
            query.setParameter(3, "WORLD");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = VARCHAR(VARCHAR(? || ' ') || ?))", _sql2.remove(0));
            } else if(platform.isDB2()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = VARCHAR(VARCHAR(? || ?) || ?))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = CONCAT(CONCAT(?, ?), ?))", _sql2.remove(0));
            }

            query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = CONCAT('HELLO', ' ', 'WORLD')");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = VARCHAR(VARCHAR('HELLO' || ' ') || 'WORLD'))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = CONCAT(CONCAT(?, ?), ?))", _sql2.remove(0));
            }

            query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = CONCAT(?1, ' ', s.strVal2)");
            query.setParameter(1, "HELLO");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = VARCHAR(VARCHAR(? || ' ') || STRVAL2))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = CONCAT(CONCAT(?, ?), STRVAL2))", _sql2.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            ParameterExpression<String> strParam2 = cb.parameter(String.class);
            ParameterExpression<String> strParam3 = cb.parameter(String.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.strVal1));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.strVal1), cb.concat(cb.concat(strParam1, strParam2), strParam3)));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "HELLO");
            query.setParameter(strParam2, " ");
            query.setParameter(strParam3, "WORLD");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = VARCHAR(VARCHAR(? || ' ') || ?))", _sql2.remove(0));
            } else if(platform.isDB2()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = VARCHAR(VARCHAR(? || ?) || ?))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = CONCAT(CONCAT(?, ?), ?))", _sql2.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.strVal1));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.strVal1), cb2.concat(cb2.concat("HELLO", cb2.literal(" ")), "WORLD")));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = VARCHAR(VARCHAR('HELLO' || ' ') || 'WORLD'))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = CONCAT(CONCAT(?, ?), ?))", _sql2.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.strVal1));
            ParameterExpression<String> strParam4 = cb3.parameter(String.class);
            cquery3.where(cb3.equal(root3.get(QuerySyntaxEntity_.strVal1), cb3.concat(strParam4, cb3.concat(" ", root3.get(QuerySyntaxEntity_.strVal2)))));

            query = em.createQuery(cquery3);
            query.setParameter(strParam4, "HELLO");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = VARCHAR(? || VARCHAR(' ' || STRVAL2)))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = CONCAT(?, CONCAT(?, STRVAL2)))", _sql2.remove(0));
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
    public void testConcat1_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = CONCAT(?1, ?2, ?3)");
            query.setParameter(1, "HELLO");
            query.setParameter(2, " ");
            query.setParameter(3, "WORLD");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = VARCHAR(VARCHAR(? || ' ') || ?))", _sql3.remove(0));
            } else if(platform.isDB2()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = VARCHAR(VARCHAR(? || ?) || ?))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = CONCAT(CONCAT(?, ?), ?))", _sql3.remove(0));
            }

            query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = CONCAT('HELLO', ' ', 'WORLD')");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = VARCHAR(VARCHAR(? || ' ') || ?))", _sql3.remove(0));
            } else if(platform.isDB2()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = VARCHAR(VARCHAR(? || ?) || ?))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = CONCAT(CONCAT(?, ?), ?))", _sql3.remove(0));
            }

            query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = CONCAT(?1, ' ', s.strVal2)");
            query.setParameter(1, "HELLO");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = VARCHAR(VARCHAR(? || ' ') || STRVAL2))", _sql3.remove(0));
            } else if(platform.isDB2()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = VARCHAR(VARCHAR(? || ?) || STRVAL2))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = CONCAT(CONCAT(?, ?), STRVAL2))", _sql3.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            ParameterExpression<String> strParam2 = cb.parameter(String.class);
            ParameterExpression<String> strParam3 = cb.parameter(String.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.strVal1));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.strVal1), cb.concat(cb.concat(strParam1, strParam2), strParam3)));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "HELLO");
            query.setParameter(strParam2, " ");
            query.setParameter(strParam3, "WORLD");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = VARCHAR(VARCHAR(? || ' ') || ?))", _sql3.remove(0));
            } else if(platform.isDB2()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = VARCHAR(VARCHAR(? || ?) || ?))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = CONCAT(CONCAT(?, ?), ?))", _sql3.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.strVal1));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.strVal1), cb2.concat(cb2.concat("HELLO", cb2.literal(" ")), "WORLD")));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = VARCHAR(VARCHAR(? || ' ') || ?))", _sql3.remove(0));
            } else if(platform.isDB2()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = VARCHAR(VARCHAR(? || ?) || ?))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = CONCAT(CONCAT(?, ?), ?))", _sql3.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.strVal1));
            ParameterExpression<String> strParam4 = cb3.parameter(String.class);
            cquery3.where(cb3.equal(root3.get(QuerySyntaxEntity_.strVal1), cb3.concat(strParam4, cb3.concat(" ", root3.get(QuerySyntaxEntity_.strVal2)))));

            query = em.createQuery(cquery3);
            query.setParameter(strParam4, "HELLO");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = VARCHAR(? || VARCHAR(? || STRVAL2)))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = CONCAT(?, CONCAT(?, STRVAL2)))", _sql3.remove(0));
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
    public void testConcat2_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT CONCAT(s.strVal1, ?1) FROM QuerySyntaxEntity s");
            query.setParameter(1, "HELLO");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT VARCHAR(STRVAL1 || ?) FROM QUERYSYNTAXENTITY", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT CONCAT(STRVAL1, ?) FROM QUERYSYNTAXENTITY", _sql.remove(0));
            }

            query = em.createQuery("SELECT CONCAT(s.strVal1, 'HELLO') FROM QuerySyntaxEntity s");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT VARCHAR(STRVAL1 || 'HELLO') FROM QUERYSYNTAXENTITY", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT CONCAT(STRVAL1, ?) FROM QUERYSYNTAXENTITY", _sql.remove(0));
            }

            query = em.createQuery("SELECT CONCAT(s.strVal1, 'HELLO', ?1) FROM QuerySyntaxEntity s");
            query.setParameter(1, "WORLD");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT VARCHAR(VARCHAR(STRVAL1 || 'HELLO') || ?) FROM QUERYSYNTAXENTITY", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT CONCAT(CONCAT(STRVAL1, ?), ?) FROM QUERYSYNTAXENTITY", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            cquery.multiselect(cb.concat(root.get(QuerySyntaxEntity_.strVal1), strParam1));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "HELLO");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT VARCHAR(STRVAL1 || ?) FROM QUERYSYNTAXENTITY", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT CONCAT(STRVAL1, ?) FROM QUERYSYNTAXENTITY", _sql.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(cb2.concat(root2.get(QuerySyntaxEntity_.strVal1), "HELLO"));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT VARCHAR(STRVAL1 || 'HELLO') FROM QUERYSYNTAXENTITY", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT CONCAT(STRVAL1, ?) FROM QUERYSYNTAXENTITY", _sql.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam2 = cb3.parameter(String.class);
            cquery3.multiselect(cb3.concat(cb3.concat(root3.get(QuerySyntaxEntity_.strVal1), "HELLO"), strParam2));

            query = em.createQuery(cquery3);
            query.setParameter(strParam2, "WORLD");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT VARCHAR(VARCHAR(STRVAL1 || 'HELLO') || ?) FROM QUERYSYNTAXENTITY", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT CONCAT(CONCAT(STRVAL1, ?), ?) FROM QUERYSYNTAXENTITY", _sql.remove(0));
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
    public void testConcat2_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT CONCAT(s.strVal1, ?1) FROM QuerySyntaxEntity s");
            query.setParameter(1, "HELLO");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT VARCHAR(STRVAL1 || ?) FROM QUERYSYNTAXENTITY", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT CONCAT(STRVAL1, ?) FROM QUERYSYNTAXENTITY", _sql2.remove(0));
            }

            query = em.createQuery("SELECT CONCAT(s.strVal1, 'HELLO') FROM QuerySyntaxEntity s");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT VARCHAR(STRVAL1 || 'HELLO') FROM QUERYSYNTAXENTITY", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT CONCAT(STRVAL1, ?) FROM QUERYSYNTAXENTITY", _sql2.remove(0));
            }

            query = em.createQuery("SELECT CONCAT(s.strVal1, 'HELLO', ?1) FROM QuerySyntaxEntity s");
            query.setParameter(1, "WORLD");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT VARCHAR(VARCHAR(STRVAL1 || 'HELLO') || ?) FROM QUERYSYNTAXENTITY", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT CONCAT(CONCAT(STRVAL1, ?), ?) FROM QUERYSYNTAXENTITY", _sql2.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            cquery.multiselect(cb.concat(root.get(QuerySyntaxEntity_.strVal1), strParam1));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "HELLO");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT VARCHAR(STRVAL1 || ?) FROM QUERYSYNTAXENTITY", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT CONCAT(STRVAL1, ?) FROM QUERYSYNTAXENTITY", _sql2.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(cb2.concat(root2.get(QuerySyntaxEntity_.strVal1), "HELLO"));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT VARCHAR(STRVAL1 || 'HELLO') FROM QUERYSYNTAXENTITY", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT CONCAT(STRVAL1, ?) FROM QUERYSYNTAXENTITY", _sql2.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam2 = cb3.parameter(String.class);
            cquery3.multiselect(cb3.concat(cb3.concat(root3.get(QuerySyntaxEntity_.strVal1), "HELLO"), strParam2));

            query = em.createQuery(cquery3);
            query.setParameter(strParam2, "WORLD");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT VARCHAR(VARCHAR(STRVAL1 || 'HELLO') || ?) FROM QUERYSYNTAXENTITY", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT CONCAT(CONCAT(STRVAL1, ?), ?) FROM QUERYSYNTAXENTITY", _sql2.remove(0));
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
    public void testConcat2_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("SELECT CONCAT(s.strVal1, ?1) FROM QuerySyntaxEntity s");
            query.setParameter(1, "HELLO");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT VARCHAR(STRVAL1 || ?) FROM QUERYSYNTAXENTITY", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT CONCAT(STRVAL1, ?) FROM QUERYSYNTAXENTITY", _sql3.remove(0));
            }

            query = em.createQuery("SELECT CONCAT(s.strVal1, 'HELLO') FROM QuerySyntaxEntity s");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT VARCHAR(STRVAL1 || ?) FROM QUERYSYNTAXENTITY", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT CONCAT(STRVAL1, ?) FROM QUERYSYNTAXENTITY", _sql3.remove(0));
            }

            query = em.createQuery("SELECT CONCAT(s.strVal1, 'HELLO', ?1) FROM QuerySyntaxEntity s");
            query.setParameter(1, "WORLD");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT VARCHAR(VARCHAR(STRVAL1 || ?) || ?) FROM QUERYSYNTAXENTITY", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT CONCAT(CONCAT(STRVAL1, ?), ?) FROM QUERYSYNTAXENTITY", _sql3.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            cquery.multiselect(cb.concat(root.get(QuerySyntaxEntity_.strVal1), strParam1));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "HELLO");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT VARCHAR(STRVAL1 || ?) FROM QUERYSYNTAXENTITY", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT CONCAT(STRVAL1, ?) FROM QUERYSYNTAXENTITY", _sql3.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(cb2.concat(root2.get(QuerySyntaxEntity_.strVal1), "HELLO"));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT VARCHAR(STRVAL1 || ?) FROM QUERYSYNTAXENTITY", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT CONCAT(STRVAL1, ?) FROM QUERYSYNTAXENTITY", _sql3.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam2 = cb3.parameter(String.class);
            cquery3.multiselect(cb3.concat(cb3.concat(root3.get(QuerySyntaxEntity_.strVal1), "HELLO"), strParam2));

            query = em.createQuery(cquery3);
            query.setParameter(strParam2, "WORLD");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT VARCHAR(VARCHAR(STRVAL1 || ?) || ?) FROM QUERYSYNTAXENTITY", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT CONCAT(CONCAT(STRVAL1, ?), ?) FROM QUERYSYNTAXENTITY", _sql3.remove(0));
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
    public void testLeftTrim1_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = TRIM(LEADING ?2)");
            query.setParameter(2, "  HELLO WORD ");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LTRIM('  HELLO WORD '))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LTRIM(?))", _sql.remove(0));
            }

            query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = TRIM(LEADING '  HELLO WORD ')");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LTRIM('  HELLO WORD '))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LTRIM(?))", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.strVal1));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.strVal1), cb.trim(Trimspec.LEADING, strParam1)));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "  HELLO WORD ");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LTRIM('  HELLO WORD '))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LTRIM(?))", _sql.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.strVal1));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.strVal1), cb2.trim(Trimspec.LEADING, cb2.literal("  HELLO WORD "))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LTRIM('  HELLO WORD '))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LTRIM(?))", _sql.remove(0));
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
    public void testLeftTrim1_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = TRIM(LEADING ?2)");
            query.setParameter(2, "  HELLO WORD ");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LTRIM('  HELLO WORD '))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LTRIM(?))", _sql2.remove(0));
            }

            query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = TRIM(LEADING '  HELLO WORD ')");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LTRIM('  HELLO WORD '))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LTRIM(?))", _sql2.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.strVal1));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.strVal1), cb.trim(Trimspec.LEADING, strParam1)));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "  HELLO WORD ");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LTRIM('  HELLO WORD '))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LTRIM(?))", _sql2.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.strVal1));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.strVal1), cb2.trim(Trimspec.LEADING, cb2.literal("  HELLO WORD "))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LTRIM('  HELLO WORD '))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LTRIM(?))", _sql2.remove(0));
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
    public void testLeftTrim1_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = TRIM(LEADING ?2)");
            query.setParameter(2, "  HELLO WORD ");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LTRIM('  HELLO WORD '))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LTRIM(?))", _sql3.remove(0));
            }

            query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = TRIM(LEADING '  HELLO WORD ')");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LTRIM('  HELLO WORD '))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LTRIM(?))", _sql3.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.strVal1));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.strVal1), cb.trim(Trimspec.LEADING, strParam1)));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "  HELLO WORD ");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LTRIM('  HELLO WORD '))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LTRIM(?))", _sql3.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.strVal1));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.strVal1), cb2.trim(Trimspec.LEADING, cb2.literal("  HELLO WORD "))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LTRIM('  HELLO WORD '))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LTRIM(?))", _sql3.remove(0));
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
    public void testRightTrim1_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = TRIM(TRAILING ?2)");
            query.setParameter(2, "  HELLO WORD ");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = RTRIM('  HELLO WORD '))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = RTRIM(?))", _sql.remove(0));
            }

            query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = TRIM(TRAILING '  HELLO WORD ')");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = RTRIM('  HELLO WORD '))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = RTRIM(?))", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.strVal1));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.strVal1), cb.trim(Trimspec.TRAILING, strParam1)));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "  HELLO WORD ");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = RTRIM('  HELLO WORD '))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = RTRIM(?))", _sql.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.strVal1));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.strVal1), cb2.trim(Trimspec.TRAILING, cb2.literal("  HELLO WORD "))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = RTRIM('  HELLO WORD '))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = RTRIM(?))", _sql.remove(0));
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
    public void testRightTrim1_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = TRIM(TRAILING ?2)");
            query.setParameter(2, "  HELLO WORD ");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = RTRIM('  HELLO WORD '))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = RTRIM(?))", _sql2.remove(0));
            }

            query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = TRIM(TRAILING '  HELLO WORD ')");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = RTRIM('  HELLO WORD '))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = RTRIM(?))", _sql2.remove(0));
            }

            // -----------------------

            // Trim test #1 with CriteriaBuilder parameters
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.strVal1));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.strVal1), cb.trim(Trimspec.TRAILING, strParam1)));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "  HELLO WORD ");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = RTRIM('  HELLO WORD '))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = RTRIM(?))", _sql2.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.strVal1));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.strVal1), cb2.trim(Trimspec.TRAILING, cb2.literal("  HELLO WORD "))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = RTRIM('  HELLO WORD '))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = RTRIM(?))", _sql2.remove(0));
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
    public void testRightTrim1_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = TRIM(TRAILING ?2)");
            query.setParameter(2, "  HELLO WORD ");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = RTRIM('  HELLO WORD '))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = RTRIM(?))", _sql3.remove(0));
            }

            query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = TRIM(TRAILING '  HELLO WORD ')");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = RTRIM('  HELLO WORD '))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = RTRIM(?))", _sql3.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.strVal1));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.strVal1), cb.trim(Trimspec.TRAILING, strParam1)));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "  HELLO WORD ");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = RTRIM('  HELLO WORD '))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = RTRIM(?))", _sql3.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.strVal1));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.strVal1), cb2.trim(Trimspec.TRAILING, cb2.literal("  HELLO WORD "))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = RTRIM('  HELLO WORD '))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = RTRIM(?))", _sql3.remove(0));
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
    public void testSubstring1_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = SUBSTRING(?1, ?2, ?3)");
            query.setParameter(1, "HELLO WORLD");
            query.setParameter(2, 1);
            query.setParameter(3, 5);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = SUBSTR('HELLO WORLD', 1, 5))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = SUBSTR(?, ?, ?))", _sql.remove(0));
            }

            query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = SUBSTRING('HELLO WORLD', 1, 5)");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = SUBSTR('HELLO WORLD', 1, 5))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = SUBSTR(?, ?, ?))", _sql.remove(0));
            }

            query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = SUBSTRING('HELLO WORLD', 1, ?3)");
            query.setParameter(3, 5);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = SUBSTR('HELLO WORLD', 1, 5))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = SUBSTR(?, ?, ?))", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            ParameterExpression<Integer> strParam2 = cb.parameter(Integer.class);
            ParameterExpression<Integer> strParam3 = cb.parameter(Integer.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.strVal1));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.strVal1), cb.substring(strParam1, strParam2, strParam3)));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "HELLO WORLD");
            query.setParameter(strParam2, 1);
            query.setParameter(strParam3, 5);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = SUBSTR('HELLO WORLD', 1, 5))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = SUBSTR(?, ?, ?))", _sql.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.strVal1));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.strVal1), cb2.substring(cb2.literal("HELLO WORLD"), 1, 5)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = SUBSTR('HELLO WORLD', 1, 5))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = SUBSTR(?, ?, ?))", _sql.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.strVal1));
            ParameterExpression<Integer> strParam4 = cb3.parameter(Integer.class);
            cquery3.where(cb3.equal(root3.get(QuerySyntaxEntity_.strVal1), cb3.substring(cb3.literal("HELLO WORLD"), cb3.literal(1), strParam4)));

            query = em.createQuery(cquery3);
            query.setParameter(strParam4, 5);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = SUBSTR('HELLO WORLD', 1, 5))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = SUBSTR(?, ?, ?))", _sql.remove(0));
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
    public void testSubstring1_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = SUBSTRING(?1, ?2, ?3)");
            query.setParameter(1, "HELLO WORLD");
            query.setParameter(2, 1);
            query.setParameter(3, 5);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = SUBSTR(?, ?, ?))", _sql2.remove(0));

            query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = SUBSTRING('HELLO WORLD', 1, 5)");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = SUBSTR('HELLO WORLD', 1, 5))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = SUBSTR(?, ?, ?))", _sql2.remove(0));
            }

            query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = SUBSTRING('HELLO WORLD', 1, ?3)");
            query.setParameter(3, 5);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = SUBSTR('HELLO WORLD', 1, ?))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = SUBSTR(?, ?, ?))", _sql2.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            ParameterExpression<Integer> strParam2 = cb.parameter(Integer.class);
            ParameterExpression<Integer> strParam3 = cb.parameter(Integer.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.strVal1));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.strVal1), cb.substring(strParam1, strParam2, strParam3)));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "HELLO WORLD");
            query.setParameter(strParam2, 1);
            query.setParameter(strParam3, 5);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = SUBSTR(?, ?, ?))", _sql2.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.strVal1));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.strVal1), cb2.substring(cb2.literal("HELLO WORLD"), 1, 5)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = SUBSTR('HELLO WORLD', 1, 5))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = SUBSTR(?, ?, ?))", _sql2.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.strVal1));
            ParameterExpression<Integer> strParam4 = cb3.parameter(Integer.class);
            cquery3.where(cb3.equal(root3.get(QuerySyntaxEntity_.strVal1), cb3.substring(cb3.literal("HELLO WORLD"), cb3.literal(1), strParam4)));

            query = em.createQuery(cquery3);
            query.setParameter(strParam4, 5);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = SUBSTR('HELLO WORLD', 1, ?))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = SUBSTR(?, ?, ?))", _sql2.remove(0));
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
    public void testSubstring1_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = SUBSTRING(?1, ?2, ?3)");
            query.setParameter(1, "HELLO WORLD");
            query.setParameter(2, 1);
            query.setParameter(3, 5);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = SUBSTR(?, ?, ?))", _sql3.remove(0));

            query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = SUBSTRING('HELLO WORLD', 1, 5)");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = SUBSTR(?, ?, ?))", _sql3.remove(0));

            query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = SUBSTRING('HELLO WORLD', 1, ?3)");
            query.setParameter(3, 5);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = SUBSTR(?, ?, ?))", _sql3.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            ParameterExpression<Integer> strParam2 = cb.parameter(Integer.class);
            ParameterExpression<Integer> strParam3 = cb.parameter(Integer.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.strVal1));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.strVal1), cb.substring(strParam1, strParam2, strParam3)));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "HELLO WORLD");
            query.setParameter(strParam2, 1);
            query.setParameter(strParam3, 5);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = SUBSTR(?, ?, ?))", _sql3.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.strVal1));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.strVal1), cb2.substring(cb2.literal("HELLO WORLD"), 1, 5)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = SUBSTR(?, ?, ?))", _sql3.remove(0));

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.strVal1));
            ParameterExpression<Integer> strParam4 = cb3.parameter(Integer.class);
            cquery3.where(cb3.equal(root3.get(QuerySyntaxEntity_.strVal1), cb3.substring(cb3.literal("HELLO WORLD"), cb3.literal(1), strParam4)));

            query = em.createQuery(cquery3);
            query.setParameter(strParam4, 5);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = SUBSTR(?, ?, ?))", _sql3.remove(0));
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
    public void testSubstring2_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT SUBSTRING(?1, ?2, ?3), s.strVal2 FROM QuerySyntaxEntity s WHERE s.strVal1 = SUBSTRING(SUBSTRING(s.strVal2, ?2, ?3), ?2, ?4)");
            query.setParameter(1, "HELLO WORLD");
            query.setParameter(2, 1);
            query.setParameter(3, 5);
            query.setParameter(4, 2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT SUBSTR('HELLO WORLD', 1, 5), STRVAL2 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = SUBSTR(SUBSTR(STRVAL2, 1, 5), 1, 2))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT SUBSTR(?, ?, ?), STRVAL2 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = SUBSTR(SUBSTR(STRVAL2, ?, ?), ?, ?))", _sql.remove(0));
            }

            query = em.createQuery("SELECT SUBSTRING('HELLO WORLD', 1, 5), s.strVal2 FROM QuerySyntaxEntity s WHERE s.strVal1 = SUBSTRING(SUBSTRING(s.strVal2, 1, 5), 1, 2)");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT SUBSTR('HELLO WORLD', 1, 5), STRVAL2 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = SUBSTR(SUBSTR(STRVAL2, 1, 5), 1, 2))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT SUBSTR(?, ?, ?), STRVAL2 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = SUBSTR(SUBSTR(STRVAL2, ?, ?), ?, ?))", _sql.remove(0));
            }

            query = em.createQuery("SELECT SUBSTRING('HELLO WORLD', 1, ?2), s.strVal2 FROM QuerySyntaxEntity s WHERE s.strVal1 = SUBSTRING(SUBSTRING(s.strVal2, 1, ?2), 1, ?3)");
            query.setParameter(2, 5);
            query.setParameter(3, 2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT SUBSTR('HELLO WORLD', 1, 5), STRVAL2 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = SUBSTR(SUBSTR(STRVAL2, 1, 5), 1, 2))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT SUBSTR(?, ?, ?), STRVAL2 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = SUBSTR(SUBSTR(STRVAL2, ?, ?), ?, ?))", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            ParameterExpression<Integer> strParam2 = cb.parameter(Integer.class);
            ParameterExpression<Integer> strParam3 = cb.parameter(Integer.class);
            ParameterExpression<Integer> strParam4 = cb.parameter(Integer.class);
            cquery.multiselect(cb.substring(strParam1, strParam2, strParam3), root.get(QuerySyntaxEntity_.strVal2));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.strVal1), cb.substring(cb.substring(root.get(QuerySyntaxEntity_.strVal2), strParam2, strParam3), strParam2, strParam4)));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "HELLO WORLD");
            query.setParameter(strParam2, 1);
            query.setParameter(strParam3, 5);
            query.setParameter(strParam4, 2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT SUBSTR('HELLO WORLD', 1, 5), STRVAL2 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = SUBSTR(SUBSTR(STRVAL2, 1, 5), 1, 2))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT SUBSTR(?, ?, ?), STRVAL2 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = SUBSTR(SUBSTR(STRVAL2, ?, ?), ?, ?))", _sql.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(cb2.substring(cb2.literal("HELLO WORLD"), 1, 5), root2.get(QuerySyntaxEntity_.strVal2));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.strVal1), cb2.substring(cb2.substring(root2.get(QuerySyntaxEntity_.strVal2), 1, 5), 1, 2)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT SUBSTR('HELLO WORLD', 1, 5), STRVAL2 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = SUBSTR(SUBSTR(STRVAL2, 1, 5), 1, 2))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT SUBSTR(?, ?, ?), STRVAL2 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = SUBSTR(SUBSTR(STRVAL2, ?, ?), ?, ?))", _sql.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.strVal1));
            ParameterExpression<Integer> strParam5 = cb.parameter(Integer.class);
            ParameterExpression<Integer> strParam6 = cb3.parameter(Integer.class);
            cquery3.multiselect(cb3.substring(cb3.literal("HELLO WORLD"), cb3.literal(1), strParam5), root3.get(QuerySyntaxEntity_.strVal2));
            cquery3.where(cb3.equal(root3.get(QuerySyntaxEntity_.strVal1), cb3.substring(cb3.substring(root3.get(QuerySyntaxEntity_.strVal2), cb3.literal(1), strParam5), cb3.literal(1), strParam6)));

            query = em.createQuery(cquery3);
            query.setParameter(strParam5, 5);
            query.setParameter(strParam6, 2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT SUBSTR('HELLO WORLD', 1, 5), STRVAL2 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = SUBSTR(SUBSTR(STRVAL2, 1, 5), 1, 2))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT SUBSTR(?, ?, ?), STRVAL2 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = SUBSTR(SUBSTR(STRVAL2, ?, ?), ?, ?))", _sql.remove(0));
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
    public void testSubstring2_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT SUBSTRING(?1, ?2, ?3), s.strVal2 FROM QuerySyntaxEntity s WHERE s.strVal1 = SUBSTRING(SUBSTRING(s.strVal2, ?2, ?3), ?2, ?4)");
            query.setParameter(1, "HELLO WORLD");
            query.setParameter(2, 1);
            query.setParameter(3, 5);
            query.setParameter(4, 2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT SUBSTR(?, ?, ?), STRVAL2 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = SUBSTR(SUBSTR(STRVAL2, ?, ?), ?, ?))", _sql2.remove(0));

            query = em.createQuery("SELECT SUBSTRING('HELLO WORLD', 1, 5), s.strVal2 FROM QuerySyntaxEntity s WHERE s.strVal1 = SUBSTRING(SUBSTRING(s.strVal2, 1, 5), 1, 2)");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT SUBSTR('HELLO WORLD', 1, 5), STRVAL2 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = SUBSTR(SUBSTR(STRVAL2, 1, 5), 1, 2))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT SUBSTR(?, ?, ?), STRVAL2 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = SUBSTR(SUBSTR(STRVAL2, ?, ?), ?, ?))", _sql2.remove(0));
            }

            query = em.createQuery("SELECT SUBSTRING('HELLO WORLD', 1, ?2), s.strVal2 FROM QuerySyntaxEntity s WHERE s.strVal1 = SUBSTRING(SUBSTRING(s.strVal2, 1, ?2), 1, ?3)");
            query.setParameter(2, 5);
            query.setParameter(3, 2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT SUBSTR('HELLO WORLD', 1, ?), STRVAL2 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = SUBSTR(SUBSTR(STRVAL2, 1, ?), 1, ?))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT SUBSTR(?, ?, ?), STRVAL2 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = SUBSTR(SUBSTR(STRVAL2, ?, ?), ?, ?))", _sql2.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            ParameterExpression<Integer> strParam2 = cb.parameter(Integer.class);
            ParameterExpression<Integer> strParam3 = cb.parameter(Integer.class);
            ParameterExpression<Integer> strParam4 = cb.parameter(Integer.class);
            cquery.multiselect(cb.substring(strParam1, strParam2, strParam3), root.get(QuerySyntaxEntity_.strVal2));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.strVal1), cb.substring(cb.substring(root.get(QuerySyntaxEntity_.strVal2), strParam2, strParam3), strParam2, strParam4)));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "HELLO WORLD");
            query.setParameter(strParam2, 1);
            query.setParameter(strParam3, 5);
            query.setParameter(strParam4, 2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT SUBSTR(?, ?, ?), STRVAL2 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = SUBSTR(SUBSTR(STRVAL2, ?, ?), ?, ?))", _sql2.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(cb2.substring(cb2.literal("HELLO WORLD"), 1, 5), root2.get(QuerySyntaxEntity_.strVal2));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.strVal1), cb2.substring(cb2.substring(root2.get(QuerySyntaxEntity_.strVal2), 1, 5), 1, 2)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT SUBSTR('HELLO WORLD', 1, 5), STRVAL2 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = SUBSTR(SUBSTR(STRVAL2, 1, 5), 1, 2))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT SUBSTR(?, ?, ?), STRVAL2 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = SUBSTR(SUBSTR(STRVAL2, ?, ?), ?, ?))", _sql2.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.strVal1));
            ParameterExpression<Integer> strParam5 = cb.parameter(Integer.class);
            ParameterExpression<Integer> strParam6 = cb3.parameter(Integer.class);
            cquery3.multiselect(cb3.substring(cb3.literal("HELLO WORLD"), cb3.literal(1), strParam5), root3.get(QuerySyntaxEntity_.strVal2));
            cquery3.where(cb3.equal(root3.get(QuerySyntaxEntity_.strVal1), cb3.substring(cb3.substring(root3.get(QuerySyntaxEntity_.strVal2), cb3.literal(1), strParam5), cb3.literal(1), strParam6)));

            query = em.createQuery(cquery3);
            query.setParameter(strParam5, 5);
            query.setParameter(strParam6, 2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT SUBSTR('HELLO WORLD', 1, ?), STRVAL2 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = SUBSTR(SUBSTR(STRVAL2, 1, ?), 1, ?))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT SUBSTR(?, ?, ?), STRVAL2 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = SUBSTR(SUBSTR(STRVAL2, ?, ?), ?, ?))", _sql2.remove(0));
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
    public void testSubstring2_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();

        try {
            Query query = em.createQuery("SELECT SUBSTRING(?1, ?2, ?3), s.strVal2 FROM QuerySyntaxEntity s WHERE s.strVal1 = SUBSTRING(SUBSTRING(s.strVal2, ?2, ?3), ?2, ?4)");
            query.setParameter(1, "HELLO WORLD");
            query.setParameter(2, 1);
            query.setParameter(3, 5);
            query.setParameter(4, 2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT SUBSTR(?, ?, ?), STRVAL2 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = SUBSTR(SUBSTR(STRVAL2, ?, ?), ?, ?))", _sql3.remove(0));

            query = em.createQuery("SELECT SUBSTRING('HELLO WORLD', 1, 5), s.strVal2 FROM QuerySyntaxEntity s WHERE s.strVal1 = SUBSTRING(SUBSTRING(s.strVal2, 1, 5), 1, 2)");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT SUBSTR(?, ?, ?), STRVAL2 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = SUBSTR(SUBSTR(STRVAL2, ?, ?), ?, ?))", _sql3.remove(0));

            query = em.createQuery("SELECT SUBSTRING('HELLO WORLD', 1, ?2), s.strVal2 FROM QuerySyntaxEntity s WHERE s.strVal1 = SUBSTRING(SUBSTRING(s.strVal2, 1, ?2), 1, ?3)");
            query.setParameter(2, 5);
            query.setParameter(3, 2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT SUBSTR(?, ?, ?), STRVAL2 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = SUBSTR(SUBSTR(STRVAL2, ?, ?), ?, ?))", _sql3.remove(0));

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            ParameterExpression<Integer> strParam2 = cb.parameter(Integer.class);
            ParameterExpression<Integer> strParam3 = cb.parameter(Integer.class);
            ParameterExpression<Integer> strParam4 = cb.parameter(Integer.class);
            cquery.multiselect(cb.substring(strParam1, strParam2, strParam3), root.get(QuerySyntaxEntity_.strVal2));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.strVal1), cb.substring(cb.substring(root.get(QuerySyntaxEntity_.strVal2), strParam2, strParam3), strParam2, strParam4)));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "HELLO WORLD");
            query.setParameter(strParam2, 1);
            query.setParameter(strParam3, 5);
            query.setParameter(strParam4, 2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT SUBSTR(?, ?, ?), STRVAL2 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = SUBSTR(SUBSTR(STRVAL2, ?, ?), ?, ?))", _sql3.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(cb2.substring(cb2.literal("HELLO WORLD"), 1, 5), root2.get(QuerySyntaxEntity_.strVal2));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.strVal1), cb2.substring(cb2.substring(root2.get(QuerySyntaxEntity_.strVal2), 1, 5), 1, 2)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT SUBSTR(?, ?, ?), STRVAL2 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = SUBSTR(SUBSTR(STRVAL2, ?, ?), ?, ?))", _sql3.remove(0));

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.strVal1));
            ParameterExpression<Integer> strParam5 = cb.parameter(Integer.class);
            ParameterExpression<Integer> strParam6 = cb3.parameter(Integer.class);
            cquery3.multiselect(cb3.substring(cb3.literal("HELLO WORLD"), cb3.literal(1), strParam5), root3.get(QuerySyntaxEntity_.strVal2));
            cquery3.where(cb3.equal(root3.get(QuerySyntaxEntity_.strVal1), cb3.substring(cb3.substring(root3.get(QuerySyntaxEntity_.strVal2), cb3.literal(1), strParam5), cb3.literal(1), strParam6)));

            query = em.createQuery(cquery3);
            query.setParameter(strParam5, 5);
            query.setParameter(strParam6, 2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT SUBSTR(?, ?, ?), STRVAL2 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = SUBSTR(SUBSTR(STRVAL2, ?, ?), ?, ?))", _sql3.remove(0));
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
    public void testTrim1_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = TRIM(?2)");
            query.setParameter(2, "  HELLO WORD ");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM('  HELLO WORD '))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(?))", _sql.remove(0));
            }

            query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = TRIM('  HELLO WORD ')");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM('  HELLO WORD '))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(?))", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.strVal1));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.strVal1), cb.trim(strParam1)));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "  HELLO WORD ");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM('  HELLO WORD '))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(?))", _sql.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.strVal1));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.strVal1), cb2.trim(cb2.literal("  HELLO WORD "))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM('  HELLO WORD '))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(?))", _sql.remove(0));
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
    public void testTrim1_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = TRIM(?2)");
            query.setParameter(2, "  HELLO WORD ");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM('  HELLO WORD '))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(?))", _sql2.remove(0));
            }

            query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = TRIM('  HELLO WORD ')");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM('  HELLO WORD '))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(?))", _sql2.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.strVal1));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.strVal1), cb.trim(strParam1)));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "  HELLO WORD ");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM('  HELLO WORD '))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(?))", _sql2.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.strVal1));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.strVal1), cb2.trim(cb2.literal("  HELLO WORD "))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM('  HELLO WORD '))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(?))", _sql2.remove(0));
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
    public void testTrim1_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = TRIM(?2)");
            query.setParameter(2, "  HELLO WORD ");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM('  HELLO WORD '))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(?))", _sql3.remove(0));
            }

            query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = TRIM('  HELLO WORD ')");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM('  HELLO WORD '))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(?))", _sql3.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.strVal1));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.strVal1), cb.trim(strParam1)));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "  HELLO WORD ");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM('  HELLO WORD '))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(?))", _sql3.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.strVal1));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.strVal1), cb2.trim(cb2.literal("  HELLO WORD "))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM('  HELLO WORD '))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(?))", _sql3.remove(0));
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
    public void testTrim2_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT TRIM(?1), s.strVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = ?2");
            query.setParameter(1, "  HELLO WORD ");
            query.setParameter(2, 23);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT TRIM('  HELLO WORD '), STRVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 23)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT TRIM(?), STRVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));
            }

            query = em.createQuery("SELECT TRIM('  HELLO WORD '), s.strVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = 23");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT TRIM('  HELLO WORD '), STRVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 23)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT TRIM(?), STRVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            cquery.multiselect(cb.trim(strParam1), root.get(QuerySyntaxEntity_.strVal1));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.intVal1), intParam1));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "  HELLO WORD ");
            query.setParameter(intParam1, 23);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT TRIM('  HELLO WORD '), STRVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 23)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT TRIM(?), STRVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(cb2.trim(cb2.literal("  HELLO WORD ")), root2.get(QuerySyntaxEntity_.strVal1));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), 23));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT TRIM('  HELLO WORD '), STRVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 23)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT TRIM(?), STRVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql.remove(0));
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
    public void testTrim2_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT TRIM(?1), s.strVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = ?2");
            query.setParameter(1, "  HELLO WORD ");
            query.setParameter(2, 23);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT TRIM('  HELLO WORD '), STRVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT TRIM(?), STRVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            }

            query = em.createQuery("SELECT TRIM('  HELLO WORD '), s.strVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = 23");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT TRIM('  HELLO WORD '), STRVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 23)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT TRIM(?), STRVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            cquery.multiselect(cb.trim(strParam1), root.get(QuerySyntaxEntity_.strVal1));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.intVal1), intParam1));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "  HELLO WORD ");
            query.setParameter(intParam1, 23);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT TRIM('  HELLO WORD '), STRVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT TRIM(?), STRVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(cb2.trim(cb2.literal("  HELLO WORD ")), root2.get(QuerySyntaxEntity_.strVal1));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), 23));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT TRIM('  HELLO WORD '), STRVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = 23)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT TRIM(?), STRVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql2.remove(0));
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
    public void testTrim2_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("SELECT TRIM(?1), s.strVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = ?2");
            query.setParameter(1, "  HELLO WORD ");
            query.setParameter(2, 23);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT TRIM('  HELLO WORD '), STRVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT TRIM(?), STRVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            }

            query = em.createQuery("SELECT TRIM('  HELLO WORD '), s.strVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = 23");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT TRIM('  HELLO WORD '), STRVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT TRIM(?), STRVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            cquery.multiselect(cb.trim(strParam1), root.get(QuerySyntaxEntity_.strVal1));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.intVal1), intParam1));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "  HELLO WORD ");
            query.setParameter(intParam1, 23);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT TRIM('  HELLO WORD '), STRVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT TRIM(?), STRVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(cb2.trim(cb2.literal("  HELLO WORD ")), root2.get(QuerySyntaxEntity_.strVal1));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), 23));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT TRIM('  HELLO WORD '), STRVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT TRIM(?), STRVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ?)", _sql3.remove(0));
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
    public void testLength1_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?2 = LENGTH(?1)");
            query.setParameter(1, "HELLO WORLD");
            query.setParameter(2, 11);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (11 = LENGTH('HELLO WORLD'))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? = LENGTH(?))", _sql.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 11 = LENGTH('HELLO WORLD')");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (11 = LENGTH('HELLO WORLD'))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? = LENGTH(?))", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));
            cquery.where(cb.equal(intParam1, cb.length(strParam1)));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "HELLO WORLD");
            query.setParameter(intParam1, 11);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (11 = LENGTH('HELLO WORLD'))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? = LENGTH(?))", _sql.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.where(cb2.equal(cb2.literal(11), cb2.length(cb2.literal("HELLO WORLD"))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (11 = LENGTH('HELLO WORLD'))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? = LENGTH(?))", _sql.remove(0));
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
    public void testLength1_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?2 = LENGTH(?1)");
            query.setParameter(1, "HELLO WORLD");
            query.setParameter(2, 11);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? = LENGTH('HELLO WORLD'))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? = LENGTH(?))", _sql2.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 11 = LENGTH('HELLO WORLD')");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (11 = LENGTH('HELLO WORLD'))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? = LENGTH(?))", _sql2.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));
            cquery.where(cb.equal(intParam1, cb.length(strParam1)));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "HELLO WORLD");
            query.setParameter(intParam1, 11);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? = LENGTH('HELLO WORLD'))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? = LENGTH(?))", _sql2.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.where(cb2.equal(cb2.literal(11), cb2.length(cb2.literal("HELLO WORLD"))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (11 = LENGTH('HELLO WORLD'))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? = LENGTH(?))", _sql2.remove(0));
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
    public void testLength1_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?2 = LENGTH(?1)");
            query.setParameter(1, "HELLO WORLD");
            query.setParameter(2, 11);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? = LENGTH('HELLO WORLD'))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? = LENGTH(?))", _sql3.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 11 = LENGTH('HELLO WORLD')");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? = LENGTH('HELLO WORLD'))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? = LENGTH(?))", _sql3.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));
            cquery.where(cb.equal(intParam1, cb.length(strParam1)));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "HELLO WORLD");
            query.setParameter(intParam1, 11);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? = LENGTH('HELLO WORLD'))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? = LENGTH(?))", _sql3.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.where(cb2.equal(cb2.literal(11), cb2.length(cb2.literal("HELLO WORLD"))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? = LENGTH('HELLO WORLD'))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (? = LENGTH(?))", _sql3.remove(0));
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
    public void testLength2_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT s.intVal1, ?1 FROM QuerySyntaxEntity s ORDER BY LENGTH(?1)");
            query.setParameter(1, "HELLO WORLD");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, 'HELLO WORLD' FROM QUERYSYNTAXENTITY ORDER BY LENGTH('HELLO WORLD')", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, ? FROM QUERYSYNTAXENTITY ORDER BY LENGTH(?)", _sql.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1, 'HELLO WORLD' FROM QuerySyntaxEntity s ORDER BY LENGTH('HELLO WORLD')");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, 'HELLO WORLD' FROM QUERYSYNTAXENTITY ORDER BY LENGTH('HELLO WORLD')", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, ? FROM QUERYSYNTAXENTITY ORDER BY LENGTH(?)", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1), strParam1);
            cquery.orderBy(cb.desc(cb.length(strParam1)));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "HELLO WORLD");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, 'HELLO WORLD' FROM QUERYSYNTAXENTITY ORDER BY LENGTH('HELLO WORLD') DESC", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, ? FROM QUERYSYNTAXENTITY ORDER BY LENGTH(?) DESC", _sql.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1), cb2.literal("HELLO WORLD"));
            cquery2.orderBy(cb2.desc(cb2.length(cb2.literal("HELLO WORLD"))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, 'HELLO WORLD' FROM QUERYSYNTAXENTITY ORDER BY LENGTH('HELLO WORLD') DESC", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, ? FROM QUERYSYNTAXENTITY ORDER BY LENGTH(?) DESC", _sql.remove(0));
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
    public void testLength2_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT s.intVal1, ?1 FROM QuerySyntaxEntity s ORDER BY LENGTH(?1)");
            query.setParameter(1, "HELLO WORLD");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, 'HELLO WORLD' FROM QUERYSYNTAXENTITY ORDER BY LENGTH('HELLO WORLD')", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, ? FROM QUERYSYNTAXENTITY ORDER BY LENGTH(?)", _sql2.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1, 'HELLO WORLD' FROM QuerySyntaxEntity s ORDER BY LENGTH('HELLO WORLD')");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, 'HELLO WORLD' FROM QUERYSYNTAXENTITY ORDER BY LENGTH('HELLO WORLD')", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, ? FROM QUERYSYNTAXENTITY ORDER BY LENGTH(?)", _sql2.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1), strParam1);
            cquery.orderBy(cb.desc(cb.length(strParam1)));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "HELLO WORLD");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, 'HELLO WORLD' FROM QUERYSYNTAXENTITY ORDER BY LENGTH('HELLO WORLD') DESC", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, ? FROM QUERYSYNTAXENTITY ORDER BY LENGTH(?) DESC", _sql2.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1), cb2.literal("HELLO WORLD"));
            cquery2.orderBy(cb2.desc(cb2.length(cb2.literal("HELLO WORLD"))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, 'HELLO WORLD' FROM QUERYSYNTAXENTITY ORDER BY LENGTH('HELLO WORLD') DESC", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, ? FROM QUERYSYNTAXENTITY ORDER BY LENGTH(?) DESC", _sql2.remove(0));
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
    public void testLength2_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("SELECT s.intVal1, ?1 FROM QuerySyntaxEntity s ORDER BY LENGTH(?1)");
            query.setParameter(1, "HELLO WORLD");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, 'HELLO WORLD' FROM QUERYSYNTAXENTITY ORDER BY LENGTH('HELLO WORLD')", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, ? FROM QUERYSYNTAXENTITY ORDER BY LENGTH(?)", _sql3.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1, 'HELLO WORLD' FROM QuerySyntaxEntity s ORDER BY LENGTH('HELLO WORLD')");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, 'HELLO WORLD' FROM QUERYSYNTAXENTITY ORDER BY LENGTH('HELLO WORLD')", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, ? FROM QUERYSYNTAXENTITY ORDER BY LENGTH(?)", _sql3.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1), strParam1);
            cquery.orderBy(cb.desc(cb.length(strParam1)));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "HELLO WORLD");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, 'HELLO WORLD' FROM QUERYSYNTAXENTITY ORDER BY LENGTH('HELLO WORLD') DESC", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, ? FROM QUERYSYNTAXENTITY ORDER BY LENGTH(?) DESC", _sql3.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1), cb2.literal("HELLO WORLD"));
            cquery2.orderBy(cb2.desc(cb2.length(cb2.literal("HELLO WORLD"))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, 'HELLO WORLD' FROM QUERYSYNTAXENTITY ORDER BY LENGTH('HELLO WORLD') DESC", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, ? FROM QUERYSYNTAXENTITY ORDER BY LENGTH(?) DESC", _sql3.remove(0));
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
    public void testLocate1_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = LOCATE(?1, ?2)");
            query.setParameter(1, "HI");
            query.setParameter(2, "ABCDEFGHIJKLMNOP");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE('HI', 'ABCDEFGHIJKLMNOP'))", _sql.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = INSTR(?, ?))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE(?, ?))", _sql.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = LOCATE('HI', 'ABCDEFGHIJKLMNOP')");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE('HI', 'ABCDEFGHIJKLMNOP'))", _sql.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = INSTR(?, ?))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE(?, ?))", _sql.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = LOCATE(?2, 'ABCDEFGHIJKLMNOP')");
            query.setParameter(2, "HI");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE('HI', 'ABCDEFGHIJKLMNOP'))", _sql.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = INSTR(?, ?))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE(?, ?))", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            ParameterExpression<String> strParam2 = cb.parameter(String.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.intVal1), cb.locate(strParam1, strParam2)));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "ABCDEFGHIJKLMNOP");
            query.setParameter(strParam2, "HI");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE('HI', 'ABCDEFGHIJKLMNOP'))", _sql.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = INSTR(?, ?))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE(?, ?))", _sql.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), cb2.locate(cb2.literal("ABCDEFGHIJKLMNOP"), "HI")));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE('HI', 'ABCDEFGHIJKLMNOP'))", _sql.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = INSTR(?, ?))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE(?, ?))", _sql.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam4 = cb3.parameter(String.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));
            cquery3.where(cb3.equal(root3.get(QuerySyntaxEntity_.intVal1), cb3.locate(cb3.literal("ABCDEFGHIJKLMNOP"), strParam4)));

            query = em.createQuery(cquery3);
            query.setParameter(strParam4, "HI");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE('HI', 'ABCDEFGHIJKLMNOP'))", _sql.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = INSTR(?, ?))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE(?, ?))", _sql.remove(0));
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
    public void testLocate1_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = LOCATE(?1, ?2)");
            query.setParameter(1, "HI");
            query.setParameter(2, "ABCDEFGHIJKLMNOP");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE('HI', 'ABCDEFGHIJKLMNOP'))", _sql2.remove(0));
            } else if(platform.isDB2()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE(?, ?))", _sql2.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = INSTR(?, ?))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE(?, ?))", _sql2.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = LOCATE('HI', 'ABCDEFGHIJKLMNOP')");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE('HI', 'ABCDEFGHIJKLMNOP'))", _sql2.remove(0));
            } else if(platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE('HI', 'ABCDEFGHIJKLMNOP'))", _sql2.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = INSTR(?, ?))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE(?, ?))", _sql2.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = LOCATE(?2, 'ABCDEFGHIJKLMNOP')");
            query.setParameter(2, "HI");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE('HI', 'ABCDEFGHIJKLMNOP'))", _sql2.remove(0));
            } else if(platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE(?, 'ABCDEFGHIJKLMNOP'))", _sql2.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = INSTR(?, ?))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE(?, ?))", _sql2.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            ParameterExpression<String> strParam2 = cb.parameter(String.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.intVal1), cb.locate(strParam1, strParam2)));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "ABCDEFGHIJKLMNOP");
            query.setParameter(strParam2, "HI");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE('HI', 'ABCDEFGHIJKLMNOP'))", _sql2.remove(0));
            } else if(platform.isDB2()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE(?, ?))", _sql2.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = INSTR(?, ?))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE(?, ?))", _sql2.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), cb2.locate(cb2.literal("ABCDEFGHIJKLMNOP"), "HI")));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE('HI', 'ABCDEFGHIJKLMNOP'))", _sql2.remove(0));
            } else if(platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE('HI', 'ABCDEFGHIJKLMNOP'))", _sql2.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = INSTR(?, ?))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE(?, ?))", _sql2.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam4 = cb3.parameter(String.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));
            cquery3.where(cb3.equal(root3.get(QuerySyntaxEntity_.intVal1), cb3.locate(cb3.literal("ABCDEFGHIJKLMNOP"), strParam4)));

            query = em.createQuery(cquery3);
            query.setParameter(strParam4, "HI");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE('HI', 'ABCDEFGHIJKLMNOP'))", _sql2.remove(0));
            } else if(platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE(?, 'ABCDEFGHIJKLMNOP'))", _sql2.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = INSTR(?, ?))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE(?, ?))", _sql2.remove(0));
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
    public void testLocate1_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = LOCATE(?1, ?2)");
            query.setParameter(1, "HI");
            query.setParameter(2, "ABCDEFGHIJKLMNOP");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE('HI', 'ABCDEFGHIJKLMNOP'))", _sql3.remove(0));
            } else if(platform.isDB2()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE(?, ?))", _sql3.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = INSTR(?, ?))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE(?, ?))", _sql3.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = LOCATE('HI', 'ABCDEFGHIJKLMNOP')");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE('HI', 'ABCDEFGHIJKLMNOP'))", _sql3.remove(0));
            } else if(platform.isDB2()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE(?, ?))", _sql3.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = INSTR(?, ?))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE(?, ?))", _sql3.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = LOCATE(?2, 'ABCDEFGHIJKLMNOP')");
            query.setParameter(2, "HI");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE('HI', 'ABCDEFGHIJKLMNOP'))", _sql3.remove(0));
            } else if(platform.isDB2()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE(?, ?))", _sql3.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = INSTR(?, ?))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE(?, ?))", _sql3.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            ParameterExpression<String> strParam2 = cb.parameter(String.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.intVal1), cb.locate(strParam1, strParam2)));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "ABCDEFGHIJKLMNOP");
            query.setParameter(strParam2, "HI");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE('HI', 'ABCDEFGHIJKLMNOP'))", _sql3.remove(0));
            } else if(platform.isDB2()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE(?, ?))", _sql3.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = INSTR(?, ?))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE(?, ?))", _sql3.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), cb2.locate(cb2.literal("ABCDEFGHIJKLMNOP"), "HI")));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE('HI', 'ABCDEFGHIJKLMNOP'))", _sql3.remove(0));
            } else if(platform.isDB2()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE(?, ?))", _sql3.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = INSTR(?, ?))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE(?, ?))", _sql3.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam4 = cb3.parameter(String.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));
            cquery3.where(cb3.equal(root3.get(QuerySyntaxEntity_.intVal1), cb3.locate(cb3.literal("ABCDEFGHIJKLMNOP"), strParam4)));

            query = em.createQuery(cquery3);
            query.setParameter(strParam4, "HI");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE('HI', 'ABCDEFGHIJKLMNOP'))", _sql3.remove(0));
            } else if(platform.isDB2()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE(?, ?))", _sql3.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = INSTR(?, ?))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE(?, ?))", _sql3.remove(0));
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
    public void testLocate2_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = LOCATE(?1, ?2, ?3)");
            query.setParameter(1, "X");
            query.setParameter(2, "OXOOOOOXXOOOOOOXX");
            query.setParameter(3, 3);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE('X', 'OXOOOOOXXOOOOOOXX', 3))", _sql.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = INSTR(?, ?, ?))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE(?, ?, ?))", _sql.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = LOCATE('X', 'OXOOOOOXXOOOOOOXX', 3)");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE('X', 'OXOOOOOXXOOOOOOXX', 3))", _sql.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = INSTR(?, ?, ?))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE(?, ?, ?))", _sql.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = LOCATE('X', ?1, 3)");
            query.setParameter(1, "OXOOOOOXXOOOOOOXX");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE('X', 'OXOOOOOXXOOOOOOXX', 3))", _sql.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = INSTR(?, ?, ?))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE(?, ?, ?))", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            ParameterExpression<String> strParam2 = cb.parameter(String.class);
            ParameterExpression<Integer> intParam3 = cb.parameter(Integer.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.intVal1), cb.locate(strParam2, strParam1, intParam3)));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "X");
            query.setParameter(strParam2, "OXOOOOOXXOOOOOOXX");
            query.setParameter(intParam3, 3);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE('X', 'OXOOOOOXXOOOOOOXX', 3))", _sql.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = INSTR(?, ?, ?))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE(?, ?, ?))", _sql.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), cb2.locate(cb2.literal("OXOOOOOXXOOOOOOXX"), "X", 3)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE('X', 'OXOOOOOXXOOOOOOXX', 3))", _sql.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = INSTR(?, ?, ?))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE(?, ?, ?))", _sql.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam5 = cb3.parameter(String.class);
            ParameterExpression<Integer> intParam6 = cb3.parameter(Integer.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));
            cquery3.where(cb3.equal(root3.get(QuerySyntaxEntity_.intVal1), cb3.locate(strParam5, cb3.literal("X"), intParam6)));

            query = em.createQuery(cquery3);
            query.setParameter(strParam5, "OXOOOOOXXOOOOOOXX");
            query.setParameter(intParam6, 3);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE('X', 'OXOOOOOXXOOOOOOXX', 3))", _sql.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = INSTR(?, ?, ?))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE(?, ?, ?))", _sql.remove(0));
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
    public void testLocate2_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = LOCATE(?1, ?2, ?3)");
            query.setParameter(1, "X");
            query.setParameter(2, "OXOOOOOXXOOOOOOXX");
            query.setParameter(3, 3);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE('X', 'OXOOOOOXXOOOOOOXX', 3))", _sql2.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = INSTR(?, ?, ?))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE(?, ?, ?))", _sql2.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = LOCATE('X', 'OXOOOOOXXOOOOOOXX', 3)");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE('X', 'OXOOOOOXXOOOOOOXX', 3))", _sql2.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = INSTR(?, ?, ?))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE(?, ?, ?))", _sql2.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = LOCATE('X', ?1, 3)");
            query.setParameter(1, "OXOOOOOXXOOOOOOXX");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE('X', 'OXOOOOOXXOOOOOOXX', 3))", _sql2.remove(0));
            } else if(platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE('X', ?, 3))", _sql2.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = INSTR(?, ?, ?))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE(?, ?, ?))", _sql2.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            ParameterExpression<String> strParam2 = cb.parameter(String.class);
            ParameterExpression<Integer> intParam3 = cb.parameter(Integer.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.intVal1), cb.locate(strParam2, strParam1, intParam3)));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "X");
            query.setParameter(strParam2, "OXOOOOOXXOOOOOOXX");
            query.setParameter(intParam3, 3);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE('X', 'OXOOOOOXXOOOOOOXX', 3))", _sql2.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = INSTR(?, ?, ?))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE(?, ?, ?))", _sql2.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), cb2.locate(cb2.literal("OXOOOOOXXOOOOOOXX"), "X", 3)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE('X', 'OXOOOOOXXOOOOOOXX', 3))", _sql2.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = INSTR(?, ?, ?))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE(?, ?, ?))", _sql2.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam5 = cb3.parameter(String.class);
            ParameterExpression<Integer> intParam6 = cb3.parameter(Integer.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));
            cquery3.where(cb3.equal(root3.get(QuerySyntaxEntity_.intVal1), cb3.locate(strParam5, cb3.literal("X"), intParam6)));

            query = em.createQuery(cquery3);
            query.setParameter(strParam5, "OXOOOOOXXOOOOOOXX");
            query.setParameter(intParam6, 3);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE('X', 'OXOOOOOXXOOOOOOXX', 3))", _sql2.remove(0));
            } else if(platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE('X', ?, ?))", _sql2.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = INSTR(?, ?, ?))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE(?, ?, ?))", _sql2.remove(0));
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
    public void testLocate2_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = LOCATE(?1, ?2, ?3)");
            query.setParameter(1, "X");
            query.setParameter(2, "OXOOOOOXXOOOOOOXX");
            query.setParameter(3, 3);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE('X', 'OXOOOOOXXOOOOOOXX', 3))", _sql3.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = INSTR(?, ?, ?))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE(?, ?, ?))", _sql3.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = LOCATE('X', 'OXOOOOOXXOOOOOOXX', 3)");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE('X', 'OXOOOOOXXOOOOOOXX', 3))", _sql3.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = INSTR(?, ?, ?))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE(?, ?, ?))", _sql3.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = LOCATE('X', ?1, 3)");
            query.setParameter(1, "OXOOOOOXXOOOOOOXX");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE('X', 'OXOOOOOXXOOOOOOXX', 3))", _sql3.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = INSTR(?, ?, ?))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE(?, ?, ?))", _sql3.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            ParameterExpression<String> strParam2 = cb.parameter(String.class);
            ParameterExpression<Integer> intParam3 = cb.parameter(Integer.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.intVal1), cb.locate(strParam2, strParam1, intParam3)));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "X");
            query.setParameter(strParam2, "OXOOOOOXXOOOOOOXX");
            query.setParameter(intParam3, 3);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE('X', 'OXOOOOOXXOOOOOOXX', 3))", _sql3.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = INSTR(?, ?, ?))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE(?, ?, ?))", _sql3.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), cb2.locate(cb2.literal("OXOOOOOXXOOOOOOXX"), "X", 3)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE('X', 'OXOOOOOXXOOOOOOXX', 3))", _sql3.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = INSTR(?, ?, ?))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE(?, ?, ?))", _sql3.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam5 = cb3.parameter(String.class);
            ParameterExpression<Integer> intParam6 = cb3.parameter(Integer.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));
            cquery3.where(cb3.equal(root3.get(QuerySyntaxEntity_.intVal1), cb3.locate(strParam5, cb3.literal("X"), intParam6)));

            query = em.createQuery(cquery3);
            query.setParameter(strParam5, "OXOOOOOXXOOOOOOXX");
            query.setParameter(intParam6, 3);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE('X', 'OXOOOOOXXOOOOOOXX', 3))", _sql3.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = INSTR(?, ?, ?))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = LOCATE(?, ?, ?))", _sql3.remove(0));
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
    public void testTrim2_1_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = TRIM(?1 FROM ?2)");
            query.setParameter(1, 'A');
            query.setParameter(2, "AAAHELLO WORDAA");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM('A' FROM 'AAAHELLO WORDAA'))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(? FROM ?))", _sql.remove(0));
            }

            query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = TRIM('A' FROM 'AAAHELLO WORDAA')");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM('A' FROM 'AAAHELLO WORDAA'))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(? FROM ?))", _sql.remove(0));
            }

            query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = TRIM('A' FROM ?2)");
            query.setParameter(2, "AAAHELLO WORDAA");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM('A' FROM 'AAAHELLO WORDAA'))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(? FROM ?))", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Character> charParam1 = cb.parameter(Character.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.strVal1));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.strVal1), cb.trim(charParam1, strParam1)));

            query = em.createQuery(cquery);
            query.setParameter(charParam1, 'A');
            query.setParameter(strParam1, "AAAHELLO WORDAA");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM('A' FROM 'AAAHELLO WORDAA'))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(? FROM ?))", _sql.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.strVal1));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.strVal1), cb2.trim(cb2.literal('A'), cb2.literal("AAAHELLO WORDAA"))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM('A' FROM 'AAAHELLO WORDAA'))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(? FROM ?))", _sql.remove(0));
            }

            query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = TRIM('A' FROM ?2)");
            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam2 = cb3.parameter(String.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.strVal1));
            cquery3.where(cb3.equal(root3.get(QuerySyntaxEntity_.strVal1), cb3.trim(cb3.literal('A'), strParam2)));

            query = em.createQuery(cquery3);
            query.setParameter(strParam2, "AAAHELLO WORDAA");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM('A' FROM 'AAAHELLO WORDAA'))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(? FROM ?))", _sql.remove(0));
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
    public void testTrim2_1_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = TRIM(?1 FROM ?2)");
            query.setParameter(1, 'A');
            query.setParameter(2, "AAAHELLO WORDAA");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM('A' FROM 'AAAHELLO WORDAA'))", _sql2.remove(0));
            } else if(platform.isDB2()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM('A' FROM ?))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(? FROM ?))", _sql2.remove(0));
            }

            query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = TRIM('A' FROM 'AAAHELLO WORDAA')");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM('A' FROM 'AAAHELLO WORDAA'))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(? FROM ?))", _sql2.remove(0));
            }

            query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = TRIM('A' FROM ?2)");
            query.setParameter(2, "AAAHELLO WORDAA");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM('A' FROM 'AAAHELLO WORDAA'))", _sql2.remove(0));
            } else if(platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM('A' FROM ?))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(? FROM ?))", _sql2.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Character> charParam1 = cb.parameter(Character.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.strVal1));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.strVal1), cb.trim(charParam1, strParam1)));

            query = em.createQuery(cquery);
            query.setParameter(charParam1, 'A');
            query.setParameter(strParam1, "AAAHELLO WORDAA");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM('A' FROM 'AAAHELLO WORDAA'))", _sql2.remove(0));
            } else if(platform.isDB2()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM('A' FROM ?))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(? FROM ?))", _sql2.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.strVal1));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.strVal1), cb2.trim(cb2.literal('A'), cb2.literal("AAAHELLO WORDAA"))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM('A' FROM 'AAAHELLO WORDAA'))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(? FROM ?))", _sql2.remove(0));
            }

            query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = TRIM('A' FROM ?2)");
            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam2 = cb3.parameter(String.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.strVal1));
            cquery3.where(cb3.equal(root3.get(QuerySyntaxEntity_.strVal1), cb3.trim(cb3.literal('A'), strParam2)));

            query = em.createQuery(cquery3);
            query.setParameter(strParam2, "AAAHELLO WORDAA");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM('A' FROM 'AAAHELLO WORDAA'))", _sql2.remove(0));
            } else if(platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM('A' FROM ?))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(? FROM ?))", _sql2.remove(0));
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
    public void testTrim2_1_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = TRIM(?1 FROM ?2)");
            query.setParameter(1, 'A');
            query.setParameter(2, "AAAHELLO WORDAA");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM('A' FROM 'AAAHELLO WORDAA'))", _sql3.remove(0));
            } else if(platform.isDB2()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM('A' FROM ?))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(? FROM ?))", _sql3.remove(0));
            }

            query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = TRIM('A' FROM 'AAAHELLO WORDAA')");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM('A' FROM 'AAAHELLO WORDAA'))", _sql3.remove(0));
            } else if(platform.isDB2()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM('A' FROM ?))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(? FROM ?))", _sql3.remove(0));
            }

            query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = TRIM('A' FROM ?2)");
            query.setParameter(2, "AAAHELLO WORDAA");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM('A' FROM 'AAAHELLO WORDAA'))", _sql3.remove(0));
            } else if(platform.isDB2()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM('A' FROM ?))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(? FROM ?))", _sql3.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Character> charParam1 = cb.parameter(Character.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.strVal1));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.strVal1), cb.trim(charParam1, strParam1)));

            query = em.createQuery(cquery);
            query.setParameter(charParam1, 'A');
            query.setParameter(strParam1, "AAAHELLO WORDAA");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM('A' FROM 'AAAHELLO WORDAA'))", _sql3.remove(0));
            } else if(platform.isDB2()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM('A' FROM ?))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(? FROM ?))", _sql3.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.strVal1));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.strVal1), cb2.trim(cb2.literal('A'), cb2.literal("AAAHELLO WORDAA"))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM('A' FROM 'AAAHELLO WORDAA'))", _sql3.remove(0));
            } else if(platform.isDB2()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM('A' FROM ?))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(? FROM ?))", _sql3.remove(0));
            }

            query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = TRIM('A' FROM ?2)");
            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam2 = cb3.parameter(String.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.strVal1));
            cquery3.where(cb3.equal(root3.get(QuerySyntaxEntity_.strVal1), cb3.trim(cb3.literal('A'), strParam2)));

            query = em.createQuery(cquery3);
            query.setParameter(strParam2, "AAAHELLO WORDAA");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM('A' FROM 'AAAHELLO WORDAA'))", _sql3.remove(0));
            } else if(platform.isDB2()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM('A' FROM ?))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(? FROM ?))", _sql3.remove(0));
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
    public void testRightTrim2_1_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = TRIM(TRAILING ?1 FROM ?2)");
            query.setParameter(1, 'A');
            query.setParameter(2, "AAAHELLO WORDAA");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(TRAILING 'A' FROM 'AAAHELLO WORDAA'))", _sql.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = RTRIM(?, ?))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(TRAILING ? FROM ?))", _sql.remove(0));
            }

            query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = TRIM(TRAILING 'A' FROM 'AAAHELLO WORDAA')");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(TRAILING 'A' FROM 'AAAHELLO WORDAA'))", _sql.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = RTRIM(?, ?))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(TRAILING ? FROM ?))", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Character> chaParam1 = cb.parameter(Character.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.strVal1));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.strVal1), cb.trim(Trimspec.TRAILING, chaParam1, strParam1)));

            query = em.createQuery(cquery);
            query.setParameter(chaParam1, 'A');
            query.setParameter(strParam1, "AAAHELLO WORDAA");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(TRAILING 'A' FROM 'AAAHELLO WORDAA'))", _sql.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = RTRIM(?, ?))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(TRAILING ? FROM ?))", _sql.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.strVal1));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.strVal1), cb2.trim(Trimspec.TRAILING, 'A', cb2.literal("AAAHELLO WORDAA"))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(TRAILING 'A' FROM 'AAAHELLO WORDAA'))", _sql.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = RTRIM(?, ?))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(TRAILING ? FROM ?))", _sql.remove(0));
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
    public void testRightTrim2_1_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = TRIM(TRAILING ?1 FROM ?2)");
            query.setParameter(1, 'A');
            query.setParameter(2, "AAAHELLO WORDAA");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(TRAILING 'A' FROM 'AAAHELLO WORDAA'))", _sql2.remove(0));
            } else if(platform.isDB2()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(TRAILING 'A' FROM ?))", _sql2.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = RTRIM(?, ?))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(TRAILING ? FROM ?))", _sql2.remove(0));
            }

            query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = TRIM(TRAILING 'A' FROM 'AAAHELLO WORDAA')");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(TRAILING 'A' FROM 'AAAHELLO WORDAA'))", _sql2.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = RTRIM(?, ?))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(TRAILING ? FROM ?))", _sql2.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Character> chaParam1 = cb.parameter(Character.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.strVal1));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.strVal1), cb.trim(Trimspec.TRAILING, chaParam1, strParam1)));

            query = em.createQuery(cquery);
            query.setParameter(chaParam1, 'A');
            query.setParameter(strParam1, "AAAHELLO WORDAA");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(TRAILING 'A' FROM 'AAAHELLO WORDAA'))", _sql2.remove(0));
            } else if(platform.isDB2()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(TRAILING 'A' FROM ?))", _sql2.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = RTRIM(?, ?))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(TRAILING ? FROM ?))", _sql2.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.strVal1));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.strVal1), cb2.trim(Trimspec.TRAILING, 'A', cb2.literal("AAAHELLO WORDAA"))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(TRAILING 'A' FROM 'AAAHELLO WORDAA'))", _sql2.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = RTRIM(?, ?))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(TRAILING ? FROM ?))", _sql2.remove(0));
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
    public void testRightTrim2_1_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = TRIM(TRAILING ?1 FROM ?2)");
            query.setParameter(1, 'A');
            query.setParameter(2, "AAAHELLO WORDAA");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(TRAILING 'A' FROM 'AAAHELLO WORDAA'))", _sql3.remove(0));
            } else if(platform.isDB2()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(TRAILING 'A' FROM ?))", _sql3.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = RTRIM(?, ?))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(TRAILING ? FROM ?))", _sql3.remove(0));
            }

            query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = TRIM(TRAILING 'A' FROM 'AAAHELLO WORDAA')");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(TRAILING 'A' FROM 'AAAHELLO WORDAA'))", _sql3.remove(0));
            } else if(platform.isDB2()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(TRAILING 'A' FROM ?))", _sql3.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = RTRIM(?, ?))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(TRAILING ? FROM ?))", _sql3.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Character> chaParam1 = cb.parameter(Character.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.strVal1));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.strVal1), cb.trim(Trimspec.TRAILING, chaParam1, strParam1)));

            query = em.createQuery(cquery);
            query.setParameter(chaParam1, 'A');
            query.setParameter(strParam1, "AAAHELLO WORDAA");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(TRAILING 'A' FROM 'AAAHELLO WORDAA'))", _sql3.remove(0));
            } else if(platform.isDB2()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(TRAILING 'A' FROM ?))", _sql3.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = RTRIM(?, ?))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(TRAILING ? FROM ?))", _sql3.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.strVal1));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.strVal1), cb2.trim(Trimspec.TRAILING, 'A', cb2.literal("AAAHELLO WORDAA"))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(TRAILING 'A' FROM 'AAAHELLO WORDAA'))", _sql3.remove(0));
            } else if(platform.isDB2()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(TRAILING 'A' FROM ?))", _sql3.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = RTRIM(?, ?))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(TRAILING ? FROM ?))", _sql3.remove(0));
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
    public void testLeftTrim2_1_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = TRIM(LEADING ?1 FROM ?2)");
            query.setParameter(1, 'A');
            query.setParameter(2, "AAAHELLO WORDAA");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(LEADING 'A' FROM 'AAAHELLO WORDAA'))", _sql.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LTRIM(?, ?))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(LEADING ? FROM ?))", _sql.remove(0));
            }

            query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = TRIM(LEADING 'A' FROM 'AAAHELLO WORDAA')");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(LEADING 'A' FROM 'AAAHELLO WORDAA'))", _sql.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LTRIM(?, ?))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(LEADING ? FROM ?))", _sql.remove(0));
            }

            query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = TRIM(LEADING 'A' FROM ?2)");
            query.setParameter(2, "AAAHELLO WORDAA");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(LEADING 'A' FROM 'AAAHELLO WORDAA'))", _sql.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LTRIM(?, ?))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(LEADING ? FROM ?))", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Character> charParam1 = cb.parameter(Character.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.strVal1));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.strVal1), cb.trim(Trimspec.LEADING, charParam1, strParam1)));

            query = em.createQuery(cquery);
            query.setParameter(charParam1, 'A');
            query.setParameter(strParam1, "AAAHELLO WORDAA");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(LEADING 'A' FROM 'AAAHELLO WORDAA'))", _sql.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LTRIM(?, ?))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(LEADING ? FROM ?))", _sql.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.strVal1));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.strVal1), cb2.trim(Trimspec.LEADING, cb2.literal('A'), cb2.literal("AAAHELLO WORDAA"))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(LEADING 'A' FROM 'AAAHELLO WORDAA'))", _sql.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LTRIM(?, ?))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(LEADING ? FROM ?))", _sql.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam2 = cb3.parameter(String.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.strVal1));
            cquery3.where(cb3.equal(root3.get(QuerySyntaxEntity_.strVal1), cb3.trim(Trimspec.LEADING, cb3.literal('A'), strParam2)));

            query = em.createQuery(cquery3);
            query.setParameter(strParam2, "AAAHELLO WORDAA");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(LEADING 'A' FROM 'AAAHELLO WORDAA'))", _sql.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LTRIM(?, ?))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(LEADING ? FROM ?))", _sql.remove(0));
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
    public void testLeftTrim2_1_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = TRIM(LEADING ?1 FROM ?2)");
            query.setParameter(1, 'A');
            query.setParameter(2, "AAAHELLO WORDAA");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(LEADING 'A' FROM 'AAAHELLO WORDAA'))", _sql2.remove(0));
            } else if(platform.isDB2()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(LEADING 'A' FROM ?))", _sql2.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LTRIM(?, ?))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(LEADING ? FROM ?))", _sql2.remove(0));
            }

            query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = TRIM(LEADING 'A' FROM 'AAAHELLO WORDAA')");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(LEADING 'A' FROM 'AAAHELLO WORDAA'))", _sql2.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LTRIM(?, ?))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(LEADING ? FROM ?))", _sql2.remove(0));
            }

            query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = TRIM(LEADING 'A' FROM ?2)");
            query.setParameter(2, "AAAHELLO WORDAA");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(LEADING 'A' FROM 'AAAHELLO WORDAA'))", _sql2.remove(0));
            } else if(platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(LEADING 'A' FROM ?))", _sql2.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LTRIM(?, ?))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(LEADING ? FROM ?))", _sql2.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Character> charParam1 = cb.parameter(Character.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.strVal1));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.strVal1), cb.trim(Trimspec.LEADING, charParam1, strParam1)));

            query = em.createQuery(cquery);
            query.setParameter(charParam1, 'A');
            query.setParameter(strParam1, "AAAHELLO WORDAA");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(LEADING 'A' FROM 'AAAHELLO WORDAA'))", _sql2.remove(0));
            } else if(platform.isDB2()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(LEADING 'A' FROM ?))", _sql2.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LTRIM(?, ?))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(LEADING ? FROM ?))", _sql2.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.strVal1));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.strVal1), cb2.trim(Trimspec.LEADING, cb2.literal('A'), cb2.literal("AAAHELLO WORDAA"))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(LEADING 'A' FROM 'AAAHELLO WORDAA'))", _sql2.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LTRIM(?, ?))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(LEADING ? FROM ?))", _sql2.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam2 = cb3.parameter(String.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.strVal1));
            cquery3.where(cb3.equal(root3.get(QuerySyntaxEntity_.strVal1), cb3.trim(Trimspec.LEADING, cb3.literal('A'), strParam2)));

            query = em.createQuery(cquery3);
            query.setParameter(strParam2, "AAAHELLO WORDAA");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(LEADING 'A' FROM 'AAAHELLO WORDAA'))", _sql2.remove(0));
            } else if(platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(LEADING 'A' FROM ?))", _sql2.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LTRIM(?, ?))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(LEADING ? FROM ?))", _sql2.remove(0));
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
    public void testLeftTrim2_1_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = TRIM(LEADING ?1 FROM ?2)");
            query.setParameter(1, 'A');
            query.setParameter(2, "AAAHELLO WORDAA");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(LEADING 'A' FROM 'AAAHELLO WORDAA'))", _sql3.remove(0));
            } else if(platform.isDB2()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(LEADING 'A' FROM ?))", _sql3.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LTRIM(?, ?))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(LEADING ? FROM ?))", _sql3.remove(0));
            }

            query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = TRIM(LEADING 'A' FROM 'AAAHELLO WORDAA')");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(LEADING 'A' FROM 'AAAHELLO WORDAA'))", _sql3.remove(0));
            } else if(platform.isDB2()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(LEADING 'A' FROM ?))", _sql3.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LTRIM(?, ?))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(LEADING ? FROM ?))", _sql3.remove(0));
            }

            query = em.createQuery("SELECT s.strVal1 FROM QuerySyntaxEntity s WHERE s.strVal1 = TRIM(LEADING 'A' FROM ?2)");
            query.setParameter(2, "AAAHELLO WORDAA");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(LEADING 'A' FROM 'AAAHELLO WORDAA'))", _sql3.remove(0));
            } else if(platform.isDB2()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(LEADING 'A' FROM ?))", _sql3.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LTRIM(?, ?))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(LEADING ? FROM ?))", _sql3.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Character> charParam1 = cb.parameter(Character.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.strVal1));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.strVal1), cb.trim(Trimspec.LEADING, charParam1, strParam1)));

            query = em.createQuery(cquery);
            query.setParameter(charParam1, 'A');
            query.setParameter(strParam1, "AAAHELLO WORDAA");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(LEADING 'A' FROM 'AAAHELLO WORDAA'))", _sql3.remove(0));
            } else if(platform.isDB2()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(LEADING 'A' FROM ?))", _sql3.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LTRIM(?, ?))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(LEADING ? FROM ?))", _sql3.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.strVal1));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.strVal1), cb2.trim(Trimspec.LEADING, cb2.literal('A'), cb2.literal("AAAHELLO WORDAA"))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(LEADING 'A' FROM 'AAAHELLO WORDAA'))", _sql3.remove(0));
            } else if(platform.isDB2()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(LEADING 'A' FROM ?))", _sql3.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LTRIM(?, ?))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(LEADING ? FROM ?))", _sql3.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            ParameterExpression<String> strParam2 = cb3.parameter(String.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.strVal1));
            cquery3.where(cb3.equal(root3.get(QuerySyntaxEntity_.strVal1), cb3.trim(Trimspec.LEADING, cb3.literal('A'), strParam2)));

            query = em.createQuery(cquery3);
            query.setParameter(strParam2, "AAAHELLO WORDAA");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(LEADING 'A' FROM 'AAAHELLO WORDAA'))", _sql3.remove(0));
            } else if(platform.isDB2()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(LEADING 'A' FROM ?))", _sql3.remove(0));
            } else if(platform.isOracle()) {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = LTRIM(?, ?))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM QUERYSYNTAXENTITY WHERE (STRVAL1 = TRIM(LEADING ? FROM ?))", _sql3.remove(0));
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
    public void testAny1_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?1 > ANY (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 = ?2)");
            query.setParameter(1, 5);
            query.setParameter(2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (? > ANY(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 5 > ANY (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 = 9)");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (5 > ANY(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = 9)))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (? > ANY(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 5 > ANY (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 = ?1)");
            query.setParameter(1, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (5 > ANY(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (? > ANY(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql.remove(0));
            }

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
            cquery.where(cb.greaterThan(intValue1, cb.any(subquery)));

            query = em.createQuery(cquery);
            query.setParameter(intValue1, 5);
            query.setParameter(intValue2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (? > ANY(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery2 = cb2.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            Subquery<Integer> subquery2 = cquery2.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot2 = subquery2.from(QuerySyntaxEntity.class);
            subquery2.select(subroot2.get(QuerySyntaxEntity_.intVal2));
            subquery2.where(cb2.equal(subroot2.get(QuerySyntaxEntity_.intVal2), 9));

            cquery2.where(cb2.greaterThan(cb2.literal(5), cb2.any(subquery2)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (5 > ANY(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = 9)))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (? > ANY(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery3 = cb3.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intValue3 = cb3.parameter(Integer.class);
            Subquery<Integer> subquery3 = cquery3.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot3 = subquery3.from(QuerySyntaxEntity.class);
            subquery3.select(subroot3.get(QuerySyntaxEntity_.intVal2));
            subquery3.where(cb3.equal(subroot3.get(QuerySyntaxEntity_.intVal2), intValue3));

            cquery3.where(cb3.greaterThan(cb3.literal(5), cb3.any(subquery3)));

            query = em.createQuery(cquery3);
            query.setParameter(intValue3, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (5 > ANY(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (? > ANY(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql.remove(0));
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
    public void testAny1_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?1 > ANY (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 = ?2)");
            query.setParameter(1, 5);
            query.setParameter(2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (? > ANY(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql2.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 5 > ANY (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 = 9)");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (5 > ANY(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = 9)))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (? > ANY(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql2.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 5 > ANY (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 = ?1)");
            query.setParameter(1, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (5 > ANY(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (? > ANY(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql2.remove(0));
            }

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
            cquery.where(cb.greaterThan(intValue1, cb.any(subquery)));

            query = em.createQuery(cquery);
            query.setParameter(intValue1, 5);
            query.setParameter(intValue2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (? > ANY(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql2.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery2 = cb2.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            Subquery<Integer> subquery2 = cquery2.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot2 = subquery2.from(QuerySyntaxEntity.class);
            subquery2.select(subroot2.get(QuerySyntaxEntity_.intVal2));
            subquery2.where(cb2.equal(subroot2.get(QuerySyntaxEntity_.intVal2), 9));

            cquery2.where(cb2.greaterThan(cb2.literal(5), cb2.any(subquery2)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (5 > ANY(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = 9)))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (? > ANY(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql2.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery3 = cb3.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intValue3 = cb3.parameter(Integer.class);
            Subquery<Integer> subquery3 = cquery3.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot3 = subquery3.from(QuerySyntaxEntity.class);
            subquery3.select(subroot3.get(QuerySyntaxEntity_.intVal2));
            subquery3.where(cb3.equal(subroot3.get(QuerySyntaxEntity_.intVal2), intValue3));

            cquery3.where(cb3.greaterThan(cb3.literal(5), cb3.any(subquery3)));

            query = em.createQuery(cquery3);
            query.setParameter(intValue3, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (5 > ANY(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (? > ANY(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql2.remove(0));
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
    public void testAny1_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?1 > ANY (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 = ?2)");
            query.setParameter(1, 5);
            query.setParameter(2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (? > ANY(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql3.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 5 > ANY (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 = 9)");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (? > ANY(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql3.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 5 > ANY (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 = ?1)");
            query.setParameter(1, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (? > ANY(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql3.remove(0));

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
            cquery.where(cb.greaterThan(intValue1, cb.any(subquery)));

            query = em.createQuery(cquery);
            query.setParameter(intValue1, 5);
            query.setParameter(intValue2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (? > ANY(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql3.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery2 = cb2.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            Subquery<Integer> subquery2 = cquery2.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot2 = subquery2.from(QuerySyntaxEntity.class);
            subquery2.select(subroot2.get(QuerySyntaxEntity_.intVal2));
            subquery2.where(cb2.equal(subroot2.get(QuerySyntaxEntity_.intVal2), 9));

            cquery2.where(cb2.greaterThan(cb2.literal(5), cb2.any(subquery2)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (? > ANY(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql3.remove(0));

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery3 = cb3.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intValue3 = cb3.parameter(Integer.class);
            Subquery<Integer> subquery3 = cquery3.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot3 = subquery3.from(QuerySyntaxEntity.class);
            subquery3.select(subroot3.get(QuerySyntaxEntity_.intVal2));
            subquery3.where(cb3.equal(subroot3.get(QuerySyntaxEntity_.intVal2), intValue3));

            cquery3.where(cb3.greaterThan(cb3.literal(5), cb3.any(subquery3)));

            query = em.createQuery(cquery3);
            query.setParameter(intValue3, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (? > ANY(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql3.remove(0));
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
    public void testAll1_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?1 = ALL (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 = ?2)");
            query.setParameter(1, 5);
            query.setParameter(2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (? = ALL(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 5 = ALL (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 = 9)");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (5 = ALL(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = 9)))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (? = ALL(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 5 = ALL (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 = ?1)");
            query.setParameter(1, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (5 = ALL(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (? = ALL(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql.remove(0));
            }

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
            cquery.where(cb.equal(intValue1, cb.all(subquery)));

            query = em.createQuery(cquery);
            query.setParameter(intValue1, 5);
            query.setParameter(intValue2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (? = ALL(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery2 = cb2.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            Subquery<Integer> subquery2 = cquery2.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot2 = subquery2.from(QuerySyntaxEntity.class);
            subquery2.select(subroot2.get(QuerySyntaxEntity_.intVal2));
            subquery2.where(cb2.equal(subroot2.get(QuerySyntaxEntity_.intVal2), 9));

            cquery2.where(cb2.equal(cb2.literal(5), cb2.all(subquery2)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (5 = ALL(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = 9)))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (? = ALL(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery3 = cb3.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intValue3 = cb3.parameter(Integer.class);
            Subquery<Integer> subquery3 = cquery3.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot3 = subquery3.from(QuerySyntaxEntity.class);
            subquery3.select(subroot3.get(QuerySyntaxEntity_.intVal2));
            subquery3.where(cb3.equal(subroot3.get(QuerySyntaxEntity_.intVal2), intValue3));

            cquery3.where(cb3.equal(cb3.literal(5), cb3.all(subquery3)));

            query = em.createQuery(cquery3);
            query.setParameter(intValue3, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (5 = ALL(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (? = ALL(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql.remove(0));
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
    public void testAll1_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?1 = ALL (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 = ?2)");
            query.setParameter(1, 5);
            query.setParameter(2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (? = ALL(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql2.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 5 = ALL (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 = 9)");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (5 = ALL(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = 9)))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (? = ALL(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql2.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 5 = ALL (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 = ?1)");
            query.setParameter(1, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (5 = ALL(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (? = ALL(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql2.remove(0));
            }

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
            cquery.where(cb.equal(intValue1, cb.all(subquery)));

            query = em.createQuery(cquery);
            query.setParameter(intValue1, 5);
            query.setParameter(intValue2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (? = ALL(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql2.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery2 = cb2.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            Subquery<Integer> subquery2 = cquery2.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot2 = subquery2.from(QuerySyntaxEntity.class);
            subquery2.select(subroot2.get(QuerySyntaxEntity_.intVal2));
            subquery2.where(cb2.equal(subroot2.get(QuerySyntaxEntity_.intVal2), 9));

            cquery2.where(cb2.equal(cb2.literal(5), cb2.all(subquery2)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (5 = ALL(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = 9)))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (? = ALL(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql2.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery3 = cb3.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intValue3 = cb3.parameter(Integer.class);
            Subquery<Integer> subquery3 = cquery3.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot3 = subquery3.from(QuerySyntaxEntity.class);
            subquery3.select(subroot3.get(QuerySyntaxEntity_.intVal2));
            subquery3.where(cb3.equal(subroot3.get(QuerySyntaxEntity_.intVal2), intValue3));

            cquery3.where(cb3.equal(cb3.literal(5), cb3.all(subquery3)));

            query = em.createQuery(cquery3);
            query.setParameter(intValue3, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (5 = ALL(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (? = ALL(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql2.remove(0));
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
    public void testAll1_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?1 = ALL (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 = ?2)");
            query.setParameter(1, 5);
            query.setParameter(2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (? = ALL(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql3.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 5 = ALL (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 = 9)");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (? = ALL(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql3.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 5 = ALL (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 = ?1)");
            query.setParameter(1, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (? = ALL(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql3.remove(0));

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
            cquery.where(cb.equal(intValue1, cb.all(subquery)));

            query = em.createQuery(cquery);
            query.setParameter(intValue1, 5);
            query.setParameter(intValue2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (? = ALL(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql3.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery2 = cb2.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            Subquery<Integer> subquery2 = cquery2.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot2 = subquery2.from(QuerySyntaxEntity.class);
            subquery2.select(subroot2.get(QuerySyntaxEntity_.intVal2));
            subquery2.where(cb2.equal(subroot2.get(QuerySyntaxEntity_.intVal2), 9));

            cquery2.where(cb2.equal(cb2.literal(5), cb2.all(subquery2)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (? = ALL(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql3.remove(0));

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery3 = cb3.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intValue3 = cb3.parameter(Integer.class);
            Subquery<Integer> subquery3 = cquery3.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot3 = subquery3.from(QuerySyntaxEntity.class);
            subquery3.select(subroot3.get(QuerySyntaxEntity_.intVal2));
            subquery3.where(cb3.equal(subroot3.get(QuerySyntaxEntity_.intVal2), intValue3));

            cquery3.where(cb3.equal(cb3.literal(5), cb3.all(subquery3)));

            query = em.createQuery(cquery3);
            query.setParameter(intValue3, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (? = ALL(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql3.remove(0));
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
    public void testSome1_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?1 = SOME (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 = ?2)");
            query.setParameter(1, 5);
            query.setParameter(2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (? = SOME(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 5 = SOME (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 = 9)");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (5 = SOME(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = 9)))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (? = SOME(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 5 = SOME (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 = ?1)");
            query.setParameter(1, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (5 = SOME(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (? = SOME(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql.remove(0));
            }

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
            cquery.where(cb.equal(intValue1, cb.some(subquery)));

            query = em.createQuery(cquery);
            query.setParameter(intValue1, 5);
            query.setParameter(intValue2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (? = SOME(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery2 = cb2.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            Subquery<Integer> subquery2 = cquery2.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot2 = subquery2.from(QuerySyntaxEntity.class);
            subquery2.select(subroot2.get(QuerySyntaxEntity_.intVal2));
            subquery2.where(cb2.equal(subroot2.get(QuerySyntaxEntity_.intVal2), 9));

            cquery2.where(cb2.equal(cb2.literal(5), cb2.some(subquery2)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (5 = SOME(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = 9)))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (? = SOME(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery3 = cb3.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intValue3 = cb3.parameter(Integer.class);
            Subquery<Integer> subquery3 = cquery3.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot3 = subquery3.from(QuerySyntaxEntity.class);
            subquery3.select(subroot3.get(QuerySyntaxEntity_.intVal2));
            subquery3.where(cb3.equal(subroot3.get(QuerySyntaxEntity_.intVal2), intValue3));

            cquery3.where(cb3.equal(cb3.literal(5), cb3.some(subquery3)));

            query = em.createQuery(cquery3);
            query.setParameter(intValue3, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (5 = SOME(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (? = SOME(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql.remove(0));
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
    public void testSome1_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?1 = SOME (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 = ?2)");
            query.setParameter(1, 5);
            query.setParameter(2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (? = SOME(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql2.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 5 = SOME (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 = 9)");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (5 = SOME(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = 9)))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (? = SOME(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql2.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 5 = SOME (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 = ?1)");
            query.setParameter(1, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (5 = SOME(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (? = SOME(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql2.remove(0));
            }

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
            cquery.where(cb.equal(intValue1, cb.some(subquery)));

            query = em.createQuery(cquery);
            query.setParameter(intValue1, 5);
            query.setParameter(intValue2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (? = SOME(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql2.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery2 = cb2.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            Subquery<Integer> subquery2 = cquery2.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot2 = subquery2.from(QuerySyntaxEntity.class);
            subquery2.select(subroot2.get(QuerySyntaxEntity_.intVal2));
            subquery2.where(cb2.equal(subroot2.get(QuerySyntaxEntity_.intVal2), 9));

            cquery2.where(cb2.equal(cb2.literal(5), cb2.some(subquery2)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (5 = SOME(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = 9)))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (? = SOME(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql2.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery3 = cb3.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intValue3 = cb3.parameter(Integer.class);
            Subquery<Integer> subquery3 = cquery3.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot3 = subquery3.from(QuerySyntaxEntity.class);
            subquery3.select(subroot3.get(QuerySyntaxEntity_.intVal2));
            subquery3.where(cb3.equal(subroot3.get(QuerySyntaxEntity_.intVal2), intValue3));

            cquery3.where(cb3.equal(cb3.literal(5), cb3.some(subquery3)));

            query = em.createQuery(cquery3);
            query.setParameter(intValue3, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (5 = SOME(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (? = SOME(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql2.remove(0));
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
    public void testSome1_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();

        try {
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ?1 = SOME (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 = ?2)");
            query.setParameter(1, 5);
            query.setParameter(2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (? = SOME(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql3.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 5 = SOME (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 = 9)");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (? = SOME(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql3.remove(0));

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE 5 = SOME (SELECT u.intVal2 FROM QuerySyntaxEntity u WHERE u.intVal2 = ?1)");
            query.setParameter(1, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (? = SOME(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql3.remove(0));

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
            cquery.where(cb.equal(intValue1, cb.some(subquery)));

            query = em.createQuery(cquery);
            query.setParameter(intValue1, 5);
            query.setParameter(intValue2, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (? = SOME(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql3.remove(0));

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery2 = cb2.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));

            Subquery<Integer> subquery2 = cquery2.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot2 = subquery2.from(QuerySyntaxEntity.class);
            subquery2.select(subroot2.get(QuerySyntaxEntity_.intVal2));
            subquery2.where(cb2.equal(subroot2.get(QuerySyntaxEntity_.intVal2), 9));

            cquery2.where(cb2.equal(cb2.literal(5), cb2.some(subquery2)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (? = SOME(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql3.remove(0));

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Integer[]> cquery3 = cb3.createQuery(Integer[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1));

            ParameterExpression<Integer> intValue3 = cb3.parameter(Integer.class);
            Subquery<Integer> subquery3 = cquery3.subquery(Integer.class);
            Root<QuerySyntaxEntity> subroot3 = subquery3.from(QuerySyntaxEntity.class);
            subquery3.select(subroot3.get(QuerySyntaxEntity_.intVal2));
            subquery3.where(cb3.equal(subroot3.get(QuerySyntaxEntity_.intVal2), intValue3));

            cquery3.where(cb3.equal(cb3.literal(5), cb3.some(subquery3)));

            query = em.createQuery(cquery3);
            query.setParameter(intValue3, 9);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT t0.INTVAL1 FROM QUERYSYNTAXENTITY t0 WHERE (? = SOME(SELECT t1.INTVAL2 FROM QUERYSYNTAXENTITY t1 WHERE (t1.INTVAL2 = ?)))", _sql3.remove(0));
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
    public void testSize1_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT 1 FROM QuerySyntaxEntity s WHERE SIZE(s.colVal1) = ?1");
            query.setParameter(1, 36);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY t0 WHERE ((SELECT COUNT(t1.ID) FROM COLTABLE1 t2, QUERYSYNTAXENTITY t1 WHERE (t2.ent_id = t0.ID)) = 36)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY t0 WHERE ((SELECT COUNT(t1.ID) FROM COLTABLE1 t2, QUERYSYNTAXENTITY t1 WHERE (t2.ent_id = t0.ID)) = ?)", _sql.remove(0));
            }

            query = em.createQuery("SELECT 1 FROM QuerySyntaxEntity s WHERE SIZE(s.colVal1) = 36");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY t0 WHERE ((SELECT COUNT(t1.ID) FROM COLTABLE1 t2, QUERYSYNTAXENTITY t1 WHERE (t2.ent_id = t0.ID)) = 36)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY t0 WHERE ((SELECT COUNT(t1.ID) FROM COLTABLE1 t2, QUERYSYNTAXENTITY t1 WHERE (t2.ent_id = t0.ID)) = ?)", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            cquery.multiselect(cb.literal(1));
            cquery.where(cb.equal(cb.size(root.get(QuerySyntaxEntity_.colVal1)), intParam1));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 36);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY t0 WHERE ((SELECT COUNT(t1.ID) FROM COLTABLE1 t2, QUERYSYNTAXENTITY t1 WHERE (t2.ent_id = t0.ID)) = 36)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY t0 WHERE ((SELECT COUNT(t1.ID) FROM COLTABLE1 t2, QUERYSYNTAXENTITY t1 WHERE (t2.ent_id = t0.ID)) = ?)", _sql.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(cb2.literal(1));
            cquery2.where(cb2.equal(cb2.size(root2.get(QuerySyntaxEntity_.colVal1)), cb2.literal(36)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY t0 WHERE ((SELECT COUNT(t1.ID) FROM COLTABLE1 t2, QUERYSYNTAXENTITY t1 WHERE (t2.ent_id = t0.ID)) = 36)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY t0 WHERE ((SELECT COUNT(t1.ID) FROM COLTABLE1 t2, QUERYSYNTAXENTITY t1 WHERE (t2.ent_id = t0.ID)) = ?)", _sql.remove(0));
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
    public void testSize1_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT 1 FROM QuerySyntaxEntity s WHERE SIZE(s.colVal1) = ?1");
            query.setParameter(1, 36);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY t0 WHERE ((SELECT COUNT(t1.ID) FROM COLTABLE1 t2, QUERYSYNTAXENTITY t1 WHERE (t2.ent_id = t0.ID)) = ?)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY t0 WHERE ((SELECT COUNT(t1.ID) FROM COLTABLE1 t2, QUERYSYNTAXENTITY t1 WHERE (t2.ent_id = t0.ID)) = ?)", _sql2.remove(0));
            }

            query = em.createQuery("SELECT 1 FROM QuerySyntaxEntity s WHERE SIZE(s.colVal1) = 36");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY t0 WHERE ((SELECT COUNT(t1.ID) FROM COLTABLE1 t2, QUERYSYNTAXENTITY t1 WHERE (t2.ent_id = t0.ID)) = 36)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY t0 WHERE ((SELECT COUNT(t1.ID) FROM COLTABLE1 t2, QUERYSYNTAXENTITY t1 WHERE (t2.ent_id = t0.ID)) = ?)", _sql2.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            cquery.multiselect(cb.literal(1));
            cquery.where(cb.equal(cb.size(root.get(QuerySyntaxEntity_.colVal1)), intParam1));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 36);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY t0 WHERE ((SELECT COUNT(t1.ID) FROM COLTABLE1 t2, QUERYSYNTAXENTITY t1 WHERE (t2.ent_id = t0.ID)) = ?)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY t0 WHERE ((SELECT COUNT(t1.ID) FROM COLTABLE1 t2, QUERYSYNTAXENTITY t1 WHERE (t2.ent_id = t0.ID)) = ?)", _sql2.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(cb2.literal(1));
            cquery2.where(cb2.equal(cb2.size(root2.get(QuerySyntaxEntity_.colVal1)), cb2.literal(36)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY t0 WHERE ((SELECT COUNT(t1.ID) FROM COLTABLE1 t2, QUERYSYNTAXENTITY t1 WHERE (t2.ent_id = t0.ID)) = 36)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY t0 WHERE ((SELECT COUNT(t1.ID) FROM COLTABLE1 t2, QUERYSYNTAXENTITY t1 WHERE (t2.ent_id = t0.ID)) = ?)", _sql2.remove(0));
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
    public void testSize1_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("SELECT 1 FROM QuerySyntaxEntity s WHERE SIZE(s.colVal1) = ?1");
            query.setParameter(1, 36);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY t0 WHERE ((SELECT COUNT(t1.ID) FROM COLTABLE1 t2, QUERYSYNTAXENTITY t1 WHERE (t2.ent_id = t0.ID)) = ?)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY t0 WHERE ((SELECT COUNT(t1.ID) FROM COLTABLE1 t2, QUERYSYNTAXENTITY t1 WHERE (t2.ent_id = t0.ID)) = ?)", _sql3.remove(0));
            }

            query = em.createQuery("SELECT 1 FROM QuerySyntaxEntity s WHERE SIZE(s.colVal1) = 36");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY t0 WHERE ((SELECT COUNT(t1.ID) FROM COLTABLE1 t2, QUERYSYNTAXENTITY t1 WHERE (t2.ent_id = t0.ID)) = ?)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY t0 WHERE ((SELECT COUNT(t1.ID) FROM COLTABLE1 t2, QUERYSYNTAXENTITY t1 WHERE (t2.ent_id = t0.ID)) = ?)", _sql3.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            cquery.multiselect(cb.literal(1));
            cquery.where(cb.equal(cb.size(root.get(QuerySyntaxEntity_.colVal1)), intParam1));

            query = em.createQuery(cquery);
            query.setParameter(intParam1, 36);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY t0 WHERE ((SELECT COUNT(t1.ID) FROM COLTABLE1 t2, QUERYSYNTAXENTITY t1 WHERE (t2.ent_id = t0.ID)) = ?)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY t0 WHERE ((SELECT COUNT(t1.ID) FROM COLTABLE1 t2, QUERYSYNTAXENTITY t1 WHERE (t2.ent_id = t0.ID)) = ?)", _sql3.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(cb2.literal(1));
            cquery2.where(cb2.equal(cb2.size(root2.get(QuerySyntaxEntity_.colVal1)), cb2.literal(36)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT 1 FROM QUERYSYNTAXENTITY t0 WHERE ((SELECT COUNT(t1.ID) FROM COLTABLE1 t2, QUERYSYNTAXENTITY t1 WHERE (t2.ent_id = t0.ID)) = ?)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT ? FROM QUERYSYNTAXENTITY t0 WHERE ((SELECT COUNT(t1.ID) FROM COLTABLE1 t2, QUERYSYNTAXENTITY t1 WHERE (t2.ent_id = t0.ID)) = ?)", _sql3.remove(0));
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
    public void testCast1_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("SELECT CAST(?1 AS CHAR(2)) FROM QuerySyntaxEntity s");
            query.setParameter(1, 65);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT CAST(? AS CHAR(2)) FROM QUERYSYNTAXENTITY", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT CAST(? AS CHAR(2)) FROM QUERYSYNTAXENTITY", _sql.remove(0));
            }

            query = em.createQuery("SELECT CAST(65 AS CHAR(2)) FROM QuerySyntaxEntity s");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT CAST(65 AS CHAR(2)) FROM QUERYSYNTAXENTITY", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT CAST(? AS CHAR(2)) FROM QUERYSYNTAXENTITY", _sql.remove(0));
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
    public void testCast1_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("SELECT CAST(?1 AS CHAR(2)) FROM QuerySyntaxEntity s");
            query.setParameter(1, 65);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT CAST(? AS CHAR(2)) FROM QUERYSYNTAXENTITY", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT CAST(? AS CHAR(2)) FROM QUERYSYNTAXENTITY", _sql2.remove(0));
            }

            query = em.createQuery("SELECT CAST(65 AS CHAR(2)) FROM QuerySyntaxEntity s");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT CAST(65 AS CHAR(2)) FROM QUERYSYNTAXENTITY", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT CAST(? AS CHAR(2)) FROM QUERYSYNTAXENTITY", _sql2.remove(0));
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
    public void testCast1_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("SELECT CAST(?1 AS CHAR(2)) FROM QuerySyntaxEntity s");
            query.setParameter(1, 65);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT CAST(? AS CHAR(2)) FROM QUERYSYNTAXENTITY", _sql3.remove(0));

            query = em.createQuery("SELECT CAST(65 AS CHAR(2)) FROM QuerySyntaxEntity s");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            Assert.assertEquals("SELECT CAST(? AS CHAR(2)) FROM QUERYSYNTAXENTITY", _sql3.remove(0));
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
