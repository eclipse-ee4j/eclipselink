/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 1998, 2024 IBM Corporation. All rights reserved.
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
//     dclarke - Bug 294985: update of comments and addition of connection logging property
//     cdelahun - Bug 214534: added COORDINATION_JMS_REUSE_PUBLISHER property to enable JMS rcm legacy behavior
//     04/01/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 2)
//     06/30/2011-2.3.1 Guy Pelletier
//       - 341940: Add disable/enable allowing native queries
//     09/20/2011-2.3.1 Guy Pelletier
//       - 357476: Change caching default to ISOLATED for multitenant's using a shared EMF.
//     12/24/2012-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support
//     01/08/2013-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support
//     01/11/2013-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support
//     02/04/2013-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support
//     02/19/2013-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support
//     08/11/2014-2.5 Rick Curtis
//       - 440594: Tolerate invalid NamedQuery at EntityManager creation.
//     11/04/2014 - Rick Curtis
//       - 450010 : Add java se test bucket
//     01/13/2015 - Rick Curtis
//       - 438871 : Add support for writing statement terminator character(s) when generating ddl to script.
//     02/19/2015 - Rick Curtis
//       - 458877 : Add national character support
//     09/03/2015 - Will Dazey
//       - 456067 : Added support for defining query timeout units
//     09/28/2015 - Will Dazey
//       - 478331 : Added support for defining local or server as the default locale for obtaining timestamps
//     12/03/2015 - 2.6 Dalia Abo Sheasha
//       - 483582 : Add the jakarta.persistence.sharedCache.mode property
//     09/14/2017 - 2.6 Will Dazey
//       - 522312 : Add the eclipselink.sequencing.start-sequence-at-nextval property
//     04/11/2018 - Will Dazey
//       - 533148 : Add the eclipselink.jpa.sql-call-deferral property
//     12/06/2018 - Will Dazey
//       - 542491: Add new 'eclipselink.jdbc.force-bind-parameters' property to force enable binding
//     12/05/2023: Tomas Kraus
//       - New Jakarta Persistence 3.2 Features
package org.eclipse.persistence.config;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * The class defines EclipseLink persistence unit property names. These values
 * are used to assist in the configuration of properties passed to
 * {@code Persistence#createEntityManagerFactory(String, Map)} which override
 * the values specified in the persistence.xml file.
 * <p>
 * <b>Usage Example:</b>
 * {@snippet :
 *  Map<String, Object> props = new HashMap<String, Object>();
 *
 *  props.put(PersistenceUnitProperties.JDBC_USER, "user-name");
 *  props.put(PersistenceUnitProperties.JDBC_PASSWORD, "password");
 *
 *  EntityManagerFactory emf = Persistence.createEntityManagerFactory("pu-name", props);
 * }
 * <p>
 * Property values are usually case-insensitive with some common sense
 * exceptions, for instance class names.
 *
 * @see jakarta.persistence.Persistence#createEntityManagerFactory(String, Map)
 */
public final class PersistenceUnitProperties {

    /**
     * The {@code jakarta.persistence.provider} property specifies the name
     * of a provider-specific implementation of {@link jakarta.persistence.spi.PersistenceProvider}.
     * This property overrides the value specified in the persistence.xml.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>fully qualified name of the provider-specific implementation class
     * <li>{@link Class} instance of the provider-specific implementation class
     * </ul>
     */
    public static final String PROVIDER = "jakarta.persistence.provider";

    /**
     * The {@code jakarta.persistence.transactionType} property specifies the
     * transaction type for the persistence unit. This property overrides the
     * value specified in the persistence.xml.
     * <p>
     * <b>Allowed Values</b> (case-sensitive string)<b>:</b>
     * <ul>
     * <li>{@code JTA}
     * <li>{@code RESOURCE_LOCAL}
     * </ul>
     */
    public static final String TRANSACTION_TYPE = "jakarta.persistence.transactionType";

    /**
     * The {@code jakarta.persistence.jtaDataSource} property specifies the JTA data
     * source name that will look up a valid {@link javax.sql.DataSource}. This
     * property is used to override the value specified in the persistence.xml.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>a well formed JNDI resource name that can locate the data source in the target container
     * <li>an instance of {@link javax.sql.DataSource}.
     * </ul>
     */
    public static final String JTA_DATASOURCE = "jakarta.persistence.jtaDataSource";

    /**
     * The {@code jakarta.persistence.nonJtaDataSource} property specifies the
     * non-JTA data source name that will look up a valid
     * {@link javax.sql.DataSource}. This can be used to override the value
     * specified in the persistence.xml.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>a well formed JNDI resource name that can locate the data source
     * in the target container
     * <li>an instance of {@link javax.sql.DataSource}
     * </ul>
     */
    public static final String NON_JTA_DATASOURCE = "jakarta.persistence.nonJtaDataSource";

    // JDBC Properties for internal connection pooling

    /**
     * The {@code jakarta.persistence.jdbc.driver} property specifies the JDBC
     * {@link java.sql.DriverManager} class name used for internal connection pooling when a data
     * source is not being used.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>the fully qualified name for a class that implements {@link java.sql.Driver} interface
     * </ul>
     * <p>
     * <b>Persistence XML example:</b>
     * {@snippet lang="XML" :
     *  <property name="jakarta.persistence.jdbc.driver" value="com.mysql.jdbc.Driver" />
     * }
     * <p>
     * The previous value for this property {@code eclipselink.jdbc.driver} is now deprecated
     * and should be replaced with this new name.
     * </p>
     */
    public static final String JDBC_DRIVER = "jakarta.persistence.jdbc.driver";

    /**
     * The {@code jakarta.persistence.jdbc.url} property specifies the JDBC URL used
     * for internal connection pooling when a data source is not being used.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>a string which represents a valid URL for the specified JDBC driver
     * </ul>
     * <p>
     * <b>Persistence XML example:</b>
     * {@snippet lang="XML" :
     *  <property name="jakarta.persistence.jdbc.url" value="jdbc:mysql://localhost/mysql" />
     * }
     * <p>
     * The previous value for this property {@code eclipselink.jdbc.url} is now deprecated
     * and should be replaced with this new name.
     * </p>
     */
    public static final String JDBC_URL = "jakarta.persistence.jdbc.url";

    /**
     * The {@code jakarta.persistence.jdbc.user} property specifies the data source
     * or JDBC user name.
     * <p>
     * <b>Persistence XML example:</b>
     * {@snippet lang="XML" :
     *  <property name="jakarta.persistence.jdbc.user" value="user-name" />
     * }
     * <p>
     * The previous value for this property {@code eclipselink.jdbc.user} is now deprecated and should
     * be replaced with this new name.
     */
    public static final String JDBC_USER = "jakarta.persistence.jdbc.user";

    /**
     * The {@code jakarta.persistence.jdbc.password} property specifies the data
     * source or JDBC password.
     * <p>
     * <b>Persistence XML example:</b>
     * {@snippet lang="XML" :
     *  <property name="jakarta.persistence.jdbc.password" value="password" />
     * }
     * <p>
     * The previous value for this property {@code eclipselink.jdbc.password} is now deprecated
     * and should be replaced with this new name.
     */
    public static final String JDBC_PASSWORD = "jakarta.persistence.jdbc.password";

    /**
     * The {@code eclipselink.jdbc.native-sql} property specifies whether
     * generic SQL should be used or platform specific 'native' SQL. The
     * platform specific SQL customizes join syntax, date operators, sequencing,
     * ...
     * <p>
     * <b>Allowed Values</b> (String)<b>:</b>
     * <ul>
     * <li>{@code false} (DEFAULT) - use generic SQL
     * <li>{@code true} - use database specific SQL
     * </ul>
     *
     * @see org.eclipse.persistence.sessions.DatabaseLogin#setUsesNativeSQL(boolean)
     */
    public static final String NATIVE_SQL = "eclipselink.jdbc.native-sql";

    /**
     * The {@code eclipselink.jdbc.sql-cast} property specifies if
     * platform specific CAST SQL operations should be used. Casting is normally
     * not required, and can cause issues when used.
     * <p>
     * <b>Allowed Values</b> (String)<b>:</b>
     * <ul>
     * <li>{@code false} (DEFAULT) - disable platform specific cast
     * <li>{@code true} - enable platform specific cast
     * </ul>
     *
     * @see org.eclipse.persistence.platform.database.DatabasePlatform#setIsCastRequired(boolean)
     */
    public static final String SQL_CAST = "eclipselink.jdbc.sql-cast";

    /**
     * The {@code eclipselink.jpql.parser} property allows the
     * JPQL parser to be configured.
     * <p>
     * <b>Allowed Values</b> (String)<b>:</b>
     * <ul>
     * <li>{@code org.eclipse.persistence.internal.jpa.jpql.HermesParser} (DEFAULT) - new parser
     * as of EclipseLink 2.4, provides extended JPQL support.
     * <li>{@code org.eclipse.persistence.queries.ANTLRQueryBuilder} - old parser used previous
     * to EclipseLink 2.4, can be used for backward compatibility.
     * </ul>
     *
     * @see ParserType
     * @see org.eclipse.persistence.queries.JPAQueryBuilder
     */
    public static final String JPQL_PARSER = "eclipselink.jpql.parser";

    /**
     * The {@code eclipselink.jpql.validation} property allows the
     * JPQL parser validation level to be configured.
     * <p>
     * This setting is only supported in the Hermes parser.
     * <p>
     * <b>Allowed Values</b> (String)<b>:</b>
     * <ul>
     * <li>{@code EclipseLink} (DEFAULT) - allows EclipseLink JPQL extensions.
     * <li>{@code JPA 1.0} - only allows valid JPA 1.0 JPQL.
     * <li>{@code JPA 2.0} - only allows valid JPA 2.0 JPQL.
     * <li>{@code JPA 2.1} - only allows valid JPA 2.1 JPQL.
     * <li>{@code None} - no JPQL validation is done.
     * </ul>
     *
     * @see #JPQL_PARSER
     * @see ParserValidationType
     */
    public static final String JPQL_VALIDATION = "eclipselink.jpql.validation";

    /**
     * The {@code wait} property.<br>
     * This can be appended to any connection pool property,
     * i.e. {@code eclipselink.jdbc.connection_pool.default.wait}
     * which specifies the timeout time in milliseconds (ms) that will be waited
     * for an available connection before an exception is thrown.
     * <p>
     * Ignored in case external connection pools are used.
     * <p>
     * Default: 180000 ms (3 minutes).
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>a string containing a positive integer value. A value of 0 means wait forever.
     * </ul>
     *
     * @see #CONNECTION_POOL
     */
    public static final String CONNECTION_POOL_WAIT = "wait";

    /**
     * The {@code max} property.<br>
     * This can be appended to any connection pool property,
     * i.e. {@code eclipselink.jdbc.connection_pool.default.max}.<br>
     * Specifies the maximum number of read connection in the internal connection pool. If
     * the maximum size is reached, threads requiring a connection will wait
     * until one is released back to the pool. By default, a single shared
     * (exclusive) read/write pool is used with min/max 32 connections and 1
     * initial.
     * <p>
     * Ignored in case external connection pools are used.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>a string containing a positive integer value
     * </ul>
     *
     * @see #CONNECTION_POOL
     */
    public static final String CONNECTION_POOL_MAX = "max";

    /**
     * The {@code min} property.<br>
     * This can be appended to any connection pool property,
     * i.e. {@code eclipselink.jdbc.connection_pool.default.min}.<br>
     * Specifies the minimum number of connections in EclipseLink connection pool.
     * Connections beyond the minimum will be disconnected when returned to the pool,
     * so this should normally be equal to the number of active threads, or server's
     * thread pool size. By default, a single shared (exclusive) read/write pool
     * is used with min/max 32 connections and 1 initial.
     * <p>
     * Ignored in case external connection pools are used.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>a string containing a positive integer value
     * </ul>
     *
     * @see #CONNECTION_POOL
     */
    public static final String CONNECTION_POOL_MIN = "min";

    /**
     * The {@code initial} property.<br>
     * This can be appended to any connection pool property,
     * i.e. {@code eclipselink.jdbc.connection_pool.default.initial}
     * EclipseLink JDBC (internal) connection pools properties. Initial number
     * of connections in EclipseLink connection pool. This is the number of
     * connections connected at startup. By default, a single shared (exclusive)
     * read/write pool is used with min/max 32 connections and 1 initial.
     * <p>
     * Ignored in case external connection pools are used.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>a string containing a positive integer value
     * </ul>
     *
     * @see #CONNECTION_POOL
     */
    public static final String CONNECTION_POOL_INITIAL = "initial";

    /**
     * The {@code shared} property.<br>
     * This can be appended to the read connection pool,
     * i.e. {@code eclipselink.jdbc.connection_pool.read.shared}.<br>
     * Configures whether connections in EclipseLink read connection pool should
     * be shared (not exclusive). Connection sharing means the same JDBC
     * connection will be used concurrently for multiple reading threads.
     * <p>
     * This property is ignored in case external connection pools are used.
     * <p>
     * <b>Allowed Values</b> (String)<b>:</b>
     * <ul>
     * <li>{@code false} (DEFAULT) - indicates read connections will not be shared
     * <li>{@code true} - indicates read connections can be shared
     * </ul>
     *
     * @see #CONNECTION_POOL_READ
     */
    public static final String CONNECTION_POOL_SHARED = "shared";

    /**
     * The {@code url} property.<br>
     * This can be appended to a connection pool property,
     * i.e. {@code eclipselink.jdbc.connection_pool.node1.url}.<br>
     * Configures the JDBC url to use for the connection pool.
     * Only required if different from the default.
     *
     * @see #CONNECTION_POOL
     */
    public static final String CONNECTION_POOL_URL = "url";

    /**
     * The {@code jtaDataSource} property.<br>
     * This can be appended to a connection pool property,
     * i.e. {@code eclipselink.jdbc.connection_pool.node1.jtaDataSource}.<br>
     * Configures the JTA DataSource name to use for the connection pool.
     * Only required if different from the default.
     * @see #CONNECTION_POOL
     */
    public static final String CONNECTION_POOL_JTA_DATA_SOURCE = "jtaDataSource";

    /**
     * The {@code nonJtaDataSource} property.<br>
     * This can be appended to a connection pool property,
     * i.e. {@code eclipselink.jdbc.connection_pool.node1.nonJtaDataSource}.<br>
     * Configures the non JTA DataSource name to use for the connection pool.
     * Only required if different from the default.
     * @see #CONNECTION_POOL
     */
    public static final String CONNECTION_POOL_NON_JTA_DATA_SOURCE = "nonJtaDataSource";

    /**
     * The {@code user} property.<br>
     * This can be appended to a connection pool property,
     * i.e. {@code eclipselink.jdbc.connection_pool.node1.user}.<br>
     * Configures the user name to use for the connection pool.
     * Only required if different from the default.
     * @see #CONNECTION_POOL
     */
    public static final String CONNECTION_POOL_USER = "user";

    /**
     * The {@code password} property.<br>
     * This can be appended to a connection pool property,
     * i.e. {@code eclipselink.jdbc.connection_pool.node1.password}.<br>
     * Configures the password to use for the connection pool.
     * Only required if different from the default.
     * @see #CONNECTION_POOL
     */
    public static final String CONNECTION_POOL_PASSWORD = "password";

    /**
     * The {@code failover} property.<br>
     * This can be appended to a connection pool property,
     * i.e. {@code eclipselink.jdbc.connection_pool.node1.failover}.<br>
     * Configures the connection pool(s) to fail over to if this connection pool fails.
     * A comma separate list is allowed if multiple failover pools are required.
     * @see #CONNECTION_POOL
     */
    public static final String CONNECTION_POOL_FAILOVER = "failover";

    /**
     * Allow configuring a {@code eclipselink.connection-pool.} properties.
     * The name of the connection pool must be appended to configure the pool,
     * if no name is appended the default (write) pool is configured.
     * The name of the property to configure must also be appended.
     * <p>
     * A user defined connection pool can be configured or one of the following system pools:
     * <ul>
     * <li> {@code read} - pool used for non-transactional read queries, (defaults to default pool if not specified).
     * <li> {@code default}, {@code write}, {@code ""} - default pool used for writing and reads if no read pool configured.
     * <li> {@code sequence} - pool used for sequencing, (default pool/write connection used if not specified).
     * </ul>
     * <p>
     * A user defined pool is only used if specified in the EntityManager properties or ClientSession ConnectionPolicy,
     * or if partitioning is used.
     * <p>
     * The following connection pool properties can be configured:
     * <ul>
     * <li> {@code initial} - number of initial connections.
     * <li> {@code min} - minimum number of connections.
     * <li> {@code max} - maximum number of connections.
     * <li> {@code wait} - amount of time to wait for a connection from the pool.
     * <li> {@code url} - JDBC URL for the connection.
     * <li> {@code shared} - only for the read connection pool, shares read connections across threads.
     * <li> {@code jtaDataSource} - JTA DataSource name to use for the connection, if different from the default.
     * <li> {@code nonJtaDataSource} - non JTA DataSource name to use for the connection, if different from the default.
     * <li> {@code user} - user to use for the connection, if different from the default.
     * <li> {@code password} - password to use for the connection, if different from the default.
     * </ul>
     * <p>
     * <b>Persistence XML example:</b>
     * {@snippet lang="XML" :
     *  <property name="eclipselink.connection-pool.node2.min" value="16"/>
     *  <property name="eclipselink.connection-pool.node2.max" value="16"/>
     *  <property name="eclipselink.connection-pool.node2.url" value="jdbc:oracle:thin:@node2:1521:orcl"/>
     * }
     *
     * @see #CONNECTION_POOL_READ
     * @see #CONNECTION_POOL_SEQUENCE
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
     * Allow configuring the {@code eclipselink.connection-pool.read.} properties.
     * The read connection pool is used for non-transaction read queries.
     * By default, a separate read connection pool is not used,
     * and the default pool is used for read queries.
     * <p>
     * One of the following connection pool properties must be appended.
     * <ul>
     * <li> {@code initial} - number of initial connections.
     * <li> {@code min} - minimum number of connections.
     * <li> {@code max} - maximum number of connections.
     * <li> {@code wait} - amount of time to wait for a connection from the pool.
     * <li> {@code url} - JDBC URL for the connection.
     * <li> {@code shared} - only for the read connection pool, shares read connections across threads.
     * <li> {@code jtaDataSource} - JTA DataSource name to use for the connection, if different from the default.
     * <li> {@code nonJtaDataSource} - non JTA DataSource name to use for the connection, if different from the default.
     * <li> {@code user} - user to use for the connection, if different from the default.
     * <li> {@code password} - password to use for the connection, if different from the default.
     * </ul>
     * <p>
     * <b>Persistence XML example:</b>
     * {@snippet lang="XML" :
     *  <property name="eclipselink.connection-pool.read.min" value="8"/>
     *  <property name="eclipselink.connection-pool.read.max" value="32"/>
     *  <property name="eclipselink.connection-pool.read.nonJtaDataSource" value="jdbc/readDataSource"/>
     * }
     *
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
     * @see org.eclipse.persistence.sessions.server.ReadConnectionPool
     */
    public static final String CONNECTION_POOL_READ = "eclipselink.connection-pool.read.";

    /**
     * Allow configuring the {@code eclipselink.connection-pool.sequence.} properties.
     * The sequence connection pool is used to allocate generated Ids.
     * This is only required for TABLE sequencing.
     * <p>
     * By default, a separate sequence connection pool is not used,
     * and the default pool is used for sequencing.
     * <p>
     * One of the following connection pool properties must be appended.
     * <ul>
     * <li> {@code initial} - number of initial connections.
     * <li> {@code min} - minimum number of connections.
     * <li> {@code max} - maximum number of connections.
     * <li> {@code wait} - amount of time to wait for a connection from the pool.
     * <li> {@code url} - JDBC URL for the connection.
     * <li> {@code jtaDataSource} - JTA DataSource name to use for the connection, if different from the default.
     * <li> {@code nonJtaDataSource} - non JTA DataSource name to use for the connection, if different from the default.
     * <li> {@code user} - user to use for the connection, if different from the default.
     * <li> {@code password} - password to use for the connection, if different from the default.
     * </ul>
     * <p>
     * <b>Persistence XML example:</b>
     * {@snippet lang="XML" :
     *  <property name="eclipselink.connection-pool.sequence.min" value="1"/>
     *  <property name="eclipselink.connection-pool.sequence.max" value="1"/>
     *  <property name="eclipselink.connection-pool.sequence.nonJtaDataSource" value="jdbc/sequenceDataSource"/>
     * }
     *
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
     * Tell EclipseLink to use its internal connection pool to pool connections from a datasource.
     * <p>
     * This property is useful when using EclipseLink with Gemini JPA because it internally wraps local
     * database information in a datasource.
     * <p>
     * <b>Allowed Values</b> (String)<b>:</b>
     * <ul>
     * <li>{@code false} (DEFAULT)
     * <li>{@code true}
     * </ul>
     */
    public static final String CONNECTION_POOL_INTERNALLY_POOL_DATASOURCE = "eclipselink.connection-pool.force-internal-pool";

    /**
     * The {@code eclipselink.jdbc.connections.wait-timeout} property
     * which specifies the timeout time in milliseconds (ms) that will be waited
     * for an available connection before an exception is thrown.
     * <p>
     * Ignored in case external connection pools are used.
     * <p>
     * Default: 180000 ms (3 minutes).
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>a string containing a positive integer value. A value of 0 means wait forever.
     * </ul>
     *
     * @see #CONNECTION_POOL_WAIT
     * @deprecated as of EclipseLink 2.2 replaced by {@link PersistenceUnitProperties#CONNECTION_POOL_WAIT}
     */
    @Deprecated
    public static final String JDBC_CONNECTIONS_WAIT = "eclipselink.jdbc.connections.wait-timeout";

    /**
     * The {@code eclipselink.jdbc.connections.max} property specifies
     * the maximum number of read connection in the internal connection pool. If
     * the maximum size is reached, threads requiring a connection will wait
     * until one is released back to the pool. By default, a single shared
     * (exclusive) read/write pool is used with min/max 32 connections and 1
     * initial.
     * <p>
     * Ignored in case external connection pools are used.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>a string containing a positive integer value
     * </ul>
     *
     * @see #CONNECTION_POOL_MAX
     * @deprecated as of EclipseLink 2.2 replaced by {@link PersistenceUnitProperties#CONNECTION_POOL_MAX}
     */
    @Deprecated
    public static final String JDBC_CONNECTIONS_MAX = "eclipselink.jdbc.connections.max";

    /**
     * The {@code eclipselink.jdbc.connections.min} property specifies
     * the minimum number of connections in EclipseLink connection pool.
     * Connections beyond the minimum will be disconnected when returned to the pool,
     * so this should normally be equal to the number of active threads, or server's
     * thread pool size. By default, a single shared (exclusive) read/write pool
     * is used with min/max 32 connections and 1 initial.
     * <p>
     * Ignored in case external connection pools are used.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>a string containing a positive integer value
     * </ul>
     *
     * @see #CONNECTION_POOL_MIN
     * @deprecated as of EclipseLink 2.2 replaced by {@link PersistenceUnitProperties#CONNECTION_POOL_MIN}
     */
    @Deprecated
    public static final String JDBC_CONNECTIONS_MIN = "eclipselink.jdbc.connections.min";

    /**
     * The {@code eclipselink.jdbc.connections.initial} property
     * EclipseLink JDBC (internal) connection pools properties. Initial number
     * of connections in EclipseLink connection pool. This is the number of
     * connections connected at startup. By default, a single shared (exclusive)
     * read/write pool is used with min/max 32 connections and 1 initial.
     * <p>
     * Ignored in case external connection pools are used.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>a string containing a positive integer value
     * </ul>
     *
     * @see #CONNECTION_POOL_INITIAL
     * @deprecated as of EclipseLink 2.2 replaced by {@link PersistenceUnitProperties#CONNECTION_POOL_INITIAL}
     */
    @Deprecated
    public static final String JDBC_CONNECTIONS_INITIAL = "eclipselink.jdbc.connections.initial";

    /**
     * The {@code eclipselink.jdbc.write-connections.max} property
     * specifies the maximum number of connections supported in the internal
     * write connection pool. Maximum number of connections in EclipseLink write
     * connection pool. If the maximum size is reached, threads requiring a
     * connection will wait until one is released back to the pool. By default, a
     * single shared (exclusive) read/write pool is used with min/max 32
     * connections and 1 initial.
     * <p>
     * Ignored in case external connection pools are used.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>a string containing a positive integer value
     * </ul>
     *
     * @see #CONNECTION_POOL_MAX
     * @deprecated as of EclipseLink 2.2 replaced by {@link PersistenceUnitProperties#CONNECTION_POOL_MAX}
     */
    @Deprecated
    public static final String JDBC_WRITE_CONNECTIONS_MAX = "eclipselink.jdbc.write-connections.max";

    /**
     * The {@code eclipselink.jdbc.write-connections.min} property
     * specifies the minimum number of connections in the internal write
     * connection pool. Connections beyond the minimum will be disconnected when
     * returned to the pool, so this should normally be equal to the number of
     * active threads, or server's thread pool size. By default, a single shared
     * (exclusive) read/write pool is used with min/max 32 connections and 1
     * initial.
     * <p>
     * Ignored in case external connection pools are used.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>a string containing a positive integer value
     * </ul>
     *
     * @see #CONNECTION_POOL_MIN
     * @deprecated as of EclipseLink 2.2 replaced by {@link PersistenceUnitProperties#CONNECTION_POOL_MIN}
     */
    @Deprecated
    public static final String JDBC_WRITE_CONNECTIONS_MIN = "eclipselink.jdbc.write-connections.min";

    /**
     * The {@code eclipselink.jdbc.write-connections.initial} property
     * configures the number of connections connected at created at startup in
     * the write connection pool. By default, a single shared (exclusive)
     * read/write pool is used with min/max 32 connections and 1 initial.
     * <p>
     * This property is ignored in case external connection pools are used.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>a string containing a zero or greater integer value
     * </ul>
     *
     * @see #CONNECTION_POOL_INITIAL
     * @deprecated as of EclipseLink 2.2 replaced by {@link PersistenceUnitProperties#CONNECTION_POOL_INITIAL}
     */
    @Deprecated
    public static final String JDBC_WRITE_CONNECTIONS_INITIAL = "eclipselink.jdbc.write-connections.initial";

    /**
     * The {@code eclipselink.jdbc.read-connections.max} property
     * configures the maximum number of connections in the read connection pool.
     * If the maximum size is reached, threads requiring a connection will wait
     * until one is released back to the pool (unless shared). By default, a
     * separate read connection pool is not used. By default, a single shared
     * (exclusive) read/write pool is used with min/max 32 connections and 1
     * initial.
     * <p>
     * This property is ignored in case external connection pools are used.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>a string containing a zero or greater integer value
     * </ul>
     * <p>
     * See:
     * <ul>
     * <li>{@link #JDBC_CONNECTIONS_WAIT} to configure the timeout waiting on a
     *      connection.
     * </ul>
     * @see #JDBC_CONNECTIONS_WAIT
     * @see #CONNECTION_POOL_MAX
     * @deprecated as of EclipseLink 2.2 replaced by {@link PersistenceUnitProperties#CONNECTION_POOL_MAX}
     */
    @Deprecated
    public static final String JDBC_READ_CONNECTIONS_MAX = "eclipselink.jdbc.read-connections.max";

    /**
     * The {@code eclipselink.jdbc.read-connections.min} property
     * configures the minimum number of connections in read connection pool.
     * Connections beyond the minimum will be disconnected when returned to the
     * pool, so this should normally be equal to the number of active threads,
     * or server's thread pool size. By default, a separate read connection pool
     * is not used. By default, a single shared (exclusive) read/write pool is
     * used with min/max 32 connections and 1 initial.
     * <p>
     * This property is ignored in case external connection pools are used.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>a string containing a zero or greater integer value
     * </ul>
     *
     * @see #CONNECTION_POOL_MIN
     * @deprecated as of EclipseLink 2.2 replaced by {@link PersistenceUnitProperties#CONNECTION_POOL_MIN}
     */
    @Deprecated
    public static final String JDBC_READ_CONNECTIONS_MIN = "eclipselink.jdbc.read-connections.min";

    /**
     * The {@code eclipselink.jdbc.read-connections.initial} property
     * configures the number of connections connected at created at startup in
     * the read connection pool. By default, a single shared (exclusive)
     * read/write pool is used with min/max 32 connections and 1 initial.
     * <p>
     * This property is ignored in case external connection pools are used.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>a string containing a zero or greater integer value
     * </ul>
     *
     * @see #CONNECTION_POOL_INITIAL
     * @deprecated as of EclipseLink 2.2 replaced by {@link PersistenceUnitProperties#CONNECTION_POOL_INITIAL}
     */
    @Deprecated
    public static final String JDBC_READ_CONNECTIONS_INITIAL = "eclipselink.jdbc.read-connections.initial";

    /**
     * The {@code eclipselink.jdbc.read-connections.shared} property
     * configures whether connections in EclipseLink read connection pool should
     * be shared (not exclusive). Connection sharing means the same JDBC
     * connection will be used concurrently for multiple reading threads.
     * <p>
     * This property is ignored in case external connection pools are used.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>{@code false} (DEFAULT) - indicates read connections will not be shared
     * <li>{@code true} - indicates read connections can be shared
     * </ul>
     *
     * @see #CONNECTION_POOL_SHARED
     * @deprecated as of EclipseLink 2.2 replaced by {@link PersistenceUnitProperties#CONNECTION_POOL_SHARED}
     */
    @Deprecated
    public static final String JDBC_READ_CONNECTIONS_SHARED = "eclipselink.jdbc.read-connections.shared";

    /**
     * The {@code eclipselink.jdbc.sequence-connection-pool.max} property
     * configures the maximum number of connections in the sequence connection
     * pool. If the maximum size is reached, threads requiring a connection will
     * wait until one is released back to the pool.
     * <p>
     * By default, a separate sequence connection pool is not used (the sequence
     * is allocated on the write connection).
     * <p>
     * This property is ignored in case external connection pools are used.
     * <p>
     * Default: 2
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>a string containing a zero or greater integer value.
     * </ul>
     * <p>
     * See:
     * <ul>
     * <li>{@link #JDBC_SEQUENCE_CONNECTION_POOL} Required to configure the use of a
     *      sequence pool/data-source
     * <li>{@link #JDBC_CONNECTIONS_WAIT} to configure the timeout
     * </ul>
     *
     * @see #JDBC_SEQUENCE_CONNECTION_POOL
     * @see #JDBC_CONNECTIONS_WAIT
     * @see #CONNECTION_POOL_MAX
     * @deprecated as of EclipseLink 2.2 replaced by {@link PersistenceUnitProperties#CONNECTION_POOL_MAX}
     */
    @Deprecated
    public static final String JDBC_SEQUENCE_CONNECTION_POOL_MAX = "eclipselink.jdbc.sequence-connection-pool.max";

    /**
     * The {@code eclipselink.jdbc.sequence-connection-pool.min} property
     * configures the minimum number of connections in sequence connection pool.
     * Connections beyond the minimum will be disconnected when returned to the
     * pool, so this should normally be equal to the maximum to avoid
     * connecting/disconnecting.
     * <p>
     * By default, a separate sequence connection pool is not used (the sequence
     * is allocated on the write connection).
     * <p>
     * This property is ignored in case external connection pools are used.
     * <p>
     * Default: 2
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>a string containing a zero or greater integer value.
     * </ul>
     * <p>
     * See:
     * <ul>
     * <li>{@link #JDBC_SEQUENCE_CONNECTION_POOL} Required to configure the use of a
     *      sequence pool/data-source
     * </ul>
     *
     * @see #JDBC_SEQUENCE_CONNECTION_POOL
     * @see #CONNECTION_POOL_MIN
     * @deprecated as of EclipseLink 2.2 replaced by {@link PersistenceUnitProperties#CONNECTION_POOL_MIN}
     */
    @Deprecated
    public static final String JDBC_SEQUENCE_CONNECTION_POOL_MIN = "eclipselink.jdbc.sequence-connection-pool.min";

    /**
     * The {@code eclipselink.jdbc.sequence-connection-pool.initial} property configures the initial number of connections in
     * sequence connection pool. This is the number of connections connected at
     * startup.
     * <p>
     * By default, a separate sequence connection pool is not used (the sequence
     * is allocated on the write connection).
     * <p>
     * This property is ignored in case external connection pools are used.
     * <p>
     * Default: 2
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>a string containing a zero or greater integer value.
     * </ul>
     * <p>
     * See:
     * <ul>
     * <li>{@link #JDBC_SEQUENCE_CONNECTION_POOL} Required to configure the use of a
     *      sequence pool/data-source
     * </ul>
     *
     * @see #JDBC_SEQUENCE_CONNECTION_POOL
     * @see #CONNECTION_POOL_INITIAL
     * @deprecated as of EclipseLink 2.2 replaced by {@link PersistenceUnitProperties#CONNECTION_POOL_INITIAL}
     */
    @Deprecated
    public static final String JDBC_SEQUENCE_CONNECTION_POOL_INITIAL = "eclipselink.jdbc.sequence-connection-pool.initial";

    /**
     * The {@code eclipselink.jdbc.sequence-connection-pool} property
     * configures a separate connection pool used for sequencing to
     * retrieve new value(s). This improves sequence allocation by allocating
     * sequencing outside the current transaction. This can be used with
     * internal or external (DataSource) connection pooling, external must
     * provide a non-jta-datasource resource name using
     * {@link #JDBC_SEQUENCE_CONNECTION_POOL_DATASOURCE}. A sequence connection
     * pool is generally only used with using TABLE sequencing to minimize
     * contention on the sequence table(s).
     * <p>
     * <b>Allowed Values</b> (case-insensitive)<b>:</b>
     * <ul>
     * <li>{@code false} (DEFAULT)
     * <li>{@code true}
     * </ul>
     * <p>
     * See:
     * <ul>
     * <li>{@link #JDBC_SEQUENCE_CONNECTION_POOL_DATASOURCE} to configure the use of a
     *      non-JTA data source for sequence allocation call.
     * <li>{@link #JDBC_SEQUENCE_CONNECTION_POOL_INITIAL} to configure the initial
     *      connections size for internal connection pooling
     * <li>{@link #JDBC_SEQUENCE_CONNECTION_POOL_MIN} to configure the minimum
     *      connections size for internal connection pooling
     * <li>{@link #JDBC_SEQUENCE_CONNECTION_POOL_MAX} to configure the maximum
     *      connections size for internal connection pooling
     * </ul>
     *
     * @see #JDBC_SEQUENCE_CONNECTION_POOL_DATASOURCE
     * @see #JDBC_SEQUENCE_CONNECTION_POOL_INITIAL
     * @see #JDBC_SEQUENCE_CONNECTION_POOL_MIN
     * @see #JDBC_SEQUENCE_CONNECTION_POOL_MAX
     * @see #CONNECTION_POOL_SEQUENCE
     * @deprecated as of EclipseLink 2.2 replaced by {@link PersistenceUnitProperties#CONNECTION_POOL_SEQUENCE}
     */
    @Deprecated
    public static final String JDBC_SEQUENCE_CONNECTION_POOL = "eclipselink.jdbc.sequence-connection-pool";

    /**
     * The {@code eclipselink.jdbc.sequence-connection-pool.non-jta-data-source} property configures the name of the non-JTA data source that
     * will be used for sequencing calls.
     *
     * @see #JDBC_SEQUENCE_CONNECTION_POOL
     * @see #CONNECTION_POOL_NON_JTA_DATA_SOURCE
     * @deprecated as of EclipseLink 2.2 replaced by {@link PersistenceUnitProperties#CONNECTION_POOL_NON_JTA_DATA_SOURCE}
     */
    @Deprecated
    public static final String JDBC_SEQUENCE_CONNECTION_POOL_DATASOURCE = "eclipselink.jdbc.sequence-connection-pool.non-jta-data-source";

    /**
     * The {@code eclipselink.partitioning} property specifies the default {@link org.eclipse.persistence.descriptors.partitioning.PartitioningPolicy} for a persistence unit.<br>
     * A PartitioningPolicy is used to partition the data for a class across multiple difference databases
     * or across a database cluster such as Oracle RAC.<br>
     * Partitioning can provide improved scalability by allowing multiple database machines to service requests.
     * <p>
     * If multiple partitions are used to process a single transaction, JTA should be used for proper XA transaction support.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>the name of an existing defined {@link org.eclipse.persistence.descriptors.partitioning.PartitioningPolicy}
     * </ul>
     *
     * @see org.eclipse.persistence.annotations.Partitioning
     * @see org.eclipse.persistence.descriptors.partitioning.PartitioningPolicy
     */
    public static final String PARTITIONING = "eclipselink.partitioning";

    /**
     * The {@code eclipselink.partitioning.callback} is used to integrate with an external
     * DataSource's data affinity support, such as UCP.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>the fully qualified name for a class that implements {@link org.eclipse.persistence.platform.database.partitioning.DataPartitioningCallback} interface
     * i.e. {@code org.eclipse.persistence.platform.database.oracle.ucp.UCPDataPartitioningCallback}
     * </ul>
     *
     * @see org.eclipse.persistence.platform.database.partitioning.DataPartitioningCallback
     * @see org.eclipse.persistence.annotations.Partitioning
     * @see org.eclipse.persistence.descriptors.partitioning.PartitioningPolicy
     */
    public static final String PARTITIONING_CALLBACK = "eclipselink.partitioning.callback";

    /**
     * Property {@code eclipselink.jdbc.bind-parameters} configures whether parameter binding
     * should be used in the creation of JDBC prepared statements.
     * <p>
     * Usage of parameter binding is generally a performance optimization;
     * allowing for SQL and prepared statement caching, as well as usage of batch writing.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>{@code false} - all values will be written literally into the generated SQL
     * <li>{@code true} (DEFAULT) - all values will be bound as parameters in the generated SQL
     * </ul>
     */
    public static final String JDBC_BIND_PARAMETERS = "eclipselink.jdbc.bind-parameters";

    /**
     * Property {@code eclipselink.jdbc.allow-partial-bind-parameters} configures whether
     * partial parameter binding should be allowed in the creation of JDBC prepared statements.
     * <p>
     * EclipseLink determines binding behavior based on the database platform's support for binding.
     * If the database platform doesn't support binding for a specific expression, EclipseLink disables
     * all binding for the whole query. Setting this property to 'true' will allow EclipseLink to bind
     * per expression, instead of per query.
     * <p>
     * Usage of parameter binding is generally a performance optimization;
     * allowing for SQL and prepared statement caching, as well as usage of batch writing.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>{@code true} - EclipseLink binds parameters per SQL function/expression. Requires Platform support if enabled.
     * <li>{@code false} (DEFAULT) - EclipseLink either binds all parameters or no parameters; depending on database support
     * </ul>
     */
    public static final String JDBC_ALLOW_PARTIAL_PARAMETERS = "eclipselink.jdbc.allow-partial-bind-parameters";

    /**
     * Property {@code eclipselink.jdbc.force-bind-parameters} enables parameter binding
     * in the creation of JDBC prepared statements. Some database platforms disable parameter binding
     * on certain functions and relations. This property allows the user to force parameter binding
     * to be enabled regardless.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>{@code false} (DEFAULT) - values will default to platform specific logic
     * <li>{@code true} - bindings will use platform default
     * </ul>
     *
     * @see #JDBC_BIND_PARAMETERS
     */
    public static final String JDBC_FORCE_BIND_PARAMETERS = "eclipselink.jdbc.force-bind-parameters";

    /**
     * The {@code eclipselink.jdbc.exclusive-connection.mode} property
     * specifies when reads are performed through the write connection.<br>
     * You can set this property while creating either an EntityManagerFactory (either
     * in the map passed to the createEntityManagerFactory method, or in the
     * persistence.xml file), or an EntityManager (in the map passed to the
     * createEntityManager method). Note that the latter overrides the former.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>{@code Transactional} (DEFAULT) - {@link ExclusiveConnectionMode#Transactional}
     * <li>{@code Isolated} - {@link ExclusiveConnectionMode#Isolated}
     * <li>{@code Always} - {@link ExclusiveConnectionMode#Always}
     * </ul>
     *
     * @see ExclusiveConnectionMode
     */
    public static final String EXCLUSIVE_CONNECTION_MODE = "eclipselink.jdbc.exclusive-connection.mode";

    /**
     * The {@code eclipselink.jdbc.exclusive-connection.is-lazy} property
     * specifies when write connection is acquired lazily.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>{@code false}
     * <li>{@code true} (DEFAULT)
     * </ul>
     */
    public static final String EXCLUSIVE_CONNECTION_IS_LAZY = "eclipselink.jdbc.exclusive-connection.is-lazy";

    /**
     * The {@code eclipselink.jdbc.cache-statements.size} property
     * specifies the number of statements held when using internal statement
     * caching.
     * <p>
     * Default: 50
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>a string containing a zero or greater integer value.
     * </ul>
     */
    public static final String CACHE_STATEMENTS_SIZE = "eclipselink.jdbc.cache-statements.size";

    /**
     * The {@code eclipselink.jdbc.cache-statements} property specifies
     * whether JDBC statements should be cached. This is recommended when using
     * EclipseLink's internal connection pooling.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>{@code false} (DEFAULT) - disable internal statement caching.
     * <li>{@code true} - enable internal statement caching.
     * </ul>
     * <p>
     * <b>Persistence XML example:</b>
     * {@snippet lang="XML" :
     *  <property name="eclipselink.jdbc.cache-statements" value="false"/>
     * }
     * <p>
     * <b>Java example:</b>
     * {@snippet :
     *  props.put(PersistenceUnitProperties.CACHE_STATEMENTS, "false");
     * }
     */
    public static final String CACHE_STATEMENTS = "eclipselink.jdbc.cache-statements";

    // Bean Validation properties

    /**
     * The {@code jakarta.persistence.validation.factory} property
     * specifies an instance of <a href =
     * http://docs.oracle.com/javaee/6/api/jakarta.validation/ValidatorFactory.html>jakarta.validation.ValidatorFactory</a> used by
     * EclipseLink to perform Automatic Validation upon Lifecycle Events. If the
     * property is not specified, and if Bean Validation API is visible to
     * EclipseLink, it will try to instantiate an instance of
     * {@code jakarta.validation.ValidationFactory} by calling
     * {@code Validation.buildDefaultValidatorFactory()}.
     */
    public static final String VALIDATOR_FACTORY = "jakarta.persistence.validation.factory";

    /**
     * The {@code jakarta.persistence.validation.mode} property specifies
     * whether the automatic lifecycle event validation is in effect.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>{@code AUTO}
     * <li>{@code CALLBACK}
     * <li>{@code NONE}
     * </ul>
     */
    public static final String VALIDATION_MODE = "jakarta.persistence.validation.mode";

    /**
     * The {@code jakarta.persistence.validation.group.pre-persist}
     * property specifies the name of the validator groups to execute for
     * {@code preUpdate} event. The value should be a string with fully qualified
     * classnames separated by a comma (','). If this value is not specified in
     * both persistence.xml or using this property, the default Bean Validation
     * group (the group Default) will be validated
     */
    public static final String VALIDATION_GROUP_PRE_PERSIST = "jakarta.persistence.validation.group.pre-persist";

    /**
     * The {@code jakarta.persistence.validation.group.pre-update} property
     * specifies the name of the validator groups to execute for {@code preUpdate}
     * event. The value should be a string with fully qualified classnames
     * separated by a comma (','). If this value is not specified in both
     * persistence.xml or using this property, the default Bean Validation group
     * (the group Default) will be validated
     */
    public static final String VALIDATION_GROUP_PRE_UPDATE = "jakarta.persistence.validation.group.pre-update";

    /**
     * The {@code jakarta.persistence.validation.group.pre-remove} property
     * specifies the name of the validator groups to execute for {@code preRemove}
     * event. The value should be a string with fully qualified classnames
     * separated by a comma (','). If this value is not specified in both
     * persistence.xml or using this property, no validation will occur on
     * remove.
     */
    public static final String VALIDATION_GROUP_PRE_REMOVE = "jakarta.persistence.validation.group.pre-remove";

    /**
     * Property for disabling Bean Validation optimisations.
     * Bean Validation features optimisations, which are used to skip BV processes on non-constrained objects.
     * <p>
     * This is to make maintenance easier and to allow for debugging in case that some object is not validated,
     * but should be.
     * <p>
     * Usage: set to {@link Boolean#TRUE} to disable optimisations, set to {@link Boolean#FALSE} to re-enable them
     * again.
     */
    public static final String BEAN_VALIDATION_NO_OPTIMISATION = "eclipselink.beanvalidation.no-optimisation";

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
     * Property prefix {@code eclipselink.cache.size.} is used to specify the cache size
     * for a specific entity type. The prefix must be followed by a valid entity type name.
     * <p>
     * Property names formed out of these prefixes by appending either entity
     * name, or class name (indicating that the property values applies only to
     * a particular entity) or {@link #DEFAULT} suffix (indicating that the property
     * value applies to all entities).
     * <p>
     * For most cache types, the size is only
     * the initial size, not a fixed or maximum size. For CacheType.SoftCache
     * and CacheType.HardCache the size is the sub-cache size. The default cache
     * size is 100.
     *
     * @see #CACHE_SIZE_DEFAULT
     */
    public static final String CACHE_SIZE_ = "eclipselink.cache.size.";

    /**
     * Property prefix {@code eclipselink.cache.type.} sets the type of cache
     * for a specific entity type. The prefix must be followed by a valid entity type name.
     * <p>
     * Property names formed out of these prefixes by appending either entity
     * name, or class name (indicating that the property values applies only to
     * a particular entity) or {@link #DEFAULT} suffix (indicating that the property
     * value applies to all entities).
     * <p>
     * All valid values for CACHE_TYPE_ properties are declared in {@link CacheType}
     * class. The default cache type is {@link CacheType#SoftWeak}.
     * <p>
     * If you do not wish to cache entities at all, then set {@link PersistenceUnitProperties#CACHE_SHARED_}
     * to {@code false}.
     *
     * @see #CACHE_SHARED_
     * @see CacheType
     * @see #CACHE_TYPE_DEFAULT
     */
    public static final String CACHE_TYPE_ = "eclipselink.cache.type.";

    /**
     * Property prefix {@code eclipselink.cache.shared.} indicates whether entity's cache
     * should be shared (non-isolated) for a specific entity type. The prefix must be followed
     * by a valid entity type name.
     * <p>
     * Property names formed out of these prefixes by appending either entity
     * name, or class name (indicating that the property values applies only to
     * a particular entity) or {@link #DEFAULT} suffix (indicating that the property
     * value applies to all entities).
     * <p>
     * If you do not wish to cache your entities, set this to {@code false}.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>{@code false}
     * <li>{@code true} (DEFAULT)
     * </ul>
     *
     * @see #CACHE_SHARED_DEFAULT
     */
    public static final String CACHE_SHARED_ = "eclipselink.cache.shared.";

    /**
     * The {@code jakarta.persistence.sharedCache.mode} property determines whether
     * second-level caching is in effect for the persistence unit. This property overrides the value
     * specified by the &lt;shared-cache-mode&gt; element in the persistence.xml.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>{@code ALL}
     * <li>{@code NONE}
     * <li>{@code ENABLE_SELECTIVE}
     * <li>{@code DISABLE_SELECTIVE}
     * <li>{@code UNSPECIFIED}
     * </ul>
     *
     * @see jakarta.persistence.SharedCacheMode
     */
    public static final String SHARED_CACHE_MODE = "jakarta.persistence.sharedCache.mode";

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
     * entities at all, then set {@link PersistenceUnitProperties#CACHE_SHARED_DEFAULT}.
     *
     * @see #CACHE_SHARED_DEFAULT
     */
    public static final String CACHE_TYPE_DEFAULT = CACHE_TYPE_ + DEFAULT;

    /**
     * The {@code eclipselink.cache.extended.logging} property control (enable/disable)
     * usage logging of JPA L2 cache. In case of {@code true} EclipseLink generates messages into log output
     * about cache hit/miss new object population and object removal or invalidation.
     * This kind of messages will be displayed only if logging level (property {@code eclipselink.logging.level})
     * is {@code FINEST}
     * It displays Entity class, ID and thread info (ID, Name).
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>{@code false} (DEFAULT)
     * <li>{@code true}
     * </ul>
     */
    public static final String CACHE_EXTENDED_LOGGING = "eclipselink.cache.extended.logging";

    /**
     * The {@code eclipselink.thread.extended.logging} property control (enable/disable)
     * some additional logging messages like print error message if cached Entity is picked by different thread,
     * or if EntityManager/UnitOfWork is reused/passed to different thread.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>{@code false} (DEFAULT)
     * <li>{@code true}
     * </ul>
     */
    public static final String THREAD_EXTENDED_LOGGING = "eclipselink.thread.extended.logging";

    /**
     * The {@code eclipselink.thread.extended.logging.threaddump} property control (enable/disable)
     * store and display thread dump. This is extension to {@code eclipselink.thread.extended.logging} which
     * must be enabled. It prints additionally to some log messages presented by {@code eclipselink.thread.extended.logging}
     * creation and current thread stack traces.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>{@code false} (DEFAULT)
     * <li>{@code true}
     * </ul>
     */
    public static final String THREAD_EXTENDED_LOGGING_THREADDUMP = "eclipselink.thread.extended.logging.threaddump";

    /*
     * NOTE: The Canonical Model properties should be kept in sync with those
     * in org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProperties.
     */

    /**
     * The {@code eclipselink.canonicalmodel.prefix} optional property specifies the prefix
     * that will be added to the start of the class name of any canonical model class generated.
     * By default, the prefix is not used.
     *
     * @see #CANONICAL_MODEL_PREFIX_DEFAULT
     */
    public static final String CANONICAL_MODEL_PREFIX = "eclipselink.canonicalmodel.prefix";

    /**
     * Default prefix that will be added to the start of the class name of any canonical model
     * class generated.
     *
     * @see #CANONICAL_MODEL_PREFIX
     */
    public static String CANONICAL_MODEL_PREFIX_DEFAULT = "";

    /**
     * The {@code eclipselink.canonicalmodel.suffix} optional property specifies the suffix
     * that will be added to the end of the class name of any canonical model class generated.
     * The suffix defaults to "_" unless a prefix is specified. If this property is
     * specified, the value must be a non-empty string that contains valid
     * characters for use in a Java class name.
     *
     * @see #CANONICAL_MODEL_SUFFIX_DEFAULT
     */
    public static final String CANONICAL_MODEL_SUFFIX = "eclipselink.canonicalmodel.suffix";

    /**
     * Default suffix that will be added to the end of the class name of any canonical model class
     * generated.
     *
     * @see #CANONICAL_MODEL_SUFFIX
     */
    public static String CANONICAL_MODEL_SUFFIX_DEFAULT = "_";

    /**
     * The {@code eclipselink.canonicalmodel.subpackage} optional property specifies
     * a sub-package name that can be used to have the canonical model generator generate its classes
     * in a sub-package of the package where the corresponding entity class is located. By
     * default, the canonical model classes are generated into the same package as the entity classes.
     *
     * @see #CANONICAL_MODEL_SUB_PACKAGE_DEFAULT
     */
    public static final String CANONICAL_MODEL_SUB_PACKAGE = "eclipselink.canonicalmodel.subpackage";

    /**
     * Default sub-package name that is used to have the canonical model generator generate its classes
     * in a sub-package of the package where the corresponding entity class is located.
     *
     * @see #CANONICAL_MODEL_SUB_PACKAGE
     */
    public static String CANONICAL_MODEL_SUB_PACKAGE_DEFAULT = "";

    /**
     * The {@code eclipselink.canonicalmodel.load_xml} optional property can be used a performance
     * enhancement between compile rounds. It is used to avoid reloading XML metadata on each
     * compile which may only contain a single class etc. The default value
     * is true and should be left as such for the initial generation to capture
     * the XML metadata. Afterwards users may choose to set this flag if no
     * changes to XML are expected thereafter.
     *
     * @see #CANONICAL_MODEL_LOAD_XML_DEFAULT
     */
    public static final String CANONICAL_MODEL_LOAD_XML = "eclipselink.canonicalmodel.load_xml";

    /**
     * Default value for the {@code eclipselink.canonicalmodel.load_xml} optional property.
     *
     * @see #CANONICAL_MODEL_LOAD_XML
     */
    public static final String CANONICAL_MODEL_LOAD_XML_DEFAULT = "true";

    /**
     * The {@code eclipselink.canonicalmodel.use_static_factory} optional property can be used
     * a performance enhancement between compile rounds within an IDE. It is used to avoid using
     * a static metadata factory between 'cache' metadata from incremental builds. Turning this
     * off in some use cases (IDE) could result in a loss of functionality.
     * The default value is true and should be left as such for full feature support.
     *
     * @see #CANONICAL_MODEL_USE_STATIC_FACTORY_DEFAULT
     */
    public static final String CANONICAL_MODEL_USE_STATIC_FACTORY = "eclipselink.canonicalmodel.use_static_factory";

    /**
     * Default value for the {@code eclipselink.canonicalmodel.use_static_factory} optional
     * property.
     *
     * @see #CANONICAL_MODEL_USE_STATIC_FACTORY
     */
    public static final String CANONICAL_MODEL_USE_STATIC_FACTORY_DEFAULT = "true";

    /**
     * The {@code eclipselink.canonicalmodel.use_generated} optional property can be used
     * to disable generation of {@link jakarta.annotation.Generated} annotation.
     * The default value is true.
     * If the value is {@code false}, {@linkplain #CANONICAL_MODEL_GENERATE_TIMESTAMP}
     * and {@linkplain #CANONICAL_MODEL_GENERATE_COMMENTS} properties are ignored.
     *
     * @see #CANONICAL_MODEL_GENERATE_GENERATED_DEFAULT
     */
    public static final String CANONICAL_MODEL_GENERATE_GENERATED = "eclipselink.canonicalmodel.use_generated";

    /**
     * Default value for the {@code eclipselink.canonicalmodel.use_generated} optional
     * property.
     *
     * @see #CANONICAL_MODEL_GENERATE_GENERATED
     */
    public static final String CANONICAL_MODEL_GENERATE_GENERATED_DEFAULT = "true";

    /**
     * The {@code eclipselink.canonicalmodel.generate_timestamp} optional property can be used
     * to disable usage of date in declaration of {@link jakarta.annotation.Generated} annotation.
     * The default value is true.
     *
     * @see #CANONICAL_MODEL_GENERATE_TIMESTAMP_DEFAULT
     */
    public static final String CANONICAL_MODEL_GENERATE_TIMESTAMP = "eclipselink.canonicalmodel.generate_timestamp";

    /**
     * Default value for the {@code eclipselink.canonicalmodel.generate_timestamp} optional
     * property.
     *
     * @see #CANONICAL_MODEL_GENERATE_TIMESTAMP
     */
    public static final String CANONICAL_MODEL_GENERATE_TIMESTAMP_DEFAULT = "true";

    /**
     * The {@code eclipselink.canonicalmodel.generate_comments} optional property can be used
     * to disable usage of comments in declaration of {@code Generated} annotation.
     * The default value is true.
     *
     * @see #CANONICAL_MODEL_GENERATE_COMMENTS_DEFAULT
     */
    public static final String CANONICAL_MODEL_GENERATE_COMMENTS = "eclipselink.canonicalmodel.generate_comments";

    /**
     * Default value for the {@code eclipselink.canonicalmodel.generate_comments} optional property.
     *
     * @see #CANONICAL_MODEL_GENERATE_COMMENTS
     */
    public static final String CANONICAL_MODEL_GENERATE_COMMENTS_DEFAULT = "true";

    /**
     * Default caching properties - apply to all entities. May be overridden by
     * individual entity property with the same prefix. If you do not wish to
     * cache your entities, set this to {@code false}.
     */
    public static final String CACHE_SHARED_DEFAULT = CACHE_SHARED_ + DEFAULT;

    /**
     * Property prefix {@code eclipselink.cache.query-results} used to
     * configure the default option for query results caching.
     * <p>
     * The query results cache is separate from the object cache.
     * It caches the results of named query execution.
     * The query results cache is not enabled by default, and
     * can be enabled per query.
     * This option allows it to be enabled for all named queries.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>{@code false} (DEFAULT)
     * <li>{@code true}
     * </ul>
     */
    public static final String QUERY_CACHE = "eclipselink.cache.query-results";

    /**
     * The {@code eclipselink.cache.database-event-listener} property allows integration
     * with a database event notification service.
     * This allows the EclipseLink cache to be invalidated by database change events.
     * This is used to support Oracle QCN/DCN (Database Change event Notification),
     * but could also be used by triggers or other services, or other types of events.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>the fully qualified name for a class that implements {@link org.eclipse.persistence.platform.database.events.DatabaseEventListener} interface,
     * for example {@code org.eclipse.persistence.platform.database.oracle.dcn.OracleChangeNotificationListener}
     * <li>{@code DCN} - for Oracle only
     * <li>{@code QCN} - for Oracle only
     * </ul>
     *
     * @see org.eclipse.persistence.platform.database.events.DatabaseEventListener
     * @see "org.eclipse.persistence.platform.database.oracle.dcn.OracleChangeNotificationListener"
     */
    public static final String DATABASE_EVENT_LISTENER = "eclipselink.cache.database-event-listener";

    /**
     * The {@code eclipselink.cache.query-force-deferred-locks} property force all queries and relationships
     * to use deferred lock strategy during object building and L2 cache population.
     * <p>
     * <b>Allowed Values</b> (String)<b>:</b>
     * <ul>
     * <li>{@code false} (DEFAULT) - use use mixed object cache locking strategy
     * <li>{@code true} - use deferred locking strategy all queries and relationships
     * </ul>
     */
    public static final String CACHE_QUERY_FORCE_DEFERRED_LOCKS = "eclipselink.cache.query-force-deferred-locks";

    // Customizations properties

    // Logging properties

    /**
     * The {@code eclipselink.logging.logger} property specifies the type of logger.
     * <p>
     * Default: {@link org.eclipse.persistence.logging.DefaultSessionLog}
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>the fully qualified name for a class that implements {@link org.eclipse.persistence.logging.SessionLog} interface
     * <li>one of values defined in {@link LoggerType}
     * </ul>
     *
     * @see org.eclipse.persistence.logging.DefaultSessionLog
     * @see LoggerType
     * @see org.eclipse.persistence.logging.SessionLog
     */
    public static final String LOGGING_LOGGER = "eclipselink.logging.logger";

    /**
     * The {@code eclipselink.logging.level} property allows the default logging levels to be specified.
     *
     * <table>
     * <caption>Logging Levels:</caption>
     * <tr><td>{@link org.eclipse.persistence.logging.SessionLog#ALL_LABEL}</td><td>&nbsp;</td><td>ALL</td></tr>
     * <tr><td>{@link org.eclipse.persistence.logging.SessionLog#FINEST_LABEL}</td><td>&nbsp;</td><td>FINEST</td></tr>
     * <tr><td>{@link org.eclipse.persistence.logging.SessionLog#FINER_LABEL}</td><td>&nbsp;</td><td>FINER</td></tr>
     * <tr><td>{@link org.eclipse.persistence.logging.SessionLog#FINE_LABEL}</td><td>&nbsp;</td><td>FINE</td></tr>
     * <tr><td>{@link org.eclipse.persistence.logging.SessionLog#CONFIG_LABEL}</td><td>&nbsp;</td><td>CONFIG</td></tr>
     * <tr><td>{@link org.eclipse.persistence.logging.SessionLog#INFO_LABEL}</td><td>&nbsp;</td><td>INFO (DEFAULT)</td></tr>
     * <tr><td>{@link org.eclipse.persistence.logging.SessionLog#WARNING_LABEL}</td><td>&nbsp;</td><td>WARNING</td></tr>
     * <tr><td>{@link org.eclipse.persistence.logging.SessionLog#SEVERE_LABEL}</td><td>&nbsp;</td><td>SEVERE</td></tr>
     * <tr><td>{@link org.eclipse.persistence.logging.SessionLog#OFF_LABEL}</td><td>&nbsp;</td><td>OFF</td></tr>
     * </table>
     * <p>
     * <b>Persistence XML example:</b>
     * {@snippet lang="XML" :
     *  <property name="eclipselink.logging.level" value="FINE" />
     * }
     * <p>
     * <b>Java example:</b>
     * {@snippet :
     *  props.put(PersistenceUnitProperties.LOGGING_LEVEL, org.eclipse.persistence.logging.SessionLog.FINE_LABEL);
     * }
     *
     * @see org.eclipse.persistence.logging.SessionLog
     * @see #CATEGORY_LOGGING_LEVEL_
     */
    public static final String LOGGING_LEVEL = "eclipselink.logging.level";

    /**
     * Property prefix {@code eclipselink.logging.level.} allows the category specific logging levels
     * to be specified.
     *
     * <table>
     * <caption>Logger categories:</caption>
     * <tr><td>{@link org.eclipse.persistence.logging.SessionLog#CACHE}</td><td>&nbsp;</td><td>cache</td></tr>
     * <tr><td>{@link org.eclipse.persistence.logging.SessionLog#CONNECTION}</td><td>&nbsp;</td><td>connection</td></tr>
     * <tr><td>{@link org.eclipse.persistence.logging.SessionLog#DMS}</td><td>&nbsp;</td><td>dms</td></tr>
     * <tr><td>{@link org.eclipse.persistence.logging.SessionLog#EJB}</td><td>&nbsp;</td><td>ejb</td></tr>
     * <tr><td>{@link org.eclipse.persistence.logging.SessionLog#EVENT}</td><td>&nbsp;</td><td>event</td></tr>
     * <tr><td>{@link org.eclipse.persistence.logging.SessionLog#JPA}</td><td>&nbsp;</td><td>jpa</td></tr>
     * <tr><td>{@link org.eclipse.persistence.logging.SessionLog#METAMODEL}</td><td>&nbsp;</td><td>metamodel</td></tr>
     * <tr><td>{@link org.eclipse.persistence.logging.SessionLog#PROPAGATION}</td><td>&nbsp;</td><td>propagation</td></tr>
     * <tr><td>{@link org.eclipse.persistence.logging.SessionLog#PROPERTIES}</td><td>&nbsp;</td><td>properties</td></tr>
     * <tr><td>{@link org.eclipse.persistence.logging.SessionLog#QUERY}</td><td>&nbsp;</td><td>query</td></tr>
     * <tr><td>{@link org.eclipse.persistence.logging.SessionLog#SEQUENCING}</td><td>&nbsp;</td><td>sequencing</td></tr>
     * <tr><td>{@link org.eclipse.persistence.logging.SessionLog#SERVER}</td><td>&nbsp;</td><td>server</td></tr>
     * <tr><td>{@link org.eclipse.persistence.logging.SessionLog#SQL}</td><td>&nbsp;</td><td>sql</td></tr>
     * <tr><td>{@link org.eclipse.persistence.logging.SessionLog#TRANSACTION}</td><td>&nbsp;</td><td>transaction</td></tr>
     * <tr><td>{@link org.eclipse.persistence.logging.SessionLog#WEAVER}</td><td>&nbsp;</td><td>weaver</td></tr>
     * </table>
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>names of levels defined in {@code java.util.logging.Level}, default value is {@code INFO}.
     * </ul>
     * <p>
     * <b>Persistence XML example:</b>
     * {@snippet lang="XML" :
     *  <property name="eclipselink.logging.level.ejb_or_metadata" value="WARNING"/>
     * }
     * <p>
     * <b>Java example:</b>
     * {@snippet :
     *  props.put(PersistenceUnitProperties.CATEGORY_LOGGING_LEVEL_ + SessionLog.EJB_OR_METADATA, SessionLog.WARNING_LABEL);
     * }
     *
     * @see org.eclipse.persistence.logging.SessionLog
     */
    public static final String CATEGORY_LOGGING_LEVEL_ = LOGGING_LEVEL + ".";

    /**
     * By default, sql bind parameters are displayed in exceptions and logs
     * when the log level is FINE or greater. To override this behavior you
     * may set this property to specify that the data should or should not be
     * visible.<br>
     * Note: this property applies only to bind parameters. When not
     * using binding, the parameters are always displayed.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>{@code false}
     * <li>{@code true}
     * </ul>
     *
     * @see #JDBC_BIND_PARAMETERS
     */
    public static final String LOGGING_PARAMETERS = "eclipselink.logging.parameters";

    /**
     * The {@code eclipselink.logging.timestamp} property configures if
     * the current time stamp should be included in each log message.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>{@code false}
     * <li>{@code true} (DEFAULT)
     * </ul>
     */
    public static final String LOGGING_TIMESTAMP = "eclipselink.logging.timestamp";

    /**
     * Property {@code eclipselink.logging.thread} indicates if current
     * thread should have its identity included in each log message.
     * <p>
     * By default, ({@code true}) the thread is logged at FINE or less level. This can
     * be turned off ({@code false}) or on ({@code true}).
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>{@code false}
     * <li>{@code true} (DEFAULT)
     * </ul>
     */
    public static final String LOGGING_THREAD = "eclipselink.logging.thread";

    /**
     * Property {@code eclipselink.logging.session} indicates if the
     * session in use should have its identity included in each log message.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>{@code false}
     * <li>{@code true} (DEFAULT)
     * </ul>
     */
    public static final String LOGGING_SESSION = "eclipselink.logging.session";

    /**
     * Property {@code eclipselink.logging.connection} indicates if the
     * connection in use should have its identity included in each log message.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>{@code false}
     * <li>{@code true} (DEFAULT)
     * </ul>
     */
    public static final String LOGGING_CONNECTION = "eclipselink.logging.connection";

    /**
     * Property {@code eclipselink.logging.exceptions} indicates if exception stack traces
     * should be included in each log message.<br>
     * By default, stack trace is logged for SEVERE all the time and at FINER level for WARNING or less.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>{@code false}
     * <li>{@code true} (DEFAULT)
     * </ul>
     */
    public static final String LOGGING_EXCEPTIONS = "eclipselink.logging.exceptions";

    /**
     * The {@code eclipselink.logging.file} property configures a file
     * location where the log will be output to instead of standard out.
     */
    public static final String LOGGING_FILE = "eclipselink.logging.file";

    // Multitenancy properties

    /**
     * The {@code eclipselink.tenant-id} property specifies the
     * default context property used to populate multitenant entities.
     * <p>
     * NOTE: This is merely a default multitenant property than can be used on
     * its own or with other properties defined by the user. Users are not
     * obligated to use this property and are free to specify their own.
     * <p>
     * <b>Persistence XML example:</b>
     * {@snippet lang="XML" :
     *  <property name="eclipselink.tenant-id" value="Oracle" />
     * }
     * <p>
     * <b>Java example:</b>
     * {@snippet :
     *  props.put(PersistenceUnitProperties.MULTITENANT_PROPERTY_DEFAULT, "Oracle");
     * }
     *
     * @see org.eclipse.persistence.annotations.Multitenant
     * @see org.eclipse.persistence.annotations.TenantDiscriminatorColumn
     */
    public static final String MULTITENANT_PROPERTY_DEFAULT = "eclipselink.tenant-id";

    /**
     * The {@code eclipselink.tenant-schema-id} property specifies the
     * context property used to distinguish tenants when using global schema per tenant
     * multitenant strategy. It is expected to be set by user when creating an {@code EntityManager}.
     * <p>
     * <b>Persistence XML example:</b>
     * {@snippet lang="XML" :
     *  <property name="eclipselink.tenant-schema-id" value="Oracle" />
     * }
     * <p>
     * <b>Java example:</b>
     * {@snippet :
     *  props.put(PersistenceUnitProperties.MULTITENANT_SCHEMA_PROPERTY_DEFAULT, "Oracle");
     * }
     *
     * @see #MULTITENANT_STRATEGY
     * @see org.eclipse.persistence.descriptors.SchemaPerMultitenantPolicy
     */
    public static final String MULTITENANT_SCHEMA_PROPERTY_DEFAULT = "eclipselink.tenant-schema-id";

    /**
     * Property {@code eclipselink.multitenant.tenants-share-cache}
     * specifies that multitenant entities will share the L2 cache. By default,
     * this property is false meaning multitenant entities will have an ISOLATED
     * setting. When setting it to true a PROTECTED cache setting will be used.
     * <p>
     * WARNING: Queries that use the cache may return data from other tenants
     * when using the PROTECTED setting.
     * <p>
     * <b>Persistence XML example:</b>
     * {@snippet lang="XML" :
     *  <property name="eclipselink.multitenant.tenants-share-cache" value="true" />
     * }
     * <p>
     * <b>Java example:</b>
     * {@snippet :
     *  props.put(PersistenceUnitProperties.MULTITENANT_SHARED_CACHE, true);
     * }
     *
     * @see #MULTITENANT_SHARED_EMF
     */
    public static final String MULTITENANT_SHARED_CACHE = "eclipselink.multitenant.tenants-share-cache";

    /**
     * Property {@code eclipselink.multitenant.shared-emf} is used to
     * indicate that multitenant entities will be used within a shared entity
     * manager factory. This property defaults to {@code true} (and applies to
     * multitenant entities only). When setting it to {@code false}, users are required
     * to provide a unique session name.
     * <p>
     * <b>Persistence XML example:</b>
     * {@snippet lang="XML" :
     *  <property name="eclipselink.multitenant.tenants-share-emf" value="true" />
     * }
     * <p>
     * <b>Java example:</b>
     * {@snippet :
     *  props.put(PersistenceUnitProperties.MULTITENANT_SHARED_EMF, true);
     * }
     *
     * @see #SESSION_NAME
     * @see #MULTITENANT_SHARED_CACHE
     */
    public static final String MULTITENANT_SHARED_EMF = "eclipselink.multitenant.tenants-share-emf";

    /**
     * The {@code eclipselink.multitenant.strategy} property specifies the
     * global, project wide multitenancy strategy.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>{@code external}
     * <li>the fully qualified name for a class that implements {@link org.eclipse.persistence.descriptors.MultitenantPolicy} interface
     * with public no-arg constructor
     * </ul>
     * <p>
     * <b>Persistence XML example:</b>
     * {@snippet lang="XML" :
     *  <property name="eclipselink.multitenant.strategy" value="external"/>
     * }
     * <p>
     * <b>Java example:</b>
     * {@snippet :
     *  props.put(PersistenceUnitProperties.MULTITENANT_STRATEGY, "external");
     * }
     *
     * @see org.eclipse.persistence.descriptors.MultitenantPolicy
     * @see org.eclipse.persistence.descriptors.SchemaPerMultitenantPolicy
     * @see #MULTITENANT_SCHEMA_PROPERTY_DEFAULT
     * @see #MULTITENANT_SHARED_CACHE
     * @see #MULTITENANT_SHARED_EMF
     */
    public static final String MULTITENANT_STRATEGY = "eclipselink.multitenant.strategy";

    // Platforms & Customization

    /**
     * The {@code eclipselink.ddl.table-creation-suffix} property is used in
     * conjunction with DDL generation options to append a string to the end of
     * generated CREATE Table statements.
     * <p>
     * This value is applied to all Table creation statements through the DDL generation feature
     * ie {@code <property name="eclipselink.ddl.table-creation-suffix" value="engine=InnoDB"/>}
     */
    public static final String TABLE_CREATION_SUFFIX = "eclipselink.ddl-generation.table-creation-suffix";

    /**
     * The {@code eclipselink.target-database} property configures the
     * database that will be used controlling custom operations and SQL
     * generation for the specified database.
     * <p>
     * Default: {@link TargetDatabase#Auto} which means EclipseLink will try to automatically
     * determine the correct database platform type.
     * <p>
     * Note: {@code eclipselink.target-database} must be specified with a
     * non-"Auto" class name or short name when {@code eclipselink.validation-only}
     * is set to {@code true}.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>a short string value as defined in {@link TargetDatabase}
     * <li>the fully qualified name for a class that extends {@link org.eclipse.persistence.platform.database.DatabasePlatform} abstract class
     * </ul>
     *
     * @see TargetDatabase
     * @see org.eclipse.persistence.platform.database.DatabasePlatform
     */
    public static final String TARGET_DATABASE = "eclipselink.target-database";

    /**
     * The {@code eclipselink.target-database-properties} property
     * configures additional properties for the configured target-database.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>A comma delimited key=value pairs (ie: key1=value1,key2=value2). Each
     * key is expected to be a set[key_name] method on the configured
     * target-database. The value must be the Stringified value to be passed
     * into the set[key] method.
     * </ul>
     * <p>
     * <b> Note: Keys and values cannot contain '=' or ','</b>
     * <p>
     * <b> If an invalid property is located a ConversionException will be thrown.</b>
     * <p>
     * <b> Example : </b> To change the value of
     * DatabasePlatform.shouldBindLiterals via configuration, provide the
     * following :
     * {@snippet lang="XML" :
     *  <property name="eclipselink.target-database-properties" value="shouldBindLiterals=true"/>
     * }
     * <p>
     * <b> Example 2 : </b> To change the value of
     * DatabasePlatform.supportsReturnGeneratedKeys via configuration, provide the
     * following :
     * {@snippet lang="XML" :
     *  <property name="eclipselink.target-database-properties" value="supportsReturnGeneratedKeys=true"/>
     * }
     * @see TargetDatabase
     * @see org.eclipse.persistence.platform.database.DatabasePlatform
     */
    public static final String TARGET_DATABASE_PROPERTIES = "eclipselink.target-database-properties";

    /**
     * The {@code eclipselink.exclude-eclipselink-orm} property
     * configures the exclusion of an EclipseLink ORM mapping file for a
     * specific persistence unit.
     * <p>
     * By default, the first file found at the resource name: "META-INF/eclipselink-orm.xml"
     * is processed and overrides configurations specified in annotations, and standard mapping files.
     */
    public static final String EXCLUDE_ECLIPSELINK_ORM_FILE = "eclipselink.exclude-eclipselink-orm";

    /**
     * The {@code eclipselink.session-name} property configures a
     * specific name to use when storing the singleton server session within the
     * {@link org.eclipse.persistence.sessions.factories.SessionManager}.
     * <p>
     * If a sessions-xml file is used this must be the name of the session in the
     * sessions-xml file.
     * <p>
     * By default, a unique session name is generated by EclipseLink, but the
     * user can provide a customary session name - and make sure it's unique.
     */
    public static final String SESSION_NAME = "eclipselink.session-name";

    // Weaving Properties

    /**
     * The {@code eclipselink.weaving} property configures whether
     * weaving should be performed. Weaving is required for lazy OneToOne,
     * ManyToOne, Basic, attribute change tracking, fetch groups, and other
     * optimizations.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>Not Set - defaults to {@code true} in Java SE using javaagent and within
     * EJB 3+ compliant containers
     * <li>{@code true} - requires that weaving is done. Will throw an exception if
     * entities are not woven
     * <li>{@code false} - forces weaving not to be done
     * <li>{@code static} - requires that the static weaving utility was used to weave
     * the entities
     * </ul>
     * <i>Note: Any value specified that is not in the above list is treated as
     * {@code static}.</i>
     */
    public static final String WEAVING = "eclipselink.weaving";

    /**
     * The {@code eclipselink.weaving.internal} property indicates
     * whether internal optimizations should be enabled through weaving.
     * <p>
     * Internal optimizations include caching of primary key and session,
     * addition of a serialVersionUID if none exists, optimization of EclipseLink's
     * cloning strategy and optimization of the way EclipseLink gets and sets values from
     * fields mapped as Basic.
     * <p>
     * This property will only be considered if weaving is enabled.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>{@code false}
     * <li>{@code true} (DEFAULT)
     * </ul>
     *
     * @see #WEAVING
     */
    public static final String WEAVING_INTERNAL = "eclipselink.weaving.internal";

    /**
     * The {@code eclipselink.weaving.lazy} property configures whether
     * LAZY OneToOne and ManyToOne mappings should be enabled through weaving.
     * <p>
     * This property will only be considered if weaving is enabled.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>{@code false}
     * <li>{@code true} (DEFAULT)
     * </ul>
     *
     * @see #WEAVING
     */
    public static final String WEAVING_LAZY = "eclipselink.weaving.lazy";

    /**
     * The {@code eclipselink.weaving.eager} property configures whether
     * EAGER mapping's attributes should be woven to use indirection.
     * <p>
     * This property will only be considered if weaving is enabled.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>{@code false} (DEFAULT)
     * <li>{@code true}
     * </ul>
     *
     * @see #WEAVING
     */
    public static final String WEAVING_EAGER = "eclipselink.weaving.eager";

    /**
     * The {@code eclipselink.weaving.changetracking} property configures
     * whether AttributeLevelChangeTracking should be enabled through weaving.
     * When this is enabled, only classes with all mappings allowing change
     * tracking will have change tracking enabled. Mutable basic attributes will
     * prevent this.
     * <p>
     * This property will only be considered if weaving is enabled.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>{@code false}
     * <li>{@code true} (DEFAULT)
     * </ul>
     *
     * @see #WEAVING
     */
    public static final String WEAVING_CHANGE_TRACKING = "eclipselink.weaving.changetracking";

    /**
     * The {@code eclipselink.weaving.fetchgroups} property configures
     * whether FetchGroup support should be enabled through weaving. When this
     * is enabled, lazy direct mappings will be supported as well as descriptor
     * and query level FetchGroups. FetchGroups allow partial objects to be read
     * and written, access to un-fetched attributes will cause the object to be
     * refreshed/fully-fetched.
     * <p>
     * This property will only be considered if weaving is enabled.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>{@code false}
     * <li>{@code true} (DEFAULT)
     * </ul>
     *
     * @see #WEAVING
     * @see org.eclipse.persistence.descriptors.FetchGroupManager
     * @see org.eclipse.persistence.queries.FetchGroup
     */
    public static final String WEAVING_FETCHGROUPS = "eclipselink.weaving.fetchgroups";

    /**
     * The {@code eclipselink.weaving.mappedsuperclass} property configures
     * whether {@code MappedSuperclass}es with no direct subclasses will be woven.
     * <p>
     * This property will only be considered if weaving is enabled.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>{@code true} (DEFAULT)
     * <li>{@code false}
     * </ul>
     */
    public static final String WEAVING_MAPPEDSUPERCLASS = "eclipselink.weaving.mappedsuperclass";

    /**
     * The {@code eclipselink.weaving.rest} property configures
     * whether classes will be woven to support EclipseLink JPA_RS functionality
     * <p>
     * This property will only be considered if weaving is enabled.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>{@code false}
     * <li>{@code true} (DEFAULT)
     * </ul>
     */
    public static final String WEAVING_REST = "eclipselink.weaving.rest";

    /**
     * The {@code eclipselink.target-server} property configures the
     * {@link org.eclipse.persistence.platform.server.ServerPlatform} that will be used to enable integration with a
     * host container.
     * <p>
     * Default: {@link TargetServer#None}
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>a short string value as defined in {@link TargetServer} class - this corresponds to
     * server platforms currently supported
     * <li>the fully qualified name for a class that implements {@link org.eclipse.persistence.platform.server.ServerPlatform} interface
     * </ul>
     * Specifying a name of the class implementing {@link org.eclipse.persistence.sessions.ExternalTransactionController} sets
     * {@link org.eclipse.persistence.platform.server.CustomServerPlatform} with this controller.
     *
     * @see TargetServer
     */
    public static final String TARGET_SERVER = "eclipselink.target-server";

    /**
     * The {@code eclipselink.jta.controller} property allows to override
     * JTA transaction controller class of {@link org.eclipse.persistence.platform.server.ServerPlatform}.
     * <p>
     * Value of this property is a fully qualified name of a class that implements
     * {@link org.eclipse.persistence.sessions.ExternalTransactionController} interface.
     * <p>
     * If both {@code eclipselink.target-server} and {@code eclipselink.jta.controller}
     * properties are set and contain classes implementing {@link org.eclipse.persistence.sessions.ExternalTransactionController}
     * interface, {@code eclipselink.target-server} value is used.
     */
    public static final String JTA_CONTROLLER = "eclipselink.jta.controller";

    /**
     * <p>The {@code jakarta.persistence.schema-generation.database.action}
     * property specifies the action to be taken by the persistence provider with
     * regard to the database artifacts.</p>
     *
     * <p>The values for this property are {@code none}, {@code create},
     * {@code drop-and-create}, {@code drop}.</p>
     *
     * EclipseLink also supports the {@code create-or-extend-tables} option.
     *
     * <p>If the {@code jakarta.persistence.schema-generation.database.action}
     * property is not specified, no schema generation actions must be taken on
     * the database.</p>
     */
    public static final String SCHEMA_GENERATION_DATABASE_ACTION = "jakarta.persistence.schema-generation.database.action";

    /**
     * <p>The {@code jakarta.persistence.schema-generation.scripts.action}
     * property specifies which scripts are to be generated by the persistence
     * provider.</p>
     *
     * <p>The values for this property are {@code none}, {@code create},
     * {@code drop-and-create}, {@code drop}.</p>
     *
     * <p>Scripts will only be generated if script targets are specified. If
     * this property is not specified, no scripts will be generated.</p>
     */
    public static final String SCHEMA_GENERATION_SCRIPTS_ACTION = "jakarta.persistence.schema-generation.scripts.action";

    /**
     * <p>The {@code jakarta.persistence.schema-generation.create-source}
     * property specifies whether the creation of database artifacts is to occur
     * on the basis of the object/relational mapping metadata, DDL script, or a
     * combination of the two.</p>
     *
     * <p>The values for this property are {@code metadata}, {@code script},
     * {@code metadata-then-script}, {@code script-then-metadata}.</p>
     *
     * <p>If this property is not specified, and a script is specified by the
     * {@code jakarta.persistence.schema-generation.create-script-source property},
     * the script (only) will be used for schema generation; otherwise if this
     * property is not specified, schema generation will occur on the basis of
     * the object/relational mapping metadata (only).</p>
     *
     * <p>The {@code metadata-then-script} and {@code script-then-metadata}
     * values specify that a combination of metadata and script is to be used
     * and the order in which this use is to occur. If either of these values is
     * specified and the resulting database actions are not disjoint, the
     * results are undefined and schema generation may fail.</p>
     */
    public static final String SCHEMA_GENERATION_CREATE_SOURCE = "jakarta.persistence.schema-generation.create-source";

    /**
     * <p>The {@code jakarta.persistence.schema-generation.drop-source} property
     * specifies whether the dropping of database artifacts is to occur on the
     * basis of the object/relational mapping metadata, DDL script, or a
     * combination of the two.</p>
     *
     * <p>The values for this property are {@code metadata}, {@code script},
     * {@code metadata-then-script}, {@code script-then-metadata}.</p>
     *
     * <p>If this property is not specified, and a script is specified by the
     * {@code jakarta.persistence.schema-generation.drop-script-source} property,
     * the script (only) will be used for the dropping of database artifacts;
     * otherwise if this property is not specified, the dropping of database
     * artifacts will occur on the basis of the object/relational mapping
     * metadata (only).</p>
     *
     * <p>The {@code metadata-then-script} and {@code script-then-metadata}
     * values specify that a combination of metadata and script is to be used
     * and the order in which this use is to occur. If either of these values is
     * specified and the resulting database actions are not disjoint, the
     * results are undefined and the dropping of database artifacts may fail.</p>
     */
    public static final String SCHEMA_GENERATION_DROP_SOURCE = "jakarta.persistence.schema-generation.drop-source";

    /**
     * <p>In Java EE environments, it is anticipated that the Java EE platform
     * provider may wish to control the creation of database schemas rather than
     * delegate this task to the persistence provider.</p>
     *
     * <p>The {@code jakarta.persistence.schema-generation.create-database-schemas}
     * property specifies whether the persistence provider is to create the
     * database schema(s) in addition to creating database objects such as
     * tables, sequences, constraints, etc.</p>
     *
     * <p>The value of this boolean property should be set to true if the
     * persistence provider is to create schemas in the database or to generate
     * DDL that contains {@code CREATE SCHEMA} commands. If this property
     * is not supplied, the provider should not attempt to create database
     * schemas. This property may also be specified in Java SE environments.</p>
     */
    public static final String SCHEMA_GENERATION_CREATE_DATABASE_SCHEMAS = "jakarta.persistence.schema-generation.create-database-schemas";

    /**
     * <p>If scripts are to be generated, the target locations for the writing
     * of these scripts must be specified.</p>
     *
     * <p>The {@code jakarta.persistence.schema-generation.scripts.create-target}
     * property specifies a java.IO.Writer configured for use by the persistence
     * provider for output of the DDL script or a string specifying the file URL
     * for the DDL script. This property should only be specified if scripts are
     * to be generated.</p>
     */
    public static final String SCHEMA_GENERATION_SCRIPTS_CREATE_TARGET = "jakarta.persistence.schema-generation.scripts.create-target";

    /**
     * <p>If scripts are to be generated, the target locations for the writing
     * of these scripts must be specified.</p>
     *
     * <p>The {@code jakarta.persistence.schema-generation.scripts.drop-target}
     * property specifies a java.IO.Writer configured for use by the persistence
     * provider for output of the DDL script or a string specifying the file URL
     * for the DDL script. This property should only be specified if scripts are
     * to be generated.</p>
     */
    public static final String SCHEMA_GENERATION_SCRIPTS_DROP_TARGET = "jakarta.persistence.schema-generation.scripts.drop-target";

    /**
     * <p>If scripts are to be generated by the persistence provider and a
     * connection to the target database is not supplied, the
     * {@code jakarta.persistence.database-product-name} property must be
     * specified.</p>
     *
     * <p>The value of this property should be the value returned for the target
     * database by the JDBC DatabaseMetaData method getDatabaseProductName.</p>
     *
     * <p>If sufficient database version information is not included in the
     * result of this method, the {@code jakarta.persistence.database-major-version}
     * and {@code jakarta.persistence.database-minor-version} properties
     * should be specified as needed. These should contain the values returned
     * by the JDBC getDatabaseMajorVersion and getDatabaseMinor-Version methods
     * respectively.</p>
     */
    public static final String SCHEMA_DATABASE_PRODUCT_NAME = "jakarta.persistence.database-product-name";

    /**
     * <p>If sufficient database version information is not included from the
     * JDBC DatabaseMetaData method getDatabaseProductName, the
     * {@code jakarta.persistence.database-major-version} property should
     * be specified as needed. This should contain the value returned by the
     * JDBC getDatabaseMajor-Version method.</p>
     */
    public static final String SCHEMA_DATABASE_MAJOR_VERSION = "jakarta.persistence.database-major-version";

    /**
     * <p>If sufficient database version information is not included from the
     * JDBC DatabaseMetaData method getDatabaseProductName, the
     * {@code jakarta.persistence.database-minor-version} property should
     * be specified as needed. This should contain the value returned by the
     * JDBC getDatabaseMinor-Version method.</p>
     */
    public static final String SCHEMA_DATABASE_MINOR_VERSION = "jakarta.persistence.database-minor-version";

    /**
     * <p>The {@code jakarta.persistence.schema-generation.create-script-source}
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
     * <p>The {@code jakarta.persistence.schema-generation.create-script-source}
     * property specifies a java.IO.Reader configured for reading of the DDL
     * script or a string designating a file URL for the DDL script.</p>
     */
    public static final String SCHEMA_GENERATION_CREATE_SCRIPT_SOURCE = "jakarta.persistence.schema-generation.create-script-source";

    /**
     * <p>The {@code jakarta.persistence.schema-generation.drop-script-source}
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
     * <p>The {@code jakarta.persistence.schema-generation.drop-script-source}
     * property specifies a java.IO.Reader configured for reading of the DDL
     * script or a string designating a file URL for the DDL script.</p>
     */
    public static final String SCHEMA_GENERATION_DROP_SCRIPT_SOURCE = "jakarta.persistence.schema-generation.drop-script-source";

    /**
     * <p>The {@code jakarta.persistence.schema-generation.connection} property
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
    public static final String SCHEMA_GENERATION_CONNECTION = "jakarta.persistence.schema-generation.connection";

    /**
     * <p>In Java EE container environments, it is generally expected that the
     * container will be responsible for executing data load scripts, although
     * the container is permitted to delegate this task to the persistence
     * provider. If a load script is to be used in Java SE environments or if
     * the Java EE container delegates the execution of the load script to the
     * persistence provider, this property must be specified.</p>
     *
     * <p>The {@code jakarta.persistence.sql-load-script-source} property
     * specifies a java.IO.Reader configured for reading of the SQL load script
     * for database initialization or a string designating a file URL for the
     * script.</p>
     */
    public static final String SCHEMA_GENERATION_SQL_LOAD_SCRIPT_SOURCE = "jakarta.persistence.sql-load-script-source";

    /**
     * The parameter value {@code create}
     * <p>For use with the {@code jakarta.persistence.schema-generation.database.action}
     * and {@code jakarta.persistence.schema-generation.scripts.action} properties.</p>
     * <p>Specifies that database tables should be created.</p>
     */
    public static final String SCHEMA_GENERATION_CREATE_ACTION = "create";

    /**
     * The parameter value {@code drop-and-create}
     * <p>For use with the {@code jakarta.persistence.schema-generation.database.action}
     * and {@code jakarta.persistence.schema-generation.scripts.action} properties.</p>
     * <p>Specifies that database tables should be dropped, then created.</p>
     */
    public static final String SCHEMA_GENERATION_DROP_AND_CREATE_ACTION = "drop-and-create";

    /**
     * The parameter value {@code drop}
     * <p>For use with the {@code jakarta.persistence.schema-generation.database.action}
     * and {@code jakarta.persistence.schema-generation.scripts.action} properties.</p>
     * <p>Specifies that database tables should be dropped.</p>
     */
    public static final String SCHEMA_GENERATION_DROP_ACTION = "drop";

    /**
     * The parameter value {@code none}
     * <p>For use with the {@code jakarta.persistence.schema-generation.database.action}
     * and {@code jakarta.persistence.schema-generation.scripts.action} properties.</p>
     * <p>Specifies that database tables should not be created or dropped.</p>
     */
    public static final String SCHEMA_GENERATION_NONE_ACTION = "none";

    /**
     * The parameter value {@code verify}
     * <p>For use with the {@code jakarta.persistence.schema-generation.database.action}
     * and {@code jakarta.persistence.schema-generation.scripts.action} properties.</p>
     * <p>Specifies that database tables should be verified.</p>
     */
    public static final String SCHEMA_GENERATION_VERIFY_ACTION = "verify";

    /**
     * The parameter value {@code metadata}
     * <p>For use with the {@code jakarta.persistence.schema-generation.create-source}
     * and {@code jakarta.persistence.schema-generation.drop-source} properties.</p>
     * <p>Specifies that DDL generation source will come from the metadata only.</p>
     */
    public static final String SCHEMA_GENERATION_METADATA_SOURCE = "metadata";

    /**
     * The parameter value {@code script}
     * <p>For use with the {@code jakarta.persistence.schema-generation.create-source}
     * and {@code jakarta.persistence.schema-generation.drop-source} properties.</p>
     * <p>Specifies that DDL generation source will come from scripts only.</p>
     */
    public static final String SCHEMA_GENERATION_SCRIPT_SOURCE = "script";

    /**
     * The parameter value {@code metadata-then-script}
     * <p>For use with the {@code jakarta.persistence.schema-generation.create-source}
     * and {@code jakarta.persistence.schema-generation.drop-source} properties.</p>
     * <p>Specifies that DDL generation source will come from the metadata first
     * followed with the scripts.</p>
     */
    public static final String SCHEMA_GENERATION_METADATA_THEN_SCRIPT_SOURCE = "metadata-then-script";

    /**
     * The parameter value {@code script-then-metadata}
     * <p>For use with the {@code jakarta.persistence.schema-generation.create-source}
     * and {@code jakarta.persistence.schema-generation.drop-source} properties.</p>
     * <p>Specifies that DDL generation source will come from the scripts first
     * followed with the metadata.</p>
     */
    public static final String SCHEMA_GENERATION_SCRIPT_THEN_METADATA_SOURCE = "script-then-metadata";

    /**
     * When the {@code eclipselink.ddlgen-terminate-statements} property
     * is set to true and a DDL script is being generated, the value of
     * {@link org.eclipse.persistence.platform.database.DatabasePlatform#getStoredProcedureTerminationToken()} is appended to the end of each statement.
     * <p>
     * <b>Allowed Values</b> (String)<b>:</b>
     * <ul>
     * <li>{@code false} - (DEFAULT) Do not print line terminator
     * characters
     * <li>{@code true}
     * </ul>
     */
    public static final String SCHEMA_GENERATION_SCRIPT_TERMINATE_STATEMENTS = "eclipselink.ddlgen-terminate-statements";

    /**
     * The {@code eclipselink.sequencing.default-sequence-to-table} property
     * determines the default behavior when a GeneratedValue of type SEQUENCE is used
     * on a database platform that does not support SEQUENCE generation.
     * By default, IDENTITY generation is used if supported.
     * If this property is set to true, then TABLE sequencing will be used instead.
     */
    public static final String SEQUENCING_SEQUENCE_DEFAULT = "eclipselink.sequencing.default-sequence-to-table";

    /**
     * By default, EclipseLink generates sequence values at (NEXTVAL - allocationSize). For instance, if NEXTVAL returns a
     * value of 100 and the allocationSize is 50 (default), EclipseLink will begin sequence values at 100 - allocationSize.
     * When the {@code eclipselink.sequencing.start-sequence-at-nextval} property
     * is set to true, the ID values generated from sequences starting at NEXTVAL and proceeding forward.
     * <p>
     * <b>Allowed Values</b> (String)<b>:</b>
     * <ul>
     * <li>{@code false} - (DEFAULT) uses default behavior of next value - allocationSize
     * <li>{@code true}
     * </ul>
     */
    public static final String SEQUENCING_START_AT_NEXTVAL = "eclipselink.sequencing.start-sequence-at-nextval";

    /**
     * The {@code eclipselink.session.customizer} property configures a
     * {@link org.eclipse.persistence.sessions.SessionCustomizer} used to alter the runtime configuration through
     * API.
     * <p>
     * Session customizer is called after all other properties have been processed.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>the fully qualified name for a class that implements {@link org.eclipse.persistence.sessions.SessionCustomizer} interface
     * </ul>
     *
     * @see org.eclipse.persistence.sessions.SessionCustomizer
     */
    public static final String SESSION_CUSTOMIZER = "eclipselink.session.customizer";

    // Under review public static final String RELATIONSHIPS_FETCH_DEFAULT =
    // "eclipselink.relationships-fetch-default";

    /**
     * The {@code eclipselink.descriptor.customizer.} is a prefix for a
     * property used to configure a {@link org.eclipse.persistence.descriptors.DescriptorCustomizer}. Customization
     * Prefix Property names formed out of this prefix by appending either
     * entity name, or class name (indicating that the property values applies
     * only to a particular entity) Allows descriptor customization.
     * <p>
     * Only session customizer is called after processing these properties.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>the fully qualified name for a class that implements {@link org.eclipse.persistence.descriptors.DescriptorCustomizer} interface
     * </ul>
     *
     * @see org.eclipse.persistence.descriptors.DescriptorCustomizer
     */
    public static final String DESCRIPTOR_CUSTOMIZER_ = "eclipselink.descriptor.customizer.";

    /**
     * The {@code eclipselink.jdbc.uppercase-columns} property configures
     * native SQL queries are used, the JDBC meta-data may return column names
     * in lower case on some platforms. If the column names are upper-case in
     * the mappings (default) then they will not match. This setting allows for
     * forcing the column names from the meta-data to upper-case.
     * <p>
     * This setting has been replaced by {@link #UPPERCASE_COLUMN_NAMES}, which should be used instead,
     * as it ensure both sides use upper case for comparisons.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>{@code false}
     * <li>{@code true}
     * </ul>
     *
     * @see #UPPERCASE_COLUMN_NAMES
     */
    public static final String NATIVE_QUERY_UPPERCASE_COLUMNS = "eclipselink.jdbc.uppercase-columns";

    /**
     * The {@code eclipselink.jpa.uppercase-column-names} property configures JPA processing
     * to uppercase all column name definitions. A value of {@code true} also sets the
     * {@code eclipselink.jdbc.uppercase-columns} property to {@code true}, so that JDBC meta-data
     * returned from the database is also returned in uppercase, ensuring fields are the same case.  This
     * gets around situations where user defined fields do not match the case returned by the database for
     * native queries, simulating case insensitivity.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>{@code false}
     * <li>{@code true} (DEFAULT)
     * </ul>
     *
     * @see #NATIVE_QUERY_UPPERCASE_COLUMNS
     */
    public static final String UPPERCASE_COLUMN_NAMES = "eclipselink.jpa.uppercase-column-names";

    /**
     * The {@code eclipselink.jdbc.batch-writing} property configures the
     * use of batch writing to optimize transactions with multiple writes.
     * <p>Batch writing allows multiple heterogeneous dynamic SQL statements to be sent to the database as a single
     * execution, or multiple homogeneous parameterized SQL statements to be executed as a single batch execution.
     * <p>Note that not all JDBC drivers, or databases support batch writing.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>{@code JDBC} - use JDBC batch writing.
     * <li>{@code Buffered} - do not use either JDBC batch writing nor native platform
     * batch writing.
     * <li>{@code Oracle-JDBC} - use Oracle's native batch writing. This requires the
     * use of an Oracle JDBC driver.
     * <li>{@code None} (DEFAULT): do not use batch writing (turn it off).
     * <li>the fully qualified name for a class that extends {@link org.eclipse.persistence.internal.databaseaccess.BatchWritingMechanism} abstract class
     * </ul>
     *
     * @see BatchWriting
     * @see org.eclipse.persistence.internal.databaseaccess.BatchWritingMechanism
     */
    public static final String BATCH_WRITING = "eclipselink.jdbc.batch-writing";

    /**
     * The {@code eclipselink.jdbc.batch-writing.size} property
     * configures the batch size used for batch writing. For parameterized batch
     * writing this is the number of statements to batch, default 100. For
     * dynamic batch writing, this is the size of the batched SQL buffer,
     * default 32k.
     *
     * @see #BATCH_WRITING
     */
    public static final String BATCH_WRITING_SIZE = "eclipselink.jdbc.batch-writing.size";

    /**
     * The {@code jakarta.persistence.bean.manager} property is used to set
     * CDI BeanManager when available
     */
    public static final String CDI_BEANMANAGER = "jakarta.persistence.bean.manager";

    /**
     * The {@code eclipselink.persistencexml} property specifies the full
     * resource name to look for the persistence XML files in. If not specified
     * the default value defined by {@link #ECLIPSELINK_PERSISTENCE_XML_DEFAULT}
     * will be used.
     * <p>
     * IMPORTANT: For now this property is used for the canonical model
     * generator, but it can later be used as a system property for customizing
     * weaving and application bootstrap usage.
     * <p>
     * This property is only used by EclipseLink when it is locating the
     * configuration file. When used within an EJB/Spring container in container
     * managed mode the locating and reading of this file is done by the
     * container and will not use this configuration.
     */
    public static final String ECLIPSELINK_PERSISTENCE_XML = "eclipselink.persistencexml";

    /**
     * The {@code eclipselink.se-puinfo} property specifies a
     * {@code org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo} that is used
     * create an EntityManagerFactory. That datastructure is used in liu of a persistence.xml.
     * <p><b>IMPORTANT</b>: This property is only supported for use in testing.
     */
    public static final String ECLIPSELINK_SE_PUINFO = "eclipselink.se-puinfo";

    /**
     * The default resource location used to locate the persistence.xml
     * configuration files. Default: "META-INF/persistence.xml"
     */
    public static final String ECLIPSELINK_PERSISTENCE_XML_DEFAULT = "META-INF/persistence.xml";

    /**
     * This {@code eclipselink.persistenceunits} property specifies the
     * set of persistence unit names that will be processed when generating the
     * canonical model. By default, all persistence units available in all
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
     * The {@code eclipselink.exception-handler} property allows an
     * {@link org.eclipse.persistence.exceptions.ExceptionHandler} to be specified. An {@link org.eclipse.persistence.exceptions.ExceptionHandler} handles
     * exceptions when they are thrown so that an application might address
     * expected failures and continue.
     *
     * @see org.eclipse.persistence.exceptions.ExceptionHandler
     */
    public static final String EXCEPTION_HANDLER_CLASS = "eclipselink.exception-handler";

    /**
     * The {@code eclipselink.session.include.descriptor.queries}
     * property configures whether to enable the copying of all descriptor named
     * queries to the session to be usable from the entity manager.
     * <p>
     * Default: {@code false}.
     */
    public static final String INCLUDE_DESCRIPTOR_QUERIES = "eclipselink.session.include.descriptor.queries";

    /**
     * The {@code eclipselink.session-event-listener} property configures
     * a session event listener class.
     * <p>
     * Use {@link #SESSION_CUSTOMIZER} to configure multiple listeners using API.
     *
     * @see org.eclipse.persistence.sessions.SessionEventListener
     * @see #SESSION_CUSTOMIZER
     */
    public static final String SESSION_EVENT_LISTENER_CLASS = "eclipselink.session-event-listener";

    /**
     * The {@code eclipselink.sessions-xml} property configures the use
     * of the specified native sessions.xml configuration file. When specified
     * this file will load all the session configuration and mapping
     * information from the native XML. No JPA annotations or XML will be used.
     */
    public static final String SESSIONS_XML = "eclipselink.sessions-xml";

    /**
     * The {@code eclipselink.project-cache} property configures the type of
     * {@code org.eclipse.persistence.jpa.metadata.ProjectCache} implementation to use to retrieve and store projects
     * representing the metadata for the project.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>{@code java-serialization} - use {@code org.eclipse.persistence.jpa.metadata.FileBasedProjectCache}
     * <li>the fully qualified name for a class that implements {@code org.eclipse.persistence.jpa.metadata.ProjectCache} interface
     * </ul>
     *
     * @see "org.eclipse.persistence.jpa.metadata.ProjectCache"
     */
    public static final String PROJECT_CACHE = "eclipselink.project-cache";

    /**
     * The property {@code eclipselink.project-cache.java-serialization.file} specifies the name
     * of the file to read/write a serialized project representing the application's metadata
     * <p>Specifies the name of the metadata repository xml file to read from using classloader to find the resource</p>
     *
     * <p>This property should be used in conjunction with
     * {@code eclipselink.project-cache} when a project is serialized to a file for caching.</p>
     *
     * @see #PROJECT_CACHE
     */
    public static final String PROJECT_CACHE_FILE = "eclipselink.project-cache.java-serialization.file-location";

    /**
     * The {@code eclipselink.temporal.mutable} property configures the
     * default for detecting changes to temporal field (Date, Calendar). Default
     * {@code false} (changes to date object itself are not detected). By default, it
     * is assumed that temporal fields are replaced, and the temporal object not
     * changed directly. Enabling mutable temporal fields will cause weaving of
     * attribute change tracking to be disabled.
     */
    public static final String TEMPORAL_MUTABLE = "eclipselink.temporal.mutable";

    /**
     * The {@code eclipselink.jdbc.allow-native-sql-queries} property
     * specifies whether any user defined SQL is allowed within a persistence
     * unit. This is of particular importance within a multitenant to minimize
     * the potential impact of revealing multi tenant information. By default,
     * any persistence unit containing at least one multitenant entity will
     * cause this flag to be set to {@code false}.
     * <p>
     * <b>Allowed Values</b> (String)<b>:</b>
     * <ul>
     * <li>{@code false} - do not allow native SQL.
     * <li>{@code true} (DEFAULT) - allow native SQL
     * </ul>
     *
     * @see org.eclipse.persistence.sessions.Project#setAllowNativeSQLQueries(boolean)
     */
    public static final String ALLOW_NATIVE_SQL_QUERIES = "eclipselink.jdbc.allow-native-sql-queries";

    /**
     * The {@code eclipselink.allow-zero-id} property configures if zero
     * is considered a valid id on a new entity. If the id is not considered
     * valid and sequencing is enabled for the entity a new value will be
     * assigned when it is persisted to the database (INSERT). By default, an id
     * value of 0 is assumed to be null/unassigned. This allows 0 to be a valid
     * id value. This can also be set per class using the {@link org.eclipse.persistence.annotations.PrimaryKey} annotation
     * and {@link org.eclipse.persistence.annotations.IdValidation}.
     * <p>
     * Caution: This property configures the static singleton on
     * {@link org.eclipse.persistence.internal.helper.Helper#isZeroValidPrimaryKey} which will be shared by all
     * concurrent uses of EclipseLink.
     *
     * @see org.eclipse.persistence.annotations.PrimaryKey
     * @see org.eclipse.persistence.annotations.IdValidation
     * @see #ID_VALIDATION
     *
     * @deprecated replaced by {@link #ID_VALIDATION} property with value {@code NULL}.
     */
    @Deprecated
    public static final String ALLOW_ZERO_ID = "eclipselink.allow-zero-id";

    /**
     * The {@code eclipselink.id-validation} property defines
     * which primary key components values are considered invalid.
     * These values will be also overridden by sequencing.
     * Note that Identity always overrides any existing id value
     * and so does any sequence with {@link org.eclipse.persistence.sequencing.Sequence#shouldAlwaysOverrideExistingValue()} flag
     * set to {@code true}.
     *
     * @see org.eclipse.persistence.annotations.PrimaryKey
     * @see org.eclipse.persistence.annotations.IdValidation
     */
    public static final String ID_VALIDATION = "eclipselink.id-validation";

    /**
     * The {@code eclipselink.flush-clear.cache} property defines {@code EntityManager} cache
     * behavior after a call to flush method followed by a call to clear method. This property
     * could be specified while creating either {@code EntityManagerFactory} (either in the map passed to
     * createEntityManagerFactory method or in persistence.xml) or EntityManager
     * (in the map passed to createEntityManager method); the latter overrides
     * the former.
     *
     * @see FlushClearCache
     */
    public static final String FLUSH_CLEAR_CACHE = "eclipselink.flush-clear.cache";

    /**
     * The {@code eclipselink.classloader} property specifies the classloader to use to create
     * an EntityManagerFactory in the property map passed to Persistence.createEntityManagerFactory.
     */
    public static final String CLASSLOADER = "eclipselink.classloader";

    /**
     * The {@code eclipselink.orm.throw.exceptions} property specifies if the first exception
     * that occurs during deployment should be thrown, or if all exceptions should be caught
     * and summary thrown at end of deployment attempt.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>{@code false}
     * <li>{@code true} (DEFAULT)
     * </ul>
     */
    public static final String THROW_EXCEPTIONS = "eclipselink.orm.throw.exceptions";

    /**
     * The {@code eclipselink.orm.validate.schema} property set on the session is used to
     * override orm.xml schema validation from its default of {@code false}.
     */
    public static final String ORM_SCHEMA_VALIDATION = "eclipselink.orm.validate.schema";

    /**
     * The {@code eclipselink.deploy-on-startup} property controls whether
     * EclipseLink creates the persistence unit when the application starts up, or
     * when the persistence unit is first actually accessed by the application.
     * <p>
     * Setting this to {@code true} causes the persistence unit to be created when the
     * EntityManagerFactory is created, usually during deployment to a Java EE
     * 7 container or servlet container.  Enabling this option may increase
     * startup time of the container/server, but will prevent the first request
     * to the application from pausing while the persistence unit is deployed.
     * <p>
     * When this property is set to {@code false} the persistence unit is not
     * initialized until the first EntityManager is created or until metadata
     * is requested from the EntityManagerFactory.
     * <p>
     * When set to {@code false}, there is a known issue with Fields of static metamodel
     * classes ("Entity_" classes) being {@code null} until the persistence unit is
     * initialized. This behaviour won't affect applications unless they use
     * the static metamodel feature.  (See <a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=383199">bug 383199</a>)
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>{@code false} (DEFAULT)
     * <li>{@code true}
     * </ul>
     */
    public static final String DEPLOY_ON_STARTUP = "eclipselink.deploy-on-startup";

    /**
     * The {@code eclipselink.validation-only} property validates deployment
     * which includes initializing descriptors but does not connect (no login to the database).
     * <p>
     * Note: {@code eclipselink.target-database} must be specified with a
     * non-"Auto" class name or short name when {@code eclipselink.validation-only}
     * is set to {@code true}.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>{@code false} (DEFAULT)
     * <li>{@code true}
     * </ul>
     * @see #TARGET_DATABASE
     * @see TargetDatabase
     */
    // See 324213
    public static final String VALIDATION_ONLY_PROPERTY = "eclipselink.validation-only";

    /**
     * The {@code eclipselink.ddl-generation} property allows the database schema to be generated
     * on deployment.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>{@code drop-tables} - {@link #DROP_ONLY}
     * <li>{@code create-tables} - {@link #CREATE_ONLY}
     * <li>{@code drop-and-create-tables} - {@link #DROP_AND_CREATE}
     * <li>{@code create-or-extend-tables} - {@link #CREATE_OR_EXTEND}
     * <li>{@code none} - {@link #NONE} (DEFAULT)
     * </ul>
     */
    public static final String DDL_GENERATION = "eclipselink.ddl-generation";

    /**
     * The parameter value {@code create-tables} specifies that database tables should be created.
     * <p>For use with the {@code eclipselink.ddl-generation} property.</p>
     *
     * @see #DDL_GENERATION
     */
    public static final String CREATE_ONLY = "create-tables";

    /**
     * The parameter value {@code drop-tables} specifies that database tables should be dropped only.
     * <p>For use with the {@code eclipselink.ddl-generation} property.</p>
     *
     * @see #DDL_GENERATION
     */
    public static final String DROP_ONLY = "drop-tables";

    /**
     * The parameter value {@code drop-and-create-tables} specifies that database tables
     * should be dropped, then created.
     * <p>For use with the {@code eclipselink.ddl-generation} property.</p>
     *
     * @see #DDL_GENERATION
     */
    public static final String DROP_AND_CREATE = "drop-and-create-tables";

    /**
     * The parameter value {@code create-or-extend-tables} specifies that database tables
     * should be created and if existing, missing columns will be added.
     * <p>For use with the {@code eclipselink.ddl-generation} property.</p>
     * <p>Note this can only be used with {@code eclipselink.ddl-generation.output-mode}
     * with value of {@code database}.</p>
     *
     * @see #DDL_GENERATION
     */
    public static final String CREATE_OR_EXTEND = "create-or-extend-tables";

    /**
     * The parameter value {@code none} specifies that database tables should not be created or dropped.
     * <p>For use with the {@code eclipselink.ddl-generation} property,
     * and is the default parameter value.</p>
     *
     * @see #DDL_GENERATION
     */
    public static final String NONE = "none";

    /**
     * The {@code eclipselink.metadata-source}property configures the type of
     * MetadataSource implementation to use to read Metadata
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>{@code XML} - use {@code org.eclipse.persistence.jpa.metadata.XMLMetadataSource}
     * <li>the fully qualified name for a class that implements {@code org.eclipse.persistence.jpa.metadata.MetadataSource} interface
     * </ul>
     *
     * @see "org.eclipse.persistence.jpa.metadata.MetadataSource"
     * @see "org.eclipse.persistence.jpa.metadata.XMLMetadataSource"
     */
    public static final String METADATA_SOURCE = "eclipselink.metadata-source";

    /**
     * The {@code eclipselink.metadata-source.send-refresh-command} property
     * works with cache coordination for a clustered environment to. If cache coordination
     * is configured and the session is deployed on startup, this property controls the sending
     * of RCM refresh metadata commands to the cluster. These commands will cause the remote
     * instances to refresh their metadata.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>{@code false}
     * <li>{@code true} (DEFAULT)
     * </ul>
     *
     * @see #COORDINATION_PROTOCOL
     * @see #DEPLOY_ON_STARTUP
     */
    public static final String METADATA_SOURCE_RCM_COMMAND = "eclipselink.metadata-source.send-refresh-command";

    /**
     * The property {@code eclipselink.metadata-source.xml.file} specifies the name of the metadata
     * repository xml file to read from using classloader to find the resource
     * <p>This property should be used in conjunction with the
     * {@code eclipselink.metadata-repository} when an XML repository is being used.</p>
     *
     * @see #METADATA_SOURCE
     */
    public static final String METADATA_SOURCE_XML_FILE = "eclipselink.metadata-source.xml.file";

    /**
     * The property {@code eclipselink.metadata-source.xml.URL} specifies the name of the metadata
     * repository xml URL to read from.
     * <p>This property should be used in conjunction with the
     * {@code eclipselink.metadata-repository} when an XML repository is being used.</p>
     *
     * @see #METADATA_SOURCE
     */
    public static final String METADATA_SOURCE_XML_URL = "eclipselink.metadata-source.xml.url";

    /**
     * The property {@code eclipselink.metadata-source.properties.file} specifies the name
     * of the metadata repository properties file to read from using classloader to find the resource.
     *
     * @see #METADATA_SOURCE
     */
    public static final String METADATA_SOURCE_PROPERTIES_FILE = "eclipselink.metadata-source.properties.file";

    /**
     * The property {@code eclipselink.application-location} specifies the file system directory
     * location where DDL files are written (output) to.
     *
     * <p>This property should be used in conjunction with the
     * {@code eclipselink.ddl-generation.output-mode} property, with
     * a setting of {@code sql-script} (or {@code both}) for
     * DDL file(s) to be written.</p>
     *
     * @see #DEFAULT_APP_LOCATION
     * @see #DDL_GENERATION_MODE
     * @see #DDL_SQL_SCRIPT_GENERATION
     * @see #DDL_BOTH_GENERATION
     */
    public static final String APP_LOCATION = "eclipselink.application-location";

    /**
     * The property {@code eclipselink.create-ddl-jdbc-file-name} specifies the name
     * of the DDL file which is used to create database tables.
     *
     * <p>This property should be used in conjunction with the
     * {@code eclipselink.application-location} property to specify a
     * location on the file system for DDL file(s) to be written.</p>
     *
     * @see #APP_LOCATION
     * @see #DEFAULT_CREATE_JDBC_FILE_NAME
     */
    public static final String CREATE_JDBC_DDL_FILE = "eclipselink.create-ddl-jdbc-file-name";

    /**
     * The property {@code eclipselink.drop-ddl-jdbc-file-name} specifies the name
     * of the DDL file which is used to drop database tables.
     *
     * <p>This property should be used in conjunction with the
     * {@code eclipselink.application-location} property to specify a
     * location on the file system for DDL file(s) to be written.</p>
     *
     * @see #APP_LOCATION
     * @see #DEFAULT_DROP_JDBC_FILE_NAME
     */
    public static final String DROP_JDBC_DDL_FILE = "eclipselink.drop-ddl-jdbc-file-name";

    /**
     * The default location in the file system to output DDL files.
     * <p>
     * Defaults to: the working directory.
     *
     * @see #APP_LOCATION
     */
    public static final String DEFAULT_APP_LOCATION = "." + File.separator;

    /**
     * The default name of the DDL file which is used to create database tables.
     * <p>
     * Defaults to: {@code createDDL.jdbc}
     *
     * @see #CREATE_JDBC_DDL_FILE
     */
    public static final String DEFAULT_CREATE_JDBC_FILE_NAME = "createDDL.jdbc";

    /**
     * The default name of the DDL file which is used to drop database tables.
     * <p>
     * Defaults to: {@code dropDDL.jdbc}
     *
     * @see #DROP_JDBC_DDL_FILE
     */
    public static final String DEFAULT_DROP_JDBC_FILE_NAME = "dropDDL.jdbc";

    /**
     * The system property {@code INTERACT_WITH_DB} specifies to enable or disable
     * the execution of DDL (configured with the {@code eclipselink.ddl-generation} property)
     * against a database.
     * <p>e.g. a command line setting of {@code -DINTERACT_WITH_DB=false}
     * will not output DDL to the database.</p>
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>{@code false} - do not output DDL to the database
     * <li>{@code true} - output DDL to the database<br>
     * </ul>
     *
     * @see #DDL_GENERATION
     */
    public static final String JAVASE_DB_INTERACTION = "INTERACT_WITH_DB";

    /**
     * The {@code eclipselink.ddl-generation.output-mode} property specifies if database schema
     * should be generated on the database, to a file, or both.
     * <p>
     * Note DDL_GENERATION must also be set, for this to have an effect.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>{@code database} - {@link #DDL_DATABASE_GENERATION} (DEFAULT)
     * <li>{@code sql-script} - {@link #DDL_SQL_SCRIPT_GENERATION}
     * <li>{@code both} - {@link #DDL_BOTH_GENERATION}
     * </ul>
     *
     * @see #DEFAULT_DDL_GENERATION_MODE
     * @see #DDL_DATABASE_GENERATION
     * @see #DDL_SQL_SCRIPT_GENERATION
     * @see #DDL_BOTH_GENERATION
     */
    public static final String DDL_GENERATION_MODE = "eclipselink.ddl-generation.output-mode";

    /**
     * The {@code eclipselink.ddl-generation.index-foreign-keys} property specifies if an index
     * should be automatically generated for foreign key constraints. It is normally recommended to have
     * an index for a foreign key.
     * <p>
     * By default, indexes are not generated, most database also do not auto generate indexes, although some do.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>{@code false} (DEFAULT)
     * <li>{@code true}
     * </ul>
     *
     * @see #DDL_GENERATION
     */
    public static final String DDL_GENERATION_INDEX_FOREIGN_KEYS = "eclipselink.ddl-generation.index-foreign-keys";

    /**
     * The {@code eclipselink.schema-validation.mode} property specifies database schema validation mode.
     * By default, the {@code simple} validation mode is selected.
     * <b>Allowed Values:</b>
     * <ul>
     * <li>{@code simple} (DEFAULT)
     * <li>{@code full} (experimental feature)
     * </ul>
     *
     * @see #SCHEMA_VALIDATION_MODE_SIMPLE
     * @see #SCHEMA_VALIDATION_MODE_FULL
     */
    public static final String SCHEMA_VALIDATION_MODE = "eclipselink.schema-validation.mode";

    /**
     * The {@code simple} value of the {@code eclipselink.schema-validation.mode} property.
     * Specifies, that simple schema validation shall be performed.
     * Simple schema validation checks missing tables and missing or surplus columns in the existing tables.
     *
     * @see #SCHEMA_VALIDATION_MODE
     */
    public static final String SCHEMA_VALIDATION_MODE_SIMPLE = "simple";

    /**
     * The {@code full} value of the {@code eclipselink.schema-validation.mode} property.
     * Specifies, that full schema validation shall be performed. Full schema validation also checks differences
     * in columns definitions. This feature is experimental.
     *
     * @see #SCHEMA_VALIDATION_MODE
     */
    public static final String SCHEMA_VALIDATION_MODE_FULL = "full";

    /**
     * The parameter value {@code sql-script} specifies that DDL will be written to file(s).
     * <p>For use with the {@code eclipselink.ddl-generation.output-mode} property.</p>
     *
     * @see #DDL_GENERATION_MODE
     * @see #CREATE_JDBC_DDL_FILE
     * @see #DROP_JDBC_DDL_FILE
     */
    public static final String DDL_SQL_SCRIPT_GENERATION = "sql-script";

    /**
     * The parameter value {@code database} specifies that DDL will be written to the database.
     * <p>For use with the {@code eclipselink.ddl-generation.output-mode} property,
     * and is the default parameter value</p>
     *
     * @see #DDL_GENERATION_MODE
     * @see #CREATE_JDBC_DDL_FILE
     * @see #DROP_JDBC_DDL_FILE
     */
    public static final String DDL_DATABASE_GENERATION = "database";

    /**
     * The parameter value {@code both} specifies that DDL will be written to file(s) and the database.
     * <p>For use with the {@code eclipselink.ddl-generation.output-mode} property.</p>
     *
     * @see #DDL_GENERATION_MODE
     * @see #CREATE_JDBC_DDL_FILE
     * @see #DROP_JDBC_DDL_FILE
     */
    public static final String DDL_BOTH_GENERATION = "both";

    /**
     * The {@code eclipselink.ddl-generation.output-mode} parameter is configured
     * to the default value of {@code database}.
     *
     * @see #DDL_GENERATION_MODE
     * @see #DDL_DATABASE_GENERATION
     */
    public static final String DEFAULT_DDL_GENERATION_MODE = DDL_DATABASE_GENERATION;

    /**
     * The {@code eclipselink.validate-existence} property configures if
     * the existence of an object should be verified on persist(), otherwise it
     * will assume to be new if not in the persistence context. If checked and
     * existing and not in the persistence context and error will be thrown.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>{@code false} (DEFAULT)
     * <li>{@code true}
     * </ul>
     */
    public static final String VALIDATE_EXISTENCE = "eclipselink.validate-existence";

    /**
     * The {@code eclipselink.order-updates} property configures if updates
     * should be ordered by primary key.
     * <p>
     * This can be used to avoid possible database deadlocks from concurrent
     * threads updating the same objects in a different order.
     * If not set to true, the order of updates is not guaranteed.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>{@code false}
     * <li>{@code true} (DEFAULT)
     * </ul>
     * @deprecated as of EclipseLink 2.6 replaced by {@link #PERSISTENCE_CONTEXT_COMMIT_ORDER}
     */
    @Deprecated
    public static final String ORDER_UPDATES = "eclipselink.order-updates";

    /**
     * The {@code eclipselink.persistence-context.commit-order} property defines the ordering of updates
     * and deletes of a set of the same entity type during a commit or flush operation.
     * The commit order of entities is defined by their foreign key constraints, and then sorted alphabetically.
     * <p>
     * By default, the commit of a set of the same entity type is ordered by its Id.
     * <p>
     * Entity type commit order can be modified using a {@link org.eclipse.persistence.descriptors.DescriptorCustomizer}
     * and the {@link org.eclipse.persistence.descriptors.ClassDescriptor#addConstraintDependency(Class)} API.
     * Commit order can also be controlled using the {@code EntityManager#flush()} API.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>{@code Id} (DEFAULT) - updates and deletes are ordered by the object's id.
     * This can help avoid deadlocks on highly concurrent systems.
     * <li>{@code Changes} - updates are ordered by the object's changes, then by id.
     * This can improve batch writing efficiency.
     * <li>{@code None} - no ordering is done.
     * </ul>
     * @see CommitOrderType
     */
    public static final String PERSISTENCE_CONTEXT_COMMIT_ORDER = "eclipselink.persistence-context.commit-order";

    /**
     * The {@code eclipselink.profiler} property configures the type of
     * profiler used to capture runtime statistics.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>{@code NoProfiler} (DEFAULT)
     * <li>{@code PerformanceMonitor} - use {@link org.eclipse.persistence.tools.profiler.PerformanceMonitor}
     * <li>{@code PerformanceProfiler} - use {@link org.eclipse.persistence.tools.profiler.PerformanceProfiler}
     * <li>{@code QueryMonitor} - use {@link org.eclipse.persistence.tools.profiler.QueryMonitor}
     * <li>{@code DMSProfiler} - use {@code org.eclipse.persistence.tools.profiler.oracle.DMSPerformanceProfiler}
     * <li>the fully qualified name for a class that implements {@link org.eclipse.persistence.sessions.SessionProfiler} interface
     * </ul>
     *
     * @see ProfilerType
     */
    public static final String PROFILER = "eclipselink.profiler";

    /**
     * The {@code eclipselink.tuning}property configures the type of
     * tuner to use to configure the persistence unit.
     * <p>
     * A {@link org.eclipse.persistence.tools.tuning.SessionTuner} can be used to define a template for a persistence unit configuration.
     * It allows a set of configuration values to be configured as a single tuning option.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>{@code Standard} (DEFAULT)
     * <li>{@code Safe} - see {@link org.eclipse.persistence.tools.tuning.SafeModeTuner}
     * <li>Custom tuner - specify a full class name of an implementation of {@link org.eclipse.persistence.tools.tuning.SessionTuner}
     * </ul>
     *
     * @see TunerType
     */
    public static final String TUNING = "eclipselink.tuning";

    /**
     * The {@code eclipselink.memory.free-metadata} property configures the JPA
     * internal deployment metadata to be released after deployment.
     * This conserves memory, as the metadata is no longer required, but make
     * future deployments of any other application take longer, as the metadata must be re-allocated.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>{@code false} (DEFAULT)
     * <li>{@code true}
     * </ul>
     */
    public static final String FREE_METADATA = "eclipselink.memory.free-metadata";

    /**
     * The {@code eclipselink.transaction.join-existing} property set to
     * {@code true} forces persistence context to read through
     * JTA-managed ("write") connection in case there is an active transaction.
     * <p>
     * Note that if the property set to {@code true} then objects read during
     * transaction won't be placed into the shared cache unless they have been
     * updated.
     * <p>
     * The property set in persistence.xml or passed to
     * createEntityManagerFactory affects all EntityManagers created by the
     * factory. Alternatively, to apply the property only to some
     * EntityManagers pass it to createEntityManager method.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>{@code false} (DEFAULT)
     * <li>{@code true}
     * </ul>
     */
    public static final String JOIN_EXISTING_TRANSACTION = "eclipselink.transaction.join-existing";

    /**
     * The {@code eclipselink.persistence-context.reference-mode}
     * property configures whether there should be hard or soft references used
     * within the Persistence Context. Default is {@code HARD}. With soft references
     * entities no longer referenced by the application may be garbage collected
     * freeing resources. Any changes that have not been flushed in these
     * entities will be lost.
     * <p>
     * The property set in persistence.xml or passed to
     * createEntityManagerFactory affects all EntityManagers created by the
     * factory. Alternatively, to apply the property only to some
     * EntityManagers pass it to createEntityManager method.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>{@code HARD} (DEFAULT) - {@link ReferenceMode#HARD}
     * <li>{@code WEAK}  - {@link ReferenceMode#WEAK}
     * <li>{@code FORCE_WEAK} - {@link ReferenceMode#FORCE_WEAK}
     * </ul>
     *
     * @see ReferenceMode
     */
    public static final String PERSISTENCE_CONTEXT_REFERENCE_MODE = "eclipselink.persistence-context.reference-mode";

    /**
     * The {@code jakarta.persistence.lock.timeout} property configures the
     * WAIT timeout used in pessimistic locking, if the database query exceeds
     * the timeout the database will terminate the query and return an
     * exception.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>a string containing a zero or greater integer value
     * </ul>
     */
    public static final String PESSIMISTIC_LOCK_TIMEOUT = QueryHints.PESSIMISTIC_LOCK_TIMEOUT;

    /**
     * The {@code eclipselink.pessimistic.lock.timeout.unit} property
     * configures the query timeout unit value. Allows users more refinement.
     * Used in combination with PersistenceUnitProperties.PESSIMISTIC_LOCK_TIMEOUT
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>{@code java.util.concurrent.TimeUnit.MILLISECONDS} (DEFAULT),
     * <li>{@code java.util.concurrent.TimeUnit.SECONDS},
     * <li>{@code java.util.concurrent.TimeUnit.MINUTES}.
     * </ul>
     * @see #PESSIMISTIC_LOCK_TIMEOUT_UNIT
    */
    public static final String PESSIMISTIC_LOCK_TIMEOUT_UNIT = QueryHints.PESSIMISTIC_LOCK_TIMEOUT_UNIT;

    /**
     * The {@code jakarta.persistence.query.timeout} property configures
     * the default query timeout value. Defaults to milliseconds, but is configurable
     * with PersistenceUnitProperties.QUERY_TIMEOUT_UNIT
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>a string containing a zero or greater integer value
     * </ul>
     * @see #QUERY_TIMEOUT_UNIT
     */
    public static final String QUERY_TIMEOUT = "jakarta.persistence.query.timeout";

    /**
     * The {@code eclipselink.query.timeout.unit} property
     * configures the query timeout unit value. Allows users more refinement.
     * Used in combination with PersistenceUnitProperties.QUERY_TIMEOUT
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>{@code java.util.concurrent.TimeUnit.MILLISECONDS} (DEFAULT),
     * <li>{@code java.util.concurrent.TimeUnit.SECONDS},
     * <li>{@code java.util.concurrent.TimeUnit.MINUTES}.
     * </ul>
     * @see #QUERY_TIMEOUT
     */
    public static final String QUERY_TIMEOUT_UNIT = "eclipselink.query.timeout.unit";

    /**
     * The {@code eclipselink.persistence-context.close-on-commit}
     * property specifies that the EntityManager will be closed or not used
     * after commit (not extended). In general this is normally always the case
     * for a container managed EntityManager, and common for application
     * managed. This can be used to avoid additional performance overhead of
     * resuming the persistence context after a commit().
     * <p>
     * The property set in persistence.xml or passed to createEntityManagerFactory affects all
     * EntityManagers created by the factory. Alternatively, to apply the property only to some
     * EntityManagers pass it to createEntityManager method.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>{@code false} (DEFAULT)
     * <li>{@code true}
     * </ul>
     */
    public static final String PERSISTENCE_CONTEXT_CLOSE_ON_COMMIT = "eclipselink.persistence-context.close-on-commit";

    /**
     * The {@code eclipselink.persistence-context.persist-on-commit}
     * property specifies that the EntityManager will search all managed objects
     * and persist any related non-managed new objects that are cascade persist.
     * This can be used to avoid the cost of performing this search if persist
     * is always used for new objects.
     * <p>
     * The property set in persistence.xml or passed to createEntityManagerFactory affects all
     * EntityManagers created by the factory. Alternatively, to apply the property only to some
     * EntityManagers pass it to createEntityManager method.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>{@code false}
     * <li>{@code true} (DEFAULT)
     * </ul>
     */
    public static final String PERSISTENCE_CONTEXT_PERSIST_ON_COMMIT = "eclipselink.persistence-context.persist-on-commit";

    /**
     * The {@code eclipselink.persistence-context.commit-without-persist-rules}
     * property specifies that the EntityManager will search all managed objects
     * and persist any related non-managed new objects that are found ignoring
     * any absence of CascadeType.PERSIST settings. Also, the Entity life-cycle
     * Persist operation will not be cascaded to related entities. This setting
     * replicates the traditional EclipseLink native functionality.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>{@code false} (DEFAULT)
     * <li>{@code true}
     * </ul>
     */
    public static final String PERSISTENCE_CONTEXT_COMMIT_WITHOUT_PERSIST_RULES = "eclipselink.persistence-context.commit-without-persist-rules";

    /**
     * The {@code eclipselink.persistence-context.flush-mode} property configures the EntityManager FlushMode to be set as a
     * persistence property. This can be set to either {@code AUTO} or {@code COMMIT}.
     * <p>
     * By default, the flush mode is {@code AUTO}, which requires an automatic flush before
     * all query execution. This can be used to avoid any flushing until commit.
     * <p>
     * The property set in persistence.xml or passed to
     * createEntityManagerFactory affects all EntityManagers created by the
     * factory. Alternatively, to apply the property only to some EntityManagers
     * pass it to createEntityManager method.
     *
     * @see jakarta.persistence.EntityManager#setFlushMode(jakarta.persistence.FlushModeType)
     * @see jakarta.persistence.FlushModeType
     */
    public static final String PERSISTENCE_CONTEXT_FLUSH_MODE = "eclipselink.persistence-context.flush-mode";

    /**
     * The {@code eclipselink.oracle.proxy-type} property is used to
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
     * </ul>
     * <p>
     * Typically, these properties should be set into EntityManager (either
     * through createEntityManager method or using proprietary setProperties
     * method on EntityManagerImpl) - that causes EntityManager to use proxy
     * connection for writing and reading inside transaction. If proxy-type and
     * the corresponding proxy property set into EntityManagerFactory then all
     * connections created by the factory will be proxy connections.
     */
    public static final String ORACLE_PROXY_TYPE = "eclipselink.oracle.proxy-type";

    /**
     * The {@code eclipselink.cache.coordination.protocol} property
     * configures cache coordination for a clustered environment. This needs to
     * be set on every persistence unit/session in the cluster. Depending on the
     * cache configuration for each descriptor, this will broadcast cache
     * updates or inserts to the cluster to update or invalidate each session's
     * cache.
     * <p>
     * Default: the cache is not coordinated.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>{@code jms}
     * <li>{@code jms-publishing}
     * <li>{@code rmi}
     * <li>{@code rmi-iiop}
     * <li>{@code jgroups}
     * <li>the fully qualified name for a class that extends {@link org.eclipse.persistence.sessions.coordination.TransportManager} abstract class.
     * </ul>
     *
     * @see CacheCoordinationProtocol
     * @see org.eclipse.persistence.annotations.Cache#coordinationType()
     * @see org.eclipse.persistence.sessions.coordination.RemoteCommandManager#setTransportManager(org.eclipse.persistence.sessions.coordination.TransportManager)
     * @see org.eclipse.persistence.sessions.coordination.TransportManager
     */
    public static final String COORDINATION_PROTOCOL = "eclipselink.cache.coordination.protocol";

    /**
     * The {@code eclipselink.cache.coordination.jgroups.config} property
     * configures cache coordination for a clustered environment.
     * <p>
     * Only used for JGroups coordination.
     * <p>
     * Sets the JGroups config XML file location.<br>
     * If not set the default JGroups config will be used.
     *
     * @see #COORDINATION_PROTOCOL
     * @see "org.eclipse.persistence.sessions.coordination.jgroups.JGroupsTransportManager#setConfigFile(String)"
     */
    public static final String COORDINATION_JGROUPS_CONFIG = "eclipselink.cache.coordination.jgroups.config";

    /**
     * The {@code eclipselink.cache.coordination.jms.host} property
     * configures cache coordination for a clustered environment.
     * <p>
     * Only used for JMS coordination.
     * <p>
     * Sets the URL for the JMS server hosting the topic.<br>
     * This is not required in the topic is distributed across the cluster (can be looked up in local JNDI).
     *
     * @see #COORDINATION_PROTOCOL
     * @see org.eclipse.persistence.sessions.coordination.jms.JMSTopicTransportManager#setTopicHostUrl(String)
     */
    public static final String COORDINATION_JMS_HOST = "eclipselink.cache.coordination.jms.host";

    /**
     * The {@code eclipselink.cache.coordination.jms.topic} property
     * configures cache coordination for a clustered environment.
     * <p>
     * Only used for JMS coordination.
     * <p>
     * Sets the JMS topic name.<br>
     * The default topic JNDI name is {@code jms/EclipseLinkTopic}.
     *
     * @see #COORDINATION_PROTOCOL
     * @see org.eclipse.persistence.sessions.coordination.broadcast.BroadcastTransportManager#setTopicName(String)
     */
    public static final String COORDINATION_JMS_TOPIC = "eclipselink.cache.coordination.jms.topic";

    /**
     * The {@code eclipselink.cache.coordination.jms.factory} property
     * configures cache coordination for a clustered environment.
     * <p>
     * Only used for JMS coordination.
     * <p>
     * Sets the JMS topic connection factory name.<br>
     * The default topic connection factory JNDI name is {@code jms/EclipseLinkTopicConnectionFactory}.
     *
     * @see #COORDINATION_PROTOCOL
     * @see org.eclipse.persistence.internal.sessions.factories.model.transport.JMSPublishingTransportManagerConfig#setTopicConnectionFactoryName(String)
     */
    public static final String COORDINATION_JMS_FACTORY = "eclipselink.cache.coordination.jms.factory";

    /**
     * The {@code eclipselink.cache.coordination.jms.reuse-topic-publisher} property
     * configures cache coordination for a clustered environment.
     * <p>
     * Only used for JMS coordination.
     * <p>
     * Sets the JSM transport manager to cache a TopicPublisher and reuse it for all cache coordination publishing.<br>
     * Default value if unset is false.
     *
     * @see #COORDINATION_PROTOCOL
     * @see org.eclipse.persistence.sessions.coordination.jms.JMSPublishingTransportManager#setShouldReuseJMSTopicPublisher(boolean)
     */
    public static final String COORDINATION_JMS_REUSE_PUBLISHER = "eclipselink.cache.coordination.jms.reuse-topic-publisher";

    /**
     * The {@code eclipselink.cache.coordination.rmi.announcement-delay}
     * property configures cache coordination for a clustered environment.
     * <p>
     * Only used for RMI coordination.
     * <p>
     * Sets the number of milliseconds to wait for announcements from other cluster members on startup.<br>
     * Default is 1000 milliseconds.
     *
     * @see #COORDINATION_PROTOCOL
     * @see org.eclipse.persistence.sessions.coordination.DiscoveryManager#setAnnouncementDelay(int)
     */
    public static final String COORDINATION_RMI_ANNOUNCEMENT_DELAY = "eclipselink.cache.coordination.rmi.announcement-delay";

    /**
     * The {@code eclipselink.cache.coordination.rmi.multicast-group}
     * property configures cache coordination for a clustered environment.
     * <p>
     * Only used for RMI coordination.
     * <p>
     * Sets the multicast socket group address. The multicast group is used to find other members
     * of the cluster.<br>
     * The default address is 239.192.0.0.
     *
     * @see #COORDINATION_PROTOCOL
     * @see org.eclipse.persistence.sessions.coordination.DiscoveryManager#setMulticastGroupAddress(String)
     */
    public static final String COORDINATION_RMI_MULTICAST_GROUP = "eclipselink.cache.coordination.rmi.multicast-group";

    /**
     * The {@code eclipselink.cache.coordination.rmi.multicast-group.port}
     * property configures cache coordination for a clustered environment.
     * <p>
     * Only used for RMI coordination.
     * <p>
     * Sets the multicast socket group port. The multicast group port is used to find other members
     * of the cluster.<br>
     * The default port is 3121.
     *
     * @see #COORDINATION_PROTOCOL
     * @see org.eclipse.persistence.sessions.coordination.DiscoveryManager#setMulticastPort(int)
     */
    public static final String COORDINATION_RMI_MULTICAST_GROUP_PORT = "eclipselink.cache.coordination.rmi.multicast-group.port";

    /**
     * The {@code eclipselink.cache.coordination.rmi.packet-time-to-live}
     * property configures cache coordination for a clustered environment.
     * <p>
     * Only used for RMI coordination.
     * <p>
     * Sets the multicast socket packet time to live.<br>
     * The multicast group is used to find other members of the cluster. Set the
     * number of hops the data packets of the session announcement will take
     * before expiring.<br>
     * The default is 2, a hub and an interface card to prevent
     * the data packets from leaving the local network.
     * <p>
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
     * The {@code eclipselink.cache.coordination.rmi.url} property
     * configures cache coordination for a clustered environment.
     * <p>
     * Only used for RMI coordination.
     * <p>
     * Sets the URL of the host server.<br>
     * This is the URL that other cluster member should use to connect to this host.
     * This may not be required in a clustered environment where JNDI is replicated.<br>
     * This can also be set as a System property or using a {@link org.eclipse.persistence.sessions.SessionCustomizer} to avoid
     * a separate persistence.xml per server.
     *
     * @see #COORDINATION_PROTOCOL
     * @see org.eclipse.persistence.sessions.coordination.RemoteCommandManager#setUrl(String)
     */
    public static final String COORDINATION_RMI_URL = "eclipselink.cache.coordination.rmi.url";

    /**
     * The {@code eclipselink.cache.coordination.naming-service} property
     * configures cache coordination for a clustered environment.
     * <p>
     * Set the naming service to use, either "jndi" or "rmi".
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>{@code jndi}
     * <li>{@code rmi}
     * </ul>
     *
     * @see #COORDINATION_PROTOCOL
     * @see org.eclipse.persistence.sessions.coordination.TransportManager#setNamingServiceType(int)
     */
    public static final String COORDINATION_NAMING_SERVICE = "eclipselink.cache.coordination.naming-service";

    /**
     * The {@code eclipselink.cache.coordination.jndi.user} property
     * configures cache coordination for a clustered environment.
     * <p>
     * Set the JNDI naming service user name.<br>
     * This is not normally require if connecting to the local server.
     *
     * @see #COORDINATION_PROTOCOL
     * @see org.eclipse.persistence.sessions.coordination.TransportManager#setUserName(String)
     */
    public static final String COORDINATION_JNDI_USER = "eclipselink.cache.coordination.jndi.user";

    /**
     * The {@code eclipselink.cache.coordination.jndi.password} property
     * configures cache coordination for a clustered environment.
     * <p>
     * Set the JNDI naming service user name.<br>
     * This is not normally require if connecting to the local server.
     *
     * @see #COORDINATION_PROTOCOL
     * @see org.eclipse.persistence.sessions.coordination.TransportManager#setPassword(String)
     */
    public static final String COORDINATION_JNDI_PASSWORD = "eclipselink.cache.coordination.jndi.password";

    /**
     * The {@code eclipselink.cache.coordination.jndi.initial-context-factory}
     * property configures cache coordination for a clustered environment.
     * <p>
     * Set the JNDI InitialContext factory.<br>
     * This is not normally require if connecting to the local server.
     *
     * @see #COORDINATION_PROTOCOL
     * @see org.eclipse.persistence.sessions.coordination.TransportManager#setInitialContextFactoryName(String)
     */
    public static final String COORDINATION_JNDI_CONTEXT = "eclipselink.cache.coordination.jndi.initial-context-factory";

    /**
     * The {@code eclipselink.cache.coordination.remove-connection-on-error}
     * property configures cache coordination for a clustered environment.
     * <p>
     * Set if the connection should be removed if a communication error occurs when
     * coordinating with it.<br>
     * This is normally used for RMI coordination in case
     * a server goes down (it will reconnect when it comes back up).
     *
     * @see #COORDINATION_PROTOCOL
     * @see org.eclipse.persistence.sessions.coordination.TransportManager#setShouldRemoveConnectionOnError(boolean)
     */
    public static final String COORDINATION_REMOVE_CONNECTION = "eclipselink.cache.coordination.remove-connection-on-error";

    /**
     * The {@code eclipselink.cache.coordination.propagate-asynchronously}
     * property configures cache coordination for a clustered environment.
     * <p>
     * Set if the coordination broadcast should occur asynchronously with the
     * committing thread. This means the coordination will be complete before
     * the thread returns from the commit of the transaction.<br>
     * Note that JMS is always asynchronous. By default, RMI is asynchronous.
     *
     * @see #COORDINATION_PROTOCOL
     * @see org.eclipse.persistence.sessions.coordination.RemoteCommandManager#setShouldPropagateAsynchronously(boolean)
     */
    public static final String COORDINATION_ASYNCH = "eclipselink.cache.coordination.propagate-asynchronously";

    /**
     * The {@code eclipselink.cache.coordination.thread.pool.size}
     * property configures thread pool size for cache coordination threads.
     * <p>
     * RMI cache coordination will spawn one thread per node to send change notifications.
     * RMI also spawns a thread to listen for new node notifications.
     * <p>
     * JMS cache coordination will spawn one thread to receive JMS change notification messages (unless MDB is used).
     * JMS also spawns a thread to process the change notification (unless MDB is used).
     * <p>
     * The default size is 32 threads.
     * <p>
     * A size of 0 indicates no thread pool should be used, and threads will be spawned when required.
     *
     * @see #COORDINATION_PROTOCOL
     * @see org.eclipse.persistence.platform.server.ServerPlatformBase#setThreadPoolSize(int)
     */
    public static final String COORDINATION_THREAD_POOL_SIZE = "eclipselink.cache.coordination.thread.pool.size";

    /**
     * The {@code eclipselink.cache.coordination.serializer} property
     * configures how cache coordination serializes message sent between nodes.
     * <p>
     * By default, Java serialization is used. Other serializer can be used for improved performance
     * or integration with other systems.
     * <p>
     * The full class name of the serializer class should be provided.
     *
     * @see #COORDINATION_PROTOCOL
     * @see org.eclipse.persistence.sessions.serializers.Serializer
     * @see org.eclipse.persistence.sessions.coordination.RemoteCommandManager#setSerializer(org.eclipse.persistence.sessions.serializers.Serializer)
     */
    public static final String COORDINATION_SERIALIZER = "eclipselink.cache.coordination.serializer";

    /**
     * The {@code eclipselink.cache.coordination.channel} property
     * configures cache coordination for a clustered environment.
     * <p>
     * Set the channel for this cluster. All server's in the same channel will be
     * coordinated.<br>
     * The default channel name is {@code EclipseLinkCommandChannel}.<br>
     * If multiple EclipseLink deployment reside on the same network, they should use different channels.
     *
     * @see #COORDINATION_PROTOCOL
     * @see org.eclipse.persistence.sessions.coordination.RemoteCommandManager#setChannel(String)
     */
    public static final String COORDINATION_CHANNEL = "eclipselink.cache.coordination.channel";

    /**
     * The {@code eclipselink.composite-unit} property indicates if it's a composite
     * persistence unit ({@code true}).
     * <p>
     * The property must be specified in persistence.xml of a composite persistence unit.
     * The property passed to createEntityManagerFactory method or in system properties is ignored.
     * <p>
     * Composite persistence unit would contain all persistence units found in jar files specified by
     * {@code <jar-file>} elements in persistence.xml.
     * <p>
     * <b>Persistence XML example:</b>
     * {@snippet lang="XML" :
     *  <jar-file>member1.jar</jar-file>
     *  <jar-file>member2.jar</jar-file>
     *  <properties>
     *    <property name="eclipselink.composite-unit" value="true"/>
     *  </properties>
     * }
     *
     * @see #COMPOSITE_UNIT_MEMBER
     * @see #COMPOSITE_UNIT_PROPERTIES
     */
    public static final String COMPOSITE_UNIT = "eclipselink.composite-unit";

    /**
     * The {@code eclipselink.composite-unit.member} property indicates if the persistence unit
     * must be a member of a composite persistence unit ({@code true}),
     * can't be used as an independent persistence unit.
     * That happens if persistence unit has dependencies on other persistence unit(s).
     * <p>
     * The property may be specified in persistence.xml.
     * The property passed to createEntityManagerFactory method or in system properties is ignored.
     * <p>
     * If this property is set to {@code true}, EntityManagerFactory still could be created,
     * but it can't be connected: an attempt to create entity manager would cause an exception.
     *
     * @see #COMPOSITE_UNIT
     * @see #COMPOSITE_UNIT_PROPERTIES
     */
    public static final String COMPOSITE_UNIT_MEMBER = "eclipselink.composite-unit.member";

    /**
     * The {@code eclipselink.composite-unit.properties} property may be passed
     * to createEntityManagerFactory method of a composite persistence unit to pass properties
     * to member persistence units.
     * <p>
     * The value is a map:<br>
     * &nbsp;&nbsp;the key is a member persistence unit's name,<br>
     * &nbsp;&nbsp;the value is a map of properties to be passed to this persistence unit.
     * <pre>
     * {@code "eclipselink.composite-unit.properties" -> (
     *    ("memberPu1" -> (
     *       "jakarta.persistence.jdbc.user" -> "user1",
     *       "jakarta.persistence.jdbc.password" -> "password1",
     *       "jakarta.persistence.jdbc.driver" -> "oracle.jdbc.OracleDriver",
     *       "jakarta.persistence.jdbc.url" -> "jdbc:oracle:thin:@oracle_db_url:1521:db"
     *    ),
     *    ("memberPu2" -> (
     *       "jakarta.persistence.jdbc.user" -> "user2",
     *       "jakarta.persistence.jdbc.password" -> "password2",
     *       "jakarta.persistence.jdbc.driver" -> "com.mysql.jdbc.Driver",
     *       "jakarta.persistence.jdbc.url" -> "jdbc:mysql://my_sql_db_url:3306/user2"
     *    )
     *  )}</pre>
     * @see #COMPOSITE_UNIT
     */
    public static final String COMPOSITE_UNIT_PROPERTIES = "eclipselink.composite-unit.properties";

    /**
     * The {@code eclipselink.remote.protocol} property
     * configures remote JPA for a client or server.
     * This allows JPA to be access over RMI or other protocol from a remote Java client.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>{@code rmi}
     * <li>the fully qualified name for a class that extends {@link org.eclipse.persistence.internal.sessions.remote.RemoteConnection} abstract class
     * </ul>
     *
     * @see RemoteProtocol
     * @see org.eclipse.persistence.internal.sessions.remote.RemoteConnection
     * @see org.eclipse.persistence.sessions.remote.RemoteSession
     */
    public static final String REMOTE_PROTOCOL = "eclipselink.remote.protocol";

    /**
     * The {@code eclipselink.remote.client.url} property
     * configures remote JPA for a client.
     * This allows JPA to be access over RMI or other protocol from a remote Java client.
     * <p>
     * The URL is the complete URL used to access the RMI server.
     *
     * @see #REMOTE_PROTOCOL
     */
    public static final String REMOTE_URL = "eclipselink.remote.client.url";

    /**
     * The {@code eclipselink.remote.server.name} property
     * configures remote JPA for a server.
     * This allows JPA to be access over RMI or other protocol from a remote Java client.
     * <p>
     * The name is the name the server will be registered under in the RMI registry.
     *
     * @see #REMOTE_PROTOCOL
     */
    public static final String REMOTE_SERVER_NAME = "eclipselink.remote.server.name";

    /**
     * The {@code eclipselink.nosql.connection-spec} property allows the connection information
     * for an NoSQL or EIS datasource to be specified.
     * <p>
     * An NoSQL datasource is a non-relational datasource such as a legacy database, NoSQL database,
     * XML database, transactional and messaging systems, or ERP systems.
     *
     * @see org.eclipse.persistence.eis.EISConnectionSpec
     * @see "org.eclipse.persistence.nosql.annotations.NoSql"
     */
    public static final String NOSQL_CONNECTION_SPEC = "eclipselink.nosql.connection-spec";

    /**
     * The {@code eclipselink.nosql.connection-factory} property allows
     * the JCA ConnectionFactory to be specified for a NoSQL or EIS adapter.
     * <p>
     * An NoSQL datasource is a non-relational datasource such as a legacy database, NoSQL database,
     * XML database, transactional and messaging systems, or ERP systems.
     *
     * @see jakarta.resource.cci.ConnectionFactory
     */
    public static final String NOSQL_CONNECTION_FACTORY = "eclipselink.nosql.connection-factory";

    /**
     * The {@code eclipselink.nosql.property.} property prefix allows setting NoSQL connection
     * properties. The NoSQL specific property name should be appended to this prefix.
     * <p>
     * i.e. "eclipselink.nosql.property.nosql.host"="localhost:5000"
     *
     * @see org.eclipse.persistence.eis.EISConnectionSpec
     * @see "org.eclipse.persistence.nosql.annotations.NoSql"
     */
    public static final String NOSQL_PROPERTY = "eclipselink.nosql.property.";

    /**
     * The {@code eclipselink.nosql.property.user} property specifies user name for NoSQL
     * connection.<br>
     * Note that {@code jakarta.persistence.jdbc.user} is also supported.
     *
     * @see org.eclipse.persistence.eis.EISConnectionSpec
     * @see "org.eclipse.persistence.nosql.annotations.NoSql"
     * @see #JDBC_USER
     */
    public static final String NOSQL_USER = "eclipselink.nosql.property.user";

    /**
     * The {@code eclipselink.nosql.property.password} property specifies password for NoSQL
     * connection.<br>
     * Note that {@code jakarta.persistence.jdbc.password} is also supported.
     *
     * @see org.eclipse.persistence.eis.EISConnectionSpec
     * @see "org.eclipse.persistence.nosql.annotations.NoSql"
     * @see #JDBC_PASSWORD
     */
    public static final String NOSQL_PASSWORD = "eclipselink.nosql.property.password";

    /**
     * The {@code eclipselink.jdbc.connector} property.<br>
     * Allows a custom connector to be used to define how to connect to the database.
     * This is not required if a DataSource or JDBC DriverManager is used.
     * It can be used to connect to a non-standard connection pool,
     * or provide additional customization in how a connection is obtained.
     *
     * @see org.eclipse.persistence.sessions.JNDIConnector
     * @see org.eclipse.persistence.sessions.DefaultConnector
     */
    public static final String JDBC_CONNECTOR = "eclipselink.jdbc.connector";

    /**
     * The {@code eclipselink.jdbc.property.} property allows
     * passing of JDBC driver specific connection properties.
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
     * The {@code eclipselink.jdbc.result-set-access-optimization} property allows to set
     * whether a query should by default use ResultSet Access optimization.
     * <p>
     * The optimization allows to avoid getting objects from ResultSet if the cached object used.
     * For instance, SELECT id, blob FROM .. with optimization would extract only "id" from ResultSet,
     * and if there is a corresponding cached object and it's not a refresh query, "blob" would never be extracted.
     * The drawback is keeping ResultSet and connection longer: until objects are built
     * (or extracted from cache) for all rows and all eager references (direct and nested) for each row.
     * Note that the optimization would not be used if it contradicts other query settings.
     * <p>
     * <b>Allowed Values</b> (String)<b>:</b>
     * <ul>
     * <li>{@code false} - don't use optimization
     * <li>{@code true} - use optimization
     * </ul>
     * <p>
     * Default value is {@code ObjectLevelReadQuery.isResultSetAccessOptimizedQueryDefault = false;}
     *
     * @see org.eclipse.persistence.internal.sessions.AbstractSession#setShouldOptimizeResultSetAccess(boolean)
     * @see org.eclipse.persistence.queries.ObjectLevelReadQuery#setIsResultSetAccessOptimizedQuery(boolean)
     */
    public static final String JDBC_RESULT_SET_ACCESS_OPTIMIZATION = "eclipselink.jdbc.result-set-access-optimization";

    /**
     * The {@code eclipselink.serializer} property specifies class name for session serializer
     * (must implement {@link org.eclipse.persistence.sessions.serializers.Serializer})
     * <p>
     * Default value is {@code org.eclipse.persistence.sessions.serializers.JavaSerializer}
     *
     * @see org.eclipse.persistence.internal.sessions.AbstractSession#setSerializer(org.eclipse.persistence.sessions.serializers.Serializer)
     * @see org.eclipse.persistence.sessions.serializers.JavaSerializer
     * @see org.eclipse.persistence.sessions.serializers.Serializer
     */
    public static final String SERIALIZER = "eclipselink.serializer";

    /**
     * The {@code eclipselink.tolerate-invalid-jpql} property allows an
     * {@code EntityManager} to be created even in the event that an application
     * has invalid JPQL statements declared in annotations or xml.
     * <p>
     * <b>Allowed Values</b> (String)<b>:</b>
     * <ul>
     * <li>{@code false} (DEFAULT)
     * <li>{@code true}
     * </ul>
     */
    public static final String JPQL_TOLERATE = "eclipselink.tolerate-invalid-jpql";

    /**
     * The {@code eclipselink.locking.timestamp.local} property defines if locking policies
     * should default to local time(true) or server time(false).
     * <p>
     * <b>Allowed Values</b> (String)<b>:</b>
     * <ul>
     * <li>{@code false} (DEFAULT)
     * <li>{@code true}
     * </ul>
     */
    public static final String USE_LOCAL_TIMESTAMP = "eclipselink.locking.timestamp.local." + PersistenceUnitProperties.DEFAULT;

    /**
     * The {@code eclipselink.jpa.sqlcall.deferral.default} property defines if SQL calls should be deferred to end of
     * transaction by default or not. When setting this property to {@code false}, the application assumes the responsibility
     * of ordering the SQL statements and must therefore be aware of any interdependencies between entities.
     * <p>
     * <b>Allowed Values</b> (String)<b>:</b>
     * <ul>
     * <li>{@code true} (DEFAULT)
     * <li>{@code false}
     * </ul>
     */
    public static final String SQL_CALL_DEFERRAL = "eclipselink.jpa.sql-call-deferral";

    /**
     * The {@code eclipselink.jpa.naming_into_indexed} property defines if stored procedure parameters passed by name
     * should be transformed into positional/index based passing if property value will be {@code true}. e.g.
     * For stored procedure:
     * {@code CREATE PROCEDURE test_stored_proc1( IN param1 TEXT, IN param2 INTEGER )}
     * following Java call
     * {@code query.registerStoredProcedureParameter( "param1",Integer.class,ParameterMode.IN );}
     * {@code query.registerStoredProcedureParameter( "param2",String.class,ParameterMode.IN );}
     * will be transformed into following e.g.
     * {@code {call test_stored_proc1(10, 'abcd')}}
     * instead of default
     * {@code {call test_stored_proc1(param1 ={@literal >} 10, param2 ={@literal >} 'abcd')}}
     * It's important to register parameters in Java in a same order as they specified in the stored procedure.
     * This code was added there to ensure backward compatibility with older EclipseLink releases.
     * <p>
     * <b>Allowed Values</b> (String)<b>:</b>
     * <ul>
     * <li>{@code false} (DEFAULT)
     * <li>{@code true}
     * </ul>
     */
    public static final String NAMING_INTO_INDEXED = "eclipselink.jpa.naming_into_indexed";

    /**
     * This system property in milliseconds can control thread management in org.eclipse.persistence.internal.helper.ConcurrencyManager.
     * It controls how much time loop wait before it tries to acquire lock for current thread again. If the value is set above 0 deadlock detection
     * mechanism and related extended logging will be activated.
     * Default value is 0 (unit is ms). Allowed values are: long
     */
    public static final String CONCURRENCY_MANAGER_ACQUIRE_WAIT_TIME = "eclipselink.concurrency.manager.waittime";

    /**
     * This system property in milliseconds can control thread management in org.eclipse.persistence.internal.helper.ConcurrencyManager.
     * It controls how much time ConcurrencyManager will wait before it will identify, that thread which builds new object/entity instance
     * should be identified as a potential deadlock source. It leads into some additional log messages.
     * Default value is 0 (unit is ms). In this case extended logging is not active. Allowed values are: long
     */
    public static final String CONCURRENCY_MANAGER_BUILD_OBJECT_COMPLETE_WAIT_TIME = "eclipselink.concurrency.manager.build.object.complete.waittime";

    /**
     * This system property in milliseconds can control thread management in org.eclipse.persistence.internal.helper.ConcurrencyManager.
     * It controls how long we are willing to wait before firing up an exception
     * Default value is 40000 (unit is ms). Allowed values are: long
     */
    public static final String CONCURRENCY_MANAGER_MAX_SLEEP_TIME  = "eclipselink.concurrency.manager.maxsleeptime";

    /**
     * This system property in milliseconds can control thread management in org.eclipse.persistence.internal.helper.ConcurrencyManager and org.eclipse.persistence.internal.helper.ConcurrencyUtil.
     * It controls how frequently the tiny dump log message is created.
     * Default value is 40000 (unit is ms). Allowed values are: long
     */
    public static final String CONCURRENCY_MANAGER_MAX_FREQUENCY_DUMP_TINY_MESSAGE = "eclipselink.concurrency.manager.maxfrequencytodumptinymessage";

    /**
     * This system property in milliseconds can control thread management in org.eclipse.persistence.internal.helper.ConcurrencyManager and org.eclipse.persistence.internal.helper.ConcurrencyUtil.
     * It controls how frequently the massive dump log message is created.
     * Default value is 60000 (unit is ms). Allowed values are: long
     */
    public static final String CONCURRENCY_MANAGER_MAX_FREQUENCY_DUMP_MASSIVE_MESSAGE  = "eclipselink.concurrency.manager.maxfrequencytodumpmassivemessage";

    /**
     * <p>
     * This property control (enable/disable) if {@code InterruptedException} fired when dead-lock diagnostic is enabled.
     * <p>
     * <b>Allowed Values</b> (case-sensitive String)<b>:</b>
     * <ul>
     * <li>{@code false} - if aborting frozen thread is not effective it is preferable to not fire the interrupted exception let the system
     * In the places where use this property normally if a thread is stuck it is because it is doing object building.
     * Blowing the threads ups is not that dangerous. It can be very dangerous for production if the dead lock ends up
     * not being resolved because the productive business transactions will become cancelled if the application has a
     * limited number of retries to for example process an MDB. However, the code spots where we use this constant are
     * not as sensible as when the write lock manager is starving to run commit.
     * <li>{@code true} (DEFAULT) - if we want the to fire up an exception to try to get the current thread to release all of its acquired locks and allow other
     * threads to progress.
     * </ul>
     */
    public static final String CONCURRENCY_MANAGER_ALLOW_INTERRUPTED_EXCEPTION  = "eclipselink.concurrency.manager.allow.interruptedexception";

    /**
     * <p>
     * This property control in {@link org.eclipse.persistence.internal.sessions.AbstractSession#getCacheKeyFromTargetSessionForMerge(java.lang.Object, org.eclipse.persistence.internal.descriptors.ObjectBuilder, org.eclipse.persistence.descriptors.ClassDescriptor, org.eclipse.persistence.internal.sessions.MergeManager)}
     * strategy how {@code org.eclipse.persistence.internal.identitymaps.CacheKey} will be fetched from shared cache.
     * <p>
     * <b>Allowed Values</b> (case-sensitive String)<b>:</b>
     * <ul>
     * <li>{@code ORIGIN} (DEFAULT) - There is infinite {@linkplain java.lang.Object#wait()} call in case of some conditions during time when object/entity referred from
     * {@code org.eclipse.persistence.internal.identitymaps.CacheKey} is locked and modified by another thread. In some cases it should leads into deadlock.
     * <li>{@code WAITLOOP} - Merge manager will try in the loop with timeout wait {@code cacheKey.wait(ConcurrencyUtil.SINGLETON.getAcquireWaitTime());}
     * fetch object/entity from {@linkplain org.eclipse.persistence.internal.identitymaps.CacheKey}. If fetch will be successful object/entity loop finish and continue
     * with remaining code. If not @{code java.lang.InterruptedException} is thrown and caught and used {@linkplain org.eclipse.persistence.internal.identitymaps.CacheKey} instance
     * status is set into invalidation state. This strategy avoid deadlock issue, but there should be impact to the performance.
     * </ul>
     */
    public static final String CONCURRENCY_MANAGER_ALLOW_GET_CACHE_KEY_FOR_MERGE_MODE  = "eclipselink.concurrency.manager.allow.getcachekeyformerge.mode";

    /**
     * <p>
     * This property control (enable/disable) if {@code ConcurrencyException} fired when dead-lock diagnostic is enabled.
     * <p>
     * <b>Allowed Values</b> (case-sensitive String)<b>:</b>
     * <ul>
     * <li>{@code false} - if aborting frozen thread is not effective it is preferable to not fire the concurrency exception let the system
     * freeze and die and force the administration to kill the server. This is preferable to aborting the transactions
     * multiple times without success in resolving the dead lock and having business critical messages that after 3 JMS
     * retries are discarded out. Failing to resolve a dead lock can have terrible impact in system recovery unless we
     * have infinite retries for the business transactions.
     * <li>{@code true} (DEFAULT) - if we want the to fire up an exception to try to get the current thread to release all of its acquired
     * locks and allow other threads to progress.
     * </ul>
     */
    public static final String CONCURRENCY_MANAGER_ALLOW_CONCURRENCY_EXCEPTION  = "eclipselink.concurrency.manager.allow.concurrencyexception";

    /**
     * <p>
     * This property control (enable/disable) collection debug/trace information during ReadLock acquisition, when dead-lock diagnostic is enabled.
     * <p>
     * <b>Allowed Values</b> (case-sensitive String)<b>:</b>
     * <ul>
     * <li>{@code false} (DEFAULT) - don't collect debug/trace information during ReadLock acquisition
     * <li>{@code true} - collect debug/trace information during ReadLock acquisition. Has negative impact to the performance.
     * </ul>
     */
    public static final String CONCURRENCY_MANAGER_ALLOW_STACK_TRACE_READ_LOCK = "eclipselink.concurrency.manager.allow.readlockstacktrace";

    /**
     * <p>
     * This property control (enable/disable) semaphore in {@link org.eclipse.persistence.internal.descriptors.ObjectBuilder}
     * </p>
     * Object building see {@link org.eclipse.persistence.internal.descriptors.ObjectBuilder} could be one of the
     * primary sources pressure on concurrency manager. Most of the cache key acquisition and releasing is taking place during object building.
     * Enable {@code true} this property to try reduce the likelihood of having dead locks is to allow less threads to start object
     * building in parallel. In this case there should be negative impact to the performance.
     * Note: Parallel access to the same entity/entity tree from different threads is not recommended technique in EclipseLink.
     * <ul>
     * <li>{@code true} - means we want to override vanilla behavior and use a semaphore to not allow too many
     * threads in parallel to do object building
     * <li>{@code false} (DEFAULT) - means just go ahead and try to build the object without any semaphore (false is
     * vanilla behavior).
     * </ul>
     */
    public static final String CONCURRENCY_MANAGER_USE_SEMAPHORE_TO_SLOW_DOWN_OBJECT_BUILDING = "eclipselink.concurrency.manager.object.building.semaphore";

    /**
     * <p>
     * This property control (enable/disable) semaphore in {@link org.eclipse.persistence.internal.helper.WriteLockManager#acquireRequiredLocks}
     * </p>
     * This algorithm
     * {@link org.eclipse.persistence.internal.helper.WriteLockManager#acquireRequiredLocks}
     * is being used when a transaction is committing and it is acquire locks to merge the change set.
     * It should happen if algorithm has trouble when multiple threads report change sets on the same entity (e.g.
     * one-to-many relations of master detail being enriched with more details on this master).
     * Note: Parallel access to the same entity/entity tree from different threads is not recommended technique in EclipseLink.
     * <ul>
     * <li>{@code true} - means we want to override vanilla behavior and use a semaphore to not allow too many
     * threads. In this case there should be negative impact to the performance.
     * <li>{@code false} (DEFAULT) - means just go ahead and try to build the object without any semaphore (false is
     * vanilla behavior).
     * </ul>
     */
    public static final String CONCURRENCY_MANAGER_USE_SEMAPHORE_TO_SLOW_DOWN_WRITE_LOCK_MANAGER_ACQUIRE_REQUIRED_LOCKS = "eclipselink.concurrency.manager.write.lock.manager.semaphore";

    /**
     * <p>
     * This property control number of threads in semaphore in {@link org.eclipse.persistence.internal.descriptors.ObjectBuilder}
     * If "eclipselink.concurrency.manager.object.building.semaphore" property is {@code true} default value is 10. Allowed values are: int
     * If "eclipselink.concurrency.manager.object.building.semaphore" property is {@code false} (DEFAULT) number of threads is unlimited.
     * </p>
     */
    public static final String CONCURRENCY_MANAGER_OBJECT_BUILDING_NO_THREADS = "eclipselink.concurrency.manager.object.building.no.threads";

    /**
     * <p>
     * This property control number of threads in semaphore in {@link org.eclipse.persistence.internal.helper.WriteLockManager#acquireRequiredLocks}
     * If "eclipselink.concurrency.manager.write.lock.manager.semaphore" property is {@code true} default value is 2. Allowed values are: int
     * If "eclipselink.concurrency.manager.write.lock.manager.semaphore" property is {@code false} (DEFAULT) number of threads is unlimited.
     * </p>
     */
    public static final String CONCURRENCY_MANAGER_WRITE_LOCK_MANAGER_ACQUIRE_REQUIRED_LOCKS_NO_THREADS = "eclipselink.concurrency.manager.write.lock.manager.no.threads";

    /**
     * <p>
     * This property control semaphore the maximum time to wait for a permit in {@link org.eclipse.persistence.internal.helper.ConcurrencySemaphore#acquireSemaphoreIfAppropriate(boolean)}
     * It's passed to {@link java.util.concurrent.Semaphore#tryAcquire(long, TimeUnit)}
     * Default value is 2000 (unit is ms). Allowed values are: long
     * </p>
     */
    public static final String CONCURRENCY_SEMAPHORE_MAX_TIME_PERMIT = "eclipselink.concurrency.semaphore.max.time.permit";

    /**
     * <p>
     * This property control timeout between log messages in {@link org.eclipse.persistence.internal.helper.ConcurrencySemaphore#acquireSemaphoreIfAppropriate(boolean)} when method/thread tries to get permit for the execution.
     * Default value is 10000 (unit is ms). Allowed values are: long
     * </p>
     */
    public static final String CONCURRENCY_SEMAPHORE_LOG_TIMEOUT = "eclipselink.concurrency.semaphore.log.timeout";

    /**
     * <p>
     * This property control (enable/disable) query result cache validation in {@link org.eclipse.persistence.internal.sessions.UnitOfWorkImpl#internalExecuteQuery}
     * </p>
     * This can be used to help debugging an object identity problem. An object identity problem is when an managed/active entity in the cache references an entity not in managed state.
     * This method will validate that objects in query results (object tree) are in a correct state. As a result there are new log messages in the log.
     * It's related with "read" queries like {@code em.find(...);} or JPQL queries like {@code SELECT e FROM Entity e}.
     * It should be controlled at query level too by query hint {@link org.eclipse.persistence.config.QueryHints#QUERY_RESULTS_CACHE_VALIDATION}
     * <ul>
     * <li>{@code true} - validate query result object tree and if content is not valid print diagnostic messages. In this case there should be negative impact to the performance.
     * <li>{@code false} (DEFAULT) - don't validate and print any diagnostic messages
     * </ul>
     */
    public static final String QUERY_RESULTS_CACHE_VALIDATION = "eclipselink.query-results-cache.validation";

    /**
     * The {@code eclipselink.login.encryptor} property configures a custom implementation of
     * {@link org.eclipse.persistence.security.Securable} class used to encrypt and decrypt database password
     * loaded from {@code jakarta.persistence.jdbc.password} property.
     * Usage of this property avoids limitation of {@link org.eclipse.persistence.sessions.SessionCustomizer} which is called when all other
     * properties have been processed (too late when database login needs to be configured).
     * If this property is not specified {@link org.eclipse.persistence.internal.security.JCEEncryptor} as a default encryptor is used.
     * <p>
     * <b>Allowed Values:</b>
     * <ul>
     * <li>the fully qualified name for a class that implements {@link org.eclipse.persistence.security.Securable} interface
     * </ul>
     *
     * @see org.eclipse.persistence.security.Securable
     * @see org.eclipse.persistence.internal.security.JCEEncryptor
     */
    public static final String LOGIN_ENCRYPTOR = "eclipselink.login.encryptor";


    /**
     * INTERNAL: The following properties will not be displayed through logging
     * but instead have an alternate value shown in the log.
     */
    public static final Map<String, String> PROPERTY_LOG_OVERRIDES = Map.of(
            JDBC_PASSWORD, "xxxxxx"
    );

    /**
     * INTERNAL: Return the overridden log string.
     *
     * @param propertyName property which value should be overridden in the log
     * @return the overridden log string
     */
    public static String getOverriddenLogStringForProperty(String propertyName) {
        return PROPERTY_LOG_OVERRIDES.get(propertyName);
    }

    /**
     * INTERNAL: The following properties passed to
     * {@code Persistence#createEntityManagerFactory(String, Map)} cached and
     * processed on the {@code EntityManagerFactory} directly. None of these
     * properties processed during pre-deploy or deploy.
     **/
    private static final Set<String> supportedNonServerSessionProperties = Set.of(
            JOIN_EXISTING_TRANSACTION,
            PERSISTENCE_CONTEXT_REFERENCE_MODE,
            PERSISTENCE_CONTEXT_FLUSH_MODE,
            PERSISTENCE_CONTEXT_CLOSE_ON_COMMIT,
            PERSISTENCE_CONTEXT_PERSIST_ON_COMMIT,
            PERSISTENCE_CONTEXT_COMMIT_WITHOUT_PERSIST_RULES,
            VALIDATE_EXISTENCE,
            ORDER_UPDATES,
            FLUSH_CLEAR_CACHE);

    public static Set<String> getSupportedNonServerSessionProperties() {
        return supportedNonServerSessionProperties;
    }

    private PersistenceUnitProperties() {
        // no instance please
    }
}
