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
package org.eclipse.persistence.testing.tests.queries.report;

import java.util.*;

import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

/**
 * ReportQuery test for Scenario 1.1
 * SELECT F_NAME, L_NAME FROM EMPLOYEE
 */
public class Scenario1_5b extends ReportQueryTestCase {
    public Scenario1_5b() {
        setDescription("DTF mappings of subclass w/ WHERE clause");
    }

    protected void buildExpectedResults() {
        Vector projects = getSession().readAllObjects(LargeProject.class);

        for (Enumeration e = projects.elements(); e.hasMoreElements(); ) {
            LargeProject project = (LargeProject)e.nextElement();
            if (project.getBudget() > 4000) {
                Object[] result = new Object[2];
                result[0] = project.getName();
                result[1] = new Double(project.getBudget());
                addResult(result, null);
            }
        }
    }

    protected void setup() throws Exception {
        super.setup();
        reportQuery = new ReportQuery(new ExpressionBuilder());

        reportQuery.setReferenceClass(LargeProject.class);
        reportQuery.addAttribute("name");
        reportQuery.addAttribute("budget");
        reportQuery.setSelectionCriteria(reportQuery.getExpressionBuilder().get("budget").greaterThan(4000));
    }
}
