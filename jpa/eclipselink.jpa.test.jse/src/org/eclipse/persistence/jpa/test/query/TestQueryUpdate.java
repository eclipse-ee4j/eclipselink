/*
 * Copyright (c) 2021 Oracle and/or its affiliates. All rights reserved.
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
//     IBM - Bug 574548: Add support for parameters with CriteriaBuilder update query
package org.eclipse.persistence.jpa.test.query;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;

import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.jpa.test.query.model.EntityTbl01;
import org.eclipse.persistence.jpa.test.query.model.EntityTbl01_;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestQueryUpdate {
    @Emf(createTables = DDLGen.DROP_CREATE, classes = { EntityTbl01.class }, 
            properties = { @Property(name="eclipselink.logging.level", value="FINE")})
    private EntityManagerFactory emf;

    private static boolean POPULATED = false;

    @Test
    public void testQueryUpdateLiterals1() {
        if (emf == null)
            return;

        if(!POPULATED) 
            populate();

        EntityManager em = emf.createEntityManager();

        try {
            Query query = em.createQuery("UPDATE EntityTbl01 t SET t.itemInteger1 = 9 WHERE t.itemString1 = 'HELLO'");

            em.getTransaction().begin();
            query.executeUpdate();
            em.getTransaction().commit();

            // equivalent CriteriaBuilder
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaUpdate<EntityTbl01> cquery = cb.createCriteriaUpdate(EntityTbl01.class);
            Root<EntityTbl01> root = cquery.from(EntityTbl01.class);

            cquery.set(root.get(EntityTbl01_.itemInteger1), 9);
            cquery.where(cb.equal(root.get(EntityTbl01_.itemString1), "HELLO"));

            query = em.createQuery(cquery);

            em.getTransaction().begin();
            query.executeUpdate();
            em.getTransaction().commit();

            // equivalent CriteriaBuilder 2
            cb = em.getCriteriaBuilder();
            cquery = cb.createCriteriaUpdate(EntityTbl01.class);
            root = cquery.from(EntityTbl01.class);

            cquery.set("itemInteger1", 9);
            cquery.where(cb.equal(root.get("itemString1"), "HELLO"));

            query = em.createQuery(cquery);

            em.getTransaction().begin();
            query.executeUpdate();
            em.getTransaction().commit();
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
    public void testQueryUpdateParameters1() {
        if (emf == null)
            return;

        if(!POPULATED) 
            populate();

        EntityManager em = emf.createEntityManager();

        try {
            Query query = em.createQuery("UPDATE EntityTbl01 t SET t.itemInteger1 = ?1 WHERE t.itemString1 = ?2");
            query.setParameter(1, 9);
            query.setParameter(2, "HELLO");

            em.getTransaction().begin();
            query.executeUpdate();
            em.getTransaction().commit();

            // equivalent CriteriaBuilder
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaUpdate<EntityTbl01> cquery = cb.createCriteriaUpdate(EntityTbl01.class);
            Root<EntityTbl01> root = cquery.from(EntityTbl01.class);

            ParameterExpression<Integer> intValue = cb.parameter(Integer.class);
            ParameterExpression<String> strValue = cb.parameter(String.class);
            cquery.set(root.get(EntityTbl01_.itemInteger1), intValue);
            cquery.where(cb.equal(root.get(EntityTbl01_.itemString1), strValue));

            query = em.createQuery(cquery);
            query.setParameter(intValue, 9);
            query.setParameter(strValue, "HELLO");
            em.getTransaction().begin();
            query.executeUpdate();
            em.getTransaction().commit();

            // equivalent CriteriaBuilder 2
            cb = em.getCriteriaBuilder();
            cquery = cb.createCriteriaUpdate(EntityTbl01.class);
            root = cquery.from(EntityTbl01.class);

            intValue = cb.parameter(Integer.class);
            strValue = cb.parameter(String.class);
            cquery.set("itemInteger1", intValue);
            cquery.where(cb.equal(root.get("itemString1"), strValue));

            query = em.createQuery(cquery);
            query.setParameter(intValue, 9);
            query.setParameter(strValue, "HELLO");
            em.getTransaction().begin();
            query.executeUpdate();
            em.getTransaction().commit();
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
            tbl1.setKeyString("Key20");
            tbl1.setItemString1("A");
            tbl1.setItemString2(null);
            tbl1.setItemString3("C");
            tbl1.setItemString4("D");
            tbl1.setItemInteger1(9);
            em.persist(tbl1);

            EntityTbl01 tbl2 = new EntityTbl01();
            tbl2.setKeyString("Key21");
            tbl2.setItemString1("A");
            tbl2.setItemString2("B");
            tbl2.setItemString3("C");
            tbl2.setItemString4(null);
            tbl2.setItemInteger1(8);
            em.persist(tbl2);

            EntityTbl01 tbl3 = new EntityTbl01();
            tbl3.setKeyString("Key22");
            tbl3.setItemString1(null);
            tbl3.setItemString2("B");
            tbl3.setItemString3("C");
            tbl3.setItemString4("D");
            tbl3.setItemInteger1(9);
            em.persist(tbl3);

            EntityTbl01 tbl4 = new EntityTbl01();
            tbl4.setKeyString("Key13");
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
} 