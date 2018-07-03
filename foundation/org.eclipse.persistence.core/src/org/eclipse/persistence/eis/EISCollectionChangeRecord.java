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
package org.eclipse.persistence.eis;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.internal.sessions.CollectionChangeRecord;
import org.eclipse.persistence.internal.sessions.ObjectChangeSet;
import org.eclipse.persistence.mappings.DatabaseMapping;

/**
 * INTERNAL:
 * Capture the changes for an unordered collection as
 * collections of adds and removes.
 */
public class EISCollectionChangeRecord extends CollectionChangeRecord implements org.eclipse.persistence.sessions.changesets.EISCollectionChangeRecord {

    /** The added stuff. */
    private List adds;

    /** The removed stuff. */
    private List removes;

    /** The stuff whose Map keys have changed. */
    private List changedMapKeys;

    /**
     * Construct a ChangeRecord that can be used to represent the changes to
     * an unordered collection.
     */
    public EISCollectionChangeRecord(ObjectChangeSet owner, String attributeName, DatabaseMapping mapping) {
        super();
        this.owner = owner;
        this.attribute = attributeName;
        this.mapping = mapping;
    }

    /**
     * Add an added change set.
     */
    public void addAddedChangeSet(Object changeSet) {
        getAdds().add(changeSet);
    }

    /**
     * Add an changed key change set.
     */
    public void addChangedMapKeyChangeSet(Object changeSet) {
        getChangedMapKeys().add(changeSet);
    }

    /**
     * Add an removed change set.
     */
    public void addRemovedChangeSet(Object changeSet) {
        getRemoves().add(changeSet);
    }

    /**
     * ADVANCED:
     * Return the added stuff.
     * The contents of this collection is determined by the mapping that
     * populated it
     */
    public List getAdds() {
        if (adds == null) {
            adds = new ArrayList(2);// keep it as small as possible
        }
        return adds;
    }

    /**
     * ADVANCED:
     * Return the stuff whose Map keys have changed.
     * The contents of this collection is determined by the mapping that
     * populated it
     */
    public List getChangedMapKeys() {
        if (changedMapKeys == null) {
            changedMapKeys = new ArrayList(2);// keep it as small as possible
        }
        return changedMapKeys;
    }

    /**
     * ADVANCED:
     * Return the removed stuff.
     * The contents of this collection is determined by the mapping that
     * populated it
     */
    public List getRemoves() {
        if (removes == null) {
            removes = new ArrayList(2);// keep it as small as possible
        }
        return removes;
    }

    /**
     * Return whether any adds have been recorded with the change record.
     * Directly reference the instance variable, so as to not trigger the lazy instantiation.
     */
    private boolean hasAdds() {
        return (this.adds != null) && (!this.adds.isEmpty());
    }

    /**
     * Return whether any changed map keys have been recorded with the change record.
     * Directly reference the instance variable, so as to not trigger the lazy instantiation.
     */
    private boolean hasChangedMapKeys() {
        return (this.changedMapKeys != null) && (!this.changedMapKeys.isEmpty());
    }

    /**
     * Return whether any changes have been recorded with the change record.
     */
    public boolean hasChanges() {
        return hasAdds() || hasRemoves() || hasChangedMapKeys() || getOwner().isNew();
    }

    /**
     * Return whether any removes have been recorded with the change record.
     * Directly reference the instance variable, so as to not trigger the lazy instantiation.
     */
    private boolean hasRemoves() {
        return (this.removes != null) && (!this.removes.isEmpty());
    }

    /**
     * Remove a previously added change set.
     * Return true if it was actually removed from the collection.
     */
    private boolean removeAddedChangeSet(Object changeSet) {
        if (this.adds == null) {
            return false;
        } else {
            return this.adds.remove(changeSet);
        }
    }

    /**
     * Remove a previously added change set.
     * Return true if it was actually removed from the collection.
     */
    private boolean removeRemovedChangeSet(Object changeSet) {
        if (this.removes == null) {
            return false;
        } else {
            return this.removes.remove(changeSet);
        }
    }

    /**
     * Add a change set after it has been applied.
     */
    public void simpleAddChangeSet(Object changeSet) {
        // check whether the change set was removed earlier
        if (!removeRemovedChangeSet(changeSet)) {
            addAddedChangeSet(changeSet);
        }
    }

    /**
     * Remove a change set after it has been applied.
     */
    public void simpleRemoveChangeSet(Object changeSet) {
        // check whether the change set was added earlier
        if (!removeAddedChangeSet(changeSet)) {
            addRemovedChangeSet(changeSet);
        }
    }
}
