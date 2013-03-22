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
package org.eclipse.persistence.queries;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * <p><b>Purpose</b>:
 * This should only be used by the descriptor, this should not be executed directly.
 * Used to determine if an object resides on the database.
 * DoesExistQuery is normally used to determine whether to make an update
 * or insert statement when writing an object.
 *
 * <p><b>Responsibilities</b>:
 * Verify the existence of an object. Used only by a write object query.
 *
 * @author Yvon Lavoie
 * @since TOPLink/Java 1.0
 */
public class DoesExistQuery extends DatabaseQuery {
    public static final int AssumeNonExistence = 1;
    public static final int AssumeExistence = 2;
    public static final int CheckCache = 3;
    public static final int CheckDatabase = 4;

    /** Query that is performing the does exist check. */
    protected Object primaryKey;
    protected Object object;

    /** Flag to determine existence check policy. */
    protected int existencePolicy;
    
    /**
     * Flag to determine cache invalidation policy support.  This overrides
     * the CheckCache existence setting if the object is invalid or if the
     * cache cannot be trusted because a flush or DML has occurred.
     * The default is true.
     */
    protected boolean checkDatabaseIfInvalid; //default to true, allows users to override
    
    /**
     * Flag to determine if the cache should be check first in addition to another option.
     * The default is true;
     */
    public boolean checkCacheFirst; //default to true

    /**
     * PUBLIC:
     * Initialize the state of the query .
     * By default the cache is checked, if non cache is used the descriptor should throw a exception and validate.
     */
    public DoesExistQuery() {
        this.existencePolicy = CheckCache;
        this.checkDatabaseIfInvalid = true;
        this.checkCacheFirst = true;
    }

    /**
     * PUBLIC:
     * Create a query to check if the object exists.
     */
    public DoesExistQuery(Object object) {
        this();
        this.object = object;
    }

    /**
     * PUBLIC:
     * Create a query to check if the object exists.
     */
    public DoesExistQuery(Call call) {
        this();
        setCall(call);
    }

    /**
     * PUBLIC:
     * Assume that if the objects primary key does not include null then it must exist.
     * This may be used if the user's system guarantees that an object with non-null key exists.
     */
    public void assumeExistenceForDoesExist() {
        setExistencePolicy(AssumeExistence);
    }

    /**
     * PUBLIC:
     * Assume that the object does not exist.
     * This may be used if the user's system guarantees objects must always be inserted.
     */
    public void assumeNonExistenceForDoesExist() {
        setExistencePolicy(AssumeNonExistence);
    }

    /**
     * PUBLIC:
     * Assume that if the objects primary key does not include null
     * and it is in the cache, then is must exist.
     * This should only be used if a full identity map is being used,
     * and a new object in the client cannot have been inserted by another client.
     */
    public void checkCacheForDoesExist() {
        setExistencePolicy(CheckCache);
    }

    /**
     * PUBLIC:
     * Perform does exist check on the database through selecting the primary key.
     */
    public void checkDatabaseForDoesExist() {
        setExistencePolicy(CheckDatabase);
    }

    /**
     * INTERNAL:
     * Check if existence can be determined without going to the database.
     * Note that custom query check is not require for does exist as the custom is always used.
     * Used by unit of work, and will return null if checkDatabaseIfInvalid is set and the cachekey is invalidated
     */
    public Object checkEarlyReturn(Object object, Object primaryKey, AbstractSession session, AbstractRecord translationRow) {
        // For bug 3136413/2610803 building the selection criteria from an EJBQL string or
        // an example object is done just in time.
        buildSelectionCriteria(session);
        // Return false on null since it can't exist.  Little more done in case PK not set in the query
        if  (object == null){ 
            return Boolean.FALSE;
        }
        ClassDescriptor descriptor = session.getDescriptor(object.getClass());
        if (primaryKey == null) {
            primaryKey = getPrimaryKey();
            if (primaryKey == null) {
                primaryKey = descriptor.getObjectBuilder().extractPrimaryKeyFromObject(object, session, true);
            }
                
        }
        if (primaryKey == null) {
            return Boolean.FALSE;
        }
        
        // Need to do the cache check first if flag set or if we should check the cache only for existence.
        if ((shouldCheckCacheForDoesExist() ||this.checkCacheFirst) && !descriptor.isDescriptorForInterface()) {
        
            // If this is a UOW and modification queries have been executed, the cache cannot be trusted.
            if (this.checkDatabaseIfInvalid && (session.isUnitOfWork() &&  ((UnitOfWorkImpl)session).shouldReadFromDB())) {
                return null;
            }
                
            CacheKey cacheKey;
            Class objectClass = object.getClass();
            AbstractSession tempSession = session;
            if (tempSession.isUnitOfWork()){
                cacheKey = tempSession.getIdentityMapAccessorInstance().getCacheKeyForObjectForLock(primaryKey, objectClass, descriptor);
                if (cacheKey != null) {
                    // If in the UOW cache it can't be invalid.
                    return Boolean.TRUE;
                }
                while (((UnitOfWorkImpl)tempSession).isNestedUnitOfWork() ){ //could be nested lets check all UOWs
                    tempSession = ((UnitOfWorkImpl)tempSession).getParent();
                    cacheKey = tempSession.getIdentityMapAccessorInstance().getCacheKeyForObjectForLock(primaryKey, objectClass, descriptor);
                    if (cacheKey != null) {
                        // If in the UOW cache it can't be invalid.
                        return Boolean.TRUE;
                    }
                }
                tempSession = ((UnitOfWorkImpl)tempSession).getParentIdentityMapSession(descriptor, false, true);
            }
            // Did not find it registered in UOW so check main cache and check for invalidation.
            cacheKey = tempSession.getIdentityMapAccessorInstance().getCacheKeyForObject(primaryKey,objectClass, descriptor, false);
                
            if ((cacheKey != null)) {
                // Assume that if there is a cachekey, object exists.
                if (this.checkDatabaseIfInvalid) {
                    checkDescriptor(object, session);
                    if (this.descriptor.getCacheInvalidationPolicy().isInvalidated(cacheKey, System.currentTimeMillis())) {
                        return null;
                    }
                }
                
                Object objectFromCache = cacheKey.getObject();
                if ((session.isUnitOfWork()) && ((UnitOfWorkImpl)session).wasDeleted(objectFromCache)) {
                    if (shouldCheckCacheForDoesExist()) {
                        return Boolean.FALSE;
                    }
                } else {
                    return Boolean.TRUE;
                }
            } else if (shouldCheckCacheForDoesExist()) {
                // We know its not in cache, and a checkcache policy so return false.
                return Boolean.FALSE;
            }
        }
        // Check if we have to assume that the object does not exist.
        if (shouldAssumeNonExistenceForDoesExist()) {
            return Boolean.FALSE;
        }

        // Check to see if we only need to check that the object contains a primary key.
        if (shouldAssumeExistenceForDoesExist()) {
            return Boolean.TRUE;
        }

        return null;
    }

    /**
     * INTERNAL:
     * Check if existence can be determined without going to the database.
     * Note that custom query check is not require for does exist as the custom is always used.
     */
    public Object checkEarlyReturn(AbstractSession session, AbstractRecord translationRow) {
        return checkEarlyReturn(getObject(), getPrimaryKey(), session, translationRow);
    }

    /**
     * INTERNAL:
     * Return if the object exists on the database.
     * This must be a Boolean object to conform with returning an object.
     * If using optimistic locking, check that the value matches.
     * @exception  DatabaseException - an error has occurred on the database.
     */
    public Object executeDatabaseQuery() throws DatabaseException {
        // Get the required fields for does exist check.
        DatabaseField field = getDoesExistField();

        // Get row from database
        AbstractRecord databaseRow = getQueryMechanism().selectRowForDoesExist(field);

        // Null means no row was returned.
        return Boolean.valueOf(databaseRow != null);
    }

    /**
     * INTERNAL:
     * Return the write lock field or the first primary key field if not using locking.
     */
    protected DatabaseField getDoesExistField() {
        return this.descriptor.getPrimaryKeyFields().get(0);
    }

    /**
     * INTERNAL:
     * Return the existence policy for this existence Query
     */
    public int getExistencePolicy() {
        return this.existencePolicy;
    }

    /**
     * PUBLIC:
     * Return the object.
     */
    public Object getObject() {
        return object;
    }

    /**
     * INTERNAL:
     * Return the primaryKey.
     */
    public Object getPrimaryKey() {
        return primaryKey;
    }

    /**
     * Return the domain class associated with this query.
     */
    public Class getReferenceClass() {
        if (getObject() == null) {
            return null;
        }
        return getObject().getClass();
    }

    /**
     * INTERNAL:
     * Return the name of the reference class for this query
     * Note: Although the API is designed to avoid requirement of classes being on the classpath,
     * this is not a user defined query type, so it is ok to access the class.
     */
    public String getReferenceClassName() {
        return getReferenceClass().getName();
    }

    /**
     * INTERNAL:
     * Prepare the receiver for execution in a session.
     */
    protected void prepare() throws QueryException {

        if (getObject() != null) {// Prepare can be called without the object set yet.
            checkDescriptor(getObject(), getSession());
            setObject(getDescriptor().getObjectBuilder().unwrapObject(getObject(), getSession()));
        }

        super.prepare();

        // It will only get to prepare if check database if required.
        getQueryMechanism().prepareDoesExist(getDoesExistField());
    }
    
    /**
     * INTERNAL:
     * Ensure that the descriptor has been set.
     */
    public void checkDescriptor(Object object, AbstractSession session) throws QueryException {
        if (this.descriptor == null) {
            if (object == null) {
                throw QueryException.objectToModifyNotSpecified(this);
            }

            //Bug#3947714  Pass the object instead of class in case object is proxy            
            ClassDescriptor referenceDescriptor = session.getDescriptor(object);
            if (referenceDescriptor == null) {
                throw QueryException.descriptorIsMissing(object.getClass(), this);
            }
            setDescriptor(referenceDescriptor);
        }
    }
    
    /**
     * INTERNAL:
     * Prepare the receiver for execution in a session.
     */
    public void prepareForExecution() throws QueryException {
        super.prepareForExecution();

        if (getObject() == null) {
            throw QueryException.objectToModifyNotSpecified(this);
        }
        setObject(this.descriptor.getObjectBuilder().unwrapObject(getObject(), getSession()));

        if (this.descriptor == null) {
            setDescriptor(getSession().getDescriptor(getObject().getClass()));
        }

        if (getPrimaryKey() == null) {
            setPrimaryKey(this.descriptor.getObjectBuilder().extractPrimaryKeyFromObject(getObject(), getSession()));
        }

        if ((getTranslationRow() == null) || (getTranslationRow().isEmpty())) {
            setTranslationRow(this.descriptor.getObjectBuilder().buildRowForTranslation(getObject(), getSession()));
        }
    }

    /**
     * INTERNAL:
     * Set if the existence policy, this must be set to one of the constants.
     */
    public void setExistencePolicy(int existencePolicy) {
        this.existencePolicy = existencePolicy;
    }

    /**
     * PUBLIC:
     * Set the object.
     */
    public void setObject(Object object) {
        this.object = object;
    }

    /**
     * INTERNAL:
     * Set the primaryKey.
     */
    public void setPrimaryKey(Object primaryKey) {
        this.primaryKey = primaryKey;
    }

    /**
     * PUBLIC:
     * Returns true if the does exist check should be based only
     * on whether the primary key of the object is set
     */
    public boolean shouldAssumeExistenceForDoesExist() {
        return existencePolicy == AssumeExistence;
    }

    /**
     * PUBLIC:
     * Returns true if the does exist check should assume non existence.
     */
    public boolean shouldAssumeNonExistenceForDoesExist() {
        return existencePolicy == AssumeNonExistence;
    }

    /**
     * PUBLIC:
     * Returns true if the does exist check should be based only
     * on a cache check.  Default behavior.
     */
    public boolean shouldCheckCacheForDoesExist() {
        return existencePolicy == CheckCache;
    }

    /**
     * PUBLIC:
     * Returns true if the does exist check should query the database.
     */
    public boolean shouldCheckDatabaseForDoesExist() {
        return existencePolicy == CheckDatabase;
    }
    
    /**
     * INTERNAL:
     * Sets checkCacheFirst flag.  If true, existence check will first go to the 
     * cache.  It will then check other options if it is not found in the cache
     * @param checkCacheFirst 
     */
    public void setCheckCacheFirst(boolean checkCacheFirst){
        this.checkCacheFirst = checkCacheFirst;
    }
    /**
     * INTERNAL:
     * @param checkCacheFirst 
     */
    public boolean getCheckCacheFirst(){
        return this.checkCacheFirst;
    }
    
    /**
     * INTERNAL:
     * Sets checkDatabaseIfInvalid flag.  If true, query will go to the 
     * database when it finds the object in the cache and it is invalid.  
     * This is only valid when it checks the cache, and is true by default
     * @param checkDatabaseIfInvalid 
     */
    public void setCheckDatabaseIfInvalid(boolean checkCacheFirst){
        this.checkCacheFirst = checkCacheFirst;
    }
    /**
     * INTERNAL:
     * @param checkDatabaseIfInvalid 
     */
    public boolean getCheckDatabaseIfInvalid(){
        return this.checkCacheFirst;
    }
}
