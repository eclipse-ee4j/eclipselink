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

import jakarta.persistence.LockModeType;
import jakarta.persistence.LockOption;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.PessimisticLockScope;
import jakarta.persistence.Timeout;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;

/**
 * {@link LockOption} processing tools.
 * <p>Currently supported implementations:<ul>
 *     <li>{@link LockModeType}</li>
 *     <li>{@link PessimisticLockScope}</li>
 *     <li>{@link Timeout}</li>
 * </ul>
 */
class LockOptionUtils extends OptionUtils {

    // EntityManager#lock(Object,LockModeType,LockOption...) has LockModeType as standalone parameter
    // and LockModeType does not implement LockOption. This is probably bug in the jakarta.persistence API.
    /**
     * Parse provided {@link LockOption} array.
     *
     * @param options {@link LockOption} array
     * @return {@link Options} instance with parsed array content
     */
    static Options parse(LockModeType lockMode, LockOption... options) {
        return OptionsBuilder.build(null, lockMode, options);
    }

    private static final class OptionsBuilder extends OptionUtils.OptionsBuilder {

        private static final Map<Class<? extends LockOption>, BiConsumer<OptionsBuilder, LockOption>> ACTIONS = initActions();

        // FindOption is mostly implemented by enums so Map based dispatcher may be faster than if statements with instanceof.
        private static Map<Class<? extends LockOption>, BiConsumer<OptionsBuilder, LockOption>> initActions() {
            Map<Class<? extends LockOption>, BiConsumer<OptionsBuilder, LockOption>> actions = new HashMap<>(MAP_CAPACITY);
            actions.put(PessimisticLockScope.class, OptionsBuilder::pessimisticLockScope);
            actions.put(Timeout.class, OptionsBuilder::timeout);
            return actions;
        }

        private OptionsBuilder(Map<String, Object> properties) {
            super(properties);
        }

        private static void pessimisticLockScope(OptionsBuilder builder, LockOption cacheStoreMode) {
            builder.putProperty(QueryHints.PESSIMISTIC_LOCK_SCOPE, cacheStoreMode);
        }

        private static void timeout(OptionsBuilder builder, LockOption timeout) {
            builder.putProperty(QueryHints.PESSIMISTIC_LOCK_TIMEOUT_UNIT, java.util.concurrent.TimeUnit.MILLISECONDS);
            builder.putProperty(QueryHints.PESSIMISTIC_LOCK_TIMEOUT, ((Timeout)timeout).milliseconds());
        }

        private static Options build(Map<String, Object> properties, LockModeType lockMode, LockOption... options) {
            OptionsBuilder builder = new OptionsBuilder(properties);
            for (LockOption option : options) {
                // Fast dispatch for known classes.
                BiConsumer<OptionsBuilder, LockOption> action = ACTIONS.get(option.getClass());
                if (action != null) {
                    action.accept(builder, option);
                // Fallback dispatch for unknown classes that may still match rules.
                // No need to handle enums because they can't be extended.
                } else if (option instanceof Timeout) {
                    timeout(builder, option);
                } else {
                    throw new PersistenceException(
                            ExceptionLocalization.buildMessage("lock_option_class_unknown",
                                                               new String[] {option.getClass().getName()}));
                }
            }
            // Parsed options shall be completely immutable.
            return new Options(lockMode, Collections.unmodifiableMap(builder.getProperties()));
        }

    }

}
