/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.config;

/**
 * PessimisticLock hint values.
 *
 * The class contains all the valid values for QueryHints.PESSIMISTIC_LOCK query hint.
 *
 * <p>JPA Query Hint Usage:
 *
 * <p><code>query.setHint(QueryHints.PESSIMISTIC_LOCK, PessimisticLock.Lock);</code>
 * <p>or
 * <p><code>@QueryHint(name=QueryHints.PESSIMISTIC_LOCK, value=PessimisticLock.Lock)</code>
 *
 * <p>Hint values are case-insensitive.
 * "" could be used instead of default value PessimisticLock.DEFAULT.
 *
 * @see QueryHints
 *
 * <B>Note:</B> As of JPA 2.0 there is a standard way to configure pessimistic locking.
 * @see javax.persistence.LockModeType
 * @see javax.persistence.EntityManager (find(), refresh(), lock())
 * @see javax.persistence.Query (setLockMode())
 */
public class PessimisticLock {
    public static final String  NoLock = "NoLock";
    public static final String  Lock = "Lock";
    public static final String  LockNoWait = "LockNoWait";

    public static final String DEFAULT = NoLock;
}
