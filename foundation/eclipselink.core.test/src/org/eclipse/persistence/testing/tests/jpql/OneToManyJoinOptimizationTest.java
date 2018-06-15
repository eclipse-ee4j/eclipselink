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
package org.eclipse.persistence.testing.tests.jpql;

import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReadObjectQuery;

/**
 * Tests one to many joins for EJBQL statement and ensures the correct
 * number of tables generated in the SQL statement.
 *
 * Fix for bug 2690488
 *
 * @author Guy Pelletier
 */
public class OneToManyJoinOptimizationTest extends JPQLTestCase {
    public OneToManyJoinOptimizationTest() {
        setDescription("Test the SQL statement generation for one-to-many relationships");
    }

    public void setup() {
        setEjbqlString("SELECT DISTINCT OBJECT(emp) FROM Employee emp, IN(emp.phoneNumbers) pn WHERE emp.firstName = 'Jill' AND pn.type = 'Work' AND pn.areaCode = '613'");

        // need to set our original object
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression exp = builder.get("firstName").equalsIgnoreCase("Jill");
        exp = exp.and(builder.anyOf("phoneNumbers").get("type").equalsIgnoreCase("Work"));
        exp = exp.and(builder.anyOf("phoneNumbers").get("areaCode").equalsIgnoreCase("613"));

        ReadObjectQuery query = new ReadObjectQuery();
        query.setReferenceClass(Employee.class);
        query.setSelectionCriteria(exp);
        setOriginalOject(getSession().executeQuery(query));

        super.setup();
    }

    // test will run the string from the super class
    public void verify() throws Exception {
        // this will test that we received the correct object back
        super.verify();

        // If we have generated too many phone tables there will be a t3
        // in the sqlString. Check for it. Finding it means our expression
        // generated incorrectly.
        String sqlString = getQuery().getSQLString();

        if (sqlString.indexOf(" t3") > -1) {
            throw new TestErrorException("Too many tables generated in SQL. Found extra PHONE table");
        }

        // no exceptions thrown at this point ... we're golden!
    }
}
