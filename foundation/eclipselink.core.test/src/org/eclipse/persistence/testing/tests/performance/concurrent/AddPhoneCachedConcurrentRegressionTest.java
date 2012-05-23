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
package org.eclipse.persistence.testing.tests.performance.concurrent;

import java.util.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.models.performance.toplink.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * This test compares the concurrency of updates.
 * This test must be run on a multi-CPU machine to be meaningful.
 */
public class AddPhoneCachedConcurrentRegressionTest extends ConcurrentPerformanceRegressionTest {
    protected Employee employee;
    protected List allObjects;
    protected List phones;
    protected int index;

    public AddPhoneCachedConcurrentRegressionTest() {
        setDescription("This tests the concurrency of adds/removes from a collection.");
    }
    
    /**
     * Return the next index to use as the phone type id.
     */
    public synchronized int incrementIndex() {
        return index++;
    }

    /**
     * Find any employee.
     */
    public void setup() {
        super.setup();
        Expression expression = new ExpressionBuilder().get("firstName").equal("Bob");
        employee = (Employee)getServerSession().acquireClientSession().readObject(Employee.class, expression);
        phones = new ArrayList(employee.getPhoneNumbers());
        // Fully load the cache.
        allObjects = getServerSession().acquireClientSession().readAllObjects(Employee.class);
        index = 0;
    }

    /**
     * Add/remove a phone to/from the employee.
     */
    public void runTask() throws Exception {
        Session client = getServerSession().acquireClientSession();
        UnitOfWork uow = client.acquireUnitOfWork();
        Employee employee = (Employee)uow.readObject(this.employee);
        PhoneNumber phone = new PhoneNumber();
        int currentIndex = incrementIndex();
        phone.setType("new" + currentIndex);
        employee.addPhoneNumber(phone);
        uow.commit();
        uow = client.acquireUnitOfWork();
        employee = (Employee)uow.readObject(employee);
        phone = (PhoneNumber)uow.readObject(phone);
        employee.removePhoneNumber(phone);
        uow.commit();
        client.release();
    }
}
