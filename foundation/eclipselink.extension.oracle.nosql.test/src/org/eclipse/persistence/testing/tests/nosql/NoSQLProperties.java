/*
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     03/30/2016-2.7 Tomas Kraus
//       - 490677: Initial API and implementation.
package org.eclipse.persistence.testing.tests.nosql;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.eis.EISLogin;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.nosql.adapters.nosql.OracleNoSQLConnectionSpec;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCaseHelper;

/**
 * NoSQL test suite properties.
 */
public class NoSQLProperties {

    /** Logger. */
    private static final SessionLog LOG = AbstractSessionLog.getLog();

    /** NoSQL database URL property prefix. */
    private static final String NOSQL_URL_KEY_PREFIX = "nosql.url";

    /** NoSQL database URL build property name. */
    public static final String NOSQL_URL_KEY = JUnitTestCaseHelper.insertIndex(NOSQL_URL_KEY_PREFIX, null);

    /** Relational database URL build property name. */
    private static final String DB_URL_KEY = JUnitTestCaseHelper.insertIndex(JUnitTestCaseHelper.DB_URL_KEY, null);
    /** Relational database user name build property name. */
    private static final String DB_USER_KEY = JUnitTestCaseHelper.insertIndex(JUnitTestCaseHelper.DB_USER_KEY, null);
    /** Relational database user password build property name. */
    private static final String DB_PWD_KEY = JUnitTestCaseHelper.insertIndex(JUnitTestCaseHelper.DB_PWD_KEY, null);
    /** Relational database driver class build property name. */
    private static final String DB_DRIVER_KEY
            = JUnitTestCaseHelper.insertIndex(JUnitTestCaseHelper.DB_DRIVER_KEY, null);
    /** Relational database platform class build property name. */
    private static final String DB_PLATFORM_KEY
            = JUnitTestCaseHelper.insertIndex(JUnitTestCaseHelper.DB_PLATFORM_KEY, null);

    // NoSQL related persistence unit property names.

    /** The persistence unit property key for the MongoDB connection host name or IP. */
    public static final String PROPERTY_NOSQL_HOST_KEY = PersistenceUnitProperties.NOSQL_PROPERTY + "nosql.host";
    /** The persistence unit property key for the MongoDB connection port. */
    public static final String PROPERTY_NOSQL_PORT_KEY = PersistenceUnitProperties.NOSQL_PROPERTY + "nosql.port";
    /** The persistence unit property key for the MongoDB database name. */
    public static final String PROPERTY_NOSQL_STORE_KEY = PersistenceUnitProperties.NOSQL_PROPERTY + "nosql.store";

    // Default properties values if not set.

    /** Default NoSQL database host. */
    private static final String DEFAULT_HOST = "localhost";
    /** Default NoSQL database store. */
    private static final String DEFAULT_STORE = "kvstore";


    /** Database PU properties {@link Map} from the build properties. Shared for whole test suite. */
    public static final Map<String, String> properties = buildProperties();

    /**
     * Get database URL from configuration properties.
     * @return Database URL.
     */
    public static String getDBURL() {
        return properties.get(PersistenceUnitProperties.JDBC_URL);
    }

    /**
     * Get database user name from configuration properties.
     * @return Database user name.
     */
    public static String getDBUserName() {
        return properties.get(PersistenceUnitProperties.JDBC_USER);
    }

    /**
     * Get database user password from configuration properties.
     * @return Database user password.
     */
    public static String getDBPassword() {
        return properties.get(PersistenceUnitProperties.JDBC_PASSWORD);
    }

    /**
     * Get database JDBC driver from configuration properties.
     * @return Database JDBC driver.
     */
    public static String getDBDriver() {
        return properties.get(PersistenceUnitProperties.JDBC_DRIVER);
    }

    /**
     * Get database platform class name from configuration properties.
     * @return Database platform class name.
     */
    public static String getDBPlatform() {
        return properties.get(PersistenceUnitProperties.TARGET_DATABASE);
    }

    /**
     * Get NoSQL database URL from configuration properties.
     * @return NoSQL database URL.
     */
    public static String getNoSQLURL() {
        final String host = properties.get(PROPERTY_NOSQL_HOST_KEY);
        final String port = properties.get(PROPERTY_NOSQL_PORT_KEY);
        final String store = properties.get(PROPERTY_NOSQL_STORE_KEY);
        final StringBuilder sb = new StringBuilder((host != null ? host.length() : 0)
                + (port != null ? port.length() + 1 : 0) + (store != null ? store.length() : 0)
                + NoSQLURI.KEYWORD.length() + 1);
        sb.append(NoSQLURI.KEYWORD);
        if (host != null) {
            sb.append(host);
        }
        if (port != null) {
            sb.append(':');
            sb.append(port);
        }
        sb.append('/');
        if (store != null) {
            sb.append(store);
        }
        return sb.toString();
    }

    /**
     * Process and add {@code nosql.url} property.
     * @param properties Test properties {@link Map} being built.
     */
    private static void processNoSqlUrl(final Map<String, String> properties) {
        final String noSqlUrl = JUnitTestCaseHelper.getProperty(NOSQL_URL_KEY);
        if (noSqlUrl != null && NoSQLURI.startsUri(noSqlUrl)) {
            final NoSQLURI uri = new NoSQLURI(noSqlUrl);
            final String host = uri.getHost();
            final int port = uri.getPort();
            final String store = uri.getStore();
            if (host != null) {
                properties.put(PROPERTY_NOSQL_HOST_KEY, host);
            } else {
                properties.put(PROPERTY_NOSQL_HOST_KEY, DEFAULT_HOST);
            }
            if (port >= 0) {
                properties.put(PROPERTY_NOSQL_PORT_KEY, Integer.toString(port));
            }
            if (store != null) {
                properties.put(PROPERTY_NOSQL_STORE_KEY, store);
            } else {
                properties.put(PROPERTY_NOSQL_STORE_KEY, DEFAULT_STORE);
            }
        }
    }

    /**
     * Process and add simple build property.
     * @param properties Test properties {@link Map} being built.
     * @param buildKey   Property key in build properties.
     * @param puKey      Property key in jUnit test.
     */
    private static void processProperty(
            final Map<String, String> properties, final String buildKey, final String testKey) {
        final String value = JUnitTestCaseHelper.getProperty(buildKey);
        LOG.log(SessionLog.FINER,
                String.format("Property %s -> %s :: %s", buildKey, testKey, value != null ? value : "N/A"));
        if (value != null) {
            properties.put(testKey, value);
        }
    }

    /**
     * Create test properties {@link Map} from build properties.
     * @return Persistence unit properties {@link Map}.
     */
    private static Map<String, String> buildProperties() {
        final Map<String, String> properties = new HashMap<>();
        processNoSqlUrl(properties);
        processProperty(properties, DB_URL_KEY, PersistenceUnitProperties.JDBC_URL);
        processProperty(properties, DB_USER_KEY, PersistenceUnitProperties.JDBC_USER);
        processProperty(properties, DB_PWD_KEY, PersistenceUnitProperties.JDBC_PASSWORD);
        processProperty(properties, DB_DRIVER_KEY, PersistenceUnitProperties.JDBC_DRIVER);
        processProperty(properties, DB_PLATFORM_KEY, PersistenceUnitProperties.TARGET_DATABASE);
        return properties;
    }

    /**
     * Build NoSQL connection property containing host and port concatenated.
     * @param host NoSQL connection host property.
     * @param port NoSQL connection port property.
     * @return Property containing host and port concatenated.
     */
    private static String buildHostPort(final String host, final String port) {
        if (host != null) {
            final StringBuilder hostPort = new StringBuilder(host.length() + (port != null ? port.length() + 1 : 0));
            hostPort.append(host);
            if (port != null) {
                hostPort.append(':');
                hostPort.append(port);
            }
            return hostPort.toString();
        } else {
            return null;
        }
    }

    /**
     * Build NoSQL mapped connection property containing host and port concatenated.
     * @param host NoSQL connection host property.
     * @param port NoSQL connection port property.
     * @return Property containing host and port concatenated.
     */
    private static String buildMappedHostPort(final String host, final String port) {
        if (host != null) {
            final StringBuilder hostPort = new StringBuilder(
                    2 * host.length() + (port != null ? 2 * port.length() + 3 : 1));
            hostPort.append(host);
            if (port != null) {
                hostPort.append(':');
                hostPort.append(port);
            }
            hostPort.append(',');
            hostPort.append(host);
            if (port != null) {
                hostPort.append(':');
                hostPort.append(port);
            }
            return hostPort.toString();
        } else {
            return null;
        }
    }

    /**
     * Set EIS login connection information for NoSQL database.
     * @param login      Target {@link EISLogin} instance.
     * @param properties Persistence unit properties {@link Map}.
     */
    public static void setEISLoginProperties(final EISLogin login) {
        final String hostPort = buildHostPort(
                properties.get(PROPERTY_NOSQL_HOST_KEY), properties.get(PROPERTY_NOSQL_PORT_KEY));
        final String store = properties.get(PROPERTY_NOSQL_STORE_KEY);
        LOG.log(SessionLog.FINE, String.format("NoSQL connection: NoSQL://%s/%s", hostPort, store));
        if (hostPort != null) {
            login.setProperty(OracleNoSQLConnectionSpec.HOST, hostPort);
        }
        if (store != null) {
            login.setProperty(OracleNoSQLConnectionSpec.STORE, store);
        }
    }

    /**
     * Build EIS login connection information for {@code EntityManager} NoSQL database connection.
     * Host and port pair property is normal (not mapped).
     * @return {@code EntityManager} NoSQL database connection properties.
     */
    public static Map<String, String> createEMProperties() {
        return createEMProperties(false);
    }

    /**
     * Build EIS login connection information for {@code EntityManager} NoSQL database connection.
     * @param mapped Build mapped property for the same host and port pair when {@code true} or normal property
     *               otherwise.
     * @return {@code EntityManager} NoSQL database connection properties.
     */
    public static Map<String, String> createEMProperties(final boolean mapped) {
        final String host = properties.get(PROPERTY_NOSQL_HOST_KEY);
        final String port = properties.get(PROPERTY_NOSQL_PORT_KEY);
        final String store = properties.get(PROPERTY_NOSQL_STORE_KEY);
        final String hostPort = mapped ? buildMappedHostPort(host, port) : buildHostPort(host, port);
        final Map<String, String> emProperties = new HashMap<>(2);
        LOG.log(SessionLog.FINE, String.format("NoSQL connection: NoSQL://%s/%s", hostPort, store));
        if (hostPort != null) {
            emProperties.put(PROPERTY_NOSQL_HOST_KEY, hostPort);
        }
        if (store != null) {
            emProperties.put(PROPERTY_NOSQL_STORE_KEY, store);
        }
        return emProperties;
    }

}
