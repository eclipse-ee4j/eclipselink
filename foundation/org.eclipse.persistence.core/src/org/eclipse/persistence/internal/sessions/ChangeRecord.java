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

import org.eclipse.persistence.mappings.*;
import java.io.Serializable;

/**
 * <p>
 * <b>Purpose</b>: This class was designed as a superclass to all possible Change Record types.
 * These Change Records holds the changes made to the objects
 */
public abstract class ChangeRecord implements Serializable, org.eclipse.persistence.sessions.changesets.ChangeRecord {

    /**
     * This is the attribute name that this change record represents
     */
    protected String attribute;

    /**
     * This attribute stores the mapping along with the attribute so that the mapping does not need to be looked up
     */
    protected transient DatabaseMapping mapping;

    /** This is the object change set that holds this record **/
    protected ObjectChangeSet owner;

    /**
     * ADVANCED:
     * Returns the name of the attribute this ChangeRecord Represents
     * @return String
     */
    public String getAttribute() {
        return attribute;
    }

    /**
     * ADVANCED:
     * Returns the mapping for the attribute this ChangeRecord Represents
     */
    public DatabaseMapping getMapping() {
        return mapping;
    }

    public org.eclipse.persistence.sessions.changesets.ObjectChangeSet getOwner() {
        return owner;
    }

    /**
     * INTERNAL:
     * This method will be used to merge one record into another
     */
    public abstract void mergeRecord(ChangeRecord mergeFromRecord, UnitOfWorkChangeSet mergeToChangeSet, UnitOfWorkChangeSet mergeFromChangeSet);

    /**
     * INTERNAL:
     * Ensure this change record is ready to by sent remotely for cache synchronization
     * In general, this means setting the CacheSynchronizationType on any ObjectChangeSets
     * associated with this ChangeRecord
     */
    public void prepareForSynchronization(AbstractSession session) {
    }

    /**
     * Sets the name of the attribute that this Record represents.
     */
    public void setAttribute(String newValue) {
        this.attribute = newValue;
    }

    /**
     * Sets the mapping for the attribute that this Record represents
     */
    public void setMapping(DatabaseMapping mapping) {
        this.mapping = mapping;
    }

    /**
     * INTERNAL:
     * This method is used to set the ObjectChangeSet that uses this Record in that Record.
     * @param ObjectChangeSet The changeSet that uses this record.
     */
    public void setOwner(ObjectChangeSet newOwner) {
        owner = newOwner;
    }

    public String toString() {
        return this.getClass().getName() + "(" + getAttribute() + ")";
    }

    /**
     * INTERNAL:
     * used by the record to update the new value ignores the value in the default implementation
     */
    public void updateChangeRecordWithNewValue(Object newValue) {
        //no op
    }

    /**
     * INTERNAL:
     * This method will be used to update the objectsChangeSets references
     */
    public abstract void updateReferences(UnitOfWorkChangeSet mergeToChangeSet, UnitOfWorkChangeSet mergeFromChangeSet);
}
