/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
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
