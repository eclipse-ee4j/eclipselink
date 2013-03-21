/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.eis;

import java.util.*;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.internal.sessions.*;

/**
 * INTERNAL:
 * Capture the changes for an unordered collection as
 * collections of adds and removes.
 */
public class EISCollectionChangeRecord extends CollectionChangeRecord implements org.eclipse.persistence.sessions.changesets.EISCollectionChangeRecord {

    /** The added stuff. */
    private Vector adds;

    /** The removed stuff. */
    private Vector removes;

    /** The stuff whose Map keys have changed. */
    private Vector changedMapKeys;

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
        this.getAdds().addElement(changeSet);
    }

    /**
     * Add an changed key change set.
     */
    public void addChangedMapKeyChangeSet(Object changeSet) {
        this.getChangedMapKeys().addElement(changeSet);
    }

    /**
     * Add an removed change set.
     */
    public void addRemovedChangeSet(Object changeSet) {
        this.getRemoves().addElement(changeSet);
    }

    /**
     * ADVANCED:
     * Return the added stuff.
     * The contents of this collection is determined by the mapping that
     * populated it
     */
    public Vector getAdds() {
        if (adds == null) {
            adds = new Vector(1);// keep it as small as possible
        }
        return adds;
    }

    /**
     * ADVANCED:
     * Return the stuff whose Map keys have changed.
     * The contents of this collection is determined by the mapping that
     * populated it
     */
    public Vector getChangedMapKeys() {
        if (changedMapKeys == null) {
            changedMapKeys = new Vector(1);// keep it as small as possible
        }
        return changedMapKeys;
    }

    /**
     * ADVANCED:
     * Return the removed stuff.
     * The contents of this collection is determined by the mapping that
     * populated it
     */
    public Vector getRemoves() {
        if (removes == null) {
            removes = new Vector(1);// keep it as small as possible
        }
        return removes;
    }

    /**
     * Return whether any adds have been recorded with the change record.
     * Directly reference the instance variable, so as to not trigger the lazy instantiation.
     */
    private boolean hasAdds() {
        return (adds != null) && (!adds.isEmpty());
    }

    /**
     * Return whether any changed map keys have been recorded with the change record.
     * Directly reference the instance variable, so as to not trigger the lazy instantiation.
     */
    private boolean hasChangedMapKeys() {
        return (changedMapKeys != null) && (!changedMapKeys.isEmpty());
    }

    /**
     * Return whether any changes have been recorded with the change record.
     */
    public boolean hasChanges() {
        return this.hasAdds() || this.hasRemoves() || this.hasChangedMapKeys() || this.getOwner().isNew();
    }

    /**
     * Return whether any removes have been recorded with the change record.
     * Directly reference the instance variable, so as to not trigger the lazy instantiation.
     */
    private boolean hasRemoves() {
        return (removes != null) && (!removes.isEmpty());
    }

    /**
     * Remove a previously added change set.
     * Return true if it was actually removed from the collection.
     */
    private boolean removeAddedChangeSet(Object changeSet) {
        if (adds == null) {
            return false;
        } else {
            return adds.remove(changeSet);
        }
    }

    /**
     * Remove a previously added change set.
     * Return true if it was actually removed from the collection.
     */
    private boolean removeRemovedChangeSet(Object changeSet) {
        if (removes == null) {
            return false;
        } else {
            return removes.remove(changeSet);
        }
    }

    /**
     * Add a change set after it has been applied.
     */
    public void simpleAddChangeSet(Object changeSet) {
        // check whether the change set was removed earlier
        if (!this.removeRemovedChangeSet(changeSet)) {
            this.addAddedChangeSet(changeSet);
        }
    }

    /**
     * Remove a change set after it has been applied.
     */
    public void simpleRemoveChangeSet(Object changeSet) {
        // check whether the change set was added earlier
        if (!this.removeAddedChangeSet(changeSet)) {
            this.addRemovedChangeSet(changeSet);
        }
    }
}
