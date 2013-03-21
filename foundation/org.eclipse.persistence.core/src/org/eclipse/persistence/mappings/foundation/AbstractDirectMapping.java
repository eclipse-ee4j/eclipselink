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
 *     11/13/2009-2.0  mobrien - 294765: MapKey keyType DirectToField processing 
 *       should return attributeClassification class in getMapKeyTargetType when 
 *       accessor.attributeField is null in the absence of a MapKey annotation
 ******************************************************************************/  
package org.eclipse.persistence.mappings.foundation;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.*;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.internal.databaseaccess.DatabaseAccessor;
import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.internal.descriptors.*;
import org.eclipse.persistence.internal.expressions.SQLSelectStatement;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.queries.JoinedAttributeManager;
import org.eclipse.persistence.internal.queries.MappedKeyMapContainerPolicy;
import org.eclipse.persistence.internal.sessions.*;
import org.eclipse.persistence.mappings.converters.*;
import org.eclipse.persistence.mappings.querykeys.DirectQueryKey;
import org.eclipse.persistence.mappings.querykeys.QueryKey;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.sessions.remote.*;
import org.eclipse.persistence.sessions.CopyGroup;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;

/**
 * <b>Purpose</b>: Maps an attribute to the corresponding database field type.
 * The list of field types that are supported by EclipseLink's direct to field mapping
 * is dependent on the relational database being used.
 * A converter can be used to convert between the object and data type if they do not match.
 *
 * @see Converter
 * @see ObjectTypeConverter
 * @see TypeConversionConverter
 * @see SerializedObjectConverter
 * @see ClassInstanceConverter
 *
 * @author Sati
 * @since TopLink/Java 1.0
 */
public abstract class AbstractDirectMapping extends AbstractColumnMapping implements MapKeyMapping {

    /** To specify the conversion type */
    protected transient Class attributeClassification;
    protected String attributeClassificationName;
    
    /** PERF: Also store object class of attribute in case of primitive. */
    protected transient Class attributeObjectClassification;

    /** Support specification of the value to use for null. */
    protected transient Object nullValue;
    
    protected DatabaseTable keyTableForMapKey = null;
    
    protected String fieldClassificationClassName = null;
    
    /** PERF: Avoid default null value conversion check if not default null value set in conversion manager. */
    protected boolean bypassDefaultNullValueCheck;

    /**
     * PERF: Indicates if this mapping's attribute is a simple atomic value and cannot be modified, only replaced.
     * This is a tri-state to allow user to set to true or false, as default is false but
     * some data-types such as Calendar or byte[] or converter types may be desired to be used as mutable.
     */
    protected Boolean isMutable;

    /**
     * Default constructor.
     */
    public AbstractDirectMapping() {
        super();
    }

    /**
     * INTERNAL:
     * Used when initializing queries for mappings that use a Map.
     * Called when the selection query is being initialized to add the fields for the map key to the query.
     */
    public void addAdditionalFieldsToQuery(ReadQuery selectionQuery, Expression baseExpression){
        if (selectionQuery.isObjectLevelReadQuery()){
            ((ObjectLevelReadQuery)selectionQuery).addAdditionalField(baseExpression.getField(getField()));        
        } else if (selectionQuery.isDataReadQuery()){
            ((SQLSelectStatement)((DataReadQuery)selectionQuery).getSQLStatement()).addField(baseExpression.getField(getField()));
        }
    }

    /**
     * INTERNAL:
     * Used when initializing queries for mappings that use a Map
     * Called when the insert query is being initialized to ensure the fields for the map key are in the insert query.
     */
    public void addFieldsForMapKey(AbstractRecord joinRow) {
        if (!isReadOnly()){
            if (isUpdatable()){
                joinRow.put(getField(), null);
            }
        }
    }

    /**
     * INTERNAL:
     * For mappings used as MapKeys in MappedKeyContainerPolicy.  Add the target of this mapping to the deleted 
     * objects list if necessary
     *
     * This method is used for removal of private owned relationships
     * DirectMappings are dealt with in their parent delete, so this is a no-op.
     */
    public void addKeyToDeletedObjectsList(Object object, Map deletedObjects) {
    }

    /**
     * PUBLIC:
     * Return true if the attribute for this mapping is a simple atomic value that cannot be modified,
     * only replaced.
     * This is false by default unless a mutable converter is used such as the SerializedObjectConverter.
     * This can be set to false in this case, or if a Calendar or byte[] is desired to be used as a mutable value it can be set to true.
     */
    public boolean isMutable() {
        if (isMutable == null) {
            return false;
        }
        return isMutable.booleanValue();
    }

    /**
     * PUBLIC:
     * Return true if the attribute for this mapping is a simple atomic value that cannot be modified,
     * only replaced.
     * This is false by default unless a mutable converter is used such as the SerializedObjectConverter.
     * This can be set to false in this case, or if a Calendar or byte[] is desired to be used as a mutable value it can be set to true.
     */
    public void setIsMutable(boolean isMutable) {
        if (isMutable == true) {
            this.isMutable = Boolean.TRUE;
        } else {
            this.isMutable = Boolean.FALSE;
        }
    }
    
    /**
     * INTERNAL:
     * Clone the attribute from the clone and assign it to the backup.
     */
    @Override
    public void buildBackupClone(Object clone, Object backup, UnitOfWorkImpl unitOfWork) {
        buildClone(clone, null, backup, null, unitOfWork);
    }

    /**
     * INTERNAL:
     * Directly build a change record without comparison
     */
    @Override
    public ChangeRecord buildChangeRecord(Object clone, ObjectChangeSet owner, AbstractSession session) {
        return internalBuildChangeRecord(getAttributeValueFromObject(clone), null, owner);
    }
    
    /**
     * INTERNAL:
     * Clone the attribute from the original and assign it to the clone.
     */
    @Override
    public void buildClone(Object original, CacheKey cacheKey, Object clone, Integer refreshCascade, AbstractSession cloningSession) {
        buildCloneValue(original, clone, cloningSession);
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
     * Clone the attribute from the original and assign it to the clone.
     * If mutability is configured to be true, clone the attribute if it is an instance of
     * byte[], java.util.Calendar or java.util.Date (or their subclasses).
     */
    public void buildCloneValue(Object original, Object clone, AbstractSession session) {
        Object attributeValue = getAttributeValueFromObject(original);
        attributeValue = buildCloneValue(attributeValue, session);
        setAttributeValueInObject(clone, attributeValue);
    }
    
    /**
     * INTERNAL:
     * Clone the actual value represented by this mapping.  Do set the cloned value into the object.
     */
    protected Object buildCloneValue(Object attributeValue, AbstractSession session) {
        Object newAttributeValue = attributeValue;
        if (isMutable() && attributeValue != null) {
            // EL Bug 252047 - Mutable attributes are not cloned when isMutable is enabled on a Direct Mapping
            if (attributeValue instanceof byte[]) {
                int length = ((byte[]) attributeValue).length;
                byte[] arrayCopy = new byte[length];
                System.arraycopy(attributeValue, 0, arrayCopy, 0, length);
                newAttributeValue = arrayCopy;
            } else if (attributeValue instanceof Byte[]) {
                int length = ((Byte[]) attributeValue).length;
                Byte[] arrayCopy = new Byte[length];
                System.arraycopy(attributeValue, 0, arrayCopy, 0, length);
                newAttributeValue = arrayCopy;
            } else if (attributeValue instanceof char[]) {
                int length = ((char[]) attributeValue).length;
                char[] arrayCopy = new char[length];
                System.arraycopy(attributeValue, 0, arrayCopy, 0, length);
                newAttributeValue = arrayCopy;
            } else if (attributeValue instanceof Character[]) {
                int length = ((Character[]) attributeValue).length;
                Character[] arrayCopy = new Character[length];
                System.arraycopy(attributeValue, 0, arrayCopy, 0, length);
                newAttributeValue = arrayCopy;
            } else if (attributeValue instanceof Date) {
                newAttributeValue = ((Date)attributeValue).clone();
            } else if (attributeValue instanceof Calendar) {
                newAttributeValue = ((Calendar)attributeValue).clone();
            } else {
                newAttributeValue = getObjectValue(getFieldValue(attributeValue, session), session);
            }
        }
        return newAttributeValue;
    }
    
    /**
     * INTERNAL:
     * Copy of the attribute of the object.
     * This is NOT used for unit of work but for templatizing an object.
     */
    @Override
    public void buildCopy(Object copy, Object original, CopyGroup group) {
        buildCloneValue(original, copy, group.getSession());
    }

    /**
     * Build a clone of the given element in a unitOfWork.
     */
    public Object buildElementClone(Object attributeValue, Object parent, CacheKey cacheKey, Integer refreshCascade, AbstractSession cloningSession, boolean isExisting, boolean isFromSharedCache){
        return buildCloneValue(attributeValue, cloningSession);
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
     * Certain key mappings favor different types of selection query.  Return the appropriate
     * type of selectionQuery.
     */
    public ReadQuery buildSelectionQueryForDirectCollectionKeyMapping(ContainerPolicy containerPolicy){
        DataReadQuery query = new DataReadQuery();
        query.setSQLStatement(new SQLSelectStatement());
        query.setContainerPolicy(containerPolicy);
        return query;
    }
    
    /**
     * INTERNAL:
     * Cascade discover and persist new objects during commit to the map key.
     */
    public void cascadeDiscoverAndPersistUnregisteredNewObjects(Object object, Map newObjects, Map unregisteredExistingObjects, Map visitedObjects, UnitOfWorkImpl uow,  boolean getAttributeValueFromObject, Set cascadeErrors){
        //objects referenced by this mapping are not registered as they have
        // no identity, this is a no-op.
    }
    
    /**
     * INTERNAL:
     * Cascade perform delete through mappings that require the cascade.
     */
    public void cascadePerformRemoveIfRequired(Object object, UnitOfWorkImpl uow, Map visitedObjects, boolean getAttributeValueFromObject) {
        //objects referenced by this mapping are not registered as they have
        // no identity, this is a no-op.
    }
    
    /**
     * INTERNAL:
     * Cascade perform delete through mappings that require the cascade.
     */
    public void cascadePerformRemoveIfRequired(Object object, UnitOfWorkImpl uow, Map visitedObjects) {
        //objects referenced by this mapping are not registered as they have
        // no identity, this is a no-op.
    }
    
    /**
     * INTERNAL:
     * Cascade registerNew for Create through mappings that require the cascade.
     */
    public void cascadeRegisterNewIfRequired(Object object, UnitOfWorkImpl uow, Map visitedObjects, boolean getAttributeValueFromObject) {
        //objects referenced by this mapping are not registered as they have
        // no identity, this is a no-op.
    }

    /**
     * INTERNAL:
     * Cascade registerNew for Create through mappings that require the cascade.
     */
    public void cascadeRegisterNewIfRequired(Object object, UnitOfWorkImpl uow, Map visitedObjects) {
        //objects referenced by this mapping are not registered as they have
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
     * INTERNAL:
     * Compare the clone and backup clone values and return a change record if the value changed.
     */
    @Override
    public ChangeRecord compareForChange(Object clone, Object backUp, ObjectChangeSet owner, AbstractSession session) {
        // same code as write from object into row for update
        if (owner.isNew()) {
            return internalBuildChangeRecord(getAttributeValueFromObject(clone), null, owner);
        } else if (!compareObjects(backUp, clone, session)) {
            Object oldValue = null;
            if (backUp != null && clone != backUp) {
                oldValue = getAttributeValueFromObject(backUp);
            }
            return internalBuildChangeRecord(getAttributeValueFromObject(clone), oldValue, owner);
        }
        return null;
    }
    
    /**
     * INTERNAL:
     * For mappings used as MapKeys in MappedKeyContainerPolicy, Delete the passed object if necessary.
     * 
     * This method is used for removal of private owned relationships
     * DirectMappings are dealt with in their parent delete, so this is a no-op.
     */
    public void deleteMapKey(Object objectDeleted, AbstractSession session){
    }

    /**
     * INTERNAL:
     * Compare the attributes belonging to this mapping for the objects.
     */
    @Override
    public boolean compareObjects(Object firstObject, Object secondObject, AbstractSession session) {
        Object firstValue = getAttributeValueFromObject(firstObject);
        Object secondValue = getAttributeValueFromObject(secondObject);
        return compareObjectValues(firstValue, secondValue, session);        
    }
    
    /**
     * INTERNAL:
     * Compare the attribute values.
     */
    protected boolean compareObjectValues(Object firstValue, Object secondValue, AbstractSession session) {
        // PERF: Check identity before conversion.
        if (firstValue == secondValue) {
            return true;
        }

        if ((firstValue != null) && (secondValue != null)) {
            // PERF: Check equals first, as normally no change.
            // Also for serialization objects bytes may not be consistent, but equals may work (HashMap).
            if (firstValue.equals(secondValue)) {
                return true;
            }
        }

        // CR2114 - following two lines modified; getFieldValue() needs class as an argument
        firstValue = getFieldValue(firstValue, session);
        secondValue = getFieldValue(secondValue, session);
        // PERF:  Check identity/nulls before special type comparison.
        if (firstValue == secondValue) {
            return true;
        }

        if ((firstValue == null) || (secondValue == null)) {
            return false;
        }

        // PERF: Check equals first, as normally no change.
        if (firstValue.equals(secondValue)) {
            return true;
        }
        
        return Helper.comparePotentialArrays(firstValue, secondValue);
    }
    
    /**
     * INTERNAL:
     * Convert all the class-name-based settings in this mapping to actual class-based settings
     * This method is implemented by subclasses as necessary.
     */
    @Override
    public void convertClassNamesToClasses(ClassLoader classLoader){
        super.convertClassNamesToClasses(classLoader);
        
        if (getAttributeClassificationName() != null) {
            Class attributeClass = null;
            try{
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    try {
                        attributeClass = (Class)AccessController.doPrivileged(new PrivilegedClassForName(getAttributeClassificationName(), true, classLoader));
                    } catch (PrivilegedActionException exception) {
                        throw ValidationException.classNotFoundWhileConvertingClassNames(getAttributeClassificationName(), exception.getException());
                    }
                } else {
                    attributeClass = org.eclipse.persistence.internal.security.PrivilegedAccessHelper.getClassForName(getAttributeClassificationName(), true, classLoader);
                }
            } catch (ClassNotFoundException exc){
                throw ValidationException.classNotFoundWhileConvertingClassNames(getAttributeClassificationName(), exc);
            }
            setAttributeClassification(attributeClass);
        }

        if (fieldClassificationClassName != null){
            Class fieldClassification = null;
            try {
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    try {
                        fieldClassification = (Class) AccessController.doPrivileged(new PrivilegedClassForName(fieldClassificationClassName, true, classLoader));
                    } catch (PrivilegedActionException exception) {
                        throw ValidationException.classNotFoundWhileConvertingClassNames(fieldClassificationClassName, exception.getException());
                    }

                } else {
                    fieldClassification = org.eclipse.persistence.internal.security.PrivilegedAccessHelper.getClassForName(fieldClassificationClassName, true, classLoader);
                }
            } catch (ClassNotFoundException exc) {
                throw ValidationException.classNotFoundWhileConvertingClassNames(fieldClassificationClassName, exc);
            } catch (Exception e) {
                // Catches IllegalAccessException and InstantiationException
                throw ValidationException.classNotFoundWhileConvertingClassNames(fieldClassificationClassName, e);
            }
            
            setFieldClassification(fieldClassification);
        }
    }
    
    /**
     * INTERNAL:
     * Creates the Array of simple types used to recreate this map.  
     */
    public Object createSerializableMapKeyInfo(Object key, AbstractSession session){
        return key; // DirectToFields are already simple types.
    }

    /**
     * INTERNAL:
     * Create an instance of the Key object from the key information extracted from the map.  
     * This may return the value directly in case of a simple key or will be used as the FK to load a related entity.
     */
    public List<Object> createMapComponentsFromSerializableKeyInfo(Object[] keyInfo, AbstractSession session){
        return Arrays.asList(keyInfo); // DirectToFields are already simple types.
    }

    /**
     * INTERNAL:
     * Create an instance of the Key object from the key information extracted from the map.  
     * This key object may be a shallow stub of the actual object if the key is an Entity type.
     */
    public Object createStubbedMapComponentFromSerializableKeyInfo(Object keyInfo, AbstractSession session){
        return keyInfo;
    }

    /**
     * INTERNAL
     * Called when a DatabaseMapping is used to map the key in a collection.  Returns the key.
     */
    public Object createMapComponentFromRow(AbstractRecord dbRow, ObjectBuildingQuery query, CacheKey parentCacheKey, AbstractSession session, boolean isTargetProtected) {
        Object key = dbRow.get(getField());
        key = getObjectValue(key, session);
        return key;
    }

    /**
     * INTERNAL
     * Called when a DatabaseMapping is used to map the key in a collection and a join query is executed.  Returns the key.
     */
    public Object createMapComponentFromJoinedRow(AbstractRecord dbRow, JoinedAttributeManager joinManger, ObjectBuildingQuery query, CacheKey parentCacheKey, AbstractSession session, boolean isTargetProtected) {
        return createMapComponentFromRow(dbRow, query, parentCacheKey, session, isTargetProtected);
    }
    
    /**
     * INTERNAL:
     * Create a query key that links to the map key.
     */
    public QueryKey createQueryKeyForMapKey() {
        DirectQueryKey queryKey = new DirectQueryKey();
        queryKey.setField(getField());
        return queryKey;
    }
    
    /**
     * INTERNAL:
     * Extract the fields for the Map key from the object to use in a query.
     */
    public Map extractIdentityFieldsForQuery(Object object, AbstractSession session){
        Map fields = new HashMap();
        Object key = object;
        if (getConverter() != null){
             key = getConverter().convertObjectValueToDataValue(key , session);
        }
        fields.put(getField(), key);
        return fields;
    }

    /**
     * INTERNAL:
     * Return any tables that will be required when this mapping is used as part of a join query.
     */
    public List<DatabaseTable> getAdditionalTablesForJoinQuery() {
        List tables = new ArrayList(1);
        tables.add(getField().getTable());
        return tables;
    }
    
    /**
     * PUBLIC:
     * Some databases do not properly support all of the base data types. For these databases,
     * the base data type must be explicitly specified in the mapping to tell EclipseLink to force
     * the instance variable value to that data type.
     */
    public Class getAttributeClassification() {
        return attributeClassification;
    }

    /**
     * INTERNAL:
     * Return the class name of the attribute type.
     * This is only used by the MW.
     */
    public String getAttributeClassificationName() {
        if ((attributeClassificationName == null) && (attributeClassification != null)) {
            attributeClassificationName = attributeClassification.getName();
        }
        return attributeClassificationName;
    }

    /**
     * INTERNAL:
     * Allows for subclasses to convert the attribute value.
     */
    public Object getObjectValue(Object fieldValue, Session session) {
        // PERF: Direct variable access.
        Object attributeValue = fieldValue;
        if ((fieldValue == null) && (this.nullValue != null)) {// Translate default null value
            return this.nullValue;
        }

        // Allow for user defined conversion to the object value.
        if (this.converter != null) {
            attributeValue = this.converter.convertDataValueToObjectValue(attributeValue, session);
        } else {
            // PERF: Avoid conversion check when not required.
            if ((attributeValue == null) || (attributeValue.getClass() != this.attributeObjectClassification)) {
                if ((attributeValue != null) || !this.bypassDefaultNullValueCheck) {                    
                    try {
                        attributeValue = session.getDatasourcePlatform().convertObject(attributeValue, this.attributeClassification);
                    } catch (ConversionException e) {
                        throw ConversionException.couldNotBeConverted(this, getDescriptor(), e);
                    }
                }
            }
        }
        if (attributeValue == null) {// Translate default null value, conversion may have produced null.
            attributeValue = this.nullValue;
        }

        return attributeValue;
    }

    /**
     * INTERNAL:
     * Same as getObjectValue method, but without checking fieldValue's class.
     * Used in case the fieldValue class is already known to be the same as attributeClassification.
     */
    public Object getObjectValueWithoutClassCheck(Object fieldValue, Session session) {
        if ((fieldValue == null) && (this.nullValue != null)) {// Translate default null value
            return this.nullValue;
        }
        // PERF: Direct variable access.
        Object attributeValue = fieldValue;

        // Allow for user defined conversion to the object value.
        if (this.converter != null) {
            attributeValue = this.converter.convertDataValueToObjectValue(attributeValue, session);
        } else {
            // PERF: Avoid conversion check when not required.
            if (attributeValue == null) {
                if (!this.bypassDefaultNullValueCheck) {                    
                    try {
                        attributeValue = session.getDatasourcePlatform().convertObject(null, this.attributeClassification);
                    } catch (ConversionException e) {
                        throw ConversionException.couldNotBeConverted(this, getDescriptor(), e);
                    }
                }
            }
        }
        if (attributeValue == null) {// Translate default null value, conversion may have produced null.
            attributeValue = this.nullValue;
        }

        return attributeValue;
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
     * INTERNAL:
     * Return the classification for the field contained in the mapping.
     * This is used to convert the row value to a consistent Java value.
     */
    public Class getFieldClassification(DatabaseField fieldToClassify) {
        // PERF: This method is a major performance code point,
        // so has been micro optimized and uses direct variable access.
        if (fieldToClassify.type != null) {
            return fieldToClassify.type;
        } else {
            if (hasConverter()) {
                return null;
            } else {
                // PERF: Ensure the object type is used for primitives.
                return Helper.getObjectClass(this.attributeClassification);
            }
        }
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
     * INTERNAL:
     * Set the name of the class that will be used for setFieldClassification and deploy time
     * Used internally by JPA deployment.
     * 
     * @see setFieldClassification(Class fieldType)
     * @param className
     */
    public void setFieldClassificationClassName(String className){
        this.fieldClassificationClassName = className;
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
     * Convert the attribute value to a field value.
     * Process any converter if defined, and check for null values.
     */
    public Object getFieldValue(Object attributeValue, AbstractSession session) {
        // PERF: This method is a major performance code point,
        // so has been micro optimized and uses direct variable access.
        Object fieldValue = attributeValue;
        if ((this.nullValue != null) && (this.nullValue.equals(fieldValue))) {
            return null;
        }

        // Allow for user defined conversion to the object value.
        if (this.converter != null) {
            fieldValue = this.converter.convertObjectValueToDataValue(fieldValue, session);
        }
        Class fieldClassification = this.field.type;
        if (fieldClassification == null) {
            fieldClassification = getFieldClassification(this.field);
        }
        // PERF: Avoid conversion if not required.
        // EclipseLink bug 240407 - nulls not translated when writing to database
        if ((fieldValue == null) || (fieldClassification != fieldValue.getClass())) {
            if ((fieldValue != null) || !this.bypassDefaultNullValueCheck) {
                try {
                    fieldValue = session.getPlatform(this.descriptor.getJavaClass()).convertObject(fieldValue, fieldClassification);
                } catch (ConversionException exception) {
                    throw ConversionException.couldNotBeConverted(this, this.descriptor, exception);
                }
            }
        }
        return fieldValue;
    }

    /**
     * INTERNAL:
     * Return a Map of any foreign keys defined within the the MapKey.
     */
    public Map<DatabaseField, DatabaseField> getForeignKeyFieldsForMapKey(){
        return null;
    }
    
    /**
     * INTERNAL:
     * Return the fields that make up the identity of the mapped object.  For mappings with
     * a primary key, it will be the set of fields in the primary key.  For mappings without
     * a primary key it will likely be all the fields.
     */
    public List<DatabaseField> getIdentityFieldsForMapKey(){
        return getAllFieldsForMapKey();
    }
    
    /**
     * INTERNAL:
     * Get all the fields for the map key.
     */
    public List<DatabaseField> getAllFieldsForMapKey(){
        Vector<DatabaseField> fields = new Vector<DatabaseField>(1);
        fields.add(getField());
        return fields;
    }
    
    /**
     * INTERNAL:
     * Return the query that is used when this mapping is part of a joined relationship
     * This method is used when this mapping is used to map the key in a Map.
     */
    public ObjectLevelReadQuery getNestedJoinQuery(JoinedAttributeManager joinManager, ObjectLevelReadQuery query, AbstractSession session){
        return null;
    }
    
    /**
     * PUBLIC:
     * Allow for the value used for null to be specified.
     * This can be used to convert database null values to application specific values, when null values
     * are not allowed by the application (such as in primitives).
     * Note: the default value for NULL is used on reads, writes, and query SQL generation
     */
    public Object getNullValue() {
        return nullValue;
    }
    
    /**
     * INTERNAL:
     * Return the selection criteria necessary to select the target object when this mapping
     * is a map key.
     * DirectMappings do not need any additional selection criteria when they are map keys.
     */
    public Expression getAdditionalSelectionCriteriaForMapKey(){
        return null;
    }
    
    /**
     * INTERNAL:
     * If required, get the targetVersion of the source object from the merge manager.
     * Used with MapKeyContainerPolicy to abstract getting the target version of a source key.
     */
    public Object getTargetVersionOfSourceObject(Object object, Object parent, MergeManager mergeManager, AbstractSession targetSession){
       return object;
    }
    
    /**
     * INTERNAL:
     * Return the class this key mapping maps or the descriptor for it
     * @return
     */
    public Class getMapKeyTargetType() {
        Class aClass = getAttributeAccessor().getAttributeClass();
        // 294765: check the attributeClassification when the MapKey annotation is not specified
        if (null == aClass) {
            aClass = getAttributeClassification();
        }
        if (null == aClass) {
            aClass = getField().getType();
        }
        return aClass;
    }
    
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
     * INTERNAL:
     * Once descriptors are serialized to the remote session. All its mappings and reference descriptors are traversed. Usually
     * mappings are initialized and serialized reference descriptors are replaced with local descriptors if they already exist on the
     * remote session.
     */
    @Override
    public void remoteInitialization(DistributedSession session) {
        if (!isRemotelyInitialized()) {
            super.remoteInitialization(session);
            if (this.attributeClassification == null) {
                this.attributeClassification = getAttributeAccessor().getAttributeClass();
            }
            this.attributeObjectClassification = Helper.getObjectClass(this.attributeClassification);
        }
    }
    
    /**
     * INTERNAL:
     * Initialize the attribute classification.
     */
    @Override
    public void preInitialize(AbstractSession session) throws DescriptorException {
        super.preInitialize(session);
        // Allow the attribute class to be set by the user.
        if (this.attributeClassification == null) {
            this.attributeClassification = getAttributeAccessor().getAttributeClass();
        }
        this.attributeObjectClassification = Helper.getObjectClass(this.attributeClassification);
        
        // Initialize isMutable if not specified, default is false (assumes not mutable).
        if (this.isMutable == null) {
            if (hasConverter()) {
                setIsMutable(getConverter().isMutable());
            } else {
                setIsMutable(false);
            }
            // If mapping a temporal type, use the project mutable default.
            if ((getAttributeClassification() != null)
                    && (ClassConstants.UTILDATE.isAssignableFrom(getAttributeClassification())
                            || ClassConstants.CALENDAR.isAssignableFrom(getAttributeClassification()))) {
                setIsMutable(session.getProject().getDefaultTemporalMutable());
            }
        }
        
        Map nullValues = session.getPlatform(this.descriptor.getJavaClass()).getConversionManager().getDefaultNullValues();
        bypassDefaultNullValueCheck = (!this.attributeClassification.isPrimitive()) &&
                ((nullValues == null) || (!nullValues.containsKey(this.attributeClassification)));
    }
    
    /**
     * INTERNAL:
     * The mapping is initialized with the given session.
     * This mapping is fully initialized after this.
     */
    @Override
    public void initialize(AbstractSession session) throws DescriptorException {
        super.initialize(session);
        
        if (getField() == null) {
            session.getIntegrityChecker().handleError(DescriptorException.fieldNameNotSetInMapping(this));
        }
        
        // Before potentially swapping out the field with an already built one, 
        // set the JPA insertable and updatable flags based on the settings from 
        // this mappings field. This must be done now. The reason for this code 
        // is to cover the case where multiple mappings map to the same field. 
        // One of those mappings must be write only, therefore, depending on the 
        // initialization order we do not want to set the writable mapping as 
        // non insertable and non updatable.
        isInsertable = getField().isInsertable();
        isUpdatable = getField().isUpdatable();

        if (keyTableForMapKey == null){
            setField(getDescriptor().buildField(getField()));
        } else {
            setField(getDescriptor().buildField(getField(), keyTableForMapKey));
        }
        setFields(collectFields());

        if (hasConverter()) {
            getConverter().initialize(this, session);
        }
        
        // Must unwrap Struct types on WLS.
        if (getField().getSqlType() == java.sql.Types.STRUCT) {
            getDescriptor().setIsNativeConnectionRequired(true);
        }
    }
    
    /**
     * INTERNAL:
     * Build a change record.
     */
    public ChangeRecord internalBuildChangeRecord(Object newValue, Object oldValue, ObjectChangeSet owner) {
        DirectToFieldChangeRecord changeRecord = new DirectToFieldChangeRecord(owner);
        changeRecord.setAttribute(getAttributeName());
        changeRecord.setMapping(this);
        changeRecord.setNewValue(newValue);
        changeRecord.setOldValue(oldValue);
        return changeRecord;
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean isDirectToFieldMapping() {
        return true;
    }

    /**
     * INTERNAL:
     * Called when iterating through descriptors to handle iteration on this mapping when it is used as a MapKey.
     */
    public void iterateOnMapKey(DescriptorIterator iterator, Object element){
        if (iterator.shouldIterateOnPrimitives()) {
            iterator.iteratePrimitiveForMapping(element, this);
        }
    }
    
    /**
     * INTERNAL:
     * Merge changes from the source to the target object.
     */
    @Override
    public void mergeChangesIntoObject(Object target, ChangeRecord changeRecord, Object source, MergeManager mergeManager, AbstractSession targetSession) {
        setAttributeValueInObject(target, buildCloneValue(((DirectToFieldChangeRecord)changeRecord).getNewValue(), mergeManager.getSession()));
    }

    /**
     * INTERNAL:
     * Merge changes from the source to the target object. This merge is only called when a changeSet for the target
     * does not exist or the target is uninitialized
     */
    @Override
    public void mergeIntoObject(Object target, boolean isTargetUnInitialized, Object source, MergeManager mergeManager, AbstractSession targetSession) {
        // If merge into the unit of work, must only merge and raise the event is the value changed.
        if ((mergeManager.shouldMergeCloneIntoWorkingCopy() || mergeManager.shouldMergeCloneWithReferencesIntoWorkingCopy())  && !mergeManager.isForRefresh()
                && this.descriptor.getObjectChangePolicy().isObjectChangeTrackingPolicy()) {
            // if it didn't change then there will be no event
            Object attributeValue = getAttributeValueFromObject(source);
            Object targetAttribute = getAttributeValueFromObject(target);
            if (!compareObjectValues(attributeValue, targetAttribute, mergeManager.getSession())) {
                setAttributeValueInObject(target, buildCloneValue(attributeValue, mergeManager.getSession()));
                //set the value first, if the owner is new ( or aggregate) the change set may be created directly
                //from the target.
                this.descriptor.getObjectChangePolicy().raiseInternalPropertyChangeEvent(target, getAttributeName(), targetAttribute, attributeValue);
            }
        } else {
            setAttributeValueInObject(target, buildCloneValue(getAttributeValueFromObject(source), mergeManager.getSession()));
        }
    }

    
    /**
     * INTERNAL:
     * Making any mapping changes necessary to use a the mapping as a map key prior to initializing the mapping.
     */
    public void preinitializeMapKey(DatabaseTable table) throws DescriptorException {
        this.keyTableForMapKey = table;
    }
    
    /**
     * INTERNAL:
     * Making any mapping changes necessary to use a the mapping as a map key after initializing the mapping.
     */
    public void postInitializeMapKey(MappedKeyMapContainerPolicy policy) {
        if (getField().getType() == null) {
            getField().setType(getFieldClassification(getField()));
        }
    }
    
    /**
     * INTERNAL:
     * Return whether this mapping requires extra queries to update the rows if it is
     * used as a key in a map.  This will typically be true if there are any parts to this mapping
     * that are not read-only.
     */
    public boolean requiresDataModificationEventsForMapKey(){
        return !isReadOnly() && isUpdatable();
    }
    
    /**
     * PUBLIC:
     * Some databases do not properly support all of the base data types. For these databases,
     * the base data type must be explicitly specified in the mapping to tell EclipseLink to force
     * the instance variable value to that data type
     */
    public void setAttributeClassification(Class attributeClassification) {
        this.attributeClassification = attributeClassification;
    }

    /**
     * INTERNAL:
     * Set the name of the class for MW usage.
     */
    public void setAttributeClassificationName(String attributeClassificationName) {
        this.attributeClassificationName = attributeClassificationName;
    }

    /**
     * PUBLIC:
     * Allow for the value used for null to be specified.
     * This can be used to convert database null values to application specific values, when null values
     * are not allowed by the application (such as in primitives).
     * Note: the default value for NULL is used on reads, writes, and query SQL generation
     */
    public void setNullValue(Object nullValue) {
        this.nullValue = nullValue;
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
     * Either create a new change record or update with the new value.  This is used
     * by attribute change tracking.
     */
    @Override
    public void updateChangeRecord(Object clone, Object newValue, Object oldValue, ObjectChangeSet objectChangeSet, UnitOfWorkImpl uow) {
        DirectToFieldChangeRecord changeRecord = (DirectToFieldChangeRecord)objectChangeSet.getChangesForAttributeNamed(this.getAttributeName());
        if (changeRecord == null) {
            objectChangeSet.addChange(internalBuildChangeRecord(newValue, oldValue, objectChangeSet));
        } else {
            changeRecord.setNewValue(newValue);
        }
    }

    /**
     * INTERNAL:
     * Return if this mapping supports change tracking.
     */
    @Override
    public boolean isChangeTrackingSupported(Project project) {
        return !isMutable();
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
     * Allow the key mapping to unwrap the object.
     */    
    public Object unwrapKey(Object key, AbstractSession session){
        return key;
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
     * Allow the key mapping to wrap the object.
     */
    public Object wrapKey(Object key, AbstractSession session){
        return key;
    }
    
    /**
     * INTERNAL:
     * Get the value from the object for this mapping.
     */
    @Override
    public Object valueFromObject(Object object, DatabaseField field, AbstractSession session) throws DescriptorException {
        return getFieldValue(getAttributeValueFromObject(object), session);
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

    /**
     * INTERNAL:
     * Return the mapping's attribute value from the row.
     * The execution session is passed for the case of building a UnitOfWork clone
     * directly from a row, the session set in the query will not know which platform to use
     * for converting the value.  Allows the correct session to be passed in.
     */
    @Override
    public Object valueFromRow(AbstractRecord row, JoinedAttributeManager joinManager, ObjectBuildingQuery query, CacheKey cacheKey, AbstractSession executionSession, boolean isTargetProtected, Boolean[] wasCacheUsed) {
        if (this.descriptor.getCachePolicy().isProtectedIsolation()) {
            if (this.isCacheable && isTargetProtected && cacheKey != null) {
                Object cached = cacheKey.getObject();
                if (cached != null) {
                    if (wasCacheUsed != null){
                        wasCacheUsed[0] = Boolean.TRUE;
                    }
                    Object attributeValue = getAttributeValueFromObject(cached);
                    return buildCloneValue(attributeValue, executionSession);
                }
            }
        }
        // PERF: Direct variable access.
        Object fieldValue = row.get(getField());
        Object attributeValue = getObjectValue(fieldValue, executionSession);

        return attributeValue;
    }

    /**
     * INTERNAL:
     * Returns the value for the mapping directly from the result-set.
     * PERF: Used for optimized object building.
     */
    @Override
    public Object valueFromResultSet(ResultSet resultSet, ObjectBuildingQuery query, AbstractSession session, DatabaseAccessor accessor, ResultSetMetaData metaData, int columnNumber, DatabasePlatform platform) throws SQLException {
        if (this.attributeObjectClassification == ClassConstants.STRING) {
            return getObjectValueWithoutClassCheck(resultSet.getString(columnNumber), session);
        } else if (this.attributeObjectClassification == ClassConstants.LONG) {
            return getObjectValueWithoutClassCheck(resultSet.getLong(columnNumber), session);
        } else if (this.attributeObjectClassification == ClassConstants.INTEGER) {
            return getObjectValueWithoutClassCheck(resultSet.getInt(columnNumber), session);
        }
        Object fieldValue = accessor.getObject(resultSet, getField(), metaData, columnNumber, platform, true, session);
        return getObjectValue(fieldValue, session);
    }

    protected abstract void writeValueIntoRow(AbstractRecord row, DatabaseField field, Object value);

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

        if (this.isPrimaryKeyMapping && !changeRecord.getOwner().isNew()) {
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
