/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.xr;

import java.util.List;

/**
 * For use with a batch SQL query, i.e. two or more SQL statements that 
 * are to be executed against the database in order.  The result will
 * be either 0 (success) or 1 (failure).  Note that if a statement
 * fails, i.e. an exception is thrown upon executing a given SQL 
 * statement, none of the remaining statements are executed.
 * 
 * Warning:  the SQL is assumed to be valid and well formed, and no 
 * roll back or any type of error handling is performed.
 *  
 */
public class BatchQueryOperation extends QueryOperation {
    protected List<String> batchSql;

    /**
     * Return the List of SQL statements to be executed for this operation.
     */
    public List<String> getBatchSql() {
        return batchSql;
    }
    
    /**
     * Set the List of SQL statements to be executed for this operation.
     */
    public void setBatchSql(List<String> batchSql) {
        this.batchSql = batchSql;
    }

    /**
     * Invoke the SQL statements in order against the database.
     * The returned ValueOject will hold either 0 (success) or 
     * 1 (failure).
     * 
     * @Override
     */
    public Object invoke(XRServiceAdapter xrService, Invocation invocation) {
        ValueObject v = new ValueObject();
        for (String sqlString : batchSql) {
            try {
                xrService.getORSession().executeNonSelectingSQL(sqlString);
            } catch (Exception x) {
                try {
                    xrService.getORSession().executeSQL(sqlString);
                } catch (Exception xx) {
                    v.value = 1;
                    return v;
                }
            }
        }
        v.value = 0;
        return v;
    }
    
    /**
     * No validation can be done for a batch query operation.
     * 
     * @Override
     */
    public void validate(XRServiceAdapter xrService) {}
    
    /**
     * The initialize method will add an XMLDesctriptor for
     * org.eclipse.persistence.internal.xr.ValueObject to
     * the OX project.  This class is used to hold the
     * result of the batch SQL execution. 
     * 
     * @Override
     */
    public void initialize(XRServiceAdapter xrService) {
        addValueObjectDescriptor(xrService);
    }
}