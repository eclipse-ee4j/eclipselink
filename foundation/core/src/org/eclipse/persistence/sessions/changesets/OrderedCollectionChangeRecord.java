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

import java.util.Hashtable;
import java.util.Vector;

/**
 * <p>
 * <b>Purpose</b>: Provide public API to the OrderedCollectionChangeRecord.
 * <p>
 * <b>Description</b>: OrderedCollections, used in TopLink SDK, must be tracked differently from regulat Collections.
 * As the objects in the collection have a particular index which must be stored. This class stores the objects which must be written
 * into the collection and the indexes they must be written in at.  Inserting a new element at the beginning of the list will result
 * in the intire list being stored in the change set as the index of all other objects has changed.  Everything after the remove index will
 * be remove.
 */
public interface OrderedCollectionChangeRecord extends ChangeRecord {

    /**
     * ADVANCED:
     * This method returns the collection of indexes in which changes were made to this collection.
     * @return java.util.Vector
     */
    public Vector getAddIndexes();

    /**
     * ADVANCED:
     * This method returns the collection of ChangeSets that were added to the collection.
     * The indexes of these objects are the Keys of the Hashtable
     * @return java.util.Hashtable
     */
    public Hashtable getAddObjectList();

    /**
     * ADVANCED:
     * This method returns the index from where objects must be removed from the collection
     * @return int
     */
    public int getStartRemoveIndex();
}