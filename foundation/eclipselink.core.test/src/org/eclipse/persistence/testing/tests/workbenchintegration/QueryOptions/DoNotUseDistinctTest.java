/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.workbenchintegration.QueryOptions;

import java.util.Vector;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;


public class DoNotUseDistinctTest extends AutoVerifyTestCase {
    private Vector employees;
    private ReadAllQuery query;

    public DoNotUseDistinctTest() {
        setDescription("Test use distinct option");
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void setup() {
    }

    public void test() {
        query =
                (ReadAllQuery)getSession().getDescriptor(org.eclipse.persistence.testing.models.employee.domain.Employee.class).getQueryManager().getQuery("doNotUseDistinctQuery");
        ReadAllQuery queryCopy = (ReadAllQuery)query.clone();
        queryCopy.setSelectionCriteria(new ExpressionBuilder().anyOf("phoneNumbers").get("areaCode").equal("613"));
        employees = (Vector)getSession().executeQuery(queryCopy);
    }

    public void verify() {
        if (employees.size() != 13) {
            throw new org.eclipse.persistence.testing.framework.TestErrorException("ReadAllQuery with doNotUseDistinct test failed. Mismatched objects returned");
        }
    }
}
