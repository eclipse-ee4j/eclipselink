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

import org.eclipse.persistence.exceptions.OptimisticLockException;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.domain.EmployeePopulator;


/**
 * UnregisteredNewObjectOptimisticLockTest checks a very specific case in which
 * an unregistered new object is inserted into the database within a UnitOfWork
 * set to store new objects in cache.  And that object has a primary key and
 * optimistic locking.  If an update is performed on that object before it is refreshed
 * then an optimistic lock exception will occur.
 */
public class UnregisteredNewObjectOptimisticLockTest extends AutoVerifyTestCase {
    // Class members

    public UnregisteredNewObjectOptimisticLockTest() {
        super();
        setDescription("This test tests a very specific error in which a unregistered " + 
                       "new object is inserted by a UnitOfWork specified to store new objects in identity map");
    }

    protected void setup() {
        // Mark begin of "transaction" on database
        getAbstractSession().beginTransaction();
    }

    public void reset() {
        // Cancel the transaction on the database
        getAbstractSession().rollbackTransaction();
        // Initialize identitymaps
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    protected void test() {
        try {
            UnitOfWork uow = getSession().acquireUnitOfWork();
            Employee orig = (Employee)uow.readObject(Employee.class);
            uow.setShouldNewObjectsBeCached(true);
            Employee newEmp = (Employee)new EmployeePopulator().basicEmployeeExample6();
            orig.getManagedEmployees().add(newEmp);
            newEmp.setManager(orig);
            uow.commit();
            uow = getSession().acquireUnitOfWork();
            newEmp = (Employee)uow.readObject(newEmp);
            newEmp.setFirstName("Changed Name");
            uow.commit();
        } catch (OptimisticLockException e) {
            throw new TestErrorException("bug 3431586 unregistered new object version number not merged into cache");
        }
    }

    protected void verify() throws Exception {
    }
}// End test case
