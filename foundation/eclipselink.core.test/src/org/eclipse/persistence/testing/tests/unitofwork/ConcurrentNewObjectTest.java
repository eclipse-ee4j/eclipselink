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

import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TestWarningException;
import org.eclipse.persistence.testing.models.employee.domain.Address;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.domain.EmployeePopulator;


/**
 * <p>
 * <b>Purpose</b>: This test to veify that new objects read by one thread before that object has
 * been merged by the creating thread will not cause the merging thread to throw UOW verification exception.
 */
public class ConcurrentNewObjectTest extends AutoVerifyTestCase {

    public ConcurrentNewObjectTest() {
        super();
    }

    public void setup() {
        if (getSession() instanceof org.eclipse.persistence.sessions.remote.RemoteSession) {
            throw new TestWarningException("Test not supported in remote, it uses internal jta callbacks.");
        }
        getAbstractSession().beginTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    protected void test() {
        UnitOfWorkImpl unitOfWork1 = (UnitOfWorkImpl)getSession().acquireUnitOfWork();
        Address address = new EmployeePopulator().addressExample1();
        Employee originalClone = (Employee)unitOfWork1.readObject(Employee.class);
        originalClone.setAddress(address);
        unitOfWork1.issueSQLbeforeCompletion();
        // Simulate use of the JTS
        unitOfWork1.setPendingMerge();
        address = (Address)getSession().readObject(address);
        try {
            unitOfWork1.mergeClonesAfterCompletion();
        } catch (Exception exception) {
            throw new TestErrorException("New Object was read into Cache before Merged by creator, creator threw error");
        }
    }

    public void reset() {
        getAbstractSession().commitTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }
}
