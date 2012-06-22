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
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

/**
 * Ensure expired objects can still be used in an UnitOfWork.
 */
public class UnitOfWorkExpiredObjectTest extends CacheExpiryTest {

    protected Employee employee = null;
    protected String firstName = null;

    public UnitOfWorkExpiredObjectTest() {
        setDescription("Ensure expired objects are still usable on a UnitOfWork.");
    }

    public void test() {
        employee = (Employee)getSession().readObject(Employee.class);
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Employee employeeClone = (Employee)uow.readObject(employee);
        uow.getIdentityMapAccessor().invalidateObject(employeeClone);
        employeeClone.setFirstName(employee.getFirstName() + "-mutated");
        firstName = employeeClone.getFirstName();
        uow.commit();
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        employee = (Employee)getSession().readObject(employee);
    }

    public void verify() {
        if (!employee.getFirstName().equals(firstName)) {
            throw new TestErrorException("Using an employee in a UnitOfWork after invalidating it failed.");
        }
    }

}
