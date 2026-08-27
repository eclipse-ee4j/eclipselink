/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.queries;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ObjectLevelReadQueryConcreteSubclassCallsCacheTest {

    @Test
    void shouldCacheConcreteSubclassCallsDefaultsToTrue() {
        ReadAllQuery query = new ReadAllQuery();

        assertTrue(query.shouldCacheConcreteSubclassCalls());
    }

    @Test
    void setShouldCacheConcreteSubclassCallsIsHonored() {
        ReadAllQuery query = new ReadAllQuery();

        query.setShouldCacheConcreteSubclassCalls(false);

        assertFalse(query.shouldCacheConcreteSubclassCalls());
    }

    @Test
    void cloneKeepsShouldCacheConcreteSubclassCallsValue() {
        ReadAllQuery query = new ReadAllQuery();
        query.setShouldCacheConcreteSubclassCalls(false);

        ReadAllQuery clone = (ReadAllQuery) query.clone();

        assertFalse(clone.shouldCacheConcreteSubclassCalls());
    }

    @Test
    void prepareFromQueryCopiesShouldCacheConcreteSubclassCallsValue() {
        // https://github.com/eclipse-ee4j/eclipselink/issues/2806
        // A dynamic query prepared from an already-prepared query must inherit the
        // cache-disabling flag, not silently fall back to the (caching) default.
        ReadAllQuery preparedQuery = new ReadAllQuery();
        preparedQuery.setShouldCacheConcreteSubclassCalls(false);

        ReadAllQuery dynamicQuery = new ReadAllQuery();
        dynamicQuery.prepareFromQuery(preparedQuery);

        assertFalse(dynamicQuery.shouldCacheConcreteSubclassCalls());
    }
}
