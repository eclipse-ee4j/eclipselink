/*
 * Copyright (c) 1998, 2026 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.platform.database.oracle;

import java.sql.Statement;
import java.util.Map;

import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;

import oracle.jdbc.OraclePreparedStatement;
import org.eclipse.persistence.tools.schemaframework.FieldDefinition;

/**
 * <p><b>Purpose:</b>
 * Supports usage of certain Oracle JDBC specific APIs.
 */
public class Oracle10Platform extends Oracle9Platform  {

    public Oracle10Platform(){
        super();
    }

    /**
     * INTERNAL:
     * Add XMLType as the default database type for org.w3c.dom.Documents.
     * Add TIMESTAMP, TIMESTAMP WITH TIME ZONE and TIMESTAMP WITH LOCAL TIME ZONE
     */
    @Override
    protected Map<Class<?>, FieldDefinition.DatabaseType> buildDatabaseTypes() {
        Map<Class<?>, FieldDefinition.DatabaseType> fieldTypes = super.buildDatabaseTypes();
        // Offset classes contain an offset from UTC/Greenwich in the ISO-8601 calendar system so TZ should be included
        // TIMESTAMP WITH TIME ZONE is supported since 10g
        fieldTypes.put(java.time.OffsetDateTime.class, new FieldDefinition.DatabaseType("TIMESTAMP WITH TIME ZONE"));
        fieldTypes.put(java.time.OffsetTime.class, new FieldDefinition.DatabaseType("TIMESTAMP WITH TIME ZONE"));
        return fieldTypes;
    }

    /**
     * Build the hint string used for first rows.
     *
     * Allows it to be overridden
     */
    @Override
    protected String buildFirstRowsHint(int max){
        //bug 374136: override setting the FIRST_ROWS hint as this is not needed on Oracle10g
        return "";
    }

    /**
     * Internal: This gets called on each batch statement execution
     * Needs to be implemented so that it returns the number of rows successfully modified
     * by this statement for optimistic locking purposes (if useNativeBatchWriting is enabled, and
     * the call uses optimistic locking).
     *
     * @param isStatementPrepared - flag is set to true if this statement is prepared
     * @return - number of rows modified/deleted by this statement
     */
    @Override
    public int executeBatch(Statement statement, boolean isStatementPrepared) throws java.sql.SQLException {
        if (usesNativeBatchWriting() && isStatementPrepared) {
            int rowCount = 0;
            try {
                rowCount = ((OraclePreparedStatement)statement).sendBatch();
                setExecuteBatchRowCounts(new int[]{rowCount});
            } finally {
                ((OraclePreparedStatement) statement).setExecuteBatch(1);
            }
            return rowCount;
        } else {
            @SuppressWarnings("unused")
            int[] results = statement.executeBatch();
            setExecuteBatchRowCounts(results);
            return statement.getUpdateCount();
        }
    }

    /**
     * INTERNAL:
     * Indicate whether app. server should unwrap connection
     * to use lob locator.
     * No need to unwrap connection because
     * writeLob method doesn't use oracle proprietary classes.
     */
    @Override
    public boolean isNativeConnectionRequiredForLobLocator() {
        return false;
    }

    /**
     * INTERNAL:
     * Supports Batch Writing with Optimistic Locking.
     */
    @Override
    public boolean canBatchWriteWithOptimisticLocking(DatabaseCall call){
        return true;//usesNativeBatchWriting || !call.hasParameters();
    }
}
