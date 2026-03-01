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
import java.util.function.BiConsumer;

import jakarta.persistence.CacheRetrieveMode;
import jakarta.persistence.CacheStoreMode;
import jakarta.persistence.FindOption;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.PessimisticLockScope;
import jakarta.persistence.Timeout;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;

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
class FindOptionUtils extends OptionUtils {

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

    private static final class OptionsBuilder extends OptionUtils.OptionsBuilder {

        private static final Map<Class<? extends FindOption>, BiConsumer<OptionsBuilder, FindOption>> ACTIONS = initActions();

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
            super(properties);
        }

        // Dispatch rules Map is in static content so all handlers must be static.
        private static void lockModeType(OptionsBuilder builder, FindOption lockModeType) {
            builder.setLockModeType((LockModeType) lockModeType);
        }

        private static void cacheRetrieveMode(OptionsBuilder builder, FindOption cacheRetrieveMode) {
            builder.putProperty(QueryHints.CACHE_RETRIEVE_MODE, cacheRetrieveMode);
        }

        private static void cacheStoreMode(OptionsBuilder builder, FindOption cacheStoreMode) {
            builder.putProperty(QueryHints.CACHE_STORE_MODE, cacheStoreMode);
        }

        private static void pessimisticLockScope(OptionsBuilder builder, FindOption cacheStoreMode) {
            builder.putProperty(QueryHints.PESSIMISTIC_LOCK_SCOPE, cacheStoreMode);
        }

        private static void timeout(OptionsBuilder builder, FindOption timeout) {
            builder.putProperty(QueryHints.QUERY_TIMEOUT_UNIT, java.util.concurrent.TimeUnit.MILLISECONDS);
            builder.putProperty(QueryHints.QUERY_TIMEOUT, ((Timeout)timeout).milliseconds());
        }

        private static Options build(Map<String, Object> properties, FindOption... options) {
            OptionsBuilder builder = new OptionsBuilder(properties);
            for (FindOption option : options) {
                // Fast dispatch for known classes.
                BiConsumer<OptionsBuilder, FindOption> action = ACTIONS.get(option.getClass());
                if (action != null) {
                    action.accept(builder, option);
                // Fallback dispatch for unknown classes that may still match rules.
                // No need to handle enums because they can't be extended.
                } else if (option instanceof Timeout) {
                    timeout(builder, option);
                } else {
                    throw new PersistenceException(
                            ExceptionLocalization.buildMessage("find_option_class_unknown",
                                                               new String[] {option.getClass().getName()}));
                }
            }
            // Parsed options shall be completely immutable.
            return new Options(builder.getLockModeType(), Collections.unmodifiableMap(builder.getProperties()));
        }

    }

}
