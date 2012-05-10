/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      dclarke - initial 
 ******************************************************************************/
package org.eclipse.persistence.jpars.test.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import org.eclipse.persistence.config.PersistenceUnitProperties;

/**
 * Helper class that will load persistence unit overrides from a properties file
 * in both the current running folder and the current user's home folder. The
 * goal is to enable developers and users of the example to customize its
 * behaviour without having to modify the source of the example.
 */
public class ExamplePropertiesLoader {

    public static final String DEFAULT_FILENAME = "test.properties";
    public static final String ECLIPSELINK_TEST_DB_PROP_PREFIX = "db.";
    public static final String JPA_DB_PROP_PREFIX = "javax.persistence.jdbc.";
    public static final String DB_DRIVER_KEY = "db.driver";
    public static final String DB_URL_KEY = "db.url";
    public static final String DB_USER_KEY = "db.user";
    public static final String DB_PWD_KEY = "db.pwd";
    public static final String DB_PLATFORM_KEY = "db.platform";
    public static final String LOGGING_LEVEL_KEY = "eclipselink.logging.level";
    
    /**
     * 
     * @param properties
     */
    public static void loadProperties(Map<String, Object> properties) {
        String fileName = System.getProperty(DEFAULT_FILENAME);
        if (fileName == null){
            fileName = DEFAULT_FILENAME;
        }
        loadProperties(properties, fileName);
    }

    /**
     * 
     * @param properties
     */
    public static void loadProperties(Map<String, Object> properties, String filename) {
        loadProperties(properties, new File(filename));

        String home = System.getProperty("user.home");
        loadProperties(properties, new File(home + System.getProperty("file.separator") + filename));

        for (Object key : System.getProperties().keySet()) {
            String keyName = (String) key;

            if (keyName.startsWith("javax.persistence") || keyName.startsWith("eclipselink")) {
                String value = System.getProperty(keyName);
                properties.put(keyName, value);
            }
        }
    }

    /**
     * 
     * @param properties
     * @param filePath
     */
    public static void loadProperties(Map<String, Object> properties, File file) {
        try {
            if (file.exists()) {
                Properties exampleProps = new Properties();
                InputStream in = new FileInputStream(file);
                exampleProps.load(in);
                in.close();

                for (Map.Entry<Object, Object> entry : exampleProps.entrySet()) {
                    if (((String)entry.getKey()).startsWith(ECLIPSELINK_TEST_DB_PROP_PREFIX)){
                        properties.put(((String) entry.getKey()).replace(ECLIPSELINK_TEST_DB_PROP_PREFIX, JPA_DB_PROP_PREFIX), entry.getValue());
                    } else {
                        properties.put((String) entry.getKey(), entry.getValue());
                    }
                }
            }
            String dbDriver = System.getProperty(DB_DRIVER_KEY);
            String dbUrl = System.getProperty(DB_URL_KEY);
            String dbUser = System.getProperty(DB_USER_KEY);
            String dbPwd = System.getProperty(DB_PWD_KEY);
            String platform = System.getProperty(DB_PLATFORM_KEY);
            String logLevel = System.getProperty(LOGGING_LEVEL_KEY);

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
            if (logLevel != null) {
                properties.put(PersistenceUnitProperties.LOGGING_LEVEL, logLevel);
            }
        } catch (Exception e) {
            // ignore
        }
    }
}
