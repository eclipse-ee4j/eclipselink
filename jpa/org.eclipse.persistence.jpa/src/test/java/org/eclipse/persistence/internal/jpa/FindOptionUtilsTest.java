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

import jakarta.persistence.CacheRetrieveMode;
import jakarta.persistence.CacheStoreMode;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PessimisticLockScope;
import junit.framework.TestCase;
import org.eclipse.persistence.config.QueryHints;

/**
 *
 */
public class FindOptionUtilsTest extends TestCase {

    public void testParseAllOptions() {
        FindOptionUtils.Options parsed = FindOptionUtils.parse(
                LockModeType.OPTIMISTIC,
                CacheRetrieveMode.USE,
                CacheStoreMode.REFRESH,
                PessimisticLockScope.EXTENDED);
        assertEquals(parsed.lockModeType(), LockModeType.OPTIMISTIC);
        assertEquals(parsed.properties().get(QueryHints.CACHE_RETRIEVE_MODE), CacheRetrieveMode.USE);
        assertEquals(parsed.properties().get(QueryHints.CACHE_STORE_MODE), CacheStoreMode.REFRESH);
        assertEquals(parsed.properties().get(QueryHints.PESSIMISTIC_LOCK_SCOPE), PessimisticLockScope.EXTENDED);
    }

    /* TODO-API-3.2: Add Timeout tests when API is fixed
    private static final class CustomTimeout extends Timeout {

    }
    */

}
