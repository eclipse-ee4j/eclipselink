/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.distributedservers;

import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


/** 
 * Test changing private parts of an object.
 * 
 */
public class UpdateChangeObjectWithOptimisticLockTest extends ComplexUpdateTest {

    public UpdateChangeObjectWithOptimisticLockTest() {
        super();
    }

    public UpdateChangeObjectWithOptimisticLockTest(Employee originalObject) {
        super(originalObject);
    }

    protected void changeObject() {
        Employee employee = (Employee)this.workingCopy;
        // Transformation
        employee.setNormalHours(new java.sql.Time[2]);
        employee.setStartTime(Helper.timeFromHourMinuteSecond(1, 1, 1));
    }

    protected void test() {
        changeObject();
        // Ensure that the original has not been changed.
        if (!getUnitOfWork().getParent().compareObjects(this.originalObject, this.objectToBeWritten)) {
            throw new TestErrorException("The original object was changed through changing the clone.");
        }
        getUnitOfWork().commit();
    }

    /**
     * Verify if the objects match completely through allowing the session to use the descriptors.
     * This will compare the objects and all of their privately owned parts.
     */
    protected void verify() {
        DatabaseSession remoteServer = ((DistributedServer)DistributedServersModel.getDistributedServers().get(0)).getDistributedSession();
        // The main session is now in transaction (started in TransactionalTestCase.setup).
        // remoteServer was setup to share accessor (and therefore connection) with the main session (see DistributedServer constructor),
        // therefore remoteServer's connection is in transaction.
        // Because it seems there is no way to write object with version without transaction,
        // let's compare versions directly
        Employee remoteEmp = (Employee)remoteServer.executeQuery(this.query);
        long remoteVersion = (Long)remoteServer.getDescriptor(Employee.class).getOptimisticLockingPolicy().getWriteLockValue(remoteEmp, remoteEmp.getId(), (AbstractSession)remoteServer);
        long writtenVersion = (Long)getUnitOfWork().getParent().getDescriptor(Employee.class).getOptimisticLockingPolicy().getWriteLockValue((Employee)objectToBeWritten, ((Employee)objectToBeWritten).getId(), (AbstractSession)getUnitOfWork().getParent());
        if (remoteVersion != writtenVersion) {
            throw new TestErrorException("Failed to copy the version number to the remote system");
        }
    }
}
