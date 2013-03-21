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
package org.eclipse.persistence.mappings;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.*;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.indirection.ValueHolder;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.queries.JoinedAttributeManager;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.internal.sessions.*;
import org.eclipse.persistence.mappings.foundation.AbstractDirectMapping;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.mappings.querykeys.*;

/**
 * <p><b>Purpose</b>: Variable one to one mappings are used to represent a pointer references
 * between a java object and an implementer of an interface. This mapping is usually represented by a single pointer
 * (stored in an instance variable) between the source and target objects. In the relational
 * database tables, these mappings are normally implemented using a foreign key and a type code.
 *
 * @author Sati
 * @since TOPLink/Java 2.0
 */
public class VariableOneToOneMapping extends ObjectReferenceMapping implements RelationalMapping {
    protected DatabaseField typeField;
    protected Map sourceToTargetQueryKeyNames;
    protected Map typeIndicatorTranslation;

    /** parallel table typeIndicatorTranslation used prior to initialization to avoid type indicators on Mapping Workbench */
    protected Map typeIndicatorNameTranslation;

    /**
     * PUBLIC:
     * Default constructor.
     */
    public VariableOneToOneMapping() {
        this.selectionQuery = new ReadObjectQuery();
        this.sourceToTargetQueryKeyNames = new HashMap(2);
        this.typeIndicatorTranslation = new HashMap(5);
        this.typeIndicatorNameTranslation = new HashMap(5);
        this.foreignKeyFields = NonSynchronizedVector.newInstance(1);

        //right now only ForeignKeyRelationships are supported
        this.isForeignKeyRelationship = false;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean isRelationalMapping() {
        return true;
    }

    /**
     * PUBLIC:
     * Add a type indicator conversion to this mapping.
     */
    public void addClassIndicator(Class implementer, Object typeIndicator) {
        if (typeIndicator == null) {
            typeIndicator = Helper.NULL_VALUE;
        }

        getTypeIndicatorTranslation().put(implementer, typeIndicator);
        getTypeIndicatorTranslation().put(typeIndicator, implementer);
    }

    /**
     * INTERNAL:
     * Add indicators by classname.  For use by the Mapping Workbench to avoid classpath dependencies
     */
    public void addClassNameIndicator(String className, Object typeIndicator) {
        if (typeIndicator == null) {
            typeIndicator = Helper.NULL_VALUE;
        }
        getTypeIndicatorNameTranslation().put(className, typeIndicator);
    }

    /**
     * PUBLIC:
     * A foreign key from the source table and abstract query key from the interface descriptor are added to the
     * mapping. This method is used if there are multiple foreign keys.
     */
    public void addForeignQueryKeyName(DatabaseField sourceForeignKeyField, String targetQueryKeyName) {
        getSourceToTargetQueryKeyNames().put(sourceForeignKeyField, targetQueryKeyName);
        getForeignKeyFields().addElement(sourceForeignKeyField);
        this.setIsForeignKeyRelationship(true);
    }
    
    /**
     * PUBLIC:
     * A foreign key from the source table and abstract query key from the interface descriptor are added to the
     * mapping. This method is used if there are multiple foreign keys.
     */
    public void addForeignQueryKeyName(String sourceForeignKeyFieldName, String targetQueryKeyName) {
        addForeignQueryKeyName(new DatabaseField(sourceForeignKeyFieldName), targetQueryKeyName);
    }

    /**
     * PUBLIC:
     * Define the target foreign key relationship in the Variable 1-1 mapping.
     * This method is used for composite target foreign key relationships,
     * that is the target object's table has multiple foreign key fields to
     * the source object's primary key fields.
     * Both the target foreign key query name and the source primary key field name
     * must be specified.
     * The distinction between a foreign key and target foreign key is that the variable 1-1
     * mapping will not populate the target foreign key value when written (because it is in the target table).
     * Normally 1-1's are through foreign keys but in bi-directional 1-1's
     * the back reference will be a target foreign key.
     * In obscure composite legacy data models a 1-1 may consist of a foreign key part and
     * a target foreign key part, in this case both method will be called with the correct parts.
     */
    public void addTargetForeignQueryKeyName(String targetForeignQueryKeyName, String sourcePrimaryKeyFieldName) {
        DatabaseField sourceField = new DatabaseField(sourcePrimaryKeyFieldName);

        getSourceToTargetQueryKeyNames().put(sourceField, targetForeignQueryKeyName);
    }

    /**
     * INTERNAL:
     * Possible for future development, not currently supported.
     *
     * Retrieve the value through using batch reading.
     * This executes a single query to read the target for all of the objects and stores the
     * result of the batch query in the original query to allow the other objects to share the results.
     */
    @Override
    protected Object batchedValueFromRow(AbstractRecord row, ObjectLevelReadQuery query, CacheKey parentCacheKey) {
        throw QueryException.batchReadingNotSupported(this, query);
    }

    /**
     * INTERNAL:
     * This methods clones all the fields and ensures that each collection refers to
     * the same clones.
     */
    @Override
    public Object clone() {
        VariableOneToOneMapping clone = (VariableOneToOneMapping)super.clone();
        Map setOfKeys = new HashMap(getSourceToTargetQueryKeyNames().size());
        Map sourceToTarget = new HashMap(getSourceToTargetQueryKeyNames().size());
        Vector foreignKeys = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(getForeignKeyFields().size());

        if (getTypeField() != null) {
            clone.setTypeField(this.getTypeField().clone());
        }

        for (Iterator enumtr = getSourceToTargetQueryKeyNames().keySet().iterator(); enumtr.hasNext();) {
            // Clone the SourceKeyFields
            DatabaseField field = (DatabaseField)enumtr.next();
            DatabaseField clonedField = field.clone();
            setOfKeys.put(field, clonedField);
            // on the next line I'm cloning the query key names
            sourceToTarget.put(clonedField, getSourceToTargetQueryKeyNames().get(field));
        }

        for (Enumeration enumtr = getForeignKeyFields().elements(); enumtr.hasMoreElements();) {
            DatabaseField field = (DatabaseField)enumtr.nextElement();
            foreignKeys.addElement(setOfKeys.get(field));
        }
        clone.setSourceToTargetQueryKeyFields(sourceToTarget);
        clone.setForeignKeyFields(foreignKeys);
        clone.setTypeIndicatorTranslation(new HashMap(this.getTypeIndicatorTranslation()));
        return clone;
    }

    /**
     * INTERNAL:
     * Return all the fields populated by this mapping.
     */
    @Override
    protected Vector collectFields() {
        DatabaseField type = getTypeField();

        //Get a shallow copy of the Vector
        if (type != null) {
            Vector sourceFields = (Vector)getForeignKeyFields().clone();
            sourceFields.addElement(type);
            return sourceFields;
        } else {
            return getForeignKeyFields();
        }
    }

    /**
     * INTERNAL:
     * Compare the references of the two objects are the same, not the objects themselves.
     * Used for independent relationships.
     * This is used for testing and validation purposes.
     *
     * Must get separate fields for the objects because we may be adding a different class to the
     * attribute because of the interface
     */
    @Override
    protected boolean compareObjectsWithoutPrivateOwned(Object firstObject, Object secondObject, AbstractSession session) {
        Object firstPrivateObject = getRealAttributeValueFromObject(firstObject, session);
        Object secondPrivateObject = getRealAttributeValueFromObject(secondObject, session);

        if ((firstPrivateObject == null) && (secondPrivateObject == null)) {
            return true;
        }

        if ((firstPrivateObject == null) || (secondPrivateObject == null)) {
            return false;
        }
        if (firstPrivateObject.getClass() != secondPrivateObject.getClass()) {
            return false;
        }
        Iterator targetKeys = getSourceToTargetQueryKeyNames().values().iterator();
        ClassDescriptor descriptor = session.getDescriptor(firstPrivateObject.getClass());
        ClassDescriptor descriptor2 = session.getDescriptor(secondPrivateObject.getClass());

        while (targetKeys.hasNext()) {
            String queryKey = (String)targetKeys.next();
            DatabaseField field = descriptor.getObjectBuilder().getFieldForQueryKeyName(queryKey);
            Object firstObjectField = descriptor.getObjectBuilder().extractValueFromObjectForField(firstPrivateObject, field, session);
            DatabaseField field2 = descriptor2.getObjectBuilder().getFieldForQueryKeyName(queryKey);
            Object secondObjectField = descriptor2.getObjectBuilder().extractValueFromObjectForField(secondPrivateObject, field2, session);

            if (!((firstObjectField == null) && (secondObjectField == null))) {
                if ((firstObjectField == null) || (secondObjectField == null)) {
                    return false;
                }
                if (!firstObjectField.equals(secondObjectField)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * INTERNAL:
     * Return the class indicator associations for XML.
     * List of class-name/value associations.
     */
    public Vector getClassIndicatorAssociations() {
        Vector associations = new Vector();
        Iterator classesEnum = getTypeIndicatorNameTranslation().keySet().iterator();
        Iterator valuesEnum = getTypeIndicatorNameTranslation().values().iterator();
        while (classesEnum.hasNext()) {
            Object className = classesEnum.next();

            // If the project was built in runtime is a class, MW is a string.
            if (className instanceof Class) {
                className = ((Class)className).getName();
            }
            Object value = valuesEnum.next();
            associations.addElement(new TypedAssociation(className, value));
        }

        return associations;
    }

    /**
     * INTERNAL:
     * Return a descriptor for the target of this mapping
     * For normal ObjectReferenceMappings, we return the reference descriptor.  For
     * a VariableOneToOneMapping, the reference descriptor is often a descriptor for an
     * interface and does not contain adequate information.  As a result, we look up
     * the descriptor for the specific class we are looking for
     * Bug 2612571
     */
    @Override
    public ClassDescriptor getDescriptorForTarget(Object targetObject, AbstractSession session) {
        return session.getDescriptor(targetObject);
    }

    /**
     * INTERNAL:
     * Return the classification for the field contained in the mapping.
     * This is used to convert the row value to a consistent java value.
     */
    @Override
    public Class getFieldClassification(DatabaseField fieldToClassify) {
        if ((getTypeField() != null) && (fieldToClassify.equals(getTypeField()))) {
            return getTypeField().getType();
        }

        String queryKey = (String)getSourceToTargetQueryKeyNames().get(fieldToClassify);
        if (queryKey == null) {
            return null;
        }
        // Search any of the implementor descriptors for a mapping for the query-key.
        Iterator iterator = getReferenceDescriptor().getInterfacePolicy().getChildDescriptors().iterator();
        if (iterator.hasNext()) {
            ClassDescriptor firstChild = (ClassDescriptor)iterator.next();
            DatabaseMapping mapping = firstChild.getObjectBuilder().getMappingForAttributeName(queryKey);
            if ((mapping != null) && (mapping.isDirectToFieldMapping())) {
                return ((AbstractDirectMapping)mapping).getAttributeClassification();
            }
            QueryKey targetQueryKey = firstChild.getQueryKeyNamed(queryKey);
            if ((targetQueryKey != null) && (targetQueryKey.isDirectQueryKey())) {
                return firstChild.getObjectBuilder().getFieldClassification(((DirectQueryKey)targetQueryKey).getField());
            }            
        }
        return null;
    }

    /**
     * PUBLIC:
     * Return the foreign key field names associated with the mapping.
     * These are only the source fields that are writable.
     */
    public Vector getForeignKeyFieldNames() {
        Vector fieldNames = new Vector(getForeignKeyFields().size());
        for (Enumeration fieldsEnum = getForeignKeyFields().elements();
                 fieldsEnum.hasMoreElements();) {
            fieldNames.addElement(((DatabaseField)fieldsEnum.nextElement()).getQualifiedName());
        }

        return fieldNames;
    }

    /**
     * INTERNAL:
     * Return the implementor for a specified type
     */
    protected Object getImplementorForType(Object type, AbstractSession session) {
        if (type == null) {
            return getTypeIndicatorTranslation().get(Helper.NULL_VALUE);
        }

        // Must ensure the type is the same, i.e. Integer != BigDecimal.
        try {
            type = session.getDatasourcePlatform().convertObject(type, getTypeField().getType());
        } catch (ConversionException e) {
            throw ConversionException.couldNotBeConverted(this, getDescriptor(), e);
        }

        return getTypeIndicatorTranslation().get(type);
    }

    /**
     * PUBLIC:
     * Return a collection of the field to query key associations.
     */
    public Vector getSourceToTargetQueryKeyFieldAssociations() {
        Vector associations = new Vector(getSourceToTargetQueryKeyNames().size());
        Iterator sourceFieldEnum = getSourceToTargetQueryKeyNames().keySet().iterator();
        Iterator targetQueryKeyEnum = getSourceToTargetQueryKeyNames().values().iterator();
        while (sourceFieldEnum.hasNext()) {
            Object fieldValue = ((DatabaseField)sourceFieldEnum.next()).getQualifiedName();
            Object attributeValue = targetQueryKeyEnum.next();
            associations.addElement(new Association(fieldValue, attributeValue));
        }

        return associations;
    }

    /**
     * INTERNAL:
     * Returns the source keys to target keys fields association.
     */
    public Map getSourceToTargetQueryKeyNames() {
        return sourceToTargetQueryKeyNames;
    }

    public DatabaseField getTypeField() {
        return typeField;
    }

    /**
     * PUBLIC:
     * This method returns the name of the typeField of the mapping.
     * The type field is used to store the type of object the relationship is referencing.
     */
    public String getTypeFieldName() {
        if (getTypeField() == null) {
            return null;
        }
        return getTypeField().getQualifiedName();
    }

    /**
     * INTERNAL:
     * Return the type for a specified implementor
     */
    protected Object getTypeForImplementor(Class implementor) {
        Object type = getTypeIndicatorTranslation().get(implementor);
        if (type == Helper.NULL_VALUE) {
            type = null;
        }

        return type;
    }

    /**
     * INTERNAL:
     * Return the type indicators.
     */
    public Map getTypeIndicatorTranslation() {
        return typeIndicatorTranslation;
    }

    /**
     * INTERNAL:
     * Return the typeIndicatorName translation
     * Used by the Mapping Workbench to avoid classpath dependencies
     */
    public Map getTypeIndicatorNameTranslation() {
        if (typeIndicatorNameTranslation.isEmpty() && !typeIndicatorTranslation.isEmpty()) {
            Iterator keysEnum = typeIndicatorTranslation.keySet().iterator();
            Iterator valuesEnum = typeIndicatorTranslation.values().iterator();
            while (keysEnum.hasNext()) {
                Object key = keysEnum.next();
                Object value = valuesEnum.next();
                if (key instanceof Class) {
                    String className = ((Class)key).getName();
                    typeIndicatorNameTranslation.put(className, value);
                }
            }
        }

        return typeIndicatorNameTranslation;
    }

    /**
     * INTERNAL:
     * Convert all the class-name-based settings in this mapping to actual class-based
     * settings. This method is used when converting a project that has been built
     * with class names to a project with classes.
     */
    @Override
    public void convertClassNamesToClasses(ClassLoader classLoader){
        super.convertClassNamesToClasses(classLoader);
        Iterator iterator = getTypeIndicatorNameTranslation().entrySet().iterator();
        this.typeIndicatorTranslation = new HashMap();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry)iterator.next();
            String referenceClassName = (String)entry.getKey();
            Object indicator = entry.getValue();
            Class referenceClass = null;
            try{
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
                    try {
                        referenceClass = (Class)AccessController.doPrivileged(new PrivilegedClassForName(referenceClassName, true, classLoader));
                    } catch (PrivilegedActionException exception) {
                        throw ValidationException.classNotFoundWhileConvertingClassNames(referenceClassName, exception.getException());
                    }
                } else {
                    referenceClass = PrivilegedAccessHelper.getClassForName(referenceClassName, true, classLoader);
                }
            } catch (ClassNotFoundException exception) {
                throw ValidationException.classNotFoundWhileConvertingClassNames(referenceClassName, exception);
            }
            addClassIndicator(referenceClass, indicator);
        }
    }

    /**
     * INTERNAL:
     * Initialize the mapping.
     */
    @Override
    public void initialize(AbstractSession session) {
        super.initialize(session);
        initializeForeignKeys(session);
        setFields(collectFields());
        if (usesIndirection()) {
            for (DatabaseField field : this.fields) {
                field.setKeepInRow(true);
            }
        }
        if (getTypeField() != null) {
            setTypeField(getDescriptor().buildField(getTypeField()));
        }
        if (shouldInitializeSelectionCriteria()) {
            initializeSelectionCriteria(session);
        }
    }

    /**
     * INTERNAL:
     * The foreign key names and their primary keys are converted to DatabaseField and stored.
     */
    protected void initializeForeignKeys(AbstractSession session) {
        HashMap newSourceToTargetQueryKeyNames = new HashMap(getSourceToTargetQueryKeyNames().size());
        Iterator iterator = getSourceToTargetQueryKeyNames().entrySet().iterator();
        while(iterator.hasNext()) {
            Map.Entry entry = (Map.Entry)iterator.next();
            DatabaseField field = getDescriptor().buildField((DatabaseField)entry.getKey());
            newSourceToTargetQueryKeyNames.put(field, entry.getValue());
        }
        this.sourceToTargetQueryKeyNames = newSourceToTargetQueryKeyNames;
    }

    /**
     * INTERNAL:
     * Selection criteria is created with source foreign keys and target keys.
     * This criteria is then used to read target records from the table.
     */
    public void initializeSelectionCriteria(AbstractSession session) {
        Expression selectionCriteria = null;
        Expression expression;

        ExpressionBuilder expBuilder = new ExpressionBuilder();

        Iterator sourceKeysEnum = getSourceToTargetQueryKeyNames().keySet().iterator();

        while (sourceKeysEnum.hasNext()) {
            DatabaseField sourceKey = (DatabaseField)sourceKeysEnum.next();
            String target = (String)this.getSourceToTargetQueryKeyNames().get(sourceKey);
            expression = expBuilder.getParameter(sourceKey).equal(expBuilder.get(target));

            if (selectionCriteria == null) {
                selectionCriteria = expression;
            } else {
                selectionCriteria = expression.and(selectionCriteria);
            }
        }

        setSelectionCriteria(selectionCriteria);
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean isVariableOneToOneMapping() {
        return true;
    }

    /**
     * INTERNAL:
     */
    @Override
    protected Object getPrimaryKeyForObject(Object object, AbstractSession session) {
        return session.getId(object);
    }

    /**
     * INTERNAL:
     * Set the type field classification through searching the indicators hashtable.
     */
    @Override
    public void preInitialize(AbstractSession session) throws DescriptorException {
        super.preInitialize(session);
        if (getTypeIndicatorTranslation().isEmpty()) {
            return;
        }
        Class type = null;
        for (Iterator typeValuesEnum = getTypeIndicatorTranslation().values().iterator();
                 typeValuesEnum.hasNext() && (type == null);) {
            Object value = typeValuesEnum.next();
            if ((value != Helper.NULL_VALUE) && (!(value instanceof Class))) {
                type = value.getClass();
            }
        }

        getTypeField().setType(type);
    }

    /**
     * INTERNAL:
     * Rehash any maps based on fields.
     * This is used to clone descriptors for aggregates, which hammer field names.
     */
    @Override
    public void rehashFieldDependancies(AbstractSession session) {
        setSourceToTargetQueryKeyFields(Helper.rehashMap(getSourceToTargetQueryKeyNames()));
    }

    /**
     * PUBLIC:
     * Set the class indicator associations.
     */
    public void setClassIndicatorAssociations(Vector classIndicatorAssociations) {
        setTypeIndicatorNameTranslation(new HashMap(classIndicatorAssociations.size() + 1));
        setTypeIndicatorTranslation(new HashMap((classIndicatorAssociations.size() * 2) + 1));
        for (Enumeration associationsEnum = classIndicatorAssociations.elements();
                 associationsEnum.hasMoreElements();) {
            Association association = (Association)associationsEnum.nextElement();
            Object classValue = association.getKey();
            if (classValue instanceof Class) {
                // 904 projects will be a class type.
                addClassIndicator((Class)association.getKey(), association.getValue());
            } else {
                addClassNameIndicator((String)association.getKey(), association.getValue());
            }
        }
    }

    /**
     * PUBLIC:
     * Return the foreign key field names associated with the mapping.
     * These are only the source fields that are writable.
     */
    public void setForeignKeyFieldNames(Vector fieldNames) {
        Vector fields = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(fieldNames.size());
        for (Enumeration fieldNamesEnum = fieldNames.elements(); fieldNamesEnum.hasMoreElements();) {
            fields.addElement(new DatabaseField((String)fieldNamesEnum.nextElement()));
        }

        setForeignKeyFields(fields);
        if (!fields.isEmpty()) {
            setIsForeignKeyRelationship(true);
        }
    }

    /**
     * PUBLIC:
     * A foreign key from the source table and abstract query key from the interface descriptor are added to the
     * mapping. This method is used if foreign key is not composite.
     */
    public void setForeignQueryKeyName(String sourceForeignKeyFieldName, String targetQueryKeyName) {
        addForeignQueryKeyName(sourceForeignKeyFieldName, targetQueryKeyName);
    }

    /**
     * PUBLIC:
     * Set a collection of the source to target query key/field associations.
     */
    public void setSourceToTargetQueryKeyFieldAssociations(Vector sourceToTargetQueryKeyFieldAssociations) {
        setSourceToTargetQueryKeyFields(new HashMap(sourceToTargetQueryKeyFieldAssociations.size() + 1));
        for (Enumeration associationsEnum = sourceToTargetQueryKeyFieldAssociations.elements();
                 associationsEnum.hasMoreElements();) {
            Association association = (Association)associationsEnum.nextElement();
            Object sourceField = new DatabaseField((String)association.getKey());
            String targetQueryKey = (String)association.getValue();
            getSourceToTargetQueryKeyNames().put(sourceField, targetQueryKey);
        }
    }

    /**
     * INTERNAL:
     * Set the source keys to target keys fields association.
     */
    protected void setSourceToTargetQueryKeyFields(Map sourceToTargetQueryKeyNames) {
        this.sourceToTargetQueryKeyNames = sourceToTargetQueryKeyNames;
    }

    /**
     * INTERNAL:
     * This method set the typeField of the mapping to the parameter field
     */
    public void setTypeField(DatabaseField typeField) {
        this.typeField = typeField;
    }

    /**
     * PUBLIC:
     * This method sets the name of the typeField of the mapping.
     * The type field is used to store the type of object the relationship is referencing.
     */
    public void setTypeFieldName(String typeFieldName) {
        setTypeField(new DatabaseField(typeFieldName));
    }

    /**
     * INTERNAL:
     * Set the typeIndicatorTranslations hashtable to the new Hashtable translations
     */
    protected void setTypeIndicatorTranslation(Map translations) {
        this.typeIndicatorTranslation = translations;
    }

    /**
     * INTERNAL:
     * For avoiding classpath dependencies on the Mapping Workbench
     */
    protected void setTypeIndicatorNameTranslation(Map translations) {
        this.typeIndicatorNameTranslation = translations;
    }

    /**
     * INTERNAL:
     * Get a value from the object and set that in the respective field of the row.
     */
    @Override
    public Object valueFromObject(Object object, DatabaseField field, AbstractSession session) {
        // First check if the value can be obtained from the value holder's row.
    	AbstractRecord referenceRow = getIndirectionPolicy().extractReferenceRow(getAttributeValueFromObject(object));
        if (referenceRow != null) {
            Object value = referenceRow.get(field);

            // Must ensure the classification to get a cache hit.
            try {
                value = session.getDatasourcePlatform().convertObject(value, getFieldClassification(field));
            } catch (ConversionException e) {
                throw ConversionException.couldNotBeConverted(this, getDescriptor(), e);
            }

            return value;
        }

        //2.5.1.6 PWK.  added to support batch reading on variable one to ones
        Object referenceObject = getRealAttributeValueFromObject(object, session);
        String queryKeyName = (String)getSourceToTargetQueryKeyNames().get(field);
        ClassDescriptor objectDescriptor = session.getDescriptor(referenceObject.getClass());
        DatabaseField targetField = objectDescriptor.getObjectBuilder().getTargetFieldForQueryKeyName(queryKeyName);

        if (targetField == null) {
            // Bug 326091 - return the type value if the field passed is the type indicator field
            if (referenceObject != null && this.typeField != null && field.equals(this.typeField)) {
                return getTypeForImplementor(referenceObject.getClass());
            } else {
                return null;
            }
        }

        return objectDescriptor.getObjectBuilder().extractValueFromObjectForField(referenceObject, targetField, session);
    }

    /**
     * INTERNAL:
     * Return the value of the field from the row or a value holder on the query to obtain the object.
     * Check for batch + aggregation reading.
     */
    @Override
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
                    return this.getAttributeValueFromObject(cached);
                }
            } else if (!this.isCacheable && !isTargetProtected && cacheKey != null) {
                return this.indirectionPolicy.buildIndirectObject(new ValueHolder(null));
            }
        }
        // If any field in the foreign key is null then it means there are no referenced objects
        for (DatabaseField field : getFields()) {
            if (row.get(field) == null) {
                return getIndirectionPolicy().nullValueFromRow();
            }
        }

        if (getTypeField() != null) {
            // If the query used batched reading, return a special value holder,
            // or retrieve the object from the query property.
            if (sourceQuery.isObjectLevelReadQuery() && (((ObjectLevelReadQuery)sourceQuery).isAttributeBatchRead(this.descriptor, getAttributeName())
                    || (sourceQuery.isReadAllQuery() && shouldUseBatchReading()))) {
                return batchedValueFromRow(row, ((ObjectLevelReadQuery)sourceQuery), cacheKey);
            }

            //If the field is empty we cannot load the object because we do not know what class it will be
            if (row.get(getTypeField()) == null) {
                return getIndirectionPolicy().nullValueFromRow();
            }
            Class implementerClass = (Class)getImplementorForType(row.get(getTypeField()), executionSession);
            ReadObjectQuery query = (ReadObjectQuery)getSelectionQuery().clone();
            query.setReferenceClass(implementerClass);
            query.setSelectionCriteria(getSelectionCriteria());
            query.setDescriptor(null);// Must set to null so the right descriptor is used

            if (sourceQuery.isObjectLevelReadQuery() && (sourceQuery.shouldCascadeAllParts() || (sourceQuery.shouldCascadePrivateParts() && isPrivateOwned()) || (sourceQuery.shouldCascadeByMapping() && this.cascadeRefresh)) ) {
                query.setShouldRefreshIdentityMapResult(sourceQuery.shouldRefreshIdentityMapResult());
                query.setCascadePolicy(sourceQuery.getCascadePolicy());
                query.setShouldMaintainCache(sourceQuery.shouldMaintainCache());
                // For flashback.
                if (((ObjectLevelReadQuery)sourceQuery).hasAsOfClause()) {
                    query.setAsOfClause(((ObjectLevelReadQuery)sourceQuery).getAsOfClause());
                }

                //CR #4365 - used to prevent infinit recursion on refresh object cascade all
                query.setQueryId(sourceQuery.getQueryId());
            }

            return getIndirectionPolicy().valueFromQuery(query, row, executionSession);
        } else {
            return super.valueFromRow(row, joinManager, sourceQuery, cacheKey, executionSession, isTargetProtected, wasCacheUsed);
        }
    }

    /**
     * INTERNAL:
     * Get a value from the object and set that in the respective field of the row.
     */
    protected void writeFromNullObjectIntoRow(AbstractRecord record) {
        if (isReadOnly()) {
            return;
        }
        if (isForeignKeyRelationship()) {
            Enumeration foreignKeys = getForeignKeyFields().elements();
            while (foreignKeys.hasMoreElements()) {
                record.put((DatabaseField)foreignKeys.nextElement(), null);
                // EL Bug 319759 - if a field is null, then the update call cache should not be used
                record.setNullValueInFields(true);
            }
        }
        if (getTypeField() != null) {
            record.put(getTypeField(), null);
            record.setNullValueInFields(true);
        }
    }

    /**
     * INTERNAL:
     * Get a value from the object and set that in the respective field of the row.
     * If the mapping id target foreign key, you must only write the type into the roe, the rest will be updated
     * when the object itself is written
     */
    @Override
    public void writeFromObjectIntoRow(Object object, AbstractRecord record, AbstractSession session, WriteType writeType) {
        if (isReadOnly()) {
            return;
        }

        Object referenceObject = getRealAttributeValueFromObject(object, session);

        if (referenceObject == null) {
            writeFromNullObjectIntoRow(record);
        } else {
            if (isForeignKeyRelationship()) {
                Enumeration sourceFields = getForeignKeyFields().elements();
                ClassDescriptor descriptor = session.getDescriptor(referenceObject.getClass());
                while (sourceFields.hasMoreElements()) {
                    DatabaseField sourceKey = (DatabaseField)sourceFields.nextElement();
                    String targetQueryKey = (String)getSourceToTargetQueryKeyNames().get(sourceKey);
                    DatabaseField targetKeyField = descriptor.getObjectBuilder().getFieldForQueryKeyName(targetQueryKey);
                    if (targetKeyField == null) {
                        throw DescriptorException.variableOneToOneMappingIsNotDefinedProperly(this, descriptor, targetQueryKey);
                    }
                    Object referenceValue = descriptor.getObjectBuilder().extractValueFromObjectForField(referenceObject, targetKeyField, session);
                    // EL Bug 319759 - if a field is null, then the update call cache should not be used
                    if (referenceValue == null) {
                        record.setNullValueInFields(true);
                    }
                    record.put(sourceKey, referenceValue);
                }
            }
            if (getTypeField() != null) {
                record.put(getTypeField(), getTypeForImplementor(referenceObject.getClass()));
            }
        }
    }

    /**
     * INTERNAL:
     * Get a value from the object and set that in the respective field of the row.
     * If the mapping id target foreign key, you must only write the type into the roe, the rest will be updated
     * when the object itself is written
     */
    @Override
    public void writeFromObjectIntoRowWithChangeRecord(ChangeRecord changeRecord, AbstractRecord record, AbstractSession session, WriteType writeType) {
        if (isReadOnly()) {
            return;
        }

        ObjectChangeSet changeSet = (ObjectChangeSet)((ObjectReferenceChangeRecord)changeRecord).getNewValue();
        if (changeSet == null) {
            writeFromNullObjectIntoRow(record);
        } else {
            Object referenceObject = changeSet.getUnitOfWorkClone();
            if (isForeignKeyRelationship()) {
                Enumeration sourceFields = getForeignKeyFields().elements();
                ClassDescriptor descriptor = session.getDescriptor(referenceObject.getClass());
                while (sourceFields.hasMoreElements()) {
                    DatabaseField sourceKey = (DatabaseField)sourceFields.nextElement();
                    String targetQueryKey = (String)getSourceToTargetQueryKeyNames().get(sourceKey);
                    DatabaseField targetKeyField = descriptor.getObjectBuilder().getFieldForQueryKeyName(targetQueryKey);
                    if (targetKeyField == null) {
                        throw DescriptorException.variableOneToOneMappingIsNotDefinedProperly(this, descriptor, targetQueryKey);
                    }
                    Object referenceValue = descriptor.getObjectBuilder().extractValueFromObjectForField(referenceObject, targetKeyField, session);
                    // EL Bug 319759 - if a field is null, then the update call cache should not be used
                    if (referenceValue == null) {
                        record.setNullValueInFields(true);
                    }
                    record.put(sourceKey, referenceValue);
                }
            }
            if (getTypeField() != null) {
                record.put(getTypeField(), getTypeForImplementor(referenceObject.getClass()));
            }
        }
    }

    /**
     * INTERNAL:
     * This row is built for shallow insert which happens in case of bidirectional inserts.
     * The foreign keys must be set to null to avoid constraints.
     */
    @Override
    public void writeFromObjectIntoRowForShallowInsert(Object object, AbstractRecord record, AbstractSession session) {
        writeFromNullObjectIntoRow(record);
    }

    /**
     * INTERNAL:
     * This row is built for update after shallow insert which happens in case of bidirectional inserts.
     * It contains the foreign keys with non null values that were set to null for shallow insert.
     * If mapping overrides writeFromObjectIntoRowForShallowInsert method it must override this one, too.
     */
    public void writeFromObjectIntoRowForUpdateAfterShallowInsert(Object object, AbstractRecord row, AbstractSession session, DatabaseTable table) {
        if (!getFields().get(0).getTable().equals(table)) {
            return;
        }
        writeFromObjectIntoRow(object, row, session, WriteType.UPDATE);
    }
    
    /**
     * INTERNAL:
     * This row is built for shallow insert which happens in case of bidirectional inserts.
     * The foreign keys must be set to null to avoid constraints.
     */
    @Override
    public void writeFromObjectIntoRowForShallowInsertWithChangeRecord(ChangeRecord changeRecord, AbstractRecord record, AbstractSession session) {
        writeFromNullObjectIntoRow(record);
    }

    /**
     * INTERNAL:
     * Get a value from the object and set that in the respective field of the row.
     */
    @Override
    public void writeFromObjectIntoRowForWhereClause(ObjectLevelModifyQuery query, AbstractRecord record) {
        if (isReadOnly()) {
            return;
        }
        Object object;
        if (query.isDeleteObjectQuery()) {
            object = query.getObject();
        } else {
            object = query.getBackupClone();
        }
        Object referenceObject = getRealAttributeValueFromObject(object, query.getSession());

        if (referenceObject == null) {
            writeFromNullObjectIntoRow(record);
        } else {
            if (isForeignKeyRelationship()) {
                Enumeration sourceFields = getForeignKeyFields().elements();
                ClassDescriptor descriptor = query.getSession().getDescriptor(referenceObject.getClass());
                while (sourceFields.hasMoreElements()) {
                    DatabaseField sourceKey = (DatabaseField)sourceFields.nextElement();
                    String targetQueryKey = (String)getSourceToTargetQueryKeyNames().get(sourceKey);
                    DatabaseField targetKeyField = descriptor.getObjectBuilder().getFieldForQueryKeyName(targetQueryKey);
                    if (targetKeyField == null) {
                        throw DescriptorException.variableOneToOneMappingIsNotDefinedProperly(this, descriptor, targetQueryKey);
                    }
                    Object referenceValue = descriptor.getObjectBuilder().extractValueFromObjectForField(referenceObject, targetKeyField, query.getSession());
                    if (referenceValue == null) {
                        // EL Bug 319759 - if a field is null, then the update call cache should not be used
                        record.setNullValueInFields(true);
                    }
                    record.put(sourceKey, referenceValue);
                }
            }
            if (getTypeField() != null) {
                Object typeForImplementor = getTypeForImplementor(referenceObject.getClass());
                record.put(getTypeField(), typeForImplementor);
            }
        }
    }

    /**
     * INTERNAL:
     * Write fields needed for insert into the template for with null values.
     */
    @Override
    public void writeInsertFieldsIntoRow(AbstractRecord record, AbstractSession session) {
        writeFromNullObjectIntoRow(record);
    }
}
