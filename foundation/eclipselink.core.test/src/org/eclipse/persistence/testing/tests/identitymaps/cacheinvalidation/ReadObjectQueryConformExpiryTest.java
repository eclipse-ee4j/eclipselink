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
package org.eclipse.persistence.testing.tests.identitymaps.cacheinvalidation;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.framework.*;

public class ReadObjectQueryConformExpiryTest extends CacheExpiryTest {
    protected Employee employee = null;

    public ReadObjectQueryConformExpiryTest() {
        setDescription("Test to ensure expired objects are still returned when conform results is used.");
    }

    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();

        employee = (Employee)getSession().readObject(Employee.class);
        ExpressionBuilder empBuilder = new ExpressionBuilder();
        Expression readExpression = empBuilder.get("firstName").equal(employee.getFirstName());
        readExpression = readExpression.and(empBuilder.get("lastName").equal(employee.getLastName()));
        readExpression = readExpression.and(empBuilder.get("address").get("street").equal(employee.getAddress().getStreet()));
        employee.setLastName("Blah");
        employee.getAddress().setPostalCode("000000");
        uow.getIdentityMapAccessor().invalidateObject(employee.getAddress());
        uow.getIdentityMapAccessor().invalidateObject(employee);

        Expression emp = empBuilder.get("id").equal(employee.getId());
        ReadObjectQuery query = new ReadObjectQuery(Employee.class, emp);
        query.conformResultsInUnitOfWork();
        employee = (Employee)uow.executeQuery(query);

        uow.release();
    }

    public void verify() {
        if (!employee.getLastName().equals("Blah") || !employee.getAddress().getPostalCode().equals("000000")) {
            throw new TestErrorException("Expired objects were not returned when conform resutls is used.");
        }
    }
}
