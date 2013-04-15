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
 *     08/23/2010-2.2 Michael O'Brien 
 *        - 323043: application.xml module ordering may cause weaving not to occur causing an NPE.
 *                       warn if expected "_persistence_*_vh" method not found
 *                       instead of throwing NPE during deploy validation.
 *     11/19/2012-2.5 Guy Pelletier 
 *       - 389090: JPA 2.1 DDL Generation Support (foreign key metadata support)
 *     12/07/2012-2.5 Guy Pelletier 
 *       - 389090: JPA 2.1 DDL Generation Support (foreign key metadata support)
 ******************************************************************************/  
package org.eclipse.persistence.mappings;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.eclipse.persistence.annotations.BatchFetchType;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.partitioning.PartitioningPolicy;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.history.AsOfClause;
import org.eclipse.persistence.indirection.ValueHolder;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.internal.descriptors.DescriptorIterator;
import org.eclipse.persistence.internal.descriptors.InstanceVariableAttributeAccessor;
import org.eclipse.persistence.internal.descriptors.MethodAttributeAccessor;
import org.eclipse.persistence.internal.expressions.ForUpdateOfClause;
import org.eclipse.persistence.internal.expressions.ObjectExpression;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.helper.NonSynchronizedSubVector;
import org.eclipse.persistence.internal.helper.NonSynchronizedVector;
import org.eclipse.persistence.internal.identitymaps.CacheId;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.indirection.BasicIndirectionPolicy;
import org.eclipse.persistence.internal.indirection.ContainerIndirectionPolicy;
import org.eclipse.persistence.internal.indirection.DatabaseValueHolder;
import org.eclipse.persistence.internal.indirection.IndirectionPolicy;
import org.eclipse.persistence.internal.indirection.NoIndirectionPolicy;
import org.eclipse.persistence.internal.indirection.WeavedObjectBasicIndirectionPolicy;
import org.eclipse.persistence.internal.queries.AttributeItem;
import org.eclipse.persistence.internal.queries.JoinedAttributeManager;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.ChangeRecord;
import org.eclipse.persistence.internal.sessions.MergeManager;
import org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.internal.sessions.remote.RemoteSessionController;
import org.eclipse.persistence.internal.sessions.remote.RemoteValueHolder;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.queries.BatchFetchPolicy;
import org.eclipse.persistence.queries.Call;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.FetchGroup;
import org.eclipse.persistence.queries.ObjectBuildingQuery;
import org.eclipse.persistence.queries.ObjectLevelModifyQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.queries.ReadQuery;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.sessions.remote.DistributedSession;

/**
 * <b>Purpose</b>: Abstract class for relationship mappings
 */
public abstract class ForeignReferenceMapping extends DatabaseMapping {
    
    /** Query parameter name used for IN batch ids. */
    public static final String QUERY_BATCH_PARAMETER = "query-batch-parameter";
    
    /** This is used only in descriptor proxy in remote session */
    protected Class referenceClass;
    protected String referenceClassName;

    /** The session is temporarily used for initialization. Once used, it is set to null */
    protected transient AbstractSession tempInitSession;

    /** The descriptor of the reference class. */
    protected transient ClassDescriptor referenceDescriptor;

    /** This query is used to read referenced objects for this mapping. */
    protected ReadQuery selectionQuery;

    /** Indicates whether the referenced object is privately owned or not. */
    protected boolean isPrivateOwned;

    /**
     * Indicates whether the referenced object should always be batch read on read all queries,
     * and defines the type of batch fetch to use.
     */
    protected BatchFetchType batchFetchType;

    /** Implements indirection behavior */
    protected IndirectionPolicy indirectionPolicy;

    /** Indicates whether the selection query is TopLink generated or defined by the user. */
    protected transient boolean hasCustomSelectionQuery;

    /** Used to reference the other half of a bi-directional relationship. */
    protected DatabaseMapping relationshipPartner;

    /** Set by users, used to retrieve the backpointer for this mapping */
    protected String relationshipPartnerAttributeName;

    /** Cascading flags used by the EntityManager */
    protected boolean cascadePersist;
    protected boolean cascadeMerge;
    protected boolean cascadeRefresh;
    protected boolean cascadeRemove;
    protected boolean cascadeDetach;
    
    /** Flag used to determine if we need to weave the transient annotation on weaved fields.*/
    protected boolean requiresTransientWeavedFields;
    
    /** Define if the relationship should always be join fetched. */
    protected int joinFetch = NONE;
    /** Specify any INNER join on a join fetch. */
    public static final int INNER_JOIN = 1;
    /** Specify any OUTER join on a join fetch. */
    public static final int OUTER_JOIN = 2;
    /** Specify no join fetch, this is the default. */
    public static final int NONE = 0;

    /** This is a way (after cloning) to force the initialization of the selection criteria */
    protected boolean forceInitializationOfSelectionCriteria;
    
    /**
     * Indicates whether and how pessimistic lock scope should be extended 
     */
    enum ExtendPessimisticLockScope {
        // should not be extended.
        NONE,
        // should be extended in mapping's selectQuery.
        TARGET_QUERY,
        // should be extended in the source query.
        SOURCE_QUERY,
        // should be extended in a dedicated query (which doesn't do anything else).
        DEDICATED_QUERY
    }
    ExtendPessimisticLockScope extendPessimisticLockScope;

    /** Support delete cascading on the database relationship constraint. */
    protected boolean isCascadeOnDeleteSetOnDatabase;

    /** Allow the mapping's queries to be targeted at specific connection pools. */
    protected PartitioningPolicy partitioningPolicy;
    
    /** Allow the mapping's queries to be targeted at specific connection pools. */
    protected String partitioningPolicyName;
    
    /** Stores JPA metadata about whether another mapping is the owning mapping.  Only populated for JPA models **/
    protected String mappedBy;
    
    protected ForeignReferenceMapping() {
        this.isPrivateOwned = false;
        this.hasCustomSelectionQuery = false;
        this.useBasicIndirection();
        this.cascadePersist = false;
        this.cascadeMerge = false;
        this.cascadeRefresh = false;
        this.cascadeRemove = false;
        this.requiresTransientWeavedFields = true;
        this.forceInitializationOfSelectionCriteria = false;
        this.extendPessimisticLockScope = ExtendPessimisticLockScope.NONE;
    }
   
    /**
     * ADVANCED: Allows the retrieval of the owning mapping for a particular
     * mapping. Note: This will only be set for JPA models
     * 
     * @return
     */
    public String getMappedBy() {
        return mappedBy;
    }

    /**
     * PUBLIC:
     * Return the mapping's partitioning policy.
     */
    public PartitioningPolicy getPartitioningPolicy() {
        return partitioningPolicy;
    }
    
    /**
     * PUBLIC:
     * Set the mapping's partitioning policy.
     * A PartitioningPolicy is used to partition, load-balance or replicate data across multiple difference databases
     * or across a database cluster such as Oracle RAC.
     * Partitioning can provide improved scalability by allowing multiple database machines to service requests.
     * Setting a policy on a mapping will set the policy on all of its mappings.
     */
    public void setPartitioningPolicy(PartitioningPolicy partitioningPolicy) {
        this.partitioningPolicy = partitioningPolicy;
    }
    
    /**
     * PUBLIC:
     * Return the name of the mapping's partitioning policy.
     * A PartitioningPolicy with the same name must be defined on the Project.
     * A PartitioningPolicy is used to partition the data for a class across multiple difference databases
     * or across a database cluster such as Oracle RAC.
     * Partitioning can provide improved scalability by allowing multiple database machines to service requests.
     */
    public String getPartitioningPolicyName() {
        return partitioningPolicyName;
    }
    
    /**
     * PUBLIC:
     * Set the name of the mapping's partitioning policy.
     * A PartitioningPolicy with the same name must be defined on the Project.
     * A PartitioningPolicy is used to partition the data for a class across multiple difference databases
     * or across a database cluster such as Oracle RAC.
     * Partitioning can provide improved scalability by allowing multiple database machines to service requests.
     */
    public void setPartitioningPolicyName(String partitioningPolicyName) {
        this.partitioningPolicyName = partitioningPolicyName;
    }

    /**
     * INTERNAL:
     * Retrieve the value through using batch reading.
     * This executes a single query to read the target for all of the objects and stores the
     * result of the batch query in the original query to allow the other objects to share the results.
     */
    protected Object batchedValueFromRow(AbstractRecord row, ObjectLevelReadQuery query, CacheKey parentCacheKey) {
        ReadQuery batchQuery = (ReadQuery)query.getProperty(this);
        if (batchQuery == null) {
            if (query.hasBatchReadAttributes()) {
                Map<DatabaseMapping, ReadQuery> queries = query.getBatchFetchPolicy().getMappingQueries();
                if (queries != null) {
                    batchQuery = queries.get(this);
                }
            }
            if (batchQuery == null) {
                batchQuery = prepareNestedBatchQuery(query);
                batchQuery.setIsExecutionClone(true);
            } else {
                batchQuery = (ReadQuery)batchQuery.clone();
                batchQuery.setIsExecutionClone(true);
            }
            query.setProperty(this, batchQuery);
        }
        return this.indirectionPolicy.valueFromBatchQuery(batchQuery, row, query, parentCacheKey);
    }

    /**
     * INTERNAL:
     * Clone the attribute from the clone and assign it to the backup.
     */
    @Override
    public void buildBackupClone(Object clone, Object backup, UnitOfWorkImpl unitOfWork) {
        Object attributeValue = getAttributeValueFromObject(clone);
        Object clonedAttributeValue = this.indirectionPolicy.backupCloneAttribute(attributeValue, clone, backup, unitOfWork);
        setAttributeValueInObject(backup, clonedAttributeValue);
    }

    /**
     * INTERNAL:
     * Used during building the backup shallow copy to copy the
     * target object without re-registering it.
     */
    @Override
    public abstract Object buildBackupCloneForPartObject(Object attributeValue, Object clone, Object backup, UnitOfWorkImpl unitOfWork);

    /**
     * INTERNAL:
     * Clone the attribute from the original and assign it to the clone.
     */
    @Override
    public void buildClone(Object original, CacheKey cacheKey, Object clone, Integer refreshCascade, AbstractSession cloningSession) {
        Object attributeValue = null;
        if (!this.isCacheable && (cacheKey != null && !cacheKey.isIsolated())){
            ReadObjectQuery query = new ReadObjectQuery(descriptor.getJavaClass());
            query.setSession(cloningSession);
            attributeValue = valueFromRow(cacheKey.getProtectedForeignKeys(), null, query, cacheKey, cloningSession, true, null);
        }else{
            attributeValue = getAttributeValueFromObject(original);
        }
        attributeValue = this.indirectionPolicy.cloneAttribute(attributeValue, original, cacheKey, clone, refreshCascade, cloningSession, false); // building clone from an original not a row.
        //GFBug#404 - fix moved to ObjectBuildingQuery.registerIndividualResult 
        setAttributeValueInObject(clone, attributeValue);
    }

    /**
     * INTERNAL:
     * A combination of readFromRowIntoObject and buildClone.
     * <p>
     * buildClone assumes the attribute value exists on the original and can
     * simply be copied.
     * <p>
     * readFromRowIntoObject assumes that one is building an original.
     * <p>
     * Both of the above assumptions are false in this method, and actually
     * attempts to do both at the same time.
     * <p>
     * Extract value from the row and set the attribute to this value in the
     * working copy clone.
     * In order to bypass the shared cache when in transaction a UnitOfWork must
     * be able to populate working copies directly from the row.
     */
    @Override
    public void buildCloneFromRow(AbstractRecord databaseRow, JoinedAttributeManager joinManager, Object clone, CacheKey sharedCacheKey, ObjectBuildingQuery sourceQuery, UnitOfWorkImpl unitOfWork, AbstractSession executionSession) {
        Boolean[] wasCacheUsed = new Boolean[]{Boolean.FALSE};
        Object attributeValue = valueFromRow(databaseRow, joinManager, sourceQuery, sharedCacheKey, executionSession, true, wasCacheUsed);
        Object clonedAttributeValue = this.indirectionPolicy.cloneAttribute(attributeValue, null, sharedCacheKey,clone, null, unitOfWork, !wasCacheUsed[0]);// building clone directly from row.
        if (executionSession.isUnitOfWork() && sourceQuery.shouldRefreshIdentityMapResult()){
            // check whether the attribute is fully build before calling getAttributeValueFromObject because that
            // call may fully build the attribute
            boolean wasAttributeValueFullyBuilt = isAttributeValueFullyBuilt(clone);
            Object oldAttribute = this.getAttributeValueFromObject(clone);
            setAttributeValueInObject(clone, clonedAttributeValue); // set this first to prevent infinite recursion
             if (wasAttributeValueFullyBuilt && this.indirectionPolicy.objectIsInstantiatedOrChanged(oldAttribute)){
                this.indirectionPolicy.instantiateObject(clone, clonedAttributeValue);
            }
        }else{
            setAttributeValueInObject(clone, clonedAttributeValue);
        }
        if((joinManager != null && joinManager.isAttributeJoined(this.descriptor, this)) || (isExtendingPessimisticLockScope(sourceQuery) && extendPessimisticLockScope == ExtendPessimisticLockScope.TARGET_QUERY)) {
            // need to instantiate to extended the lock beyond the source object table(s).
            this.indirectionPolicy.instantiateObject(clone, clonedAttributeValue);
        }
    }

    /**
     * INTERNAL:
     * Require for cloning, the part must be cloned.
     */
    public abstract Object buildCloneForPartObject(Object attributeValue, Object original, CacheKey cacheKey, Object clone, AbstractSession cloningSession, Integer refreshCascade, boolean isExisting, boolean isFromSharedCache);

    /**
     * INTERNAL:
     * The mapping clones itself to create deep copy.
     */
    @Override
    public Object clone() {
        ForeignReferenceMapping clone = (ForeignReferenceMapping)super.clone();

        clone.setIndirectionPolicy((IndirectionPolicy)indirectionPolicy.clone());
        clone.setSelectionQuery((ReadQuery)getSelectionQuery().clone());

        return clone;
    }

    /**
     * INTERNAL:
     * This method will access the target relationship and create a list of information to rebuild the relationship.
     * This method is used in combination with the CachedValueHolder to store references to PK's to be loaded from
     * a cache instead of a query.
     */
    public abstract Object[] buildReferencesPKList(Object entity, Object attribute, AbstractSession session);

    /**
     * INTERNAL: Compare the attributes belonging to this mapping for the
     * objects.
     */
    @Override
    public boolean compareObjects(Object firstObject, Object secondObject, AbstractSession session) {
        if (isPrivateOwned()) {
            return compareObjectsWithPrivateOwned(firstObject, secondObject, session);
        } else {
            return compareObjectsWithoutPrivateOwned(firstObject, secondObject, session);
        }
    }

    /**
     * Compare two objects if their parts are not private owned
     */
    protected abstract boolean compareObjectsWithoutPrivateOwned(Object first, Object second, AbstractSession session);

    /**
     * Compare two objects if their parts are private owned
     */
    protected abstract boolean compareObjectsWithPrivateOwned(Object first, Object second, AbstractSession session);

    /**
     * INTERNAL:
     * Convert all the class-name-based settings in this mapping to actual class-based
     * settings. This method is used when converting a project that has been built
     * with class names to a project with classes.
     */
    @Override
    public void convertClassNamesToClasses(ClassLoader classLoader){
        super.convertClassNamesToClasses(classLoader);

        // DirectCollection mappings don't require a reference class.
        if (getReferenceClassName() != null) {
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
        }
        if (getSelectionQuery() != null) {
            getSelectionQuery().convertClassNamesToClasses(classLoader);
        }
    }

    /**
     * INTERNAL:
     * Builder the unit of work value holder.
     * Ignore the original object.
     * @param buildDirectlyFromRow indicates that we are building the clone directly
     * from a row as opposed to building the original from the row, putting it in
     * the shared cache, and then cloning the original.
     */
    public DatabaseValueHolder createCloneValueHolder(ValueHolderInterface attributeValue, Object original, Object clone, AbstractRecord row, AbstractSession cloningSession, boolean buildDirectlyFromRow) {
        return cloningSession.createCloneQueryValueHolder(attributeValue, clone, row, this);
    }

    /**
     * INTERNAL:
     * Return true if the merge should be bypassed. This would be the case for several reasons, depending on
     * the kind of merge taking place.
     */
    protected boolean dontDoMerge(Object target, Object source, MergeManager mergeManager) {
        if (!shouldMergeCascadeReference(mergeManager)) {
            return true;
        }
        if (mergeManager.isForRefresh()) {
            // For reverts we are more concerned about the target than the source.
            if (!isAttributeValueInstantiated(target)) {
                return true;
            }
        } else {
            if (mergeManager.shouldRefreshRemoteObject() && shouldMergeCascadeParts(mergeManager) && usesIndirection()) {
                return true;
            } else {
                if (!isAttributeValueInstantiated(source)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * PUBLIC:
     * Indicates whether the referenced object should always be batch read on read all queries.
     * Batch reading will read all of the related objects in a single query when accessed from an originating read all.
     * This should only be used if it is know that the related objects are always required with the source object, or indirection is not used.
     */
    public void dontUseBatchReading() {
        setUsesBatchReading(false);
    }

    /**
     * PUBLIC:
     * Indirection means that a ValueHolder will be put in-between the attribute and the real object.
     * This allows for the reading of the target from the database to be delayed until accessed.
     * This defaults to true and is strongly suggested as it give a huge performance gain.
     */
    public void dontUseIndirection() {
        setIndirectionPolicy(new NoIndirectionPolicy());
    }

    /**
     * INTERNAL:
     * Called if shouldExtendPessimisticLockScopeInTargetQuery() is true.
     * Adds locking clause to the target query to extend pessimistic lock scope.
     */
    protected void extendPessimisticLockScopeInTargetQuery(ObjectLevelReadQuery targetQuery, ObjectBuildingQuery sourceQuery) {
        targetQuery.setLockMode(sourceQuery.getLockMode());
    }
    
    /**
     * INTERNAL:
     * Called if shouldExtendPessimisticLockScopeInSourceQuery is true.
     * Adds fields to be locked to the where clause of the source query.
     * Note that the sourceQuery must be ObjectLevelReadQuery so that it has ExpressionBuilder.
     * 
     * This method must be implemented in subclasses that allow
     * setting shouldExtendPessimisticLockScopeInSourceQuery to true.
     */
    public void extendPessimisticLockScopeInSourceQuery(ObjectLevelReadQuery sourceQuery) {
    }
    
    /**
     * INTERNAL:
     * Extract the value from the batch optimized query, this should be supported by most query types.
     */
    public Object extractResultFromBatchQuery(ReadQuery batchQuery, CacheKey parentCacheKey, AbstractRecord sourceRow, AbstractSession session, ObjectLevelReadQuery originalQuery) throws QueryException {
        Map<Object, Object> batchedObjects = null;
        Object result = null;
        Object sourceKey = extractBatchKeyFromRow(sourceRow, session);
        if (sourceKey == null) {
            // If the foreign key was null, then just return null.
            return null;
        }
        Object cachedObject = checkCacheForBatchKey(sourceRow, sourceKey, batchedObjects, batchQuery, originalQuery, session);
        if (cachedObject != null) {
            // If the object is already in the cache, then just return it.
            return cachedObject;
        }
        // Ensure the query is only executed once.
        synchronized (batchQuery) {
            // Check if query was already executed.
            batchedObjects = batchQuery.getBatchObjects();
            BatchFetchPolicy originalPolicy = originalQuery.getBatchFetchPolicy();
            if (batchedObjects == null) {
                batchedObjects = new Hashtable();
                batchQuery.setBatchObjects(batchedObjects);
            } else {
                result = batchedObjects.get(sourceKey);
                if (result == Helper.NULL_VALUE) {
                    return null;
                // If IN may not have that batch yet, or it may have been null.
                } else if ((result != null) || (!originalPolicy.isIN())) {
                    return result;
                }
            }
            // In case of IN the batch including this row may not have been executed yet.
            if (result == null) {
                AbstractRecord translationRow = originalQuery.getTranslationRow();
                // Execute query and index resulting object sets by key.
                if (originalPolicy.isIN()) {
                    // Need to extract all foreign key values from all parent rows for IN parameter.
                    List<AbstractRecord> parentRows = originalPolicy.getDataResults(this);
                    // Execute queries by batch if too many rows.
                    int rowsSize = parentRows.size();
                    int size = Math.min(rowsSize, originalPolicy.getSize());
                    if (size == 0) {
                        return null;
                    }
                    int startIndex = 0;
                    if (size != rowsSize) {
                        // If only fetching a page, need to make sure the row we want is in the page.
                        startIndex = parentRows.indexOf(sourceRow);
                    }
                    List foreignKeyValues = new ArrayList(size);
                    Set foreignKeys = new HashSet(size);
                    int index = 0;
                    int offset = startIndex;
                    for (int count = 0; count < size; count++) {
                        if (index >= rowsSize) {
                            // Processed all rows, done.
                            break;
                        } else if ((offset + index) >= rowsSize) {
                            // If passed the end, go back to start.
                            offset = index * -1;
                        }
                        AbstractRecord row = parentRows.get(offset + index);
                        // Handle duplicate rows in the ComplexQueryResult being replaced with null, as a
                        // result of duplicate filtering being true for constructing the ComplexQueryResult
                        if (row != null) {
                            Object foreignKey = extractBatchKeyFromRow(row, session);
                            if (foreignKey == null) {
                                // Ignore null foreign keys.
                                count--;
                            } else {
                                cachedObject = checkCacheForBatchKey(row, foreignKey, batchedObjects, batchQuery, originalQuery, session);
                                if (cachedObject != null) {
                                    // Avoid fetching things a cache hit occurs for.
                                    count--;                                
                                } else {
                                    // Ensure the same id is not selected twice.
                                    if (foreignKeys.contains(foreignKey)) {
                                        count--;
                                    } else {
                                        Object[] key = ((CacheId)foreignKey).getPrimaryKey();
                                        Object foreignKeyValue = key[0];
                                        // Support composite keys using nested IN.
                                        if (key.length > 1) {
                                            foreignKeyValue = Arrays.asList(key);
                                        }
                                        foreignKeyValues.add(foreignKeyValue);
                                        foreignKeys.add(foreignKey);
                                    }
                                }
                            }
                        }
                        index++;
                    }
                    // Need to compute remaining rows, this is tricky because a page in the middle could have been processed.
                    List<AbstractRecord> remainingParentRows = null;
                    if (startIndex == 0) {
                        // Tail
                        remainingParentRows = new ArrayList(parentRows.subList(index, rowsSize));
                    } else if (startIndex == offset) {
                        // Head and tail.
                        remainingParentRows = new ArrayList(parentRows.subList(0, startIndex - 1));
                        remainingParentRows.addAll(parentRows.subList(index, rowsSize));
                    } else {
                        // Middle
                        // Check if empty,
                        if ((offset + index) >= (startIndex - 1)) {
                            remainingParentRows = new ArrayList(0);
                        } else {
                            remainingParentRows = new ArrayList(parentRows.subList(offset + index, startIndex - 1));
                        }
                    }
                    originalPolicy.setDataResults(this, remainingParentRows);
                    translationRow = translationRow.clone();
                    translationRow.put(QUERY_BATCH_PARAMETER, foreignKeyValues);
                    // Register each id as null, in case it has no relationship.
                    for (Object foreignKey : foreignKeys) {
                        batchedObjects.put(foreignKey, Helper.NULL_VALUE);
                    }
                } else if (batchQuery.isReadAllQuery() && ((ReadAllQuery)batchQuery).getBatchFetchPolicy().isIN()) {
                    throw QueryException.originalQueryMustUseBatchIN(this, originalQuery);
                }
                executeBatchQuery(batchQuery, parentCacheKey, batchedObjects, session, translationRow);
                batchQuery.setSession(null);
            }
        }
        result = batchedObjects.get(sourceKey);
        if (result == Helper.NULL_VALUE) {
            return null;
        } else {
            return result;
        }
    }

    /**
     * INTERNAL:
     * Extract the batch key value from the source row.
     * Used for batch reading, most following same order and fields as in the mapping.
     * The method should be overridden by classes that support batch reading.
     */
    protected Object extractBatchKeyFromRow(AbstractRecord targetRow, AbstractSession session) {
        throw QueryException.batchReadingNotSupported(this, null);
    }

    /**
     * INTERNAL: 
     * This method is used to store the FK fields that can be cached that correspond to noncacheable mappings
     * the FK field values will be used to re-issue the query when cloning the shared cache entity
     */
    public abstract void collectQueryParameters(Set<DatabaseField> cacheFields);
    
    /**
     * INTERNAL:
     * Check if the target object is in the cache if possible based on the source row.
     * If in the cache, add the object to the batch results.
     * Return null if not possible or not in the cache.
     */
    protected Object checkCacheForBatchKey(AbstractRecord sourceRow, Object foreignKey, Map batchObjects, ReadQuery batchQuery, ObjectLevelReadQuery originalQuery, AbstractSession session) {
        return null;
    }
    
    /**
     * INTERNAL:
     * Prepare and execute the batch query and store the
     * results for each source object in a map keyed by the
     * mappings source keys of the source objects.
     */
    protected void executeBatchQuery(DatabaseQuery query, CacheKey parentCacheKey, Map referenceObjectsByKey, AbstractSession session, AbstractRecord row) {
        throw QueryException.batchReadingNotSupported(this, query);
    }

    /**
     * INTERNAL:
     * Clone and prepare the JoinedAttributeManager nested JoinedAttributeManager.
     * This is used for nested joining as the JoinedAttributeManager passed to the joined build object.
     */
    public ObjectLevelReadQuery prepareNestedJoins(JoinedAttributeManager joinManager, ObjectBuildingQuery baseQuery, AbstractSession session) {
        // A nested query must be built to pass to the descriptor that looks like the real query execution would.
        ObjectLevelReadQuery nestedQuery = (ObjectLevelReadQuery)((ObjectLevelReadQuery)getSelectionQuery()).deepClone();
        nestedQuery.setSession(session); 
        // Must cascade for nested partial/join attributes, the expressions must be filter to only the nested ones.
        if (baseQuery.hasPartialAttributeExpressions()) {
            nestedQuery.setPartialAttributeExpressions(extractNestedExpressions(((ObjectLevelReadQuery)baseQuery).getPartialAttributeExpressions(), nestedQuery.getExpressionBuilder(), false));
            // bug 5501751: USING GETALLOWINGNULL() WITH ADDPARTIALATTRIBUTE() BROKEN IN 10.1.3
            // The query against Employee with 
            //   query.addPartialAttribute(builder.getAllowingNull("address"));
            // in case there's no address returns null instead of Address object.
            // Note that in case 
            //   query.addPartialAttribute(builder.getAllowingNull("address").get("city"));
            // in case there's no address an empty Address object (all atributes are nulls) is returned.
            if(nestedQuery.getPartialAttributeExpressions().isEmpty()) {
                if(hasRootExpressionThatShouldUseOuterJoin(((ObjectLevelReadQuery)baseQuery).getPartialAttributeExpressions())) {
                    nestedQuery.setShouldBuildNullForNullPk(true);
                }
            }
        } else {
            if(nestedQuery.getDescriptor().hasFetchGroupManager()) {
                FetchGroup sourceFG = baseQuery.getExecutionFetchGroup();
                if (sourceFG != null) {                    
                    FetchGroup targetFetchGroup = sourceFG.getGroup(getAttributeName());
                    if (targetFetchGroup != null) {
                        nestedQuery.setFetchGroup(targetFetchGroup);
                        nestedQuery.prepareFetchGroup();
                    }
                }
            }
            
            List nestedJoins = extractNestedExpressions(joinManager.getJoinedAttributeExpressions(), nestedQuery.getExpressionBuilder(), false);
            if (nestedJoins.size() > 0) {
                // Recompute the joined indexes based on the nested join expressions.
                nestedQuery.getJoinedAttributeManager().clear();
                nestedQuery.getJoinedAttributeManager().setJoinedAttributeExpressions_(nestedJoins);
                // the next line sets isToManyJoinQuery flag
                nestedQuery.getJoinedAttributeManager().prepareJoinExpressions(session);
                nestedQuery.getJoinedAttributeManager().computeJoiningMappingQueries(session);
                nestedQuery.getJoinedAttributeManager().computeJoiningMappingIndexes(true, session, 0);
            } else if (nestedQuery.hasJoining()) {
                // Clear any mapping level joins.
                nestedQuery.setJoinedAttributeManager(null);
            }
            // Configure nested locking clause.
            if (baseQuery.isLockQuery()) {
                if (((ObjectLevelReadQuery)baseQuery).getLockingClause().isForUpdateOfClause()) {
                    ForUpdateOfClause clause = (ForUpdateOfClause)((ObjectLevelReadQuery)baseQuery).getLockingClause().clone();
                    clause.setLockedExpressions(extractNestedExpressions(clause.getLockedExpressions(), nestedQuery.getExpressionBuilder(), true));
                    nestedQuery.setLockingClause(clause);
                } else {
                    nestedQuery.setLockingClause(((ObjectLevelReadQuery)baseQuery).getLockingClause());
                }
            }
        }
        nestedQuery.setShouldMaintainCache(baseQuery.shouldMaintainCache());
        nestedQuery.setShouldRefreshIdentityMapResult(baseQuery.shouldRefreshIdentityMapResult());

        // For flashback: Must still propagate all properties, as the
        // attributes of this joined attribute may be read later too.
        if (baseQuery.isObjectLevelReadQuery() && ((ObjectLevelReadQuery)baseQuery).hasAsOfClause()) {
            nestedQuery.setAsOfClause(((ObjectLevelReadQuery)baseQuery).getAsOfClause());
        }
        nestedQuery.setCascadePolicy(baseQuery.getCascadePolicy());
        if (nestedQuery.hasJoining()) {
            nestedQuery.getJoinedAttributeManager().computeJoiningMappingQueries(session);
        }
        nestedQuery.setSession(null);
        nestedQuery.setRequiresDeferredLocks(baseQuery.requiresDeferredLocks());

        return nestedQuery;
    }
    
    /**
     * INTERNAL:
     * Allow the mapping the do any further batch preparation.
     */
    protected void postPrepareNestedBatchQuery(ReadQuery batchQuery, ObjectLevelReadQuery query) {
        // Do nothing.
    }
    
    /**
     * INTERNAL:
     * Return the selection criteria used to IN batch fetching.
     */
    protected Expression buildBatchCriteria(ExpressionBuilder builder, ObjectLevelReadQuery query) {
        throw QueryException.batchReadingNotSupported(this, null);
    }
    
    /**
     * INTERNAL:
     * Clone and prepare the selection query as a nested batch read query.
     * This is used for nested batch reading.
     */
    public ReadQuery prepareNestedBatchQuery(ObjectLevelReadQuery query) {
        // For CR#2646-S.M.  In case of inheritance the descriptor to use may not be that
        // of the source query (the base class descriptor), but that of the subclass, if the
        // attribute is only of the subclass.  Thus in this case use the descriptor from the mapping.
        // Also: for Bug 5478648 - Do not switch the descriptor if the query's descriptor is an aggregate
        ClassDescriptor descriptorToUse = query.getDescriptor();
        if ((descriptorToUse != this.descriptor) && (!descriptorToUse.getMappings().contains(this)) && (!this.descriptor.isDescriptorTypeAggregate())) { 
            descriptorToUse = this.descriptor;
        }
        
        ExpressionBuilder builder = new ExpressionBuilder(this.referenceClass);
        builder.setQueryClassAndDescriptor(this.referenceClass, getReferenceDescriptor());
        ReadAllQuery batchQuery = new ReadAllQuery(this.referenceClass, builder);
        batchQuery.setName(getAttributeName());
        batchQuery.setDescriptor(getReferenceDescriptor());
        batchQuery.setSession(query.getSession());

        //bug 3965568 
        // we should not wrap the results as this is an internal query
        batchQuery.setShouldUseWrapperPolicy(false);
        if (query.shouldCascadeAllParts() || (query.shouldCascadePrivateParts() && isPrivateOwned()) || (query.shouldCascadeByMapping() && this.cascadeRefresh)) {
            batchQuery.setShouldRefreshIdentityMapResult(query.shouldRefreshIdentityMapResult());
            batchQuery.setCascadePolicy(query.getCascadePolicy());
            batchQuery.setShouldMaintainCache(query.shouldMaintainCache());
            if (query.hasAsOfClause()) {
                batchQuery.setAsOfClause(query.getAsOfClause());
            }

            //bug 3802197 - cascade binding and prepare settings
            batchQuery.setShouldBindAllParameters(query.getShouldBindAllParameters());
            batchQuery.setShouldPrepare(query.shouldPrepare());
        }
        batchQuery.setShouldOuterJoinSubclasses(query.shouldOuterJoinSubclasses());
        //CR #4365
        batchQuery.setQueryId(query.getQueryId());

        Expression batchSelectionCriteria = null;
        // Build the batch query, either using joining, or an exist sub-select.
        BatchFetchType batchType = query.getBatchFetchPolicy().getType();
        if (this.batchFetchType != null) {
            batchType = this.batchFetchType;
        }
        if (batchType == BatchFetchType.EXISTS) {
            // Using a EXISTS sub-select (WHERE EXIST (<original-query> AND <mapping-join> AND <mapping-join>)
            ExpressionBuilder subBuilder = new ExpressionBuilder(descriptorToUse.getJavaClass());
            subBuilder.setQueryClassAndDescriptor(descriptorToUse.getJavaClass(), descriptorToUse);
            ReportQuery subQuery = new ReportQuery(descriptorToUse.getJavaClass(), subBuilder);
            subQuery.setDescriptor(descriptorToUse);
            subQuery.setShouldRetrieveFirstPrimaryKey(true);
            Expression subCriteria = subBuilder.twist(getSelectionCriteria(), builder);
            if (query.getSelectionCriteria() != null) {
                // For bug 2612567, any query can have batch attributes, so the
                // original selection criteria can be quite complex, with multiple
                // builders (i.e. for parallel selects).
                // Now uses cloneUsing(newBase) instead of rebuildOn(newBase).
                subCriteria = query.getSelectionCriteria().cloneUsing(subBuilder).and(subCriteria);
            }
            // Check for history and set asOf. 
            if (descriptorToUse.getHistoryPolicy() != null) {
                if (query.getSession().getAsOfClause() != null) {
                    subBuilder.asOf(query.getSession().getAsOfClause());
                } else if (batchQuery.getAsOfClause() == null) {
                    subBuilder.asOf(AsOfClause.NO_CLAUSE);
                } else {
                    subBuilder.asOf(batchQuery.getAsOfClause());
                }
            }
            subQuery.setSelectionCriteria(subCriteria);
            batchSelectionCriteria = builder.exists(subQuery);
        } else if (batchType == BatchFetchType.IN) {
            // Using a IN with foreign key values (WHERE FK IN :QUERY_BATCH_PARAMETER)
            batchSelectionCriteria = buildBatchCriteria(builder, query);
        } else {
            // Using a join, (WHERE <orginal-query-criteria> AND <mapping-join>)
            // Join the query where clause with the mapping's,
            // this will cause a join that should bring in all of the target objects.
            Expression backRef = builder.getManualQueryKey(getAttributeName() + "-back-ref", descriptorToUse);
            batchSelectionCriteria = backRef.twist(getSelectionCriteria(), builder);
            if (query.getSelectionCriteria() != null) {
                // For bug 2612567, any query can have batch attributes, so the
                // original selection criteria can be quite complex, with multiple
                // builders (i.e. for parallel selects).
                // Now uses cloneUsing(newBase) instead of rebuildOn(newBase).
                batchSelectionCriteria = batchSelectionCriteria.and(query.getSelectionCriteria().cloneUsing(backRef));
            }
            // Since a manual query key expression does not really get normalized,
            // it must get its additional expressions added in here.  Probably best
            // to somehow keep all this code inside QueryKeyExpression.normalize.
            if (descriptorToUse.getQueryManager().getAdditionalJoinExpression() != null) {
                batchSelectionCriteria = batchSelectionCriteria.and(descriptorToUse.getQueryManager().getAdditionalJoinExpression().rebuildOn(backRef));
            }
            // Check for history and add history expression. 
            if (descriptorToUse.getHistoryPolicy() != null) {
                if (query.getSession().getAsOfClause() != null) {
                    backRef.asOf(query.getSession().getAsOfClause());
                } else if (batchQuery.getAsOfClause() == null) {
                    backRef.asOf(AsOfClause.NO_CLAUSE);
                } else {
                    backRef.asOf(batchQuery.getAsOfClause());
                }
                batchSelectionCriteria = batchSelectionCriteria.and(descriptorToUse.getHistoryPolicy().additionalHistoryExpression(backRef, backRef));
            }
        }
        batchQuery.setSelectionCriteria(batchSelectionCriteria);
        
        if (query.isDistinctComputed()) {
            // Only recompute if it has not already been set by the user
            batchQuery.setDistinctState(query.getDistinctState());
        }

        // Add batch reading attributes contained in the mapping's query.
        ReadQuery mappingQuery = this.selectionQuery;
        if (mappingQuery.isReadAllQuery()) {
            // CR#3238 clone these vectors so they will not grow with each call to the query. -TW
            batchQuery.setOrderByExpressions(new ArrayList<Expression>(((ReadAllQuery)mappingQuery).getOrderByExpressions()));
            if (((ReadAllQuery)mappingQuery).hasBatchReadAttributes()) {
                for (Expression expression : ((ReadAllQuery)mappingQuery).getBatchReadAttributeExpressions()) {
                    batchQuery.addBatchReadAttribute(expression);
                }
            }
        }

        // Bug 385700 - Populate session & query class if not initialized by 
        // ObjectLevelReadQuery.computeBatchReadMappingQueries() in case batch query
        // has been using inheritance and child descriptors can have different mappings.
        if (query.hasBatchReadAttributes()) {
            for (Expression expression : query.getBatchReadAttributeExpressions()) {
                ObjectExpression batchReadExpression = (ObjectExpression) expression;
                
                // Batch Read Attribute Expressions may not have initialized.
                ExpressionBuilder expressionBuilder = batchReadExpression.getBuilder();
                if (expressionBuilder.getQueryClass() == null) {
                    expressionBuilder.setSession(query.getSession().getRootSession(null));
                    expressionBuilder.setQueryClass(query.getReferenceClass());
                }
            }
            
            // Computed nested batch attribute expressions, and add them to batch query.
            List<Expression> nestedExpressions = extractNestedExpressions(query.getBatchReadAttributeExpressions(), batchQuery.getExpressionBuilder(), false);
            batchQuery.getBatchReadAttributeExpressions().addAll(nestedExpressions);
        }

        batchQuery.setBatchFetchType(batchType);
        batchQuery.setBatchFetchSize(query.getBatchFetchPolicy().getSize());
        // Allow subclasses to further prepare.
        postPrepareNestedBatchQuery(batchQuery, query);
        
        // Set nested fetch group.
        if (batchQuery.getDescriptor().hasFetchGroupManager()) {
            FetchGroup sourceFetchGruop = query.getExecutionFetchGroup();
            if (sourceFetchGruop != null) {                    
                FetchGroup targetFetchGroup = sourceFetchGruop.getGroup(getAttributeName());
                if (targetFetchGroup != null) {
                    ((ObjectLevelReadQuery)batchQuery).setFetchGroup(targetFetchGroup);
                }
            }
        }
        
        if (batchQuery.shouldPrepare()) {
            batchQuery.checkPrepare(query.getSession(), query.getTranslationRow());
        }
        batchQuery.setSession(null);

        return batchQuery;
    }

    /**
     * INTERNAL:
     * An object has been serialized from the server to the client.
     * Replace the transient attributes of the remote value holders
     * with client-side objects.
     */
    @Override
    public void fixObjectReferences(Object object, Map objectDescriptors, Map processedObjects, ObjectLevelReadQuery query, DistributedSession session) {
        this.indirectionPolicy.fixObjectReferences(object, objectDescriptors, processedObjects, query, session);
    }

    /**
     * INTERNAL:
     * Return the value of an attribute which this mapping represents for an object.
     */
    @Override
    public Object getAttributeValueFromObject(Object object) throws DescriptorException {
        Object attributeValue = super.getAttributeValueFromObject(object);
        Object indirectionValue = this.indirectionPolicy.validateAttributeOfInstantiatedObject(attributeValue);

        // PERF: Allow the indirection policy to initialize null attribute values,
        // this allows the indirection objects to not be initialized in the constructor.
        if (indirectionValue != attributeValue) {
            setAttributeValueInObject(object, indirectionValue);
            attributeValue = indirectionValue;
        }
        return attributeValue;
    }
    
    /**
     * INTERNAL:
     * Returns the attribute value from the reference object.
     * If the attribute is using indirection the value of the value-holder is returned.
     * If the value holder is not instantiated then it is instantiated.
     */
    public Object getAttributeValueWithClonedValueHolders(Object object) {
        Object attributeValue = getAttributeValueFromObject(object);
        if (attributeValue instanceof DatabaseValueHolder){
            return ((DatabaseValueHolder)attributeValue).clone();
        } else if (attributeValue instanceof ValueHolder){
            return ((ValueHolder)attributeValue).clone(); 
        }
        return attributeValue;
    }
    
    /**
     * INTERNAL:
     * Return source key fields for translation by an AggregateObjectMapping
     * By default, return an empty NonSynchronizedVector
     */
    public Collection getFieldsForTranslationInAggregate() {
        return new NonSynchronizedVector(0);
    }
    
    /**
     * INTERNAL:
     * Should be overridden by subclass that allows setting
     * extendPessimisticLockScope to DEDICATED_QUERY. 
     */
    protected ReadQuery getExtendPessimisticLockScopeDedicatedQuery(AbstractSession session, short lockMode) {
        return null;
    }
    
    /**
     * INTERNAL:
     * Return the mapping's indirection policy.
     */
    public IndirectionPolicy getIndirectionPolicy() {
        return indirectionPolicy;
    }

    /**
     * INTERNAL:
     * Return whether the specified object is instantiated.
     */
    @Override
    public boolean isAttributeValueFromObjectInstantiated(Object object) {
        return this.indirectionPolicy.objectIsInstantiated(getAttributeValueFromObject(object));
    }
    
    /**
     * INTERNAL:
     * Returns the join criteria stored in the mapping selection query. This criteria
     * is used to read reference objects across the tables from the database.
     */
    public Expression getJoinCriteria(ObjectExpression context, Expression base) {
        Expression selectionCriteria = getSelectionCriteria();
        return context.getBaseExpression().twist(selectionCriteria, base);
    }

    /**
     * INTERNAL:
     * return the object on the client corresponding to the specified object.
     * ForeignReferenceMappings have to worry about
     * maintaining object identity.
     */
    @Override
    public Object getObjectCorrespondingTo(Object object, DistributedSession session, Map objectDescriptors, Map processedObjects, ObjectLevelReadQuery query) {
        return session.getObjectCorrespondingTo(object, objectDescriptors, processedObjects, query);
    }

    /**
     * INTERNAL:
     * Returns the attribute value from the reference object.
     * If the attribute is using indirection the value of the value-holder is returned.
     * If the value holder is not instantiated then it is instantiated.
     */
    @Override
    public Object getRealAttributeValueFromAttribute(Object attributeValue, Object object, AbstractSession session) {
        return this.indirectionPolicy.getRealAttributeValueFromObject(object, attributeValue);
    }
        
    /**
     * Return if this mapping is lazy.
     * For relationship mappings this should normally be the same value as indirection,
     * however for eager relationships this can be used with indirection to allow
     * indirection locking and change tracking, but still always force instantiation.
     */
    @Override
    public boolean isLazy() {
        if (isLazy == null) {
            // False by default for mappings without indirection.
            isLazy = usesIndirection();
        }
        return isLazy;
    }

    /**
     * INTERNAL:
     * Return whether this mapping should be traversed when we are locking.
     */
    @Override
    public boolean isLockableMapping(){
        return !(this.usesIndirection()) && !referenceDescriptor.getCachePolicy().isIsolated();
    }
    
    /**
     * INTERNAL:
     * Trigger the instantiation of the attribute if lazy.
     */
    @Override
    public void instantiateAttribute(Object object, AbstractSession session) {
        this.indirectionPolicy.instantiateObject(object, getAttributeValueFromObject(object));
    }
    
    /**
     * PUBLIC:
     * Returns the reference class.
     */
    public Class getReferenceClass() {
        return referenceClass;
    }

    /**
     * INTERNAL:
     * Returns the reference class name.
     */
    public String getReferenceClassName() {
        if ((referenceClassName == null) && (referenceClass != null)) {
            referenceClassName = referenceClass.getName();
        }
        return referenceClassName;
    }

    /**
     * INTERNAL:
     * Return the referenceDescriptor. This is a descriptor which is associated with
     * the reference class.
     */
    public ClassDescriptor getReferenceDescriptor() {
        if (referenceDescriptor == null) {
            if (getTempSession() == null) {
                return null;
            } else {
                referenceDescriptor = getTempSession().getDescriptor(getReferenceClass());
            }
        }

        return referenceDescriptor;
    }

    /**
     * INTERNAL:
     * Return the relationshipPartner mapping for this bi-directional mapping. If the relationshipPartner is null then
     * this is a uni-directional mapping.
     */
    public DatabaseMapping getRelationshipPartner() {
        if ((this.relationshipPartner == null) && (this.relationshipPartnerAttributeName != null)) {
            setRelationshipPartner(getReferenceDescriptor().getObjectBuilder().getMappingForAttributeName(getRelationshipPartnerAttributeName()));
        }
        return this.relationshipPartner;
    }

    /**
     * PUBLIC:
     *  Use this method retrieve the relationship partner attribute name of this bidirectional Mapping.
     */
    public String getRelationshipPartnerAttributeName() {
        return this.relationshipPartnerAttributeName;
    }

    /**
     * INTERNAL:
     * Returns the selection criteria stored in the mapping selection query. This criteria
     * is used to read reference objects from the database.  It will return null before
     * initialization.  To obtain the selection criteria before initialization (e.g., in a 
     * customizer) you can use the buildSelectionCriteria() method defined by some subclasses.
     * 
     * @see org.eclipse.persistence.mappings.OneToOneMapping#buildSelectionCriteria()
     * @see org.eclipse.persistence.mappings.OneToManyMapping#buildSelectionCriteria()
     */
    public Expression getSelectionCriteria() {
        return getSelectionQuery().getSelectionCriteria();
    }

    /**
     * INTERNAL:
     * Returns the read query associated with the mapping.
     */
    public ReadQuery getSelectionQuery() {
        return selectionQuery;
    }

    protected AbstractSession getTempSession() {
        return tempInitSession;
    }

    /**
     * INTERNAL:
     * Extract and return the appropriate value from the
     * specified remote value holder.
     */
    @Override
    public Object getValueFromRemoteValueHolder(RemoteValueHolder remoteValueHolder) {
        return this.indirectionPolicy.getValueFromRemoteValueHolder(remoteValueHolder);
    }

    /**
     * INTERNAL:
     * Indicates whether the selection query is TopLink generated or defined by 
     * the user.
     */
    public boolean hasCustomSelectionQuery() {
        return hasCustomSelectionQuery;
    }

    /**
     * INTERNAL:
     * Initialize the state of mapping.
     */
    @Override
    public void preInitialize(AbstractSession session) throws DescriptorException {
        super.preInitialize(session);
        // If weaving was used the mapping must be configured to use the weaved get/set methods.
        if ((this.indirectionPolicy instanceof BasicIndirectionPolicy) && ClassConstants.PersistenceWeavedLazy_Class.isAssignableFrom(getDescriptor().getJavaClass())) {
            Class attributeType = getAttributeAccessor().getAttributeClass();
            // Check that not already weaved or coded.
            if (!(ClassConstants.ValueHolderInterface_Class.isAssignableFrom(attributeType))) {
                if (!indirectionPolicy.isWeavedObjectBasicIndirectionPolicy()){
                    if(getAttributeAccessor().isMethodAttributeAccessor()) {
                        useWeavedIndirection(getGetMethodName(), getSetMethodName(), true);
                    } else if(getAttributeAccessor().isInstanceVariableAttributeAccessor()) {
                        useWeavedIndirection(Helper.getWeavedGetMethodName(getAttributeName()), Helper.getWeavedSetMethodName(getAttributeName()), false);
                    }
                }
                setGetMethodName(Helper.getWeavedValueHolderGetMethodName(getAttributeName()));
                setSetMethodName(Helper.getWeavedValueHolderSetMethodName(getAttributeName()));
                // Must re-initialize the attribute accessor.
                super.preInitialize(session);
            }
        }

        if (getPartitioningPolicyName() != null) {
            PartitioningPolicy policy = session.getProject().getPartitioningPolicy(getPartitioningPolicyName());
            if (policy == null) {
                session.getIntegrityChecker().handleError(DescriptorException.missingPartitioningPolicy(getPartitioningPolicyName(), null, this));
            }
            setPartitioningPolicy(policy);
        }
        if (this.isCascadeOnDeleteSetOnDatabase && !session.getPlatform().supportsDeleteOnCascade()) {
            this.isCascadeOnDeleteSetOnDatabase = false;
        }
    }
    
    /**
     * INTERNAL:
     * Initialize the state of mapping.
     */
    @Override
    public void initialize(AbstractSession session) throws DescriptorException {
        super.initialize(session);
        if (this.isPrivateOwned && (this.descriptor != null)) {
            this.descriptor.addMappingsPostCalculateChanges(this);
        }
        initializeReferenceDescriptor(session);
        initializeSelectionQuery(session);
        this.indirectionPolicy.initialize();
        
        if ((this.referenceDescriptor != null) && this.referenceDescriptor.getCachePolicy().isIsolated()) {
            this.isCacheable = false;
        }
    }

    /**
     * Initialize and set the descriptor for the referenced class in this mapping.
     */
    protected void initializeReferenceDescriptor(AbstractSession session) throws DescriptorException {
        if (getReferenceClass() == null) {
            throw DescriptorException.referenceClassNotSpecified(this);
        }

        ClassDescriptor refDescriptor = session.getDescriptor(getReferenceClass());

        if (refDescriptor == null) {
            throw DescriptorException.descriptorIsMissing(getReferenceClass().getName(), this);
        }

        if (refDescriptor.isAggregateDescriptor() && (!isAggregateCollectionMapping())) {
            throw DescriptorException.referenceDescriptorCannotBeAggregate(this);
        }

        // can not be isolated if it is null.  Seems that only aggregates do not set
        // the owning descriptor on the mapping.

        setReferenceDescriptor(refDescriptor);
    }
    
    /**
     * INTERNAL:
     * The method validateAttributeOfInstantiatedObject(Object attributeValue) fixes the value of the attributeValue 
     * in cases where it is null and indirection requires that it contain some specific data structure.  Return whether this will happen.
     * This method is used to help determine if indirection has been triggered
     * @param attributeValue
     * @return
     * @see validateAttributeOfInstantiatedObject(Object attributeValue)
     */
    public boolean isAttributeValueFullyBuilt(Object object){
        Object attributeValue = super.getAttributeValueFromObject(object);
        return this.indirectionPolicy.isAttributeValueFullyBuilt(attributeValue);
    }

    /**
     * A subclass should implement this method if it wants non default behavior.
     */
    protected void initializeSelectionQuery(AbstractSession session) throws DescriptorException {
        if (((ObjectLevelReadQuery)getSelectionQuery()).getReferenceClass() == null) {
            throw DescriptorException.referenceClassNotSpecified(this);
        }
        getSelectionQuery().setName(getAttributeName());
        getSelectionQuery().setDescriptor(getReferenceDescriptor());
        getSelectionQuery().setSourceMapping(this);
        if (getSelectionQuery().getPartitioningPolicy() == null) {
            getSelectionQuery().setPartitioningPolicy(getPartitioningPolicy());
        }
    }

    /**
     * INTERNAL:
     * The referenced object is checked if it is instantiated or not
     */
    public boolean isAttributeValueInstantiated(Object object) {
        return this.indirectionPolicy.objectIsInstantiated(getAttributeValueFromObject(object));
    }

    /**
     * PUBLIC:
     * Check cascading value for the detach operation.
     */
    public boolean isCascadeDetach() {
        return this.cascadeDetach;
    }

    /**
     * PUBLIC:
     * Check cascading value for the CREATE operation.
     */
    public boolean isCascadePersist() {
        return this.cascadePersist;
    }

    /**
     * PUBLIC:
     * Check cascading value for the MERGE operation.
     */
    public boolean isCascadeMerge() {
        return this.cascadeMerge;
    }

    /**
     * PUBLIC:
     * Check cascading value for the REFRESH operation.
     */
    public boolean isCascadeRefresh() {
        return this.cascadeRefresh;
    }

    /**
     * PUBLIC:
     * Check cascading value for the REMOVE operation.
     */
    public boolean isCascadeRemove() {
        return this.cascadeRemove;
    }
    
    /**
     * INTERNAL:
     * Return if the mapping has any ownership or other dependency over its target object(s).
     */
    @Override
    public boolean hasDependency() {
        return isPrivateOwned() || isCascadeRemove();
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean isForeignReferenceMapping() {
        return true;
    }

    /**
     * INTERNAL:
     * Return if this mapping supports joining.
     */
    public boolean isJoiningSupported() {
        return false;
    }

    /**
     * PUBLIC:
     * Return true if referenced objects are privately owned else false.
     */
    @Override
    public boolean isPrivateOwned() {
        return isPrivateOwned;
    }

    /**
     * INTERNAL:
     * Iterate on the iterator's current object's attribute defined by this mapping.
     * The iterator's settings for cascading and value holders determine how the
     * iteration continues from here.
     */
    @Override
    public void iterate(DescriptorIterator iterator) {
        Object attributeValue = this.getAttributeValueFromObject(iterator.getVisitedParent());
        this.indirectionPolicy.iterateOnAttributeValue(iterator, attributeValue);
    }

    /**
     * INTERNAL:
     * Iterate on the attribute value.
     * The value holder has already been processed.
     */
    @Override
    public abstract void iterateOnRealAttributeValue(DescriptorIterator iterator, Object realAttributeValue);


    /**
     * Force instantiation of the load group.
     */
    @Override
    public void load(final Object object, AttributeItem item, final AbstractSession session) {
        instantiateAttribute(object, session);
        if (item.getGroup() != null) {
            Object value = getRealAttributeValueFromObject(object, session);
            session.load(value, item.getGroup());
        }
    }
    
    /**
     * INTERNAL:
     * Replace the client value holder with the server value holder,
     * after copying some of the settings from the client value holder.
     */
    public void mergeRemoteValueHolder(Object clientSideDomainObject, Object serverSideDomainObject, MergeManager mergeManager) {
        this.indirectionPolicy.mergeRemoteValueHolder(clientSideDomainObject, serverSideDomainObject, mergeManager);
    }

    /**
     * PUBLIC:
     * Sets the reference object to be a private owned.
     * The default behavior is non private owned, or independent.
     * @see #setIsPrivateOwned(boolean)
     */
    public void privateOwnedRelationship() {
        setIsPrivateOwned(true);
    }

    /**
     * INTERNAL:
     * Extract value from the row and set the attribute to this value in the object.
     * return value as this value will have been converted to the appropriate type for
     * the object.
     */
    public Object readFromRowIntoObject(AbstractRecord databaseRow, JoinedAttributeManager joinManager, Object targetObject, CacheKey parentCacheKey, ObjectBuildingQuery sourceQuery, AbstractSession executionSession, boolean isTargetProtected) throws DatabaseException {
        Boolean[] wasCacheUsed = new Boolean[]{Boolean.FALSE};
        Object attributeValue = valueFromRow(databaseRow, joinManager, sourceQuery, parentCacheKey, executionSession, isTargetProtected, wasCacheUsed);
        if (wasCacheUsed[0]){
            //must clone here as certain mappings require the clone object to clone the attribute.
            Integer refreshCascade = null;
            if (sourceQuery != null && sourceQuery.isObjectBuildingQuery() && sourceQuery.shouldRefreshIdentityMapResult()) {
                refreshCascade = sourceQuery.getCascadePolicy();
            }
            attributeValue = this.indirectionPolicy.cloneAttribute(attributeValue, parentCacheKey.getObject(), parentCacheKey, targetObject, refreshCascade, executionSession, false);
        }
        if (executionSession.isUnitOfWork() && sourceQuery.shouldRefreshIdentityMapResult()){
            // check whether the attribute is fully build before calling getAttributeValueFromObject because that
            // call may fully build the attribute
            boolean wasAttributeValueFullyBuilt = isAttributeValueFullyBuilt(targetObject);
            Object oldAttribute = this.getAttributeValueFromObject(targetObject);
            setAttributeValueInObject(targetObject, attributeValue); // set this first to prevent infinite recursion
            if (wasAttributeValueFullyBuilt && this.indirectionPolicy.objectIsInstantiatedOrChanged(oldAttribute)){
                this.indirectionPolicy.instantiateObject(targetObject, attributeValue);
            }
        }else{
            setAttributeValueInObject(targetObject, attributeValue);
        }
        if (parentCacheKey != null){
            this.indirectionPolicy.setSourceObject(parentCacheKey.getObject(), attributeValue);
        }
        return attributeValue;
    }    

    /**
     * INTERNAL:
     * Once descriptors are serialized to the remote session. All its mappings and reference descriptors are traversed. Usually
     * mappings are initialized and serialized reference descriptors are replaced with local descriptors if they already exist on the
     * remote session.
     */
    @Override
    public void remoteInitialization(DistributedSession session) {
        super.remoteInitialization(session);
        setTempSession(session);
    }

    /**
     * INTERNAL:
     * replace the value holders in the specified reference object(s)
     */
    @Override
    public Map replaceValueHoldersIn(Object object, RemoteSessionController controller) {
        return controller.replaceValueHoldersIn(object);
    }

    /**
     * Returns true if this mappings associated weaved field requires a
     * transient setting to avoid metadata processing. 
     */
    public boolean requiresTransientWeavedFields() {
        return requiresTransientWeavedFields;
    }
    
    /**
     * PUBLIC:
     * Sets the cascading for all JPA operations.
     */
    public void setCascadeAll(boolean value) {
        setCascadePersist(value);
        setCascadeMerge(value);
        setCascadeRefresh(value);
        setCascadeRemove(value);
        setCascadeDetach(value);
    }

    /**
     * PUBLIC:
     * Sets the cascading for the JPA detach operation.
     */
    public void setCascadeDetach(boolean value) {
        this.cascadeDetach = value;
    }

    /**
     * PUBLIC:
     * Sets the cascading for the JPA CREATE operation.
     */
    public void setCascadePersist(boolean value) {
        this.cascadePersist = value;
    }

    /**
     * PUBLIC:
     * Sets the cascading for the JPA MERGE operation.
     */
    public void setCascadeMerge(boolean value) {
        this.cascadeMerge = value;
    }

    /**
     * PUBLIC:
     * Sets the cascading for the JPA REFRESH operation.
     */
    public void setCascadeRefresh(boolean value) {
        this.cascadeRefresh = value;
    }

    /**
     * PUBLIC:
     * Sets the cascading for the JPA REMOVE operation.
     */
    public void setCascadeRemove(boolean value) {
        this.cascadeRemove = value;
    }

    /**
     * PUBLIC:
     * Relationship mappings creates a read query to read reference objects. If this default
     * query needs to be customize then user can specify its own read query to do the reading
     * of reference objects. One must instance of ReadQuery or subclasses of the ReadQuery.
     */
    public void setCustomSelectionQuery(ReadQuery query) {
        setSelectionQuery(query);
        setHasCustomSelectionQuery(true);
    }
    
    protected void setHasCustomSelectionQuery(boolean bool) {
        hasCustomSelectionQuery = bool;
    }

    /**
     * INTERNAL:
     * A way of forcing the selection criteria to be rebuilt.
     */
    public void setForceInitializationOfSelectionCriteria(boolean bool) {
        forceInitializationOfSelectionCriteria = bool;
    }
    
    /**
     * ADVANCED:
     * Set the indirection policy.
     */
    public void setIndirectionPolicy(IndirectionPolicy indirectionPolicy) {
        this.indirectionPolicy = indirectionPolicy;
        indirectionPolicy.setMapping(this);
    }

    /**
     * PUBLIC:
     * Set if the relationship is privately owned.
     * A privately owned relationship means the target object is a dependent part of the source
     * object and is not referenced by any other object and cannot exist on its own.
     * Private ownership causes many operations to be cascaded across the relationship,
     * including, deletion, insertion, refreshing, locking (when cascaded).
     * It also ensures that private objects removed from collections are deleted and object added are inserted.
     */
    public void setIsPrivateOwned(boolean isPrivateOwned) {
        if (this.descriptor != null && ! this.isMapKeyMapping()){ // initialized
            if (isPrivateOwned && !this.isPrivateOwned){
                this.descriptor.addMappingsPostCalculateChanges(this);
                if (getDescriptor().hasInheritance()){
                    for (ClassDescriptor descriptor: getDescriptor().getInheritancePolicy().getAllChildDescriptors()) {
                        descriptor.addMappingsPostCalculateChanges(this);
                    }
                }
            }else if (!isPrivateOwned && this.isPrivateOwned){
                this.descriptor.getMappingsPostCalculateChanges().remove(this);
                if (getDescriptor().hasInheritance()){
                    for (ClassDescriptor descriptor: getDescriptor().getInheritancePolicy().getAllChildDescriptors()) {
                        descriptor.getMappingsPostCalculateChanges().remove(this);
                    }
                }
            }
        }
        this.isPrivateOwned = isPrivateOwned;
    }

    /**
     * INTERNAL:
     * Set the value of the attribute mapped by this mapping,
     * placing it inside a value holder if necessary.
     * If the value holder is not instantiated then it is instantiated.
     */
    @Override
    public void setRealAttributeValueInObject(Object object, Object value) throws DescriptorException {
        this.indirectionPolicy.setRealAttributeValueInObject(object, value);
    }

    /**
     * PUBLIC:
     * Set the referenced class.
     */
    public void setReferenceClass(Class referenceClass) {
        this.referenceClass = referenceClass;
        if (referenceClass != null) {
            setReferenceClassName(referenceClass.getName());
            // Make sure the reference class of the selectionQuery is set.
            setSelectionQuery(getSelectionQuery());
        }
    }

    /**
     * INTERNAL:
     * Used by MW.
     */
    public void setReferenceClassName(String referenceClassName) {
        this.referenceClassName = referenceClassName;
    }

    /**
     * Set the referenceDescriptor. This is a descriptor which is associated with
     * the reference class.
     */
    protected void setReferenceDescriptor(ClassDescriptor aDescriptor) {
        referenceDescriptor = aDescriptor;
    }

    /**
     * INTERNAL:
     * Sets the relationshipPartner mapping for this bi-directional mapping. If the relationshipPartner is null then
     * this is a uni-directional mapping.
     */
    public void setRelationshipPartner(DatabaseMapping mapping) {
        this.relationshipPartner = mapping;
    }

    /**
    * PUBLIC:
    * Use this method to specify the relationship partner attribute name of a bidirectional Mapping.
    * TopLink will use the attribute name to find the back pointer mapping to maintain referential integrity of
    * the bi-directional mappings.
    */
    public void setRelationshipPartnerAttributeName(String attributeName) {
        this.relationshipPartnerAttributeName = attributeName;
    }

    /**
     * Set this flag if this mappings associated weaved field requires a
     * transient setting to avoid metadata processing. 
     */
    public void setRequiresTransientWeavedFields(boolean requiresTransientWeavedFields) {
        this.requiresTransientWeavedFields = requiresTransientWeavedFields;
    }
    
    /**
     * PUBLIC:
     * Sets the selection criteria to be used as a where clause to read
     * reference objects. This criteria is automatically generated by the
     * TopLink if not explicitly specified by the user.
     */
    public void setSelectionCriteria(Expression anExpression) {
        getSelectionQuery().setSelectionCriteria(anExpression);
    }

    /**
     * Sets the query
     */
    protected void setSelectionQuery(ReadQuery aQuery) {
        selectionQuery = aQuery;
        // Make sure the reference class of the selectionQuery is set.        
        if ((selectionQuery != null) && selectionQuery.isObjectLevelReadQuery() && (selectionQuery.getReferenceClassName() == null)) {
            ((ObjectLevelReadQuery)selectionQuery).setReferenceClass(getReferenceClass());
        }
    }

    /**
     * PUBLIC:
     * This is a property on the mapping which will allow custom SQL to be
     * substituted for reading a reference object.
     */
    public void setSelectionSQLString(String sqlString) {
        getSelectionQuery().setSQLString(sqlString);
        setCustomSelectionQuery(getSelectionQuery());
    }

    /**
     * PUBLIC:
     * This is a property on the mapping which will allow custom call to be
     * substituted for reading a reference object.
     */
    public void setSelectionCall(Call call) {
        getSelectionQuery().setCall(call);
        setCustomSelectionQuery(getSelectionQuery());
    }

    /**
     * ADVANCED:
     * Indicates whether pessimistic lock of ObjectLevelReadQuery with isPessimisticLockScopeExtended set to true  
     * should be applied through this mapping beyond the tables mapped to the source object.
     */
    public void setShouldExtendPessimisticLockScope(boolean shouldExtend) {
        extendPessimisticLockScope = shouldExtend ? ExtendPessimisticLockScope.TARGET_QUERY : ExtendPessimisticLockScope.NONE;  
    }
    
    protected void setTempSession(AbstractSession session) {
        this.tempInitSession = session;
    }

    /**
     * PUBLIC:
     * Indicates whether the referenced object should always be batch read on read all queries.
     * Batch reading will read all of the related objects in a single query when accessed from an originating read all.
     * This should only be used if it is know that the related objects are always required with the source object, or indirection is not used.
     * @see #setBatchFetchType(BatchFetchType)
     */
    public void setUsesBatchReading(boolean usesBatchReading) {
        if (usesBatchReading) {
            setBatchFetchType(BatchFetchType.JOIN);
        } else {
            setBatchFetchType(null);            
        }
    }

    /**
     * PUBLIC:
     * Indirection means that a ValueHolder will be put in-between the attribute and the real object.
     * This allows for the reading of the target from the database to be delayed until accessed.
     * This defaults to true and is strongly suggested as it give a huge performance gain.
     * @see #useBasicIndirection()
     * @see #dontUseIndirection()
     */
    public void setUsesIndirection(boolean usesIndirection) {
        if (usesIndirection) {
            useBasicIndirection();
        } else {
            dontUseIndirection();
        }
    }

    /**
     * INTERNAL:
     * Indicates whether pessimistic lock of ObjectLevelReadQuery with isPessimisticLockScopeExtended set to true  
     * should be applied through this mapping beyond the tables mapped to the source object.
     */
    public boolean shouldExtendPessimisticLockScope() {
        return extendPessimisticLockScope != ExtendPessimisticLockScope.NONE;
    }
    
    public boolean shouldExtendPessimisticLockScopeInSourceQuery() {
        return extendPessimisticLockScope == ExtendPessimisticLockScope.SOURCE_QUERY;
    }
    
    public boolean shouldExtendPessimisticLockScopeInTargetQuery() {
        return extendPessimisticLockScope == ExtendPessimisticLockScope.TARGET_QUERY;
    }
    
    public boolean shouldExtendPessimisticLockScopeInDedicatedQuery() {
        return extendPessimisticLockScope == ExtendPessimisticLockScope.DEDICATED_QUERY;
    }
    
    /**
     * INTERNAL:
     */
    protected boolean shouldForceInitializationOfSelectionCriteria() {
        return forceInitializationOfSelectionCriteria;
    }
    
    protected boolean shouldInitializeSelectionCriteria() {
        if (shouldForceInitializationOfSelectionCriteria()) {
            return true;
        }
        
        if (hasCustomSelectionQuery()) {
            return false;
        }

        if (getSelectionCriteria() == null) {
            return true;
        }

        return false;
    }

    /**
     * INTERNAL:
     * Returns true if the merge should cascade to the mappings reference's parts.
     */
    public boolean shouldMergeCascadeParts(MergeManager mergeManager) {
        return (mergeManager.shouldCascadeByMapping() && ((this.isCascadeMerge() && !mergeManager.isForRefresh()) || (this.isCascadeRefresh() && mergeManager.isForRefresh()) )) || mergeManager.shouldCascadeAllParts() || (mergeManager.shouldCascadePrivateParts() && isPrivateOwned());
    }

    /**
     * INTERNAL:
     * Returns true if the merge should cascade to the mappings reference's parts.
     */
    public boolean shouldRefreshCascadeParts(MergeManager mergeManager) {
        return (mergeManager.shouldCascadeByMapping() && this.isCascadeRefresh()) || mergeManager.shouldCascadeAllParts() || (mergeManager.shouldCascadePrivateParts() && isPrivateOwned());
    }

    /**
     * Returns true if the merge should cascade to the mappings reference.
     */
    protected boolean shouldMergeCascadeReference(MergeManager mergeManager) {
        if (mergeManager.shouldCascadeReferences()) {
            return true;
        }

        // P2.0.1.3: Was merging references on non-privately owned parts
        // Same logic in:
        return shouldMergeCascadeParts(mergeManager);
    }

    /**
     * Returns true if any process leading to object modification should also affect its parts
     * Usually used by write, insert, update and delete.
     */
    protected boolean shouldObjectModifyCascadeToParts(ObjectLevelModifyQuery query) {
        if (this.isReadOnly) {
            return false;
        }

        // Only cascade dependents writes in uow.
        if (query.shouldCascadeOnlyDependentParts()) {
            return hasConstraintDependency();
        }

        if (this.isPrivateOwned) {
            return true;
        }

        return query.shouldCascadeAllParts();
    }

    /**
     * PUBLIC:
     * Indicates whether the referenced object should always be batch read on read all queries.
     * Batch reading will read all of the related objects in a single query when accessed from an originating read all.
     * This should only be used if it is know that the related objects are always required with the source object, or indirection is not used.
     */
    public boolean shouldUseBatchReading() {
        return this.batchFetchType != null;
    }

    /**
     * PUBLIC:
     * Indirection means that a ValueHolder will be put in-between the attribute and the real object.
     * This allows for the reading of the target from the database to be delayed until accessed.
     * This defaults to true and is strongly suggested as it give a huge performance gain.
     */
    public void useBasicIndirection() {
        setIndirectionPolicy(new BasicIndirectionPolicy());
    }

    /**
     * PUBLIC:
     * Indicates whether the referenced object should always be batch read on read all queries.
     * Batch reading will read all of the related objects in a single query when accessed from an originating read all.
     * This should only be used if it is know that the related objects are always required with the source object, or indirection is not used.
     */
    public void useBatchReading() {
        setBatchFetchType(BatchFetchType.JOIN);
    }

    /**
     * INTERNAL:
     * Configures the mapping to used weaved indirection.
     * This requires that the toplink-agent be used to weave indirection into the class.
     * This policy is only require for method access.
     * @param getMethodName is the name of the original (or weaved in field access case) set method for the mapping.
     * @param setMethodName is the name of the original (or weaved in field access case) set method for the mapping.
     * @param hasUsedMethodAccess indicates whether method or field access was originally used.
     */
    public void useWeavedIndirection(String getMethodName, String setMethodName, boolean hasUsedMethodAccess){
        setIndirectionPolicy(new WeavedObjectBasicIndirectionPolicy(getMethodName, setMethodName, null, hasUsedMethodAccess));
    }

    /**
     * PUBLIC:
     * Indirection means that a IndirectContainer (wrapping a ValueHolder) will be put in-between the attribute and the real object.
     * This allows for an application specific class to be used which wraps the value holder.
     * The purpose of this is that the domain objects will not require to import the ValueHolderInterface class.
     * Refer also to transparent indirection for a transparent solution to indirection.
     */
    public void useContainerIndirection(Class containerClass) {
        ContainerIndirectionPolicy policy = new ContainerIndirectionPolicy();
        policy.setContainerClass(containerClass);
        setIndirectionPolicy(policy);
    }

    /**
     * PUBLIC:
     * Indirection means that some sort of indirection object will be put in-between the attribute and the real object.
     * This allows for the reading of the target from the database to be delayed until accessed.
     * This defaults to true and is strongly suggested as it give a huge performance gain.
     */
    public boolean usesIndirection() {
        return this.indirectionPolicy.usesIndirection();
    }
    
    /**
     * INTERNAL:
     * Update a ChangeRecord to replace the ChangeSet for the old entity with the changeSet for the new Entity.  This is
     * used when an Entity is merged into itself and the Entity reference new or detached entities.
     */
    public abstract void updateChangeRecordForSelfMerge(ChangeRecord changeRecord, Object source, Object target, UnitOfWorkChangeSet parentUOWChangeSet, UnitOfWorkImpl unitOfWork);
    
    /**
     * PUBLIC:
     * Indicates whether the referenced object(s) should always be joined on read queries.
     * Joining will join the two classes tables to read all of the data in a single query.
     * This should only be used if it is know that the related objects are always required with the source object,
     * or indirection is not used.
     * A join-fetch can either use an INNER_JOIN or OUTER_JOIN,
     * if the relationship may reference null or an empty collection an outer join should be used to avoid filtering the source objects from the queries.
     * Join fetch can also be specified on the query, and it is normally more efficient to do so as some queries may not require the related objects.
     * Typically batch reading is more efficient than join fetching and should be considered, especially for collection relationships.
     * @see org.eclipse.persistence.queries.ObjectLevelReadQuery#addJoinedAttribute(String)
     * @see org.eclipse.persistence.queries.ReadAllQuery#addBatchReadAttribute(String)
     */
    public void setJoinFetch(int joinFetch) {
        this.joinFetch = joinFetch;
    }
    
    /**
     * PUBLIC:
     * Return if this relationship should always be join fetched.
     */
    public int getJoinFetch() {
        return joinFetch;
    }
        
    /**
     * INTERNAL: Called by JPA metadata processing to store the owning mapping
     * for this mapping
     * 
     * @param mappedBy
     */
    public void setMappedBy(String mappedBy) {
        this.mappedBy = mappedBy;
    }        
    
    /**
     * PUBLIC:
     * Return if this relationship should always be join fetched.
     */
    public boolean isJoinFetched() {
        return getJoinFetch() != NONE;
    }
            
    /**
     * PUBLIC:
     * Return if this relationship should always be INNER join fetched.
     */
    public boolean isInnerJoinFetched() {
        return getJoinFetch() == INNER_JOIN;
    }
    
    /**
     * PUBLIC:
     * Return if this relationship should always be OUTER join fetched.
     */
    public boolean isOuterJoinFetched() {
        return getJoinFetch() == OUTER_JOIN;
    }
        
    /**
     * PUBLIC:
     * Specify this relationship to always be join fetched using an INNER join.
     */
    public void useInnerJoinFetch() {
        setJoinFetch(INNER_JOIN);
    }
    
    /**
     * PUBLIC:
     * Specify this relationship to always be join fetched using an OUTER join.
     */
    public void useOuterJoinFetch() {
        setJoinFetch(OUTER_JOIN);
    }

    /**
     * ADVANCED:
     * Return if delete cascading has been set on the database for the
     * mapping's foreign key constraint.
     */
    public boolean isCascadeOnDeleteSetOnDatabase() {
        return isCascadeOnDeleteSetOnDatabase;
    }

    /**
     * ADVANCED:
     * Set if delete cascading has been set on the database for the
     * mapping's foreign key constraint.
     * The behavior is dependent on the mapping.
     * <p>OneToOne (target foreign key) - deletes target object (private owned)
     * <p>OneToMany, AggregateCollection - deletes target objects (private owned)
     * <p>ManyToMany - deletes from join table (only)
     * <p>DirectCollection - delete from direct table
     */
    public void setIsCascadeOnDeleteSetOnDatabase(boolean isCascadeOnDeleteSetOnDatabase) {
        this.isCascadeOnDeleteSetOnDatabase = isCascadeOnDeleteSetOnDatabase;
    }
    
    /**
     * Used to signal that this mapping references a protected/isolated entity and requires
     * special merge/object building behaviour.
     */
    public void setIsCacheable(boolean cacheable) {
        this.isCacheable = cacheable;
    }

    /**
     * INTERNAL:
     * To validate mappings declaration
     */
    @Override
    public void validateBeforeInitialization(AbstractSession session) throws DescriptorException {
        super.validateBeforeInitialization(session);

        // If a lazy mapping required weaving for lazy, and weaving did not occur,
        // then the mapping must be reverted to no use indirection.
        if ((this.indirectionPolicy instanceof WeavedObjectBasicIndirectionPolicy) && !ClassConstants.PersistenceWeavedLazy_Class.isAssignableFrom(getDescriptor().getJavaClass())) {
            Object[] args = new Object[2];
            args[0] = getAttributeName();
            args[1] = getDescriptor().getJavaClass();
            session.log(SessionLog.WARNING, SessionLog.METADATA, "metadata_warning_ignore_lazy", args);
            setIndirectionPolicy(new NoIndirectionPolicy());
        }
        
        if (getAttributeAccessor() instanceof InstanceVariableAttributeAccessor) {
            Class attributeType = ((InstanceVariableAttributeAccessor)getAttributeAccessor()).getAttributeType();
            this.indirectionPolicy.validateDeclaredAttributeType(attributeType, session.getIntegrityChecker());
        } else if (getAttributeAccessor().isMethodAttributeAccessor()) {
            // 323148
            Class returnType = ((MethodAttributeAccessor)getAttributeAccessor()).getGetMethodReturnType();
            this.indirectionPolicy.validateGetMethodReturnType(returnType, session.getIntegrityChecker());
            Class parameterType = ((MethodAttributeAccessor)getAttributeAccessor()).getSetMethodParameterType();            
            this.indirectionPolicy.validateSetMethodParameterType(parameterType, session.getIntegrityChecker());
        }
    }

    /**
     * This method is used to load a relationship from a list of PKs. This list
     * may be available if the relationship has been cached.
     */
    public abstract Object valueFromPKList(Object[] pks, AbstractRecord foreignKeys, AbstractSession session);

    /**
     * INTERNAL: Return the value of the reference attribute or a value holder.
     * Check whether the mapping's attribute should be optimized through batch
     * and joining.
     */
    @Override
    public Object valueFromRow(AbstractRecord row, JoinedAttributeManager joinManager, ObjectBuildingQuery sourceQuery, CacheKey cacheKey, AbstractSession executionSession, boolean isTargetProtected, Boolean[] wasCacheUsed) throws DatabaseException {
        if (this.descriptor.getCachePolicy().isProtectedIsolation()) {
            if (this.isCacheable && isTargetProtected && cacheKey != null) {
                //cachekey will be null when isolating to uow
                //used cached collection
                Object cached = cacheKey.getObject();
                if (cached != null) {
                    if (wasCacheUsed != null){
                        wasCacheUsed[0] = Boolean.TRUE;
                    }
                    //this will just clone the indirection.
                    //the indirection object is responsible for cloning the value.
                    return getAttributeValueFromObject(cached);
                }
            } else if (!this.isCacheable && !isTargetProtected && cacheKey != null) {
                return this.indirectionPolicy.buildIndirectObject(new ValueHolder(null));
            }
        }
        // PERF: Direct variable access.
        // If the query uses batch reading, return a special value holder
        // or retrieve the object from the query property.
        if (sourceQuery.isObjectLevelReadQuery() && (((ObjectLevelReadQuery)sourceQuery).isAttributeBatchRead(this.descriptor, getAttributeName())
                || (sourceQuery.isReadAllQuery() && shouldUseBatchReading()))) {
            return batchedValueFromRow(row, (ObjectLevelReadQuery)sourceQuery, cacheKey);
        }
        if (shouldUseValueFromRowWithJoin(joinManager, sourceQuery)) {
            return valueFromRowInternalWithJoin(row, joinManager, sourceQuery, cacheKey, executionSession, isTargetProtected);
        } else {
            return valueFromRowInternal(row, joinManager, sourceQuery, executionSession);
        }
    }
    
    /**
     * INTERNAL:
     * Indicates whether valueFromRow should call valueFromRowInternalWithJoin (true)
     * or valueFromRowInternal (false)
     */
    protected boolean shouldUseValueFromRowWithJoin(JoinedAttributeManager joinManager, ObjectBuildingQuery sourceQuery) {
        return ((joinManager != null) && (joinManager.isAttributeJoined(this.descriptor, this))) || sourceQuery.hasPartialAttributeExpressions();
    }
    
    /**
     * INTERNAL:
     * If the query used joining or partial attributes, build the target object directly.
     * If isJoiningSupported()==true then this method must be overridden.
     */
    protected Object valueFromRowInternalWithJoin(AbstractRecord row, JoinedAttributeManager joinManager, ObjectBuildingQuery sourceQuery, CacheKey parentCacheKey, AbstractSession executionSession, boolean isTargetProtected) throws DatabaseException {
        throw ValidationException.mappingDoesNotOverrideValueFromRowInternalWithJoin(Helper.getShortClassName(this.getClass()));
    }
    
    /**
     * INTERNAL:
     * Return the value of the reference attribute or a value holder.
     * Check whether the mapping's attribute should be optimized through batch and joining.
     */
    protected Object valueFromRowInternal(AbstractRecord row, JoinedAttributeManager joinManager, ObjectBuildingQuery sourceQuery, AbstractSession executionSession) throws DatabaseException {
        // PERF: Direct variable access.
        ReadQuery targetQuery = this.selectionQuery;

        // Copy nested fetch group from the source query 
        if (targetQuery.isObjectLevelReadQuery() && targetQuery.getDescriptor().hasFetchGroupManager()) {
            FetchGroup sourceFG = sourceQuery.getExecutionFetchGroup(this.getDescriptor());
            if (sourceFG != null) {                    
                FetchGroup targetFetchGroup = sourceFG.getGroup(getAttributeName());
                if(targetFetchGroup != null) {
                    // perf: bug#4751950, first prepare the query before cloning.
                    if (targetQuery.shouldPrepare()) {
                        targetQuery.checkPrepare(executionSession, row);
                    }
                    targetQuery = (ObjectLevelReadQuery)targetQuery.clone();
                    targetQuery.setIsExecutionClone(true);
                    ((ObjectLevelReadQuery)targetQuery).setFetchGroup(targetFetchGroup);
                }
            }
        }
        
        // CR #4365, 3610825 - moved up from the block below, needs to be set with 
        // indirection off. Clone the query and set its id.
        if (!this.indirectionPolicy.usesIndirection()) {
            if (targetQuery == this.selectionQuery) {
                // perf: bug#4751950, first prepare the query before cloning.
                if (targetQuery.shouldPrepare()) {
                    targetQuery.checkPrepare(executionSession, row);
                }
                targetQuery = (ObjectLevelReadQuery)targetQuery.clone();
                targetQuery.setIsExecutionClone(true);
            }
            targetQuery.setQueryId(sourceQuery.getQueryId());
            if (sourceQuery.usesResultSetAccessOptimization()) {
                targetQuery.setAccessors(sourceQuery.getAccessors());
            }
            ((ObjectLevelReadQuery)targetQuery).setRequiresDeferredLocks(sourceQuery.requiresDeferredLocks());
        }

        // If the source query is cascading then the target query must use the same settings.
        if (targetQuery.isObjectLevelReadQuery()) {
            if (sourceQuery.shouldCascadeAllParts() || (this.isPrivateOwned && sourceQuery.shouldCascadePrivateParts()) || (this.cascadeRefresh && sourceQuery.shouldCascadeByMapping())) {
                // If the target query has already been cloned (we're refreshing) avoid 
                // re-cloning the query again.
                if (targetQuery == this.selectionQuery) {
                    // perf: bug#4751950, first prepare the query before cloning.
                    if (targetQuery.shouldPrepare()) {
                        targetQuery.checkPrepare(executionSession, row);
                    }
                    targetQuery = (ObjectLevelReadQuery)targetQuery.clone();
                    targetQuery.setIsExecutionClone(true);
                }

                ((ObjectLevelReadQuery)targetQuery).setShouldRefreshIdentityMapResult(sourceQuery.shouldRefreshIdentityMapResult());
                targetQuery.setCascadePolicy(sourceQuery.getCascadePolicy());
    
                // For queries that have turned caching off, such as aggregate collection, leave it off.
                if (targetQuery.shouldMaintainCache()) {
                    targetQuery.setShouldMaintainCache(sourceQuery.shouldMaintainCache());
                }
    
                // For flashback: Read attributes as of the same time if required.
                if (((ObjectLevelReadQuery)sourceQuery).hasAsOfClause()) {
                    targetQuery.setSelectionCriteria((Expression)targetQuery.getSelectionCriteria().clone());
                    ((ObjectLevelReadQuery)targetQuery).setAsOfClause(((ObjectLevelReadQuery)sourceQuery).getAsOfClause());
                }
            }
            
            if (isExtendingPessimisticLockScope(sourceQuery)) {
                if (this.extendPessimisticLockScope == ExtendPessimisticLockScope.TARGET_QUERY) {
                    if (targetQuery == this.selectionQuery) {
                        // perf: bug#4751950, first prepare the query before cloning.
                        if (targetQuery.shouldPrepare()) {
                            targetQuery.checkPrepare(executionSession, row);
                        }
                        targetQuery = (ObjectLevelReadQuery)targetQuery.clone();
                        targetQuery.setIsExecutionClone(true);
                    }
                    extendPessimisticLockScopeInTargetQuery((ObjectLevelReadQuery)targetQuery, sourceQuery);
                } else if (this.extendPessimisticLockScope == ExtendPessimisticLockScope.DEDICATED_QUERY) {
                    ReadQuery dedicatedQuery = getExtendPessimisticLockScopeDedicatedQuery(executionSession, sourceQuery.getLockMode());
                    executionSession.executeQuery(dedicatedQuery, row);
                }
            }
        }
        targetQuery = prepareHistoricalQuery(targetQuery, sourceQuery, executionSession);

        return this.indirectionPolicy.valueFromQuery(targetQuery, row, executionSession);
    }
    
    /**
     * INTERNAL:
     * Indicates whether the source query's pessimistic lock scope scope should be extended in the target query.
     */
    protected boolean isExtendingPessimisticLockScope(ObjectBuildingQuery sourceQuery) {
        // TODO: What if sourceQuery is NOT ObjectLevelReadQuery? Should we somehow handle this? 
        // Or alternatively define ObjectBuildingQuery.shouldExtendPessimisticLockScope() to always return false?
        return sourceQuery.isLockQuery() && sourceQuery.isObjectLevelReadQuery() && ((ObjectLevelReadQuery)sourceQuery).shouldExtendPessimisticLockScope();
    }

    /**
     * INTERNAL:
     * Allow for the mapping to perform any historical query additions.
     * Return the new target query.
     */
    protected ReadQuery prepareHistoricalQuery(ReadQuery targetQuery, ObjectBuildingQuery sourceQuery, AbstractSession executionSession) {
        return targetQuery;
    }

    /**
     * INTERNAL:
     * Return a sub-partition of the row starting at the index for the mapping.
     */
    public AbstractRecord trimRowForJoin(AbstractRecord row, JoinedAttributeManager joinManager, AbstractSession executionSession) {
        // The field for many objects may be in the row,
        // so build the subpartion of the row through the computed values in the query,
        // this also helps the field indexing match.
        if ((joinManager != null) && (joinManager.getJoinedMappingIndexes_() != null)) {
            Object value = joinManager.getJoinedMappingIndexes_().get(this);
            if (value != null) {
               return trimRowForJoin(row, value, executionSession);
            }
        }
        return row;
    }

    /**
     * INTERNAL:
     * Return a sub-partition of the row starting at the index.
     */
    public AbstractRecord trimRowForJoin(AbstractRecord row, Object value, AbstractSession executionSession) {
        // CR #... the field for many objects may be in the row,
        // so build the subpartion of the row through the computed values in the query,
        // this also helps the field indexing match.
        int fieldStartIndex;
        if (value instanceof Integer) {
            fieldStartIndex = ((Integer)value).intValue();
        } else {
            // must be Map of classes to Integers
            Map map = (Map)value;
            Class cls;
            if (getDescriptor().hasInheritance() && getDescriptor().getInheritancePolicy().shouldReadSubclasses()) {
                cls = getDescriptor().getInheritancePolicy().classFromRow(row, executionSession);
            } else {
                cls = getDescriptor().getJavaClass();
            }
            fieldStartIndex = ((Integer)map.get(cls)).intValue();
        }
        Vector trimedFields = new NonSynchronizedSubVector(row.getFields(), fieldStartIndex, row.size());
        Vector trimedValues = new NonSynchronizedSubVector(row.getValues(), fieldStartIndex, row.size());
        return new DatabaseRecord(trimedFields, trimedValues);
    }
    
    /**
     * INTERNAL:
     * Prepare the clone of the nested query for joining.
     * The nested query clones are stored on the execution (clone) joinManager to avoid cloning per row.
     */
    protected ObjectLevelReadQuery prepareNestedJoinQueryClone(AbstractRecord row, List dataResults, JoinedAttributeManager joinManager, ObjectBuildingQuery sourceQuery, AbstractSession executionSession) {
        // A nested query must be built to pass to the descriptor that looks like the real query execution would,
        // these should be cached on the query during prepare.
        ObjectLevelReadQuery nestedQuery = null;
        // This is also call for partial object reading.
        if (joinManager == null) {
            nestedQuery = prepareNestedJoins(joinManager, sourceQuery, executionSession);
            nestedQuery.setSession(executionSession);
            return nestedQuery;
        }
        // PERF: Also store the clone of the nested query on the execution query to avoid
        // cloning per row.
        if (joinManager.getJoinedMappingQueryClones() == null) {
            joinManager.setJoinedMappingQueryClones(new HashMap(5));
        }
        nestedQuery = joinManager.getJoinedMappingQueryClones().get(this);
        if (nestedQuery == null) {
            if (joinManager.getJoinedMappingQueries_() != null) {
                nestedQuery = joinManager.getJoinedMappingQueries_().get(this);
                nestedQuery = (ObjectLevelReadQuery)nestedQuery.clone();
            } else {
                nestedQuery = prepareNestedJoins(joinManager, sourceQuery, executionSession);
            }
            nestedQuery.setSession(executionSession);
            //CR #4365 - used to prevent infinite recursion on refresh object cascade all
            nestedQuery.setQueryId(joinManager.getBaseQuery().getQueryId());
            nestedQuery.setExecutionTime(joinManager.getBaseQuery().getExecutionTime());
            joinManager.getJoinedMappingQueryClones().put(this, nestedQuery);
        }
        // Must also set data results to the nested query if it uses to-many joining.
        if (nestedQuery.hasJoining() && nestedQuery.getJoinedAttributeManager().isToManyJoin()) {
            // The data results only of the child object are required, they must also be trimmed.
            List nestedDataResults = dataResults;
            if (nestedDataResults == null) {
                // Extract the primary key of the source object, to filter only the joined rows for that object.
                Object sourceKey = this.descriptor.getObjectBuilder().extractPrimaryKeyFromRow(row, executionSession);
                nestedDataResults = joinManager.getDataResultsByPrimaryKey().get(sourceKey);
            }
            nestedDataResults = new ArrayList(nestedDataResults);
            Object indexObject = joinManager.getJoinedMappingIndexes_().get(this);
            // Trim results to start at nested row index.
            for (int index = 0; index < nestedDataResults.size(); index++) {
                AbstractRecord sourceRow = (AbstractRecord)nestedDataResults.get(index);                        
                nestedDataResults.set(index, trimRowForJoin(sourceRow, indexObject, executionSession));
            }
            nestedQuery.getJoinedAttributeManager().setDataResults(nestedDataResults, executionSession);
        }
        nestedQuery.setRequiresDeferredLocks(sourceQuery.requiresDeferredLocks());
        return nestedQuery;
    }
    
    /**
     * PUBLIC:
     * Return the type of batch fetching to use for all queries for this class if configured.
     */
    public BatchFetchType getBatchFetchType() {
        return batchFetchType;
    }
    
    /**
     * PUBLIC:
     * Set the type of batch fetching to use for all queries for this class.
     */
    public void setBatchFetchType(BatchFetchType batchFetchType) {
        this.batchFetchType = batchFetchType;
    }

    /**
     * INTERNAL:
     * Allow subclass to define a foreign key in the target's table.
     */
    public void addTargetForeignKeyField(DatabaseField targetForeignKeyField, DatabaseField sourcePrimaryKeyField) {
        throw new UnsupportedOperationException("addTargetForeignKeyField");
    }

    /**
     * INTERNAL:
     * Allow subclass to define a foreign key in the source's table.
     */
    public void addForeignKeyField(DatabaseField sourceForeignKeyField, DatabaseField targetPrimaryKeyField) {
        throw new UnsupportedOperationException("addForeignKeyField");
    }
    
    /**
     * INTERNAL:
     * Relationships order by their target primary key fields by default.
     */
    @Override
    public List<Expression> getOrderByNormalizedExpressions(Expression base) {
        List<Expression> orderBys = new ArrayList(this.referenceDescriptor.getPrimaryKeyFields().size());
        for (DatabaseField field : this.referenceDescriptor.getPrimaryKeyFields()) {
            orderBys.add(base.getField(field));
        }
        return orderBys;
    }
}
