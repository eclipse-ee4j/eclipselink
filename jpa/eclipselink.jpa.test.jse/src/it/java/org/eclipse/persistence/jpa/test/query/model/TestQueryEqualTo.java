/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
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
//     10/25/2023: Tomas Kraus
//       - New Jakarta Persistence 3.2 Features
package org.eclipse.persistence.jpa.test.query.model;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.ParameterExpression;
import jakarta.persistence.criteria.Root;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(EmfRunner.class)
public class TestQueryEqualTo {

    @Emf(createTables = DDLGen.DROP_CREATE, classes = { EntityTbl01.class },
         properties = { @Property(name="eclipselink.logging.level", value="FINE")})
    private EntityManagerFactory emf;

    private static boolean POPULATED = false;

    @Test
    public void testQueryEqualToExpression() {
        if (!POPULATED)
            populate();

        EntityManager em = emf.createEntityManager();

        try {
            TypedQuery<String> query = em.createQuery(
                    "SELECT t.KeyString FROM EntityTbl01 t "
                           + "WHERE t.KeyString = ?1", String.class);
            query.setParameter(1, "EqualTo_1");
            List<String> dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(1, dto01.size());

            // equivalent CriteriaBuilder
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<String> cquery = cb.createQuery(String.class);
            Root<EntityTbl01> root = cquery.from(EntityTbl01.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            cquery.multiselect(root.get(EntityTbl01_.KeyString));
            cquery.where(root.get(EntityTbl01_.KeyString).equalTo(strParam1));
            query = em.createQuery(cquery);
            query.setParameter(strParam1, "EqualTo_1");
            dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(1, dto01.size());
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }
    }

    @Test
    public void testQueryEqualToObject() {
        if (!POPULATED)
            populate();

        EntityManager em = emf.createEntityManager();

        try {
            TypedQuery<String> query = em.createQuery(
                    "SELECT t.KeyString FROM EntityTbl01 t "
                            + "WHERE t.KeyString = 'EqualTo_2'", String.class);
            List<String> dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(1, dto01.size());

            // equivalent CriteriaBuilder
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<String> cquery = cb.createQuery(String.class);
            Root<EntityTbl01> root = cquery.from(EntityTbl01.class);
            cquery.multiselect(root.get(EntityTbl01_.KeyString));
            cquery.where(root.get(EntityTbl01_.KeyString).equalTo("EqualTo_2"));
            query = em.createQuery(cquery);
            dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(1, dto01.size());
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }
    }

    @Test
    public void testQueryNotEqualToExpression() {
        if (!POPULATED)
            populate();

        EntityManager em = emf.createEntityManager();

        try {
            TypedQuery<String> query = em.createQuery(
                    "SELECT t.KeyString FROM EntityTbl01 t "
                            + "WHERE t.KeyString <> ?1", String.class);
            query.setParameter(1, "EqualTo_3");
            List<String> dto01 = query.getResultList();
            assertNotNull(dto01);
            assertTrue(dto01.size() > 1);

            // equivalent CriteriaBuilder
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<String> cquery = cb.createQuery(String.class);
            Root<EntityTbl01> root = cquery.from(EntityTbl01.class);
            ParameterExpression<String> strParam1 = cb.parameter(String.class);
            cquery.multiselect(root.get(EntityTbl01_.KeyString));
            cquery.where(root.get(EntityTbl01_.KeyString).notEqualTo(strParam1));
            query = em.createQuery(cquery);
            query.setParameter(strParam1, "EqualTo_3");
            dto01 = query.getResultList();
            assertNotNull(dto01);
            assertTrue(dto01.size() > 1);
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }
    }

    @Test
    public void testQueryNotEqualToObject() {
        if (!POPULATED)
            populate();

        EntityManager em = emf.createEntityManager();

        try {
            TypedQuery<String> query = em.createQuery(
                    "SELECT t.KeyString FROM EntityTbl01 t "
                            + "WHERE t.KeyString <> 'EqualTo_4'", String.class);
            List<String> dto01 = query.getResultList();
            assertNotNull(dto01);
            assertTrue(dto01.size() > 1);

            // equivalent CriteriaBuilder
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<String> cquery = cb.createQuery(String.class);
            Root<EntityTbl01> root = cquery.from(EntityTbl01.class);
            cquery.multiselect(root.get(EntityTbl01_.KeyString));
            cquery.where(root.get(EntityTbl01_.KeyString).notEqualTo("EqualTo_4"));
            query = em.createQuery(cquery);
            dto01 = query.getResultList();
            assertNotNull(dto01);
            assertTrue(dto01.size() > 1);
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
            tbl1.setKeyString("EqualTo_1");
            tbl1.setItemString1("A");
            tbl1.setItemString2(null);
            tbl1.setItemString3("C");
            tbl1.setItemString4("D");
            tbl1.setItemInteger1(45);
            em.persist(tbl1);

            EntityTbl01 tbl2 = new EntityTbl01();
            tbl2.setKeyString("EqualTo_2");
            tbl2.setItemString1("A");
            tbl2.setItemString2("B");
            tbl2.setItemString3("C");
            tbl2.setItemString4(null);
            tbl2.setItemInteger1(83);
            em.persist(tbl2);

            EntityTbl01 tbl3 = new EntityTbl01();
            tbl3.setKeyString("EqualTo_3");
            tbl3.setItemString1(null);
            tbl3.setItemString2("B");
            tbl3.setItemString3("C");
            tbl3.setItemString4("D");
            tbl3.setItemInteger1(17);
            em.persist(tbl3);

            EntityTbl01 tbl4 = new EntityTbl01();
            tbl4.setKeyString("EqualTo_4");
            tbl4.setItemString1("A");
            tbl4.setItemString2("B");
            tbl4.setItemString3("C");
            tbl4.setItemString4(null);
            tbl4.setItemInteger1(29);
            em.persist(tbl4);

            em.getTransaction().commit();

            POPULATED = true;
        } finally {
            if(em.isOpen()) {
                em.close();
            }
        }
    }


}
