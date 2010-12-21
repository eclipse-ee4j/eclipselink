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
package org.eclipse.persistence.testing.framework.junit;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.persistence.config.PersistenceUnitProperties;

public class JUnitTestCaseHelper {

    public static final String DB_DRIVER_KEY = "db.driver";
    public static final String DB_URL_KEY = "db.url";
    public static final String DB_USER_KEY = "db.user";
    public static final String DB_PWD_KEY = "db.pwd";
    public static final String DB_PLATFORM_KEY = "db.platform";
    public static final String LOGGING_LEVEL_KEY = "eclipselink.logging.level";

    public static final String TEST_PROPERTIES_FILE_KEY = "test.properties";
    public static final String TEST_PROPERTIES_FILE_DEFAULT = "test.properties";

    public static Map propertiesMap = null;
    public static Map persistencePropertiesTestMap = new HashMap();
    
    static {
        // These following properties used for property processing testing.
        // Some (or all) of them may override persistence properties.
        // Used by EntityManagerJUnitTestSuite.testPersistenceProperties()
        persistencePropertiesTestMap.put(PersistenceUnitProperties.JDBC_READ_CONNECTIONS_SHARED, "false");
        persistencePropertiesTestMap.put(PersistenceUnitProperties.JDBC_WRITE_CONNECTIONS_INITIAL, "0");
        persistencePropertiesTestMap.put(PersistenceUnitProperties.JDBC_WRITE_CONNECTIONS_MIN, "4");
        persistencePropertiesTestMap.put(PersistenceUnitProperties.JDBC_WRITE_CONNECTIONS_MAX, "9");
        persistencePropertiesTestMap.put(PersistenceUnitProperties.JDBC_READ_CONNECTIONS_MIN, "4");
        persistencePropertiesTestMap.put(PersistenceUnitProperties.JDBC_READ_CONNECTIONS_MAX, "4");
        persistencePropertiesTestMap.put(PersistenceUnitProperties.BATCH_WRITING_SIZE, "150");
    }

    /**
     * Get common properties (including database properties) from System, for unavailable ones, read from test.properties file.
     * The location of properties file can be given by system property <tt>test.properties</tt>.
     * The default location is "test.properties" file in current directory. 
     */
    @SuppressWarnings("deprecation")
    public static Map<String, String> getDatabaseProperties(){
        if (propertiesMap == null){
            String dbDriver = System.getProperty(DB_DRIVER_KEY);
            String dbUrl = System.getProperty(DB_URL_KEY);
            String dbUser = System.getProperty(DB_USER_KEY);
            String dbPwd = System.getProperty(DB_PWD_KEY);
            String platform = System.getProperty(DB_PLATFORM_KEY);
            String logLevel = System.getProperty(LOGGING_LEVEL_KEY);

            //if not all of these properties available from System, read unavailable ones from test.properties file
            if ((dbDriver == null) || (dbUrl == null) || (dbUser == null) || (dbPwd == null) || (platform == null) || (logLevel == null))
            {
                Properties properties = new Properties();
                File testPropertiesFile 
                    = new File(System.getProperty(TEST_PROPERTIES_FILE_KEY, TEST_PROPERTIES_FILE_DEFAULT));
                URL url = null;
                if (testPropertiesFile.exists()) {
                    try {
                        url = testPropertiesFile.toURL();
                    } catch (MalformedURLException exception) {
                        throw new RuntimeException("Error loading " + testPropertiesFile.getName() + ".", exception);
                    }
                } else {
                    // Load as a resource if from a jar.
                    url = JUnitTestCaseHelper.class.getResource("/" + System.getProperty(TEST_PROPERTIES_FILE_KEY, TEST_PROPERTIES_FILE_DEFAULT));
                }
                if (url != null){
                    try{
                        properties.load(url.openStream());
                    } catch (java.io.IOException exception){
                       throw new  RuntimeException("Error loading " + testPropertiesFile.getName() + ".", exception);
                    }
                }
                if (dbDriver == null) {
                    dbDriver = (String) properties.get(DB_DRIVER_KEY);
                }
                if (dbUrl == null) {
                    dbUrl = (String) properties.get(DB_URL_KEY);
                }
                if (dbUser == null) {
                    dbUser = (String) properties.get(DB_USER_KEY);
                }
                if (dbPwd == null) {
                    dbPwd = (String) properties.get(DB_PWD_KEY);
                }
                if (platform == null) {
                    platform = (String) properties.get(DB_PLATFORM_KEY);
                }
                if (logLevel == null) {
                    logLevel = (String) properties.get(PersistenceUnitProperties.LOGGING_LEVEL);
                }
            }

            propertiesMap = new HashMap();
            if (dbDriver != null) {
                propertiesMap.put(PersistenceUnitProperties.JDBC_DRIVER, dbDriver);
            }
            if (dbUrl != null) {
                propertiesMap.put(PersistenceUnitProperties.JDBC_URL, dbUrl);
            }
            if (dbUser != null) {
                propertiesMap.put(PersistenceUnitProperties.JDBC_USER, dbUser);
            }
            if (dbPwd != null) {
                propertiesMap.put(PersistenceUnitProperties.JDBC_PASSWORD, dbPwd);
            }
            if (logLevel != null) {
                propertiesMap.put(PersistenceUnitProperties.LOGGING_LEVEL, logLevel);
            }
            if (platform != null) {
                propertiesMap.put(PersistenceUnitProperties.TARGET_DATABASE, platform);
            }
            propertiesMap.putAll(persistencePropertiesTestMap);
        }
        return propertiesMap;
    }
    
}
