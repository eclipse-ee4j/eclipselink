/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * David McCann - 2.4 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.tools.dbws;

import static org.eclipse.persistence.oxm.XMLConstants.SCHEMA_PREFIX;
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
     * 
     * @Override 
     */
    public boolean isBatchSQLOperation() {
        return true;
    }

    /**
     * Build the BatchQueryOperation instance that will be responsible
     * for executing the SQL statements.
     * 
     * @Override 
     */
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