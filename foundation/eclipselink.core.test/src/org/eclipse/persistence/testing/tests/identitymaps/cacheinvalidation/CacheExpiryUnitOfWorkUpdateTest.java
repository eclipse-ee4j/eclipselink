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

public class CacheExpiryUnitOfWorkUpdateTest extends CacheExpiryTest {

    protected Employee employee = null;
    protected String employeeName = null;

    public CacheExpiryUnitOfWorkUpdateTest() {
        setDescription("Test to ensure UnitOfWork updates work when an object expires between registration and commit.");
    }

    public void test() {
        employee = (Employee)getSession().readObject(Employee.class);
        getSession().getIdentityMapAccessor().invalidateObject(employee);
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Employee employeeClone = (Employee)uow.registerObject(employee);
        employeeName = employeeClone.getFirstName();
        employeeClone.setFirstName(employee.getFirstName() + "-mutated");
        uow.commit();
    }

    public void verify() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        employee = (Employee)getSession().readObject(employee);
        if (!employee.getFirstName().equals(employeeName + "-mutated")) {
            throw new TestErrorException("UnitOfWork update did not work correctly when expiry occurred " + 
                                         "between registration and commit.");
        }
    }

}
