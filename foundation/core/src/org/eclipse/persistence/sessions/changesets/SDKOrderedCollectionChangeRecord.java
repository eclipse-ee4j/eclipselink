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
 * <b>Description</b>: Capture the changes for an ordered collection where
 * the entire collection is simply replaced if it has changed.
 * <p>
 * @see SDKAggregateCollectionMapping
 * @see SDKObjectCollectionMapping
 * @see SDKDirectCollectionMapping
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).
 */
public interface SDKOrderedCollectionChangeRecord extends ChangeRecord {

    /**
     * ADVANCED:
     * Return the indexes into the new collection of
     * the elements that were added.
     */
    int[] getAddIndexes();

    /**
     * ADVANCED:
     * Return the entries for all the elements added to the new collection.
     * The contents of this collection is determined by the mapping that
     * populated it:<ul>
     * <li>SDKAggregateCollectionMapping will store ObjectChangeSets
     * <li>SDKDirectCollectionMapping will store the direct elements themselves
     * <li>SDKObjectCollectionMapping will store the foreign keys
     * </ul>
     */
    Vector getAdds();

    /**
     * OBSOLETE:
     * @deprecated use #getMoveIndexPairs()
     * @see #getMoveIndexPairs()
     */
    int[][] getMoveIndexes();

    /**
     * ADVANCED:
     * ADVANCED:
     * Return the indexes of the elements that were simply moved
     * within the collection.
     * Each element in the outer array is another two-element
     * array where the first entry [0] is the index of the object in
     * the old collection and the second entry [1] is the index
     * of the object in the new collection. These two indexes
     * can be equal.
     */
    int[][] getMoveIndexPairs();

    /**
     * ADVANCED:
     * Return the entries for all the elements that were simply shuffled
     * within the collection.
     * The contents of this collection is determined by the mapping that
     * populated it:<ul>
     * <li>SDKAggregateCollectionMapping will store ObjectChangeSets
     * <li>SDKDirectCollectionMapping will store the direct elements themselves
     * <li>SDKObjectCollectionMapping will store the foreign keys
     * </ul>
     */
    Vector getMoves();

    /**
     * ADVANCED:
     * Return the entries for all the elements in the new collection.
     * The contents of this collection is determined by the mapping that
     * populated it:<ul>
     * <li>SDKAggregateCollectionMapping will store ObjectChangeSets
     * <li>SDKDirectCollectionMapping will store the direct elements themselves
     * <li>SDKObjectCollectionMapping will store the foreign keys
     * </ul>
     */
    Vector getNewCollection();

    /**
     * ADVANCED:
     * Return the indexes into the old collection of
     * the elements that were removed.
     */
    int[] getRemoveIndexes();

    /**
     * ADVANCED:
     * Return the entries for all the elements removed from the old collection.
     * The contents of this collection is determined by the mapping that
     * populated it:<ul>
     * <li>SDKAggregateCollectionMapping will store ObjectChangeSets
     * <li>SDKDirectCollectionMapping will store the direct elements themselves
     * <li>SDKObjectCollectionMapping will store the foreign keys
     * </ul>
     */
    Vector getRemoves();

    /**
     * ADVANCED:
     * Return whether any changes have been recorded with the change record.
     */
    boolean hasChanges();
}