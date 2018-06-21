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
//     tware - initial API check-in for MappedKeyMapContainerPolicy
//     14/05/2012-2.4 Guy Pelletier
//       - 376603: Provide for table per tenant support for multitenant applications
package org.eclipse.persistence.mappings.foundation;

import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.ObjectBuildingQuery;

/**
 * A MapComponentMapping is any mapping that can be used as the key or the value
 * in a mapping that uses a MappedKeyMapContainerPolicy.  This interface is generally implemented
 * by mappings that provide the value in a mapping to a Map.  Mappings that provide the key generally
 * implement sub-interface MapKeyMapping
 *
 * @see org.eclipse.persistence.internal.queries.MappedKeyMapContainerPolicy
 * @see org.eclipse.persistence.mappings.foundation.MapKeyMapping
 * @see org.eclipse.persistence.mappings.DirectCollectionMapping
 * @see org.eclipse.persistence.mappings.AggregateCollectionMapping
 * @see org.eclipse.persistence.mappings.OneToManyMapping
 * @see org.eclipse.persistence.mappings.UnidirectionalOneToManyMapping
 * @see org.eclipse.persistence.mappings.ManyToManyMapping
 * @author tware
 *
 */
public interface MapComponentMapping {

    /**
     * INTERNAL
     * Called when a DatabaseMapping is used to map the key in a collection.  Returns the key.
     */
    public Object createMapComponentFromRow(AbstractRecord dbRow, ObjectBuildingQuery query, CacheKey parentCacheKey, AbstractSession session, boolean isTargetProtected);

    /**
     * INTERNAL
     * Called when cloning the container policy.
     */
    public Object clone();
}
