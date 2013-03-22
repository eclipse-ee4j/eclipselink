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

import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.queries.*;

/**
 * <p><b>Purpose</b>: An implementation of the OptimisticLockingPolicy interface.
 * This policy compares every field in the table in the WHERE clause
 * when doing an update or a delete.  If any field has been changed, an
 * optimistic locking exception will be thrown.<p>
 * NOTE: This policy can only be used inside a unit of work.
 *
 * @since TopLink 2.1
 * @author Peter Krogh
 */
public class AllFieldsLockingPolicy extends FieldsLockingPolicy {

    /**
     * PUBLIC:
     * Create a new all fields locking policy.
     * A field locking policy is based on locking on all fields by comparing with their previous values to detect field-level collisions.
     * Note: the unit of work must be used for all updates when using field locking.
     */
    public AllFieldsLockingPolicy() {
        super();
    }

    /**
     * INTERNAL:
     * Values to be included in the locking mechanism are added
     * to the translation row.  Set the translation row to all the original field values.
     */
    @Override
    public void addLockValuesToTranslationRow(ObjectLevelModifyQuery query) {
        verifyUsage(query.getSession());
        query.setTranslationRow(descriptor.getObjectBuilder().buildRowForWhereClause(query));
    }

    /**
     * INTERNAL:
     * Returns the fields that should be compared in the where clause.
     * In this case, it is all the fields, except for the primary key
     * and class indicator.
     */
    @Override
    protected List<DatabaseField> getFieldsToCompare(DatabaseTable table, AbstractRecord transRow, AbstractRecord modifyRow) {
        return getAllNonPrimaryKeyFields(table);
    }
}
