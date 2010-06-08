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
package org.eclipse.persistence.testing.tests.clientserver;

import java.util.*;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

public class Client extends Thread {
    protected Server server;
    protected Session clientSession;
    protected Session session;
    protected Object originalObject;
    protected UnitOfWork unitOfWork;
    protected Object objectToBeWritten;
    protected Expression expression;

    public Client(Server server, String employeeLastName, Session session) {
        super(employeeLastName);
        this.server = server;
        this.session = session;
        if (getName().equals("Smith")) {
            this.clientSession = server.serverSession.acquireClientSession();
        } else if (getName().equals("Way")) {
            this.clientSession = server.serverSession.acquireClientSession("default");
        } else {
            this.clientSession = server.serverSession.acquireClientSession((DatabaseLogin)session.getLogin().clone());
        }
        this.expression = new ExpressionBuilder().get("lastName").equal(employeeLastName);
    }

    protected void changeObject() {
        if (getName().equals("Smith")) {
            changeObjectForFirstClient();
        } else if (getName().equals("Way")) {
            changeObjectForFirstClient();
        } else if (getName().equals("Chanley")) {
            changeObjectForFirstClient();
        } else {
            throw new TestErrorException("Problem in setting up a test case");
        }
    }

    public void changeObjectForFirstClient() {
        Employee employee = (Employee)this.objectToBeWritten;

        // Transformation
        employee.setNormalHours(new java.sql.Time[2]);
        employee.setStartTime(Helper.timeFromHourMinuteSecond(1, 1, 1));
        employee.setEndTime(Helper.timeFromHourMinuteSecond(1, 1, 1));
        // Aggregate
        employee.setPeriod(new org.eclipse.persistence.testing.models.employee.domain.EmploymentPeriod(Helper.dateFromYearMonthDate(1901, 1, 1), Helper.dateFromYearMonthDate(1902, 2, 2)));
        employee.getAddress().setCity("foobarrrr");
    }

    public void changeObjectForSecondClient() {
        Employee employee = (Employee)this.objectToBeWritten;

        // One to many private
        employee.addPhoneNumber(new org.eclipse.persistence.testing.models.employee.domain.PhoneNumber("homey", "613", "2263374"));
        employee.addPhoneNumber(new org.eclipse.persistence.testing.models.employee.domain.PhoneNumber("officey", "416", "8224599"));
        // Many to many
        employee.setProjects(new Vector());
        employee.addProject((org.eclipse.persistence.testing.models.employee.domain.Project)this.unitOfWork.readObject(SmallProject.class));
        employee.addProject((org.eclipse.persistence.testing.models.employee.domain.Project)this.unitOfWork.readObject(LargeProject.class));
    }

    public void changeObjectForThirdClient() {
        Employee employee = (Employee)this.objectToBeWritten;

        // Direct collection
        employee.addResponsibility("make coffee, lots of coffe");
        employee.addResponsibility("buy donuts, pink douts");
        // One to one private/public
        employee.getAddress().setCity("Putkiss land");
        employee.setManager((Employee)this.unitOfWork.readObject(Employee.class));
    }

    public void release() {
        this.clientSession.release();
    }

    public void run() {
        try {
            this.originalObject = this.clientSession.readObject(Employee.class, this.expression);
            this.unitOfWork = this.clientSession.acquireUnitOfWork();
            this.unitOfWork.readAllObjects(Employee.class);// Test read concurrency.
            this.objectToBeWritten = this.unitOfWork.registerObject(this.originalObject);
            changeObject();
            if (!((AbstractSession) this.session).compareObjectsDontMatch(this.originalObject, this.objectToBeWritten)) {
                throw new TestErrorException("The original object was changed through changing the clone.");
            }

            this.unitOfWork.commit();
        } catch (Exception exception) {
            this.server.errorOccured = true;
            exception.printStackTrace(System.out);
        }
    }
}
