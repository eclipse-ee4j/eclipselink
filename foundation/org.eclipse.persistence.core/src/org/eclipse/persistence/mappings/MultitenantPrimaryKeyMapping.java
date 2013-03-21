/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.mappings;

import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.internal.descriptors.MultitenantPrimaryKeyAccessor;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.queries.JoinedAttributeManager;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.ChangeRecord;
import org.eclipse.persistence.internal.sessions.MergeManager;
import org.eclipse.persistence.internal.sessions.ObjectChangeSet;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.mappings.foundation.AbstractColumnMapping;
import org.eclipse.persistence.queries.ObjectBuildingQuery;
import org.eclipse.persistence.sessions.Session;

/**
 * <b>Purpose</b>: Maps a multitenant property to the corresponding database 
 * field type. The list of field types that are supported by EclipseLink's 
 * direct to field mapping is dependent on the relational database being used.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.4
 */
public class MultitenantPrimaryKeyMapping extends AbstractColumnMapping {
    private MultitenantPrimaryKeyAccessor accessor;
    
    /**
     * Constructor
     */
    public MultitenantPrimaryKeyMapping() {
        super();

        isInsertable = true;
        isUpdatable = false;
        setIsOptional(false);
        accessor = new MultitenantPrimaryKeyAccessor();
        setAttributeAccessor(accessor);
    }
    
    /**
     * INTERNAL:
     * Clone the attribute from the clone and assign it to the backup.
     * 
     * This is an override from DatabaseMapping and must be implemented.
     */
    @Override
    public void buildBackupClone(Object clone, Object backup, UnitOfWorkImpl unitOfWork) {
        // Mapping is write only so nothing to do.
    }
    
    /**
     * INTERNAL:
     * Clone the attribute from the original and assign it to the clone.
     * 
     * This is an override from DatabaseMapping and must be implemented.
     */
    @Override
    public void buildClone(Object original, CacheKey cacheKey, Object clone, Integer refreshCascade, AbstractSession cloningSession) {
        // Mapping is write only so nothing to do.
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
        // Mapping is write only so nothing to do.
    }

    /**
     * INTERNAL:
     * Compare the clone and backup clone values and return a change record if 
     * the value changed.
     * 
     * This is an override from DatabaseMapping and must be implemented.
     */
    @Override
    public ChangeRecord compareForChange(Object clone, Object backUp, ObjectChangeSet owner, AbstractSession session) {
        // Mapping is write only so nothing to do.
        return null;
    }
    
    /**
     * INTERNAL:
     * Compare the attributes belonging to this mapping for the objects.
     * 
     * This is an override from DatabaseMapping and must be implemented.
     */
    @Override
    public boolean compareObjects(Object firstObject, Object secondObject, AbstractSession session) {
        // Mapping is write only so nothing to do.
        return true;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public Object getFieldValue(Object propertyValue, AbstractSession session) {
        return accessor.getValue(session);
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public Object getObjectValue(Object fieldValue, Session session) {
        return accessor.getValue(session);    
    }
    
    /**
     * INTERNAL:
     * The mapping is initialized with the given session. This mapping is fully 
     * initialized after this.
     */
    @Override
    public void initialize(AbstractSession session) throws DescriptorException {
        super.initialize(session);
        
        if (getField() == null) {
            session.getIntegrityChecker().handleError(DescriptorException.fieldNameNotSetInMapping(this));
        }

        setField(getDescriptor().buildField(getField()));
        setFields(collectFields());

        // Must unwrap Struct types on WLS.
        if (getField().getSqlType() == java.sql.Types.STRUCT) {
            getDescriptor().setIsNativeConnectionRequired(true);
        }
    }
    
    /**
     * INTERNAL:
     * Return if this mapping requires its attribute value to be cloned.
     */
    @Override
    public boolean isCloningRequired() {
        return false;
    }
    
    /**
     * INTERNAL
     */
    @Override
    public boolean isMultitenantPrimaryKeyMapping() {
        return true;
    }
    
    /**
     * INTERNAL:
     */
    public boolean isRelationalMapping() {
        return true;
    }
    
    /**
     * INTERNAL
     * This mapping must be write only as their is no attribute to read into.
     */
    @Override
    public boolean isWriteOnly() {
        return true;
    }
    
    /**
     * INTERNAL:
     * Merge changes from the source to the target object.
     * 
     * This is an override from DatabaseMapping and must be implemented.
     */
    @Override
    public void mergeChangesIntoObject(Object target, ChangeRecord changeRecord, Object source, MergeManager mergeManager, AbstractSession targetSession) {
        // Mapping is write only so do nothing.
    }
    
    /**
     * INTERNAL:
     * Merge changes from the source to the target object. This merge is only 
     * called when a changeSet for the target does not exist or the target is 
     * uninitialized
     * 
     * This is an override from DatabaseMapping and must be implemented.
     */
    @Override
    public void mergeIntoObject(Object target, boolean isTargetUninitialized, Object source, MergeManager mergeManager, AbstractSession targetSession) {
        // Mapping is write only so do nothing.
    }
    
    /**
     * INTERNAL:
     * The context property that is used to write this mapping must be set. It
     * is set as the attribute name (which gets set on the accessor)
     */
    public void setContextProperty(String contextProperty) {
        setAttributeName(contextProperty);
    }
    
    /**
     * INTERNAL:
     * Get a value from the object and set that in the respective field of the row.
     */
    @Override
    public void writeFromObjectIntoRow(Object object, AbstractRecord row, AbstractSession session, WriteType writeType) {
        writeValueIntoRow(row, getField(), getFieldValue(null, session));
    }
    
    /**
     * INTERNAL:
     * Return the Value from the object.
     */
    @Override
    public Object valueFromObject(Object anObject, DatabaseField field, AbstractSession session) {
        return accessor.getValue(session);
    }
    
    /**
     * INTERNAL:
     * Write fields needed for insert into the template for with null values.
     */
    @Override
    public void writeInsertFieldsIntoRow(AbstractRecord databaseRow, AbstractSession session) {
        databaseRow.add(getField(), null);
    }
    
    /**
     * INTERNAL:
     */
    @Override
    protected void writeValueIntoRow(AbstractRecord row, DatabaseField field, Object fieldValue) {
        row.add(getField(), fieldValue);
    }
}
