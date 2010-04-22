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
 *     dclarke - Bug 294985: update of comments and addition of connection logging property
 *     cdelahun - Bug 214534: added COORDINATION_JMS_REUSE_PUBLISHER property to enable JMS rcm legacy behavior
 ******************************************************************************/
package org.eclipse.persistence.config;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FlushModeType;
import javax.persistence.Persistence;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.CacheCoordinationType;
import org.eclipse.persistence.exceptions.ExceptionHandler;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.eclipse.persistence.platform.server.ServerPlatform;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.SessionEventListener;
import org.eclipse.persistence.sessions.SessionProfiler;
import org.eclipse.persistence.sessions.coordination.RemoteCommandManager;
import org.eclipse.persistence.sessions.factories.ReferenceMode;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.eclipse.persistence.tools.profiler.PerformanceProfiler;
import org.eclipse.persistence.tools.profiler.QueryMonitor;

/**
 * The class defines EclipseLink persistence unit property names. These values
 * are used to assist in the configuration of properties passed to
 * {@link Persistence#createEntityManagerFactory(String, Map)} which override
 * the values specified in the persistence.xml file.
 * <p>
 * <b>Usage Example:</b> <code>
 * Map<String, Object> props = new HashMap<String, Object>();</br>
 * </br>
 * props.put(PersistenceUnitProperties.JDBC_USER, "user-name");</br>
 * props.put(PersistenceUnitProperties.JDBC_PASSWORD, "password");</br>
 * </br>
 * EntityManagerFactory emf = Persistence.createENtityManagerFactory("pu-name", props);
 * </code>
 * <p>
 * Property values are usually case-insensitive with some common sense
 * exceptions, for instance class names.
 * 
 * @see Persistence#createEntityManagerFactory(String, Map)
 */
public class PersistenceUnitProperties {

    /**
     * The <code>javax.persistence.transactionType"</code> property specifies the
     * transaction type for the persistence unit. This property overrides the
     * value specified in the persistence.xml.
     * <p>
     * Values: A string value of "JTA" or "RESOURCE_LOCAL"
     */
    public static final String TRANSACTION_TYPE = "javax.persistence.transactionType";

    /**
     * The <code>javax.persistence.jtaDataSource"</code> property specifies the JTA data
     * source name that will look up a valid {@link javax.sql.DataSource}. This
     * property is used to override the value specified in the persistence.xml.
     * <p>
     * Values: A well formed JNDI resource name that can locate the data source
     * in the target container or an instance of {@link javax.sql.DataSource}
     */
    public static final String JTA_DATASOURCE = "javax.persistence.jtaDataSource";

    /**
     * The <code>javax.persistence.nonJtaDataSource"</code> property specifies the
     * non-JTA data source name that will look up a valid
     * {@link javax.sql.DataSource}. This can be used to override the value
     * specified in the persistence.xml.
     * <p>
     * Values: A well formed JNDI resource name that can locate the data source
     * in the target container or an instance of {@link javax.sql.DataSource}
     */
    public static final String NON_JTA_DATASOURCE = "javax.persistence.nonJtaDataSource";

    // JDBC Properties for internal connection pooling

    /**
     * The <code>javax.persistence.jdbc.driver"</code> property specifies the JDBC
     * DriverManager class name used for internal connection pooling when a data
     * source is not being used. The value must be a string which is the
     * qualified class name for a valid class that implements
     * <code>java.sql.Driver</code>.
     * <p>
     * <b>Persistence XML example:</b> <code>
     * <property name="javax.persistence.jdbc.driver" value="com.mysql.jdbc.Driver" />
     * </code>
     * <p>
     * The previous value for this property <code>"eclipselink.jdbc.driver</code> is now deprecated and should
     * be replaced with this new name.
     */
    public static final String JDBC_DRIVER = "javax.persistence.jdbc.driver";

    /**
     * The <code>javax.persistence.jdbc.url"</code> property specifies the JDBC URL used
     * for internal connection pooling when a data source is not being used. The
     * value must be a string which represents a valid URL for the specified
     * JDBC driver.
     * <p>
     * <b>Persistence XML example:</b> <code>
     * <property name="javax.persistence.jdbc.url" value="jdbc:mysql://localhost/mysql" />
     * </code>
     * <p>
     * The previous value for this property <code>"eclipselink.jdbc.url</code> is now deprecated and should
     * be replaced with this new name.
     */
    public static final String JDBC_URL = "javax.persistence.jdbc.url";

    /**
     * The <code>javax.persistence.jdbc.user"</code> property specifies the data source
     * or JDBC user name.
     * <p>
     * <b>Persistence XML example:</b> <code>
     * <property name="javax.persistence.jdbc.user" value="user-name" />
     * </code>
     * <p>
     * The previous value for this property <code>"eclipselink.jdbc.user</code> is now deprecated and should
     * be replaced with this new name.
     */
    public static final String JDBC_USER = "javax.persistence.jdbc.user";

    /**
     * The <code>javax.persistence.jdbc.password"</code> property specifies the data
     * source or JDBC password.
     * <p>
     * <b>Persistence XML example:</b> <code>
     * <property name="javax.persistence.jdbc.password" value="password" />
     * </code> The previous value for this
     * property <code>"eclipselink.jdbc.password</code> is now deprecated and should be replaced with this new
     * name.
     */
    public static final String JDBC_PASSWORD = "javax.persistence.jdbc.password";

    /**
     * The <code>"eclipselink.jdbc.native-sql"</code> property specifies whether
     * generic SQL should be used or platform specific 'native' SQL. The
     * platform specific SQL customizes join syntax, date operators, sequencing,
     * ...
     * <p>
     * Allowed Values (String):
     * <ul>
     * <li>"true" - use database specific SQL
     * <li>"false" (DEFAULT) - use generic SQL
     * </ul>
     * 
     * @see DatabaseLogin#setUsesNativeSQL(boolean)
     */
    public static final String NATIVE_SQL = "eclipselink.jdbc.native-sql";

    /**
     * The <code>"eclipselink.jdbc.sql-cast"</code> property specifies is
     * platform specific CAST SQL operations should be used. Casting is normally
     * not required, and can cause issue when used. Allowed Values (String):
     * <ul>
     * <li>"true" - enable platform specific cast
     * <li>"false" (DEFAULT) - disable platform specific cast
     * </ul>
     * 
     * @see DatabasePlatform#setIsCastRequired(boolean)
     */
    public static final String SQL_CAST = "eclipselink.jdbc.sql-cast";

    /**
     * The <code>"eclipselink.jdbc.connections.wait-timeout"</code> property
     * which specifies the timeout time in milliseconds (ms) that will be waited
     * for an available connection before an exception is thrown. Ignored in
     * case external connection pools are used.
     * <p>
     * Default: 180000 ms (3 minutes).
     * <p>
     * The value specified should be a string containing a positive integer
     * value. A value of 0 means wait forever.
     */
    public static final String JDBC_CONNECTIONS_WAIT = "eclipselink.jdbc.connections.wait-timeout";

    /**
     * The <code>"eclipselink.jdbc.connections.max"</code> property specifies
     * the maximum number of read connection in the internal connection pool. If
     * the maximum size is reached, threads requiring a connection will wait
     * until one is released back to the pool. By default a single shared
     * (exclusive) read/write pool is used with min/max 32 connections and 1
     * initial. Ignored in case external connection pools are used.
     * <p>
     * The value specified should be a string containing a positive integer
     * value.
     */
    public static final String JDBC_CONNECTIONS_MAX = "eclipselink.jdbc.connections.max";

    /**
     * The <code>"eclipselink.jdbc.connections.min"</code> property specifies
     * the minimum number of connections in EclipseLink connection pool.
     * <p>
     * Ignored in case external connection pools are used. Connections beyond
     * the minimum will be disconnected when returned to the pool, so this
     * should normally be equal to the number of active threads, or server's
     * thread pool size. By default a single shared (exclusive) read/write pool
     * is used with min/max 32 connections and 1 initial.
     * <p>
     * The value specified should be a string containing a positive integer
     * value.
     */
    public static final String JDBC_CONNECTIONS_MIN = "eclipselink.jdbc.connections.min";

    /**
     * The <code>"eclipselink.jdbc.connections.initial"</code> property
     * EclipseLink JDBC (internal) connection pools properties. Initial number
     * of connections in EclipseLink connection pool. This is the number of
     * connections connected at startup. By default a single shared (exclusive)
     * read/write pool is used with min/max 32 connections and 1 initial.Ignored
     * in case external connection pools are used.
     * <p>
     * The value specified should be a string containing a positive integer
     * value.
     */
    public static final String JDBC_CONNECTIONS_INITIAL = "eclipselink.jdbc.connections.initial";

    /**
     * The <code>"eclipselink.jdbc.write-connections.max"</code> property
     * specifies the maximum number of connections supported in the internal
     * write connection pool. Maximum number of connections in EclipseLink write
     * connection pool. If the maximum size is reached, threads requiring a
     * connection will wait until one is released back to the pool. By default a
     * single shared (exclusive) read/write pool is used with min/max 32
     * connections and 1 initial.Ignored in case external connection pools are
     * used.
     * <p>
     * The value specified should be a string containing a positive integer
     * value.
     */
    public static final String JDBC_WRITE_CONNECTIONS_MAX = "eclipselink.jdbc.write-connections.max";

    /**
     * The <code>"eclipselink.jdbc.write-connections.min"</code> property
     * specifies the minimum number of connections in the internal write
     * connection pool. Connections beyond the minimum will be disconnected when
     * returned to the pool, so this should normally be equal to the number of
     * active threads, or server's thread pool size. By default a single shared
     * (exclusive) read/write pool is used with min/max 32 connections and 1
     * initial. Ignored in case external connection pools are used.
     * <p>
     * The value specified should be a string containing a positive integer
     * value.
     */
    public static final String JDBC_WRITE_CONNECTIONS_MIN = "eclipselink.jdbc.write-connections.min";

    /**
     * The <code>"eclipselink.jdbc.write-connections.initial"</code> property
     * configures the number of connections connected at created at startup in
     * the write connection pool. By default a single shared (exclusive)
     * read/write pool is used with min/max 32 connections and 1 initial.
     * <p>
     * This property is ignored in case external connection pools are used.
     * Initial number of connections in EclipseLink write connection pool.
     * <p>
     * The value must be a string containing a zero or greater integer value.
     */
    public static final String JDBC_WRITE_CONNECTIONS_INITIAL = "eclipselink.jdbc.write-connections.initial";

    /**
     * The <code>"eclipselink.jdbc.read-connections.max"</code> property
     * configures the maximum number of connections in the read connection pool.
     * If the maximum size is reached, threads requiring a connection will wait
     * until one is released back to the pool (unless shared). By default a
     * separate read connection pool is not used. By default a single shared
     * (exclusive) read/write pool is used with min/max 32 connections and 1
     * initial.
     * <p>
     * This property is ignored in case external connection pools are used.
     * Initial number of connections in EclipseLink write connection pool.
     * <p>
     * The value specified should be a string containing a zero or greater
     * integer value.
     * 
     * @see #JDBC_CONNECTIONS_WAIT to configure the timeout waiting on a
     *      connection.
     */
    public static final String JDBC_READ_CONNECTIONS_MAX = "eclipselink.jdbc.read-connections.max";

    /**
     * The <code>"eclipselink.jdbc.read-connections.min"</code> property
     * configures the minimum number of connections in read connection pool.
     * Connections beyond the minimum will be disconnected when returned to the
     * pool, so this should normally be equal to the number of active threads,
     * or server's thread pool size. By default a separate read connection pool
     * is not used. By default a single shared (exclusive) read/write pool is
     * used with min/max 32 connections and 1 initial.
     * <p>
     * This property is ignored in case external connection pools are used.
     * Initial number of connections in EclipseLink write connection pool.
     * <p>
     * The value specified should be a string containing a zero or greater
     * integer value.
     */
    public static final String JDBC_READ_CONNECTIONS_MIN = "eclipselink.jdbc.read-connections.min";

    /**
     * The <code>"eclipselink.jdbc.read-connections.initial"</code> property
     * configures the number of connections connected at created at startup in
     * the read connection pool. By default a single shared (exclusive)
     * read/write pool is used with min/max 32 connections and 1 initial.
     * <p>
     * This property is ignored in case external connection pools are used.
     * Initial number of connections in EclipseLink write connection pool.
     * <p>
     * The value must be a string containing a zero or greater integer value.
     */
    public static final String JDBC_READ_CONNECTIONS_INITIAL = "eclipselink.jdbc.read-connections.initial";

    /**
     * The <code>"eclipselink.jdbc.read-connections.shared"</code> property
     * configures whether connections in EclipseLink read connection pool should
     * be shared (not exclusive). Connection sharing means the same JDBC
     * connection will be used concurrently for multiple reading threads.
     * <p>
     * This property is ignored in case external connection pools are used.
     * Initial number of connections in EclipseLink write connection pool.
     * <p>
     * Values (case insensitive):
     * <ul>
     * "false" (DEFAULT): indicates read connections will not be shared
     * <li>"true": indicates read connections can be shared
     * <li>
     * </ul>
     */
    public static final String JDBC_READ_CONNECTIONS_SHARED = "eclipselink.jdbc.read-connections.shared";

    /**
     * The <code>"eclipselink.jdbc.sequence-connection-pool.max"</code> property
     * configures the maximum number of connections in the sequence connection
     * pool. If the maximum size is reached, threads requiring a connection will
     * wait until one is released back to the pool.
     * <p>
     * By default a separate sequence connection pool is not used (the sequence
     * is allocated on the write connection).
     * <p>
     * This property is ignored in case external connection pools are used.
     * Initial number of connections in EclipseLink write connection pool.
     * <p>
     * The value must be a string containing a zero or greater integer value.
     * The default value is two (2).
     * 
     * @see #JDBC_SEQUENCE_CONNECTION_POOL Required to configure the use of a
     *      sequence pool/data-source
     * @see #JDBC_CONNECTIONS_WAIT to configure the timeout
     */
    public static final String JDBC_SEQUENCE_CONNECTION_POOL_MAX = "eclipselink.jdbc.sequence-connection-pool.max";

    /**
     * The <code>"eclipselink.jdbc.sequence-connection-pool.min"</code> property
     * configures the minimum number of connections in sequence connection pool.
     * Connections beyond the minimum will be disconnected when returned to the
     * pool, so this should normally be equal to the maximum to avoid
     * connecting/disconnecting.
     * <p>
     * By default a separate sequence connection pool is not used (the sequence
     * is allocated on the write connection).
     * <p>
     * This property is ignored in case external connection pools are used.
     * Initial number of connections in EclipseLink write connection pool.
     * <p>
     * The value must be a string containing a zero or greater integer value.
     * The default value is two (2).
     * 
     * @see #JDBC_SEQUENCE_CONNECTION_POOL Required to configure the use of a
     *      sequence pool/data-source
     */
    public static final String JDBC_SEQUENCE_CONNECTION_POOL_MIN = "eclipselink.jdbc.sequence-connection-pool.min";

    /**
     * The <code>"eclipselink.jdbc.sequence-connection-pool.initial</code> property configures the initial number of connections in
     * sequence connection pool. This is the number of connections connected at
     * startup.
     * <p>
     * By default a separate sequence connection pool is not used (the sequence
     * is allocated on the write connection).
     * <p>
     * This property is ignored in case external connection pools are used.
     * Initial number of connections in EclipseLink write connection pool.
     * <p>
     * The value must be a string containing a zero or greater integer value.
     * The default value is two (2).
     * 
     * @see #JDBC_SEQUENCE_CONNECTION_POOL Required to configure the use of a
     *      sequence pool/data-source
     */
    public static final String JDBC_SEQUENCE_CONNECTION_POOL_INITIAL = "eclipselink.jdbc.sequence-connection-pool.initial";

    /**
     * The <code>"eclipselink.jdbc.sequence-connection-pool"</code> property
     * configures a separate connection pool should used for sequencing to
     * retrieve new value(s). This improves sequence allocation by allocating
     * sequencing outside the current transaction. This can be used with
     * internal or external (DataSource) connection pooling, external must
     * provide a non-jta-datasource resource name using
     * {@link #JDBC_SEQUENCE_CONNECTION_POOL_DATASOURCE}. A sequence connection
     * pool is generally only used with using TABLE sequencing to minimize
     * contention on the sequence table(s).
     * <p>
     * Values (case-insensitive):
     * <ul>
     * <li>"false" (DEFAULT):
     * <li>"true":
     * </ul>
     * 
     * @see #JDBC_SEQUENCE_CONNECTION_POOL_DATASOURCE to configure the use of a
     *      non-JTA data source for sequence allocation call.
     * @see #JDBC_SEQUENCE_CONNECTION_POOL_INITIAL to configure the initial
     *      connections size for internal connection pooling
     * @see #JDBC_SEQUENCE_CONNECTION_POOL_MIN to configure the minimum
     *      connections size for internal connection pooling
     * @see #JDBC_SEQUENCE_CONNECTION_POOL_MAX to configure the maximum
     *      connections size for internal connection pooling
     */
    public static final String JDBC_SEQUENCE_CONNECTION_POOL = "eclipselink.jdbc.sequence-connection-pool";

    /**
     * The <code>"eclipselink.jdbc.sequence-connection-pool.non-jta-data-source</code> property configures the name of the non-JTA data source that
     * will be used for sequencing calls.
     * 
     * @see #JDBC_SEQUENCE_CONNECTION_POOL
     */
    public static final String JDBC_SEQUENCE_CONNECTION_POOL_DATASOURCE = "eclipselink.jdbc.sequence-connection-pool.non-jta-data-source";

    /**
     * Property <code>"eclipselink.jdbc.bind-parameters</code> configures whether parameter binding will be used in the
     * creation of JDBC prepared statements. Usage of parameter binding is
     * generally a performance optimization allowing for SQL and prepared
     * statement caching as well as usage of batch writing.
     * <p>
     * Values:
     * <ul>
     * <li>"true" (DEFAULT): binding will be used
     * <li>"false": values will be written literally into the generated SQL
     * </ul>
     */
    public static final String JDBC_BIND_PARAMETERS = "eclipselink.jdbc.bind-parameters";

    /**
     * The <code>"eclipselink.jdbc.exclusive-connection.mode"</code> property
     * specifies when reads are performed through the write connection. You can
     * set this property while creating either an EntityManagerFactory (either
     * in the map passed to the createEntityManagerFactory method, or in the
     * persistence.xml file), or an EntityManager (in the map passed to the
     * createEntityManager method). Note that the latter overrides the former.
     * <p>
     * Values:
     * <ul>
     * <li>"Transactional" (DEFAULT):
     * {@link ExclusiveConnectionMode#Transactional}
     * <li>"Isolated" - {@link ExclusiveConnectionMode#Isolated}
     * <li>"Always" - {@link ExclusiveConnectionMode#Always}
     * </ul>
     * 
     * @see ExclusiveConnectionMode
     */
    public static final String EXCLUSIVE_CONNECTION_MODE = "eclipselink.jdbc.exclusive-connection.mode";

    /**
     * The <code>"eclipselink.jdbc.exclusive-connection.is-lazy"</code> property
     * specifies when write connection is acquired lazily.
     * <p>
     * Values:
     * <ul>
     * <li>"false"
     * <li>"true" (DEFAULT)
     * </ul>
     */
    public static final String EXCLUSIVE_CONNECTION_IS_LAZY = "eclipselink.jdbc.exclusive-connection.is-lazy";

    /**
     * The <code>"eclipselink.jdbc.cache-statements.size"</code> property
     * specifies the number of statements held when using internal statement
     * caching. The value must be a string value containing a positive integer
     * or zero.
     * <p>
     * Default: "50".
     */
    public static final String CACHE_STATEMENTS_SIZE = "eclipselink.jdbc.cache-statements.size";

    /**
     * The <code>"eclipselink.jdbc.cache-statements"</code> property specifies
     * whether JDBC statements should be cached. This is recommended when using
     * EclipseLink's internal connection pooling.
     * <p>
     * Valid values:
     * <ul>
     * <li>true – enable internal statement caching.
     * <li>false – disable internal statement caching. (DEFAULT)
     * </ul>
     * <p>
     * Example: persistence.xml file <code>
     * <property name="eclipselink.jdbc.cache-statements" value="false"/>
     * </code> Example: property Map <code>
     * propertiesMap.put(PersistenceUnitProperties.CACHE_STATEMENTS, "false");
     * </code>
     */
    public static final String CACHE_STATEMENTS = "eclipselink.jdbc.cache-statements";

    // Bean Validation properties

    /**
     * The <code>"javax.persistence.validation.factory"</code> property
     * specifies an instance of <a href =
     * http://java.sun.com/javaee/6/docs/api/javax/validation
     * /ValidatorFactory.html>javax.validation.ValidatorFactory</a> used by
     * EclipseLink to perform Automatic Validation upon Lifecycle Events. If the
     * propoerty is not specified, and if Bean Validation API is visible to
     * Eclipselink, it will try to instantiate an insance of
     * <code>javax.validation.ValidationFactory</code> by calling
     * <code>Validation.buildDefaultValidatorFactory()</code>
     */
    public static final String VALIDATOR_FACTORY = "javax.persistence.validation.factory";

    /**
     * The <code>"javax.persistence.validation.mode"</code> property specifies
     * whether the automatic lifecycle event validation is in effect.
     * <p>
     * Valid values for this property are "AUTO", "CALLBACK" or "NONE".
     */
    public static final String VALIDATION_MODE = "javax.persistence.validation.mode";

    /**
     * The <code>"javax.persistence.validation.group.pre-persist"</code>
     * property specifies the name of the validator groups to execute for
     * preUpdate event. The value should be a string with fully qualified
     * classnames separated by a comma (','). If this value is not specified in
     * both persistence.xml or using this property, the default Bean Validation
     * group (the group Default) will be validated
     */
    public static final String VALIDATION_GROUP_PRE_PERSIST = "javax.persistence.validation.group.pre-persist";

    /**
     * The <code>"javax.persistence.validation.group.pre-update"</code> property
     * specifies the name of the validator groups to execute for preUpdate
     * event. The value should be a string with fully qualified classnames
     * separated by a comma (','). If this value is not specified in both
     * persistence.xml or using this property, the default Bean Validation group
     * (the group Default) will be validated
     */
    public static final String VALIDATION_GROUP_PRE_UPDATE = "javax.persistence.validation.group.pre-update";

    /**
     * The <code>"javax.persistence.validation.group.pre-remove"</code> property
     * specifies the name of the validator groups to execute for preRemove
     * event. The value should be a string with fully qualified classnames
     * separated by a comma (','). If this value is not specified in both
     * persistence.xml or using this property, no validation will occur on
     * remove.
     */
    public static final String VALIDATION_GROUP_PRE_REMOVE = "javax.persistence.validation.group.pre-remove";

    // Caching Properties

    /**
     * Default Suffix could be appended to some prefixes to form a property name
     * setting the default for the persistence unit.
     * 
     * @see #CACHE_SIZE_
     * @see #CACHE_TYPE_
     * @see #CACHE_SHARED_
     */
    public static final String DEFAULT = "default";

    /**
     * Property prefix <code>"eclipselink.cache.size.</code> used to specify the cache size for a specific
     * entity type. The prefix must be followed by a valid entity type name.
     * <p>
     * Property names formed out of these prefixes by appending either entity
     * name, or class name (indicating that the property values applies only to
     * a particular entity) or DEFAULT suffix (indicating that the property
     * value applies to all entities). For most cache types, the size is only
     * the initial size, not a fixed or maximum size. For CacheType.SoftCache
     * and CacheType.HardCache the size is the sub-cache size. The default cache
     * size is 100.
     * 
     * @see #CACHE_SIZE_DEFAULT
     */
    public static final String CACHE_SIZE_ = "eclipselink.cache.size.";

    /**
     * All valid values for CACHE_TYPE_ properties are declared in CacheType
     * class. The default cache type is SoftWeak. This sets the type of cache,
     * if you do not wish to cache entities at all, then set CACHE_SHARED_.
     * 
     * @see #CACHE_SHARED_
     * @see CacheType
     * @see #CACHE_TYPE_DEFAULT
     */
    public static final String CACHE_TYPE_ = "eclipselink.cache.type.";

    /**
     * Indicates whether entity's cache should be shared (non-isolated). Valid
     * values are case-insensitive "false" and "true"; "true" is default. If you
     * do not wish to cache your entities, set this to "false".
     * 
     * @see #CACHE_SHARED_DEFAULT
     */
    public static final String CACHE_SHARED_ = "eclipselink.cache.shared.";

    /**
     * Default caching properties - apply to all entities. May be overridden by
     * individual entity property with the same prefix. For most cache types,
     * the size is only the initial size, not a fixed or maximum size. For
     * CacheType.SoftCache and CacheType.HardCache the size is the sub-cache
     * size. The default cache size is 100.
     */
    public static final String CACHE_SIZE_DEFAULT = CACHE_SIZE_ + DEFAULT;

    /**
     * Default caching properties - apply to all entities. May be overridden by
     * individual entity property with the same prefix. The default cache type
     * is SoftWeak. This sets the type of cache, if you do not wish to cache
     * entities at all, then set CACHE_SHARED_DEFAULT.
     * 
     * @see #CACHE_SHARED_DEFAULT
     */
    public static final String CACHE_TYPE_DEFAULT = CACHE_TYPE_ + DEFAULT;

    /**
     * Default caching properties - apply to all entities. May be overridden by
     * individual entity property with the same prefix. If you do not wish to
     * cache your entities, set this to "false".
     */
    public static final String CACHE_SHARED_DEFAULT = CACHE_SHARED_ + DEFAULT;

    // Customizations properties

    /**
     * The type of logger. By default DefaultSessionLog is used. Valid values
     * are the logger class name which implements
     * org.eclipse.persistence.logging.SessionLog or one of values defined in
     * LoggerType.
     * 
     * @see LoggerType
     */
    public static final String LOGGING_LOGGER = "eclipselink.logging.logger";

    /**
     * Property <code>"eclipselink.logging.level</code> allows the default logging levels to be specified.
     * <p>
     * <b>Java example:</b> <code>
     * props.put(PersistenceUnitProperties.LOGGING_LEVEL, SessionLog.FINE_LABEL);</br>
     * <p>
     * <b>XML example:</b>
     * <code>
     * <property name="eclipselink.logging.level" value="FINE" />;</br>
     * </code>
     * <p>
     * <b>Logging Levels:</b>
     * 
     * @see SessionLog#OFF_LABEL = "OFF"
     * @see SessionLog#SEVERE_LABEL = "SEVERE"
     * @see SessionLog#WARNING_LABEL = "WARNING"
     * @see SessionLog#CONFIG_LABEL = "CONFIG"
     * @see SessionLog#INFO_LABEL = "INFO" (DEFAULT)
     * @see SessionLog#FINE_LABEL = "FINE"
     * @see SessionLog#FINER_LABEL = "FINER"
     * @see SessionLog#FINEST_LABEL = "FINEST"
     * @see SessionLog#ALL_LABEL = "ALL"
     * 
     * @see #CATEGORY_LOGGING_LEVEL_ for category specific level configuration
     */
    public static final String LOGGING_LEVEL = "eclipselink.logging.level";

    /**
     * Property prefix <code>"eclipselink.logging.level.</code> allows the category specific logging levels to be
     * specified.
     * <p>
     * <b>Java example:</b> <code>
     * props.put(PersistenceUnitProperties.CATEGORY_LOGGING_LEVEL_ + SessionLog.EJB_OR_METADATA, SessionLog.WARNING_LABEL);</br>
     * <p>
     * <b>XML example:</b>
     * <code>
     * <property name="eclipselink.logging.level.ejb_or_metadata" value="WARNING" />;</br>
     * </code> specific Valid values are names of levels defined in
     * java.util.logging.Level, default value is INFO.
     * <p>
     * <b>Categories:</b>
     * 
     * @see SessionLog#SQL
     * @see SessionLog#TRANSACTION
     * @see SessionLog#EVENT
     * @see SessionLog#CONNECTION
     * @see SessionLog#QUERY
     * @see SessionLog#CACHE
     * @see SessionLog#PROPAGATION
     * @see SessionLog#SEQUENCING
     * @see SessionLog#EJB_OR_METADATA
     * @see SessionLog#METAMODEL
     * @see SessionLog#WEAVER
     * @see SessionLog#PROPERTIES
     * @see SessionLog#SERVER
     */
    public static final String CATEGORY_LOGGING_LEVEL_ = LOGGING_LEVEL + ".";

    /**
     * The <code>"eclipselink.logging.timestamp"</code> property configures if
     * the current timestamp should be included in each log message.
     * <p>
     * Values:
     * <ul>
     * <li>"true" (Default)
     * <li>"false"
     * </ul>
     */
    public static final String LOGGING_TIMESTAMP = "eclipselink.logging.timestamp";

    /**
     * Property <code>"eclipselink.logging.thread"</code> indicating if current
     * thread should have its identity included in each log message.
     * <p>
     * By default ("true") the thread is logged at FINE or less level. This can
     * be turned off ("false") or on ("true"). Values:
     * <ul>
     * <li>"true" (Default)
     * <li>"false"
     * </ul>
     */
    public static final String LOGGING_THREAD = "eclipselink.logging.thread";

    /**
     * Property <code>"eclipselink.logging.session"</code> indicating if the
     * session in use should have its identity included in each log message.
     * <p>
     * Values:
     * <ul>
     * <li>"true" (Default)
     * <li>"false"
     * </ul>
     */
    public static final String LOGGING_SESSION = "eclipselink.logging.session";

    /**
     * Property: <code>"eclipselink.logging.connection"</code>
     * <p>
     * Values:
     * <ul>
     * <li>"true" (Default)
     * <li>"false"
     * </ul>
     */
    public static final String LOGGING_CONNECTION = "eclipselink.logging.connection";

    /**
     * Values:
     * <ul>
     * <li>"true" (Default)
     * <li>"false"
     * </ul>
     */
    public static final String LOGGING_EXCEPTIONS = "eclipselink.logging.exceptions";

    /**
     * The <code>"eclipselink.logging.file"</code> property configures a file
     * location where the log will be output to instead of standard out.
     */
    public static final String LOGGING_FILE = "eclipselink.logging.file";

    // Platforms & Customization

    /**
     * The <code>"eclipselink.target-database"</code> property configures the
     * database that will be used controlling custom operations and SQL
     * generation for the specified database.
     * <p>
     * Values: Either a short string values as defined in {@link TargetDatabase}
     * or a fully qualified class name for a class that extends
     * {@link DatabasePlatform}. The default value is TargetDatabase.Auto which
     * means EclipseLink will try to automatically determine the correct
     * database platform type.
     * 
     * @see TargetDatabase
     * @see DatabasePlatform
     */
    public static final String TARGET_DATABASE = "eclipselink.target-database";

    /**
     * The <code>"eclipselink.exclude-eclipselink-orm"</code> property
     * configures the exclusion of an EclipseLink ORM mapping file for a
     * specific persistence unit. By default the first file found at the
     * resource name: "META-INF/eclipselink-orm.xml" is processed and overrides
     * configurations specified in annotations, and standard mapping files.
     */
    public static final String EXCLUDE_ECLIPSELINK_ORM_FILE = "eclipselink.exclude-eclipselink-orm";

    /**
     * The <code>"eclipselink.session-name"</code> property configures a
     * specific name to use when storing the singleton server session within the
     * {@link SessionManager}.
     * <p>
     * By default a unique session name is generated by EclipseLink, but the
     * user can provide a customary session name - and make sure it's unique. If
     * a sessions-xml file is used this must be the name of the session in the
     * sessions-xml file.
     */
    public static final String SESSION_NAME = "eclipselink.session-name";

    // Weaving Properties

    /**
     * The <code>"eclipselink.weaving"</code> property configures whether
     * weaving should be performed. Weaving is requires for lazy OneToOne,
     * ManyToOne, Basic, attribute change tracking, fetch groups, and other
     * optimizations.
     * <p>
     * Values (case insensitive):
     * <ul>
     * <li>Not Set - defaults to "true" in Java SE using javaagent and within
     * EJB 3+ compliant containers
     * <li>"true" - requires that weaving be done. Will throw an exception if
     * entities are not woven
     * <li>"false" - forces weaving not to be done
     * <li>"static" - requires that the static weaving utility was used to weave
     * the entities
     * </ul>
     * <i>Note: Any value specified that is not in the above list is treated as
     * "static".</i>
     */
    public static final String WEAVING = "eclipselink.weaving";

    /**
     * The <code>"eclipselink.weaving.internal"</code> property indicates
     * whether internal optimizations should be enabled through weaving.
     * <p>
     * This property will only be considered if weaving is enabled.
     * <p>
     * Values (case insensitive):
     * <ul>
     * <li>"true" (DEFAULT)
     * <li>"false"
     * </ul>
     * 
     * @see #WEAVING
     */
    public static final String WEAVING_INTERNAL = "eclipselink.weaving.internal";

    /**
     * The <code>"eclipselink.weaving.lazy"</code> property configures whether
     * LAZY OneToOne and ManyToOne mappings should be enabled through weaving.
     * <p>
     * This property will only be considered if weaving is enabled.
     * <p>
     * Values (case insensitive):
     * <ul>
     * <li>"true" (DEFAULT)
     * <li>"false"
     * </ul>
     * 
     * @see #WEAVING
     */
    public static final String WEAVING_LAZY = "eclipselink.weaving.lazy";

    /**
     * The <code>"eclipselink.weaving.eager"</code> property configures whether
     * EAGER mapping's attributes should be woven to use indirection.
     * <p>
     * This property will only be considered if weaving is enabled.
     * <p>
     * Values (case insensitive):
     * <ul>
     * <li>"true"
     * <li>"false" (DEFAULT)
     * </ul>
     * 
     * @see #WEAVING
     */
    public static final String WEAVING_EAGER = "eclipselink.weaving.eager";

    /**
     * The <code>"eclipselink.weaving.changetracking"</code> property configures
     * whether AttributeLevelChangeTracking should be enabled through weaving.
     * When this is enabled, only classes with all mappings allowing change
     * tracking will have change tracking enabled. Mutable basic attributes will
     * prevent this.
     * <p>
     * This property will only be considered if weaving is enabled.
     * <p>
     * Values (case insensitive):
     * <ul>
     * <li>"true" (DEFAULT)
     * <li>"false"
     * </ul>
     * 
     * @see #WEAVING
     */
    public static final String WEAVING_CHANGE_TRACKING = "eclipselink.weaving.changetracking";

    /**
     * The <code>"eclipselink.weaving.fetchgroups"</code> property configures
     * whether FetchGroup support should be enabled through weaving. When this
     * is enabled, lazy direct mappings will be supported as well as descriptor
     * and query level FetchGroups. FetchGroups allow partial objects to be read
     * and written, access to un-fetched attributes will cause the object to be
     * refreshed/fully-fetched.
     * <p>
     * This property will only be considered if weaving is enabled.
     * <p>
     * Values (case insensitive):
     * <ul>
     * <li>"true" (DEFAULT)
     * <li>"false"
     * </ul>
     * 
     * @see #WEAVING
     * @see org.eclipse.persistence.descriptors.FetchGroupManager
     * @see org.eclipse.persistence.queries.FetchGroup
     */
    public static final String WEAVING_FETCHGROUPS = "eclipselink.weaving.fetchgroups";

    /**
     * The <code>"eclipselink.target-server"</code> property configures the
     * {@link ServerPlatform} that will be used to enable integration with a
     * host container.
     * <p>
     * Valid values are defined in TargetServer class - they correspond to
     * server platforms currently supported. Also a custom server platform may
     * be specified by supplying a full class name. Specifying a name of the
     * class implementing ExternalTransactionController sets
     * CustomServerPlatform with this controller. Default is TargetServer.None.
     * 
     * @see TargetServer
     */
    public static final String TARGET_SERVER = "eclipselink.target-server";

    /**
     * The <code>"eclipselink.session.customizer"</code> property configures a
     * {@link SessionCustomizer} used to alter the runtime configuration through
     * API.
     * <p>
     * The value is a full name for a class which implements SessionCustomizer.
     * Session customizer called after all other properties have been processed.
     * 
     * @see SessionCustomizer
     */
    public static final String SESSION_CUSTOMIZER = "eclipselink.session.customizer";

    // Under review public static final String RELATIONSHIPS_FETCH_DEFAULT =
    // "eclipselink.relationships-fetch-default";

    /**
     * The <code>"eclipselink.descriptor.customizer."</code> is a prefix for a
     * property used to configure a {@link DescriptorCustomizer}. Customization
     * Prefix Property names formed out of this prefix by appending either
     * entity name, or class name (indicating that the property values applies
     * only to a particular entity) Allows descriptor customization.
     * <p>
     * The value is a full name for a class which implements
     * DescriptorCustomizer. Only session customizer is called after processing
     * these properties.
     * 
     * @see DescriptorCustomizer
     */
    public static final String DESCRIPTOR_CUSTOMIZER_ = "eclipselink.descriptor.customizer.";

    /**
     * The <code>"eclipselink.jdbc.uppercase-columns"</code> property configures
     * native SQL queries are used, the JDBC meta-data may return column names
     * in lower case on some platforms. If the column names are upper-case in
     * the mappings (default) then they will not match. This setting allows for
     * forcing the column names from the meta-data to upper-case.
     */
    public static final String NATIVE_QUERY_UPPERCASE_COLUMNS = "eclipselink.jdbc.uppercase-columns";

    /**
     * The <code>"eclipselink.jpa.uppercase-column-names"</code> property configures JPA processing
     * to uppercase all column name definitions.  A value of true also sets the 
     * <code>"eclipselink.jdbc.uppercase-columns"</code> property to true, so that JDBC meta-data
     * returned from the database is also returned in uppercase, ensuring fields are the same case.  This
     * gets around situations where user defined fields do not match the case returned by the database for
     * native queries, simulating case insensitivity.  
     * <p>
     * Values (case insensitive):
     * <ul>
     * <li>"true" (DEFAULT)
     * <li>"false"
     * </ul>
     */
    public static final String UPPERCASE_COLUMN_NAMES = "eclipselink.jpa.uppercase-column-names";

    /**
     * The <code>"eclipselink.jdbc.batch-writing"</code> property configures the
     * use of batch writing to optimize transactions with multiple writes.
     * <p>
     * Values (case insensitive):
     * <ul>
     * <li>"JDBC": use JDBC batch writing.
     * <li>"Buffered": do not use either JDBC batch writing nor native platform
     * batch writing.
     * <li>"Oracle-JDBC": use Oracle's native batch writing. This requires the
     * use of an Oracle JDBC driver.
     * <li>"None" (DEFAULT): do not use batch writing (turn it off).
     * </ul>
     * 
     * @see BatchWriting
     */
    public static final String BATCH_WRITING = "eclipselink.jdbc.batch-writing";

    /**
     * The <code>"eclipselink.jdbc.batch-writing.size"</code> property
     * configures the batch size used for batch writing. For parameterized batch
     * writing this is the number of statements to batch, default 100. For
     * dynamic batch writing, this is the size of the batched SQL buffer,
     * default 32k.
     * 
     * @see #BATCH_WRITING
     */
    public static final String BATCH_WRITING_SIZE = "eclipselink.jdbc.batch-writing.size";

    /**
     * The <code>"eclipselink.persistencexml"</code> property specifies the full
     * resource name to look for the persistence XML files in. If not specified
     * the default value defined by {@link #ECLIPSELINK_PERSISTENCE_XML_DEFAULT}
     * will be used.
     * <p>
     * IMPORTANT: For now this property is used for the canonical model
     * generator but it can later be used as a system property for customizing
     * weaving and application bootstrap usage.
     * <p>
     * This property is only used by EclipseLink when it is locating the
     * configuration file. When used within an EJB/Spring container in container
     * managed mode the locating and reading of this file is done by the
     * container and will not use this configuration.
     */
    public static final String ECLIPSELINK_PERSISTENCE_XML = "eclipselink.persistencexml";

    /**
     * The default resource location used to locate the persistence.xml
     * configuration files. Default: "META-INF/persistence.xml"
     */
    public static final String ECLIPSELINK_PERSISTENCE_XML_DEFAULT = "META-INF/persistence.xml";

    /**
     * This <code>"eclipselink.persistenceunits"</code> property specifies the
     * set of persistence unit names that will be processed when generating the
     * canonical model. By default all persistence units available in all
     * persistence XML files will be used. The value of this property is a comma
     * separated list. When specifying multiple persistence units it is not
     * possible to have persistence units with a comma in their name.
     * <p>
     * Note: In the future this property can also be used to limit the
     * persistence units that will be processed by the static or dynamic
     * weaving.
     */
    public static final String ECLIPSELINK_PERSISTENCE_UNITS = "eclipselink.persistenceunits";

    /**
     * The <code>"eclipselink.exception-handler"</code> property allows an
     * {@link ExceptionHandler} to be specified. An ExceptionHandler handles
     * exceptions when they are thrown so that an application might address
     * address expected failures and continue.
     * 
     * @see ExceptionHandler
     */
    public static final String EXCEPTION_HANDLER_CLASS = "eclipselink.exception-handler";

    /**
     * The <code>"eclipselink.session.include.descriptor.queries"</code>
     * property configures whether to enable the copying of all descriptor named
     * queries to the session to be usable from the entity manager.
     * <p>
     * Default: "true".
     */
    public static final String INCLUDE_DESCRIPTOR_QUERIES = "eclipselink.session.include.descriptor.queries";

    /**
     * The <code>"eclipselink.session-event-listener"</code> property configures
     * a session event listener class.
     * 
     * @see SessionEventListener
     * @see #SESSION_CUSTOMIZER as a means to configure multiple listeners using
     *      API
     */
    public static final String SESSION_EVENT_LISTENER_CLASS = "eclipselink.session-event-listener";

    /**
     * The <code>"eclipselink.sessions-xml"</code> property configures the use
     * of the specified native sessions.xml configuration file. When specified
     * this file will load all of the session configuration and mapping
     * information from the native XML. No JPA annotations or XML will be used.
     */
    public static final String SESSIONS_XML = "eclipselink.sessions-xml";

    /**
     * The <code>"eclipselink.temporal.mutable"</code> property configures the
     * default for detecting changes to temporal field (Date, Calendar). Default
     * "false" (changes to date object itself are not detected). By default it
     * is assumed that temporal fields are replaced, and the temporal object not
     * changed directly. Enabling mutable temporal fields will cause weaving of
     * attribute change tracking to be disabled.
     */
    public static final String TEMPORAL_MUTABLE = "eclipselink.temporal.mutable";

    /**
     * The <code>"eclipselink.allow-zero-id"</code> property configures if zero
     * is considered a valid id on a new entity. If the id is not considered
     * valid and sequencing is enabled for the entity a new value will be
     * assigned when it is persisted to the database (INSERT)By default an id
     * value of 0 is assumed to be null/unassigned. This allows 0 to be a valid
     * id value. This can also be set per class using the @PrimaryKey annotation
     * and IdValidation.
     * <p>
     * Caution: This property configures the static singleton on
     * {@link Helper#isZeroValidPrimaryKey} which will be shared by all
     * concurrent uses of EclipseLink.
     * 
     * @see org.eclipse.persistence.annotations.PrimaryKey
     * @see org.eclipse.persistence.annotations.IdValidation
     * 
     * @deprecated replaced by ID_VALIDATION property with value "NULL".
     */
    public static final String ALLOW_ZERO_ID = "eclipselink.allow-zero-id";

    /**
     * The <code>"eclipselink.id-validation"</code> property defines
     * which primary key componets values are considered invalid.
     * These values will be also overridden by sequencing.
     * Note that Identity always overrides any existing id value
     * and so does any sequence with shouldAlwaysOverrideExistingValue flag set to true.
     * 
     * @see org.eclipse.persistence.annotations.PrimaryKey
     * @see org.eclipse.persistence.annotations.IdValidation
     */
    public static final String ID_VALIDATION = "eclipselink.id-validation";

    /**
     * Defines EntityManager cache behavior after a call to flush method
     * followed by a call to clear method. This property could be specified
     * while creating either EntityManagerFactory (either in the map passed to
     * createEntityManagerFactory method or in persistence.xml) or EntityManager
     * (in the map passed to createEntityManager method); the latter overrides
     * the former.
     * 
     * @see FlushClearCache
     */
    public static final String FLUSH_CLEAR_CACHE = "eclipselink.flush-clear.cache";

    /**
     * Specify the classloader to use to create an EntityManagerFactory in the
     * property map passed to Persistence.createEntityManagerFactory.
     */
    public static final String CLASSLOADER = "eclipselink.classloader";

    /**
     * Configures if the first exception that occurs during deployment should be
     * thrown, or if all exceptions should be caught and summary thrown at end
     * of deployment attempt. "true" by default.
     */
    public static final String THROW_EXCEPTIONS = "eclipselink.orm.throw.exceptions";

    /**
     * This property set on the session is used to override orm.xml schema
     * validation from its default of false.
     */
    public static final String ORM_SCHEMA_VALIDATION = "eclipselink.orm.validate.schema";

    /** Validate deployment, but do not connect. */
    public static final String VALIDATION_ONLY_PROPERTY = "eclipselink.validation-only";

    /**
     * Allows the database schema to be generated on deployment. Valid values,
     * CREATE_ONLY, DROP_AND_CREATE, NONE ("create-tables",
     * "drop-and-create-tables", "none"). Default is NONE.
     */
    public static final String DDL_GENERATION = "eclipselink.ddl-generation";

    public static final String CREATE_ONLY = "create-tables";
    public static final String DROP_AND_CREATE = "drop-and-create-tables";
    public static final String NONE = "none";

    public static final String APP_LOCATION = "eclipselink.application-location";

    public static final String CREATE_JDBC_DDL_FILE = "eclipselink.create-ddl-jdbc-file-name";
    public static final String DROP_JDBC_DDL_FILE = "eclipselink.drop-ddl-jdbc-file-name";

    public static final String DEFAULT_APP_LOCATION = "." + File.separator;
    public static final String DEFAULT_CREATE_JDBC_FILE_NAME = "createDDL.jdbc";
    public static final String DEFAULT_DROP_JDBC_FILE_NAME = "dropDDL.jdbc";
    public static final String JAVASE_DB_INTERACTION = "INTERACT_WITH_DB";

    /**
     * Configures if database schema should be generated on the database, to a
     * file, or both. Valid values, DDL_SQL_SCRIPT_GENERATION,
     * DDL_DATABASE_GENERATION, DDL_BOTH_GENERATION ("sql-script", "database",
     * "both") DDL_GENERATION must also be set, for this to have an effect.
     * Default is DDL_DATABASE_GENERATION.
     */
    public static final String DDL_GENERATION_MODE = "eclipselink.ddl-generation.output-mode";
    public static final String DDL_SQL_SCRIPT_GENERATION = "sql-script";
    public static final String DDL_DATABASE_GENERATION = "database";
    public static final String DDL_BOTH_GENERATION = "both";
    public static final String DEFAULT_DDL_GENERATION_MODE = DDL_DATABASE_GENERATION;

    /**
     * The <code>"eclipselink.validate-existence"</code> property configures if
     * the existence of an object should be verified on persist(), otherwise it
     * will assume to be new if not in the persistence context. If checked and
     * existing and not in the persistence context and error will be thrown.
     * "false" by default.
     */
    public static final String VALIDATE_EXISTENCE = "eclipselink.validate-existence";

    /**
     * Configures if updates should be ordered by primary key.
     * This can be used to avoid possible database deadlocks from concurrent threads
     * updating the same objects in different order.
     * If not set to true, the order of updates is not guaranteed.
     * "false" by default.
     */    
    public static final String ORDER_UPDATES = "eclipselink.order-updates";
    
    /**
     * The <code>"eclipselink.profiler"</code>property configures the type of
     * profiler used to capture runtime statistics.
     * <p>
     * Values (case insensitive):
     * <ul>
     * <li>"NoProfiler" (DEFAULT)
     * <li>"PerformanceProfiler": Use {@link PerformanceProfiler}
     * <li>"QueryMonitor": Use {@link QueryMonitor}
     * <li>Custom profiler – Specify a custom profiler class name which
     * implements {@link SessionProfiler}
     * </ul>
     * 
     * @see ProfilerType
     */
    public static final String PROFILER = "eclipselink.profiler";

    /**
     * The <code>"eclipselink.transaction.join-existing"</code> property Set to
     * "true" this property forces persistence context to read through
     * JTA-managed ("write") connection in case there is an active
     * transaction.The property set in persistence.xml or passed to
     * createEntityManagerFactory affects all EntityManagers created by the
     * factory. Note that if the property set to "true" then objects read during
     * transaction won't be placed into the shared cache unless they have been
     * updated. Alternatively, to apply the property only to some
     * SessionManagers pass it to createEntityManager method.
     * <p>
     * Values (caseinsensitive):
     * <ul>
     * <li>"false" (DEFAULT):
     * <li>"true":
     * </ul>
     */
    public static final String JOIN_EXISTING_TRANSACTION = "eclipselink.transaction.join-existing";

    /**
     * The <code>"eclipselink.persistence-context.reference-mode"</code>
     * property configures whether there should be hard or soft references used
     * within the Persistence Context. Default is "HARD". With soft references
     * entities no longer referenced by the application may be garbage collected
     * freeing resources. Any changes that have not been flushed in these
     * entities will be lost. The property set in persistence.xml or passed to
     * createEntityManagerFactory affects all EntityManagers created by the
     * factory. Alternatively, to apply the property only to some
     * SessionManagers pass it to createEntityManager method.
     * <p>
     * Values:<ul>
     * <li>"HARD" (DEFAULT) - {@link ReferenceMode#HARD
     * <li>"WEAK"  - {@link ReferenceMode#WEAK
     * <li>"FORCE_WEAK" - {@link ReferenceMode#FORCE_WEAK
     * </ul>
     * 
     * @see ReferenceMode
     */
    public static final String PERSISTENCE_CONTEXT_REFERENCE_MODE = "eclipselink.persistence-context.reference-mode";

    /**
     * The <code>"javax.persistence.lock.timeout"</code> property configures the
     * WAIT timeout used in pessimistic locking, if the database query exceeds
     * the timeout the database will terminate the query and return an
     * exception.
     * <p>
     * Valid values are Strings containing integers zero or greater.
     */
    public static final String PESSIMISTIC_LOCK_TIMEOUT = QueryHints.PESSIMISTIC_LOCK_TIMEOUT;

    /**
     * The <code>"javax.persistence.query.timeout"</code> property configures
     * the default query timeout value.
     * <p>
     * Valid values are strings containing integers zero or greater.
     */
    public static final String QUERY_TIMEOUT = "javax.persistence.query.timeout";

    /**
     * The <code>"eclipselink.persistence-context.close-on-commit"</code>
     * property specifies that the EntityManager will be closed or not used
     * after commit (not extended). In general this is normally always the case
     * for a container managed EntityManager, and common for application
     * managed. This can be used to avoid additional performance overhead of
     * resuming the persistence context after a commit(). The property set in
     * persistence.xml or passed to createEntityManagerFactory affects all
     * EntityManagers created by the factory. Alternatively, to apply the
     * property only to some EntityManagers pass it to createEntityManager
     * method.
     * <p>
     * Values (case insensitive):
     * <ul>
     * <li>"false" (DEFAULT)
     * <li>"true"
     * </ul>
     */
    public static final String PERSISTENCE_CONTEXT_CLOSE_ON_COMMIT = "eclipselink.persistence-context.close-on-commit";

    /**
     * The <code>"eclipselink.persistence-context.persist-on-commit"</code>
     * property specifies that the EntityManager will search all managed objects
     * and persist any related non-managed new objects that are cascade persist.
     * This can be used to avoid the cost of performing this search if persist
     * is always used for new objects. The property set in persistence.xml or
     * passed to createEntityManagerFactory affects all EntityManagers created
     * by the factory. Alternatively, to apply the property only to some
     * EntityManagers pass it to createEntityManager method.
     * <p>
     * Values (case insensitive):
     * <ul>
     * <li>"true" (DEFAULT)
     * <li>"false"
     * </ul>
     */
    public static final String PERSISTENCE_CONTEXT_PERSIST_ON_COMMIT = "eclipselink.persistence-context.persist-on-commit";

    /**
     * The
     * <code>"eclipselink.persistence-context.commit-without-persist-rules"</code>
     * property specifies that the EntityManager will search all managed objects
     * and persist any related non-managed new objects that are found ignoring
     * any absence of CascadeType.PERSIST settings. Also the Entity life-cycle
     * Persist operation will not be cascaded to related entities. This setting
     * replicates the traditional EclipseLink native functionality.
     * <p>
     * Values (case insensitive):
     * <ul>
     * <li>"false" (DEFAULT)
     * <li>"true"
     * </ul>
     */
    public static final String PERSISTENCE_CONTEXT_COMMIT_WITHOUT_PERSIST_RULES = "eclipselink.persistence-context.commit-without-persist-rules";

    /**
     * The <code>"eclipselink.persistence-context.flush-mode</code> property configures the EntityManager FlushMode to be set as a
     * persistence property. This can be set to either "AUTO" or "COMMIT". By
     * default the flush mode is AUTO, which requires an automatic flush before
     * all query execution. This can be used to avoid any flushing until commit.
     * The property set in persistence.xml or passed to
     * createEntityManagerFactory affects all EntityManagers created by the
     * factory. Alternatively, to apply the property only to some EntityManagers
     * pass it to createEntityManager method.
     * 
     * @see EntityManager#setFlushMode(javax.persistence.FlushModeType)
     * @see FlushModeType
     */
    public static final String PERSISTENCE_CONTEXT_FLUSH_MODE = "eclipselink.persistence-context.flush-mode";

    /**
     * The <code>"eclipselink.oracle.proxy-type"</code> property is used to
     * specify proxy type that should be passed to
     * OracleConnection.openProxySession method. Requires Oracle JDBC version
     * 10.1.0.2 or later. Requires Oracle9Platform or later as a database
     * platform (TARGET_DATABASE property value should be TargetDatabase.Oracle9
     * or later).
     * <p>
     * Values:
     * <ul>
     * <li>oracle.jdbc.OracleConnection.PROXYTYPE_USER_NAME,
     * <li>oracle.jdbc.OracleConnection.PROXYTYPE_DISTINGUISHED_NAME,
     * <li>oracle.jdbc.OracleConnection.PROXYTYPE_CERTIFICATE.
     * </ul>
     * <p>
     * Property values corresponding to the specified type should be also
     * provided:
     * <ul>
     * <li>oracle.jdbc.OracleConnection.PROXY_USER_NAME,
     * <li>oracle.jdbc.OracleConnection.PROXY_DISTINGUISHED_NAME,
     * <li>oracle.jdbc.OracleConnection.PROXY_CERTIFICATE.
     * <p>
     * Typically these properties should be set into EntityManager (either
     * through createEntityManager method or using proprietary setProperties
     * method on EntityManagerImpl) - that causes EntityManager to use proxy
     * connection for writing and reading inside transaction. If proxy-type and
     * the corresponding proxy property set into EntityManagerFactory then all
     * connections created by the factory will be proxy connections.
     */
    public static final String ORACLE_PROXY_TYPE = "eclipselink.oracle.proxy-type";

    /**
     * The <code>"eclipselink.cache.coordination.protocol"</code> property
     * configures cache coordination for a clustered environment. This needs to
     * be set on every persistence unit/session in the cluster. Depending on the
     * cache configuration for each descriptor, this will broadcast cache
     * updates or inserts to the cluster to update or invalidate each session's
     * cache.
     * <p>
     * Values:
     * <ul>
     * <li>"jms"
     * <li>"rmi"
     * </ul>
     * by default the cache is not coordinated.
     * 
     * @see Cache#coordinationType()
     * @see CacheCoordinationType
     * @see RemoteCommandManager#setTransportManager(TransportManager)
     */
    public static final String COORDINATION_PROTOCOL = "eclipselink.cache.coordination.protocol";

    /**
     * The <code>"eclipselink.cache.coordination.jms.host"</code> property
     * configures cache coordination for a clustered environment. Only used for
     * JMS coordination. Sets the URL for the JMS server hosting the topic.
     * 
     * @see #COORDINATION_PROTOCOL
     * @see org.eclipse.persistence.sessions.coordination.jms.JMSTopicTransportManager#setTopicHostUrl(String)
     */
    public static final String COORDINATION_JMS_HOST = "eclipselink.cache.coordination.jms.host";

    /**
     * The <code>"eclipselink.cache.coordination.jms.topic"</code> property
     * configures cache coordination for a clustered environment. Only used for
     * JMS coordination. Sets the JMS topic name.
     * 
     * @see #COORDINATION_PROTOCOL
     * @see org.eclipse.persistence.sessions.coordination.broadcast.BroadcastTransportManager#setTopicName(String)
     */
    public static final String COORDINATION_JMS_TOPIC = "eclipselink.cache.coordination.jms.topic";

    /**
     * The <code>"eclipselink.cache.coordination.jms.factory"</code> property
     * configures cache coordination for a clustered environment. Only used for
     * JMS coordination. Sets the JMS topic connection factory name.
     * 
     * @see #COORDINATION_PROTOCOL
     * @see org.eclipse.persistence.sessions.coordination.broadcast.BroadcastTransportManager#setTopicConnectionFactoryName(String)
     */
    public static final String COORDINATION_JMS_FACTORY = "eclipselink.cache.coordination.jms.factory";
    
    /**
     * The <code>"eclipselink.cache.coordination.jms.reuse-topic-publisher"</code> property
     * configures cache coordination for a clustered environment. Only used for
     * JMS coordination. Sets the JSM transport manager to cache a TopicPubliser 
     * and reuse it for all cache coordination publishing. Default value if unset is false.
     *
     * @see #COORDINATION_PROTOCOL
     * @see org.eclipse.persistence.sessions.coordination.jms.JMSPublishingTransportManager#setShouldReuseJMSTopicPublisher(boolean)
     */
    public static final String COORDINATION_JMS_REUSE_PUBLISHER = "eclipselink.cache.coordination.jms.reuse-topic-publisher";

    /**
     * The <code>"eclipselink.cache.coordination.rmi.announcement-delay"</code>
     * property configures cache coordination for a clustered environment. Only
     * used for RMI coordination. Sets the number of milliseconds to wait for
     * announcements from other cluster members on startup.
     * 
     * @see #COORDINATION_PROTOCOL
     * @see org.eclipse.persistence.sessions.coordination.DiscoveryManager#setAnnouncementDelay(int)
     */
    public static final String COORDINATION_RMI_ANNOUNCEMENT_DELAY = "eclipselink.cache.coordination.rmi.announcement-delay";

    /**
     * The <code>"eclipselink.cache.coordination.rmi.multicast-group"</code>
     * property configures cache coordination for a clustered environment. Only
     * used for RMI coordination. Sets the multicast socket group address. The
     * multicast group is used to find other members of the cluster.
     * 
     * @see #COORDINATION_PROTOCOL
     * @see org.eclipse.persistence.sessions.coordination.DiscoveryManager#setMulticastGroupAddress(String)
     */
    public static final String COORDINATION_RMI_MULTICAST_GROUP = "eclipselink.cache.coordination.rmi.multicast-group";

    /**
     * The
     * <code>"eclipselink.cache.coordination.rmi.multicast-group.port"</code>
     * property configures cache coordination for a clustered environment. Only
     * used for RMI coordination. Sets the multicast socket group port. The
     * multicast group port is used to find other members of the cluster.
     * 
     * @see #COORDINATION_PROTOCOL
     * @see org.eclipse.persistence.sessions.coordination.DiscoveryManager#setMulticastPort(int)
     */
    public static final String COORDINATION_RMI_MULTICAST_GROUP_PORT = "eclipselink.cache.coordination.rmi.multicast-group.port";

    /**
     * The <code>"eclipselink.cache.coordination.rmi.packet-time-to-live"</code>
     * property configures cache coordination for a clustered environment. Only
     * used for RMI coordination. Sets the multicast socket packet time to live.
     * The multicast group is used to find other members of the cluster. Set the
     * number of hops the data packets of the session announcement will take
     * before expiring. The default is 2, a hub and an interface card to prevent
     * the data packets from leaving the local network.
     * 
     * Note that if sessions are hosted on different LANs that are part of WAN,
     * the announcement sending by one session may not reach other sessions. In
     * this case, consult your network administrator for the right time-to-live
     * value or test your network by increase the value until sessions receive
     * announcement sent by others.
     * 
     * @see #COORDINATION_PROTOCOL
     * @see org.eclipse.persistence.sessions.coordination.DiscoveryManager#setPacketTimeToLive(int)
     */
    public static final String COORDINATION_RMI_PACKET_TIME_TO_LIVE = "eclipselink.cache.coordination.rmi.packet-time-to-live";

    /**
     * The <code>"eclipselink.cache.coordination.rmi.url"</code> property
     * configures cache coordination for a clustered environment. Only used for
     * RMI coordination. Sets the URL of the host server. This is the URL that
     * other cluster member should use to connect to this host.
     * 
     * @see #COORDINATION_PROTOCOL
     * @see org.eclipse.persistence.sessions.coordination.RemoteCommandManager#setUrl(String)
     */
    public static final String COORDINATION_RMI_URL = "eclipselink.cache.coordination.rmi.url";

    /**
     * The <code>"eclipselink.cache.coordination.naming-service"</code> property
     * configures cache coordination for a clustered environment. Set the naming
     * service to use, either "jndi" or "rmi".
     * 
     * @see #COORDINATION_PROTOCOL
     * @see org.eclipse.persistence.sessions.coordination.TransportManager#setNamingServiceType(int)
     */
    public static final String COORDINATION_NAMING_SERVICE = "eclipselink.cache.coordination.naming-service";

    /**
     * the <code>"eclipselink.cache.coordination.jndi.user"</code> property
     * configures cache coordination for a clustered environment. Set the JNDI
     * naming service user name.
     * 
     * @see #COORDINATION_PROTOCOL
     * @see org.eclipse.persistence.sessions.coordination.TransportManager#setUserName(String)
     */
    public static final String COORDINATION_JNDI_USER = "eclipselink.cache.coordination.jndi.user";

    /**
     * The <code>"eclipselink.cache.coordination.jndi.password"</code> property
     * configures cache coordination for a clustered environment. Set the JNDI
     * naming service user name.
     * 
     * @see #COORDINATION_PROTOCOL
     * @see org.eclipse.persistence.sessions.coordination.TransportManager#setPassword(String)
     */
    public static final String COORDINATION_JNDI_PASSWORD = "eclipselink.cache.coordination.jndi.password";

    /**
     * The
     * <code>"eclipselink.cache.coordination.jndi.initial-context-factory"</code>
     * property configures cache coordination for a clustered environment. Set
     * the JNDI InitialContext factory.
     * 
     * @see #COORDINATION_PROTOCOL
     * @see org.eclipse.persistence.sessions.coordination.TransportManager#setInitialContextFactoryName(String)
     */
    public static final String COORDINATION_JNDI_CONTEXT = "eclipselink.cache.coordination.jndi.initial-context-factory";

    /**
     * The
     * <code>"eclipselink.cache.coordination.remove-connection-on-error"</code>
     * property configures cache coordination for a clustered environment. Set
     * if the connection should be removed if a communication error occurs when
     * coordinating with it. This is normally used for RMI coordination in case
     * a server goes down (it will reconnect when it comes back up).
     * 
     * @see #COORDINATION_PROTOCOL
     * @see org.eclipse.persistence.sessions.coordination.TransportManager#setShouldRemoveConnectionOnError(boolean)
     */
    public static final String COORDINATION_REMOVE_CONNECTION = "eclipselink.cache.coordination.remove-connection-on-error";

    /**
     * the
     * <code>"eclipselink.cache.coordination.propagate-asynchronously"</code>
     * property configures cache coordination for a clustered environment. Set
     * if the coordination broadcast should occur asynchronously with the
     * committing thread. This means the coordination will be complete before
     * the thread returns from the commit of the transaction. Note that JMS is
     * always asynchronous. By default RMI is asynchronous.
     * 
     * @see #COORDINATION_PROTOCOL
     * @see org.eclipse.persistence.sessions.coordination.RemoteCommandManager#setShouldPropagateAsynchronously(boolean)
     */
    public static final String COORDINATION_ASYNCH = "eclipselink.cache.coordination.propagate-asynchronously";

    /**
     * the <code>"eclipselink.cache.coordination.channel"</code> property
     * configures cache coordination for a clustered environment. Set if the
     * channel for this cluster. All server's in the same channel will be
     * coordinated.
     * 
     * @see #COORDINATION_PROTOCOL
     * @see org.eclipse.persistence.sessions.coordination.RemoteCommandManager#setChannel(String)
     */
    public static final String COORDINATION_CHANNEL = "eclipselink.cache.coordination.channel";

    /**
     * INTERNAL: The following properties will not be displayed through logging
     * but instead have an alternate value shown in the log.
     */
    public static final Map<String, String> PROPERTY_LOG_OVERRIDES = new HashMap<String, String>(1);

    static {
        PROPERTY_LOG_OVERRIDES.put(JDBC_PASSWORD, "xxxxxx");
    }

    /**
     * INTERNAL: Return the overridden log string.
     */
    public static String getOverriddenLogStringForProperty(String propertyName) {
        return PROPERTY_LOG_OVERRIDES.get(propertyName);
    }

    /**
     * INTERNAL: The following properties passed to
     * {@link Persistence#createEntityManagerFactory(String, Map)} cached and
     * processed on the {@link EntityManagerFactory} directly. None of these
     * properties processed during pre-deploy or deploy.
     **/
    private static final Set<String> supportedNonServerSessionProperties = new HashSet<String>() {
        {
            add(JOIN_EXISTING_TRANSACTION);
            add(PERSISTENCE_CONTEXT_REFERENCE_MODE);
            add(PERSISTENCE_CONTEXT_FLUSH_MODE);
            add(PERSISTENCE_CONTEXT_CLOSE_ON_COMMIT);
            add(PERSISTENCE_CONTEXT_PERSIST_ON_COMMIT);
            add(PERSISTENCE_CONTEXT_COMMIT_WITHOUT_PERSIST_RULES);
            add(VALIDATE_EXISTENCE);
            add(ORDER_UPDATES);
            add(FLUSH_CLEAR_CACHE);
        }
    };

    public static Set<String> getSupportedNonServerSessionProperties() {
        return Collections.unmodifiableSet(supportedNonServerSessionProperties);
    }

}
