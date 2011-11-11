/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     11/13/2009-2.0  mobrien - 294765: MapKey keyType DirectToField processing 
 *       should return attributeClassification class in getMapKeyTargetType when 
 *       accessor.attributeField is null in the absence of a MapKey annotation
 *     11/10/2011-2.4 Guy Pelletier 
 *       - 357474: Address primaryKey option from tenant discriminator column
 ******************************************************************************/  
package org.eclipse.persistence.mappings.foundation;

import java.util.*;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.internal.descriptors.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.queries.JoinedAttributeManager;
import org.eclipse.persistence.internal.sessions.*;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.sessions.remote.*;
import org.eclipse.persistence.sessions.Session;

/**
 * <b>Purpose</b>: Maps an attribute or some other property to the corresponding 
 * database field type. The list of field types that are supported by 
 * EclipseLink's direct to field mapping is dependent on the relational database 
 * being used.
 * 
 * @see org.eclipse.persistence.mappings.foundation.AbstractAttributeDirectMapping
 * @see org.eclipse.persistence.mappings.foundation.MultitenantPrimaryKeyMapping
 *
 * @author Sati
 * @since TopLink/Java 1.0
 */
public abstract class AbstractDirectMapping extends DatabaseMapping {
    
    /** DatabaseField which this mapping represents. */
    protected DatabaseField field;

    /**
     * Default constructor.
     */
    public AbstractDirectMapping() {
        super();
        this.setWeight(WEIGHT_DIRECT);
    }

    /**
     * PUBLIC:
     * Return true if the attribute for this mapping is a simple atomic value 
     * that cannot be modified, only replaced.
     * This is false by default unless a mutable converter is used such as the 
     * SerializedObjectConverter.
     * This can be set to false in this case, or if a Calendar or byte[] is 
     * desired to be used as a mutable value it can be set to true.
     */
    public boolean isMutable() {
        return false;
    }

    /**
     * PUBLIC:
     * By default mappings are insertable. Return false otherwise.
     */
    public boolean isInsertable() {
        return true;
    }
    
    /**
     * PUBLIC:
     * By default mappings are updatable. Return false otherwise.
     */
    public boolean isUpdatable() {
        return true;
    }
    
    /**
     * PUBLIC:
     * Return true if the attribute for this mapping is a simple atomic value 
     * that cannot be modified, only replaced.
     * This is false by default unless a mutable converter is used such as the 
     * SerializedObjectConverter.
     * This can be set to false in this case, or if a Calendar or byte[] is 
     * desired to be used as a mutable value it can be set to true.
     */
    public void setIsMutable(boolean isMutable) {
        // does nothing at this level.
    }
    
    /**
     * PUBLIC:
     * Allow for the value used for null to be specified. This can be used to 
     * convert database null values to application specific values, when null 
     * values are not allowed by the application (such as in primitives). 
     * Note: the default value for NULL is used on reads, writes, and query SQL 
     * generation. Subclasses should overwrite this method.
     */
    public void setNullValue(Object nullValue) {
        // does nothing at this level
    }
    
    /**
     * INTERNAL:
     * In case Query By Example is used, this method builds and returns an expression that
     * corresponds to a single attribute and it's value for a directToField mapping.
     */
    @Override
    public Expression buildExpression(Object queryObject, QueryByExamplePolicy policy, Expression expressionBuilder, Map processedObjects, AbstractSession session) {
        String attributeName = this.getAttributeName();
        Object attributeValue = this.getAttributeValueFromObject(queryObject);

        if (!policy.shouldIncludeInQuery(queryObject.getClass(), attributeName, attributeValue)) {
            //the attribute name and value pair is not to be included in the query.
            return null;
        }

        Expression expression = expressionBuilder.get(attributeName);
        if (attributeValue == null) {
            expression = policy.completeExpressionForNull(expression);
        } else {
            expression = policy.completeExpression(expression, attributeValue, attributeValue.getClass());
        }

        return expression;
    }
    
    /**
     * INTERNAL:
     * Cascade perform delete through mappings that require the cascade.
     */
    public void cascadePerformRemoveIfRequired(Object object, UnitOfWorkImpl uow, Map visitedObjects) {
        // objects referenced by this mapping are not registered as they have
        // no identity, this is a no-op.
    }

    /**
     * INTERNAL:
     * Cascade registerNew for Create through mappings that require the cascade.
     */
    public void cascadeRegisterNewIfRequired(Object object, UnitOfWorkImpl uow, Map visitedObjects) {
        // objects referenced by this mapping are not registered as they have
        // no identity, this is a no-op.
    }

    /**
     * INTERNAL:
     * The mapping clones itself to create deep copy.
     */
    @Override
    public Object clone() {
        AbstractDirectMapping clone = (AbstractDirectMapping)super.clone();

        // Field must be cloned so aggregates do not share fields.
        clone.setField(getField().clone());

        return clone;
    }

    /**
     * Returns the field this mapping represents.
     */
    @Override
    protected Vector<DatabaseField> collectFields() {
        Vector databaseField = new Vector(1);
        databaseField.addElement(getField());
        return databaseField;
    }
    
    /**
     * INTERNAL:
     * An object has been serialized from the server to the client.
     * Replace the transient attributes of the remote value holders
     * with client-side objects.
     */
    @Override
    public void fixObjectReferences(Object object, Map objectDescriptors, Map processedObjects, ObjectLevelReadQuery query, RemoteSession session) {
    }

    /**
     * PUBLIC:
     * Return the converter on the mapping. By default there isn't one unless
     * a sub class supports it.
     * A converter can be used to convert between the object's value and 
     * database value of the attribute. 
     */
    public Converter getConverter() {
        return null;
    }
        
    /**
     * INTERNAL:
     * Returns the field which this mapping represents.
     */
    public DatabaseField getField() {
        return field;
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean isAbstractDirectMapping() {
        return true;
    }

    /**
     * INTERNAL:
     * Get the descriptor for this mapping
     * This method is potentially called when this mapping is used as a map key and
     * will return null since direct mappings do not have reference descriptors.
     */
    public ClassDescriptor getReferenceDescriptor(){
        return null;
    }
    
    /**
     * ADVANCED:
     * Return the class type of the field value.
     * This can be used if field value differs from the object value,
     * has specific typing requirements such as usage of java.sql.Blob or NChar.
     */
    public Class getFieldClassification() {
        if (getField() == null) {
            return null;
        }
        return getField().getType();
    }

    /**
     * ADVANCED:
     * Set the class type of the field value.
     * This can be used if field value differs from the object value,
     * has specific typing requirements such as usage of java.sql.Blob or NChar.
     * This must be called after the field name has been set.
     */
    public void setFieldClassification(Class fieldType) {
        getField().setType(fieldType);
    }

    /**
     * ADVANCED:
     * Set the JDBC type of the field value.
     * This can be used if field type does not correspond directly to a Java class type,
     * such as MONEY.
     * This is used for binding.
     */
    public void setFieldType(int jdbcType) {
        getField().setSqlType(jdbcType);
    }

    /**
     * PUBLIC:
     * Name of the field this mapping represents.
     */
    public String getFieldName() {
        return getField().getQualifiedName();
    }

    /**
     * INTERNAL:
     * Convert the object (attribute or property) value to a field value.
     */
    public abstract Object getFieldValue(Object objectValue, AbstractSession session);
    
    /**
     * INTERNAL:
     * Return the class name of the attribute type (if there is one).
     * This is only used by the MW.
     * 
     * @see org.eclipse.persistence.mappings.foundation.AbstractAttributeDirectMapping.getAttributeClassificationName()
     */
    @Deprecated
    public String getAttributeClassificationName() {
        return null;
    }
    
    /**
     * INTERNAL:
     * Allows for subclasses to convert the attribute value. Deprecated this
     * method versus removing all together since a customer may have subclassed 
     * this class?
     * 
     * @see getObjectValue(Object, Session)
     * @see AbstractAttributeDirectMapping which implements getAttributeValue()
     */
    @Deprecated
    public Object getAttributeValue(Object fieldValue, Session session) {
        return getObjectValue(fieldValue, session);
    }
    
    /**
     * PUBLIC:
     * Allow for the value used for null to be specified. This can be used to 
     * convert database null values to application specific values, when null 
     * values are not allowed by the application (such as in primitives).
     * Note: the default value for NULL is used on reads, writes, and query SQL 
     * generation. Subclasses should overwrite this method.
     * 
     */
    public Object getNullValue() {
        return null;
    }
    
    /**
     * INTERNAL
     * Allows for subclasses to convert the the attribute or property value. 
     */
    public abstract Object getObjectValue(Object fieldValue, Session session);
    
    /**
     * INTERNAL:
     * Return the weight of the mapping, used to sort mappings to ensure that
     * DirectToField Mappings get merged first
     */
    @Override
    public Integer getWeight() {
        return this.weight;
    }

    /**
     * Indicates if the mapping has a converter set on it.
     * 
     * @return true if there is a converter set on the mapping,
     * false otherwise.
     */
    public boolean hasConverter() {
        return false;
    }
    
    /**
     * INTERNAL:
     * Iterate on the appropriate attribute.
     */
    @Override
    public void iterate(DescriptorIterator iterator) {
        // PERF: Only iterate when required.
        if (iterator.shouldIterateOnPrimitives()) {
            iterator.iteratePrimitiveForMapping(getAttributeValueFromObject(iterator.getVisitedParent()), this);
        }
    }
    
    /**
     * PUBLIC:
     * Some databases do not properly support all of the base data types. For these databases,
     * the base data type must be explicitly specified in the mapping to tell EclipseLink to force
     * the instance variable value to that data type
     * 
     * @see org.eclipse.persistence.mappings.foundation.AbstractAttributeDirectMapping.setAttributeClassification(Class)
     */
    @Deprecated
    public abstract void setAttributeClassification(Class attributeClassification);
     
    
    /**
     * INTERNAL:
     * Return the class name of the attribute type (if there is one).
     * This is only used by the MW.
     * 
     * @see org.eclipse.persistence.mappings.foundation.AbstractAttributeDirectMapping.setAttributeClassificationName(String)
     */
    @Deprecated
    public void setAttributeClassificationName(String attributeClassificationName) {
        // Does nothing
    }
    
    /**
     * PUBLIC:
     * Set the converter on the mapping.
     * A converter can be used to convert between the object's value and 
     * database value of the attribute.
     */
    public void setConverter(Converter converter) {
        // Does nothing at this level.
    }
    
    /**
     * ADVANCED:
     * Set the field in the mapping.
     * This can be used for advanced field types, such as XML nodes, or to set the field type.
     */
    public void setField(DatabaseField theField) {
        field = theField;
    }

    /**
     * INTERNAL:
     */
    @Override
    public String toString() {
        return getClass().getName() + "[" + getAttributeName() + "-->" + getField() + "]";
    }
    
    /**
     * INTERNAL:
     * Return if this mapping requires its attribute value to be cloned.
     */
    @Override
    public boolean isCloningRequired() {
        return isMutable() || getDescriptor().getCopyPolicy().buildsNewInstance();
    }
    
    /**
     * INTERNAL:
     * Allow for subclasses to perform validation.
     */
    @Override
    public void validateBeforeInitialization(AbstractSession session) throws DescriptorException {
        if ((getFieldName() == null) || (getFieldName().length() == 0)) {
            session.getIntegrityChecker().handleError(DescriptorException.noFieldNameForMapping(this));
        }
    }

    /**
     * INTERNAL:
     * Extract value from the row and set the attribute to this value in the
     * working copy clone.
     * In order to bypass the shared cache when in transaction a UnitOfWork must
     * be able to populate working copies directly from the row.
     */
    @Override
    public void buildCloneFromRow(AbstractRecord databaseRow, JoinedAttributeManager joinManager, Object clone, CacheKey sharedCacheKey, ObjectBuildingQuery sourceQuery, UnitOfWorkImpl unitOfWork, AbstractSession executionSession) {
        // Even though the correct value may exist on the original, we can't
        // make that assumption.  It is easy to just build it again from the
        // row even if copy policy already copied it.
        // That optimization is lost.
        Object attributeValue = valueFromRow(databaseRow, joinManager, sourceQuery, sharedCacheKey, executionSession, true, null);

        setAttributeValueInObject(clone, attributeValue);
    }

    /**
     * INTERNAL:
     * Builds a shallow original object.  Only direct attributes and primary
     * keys are populated.  In this way the minimum original required for
     * instantiating a working copy clone can be built without placing it in
     * the shared cache (no concern over cycles).
     * @parameter original later the input to buildCloneFromRow
     */
    @Override
    public void buildShallowOriginalFromRow(AbstractRecord databaseRow, Object original, JoinedAttributeManager joinManager, ObjectBuildingQuery query, AbstractSession executionSession) {
        readFromRowIntoObject(databaseRow, null, original, null, query, executionSession, true);
    }

    protected abstract void writeValueIntoRow(AbstractRecord row, DatabaseField field, Object value);

    /**
     * INTERNAL:
     * Get a value from the object and set that in the respective field of the row.
     */
    @Override
    public void writeFromObjectIntoRow(Object object, AbstractRecord row, AbstractSession session, WriteType writeType) {
        if (isReadOnly() || 
            (writeType.equals(WriteType.INSERT) && ! isInsertable()) ||
            (writeType.equals(WriteType.UPDATE) && ! isUpdatable())) {
            return;
        }

        Object attributeValue = getAttributeValueFromObject(object);
        Object fieldValue = getFieldValue(attributeValue, session);
        
        // EL Bug 319759 - if a field is null, then the update call cache should not be used
        if (fieldValue == null) {
            row.setNullValueInFields(true);
        }

        writeValueIntoRow(row, getField(), fieldValue);
    }
    
    /**
     * INTERNAL:
     * Write the attribute value from the object to the row for update.
     */
    @Override
    public void writeFromObjectIntoRowForUpdate(WriteObjectQuery query, AbstractRecord databaseRow) {
        if (query.getSession().isUnitOfWork()) {
            if (compareObjects(query.getBackupClone(), query.getObject(), query.getSession())) {
                return;
            }
        }

        super.writeFromObjectIntoRowForUpdate(query, databaseRow);
    }
    
    /**
     * INTERNAL:
     * Get a value from the object and set that in the respective field of the row.
     * Validation preventing primary key updates is implemented here.
     */
    @Override
    public void writeFromObjectIntoRowWithChangeRecord(ChangeRecord changeRecord, AbstractRecord row, AbstractSession session, WriteType writeType) {
        if (isReadOnly() || 
           (writeType.equals(WriteType.INSERT) && ! isInsertable()) ||
           (writeType.equals(WriteType.UPDATE) && ! isUpdatable())) {
           return;
        }

        if (isPrimaryKeyMapping() && !changeRecord.getOwner().isNew()) {
           throw ValidationException.primaryKeyUpdateDisallowed(changeRecord.getOwner().getClassName(), changeRecord.getAttribute());
        }
        
        Object attributeValue = ((DirectToFieldChangeRecord)changeRecord).getNewValue();
        Object fieldValue = getFieldValue(attributeValue, session);
        
        // EL Bug 319759 - if a field is null, then the update call cache should not be used
        if (fieldValue == null) {
            row.setNullValueInFields(true);
        }

        row.add(getField(), fieldValue);
    }
    
    /**
     * INTERNAL:
     * Write fields needed for insert into the template for with null values.
     */
    @Override
    public void writeInsertFieldsIntoRow(AbstractRecord databaseRow, AbstractSession session) {
        if (isInsertable() && ! isReadOnly()) {
            databaseRow.add(getField(), null);
        }
    }
    
    /**
     * INTERNAL:
     * Write fields needed for update into the template for with null values.
     * By default inserted fields are used.
     */
    @Override
    public void writeUpdateFieldsIntoRow(AbstractRecord databaseRow, AbstractSession session) {
        if (isUpdatable() && ! isReadOnly()) {
            databaseRow.add(getField(), null);    
        }
    }
}
