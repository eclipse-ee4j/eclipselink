/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
//    Gyorke
//
//     05/28/2008-1.0M8 Andrei Ilitchev
//        - 224964: Provide support for Proxy Authentication through JPA.
//        Now properties' names that could be used both in createEM and createEMF are the same.
package org.eclipse.persistence.config;

import java.util.Set;

/**
 * The class defines EclipseLink properties' names for use at the {@code EntityManager} level.
 * <p>
 * These properties are specific to an EntityManger and should be
 * passed to createEntityManager methods of {@code EntityManagerFactory}.
 * <p>
 * Property values are usually case-insensitive with some common sense exceptions,
 * for instance class names.
 *
 */
public final class EntityManagerProperties {

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
     * The context could be removed by calling clear method on the {@code EntityManager} when there is no active transaction.
     * @see org.eclipse.persistence.config.ReferenceMode
     */
    public static final String PERSISTENCE_CONTEXT_REFERENCE_MODE = PersistenceUnitProperties.PERSISTENCE_CONTEXT_REFERENCE_MODE;

    /**
     * The {@code "eclipselink.tenant-id"} property specifies the
     * default context property used to populate multitenant entities.
     * <p>
     * NOTE: This is merely a default multitenant property than can be used on
     * its own or with other properties defined by the user. Users are not
     * obligated to use this property and are free to specify their own.
     * <p>
     * Example: persistence.xml file
     * {@snippet lang="XML" :
     * <property name="eclipselink.tenant-id" value="Oracle"/>
     * }
     * Example: property Map
     * {@snippet :
     *  propertiesMap.put(PersistenceUnitProperties.MULTITENANT_PROPERTY_DEFAULT, "Oracle");
     * }
     *
     * @see org.eclipse.persistence.annotations.Multitenant
     * @see org.eclipse.persistence.annotations.TenantDiscriminatorColumn
     */
    public static final String MULTITENANT_PROPERTY_DEFAULT = PersistenceUnitProperties.MULTITENANT_PROPERTY_DEFAULT;

    /**
     * The {@code eclipselink.tenant-schema-id} property specifies the
     * context property used to distinguish tenants when using global schema per tenant
     * multitenant strategy. It is expected to be set by user when creating an {@code EntityManager}.
     * <p>
     * <b>Java example:</b><pre>
     * {@code props.put(PersistenceUnitProperties.MULTITENANT_SCHEMA_PROPERTY_DEFAULT, "Oracle");}</pre>
     *
     * @see PersistenceUnitProperties#MULTITENANT_STRATEGY
     * @see org.eclipse.persistence.descriptors.SchemaPerMultitenantPolicy
     */
    public static final String MULTITENANT_SCHEMA_PROPERTY_DEFAULT = PersistenceUnitProperties.MULTITENANT_SCHEMA_PROPERTY_DEFAULT;

    /**
     * Specifies that the {@code EntityManager} will be closed or not used after commit (not extended).
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
     * Specifies that the {@code EntityManager} will search all managed objects and persist any related non-managed
     * new objects that are cascade persist.
     * This can be used to avoid the cost of performing this search if persist is always used for new objects.
     * The property set in persistence.xml or passed to createEntityManagerFactory affects all EntityManagers
     * created by the factory.
     * Alternatively, to apply the property only to some EntityManagers pass it to createEntityManager method.
     * Either "true" or "false.  "true" is the default.
     */
    public static final String PERSISTENCE_CONTEXT_PERSIST_ON_COMMIT = PersistenceUnitProperties.PERSISTENCE_CONTEXT_PERSIST_ON_COMMIT;

    /**
     * Specifies that the {@code EntityManager} will search all managed objects and persist any related non-managed
     * new objects that are found ignoring any absence of CascadeType.PERSIST settings.
     * Also, the Entity lifecycle Persist operation will not be cascaded to related entities.
     * This setting replicates the traditional EclipseLink native functionality.
     */
    public static final String PERSISTENCE_CONTEXT_COMMIT_WITHOUT_PERSIST_RULES = PersistenceUnitProperties.PERSISTENCE_CONTEXT_COMMIT_WITHOUT_PERSIST_RULES;

    /**
     * Allows the {@code EntityManager} FlushMode to be set as a persistence property.
     * This can be set to either "AUTO" or "COMMIT".
     * By default, the flush mode is AUTO, which requires an automatic flush before all query execution.
     * This can be used to avoid any flushing until commit.
     * The property set in persistence.xml or passed to createEntityManagerFactory affects all EntityManagers
     * created by the factory.
     * Alternatively, to apply the property only to some EntityManagers pass it to createEntityManager method.
     *
     * @see jakarta.persistence.EntityManager#setFlushMode(jakarta.persistence.FlushModeType)
     * @see jakarta.persistence.FlushModeType
     */
    public static final String PERSISTENCE_CONTEXT_FLUSH_MODE = PersistenceUnitProperties.PERSISTENCE_CONTEXT_FLUSH_MODE;

    /**
     * This property is used to specify proxy type that should be passed to OarcleConnection.openProxySession method.
     * Requires Oracle jdbc version 10.1.0.2 or later.
     * Requires Oracle9Platform or later as a database platform
     * (TARGET_DATABASE property value should be TargetDatabase.Oracle9 or later).
     * The valid values are:
     * OracleConnection.PROXYTYPE_USER_NAME, OracleConnection.PROXYTYPE_DISTINGUISHED_NAME, OracleConnection.PROXYTYPE_CERTIFICATE.
     * Property corresponding to the specified type should be also provided:
     * OracleConnection.PROXY_USER_NAME, OracleConnection.PROXY_DISTINGUISHED_NAME, OracleConnection.PROXY_CERTIFICATE.
     * Typically, these properties should be set into {@code EntityManager} (either through createEntityManager method or
     * using proprietary setProperties method on EntityManagerImpl) - that causes {@code EntityManager} to use proxy connection for
     * writing and reading inside transaction.
     * If proxy-type and the corresponding proxy property set into {@code EntityManagerFactory} then all connections
     * created by the factory will be proxy connections.
     * The property cannot be applied to existing active persistence unit context.
     * The context could be removed by calling clear method on the {@code EntityManager} when there is no active transaction.
     */
    public static final String ORACLE_PROXY_TYPE = PersistenceUnitProperties.ORACLE_PROXY_TYPE;

    /**
     * Determines when reads are performed through the write connection.
     * This property alters ConnectionPolicy.
     * The property cannot be applied to existing active persistence unit context.
     * The context could be removed by calling clear method on the {@code EntityManager} when there is no active transaction.
     * @see ExclusiveConnectionMode
     */
    public static final String EXCLUSIVE_CONNECTION_MODE = PersistenceUnitProperties.EXCLUSIVE_CONNECTION_MODE;

    /**
     * Determines when write connection is acquired lazily.
     * Valid values are case-insensitive "false" and "true"; "true" is default.
     * This property alters ConnectionPolicy.
     * The property cannot be applied to existing active persistence unit context.
     * The context could be removed by calling clear method on the {@code EntityManager} when there is no active transaction.
     */
    public static final String EXCLUSIVE_CONNECTION_IS_LAZY = PersistenceUnitProperties.EXCLUSIVE_CONNECTION_IS_LAZY;

    /**
     * JTA DataSource.
     * The value may be either data source or its name.
     * Note that this property will be ignored in case persistence unit was set up to NOT use JTA:
     * persistence.xml or createEntityManagerFactory had property {@code jakarta.persistence.transactionType} with RESOURCE_LOCAL value.
     * To avoid a conflict resulting in exception don't specify this property together with either JDBC_DRIVER or JDBC_URL;
     * however this property may override JDBC_DRIVER or JDBC_URL specified in persistence.xml or in createEntityManagerFactory method.
     * This property alters ConnectionPolicy.
     * The property cannot be applied to existing active persistence unit context.
     * The context could be removed by calling clear method on the {@code EntityManager} when there is no active transaction.
     */
    public static final String JTA_DATASOURCE = PersistenceUnitProperties.JTA_DATASOURCE;

    /**
     * NON JTA DataSource.
     * The value may be either data source or its name.
     * Note that this property will be ignored in case persistence unit was set up to use JTA:
     * persistence.xml or createEntityManagerFactory had property {@code jakarta.persistence.transactionType} with JTA value.
     * To avoid a conflict resulting in exception don't specify this property together with either JDBC_DRIVER or JDBC_URL;
     * however this property may override JDBC_DRIVER or JDBC_URL specified in persistence.xml or in createEntityManagerFactory method.
     * This property alters ConnectionPolicy.
     * The property cannot be applied to existing active persistence unit context.
     * The context could be removed by calling clear method on the {@code EntityManager} when there is no active transaction.
     */
    public static final String NON_JTA_DATASOURCE = PersistenceUnitProperties.NON_JTA_DATASOURCE;

    /** JDBC Driver class name.
     * To avoid a conflict resulting in exception don't specify this property together with either JTA_DATASOURCE or JTA_DATASOURCE;
     * however this property may override JTA_DATASOURCE or JTA_DATASOURCE specified in persistence.xml or in createEntityManagerFactory method.
     * This property alters ConnectionPolicy.
     * The property cannot be applied to existing active persistence unit context.
     * The context could be removed by calling clear method on the {@code EntityManager} when there is no active transaction.
     */
    public static final String JDBC_DRIVER = PersistenceUnitProperties.JDBC_DRIVER;

    /** JDBC Connection String.
     * To avoid a conflict resulting in exception don't specify this property together with either JTA_DATASOURCE or JTA_DATASOURCE;
     * however this property may override JTA_DATASOURCE or JTA_DATASOURCE specified in persistence.xml or in createEntityManagerFactory method.
     * This property alters ConnectionPolicy.
     * The property cannot be applied to existing active persistence unit context.
     * The context could be removed by calling clear method on the {@code EntityManager} when there is no active transaction.
     */
    public static final String JDBC_URL = PersistenceUnitProperties.JDBC_URL;

    /** DataSource or JDBC DriverManager user name.
     * Non-empty value overrides the value assigned in persistence.xml or in createEntityManagerFactory;
     * empty string value causes removal this property and JDBC_PASSWORD property
     * specified in persistence.xml or in createEntityManagerFactory method.
     * This property alters ConnectionPolicy.
     * The property cannot be applied to existing active persistence unit context.
     * The context could be removed by calling clear method on the {@code EntityManager} when there is no active transaction.
     */
    public static final String JDBC_USER = PersistenceUnitProperties.JDBC_USER;

    /** DataSource or JDBC DriverManager password.
     * Non-empty value overrides the value assigned in persistence.xml or in createEntityManagerFactory;
     * empty string value causes removal this property
     * specified in persistence.xml or in createEntityManagerFactory method.
     * This property alters ConnectionPolicy.
     * The property cannot be applied to existing active persistence unit context.
     * The context could be removed by calling clear method on the {@code EntityManager} when there is no active transaction.
     */
    public static final String JDBC_PASSWORD = PersistenceUnitProperties.JDBC_PASSWORD;

    /** ConnectionPolicy
     * Allows to specify an entire ConnectionPolicy.
     * Note that in case any other ConnectionPolicy-altering properties are present
     * they will be applied to this ConnectionPolicy.
     * The property cannot be applied to existing active persistence unit context.
     * The context could be removed by calling clear method on the {@code EntityManager} when there is no active transaction.
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
     * @deprecated since 2.6 replaced by PERSISTENCE_CONTEXT_COMMIT_ORDER
     */
    @Deprecated
    public static final String ORDER_UPDATES = PersistenceUnitProperties.ORDER_UPDATES;

    /**
     * Defines the ordering of updates and deletes of a set of the same entity type during a commit or flush operation.
     * The commit order of entities is defined by their foreign key constraints, and then sorted alphabetically.\
     * <p>
     * By default, the commit of a set of the same entity type is ordered by its Id.
     * <p>
     * Entity type commit order can be modified using a DescriptorCustomizer and the ClassDescriptor.addConstraintDependency() API.
     * Commit order can also be controlled using the EntityManager.flush() API.
     * <p>
     * Values (case-insensitive):
     * <ul>
     * <li>"Id" (DEFAULT) : Updates and deletes are ordered by the object's id.  This can help avoid deadlocks on highly concurrent systems.
     * <li>"Changes": Updates are ordered by the object's changes, then by id.  This can improve batch writing efficiency.
     * <li>"None": No ordering is done.
     * </ul>
     * @see CommitOrderType
     */
    public static final String PERSISTENCE_CONTEXT_COMMIT_ORDER = PersistenceUnitProperties.PERSISTENCE_CONTEXT_COMMIT_ORDER;

    /**
     * Defines {@code EntityManager} cache behavior after a call to flush method
     * followed by a call to clear method.
     * This property could be specified while creating either EntityManagerFactory
     * (either in the map passed to createEntityManagerFactory method or in persistence.xml)
     * or {@code EntityManager} (in the map passed to createEntityManager method);
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
     * <p>
     * "eclipselink.composite-unit.properties" {@literal ->} (<br>
     *  &nbsp;("memberPu1" {@literal ->} (<br>
     *  &nbsp;&nbsp;&nbsp;"jakarta.persistence.jdbc.user" {@literal ->} "user1",<br>
     *  &nbsp;&nbsp;&nbsp;"jakarta.persistence.jdbc.password" {@literal ->} "password1",<br>
     *  &nbsp;&nbsp;&nbsp;"jakarta.persistence.jdbc.driver" {@literal ->} "oracle.jdbc.OracleDriver",<br>
     *  &nbsp;&nbsp;&nbsp;"jakarta.persistence.jdbc.url" {@literal ->} "jdbc:oracle:thin:@oracle_db_url:1521:db",<br>
     *  &nbsp;&nbsp;) ,<br>
     *  &nbsp;("memberPu2" {@literal ->} (<br>
     *  &nbsp;&nbsp;&nbsp;"jakarta.persistence.jdbc.user" {@literal ->} "user2",<br>
     *  &nbsp;&nbsp;&nbsp;"jakarta.persistence.jdbc.password" {@literal ->} "password2"<br>
     *  &nbsp;&nbsp;&nbsp;"jakarta.persistence.jdbc.driver" {@literal ->} "com.mysql.jdbc.Driver",<br>
     *  &nbsp;&nbsp;&nbsp;"jakarta.persistence.jdbc.url" {@literal ->} "jdbc:mysql://my_sql_db_url:3306/user2",<br>
     *  &nbsp;&nbsp;)<br>
     * )
     */
    public static final String COMPOSITE_UNIT_PROPERTIES = PersistenceUnitProperties.COMPOSITE_UNIT_PROPERTIES;

    private static final Set<String> supportedProperties = Set.of(
            JOIN_EXISTING_TRANSACTION,
            PERSISTENCE_CONTEXT_REFERENCE_MODE,
            PERSISTENCE_CONTEXT_CLOSE_ON_COMMIT,
            PERSISTENCE_CONTEXT_PERSIST_ON_COMMIT,
            PERSISTENCE_CONTEXT_FLUSH_MODE,
            PERSISTENCE_CONTEXT_COMMIT_WITHOUT_PERSIST_RULES,
            ORACLE_PROXY_TYPE,
            EXCLUSIVE_CONNECTION_MODE,
            EXCLUSIVE_CONNECTION_IS_LAZY,
            JTA_DATASOURCE,
            NON_JTA_DATASOURCE,
            JDBC_DRIVER,
            JDBC_URL,
            JDBC_USER,
            JDBC_PASSWORD,
            CONNECTION_POLICY,
            VALIDATE_EXISTENCE,
            ORDER_UPDATES,
            PERSISTENCE_CONTEXT_COMMIT_ORDER,
            FLUSH_CLEAR_CACHE,
            COMPOSITE_UNIT_PROPERTIES);

    private EntityManagerProperties() {
        //no instance please
    }

    public static Set<String> getSupportedProperties() {
        return supportedProperties;
    }
}
