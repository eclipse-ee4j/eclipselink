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
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.localization.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.changetracking.AttributeChangeTrackingPolicy;

/**
 * This class maintains a commit stack and resolves circular references.
 */
public class CommitManager {
    protected Vector commitOrder;

    /** Changed the folowing line to work like mergemanager.  The commitManager
     * will now track what has been processed as apposed to removing from the list
     * objects that have been processed.  This must be done to allow for customers
     * modifying the changesets in events
     */
    protected Map processedCommits;
    protected Map pendingCommits;
    protected Map preModifyCommits;
    protected Map postModifyCommits;
    protected Map completedCommits;
    protected Map shallowCommits;
    protected AbstractSession session;
    protected boolean isActive;
    protected Hashtable dataModifications;
    protected Vector objectsToDelete;

    /**
     * Create the commit manager on the session.
     * It must be initialized later on after the descriptors have been added.
     */
    public CommitManager(AbstractSession session) {
        this.session = session;
        this.commitOrder = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance();
        this.isActive = false;

        // PERF - move to lazy initialization (3286091)
        //this.processedCommits = new IdentityHashMap(20);
        //this.pendingCommits = new IdentityHashMap(20);
        //this.preModifyCommits = new IdentityHashMap(20);
        //this.postModifyCommits = new IdentityHashMap(20);
        //this.completedCommits = new IdentityHashMap(20);
        //this.shallowCommits = new IdentityHashMap(20);
    }

    /**
     * Add the data query to be performed at the end of the commit.
     * This is done to decrease dependencies and avoid deadlock.
     */
    public void addDataModificationEvent(DatabaseMapping mapping, Object[] event) {
        // For lack of inner class the array is being called an event.
        if (!getDataModifications().containsKey(mapping)) {
            getDataModifications().put(mapping, new Vector());
        }

        ((Vector)getDataModifications().get(mapping)).addElement(event);
    }

    /**
     * Deletion are cached until the end.
     */
    public void addObjectToDelete(Object objectToDelete) {
        getObjectsToDelete().addElement(objectToDelete);
    }

    /**
     * add the commit of the object to the processed list.
     */
    protected void addProcessedCommit(Object domainObject) {
        getProcessedCommits().put(domainObject, domainObject);
    }

    /**
     * Commit all of the objects as a single transaction.
     * This should commit the object in the correct order to maintain referencial integrity.
     */
    public void commitAllObjects(Map domainObjects) throws RuntimeException, DatabaseException, OptimisticLockException {
        reinitialize();
        setPendingCommits(domainObjects);

        setIsActive(true);
        getSession().beginTransaction();
        try {
            // The commit order is all of the classes ordered by dependencies, this is done for dealock avoidance.
            for (Enumeration classesEnum = getCommitOrder().elements();
                     classesEnum.hasMoreElements();) {
                Class theClass = (Class)classesEnum.nextElement();

                for (Iterator pendingEnum = getPendingCommits().values().iterator();
                         pendingEnum.hasNext();) {
                    Object objectToWrite = pendingEnum.hasNext();

                    // Old commit is not supported for attribute change tracking.
                    if (getSession().getDescriptor(objectToWrite).getObjectChangePolicy() instanceof AttributeChangeTrackingPolicy) {
                        throw ValidationException.oldCommitNotSupportedForAttributeTracking();
                    }
                    if (objectToWrite.getClass() == theClass) {
                        removePendingCommit(objectToWrite);// I think removing while enumerating is ok.

                        WriteObjectQuery commitQuery = new WriteObjectQuery();
                        commitQuery.setIsExecutionClone(true);
                        commitQuery.setObject(objectToWrite);
                        if (getSession().isUnitOfWork()) {
                            commitQuery.cascadeOnlyDependentParts();
                        } else {
                            commitQuery.cascadeAllParts();// Used in write all objects in session.
                        }
                        getSession().executeQuery(commitQuery);
                    }
                }
            }

            for (Enumeration mappingsEnum = getDataModifications().keys(), mappingEventsEnum = getDataModifications().elements();
                     mappingEventsEnum.hasMoreElements();) {
                Vector events = (Vector)mappingEventsEnum.nextElement();
                DatabaseMapping mapping = (DatabaseMapping)mappingsEnum.nextElement();
                for (Enumeration eventsEnum = events.elements(); eventsEnum.hasMoreElements();) {
                    Object[] event = (Object[])eventsEnum.nextElement();
                    mapping.performDataModificationEvent(event, getSession());
                }
            }

            Vector objects = getObjectsToDelete();
            reinitialize();
            for (Enumeration objectsToDeleteEnum = objects.elements();
                     objectsToDeleteEnum.hasMoreElements();) {
                getSession().deleteObject(objectsToDeleteEnum.nextElement());
            }
        } catch (RuntimeException exception) {
            getSession().rollbackTransaction();
            throw exception;
        } finally {
            reinitialize();
            setIsActive(false);
        }

        getSession().commitTransaction();
    }

    /**
     * Commit all of the objects as a single transaction.
     * This should commit the object in the correct order to maintain referencial integrity.
     */
    public void commitAllObjectsWithChangeSet(UnitOfWorkChangeSet uowChangeSet) throws RuntimeException, DatabaseException, OptimisticLockException {
        reinitialize();
        setIsActive(true);
        getSession().beginTransaction();
        try {
            // PERF: if the number of classes in the project is large this loop can be a perf issue.
            // If only one class types changed, then avoid loop.
            if ((uowChangeSet.getObjectChanges().size() + uowChangeSet.getNewObjectChangeSets().size()) <= 1) {
                Enumeration classes = uowChangeSet.getNewObjectChangeSets().keys();
                if (classes.hasMoreElements()) {
                    Class theClass = (Class)classes.nextElement();
                    commitNewObjectsForClassWithChangeSet(uowChangeSet, theClass);
                }
                Enumeration classNames = uowChangeSet.getObjectChanges().keys();
                if (classNames.hasMoreElements()) {
                    String className = (String)classNames.nextElement();
                    commitChangedObjectsForClassWithChangeSet(uowChangeSet, className);
                }
            } else {
                // The commit order is all of the classes ordered by dependencies, this is done for dealock avoidance.
                for (Enumeration classesEnum = getCommitOrder().elements();
                         classesEnum.hasMoreElements();) {
                    Class theClass = (Class)classesEnum.nextElement();
                    commitAllObjectsForClassWithChangeSet(uowChangeSet, theClass);
                }
            }

            if (hasDataModifications()) {
                // Perform all batched up data modifications, done to avoid dependecies.
                for (Enumeration mappingsEnum = getDataModifications().keys(), mappingEventsEnum = getDataModifications().elements();
                         mappingEventsEnum.hasMoreElements();) {
                    Vector events = (Vector)mappingEventsEnum.nextElement();
                    DatabaseMapping mapping = (DatabaseMapping)mappingsEnum.nextElement();
                    for (Enumeration eventsEnum = events.elements(); eventsEnum.hasMoreElements();) {
                        Object[] event = (Object[])eventsEnum.nextElement();
                        mapping.performDataModificationEvent(event, getSession());
                    }
                }
            }

            if (hasObjectsToDelete()) {
                Vector objects = getObjectsToDelete();
                reinitialize();
                for (Enumeration objectsToDeleteEnum = objects.elements();
                         objectsToDeleteEnum.hasMoreElements();) {
                    getSession().deleteObject(objectsToDeleteEnum.nextElement());
                }
            }
        } catch (RuntimeException exception) {
            getSession().rollbackTransaction();
            throw exception;
        } finally {
            reinitialize();
            setIsActive(false);
        }

        getSession().commitTransaction();
    }

    /**
     * Commit all of the objects of the class type in the change set.
     * This allows for the order of the classes to be processed optimally.
     */
    protected void commitAllObjectsForClassWithChangeSet(UnitOfWorkChangeSet uowChangeSet, Class theClass) {    
        // Although new objects should be first, there is an issue that new objects get added to non-new after the insert,
        // so the object would be written twice.
        commitChangedObjectsForClassWithChangeSet(uowChangeSet, theClass.getName());
        commitNewObjectsForClassWithChangeSet(uowChangeSet, theClass);
    }

    /**
     * Commit all of the objects of the class type in the change set.
     * This allows for the order of the classes to be processed optimally.
     */
    protected void commitNewObjectsForClassWithChangeSet(UnitOfWorkChangeSet uowChangeSet, Class theClass) {
        Map newObjectChangesList = (Map)uowChangeSet.getNewObjectChangeSets().get(theClass);
        if (newObjectChangesList != null) { // may be no changes for that class type.
            ClassDescriptor descriptor = getSession().getDescriptor(theClass);
            for (Iterator pendingEnum = new IdentityHashMap(newObjectChangesList).values().iterator();
                     pendingEnum.hasNext();) {
                ObjectChangeSet changeSetToWrite = (ObjectChangeSet)pendingEnum.next();
                Object objectToWrite = changeSetToWrite.getUnitOfWorkClone();
                if ((!getProcessedCommits().containsKey(changeSetToWrite)) && (!getProcessedCommits().containsKey(objectToWrite))) {
                    addProcessedCommit(changeSetToWrite);
                    InsertObjectQuery commitQuery = new InsertObjectQuery();
                    commitQuery.setIsExecutionClone(true);
                    commitQuery.setDescriptor(descriptor);
                    commitQuery.setObjectChangeSet(changeSetToWrite);
                    commitQuery.setObject(objectToWrite);
                    commitQuery.cascadeOnlyDependentParts();
                    // removed checking session type to set cascade level
                    // will always be a unitOfWork so we need to cascade dependent parts
                    getSession().executeQuery(commitQuery);
                }
                ((UnitOfWorkImpl)getSession()).updateChangeTrackersIfRequired(objectToWrite, changeSetToWrite, (UnitOfWorkImpl)getSession(), descriptor);

                //after the query has executed lets clear the change detection policies
                //this is important for write changes and non deferred writes support
            }
        }
    }

    /**
     * Commit changed of the objects of the class type in the change set.
     * This allows for the order of the classes to be processed optimally.
     */
    protected void commitChangedObjectsForClassWithChangeSet(UnitOfWorkChangeSet uowChangeSet, String className) {
        Hashtable objectChangesList = (Hashtable)uowChangeSet.getObjectChanges().get(className);
        if (objectChangesList != null) {// may be no changes for that class type.				
            ClassDescriptor descriptor = null;
            for (Enumeration pendingEnum = objectChangesList.elements();
                     pendingEnum.hasMoreElements();) {
                ObjectChangeSet changeSetToWrite = (ObjectChangeSet)pendingEnum.nextElement();
                if (descriptor == null) {
                    // Need a class to get descriptor, for some evil reason the keys are class name.
                    descriptor = getSession().getDescriptor(changeSetToWrite.getClassType(getSession()));
                }
                Object objectToWrite = changeSetToWrite.getUnitOfWorkClone();
                if ((!getProcessedCommits().containsKey(changeSetToWrite)) && (!getProcessedCommits().containsKey(objectToWrite))) {
                    addProcessedCommit(changeSetToWrite);
                    // Commit and resume on failure can cause a new change set to be in existing, so need to check here.
                    WriteObjectQuery commitQuery = null;
                    if (changeSetToWrite.isNew()) {
                        commitQuery = new InsertObjectQuery();
                    } else {
                        commitQuery = new UpdateObjectQuery();
                    }
                    commitQuery.setIsExecutionClone(true);
                    commitQuery.setDescriptor(descriptor);
                    commitQuery.setObjectChangeSet(changeSetToWrite);
                    commitQuery.setObject(objectToWrite);
                    commitQuery.cascadeOnlyDependentParts();
                    // removed checking session type to set cascade level
                    // will always be a unitOfWork so we need to cascade dependent parts
                    getSession().executeQuery(commitQuery);
                }
                ((UnitOfWorkImpl)getSession()).updateChangeTrackersIfRequired(objectToWrite, changeSetToWrite, (UnitOfWorkImpl)getSession(), descriptor);

                // after the query has executed lets clear the change detection policies
                // this is important for write changes and non deferred writes support
            }
        }
    }

    /**
     * delete all of the objects as a single transaction.
     * This should delete the object in the correct order to maintain referencial integrity.
     */
    public void deleteAllObjects(Vector objectsForDeletion) throws RuntimeException, DatabaseException, OptimisticLockException {
        setIsActive(true);
        getSession().beginTransaction();

        try {
            for (int index = getCommitOrder().size() - 1; index >= 0; index--) {
                Class theClass = (Class)getCommitOrder().elementAt(index);

                for (Enumeration objectsForDeletionEnum = objectsForDeletion.elements();
                         objectsForDeletionEnum.hasMoreElements();) {
                    Object objectToDelete = objectsForDeletionEnum.nextElement();
                    if (objectToDelete.getClass() == theClass) {
                        DeleteObjectQuery deleteQuery = new DeleteObjectQuery();
                        deleteQuery.setIsExecutionClone(true);
                        deleteQuery.setObject(objectToDelete);
                        getSession().executeQuery(deleteQuery);
                    }
                }
            }
        } catch (RuntimeException exception) {
            try {
                getSession().rollbackTransaction();
            } catch (Exception ignore) {
            }
            throw exception;
        } finally {
            setIsActive(false);
        }

        getSession().commitTransaction();
    }

    /**
     * Return the order in which objects should be commited to the database.
     * This order is based on ownership in the descriptors and is require for referencial integrity.
     * The commit order is a vector of vectors,
     * where the first vector is all root level classes, the second is classes owned by roots and so on.
     */
    public Vector getCommitOrder() {
        return commitOrder;
    }

    /**
     * Return any objects that have been written during this commit process.
     */
    protected Map getCompletedCommits() {
        if (completedCommits == null) {
            // 2612538 - the default size of Map (32) is appropriate
            completedCommits = new IdentityHashMap();
        }
        return completedCommits;
    }

    protected boolean hasDataModifications() {
        return ((dataModifications != null) && (!dataModifications.isEmpty()));
    }

    /**
     * Used to store data querys to be performed at the end of the commit.
     * This is done to decrease dependencies and avoid deadlock.
     */
    protected Hashtable getDataModifications() {
        if (dataModifications == null) {
            dataModifications = new Hashtable(10);
        }
        return dataModifications;
    }

    protected boolean hasObjectsToDelete() {
        return ((objectsToDelete != null) && (!objectsToDelete.isEmpty()));
    }

    /**
     * Deletion are cached until the end.
     */
    protected Vector getObjectsToDelete() {
        if (objectsToDelete == null) {
            objectsToDelete = new Vector(5);
        }
        return objectsToDelete;
    }

    /**
     * Return any objects that should be written during this commit process.
     */
    protected Map getProcessedCommits() {
        if (processedCommits == null) {
            // 2612538 - the default size of Map (32) is appropriate
            processedCommits = new IdentityHashMap();
        }
        return processedCommits;
    }

    /**
     * Return any objects that should be written during this commit process.
     */
    protected Map getPendingCommits() {
        if (pendingCommits == null) {
            // 2612538 - the default size of Map (32) is appropriate
            pendingCommits = new IdentityHashMap();
        }
        return pendingCommits;
    }

    /**
     * Return any objects that should be written during post modify commit process.
     * These objects should be order by their ownership constraints to maintain referencial integrity.
     */
    protected Map getPostModifyCommits() {
        if (postModifyCommits == null) {
            // 2612538 - the default size of Map (32) is appropriate
            postModifyCommits = new IdentityHashMap();
        }
        return postModifyCommits;
    }

    /**
     * Return any objects that should be written during pre modify commit process.
     * These objects should be order by their ownership constraints to maintain referencial integrity.
     */
    protected Map getPreModifyCommits() {
        if (preModifyCommits == null) {
            // 2612538 - the default size of Map (32) is appropriate
            preModifyCommits = new IdentityHashMap();
        }
        return preModifyCommits;
    }

    /**
     * Return the session that this is managing commits for.
     */
    protected AbstractSession getSession() {
        return session;
    }

    /**
     * Return any objects that have been shallow comitted during this commit process.
     */
    protected Map getShallowCommits() {
        if (shallowCommits == null) {
            // 2612538 - the default size of Map (32) is appropriate
            shallowCommits = new IdentityHashMap();
        }
        return shallowCommits;
    }

    /**
     * Reset the commit order from the session's descriptors.
     * This uses the constraint dependencies in the descriptor's mappings,
     * to decide which descriptors are dependent on which other descriptors.
     * Multiple computations of the commit order should produce the same ordering.
     * This is done to improve performance on unit of work writes through decreasing the
     * stack size, and acts as a deadlock avoidance mechansim.
     */
    public void initializeCommitOrder() {
        Vector descriptors = Helper.buildVectorFromMapElements(getSession().getDescriptors());

        // Must ensure uniqueness, some descriptor my be register twice for interfaces.
        descriptors = Helper.addAllUniqueToVector(new Vector(descriptors.size()), descriptors);
        Object[] descriptorsArray = new Object[descriptors.size()];
        for (int index = 0; index < descriptors.size(); index++) {
            descriptorsArray[index] = descriptors.elementAt(index);
        }
        Arrays.sort(descriptorsArray, new DescriptorCompare());
        descriptors = new Vector(descriptors.size());
        for (int index = 0; index < descriptorsArray.length; index++) {
            descriptors.addElement(descriptorsArray[index]);
        }

        CommitOrderCalculator calculator = new CommitOrderCalculator(getSession());
        calculator.addNodes(descriptors);
        calculator.calculateMappingDependencies();
        calculator.orderCommits();
        descriptors = calculator.getOrderedDescriptors();

        calculator = new CommitOrderCalculator(getSession());
        calculator.addNodes(descriptors);
        calculator.calculateSpecifiedDependencies();
        calculator.orderCommits();

        setCommitOrder(calculator.getOrderedClasses());
    }

    /**
     * Return if the commit manager is active.
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * Return if the object has been commited.
     * This should be called by any query that is writing an object,
     * if true the query should not write the object.
     */
    public boolean isCommitCompleted(Object domainObject) {
        return getCompletedCommits().containsKey(domainObject);
    }

    /**
     * Return if the object is being in progress of being post modify commit.
     * This should be called by any query that is writing an object,
     * if true the query must force a shallow insert of the object if it is new.
     */
    public boolean isCommitInPostModify(Object domainObject) {
        return getPostModifyCommits().containsKey(domainObject);
    }

    /**
     * Return if the object is being in progress of being pre modify commit.
     * This should be called by any query that is writing an object,
     * if true the query must force a shallow insert of the object if it is new.
     */
    public boolean isCommitInPreModify(Object domainObject) {
        return getPreModifyCommits().containsKey(domainObject);
    }

    /**
     * Return if the object is shallow committed.
     * This is required to resolve bidirection references.
     */
    public boolean isShallowCommitted(Object domainObject) {
        return getShallowCommits().containsKey(domainObject);
    }

    /**
     * Mark the commit of the object as being fully completed.
     * This should be called by any query that has finished writing an object.
     */
    public void markCommitCompleted(Object domainObject) {
        getPreModifyCommits().remove(domainObject);
        getPostModifyCommits().remove(domainObject);
        // If not in a unit of work commit and the commit of this object is done reset the comitt manager
        if ((!isActive()) && getPostModifyCommits().isEmpty() && getPreModifyCommits().isEmpty()) {
            reinitialize();
            return;
        }
        getCompletedCommits().put(domainObject, domainObject);// Treat as set.
    }

    /**
     * Add an object as being in progress of being commited.
     * This should be called by any query that is writing an object.
     */
    public void markPostModifyCommitInProgress(Object domainObject) {
        getPreModifyCommits().remove(domainObject);
        getPostModifyCommits().put(domainObject, domainObject);// Use as set.
    }

    /**
     * Add an object as being in progress of being commited.
     * This should be called by any query that is writing an object.
     */
    public void markPreModifyCommitInProgress(Object domainObject) {
        removePendingCommit(domainObject);
        addProcessedCommit(domainObject);
        getPreModifyCommits().put(domainObject, domainObject);// Use as set.
    }

    /**
     * Mark the object as shallow committed.
     * This is required to resolve bidirection references.
     */
    public void markShallowCommit(Object domainObject) {
        getShallowCommits().put(domainObject, domainObject);// Use as set.
    }

    /**
     * Reset the commits.
     * This must be done before a new commit process is begun.
     */
    public void reinitialize() {
        this.pendingCommits = null;
        this.processedCommits = null;
        this.preModifyCommits = null;
        this.postModifyCommits = null;
        this.completedCommits = null;
        this.shallowCommits = null;
        this.objectsToDelete = null;
        this.dataModifications = null;
    }

    /**
     * Remove the commit of the object from pending.
     */
    protected void removePendingCommit(Object domainObject) {
        getPendingCommits().remove(domainObject);
    }

    /**
     * Set the order in which objects should be commited to the database.
     * This order is based on ownership in the descriptors and is require for referencial integrity.
     * The commit order is a vector of vectors,
     * where the first vector is all root level classes, the second is classes owned by roots and so on.
     */
    public void setCommitOrder(Vector commitOrder) {
        this.commitOrder = commitOrder;
    }

    /**
     * Set the objects that have been written during this commit process.
     */
    protected void setCompletedCommits(Map completedCommits) {
        this.completedCommits = completedCommits;
    }

    /**
     * Used to store data querys to be performed at the end of the commit.
     * This is done to decrease dependencies and avoid deadlock.
     */
    protected void setDataModifications(Hashtable dataModifications) {
        this.dataModifications = dataModifications;
    }

    /**
     * Set if the commit manager is active.
     */
    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * Deletion are cached until the end.
     */
    protected void setObjectsToDelete(Vector objectsToDelete) {
        this.objectsToDelete = objectsToDelete;
    }

    /**
     * Set the objects that should be written during this commit process.
     */
    protected void setPendingCommits(Map pendingCommits) {
        this.pendingCommits = pendingCommits;
    }

    /**
     * Set the objects that should be written during this commit process.
     */
    protected void setProcessedCommits(Map processedCommits) {
        this.processedCommits = processedCommits;
    }

    /**
     * Set any objects that should be written during post modify commit process.
     * These objects should be order by their ownership constraints to maintain referencial integrity.
     */
    protected void setPostModifyCommits(Map postModifyCommits) {
        this.postModifyCommits = postModifyCommits;
    }

    /**
     * Set any objects that should be written during pre modify commit process.
     * These objects should be order by their ownership constraints to maintain referencial integrity.
     */
    protected void setPreModifyCommits(Map preModifyCommits) {
        this.preModifyCommits = preModifyCommits;
    }

    /**
     * Set the session that this is managing commits for.
     */
    protected void setSession(AbstractSession session) {
        this.session = session;
    }

    /**
     * Set any objects that have been shallow comitted during this commit process.
     */
    protected void setShallowCommits(Map shallowCommits) {
        this.shallowCommits = shallowCommits;
    }

    /**
     * Print the in progress depth.
     */
    public String toString() {
        int size = 0;
        if (preModifyCommits != null) {
            size += getPreModifyCommits().size();
        }
        if (postModifyCommits != null) {
            size += getPostModifyCommits().size();
        }
        Object[] args = { new Integer(size) };
        return Helper.getShortClassName(getClass()) + ToStringLocalization.buildMessage("commit_depth", args);
    }
}