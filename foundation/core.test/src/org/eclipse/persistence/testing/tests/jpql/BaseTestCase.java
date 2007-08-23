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
import org.eclipse.persistence.testing.models.employee.domain.*;

public class BaseTestCase extends JPQLTestCase {
    public void setup() {
        Vector employees = (Vector)getSession().readAllObjects(Employee.class);

        setOriginalOject(employees);
        setEjbqlString("SELECT OBJECT(emp) FROM Employee emp");

        super.setup();
    }
}