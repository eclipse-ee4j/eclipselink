/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Tomas Kraus - Initial API and implementation.
package org.eclipse.persistence.testing.tests.expressions;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;

/**
 * Test ExpressionBuilder class and instance methods.
 */
public class ExpressionBuilderTestSuite extends TestCase {

    public ExpressionBuilderTestSuite() {
        super();
        setName(getName());
        setDescription("Test ExpressionBuilder class and instance methods");
    }

    public void setup() {
    }

    // Execute all tests in suite.
    public void test() {
        testUnitOfWorkInExpressionBuilder();
    }

    // Bug# 429232 - Instance of UnitOfWork is stored in ExpressionBuilder inside ConcurrentFixedCache
    // Make sure that [ExpressionBuilder].setSession() will not store UnitOfWork inside builder even
    // when UnitOfWork is passed as an argument.
    public void testUnitOfWorkInExpressionBuilder() {
        ExpressionBuilder builder = new ExpressionBuilder();
        UnitOfWorkImpl uow = (UnitOfWorkImpl)getSession().acquireUnitOfWork();
        builder.setSession(uow);
        AbstractSession session = builder.getSession();
        if (session instanceof UnitOfWork) {
            throw new TestErrorException("Session stored in ExpressionBuilder shall not be UnitOfWork.");
        }
    }

}
