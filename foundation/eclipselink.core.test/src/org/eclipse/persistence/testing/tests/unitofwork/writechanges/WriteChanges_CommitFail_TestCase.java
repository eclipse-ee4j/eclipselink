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
 *     09/14/2010-2.2 Chris Delahunt 
 *       - bug 325002: ConcurrencyException if deferred constraints cause an exception and commit is called after writeChanges
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.unitofwork.writechanges;


import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;

import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;

/**
 * Tests that write changes works for non-trivial updates.
 * Tests that sequencing is done and primary keys are available after writeChanges().
 * Verifies that the object was added correctly and can be safely removed.
 *  @author  cdelahun
 */
public class WriteChanges_CommitFail_TestCase extends AutoVerifyTestCase {
    public EclipseLinkException exception = null;
    public UnitOfWorkImpl uow= null;

    public void test() {
        uow = new UnitOfWorkImpl((AbstractSession)getSession(), null){
            public void commitTransaction() throws DatabaseException {
                //throw a bogus exception instead of committing.  Simulates an error when constraint checking is delayed until commit
                if(beginCount==1){
                    throw DatabaseException.databaseAccessorNotConnected();
                }
                beginCount--;
                getParent().commitTransaction();
            }
            int beginCount = 0;
            public void beginTransaction() throws DatabaseException {
                //throw a bogus exception instead of committing.  Simulates an error when constraint checking is delayed until commit
                beginCount++;
                getParent().beginTransaction();
            }
        };

        Employee employee = new Employee();
        employee = (Employee)uow.registerObject(employee);
        employee.setFirstName("Stephen");
        employee.setLastName("McRitchie");

        uow.writeChanges();

        try {
            uow.commit();
        } catch(EclipseLinkException e) {
            exception = e;
        }
    }

    public void verify() {
        if (exception==null || (exception.getErrorCode() != DatabaseException.DATABASE_ACCESSOR_NOT_CONNECTED)){
            throw new TestErrorException("UnitOfWorkImpl commit did not throw original database exception and instead threw :"+exception);
        }
        if (uow.getLifecycle() == UnitOfWorkImpl.CommitTransactionPending) {
            throw new TestErrorException("UnitOfWorkImpl's getLifecycle still shows status as CommitTransactionPending after commit");
        }
    }


}
