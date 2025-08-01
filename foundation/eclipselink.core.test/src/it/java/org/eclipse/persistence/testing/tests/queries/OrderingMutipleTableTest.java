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

import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.sessions.DataRecord;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.LargeProject;

import java.util.Vector;

/**
 * Test the ordering feature.
 */
public class OrderingMutipleTableTest extends OrderingTest {
    @Override
    protected void setup() {
        customSQLRows = getSession().executeSelectingCall(new org.eclipse.persistence.queries.SQLCall("SELECT t1.PROJ_NAME FROM PROJECT t1, LPROJECT t2 WHERE t1.PROJ_ID = t2.PROJ_ID ORDER BY t2.BUDGET"));
    }

    @Override
    public void test() {
        orderedQueryObjects = executeOrderingQuery(LargeProject.class, "budget");
    }

    protected Vector executeOrderingQuery(Class<?> class1, String orderField) {
        ReadAllQuery query = new ReadAllQuery();
        query.addAscendingOrdering(orderField);
        query.setReferenceClass(class1);

        return (Vector)getSession().executeQuery(query);
    }

    @Override
    protected void verify() {
        DataRecord row;
        org.eclipse.persistence.testing.models.employee.domain.Project project;
        String name;

        for (int i = 0; i < orderedQueryObjects.size(); i++) {
            row = (DataRecord)customSQLRows.get(i);
            project = (org.eclipse.persistence.testing.models.employee.domain.Project)orderedQueryObjects.get(i);
            name = (String)row.get("PROJ_NAME");

            if (!(project.getName().equals(name))) {
                throw new TestErrorException("The ordering test failed.  The results are not in the right order");
            }
        }
    }
}
