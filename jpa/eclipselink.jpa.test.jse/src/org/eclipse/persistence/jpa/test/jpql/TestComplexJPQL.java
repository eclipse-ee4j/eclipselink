/*
 * Copyright (c) 2018 Oracle and/or its affiliates, IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     02/20/2018-2.7 Will Dazey
//       - 531062: Incorrect expression type created for CollectionExpression
//     05/11/2018-2.7 Will Dazey
//       - 534515: Incorrect return type set for CASE functions
package org.eclipse.persistence.jpa.test.jpql;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.jpql.model.JPQLEmbeddedValue;
import org.eclipse.persistence.jpa.test.jpql.model.JPQLEntity;
import org.eclipse.persistence.jpa.test.jpql.model.JPQLEntityId;
import org.eclipse.persistence.jpa.test.jpql.model.QueryResult;
import org.eclipse.persistence.jpa.test.jpql.model.SimpleEntity;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.eclipse.persistence.platform.database.DerbyPlatform;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestComplexJPQL {

    @Emf(createTables = DDLGen.DROP_CREATE, classes = { JPQLEntity.class, JPQLEntityId.class, JPQLEmbeddedValue.class, SimpleEntity.class })
    private EntityManagerFactory emf;

    @Test
    public void testComplexJPQLIN() {
        if(getPlatform(emf) instanceof DerbyPlatform) {
            Assert.assertTrue("Test will not run on DerbyPlatform. Derby does "
                    + "not support multiple IN clause for prepared statements.", true);
            return;
        }
        EntityManager em = emf.createEntityManager();

        Query q = em.createQuery("select t0.id from JPQLEntity t0 "
                + "where (t0.string1, t0.string2) "
                + "in (select t1.string1, t1.string2 from JPQLEntity t1)");
        q.getResultList();

        if (em.isOpen()) {
            em.clear();
            em.close();
        }
    }

    @Test
    public void testComplexJPQLCase() {
        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();

        SimpleEntity tbl1 = new SimpleEntity();
        tbl1.setKeyString("Key01");
        tbl1.setItemString1("A");
        tbl1.setItemString2("B");
        tbl1.setItemString3("C");
        tbl1.setItemString4("D");
        tbl1.setItemInteger1(1);
        em.persist(tbl1);

        SimpleEntity tbl2 = new SimpleEntity();
        tbl2.setKeyString("Key02");
        tbl2.setItemString1("A");
        tbl2.setItemString2("B");
        tbl2.setItemString3("C");
        tbl2.setItemString4("D");
        tbl2.setItemInteger1(2);
        em.persist(tbl2);

        em.getTransaction().commit();

        TypedQuery<QueryResult> q = em.createQuery("SELECT new org.eclipse.persistence.jpa.test.jpql.model.QueryResult("
                + "s.itemString1, "
                + "CASE s.itemString2 WHEN 'J' THEN 'Japan' ELSE 'Other' END, "
                + "SUM(CASE WHEN s.itemString3 = 'C' THEN 1 ELSE 0 END), "
                + "SUM(CASE WHEN s.itemString4 = 'D' THEN 1 ELSE 0 END) ) "
            + "FROM SimpleEntity s "
            + "GROUP BY s.itemString1, s.itemString2", QueryResult.class);
        q.getResultList();

        if (em.isOpen()) {
            em.clear();
            em.close();
        }
    }

    private DatabasePlatform getPlatform(EntityManagerFactory emf) {
        return ((EntityManagerFactoryImpl)emf).getServerSession().getPlatform();
    }
}
