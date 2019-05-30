/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2018 IBM Corporation, Oracle, and/or affiliates. All rights reserved.
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
//     IBM - Bug 537795: CASE THEN and ELSE scalar expression Constants should not be casted to CASE operand type
package org.eclipse.persistence.jpa.test.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.jpa.test.query.model.Dto01;
import org.eclipse.persistence.jpa.test.query.model.EntityTbl01;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestQueryCase {
    @Emf(createTables = DDLGen.DROP_CREATE, classes = {EntityTbl01.class},
            properties = {@Property(name = "eclipselink.logging.level", value = "FINE")})
    private EntityManagerFactory emf;
    private EntityManager em;

    private static boolean POPULATED = false;

    @Test
    public void testQueryCase1() {
        TypedQuery<EntityTbl01> query = em.createQuery(""
                + "SELECT t FROM EntityTbl01 t "
                + "WHERE t.itemString1 = ( "
                + "CASE t.itemInteger1 "
                + "WHEN 1000 THEN '047010' "
                + "WHEN 100 THEN '023010' "
                + "ELSE '033020' "
                + "END )", EntityTbl01.class);

        List<EntityTbl01> dto01 = query.getResultList();
        assertNotNull(dto01);
        assertEquals(0, dto01.size());

        query = em.createQuery(""
                + "SELECT t FROM EntityTbl01 t "
                + "WHERE t.itemString1 = ( "
                + "CASE t.itemInteger1 "
                + "WHEN 1 THEN 'A' "
                + "WHEN 100 THEN 'B' "
                + "ELSE 'C' "
                + "END )", EntityTbl01.class);
        dto01 = query.getResultList();
        assertNotNull(dto01);
        assertEquals(1, dto01.size());

        assertEquals("A", dto01.get(0).getItemString1());
        assertEquals("B", dto01.get(0).getItemString2());
        assertEquals("C", dto01.get(0).getItemString3());
        assertEquals("D", dto01.get(0).getItemString4());
        assertEquals(new Integer(1), dto01.get(0).getItemInteger1());
    }

    @Test
    public void testQueryCase2() {
        TypedQuery<EntityTbl01> query = em.createQuery(""
                + "SELECT t FROM EntityTbl01 t "
                + "WHERE t.itemString1 = ( "
                + "CASE "
                + "WHEN t.itemInteger1 = 1000 THEN '047010' "
                + "WHEN t.itemInteger1 = 100 THEN '023010' "
                + "ELSE '033020' "
                + "END )", EntityTbl01.class);

        List<EntityTbl01> dto01 = query.getResultList();
        assertNotNull(dto01);
        assertEquals(0, dto01.size());

        query = em.createQuery(""
                + "SELECT t FROM EntityTbl01 t "
                + "WHERE t.itemString1 = ( "
                + "CASE "
                + "WHEN t.itemInteger1 = 1 THEN 'A' "
                + "WHEN t.itemInteger1 = 100 THEN 'B' "
                + "ELSE 'C' "
                + "END )", EntityTbl01.class);

        dto01 = query.getResultList();
        assertNotNull(dto01);
        assertEquals(1, dto01.size());

        assertEquals("A", dto01.get(0).getItemString1());
        assertEquals("B", dto01.get(0).getItemString2());
        assertEquals("C", dto01.get(0).getItemString3());
        assertEquals("D", dto01.get(0).getItemString4());
        assertEquals(new Integer(1), dto01.get(0).getItemInteger1());

        query = em.createQuery(""
                + "SELECT t FROM EntityTbl01 t "
                + "WHERE t.itemString1 = ( "
                + "CASE "
                + "WHEN t.itemInteger1 = 1 AND t.KeyString = 'Key01' THEN 'A' "
                + "WHEN t.itemInteger1 = 100 THEN 'B' "
                + "ELSE 'C' "
                + "END )", EntityTbl01.class);
    }

    @Test
    public void testQueryCase3() {
        TypedQuery<Dto01> query = em.createQuery(""
                + "SELECT new org.eclipse.persistence.jpa.test.query.model.Dto01("
                + "t.itemString1, "               // String
                + "CASE t.itemString2 "           // String
                + "WHEN 'J' THEN 'Japan' "
                + "ELSE 'Other' "
                + "END  "
                + ", "
                + "SUM("      // Returns Long (4.8.5)
                + "CASE "
                + "WHEN t.itemString3 = 'C' "
                + "THEN 1 ELSE 0 "
                + "END"
                + ") "
                + ", "
                + "SUM("      // Returns Long (4.8.5)
                + "CASE "
                + "WHEN t.itemString4 = 'D' "
                + "THEN 1 ELSE 0 "
                + "END"
                + ") "
                + ") "
                + "FROM EntityTbl01 t "
                + "GROUP BY t.itemString1, t.itemString2", Dto01.class);

        List<Dto01> dto01 = query.getResultList();
        assertNotNull(dto01);
        assertEquals(1, dto01.size());
        assertEquals("A", dto01.get(0).getStr1());
        assertEquals("Other", dto01.get(0).getStr2());
        assertNull(dto01.get(0).getStr3());
        assertNull(dto01.get(0).getStr4());
        assertEquals(new Integer(2), dto01.get(0).getInteger1());
        assertEquals(new Integer(2), dto01.get(0).getInteger2());
    }

    @Test
    public void testQueryCase4() {
        TypedQuery<Integer> query = em.createQuery(""
                + "SELECT ("
                + "CASE t.itemString2 "
                + "WHEN 'A' THEN 42 "
                + "WHEN 'B' THEN 100 "
                + "ELSE 0 "
                + "END "
                + ") "
                + "FROM EntityTbl01 t", Integer.class);

        List<Integer> intList = query.getResultList();
        assertNotNull(intList);
        assertEquals(2, intList.size());
        assertEquals(new Integer(100), intList.get(0));
        assertEquals(new Integer(100), intList.get(1));
    }

    private void populate() {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();

            em.getTransaction().begin();

            EntityTbl01 tbl1 = new EntityTbl01();
            tbl1.setKeyString("Key01");
            tbl1.setItemString1("A");
            tbl1.setItemString2("B");
            tbl1.setItemString3("C");
            tbl1.setItemString4("D");
            tbl1.setItemInteger1(1);
            em.persist(tbl1);

            EntityTbl01 tbl2 = new EntityTbl01();
            tbl2.setKeyString("Key02");
            tbl2.setItemString1("A");
            tbl2.setItemString2("B");
            tbl2.setItemString3("C");
            tbl2.setItemString4("D");
            tbl2.setItemInteger1(2);
            em.persist(tbl2);
            em.getTransaction().commit();

        } catch (Exception e) {
            fail(e.getLocalizedMessage());
        } finally {
            if (em != null) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                em.close();
            }
        }

        POPULATED = true;
    }

    @Before
    public void setUp() throws Exception {
        if (emf == null)
            return;
        if (!POPULATED)
            populate();
        em = emf.createEntityManager();
        em.getTransaction().begin();
    }

    @After
    public void tearDown() throws Exception {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
        if (em.isOpen()) {
            em.close();
        }
    }

}
