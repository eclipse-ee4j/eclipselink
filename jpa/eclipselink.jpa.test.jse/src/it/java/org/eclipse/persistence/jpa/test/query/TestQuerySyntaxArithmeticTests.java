/*
 * Copyright (c) 2025, 2026 Contributors to the Eclipse Foundation.
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
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.ParameterExpression;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;

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
public class TestQuerySyntaxArithmeticTests {
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
    public void testABS1_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            populate(em, _sql, 36, 49);
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ABS(?1) = s.intVal1");
            for (int operand : new int[] { -36, -49, -81, -36 }) {
                query.setParameter(1, operand);
                if (operand == -81) {
                    Assert.assertTrue(query.getResultList().isEmpty());
                } else {
                    assertScalarResult(query, operand == -36 ? 36 : 49);
                }
                Assert.assertEquals(1, _sql.size());
                if(platform.isDB2Z() || platform.isDerby() || platform.isH2()) {
                    Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (ABS(" + operand + ") = INTVAL1)", _sql.remove(0));
                } else {
                    Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (ABS(?) = INTVAL1)", _sql.remove(0));
                }
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ABS(-36) = s.intVal1");
            assertScalarResult(query, 36);
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDerby() || platform.isH2()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (ABS(-36) = INTVAL1)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (ABS(?) = INTVAL1)", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));
            cquery.where(cb.equal(cb.abs(intParam1), root.get(QuerySyntaxEntity_.intVal1)));

            query = em.createQuery(cquery);
            for (int operand : new int[] { -36, -49, -81, -36 }) {
                query.setParameter(intParam1, operand);
                if (operand == -81) {
                    Assert.assertTrue(query.getResultList().isEmpty());
                } else {
                    assertTupleResult(query, operand == -36 ? 36 : 49);
                }
                Assert.assertEquals(1, _sql.size());
                if(platform.isDB2Z() || platform.isDerby() || platform.isH2()) {
                    Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (ABS(" + operand + ") = INTVAL1)", _sql.remove(0));
                } else {
                    Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (ABS(?) = INTVAL1)", _sql.remove(0));
                }
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.where(cb2.equal(cb2.abs(cb2.literal(-36)), root2.get(QuerySyntaxEntity_.intVal1)));

            query = em.createQuery(cquery2);
            assertTupleResult(query, 36);
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDerby() || platform.isH2()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (ABS(-36) = INTVAL1)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (ABS(?) = INTVAL1)", _sql.remove(0));
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
    public void testABS1_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            populate(em, _sql2, 36, 49);
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ABS(?1) = s.intVal1");
            for (int operand : new int[] { -36, -49, -81, -36 }) {
                query.setParameter(1, operand);
                if (operand == -81) {
                    Assert.assertTrue(query.getResultList().isEmpty());
                } else {
                    assertScalarResult(query, operand == -36 ? 36 : 49);
                }
                Assert.assertEquals(1, _sql2.size());
                if(platform.isDB2Z() || platform.isH2()) {
                    Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (ABS(" + operand + ") = INTVAL1)", _sql2.remove(0));
                } else {
                    Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (ABS(?) = INTVAL1)", _sql2.remove(0));
                }
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ABS(-36) = s.intVal1");
            assertScalarResult(query, 36);
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isH2()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (ABS(-36) = INTVAL1)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (ABS(?) = INTVAL1)", _sql2.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));
            cquery.where(cb.equal(cb.abs(intParam1), root.get(QuerySyntaxEntity_.intVal1)));

            query = em.createQuery(cquery);
            for (int operand : new int[] { -36, -49, -81, -36 }) {
                query.setParameter(intParam1, operand);
                if (operand == -81) {
                    Assert.assertTrue(query.getResultList().isEmpty());
                } else {
                    assertTupleResult(query, operand == -36 ? 36 : 49);
                }
                Assert.assertEquals(1, _sql2.size());
                if(platform.isDB2Z() || platform.isH2()) {
                    Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (ABS(" + operand + ") = INTVAL1)", _sql2.remove(0));
                } else {
                    Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (ABS(?) = INTVAL1)", _sql2.remove(0));
                }
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.where(cb2.equal(cb2.abs(cb2.literal(-36)), root2.get(QuerySyntaxEntity_.intVal1)));

            query = em.createQuery(cquery2);
            assertTupleResult(query, 36);
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isH2()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (ABS(-36) = INTVAL1)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (ABS(?) = INTVAL1)", _sql2.remove(0));
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
    public void testABS1_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            populate(em, _sql3, 36, 49);
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ABS(?1) = s.intVal1");
            for (int operand : new int[] { -36, -49, -81, -36 }) {
                query.setParameter(1, operand);
                if (operand == -81) {
                    Assert.assertTrue(query.getResultList().isEmpty());
                } else {
                    assertScalarResult(query, operand == -36 ? 36 : 49);
                }
                Assert.assertEquals(1, _sql3.size());
                if(platform.isDB2Z() || platform.isH2()) {
                    Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (ABS(" + operand + ") = INTVAL1)", _sql3.remove(0));
                } else {
                    Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (ABS(?) = INTVAL1)", _sql3.remove(0));
                }
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ABS(-36) = s.intVal1");
            assertScalarResult(query, 36);
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isH2()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (ABS(-36) = INTVAL1)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (ABS(?) = INTVAL1)", _sql3.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));
            cquery.where(cb.equal(cb.abs(intParam1), root.get(QuerySyntaxEntity_.intVal1)));

            query = em.createQuery(cquery);
            for (int operand : new int[] { -36, -49, -81, -36 }) {
                query.setParameter(intParam1, operand);
                if (operand == -81) {
                    Assert.assertTrue(query.getResultList().isEmpty());
                } else {
                    assertTupleResult(query, operand == -36 ? 36 : 49);
                }
                Assert.assertEquals(1, _sql3.size());
                if(platform.isDB2Z() || platform.isH2()) {
                    Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (ABS(" + operand + ") = INTVAL1)", _sql3.remove(0));
                } else {
                    Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (ABS(?) = INTVAL1)", _sql3.remove(0));
                }
            }

            // ABS test #1 with CriteriaBuilder literal values
            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.where(cb2.equal(cb2.abs(cb2.literal(-36)), root2.get(QuerySyntaxEntity_.intVal1)));

            query = em.createQuery(cquery2);
            assertTupleResult(query, 36);
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isH2()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (ABS(-36) = INTVAL1)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (ABS(?) = INTVAL1)", _sql3.remove(0));
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
    public void testSQRT1_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            populate(em, _sql, 6, 7);
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = SQRT(?1)");
            for (int operand : new int[] { 36, 49, 81, 36 }) {
                query.setParameter(1, operand);
                if (operand == 81) {
                    Assert.assertTrue(query.getResultList().isEmpty());
                } else {
                    assertScalarResult(query, operand == 36 ? 6 : 7);
                }
                Assert.assertEquals(1, _sql.size());
                if(platform.isDB2Z() || platform.isDerby() || platform.isH2()) {
                    Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = SQRT(" + operand + "))", _sql.remove(0));
                } else {
                    Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = SQRT(?))", _sql.remove(0));
                }
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = SQRT(36)");
            assertScalarResult(query, 6);
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDerby() || platform.isH2()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = SQRT(36))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = SQRT(?))", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.intVal1), cb.sqrt(intParam1)));

            query = em.createQuery(cquery);
            for (int operand : new int[] { 36, 49, 81, 36 }) {
                query.setParameter(intParam1, operand);
                if (operand == 81) {
                    Assert.assertTrue(query.getResultList().isEmpty());
                } else {
                    assertTupleResult(query, operand == 36 ? 6 : 7);
                }
                Assert.assertEquals(1, _sql.size());
                if(platform.isDB2Z() || platform.isDerby() || platform.isH2()) {
                    Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = SQRT(" + operand + "))", _sql.remove(0));
                } else {
                    Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = SQRT(?))", _sql.remove(0));
                }
            }

            // SQRT test #1 with CriteriaBuilder literal values
            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), cb2.sqrt(cb2.literal(36))));

            query = em.createQuery(cquery2);
            assertTupleResult(query, 6);
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDerby() || platform.isH2()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = SQRT(36))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = SQRT(?))", _sql.remove(0));
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
    public void testSQRT1_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            populate(em, _sql2, 6, 7);
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = SQRT(?1)");
            for (int operand : new int[] { 36, 49, 81, 36 }) {
                query.setParameter(1, operand);
                if (operand == 81) {
                    Assert.assertTrue(query.getResultList().isEmpty());
                } else {
                    assertScalarResult(query, operand == 36 ? 6 : 7);
                }
                Assert.assertEquals(1, _sql2.size());
                if(platform.isDB2Z() || platform.isH2()) {
                    Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = SQRT(" + operand + "))", _sql2.remove(0));
                } else {
                    Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = SQRT(?))", _sql2.remove(0));
                }
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = SQRT(36)");
            assertScalarResult(query, 6);
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isH2()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = SQRT(36))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = SQRT(?))", _sql2.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.intVal1), cb.sqrt(intParam1)));

            query = em.createQuery(cquery);
            for (int operand : new int[] { 36, 49, 81, 36 }) {
                query.setParameter(intParam1, operand);
                if (operand == 81) {
                    Assert.assertTrue(query.getResultList().isEmpty());
                } else {
                    assertTupleResult(query, operand == 36 ? 6 : 7);
                }
                Assert.assertEquals(1, _sql2.size());
                if(platform.isDB2Z() || platform.isH2()) {
                    Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = SQRT(" + operand + "))", _sql2.remove(0));
                } else {
                    Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = SQRT(?))", _sql2.remove(0));
                }
            }

            // SQRT test #1 with CriteriaBuilder literal values
            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), cb2.sqrt(cb2.literal(36))));

            query = em.createQuery(cquery2);
            assertTupleResult(query, 6);
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isH2()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = SQRT(36))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = SQRT(?))", _sql2.remove(0));
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
    public void testSQRT1_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            populate(em, _sql3, 6, 7);
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = SQRT(?1)");
            for (int operand : new int[] { 36, 49, 81, 36 }) {
                query.setParameter(1, operand);
                if (operand == 81) {
                    Assert.assertTrue(query.getResultList().isEmpty());
                } else {
                    assertScalarResult(query, operand == 36 ? 6 : 7);
                }
                Assert.assertEquals(1, _sql3.size());
                if(platform.isDB2Z() || platform.isH2()) {
                    Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = SQRT(" + operand + "))", _sql3.remove(0));
                } else {
                    Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = SQRT(?))", _sql3.remove(0));
                }
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = SQRT(36)");
            assertScalarResult(query, 6);
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isH2()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = SQRT(36))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = SQRT(?))", _sql3.remove(0));
            }

            // -----------------------

            // SQRT test #1 with CriteriaBuilder parameters
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.intVal1), cb.sqrt(intParam1)));

            query = em.createQuery(cquery);
            for (int operand : new int[] { 36, 49, 81, 36 }) {
                query.setParameter(intParam1, operand);
                if (operand == 81) {
                    Assert.assertTrue(query.getResultList().isEmpty());
                } else {
                    assertTupleResult(query, operand == 36 ? 6 : 7);
                }
                Assert.assertEquals(1, _sql3.size());
                if(platform.isDB2Z() || platform.isH2()) {
                    Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = SQRT(" + operand + "))", _sql3.remove(0));
                } else {
                    Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = SQRT(?))", _sql3.remove(0));
                }
            }

            // SQRT test #1 with CriteriaBuilder literal values
            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), cb2.sqrt(cb2.literal(36))));

            query = em.createQuery(cquery2);
            assertTupleResult(query, 6);
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isH2()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = SQRT(36))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = SQRT(?))", _sql3.remove(0));
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
    public void testMOD1_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            populate(em, _sql, 6, 7);
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = MOD(?1, ?2)");
            for (int operand : new int[] { 36, 37, 38, 36 }) {
                query.setParameter(1, operand);
                query.setParameter(2, 10);
                if (operand == 38) {
                    Assert.assertTrue(query.getResultList().isEmpty());
                } else {
                    assertScalarResult(query, operand == 36 ? 6 : 7);
                }
                Assert.assertEquals(1, _sql.size());
                if(platform.isDB2Z() || platform.isDerby() || platform.isH2()) {
                    Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = MOD(" + operand + ", 10))", _sql.remove(0));
                } else if (platform.isSQLServer()) {
                    Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ? % ?)", _sql.remove(0));
                } else {
                    Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = MOD(?, ?))", _sql.remove(0));
                }
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = MOD(36, 10)");
            assertScalarResult(query, 6);
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDerby() || platform.isH2()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = MOD(36, 10))", _sql.remove(0));
            } else if (platform.isSQLServer()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ? % ?)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = MOD(?, ?))", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.intVal1), cb.mod(intParam1, intParam2)));

            query = em.createQuery(cquery);
            for (int operand : new int[] { 36, 37, 38, 36 }) {
                query.setParameter(intParam1, operand);
                query.setParameter(intParam2, 10);
                if (operand == 38) {
                    Assert.assertTrue(query.getResultList().isEmpty());
                } else {
                    assertTupleResult(query, operand == 36 ? 6 : 7);
                }
                Assert.assertEquals(1, _sql.size());
                if(platform.isDB2Z() || platform.isDerby() || platform.isH2()) {
                    Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = MOD(" + operand + ", 10))", _sql.remove(0));
                } else if (platform.isSQLServer()) {
                    Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ? % ?)", _sql.remove(0));
                } else {
                    Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = MOD(?, ?))", _sql.remove(0));
                }
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), cb2.mod(36, cb2.literal(10))));

            query = em.createQuery(cquery2);
            assertTupleResult(query, 6);
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDerby() || platform.isH2()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = MOD(36, 10))", _sql.remove(0));
            } else if (platform.isSQLServer()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ? % ?)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = MOD(?, ?))", _sql.remove(0));
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
    public void testMOD1_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            populate(em, _sql2, 6, 7);
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = MOD(?1, ?2)");
            for (int operand : new int[] { 36, 37, 38, 36 }) {
                query.setParameter(1, operand);
                query.setParameter(2, 10);
                if (operand == 38) {
                    Assert.assertTrue(query.getResultList().isEmpty());
                } else {
                    assertScalarResult(query, operand == 36 ? 6 : 7);
                }
                Assert.assertEquals(1, _sql2.size());
                if(platform.isDB2Z() || platform.isH2()) {
                    Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = MOD(" + operand + ", 10))", _sql2.remove(0));
                } else if(platform.isDerby()) {
                    Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = MOD(?, 10))", _sql2.remove(0));
                } else if (platform.isSQLServer()) {
                    Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ? % ?)", _sql2.remove(0));
                } else {
                    Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = MOD(?, ?))", _sql2.remove(0));
                }
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = MOD(36, 10)");
            assertScalarResult(query, 6);
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isH2()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = MOD(36, 10))", _sql2.remove(0));
            } else if(platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = MOD(?, 10))", _sql2.remove(0));
            } else if (platform.isSQLServer()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ? % ?)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = MOD(?, ?))", _sql2.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.intVal1), cb.mod(intParam1, intParam2)));

            query = em.createQuery(cquery);
            for (int operand : new int[] { 36, 37, 38, 36 }) {
                query.setParameter(intParam1, operand);
                query.setParameter(intParam2, 10);
                if (operand == 38) {
                    Assert.assertTrue(query.getResultList().isEmpty());
                } else {
                    assertTupleResult(query, operand == 36 ? 6 : 7);
                }
                Assert.assertEquals(1, _sql2.size());
                if(platform.isDB2Z() || platform.isH2()) {
                    Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = MOD(" + operand + ", 10))", _sql2.remove(0));
                } else if(platform.isDerby()) {
                    Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = MOD(?, 10))", _sql2.remove(0));
                } else if (platform.isSQLServer()) {
                    Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ? % ?)", _sql2.remove(0));
                } else {
                    Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = MOD(?, ?))", _sql2.remove(0));
                }
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), cb2.mod(36, cb2.literal(10))));

            query = em.createQuery(cquery2);
            assertTupleResult(query, 6);
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isH2()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = MOD(36, 10))", _sql2.remove(0));
            } else if(platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = MOD(?, 10))", _sql2.remove(0));
            } else if (platform.isSQLServer()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ? % ?)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = MOD(?, ?))", _sql2.remove(0));
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
    public void testMOD1_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            populate(em, _sql3, 6, 7);
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = MOD(?1, ?2)");
            for (int operand : new int[] { 36, 37, 38, 36 }) {
                query.setParameter(1, operand);
                query.setParameter(2, 10);
                if (operand == 38) {
                    Assert.assertTrue(query.getResultList().isEmpty());
                } else {
                    assertScalarResult(query, operand == 36 ? 6 : 7);
                }
                Assert.assertEquals(1, _sql3.size());
                if(platform.isDB2Z() || platform.isH2()) {
                    Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = MOD(" + operand + ", 10))", _sql3.remove(0));
                } else if(platform.isDerby()) {
                    Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = MOD(?, 10))", _sql3.remove(0));
                } else if (platform.isSQLServer()) {
                    Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ? % ?)", _sql3.remove(0));
                } else {
                    Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = MOD(?, ?))", _sql3.remove(0));
                }
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = MOD(36, 10)");
            assertScalarResult(query, 6);
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isH2()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = MOD(36, 10))", _sql3.remove(0));
            } else if(platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = MOD(?, 10))", _sql3.remove(0));
            } else if (platform.isSQLServer()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ? % ?)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = MOD(?, ?))", _sql3.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.intVal1), cb.mod(intParam1, intParam2)));

            query = em.createQuery(cquery);
            for (int operand : new int[] { 36, 37, 38, 36 }) {
                query.setParameter(intParam1, operand);
                query.setParameter(intParam2, 10);
                if (operand == 38) {
                    Assert.assertTrue(query.getResultList().isEmpty());
                } else {
                    assertTupleResult(query, operand == 36 ? 6 : 7);
                }
                Assert.assertEquals(1, _sql3.size());
                if(platform.isDB2Z() || platform.isH2()) {
                    Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = MOD(" + operand + ", 10))", _sql3.remove(0));
                } else if(platform.isDerby()) {
                    Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = MOD(?, 10))", _sql3.remove(0));
                } else if (platform.isSQLServer()) {
                    Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ? % ?)", _sql3.remove(0));
                } else {
                    Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = MOD(?, ?))", _sql3.remove(0));
                }
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), cb2.mod(36, cb2.literal(10))));

            query = em.createQuery(cquery2);
            assertTupleResult(query, 6);
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isH2()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = MOD(36, 10))", _sql3.remove(0));
            } else if(platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = MOD(?, 10))", _sql3.remove(0));
            } else if (platform.isSQLServer()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = ? % ?)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = MOD(?, ?))", _sql3.remove(0));
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
    public void testADD1_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            populate(em, _sql, 6, 7);
            Query query = em.createQuery("SELECT s.intVal1, (?1 + ?2) FROM QuerySyntaxEntity s WHERE s.intVal1 = (?1 + ?2)");
            for (int operand : new int[] { 4, 5, 6, 4 }) {
                query.setParameter(1, operand);
                query.setParameter(2, 2);
                if (operand == 6) {
                    Assert.assertTrue(query.getResultList().isEmpty());
                } else {
                    assertTupleResult(query, operand == 4 ? 6 : 7, operand == 4 ? 6 : 7);
                }
                Assert.assertEquals(1, _sql.size());
                if(platform.isDB2Z() || platform.isDerby() || platform.isH2()) {
                    Assert.assertEquals("SELECT INTVAL1, (" + operand + " + 2) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (" + operand + " + 2))", _sql.remove(0));
                } else {
                    Assert.assertEquals("SELECT INTVAL1, (? + ?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (? + ?))", _sql.remove(0));
                }
            }

            query = em.createQuery("SELECT s.intVal1, (4 + 2) FROM QuerySyntaxEntity s WHERE s.intVal1 = (4 + 2)");
            assertTupleResult(query, 6, 6);
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDerby() || platform.isH2()) {
                Assert.assertEquals("SELECT INTVAL1, (4 + 2) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (4 + 2))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, (? + ?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (? + ?))", _sql.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1, (4 + ?1) FROM QuerySyntaxEntity s WHERE s.intVal1 = (?1 + 4)");
            query.setParameter(1, 2);
            assertTupleResult(query, 6, 6);
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDerby() || platform.isH2()) {
                Assert.assertEquals("SELECT INTVAL1, (4 + 2) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (2 + 4))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, (? + ?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (? + ?))", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1), cb.sum(intParam1, intParam2));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.intVal1), cb.sum(intParam1, intParam2)));

            query = em.createQuery(cquery);
            for (int operand : new int[] { 4, 5, 6, 4 }) {
                query.setParameter(intParam1, operand);
                query.setParameter(intParam2, 2);
                if (operand == 6) {
                    Assert.assertTrue(query.getResultList().isEmpty());
                } else {
                    assertTupleResult(query, operand == 4 ? 6 : 7, operand == 4 ? 6 : 7);
                }
                Assert.assertEquals(1, _sql.size());
                if(platform.isDB2Z() || platform.isDerby() || platform.isH2()) {
                    Assert.assertEquals("SELECT INTVAL1, (" + operand + " + 2) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (" + operand + " + 2))", _sql.remove(0));
                } else {
                    Assert.assertEquals("SELECT INTVAL1, (? + ?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (? + ?))", _sql.remove(0));
                }
            }

            // Arithmetic test #1 with CriteriaBuilder literal values
            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1), cb2.sum(cb2.literal(4), 2));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), cb2.sum(cb2.literal(4), 2)));

            query = em.createQuery(cquery2);
            assertTupleResult(query, 6, 6);
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDerby() || platform.isH2()) {
                Assert.assertEquals("SELECT INTVAL1, (4 + 2) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (4 + 2))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, (? + ?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (? + ?))", _sql.remove(0));
            }

            // Arithmetic test #1 with a mixture of literal values and CriteriaBuilder parameters
            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam3 = cb3.parameter(Integer.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1), cb3.sum(2, intParam3));
            cquery3.where(cb3.equal(root3.get(QuerySyntaxEntity_.intVal1), cb3.sum(intParam3, 2)));

            query = em.createQuery(cquery3);
            query.setParameter(intParam3, 4);
            assertTupleResult(query, 6, 6);
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDerby() || platform.isH2()) {
                Assert.assertEquals("SELECT INTVAL1, (2 + 4) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (4 + 2))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, (? + ?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (? + ?))", _sql.remove(0));
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
    public void testADD1_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            populate(em, _sql2, 6, 7);
            Query query = em.createQuery("SELECT s.intVal1, (?1 + ?2) FROM QuerySyntaxEntity s WHERE s.intVal1 = (?1 + ?2)");
            for (int operand : new int[] { 4, 5, 6, 4 }) {
                query.setParameter(1, operand);
                query.setParameter(2, 2);
                if (operand == 6) {
                    Assert.assertTrue(query.getResultList().isEmpty());
                } else {
                    assertTupleResult(query, operand == 4 ? 6 : 7, operand == 4 ? 6 : 7);
                }
                Assert.assertEquals(1, _sql2.size());
                if (platform.isH2()) {
                    Assert.assertEquals("SELECT INTVAL1, (" + operand + " + 2) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (" + operand + " + 2))", _sql2.remove(0));
                } else if(platform.isDerby()) {
                    Assert.assertEquals("SELECT INTVAL1, (? + 2) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (? + 2))", _sql2.remove(0));
                } else {
                    Assert.assertEquals("SELECT INTVAL1, (? + ?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (? + ?))", _sql2.remove(0));
                }
            }

            query = em.createQuery("SELECT s.intVal1, (4 + 2) FROM QuerySyntaxEntity s WHERE s.intVal1 = (4 + 2)");
            assertTupleResult(query, 6, 6);
            Assert.assertEquals(1, _sql2.size());
            if (platform.isH2()) {
                Assert.assertEquals("SELECT INTVAL1, (4 + 2) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (4 + 2))", _sql2.remove(0));
            } else if(platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, (? + 2) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (? + 2))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, (? + ?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (? + ?))", _sql2.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1, (4 + ?1) FROM QuerySyntaxEntity s WHERE s.intVal1 = (?1 + 4)");
            query.setParameter(1, 2);
            assertTupleResult(query, 6, 6);
            Assert.assertEquals(1, _sql2.size());
            if (platform.isH2()) {
                Assert.assertEquals("SELECT INTVAL1, (4 + 2) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (2 + 4))", _sql2.remove(0));
            } else if(platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, (? + 2) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (? + 4))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, (? + ?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (? + ?))", _sql2.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1), cb.sum(intParam1, intParam2));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.intVal1), cb.sum(intParam1, intParam2)));

            query = em.createQuery(cquery);
            for (int operand : new int[] { 4, 5, 6, 4 }) {
                query.setParameter(intParam1, operand);
                query.setParameter(intParam2, 2);
                if (operand == 6) {
                    Assert.assertTrue(query.getResultList().isEmpty());
                } else {
                    assertTupleResult(query, operand == 4 ? 6 : 7, operand == 4 ? 6 : 7);
                }
                Assert.assertEquals(1, _sql2.size());
                if (platform.isH2()) {
                    Assert.assertEquals("SELECT INTVAL1, (" + operand + " + 2) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (" + operand + " + 2))", _sql2.remove(0));
                } else if(platform.isDerby()) {
                    Assert.assertEquals("SELECT INTVAL1, (? + 2) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (? + 2))", _sql2.remove(0));
                } else {
                    Assert.assertEquals("SELECT INTVAL1, (? + ?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (? + ?))", _sql2.remove(0));
                }
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1), cb2.sum(cb2.literal(4), 2));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), cb2.sum(cb2.literal(4), 2)));

            query = em.createQuery(cquery2);
            assertTupleResult(query, 6, 6);
            Assert.assertEquals(1, _sql2.size());
            if (platform.isH2()) {
                Assert.assertEquals("SELECT INTVAL1, (4 + 2) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (4 + 2))", _sql2.remove(0));
            } else if(platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, (? + 2) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (? + 2))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, (? + ?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (? + ?))", _sql2.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam3 = cb3.parameter(Integer.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1), cb3.sum(2, intParam3));
            cquery3.where(cb3.equal(root3.get(QuerySyntaxEntity_.intVal1), cb3.sum(intParam3, 2)));

            query = em.createQuery(cquery3);
            query.setParameter(intParam3, 4);
            assertTupleResult(query, 6, 6);
            Assert.assertEquals(1, _sql2.size());
            if (platform.isH2()) {
                Assert.assertEquals("SELECT INTVAL1, (2 + 4) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (4 + 2))", _sql2.remove(0));
            } else if(platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, (? + 4) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (? + 2))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, (? + ?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (? + ?))", _sql2.remove(0));
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
    public void testADD1_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            populate(em, _sql3, 6, 7);
            Query query = em.createQuery("SELECT s.intVal1, (?1 + ?2) FROM QuerySyntaxEntity s WHERE s.intVal1 = (?1 + ?2)");
            for (int operand : new int[] { 4, 5, 6, 4 }) {
                query.setParameter(1, operand);
                query.setParameter(2, 2);
                if (operand == 6) {
                    Assert.assertTrue(query.getResultList().isEmpty());
                } else {
                    assertTupleResult(query, operand == 4 ? 6 : 7, operand == 4 ? 6 : 7);
                }
                Assert.assertEquals(1, _sql3.size());
                if (platform.isH2()) {
                    Assert.assertEquals("SELECT INTVAL1, (" + operand + " + 2) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (" + operand + " + 2))", _sql3.remove(0));
                } else if(platform.isDerby()) {
                    Assert.assertEquals("SELECT INTVAL1, (? + 2) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (? + 2))", _sql3.remove(0));
                } else {
                    Assert.assertEquals("SELECT INTVAL1, (? + ?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (? + ?))", _sql3.remove(0));
                }
            }

            query = em.createQuery("SELECT s.intVal1, (4 + 2) FROM QuerySyntaxEntity s WHERE s.intVal1 = (4 + 2)");
            assertTupleResult(query, 6, 6);
            Assert.assertEquals(1, _sql3.size());
            if (platform.isH2()) {
                Assert.assertEquals("SELECT INTVAL1, (4 + 2) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (4 + 2))", _sql3.remove(0));
            } else if(platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, (? + 2) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (? + 2))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, (? + ?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (? + ?))", _sql3.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1, (4 + ?1) FROM QuerySyntaxEntity s WHERE s.intVal1 = (?1 + 4)");
            query.setParameter(1, 2);
            assertTupleResult(query, 6, 6);
            Assert.assertEquals(1, _sql3.size());
            if (platform.isH2()) {
                Assert.assertEquals("SELECT INTVAL1, (4 + 2) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (2 + 4))", _sql3.remove(0));
            } else if(platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, (? + 2) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (? + 4))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, (? + ?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (? + ?))", _sql3.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.intVal1), cb.sum(intParam1, intParam2));
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.intVal1), cb.sum(intParam1, intParam2)));

            query = em.createQuery(cquery);
            for (int operand : new int[] { 4, 5, 6, 4 }) {
                query.setParameter(intParam1, operand);
                query.setParameter(intParam2, 2);
                if (operand == 6) {
                    Assert.assertTrue(query.getResultList().isEmpty());
                } else {
                    assertTupleResult(query, operand == 4 ? 6 : 7, operand == 4 ? 6 : 7);
                }
                Assert.assertEquals(1, _sql3.size());
                if (platform.isH2()) {
                    Assert.assertEquals("SELECT INTVAL1, (" + operand + " + 2) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (" + operand + " + 2))", _sql3.remove(0));
                } else if(platform.isDerby()) {
                    Assert.assertEquals("SELECT INTVAL1, (? + 2) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (? + 2))", _sql3.remove(0));
                } else {
                    Assert.assertEquals("SELECT INTVAL1, (? + ?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (? + ?))", _sql3.remove(0));
                }
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1), cb2.sum(cb2.literal(4), 2));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), cb2.sum(cb2.literal(4), 2)));

            query = em.createQuery(cquery2);
            assertTupleResult(query, 6, 6);
            Assert.assertEquals(1, _sql3.size());
            if (platform.isH2()) {
                Assert.assertEquals("SELECT INTVAL1, (4 + 2) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (4 + 2))", _sql3.remove(0));
            } else if(platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, (? + 2) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (? + 2))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, (? + ?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (? + ?))", _sql3.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam3 = cb3.parameter(Integer.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.intVal1), cb3.sum(2, intParam3));
            cquery3.where(cb3.equal(root3.get(QuerySyntaxEntity_.intVal1), cb3.sum(intParam3, 2)));

            query = em.createQuery(cquery3);
            query.setParameter(intParam3, 4);
            assertTupleResult(query, 6, 6);
            Assert.assertEquals(1, _sql3.size());
            if (platform.isH2()) {
                Assert.assertEquals("SELECT INTVAL1, (2 + 4) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (4 + 2))", _sql3.remove(0));
            } else if(platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, (? + 4) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (? + 2))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, (? + ?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (? + ?))", _sql3.remove(0));
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
    public void testSUB1_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            populate(em, _sql, 6);
            Query query = em.createQuery("SELECT s.strVal1, ?2 FROM QuerySyntaxEntity s WHERE s.intVal1 != (?1 - ?2)");
            for (int operand : new int[] { 2, 3, -2, 2 }) {
                query.setParameter(1, 4);
                query.setParameter(2, operand);
                if (operand == -2) {
                    Assert.assertTrue(query.getResultList().isEmpty());
                } else {
                    assertTupleResult(query, "value6", operand);
                }
                Assert.assertEquals(1, _sql.size());
                if (platform.isDB2Z() || platform.isDerby() || platform.isH2()) {
                    Assert.assertEquals("SELECT STRVAL1, " + operand + " FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (4 - " + operand + "))", _sql.remove(0));
                } else if (platform.isDB2()) {
                    Assert.assertEquals("SELECT STRVAL1, " + operand + " FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - ?))", _sql.remove(0));
                } else {
                    Assert.assertEquals("SELECT STRVAL1, ? FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - ?))", _sql.remove(0));
                }
            }

            query = em.createQuery("SELECT s.strVal1, 2 FROM QuerySyntaxEntity s WHERE s.intVal1 != (4 - 2)");
            assertTupleResult(query, "value6", 2);
            Assert.assertEquals(1, _sql.size());
            if (platform.isDB2Z() || platform.isDerby() || platform.isH2()) {
                Assert.assertEquals("SELECT STRVAL1, 2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (4 - 2))", _sql.remove(0));
            } else if (platform.isDB2()) {
                Assert.assertEquals("SELECT STRVAL1, 2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - ?))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1, ? FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - ?))", _sql.remove(0));
            }

            query = em.createQuery("SELECT s.strVal1, ?2 FROM QuerySyntaxEntity s WHERE s.intVal1 != (4 - ?2)");
            query.setParameter(2, 2);
            assertTupleResult(query, "value6", 2);
            Assert.assertEquals(1, _sql.size());
            if (platform.isDB2Z() || platform.isDerby() || platform.isH2()) {
                Assert.assertEquals("SELECT STRVAL1, 2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (4 - 2))", _sql.remove(0));
            } else if (platform.isDB2()) {
                Assert.assertEquals("SELECT STRVAL1, 2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - ?))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1, ? FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - ?))", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.strVal1), intParam2);
            cquery.where(cb.notEqual(root.get(QuerySyntaxEntity_.intVal1), cb.diff(intParam1, intParam2)));

            query = em.createQuery(cquery);
            for (int operand : new int[] { 2, 3, -2, 2 }) {
                query.setParameter(intParam1, 4);
                query.setParameter(intParam2, operand);
                if (operand == -2) {
                    Assert.assertTrue(query.getResultList().isEmpty());
                } else {
                    assertTupleResult(query, "value6", operand);
                }
                Assert.assertEquals(1, _sql.size());
                if (platform.isDB2Z() || platform.isDerby() || platform.isH2()) {
                    Assert.assertEquals("SELECT STRVAL1, " + operand + " FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (4 - " + operand + "))", _sql.remove(0));
                } else if (platform.isDB2()) {
                    Assert.assertEquals("SELECT STRVAL1, " + operand + " FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - ?))", _sql.remove(0));
                } else {
                    Assert.assertEquals("SELECT STRVAL1, ? FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - ?))", _sql.remove(0));
                }
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.strVal1), cb2.literal(2));
            cquery2.where(cb2.notEqual(root2.get(QuerySyntaxEntity_.intVal1), cb2.diff(cb2.literal(4), cb2.literal(2))));

            query = em.createQuery(cquery2);
            assertTupleResult(query, "value6", 2);
            Assert.assertEquals(1, _sql.size());
            if (platform.isDB2Z() || platform.isDerby() || platform.isH2()) {
                Assert.assertEquals("SELECT STRVAL1, 2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (4 - 2))", _sql.remove(0));
            } else if (platform.isDB2()) {
                Assert.assertEquals("SELECT STRVAL1, 2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - ?))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1, ? FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - ?))", _sql.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam6 = cb3.parameter(Integer.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.strVal1), intParam6);
            cquery3.where(cb3.notEqual(root3.get(QuerySyntaxEntity_.intVal1), cb3.diff(cb3.literal(4), intParam6)));

            query = em.createQuery(cquery3);
            query.setParameter(intParam6, 2);
            assertTupleResult(query, "value6", 2);
            Assert.assertEquals(1, _sql.size());
            if (platform.isDB2Z() || platform.isDerby() || platform.isH2()) {
                Assert.assertEquals("SELECT STRVAL1, 2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (4 - 2))", _sql.remove(0));
            } else if (platform.isDB2()) {
                Assert.assertEquals("SELECT STRVAL1, 2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - ?))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1, ? FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - ?))", _sql.remove(0));
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
    public void testSUB1_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            populate(em, _sql2, 6);
            Query query = em.createQuery("SELECT s.strVal1, ?2 FROM QuerySyntaxEntity s WHERE s.intVal1 != (?1 - ?2)");
            for (int operand : new int[] { 2, 3, -2, 2 }) {
                query.setParameter(1, 4);
                query.setParameter(2, operand);
                if (operand == -2) {
                    Assert.assertTrue(query.getResultList().isEmpty());
                } else {
                    assertTupleResult(query, "value6", operand);
                }
                Assert.assertEquals(1, _sql2.size());
                if (platform.isH2()) {
                    Assert.assertEquals("SELECT STRVAL1, " + operand + " FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (4 - " + operand + "))", _sql2.remove(0));
                } else if(platform.isDerby()) {
                    Assert.assertEquals("SELECT STRVAL1, " + operand + " FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - " + operand + "))", _sql2.remove(0));
                } else if(platform.isDB2Z() || platform.isDB2()) {
                    Assert.assertEquals("SELECT STRVAL1, " + operand + " FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - ?))", _sql2.remove(0));
                } else {
                    Assert.assertEquals("SELECT STRVAL1, ? FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - ?))", _sql2.remove(0));
                }
            }

            query = em.createQuery("SELECT s.strVal1, 2 FROM QuerySyntaxEntity s WHERE s.intVal1 != (4 - 2)");
            assertTupleResult(query, "value6", 2);
            Assert.assertEquals(1, _sql2.size());
            if (platform.isH2()) {
                Assert.assertEquals("SELECT STRVAL1, 2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (4 - 2))", _sql2.remove(0));
            } else if(platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1, 2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - 2))", _sql2.remove(0));
            } else if(platform.isDB2Z() || platform.isDB2()) {
                Assert.assertEquals("SELECT STRVAL1, 2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - ?))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1, ? FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - ?))", _sql2.remove(0));
            }

            query = em.createQuery("SELECT s.strVal1, ?2 FROM QuerySyntaxEntity s WHERE s.intVal1 != (4 - ?2)");
            query.setParameter(2, 2);
            assertTupleResult(query, "value6", 2);
            Assert.assertEquals(1, _sql2.size());
            if (platform.isH2()) {
                Assert.assertEquals("SELECT STRVAL1, 2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (4 - 2))", _sql2.remove(0));
            } else if(platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1, 2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - 2))", _sql2.remove(0));
            } else if(platform.isDB2Z() || platform.isDB2()) {
                Assert.assertEquals("SELECT STRVAL1, 2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - ?))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1, ? FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - ?))", _sql2.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.strVal1), intParam2);
            cquery.where(cb.notEqual(root.get(QuerySyntaxEntity_.intVal1), cb.diff(intParam1, intParam2)));

            query = em.createQuery(cquery);
            for (int operand : new int[] { 2, 3, -2, 2 }) {
                query.setParameter(intParam1, 4);
                query.setParameter(intParam2, operand);
                if (operand == -2) {
                    Assert.assertTrue(query.getResultList().isEmpty());
                } else {
                    assertTupleResult(query, "value6", operand);
                }
                Assert.assertEquals(1, _sql2.size());
                if (platform.isH2()) {
                    Assert.assertEquals("SELECT STRVAL1, " + operand + " FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (4 - " + operand + "))", _sql2.remove(0));
                } else if(platform.isDerby()) {
                    Assert.assertEquals("SELECT STRVAL1, " + operand + " FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - " + operand + "))", _sql2.remove(0));
                } else if(platform.isDB2Z() || platform.isDB2()) {
                    Assert.assertEquals("SELECT STRVAL1, " + operand + " FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - ?))", _sql2.remove(0));
                } else {
                    Assert.assertEquals("SELECT STRVAL1, ? FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - ?))", _sql2.remove(0));
                }
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.strVal1), cb2.literal(2));
            cquery2.where(cb2.notEqual(root2.get(QuerySyntaxEntity_.intVal1), cb2.diff(cb2.literal(4), cb2.literal(2))));

            query = em.createQuery(cquery2);
            assertTupleResult(query, "value6", 2);
            Assert.assertEquals(1, _sql2.size());
            if (platform.isH2()) {
                Assert.assertEquals("SELECT STRVAL1, 2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (4 - 2))", _sql2.remove(0));
            } else if(platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1, 2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - 2))", _sql2.remove(0));
            } else if (platform.isDB2Z() || platform.isDB2()) {
                Assert.assertEquals("SELECT STRVAL1, 2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - ?))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1, ? FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - ?))", _sql2.remove(0));
            }

            // Arithmetic test #2 with a mixture of literal values and CriteriaBuilder parameters
            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam6 = cb3.parameter(Integer.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.strVal1), intParam6);
            cquery3.where(cb3.notEqual(root3.get(QuerySyntaxEntity_.intVal1), cb3.diff(cb3.literal(4), intParam6)));

            query = em.createQuery(cquery3);
            query.setParameter(intParam6, 2);
            assertTupleResult(query, "value6", 2);
            Assert.assertEquals(1, _sql2.size());
            if (platform.isH2()) {
                Assert.assertEquals("SELECT STRVAL1, 2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (4 - 2))", _sql2.remove(0));
            } else if(platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1, 2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - 2))", _sql2.remove(0));
            } else if(platform.isDB2Z() || platform.isDB2()) {
                Assert.assertEquals("SELECT STRVAL1, 2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - ?))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1, ? FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - ?))", _sql2.remove(0));
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
    public void testSUB1_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            populate(em, _sql3, 6);
            Query query = em.createQuery("SELECT s.strVal1, ?2 FROM QuerySyntaxEntity s WHERE s.intVal1 != (?1 - ?2)");
            for (int operand : new int[] { 2, 3, -2, 2 }) {
                query.setParameter(1, 4);
                query.setParameter(2, operand);
                if (operand == -2) {
                    Assert.assertTrue(query.getResultList().isEmpty());
                } else {
                    assertTupleResult(query, "value6", operand);
                }
                Assert.assertEquals(1, _sql3.size());
                if (platform.isH2()) {
                    Assert.assertEquals("SELECT STRVAL1, " + operand + " FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (4 - " + operand + "))", _sql3.remove(0));
                } else if(platform.isDerby()) {
                    Assert.assertEquals("SELECT STRVAL1, " + operand + " FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - " + operand + "))", _sql3.remove(0));
                } else if(platform.isDB2Z() || platform.isDB2()) {
                    Assert.assertEquals("SELECT STRVAL1, " + operand + " FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - ?))", _sql3.remove(0));
                } else {
                    Assert.assertEquals("SELECT STRVAL1, ? FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - ?))", _sql3.remove(0));
                }
            }

            query = em.createQuery("SELECT s.strVal1, 2 FROM QuerySyntaxEntity s WHERE s.intVal1 != (4 - 2)");
            assertTupleResult(query, "value6", 2);
            Assert.assertEquals(1, _sql3.size());
            if (platform.isH2()) {
                Assert.assertEquals("SELECT STRVAL1, 2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (4 - 2))", _sql3.remove(0));
            } else if(platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1, 2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - 2))", _sql3.remove(0));
            } else if(platform.isDB2Z() || platform.isDB2()) {
                Assert.assertEquals("SELECT STRVAL1, 2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - ?))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1, ? FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - ?))", _sql3.remove(0));
            }

            query = em.createQuery("SELECT s.strVal1, ?2 FROM QuerySyntaxEntity s WHERE s.intVal1 != (4 - ?2)");
            query.setParameter(2, 2);
            assertTupleResult(query, "value6", 2);
            Assert.assertEquals(1, _sql3.size());
            if (platform.isH2()) {
                Assert.assertEquals("SELECT STRVAL1, 2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (4 - 2))", _sql3.remove(0));
            } else if(platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1, 2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - 2))", _sql3.remove(0));
            } else if(platform.isDB2Z() || platform.isDB2()) {
                Assert.assertEquals("SELECT STRVAL1, 2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - ?))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1, ? FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - ?))", _sql3.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            cquery.multiselect(root.get(QuerySyntaxEntity_.strVal1), intParam2);
            cquery.where(cb.notEqual(root.get(QuerySyntaxEntity_.intVal1), cb.diff(intParam1, intParam2)));

            query = em.createQuery(cquery);
            for (int operand : new int[] { 2, 3, -2, 2 }) {
                query.setParameter(intParam1, 4);
                query.setParameter(intParam2, operand);
                if (operand == -2) {
                    Assert.assertTrue(query.getResultList().isEmpty());
                } else {
                    assertTupleResult(query, "value6", operand);
                }
                Assert.assertEquals(1, _sql3.size());
                if (platform.isH2()) {
                    Assert.assertEquals("SELECT STRVAL1, " + operand + " FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (4 - " + operand + "))", _sql3.remove(0));
                } else if(platform.isDerby()) {
                    Assert.assertEquals("SELECT STRVAL1, " + operand + " FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - " + operand + "))", _sql3.remove(0));
                } else if(platform.isDB2Z() || platform.isDB2()) {
                    Assert.assertEquals("SELECT STRVAL1, " + operand + " FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - ?))", _sql3.remove(0));
                } else {
                    Assert.assertEquals("SELECT STRVAL1, ? FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - ?))", _sql3.remove(0));
                }
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.strVal1), cb2.literal(2));
            cquery2.where(cb2.notEqual(root2.get(QuerySyntaxEntity_.intVal1), cb2.diff(cb2.literal(4), cb2.literal(2))));

            query = em.createQuery(cquery2);
            assertTupleResult(query, "value6", 2);
            Assert.assertEquals(1, _sql3.size());
            if (platform.isH2()) {
                Assert.assertEquals("SELECT STRVAL1, 2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (4 - 2))", _sql3.remove(0));
            } else if(platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1, 2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - 2))", _sql3.remove(0));
            } else if (platform.isDB2Z() || platform.isDB2()) {
                Assert.assertEquals("SELECT STRVAL1, 2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - ?))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1, ? FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - ?))", _sql3.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery3 = cb3.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);
            ParameterExpression<Integer> intParam6 = cb3.parameter(Integer.class);
            cquery3.multiselect(root3.get(QuerySyntaxEntity_.strVal1), intParam6);
            cquery3.where(cb3.notEqual(root3.get(QuerySyntaxEntity_.intVal1), cb3.diff(cb3.literal(4), intParam6)));

            query = em.createQuery(cquery3);
            query.setParameter(intParam6, 2);
            assertTupleResult(query, "value6", 2);
            Assert.assertEquals(1, _sql3.size());
            if (platform.isH2()) {
                Assert.assertEquals("SELECT STRVAL1, 2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (4 - 2))", _sql3.remove(0));
            } else if(platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1, 2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - 2))", _sql3.remove(0));
            } else if(platform.isDB2Z() || platform.isDB2()) {
                Assert.assertEquals("SELECT STRVAL1, 2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - ?))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1, ? FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - ?))", _sql3.remove(0));
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
    public void testCriteriaArrayVarargsResults() {
        assertCriteriaArrayResults("varargs");
    }

    @Test
    public void testCriteriaArrayListResults() {
        assertCriteriaArrayResults("list");
    }

    @Test
    public void testCriteriaArraySelectionResults() {
        assertCriteriaArrayResults("array");
    }

    private void assertCriteriaArrayResults(String selectionKind) {
        if (emf == null) {
            return;
        }
        EntityManager em = emf.createEntityManager();
        try {
            populate(em, _sql, 6, 7);
            CriteriaBuilder cb = em.getCriteriaBuilder();
            for (int width : new int[] { 1, 2 }) {
                CriteriaQuery<Object[]> criteria = cb.createQuery(Object[].class);
                Root<QuerySyntaxEntity> root = criteria.from(QuerySyntaxEntity.class);
                Selection<?>[] selections = width == 1
                        ? new Selection<?>[] { root.get(QuerySyntaxEntity_.intVal2) }
                        : new Selection<?>[] { root.get(QuerySyntaxEntity_.intVal1), root.get(QuerySyntaxEntity_.intVal2) };
                switch (selectionKind) {
                    case "varargs":
                        criteria.multiselect(selections);
                        break;
                    case "list":
                        criteria.multiselect(List.of(selections));
                        break;
                    case "array":
                        criteria.select(cb.array(selections));
                        break;
                    default:
                        throw new AssertionError(selectionKind);
                }
                criteria.where(cb.ge(root.get(QuerySyntaxEntity_.id), cb.parameter(Long.class, "minimumId")));
                criteria.orderBy(cb.asc(root.get(QuerySyntaxEntity_.id)));
                TypedQuery<Object[]> query = em.createQuery(criteria);
                for (int minimumId : new int[] { 6, 7, 8, 6 }) {
                    query.setParameter("minimumId", (long) minimumId);
                    List<?> rows = query.getResultList();
                    Assert.assertEquals(Math.max(0, 8 - minimumId), rows.size());
                    for (int i = 0; i < rows.size(); i++) {
                        Assert.assertTrue("Expected an Object[] row", rows.get(i) instanceof Object[]);
                        Object[] row = (Object[]) rows.get(i);
                        Assert.assertEquals(width, row.length);
                        if (width == 2) {
                            Assert.assertEquals(Integer.valueOf(minimumId + i), row[0]);
                        }
                        Assert.assertNull(row[width - 1]);
                    }
                    assertSQL("SELECT " + (width == 1 ? "INTVAL2" : "INTVAL1, INTVAL2")
                            + " FROM QUERYSYNTAXENTITY WHERE (ID >= ?) ORDER BY ID ASC");
                }
            }
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    @Test
    public void testOtherCriteriaResultShapes() {
        if (emf == null) {
            return;
        }
        EntityManager em = emf.createEntityManager();
        try {
            populate(em, _sql, 6, 7);
            CriteriaBuilder cb = em.getCriteriaBuilder();
            // Object and untyped multiselect queries unwrap one selection but retain multiple selections.
            for (boolean explicitObjectType : new boolean[] { false, true }) {
                CriteriaQuery<Object> scalar = explicitObjectType ? cb.createQuery(Object.class) : cb.createQuery();
                Root<QuerySyntaxEntity> scalarRoot = scalar.from(QuerySyntaxEntity.class);
                scalar.multiselect(scalarRoot.get(QuerySyntaxEntity_.intVal1));
                scalar.orderBy(cb.asc(scalarRoot.get(QuerySyntaxEntity_.id)));
                Assert.assertEquals(List.of(6, 7), em.createQuery(scalar).getResultList());
                assertSQL("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY ID ASC");

                CriteriaQuery<Object> multiple = explicitObjectType ? cb.createQuery(Object.class) : cb.createQuery();
                Root<QuerySyntaxEntity> multipleRoot = multiple.from(QuerySyntaxEntity.class);
                multiple.multiselect(multipleRoot.get(QuerySyntaxEntity_.intVal1), multipleRoot.get(QuerySyntaxEntity_.intVal2));
                multiple.orderBy(cb.asc(multipleRoot.get(QuerySyntaxEntity_.id)));
                List<Object> rows = em.createQuery(multiple).getResultList();
                Assert.assertEquals(2, rows.size());
                for (int i = 0; i < rows.size(); i++) {
                    Assert.assertTrue(rows.get(i) instanceof Object[]);
                    Assert.assertArrayEquals(new Object[] { i + 6, null }, (Object[]) rows.get(i));
                }
                assertSQL("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY ORDER BY ID ASC");
            }

            CriteriaQuery<Integer> scalar = cb.createQuery(Integer.class);
            Root<QuerySyntaxEntity> scalarRoot = scalar.from(QuerySyntaxEntity.class);
            scalar.select(scalarRoot.get(QuerySyntaxEntity_.intVal1));
            scalar.orderBy(cb.asc(scalarRoot.get(QuerySyntaxEntity_.id)));
            Assert.assertEquals(List.of(6, 7), em.createQuery(scalar).getResultList());
            assertSQL("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY ID ASC");

            CriteriaQuery<Tuple> tuple = cb.createTupleQuery();
            Root<QuerySyntaxEntity> tupleRoot = tuple.from(QuerySyntaxEntity.class);
            tuple.multiselect(tupleRoot.get(QuerySyntaxEntity_.intVal1).alias("value"),
                    tupleRoot.get(QuerySyntaxEntity_.intVal2).alias("missing"));
            tuple.orderBy(cb.asc(tupleRoot.get(QuerySyntaxEntity_.id)));
            List<Tuple> tuples = em.createQuery(tuple).getResultList();
            Assert.assertEquals(2, tuples.size());
            for (int i = 0; i < tuples.size(); i++) {
                Assert.assertEquals(2, tuples.get(i).getElements().size());
                Assert.assertEquals(Integer.valueOf(i + 6), tuples.get(i).get("value", Integer.class));
                Assert.assertNull(tuples.get(i).get("missing"));
            }
            assertSQL("SELECT INTVAL1, INTVAL2 FROM QUERYSYNTAXENTITY ORDER BY ID ASC");

            CriteriaQuery<CriteriaValue> constructor = cb.createQuery(CriteriaValue.class);
            Root<QuerySyntaxEntity> constructorRoot = constructor.from(QuerySyntaxEntity.class);
            constructor.multiselect(constructorRoot.get(QuerySyntaxEntity_.intVal1));
            constructor.orderBy(cb.asc(constructorRoot.get(QuerySyntaxEntity_.id)));
            List<CriteriaValue> values = em.createQuery(constructor).getResultList();
            Assert.assertEquals(2, values.size());
            Assert.assertEquals(Integer.valueOf(6), values.get(0).value);
            Assert.assertEquals(Integer.valueOf(7), values.get(1).value);
            assertSQL("SELECT INTVAL1 FROM QUERYSYNTAXENTITY ORDER BY ID ASC");
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    public static final class CriteriaValue {
        private final Integer value;

        public CriteriaValue(Integer value) {
            this.value = value;
        }
    }

    private void assertSQL(String expected) {
        Assert.assertEquals(1, _sql.size());
        Assert.assertEquals(expected, _sql.remove(0));
    }

    private void populate(EntityManager em, List<String> sql, int... values) {
        // Each runner owns fresh tables; roll back these rows in the test's finally block.
        em.getTransaction().begin();
        for (int value : values) {
            QuerySyntaxEntity entity = new QuerySyntaxEntity((long) value, "value" + value);
            entity.setIntVal1(value);
            em.persist(entity);
        }
        em.flush();
        sql.clear();
    }

    private void assertScalarResult(Query query, int expected) {
        Assert.assertEquals(List.of(expected), query.getResultList());
    }

    private void assertTupleResult(Query query, Object... expected) {
        List<?> results = query.getResultList();
        Assert.assertEquals(1, results.size());
        Assert.assertTrue("Expected an Object[] result", results.get(0) instanceof Object[]);
        Object[] result = (Object[]) results.get(0);
        Assert.assertEquals(expected.length, result.length);
        for (int i = 0; i < expected.length; i++) {
            if (expected[i] instanceof Number) {
                Assert.assertTrue("Expected a numeric result at index " + i, result[i] instanceof Number);
                Assert.assertEquals(((Number) expected[i]).doubleValue(), ((Number) result[i]).doubleValue(), 0);
            } else {
                Assert.assertEquals(expected[i], result[i]);
            }
        }
    }

    protected DatabasePlatform getPlatform(EntityManagerFactory emf) {
        return ((EntityManagerFactoryImpl)emf).getServerSession().getPlatform();
    }
}
