/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.descriptors.changetracking;

import java.beans.PropertyChangeEvent;

/**
 * <p>
 * <b>Purpose</b>: Define a change event for collection types.
 * <p>
 * <b>Description</b>: For any object that wishes to use either object change tracking or
 * attribute change tracking, its collection attributes need to fire CollectionChangeEvent
 * in the add or remove methods.
 * <p>
 * <b>Responsibilities</b>: Create a CollectionChangeEvent for an object
 */
public class CollectionChangeEvent extends PropertyChangeEvent {
    public static final int ADD = 0;
    public static final int REMOVE = 1;

    /**
     * INTERNAL:
     * Change type is either add or remove
     */
    protected int changeType;

    /**
     * INTERNAL:
     * index is the location of the change in the collection
     */
    protected Integer index;

    /**
     * INTERNAL:
     * Set operation in IndirectList results in raising two events: removal of the old value and addition of the new one at the same index:
     *   oldValue = list.set(i, newValue);
     *   raiseRemoveEvent(i, oldValue, true);
     *   raiseAddEvent(i, newValue, true);
     * This flag indicates whether the event was raised by set operation on the list.
     */
    protected boolean isSet;

    /**
     * INTERNAL:
     * This flag will indicate if the object has already been removed or added to the collection before raising an event.
     * The object is not removed or added before raising an event during merge.
     */
    protected boolean isChangeApplied = true;

    /**
     * PUBLIC:
     * Create a CollectionChangeEvent for an object based on the property name, old value, new value,
     * change type (add or remove) and change applied.
     *
     */
    public CollectionChangeEvent(Object collectionOwner, String propertyName, Object collectionChanged, Object elementChanged, int changeType, boolean isChangeApplied) {
        this(collectionOwner, propertyName, collectionChanged, elementChanged, changeType, null, false, isChangeApplied);
    }

    /**
     * PUBLIC:
     * Create a CollectionChangeEvent for an object based on the property name, old value, new value,
     * change type (add or remove) and the index where the object is/was in the collection (list),
     * flag indicating whether the change (addition or removal) is part of a single set operation on a list,
     * flag indicating whether the object has already been added or removed from the collection.
     */
    public CollectionChangeEvent(Object collectionOwner, String propertyName, Object collectionChanged, Object elementChanged, int changeType, Integer index, boolean isSet, boolean isChangeApplied) {
        super(collectionOwner, propertyName, collectionChanged, elementChanged);
        this.changeType = changeType;
        this.index = index;
        this.isSet = isSet;
        this.isChangeApplied = isChangeApplied;
    }

    /**
     * INTERNAL:
     * Return the change type
     */
    public int getChangeType() {
        return changeType;
    }

    /**
     * INTERNAL:
     * Return whether the event was raised by set operation on the list.
     */
    public boolean isSet() {
        return isSet;
    }

    /**
     * INTERNAL:
     * Return the index of the change in the collection
     */
    public Integer getIndex() {
        return index;
    }

    /**
     * INTERNAL:
     * Set the index of the change in the collection
     */
    public void setIndex(Integer index) {
        this.index = index;
    }

    /**
     * INTERNAL:
     * Return the value indicating if the object has been already added or removed from the collection.
     */
    public boolean isChangeApplied() {
        return isChangeApplied;
    }
}
