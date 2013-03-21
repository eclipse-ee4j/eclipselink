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

import java.util.*;
import java.io.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.CustomObjectInputStream;
import org.eclipse.persistence.internal.identitymaps.CacheId;

/**
 * <p>
 * <b>Purpose</b>: This is the overall collection of changes.
 * <p>
 * <b>Description</b>: It holds all of the object changes and
 * all ObjectChanges, with the same classType and primary keys, referenced in a changeSet should be
 * the same object.
 * <p>
 */
public class UnitOfWorkChangeSet implements Serializable, org.eclipse.persistence.sessions.changesets.UnitOfWorkChangeSet {

    /** This is the collection of ObjectChanges held by this ChangeSet */
    // *** TODO fix transients *** */
    protected Map<Class, Map<ObjectChangeSet, ObjectChangeSet>> objectChanges;

    // This collection holds the new objects which will have no real identity until inserted.
    protected Map<Class, Map<ObjectChangeSet, ObjectChangeSet>> newObjectChangeSets;
    protected Map<Object, ObjectChangeSet> cloneToObjectChangeSet;
    protected Map<ObjectChangeSet, Object> objectChangeSetToUOWClone;
    protected Map<ObjectChangeSet, ObjectChangeSet> aggregateChangeSets;
    protected Map<ObjectChangeSet, ObjectChangeSet> allChangeSets;
    protected Map<ObjectChangeSet, ObjectChangeSet> deletedObjects;

    /** This attribute is set to true if a changeSet with changes has been added */
    protected boolean hasChanges;
    protected boolean hasForcedChanges;
    
    /**
     * Flag set when calling commitToDatabaseWithPreBuiltChangeSet 
     * so we are aware the UOW does not contain the changes from this change set.
     */
    protected boolean isChangeSetFromOutsideUOW = false;

    /** Stores unit of work before it is serialized. */
    protected transient AbstractSession session;
    
    /**
     * INTERNAL:
     * Create a ChangeSet
     */
    public UnitOfWorkChangeSet() {
        super();
        this.setHasChanges(false);
    }
    
    /**
     * INTERNAL:
     * Create a ChangeSet
     */
    public UnitOfWorkChangeSet(AbstractSession session) {
        this.session = session;
    }

    /**
     * Return the session.
     * This only exists before serialization.
     */
    public AbstractSession getSession() {
        return session;
    }

    /**
     * INTERNAL:
     * Set the session.
     * This only exists before serialization.
     */
    public void setSession(AbstractSession session) {
        this.session = session;
    }
    
    /**
     * INTERNAL:
     * Recreate a UnitOfWorkChangeSet that has been converted to a byte array with the
     * getByteArrayRepresentation() method.
     * The passed session ensures that the correct class loader is used for deserialization.
     * That allows to deserialize instances of user-defined classes such as enums.
     * See Bug 280129: WLS JMS CacheCoordination fails if enum changed with ClassNotFoundException.
     */
    public UnitOfWorkChangeSet(byte[] bytes, AbstractSession session) throws java.io.IOException, ClassNotFoundException {
        java.io.ByteArrayInputStream byteIn = new java.io.ByteArrayInputStream(bytes);
        ObjectInputStream objectIn;
        objectIn = new CustomObjectInputStream(byteIn, session);
        // bug 4416412: allChangeSets set directly instead of using setInternalAllChangeSets
        allChangeSets = (Map)objectIn.readObject();
        deletedObjects = (Map)objectIn.readObject();
    }

    /**
     * INTERNAL:
     * Add the Deleted objects to the changeSet.
     */
    public void addDeletedObjects(Map deletedObjects, AbstractSession session) {
        Iterator enumtr = deletedObjects.keySet().iterator();
        while (enumtr.hasNext()) {
            Object object = enumtr.next();
            this.addDeletedObject(object, session);
        }
    }
    
    /**
     * INTERNAL:
     * Add the Deleted object to the changeSet.
     */
    public void addDeletedObject(Object object, AbstractSession session) {
        //CR 4080 - must prevent aggregate objects added to DeletedObjects list
        ClassDescriptor descriptor = session.getDescriptor(object);
        if (!descriptor.isAggregateCollectionDescriptor()) {
            ObjectChangeSet set = descriptor.getObjectBuilder().createObjectChangeSet(object, this, false, session);

            // needed for xml change set
            set.setShouldBeDeleted(true);
            getDeletedObjects().put(set, set);
        }
    }

    /**
     * INTERNAL:
     * Add to the changes for 'object' object to this changeSet. This method 
     * will not add to the lists that are used for identity lookups.
     * The passed change set *must* either have changes or forced changes.
     * @see addObjectChangeSetForIdentity()
     * @param forceToNewObjectList - Any pre commit actions should pass in true 
     * since new objects have extra-handling. Anything post commit, pass in 
     * false.
     */
    public void addObjectChangeSet(ObjectChangeSet objectChanges, AbstractSession session, boolean forceToNewObjectList) {
        if (objectChanges != null) {
             if (objectChanges.isNew() && forceToNewObjectList) {
                // Add it to the new list (unless there is no force, that is, 
                // we are in a post commit and we can trust the cache key then) 
                // so we do not loose it as it may not have a valid primary key 
                // it will be moved to the standard list once it is inserted.
                addNewObjectChangeSet(objectChanges, session);
                getAllChangeSets().put(objectChanges, objectChanges);
            } else {
                // If this object change set has changes or forced changes then 
                // record this. Must be done for each change set added because 
                // some may not contain 'real' changes.  This is the case for 
                // opt. read lock and forceUdpate.  Keep the flags separate 
                // because we don't want to cache sync. a change set with no 
                // 'real' changes.
                boolean objectChangeSetHasChanges = objectChanges.hasChanges();
                if (objectChangeSetHasChanges) {
                    this.setHasChanges(true);
                    this.hasForcedChanges = this.hasForcedChanges || objectChanges.hasForcedChanges();
                } else {
                    // Object change set doesn't have changes so it has to have 
                    // forced changes.
                    this.hasForcedChanges = true;
                }

                if (!objectChanges.isAggregate()) {
                    if (objectChangeSetHasChanges) {
                        // Each time I create a changeSet it is added to this 
                        // list and when I compute a changeSet for this object 
                        // I again add it to these lists so that before this 
                        // UOWChangeSet is Serialized there is a copy of every 
                        // changeSet which has changes affecting cache in 
                        // allChangeSets.
                        getAllChangeSets().put(objectChanges, objectChanges);
                    }
         
                    if (objectChanges.getId() != null) {
                        Map<ObjectChangeSet, ObjectChangeSet> map = getObjectChanges().get(objectChanges.getClassType());

                        if (map == null) {
                            map = new HashMap<ObjectChangeSet, ObjectChangeSet>();
                            getObjectChanges().put(objectChanges.getClassType(), map);
                            map.put(objectChanges, objectChanges);
                        } else {
                            map.put(objectChanges, objectChanges);
                        }
                    }
                }
            }
        }
    }
    
    /**
     * INTERNAL:
     * Add to the changes for 'object' object to this changeSet.  This method will not
     * add to the lists that are used for identity lookups.  It is called specifically
     * for new objects, and new object will be moved to the standard changes list by
     * the QueryMechanism after insert.
     * @see addObjectChangeSetForIdentity()
     * @param objectChanges the new object change set
     */
    protected void addNewObjectChangeSet(ObjectChangeSet objectChanges, AbstractSession session) {
        Map<ObjectChangeSet, ObjectChangeSet> changeSetTable = getNewObjectChangeSets().get(objectChanges.getClassType(session));
        if (changeSetTable == null) {
            // 2612538 - the default size of Map (32) is appropriate
            changeSetTable = new IdentityHashMap<ObjectChangeSet, ObjectChangeSet>();
            getNewObjectChangeSets().put(objectChanges.getClassType(session), changeSetTable);
        }
        changeSetTable.put(objectChanges, objectChanges);
        this.hasChanges = true;
    }

    /**
     * INTERNAL:
     * This method can be used find the equivalent changeset within this UnitOfWorkChangeSet
     * Aggregates, and new objects without primaryKeys from serialized ChangeSets will not be found
     * Which may result in duplicates, in the UnitOfWorkChangeSet.
     */
    public ObjectChangeSet findObjectChangeSet(ObjectChangeSet changeSet, UnitOfWorkChangeSet mergeFromChangeSet) {
        Map<ObjectChangeSet, ObjectChangeSet> changes = getObjectChanges().get(changeSet.getClassName());
        ObjectChangeSet potential = null;
        if (changes != null) {
            potential = changes.get(changeSet);
        }
        if (potential == null) {
            potential = (ObjectChangeSet)getObjectChangeSetForClone(changeSet.getUnitOfWorkClone());
        }
        return potential;
    }

    /**
     * INTERNAL:
     * This method will be used during the merge process to either find an equivalent change set
     * within this UnitOfWorkChangeSet or integrate that changeset into this UOW ChangeSet
     */
    public ObjectChangeSet findOrIntegrateObjectChangeSet(ObjectChangeSet tofind, UnitOfWorkChangeSet mergeFromChangeSet) {
        if (tofind == null) {
            return tofind;
        }
        ObjectChangeSet localChangeSet = this.findObjectChangeSet(tofind, mergeFromChangeSet);
        if (localChangeSet == null) {//not found locally then replace it with the one from the merging changeset
            if (tofind.getDescriptor() == null) {
                tofind.getClassType(this.session);
                tofind.setDescriptor(this.session.getDescriptor(tofind.getClassType()));
            }
            localChangeSet = new ObjectChangeSet(tofind.getId(), tofind.getDescriptor(), tofind.getUnitOfWorkClone(), this, tofind.isNew());
            this.addObjectChangeSetForIdentity(localChangeSet, localChangeSet.getUnitOfWorkClone());
        }
        return localChangeSet;
    }
    
    /**
     * INTERNAL"
     * This method is used during the merge process to either find the existing ChangeSet or create a new one.
     */
    public ObjectChangeSet findOrCreateLocalObjectChangeSet(Object entityClone, ClassDescriptor descriptor, boolean isNew){
        ObjectChangeSet changes = (ObjectChangeSet)this.getObjectChangeSetForClone(entityClone);
        if (changes == null) {
            if (descriptor.isAggregateDescriptor()) {
                changes = new AggregateObjectChangeSet(CacheId.EMPTY, descriptor, entityClone, this, isNew);
            } else {
                changes = new ObjectChangeSet(descriptor.getObjectBuilder().extractPrimaryKeyFromObject(entityClone, session), descriptor, entityClone, this, isNew);
            }
            changes.setIsAggregate(descriptor.isDescriptorTypeAggregate());
            this.addObjectChangeSetForIdentity(changes, entityClone);
        }
        return changes;
    }

    /**
     * INTERNAL:
     * Add change records to the lists used to maintain identity.  This will not actually
     * add the changes to 'object' to the change set.
     * @see addObjectChangeSet()
     * @param objectChanges prototype.changeset.ObjectChanges
     */
    public void addObjectChangeSetForIdentity(ObjectChangeSet objectChanges, Object object) {
        if ((objectChanges == null) || (object == null)) {
            return;
        }

        if (objectChanges.isAggregate()) {
            getAggregateChangeSets().put(objectChanges, objectChanges);
        }

        getObjectChangeSetToUOWClone().put(objectChanges, object);
        getCloneToObjectChangeSet().put(object, objectChanges);

    }

    /**
     * INTERNAL:
     * Get the Aggregate list.  Lazy initializes the map if required.
     */
    public Map<ObjectChangeSet, ObjectChangeSet> getAggregateChangeSets() {
        if (this.aggregateChangeSets == null) {
            this.aggregateChangeSets = new IdentityHashMap<ObjectChangeSet, ObjectChangeSet>();
        }
        return this.aggregateChangeSets;
    }

    /**
     * INTERNAL:
     * This method returns a reference to the collection.
     */
    public Map<ObjectChangeSet, ObjectChangeSet> getAllChangeSets() {
        if (this.allChangeSets == null) {
            // 2612538 - the default size of Map (32) is appropriate
            this.allChangeSets = new IdentityHashMap<ObjectChangeSet, ObjectChangeSet>();
        }
        return this.allChangeSets;
    }

    /**
     * INTERNAL:
     * Get a byte array that can be converted back into the UnitOfWorkChangeSet. This
     * byte array is used by Cache Synchronization for more efficient serialization.
     */
    public byte[] getByteArrayRepresentation(AbstractSession session) throws java.io.IOException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream objectOut = new ObjectOutputStream(byteOut);
        //bug 4416412: Map sent instead of Vector
        Map writableChangeSets = new IdentityHashMap();

        Iterator iterator = getAllChangeSets().values().iterator();
        while (iterator.hasNext()) {
            ObjectChangeSet changeSet = (ObjectChangeSet)iterator.next();

            // navigate through the related change sets here and set their cache synchronization type as well
            ClassDescriptor descriptor = changeSet.getDescriptor();
            int syncType = descriptor.getCachePolicy().getCacheSynchronizationType();

            // Change sets for new objects will only be sent as part of the UnitOfWorkChangeSet
            // if they are meant to be merged into the distributed cache.
            // Note: New objects could still be sent if the are referred to by a change record.
            if ((syncType != ClassDescriptor.DO_NOT_SEND_CHANGES)
                    && (!changeSet.isNew() || (syncType == ClassDescriptor.SEND_NEW_OBJECTS_WITH_CHANGES))) {
                writableChangeSets.put(changeSet, changeSet);
            }
        }
        Map sendableDeletedObjects = new IdentityHashMap();
        iterator = getDeletedObjects().keySet().iterator();
        while (iterator.hasNext()) {
            ObjectChangeSet changeSet = (ObjectChangeSet)iterator.next();

            // navigate through the related change sets here and set their cache synchronization type as well
            ClassDescriptor descriptor = changeSet.getDescriptor();
            int syncType = descriptor.getCacheSynchronizationType();

            // Change sets for new objects will only be sent as part of the UnitOfWorkChangeSet
            // if they are meant to be merged into the distributed cache.
            // Note: New objects could still be sent if the are referred to by a change record.
            if (syncType != ClassDescriptor.DO_NOT_SEND_CHANGES) {
                sendableDeletedObjects.put(changeSet, changeSet);
            }
        }

        // Do not write if nothing to write i.e. only does inserts
        if (writableChangeSets.isEmpty() && sendableDeletedObjects.isEmpty()) {
            return null;
        }
        objectOut.writeObject(writableChangeSets);
        objectOut.writeObject(sendableDeletedObjects);
        return byteOut.toByteArray();
    }

    /**
     * INTERNAL:
     * Get the clone to object change hash table.  Lazy initializes the map if required.
     */
    public Map<Object, ObjectChangeSet> getCloneToObjectChangeSet() {
        if (cloneToObjectChangeSet == null) {
            cloneToObjectChangeSet = new IdentityHashMap();
        }
        return cloneToObjectChangeSet;
    }

    /**
     * INTERNAL:
     * This method returns the reference to the deleted objects from the changeSet.
     */
    public Map<ObjectChangeSet, ObjectChangeSet> getDeletedObjects() {
        if (this.deletedObjects == null) {
            // 2612538 - the default size of Map (32) is appropriate
            this.deletedObjects = new IdentityHashMap();
        }
        return deletedObjects;
    }

    /**
     * INTERNAL:
     * Returns the ObjectChanges held by this ChangeSet.
     */
    public Map<Class, Map<ObjectChangeSet, ObjectChangeSet>> getObjectChanges() {
        if (objectChanges == null) {
            objectChanges = new HashMap<Class, Map<ObjectChangeSet, ObjectChangeSet>>();
        }
        return objectChanges;
    }

    /**
     * INTERNAL:
     * Returns the set of classes corresponding to updated objects in objectChanges.
     * @return HashSet<Class>
     */
    public Set<ClassDescriptor> findUpdatedObjectsClasses() {
        if (this.objectChanges == null || this.objectChanges.isEmpty()) {
            return null;
        }
        HashSet<ClassDescriptor> updatedObjectsClasses = new HashSet<ClassDescriptor>(getObjectChanges().size());
        for (Map<ObjectChangeSet, ObjectChangeSet> objectChanges : getObjectChanges().values()) {
            for (ObjectChangeSet changeSet : objectChanges.values()) {
                // any change set will do
                if(!changeSet.isNew()) {
                    // found updated object - add its class to the set
                    updatedObjectsClasses.add(changeSet.getDescriptor());
                    // and go to the table corresponding to the next class
                    break;
                }
            }
        }
        return updatedObjectsClasses;
    }

    /**
     * ADVANCED:
     * Get ChangeSet for a particular clone
     * @return ObjectChangeSet the changeSet that represents a particular clone
     */
    public org.eclipse.persistence.sessions.changesets.ObjectChangeSet getObjectChangeSetForClone(Object clone) {
        if ((clone == null) || (this.cloneToObjectChangeSet == null)) {
            return null;
        }
        return this.cloneToObjectChangeSet.get(clone);
    }

    /**
     * INTERNAL:
     * This method returns a reference to the collection
     * @return Map
     */
    protected Map<ObjectChangeSet, Object> getObjectChangeSetToUOWClone() {
        if (this.objectChangeSetToUOWClone == null) {
            // 2612538 - the default size of Map (32) is appropriate
            this.objectChangeSetToUOWClone = new IdentityHashMap<ObjectChangeSet, Object>();
        }
        return objectChangeSetToUOWClone;
    }

    /**
     * ADVANCED:
     * This method returns the Clone for a particular changeSet
     * @return Object the clone represented by the changeSet
     */
    public Object getUOWCloneForObjectChangeSet(org.eclipse.persistence.sessions.changesets.ObjectChangeSet changeSet) {
        if ((changeSet == null) || (this.objectChangeSetToUOWClone == null)) {
            return null;
        }
        return this.objectChangeSetToUOWClone.get(changeSet);
    }

    /**
     * INTERNAL:
     * Returns true if the Unit Of Work change Set has changes
     */
    public boolean hasChanges() {
        // All of the object change sets were empty (none contained changes)
        // The this.hasChanges variable is set in addObjectChangeSet
        return (this.hasChanges || (this.deletedObjects != null) && (!this.deletedObjects.isEmpty()));
    }

    /**
     * INTERNAL:
     * Returns true if any deleted objects.
     * This should be used before accessing deleted object to avoid creation of map.
     */
    public boolean hasDeletedObjects() {
        return (this.deletedObjects != null) && (!this.deletedObjects.isEmpty());
    }
    
    /**
     * INTERNAL:
     * Set whether the Unit Of Work change Set has changes
     */
    public void setHasChanges(boolean flag) {
        this.hasChanges = flag;
    }

    /**
     * INTERNAL:
     * Returns true if this uowChangeSet contains an objectChangeSet that has forced
     * SQL changes.  This is true whenever CMPPolicy.getForceUpdate() == true.
     * @return boolean
     */
    public boolean hasForcedChanges() {
        return this.hasForcedChanges;
    }

    /**
     * INTERNAL:
     * This method will be used to merge a change set into an UnitOfWorkChangeSet
     * This method returns the local instance of the changeset
     */
    public ObjectChangeSet mergeObjectChanges(ObjectChangeSet objectChangeSet, UnitOfWorkChangeSet mergeFromChangeSet) {
        ObjectChangeSet localChangeSet = this.findOrIntegrateObjectChangeSet(objectChangeSet, mergeFromChangeSet);
        if (localChangeSet != null) {
            localChangeSet.mergeObjectChanges(objectChangeSet, this, mergeFromChangeSet);
        }
        return localChangeSet;
    }

    /**
    * INTERNAL:
    * THis method will be used to merge another changeset into this changeset.  The
    * Main use of this method is for non-deferred writes and checkpointing so that
    * the accumulated changes are collected and merged at the end of the transaction.
    */
    public void mergeUnitOfWorkChangeSet(UnitOfWorkChangeSet mergeFromChangeSet, AbstractSession session, boolean postCommit) {
        if (mergeFromChangeSet == null) {
            return;
        }
        for (Map<ObjectChangeSet, ObjectChangeSet> objectChanges : mergeFromChangeSet.getObjectChanges().values()) {
            for (ObjectChangeSet objectChangeSet : objectChanges.values()) {
                objectChangeSet = mergeObjectChanges(objectChangeSet, mergeFromChangeSet);
                addObjectChangeSet(objectChangeSet, session, !postCommit);
            }
        }

        //merging a serialized UnitOfWorkChangeSet can result in duplicate deletes
        //if a delete for the same object already exists in this UOWChangeSet.
        if (mergeFromChangeSet.hasDeletedObjects()) {
            for (ObjectChangeSet objectChangeSet : mergeFromChangeSet.getDeletedObjects().values()) {
                ObjectChangeSet localObjectChangeSet = findObjectChangeSet(objectChangeSet, mergeFromChangeSet);
                if (localObjectChangeSet == null) {
                    localObjectChangeSet = objectChangeSet;
                }
                getDeletedObjects().put(localObjectChangeSet, localObjectChangeSet);
            }
        }
    }

    /**
     * INTERNAL:
     * Used to rehash the new objects back into the objectChanges list for serialization
     * Assumes the transaction in in post commit stage.
     */
    public void putNewObjectInChangesList(ObjectChangeSet objectChangeSet, AbstractSession session) {
        // Must reset the cache key for new objects assigned in insert.
        if (objectChangeSet.getId() == null) {
            Object clone = objectChangeSet.getUnitOfWorkClone();
            objectChangeSet.setId(session.getDescriptor(clone.getClass()).getObjectBuilder().extractPrimaryKeyFromObject(clone, session, false));
        }
        addObjectChangeSet(objectChangeSet, session, false);
        removeObjectChangeSetFromNewList(objectChangeSet, session);
    }

    /**
     * INTERNAL:
     * Used to remove a new object from the new objects list once it has been
     * inserted and added to the objectChangesList
     */
    public void removeObjectChangeSetFromNewList(ObjectChangeSet objectChangeSet, AbstractSession session) {
        Map table = getNewObjectChangeSets().get(objectChangeSet.getClassType(session));
        if (table != null) {
            table.remove(objectChangeSet);
        }
    }

    /**
     * INTERNAL:
     * Add the changed Object's records to the ChangeSet.
     */
    public void removeObjectChangeSet(ObjectChangeSet changeSet) {
        if (changeSet == null) {
            return;
        }
        Object object = getObjectChangeSetToUOWClone().get(changeSet);
        if (changeSet.isAggregate()) {
            getAggregateChangeSets().remove(changeSet);
        } else {
            Map classChanges = getObjectChanges().get(object.getClass());
            if (classChanges != null) {
                classChanges.remove(changeSet);
            }
        }
        getObjectChangeSetToUOWClone().remove(changeSet);
        if (object != null) {
            getCloneToObjectChangeSet().remove(object);
        }
        getAllChangeSets().remove(changeSet);
    }

    /**
     * INTERNAL:
     * Set the internal flag that tells that this change set was built outside this
     * UOW and the changes it contains cannot be calculated from the contents of this UOW
     */
    public void setIsChangeSetFromOutsideUOW(boolean isChangeSetFromOutsideUOW){
        this.isChangeSetFromOutsideUOW = isChangeSetFromOutsideUOW;
    }
    
    /**
     * INTERNAL:
     * Get the internal flag that tells that this change set was built outside this
     * UOW and the changes it contains cannot be calculated from the contents of this UOW
     */
    public boolean isChangeSetFromOutsideUOW(){
        return isChangeSetFromOutsideUOW;
    }
    
    /**
     * INTERNAL:
     * This method is used to set the map for cloneToObject reference.
     */
    public void setCloneToObjectChangeSet(Map<Object, ObjectChangeSet> cloneToObjectChangeSet) {
        this.cloneToObjectChangeSet = cloneToObjectChangeSet;
    }

    /**
     * INTERNAL:
     * Sets the collection of ObjectChanges in the change Set.
     */
    protected void setObjectChanges(Map objectChanges) {
        this.objectChanges = objectChanges;
    }

    /**
     * INTERNAL:
     * This method is used to insert a new collection into the UOWChangeSet.
     */
    public void setObjectChangeSetToUOWClone(Map<ObjectChangeSet, Object> objectChangeSetToUOWClone) {
        this.objectChangeSetToUOWClone = objectChangeSetToUOWClone;
    }

    /**
     * INTERNAL:
     * This method will return a reference to the new object change set collections.
     */
    public Map<Class, Map<ObjectChangeSet, ObjectChangeSet>> getNewObjectChangeSets() {
        if (this.newObjectChangeSets == null) {
            this.newObjectChangeSets = new HashMap<Class, Map<ObjectChangeSet, ObjectChangeSet>>();
        }
        return this.newObjectChangeSets;
    }

}
