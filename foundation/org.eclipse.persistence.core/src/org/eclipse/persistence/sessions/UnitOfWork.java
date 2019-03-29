/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.sessions;

import java.util.Collection;
import java.util.Set;
import java.util.Vector;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.OptimisticLockException;
import org.eclipse.persistence.sessions.changesets.UnitOfWorkChangeSet;

/**
 * <b>Purpose</b>: To allow object level transactions.  This class represents the public API of the
 * unit of work and should be used to maintain complete compatibility.
 * <p>
 * <b>Description</b>: The unit of work is a session that implements all of the normal
 * protocol of an EclipseLink session. It can be spawned from any other session including another unit of work.
 * Objects can be brought into the unit of work through reading them or through registering them.
 * The unit of work will operate on its own object space, that is the objects within the unit of work
 * will be clones of the original objects.  When the unit of work is committed, all changes to any objects
 * registered within the unit of work will be committed to the database.  A minimal commit/update will
 * be performed and any foreign keys/circular reference/referential integrity will be resolved.
 * If the commit to the database is successful the changed objects will be merged back into the unit of work
 * parent session.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Allow parallel transactions against a session's objects.
 * <li> Allow nested transactions.
 * <li> Not require the application to write objects that is changes, automatically determine what has changed.
 * <li> Perform a minimal commit/update of all changes that occurred.
 * <li> Resolve foreign keys for newly created objects and maintain referential integrity.
 * <li> Allow for the object transaction to use its own object space.
 * </ul>
 */
public interface UnitOfWork extends Session {
    /**
     * Defines the ordering of updates and deletes of a set of the same entity type during a commit or flush operation.
     * The commit order of entities is defined by their foreign key constraints, and then sorted alphabetically.
     * <p>
     * By default the commit of a set of the same entity type is ordered by primary key.
     */
    public enum CommitOrderType {
        /** Updates and deletes are ordered by the object's id.  This can help avoid deadlocks on highly concurrent systems. */
        ID,
        /** Updates are ordered by the object's changes, then by id.  This can improve batch writing efficiency. */
        CHANGES,
        /** No ordering is done. */
        NONE
    }

    /**
     * ADVANCED:
     * Returns the set of read-only classes in this UnitOfWork.
     */
    Set getReadOnlyClasses();

    /**
     * PUBLIC:
     * Adds the given Java class to the receiver's set of read-only classes.
     * Cannot be called after objects have been registered in the unit of work.
     */
    void addReadOnlyClass(Class theClass);

    /**
     * PUBLIC:
     * Adds the classes in the given Vector to the existing set of read-only classes.
     * Cannot be called after objects have been registered in the unit of work.
     */
    void addReadOnlyClasses(Collection classes);

    /**
     * ADVANCED:
     * Assign sequence number to the object.
     * This allows for an object's id to be assigned before commit.
     * It can be used if the application requires to use the object id before the object exists on the database.
     * Normally all ids are assigned during the commit automatically.
     */
    void assignSequenceNumber(Object object) throws DatabaseException;

    /**
     * ADVANCED:
     * Assign sequence numbers to all new objects registered in this unit of work,
     * or any new objects reference by any objects registered.
     * This allows for an object's id to be assigned before commit.
     * It can be used if the application requires to use the object id before the object exists on the database.
     * Normally all ids are assigned during the commit automatically.
     */
    void assignSequenceNumbers() throws DatabaseException;

    /**
     * PUBLIC:
     * Tell the unit of work to begin a transaction now.
     * By default the unit of work will begin a transaction at commit time.
     * The default is the recommended approach, however sometimes it is
     * necessary to start the transaction before commit time.  When the
     * unit of work commits, this transaction will be committed.
     *
     * @see #commit()
     * @see #release()
     */
    void beginEarlyTransaction() throws DatabaseException;

    /**
     * PUBLIC:
     * Commit the unit of work to its parent.
     * For a nested unit of work this will merge any changes to its objects
     * with its parents.
     * For a first level unit of work it will commit all changes to its objects
     * to the database as a single transaction.  If successful the changes to its
     * objects will be merged to its parent's objects.  If the commit fails the database
     * transaction will be rolledback, and the unit of work will be released.
     * If the commit is successful the unit of work is released, and a new unit of work
     * must be acquired if further changes are desired.
     *
     * @see #commitAndResumeOnFailure()
     * @see #commitAndResume()
     * @see #release()
     */
    void commit() throws DatabaseException, OptimisticLockException;

    /**
     * PUBLIC:
     * Commit the unit of work to its parent.
     * For a nested unit of work this will merge any changes to its objects
     * with its parents.
     * For a first level unit of work it will commit all changes to its objects
     * to the database as a single transaction.  If successful the changes to its
     * objects will be merged to its parent's objects.  If the commit fails the database
     * transaction will be rolledback, and the unit of work will be released.
     * The normal commit releases the unit of work, forcing a new one to be acquired if further changes are desired.
     * The resuming feature allows for the same unit of work (and working copies) to be continued to be used.
     *
     * @see #commitAndResumeOnFailure()
     * @see #commit()
     * @see #release()
     */
    void commitAndResume() throws DatabaseException, OptimisticLockException;

    /**
     * PUBLIC:
     * Commit the unit of work to its parent.
     * For a nested unit of work this will merge any changes to its objects
     * with its parents.
     * For a first level unit of work it will commit all changes to its objects
     * to the database as a single transaction.  If successful the changes to its
     * objects will be merged to its parent's objects.  If the commit fails the database
     * transaction will be rolledback, but the unit of work will remain active.
     * It can then be retried or released.
     * The normal commit failure releases the unit of work, forcing a new one to be acquired if further changes are desired.
     * The resuming feature allows for the same unit of work (and working copies) to be continued to be used if an error occurs.
     * The UnitOfWork will also remain active if the commit is successful.
     *
     * @see #commit()
     * @see #release()
     */
    void commitAndResumeOnFailure() throws DatabaseException, OptimisticLockException;

    /**
     * PUBLIC:
     * Merge the attributes of the clone into the unit of work copy.
     * This can be used for objects that are returned from the client through
     * RMI serialization or other serialization mechanisms, because the RMI object will
     * be a clone this will merge its attributes correctly to preserve object identity
     * within the unit of work and record its changes.
     * Everything connected to this object (i.e. the entire object tree where rmiClone
     * is the root) is also merged.
     *
     * @return the registered version for the clone being merged.
     * @see #mergeClone(Object)
     * @see #shallowMergeClone(Object)
     */
    Object deepMergeClone(Object rmiClone);

    /**
     * PUBLIC:
     * Revert the object's attributes from the parent.
     * This reverts everything the object references.
     *
     * @return the object reverted.
     * @see #revertObject(Object)
     * @see #shallowRevertObject(Object)
     */
    Object deepRevertObject(Object clone);

    /**
     * ADVANCED:
     * Unregister the object with the unit of work.
     * This can be used to delete an object that was just created and is not yet persistent.
     * Delete object can also be used, but will result in inserting the object and then deleting it.
     * The method should be used carefully because it will delete all the reachable parts.
     */
    void deepUnregisterObject(Object clone);

    /**
     * PUBLIC:
     * Delete all of the objects and all of their privately owned parts in the database.
     * Delete operations are delayed in a unit of work until commit.
     */
    void deleteAllObjects(Collection domainObjects);

    /**
     * PUBLIC:
     * Delete the object and all of their privately owned parts in the database.
     * Delete operations are delayed in a unit of work until commit.
     */
    Object deleteObject(Object domainObject);

    /**
     * ADVANCED:
     * The unit of work performs validations such as,
     * ensuring multiple copies of the same object don't exist in the same unit of work,
     * ensuring deleted objects are not referred after commit,
     * ensures that objects from the parent cache are not referred in the unit of work cache.
     * The level of validation can be increased or decreased for debugging purposes or under
     * advanced situation where the application requires/desires to violate clone identity in the unit of work.
     * It is strongly suggested that clone identity not be violate in the unit of work.
     */
    void dontPerformValidation();

    /**
     * ADVANCED:
     * Set optimistic read lock on the object.  This feature is override by normal optimistic lock.
     * when the object is changed in UnitOfWork. The cloneFromUOW must be the clone of from this
     * UnitOfWork and it must implements version locking or timestamp locking.
     * The SQL would look like the followings.
     *
     * If shouldModifyVersionField is true,
     * "UPDATE EMPLOYEE SET VERSION = 2 WHERE EMP_ID = 9 AND VERSION = 1"
     *
     * If shouldModifyVersionField is false,
     * "UPDATE EMPLOYEE SET VERSION = 1 WHERE EMP_ID = 9 AND VERSION = 1"
     */
    void forceUpdateToVersionField(Object cloneFromUOW, boolean shouldModifyVersionField);

    /**
     * ADVANCED:
     * This method will calculate the changes for the UnitOfWork.  Without assigning sequence numbers.
     * This is a computationaly intensive operation and should be avoided unless necessary.
     * A valid changeSet, with sequence numbers can be collected from the UnitOfWork after the commit
     * is complete by calling unitOfWork.getUnitOfWorkChangeSet().
     */
    UnitOfWorkChangeSet getCurrentChanges();

    /**
     * ADVANCED:
     * Return the original version of the object(clone) from the parent's identity map.
     */
    Object getOriginalVersionOfObject(Object workingClone);

    /**
     * PUBLIC:
     * Return the parent.
     * This is a unit of work if nested, otherwise a database session or client session.
     */
    org.eclipse.persistence.internal.sessions.AbstractSession getParent();

    /**
     * ADVANCED:
     * Returns the currentChangeSet from the UnitOfWork.
     * This is only valid after the UnitOfWork has committed successfully
     */
    org.eclipse.persistence.sessions.changesets.UnitOfWorkChangeSet getUnitOfWorkChangeSet();

    /**
     * ADVANCED:
     * The unit of work performs validations such as,
     * ensuring multiple copies of the same object don't exist in the same unit of work,
     * ensuring deleted objects are not referred after commit,
     * ensures that objects from the parent cache are not referred in the unit of work cache.
     * The level of validation can be increased or decreased for debugging purposes or under
     * advanced situation where the application requires/desires to violate clone identity in the unit of work.
     * It is strongly suggested that clone identity not be violate in the unit of work.
     */
    int getValidationLevel();

    /**
     * ADVANCED:
     * The Unit of work is capable of preprocessing to determine if any on the clone have been changed.
     * This is computationally expensive and should be avoided on large object graphs.
      */
    boolean hasChanges();

    /**
     * PUBLIC:
     * Return if the unit of work is active (has not been released).
     */
    boolean isActive();

    /**
     * PUBLIC:
     * Checks to see if the specified class is read-only or not in this UnitOfWork.
     *
     * @return true if the class is read-only, false otherwise.
     */
    boolean isClassReadOnly(Class theClass);

    /**
     * ADVANCED:
     * Return whether the clone object is already registered.
     */
    boolean isObjectRegistered(Object clone);

    /**
     * ADVANCED:
     * Return if this session is a nested unit of work.
     */
    boolean isNestedUnitOfWork();

    /**
     * PUBLIC:
     * Merge the attributes of the clone into the unit of work copy.
     * This can be used for objects that are returned from the client through
     * RMI serialization (or another serialization mechanism), because the RMI object
     * will be a clone this will merge its attributes correctly to preserve object
     * identity within the unit of work and record its changes.
     *
     * The object and its private owned parts are merged.
     *
     * @return the registered version for the clone being merged.
     * @see #shallowMergeClone(Object)
     * @see #deepMergeClone(Object)
     */
    Object mergeClone(Object rmiClone);

    /**
     * PUBLIC:
     * Merge the attributes of the clone into the unit of work copy.
     * This can be used for objects that are returned from the client through
     * RMI serialization (or another serialization mechanism), because the RMI object
     * will be a clone this will merge its attributes correctly to preserve object
     * identity within the unit of work and record its changes.
     *
     * The object and its private owned parts are merged. This will include
     * references from this clone to independent objects.
     *
     * @return the registered version for the clone being merged.
     * @see #shallowMergeClone(Object)
     * @see #deepMergeClone(Object)
     */
    Object mergeCloneWithReferences(Object rmiClone);

    /**
     * PUBLIC:
     * Return a new instance of the class registered in this unit of work.
     * This can be used to ensure that new objects are registered correctly.
     */
    Object newInstance(Class theClass);

    /**
     * ADVANCED:
     * The unit of work performs validations such as,
     * ensuring multiple copies of the same object don't exist in the same unit of work,
     * ensuring deleted objects are not referred after commit,
     * ensures that objects from the parent cache are not referred in the unit of work cache.
     * The level of validation can be increased or decreased for debugging purposes or under
     * advanced situation where the application requires/desires to violate clone identity in the unit of work.
     * It is strongly suggested that clone identity not be violate in the unit of work.
     */
    void performFullValidation();

    /**
     * ADVANCED:
     * The unit of work performs validations such as,
     * ensuring multiple copies of the same object don't exist in the same unit of work,
     * ensuring deleted objects are not referred after commit,
     * ensures that objects from the parent cache are not referred in the unit of work cache.
     * The level of validation can be increased or decreased for debugging purposes or under
     * advanced situation where the application requires/desires to violate clone identity in the unit of work.
     * It is strongly suggested that clone identity not be violate in the unit of work.
     */
    void performPartialValidation();

    /**
     * PUBLIC:
     * Print the objects in the unit of work.
     */
    void printRegisteredObjects();

    /**
     * PUBLIC:
     * Refresh the attributes of the object and of all of its private parts from the database.
     * The object will be pessimistically locked on the database for the duration of the transaction.
     * If the object is already locked this method will wait until the lock is released.
     * A no wait option is available through setting the lock mode.
     * @see #refreshAndLockObject(Object, short)
     */
    Object refreshAndLockObject(Object object);

    /**
     * PUBLIC:
     * Refresh the attributes of the object and of all of its private parts from the database.
     * The object will be pessimistically locked on the database for the duration of the transaction.
     * <p>Lock Modes: ObjectBuildingQuery.NO_LOCK, LOCK, LOCK_NOWAIT
     */
    Object refreshAndLockObject(Object object, short lockMode);

    /**
     * PUBLIC:
     * Register the objects with the unit of work.
     * All newly created root domain objects must be registered to be inserted on commit.
     * Also any existing objects that will be edited and were not read from this unit of work
     * must also be registered.
     * Once registered any changes to the objects will be committed to the database on commit.
     *
     * @return is the clones of the original objects, the return value must be used for editing,
     * editing the original is not allowed in the unit of work.
     */
    Vector registerAllObjects(Collection domainObjects);

    /**
     * ADVANCED:
     * Register the existing object with the unit of work.
     * This is a advanced API that can be used if the application can guaranteed the object exists on the database.
     * When registerObject is called the unit of work determines existence through the descriptor's doesExist setting.
     *
     * @return The clone of the original object, the return value must be used for editing.
     * Editing the original is not allowed in the unit of work.
     */
    Object registerExistingObject(Object existingObject);

    /**
     * ADVANCED:
     * Register the new object with the unit of work.
     * This will register the new object without cloning.
     * Normally the registerObject method should be used for all registration of new and existing objects.
     * This version of the register method can only be used for new objects.
     * This method should only be used if a new object is desired to be registered without cloning.
     *
     * @see #registerObject(Object)
     */
    Object registerNewObject(Object newObject);

    /**
     * PUBLIC:
     * Register the object with the unit of work.
     * All newly created root domain objects must be registered to be inserted on commit.
     * Also any existing objects that will be edited and were not read from this unit of work
     * must also be registered.
     * Once registered any changes to the objects will be committed to the database on commit.
     *
     * @return the clone of the original object, the return value must be used for editing,
     *
     * ** Editing the original is not allowed in the unit of work. **
     */
    Object registerObject(Object domainObject);

    /**
     * PUBLIC:
     * Release the unit of work.
     * This terminates this unit of work.
     * Because the unit of work operates on its own object space (clones) no work is required.
     * The unit of work should no longer be used or referenced by the application beyond this point
     * so that it can garbage collect.
     *
     * @see #commit()
     */
    @Override void release();

    /**
     * PUBLIC:
     * Empties the set of read-only classes.
     * It is illegal to call this method on nested UnitOfWork objects. A nested UnitOfWork
     * cannot have a subset of its parent's set of read-only classes.
     */
    void removeAllReadOnlyClasses();

    /**
     * ADVANCED:
     * Remove optimistic read lock from the object
     * See forceUpdateToVersionField(Object)
     */
    void removeForceUpdateToVersionField(Object cloneFromUOW);

    /**
     * PUBLIC:
     * Removes a Class from the receiver's set of read-only classes.
     * It is illegal to try to send this method to a nested UnitOfWork.
     */
    void removeReadOnlyClass(Class theClass);

    /**
     * PUBLIC:
     * Revert all changes made to any registered object.
     * Clear all deleted and new objects.
     * Revert should not be confused with release which it the normal compliment to commit.
     * Revert is more similar to commit and resume, however reverts all changes and resumes.
     * If you do not require to resume the unit of work release should be used instead.
     *
     * @see #commitAndResume()
     * @see #release()
     */
    void revertAndResume();

    /**
     * PUBLIC:
     * Revert the object's attributes from the parent.
     * This also reverts the object privately-owned parts.
     *
     * @return the object reverted.
     * @see #shallowRevertObject(Object)
     * @see #deepRevertObject(Object)
     */
    Object revertObject(Object clone);

    /**
     * ADVANCED:
     * By default new objects are not cached until the exist on the database.
     * Occasionally if mergeClone is used on new objects and is required to allow multiple merges
     * on the same new object, then if the new objects are not cached, each mergeClone will be
     * interpreted as a different new object.
     * By setting new objects to be cached mergeClone can be performed multiple times before commit.
     * New objects cannot be cached unless they have a valid assigned primary key before being registered.
     * New object with non-null invalid primary keys such as 0 or '' can cause problems and should not be used with this option.
     */
    void setShouldNewObjectsBeCached(boolean shouldNewObjectsBeCached);

    /**
     * ADVANCED:
     * By default deletes are performed last in a unit of work.
     * Sometimes you may want to have the deletes performed before other actions.
     */
    void setShouldPerformDeletesFirst(boolean shouldPerformDeletesFirst);

    /**
     * ADVANCED:
     * Conforming queries can be set to provide different levels of detail about the
     * exceptions they encounter
     * There are two levels:<br>
     *    DO_NOT_THROW_CONFORM_EXCEPTIONS = 0;<br>
     *    THROW_ALL_CONFORM_EXCEPTIONS = 1;
     */
    void setShouldThrowConformExceptions(int shouldThrowExceptions);

    /**
     * ADVANCED:
     * The unit of work performs validations such as,
     * ensuring multiple copies of the same object don't exist in the same unit of work,
     * ensuring deleted objects are not referred after commit,
     * ensures that objects from the parent cache are not referred in the unit of work cache.
     * The level of validation can be increased or decreased for debugging purposes or under
     * advanced situation where the application requires/desires to violate clone identity in the unit of work.
     * It is strongly suggested that clone identity not be violate in the unit of work.
     */
    void setValidationLevel(int validationLevel);

    /**
     * PUBLIC:
     * Merge the attributes of the clone into the unit of work copy.
     * This can be used for objects that are returned from the client through
     * RMI serialization (or other serialization mechanisms), because the RMI object will
     * be a clone this will merge its attributes correctly to preserve object identity
     * within the unit of work and record its changes.
     *
     * Only direct attributes are merged.
     *
     * @return the registered version for the clone being merged.
     * @see #mergeClone(Object)
     * @see #deepMergeClone(Object)
     */
    Object shallowMergeClone(Object rmiClone);

    /**
     * PUBLIC:
     * Revert the object's attributes from the parent.
     * This only reverts the object's direct attributes.
     *
     * @return the object reverted.
     * @see #revertObject(Object)
     * @see #deepRevertObject(Object)
     */
    Object shallowRevertObject(Object clone);

    /**
     * ADVANCED:
     * Unregister the object with the unit of work.
     * This can be used to delete an object that was just created and is not yet persistent.
     * Delete object can also be used, but will result in inserting the object and then deleting it.
     * The method will only unregister the clone, none of its parts.
     */
    void shallowUnregisterObject(Object clone);

    /**
     * ADVANCED:
     * By default new objects are not cached until the exist on the database.
     * Occasionally if mergeClone is used on new objects and is required to allow multiple merges
     * on the same new object, then if the new objects are not cached, each mergeClone will be
     * interpreted as a different new object.
     * By setting new objects to be cached mergeClone can be performed multiple times before commit.
     * New objects cannot be cached unless they have a valid assigned primary key before being registered.
     * New object with non-null invalid primary keys such as 0 or '' can cause problems and should not be used with this option.
     */
    boolean shouldNewObjectsBeCached();

    /**
     * ADVANCED:
     * By default all objects are inserted and updated in the database before
     * any object is deleted. If this flag is set to true, deletes will be
     * performed before inserts and updates
     */
    boolean shouldPerformDeletesFirst();

    /**
     * ADVANCED:
     * The unit of work performs validations such as,
     * ensuring multiple copies of the same object don't exist in the same unit of work,
     * ensuring deleted objects are not referred after commit,
     * ensures that objects from the parent cache are not referred in the unit of work cache.
     * The level of validation can be increased or decreased for debugging purposes or under
     * advanced situation where the application requires/desires to violate clone identity in the unit of work.
     * It is strongly suggested that clone identity not be violate in the unit of work.
     */
    boolean shouldPerformFullValidation();

    /**
     * ADVANCED:
     * The unit of work performs validations such as,
     * ensuring multiple copies of the same object don't exist in the same unit of work,
     * ensuring deleted objects are not referred after commit,
     * ensures that objects from the parent cache are not referred in the unit of work cache.
     * The level of validation can be increased or decreased for debugging purposes or under
     * advanced situation where the application requires/desires to violate clone identity in the unit of work.
     * It is strongly suggested that clone identity not be violate in the unit of work.
     */
    boolean shouldPerformNoValidation();

    /**
     * ADVANCED:
     * The unit of work performs validations such as,
     * ensuring multiple copies of the same object don't exist in the same unit of work,
     * ensuring deleted objects are not referred after commit,
     * ensures that objects from the parent cache are not referred in the unit of work cache.
     * The level of validation can be increased or decreased for debugging purposes or under
     * advanced situation where the application requires/desires to violate clone identity in the unit of work.
     * It is strongly suggested that clone identity not be violate in the unit of work.
     */
    boolean shouldPerformPartialValidation();

    /**
     * ADVANCED:
     * Unregister the object with the unit of work.
     * This can be used to delete an object that was just created and is not yet persistent.
     * Delete object can also be used, but will result in inserting the object and then deleting it.
     * The method will only unregister private owned parts
     */
    void unregisterObject(Object clone);

    /**
     * ADVANCED:
     * This can be used to help debugging an object-space corruption.
     * An object-space corruption is when your application has incorrectly related a clone to an original object.
     * This method will validate that all registered objects are in a correct state and throw
     * an error if not,  it will contain the full stack of object references in the error message.
     * If you call this method after each register or change you perform it will pin-point where the error was made.
     */
    void validateObjectSpace();

    /**
     * ADVANCED: Writes all changes now before commit().
     * The commit process will begin and all changes will be written out to the datastore, but the datastore transaction will not
     * be committed, nor will changes be merged into the global cache.
     * <p>
     * A subsequent commit (on UnitOfWork or global transaction) will be required to finalize the commit process.
     * <p>
     * As the commit process has begun any attempt to register objects, or execute object-level queries will
     * generate an exception.  Report queries, non-caching queries, and data read/modify queries are allowed.
     * <p>
     * On exception any global transaction will be rolled marked or marked rollback only.  No recovery of this UnitOfWork will be possible.
     * <p>
     * Can only be called once.  It can not be used to write out changes in an incremental fashion.
     * <p>
     * Use to partially commit a transaction outside of a JTA transaction's callbacks.  Allows you to get back any exception directly.
     * <p>
     * Use to commit a UnitOfWork in two stages.
     */
    void writeChanges();

    /**
     * Get an instance, whose state may be lazily fetched.
     * If the requested instance does not exist in the database, null is returned, or the object will fail when accessed.
     * The instance will be lazy when it does not exist in the cache, and supports fetch groups.
     * @param primaryKey - The primary key of the object, either as a List, singleton, IdClass or an instance of the object.
     */
    Object getReference(Class theClass, Object primaryKey);

    /**
     * ADVANCED:
     * Return the commit order.
     */
    CommitOrderType getCommitOrder();

    /**
     * ADVANCED:
     * Set the commit order.
     */
    void setCommitOrder(CommitOrderType order);
}
