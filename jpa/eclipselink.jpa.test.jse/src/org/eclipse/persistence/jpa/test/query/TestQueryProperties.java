/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2015 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     06/30/2015-2.6.0 Will Dazey
//       - 471487: Added test for QueryHints.JDBC_TIMEOUT that checks the executed sql statement
//     09/03/2015 - Will Dazey
//       - 456067 : Added tests to check query timeout units
package org.eclipse.persistence.jpa.test.query;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.jpa.test.basic.model.Employee;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.PUPropertiesProvider;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.sessions.Connector;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestQueryProperties implements PUPropertiesProvider {

    private static int setTimeout;
    
    private final static int realTimeout = 3099;

    @Emf(name = "timeoutEMF", classes = { Employee.class }, properties = { 
            @Property(name = PersistenceUnitProperties.QUERY_TIMEOUT, value = "" + TestQueryProperties.realTimeout) })
    private EntityManagerFactory emfTimeout;

    @Emf(name = "timeoutWithUnitMintuesEMF", classes = { Employee.class }, properties = { 
            @Property(name = PersistenceUnitProperties.QUERY_TIMEOUT, value = "" + TestQueryProperties.realTimeout),
            @Property(name = PersistenceUnitProperties.QUERY_TIMEOUT_UNIT, value = "MINUTES") })
    private EntityManagerFactory emfTimeoutMinutes;
    
    @Emf(name = "timeoutWithUnitMillisecondsEMF", classes = { Employee.class }, properties = { 
            @Property(name = PersistenceUnitProperties.QUERY_TIMEOUT, value = "" + TestQueryProperties.realTimeout),
            @Property(name = PersistenceUnitProperties.QUERY_TIMEOUT_UNIT, value = "MILLISECONDS") })
    private EntityManagerFactory emfTimeoutMilliseconds;

    /**
     * Test that setting the property "PersistenceUnitProperties.QUERY_TIMEOUT_UNIT" sets the
     * timeout accordingly on the executed java.sql.Statement.
     * 
     * Assumes value will be converted to Seconds for JDBC.
     * 
     * @throws Exception
     */
    @Test
    public void testTimeoutUnitDefault() throws Exception {
        EntityManager em = null;
        try {
            em = emfTimeout.createEntityManager();

            em.getTransaction().begin();
            Query q = em.createQuery("SELECT x FROM Employee x");
            q.getResultList();
            
            int queryTimeoutSeconds = TestQueryProperties.realTimeout;

            Assert.assertEquals(queryTimeoutSeconds, TestQueryProperties.setTimeout);

            em.getTransaction().rollback();
        } catch (Exception e) {
            Assert.fail(e.getLocalizedMessage());
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Test that setting the property "PersistenceUnitProperties.QUERY_TIMEOUT_UNIT" sets the
     * timeout accordingly on the executed java.sql.Statement.
     * 
     * Assumes value will be converted to Seconds for JDBC.
     * 
     * @throws Exception
     */
    @Test
    public void testTimeoutUnitMinutes() throws Exception {
        EntityManager em = null;
        try {
            em = emfTimeoutMinutes.createEntityManager();

            em.getTransaction().begin();
            Query q = em.createQuery("SELECT x FROM Employee x");
            q.getResultList();
            
            int queryTimeoutSeconds = TestQueryProperties.realTimeout * 60;

            Assert.assertEquals(queryTimeoutSeconds, TestQueryProperties.setTimeout);

            em.getTransaction().rollback();
        } catch (Exception e) {
            Assert.fail(e.getLocalizedMessage());
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Test that setting the property "PersistenceUnitProperties.QUERY_TIMEOUT_UNIT" sets the
     * timeout accordingly on the executed java.sql.Statement.
     * 
     * Assumes value will be converted to Seconds for JDBC.
     * 
     * @throws Exception
     */
    @Test
    public void testTimeoutUnitMilliseconds() throws Exception {
        EntityManager em = null;
        try {
            em = emfTimeoutMilliseconds.createEntityManager();

            em.getTransaction().begin();
            Query q = em.createQuery("SELECT x FROM Employee x");
            q.getResultList();
            
            double queryTimeoutSeconds = TestQueryProperties.realTimeout / 1000d;
            //if there was a remainder, it should round up
            if(queryTimeoutSeconds % 1 > 0){
                queryTimeoutSeconds += 1;
            }

            Assert.assertEquals((int)queryTimeoutSeconds, TestQueryProperties.setTimeout);

            em.getTransaction().rollback();
        } catch (Exception e) {
            Assert.fail(e.getLocalizedMessage());
        } finally {
            if (em != null) {
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

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getName().equals("connect") && proxy instanceof Connector) {
                return ConnectionInvocationHandler.createStatementProxy((Connection) method.invoke(wrappedConnector, args));
            }
            return method.invoke(wrappedConnector, args);
        }

        public static Connector createStatementProxy(Connector toWrap) {
            return (Connector) Proxy.newProxyInstance(Connector.class.getClassLoader(), new Class[] { Connector.class }, new ConnectorInvocationHandler(toWrap));
        }
    }

    public static class ConnectionInvocationHandler implements InvocationHandler {
        private Connection wrappedConnection;

        public ConnectionInvocationHandler(Connection stmt) {
            wrappedConnection = stmt;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getName().equals("prepareStatement") && proxy instanceof Connection) {
                return PreparedStatementInvocationHandler.createStatementProxy((PreparedStatement) method.invoke(wrappedConnection, args));
            }
            return method.invoke(wrappedConnection, args);
        }

        public static Connection createStatementProxy(Connection toWrap) {
            return (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(), new Class[] { Connection.class }, new ConnectionInvocationHandler(toWrap));
        }
    }

    public static class PreparedStatementInvocationHandler implements InvocationHandler {
        private PreparedStatement wrappedStatement;

        public PreparedStatementInvocationHandler(PreparedStatement stmt) {
            wrappedStatement = stmt;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getName().equals("setQueryTimeout") && proxy instanceof PreparedStatement) {
                if (args.length > 0) {
                    TestQueryProperties.setTimeout = (Integer) args[0];
                }
            }
            return method.invoke(wrappedStatement, args);
        }

        public static PreparedStatement createStatementProxy(PreparedStatement toWrap) {
            return (PreparedStatement) Proxy.newProxyInstance(PreparedStatement.class.getClassLoader(), new Class[] { PreparedStatement.class }, new PreparedStatementInvocationHandler(toWrap));
        }
    }
}
