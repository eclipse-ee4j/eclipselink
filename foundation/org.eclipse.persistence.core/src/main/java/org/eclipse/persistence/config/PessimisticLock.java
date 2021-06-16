/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
 * @see jakarta.persistence.LockModeType
 * @see jakarta.persistence.EntityManager (find(), refresh(), lock())
 * @see jakarta.persistence.Query (setLockMode())
 */
public class PessimisticLock {
    public static final String  NoLock = "NoLock";
    public static final String  Lock = "Lock";
    public static final String  LockNoWait = "LockNoWait";

    public static final String DEFAULT = NoLock;
}
