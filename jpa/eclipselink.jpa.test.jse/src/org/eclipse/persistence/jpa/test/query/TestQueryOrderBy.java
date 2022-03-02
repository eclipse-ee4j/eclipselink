/*
 * Copyright (c) 2022 IBM Corporation, Oracle, and/or affiliates. All rights reserved.
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
import static org.junit.Assert.assertNotNull;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;

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
            TypedQuery<Integer> query = em.createQuery(""
                    + "SELECT t.itemInteger1 FROM EntityTbl01 t ORDER BY 1", Integer.class);

            List<Integer> dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(4, dto01.size());

            query = em.createQuery(""
                    + "SELECT t.itemInteger1 FROM EntityTbl01 t ORDER BY 1 ASC", Integer.class);

            dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(4, dto01.size());

            // equivalent CriteriaBuilder
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer> cquery = cb.createQuery(Integer.class);
            Root<EntityTbl01> root = cquery.from(EntityTbl01.class);
            cquery.multiselect(root.get(EntityTbl01_.itemInteger1));

            cquery.orderBy(cb.asc(cb.literal(1)));

            query = em.createQuery(cquery);
            dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(4, dto01.size());
        } finally {
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
            TypedQuery<Integer> query = em.createQuery(""
                    + "SELECT t.itemInteger1 FROM EntityTbl01 t ORDER BY 1", Integer.class);

            List<Integer> dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(4, dto01.size());

            query = em.createQuery(""
                    + "SELECT t.itemInteger1 FROM EntityTbl01 t ORDER BY 1 ASC", Integer.class);

            dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(4, dto01.size());

            // equivalent CriteriaBuilder
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer> cquery = cb.createQuery(Integer.class);
            Root<EntityTbl01> root = cquery.from(EntityTbl01.class);
            cquery.multiselect(root.get(EntityTbl01_.itemInteger1));

            cquery.orderBy(cb.asc(cb.literal(1)));

            query = em.createQuery(cquery);
            dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(4, dto01.size());
        } finally {
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
            TypedQuery<Integer> query = em.createQuery(""
                    + "SELECT t.itemInteger1 FROM EntityTbl01 t WHERE t.itemString2 = 'B' ORDER BY 1", Integer.class);

            List<Integer> dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(3, dto01.size());
            assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = 'B') ORDER BY 1", _sql.remove(0));
            } else {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = ?) ORDER BY ?", _sql.remove(0));
            }

            query = em.createQuery(""
                    + "SELECT t.itemInteger1 FROM EntityTbl01 t WHERE t.itemString2 = 'B' ORDER BY 1 ASC", Integer.class);

            dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(3, dto01.size());
            assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = 'B') ORDER BY 1 ASC", _sql.remove(0));
            } else {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = ?) ORDER BY ? ASC", _sql.remove(0));
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
            assertEquals(3, dto01.size());
            assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = 'B') ORDER BY 1 ASC", _sql.remove(0));
            } else {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = ?) ORDER BY ? ASC", _sql.remove(0));
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
            TypedQuery<Integer> query = em.createQuery(""
                    + "SELECT t.itemInteger1 FROM EntityTbl01 t WHERE t.itemString2 = 'B' ORDER BY 1", Integer.class);

            List<Integer> dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(3, dto01.size());
            assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = 'B') ORDER BY 1", _sql2.remove(0));
            } else {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = ?) ORDER BY ?", _sql2.remove(0));
            }

            query = em.createQuery(""
                    + "SELECT t.itemInteger1 FROM EntityTbl01 t WHERE t.itemString2 = 'B' ORDER BY 1 ASC", Integer.class);

            dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(3, dto01.size());
            assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = 'B') ORDER BY 1 ASC", _sql2.remove(0));
            } else {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = ?) ORDER BY ? ASC", _sql2.remove(0));
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
            assertEquals(3, dto01.size());
            assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = 'B') ORDER BY 1 ASC", _sql2.remove(0));
            } else {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = ?) ORDER BY ? ASC", _sql2.remove(0));
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
            TypedQuery<Integer> query = em.createQuery(""
                    + "SELECT t.itemInteger1 FROM EntityTbl01 t ORDER BY ?1", Integer.class);
            query.setParameter(1, 1);

            List<Integer> dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(4, dto01.size());

            query = em.createQuery(""
                    + "SELECT t.itemInteger1 FROM EntityTbl01 t ORDER BY ?1 ASC", Integer.class);
            query.setParameter(1, 1);

            dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(4, dto01.size());

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
            assertEquals(4, dto01.size());
        } finally {
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
            TypedQuery<Integer> query = em.createQuery(""
                    + "SELECT t.itemInteger1 FROM EntityTbl01 t WHERE t.itemString2 = ?1 ORDER BY ?2", Integer.class);
            query.setParameter(1, "B");
            query.setParameter(2, 1);

            List<Integer> dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(3, dto01.size());
            assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = ?) ORDER BY 1", _sql.remove(0));
            } else {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = ?) ORDER BY ?", _sql.remove(0));
            }

            query = em.createQuery(""
                    + "SELECT t.itemInteger1 FROM EntityTbl01 t WHERE t.itemString2 = ?1 ORDER BY ?2 ASC", Integer.class);
            query.setParameter(1, "B");
            query.setParameter(2, 1);

            dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(3, dto01.size());
            assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = ?) ORDER BY 1 ASC", _sql.remove(0));
            } else {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = ?) ORDER BY ? ASC", _sql.remove(0));
            }

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
            assertEquals(3, dto01.size());
            assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = ?) ORDER BY 1 ASC", _sql.remove(0));
            } else {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = ?) ORDER BY ? ASC", _sql.remove(0));
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
    public void testQueryOrderByMix1() {
        if (emf == null)
            return;

        if(!POPULATED) 
            populate();

        Platform platform = getPlatform(emf);
        EntityManager em = emf.createEntityManager();

        try {
            TypedQuery<Integer> query = em.createQuery(""
                    + "SELECT t.itemInteger1 FROM EntityTbl01 t WHERE t.itemString2 = ?1 ORDER BY 1", Integer.class);
            query.setParameter(1, "B");

            List<Integer> dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(3, dto01.size());
            assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = ?) ORDER BY 1", _sql.remove(0));
            } else {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = ?) ORDER BY ?", _sql.remove(0));
            }

            query = em.createQuery(""
                    + "SELECT t.itemInteger1 FROM EntityTbl01 t WHERE t.itemString2 = ?1 ORDER BY 1 ASC", Integer.class);
            query.setParameter(1, "B");

            dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(3, dto01.size());
            assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = 'B') ORDER BY 1 ASC", _sql.remove(0));
            } else {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = ?) ORDER BY ? ASC", _sql.remove(0));
            }

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
            assertEquals(3, dto01.size());
            assertEquals(1, _sql.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = 'B') ORDER BY 1 ASC", _sql.remove(0));
            } else {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = ?) ORDER BY ? ASC", _sql.remove(0));
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
    public void testQueryOrderByMix2() {
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
            TypedQuery<Integer> query = em.createQuery(""
                    + "SELECT t.itemInteger1 FROM EntityTbl01 t WHERE t.itemString2 = ?1 ORDER BY 1", Integer.class);
            query.setParameter(1, "B");

            List<Integer> dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(3, dto01.size());
            assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = 'B') ORDER BY 1", _sql2.remove(0));
            } else {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = ?) ORDER BY ?", _sql2.remove(0));
            }

            query = em.createQuery(""
                    + "SELECT t.itemInteger1 FROM EntityTbl01 t WHERE t.itemString2 = ?1 ORDER BY 1 ASC", Integer.class);
            query.setParameter(1, "B");

            dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(3, dto01.size());
            assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = 'B') ORDER BY 1 ASC", _sql2.remove(0));
            } else {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = ?) ORDER BY ? ASC", _sql2.remove(0));
            }

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
            assertEquals(3, dto01.size());
            assertEquals(1, _sql2.size());
            if(platform.isDB2Z() || platform.isDB2() || platform.isDerby()) {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = 'B') ORDER BY 1 ASC", _sql2.remove(0));
            } else {
                assertEquals("SELECT ITEM_INTEGER1 FROM SIMPLE_TBL01 WHERE (ITEM_STRING2 = ?) ORDER BY ? ASC", _sql2.remove(0));
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

    private DatabasePlatform getPlatform(EntityManagerFactory emf) {
        return ((EntityManagerFactoryImpl)emf).getServerSession().getPlatform();
    }
}