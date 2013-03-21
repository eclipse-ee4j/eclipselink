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
package org.eclipse.persistence.internal.descriptors;

import java.io.Serializable;

import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.descriptors.ClassDescriptor;

public interface OptimisticLockingPolicy extends Cloneable, Serializable {

    /**
     * INTERNAL:
     * Add update fields for template row.
     * These are any unmapped fields required to write in an update.
     *
     * #see this method in VersionLockingPolicy
     */
    public void addLockFieldsToUpdateRow(AbstractRecord databaseRow, AbstractSession session);

    /**
     * INTERNAL:
     * The method should update the translation row with the
     * correct write lock values. This method is called on a delete.
     *
     * #see this method in VersionLockingPolicy
     */
    public void addLockValuesToTranslationRow(ObjectLevelModifyQuery query);

    /**
     * INTERNAL:
     * When given an expression, this method will return a new expression with
     * the optimistic locking values included.  This expression will be used
     * in a delete call.
     *
     * #see this method in VersionLockingPolicy
     */
    public Expression buildDeleteExpression(org.eclipse.persistence.internal.helper.DatabaseTable table, Expression mainExpression, AbstractRecord row);

    /**
     * INTERNAL:
     * When given an expression, this method will return a new expression with
     * the optimistic locking values included.  This expression will be used in
     * an update call.
     *
     * #see this method in VersionLockingPolicy
     */
    public Expression buildUpdateExpression(org.eclipse.persistence.internal.helper.DatabaseTable table, Expression mainExpression, AbstractRecord translationRow, AbstractRecord modifyRow);

    public Object clone();

    /**
     * INTERNAL:
     * Indicates whether compareWriteLockValues method is supported by the policy.
     * Numeric or timestamp lock values could be compared:
     * for every pair of values v1 and v2 - either v1 lessthan v2; or v1==v2; or v1 greaterthan v2.
     * However it's impossible to compare values for FieldsLockingPolicy for two reasons:
     * 1. there is no "linear order": v1 lessthan v2 and v greaterthan v2 is not defined: either v1==v2 or v1!=v2;
     * 2. locking value is not a single field which is not part of mapped object value
     *    but rather a set of object's mapped fields. That means any object's mapped attribute change
     *    is potentially a change of the locking value.
     *    For ChangedFieldsLockingPolicy every mapped attribute's change is a change of locking value.
     *    The pattern used by versioning: "if the original locking value is unchanged
     *    then the object hasn't been changed outside of the application", which allows
     *    to distinguish between the change made inside and outside the application,
     *    doesn't work for fields locking.
     *    It degenerates into useless pattern: "if the original locking value is unchanged
     *    then the object hasn't been changed".
     *    
     * Use compareWriteLockValues method only if this method returns true.
     */
    public boolean supportsWriteLockValuesComparison();

    /**
     * INTERNAL:
     * This method shouldn't be called if supportsWriteLockValuesComparison() returns false.
     * This method compares two writeLockValues.
     * The writeLockValues should be non-null and of the correct type.
     * Returns:
     * -1 if value1 is less (older) than value2;
     *  0 if value1 equals value2;
     *  1 if value1 is greater (newer) than value2.
     * Throws:
     *  NullPointerException if the passed value is null;
     *  ClassCastException if the passed value is of a wrong type.
     */
    public int compareWriteLockValues(Object value1, Object value2);

    /**
     * INTERNAL:
     * This is the base value that is older than all other values, it is used in the place of
     * null in some situations.
     */
    abstract public Object getBaseValue();

    /**
     * ADVANCED:
     * returns the LockOnChange mode for this policy.  This mode specifies if a 
     * Optimistic Write lock should be enforced on this entity when a set of mappings are changed.
     */
    public LockOnChange getLockOnChangeMode();
    
    /**
     * INTERNAL:
     * Return the value that should be stored in the identity map.
     * If the value is not stored in the cache, then return a null.
     *
     * #see this method in VersionLockingPolicy
     */
    public Object getValueToPutInCache(AbstractRecord row, AbstractSession session);

    /**
     * PUBLIC:
     * Return the number of versions different between these objects.
     */
    public int getVersionDifference(Object currentValue, Object domainObject, Object primaryKey, AbstractSession session);

    /**
     * INTERNAL:
     * Return the write lock field.
    *
    * #see this method in VersionLockingPolicy
     */
    public DatabaseField getWriteLockField();

    /**
     * INTERNAL:
     * This method will return the optimistic lock value for the object
     *
     * #see this method in VersionLockingPolicy
     */
    public Object getWriteLockValue(Object domainObject, Object primaryKey, AbstractSession session);

    /**
     * INTERNAL:
     * This method will return an expression that is used to update its optimistic locking field
     *
     * #see this method in VersionLockingPolicy
     */
    public Expression getWriteLockUpdateExpression(ExpressionBuilder builder, AbstractSession session);

    /**
     * INTERNAL:
     * It is responsible for initializing the policy.
     *
     * #see this method in VersionLockingPolicy
     */
    public void initialize(AbstractSession session);

    /**
     * INTERNAL:
     * Responsible for pre-initializing.
     *
     * #see this method in VersionLockingPolicy
     */
    public void initializeProperties();
    
    /**
     * INTERNAL:
     * Return true if the lock value is stored in the cache.
     */
    public boolean isStoredInCache();
    
    /**
     * INTERNAL:
     * Specify if the policy cascade locks.
     */
    public boolean isCascaded();

    /**
     * INTERNAL:
     * Returns true if the value stored with the domainObject is more recent
     * than the value .  Returns false otherwise.
     *
     * #see this method in VersionLockingPolicy
     */
    public boolean isNewerVersion(Object currentValue, Object domainObject, Object primaryKey, AbstractSession session);

    /**
     * INTERNAL:
     * Returns true if the value stored with the domainObject is more recent
     * than the value in the row.  Returns false otherwise.
     * NOTE: This method will only be called if the shouldOnlyRefreshCacheIfNewerVersion()
     * flag is set on descriptor.
     *
     * #see this method in VersionLockingPolicy
     */
    public boolean isNewerVersion(AbstractRecord databaseRow, Object domainObject, Object primaryKey, AbstractSession session);

    /**
     * INTERNAL:
     * This method should merge changes from the parent into the child.
     *
     * #see this method in VersionLockingPolicy
     */
    public void mergeIntoParentCache(UnitOfWorkImpl uow, Object primaryKey, Object object);

    /**
     * INTERNAL:
     * This method should merge changes from the parent into the child.
     *
     * #see this method in VersionLockingPolicy
     */
    public void mergeIntoParentCache(CacheKey unitOfWorkCacheKey, CacheKey parentSessionCacheKey);

    /**
     * INTERNAL:
     * provide a way to set the descriptor for this policy
     */
    public void setDescriptor(ClassDescriptor descriptor);
    
    /**
     * ADVANCED:
     * Sets the LockOnChange mode for this policy.  This mode specifies if a 
     * Optimistic Write lock should be enforced on this entity when set of mappings are changed.
     */
    public void setLockOnChangeMode(LockOnChange lockOnChangeMode);

    /**
     * INTERNAL:
     * Add the initial right lock values to the modify
     * row in the query. This method will only be called
     * on insert.
     *
     * #see this method in VersionLockingPolicy
     */
    public void setupWriteFieldsForInsert(ObjectLevelModifyQuery query);

    /**
     * INTERNAL:
     * This method should update the translation row, the modify
     * row and the domain object with th lock value.
     *
     * #see this method in VersionLockingPolicy
     */
    public void updateRowAndObjectForUpdate(ObjectLevelModifyQuery query, Object object);
    
    /**
     * INTERNAL:
     * Returns true if the policy has been set to set an optimistic read lock when a owning mapping changes.
     */
    public boolean shouldUpdateVersionOnOwnedMappingChange();

    /**
     * INTERNAL:
     * Returns true if the policy has been set to set an optimistic read lock when any mapping changes.
     */
    public boolean shouldUpdateVersionOnMappingChange();

    public void validateDelete(int rowCount, Object object, DeleteObjectQuery query);

    public void validateUpdate(int rowCount, Object object, WriteObjectQuery query);
    
    /**
     * Advanced:
     * <p>
     * <b>Purpose</b>: Provides the configuration options to force an Optimistic Write Lock
     * When a set of mappings have changes.  This enum defines what mappings are within that set.
     * @author Gordon Yorke
     * @since EclipseLink 2.0
     */
    public enum LockOnChange{
        OWNING, // update version when an owning mapping changes
        NONE,
        ALL;
    }

}
