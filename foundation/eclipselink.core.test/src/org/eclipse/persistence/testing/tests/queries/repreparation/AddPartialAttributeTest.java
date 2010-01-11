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
package org.eclipse.persistence.testing.tests.queries.repreparation;

import java.util.Vector;

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

public class AddPartialAttributeTest extends TestCase {
    private ReadAllQuery query;
    private Vector employees;

    public AddPartialAttributeTest() {
        setDescription("Test if SQL is reprepared the second time");
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        query = new ReadAllQuery(Employee.class);
        employees = (Vector)getSession().executeQuery(query);
    }

    public void test() {
        query.dontMaintainCache();
        query.addPartialAttribute("lastName");
        employees = (Vector)getSession().executeQuery(query);
    }

    public void verify() {
        if (!query.getCall().getSQLString().equals("SELECT t0.EMP_ID, t0.L_NAME FROM EMPLOYEE t0, SALARY t1 WHERE (t1.EMP_ID = t0.EMP_ID)")) {
            throw new TestErrorException("AddPartialAttributeTest failed.");
        }
    }
}

