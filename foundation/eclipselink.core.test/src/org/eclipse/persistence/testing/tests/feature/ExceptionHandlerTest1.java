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
package org.eclipse.persistence.testing.tests.feature;

import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

/**
 *    To test the functionality of ExceptionHandler.
 *    ExceptionHandler can catch errors that occur on queries or during database access.
 *    The exception handler has the option of re-throwing the exception, throwing a different
 *    exception or re-trying the query or database operation.
 *  This test handles query errors.
 */
public class ExceptionHandlerTest1 extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    public ExceptionHandlerTest1() {
        setDescription(" To rewrite the query.");
    }

    public void test() {
        Expression exp = new ExpressionBuilder().get("addres").get("province").equal("ONT");
        Handler handler = new Handler();
        getSession().setExceptionHandler(handler);
        try {
            Object result = getSession().readObject(Employee.class, exp);
        } finally {
            getSession().setExceptionHandler(null);
        }
    }
}
