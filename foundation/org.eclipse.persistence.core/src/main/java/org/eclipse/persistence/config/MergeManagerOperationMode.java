/*
 * Copyright (c) 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.config;

/**
 * INTERNAL:
 * <p>
 * <b>Purpose</b>: It is data model behind {@linkplain  org.eclipse.persistence.config.SystemProperties#CONCURRENCY_MANAGER_ALLOW_GET_CACHE_KEY_FOR_MERGE_MODE}
 * or {@linkplain  org.eclipse.persistence.config.PersistenceUnitProperties#CONCURRENCY_MANAGER_ALLOW_GET_CACHE_KEY_FOR_MERGE_MODE}.
 */
public final class MergeManagerOperationMode {

    /**
     * {@code ORIGIN} (DEFAULT) - There is infinite {@linkplain java.lang.Object#wait()} call in case of some conditions during time when object/entity referred from
     * {@code org.eclipse.persistence.internal.identitymaps.CacheKey} is locked and modified by another thread. In some cases it should leads into deadlock.
     */
    public static final String ORIGIN = "ORIGIN";

    /**
     * {@code WAITLOOP} - Merge manager will try in the loop with timeout wait {@code cacheKey.wait(ConcurrencyUtil.SINGLETON.getAcquireWaitTime());}
     * fetch object/entity from {@linkplain org.eclipse.persistence.internal.identitymaps.CacheKey}. If fetch will be successful object/entity loop finish and continue
     * with remaining code. If not @{code java.lang.InterruptedException} is thrown and caught and used {@linkplain org.eclipse.persistence.internal.identitymaps.CacheKey} instance
     * status is set into invalidation state. This strategy avoid deadlock issue, but there should be impact to the performance.
     */
    public static final String WAITLOOP = "WAITLOOP";

    private MergeManagerOperationMode() {
        // no instance please
    }
}
