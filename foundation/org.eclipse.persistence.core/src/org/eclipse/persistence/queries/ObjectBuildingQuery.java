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

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.*;
import org.eclipse.persistence.internal.expressions.*;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.MergeManager;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.queries.JoinedAttributeManager;
import org.eclipse.persistence.mappings.CollectionMapping;

/**
 * <p><b>Purpose</b>:
 * Abstract class for all read queries that build objects and potentially manipulate
 * the TopLink cache.
 *
 * <p><b>Description</b>:
 * Contains common behavior for all read queries building objects.
 *
 * @author Gordon Yorke
 * @since TopLink Essentials
 */
public abstract class ObjectBuildingQuery extends ReadQuery {

    /** The class of the target objects to be read from the database. */
    protected Class referenceClass;
    protected String referenceClassName;

    /** Allows for the resulting objects to be refresh with the data from the database. */
    protected boolean shouldRefreshIdentityMapResult;
    protected boolean shouldRefreshRemoteIdentityMapResult;

    /** INTERNAL: for bug 2612601 allow ability not to register results in UOW. */
    protected boolean shouldRegisterResultsInUnitOfWork = true;

    /** Used for pessimistic locking. */
    protected ForUpdateClause lockingClause;
    public static final short NO_LOCK = 0;
    public static final short LOCK = 1;
    public static final short LOCK_NOWAIT = 2;
    // allow pessimistic locking policy to be used
    public static final short DEFAULT_LOCK_MODE = -1;

    /**
     * Used to set the read time on objects that use this query.
     * Should be set to the time the query returned from the database.
     */
    protected long executionTime = 0;

    /**
     * Added for Exclusive Connection (VPD) support see accessor for information
     */
    protected boolean shouldUseExclusiveConnection = false;

    /**
     * INTERNAL:  This is the key for accessing unregistered and locked result in the query's properties.
     * The uow and  QueryBaseValueHolder use this property to record and to retrieve the result respectively.
     */
    public static final String LOCK_RESULT_PROPERTY = "LOCK_RESULT";

    /** PERF: Store if the query originally used the default lock mode. */
    protected boolean wasDefaultLockMode = false;
    
    /**
     * INTERNAL:  If primary key is null ObjectBuilder.buildObject returns null
     * in case this flag is set to true (instead of throwing exception).
     */
    protected boolean shouldBuildNullForNullPk;
    
    /**
     * When reading across relationships, queries may be set to acquire deferred locks
     * This is used to ensure any Eagerly fetched object that is the target of a relationship
     * with an object the acquires deferred locks behaves the same as its owner
     */
    protected Boolean requiresDeferredLocks = null;
    
    /** was a check early return completed */
    protected boolean isCacheCheckComplete;

    /**
     * INTERNAL:
     * Initialize the state of the query
     */
    public ObjectBuildingQuery() {
        this.shouldRefreshIdentityMapResult = false;
        this.isCacheCheckComplete = false;
    }

    /**
     * INTERNAL:
     * Clone the query
     */
    public Object clone() {
        ObjectBuildingQuery cloneQuery = (ObjectBuildingQuery) super.clone();
        cloneQuery.isCacheCheckComplete = this.isCacheCheckComplete;
        return cloneQuery;
    }
    /**
     * INTERNAL
     * Used to give the subclasses opportunity to copy aspects of the cloned query
     * to the original query.
     */
    protected void clonedQueryExecutionComplete(DatabaseQuery query, AbstractSession session) {
        super.clonedQueryExecutionComplete(query, session);
        //reset cache check flag for next execution.
        this.isCacheCheckComplete = false;
    }

    /**
     * INTERNAL:
     * Convert all the class-name-based settings in this query to actual class-based
     * settings. This method is used when converting a project that has been built
     * with class names to a project with classes.
     * @param classLoader 
     */
    public void convertClassNamesToClasses(ClassLoader classLoader){
        super.convertClassNamesToClasses(classLoader);
        Class referenceClass = null;
        try{
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                try {
                    referenceClass = (Class)AccessController.doPrivileged(new PrivilegedClassForName(getReferenceClassName(), true, classLoader));
                } catch (PrivilegedActionException exception) {
                    throw ValidationException.classNotFoundWhileConvertingClassNames(getReferenceClassName(), exception.getException());
                }
            } else {
                referenceClass = org.eclipse.persistence.internal.security.PrivilegedAccessHelper.getClassForName(getReferenceClassName(), true, classLoader);
            }
        } catch (ClassNotFoundException exc){
            throw ValidationException.classNotFoundWhileConvertingClassNames(getReferenceClassName(), exc);
        }
        setReferenceClass(referenceClass);
    };

    /**
     * INTERNAL:
     * Return if this query originally used the default lock mode.
     */
    protected boolean wasDefaultLockMode() {
        return wasDefaultLockMode;
    }
    
    /**
     * INTERNAL:
     * Set if this query originally used the default lock mode.
     */
    protected void setWasDefaultLockMode(boolean wasDefaultLockMode) {
        this.wasDefaultLockMode = wasDefaultLockMode;
    }
    
    /**
     * INTERNAL:
     * Clone the query, including its selection criteria.
     * <p>
     * Normally selection criteria are not cloned here as they are cloned
     * later on during prepare.
     */
    public Object deepClone() {
    	return clone();
    }
        
    /**
     * INTERNAL:
     * Copy all setting from the query.
     * This is used to morph queries from one type to the other.
     * By default this calls prepareFromQuery, but additional properties may be required
     * to be copied as prepareFromQuery only copies properties that affect the SQL.
     */
    public void copyFromQuery(DatabaseQuery query) {
        super.copyFromQuery(query);
        if (query.isObjectBuildingQuery()) {
            ObjectBuildingQuery readQuery = (ObjectBuildingQuery)query;
            this.shouldBuildNullForNullPk = readQuery.shouldBuildNullForNullPk;
            this.shouldRefreshIdentityMapResult = readQuery.shouldRefreshIdentityMapResult;
            this.shouldRefreshRemoteIdentityMapResult = readQuery.shouldRefreshRemoteIdentityMapResult;
            this.shouldRegisterResultsInUnitOfWork = readQuery.shouldRegisterResultsInUnitOfWork;
            this.shouldUseExclusiveConnection = readQuery.shouldUseExclusiveConnection;
        }
    }
    
    /**
     * INTERNAL:
     * Set the properties needed to be cascaded into the custom query including the translation row.
     * This is used only for primary key queries, as the descriptor query manager
     * stores a predefined query for this query to avoid having to re-prepare and allow for customization.
     */
    protected void prepareCustomQuery(DatabaseQuery customQuery) {
        ((ObjectBuildingQuery)customQuery).isCacheCheckComplete = this.isCacheCheckComplete;
    }

    /**
     * INTERNAL:
     * Prepare the query from the prepared query.
     * This allows a dynamic query to prepare itself directly from a prepared query instance.
     * This is used in the EJBQL parse cache to allow preparsed queries to be used to prepare
     * dynamic queries.
     * This only copies over properties that are configured through EJBQL.
     */
    public void prepareFromQuery(DatabaseQuery query) {
        super.prepareFromQuery(query);
        if (query.isObjectBuildingQuery()) {
            ObjectBuildingQuery objectQuery = (ObjectBuildingQuery)query;
            this.referenceClass = objectQuery.referenceClass;
            this.referenceClassName = objectQuery.referenceClassName;
            this.lockingClause = objectQuery.lockingClause;
            this.wasDefaultLockMode = objectQuery.wasDefaultLockMode;
        }
    }
    
    /**
     * PUBLIC:
     * When unset means perform read normally and dont do refresh.
     */
    public void dontRefreshIdentityMapResult() {
        setShouldRefreshIdentityMapResult(false);
    }

    /**
     * PUBLIC:
     * When unset means perform read normally and dont do refresh.
     */
    public void dontRefreshRemoteIdentityMapResult() {
        setShouldRefreshRemoteIdentityMapResult(false);
    }

    /**
     * INTERNAL:
     * Indicates whether a FetchGroup will be applied to the query.
     */
    public boolean hasExecutionFetchGroup() {
        return false;
    }

    /**
     * INTERNAL:
     * Returns FetchGroup that will be applied to the query.
     * Note that the returned fetchGroup may be updated during preProcess.
     */
    public FetchGroup getExecutionFetchGroup() {
        return null;
    }

    /**
     * INTERNAL:
     * Returns FetchGroup that will be applied to the query.
     * Note that the returned fetchGroup may be updated during preProcess.
     */
    public FetchGroup getExecutionFetchGroup(ClassDescriptor descriptor) {
        return null;
    }

    /**
     * Return the load group set in the query.
     */
    public LoadGroup getLoadGroup() {
        return null;
    }

    /**
     * PUBLIC: Return the current locking mode.
     */
    public short getLockMode() {
        if (lockingClause == null) {
            return DEFAULT_LOCK_MODE;
        } else {
            return lockingClause.getLockMode();
        }
    }

    /**
     * INTERNAL:
     * Return  all of the rows fetched by the query, used for 1-m joining.
     */
    public List getDataResults() {
        return null;
    }

    /**
     * INTERNAL:
     * Return the time this query actually went to the database
     */
    public long getExecutionTime() {
        return executionTime;
    }

    /**
     * INTERNAL:
     * Return the primary key stored in this query if there is one
     * This is overridden by subclasses that actually hold a primary key
     * 
     * @return
     * 
     * @see ReadObjectQuery
     */
    protected Object getQueryPrimaryKey(){
        return null;
    }
    
    /**
     * PUBLIC:
     * Return the reference class of the query.
     */
    public Class getReferenceClass() {
        return referenceClass;
    }

    /**
     * INTERNAL:
     * Return the reference class of the query.
     */
    public String getReferenceClassName() {
        if ((referenceClassName == null) && (referenceClass != null)) {
            referenceClassName = referenceClass.getName();
        }
        return referenceClassName;
    }

    /**
     * INTERNAL:
     * Return if partial attributes.
     */
    public boolean hasPartialAttributeExpressions() {
        return false;
    }

    /**
     * PUBLIC:
     * Answers if the query lock mode is known to be LOCK or LOCK_NOWAIT.
     *
     * In the case of DEFAULT_LOCK_MODE and the query reference class being a CMP entity bean,
     * at execution time LOCK, LOCK_NOWAIT, or NO_LOCK will be decided.
     * <p>
     * If a single joined attribute was configured for pessimistic locking then
     * this will return true (after first execution) as the SQL contained a
     * FOR UPDATE OF clause.
     */
    public boolean isLockQuery() {
        return getLockMode() > NO_LOCK;
    }

    /**
     * PUBLIC:
     * Return if this is an object building query.
     */
    public boolean isObjectBuildingQuery() {
        return true;
    }

    /**
     * INTERNAL:
     * Answers if we are executing through a UnitOfWork and registering results.
     * This is only ever false if using the conforming without registering
     * feature.
     */
    public boolean isRegisteringResults() {
        return ((shouldRegisterResultsInUnitOfWork() && this.descriptor.shouldRegisterResultsInUnitOfWork()) || isLockQuery());
    }

    /**
     * PUBLIC:
     * Refresh the attributes of the object(s) resulting from the query.
     * If cascading is used the private parts of the objects will also be refreshed.
     */
    public void refreshIdentityMapResult() {
        setShouldRefreshIdentityMapResult(true);
    }

    /**
     * PUBLIC:
     * Refresh the attributes of the object(s) resulting from the query.
     * If cascading is used the private parts of the objects will also be refreshed.
     */
    public void refreshRemoteIdentityMapResult() {
        setShouldRefreshRemoteIdentityMapResult(true);
    }

    /**
     * INTERNAL:
     * Constructs the final (registered) object for every individual object
     * queried via a UnitOfWork.
     * <p>
     * Called for every object in a read all, the object in a read object, and
     * every time the next or previous row is retrieved from a cursor.
     * <p>
     * The (conform) without registering feature is implemented here, and may
     * return an original non UnitOfWork registered result.
     * <p>
     * Pessimistically locked objects are tracked here.
     *
     * @return a refreshed UnitOfWork queried object, unwrapped.
     */
    public Object registerIndividualResult(Object result, Object primaryKey, UnitOfWorkImpl unitOfWork, JoinedAttributeManager joinManager, ClassDescriptor concreteDescriptor) {
        if (concreteDescriptor == null) {
            concreteDescriptor = unitOfWork.getDescriptor(result.getClass());
        }
        // PERF: Do not register nor process read-only.
        if (unitOfWork.isClassReadOnly(result.getClass(), concreteDescriptor)) {
            // There is an obscure case where they object could be read-only and pessimistic.
            // Record clone if referenced class has pessimistic locking policy.
            recordCloneForPessimisticLocking(result, unitOfWork);
            return result;
        }
        Object clone = null;
        // For bug 2612601 Conforming without registering in Unit Of Work.
        if (!isRegisteringResults()) {
            if (primaryKey == null) {
                primaryKey = concreteDescriptor.getObjectBuilder().extractPrimaryKeyFromObject(result, getSession());
            }
            clone = unitOfWork.getIdentityMapAccessorInstance().getIdentityMapManager().getFromIdentityMap(primaryKey, result.getClass(), concreteDescriptor);

            // If object not registered do not register it here!  Simply return 
            // the original to the user.
            // Avoid setting clone = original, in case revert(clone) is called.
            if (clone == null) {
                clone = result;
            }
        } else {
            // bug # 3183379 either the object comes from the shared cache and is existing, or
            //it is from a parent unit of work and this unit of work does not need to know if it is new
            //or not.  It will query the parent unit of work to determine newness.
           
            clone = unitOfWork.registerExistingObject(result, concreteDescriptor, getQueryPrimaryKey(), true);
        }
        postRegisterIndividualResult(clone, result, primaryKey, unitOfWork, joinManager, concreteDescriptor);
        return clone;
    }

    /**
     * Post process the object once it is registered in the unit of work.
     */
    public void postRegisterIndividualResult(Object clone, Object original, Object primaryKey, UnitOfWorkImpl unitOfWork, JoinedAttributeManager joinManager, ClassDescriptor concreteDescriptor) {
        // Check for refreshing, require to revert in the unit of work to accomplish a refresh.
        if (shouldRefreshIdentityMapResult()) {
            
            if (shouldCascadeAllParts()) {
                unitOfWork.mergeClone(original, MergeManager.CASCADE_ALL_PARTS, true);
            } else if (shouldCascadePrivateParts()) {
                unitOfWork.mergeClone(original, MergeManager.CASCADE_PRIVATE_PARTS, true);
            } else if (shouldCascadeByMapping()) {
                unitOfWork.mergeClone(original, MergeManager.CASCADE_BY_MAPPING, true);
            } else if (!shouldCascadeParts()) {
                unitOfWork.mergeClone(original, MergeManager.NO_CASCADE, true);
            }
        }
        //bug 6130550: trigger indirection on the clone where required due to fetch joins on the query
        if (joinManager != null && joinManager.hasJoinedAttributeExpressions()) { 
            triggerJoinExpressions(unitOfWork, joinManager, clone, concreteDescriptor);
        }
    }

    /**
     * INTERNAL:
     * Fetch/trigger indirection on the clone passed in, based on join expressions in the joinManager.
     */
    private void triggerJoinExpressions(UnitOfWorkImpl unitOfWork, JoinedAttributeManager joinManager, Object clone, ClassDescriptor concreteDescriptor) {
        List joinExpressions = joinManager.getJoinedAttributeExpressions();
        int size = joinExpressions.size();
        if ((size == 0) || (clone == null)) {
            return;
        }
        if (concreteDescriptor == null) {
            concreteDescriptor = unitOfWork.getDescriptor(clone.getClass());
        }
        for (int index = 0; index < size; index++) {//since a's descriptor won't have a mapping for 'b'. 
            //baseExpression will be first relationship expression after the ExpressionBuilder, and may include aggregate intermediaries
            QueryKeyExpression baseExpression = (QueryKeyExpression)joinManager.getJoinedAttributes().get(index);
            DatabaseMapping mapping = joinManager.getJoinedAttributeMappings().get(index);

            if (mapping != null) {
                Object attributeValue = joinManager.getValueFromObjectForExpression(unitOfWork, clone, baseExpression);
                if (attributeValue != null) {
                    //recurse through the mapping if the expression's base isn't the base expressionBuilder
                    QueryKeyExpression queryKeyExpression = (QueryKeyExpression)joinExpressions.get(index);
                    if (baseExpression != queryKeyExpression) {
                        ObjectLevelReadQuery nestedQuery = null;
                        if (joinManager.getJoinedMappingQueryClones() == null) {
                            if (joinManager.getJoinedMappingQueries_() != null) {
                                nestedQuery = joinManager.getJoinedMappingQueries_().get(mapping);
                            }
                        } else {
                            nestedQuery = joinManager.getJoinedMappingQueryClones().get(mapping);
                        }

                        //use the nestedQuery to trigger joins remaining for this expression (base onward)
                        if ((nestedQuery != null) && (nestedQuery.getJoinedAttributeManager() != null)) {
                            if (!mapping.isCollectionMapping()) {
                                triggerJoinExpressions(unitOfWork, nestedQuery.getJoinedAttributeManager(), attributeValue, null);
                            }else {
                                ContainerPolicy cp = ((CollectionMapping)mapping).getContainerPolicy();
                                Object iterator = cp.iteratorFor(attributeValue);
                                while (cp.hasNext(iterator)){
                                    triggerJoinExpressions(unitOfWork, nestedQuery.getJoinedAttributeManager(), cp.next(iterator, unitOfWork), null);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * INTERNAL:
     * When reading across relationships, queries may be set to acquire deferred locks
     * This is used to ensure any Eagerly fetched object that is the target of a relationship
     * with an object the acquires deferred locks behaves the same as its owner
     */
    public boolean requiresDeferredLocks() {
        return requiresDeferredLocks != null && requiresDeferredLocks.booleanValue();
    }

    /**
     * INTERNAL:
     * Set the the time this query went to the database.
     */
    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }

    /**
     * PUBLIC:
     * Sets whether this is a pessimistically locking query.
     * <ul>
     * <li>ObjectBuildingQuery.LOCK: SELECT .... FOR UPDATE issued.
     * <li>ObjectBuildingQuery.LOCK_NOWAIT: SELECT .... FOR UPDATE NO WAIT issued.
     * <li>ObjectBuildingQuery.NO_LOCK: no pessimistic locking.
     * <li>ObjectBuildingQuery.DEFAULT_LOCK_MODE (default) and you have a CMP descriptor:
     * fine grained locking will occur.
     * </ul>
     * <p>Fine Grained Locking: On execution the reference class
     * and those of all joined attributes will be checked.  If any of these have a
     * PessimisticLockingPolicy set on their descriptor, they will be locked in a
     * SELECT ... FOR UPDATE OF ... {NO WAIT}.  Issues fewer locks
     * and avoids setting the lock mode on each query.
     * <p>Example:<code>readAllQuery.setSelectionCriteria(employee.get("address").equal("Ottawa"));</code>
     * <ul><li>LOCK: all employees in Ottawa and all referenced Ottawa addresses will be locked.
     * <li>DEFAULT_LOCK_MODE: if address is a joined attribute, and only address has a pessimistic
     * locking policy, only referenced Ottawa addresses will be locked.
     * </ul>
     * @see org.eclipse.persistence.descriptors.PessimisticLockingPolicy
     */
    public void setLockMode(short lockMode) {
        lockingClause = ForUpdateClause.newInstance(lockMode);
    }

    /**
     * REQUIRED:
     * Set the reference class for the query.
     */
    public void setReferenceClass(Class aClass) {
        referenceClass = aClass;
        setIsPrepared(false);
    }

    /**
     * INTERNAL:
     * Set the reference class for the query.
     */
    public void setReferenceClassName(String aClass) {
        referenceClassName = aClass;
        setIsPrepared(false);
    }

    /**
     * INTERNAL:
     * When reading across relationships, queries may be set to acquire deferred locks
     * This is used to ensure any Eagerly fetched object that is the target of a relationship
     * with an object the acquires deferred locks behaves the same as its owner
     */
    public void setRequiresDeferredLocks(boolean cascadeDeferredLocks) {
        this.requiresDeferredLocks = Boolean.valueOf(cascadeDeferredLocks);
    }

    /**
     * PUBLIC:
     * Set if the attributes of the object(s) resulting from the query should be refreshed.
     * If cascading is used the private parts of the objects will also be refreshed.
     */
    public void setShouldRefreshIdentityMapResult(boolean shouldRefreshIdentityMapResult) {
        this.shouldRefreshIdentityMapResult = shouldRefreshIdentityMapResult;
        if (shouldRefreshIdentityMapResult) {
            setShouldRefreshRemoteIdentityMapResult(true);
        }
    }

    /**
     * PUBLIC:
     * Set if the attributes of the object(s) resulting from the query should be refreshed.
     * If cascading is used the private parts of the objects will also be refreshed.
     */
    public void setShouldRefreshRemoteIdentityMapResult(boolean shouldRefreshIdentityMapResult) {
        this.shouldRefreshRemoteIdentityMapResult = shouldRefreshIdentityMapResult;
    }

    /**
     * INTERNAL:
     * Set to false to have queries conform to a UnitOfWork without registering
     * any additional objects not already in that UnitOfWork.
     * @see #shouldRegisterResultsInUnitOfWork
     * @bug 2612601
     */
    public void setShouldRegisterResultsInUnitOfWork(boolean shouldRegisterResultsInUnitOfWork) {
        this.shouldRegisterResultsInUnitOfWork = shouldRegisterResultsInUnitOfWork;
    }

    /**
     * ADVANCED:
     * If the user has isolated data and specified that the client session should
     * use an exclusive connection then by setting this condition to true
     * EclipseLink will ensure that the query is executed through the exclusive
     * connection.  This may be required in certain cases.  An example being
     * where database security will prevent a query joining to a secure table
     * from returning the correct results when executed through the shared
     * connection.
     */
    public void setShouldUseExclusiveConnection(boolean shouldUseExclusiveConnection) {
        this.shouldUseExclusiveConnection = shouldUseExclusiveConnection;
    }

    /**
     * INTERNAL:
     * Allows one to do conforming in a UnitOfWork without registering.
     * Queries executed on a UnitOfWork will only return working copies for objects
     * that have already been registered.
     * <p>Extreme care should be taken in using this feature, for a user will
     * get back a mix of registered and original (unregistered) objects.
     * <p>Best used with a WrapperPolicy where invoking on an object will trigger
     * its registration (CMP).  Without a WrapperPolicy {@link org.eclipse.persistence.sessions.UnitOfWork#registerExistingObject registerExistingObject}
     * should be called on any object that you intend to change.
     * @return true by default.
     * @see #setShouldRegisterResultsInUnitOfWork
     * @bug 2612601
     */
    public boolean shouldRegisterResultsInUnitOfWork() {
        return shouldRegisterResultsInUnitOfWork;
    }

    /**
     * ADVANCED:
     * If the user has isolated data and specified that the client session should
     * use an exclusive connection then by setting this condition to true
     * EclipseLink will ensure that the query is executed through the exclusive
     * connection.  This may be required in certain cases.  An example being
     * where database security will prevent a query joining to a secure table
     * from returning the correct results when executed through the shared
     * connection.
     */
    public boolean shouldUseExclusiveConnection() {
        return this.shouldUseExclusiveConnection;
    }

    /**
     * INTERNAL:
     * Return if this is a full object query, not partial nor fetch group.
     */
    public boolean shouldReadAllMappings() {
        return true;
    }
    
    /**
     * INTERNAL:
     * Check if the mapping is part of the partial attributes.
     */
    public boolean shouldReadMapping(DatabaseMapping mapping, FetchGroup fetchGroup) {
        return true;
    }

    /**
     * PUBLIC:
     * Set to a boolean. When set means refresh the instance
     * variables of referenceObject from the database.
     */
    public boolean shouldRefreshIdentityMapResult() {
        return shouldRefreshIdentityMapResult;
    }

    /**
     * PUBLIC:
     * Set to a boolean. When set means refresh the instance
     * variables of referenceObject from the database.
     */
    public boolean shouldRefreshRemoteIdentityMapResult() {
        return shouldRefreshRemoteIdentityMapResult;
    }

    /**
     * INTERNAL:
     * Return if the attribute is specified for joining.
     */
    public boolean isAttributeJoined(ClassDescriptor mappingDescriptor, String attributeName) {
        return false;
    }

    /**
     * INTERNAL:
     * Returns true if an early return cache check was completed
     */
    public boolean isCacheCheckComplete(){
        return this.isCacheCheckComplete;
    }

    /**
    * INTERNAL:
    * Helper method that checks if clone has been locked with uow.
    */
    public boolean isClonePessimisticLocked(Object clone, UnitOfWorkImpl uow) {
        return false;
    }

    /**
     * INTERNAL:
     * Helper method that records clone with uow if query is pessimistic locking.
     */
    public void recordCloneForPessimisticLocking(Object clone, UnitOfWorkImpl uow) {
        if ((isLockQuery()) && lockingClause.isReferenceClassLocked()) {
            uow.addPessimisticLockedClone(clone);
        }
    }

    /**
    * INTERNAL: Helper method to determine the default mode. If true and quey has a pessimistic locking policy,
    * locking will be configured according to the pessimistic locking policy.
    */
    public boolean isDefaultLock() {
        return (lockingClause == null);
    }

    /**
     * INTERNAL:  
     * If primary key is null ObjectBuilder.buildObject returns null
     * in case this flag is set to true (instead of throwing exception).
     */
    public boolean shouldBuildNullForNullPk() {
        return shouldBuildNullForNullPk;
    }

    /**
     * INTERNAL:  
     * If primary key is null ObjectBuilder.buildObject returns null
     * in case this flag is set to true (instead of throwing exception).
     */
    public void setShouldBuildNullForNullPk(boolean shouldBuildNullForNullPk) {
        this.shouldBuildNullForNullPk = shouldBuildNullForNullPk;
    }
    /**
     * INTERNAL:
     * Return if the query uses ResultSet optimization.
     */
    public boolean usesResultSetAccessOptimization() {
        return false;
    }
}
