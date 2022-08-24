/*
 * Copyright (c) 2017, 2022 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2017, 2022 IBM Corporation. All rights reserved.
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
//     06/30/2015-2.6.0 Will Dazey
//       - 471487: Added test for QueryHints.JDBC_TIMEOUT that checks the executed sql statement
//     09/03/2015 - Will Dazey
//       - 456067 : Added tests to check query timeout units
//     01/31/2017 - Will Dazey
//       - 511426: Adding test to check QueryHints.SCROLLABLE_CURSOR
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

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;

import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.PUPropertiesProvider;
import org.eclipse.persistence.jpa.test.query.model.QueryEmployee;
import org.eclipse.persistence.jpa.test.query.model.QueryOrder;
import org.eclipse.persistence.jpa.test.query.model.QueryOrderLine;
import org.eclipse.persistence.queries.ScrollableCursor;
import org.eclipse.persistence.sessions.Connector;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(EmfRunner.class)
public class TestQueryHints implements PUPropertiesProvider {

    private static int statementTimeout;

    private final static int propertyTimeout = 3099;

    @Emf(name = "queryhintsEMF", classes = { QueryEmployee.class, QueryOrder.class, QueryOrderLine.class }, createTables = DDLGen.DROP_CREATE)
    private EntityManagerFactory emf;

    public void setup() {
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
     * Test that setting the Query Hint: QueryHints.JDBC_TIMEOUT to the default value
     *  will see the expected value of seconds being set on the statement
     */
    @Test
    public void testJDBCQueryTimeout() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("SELECT x FROM QueryEmployee x")
                .setHint(QueryHints.QUERY_TIMEOUT, TestQueryHints.propertyTimeout)
                .getResultList();

            //Convert the timeout set (MILLISECONDS) to what is expected by the JDBC layer (SECONDS)
            double queryTimeoutSeconds = TestQueryHints.propertyTimeout / 1000d;
            //if there was a remainder, it should round up
            if(queryTimeoutSeconds % 1 > 0) {
                queryTimeoutSeconds += 1;
            }

            assertEquals((int)queryTimeoutSeconds, TestQueryHints.statementTimeout);
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }

        em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("SELECT x FROM QueryEmployee x")
                .setHint(QueryHints.JDBC_TIMEOUT, TestQueryHints.propertyTimeout)
                .getResultList();

            //Convert the timeout set (MILLISECONDS) to what is expected by the JDBC layer (SECONDS)
            double queryTimeoutSeconds = TestQueryHints.propertyTimeout / 1000d;
            //if there was a remainder, it should round up
            if(queryTimeoutSeconds % 1 > 0) {
                queryTimeoutSeconds += 1;
            }

            assertEquals((int)queryTimeoutSeconds, TestQueryHints.statementTimeout);
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
     * Test that setting the Query Hint: QueryHints.JDBC_TIMEOUT_UNIT to the "SECONDS" value
     *  will see the expected value of seconds being set on the statement
     */
    @Test
    public void testQueryTimeoutUnitSeconds() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("SELECT x FROM QueryEmployee x")
                .setHint(QueryHints.QUERY_TIMEOUT, TestQueryHints.propertyTimeout)
                .setHint(QueryHints.QUERY_TIMEOUT_UNIT, TimeUnit.SECONDS.toString()).getResultList();

            //Convert the timeout set (SECONDS) to what is expected by the JDBC layer (SECONDS)
            int queryTimeoutSecondsDouble = TestQueryHints.propertyTimeout;

            assertEquals(queryTimeoutSecondsDouble, TestQueryHints.statementTimeout);
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if (em != null) {
                em.close();
            }
        }

        em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("SELECT x FROM QueryEmployee x")
                .setHint(QueryHints.JDBC_TIMEOUT, TestQueryHints.propertyTimeout)
                .setHint(QueryHints.QUERY_TIMEOUT_UNIT, TimeUnit.SECONDS.toString()).getResultList();

            //Convert the timeout set (SECONDS) to what is expected by the JDBC layer (SECONDS)
            int queryTimeoutSecondsDouble = TestQueryHints.propertyTimeout;

            assertEquals(queryTimeoutSecondsDouble, TestQueryHints.statementTimeout);
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
     * Test that setting the Query Hint: QueryHints.JDBC_TIMEOUT_UNIT to the "MINUTES" value
     *  will see the expected value of seconds being set on the statement
     */
    @Test
    public void testQueryTimeoutUnitMinutes() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("SELECT x FROM QueryEmployee x")
                .setHint(QueryHints.QUERY_TIMEOUT, TestQueryHints.propertyTimeout)
                .setHint(QueryHints.QUERY_TIMEOUT_UNIT, TimeUnit.MINUTES.toString()).getResultList();

            //Convert the timeout set (MINUTES) to what is expected by the JDBC layer (SECONDS)
            int queryTimeoutSeconds = TestQueryHints.propertyTimeout * 60;

            assertEquals(queryTimeoutSeconds, TestQueryHints.statementTimeout);
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }

        em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("SELECT x FROM QueryEmployee x")
                .setHint(QueryHints.JDBC_TIMEOUT, TestQueryHints.propertyTimeout)
                .setHint(QueryHints.QUERY_TIMEOUT_UNIT, TimeUnit.MINUTES.toString()).getResultList();

            //Convert the timeout set (MINUTES) to what is expected by the JDBC layer (SECONDS)
            int queryTimeoutSeconds = TestQueryHints.propertyTimeout * 60;

            assertEquals(queryTimeoutSeconds, TestQueryHints.statementTimeout);
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
     * Test that setting the Query Hint: QueryHints.SCROLLABLE_CURSOR on a NamedQuery
     * does not cause subsequent Queries, created using the same name, to throw exception.
     *
     */
    @Test
    public void testMultipleNamedQueryWithScrollableCursor() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            /*
             * First create a NamedQuery and return the result list
             */
            Query query1 = em.createNamedQuery("QueryEmployee.findAll");
            query1.getResultList();

            /*
             * Next, create the same NamedQuery, but add the QueryHints.SCROLLABLE_CURSOR hint
             * and return the ScrollableCursor
             */
            Query query2 = em.createNamedQuery("QueryEmployee.findAll");
            query2.setHint(QueryHints.SCROLLABLE_CURSOR, HintValues.TRUE);
            ScrollableCursor cursor = ((ScrollableCursor) query2.getSingleResult());
            cursor.close();

            /*
             * Finally, attempt to create a third NamedQuery, but return a result list
             * without adding a hint to this Query
             */
            Query query3 = em.createNamedQuery("QueryEmployee.findAll");
            query3.getResultList();
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
     * Test that setting the Query Hint: QueryHints.PRINT_INNER_JOIN_IN_WHERE_CLAUSE are correctly applied.
     */
    @Test
    public void testPrintInnerJoinInWhereClauseHint() {
        setup();
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            /*
             * First create a NamedQuery and return the result list without hint (defaut value)
             */
            Query query1 = em.createNamedQuery("QueryOrder.findAllOrdersWithEmptyOrderLines", QueryOrder.class);
            List<QueryOrder> result1 = query1.getResultList();
            assertEquals(1, result1.size());
            assertEquals(2L, result1.get(0).getOrderKey());

            /*
             * Second create a NamedQuery and return the result list with hint (true value)
             */
            Query query2 = em.createNamedQuery("QueryOrder.findAllOrdersWithEmptyOrderLinesHintTrue", QueryOrder.class);
            List<QueryOrder> result2 = query2.getResultList();
            assertEquals(1, result2.size());
            assertEquals(2L, result2.get(0).getOrderKey());

            /*
             * Third create a NamedQuery and return the result list with hint (false value)
             * This test is based on bug in EL normalization part as some queries are not correctly translated
             * in case of PrintInnerJoinInWhereClause == false
             * Generated SQL query incorrectly doesn't return any value.
             * Some fix in this part should lead into crash there.
             */
            Query query3 = em.createNamedQuery("QueryOrder.findAllOrdersWithEmptyOrderLinesHintFalse", QueryOrder.class);
            List<QueryOrder> result3 = query3.getResultList();
            assertEquals(0, result3.size());

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
            query4.setHint(QueryHints.PRINT_INNER_JOIN_IN_WHERE_CLAUSE, "false");
            List<QueryOrder> result4 = query4.getResultList();
            assertEquals(0, result4.size());


        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }
    }

    @Override
    public Map<String, Object> getAdditionalPersistenceProperties(String puName) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(PersistenceUnitProperties.SESSION_CUSTOMIZER, Customizer.class.getName());
        return map;
    }

    public static class Customizer implements SessionCustomizer {
        @Override
        public void customize(Session session) throws Exception {
            session.getEventManager().addListener(new SessionEventAdapter() {
                @Override
                public void preLogin(SessionEvent event) {
                    DatabaseLogin login = event.getSession().getLogin();
                    login.setConnector(ConnectorInvocationHandler.createStatementProxy(login.getConnector()));
                }
            });
        }
    }

    public static class ConnectorInvocationHandler implements InvocationHandler {
        private Connector wrappedConnector;

        public ConnectorInvocationHandler(Connector stmt) {
            wrappedConnector = stmt;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getName().equals("connect") && proxy instanceof Connector) {
                return ConnectionInvocationHandler.createStatementProxy((Connection) method.invoke(wrappedConnector, args));
            }
            return method.invoke(wrappedConnector, args);
        }

        public static Connector createStatementProxy(Connector toWrap) {
            return (Connector) Proxy.newProxyInstance(Connector.class.getClassLoader(), new Class<?>[] { Connector.class }, new ConnectorInvocationHandler(toWrap));
        }
    }

    public static class ConnectionInvocationHandler implements InvocationHandler {
        private Connection wrappedConnection;

        public ConnectionInvocationHandler(Connection stmt) {
            wrappedConnection = stmt;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            try {
                if (method.getName().equals("prepareStatement") && proxy instanceof Connection) {
                    return PreparedStatementInvocationHandler.createStatementProxy((PreparedStatement) method.invoke(wrappedConnection, args));
                }
                return method.invoke(wrappedConnection, args);
            } catch (Exception e) {
                throw e.getCause();
            }
        }

        public static Connection createStatementProxy(Connection toWrap) {
            return (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(), new Class<?>[] { Connection.class }, new ConnectionInvocationHandler(toWrap));
        }
    }

    public static class PreparedStatementInvocationHandler implements InvocationHandler {
        private PreparedStatement wrappedStatement;

        public PreparedStatementInvocationHandler(PreparedStatement stmt) {
            wrappedStatement = stmt;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            try {
                //Get the query timeout being set on the statement
                //This value should be in seconds, since that is what the Statement expects
                if (method.getName().equals("setQueryTimeout") && proxy instanceof PreparedStatement) {
                    if(args.length > 0) {
                        TestQueryHints.statementTimeout = (Integer)args[0];
                    }
                }
                return method.invoke(wrappedStatement, args);
            } catch (Exception e) {
                throw e.getCause();
            }
        }

        public static PreparedStatement createStatementProxy(PreparedStatement toWrap) {
            return (PreparedStatement) Proxy.newProxyInstance(PreparedStatement.class.getClassLoader(), new Class<?>[] { PreparedStatement.class }, new PreparedStatementInvocationHandler(toWrap));
        }
    }
}
