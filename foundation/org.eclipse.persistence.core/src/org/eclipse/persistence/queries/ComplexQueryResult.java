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
package org.eclipse.persistence.queries;


/**
 * <p><b>Purpose</b>:
 * Used to return multiple sets of information from a query.
 * This is used if the objects and rows are required to be returned.
 *
 * <p><b>Responsibilities</b>:
 * Hold both the result of the query and the row results.
 *
 * @author James Sutherland
 * @since TOPLink/Java 3.0
 */
public class ComplexQueryResult {
    protected Object result;
    protected Object data;

    /**
     * PUBLIC:
     * Return the database rows for the query result.
     */
    public Object getData() {
        return data;
    }

    /**
     * PUBLIC:
     * Return the result of the query.
     */
    public Object getResult() {
        return result;
    }

    /**
     * INTERNAL:
     * Set the database rows for the query result.
     */
    public void setData(Object data) {
        this.data = data;
    }

    /**
     * INTERNAL:
     * Set the result of the query.
     */
    public void setResult(Object result) {
        this.result = result;
    }
}
