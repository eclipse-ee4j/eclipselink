/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.descriptors;

import org.eclipse.persistence.internal.identitymaps.CacheKey;

/**
 * Define an interface for utility methods weaved into the persistence classes.
 * These general methods are used to optimize performance, such as caching the key in the object.
 * It can be disabled through the "weaving.internal" option.
 *
 * @author  mmacivor
 * @since   10.1.3
 */
public interface PersistenceEntity {
    Object _persistence_getId();
    void _persistence_setId(Object primaryKey);
    CacheKey _persistence_getCacheKey();
    void _persistence_setCacheKey(CacheKey cacheKey);
}
