/*******************************************************************************
 * Copyright (c) 2022 IBM Corporation. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     05/04/2022 - Will Dazey
 *       - Add support for partial parameter binding for DB2
 ******************************************************************************/
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
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ABS(?1) = s.intVal1");
            query.setParameter(1, -36);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (ABS(-36) = INTVAL1)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (ABS(?) = INTVAL1)", _sql.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ABS(-36) = s.intVal1");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
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
            query.setParameter(intParam1, -36);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (ABS(-36) = INTVAL1)", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (ABS(?) = INTVAL1)", _sql.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.where(cb2.equal(cb2.abs(cb2.literal(-36)), root2.get(QuerySyntaxEntity_.intVal1)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
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
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ABS(?1) = s.intVal1");
            query.setParameter(1, -36);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (ABS(-36) = INTVAL1)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (ABS(?) = INTVAL1)", _sql2.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ABS(-36) = s.intVal1");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
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
            query.setParameter(intParam1, -36);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (ABS(-36) = INTVAL1)", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (ABS(?) = INTVAL1)", _sql2.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.where(cb2.equal(cb2.abs(cb2.literal(-36)), root2.get(QuerySyntaxEntity_.intVal1)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
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
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ABS(?1) = s.intVal1");
            query.setParameter(1, -36);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (ABS(-36) = INTVAL1)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (ABS(?) = INTVAL1)", _sql3.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE ABS(-36) = s.intVal1");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
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
            query.setParameter(intParam1, -36);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (ABS(-36) = INTVAL1)", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (ABS(?) = INTVAL1)", _sql3.remove(0));
            }

            // ABS test #1 with CriteriaBuilder literal values
            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.where(cb2.equal(cb2.abs(cb2.literal(-36)), root2.get(QuerySyntaxEntity_.intVal1)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
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
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = SQRT(?1)");
            query.setParameter(1, 36);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = SQRT(36))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = SQRT(?))", _sql.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = SQRT(36)");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
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
            query.setParameter(intParam1, 36);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = SQRT(36))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = SQRT(?))", _sql.remove(0));
            }

            // SQRT test #1 with CriteriaBuilder literal values
            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), cb2.sqrt(cb2.literal(36))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
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
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = SQRT(?1)");
            query.setParameter(1, 36);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = SQRT(36))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = SQRT(?))", _sql2.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = SQRT(36)");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
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
            query.setParameter(intParam1, 36);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = SQRT(36))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = SQRT(?))", _sql2.remove(0));
            }

            // SQRT test #1 with CriteriaBuilder literal values
            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), cb2.sqrt(cb2.literal(36))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
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
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = SQRT(?1)");
            query.setParameter(1, 36);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = SQRT(36))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = SQRT(?))", _sql3.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = SQRT(36)");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
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
            query.setParameter(intParam1, 36);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = SQRT(36))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = SQRT(?))", _sql3.remove(0));
            }

            // SQRT test #1 with CriteriaBuilder literal values
            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), cb2.sqrt(cb2.literal(36))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
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
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = MOD(?1, ?2)");
            query.setParameter(1, 36);
            query.setParameter(2, 10);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = MOD(36, 10))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = MOD(?, ?))", _sql.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = MOD(36, 10)");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = MOD(36, 10))", _sql.remove(0));
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
            query.setParameter(intParam1, 36);
            query.setParameter(intParam2, 10);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = MOD(36, 10))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = MOD(?, ?))", _sql.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), cb2.mod(36, cb2.literal(10))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = MOD(36, 10))", _sql.remove(0));
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
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = MOD(?1, ?2)");
            query.setParameter(1, 36);
            query.setParameter(2, 10);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = MOD(36, 10))", _sql2.remove(0));
            } else if(platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = MOD(?, 10))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = MOD(?, ?))", _sql2.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = MOD(36, 10)");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = MOD(36, 10))", _sql2.remove(0));
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
            query.setParameter(intParam1, 36);
            query.setParameter(intParam2, 10);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = MOD(36, 10))", _sql2.remove(0));
            } else if(platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = MOD(?, 10))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = MOD(?, ?))", _sql2.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), cb2.mod(36, cb2.literal(10))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = MOD(36, 10))", _sql2.remove(0));
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
            Query query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = MOD(?1, ?2)");
            query.setParameter(1, 36);
            query.setParameter(2, 10);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = MOD(36, 10))", _sql3.remove(0));
            } else if(platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = MOD(?, 10))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = MOD(?, ?))", _sql3.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1 FROM QuerySyntaxEntity s WHERE s.intVal1 = MOD(36, 10)");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = MOD(36, 10))", _sql3.remove(0));
            } else if(platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = MOD(?, 10))", _sql3.remove(0));
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
            query.setParameter(intParam1, 36);
            query.setParameter(intParam2, 10);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = MOD(36, 10))", _sql3.remove(0));
            } else if(platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = MOD(?, 10))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = MOD(?, ?))", _sql3.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), cb2.mod(36, cb2.literal(10))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = MOD(36, 10))", _sql3.remove(0));
            } else if(platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = MOD(?, 10))", _sql3.remove(0));
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
            Query query = em.createQuery("SELECT s.intVal1, (?1 + ?2) FROM QuerySyntaxEntity s WHERE s.intVal1 = (?1 + ?2)");
            query.setParameter(1, 4);
            query.setParameter(2, 2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, (4 + 2) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (4 + 2))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, (? + ?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (? + ?))", _sql.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1, (4 + 2) FROM QuerySyntaxEntity s WHERE s.intVal1 = (4 + 2)");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, (4 + 2) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (4 + 2))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, (? + ?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (? + ?))", _sql.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1, (4 + ?1) FROM QuerySyntaxEntity s WHERE s.intVal1 = (?1 + 4)");
            query.setParameter(1, 2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
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
            query.setParameter(intParam1, 4);
            query.setParameter(intParam2, 2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, (4 + 2) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (4 + 2))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, (? + ?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (? + ?))", _sql.remove(0));
            }

            // Arithmetic test #1 with CriteriaBuilder literal values
            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1), cb2.sum(cb2.literal(4), 2));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), cb2.sum(cb2.literal(4), 2)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
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
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
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
            Query query = em.createQuery("SELECT s.intVal1, (?1 + ?2) FROM QuerySyntaxEntity s WHERE s.intVal1 = (?1 + ?2)");
            query.setParameter(1, 4);
            query.setParameter(2, 2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, (? + 2) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (? + 2))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, (? + ?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (? + ?))", _sql2.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1, (4 + 2) FROM QuerySyntaxEntity s WHERE s.intVal1 = (4 + 2)");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, (4 + 2) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (4 + 2))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, (? + ?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (? + ?))", _sql2.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1, (4 + ?1) FROM QuerySyntaxEntity s WHERE s.intVal1 = (?1 + 4)");
            query.setParameter(1, 2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, (4 + ?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (? + 4))", _sql2.remove(0));
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
            query.setParameter(intParam1, 4);
            query.setParameter(intParam2, 2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, (? + 2) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (? + 2))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, (? + ?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (? + ?))", _sql2.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1), cb2.sum(cb2.literal(4), 2));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), cb2.sum(cb2.literal(4), 2)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, (4 + 2) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (4 + 2))", _sql2.remove(0));
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
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, (2 + ?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (? + 2))", _sql2.remove(0));
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
            Query query = em.createQuery("SELECT s.intVal1, (?1 + ?2) FROM QuerySyntaxEntity s WHERE s.intVal1 = (?1 + ?2)");
            query.setParameter(1, 4);
            query.setParameter(2, 2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, (? + 2) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (? + 2))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, (? + ?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (? + ?))", _sql3.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1, (4 + 2) FROM QuerySyntaxEntity s WHERE s.intVal1 = (4 + 2)");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, (? + 2) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (? + 2))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, (? + ?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (? + ?))", _sql3.remove(0));
            }

            query = em.createQuery("SELECT s.intVal1, (4 + ?1) FROM QuerySyntaxEntity s WHERE s.intVal1 = (?1 + 4)");
            query.setParameter(1, 2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDerby()) {
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
            query.setParameter(intParam1, 4);
            query.setParameter(intParam2, 2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDerby()) {
                Assert.assertEquals("SELECT INTVAL1, (? + 2) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (? + 2))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT INTVAL1, (? + ?) FROM QUERYSYNTAXENTITY WHERE (INTVAL1 = (? + ?))", _sql3.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.intVal1), cb2.sum(cb2.literal(4), 2));
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.intVal1), cb2.sum(cb2.literal(4), 2)));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDerby()) {
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
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDerby()) {
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
            Query query = em.createQuery("SELECT s.strVal1, ?2 FROM QuerySyntaxEntity s WHERE s.intVal1 != (?1 - ?2)");
            query.setParameter(1, 4);
            query.setParameter(2, 2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1, 2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (4 - 2))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1, ? FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - ?))", _sql.remove(0));
            }

            query = em.createQuery("SELECT s.strVal1, 2 FROM QuerySyntaxEntity s WHERE s.intVal1 != (4 - 2)");
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1, 2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (4 - 2))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1, ? FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - ?))", _sql.remove(0));
            }

            query = em.createQuery("SELECT s.strVal1, ?2 FROM QuerySyntaxEntity s WHERE s.intVal1 != (4 - ?2)");
            query.setParameter(2, 2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1, 2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (4 - 2))", _sql.remove(0));
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
            query.setParameter(intParam1, 4);
            query.setParameter(intParam2, 2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1, 2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (4 - 2))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1, ? FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - ?))", _sql.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.strVal1), cb2.literal(2));
            cquery2.where(cb2.notEqual(root2.get(QuerySyntaxEntity_.intVal1), cb2.diff(cb2.literal(4), cb2.literal(2))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1, 2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (4 - 2))", _sql.remove(0));
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
            query.getResultList();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1, 2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (4 - 2))", _sql.remove(0));
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
            Query query = em.createQuery("SELECT s.strVal1, ?2 FROM QuerySyntaxEntity s WHERE s.intVal1 != (?1 - ?2)");
            query.setParameter(1, 4);
            query.setParameter(2, 2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1, 2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - 2))", _sql2.remove(0));
            } else if(platform.isDB2Z() || platform.isDB2()) {
                Assert.assertEquals("SELECT STRVAL1, 2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - ?))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1, ? FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - ?))", _sql2.remove(0));
            }

            query = em.createQuery("SELECT s.strVal1, 2 FROM QuerySyntaxEntity s WHERE s.intVal1 != (4 - 2)");
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1, 2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (4 - 2))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1, ? FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - ?))", _sql2.remove(0));
            }

            query = em.createQuery("SELECT s.strVal1, ?2 FROM QuerySyntaxEntity s WHERE s.intVal1 != (4 - ?2)");
            query.setParameter(2, 2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1, 2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (4 - ?))", _sql2.remove(0));
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
            query.setParameter(intParam1, 4);
            query.setParameter(intParam2, 2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1, 2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - 2))", _sql2.remove(0));
            } else if(platform.isDB2Z() || platform.isDB2()) {
                Assert.assertEquals("SELECT STRVAL1, 2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - ?))", _sql2.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1, ? FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - ?))", _sql2.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.strVal1), cb2.literal(2));
            cquery2.where(cb2.notEqual(root2.get(QuerySyntaxEntity_.intVal1), cb2.diff(cb2.literal(4), cb2.literal(2))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1, 2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (4 - 2))", _sql2.remove(0));
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
            query.getResultList();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1, 2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (4 - ?))", _sql2.remove(0));
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
            Query query = em.createQuery("SELECT s.strVal1, ?2 FROM QuerySyntaxEntity s WHERE s.intVal1 != (?1 - ?2)");
            query.setParameter(1, 4);
            query.setParameter(2, 2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1, 2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - 2))", _sql3.remove(0));
            } else if(platform.isDB2Z() || platform.isDB2()) {
                Assert.assertEquals("SELECT STRVAL1, 2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - ?))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1, ? FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - ?))", _sql3.remove(0));
            }

            query = em.createQuery("SELECT s.strVal1, 2 FROM QuerySyntaxEntity s WHERE s.intVal1 != (4 - 2)");
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1, 2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - 2))", _sql3.remove(0));
            } else if(platform.isDB2Z() || platform.isDB2()) {
                Assert.assertEquals("SELECT STRVAL1, 2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - ?))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1, ? FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - ?))", _sql3.remove(0));
            }

            query = em.createQuery("SELECT s.strVal1, ?2 FROM QuerySyntaxEntity s WHERE s.intVal1 != (4 - ?2)");
            query.setParameter(2, 2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDerby()) {
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
            query.setParameter(intParam1, 4);
            query.setParameter(intParam2, 2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDerby()) {
                Assert.assertEquals("SELECT STRVAL1, 2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - 2))", _sql3.remove(0));
            } else if(platform.isDB2Z() || platform.isDB2()) {
                Assert.assertEquals("SELECT STRVAL1, 2 FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - ?))", _sql3.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1, ? FROM QUERYSYNTAXENTITY WHERE (INTVAL1 <> (? - ?))", _sql3.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery2 = cb2.createQuery(Object[].class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.multiselect(root2.get(QuerySyntaxEntity_.strVal1), cb2.literal(2));
            cquery2.where(cb2.notEqual(root2.get(QuerySyntaxEntity_.intVal1), cb2.diff(cb2.literal(4), cb2.literal(2))));

            query = em.createQuery(cquery2);
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDerby()) {
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
            query.getResultList();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDerby()) {
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

    protected DatabasePlatform getPlatform(EntityManagerFactory emf) {
        return ((EntityManagerFactoryImpl)emf).getServerSession().getPlatform();
    }
}
