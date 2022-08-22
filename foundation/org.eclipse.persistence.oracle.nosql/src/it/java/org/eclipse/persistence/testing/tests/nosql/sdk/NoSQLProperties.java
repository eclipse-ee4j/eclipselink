/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
package org.eclipse.persistence.testing.tests.nosql.sdk;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.eis.EISLogin;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.nosql.adapters.sdk.OracleNoSQLConnectionSpec;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCaseHelper;

/**
 * NoSQL test suite properties.
 */
public class NoSQLProperties {

    /** Logger. */
    private static final SessionLog LOG = AbstractSessionLog.getLog();

    /** NoSQL database service build property name. */
    private static final String NOSQL_BUILD_PROPERTY_PREFIX = "nosql.sdk.";
    public static final String NOSQL_SERVICE_BUILD_KEY = JUnitTestCaseHelper.insertIndex(NOSQL_BUILD_PROPERTY_PREFIX + "service", null);
    public static final String NOSQL_ENDPOINT_BUILD_KEY = JUnitTestCaseHelper.insertIndex(NOSQL_BUILD_PROPERTY_PREFIX + "endpoint", null);
    public static final String NOSQL_COMPARTMENT_BUILD_KEY = JUnitTestCaseHelper.insertIndex(NOSQL_BUILD_PROPERTY_PREFIX + "compartment", null);
    public static final String NOSQL_AUTH_PRINCIPAL_BUILD_KEY = JUnitTestCaseHelper.insertIndex(NOSQL_BUILD_PROPERTY_PREFIX + "authprincipal", null);

    /** Relational database platform class build property name. */
    private static final String DB_PLATFORM_KEY = JUnitTestCaseHelper.insertIndex(JUnitTestCaseHelper.DB_PLATFORM_KEY, null);

    // NoSQL related persistence unit property names.
    /** The persistence unit property key for the Oracle NoSQL service (cloud | onprem | cloudsim). */
    public static final String NOSQL_SERVICE_PROPERTY_KEY = PersistenceUnitProperties.NOSQL_PROPERTY + "nosql.service";
    /** The persistence unit property key for the Oracle NoSQL endpoint (e.g. eu-frankfurt-1 (for cloud) | http://localhost:8080 (for onprem) | http://localhost:8080 (for cloudsim)). */
    public static final String NOSQL_ENDPOINT_PROPERTY_KEY = PersistenceUnitProperties.NOSQL_PROPERTY + "nosql.endpoint";
    /** The persistence unit property key for the Oracle NoSQL compartment. */
    public static final String NOSQL_COMPARTMENT_PROPERTY_KEY = PersistenceUnitProperties.NOSQL_PROPERTY + "nosql.compartment";
    /** The persistence unit property key for the Oracle NoSQL authorization principal (user | instance | resource). */
    public static final String NOSQL_AUTH_PRINCIPAL_PROPERTY_KEY = PersistenceUnitProperties.NOSQL_PROPERTY + "nosql.authprincipal";


    /** Database PU properties {@link Map} from the build properties. Shared for whole test suite. */
    public static final Map<String, String> properties = buildProperties();


    /**
     * Get database platform class name from configuration properties.
     * @return Database platform class name.
     */
    public static String getDBPlatform() {
        return properties.get(PersistenceUnitProperties.TARGET_DATABASE);
    }

    /**
     * Process and add simple build property.
     * @param properties Test properties {@link Map} being built.
     * @param buildKey   Property key in build properties.
     * @param testKey      Property key in jUnit test.
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
        processProperty(properties, NOSQL_SERVICE_BUILD_KEY, NOSQL_SERVICE_PROPERTY_KEY);
        processProperty(properties, NOSQL_ENDPOINT_BUILD_KEY, NOSQL_ENDPOINT_PROPERTY_KEY);
        processProperty(properties, NOSQL_COMPARTMENT_BUILD_KEY, NOSQL_COMPARTMENT_PROPERTY_KEY);
        processProperty(properties, NOSQL_AUTH_PRINCIPAL_BUILD_KEY, NOSQL_AUTH_PRINCIPAL_PROPERTY_KEY);
        processProperty(properties, DB_PLATFORM_KEY, PersistenceUnitProperties.TARGET_DATABASE);
        return properties;
    }

    /**
     * Set EIS login connection information for NoSQL database.
     * @param login      Target {@link EISLogin} instance.
     */
    public static void setEISLoginProperties(final EISLogin login) {
        final String service = properties.get(NOSQL_SERVICE_PROPERTY_KEY);
        final String endpoint = properties.get(NOSQL_ENDPOINT_PROPERTY_KEY);
        final String compartment = properties.get(NOSQL_COMPARTMENT_PROPERTY_KEY);
        final String authPrincipal = properties.get(NOSQL_AUTH_PRINCIPAL_PROPERTY_KEY);
        LOG.log(SessionLog.FINE, String.format("NoSQL service:%s\tendpoint:%s\tcompartment:%s\tauthorization principal:%s", service, endpoint, compartment, authPrincipal));
        if (service != null) {
            login.setProperty(OracleNoSQLConnectionSpec.SERVICE, service);
        }
        if (endpoint != null) {
            login.setProperty(OracleNoSQLConnectionSpec.END_POINT, endpoint);
        }
        if (compartment != null) {
            login.setProperty(OracleNoSQLConnectionSpec.COMPARTMENT, compartment);
        }
        if (authPrincipal != null) {
            login.setProperty(OracleNoSQLConnectionSpec.AUTHORIZATION_PRINCIPAL, authPrincipal);
        }
    }

    /**
     * Build EIS login connection information for {@code EntityManager} NoSQL database connection.
     * @param mapped Build mapped property for the same host and port pair when {@code true} or normal property
     *               otherwise.
     * @return {@code EntityManager} NoSQL database connection properties.
     */
    public static Map<String, String> createEMProperties(final boolean mapped) {
        final String service = properties.get(NOSQL_SERVICE_PROPERTY_KEY);
        final String endpoint = properties.get(NOSQL_ENDPOINT_PROPERTY_KEY);
        final String compartment = properties.get(NOSQL_COMPARTMENT_PROPERTY_KEY);
        final String authPrincipal = properties.get(NOSQL_AUTH_PRINCIPAL_PROPERTY_KEY);
        final Map<String, String> emProperties = new HashMap<>(2);
        LOG.log(SessionLog.FINE, String.format("NoSQL service:%s\tendpoint:%s\tcompartment:%s\tauthorization principal:%s", service, endpoint, compartment, authPrincipal));
        if (service != null) {
            emProperties.put(NOSQL_SERVICE_PROPERTY_KEY, service);
        }
        if (endpoint != null) {
            emProperties.put(NOSQL_ENDPOINT_PROPERTY_KEY, endpoint);
        }
        if (compartment != null) {
            emProperties.put(NOSQL_COMPARTMENT_PROPERTY_KEY, compartment);
        }
        if (authPrincipal != null) {
            emProperties.put(NOSQL_AUTH_PRINCIPAL_PROPERTY_KEY, authPrincipal);
        }
        return emProperties;
    }
}
