/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.mappings;

import java.util.*;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.identitymaps.*;
import org.eclipse.persistence.internal.queries.*;
import org.eclipse.persistence.internal.sessions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.internal.descriptors.CascadeLockingPolicy;

/**
 * <p><b>Purpose</b>: This mapping is used to represent the
 * typical RDBMS relationship between a single
 * source object and collection of target objects; where,
 * on the database, the target objects have references
 * (foreign keys) to the source object.
 *
 * @author Sati
 * @since TOPLink/Java 1.0
 */
public class OneToManyMapping extends CollectionMapping implements RelationalMapping {

    /** The target foreign key fields that reference the sourceKeyFields. */
    protected transient Vector<DatabaseField> targetForeignKeyFields;

    /** The (typically primary) source key fields that are referenced by the targetForeignKeyFields. */
    protected transient Vector<DatabaseField> sourceKeyFields;

    /** This maps the target foreign key fields to the corresponding (primary) source key fields. */
    protected transient Map<DatabaseField, DatabaseField> targetForeignKeysToSourceKeys;
    
    /** This maps the (primary) source key fields to the corresponding target foreign key fields. */
    protected transient Map<DatabaseField, DatabaseField> sourceKeysToTargetForeignKeys;

    /**
     * PUBLIC:
     * Default constructor.
     */
    public OneToManyMapping() {
        super();

        this.targetForeignKeysToSourceKeys = new HashMap(2);
        this.sourceKeysToTargetForeignKeys = new HashMap(2);
        
        this.sourceKeyFields = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(1);
        this.targetForeignKeyFields = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(1);

        this.deleteAllQuery = new DeleteAllQuery();
    }

    /**
     * INTERNAL:
     */
    public boolean isRelationalMapping() {
        return true;
    }

    /**
     * INTERNAL:
     * Add the associated fields to the appropriate collections.
     */
    public void addTargetForeignKeyField(DatabaseField targetForeignKeyField, DatabaseField sourceKeyField) {
        getTargetForeignKeyFields().addElement(targetForeignKeyField);
        getSourceKeyFields().addElement(sourceKeyField);
    }

    /**
     * PUBLIC:
     * Define the target foreign key relationship in the one-to-many mapping.
     * This method is used for composite target foreign key relationships.
     * That is, the target object's table has multiple foreign key fields
     * that are references to
     * the source object's (typically primary) key fields.
     * Both the target foreign key field name and the corresponding
     * source primary key field name must be specified.
     * Because the target object's table must store a foreign key to the source table,
     * the target object must map that foreign key, this is normally done through a
     * one-to-one mapping back-reference. Other options include:
     * <ul>
     * <li> use a DirectToFieldMapping and maintain the
     * foreign key fields directly in the target
     * <li> use a ManyToManyMapping
     * <li> use an AggregateCollectionMapping
     * </ul>
     * @see DirectToFieldMapping
     * @see ManyToManyMapping
     * @see AggregateCollectionMapping
     */
    public void addTargetForeignKeyFieldName(String targetForeignKeyFieldName, String sourceKeyFieldName) {
        addTargetForeignKeyField(new DatabaseField(targetForeignKeyFieldName), new DatabaseField(sourceKeyFieldName));
    }

    /**
     * The selection criteria are created with target foreign keys and source "primary" keys.
     * These criteria are then used to read the target records from the table.
     * These criteria are also used as the default "delete all" criteria.
     *
     * CR#3922 - This method is almost the same as buildSelectionCriteria() the difference
     * is that TargetForeignKeysToSourceKeys contains more information after login then SourceKeyFields
     * contains before login.
     */
    protected Expression buildDefaultSelectionCriteria() {
        Expression selectionCriteria = null;
        Expression builder = new ExpressionBuilder();

        for (Iterator keys = getTargetForeignKeysToSourceKeys().keySet().iterator();
                 keys.hasNext();) {
            DatabaseField targetForeignKey = (DatabaseField)keys.next();
            DatabaseField sourceKey = getTargetForeignKeysToSourceKeys().get(targetForeignKey);

            Expression partialSelectionCriteria = builder.getField(targetForeignKey).equal(builder.getParameter(sourceKey));
            selectionCriteria = partialSelectionCriteria.and(selectionCriteria);
        }
        return selectionCriteria;
    }
    
    /**
     * This method would allow customers to get the potential selection criteria for a mapping
     * prior to initialization.  This would allow them to more easily create an ammendment method
     * that would ammend the SQL for the join.
     *
     * CR#3922 - This method is almost the same as buildDefaultSelectionCriteria() the difference
     * is that TargetForeignKeysToSourceKeys contains more information after login then SourceKeyFields
     * contains before login.
     */
    public Expression buildSelectionCriteria() {
        //CR3922	
        Expression selectionCriteria = null;
        Expression builder = new ExpressionBuilder();

        Enumeration sourceKeys = getSourceKeyFields().elements();
        for (Enumeration targetForeignKeys = getTargetForeignKeyFields().elements();
                 targetForeignKeys.hasMoreElements();) {
            DatabaseField targetForeignKey = (DatabaseField)targetForeignKeys.nextElement();
            DatabaseField sourceKey = (DatabaseField)sourceKeys.nextElement();
            Expression partialSelectionCriteria = builder.getField(targetForeignKey).equal(builder.getParameter(sourceKey));
            selectionCriteria = partialSelectionCriteria.and(selectionCriteria);
        }
        return selectionCriteria;
    }

    /**
     * INTERNAL:
     * Clone the appropriate attributes.
     */
    public Object clone() {
        OneToManyMapping clone = (OneToManyMapping)super.clone();
        clone.setTargetForeignKeysToSourceKeys(new HashMap(getTargetForeignKeysToSourceKeys()));
        return clone;
    }

    /**
     * Delete all the reference objects with a single query.
     */
    protected void deleteAll(DeleteObjectQuery query) throws DatabaseException {
        Object attribute = getAttributeAccessor().getAttributeValueFromObject(query.getObject()); 
        if(usesIndirection()) {
           if(attribute == null || !getIndirectionPolicy().objectIsInstantiated(attribute)) {
               // An empty Vector indicates to DeleteAllQuery that no objects should be removed from cache
               ((DeleteAllQuery)getDeleteAllQuery()).executeDeleteAll(query.getSession().getSessionForClass(getReferenceClass()), query.getTranslationRow(), new Vector(0));
               return;
           }
        }
        Object referenceObjects = this.getRealCollectionAttributeValueFromObject(query.getObject(), query.getSession());
        ((DeleteAllQuery)getDeleteAllQuery()).executeDeleteAll(query.getSession().getSessionForClass(getReferenceClass()), query.getTranslationRow(), getContainerPolicy().vectorFor(referenceObjects, query.getSession()));
     }

    /**
     *    This method will make sure that all the records privately owned by this mapping are
     * actually removed. If such records are found then those are all read and removed one
     * by one along with their privately owned parts.
     */
    protected void deleteReferenceObjectsLeftOnDatabase(DeleteObjectQuery query) throws DatabaseException, OptimisticLockException {
        Object objects = readPrivateOwnedForObject(query);

        // Delete all these object one by one.
        ContainerPolicy cp = getContainerPolicy();
        for (Object iter = cp.iteratorFor(objects); cp.hasNext(iter);) {
            query.getSession().deleteObject(cp.next(iter, query.getSession()));
        }
    }

    /**
     * We need to execute the batch query and store the
     * results in a hash table keyed by the (primary) keys
     * of the source objects.
     */
    protected Hashtable executeBatchQuery(DatabaseQuery query, AbstractSession session, AbstractRecord row) {
        ContainerPolicy mappingCP = this.getContainerPolicy();
        ContainerPolicy queryCP = ((ReadAllQuery)query).getContainerPolicy();
        Object queryResult = null;

        queryResult = session.executeQuery(query, row);

        Hashtable batchedObjects = new Hashtable(queryCP.sizeFor(queryResult));

        for (Object iter = queryCP.iteratorFor(queryResult); queryCP.hasNext(iter);) {
            Object eachReferenceObject = queryCP.next(iter, session);
            CacheKey eachForeignKey = new CacheKey(extractForeignKeyFromReferenceObject(eachReferenceObject, session));

            Object container = batchedObjects.get(eachForeignKey);
            if (container == null) {
                container = mappingCP.containerInstance();
                batchedObjects.put(eachForeignKey, container);
            }
            mappingCP.addInto(eachReferenceObject, container, session);
        }
        return batchedObjects;
    }

    /**
     * Extract the foreign key value from the reference object.
     * Used for batch reading. Keep the fields in the same order
     * as in the targetForeignKeysToSourceKeys hashtable.
     */
    protected Vector extractForeignKeyFromReferenceObject(Object object, AbstractSession session) {
        Vector foreignKey = new Vector(this.getTargetForeignKeysToSourceKeys().size());

        for (Iterator stream = getTargetForeignKeysToSourceKeys().entrySet().iterator();
                 stream.hasNext();) {
            Map.Entry entry = (Map.Entry)stream.next();
            DatabaseField targetField = (DatabaseField)entry.getKey();
            DatabaseField sourceField = (DatabaseField)entry.getValue();
            if (object == null) {
                foreignKey.addElement(null);
            } else {
                Object value = getReferenceDescriptor().getObjectBuilder().extractValueFromObjectForField(object, targetField, session);

                //CR:somenewsgroupbug need to ensure source and target types match.
                try {
                    value = session.getDatasourcePlatform().convertObject(value, getDescriptor().getObjectBuilder().getFieldClassification(sourceField));
                } catch (ConversionException e) {
                    throw ConversionException.couldNotBeConverted(this, getDescriptor(), e);
                }

                foreignKey.addElement(value);
            }
        }
        return foreignKey;
    }

    /**
     * Extract the key field values from the specified row.
     * Used for batch reading. Keep the fields in the same order
     * as in the targetForeignKeysToSourceKeys hashtable.
     */
    protected Vector extractKeyFromRow(AbstractRecord row, AbstractSession session) {
        Vector key = new Vector(this.getTargetForeignKeysToSourceKeys().size());

        for (Iterator stream = getTargetForeignKeysToSourceKeys().values().iterator();
                 stream.hasNext();) {
            DatabaseField field = (DatabaseField)stream.next();
            Object value = row.get(field);

            // Must ensure the classification to get a cache hit.
            try {
                value = session.getDatasourcePlatform().convertObject(value, getDescriptor().getObjectBuilder().getFieldClassification(field));
            } catch (ConversionException e) {
                throw ConversionException.couldNotBeConverted(this, getDescriptor(), e);
            }

            key.addElement(value);
        }
        return key;
    }

    /**
     * INTERNAL:
     * Extract the value from the batch optmized query.
     */
    public Object extractResultFromBatchQuery(DatabaseQuery query, AbstractRecord row, AbstractSession session, AbstractRecord argumentRow) {
        //this can be null, because either one exists in the query or it will be created
        Hashtable batchedObjects = null;
        synchronized (query) {
            batchedObjects = getBatchReadObjects(query, session);
            if (batchedObjects == null) {
                batchedObjects = executeBatchQuery(query, session, argumentRow);
                setBatchReadObjects(batchedObjects, query, session);
            }
        }
        Object result = batchedObjects.get(new CacheKey(extractKeyFromRow(row, session)));

        // The source object might not have any target objects
        if (result == null) {
            return getContainerPolicy().containerInstance();
        } else {
            return result;
        }
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
     * Return the source key fields.
     */
    public Vector<DatabaseField> getSourceKeyFields() {
        return sourceKeyFields;
    }
    
    /**
     * INTERNAL:
     * Return the source/target key fields.
     */
    public Map<DatabaseField, DatabaseField> getSourceKeysToTargetForeignKeys() {
        return sourceKeysToTargetForeignKeys;
    }

    /**
     * INTERNAL:
     * Return the target foreign key field names associated with the mapping.
     * These are in-order with the targetForeignKeyFieldNames.
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
     * Return the target foreign key fields.
     */
    public Vector<DatabaseField> getTargetForeignKeyFields() {
        return targetForeignKeyFields;
    }

    /**
     * INTERNAL:
     * Return the target/source key fields.
     */
    public Map<DatabaseField, DatabaseField> getTargetForeignKeysToSourceKeys() {
        return targetForeignKeysToSourceKeys;
    }

    /**
     * INTERNAL:
     * Maintain for backward compatibility.
     * This is 'public' so StoredProcedureGenerator
     * does not have to use the custom query expressions.
     */
    public Map getTargetForeignKeyToSourceKeys() {
        return getTargetForeignKeysToSourceKeys();
    }

    /**
     * INTERNAL:
     * Return whether the mapping has any inverse constraint dependencies,
     * such as foreign keys and join tables.
     */
    public boolean hasInverseConstraintDependency() {
        return true;
    }

    /**
     * INTERNAL:
     * Initialize the mapping.
     */
    public void initialize(AbstractSession session) throws DescriptorException {
        super.initialize(session);

        if (!isSourceKeySpecified()) {
            // sourceKeyFields will be empty when #setTargetForeignKeyFieldName() is used
            setSourceKeyFields(org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(getDescriptor().getPrimaryKeyFields()));
        }
        initializeTargetForeignKeysToSourceKeys();

        if (shouldInitializeSelectionCriteria()) {
            setSelectionCriteria(buildDefaultSelectionCriteria());
        }

        initializeDeleteAllQuery();
    }

    /**
     * Initialize the delete all query.
     * This query is used to delete the collection of objects from the
     * database.
     */
    protected void initializeDeleteAllQuery() {
        ((DeleteAllQuery)getDeleteAllQuery()).setReferenceClass(getReferenceClass());
        if (!hasCustomDeleteAllQuery()) {
            // the selection criteria are re-used by the delete all query
            if (getSelectionCriteria() == null) {
                getDeleteAllQuery().setSelectionCriteria(buildDefaultSelectionCriteria());
            } else {
                getDeleteAllQuery().setSelectionCriteria(getSelectionCriteria());
            }
        }
    }

    /**
     * Verify, munge, and hash the target foreign keys and source keys.
     */
    protected void initializeTargetForeignKeysToSourceKeys() throws DescriptorException {
        if (getTargetForeignKeyFields().isEmpty()) {
            if (shouldInitializeSelectionCriteria()) {
                throw DescriptorException.noTargetForeignKeysSpecified(this);
            } else {
                // if they have specified selection criteria, the keys do not need to be specified
                return;
            }
        }

        if (getTargetForeignKeyFields().size() != getSourceKeyFields().size()) {
            throw DescriptorException.targetForeignKeysSizeMismatch(this);
        }

        for (int index = 0; index < getTargetForeignKeyFields().size(); index++) {
            DatabaseField field = getReferenceDescriptor().buildField(getTargetForeignKeyFields().get(index));
            getTargetForeignKeyFields().set(index, field);
        }

        for (int index = 0; index < getSourceKeyFields().size(); index++) {
            DatabaseField field = getDescriptor().buildField(getSourceKeyFields().get(index));
            getSourceKeyFields().set(index, field);
        }

        Iterator<DatabaseField> targetForeignKeys = getTargetForeignKeyFields().iterator();
        Iterator<DatabaseField> sourceKeys = getSourceKeyFields().iterator();
        while (targetForeignKeys.hasNext()) {
            DatabaseField targetForeignKey = targetForeignKeys.next();
            DatabaseField sourcePrimaryKey = sourceKeys.next();
            getTargetForeignKeysToSourceKeys().put(targetForeignKey, sourcePrimaryKey);
            getSourceKeysToTargetForeignKeys().put(sourcePrimaryKey, targetForeignKey);
        }
    }

    /**
     * INTERNAL:
     */
    public boolean isOneToManyMapping() {
        return true;
    }

    /**
     * Return whether the source key is specified.
     * It will be empty when #setTargetForeignKeyFieldName(String) is used.
     */
    protected boolean isSourceKeySpecified() {
        return !getSourceKeyFields().isEmpty();
    }

    /**
     * Return whether the reference objects must be deleted
     * one by one, as opposed to with a single DELETE statement.
     */
    protected boolean mustDeleteReferenceObjectsOneByOne() {
        ClassDescriptor referenceDescriptor = this.getReferenceDescriptor();
        return referenceDescriptor.hasDependencyOnParts() || referenceDescriptor.usesOptimisticLocking() || (referenceDescriptor.hasInheritance() && referenceDescriptor.getInheritancePolicy().shouldReadSubclasses()) || referenceDescriptor.hasMultipleTables();
    }

    /**
     * INTERNAL:
     * Insert the reference objects.
     */
    public void postInsert(WriteObjectQuery query) throws DatabaseException, OptimisticLockException {
        if (!shouldObjectModifyCascadeToParts(query)) {
            return;
        }

        // only cascade dependents in UOW
        if (query.shouldCascadeOnlyDependentParts()) {
            return;
        }

        Object objects = getRealCollectionAttributeValueFromObject(query.getObject(), query.getSession());

        // insert each object one by one
        ContainerPolicy cp = getContainerPolicy();
        for (Object iter = cp.iteratorFor(objects); cp.hasNext(iter);) {
            Object object = cp.next(iter, query.getSession());
            if (isPrivateOwned()) {
                // no need to set changeSet as insert is a straight copy
                InsertObjectQuery insertQuery = new InsertObjectQuery();
                insertQuery.setIsExecutionClone(true);
                insertQuery.setObject(object);
                insertQuery.setCascadePolicy(query.getCascadePolicy());
                query.getSession().executeQuery(insertQuery);
            } else {
                // This will happen in a cascaded query.
                // This is done only for persistence by reachability and is not required if the targets are in the queue anyway
                // Avoid cycles by checking commit manager, this is allowed because there is no dependency.
                if (!query.getSession().getCommitManager().isCommitInPreModify(object)) {
                    WriteObjectQuery writeQuery = new WriteObjectQuery();
                    writeQuery.setIsExecutionClone(true);
                    writeQuery.setObject(object);
                    writeQuery.setCascadePolicy(query.getCascadePolicy());
                    query.getSession().executeQuery(writeQuery);
                }
            }
        }
    }

    /**
     * INTERNAL:
     * Update the reference objects.
     */
    public void postUpdate(WriteObjectQuery query) throws DatabaseException, OptimisticLockException {
        if (!shouldObjectModifyCascadeToParts(query)) {
            return;
        }

        // if the target objects are not instantiated, they could not have been changed....
        if (!isAttributeValueInstantiatedOrChanged(query.getObject())) {
            return;
        }

        // manage objects added and removed from the collection
        Object objectsInMemory = getRealCollectionAttributeValueFromObject(query.getObject(), query.getSession());
        Object objectsInDB = readPrivateOwnedForObject(query);

        compareObjectsAndWrite(objectsInDB, objectsInMemory, query);
    }

    /**
     * INTERNAL:
     * Delete the reference objects.
     */
    public void preDelete(DeleteObjectQuery query) throws DatabaseException, OptimisticLockException {
        if (!shouldObjectModifyCascadeToParts(query)) {
            return;
        }

        Object objects = getRealCollectionAttributeValueFromObject(query.getObject(), query.getSession());

        ContainerPolicy cp = getContainerPolicy();

        // if privately-owned parts have their privately-owned sub-parts, delete them one by one;
        // else delete everything in one shot
        if (mustDeleteReferenceObjectsOneByOne()) {
            for (Object iter = cp.iteratorFor(objects); cp.hasNext(iter);) {
                DeleteObjectQuery deleteQuery = new DeleteObjectQuery();
                deleteQuery.setIsExecutionClone(true);
                deleteQuery.setObject(cp.next(iter, query.getSession()));
                deleteQuery.setCascadePolicy(query.getCascadePolicy());
                query.getSession().executeQuery(deleteQuery);
            }
            if (!query.getSession().isUnitOfWork()) {
                // This deletes any objects on the database, as the collection in memory may have been changed.
                // This is not required for unit of work, as the update would have already deleted these objects,
                // and the backup copy will include the same objects causing double deletes.
                deleteReferenceObjectsLeftOnDatabase(query);
            }
        } else {
            deleteAll(query);
        }
    }
    
    /**
     * Prepare a cascade locking policy.
     */
    public void prepareCascadeLockingPolicy() {
        CascadeLockingPolicy policy = new CascadeLockingPolicy(getDescriptor(), getReferenceDescriptor());
        policy.setQueryKeyFields(getSourceKeysToTargetForeignKeys());
        getReferenceDescriptor().addCascadeLockingPolicy(policy);
    }

    /**
     * PUBLIC:
     * Set the SQL string used by the mapping to delete the target objects.
     * This allows the developer to override the SQL
     * generated by TopLink with a custom SQL statement or procedure call.
     * The arguments are
     * translated from the fields of the source row, by replacing the field names
     * marked by '#' with the values for those fields at execution time.
     * A one-to-many mapping will only use this delete all optimization if the target objects
     * can be deleted in a single SQL call. This is possible when the target objects
     * are in a single table, do not using locking, do not contain other privately-owned
     * parts, do not read subclasses, etc.
     * <p>
     * Example: "delete from PHONE where OWNER_ID = #EMPLOYEE_ID"
     */
    public void setDeleteAllSQLString(String sqlString) {
        DeleteAllQuery query = new DeleteAllQuery();
        query.setSQLString(sqlString);
        setCustomDeleteAllQuery(query);
    }

    /**
     * INTERNAL:
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
     * Set the source key fields.
     */
    public void setSourceKeyFields(Vector<DatabaseField> sourceKeyFields) {
        this.sourceKeyFields = sourceKeyFields;
    }

    /**
     * PUBLIC:
     * Define the target foreign key relationship in the one-to-many mapping.
     * This method can be used when the foreign and primary keys
     * have only a single field each.
     * (Use #addTargetForeignKeyFieldName(String, String)
     * for "composite" keys.)
     * Only the target foreign key field name is specified and the source
     * (primary) key field is
     * assumed to be the primary key of the source object.
     * Because the target object's table must store a foreign key to the source table,
     * the target object must map that foreign key, this is normally done through a
     * one-to-one mapping back-reference. Other options include:
     * <ul>
     * <li> use a DirectToFieldMapping and maintain the
     * foreign key fields directly in the target
     * <li> use a ManyToManyMapping
     * <li> use an AggregateCollectionMapping
     * </ul>
     * @see DirectToFieldMapping
     * @see ManyToManyMapping
     * @see AggregateCollectionMapping
     */
    public void setTargetForeignKeyFieldName(String targetForeignKeyFieldName) {
        getTargetForeignKeyFields().addElement(new DatabaseField(targetForeignKeyFieldName));
    }

    /**
     * PUBLIC:
     * Define the target foreign key relationship in the one-to-many mapping.
     * This method is used for composite target foreign key relationships.
     * That is, the target object's table has multiple foreign key fields to
     * the source object's (typically primary) key fields.
     * Both the target foreign key field names and the corresponding source primary
     * key field names must be specified.
     */
    public void setTargetForeignKeyFieldNames(String[] targetForeignKeyFieldNames, String[] sourceKeyFieldNames) {
        if (targetForeignKeyFieldNames.length != sourceKeyFieldNames.length) {
            throw DescriptorException.targetForeignKeysSizeMismatch(this);
        }
        for (int i = 0; i < targetForeignKeyFieldNames.length; i++) {
            addTargetForeignKeyFieldName(targetForeignKeyFieldNames[i], sourceKeyFieldNames[i]);
        }
    }

    /**
     * INTERNAL:
     * Set the target key field names associated with the mapping.
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
     * Set the target fields.
     */
    public void setTargetForeignKeyFields(Vector<DatabaseField> targetForeignKeyFields) {
        this.targetForeignKeyFields = targetForeignKeyFields;
    }

    /**
     * INTERNAL:
     * Set the target fields.
     */
    protected void setTargetForeignKeysToSourceKeys(Map<DatabaseField, DatabaseField> targetForeignKeysToSourceKeys) {
        this.targetForeignKeysToSourceKeys = targetForeignKeysToSourceKeys;
    }

    /**
     * Return whether any process leading to object modification
     * should also affect its parts.
     * Used by write, insert, update, and delete.
     */
    protected boolean shouldObjectModifyCascadeToParts(ObjectLevelModifyQuery query) {
        if (isReadOnly()) {
            return false;
        }

        if (isPrivateOwned()) {
            return true;
        }

        return query.shouldCascadeAllParts();
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
     * Return if this mapping support joining.
     */
    public boolean isJoiningSupported() {
        return true;
    }

    /**
     * INTERNAL:
     * Used to verify whether the specified object is deleted or not.
     */
    public boolean verifyDelete(Object object, AbstractSession session) throws DatabaseException {
        if (this.isPrivateOwned()) {
            Object objects = this.getRealCollectionAttributeValueFromObject(object, session);

            ContainerPolicy containerPolicy = getContainerPolicy();
            for (Object iter = containerPolicy.iteratorFor(objects); containerPolicy.hasNext(iter);) {
                if (!session.verifyDelete(containerPolicy.next(iter, session))) {
                    return false;
                }
            }
        }
        return true;
    }
}
