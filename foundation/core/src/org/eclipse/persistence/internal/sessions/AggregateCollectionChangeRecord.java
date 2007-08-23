/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.sessions;

import java.util.*;

import org.eclipse.persistence.descriptors.ClassDescriptor;

/**
 * This change record records the changes for AggregateCollectionMapping.
 */
public class AggregateCollectionChangeRecord extends ChangeRecord implements org.eclipse.persistence.sessions.changesets.AggregateCollectionChangeRecord {
    protected Vector changedValues;

    /**
     * This default constructor referenced internally by SDK XML project
     */
    public AggregateCollectionChangeRecord() {
        super();
    }

    /**
     * This constructor returns an ChangeRecord representing an AggregateMapping.
     * @param owner org.eclipse.persistence.internal.sessions.ObjectChangeSet represents the Object Change Set that uses this record
     */
    public AggregateCollectionChangeRecord(ObjectChangeSet owner) {
        super();
        this.owner = owner;
    }

    /**
     * ADVANCED:
     * Return the values representing the changed AggregateCollection.
     * @return prototype.changeset.ObjectChanges
     */
    public Vector getChangedValues() {
        if (changedValues == null) {
            changedValues = new Vector(2);
        }
        return changedValues;
    }

    /**
     * INTERNAL:
     * This method will be used to merge one record into another
     */
    public void mergeRecord(ChangeRecord mergeFromRecord, UnitOfWorkChangeSet mergeToChangeSet, UnitOfWorkChangeSet mergeFromChangeSet) {
        this.setChangedValues(((AggregateCollectionChangeRecord)mergeFromRecord).getChangedValues());

        //an aggregate collection changerecord contains a copy of the entire collection, not just the changes
        //so there in no need to merge it, just replace it.
        for (int index = 0; index < this.getChangedValues().size(); ++index) {
            ((ObjectChangeSet)this.getChangedValues().get(index)).updateReferences(mergeToChangeSet, mergeFromChangeSet);
            ;
        }
    }

    /**
     * INTERNAL:
     * Ensure this change record is ready to by sent remotely for cache synchronization
     * In general, this means setting the CacheSynchronizationType on any ObjectChangeSets
     * associated with this ChangeRecord
     */
    public void prepareForSynchronization(AbstractSession session) {
        Enumeration changes = getChangedValues().elements();
        while (changes.hasMoreElements()) {
            ObjectChangeSet changedObject = (ObjectChangeSet)changes.nextElement();
            if (((org.eclipse.persistence.internal.sessions.ObjectChangeSet)changedObject).getSynchronizationType() == ClassDescriptor.UNDEFINED_OBJECT_CHANGE_BEHAVIOR) {
                ClassDescriptor descriptor = session.getDescriptor(changedObject.getClassType(session));
                int syncType = descriptor.getCacheSynchronizationType();
                ((org.eclipse.persistence.internal.sessions.ObjectChangeSet)changedObject).setSynchronizationType(syncType);
                ((org.eclipse.persistence.internal.sessions.ObjectChangeSet)changedObject).prepareChangeRecordsForSynchronization(session);
            }
        }
    }

    /**
     * INTERNAL:
     * Set the changed values
     * @param newValue prototype.changeset.ObjectChanges
     */
    public void setChangedValues(Vector newValues) {
        changedValues = newValues;
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
        for (int index = 0; index < this.getChangedValues().size(); ++index) {
            ObjectChangeSet mergedChangeSet = (ObjectChangeSet)this.getChangedValues().get(index);
            Object localObject = mergeToChangeSet.getUOWCloneForObjectChangeSet(mergedChangeSet);
            if (localObject == null) {
                mergeToChangeSet.addObjectChangeSetForIdentity(mergedChangeSet, mergeFromChangeSet.getUOWCloneForObjectChangeSet(mergedChangeSet));
            }
        }
    }
}