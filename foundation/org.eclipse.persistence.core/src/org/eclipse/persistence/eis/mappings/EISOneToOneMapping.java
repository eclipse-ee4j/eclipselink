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

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.annotations.CacheKeyType;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.indirection.ValueHolder;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.identitymaps.CacheId;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.queries.JoinedAttributeManager;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ObjectReferenceMapping;
import org.eclipse.persistence.queries.*;

/**
 * <p>An EIS one-to-one mapping is a reference mapping that represents the relationship between 
 * a single source object and a single mapped persistent Java object.  The source object usually 
 * contains a foreign key (pointer) to the target object (key on source); alternatively, the target 
 * object may contain a foreign key to the source object (key on target).  Because both the source 
 * and target objects use interactions, they must both be configured as root object types.  
 * 
 * <p><table border="1">
 * <tr>
 * <th id="c1" align="left">Record Type</th>
 * <th id="c2" align="left">Description</th>
 * </tr>
 * <tr>
 * <td headers="c1">Indexed</td>
 * <td headers="c2">Ordered collection of record elements.  The indexed record EIS format 
 * enables Java class attribute values to be retrieved by position or index.</td>
 * </tr>
 * <tr>
 * <td headers="c1">Mapped</td>
 * <td headers="c2">Key-value map based representation of record elements.  The mapped record
 * EIS format enables Java class attribute values to be retrieved by an object key.</td>
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
public class EISOneToOneMapping extends ObjectReferenceMapping implements EISMapping {

    /** Maps the source foreign/primary key fields to the target primary/foreign key fields. */

    protected Map sourceToTargetKeyFields;

    /** Maps the target primary/foreign key fields to the source foreign/primary key fields. */
    protected Map<DatabaseField, DatabaseField> targetToSourceKeyFields;

    /** These are used for non-unit of work modification to check if the value of the 1-1 was changed and a deletion is required. */
    protected boolean shouldVerifyDelete;
    protected transient Expression privateOwnedCriteria;

    public EISOneToOneMapping() {
        this.selectionQuery = new ReadObjectQuery();

        this.foreignKeyFields = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(1);

        this.sourceToTargetKeyFields = new HashMap(2);
        this.targetToSourceKeyFields = new HashMap(2);
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean isEISMapping() {
        return true;
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean isOneToOneMapping() {
        return true;
    }

    /**
     * PUBLIC:
     * Define the source foreign key relationship in the one-to-one mapping.
     * This method is used to add foreign key relationships to the mapping.
     * Both the source foreign key field name and the corresponding
     * target primary key field name must be specified.
     */
    @Override
    public void addForeignKeyField(DatabaseField sourceForeignKeyField, DatabaseField targetKeyField) {
        getSourceToTargetKeyFields().put(sourceForeignKeyField, targetKeyField);
        getTargetToSourceKeyFields().put(targetKeyField, sourceForeignKeyField);

        getForeignKeyFields().add(sourceForeignKeyField);
        setIsForeignKeyRelationship(true);
    }

    /**
     * PUBLIC:
     * Define the source foreign key relationship in the one-to-one mapping.
     * This method is used to add foreign key relationships to the mapping.
     * Both the source foreign key field name and the corresponding
     * target primary key field name must be specified.
     */
    public void addForeignKeyFieldName(String sourceForeignKeyFieldName, String targetKeyFieldName) {
        this.addForeignKeyField(new DatabaseField(sourceForeignKeyFieldName), new DatabaseField(targetKeyFieldName));
    }

    /**
     * INTERNAL:
     * This methods clones all the fields and ensures that each collection refers to
     * the same clones.
     */
    @Override
    public Object clone() {
        EISOneToOneMapping clone = (EISOneToOneMapping)super.clone();
        clone.setForeignKeyFields(org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(getForeignKeyFields().size()));
        clone.setSourceToTargetKeyFields(new HashMap(getSourceToTargetKeyFields().size()));
        clone.setTargetToSourceKeyFields(new HashMap(getTargetToSourceKeyFields().size()));
        Map setOfFields = new HashMap(getTargetToSourceKeyFields().size());

        for (Enumeration enumtr = getForeignKeyFields().elements(); enumtr.hasMoreElements();) {
            DatabaseField field = (DatabaseField)enumtr.nextElement();

            DatabaseField fieldClone = field.clone();
            setOfFields.put(field, fieldClone);
            clone.getForeignKeyFields().addElement(fieldClone);
        }

        //get clones from set for source hashtable.  If they do not exist, create a new one.		
        Iterator sourceKeyIterator = getSourceToTargetKeyFields().keySet().iterator();
        while (sourceKeyIterator.hasNext()) {
            DatabaseField sourceField = (DatabaseField)sourceKeyIterator.next();
            DatabaseField targetField = getSourceToTargetKeyFields().get(sourceField);

            DatabaseField targetClone = (DatabaseField)setOfFields.get(targetField);
            if (targetClone == null) {
                targetClone = targetField.clone();
                setOfFields.put(targetField, targetClone);
            }

            DatabaseField sourceClone = sourceField.clone();
            if (sourceClone == null) {
                sourceClone = sourceField.clone();
                setOfFields.put(sourceField, sourceClone);
            }
            clone.getSourceToTargetKeyFields().put(sourceClone, targetClone);
        }

        //get clones from set for target hashtable.  If they do not exist, create a new one.						
        Iterator targetKeyIterator = getTargetToSourceKeyFields().keySet().iterator();
        while (targetKeyIterator.hasNext()) {
            DatabaseField targetField = (DatabaseField)targetKeyIterator.next();
            DatabaseField sourceField = getTargetToSourceKeyFields().get(targetField);

            DatabaseField targetClone = (DatabaseField)setOfFields.get(targetField);
            if (targetClone == null) {
                targetClone = targetField.clone();
                setOfFields.put(targetField, targetClone);
            }

            DatabaseField sourceClone = (DatabaseField)setOfFields.get(sourceField);
            if (sourceClone == null) {
                sourceClone = sourceField.clone();
                setOfFields.put(sourceField, sourceClone);
            }
            clone.getTargetToSourceKeyFields().put(targetClone, sourceClone);
        }

        return clone;
    }

    /**
     * INTERNAL:
     * Return the primary key for the reference object (i.e. the object
     * object referenced by domainObject and specified by mapping).
     * This key will be used by a RemoteValueHolder.
     */
    @Override
    public Object extractPrimaryKeysForReferenceObjectFromRow(AbstractRecord row) {
        List primaryKeyFields = getReferenceDescriptor().getPrimaryKeyFields();
        Object[] result = new  Object[primaryKeyFields.size()];
        for (int index = 0; index < primaryKeyFields.size(); index++) {
            DatabaseField targetKeyField = (DatabaseField)primaryKeyFields.get(index);
            DatabaseField sourceKeyField = getTargetToSourceKeyFields().get(targetKeyField);
            if (sourceKeyField == null) {
                return null;
            }
            result[index] = row.get(sourceKeyField);
            if (getReferenceDescriptor().getCachePolicy().getCacheKeyType() == CacheKeyType.ID_VALUE) {
                return result[index];
            }
        }
        return new CacheId(result);
    }

    /**
     * INTERNAL:
     * Initialize the mapping.
     */
    @Override
    public void initialize(AbstractSession session) throws DescriptorException {
        super.initialize(session);

        // Must build foreign keys fields.
        List foreignKeyFields = getForeignKeyFields();
        int size = foreignKeyFields.size();
        for (int index = 0; index < size; index++) {
            DatabaseField foreignKeyField = (DatabaseField)foreignKeyFields.get(index);
            foreignKeyField = getDescriptor().buildField(foreignKeyField);
            foreignKeyFields.set(index, foreignKeyField);
        }

        initializeForeignKeys(session);

        if (shouldInitializeSelectionCriteria()) {
            initializeSelectionCriteria(session);
        } else {
            setShouldVerifyDelete(false);
        }
        setFields(collectFields());
    }

    /**
     * INTERNAL:
     * The foreign keys primary keys are stored as database fields in the hashtable.
     */
    protected void initializeForeignKeys(AbstractSession session) {
        HashMap newSourceToTargetKeyFields = new HashMap(getSourceToTargetKeyFields().size());
        HashMap newTargetToSourceKeyFields = new HashMap(getTargetToSourceKeyFields().size());
        Iterator iterator = getSourceToTargetKeyFields().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry)iterator.next();
            DatabaseField sourceField = (DatabaseField)entry.getKey();
            DatabaseField targetField = (DatabaseField)entry.getValue();

            sourceField = getDescriptor().buildField(sourceField);
            targetField = getReferenceDescriptor().buildField(targetField);
            newSourceToTargetKeyFields.put(sourceField, targetField);
            newTargetToSourceKeyFields.put(targetField, sourceField);
        }
        setSourceToTargetKeyFields(newSourceToTargetKeyFields);
        setTargetToSourceKeyFields(newTargetToSourceKeyFields);
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
        if (this.getSourceToTargetKeyFields().isEmpty()) {
            throw DescriptorException.noForeignKeysAreSpecified(this);
        }

        Expression criteria;
        Expression builder = new ExpressionBuilder();
        Iterator keyIterator = getSourceToTargetKeyFields().keySet().iterator();
        while (keyIterator.hasNext()) {
            DatabaseField foreignKey = (DatabaseField)keyIterator.next();
            DatabaseField targetKey = getSourceToTargetKeyFields().get(foreignKey);

            Expression expression = builder.getField(targetKey).equal(builder.getParameter(foreignKey));
            criteria = expression.and(getSelectionCriteria());
            setSelectionCriteria(criteria);
        }
    }

    /**
     * INTERNAL:
     * Reads the private owned object.
     */
    @Override
    protected Object readPrivateOwnedForObject(ObjectLevelModifyQuery modifyQuery) throws DatabaseException {
        if (modifyQuery.getSession().isUnitOfWork()) {
            return getRealAttributeValueFromObject(modifyQuery.getBackupClone(), modifyQuery.getSession());
        } else {
            if (!shouldVerifyDelete()) {
                return null;
            }
            ReadObjectQuery readQuery = (ReadObjectQuery)getSelectionQuery().clone();

            readQuery.setSelectionCriteria(getPrivateOwnedCriteria());
            return modifyQuery.getSession().executeQuery(readQuery, modifyQuery.getTranslationRow());
        }
    }

    /**
     * INTERNAL:
     * Selection criteria is created with source foreign keys and target keys.
     */
    protected void initializePrivateOwnedCriteria() {
        if (!isForeignKeyRelationship()) {
            setPrivateOwnedCriteria(getSelectionCriteria());
        } else {
            Expression pkCriteria = getDescriptor().getObjectBuilder().getPrimaryKeyExpression();
            ExpressionBuilder builder = new ExpressionBuilder();
            Expression backRef = builder.getManualQueryKey(getAttributeName() + "-back-ref", getDescriptor());
            Expression newPKCriteria = pkCriteria.rebuildOn(backRef);
            Expression twistedSelection = backRef.twist(getSelectionCriteria(), builder);
            if (getDescriptor().getQueryManager().getAdditionalJoinExpression() != null) {
                // We don't have to twist the additional join because it's all against the same node, which is our base
                // but we do have to rebuild it onto the manual query key
                Expression rebuiltAdditional = getDescriptor().getQueryManager().getAdditionalJoinExpression().rebuildOn(backRef);
                if (twistedSelection == null) {
                    twistedSelection = rebuiltAdditional;
                } else {
                    twistedSelection = twistedSelection.and(rebuiltAdditional);
                }
            }
            setPrivateOwnedCriteria(newPKCriteria.and(twistedSelection));
        }
    }

    /**
     * INTERNAL:
     * Return the value of the field from the row or a value holder on the query to obtain the object.
     * Check for batch + aggregation reading.
     */
    @Override
    public Object valueFromRow(AbstractRecord row, JoinedAttributeManager joinManager, ObjectBuildingQuery query, CacheKey cacheKey, AbstractSession session, boolean isTargetProtected, Boolean[] wasCacheUsed) throws DatabaseException {
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
                return result;
            } else if (!this.isCacheable && !isTargetProtected && cacheKey != null) {
                return this.indirectionPolicy.buildIndirectObject(new ValueHolder(null));
            }
        }
        // If any field in the foreign key is null then it means there are no referenced objects
        // Skip for partial objects as fk may not be present.
        if (!query.hasPartialAttributeExpressions()) {
            for (Enumeration enumeration = getFields().elements(); enumeration.hasMoreElements();) {
                DatabaseField field = (DatabaseField)enumeration.nextElement();
                if (row.get(field) == null) {
                    return getIndirectionPolicy().nullValueFromRow();
                }
            }
        }

        // Call the default which executes the selection query,
        // or wraps the query with a value holder.
        //return super.valueFromRow(row, query);
        ReadQuery targetQuery = getSelectionQuery();

        // if the source query is cascading then the target query must use the same settings
        if (targetQuery.isObjectLevelReadQuery() && (query.shouldCascadeAllParts() || (query.shouldCascadePrivateParts() && isPrivateOwned()) || (query.shouldCascadeByMapping() && this.cascadeRefresh))) {
            targetQuery = (ObjectLevelReadQuery)targetQuery.clone();
            ((ObjectLevelReadQuery)targetQuery).setShouldRefreshIdentityMapResult(query.shouldRefreshIdentityMapResult());
            targetQuery.setCascadePolicy(query.getCascadePolicy());
            //CR #4365
            targetQuery.setQueryId(query.getQueryId());
            // For queries that have turned caching off, such as aggregate collection, leave it off.
            if (targetQuery.shouldMaintainCache()) {
                targetQuery.setShouldMaintainCache(query.shouldMaintainCache());
            }
        }

        return getIndirectionPolicy().valueFromQuery(targetQuery, row, query.getSession());
    }

    /**
     * INTERNAL:
     * Get a value from the object and set that in the respective field of the row.
     */
    @Override
    public void writeFromObjectIntoRow(Object object, AbstractRecord Record, AbstractSession session, WriteType writeType) {
        if (isReadOnly() || (!isForeignKeyRelationship())) {
            return;
        }

        AbstractRecord referenceRow = getIndirectionPolicy().extractReferenceRow(getAttributeValueFromObject(object));
        if (referenceRow == null) {
            // Extract from object.
            Object referenceObject = getRealAttributeValueFromObject(object, session);

            for (int i = 0; i < getForeignKeyFields().size(); i++) {
                DatabaseField sourceKey = getForeignKeyFields().get(i);
                DatabaseField targetKey = getSourceToTargetKeyFields().get(sourceKey);

                Object referenceValue = null;

                // If privately owned part is null then method cannot be invoked.
                if (referenceObject != null) {
                    referenceValue = getReferenceDescriptor().getObjectBuilder().extractValueFromObjectForField(referenceObject, targetKey, session);
                }
                Record.add(sourceKey, referenceValue);
            }
        } else {
            for (int i = 0; i < getForeignKeyFields().size(); i++) {
                DatabaseField sourceKey = getForeignKeyFields().get(i);
                Record.add(sourceKey, referenceRow.get(sourceKey));
            }
        }
    }

    /**
     * INTERNAL:
     * Return the classifiction for the field contained in the mapping.
     * This is used to convert the row value to a consistent java value.
     */
    @Override
    public Class getFieldClassification(DatabaseField fieldToClassify) throws DescriptorException {
        DatabaseField fieldInTarget = getSourceToTargetKeyFields().get(fieldToClassify);
        if (fieldInTarget == null) {
            return null;// Can be registered as multiple table secondary field mapping
        }
        DatabaseMapping mapping = getReferenceDescriptor().getObjectBuilder().getMappingForField(fieldInTarget);
        if (mapping == null) {
            return null;// Means that the mapping is read-only
        }
        return mapping.getFieldClassification(fieldInTarget);
    }

    /**
     * INTERNAL:
     * The private owned criteria is only used outside of the unit of work to compare the previous value of the reference.
     */
    public Expression getPrivateOwnedCriteria() {
        if (privateOwnedCriteria == null) {
            initializePrivateOwnedCriteria();
        }
        return privateOwnedCriteria;
    }

    /**
     * INTERNAL:
     * Private owned criteria is used to verify the deletion of the target.
     * It joins from the source table on the foreign key to the target table,
     * with a parameterization of the primary key of the source object.
     */
    protected void setPrivateOwnedCriteria(Expression expression) {
        privateOwnedCriteria = expression;
    }

    /**
     * PUBLIC:
     * Verify delete is used during delete and update on private 1:1's outside of a unit of work only.
     * It checks for the previous value of the target object through joining the source and target tables.
     * By default it is always done, but may be disabled for performance on distributed database reasons.
     * In the unit of work the previous value is obtained from the backup-clone so it is never used.
     */
    public void setShouldVerifyDelete(boolean shouldVerifyDelete) {
        this.shouldVerifyDelete = shouldVerifyDelete;
    }

    /**
    * PUBLIC:
    * Verify delete is used during delete and update outside of a unit of work only.
    * It checks for the previous value of the target object through joining the source and target tables.
    */
    public boolean shouldVerifyDelete() {
        return shouldVerifyDelete;
    }

    /**
     * INTERNAL:
     * Gets the foreign key fields.
     */
    public Map<DatabaseField, DatabaseField> getSourceToTargetKeyFields() {
        return sourceToTargetKeyFields;
    }

    /**
     * INTERNAL:
     * Gets the target foreign key fields.
     */
    public Map<DatabaseField, DatabaseField> getTargetToSourceKeyFields() {
        return targetToSourceKeyFields;
    }

    /**
     * INTERNAL:
     * Set the source keys to target keys fields association.
     */
    public void setSourceToTargetKeyFields(Map sourceToTargetKeyFields) {
        this.sourceToTargetKeyFields = sourceToTargetKeyFields;
    }

    /**
     * INTERNAL:
     * Set the source keys to target keys fields association.
     */
    public void setTargetToSourceKeyFields(Map targetToSourceKeyFields) {
        this.targetToSourceKeyFields = targetToSourceKeyFields;
    }

    /**
     * INTERNAL:
     * This method is not supported in an EIS environment.
     */
    @Override
    public void setSelectionSQLString(String sqlString) {
        throw DescriptorException.invalidMappingOperation(this, "setSelectionSQLString");
    }
}
