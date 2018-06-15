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


// Java imports
import java.util.*;

// Domain imports
import org.eclipse.persistence.testing.models.employee.domain.*;

//TopLink imports
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;

public class SelectOneToOneTest extends JPQLTestCase {
    public void setup() {
        Vector addressesWithAnEmployee = null;
        String ejbqlString = null;

        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(Address.class);
        query.useDistinct();
        ExpressionBuilder employeeBuilder = new ExpressionBuilder(Employee.class);
        Expression selectionCriteria = new ExpressionBuilder(Address.class).equal(employeeBuilder.get("address")).and(employeeBuilder.get("lastName").like("%Way%"));
        query.setSelectionCriteria(selectionCriteria);

        addressesWithAnEmployee = (Vector)getSession().executeQuery(query);

        setOriginalOject(addressesWithAnEmployee);
        setReferenceClass(Employee.class);

        //setup the EJBQL to do the same
        ejbqlString = "SELECT DISTINCT employee.address FROM Employee employee WHERE employee.lastName LIKE '%Way%'";
        setEjbqlString(ejbqlString);
        super.setup();
    }
}
