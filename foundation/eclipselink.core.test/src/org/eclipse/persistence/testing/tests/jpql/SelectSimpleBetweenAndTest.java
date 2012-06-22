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
package org.eclipse.persistence.testing.tests.jpql;

import java.math.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;

/**
 * Tests Simple OR clause
 */
class SelectSimpleBetweenAndTest extends org.eclipse.persistence.testing.tests.jpql.JPQLTestCase {
    public void setup() {
        BigDecimal emp1 = new BigDecimal(0);
        Employee emp2;
        emp2 = (Employee)getSomeEmployees().lastElement();

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = builder.get("id").between(emp1, emp2.getId());
        ReadAllQuery raq = new ReadAllQuery();
        raq.setReferenceClass(Employee.class);
        raq.setSelectionCriteria(whereClause);
        setOriginalOject(getSession().executeQuery(raq));

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE emp.id BETWEEN " + emp1 + "AND " + emp2.id;
        setEjbqlString(ejbqlString);

        super.setup();
    }
}
