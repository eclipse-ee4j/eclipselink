/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.eis.aq;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.sessions.server.*;
import org.eclipse.persistence.eis.*;
import org.eclipse.persistence.eis.adapters.aq.*;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;

/**
 * Basic authentication tests. This tests username/password verification when connecting 
 * TopLink EIS to the native AQ JCA connector.  Valid/invalid usernames and passwords
 * will be tested using both server and database sessions.
 */
public class AuthenticationTest extends TestCase {
    public AuthenticationTest() {
        setName("AuthenticationTest");
        setDescription("This tests username/password verification when connecting TopLink EIS to the native AQ JCA connector.");
    }

    public void test() throws Exception {
		testValidUsernameAndPassword();
		testValidUsernameAndPasswordDBSession();
		testInvalidUsername();
		testInvalidPassword();
		testInvalidUsernameDBSession();
		testInvalidPasswordDBSession();
    }

	public void testValidUsernameAndPassword() throws Exception {
        EISLogin login = new EISLogin(new AQPlatform());
        AQEISConnectionSpec spec = new AQEISConnectionSpec();
        login.setConnectionSpec(spec);

        String url = (String)getSession().getDatasourceLogin().getProperty(AQEISConnectionSpec.URL);

        login.setUserName("aquser");
        login.setPassword("aquser");
        login.setProperty(AQEISConnectionSpec.URL, url);

		boolean failure = false;
        Server session = new Project(login).createServerSession();
        session.setSessionLog(getSession().getSessionLog());
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

	public void testValidUsernameAndPasswordDBSession() throws Exception {
        EISLogin login = new EISLogin(new AQPlatform());
        AQEISConnectionSpec spec = new AQEISConnectionSpec();
        login.setConnectionSpec(spec);

        String url = (String)getSession().getDatasourceLogin().getProperty(AQEISConnectionSpec.URL);

        login.setUserName("aquser");
        login.setPassword("aquser");
        login.setProperty(AQEISConnectionSpec.URL, url);

		boolean failure = false;
        DatabaseSession session = new Project(login).createDatabaseSession();
        session.setSessionLog(getSession().getSessionLog());
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

	public void testInvalidUsername() throws Exception {
        EISLogin login = new EISLogin(new AQPlatform());
        AQEISConnectionSpec spec = new AQEISConnectionSpec();
        login.setConnectionSpec(spec);

        String url = (String)getSession().getDatasourceLogin().getProperty(AQEISConnectionSpec.URL);

        login.setUserName("invaliduser");
        login.setPassword("aquser");
        login.setProperty(AQEISConnectionSpec.URL, url);

		boolean failure = false;
        Server session = new Project(login).createServerSession();
        session.setSessionLog(getSession().getSessionLog());
        try {
			session.login();
	        session.logout();
        } catch (Exception ex) {
            if (ex.getMessage().indexOf("invalid username/password") == -1) {
                throw ex;
            }
			failure = true;
		}
		
		if (!failure) {
		    throw new TestErrorException("Authentication did not fail as expected.");
		}
	}
	
	public void testInvalidPassword() throws Exception {
        EISLogin login = new EISLogin(new AQPlatform());
        AQEISConnectionSpec spec = new AQEISConnectionSpec();
        login.setConnectionSpec(spec);

        String url = (String)getSession().getDatasourceLogin().getProperty(AQEISConnectionSpec.URL);

        login.setUserName("aquser");
        login.setPassword("invalidpassword");
        login.setProperty(AQEISConnectionSpec.URL, url);

		boolean failure = false;
		Server session = new Project(login).createServerSession();
        session.setSessionLog(getSession().getSessionLog());
        try {
			session.login();
	        session.logout();
        } catch (Exception ex) {
            if (ex.getMessage().indexOf("invalid username/password") == -1) {
                throw ex;
            }
			failure = true;
		}
		
		if (!failure) {
		    throw new TestErrorException("Authentication did not fail as expected.");
		}
	}

	public void testInvalidUsernameDBSession() throws Exception {
        EISLogin login = new EISLogin(new AQPlatform());
        AQEISConnectionSpec spec = new AQEISConnectionSpec();
        login.setConnectionSpec(spec);

        String url = (String)getSession().getDatasourceLogin().getProperty(AQEISConnectionSpec.URL);

        login.setUserName("invaliduser");
        login.setPassword("aquser");
        login.setProperty(AQEISConnectionSpec.URL, url);

		boolean failure = false;
        DatabaseSession session = new Project(login).createDatabaseSession();
        session.setSessionLog(getSession().getSessionLog());
        try {
			session.login();
	        session.logout();
        } catch (Exception ex) {
            if (ex.getMessage().indexOf("invalid username/password") == -1) {
                throw ex;
            }
			failure = true;
		}
		
		if (!failure) {
		    throw new TestErrorException("Authentication did not fail as expected.");
		}
	}

	public void testInvalidPasswordDBSession() throws Exception {
        EISLogin login = new EISLogin(new AQPlatform());
        AQEISConnectionSpec spec = new AQEISConnectionSpec();
        login.setConnectionSpec(spec);

        String url = (String)getSession().getDatasourceLogin().getProperty(AQEISConnectionSpec.URL);

        login.setUserName("aquser");
        login.setPassword("invalidpassword");
        login.setProperty(AQEISConnectionSpec.URL, url);

		boolean failure = false;
		DatabaseSession session = new Project(login).createDatabaseSession();
        session.setSessionLog(getSession().getSessionLog());
        try {
			session.login();
	        session.logout();
        } catch (Exception ex) {
            if (ex.getMessage().indexOf("invalid username/password") == -1) {
                throw ex;
            }
			failure = true;
		}
		
		if (!failure) {
		    throw new TestErrorException("Authentication did not fail as expected.");
		}
	}
}
