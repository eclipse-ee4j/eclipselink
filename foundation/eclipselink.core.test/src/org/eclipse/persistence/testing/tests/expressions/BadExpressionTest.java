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
package org.eclipse.persistence.testing.tests.expressions;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.exceptions.*;

/**
 * Generic test to ensure that the correct exceptions are being thrown for bad expressions.
 * @author Peter O'Blenis
 * @date Feb. 3/99
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
