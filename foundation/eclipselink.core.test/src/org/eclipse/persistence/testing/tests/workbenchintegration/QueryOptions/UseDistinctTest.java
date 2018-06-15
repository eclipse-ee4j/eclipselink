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
package org.eclipse.persistence.testing.tests.workbenchintegration.QueryOptions;

import java.util.Vector;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;


public class UseDistinctTest extends AutoVerifyTestCase {
    private Vector employees;
    private ReadAllQuery query;

    public UseDistinctTest() {
        setDescription("Test use distinct option");
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void setup() {
    }

    public void test() {
        query =
                (ReadAllQuery)getSession().getDescriptor(org.eclipse.persistence.testing.models.employee.domain.Employee.class).getQueryManager().getQuery("useDistinctQuery");
        ReadAllQuery queryCopy = (ReadAllQuery)query.clone();
        queryCopy.setSelectionCriteria(new ExpressionBuilder().anyOf("phoneNumbers").get("areaCode").equal("613"));
        employees = (Vector)getSession().executeQuery(queryCopy);
    }

    public void verify() {
        if (employees.size() != 10) {
            throw new org.eclipse.persistence.testing.framework.TestErrorException("ReadAllQuery with useDistinct test failed. Mismatched objects returned");
        }
    }
}
