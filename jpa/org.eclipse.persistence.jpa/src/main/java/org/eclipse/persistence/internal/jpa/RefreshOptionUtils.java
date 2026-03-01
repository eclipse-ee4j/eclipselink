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
package org.eclipse.persistence.internal.jpa;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import jakarta.persistence.CacheStoreMode;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.PessimisticLockScope;
import jakarta.persistence.RefreshOption;
import jakarta.persistence.Timeout;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;

/**
 * {@link RefreshOption} processing tools.
 * <p>Currently supported implementations:<ul>
 *     <li>{@link LockModeType}</li>
 *     <li>{@link CacheStoreMode}</li>
 *     <li>{@link PessimisticLockScope}</li>
 *     <li>{@link Timeout}</li>
 * </ul>
 */
class RefreshOptionUtils extends OptionUtils {

    /**
     * Parse provided {@link RefreshOption} array.
     *
     * @param options {@link RefreshOption} array
     * @return {@link Options} instance with parsed array content
     */
    static Options parse(RefreshOption... options) {
        return OptionsBuilder.build(null, options);
    }

    private static final class OptionsBuilder extends OptionUtils.OptionsBuilder {

        private static final Map<Class<? extends RefreshOption>, BiConsumer<OptionsBuilder, RefreshOption>> ACTIONS = initActions();

        // FindOption is mostly implemented by enums so Map based dispatcher may be faster than if statements with instanceof.
        private static Map<Class<? extends RefreshOption>, BiConsumer<OptionsBuilder, RefreshOption>> initActions() {
            Map<Class<? extends RefreshOption>, BiConsumer<OptionsBuilder, RefreshOption>> actions = new HashMap<>(MAP_CAPACITY);
            actions.put(LockModeType.class, OptionsBuilder::lockModeType);
            actions.put(CacheStoreMode.class, OptionsBuilder::cacheStoreMode);
            actions.put(PessimisticLockScope.class, OptionsBuilder::pessimisticLockScope);
            actions.put(Timeout.class, OptionsBuilder::timeout);
            return actions;
        }

        private OptionsBuilder(Map<String, Object> properties) {
            super(properties);
        }

        private static void lockModeType(OptionsBuilder builder, RefreshOption lockModeType) {
            builder.setLockModeType((LockModeType) lockModeType);
        }

        private static void cacheStoreMode(OptionsBuilder builder, RefreshOption cacheStoreMode) {
            builder.putProperty(QueryHints.CACHE_STORE_MODE, cacheStoreMode);
        }

        private static void pessimisticLockScope(OptionsBuilder builder, RefreshOption cacheStoreMode) {
            builder.putProperty(QueryHints.PESSIMISTIC_LOCK_SCOPE, cacheStoreMode);
        }

        private static void timeout(OptionsBuilder builder, RefreshOption timeout) {
            builder.putProperty(QueryHints.QUERY_TIMEOUT_UNIT, java.util.concurrent.TimeUnit.MILLISECONDS);
            builder.putProperty(QueryHints.QUERY_TIMEOUT, ((Timeout)timeout).milliseconds());
        }

        private static Options build(Map<String, Object> properties, RefreshOption... options) {
            OptionsBuilder builder = new OptionsBuilder(properties);
            for (RefreshOption option : options) {
                // Fast dispatch for known classes.
                BiConsumer<OptionsBuilder, RefreshOption> action = ACTIONS.get(option.getClass());
                if (action != null) {
                    action.accept(builder, option);
                // Fallback dispatch for unknown classes that may still match rules.
                // No need to handle enums because they can't be extended.
                } else if (option instanceof Timeout) {
                    timeout(builder, option);
                } else {
                    throw new PersistenceException(
                            ExceptionLocalization.buildMessage("refresh_option_class_unknown",
                                                               new String[] {option.getClass().getName()}));
                }
            }
            // Parsed options shall be completely immutable.
            return new Options(builder.getLockModeType(), Collections.unmodifiableMap(builder.getProperties()));
        }

    }

}
