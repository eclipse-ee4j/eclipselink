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
package org.eclipse.persistence.testing.tests.manual;

import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.*;

public class NewObjectDeleteTest extends ManualVerifyTestCase {
    public Employee employee;

    public NewObjectDeleteTest() {
        setDescription("Check the SQL to see if the new objects inserted are also deleted or not. The test case is a faliure if objects are not deleted.");
    }

    public void reset() {
        rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    protected void setup() {
        beginTransaction();
    }

    protected void test() {
        this.employee = (Employee)(new EmployeePopulator()).basicEmployeeExample1();
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Employee workingCopy = (Employee)uow.registerObject(this.employee);
        workingCopy.setFirstName("firstName");
        uow.deleteObject(workingCopy);
        uow.commit();

        this.employee = (Employee)(new EmployeePopulator()).basicEmployeeExample2();
        uow = getSession().acquireUnitOfWork();
        UnitOfWork nuow = uow.acquireUnitOfWork();
        workingCopy = (Employee)nuow.registerObject(this.employee);
        workingCopy.setFirstName("firstName");
        nuow.deleteObject(workingCopy);
        nuow.commit();
        uow.commit();

    }
}
