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

import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.queries.*;

/**
 * ReportQuery test for Scenario 1.1
 * SELECT F_NAME, L_NAME FROM EMPLOYEE
 */
public class Scenario1_5a extends ReportQueryTestCase {
    public Scenario1_5a() {
        setDescription("DTF mappings of subclass");
    }

    protected void buildExpectedResults() {
        Vector projects = getSession().readAllObjects(LargeProject.class);

        for (Enumeration e = projects.elements(); e.hasMoreElements(); ) {
            LargeProject project = (LargeProject)e.nextElement();
            Object[] result = new Object[2];
            result[0] = project.getName();
            result[1] = new Double(project.getBudget());
            addResult(result, null);
        }
    }

    protected void setup() throws Exception {
        super.setup();
        reportQuery = new ReportQuery(new ExpressionBuilder());

        reportQuery.setReferenceClass(LargeProject.class);
        reportQuery.addAttribute("name");
        reportQuery.addAttribute("budget");
    }
}
