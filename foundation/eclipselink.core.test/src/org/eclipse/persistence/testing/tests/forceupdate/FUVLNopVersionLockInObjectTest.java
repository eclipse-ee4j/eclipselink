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
package org.eclipse.persistence.testing.tests.forceupdate;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.models.forceupdate.*;

/**
    Scenario:
    One thread reads in two objects, Employee and Address. It begins to recalculate employee's
    Salary based on the employee's office address. Another thread changes the employee's address. Then
    the first commits. No exception would be thrown, but the entire calculation would be invalid.
    If the user forces optimistic locking check of the Address in the first thread, an optimistic
    lock exception is thrown.(here we use two UOWs to simulate two threads)
    
    (Version locking stores in the Object)
    Test 1:(Correctly use method forceUpdateToVersionField() with NOP)
    UOW1 calculates employee's salary based on the emplyee's office address,
    calls forceUpdateToVersionField(Object addressCloneFromUOW1,false).
    UOW2 updates the employee's address and commits.
    UOW1 commits.
    The test verified an optimistic lock exception is thrown in UOW1.
    
    Test 2: (forceUpdateToVersionField() with NOP effects read-only UOW)
    UOW1 has only read-operation,
    calls forceUpdateToVersionField(Object addressCloneFromUOW1,false) and commits.
    UOW2 updates the emplyee's address and commits.
    UOW1 commits.
    The test verified an optimistic lock exception is thrown in UOW1.
    
    Test 3: (Test method removeForceUpdateToVersionField())
    UOW1 calculates employee's salary based on the emplyee's office address,
    calls forceUpdateToVersionField(Object addressCloneFromUOW1,false),
    calls removeForceUpdateToVersionField(Object addressCloneFromUOW1).
    UOW2 updates the employee's address and commits.
    UOW1 commits.
    The test verified no optimistic lock exception is thrown in UOW1.
    
    Test 4: (Demonstrate the result when no using forceUpdateToVersionField() with NOP )
    UOW1 calculates employee's salary based on the emplyee's office address,
    UOW2 updates the employee's address and commits.
    UOW1 commits.
    The test verified no optimistic lock exception is thrown in UOW1.
*/
public class FUVLNopVersionLockInObjectTest extends TransactionalTestCase {
    boolean exceptionCaught = false;
    int testnumber;

    public FUVLNopVersionLockInObjectTest(int anInt) {
        testnumber = anInt;
        setName(getName() + "(Test" + anInt + ")");
        switch (testnumber) {
        case 1:
            setDescription("Correctly use method forceUpdateToVersionField() with NOP");
            break;
        case 2:
            setDescription("forceUpdateToVersionField() with NOP doesn't effect read-only UOW");
            break;
        case 3:
            setDescription("Test method removeForceUpdateToVersionField()");
            break;
        case 4:
            setDescription("Demonstrate the result when no using forceUpdateToVersionField() with NOP");
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

        EmployeeVLIO emp = (EmployeeVLIO)getSession().readObject(EmployeeVLIO.class, exp);

        uow1 = getSession().acquireUnitOfWork();
        uow2 = getSession().acquireUnitOfWork();
        EmployeeVLIO clone1 = (EmployeeVLIO)uow1.registerObject(emp);
        EmployeeVLIO clone2 = (EmployeeVLIO)uow2.registerObject(emp);

        uow1.forceUpdateToVersionField(clone1.getAddress(), false);
        //caculate salary based on office address
        if (clone1.getAddress().getCountry() == "Canada") {
            clone1.setSalary(clone1.getSalary() + 200);
        }

        clone2.getAddress().setStreet("4 Hutton Centre,Suite 900");
        clone2.getAddress().setCity("Santa Ana");
        clone2.getAddress().setProvince("CA");
        clone2.getAddress().setCountry("USA");
        clone2.getAddress().setPostalCode("92797");
        uow2.commit();

        try {
            uow1.commit();
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

        EmployeeVLIO emp = (EmployeeVLIO)getSession().readObject(EmployeeVLIO.class, exp);

        uow1 = getSession().acquireUnitOfWork();
        uow2 = getSession().acquireUnitOfWork();
        EmployeeVLIO clone1 = (EmployeeVLIO)uow1.registerObject(emp);
        EmployeeVLIO clone2 = (EmployeeVLIO)uow2.registerObject(emp);

        uow1.forceUpdateToVersionField(clone1.getAddress(), false);
        // uow1 has only read-operation here
        //...

        clone2.getAddress().setStreet("4 Hutton Centre,Suite 900");
        clone2.getAddress().setCity("Santa Ana");
        clone2.getAddress().setProvince("CA");
        clone2.getAddress().setCountry("USA");
        clone2.getAddress().setPostalCode("92797");
        uow2.commit();

        try {
            uow1.commit();
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

        EmployeeVLIO emp = (EmployeeVLIO)getSession().readObject(EmployeeVLIO.class, exp);

        uow1 = getSession().acquireUnitOfWork();
        uow2 = getSession().acquireUnitOfWork();
        EmployeeVLIO clone1 = (EmployeeVLIO)uow1.registerObject(emp);
        EmployeeVLIO clone2 = (EmployeeVLIO)uow2.registerObject(emp);

        uow1.forceUpdateToVersionField(clone1.getAddress(), false);
        uow1.removeForceUpdateToVersionField(clone1.getAddress());
        //caculate salary based on office address
        if (clone1.getAddress().getCountry() == "Canada") {
            clone1.setSalary(clone1.getSalary() + 200);
        }

        clone2.getAddress().setStreet("4 Hutton Centre,Suite 900");
        clone2.getAddress().setCity("Santa Ana");
        clone2.getAddress().setProvince("CA");
        clone2.getAddress().setCountry("USA");
        clone2.getAddress().setPostalCode("92797");
        uow2.commit();

        try {
            uow1.commit();
        } catch (OptimisticLockException exception) {
            exceptionCaught = true;
        }
    }

    public void test4() {
        UnitOfWork uow1, uow2;
        EmployeeVLIO emp1, emp2;

        ExpressionBuilder bldr = new ExpressionBuilder();
        Expression exp1 = bldr.get("firstName").equal("Bob");
        Expression exp2 = bldr.get("lastName").equal("Smith");
        Expression exp = exp1.and(exp2);

        EmployeeVLIO emp = (EmployeeVLIO)getSession().readObject(EmployeeVLIO.class, exp);

        uow1 = getSession().acquireUnitOfWork();
        uow2 = getSession().acquireUnitOfWork();
        EmployeeVLIO clone1 = (EmployeeVLIO)uow1.registerObject(emp);
        EmployeeVLIO clone2 = (EmployeeVLIO)uow2.registerObject(emp);

        //caculate salary based on office address
        if (clone1.getAddress().getCountry() == "Canada") {
            clone1.setSalary(clone1.getSalary() + 200);
        }

        clone2.getAddress().setStreet("4 Hutton Centre,Suite 900");
        clone2.getAddress().setCity("Santa Ana");
        clone2.getAddress().setProvince("CA");
        clone2.getAddress().setCountry("USA");
        clone2.getAddress().setPostalCode("92797");
        uow2.commit();

        try {
            uow1.commit();
        } catch (OptimisticLockException exception) {
            exceptionCaught = true;
        }
    }

    protected void verify() {
        switch (testnumber) {
        case 1:
        case 2:
            if (!exceptionCaught)
                throw new TestErrorException("No Optimistic Lock exception was thrown");
            break;
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
