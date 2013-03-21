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

import java.util.*;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.mappings.DatabaseMapping.WriteType;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;

/**
 * <p><b>Purpose</b>: An implementation of the OptimisticLockingPolicy interface.
 * This policy compares only the changed fields in the WHERE clause
 * when doing an update.  If any field has been changed, an optimistic
 * locking exception will be thrown.  A delete will only compare
 * the primary key.  <p>
 * NOTE: This policy can only be used inside a unit of work.
 *
 * @since TopLink 2.1
 * @author Peter Krogh
 */
public class ChangedFieldsLockingPolicy extends FieldsLockingPolicy {

    /**
     * PUBLIC:
     * Create a new changed fields locking policy.
     * This locking policy is based on locking on all changed fields by comparing with
     * their previous values to detect field-level collisions.
     *
     * Note: the unit of work must be used for all updates when using field locking. Without
     * a unit of work, there is no way for  to know what the original values were
     * without the back up clone in the unit of work.
     */
    public ChangedFieldsLockingPolicy() {
        super();
    }

    /**
     * INTERNAL:
     * Values to be included in the locking mechanism are added to the translation row.
     * For changed fields the normal build row is ok as only changed fields matter.
     */
    @Override
    public void addLockValuesToTranslationRow(ObjectLevelModifyQuery query) {
        verifyUsage(query.getSession());
        Object object;
        if (query.isDeleteObjectQuery()) {
            return;
        }
        object = query.getBackupClone();
        
        // EL bug 319759
        if (query.isUpdateObjectQuery()) {
            query.setShouldValidateUpdateCallCacheUse(true);
        }
        
        for (Enumeration enumtr = query.getModifyRow().keys(); enumtr.hasMoreElements();) {
            DatabaseField field = (DatabaseField)enumtr.nextElement();
            DatabaseMapping mapping = descriptor.getObjectBuilder().getMappingForField(field);
            mapping.writeFromObjectIntoRow(object, query.getTranslationRow(), query.getSession(), WriteType.UNDEFINED);
        }
    }

    /**
     * INTERNAL:
     * When given an expression, this method will return a new expression with the optimistic
     * locking values included.  The values are taken from the passed in database row.
     * This expression will be used in a delete call.
     * No new criteria will be added for changed fields.
     */
    @Override
    public Expression buildDeleteExpression(DatabaseTable table, Expression mainExpression, AbstractRecord row) {
        return mainExpression;
    }

    /**
     * INTERNAL:
     * Returns the fields that should be compared in the where clause.
     * In this case, it is only the fields that were changed.
     */
    @Override
    protected List<DatabaseField> getFieldsToCompare(DatabaseTable table, AbstractRecord transRow, AbstractRecord modifyRow) {
        List<DatabaseField> fields = getAllNonPrimaryKeyFields(table);
        List<DatabaseField> returnedFields = new ArrayList<DatabaseField>();
        for (DatabaseField field : fields) {
            if (modifyRow.containsKey(field)) {
                returnedFields.add(field);
            }
        }
        return returnedFields;
    }
}
