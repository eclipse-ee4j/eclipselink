/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 * Andrei Ilitchev May 28, 2008. Bug 224964: Provide support for Proxy Authentication through JPA.
 *     Changed the was Proxy Authentication supported in case of thin driver, but support for oci case remains the same.
 *     That caused re-arranging of the tests: before the fix all the tests were directly in proxiauthentication package;
 *     now the old tests (minus thin-specific setup) were moved into the new proxyauthentication.oci package,
 *     and the new tests defined in the new proxyauthentication.thin package.
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.proxyauthentication.oci;

import java.util.HashMap;
import java.util.Properties;

import java.sql.SQLException;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * Test Database change notification using JMS on top of Oracle AQ.
 */
public class ProxyAuthenticationOCITestModel extends TestModel {
    public ProxyAuthenticationOCITestModel() {
        super();
        this.proxyTestHelperClassName = "org.eclipse.persistence.testing.tests.proxyauthentication.oci.OCIProxyTestHelper";
    }
    protected Session oldSession;
    // Proxy Authentication user through which the session will be connected
    public static String connUser = "PA_CONN";
    public static String connPassword = "PA_CONN";
    // Proxy Authentication proxy user
    public static String proxyUser = "PA_PROXY";
    //    static protected String proxyPassword = "PA_PROXY";

    protected static HashMap proxyTestHelperMap = new HashMap();

    protected String proxyTestHelperClassName;
    protected ProxyTestHelper proxyTestHelper;

    public void addTests() {
        Properties prop = proxyTestHelper.createProxyProperties(proxyUser);
        //        Properties prop = proxyTestHelper.createProxyProperties(proxyUser, proxyPassword);
        addTest(new NonPooledConnectionTestCase(prop, false));
        addTest(new NonPooledConnectionTestCase(prop, true));
        addTest(new PooledConnectionTestCase(prop));
        addTest(new ExternalConnectionPoolTestCase(prop, false));
        addTest(new ExternalConnectionPoolTestCase(prop, true));
        addTest(new MainLoginTestCase(prop));
    }

    public void addRequiredSystems() {
        setupUser();

        /*		addRequiredSystem(
			new TestSystemAdapted(
				new EmployeeSystem(),
				new DbChangeNotificationAdapter(queueName, queueTableName, useMultipleConsumers)));*/
    }

    protected void setupUser() {
        if (!getSession().getPlatform().isOracle()) {
            throw new TestWarningException("Supports Oracle platform only");
        }

        oldSession = getSession();
        try {
            if (!((AbstractSession)getSession()).getAccessor().getConnection().getMetaData().getUserName().equalsIgnoreCase(connUser)) {
                /** to setup Proxy Authentication users in Oracle db, need to execute in sqlPlus or EnterpriseManager
				1 - Connect as sysdba
				connect sys/password as sysdba

				2 - Create connUser
                create user PA_CONN identified by PA_CONN
				grant connect to PA_CONN

                3 - Create proxyUser
                create user PA_PROXY identified by PA_PROXY
				grant connect to PA_PROXY

                4. Grant proxyUser connection through connUser
                alter user PA_PROXY grant connect through PA_CONN
			*/
                DatabaseLogin login = (DatabaseLogin)oldSession.getLogin().clone();
                login.setUserName(connUser);
                login.setPassword(connPassword);
                DatabaseSession session = new Project(login).createServerSession();
                session.setSessionLog(getSession().getSessionLog());
                session.setLogLevel(getSession().getLogLevel());

                // get the specified proxyTestHelper, instantiate if not already exists.
                proxyTestHelper = (ProxyTestHelper)proxyTestHelperMap.get(proxyTestHelperClassName);
                if (proxyTestHelper == null) {
                    try {
                        proxyTestHelper = (ProxyTestHelper)Class.forName(proxyTestHelperClassName).newInstance();
                    } catch (Exception ex) {
                        throw new TestProblemException("Failed to instantiate proxyTestHelperClass " + proxyTestHelperClassName, ex);
                    }
                    proxyTestHelperMap.put(proxyTestHelperClassName, proxyTestHelper);
                }

                // normally user would set ProxyConnector into the login in preLogin event
                try {
                    proxyTestHelper.setProxyConnectorIntoLogin(login, oldSession);
                } catch (SQLException sqlException) {
                    throw new TestProblemException("Failed to create ProxyConnector ", sqlException);
                }

                try {
                    session.login();
                } catch (Exception exception) {
                    throw new TestProblemException("Database needs to be setup for ProxyAuthentication, needs PA_CONN, PA_PROXY users. See comments in ProxyAuthenticationInternalTestModel", exception);
                }

                getExecutor().setSession(session);
            }
        } catch (java.sql.SQLException se) {
            se.printStackTrace();
            throw new TestErrorException("There is SQLException");
        }
    }

    public void reset() {
        if (oldSession != getExecutor().getSession()) {
            ((DatabaseSession)getSession()).logout();
            getExecutor().setSession(oldSession);
            try {
                proxyTestHelper.close();
            } catch (SQLException sqlException) {
                throw new TestErrorException("Failed to close ProxyConnector's adataSource ", sqlException);
            }
        }
    }
}
