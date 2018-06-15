/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.sessions;

import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.sessions.Record;

/**
 * <p>
 * <b>Purpose</b>: To record the changes for attributes that can be represented as Transformation Mapping
 * <p>
 */
public class TransformationMappingChangeRecord extends ChangeRecord implements org.eclipse.persistence.sessions.changesets.TransformationMappingChangeRecord {
    protected AbstractRecord rowCollection;
    protected transient Object oldValue;

    /**
     * This default constructor.
     */
    public TransformationMappingChangeRecord() {
        super();
    }

    /**
     * This is the basic constructor for this change Record.
     * This change record stores the fields that have changed as that is our current method of comparing
     * changes.
     */
    public TransformationMappingChangeRecord(ObjectChangeSet owner) {
        this.owner = owner;
    }

    /**
     * ADVANCED:
     * This method is used to access the changes of the fields in a transformation mapping.
     */
    @Override
    public Record getRecord() {
        if (rowCollection == null) {
            this.rowCollection = new DatabaseRecord();
        }
        return rowCollection;
    }

    /**
     * INTERNAL:
     * This method will be used to merge one record into another
     */
    @Override
    public void mergeRecord(ChangeRecord mergeFromRecord, UnitOfWorkChangeSet mergeToChangeSet, UnitOfWorkChangeSet mergeFromChangeSet) {
        this.rowCollection = (AbstractRecord)((TransformationMappingChangeRecord)mergeFromRecord).getRecord();
    }

    /**
     * This method is used to add a changed value to the changeRecord.  The changes in a transformation
     * mapping are recorded on the database field level
     * @param Record Record the values from the transformation mapping
     */
    public void setRow(AbstractRecord record) {
        this.rowCollection = record;
    }

    /**
     * INTERNAL:
     * This method will be used to update the objectsChangeSets references
     * There is nothing to do for this mapping type as there are no object
     * references
     */
    @Override
    public void updateReferences(UnitOfWorkChangeSet mergeToChangeSet, UnitOfWorkChangeSet mergeFromChangeSet) {
    }

    /**
     * ADVANCED:
     * Return the old value of the attribute represented by this ChangeRecord.
     */
    @Override
    public Object getOldValue() {
        return this.oldValue;
    }

    /**
     * INTERNAL:
     * Set the old value of the attribute represented by this ChangeRecord.
     */
    public void setOldValue(Object oldValue) {
        this.oldValue = oldValue;
    }
}
