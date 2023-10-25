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
//     08/31/2023: Tomas Kraus
//       - New Jakarta Persistence 3.2 Features
package org.eclipse.persistence.internal.jpa;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

import jakarta.persistence.CacheRetrieveMode;
import jakarta.persistence.CacheStoreMode;
import jakarta.persistence.FindOption;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PessimisticLockScope;
import jakarta.persistence.Timeout;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.logging.SessionLog;

/**
 * {@link FindOption} processing tools.
 * <p>Currently supported implementations:<ul>
 *     <li>{@link LockModeType}</li>
 *     <li>{@link CacheRetrieveMode}</li>
 *     <li>{@link CacheStoreMode}</li>
 *     <li>{@link PessimisticLockScope}</li>
 *     <li>{@link Timeout}</li>
 * </ul>
 */
class FindOptionUtils {

    /**
     * Parsed {@link FindOption} array.
     */
    record Options (LockModeType lockModeType, Map<String, Object> properties) {
    }

    // TODO-API-3.2 - FindOption not defined in EclipseLink scope, just in API.
    private static final class OptionsBuilder {

        private static final Map<Class<? extends FindOption>, BiConsumer<OptionsBuilder, FindOption>> ACTIONS = initActions();
        // Tune this value to be at least number of supported FindOption classes.
        private static final int MAP_CAPACITY = 8;

        private final Map<String, Object> properties;
        private LockModeType lockModeType;

        // FindOption is mostly implemented by enums so Map based dispatcher may be faster than if statements with instanceof.
        private static Map<Class<? extends FindOption>, BiConsumer<OptionsBuilder, FindOption>> initActions() {
            Map<Class<? extends FindOption>, BiConsumer<OptionsBuilder, FindOption>> actions = new HashMap<>(MAP_CAPACITY);
            actions.put(LockModeType.class, OptionsBuilder::lockModeType);
            actions.put(CacheRetrieveMode.class, OptionsBuilder::cacheRetrieveMode);
            actions.put(CacheStoreMode.class, OptionsBuilder::cacheStoreMode);
            actions.put(PessimisticLockScope.class, OptionsBuilder::pessimisticLockScope);
            actions.put(Timeout.class, OptionsBuilder::timeout);
            return actions;
        }

        private OptionsBuilder(Map<String, Object> properties) {
            this.lockModeType = null;
            if (properties != null) {
                this.properties = new HashMap<>(properties.size() + MAP_CAPACITY);
                this.properties.putAll(properties);
            } else {
                this.properties = new HashMap<>(MAP_CAPACITY);
            }
        }

        // Dispatch rules Map is in static content so all handlers must be static.
        private static void lockModeType(OptionsBuilder builder, FindOption lockModeType) {
            builder.lockModeType = (LockModeType) lockModeType;
        }

        private static void cacheRetrieveMode(OptionsBuilder builder, FindOption cacheRetrieveMode) {
            builder.properties.put(QueryHints.CACHE_RETRIEVE_MODE, cacheRetrieveMode);
        }

        private static void cacheStoreMode(OptionsBuilder builder, FindOption cacheStoreMode) {
            builder.properties.put(QueryHints.CACHE_STORE_MODE, cacheStoreMode);
        }

        private static void pessimisticLockScope(OptionsBuilder builder, FindOption cacheStoreMode) {
            builder.properties.put(QueryHints.PESSIMISTIC_LOCK_SCOPE, cacheStoreMode);
        }

        private static void timeout(OptionsBuilder builder, FindOption timeout) {
            builder.properties.put(QueryHints.QUERY_TIMEOUT_UNIT, java.util.concurrent.TimeUnit.MILLISECONDS);
            builder.properties.put(QueryHints.QUERY_TIMEOUT, ((Timeout)timeout).milliseconds());
        }

        private static Options build(Map<String, Object> properties, FindOption... options) {
            OptionsBuilder builder = new OptionsBuilder(properties);
            for (FindOption option : options) {
                // TODO: Java 21 will allow pattern matching for switch, e.g. case rules for individual types.
                // Fast dispatch for known classes.
                BiConsumer<OptionsBuilder, FindOption> action = ACTIONS.get(option.getClass());
                if (action != null) {
                    action.accept(builder, option);
                // Fallback dispatch for unknown classes that may still match rules.
                // No need to handle enums because they can't be extended.
                } else if (option instanceof Timeout) {
                    timeout(builder, option);
                } else {
                    // TODO: Log unknown FindOption instance
                }
            }
            // Parsed options shall be completely immutable.
            return new Options(builder.lockModeType, Collections.unmodifiableMap(builder.properties));
        }

    }

    /**
     * Parse provided {@link FindOption} array.
     *
     * @param options {@link FindOption} array
     * @return {@link Options} instance with parsed array content
     */
    static Options parse(FindOption... options) {
        return OptionsBuilder.build(null, options);
    }

    /**
     * Parse provided {@link FindOption} array.
     * Returned {@link Options} instance contains provided {@code properties} which
     * may be overwritten by content of {@link FindOption} array.
     *
     * @param properties initial query properties
     * @param options {@link FindOption} array
     * @return {@link Options} instance with parsed array content
     */
    static Options parse(Map<String, Object> properties, FindOption... options) {
        return OptionsBuilder.build(properties, options);
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

    @SuppressWarnings("unchecked")
    static void setCacheRetrieveMode(Map<?, Object> properties, CacheRetrieveMode cacheRetrieveMode) {
        ((Map<String, Object>)properties).put(QueryHints.CACHE_RETRIEVE_MODE, cacheRetrieveMode);
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
