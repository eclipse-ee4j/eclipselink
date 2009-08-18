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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.config;

import java.io.File;
import java.util.Map;
import java.util.HashMap;

/**
 * The class defines EclipseLink properties' names.
 * 
 * JPA persistence properties could be specified either in PersistenceUnit or 
 * passes to createEntityManagerFactory / createContainerEntityManagerFactory
 * methods of EntityManagerFactoryProvider.
 * 
 * Property values are usually case-insensitive with some common sense exceptions,
 * for instance class names.
 * 
 * @see CacheType
 * @see TargetDatabase
 * @see TargetServer
 */
public class PersistenceUnitProperties {
    /** Standard JPA PersistenceUnitTransactionType property, JTA or RESOURCE_LOCAL. */
    public static final String TRANSACTION_TYPE = "javax.persistence.transactionType";
    /** Standard JPA JTA DataSource name. */
    public static final String JTA_DATASOURCE = "javax.persistence.jtaDataSource";
    /** Standard JPA non-JTA DataSource name. */
    public static final String NON_JTA_DATASOURCE = "javax.persistence.nonJtaDataSource";
    
    /** JDBC DriverManager class name. */
    public static final String JDBC_DRIVER = "javax.persistence.jdbc.driver";
    /** JDBC DriverManager driver URL. */
    public static final String JDBC_URL = "javax.persistence.jdbc.url";
    /** DataSource or JDBC DriverManager user name. */
    public static final String JDBC_USER = "javax.persistence.jdbc.user";
    /** DataSource or JDBC DriverManager password. */
    public static final String JDBC_PASSWORD = "javax.persistence.jdbc.password";


    /**
     * EclipseLink JDBC (internal) connection pools properties. Ignored in case external connection pools are used.
     * Set the time in milliseconds to wait for an available connection.
     * If the wait time is exceeded an exception will occur.
     * The default is 180000 or 3 minutes.
     * A value of 0 means wait forever.
     */
    public static final String JDBC_CONNECTIONS_WAIT = "eclipselink.jdbc.connections.wait-timeout";
    /**
     * EclipseLink JDBC (internal) connection pools properties. Ignored in case external connection pools are used.
     * Maximum number of connections in EclipseLink connection pool.
     * If the maximum size is reached, threads requiring a connection will wait until one is released back to the pool.
     * By default a single shared (exclusive) read/write pool is used with min/max 32 connections and 1 initial.
     */
    public static final String JDBC_CONNECTIONS_MAX = "eclipselink.jdbc.connections.max";
    /**
     * EclipseLink JDBC (internal) connection pools properties. Ignored in case external connection pools are used.
     * Minimum number of connections in EclipseLink connection pool.
     * Connections beyond the minimum will be disconnected when returned to the pool, so this should normally
     * be equal to the number of active threads, or server's thread pool size.
     * By default a single shared (exclusive) read/write pool is used with min/max 32 connections and 1 initial.
     */
    public static final String JDBC_CONNECTIONS_MIN = "eclipselink.jdbc.connections.min";
    /**
     * EclipseLink JDBC (internal) connection pools properties. Ignored in case external connection pools are used.
     * Initial number of connections in EclipseLink connection pool.
     * This is the number of connections connected at startup.
     * By default a single shared (exclusive) read/write pool is used with min/max 32 connections and 1 initial.
     */
    public static final String JDBC_CONNECTIONS_INITIAL = "eclipselink.jdbc.connections.initial";
    /**
     * EclipseLink JDBC (internal) connection pools properties. Ignored in case external connection pools are used.
     * Maximum number of connections in EclipseLink write connection pool.
     * If the maximum size is reached, threads requiring a connection will wait until one is released back to the pool.
     * By default a single shared (exclusive) read/write pool is used with min/max 32 connections and 1 initial.
     */
    public static final String JDBC_WRITE_CONNECTIONS_MAX = "eclipselink.jdbc.write-connections.max";
    /**
     * EclipseLink JDBC (internal) connection pools properties. Ignored in case external connection pools are used.
     * Minimum number of connections in EclipseLink write connection pool.
     * Connections beyond the minimum will be disconnected when returned to the pool, so this should normally
     * be equal to the number of active threads, or server's thread pool size.
     * By default a single shared (exclusive) read/write pool is used with min/max 32 connections and 1 initial.
     */
    public static final String JDBC_WRITE_CONNECTIONS_MIN = "eclipselink.jdbc.write-connections.min";
    /**
     * EclipseLink JDBC (internal) connection pools properties. Ignored in case external connection pools are used.
     * Initial number of connections in EclipseLink write connection pool.
     * This is the number of connections connected at startup.
     * By default a single shared (exclusive) read/write pool is used with min/max 32 connections and 1 initial.
     */
    public static final String JDBC_WRITE_CONNECTIONS_INITIAL = "eclipselink.jdbc.write-connections.initial";
    /**
     * EclipseLink JDBC (internal) connection pools properties. Ignored in case external connection pools are used.
     * Maximum number of connections in EclipseLink read connection pool.
     * If the maximum size is reached, threads requiring a connection will wait until one is released back to the pool (unless shared).
     * By default a separate read connection pool is not used.
     * By default a single shared (exclusive) read/write pool is used with min/max 32 connections and 1 initial.
     */
    public static final String JDBC_READ_CONNECTIONS_MAX = "eclipselink.jdbc.read-connections.max";
    /**
     * EclipseLink JDBC (internal) connection pools properties. Ignored in case external connection pools are used.
     * Minimum number of connections in EclipseLink read connection pool.
     * Connections beyond the minimum will be disconnected when returned to the pool, so this should normally
     * be equal to the number of active threads, or server's thread pool size.
     * By default a separate read connection pool is not used.
     * By default a single shared (exclusive) read/write pool is used with min/max 32 connections and 1 initial.
     */
    public static final String JDBC_READ_CONNECTIONS_MIN = "eclipselink.jdbc.read-connections.min";
    /**
     * EclipseLink JDBC (internal) connection pools properties. Ignored in case external connection pools are used.
     * Initial number of connections in EclipseLink read connection pool.
     * This is the number of connections connected at startup.
     * By default a separate read connection pool is not used.
     * By default a single shared (exclusive) read/write pool is used with min/max 32 connections and 1 initial.
     */
    public static final String JDBC_READ_CONNECTIONS_INITIAL = "eclipselink.jdbc.read-connections.initial";
    /**
     * EclipseLink JDBC (internal) connection pools properties. Ignored in case external connection pools are used.
     * Indicates whether connections in EclipseLink read connection pool should be shared (not exclusive).
     * Connection sharing means the same JDBC connection will be used concurrently for multiple reading threads.
     * Valid values are case-insensitive "false" and "true"; "false" is default and is recommend.
     */
    public static final String JDBC_READ_CONNECTIONS_SHARED = "eclipselink.jdbc.read-connections.shared";
    /**
     * EclipseLink JDBC (internal) connection pools properties. Ignored in case external connection pools are used.
     * Maximum number of connections in EclipseLink sequence connection pool.
     * If the maximum size is reached, threads requiring a connection will wait until one is released back to the pool.
     * By default a separate sequence connection pool is not used (the sequence is allocated on the write connection).
     * If a sequence connection pool is used the default is 2.
     * JDBC_SEQUENCE_CONNECTION_POOL must also be set to true.
     * @see #JDBC_SEQUENCE_CONNECTION_POOL
     */
    public static final String JDBC_SEQUENCE_CONNECTION_POOL_MAX = "eclipselink.jdbc.sequence-connection-pool.max";
    /**
     * EclipseLink JDBC (internal) connection pools properties. Ignored in case external connection pools are used.
     * Minimum number of connections in EclipseLink sequence connection pool.
     * Connections beyond the minimum will be disconnected when returned to the pool, so this should normally
     * be equal to the maximum to avoid connecting/disconnecting.
     * By default a separate sequence connection pool is not used (the sequence is allocated on the write connection).
     * If a sequence connection pool is used the default is 2.
     * JDBC_SEQUENCE_CONNECTION_POOL must also be set to true.
     * @see #JDBC_SEQUENCE_CONNECTION_POOL
     */
    public static final String JDBC_SEQUENCE_CONNECTION_POOL_MIN = "eclipselink.jdbc.sequence-connection-pool.min";
    /**
     * EclipseLink JDBC (internal) connection pools properties. Ignored in case external connection pools are used.
     * Initial number of connections in EclipseLink sequence connection pool.
     * This is the number of connections connected at startup.
     * By default a separate sequence connection pool is not used (the sequence is allocated on the write connection).
     * If a sequence connection pool is used the default is 1.
     * JDBC_SEQUENCE_CONNECTION_POOL must also be set to true.
     * @see #JDBC_SEQUENCE_CONNECTION_POOL
     */
    public static final String JDBC_SEQUENCE_CONNECTION_POOL_INITIAL = "eclipselink.jdbc.sequence-connection-pool.initial";
    /**
     * Configure is a separate connection pool should be used for sequencing.
     * This improves sequence allocation by allocating sequencing outside the current transaction.
     * This can be used with internal or external (DataSource) connection pooling,
     * external must provide a non-jta-datasource.
     * A sequence connection pool is only used/necessary when using TABLE sequencing.
     * Valid values are case-insensitive "false" and "true"; "false" is default.
     */
    public static final String JDBC_SEQUENCE_CONNECTION_POOL = "eclipselink.jdbc.sequence-connection-pool";
    /**
     * Configure is a separate connection pool should be used for sequencing.
     * This improves sequence allocation by allocating sequencing outside the current transaction.
     * This can be used with internal or external (DataSource) connection pooling,
     * external must provide a non-jta-datasource.
     * Valid values are case-insensitive "false" and "true"; "false" is default.
     */
    public static final String JDBC_SEQUENCE_CONNECTION_POOL_DATASOURCE = "eclipselink.jdbc.sequence-connection-pool.non-jta-data-source";

    /**
     * Bind all parameters property. Valid values are case-insensitive "true" and "false"; "true" is default.
     */
    public static final String JDBC_BIND_PARAMETERS = "eclipselink.jdbc.bind-parameters";

    /**
     * Caching Prefixes.<p>
     * Property names formed out of these prefixes by appending either 
     * entity name, or class name (indicating that the property values applies only to a particular entity)
     * or DEFAULT suffix (indicating that the property value applies to all entities).
     * For most cache types, the size is only the initial size, not a fixed or maximum size.
     * For CacheType.SoftCache and CacheType.HardCache the size is the sub-cache size.
     * The default cache size is 100.
     */
    public static final String CACHE_SIZE_ = "eclipselink.cache.size.";
    
    /**
     * All valid values for CACHE_TYPE_ properties are declared in CacheType class.
     * The default cache type is SoftWeak.
     * This sets the type of cache, if you do not wish to cache entities at all,
     * then set CACHE_SHARED_.
     * @see #CACHE_SHARED_
     * @see CacheType
     */
    public static final String CACHE_TYPE_ = "eclipselink.cache.type.";
    
    /**
     * Indicates whether entity's cache should be shared (non-isolated).
     * Valid values are case-insensitive "false" and "true"; "true" is default.
     * If you do not wish to cache your entities, set this to "false".
     */
    public static final String CACHE_SHARED_ = "eclipselink.cache.shared.";
    
    /**
     * Default Suffix could be appended to some prefixes to form a property name
     * setting the default for the persistence unit.
     */
    public static final String DEFAULT = "default";
    
    /**
     * Default caching properties - apply to all entities. 
     * May be overridden by individual entity property with the same prefix.
     * For most cache types, the size is only the initial size, not a fixed or maximum size.
     * For CacheType.SoftCache and CacheType.HardCache the size is the sub-cache size.
     * The default cache size is 100.
     */
    public static final String CACHE_SIZE_DEFAULT = CACHE_SIZE_ + DEFAULT;
    
    /**
     * Default caching properties - apply to all entities. 
     * May be overridden by individual entity property with the same prefix.
     * The default cache type is SoftWeak.
     * This sets the type of cache, if you do not wish to cache entities at all,
     * then set CACHE_SHARED_DEFAULT.
     * @see #CACHE_SHARED_DEFAULT
     */
    public static final String CACHE_TYPE_DEFAULT = CACHE_TYPE_ + DEFAULT;
    
    /**
     * Default caching properties - apply to all entities. 
     * May be overridden by individual entity property with the same prefix.
     * If you do not wish to cache your entities, set this to "false".
     */
    public static final String CACHE_SHARED_DEFAULT = CACHE_SHARED_ + DEFAULT;

    /**
     * Determines when reads are performed through the write connection.
     * @see ExclusiveConnectionMode
     */
    public static final String EXCLUSIVE_CONNECTION_MODE = "eclipselink.jdbc.exclusive-connection.mode";

    /**
     * Determines when write connection is acquired lazily.
     * Valid values are case-insensitive "false" and "true"; "true" is default.
     */
    public static final String EXCLUSIVE_CONNECTION_IS_LAZY = "eclipselink.jdbc.exclusive-connection.is-lazy";

    // Customizations properties

    /** 
     * The type of logger. By default DefaultSessionLog is used.
     * Valid values are the logger class name which implements org.eclipse.persistence.logging.SessionLog
     * or one of values defined in LoggerType.
     * @see LoggerType
     */
    public static final String LOGGING_LOGGER = "eclipselink.logging.logger";
    
    /**
     * Valid values are names of levels defined in java.util.logging.Level,
     * default value is INFO.
     */
    public static final String LOGGING_LEVEL = "eclipselink.logging.level";

    /**
     * Category-specific logging level prefix
     * Property names formed out of this prefix by appending a category name
     * e.g.) eclipselink.logging.level.sql
     * Valid categories are defined in SessionLog
     */
    public static final String CATEGORY_LOGGING_LEVEL_ = "eclipselink.logging.level.";
    
    /**
     * By default ("true") the timestamp is always logged.
     * This can be turned off ("false").
     */
    public static final String  LOGGING_TIMESTAMP = "eclipselink.logging.timestamp";
    
    /**
     * By default ("true") the thread is logged at FINE or less level.
     * This can be turned off ("false") or on ("true").
     */
    public static final String  LOGGING_THREAD = "eclipselink.logging.thread";
    
    /**
     * By default ("true") the Session is always printed whenever available.
     * This can be turned off ("false").
     */
    public static final String  LOGGING_SESSION = "eclipselink.logging.session";
    
    /**
     * By default ("true") stack trace is logged for SEVERE all the time and at FINER level for WARNING or less.
     * This can be turned off ("false") or on ("true").
     */
    public static final String  LOGGING_EXCEPTIONS = "eclipselink.logging.exceptions";
    
    /**
     * Valid values are defined in TargetDatabase class - they correspond to database platforms currently supported by EclipseLink.
     * Also a custom database platform may be specified by supplying a full class name.
     * Default value is TargetDatabase.Auto which means EclipseLink will try to automatically determine
     * the correct database platform type.
     * @see TargetDatabase
     */
    public static final String TARGET_DATABASE = "eclipselink.target-database";
    
    /**
     * A persistence unit may opt to exclude an EclipseLink orm file for a
     * specific persistence unit. 
     */ 
    public static final String EXCLUDE_ECLIPSELINK_ORM_FILE = "eclipselink.exclude-eclipselink-orm";
    
    /**
     * By default a unique session name is generated by EclipseLink, but the user
     * can provide a customary session name - and make sure it's unique.
     * If a sessions-xml file is used this must be the name of the session in the sessions-xml file.
     */
    public static final String SESSION_NAME = "eclipselink.session-name";
    
    /**
     * Indicates whether weaving should be performed.
     * Weaving is requires for lazy OneToOne, ManyToOne, Basic, attribute change tracking, fetch groups,
     * and other optimizations.
     * <ul>
     * <li>"true" by default in JEE (EJB3 compliant).
     * <li>"true" by default in JSE if an agent is used, (JVM option: "-javaagent:eclipselink.jar").
     * <li>"false" by default in JSE if an agent is not used.
     * <li>"static" should be used if the static weaver was run on the persistence unit (must run static weaver ant task).
     * </ul>
     */
    public static final String WEAVING = "eclipselink.weaving";
    
    /** Indicates whether internal optimizations should be enabled through weaving - "true" by default. */
    public static final String WEAVING_INTERNAL = "eclipselink.weaving.internal";
    
    /** Indicates whether LAZY OneToOne mappings should be enabled through weaving - "true" by default. */
    public static final String WEAVING_LAZY = "eclipselink.weaving.lazy";
    
    /** Indicates whether EAGER mappings should be enabled to use indirection - "false" by default. */
    public static final String WEAVING_EAGER = "eclipselink.weaving.eager";
    
    /**
     * Indicates whether AttributeLevelChangeTracking should be enabled through weaving - "true" by default.
     * When this is enabled, only classes with all mappings allowing change tracking will have change tracking enabled.
     */
    public static final String WEAVING_CHANGE_TRACKING = "eclipselink.weaving.changetracking";
    
    /**
     * Indicates whether FetchGroup support should be enabled through weaving - "true" by default.
     * When this is enabled, lazy direct mappings will be supported as well as descriptor and query level FetchGroups.
     * FetchGroups allow partial objects to be read and written, access to unfetched attributes will cause the object to be refreshed/fully-fetched.
     * @see org.eclipse.persistence.descriptors.FetchGroupManager
     * @see org.eclipse.persistence.queries.FetchGroup
     */
    public static final String WEAVING_FETCHGROUPS = "eclipselink.weaving.fetchgroups";
    
    /**
     * Valid values are defined in TargetServer class - they correspond to server platforms currently supported by EclipseLink.
     * Also a custom server platform may be specified by supplying a full class name.
     * Specifying a name of the class implementing ExternalTransactionController sets
     * CustomServerPlatform with this controller.
     * Default is TargetServer.None - JSE case.
     * @see TargetServer
     */
    public static final String TARGET_SERVER = "eclipselink.target-server";
    
    /**
     * Allows session customization. The value is a full name for a class which implements SessionCustomizer.
     * Session customizer called after all other properties have been processed.
     * @see org.eclipse.persistence.config.SessionCustomizer
     */
    public static final String SESSION_CUSTOMIZER = "eclipselink.session.customizer";
    
// Under review    public static final String RELATIONSHIPS_FETCH_DEFAULT = "eclipselink.relationships-fetch-default";

    /**
     * Customization Prefix
     * Property names formed out of this prefix by appending either 
     * entity name, or class name (indicating that the property values applies only to a particular entity)
     * Allows descriptor customization. The value is a full name for a class which implements DescriptorCustomizer.
     * Only session customizer is called after processing these properties.
     */
    public static final String DESCRIPTOR_CUSTOMIZER_ = "eclipselink.descriptor.customizer.";
    
    /** Indicate whether to enable EclipseLink to generate DB platform specific SQL versus generic SQL. */
    public static final String NATIVE_SQL = "eclipselink.jdbc.native-sql";
    
    /**
     * When native SQL queries are used, the JDBC meta-data may return column names in lower case on some platforms.
     * If the column names are uppercase in the mappings (default) then they will not match.
     * This setting allows for forcing the column names from the meta-data to uppercase.
     */
    public static final String NATIVE_QUERY_UPPERCASE_COLUMNS = "eclipselink.jdbc.uppercase-columns";
    
    /**
     * Specify the use of batch writing to optimize transactions with multiple writes, by default batch writing is not used.
     * @see BatchWriting
     */
    public static final String BATCH_WRITING = "eclipselink.jdbc.batch-writing";
    
    /**
     * Specify the batch size used for batch writing.
     * For parameterized batch writing this is the number of statements to batch, default 100.
     * For dynamic batch writing, this is the size of the batched SQL buffer, default 32k.
     * @see #BATCH_WRITING
     */
    public static final String BATCH_WRITING_SIZE = "eclipselink.jdbc.batch-writing.size";
    
    /** Specify ExceptionHandler implementing class. */
    public static final String EXCEPTION_HANDLER_CLASS = "eclipselink.exception-handler";
    
    /** Specify number of statements held when using internal statement caching, default "50". */
    public static final String CACHE_STATEMENTS_SIZE = "eclipselink.jdbc.cache-statements.size";
    
    /** Specify whether to enable the copying of all descriptor named queries to the session to be usable from the entity manager.  Default "true". */
    public static final String INCLUDE_DESCRIPTOR_QUERIES = "eclipselink.session.include.descriptor.queries";

    /** Specify enable/disable cache statements. Default "false". */
    public static final String CACHE_STATEMENTS = "eclipselink.jdbc.cache-statements";

    /**
     * Specify a session event listener class.
     * @see org.eclipse.persistence.sessions.SessionEventListener
     */
    public static final String SESSION_EVENT_LISTENER_CLASS = "eclipselink.session-event-listener";

    /** Specify persistence info to be loaded from a sessions-xml file. */
    public static final String SESSIONS_XML = "eclipselink.sessions-xml";
    
    /** Specify a file location where the log will be output to instead of standard out. */
    public static final String LOGGING_FILE = "eclipselink.logging.file";
    
    /**
     * Specify the default for detecting changes to temporal field (Date, Calendar).  Default "false" (changes to date object itself are not detected).
     * By default it is assumed that temporal fields are replaced, and the temporal object not changed directly.
     * Enabling mutable temporal fields will cause weaving of attribute change tracking to be disabled.
     */
    public static final String TEMPORAL_MUTABLE = "eclipselink.temporal.mutable";
    
    /**
     * By default an id value of 0 is assumed to be null/unassigned.
     * This allows 0 to be a valid id value.
     * This can also be set per class using the @PrimaryKey annotation and IdValidation.
     * @see org.eclipse.persistence.annotations.PrimaryKey
     * @see org.eclipse.persistence.annotations.IdValidation
     */
    public static final String ALLOW_ZERO_ID = "eclipselink.allow-zero-id";
    
    /**
     * Defines EntityManager cache behavior after  a call to flush method
     * followed by a call to clear method.
     * This property could be specified while creating either EntityManagerFactory 
     * (either in the map passed to createEntityManagerFactory method or in persistence.xml)
     * or EntityManager (in the map passed to createEntityManager method);
     * the latter overrides the former.
     * @see FlushClearCache
     */
    public static final String FLUSH_CLEAR_CACHE = "eclipselink.flush-clear.cache";

    /**
     * Specify the classloader to use to create an EntityManagerFactory in the
     * property map passed to Persistence.createEntityManagerFactory.
     */
    public static final String CLASSLOADER = "eclipselink.classloader";
    
    /**
     * Configures if the first exception that occurs during deployment should be thrown,
     * or if all exceptions should be caught and summary thrown at end of deployment attempt.
     * "true" by default.
     */
    public static final String THROW_EXCEPTIONS = "eclipselink.orm.throw.exceptions";
    
    /** 
     * This property set on the session is used to override orm.xml schema validation from its default of false. 
     */
    public static final String ORM_SCHEMA_VALIDATION = "eclipselink.orm.validate.schema";
    
    
    /** Validate deployment, but do not connect. */
    public static final String VALIDATION_ONLY_PROPERTY = "eclipselink.validation-only";

    /**
     * Allows the database schema to be generated on deployment.
     * Valid values, CREATE_ONLY, DROP_AND_CREATE, NONE ("create-tables", "drop-and-create-tables", "none").
     * Default is NONE.
     */
    public static final String DDL_GENERATION   = "eclipselink.ddl-generation";
    
    public static final String CREATE_ONLY      = "create-tables";
    public static final String DROP_AND_CREATE  = "drop-and-create-tables";
    public static final String NONE             = "none";
    
    public static final String APP_LOCATION     = "eclipselink.application-location";
    
    public static final String CREATE_JDBC_DDL_FILE = "eclipselink.create-ddl-jdbc-file-name";
    public static final String DROP_JDBC_DDL_FILE   = "eclipselink.drop-ddl-jdbc-file-name";
    
    public static final String DEFAULT_APP_LOCATION = "." + File.separator;
    public static final String DEFAULT_CREATE_JDBC_FILE_NAME = "createDDL.jdbc";
    public static final String DEFAULT_DROP_JDBC_FILE_NAME = "dropDDL.jdbc";
    public static final String JAVASE_DB_INTERACTION = "INTERACT_WITH_DB";    
    
    /**
     * Configures if database schema should be generated on the database, to a file, or both.
     * Valid values, DDL_SQL_SCRIPT_GENERATION, DDL_DATABASE_GENERATION, DDL_BOTH_GENERATION ("sql-script", "database", "both")
     * DDL_GENERATION must also be set, for this to have an effect.
     * Default is DDL_DATABASE_GENERATION.
     */
    public static final String DDL_GENERATION_MODE = "eclipselink.ddl-generation.output-mode";
    public static final String DDL_SQL_SCRIPT_GENERATION = "sql-script";
    public static final String DDL_DATABASE_GENERATION = "database";
    public static final String DDL_BOTH_GENERATION = "both";
    public static final String DEFAULT_DDL_GENERATION_MODE = DDL_DATABASE_GENERATION;
    
    /**
     * Configures if the existence of an object should be verified on persist(),
     * otherwise it will assume to be new if not in the persistence context.
     * If checked and existing and not in the persistence context and error will be thrown.
     * "false" by default.
     */    
    public static final String VALIDATE_EXISTENCE = "eclipselink.validate-existence";
    
    /**
     * The type of performance profiler. Valid values are names of profiler defined in ProfilerType,
     * default value is NoProfiler.
     * @see ProfilerType
     */
    public static final String PROFILER = "eclipselink.profiler";

    /**
     * Set to "true" this property forces persistence context to read through JTA-managed ("write") connection
     * in case there is an active transaction. 
     * Valid values are case-insensitive "false" and "true"; "false" is default.
     * The property set in persistence.xml or passed to createEntityManagerFactory affects all EntityManagers
     * created by the factory. 
     * Note that if the property set to "true" then objects read during transaction won't be placed into the
     * shared cache unless they have been updated.
     * Alternatively, to apply the property only to some SessionManagers pass it to createEntityManager method.
     */
    public static final String JOIN_EXISTING_TRANSACTION = "eclipselink.transaction.join-existing";
    
    /**
     * Specifies whether there should be hard or soft references used within the Persistence Context.
     * Default is "HARD".  With soft references entities no longer referenced by the application
     * may be garbage collected freeing resources.  Any changes that have not been flushed in these
     * entities will be lost.
     * The property set in persistence.xml or passed to createEntityManagerFactory affects all EntityManagers
     * created by the factory. 
     * Alternatively, to apply the property only to some SessionManagers pass it to createEntityManager method.
     * @see org.eclipse.persistence.sessions.factories.ReferenceMode
     */
    public static final String PERSISTENCE_CONTEXT_REFERENCE_MODE = "eclipselink.persistence-context.reference-mode";

    /**
     * Configures the WAIT timeout used in pessimistic locking, if the database 
     * query exceeds the timeout the database will terminate the query and 
     * return an exception. Valid values are Integer or Strings that can be 
     * parsed to int values.
     */
    public static final String PESSIMISTIC_LOCK_TIMEOUT = "javax.persistence.lock.timeout";

    public static final String QUERY_TIMEOUT = "javax.persistence.query.timeout";
    
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
    public static final String PERSISTENCE_CONTEXT_CLOSE_ON_COMMIT = "eclipselink.persistence-context.close-on-commit";

    /**
     * Specifies that the EntityManager will search all managed objects and persist any related non-managed
     * new objects that are cascade persist.
     * This can be used to avoid the cost of performing this search if persist is always used for new objects.
     * The property set in persistence.xml or passed to createEntityManagerFactory affects all EntityManagers
     * created by the factory.
     * Alternatively, to apply the property only to some EntityManagers pass it to createEntityManager method.
     * Either "true" or "false.  "true" is the default.
     */
    public static final String PERSISTENCE_CONTEXT_PERSIST_ON_COMMIT = "eclipselink.persistence-context.persist-on-commit";
    
    /**
     * Specifies that the EntityManager will search all managed objects and persist any related non-managed
     * new objects that are found ignoring any absence of CascadeType.PERSIST settings.
     * Also the Entity lifecycle Persist operation will not be cascaded to related entities.
     * This setting replicates the traditional EclipseLink native functionality.
     */
    public static final String PERSISTENCE_CONTEXT_COMMIT_WITHOUT_PERSIST_RULES = "eclipselink.persistence-context.commit-without-persist-rules";

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
    public static final String PERSISTENCE_CONTEXT_FLUSH_MODE = "eclipselink.persistence-context.flush-mode";
    
    /**
     * This property is used to specify proxy type that should be passed to OracleConnection.openProxySession method.
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
     */
    public static final String ORACLE_PROXY_TYPE = "eclipselink.oracle.proxy-type";

    /**
     * Configures cache coordination for a clustered environment.
     * This needs to be set on every persistence unit/session in the cluster.
     * Depending on the cache configuration for each descriptor, this will broadcast
     * cache updates or inserts to the cluster to update or invalidate each session's cache.
     * Either "jms" or "rmi", by default the cache is not coordinated.
     * @see org.eclipse.persistence.annotations.Cache
     * @see org.eclipse.persistence.sessions.coordination.RemoteCommandManager#setTransportManager(org.eclipse.persistence.sessions.coordination.TransportManager)
     */
    public static final String COORDINATION_PROTOCOL = "eclipselink.cache.coordination.protocol";
    
    /**
     * Configures cache coordination for a clustered environment.
     * Only used for JMS coordination.
     * Sets the URL for the JMS server hosting the topic.
     * @see COORDINATION_PROTOCOL
     * @see org.eclipse.persistence.sessions.coordination.jms.JMSTopicTransportManager#setTopicHostUrl(String)
     */
    public static final String COORDINATION_JMS_HOST = "eclipselink.cache.coordination.jms.host";
    
    /**
     * Configures cache coordination for a clustered environment.
     * Only used for JMS coordination.
     * Sets the JMS topic name.
     * @see COORDINATION_PROTOCOL
     * @see org.eclipse.persistence.sessions.coordination.broadcast.BroadcastTransportManager#setTopicName(String)
     */
    public static final String COORDINATION_JMS_TOPIC = "eclipselink.cache.coordination.jms.topic";
    
    /**
     * Configures cache coordination for a clustered environment.
     * Only used for JMS coordination.
     * Sets the JMS topic connection factory name.
     * @see COORDINATION_PROTOCOL
     * @see org.eclipse.persistence.sessions.coordination.broadcast.BroadcastTransportManager#setTopicConnectionFactoryName(String)
     */
    public static final String COORDINATION_JMS_FACTORY = "eclipselink.cache.coordination.jms.factory";
     
    /**
     * Configures cache coordination for a clustered environment.
     * Only used for RMI coordination.
     * Sets the number of milliseconds to wait for announcements from other cluster members on startup.
     * @see COORDINATION_PROTOCOL
     * @see org.eclipse.persistence.sessions.coordination.DiscoveryManager#setAnnouncementDelay(int)
     */
    public static final String COORDINATION_RMI_ANNOUNCEMENT_DELAY = "eclipselink.cache.coordination.rmi.announcement-delay";

    /**
     * Configures cache coordination for a clustered environment.
     * Only used for RMI coordination.
     * Sets the multicast socket group address.
     * The multicast group is used to find other members of the cluster.
     * @see COORDINATION_PROTOCOL
     * @see org.eclipse.persistence.sessions.coordination.DiscoveryManager#setMulticastGroupAddress(String)
     */
    public static final String COORDINATION_RMI_MULTICAST_GROUP = "eclipselink.cache.coordination.rmi.multicast-group";

    /**
     * Configures cache coordination for a clustered environment.
     * Only used for RMI coordination.
     * Sets the multicast socket group port.
     * The multicast group port is used to find other members of the cluster.
     * @see COORDINATION_PROTOCOL
     * @see org.eclipse.persistence.sessions.coordination.DiscoveryManager#setMulticastPort(int)
     */
    public static final String COORDINATION_RMI_MULTICAST_GROUP_PORT = "eclipselink.cache.coordination.rmi.multicast-group.port";

    /**
     * Configures cache coordination for a clustered environment.
     * Only used for RMI coordination.
     * Sets the multicast socket packet time to live.
     * The multicast group is used to find other members of the cluster.
     * Set the number of hops the data packets of the session announcement will take before expiring.
     * The default is 2, a hub and an interface card to prevent the data packets from leaving the local network.
     *
     * Note that if sessions are hosted on different LANs that are part of WAN, the announcement sending by one session
     * may not reach other sessions.  In this case, consult your network administrator for the right time-to-live value
     * or test your network by increase the value until sessions receive announcement sent by others.
     * @see COORDINATION_PROTOCOL
     * @see org.eclipse.persistence.sessions.coordination.DiscoveryManager#setPacketTimeToLive(int)
     */
    public static final String COORDINATION_RMI_PACKET_TIME_TO_LIVE = "eclipselink.cache.coordination.rmi.packet-time-to-live";

    /**
     * Configures cache coordination for a clustered environment.
     * Only used for RMI coordination.
     * Sets the URL of the host server.
     * This is the URL that other cluster member should use to connect to this host.
     * @see COORDINATION_PROTOCOL
     * @see org.eclipse.persistence.sessions.coordination.RemoteCommandManager#setUrl(String)
     */
    public static final String COORDINATION_RMI_URL = "eclipselink.cache.coordination.rmi.url";

    /**
     * Configures cache coordination for a clustered environment.
     * Set the naming service to use, either "jndi" or "rmi".
     * @see COORDINATION_PROTOCOL
     * @see org.eclipse.persistence.sessions.coordination.TransportManager#setNamingServiceType(int)
     */
    public static final String COORDINATION_NAMING_SERVICE = "eclipselink.cache.coordination.naming-service";

    /**
     * Configures cache coordination for a clustered environment.
     * Set the JNDI naming service user name.
     * @see COORDINATION_PROTOCOL
     * @see org.eclipse.persistence.sessions.coordination.TransportManager#setUserName(String)
     */
    public static final String COORDINATION_JNDI_USER = "eclipselink.cache.coordination.jndi.user";

    /**
     * Configures cache coordination for a clustered environment.
     * Set the JNDI naming service user name.
     * @see COORDINATION_PROTOCOL
     * @see org.eclipse.persistence.sessions.coordination.TransportManager#setPassword(String)
     */
    public static final String COORDINATION_JNDI_PASSWORD = "eclipselink.cache.coordination.jndi.password";

    /**
     * Configures cache coordination for a clustered environment.
     * Set the JNDI InitialContext factory.
     * @see COORDINATION_PROTOCOL
     * @see org.eclipse.persistence.sessions.coordination.TransportManager#setInitialContextFactoryName(String)
     */
    public static final String COORDINATION_JNDI_CONTEXT = "eclipselink.cache.coordination.jndi.initial-context-factory";

    /**
     * Configures cache coordination for a clustered environment.
     * Set if the connection should be removed if a communication error occurs when coordinating with it.
     * This is normally used for RMI coordination in case a server goes down (it will reconnect when it comes back up).
     * @see COORDINATION_PROTOCOL
     * @see org.eclipse.persistence.sessions.coordination.TransportManager#setShouldRemoveConnectionOnError(boolean)
     */
    public static final String COORDINATION_REMOVE_CONNECTION = "eclipselink.cache.coordination.remove-connection-on-error";

    /**
     * Configures cache coordination for a clustered environment.
     * Set if the coordination broadcast should occur asynchronously with the committing thread.
     * This means the coordination will be complete before the thread returns from the commit of the transaction.
     * Note that JMS is always asynchronous.  By default RMI is asynchronous.
     * @see COORDINATION_PROTOCOL
     * @see org.eclipse.persistence.sessions.coordination.RemoteCommandManager#setShouldPropagateAsynchronously(boolean)
     */
    public static final String COORDINATION_ASYNCH = "eclipselink.cache.coordination.propagate-asynchronously";

    /**
     * Configures cache coordination for a clustered environment.
     * Set if the channel for this cluster.
     * All server's in the same channel will be coordinated.
     * @see COORDINATION_PROTOCOL
     * @see org.eclipse.persistence.sessions.coordination.RemoteCommandManager#setChannel(String)
     */
    public static final String COORDINATION_CHANNEL = "eclipselink.cache.coordination.channel";
      
    /** INTERNAL: The following properties will not be displayed through logging but instead have an alternate value shown in the log. */
    public static final Map<String, String> PROPERTY_LOG_OVERRIDES = new HashMap<String, String>(1);
    
    /**
     * Used to set delimiter used when using delimited identifiers
     * Due to the way this property is used, it is universally set rather than configurable on per-persistence-unit basis
     * (i.e. it is a global setting)
     * This property should be set as a string that contains the following:
     * 1. If the start and end delimiter are the same, the string consists of that identifier
     * 2. If the start and end delimiter are different, the string has two characters, the first being the start identifier the second being the end identifier
     */
    public static final String DATABASE_DELIMITERS = "eclipselink.database.delimiters";
    
    static {
        PROPERTY_LOG_OVERRIDES.put(JDBC_PASSWORD, "xxxxxx");
    }
    
    /**
     * INTERNAL: Return the overridden log string.
     */
    public static final String getOverriddenLogStringForProperty(String propertyName){
        return PROPERTY_LOG_OVERRIDES.get(propertyName);
    }    

}
