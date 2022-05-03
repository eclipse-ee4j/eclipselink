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
import javax.persistence.criteria.CriteriaUpdate;
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
public class TestQuerySyntaxUpdateTests {

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
    public void testUpdate1_Default() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);

        try {
            Query query = em.createQuery("UPDATE QuerySyntaxEntity s SET s.intVal1 = ?1 WHERE s.strVal2 = LOWER(?2)");
            query.setParameter(1, 9);
            query.setParameter(2, "HELLO");
            em.getTransaction().begin();
            query.executeUpdate();
            em.getTransaction().commit();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("UPDATE QUERYSYNTAXENTITY SET INTVAL1 = 9 WHERE (STRVAL2 = LCASE('HELLO'))", _sql.remove(0));
            } else {
                Assert.assertEquals("UPDATE QUERYSYNTAXENTITY SET INTVAL1 = ? WHERE (STRVAL2 = LOWER(?))", _sql.remove(0));
            }

            query = em.createQuery("UPDATE QuerySyntaxEntity s SET s.intVal1 = 9 WHERE s.strVal2 = LOWER('HELLO')");
            em.getTransaction().begin();
            query.executeUpdate();
            em.getTransaction().commit();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("UPDATE QUERYSYNTAXENTITY SET INTVAL1 = 9 WHERE (STRVAL2 = LCASE('HELLO'))", _sql.remove(0));
            } else {
                Assert.assertEquals("UPDATE QUERYSYNTAXENTITY SET INTVAL1 = ? WHERE (STRVAL2 = LOWER(?))", _sql.remove(0));
            }

            query = em.createQuery("UPDATE QuerySyntaxEntity s SET s.intVal1 = ?1 WHERE s.strVal2 = LOWER('HELLO')");
            query.setParameter(1, 9);
            em.getTransaction().begin();
            query.executeUpdate();
            em.getTransaction().commit();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("UPDATE QUERYSYNTAXENTITY SET INTVAL1 = 9 WHERE (STRVAL2 = LCASE('HELLO'))", _sql.remove(0));
            } else {
                Assert.assertEquals("UPDATE QUERYSYNTAXENTITY SET INTVAL1 = ? WHERE (STRVAL2 = LOWER(?))", _sql.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaUpdate<QuerySyntaxEntity> cquery = cb.createCriteriaUpdate(QuerySyntaxEntity.class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);

            ParameterExpression<Integer> intValue = cb.parameter(Integer.class);
            ParameterExpression<String> strValue = cb.parameter(String.class);
            cquery.set(root.get(QuerySyntaxEntity_.intVal1), intValue);
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.strVal2), cb.lower(strValue)));

            query = em.createQuery(cquery);
            query.setParameter(intValue, 9);
            query.setParameter(strValue, "HELLO");
            em.getTransaction().begin();
            query.executeUpdate();
            em.getTransaction().commit();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("UPDATE QUERYSYNTAXENTITY SET INTVAL1 = 9 WHERE (STRVAL2 = LCASE('HELLO'))", _sql.remove(0));
            } else {
                Assert.assertEquals("UPDATE QUERYSYNTAXENTITY SET INTVAL1 = ? WHERE (STRVAL2 = LOWER(?))", _sql.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaUpdate<QuerySyntaxEntity> cquery2 = cb2.createCriteriaUpdate(QuerySyntaxEntity.class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.set(root2.get(QuerySyntaxEntity_.intVal1), 9);
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.strVal2), cb2.lower(cb2.literal("HELLO"))));

            query = em.createQuery(cquery2);
            em.getTransaction().begin();
            query.executeUpdate();
            em.getTransaction().commit();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("UPDATE QUERYSYNTAXENTITY SET INTVAL1 = 9 WHERE (STRVAL2 = LCASE('HELLO'))", _sql.remove(0));
            } else {
                Assert.assertEquals("UPDATE QUERYSYNTAXENTITY SET INTVAL1 = ? WHERE (STRVAL2 = LOWER(?))", _sql.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaUpdate<QuerySyntaxEntity> cquery3 = cb3.createCriteriaUpdate(QuerySyntaxEntity.class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);

            ParameterExpression<String> strValue1 = cb3.parameter(String.class);
            cquery3.set(root3.get(QuerySyntaxEntity_.intVal1), 9);
            cquery3.where(cb3.equal(root3.get(QuerySyntaxEntity_.strVal2), cb2.lower(strValue1)));

            query = em.createQuery(cquery3);
            query.setParameter(strValue1, "HELLO");
            em.getTransaction().begin();
            query.executeUpdate();
            em.getTransaction().commit();
            Assert.assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("UPDATE QUERYSYNTAXENTITY SET INTVAL1 = 9 WHERE (STRVAL2 = LCASE('HELLO'))", _sql.remove(0));
            } else {
                Assert.assertEquals("UPDATE QUERYSYNTAXENTITY SET INTVAL1 = ? WHERE (STRVAL2 = LOWER(?))", _sql.remove(0));
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
    public void testUpdate1_PartialBind() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        try {
            Query query = em.createQuery("UPDATE QuerySyntaxEntity s SET s.intVal1 = ?1 WHERE s.strVal2 = LOWER(?2)");
            query.setParameter(1, 9);
            query.setParameter(2, "HELLO");
            em.getTransaction().begin();
            query.executeUpdate();
            em.getTransaction().commit();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("UPDATE QUERYSYNTAXENTITY SET INTVAL1 = ? WHERE (STRVAL2 = LCASE(?))", _sql2.remove(0));
            } else {
                Assert.assertEquals("UPDATE QUERYSYNTAXENTITY SET INTVAL1 = ? WHERE (STRVAL2 = LOWER(?))", _sql2.remove(0));
            }

            query = em.createQuery("UPDATE QuerySyntaxEntity s SET s.intVal1 = 9 WHERE s.strVal2 = LOWER('HELLO')");
            em.getTransaction().begin();
            query.executeUpdate();
            em.getTransaction().commit();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("UPDATE QUERYSYNTAXENTITY SET INTVAL1 = 9 WHERE (STRVAL2 = LCASE('HELLO'))", _sql2.remove(0));
            } else {
                Assert.assertEquals("UPDATE QUERYSYNTAXENTITY SET INTVAL1 = ? WHERE (STRVAL2 = LOWER(?))", _sql2.remove(0));
            }

            query = em.createQuery("UPDATE QuerySyntaxEntity s SET s.intVal1 = ?1 WHERE s.strVal2 = LOWER('HELLO')");
            query.setParameter(1, 9);
            em.getTransaction().begin();
            query.executeUpdate();
            em.getTransaction().commit();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("UPDATE QUERYSYNTAXENTITY SET INTVAL1 = ? WHERE (STRVAL2 = LCASE('HELLO'))", _sql2.remove(0));
            } else {
                Assert.assertEquals("UPDATE QUERYSYNTAXENTITY SET INTVAL1 = ? WHERE (STRVAL2 = LOWER(?))", _sql2.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaUpdate<QuerySyntaxEntity> cquery = cb.createCriteriaUpdate(QuerySyntaxEntity.class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);

            ParameterExpression<Integer> intValue = cb.parameter(Integer.class);
            ParameterExpression<String> strValue = cb.parameter(String.class);
            cquery.set(root.get(QuerySyntaxEntity_.intVal1), intValue);
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.strVal2), cb.lower(strValue)));

            query = em.createQuery(cquery);
            query.setParameter(intValue, 9);
            query.setParameter(strValue, "HELLO");
            em.getTransaction().begin();
            query.executeUpdate();
            em.getTransaction().commit();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("UPDATE QUERYSYNTAXENTITY SET INTVAL1 = ? WHERE (STRVAL2 = LCASE(?))", _sql2.remove(0));
            } else {
                Assert.assertEquals("UPDATE QUERYSYNTAXENTITY SET INTVAL1 = ? WHERE (STRVAL2 = LOWER(?))", _sql2.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaUpdate<QuerySyntaxEntity> cquery2 = cb2.createCriteriaUpdate(QuerySyntaxEntity.class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.set(root2.get(QuerySyntaxEntity_.intVal1), 9);
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.strVal2), cb2.lower(cb2.literal("HELLO"))));

            query = em.createQuery(cquery2);
            em.getTransaction().begin();
            query.executeUpdate();
            em.getTransaction().commit();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("UPDATE QUERYSYNTAXENTITY SET INTVAL1 = 9 WHERE (STRVAL2 = LCASE('HELLO'))", _sql2.remove(0));
            } else {
                Assert.assertEquals("UPDATE QUERYSYNTAXENTITY SET INTVAL1 = ? WHERE (STRVAL2 = LOWER(?))", _sql2.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaUpdate<QuerySyntaxEntity> cquery3 = cb3.createCriteriaUpdate(QuerySyntaxEntity.class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);

            ParameterExpression<String> strValue1 = cb3.parameter(String.class);
            cquery3.set(root3.get(QuerySyntaxEntity_.intVal1), 9);
            cquery3.where(cb3.equal(root3.get(QuerySyntaxEntity_.strVal2), cb2.lower(strValue1)));

            query = em.createQuery(cquery3);
            query.setParameter(strValue1, "HELLO");
            em.getTransaction().begin();
            query.executeUpdate();
            em.getTransaction().commit();
            Assert.assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("UPDATE QUERYSYNTAXENTITY SET INTVAL1 = 9 WHERE (STRVAL2 = LCASE(?))", _sql2.remove(0));
            } else {
                Assert.assertEquals("UPDATE QUERYSYNTAXENTITY SET INTVAL1 = ? WHERE (STRVAL2 = LOWER(?))", _sql2.remove(0));
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
    public void testUpdate1_PartialBind_BindLiteral() {
        if (emf3 == null)
            return;

        EntityManager em = emf3.createEntityManager();
        DatabasePlatform platform = getPlatform(emf3);

        try {
            Query query = em.createQuery("UPDATE QuerySyntaxEntity s SET s.intVal1 = ?1 WHERE s.strVal2 = LOWER(?2)");
            query.setParameter(1, 9);
            query.setParameter(2, "HELLO");
            em.getTransaction().begin();
            query.executeUpdate();
            em.getTransaction().commit();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("UPDATE QUERYSYNTAXENTITY SET INTVAL1 = ? WHERE (STRVAL2 = LCASE(?))", _sql3.remove(0));
            } else {
                Assert.assertEquals("UPDATE QUERYSYNTAXENTITY SET INTVAL1 = ? WHERE (STRVAL2 = LOWER(?))", _sql3.remove(0));
            }

            query = em.createQuery("UPDATE QuerySyntaxEntity s SET s.intVal1 = 9 WHERE s.strVal2 = LOWER('HELLO')");
            em.getTransaction().begin();
            query.executeUpdate();
            em.getTransaction().commit();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("UPDATE QUERYSYNTAXENTITY SET INTVAL1 = ? WHERE (STRVAL2 = LCASE(?))", _sql3.remove(0));
            } else {
                Assert.assertEquals("UPDATE QUERYSYNTAXENTITY SET INTVAL1 = ? WHERE (STRVAL2 = LOWER(?))", _sql3.remove(0));
            }

            query = em.createQuery("UPDATE QuerySyntaxEntity s SET s.intVal1 = ?1 WHERE s.strVal2 = LOWER('HELLO')");
            query.setParameter(1, 9);
            em.getTransaction().begin();
            query.executeUpdate();
            em.getTransaction().commit();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("UPDATE QUERYSYNTAXENTITY SET INTVAL1 = ? WHERE (STRVAL2 = LCASE(?))", _sql3.remove(0));
            } else {
                Assert.assertEquals("UPDATE QUERYSYNTAXENTITY SET INTVAL1 = ? WHERE (STRVAL2 = LOWER(?))", _sql3.remove(0));
            }

            // -----------------------

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaUpdate<QuerySyntaxEntity> cquery = cb.createCriteriaUpdate(QuerySyntaxEntity.class);
            Root<QuerySyntaxEntity> root = cquery.from(QuerySyntaxEntity.class);

            ParameterExpression<Integer> intValue = cb.parameter(Integer.class);
            ParameterExpression<String> strValue = cb.parameter(String.class);
            cquery.set(root.get(QuerySyntaxEntity_.intVal1), intValue);
            cquery.where(cb.equal(root.get(QuerySyntaxEntity_.strVal2), cb.lower(strValue)));

            query = em.createQuery(cquery);
            query.setParameter(intValue, 9);
            query.setParameter(strValue, "HELLO");
            em.getTransaction().begin();
            query.executeUpdate();
            em.getTransaction().commit();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("UPDATE QUERYSYNTAXENTITY SET INTVAL1 = ? WHERE (STRVAL2 = LCASE(?))", _sql3.remove(0));
            } else {
                Assert.assertEquals("UPDATE QUERYSYNTAXENTITY SET INTVAL1 = ? WHERE (STRVAL2 = LOWER(?))", _sql3.remove(0));
            }

            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaUpdate<QuerySyntaxEntity> cquery2 = cb2.createCriteriaUpdate(QuerySyntaxEntity.class);
            Root<QuerySyntaxEntity> root2 = cquery2.from(QuerySyntaxEntity.class);
            cquery2.set(root2.get(QuerySyntaxEntity_.intVal1), 9);
            cquery2.where(cb2.equal(root2.get(QuerySyntaxEntity_.strVal2), cb2.lower(cb2.literal("HELLO"))));

            query = em.createQuery(cquery2);
            em.getTransaction().begin();
            query.executeUpdate();
            em.getTransaction().commit();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("UPDATE QUERYSYNTAXENTITY SET INTVAL1 = ? WHERE (STRVAL2 = LCASE(?))", _sql3.remove(0));
            } else {
                Assert.assertEquals("UPDATE QUERYSYNTAXENTITY SET INTVAL1 = ? WHERE (STRVAL2 = LOWER(?))", _sql3.remove(0));
            }

            CriteriaBuilder cb3 = em.getCriteriaBuilder();
            CriteriaUpdate<QuerySyntaxEntity> cquery3 = cb3.createCriteriaUpdate(QuerySyntaxEntity.class);
            Root<QuerySyntaxEntity> root3 = cquery3.from(QuerySyntaxEntity.class);

            ParameterExpression<String> strValue1 = cb3.parameter(String.class);
            cquery3.set(root3.get(QuerySyntaxEntity_.intVal1), 9);
            cquery3.where(cb3.equal(root3.get(QuerySyntaxEntity_.strVal2), cb2.lower(strValue1)));

            query = em.createQuery(cquery3);
            query.setParameter(strValue1, "HELLO");
            em.getTransaction().begin();
            query.executeUpdate();
            em.getTransaction().commit();
            Assert.assertEquals(1, _sql3.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                Assert.assertEquals("UPDATE QUERYSYNTAXENTITY SET INTVAL1 = ? WHERE (STRVAL2 = LCASE(?))", _sql3.remove(0));
            } else {
                Assert.assertEquals("UPDATE QUERYSYNTAXENTITY SET INTVAL1 = ? WHERE (STRVAL2 = LOWER(?))", _sql3.remove(0));
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
