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

import org.eclipse.persistence.sessions.SessionEventListener;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TestWarningException;
import org.eclipse.persistence.testing.framework.TransactionalTestCase;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


public class NoValidationWithInitIdentityMaps extends TransactionalTestCase {
    protected String existenceCheck;
    protected Employee objectToBeWritten;
    // On some platforms (Sybase, Symfoware) if conn1 updates a row but hasn't yet committed
    // the transaction then reading the row through conn2 may hang.
    // To avoid this problem the listener would decrement transaction isolation level,
    // then reading through conn2 no longer hangs, however may result (results on Sybase)
    // in reading of uncommitted data.
    SessionEventListener listener;

    public NoValidationWithInitIdentityMaps() {
        setDescription("Test using no validation.");
    }

    public void setup() {
        if(getSession().isClientSession()) {
            listener = checkTransactionIsolation();
        }

        super.setup();
        existenceCheck = getSession().getDescriptor(Employee.class).getQueryManager().getExistenceCheck();
        if (getSession() instanceof org.eclipse.persistence.sessions.remote.RemoteSession) {
            throw new TestWarningException("This test uses functionality that does not work over remote sessions.");
        }
    }

    public void reset() {
        super.reset();
        getSession().getDescriptor(Employee.class).setExistenceChecking(this.existenceCheck);
        if(listener != null) {
            getAbstractSession().getParent().getEventManager().removeListener(listener);
            listener = null;
        }
    }

    public void test() {
        testNoneCheckDatabaseNewObject();
        testNoneAssumeNonExistenceNewObject();
        testNoneCheckDatabase();
        testNoneAssumeExistence();
        testNoneAssumeNonExistence();
    }

    public void testNoneCheckDatabaseNewObject() {
        Employee employeeFromCache = new Employee();

        //initialize Identity map to verify toplink will perform an existence check
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        getSession().getDescriptor(Employee.class).setExistenceChecking("Check database");

        UnitOfWork uow = getSession().acquireUnitOfWork();
        Employee newObject = (Employee)uow.newInstance(Employee.class);
        this.objectToBeWritten = newObject;
        uow.dontPerformValidation();
        newObject.setManager(employeeFromCache);
        uow.commit();
    }

    public void testNoneAssumeNonExistenceNewObject() {
        Employee employeeFromCache = new Employee();

        //initialize Identity map to verify toplink will perform an existence check
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        getSession().getDescriptor(Employee.class).setExistenceChecking("Assume non-existence");

        UnitOfWork uow = getSession().acquireUnitOfWork();
        Employee newObject = (Employee)uow.newInstance(Employee.class);
        this.objectToBeWritten = newObject;
        uow.dontPerformValidation();
        newObject.setManager(employeeFromCache);
        uow.commit();
    }

    public void testNoneCheckDatabase() {
        Employee employeeFromCache = (Employee)getSession().readObject(Employee.class);

        //initialize Identity map to verify toplink will perform an existence check
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        getSession().getDescriptor(Employee.class).setExistenceChecking("Check database");

        UnitOfWork uow = getSession().acquireUnitOfWork();
        Employee newObject = (Employee)uow.newInstance(Employee.class);
        this.objectToBeWritten = newObject;
        uow.dontPerformValidation();
        newObject.setManager(employeeFromCache);
        uow.commit();
    }

    public void testNoneAssumeExistence() {
        Employee employeeFromCache = (Employee)getSession().readObject(Employee.class);

        //initialize Identity map to verify toplink will perform an existence check
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        getSession().getDescriptor(Employee.class).setExistenceChecking("Assume existence");

        UnitOfWork uow = getSession().acquireUnitOfWork();
        Employee newObject = (Employee)uow.newInstance(Employee.class);
        this.objectToBeWritten = newObject;
        uow.dontPerformValidation();
        newObject.setManager(employeeFromCache);
        uow.commit();
    }

    public void testNoneAssumeNonExistence() {
        Employee employeeFromCache = (Employee)getSession().readObject(Employee.class);

        //initialize Identity map to verify toplink will perform an existence check
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        getSession().getDescriptor(Employee.class).setExistenceChecking("Assume non-existence");

        UnitOfWork uow = getSession().acquireUnitOfWork();
        Employee newObject = (Employee)uow.newInstance(Employee.class);
        this.objectToBeWritten = newObject;
        uow.dontPerformValidation();
        //	newObject.setManager(employeeFromCache);
        uow.commit();
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
