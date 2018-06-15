/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.platform.database.oracle;

import java.sql.Statement;

import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;

import oracle.jdbc.OraclePreparedStatement;

/**
 * <p><b>Purpose:</b>
 * Supports usage of certain Oracle JDBC specific APIs.
 */
public class Oracle10Platform extends Oracle9Platform  {

    public Oracle10Platform(){
        super();
    }

    /**
     * Build the hint string used for first rows.
     *
     * Allows it to be overridden
     * @param max
     * @return
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
            } finally {
                ((OraclePreparedStatement) statement).setExecuteBatch(1);
            }
            return rowCount;
        } else {
            @SuppressWarnings("unused")
            int[] results = statement.executeBatch();
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
