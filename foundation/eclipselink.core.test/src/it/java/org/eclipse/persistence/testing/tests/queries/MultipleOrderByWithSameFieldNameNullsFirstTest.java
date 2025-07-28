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
package org.eclipse.persistence.testing.tests.queries;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

/**
 * Test the ordering feature using multiple orderings with the same field name.
 */
public class MultipleOrderByWithSameFieldNameNullsFirstTest extends AutoVerifyTestCase {

    public MultipleOrderByWithSameFieldNameNullsFirstTest() {
        setDescription("This test verifies the ordering feature works properly with multiple orderings with the same field name.");
    }

    @Override
    public void setup() {
        if (!getSession().getPlatform().isOracle()) {
            throwWarning("NULLS FIRST only supported on Oracle.");
        }
    }

    @Override
    public void test() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression orderExpr1 = builder.get("address").get("street").ascending().nullsFirst();
        Expression orderExpr2 = builder.get("manager").get("address").get("street").ascending().nullsLast();
        Expression expr = builder.anyOf("projects").get("id").equal(2193);

        ReadAllQuery query = new ReadAllQuery(Employee.class, expr);
        query.addOrdering(orderExpr1);
        query.addOrdering(orderExpr2);

        getSession().executeQuery(query);
    }
}
