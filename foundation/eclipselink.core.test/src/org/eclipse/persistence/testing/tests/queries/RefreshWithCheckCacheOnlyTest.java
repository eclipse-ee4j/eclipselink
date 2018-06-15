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

import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.testing.tests.validation.ExceptionTest;
import org.eclipse.persistence.exceptions.*;

//Bug#2839852  Refreshing is not possible if the query uses checkCacheOnly.
public class RefreshWithCheckCacheOnlyTest extends ExceptionTest {
    public RefreshWithCheckCacheOnlyTest() {
        setDescription("Tests if refreshNotPossibleWithCheckCacheOnly exception would be thrown");
    }

    protected void setup() {
        expectedException = org.eclipse.persistence.exceptions.QueryException.refreshNotPossibleWithCheckCacheOnly(null);
    }

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
