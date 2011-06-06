/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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

import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.sessions.SessionEventListener;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TransactionalTestCase;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


public class ViolateObjectSpaceTest extends TransactionalTestCase {
    protected Object objectToBeWritten;
    // On some platforms (Sybase) if conn1 updates a row but hasn't yet committed transaction then
    // reading the row through conn2 may hang.
    // To avoid this problem the listener would decrement transaction isolation level,
    // then reading through conn2 no longer hangs, however may result (results on Sybase)
    // in reading of uncommitted data.
    SessionEventListener listener;

    public ViolateObjectSpaceTest() {
        setDescription("Test using no validation.");
    }

    public void reset() {
        super.reset();
        if(listener != null) {
            getAbstractSession().getParent().getEventManager().removeListener(listener);
            listener = null;
        }
    }
    
    protected void setup() {
        if (getSession().isClientSession()) {
            listener = checkTransactionIsolation();
        }
        super.setup();
    }
    
    public void test() {
        testPartial();
        testFull();
        testNone();
    }

    public void testFull() {
        Employee employeeFromCache = (Employee)getSession().readObject(Employee.class);
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.performFullValidation();
        Employee newEmployee = new org.eclipse.persistence.testing.models.employee.domain.Employee();
        newEmployee.setId(employeeFromCache.getId());
        ValidationException exception = null;
        try {
            uow.registerObject(newEmployee);
        } catch (ValidationException caught) {
            exception = caught;
        }
        if ((exception == null) || (exception.getErrorCode() != ValidationException.WRONG_OBJECT_REGISTERED)) {
            throw new TestErrorException("incorrect exception thrown.");
        }
    }

    public void testNone() {
        Employee employeeFromCache = (Employee)getSession().readObject(Employee.class);
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Employee newObject = (Employee)uow.newInstance(Employee.class);
        this.objectToBeWritten = newObject;
        uow.dontPerformValidation();
        newObject.setManager(employeeFromCache);
        uow.commit();
    }

    public void testPartial() {
        Employee employeeFromCache = (Employee)getSession().readObject(Employee.class);
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Employee newObject = (Employee)uow.newInstance(Employee.class);
        uow.performPartialValidation();
        newObject.setManager(employeeFromCache);
        QueryException exception = null;
        try {
            uow.commit();
        } catch (QueryException caught) {
            exception = caught;
        }
        if ((exception == null) || 
            (exception.getErrorCode() != QueryException.BACKUP_CLONE_IS_ORIGINAL_FROM_PARENT)) {
            throw new TestErrorException("incorrect exception thrown.");
        }
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void verify() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        Object objectFromDatabase = getSession().readObject(objectToBeWritten);

        if (!(compareObjects(this.objectToBeWritten, objectFromDatabase))) {
            throw new TestErrorException("The object inserted into the database, '" + objectFromDatabase + 
                                         "' does not match the original, '" + this.objectToBeWritten + ".");
        }
    }
}
