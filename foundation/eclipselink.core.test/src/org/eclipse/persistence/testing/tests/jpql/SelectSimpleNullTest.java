/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.jpql;

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

public class SelectSimpleNullTest extends org.eclipse.persistence.testing.tests.jpql.JPQLTestCase {
    private Expression originalObjectExpression;
    private Employee nullTestEmployee = null;

    public SelectSimpleNullTest(String theEjbqlString) {
        super(theEjbqlString);
    }

    public static SelectSimpleNullTest getSimpleNotNullTest() {
        SelectSimpleNullTest theTest = new SelectSimpleNullTest("SELECT OBJECT(emp) FROM Employee emp WHERE emp.firstName IS NOT NULL");
        theTest.setName("Select EJBQL Not Null Test");

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = builder.get("firstName").isNull().not();
        theTest.setOriginalObjectExpression(whereClause);

        return theTest;
    }

    public static SelectSimpleNullTest getSimpleNullTest() {
        SelectSimpleNullTest theTest = new SelectSimpleNullTest("SELECT OBJECT(emp) FROM Employee emp WHERE emp.firstName IS NULL");
        theTest.setName("Select EJBQL Null Test");

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = builder.get("firstName").isNull();
        theTest.setOriginalObjectExpression(whereClause);

        return theTest;
    }

    public Expression getOriginalObjectExpression() {
        return originalObjectExpression;
    }

    public void setOriginalObjectExpression(Expression theExpression) {
        originalObjectExpression = theExpression;
    }

    public Employee getNullTestEmployee() {
        if (nullTestEmployee == null) {
            nullTestEmployee = new Employee();
            nullTestEmployee.setFemale();
            nullTestEmployee.setFirstName(null);
            nullTestEmployee.setLastName("NullTestEmployee");
            nullTestEmployee.setSalary(35000);
        }
        return nullTestEmployee;
    }

    public void setup() {
        //Set comparer here. ET
        NullDomainObjectComparer comparer = new NullDomainObjectComparer();
        comparer.setSession(getSession());
        setComparer(comparer);

        // Ensure we have a valid employee in the database
        getDatabaseSession().writeObject(getNullTestEmployee());

        ReadAllQuery raq = new ReadAllQuery();
        raq.setReferenceClass(Employee.class);
        raq.setSelectionCriteria(getOriginalObjectExpression());

        setOriginalOject(getSession().executeQuery(raq));

        super.setup();
    }

    public void reset() {
        getDatabaseSession().deleteObject(getNullTestEmployee());
        super.reset();
    }
}
