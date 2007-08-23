/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package deprecated.sdk;

import java.util.*;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.internal.sessions.*;

/**
 * Capture the changes for an unordered collection as
 * collections of adds and removes.
 *
 * @see SDKAggregateCollectionMapping
 * @see SDKObjectCollectionMapping
 * @see SDKDirectCollectionMapping
 *
 * @author Big Country
 * @since TOPLink/Java 3.0
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).  This class is replaced by
 *         {@link org.eclipse.persistence.eis}
 */
class SDKCollectionChangeRecord extends CollectionChangeRecord implements org.eclipse.persistence.sessions.changesets.SDKCollectionChangeRecord {

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
    SDKCollectionChangeRecord(ObjectChangeSet owner, String attributeName, DatabaseMapping mapping) {
        super();
        this.owner = owner;
        this.attribute = attributeName;
        this.mapping = mapping;
    }

    /**
     * Add an added change set.
     */
    void addAddedChangeSet(Object changeSet) {
        this.getAdds().addElement(changeSet);
    }

    /**
     * Add an changed key change set.
     */
    void addChangedMapKeyChangeSet(Object changeSet) {
        this.getChangedMapKeys().addElement(changeSet);
    }

    /**
     * Add an removed change set.
     */
    void addRemovedChangeSet(Object changeSet) {
        this.getRemoves().addElement(changeSet);
    }

    /**
     * ADVANCED:
     * Return the added stuff.
     * The contents of this collection is determined by the mapping that
     * populated it:<ul>
     * <li>SDKAggregateCollectionMapping will store ObjectChangeSets
     * <li>SDKDirectCollectionMapping will store the direct elements themselves
     * <li>SDKObjectCollectionMapping will store the foreign keys
     * </ul>
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
     * populated it:<ul>
     * <li>SDKAggregateCollectionMapping will store ObjectChangeSets
     * <li>SDKDirectCollectionMapping will store the direct elements themselves
     * <li>SDKObjectCollectionMapping will store the foreign keys
     * </ul>
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
     * populated it:<ul>
     * <li>SDKAggregateCollectionMapping will store ObjectChangeSets
     * <li>SDKDirectCollectionMapping will store the direct elements themselves
     * <li>SDKObjectCollectionMapping will store the foreign keys
     * </ul>
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
    void simpleAddChangeSet(Object changeSet) {
        // check whether the change set was removed earlier
        if (!this.removeRemovedChangeSet(changeSet)) {
            this.addAddedChangeSet(changeSet);
        }
    }

    /**
     * Remove a change set after it has been applied.
     */
    void simpleRemoveChangeSet(Object changeSet) {
        // check whether the change set was added earlier
        if (!this.removeAddedChangeSet(changeSet)) {
            this.addRemovedChangeSet(changeSet);
        }
    }
}