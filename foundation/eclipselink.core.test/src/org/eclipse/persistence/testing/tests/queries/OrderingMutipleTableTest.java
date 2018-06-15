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

import java.util.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.sessions.*;

/**
 * Test the ordering feature.
 */
public class OrderingMutipleTableTest extends OrderingTest {
    protected void setup() {
        customSQLRows = getSession().executeSelectingCall(new org.eclipse.persistence.queries.SQLCall("SELECT t1.PROJ_NAME FROM PROJECT t1, LPROJECT t2 WHERE t1.PROJ_ID = t2.PROJ_ID ORDER BY t2.BUDGET"));
    }

    public void test() {
        orderedQueryObjects = executeOrderingQuery(LargeProject.class, "budget");
    }

    protected Vector executeOrderingQuery(Class class1, String orderField) {
        ReadAllQuery query = new ReadAllQuery();
        query.addAscendingOrdering(orderField);
        query.setReferenceClass(class1);

        return (Vector)getSession().executeQuery(query);
    }

    protected void verify() {
        Record row;
        org.eclipse.persistence.testing.models.employee.domain.Project project;
        String name;

        for (int i = 0; i < orderedQueryObjects.size(); i++) {
            row = (Record)customSQLRows.elementAt(i);
            project = (org.eclipse.persistence.testing.models.employee.domain.Project)orderedQueryObjects.elementAt(i);
            name = (String)row.get("PROJ_NAME");

            if (!(project.getName().equals(name))) {
                throw new TestErrorException("The ordering test failed.  The results are not in the right order");
            }
        }
    }
}
