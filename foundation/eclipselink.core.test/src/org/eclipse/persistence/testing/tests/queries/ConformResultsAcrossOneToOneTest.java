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
package org.eclipse.persistence.testing.tests.queries;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.models.legacy.*;

public class ConformResultsAcrossOneToOneTest extends ConformResultsInUnitOfWorkTest {
    Object comparedObject;
    int operator;
    static int EQUAL = 0;
    static int NOT_EQUAL = 1;
    static int IS_NULL = 2;
    static int NOT_NULL = 3;

    public ConformResultsAcrossOneToOneTest() {
        this(EQUAL);
    }

    public ConformResultsAcrossOneToOneTest(int operator) {
        this.operator = operator;
        String nameString = getName();
        if (operator == EQUAL) {
            nameString = nameString + "( EQUAL )";
        }
        if (operator == IS_NULL) {
            nameString = nameString + "( IS NULL )";
        }
        if (operator == NOT_EQUAL) {
            nameString = nameString + "( NOT EQUAL )";
        }
        if (operator == NOT_NULL) {
            nameString = nameString + "( NOT NULL )";
        }
        setName(nameString);
    }

    public void buildConformQuery() {
        conformedQuery = new ReadObjectQuery();
        conformedQuery.setReferenceClass(org.eclipse.persistence.testing.models.legacy.Computer.class);
        conformedQuery.conformResultsInUnitOfWork();
        conformedQuery.setSelectionCriteria(buildFullExpression(new ExpressionBuilder().get("employee"), comparedObject));

    }

    public Expression buildFullExpression(Expression partialExpression, Object object) {
        if (operator == EQUAL) {
            return partialExpression.equal(object);
        }
        if (operator == NOT_EQUAL) {
            return partialExpression.notEqual(object);
        }
        if (operator == IS_NULL) {
            //object is not needed here
            return partialExpression.isNull();
        }
        if (operator == NOT_NULL) {
            //object is not needed here
            return partialExpression.notNull();
        }
        throw new TestErrorException("operator not supported");

    }

    public void prepareTest() {
        comparedObject = getSession().readObject(Employee.class);
        if (operator == IS_NULL) {
            //make sure there is one that conforms
            Computer c = (Computer)getSession().readObject(Computer.class);
            c.setEmployee(null);
            getDatabaseSession().writeObject(c);
        }
    }

    public void reset() {
        super.reset();
    }

    public void setup() {
        super.setup();
    }

    public void verify() {
        if (result == null) {
            throw new TestErrorException("object existed in database but not returned in query");
        }
        Computer computer = (Computer)result;
        if (operator == IS_NULL) {
            if (computer.employee != null) {
                throw new TestErrorException("Wrong object has been returned.");
            }
            return;
        }
        if (operator == NOT_NULL) {
            if (computer.employee == null) {
                throw new TestErrorException("Wrong object has been returned.");
            }
            return;
        }
        if (operator == EQUAL) {
            //check if primary keys are equal.
            if (!((computer.employee.firstName == ((Employee)comparedObject).firstName) && (computer.employee.lastName == ((Employee)comparedObject).lastName))) {
                //primary keys are NOT equal
                throw new TestErrorException("Wrong object has been returned.");
            }
            return;
        }
        if (operator == NOT_EQUAL) {
            if ((computer.employee.firstName == ((Employee)comparedObject).firstName) && (computer.employee.lastName == ((Employee)comparedObject).lastName)) {
                throw new TestErrorException("Wrong object has been returned.");
            }
        }
    }
}
