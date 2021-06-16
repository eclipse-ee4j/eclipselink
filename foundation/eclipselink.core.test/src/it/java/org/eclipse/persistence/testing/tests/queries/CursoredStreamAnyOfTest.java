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

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;

public class CursoredStreamAnyOfTest extends TestCase {
    public CursoredStreamAnyOfTest() {
        setDescription("Verify the size works correctly with distinct through anyOf");
    }

    public void setup() {
        // Access does not like this distinct.
        if (getSession().getPlatform().isAccess()) {
            throw new TestWarningException("Access does not support distinct in counts.");
        }
    }

    public void test() {
        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(Employee.class);
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression exp = builder.anyOf("managedEmployees").get("salary").greaterThan(0);
        query.setSelectionCriteria(exp);
        query.useCursoredStream(1, 1);
        CursoredStream stream = (CursoredStream)getSession().executeQuery(query);
        try {
            stream.read(1);
            if (stream.size() != (getSession().readAllObjects(Employee.class, exp)).size()) {
                throw new TestErrorException("Size is incorrect");
            }
        } finally {
            stream.close();
        }


        query = new ReadAllQuery();
        query.setReferenceClass(org.eclipse.persistence.testing.models.legacy.Employee.class);
        builder = new ExpressionBuilder();
        exp = builder.anyOf("shipments").get("shipmentNumber").equal(10000);
        query.setSelectionCriteria(exp);
        query.useCursoredStream(1, 1);
        stream = (CursoredStream)getSession().executeQuery(query);
        try {
            if (stream.size() !=
                (getSession().readAllObjects(org.eclipse.persistence.testing.models.legacy.Employee.class, exp)).size()) {
                throw new TestErrorException("Size is incorrect");
            }
        } finally {
            stream.close();
        }
    }
}
