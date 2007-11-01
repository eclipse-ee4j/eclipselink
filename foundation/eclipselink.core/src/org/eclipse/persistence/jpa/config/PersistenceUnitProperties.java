/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.jpa.config;

import java.io.File;
import java.util.Map;
import java.util.HashMap;

/**
 * The class defines TopLink properties' names.
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
    public static final String JDBC_DRIVER = "eclipselink.jdbc.driver";
    /** JDBC DriverManager driver URL. */
    public static final String JDBC_URL = "eclipselink.jdbc.url";
    /** DataSource or JDBC DriverManager user name. */
    public static final String JDBC_USER = "eclipselink.jdbc.user";
    /** DataSource or JDBC DriverManager password. */
    public static final String JDBC_PASSWORD = "eclipselink.jdbc.password";

    /**
     * TopLink JDBC (internal) connection pools properties. Ignored in case external connection pools are used.
     * Maximum number of connections in TopLink write connection pool by default is 10.
     */
    public static final String JDBC_WRITE_CONNECTIONS_MAX = "eclipselink.jdbc.write-connections.max";
    /**
     * TopLink JDBC (internal) connection pools properties. Ignored in case external connection pools are used.
     * Minimum number of connections in TopLink write connection pool by default is 5.
     */
    public static final String JDBC_WRITE_CONNECTIONS_MIN = "eclipselink.jdbc.write-connections.min";
    /**
     * TopLink JDBC (internal) connection pools properties. Ignored in case external connection pools are used.
     * Maximum number of connections in TopLink read connection pool by default is 2.
     */
    public static final String JDBC_READ_CONNECTIONS_MAX = "eclipselink.jdbc.read-connections.max";
    /**
     * TopLink JDBC (internal) connection pools properties. Ignored in case external connection pools are used.
     * Minimum number of connections in TopLink read connection pool by default is 2.
     */
    public static final String JDBC_READ_CONNECTIONS_MIN = "eclipselink.jdbc.read-connections.min";
    /**
     * TopLink JDBC (internal) connection pools properties. Ignored in case external connection pools are used.
     * Indicates wheather connections in TopLink read connection pool should be shared.
     * Valid values are case-insensitive "false" and "true"; "false" is default.
     */
    public static final String JDBC_READ_CONNECTIONS_SHARED = "eclipselink.jdbc.read-connections.shared";

    /**
     * Bind all parameters property. Valid values are case-insensitive "true" and "false"; "true" is default.
     */
    public static final String JDBC_BIND_PARAMETERS = "eclipselink.jdbc.bind-parameters";

    /**
     * Caching Prefixes.<p>
     * Property names formed out of these prefixes by appending either 
     * entity name, or class name (indicating that the property values applies only to a particular entity)
     * or DEFAULT suffix (indicating that the property value applies to all entities).
     * CACHE_SIZE_ properties default value is 1000.
     */
    public static final String CACHE_SIZE_ = "eclipselink.cache.size.";
    /**
     * All valid values for CACHE_TYPE_ properties are declared in CacheType class.
     * @see CacheType
     */
    public static final String CACHE_TYPE_ = "eclipselink.cache.type.";
    /**
     * Indicates whether entity's cache should be shared (non-isolated).
     * Valid values are case-insensitive "false" and "true"; "true" is default.
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
     */
    public static final String CACHE_SIZE_DEFAULT = CACHE_SIZE_ + DEFAULT; 
    /**
     * Default caching properties - apply to all entities. 
     * May be overridden by individual entity property with the same prefix.
     */
    public static final String CACHE_TYPE_DEFAULT = CACHE_TYPE_ + DEFAULT; 
    /**
     * Default caching properties - apply to all entities. 
     * May be overridden by individual entity property with the same prefix.
     */
    public static final String CACHE_SHARED_DEFAULT = CACHE_SHARED_ + DEFAULT;

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
     * Valid values are defined in TargetDatabase class - they correspond to database platforms currently supported by TopLink.
     * Also a custom database platform may be specified by supplying a full class name.
     * Default value is TargetDatabase.Auto which means TopLink will try to automatically determine
     * the correct database platform type.
     * @see TargetDatabase
     */
    public static final String TARGET_DATABASE = "eclipselink.target-database";
    
    /**
     * By default a unique session name is generated by TopLink, but the user
     * can provide a customary session name - and make sure it's unique.
     * If a sessions-xml file is used this must be the name of the session in the sessions-xml file.
     */
    public static final String SESSION_NAME = "eclipselink.session-name";
    
    /** Indicates whether weaving should be performed - "true" by default. */
    public static final String WEAVING = "eclipselink.weaving";
    
    /** Indicates whether internal optimizations should be enabled through weaving - "true" by default. */
    public static final String WEAVING_INTERNAL = "eclipselink.weaving.internal";
    
    /** Indicates whether LAZY OneToOne mappings should be enabled through weaving - "true" by default. */
    public static final String WEAVING_LAZY = "eclipselink.weaving.lazy";
    
    /**
     * Indicates whether AttributeLevelChangeTracking should be enabled through weaving - "true" by default.
     * When this is enabled, only classes with all mappings allowing change tracking will have change tracking enabled.
     */
    public static final String WEAVING_CHANGE_TRACKING = "eclipselink.weaving.changetracking";
    
    /**
     * Indicates whether FetchGroup support should be enabled through weaving - "true" by default.
     * When this is enabled, lazy direct mappings will be supported as well as descriptor and query level FetchGroups.
     * FetchGroups allow partial objects to be read and written, access to unfetch attributes will cause the object to be refreshed/fully-fetched.
     * @see org.eclipse.persistence.descriptors.FetchGroupManager
     * @see org.eclipse.persistence.queries.FetchGroup
     */
    public static final String WEAVING_FETCHGROUPS = "eclipselink.weaving.fetchgroups";
    
    /**
     * Valid values are defined in TargetServer class - they correspond to server platforms currently supported by TopLink.
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
     * @see org.eclipse.persistence.internal.sessions.factories.SessionCustomizer
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
    
    /** Indicate whether enable toplink to generate DB platform specific SQL versus generic SQL. */
    public static final String NATIVE_SQL = "eclipselink.jdbc.native-sql";
    
    /**
     * Specify the use of batch writing to optimize transactions with multiple writes, by default batch writing is not used.
     * @see BatchWriting
     */
    public static final String BATCH_WRITING = "eclipselink.jdbc.batch-writing";
    
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
    
    /** Specify a file location where the log will be output to intead of standard out. */
    public static final String LOGGING_FILE = "eclipselink.logging.file";
    
    /**
     * Specify the default for detecting changes to temporal field (Date, Calendar).  Default "false" (changes to date object itself are not detected).
     * By default it is assumed that temporal fields are replaced, and the temporal object not changed directly.
     * Enabling mutable temporal fields will cause weaving of attribute change tracking to be disabled.
     */
    public static final String TEMPORAL_MUTABLE = "eclipselink.temporal.mutable";
    
    /**
     * Defines EntityManager cache behaviour after  a call to flush method
     * followed by a call to clear method.
     * This property could be specified while creating either EntityManagerFactory 
     * (either in the map passed to createEntityManagerFactory method or in persistence.xml)
     * or EntityManager (in the map passed to createEntityManager method);
     * the latter overrides the former.
     * @see FlushClearCache
     */
    public static final String FLUSH_CLEAR_CACHE = "eclipselink.flush-clear.cache";
    
    /** INTERNAL: The following properties will not be displayed through logging but instead have an alternate value shown in the log. */
    public static final Map<String, String> PROPERTY_LOG_OVERRIDES = new HashMap<String, String>(1);
    //for gf3334, this property force persistence context to read through JTA-managed ("write") connection in case there is an active transaction.    
    public static final String JOIN_EXISTING_TRANSACTION = "eclipselink.transaction.join-existing";
    static {
        PROPERTY_LOG_OVERRIDES.put(JDBC_PASSWORD, "xxxxxx");
    }
    
    public static final String TOPLINK_ORM_THROW_EXCEPTIONS = "eclipselink.orm.throw.exceptions";
    public static final String TOPLINK_VALIDATION_ONLY_PROPERTY = "eclipselink.validation-only";

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
    
    public static final String DDL_GENERATION_MODE = "eclipselink.ddl-generation.output-mode";
    public static final String DDL_SQL_SCRIPT_GENERATION = "sql-script";
    public static final String DDL_DATABASE_GENERATION = "database";
    public static final String DDL_BOTH_GENERATION = "both";
    // This is the default for now to ensure we still play nicely with Glassfish.
    public static final String DEFAULT_DDL_GENERATION_MODE = DDL_SQL_SCRIPT_GENERATION;
    
    
    /** INTERNAL: Return the overridden log string. */
    public static final String getOverriddenLogStringForProperty(String propertyName){
        return PROPERTY_LOG_OVERRIDES.get(propertyName);
    }
    
    /**
     * The type of performance profiler. Valid values are names of profiler defined in ProfilerType,
     * default value is NoProfiler.
     */
    public static final String PROFILER = "eclipselink.profiler";
    

}
