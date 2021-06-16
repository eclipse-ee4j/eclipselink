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
package org.eclipse.persistence.testing.tests.validation;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReadAllQuery;


/**
 * Tests invlaid query key exception.
 */
public class InvalidQueryKeyTest extends ExceptionTest {
    public InvalidQueryKeyTest() {
        super();
        setDescription("This tests the generation of an invalid query key exception");
    }

    /**
     * This test expects a QueryException.INVALID_QUERY_KEY_IN_EXPRESSION TO BE THROWN
     */
    public void setup() {
        expectedException = org.eclipse.persistence.exceptions.QueryException.invalidQueryKeyInExpression(null);
    }

    public void test() {
        // perform test(s)
        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(org.eclipse.persistence.testing.models.employee.domain.Employee.class);

        ExpressionBuilder employee = new ExpressionBuilder();
        Expression exp = employee.get("firstName").get("bob").equal("foo");

        query.setSelectionCriteria(exp);
        try {
            Object o = getSession().executeQuery(query);
        } catch (org.eclipse.persistence.exceptions.EclipseLinkException te) {
            caughtException = te;
        } catch (Exception e) {
            throw new org.eclipse.persistence.testing.framework.TestException("Caught exception " + e.toString() + "running test " + getName());
        }
    }
}
