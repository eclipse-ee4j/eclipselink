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
 *     dclarke - Dynamic Persistence
 *       http://wiki.eclipse.org/EclipseLink/Development/Dynamic 
 *       (https://bugs.eclipse.org/bugs/show_bug.cgi?id=200045)
 *     mnorman - tweaks to work from Ant command-line,
 *               get database properties from System, etc.
 *
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.dynamic;

//JUnit4 imports
import static junit.framework.Assert.fail;

//EclipseLink imports
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Project;
import static org.eclipse.persistence.logging.AbstractSessionLog.translateStringToLoggingLevel;

//domain-specific (testing) imports
import static org.eclipse.persistence.testing.framework.junit.JUnitTestCaseHelper.DB_DRIVER_KEY;
import static org.eclipse.persistence.testing.framework.junit.JUnitTestCaseHelper.DB_PLATFORM_KEY;
import static org.eclipse.persistence.testing.framework.junit.JUnitTestCaseHelper.DB_PWD_KEY;
import static org.eclipse.persistence.testing.framework.junit.JUnitTestCaseHelper.DB_URL_KEY;
import static org.eclipse.persistence.testing.framework.junit.JUnitTestCaseHelper.DB_USER_KEY;
import static org.eclipse.persistence.testing.framework.junit.JUnitTestCaseHelper.LOGGING_LEVEL_KEY;

public class DynamicTestingHelper {

    public final static String DEFAULT_LOG_LEVEL = SessionLog.OFF_LABEL;

    // test fixtures
    public static String username = null;
    public static String password = null;
    public static String url = null;
    public static String driver = null;
    public static String platform = null;
    public static int logLevel = SessionLog.OFF;
    static {
        username = System.getProperty(DB_USER_KEY);
        password = System.getProperty(DB_PWD_KEY);
        url = System.getProperty(DB_URL_KEY);
        driver = System.getProperty(DB_DRIVER_KEY);
        platform = System.getProperty(DB_PLATFORM_KEY);
        logLevel = translateStringToLoggingLevel(
            System.getProperty(LOGGING_LEVEL_KEY, DEFAULT_LOG_LEVEL));
    }

    public static DatabaseSession createEmptySession() {
        Project project = new Project(new DatabaseLogin());
        return project.createDatabaseSession();
    }

    public static DatabaseLogin createLogin() {
        DatabaseLogin login = new DatabaseLogin();
        login.setUserName(username);
        login.setPassword(password);
        login.setConnectionString(url);
        login.setDriverClassName(driver);
        login.setPlatformClassName(platform);
        return login;
    }
    
    public static DatabaseSession createSession() {
        if (username == null) {
            fail("error retrieving database username");
        }
        if (password == null) {
            fail("error retrieving database password");
        }
        if (url == null) {
            fail("error retrieving database url");
        }
        Project project = new Project(createLogin());
        DatabaseSession session = project.createDatabaseSession();
        if (SessionLog.OFF == logLevel) {
            session.dontLogMessages();
        }
        else {
            session.setLogLevel(logLevel); 
        }
        return session;
    }

}