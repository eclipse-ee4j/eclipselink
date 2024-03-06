/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.config;

/**
 * PessimisticLock hint values.
 * <p>
 * The class contains all the valid values for QueryHints.PESSIMISTIC_LOCK query hint.
 * <p>JPA Query Hint usage:
 * {@snippet :
 *  query.setHint(QueryHints.PESSIMISTIC_LOCK, PessimisticLock.Lock);
 * }
 * <p>or
 * {@snippet :
 * @QueryHint(name=QueryHints.PESSIMISTIC_LOCK, value=PessimisticLock.Lock)
 * }
 *
 * Hint values are case-insensitive. {@code ""} could be used instead of default value
 * {@link PessimisticLock#DEFAULT}.
 * <p>
 * <B>Note:</B> As of JPA 2.0 there is a standard way to configure pessimistic locking.
 *
 * @see QueryHints
 * @see jakarta.persistence.LockModeType
 * @see jakarta.persistence.EntityManager
 * @see jakarta.persistence.Query
 */
public final class PessimisticLock {
    public static final String  NoLock = "NoLock";
    public static final String  Lock = "Lock";
    public static final String  LockNoWait = "LockNoWait";

    public static final String DEFAULT = NoLock;

    private PessimisticLock() {
        // no instance please
    }
}
