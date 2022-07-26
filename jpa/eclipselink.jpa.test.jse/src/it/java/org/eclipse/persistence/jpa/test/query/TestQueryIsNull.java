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
//     IBM - Bug 579327: ParameterExpression in CriteriaBuilder.isNull() doesn't register
package org.eclipse.persistence.jpa.test.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.ParameterExpression;
import jakarta.persistence.criteria.Root;

import org.eclipse.persistence.internal.databaseaccess.Platform;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.jpa.test.query.model.EntityTbl01;
import org.eclipse.persistence.jpa.test.query.model.EntityTbl01_;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestQueryIsNull {
    @Emf(createTables = DDLGen.DROP_CREATE, classes = { EntityTbl01.class }, 
            properties = { @Property(name="eclipselink.logging.level", value="FINE")})
    private EntityManagerFactory emf;

    private static boolean POPULATED = false;

    @Test
    public void testQueryIsNullLiterals1() {
        if (emf == null)
            return;

        if(!POPULATED) 
            populate();

        EntityManager em = emf.createEntityManager();

        try {
            TypedQuery<String> query = em.createQuery(""
                    + "SELECT t.itemString1 FROM EntityTbl01 t "
                        + "WHERE 'HELLO' IS NULL", String.class);

            List<String> dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(0, dto01.size());

            // equivalent CriteriaBuilder
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<String> cquery = cb.createQuery(String.class);
            Root<EntityTbl01> root = cquery.from(EntityTbl01.class);
            cquery.multiselect(root.get(EntityTbl01_.itemString1));

            cquery.where(cb.isNull(cb.literal("HELLO")));

            query = em.createQuery(cquery);
            dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(0, dto01.size());

            // equivalent, alternative CriteriaBuilder
            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<String> cquery2 = cb2.createQuery(String.class);
            Root<EntityTbl01> root2 = cquery2.from(EntityTbl01.class);
            cquery2.multiselect(root2.get(EntityTbl01_.itemString1));

            cquery2.where(cb2.literal("HELLO").isNull());

            query = em.createQuery(cquery2);
            dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(0, dto01.size());
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }
    }

    @Test
    public void testQueryIsNullLiterals2() {
        if (emf == null)
            return;

        if(!POPULATED) 
            populate();

        Platform platform = getPlatform(emf);
        EntityManager em = emf.createEntityManager();

        // DB2 on z and Derby does not support null literal values in 'IS NULL' function
        // ie. "... WHERE (NULL IS NULL)"
        if(platform.isDB2Z() || platform.isDerby()) {
            return;
        }

        try {
            TypedQuery<String> query = em.createQuery(""
                    + "SELECT t.itemString1 FROM EntityTbl01 t "
                        + "WHERE NULL IS NULL", String.class);

            List<String> dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(4, dto01.size());

            // equivalent CriteriaBuilder
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<String> cquery = cb.createQuery(String.class);
            Root<EntityTbl01> root = cquery.from(EntityTbl01.class);
            cquery.multiselect(root.get(EntityTbl01_.itemString1));

            cquery.where(cb.isNull(cb.nullLiteral(String.class)));

            query = em.createQuery(cquery);
            dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(4, dto01.size());

            // equivalent, alternative CriteriaBuilder
            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<String> cquery2 = cb2.createQuery(String.class);
            Root<EntityTbl01> root2 = cquery2.from(EntityTbl01.class);
            cquery2.multiselect(root2.get(EntityTbl01_.itemString1));

            cquery2.where(cb2.nullLiteral(String.class).isNull());

            query = em.createQuery(cquery2);
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
    public void testQueryIsNotNullLiterals1() {
        if (emf == null)
            return;

        if(!POPULATED) 
            populate();

        EntityManager em = emf.createEntityManager();

        try {
            TypedQuery<String> query = em.createQuery(""
                    + "SELECT t.itemString1 FROM EntityTbl01 t "
                        + "WHERE 'HELLO' IS NOT NULL", String.class);

            List<String> dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(4, dto01.size());

            // equivalent CriteriaBuilder
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<String> cquery = cb.createQuery(String.class);
            Root<EntityTbl01> root = cquery.from(EntityTbl01.class);
            cquery.multiselect(root.get(EntityTbl01_.itemString1));

            cquery.where(cb.isNotNull(cb.literal("HELLO")));

            query = em.createQuery(cquery);
            dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(4, dto01.size());

            // equivalent, alternative CriteriaBuilder
            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<String> cquery2 = cb2.createQuery(String.class);
            Root<EntityTbl01> root2 = cquery2.from(EntityTbl01.class);
            cquery2.multiselect(root2.get(EntityTbl01_.itemString1));

            cquery2.where(cb2.literal("HELLO").isNotNull());

            query = em.createQuery(cquery2);
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
    public void testQueryIsNotNullLiterals2() {
        if (emf == null)
            return;

        if(!POPULATED) 
            populate();

        Platform platform = getPlatform(emf);
        EntityManager em = emf.createEntityManager();

        // DB2 on z and Derby does not support null literal values in 'IS NOT NULL' function
        // ie. "... WHERE (NULL IS NOT NULL)"
        if(platform.isDB2Z() || platform.isDerby()) {
            return;
        }

        try {
            TypedQuery<String> query = em.createQuery(""
                    + "SELECT t.itemString1 FROM EntityTbl01 t "
                        + "WHERE NULL IS NOT NULL", String.class);

            List<String> dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(0, dto01.size());

            // equivalent CriteriaBuilder
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<String> cquery = cb.createQuery(String.class);
            Root<EntityTbl01> root = cquery.from(EntityTbl01.class);
            cquery.multiselect(root.get(EntityTbl01_.itemString1));

            cquery.where(cb.isNotNull(cb.nullLiteral(String.class)));

            query = em.createQuery(cquery);
            dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(0, dto01.size());

            // equivalent, alternative CriteriaBuilder
            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<String> cquery2 = cb2.createQuery(String.class);
            Root<EntityTbl01> root2 = cquery2.from(EntityTbl01.class);
            cquery2.multiselect(root2.get(EntityTbl01_.itemString1));

            cquery2.where(cb2.nullLiteral(String.class).isNotNull());

            query = em.createQuery(cquery2);
            dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(0, dto01.size());
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }
    }

    @Test
    public void testQueryIsNullParameters1() {
        if (emf == null)
            return;

        if(!POPULATED) 
            populate();

        EntityManager em = emf.createEntityManager();

        try {
            TypedQuery<String> query = em.createQuery(""
                    + "SELECT t.itemString1 FROM EntityTbl01 t "
                        + "WHERE ?1 IS NULL", String.class);
            query.setParameter(1, "HELLO");

            List<String> dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(0, dto01.size());

            // equivalent CriteriaBuilder
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<String> cquery = cb.createQuery(String.class);
            Root<EntityTbl01> root = cquery.from(EntityTbl01.class);
            cquery.multiselect(root.get(EntityTbl01_.itemString1));

            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            cquery.where(cb.isNull(strParam1));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "HELLO");
            dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(0, dto01.size());

            // equivalent, alternative CriteriaBuilder
            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<String> cquery2 = cb2.createQuery(String.class);
            Root<EntityTbl01> root2 = cquery2.from(EntityTbl01.class);
            cquery2.multiselect(root2.get(EntityTbl01_.itemString1));

            ParameterExpression<String> strParam2 = cb.parameter(String.class);
            cquery2.where(strParam2.isNull());

            query = em.createQuery(cquery2);
            query.setParameter(strParam2, "HELLO");
            dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(0, dto01.size());
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }
    }

    @Test
    public void testQueryIsNullParameters2() {
        if (emf == null)
            return;

        if(!POPULATED) 
            populate();

        Platform platform = getPlatform(emf);
        EntityManager em = emf.createEntityManager();

        // DB2 on z and Derby does not support null literal values in 'IS NULL' function
        // ie. "... WHERE (NULL IS NULL)"
        if(platform.isDB2Z() || platform.isDerby()) {
            return;
        }

        try {
            TypedQuery<String> query = em.createQuery(""
                    + "SELECT t.itemString1 FROM EntityTbl01 t "
                        + "WHERE ?1 IS NULL", String.class);
            query.setParameter(1, null);

            List<String> dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(4, dto01.size());

            // equivalent CriteriaBuilder
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<String> cquery = cb.createQuery(String.class);
            Root<EntityTbl01> root = cquery.from(EntityTbl01.class);
            cquery.multiselect(root.get(EntityTbl01_.itemString1));

            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            cquery.where(cb.isNull(strParam1));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, null);
            dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(4, dto01.size());

            // equivalent, alternative CriteriaBuilder
            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<String> cquery2 = cb2.createQuery(String.class);
            Root<EntityTbl01> root2 = cquery2.from(EntityTbl01.class);
            cquery2.multiselect(root2.get(EntityTbl01_.itemString1));

            ParameterExpression<String> strParam2 = cb.parameter(String.class);
            cquery2.where(strParam2.isNull());

            query = em.createQuery(cquery2);
            query.setParameter(strParam2, null);
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
    public void testQueryIsNotNullParameters1() {
        if (emf == null)
            return;

        if(!POPULATED) 
            populate();

        EntityManager em = emf.createEntityManager();

        try {
            TypedQuery<String> query = em.createQuery(""
                    + "SELECT t.itemString1 FROM EntityTbl01 t "
                        + "WHERE ?1 IS NOT NULL", String.class);
            query.setParameter(1, "HELLO");

            List<String> dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(4, dto01.size());

            // equivalent CriteriaBuilder
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<String> cquery = cb.createQuery(String.class);
            Root<EntityTbl01> root = cquery.from(EntityTbl01.class);
            cquery.multiselect(root.get(EntityTbl01_.itemString1));

            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            cquery.where(cb.isNotNull(strParam1));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, "HELLO");
            dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(4, dto01.size());

            // equivalent, alternative CriteriaBuilder
            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<String> cquery2 = cb2.createQuery(String.class);
            Root<EntityTbl01> root2 = cquery2.from(EntityTbl01.class);
            cquery2.multiselect(root2.get(EntityTbl01_.itemString1));

            ParameterExpression<String> strParam2 = cb.parameter(String.class);
            cquery2.where(strParam2.isNotNull());

            query = em.createQuery(cquery2);
            query.setParameter(strParam2, "HELLO");
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
    public void testQueryIsNotNullParameters2() {
        if (emf == null)
            return;

        if(!POPULATED) 
            populate();

        Platform platform = getPlatform(emf);
        EntityManager em = emf.createEntityManager();

        // DB2 on z and Derby does not support null literal values in 'IS NOT NULL' function
        // ie. "... WHERE (NULL IS NOT NULL)"
        if(platform.isDB2Z() || platform.isDerby()) {
            return;
        }

        try {
            TypedQuery<String> query = em.createQuery(""
                    + "SELECT t.itemString1 FROM EntityTbl01 t "
                        + "WHERE ?1 IS NOT NULL", String.class);
            query.setParameter(1, null);

            List<String> dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(0, dto01.size());

            // equivalent CriteriaBuilder
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<String> cquery = cb.createQuery(String.class);
            Root<EntityTbl01> root = cquery.from(EntityTbl01.class);
            cquery.multiselect(root.get(EntityTbl01_.itemString1));

            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            cquery.where(cb.isNotNull(strParam1));

            query = em.createQuery(cquery);
            query.setParameter(strParam1, null);
            dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(0, dto01.size());

            // equivalent, alternative CriteriaBuilder
            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<String> cquery2 = cb2.createQuery(String.class);
            Root<EntityTbl01> root2 = cquery2.from(EntityTbl01.class);
            cquery2.multiselect(root2.get(EntityTbl01_.itemString1));

            ParameterExpression<String> strParam2 = cb.parameter(String.class);
            cquery2.where(strParam2.isNotNull());

            query = em.createQuery(cquery2);
            query.setParameter(strParam2, null);
            dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(0, dto01.size());
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
            tbl1.setKeyString("Key50");
            tbl1.setItemString1("A");
            tbl1.setItemString2(null);
            tbl1.setItemString3("C");
            tbl1.setItemString4("D");
            tbl1.setItemInteger1(3);
            em.persist(tbl1);

            EntityTbl01 tbl2 = new EntityTbl01();
            tbl2.setKeyString("Key51");
            tbl2.setItemString1("A");
            tbl2.setItemString2("B");
            tbl2.setItemString3("C");
            tbl2.setItemString4(null);
            tbl2.setItemInteger1(4);
            em.persist(tbl2);

            EntityTbl01 tbl3 = new EntityTbl01();
            tbl3.setKeyString("Key52");
            tbl3.setItemString1(null);
            tbl3.setItemString2("B");
            tbl3.setItemString3("C");
            tbl3.setItemString4("D");
            tbl3.setItemInteger1(3);
            em.persist(tbl3);

            EntityTbl01 tbl4 = new EntityTbl01();
            tbl4.setKeyString("Key53");
            tbl4.setItemString1("A");
            tbl4.setItemString2("B");
            tbl4.setItemString3("C");
            tbl4.setItemString4(null);
            tbl4.setItemInteger1(4);
            em.persist(tbl4);

            em.getTransaction().commit();

            POPULATED = true;
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