/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Vikram Bhatia
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.unitofwork;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Address;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


public class NestedUnitOfWorkReadOnlyClassTest extends AutoVerifyTestCase {
    public NestedUnitOfWorkReadOnlyClassTest() {
        setDescription("Nested Unit Of Work with Read Only class");
    }

    private String postalCode;
    private Employee originalEmployee;
    private Address originalAddress;

    public void setup() {
        getAbstractSession().beginTransaction();
        postalCode = "AB7J98";
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Address address = (Address) uow.registerObject(new Address());
        uow.assignSequenceNumber(address);
        address.setPostalCode(postalCode);
        address.setCity("Toronto");
        address.setCountry("CANADA");
        uow.commit();
        getAbstractSession().commitTransaction();
        originalAddress = address;
    }

    public void reset() {
        UnitOfWork deleteUOW = getSession().acquireUnitOfWork();
        deleteUOW.deleteObject(deleteUOW.readObject(originalAddress));
        deleteUOW.deleteObject(deleteUOW.readObject(originalEmployee));
        deleteUOW.commit();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void test() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        // start root transaction
        UnitOfWork rootUOW = getSession().acquireUnitOfWork();
        
        Address addressRO = (Address)getSession().readObject(Address.class, new ExpressionBuilder().get("postalCode").equal(postalCode));
        
        if (addressRO == null) {
            throw new TestErrorException("Unable to find address.");
        }
        // start nested transaction
        UnitOfWork nestedUOW = rootUOW.acquireUnitOfWork();
        nestedUOW.addReadOnlyClass(Address.class);
        
        Employee emp = (Employee)nestedUOW.registerObject(new Employee());
        nestedUOW.assignSequenceNumber(emp);
        emp.setFirstName("John");
        emp.setAddress(addressRO);
        
        nestedUOW.commit();
        rootUOW.commit();
        
        originalEmployee = emp;
    }

    protected void verify() {
    }
}
