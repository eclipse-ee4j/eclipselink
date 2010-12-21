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
package org.eclipse.persistence.testing.tests.distributedservers.rcm;

import java.util.Hashtable;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.tests.distributedservers.DistributedServer;
import org.eclipse.persistence.testing.tests.distributedservers.DistributedServersModel;
import org.eclipse.persistence.testing.tests.isolatedsession.IsolatedEmployee;

/**
 * Bug 3587273
 * Ensure objects that are isolated are not sent by cache synchronization.
 */
public class IsolatedObjectNotSentTest extends ConfigurableCacheSyncDistributedTest {
    protected IsolatedEmployee employee = null;
    protected Expression expression = null;
    protected IsolatedEmployee distributedEmployee = null;
    public boolean sentChanges = false;
    protected SessionEventAdapter listener;

    public IsolatedObjectNotSentTest() {
        setDescription("Test to ensure that objects that are set as isolated will not be sent over Cache Synchronization.");
    }

    public IsolatedObjectNotSentTest(Hashtable cacheSyncConfigValues) {
        setDescription("Test to ensure that objects that are set as isolated will not be sent over Cache Synchronization.");
    }

    public void setup() {
        super.setup();
        ExpressionBuilder employees = new ExpressionBuilder();
        this.expression = employees.get("firstName").equal("Andy");
        this.expression = this.expression.and(employees.get("lastName").equal("McDurmont"));
        // ensure our employee is in one of the distributed caches
        DistributedServer server = (DistributedServer)DistributedServersModel.getDistributedServers().get(0);
        this.distributedEmployee = (IsolatedEmployee)server.getDistributedSession().readObject(IsolatedEmployee.class, this.expression);
        this.listener = new SessionEventAdapter() {
            public void preMergeUnitOfWorkChangeSet(SessionEvent event) {
                sentChanges = true;
            }
        };
        server.getDistributedSession().getEventManager().addListener(this.listener);
        this.sentChanges = false;
    }

    public void test() {
        this.employee = (IsolatedEmployee)getSession().readObject(IsolatedEmployee.class, this.expression);

        UnitOfWork uow = getSession().acquireUnitOfWork();
        IsolatedEmployee employeeClone = (IsolatedEmployee)uow.registerObject(this.employee);
        employeeClone.setSalary(employeeClone.getSalary() + 1000);
        uow.commit();
    }

    public void verify() {
        this.distributedEmployee = (IsolatedEmployee)getObjectFromDistributedCache(this.employee);
        if (this.distributedEmployee.getSalary() == this.employee.getSalary()) {
            throwError("The employee was sent by cache synchronization, but should not have been since it is isolated.");
        }
        if (this.sentChanges) {
            throwError("Cache coordintion was sent even though no object were synchronized.");
        }
    }
    
    public void reset() {
        super.reset();
        DistributedServer server = (DistributedServer)DistributedServersModel.getDistributedServers().get(0);
        server.getDistributedSession().getEventManager().removeListener(this.listener);
    }
}
