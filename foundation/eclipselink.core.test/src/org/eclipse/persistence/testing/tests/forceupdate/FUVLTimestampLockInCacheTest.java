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
package org.eclipse.persistence.testing.tests.forceupdate;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.forceupdate.*;

/**
    Scenario:
    Two threads(here we use two UOWs to simulate two threads) read in the same emplyee
    object. The first thread updates employee's address(relationship) and commits. The
    second thread updates the emplyee's salary(non-relationship attribute based on
    where the emplyee lives)and commits. No exception is thrown but the updated salary
    is invalid. If the first thread forces update the version value of the employee, an
    optimistic lock exception is thrown in the second thread.
    
    (Timestamp locking stores in the cache)
    Test 1: (Correctly use method forceUpdateToVersionField())
    UOW1 updates employee's address,
    calls forceUpdateToVersionField(Object cloneFromUOW1,true) and commits.
    UOW2 updates the employee's salary and commits.
    The test verified an optimistic lock exception is thrown in UOW2.
    
    Test 2: (forceUpdateToVersionField() doesn't effect read-only UOW)
    UOW1 updates employee's address,
    calls forceUpdateToVersionField(Object cloneFromUOW1,true) and commits.
    UOW2 has only read-operation and commits.
    The test verified no optimistic lock exception is thrown in UOW2.
    
    Test 3: (Test method removeForceUpdateToVersionField())
    UOW1 updates employee's address,
    calls forceUpdateToVersionField(cloneFromUOW1,true),COMMIT&RESUMEs.
    It calls removeForceUpdateToVersionField(cloneFromUOW1),
    updates employee's address again and commits.
    UOW2 reads employee after the first commit of UOW1,updates the emplyee's salary
    and commits.
    The test verified no optimistic lock exception is thrown in UOW2.
    
    Test 4: (Demonstrate the result when no using forceUpdateToVersionField())
    UOW1 updates employee's address, commits.
    UOW2 updates the employee's salary and commits.
    The test verified no optimistic lock exception is thrown in UOW2.
*/
public class FUVLTimestampLockInCacheTest extends TransactionalTestCase {
    boolean exceptionCaught = false;
    int testnumber;

    public FUVLTimestampLockInCacheTest(int anInt) {
        testnumber = anInt;
        setName(getName() + "(Test" + anInt + ")");
        switch (testnumber) {
        case 1:
            setDescription("Correctly use method forceUpdateToVersionField()");
            break;
        case 2:
            setDescription("forceUpdateToVersionField() doesn't effect read-only UOW");
            break;
        case 3:
            setDescription("Test method removeForceUpdateToVersionField()");
            break;
        case 4:
            setDescription("Demonstrate the result when no using forceUpdateToVersionField()");
            break;
        default:
            break;
        }
    }

    public void test() {
        switch (testnumber) {
        case 1:
            test1();
            break;
        case 2:
            test2();
            break;
        case 3:
            test3();
            break;
        case 4:
            test4();
            break;
        default:
            break;
        }
    }

    public void test1() {
        UnitOfWork uow1, uow2;

        ExpressionBuilder bldr = new ExpressionBuilder();
        Expression exp1 = bldr.get("firstName").equal("Bob");
        Expression exp2 = bldr.get("lastName").equal("Smith");
        Expression exp = exp1.and(exp2);

        EmployeeTLIC emp = (EmployeeTLIC)getSession().readObject(EmployeeTLIC.class, exp);

        uow1 = getSession().acquireUnitOfWork();
        uow2 = getSession().acquireUnitOfWork();
        EmployeeTLIC clone1 = (EmployeeTLIC)uow1.registerObject(emp);
        EmployeeTLIC clone2 = (EmployeeTLIC)uow2.registerObject(emp);

        uow1.forceUpdateToVersionField(clone1, true);
        clone1.getAddress().setStreet("4 Hutton Centre,Suite 900");
        clone1.getAddress().setCity("Santa Ana");
        clone1.getAddress().setProvince("CA");
        clone1.getAddress().setCountry("USA");
        clone1.getAddress().setPostalCode("92797");
        uow1.commit();

        clone2.setSalary(clone2.getSalary() + 200);
        try {
            uow2.commit();
        } catch (OptimisticLockException exception) {
            exceptionCaught = true;
        }
    }

    public void test2() {
        UnitOfWork uow1, uow2;

        ExpressionBuilder bldr = new ExpressionBuilder();
        Expression exp1 = bldr.get("firstName").equal("Bob");
        Expression exp2 = bldr.get("lastName").equal("Smith");
        Expression exp = exp1.and(exp2);

        EmployeeTLIC emp = (EmployeeTLIC)getSession().readObject(EmployeeTLIC.class, exp);

        uow1 = getSession().acquireUnitOfWork();
        uow2 = getSession().acquireUnitOfWork();
        EmployeeTLIC clone1 = (EmployeeTLIC)uow1.registerObject(emp);
        EmployeeTLIC clone2 = (EmployeeTLIC)uow2.registerObject(emp);

        uow1.forceUpdateToVersionField(clone1, true);
        clone1.getAddress().setStreet("4 Hutton Centre,Suite 900");
        clone1.getAddress().setCity("Santa Ana");
        clone1.getAddress().setProvince("CA");
        clone1.getAddress().setCountry("USA");
        clone1.getAddress().setPostalCode("92797");
        uow1.commit();

        // uow2 has only read-operation here
        //...
        try {
            uow2.commit();
        } catch (OptimisticLockException exception) {
            exceptionCaught = true;
        }
    }

    public void test3() {
        UnitOfWork uow1, uow2;

        ExpressionBuilder bldr = new ExpressionBuilder();
        Expression exp1 = bldr.get("firstName").equal("Bob");
        Expression exp2 = bldr.get("lastName").equal("Smith");
        Expression exp = exp1.and(exp2);

        EmployeeTLIC emp = (EmployeeTLIC)getSession().readObject(EmployeeTLIC.class, exp);

        uow1 = getSession().acquireUnitOfWork();
        EmployeeTLIC clone1 = (EmployeeTLIC)uow1.registerObject(emp);

        uow1.forceUpdateToVersionField(clone1, true);
        clone1.getAddress().setStreet("100 Young Street");
        clone1.getAddress().setCity("Toronto");
        clone1.getAddress().setProvince("ON");
        clone1.getAddress().setCountry("CANADA");
        clone1.getAddress().setPostalCode("K489Y7");
        uow1.commitAndResume();
        uow1.removeForceUpdateToVersionField(clone1);

        uow2 = getSession().acquireUnitOfWork();
        EmployeeTLIC clone2 = (EmployeeTLIC)uow2.registerObject(emp);

        clone1.getAddress().setStreet("4 Hutton Centre,Suite 900");
        clone1.getAddress().setCity("Santa Ana");
        clone1.getAddress().setProvince("CA");
        clone1.getAddress().setCountry("USA");
        clone1.getAddress().setPostalCode("92797");
        uow1.commit();

        clone2.setSalary(clone2.getSalary() + 200);
        try {
            uow2.commit();
        } catch (OptimisticLockException exception) {
            exceptionCaught = true;
        }
    }

    public void test4() {
        UnitOfWork uow1, uow2;

        ExpressionBuilder bldr = new ExpressionBuilder();
        Expression exp1 = bldr.get("firstName").equal("Bob");
        Expression exp2 = bldr.get("lastName").equal("Smith");
        Expression exp = exp1.and(exp2);

        EmployeeTLIC emp = (EmployeeTLIC)getSession().readObject(EmployeeTLIC.class, exp);

        uow1 = getSession().acquireUnitOfWork();
        uow2 = getSession().acquireUnitOfWork();
        EmployeeTLIC clone1 = (EmployeeTLIC)uow1.registerObject(emp);
        EmployeeTLIC clone2 = (EmployeeTLIC)uow2.registerObject(emp);

        clone1.getAddress().setStreet("4 Hutton Centre,Suite 900");
        clone1.getAddress().setCity("Santa Ana");
        clone1.getAddress().setProvince("CA");
        clone1.getAddress().setCountry("USA");
        clone1.getAddress().setPostalCode("92797");
        uow1.commit();

        clone2.setSalary(clone2.getSalary() + 200);
        try {
            uow2.commit();
        } catch (OptimisticLockException exception) {
            exceptionCaught = true;
        }
    }

    protected void verify() {
        switch (testnumber) {
        case 1:
            if (!exceptionCaught)
                throw new TestErrorException("No Optimistic Lock exception was thrown");
            break;
        case 2:
        case 3:
        case 4:
            if (exceptionCaught)
                throw new TestErrorException("Optimistic Lock exception should not have been thrown");
            break;
        default:
            break;
        }
    }
}
