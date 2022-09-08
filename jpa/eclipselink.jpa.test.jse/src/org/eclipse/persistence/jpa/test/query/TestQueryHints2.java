/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
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
//     09/08/2022 - Oracle
package org.eclipse.persistence.jpa.test.query;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.jpa.test.framework.PUPropertiesProvider;
import org.eclipse.persistence.jpa.test.query.model.QueryOrder;
import org.eclipse.persistence.jpa.test.query.model.QueryOrderLine;
import org.eclipse.persistence.queries.ScrollableCursor;
import org.eclipse.persistence.sessions.Connector;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestQueryHints2 {

    private static boolean POPULATED = false;

    @Emf(createTables = DDLGen.DROP_CREATE, classes = { QueryOrder.class, QueryOrderLine.class },
            properties = { @Property(name="eclipselink.logging.level", value="FINEST")})
    private EntityManagerFactory emf;

    /**
     * Test that setting the Query Hint: QueryHints.INNER_JOIN_IN_WHERE_CLAUSE are correctly applied.
     */
    @Test
    public void testPrintInnerJoinInWhereClauseHint() {
        if (emf == null)
            return;

        if(!POPULATED)
            populate();

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            /*
             * First create a NamedQuery and return the result list without hint (defaut value)
             */
            Query query1 = em.createNamedQuery("QueryOrder.findAllOrdersWithEmptyOrderLines", QueryOrder.class);
            List<QueryOrder> result1 = query1.getResultList();
            Assert.assertEquals(1, result1.size());
            Assert.assertEquals(2L, result1.get(0).getOrderKey());

            /*
             * Second create a NamedQuery and return the result list with hint (true value)
             */
            Query query2 = em.createNamedQuery("QueryOrder.findAllOrdersWithEmptyOrderLinesHintTrue", QueryOrder.class);
            List<QueryOrder> result2 = query2.getResultList();
            Assert.assertEquals(1, result2.size());
            Assert.assertEquals(2L, result2.get(0).getOrderKey());

            /*
             * Third create a NamedQuery and return the result list with hint (false value)
             * This test is based on bug in EL normalization part as some queries are not correctly translated
             * in case of PrintInnerJoinInWhereClause == false
             * Generated SQL query incorrectly doesn't return any value.
             * Some fix in this part should lead into crash there.
             */
            Query query3 = em.createNamedQuery("QueryOrder.findAllOrdersWithEmptyOrderLinesHintFalse", QueryOrder.class);
            List<QueryOrder> result3 = query3.getResultList();
            Assert.assertEquals(0, result3.size());

            /*
             * Fourth create a Query based on JPQL and return the result list with hint (false value)
             * This test is based on bug in EL normalization part as some queries are not correctly translated
             * in case of PrintInnerJoinInWhereClause == false
             * Generated SQL query incorrectly doesn't return any value.
             * Some fix in this part should lead into crash there.
             */
            //JPQL Query test
            String jpql = "SELECT o FROM QueryOrder o WHERE o.queryOrderLines IS EMPTY";
            Query query4 = em.createQuery(jpql, QueryOrder.class);
            query4.setHint(QueryHints.INNER_JOIN_IN_WHERE_CLAUSE, "false");
            List<QueryOrder> result4 = query4.getResultList();
            Assert.assertEquals(0, result4.size());


        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }
    }

    public void populate() {
        //Populate the tables
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            QueryOrder queryOrder1 = new QueryOrder();
            queryOrder1.setOrderKey(1);
            em.persist(queryOrder1);
            QueryOrder queryOrder2 = new QueryOrder();
            queryOrder2.setOrderKey(2);
            em.persist(queryOrder2);
            QueryOrderLine queryOrderLine1 = new QueryOrderLine();
            queryOrderLine1.setOrderLineKey(101);
            queryOrderLine1.setOrder(queryOrder1);
            em.persist(queryOrderLine1);
            QueryOrderLine queryOrderLine2 = new QueryOrderLine();
            queryOrderLine2.setOrderLineKey(102);
            queryOrderLine2.setOrder(queryOrder1);
            em.persist(queryOrderLine2);

            em.getTransaction().commit();

            POPULATED = true;
        } finally {
            if(em.isOpen()) {
                em.close();
            }
        }
    }
}
