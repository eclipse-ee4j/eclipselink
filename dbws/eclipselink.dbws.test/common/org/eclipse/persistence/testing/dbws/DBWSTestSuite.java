/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/

package org.eclipse.persistence.testing.dbws;

// Javase imports
import java.util.Properties;

// Java extension imports

// JUnit imports
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

// Parameterized Before JUnit extension
import junit.extensions.pb4.ParameterizedBeforeRunner;

// EclipseLink imports
import org.eclipse.persistence.internal.xr.XRServiceAdapter;
import org.eclipse.persistence.platform.xml.XMLComparer;
import org.eclipse.persistence.platform.xml.XMLParser;
import org.eclipse.persistence.platform.xml.XMLPlatform;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;

@RunWith(ParameterizedBeforeRunner.class)
public class DBWSTestSuite {

    protected final static String DATABASE_USERNAME_KEY = "login.username";
    protected final static String DATABASE_PASSWORD_KEY = "login.password";
    protected final static String DATABASE_URL_KEY = "login.databaseURL";
    protected final static String DATABASE_DRIVER_KEY = "login.driverClass";
    protected final static String DATABASE_PLATFORM_KEY = "login.databaseplatform";
    protected static String DEFAULT_DATABASE_USERNAME = "scott";
    protected static String DEFAULT_DATABASE_PASSWORD = "tiger";
    protected static String DEFAULT_DATABASE_URL = "jdbc:oracle:thin:@localhost:1521:ORCL";
    protected static String DEFAULT_DATABASE_DRIVER = "oracle.jdbc.driver.OracleDriver";
    protected static String DEFAULT_DATABASE_PLATFORM =
        "org.eclipse.persistence.platform.database.oracle.OraclePlatform";

    // test fixtures
    public static XMLComparer comparer = new XMLComparer();
    public static XMLPlatform xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
    public static XMLParser xmlParser = xmlPlatform.newXMLParser();
    public static XRServiceAdapter xrService = null;
    @BeforeClass
    public static void setUpDBWSService(Properties p) {
        TestDBWSFactory serviceFactory = new TestDBWSFactory();
        serviceFactory.username = p.getProperty(DATABASE_USERNAME_KEY,
            DEFAULT_DATABASE_USERNAME);
        serviceFactory.password = p.getProperty(DATABASE_PASSWORD_KEY,
            DEFAULT_DATABASE_PASSWORD);
        serviceFactory.url = p.getProperty(DATABASE_URL_KEY,
            DEFAULT_DATABASE_URL);
        serviceFactory.driver = p.getProperty(DATABASE_DRIVER_KEY,
            DEFAULT_DATABASE_DRIVER);
        serviceFactory.platform = p.getProperty(DATABASE_PLATFORM_KEY,
            DEFAULT_DATABASE_PLATFORM);
        xrService = serviceFactory.buildService();
    }

}
