/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2021 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
//     05/07/2009-1.1.1 Guy Pelletier
//       - 263904: [PATCH] ExpressionOperator doesn't compare arrays correctly
package org.eclipse.persistence.testing.tests.expressions;

import java.util.*;
import org.eclipse.persistence.tools.schemaframework.PopulationManager;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.framework.*;

public class ExpressionOperatorUnitTestSuite extends ExpressionTestSuite {
    public ExpressionOperatorUnitTestSuite() {
        setDescription("This suite tests ExpressionOperator.");
    }

    public void _testEquals$nullTest() {
        ExpressionOperator operator = ExpressionOperator.getOperator(ExpressionOperator.Between);
        ExpressionOperator operator2 = null;
        if (operator.equals(operator2)) {
            throw new TestErrorException("Equals() must handle null case.");
        }
    }

    public void _testEquals$ObjectTest() {
        ExpressionOperator operator = ExpressionOperator.getOperator(ExpressionOperator.Between);
        Object operator2 = 5;
        if (operator.equals(operator2)) {
            throw new TestErrorException("Equals() must handle other class case.");
        }
    }

    public void _testEqualsTest() {
        ExpressionOperator operator = ExpressionOperator.getOperator(ExpressionOperator.Between);
        ExpressionOperator operator2 = new ExpressionOperator(ExpressionOperator.Between, new Vector());
        if (!operator.equals(operator2)) {
            throw new TestErrorException("Equals() must do comparison by selector only.");
        }
    }

    public void _testEqualsArrayTest() {
        Vector dbStrings = new Vector();
        dbStrings.add("one");
        dbStrings.add("two");

        ExpressionOperator operator1 = new ExpressionOperator(0, dbStrings);
        ExpressionOperator operator2 = new ExpressionOperator(0, dbStrings);

        if (!operator1.equals(operator2)) {
            throw new TestErrorException("Equals() must do comparison by database strings.");
        }
    }

    public void _testIsComparisonOperatorTest() {
        ExpressionOperator operator = ExpressionOperator.getOperator(ExpressionOperator.Between);
        if (!operator.isComparisonOperator()) {
            throw new TestErrorException("IsComparisonOperator() invalid.");
        }
    }

    public void _testIsFunctionOperatorTest() {
        ExpressionOperator operator = ExpressionOperator.getOperator(ExpressionOperator.Not);
        if (!operator.isFunctionOperator()) {
            throw new TestErrorException("IsFunctionOperator() invalid.");
        }
    }

    public void _testIsLogicalOperatorTest() {
        ExpressionOperator operator = ExpressionOperator.getOperator(ExpressionOperator.And);
        if (!operator.isLogicalOperator()) {
            throw new TestErrorException("IsLogicalOperator() invalid.");
        }
    }

    public void _testDefaultCaseOperatorDatabaseStringsTest() {
        ExpressionOperator caseOp = ExpressionOperator.caseStatement();

        String[] databaseStrings = caseOp.getDatabaseStrings(0);
        String[] expectedStrings = new String[] {"CASE ", " END"};
        if(!(Arrays.equals(expectedStrings, databaseStrings))) {
            throw new TestErrorException("Expected " + Arrays.toString(expectedStrings) + 
                    " but was " + Arrays.toString(databaseStrings));
        }

        databaseStrings = caseOp.getDatabaseStrings(1);
        expectedStrings = new String[] {"CASE ", " END"};
        if(!(Arrays.equals(expectedStrings, databaseStrings))) {
            throw new TestErrorException("Expected " + Arrays.toString(expectedStrings) + 
                    " but was " + Arrays.toString(databaseStrings));
        }

        databaseStrings = caseOp.getDatabaseStrings(2);
        expectedStrings = new String[] {"CASE ", " ELSE ", " END"};
        if(!(Arrays.equals(expectedStrings, databaseStrings))) {
            throw new TestErrorException("Expected " + Arrays.toString(expectedStrings) + 
                    " but was " + Arrays.toString(databaseStrings));
        }

        databaseStrings = caseOp.getDatabaseStrings(3);
        expectedStrings = new String[] {"CASE ", " WHEN ", " THEN ", " END"};
        if(!(Arrays.equals(expectedStrings, databaseStrings))) {
            throw new TestErrorException("Expected " + Arrays.toString(expectedStrings) + 
                    " but was " + Arrays.toString(databaseStrings));
        }

        databaseStrings = caseOp.getDatabaseStrings(4);
        expectedStrings = new String[] {"CASE ", " WHEN ", " THEN ", " ELSE ", " END"};
        if(!(Arrays.equals(expectedStrings, databaseStrings))) {
            throw new TestErrorException("Expected " + Arrays.toString(expectedStrings) + 
                    " but was " + Arrays.toString(databaseStrings));
        }

        databaseStrings = caseOp.getDatabaseStrings(5);
        expectedStrings = new String[] {"CASE ", " WHEN ", " THEN ", " WHEN ", " THEN ", " END"};
        if(!(Arrays.equals(expectedStrings, databaseStrings))) {
            throw new TestErrorException("Expected " + Arrays.toString(expectedStrings) + 
                    " but was " + Arrays.toString(databaseStrings));
        }

        databaseStrings = caseOp.getDatabaseStrings(6);
        expectedStrings = new String[] {"CASE ", " WHEN ", " THEN ", " WHEN ", " THEN ", " ELSE ", " END"};
        if(!(Arrays.equals(expectedStrings, databaseStrings))) {
            throw new TestErrorException("Expected " + Arrays.toString(expectedStrings) + 
                    " but was " + Arrays.toString(databaseStrings));
        }
    }

    public void _testDefaultCaseConditionOperatorDatabaseStringsTest() {
        ExpressionOperator caseConditionOp = ExpressionOperator.caseConditionStatement();

        String[] databaseStrings = caseConditionOp.getDatabaseStrings(0);
        String[] expectedStrings = new String[] {"CASE WHEN ", " END "};
        if(!(Arrays.equals(expectedStrings, databaseStrings))) {
            throw new TestErrorException("Expected " + Arrays.toString(expectedStrings) + 
                    " but was " + Arrays.toString(databaseStrings));
        }

        databaseStrings = caseConditionOp.getDatabaseStrings(1);
        expectedStrings = new String[] {"CASE WHEN ", " END "};
        if(!(Arrays.equals(expectedStrings, databaseStrings))) {
            throw new TestErrorException("Expected " + Arrays.toString(expectedStrings) + 
                    " but was " + Arrays.toString(databaseStrings));
        }

        databaseStrings = caseConditionOp.getDatabaseStrings(2);
        expectedStrings = new String[] {"CASE WHEN ", " THEN ", " END "};
        if(!(Arrays.equals(expectedStrings, databaseStrings))) {
            throw new TestErrorException("Expected " + Arrays.toString(expectedStrings) + 
                    " but was " + Arrays.toString(databaseStrings));
        }

        databaseStrings = caseConditionOp.getDatabaseStrings(3);
        expectedStrings = new String[] {"CASE WHEN ", " THEN ", " ELSE ", " END "};
        if(!(Arrays.equals(expectedStrings, databaseStrings))) {
            throw new TestErrorException("Expected " + Arrays.toString(expectedStrings) + 
                    " but was " + Arrays.toString(databaseStrings));
        }

        databaseStrings = caseConditionOp.getDatabaseStrings(4);
        expectedStrings = new String[] {"CASE WHEN ", " THEN ", " WHEN ", " THEN ", " END "};
        if(!(Arrays.equals(expectedStrings, databaseStrings))) {
            throw new TestErrorException("Expected " + Arrays.toString(expectedStrings) + 
                    " but was " + Arrays.toString(databaseStrings));
        }

        databaseStrings = caseConditionOp.getDatabaseStrings(5);
        expectedStrings = new String[] {"CASE WHEN ", " THEN ", " WHEN ", " THEN ", " ELSE ", " END "};
        if(!(Arrays.equals(expectedStrings, databaseStrings))) {
            throw new TestErrorException("Expected " + Arrays.toString(expectedStrings) + 
                    " but was " + Arrays.toString(databaseStrings));
        }

        databaseStrings = caseConditionOp.getDatabaseStrings(6);
        expectedStrings = new String[] {"CASE WHEN ", " THEN ", " WHEN ", " THEN ", " WHEN ", " THEN ", " END "};
        if(!(Arrays.equals(expectedStrings, databaseStrings))) {
            throw new TestErrorException("Expected " + Arrays.toString(expectedStrings) + 
                    " but was " + Arrays.toString(databaseStrings));
        }
    }

    public void _testDefaultCoalesceOperatorDatabaseStringsTest() {
        ExpressionOperator coalesceOp = ExpressionOperator.coalesce();

        String[] databaseStrings = coalesceOp.getDatabaseStrings(0);
        String[] expectedStrings = new String[] {"COALESCE(", ")"};
        if(!(Arrays.equals(expectedStrings, databaseStrings))) {
            throw new TestErrorException("Expected " + Arrays.toString(expectedStrings) + 
                    " but was " + Arrays.toString(databaseStrings));
        }

        databaseStrings = coalesceOp.getDatabaseStrings(1);
        expectedStrings = new String[] {"COALESCE(", ")"};
        if(!(Arrays.equals(expectedStrings, databaseStrings))) {
            throw new TestErrorException("Expected " + Arrays.toString(expectedStrings) + 
                    " but was " + Arrays.toString(databaseStrings));
        }

        databaseStrings = coalesceOp.getDatabaseStrings(2);
        expectedStrings = new String[] {"COALESCE(", ", ", ")"};
        if(!(Arrays.equals(expectedStrings, databaseStrings))) {
            throw new TestErrorException("Expected " + Arrays.toString(expectedStrings) + 
                    " but was " + Arrays.toString(databaseStrings));
        }

        databaseStrings = coalesceOp.getDatabaseStrings(3);
        expectedStrings = new String[] {"COALESCE(", ", ", ", ", ")"};
        if(!(Arrays.equals(expectedStrings, databaseStrings))) {
            throw new TestErrorException("Expected " + Arrays.toString(expectedStrings) + 
                    " but was " + Arrays.toString(databaseStrings));
        }

        databaseStrings = coalesceOp.getDatabaseStrings(4);
        expectedStrings = new String[] {"COALESCE(", ", ", ", ", ", ", ")"};
        if(!(Arrays.equals(expectedStrings, databaseStrings))) {
            throw new TestErrorException("Expected " + Arrays.toString(expectedStrings) + 
                    " but was " + Arrays.toString(databaseStrings));
        }

        databaseStrings = coalesceOp.getDatabaseStrings(5);
        expectedStrings = new String[] {"COALESCE(", ", ", ", ", ", ", ", ", ")"};
        if(!(Arrays.equals(expectedStrings, databaseStrings))) {
            throw new TestErrorException("Expected " + Arrays.toString(expectedStrings) + 
                    " but was " + Arrays.toString(databaseStrings));
        }
    }

    @Override
    public void addTests() {
        setManager(PopulationManager.getDefaultManager());

        addTest(new UnitTestCase("Equals$nullTest"));
        addTest(new UnitTestCase("Equals$ObjectTest"));
        addTest(new UnitTestCase("EqualsTest"));
        addTest(new UnitTestCase("EqualsArrayTest"));
        addTest(new UnitTestCase("IsComparisonOperatorTest"));
        addTest(new UnitTestCase("IsFunctionOperatorTest"));
        addTest(new UnitTestCase("IsLogicalOperatorTest"));

        addTest(new UnitTestCase("DefaultCaseOperatorDatabaseStringsTest"));
        addTest(new UnitTestCase("DefaultCaseConditionOperatorDatabaseStringsTest"));
        addTest(new UnitTestCase("DefaultCoalesceOperatorDatabaseStringsTest"));
    }
}
