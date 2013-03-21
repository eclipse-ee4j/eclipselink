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
 * <b>Purpose</b>: To record the changes for an attribute that references a single Object
 */
public class ObjectReferenceChangeRecord extends ChangeRecord implements org.eclipse.persistence.sessions.changesets.ObjectReferenceChangeRecord {

    /** This is the object change set that the attribute points to. */
    protected ObjectChangeSet newValue;
    
    /** A reference to the old value must also be stored.  This is only required for the commit and must never be serialized. */
    protected transient Object oldValue;

    /**
     * INTERNAL:
     * This default constructor.
     */
    public ObjectReferenceChangeRecord() {
        super();
    }

    /**
     * INTERNAL:
     * This Constructor is used to create an ObjectReferenceChangeRecord With an owner
     */
    public ObjectReferenceChangeRecord(ObjectChangeSet owner) {
        this.owner = owner;
    }

    /**
     * ADVANCED:
     * Returns the new reference for this object
     */
    public org.eclipse.persistence.sessions.changesets.ObjectChangeSet getNewValue() {
        return newValue;
    }

    /**
     * INTERNAL:
     * This method will be used to merge one record into another
     */
    public void mergeRecord(ChangeRecord mergeFromRecord, UnitOfWorkChangeSet mergeToChangeSet, UnitOfWorkChangeSet mergeFromChangeSet) {
        ObjectChangeSet localChangeSet = mergeToChangeSet.findOrIntegrateObjectChangeSet((ObjectChangeSet)((ObjectReferenceChangeRecord)mergeFromRecord).getNewValue(), mergeFromChangeSet);
        this.newValue = localChangeSet;
    }

    /**
     * This method sets the value of the change to be made.
     * @param newValue ObjectChangeSet
     */
    public void setNewValue(org.eclipse.persistence.sessions.changesets.ObjectChangeSet newValue) {
        this.newValue = (ObjectChangeSet)newValue;
    }

    /**
     * This method sets the value of the change to be made.
     */
    public void setNewValue(ObjectChangeSet newValue) {
        this.newValue = newValue;
    }
    
    /**
     * Return the old value of the object reference.
     * This is used during the commit for private-owned references.
     */
    public Object getOldValue() {
        return oldValue;
    }
    
    /**
     * Set the old value of the object reference.
     * This is used during the commit for private-owned references.
     */
    public void setOldValue(Object oldValue) {
        this.oldValue = oldValue;
    }

    /**
     * INTERNAL:
     * This method will be used to update the objectsChangeSets references
     */
    public void updateReferences(UnitOfWorkChangeSet mergeToChangeSet, UnitOfWorkChangeSet mergeFromChangeSet) {
        this.setNewValue(mergeToChangeSet.findOrIntegrateObjectChangeSet((ObjectChangeSet)this.getNewValue(), mergeFromChangeSet));
    }
}
