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
package org.eclipse.persistence.eis.mappings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.eclipse.persistence.eis.EISDescriptor;
import org.eclipse.persistence.eis.EISException;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.OptimisticLockException;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.indirection.ValueHolder;
import org.eclipse.persistence.internal.descriptors.ObjectBuilder;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.indirection.EISOneToManyQueryBasedValueHolder;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.internal.oxm.XPathEngine;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.queries.JoinedAttributeManager;
import org.eclipse.persistence.internal.sessions.*;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.oxm.record.DOMRecord;
import org.eclipse.persistence.oxm.record.XMLRecord;
import org.eclipse.persistence.queries.*;
import org.w3c.dom.Element;

/**
 * <p>An EIS one-to-many mapping is a reference mapping that represents the relationship between 
 * a single source object and a collection of mapped persistent Java objects.  The source object usually 
 * contains a foreign key (pointer) to the target objects (key on source); alternatively, the target 
 * objects may contain a foreign key to the source object (key on target).  Because both the source 
 * and target objects use interactions, they must all be configured as root object types.  
 * 
 * <p><table border="1">
 * <tr>
 * <th id="c1" align="left">Record Type</th>
 * <th id="c2" align="left">Description</th>
 * </tr>
 * <tr>
 * <td headers="c1">Indexed</td>
 * <td headers="c2">Ordered collection of record elements.  The indexed record EIS format 
 * enables Java class attribute values to be retreived by position or index.</td>
 * </tr>
 * <tr>
 * <td headers="c1">Mapped</td>
 * <td headers="c2">Key-value map based representation of record elements.  The mapped record
 * EIS format enables Java class attribute values to be retreived by an object key.</td>
 * </tr>
 * <tr>
 * <td headers="c1">XML</td>
 * <td headers="c2">Record/Map representation of an XML DOM element.</td>
 * </tr>
 * </table>
 * 
 * @see org.eclipse.persistence.eis.EISDescriptor#useIndexedRecordFormat
 * @see org.eclipse.persistence.eis.EISDescriptor#useMappedRecordFormat
 * @see org.eclipse.persistence.eis.EISDescriptor#useXMLRecordFormat
 * 
 * @since Oracle TopLink 10<i>g</i> Release 2 (10.1.3)
 */
public class EISOneToManyMapping extends CollectionMapping implements EISMapping {

    /** Keeps track if any of the fields are foreign keys. */
    protected boolean isForeignKeyRelationship;

    /** The target foreign key fields that reference the sourceKeyFields. */
    protected transient List<DatabaseField> targetForeignKeyFields;

    /** The (typically primary) source key fields that are referenced by the targetForeignKeyFields. */
    protected transient List<DatabaseField> sourceForeignKeyFields;

    /** This maps the source foreign key fields to the corresponding (primary) target key fields. */
    protected transient Map<DatabaseField, DatabaseField> sourceForeignKeysToTargetKeys;

    /** The grouping-element field. */
    protected DatabaseField foreignKeyGroupingElement;

    public EISOneToManyMapping() {
        this.isForeignKeyRelationship = false;
        this.sourceForeignKeyFields = new ArrayList(1);
        this.targetForeignKeyFields = new ArrayList(1);
        this.sourceForeignKeysToTargetKeys = new HashMap(2);
        this.deleteAllQuery = new DeleteAllQuery();
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean isEISMapping() {
        return true;
    }

    /**
     * PUBLIC:
     * Define the source foreign key relationship in the one-to-many mapping.
     * This method is used for composite source foreign key relationships.
     * That is, the source object's table has multiple foreign key fields
     * that are references to
     * the target object's (typically primary) key fields.
     * Both the source foreign key field name and the corresponding
     * target primary key field name must be specified.
     */
    @Override
    public void addForeignKeyField(DatabaseField sourceForeignKeyField, DatabaseField targetKeyField) {
        this.getSourceForeignKeyFields().add(sourceForeignKeyField);
        this.getTargetForeignKeyFields().add(targetKeyField);

        this.setIsForeignKeyRelationship(true);
    }

    /**
     * PUBLIC:
     * Define the source foreign key relationship in the one-to-many mapping.
     * This method is used for composite source foreign key relationships.
     * That is, the source object's table has multiple foreign key fields
     * that are references to
     * the target object's (typically primary) key fields.
     * Both the source foreign key field name and the corresponding
     * target primary key field name must be specified.
     */
    public void addForeignKeyFieldName(String sourceForeignKeyFieldName, String targetKeyFieldName) {
        this.addForeignKeyField(new DatabaseField(sourceForeignKeyFieldName), new DatabaseField(targetKeyFieldName));
    }

    /**
     * INTERNAL:
     * Return if the 1-M mapping has a foreign key dependency to its target.
     * This is true if any of the foreign key fields are true foreign keys,
     * i.e. populated on write from the targets primary key.
     */
    public boolean isForeignKeyRelationship() {
        return isForeignKeyRelationship;
    }

    /**
     * INTERNAL:
     * Set if the 1-M mapping has a foreign key dependency to its target.
     * This is true if any of the foreign key fields are true foreign keys,
     * i.e. populated on write from the targets primary key.
     */
    public void setIsForeignKeyRelationship(boolean isForeignKeyRelationship) {
        this.isForeignKeyRelationship = isForeignKeyRelationship;
    }

    /**
     * Get the grouping element field on the mapping.
     * This is an optional setting.
     */
    public DatabaseField getForeignKeyGroupingElement() {
        return this.foreignKeyGroupingElement;
    }

    /**
     * Set the grouping element field on the mapping.
     * This is an optional setting; however it is a required setting when
     * there are more than one foreign keys specified
     */
    public void setForeignKeyGroupingElement(String name) {
        setForeignKeyGroupingElement(new DatabaseField(name));
    }

    public boolean hasCustomDeleteAllQuery() {
        return hasCustomDeleteAllQuery;
    }

    public ModifyQuery getDeleteAllQuery() {
        if (deleteAllQuery == null) {
            deleteAllQuery = new DataModifyQuery();
        }
        return deleteAllQuery;
    }

    /**
     * PUBLIC:
     * The default delete all call for this mapping can be overridden by specifying the new call.
     * This call is responsible for doing the deletion required by the mapping,
     * such as optimized delete all of target objects for 1-M.
     */
    public void setDeleteAllCall(Call call) {
        DeleteAllQuery deleteAllQuery = new DeleteAllQuery();
        deleteAllQuery.setCall(call);
        setDeleteAllQuery(deleteAllQuery);
        setHasCustomDeleteAllQuery(true);
    }

    /**
     * Set if the grouping element field on the mapping.
     * This is an optional setting; however it is a required setting when
     * there are more than one foreign keys specified.
     */
    public void setForeignKeyGroupingElement(DatabaseField field) {
        this.foreignKeyGroupingElement = field;
    }

    /**
     * INTERNAL:
     * Return the source foreign key fields.
     */
    public List<DatabaseField> getSourceForeignKeyFields() {
        return sourceForeignKeyFields;
    }

    /**
     * INTERNAL:
     * Sets the source foreign key fields.
     */
    public void setSourceForeignKeyFields(List<DatabaseField> fields) {
        sourceForeignKeyFields = fields;
        if ((fields != null) && (fields.size() > 0)) {
            this.setIsForeignKeyRelationship(true);
        }
    }

    /**
     * INTERNAL:
     * Return the source foreign key fields.
     */
    public List<DatabaseField> getTargetForeignKeyFields() {
        return targetForeignKeyFields;
    }

    /**
     * INTERNAL:
     * Sets the target foreign key fields.
     */
    public void setTargetForeignKeyFields(List<DatabaseField> fields) {
        targetForeignKeyFields = fields;
    }

    /**
    * INTERNAL:
    * Sets the target foreign key fields.
    */
    public Map<DatabaseField, DatabaseField> getSourceForeignKeysToTargetKeys() {
        return sourceForeignKeysToTargetKeys;
    }

    /**
     * INTERNAL:
     * Set the source keys to target keys fields association.
     */
    public void setSourceForeignKeysToTargetKeys(Map<DatabaseField, DatabaseField> sourceToTargetKeyFields) {
        this.sourceForeignKeysToTargetKeys = sourceToTargetKeyFields;
        if ((sourceToTargetKeyFields != null) && (sourceToTargetKeyFields.keySet() != null) && (sourceToTargetKeyFields.keySet().size() > 0)) {
            this.setIsForeignKeyRelationship(true);
        }
    }

    /**
     * INTERNAL:
     * Return whether the mapping has any inverse constraint dependencies,
     * such as foreign keys.
     */
    @Override
    public boolean hasInverseConstraintDependency() {
        return true;
    }

    /**
     * INTERNAL:
     * Initialize the mapping.
     */
    @Override
    public void initialize(AbstractSession session) throws DescriptorException {
        super.initialize(session);

        if ((this.getForeignKeyGroupingElement() == null) && (this.getSourceForeignKeysToTargetKeys().size() > 1)) {
            throw EISException.groupingElementRequired();
        }

        if (this.getForeignKeyGroupingElement() != null) {
            DatabaseField field = this.getDescriptor().buildField(this.getForeignKeyGroupingElement());
            setForeignKeyGroupingElement(field);
        }

        this.initializeSourceForeignKeysToTargetKeys();
        
        if (shouldInitializeSelectionCriteria()) {
            initializeSelectionCriteria(session);
        }

        this.initializeDeleteAllQuery();
    }

    /**
     * INTERNAL:
     * Selection criteria is created with source foreign keys and target keys.
     * This criteria is then used to read target records from the table.
     *
     * CR#3922 - This method is almost the same as buildSelectionCriteria() the difference
     * is that getSelectionCriteria() is called
     */
    protected void initializeSelectionCriteria(AbstractSession session) {
        if (this.getSourceForeignKeysToTargetKeys().isEmpty()) {
            throw DescriptorException.noForeignKeysAreSpecified(this);
        }

        Expression criteria;
        Expression builder = new ExpressionBuilder();
        Iterator keyIterator = getSourceForeignKeysToTargetKeys().keySet().iterator();
        while (keyIterator.hasNext()) {
            DatabaseField foreignKey = (DatabaseField)keyIterator.next();
            DatabaseField targetKey = getSourceForeignKeysToTargetKeys().get(foreignKey);

            Expression expression = builder.getField(targetKey).equal(builder.getParameter(foreignKey));
            criteria = expression.and(getSelectionCriteria());
            setSelectionCriteria(criteria);
        }
    }

    protected void initializeSourceForeignKeysToTargetKeys() throws DescriptorException {
        // Since we require a custom selection query, these keys are optional.
        if (getSourceForeignKeyFields().size() != getTargetForeignKeyFields().size()) {
            throw DescriptorException.sizeMismatchOfForeignKeys(this);
        }

        for (int i = 0; i < getTargetForeignKeyFields().size(); i++) {
            DatabaseField field = getReferenceDescriptor().buildField(getTargetForeignKeyFields().get(i));
            getTargetForeignKeyFields().set(i, field);
        }

        for (int i = 0; i < getSourceForeignKeyFields().size(); i++) {
            DatabaseField field = getDescriptor().buildField(getSourceForeignKeyFields().get(i));
            getSourceForeignKeyFields().set(i, field);
            getSourceForeignKeysToTargetKeys().put(field, getTargetForeignKeyFields().get(i));
        }
    }

    /**
     * Initialize the delete all query.
     * This query is used to delete the collection of objects from the
     * database.
     */
    protected void initializeDeleteAllQuery() {
        ((DeleteAllQuery)this.getDeleteAllQuery()).setReferenceClass(this.getReferenceClass());
        if (!this.hasCustomDeleteAllQuery()) {
            // the selection criteria are re-used by the delete all query
            this.getDeleteAllQuery().setSelectionCriteria(this.getSelectionCriteria());
        }
    }
    
    /**
     * Fix field names for XML data descriptors.
     * Since fields are fixed to use text() by default in descriptor, ensure the correct non text field is used here.
     */
    @Override
    public void preInitialize(AbstractSession session) {
        super.preInitialize(session);
        if (((EISDescriptor)this.descriptor).isXMLFormat()) {
            if ((this.foreignKeyGroupingElement != null) && !(this.foreignKeyGroupingElement instanceof XMLField)) {
                XMLField newField = new XMLField(this.foreignKeyGroupingElement.getName());
                this.foreignKeyGroupingElement = newField;
            }
        }
    }

    /**
     * Return whether any process leading to object modification
     * should also affect its parts.
     * Used by write, insert, update, and delete.
     */
    @Override
    protected boolean shouldObjectModifyCascadeToParts(ObjectLevelModifyQuery query) {
        if (isForeignKeyRelationship()) {
            return super.shouldObjectModifyCascadeToParts(query);
        } else {
            if (this.isReadOnly()) {
                return false;
            }

            if (this.isPrivateOwned()) {
                return true;
            }

            return query.shouldCascadeAllParts();
        }
    }

    /**
     * INTERNAL:
     * Used to verify whether the specified object is deleted or not.
     */
    @Override
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

    /**
     * INTERNAL:
     * Insert the reference objects.
     */
    @Override
    public void postInsert(WriteObjectQuery query) throws DatabaseException, OptimisticLockException {
        if (isForeignKeyRelationship()) {
            return;
        }

        if (!this.shouldObjectModifyCascadeToParts(query)) {
            return;
        }

        // only cascade dependents in UOW
        if (query.shouldCascadeOnlyDependentParts()) {
            return;
        }

        Object objects = this.getRealCollectionAttributeValueFromObject(query.getObject(), query.getSession());

        // insert each object one by one
        ContainerPolicy cp = this.getContainerPolicy();
        for (Object iter = cp.iteratorFor(objects); cp.hasNext(iter);) {
            Object object = cp.next(iter, query.getSession());
            if (this.isPrivateOwned()) {
                // no need to set changeSet as insert is a straight copy
                InsertObjectQuery insertQuery = new InsertObjectQuery();
                insertQuery.setIsExecutionClone(true);
                insertQuery.setObject(object);
                insertQuery.setCascadePolicy(query.getCascadePolicy());
                query.getSession().executeQuery(insertQuery);
            } else {
                // This will happen in a or cascaded query.
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
    @Override
    public void postUpdate(WriteObjectQuery query) throws DatabaseException, OptimisticLockException {
        if (isForeignKeyRelationship()) {
            return;
        }

        if (!shouldObjectModifyCascadeToParts(query)) {
            return;
        }

        // if the target objects are not instantiated, they could not have been changed....
        if (!isAttributeValueInstantiatedOrChanged(query.getObject())) {
            return;
        }

        if (query.getObjectChangeSet() != null) {
            // UnitOfWork
            writeChanges(query.getObjectChangeSet(), query);
        } else {
            // OLD COMMIT    
            compareObjectsAndWrite(query);
        }
    }

    /**
     * INTERNAL:
     * Delete the reference objects.
     */
    @Override
    public void postDelete(DeleteObjectQuery query) throws DatabaseException, OptimisticLockException {
        if (!isForeignKeyRelationship()) {
            return;
        }

        if (!this.shouldObjectModifyCascadeToParts(query)) {
            return;
        }
        Object referenceObjects = this.getRealCollectionAttributeValueFromObject(query.getObject(), query.getSession());

        // if we have a custom delete all query, use it;
        // otherwise, delete the reference objects one by one
        if (this.hasCustomDeleteAllQuery()) {
            this.deleteAll(query, referenceObjects);
        } else {
            ContainerPolicy cp = this.getContainerPolicy();
            for (Object iter = cp.iteratorFor(referenceObjects); cp.hasNext(iter);) {
                DeleteObjectQuery deleteQuery = new DeleteObjectQuery();
                deleteQuery.setIsExecutionClone(true);
                deleteQuery.setObject(cp.next(iter, query.getSession()));
                deleteQuery.setCascadePolicy(query.getCascadePolicy());
                query.getSession().executeQuery(deleteQuery);
            }
            if (!query.getSession().isUnitOfWork()) {
                // This deletes any objects on the database, as the collection in memory may have been changed.
                // This is not required for unit of work, as the update would have already deleted these objects,
                // and the backup copy will include the same objects, causing double deletes.
                this.deleteReferenceObjectsLeftOnDatabase(query);
            }
        }
    }

    /**
     * INTERNAL:
     * Delete the reference objects.
     */
    @Override
    public void preDelete(DeleteObjectQuery query) throws DatabaseException, OptimisticLockException {
        if (isForeignKeyRelationship()) {
            return;
        }

        if (!this.shouldObjectModifyCascadeToParts(query)) {
            return;
        }

        Object objects = this.getRealCollectionAttributeValueFromObject(query.getObject(), query.getSession());
        ContainerPolicy cp = this.getContainerPolicy();

        // if privately-owned parts have their privately-owned sub-parts, delete them one by one;
        // else delete everything in one shot
        if (this.mustDeleteReferenceObjectsOneByOne()) {
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
                this.deleteReferenceObjectsLeftOnDatabase(query);
            }
        } else {
            this.deleteAll(query);
        }
    }

    /**
    * INTERNAL:
    * Insert privately owned parts
    */
    @Override
    public void preInsert(WriteObjectQuery query) throws DatabaseException, OptimisticLockException {
        if (!this.isForeignKeyRelationship()) {
            return;
        }

        if (!this.shouldObjectModifyCascadeToParts(query)) {
            return;
        }

        // only cascade dependents in UOW
        if (query.shouldCascadeOnlyDependentParts()) {
            return;
        }

        Object objects = this.getRealCollectionAttributeValueFromObject(query.getObject(), query.getSession());

        // insert each object one by one
        ContainerPolicy cp = this.getContainerPolicy();
        for (Object iter = cp.iteratorFor(objects); cp.hasNext(iter);) {
            Object object = cp.next(iter, query.getSession());
            if (this.isPrivateOwned()) {
                // no need to set changeset here as insert is just a copy of the object anyway
                InsertObjectQuery insertQuery = new InsertObjectQuery();
                insertQuery.setIsExecutionClone(true);
                insertQuery.setObject(object);
                insertQuery.setCascadePolicy(query.getCascadePolicy());
                query.getSession().executeQuery(insertQuery);
            } else {
                // This will happen in a unit of work or cascaded query.
                // This is done only for persistence by reachability and is not required if the targets are in the queue anyway
                // Avoid cycles by checking commit manager, this is allowed because there is no dependency.
                if (!query.getSession().getCommitManager().isCommitInPreModify(object)) {
                    WriteObjectQuery writeQuery = new WriteObjectQuery();
                    writeQuery.setIsExecutionClone(true);
                    if (query.getSession().isUnitOfWork()) {
                        UnitOfWorkChangeSet uowChangeSet = (UnitOfWorkChangeSet)((UnitOfWorkImpl)query.getSession()).getUnitOfWorkChangeSet();
                        if (uowChangeSet != null) {
                            writeQuery.setObjectChangeSet((ObjectChangeSet)uowChangeSet.getObjectChangeSetForClone(object));
                        }
                    }
                    writeQuery.setObject(object);
                    writeQuery.setCascadePolicy(query.getCascadePolicy());
                    query.getSession().executeQuery(writeQuery);
                }
            }
        }
    }

    /**
     * INTERNAL:
     * Update the privately owned parts.
     */
    @Override
    public void preUpdate(WriteObjectQuery query) throws DatabaseException, OptimisticLockException {
        if (!isForeignKeyRelationship()) {
            return;
        }

        if (!shouldObjectModifyCascadeToParts(query)) {
            return;
        }

        // if the target objects are not instantiated, they could not have been changed....
        if (!isAttributeValueInstantiatedOrChanged(query.getObject())) {
            return;
        }

        if (query.getObjectChangeSet() != null) {
            // UnitOfWork
            writeChanges(query.getObjectChangeSet(), query);
        } else {
            // OLD COMMIT    
            compareObjectsAndWrite(query);
        }
    }

    /**
     * INTERNAL:
     * Build and return a new element based on the change set.
     */
    public Object buildAddedElementFromChangeSet(Object changeSet, MergeManager mergeManager, AbstractSession targetSession) {
        ObjectChangeSet objectChangeSet = (ObjectChangeSet)changeSet;

        if (this.shouldMergeCascadeParts(mergeManager)) {
            Object targetElement = null;
            if (mergeManager.shouldMergeChangesIntoDistributedCache()) {
                targetElement = objectChangeSet.getTargetVersionOfSourceObject(mergeManager, mergeManager.getSession(), true);
            } else {
                targetElement = objectChangeSet.getUnitOfWorkClone();
            }
            mergeManager.mergeChanges(targetElement, objectChangeSet, targetSession);
        }

        return this.buildElementFromChangeSet(changeSet, mergeManager, targetSession);
    }

    /**
     * INTERNAL:
     * Build and return a change set for the specified element.
     */
    public Object buildChangeSet(Object element, ObjectChangeSet owner, AbstractSession session) {
        ObjectBuilder objectBuilder = session.getDescriptor(element).getObjectBuilder();
        return objectBuilder.createObjectChangeSet(element, (UnitOfWorkChangeSet)owner.getUOWChangeSet(), session);
    }

    /**
     * Build and return a new element based on the change set.
     */
    protected Object buildElementFromChangeSet(Object changeSet, MergeManager mergeManager, AbstractSession targetSession) {
        return ((ObjectChangeSet)changeSet).getTargetVersionOfSourceObject(mergeManager, targetSession);
    }

    /**
     * INTERNAL:
     * Build and return a new element based on the specified element.
     */
    public Object buildElementFromElement(Object element, MergeManager mergeManager, AbstractSession targetSession) {
        if (this.shouldMergeCascadeParts(mergeManager)) {
            ObjectChangeSet objectChangeSet = null;
            if (mergeManager.getSession().isUnitOfWork()) {
                UnitOfWorkChangeSet uowChangeSet = (UnitOfWorkChangeSet)((UnitOfWorkImpl)mergeManager.getSession()).getUnitOfWorkChangeSet();
                if (uowChangeSet != null) {
                    objectChangeSet = (ObjectChangeSet)uowChangeSet.getObjectChangeSetForClone(element);
                }
            }
            Object mergeElement = mergeManager.getObjectToMerge(element, referenceDescriptor, targetSession);
            mergeManager.mergeChanges(mergeElement, objectChangeSet, targetSession);
        }

        return mergeManager.getTargetVersionOfSourceObject(element, referenceDescriptor, targetSession);

    }

    /**
     * INTERNAL:
     * Build and return a new element based on the change set.
     */
    public Object buildRemovedElementFromChangeSet(Object changeSet, MergeManager mergeManager, AbstractSession targetSession) {
        ObjectChangeSet objectChangeSet = (ObjectChangeSet)changeSet;

        if (!mergeManager.shouldMergeChangesIntoDistributedCache()) {
            mergeManager.registerRemovedNewObjectIfRequired(objectChangeSet.getUnitOfWorkClone());
        }

        return this.buildElementFromChangeSet(changeSet, mergeManager, targetSession);
    }

    /**
     * INTERNAL:
     * Clone the appropriate attributes.
     */
    @Override
    public Object clone() {
        EISOneToManyMapping clone = (EISOneToManyMapping)super.clone();
        clone.setSourceForeignKeysToTargetKeys((Map)((HashMap)getSourceForeignKeysToTargetKeys()).clone());
        return clone;
    }

    /**
     * Return all the fields mapped by the mapping.
     */
    @Override
    protected Vector collectFields() {
        if (isForeignKeyRelationship()) {
            if (this.getForeignKeyGroupingElement() != null) {
                Vector fields = new Vector(1);
                fields.addElement(this.getForeignKeyGroupingElement());
                return fields;
            } else {
                return NO_FIELDS;
            }
        } else {
            return NO_FIELDS;
        }
    }

    /**
     * INTERNAL:
     * Compare the non-null elements and return true if they are alike.
     */
    public boolean compareElements(Object element1, Object element2, AbstractSession session) {
        if (!isForeignKeyRelationship()) {
            return false;
        }

        Object primaryKey1 = getReferenceDescriptor().getObjectBuilder().extractPrimaryKeyFromObject(element1, session);
        Object primaryKey2 = getReferenceDescriptor().getObjectBuilder().extractPrimaryKeyFromObject(element2, session);

        if (!primaryKey1.equals(primaryKey2)) {
            return false;
        }

        if (this.isPrivateOwned()) {
            return session.compareObjects(element1, element2);
        } else {
            return true;
        }
    }

    /**
     * INTERNAL:
     * Return whether the element's user-defined Map key has changed
     * since it was cloned from the original version.
     * Object elements can change their keys without detection.
     * Get the original object and compare keys.
     */
    public boolean mapKeyHasChanged(Object element, AbstractSession session) {
        //CR 4172 compare keys will now get backup if required
        return !this.getContainerPolicy().compareKeys(element, session);
    }

    /**
     * INTERNAL:
     * Compare the non-null elements and return true if they are alike.
     * Here we use object identity.
     */
    public boolean compareElementsForChange(Object element1, Object element2, AbstractSession session) {
        return element1 == element2;
    }

    /**
     * INTERNAL:
     * Compare the changes between two collections. Element comparisons are
     * made using identity and, when appropriate, the value of the element's key
     * for the Map container.
     */
    @Override
    public ChangeRecord compareForChange(Object clone, Object backup, ObjectChangeSet owner, AbstractSession session) {
        if (isForeignKeyRelationship()) {
            if ((this.getAttributeValueFromObject(clone) != null) && (!this.isAttributeValueInstantiatedOrChanged(clone))) {
                return null;// never instantiated - no changes to report
            }
            return (new EISOneToManyMappingHelper(this)).compareForChange(clone, backup, owner, session);
        } else {
            return super.compareForChange(clone, backup, owner, session);
        }
    }

    /**
     * INTERNAL:
     * Compare the attributes belonging to this mapping for the objects.
     */
    @Override
    public boolean compareObjects(Object object1, Object object2, AbstractSession session) {
        if (isForeignKeyRelationship()) {
            return (new EISOneToManyMappingHelper(this)).compareObjects(object1, object2, session);
        }
        return super.compareObjects(object1, object2, session);
    }

    /**
     * ADVANCED:
     * This method is used to have an object add to a collection once the changeSet is applied
     * The referenceKey parameter should only be used for direct Maps.
     */
    @Override
    public void simpleAddToCollectionChangeRecord(Object referenceKey, Object changeSetToAdd, ObjectChangeSet changeSet, AbstractSession session) {
        (new EISOneToManyMappingHelper(this)).simpleAddToCollectionChangeRecord(referenceKey, changeSetToAdd, changeSet, session);
    }

    /**
     * ADVANCED:
     * This method is used to have an object removed from a collection once the changeSet is applied
     * The referenceKey parameter should only be used for direct Maps.
     */
    @Override
    public void simpleRemoveFromCollectionChangeRecord(Object referenceKey, Object changeSetToRemove, ObjectChangeSet changeSet, AbstractSession session) {
        (new EISOneToManyMappingHelper(this)).simpleRemoveFromCollectionChangeRecord(referenceKey, changeSetToRemove, changeSet, session);
    }

    /**
     * Delete all the reference objects.
     */
    protected void deleteAll(DeleteObjectQuery query, Object referenceObjects) throws DatabaseException {
        ((DeleteAllQuery)this.getDeleteAllQuery()).executeDeleteAll(query.getSession().getSessionForClass(this.getReferenceClass()), query.getTranslationRow(), this.getContainerPolicy().vectorFor(referenceObjects, query.getSession()));
    }

    /**
     * Delete all the reference objects.
     */
    protected void deleteAll(DeleteObjectQuery query) throws DatabaseException {
        Object referenceObjects = this.getRealCollectionAttributeValueFromObject(query.getObject(), query.getSession());
        deleteAll(query, referenceObjects);
    }

    /**
     * This method will make sure that all the records privately owned by this mapping are
     * actually removed. If such records are found then those are all read and removed one
     * by one along with their privately owned parts.
     */
    protected void deleteReferenceObjectsLeftOnDatabase(DeleteObjectQuery query) throws DatabaseException, OptimisticLockException {
        Object objects = this.readPrivateOwnedForObject(query);

        // delete all these objects one by one
        ContainerPolicy cp = this.getContainerPolicy();
        for (Object iter = cp.iteratorFor(objects); cp.hasNext(iter);) {
            query.getSession().deleteObject(cp.next(iter, query.getSession()));
        }
    }

    /**
     * Build and return a database row that contains a foreign key for the specified reference 
     * object.  This will be stored in the nested row(s).
     */
    protected AbstractRecord extractKeyRowFromReferenceObject(Object object, AbstractSession session, AbstractRecord parentRecord) {
        int size = this.sourceForeignKeyFields.size();
        AbstractRecord result;
        if (((EISDescriptor) this.getDescriptor()).isXMLFormat()) {
            Element newNode = XPathEngine.getInstance().createUnownedElement(((XMLRecord)parentRecord).getDOM(), (XMLField)getForeignKeyGroupingElement());
            result = new DOMRecord(newNode);
            ((DOMRecord)result).setSession(session);
        } else {
            result = this.descriptor.getObjectBuilder().createRecord(size, session);
        }
        for (int index = 0; index < size; index++) {
            DatabaseField fkField = this.sourceForeignKeyFields.get(index);
            if (object == null) {
                result.add(fkField, null);
            } else {
                DatabaseField pkField = this.sourceForeignKeysToTargetKeys.get(fkField);
                Object value = this.referenceDescriptor.getObjectBuilder().extractValueFromObjectForField(object, pkField, session);
                result.add(fkField, value);
            }
        }
        return result;
    }

    /**
     * INTERNAL:
     * Return the value of the reference attribute or a value holder.
     * Check whether the mapping's attribute should be optimized through batch and joining.
     */
    @Override
    public Object valueFromRow(AbstractRecord row, JoinedAttributeManager joinManager, ObjectBuildingQuery sourceQuery, CacheKey cacheKey, AbstractSession executionSession, boolean isTargetProtected, Boolean[] wasCacheUsed) throws DatabaseException {
        if (this.descriptor.getCachePolicy().isProtectedIsolation()) {
            if (this.isCacheable && isTargetProtected && cacheKey != null) {
                //cachekey will be null when isolating to uow
                //used cached collection
                Object cached = cacheKey.getObject();
                if (cached != null) {
                    if (wasCacheUsed != null){
                        wasCacheUsed[0] = Boolean.TRUE;
                    }
                    //this will just clone the indirection.
                    //the indirection object is responsible for cloning the value.
                    return getAttributeValueFromObject(cached);
                }
            } else if (!this.isCacheable && !isTargetProtected && cacheKey != null) {
                return this.indirectionPolicy.buildIndirectObject(new ValueHolder(null));
            }
        }
        if (((EISDescriptor) this.getDescriptor()).isXMLFormat()) {
            ((XMLRecord) row).setSession(executionSession);
        }
        
        ReadQuery targetQuery = getSelectionQuery();
        if (!this.isForeignKeyRelationship) {
            // if the source query is cascading then the target query must use the same settings
            if (targetQuery.isObjectLevelReadQuery() && (sourceQuery.shouldCascadeAllParts() || (sourceQuery.shouldCascadePrivateParts() && isPrivateOwned()) || (sourceQuery.shouldCascadeByMapping() && this.cascadeRefresh))) {
                targetQuery = (ObjectLevelReadQuery)targetQuery.clone();
                ((ObjectLevelReadQuery)targetQuery).setShouldRefreshIdentityMapResult(sourceQuery.shouldRefreshIdentityMapResult());
                targetQuery.setCascadePolicy(sourceQuery.getCascadePolicy());
                //CR #4365
                targetQuery.setQueryId(sourceQuery.getQueryId());
                // For queries that have turned caching off, such as aggregate collection, leave it off.
                if (targetQuery.shouldMaintainCache()) {
                    targetQuery.setShouldMaintainCache(sourceQuery.shouldMaintainCache());
                }
            }

            return getIndirectionPolicy().valueFromQuery(targetQuery, row, sourceQuery.getSession());
        } else {
            if (getIndirectionPolicy().usesIndirection()) {
                EISOneToManyQueryBasedValueHolder valueholder = new EISOneToManyQueryBasedValueHolder(this, targetQuery, row, sourceQuery.getSession());
                return getIndirectionPolicy().buildIndirectObject(valueholder);
            } else {
                Vector subRows = getForeignKeyRows(row, executionSession);

                if (subRows == null) {
                    return null;
                }

                ContainerPolicy cp = this.getContainerPolicy();
                Object results = cp.containerInstance(subRows.size());

                for (int i = 0; i < subRows.size(); i++) {
                    XMLRecord subRow = (XMLRecord)subRows.elementAt(i);
                    subRow.setSession(executionSession);
                    Object object = getIndirectionPolicy().valueFromQuery(targetQuery, subRow, sourceQuery.getSession());
                    if (object instanceof Collection) {
                        java.util.Iterator iter = ((Collection)object).iterator();
                        while (iter.hasNext()) {
                            cp.addInto(iter.next(), results, executionSession);
                        }
                    } else if (object instanceof java.util.Map) {
                        java.util.Iterator iter = ((java.util.Map)object).values().iterator();
                        while (iter.hasNext()) {
                            cp.addInto(iter.next(), results, executionSession);
                        }
                    } else {
                        cp.addInto(object, results, executionSession);
                    }
                }
                if (cp.sizeFor(results) == 0) {
                    return null;
                }
                return results;
            }
        }
    }

    /**
     * INTERNAL:
     */
    public Vector getForeignKeyRows(AbstractRecord row, AbstractSession session) {
        Vector subRows = new Vector();
        if (getForeignKeyGroupingElement() == null) {
            if (this.getSourceForeignKeyFields().size() > 0) {
                Object values = row.getValues(this.getSourceForeignKeyFields().get(0));

                if (values != null) {
                    if (values instanceof Vector) {
                        int valuesSize = ((Vector)values).size();
                        for (int j = 0; j < valuesSize; j++) {
                            AbstractRecord newRecord = this.descriptor.getObjectBuilder().createRecord(session);
                            newRecord.put(this.getSourceForeignKeyFields().get(0), ((Vector)values).get(j));
                            subRows.add(newRecord);
                        }
                    } else {
                        AbstractRecord newRecord = this.descriptor.getObjectBuilder().createRecord(session);
                        newRecord.put(getSourceForeignKeyFields().get(0), values);
                        subRows.add(newRecord);
                    }
                }
            }
        } else {
            subRows = (Vector)row.getValues(getForeignKeyGroupingElement());
        }
        return subRows;
    }

    /**
     * INTERNAL:
     * Get the appropriate attribute value from the object
     * and put it in the appropriate field of the database row.
     * Loop through the reference objects and extract the
     * primary keys and put them in the vector of "nested" rows.
     */
    @Override
    public void writeFromObjectIntoRow(Object object, AbstractRecord row, AbstractSession session, WriteType writeType) {
        if (!isForeignKeyRelationship) {
            return;
        }

        if (((getSourceForeignKeysToTargetKeys()) == null) || (getSourceForeignKeysToTargetKeys().size() == 0)) {
            return;
        }

        if (this.isReadOnly()) {
            return;
        }

        AbstractRecord referenceRow = this.getIndirectionPolicy().extractReferenceRow(this.getAttributeValueFromObject(object));
        if (referenceRow != null) {
            // the reference objects have not been instantiated - use the value from the original row				
            if (getForeignKeyGroupingElement() != null) {
                row.put(this.getForeignKeyGroupingElement(), referenceRow.getValues(this.getForeignKeyGroupingElement()));
            } else if (getSourceForeignKeyFields().size() > 0) {
                DatabaseField foreignKeyField = getSourceForeignKeyFields().get(0);
                row.put(foreignKeyField, referenceRow.getValues(foreignKeyField));
            }
            return;
        }

        ContainerPolicy cp = this.getContainerPolicy();

        // extract the keys from the objects
        Object attributeValue = this.getRealCollectionAttributeValueFromObject(object, session);
        Vector nestedRows = new Vector(cp.sizeFor(attributeValue));

        if (getForeignKeyGroupingElement() != null) {
            for (Object iter = cp.iteratorFor(attributeValue); cp.hasNext(iter);) {
                AbstractRecord nestedRow = extractKeyRowFromReferenceObject(cp.next(iter, session), session, row);
                nestedRows.add(nestedRow);
            }
            row.add(this.getForeignKeyGroupingElement(), nestedRows);
        } else {
            DatabaseField singleField = getSourceForeignKeyFields().get(0);
            DatabaseField pkField = getSourceForeignKeysToTargetKeys().get(singleField);
            List foreignKeys = new ArrayList(cp.sizeFor(attributeValue));
            for (Object iter = cp.iteratorFor(attributeValue); cp.hasNext(iter);) {
                Object singleValue = getReferenceDescriptor().getObjectBuilder().extractValueFromObjectForField(cp.next(iter, session), pkField, session);
                foreignKeys.add(singleValue);
            }
            row.add(singleField, foreignKeys);
        }
    }

    /**
     * INTERNAL:
     * This row is built for shallow insert which happens in case of bidirectional inserts.
     * The foreign keys must be set to null to avoid constraints.
     */
    @Override
    public void writeFromObjectIntoRowForShallowInsert(Object object, AbstractRecord row, AbstractSession session) {
        if (isForeignKeyRelationship() && !isReadOnly()) {
            if (getForeignKeyGroupingElement() != null) {
                row.put(getForeignKeyGroupingElement(), null);
            } else if (this.getSourceForeignKeyFields().size() > 0) {
                row.put(getSourceForeignKeyFields().get(0), null);
            }
        } else {
            super.writeFromObjectIntoRowForShallowInsert(object, row, session);
        }
    }

    /**
     * INTERNAL:
     * This row is built for update after shallow insert which happens in case of bidirectional inserts.
     * It contains the foreign keys with non null values that were set to null for shallow insert.
     * If mapping overrides writeFromObjectIntoRowForShallowInsert method it must override this one, too.
     */
    public void writeFromObjectIntoRowForUpdateAfterShallowInsert(Object object, AbstractRecord row, AbstractSession session, DatabaseTable table) {
        if (isReadOnly() || !isForeignKeyRelationship()) {
            return;
        }
        if (getForeignKeyGroupingElement() != null) {
            if (!getForeignKeyGroupingElement().getTable().equals(table)) {
                return;
            }
        } else if (this.getSourceForeignKeyFields().size() > 0) {
            if (!getSourceForeignKeyFields().get(0).getTable().equals(table)) {
                return;
            }
        }
        writeFromObjectIntoRow(object, row, session, WriteType.UPDATE);
    }
    
    /**
     * INTERNAL:
     * This row is built for shallow insert which happens in case of bidirectional inserts.
     * The foreign keys must be set to null to avoid constraints.
     */
    @Override
    public void writeFromObjectIntoRowForShallowInsertWithChangeRecord(ChangeRecord changeRecord, AbstractRecord row, AbstractSession session) {
        if (isForeignKeyRelationship() && !isReadOnly()) {
            if (getForeignKeyGroupingElement() != null) {
                row.put(getForeignKeyGroupingElement(), null);
            } else if (this.getSourceForeignKeyFields().size() > 0) {
                row.put(getSourceForeignKeyFields().get(0), null);
            }
        } else {
            super.writeFromObjectIntoRowForShallowInsertWithChangeRecord(changeRecord, row, session);
        }
    }

    /**
     * INTERNAL:
     * If any of the references objects has changed, write out
     * all the keys.
     */
    @Override
    public void writeFromObjectIntoRowForUpdate(WriteObjectQuery writeQuery, AbstractRecord row) throws DescriptorException {
        if (!this.isAttributeValueInstantiatedOrChanged(writeQuery.getObject())) {
            return;
        }

        AbstractSession session = writeQuery.getSession();

        if (session.isUnitOfWork()) {
            // PRS2074 fix for "traditional" Indirection
            Object collection1 = this.getRealCollectionAttributeValueFromObject(writeQuery.getObject(), session);
            Object collection2 = this.getRealCollectionAttributeValueFromObject(writeQuery.getBackupClone(), session);
            if (this.compareObjectsWithoutPrivateOwned(collection1, collection2, session)) {
                return;// nothing has changed - don't put anything in the row
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
        if (isForeignKeyRelationship()) {
            Object object = ((ObjectChangeSet)changeRecord.getOwner()).getUnitOfWorkClone();
            this.writeFromObjectIntoRow(object, row, session, writeType);
        } else {
            super.writeFromObjectIntoRowWithChangeRecord(changeRecord, row, session, writeType);
        }
    }

    /**
     * INTERNAL:
     * Write fields needed for insert into the template for with null values.
     */
    @Override
    public void writeInsertFieldsIntoRow(AbstractRecord row, AbstractSession session) {
        if (isForeignKeyRelationship() && !isReadOnly()) {
            if (getForeignKeyGroupingElement() != null) {
                row.put(getForeignKeyGroupingElement(), null);
            } else if (this.getSourceForeignKeyFields().size() > 0) {
                row.put(getSourceForeignKeyFields().get(0), null);
            }
        } else {
            super.writeInsertFieldsIntoRow(row, session);
        }
    }

    /**
     * INTERNAL:
     * This method is not supported in an EIS environment.
     */
    @Override
    public void setSelectionSQLString(String sqlString) {
        throw DescriptorException.invalidMappingOperation(this, "setSelectionSQLString");
    }

    /**
     * INTERNAL:
     * This method is not supported in an EIS environment.
     */
    @Override
    public void setDeleteAllSQLString(String sqlString) {
        throw DescriptorException.invalidMappingOperation(this, "setDeleteAllSQLString");
    }
}
