/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.queries.optimization;

import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

/**
 * Verify that the correct exceptions are thrown for invalid join expressions.
 * Batch and partial should also be tested, but currently these are not validating correctly.
 */
public class QueryValidationTest extends AutoVerifyTestCase {
    public QueryValidationTest() {
        setDescription("Verify that the correct exceptions are thrown for invalid join expressions.");
    }

    @Override
    public void test() {
        boolean exceptionOccured = false;
        //removed many to many join test as this is now supported

        try {
            ReadAllQuery query = new ReadAllQuery();
            query.setReferenceClass(Employee.class);
            query.addJoinedAttribute(query.getExpressionBuilder().get("id"));
            getSession().executeQuery(query);
        } catch (QueryException exception) {// validating
            exceptionOccured = true;
        }

        if (!exceptionOccured) {
            throw new TestErrorException("Invalid join expression not validated.");
        }

        try {
            ReadAllQuery query = new ReadAllQuery();
            query.setReferenceClass(Employee.class);
            query.addJoinedAttribute(query.getExpressionBuilder().get("id").maximum());
            getSession().executeQuery(query);
        } catch (QueryException exception) {// validating
            exceptionOccured = true;
        }

        if (!exceptionOccured) {
            throw new TestErrorException("Invalid join expression not validated.");
        }

        /** Batch and partial must also validate, currently they do not.
        try {
            ReadAllQuery query = new ReadAllQuery();
            query.setReferenceClass(Employee.class);
            query.addBatchReadAttribute(query.getExpressionBuilder().get("id"));
            getSession().executeQuery(query);
        } catch (QueryException exception) {// validating
            exceptionOccured = true;
        }

        if (! exceptionOccured) {
            throw new TestErrorException("Invalid batch expression not validated.");
        }*/
    }
}
