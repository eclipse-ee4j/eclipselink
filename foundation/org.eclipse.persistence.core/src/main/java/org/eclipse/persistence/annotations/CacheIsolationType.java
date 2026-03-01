/*
 * Copyright (c) 2011, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     Gordon Yorke - initial API and implementation
package org.eclipse.persistence.annotations;

/**
 * Options for how Entity instances should be shared within an EclipseLink Persistence Unit / ServerSession
 * @see org.eclipse.persistence.descriptors.ClassDescriptor#setCacheIsolation(CacheIsolationType)
 * @see Cache
 * @author Gordon Yorke
 * @since EclipseLink 2.2
 */
public enum CacheIsolationType {
    // These enums are ordered with ascending protective requirements
    // This is intentional and any additions/alterations should take that into account

    /**
     * Entity instances will be cached within the EntityManagerFactory/ServerSession level.
     * Any user queries for shared cache instances (ie Read-Only query hint) will return an Entity
     * instance that may be shared by multiple clients.
     * <p>
     * This setting is the default isolation level.
     */
    SHARED,

    /**
     * Entity state information will be cached in the shared cache but Entity
     * instances will not be shared. Any user queries for shared cache instances
     * (ie Read-Only query hint) will return a new Entity instance with the cached state.
     * This will ensure the instance is <i>protected</i> from any concurrent
     * state change.
     */
    PROTECTED,

    /**
     * The Entity and its data are not stored in the shared cache but is
     * <i>isolated</i> to the Persistence Context/UnitOfWork or
     * IsolatedClientSession. This setting effectively disables second level
     * caching for this entity and should be used when users do not want caching for
     * a particular Entity.
     */
    ISOLATED
}
