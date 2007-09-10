/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.framework.junit;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;

import org.eclipse.persistence.jpa.config.PersistenceUnitProperties;

public class JUnitTestCaseHelper {

    public static final String TEST_PROPERTIES_FILE_KEY = "test.properties";
    public static final String TEST_PROPERTIES_FILE_DEFAULT = "test.properties";
	
	public static Map propertiesMap = null;
    public static Map persistencePropertiesTestMap = new HashMap();
    
    static {
        // These following properties used for property processing testing.
        // Some (or all) of them may override persistence properties.
        // Used by EntityManagerJUnitTestSuite.testPersistenceProperties()
        persistencePropertiesTestMap.put(PersistenceUnitProperties.JDBC_READ_CONNECTIONS_SHARED, "false");
        persistencePropertiesTestMap.put(PersistenceUnitProperties.JDBC_WRITE_CONNECTIONS_MIN, "4");
        persistencePropertiesTestMap.put(PersistenceUnitProperties.JDBC_WRITE_CONNECTIONS_MAX, "9");
        persistencePropertiesTestMap.put(PersistenceUnitProperties.JDBC_READ_CONNECTIONS_MIN, "4");
        persistencePropertiesTestMap.put(PersistenceUnitProperties.JDBC_READ_CONNECTIONS_MAX, "4");
    }
	
    /**
     * Read common properties (including database properties) from test.properties file.
     * The location of properties file can be given by system property <tt>test.properties</tt>.
     * The default location is "test.properties" file in current directory. 
     */
    public static Map getDatabaseProperties(){
        if (propertiesMap == null){
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
            }
            propertiesMap = new HashMap();
            if (url != null){
                try{
                    properties.load(url.openStream());
                } catch (java.io.IOException exception){
                   throw new  RuntimeException("Error loading " + testPropertiesFile.getName() + ".", exception);
                }
                
                String dbDriver = (String) properties.get("db.driver");
                String dbUrl = (String) properties.get("db.url");
                String dbUser = (String) properties.get("db.user");
                String dbPwd = (String) properties.get("db.pwd");
                String logLevel = (String) properties.get("eclipselink.logging.level");
                
                if (dbDriver != null) {
                    propertiesMap.put("eclipselink.jdbc.driver", dbDriver);
                }
                if (dbUrl != null) {
                    propertiesMap.put("eclipselink.jdbc.url", dbUrl);
                }
                if (dbUser != null) {
                    propertiesMap.put("eclipselink.jdbc.user", dbUser);
                }
                if (dbPwd != null) {
                    propertiesMap.put("eclipselink.jdbc.password", dbPwd);
                }
                if (logLevel != null) {
                    propertiesMap.put("eclipselink.logging.level", logLevel);
                }
            }
            propertiesMap.putAll(persistencePropertiesTestMap);
        }
        return propertiesMap;
    }
    
}
