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

import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.tests.validation.ExceptionTest;

//Bug#2839852  Refreshing is not possible if the query uses checkCacheOnly.
public class RefreshWithCheckCacheOnlyTest extends ExceptionTest {
    public RefreshWithCheckCacheOnlyTest() {
        setDescription("Tests if refreshNotPossibleWithCheckCacheOnly exception would be thrown");
    }

    @Override
    protected void setup() {
        expectedException = org.eclipse.persistence.exceptions.QueryException.refreshNotPossibleWithCheckCacheOnly(null);
    }

    @Override
    protected void test() {
        try {
            ReadAllQuery query = new ReadAllQuery(Employee.class);
            ExpressionBuilder emp = new ExpressionBuilder(Employee.class);
            Expression expression = emp.get("firstName").equal("Bob").or(emp.get("firstName").equal("Jill")).or(emp.get("firstName").equal("John"));
            query.setSelectionCriteria(expression);
            query.checkCacheOnly();
            query.refreshIdentityMapResult();
            getSession().executeQuery(query);
        } catch (EclipseLinkException ex) {
            caughtException = ex;
        }
    }
}
