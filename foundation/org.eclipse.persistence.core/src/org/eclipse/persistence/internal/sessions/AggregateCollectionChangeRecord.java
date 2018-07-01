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
package org.eclipse.persistence.internal.sessions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.AggregateCollectionMapping;

/**
 * This change record records the changes for AggregateCollectionMapping.
 */
public class AggregateCollectionChangeRecord extends CollectionChangeRecord implements org.eclipse.persistence.sessions.changesets.AggregateCollectionChangeRecord {
    protected List<ObjectChangeSet> changedValues;

    /**
     * This default constructor.
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
     */
    public List<ObjectChangeSet> getChangedValues() {
        if (changedValues == null) {
            changedValues = new ArrayList(2);
        }
        return changedValues;
    }

    /**
     * Returns descriptor corresponding to the object.
     */
    ClassDescriptor getReferenceDescriptor(Object object, AbstractSession session) {
        return ((AggregateCollectionMapping)this.mapping).getReferenceDescriptor(object.getClass(), session);
    }

    /**
     * INTERNAL:
     * This method will be used to merge one record into another
     */
    public void mergeRecord(ChangeRecord mergeFromRecord, UnitOfWorkChangeSet mergeToChangeSet, UnitOfWorkChangeSet mergeFromChangeSet) {
        this.setChangedValues(((AggregateCollectionChangeRecord)mergeFromRecord).getChangedValues());

        //an aggregate collection changerecord contains a copy of the entire collection, not just the changes
        //so there in no need to merge it, just replace it.
        for (ObjectChangeSet change : getChangedValues()) {
            change.updateReferences(mergeToChangeSet, mergeFromChangeSet);
        }
    }

    /**
     * INTERNAL:
     * Set the changed values.
     */
    public void setChangedValues(List<ObjectChangeSet> newValues) {
        this.changedValues = newValues;
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
        for (ObjectChangeSet mergedChangeSet : getChangedValues()) {
            Object localObject = mergeToChangeSet.getUOWCloneForObjectChangeSet(mergedChangeSet);
            if (localObject == null) {
                mergeToChangeSet.addObjectChangeSetForIdentity(mergedChangeSet, mergeFromChangeSet.getUOWCloneForObjectChangeSet(mergedChangeSet));
            }
        }
    }
}
