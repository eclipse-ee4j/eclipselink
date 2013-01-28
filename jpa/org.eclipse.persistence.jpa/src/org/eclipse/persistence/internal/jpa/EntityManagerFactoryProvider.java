/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     12/24/2012-2.5 Guy Pelletier 
 *       - 389090: JPA 2.1 DDL Generation Support
 *     01/08/2013-2.5 Guy Pelletier 
 *       - 389090: JPA 2.1 DDL Generation Support
 *     01/11/2013-2.5 Guy Pelletier 
 *       - 389090: JPA 2.1 DDL Generation Support
 *     01/16/2013-2.5 Guy Pelletier 
 *       - 389090: JPA 2.1 DDL Generation Support
 *     01/24/2013-2.5 Guy Pelletier 
 *       - 389090: JPA 2.1 DDL Generation Support
 *     02/04/2013-2.5 Guy Pelletier 
 *       - 389090: JPA 2.1 DDL Generation Support
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

import javax.persistence.PersistenceException;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.config.TargetDatabase;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.internal.jpa.EntityManagerSetupImpl.TableCreationType;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;

/**
 * This is a helper/impl class for the EclipseLink EJB 3.0 provider
 * The default constructor can be used to build the provider by reflection, after which it can
 * be used to create EntityManagerFactories
 */
public class EntityManagerFactoryProvider { 
    public static final HashMap<String, EntityManagerSetupImpl> emSetupImpls = new HashMap<String, EntityManagerSetupImpl>();
    
    // TEMPORARY - WILL BE REMOVED.
    // Used to warn users about deprecated property name and suggest the valid name.
    // TEMPORARY the old property names will be translated to the new ones and processed.
    protected static final String oldPropertyNames[][] = {
        {PersistenceUnitProperties.CONNECTION_POOL_MAX, "eclipselink.max-write-connections"},
        {PersistenceUnitProperties.CONNECTION_POOL_MIN, "eclipselink.min-write-connections"},
        {PersistenceUnitProperties.CONNECTION_POOL_MAX, "eclipselink.max-read-connections"},
        {PersistenceUnitProperties.CONNECTION_POOL_MIN, "eclipselink.min-read-connections"},
        {PersistenceUnitProperties.CONNECTION_POOL_MAX, "eclipselink.connections.max"},
        {PersistenceUnitProperties.CONNECTION_POOL_MIN, "eclipselink.connections.min"},
        {PersistenceUnitProperties.CONNECTION_POOL_INITIAL, "eclipselink.connections.initial"},
        {PersistenceUnitProperties.CONNECTION_POOL_WAIT, "eclipselink.connections.wait"},
        {PersistenceUnitProperties.CONNECTION_POOL_MAX, "eclipselink.write-connections.max"},
        {PersistenceUnitProperties.CONNECTION_POOL_MIN, "eclipselink.write-connections.min"},
        {PersistenceUnitProperties.CONNECTION_POOL_INITIAL, "eclipselink.write-connections.initial"},
        {PersistenceUnitProperties.CONNECTION_POOL_MAX, "eclipselink.read-connections.max"},
        {PersistenceUnitProperties.CONNECTION_POOL_MIN, "eclipselink.read-connections.min"},
        {PersistenceUnitProperties.CONNECTION_POOL_SHARED, "eclipselink.read-connections.shared"},
        {PersistenceUnitProperties.CONNECTION_POOL_INITIAL, "eclipselink.read-connections.initial"},
        {PersistenceUnitProperties.JDBC_BIND_PARAMETERS, "eclipselink.bind-all-parameters"},
        {PersistenceUnitProperties.TARGET_DATABASE, "eclipselink.platform.class.name"},
        {PersistenceUnitProperties.TARGET_SERVER, "eclipselink.server.platform.class.name"},
        {PersistenceUnitProperties.CACHE_SIZE_DEFAULT, "eclipselink.cache.default-size"},
        {PersistenceUnitProperties.JDBC_USER , "eclipselink.jdbc.user"},
        {PersistenceUnitProperties.JDBC_DRIVER ,"eclipselink.jdbc.driver"},
        {PersistenceUnitProperties.JDBC_URL , "eclipselink.jdbc.url"},
        {PersistenceUnitProperties.JDBC_PASSWORD , "eclipselink.jdbc.password"},
        {PersistenceUnitProperties.WEAVING , "persistence.tools.weaving"},
        {PersistenceUnitProperties.LOGGING_LEVEL + "." + SessionLog.METAMODEL, PersistenceUnitProperties.LOGGING_LEVEL + ".jpa_" + SessionLog.METAMODEL},
        {PersistenceUnitProperties.LOGGING_LEVEL + "." + SessionLog.METADATA, PersistenceUnitProperties.LOGGING_LEVEL + ".ejb_or_" + SessionLog.METADATA}
    };

    /**
     * Add an EntityManagerSetupImpl to the cached list
     * These are used to ensure all persistence units that are the same get the same underlying session
     * @param name
     * @param setup
     */
    public static void addEntityManagerSetupImpl(String name, EntityManagerSetupImpl setup){
        if (name == null){
            emSetupImpls.put("", setup);
        }
        emSetupImpls.put(name, setup);
    }

    protected static String addFileSeperator(String appLocation) {
        int strLength = appLocation.length();
        if (appLocation.substring(strLength -1, strLength).equals(File.separator)) {
            return appLocation;
        } else {
            return appLocation + File.separator;
        }
    }
    
    /**
     * Calls the appropriate create,replace or alter SchemaManager api.  
     * @param mgr
     * @param ddlType - ddl operation to be performed
     */
    protected static void generateDefaultTables(SchemaManager mgr, TableCreationType ddlType) {          
        if (ddlType == null || ddlType == TableCreationType.CREATE) {
            mgr.createDefaultTables(true); 
        } else if (ddlType == TableCreationType.DROP) {
            mgr.dropDefaultTables();
        } else if  (ddlType == TableCreationType.DROP_AND_CREATE) {
            mgr.replaceDefaultTables(true, false, true); 
        } else if (ddlType == TableCreationType.EXTEND) { 
            mgr.extendDefaultTables(true);
        }
    }

    public static String getConfigPropertyAsString(String propertyKey, Map overrides){
        String value = null;
        if (overrides != null){
            value = (String)overrides.get(propertyKey);
        }
        if (value == null){
            value = System.getProperty(propertyKey);
        }
        
        return value;
    }
    
    /**
     * Check the provided map for an object with the given key.  If that object is not available, check the
     * System properties.  If it is not available from either location, return the default value.
     * @param propertyKey 
     * @param map 
     * @param defaultValue 
     * @return 
     */
    public static String getConfigPropertyAsString(String propertyKey, Map overrides, String defaultValue){
    	String value = getConfigPropertyAsString(propertyKey, overrides);
        if (value == null){
            value = defaultValue;
        }
        return value;
    }

    protected static String getConfigPropertyAsStringLogDebug(String propertyKey, Map overrides, AbstractSession session) {
        return (String)getConfigPropertyLogDebug(propertyKey, overrides, session);
    }
    
    protected static String getConfigPropertyAsStringLogDebug(String propertyKey, Map overrides, AbstractSession session, boolean useSystemAsDefault) {
        return (String)getConfigPropertyLogDebug(propertyKey, overrides, session, useSystemAsDefault);
    }
    
    protected static String getConfigPropertyAsStringLogDebug(String propertyKey, Map overrides, String defaultValue, AbstractSession session){
        String value = getConfigPropertyAsStringLogDebug(propertyKey, overrides, session);
        if (value == null){
            value = defaultValue;
            session.log(SessionLog.FINEST, SessionLog.PROPERTIES, "property_value_default", new Object[]{propertyKey, value});
        }
        return value;
    }
    
    protected static Object getConfigPropertyLogDebug(String propertyKey, Map overrides, AbstractSession session){
        return getConfigPropertyLogDebug(propertyKey, overrides, session, true);
    }
    
    protected static Object getConfigPropertyLogDebug(String propertyKey, Map overrides, AbstractSession session, boolean useSystemAsDefault){
        Object value = null;
        if (overrides != null){
            value = overrides.get(propertyKey);
        }
        if ((value == null) && useSystemAsDefault){
            value = System.getProperty(propertyKey);
        }
        if ((value != null) && (session !=  null)) {
            if (session.shouldLog(SessionLog.FINEST, SessionLog.PROPERTIES)) {
                String overrideValue = PersistenceUnitProperties.getOverriddenLogStringForProperty(propertyKey);;           
                Object logValue = (overrideValue == null) ? value : overrideValue;
                session.log(SessionLog.FINEST, SessionLog.PROPERTIES, "property_value_specified", new Object[]{propertyKey, logValue});
            }
        }
        
        return value;
    }
    
    public static boolean hasConfigProperty(String propertyKey, Map overrides) {
        return getConfigProperty(propertyKey, overrides) != null;
    }
    
    protected static Object getConfigProperty(String propertyKey, Map overrides){
        return getConfigProperty(propertyKey, overrides, true);
    }
    
    protected static Object getConfigProperty(String propertyKey, Map overrides, boolean useSystemAsDefault){
        Object value = null;
        if (overrides != null){
            value = overrides.get(propertyKey);
        }
        if ((value == null) && useSystemAsDefault){
            value = System.getProperty(propertyKey);
        }
        
        return value;
    }
    
    protected static Object getConfigProperty(String propertyKey, Map overrides, Object defaultObj){
        Object obj = getConfigProperty(propertyKey, overrides);
        return (obj == null) ? defaultObj : obj;
    }
    
    /**
     * Return the setup class for a given entity manager name 
     * @param emName 
     */
    public static EntityManagerSetupImpl getEntityManagerSetupImpl(String emName){
    	if (emName == null){
    		return emSetupImpls.get("");
    	}
        return emSetupImpls.get(emName);
    }

    public static Map<String, EntityManagerSetupImpl>getEmSetupImpls(){
        return emSetupImpls;
    }
    
    /**
     * Logs in to given session. If user has not specified  <code>TARGET_DATABASE</code>
     * the platform would be auto detected
     * @param session The session to login to.
     * @param properties User specified properties for the persistence unit
     */
    protected static void login(DatabaseSessionImpl session, Map properties, boolean requiresConnection) {
        String ddlGenerationTarget = getConfigPropertyAsString(PersistenceUnitProperties.SCHEMA_GENERATION_TARGET, properties);
        
        // Avoid an actual connection if we don't need one. If the user provides 
        // us with a user name and password we could connect. At minimum if they 
        // provide the platform we'll generate the DDL as if we had connected.
        if (ddlGenerationTarget != null && ddlGenerationTarget.equals(PersistenceUnitProperties.SCHEMA_SCRIPTS_GENERATION) && ! requiresConnection) {
            session.loginNoConnect();
        } else {
            String eclipselinkPlatform = (String)properties.get(PersistenceUnitProperties.TARGET_DATABASE);
            if (eclipselinkPlatform == null || eclipselinkPlatform.equals(TargetDatabase.Auto) || session.isBroker()) {
                // if user has not specified a database platform, try to detect. 
                // Will also look for jpa 2.1 schema properties.
                session.loginAndDetectDatasource();
            } else {
                session.login();
            }
        }
    }
    
    /**
     * Merge the properties from the source object into the target object.  If the property
     * exists in both objects, use the one from the target
     * @param target 
     * @param source 
     * @return the target object
     */
    public static Map mergeMaps(Map target, Map source){
        Map map = new HashMap();
        if (source != null){
            map.putAll(source);
        }

        if (target != null){
            map.putAll(target);
        }
        return map;
    }
    
    /**
     * Copies source into target, removes from target all keysToBeRemoved. 
     * @param source 
     * @param keysToBeRemoved 
     * @return the target object
     */
    public static Map removeSpecifiedProperties(Map source, Collection keysToBeRemoved){
        Map target = new HashMap();
        if (source != null){
            target.putAll(source);
            Iterator it = keysToBeRemoved.iterator();
            while(it.hasNext()) {
                target.remove(it.next());
            }
        }
        return target;
    }
    
    /**
     * target contains the entries from source with keysToBeKept. 
     * @param source 
     * @param keysToBeKept 
     * @return the target object
     */
    public static Map keepSpecifiedProperties(Map source, Collection keysToBeKept){
        Map target = new HashMap();
        if (source != null){
            target.putAll(source);
            Iterator<Map.Entry> it = source.entrySet().iterator();
            while(it.hasNext()) {
                Map.Entry entry = it.next();
                if(keysToBeKept.contains(entry.getKey())) {
                    target.put(entry.getKey(), entry.getValue());
                }
            }
        }
        return target;
    }
    
    /**
     * target is a array of two Maps
     * the first one contains specified properties;
     * the second all the rest. 
     * @param source 
     * @param keysToBeKept 
     * @return the target object
     */
    public static Map[] splitSpecifiedProperties(Map source, Collection keysToBeKept){
        HashMap in = new HashMap();
        HashMap out = new HashMap();
        Map[] target = new Map[]{in, out};
        if (source != null){
            Iterator<Map.Entry> it = source.entrySet().iterator();
            while(it.hasNext()) {
                Map.Entry entry = it.next();
                if(keysToBeKept.contains(entry.getKey())) {
                    in.put(entry.getKey(), entry.getValue());
                } else {
                    out.put(entry.getKey(), entry.getValue());
                }
            }
        }
        return target;
    }
    
    /**
     * Source Map is divided between Map[] in target.  
     * Target's i-th member contains all source's Map.Entries 
     * keys for which are in keys[i] Collection.
     * Target's size equals keys' size + 1:
     * all the source's Map.Entries not found in any of keys Collections
     * go into the last target's map.
     * @param source 
     * @param keys is array of Maps of size n
     * @return the target object is array of Maps of size n+1
     */
    public static Map[] splitProperties(Map source, Collection[] keys){
        Map[] target = new Map[keys.length + 1];
        for (int i=0; i <= keys.length; i++) {
            target[i] = new HashMap();
        }
        if (source != null){
            Iterator<Map.Entry> it = source.entrySet().iterator();
            while(it.hasNext()) {
                Map.Entry entry = it.next();
                boolean isFound = false;
                for (int i=0; i < keys.length && !isFound; i++) {
                    if (keys[i].contains(entry.getKey())) {
                        isFound = true;
                        target[i].put(entry.getKey(), entry.getValue());
                    }
                }
                if (!isFound) {
                    target[keys.length].put(entry.getKey(), entry.getValue());
                }
            }
        }
        return target;
    }
    
    /**
     * This is a TEMPORARY method that will be removed.
     * DON'T USE THIS METHOD - for internal use only.
     * @param Map m
     * @param AbstractSession session
     */
    protected static void translateOldProperties(Map m, AbstractSession session) {
        for(int i=0; i < oldPropertyNames.length; i++) {
            Object value = getConfigPropertyAsString(oldPropertyNames[i][1], m);
            if(value != null) {
                if(session != null){
                    session.log(SessionLog.INFO, SessionLog.TRANSACTION, "deprecated_property", oldPropertyNames[i]);
                }
                m.put(oldPropertyNames[i][0], value);
            }
        }
    }
    
    protected static void warnOldProperties(Map m, AbstractSession session) {
    	for(int i=0; i < oldPropertyNames.length; i++) {
    		Object value = m.get(oldPropertyNames[i][1]);
            if(value != null) {
                session.log(SessionLog.INFO, SessionLog.TRANSACTION, "deprecated_property", oldPropertyNames[i]);
            }
        }
    }
    
    protected static void writeDDLToDatabase(SchemaManager mgr, TableCreationType ddlType) {
        String str = getConfigPropertyAsString(PersistenceUnitProperties.JAVASE_DB_INTERACTION, null ,"true");
        boolean interactWithDB = Boolean.valueOf(str.toLowerCase()).booleanValue();
        if (!interactWithDB){
            return;
        }
        generateDefaultTables(mgr, ddlType);
    }
    
    /**
     * This method will read SQL from a reader or URL and send it through
     * to the database. This could open up the database to SQL injection if
     * not careful.
     */
    protected static void writeDDLToDatabase(DatabaseSessionImpl session, Object source, ClassLoader loader) {
        if (source != null) {
            Reader reader = null;
            
            try {
                if (source instanceof Reader) {
                    reader = (Reader) source;
                } else {
                    URL sourceURL = loader.getResource((String) source);
                    
                    if (sourceURL == null) {
                        throw new PersistenceException(ExceptionLocalization.buildMessage("jpa21-ddl-source-script-not-found", new Object[]{source}));
                    } else {
                        URLConnection connection = sourceURL.openConnection();
                        // Set to false to prevent locking of jar files on Windows. EclipseLink issue 249664
                        connection.setUseCaches(false);
                        reader = new InputStreamReader(connection.getInputStream(), "UTF-8");
                    }
                }
            
                if (reader != null) {
                    StringBuffer sqlBuffer;
                    int data = reader.read();
                    
                    // While there is still data to read, read line by line.
                    while (data != -1) {
                        sqlBuffer = new StringBuffer();
                        char aChar = (char) data;
        
                        // Read till the end of the line or maybe even file.
                        while (aChar != '\n' && data != -1) {
                            sqlBuffer.append(aChar);
                                
                            // Read next character.
                            data = reader.read();
                            aChar = (char) data;
                        }

                        String sqlString = sqlBuffer.toString();
                        try {
                            session.executeNonSelectingSQL(sqlString);
                        } catch (DatabaseException e) {
                            // TODO: ignore for now, although the spec seems to want an exception??
                            //throw new PersistenceException(ExceptionLocalization.buildMessage("jpa21-ddl-source-script-sql-exception", new Object[]{sqlString, source}), e); 
                        }
                            
                        data = reader.read();
                    }
        
                    reader.close();
                }
            } catch (IOException e) {
                throw new PersistenceException(ExceptionLocalization.buildMessage("jpa21-ddl-source-script-io-exception", new Object[]{source}), e);
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        // ignore.
                    }
                }
            }
        }
    }
    
    protected static void writeDDLsToFiles(SchemaManager mgr, String appLocation, Object createDDLJdbc, Object dropDDLJdbc, TableCreationType ddlType) {
        // Ensure that the appLocation string ends with File.separator if 
        // specified. In JPA there is no default for app location, however, if 
        // the user did specify one, we'll use it.
        String appLocationPrefix = (appLocation == null) ? "" : addFileSeperator(appLocation); 
        
        if (ddlType.equals(TableCreationType.CREATE) || ddlType.equals(TableCreationType.DROP_AND_CREATE) || ddlType.equals(TableCreationType.EXTEND)) {
            if (createDDLJdbc == null) {
                // Using EclipseLink properties, the create script has a default.
                // Using JPA properties, the user must specify the target else an exception must be thrown.
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("jpa21-ddl-create-script-target-not-specified"));
            } else if (createDDLJdbc instanceof Writer) {
                mgr.outputCreateDDLToWriter((Writer) createDDLJdbc);
            } else {
                // Assume it is a String file name.
                mgr.outputCreateDDLToFile(appLocationPrefix + createDDLJdbc);
            }
        }

        if (ddlType.equals(TableCreationType.DROP) || ddlType.equals(TableCreationType.DROP_AND_CREATE)) {
            if (dropDDLJdbc == null) {
                // Using EclipseLink properties, the drop script has a default.
                // Using JPA properties, the user must specify the target else an exception must be thrown.
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("jpa21-ddl-drop-script-target-not-specified"));
            } else if (dropDDLJdbc instanceof Writer) {
                mgr.outputDropDDLToWriter((Writer) dropDDLJdbc);
            } else {
                // Assume it is a String file name.
                mgr.outputDropDDLToFile(appLocationPrefix + dropDDLJdbc);
            }
        }

        mgr.setCreateSQLFiles(false);
        generateDefaultTables(mgr, ddlType);
        mgr.closeDDLWriter();
    }
}
