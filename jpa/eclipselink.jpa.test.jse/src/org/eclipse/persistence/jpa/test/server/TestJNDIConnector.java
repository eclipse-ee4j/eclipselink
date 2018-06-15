/*
 * Copyright (c) 2015, 2018 IBM Corporation, Oracle and/or its affiliates All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     01/06/2015 Rick Curtis
//       - 55690: Move JNDIConnector lookup type to ServerPlatform.
//     03/15/2016 Jody Grassel
//       - 489794: Add WebSphere EJBEmbeddable platform test
package org.eclipse.persistence.jpa.test.server;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Properties;

import javax.naming.CompositeName;
import javax.naming.Context;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.jpa.test.basic.model.Employee;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.sessions.JNDIConnector;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * This test validates that when a given target-server is configured, the proper Context.lookup(...) method
 * is invoked.
 */
@RunWith(EmfRunner.class)
public class TestJNDIConnector {
    @Emf(name = "libertyEmf", createTables = DDLGen.NONE, classes = { Employee.class }, properties = { @Property(
            name = "eclipselink.target-server", value = "WebSphere_Liberty") })
    private EntityManagerFactory libertyEmf;

    @Emf(name = "wasEmf", createTables = DDLGen.NONE, classes = { Employee.class }, properties = { @Property(
            name = "eclipselink.target-server", value = "WebSphere_7") })
    private EntityManagerFactory wasEmf;
    
    @Emf(name = "wasEJBEmbedEmf", createTables = DDLGen.NONE, classes = { Employee.class }, properties = { @Property(
            name = "eclipselink.target-server", value = "WebSphere_EJBEmbeddable") })
    private EntityManagerFactory wasEJBEmbeddableEmf;

    @Emf(name = "defaultEmf", createTables = DDLGen.NONE, classes = { Employee.class }, properties = {})
    private EntityManagerFactory defaultEmf;

    Context _ctx;
    MyInvocationHandler _handler;
    JNDIConnector _connector;
    DataSource _dataSource;

    @Before
    public void setup() {
        _handler = new MyInvocationHandler();
        _ctx = (Context) Proxy.newProxyInstance(null, new Class<?>[] { Context.class }, _handler);
        _dataSource = (DataSource) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class<?>[] { DataSource.class }, _handler);
        _connector = new JNDIConnector(_ctx, "test");
    }

    @Test
    public void testWasTargetServerLookupType() {
        ServerSession session = ((EntityManagerFactoryImpl) wasEmf).getServerSession();
        _connector.connect(new Properties(), session);

        Assert.assertEquals(String.class, _handler.getParamType());
    }
    
    @Test
    public void testWasEJBEmbeddableTargetServerLookupType() {
        ServerSession session = ((EntityManagerFactoryImpl) wasEJBEmbeddableEmf).getServerSession();
        _connector.connect(new Properties(), session);

        Assert.assertEquals(String.class, _handler.getParamType());
    }


    @Test
    public void testLibertTargetServerLookupType() {
        ServerSession session = ((EntityManagerFactoryImpl) libertyEmf).getServerSession();
        _connector.connect(new Properties(), session);

        Assert.assertEquals(String.class, _handler.getParamType());
    }

    @Test
    public void testDefaultTargetServerLookupType() {
        ServerSession session = ((EntityManagerFactoryImpl) defaultEmf).getServerSession();
        _connector.connect(new Properties(), session);

        Assert.assertEquals(CompositeName.class, _handler.getParamType());
    }

    class MyInvocationHandler implements InvocationHandler {
        Class<?> _param;

        Class<?> getParamType() {
            return _param;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // Save the parameter that is passed when lookup is called for later validation.
            if (method.getName().equals("lookup")) {
                _param = args[0].getClass();
                return _dataSource;
            }
            // Could check the method call here and return a connection for some of the DataSource.getConnection(..)
            // method calls.
            return null;
        }
    }

}
