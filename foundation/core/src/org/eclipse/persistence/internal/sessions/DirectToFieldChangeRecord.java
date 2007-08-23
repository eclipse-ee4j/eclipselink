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


/**
 * <p>
 * <b>Purpose</b>: To record the changes for attributes that can be represented as DirectToField
 * <p>
 * @see RelatedClasses prototype.changeset.CollectionChangeRecord,prototype.changeset.SingleObjectChangeRecord
 */
public class DirectToFieldChangeRecord extends ChangeRecord implements org.eclipse.persistence.sessions.changesets.DirectToFieldChangeRecord {
    protected Object newValue;

    /**
     * This default constructor is reference internally by SDK XML project to mapp this class
     */
    public DirectToFieldChangeRecord() {
        super();
    }

    /**
     * This constructor returns a changeRecord representing the DirectToField mapping
     * @param owner prototype.changeset.ObjectChangeSet that ObjectChangeSet that uses this record
     */
    public DirectToFieldChangeRecord(ObjectChangeSet owner) {
        this.owner = owner;
    }

    /**
     * ADVANCED:
     * Returns the new value assigned during the change
     * @return java.lang.Object
     */
    public Object getNewValue() {
        return newValue;
    }

    /**
     * INTERNAL:
     * This method will be used to merge one record into another
     */
    public void mergeRecord(ChangeRecord mergeFromRecord, UnitOfWorkChangeSet mergeToChangeSet, UnitOfWorkChangeSet mergeFromChangeSet) {
        this.newValue = ((DirectToFieldChangeRecord)mergeFromRecord).getNewValue();
    }

    /**
     * ADVANCED:
     * Sets the new value assigned during the change
     * @param newValue java.lang.Object
     */
    public void setNewValue(Object newValue) {
        this.newValue = newValue;
    }

    /**
     * INTERNAL:
     * used by the record to update the new value ignores the value in the default implementation
     */
    public void updateChangeRecordWithNewValue(Object newValue) {
        setNewValue(newValue);
    }

    /**
     * INTERNAL:
     * This method will be used to update the objectsChangeSets references
     */
    public void updateReferences(UnitOfWorkChangeSet mergeToChangeSet, UnitOfWorkChangeSet mergeFromChangeSet) {
    }
}