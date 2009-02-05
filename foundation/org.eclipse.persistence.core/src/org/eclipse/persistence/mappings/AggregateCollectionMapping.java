/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
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
package org.eclipse.persistence.mappings;

import java.util.*;

import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.internal.descriptors.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.identitymaps.*;
import org.eclipse.persistence.internal.queries.*;
import org.eclipse.persistence.internal.sessions.*;
import org.eclipse.persistence.mappings.foundation.MapComponentMapping;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventManager;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.remote.DistributedSession;

/**
 * <p><b>Purpose</b>: The aggregate collection mapping is used to represent the aggregate relationship between a single
 * source object and a collection of target objects. The target objects cannot exist without the existence of the
 * source object (privately owned)
 * Unlike the normal aggregate mapping, there is a target table being mapped from the target objects.
 * Unlike normal 1:m mapping, there is no 1:1 back reference mapping, as foreign key constraints have been resolved by the aggregation.
 *
 * @author King (Yaoping) Wang
 * @since TOPLink/Java 3.0
 */
public class AggregateCollectionMapping extends CollectionMapping implements RelationalMapping, MapComponentMapping, EmbeddableMapping {

    /** This is a key in the target table which is a foreign key in the target table. */
    protected transient Vector<DatabaseField> targetForeignKeyFields;

    /** This is a primary key in the source table that is used as foreign key in the target table */
    protected transient Vector<DatabaseField> sourceKeyFields;

    /** Foreign keys in the target table to the related keys in the source table */
    protected transient Map<DatabaseField, DatabaseField> targetForeignKeyToSourceKeys;

    /** Map the name of a field in the aggregate collection descriptor to a field in the actual table specified in the mapping. */
    protected transient Map<String, String> aggregateToSourceFieldNames;

    /** Map the name of an attribute of the reference descriptor mapped with AggregateCollectionMapping to aggregateToSourceFieldNames
     * that should be applied to this mapping.
     */  
    protected transient Map<String, Map<String, String>> nestedAggregateToSourceFieldNames;
    
    /** In RemoteSession case the mapping needs the reference descriptor serialized from the server, 
     * but referenceDescriptor attribute defined as transient in the superclass. To overcome that
     * in non-remote case referenceDescriptor is assigned to remoteReferenceDescriptor; in remote - another way around.
     */  
    protected ClassDescriptor remoteReferenceDescriptor;


    /**
     * PUBLIC:
     * Default constructor.
     */
    public AggregateCollectionMapping() {
        this.targetForeignKeyToSourceKeys = new HashMap(5);
        this.sourceKeyFields = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(1);
        this.targetForeignKeyFields = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(1);
        this.deleteAllQuery = new DeleteAllQuery();
        //aggregates should always cascade all operations
        this.setCascadeAll(true);
    }

    /**
     * INTERNAL:
     */
    public boolean isRelationalMapping() {
        return true;
    }

    /**
     * PUBLIC:
     * Maps a field name in the aggregate descriptor
     * to a field name in the source table.
     */
    public void addFieldNameTranslation(String sourceFieldName, String aggregateFieldName) {
        if(aggregateToSourceFieldNames == null) {
            aggregateToSourceFieldNames = new HashMap(5); 
        }
        aggregateToSourceFieldNames.put(aggregateFieldName, sourceFieldName);
    }

    /**
     * PUBLIC:
     * Maps a field name in the aggregate descriptor
     * to a field name in the source table.
     */
    public void addFieldNameTranslations(Map<String, String> map) {
        if(aggregateToSourceFieldNames == null) {
            aggregateToSourceFieldNames = map; 
        } else {
            aggregateToSourceFieldNames.putAll(map);
        }
    }

    /**
     * PUBLIC:
     * Map the name of an attribute of the reference descriptor mapped with AggregateCollectionMapping to aggregateToSourceFieldNames
     * that should be applied to this mapping.
     */
    public void addNestedFieldNameTranslation(String attributeName, String sourceFieldName, String aggregateFieldName) {
        if(nestedAggregateToSourceFieldNames == null) {
            nestedAggregateToSourceFieldNames = new HashMap<String, Map<String, String>>(5); 
        }
        Map<String, String> attributeFieldNameTranslation = nestedAggregateToSourceFieldNames.get(attributeName);
        if(attributeFieldNameTranslation == null) {
            attributeFieldNameTranslation = new HashMap<String, String>(5);
            nestedAggregateToSourceFieldNames.put(attributeName, attributeFieldNameTranslation);
        }
        attributeFieldNameTranslation.put(aggregateFieldName, sourceFieldName);
    }

    /**
     * PUBLIC:
     * Map the name of an attribute of the reference descriptor mapped with AggregateCollectionMapping to aggregateToSourceFieldNames
     * that should be applied to this mapping.
     */
    public void addNestedFieldNameTranslations(String attributeName, Map<String, String> map) {
        if(nestedAggregateToSourceFieldNames == null) {
            nestedAggregateToSourceFieldNames = new HashMap<String, Map<String, String>>(5); 
        }
        Map<String, String> attributeFieldNameTranslation = nestedAggregateToSourceFieldNames.get(attributeName);
        if(attributeFieldNameTranslation == null) {
            nestedAggregateToSourceFieldNames.put(attributeName, map);
        } else {
            attributeFieldNameTranslation.putAll(map);
        }
    }

    /**
     * PUBLIC:
     * Define the target foreign key relationship in the 1-M aggregate collection mapping.
     * Both the target foreign key field and the source primary key field must be specified.
     */
    public void addTargetForeignKeyField(DatabaseField targetForeignKey, DatabaseField sourceKey) {
        getTargetForeignKeyFields().addElement(targetForeignKey);
        getSourceKeyFields().addElement(sourceKey);
    }
    
    /**
     * PUBLIC:
     * Define the target foreign key relationship in the 1-M aggregate collection mapping.
     * Both the target foreign key field name and the source primary key field name must be specified.
     */
    public void addTargetForeignKeyFieldName(String targetForeignKey, String sourceKey) {
        addTargetForeignKeyField(new DatabaseField(targetForeignKey), new DatabaseField(sourceKey));
    }

    /**
     * INTERNAL:
     * Used during building the backup shallow copy to copy the vector without re-registering the target objects.
     */
    public Object buildBackupCloneForPartObject(Object attributeValue, Object clone, Object backup, UnitOfWorkImpl unitOfWork) {
        ContainerPolicy containerPolicy = getContainerPolicy();
        if (attributeValue == null) {
            return containerPolicy.containerInstance(1);
        }

        Object clonedAttributeValue = containerPolicy.containerInstance(containerPolicy.sizeFor(attributeValue));
        synchronized (attributeValue) {
            for (Object valuesIterator = containerPolicy.iteratorFor(attributeValue);
                     containerPolicy.hasNext(valuesIterator);) {
                Object wrappedElement = containerPolicy.nextEntry(valuesIterator, unitOfWork);
                Object cloneValue = buildElementBackupClone(containerPolicy.unwrapIteratorResult(wrappedElement), unitOfWork);
                containerPolicy.addInto(containerPolicy.keyFromIterator(valuesIterator), cloneValue, clonedAttributeValue, unitOfWork);
            }
        }
        return clonedAttributeValue;
    }

    /**
     * INTERNAL:
     * Require for cloning, the part must be cloned.
     * Ignore the objects, use the attribute value.
     * this is identical to the super class except that the element must be added to the new
     * aggregates collection so that the referenced objects will be cloned correctly
     */
    public Object buildCloneForPartObject(Object attributeValue, Object original, Object clone, UnitOfWorkImpl unitOfWork, boolean isExisting) {
        ContainerPolicy containerPolicy = getContainerPolicy();
        if (attributeValue == null) {
            return containerPolicy.containerInstance(1);
        }
        Object clonedAttributeValue = containerPolicy.containerInstance(containerPolicy.sizeFor(attributeValue));

        // I need to synchronize here to prevent the collection from changing while I am cloning it.
        // This will occur when I am merging into the cache and I am instantiating a UOW valueHolder at the same time
        // I can not synchronize around the clone, as this will cause deadlocks, so I will need to copy the collection then create the clones
        // I will use a temporary collection to help speed up the process
        Object temporaryCollection = null;
        synchronized (attributeValue) {
            temporaryCollection = containerPolicy.cloneFor(attributeValue);
        }
        for (Object valuesIterator = containerPolicy.iteratorFor(temporaryCollection);
                 containerPolicy.hasNext(valuesIterator);) {
            Object wrappedElement = containerPolicy.nextEntry(valuesIterator, unitOfWork);
            Object originalElement = containerPolicy.unwrapIteratorResult(wrappedElement);

            //need to add to aggregate list in the case that there are related objects.
            if (unitOfWork.isOriginalNewObject(original)) {
                unitOfWork.addNewAggregate(originalElement);
            }
            Object cloneValue = buildElementClone(originalElement, unitOfWork, isExisting);
            containerPolicy.addInto(containerPolicy.keyFromIterator(valuesIterator), cloneValue, clonedAttributeValue, unitOfWork);
        }
        return clonedAttributeValue;
    }

    /**
     * INTERNAL:
     * Clone the aggregate collection, if necessary.
     */
    protected Object buildElementBackupClone(Object element, UnitOfWorkImpl unitOfWork) {
        // Do not clone for read-only.
        if (unitOfWork.isClassReadOnly(element.getClass(), getReferenceDescriptor())) {
            return element;
        }

        ClassDescriptor aggregateDescriptor = getReferenceDescriptor(element.getClass(), unitOfWork);
        Object clonedElement = aggregateDescriptor.getObjectBuilder().buildBackupClone(element, unitOfWork);

        return clonedElement;
    }

    /**
     * INTERNAL:
     * Clone the aggregate collection, if necessary.
     */
    public Object buildElementClone(Object element, UnitOfWorkImpl unitOfWork, boolean isExisting) {
        // Do not clone for read-only.
        if (unitOfWork.isClassReadOnly(element.getClass(), getReferenceDescriptor())) {
            return element;
        }

        ClassDescriptor aggregateDescriptor = getReferenceDescriptor(element.getClass(), unitOfWork);

        // bug 2612602 as we are building the working copy make sure that we call to correct clone method.
        Object clonedElement = aggregateDescriptor.getObjectBuilder().instantiateWorkingCopyClone(element, unitOfWork);
        aggregateDescriptor.getObjectBuilder().populateAttributesForClone(element, clonedElement, unitOfWork);
        // CR 4155 add the originals to the UnitOfWork so that we can find it later in the merge
        // as aggregates have no identity.  If we don't do this we will loose indirection information.
        unitOfWork.getCloneToOriginals().put(clonedElement, element);
        return clonedElement;
    }

    /**
     * INTERNAL:
     * Cascade discover and persist new objects during commit.
     */
    public void cascadeDiscoverAndPersistUnregisteredNewObjects(Object object, Map newObjects, Map unregisteredExistingObjects, Map visitedObjects, UnitOfWorkImpl uow) {
        //aggregate objects are not registered but their mappings should be.
        Object cloneAttribute = null;
        cloneAttribute = getAttributeValueFromObject(object);
        if ((cloneAttribute == null) || (!getIndirectionPolicy().objectIsInstantiated(cloneAttribute))) {
            return;
        }

        ObjectBuilder builder = null;
        ContainerPolicy cp = getContainerPolicy();
        Object cloneObjectCollection = null;
        cloneObjectCollection = getRealCollectionAttributeValueFromObject(object, uow);
        Object cloneIter = cp.iteratorFor(cloneObjectCollection);
        while (cp.hasNext(cloneIter)) {
            Object nextObject = cp.next(cloneIter, uow);
            if (nextObject != null) {
                builder = getReferenceDescriptor(nextObject.getClass(), uow).getObjectBuilder();
                builder.cascadeDiscoverAndPersistUnregisteredNewObjects(nextObject, newObjects, unregisteredExistingObjects, visitedObjects, uow);
            }
        }
    }
    
    /**
     * INTERNAL:
     * Cascade registerNew for Create through mappings that require the cascade
     */
    public void cascadeRegisterNewIfRequired(Object object, UnitOfWorkImpl uow, Map visitedObjects){
        //aggregate objects are not registered but their mappings should be.
        Object cloneAttribute = null;
        cloneAttribute = getAttributeValueFromObject(object);
        if ((cloneAttribute == null) || (!getIndirectionPolicy().objectIsInstantiated(cloneAttribute))) {
            return;
        }

        ObjectBuilder builder = null;
        ContainerPolicy cp = getContainerPolicy();
        Object cloneObjectCollection = null;
        cloneObjectCollection = getRealCollectionAttributeValueFromObject(object, uow);
        Object cloneIter = cp.iteratorFor(cloneObjectCollection);
        while (cp.hasNext(cloneIter)) {
            Object nextObject = cp.next(cloneIter, uow);
            if (nextObject != null && (! visitedObjects.containsKey(nextObject))){
                visitedObjects.put(nextObject, nextObject);
                builder = getReferenceDescriptor(nextObject.getClass(), uow).getObjectBuilder();
                builder.cascadeRegisterNewForCreate(nextObject, uow, visitedObjects);
            }
        }
    }

    /**
     * INTERNAL:
     * Cascade registerNew for Create through mappings that require the cascade
     */
    public void cascadePerformRemoveIfRequired(Object object, UnitOfWorkImpl uow, Map visitedObjects){
        //aggregate objects are not registered but their mappings should be.
        Object cloneAttribute = null;
        cloneAttribute = getAttributeValueFromObject(object);
        if ((cloneAttribute == null)) {
            return;
        }

        ObjectBuilder builder = null;
        ContainerPolicy cp = getContainerPolicy();
        Object cloneObjectCollection = null;
        cloneObjectCollection = getRealCollectionAttributeValueFromObject(object, uow);
        Object cloneIter = cp.iteratorFor(cloneObjectCollection);
        while (cp.hasNext(cloneIter)) {
            Object nextObject = cp.next(cloneIter, uow);
            if (nextObject != null && ( ! visitedObjects.containsKey(nextObject) ) ){
                visitedObjects.put(nextObject, nextObject);
                builder = getReferenceDescriptor(nextObject.getClass(), uow).getObjectBuilder();
                builder.cascadePerformRemove(nextObject, uow, visitedObjects);
            }
        }
    }

    /**
     * INTERNAL:
     * The mapping clones itself to create deep copy.
     */
    public Object clone() {
        AggregateCollectionMapping mappingObject = (AggregateCollectionMapping)super.clone();
        mappingObject.setTargetForeignKeyToSourceKeys(new HashMap(getTargetForeignKeyToSourceKeys()));
        mappingObject.setSourceKeyFields(org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(getSourceKeyFields()));
        mappingObject.setTargetForeignKeyFields(org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(getTargetForeignKeyFields()));
        return mappingObject;
    }

    /**
     * INTERNAL:
     * This method is used to create a change record from comparing two aggregate collections
     * @return ChangeRecord
     */
    public ChangeRecord compareForChange(Object clone, Object backUp, ObjectChangeSet owner, AbstractSession session) {
        Object cloneAttribute = null;
        Object backUpAttribute = null;

        cloneAttribute = getAttributeValueFromObject(clone);

        if ((cloneAttribute != null) && (!getIndirectionPolicy().objectIsInstantiated(cloneAttribute))) {
            //If the clone's valueholder was not triggered then no changes were made.
            return null;
        }
        if (!owner.isNew()) {
            backUpAttribute = getAttributeValueFromObject(backUp);
            if ((backUpAttribute == null) && (cloneAttribute == null)) {
                return null;
            }
            ContainerPolicy cp = getContainerPolicy();
            Object backupCollection = null;
            Object cloneCollection = null;

            cloneCollection = getRealCollectionAttributeValueFromObject(clone, session);
            backupCollection = getRealCollectionAttributeValueFromObject(backUp, session);

            if (cp.sizeFor(backupCollection) != cp.sizeFor(cloneCollection)) {
                return convertToChangeRecord(cloneCollection, owner, session);
            }
            Object cloneIterator = cp.iteratorFor(cloneCollection);
            Object backUpIterator = cp.iteratorFor(backupCollection);
            boolean change = false;

            // For bug 2863721 must use a different UnitOfWorkChangeSet as here just
            // seeing if changes are needed.  If changes are needed then a
            // real changeSet will be created later.
            UnitOfWorkChangeSet uowComparisonChangeSet = new UnitOfWorkChangeSet(session);
            while (cp.hasNext(cloneIterator)) {
                Object cloneObject = cp.next(cloneIterator, session);

                // For CR#2285 assume that if null is added the collection has changed.
                if (cloneObject == null) {
                    change = true;
                    break;
                }
                Object backUpObject = null;
                if (cp.hasNext(backUpIterator)) {
                    backUpObject = cp.next(backUpIterator, session);
                } else {
                    change = true;
                    break;
                }
                if (cloneObject.getClass().equals(backUpObject.getClass())) {
                    ObjectBuilder builder = getReferenceDescriptor(cloneObject.getClass(), session).getObjectBuilder();
                    ObjectChangeSet initialChanges = builder.createObjectChangeSet(cloneObject, uowComparisonChangeSet, owner.isNew(), session);

                    //compare for changes will return null if no change is detected and I need to remove the changeSet
                    ObjectChangeSet changes = builder.compareForChange(cloneObject, backUpObject, uowComparisonChangeSet, session);
                    if (changes != null) {
                        change = true;
                        break;
                    }
                } else {
                    change = true;
                    break;
                }
            }
            if ((change == true) || (cp.hasNext(backUpIterator))) {
                return convertToChangeRecord(cloneCollection, owner, session);
            } else {
                return null;
            }
        }

        return convertToChangeRecord(getRealCollectionAttributeValueFromObject(clone, session), owner, session);
    }

    /**
     * INTERNAL:
     * Compare the attributes belonging to this mapping for the objects.
     */
    public boolean compareObjects(Object firstObject, Object secondObject, AbstractSession session) {
        Object firstCollection = getRealCollectionAttributeValueFromObject(firstObject, session);
        Object secondCollection = getRealCollectionAttributeValueFromObject(secondObject, session);
        ContainerPolicy containerPolicy = getContainerPolicy();

        if (containerPolicy.sizeFor(firstCollection) != containerPolicy.sizeFor(secondCollection)) {
            return false;
        }

        if (containerPolicy.sizeFor(firstCollection) == 0) {
            return true;
        }

        //iterator the first aggregate collection	
        for (Object iterFirst = containerPolicy.iteratorFor(firstCollection);
                 containerPolicy.hasNext(iterFirst);) {
            //fetch the next object from the first iterator.
            Object firstAggregateObject = containerPolicy.next(iterFirst, session);

            //iterator the second aggregate collection	
            for (Object iterSecond = containerPolicy.iteratorFor(secondCollection); true;) {
                //fetch the next object from the second iterator.
                Object secondAggregateObject = containerPolicy.next(iterSecond, session);

                //matched object found, break to outer FOR loop			
                if (getReferenceDescriptor().getObjectBuilder().compareObjects(firstAggregateObject, secondAggregateObject, session)) {
                    break;
                }

                if (!containerPolicy.hasNext(iterSecond)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * INTERNAL:
     * This method is used to convert the contents of an aggregateCollection into a
     * changeRecord
     * @return org.eclipse.persistence.internal.sessions.AggregateCollectionChangeRecord the changerecord representing this AggregateCollectionMapping
     * @param owner org.eclipse.persistence.internal.sessions.ObjectChangeSet the ChangeSet that uses this record
     * @param cloneCollection Object the collection to convert
     * @param session org.eclipse.persistence.internal.sessions.AbstractSession
     */
    protected ChangeRecord convertToChangeRecord(Object cloneCollection, ObjectChangeSet owner, AbstractSession session) {
        ContainerPolicy cp = getContainerPolicy();
        Object cloneIter = cp.iteratorFor(cloneCollection);
        Vector collectionChanges = new Vector(2);
        while (cp.hasNext(cloneIter)) {
            Object entry = cp.nextEntry(cloneIter, session);
            Object aggregateObject = cp.unwrapIteratorResult(entry);
            // For CR#2258 quietly ignore nulls inserted into a collection.
            if (aggregateObject != null) {
                ObjectChangeSet changes = getReferenceDescriptor(aggregateObject.getClass(), session).getObjectBuilder().compareForChange(aggregateObject, null, (UnitOfWorkChangeSet)owner.getUOWChangeSet(), session);
                changes.setNewKey(cp.keyFromIterator(cloneIter));
                collectionChanges.addElement(changes);
            }
        }

        //cr 3013 Removed if collection is empty return null block, which prevents recording clear() change
        AggregateCollectionChangeRecord changeRecord = new AggregateCollectionChangeRecord(owner);
        changeRecord.setAttribute(getAttributeName());
        changeRecord.setMapping(this);
        changeRecord.setChangedValues(collectionChanges);
        return changeRecord;
    }

    /**
     * INTERNAL
     * Called when a DatabaseMapping is used to map the key in a collection.  Returns the key.
     */
    public Object createMapComponentFromRow(AbstractRecord dbRow, ObjectBuildingQuery query, AbstractSession session){
        return valueFromRow(dbRow, null, query);
    }
    
    /**
     * To delete all the entries matching the selection criteria from the table stored in the
     * referenced descriptor
     */
    protected void deleteAll(DeleteObjectQuery query, Object elements) throws DatabaseException {
        // Ensure that the query is prepare before cloning.
        ((DeleteAllQuery)getDeleteAllQuery()).executeDeleteAll(query.getSession().getSessionForClass(getReferenceClass()), query.getTranslationRow(), getContainerPolicy().vectorFor(elements, query.getSession()));
    }

    /**
     * INTERNAL:
     * Execute a descriptor event for the specified event code.
     */
    protected void executeEvent(int eventCode, ObjectLevelModifyQuery query) {
        ClassDescriptor referenceDescriptor = getReferenceDescriptor(query.getObject().getClass(), query.getSession());

        // PERF: Avoid events if no listeners.
        if (referenceDescriptor.getEventManager().hasAnyEventListeners()) {
            referenceDescriptor.getEventManager().executeEvent(new DescriptorEvent(eventCode, query));
        }
    }

    /**
     * INTERNAL:
     * Extract the source primary key value from the target row.
     * Used for batch reading, most following same order and fields as in the mapping.
     */
    protected Vector extractKeyFromTargetRow(AbstractRecord row, AbstractSession session) {
        Vector key = new Vector(getTargetForeignKeyFields().size());

        for (int index = 0; index < getTargetForeignKeyFields().size(); index++) {
            DatabaseField targetField = getTargetForeignKeyFields().elementAt(index);
            DatabaseField sourceField = getSourceKeyFields().elementAt(index);
            Object value = row.get(targetField);

            // Must ensure the classification gets a cache hit.
            try {
                value = session.getDatasourcePlatform().getConversionManager().convertObject(value, getDescriptor().getObjectBuilder().getFieldClassification(sourceField));
            } catch (ConversionException e) {
                throw ConversionException.couldNotBeConverted(this, getDescriptor(), e);
            }

            key.addElement(value);
        }

        return key;
    }

    /**
     * INTERNAL:
     * Extract the primary key value from the source row.
     * Used for batch reading, most following same order and fields as in the mapping.
     */
    protected Vector extractPrimaryKeyFromRow(AbstractRecord row, AbstractSession session) {
        Vector key = new Vector(getSourceKeyFields().size());

        for (Enumeration fieldEnum = getSourceKeyFields().elements(); fieldEnum.hasMoreElements();) {
            DatabaseField field = (DatabaseField)fieldEnum.nextElement();
            Object value = row.get(field);

            // Must ensure the classification gets a cache hit.
            try {
                value = session.getDatasourcePlatform().getConversionManager().convertObject(value, getDescriptor().getObjectBuilder().getFieldClassification(field));
            } catch (ConversionException e) {
                throw ConversionException.couldNotBeConverted(this, getDescriptor(), e);
            }

            key.addElement(value);
        }

        return key;
    }

    /**
     * INTERNAL:
     * Allow the mapping the do any further batch preparation.
     */
    protected void postPrepareNestedBatchQuery(ReadQuery batchQuery, ReadAllQuery query) {        
        ReadAllQuery aggregateBatchQuery = (ReadAllQuery)batchQuery;
        aggregateBatchQuery.setShouldIncludeData(true);
        for (Enumeration relationFieldsEnum = getTargetForeignKeyFields().elements(); relationFieldsEnum.hasMoreElements();) {
            aggregateBatchQuery.getAdditionalFields().addElement(relationFieldsEnum.nextElement());
        }
    }
    
    /**
     * INTERNAL:
     * Extract the value from the batch optimized query.
     */
    public Object extractResultFromBatchQuery(DatabaseQuery query, AbstractRecord databaseRow, AbstractSession session, AbstractRecord argumentRow) {
        //this can be null, because either one exists in the query or it will be created
        Hashtable referenceObjectsByKey = null;
        ContainerPolicy mappingContainerPolicy = getContainerPolicy();
        synchronized (query) {
            mappingContainerPolicy = getContainerPolicy();
            referenceObjectsByKey = getBatchReadObjects(query, session);
            if (referenceObjectsByKey == null) {
                ReadAllQuery batchQuery = (ReadAllQuery)query;
                ComplexQueryResult complexResult = null;
                complexResult = (ComplexQueryResult)session.executeQuery(query, argumentRow);
                Object results = complexResult.getResult();

                referenceObjectsByKey = new Hashtable();
                Enumeration rowsEnum = ((Vector)complexResult.getData()).elements();
                ContainerPolicy queryContainerPolicy = batchQuery.getContainerPolicy();
                for (Object elementsIterator = queryContainerPolicy.iteratorFor(results);
                         queryContainerPolicy.hasNext(elementsIterator);) {
                    Object eachReferenceObject = queryContainerPolicy.next(elementsIterator, session);
                    CacheKey eachReferenceKey = new CacheKey(extractKeyFromTargetRow((AbstractRecord)rowsEnum.nextElement(), session));

                    if (!referenceObjectsByKey.containsKey(eachReferenceKey)) {
                        referenceObjectsByKey.put(eachReferenceKey, mappingContainerPolicy.containerInstance());
                    }
                    mappingContainerPolicy.addInto(eachReferenceObject, referenceObjectsByKey.get(eachReferenceKey), session);
                }
                setBatchReadObjects(referenceObjectsByKey, query, session);
            }
        }
        Object result = referenceObjectsByKey.get(new CacheKey(extractPrimaryKeyFromRow(databaseRow, session)));

        // The source object might not have any target objects
        if (result == null) {
            return mappingContainerPolicy.containerInstance();
        } else {
            return result;
        }
    }

    /**
     * INTERNAL:
     * return the aggregate Record with the primary keys from the source table and target table
     */
    public AbstractRecord getAggregateRow(ObjectLevelModifyQuery query, Object object) {
        Vector referenceObjectKeys = getReferenceObjectKeys(query);
        AbstractRecord aggregateRow = new DatabaseRecord();
        Vector keys = getTargetForeignKeyFields();
        for (int keyIndex = 0; keyIndex < keys.size(); keyIndex++) {
            aggregateRow.put(keys.elementAt(keyIndex), referenceObjectKeys.elementAt(keyIndex));
        }
        getReferenceDescriptor(object.getClass(), query.getSession()).getObjectBuilder().buildRow(aggregateRow, object, query.getSession());

        return aggregateRow;
    }

    /**
     * Delete all criteria is created with target foreign keys and source keys.
     * This criteria is then used to delete target records from the table.
     */
    protected Expression getDeleteAllCriteria(AbstractSession session) {
        Expression expression;
        Expression criteria = null;
        Expression builder = new ExpressionBuilder();

        for (Iterator keys = getTargetForeignKeyToSourceKeys().keySet().iterator(); keys.hasNext();) {
            DatabaseField targetForeignKey = (DatabaseField)keys.next();
            DatabaseField sourceKey = getTargetForeignKeyToSourceKeys().get(targetForeignKey);

            expression = builder.getField(targetForeignKey).equal(builder.getParameter(sourceKey));

            criteria = expression.and(criteria);
        }

        return criteria;
    }

    /**
     * INTERNAL:
     * Return the referenceDescriptor. This is a descriptor which is associated with the reference class.
     * NOTE: If you are looking for the descriptor for a specific aggregate object, use
     * #getReferenceDescriptor(Object). This will ensure you get the right descriptor if the object's
     * descriptor is part of an inheritance tree.
     */
    public ClassDescriptor getReferenceDescriptor() {
        if(referenceDescriptor == null) {
            referenceDescriptor = remoteReferenceDescriptor;
        }
        return referenceDescriptor;
    }

    /**
     * INTERNAL:
     * for inheritance purpose
     */
    public ClassDescriptor getReferenceDescriptor(Class theClass, AbstractSession session) {
        if (getReferenceDescriptor().getJavaClass().equals(theClass)) {
            return getReferenceDescriptor();
        } else {
            ClassDescriptor subDescriptor;
            // Since aggregate collection mappings clone their descriptors, for inheritance the correct child clone must be found.
            subDescriptor = getReferenceDescriptor().getInheritancePolicy().getSubclassDescriptor(theClass);
            if (subDescriptor == null) {
                throw DescriptorException.noSubClassMatch(theClass, this);
            } else {
                return subDescriptor;
            }
        }
    }

    /**
     * INTERNAL:
     * get reference object keys
     */
    public Vector getReferenceObjectKeys(ObjectLevelModifyQuery query) throws DatabaseException, OptimisticLockException {
        Vector referenceObjectKeys = new Vector(getSourceKeyFields().size());

        //For CR#2587-S.M.  For nested aggregate collections the source keys can easily be read from the original query.
        AbstractRecord translationRow = query.getTranslationRow();

        for (Enumeration sourcekeys = getSourceKeyFields().elements();
                 sourcekeys.hasMoreElements();) {
            DatabaseField sourceKey = (DatabaseField)sourcekeys.nextElement();

            // CR#2587.  Try first to get the source key from the original query.  If that fails try to get it from the object. 
            Object referenceKey = null;
            if ((translationRow != null) && (translationRow.containsKey(sourceKey))) {
                referenceKey = translationRow.get(sourceKey);
            } else {
                referenceKey = getDescriptor().getObjectBuilder().extractValueFromObjectForField(query.getObject(), sourceKey, query.getSession());
            }
            referenceObjectKeys.addElement(referenceKey);
        }

        return referenceObjectKeys;
    }

    /**
     * PUBLIC:
     * Return the source key field names associated with the mapping.
     * These are in-order with the targetForeignKeyFieldNames.
     */
    public Vector getSourceKeyFieldNames() {
        Vector fieldNames = new Vector(getSourceKeyFields().size());
        for (Enumeration fieldsEnum = getSourceKeyFields().elements();
                 fieldsEnum.hasMoreElements();) {
            fieldNames.addElement(((DatabaseField)fieldsEnum.nextElement()).getQualifiedName());
        }

        return fieldNames;
    }

    /**
     * INTERNAL:
     * Return the source key names associated with the mapping
     */
    public Vector<DatabaseField> getSourceKeyFields() {
        return sourceKeyFields;
    }

    /**
     * PUBLIC:
     * Return the target foregin key field names associated with the mapping.
     * These are in-order with the sourceKeyFieldNames.
     */
    public Vector getTargetForeignKeyFieldNames() {
        Vector fieldNames = new Vector(getTargetForeignKeyFields().size());
        for (Enumeration fieldsEnum = getTargetForeignKeyFields().elements();
                 fieldsEnum.hasMoreElements();) {
            fieldNames.addElement(((DatabaseField)fieldsEnum.nextElement()).getQualifiedName());
        }

        return fieldNames;
    }

    /**
     * INTERNAL:
     * Return the target foregin key fields associated with the mapping
     */
    public Vector<DatabaseField> getTargetForeignKeyFields() {
        return targetForeignKeyFields;
    }

    /**
     * INTERNAL:
     */
    public Map<DatabaseField, DatabaseField> getTargetForeignKeyToSourceKeys() {
        return targetForeignKeyToSourceKeys;
    }

    /**
     * INTERNAL:
     * For aggregate collection mapping the reference descriptor is cloned. The cloned descriptor is then
     * assigned primary keys and table names before initialize. Once cloned descriptor is initialized
     * it is assigned as reference descriptor in the aggregate mapping. This is very specific
     * behavior for aggregate mappings. The original descriptor is used only for creating clones and
     * after that mapping never uses it.
     * Some initialization is done in postInitialize to ensure the target descriptor's references are initialized.
     */
    public void initialize(AbstractSession session) throws DescriptorException {
        super.initialize(session);

        if (!getReferenceDescriptor().isAggregateCollectionDescriptor()) {
            session.getIntegrityChecker().handleError(DescriptorException.referenceDescriptorIsNotAggregateCollection(getReferenceClass().getName(), this));
        }
        if (shouldInitializeSelectionCriteria()) {
            if (isSourceKeySpecified()) {
                initializeTargetForeignKeyToSourceKeys(session);
            } else {
                initializeTargetForeignKeyToSourceKeysWithDefaults(session);
            }

            initializeSelectionCriteria(session);
            getContainerPolicy().addAdditionalFieldsToQuery(getSelectionQuery(), null);
        }

        // Aggregate 1:m never maintains cache as target objects are aggregates.
        getSelectionQuery().setShouldMaintainCache(false);

        initializeDeleteAllQuery(session);
    }

    /**
     * Initialize and set the descriptor for the referenced class in this mapping.
     */
    protected void initializeReferenceDescriptor(AbstractSession session) throws DescriptorException {
        super.initializeReferenceDescriptor(session);

        HashMap<DatabaseField, DatabaseField> fieldTranslation = null;
        HashMap<DatabaseTable, DatabaseTable> tableTranslation = null;
        Vector<DatabaseTable> newTables = null;

        int nAggregateTables = 0;
        if(getReferenceDescriptor().getTables() != null) {
            nAggregateTables = getReferenceDescriptor().getTables().size();
        }
        if(this.aggregateToSourceFieldNames != null) {
            DatabaseTable aggregateDefaultTable = null;
            if(nAggregateTables != 0) {
                aggregateDefaultTable = getReferenceDescriptor().getTables().get(0);
            } else {
                aggregateDefaultTable = new DatabaseTable();
            }
            tableTranslation = new HashMap<DatabaseTable, DatabaseTable>();
            fieldTranslation = new HashMap<DatabaseField, DatabaseField>();
            
            Iterator<Map.Entry<String, String>> it = this.aggregateToSourceFieldNames.entrySet().iterator();
            while(it.hasNext()) {
                Map.Entry<String, String> entry = it.next(); 
                DatabaseField aggregateField = new DatabaseField(entry.getKey());
                if(!aggregateField.hasTableName()) {
                    aggregateField.setTable(aggregateDefaultTable);
                }
                DatabaseField sourceField = new DatabaseField(entry.getValue());
                if(!sourceField.hasTableName()) {
                    //TODO: throw exception: source field doesn't have table
                }
                DatabaseTable sourceTable = sourceField.getTable();
                DatabaseTable savedSourceTable = tableTranslation.get(aggregateField.getTable());
                if(savedSourceTable == null) {
                    tableTranslation.put(aggregateField.getTable(), sourceTable);
                } else {
                    if(!sourceTable.equals(savedSourceTable)) {
                        // TODO: throw exception: aggregate table mapped to two source tables
                    }
                }
                fieldTranslation.put(aggregateField, sourceField);
            }
        } else {
            if(nAggregateTables == 0) {
                //TODO: throw exception
            }
        }
        
        ClassDescriptor clonedDescriptor = (ClassDescriptor)getReferenceDescriptor().clone();
        if(fieldTranslation != null) {
            translateTablesAndFields(clonedDescriptor, fieldTranslation, tableTranslation); 
        }
        
        if(this.nestedAggregateToSourceFieldNames != null) {
            updateNestedAggregateCollectionMappings(clonedDescriptor);
        }
        
        if (clonedDescriptor.isChildDescriptor()) {
            ClassDescriptor parentDescriptor = session.getDescriptor(clonedDescriptor.getInheritancePolicy().getParentClass());
            initializeParentInheritance(parentDescriptor, clonedDescriptor, session, fieldTranslation, tableTranslation);
        }

        if(clonedDescriptor.isAggregateDescriptor()) {
            clonedDescriptor.descriptorIsAggregateCollection();
        }

        setReferenceDescriptor(clonedDescriptor);
        
        clonedDescriptor.preInitialize(session);
        getContainerPolicy().initialize(session, clonedDescriptor.getDefaultTable());
        clonedDescriptor.getAdditionalAggregateCollectionKeyFields().addAll(getTargetForeignKeyFields());
        List<DatabaseField> identityFields = getContainerPolicy().getIdentityFieldsForMapKey();
        if (identityFields != null){
            clonedDescriptor.getAdditionalAggregateCollectionKeyFields().addAll(identityFields);
        }
        clonedDescriptor.initialize(session);
        if (clonedDescriptor.hasInheritance() && clonedDescriptor.getInheritancePolicy().hasChildren()) {
            //clone child descriptors
            initializeChildInheritance(clonedDescriptor, session, fieldTranslation, tableTranslation);
        }
    }

    /**
     * INTERNAL:
     * Called in case fieldTranslation != null
     * Sets new primary keys, tables, appends fieldTranslation to fieldMap so that all fields in mappings, inheritance etc. translated to the new ones.
     */
    protected static void translateTablesAndFields(ClassDescriptor descriptor, HashMap<DatabaseField, DatabaseField> fieldTranslation, HashMap<DatabaseTable, DatabaseTable> tableTranslation) {
        int nTables = 0;
        if(descriptor.getTables() != null) {
            nTables = descriptor.getTables().size(); 
        }
        DatabaseTable defaultAggregateTable = null;
        if(nTables == 0) {
            defaultAggregateTable = new DatabaseTable();
            DatabaseTable defaultSourceTable = tableTranslation.get(defaultAggregateTable);
            if(defaultSourceTable == null) {
                //TODO: throw exception
            }
            descriptor.addTable(defaultSourceTable);
        } else {
            defaultAggregateTable = descriptor.getTables().get(0);
            Vector newTables = NonSynchronizedVector.newInstance(nTables);
            for(int i=0; i < nTables; i++) {
                DatabaseTable table = tableTranslation.get(descriptor.getTables().get(i));
                if(table == null) {
                    //TODO: throw exception
                }
                if(!newTables.contains(table)) {
                    newTables.add(table);
                }
            }
            descriptor.setTables(newTables);
        }
        
        int nPrimaryKeyFields = 0;
        if(descriptor.getPrimaryKeyFields() != null) {
            nPrimaryKeyFields = descriptor.getPrimaryKeyFields().size(); 
        }
        if(nPrimaryKeyFields > 0) {
            ArrayList<DatabaseField> newPrimaryKeyFields = new ArrayList(nPrimaryKeyFields);
            for(int i=0; i < nPrimaryKeyFields; i++) {
                DatabaseField pkField = descriptor.getPrimaryKeyFields().get(i);
                if(!pkField.hasTableName() && nTables > 0) {
                    pkField = new DatabaseField(pkField.getName(), defaultAggregateTable);
                }
                DatabaseField field = fieldTranslation.get(pkField);
                if(field == null) {
                    //TODO: throw exception: pk not translated
                }
                newPrimaryKeyFields.add(field);
            }
            descriptor.setPrimaryKeyFields(newPrimaryKeyFields);
        }
        
        // put fieldTranslation into fieldsMap so that all the fields in the mappings, inheritance policy etc
        // are translated to the new ones.
        descriptor.getObjectBuilder().getFieldsMap().putAll(fieldTranslation);
    }
    
    /**
     * INTERNAL:
     * Called in case nestedAggregateToSourceFieldNames != null
     * Updates AggregateCollectionMappings of the reference descriptor.  
     */
    protected void updateNestedAggregateCollectionMappings(ClassDescriptor descriptor) {
        Iterator<Map.Entry<String, Map<String, String>>> it = nestedAggregateToSourceFieldNames.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry<String, Map<String, String>> entry = it.next();
            String attribute = entry.getKey();
            String nestedAttribute = null;
            int indexOfDot = attribute.indexOf('.'); 
            // attribute "homes.sellingPonts" is divided into attribute "homes" and nestedAttribute "sellingPoints" 
            if(indexOfDot >= 0) {
                nestedAttribute = attribute.substring(indexOfDot + 1, attribute.length());
                attribute = attribute.substring(0, indexOfDot);
            }
            DatabaseMapping mapping = descriptor.getMappingForAttributeName(attribute);
            if(mapping == null) {
                //TODO: may have been already processed by the parent, may be processed later by a child.
                //Should add method verifyNestedAggregateToSourceFieldNames that would go through
                //all the children and detect the wrong attribute.
                continue;
            }
            if(!mapping.isAggregateCollectionMapping()) {
                //TODO: throw exception: mapping corresponding to attribute is not AggregateCollectionMapping
            }
            AggregateCollectionMapping nestedAggregateCollectionMapping = (AggregateCollectionMapping)mapping;
            if(nestedAttribute == null) {
                nestedAggregateCollectionMapping.addFieldNameTranslations(entry.getValue());
            } else {
                nestedAggregateCollectionMapping.addNestedFieldNameTranslations(nestedAttribute, entry.getValue());
            }
        }
    }
    
    /**
     * INTERNAL:
     * If field names are different in the source and aggregate objects then the translation
     * is done here. The aggregate field name is converted to source field name from the
     * field name mappings stored.
     */
    protected void translateFields(ClassDescriptor clonedDescriptor, AbstractSession session) {
        if(aggregateToSourceFieldNames != null) {
            for (Enumeration entry = clonedDescriptor.getFields().elements(); entry.hasMoreElements();) {
                DatabaseField field = (DatabaseField)entry.nextElement();
                String nameInAggregate = field.getName();
                String nameInSource = aggregateToSourceFieldNames.get(nameInAggregate);
    
                // Do not modify non-translated fields.
                if (nameInSource != null) {
                    DatabaseField fieldInSource = new DatabaseField(nameInSource);
    
                    // Check if the translated field specified a table qualifier.
                    if (fieldInSource.getName().equals(nameInSource)) {
                        // No table so just set the field name.
                        field.setName(nameInSource);
                    } else {
                        // There is a table, so set the name and table.
                        field.setName(fieldInSource.getName());
                        field.setTable(clonedDescriptor.getTable(fieldInSource.getTable().getName()));
                    }
                }
            }
    
            clonedDescriptor.rehashFieldDependancies(session);
        }
    }

    /**
     * INTERNAL:
     * For aggregate mapping the reference descriptor is cloned. Also the involved inheritance descriptor, its children
     * and parents all need to be cloned. The cloned descriptors are then assigned primary keys and table names before
     * initialize. Once cloned descriptor is initialized it is assigned as reference descriptor in the aggregate mapping.
     * This is very specific behavior for aggregate mappings. The original descriptor is used only for creating clones
     * and after that mapping never uses it.
     * Some initialization is done in postInitialize to ensure the target descriptor's references are initialized.
     */
    public void initializeChildInheritance(ClassDescriptor parentDescriptor, AbstractSession session, 
                HashMap<DatabaseField, DatabaseField> fieldTranslation, HashMap<DatabaseTable, DatabaseTable> tableTranslation) throws DescriptorException {
        //recursive call to further children descriptors
        if (parentDescriptor.getInheritancePolicy().hasChildren()) {
            //setFields(clonedChildDescriptor.getFields());		
            Vector childDescriptors = parentDescriptor.getInheritancePolicy().getChildDescriptors();
            Vector cloneChildDescriptors = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance();
            for (Enumeration enumtr = childDescriptors.elements(); enumtr.hasMoreElements();) {
                ClassDescriptor clonedChildDescriptor = (ClassDescriptor)((ClassDescriptor)enumtr.nextElement()).clone();
                if(fieldTranslation != null) {
                    translateTablesAndFields(clonedChildDescriptor, fieldTranslation, tableTranslation); 
                }
                if(this.nestedAggregateToSourceFieldNames != null) {
                    updateNestedAggregateCollectionMappings(clonedChildDescriptor);
                }
                
                if(clonedChildDescriptor.isAggregateDescriptor()) {
                    clonedChildDescriptor.descriptorIsAggregateCollection();
                }
                if (!clonedChildDescriptor.isAggregateCollectionDescriptor()) {
                    session.getIntegrityChecker().handleError(DescriptorException.referenceDescriptorIsNotAggregate(clonedChildDescriptor.getJavaClass().getName(), this));
                }

                clonedChildDescriptor.getInheritancePolicy().setParentDescriptor(parentDescriptor);
                clonedChildDescriptor.preInitialize(session);
                clonedChildDescriptor.initialize(session);
                cloneChildDescriptors.addElement(clonedChildDescriptor);
                initializeChildInheritance(clonedChildDescriptor, session, fieldTranslation, tableTranslation);
            }
            parentDescriptor.getInheritancePolicy().setChildDescriptors(cloneChildDescriptors);
        }
    }

    /**
     * INTERNAL:
     * Initialize delete all query. This query is used to delete the collection of objects from the
     * target table.
     */
    protected void initializeDeleteAllQuery(AbstractSession session) {
        DeleteAllQuery query = (DeleteAllQuery)getDeleteAllQuery();
        query.setReferenceClass(getReferenceClass());
        query.setDescriptor(getReferenceDescriptor());
        query.setShouldMaintainCache(false);
        if (!hasCustomDeleteAllQuery()) {
            if (getSelectionCriteria() == null) {
                query.setSelectionCriteria(getDeleteAllCriteria(session));
            } else {
                query.setSelectionCriteria(getSelectionCriteria());
            }
        }
    }

    /**
     * INTERNAL:
     * For aggregate mapping the reference descriptor is cloned. Also the involved inheritance descriptor, its children
     * and parents all need to be cloned. The cloned descriptors are then assigned primary keys and table names before
     * initialize. Once cloned descriptor is initialized it is assigned as reference descriptor in the aggregate mapping.
     * This is very specific behavior for aggregate mappings. The original descriptor is used only for creating clones
     * and after that mapping never uses it.
     * Some initialization is done in postInitialize to ensure the target descriptor's references are initialized.
     */
    public void initializeParentInheritance(ClassDescriptor parentDescriptor, ClassDescriptor childDescriptor, AbstractSession session, 
                HashMap<DatabaseField, DatabaseField> fieldTranslation, HashMap<DatabaseTable, DatabaseTable> tableTranslation) throws DescriptorException {

        ClassDescriptor clonedParentDescriptor = (ClassDescriptor)parentDescriptor.clone();
        if(clonedParentDescriptor.isAggregateDescriptor()) {
            clonedParentDescriptor.descriptorIsAggregateCollection();
        }
        if (!clonedParentDescriptor.isAggregateCollectionDescriptor()) {
            session.getIntegrityChecker().handleError(DescriptorException.referenceDescriptorIsNotAggregateCollection(parentDescriptor.getJavaClass().getName(), this));
        }
        if (fieldTranslation != null) {
            translateTablesAndFields(clonedParentDescriptor, fieldTranslation, tableTranslation); 
        }
        if(this.nestedAggregateToSourceFieldNames != null) {
            updateNestedAggregateCollectionMappings(clonedParentDescriptor);
        }
        
        //recursive call to the further parent descriptors
        if (clonedParentDescriptor.getInheritancePolicy().isChildDescriptor()) {
            ClassDescriptor parentToParentDescriptor = session.getDescriptor(clonedParentDescriptor.getJavaClass());
            initializeParentInheritance(parentToParentDescriptor, parentDescriptor, session, fieldTranslation, tableTranslation);
        }

        Vector children = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(1);
        children.addElement(childDescriptor);
        clonedParentDescriptor.getInheritancePolicy().setChildDescriptors(children);
        clonedParentDescriptor.preInitialize(session);
        clonedParentDescriptor.initialize(session);
    }

    /**
     * INTERNAL:
     * Selection criteria is created with target foreign keys and source keys.
     * This criteria is then used to read records from the target table.
     */
    protected void initializeSelectionCriteria(AbstractSession session) {
        Expression expression;
        Expression criteria;
        Expression builder = new ExpressionBuilder();

        for (Iterator keys = getTargetForeignKeyToSourceKeys().keySet().iterator(); keys.hasNext();) {
            DatabaseField targetForeignKey = (DatabaseField)keys.next();
            DatabaseField sourceKey = getTargetForeignKeyToSourceKeys().get(targetForeignKey);

            expression = builder.getField(targetForeignKey).equal(builder.getParameter(sourceKey));

            criteria = expression.and(getSelectionCriteria());
            setSelectionCriteria(criteria);
        }
    }

    /**
     * INTERNAL:
     * The foreign keys and the primary key names are converted to DatabaseFields and stored.
     */
    protected void initializeTargetForeignKeyToSourceKeys(AbstractSession session) throws DescriptorException {
        if (getTargetForeignKeyFields().isEmpty()) {
            throw DescriptorException.noTargetForeignKeysSpecified(this);
        }

        for (int index = 0;  index < getTargetForeignKeyFields().size(); index++) {
            DatabaseField foreignKeyfield = getTargetForeignKeyFields().get(index);
            foreignKeyfield = getReferenceDescriptor().buildField(foreignKeyfield);
            getTargetForeignKeyFields().set(index, foreignKeyfield);
        }

        for (int index = 0;  index < getSourceKeyFields().size(); index++) {
            DatabaseField sourceKeyfield = getSourceKeyFields().get(index);
            sourceKeyfield = getDescriptor().buildField(sourceKeyfield);
            getSourceKeyFields().set(index, sourceKeyfield);
        }

        if (getTargetForeignKeyFields().size() != getSourceKeyFields().size()) {
            throw DescriptorException.targetForeignKeysSizeMismatch(this);
        }

        Iterator<DatabaseField> targetForeignKeysEnum = getTargetForeignKeyFields().iterator();
        Iterator<DatabaseField> sourceKeysEnum = getSourceKeyFields().iterator();
        while (targetForeignKeysEnum.hasNext()) {
            getTargetForeignKeyToSourceKeys().put(targetForeignKeysEnum.next(), sourceKeysEnum.next());
        }
    }

    /**
     * INTERNAL:
     * The foreign keys and the primary key names are converted to DatabaseFields and stored. The source keys
     * are not specified by the user so primary keys are extracted from the reference descriptor.
     */
    protected void initializeTargetForeignKeyToSourceKeysWithDefaults(AbstractSession session) throws DescriptorException {
        if (getTargetForeignKeyFields().isEmpty()) {
            throw DescriptorException.noTargetForeignKeysSpecified(this);
        }

        List<DatabaseField> sourceKeys = getDescriptor().getPrimaryKeyFields();
        setSourceKeyFields(org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(sourceKeys));
        for (int index = 0; index < getTargetForeignKeyFields().size(); index++) {
            DatabaseField foreignKeyfield = getTargetForeignKeyFields().get(index);
            foreignKeyfield = getReferenceDescriptor().buildField(foreignKeyfield);
            getTargetForeignKeyFields().set(index, foreignKeyfield);
        }

        if (getTargetForeignKeyFields().size() != sourceKeys.size()) {
            throw DescriptorException.targetForeignKeysSizeMismatch(this);
        }

        for (int index = 0; index < getTargetForeignKeyFields().size(); index++) {
            getTargetForeignKeyToSourceKeys().put(getTargetForeignKeyFields().get(index), sourceKeys.get(index));
        }
    }

    /**
     * INTERNAL:
     * Iterate on the specified element.
     */
    public void iterateOnElement(DescriptorIterator iterator, Object element) {
        // CR#... Aggregate collections must iterate as aggregates, not regular mappings.
        // For some reason the element can be null, this makes absolutely no sense, but we have a test case for it...
        if (element != null) {
            iterator.iterateForAggregateMapping(element, this, getReferenceDescriptor(element.getClass(), iterator.getSession()));
        }
    }

    /**
     * INTERNAL:
     */
    public boolean isAggregateCollectionMapping() {
        return true;
    }
    
    /**
     * INTERNAL:
     * Return if this mapping support joining.
     */
    public boolean isJoiningSupported() {
        return true;
    }
    
    /**
     * INTERNAL:
     */
    public boolean isPrivateOwned() {
        return true;
    }

    /**
     * Checks if source key is specified or not.
     */
    protected boolean isSourceKeySpecified() {
        return !(getSourceKeyFields().isEmpty());
    }

    /**
     * INTERNAL:
     * Merge changes from the source to the target object.
     * Because this is a collection mapping, values are added to or removed from the
     * collection based on the changeset
     */
    public void mergeChangesIntoObject(Object target, ChangeRecord changeRecord, Object source, MergeManager mergeManager) {
        //Check to see if the target has an instantiated collection
        if (!isAttributeValueInstantiatedOrChanged(target)) {
            //Then do nothing.
            return;
        }

        ContainerPolicy containerPolicy = getContainerPolicy();
        AbstractSession session = mergeManager.getSession();
        Object valueOfTarget = null;

        //At this point the source's indirection must be instantiated or the changeSet would never have
        // been created
        Object sourceAggregate = null;

        //On a distributed cache if our changes are for the same version as the target object
        //then load the changes from database.
        // CR 4143  
        // CR 4155 Always replace the collection with the query results as we will not be able to
        // find the originals for merging and indirection information may be lost.
        if (mergeManager.shouldMergeChangesIntoDistributedCache()) {
            ClassDescriptor descriptor = getDescriptor();
            AbstractRecord parentRow = descriptor.getObjectBuilder().extractPrimaryKeyRowFromObject(target, session);
            Object result = getIndirectionPolicy().valueFromQuery(getSelectionQuery(), parentRow, session);//fix for indirection
            setAttributeValueInObject(target, result);
            return;
        }

        // iterate over the changes and merge the collections
        Vector aggregateObjects = ((AggregateCollectionChangeRecord)changeRecord).getChangedValues();
        valueOfTarget = containerPolicy.containerInstance();
        // Next iterate over the changes and add them to the container
        ObjectChangeSet objectChanges = null;
        for (int i = 0; i < aggregateObjects.size(); ++i) {
            objectChanges = (ObjectChangeSet)aggregateObjects.elementAt(i);
            Class localClassType = objectChanges.getClassType(session);
            sourceAggregate = objectChanges.getUnitOfWorkClone();

            // cr 4155 Load the target from the UnitOfWork.  This will be the original
            // aggregate object that has the original indirection in it.
            Object targetAggregate = ((UnitOfWorkImpl)mergeManager.getSession()).getCloneToOriginals().get(sourceAggregate);

            if (targetAggregate == null) {
                targetAggregate = getReferenceDescriptor(localClassType, session).getObjectBuilder().buildNewInstance();
            }
            getReferenceDescriptor(localClassType, session).getObjectBuilder().mergeChangesIntoObject(targetAggregate, objectChanges, sourceAggregate, mergeManager);
            containerPolicy.addInto(objectChanges.getNewKey(), targetAggregate, valueOfTarget, session);
        }
        setRealAttributeValueInObject(target, valueOfTarget);
    }

    /**
     * INTERNAL:
     * Merge changes from the source to the target object.
     */
    public void mergeIntoObject(Object target, boolean isTargetUnInitialized, Object source, MergeManager mergeManager) {
        if (isTargetUnInitialized) {
            // This will happen if the target object was removed from the cache before the commit was attempted
            if (mergeManager.shouldMergeWorkingCopyIntoOriginal() && (!isAttributeValueInstantiatedOrChanged(source))) {
                setAttributeValueInObject(target, getIndirectionPolicy().getOriginalIndirectionObject(getAttributeValueFromObject(source), mergeManager.getSession()));
                return;
            }
        }
        if (!shouldMergeCascadeReference(mergeManager)) {
            // This is only going to happen on mergeClone, and we should not attempt to merge the reference
            return;
        }
        if (mergeManager.shouldRefreshRemoteObject() && shouldMergeCascadeParts(mergeManager) && usesIndirection()) {
            mergeRemoteValueHolder(target, source, mergeManager);
            return;
        }
        if (mergeManager.shouldMergeOriginalIntoWorkingCopy()) {
            if (!isAttributeValueInstantiatedOrChanged(target)) {
                // This will occur when the clone's value has not been instantiated yet and we do not need
                // the refresh that attribute
                return;
            }
        } else if (!isAttributeValueInstantiatedOrChanged(source)) {
            // I am merging from a clone into an original.  No need to do merge if the attribute was never
            // modified
            return;
        }

        ContainerPolicy containerPolicy = getContainerPolicy();
        Object valueOfSource = getRealCollectionAttributeValueFromObject(source, mergeManager.getSession());
        Object valueOfTarget = containerPolicy.containerInstance(containerPolicy.sizeFor(valueOfSource));
        for (Object sourceValuesIterator = containerPolicy.iteratorFor(valueOfSource);
                 containerPolicy.hasNext(sourceValuesIterator);) {
            Object wrappedSourceValue = containerPolicy.nextEntry(sourceValuesIterator, mergeManager.getSession());
            Object sourceValue = containerPolicy.unwrapIteratorResult(wrappedSourceValue);

            // For some odd reason support for having null in the collection was added. This does not make sense...
            Object originalValue = null;
            if (sourceValue != null) {
                //CR#2896 - TW
                originalValue = getReferenceDescriptor(sourceValue.getClass(), mergeManager.getSession()).getObjectBuilder().buildNewInstance();
                getReferenceDescriptor(sourceValue.getClass(), mergeManager.getSession()).getObjectBuilder().mergeIntoObject(originalValue, true, sourceValue, mergeManager);
                containerPolicy.addInto(containerPolicy.keyFromIterator(sourceValuesIterator), originalValue, valueOfTarget, mergeManager.getSession());
            }
        }

        // Must re-set variable to allow for set method to re-morph changes if the collection is not being stored directly.
        setRealAttributeValueInObject(target, valueOfTarget);
    }

    /**
     * INTERNAL:
     * An object was added to the collection during an update, insert it if private.
     */
    protected void objectAddedDuringUpdate(ObjectLevelModifyQuery query, Object objectAdded, ObjectChangeSet changeSet, Map extraData) throws DatabaseException, OptimisticLockException {
        // Insert must not be done for uow or cascaded queries and we must cascade to cascade policy.
        InsertObjectQuery insertQuery = getAndPrepareModifyQueryForInsert(query, objectAdded);
        ContainerPolicy.copyMapDataToRow(extraData, insertQuery.getModifyRow());
        query.getSession().executeQuery(insertQuery, insertQuery.getTranslationRow());
    }

    /**
     * INTERNAL:
     * An object was removed to the collection during an update, delete it if private.
     */
    protected void objectRemovedDuringUpdate(ObjectLevelModifyQuery query, Object objectDeleted, Map extraData) throws DatabaseException, OptimisticLockException {
        // Delete must not be done for uow or cascaded queries and we must cascade to cascade policy.
        DeleteObjectQuery deleteQuery = new DeleteObjectQuery();
        deleteQuery.setIsExecutionClone(true);
        prepareModifyQueryForDelete(query, deleteQuery, objectDeleted);
        ContainerPolicy.copyMapDataToRow(extraData, deleteQuery.getTranslationRow());
        query.getSession().executeQuery(deleteQuery, deleteQuery.getTranslationRow());
    }

    /**
     * INTERNAL:
     * An object is still in the collection, update it as it may have changed.
     */
    protected void objectUnchangedDuringUpdate(ObjectLevelModifyQuery query, Object object, Hashtable backupCloneKeyedCache, CacheKey cachedKey) throws DatabaseException, OptimisticLockException {
        // Always write for updates, either private or in uow if calling this method.
        UpdateObjectQuery updateQuery = new UpdateObjectQuery();
        updateQuery.setIsExecutionClone(true);
        Object backupclone = backupCloneKeyedCache.get(cachedKey);
        updateQuery.setBackupClone(backupclone);
        prepareModifyQueryForUpdate(query, updateQuery, object);
        query.getSession().executeQuery(updateQuery, updateQuery.getTranslationRow());
    }

    /**
     * INTERNAL:
     * For aggregate collection mapping the reference descriptor is cloned. The cloned descriptor is then
     * assigned primary keys and table names before initialize. Once cloned descriptor is initialized
     * it is assigned as reference descriptor in the aggregate mapping. This is very specific
     * behavior for aggregate mappings. The original descriptor is used only for creating clones and
     * after that mapping never uses it.
     * Some initialization is done in postInitialize to ensure the target descriptor's references are initialized.
     */
    public void postInitialize(AbstractSession session) throws DescriptorException {
        super.postInitialize(session);
        getReferenceDescriptor().postInitialize(session);
    }

    /**
     * INTERNAL:
     * Insert privately owned parts
     */
    public void postInsert(WriteObjectQuery query) throws DatabaseException, OptimisticLockException {
        if (isReadOnly()) {
            return;
        }

        Object objects = getRealCollectionAttributeValueFromObject(query.getObject(), query.getSession());

        // insert each object one by one
        ContainerPolicy cp = getContainerPolicy();
        for (Object iter = cp.iteratorFor(objects); cp.hasNext(iter);) {
            Object wrappedObject = cp.nextEntry(iter, query.getSession());
            Object object = cp.unwrapIteratorResult(wrappedObject);
            InsertObjectQuery insertQuery = getAndPrepareModifyQueryForInsert(query, object);
            ContainerPolicy.copyMapDataToRow(cp.getKeyMappingDataForWriteQuery(wrappedObject, query.getSession()), insertQuery.getModifyRow());
            query.getSession().executeQuery(insertQuery, insertQuery.getTranslationRow());
        }
    }

    /**
     * INTERNAL:
     * Update the privately owned parts
     */
    public void postUpdate(WriteObjectQuery writeQuery) throws DatabaseException, OptimisticLockException {
        if (isReadOnly()) {
            return;
        }

        // If objects are not instantiated that means they are not changed.
        if (!isAttributeValueInstantiatedOrChanged(writeQuery.getObject())) {
            return;
        }

        // Manage objects added and removed from the collection.
        Object objects = getRealCollectionAttributeValueFromObject(writeQuery.getObject(), writeQuery.getSession());
        Object currentObjectsInDB = readPrivateOwnedForObject(writeQuery);
        if (currentObjectsInDB == null) {
            currentObjectsInDB = getContainerPolicy().containerInstance(1);
        }
        compareObjectsAndWrite(currentObjectsInDB, objects, writeQuery);
    }

    /**
     * INTERNAL:
     * Delete privately owned parts
     */
    public void preDelete(DeleteObjectQuery query) throws DatabaseException, OptimisticLockException {
        if (isReadOnly()) {
            return;
        }

        Object objects = getRealCollectionAttributeValueFromObject(query.getObject(), query.getSession());

        ContainerPolicy containerPolicy = getContainerPolicy();

        // if privately owned parts have their privately own parts, delete those one by one
        // else delete everything in one shot.
        if (mustDeleteReferenceObjectsOneByOne()) {
            for (Object iter = containerPolicy.iteratorFor(objects); containerPolicy.hasNext(iter);) {
                Object object = containerPolicy.next(iter, query.getSession());
                DeleteObjectQuery deleteQuery = new DeleteObjectQuery();
                deleteQuery.setIsExecutionClone(true);
                prepareModifyQueryForDelete(query, deleteQuery, object);
                query.getSession().executeQuery(deleteQuery, deleteQuery.getTranslationRow());
            }
            if (!query.getSession().isUnitOfWork()) {
                // This deletes any objects on the database, as the collection in memory may has been changed.
                // This is not required for unit of work, as the update would have already deleted these objects,
                // and the backup copy will include the same objects causing double deletes.
                verifyDeleteForUpdate(query);
            }
        } else {
            deleteAll(query, objects);
        }
    }

    /**
     * INTERNAL:
     * The message is passed to its reference class descriptor.
     */
    public void preInsert(WriteObjectQuery query) throws DatabaseException, OptimisticLockException {
        if (isReadOnly()) {
            return;
        }

        Object objects = getRealCollectionAttributeValueFromObject(query.getObject(), query.getSession());

        // pre-insert each object one by one
        ContainerPolicy cp = getContainerPolicy();
        for (Object iter = cp.iteratorFor(objects); cp.hasNext(iter);) {
            Object wrappedObject = cp.nextEntry(iter, query.getSession());
            Object object = cp.unwrapIteratorResult(wrappedObject);
            InsertObjectQuery insertQuery = getAndPrepareModifyQueryForInsert(query, object);
            ContainerPolicy.copyMapDataToRow(cp.getKeyMappingDataForWriteQuery(wrappedObject, query.getSession()), insertQuery.getModifyRow());

            // aggregates do not actually use a query to write to the database so the pre-write must be called here
            executeEvent(DescriptorEventManager.PreWriteEvent, insertQuery);
            executeEvent(DescriptorEventManager.PreInsertEvent, insertQuery);
            getReferenceDescriptor(object.getClass(), query.getSession()).getQueryManager().preInsert(insertQuery);
        }
    }

    /**
     * INTERNAL:
     * Returns clone of InsertObjectQuery from the reference descriptor, if it is not set - create it.
     */
    protected InsertObjectQuery getInsertObjectQuery(AbstractSession session, ClassDescriptor desc) {
        InsertObjectQuery insertQuery = desc.getQueryManager().getInsertQuery();
        if (insertQuery == null) {
            insertQuery = new InsertObjectQuery();
            desc.getQueryManager().setInsertQuery(insertQuery);
        }
        if (insertQuery.getModifyRow() == null) {
            AbstractRecord modifyRow = new DatabaseRecord();
            for (int i = 0; i < getTargetForeignKeyFields().size(); i++) {
                DatabaseField field = getTargetForeignKeyFields().elementAt(i);
                modifyRow.put(field, null);
            }
            desc.getObjectBuilder().buildTemplateInsertRow(session, modifyRow);
            getContainerPolicy().addFieldsForMapKey(modifyRow);
            insertQuery.setModifyRow(modifyRow);
        }
        return insertQuery;
    }

    /**
     * INTERNAL:
     * setup the modifyQuery for post insert/update and pre delete
     */
    public InsertObjectQuery getAndPrepareModifyQueryForInsert(ObjectLevelModifyQuery originalQuery, Object object) {
        AbstractSession session = originalQuery.getSession();
        ClassDescriptor objReferenceDescriptor = getReferenceDescriptor(object.getClass(), session);
        InsertObjectQuery insertQueryFromDescriptor = getInsertObjectQuery(session, objReferenceDescriptor);
        insertQueryFromDescriptor.checkPrepare(session, insertQueryFromDescriptor.getModifyRow());

        InsertObjectQuery insertQuery = (InsertObjectQuery)insertQueryFromDescriptor.clone();
        insertQuery.setObject(object);
        insertQuery.setDescriptor(objReferenceDescriptor);

        AbstractRecord targetForeignKeyRow = new DatabaseRecord();
        Vector referenceObjectKeys = getReferenceObjectKeys(originalQuery);
        for (int keyIndex = 0; keyIndex < getTargetForeignKeyFields().size(); keyIndex++) {
            targetForeignKeyRow.put(getTargetForeignKeyFields().elementAt(keyIndex), referenceObjectKeys.elementAt(keyIndex));
        }

        insertQuery.setModifyRow(targetForeignKeyRow);
        insertQuery.setTranslationRow(targetForeignKeyRow);
        insertQuery.setSession(session);
        insertQuery.setCascadePolicy(originalQuery.getCascadePolicy());
        insertQuery.dontMaintainCache();

        // For bug 2863721 must set a backup clone for compatibility with
        // old event mechanism, even though for AggregateCollections there is no
        // way to get a backup directly from a clone.
        if (session.isUnitOfWork()) {
            Object backupAttributeValue = getReferenceDescriptor(object.getClass(), session).getObjectBuilder().buildNewInstance();
            insertQuery.setBackupClone(backupAttributeValue);
        }
        return insertQuery;
    }

    /**
     * INTERNAL:
     * setup the modifyQuery for pre delete
     */
    public void prepareModifyQueryForDelete(ObjectLevelModifyQuery originalQuery, ObjectLevelModifyQuery modifyQuery, Object object) {
        AbstractRecord aggregateRow = getAggregateRow(originalQuery, object);
        modifyQuery.setObject(object);
        modifyQuery.setDescriptor(getReferenceDescriptor(object.getClass(), originalQuery.getSession()));
        modifyQuery.setPrimaryKey(getReferenceDescriptor().getObjectBuilder().extractPrimaryKeyFromRow(aggregateRow, originalQuery.getSession()));
        modifyQuery.setModifyRow(aggregateRow);
        modifyQuery.setTranslationRow(aggregateRow);
        modifyQuery.setSession(originalQuery.getSession());
        if (originalQuery.shouldCascadeOnlyDependentParts()) {
            //This query is the result of being in a UnitOfWork therefor use the Aggregate Collection
            //specific cascade policy to prevent cascading the delete now
            modifyQuery.setCascadePolicy(DatabaseQuery.CascadeAggregateDelete);
        } else {
            modifyQuery.setCascadePolicy(originalQuery.getCascadePolicy());
        }
        modifyQuery.dontMaintainCache();
    }

    /**
     * INTERNAL:
     * setup the modifyQuery for update,
     */
    public void prepareModifyQueryForUpdate(ObjectLevelModifyQuery originalQuery, ObjectLevelModifyQuery modifyQuery, Object object) {
        AbstractRecord aggregateRow = getAggregateRow(originalQuery, object);
        modifyQuery.setObject(object);
        modifyQuery.setDescriptor(getReferenceDescriptor(object.getClass(), originalQuery.getSession()));
        modifyQuery.setPrimaryKey(getReferenceDescriptor().getObjectBuilder().extractPrimaryKeyFromRow(aggregateRow, originalQuery.getSession()));
        modifyQuery.setTranslationRow(aggregateRow);
        modifyQuery.setSession(originalQuery.getSession());
        modifyQuery.setCascadePolicy(originalQuery.getCascadePolicy());
        modifyQuery.dontMaintainCache();
    }

    /**
     * INTERNAL:
     * Set the referenceDescriptor. This is a descriptor which is associated with
     * the reference class.
     */
    protected void setReferenceDescriptor(ClassDescriptor aDescriptor) {
        this.referenceDescriptor = aDescriptor;
        this.remoteReferenceDescriptor = this.referenceDescriptor;
    }

    /**
     * PUBLIC:
     * Set the source key field names associated with the mapping.
     * These must be in-order with the targetForeignKeyFieldNames.
     */
    public void setSourceKeyFieldNames(Vector fieldNames) {
        Vector fields = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(fieldNames.size());
        for (Enumeration fieldNamesEnum = fieldNames.elements(); fieldNamesEnum.hasMoreElements();) {
            fields.addElement(new DatabaseField((String)fieldNamesEnum.nextElement()));
        }

        setSourceKeyFields(fields);
    }

    /**
     * INTERNAL:
     * set all the primary key names associated with this mapping
     */
    public void setSourceKeyFields(Vector<DatabaseField> sourceKeyFields) {
        this.sourceKeyFields = sourceKeyFields;
    }

    /**
     * PUBLIC:
     * Set the target foregin key field names associated with the mapping.
     * These must be in-order with the sourceKeyFieldNames.
     */
    public void setTargetForeignKeyFieldNames(Vector fieldNames) {
        Vector fields = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(fieldNames.size());
        for (Enumeration fieldNamesEnum = fieldNames.elements(); fieldNamesEnum.hasMoreElements();) {
            fields.addElement(new DatabaseField((String)fieldNamesEnum.nextElement()));
        }

        setTargetForeignKeyFields(fields);
    }

    /**
     * INTERNAL:
     * set the target foregin key fields associated with the mapping
     */
    public void setTargetForeignKeyFields(Vector<DatabaseField> targetForeignKeyFields) {
        this.targetForeignKeyFields = targetForeignKeyFields;
    }

    protected void setTargetForeignKeyToSourceKeys(Map<DatabaseField, DatabaseField> targetForeignKeyToSourceKeys) {
        this.targetForeignKeyToSourceKeys = targetForeignKeyToSourceKeys;
    }

    /**
     * Returns true as any process leading to object modification should also affect its privately owned parts
     * Usually used by write, insert, update and delete.
     */
    protected boolean shouldObjectModifyCascadeToParts(ObjectLevelModifyQuery query) {
        if (isReadOnly()) {
            return false;
        }

        return true;
    }

    /**
     * ADVANCED:
     * This method is used to have an object add to a collection once the changeSet is applied
     * The referenceKey parameter should only be used for direct Maps. PLEASE ENSURE that the changes
     * have been made in the object model first.
     */
    public void simpleAddToCollectionChangeRecord(Object referenceKey, Object changeSetToAdd, ObjectChangeSet changeSet, AbstractSession session) {
        AggregateCollectionChangeRecord collectionChangeRecord = (AggregateCollectionChangeRecord)changeSet.getChangesForAttributeNamed(this.getAttributeName());
        if (collectionChangeRecord == null) {
            //if there is no change for this attribute then create a changeSet for it. no need to modify the resulting
            // change record as it should be built from the clone which has the changes allready
            Object cloneObject = ((UnitOfWorkChangeSet)changeSet.getUOWChangeSet()).getUOWCloneForObjectChangeSet(changeSet);
            Object cloneCollection = this.getRealAttributeValueFromObject(cloneObject, session);
            collectionChangeRecord = (AggregateCollectionChangeRecord)convertToChangeRecord(cloneCollection, changeSet, session);
            changeSet.addChange(collectionChangeRecord);
        } else {
            collectionChangeRecord.getChangedValues().add(changeSetToAdd);
        }
    }

    /**
     * ADVANCED:
     * This method is used to have an object removed from a collection once the changeSet is applied
     * The referenceKey parameter should only be used for direct Maps.  PLEASE ENSURE that the changes
     * have been made in the object model first.
     */
    public void simpleRemoveFromCollectionChangeRecord(Object referenceKey, Object changeSetToRemove, ObjectChangeSet changeSet, AbstractSession session) {
        AggregateCollectionChangeRecord collectionChangeRecord = (AggregateCollectionChangeRecord)changeSet.getChangesForAttributeNamed(this.getAttributeName());

        if (collectionChangeRecord == null) {
            //if there is no change for this attribute then create a changeSet for it. no need to modify the resulting
            // change record as it should be built from the clone which has the changes allready
            Object cloneObject = ((UnitOfWorkChangeSet)changeSet.getUOWChangeSet()).getUOWCloneForObjectChangeSet(changeSet);
            Object cloneCollection = this.getRealAttributeValueFromObject(cloneObject, session);
            collectionChangeRecord = (AggregateCollectionChangeRecord)convertToChangeRecord(cloneCollection, changeSet, session);
            changeSet.addChange(collectionChangeRecord);
        } else {
            collectionChangeRecord.getChangedValues().remove(changeSetToRemove);
        }
    }

    /**
     * INTERNAL:
     * Retrieves a value from the row for a particular query key
     */
    public Object valueFromRow(AbstractRecord row, JoinedAttributeManager joinManager, ObjectBuildingQuery sourceQuery, AbstractSession executionSession) throws DatabaseException {
        // For CR#2587: a fix to allow the reading of nested aggregate collections that
        // use foreign keys as primary keys.
        // Even though foreign keys are not read in a read query insert them into the row that 
        // is returned from the database to allow cascading of primary keys.
        // This row will eventually become the translation row which is used to read the aggregate collection.
        // The fix works by passing foreign key information between source and target queries via the translation row.
        // Must clone the row first, for due to prior optimizations the vector of fields is now part of
        // a prepared query!
        row = (AbstractRecord)row.clone();
        int i = 0;
        for (Enumeration sourceKeys = getSourceKeyFields().elements();
                 sourceKeys.hasMoreElements(); i++) {
            DatabaseField sourceKey = (DatabaseField)sourceKeys.nextElement();
            Object value = null;

            // First insure that the source foreign key field is in the row.
            // N.B. If get() is used and returns null it may just mean that the field exists but the value is null.
            int index = row.getFields().indexOf(sourceKey);
            if (index == -1) {
                //Line x: Retrieve the value from the source query's translation row.
                value = sourceQuery.getTranslationRow().get(sourceKey);
                row.add(sourceKey, value);
            } else {
                value = row.getValues().elementAt(index);
            }

            //Now duplicate the source key field values with target key fields, so children aggregate collections can later access them.
            //This will enable the later execution of the above line x.
            row.add(getTargetForeignKeyFields().elementAt(i), value);
        }
        return super.valueFromRow(row, joinManager, sourceQuery, executionSession);
    }

    /**
     * INTERNAL:
     * Checks if object is deleted from the database or not.
     */
    public boolean verifyDelete(Object object, AbstractSession session) throws DatabaseException {
        // Row is built for translation
        if (isReadOnly()) {
            return true;
        }

        AbstractRecord row = getDescriptor().getObjectBuilder().buildRowForTranslation(object, session);
        Object value = session.executeQuery(getSelectionQuery(), row);

        return getContainerPolicy().isEmpty(value);
    }

    /**
     *    Verifying deletes make sure that all the records privately owned by this mapping are
     * actually removed. If such records are found than those are all read and removed one
     * by one taking their privately owned parts into account.
     */
    protected void verifyDeleteForUpdate(DeleteObjectQuery query) throws DatabaseException, OptimisticLockException {
        Object objects = readPrivateOwnedForObject(query);

        // Delete all these object one by one.
        ContainerPolicy cp = getContainerPolicy();
        for (Object iter = cp.iteratorFor(objects); cp.hasNext(iter);) {
            query.getSession().deleteObject(cp.next(iter, query.getSession()));
        }
    }

    /**
     * INTERNAL:
     * Add a new value and its change set to the collection change record.  This is used by
     * attribute change tracking.  Currently it is not supported in AggregateCollectionMapping.
     */
    public void addToCollectionChangeRecord(Object newKey, Object newValue, ObjectChangeSet objectChangeSet, UnitOfWorkImpl uow) throws DescriptorException {
        throw DescriptorException.invalidMappingOperation(this, "addToCollectionChangeRecord");
    }
    
    /**
     * INTERNAL
     * Return true if this mapping supports cascaded version optimistic locking.
     */
    public boolean isCascadedLockingSupported() {
        return true;
    }
    
    /**
     * INTERNAL:
     * Return if this mapping supports change tracking.
     */
    public boolean isChangeTrackingSupported(Project project) {
        return false;
    }

    /**
     * INTERNAL:
     * Remove a value and its change set from the collection change record.  This is used by
     * attribute change tracking.  Currently it is not supported in AggregateCollectionMapping.
     */
    public void removeFromCollectionChangeRecord(Object newKey, Object newValue, ObjectChangeSet objectChangeSet, UnitOfWorkImpl uow) throws DescriptorException {
        throw DescriptorException.invalidMappingOperation(this, "removeFromCollectionChangeRecord");
    }

    /**
     * INTERNAL:
     * Once a descriptor is serialized to the remote session, all its mappings and reference descriptors are traversed.
     * Usually the mappings are initialized and the serialized reference descriptors are replaced with local descriptors
     * if they already exist in the remote session.
     */
    public void remoteInitialization(DistributedSession session) {
        super.remoteInitialization(session);
        getReferenceDescriptor().remoteInitialization(session);
    }
}