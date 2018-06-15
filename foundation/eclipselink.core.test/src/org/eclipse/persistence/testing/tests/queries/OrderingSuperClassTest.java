/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.testing.tests.queries;

import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.testing.models.employee.domain.Project;

public class OrderingSuperClassTest extends OrderingMutipleTableTest {
    protected void setup() {
        customSQLRows = getSession().executeSelectingCall(new org.eclipse.persistence.queries.SQLCall(
                "SELECT t0.PROJ_NAME FROM PROJECT t0 ORDER BY t0.PROJ_NAME"));
    }

    public void test() {
        ReadAllQuery query = new ReadAllQuery();
        query.addAscendingOrdering("name");
        query.setReferenceClass(Project.class);

        orderedQueryObjects = executeOrderingQuery(Project.class, "name");

    }

}
