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
package org.eclipse.persistence.descriptors;

import java.io.*;
import java.util.*;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.descriptors.ObjectBuilder;
import org.eclipse.persistence.internal.descriptors.OptimisticLockingPolicy;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.DirectToFieldChangeRecord;
import org.eclipse.persistence.internal.sessions.ObjectChangeSet;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.mappings.foundation.AbstractDirectMapping;


/**
 * <p><b>Purpose</b>: Used to allow a single version number to be used for optimistic locking.
 *
 * @since TOPLink/Java 2.0
 */
public class VersionLockingPolicy implements OptimisticLockingPolicy, Serializable {
    protected DatabaseField writeLockField;
    protected boolean isCascaded;
    protected int lockValueStored;
    protected ClassDescriptor descriptor;
    protected transient Expression cachedExpression;
    public final static int IN_CACHE = 1;
    public final static int IN_OBJECT = 2;
    
    /** PERF: Cache the lock mapping if mapped with a direct mapping. */
    protected AbstractDirectMapping lockMapping;

    protected LockOnChange lockOnChangeMode;

    /**
     * PUBLIC:
     * Create a new VersionLockingPolicy.  Defaults to
     * storing the lock value in the cache.
     */
    public VersionLockingPolicy() {
        super();
        storeInCache();
    }

    /**
     * PUBLIC:
     * Create a new VersionLockingPolicy.  Defaults to
     * storing the lock value in the cache.
     * @param fieldName specifies the field name for the write
     * lock field.
     */
    public VersionLockingPolicy(String fieldName) {
        this(new DatabaseField(fieldName));
    }

    /**
     * PUBLIC:
     * Create a new VersionLockingPolicy.  Defaults to
     * storing the lock value in the cache.
     * @param field the write lock field.
     */
    public VersionLockingPolicy(DatabaseField field) {
        this();
        setWriteLockField(field);
    }

    /**
     * INTERNAL:
     * Add update fields for template row.
     * These are any unmapped fields required to write in an update.
     */
    public void addLockFieldsToUpdateRow(AbstractRecord databaseRow, AbstractSession session) {
        if (isStoredInCache()) {
            databaseRow.put(getWriteLockField(), null);
        }
    }

    /**
     * INTERNAL:
     * This method adds the lock value to the translation row of the
     * passed in query. depending on the storage flag, the value is
     * either retrieved from the cache of the object.
     */
    public void addLockValuesToTranslationRow(ObjectLevelModifyQuery query) {
        Object value;
        if (isStoredInCache()) {
            value = query.getSession().getIdentityMapAccessorInstance().getWriteLockValue(query.getPrimaryKey(), query.getObject().getClass(), getDescriptor());
        } else {
            value = lockValueFromObject(query.getObject());
        }
        if (value == null) {
            if (query.isDeleteObjectQuery()) {
                throw OptimisticLockException.noVersionNumberWhenDeleting(query.getObject(), query);
            } else {
                throw OptimisticLockException.noVersionNumberWhenUpdating(query.getObject(), query);
            }
        }
        // EL bug 319759
        if (query.isUpdateObjectQuery()) {
            query.setShouldValidateUpdateCallCacheUse(true);
        }
        query.getTranslationRow().put(this.writeLockField, value);
    }

    /**
     * INTERNAL:
     * When given an expression, this method will return a new expression with
     * the optimistic locking values included.  The values are taken from the
     * passed in database row.  This expression will be used in a delete call.
     */
    public Expression buildDeleteExpression(DatabaseTable table, Expression mainExpression, AbstractRecord row) {
        //use the same expression as update
        return buildUpdateExpression(table, mainExpression, row, null);
    }

    /**
     * INTERNAL:
     * Returns an expression that will be used for both the update and
     * delete where clause
     */
    protected Expression buildExpression() {
        ExpressionBuilder builder = new ExpressionBuilder();

        return builder.getField(getWriteLockField()).equal(builder.getParameter(getWriteLockField()));
    }

    /**
     * INTERNAL:
     * When given an expression, this method will return a new expression
     * with the optimistic locking values included.  The values are taken
     * from the passed in database row.  This expression will be used in
     * an update call.
     */
    public Expression buildUpdateExpression(DatabaseTable table, Expression mainExpression, AbstractRecord row, AbstractRecord row2) {
        if (cachedExpression == null) {
            cachedExpression = buildExpression();
        }
        if (getWriteLockField().getTableName().equals(table.getName())) {
            return mainExpression.and(cachedExpression);
        }
        return mainExpression;
    }

    /**
     * INTERNAL:
     * Clone the policy
     */
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    /**
     * INTERNAL:
     * Indicates that compareWriteLockValues method is supported by the policy.
     */
    public boolean supportsWriteLockValuesComparison() {
        return true;
    }

    /**
     * INTERNAL:
     * This method compares two writeLockValues.
     * The writeLockValues should be non-null and of type Number.
     * Returns:
     * -1 if value1 is less (older) than value2;
     *  0 if value1 equals value2;
     *  1 if value1 is greater (newer) than value2.
     * Throws:
     *  NullPointerException if the passed value is null;
     *  ClassCastException if the passed value is of a wrong type.
     */
    public int compareWriteLockValues(Object value1, Object value2) {
        long longValue1 = ((Number)value1).longValue();
        long longValue2 = ((Number)value2).longValue();
        if ( longValue1 < longValue2 ) return -1;
        if ( longValue1 == longValue2 ) return 0;
        return 1;
    }

    /**
     * INTERNAL:

     * Return the default version locking filed java type, default is BigDecimal

     */
    protected Class getDefaultLockingFieldType() {
        return ClassConstants.LONG;

    }

    /**
     * INTERNAL:
     * This is the base value that is older than all other values, it is used in the place of
     * null in some situations.
     */
    public Object getBaseValue() {
        return Long.valueOf(0);
    }

    /**
     * INTERNAL:
     */
    protected ClassDescriptor getDescriptor() {
        return descriptor;
    }

    /**
     * INTERNAL:
     * returns the initial locking value
     */
    protected Object getInitialWriteValue(AbstractSession session) {
        return Long.valueOf(1);
    }

    /**
     * ADVANCED:
     * returns the LockOnChange mode for this policy.  This mode specifies if a 
     * Optimistic Write lock should be enforced on this entity when a set of mappings are changed.
     * Unfortunately this locking policy can not enforce an optimistic write lock unless a FK or DTF field
     * has changed so this type returns LockOnChange.NONE
     */
    public LockOnChange getLockOnChangeMode(){
        return this.lockOnChangeMode;
    }

    /**
     * INTERNAL:
     * This method gets the write lock value from either the cache or
     * the object stored in the query.  It then returns the new incremented value.
     */
    public Object getNewLockValue(ModifyQuery query) {
        Class objectClass = query.getDescriptor().getJavaClass();
        Number value;
        Number newWriteLockValue = null;
        if (isStoredInCache()) {
            value = (Number)query.getSession().getIdentityMapAccessorInstance().getWriteLockValue(((WriteObjectQuery)query).getPrimaryKey(), objectClass, getDescriptor());
        } else {
            value = (Number)lockValueFromObject(((ObjectLevelModifyQuery)query).getObject());
        }
        if (value == null) {
            throw OptimisticLockException.noVersionNumberWhenUpdating(((ObjectLevelModifyQuery)query).getObject(), (ObjectLevelModifyQuery)query);
        }

        // Increment the value, this goes to the database
        newWriteLockValue = incrementWriteLockValue(value);
        return newWriteLockValue;
    }

    /**
     * INTERNAL:
     * This method returns any of the fields that are not mapped in
     * the object.  In the case of the value being stored in the
     * cache, a vector with one value is returned.  In the case
     * of being stored in the object, an empty vector is returned.
     */
    protected Vector getUnmappedFields() {
        Vector fields = new Vector(1);
        if (isStoredInCache()) {
            fields.addElement(getWriteLockField());
        }
        return fields;
    }

    /**
     * INTERNAL:
     * Return the value that should be stored in the identity map.
     * If the value is stored in the object, then return a null.
     */
    public Object getValueToPutInCache(AbstractRecord row, AbstractSession session) {
        if (isStoredInCache()) {
            return row.get(getWriteLockField());
        } else {
            return null;
        }
    }

    /**
     * PUBLIC:
     * Return the number of versions different between these objects.
     * @param currentValue the new lock value
     * @param domainObject the object containing the version to be compared to
     * @param primaryKeys a vector containing the primary keys of the domainObject
     * @param session the session to be used with the comparison
     */
    public int getVersionDifference(Object currentValue, Object domainObject, Object primaryKeys, AbstractSession session) {
        Number writeLockFieldValue;
        Number newWriteLockFieldValue = (Number)currentValue;

        // If null, was an insert, use 0.
        if (newWriteLockFieldValue == null) {
            newWriteLockFieldValue = Long.valueOf(0);
        }

        if (isStoredInCache()) {
            writeLockFieldValue = (Number)session.getIdentityMapAccessorInstance().getWriteLockValue(primaryKeys, domainObject.getClass(), getDescriptor());
        } else {
            writeLockFieldValue = (Number)lockValueFromObject(domainObject);
        }
        if (writeLockFieldValue == null){
            writeLockFieldValue = Long.valueOf(0);
        }
        return (int)(newWriteLockFieldValue.longValue() - writeLockFieldValue.longValue());
    }

    /**
     * INTERNAL:
     * Return the write lock field.
     */
    public DatabaseField getWriteLockField() {
        return writeLockField;
    }

    /**
     * PUBLIC:
     * Return the field name of the field that stores the write lock value.
     */
    public String getWriteLockFieldName() {
        return getWriteLockField().getQualifiedName();
    }

    /**
     * INTERNAL:
     * Retrun an expression that updates the write lock
     */
    public Expression getWriteLockUpdateExpression(ExpressionBuilder builder, AbstractSession session) {
        return ExpressionMath.add(builder.getField(writeLockField.getName()), 1);
    }

    /**
     * INTERNAL:
     * This method will return the optimistic lock value for the object
     */
    public Object getWriteLockValue(Object domainObject, Object primaryKey, AbstractSession session) {
        Number writeLockFieldValue;
        if (isStoredInCache()) {
            if (primaryKey == null) {
                return null;
            }
            writeLockFieldValue = (Number)session.getIdentityMapAccessorInstance().getWriteLockValue(primaryKey, domainObject.getClass(), getDescriptor());
        } else {
            writeLockFieldValue = (Number)lockValueFromObject(domainObject);
        }
        return writeLockFieldValue;
    }

    /**
     * INTERNAL:
     * Adds 1 to the value passed in.
     */
    protected Number incrementWriteLockValue(Number numberValue) {
        return Long.valueOf(numberValue.longValue() + 1);
    }

    /**
     * INTERNAL:
     * It is responsible for initializing the policy;
     */
    public void initialize(AbstractSession session) {
        DatabaseMapping mapping = this.descriptor.getObjectBuilder().getMappingForField(getWriteLockField());
        if (mapping == null) {
            if (isStoredInObject()) {
                if (this.descriptor.getObjectBuilder().getReadOnlyMappingsForField(getWriteLockField()) != null) {
                    mapping = this.descriptor.getObjectBuilder().getReadOnlyMappingsForField(getWriteLockField()).get(0);
                    session.getIntegrityChecker().handleError(DescriptorException.mappingCanNotBeReadOnly(mapping));
                } else {
                    session.getIntegrityChecker().handleError(OptimisticLockException.mustHaveMappingWhenStoredInObject(this.descriptor.getJavaClass()));
                }
            } else {
                return;
            }
        }
        if (isStoredInCache()) {
            session.getIntegrityChecker().handleError(DescriptorException.mustBeReadOnlyMappingWhenStoredInCache(mapping));
        }
        // PERF: Cache the mapping if direct.
        if (mapping.isDirectToFieldMapping() && (this.descriptor.getObjectBuilder().getReadOnlyMappingsForField(getWriteLockField()) == null)) {
            this.lockMapping = (AbstractDirectMapping)mapping;
        }
        // If the version field is not in the primary table, then they cannot be batched together.
        if ((this.descriptor.getTables().size() > 0) && !getWriteLockField().getTable().equals(this.descriptor.getTables().get(0))) {
            this.descriptor.setHasMultipleTableConstraintDependecy(true);
        }
    }

    /**
     * INTERNAL:
     * It is responsible for initializing the policy properties;
     */
    public void initializeProperties() {
        DatabaseField dbField = getWriteLockField();
        dbField = descriptor.buildField(dbField);
        setWriteLockField(dbField);
        if (isStoredInCache() && (dbField.getType() == null)) {
            // Set the default type, only if un-mapped.
            dbField.setType(getDefaultLockingFieldType());
        }
        Enumeration enumtr = this.getUnmappedFields().elements();
        while (enumtr.hasMoreElements()) {
            DatabaseField lockField;
            lockField = (DatabaseField)enumtr.nextElement();
            descriptor.getFields().addElement(lockField);
        }
    }

    /**
     * PUBLIC:
     * Return true if the policy uses cascade locking.
     */
    public boolean isCascaded() {
        return isCascaded;
    }

    /**
     * INTERNAL:
     * Compares the value with the value from the object (or cache).
     * Will return true if the currentValue is newer than the domainObject.
     */
    public boolean isNewerVersion(Object currentValue, Object domainObject, Object primaryKey, AbstractSession session) {
        Number writeLockFieldValue;
        Number newWriteLockFieldValue = (Number)currentValue;

        if (isStoredInCache()) {
            writeLockFieldValue = (Number)session.getIdentityMapAccessorInstance().getWriteLockValue(primaryKey, domainObject.getClass(), getDescriptor());
        } else {
            writeLockFieldValue = (Number)lockValueFromObject(domainObject);
        }

        return isNewerVersion(newWriteLockFieldValue, writeLockFieldValue);
    }

    /**
     * INTERNAL:
     * Compares the value from the row and from the object (or cache).
     * Will return true if the row is newer than the object.
     */
    public boolean isNewerVersion(AbstractRecord databaseRow, Object domainObject, Object primaryKey, AbstractSession session) {
        Number writeLockFieldValue;
        Number newWriteLockFieldValue = (Number)databaseRow.get(getWriteLockField());
        if (isStoredInCache()) {
            writeLockFieldValue = (Number)session.getIdentityMapAccessorInstance().getWriteLockValue(primaryKey, domainObject.getClass(), getDescriptor());
        } else {
            writeLockFieldValue = (Number)lockValueFromObject(domainObject);
        }

        return isNewerVersion(newWriteLockFieldValue, writeLockFieldValue);
    }
    
    /**
     * INTERNAL:
     * Compares two values.
     * Will return true if the firstLockFieldValue is newer than the secondWriteLockFieldValue.
     */
    public boolean isNewerVersion(Object firstLockFieldValue, Object secondWriteLockFieldValue) {
        Number firstValue = (Number)firstLockFieldValue;//domain object/clone
        Number secondValue = (Number)secondWriteLockFieldValue;//base value/cache
        
        // 2.5.1.6 if the write lock value is null, then what ever we have is treated as newer.
        if (firstValue == null) {
            return false;
        }
        
        // bug 6342382: first is not null, second is null, so we know first>second.
        if(secondValue == null) {
            return true;
        }
        
        if (firstValue.longValue() > secondValue.longValue()){
            return true;
        }
        return false;
    } 

    /**
     * PUBLIC:
     * Return true if the lock value is stored in the cache.
     */
    public boolean isStoredInCache() {
        return lockValueStored == IN_CACHE;
    }

    /**
     * PUBLIC:
     * Return true if the lock value is stored in the object.
     */
    public boolean isStoredInObject() {
        return lockValueStored == IN_OBJECT;
    }

    /**
     * INTERNAL:
     * Retrieves the lock value from the object.
     */
    protected Object lockValueFromObject(Object domainObject) {
        // PERF: If mapping with a direct mapping get from cached mapping.
        if (this.lockMapping != null) {
            return this.lockMapping.getAttributeValueFromObject(domainObject);
        } else {
            return this.descriptor.getObjectBuilder().getBaseValueForField(this.writeLockField, domainObject);
        }
    }
    
    /**
     * INTERNAL:
     * Returns the mapping that will be used to access the version value from an object.
     */
    
    public AbstractDirectMapping getVersionMapping(){
        if (this.lockMapping != null){
            return this.lockMapping;
        }else{
            return (AbstractDirectMapping)this.descriptor.getObjectBuilder().getBaseMappingForField(this.writeLockField);
        }
    }

    /**
     * INTERNAL:
     * Only applicable when the value is stored in the cache.  Will merge with the parent unit of work.
     */
    public void mergeIntoParentCache(UnitOfWorkImpl uow, Object primaryKey, Object object) {
        if (isStoredInCache()) {
            Object parentValue = uow.getParentIdentityMapSession(descriptor, false, false).getIdentityMapAccessorInstance().getWriteLockValue(primaryKey, object.getClass(), getDescriptor());
            uow.getIdentityMapAccessor().updateWriteLockValue(primaryKey, object.getClass(), parentValue);
        }
    }

    /**
     * INTERNAL:
     * This method should merge changes from the parent into the child.
     *
     * #see this method in VersionLockingPolicy
     */
    public void mergeIntoParentCache(CacheKey unitOfWorkCacheKey, CacheKey parentSessionCacheKey){
        if (isStoredInCache() && unitOfWorkCacheKey != null && parentSessionCacheKey != null) {
            unitOfWorkCacheKey.setWriteLockValue(parentSessionCacheKey.getWriteLockValue());
        }
    }

    /**
     * INTERNAL:
     */
    public void setDescriptor(ClassDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    /**
     * PUBLIC:
     * Set whether to store the lock in the cache or in the object.
     * @param isStoredInCache set this to true if you would like to store lock in the cache and set it
     * to false if you would like to store it in the object.
     */
    public void setIsStoredInCache(boolean isStoredInCache) {
        if (isStoredInCache) {
            storeInCache();
        } else {
            storeInObject();
        }
    }

    /**
     * PUBLIC:
     * Set whether to use cascade locking on the policy.
     * @param isCascaded set this to true if you would like cascade the locking
     * and set it to false if you would like no cascade locking.
     */
    public void setIsCascaded(boolean isCascaded) {
        this.isCascaded = isCascaded;
    }

    /**
     * INTERNAL:
     * This method must be included in any locking policy.
     * Put the initial writelock value into the modifyRow.
     */
    public void setupWriteFieldsForInsert(ObjectLevelModifyQuery query) {
        Object lockValue = getInitialWriteValue(query.getSession());
        ObjectChangeSet objectChangeSet = query.getObjectChangeSet();
        if (objectChangeSet != null) {
            objectChangeSet.setInitialWriteLockValue(lockValue);
        }
        updateWriteLockValueForWrite(query, lockValue);
    }

    /**
     * INTERNAL:
     * Update the row, object and change set with the version value.
     * This handles the version being mapped in nested aggregates, writable or read-only.
     */
    protected void updateWriteLockValueForWrite(ObjectLevelModifyQuery query, Object lockValue) {
        // PERF: direct-access.
        query.getModifyRow().put(this.writeLockField, lockValue);
        updateObjectWithWriteValue(query, lockValue);
    }
    
    /**
     * INTERNAL:
     * Returns true if the policy has been set to set an optimistic read lock when a owning mapping changes.
     */
    public boolean shouldUpdateVersionOnOwnedMappingChange(){
        return this.lockOnChangeMode == LockOnChange.OWNING;
    }

    /**
     * INTERNAL:
     * Returns true if the policy has been set to set an optimistic read lock when any mapping changes.
     */
    public boolean shouldUpdateVersionOnMappingChange(){
        return this.lockOnChangeMode == LockOnChange.ALL;
    }

    public void updateObjectWithWriteValue(ObjectLevelModifyQuery query, Object lockValue){
        AbstractSession session = query.getSession();
        Object object = query.getObject();
        ObjectChangeSet objectChangeSet = query.getObjectChangeSet();
        if (objectChangeSet == null) {
            if (session.isUnitOfWork() && (((UnitOfWorkImpl)session).getUnitOfWorkChangeSet() != null)) {
                // For aggregate collections the change set may be null, as they use the old commit still.
                objectChangeSet = (ObjectChangeSet)((UnitOfWorkImpl)session).getUnitOfWorkChangeSet().getObjectChangeSetForClone(object);
            }
        }
        // PERF:  handle normal case faster.
        if (this.lockMapping != null) {
            // converted to the correct (for the mapping) type lock value.
            Object convertedLockValue = this.lockMapping.getObjectValue(lockValue, session); 
            if (objectChangeSet != null && (!objectChangeSet.isNew() || query.getDescriptor().shouldUseFullChangeSetsForNewObjects())) {
                Object oldValue = this.lockMapping.getAttributeValueFromObject(object);
                this.lockMapping.setAttributeValueInObject(object, convertedLockValue);
                objectChangeSet.setWriteLockValue(lockValue);
                // Don't use ObjectChangeSet.updateChangeRecordForAttributeWithMappedObject to avoid unnecessary conversion - convertedLockValue is already converted.
                DirectToFieldChangeRecord changeRecord = new DirectToFieldChangeRecord(objectChangeSet);
                changeRecord.setAttribute(this.lockMapping.getAttributeName());
                changeRecord.setMapping(this.lockMapping);
                changeRecord.setNewValue(convertedLockValue);
                changeRecord.setOldValue(oldValue);
                objectChangeSet.addChange(changeRecord);

            } else {
                this.lockMapping.setAttributeValueInObject(object, convertedLockValue);
            }
        } else {
            // CR#3173211
            // If the value is stored in the cache or object, there still may
            // be read-only mappings for it, so the object must always be updated for
            // any writable or read-only mappings for the version value.
            // Reuse the method used for returning as has the same requirements.
            ObjectBuilder objectBuilder = this.descriptor.getObjectBuilder();
            AbstractRecord record = objectBuilder.createRecord(1, session);
            record.put(this.writeLockField, lockValue);
            if (objectChangeSet != null) {
                objectChangeSet.setWriteLockValue(lockValue);
            }
            objectBuilder.assignReturnRow(object, session, record, objectChangeSet);            
        }
    }

    /**
     * ADVANCED:
     * Sets the LockOnChange mode for this policy.  This mode specifies if a 
     * Optimistic Write lock should be enforced on this entity when set of mappings are changed.
     */
    public void setLockOnChangeMode(LockOnChange lockOnChangeMode){
        this.lockOnChangeMode = lockOnChangeMode;
    }

    /**
     * ADVANCED:
     * Set the write lock field.
     * This can be used for advanced field types, such as XML nodes, or to set the field type.
     */
    public void setWriteLockField(DatabaseField writeLockField) {
        this.writeLockField = writeLockField;
    }

    /**
     * PUBLIC:
     * Set the write lock field name.
     * @param writeLockFieldName the name of the field to lock against.
     */
    public void setWriteLockFieldName(String writeLockFieldName) {
        setWriteLockField(new DatabaseField(writeLockFieldName));
    }

    /**
     * PUBLIC:
     * Configure the version lock value to be stored in the cache.
     * This allows for the object not to require to store its version value as an attribute.
     * Note: if using a stateless model where the object can be passed to a client and then
     * later updated in a different transaction context, then the version lock value should
     * not be stored in the cache, but in the object to ensure it is the correct value for
     * that object.  This is the default.
     */
    public void storeInCache() {
        lockValueStored = IN_CACHE;
    }

    /**
     * PUBLIC:
     * Configure the version lock value to be stored in the object.
     * The object must define a mapping and an attribute to store the version value.
     * Note: the value will be updated internally by EclipseLink and should not be updated
     * by the application.
     */
    public void storeInObject() {
        lockValueStored = IN_OBJECT;
    }

    /**
     * INTERNAL:
     * This method updates the modify row, and the domain object
     * with the new lock value.
     */
    public void updateRowAndObjectForUpdate(ObjectLevelModifyQuery query, Object domainObject) {
        Object lockValue = getNewLockValue(query);
        if (isStoredInCache()) {
            query.getSession().getIdentityMapAccessor().updateWriteLockValue(query.getPrimaryKey(), domainObject.getClass(), lockValue);
        }
        updateWriteLockValueForWrite(query, lockValue);
    }

    /**
     * INTERNAL:
     * This method updates the modify row with the old lock value.
     */
    public void writeLockValueIntoRow(ObjectLevelModifyQuery query, Object domainObject) {
        Object lockValue = getWriteLockValue(domainObject, query.getPrimaryKey(), query.getSession());
        query.getModifyRow().put(this.writeLockField, lockValue);
        if (isStoredInCache()) {
            query.getSession().getIdentityMapAccessor().updateWriteLockValue(query.getPrimaryKey(), domainObject.getClass(), lockValue);
        }
    }

    /**
     * INTERNAL:
     * Check the row count for lock failure.
     */
    public void validateDelete(int rowCount, Object object, DeleteObjectQuery query) {
        if (rowCount <= 0) {
            // Mark the object as invalid in the session cache, only if version is the same as in query.
            Object primaryKey = query.getPrimaryKey();
            AbstractSession session = query.getSession().getParentIdentityMapSession(query, true, true);
            CacheKey cacheKey = session.getIdentityMapAccessorInstance().getCacheKeyForObject(primaryKey, query.getReferenceClass(), query.getDescriptor(), false);
            if ((cacheKey != null) && (cacheKey.getObject() != null) && (query.getObjectChangeSet() != null)) {
                Object queryVersion = query.getObjectChangeSet().getInitialWriteLockValue();
                Object cacheVersion = getWriteLockValue(cacheKey.getObject(), primaryKey, session);
                if (compareWriteLockValues(queryVersion, cacheVersion) != 0) {
                    cacheKey.setInvalidationState(CacheKey.CACHE_KEY_INVALID);
                }
            }
            throw OptimisticLockException.objectChangedSinceLastReadWhenDeleting(object, query);
        }
    }

    /**
     * INTERNAL:
     * Check the row count for lock failure.
     */
    public void validateUpdate(int rowCount, Object object, WriteObjectQuery query) {
        if (rowCount <= 0) {
            // Mark the object as invalid in the session cache, only if version is the same as in query.
            Object primaryKey = query.getPrimaryKey();
            AbstractSession session = query.getSession().getParentIdentityMapSession(query, true, true);
            CacheKey cacheKey = session.getIdentityMapAccessorInstance().getCacheKeyForObject(primaryKey, query.getReferenceClass(), query.getDescriptor(), false);
            if ((cacheKey != null) && (cacheKey.getObject() != null) && (query.getObjectChangeSet() != null)) {
                Object queryVersion = query.getObjectChangeSet().getInitialWriteLockValue();
                Object cacheVersion = getWriteLockValue(cacheKey.getObject(), primaryKey, session);
                if (compareWriteLockValues(queryVersion, cacheVersion) >= 0) {
                    cacheKey.setInvalidationState(CacheKey.CACHE_KEY_INVALID);
                }
            }
            throw OptimisticLockException.objectChangedSinceLastReadWhenUpdating(object, query);
        }
    }
}
