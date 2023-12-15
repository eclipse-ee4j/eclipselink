package org.eclipse.persistence.internal.jpa;

/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
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
//     12/14/2023: Tomas Kraus
//       - New Jakarta Persistence 3.2 Features

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import jakarta.persistence.CacheRetrieveMode;
import jakarta.persistence.CacheStoreMode;
import jakarta.persistence.LockModeType;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.logging.SessionLog;

/**
 * Common {@link jakarta.persistence.EntityManager} options processing tools.
 */
class OptionUtils {

    /**
     * Parsed {@link jakarta.persistence.FindOption} array.
     */
    record Options (LockModeType lockModeType, Map<String, Object> properties) {
    }

    static abstract class OptionsBuilder {

        // Tune this value to be at least number of supported FindOption (the largest number) classes.
        static final int MAP_CAPACITY = 8;

        private final Map<String, Object> properties;
        private LockModeType lockModeType;

        OptionsBuilder(Map<String, Object> properties) {
            this.lockModeType = null;
            if (properties != null) {
                this.properties = new HashMap<>(properties.size() + MAP_CAPACITY);
                this.properties.putAll(properties);
            } else {
                this.properties = new HashMap<>(MAP_CAPACITY);
            }
        }

        void putProperty(String key, Object value) {
            properties.put(key, value);
        }

        Map<String, Object> getProperties() {
            return properties;
        }

        LockModeType getLockModeType() {
            return lockModeType;
        }

        void setLockModeType(LockModeType lockModeType) {
            this.lockModeType = lockModeType;
        }
    }

    @SuppressWarnings("unchecked")
    static void setCacheRetrieveMode(Map<?, Object> properties, CacheRetrieveMode cacheRetrieveMode) {
        ((Map<String, Object>)properties).put(QueryHints.CACHE_RETRIEVE_MODE, cacheRetrieveMode);
    }

    // Based on EntityManagerImpl#getQueryHints(Object,OperationType)
    static CacheRetrieveMode getCacheRetrieveMode(AbstractSession session, Map<?, Object> properties) {
        // QueryHints property
        Object propertyValue = properties.get(QueryHints.CACHE_RETRIEVE_MODE);
        if (propertyValue instanceof CacheRetrieveMode) {
            return (CacheRetrieveMode) propertyValue;
        } else if (propertyValue != null) {
            session.log(SessionLog.WARNING,
                        SessionLog.QUERY,
                        "unknown_property_type",
                        propertyValue.getClass().getName(),
                        QueryHints.CACHE_RETRIEVE_MODE);
        }
        // Default value according to JPA spec.
        return CacheRetrieveMode.USE;
    }

    static CacheStoreMode getCacheStoreMode(AbstractSession session, Map<?, Object> properties) {
        // QueryHints property
        Object propertyValue = properties.get(QueryHints.CACHE_STORE_MODE);
        if (propertyValue instanceof CacheStoreMode) {
            return (CacheStoreMode) propertyValue;
        } else if (propertyValue != null) {
            session.log(SessionLog.WARNING,
                        SessionLog.QUERY,
                        "unknown_property_type",
                        propertyValue.getClass().getName(),
                        QueryHints.CACHE_STORE_MODE);
        }
        // Default value according to JPA spec.
        return CacheStoreMode.USE;
    }

    @SuppressWarnings("unchecked")
    static void setCacheStoreMode(Map<?, Object> properties, CacheStoreMode cacheStoreMode) {
        ((Map<String, Object>)properties).put(QueryHints.CACHE_STORE_MODE, cacheStoreMode);
    }

    static Integer getTimeout(AbstractSession session, Map<?, Object> properties) {
        // QueryHints.QUERY_TIMEOUT_UNIT may contain TimeUnit
        TimeUnit timeUnit = TimeUnit.MILLISECONDS;
        Object propertyValue = properties.get(QueryHints.QUERY_TIMEOUT_UNIT);
        if (propertyValue instanceof TimeUnit) {
            timeUnit = (TimeUnit)propertyValue;
        } else if (propertyValue != null) {
            session.log(SessionLog.WARNING,
                        SessionLog.QUERY,
                        "unknown_property_type",
                        propertyValue.getClass().getName(),
                        QueryHints.QUERY_TIMEOUT_UNIT);
        }
        // QueryHints.QUERY_TIMEOUT must be converted from actual units to milliseconds
        propertyValue = properties.get(QueryHints.QUERY_TIMEOUT);
        if (propertyValue instanceof Number n) {
            return (int)TimeUnit.MILLISECONDS.convert(n.longValue(), timeUnit);
        } else if (propertyValue instanceof String s) {
            try {
                long value = Long.parseLong(s);
                return (int)TimeUnit.MILLISECONDS.convert(value, timeUnit);
            } catch (NumberFormatException e) {
                session.log(SessionLog.WARNING,
                            SessionLog.QUERY,
                            "error_queryTimeoutParse",
                            s, e.getLocalizedMessage());
            }
        } else {
            session.log(SessionLog.WARNING,
                        SessionLog.QUERY,
                        "unknown_property_type",
                        propertyValue.getClass().getName(),
                        QueryHints.QUERY_TIMEOUT);
        }
        // Return default value (means no timeout was set)
        return null;
    }

    @SuppressWarnings("unchecked")
    static void setTimeout(Map<?, Object> properties, Integer timeout) {
        // Javadoc does not specify units. Default QueryHints.QUERY_TIMEOUT unit is milliseconds
        // so timeout argument is expected to be miliseconds too.
        ((Map<String, Object>)properties).put(QueryHints.QUERY_TIMEOUT_UNIT, TimeUnit.MILLISECONDS);
        ((Map<String, Object>)properties).put(QueryHints.QUERY_TIMEOUT, timeout);
    }

}
