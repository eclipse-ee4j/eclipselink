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

import java.util.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.descriptors.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.queries.*;
import org.eclipse.persistence.internal.sessions.*;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.mappings.converters.*;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.sessions.remote.*;
import org.eclipse.persistence.sessions.CopyGroup;

/**
 * <code>AbstractCompositeDirectCollectionMapping</code> consolidates the behavior of mappings that
 * map collections of "native" data objects (e.g. <code>String</code>s).
 * These are objects that do not have their own descriptor and repeat within the XML record
 * for the containing object.
 *
 * @author Big Country
 * @since TOPLink/Java 3.0
 */
public abstract class AbstractCompositeDirectCollectionMapping extends DatabaseMapping implements ContainerMapping {

    /** This is the field holding the nested collection. */
    protected DatabaseField field;

    /** This is the "data type" associated with each element in the nested collection.
        Depending on the data store, this could be optional. */
    protected String elementDataTypeName;

    /** Allows user defined conversion between the object value and the database value. */
    protected Converter valueConverter;

    /** This determines the type of container used to hold the nested collection
        in the object. */
    private ContainerPolicy containerPolicy;

    /**
     * Default constructor.
     */
    public AbstractCompositeDirectCollectionMapping() {
        super();
        this.containerPolicy = ContainerPolicy.buildDefaultPolicy();
        this.elementDataTypeName = "";
        this.setWeight(WEIGHT_AGGREGATE);
    }

    /**
     * PUBLIC:
     * Return the converter on the mapping.
     * A converter can be used to convert between the direct collection's object value and database value.
     */
    public Converter getValueConverter() {
        return valueConverter;
    }

    /**
     * PUBLIC:
     * Indicates if there is a converter on the mapping.
     */
    public boolean hasValueConverter() {
        return getValueConverter() != null;
    }

    /**
     * PUBLIC:
     * Set the converter on the mapping.
     * A converter can be used to convert between the direct collection's object value and database value.
     */
    public void setValueConverter(Converter valueConverter) {
        this.valueConverter = valueConverter;
    }

    /**
     * INTERNAL:
     * Build and return a new element based on the change set.
     */
    public Object buildAddedElementFromChangeSet(Object changeSet, MergeManager mergeManager) {
        return this.buildElementFromChangeSet(changeSet, mergeManager);
    }

    /**
     * INTERNAL:
     * Clone the attribute from the clone and assign it to the backup.
     * For these mappings, this is the same as building the first clone.
     */
    public void buildBackupClone(Object clone, Object backup, UnitOfWorkImpl unitOfWork) {
        this.buildClone(clone, null, backup, unitOfWork);
    }

    /**
     * INTERNAL:
     * Build and return a change set for the specified element.
     * Direct collections simply store the element itself, since it is immutable.
     */
    public Object buildChangeSet(Object element, ObjectChangeSet owner, AbstractSession session) {
        return element;
    }

    /**
     * INTERNAL:
     * Clone the attribute from the original and assign it to the clone.
     */
    @Override
    public void buildClone(Object original, CacheKey cacheKey, Object clone, AbstractSession cloningSession) {
        Object attributeValue = this.getAttributeValueFromObject(original);
        this.setAttributeValueInObject(clone, this.buildClonePart(attributeValue, cacheKey, cloningSession));
    }

    /**
     * INTERNAL:
     * Extract value from the row and set the attribute to this value in the
     * working copy clone.
     * In order to bypass the shared cache when in transaction a UnitOfWork must
     * be able to populate working copies directly from the row.
     */
    public void buildCloneFromRow(AbstractRecord row, JoinedAttributeManager joinManager, Object clone, CacheKey sharedCacheKey, ObjectBuildingQuery sourceQuery, UnitOfWorkImpl unitOfWork, AbstractSession executionSession) {
        // for direct collection a cloned value is no different from an original value
        Object cloneAttributeValue = valueFromRow(row, joinManager, sourceQuery, sharedCacheKey, executionSession, true);
        setAttributeValueInObject(clone, cloneAttributeValue);
    }

    /**
     * Build and return a clone of the specified attribute value.
     */
    protected Object buildClonePart(Object attributeValue, CacheKey parentCacheKey, AbstractSession cloningSession) {
        if (attributeValue == null) {
            return this.getContainerPolicy().containerInstance();
        } else {
            if ((getValueConverter() == null) || (!getValueConverter().isMutable())) {
                return this.getContainerPolicy().cloneFor(attributeValue);
            }

            // Clone the values of the collection as well.
            Object cloneContainer = this.getContainerPolicy().containerInstance();
            Object iterator = this.getContainerPolicy().iteratorFor(attributeValue);
            while (this.getContainerPolicy().hasNext(iterator)) {
                Object originalValue = this.getContainerPolicy().next(iterator, (AbstractSession) cloningSession);

                // Bug 4182377 - there was a typo in the conversion logic
                Object cloneValue = getValueConverter().convertDataValueToObjectValue(getValueConverter().convertObjectValueToDataValue(originalValue, cloningSession), cloningSession);
                this.getContainerPolicy().addInto(cloneValue, cloneContainer, (AbstractSession) cloningSession);
            }
            return cloneContainer;
        }
    }

    /**
     * INTERNAL:
     * Copy of the attribute of the object.
     * This is NOT used for unit of work but for templatizing an object.
     */
    @Override
    public void buildCopy(Object copy, Object original, CopyGroup group) {
        Object attributeValue = getAttributeValueFromObject(original);
        if (attributeValue == null) {
            attributeValue = getContainerPolicy().containerInstance();
        } else {
            attributeValue = getContainerPolicy().cloneFor(attributeValue);
        }
        setAttributeValueInObject(copy, attributeValue);
    }

    /**
     * Build and return a new element based on the change set.
     * Direct collections simply store the element itself, since it is immutable.
     */
    protected Object buildElementFromChangeSet(Object changeSet, MergeManager mergeManager) {
        return changeSet;
    }

    /**
     * INTERNAL:
     * Build and return a new element based on the specified element.
     * Direct collections simply return the element itself, since it is immutable.
     */
    public Object buildElementFromElement(Object object, CacheKey elementCacheKey, MergeManager mergeManager) {
        return object;
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
    public void cascadePerformRemoveIfRequired(Object object, UnitOfWorkImpl uow, Map visitedObjects) {
        //objects referenced by this mapping are not registered as they have
        // no identity, this is a no-op.
    }

    /**
     * INTERNAL:
     * Cascade registerNew for Create through mappings that require the cascade
     */
    public void cascadeRegisterNewIfRequired(Object object, UnitOfWorkImpl uow, Map visitedObjects) {
        //objects referenced by this mapping are not registered as they have
        // no identity, this is a no-op.
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
     * Compare the non-null elements. Return true if they are alike.
     * Use #equals() to determine if two elements are the same.
     */
    public boolean compareElements(Object element1, Object element2, AbstractSession session) {
        return element1.equals(element2);
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
     * from comparing the two direct collection attributes.
     */
    public ChangeRecord compareForChange(Object clone, Object backup, ObjectChangeSet owner, AbstractSession session) {
        Object cloneAttribute = null;
        Object backUpAttribute = null;

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
                    if (!(cloneObject.equals(backUpObject))) {
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

    protected ChangeRecord convertToChangeRecord(Object cloneCollection, ObjectChangeSet owner, AbstractSession session) {
        //since a minimal update for composites can't be done, we are only recording
        //an all-or-none change. Therefore, this can be treated as a simple direct
        //value.
        ContainerPolicy cp = this.getContainerPolicy();
        Object container = cp.containerInstance();

        Object iter = cp.iteratorFor(cloneCollection);
        while (cp.hasNext(iter)) {
            cp.addInto(cp.next(iter, session), container, session);
        }
        DirectToFieldChangeRecord changeRecord = new DirectToFieldChangeRecord(owner);
        changeRecord.setAttribute(getAttributeName());
        changeRecord.setMapping(this);
        changeRecord.setNewValue(container);
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

        //iterator the first aggregate collection, comparing direct values.
        //paying attention to order.
        while (containerPolicy.hasNext(iterFirst)) {
            //fetch the next object from the first iterator.
            Object firstAggregateObject = containerPolicy.next(iterFirst, session);

            Object secondAggregateObject = containerPolicy.next(iterSecond, session);

            //matched object found, break to outer FOR loop			
            if (!firstAggregateObject.equals(secondAggregateObject)) {
                return false;
            }
        }

        return true;
    }

    /**
     * INTERNAL:
     * An object has been serialized from the server to the client.
     * Replace the transient attributes of the remote value holders
     * with client-side objects.
     */
    public void fixObjectReferences(Object object, Map objectDescriptors, Map processedObjects, ObjectLevelReadQuery query, RemoteSession session) {
        // Do nothing....
        // The nested collection should de-serialize without need for any further manipulation.
    }

    /**
     * PUBLIC:
     * Return the class each element in the object's
     * collection should be converted to, before the collection
     * is inserted into the object.
     * This is optional - if left null, the elements will be added
     * to the object's collection unconverted.
     */
    public Class getAttributeElementClass() {
        if (!(getValueConverter() instanceof TypeConversionConverter)) {
            return null;
        }
        return ((TypeConversionConverter)getValueConverter()).getObjectClass();
    }

    /**
     * INTERNAL:
     * Return the mapping's containerPolicy.
     */
    public ContainerPolicy getContainerPolicy() {
        return containerPolicy;
    }

    /**
     * INTERNAL:
     * Return the field that holds the nested collection.
     */
    public DatabaseField getField() {
        return field;
    }

    /**
     * INTERNAL:
     */
    public boolean isAbstractCompositeDirectCollectionMapping() {
        return true;
    }

    /**
     * PUBLIC:
     * Return the class each element in the database row's
     * collection should be converted to, before the collection
     * is inserted into the database.
     * This is optional - if left null, the elements will be added
     * to the database row's collection unconverted.
     */
    public Class getFieldElementClass() {
        if (!(getValueConverter() instanceof TypeConversionConverter)) {
            return null;
        }
        return ((TypeConversionConverter)getValueConverter()).getDataClass();
    }

    /**
     * PUBLIC:
     * Return the name of the field that holds the nested collection.
     */
    public String getFieldName() {
        return this.getField().getName();
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
     * INTERNAL:
     * Initialize the mapping.
     */
    public void initialize(AbstractSession session) throws DescriptorException {
        super.initialize(session);
        if (getField() == null) {
            throw DescriptorException.fieldNameNotSetInMapping(this);
        }
        setField(getDescriptor().buildField(getField()));
        setFields(collectFields());
        if (getValueConverter() != null) {
            getValueConverter().initialize(this, session);
        }
    }

    /**
     * INTERNAL:
     * Iterate on the appropriate attribute value.
     */
    public void iterate(DescriptorIterator iterator) {
        // PERF: Only iterate when required.
        if (iterator.shouldIterateOnPrimitives()) {
            Object attributeValue = this.getAttributeValueFromObject(iterator.getVisitedParent());
            if (attributeValue == null) {
                return;
            }
            ContainerPolicy cp = this.getContainerPolicy();
            for (Object iter = cp.iteratorFor(attributeValue); cp.hasNext(iter);) {
                iterator.iteratePrimitiveForMapping(cp.next(iter, iterator.getSession()), this);
            }
        }
    }

    /**
     * INTERNAL:
     * Return whether the element's user-defined Map key has changed
     * since it was cloned from the original version.
     * Direct elements are not allowed to have keys.
     */
    public boolean mapKeyHasChanged(Object element, AbstractSession session) {
        return false;
    }

    /**
     * INTERNAL:
     * Merge changes from the source to the target object. Treat the collection as a
    * simple direct value, since minimal update isn't possible.
     */
    public void mergeChangesIntoObject(Object target, CacheKey targetCacheKey, ChangeRecord changeRecord, Object source, MergeManager mergeManager) {
        if (changeRecord == null) {// I have not calculated changes then simply merge value into target
            Object targetValue = getRealAttributeValueFromObject(source, mergeManager.getSession());
            ContainerPolicy cp = this.getContainerPolicy();
            Object container = cp.containerInstance();

            Object iter = cp.iteratorFor(targetValue);
            while (cp.hasNext(iter)) {
                cp.addInto(cp.next(iter, mergeManager.getSession()), container, mergeManager.getSession());
            }
            setAttributeValueInObject(target, container);
        } else {
            setAttributeValueInObject(target, ((DirectToFieldChangeRecord)changeRecord).getNewValue());
        }
    }

    /**
     * INTERNAL:
     * Merge changes from the source to the target object. This merge is only called when a changeSet for the target
     * does not exist or the target is uninitialized.
    * Treat the collection as a simple direct value.
     */
    public void mergeIntoObject(Object target, CacheKey targetCacheKey, boolean isTargetUnInitialized, Object source, MergeManager mergeManager) {
        Object attributeValue = getAttributeValueFromObject(source);
        ContainerPolicy cp = this.getContainerPolicy();
        Object container = cp.containerInstance();

        Object iter = cp.iteratorFor(attributeValue);
        while (cp.hasNext(iter)) {
            cp.addInto(cp.next(iter, mergeManager.getSession()), container, mergeManager.getSession());
        }
        setAttributeValueInObject(target, container);
    }

    /**
     * PUBLIC:
     * Set the class each element in the object's
     * collection should be converted to, before the collection
     * is inserted into the object.
     * This is optional - if left null, the elements will be added
     * to the object's collection unconverted.
     */
    public void setAttributeElementClass(Class attributeElementClass) {
        TypeConversionConverter converter;
        if (getValueConverter() instanceof TypeConversionConverter) {
            converter = (TypeConversionConverter)getValueConverter();
        } else {
            converter = new TypeConversionConverter();
            setValueConverter(converter);
        }
        converter.setObjectClass(attributeElementClass);
    }

    /**
     * ADVANCED:
     * Set the mapping's containerPolicy.
     */
    public void setContainerPolicy(ContainerPolicy containerPolicy) {
        this.containerPolicy = containerPolicy;
    }

    /**
     * Set the field that holds the nested collection.
     */
    public void setField(DatabaseField field) {
        this.field = field;
    }

    /**
     * PUBLIC:
     * Set the class each element in the database row's
     * collection should be converted to, before the collection
     * is inserted into the database.
     * This is optional - if left null, the elements will be added
     * to the database row's collection unconverted.
     */
    public void setFieldElementClass(Class fieldElementClass) {
        TypeConversionConverter converter;
        if (getValueConverter() instanceof TypeConversionConverter) {
            converter = (TypeConversionConverter)getValueConverter();
        } else {
            converter = new TypeConversionConverter();
            setValueConverter(converter);
        }
        converter.setDataClass(fieldElementClass);
    }

    /**
     * ADVANCED:
     * This method is used to have an object add to a collection once the changeSet is applied
     * The referenceKey parameter should only be used for direct Maps.
     */
    public void simpleAddToCollectionChangeRecord(Object referenceKey, Object changeSetToAdd, ObjectChangeSet changeSet, AbstractSession session) {
        DirectToFieldChangeRecord collectionChangeRecord = (DirectToFieldChangeRecord)changeSet.getAttributesToChanges().get(getAttributeName());
        if (collectionChangeRecord == null) {
            //if there is no change for this attribute then create a changeSet for it. no need to modify the resulting
            // change record as it should be built from the clone which has the changes allready
            Object cloneObject = ((UnitOfWorkChangeSet)changeSet.getUOWChangeSet()).getUOWCloneForObjectChangeSet(changeSet);
            Object cloneCollection = this.getRealAttributeValueFromObject(cloneObject, session);
            collectionChangeRecord = (DirectToFieldChangeRecord)convertToChangeRecord(cloneCollection, changeSet, session);
            changeSet.addChange(collectionChangeRecord);
        } else {
            getContainerPolicy().addInto(changeSetToAdd, collectionChangeRecord.getNewValue(), session);
        }
    }

    /**
     * ADVANCED:
     * This method is used to have an object removed from a collection once the changeSet is applied
     * The referenceKey parameter should only be used for direct Maps.
     */
    public void simpleRemoveFromCollectionChangeRecord(Object referenceKey, Object changeSetToRemove, ObjectChangeSet changeSet, AbstractSession session) {
        DirectToFieldChangeRecord collectionChangeRecord = (DirectToFieldChangeRecord)changeSet.getAttributesToChanges().get(getAttributeName());
        if (collectionChangeRecord == null) {
            //if there is no change for this attribute then create a changeSet for it. no need to modify the resulting
            // change record as it should be built from the clone which has the changes allready
            Object cloneObject = ((UnitOfWorkChangeSet)changeSet.getUOWChangeSet()).getUOWCloneForObjectChangeSet(changeSet);
            Object cloneCollection = this.getRealAttributeValueFromObject(cloneObject, session);
            collectionChangeRecord = (DirectToFieldChangeRecord)convertToChangeRecord(cloneCollection, changeSet, session);
            changeSet.addChange(collectionChangeRecord);
        } else {
            getContainerPolicy().removeFrom(changeSetToRemove, collectionChangeRecord.getNewValue(), session);
        }
    }

    /**
     * PUBLIC:
     * Configure the mapping to use an instance of the specified container class
     * to hold the nested objects.
     * <p>jdk1.2.x: The container class must implement (directly or indirectly) the Collection interface.
     * <p>jdk1.1.x: The container class must be a subclass of Vector.
     */
    public void useCollectionClass(Class concreteClass) {
        this.setContainerPolicy(ContainerPolicy.buildPolicyFor(concreteClass));
    }

    /**
     * PUBLIC:
     * Mapping does not support Map containers.
     * It supports only Collection containers.
     */
    public void useMapClass(Class concreteClass, String methodName) {
        throw new UnsupportedOperationException(this.getClass().getName() + ".useMapClass(Class, String)");
    }

    /**
     * PUBLIC:
     * Sets whether the mapping uses a single node.
     * @param True if the items in the collection are in a single node or false if each of the items in the collection is in its own node
     */
    public void setUsesSingleNode(boolean usesSingleNode) {
        if (getField() instanceof XMLField) {
            ((XMLField)getField()).setUsesSingleNode(usesSingleNode);
        }
    }

    /**
    * PUBLIC:
    * Checks whether the mapping uses a single node.
    *
    * @returns True if the items in the collection are in a single node or false if each of the items in the collection is in its own node.
    */
    public boolean usesSingleNode() {
        if (getField() instanceof XMLField) {
            return ((XMLField)getField()).usesSingleNode();
        }
        return false;
    }

    /**
     * INTERNAL:
     * Build the nested collection from the database row.
     */
    @Override
    public Object valueFromRow(AbstractRecord row, JoinedAttributeManager joinManager, ObjectBuildingQuery sourceQuery, CacheKey cacheKey, AbstractSession executionSession, boolean isTargetProtected) throws DatabaseException {
        ContainerPolicy cp = this.getContainerPolicy();

        Object fieldValue = row.getValues(this.getField());
        if (fieldValue == null) {
            return cp.containerInstance();
        }

        Vector fieldValues = this.getDescriptor().buildDirectValuesFromFieldValue(fieldValue);
        if (fieldValues == null) {
            return cp.containerInstance();
        }

        Object result = cp.containerInstance(fieldValues.size());
        for (Enumeration stream = fieldValues.elements(); stream.hasMoreElements();) {
            Object element = stream.nextElement();
            if (this.getValueConverter() != null) {
                element = getValueConverter().convertDataValueToObjectValue(element, executionSession);
            }
            cp.addInto(element, result, sourceQuery.getSession());
        }
        return result;
    }

    /**
     * INTERNAL:
     * Get the attribute value from the object and
     * store it in the appropriate field of the row.
     */
    @Override
    public void writeFromObjectIntoRow(Object object, AbstractRecord row, AbstractSession session, WriteType writeType) {
        if (this.isReadOnly()) {
            return;
        }

        Object attributeValue = this.getAttributeValueFromObject(object);
        if (attributeValue == null) {
            row.put(this.getField(), null);
            return;
        }

        ContainerPolicy cp = this.getContainerPolicy();

        Vector elements = new Vector(cp.sizeFor(attributeValue));
        for (Object iter = cp.iteratorFor(attributeValue); cp.hasNext(iter);) {
            Object element = cp.next(iter, session);
            if (this.getValueConverter() != null) {
                element = getValueConverter().convertObjectValueToDataValue(element, session);
            }
            if (element != null) {
                elements.addElement(element);
            }
        }

        Object fieldValue = null;
        if (!elements.isEmpty()) {
            fieldValue = this.getDescriptor().buildFieldValueFromDirectValues(elements, elementDataTypeName, session);
        }
        row.put(this.getField(), fieldValue);
    }

    /**
     * INTERNAL:
     * If any part of the nested collection has changed, the whole thing is written.
     */
    @Override
    public void writeFromObjectIntoRowForUpdate(WriteObjectQuery writeQuery, AbstractRecord row) throws DescriptorException {
        AbstractSession session = writeQuery.getSession();

        if (session.isUnitOfWork()) {
            if (this.compareObjects(writeQuery.getObject(), writeQuery.getBackupClone(), session)) {
                return;// nothing is changed, no work required
            }
        }
        this.writeFromObjectIntoRow(writeQuery.getObject(), row, session, WriteType.UPDATE);
    }

    /**
     * INTERNAL:
     * Get the appropriate attribute value from the object
     * and put it in the appropriate field of the database row.
     * Loop through the reference objects and extract the
     * primary keys and put them in the vector of "nested" rows.
     */
    @Override
    public void writeFromObjectIntoRowWithChangeRecord(ChangeRecord changeRecord, AbstractRecord row, AbstractSession session, WriteType writeType) {
        Object object = ((ObjectChangeSet)changeRecord.getOwner()).getUnitOfWorkClone();
        this.writeFromObjectIntoRow(object, row, session, writeType);
    }

    /**
     * INTERNAL:
     * Write the fields needed for insert into the template with null values.
     */
    public void writeInsertFieldsIntoRow(AbstractRecord row, AbstractSession session) {
        if (this.isReadOnly()) {
            return;
        }
        row.put(this.getField(), null);
    }

    /**
     * INTERNAL:
     * Return the classifiction for the field contained in the mapping.
     * This is used to convert the row value to a consistent java value.
     * By default this is unknown.
     */
    public Class getFieldClassification(DatabaseField fieldToClassify) {
        return getAttributeElementClass();
    }

    public boolean isCollectionMapping() {
        return true;
    }
    
    public void convertClassNamesToClasses(ClassLoader classLoader){
        super.convertClassNamesToClasses(classLoader);
        
        if (valueConverter != null) {
            if (valueConverter instanceof TypeConversionConverter) {
                ((TypeConversionConverter)valueConverter).convertClassNamesToClasses(classLoader);
            } else if (valueConverter instanceof ObjectTypeConverter) {
                // To avoid 1.5 dependencies with the EnumTypeConverter check
                // against ObjectTypeConverter.
                ((ObjectTypeConverter) valueConverter).convertClassNamesToClasses(classLoader);
            }
        }         
    }

}
