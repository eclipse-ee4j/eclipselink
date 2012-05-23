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
package org.eclipse.persistence.testing.tests.queries;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.expressions.*;
import java.util.*;

/**
 * <b>Purpose:</b>Helps setup and reset a wide variety of conforming tests.
 */
public class ConformingTestConfiguration {
    UnitOfWork unitOfWork;

    public ConformingTestConfiguration() {
    }

    public void setup(Session session) {
        //	setup(session, false);
        //}
        //public void setup(Session session, boolean registerAll) {
        unitOfWork = session.acquireUnitOfWork();

        // Assume the query will be for all employees with a salary over
        // 50000.
        ExpressionBuilder emp = new ExpressionBuilder();
        Expression criteria = emp.get("salary").lessThan(50000);
        ReadAllQuery query = new ReadAllQuery(Employee.class, criteria);
        Vector initiallyUnconforming = (Vector)session.executeQuery(query);

        Employee modifiedIn1 = (Employee)unitOfWork.registerExistingObject(initiallyUnconforming.elementAt(0));
        Employee modifiedIn2 = (Employee)unitOfWork.registerExistingObject(initiallyUnconforming.elementAt(initiallyUnconforming.size() - 1));
        Employee newEmployee = (Employee)unitOfWork.newInstance(Employee.class);

        emp = new ExpressionBuilder();
        criteria = emp.get("salary").greaterThan(50000);
        query = new ReadAllQuery(Employee.class, criteria);
        Vector initiallyConforming = (Vector)session.executeQuery(query);

        Employee deleted = (Employee)initiallyConforming.elementAt(0);
        Employee modifiedOut1 = (Employee)unitOfWork.registerExistingObject(initiallyConforming.elementAt(1));
        Employee modifiedOut2 = (Employee)unitOfWork.registerExistingObject(initiallyConforming.elementAt(initiallyConforming.size() - 1));

        modifiedIn1.setSalary(80000);
        modifiedIn2.setSalary(70000);
        newEmployee.setSalary(60000);
        unitOfWork.deleteObject(deleted);
        modifiedOut1.setSalary(40000);
        modifiedOut2.setSalary(30000);

    }

    public void reset() {
        unitOfWork.release();
    }

    public UnitOfWork getUnitOfWork() {
        return unitOfWork;
    }

    public void setUnitOfWork(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }
}
