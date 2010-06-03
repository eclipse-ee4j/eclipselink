/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.mappings.foundation;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.*;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.descriptors.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.queries.*;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.internal.sessions.*;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.sessions.remote.*;
import org.eclipse.persistence.sessions.CopyGroup;

public abstract class AbstractCompositeCollectionMapping extends AggregateMapping implements ContainerMapping {

    /** The aggregate objects are stored in a single field. */
    protected DatabaseField field;

    /** This determines the type of container used to hold the aggregate objects. */
    private ContainerPolicy containerPolicy;

    /** Allows user defined conversion between the object attribute value and the database value. */
    protected Converter converter;

    /**
     * Default constructor.
     */
    public AbstractCompositeCollectionMapping() {
        super();
        this.containerPolicy = ContainerPolicy.buildDefaultPolicy();
    }

    /**
     * INTERNAL:
     * Build and return a new element based on the change set.
     */
    public Object buildAddedElementFromChangeSet(Object changeSet, MergeManager mergeManager) {
        return this.buildElementFromChangeSet(changeSet, mergeManager);
    }

    /**
     * Build and return a backup clone of the attribute.
     */
    protected Object buildBackupClonePart(Object attributeValue, UnitOfWorkImpl unitOfWork) {
        ContainerPolicy cp = this.getContainerPolicy();
        if (attributeValue == null) {
            return cp.containerInstance();
        }

        Object backupAttributeValue = cp.containerInstance(cp.sizeFor(attributeValue));
        for (Object iter = cp.iteratorFor(attributeValue); cp.hasNext(iter);) {
            Object backupElement = super.buildBackupClonePart(cp.next(iter, unitOfWork), unitOfWork);
            cp.addInto(backupElement, backupAttributeValue, unitOfWork);
        }
        return backupAttributeValue;
    }

    /**
     * INTERNAL:
     * Build and return a change set for the specified element.
     */
    public Object buildChangeSet(Object element, ObjectChangeSet owner, AbstractSession session) {
        ObjectBuilder objectBuilder = this.getObjectBuilder(element, session);
        return objectBuilder.compareForChange(element, null, (UnitOfWorkChangeSet)owner.getUOWChangeSet(), session);
    }

    /**
     * Build and return a clone of the attribute.
     */
    protected Object buildClonePart(Object original, Object attributeValue, UnitOfWorkImpl unitOfWork) {
        ContainerPolicy cp = this.getContainerPolicy();
        if (attributeValue == null) {
            return cp.containerInstance();
        }

        Object clonedAttributeValue = cp.containerInstance(cp.sizeFor(attributeValue));
        for (Object iter = cp.iteratorFor(attributeValue); cp.hasNext(iter);) {
            Object cloneElement = super.buildClonePart(original, cp.next(iter, unitOfWork), unitOfWork);
            cp.addInto(cloneElement, clonedAttributeValue, unitOfWork);
        }
        return clonedAttributeValue;
    }

    /**
     * Copy of the attribute of the object.
     * This is NOT used for unit of work but for templatizing an object.
     */
    protected Object buildCopyOfAttributeValue(Object attributeValue, CopyGroup group) {
        ContainerPolicy cp = this.getContainerPolicy();
        if (attributeValue == null) {
            return cp.containerInstance();
        }

        Object attributeValueCopy = cp.containerInstance(cp.sizeFor(attributeValue));
        for (Object iter = cp.iteratorFor(attributeValue); cp.hasNext(iter);) {
            Object copyElement = super.buildCopyOfAttributeValue(cp.next(iter, group.getSession()), group);
            cp.addInto(copyElement, attributeValueCopy, group.getSession());
        }
        return attributeValueCopy;
    }

    /**
     * Build and return a new element based on the change set.
     */
    protected Object buildElementFromChangeSet(Object changeSet, MergeManager mergeManager) {
        ObjectChangeSet objectChangeSet = (ObjectChangeSet)changeSet;
        ObjectBuilder objectBuilder = this.getObjectBuilderForClass(objectChangeSet.getClassType(mergeManager.getSession()), mergeManager.getSession());
        Object result = objectBuilder.buildNewInstance();
        objectBuilder.mergeChangesIntoObject(result, objectChangeSet, null, mergeManager);

        return result;
    }

    /**
     * INTERNAL:
     * Build and return a new element based on the specified element.
     */
    public Object buildElementFromElement(Object element, MergeManager mergeManager) {
        ObjectBuilder objectBuilder = this.getObjectBuilder(element, mergeManager.getSession());
        Object result = objectBuilder.buildNewInstance();
        objectBuilder.mergeIntoObject(result, true, element, mergeManager);

        return result;
    }

    /**
     * INTERNAL:
     * Build and return a new element based on the change set.
     */
    public Object buildRemovedElementFromChangeSet(Object changeSet, MergeManager mergeManager) {
        return this.buildElementFromChangeSet(changeSet, mergeManager);
    }

    /**
     * INTERNAL:
     * Cascade perform delete through mappings that require the cascade
     */
    public void cascadePerformRemoveIfRequired(Object object, UnitOfWorkImpl uow, Map visitedObjects){
        Object cloneAttribute = null;
        cloneAttribute = getAttributeValueFromObject(object);
        if ( cloneAttribute == null ) {
            return;
        }

        ContainerPolicy cp = getContainerPolicy();
        Object cloneObjectCollection = null;
        cloneObjectCollection = getRealCollectionAttributeValueFromObject(object, uow);
        Object cloneIter = cp.iteratorFor(cloneObjectCollection);
        while (cp.hasNext(cloneIter)) {
            Object objectToCascadeOn = cp.next(cloneIter, uow);
            if (objectToCascadeOn != null && (!visitedObjects.containsKey(objectToCascadeOn)) ) {
                visitedObjects.put(objectToCascadeOn, objectToCascadeOn);
                ObjectBuilder builder = getReferenceDescriptor(objectToCascadeOn.getClass(), uow).getObjectBuilder();
                builder.cascadePerformRemove(objectToCascadeOn, uow, visitedObjects);
            }
        }
    }
    
    /**
     * INTERNAL:
     * Cascade discover and persist new objects during commit.
     */
    public void cascadeDiscoverAndPersistUnregisteredNewObjects(Object object, Map newObjects, Map unregisteredExistingObjects, Map visitedObjects, UnitOfWorkImpl uow) {
        Object cloneAttribute = getAttributeValueFromObject(object);
        if (cloneAttribute == null ) {
            return;
        }
        ContainerPolicy containerPolicy = getContainerPolicy();
        Object cloneObjectCollection = getRealCollectionAttributeValueFromObject(object, uow);
        Object iterator = containerPolicy.iteratorFor(cloneObjectCollection);
        while (containerPolicy.hasNext(iterator)) {
            Object nextObject = containerPolicy.next(iterator, uow);
            if (nextObject != null) {
                ObjectBuilder builder = getReferenceDescriptor(nextObject.getClass(), uow).getObjectBuilder();
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
        if ( cloneAttribute == null ) {
            return;
        }

        ObjectBuilder builder = null;
        ContainerPolicy cp = getContainerPolicy();
        Object cloneObjectCollection = null;
        cloneObjectCollection = getRealCollectionAttributeValueFromObject(object, uow);
        Object cloneIter = cp.iteratorFor(cloneObjectCollection);
        while (cp.hasNext(cloneIter)) {
            Object nextObject = cp.next(cloneIter, uow);
            if (nextObject != null && (! visitedObjects.containsKey(nextObject)) ) {
                visitedObjects.put(nextObject, nextObject);
                builder = getReferenceDescriptor(nextObject.getClass(), uow).getObjectBuilder();
                builder.cascadeRegisterNewForCreate(nextObject, uow, visitedObjects);
            }
        }
    }

    /**
     * Return the fields handled by the mapping.
     */
    protected Vector collectFields() {
        Vector fields = new Vector(1);
        fields.addElement(this.getField());
        return fields;
    }

    /**
     * INTERNAL:
     * Compare the non-null elements and return true if they are alike.
     */
    public boolean compareElements(Object element1, Object element2, AbstractSession session) {
        if (element1.getClass() != element2.getClass()) {
            return false;
        }
        return this.getObjectBuilder(element1, session).compareObjects(element1, element2, session);
    }

    /**
     * INTERNAL:
     * Compare the non-null elements and return true if they are alike.
     */
    public boolean compareElementsForChange(Object element1, Object element2, AbstractSession session) {
        return this.compareElements(element1, element2, session);
    }

    /**
     * INTERNAL:
     * Build and return the change record that results
     * from comparing the two aggregate collection attributes.
     */
    public ChangeRecord compareForChange(Object clone, Object backup, ObjectChangeSet owner, AbstractSession session) {
        Object cloneAttribute = null;
        Object backUpAttribute = null;
        
        // Fixed to match build-update-row.
        if (session.isClassReadOnly(this.getReferenceClass())) {
            return null;
        }
        
        cloneAttribute = getAttributeValueFromObject(clone);

        if (!owner.isNew()) {
            backUpAttribute = getAttributeValueFromObject(backup);
            if ((backUpAttribute == null) && (cloneAttribute == null)) {
                return null;
            }
            ContainerPolicy cp = getContainerPolicy();
            Object backupCollection = null;
            Object cloneCollection = null;

            cloneCollection = getRealCollectionAttributeValueFromObject(clone, session);
            backupCollection = getRealCollectionAttributeValueFromObject(backup, session);

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
     * Convert all the class-name-based settings in this mapping to actual class-based
     * settings. This method is used when converting a project that has been built
     * with class names to a project with classes.
     * @param classLoader 
     */
    public void convertClassNamesToClasses(ClassLoader classLoader){
        Class referenceClass = null;
        if(getReferenceClassName()!= null){
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
        containerPolicy.convertClassNamesToClasses(classLoader);
    };
    

    protected ChangeRecord convertToChangeRecord(Object cloneCollection, ObjectChangeSet owner, AbstractSession session) {
        ContainerPolicy cp = getContainerPolicy();
        Object cloneIter = cp.iteratorFor(cloneCollection);
        Vector collectionChanges = new Vector(2);
        while (cp.hasNext(cloneIter)) {
            Object aggregateObject = cp.next(cloneIter, session);

            // For CR#2258 quietly ignore nulls inserted into a collection.
            if (aggregateObject != null) {
                ObjectChangeSet changes = getReferenceDescriptor(aggregateObject.getClass(), session).getObjectBuilder().compareForChange(aggregateObject, null, (UnitOfWorkChangeSet)owner.getUOWChangeSet(), session);
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
     * INTERNAL:
     * Compare the attributes belonging to this mapping for the objects.
     */
    public boolean compareObjects(Object object1, Object object2, AbstractSession session) {
        Object firstCollection = getRealCollectionAttributeValueFromObject(object1, session);
        Object secondCollection = getRealCollectionAttributeValueFromObject(object2, session);
        ContainerPolicy containerPolicy = getContainerPolicy();

        if (containerPolicy.sizeFor(firstCollection) != containerPolicy.sizeFor(secondCollection)) {
            return false;
        }

        if (containerPolicy.sizeFor(firstCollection) == 0) {
            return true;
        }
        Object iterFirst = containerPolicy.iteratorFor(firstCollection);
        Object iterSecond = containerPolicy.iteratorFor(secondCollection);

        //loop through the elements in both collections and compare elements at the
        //same index. This ensures that a change to order registers as a change.
        while (containerPolicy.hasNext(iterFirst)) {
            //fetch the next object from the first iterator.
            Object firstAggregateObject = containerPolicy.next(iterFirst, session);
            Object secondAggregateObject = containerPolicy.next(iterSecond, session);

            //fetch the next object from the second iterator.
            //matched object found, break to outer FOR loop			
            if (!getReferenceDescriptor().getObjectBuilder().compareObjects(firstAggregateObject, secondAggregateObject, session)) {
                return false;
            }
        }
        return true;
    }

    /**
     * An object has been serialized from the server to the remote client.
     * Replace the transient attributes of the remote value holders
     * with client-side objects.
     */
    protected void fixAttributeValue(Object attributeValue, Map objectDescriptors, Map processedObjects, ObjectLevelReadQuery query, RemoteSession session) {
        if (attributeValue == null) {
            return;
        }
        ContainerPolicy cp = this.getContainerPolicy();
        for (Object iter = cp.iteratorFor(attributeValue); cp.hasNext(iter);) {
            super.fixAttributeValue(cp.next(iter, session), objectDescriptors, processedObjects, query, session);
        }
    }

    /**
     * Return the appropriate attribute value.
     * This method is a hack to allow the aggregate collection
     * subclass to override....
     * The intent is to return the aggregate object in the backup clone
     * that corresponds to the one in the working copy.
     * Since we don't know which element in the backup clone
     * collection corresponds any given element in the working copy
     * collection (there is no "primary key"); we simply return null,
     * which will cause a new, empty, instance to be built and used
     * for comparison.
     */
    protected Object getAttributeValueFromBackupClone(Object backupClone) {
        return null;
    }

    /**
     * INTERNAL:
     * Return the mapping's containerPolicy.
     */
    public ContainerPolicy getContainerPolicy() {
        return containerPolicy;
    }

    /**
     * PUBLIC:
     * Return the converter on the mapping.
     * A converter can be used to convert between the object's value and database value of the attribute.
     */
    public Converter getConverter() {
        return converter;
    }

    /**
     * INTERNAL:
     * Return the field mapped by this mapping.
     */
    public DatabaseField getField() {
        return field;
    }

    /**
     * INTERNAL:
     * Convenience method.
     * Return the value of an attribute, unwrapping value holders if necessary.
     * If the value is null, build a new container.
     */
    public Object getRealCollectionAttributeValueFromObject(Object object, AbstractSession session) throws DescriptorException {
        Object value = this.getRealAttributeValueFromObject(object, session);
        if (value == null) {
            value = this.getContainerPolicy().containerInstance(1);
        }
        return value;
    }

    /**
     * This is required for ObjectArrayMapping which defines a name for the collection type.
     * Currently this type name is not required or used in general with the SDK.
     */
    protected String getStructureName() {
        return "";
    }

    /**
     * PUBLIC:
     * Indicates if there is a converter on the mapping.
     */
    public boolean hasConverter() {
        return getConverter() != null;
    }

    /**
     * INTERNAL:
     */
    public boolean isAbstractCompositeCollectionMapping() {
        return true;
    }

    /**
     * INTERNAL:
     * The mapping is initialized with the given session. This mapping is fully initialized
     * after this.
     */
    public void initialize(AbstractSession session) throws DescriptorException {
        super.initialize(session);
        if (getField() == null) {
            throw DescriptorException.fieldNameNotSetInMapping(this);
        }
        setField(getDescriptor().buildField(getField()));
        setFields(collectFields());
        if (hasConverter()) {
            getConverter().initialize(this, session);
        }
    }

    /**
     * Iterate on the specified attribute value.
     */
    protected void iterateOnAttributeValue(DescriptorIterator descriptorIterator, Object attributeValue) {
        if (attributeValue == null) {
            return;
        }
        ContainerPolicy cp = this.getContainerPolicy();
        for (Object iter = cp.iteratorFor(attributeValue); cp.hasNext(iter);) {
            super.iterateOnAttributeValue(descriptorIterator, cp.next(iter, descriptorIterator.getSession()));
        }
    }

    /**
     * INTERNAL:
     * Return whether the element's user-defined Map key has changed
     * since it was cloned from the original version.
     * Aggregate elements cannot change their keys without detection.
     */
    public boolean mapKeyHasChanged(Object element, AbstractSession session) {
        return false;
    }

    /**
     * INTERNAL:
     * Merge changes from the source to the target object.
     */
    public void mergeChangesIntoObject(Object target, ChangeRecord changeRecord, Object source, MergeManager mergeManager) {
        ContainerPolicy containerPolicy = getContainerPolicy();
        AbstractSession session = mergeManager.getSession();
        // Iterate over the changes and merge the collections
        Vector aggregateObjects = ((AggregateCollectionChangeRecord)changeRecord).getChangedValues();
        Object valueOfTarget = containerPolicy.containerInstance();
        // Next iterate over the changes and add them to the container
        ObjectChangeSet objectChanges = null;
        int size = aggregateObjects.size();
        for (int index = 0; index < size; ++index) {
            objectChanges = (ObjectChangeSet)aggregateObjects.get(index);
            // Since the CompositeCollectionMapping only registers an all or none
            // change set, we can simply replace the entire collection;
            containerPolicy.addInto(buildElementFromChangeSet(objectChanges, mergeManager), valueOfTarget, session);
        }
        setRealAttributeValueInObject(target, valueOfTarget);
    }

    /**
     * INTERNAL:
     * Merge changes from the source to the target object.
     * Simply replace the entire target collection.
     */
    public void mergeIntoObject(Object target, boolean isTargetUnInitialized, Object source, MergeManager mergeManager) {
        if (!mergeManager.shouldCascadeReferences()) {
            // This is only going to happen on mergeClone, and we should not attempt to merge the reference
            return;
        }

        ContainerPolicy containerPolicy = getContainerPolicy();
        Object valueOfSource = getRealCollectionAttributeValueFromObject(source, mergeManager.getSession());
        Object valueOfTarget = containerPolicy.containerInstance(containerPolicy.sizeFor(valueOfSource));
        for (Object sourceValuesIterator = containerPolicy.iteratorFor(valueOfSource);
                 containerPolicy.hasNext(sourceValuesIterator);) {
            Object sourceValue = containerPolicy.next(sourceValuesIterator, mergeManager.getSession());

            //CR#2896 - TW
            Object originalValue = getReferenceDescriptor(sourceValue.getClass(), mergeManager.getSession()).getObjectBuilder().buildNewInstance();
            getReferenceDescriptor(sourceValue.getClass(), mergeManager.getSession()).getObjectBuilder().mergeIntoObject(originalValue, true, sourceValue, mergeManager);
            containerPolicy.addInto(originalValue, valueOfTarget, mergeManager.getSession());
        }

        // Must re-set variable to allow for set method to re-morph changes if the collection is not being stored directly.
        setRealAttributeValueInObject(target, valueOfTarget);
    }

    /**
     * The message is passed to its reference class descriptor.
     */
    public void postDeleteAttributeValue(DeleteObjectQuery query, Object attributeValue) throws DatabaseException, OptimisticLockException {
        if (attributeValue == null) {
            return;
        }
        ContainerPolicy cp = this.getContainerPolicy();
        for (Object iter = cp.iteratorFor(attributeValue); cp.hasNext(iter);) {
            super.postDeleteAttributeValue(query, cp.next(iter, query.getSession()));
        }
    }

    /**
     * The message is passed to its reference class descriptor.
     */
    public void postInsertAttributeValue(WriteObjectQuery query, Object attributeValue) throws DatabaseException, OptimisticLockException {
        if (attributeValue == null) {
            return;
        }
        ContainerPolicy cp = this.getContainerPolicy();
        for (Object iter = cp.iteratorFor(attributeValue); cp.hasNext(iter);) {
            super.postInsertAttributeValue(query, cp.next(iter, query.getSession()));
        }
    }

    /**
     * The message is passed to its reference class descriptor.
     */
    public void postUpdateAttributeValue(WriteObjectQuery query, Object attributeValue) throws DatabaseException, OptimisticLockException {
        if (attributeValue == null) {
            return;
        }
        ContainerPolicy cp = this.getContainerPolicy();
        for (Object iter = cp.iteratorFor(attributeValue); cp.hasNext(iter);) {
            super.postUpdateAttributeValue(query, cp.next(iter, query.getSession()));
        }
    }

    /**
     * The message is passed to its reference class descriptor.
     */
    public void preDeleteAttributeValue(DeleteObjectQuery query, Object attributeValue) throws DatabaseException, OptimisticLockException {
        if (attributeValue == null) {
            return;
        }
        ContainerPolicy cp = this.getContainerPolicy();
        for (Object iter = cp.iteratorFor(attributeValue); cp.hasNext(iter);) {
            super.preDeleteAttributeValue(query, cp.next(iter, query.getSession()));
        }
    }

    /**
     * The message is passed to its reference class descriptor.
     */
    public void preInsertAttributeValue(WriteObjectQuery query, Object attributeValue) throws DatabaseException, OptimisticLockException {
        if (attributeValue == null) {
            return;
        }
        ContainerPolicy cp = this.getContainerPolicy();
        for (Object iter = cp.iteratorFor(attributeValue); cp.hasNext(iter);) {
            super.preInsertAttributeValue(query, cp.next(iter, query.getSession()));
        }
    }

    /**
     * The message is passed to its reference class descriptor.
     */
    public void preUpdateAttributeValue(WriteObjectQuery query, Object attributeValue) throws DatabaseException, OptimisticLockException {
        if (attributeValue == null) {
            return;
        }
        ContainerPolicy cp = this.getContainerPolicy();
        for (Object iter = cp.iteratorFor(attributeValue); cp.hasNext(iter);) {
            super.preUpdateAttributeValue(query, cp.next(iter, query.getSession()));
        }
    }

    /**
     * ADVANCED:
     * Set the mapping's containerPolicy.
     */
    public void setContainerPolicy(ContainerPolicy containerPolicy) {
        this.containerPolicy = containerPolicy;
    }

    /**
     * PUBLIC:
     * Set the converter on the mapping.
     * A converter can be used to convert between the object's value and database value of the attribute.
     */
    public void setConverter(Converter converter) {
        this.converter = converter;
    }

    /**
     * Set the field in the mapping.
     */
    public void setField(DatabaseField field) {
        this.field = field;
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
     * PUBLIC:
     * Configure the mapping to use an instance of the specified container class
     * to hold the target objects.
     * <p>jdk1.2.x: The container class must implement (directly or indirectly) the Collection interface.
     * <p>jdk1.1.x: The container class must be a subclass of Vector.
     */
    public void useCollectionClass(Class concreteContainerClass) {
        this.setContainerPolicy(ContainerPolicy.buildPolicyFor(concreteContainerClass));
    }

    public void useCollectionClassName(String concreteContainerClassName) {
        this.setContainerPolicy(new CollectionContainerPolicy(concreteContainerClassName));
    }

    public void useListClassName(String concreteContainerClassName) {
        this.setContainerPolicy(new ListContainerPolicy(concreteContainerClassName));
    }

    /**
     * PUBLIC:
     * Configure the mapping to use an instance of the specified container class
     * to hold the target objects. The key used to index the value in the Map
     * is the value returned by a call to the specified zero-argument method.
     * The method must be implemented by the class (or a superclass) of the
     * value to be inserted into the Map.
     * <p>jdk1.2.x: The container class must implement (directly or indirectly) the Map interface.
     * <p>jdk1.1.x: The container class must be a subclass of Hashtable.
     * <p>The referenceClass must be set before calling this method.
     */
    public void useMapClass(Class concreteContainerClass, String methodName) {
        // the reference class has to be specified before coming here
        if (this.getReferenceClass() == null) {
            throw DescriptorException.referenceClassNotSpecified(this);
        }
        ContainerPolicy policy = ContainerPolicy.buildPolicyFor(concreteContainerClass);
        policy.setKeyName(methodName, getReferenceClass());
        this.setContainerPolicy(policy);
    }

    public void useMapClassName(String concreteContainerClassName, String methodName) {
        // the reference class has to be specified before coming here
        if (this.getReferenceClassName() == null) {
            throw DescriptorException.referenceClassNotSpecified(this);
        }
        MapContainerPolicy policy = new MapContainerPolicy(concreteContainerClassName);
        policy.setKeyName(methodName, getReferenceClass().getName());
        this.setContainerPolicy(policy);
    }

    /**
     * INTERNAL:
     * Build and return an aggregate collection from the specified row.
     */
    public Object valueFromRow(AbstractRecord row, JoinedAttributeManager joinManager, ObjectBuildingQuery sourceQuery, AbstractSession executionSession) throws DatabaseException {
        ContainerPolicy cp = this.getContainerPolicy();

        Object fieldValue = row.getValues(this.getField());

        // BUG#2667762 there could be whitespace in the row instead of null
        if ((fieldValue == null) || (fieldValue instanceof String)) {
            return cp.containerInstance();
        }

        Vector nestedRows = this.getReferenceDescriptor().buildNestedRowsFromFieldValue(fieldValue, executionSession);
        if (nestedRows == null) {
            return cp.containerInstance();
        }

        Object result = cp.containerInstance(nestedRows.size());
        for (Enumeration stream = nestedRows.elements(); stream.hasMoreElements();) {
        	AbstractRecord nestedRow = (AbstractRecord)stream.nextElement();

            ClassDescriptor descriptor = this.getReferenceDescriptor();
            if (descriptor.hasInheritance()) {
                Class newElementClass = descriptor.getInheritancePolicy().classFromRow(nestedRow, executionSession);
                descriptor = this.getReferenceDescriptor(newElementClass, executionSession);
            }

            Object element = buildCompositeObject(descriptor, nestedRow, sourceQuery, joinManager);
            if (hasConverter()) {
                element = getConverter().convertDataValueToObjectValue(element, executionSession);
            }
            cp.addInto(element, result, sourceQuery.getSession());
        }
        return result;
    }

    protected abstract Object buildCompositeObject(ClassDescriptor descriptor, AbstractRecord nestedRow, ObjectBuildingQuery query, JoinedAttributeManager joinManger);

    /**
     * Return whether the specified object and all its components have been deleted.
     */
    protected boolean verifyDeleteOfAttributeValue(Object attributeValue, AbstractSession session) throws DatabaseException {
        if (attributeValue == null) {
            return true;
        }
        ContainerPolicy cp = this.getContainerPolicy();
        for (Object iter = cp.iteratorFor(attributeValue); cp.hasNext(iter);) {
            if (!super.verifyDeleteOfAttributeValue(cp.next(iter, session), session)) {
                return false;
            }
        }
        return true;
    }

    /**
     * INTERNAL:
     * Get the attribute value from the object and add the appropriate
     * values to the specified database row.
     */
    @Override
    public void writeFromObjectIntoRow(Object object, AbstractRecord row, AbstractSession session, WriteType writeType) throws DescriptorException {
        if (this.isReadOnly()) {
            return;
        }

        Object attributeValue = this.getAttributeValueFromObject(object);
        if (attributeValue == null) {
            row.put(this.getField(), null);
            return;
        }

        ContainerPolicy cp = this.getContainerPolicy();

        Vector nestedRows = new Vector(cp.sizeFor(attributeValue));
        for (Object iter = cp.iteratorFor(attributeValue); cp.hasNext(iter);) {
            Object element = cp.next(iter, session);
            // convert the value - if necessary
            if (hasConverter()) {
                element = getConverter().convertObjectValueToDataValue(element, session);
            }
            nestedRows.addElement(buildCompositeRow(element, session, row, writeType));
        }

        Object fieldValue = null;
        if (!nestedRows.isEmpty()) {
            fieldValue = this.getReferenceDescriptor().buildFieldValueFromNestedRows(nestedRows, getStructureName(), session);
        }
        row.put(this.getField(), fieldValue);
    }

    protected abstract AbstractRecord buildCompositeRow(Object attributeValue, AbstractSession session, AbstractRecord record, WriteType writeType);

    /**
     * INTERNAL:
     * Get the attribute value from the object and add the changed
     * values to the specified database row.
     */
    public void writeFromObjectIntoRowForUpdate(WriteObjectQuery writeQuery, AbstractRecord row) throws DescriptorException {
        AbstractSession session = writeQuery.getSession();

        //Helper.toDo("bjv: need to figure out how to handle read-only elements...");
        if (session.isClassReadOnly(this.getReferenceClass())) {
            return;
        }

        if (session.isUnitOfWork()) {
            if (this.compareObjects(writeQuery.getObject(), writeQuery.getBackupClone(), session)) {
                return;// nothing has changed - don't put anything in the row
            }
        }
        this.writeFromObjectIntoRow(writeQuery.getObject(), row, session, WriteType.UPDATE);
    }

    /**
     * INTERNAL:
     * Get the attribute value from the object and add the appropriate
     * values to the specified database row.
     */
    @Override
    public void writeFromObjectIntoRowWithChangeRecord(ChangeRecord changeRecord, AbstractRecord row, AbstractSession session, WriteType writeType) throws DescriptorException {
        Object object = ((ObjectChangeSet)changeRecord.getOwner()).getUnitOfWorkClone();
        this.writeFromObjectIntoRow(object, row, session, writeType);
    }

    /**
     * INTERNAL:
     * Write fields needed for insert into the template with null values.
     */
    public void writeInsertFieldsIntoRow(AbstractRecord record, AbstractSession session) {
        if (this.isReadOnly()) {
            return;
        }
        record.put(this.getField(), null);
    }

    public boolean isCollectionMapping() {
        return true;
    }

}
