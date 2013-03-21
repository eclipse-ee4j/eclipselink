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
 ******************************************************************************/  
package org.eclipse.persistence.mappings.foundation;

import java.util.*;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.descriptors.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.queries.JoinedAttributeManager;
import org.eclipse.persistence.internal.sessions.*;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.queries.*;

/**
 * Chunks of data from non-relational data sources can have an
 * embedded component objects. These can be
 * mapped using this mapping. The format of the embedded
 * data is determined by the reference descriptor.
 *
 * @author Big Country
 * @since TOPLink/Java 3.0
 */
public abstract class AbstractCompositeObjectMapping extends AggregateMapping {

    /** The aggregate object is stored in a single field. */
    protected DatabaseField field;

    /** Allows user defined conversion between the object attribute value and the database value. */
    protected Converter converter;
    
    /**
     * Default constructor.
     */
    public AbstractCompositeObjectMapping() {
        super();
    }

    /**
     * INTERNAL:
     * Cascade perform delete through mappings that require the cascade
     */
    public void cascadePerformRemoveIfRequired(Object object, UnitOfWorkImpl uow, Map visitedObjects) {
        //objects referenced by this mapping are not registered as they have
        // no identity, however mappings from the referenced object may need cascading.
        Object objectReferenced = getRealAttributeValueFromObject(object, uow);
        if (objectReferenced == null) {
            return;
        }
        if (!visitedObjects.containsKey(objectReferenced)) {
            visitedObjects.put(objectReferenced, objectReferenced);
            ObjectBuilder builder = getReferenceDescriptor(objectReferenced.getClass(), uow).getObjectBuilder();
            builder.cascadePerformRemove(objectReferenced, uow, visitedObjects);
        }
    }
    
    /**
     * INTERNAL:
     * Cascade discover and persist new objects during commit.
     */
    public void cascadeDiscoverAndPersistUnregisteredNewObjects(Object object, Map newObjects, Map unregisteredExistingObjects, Map visitedObjects, UnitOfWorkImpl uow, Set cascadeErrors) {
        Object objectReferenced = getRealAttributeValueFromObject(object, uow);
        if (objectReferenced != null) {
            ObjectBuilder builder = getReferenceDescriptor(objectReferenced.getClass(), uow).getObjectBuilder();
            builder.cascadeRegisterNewForCreate(objectReferenced, uow, visitedObjects);
        }
    }
    
    /**
     * INTERNAL:
     * Cascade registerNew for Create through mappings that require the cascade
     */
    public void cascadeRegisterNewIfRequired(Object object, UnitOfWorkImpl uow, Map visitedObjects) {
        //aggregate objects are not registered but their mappings should be.
        Object objectReferenced = getRealAttributeValueFromObject(object, uow);
        if (objectReferenced == null) {
            return;
        }
        if (!visitedObjects.containsKey(objectReferenced)) {
            visitedObjects.put(objectReferenced, objectReferenced);
            ObjectBuilder builder = getReferenceDescriptor(objectReferenced.getClass(), uow).getObjectBuilder();
            builder.cascadeRegisterNewForCreate(objectReferenced, uow, visitedObjects);
        }
    }

    /**
     * Return the fields mapped by the mapping.
     */
    protected Vector collectFields() {
        Vector fields = new Vector(1);
        fields.addElement(this.getField());
        return fields;
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
     * The aggregate object is held in a single field.
     */
    public DatabaseField getField() {
        return field;
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
    public boolean isAbstractCompositeObjectMapping() {
        return true;
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
        // initialize the converter - if necessary
        if (hasConverter()) {
            getConverter().initialize(this, session);
        }
    }

    /**
     * INTERNAL:
     * Set the value of the attribute mapped by this mapping.
     */
    public void setAttributeValueInObject(Object object, Object value) throws DescriptorException {
        // PERF: Direct variable access.
        try {
            this.attributeAccessor.setAttributeValueInObject(object, value);
        } catch (DescriptorException exception) {
            exception.setMapping(this);
            throw exception;
        }
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
     * The aggregate object is held in a single field.
     */
    public void setField(DatabaseField field) {
        this.field = field;
    }

    /**
     * INTERNAL:
     * Extract and return value of the field from the object
     */
    public Object valueFromObject(Object object, DatabaseField field, AbstractSession session) throws DescriptorException {
        Object attributeValue = this.getAttributeValueFromObject(object);
        if(this.getConverter() != null) {
            this.getConverter().convertObjectValueToDataValue(attributeValue, session);
        }
        if (attributeValue == null) {
            return null;
        } else {
            return this.getObjectBuilder(attributeValue, session).extractValueFromObjectForField(attributeValue, field, session);
        }
    }

    /**
     * INTERNAL:
     * Extract and return the aggregate object from
     * the specified row.
     */
    public Object valueFromRow(AbstractRecord row, JoinedAttributeManager joinManager, ObjectBuildingQuery sourceQuery, CacheKey cacheKey, AbstractSession executionSession, boolean isTargetProtected, Boolean[] wasCacheUsed) throws DatabaseException {
        if (this.descriptor.getCachePolicy().isProtectedIsolation()) {
            if (this.isCacheable && isTargetProtected && cacheKey != null) {
                //cachekey will be null when isolating to uow
                //used cached collection
                Object result = null;
                Object cached = cacheKey.getObject();
                if (cached != null) {
                    if (wasCacheUsed != null){
                        wasCacheUsed[0] = Boolean.TRUE;
                    }
                    Object attributeValue = this.getAttributeValueFromObject(cached);
                    Integer refreshCascade = null;
                    if (sourceQuery != null && sourceQuery.isObjectBuildingQuery() && ((ObjectBuildingQuery)sourceQuery).shouldRefreshIdentityMapResult()){
                        refreshCascade = sourceQuery.getCascadePolicy();
                    }
                    //get the clone root.
                    return buildClonePart(cached, executionSession.getIdentityMapAccessor().getFromIdentityMap(cacheKey.getKey(), referenceClass), cacheKey, attributeValue, refreshCascade, executionSession);
                }
                return result;
                
            } else if (!this.isCacheable && !isTargetProtected && (cacheKey != null)) {
                return null;
            }
        }
        Object fieldValue = row.get(this.field);

        // BUG#2667762 there could be whitespace in the row instead of null
        if ((fieldValue == null) || (fieldValue instanceof String)) {
            return null;
        }

        // pretty sure we can ignore inheritance here:
        AbstractRecord nestedRow = this.referenceDescriptor.buildNestedRowFromFieldValue(fieldValue);

        ClassDescriptor descriptor = this.referenceDescriptor;
        if (descriptor.hasInheritance()) {
            Class nestedElementClass = descriptor.getInheritancePolicy().classFromRow(nestedRow, executionSession);
            descriptor = getReferenceDescriptor(nestedElementClass, executionSession);
        }
        ObjectBuilder objectBuilder = descriptor.getObjectBuilder();
        Object toReturn = buildCompositeObject(objectBuilder, nestedRow, sourceQuery, cacheKey, joinManager, executionSession);
        if (this.converter != null) {
            toReturn = this.converter.convertDataValueToObjectValue(toReturn, executionSession);
        }
        return toReturn;
    }

    /**
     * INTERNAL:
     * Builds a shallow original object.  Only direct attributes and primary
     * keys are populated.  In this way the minimum original required for
     * instantiating a working copy clone can be built without placing it in
     * the shared cache (no concern over cycles).
     */
    public void buildShallowOriginalFromRow(AbstractRecord row, Object original, JoinedAttributeManager joinManager, ObjectBuildingQuery sourceQuery, AbstractSession executionSession) {
        Object fieldValue = row.get(this.getField());

        // BUG#2667762 there could be whitespace in the row instead of null
        if ((fieldValue == null) || (fieldValue instanceof String)) {
            return;
        }

        // pretty sure we can ignore inheritance here:
        AbstractRecord nestedRow = this.getReferenceDescriptor().buildNestedRowFromFieldValue(fieldValue);

        ClassDescriptor descriptor = this.getReferenceDescriptor();
        if (descriptor.hasInheritance()) {
            Class nestedElementClass = descriptor.getInheritancePolicy().classFromRow(nestedRow, executionSession);
            descriptor = this.getReferenceDescriptor(nestedElementClass, executionSession);
        }
        ObjectBuilder objectBuilder = descriptor.getObjectBuilder();

        // instead of calling buildCompositeObject, which calls either objectBuilder.
        // buildObject or buildNewInstance and buildAttributesIntoObject, do the
        // following always.  Since shallow original no concern over cycles or caching.
        Object element = objectBuilder.buildNewInstance();
        objectBuilder.buildAttributesIntoShallowObject(element, nestedRow, sourceQuery);

        setAttributeValueInObject(original, element);
    }

    protected abstract Object buildCompositeObject(ObjectBuilder objectBuilder, AbstractRecord nestedRow, ObjectBuildingQuery query, CacheKey parentCacheKey, JoinedAttributeManager joinManger, AbstractSession targetSession);

    /**
     * INTERNAL:
     * Build the value for the database field and put it in the
     * specified database row.
     */
    @Override
    public void writeFromObjectIntoRow(Object object, AbstractRecord record, AbstractSession session, WriteType writeType) throws DescriptorException {
        if (this.isReadOnly()) {
            return;
        }
        Object attributeValue = this.getAttributeValueFromObject(object);
        if(getConverter() != null) {
            getConverter().convertObjectValueToDataValue(attributeValue, session);
        }
        if (attributeValue == null) {
            record.put(this.getField(), null);
        } else {
            Object fieldValue = buildCompositeRow(attributeValue, session, record, writeType);
            record.put(this.getField(), fieldValue);
        }
    }

    protected abstract Object buildCompositeRow(Object attributeValue, AbstractSession session, AbstractRecord record, WriteType writeType);

    /**
     * INTERNAL:
     * If it has changed, build the value for the database field and put it in the
     * specified database row.
     * If any part of the aggregate object has changed, the entire object is
     * written to the database row (i.e. partial updates are not supported).
     */
    @Override
    public void writeFromObjectIntoRowForUpdate(WriteObjectQuery query, AbstractRecord row) throws DescriptorException {
        if (query.getSession().isUnitOfWork()) {
            if (this.compareObjects(query.getObject(), query.getBackupClone(), query.getSession())) {
                return;// nothing has changed
            }
        }
        this.writeFromObjectIntoRow(query.getObject(), row, query.getSession(), WriteType.UPDATE);
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
     * Write fields needed for insert into the template for with null values.
     */
    public void writeInsertFieldsIntoRow(AbstractRecord record, AbstractSession session) {
        if (this.isReadOnly()) {
            return;
        }
        record.put(this.getField(), null);
    }
}
