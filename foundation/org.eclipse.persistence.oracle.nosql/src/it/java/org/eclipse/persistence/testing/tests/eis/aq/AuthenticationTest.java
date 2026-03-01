/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
//     04/01/2016-2.7 Tomas Kraus
//       - 490677: Database connection properties made configurable in test.properties
package org.eclipse.persistence.testing.tests.eis.aq;

import org.eclipse.persistence.eis.EISLogin;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.server.Server;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.junit.LogTestExecution;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Basic authentication tests. This tests user name / password verification when connecting
 * TopLink EIS to the native AQ JCA connector.  Valid/invalid user names and passwords
 * will be tested using both server and database sessions.
 */
public class AuthenticationTest {

    /** Log the test being currently executed. */
    @Rule public LogTestExecution logExecution = new LogTestExecution();

    /** Logger. */
    private static final SessionLog LOG = AbstractSessionLog.getLog();

    /** EIS login instance. */
    private EISLogin login;

    /**
     * Set up this test suite.
     * New login instance is required for each test.
     */
    @Before
    public void setUp() {
        login = AQTestSuite.initLogin();
    }

    /**
     * Clean up this test suite.
     */
    @After
    public void tearDown() {
        login = null;
    }

    /**
     * Test server session login with valid user name and password.
     */
    @Test
    public void testValidUsernameAndPassword() throws Exception {
        boolean failure = false;
        final Server session = new Project(login).createServerSession();
        session.setSessionLog(LOG);
        try {
            session.login();
            session.logout();
        } catch (Exception ex) {
            if (ex.getMessage().indexOf("invalid username/password") == -1) {
                throw ex;
            }
            failure = true;
        }
        if (failure) {
            throw new TestErrorException("Authentication failed unexpectedly.");
        }
    }

    /**
     * Test database session login with valid user name and password.
     */
    @Test
    public void testValidUsernameAndPasswordDBSession() throws Exception {
        boolean failure = false;
        final DatabaseSession session = new Project(login).createDatabaseSession();
        session.setSessionLog(LOG);
        try {
            session.login();
            session.logout();
        } catch (Exception ex) {
            if (ex.getMessage().indexOf("invalid username/password") == -1) {
                throw ex;
            }
            failure = true;
        }
        if (failure) {
            throw new TestErrorException("Authentication failed unexpectedly.");
        }
    }

    /**
     * Test server session login with invalid user name and valid password.
     */
    @Test
    public void testInvalidUsername() throws Exception {
        final String userName = "invaliduser";
        login.setUserName(userName);
        boolean failure = false;
        final Server session = new Project(login).createServerSession();
        session.setSessionLog(LOG);
        try {
            session.login();
            session.logout();
        } catch (Exception ex) {
            if (ex.getMessage().indexOf("invalid credential or not authorized; logon denied") == -1) {
                throw ex;
            }
            failure = true;
        }
        if (!failure) {
            throw new TestErrorException("Authentication did not fail as expected.");
        }
    }

    /**
     * Test server session login with valid user name and invalid password.
     */
    @Test
    public void testInvalidPassword() throws Exception {
        final String password = "invalidpassword";
        login.setPassword(password);
        boolean failure = false;
        final Server session = new Project(login).createServerSession();
        session.setSessionLog(LOG);
        try {
            session.login();
            session.logout();
        } catch (Exception ex) {
            if (ex.getMessage().indexOf("invalid credential or not authorized; logon denied") == -1) {
                throw ex;
            }
            failure = true;
        }
        if (!failure) {
            throw new TestErrorException("Authentication did not fail as expected.");
        }
    }

    /**
     * Test database session login with invalid user name and valid password.
     */
    @Test
    public void testInvalidUsernameDBSession() throws Exception {
        final String userName = "invaliduser";
        login.setUserName(userName);
        boolean failure = false;
        DatabaseSession session = new Project(login).createDatabaseSession();
        session.setSessionLog(LOG);
        try {
            session.login();
            session.logout();
        } catch (Exception ex) {
            if (ex.getMessage().indexOf("invalid credential or not authorized; logon denied") == -1) {
                throw ex;
            }
            failure = true;
        }
        if (!failure) {
            throw new TestErrorException("Authentication did not fail as expected.");
        }
    }

    /**
     * Test database session login with valid user name and invalid password.
     */
    @Test
    public void testInvalidPasswordDBSession() throws Exception {
        final String password = "invalidpassword";
        login.setPassword(password);
        boolean failure = false;
        DatabaseSession session = new Project(login).createDatabaseSession();
        session.setSessionLog(LOG);
        try {
            session.login();
            session.logout();
        } catch (Exception ex) {
            if (ex.getMessage().indexOf("invalid credential or not authorized; logon denied") == -1) {
                throw ex;
            }
            failure = true;
        }
        if (!failure) {
            throw new TestErrorException("Authentication did not fail as expected.");
        }
    }

}
