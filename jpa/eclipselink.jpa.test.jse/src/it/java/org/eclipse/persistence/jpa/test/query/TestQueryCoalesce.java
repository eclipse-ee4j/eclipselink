/*
 * Copyright (c) 2021 IBM Corporation. All rights reserved.
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
//     IBM - Bug 573361: Add support for Parameters in CriteriaBuilder in Case/Coalesce expressions
package org.eclipse.persistence.jpa.test.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.ParameterExpression;
import jakarta.persistence.criteria.Root;

import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.jpa.test.query.model.EntityTbl01;
import org.eclipse.persistence.jpa.test.query.model.EntityTbl01_;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestQueryCoalesce {
    @Emf(createTables = DDLGen.DROP_CREATE, classes = { EntityTbl01.class }, 
            properties = { @Property(name="eclipselink.logging.level", value="FINE")})
    private EntityManagerFactory emf;

    private static boolean POPULATED = false;

    @Test
    public void testQueryCoalesceLiterals1() {
        if (emf == null)
            return;

        if(!POPULATED) 
            populate();

        EntityManager em = emf.createEntityManager();

        try {
            TypedQuery<EntityTbl01> query = em.createQuery("SELECT t FROM EntityTbl01 t "
                        + "WHERE t.itemString2 = "
                            + "COALESCE (t.itemString1, 'Sample')", EntityTbl01.class);

            List<EntityTbl01> dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(0, dto01.size());

            TypedQuery<String> query2 = em.createQuery("SELECT COALESCE (t.itemString2, 'Sample') FROM EntityTbl01 t ORDER BY t.itemInteger1 ASC", String.class);

            List<String> dto02 = query2.getResultList();
            assertNotNull(dto02);
            assertEquals(2, dto02.size());

            assertEquals("Sample", dto02.get(0));
            assertEquals("B", dto02.get(1));

            // test 1 equivalent CriteriaBuilder
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<EntityTbl01> cquery = cb.createQuery(EntityTbl01.class);
            Root<EntityTbl01> root = cquery.from(EntityTbl01.class);
            cquery.select(root);

            Expression<String> coalesce = cb.coalesce(root.get(EntityTbl01_.itemString1), "Sample");
            cquery.where(cb.equal(root.get(EntityTbl01_.itemString2), coalesce));

            query = em.createQuery(cquery);
            dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(0, dto01.size());

            // test 2 equivalent CriteriaBuilder
            cb = em.getCriteriaBuilder();
            CriteriaQuery<String> cquery2 = cb.createQuery(String.class);
            root = cquery2.from(EntityTbl01.class);
            Expression<String> coalesce2 = cb.coalesce(root.get(EntityTbl01_.itemString2), "Sample");
            cquery2.multiselect(coalesce2);

            cquery2.orderBy(cb.asc(root.get(EntityTbl01_.itemInteger1)));

            query2 = em.createQuery(cquery2);
            dto02 = query2.getResultList();
            assertNotNull(dto02);
            assertEquals(2, dto02.size());

            assertEquals("Sample", dto02.get(0));
            assertEquals("B", dto02.get(1));
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }
    }

    @Test
    public void testQueryCaseParameters1() {
        if (emf == null)
            return;

        if(!POPULATED) 
            populate();

        EntityManager em = emf.createEntityManager();

        try {
            TypedQuery<EntityTbl01> query = em.createQuery("SELECT t FROM EntityTbl01 t "
                        + "WHERE t.itemString2 = "
                            + "COALESCE (t.itemString1, ?1)", EntityTbl01.class);
            query.setParameter(1, "Sample");

            List<EntityTbl01> dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(0, dto01.size());

            TypedQuery<String> query2 = em.createQuery("SELECT COALESCE (t.itemString2, ?1) FROM EntityTbl01 t ORDER BY t.itemInteger1 ASC", String.class);
            query2.setParameter(1, "Sample");

            List<String> dto02 = query2.getResultList();
            assertNotNull(dto02);
            assertEquals(2, dto02.size());

            assertEquals("Sample", dto02.get(0));
            assertEquals("B", dto02.get(1));

            // test 1 equivalent CriteriaBuilder
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<EntityTbl01> cquery = cb.createQuery(EntityTbl01.class);
            Root<EntityTbl01> root = cquery.from(EntityTbl01.class);
            cquery.select(root);

            ParameterExpression<String> checkParam1 = cb.parameter(String.class);
            Expression<String> coalesce = cb.coalesce(root.get(EntityTbl01_.itemString1), checkParam1);
            cquery.where(cb.equal(root.get(EntityTbl01_.itemString2), coalesce));

            query = em.createQuery(cquery);
            query.setParameter(checkParam1, "Sample");
            dto01 = query.getResultList();
            assertNotNull(dto01);
            assertEquals(0, dto01.size());

            // test 2 equivalent CriteriaBuilder
            cb = em.getCriteriaBuilder();
            CriteriaQuery<String> cquery2 = cb.createQuery(String.class);
            root = cquery2.from(EntityTbl01.class);
            ParameterExpression<String> checkParam2 = cb.parameter(String.class);
            Expression<String> coalesce2 = cb.coalesce(root.get(EntityTbl01_.itemString2), checkParam2);
            cquery2.multiselect(coalesce2);

            cquery2.orderBy(cb.asc(root.get(EntityTbl01_.itemInteger1)));

            query2 = em.createQuery(cquery2);
            query2.setParameter(checkParam2, "Sample");
            dto02 = query2.getResultList();
            assertNotNull(dto02);
            assertEquals(2, dto02.size());

            assertEquals("Sample", dto02.get(0));
            assertEquals("B", dto02.get(1));
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
            tbl1.setKeyString("Key03");
            tbl1.setItemString1("A");
            tbl1.setItemString2(null);
            tbl1.setItemString3("C");
            tbl1.setItemString4("D");
            tbl1.setItemInteger1(3);
            em.persist(tbl1);

            EntityTbl01 tbl2 = new EntityTbl01();
            tbl2.setKeyString("Key04");
            tbl2.setItemString1("A");
            tbl2.setItemString2("B");
            tbl2.setItemString3("C");
            tbl2.setItemString4(null);
            tbl2.setItemInteger1(4);
            em.persist(tbl2);

            em.getTransaction().commit();

            POPULATED = true;
        } finally {
            if(em.isOpen()) {
                em.close();
            }
        }
    }
} 