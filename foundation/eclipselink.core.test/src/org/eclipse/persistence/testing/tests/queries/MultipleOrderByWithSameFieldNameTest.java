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
package org.eclipse.persistence.testing.tests.queries;

import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

/**
 * Test the ordering feature using multiple orderings with the same field name.
 */
public class MultipleOrderByWithSameFieldNameTest extends AutoVerifyTestCase {

    public MultipleOrderByWithSameFieldNameTest() {
        setDescription("This test verifies the ordering feature works properly with multiple orderings with the same field name.");
    }

    public void test() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression orderExpr1 = builder.get("address").get("street").ascending();
        Expression orderExpr2 = builder.get("manager").get("address").get("street").ascending();
        Expression expr = builder.anyOf("projects").get("id").equal(2193);

        ReadAllQuery query = new ReadAllQuery(Employee.class, expr);
        query.addOrdering(orderExpr1);
        query.addOrdering(orderExpr2);

        getSession().executeQuery(query);
    }
}
