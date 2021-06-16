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


// TopLink imports
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;

public class MultiNameQueriesTestCase extends AutoVerifyTestCase {
    public ReadAllQuery getNamedQueryFirstAndLastName() {
        ExpressionBuilder emp = new ExpressionBuilder();
        Expression firstNameExpression = emp.get("firstName").equal(emp.getParameter("firstName"));
        Expression lastNameExpression = emp.get("lastName").equal(emp.getParameter("lastName"));
        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(Employee.class);
        query.setSelectionCriteria(firstNameExpression.and(lastNameExpression));
        query.addArgument("firstName", java.lang.String.class);
        query.addArgument("lastName", java.lang.String.class);

        return query;
    }

    // end of getNamedQueryFirstAndLastName
    public ReadAllQuery getNamedQueryFirstName() {
        ExpressionBuilder emp = new ExpressionBuilder();
        Expression firstNameExpression = emp.get("firstName").equal(emp.getParameter("firstName"));
        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(Employee.class);
        query.setSelectionCriteria(firstNameExpression);
        query.addArgument("firstName", java.lang.String.class);

        return query;
    }

    // end of getNamedQueryFirstName
    public ReadAllQuery getNamedQueryFirstAndLastNameNoArguments() {
        ExpressionBuilder emp = new ExpressionBuilder();
        Expression firstNameExpression = emp.get("firstName").equal("Jill");
        Expression lastNameExpression = emp.get("lastName").equal("May");
        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(Employee.class);
        query.setSelectionCriteria(firstNameExpression.and(lastNameExpression));

        return query;
    }
    // end of getNamedQueryFirstAndLastNameNoArguments
}// end of public class MultiNameQueriesTestCase
