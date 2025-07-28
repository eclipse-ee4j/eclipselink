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

import org.eclipse.persistence.queries.CursoredStream;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ValueReadQuery;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

public class CursoredStreamCustomSQLTest extends CursoredStreamTest {

    public CursoredStreamCustomSQLTest() {
        super(Employee.class, null);
    }

    @Override
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

    @Override
    protected void verify() {
    }
}
