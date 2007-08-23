/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.sessions.changesets;

import java.util.Vector;

/**
 * <p>
 * <b>Purpose</b>: To provide API into the SDKCollectionChangeSet.
 * <p>
 * <b>Description</b>: Capture the changes for an unordered collection as
 * collections of adds and removes.
 * <p>
 * @see SDKAggregateCollectionMapping
 * @see SDKObjectCollectionMapping
 * @see SDKDirectCollectionMapping
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).
 */
public interface SDKCollectionChangeRecord extends ChangeRecord {

    /**
     * ADVANCED:
     * Return the objects added to the collection.
     * The contents of this collection is determined by the mapping that
     * populated it:
     * <ul>
     * <li>SDKAggregateCollectionMapping will store ObjectChangeSets
     * <li>SDKDirectCollectionMapping will store the direct elements themselves
     * <li>SDKObjectCollectionMapping will store the foreign keys
     * </ul>
     */
    public Vector getAdds();

    /**
     * <p>
     * ADVANCED:
     * Return the objets whose Map keys have changed.
     * The contents of this collection is determined by the mapping that
     * populated it:
     * </p>
     * <ul>
     * <li>SDKAggregateCollectionMapping will store ObjectChangeSets
     * <li>SDKDirectCollectionMapping will store the direct elements themselves
     * <li>SDKObjectCollectionMapping will store the foreign keys
     * </ul>
     */
    public Vector getChangedMapKeys();

    /**
     * ADVANCED:
     * Return the removed objects.
     * The contents of this collection is determined by the mapping that
     * populated it:<ul>
     * <li>SDKAggregateCollectionMapping will store ObjectChangeSets
     * <li>SDKDirectCollectionMapping will store the direct elements themselves
     * <li>SDKObjectCollectionMapping will store the foreign keys
     * </ul>
     */
    public Vector getRemoves();

    /**
     * ADVANCED:
     * Return whether any changes have been recorded with the change record.
     */
    public boolean hasChanges();
}