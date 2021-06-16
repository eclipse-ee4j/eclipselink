/*
 * Copyright (c) 2018, 2021 Oracle and/or its affiliates. All rights reserved.
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
