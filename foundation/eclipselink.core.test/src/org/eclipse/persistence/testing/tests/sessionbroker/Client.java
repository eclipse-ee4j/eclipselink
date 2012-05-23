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
package org.eclipse.persistence.testing.tests.sessionbroker;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.sessions.broker.SessionBroker;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.models.employee.domain.Address;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.domain.PhoneNumber;
import org.eclipse.persistence.testing.models.employee.domain.SmallProject;


public class Client extends Thread {
    protected Server server;
    protected SessionBroker cSessionBroker;
    protected Session session;
    protected Object originalObject;
    protected UnitOfWork unitOfWork;
    protected Object objectToBeWritten;
    protected Expression expression;

    public Client(Server server, String employeeLastName, Session session) {
        super(employeeLastName);
        this.server = server;
        this.session = session;
        this.cSessionBroker = server.sSessionBroker.acquireClientSessionBroker();
        this.expression = new ExpressionBuilder().get("lastName").equal(employeeLastName);
    }

    protected void changeObject() {
        int num = (int)(Math.random() * 5) + 1;

        Employee e = (Employee)objectToBeWritten;
        if (num == 1) {
            e.setSalary(e.getSalary() * 2);
        }
        if (num == 2) {
            Address a = new org.eclipse.persistence.testing.models.employee.domain.Address();
            a.setCity("Ottawa city");
            e.setAddress(a);
        }
        if (num == 3) {
            PhoneNumber p = new org.eclipse.persistence.testing.models.employee.domain.PhoneNumber();
            p.setNumber("1234");
            p.setType("SHOE");
            e.addPhoneNumber(p);
        }
        if (num == 4) {
            SmallProject s = new org.eclipse.persistence.testing.models.employee.domain.SmallProject();
            s.setName("project for me");
            e.addProject(s);
        }
        if (num == 5) {
            e.setFirstName(e.getFirstName() + "1");
        }

    }

    public void release() {
        this.cSessionBroker.release();
    }

    public void run() {
        try {
            this.originalObject = this.cSessionBroker.readObject(Employee.class, this.expression);
            this.unitOfWork = this.cSessionBroker.acquireUnitOfWork();
            this.unitOfWork.readAllObjects(Employee.class); // Test read concurrency.
            this.objectToBeWritten = this.unitOfWork.registerObject(this.originalObject);
            changeObject();
            //		if (! this.session.compareObjectsDontMatch(this.originalObject, this.objectToBeWritten)) {
            //			throw new TestErrorException("The original object was changed through changing the clone.");
            //		}

            this.unitOfWork.commit();
        } catch (Exception exception) {
            this.server.errorOccured = true;
            exception.printStackTrace(System.out);
        }

    }
}
