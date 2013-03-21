/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *    Gyorke
 *    
 *     05/28/2008-1.0M8 Andrei Ilitchev 
 *        - 224964: Provide support for Proxy Authentication through JPA.
 *        Now properties' names that could be used both in createEM and createEMF are the same. 
 ******************************************************************************/  
package org.eclipse.persistence.config;

import java.util.HashSet;
import java.util.Set;
import java.util.Collections;

import org.eclipse.persistence.config.PersistenceUnitProperties;

/**
 * The class defines EclipseLink properties' names for use at the EntityManager level.
 * 
 * This properties are specific to an EnityManger and should be 
 * passed to createEntityManager methods of EntityManagerFactory.
 * 
 * Property values are usually case-insensitive with some common sense exceptions,
 * for instance class names.
 * 
 */
public class EntityManagerProperties {
	
    /**
     * Set to "true" this property forces persistence context to read through JTA-managed ("write") connection
     * in case there is an active transaction.
     * Valid values are case-insensitive "false" and "true"; "false" is default.
     * The property could also be set in persistence.xml or passed to createEntityManagerFactory,
     * Note that if the property set to "true" then objects read during transaction won't be placed into the
     * shared cache unless they have been updated.
     * in that case it affects all EntityManagers created by the factory. 
     */
    public static final String JOIN_EXISTING_TRANSACTION = PersistenceUnitProperties.JOIN_EXISTING_TRANSACTION;
    
    /**
     * Specifies whether there should be hard or soft references used within the Persistence Context.
     * Default is "HARD".  With soft references entities no longer referenced by the application
     * may be garbage collected freeing resources.  Any changes that have not been flushed in these
     * entities will be lost.
     * The property could also be set in persistence.xml or passed to createEntityManagerFactory,
     * in that case it affects all EntityManagers created by the factory. 
     * The property cannot be applied to existing active persistence unit context.
     * The context could be removed by calling clear method on the EntityManager when there is no active transaction.
     * @see org.eclipse.persistence.config.ReferenceMode
     */
    public static final String PERSISTENCE_CONTEXT_REFERENCE_MODE = PersistenceUnitProperties.PERSISTENCE_CONTEXT_REFERENCE_MODE;

    /**
     * The <code>"eclipselink.tenant-id"</code> property specifies the 
     * default context property used to populate multitenant entities.
     * 
     * NOTE: This is merely a default multitenant property than can be used on 
     * its own or with other properties defined by the user. Users are not 
     * obligated to use this property and are free to specify their own.
     * 
     * Example: persistence.xml file <code>
     * <property name="eclipselink.tenant-id" value="Oracle"/>
     * </code> Example: property Map <code>
     * propertiesMap.put(PersistenceUnitProperties.MULTITENANT_PROPERTY_DEFAULT, "Oracle");
     * </code>
     * 
     * @see org.eclipse.persistence.annotations.Multitenant
     * @see org.eclipse.persistence.annotations.TenantDiscriminatorColumn
     */
    public static final String MULTITENANT_PROPERTY_DEFAULT = PersistenceUnitProperties.MULTITENANT_PROPERTY_DEFAULT;
    
    /**
     * Specifies that the EntityManager will be closed or not used after commit (not extended).
     * In general this is normally always the case for a container managed EntityManager,
     * and common for application managed.
     * This can be used to avoid additional performance overhead of resuming the persistence context
     * after a commit().
     * The property set in persistence.xml or passed to createEntityManagerFactory affects all EntityManagers
     * created by the factory.
     * Alternatively, to apply the property only to some EntityManagers pass it to createEntityManager method.
     * Either "true" or "false.  "false" is the default.
     */
    public static final String PERSISTENCE_CONTEXT_CLOSE_ON_COMMIT = PersistenceUnitProperties.PERSISTENCE_CONTEXT_CLOSE_ON_COMMIT;

    /**
     * Specifies that the EntityManager will search all managed objects and persist any related non-managed
     * new objects that are cascade persist.
     * This can be used to avoid the cost of performing this search if persist is always used for new objects.
     * The property set in persistence.xml or passed to createEntityManagerFactory affects all EntityManagers
     * created by the factory.
     * Alternatively, to apply the property only to some EntityManagers pass it to createEntityManager method.
     * Either "true" or "false.  "true" is the default.
     */
    public static final String PERSISTENCE_CONTEXT_PERSIST_ON_COMMIT = PersistenceUnitProperties.PERSISTENCE_CONTEXT_PERSIST_ON_COMMIT;
    
    /**
     * Specifies that the EntityManager will search all managed objects and persist any related non-managed
     * new objects that are found ignoring any absence of CascadeType.PERSIST settings.
     * Also the Entity lifecycle Persist operation will not be cascaded to related entities.
     * This setting replicates the traditional EclipseLink native functionality.
     */
    public static final String PERSISTENCE_CONTEXT_COMMIT_WITHOUT_PERSIST_RULES = PersistenceUnitProperties.PERSISTENCE_CONTEXT_COMMIT_WITHOUT_PERSIST_RULES;

    /**
     * Allows the EntityManager FlushMode to be set as a persistence property.
     * This can be set to either "AUTO" or "COMMIT".
     * By default the flush mode is AUTO, which requires an automatic flush before all query execution.
     * This can be used to avoid any flushing until commit.
     * The property set in persistence.xml or passed to createEntityManagerFactory affects all EntityManagers
     * created by the factory.
     * Alternatively, to apply the property only to some EntityManagers pass it to createEntityManager method.
     * @see javax.persistence.EntityManager#setFlushMode(javax.persistence.FlushModeType)
     * @see javax.persistence.FlushModeType
     */
    public static final String PERSISTENCE_CONTEXT_FLUSH_MODE = PersistenceUnitProperties.PERSISTENCE_CONTEXT_FLUSH_MODE;
    
    /**
     * This property is used to specify proxy type that should be passed to OarcleConnection.openProxySession method.
     * Requires Oracle jdbc version 10.1.0.2 or later.
     * Requires Oracle9Platform or later as a database platform 
     * (TARGET_DATABASE property value should be TargetDatabase.Oracle9 or later).
     * The valid values are:
     * OracleConnection.PROXYTYPE_USER_NAME, OracleConnection.PROXYTYPE_DISTINGUISHED_NAME, OracleConnection.PROXYTYPE_CERTIFICATE.
     * Property property corresponding to the specified type should be also provided:
     * OracleConnection.PROXY_USER_NAME, OracleConnection.PROXY_DISTINGUISHED_NAME, OracleConnection.PROXY_CERTIFICATE.
     * Typically these properties should be set into EntityManager (either through createEntityManager method or
     * using proprietary setProperties method on EntityManagerImpl) - that causes EntityManager to use proxy connection for
     * writing and reading inside transaction. 
     * If proxy-type and the corresponding proxy property set into EntityManagerFactory then all connections
     * created by the factory will be proxy connections.
     * The property cannot be applied to existing active persistence unit context.
     * The context could be removed by calling clear method on the EntityManager when there is no active transaction.
     */
    public static final String ORACLE_PROXY_TYPE = PersistenceUnitProperties.ORACLE_PROXY_TYPE;
    
    /**
     * Determines when reads are performed through the write connection.
     * This property alters ConnectionPolicy.
     * The property cannot be applied to existing active persistence unit context.
     * The context could be removed by calling clear method on the EntityManager when there is no active transaction.
     * @see ExclusiveConnectionMode
     */
    public static final String EXCLUSIVE_CONNECTION_MODE = PersistenceUnitProperties.EXCLUSIVE_CONNECTION_MODE;
    
    /**
     * Determines when write connection is acquired lazily.
     * Valid values are case-insensitive "false" and "true"; "true" is default.
     * This property alters ConnectionPolicy.
     * The property cannot be applied to existing active persistence unit context.
     * The context could be removed by calling clear method on the EntityManager when there is no active transaction.
     */
    public static final String EXCLUSIVE_CONNECTION_IS_LAZY = PersistenceUnitProperties.EXCLUSIVE_CONNECTION_IS_LAZY;
    
    /**
     * JTA DataSource.
     * The value may be either data source or its name.
     * Note that this property will be ignore in case persistence unit was setup to NOT use JTA:
     * persistence.xml or createEntityManagerFactory had property "javax.persistence.transactionType" with RESOURCE_LOCAL value.
     * To avoid a conflict resulting in exception don't specify this property together with either JDBC_DRIVER or JDBC_URL;
     * however this property may override JDBC_DRIVER or JDBC_URL specified in persistence.xml or in createEntityManagerFactory method.
     * This property alters ConnectionPolicy.
     * The property cannot be applied to existing active persistence unit context.
     * The context could be removed by calling clear method on the EntityManager when there is no active transaction.
     */
    public static final String JTA_DATASOURCE = PersistenceUnitProperties.JTA_DATASOURCE;

    /**
     * NON JTA DataSource.
     * The value may be either data source or its name.
     * Note that this property will be ignore in case persistence unit was setup to use JTA:
     * persistence.xml or createEntityManagerFactory had property "javax.persistence.transactionType" with JTA value. 
     * To avoid a conflict resulting in exception don't specify this property together with either JDBC_DRIVER or JDBC_URL;
     * however this property may override JDBC_DRIVER or JDBC_URL specified in persistence.xml or in createEntityManagerFactory method.
     * This property alters ConnectionPolicy.
     * The property cannot be applied to existing active persistence unit context.
     * The context could be removed by calling clear method on the EntityManager when there is no active transaction.
     */
    public static final String NON_JTA_DATASOURCE = PersistenceUnitProperties.NON_JTA_DATASOURCE;
    
    /** JDBC Driver class name. 
     * To avoid a conflict resulting in exception don't specify this property together with either JTA_DATASOURCE or JTA_DATASOURCE;
     * however this property may override JTA_DATASOURCE or JTA_DATASOURCE specified in persistence.xml or in createEntityManagerFactory method.
     * This property alters ConnectionPolicy.
     * The property cannot be applied to existing active persistence unit context.
     * The context could be removed by calling clear method on the EntityManager when there is no active transaction.
     */
    public static final String JDBC_DRIVER = PersistenceUnitProperties.JDBC_DRIVER;

    /** JDBC Connection String. 
     * To avoid a conflict resulting in exception don't specify this property together with either JTA_DATASOURCE or JTA_DATASOURCE;
     * however this property may override JTA_DATASOURCE or JTA_DATASOURCE specified in persistence.xml or in createEntityManagerFactory method.
     * This property alters ConnectionPolicy.
     * The property cannot be applied to existing active persistence unit context.
     * The context could be removed by calling clear method on the EntityManager when there is no active transaction.
     */
    public static final String JDBC_URL = PersistenceUnitProperties.JDBC_URL;

    /** DataSource or JDBC DriverManager user name. 
     * Non-empty value overrides the value assigned in persistence.xml or in createEntityManagerFactory;
     * empty string value causes removal this property and JDBC_PASSWORD property 
     * specified in persistence.xml or in createEntityManagerFactory method.
     * This property alters ConnectionPolicy.
     * The property cannot be applied to existing active persistence unit context.
     * The context could be removed by calling clear method on the EntityManager when there is no active transaction.
     */
    public static final String JDBC_USER = PersistenceUnitProperties.JDBC_USER;

    /** DataSource or JDBC DriverManager password. 
     * Non-empty value overrides the value assigned in persistence.xml or in createEntityManagerFactory;
     * empty string value causes removal this property 
     * specified in persistence.xml or in createEntityManagerFactory method.
     * This property alters ConnectionPolicy.
     * The property cannot be applied to existing active persistence unit context.
     * The context could be removed by calling clear method on the EntityManager when there is no active transaction.
     */
    public static final String JDBC_PASSWORD = PersistenceUnitProperties.JDBC_PASSWORD;

    /** ConnectionPolicy 
     * Allows to specify an entire ConnectionPolicy.
     * Note that in case any other ConnectionPolicy-altering properties are present
     * they will be applied to this ConnectionPolicy. 
     * The property cannot be applied to existing active persistence unit context.
     * The context could be removed by calling clear method on the EntityManager when there is no active transaction.
     */
    public static final String CONNECTION_POLICY = "eclipselink.jdbc.connection-policy";

    /**
     * Configures if the existence of an object should be verified on persist(),
     * otherwise it will assume to be new if not in the persistence context.
     * If checked and existing and not in the persistence context and error will be thrown.
     * "false" by default.
     */    
    public static final String VALIDATE_EXISTENCE = PersistenceUnitProperties.VALIDATE_EXISTENCE;
    
    /**
     * Configures if updates should be ordered by primary key.
     * This can be used to avoid possible database deadlocks from concurrent threads
     * updating the same objects in different order.
     * If not set to true, the order of updates is not guaranteed.
     * "false" by default.
     */    
    public static final String ORDER_UPDATES = PersistenceUnitProperties.ORDER_UPDATES;
    
    /**
     * Defines EntityManager cache behavior after a call to flush method
     * followed by a call to clear method.
     * This property could be specified while creating either EntityManagerFactory 
     * (either in the map passed to createEntityManagerFactory method or in persistence.xml)
     * or EntityManager (in the map passed to createEntityManager method);
     * the latter overrides the former.
     * @see FlushClearCache
     */
    public static final String FLUSH_CLEAR_CACHE = PersistenceUnitProperties.FLUSH_CLEAR_CACHE;

    /**
     * The property may be passed to createEntityManager method of a composite persistence unit
     * to pass properties to member persistence units.
     * The value is a map: 
     * the key is a member persistence unit's name,
     * the value is a map of properties to be passed to this persistence unit. 
     * "eclipselink.composite-unit.properties" -> (
     *   ("memberPu1" -> (   "javax.persistence.jdbc.user" -> "user1", 
     *                       "javax.persistence.jdbc.password" -> "password1",
     *                       "javax.persistence.jdbc.driver" -> "oracle.jdbc.OracleDriver",
     *                       "javax.persistence.jdbc.url" -> "jdbc:oracle:thin:@oracle_db_url:1521:db",
     *                    ) , 
     *   ("memberPu2" -> (   "javax.persistence.jdbc.user" -> "user2",
     *                       "javax.persistence.jdbc.password" -> "password2"
     *                       "javax.persistence.jdbc.driver" -> "com.mysql.jdbc.Driver",
     *                       "javax.persistence.jdbc.url" -> "jdbc:mysql://my_sql_db_url:3306/user2",
     *                    )
     * )
     */
    public static final String COMPOSITE_UNIT_PROPERTIES = PersistenceUnitProperties.COMPOSITE_UNIT_PROPERTIES;
    
    private static final Set<String> supportedProperties = new HashSet<String>() {

        {
            add(JOIN_EXISTING_TRANSACTION);
            add(PERSISTENCE_CONTEXT_REFERENCE_MODE);
            add(PERSISTENCE_CONTEXT_CLOSE_ON_COMMIT);
            add(PERSISTENCE_CONTEXT_PERSIST_ON_COMMIT);
            add(PERSISTENCE_CONTEXT_FLUSH_MODE);
            add(PERSISTENCE_CONTEXT_COMMIT_WITHOUT_PERSIST_RULES);
            add(ORACLE_PROXY_TYPE);
            add(EXCLUSIVE_CONNECTION_MODE);
            add(EXCLUSIVE_CONNECTION_IS_LAZY);
            add(JTA_DATASOURCE);
            add(NON_JTA_DATASOURCE);
            add(JDBC_DRIVER);
            add(JDBC_URL);
            add(JDBC_USER);
            add(JDBC_PASSWORD);
            add(CONNECTION_POLICY);
            add(VALIDATE_EXISTENCE);
            add(ORDER_UPDATES);
            add(FLUSH_CLEAR_CACHE);
            add(COMPOSITE_UNIT_PROPERTIES);
        }
    };

    public static Set<String> getSupportedProperties() {
        return Collections.unmodifiableSet(supportedProperties);
    }
}
