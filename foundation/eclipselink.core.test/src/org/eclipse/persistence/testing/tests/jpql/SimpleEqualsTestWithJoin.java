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

import java.util.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

public class SimpleEqualsTestWithJoin extends JPQLTestCase {
    public void setup() {
        setReferenceClass(Employee.class);

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = builder.anyOf("managedEmployees").get("address").get("city").equal("Ottawa");
        Vector employees = getSession().readAllObjects(Employee.class, whereClause);

        setOriginalOject(employees);
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        String ejbqlString;
        ejbqlString = "SELECT OBJECT(emp) FROM Employee emp, IN(emp.managedEmployees) managedEmployees " + "WHERE managedEmployees.address.city = 'Ottawa'";

        setEjbqlString(ejbqlString);
        super.setup();
    }
}