/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
// David McCann - 2.4 - Initial implementation
package org.eclipse.persistence.tools.dbws;

import static org.eclipse.persistence.internal.oxm.Constants.SCHEMA_PREFIX;
import static org.eclipse.persistence.oxm.XMLConstants.SCHEMA_URL;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.persistence.internal.xr.BatchQueryOperation;
import org.eclipse.persistence.internal.xr.Result;

/**
 * Model class for a batch SQL operation, i.e. two or more SQL statements
 * that are to be executed against the database in order.  The main
 * responsibilities of this model will be to hold the List of  SQL
 * statements and build the BatchQueryOperation instance that will
 * be responsible for executing the SQL statements.
 */
public class BatchSQLOperationModel extends OperationModel {
    protected List<String> batchSql;

    /**
     * The default constructor.
     */
    public BatchSQLOperationModel() {}

    /**
     * Get the list of SQL statements to be executed.
     *
     * @return List of SQL statements to be executed
     */
    public List<String> getBatchSql() {
        return batchSql;
    }

    /**
     * Set the list of SQL statements to be executed.
     *
     * @param sql List of SQL statements to be executed
     */
    public void setBatchSql(List<String> sql) {
        this.batchSql = sql;
    }

    /**
     * Convenience method for adding a single SQL statement to the
     * list of statements to be executed.  Note that the list
     * will be created if necessary.
     *
     * @param sql SQL statement to be added to the list of
     *            statements to be executed
     */
    public void addBatchSql(String sql) {
        if (batchSql == null) {
            batchSql = new ArrayList<String>();
        }
        batchSql.add(sql);
    }

    /**
     * Indicates that this is a batch SQL operation.
     */
    @Override
    public boolean isBatchSQLOperation() {
        return true;
    }

    /**
     * Build the BatchQueryOperation instance that will be responsible
     * for executing the SQL statements.
     */
    @Override
    public void buildOperation(DBWSBuilder builder) {
        super.buildOperation(builder);

        BatchQueryOperation batchQueryOp = new BatchQueryOperation();
        batchQueryOp.setName(name);
        batchQueryOp.setBatchSql(batchSql);

        Result result = new Result();
        result.setType(new QName(SCHEMA_URL, "int", SCHEMA_PREFIX)); // result 0, 1
        batchQueryOp.setResult(result);
        builder.xrServiceModel.getOperations().put(name, batchQueryOp);
    }
}
