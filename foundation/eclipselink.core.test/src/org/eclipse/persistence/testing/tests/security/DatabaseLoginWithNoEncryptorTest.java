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
package org.eclipse.persistence.testing.tests.security;

import java.util.Properties;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.eclipse.persistence.sessions.Session;

/**
 * Tests the database login when no encryption object has been initialized via
 * the setPassword() or setEncryptedPassword() methods.
 * 
 * Fix for bug 2700529
 * 
 * @author Guy Pelletier
 */
public class DatabaseLoginWithNoEncryptorTest extends AutoVerifyTestCase {
    boolean exceptionOccurred;
    DatabaseSession mySession;
  Session originalSession = null;

    public DatabaseLoginWithNoEncryptorTest() {
        setDescription("Test the login without making a set password call, i.e. no Securable object initialized");
    }

    public void reset() {
        if ((mySession != null) && mySession.isConnected()) {
            mySession.logout();// If session is logged in, log it out
        }

        SessionManager.getManager().getSessions().remove(mySession);
        mySession = null;
    }

    protected void setup() throws Throwable {
    exceptionOccurred = false;

    originalSession = getSession();
    
    DatabaseLogin login = new DatabaseLogin();
    login.setPlatform(originalSession.getLogin().getPlatform());
    //this change for making tests pass on oc4j server, suggested by James
    /*
    login.setDriverClassName(originalSession.getLogin().getDriverClassName());
    login.setConnectionString(originalSession.getLogin().getConnectionString());
    */
    login.setConnector(originalSession.getLogin().getConnector());
    Properties properties = new Properties();
	if (originalSession.getLogin().getUserName() != null){
		properties.setProperty("user", originalSession.getLogin().getUserName());
	}
    if (originalSession.getLogin().getPassword() != null){
		properties.setProperty("password", originalSession.getLogin().getPassword());
    }
    
        login.setProperties(properties);

        mySession = new Project(login).createDatabaseSession();
        mySession.dontLogMessages();
    }

    public void test() {
        try {
            mySession.login();
        } catch (Exception e) {
            exceptionOccurred = true;
        }
    }

    protected void verify() {
        if (exceptionOccurred) {
            throw new TestErrorException("Exception was caught on login");
        } else if (!mySession.isConnected()) {
            throw new TestWarningException("Session was not connected, but no exception was thrown");
        }
    }
}
