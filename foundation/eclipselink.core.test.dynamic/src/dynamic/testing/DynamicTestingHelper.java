/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     dclarke - Dynamic Persistence INCUBATION - Enhancement 200045
 *     			 http://wiki.eclipse.org/EclipseLink/Development/Dynamic
 *     mnorman - tweaks to work from Ant command-line,
 *               et database properties from System, etc.
 *     
 * This code is being developed under INCUBATION and is not currently included 
 * in the automated EclipseLink build. The API in this code may change, or 
 * may never be included in the product. Please provide feedback through mailing 
 * lists or the bug database.
 ******************************************************************************/
package dynamic.testing;

//JUnit4 imports
import static junit.framework.Assert.fail;

//EclipseLink imports
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Project;
import static org.eclipse.persistence.logging.AbstractSessionLog.translateStringToLoggingLevel;

public class DynamicTestingHelper {

    public final static String DATABASE_USERNAME_KEY = "db.user";
    public final static String DATABASE_PASSWORD_KEY = "db.pwd";
    public final static String DATABASE_URL_KEY = "db.url";
    public final static String DATABASE_DRIVER_KEY = "db.driver";
    public final static String DEFAULT_DATABASE_DRIVER = "com.mysql.jdbc.Driver";
    public final static String DATABASE_PLATFORM_KEY = "db.platform";
    public final static String DEFAULT_DATABASE_PLATFORM =
        "org.eclipse.persistence.platform.database.MySQLPlatform";
    public final static String LOG_LEVEL_KEY = "logging.level";
    public final static String DEFAULT_LOG_LEVEL = SessionLog.OFF_LABEL;

    // test fixtures
    public static String username = null;
    public static String password = null;
    public static String url = null;
    public static String driver = null;
    public static String platform = null;
    public static int logLevel = SessionLog.OFF;
    static {
        username = System.getProperty(DATABASE_USERNAME_KEY);
        password = System.getProperty(DATABASE_PASSWORD_KEY);
        url = System.getProperty(DATABASE_URL_KEY);
        driver = System.getProperty(DATABASE_DRIVER_KEY, DEFAULT_DATABASE_DRIVER);
        platform = System.getProperty(DATABASE_PLATFORM_KEY, DEFAULT_DATABASE_PLATFORM);
        logLevel = translateStringToLoggingLevel(
            System.getProperty(LOG_LEVEL_KEY, DEFAULT_LOG_LEVEL));
    }

    public static DatabaseSession createEmptySession() {
        Project project = new Project(new DatabaseLogin());
        return project.createDatabaseSession();
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
        DatabaseLogin login = new DatabaseLogin();
        login.setUserName(username);
        login.setPassword(password);
        login.setConnectionString(url);
        login.setDriverClassName(driver);
        login.setPlatformClassName(platform);
        Project project = new Project (login);
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