/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.queries.report;

import java.util.*;

import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

/**
 * ReportQuery test for Scenario 1.1
 * SELECT F_NAME, L_NAME FROM EMPLOYEE
 */
public class Scenario1_4 extends ReportQueryTestCase {
    public Scenario1_4() {
        setDescription("DTF mappings in root class");
    }

    protected void buildExpectedResults() {
        Vector projects = getSession().readAllObjects(Project.class);

        for (Enumeration e = projects.elements(); e.hasMoreElements(); ) {
            Project project = (Project)e.nextElement();
            if (project.getDescription().indexOf("EclipseLink") != -1) {
                Object[] result = new Object[1];
                result[0] = project.getName();
                addResult(result, null);
            }
        }
    }

    protected void setup() throws Exception {
        super.setup();
        reportQuery = new ReportQuery(new ExpressionBuilder());

        reportQuery.setReferenceClass(Project.class);
        reportQuery.addAttribute("name");
        reportQuery.setSelectionCriteria(reportQuery.getExpressionBuilder().get("description").like("%EclipseLink%"));
    }
}
