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
 *     11/10/2011-2.4 Guy Pelletier 
 *       - 357474: Address primaryKey option from tenant discriminator column
 ******************************************************************************/  
package org.eclipse.persistence.mappings.foundation;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.internal.databaseaccess.DatabaseAccessor;
import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.internal.descriptors.DescriptorIterator;
import org.eclipse.persistence.internal.expressions.SQLSelectStatement;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.queries.JoinedAttributeManager;
import org.eclipse.persistence.internal.queries.MappedKeyMapContainerPolicy;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.internal.security.PrivilegedNewInstanceFromClass;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.ChangeRecord;
import org.eclipse.persistence.internal.sessions.DirectToFieldChangeRecord;
import org.eclipse.persistence.internal.sessions.MergeManager;
import org.eclipse.persistence.internal.sessions.ObjectChangeSet;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.mappings.converters.ClassInstanceConverter;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.mappings.converters.ObjectTypeConverter;
import org.eclipse.persistence.mappings.converters.SerializedObjectConverter;
import org.eclipse.persistence.mappings.converters.TypeConversionConverter;
import org.eclipse.persistence.mappings.querykeys.DirectQueryKey;
import org.eclipse.persistence.mappings.querykeys.QueryKey;
import org.eclipse.persistence.queries.DataReadQuery;
import org.eclipse.persistence.queries.ObjectBuildingQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReadQuery;
import org.eclipse.persistence.sessions.CopyGroup;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.remote.DistributedSession;

/**
 * <b>Purpose</b>: Maps an attribute to the corresponding database field type.
 * The list of field types that are supported by EclipseLink's direct to field 
 * mapping is dependent on the relational database being used.
 * A converter can be used to convert between the object and data type if they 
 * do not match.
 * 
 * NOTE: Much of the content of this class was extracted from 
 * AbstractDirectMapping. With the new MultitenantPrimaryKeyMapping (property 
 * mapping) where an attribute does not exist, attribute mapping code has been 
 * extracted here with AbstractDirectMapping containing the common code used for 
 * both attribute and property mappings to a direct field. 
 *
 * @see Converter
 * @see ObjectTypeConverter
 * @see TypeConversionConverter
 * @see SerializedObjectConverter
 * @see ClassInstanceConverter
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.4
 */
public abstract class AbstractAttributeDirectMapping extends AbstractDirectMapping implements MapKeyMapping {

    /** To specify the conversion type */
    protected transient Class attributeClassification;
    protected transient String attributeClassificationName;
    
    /** PERF: Also store object class of attribute in case of primitive. */
    protected transient Class attributeObjectClassification;

    /** Allows user defined conversion between the object attribute value and the database value. */
    protected Converter converter;
    protected String converterClassName;
    
    protected DatabaseTable keyTableForMapKey = null;
    
    /** Flag to support insertable JPA setting */
    protected boolean isInsertable = true;
    
    /** Flag to support updatable JPA setting */
    protected boolean isUpdatable = true;
    
    /**
     * PERF: Indicates if this mapping's attribute is a simple atomic value and cannot be modified, only replaced.
     * This is a tri-state to allow user to set to true or false, as default is false but
     * some data-types such as Calendar or byte[] or converter types may be desired to be used as mutable.
     */
    protected Boolean isMutable;
    
    /** Support specification of the value to use for null. */
    protected transient Object nullValue;
    
    protected String fieldClassificationClassName = null;
    
    /**
     * INTERNAL:
     * Used when initializing queries for mappings that use a Map
     * Called when the insert query is being initialized to ensure the fields for the map key are in the insert query.
     */
    public void addFieldsForMapKey(AbstractRecord joinRow) {
        if (!isReadOnly()) {
            if (isUpdatable()){
                joinRow.put(getField(), null);
            }
        }
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
     * For mappings used as MapKeys in MappedKeyContainerPolicy.  Add the target of this mapping to the deleted 
     * objects list if necessary
     *
     * This method is used for removal of private owned relationships
     * DirectMappings are dealt with in their parent delete, so this is a no-op.
     */
    public void addKeyToDeletedObjectsList(Object object, Map deletedObjects) {
    }
    
    /**
     * INTERNAL:
     * Clone the attribute from the clone and assign it to the backup.
     */
    @Override
    public void buildBackupClone(Object clone, Object backup, UnitOfWorkImpl unitOfWork) {
        buildClone(clone, null, backup, unitOfWork);
    }
    
    /**
     * INTERNAL:
     * Clone the attribute from the original and assign it to the clone.
     */
    @Override
    public void buildClone(Object original, CacheKey cacheKey, Object clone, AbstractSession cloningSession) {
        buildCloneValue(original, clone, cloningSession);
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
                newAttributeValue = getAttributeValue(getFieldValue(attributeValue, (AbstractSession) session), session);
            }
        }
        return newAttributeValue;
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
     * Directly build a change record without comparison
     */
    @Override
    public ChangeRecord buildChangeRecord(Object clone, ObjectChangeSet owner, AbstractSession session) {
        return internalBuildChangeRecord(getAttributeValueFromObject(clone), null, owner);
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
    public Object buildElementClone(Object attributeValue, Object parent, CacheKey cacheKey, AbstractSession cloningSession, boolean isExisting){
        return buildCloneValue(attributeValue, cloningSession);
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
        // objects referenced by this mapping are not registered as they have
        // no identity, this is a no-op.
    }
    
    /**
     * INTERNAL:
     * Cascade perform delete through mappings that require the cascade.
     */
    public void cascadePerformRemoveIfRequired(Object object, UnitOfWorkImpl uow, Map visitedObjects, boolean getAttributeValueFromObject) {
        // objects referenced by this mapping are not registered as they have
        // no identity, this is a no-op.
    }
    
    /**
     * INTERNAL:
     * Cascade registerNew for Create through mappings that require the cascade.
     */
    public void cascadeRegisterNewIfRequired(Object object, UnitOfWorkImpl uow, Map visitedObjects, boolean getAttributeValueFromObject) {
        // objects referenced by this mapping are not registered as they have
        // no identity, this is a no-op.
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
            
        if (converter != null) {
            if (converter instanceof TypeConversionConverter) {
                ((TypeConversionConverter)converter).convertClassNamesToClasses(classLoader);
            } else if (converter instanceof ObjectTypeConverter) {
                // To avoid 1.5 dependencies with the EnumTypeConverter check
                // against ObjectTypeConverter.
                ((ObjectTypeConverter) converter).convertClassNamesToClasses(classLoader);
            }
        } 
        
        // Instantiate any custom converter class
        if (converterClassName != null) {
            Class converterClass;
            Converter converter;
    
            try {
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    try {
                        converterClass = (Class) AccessController.doPrivileged(new PrivilegedClassForName(converterClassName, true, classLoader));
                    } catch (PrivilegedActionException exception) {
                        throw ValidationException.classNotFoundWhileConvertingClassNames(converterClassName, exception.getException());
                    }
                    
                    try {
                        converter = (Converter) AccessController.doPrivileged(new PrivilegedNewInstanceFromClass(converterClass));
                    } catch (PrivilegedActionException exception) {
                        throw ValidationException.classNotFoundWhileConvertingClassNames(converterClassName, exception.getException());
                    }
                } else {
                    converterClass = org.eclipse.persistence.internal.security.PrivilegedAccessHelper.getClassForName(converterClassName, true, classLoader);
                    converter = (Converter) org.eclipse.persistence.internal.security.PrivilegedAccessHelper.newInstanceFromClass(converterClass);
                }
            } catch (ClassNotFoundException exc) {
                throw ValidationException.classNotFoundWhileConvertingClassNames(converterClassName, exc);
            } catch (Exception e) {
                // Catches IllegalAccessException and InstantiationException
                throw ValidationException.classNotFoundWhileConvertingClassNames(converterClassName, e);
            }
            
            setConverter(converter);
        }
        
        if (fieldClassificationClassName != null){
            Class fieldClassification = null;
            try {
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    try {
                        fieldClassification = (Class) AccessController.doPrivileged(new PrivilegedClassForName(fieldClassificationClassName, true, classLoader));
                    } catch (PrivilegedActionException exception) {
                        throw ValidationException.classNotFoundWhileConvertingClassNames(converterClassName, exception.getException());
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
     * INTERNAL
     * Called when a DatabaseMapping is used to map the key in a collection and a join query is executed.  Returns the key.
     */
    public Object createMapComponentFromJoinedRow(AbstractRecord dbRow, JoinedAttributeManager joinManger, ObjectBuildingQuery query, CacheKey parentCacheKey, AbstractSession session, boolean isTargetProtected) {
        return createMapComponentFromRow(dbRow, query, parentCacheKey, session, isTargetProtected);
    }
    
    /**
     * INTERNAL
     * Called when a DatabaseMapping is used to map the key in a collection.  Returns the key.
     */
    public Object createMapComponentFromRow(AbstractRecord dbRow, ObjectBuildingQuery query, CacheKey parentCacheKey, AbstractSession session, boolean isTargetProtected) {
        Object key = dbRow.get(getField());
        key = getAttributeValue(key, session);
        return key;
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
     * Create a query key that links to the map key.
     */
    public QueryKey createQueryKeyForMapKey() {
        DirectQueryKey queryKey = new DirectQueryKey();
        queryKey.setField(field);
        return queryKey;
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
     * This key object may be a shallow stub of the actual object if the key is an Entity type.
     */
    public Object createStubbedMapComponentFromSerializableKeyInfo(Object keyInfo, AbstractSession session){
        return keyInfo;
    }
    
    /**
     * INTERNAL:
     * For mappings used as MapKeys in MappedKeyContainerPolicy, Delete the passed object if necessary.
     * 
     * This method is used for removal of private owned relationships
     * DirectMappings are dealt with in their parent delete, so this is a no-op.
     */
    public void deleteMapKey(Object objectDeleted, AbstractSession session) {}
    
    /**
     * INTERNAL:
     * Extract the fields for the Map key from the object to use in a query.
     */
    public Map extractIdentityFieldsForQuery(Object object, AbstractSession session){
        Map fields = new HashMap();
        Object key = object;
        if (hasConverter()) {
             key = getConverter().convertObjectValueToDataValue(key , session);
        }
        fields.put(getField(), key);
        return fields;
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
     * Return any tables that will be required when this mapping is used as part of a join query.
     */
    public List<DatabaseTable> getAdditionalTablesForJoinQuery() {
        List tables = new ArrayList(1);
        tables.add(getField().getTable());
        return tables;
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
    @Override
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
    @Override
    public Object getAttributeValue(Object fieldValue, Session session) {
        return getObjectValue(fieldValue, session);
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
     * Return the classification for the field contained in the mapping.
     * This is used to convert the row value to a consistent Java value.
     */
    public Class getFieldClassification(DatabaseField fieldToClassify) {
        // PERF: This method is a major performance code point,
        // so has been micro optimized and uses direct variable access.
        if (fieldToClassify.type != null) {
            return fieldToClassify.type;
        } else {
            if (this.converter != null) {
                return null;
            } else {
                // PERF: Ensure the object type is used for primitives.
                return Helper.getObjectClass(this.attributeClassification);
            }
        }
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
        Class fieldClassification = getFieldClassification(this.field);
        // PERF: Avoid conversion if not required.
        // EclipseLink bug 240407 - nulls not translated when writing to database
        if ((fieldValue == null) || (fieldClassification != fieldValue.getClass())) {
            try {
                fieldValue = session.getPlatform(this.descriptor.getJavaClass()).convertObject(fieldValue, fieldClassification);
            } catch (ConversionException exception) {
                throw ConversionException.couldNotBeConverted(this, this.descriptor, exception);
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
     * Allows for subclasses to convert the attribute value.
     */
    @Override
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
                try {
                    attributeValue = session.getDatasourcePlatform().convertObject(attributeValue, this.attributeClassification);
                } catch (ConversionException e) {
                    throw ConversionException.couldNotBeConverted(this, getDescriptor(), e);
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
     * If required, get the targetVersion of the source object from the merge manager.
     * Used with MapKeyContainerPolicy to abstract getting the target version of a source key.
     */
    public Object getTargetVersionOfSourceObject(Object object, Object parent, MergeManager mergeManager, AbstractSession targetSession){
       return object;
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
        if ((mergeManager.shouldMergeCloneIntoWorkingCopy() || mergeManager.shouldMergeCloneWithReferencesIntoWorkingCopy())
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
     * Making any mapping changes necessary to use a the mapping as a map key after initializing the mapping.
     */
    public void postInitializeMapKey(MappedKeyMapContainerPolicy policy) {
        if (getField().getType() == null) {
            getField().setType(getFieldClassification(getField()));
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
            if (getConverter() != null) {
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
    }
    
    /**
     * INTERNAL:
     * Making any mapping changes necessary to use a the mapping as a map key prior to initializing the mapping.
     */
    //@Override
    public void preinitializeMapKey(DatabaseTable table) throws DescriptorException {
        this.keyTableForMapKey = table;
    }
    
    /**
     * Indicates if the mapping has a converter set on it.
     * 
     * @return true if there is a converter set on the mapping,
     * false otherwise.
     */
    public boolean hasConverter() {
        return getConverter() != null;
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
    public boolean isAbstractAttributeDirectMapping() {
        return true;
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
     */
    @Override
    public boolean isDirectToFieldMapping() {
        return true;
    }
    
    @Override
    public boolean isInsertable() {
        return isInsertable;
    }
    
    /**
     * PUBLIC:
     * Return true if the attribute for this mapping is a simple atomic value that cannot be modified,
     * only replaced.
     * This is false by default unless a mutable converter is used such as the SerializedObjectConverter.
     * This can be set to false in this case, or if a Calendar or byte[] is desired to be used as a mutable value it can be set to true.
     */
    @Override
    public boolean isMutable() {
        if (isMutable == null) {
            return false;
        }
        return isMutable.booleanValue();
    }
    
    @Override
    public boolean isUpdatable() {
        return isUpdatable;
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
     * Set the converter on the mapping.
     * A converter can be used to convert between the object's value and database value of the attribute.
     */
    public void setConverter(Converter converter) {
        this.converter = converter;
    }
    
    /**
     * PUBLIC:
     * Set the converter class name on the mapping. It will be instantiated 
     * during the convertClassNamesToClasses.
     * A converter can be used to convert between the object's value and 
     * database value of the attribute.
     */
    public void setConverterClassName(String converterClassName) {
        this.converterClassName = converterClassName;
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
     * Either create a new change record or update with the new value. This is 
     * used by attribute change tracking.
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
     * Allow the key mapping to unwrap the object.
     */    
    public Object unwrapKey(Object key, AbstractSession session){
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
        Object fieldValue = row.get(this.field);
        Object attributeValue = getAttributeValue(fieldValue, executionSession);

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
            return resultSet.getString(columnNumber);
        } else if (this.attributeObjectClassification == ClassConstants.LONG) {
            return resultSet.getLong(columnNumber);
        } else if (this.attributeObjectClassification == ClassConstants.INTEGER) {
            return resultSet.getInt(columnNumber);
        }
        return accessor.getObject(resultSet, this.field, metaData, columnNumber, platform, true, session);
    }
    
    /**
     * INTERNAL:
     * Allow the key mapping to wrap the object.
     */
    public Object wrapKey(Object key, AbstractSession session){
        return key;
    }
}
