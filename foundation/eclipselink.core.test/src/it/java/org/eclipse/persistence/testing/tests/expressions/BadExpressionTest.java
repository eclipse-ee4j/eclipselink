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

import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

/**
 * Generic test to ensure that the correct exceptions are being thrown for bad expressions.
 * @author Peter O'Blenis
 */
public class BadExpressionTest extends AutoVerifyTestCase {
    // Expression to test
    private Expression m_expression = null;

    // Set exception code
    private int m_nExceptionCode = -1;

    public BadExpressionTest() {
        setDescription("Test the wrong query keys are error handled correctly.");
    }

    public void setExceptionCode(int nCode) {
        m_nExceptionCode = nCode;
    }

    public void setExpression(Expression expression) {
        m_expression = expression;
    }

    @Override
    public void test() {
        if ((m_expression == null) || (m_nExceptionCode == -1)) {
            throw new RuntimeException("BadExpressionTest was invoked without setting all parameters");
        }

        try {
            getSession().readAllObjects(Employee.class, m_expression);
        } catch (EclipseLinkException exception) {
            if (exception.getErrorCode() != m_nExceptionCode) {
                throw exception;
            }
        }
    }
}
