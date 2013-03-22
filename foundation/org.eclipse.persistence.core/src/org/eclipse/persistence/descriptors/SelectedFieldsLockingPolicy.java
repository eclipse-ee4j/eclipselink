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
package org.eclipse.persistence.descriptors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DatabaseMapping.WriteType;
import org.eclipse.persistence.queries.ObjectLevelModifyQuery;

/**
 * <p>
 * <b>Purpose</b>: An implementation of the OptimisticLockingPolicy interface.
 * This policy compares selected fields in the WHERE clause when doing an update
 * or a delete. If any field has been changed, an optimistic locking exception
 * will be thrown. Note that the fields specified must be mapped and not be
 * primary keys.
 * <p>
 * NOTE: This policy can only be used inside a unit of work.
 * 
 * @since TopLink 2.5
 */
public class SelectedFieldsLockingPolicy extends FieldsLockingPolicy {
    protected Map<DatabaseTable, List<DatabaseField>> lockFieldsByTable;
    protected List<DatabaseField> lockFields;

    /**
     * PUBLIC: Create a new selected fields locking policy. A field locking
     * policy is based on locking on the specified fields by comparing with
     * their previous values to detect field-level collisions. Note: the unit of
     * work must be used for all updates when using field locking.
     */
    public SelectedFieldsLockingPolicy() {
        super();
        this.lockFieldsByTable = new HashMap(4);
        this.lockFields = new ArrayList();
    }

    /**
     * PUBLIC: Add a field name to lock on. All fields in this list will be
     * compared when updating if the value of any of the fields does not match
     * the value in memory, an OptimisticLockException will be thrown.
     * 
     * Note: An Automatic update will not be done on this field, only a
     * comparison occurs.
     */
    public void addLockFieldName(String fieldName) {
        getLockFields().add(new DatabaseField(fieldName));
    }

    /**
     * INTERNAL: Values to be included in the locking mechanism are added to the
     * translation row. For changed fields the normal build row is ok as only
     * changed fields matter.
     */
    @Override
    public void addLockValuesToTranslationRow(ObjectLevelModifyQuery query) throws DatabaseException {
        Object object;
        verifyUsage(query.getSession());
        if (query.isDeleteObjectQuery()) {
            object = query.getObject();
        } else {
            object = query.getBackupClone();
        }

        // EL bug 319759
        if (query.isUpdateObjectQuery()) {
            query.setShouldValidateUpdateCallCacheUse(true);
        }

        for (Iterator<List<DatabaseField>> fields = getLockFieldsByTable().values().iterator(); fields.hasNext();) {
            for (DatabaseField field : fields.next()) {
                DatabaseMapping mapping = descriptor.getObjectBuilder().getMappingForField(field);
                // Bug5892889, Exception will be thrown if no matched database
                // field found
                if (mapping == null) {
                    throw DatabaseException.specifiedLockingFieldsNotFoundInDatabase(field.getQualifiedName());
                } else {
                    mapping.writeFromObjectIntoRow(object, query.getTranslationRow(), query.getSession(), WriteType.UNDEFINED);
                }
            }
        }
    }

    /**
     * INTERNAL: returns the lock fields to compare based on the passed in
     * table.
     */
    @Override
    protected List<DatabaseField> getFieldsToCompare(org.eclipse.persistence.internal.helper.DatabaseTable table, AbstractRecord transRow, AbstractRecord modifyRow) {
        return getLockFields(table);
    }

    /**
     * INTERNAL: Returns the lock fields
     */
    public List<DatabaseField> getLockFields() {
        return lockFields;
    }

    /**
     * INTERNAL: returns the lock fields based on the passed in table
     */
    protected List<DatabaseField> getLockFields(DatabaseTable table) {
        List<DatabaseField> temp = this.lockFieldsByTable.get(table);
        if (temp == null) {
            return Collections.EMPTY_LIST;
        }
        return temp;
    }

    /**
     * INTERNAL: returns the lock fields
     */
    protected Map<DatabaseTable, List<DatabaseField>> getLockFieldsByTable() {
        return lockFieldsByTable;
    }

    /**
     * INTERNAL: It is responsible for initializing the policy;
     */
    @Override
    public void initialize(AbstractSession session) {
        super.initialize(session);
        List<DatabaseField> lockFields = getLockFields();
        int size = lockFields.size();
        for (int index = 0; index < size; index++) {
            DatabaseField field = lockFields.get(index);
            field = descriptor.buildField(field);
            lockFields.set(index, field);
            List<DatabaseField> fieldsForTable = getLockFieldsByTable().get(field.getTable());
            if (fieldsForTable == null) {
                fieldsForTable = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance();
                getLockFieldsByTable().put(field.getTable(), fieldsForTable);
            }
            fieldsForTable.add(field);
        }
    }

    /**
     * PUBLIC: Set the field names to lock on. All fields in this list will be
     * compared when Updating. If the value of any of the fields does not match
     * the value in memory, an OptimisticLockException will be thrown.
     * 
     * Note: An Automatic update will not be done on this field, only a
     * comparison occurs.
     */
    public void setLockFieldNames(List<String> lockFieldNames) {
        for (String name : lockFieldNames) {
            addLockFieldName(name);
        }
    }

    /**
     * INTERNAL: Sets the lock fields
     */
    protected void setLockFields(List<DatabaseField> lockFields) {
        this.lockFields = lockFields;
    }

    /**
     * INTERNAL: Used to set the field names to be used in this policy.
     */
    protected void setLockFieldsByTable(Map<DatabaseTable, List<DatabaseField>> lockFieldsByTable) {
        this.lockFieldsByTable = lockFieldsByTable;
    }
}
