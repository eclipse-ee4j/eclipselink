/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package deprecated.sdk;

import java.util.*;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.queries.*;
import org.eclipse.persistence.internal.sessions.*;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.internal.descriptors.*;
import org.eclipse.persistence.internal.identitymaps.*;
import org.eclipse.persistence.internal.helper.*;

/**
 * <code>SDKObjectCollectionMapping</code> is used to represent
 * a relationship between a single
 * source object and collection of target objects; where,
 * on the data store, the source object has a collection of
 * references (foreign keys) to the target objects. The parent
 * database row stores the nested foreign keys in database rows
 * in an <code>SDKFieldValue</code>.
 *
 * @see SDKDescriptor
 * @see SDKFieldValue
 * @see deprecated.sdk.SDKCollectionMappingHelper
 * @see deprecated.sdk.SDKCollectionChangeRecord
 * @see deprecated.sdk.SDKOrderedCollectionChangeRecord
 *
 * @author Big Country
 * @since TOPLink/Java 3.0
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).  This class is replaced by
 *         {@link org.eclipse.persistence.eis}
 */
public class SDKObjectCollectionMapping extends CollectionMapping implements SDKCollectionMapping {

    /** The source foreign key fields that reference the targetKeyFields. */
    protected transient Vector sourceForeignKeyFields;

    /** The (typically primary) target key fields that are referenced by the sourceForeignKeyFields. */
    protected transient Vector targetKeyFields;

    /** This maps the source foreign key fields to the corresponding (primary) target key fields. */
    protected transient Hashtable sourceForeignKeysToTargetKeys;

    /** The source foreign keys are stored in a single field. */
    protected DatabaseField field;

    /** The "data type" of the foreign keys. Depending on the data store, this could be optional. */
    protected String referenceDataTypeName;

    /**
     * Default constructor.
     */
    public SDKObjectCollectionMapping() {
        super();

        this.sourceForeignKeysToTargetKeys = new Hashtable(2);
        this.sourceForeignKeyFields = new Vector(1);
        this.targetKeyFields = new Vector(1);

        this.referenceDataTypeName = "";
    }

    /**
     * PUBLIC:
     * Currently, the TOPLink SDK does not support query result ordering.
     */
    public void addAscendingOrdering(String queryKeyName) {
        SDKDescriptorException.unsupported("query result ordering", this);
    }

    /**
     * PUBLIC:
     * Currently, the TOPLink SDK does not support query result ordering.
     */
    public void addDescendingOrdering(String queryKeyName) {
        SDKDescriptorException.unsupported("query result ordering", this);
    }

    /**
     * Add the associated fields to the appropriate collections.
     */
    protected void addSourceForeignKeyField(DatabaseField sourceForeignKeyField, DatabaseField targetKeyField) {
        this.getSourceForeignKeyFields().addElement(sourceForeignKeyField);
        this.getTargetKeyFields().addElement(targetKeyField);
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
    public void addSourceForeignKeyFieldName(String sourceForeignKeyFieldName, String targetKeyFieldName) {
        this.addSourceForeignKeyField(new DatabaseField(sourceForeignKeyFieldName), new DatabaseField(targetKeyFieldName));
    }

    /**
     * INTERNAL:
     * Build and return a new element based on the change set.
     */
    public Object buildAddedElementFromChangeSet(Object changeSet, MergeManager mergeManager) {
        ObjectChangeSet objectChangeSet = (ObjectChangeSet)changeSet;

        if (this.shouldMergeCascadeParts(mergeManager)) {
            Object targetElement = null;
            if (mergeManager.shouldMergeChangesIntoDistributedCache()) {
                targetElement = objectChangeSet.getTargetVersionOfSourceObject(mergeManager.getSession(), true);
            } else {
                targetElement = objectChangeSet.getUnitOfWorkClone();
            }
            mergeManager.mergeChanges(targetElement, objectChangeSet);
        }

        return this.buildElementFromChangeSet(changeSet, mergeManager);
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
    protected Object buildElementFromChangeSet(Object changeSet, MergeManager mergeManager) {
        return ((ObjectChangeSet)changeSet).getTargetVersionOfSourceObject(mergeManager.getSession());
    }

    /**
     * INTERNAL:
     * Build and return a new element based on the specified element.
     */
    public Object buildElementFromElement(Object element, MergeManager mergeManager) {
        if (this.shouldMergeCascadeParts(mergeManager)) {
            ObjectChangeSet objectChangeSet = null;
            if (mergeManager.getSession().isUnitOfWork()) {
                UnitOfWorkChangeSet uowChangeSet = (UnitOfWorkChangeSet)((UnitOfWorkImpl)mergeManager.getSession()).getUnitOfWorkChangeSet();
                if (uowChangeSet != null) {
                    objectChangeSet = (ObjectChangeSet)uowChangeSet.getObjectChangeSetForClone(element);
                }
            }
            Object mergeElement = mergeManager.getObjectToMerge(element);
            mergeManager.mergeChanges(mergeElement, objectChangeSet);
        }

        return mergeManager.getTargetVersionOfSourceObject(element);
    }

    /**
     * INTERNAL:
     * Build and return a new element based on the change set.
     */
    public Object buildRemovedElementFromChangeSet(Object changeSet, MergeManager mergeManager) {
        ObjectChangeSet objectChangeSet = (ObjectChangeSet)changeSet;

        if (!mergeManager.shouldMergeChangesIntoDistributedCache()) {
            mergeManager.registerRemovedNewObjectIfRequired(objectChangeSet.getUnitOfWorkClone());
        }

        return this.buildElementFromChangeSet(changeSet, mergeManager);
    }

    /**
     * INTERNAL:
     * Clone the appropriate attributes.
     */
    public Object clone() {
        SDKObjectCollectionMapping clone = (SDKObjectCollectionMapping)super.clone();
        clone.setSourceForeignKeysToTargetKeys((Hashtable)this.getSourceForeignKeysToTargetKeys().clone());
        return clone;
    }

    /**
     * Return all the fields mapped by the mapping.
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
        Vector primaryKey1 = this.getReferenceDescriptor().getObjectBuilder().extractPrimaryKeyFromObject(element1, session);
        Vector primaryKey2 = this.getReferenceDescriptor().getObjectBuilder().extractPrimaryKeyFromObject(element2, session);

        CacheKey cacheKey1 = new CacheKey(primaryKey1);
        CacheKey cacheKey2 = new CacheKey(primaryKey2);

        if (!cacheKey1.equals(cacheKey2)) {
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
    public ChangeRecord compareForChange(Object clone, Object backup, ObjectChangeSet owner, AbstractSession session) {
        if ((this.getAttributeValueFromObject(clone) != null) && (!this.isAttributeValueInstantiated(clone))) {
            return null;// never instantiated - no changes to report
        }
        return (new SDKCollectionMappingHelper(this)).compareForChange(clone, backup, owner, session);
    }

    /**
     * INTERNAL:
     * Compare the attributes belonging to this mapping for the objects.
     */
    public boolean compareObjects(Object object1, Object object2, AbstractSession session) {
        return (new SDKCollectionMappingHelper(this)).compareObjects(object1, object2, session);
    }

    /**
     * Delete all the reference objects.
     */
    protected void deleteAll(DeleteObjectQuery query, Object referenceObjects) throws DatabaseException {
        ((DeleteAllQuery)this.getDeleteAllQuery()).executeDeleteAll(query.getSession().getSessionForClass(this.getReferenceClass()), query.getTranslationRow(), this.getContainerPolicy().vectorFor(referenceObjects, query.getSession()));
    }

    /**
     *    This method will make sure that all the records privately owned by this mapping are
     * actually removed. If such records are found then those are all read and removed one
     * by one along with their privately owned parts.
     */
    protected void deleteReferenceObjectsLeftOnDatabase(DeleteObjectQuery query) throws DatabaseException, OptimisticLockException {
        Object objects = this.readPrivateOwnedForObject(query);

        // delete all these object one by one
        ContainerPolicy cp = this.getContainerPolicy();
        for (Object iter = cp.iteratorFor(objects); cp.hasNext(iter);) {
            query.getSession().deleteObject(cp.next(iter, query.getSession()));
        }
    }

    /**
     * INTERNAL:
     * Extract the collection of rows holding the
     * foreign keys from the specified row.
     */
    public Vector extractForeignKeyRows(AbstractRecord row, AbstractSession session) {
        Object fieldValue = row.get(this.getField());

        // BUG#2667762 there could be whitespace in the row instead of null
        if ((fieldValue == null) || (!(fieldValue instanceof SDKFieldValue))) {
            return new Vector();
        } else {
            return this.getDescriptor().buildNestedRowsFromFieldValue(fieldValue, session);
        }
    }

    /**
     * Build and return a database row that contains
     * a foreign key for the specified reference object.
     * This will be stored in the nested row(s).
     */
    protected AbstractRecord extractKeyRowFromReferenceObject(Object object, AbstractSession session) {
        DatabaseRecord result = new DatabaseRecord(this.getSourceForeignKeysToTargetKeys().size());

        for (Enumeration stream = this.getSourceForeignKeysToTargetKeys().keys();
                 stream.hasMoreElements();) {
            DatabaseField fkField = (DatabaseField)stream.nextElement();
            if (object == null) {
                result.put(fkField, null);
            } else {
                DatabaseField pkField = (DatabaseField)this.getSourceForeignKeysToTargetKeys().get(fkField);
                result.put(fkField, this.getReferenceDescriptor().getObjectBuilder().extractValueFromObjectForField(object, pkField, session));
            }
        }
        return result;
    }

    /**
     * INTERNAL:
     * Return the field mapped by the mapping.
     */
    public DatabaseField getField() {
        return field;
    }

    /**
     * PUBLIC:
     * Return the name of the field mapped by the mapping.
     */
    public String getFieldName() {
        return this.getField().getName();
    }

    /**
     * INTERNAL:
     * Return a vector of the foreign key fields in the same order
     * as the corresponding primary key fields are in their descriptor.
     */
    public Vector getOrderedForeignKeyFields() {
        Vector result = new Vector(this.getReferenceDescriptor().getPrimaryKeyFields().size());

        for (Iterator pkStream = this.getReferenceDescriptor().getPrimaryKeyFields().iterator();
                 pkStream.hasNext();) {
            DatabaseField pkField = (DatabaseField)pkStream.next();

            boolean found = false;
            for (Enumeration fkStream = this.getSourceForeignKeysToTargetKeys().keys();
                     fkStream.hasMoreElements();) {
                DatabaseField fkField = (DatabaseField)fkStream.nextElement();

                if (this.getSourceForeignKeysToTargetKeys().get(fkField).equals(pkField)) {
                    found = true;
                    result.addElement(fkField);
                    break;
                }
            }
            if (!found) {
                throw SDKDescriptorException.missingForeignKeyTranslation(this, pkField);
            }
        }
        return result;
    }

    /**
     * PUBLIC:
     * Return the "data type" of the reference objects.
     * Depending on the data store, this could be optional.
     */
    public String getReferenceDataTypeName() {
        return referenceDataTypeName;
    }

    /**
     * INTERNAL:
     * Return the source foreign key fields.
     */
    public Vector getSourceForeignKeyFields() {
        return sourceForeignKeyFields;
    }

    protected Hashtable getSourceForeignKeysToTargetKeys() {
        return sourceForeignKeysToTargetKeys;
    }

    /**
     * INTERNAL:
     * Return the target key fields.
     */
    public Vector getTargetKeyFields() {
        return targetKeyFields;
    }

    /**
     * INTERNAL:
     * Return whether the mapping has any constraint
     * dependencies, such as foreign keys.
     */
    public boolean hasConstraintDependency() {
        return true;
    }

    /**
     * INTERNAL:
     * Initialize the mapping.
     */
    public void initialize(AbstractSession session) throws DescriptorException {
        super.initialize(session);

        if (!isTargetKeySpecified()) {
            // targetKeyFields will be empty when #setSourceForeignKeyFieldName() is used
            setTargetKeyFields(new Vector(getReferenceDescriptor().getPrimaryKeyFields()));
        }
        this.initializeSourceForeignKeysToTargetKeys();

        if (getField() == null) {
            throw DescriptorException.fieldNameNotSetInMapping(this);
        }
        setField(getDescriptor().buildField(getField()));

        if (!hasCustomSelectionQuery()) {
            throw SDKDescriptorException.customSelectionQueryRequired(this);
        }
    }

    /**
     * Verify, munge, and hash the source foreign keys and target keys.
     */
    protected void initializeSourceForeignKeysToTargetKeys() throws DescriptorException {
        // Since we require a custom selection query, these keys are optional.
        if (getSourceForeignKeyFields().size() != getTargetKeyFields().size()) {
            throw DescriptorException.sizeMismatchOfForeignKeys(this);
        }

        // don't munge the source foreign keys - they are in a "nested table", not the "root table"
        for (int index = 0; index < getTargetKeyFields().size(); index++) {
            DatabaseField field = getReferenceDescriptor().buildField((DatabaseField)getTargetKeyFields().get(index));
            getTargetKeyFields().set(index, field);            
        }

        Enumeration sourceForeignKeys = getSourceForeignKeyFields().elements();
        Enumeration targetKeys = getTargetKeyFields().elements();
        while (sourceForeignKeys.hasMoreElements()) {
            getSourceForeignKeysToTargetKeys().put(sourceForeignKeys.nextElement(), targetKeys.nextElement());
        }
    }

    /**
     * Return whether the target key is specified.
     * It will be empty when #setSourceForeignKeyFieldName(String) is used.
     */
    protected boolean isTargetKeySpecified() {
        return !this.getTargetKeyFields().isEmpty();
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
     * Merge changes from the source to the target object.
     */
    public void mergeChangesIntoObject(Object target, ChangeRecord changeRecord, Object source, MergeManager mergeManager) {
        (new SDKCollectionMappingHelper(this)).mergeChangesIntoObject(target, changeRecord, source, mergeManager);
    }

    /**
     * INTERNAL:
     * Merge changes from the source to the target object.
     * Simply replace the entire target collection.
     */
    public void mergeIntoObject(Object target, boolean isTargetUnInitialized, Object source, MergeManager mergeManager) {
        if (isTargetUnInitialized && mergeManager.shouldMergeWorkingCopyIntoOriginal() && (!this.isAttributeValueInstantiated(source))) {
            // the target object was removed from the cache before the start of the commit
            this.setAttributeValueInObject(target, this.getIndirectionPolicy().getOriginalIndirectionObject(this.getAttributeValueFromObject(source), mergeManager.getSession()));
            return;
        }

        if (!this.shouldMergeCascadeReference(mergeManager)) {
            // this is a mergeClone - we should not merge the reference
            return;
        }

        if (mergeManager.shouldRefreshRemoteObject() && this.shouldMergeCascadeParts(mergeManager) && this.usesIndirection()) {
            this.mergeRemoteValueHolder(target, source, mergeManager);
            return;
        }

        if (mergeManager.shouldMergeOriginalIntoWorkingCopy()) {
            if (!this.isAttributeValueInstantiated(target)) {
                // the clone's value has not been instantiated yet, so we do not need to refresh it
                return;
            }
        } else if (!this.isAttributeValueInstantiated(source)) {
            // we are merging from a clone into an original - the attribute was never modified
            return;
        }

        (new SDKCollectionMappingHelper(this)).mergeIntoObject(target, isTargetUnInitialized, source, mergeManager);
    }

    /**
     * INTERNAL:
     * Delete the reference objects.
     */
    public void postDelete(DeleteObjectQuery query) throws DatabaseException, OptimisticLockException {
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
     * Insert privately owned parts
     */
    public void preInsert(WriteObjectQuery query) throws DatabaseException, OptimisticLockException {
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
                insertQuery.setObject(object);
                insertQuery.setCascadePolicy(query.getCascadePolicy());
                query.getSession().executeQuery(insertQuery);
            } else {
                // This will happen in a unit of work or cascaded query.
                // This is done only for persistence by reachability and is not required if the targets are in the queue anyway
                // Avoid cycles by checking commit manager, this is allowed because there is no dependency.
                if (!query.getSession().getCommitManager().isCommitInPreModify(object)) {
                    WriteObjectQuery writeQuery = new WriteObjectQuery();
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
    public void preUpdate(WriteObjectQuery query) throws DatabaseException, OptimisticLockException {
        if (!this.shouldObjectModifyCascadeToParts(query)) {
            return;
        }

        // if the target objects are not instantiated, they could not have been changed....
        if (!this.isAttributeValueInstantiated(query.getObject())) {
            return;
        }

        // manage objects added and removed from the collection
        Object objectsInMemory = this.getRealCollectionAttributeValueFromObject(query.getObject(), query.getSession());
        Object objectsInDB = this.readPrivateOwnedForObject(query);

        this.compareObjectsAndWrite(objectsInDB, objectsInMemory, query);
    }

    protected void setField(DatabaseField field) {
        this.field = field;
    }

    /**
     * PUBLIC:
     * Set the name of the field mapped by the mapping.
     */
    public void setFieldName(String fieldName) {
        this.setField(new DatabaseField(fieldName));
    }

    /**
     * PUBLIC:
     * Set the "data type" of the reference objects.
     * Depending on the data store, this could be optional.
     */
    public void setReferenceDataTypeName(String referenceDataTypeName) {
        this.referenceDataTypeName = referenceDataTypeName;
    }

    /**
     * PUBLIC:
     * Set the custom call that will perform the read
     * query to read all the related objects.
     */
    public void setSelectionCall(SDKCall call) {
        ReadAllQuery query = new ReadAllQuery();
        query.setCall(call);
        this.setCustomSelectionQuery(query);
    }

    /**
     * PUBLIC:
     * Define the source foreign key relationship in the one-to-many mapping.
     * This method can be used when the foreign and primary keys
     * have only a single field each.
     * (Use #addSourceForeignKeyFieldName(String, String)
     * #setSourceForeignKeyFieldNames(String[], String[]) for "composite" keys.)
     * Only the source foreign key field name is specified and the target
     * (primary) key field is
     * assumed to be the primary key of the target object.
     */
    public void setSourceForeignKeyFieldName(String sourceForeignKeyFieldName) {
        this.getSourceForeignKeyFields().addElement(new DatabaseField(sourceForeignKeyFieldName));
    }

    /**
     * PUBLIC:
     * Define the source foreign key relationship in the one-to-many mapping.
     * This method is used for composite source foreign key relationships.
     * That is, the source object's table has multiple foreign key fields to
     * the target object's (typically primary) key fields.
     * Both the source foreign key field names and the corresponding target primary
     * key field names must be specified.
     */
    public void setSourceForeignKeyFieldNames(String[] sourceForeignKeyFieldNames, String[] targetKeyFieldNames) {
        if (sourceForeignKeyFieldNames.length != targetKeyFieldNames.length) {
            throw DescriptorException.sizeMismatchOfForeignKeys(this);
        }
        for (int i = 0; i < sourceForeignKeyFieldNames.length; i++) {
            this.addSourceForeignKeyFieldName(sourceForeignKeyFieldNames[i], targetKeyFieldNames[i]);
        }
    }

    protected void setSourceForeignKeyFields(Vector sourceForeignKeyFields) {
        this.sourceForeignKeyFields = sourceForeignKeyFields;
    }

    protected void setSourceForeignKeysToTargetKeys(Hashtable sourceForeignKeysToTargetKeys) {
        this.sourceForeignKeysToTargetKeys = sourceForeignKeysToTargetKeys;
    }

    protected void setTargetKeyFields(Vector targetKeyFields) {
        this.targetKeyFields = targetKeyFields;
    }

    /**
     * PUBLIC:
     * Currently, the TOPLink SDK does not support batch reading.
     */
    public void setUsesBatchReading(boolean usesBatchReading) {
        SDKDescriptorException.unsupported("batch reading", this);
    }

    /**
     * ADVANCED:
     * This method is used to have an object add to a collection once the changeSet is applied
     * The referenceKey parameter should only be used for direct Maps.
     */
    public void simpleAddToCollectionChangeRecord(Object referenceKey, Object changeSetToAdd, ObjectChangeSet changeSet, AbstractSession session) {
        (new SDKCollectionMappingHelper(this)).simpleAddToCollectionChangeRecord(referenceKey, changeSetToAdd, changeSet, session);
    }

    /**
     * ADVANCED:
     * This method is used to have an object removed from a collection once the changeSet is applied
     * The referenceKey parameter should only be used for direct Maps.
     */
    public void simpleRemoveFromCollectionChangeRecord(Object referenceKey, Object changeSetToRemove, ObjectChangeSet changeSet, AbstractSession session) {
        (new SDKCollectionMappingHelper(this)).simpleRemoveFromCollectionChangeRecord(referenceKey, changeSetToRemove, changeSet, session);
    }

    /**
    * INTERNAL:
    * Get the appropriate attribute value from the object
    * and put it in the appropriate field of the database row.
    * Loop through the reference objects and extract the
    * primary keys and put them in the vector of "nested" rows.
    */
    public void writeFromObjectIntoRow(Object object, AbstractRecord row, AbstractSession session) {
        if (this.isReadOnly()) {
            return;
        }

        AbstractRecord referenceRow = this.getIndirectionPolicy().extractReferenceRow(this.getAttributeValueFromObject(object));
        if (referenceRow != null) {
            // the reference objects have not been instantiated - use the value from the original row
            row.put(this.getField(), referenceRow.get(this.getField()));
            return;
        }

        ContainerPolicy cp = this.getContainerPolicy();

        // extract the keys from the objects
        Object attributeValue = this.getRealCollectionAttributeValueFromObject(object, session);
        Vector nestedRows = new Vector(cp.sizeFor(attributeValue));
        for (Object iter = cp.iteratorFor(attributeValue); cp.hasNext(iter);) {
            Object element = cp.next(iter, session);
            AbstractRecord nestedRow = this.extractKeyRowFromReferenceObject(element, session);
            nestedRows.addElement(nestedRow);
        }

        Object fieldValue = null;
        if (!nestedRows.isEmpty()) {
            fieldValue = this.getDescriptor().buildFieldValueFromForeignKeys(nestedRows, this.getReferenceDataTypeName(), session);
        }
        row.put(this.getField(), fieldValue);
    }

    /**
     * INTERNAL:
     * This row is built for shallow insert which happens in case of bidirectional inserts.
     * The foreign keys must be set to null to avoid constraints.
     */
    public void writeFromObjectIntoRowForShallowInsert(Object object, AbstractRecord row, AbstractSession session) {
        if (this.isReadOnly()) {
            return;
        }
        row.put(this.getField(), null);
    }

    /**
     * INTERNAL:
     * This row is built for shallow insert which happens in case of bidirectional inserts.
     * The foreign keys must be set to null to avoid constraints.
     */
    public void writeFromObjectIntoRowForShallowInsertWithChangeRecord(ChangeRecord changeRecord, AbstractRecord row, AbstractSession session) {
        if (this.isReadOnly()) {
            return;
        }
        row.put(this.getField(), null);
    }

    /**
     * INTERNAL:
     * If any of the references objects has changed, write out
     * all the keys.
     */
    public void writeFromObjectIntoRowForUpdate(WriteObjectQuery writeQuery, AbstractRecord row) throws DescriptorException {
        if (!this.isAttributeValueInstantiated(writeQuery.getObject())) {
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
        this.writeFromObjectIntoRow(writeQuery.getObject(), row, session);
    }

    /**
     * INTERNAL:
     * Get the appropriate attribute value from the object
     * and put it in the appropriate field of the database row.
     * Loop through the reference objects and extract the
     * primary keys and put them in the vector of "nested" rows.
     */
    public void writeFromObjectIntoRowWithChangeRecord(ChangeRecord changeRecord, AbstractRecord row, AbstractSession session) {
        Object object = ((ObjectChangeSet)changeRecord.getOwner()).getUnitOfWorkClone();
        this.writeFromObjectIntoRow(object, row, session);
    }

    /**
     * INTERNAL:
     * Write fields needed for insert into the template for with null values.
     */
    public void writeInsertFieldsIntoRow(AbstractRecord row, AbstractSession session) {
        if (this.isReadOnly()) {
            return;
        }
        row.put(this.getField(), null);
    }

    //bug fix for bug#4534094 - private owned items were not being deleted
    public boolean isAggregateCollectionMapping() {
        return true;
    }
}
