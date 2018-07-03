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
//     03/19/2018-2.7.2 Lukas Jungmann
//       - 496836: NullPointerException on ObjectChangeSet.mergeObjectChanges
package org.eclipse.persistence.internal.sessions;

/**
 * This change Record is used to record the changes for AggregateObjectMapping.
 */
public class AggregateChangeRecord extends ChangeRecord implements org.eclipse.persistence.sessions.changesets.AggregateChangeRecord {
    protected org.eclipse.persistence.sessions.changesets.ObjectChangeSet changedObject;
    protected transient Object oldValue;
    // assign NULL to oldValue to indicate that it has been set to null.
    protected static final String NULL = "NULL";

    /**
     * This default constructor.
     */
    public AggregateChangeRecord() {
        super();
    }

    /**
     * This constructor returns an ChangeRecord representing an AggregateMapping.
     */
    public AggregateChangeRecord(ObjectChangeSet owner) {
        this.owner = owner;
    }

    /**
     * ADVANCED:
     * This method is used to return the ObjectChangeSet representing the changed Aggregate.
     */
    public org.eclipse.persistence.sessions.changesets.ObjectChangeSet getChangedObject() {
        return changedObject;
    }

    /**
     * INTERNAL:
     * This method will be used to merge one record into another
     */
    public void mergeRecord(ChangeRecord mergeFromRecord, UnitOfWorkChangeSet mergeToChangeSet, UnitOfWorkChangeSet mergeFromChangeSet) {
        if (this.changedObject == null) {
            this.changedObject = ((AggregateChangeRecord)mergeFromRecord).getChangedObject();
            if(this.changedObject == null) {
                return;
            } else {
                mergeToChangeSet.addObjectChangeSetForIdentity((ObjectChangeSet)this.changedObject, mergeFromChangeSet.getUOWCloneForObjectChangeSet(this.changedObject));
                ((ObjectChangeSet)this.changedObject).updateReferences(mergeToChangeSet, mergeFromChangeSet);
                return;
            }
        }
        ObjectChangeSet change = (ObjectChangeSet)((AggregateChangeRecord)mergeFromRecord).getChangedObject();
        if (change == null) {
            // bug #496836 - incoming change is setting the aggregate to null
            this.changedObject = null;
        } else {
            ((ObjectChangeSet)this.changedObject).mergeObjectChanges(change, mergeToChangeSet, mergeFromChangeSet);
        }
    }

    /**
     * INTERNAL:
     * This method is used to set the changed value or values.
     */
    public void setChangedObject(org.eclipse.persistence.sessions.changesets.ObjectChangeSet newValue) {
        changedObject = newValue;
        if(this.oldValue == null) {
            this.oldValue = newValue;
        }
    }

    /**
     * INTERNAL:
     * This method will be used to update the objectsChangeSets references
     * If this is an aggregate change set then there is no need to update the
     * reference as the ChangeSet has no identity outside of this record
     * Check to see if it exists here already to prevent us from creating a little
     * extra garbage.
     */
    public void updateReferences(UnitOfWorkChangeSet mergeToChangeSet, UnitOfWorkChangeSet mergeFromChangeSet) {
        Object localChangeSet = mergeToChangeSet.getUOWCloneForObjectChangeSet(this.changedObject);
        if (localChangeSet == null) {
            mergeToChangeSet.addObjectChangeSetForIdentity((ObjectChangeSet)this.changedObject, mergeFromChangeSet.getUOWCloneForObjectChangeSet(this.changedObject));
        }
    }

    /**
     * ADVANCED:
     * If the owning UnitOfWork has shouldChangeRecordKeepOldValue set to true,
     * then return the old value of the attribute represented by this ChangeRecord.
     */
    public Object getOldValue() {
        if (oldValue == NULL) {
            return null;
        } else if (oldValue instanceof ObjectChangeSet) {
            ObjectChangeSet changeSet = (ObjectChangeSet)oldValue;
            return changeSet.getOldValue();
        } else {
            return this.oldValue;
        }
    }

    /**
     * INTERNAL:
     * Set the old value of the attribute represented by this ChangeRecord.
     */
    public void setOldValue(Object oldValue) {
        if (oldValue == null) {
            this.oldValue = NULL;
        } else {
            this.oldValue = oldValue;
        }
    }
}
