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
package org.eclipse.persistence.sessions.changesets;

import java.util.List;

/**
 * <p>
 * <b>Purpose</b>: To provide API into the EISCollectionChangeSet.
 * <p>
 * <b>Description</b>: Capture the changes for an ordered collection where
 * the entire collection is simply replaced if it has changed.
 */
public interface EISOrderedCollectionChangeRecord extends ChangeRecord {

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
     * populated it
     */
    List getAdds();

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
     * populated it
     */
    List getMoves();

    /**
     * ADVANCED:
     * Return the entries for all the elements in the new collection.
     * The contents of this collection is determined by the mapping that
     * populated it
     */
    List getNewCollection();

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
     * populated it
     */
    List getRemoves();

    /**
     * ADVANCED:
     * Return whether any changes have been recorded with the change record.
     */
    boolean hasChanges();
}
