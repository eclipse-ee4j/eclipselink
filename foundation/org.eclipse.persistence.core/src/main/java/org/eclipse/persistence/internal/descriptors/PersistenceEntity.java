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
