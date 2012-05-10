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

import java.util.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;

public class SelectSimpleNotInTest extends org.eclipse.persistence.testing.tests.jpql.JPQLTestCase {
    public void setup() {
        Employee emp;
        emp = (Employee)getSomeEmployees().firstElement();

        ExpressionBuilder builder = new ExpressionBuilder();

        Vector idVector = new Vector();
        idVector.add(emp.getId());

        Expression whereClause = builder.get("id").notIn(idVector);
        ReadAllQuery raq = new ReadAllQuery();
        raq.setReferenceClass(Employee.class);
        raq.setSelectionCriteria(whereClause);

        setOriginalOject(getSession().executeQuery(raq));
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        //////////
        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE emp.id NOT IN (" + emp.getId().toString() + ")";

        setEjbqlString(ejbqlString);

        super.setup();

    }
}
