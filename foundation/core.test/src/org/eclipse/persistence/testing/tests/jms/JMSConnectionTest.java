/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jms;

import org.eclipse.persistence.sessions.remote.jms.JMSConnection;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;

import java.lang.reflect.*;

/**
 * Test the JMSConnection class.
 */
public class JMSConnectionTest extends TestCase {
    protected String m_results;
    protected JMSConnection m_con;
    protected String m_methodName;
    protected Class[] m_paramTypes;
    protected Object[] m_params;

    public JMSConnectionTest(String method) {
        m_methodName = method;
        m_paramTypes = null;
        m_params = null;
        setName("JMSConnectionTest - " + m_methodName);
    }

    public JMSConnectionTest(String method, Class[] paramTypes, Object[] params) {
        m_methodName = method;
        m_paramTypes = paramTypes;
        m_params = params;
        setName("JMSConnectionTest - " + m_methodName);
    }

    public void setup() {
        m_results = "";
        m_con = new JMSConnection(new TopicSessionImpl(), new TopicPublisherImpl());
    }

    public void test() {
        Method meth;

        try {
            meth = m_con.getClass().getMethod(m_methodName, m_paramTypes);
        } catch (NoSuchMethodException nsme) {
            m_results = "Attempted to test method '" + m_methodName + "' which does not exist.";
            return;
        }

        try {
            meth.invoke(m_con, m_params);
        } catch (Exception e) {
            m_results = "Test of method '" + m_methodName + "' falied.";
        }
    }

    public void verify() {
        if (m_results.length() > 0) {
            throw (new TestErrorException(m_results));
        }
    }

    public void reset() {
        m_results = "";
        m_con = null;
    }
}
