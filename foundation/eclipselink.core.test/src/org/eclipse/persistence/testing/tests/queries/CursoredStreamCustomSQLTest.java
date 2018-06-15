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

import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.queries.*;

public class CursoredStreamCustomSQLTest extends CursoredStreamTest {

    public CursoredStreamCustomSQLTest() {
        super(Employee.class, null);
    }

    public void test() {
        // Test custom SQL cursors
        ReadAllQuery query2 = new ReadAllQuery();
        query2.setReferenceClass(Employee.class);
        query2.setSQLString("select E.*, S.* from EMPLOYEE E, SALARY S where E.EMP_ID = S.EMP_ID");
        ValueReadQuery sizeQuery = new ValueReadQuery();
        sizeQuery.setSQLString("select count(*) from EMPLOYEE");
        query2.useCursoredStream(0, 1, sizeQuery);
        CursoredStream stream3 = (CursoredStream)getSession().executeQuery(query2);
        stream3.read(5);
        stream3.size();
        stream3.close();
    }

    protected void verify() {
    }
}
