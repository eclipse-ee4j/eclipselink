/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpql;

import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.queries.ReadAllQuery;
import java.util.Vector;

public class SimpleIsNotEmptyTest extends org.eclipse.persistence.testing.tests.jpql.JPQLTestCase {
    public void setup() {
        Vector selectedEmployees = getSomeEmployees();

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = builder.notEmpty("phoneNumbers");

        ReadAllQuery raq = new ReadAllQuery();
        raq.setReferenceClass(Employee.class);
        raq.setSelectionCriteria(whereClause);

        setOriginalOject((Vector)getSession().executeQuery(raq));

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE emp.phoneNumbers IS NOT EMPTY";

        setEjbqlString(ejbqlString);

        super.setup();
    }
}