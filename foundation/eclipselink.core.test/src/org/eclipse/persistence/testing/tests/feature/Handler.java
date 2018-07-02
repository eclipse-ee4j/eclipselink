/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.feature;

import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.expressions.*;

/**
 *  Test the functionality of ExceptionHandler.
 *    ExceptionHandler can catch errors that occur on queries or during database access.
 *    The exception handler has the option of re-throwing the exception, throwing a different
 *    exception or re-trying the query or database operation.
 */
public class Handler implements ExceptionHandler, java.io.Serializable {

    /**
     * PUBLIC:
     */
    public Handler() {
        super();
    }

    /**
     * PUBLIC:
     * To test the functionality of ExceptionHandler.
     * Handles QueryException and DatabaseException.
     */
    public Object handleException(RuntimeException exception) {
        if (exception instanceof QueryException) {
            QueryException queryException = (QueryException)exception;
            Expression exp = new ExpressionBuilder().get("address").get("province").equal("ONT");
            queryException.getQuery().setSelectionCriteria(exp);
            return queryException.getSession().executeQuery(queryException.getQuery());
        } else if (exception instanceof DatabaseException) {
            DatabaseException databaseException = (DatabaseException)exception;
            databaseException.getAccessor().disconnect(databaseException.getSession());
            databaseException.getAccessor().reestablishConnection(databaseException.getSession());
            if (databaseException.getQuery().getSQLString().equals("select * from employee")) {
                throw exception;
            }
            databaseException.getQuery().setSQLString("select * from EMPLOYEE");
            return databaseException.getSession().executeQuery(databaseException.getQuery());

        }
        return null;
    }
}
