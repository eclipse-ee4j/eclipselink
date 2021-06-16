/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
