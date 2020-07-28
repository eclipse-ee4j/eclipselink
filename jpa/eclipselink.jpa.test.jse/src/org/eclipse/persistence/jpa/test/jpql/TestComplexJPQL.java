/*
 * Copyright (c) 2018, 2020 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2018, 2020 IBM Corporation. All rights reserved.
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
//     02/20/2018-2.7 Will Dazey
//       - 531062: Incorrect expression type created for CollectionExpression
//     05/11/2018-2.7 Will Dazey
//       - 534515: Incorrect return type set for CASE functions
package org.eclipse.persistence.jpa.test.jpql;

import java.util.ArrayList;
import java.util.List;

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
import org.eclipse.persistence.jpa.test.jpql.model.OtherSubClass;
import org.eclipse.persistence.jpa.test.jpql.model.QueryResult;
import org.eclipse.persistence.jpa.test.jpql.model.SimpleEntity;
import org.eclipse.persistence.jpa.test.jpql.model.SubClass;
import org.eclipse.persistence.jpa.test.jpql.model.SuperClass;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.eclipse.persistence.platform.database.DerbyPlatform;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestComplexJPQL {

    @Emf(name = "inEMF", createTables = DDLGen.DROP_CREATE, classes = { JPQLEntity.class, JPQLEntityId.class, JPQLEmbeddedValue.class })
    private EntityManagerFactory inEMF;

    @Emf(name = "caseEMF", createTables = DDLGen.DROP_CREATE, classes = { SimpleEntity.class })
    private EntityManagerFactory caseEMF;

    @Emf(name = "joinEMF", createTables = DDLGen.DROP_CREATE, classes = { SubClass.class, SuperClass.class, OtherSubClass.class })
    private EntityManagerFactory joinEMF;

    @Test
    public void testINWithSubquery() {
        DatabasePlatform platform = getPlatform(inEMF);
        Assume.assumeFalse("Test will not run on " + platform + ". " + platform + " does "
                + "not support multiple IN clause for prepared statements.", platform instanceof DerbyPlatform);

        EntityManager em = inEMF.createEntityManager();;
        try {
            Query q = em.createQuery("select t0.id from JPQLEntity t0 "
                    + "where (t0.string1, t0.string2) "
                    + "in (select t1.string1, t1.string2 from JPQLEntity t1)");
            q.getResultList();
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
    public void test_IN_ClauseLimit() {
        EntityManager em = inEMF.createEntityManager();
        try {
            Query q = em.createQuery("select t0.id from JPQLEntity t0 "
                    + "where t0.id.value1.value <> :parameterString and t0.string1 in :parameterList");

            //Create a list longer than the limit
            int limit = getPlatform(inEMF).getINClauseLimit() + 10;
            List<String> parameterList = new ArrayList<String>();
            for(int p = 0; p < limit; p++) {
                parameterList.add("" + p);
            }
            q.setParameter("parameterList", parameterList);
            q.setParameter("parameterString", "Test");

            q.getResultList();
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
    public void test_NOTIN_ClauseLimit() {
        EntityManager em = inEMF.createEntityManager();
        try {
            Query q = em.createQuery("select t0.id from JPQLEntity t0 "
                    + "where t0.id.value1.value <> :parameterString and t0.string1 not in :parameterList");

            //Create a list longer than the limit
            int limit = getPlatform(inEMF).getINClauseLimit() + 10;
            List<String> parameterList = new ArrayList<String>();
            for(int p = 0; p < limit; p++) {
                parameterList.add("" + p);
            }
            q.setParameter("parameterList", parameterList);
            q.setParameter("parameterString", "Test");

            q.getResultList();
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
    public void testComplexJPQLCase() {
        EntityManager em = caseEMF.createEntityManager();
        try {
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

            //Populate
            em.getTransaction().commit();

            TypedQuery<QueryResult> q = em.createQuery("SELECT new org.eclipse.persistence.jpa.test.jpql.model.QueryResult("
                    + "s.itemString1, "
                    + "CASE s.itemString2 WHEN 'J' THEN 'Japan' ELSE 'Other' END, "
                    + "SUM(CASE WHEN s.itemString3 = 'C' THEN 1 ELSE 0 END), "
                    + "SUM(CASE WHEN s.itemString4 = 'D' THEN 1 ELSE 0 END) ) "
                + "FROM SimpleEntity s "
                + "GROUP BY s.itemString1, s.itemString2", QueryResult.class);
            q.getResultList();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Tests EclipseLink's parsing of a LEFT OUTER JOIN when the condition has no matches. 
     * EclipseLink should account for the NULL values in the right table.
     * @see Bug 493804
     */
    @Test
    public void testLeftOuterJoinWithNullResult() {
        EntityManager em = joinEMF.createEntityManager();
        try {
            em.getTransaction().begin();

            SubClass t = new SubClass("Sub1");
            em.persist(t);
            t = new SubClass("Sub2");
            em.persist(t);

            OtherSubClass t2 = new OtherSubClass("OtherSub1");
            em.persist(t2);
            t2 = new OtherSubClass("OtherSub2");
            em.persist(t2);

            //Populate
            em.getTransaction().commit();

            Query q = em.createQuery("SELECT sub1, sub2 from SubClass sub1 LEFT JOIN SubClass sub2 ON sub2.name = 'unknown'");
            List<?> result = q.getResultList();

            Assert.assertEquals("The result size returned was incorrect", 2, result.size());
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }
    }

    private static DatabasePlatform getPlatform(EntityManagerFactory emf) {
        return ((EntityManagerFactoryImpl)emf).getServerSession().getPlatform();
    }
}
