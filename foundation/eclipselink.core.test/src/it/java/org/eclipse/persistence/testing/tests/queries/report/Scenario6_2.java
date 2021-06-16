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
public class Scenario6_2 extends ReportQueryTestCase {
    public Scenario6_2() {
        setDescription("Join query");
    }

    protected void buildExpectedResults() throws Exception {
        ExpressionBuilder builder = new ExpressionBuilder();

        Vector phones = getSession().readAllObjects(PhoneNumber.class);

        for (Enumeration p = phones.elements(); p.hasMoreElements();) {
            PhoneNumber phoneNum = (PhoneNumber)p.nextElement();
            Object[] result = new Object[2];
            result[0] = phoneNum.getNumber();
            result[1] = phoneNum.getOwner().getFirstName();
            addResult(result, null);
        }
    }
protected void setup()  throws Exception
{
        super.setup();
        reportQuery = new ReportQuery(new ExpressionBuilder());

        reportQuery.setReferenceClass(PhoneNumber.class);
        reportQuery.addAttribute("number");
        reportQuery.addAttribute("firstName", reportQuery.getExpressionBuilder().get("owner").get("firstName"));
    }
}
