/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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

import java.util.*;

import org.eclipse.persistence.descriptors.ClassDescriptor;

/**
 * <p>
 * <b>Purpose</b>: This class holds the record of the changes made to a collection attribute of
 * an object.
 * <p>
 * <b>Description</b>: Collections must be compared to each other and added and removed objects must
 * be recorded separately.
 *
 * NOTE: This class and its sub class are currently not used within TopLink and should be removed.
 */
public class OrderedCollectionChangeRecord extends ChangeRecord implements org.eclipse.persistence.sessions.changesets.OrderedCollectionChangeRecord {
    protected Hashtable addObjectList;
    protected Vector addIndexes;
    protected int startIndexOfRemove;

    /**
     * This constructor returns a changeRecord representing the DirectCollection mapping
     */
    public OrderedCollectionChangeRecord(ObjectChangeSet owner) {
        this.owner = owner;
        this.startIndexOfRemove = Integer.MAX_VALUE;
    }

    /**
     * This method takes a hastable of primitive objects and adds them to the add list.
     */
    public void addAdditionChange(Hashtable additions, Vector indexes, UnitOfWorkChangeSet changes, AbstractSession session) {
        for (Enumeration enumtr = additions.keys(); enumtr.hasMoreElements();) {
            Object index = enumtr.nextElement();
            Object object = additions.get(index);
            Object changeSet = session.getDescriptor(object.getClass()).getObjectBuilder().createObjectChangeSet(object, changes, session);
            additions.put(index, changeSet);
        }

        this.addObjectList = additions;
        this.addIndexes = indexes;
    }

    /**
     * This method returns the collection of indexes in which changes were made to this collection.
     */
    public Vector getAddIndexes() {
        if (this.addIndexes == null) {
            this.addIndexes = new Vector(1);
        }
        return addIndexes;
    }

    /**
     * This method returns the collection of ChangeSets that were added to the collection.
     */
    public Hashtable getAddObjectList() {
        if (this.addObjectList == null) {
            this.addObjectList = new Hashtable(1);
        }
        return addObjectList;
    }

    /**
     * This method returns the index from where objects must be removed from the collection
     */
    public int getStartRemoveIndex() {
        return this.startIndexOfRemove;
    }

    /**
     * INTERNAL:
     * This method will be used to merge one record into another
     */
    public void mergeRecord(ChangeRecord mergeFromRecord, UnitOfWorkChangeSet mergeToChangeSet, UnitOfWorkChangeSet mergeFromChangeSet) {
        // NOTE: if this class is ever used then this method will need to be implemented
    }

    /**
     * INTERNAL:
     * Ensure this change record is ready to by sent remotely for cache synchronization
     * In general, this means setting the CacheSynchronizationType on any ObjectChangeSets
     * associated with this ChangeRecord
     */
    public void prepareForSynchronization(AbstractSession session) {
        Enumeration changes = getAddObjectList().elements();
        while (changes.hasMoreElements()) {
            ObjectChangeSet changedObject = (ObjectChangeSet)changes.nextElement();
            if (changedObject.getSynchronizationType() == ClassDescriptor.UNDEFINED_OBJECT_CHANGE_BEHAVIOR) {
                ClassDescriptor descriptor = session.getDescriptor(changedObject.getClassType(session));
                int syncType = descriptor.getCacheSynchronizationType();
                changedObject.setSynchronizationType(syncType);
                changedObject.prepareChangeRecordsForSynchronization(session);
            }
        }
    }

    /**
     * This method sets the index from where objects must be removed from the collection
     */
    public void setStartRemoveIndex(int startRemoveIndex) {
        this.startIndexOfRemove = startRemoveIndex;
    }

    /**
     * INTERNAL:
     * This method will be used to update the objectsChangeSets references
     */
    public void updateReferences(UnitOfWorkChangeSet mergeToChangeSet, UnitOfWorkChangeSet mergeFromChangeSet) {
        //if this class is ever used this method will need to be implemented
    }
}