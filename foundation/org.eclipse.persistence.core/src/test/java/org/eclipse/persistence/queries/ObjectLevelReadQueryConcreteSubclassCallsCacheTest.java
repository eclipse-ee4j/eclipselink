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
    void copyFromQueryCopiesShouldCacheConcreteSubclassCallsValue() {
        ReadAllQuery source = new ReadAllQuery();
        source.setShouldCacheConcreteSubclassCalls(false);

        ReadAllQuery query = new ReadAllQuery();
        query.copyFromQuery(source);

        assertFalse(query.shouldCacheConcreteSubclassCalls());
    }

    @Test
    void prepareFromQueryKeepsDisabledSettingWhenCachedQueryIsEnabled() {
        // A query prepared from an equivalent cached query keeps its own setting
        ReadAllQuery cachedQuery = new ReadAllQuery();

        ReadAllQuery query = new ReadAllQuery();
        query.setShouldCacheConcreteSubclassCalls(false);
        query.prepareFromQuery(cachedQuery);

        assertFalse(query.shouldCacheConcreteSubclassCalls());
    }

    @Test
    void prepareFromQueryKeepsEnabledSettingWhenCachedQueryIsDisabled() {
        ReadAllQuery cachedQuery = new ReadAllQuery();
        cachedQuery.setShouldCacheConcreteSubclassCalls(false);

        ReadAllQuery query = new ReadAllQuery();
        query.prepareFromQuery(cachedQuery);

        assertTrue(query.shouldCacheConcreteSubclassCalls());
    }

    @Test
    void readObjectQueryPrepareFromQueryKeepsItsOwnSetting() {
        ReadObjectQuery enabledCachedQuery = new ReadObjectQuery();
        ReadObjectQuery disabledQuery = new ReadObjectQuery();
        disabledQuery.setShouldCacheConcreteSubclassCalls(false);
        disabledQuery.prepareFromQuery(enabledCachedQuery);

        ReadObjectQuery disabledCachedQuery = new ReadObjectQuery();
        disabledCachedQuery.setShouldCacheConcreteSubclassCalls(false);
        ReadObjectQuery enabledQuery = new ReadObjectQuery();
        enabledQuery.prepareFromQuery(disabledCachedQuery);

        assertFalse(disabledQuery.shouldCacheConcreteSubclassCalls());
        assertTrue(enabledQuery.shouldCacheConcreteSubclassCalls());
    }
}
