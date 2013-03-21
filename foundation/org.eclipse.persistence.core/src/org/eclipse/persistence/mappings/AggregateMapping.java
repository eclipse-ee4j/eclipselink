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
 *      *     30/05/2012-2.4 Guy Pelletier    
 *       - 354678: Temp classloader is still being used during metadata processing
 *     09 Jan 2013-2.5 Gordon Yorke
 *       - 397772: JPA 2.1 Entity Graph Support
 ******************************************************************************/  
package org.eclipse.persistence.mappings;

import java.util.*;
import java.security.AccessController;
import java.security.PrivilegedActionException;

import org.eclipse.persistence.internal.descriptors.changetracking.AggregateAttributeChangeListener;
import org.eclipse.persistence.internal.descriptors.changetracking.AttributeChangeListener;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorEventManager;
import org.eclipse.persistence.descriptors.DescriptorQueryManager;
import org.eclipse.persistence.descriptors.changetracking.ChangeTracker;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.internal.descriptors.*;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.internal.sessions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.sessions.remote.*;
import org.eclipse.persistence.sessions.CopyGroup;
import org.eclipse.persistence.internal.queries.AttributeItem;
import org.eclipse.persistence.internal.queries.JoinedAttributeManager;

/**
 * <b>Purpose</b>: Two objects can be considered to be related by aggregation if there is a strict
 * 1:1 relationship between the objects. This means that if the source (parent)object exists, then
 * the target (child or owned) object must exist. This class implements the behavior common to the
 * aggregate object and structure mappings.
 *
 * @author Sati
 * @since TopLink for Java 1.0
 */
public abstract class AggregateMapping extends DatabaseMapping {

    /** Stores a reference class */
    protected Class referenceClass;
    protected String referenceClassName;

    /** The descriptor of the reference class */
    protected ClassDescriptor referenceDescriptor;

    /**
     * Default constructor.
     */
    public AggregateMapping() {
        super();
        this.setWeight(WEIGHT_AGGREGATE);
    }

    /**
     * Make a copy of the sourceQuery for the attribute.
     */
    protected DeleteObjectQuery buildAggregateDeleteQuery(DeleteObjectQuery sourceQuery, Object sourceAttributeValue) {
        DeleteObjectQuery aggregateQuery = new DeleteObjectQuery();
        buildAggregateModifyQuery(sourceQuery, aggregateQuery, sourceAttributeValue);
        return aggregateQuery;
    }

    /**
     * Initialize the aggregate query with the settings from the source query.
     */
    protected void buildAggregateModifyQuery(ObjectLevelModifyQuery sourceQuery, ObjectLevelModifyQuery aggregateQuery, Object sourceAttributeValue) {
        // If we are map key mapping we can't build a backupAttributeValue
        // from a back up clone since a map key mapping does not map a field
        // on the source queries backup clone.
        if (sourceQuery.getSession().isUnitOfWork() && ! isMapKeyMapping()) {
            Object backupAttributeValue = getAttributeValueFromBackupClone(sourceQuery.getBackupClone());
            if (backupAttributeValue == null) {
                backupAttributeValue = getObjectBuilder(sourceAttributeValue, sourceQuery.getSession()).buildNewInstance();
            }
            
            aggregateQuery.setBackupClone(backupAttributeValue);
        }
        aggregateQuery.setCascadePolicy(sourceQuery.getCascadePolicy());
        aggregateQuery.setObject(sourceAttributeValue);
        aggregateQuery.setTranslationRow(sourceQuery.getTranslationRow());
        aggregateQuery.setSession(sourceQuery.getSession());
        aggregateQuery.setProperties(sourceQuery.getProperties());
    }

    /**
     * Make a copy of the sourceQuery for the attribute.
     */
    protected WriteObjectQuery buildAggregateWriteQuery(WriteObjectQuery sourceQuery, Object sourceAttributeValue) {
        WriteObjectQuery aggregateQuery = new WriteObjectQuery();
        buildAggregateModifyQuery(sourceQuery, aggregateQuery, sourceAttributeValue);
        return aggregateQuery;
    }

    /**
     * INTERNAL:
     * Clone the attribute from the clone and assign it to the backup.
     */
    public void buildBackupClone(Object clone, Object backup, UnitOfWorkImpl unitOfWork) {
        Object attributeValue = getAttributeValueFromObject(clone);
        setAttributeValueInObject(backup, buildBackupClonePart(attributeValue, unitOfWork));
    }

    /**
     * INTERNAL:
     * Build and return a backup clone of the attribute.
     */
    protected Object buildBackupClonePart(Object attributeValue, UnitOfWorkImpl unitOfWork) {
        if (attributeValue == null) {
            return null;
        }
        return getObjectBuilder(attributeValue, unitOfWork).buildBackupClone(attributeValue, unitOfWork);
    }

    /**
     * INTERNAL:
     * Clone the attribute from the original and assign it to the clone.
     */
    @Override
    public void buildClone(Object original, CacheKey cacheKey, Object clone, Integer refreshCascade, AbstractSession cloningSession) {
        Object attributeValue = getAttributeValueFromObject(original);
        setAttributeValueInObject(clone, buildClonePart(original, clone, cacheKey, attributeValue, refreshCascade, cloningSession));
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
    public void buildCloneFromRow(AbstractRecord databaseRow, JoinedAttributeManager joinManager, Object clone, CacheKey sharedCacheKey, ObjectBuildingQuery sourceQuery, UnitOfWorkImpl unitOfWork, AbstractSession executionSession) {
        // automatically returns a uow result from scratch that doesn't need cloning
        Object cloneAttributeValue = valueFromRow(databaseRow, joinManager, sourceQuery, sharedCacheKey, executionSession, true, null);
        setAttributeValueInObject(clone, cloneAttributeValue);
    }

    /**
     * INTERNAL:
     * Build and return a clone of the attribute.
     */
    protected Object buildClonePart(Object original, Object clone, CacheKey cacheKey, Object attributeValue, Integer refreshCascade, AbstractSession cloningSession) {
        return buildClonePart(attributeValue, clone, cacheKey, refreshCascade, cloningSession, cloningSession.isUnitOfWork() && ((UnitOfWorkImpl)cloningSession).isOriginalNewObject(original));
    }
    
    /**
     * INTERNAL:     * Build and return a clone of the attribute.
     */
    protected Object buildClonePart(Object attributeValue, Object clone, CacheKey parentCacheKey, Integer refreshCascade, AbstractSession cloningSession, boolean isNewObject) {
        if (attributeValue == null) {
            return null;
        }
        if (cloningSession.isUnitOfWork() && isNewObject) { // only true if cloningSession is UOW as this signature only exists in this mapping.
            ((UnitOfWorkImpl)cloningSession).addNewAggregate(attributeValue);
        }

        // Do not clone for read-only.
        if (cloningSession.isUnitOfWork() && cloningSession.isClassReadOnly(attributeValue.getClass())){
            return attributeValue;
        }

        ObjectBuilder aggregateObjectBuilder = getObjectBuilder(attributeValue, cloningSession);

        // bug 2612602 as we are building the working copy make sure that we call to correct clone method.
        Object clonedAttributeValue = aggregateObjectBuilder.instantiateWorkingCopyClone(attributeValue, cloningSession);
        aggregateObjectBuilder.populateAttributesForClone(attributeValue, parentCacheKey, clonedAttributeValue, refreshCascade, cloningSession);
        //also clone the fetch group reference if applied
        if (aggregateObjectBuilder.getDescriptor().hasFetchGroupManager()) {
            aggregateObjectBuilder.getDescriptor().getFetchGroupManager().copyAggregateFetchGroupInto(attributeValue, clonedAttributeValue, clone, cloningSession);
        }

        return clonedAttributeValue;
    }

    /**
     * INTERNAL:
     * Copy of the attribute of the object.
     * This is NOT used for unit of work but for templatizing an object.
     */
    @Override
    public void buildCopy(Object copy, Object original, CopyGroup group) {
        Object attributeValue = getAttributeValueFromObject(original);
        setAttributeValueInObject(copy, buildCopyOfAttributeValue(attributeValue, group));
    }

    /**
     * Copy of the attribute of the object.
     * This is NOT used for unit of work but for templatizing an object.
     */
    protected Object buildCopyOfAttributeValue(Object attributeValue, CopyGroup group) {
        if (attributeValue == null) {
            return null;
        }
        return getObjectBuilder(attributeValue, group.getSession()).copyObject(attributeValue, group);
    }

    /**
     * INTERNAL:
     * In case Query By Example is used, this method generates an expression from a attribute value pair.  Since
     * this is an Aggregate mapping, a recursive call is made to the buildExpressionFromExample method of
     * ObjectBuilder.
     */
    public Expression buildExpression(Object queryObject, QueryByExamplePolicy policy, Expression expressionBuilder, Map processedObjects, AbstractSession session) {
        String attributeName = this.getAttributeName();
        Object attributeValue = this.getRealAttributeValueFromObject(queryObject, session);

        if (!policy.shouldIncludeInQuery(queryObject.getClass(), attributeName, attributeValue)) {
            //the attribute name and value pair is not to be included in the query.
            return null;
        }

        if (attributeValue == null) {
            //even though it is null, it is to be always included in the query
            Expression expression = expressionBuilder.get(attributeName);
            return policy.completeExpressionForNull(expression);
        }

        ObjectBuilder objectBuilder = getReferenceDescriptor().getObjectBuilder();
        return objectBuilder.buildExpressionFromExample(attributeValue, policy, expressionBuilder.get(attributeName), processedObjects, session);
    }

    /**
     * INTERNAL:
     * Build and return a new instance of the specified attribute.
     * This will be populated by a merge.
     */
    protected Object buildNewMergeInstanceOf(Object sourceAttributeValue, AbstractSession session) {
        return getObjectBuilder(sourceAttributeValue, session).buildNewInstance();
    }

    /**
     * INTERNAL:
     * Cascade perform delete through mappings that require the cascade
     */
//    public void cascadePerformDeleteIfRequired(Object object, UnitOfWork uow, Map visitedObjects){
        //objects referenced by this mapping are not registered as they have
        // no identity, this is a no-op.
//    }

    /**
     * INTERNAL:
     * Cascade registerNew for Create through mappings that require the cascade
     */
//    public void cascadeRegisterNewIfRequired(Object object, UnitOfWork uow, Map visitedObjects){
        //aggregate objects are not registeres as they have no identity, this is a no-op.
//    }

    /**
     * INTERNAL:
     * Compare the attributes. Return true if they are alike.
     */
    protected boolean compareAttributeValues(Object attributeValue1, Object attributeValue2, AbstractSession session) {
        if ((attributeValue1 == null) && (attributeValue2 == null)) {
            return true;
        }
        if ((attributeValue1 == null) || (attributeValue2 == null)) {
            return false;
        }
        if (attributeValue1.getClass() != attributeValue2.getClass()) {
            return false;
        }
        return getObjectBuilder(attributeValue1, session).compareObjects(attributeValue1, attributeValue2, session);
    }

    /**
     * INTERNAL:
     * Compare the changes between two aggregates.
     * Return a change record holding the changes.
     */
    public ChangeRecord compareForChange(Object clone, Object backup, ObjectChangeSet owner, AbstractSession session) {
        Object cloneAttribute = getAttributeValueFromObject(clone);
        Object backupAttribute = null;

        if (!owner.isNew()) {
            backupAttribute = getAttributeValueFromObject(backup);
            if ((cloneAttribute == null) && (backupAttribute == null)) {
                return null;// no change
            }
            if ((cloneAttribute != null) && (backupAttribute != null) && (!cloneAttribute.getClass().equals(backupAttribute.getClass()))) {
                backupAttribute = null;
            }
        }

        AggregateChangeRecord changeRecord = new AggregateChangeRecord(owner);
        changeRecord.setAttribute(getAttributeName());
        changeRecord.setMapping(this);
        changeRecord.setOldValue(backupAttribute);

        if (cloneAttribute == null) {// the attribute was set to null
            changeRecord.setChangedObject(null);
            return changeRecord;
        }

        ObjectBuilder builder = getObjectBuilder(cloneAttribute, session);

        //if the owner is new then the backup will be null, if the owner is new then the aggregate is new
        //if the backup is null but the owner is not new then this aggregate is new
        ObjectChangeSet initialChanges = builder.createObjectChangeSet(cloneAttribute, (UnitOfWorkChangeSet)owner.getUOWChangeSet(), (backupAttribute == null), session);
        ObjectChangeSet changeSet = builder.compareForChange(cloneAttribute, backupAttribute, (UnitOfWorkChangeSet)owner.getUOWChangeSet(), session);
        if (changeSet == null) {
            if (initialChanges.isNew()) {
                // This happens if original aggregate is of class A, the new aggregate
                // is of class B (B inherits from A) - and neither A nor B has any mapped attributes.
                // CR3664
                changeSet = initialChanges;
            } else {
                return null;// no change
            }
        }
        changeRecord.setChangedObject(changeSet);
        return changeRecord;
    }

    /**
     * INTERNAL:
     * Compare the attributes belonging to this mapping for the objects.
     */
    public boolean compareObjects(Object firstObject, Object secondObject, AbstractSession session) {
        return compareAttributeValues(getAttributeValueFromObject(firstObject), getAttributeValueFromObject(secondObject), session);
    }

    /**
     * INTERNAL:
     * Convert all the class-name-based settings in this mapping to actual class-based
     * settings. This method is used when converting a project that has been built
     * with class names to a project with classes.
     * @param classLoader 
     */
    @Override
    public void convertClassNamesToClasses(ClassLoader classLoader) {
        super.convertClassNamesToClasses(classLoader);
        
        if (getReferenceClassName() != null) {
            try {
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
                    try {
                        setReferenceClass((Class)AccessController.doPrivileged(new PrivilegedClassForName(getReferenceClassName(), true, classLoader)));
                    } catch (PrivilegedActionException exception) {
                        throw ValidationException.classNotFoundWhileConvertingClassNames(getReferenceClassName(), exception.getException());
                    }
                } else {
                    setReferenceClass(org.eclipse.persistence.internal.security.PrivilegedAccessHelper.getClassForName(getReferenceClassName(), true, classLoader));
                }
            } catch (ClassNotFoundException exc) {
                throw ValidationException.classNotFoundWhileConvertingClassNames(getReferenceClassName(), exc);
            }
        }
    }

    /**
     * INTERNAL:
     * Execute a descriptor event for the specified event code.
     */
    protected void executeEvent(int eventCode, ObjectLevelModifyQuery query) {
        ClassDescriptor referenceDescriptor = getReferenceDescriptor(query.getObject(), query.getSession());

        // PERF: Avoid events if no listeners.
        if (referenceDescriptor.getEventManager().hasAnyEventListeners()) {
            referenceDescriptor.getEventManager().executeEvent(new org.eclipse.persistence.descriptors.DescriptorEvent(eventCode, query));
        }
    }

    /**
     * INTERNAL:
     * An object has been serialized from the server to the remote client.
     * Replace the transient attributes of the remote value holders
     * with client-side objects.
     */
    protected void fixAttributeValue(Object attributeValue, Map objectDescriptors, Map processedObjects, ObjectLevelReadQuery query, DistributedSession session) {
        if (attributeValue == null) {
            return;
        }
        getObjectBuilder(attributeValue, query.getSession()).fixObjectReferences(attributeValue, objectDescriptors, processedObjects, query, session);
    }

    /**
     * INTERNAL:
     * An object has been serialized from the server to the remote client.
     * Replace the transient attributes of the remote value holders
     * with client-side objects.
     */
    public void fixObjectReferences(Object object, Map objectDescriptors, Map processedObjects, ObjectLevelReadQuery query, DistributedSession session) {
        Object attributeValue = getAttributeValueFromObject(object);
        fixAttributeValue(attributeValue, objectDescriptors, processedObjects, query, session);
    }

    /**
     * Return the appropriate attribute value.
     * This method is a hack to allow the aggregate collection
     * subclass to override....
     */
    protected Object getAttributeValueFromBackupClone(Object backupClone) {
        return getAttributeValueFromObject(backupClone);
    }

    /**
     * Convenience method
     */
    protected ObjectBuilder getObjectBuilderForClass(Class javaClass, AbstractSession session) {
        return getReferenceDescriptor(javaClass, session).getObjectBuilder();
    }

    /**
     * Convenience method
     */
    protected ObjectBuilder getObjectBuilder(Object attributeValue, AbstractSession session) {
        return getReferenceDescriptor(attributeValue, session).getObjectBuilder();
    }

    /**
     * Convenience method
     */
    protected DescriptorQueryManager getQueryManager(Object attributeValue, AbstractSession session) {
        return getReferenceDescriptor(attributeValue, session).getQueryManager();
    }

    /**
     * PUBLIC:
     * Returns the reference class
     */
    public Class getReferenceClass() {
        return referenceClass;
    }

    /**
     * INTERNAL:
     * Used by MW.
     */
    public String getReferenceClassName() {
        if ((referenceClassName == null) && (referenceClass != null)) {
            referenceClassName = referenceClass.getName();
        }
        return referenceClassName;
    }

    /**
     * INTERNAL:
     * Return the referenceDescriptor. This is a descriptor which is associated with the reference class.
     * NOTE: If you are looking for the descriptor for a specific aggregate object, use
     * #getReferenceDescriptor(Object). This will ensure you get the right descriptor if the object's
     * descriptor is part of an inheritance tree.
     */
    public ClassDescriptor getReferenceDescriptor() {
        return referenceDescriptor;
    }

    /**
     * INTERNAL:
     * For inheritance purposes.
     */
    protected ClassDescriptor getReferenceDescriptor(Class theClass, AbstractSession session) {
        if (this.referenceDescriptor.getJavaClass() == theClass) {
            return this.referenceDescriptor;
        }

        ClassDescriptor subDescriptor = session.getDescriptor(theClass);
        if (subDescriptor == null) {
            throw DescriptorException.noSubClassMatch(theClass, this);
        } else {
            return subDescriptor;
        }
    }

    /**
     * Convenience method
     */
    protected ClassDescriptor getReferenceDescriptor(Object attributeValue, AbstractSession session) {
        if (attributeValue == null) {
            return this.referenceDescriptor;
        } else {
            return getReferenceDescriptor(attributeValue.getClass(), session);
        }
    }

    /**
     * INTERNAL:
     * Initialize the reference descriptor.
     */
    public void initialize(AbstractSession session) throws DescriptorException {
        super.initialize(session);

        if (getReferenceClass() == null) {
            throw DescriptorException.referenceClassNotSpecified(this);
        }

        setReferenceDescriptor(session.getDescriptor(getReferenceClass()));

        ClassDescriptor refDescriptor = this.getReferenceDescriptor();
        if (refDescriptor == null) {
            session.getIntegrityChecker().handleError(DescriptorException.descriptorIsMissing(getReferenceClass().getName(), this));
            return;
        }
        if (refDescriptor.isDescriptorTypeAggregate()) {
            refDescriptor.checkInheritanceTreeAggregateSettings(session, this);
        } else {
            session.getIntegrityChecker().handleError(DescriptorException.referenceDescriptorIsNotAggregate(getReferenceClass().getName(), this));
        }
    }

    /**
     * INTERNAL:
     * Related mapping should implement this method to return true.
     */
    public boolean isAggregateMapping() {
        return true;
    }

    /**
     * INTERNAL:
     * Iterate on the appropriate attribute value.
     */
    public void iterate(DescriptorIterator iterator) {
        iterateOnAttributeValue(iterator, getAttributeValueFromObject(iterator.getVisitedParent()));
    }

    /**
     * Iterate on the specified attribute value.
     */
    protected void iterateOnAttributeValue(DescriptorIterator iterator, Object attributeValue) {
        iterator.iterateForAggregateMapping(attributeValue, this, getReferenceDescriptor(attributeValue, iterator.getSession()));
    }
    
    /**
     * Force instantiation of the load group.
     */
    @Override
    public void load(final Object object, AttributeItem item, final AbstractSession session) {
        if (item.getGroup() != null) {
            Object value = getAttributeValueFromObject(object);
            getObjectBuilder(value, session).load(value, item.getGroup(), session);
        }
    }
    
    /**
     * Merge the attribute values.
     */
    protected void mergeAttributeValue(Object targetAttributeValue, boolean isTargetUnInitialized, Object sourceAttributeValue, MergeManager mergeManager, AbstractSession targetSession) {
        // don't merge read-only attributes
        if (mergeManager.getSession().isClassReadOnly(sourceAttributeValue.getClass())) {
            return;
        }
        if (mergeManager.getSession().isClassReadOnly(targetAttributeValue.getClass())) {
            return;
        }

        // Toggle change tracking during the merge.
        ClassDescriptor descriptor = getReferenceDescriptor(sourceAttributeValue, mergeManager.getSession());
        descriptor.getObjectChangePolicy().dissableEventProcessing(targetAttributeValue);
        try {
            descriptor.getObjectBuilder().mergeIntoObject(targetAttributeValue, isTargetUnInitialized, sourceAttributeValue, mergeManager, targetSession);
        } finally {            
            descriptor.getObjectChangePolicy().enableEventProcessing(targetAttributeValue);
        }
    }

    /**
     * INTERNAL:
     * Merge changes from the source to the target object.
     * With aggregates the merge must cascade to the object changes for the aggregate object
     * because aggregate objects have no identity outside of themselves.
     * The actual aggregate object does not need to be replaced, because even if the clone references
     * another aggregate it appears the same to TopLink
     */
    public void mergeChangesIntoObject(Object target, ChangeRecord changeRecord, Object source, MergeManager mergeManager, AbstractSession targetSession) {
        ObjectChangeSet aggregateChangeSet = (ObjectChangeSet)((AggregateChangeRecord)changeRecord).getChangedObject();
        if (aggregateChangeSet == null) {// the change was to set the value to null
            setAttributeValueInObject(target, null);
            return;
        }

        Object sourceAggregate = null;
        if (source != null) {
            sourceAggregate = getAttributeValueFromObject(source);
        }
        ObjectBuilder objectBuilder = getObjectBuilderForClass(aggregateChangeSet.getClassType(mergeManager.getSession()), mergeManager.getSession());
        //Bug#4719341  Always obtain aggregate attribute value from the target object regardless of new or not
        Object targetAggregate = getAttributeValueFromObject(target);
        boolean wasOriginalNull = false;
        if (targetAggregate == null || targetAggregate == sourceAggregate) {
            targetAggregate = objectBuilder.buildNewInstance();
            wasOriginalNull = true;
        } else {
        	//bug 205939 - use the type from the changeset to determine if a new aggregate instance
        	//is needed because of a class change.  The old way of using the sourceAggregate will not
        	//work on a remote system after cache sync because the sourceAggregate will not be available
            if (aggregateChangeSet.getClassType(mergeManager.getSession()) != targetAggregate.getClass()) {
                targetAggregate = objectBuilder.buildNewInstance();
                wasOriginalNull = true;
            }
        }
        objectBuilder.mergeChangesIntoObject(targetAggregate, aggregateChangeSet, sourceAggregate, mergeManager, targetSession,false, wasOriginalNull);
        setAttributeValueInObject(target, targetAggregate);
    }

    /**
     * INTERNAL:
     * Merge changes from the source to the target object. This merge is only called when a changeSet for the target
     * does not exist or the target is uninitialized
     */
    public void mergeIntoObject(Object target, boolean isTargetUnInitialized, Object source, MergeManager mergeManager, AbstractSession targetSession) {
        Object sourceAttributeValue = getAttributeValueFromObject(source);
        if (sourceAttributeValue == null) {
            setAttributeValueInObject(target, null);
            return;
        }

        Object targetAttributeValue = getAttributeValueFromObject(target);
        boolean originalWasNull = targetAttributeValue == null;
        if (targetAttributeValue == null || targetAttributeValue == sourceAttributeValue || !targetAttributeValue.getClass().equals(sourceAttributeValue.getClass())) {
            // avoid null-pointer/nothing to merge to - create a new instance
            // (a new clone cannot be used as all changes must be merged)
            targetAttributeValue = buildNewMergeInstanceOf(sourceAttributeValue, mergeManager.getSession());
            mergeAttributeValue(targetAttributeValue, true, sourceAttributeValue, mergeManager, targetSession);
            // setting new instance so fire event as if set was called by user.
            // this call will eventually get passed to updateChangeRecord which will 
            //ensure this new aggregates is fully initialized with listeners.
            // If merge into the unit of work, must only merge and raise the event is the value changed.
            if ((mergeManager.shouldMergeCloneIntoWorkingCopy() || mergeManager.shouldMergeCloneWithReferencesIntoWorkingCopy())  && !mergeManager.isForRefresh()) {
                this.descriptor.getObjectChangePolicy().raiseInternalPropertyChangeEvent(target, getAttributeName(), getAttributeValueFromObject(target), targetAttributeValue);
            }
            
        } else {
            mergeAttributeValue(targetAttributeValue, isTargetUnInitialized, sourceAttributeValue, mergeManager, targetSession);
        }
        if(this.descriptor.hasFetchGroupManager()) {
            FetchGroup sourceFetchGroup = this.descriptor.getFetchGroupManager().getObjectFetchGroup(source);
            FetchGroup targetFetchGroup = this.descriptor.getFetchGroupManager().getObjectFetchGroup(target);
            if(targetFetchGroup != null) {
                if(!targetFetchGroup.isSupersetOf(sourceFetchGroup)) {
                    targetFetchGroup.onUnfetchedAttribute((FetchGroupTracker)target, null);
                }
            } else if (originalWasNull && sourceFetchGroup != null){
                    this.descriptor.getFetchGroupManager().setObjectFetchGroup(target, sourceFetchGroup, targetSession);
            }
        }

        // Must re-set variable to allow for set method to re-morph changes.
        setAttributeValueInObject(target, targetAttributeValue);
    }

    /**
     * INTERNAL:
     * The message is passed to its reference class descriptor.
     */
    public void postDelete(DeleteObjectQuery query) throws DatabaseException, OptimisticLockException {
        if (!isReadOnly()) {
            postDeleteAttributeValue(query, getAttributeValueFromObject(query.getObject()));
        }
    }

    /**
     * INTERNAL:
     * The message is passed to its reference class descriptor.
     */
    public void postDeleteAttributeValue(DeleteObjectQuery query, Object attributeValue) throws DatabaseException, OptimisticLockException {
        if (attributeValue == null) {
            return;
        }
        // PERF: Avoid for simple aggregates.
        ClassDescriptor descriptor = getReferenceDescriptor(attributeValue, query.getSession());
        if (descriptor.getObjectBuilder().isSimple() && !descriptor.getEventManager().hasAnyEventListeners()) {
            return;
        }
        DeleteObjectQuery aggregateQuery = buildAggregateDeleteQuery(query, attributeValue);
        descriptor.getQueryManager().postDelete(aggregateQuery);
        executeEvent(DescriptorEventManager.PostDeleteEvent, aggregateQuery);
    }

    /**
     * INTERNAL:
     * The message is passed to its reference class descriptor.
     */
    public void postInsert(WriteObjectQuery query) throws DatabaseException, OptimisticLockException {
        if (!isReadOnly()) {
            postInsertAttributeValue(query, getAttributeValueFromObject(query.getObject()));
        }
    }

    /**
     * INTERNAL:
     * The message is passed to its reference class descriptor.
     */
    public void postInsertAttributeValue(WriteObjectQuery query, Object attributeValue) throws DatabaseException, OptimisticLockException {
        if (attributeValue == null) {
            return;
        }
        // PERF: Avoid for simple aggregates.
        ClassDescriptor descriptor = getReferenceDescriptor(attributeValue, query.getSession());
        if (descriptor.getObjectBuilder().isSimple() && !descriptor.getEventManager().hasAnyEventListeners()) {
            return;
        }
        WriteObjectQuery aggregateQuery = buildAggregateWriteQuery(query, attributeValue);
        descriptor.getQueryManager().postInsert(aggregateQuery);
        executeEvent(DescriptorEventManager.PostInsertEvent, aggregateQuery);
        // aggregates do not actually use a query to write to the database so the post write must be called here
        executeEvent(DescriptorEventManager.PostWriteEvent, aggregateQuery);
    }

    /**
     * INTERNAL:
     * The message is passed to its reference class descriptor.
     */
    public void postUpdate(WriteObjectQuery query) throws DatabaseException, OptimisticLockException {
        if (!isReadOnly()) {
            postUpdateAttributeValue(query, getAttributeValueFromObject(query.getObject()));
        }
    }

    /**
     * INTERNAL:
     * The message is passed to its reference class descriptor.
     */
    public void postUpdateAttributeValue(WriteObjectQuery query, Object attributeValue) throws DatabaseException, OptimisticLockException {
        if (attributeValue == null) {
            return;
        }
        // PERF: Avoid for simple aggregates.
        AbstractSession session = query.getSession();
        ClassDescriptor descriptor = getReferenceDescriptor(attributeValue, session);
        if (descriptor.getObjectBuilder().isSimple() && !descriptor.getEventManager().hasAnyEventListeners()) {
            return;
        }
        ObjectChangeSet changeSet = null;
        UnitOfWorkChangeSet uowChangeSet = null;
        if (session.isUnitOfWork() && (((UnitOfWorkImpl)session).getUnitOfWorkChangeSet() != null)) {
            uowChangeSet = (UnitOfWorkChangeSet)((UnitOfWorkImpl)session).getUnitOfWorkChangeSet();
            changeSet = (ObjectChangeSet)uowChangeSet.getObjectChangeSetForClone(attributeValue);
        }
        WriteObjectQuery aggregateQuery = buildAggregateWriteQuery(query, attributeValue);
        aggregateQuery.setObjectChangeSet(changeSet);
        descriptor.getQueryManager().postUpdate(aggregateQuery);
        executeEvent(DescriptorEventManager.PostUpdateEvent, aggregateQuery);
        // aggregates do not actually use a query to write to the database so the post write must be called here
        executeEvent(DescriptorEventManager.PostWriteEvent, aggregateQuery);
    }

    /**
     * INTERNAL:
     * The message is passed to its reference class descriptor.
     */
    public void preDelete(DeleteObjectQuery query) throws DatabaseException, OptimisticLockException {
        if (!isReadOnly()) {
            preDeleteAttributeValue(query, getAttributeValueFromObject(query.getObject()));
        }
    }

    /**
     * INTERNAL:
     * The message is passed to its reference class descriptor.
     */
    public void preDeleteAttributeValue(DeleteObjectQuery query, Object attributeValue) throws DatabaseException, OptimisticLockException {
        if (attributeValue == null) {
            return;
        }
        // PERF: Avoid for simple aggregates.
        AbstractSession session = query.getSession();
        ClassDescriptor descriptor = getReferenceDescriptor(attributeValue, session);
        if (descriptor.getObjectBuilder().isSimple() && !descriptor.getEventManager().hasAnyEventListeners()) {
            return;
        }
        DeleteObjectQuery aggregateQuery = buildAggregateDeleteQuery(query, attributeValue);
        executeEvent(DescriptorEventManager.PreDeleteEvent, aggregateQuery);
        descriptor.getQueryManager().preDelete(aggregateQuery);
    }

    /**
     * INTERNAL:
     * The message is passed to its reference class descriptor.
     */
    public void preInsert(WriteObjectQuery query) throws DatabaseException, OptimisticLockException {
        if (!isReadOnly()) {
            preInsertAttributeValue(query, getAttributeValueFromObject(query.getObject()));
        }
    }

    /**
     * INTERNAL:
     * The message is passed to its reference class descriptor.
     */
    public void preInsertAttributeValue(WriteObjectQuery query, Object attributeValue) throws DatabaseException, OptimisticLockException {
        if (attributeValue == null) {
            return;
        }
        // PERF: Avoid for simple aggregates.
        AbstractSession session = query.getSession();
        ClassDescriptor descriptor = getReferenceDescriptor(attributeValue, session);
        if (descriptor.getObjectBuilder().isSimple() && !descriptor.getEventManager().hasAnyEventListeners()) {
            return;
        }
        WriteObjectQuery aggregateQuery = buildAggregateWriteQuery(query, attributeValue);
        ObjectChangeSet changeSet = null;
        if (session.isUnitOfWork() && (((UnitOfWorkImpl)session).getUnitOfWorkChangeSet() != null)) {
            UnitOfWorkChangeSet uowChangeSet = (UnitOfWorkChangeSet)((UnitOfWorkImpl)session).getUnitOfWorkChangeSet();
            changeSet = (ObjectChangeSet)uowChangeSet.getObjectChangeSetForClone(aggregateQuery.getObject());
        }
        aggregateQuery.setObjectChangeSet(changeSet);
        // aggregates do not actually use a query to write to the database so the pre-write must be called here
        if (changeSet == null) {// then we didn't fire events at calculations
            executeEvent(DescriptorEventManager.PreWriteEvent, aggregateQuery);
            executeEvent(DescriptorEventManager.PreInsertEvent, aggregateQuery);
        }
        descriptor.getQueryManager().preInsert(aggregateQuery);
    }

    /**
     * INTERNAL:
     * The message is passed to its reference class descriptor.
     */
    public void preUpdate(WriteObjectQuery query) throws DatabaseException, OptimisticLockException {
        if (!isReadOnly()) {
            preUpdateAttributeValue(query, getAttributeValueFromObject(query.getObject()));
        }
    }

    /**
     * INTERNAL:
     * The message is passed to its reference class descriptor.
     */
    public void preUpdateAttributeValue(WriteObjectQuery query, Object attributeValue) throws DatabaseException, OptimisticLockException {
        if (attributeValue == null) {
            return;
        }
        // PERF: Avoid for simple aggregates.
        AbstractSession session = query.getSession();
        ClassDescriptor descriptor = getReferenceDescriptor(attributeValue, session);
        if (descriptor.getObjectBuilder().isSimple() && !descriptor.getEventManager().hasAnyEventListeners()) {
            return;
        }
        WriteObjectQuery aggregateQuery = buildAggregateWriteQuery(query, attributeValue);
        ObjectChangeSet changeSet = null;
        UnitOfWorkChangeSet uowChangeSet = null;
        if (session.isUnitOfWork() && (((UnitOfWorkImpl)session).getUnitOfWorkChangeSet() != null)) {
            uowChangeSet = (UnitOfWorkChangeSet)((UnitOfWorkImpl)session).getUnitOfWorkChangeSet();
            changeSet = (ObjectChangeSet)uowChangeSet.getObjectChangeSetForClone(aggregateQuery.getObject());
        }

        aggregateQuery.setObjectChangeSet(changeSet);
        // aggregates do not actually use a query to write to the database so the pre-write must be called here
        if (changeSet == null) {// then we didn't fire events at calculations
            executeEvent(DescriptorEventManager.PreWriteEvent, aggregateQuery);
            executeEvent(DescriptorEventManager.PreUpdateEvent, aggregateQuery);
        }
        descriptor.getQueryManager().preUpdate(aggregateQuery);
    }

    /**
     * INTERNAL:
     * Once a descriptor is serialized to the remote session, all its mappings and reference descriptors are traversed.
     * Usually the mappings are initialized and the serialized reference descriptors are replaced with local descriptors
     * if they already exist in the remote session.
     */
    public void remoteInitialization(DistributedSession session) {
        super.remoteInitialization(session);
        ClassDescriptor refDescriptor = getReferenceDescriptor();

        if (session.hasCorrespondingDescriptor(refDescriptor)) {
            ClassDescriptor correspondingDescriptor = session.getDescriptorCorrespondingTo(refDescriptor);
            setReferenceDescriptor(correspondingDescriptor);
        } else {
            session.privilegedAddDescriptor(refDescriptor);
            refDescriptor.remoteInitialization(session);
        }
    }

    /**
     * PUBLIC:
     * This is a reference class whose instances this mapping will store in the domain objects.
     */
    public void setReferenceClass(Class aClass) {
        referenceClass = aClass;
    }

    /**
     * INTERNAL:
     * Used by MW.
     */
    public void setReferenceClassName(String aClassName) {
        referenceClassName = aClassName;
    }

    /**
     * INTERNAL:
     * Set the referenceDescriptor. This is a descriptor which is associated with
     * the reference class.
     */
    protected void setReferenceDescriptor(ClassDescriptor aDescriptor) {
        referenceDescriptor = aDescriptor;
    }

    /**
     * INTERNAL:
     * Either create a new change record or update the change record with the new value.
     * This is used by attribute change tracking.
     */
    public void updateChangeRecord(Object sourceClone, Object newValue, Object oldValue, ObjectChangeSet objectChangeSet, UnitOfWorkImpl uow) throws DescriptorException {
        //This method will be called when either the referenced aggregate has 
        //been changed or a component of the referenced aggregate has been changed
        //this case is determined by the value of the sourceClone 
        boolean isNewRecord = false;
        AggregateChangeRecord changeRecord = (AggregateChangeRecord)objectChangeSet.getChangesForAttributeNamed(this.getAttributeName());
        if (changeRecord == null){
            changeRecord = new AggregateChangeRecord(objectChangeSet);
            changeRecord.setAttribute(this.getAttributeName());
            changeRecord.setMapping(this);
            objectChangeSet.addChange(changeRecord);
            isNewRecord = true;
        }
        
        if ( sourceClone.getClass().equals(objectChangeSet.getClassType(uow)) ) {
            if (isNewRecord) {
                changeRecord.setOldValue(oldValue);
            }
            // event was fired on the parent to the aggregate, the attribute value changed.
            ClassDescriptor referenceDescriptor = getReferenceDescriptor(newValue, uow);
            if ( newValue == null ) { // attribute set to null
                changeRecord.setChangedObject(null);
                if (referenceDescriptor.getObjectChangePolicy().isAttributeChangeTrackingPolicy()){
                    if(((ChangeTracker)oldValue)._persistence_getPropertyChangeListener() != null) {
                        //need to detach listener
                        ((AggregateAttributeChangeListener)((ChangeTracker)oldValue)._persistence_getPropertyChangeListener()).setParentListener(null);
                    }
                }
                return;
            }else{ // attribute set to new aggregate
                UnitOfWorkChangeSet uowChangeSet = (UnitOfWorkChangeSet)objectChangeSet.getUOWChangeSet();
                //force comparison change detection to build changeset.
                ObjectChangeSet aggregateChangeSet = (ObjectChangeSet)uowChangeSet.getObjectChangeSetForClone(newValue);
                if (aggregateChangeSet != null) {
                    aggregateChangeSet.clear(true); // old differences must be thrown away because difference is between old value and new value
                }
                //make sure the listener is initialized
                if (referenceDescriptor.getObjectChangePolicy().isAttributeChangeTrackingPolicy()){
                    if(oldValue != null && ((ChangeTracker)oldValue)._persistence_getPropertyChangeListener() != null) {
                        //need to detach listener
                        ((AggregateAttributeChangeListener)((ChangeTracker)oldValue)._persistence_getPropertyChangeListener()).setParentListener(null);
                    }
                    //need to attach new listener.
                    AggregateAttributeChangeListener newListener = (AggregateAttributeChangeListener)((ChangeTracker)newValue)._persistence_getPropertyChangeListener();
                    if (newListener == null){
                        newListener = new AggregateAttributeChangeListener(referenceDescriptor, uow, ((AttributeChangeListener)((ChangeTracker)sourceClone)._persistence_getPropertyChangeListener()), this.getAttributeName(), newValue);
                        ((ChangeTracker)newValue)._persistence_setPropertyChangeListener(newListener);
                    }
                    newListener.setParentListener((AttributeChangeListener)((ChangeTracker)sourceClone)._persistence_getPropertyChangeListener());
                    if (changeRecord.getChangedObject() != null && changeRecord.getChangedObject().hasChanges()) {
                        // the oldValue has been already changed - get the original oldValue.
                        oldValue = changeRecord.getOldValue();
                    }
                    if (oldValue != null) {
                        if(referenceDescriptor != getReferenceDescriptor(oldValue, uow)) {
                            // oldValue and newValue belong to different types - have to start from scratch.
                            oldValue = null;
                        }
                    }
                }
                //force comparison change detection to build changeset.
                changeRecord.setChangedObject(referenceDescriptor.getObjectChangePolicy().createObjectChangeSetThroughComparison(newValue,oldValue, uowChangeSet, (oldValue == null), uow, referenceDescriptor));
                // process nested aggregates
                for(DatabaseMapping mapping : referenceDescriptor.getMappings()) {
                    if(mapping.isAggregateObjectMapping()) {
                        Object nestedNewValue = mapping.getAttributeValueFromObject(newValue);
                        Object nestedOldValue = null;
                        if(oldValue != null) {
                            nestedOldValue = mapping.getAttributeValueFromObject(oldValue);
                        }
                        mapping.updateChangeRecord(newValue, nestedNewValue, nestedOldValue, (org.eclipse.persistence.internal.sessions.ObjectChangeSet)changeRecord.getChangedObject(), uow);
                    }
                }
                referenceDescriptor.getObjectChangePolicy().setChangeSetOnListener((ObjectChangeSet)changeRecord.getChangedObject(), newValue);
            }
        } else {
            //a value was set on the aggregate but the aggregate was not changed.
            if (referenceDescriptor.getObjectChangePolicy().isAttributeChangeTrackingPolicy()){
                //The aggregate that is referenced is Attribute Change Tracked as well.
                changeRecord.setChangedObject(((AggregateAttributeChangeListener)((ChangeTracker)sourceClone)._persistence_getPropertyChangeListener()).getObjectChangeSet());
            } else {
                // not tracked at attribute level, lets force build a changeset then.
                changeRecord.setChangedObject(referenceDescriptor.getObjectChangePolicy().createObjectChangeSetThroughComparison(sourceClone, null, (UnitOfWorkChangeSet)objectChangeSet.getUOWChangeSet(), true, uow, referenceDescriptor));
            }
        }
    }
    
    /**
     * INTERNAL:
     * Return whether the specified object and all its components have been deleted.
     */
    public boolean verifyDelete(Object object, AbstractSession session) throws DatabaseException {
        return verifyDeleteOfAttributeValue(getAttributeValueFromObject(object), session);
    }

    /**
     * INTERNAL:
     * Return whether the specified object and all its components have been deleted.
     */
    protected boolean verifyDeleteOfAttributeValue(Object attributeValue, AbstractSession session) throws DatabaseException {
        if (attributeValue == null) {
            return true;
        }
        for (Enumeration mappings = getReferenceDescriptor(attributeValue, session).getMappings().elements();
                 mappings.hasMoreElements();) {
            DatabaseMapping mapping = (DatabaseMapping)mappings.nextElement();
            if (!mapping.verifyDelete(attributeValue, session)) {
                return false;
            }
        }
        return true;
    }
}
