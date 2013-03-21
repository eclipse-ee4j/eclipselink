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
 *     Oracle - initial API and implementation from Oracle TopLink
 *     dclarke - Bug 294985: update of comments and addition of connection logging property
 *     cdelahun - Bug 214534: added COORDINATION_JMS_REUSE_PUBLISHER property to enable JMS rcm legacy behavior
 *     04/01/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 2)
 *     06/30/2011-2.3.1 Guy Pelletier 
 *       - 341940: Add disable/enable allowing native queries 
 *     09/20/2011-2.3.1 Guy Pelletier 
 *       - 357476: Change caching default to ISOLATED for multitenant's using a shared EMF.
 *     12/24/2012-2.5 Guy Pelletier 
 *       - 389090: JPA 2.1 DDL Generation Support
 *     01/08/2013-2.5 Guy Pelletier 
 *       - 389090: JPA 2.1 DDL Generation Support
 *     01/11/2013-2.5 Guy Pelletier 
 *       - 389090: JPA 2.1 DDL Generation Support
 *     02/04/2013-2.5 Guy Pelletier 
 *       - 389090: JPA 2.1 DDL Generation Support
 *     02/19/2013-2.5 Guy Pelletier 
 *       - 389090: JPA 2.1 DDL Generation Support
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
import org.eclipse.persistence.exceptions.ExceptionHandler;
import org.eclipse.persistence.internal.databaseaccess.BatchWritingMechanism;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.eclipse.persistence.platform.database.events.DatabaseEventListener;
import org.eclipse.persistence.platform.server.ServerPlatform;
import org.eclipse.persistence.queries.JPAQueryBuilder;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.SessionEventListener;
import org.eclipse.persistence.sessions.SessionProfiler;
import org.eclipse.persistence.sessions.coordination.RemoteCommandManager;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.eclipse.persistence.tools.profiler.PerformanceMonitor;
import org.eclipse.persistence.sessions.remote.RemoteSession;
import org.eclipse.persistence.tools.profiler.PerformanceProfiler;
import org.eclipse.persistence.tools.profiler.QueryMonitor;
import org.eclipse.persistence.tools.tuning.SafeModeTuner;
import org.eclipse.persistence.tools.tuning.SessionTuner;

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
 * EntityManagerFactory emf = Persistence.createEntityManagerFactory("pu-name", props);
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
     * The <code>"eclipselink.jdbc.sql-cast"</code> property specifies if
     * platform specific CAST SQL operations should be used. Casting is normally
     * not required, and can cause issues when used. Allowed Values (String):
     * <ul>
     * <li>"true" - enable platform specific cast
     * <li>"false" (DEFAULT) - disable platform specific cast
     * </ul>
     * 
     * @see DatabasePlatform#setIsCastRequired(boolean)
     */
    public static final String SQL_CAST = "eclipselink.jdbc.sql-cast";

    /**
     * The <code>"eclipselink.jpql.parser"</code> property allows the
     * JPQL parser to be configured.
     * Two JPQL parsers are provided:
     * <ul>
     * <li>"org.eclipse.persistence.internal.jpa.jpql.HermesParser" (DEFAULT) - new parser as of EclipseLink 2.4, provides extended JPQL support.
     * <li>"org.eclipse.persistence.queries.ANTLRQueryBuilder" - old parser used previous to EclipseLink 2.4, can be used for backward compatibility.
     * </ul>
     * 
     * @see ParserType
     * @see JPAQueryBuilder
     */
    public static final String JPQL_PARSER = "eclipselink.jpql.parser";
    
    /**
     * The <code>"eclipselink.jpql.validation"</code> property allows the
     * JPQL parser validation level to be configured.
     * This setting is only supported in the Hermes parser.
     * Valid values are:
     * <ul>
     * <li>"EclipseLink" (DEFAULT) - allows EclipseLink JPQL extensions.
     * <li>"JPA 1.0" - only allows valid JPA 1.0 JPQL.
     * <li>"JPA 2.0" - only allows valid JPA 2.0 JPQL.
     * <li>"JPA 2.1" - only allows valid JPA 2.1 JPQL.
     * <li>"None" - no JPQL validation is done.
     * </ul>
     * 
     * @see ParserValidationType
     */
    public static final String JPQL_VALIDATION = "eclipselink.jpql.validation";
    
    /**
     * The <code>"wait"</code> property.
     * This can be append to any connection pool property,
     * i.e. <code>"eclipselink.jdbc.connection_pool.default.wait"</code>
     * which specifies the timeout time in milliseconds (ms) that will be waited
     * for an available connection before an exception is thrown. Ignored in
     * case external connection pools are used.
     * <p>
     * Default: 180000 ms (3 minutes).
     * <p>
     * The value specified should be a string containing a positive integer
     * value. A value of 0 means wait forever.
     * @see #CONNECTION_POOL
     */
    public static final String CONNECTION_POOL_WAIT = "wait";
    
    /**
     * The <code>"max"</code> property.
     * This can be append to any connection pool property,
     * i.e. <code>"eclipselink.jdbc.connection_pool.default.max"</code>
     * Specifies the maximum number of read connection in the internal connection pool. If
     * the maximum size is reached, threads requiring a connection will wait
     * until one is released back to the pool. By default a single shared
     * (exclusive) read/write pool is used with min/max 32 connections and 1
     * initial. Ignored in case external connection pools are used.
     * <p>
     * The value specified should be a string containing a positive integer
     * value.
     * @see #CONNECTION_POOL
     */
    public static final String CONNECTION_POOL_MAX = "max";

    /**
     * The <code>"min"</code> property.
     * This can be append to any connection pool property,
     * i.e. <code>"eclipselink.jdbc.connection_pool.default.min"</code>
     * Specifies the minimum number of connections in EclipseLink connection pool.
     * <p>
     * Ignored in case external connection pools are used. Connections beyond
     * the minimum will be disconnected when returned to the pool, so this
     * should normally be equal to the number of active threads, or server's
     * thread pool size. By default a single shared (exclusive) read/write pool
     * is used with min/max 32 connections and 1 initial.
     * <p>
     * The value specified should be a string containing a positive integer
     * value.
     * @see #CONNECTION_POOL
     */
    public static final String CONNECTION_POOL_MIN = "min";

    /**
     * The <code>"initial"</code> property.
     * This can be append to any connection pool property,
     * i.e. <code>"eclipselink.jdbc.connection_pool.default.initial"</code>
     * EclipseLink JDBC (internal) connection pools properties. Initial number
     * of connections in EclipseLink connection pool. This is the number of
     * connections connected at startup. By default a single shared (exclusive)
     * read/write pool is used with min/max 32 connections and 1 initial. Ignored
     * in case external connection pools are used.
     * <p>
     * The value specified should be a string containing a positive integer
     * value.
     * @see #CONNECTION_POOL
     */
    public static final String CONNECTION_POOL_INITIAL = "initial";
    
    /**
     * The <code>"shared"</code> property.
     * This can be append to the read connection pool,
     * i.e. <code>"eclipselink.jdbc.connection_pool.read.shared"</code>
     * Configures whether connections in EclipseLink read connection pool should
     * be shared (not exclusive). Connection sharing means the same JDBC
     * connection will be used concurrently for multiple reading threads.
     * <p>
     * This property is ignored in case external connection pools are used.
     * <p>
     * Values (case insensitive):
     * <ul>
     * <li>"false" (DEFAULT): indicates read connections will not be shared
     * <li>"true": indicates read connections can be shared
     * </ul>
     * @see #CONNECTION_POOL_READ
     */
    public static final String CONNECTION_POOL_SHARED = "shared";
    
    /**
     * The <code>"url"</code> property.
     * This can be append to a connection pool property,
     * i.e. <code>"eclipselink.jdbc.connection_pool.node1.url"</code>
     * Configures the JDBC url to use for the connection pool.
     * Only required if different than the default.
     * @see #CONNECTION_POOL
     */
    public static final String CONNECTION_POOL_URL = "url";
    
    /**
     * The <code>"jtaDataSource"</code> property.
     * This can be append to a connection pool property,
     * i.e. <code>"eclipselink.jdbc.connection_pool.node1.jtaDataSource"</code>
     * Configures the JTA DataSource name to use for the connection pool.
     * Only required if different than the default.
     * @see #CONNECTION_POOL
     */
    public static final String CONNECTION_POOL_JTA_DATA_SOURCE = "jtaDataSource";
    
    /**
     * The <code>"nonJtaDataSource"</code> property.
     * This can be append to a connection pool property,
     * i.e. <code>"eclipselink.jdbc.connection_pool.node1.nonJtaDataSource"</code>
     * Configures the non JTA DataSource name to use for the connection pool.
     * Only required if different than the default.
     * @see #CONNECTION_POOL
     */
    public static final String CONNECTION_POOL_NON_JTA_DATA_SOURCE = "nonJtaDataSource";
    
    /**
     * The <code>"user"</code> property.
     * This can be append to a connection pool property,
     * i.e. <code>"eclipselink.jdbc.connection_pool.node1.user"</code>
     * Configures the user name to use for the connection pool.
     * Only required if different than the default.
     * @see #CONNECTION_POOL
     */
    public static final String CONNECTION_POOL_USER = "user";
    
    /**
     * The <code>"password"</code> property.
     * This can be append to a connection pool property,
     * i.e. <code>"eclipselink.jdbc.connection_pool.node1.password"</code>
     * Configures the password to use for the connection pool.
     * Only required if different than the default.
     * @see #CONNECTION_POOL
     */
    public static final String CONNECTION_POOL_PASSWORD = "password";

    /**
     * The <code>"failover"</code> property.
     * This can be append to a connection pool property,
     * i.e. <code>"eclipselink.jdbc.connection_pool.node1.failover"</code>
     * Configures the connection pool(s) to fail-over to if this connection pool fails.
     * A comma separate list is allowed if multiple failover pools are required.
     * @see #CONNECTION_POOL
     */
    public static final String CONNECTION_POOL_FAILOVER = "failover";
    
    /**
     * Allow configuring a <code>"eclipselink.connection-pool."</code> properties.
     * The name of the connection pool must be appended to configure the pool,
     * if no name is appended the default (write) pool is configured.
     * The name of the property to configure must also be appended.
     * <p>
     * A user defined connection pool can be configured or one of the following system pools:
     * <ul>
     * <li> "read" - pool used for non-transactional read queries, (defaults to default pool if not specified).
     * <li> "default", "write", "" - default pool used for writing and reads if no read pool configured.
     * <li> "sequence" - pool used for sequencing, (default pool/write connection used if not specified).
     * </ul>
     * A user defined pool is only used if specified in the EntityManager properties or ClientSession ConnectionPolicy,
     * or if partitioning is used.     * 
     * <p>
     * The following connection pool properties can be configured:
     * <ul>
     * <li> "initial" - number of initial connections.
     * <li> "min" - minimum number of connections.
     * <li> "max" - maximum number of connections.
     * <li> "wait" - amount of time to wait for a connection from the pool.
     * <li> "url" - JDBC URL for the connection.
     * <li> "shared" - only for the read connection pool, shares read connections across threads.
     * <li> "jtaDataSource" - JTA DataSource name to use for the connection, if different than the default.
     * <li> "nonJtaDataSource" - non JTA DataSource name to use for the connection, if different than the default.
     * <li> "user" - user to use for the connection, if different than the default.
     * <li> "password" - password to use for the connection, if different than the default.
     * </ul>
     * <p>
     * Example:
     * <pre>
     * &lt;property name="eclipselink.connection-pool.node2.min" value="16"/&gt;
     * &lt;property name="eclipselink.connection-pool.node2.max" value="16"/&gt;
     * &lt;property name="eclipselink.connection-pool.node2.url" value="jdbc:oracle:thin:@node2:1521:orcl"/&gt;
     * </pre>
     * @see CONNECTION_POOL_READ
     * @see CONNECTION_POOL_SEQUENCE
     * @see #CONNECTION_POOL_INITIAL
     * @see #CONNECTION_POOL_MIN
     * @see #CONNECTION_POOL_MAX
     * @see #CONNECTION_POOL_WAIT
     * @see #CONNECTION_POOL_USER
     * @see #CONNECTION_POOL_PASSWORD
     * @see #CONNECTION_POOL_URL
     * @see #CONNECTION_POOL_JTA_DATA_SOURCE
     * @see #CONNECTION_POOL_NON_JTA_DATA_SOURCE
     * @see #CONNECTION_POOL_SHARED
     * @see org.eclipse.persistence.sessions.server.ConnectionPool
     */
    public static final String CONNECTION_POOL = "eclipselink.connection-pool.";

    /**
     * Allow configuring the <code>"eclipselink.connection-pool.read."</code> properties.
     * The read connection pool is used for non-transaction read queries.
     * By default a separate read connection pool is not used,
     * and the default pool is used for read queries.
     * <p>
     * One of the following connection pool properties must be appended.
     * @see #CONNECTION_POOL_INITIAL
     * @see #CONNECTION_POOL_MIN
     * @see #CONNECTION_POOL_MAX
     * @see #CONNECTION_POOL_WAIT
     * @see #CONNECTION_POOL_USER
     * @see #CONNECTION_POOL_PASSWORD
     * @see #CONNECTION_POOL_URL
     * @see #CONNECTION_POOL_JTA_DATA_SOURCE
     * @see #CONNECTION_POOL_NON_JTA_DATA_SOURCE
     * @see org.eclipse.persistence.sessions.server.ReadConnectionPool
     */
    public static final String CONNECTION_POOL_READ = "eclipselink.connection-pool.read.";

    /**
     * Allow configuring the <code>"eclipselink.connection-pool.sequence."</code> properties.
     * The sequence connection pool is used to allocate generated Ids.
     * This is only required for TABLE sequencing.
     * By default a separate sequence connection pool is not used,
     * and the default pool is used for sequencing.
     * <p>
     * One of the following connection pool properties must be appended.
     * @see #CONNECTION_POOL_INITIAL
     * @see #CONNECTION_POOL_MIN
     * @see #CONNECTION_POOL_MAX
     * @see #CONNECTION_POOL_WAIT
     * @see #CONNECTION_POOL_USER
     * @see #CONNECTION_POOL_PASSWORD
     * @see #CONNECTION_POOL_URL
     * @see #CONNECTION_POOL_JTA_DATA_SOURCE
     * @see #CONNECTION_POOL_NON_JTA_DATA_SOURCE
     * @see org.eclipse.persistence.sessions.server.ReadConnectionPool
     */
    public static final String CONNECTION_POOL_SEQUENCE = "eclipselink.connection-pool.sequence.";
    
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
     * @deprecated as of EclipseLink 2.2 replaced by CONNECTION_POOL_WAIT
     * @see #CONNECTION_POOL_WAIT
     */
    @Deprecated
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
     * @deprecated as of EclipseLink 2.2 replaced by CONNECTION_POOL_MAX
     * @see #CONNECTION_POOL_MAX
     */
    @Deprecated
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
     * @deprecated as of EclipseLink 2.2 replaced by CONNECTION_POOL_MIN
     * @see #CONNECTION_POOL_MIN
     */
    @Deprecated
    public static final String JDBC_CONNECTIONS_MIN = "eclipselink.jdbc.connections.min";

    /**
     * The <code>"eclipselink.jdbc.connections.initial"</code> property
     * EclipseLink JDBC (internal) connection pools properties. Initial number
     * of connections in EclipseLink connection pool. This is the number of
     * connections connected at startup. By default a single shared (exclusive)
     * read/write pool is used with min/max 32 connections and 1 initial. Ignored
     * in case external connection pools are used.
     * <p>
     * The value specified should be a string containing a positive integer
     * value.
     * @deprecated as of EclipseLink 2.2 replaced by CONNECTION_POOL_INITIAL
     * @see #CONNECTION_POOL_INITIAL
     */
    @Deprecated
    public static final String JDBC_CONNECTIONS_INITIAL = "eclipselink.jdbc.connections.initial";

    /**
     * The <code>"eclipselink.jdbc.write-connections.max"</code> property
     * specifies the maximum number of connections supported in the internal
     * write connection pool. Maximum number of connections in EclipseLink write
     * connection pool. If the maximum size is reached, threads requiring a
     * connection will wait until one is released back to the pool. By default a
     * single shared (exclusive) read/write pool is used with min/max 32
     * connections and 1 initial. Ignored in case external connection pools are
     * used.
     * <p>
     * The value specified should be a string containing a positive integer
     * value.
     * @deprecated as of EclipseLink 2.2 replaced by CONNECTION_POOL_MAX
     * @see #CONNECTION_POOL_MAX
     */
    @Deprecated
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
     * @deprecated as of EclipseLink 2.2 replaced by CONNECTION_POOL_MIN
     * @see #CONNECTION_POOL_MIN
     */
    @Deprecated
    public static final String JDBC_WRITE_CONNECTIONS_MIN = "eclipselink.jdbc.write-connections.min";

    /**
     * The <code>"eclipselink.jdbc.write-connections.initial"</code> property
     * configures the number of connections connected at created at startup in
     * the write connection pool. By default a single shared (exclusive)
     * read/write pool is used with min/max 32 connections and 1 initial.
     * <p>
     * This property is ignored in case external connection pools are used.
     * <p>
     * The value must be a string containing a zero or greater integer value.
     * @deprecated as of EclipseLink 2.2 replaced by CONNECTION_POOL_INITIAL
     * @see #CONNECTION_POOL_INITIAL
     */
    @Deprecated
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
     * <p>
     * The value specified should be a string containing a zero or greater
     * integer value.
     * 
     * @see #JDBC_CONNECTIONS_WAIT to configure the timeout waiting on a
     *      connection.
     * @deprecated as of EclipseLink 2.2 replaced by CONNECTION_POOL_MAX
     * @see #CONNECTION_POOL_MAX
     */
    @Deprecated
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
     * <p>
     * The value specified should be a string containing a zero or greater
     * integer value.
     * @deprecated as of EclipseLink 2.2 replaced by CONNECTION_POOL_MIN
     * @see #CONNECTION_POOL_MIN
     */
    @Deprecated
    public static final String JDBC_READ_CONNECTIONS_MIN = "eclipselink.jdbc.read-connections.min";

    /**
     * The <code>"eclipselink.jdbc.read-connections.initial"</code> property
     * configures the number of connections connected at created at startup in
     * the read connection pool. By default a single shared (exclusive)
     * read/write pool is used with min/max 32 connections and 1 initial.
     * <p>
     * This property is ignored in case external connection pools are used.
     * <p>
     * The value must be a string containing a zero or greater integer value.
     * @deprecated as of EclipseLink 2.2 replaced by CONNECTION_POOL_INITIAL
     * @see #CONNECTION_POOL_INITIAL
     */
    @Deprecated
    public static final String JDBC_READ_CONNECTIONS_INITIAL = "eclipselink.jdbc.read-connections.initial";

    /**
     * The <code>"eclipselink.jdbc.read-connections.shared"</code> property
     * configures whether connections in EclipseLink read connection pool should
     * be shared (not exclusive). Connection sharing means the same JDBC
     * connection will be used concurrently for multiple reading threads.
     * <p>
     * This property is ignored in case external connection pools are used.
     * <p>
     * Values (case insensitive):
     * <ul>
     * <li>"false" (DEFAULT): indicates read connections will not be shared
     * <li>"true": indicates read connections can be shared
     * </ul>
     * @deprecated as of EclipseLink 2.2 replaced by CONNECTION_POOL_SHARED
     * @see #CONNECTION_POOL_SHARED
     */
    @Deprecated
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
     * <p>
     * The value must be a string containing a zero or greater integer value.
     * The default value is two (2).
     * 
     * @see #JDBC_SEQUENCE_CONNECTION_POOL Required to configure the use of a
     *      sequence pool/data-source
     * @see #JDBC_CONNECTIONS_WAIT to configure the timeout
     * @deprecated as of EclipseLink 2.2 replaced by CONNECTION_POOL_MAX
     * @see #CONNECTION_POOL_MAX
     */
    @Deprecated
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
     * <p>
     * The value must be a string containing a zero or greater integer value.
     * The default value is two (2).
     * 
     * @see #JDBC_SEQUENCE_CONNECTION_POOL Required to configure the use of a
     *      sequence pool/data-source
     * @deprecated as of EclipseLink 2.2 replaced by CONNECTION_POOL_MIN
     * @see #CONNECTION_POOL_MIN
     */
    @Deprecated
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
     * <p>
     * The value must be a string containing a zero or greater integer value.
     * The default value is two (2).
     * 
     * @see #JDBC_SEQUENCE_CONNECTION_POOL Required to configure the use of a
     *      sequence pool/data-source
     * @deprecated as of EclipseLink 2.2 replaced by CONNECTION_POOL_INITIAL
     * @see #CONNECTION_POOL_INITIAL
     */
    @Deprecated
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
     * @deprecated as of EclipseLink 2.2 replaced by CONNECTION_POOL_SEQUENCE
     * @see #CONNECTION_POOL_SEQUENCE
     */
    @Deprecated
    public static final String JDBC_SEQUENCE_CONNECTION_POOL = "eclipselink.jdbc.sequence-connection-pool";

    /**
     * The <code>"eclipselink.jdbc.sequence-connection-pool.non-jta-data-source</code> property configures the name of the non-JTA data source that
     * will be used for sequencing calls.
     * 
     * @see #JDBC_SEQUENCE_CONNECTION_POOL
     * @deprecated as of EclipseLink 2.2 replaced by CONNECTION_POOL_NON_JTA_DATA_SOURCE
     * @see #CONNECTION_POOL_NON_JTA_DATA_SOURCE
     */
    @Deprecated
    public static final String JDBC_SEQUENCE_CONNECTION_POOL_DATASOURCE = "eclipselink.jdbc.sequence-connection-pool.non-jta-data-source";

    /**
     * "eclipselink.partitioning"
     * <p>Used to set the default PartitioningPolicy for a persistence unit.
     * A PartitioningPolicy is used to partition the data for a class across multiple difference databases
     * or across a database cluster such as Oracle RAC.
     * Partitioning can provide improved scalability by allowing multiple database machines to service requests.
     * <p>
     * If multiple partitions are used to process a single transaction, JTA should be used for proper XA transaction support.
     * <p>
     * The value must be set to the name of an existing defined PartitioningPolicy.
     * 
     * @see org.eclipse.persistence.annotations.Partitioning
     * @see org.eclipse.persistence.descriptors.partitioning.PartitioningPolicy
     */
    public static final String PARTITIONING = "eclipselink.partitioning";
    
    /**
     * "eclipselink.partitioning.callback"
     * <p>Used to integrate with an external DataSource's data affinity support, such as UCP.
     * <p>
     * The value must be set to the full class name of the implementor of the DataPartitioningCallback interface.
     * <p>i.e. "org.eclipse.persistence.platform.database.oracle.ucp.UCPDataPartitioningCallback".
     * 
     * @see org.eclipse.persistence.platform.database.partitioning.DataPartitioningCallback
     * @see org.eclipse.persistence.annotations.Partitioning
     * @see org.eclipse.persistence.descriptors.partitioning.PartitioningPolicy
     */
    public static final String PARTITIONING_CALLBACK = "eclipselink.partitioning.callback";
    
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
     * <li>true - enable internal statement caching.
     * <li>false - disable internal statement caching. (DEFAULT)
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
     * http://download.oracle.com/javaee/6/docs/api/javax/validation/ValidatorFactory.html>javax.validation.ValidatorFactory</a> used by
     * EclipseLink to perform Automatic Validation upon Lifecycle Events. If the
     * property is not specified, and if Bean Validation API is visible to
     * Eclipselink, it will try to instantiate an instance of
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

    /**
     * Property prefix <code>"eclipselink.cache.query-results</code> used to
     * configure the default option for query results caching.
     * <p>
     * The query results cache is separate from the object cache.
     * It caches the results of named query execution.
     * The query results cache is not enabled by default, and
     * can be enabled per query.
     * This option allows it to be enabled for all named queries.
     * Valid values are "true" or "false" (default).
     */
    public static final String QUERY_CACHE = "eclipselink.cache.query-results";
    
    /**
     * Allows integration with a database event notification service.
     * This allows the EclipseLink cache to be invalidated by database change events.
     * This is used to support Oracle QCN/DCN (Database Change event Notification),
     * but could also be used by triggers or other services, or other types of events.
     * Must be set to the name of a class that implements DatabaseEventListener,
     * such as the OracleChangeNotificationListener (org.eclipse.persistence.platform.database.oracle.dcn.OracleChangeNotificationListener).
     * The values of "DCN" and "QCN" may also be used for Oracle.
     * 
     * @see DatabaseEventListener
     * @see org.eclipse.persistence.platform.database.oracle.dcn.OracleChangeNotificationListener
     */
    public static final String DATABASE_EVENT_LISTENER = "eclipselink.cache.database-event-listener";
    
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
     * @see SessionLog#METADATA
     * @see SessionLog#METAMODEL
     * @see SessionLog#WEAVER
     * @see SessionLog#PROPERTIES
     * @see SessionLog#SERVER
     */
    public static final String CATEGORY_LOGGING_LEVEL_ = LOGGING_LEVEL + ".";
    
    /**
     * By default sql bind paramters are displayed in exceptions and logs
     * when the log level is FINE or greater. To override this behavior you
     * may set this property to specify that the data should or should not be
     * visible. Note: this property applies only to bind parameters. When not
     * using binding, the parameters are always displayed.
     * 
     * @see JDBC_BIND_PARAMETERS 
     */
    public static final String LOGGING_PARAMETERS = "eclipselink.logging.parameters";

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
     * The <code>"eclipselink.tenant-id"</code> property specifies the 
     * default context property used to populate multitenant entities.
     * 
     * NOTE: This is merely a default multitenant property than can be used on 
     * its own or with other properties defined by the user. Users are not 
     * obligated to use this property and are free to specify their own.
     * 
     * @see org.eclipse.persistence.annotations.Multitenant
     * @see org.eclipse.persistence.annotations.TenantDiscriminatorColumn
     * 
     * Example: persistence.xml file <code>
     * <property name="eclipselink.tenant-id" value="Oracle"/>
     * </code> Example: property Map <code>
     * propertiesMap.put(PersistenceUnitProperties.MULTITENANT_PROPERTY_DEFAULT, "Oracle");
     * </code>
     */
    public static final String MULTITENANT_PROPERTY_DEFAULT = "eclipselink.tenant-id";
    
    /**
     * Property <code>"eclipselink.multitenant.tenants-share-cache</code> 
     * specifies that multitenant entities will share the L2 cache. By default 
     * this property is false meaning multitenant entities will have an ISOLATED 
     * setting. When setting it to true a PROTECTED cache setting will be used.
     * 
     * WARNING: Queries that use the cache may return data from other tenants 
     * when using the PROTECTED setting.
     * 
     * @see eclipselink.multitenant.tenants-share-emf
     * 
     * <p>
     * <b>Java example:</b> <code>
     * props.put(PersistenceUnitProperties.MULTITENANT_SHARED_CACHE, true);</br>
     * <p>
     * <b>XML example:</b>
     * <code>
     * <property name="eclipselink.multitenant.tenants-share-cache" value="true" />;</br>
     * </code>
     */
    public static final String MULTITENANT_SHARED_CACHE = "eclipselink.multitenant.tenants-share-cache";
    
    /**
     * Property <code>"eclipselink.multitenant.shared-emf</code> is used to 
     * indicate that multitenant entities will be used within a shared entity
     * manager factory. This property defaults to true (and applies to
     * multitenant entities only). When setting it to false, users are required 
     * to provide a unique session name.
     * 
     * @see eclipselink.session-name
     * @see eclipselink.multitenant.tenants-share-cache
     * 
     * <p>
     * <b>Java example:</b> <code>
     * props.put(PersistenceUnitProperties.MULTITENANT_SHARED_EMF, true);</br>
     * <p>
     * <b>XML example:</b>
     * <code>
     * <property name="eclipselink.multitenant.tenants-share-emf" value="true" />;</br>
     * </code>
     */
    public static final String MULTITENANT_SHARED_EMF = "eclipselink.multitenant.tenants-share-emf";
    
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
     * The <code>"eclipselink.ddl.table-creation-suffix"</code> property is used in 
     * conjunction with DDL generation options to append a string to the end of 
     * generated CREATE Table statements
     * 
     * This value is applied to all Table creation statements through the DDL generation feature
     * ie <property name="eclipselink.ddl.table-creation-suffix" value="engine=InnoDB"/>
     */
    public static final String TABLE_CREATION_SUFFIX = "eclipselink.ddl-generation.table-creation-suffix";

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
     * </p>
     * Note: <code>"eclipselink.target-database"</code> must be specified with a
     * non-"Auto" class name or short name when <code>"eclipselink.validation-only"</code>
     * is set to <code>"True"</code>.
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
     * 
     * Internal optimizations include caching of primary key and session, 
     * addition of a serialVersionUID if none exists, optimization of EclipseLink's
     * cloning strategy and optimization of the way EclipseLink gets and sets values from
     * fields mapped as Basic.
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
     * <p>The <code>"javax.persistence.schema-generation.database.action"</code> 
     * property specifies the action to be taken by the persistence provider with 
     * regard to the database artifacts.</p>
     *  
     * <p>The values for this property are <code>"none"</code>, <code>"create"</code>, 
     * <code>"drop-and-create"</code>, <code>"drop"</code>.</p> 
     * 
     * <p>If the <code>"javax.persistence.schema-generation.database.action"</code> 
     * property is not specified, no schema generation actions must be taken on 
     * the database.</p>
     */
    public static final String SCHEMA_GENERATION_DATABASE_ACTION = "javax.persistence.schema-generation.database.action";
    
    /**
     * <p>The <code>"javax.persistence.schema-generation.scripts.action"</code> 
     * property specifies which scripts are to be generated by the persistence 
     * provider.</p>
     * 
     * <p>The values for this property are <code>"none"</code>, <code>"create"</code>, 
     * <code>"drop-and-create"</code>, <code>"drop"</code>.</p> 
     * 
     * <p>Scripts will only be generated if script targets are specified. If 
     * this property is not specified, no scripts will be generated.</p>
     */
    public static final String SCHEMA_GENERATION_SCRIPTS_ACTION = "javax.persistence.schema-generation.scripts.action";
    
    /**
     * <p>The <code>"javax.persistence.schema-generation.create-source"</code> 
     * property specifies whether the creation of database artifacts is to occur 
     * on the basis of the object/relational mapping metadata, DDL script, or a 
     * combination of the two.</p>
     * 
     * <p>The values for this property are <code>"metadata"</code>, <code>"script"</code>, 
     * <code>"metadata-then-script"</code>, <code>"script-then-metadata"</code>.</p>
     * 
     * <p>If this property is not specified, and a script is specified by the 
     * <code>"javax.persistence.schema-generation.create-script-source property"</code>, 
     * the script (only) will be used for schema generation; otherwise if this 
     * property is not specified, schema generation will occur on the basis of 
     * the object/relational mapping metadata (only).</p>
     * 
     * <p>The <code>"metadata-then-script"</code> and <code>"script-then-metadata"</code> 
     * values specify that a combination of metadata and script is to be used 
     * and the order in which this use is to occur. If either of these values is 
     * specified and the resulting database actions are not disjoint, the 
     * results are undefined and schema generation may fail.</p>
     */
    public static final String SCHEMA_GENERATION_CREATE_SOURCE = "javax.persistence.schema-generation.create-source";
    
    /**
     * <p>The <code>"javax.persistence.schema-generation.drop-source"</code> property 
     * specifies whether the dropping of database artifacts is to occur on the 
     * basis of the object/relational mapping metadata, DDL script, or a 
     * combination of the two.</p> 
     * 
     * <p>The values for this property are <code>"metadata"</code>, <code>"script"</code>, 
     * <code>"metadata-then-script"</code>, <code>"script-then-metadata"</code>.</p>
     * 
     * <p>If this property is not specified, and a script is specified by the 
     * <code>"javax.persistence.schema-generation.drop-script-source"</code> property, 
     * the script (only) will be used for the dropping of database artifacts; 
     * otherwise if this property is not specified, the dropping of database 
     * artifacts will occur on the basis of the object/relational mapping 
     * metadata (only).</p>
     * 
     * <p>The <code>"metadata-then-script"</code> and <code>"script-then-metadata"</code> 
     * values specify that a combination of metadata and script is to be used 
     * and the order in which this use is to occur. If either of these values is 
     * specified and the resulting database actions are not disjoint, the 
     * results are undefined and the dropping of database artifacts may fail.</p>
     */
    public static final String SCHEMA_GENERATION_DROP_SOURCE = "javax.persistence.schema-generation.drop-source";
    
    /**
     * <p>In Java EE environments, it is anticipated that the Java EE platform 
     * provider may wish to control the creation of database schemas rather than 
     * delegate this task to the persistence provider.</p> 
     * 
     * <p>The <code>"javax.persistence.schema-generation.create-database-schemas"</code> 
     * property specifies whether the persistence provider is to create the 
     * database schema(s) in addition to creating database objects such as 
     * tables, sequences, constraints, etc.</p>
     * 
     * <p>The value of this boolean property should be set to true if the 
     * persistence provider is to create schemas in the database or to generate 
     * DDL that contains <code>"CREATE SCHEMA"</code> commands. If this property 
     * is not supplied, the provider should not attempt to create database 
     * schemas. This property may also be specified in Java SE environments.</p>
     */
    public static final String SCHEMA_GENERATION_CREATE_DATABASE_SCHEMAS = "javax.persistence.schema-generation.create-database-schemas";
    
    /**
     * <p>If scripts are to be generated, the target locations for the writing 
     * of these scripts must be specified.</p>
     * 
     * <p>The <code>"javax.persistence.schema-generation.scripts.create-target"</code> 
     * property specifies a java.IO.Writer configured for use by the persistence 
     * provider for output of the DDL script or a string specifying the file URL 
     * for the DDL script. This property should only be specified if scripts are 
     * to be generated.</p>
     */
    public static final String SCHEMA_GENERATION_SCRIPTS_CREATE_TARGET = "javax.persistence.schema-generation.scripts.create-target";
    
    /**
     * <p>If scripts are to be generated, the target locations for the writing 
     * of these scripts must be specified.</p>
     * 
     * <p>The <code>"javax.persistence.schema-generation.scripts.drop-target"</code> 
     * property specifies a java.IO.Writer configured for use by the persistence 
     * provider for output of the DDL script or a string specifying the file URL 
     * for the DDL script. This property should only be specified if scripts are 
     * to be generated.</p>
     */
    public static final String SCHEMA_GENERATION_SCRIPTS_DROP_TARGET = "javax.persistence.schema-generation.scripts.drop-target";
    
    /**
     * <p>If scripts are to be generated by the persistence provider and a 
     * connection to the target database is not supplied, the 
     * <code>"javax.persistence.database-product-name"</code> property must be 
     * specified.</p> 
     * 
     * <p>The value of this property should be the value returned for the target 
     * database by the JDBC DatabaseMetaData method getDatabaseProductName.</p> 
     * 
     * <p>If sufficient database version information is not included in the 
     * result of this method, the <code>"javax.persistence.database-major-version"</code> 
     * and <code>"javax.persistence.database-minor-version"</code> properties 
     * should be specified as needed. These should contain the values returned 
     * by the JDBC getDatabaseMajorVersion and getDatabaseMinor-Version methods 
     * respectively.</p>
     */
    public static final String SCHEMA_DATABASE_PRODUCT_NAME = "javax.persistence.database-product-name";
    
    /**
     * <p>If sufficient database version information is not included from the
     * JDBC DatabaseMetaData method getDatabaseProductName, the 
     * <code>"javax.persistence.database-major-version"</code> property should 
     * be specified as needed. This should contain the value returned by the 
     * JDBC getDatabaseMajor-Version method.</p>
     */
    public static final String SCHEMA_DATABASE_MAJOR_VERSION = "javax.persistence.database-major-version";
    
    /**
     * <p>If sufficient database version information is not included from the 
     * JDBC DatabaseMetaData method getDatabaseProductName, the 
     * <code>"javax.persistence.database-minor-version"</code> property should 
     * be specified as needed. This should contain the value returned by the 
     * JDBC getDatabaseMinor-Version method.</p>
     */
    public static final String SCHEMA_DATABASE_MINOR_VERSION = "javax.persistence.database-minor-version";
    
    /**
     * <p>The <code>"javax.persistence.schema-generation.create-script-source"</code> 
     * is used for script execution.</p>
     * 
     * <p>In Java EE container environments, it is generally expected that the 
     * container will be responsible for executing DDL scripts, although the 
     * container is permitted to delegate this task to  the persistence provider.</p> 
     * 
     * <p>If DDL scripts are to be used in Java SE environments or if the Java 
     * EE container delegates the execution of scripts to the persistence 
     * provider, this property must be specified.</p>
     * 
     * <p>The <code>"javax.persistence.schema-generation.create-script-source"</code> 
     * property specifies a java.IO.Reader configured for reading of the DDL 
     * script or a string designating a file URL for the DDL script.</p>
     */
    public static final String SCHEMA_GENERATION_CREATE_SCRIPT_SOURCE = "javax.persistence.schema-generation.create-script-source";
    
    /**
     * <p>The <code>"javax.persistence.schema-generation.drop-script-source"</code> 
     * is used for script execution.</p> 
     * 
     * <p>In Java EE container environments, it is generally expected that the 
     * container will be responsible for executing DDL scripts, although the 
     * container is permitted to delegate this task to  the persistence provider.</p> 
     * 
     * <p>If DDL scripts are to be used in Java SE environments or if the Java 
     * EE container delegates the execution of scripts to the persistence 
     * provider, this property must be specified.</p>
     * 
     * <p>The <code>"javax.persistence.schema-generation.drop-script-source"</code> 
     * property specifies a java.IO.Reader configured for reading of the DDL 
     * script or a string designating a file URL for the DDL script.</p>
     */
    public static final String SCHEMA_GENERATION_DROP_SCRIPT_SOURCE = "javax.persistence.schema-generation.drop-script-source";
    
    /**
     * <p>The <code>"javax.persistence.schema-generation.connection"</code> property 
     * specifies the JDBC connection to be used for schema generation. This is 
     * intended for use in Java EE environments, where the platform provider may 
     * want to control the database privileges that are available to the 
     * persistence provider.</p>
     * 
     * <p>This connection is provided by the container, and should be closed by 
     * the container when the schema generation request or entity manager 
     * factory creation completes.</p> 
     * 
     * <p>The connection provided must have credentials sufficient for the 
     * persistence provider to carry out the requested actions.</p> 
     * 
     * <p>If this property is not specified, the persistence provider should use 
     * the DataSource that has otherwise been provided.</p>
     */
    public static final String SCHEMA_GENERATION_CONNECTION = "javax.persistence.schema-generation.connection";
    
    /**
     * <p>In Java EE container environments, it is generally expected that the 
     * container will be responsible for executing data load scripts, although 
     * the container is permitted to delegate this task to the persistence 
     * provider. If a load script is to be used in Java SE environments or if 
     * the Java EE container delegates the execution of the load script to the 
     * persistence provider, this property must be specified.</p>
     * 
     * <p>The <code>"javax.persistence.sql-load-script-source"</code> property 
     * specifies a java.IO.Reader configured for reading of the SQL load script 
     * for database initialization or a string designating a file URL for the 
     * script.</p>
     */
    public static final String SCHEMA_GENERATION_SQL_LOAD_SCRIPT_SOURCE = "javax.persistence.sql-load-script-source";
    
    /**
     * The parameter value <code>"create"</code>
     * <p>For use with the <code>"javax.persistence.schema-generation.database.action"</code> 
     * and <code>"javax.persistence.schema-generation.scripts.action"</code> properties.</p>
     * <p>Specifies that database tables should be created.</p>
     */
    public static final String SCHEMA_GENERATION_CREATE_ACTION = "create";
    
    /**
     * The parameter value <code>"drop-and-create"</code>
     * <p>For use with the <code>"javax.persistence.schema-generation.database.action"</code> 
     * and <code>"javax.persistence.schema-generation.scripts.action"</code> properties.</p>
     * <p>Specifies that database tables should be dropped, then created.</p>
     */
    public static final String SCHEMA_GENERATION_DROP_AND_CREATE_ACTION = "drop-and-create";
    
    /**
     * The parameter value <code>"drop"</code>
     * <p>For use with the <code>"javax.persistence.schema-generation.database.action"</code> 
     * and <code>"javax.persistence.schema-generation.scripts.action"</code> properties.</p>
     * <p>Specifies that database tables should be dropped.</p>
     */
    public static final String SCHEMA_GENERATION_DROP_ACTION = "drop";
    
    /**
     * The parameter value <code>"none"</code> 
     * <p>For use with the <code>"javax.persistence.schema-generation.database.action"</code> 
     * and <code>"javax.persistence.schema-generation.scripts.action"</code> properties.</p>
     * <p>Specifies that database tables should not be created or dropped.</p>
     */
    public static final String SCHEMA_GENERATION_NONE_ACTION = "none";

    /**
     * The parameter value <code>"metadata"</code> 
     * <p>For use with the <code>"javax.persistence.schema-generation.create-source"</code> 
     * and <code>"javax.persistence.schema-generation.drop-source"</code> properties.</p>
     * <p>Specifies that DDL generation source will come from the metadata only.</p>
     */
    public static final String SCHEMA_GENERATION_METADATA_SOURCE = "metadata";
    
    /**
     * The parameter value <code>"script"</code> 
     * <p>For use with the <code>"javax.persistence.schema-generation.create-source"</code> 
     * and <code>"javax.persistence.schema-generation.drop-source"</code> properties.</p>
     * <p>Specifies that DDL generation source will come from scripts only.</p>
     */
    public static final String SCHEMA_GENERATION_SCRIPT_SOURCE = "script";
    
    /**
     * The parameter value <code>"metadata-then-script"</code> 
     * <p>For use with the <code>"javax.persistence.schema-generation.create-source"</code> 
     * and <code>"javax.persistence.schema-generation.drop-source"</code> properties.</p>
     * <p>Specifies that DDL generation source will come from the metadata first
     * followed with the scripts.</p>
     */
    public static final String SCHEMA_GENERATION_METADATA_THEN_SCRIPT_SOURCE = "metadata-then-script";
    
    /**
     * The parameter value <code>"script-then-metadata"</code> 
     * <p>For use with the <code>"javax.persistence.schema-generation.create-source"</code> 
     * and <code>"javax.persistence.schema-generation.drop-source"</code> properties.</p>
     * <p>Specifies that DDL generation source will come from the scripts first
     * followed with the metadata.</p>
     */
    public static final String SCHEMA_GENERATION_SCRIPT_THEN_METADATA_SOURCE = "script-then-metadata";
    
    /**
     * The <code>"eclipselink.sequencing.default-sequence-to-table"</code> property
     * determines the default behavior when a GeneratedValue of type SEQUENCE is used
     * on a database platform that does not support SEQUENCE generation.
     * By default IDENTITY generation is used if supported.
     * If this property is set to true, then TABLE sequencing will be used instead.
     */
    public static final String SEQUENCING_SEQUENCE_DEFAULT = "eclipselink.sequencing.default-sequence-to-table";
    
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
     * <p>
     * This setting has been replaced by UPPERCASE_COLUMN_NAMES, which should be used instead,
     * as it ensure both sides use upper case for comparisons.
     * @see #UPPERCASE_COLUMN_NAMES
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
     * <p>Batch writing allows multiple heterogeneous dynamic SQL statements to be sent to the database as a single
     * execution, or multiple homogeneous parameterized SQL statements to be executed as a single batch execution.
     * <p>Note that not all JDBC drivers, or databases support batch writing.
     * <p>
     * Values (case insensitive):
     * <ul>
     * <li>"JDBC": use JDBC batch writing.
     * <li>"Buffered": do not use either JDBC batch writing nor native platform
     * batch writing.
     * <li>"Oracle-JDBC": use Oracle's native batch writing. This requires the
     * use of an Oracle JDBC driver.
     * <li>&ltcustom-class&gt - A custom class that extends the BatchWritingMechansim class.
     * <li>"None" (DEFAULT): do not use batch writing (turn it off).
     * </ul>
     * 
     * @see BatchWriting
     * @see BatchWritingMechanism
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
     * Default: "false".
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
     * The <code>"eclipselink.project-cache"</code>property configures the type of
     * ProjectCacheAccessor implementation to use to retrieve and store projects
     * representing the metadata for the project
     * <p>
     * Values (case insensitive):
     * <ul>
     * <li>"java-serialization": Use {@link FileBasedProjectCache}
     * <li>Custom ProjectCacheAccessor - Specify a custom class name which
     * implements {@link ProjectCacheAccessor}
     * </ul>
     * 
     * @see ProjectCacheAccessor
     */
    public static final String PROJECT_CACHE = "eclipselink.project-cache"; 
    
    /**
     * The property <code>"eclipselink.project-cache.java-serialization.file"</code>
     * 
     * <p>Specifies the name of the file to read/write a serialized project representing the application's
     * metadata</p>* 
     * 
     * 
     * <p>Specifies the name of the metadata repository xml file to read from using classloader to find the resource</p>
     * 
     * <p>This property should be used in conjunction with 
     * <code>"eclipselink.project-cache"</code> when a project is serialized to a file for caching.</p>
     * 
     * @see #PROJECT_CACHE
     */
    public static final String PROJECT_CACHE_FILE = "eclipselink.project-cache.java-serialization.file-location";

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
     * The <code>"eclipselink.jdbc.allow-native-sql-queries"</code> property 
     * specifies whether any user defined SQL is allowed within a persistence 
     * unit. This is of particular importance within a multitenant to minimize 
     * the potential impact of revealing multi tenant information. By default
     * any persistence unit containing at least one multitenant entity will
     * cause this flag to be set to 'false'. 
     * <p>
     * Allowed Values (String):
     * <ul>
     * <li>"true" - (DEFAULT) allow native SQL
     * <li>"false" - do not allow native SQL.
     * </ul>
     *
     * @see Project#setAllowNativeSQLQueries(boolean)
     */
    public static final String ALLOW_NATIVE_SQL_QUERIES = "eclipselink.jdbc.allow-native-sql-queries";
    
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
     * @see #ID_VALIDATION
     */
    public static final String ALLOW_ZERO_ID = "eclipselink.allow-zero-id";

    /**
     * The <code>"eclipselink.id-validation"</code> property defines
     * which primary key components values are considered invalid.
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

    /**
     * The <code>"eclipselink.deploy-on-startup"</code> property controls whether
     * EclipseLink creates the persistence unit when the application starts up, or
     * when the persistence unit is first actually accessed by the application.
     *
     * Setting this to true causes the persistence unit to be created when the
     * EntityManagerFactory is created, usually during deployment to a Java EE
     * 7 container or servlet container.  Enabling this option may increase
     * startup time of the container/server, but will prevent the first request
     * to the application from pausing while the persistence unit is deployed.
     *
     * When this property is set to false the persistence unit is not
     * initialized until the first EntityManager is created or until metadata
     * is requested from the EntityManagerFactory. 
     * 
     * When set to False, there is a known issue with Fields of static metamodel 
     * classes ("Entity_" classes) being null until the persistence unit is
     * initialized. This behaviour won't affect applications unless they use 
     * the static metamodel feature.  (See bug 383199)
     * <p>
     * Values: A boolean value of "True" or (default "False").
     */
    public static final String DEPLOY_ON_STARTUP = "eclipselink.deploy-on-startup";
    
    /**
     * The <code>"eclipselink.validation-only"</code> property validates deployment
     * which includes initializing descriptors but does not connect (no login to the database).
     * <p>
     * Values: A boolean value of "True" or (default "False").
     * </p>
     * Note: <code>"eclipselink.target-database"</code> must be specified with a
     * non-"Auto" class name or short name when <code>"eclipselink.validation-only"</code>
     * is set to <code>"True"</code>.
     * 
     * @see TargetDatabase
     */    
    // See 324213
    public static final String VALIDATION_ONLY_PROPERTY = "eclipselink.validation-only";

    /**
     * Allows the database schema to be generated on deployment. Valid values,
     * CREATE_ONLY, DROP_AND_CREATE, CREATE_OR_EXTEND, NONE ("create-tables",
     * "drop-and-create-tables", "create-or-extend-tables", "none"). Default is NONE.
     */
    public static final String DDL_GENERATION = "eclipselink.ddl-generation";

    /**
     * The parameter value <code>"create-tables"</code>
     * 
     * <p>For use with the <code>"eclipselink.ddl-generation"</code> property.</p>
     * <p>Specifies that database tables should be created.</p>
     *
     * @see #DDL_GENERATION
     */
    public static final String CREATE_ONLY = "create-tables";
    
    /**
     * The parameter value <code>"drop-tables"</code>
     * 
     * <p>For use with the <code>"eclipselink.ddl-generation"</code> property.</p>
     * <p>Specifies that database tables should be dropped only.</p>
     * 
     * @see #DDL_GENERATION
     */
    public static final String DROP_ONLY = "drop-tables";
    
    /**
     * The parameter value <code>"drop-and-create-tables"</code>
     * 
     * <p>For use with the <code>"eclipselink.ddl-generation"</code> property.</p>
     * <p>Specifies that database tables should be dropped, then created.</p>
     * 
     * @see #DDL_GENERATION
     */
    public static final String DROP_AND_CREATE = "drop-and-create-tables";
    
    /**
     * The parameter value <code>"create-or-extend-tables"</code>
     * 
     * <p>For use with the <code>"eclipselink.ddl-generation"</code> property.</p>
     * <p>Specifies that database tables should be created and if existing, missing columns will be added.</p>
     * <p>This can only be used with eclipselink.ddl-generation.output-mode with value of "database".</p>
     * 
     * @see #DDL_GENERATION
     */
    public static final String CREATE_OR_EXTEND = "create-or-extend-tables";
    
    /**
     * The parameter value <code>"none"</code>
     * 
     * <p>For use with the <code>"eclipselink.ddl-generation"</code> property,
     * and is the default parameter value.</p>
     * <p>Specifies that database tables should not be created or dropped.</p>
     * 
     * @see #DDL_GENERATION
     */
    public static final String NONE = "none";
    
    /**
     * The <code>"eclipselink.metadata-source"</code>property configures the type of
     * MetadataSource implementation to use to read Metadata
     * <p>
     * Values (case insensitive):
     * <ul>
     * <li>"XML": Use {@link XMLMetadataSource}
     * <li>Custom MetadataSource - Specify a custom class name which
     * implements {@link MetadataSource}
     * </ul>
     * 
     * @see MetadataSource
     */
    public static final String METADATA_SOURCE = "eclipselink.metadata-source";
    
    /**
     * the <code>"eclipselink.metadata-source.send-refresh-command"</code> property
     * works with cache coordination for a clustered environment to. If cache coordination
     * is configured and the session is deployed on startup, this property controls the sending
     * of RCM refresh metadata commands to the cluster.  These commands will cause the remote
     * instances to refresh their metadata.
     * 
     * Defaults to true
     * 
     * @see #COORDINATION_PROTOCOL
     * @see #DEPLOY_ON_STARTUP 
     */
    public static final String METADATA_SOURCE_RCM_COMMAND = "eclipselink.metadata-source.send-refresh-command";
    
    /**
     * The property <code>"eclipselink.metadata-source.xml.file"</code>
     * 
     * <p>Specifies the name of the metadata repository xml file to read from using classloader to find the resource</p>
     * 
     * <p>This property should be used in conjunction with the 
     * <code>"eclipselink.metadata-repository"</code> when an XML repository is being used.</p>
     * 
     * @see #METADATA_SOURCE
     */
    public static final String METADATA_SOURCE_XML_FILE = "eclipselink.metadata-source.xml.file";
    
    /**
     * The property <code>"eclipselink.metadata-source.xml.URL"</code>
     * 
     * <p>Specifies the name of the metadata repository xml URL to read from.</p>
     * 
     * <p>This property should be used in conjunction with the 
     * <code>"eclipselink.metadata-repository"</code> when an XML repository is being used.</p>
     * 
     * @see #METADATA_SOURCE
     */
    public static final String METADATA_SOURCE_XML_URL = "eclipselink.metadata-source.xml.url";

    /**
     * The property <code>"eclipselink.metadata-source.properties.file"</code>
     * 
     * <p>Specifies the name of the metadata repository properties file to read from using classloader to find the resource</p>
     * 
     * @see #METADATA_SOURCE
     */
    public static final String METADATA_SOURCE_PROPERTIES_FILE = "eclipselink.metadata-source.properties.file";
    
    /**
     * The property <code>"eclipselink.application-location"</code>
     * 
     * <p>Specifies the file system directory location where 
     * DDL files are written (output) to.</p>
     * 
     * <p>This property should be used in conjunction with the 
     * <code>"eclipselink.ddl-generation.output-mode"</code> property, with 
     * a setting of <code>"sql-script"</code> (or <code>"both"</code>) for 
     * DDL file(s) to be written.</p>
     * 
     * @see #DEFAULT_APP_LOCATION
     * @see #DDL_GENERATION_MODE
     * @see #DDL_SQL_SCRIPT_GENERATION
     * @see #DDL_BOTH_GENERATION
     */
    public static final String APP_LOCATION = "eclipselink.application-location";
    
    /**
     * The property <code>"eclipselink.create-ddl-jdbc-file-name"</code>
     * 
     * <p>Specifies the name of the DDL file which is used to create 
     * database tables.</p>
     * 
     * <p>This property should be used in conjunction with the 
     * <code>"eclipselink.application-location"</code> property to specify a 
     * location on the file system for DDL file(s) to be written.</p>
     * 
     * @see #APP_LOCATION
     * @see #DEFAULT_CREATE_JDBC_FILE_NAME
     */
    public static final String CREATE_JDBC_DDL_FILE = "eclipselink.create-ddl-jdbc-file-name";
    
    /**
     * The property <code>"eclipselink.drop-ddl-jdbc-file-name"</code>
     * 
     * <p>Specifies the name of the DDL file which is used to drop 
     * database tables.</p>
     * 
     * <p>This property should be used in conjunction with the 
     * <code>"eclipselink.application-location"</code> property to specify a 
     * location on the file system for DDL file(s) to be written.</p>
     * 
     * @see #APP_LOCATION
     * @see #DEFAULT_DROP_JDBC_FILE_NAME
     */
    public static final String DROP_JDBC_DDL_FILE = "eclipselink.drop-ddl-jdbc-file-name";

    /**
     * The default location in the file system to output DDL files.
     * Defaults to: the working directory.
     * 
     * @see #APP_LOCATION
     */
    public static final String DEFAULT_APP_LOCATION = "." + File.separator;
    
    /**
     * The default name of the DDL file which is used to create database tables.
     * Defaults to: <code>createDDL.jdbc</code>
     * 
     * @see #CREATE_JDBC_DDL_FILE
     */
    public static final String DEFAULT_CREATE_JDBC_FILE_NAME = "createDDL.jdbc";
    
    /**
     * The default name of the DDL file which is used to drop database tables.
     * Defaults to: <code>dropDDL.jdbc</code>
     * 
     * @see #DROP_JDBC_DDL_FILE
     */
    public static final String DEFAULT_DROP_JDBC_FILE_NAME = "dropDDL.jdbc";
    
    /**
     * The system property <code>INTERACT_WITH_DB</code>
     * 
     * <p>Specified to enable or disable the execution of DDL (configured with 
     * the <code>eclipselink.ddl-generation<code> property) against a database.</p>
     * 
     * <p>e.g. a command line setting of <code>-DINTERACT_WITH_DB=false</code> 
     * will not output DDL to the database.</p>
     * 
     * <p>Valid values:</p>
     * <code>true</code> - output DDL to the database<br> 
     * <code>false</code> - do not output DDL to the database
     * 
     * @see #DDL_GENERATION
     */
    public static final String JAVASE_DB_INTERACTION = "INTERACT_WITH_DB";

    /**
     * Configures if database schema should be generated on the database, to a
     * file, or both. Valid values, DDL_SQL_SCRIPT_GENERATION,
     * DDL_DATABASE_GENERATION, DDL_BOTH_GENERATION ("sql-script", "database",
     * "both") DDL_GENERATION must also be set, for this to have an effect.
     * Default is DDL_DATABASE_GENERATION.
     * 
     * @see #DEFAULT_DDL_GENERATION_MODE
     * @see #DDL_DATABASE_GENERATION
     * @see #DDL_SQL_SCRIPT_GENERATION
     * @see #DDL_BOTH_GENERATION
     */
    public static final String DDL_GENERATION_MODE = "eclipselink.ddl-generation.output-mode";

    /**
     * The <code>"eclipselink.ddl-generation.index-foreign-keys"</code> property.
     * Configures if an index should be automatically generated for foreign key constraints.
     * It is normally recommended to have an index for a foreign key.
     * By default indexes are not generated, most database also do not auto generate indexes, although some do.
     * Values: "true" or "false" (default).
     * 
     * @see #DDL_GENERATION
     */
    public static final String DDL_GENERATION_INDEX_FOREIGN_KEYS = "eclipselink.ddl-generation.index-foreign-keys";
    
    /**
     * The parameter value <code>"sql-script"</code>
     * 
     * <p>For use with the <code>"eclipselink.ddl-generation.output-mode"</code> property.</p>
     * 
     * <p>Specifies that DDL will be written to file(s).</p>
     * 
     * @see #DDL_GENERATION_MODE
     * @see #CREATE_JDBC_DDL_FILE
     * @see #DROP_JDBC_DDL_FILE
     */
    public static final String DDL_SQL_SCRIPT_GENERATION = "sql-script";
    
    /**
     * The parameter value <code>"database"</code>
     * 
     * <p>For use with the <code>"eclipselink.ddl-generation.output-mode"</code> property, 
     * and is the default parameter value</p>
     * 
     * <p>Specifies that DDL will be written to the database.</p>
     * 
     * @see #DDL_GENERATION_MODE
     * @see #CREATE_JDBC_DDL_FILE
     * @see #DROP_JDBC_DDL_FILE
     */
    public static final String DDL_DATABASE_GENERATION = "database";
    
    /**
     * The parameter value <code>"both"</code> 
     * 
     * <p>For use with the <code>"eclipselink.ddl-generation.output-mode"</code> property.</p>
     * 
     * <p>Specifies that DDL will be written to file(s) and the database.</p>
     * 
     * @see #DDL_GENERATION_MODE
     * @see #CREATE_JDBC_DDL_FILE
     * @see #DROP_JDBC_DDL_FILE
     */
    public static final String DDL_BOTH_GENERATION = "both";
    
    /**
     * The <code>eclipselink.ddl-generation.output-mode</code> parameter is configured
     * to the default value of <code>database</code>.
     * 
     * @see #DDL_GENERATION_MODE
     * @see #DDL_DATABASE_GENERATION
     */
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
     * This can be used to avoid possible database deadlocks from concurrent 
     * threads updating the same objects in a different order.
     * If not set to true, the order of updates is not guaranteed.
     * "true" by default.
     */    
    public static final String ORDER_UPDATES = "eclipselink.order-updates";
    
    /**
     * The <code>"eclipselink.profiler"</code>property configures the type of
     * profiler used to capture runtime statistics.
     * <p>
     * Values (case insensitive):
     * <ul>
     * <li>"NoProfiler" (DEFAULT)
     * <li>"PerformanceMonitor": Use {@link PerformanceMonitor}
     * <li>"PerformanceProfiler": Use {@link PerformanceProfiler}
     * <li>"QueryMonitor": Use {@link QueryMonitor}
     * <li>"DMSProfiler": Use {@link DMSPerformanceProfiler}
     * <li>Custom profiler - Specify a custom profiler class name which
     * implements {@link SessionProfiler}
     * </ul>
     * 
     * @see ProfilerType
     */
    public static final String PROFILER = "eclipselink.profiler";
    
    /**
     * The <code>"eclipselink.tuning"</code>property configures the type of
     * tuner to use to configure the persistence unit.
     * A SessionTuner can be used to define a template for a persistence unit configuration.
     * It allows a set of configuration values to be configured as a single tuning option.
     * <p>
     * Values (case insensitive):
     * <ul>
     * <li>"Standard" (DEFAULT)
     * <li>"Safe": See {@link SafeModeTuner}
     * <li>Custom tuner - Specify a full class name of an implementation of {@link SessionTuner}
     * </ul>
     * 
     * @see TunerType
     */
    public static final String TUNING = "eclipselink.tuning";

    /**
     * The <code>"eclipselink.transaction.join-existing"</code> property Set to
     * "true" this property forces persistence context to read through
     * JTA-managed ("write") connection in case there is an active
     * transaction.The property set in persistence.xml or passed to
     * createEntityManagerFactory affects all EntityManagers created by the
     * factory. Note that if the property set to "true" then objects read during
     * transaction won't be placed into the shared cache unless they have been
     * updated. Alternatively, to apply the property only to some
     * EntityManagers pass it to createEntityManager method.
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
     * <li>"jms-publishing"
     * <li>"rmi"
     * <li>"rmi-iiop"
     * <li>a <package.class> name of a subclass implementation of the TransportManager abstract class.
     * </ul>
     * by default the cache is not coordinated.
     * 
     * @see CacheCoordinationProtocol
     * @see Cache#coordinationType()
     * @see RemoteCommandManager#setTransportManager(TransportManager)
     */
    public static final String COORDINATION_PROTOCOL = "eclipselink.cache.coordination.protocol";

    /**
     * The <code>"eclipselink.cache.coordination.jms.host"</code> property
     * configures cache coordination for a clustered environment. Only used for
     * JMS coordination. Sets the URL for the JMS server hosting the topic.
     * This is not required in the topic is distributed across the cluster (can be looked up in local JNDI).
     * 
     * @see #COORDINATION_PROTOCOL
     * @see org.eclipse.persistence.sessions.coordination.jms.JMSTopicTransportManager#setTopicHostUrl(String)
     */
    public static final String COORDINATION_JMS_HOST = "eclipselink.cache.coordination.jms.host";

    /**
     * The <code>"eclipselink.cache.coordination.jms.topic"</code> property
     * configures cache coordination for a clustered environment. Only used for
     * JMS coordination. Sets the JMS topic name.
     * The default topic JNDI name is "jms/EclipseLinkTopic".
     * 
     * @see #COORDINATION_PROTOCOL
     * @see org.eclipse.persistence.sessions.coordination.broadcast.BroadcastTransportManager#setTopicName(String)
     */
    public static final String COORDINATION_JMS_TOPIC = "eclipselink.cache.coordination.jms.topic";

    /**
     * The <code>"eclipselink.cache.coordination.jms.factory"</code> property
     * configures cache coordination for a clustered environment. Only used for
     * JMS coordination. Sets the JMS topic connection factory name.
     * The default topic connection factory JNDI name is "jms/EclipseLinkTopicConnectionFactory".
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
     * Default is 1000 milliseconds.
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
     * The default address is 239.192.0.0.
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
     * The default port is 3121.
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
     * This may not be required in a clustered environment where JNDI is replicated.
     * This can also be set as a System property or using a SessionCustomizer to avoid
     * a separate persistence.xml per server.
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
     * This is not normally require if connecting to the local server.
     * 
     * @see #COORDINATION_PROTOCOL
     * @see org.eclipse.persistence.sessions.coordination.TransportManager#setUserName(String)
     */
    public static final String COORDINATION_JNDI_USER = "eclipselink.cache.coordination.jndi.user";

    /**
     * The <code>"eclipselink.cache.coordination.jndi.password"</code> property
     * configures cache coordination for a clustered environment. Set the JNDI
     * naming service user name.
     * This is not normally require if connecting to the local server.
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
     * This is not normally require if connecting to the local server.
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
     * the
     * <code>"eclipselink.cache.coordination.thread.pool.size"</code>
     * property configures thread pool size for cache coordination threads.
     * RMI cache coordination will spawn one thread per node to send change notifications.
     * RMI also spawns a thread to listen for new node notifications.
     * JMS cache coordination will spawn one thread to receive JMS change notification messages (unless MDB is used).
     * JMS also spawns a thread to process the change notificaiton (unless MDB is used).
     * The default size is 32 threads.
     * A size of 0 indicates no thread pool should be used, and threads will be spawned when required.
     * 
     * @see #COORDINATION_PROTOCOL
     * @see org.eclipse.persistence.platform.server.ServerPlatformBase#setThreadPoolSize(int)
     */
    public static final String COORDINATION_THREAD_POOL_SIZE = "eclipselink.cache.coordination.thread.pool.size";
    
    /**
     * the <code>"eclipselink.cache.coordination.channel"</code> property
     * configures cache coordination for a clustered environment. Set if the
     * channel for this cluster. All server's in the same channel will be
     * coordinated.  The default channel name is "EclipseLinkCommandChannel".
     * 
     * @see #COORDINATION_PROTOCOL
     * @see org.eclipse.persistence.sessions.coordination.RemoteCommandManager#setChannel(String)
     */
    public static final String COORDINATION_CHANNEL = "eclipselink.cache.coordination.channel";

    /**
     * Indicates if it's a composite persistence unit ("true").
     * The property must be specified in persistence.xml of a composite persistence unit.
     * The property passed to createEntityManagerFactory method or in system properties is ignored.
     * Composite persistence unit would contain all persistence units found in jar files specified by <jar-file> elements in persistence.xml. 
     *    <jar-file>member1.jar</jar-file>
     *    <jar-file>member2.jar</jar-file>
     *    <properties>
     *        <property name="eclipselink.composite-unit" value="true"/>
     *    </properties>
     * @see #COMPOSITE_UNIT_MEMBER
     * @see #COMPOSITE_UNIT_PROPERTIES
     */
    public static final String COMPOSITE_UNIT = "eclipselink.composite-unit";
    
    /**
     * Indicates if the persistence unit must be a member of a composite persistence unit ("true"),
     * can't be used as an independent persistence unit.
     * That happens if persistence unit has dependencies on other persistence unit(s).
     * The property may be specified in persistence.xml.
     * The property passed to createEntityManagerFactory method or in system properties is ignored.
     * If this property is set to true, EntityManagerFactory still could be created,
     * but it can't be connected: an attempt to create entity manager would cause an exception.
     * @see #COMPOSITE_UNIT
     * @see #COMPOSITE_UNIT_PROPERTIES
     */
    public static final String COMPOSITE_UNIT_MEMBER = "eclipselink.composite-unit.member";
    
    /**
     * The property may be passed to createEntityManagerFactory method of a composite persistence unit
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
     * @see #COMPOSITE_UNIT
     */
    public static final String COMPOSITE_UNIT_PROPERTIES = "eclipselink.composite-unit.properties";

    /**
     * The <code>"eclipselink.remote.protocol"</code> property
     * configures remote JPA for a client or server.
     * This allows JPA to be access over RMI or other protocol from a remote Java client.
     * <p>
     * Values:
     * <ul>
     * <li>"rmi"
     * <li>a <package.class> name of a subclass implementation of the RemoteConnection abstract class.
     * </ul>
     * 
     * @see RemoteProtocol
     * @see org.eclipse.persistence.internal.sessions.remote.RemoteConnection
     * @see RemoteSession
     */
    public static final String REMOTE_PROTOCOL = "eclipselink.remote.protocol";
    
    /**
     * The <code>"eclipselink.remote.client.url"</code> property
     * configures remote JPA for a client.
     * This allows JPA to be access over RMI or other protocol from a remote Java client.
     * The URL is the complete URL used to access the RMI server.
     * 
     * @see REMOTE_PROTOCOL
     */
    public static final String REMOTE_URL = "eclipselink.remote.client.url";
    
    /**
     * The <code>"eclipselink.remote.server.name"</code> property
     * configures remote JPA for a server.
     * This allows JPA to be access over RMI or other protocol from a remote Java client.
     * The name is the name the server will be registered under in the RMI registry.
     * 
     * @see REMOTE_PROTOCOL
     */
    public static final String REMOTE_SERVER_NAME = "eclipselink.remote.server.name";
    
    /**
     * The <code>"eclipselink.nosql.connection-spec"</code> property.<br>
     * Allows the connection information for an NoSQL or EIS datasource to be specified.
     * An NoSQL datasource is a non-relational datasource such as a legacy database, NoSQL database,
     * XML database, transactional and messaging systems, or ERP systems.
     * @see org.eclipse.persistence.eis.EISConnectionSpec
     * @see org.eclipse.persistence.annotations.NoSQL
     */
    public static final String NOSQL_CONNECTION_SPEC = "eclipselink.nosql.connection-spec";
    
    /**
     * The <code>"eclipselink.nosql.connection-factory"</code> property.<br>
     * Allows the JCA ConnectionFactory to be specified for a NoSQL or EIS adapter.
     * An NoSQL datasource is a non-relational datasource such as a legacy database, NoSQL database,
     * XML database, transactional and messaging systems, or ERP systems.
     * @see javax.resource.cci.ConnectionFactory
     */
    public static final String NOSQL_CONNECTION_FACTORY = "eclipselink.nosql.connection-factory";
    
    /**
     * The <code>"eclipselink.jdbc.connector"</code> property.<br>
     * Allows a custom connector to be used to define how to connect to the database.
     * This is not required if a DataSource or JDBC DriverManager is used.
     * It can be used to connect to a non standard connection pool,
     * or provide additional customization in how a connection is obtained.
     * @see org.eclipse.persistence.sessions.JNDIConnector
     * @see org.eclipse.persistence.sessions.DefaultConnector
     */
    public static final String JDBC_CONNECTOR = "eclipselink.jdbc.connector";
    
    /**
     * Allows setting of NoSQL connection properties.
     * The NoSQL specific property name should be appended to this prefix.
     * <p>
     * i.e.<br>
     * "eclipselink.nosql.property.nosql.host"="localhost:5000"
     * 
     * @see org.eclipse.persistence.eis.EISConnectionSpec
     * @see org.eclipse.persistence.annotations.NoSQL
     */
    public static final String NOSQL_PROPERTY = "eclipselink.nosql.property.";
    
    /**
     * Allows setting of NoSQL user property.
     * Note that "javax.persistence.jdbc.user" is also supported.
     * 
     * @see org.eclipse.persistence.eis.EISConnectionSpec
     * @see org.eclipse.persistence.annotations.NoSQL
     */
    public static final String NOSQL_USER = "eclipselink.nosql.property.user";
    
    /**
     * Allows setting of NoSQL password property.
     * Note that "javax.persistence.jdbc.password" is also supported.
     * 
     * @see org.eclipse.persistence.eis.EISConnectionSpec
     * @see org.eclipse.persistence.annotations.NoSQL
     */
    public static final String NOSQL_PASSWORD = "eclipselink.nosql.property.password";
    
    /**
     * Allows passing of JDBC driver specific connection properties.
     * This allows for properties to be set on the JDBC connection.
     * The JDBC driver specific property name should be appended to this prefix.
     * <p>
     * i.e.<br>
     * "eclipselink.jdbc.property.defaultRowPrefetch"="25"
     * 
     * @see org.eclipse.persistence.sessions.DatasourceLogin#setProperty(String, Object)
     */
    public static final String JDBC_PROPERTY = "eclipselink.jdbc.property.";
    
    /**
     * INTERNAL: The following properties will not be displayed through logging
     * but instead have an alternate value shown in the log.
     */
    public static final Map<String, String> PROPERTY_LOG_OVERRIDES = new HashMap<String, String>(1);

    static {
        PROPERTY_LOG_OVERRIDES.put(JDBC_PASSWORD, "xxxxxx");
    }


    /**
     * The <code>"eclipselink.weaving.rest"</code> property configures
     * whether classes will be weaved to support our JPA_RS functionality
     * <p>
     * This property will only be considered if weaving is enabled.
     * <p>
     * Values (case insensitive):
     * <ul>
     * <li>"true" (DEFAULT)
     * <li>"false"
     * </ul>
     */
    public static final String WEAVING_REST = "eclipselink.weaving.rest";

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
