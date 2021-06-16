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
package org.eclipse.persistence.testing.tests.jpql;

import java.util.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

public class WhitespaceTest extends JPQLTestCase {
    public static WhitespaceTest getEntityWSTest1() {
        WhitespaceTest test = new WhitespaceTest();
        test.setName("Entity Test 1");
        test.setEjbqlString("SELECT OBJECT(   emp   ) FROM Employee emp");

        return test;
    }

    public static WhitespaceTest getEntityWSTest2() {
        WhitespaceTest test = new WhitespaceTest();
        test.setName("Entity Test 2");
        test.setEjbqlString("SELECT OBJECT (   emp   ) FROM Employee emp");

        return test;
    }

    public static WhitespaceTest getInWSTest1() {
        WhitespaceTest test = new WhitespaceTest();
        test.setName("In Test 1");
        test.setEjbqlString("SELECT OBJECT(emp) FROM Employee emp WHERE emp.firstName IN ( \"Jill\",  \"Charles\")");

        Vector ids = new Vector();
        ids.add("Jill");
        ids.add("Charles");

        ExpressionBuilder employee = new ExpressionBuilder();
        Expression whereClause = employee.get("firstName").in(ids);

        test.setOriginalObjectExpression(whereClause);

        return test;
    }

    public static TestSuite getWhitespaceTests() {
        TestSuite suite = new TestSuite();
        suite.setName("White space test suite");

        suite.addTest(getEntityWSTest1());
        suite.addTest(getEntityWSTest2());
        suite.addTest(getInWSTest1());

        return suite;
    }

    public void setup() {
        if (getOriginalObjectExpression() == null) {
            setOriginalOject(getSomeEmployees());
        } else {
            ReadAllQuery raq = new ReadAllQuery();
            raq.setReferenceClass(Employee.class);
            raq.setSelectionCriteria(getOriginalObjectExpression());

            setOriginalOject(getSession().executeQuery(raq));
            getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        }
        super.setup();
    }
}
