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
package org.eclipse.persistence.internal.sessions;


/**
 * <p>
 * <b>Purpose</b>: To record the changes for attributes that can be represented as DirectToField
 */
public class DirectToFieldChangeRecord extends ChangeRecord implements org.eclipse.persistence.sessions.changesets.DirectToFieldChangeRecord {
    protected Object newValue;
    protected transient Object oldValue;

    /**
     * This default constructor.
     */
    public DirectToFieldChangeRecord() {
        super();
    }

    /**
     * This constructor returns a changeRecord representing the DirectToField mapping.
     */
    public DirectToFieldChangeRecord(ObjectChangeSet owner) {
        this.owner = owner;
    }

    /**
     * ADVANCED:
     * Returns the new value assigned during the change.
     */
    public Object getNewValue() {
        return newValue;
    }

    /**
     * INTERNAL:
     * This method will be used to merge one record into another.
     */
    public void mergeRecord(ChangeRecord mergeFromRecord, UnitOfWorkChangeSet mergeToChangeSet, UnitOfWorkChangeSet mergeFromChangeSet) {
        this.newValue = ((DirectToFieldChangeRecord)mergeFromRecord).getNewValue();
    }

    /**
     * ADVANCED:
     * Sets the new value assigned during the change.
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

    /**
     * ADVANCED:
     * Return the old value of the attribute represented by this ChangeRecord.
     */
    public Object getOldValue() {
        return oldValue;
    }
    
    /**
     * INTERNAL:
     * Set the old value of the attribute represented by this ChangeRecord.
     */
    public void setOldValue(Object oldValue) {
        this.oldValue = oldValue;
    }
}
