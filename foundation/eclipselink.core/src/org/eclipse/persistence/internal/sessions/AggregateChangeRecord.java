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

import org.eclipse.persistence.descriptors.ClassDescriptor;

/**
 * This change Record is used to record the changes for AggregateObjectMapping.
 */
public class AggregateChangeRecord extends ChangeRecord implements org.eclipse.persistence.sessions.changesets.AggregateChangeRecord {
    protected org.eclipse.persistence.sessions.changesets.ObjectChangeSet changedObject;

    /**
     * This default constructor is reference internally by SDK XML project to mapp this class
     */
    public AggregateChangeRecord() {
        super();
    }

    /**
     * This constructor returns an ChangeRecord representing.
     * an AggregateMapping.
     * @param owner prototype.changeset.ObjectChangeSet represents the changeSet that uses this record
     */
    public AggregateChangeRecord(ObjectChangeSet owner) {
        this.owner = owner;
    }

    /**
     * ADVANCED:
     * This method is used to return the ObjectChangeSet representing the changed Aggregate.
     * @return prototype.changeset.ObjectChanges
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
        ((ObjectChangeSet)this.changedObject).mergeObjectChanges((ObjectChangeSet)((AggregateChangeRecord)mergeFromRecord).getChangedObject(), mergeToChangeSet, mergeFromChangeSet);
    }

    /**
     * INTERNAL:
     * Ensure this change record is ready to by sent remotely for cache synchronization
     * In general, this means setting the CacheSynchronizationType on any ObjectChangeSets
     * associated with this ChangeRecord
     */
    public void prepareForSynchronization(AbstractSession session) {
        if ((changedObject != null) && (((org.eclipse.persistence.internal.sessions.ObjectChangeSet)changedObject).getSynchronizationType() == ClassDescriptor.UNDEFINED_OBJECT_CHANGE_BEHAVIOR)) {
            ClassDescriptor descriptor = session.getDescriptor(changedObject.getClassType(session));
            int syncType = descriptor.getCacheSynchronizationType();
            ((org.eclipse.persistence.internal.sessions.ObjectChangeSet)changedObject).setSynchronizationType(syncType);
            ((org.eclipse.persistence.internal.sessions.ObjectChangeSet)changedObject).prepareChangeRecordsForSynchronization(session);
        }
    }

    /**
     * INTERNAL:
     * This method is used to set the changed value or values
     * @param newValue prototype.changeset.ObjectChanges
     */
    public void setChangedObject(org.eclipse.persistence.sessions.changesets.ObjectChangeSet newValue) {
        changedObject = newValue;
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
}