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

import org.eclipse.persistence.queries.*;
import java.util.*;

public class OneToManyMaxRowsSettingTest extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    private Vector employees;
    private Vector ssns;

    public OneToManyMaxRowsSettingTest() {
        setDescription("Tests setup the limit for the maximum number of rows the query returns");
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void setup() {
    }

    public void test() {
        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(org.eclipse.persistence.testing.models.employee.domain.Employee.class);
        query.setMaxRows(5);
        employees = (Vector)getSession().executeQuery(query);

        DirectReadQuery query2 = new DirectReadQuery();
        query2.setSQLString("SELECT SSN FROM HOLDER");
        query2.setMaxRows(3);
        ssns = (Vector)getSession().executeQuery(query2);
    }

    public void verify() {
        if (employees.size() != 5) {
            throw new org.eclipse.persistence.testing.framework.TestErrorException("ReadAllQuery with setMaxRows test failed. mismatched objects returned");
        }

        if (ssns.size() != 3) {
            throw new org.eclipse.persistence.testing.framework.TestErrorException("DirectReadQuery with setMaxRows test fails, mismatched objects returned");
        }
    }
}
