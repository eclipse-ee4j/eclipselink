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
package org.eclipse.persistence.testing.tests.unitofwork;

import java.util.Vector;

import org.eclipse.persistence.queries.DataModifyQuery;
import org.eclipse.persistence.sessions.remote.RemoteSession;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TestProblemException;
import org.eclipse.persistence.testing.framework.TestWarningException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


/*
 * Test for bug 4211104  Where query executed in UOW event (during trans) prevents UOW
 * from commiting the transaction Resulting in a rollback.
 */
public class CommitAfterExecuteModifyQueryDuringTransTest extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    Employee originalEmployee;
    Employee cachedEmployee = null;
    DataModifyQuery dataModifyQuery = null;
    org.eclipse.persistence.sessions.UnitOfWork uow = null;
    String employeesNewFirstName = "";
    Object initialVersionField = null;

    public CommitAfterExecuteModifyQueryDuringTransTest() {
        setDescription("Test for successful UOW commit with DataModifyQuery being executed from an event during the transaction");
    }

    /**
     * Test makes modifications that is not wrapped in a transaction..
     */
    public void setup() {
        if (getSession() instanceof RemoteSession) {
            throw new TestWarningException("test will not run on RemoteSession - it uses events");
        }
        //employee to be modified - needed for reset.
        cachedEmployee = (Employee)getSession().readObject(Employee.class);
        originalEmployee = (Employee)getSession().copyObject(cachedEmployee);
        employeesNewFirstName = "formerlyKnownAs";
        initialVersionField = getSession().getIdentityMapAccessor().getWriteLockValue(cachedEmployee);

        //query to be executed more than once
        dataModifyQuery = 
                new DataModifyQuery("UPDATE EMPLOYEE SET F_NAME = #F_NAME, VERSION = #VERSION WHERE L_NAME = #L_NAME");
        dataModifyQuery.addArgument("F_NAME");
        dataModifyQuery.addArgument("VERSION");
        dataModifyQuery.addArgument("L_NAME");
    }

    /**
     * Test makes modifications that is not wrapped in a transaction..
     * so we have to reset our change
     */
    public void reset() {
        Vector myV = new Vector();
        myV.addElement(originalEmployee.firstName);
        myV.addElement(initialVersionField);
        myV.addElement(originalEmployee.lastName);

        try {
            getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
            getSession().executeQuery(dataModifyQuery, myV);
        } catch (Exception internalException) {
            throw new TestProblemException("Test case may not have cleaned up correctly affecting other tests", 
                                           internalException);
        }
    }

    public void test() {
        uow = getSession().acquireUnitOfWork();
        uow.getEventManager().addListener(new SessionEventAdapter() {
                    public void prepareUnitOfWork(SessionEvent event) {
                        Vector myV = new Vector();
                        myV.addElement(employeesNewFirstName);
                        myV.addElement(initialVersionField);
                        myV.addElement(originalEmployee.lastName);
                        uow.executeQuery(dataModifyQuery, myV);
                    }
                });

        Employee temp = (Employee)uow.registerObject(cachedEmployee);
        temp.firstName = "tempFirstName";

        //no exceptions are expected, though transaction should not be commited if test fails:
        uow.commit();
        cachedEmployee = (Employee)getSession().refreshObject(cachedEmployee);
    }

    public void verify() {
        if (!employeesNewFirstName.equals(cachedEmployee.firstName)) {
            throw new TestErrorException("UOW was not commited when non-selecting SQL was issued during transaction.  Emp First name was not updated");
        }
    }
}
