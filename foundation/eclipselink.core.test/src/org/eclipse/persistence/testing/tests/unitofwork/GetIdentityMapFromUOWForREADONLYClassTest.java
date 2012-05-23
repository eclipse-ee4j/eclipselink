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
package org.eclipse.persistence.testing.tests.unitofwork;

import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


/**
 * Test READ-ONLY class feature should work with uow.getFromIdentityMap().
 * To verify it, uow.getCloneMapping().isEmpty() should return true.
 * READ-ONLY classes do not require clones to be created and merged.
 */
public class GetIdentityMapFromUOWForREADONLYClassTest extends AutoVerifyTestCase {
    public UnitOfWork uow;
    Employee employee;

    public GetIdentityMapFromUOWForREADONLYClassTest() {
        setDescription("This test verifies that READ-ONLY class feauture should work if UnitOfWork.getFromIdentityMap() is called  ");
    }

    protected void setup() {
        getAbstractSession().beginTransaction();
        employee = (Employee)getSession().readObject(Employee.class);
    }

    public void reset() {
        getAbstractSession().rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    protected void test() {
        uow = getSession().acquireUnitOfWork();
        uow.addReadOnlyClass(employee.getClass());
        uow.getIdentityMapAccessor().getFromIdentityMap(getSession().getId(employee), employee.getClass());
        uow.commit();
    }

    protected void verify() {
        if (!((UnitOfWorkImpl)uow).getCloneMapping().isEmpty()) {
            throw new TestErrorException("READ-ONLY class feature failed when getFromIdentityMap() is called");
        }
    }
}
