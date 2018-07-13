/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.framework.junit;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.persistence.config.PersistenceUnitProperties;

public class JUnitTestCaseHelper {

    public static final String DB = "db";
    public static final String DB_DRIVER_KEY = "db.driver";
    public static final String DB_URL_KEY = "db.url";
    public static final String DB_USER_KEY = "db.user";
    public static final String DB_PWD_KEY = "db.pwd";
    public static final String DB_PLATFORM_KEY = "db.platform";
    /** EISConnectionSpec class name used to connect to the NoSQL data source (used in MongoDB test setup). */
    public static final String DB_SPEC_KEY = "db.spec";
    /** EclipseLink logging level. */
    public static final String LOGGING_LEVEL_KEY = "eclipselink.logging.level";

    public static final String TEST_PROPERTIES_FILE_KEY = "test.properties";
    public static final String TEST_PROPERTIES_FILE_DEFAULT = "test.properties";

    public static Map propertiesMap = null;
    public static Map persistencePropertiesTestMap = new HashMap();

    public static Properties propertiesFromFile = null;

    // Maps puName to properties
    //   "composite-advanced" -> Map,
    //   "xml-composite-advanced" -> Map
    public static Map<String, Map> puPropertiesMap = new HashMap();

    // Maps dbIndex to properties
    //   "2" -> Map,
    //   "3" -> Map
    public static Map<String, Map> dbPropertiesMap = new HashMap();

    /**
     * If this property is set to "true" either in System properties or property file then
     * db properties corresponding to dbIndex that was not found will be substituted for default properties.
     * If ALL of the properties db2.user, db2.pwd, db2.url, db2.driver, db2.platform are not found
     * then they are overridden with default properties (produced by getDatabaseProperties());
     * If ALL of the properties db3.user, db3.pwd, db3.url, db3.driver, db3.platform are not found
     * then they are overridden with default properties (produced by getDatabaseProperties());
     * Setting the property to "true" allows to run session  test on a single data base (it will be used by all member sessions).
     **/
    public static final String SINGLE_DB = "single.db";

    public static Boolean shouldUseSingleDb;

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
        //persistencePropertiesTestMap.put(PersistenceUnitProperties.BATCH_WRITING, "ExaLogic");
        //persistencePropertiesTestMap.put(PersistenceUnitProperties.PROFILER, "oracle.toplink.exalogic.tuning.TuningAgent");
    }

    /**
     * Get common properties (including database properties) from System, for unavailable ones, read from test.properties file.
     * The location of properties file can be given by system property <tt>test.properties</tt>.
     * The default location is "test.properties" file in current directory.
     */
    public static Map<String, String> getDatabaseProperties() {
        if (propertiesMap == null) {
            propertiesMap = createDatabaseProperties(null);
            propertiesMap.putAll(persistencePropertiesTestMap);
        }
        return propertiesMap;
    }

    public static Map<String, String> getDatabaseProperties(String puName) {
        Map puProperties = null;
        if (puName != null && puName.length() > 0) {
            puProperties = puPropertiesMap.get(puName);
            if (puProperties == null) {
                if (puName.equals("composite-advanced") || puName.equals("xml-composite-advanced") || puName.equals("xml-extended-composite-advanced")) {
                    String prefix = puName;
                    if (puName.equals("xml-extended-composite-advanced")) {
                        prefix = "xml-composite-advanced";
                    }
                    String[] sessions = {"member_1", "member_2", "member_3"};
                    for(int i=0; i < sessions.length; i++) {
                        sessions[i] = prefix + "-" + sessions[i];
                    }
                    puProperties = createCompositeProperties(sessions);
                }
                if (puProperties != null) {
                    puPropertiesMap.put(puName, puProperties);
                }
            }
        }

        if (puProperties != null) {
            return puProperties;
        } else {
            return getDatabaseProperties();
        }
    }

    /**
     * Get the property value from the {@link System} or from the properties file as a fall back option.
     * @param key The name of the property.
     * @return The {@link String} value of the property.
     */
    public static String getProperty(final String key) {
        final String property = System.getProperty(key);
        return property != null ? property : JUnitTestCaseHelper.getPropertyFromFile(key);
    }

    /**
     * Get the property value from the properties file.
     * @param key The name of the property.
     * @return The {@link String} value of the property.
     */
    private static String getPropertyFromFile(final String key) {
        if (propertiesFromFile == null) {
            createPropertiesFromFile();
        }
        return (String) propertiesFromFile.get(key);
    }

    // No index corresponds to db.user, db.pwd,...
    // index = 2 -> db2.user, db2.pwd,... etc
    /**
     * Create the database properties {@link Map}.
     * @param dbIndex The properties set index to append to the {@code "db"} prefix.
     * @return The database properties {@link Map} containing properties from the {@link System} or from the properties
     *         file as a fall back option.
     */
    static Map<String, String> createDatabaseProperties(final String dbIndex) {
        final boolean addLoggingLevel = dbIndex == null || dbIndex.length() == 0;

        final String db_driver_key = insertIndex(DB_DRIVER_KEY, dbIndex);
        final String db_url_key = insertIndex(DB_URL_KEY, dbIndex);
        final String db_user_key = insertIndex(DB_USER_KEY, dbIndex);
        final String db_pwd_key = insertIndex(DB_PWD_KEY, dbIndex);
        final String db_platform_key = insertIndex(DB_PLATFORM_KEY, dbIndex);

        final String dbDriver = getProperty(db_driver_key);
        final String dbUrl = getProperty(db_url_key);
        final String dbUser = getProperty(db_user_key);
        final String dbPwd = getProperty(db_pwd_key);
        final String platform = getProperty(db_platform_key);
        final String logLevel = getProperty(LOGGING_LEVEL_KEY);

        final Map<String, String> properties = new HashMap<>();

        if (dbDriver != null) {
            properties.put(PersistenceUnitProperties.JDBC_DRIVER, dbDriver);
        }
        if (dbUrl != null) {
            properties.put(PersistenceUnitProperties.JDBC_URL, dbUrl);
        }
        if (dbUser != null) {
            properties.put(PersistenceUnitProperties.JDBC_USER, dbUser);
        }
        if (dbPwd != null) {
            properties.put(PersistenceUnitProperties.JDBC_PASSWORD, dbPwd);
        }
        if (platform != null) {
            properties.put(PersistenceUnitProperties.TARGET_DATABASE, platform);
        }
        if (addLoggingLevel) {
            if (logLevel != null) {
                properties.put(PersistenceUnitProperties.LOGGING_LEVEL, logLevel);
            }
        }
        return properties;
    }

    public static Map createCompositeProperties(String[] sessions) {
        Map properties = new HashMap();

        Map compositeMap = new HashMap();
        properties.put(PersistenceUnitProperties.COMPOSITE_UNIT_PROPERTIES, compositeMap);
        Map defaultProperties = getDatabaseProperties();
        String logLevel = (String)defaultProperties.get(PersistenceUnitProperties.LOGGING_LEVEL);
        if (logLevel != null) {
            properties.put(PersistenceUnitProperties.LOGGING_LEVEL, logLevel);
        }
        // the first session uses db.user, db.pwd ...
        compositeMap.put(sessions[0], defaultProperties);
        for (int i = 1; i < sessions.length; i++) {
            String dbIndex = Integer.toString(i + 1);
            // the second session uses db2.user, db2.pwd ...
            Map sessionProperties = getDatabasePropertiesForIndex(dbIndex);
            if (sessionProperties.isEmpty() && shouldUseSingleDb()) {
                // if non of these properties defined, then use firstSessionProperties
                sessionProperties = defaultProperties;
            }
            if (!sessionProperties.isEmpty()) {
                compositeMap.put(sessions[i], sessionProperties);
            }
        }
        return properties;
    }

    public static String insertIndex(String key, String index) {
        if (index == null || index.length() == 0) {
            return key;
        } else {
            String suffix = key.substring(DB.length(), key.length());
            return DB + index + suffix;
        }
    }

    public static Map<String, String> getDatabasePropertiesForIndex(String dbIndex) {
        if (dbIndex != null && dbIndex.length() > 0) {
            Map dbProperties = dbPropertiesMap.get(dbIndex);
            if (dbProperties == null) {
                dbProperties = createDatabaseProperties(dbIndex);
                dbPropertiesMap.put(dbIndex, dbProperties);
            }
            return dbProperties;
        } else {
            throw new RuntimeException("dbIndex is null or an empty String");
        }
    }

    @SuppressWarnings("deprecation")
    public static void createPropertiesFromFile() {
        propertiesFromFile = new Properties();
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
                propertiesFromFile.load(url.openStream());
            } catch (java.io.IOException exception){
               throw new  RuntimeException("Error loading " + testPropertiesFile.getName() + ".", exception);
            }
        }
    }

    public static boolean shouldUseSingleDb() {
        if (shouldUseSingleDb == null) {
            shouldUseSingleDb = Boolean.TRUE;
            String property = System.getProperty(SINGLE_DB);
            if (property == null) {
                property = getPropertyFromFile(SINGLE_DB);
            }
            if (property != null) {
                if (property.toUpperCase().equals("FALSE")) {
                    shouldUseSingleDb = Boolean.FALSE;
                }
            }
        }
        return shouldUseSingleDb;
    }

}
