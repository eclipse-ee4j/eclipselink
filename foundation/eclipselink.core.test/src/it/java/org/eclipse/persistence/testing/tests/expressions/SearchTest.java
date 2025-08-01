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
package org.eclipse.persistence.testing.tests.expressions;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

import java.util.Vector;

/**
 * Generic test to check if any results were returned as the result of a expression query.
 * If result vector is empty then throw an exception with specified message.
 * @author Peter O'Blenis
 */
public class SearchTest extends AutoVerifyTestCase {
    // Expression to test
    private Expression m_expression = null;

    // Error Message
    private String m_szErrorMessage = null;

    public SearchTest() {
        setDescription("Test the wrong query keys are error handled correctly.");
    }

    public void setErrorMessage(String szMsg) {
        m_szErrorMessage = szMsg;
    }

    public void setExpression(Expression expression) {
        m_expression = expression;
    }

    @Override
    public void test() {
        if ((m_expression == null) || (m_szErrorMessage == null)) {
            throw new RuntimeException("Search Test was invoked without setting all parameters");
        }

        Vector results = getSession().readAllObjects(Employee.class, m_expression);
        if (results.isEmpty()) {
            throw new TestErrorException(m_szErrorMessage);
        }
    }
}
