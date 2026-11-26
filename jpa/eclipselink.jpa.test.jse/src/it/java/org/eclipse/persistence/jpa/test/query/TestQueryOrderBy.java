/*
 * Copyright (c) 2025, 2026 Contributors to the Eclipse Foundation.
 * Copyright (c) 2022, 2026 Oracle and/or its affiliates. All rights reserved.
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
//     IBM - Bug 417259: Add support for Parameters in CriteriaBuilder in HAVING clause
package org.eclipse.persistence.jpa.test.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Parameter;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.ParameterExpression;
import jakarta.persistence.criteria.Root;

import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.internal.databaseaccess.Platform;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.jpa.test.framework.SQLCallListener;
import org.eclipse.persistence.jpa.test.query.model.EntityTbl01;
import org.eclipse.persistence.jpa.test.query.model.EntityTbl01_;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestQueryOrderBy {
    @Emf(name = "DefaultPersistenceUnit", createTables = DDLGen.DROP_CREATE, classes = { EntityTbl01.class }, 
            properties = { 
                    @Property(name="eclipselink.logging.level", value="FINE")})
    private EntityManagerFactory emf;

    @Emf(name = "BindLiteralsPersistenceUnit", classes = { EntityTbl01.class }, 
            properties = { 
                    @Property(name="eclipselink.target-database-properties", value="shouldBindLiterals=true"), 
                    @Property(name="eclipselink.logging.level", value="FINE")})
    private EntityManagerFactory emf2;

    @SQLCallListener(name = "DefaultPersistenceUnit")
    List<String> _sql;

    @SQLCallListener(name = "BindLiteralsPersistenceUnit")
    List<String> _sql2;

    private static boolean POPULATED = false;

    @Test
    public void testQueryOrderByLiterals1() {
        if (emf == null)
            return;

        if(!POPULATED) 
            populate();

        EntityManager em = emf.createEntityManager();

        try {
            TypedQuery<Integer> query = em.createQuery("SELECT t.itemInteger1 FROM EntityTbl01 t ORDER BY 1", Integer.class);

            List<Integer> dto01 = query.getResultList();
            assertNotNull(dto01);
            assertValues(dto01, 17, 29, 45, 83);

            query = em.createQuery("SELECT t.itemInteger1 FROM EntityTbl01 t ORDER BY 1 ASC", Integer.class);

            dto01 = query.getResultList();
            assertNotNull(dto01);
            assertValues(dto01, 17, 29, 45, 83);

            // equivalent CriteriaBuilder
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer> cquery = cb.createQuery(Integer.class);
            Root<EntityTbl01> root = cquery.from(EntityTbl01.class);
            cquery.multiselect(root.get(EntityTbl01_.itemInteger1));

            cquery.orderBy(cb.asc(cb.literal(1)));

            query = em.createQuery(cquery);
            dto01 = query.getResultList();
            assertNotNull(dto01);
            assertValues(dto01, 17, 29, 45, 83);
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }
    }

    @Test
    public void testQueryOrderByLiterals2() {
        if (emf2 == null)
            return;

        if(!POPULATED) 
            populate();

        Platform platform = getPlatform(emf);
        EntityManager em = emf2.createEntityManager();

        // DB2 and Derby does not support untyped parameter binding in ORDER BY clause
        // 'emf2' sets 'shouldBindLiterals=true', which makes literal values bind as untyped parameters
        if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
            return;
        }

        try {
            TypedQuery<Integer> query = em.createQuery("SELECT t.itemInteger1 FROM EntityTbl01 t ORDER BY 1", Integer.class);

            List<Integer> dto01 = query.getResultList();
            assertNotNull(dto01);
            assertValues(dto01, 17, 29, 45, 83);

            query = em.createQuery("SELECT t.itemInteger1 FROM EntityTbl01 t ORDER BY 1 ASC", Integer.class);

            dto01 = query.getResultList();
            assertNotNull(dto01);
            assertValues(dto01, 17, 29, 45, 83);

            // equivalent CriteriaBuilder
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer> cquery = cb.createQuery(Integer.class);
            Root<EntityTbl01> root = cquery.from(EntityTbl01.class);
            cquery.multiselect(root.get(EntityTbl01_.itemInteger1));

            cquery.orderBy(cb.asc(cb.literal(1)));

            query = em.createQuery(cquery);
            dto01 = query.getResultList();
            assertNotNull(dto01);
            assertValues(dto01, 17, 29, 45, 83);
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }
    }

    @Test
    public void testQueryOrderByLiterals3() {
        if (emf == null)
            return;

        if(!POPULATED) 
            populate();

        Platform platform = getPlatform(emf);
        EntityManager em = emf.createEntityManager();

        try {
            TypedQuery<Integer> query = em.createQuery("SELECT t.itemInteger1 FROM EntityTbl01 t WHERE t.itemString2 = 'B' ORDER BY 1", Integer.class);

            List<Integer> dto01 = query.getResultList();
            assertNotNull(dto01);
            assertValues(dto01, 17, 29, 83);
            assertEquals(1, _sql.size());
            if (platform.isDB2()) {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = ?) ORDER BY 1", _sql.remove(0));
            } else if (platform.isDB2Z() || platform.isDerby()) {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = 'B') ORDER BY 1", _sql.remove(0));
            } else {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = ?) ORDER BY " + boundOrderByValue(platform), _sql.remove(0));
            }

            query = em.createQuery("SELECT t.itemInteger1 FROM EntityTbl01 t WHERE t.itemString2 = 'B' ORDER BY 1 ASC", Integer.class);

            dto01 = query.getResultList();
            assertNotNull(dto01);
            assertValues(dto01, 17, 29, 83);
            assertEquals(1, _sql.size());
            
            if (platform.isDB2()) {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = ?) ORDER BY 1 ASC", _sql.remove(0));
            } else if (platform.isDB2Z() || platform.isDerby() || platform.isH2()) {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = 'B') ORDER BY 1 ASC", _sql.remove(0));
            } else {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = ?) ORDER BY " + boundOrderByValue(platform) + " ASC", _sql.remove(0));
            }

            // Equivalent CriteriaBuilder
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer> cquery = cb.createQuery(Integer.class);
            Root<EntityTbl01> root = cquery.from(EntityTbl01.class);
            cquery.multiselect(root.get(EntityTbl01_.itemInteger1));
            cquery.where(cb.equal(root.get(EntityTbl01_.itemString2), cb.literal("B")));
            cquery.orderBy(cb.asc(cb.literal(1)));

            query = em.createQuery(cquery);
            dto01 = query.getResultList();
            assertNotNull(dto01);
            assertValues(dto01, 17, 29, 83);
            assertEquals(1, _sql.size());
            
            if (platform.isDB2()) {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = ?) ORDER BY 1 ASC", _sql.remove(0));
            } else if (platform.isDB2Z() || platform.isDB2() || platform.isDerby() || platform.isH2()) {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = 'B') ORDER BY 1 ASC", _sql.remove(0));
            } else {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = ?) ORDER BY " + boundOrderByValue(platform) + " ASC", _sql.remove(0));
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
    public void testQueryOrderByLiterals4() {
        if (emf2 == null)
            return;

        if(!POPULATED) 
            populate();

        Platform platform = getPlatform(emf2);
        EntityManager em = emf2.createEntityManager();

        // DB2 and Derby does not support untyped parameter binding in ORDER BY clause
        // 'emf2' sets 'shouldBindLiterals=true', which makes literal values bind as untyped parameters
        if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
            return;
        }

        try {
            TypedQuery<Integer> query = em.createQuery("SELECT t.itemInteger1 FROM EntityTbl01 t WHERE t.itemString2 = 'B' ORDER BY 1", Integer.class);

            List<Integer> dto01 = query.getResultList();
            assertNotNull(dto01);
            assertValues(dto01, 17, 29, 83);
            assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = 'B') ORDER BY 1", _sql2.remove(0));
            } else {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = ?) ORDER BY " + boundOrderByValue(platform), _sql2.remove(0));
            }

            query = em.createQuery("SELECT t.itemInteger1 FROM EntityTbl01 t WHERE t.itemString2 = 'B' ORDER BY 1 ASC", Integer.class);

            dto01 = query.getResultList();
            assertNotNull(dto01);
            assertValues(dto01, 17, 29, 83);
            assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby() || platform.isH2()) {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = 'B') ORDER BY 1 ASC", _sql2.remove(0));
            } else {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = ?) ORDER BY " + boundOrderByValue(platform) + " ASC", _sql2.remove(0));
            }

            // equivalent CriteriaBuilder
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer> cquery = cb.createQuery(Integer.class);
            Root<EntityTbl01> root = cquery.from(EntityTbl01.class);
            cquery.multiselect(root.get(EntityTbl01_.itemInteger1));
            cquery.where(cb.equal(root.get(EntityTbl01_.itemString2), cb.literal("B")));
            cquery.orderBy(cb.asc(cb.literal(1)));

            query = em.createQuery(cquery);
            dto01 = query.getResultList();
            assertNotNull(dto01);
            assertValues(dto01, 17, 29, 83);
            assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby() || platform.isH2()) {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = 'B') ORDER BY 1 ASC", _sql2.remove(0));
            } else {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = ?) ORDER BY " + boundOrderByValue(platform) + " ASC", _sql2.remove(0));
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
    public void testQueryOrderByParameters1() {
        if (emf == null)
            return;

        if(!POPULATED) 
            populate();

        Platform platform = getPlatform(emf);
        EntityManager em = emf.createEntityManager();

        // DB2 and Derby does not support untyped parameter binding in ORDER BY clause
        if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
            return;
        }

        try {
            TypedQuery<Integer> query = em.createQuery("SELECT t.itemInteger1 FROM EntityTbl01 t ORDER BY ?1", Integer.class);
            query.setParameter(1, 1);

            List<Integer> dto01 = query.getResultList();
            assertNotNull(dto01);
            assertValues(dto01, 17, 29, 45, 83);

            query = em.createQuery("SELECT t.itemInteger1 FROM EntityTbl01 t ORDER BY ?1 ASC", Integer.class);
            query.setParameter(1, 1);

            dto01 = query.getResultList();
            assertNotNull(dto01);
            assertValues(dto01, 17, 29, 45, 83);

            // equivalent CriteriaBuilder
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer> cquery = cb.createQuery(Integer.class);
            Root<EntityTbl01> root = cquery.from(EntityTbl01.class);
            cquery.multiselect(root.get(EntityTbl01_.itemInteger1));

            ParameterExpression<Integer> checkParam1 = cb.parameter(Integer.class);
            cquery.orderBy(cb.asc(checkParam1));

            query = em.createQuery(cquery);
            query.setParameter(checkParam1, 1);
            dto01 = query.getResultList();
            assertNotNull(dto01);
            assertValues(dto01, 17, 29, 45, 83);
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }
    }

    @Test
    public void testQueryOrderByParameters2() {
        if (emf == null)
            return;

        if(!POPULATED) 
            populate();

        Platform platform = getPlatform(emf);
        EntityManager em = emf.createEntityManager();

        // DB2 and Derby does not support untyped parameter binding in ORDER BY clause
        if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
            return;
        }

        try {
            TypedQuery<Integer> query = em.createQuery("SELECT t.itemInteger1 FROM EntityTbl01 t WHERE t.itemString2 = ?1 ORDER BY ?2", Integer.class);
            query.setParameter(1, "B");
            query.setParameter(2, 1);

            List<Integer> dto01 = query.getResultList();
            assertNotNull(dto01);
            assertValues(dto01, 17, 29, 83);
            assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = ?) ORDER BY 1", _sql.remove(0));
            } else {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = ?) ORDER BY " + boundOrderByValue(platform), _sql.remove(0));
            }

            query = em.createQuery("SELECT t.itemInteger1 FROM EntityTbl01 t WHERE t.itemString2 = ?1 ORDER BY ?2 ASC", Integer.class);
            query.setParameter(1, "B");
            query.setParameter(2, 1);

            dto01 = query.getResultList();
            assertNotNull(dto01);
            assertValues(dto01, 17, 29, 83);
            assertEquals(1, _sql.size());
            if (platform.isH2()) {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = 'B') ORDER BY 1 ASC", _sql.remove(0));
            } else if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = ?) ORDER BY 1 ASC", _sql.remove(0));
            } else {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = ?) ORDER BY " + boundOrderByValue(platform) + " ASC", _sql.remove(0));
            }

            assertFilterRebinding(query, query.getParameter(1, String.class), platform, _sql,
                    platform.isH2());

            // equivalent CriteriaBuilder
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer> cquery = cb.createQuery(Integer.class);
            Root<EntityTbl01> root = cquery.from(EntityTbl01.class);
            cquery.multiselect(root.get(EntityTbl01_.itemInteger1));
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            cquery.where(cb.equal(root.get(EntityTbl01_.itemString2), strParam1));
            cquery.orderBy(cb.asc(intParam2));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "B");
            query.setParameter(intParam2, 1);
            dto01 = query.getResultList();
            assertNotNull(dto01);
            assertValues(dto01, 17, 29, 83);
            assertEquals(1, _sql.size());
            if (platform.isH2()) {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = 'B') ORDER BY 1 ASC", _sql.remove(0));
            } else if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = ?) ORDER BY 1 ASC", _sql.remove(0));
            } else {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = ?) ORDER BY " + boundOrderByValue(platform) + " ASC", _sql.remove(0));
            }
            assertFilterRebinding(query, strParam1, platform, _sql,
                    platform.isH2());
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }
    }

    @Test
    public void testQueryOrderByMix1() {
        if (emf == null)
            return;

        if(!POPULATED) 
            populate();

        Platform platform = getPlatform(emf);
        EntityManager em = emf.createEntityManager();

        try {
            TypedQuery<Integer> query = em.createQuery("SELECT t.itemInteger1 FROM EntityTbl01 t WHERE t.itemString2 = ?1 ORDER BY 1", Integer.class);
            query.setParameter(1, "B");

            List<Integer> dto01 = query.getResultList();
            assertNotNull(dto01);
            assertValues(dto01, 17, 29, 83);
            assertEquals(1, _sql.size());
            
            if (platform.isDB2Z() || platform.isDerby()) {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = 'B') ORDER BY 1", _sql.remove(0));
            } else if (platform.isDB2()) {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = ?) ORDER BY 1", _sql.remove(0));
            } else {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = ?) ORDER BY " + boundOrderByValue(platform), _sql.remove(0));
            }

            query = em.createQuery("SELECT t.itemInteger1 FROM EntityTbl01 t WHERE t.itemString2 = ?1 ORDER BY 1 ASC", Integer.class);
            query.setParameter(1, "B");

            dto01 = query.getResultList();
            assertNotNull(dto01);
            assertValues(dto01, 17, 29, 83);
            assertEquals(1, _sql.size());
            
            if (platform.isDB2Z() || platform.isDerby() || platform.isH2()) {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = 'B') ORDER BY 1 ASC", _sql.remove(0));
            } else if (platform.isDB2()) {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = ?) ORDER BY 1 ASC", _sql.remove(0));
            } else {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = ?) ORDER BY " + boundOrderByValue(platform) + " ASC", _sql.remove(0));
            }

            assertFilterRebinding(query, query.getParameter(1, String.class), platform, _sql,
                    platform.isH2() || platform.isDB2Z() || platform.isDerby());

            // equivalent CriteriaBuilder
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer> cquery = cb.createQuery(Integer.class);
            Root<EntityTbl01> root = cquery.from(EntityTbl01.class);
            cquery.multiselect(root.get(EntityTbl01_.itemInteger1));
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            cquery.where(cb.equal(root.get(EntityTbl01_.itemString2), strParam1));
            cquery.orderBy(cb.asc(cb.literal(1)));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "B");
            dto01 = query.getResultList();
            assertNotNull(dto01);
            assertValues(dto01, 17, 29, 83);
            assertEquals(1, _sql.size());
            
            if (platform.isDB2Z() || platform.isDerby() || platform.isH2()) {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = 'B') ORDER BY 1 ASC", _sql.remove(0));
            } else if (platform.isDB2()) {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = ?) ORDER BY 1 ASC", _sql.remove(0));
            } else {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = ?) ORDER BY " + boundOrderByValue(platform) + " ASC", _sql.remove(0));
            }
            assertFilterRebinding(query, strParam1, platform, _sql,
                    platform.isH2() || platform.isDB2Z() || platform.isDerby());
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }
    }

    @Test
    public void testQueryOrderByMix2() {
        if (emf2 == null)
            return;

        if(!POPULATED) 
            populate();

        Platform platform = getPlatform(emf2);
        EntityManager em = emf2.createEntityManager();

        // DB2 and Derby does not support untyped parameter binding in ORDER BY clause
        // 'emf2' sets 'shouldBindLiterals=true', which makes literal values bind as untyped parameters
        if (platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
            return;
        }

        try {
            TypedQuery<Integer> query = em.createQuery("SELECT t.itemInteger1 FROM EntityTbl01 t WHERE t.itemString2 = ?1 ORDER BY 1", Integer.class);
            query.setParameter(1, "B");

            List<Integer> dto01 = query.getResultList();
            assertNotNull(dto01);
            assertValues(dto01, 17, 29, 83);
            assertEquals(1, _sql2.size());
            if (platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = 'B') ORDER BY 1", _sql2.remove(0));
            } else {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = ?) ORDER BY " + boundOrderByValue(platform), _sql2.remove(0));
            }

            query = em.createQuery("SELECT t.itemInteger1 FROM EntityTbl01 t WHERE t.itemString2 = ?1 ORDER BY 1 ASC", Integer.class);
            query.setParameter(1, "B");

            dto01 = query.getResultList();
            assertNotNull(dto01);
            assertValues(dto01, 17, 29, 83);
            assertEquals(1, _sql2.size());
            if (platform.isDB2Z() || platform.isDB2() || platform.isDerby() || platform.isH2()) {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = 'B') ORDER BY 1 ASC", _sql2.remove(0));
            } else {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = ?) ORDER BY " + boundOrderByValue(platform) + " ASC", _sql2.remove(0));
            }

            assertFilterRebinding(query, query.getParameter(1, String.class), platform, _sql2,
                    platform.isH2() || platform.isDB2Z() || platform.isDerby() || platform.isDB2());

            // equivalent CriteriaBuilder
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer> cquery = cb.createQuery(Integer.class);
            Root<EntityTbl01> root = cquery.from(EntityTbl01.class);
            cquery.multiselect(root.get(EntityTbl01_.itemInteger1));
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            cquery.where(cb.equal(root.get(EntityTbl01_.itemString2), strParam1));
            cquery.orderBy(cb.asc(cb.literal(1)));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "B");
            dto01 = query.getResultList();
            assertNotNull(dto01);
            assertValues(dto01, 17, 29, 83);
            assertEquals(1, _sql2.size());
            if (platform.isDB2Z() || platform.isDB2() || platform.isDerby() || platform.isH2()) {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = 'B') ORDER BY 1 ASC", _sql2.remove(0));
            } else {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = ?) ORDER BY " + boundOrderByValue(platform) + " ASC", _sql2.remove(0));
            }
            assertFilterRebinding(query, strParam1, platform, _sql2,
                    platform.isH2() || platform.isDB2Z() || platform.isDerby() || platform.isDB2());
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }
    }

    @Test
    public void testQueryOrderByMappedAttributes() {
        if (emf == null) {
            return;
        }
        if (!POPULATED) {
            populate();
        }

        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Integer> query = em.createQuery(
                    "SELECT t.itemInteger1 FROM EntityTbl01 t ORDER BY t.itemInteger1 ASC", Integer.class);
            assertEquals(List.of(17, 29, 45, 83), query.getResultList());
            assertSQL(_sql, "SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 ORDER BY ITEM_INTEGER1 ASC");

            query = em.createQuery(
                    "SELECT t.itemInteger1 FROM EntityTbl01 t ORDER BY t.itemInteger1 DESC", Integer.class);
            assertEquals(List.of(83, 45, 29, 17), query.getResultList());
            assertSQL(_sql, "SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 ORDER BY ITEM_INTEGER1 DESC");

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer> cquery = cb.createQuery(Integer.class);
            Root<EntityTbl01> root = cquery.from(EntityTbl01.class);
            cquery.select(root.get(EntityTbl01_.itemInteger1));
            cquery.orderBy(cb.asc(root.get(EntityTbl01_.itemInteger1)));
            assertEquals(List.of(17, 29, 45, 83), em.createQuery(cquery).getResultList());
            assertSQL(_sql, "SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 ORDER BY ITEM_INTEGER1 ASC");

            cquery.orderBy(cb.desc(root.get(EntityTbl01_.itemInteger1)));
            assertEquals(List.of(83, 45, 29, 17), em.createQuery(cquery).getResultList());
            assertSQL(_sql, "SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 ORDER BY ITEM_INTEGER1 DESC");
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    @Test
    public void testQueryOrderByValueSemantics() {
        if (!POPULATED) {
            populate();
        }
        for (EntityManagerFactory factory : List.of(emf, emf2)) {
            EntityManager em = factory.createEntityManager();
            List<String> sql = factory == emf ? _sql : _sql2;
            try {
                assertMappedAlias(em, sql);
                // Scalar subqueries are the SQL Server-specific rendering contract.
                if (getPlatform(factory).isSQLServer()) {
                    for (int value : new int[] {1, 2}) {
                        for (String direction : List.of("", " ASC", " DESC")) {
                            String jpql = "SELECT t.itemInteger1 FROM EntityTbl01 t ORDER BY "
                                    + value + direction + ", t.itemInteger1 DESC";
                            TypedQuery<Integer> query = em.createQuery(jpql, Integer.class);
                            assertEquals(List.of(83, 45, 29, 17), query.getResultList());
                            assertSQL(sql, "SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 ORDER BY (SELECT ?)"
                                    + direction + ", ITEM_INTEGER1 DESC");
                            // A fresh query from the same text must retain value semantics too.
                            assertEquals(List.of(83, 45, 29, 17), em.createQuery(jpql, Integer.class).getResultList());
                            assertSQL(sql, "SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 ORDER BY (SELECT ?)"
                                    + direction + ", ITEM_INTEGER1 DESC");

                            TypedQuery<Object[]> tuples = em.createQuery(
                                    "SELECT t.KeyString, t.itemInteger1 FROM EntityTbl01 t ORDER BY "
                                            + value + direction + ", t.itemInteger1 DESC", Object[].class);
                            List<Object[]> rows = tuples.getResultList();
                            assertEquals(List.of("Key15", "Key14", "Key17", "Key16"),
                                    rows.stream().map(row -> row[0]).toList());
                            assertEquals(List.of(83, 45, 29, 17), rows.stream().map(row -> row[1]).toList());
                            assertSQL(sql, "SELECT KEY_CHAR, ITEM_INTEGER1 FROM SIMPLE_TBL01 ORDER BY (SELECT ?)"
                                    + direction + ", ITEM_INTEGER1 DESC");
                        }
                    }

                    CriteriaBuilder cb = em.getCriteriaBuilder();
                    for (Number value : Arrays.asList(1, 2, 0, -1, Integer.MIN_VALUE, Integer.MAX_VALUE, 1.5, null)) {
                        jakarta.persistence.criteria.Expression<? extends Number> literal = value == null
                                ? cb.nullLiteral(Integer.class) : cb.literal(value);
                        CriteriaQuery<Integer> criteria = cb.createQuery(Integer.class);
                        Root<EntityTbl01> root = criteria.from(EntityTbl01.class);
                        criteria.select(root.get(EntityTbl01_.itemInteger1));
                        criteria.orderBy(cb.asc(literal), cb.desc(root.get(EntityTbl01_.itemInteger1)));
                        assertEquals(List.of(83, 45, 29, 17), em.createQuery(criteria).getResultList());
                        assertSQL(sql, "SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 ORDER BY (SELECT ?) ASC, ITEM_INTEGER1 DESC");
                        // Reuse the original value expression in a separately prepared query.
                        criteria.orderBy(cb.desc(literal), cb.desc(root.get(EntityTbl01_.itemInteger1)));
                        assertEquals(List.of(83, 45, 29, 17), em.createQuery(criteria).getResultList());
                        assertSQL(sql, "SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 ORDER BY (SELECT ?) DESC, ITEM_INTEGER1 DESC");
                    }
                    for (int value : new int[] {1, 2}) {
                        TypedQuery<Integer> query = em.createQuery(
                                "SELECT t.itemInteger1 FROM EntityTbl01 t ORDER BY " + value + ", t.itemInteger1 DESC",
                                Integer.class);
                        query.setHint(QueryHints.BIND_PARAMETERS, HintValues.FALSE);
                        assertEquals(List.of(83, 45, 29, 17), query.getResultList());
                        assertSQL(sql, "SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 ORDER BY (SELECT "
                                + value + "), ITEM_INTEGER1 DESC");

                        CriteriaQuery<Integer> criteria = cb.createQuery(Integer.class);
                        Root<EntityTbl01> root = criteria.from(EntityTbl01.class);
                        criteria.select(root.get(EntityTbl01_.itemInteger1));
                        criteria.orderBy(cb.asc(cb.literal(value)), cb.desc(root.get(EntityTbl01_.itemInteger1)));
                        TypedQuery<Integer> typed = em.createQuery(criteria);
                        typed.setHint(QueryHints.BIND_PARAMETERS, HintValues.FALSE);
                        assertEquals(List.of(83, 45, 29, 17), typed.getResultList());
                        assertSQL(sql, "SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 ORDER BY (SELECT "
                                + value + ") ASC, ITEM_INTEGER1 DESC");
                    }
                }
            } finally {
                em.close();
            }
        }
    }

    @Test
    public void testQueryOrderByParameterMetadata() {
        if (!POPULATED) {
            populate();
        }
        EntityManager em = emf.createEntityManager();
        try {
            assertMappedAlias(em, _sql);
            if (getPlatform(emf).isSQLServer()) {
                String jpql = "SELECT t.itemInteger1 FROM EntityTbl01 t WHERE t.itemString2 = :filter "
                        + "ORDER BY :sort, t.itemInteger1 DESC";
                TypedQuery<Integer> query = em.createQuery(jpql, Integer.class);
                Parameter<?> sort = query.getParameter("sort");
                assertEquals(2, query.getParameters().size());
                assertTrue(query.getParameters().contains(sort));
                assertEquals("sort", sort.getName());
                assertEquals(Object.class, sort.getParameterType());
                assertNull(sort.getPosition());
                assertFalse(query.isBound(sort));
                assertEquals(String.class, query.getParameter("filter").getParameterType());
                for (Object value : Arrays.asList(1, 2, 0, -1, Integer.MIN_VALUE, Integer.MAX_VALUE, null,
                        "a'; SELECT 1; --")) {
                    for (String filter : List.of("B", "missing", "B")) {
                        query.setParameter("filter", filter);
                        query.setParameter("sort", value);
                        assertTrue(query.isBound(sort));
                        assertEquals(value, query.getParameterValue(sort));
                        assertEquals(filter, query.getParameterValue("filter"));
                        assertEquals("B".equals(filter) ? List.of(83, 29, 17) : List.of(), query.getResultList());
                        assertSQL(_sql, "SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = ?) "
                                + "ORDER BY (SELECT ?), ITEM_INTEGER1 DESC");
                        assertEquals(2, query.getParameters().size());
                        assertEquals(Object.class, query.getParameter("sort").getParameterType());
                        assertEquals(value, query.getParameterValue("sort"));
                    }
                }
                TypedQuery<Integer> positional = em.createQuery(
                        "SELECT t.itemInteger1 FROM EntityTbl01 t WHERE t.itemInteger1 > ?1 "
                                + "ORDER BY ?1 ASC, t.itemInteger1 DESC", Integer.class);
                Parameter<Object> shared = positional.getParameter(1, Object.class);
                Class<?> sharedType = shared.getParameterType();
                assertEquals(1, positional.getParameters().size());
                assertEquals(Integer.valueOf(1), shared.getPosition());
                assertNotNull(sharedType);
                assertNull(shared.getName());
                assertFalse(positional.isBound(shared));
                for (int value : new int[] {0, 29, 100, 0}) {
                    positional.setParameter(shared, value);
                    assertEquals(value == 100 ? List.of() : value == 29 ? List.of(83, 45) : List.of(83, 45, 29, 17),
                            positional.getResultList());
                    assertSQL(_sql, "SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_INTEGER1 > ?) "
                            + "ORDER BY (SELECT ?) ASC, ITEM_INTEGER1 DESC");
                    assertTrue(positional.isBound(shared));
                    assertEquals(sharedType, positional.getParameter(1).getParameterType());
                    assertEquals(Integer.valueOf(value), positional.getParameterValue(1));
                }

                CriteriaBuilder cb = em.getCriteriaBuilder();
                for (boolean named : new boolean[] {true, false}) {
                    ParameterExpression<Integer> parameter = named ? cb.parameter(Integer.class, "sort") : cb.parameter(Integer.class);
                    CriteriaQuery<Integer> criteria = cb.createQuery(Integer.class);
                    Root<EntityTbl01> root = criteria.from(EntityTbl01.class);
                    criteria.select(root.get(EntityTbl01_.itemInteger1));
                    criteria.orderBy(cb.desc(parameter), cb.desc(root.get(EntityTbl01_.itemInteger1)));
                    TypedQuery<Integer> typed = em.createQuery(criteria);
                    assertEquals(1, typed.getParameters().size());
                    assertTrue(typed.getParameters().contains(parameter));
                    assertEquals(Integer.class, parameter.getParameterType());
                    assertEquals(named ? "sort" : null, parameter.getName());
                    assertNull(parameter.getPosition());
                    assertFalse(typed.isBound(parameter));
                    if (named) {
                        assertEquals(parameter, typed.getParameter("sort", Integer.class));
                    }
                    for (Integer value : Arrays.asList(1, 2, null)) {
                        typed.setParameter(parameter, value);
                        assertEquals(List.of(83, 45, 29, 17), typed.getResultList());
                        assertSQL(_sql, "SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 ORDER BY (SELECT ?) DESC, ITEM_INTEGER1 DESC");
                        assertTrue(typed.isBound(parameter));
                        assertEquals(value, typed.getParameterValue(parameter));
                        assertTrue(typed.getParameters().contains(parameter));
                    }
                }
            }
        } finally {
            em.close();
        }
    }

    @Test
    public void testQueryOrderByParameterValidation() {
        if (!POPULATED) {
            populate();
        }
        EntityManager em = emf.createEntityManager();
        try {
            assertMappedAlias(em, _sql);
            TypedQuery<Integer> missing = em.createQuery(
                    "SELECT t.itemInteger1 FROM EntityTbl01 t ORDER BY :sort", Integer.class);
            assertThrows(IllegalStateException.class, missing::getResultList);
            assertTrue(_sql.isEmpty());

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer> criteria = cb.createQuery(Integer.class);
            Root<EntityTbl01> root = criteria.from(EntityTbl01.class);
            ParameterExpression<Integer> sort = cb.parameter(Integer.class, "sort");
            criteria.select(root.get(EntityTbl01_.itemInteger1)).orderBy(cb.asc(sort));
            TypedQuery<Integer> typed = em.createQuery(criteria);
            assertEquals(sort, typed.getParameter("sort", Integer.class));
            ParameterExpression<String> wrongDescriptor = cb.parameter(String.class, "sort");
            assertThrows(IllegalArgumentException.class, () -> typed.setParameter(wrongDescriptor, "wrong type"));
            assertEquals(Integer.class, typed.getParameter("sort").getParameterType());
            assertFalse(typed.isBound(sort));
            assertTrue(_sql.isEmpty());

            TypedQuery<Integer> typedWhere = em.createQuery("SELECT t.itemInteger1 FROM EntityTbl01 t "
                    + "WHERE t.itemInteger1 = :threshold ORDER BY :sort", Integer.class);
            Parameter<Integer> threshold = typedWhere.getParameter("threshold", Integer.class);
            assertThrows(IllegalArgumentException.class, () -> typedWhere.setParameter("threshold", "wrong type"));
            assertFalse(typedWhere.isBound(threshold));
            assertEquals(Integer.class, typedWhere.getParameter("threshold").getParameterType());
            assertTrue(_sql.isEmpty());
        } finally {
            em.close();
        }
    }

    @Test
    public void testQueryOrderByPaginationAndNulls() {
        if (!POPULATED) {
            populate();
        }
        EntityManager em = emf.createEntityManager();
        try {
            assertMappedAlias(em, _sql);
            if (getPlatform(emf).isSQLServer()) {
                List<Integer> descending = List.of(83, 45, 29, 17);
                for (int[] page : new int[][] {{0, -1}, {0, 2}, {1, -1}, {1, 2}, {10, 2}}) {
                    TypedQuery<Integer> query = em.createQuery(
                            "SELECT t.itemInteger1 FROM EntityTbl01 t ORDER BY :sort, t.itemInteger1 DESC", Integer.class);
                    query.setParameter("sort", 1);
                    query.setFirstResult(page[0]);
                    if (page[1] >= 0) {
                        query.setMaxResults(page[1]);
                    }
                    int from = Math.min(page[0], descending.size());
                    int to = page[1] < 0 ? descending.size() : Math.min(from + page[1], descending.size());
                    String suffix = page[0] == 0 && page[1] < 0 ? "" : page[1] < 0
                            ? " OFFSET ? ROWS" : " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
                    for (int repeat = 0; repeat < 2; repeat++) {
                        assertEquals(descending.subList(from, to), query.getResultList());
                        assertSQL(_sql, "SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 ORDER BY (SELECT ?), ITEM_INTEGER1 DESC" + suffix);
                    }
                }
                TypedQuery<Integer> empty = em.createQuery(
                        "SELECT t.itemInteger1 FROM EntityTbl01 t WHERE t.itemString2 = :filter "
                                + "ORDER BY :sort, t.itemInteger1 DESC", Integer.class);
                empty.setParameter("filter", "missing").setParameter("sort", 2).setFirstResult(1).setMaxResults(2);
                assertEquals(List.of(), empty.getResultList());
                assertSQL(_sql, "SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = ?) "
                        + "ORDER BY (SELECT ?), ITEM_INTEGER1 DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

                for (String nulls : List.of("FIRST", "LAST")) {
                    for (String direction : List.of("", " ASC", " DESC")) {
                        TypedQuery<Integer> query = em.createQuery("SELECT t.itemInteger1 FROM EntityTbl01 t "
                                + "ORDER BY :sort" + direction + " NULLS " + nulls + ", t.itemInteger1 DESC", Integer.class);
                        query.setParameter("sort", null);
                        assertEquals(descending, query.getResultList());
                        String rank = "FIRST".equals(nulls) ? "0 ELSE 1" : "1 ELSE 0";
                        assertSQL(_sql, "SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 ORDER BY "
                                + "(CASE WHEN (SELECT ?) IS NULL THEN " + rank + " END), (SELECT ?)"
                                + direction + ", ITEM_INTEGER1 DESC");
                    }
                    TypedQuery<Integer> query = em.createQuery("SELECT t.itemInteger1 FROM EntityTbl01 t "
                            + "ORDER BY t.itemString2 ASC NULLS " + nulls + ", t.itemInteger1 DESC", Integer.class);
                    assertEquals("FIRST".equals(nulls) ? List.of(45, 83, 29, 17) : List.of(83, 29, 17, 45), query.getResultList());
                    String rank = "FIRST".equals(nulls) ? "0 ELSE 1" : "1 ELSE 0";
                    assertSQL(_sql, "SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 ORDER BY "
                            + "(CASE WHEN ITEM_STRING2 IS NULL THEN " + rank + " END), ITEM_STRING2 ASC, ITEM_INTEGER1 DESC");
                }
            }
        } finally {
            em.close();
        }
    }

    @Test
    public void testQueryOrderByExpressionBoundaries() {
        if (!POPULATED) {
            populate();
        }
        EntityManager em = emf.createEntityManager();
        try {
            assertMappedAlias(em, _sql);
            if (getPlatform(emf).isSQLServer()) {
                for (String expression : List.of(":sort + t.itemInteger1", "t.itemInteger1 + :sort", "ABS(:sort)")) {
                    TypedQuery<Integer> query = em.createQuery("SELECT t.itemInteger1 FROM EntityTbl01 t ORDER BY "
                            + expression + " DESC, t.itemInteger1 DESC", Integer.class);
                    query.setParameter("sort", -2);
                    assertEquals(List.of(83, 45, 29, 17), query.getResultList());
                    String expected = "ABS(:sort)".equals(expression) ? "ABS(?)" : expression.startsWith(":sort")
                            ? "(? + ITEM_INTEGER1)" : "(ITEM_INTEGER1 + ?)";
                    assertSQL(_sql, "SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 ORDER BY " + expected + " DESC, ITEM_INTEGER1 DESC");
                }
                TypedQuery<Integer> arithmetic = em.createQuery("SELECT t.itemInteger1 FROM EntityTbl01 t "
                        + "ORDER BY :left + :right, t.itemInteger1 DESC", Integer.class);
                arithmetic.setParameter("left", 1).setParameter("right", 2);
                assertEquals(List.of(83, 45, 29, 17), arithmetic.getResultList());
                assertSQL(_sql, "SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 ORDER BY (? + ?), ITEM_INTEGER1 DESC");
                TypedQuery<Integer> product = em.createQuery("SELECT t.itemInteger1 FROM EntityTbl01 t "
                        + "ORDER BY :sign * t.itemInteger1 ASC", Integer.class);
                for (int sign : new int[] {1, -1, 1}) {
                    product.setParameter("sign", sign);
                    assertEquals(sign == 1 ? List.of(17, 29, 45, 83) : List.of(83, 45, 29, 17), product.getResultList());
                    assertSQL(_sql, "SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 ORDER BY (? * ITEM_INTEGER1) ASC");
                }
                Query nativeQuery = em.createNativeQuery("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 ORDER BY 1");
                assertEquals(List.of(17, 29, 45, 83), nativeQuery.getResultList());
                assertSQL(_sql, "SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 ORDER BY 1");
                assertSqlServerError(em.createNativeQuery("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 ORDER BY 2"), 108);
                assertSQL(_sql, "SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 ORDER BY 2");
                TypedQuery<Integer> unsupported = em.createQuery(
                        "SELECT t.itemInteger1 FROM EntityTbl01 t ORDER BY 1 + 1", Integer.class);
                unsupported.setHint(QueryHints.BIND_PARAMETERS, HintValues.FALSE);
                assertSqlServerError(unsupported, 408);
                assertSQL(_sql, "SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 ORDER BY (1 + 1)");
            }
        } finally {
            em.close();
        }
    }

    @Test
    public void testQueryOrderByDistinctAndSetOperations() {
        if (!POPULATED) {
            populate();
        }
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Integer> mapped = em.createQuery("SELECT DISTINCT t.itemInteger1 AS orderedValue "
                    + "FROM EntityTbl01 t ORDER BY orderedValue DESC", Integer.class);
            assertEquals(List.of(83, 45, 29, 17), mapped.getResultList());
            assertSQL(_sql, "SELECT DISTINCT ITEM_INTEGER1 FROM SIMPLE_TBL01 ORDER BY ITEM_INTEGER1 DESC");
            if (getPlatform(emf).isSQLServer()) {
                TypedQuery<Integer> literal = em.createQuery("SELECT DISTINCT t.itemInteger1 "
                        + "FROM EntityTbl01 t ORDER BY 1", Integer.class);
                literal.setHint(QueryHints.BIND_PARAMETERS, HintValues.FALSE);
                assertEquals(List.of(17, 29, 45, 83), literal.getResultList());
                assertSQL(_sql, "SELECT DISTINCT ITEM_INTEGER1, 1 FROM SIMPLE_TBL01 ORDER BY 1");
                CriteriaBuilder cb = em.getCriteriaBuilder();
                CriteriaQuery<Integer> criteria = cb.createQuery(Integer.class);
                Root<EntityTbl01> root = criteria.from(EntityTbl01.class);
                criteria.select(root.get(EntityTbl01_.itemInteger1)).distinct(true).orderBy(cb.asc(cb.literal(1)));
                TypedQuery<Integer> typed = em.createQuery(criteria);
                typed.setHint(QueryHints.BIND_PARAMETERS, HintValues.FALSE);
                assertEquals(List.of(17, 29, 45, 83), typed.getResultList());
                assertSQL(_sql, "SELECT DISTINCT ITEM_INTEGER1, 1 FROM SIMPLE_TBL01 ORDER BY 1 ASC");

                CriteriaQuery<Integer> left = cb.createQuery(Integer.class);
                Root<EntityTbl01> leftRoot = left.from(EntityTbl01.class);
                left.select(leftRoot.get(EntityTbl01_.itemInteger1));
                left.where(cb.gt(leftRoot.get(EntityTbl01_.itemInteger1), 29));
                CriteriaQuery<Integer> right = cb.createQuery(Integer.class);
                Root<EntityTbl01> rightRoot = right.from(EntityTbl01.class);
                right.select(rightRoot.get(EntityTbl01_.itemInteger1));
                right.where(cb.le(rightRoot.get(EntityTbl01_.itemInteger1), 29));
                assertValues(em.createQuery(cb.union(left, right)).getResultList(), 17, 29, 45, 83);
                assertSQL(_sql, "(SELECT t0.ITEM_INTEGER1 FROM SIMPLE_TBL01 t0 WHERE (t0.ITEM_INTEGER1 > ?) "
                        + "UNION (SELECT t1.ITEM_INTEGER1 FROM SIMPLE_TBL01 t1 WHERE (t1.ITEM_INTEGER1 <= ?)))");

                // This branch's provider prints ORDER BY before UNION. Retain the supported
                // unordered provider operation and the native SQL ordering contract separately.
                Query union = em.createNativeQuery("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 UNION "
                        + "SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 ORDER BY 1 DESC");
                assertEquals(List.of(83, 45, 29, 17), union.getResultList());
                assertSQL(_sql, "SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 UNION "
                        + "SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 ORDER BY 1 DESC");
                TypedQuery<Integer> unsupported = em.createQuery("SELECT DISTINCT t.itemInteger1 "
                        + "FROM EntityTbl01 t ORDER BY :sort", Integer.class);
                unsupported.setParameter("sort", 1);
                assertSqlServerError(unsupported, 1008);
                assertSQL(_sql, "SELECT DISTINCT ITEM_INTEGER1, ? FROM SIMPLE_TBL01 ORDER BY ?");
            }
        } finally {
            em.close();
        }
    }

    private void assertMappedAlias(EntityManager em, List<String> sql) {
        TypedQuery<Integer> mapped = em.createQuery(
                "SELECT t.itemInteger1 AS orderedValue FROM EntityTbl01 t ORDER BY orderedValue DESC", Integer.class);
        assertEquals(List.of(83, 45, 29, 17), mapped.getResultList());
        assertSQL(sql, "SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 ORDER BY ITEM_INTEGER1 DESC");
        TypedQuery<Integer> scalar = em.createQuery("SELECT ABS(t.itemInteger1) AS orderedValue "
                + "FROM EntityTbl01 t ORDER BY orderedValue DESC", Integer.class);
        assertEquals(List.of(83, 45, 29, 17), scalar.getResultList());
        assertSQL(sql, "SELECT ABS(ITEM_INTEGER1) FROM SIMPLE_TBL01 ORDER BY ABS(ITEM_INTEGER1) DESC");
    }

    private void assertSqlServerError(Query query, int code) {
        PersistenceException failure = assertThrows(PersistenceException.class, query::getResultList);
        Throwable cause = failure;
        while (cause != null && !(cause instanceof SQLException)) {
            cause = cause.getCause();
        }
        assertNotNull("Expected SQL Server error " + code, cause);
        assertEquals(code, ((SQLException) cause).getErrorCode());
    }

    private void assertValues(List<Integer> actual, Integer... expected) {
        // A bound ORDER BY constant does not define row order, but must preserve every value.
        assertEquals(List.of(expected), actual.stream().sorted().toList());
    }

    private void assertFilterRebinding(TypedQuery<Integer> query, Parameter<String> parameter,
            Platform platform, List<String> sql, boolean inlineWhere) {
        // WHERE binding also depends on the query's literals and factory configuration.
        String order = platform.isH2() || platform.isDB2Z() || platform.isDB2() || platform.isDerby() ? "1" : boundOrderByValue(platform);
        for (String value : List.of("missing", "B")) {
            query.setParameter(parameter, value);
            List<Integer> results = query.getResultList();
            if ("B".equals(value)) {
                assertValues(results, 17, 29, 83);
            } else {
                assertEquals(List.of(), results);
            }
            String where = inlineWhere ? "'" + value + "'" : "?";
            assertSQL(sql, "SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = "
                    + where + ") ORDER BY " + order + " ASC");
        }
    }

    private void assertSQL(List<String> sql, String expected) {
        assertEquals(1, sql.size());
        assertEquals(expected, sql.remove(0));
    }

    private void populate() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            EntityTbl01 tbl1 = new EntityTbl01();
            tbl1.setKeyString("Key14");
            tbl1.setItemString1("A");
            tbl1.setItemString2(null);
            tbl1.setItemString3("C");
            tbl1.setItemString4("D");
            tbl1.setItemInteger1(45);
            em.persist(tbl1);

            EntityTbl01 tbl2 = new EntityTbl01();
            tbl2.setKeyString("Key15");
            tbl2.setItemString1("A");
            tbl2.setItemString2("B");
            tbl2.setItemString3("C");
            tbl2.setItemString4(null);
            tbl2.setItemInteger1(83);
            em.persist(tbl2);

            EntityTbl01 tbl3 = new EntityTbl01();
            tbl3.setKeyString("Key16");
            tbl3.setItemString1(null);
            tbl3.setItemString2("B");
            tbl3.setItemString3("C");
            tbl3.setItemString4("D");
            tbl3.setItemInteger1(17);
            em.persist(tbl3);

            EntityTbl01 tbl4 = new EntityTbl01();
            tbl4.setKeyString("Key17");
            tbl4.setItemString1("A");
            tbl4.setItemString2("B");
            tbl4.setItemString3("C");
            tbl4.setItemString4(null);
            tbl4.setItemInteger1(29);
            em.persist(tbl4);

            em.getTransaction().commit();

            POPULATED = true;
            _sql.clear();
            _sql2.clear();
        } finally {
            if(em.isOpen()) {
                em.close();
            }
        }
    }

    private String boundOrderByValue(Platform platform) {
        return platform.isSQLServer() ? "(SELECT ?)" : "?";
    }

    private DatabasePlatform getPlatform(EntityManagerFactory emf) {
        return ((EntityManagerFactoryImpl)emf).getServerSession().getPlatform();
    }
}
