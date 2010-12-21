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

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.domain.PhoneNumber;


//Bug4736360  A new clone from parent UOW should only be registered once in a nested UOW
public class NestedUOWWithNewObjectRegisteredTwiceTest extends AutoVerifyTestCase {
    public NestedUOWWithNewObjectRegisteredTwiceTest() {
        setDescription("");
    }

    String firstName;
    String areaCode;
    PhoneNumber pNT02REL;
    PhoneNumber pNT02REG;

    public void setup() {
        getAbstractSession().beginTransaction();

        firstName = "Master_" + System.currentTimeMillis();
        areaCode = "617";

        UnitOfWork uow = getSession().acquireUnitOfWork();
        Employee emp = (Employee)uow.registerObject(new Employee());
        emp.setFirstName(firstName);
        uow.commit();
    }

    public void reset() {
        getAbstractSession().rollbackTransaction();
    }

    public void test() {

        Employee empRO = 
            (Employee)getSession().readObject(Employee.class, new ExpressionBuilder().get("firstName").equal(firstName));

        // start transaction T0
        UnitOfWork uowT0 = getSession().acquireUnitOfWork();
        Employee empT0 = (Employee)uowT0.registerObject(empRO);

        // create phone number and commit in a nested unit of work
        UnitOfWork uowT01 = uowT0.acquireUnitOfWork();
        Employee empT01 = (Employee)uowT01.registerObject(empT0);
        PhoneNumber pN = (PhoneNumber)uowT01.registerObject(new PhoneNumber());
        pN.setAreaCode(areaCode);
        empT01.addPhoneNumber(pN);
        uowT01.commit();

        // the phone number is a new object in uowT0
        PhoneNumber pNT0 = (PhoneNumber)empT0.getPhoneNumbers().get(0);

        // start another nested unit of work
        UnitOfWork uowT02 = uowT0.acquireUnitOfWork();
        uowT02.setShouldNewObjectsBeCached(true);
        // load empT0 from the parent cache and clone and register it
        Employee empT02 = (Employee)uowT02.registerObject(empT0);
        // get detail via relation.  this will trigger the value holder and clone and register phone number in uowT02
        pNT02REL = (PhoneNumber)empT02.getPhoneNumbers().get(0);
        // get detail via registration.  this should return the same clone as referenced by pNT02REL
        pNT02REG = (PhoneNumber)uowT02.registerObject(pNT0);

        uowT0.commit();
    }

    protected void verify() {
        if (pNT02REL != pNT02REG) {
            throw new TestErrorException("PhoneNumber is registered twice in the nested unit of work");
        }
    }
}
