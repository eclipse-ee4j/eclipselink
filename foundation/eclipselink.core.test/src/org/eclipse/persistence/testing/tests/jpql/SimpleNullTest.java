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

public class SimpleNullTest extends org.eclipse.persistence.testing.tests.jpql.JPQLTestCase {
    Employee nullTestEmployee = null;

    public Employee buildNullTestEmployee() {
        Employee employee = new Employee();
        employee.setFirstName(null);
        employee.setLastName("Smith");
        employee.setSalary(10000);

        return employee;
    }

    public void setup() {
        getDatabaseSession().writeObject(getNullTestEmployee());

        ReadAllQuery raq = new ReadAllQuery();
        raq.setReferenceClass(Employee.class);

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = builder.get("firstName").isNull();
        raq.setSelectionCriteria(whereClause);

        setOriginalOject(getSession().executeQuery(raq));

        setEjbqlString("SELECT OBJECT(emp) FROM Employee emp WHERE emp.firstName IS NULL");

        super.setup();
    }

    public Employee getNullTestEmployee() {
        if (nullTestEmployee == null) {
            setNullTestEmployee(buildNullTestEmployee());
        }
        return nullTestEmployee;
    }

    public void reset() {
        getDatabaseSession().deleteObject(getNullTestEmployee());
        super.reset();
    }

    public void setNullTestEmployee(Employee newEmployee) {
        nullTestEmployee = newEmployee;
    }
}
