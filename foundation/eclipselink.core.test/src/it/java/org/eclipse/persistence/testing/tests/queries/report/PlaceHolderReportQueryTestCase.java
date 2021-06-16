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
package org.eclipse.persistence.testing.tests.queries.report;

import java.util.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.queries.*;

/**
 * ReportQuery test for Scenario 1.1
 * SELECT F_NAME, L_NAME FROM EMPLOYEE
 */
public class PlaceHolderReportQueryTestCase extends TestCase {
    protected ReportQuery reportQuery;
    protected Vector expectedResults;
    protected Vector results;

    protected void setup() {
        results = new Vector();
        expectedResults = new Vector();
    }

    public void test() {
        try {
            reportQuery = new ReportQuery(new ExpressionBuilder());
            reportQuery.setReferenceClass(Employee.class);
            reportQuery.addAttribute("firstName");
            reportQuery.addAttribute("NullPlaceHolder", null);
            reportQuery.addAttribute("lastName");
            results = (Vector)getSession().executeQuery(reportQuery);
        } catch (org.eclipse.persistence.exceptions.QueryException exception) {
            throw new TestErrorException("Exception thrown because row size check based on total result size, including place holders, not expected fields; see CR 4240");
        }
    }
}
