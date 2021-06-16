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
package org.eclipse.persistence.testing.tests.queries.repreparation;

import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import java.util.Vector;

public class UseDistinctTest extends AutoVerifyTestCase {
    ReportQuery reportQuery;
    Vector results;

    public UseDistinctTest() {
        setDescription("Test if SQL is reprepared the second time");
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        reportQuery = new ReportQuery(new ExpressionBuilder());
        reportQuery.setReferenceClass(Employee.class);
        reportQuery.addAttribute("firstName");
        results = (Vector)getSession().executeQuery(reportQuery);
    }

    public void test() {
        reportQuery.useDistinct();
        results = (Vector)getSession().executeQuery(reportQuery);
    }

    public void verify() {
        if (!reportQuery.getCall().getSQLString().equals("SELECT DISTINCT t0.F_NAME FROM EMPLOYEE t0, SALARY t1 WHERE (t1.EMP_ID = t0.EMP_ID)")) {
            throw new org.eclipse.persistence.testing.framework.TestErrorException("UseDistinctTest failed.");
        }
    }
}
