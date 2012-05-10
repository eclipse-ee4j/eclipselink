/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.expressions;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.exceptions.*;

/**
 * Specific test to verify that invalid querykeys in function expressions do not cause NullPointerExceptions.
 * BUG # 2956674
 * @author Gordon Yorke
 */
public class InvalidQueryKeyFunctionExpressionTest extends AutoVerifyTestCase {
    // Expression to test
    private Expression m_expression = null;

    public InvalidQueryKeyFunctionExpressionTest(Expression expression) {
        setDescription("Test the wrong query keys are error handled correctly. bug #2956674");
        this.m_expression = expression;
    }

    public void test() {
        try {
            getSession().readAllObjects(Employee.class, m_expression);
        } catch (EclipseLinkException exception) {
            if (exception.getErrorCode() != QueryException.INVALID_QUERY_KEY_IN_EXPRESSION) {
                throw exception;
            }
        }
    }
}
