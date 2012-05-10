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
package org.eclipse.persistence.testing.tests.jpa.advanced;

import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.jpa.advanced.*;
import org.eclipse.persistence.testing.tests.jpa.CMP3TestModel;
import org.eclipse.persistence.testing.tests.jpa.EntityContainerTestBase;

public class EMCascadingModifyAndMergeTest extends EntityContainerTestBase  {
    public EMCascadingModifyAndMergeTest() {
		setDescription("Test cascading modify and merge in EntityManager");
    }

    public Integer[] empIDs = new Integer[2];
    Employee employee;
    
    public void setup (){
        super.setup();

        employee = ModelExamples.employeeExample1();	
        employee.addPhoneNumber(ModelExamples.phoneExample3());
        
        try {
            beginTransaction();
            getEntityManager().persist(employee);
            commitTransaction();
        } catch (Exception ex) {
            rollbackTransaction();
            ex.printStackTrace();
            throw new TestErrorException("Exception thrown durring persist and flush" + ex);
        }
        
        empIDs[0] = employee.getId();
    }
    
    public void test(){
        employee.setFirstName("Ivy");
        PhoneNumber phone = employee.getPhoneNumbers().iterator().next();
        phone.setAreaCode("519");
        phone.setNumber("9876543");
        
        try {
            CMP3TestModel.createEntityManager();
            beginTransaction();
            Employee emp = getEntityManager().find(Employee.class, empIDs[0]);
            emp.setFirstName("Tobin");
            PhoneNumber phon = emp.getPhoneNumbers().iterator().next();
            phon.setAreaCode("416");
            phon.setNumber("7654321");
//            emp.addPhoneNumber(ModelExamples.phoneExample7());           
            getEntityManager().merge(employee);
            commitTransaction();
        } catch (Exception ex) {
            rollbackTransaction();
            ex.printStackTrace();
            throw new TestErrorException("Exception thrown durring persist and flush" + ex);
        }
    }
    
    public void verify(){
        //lets check the cache for the objects
        employee = getEntityManager().find(Employee.class, empIDs[0]);
        if(!employee.getFirstName().equals("Ivy")) {
            throw new TestErrorException("Employee ID :" + empIDs[0] + " first name was not merged");
        }
        if(employee.getPhoneNumbers().size() > 1) {
            throw new TestErrorException("Employee ID :" + empIDs[0] + " phone number added after merge");
        }
        PhoneNumber phone = employee.getPhoneNumbers().iterator().next();
        if(!phone.getAreaCode().equals("519")) {
            throw new TestErrorException("Employee ID :" + empIDs[0] + " phone number areacode was not merged");
        }
        if(!phone.getNumber().equals("9876543")) {
            throw new TestErrorException("Employee ID :" + empIDs[0] + " phone number was not merged");
        }
        
        //lets initialize the identity map to make sure they were persisted
        ((JpaEntityManager)getEntityManager()).getActiveSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        employee = getEntityManager().find(Employee.class, empIDs[0]);
        if(!employee.getFirstName().equals("Ivy")) {
            throw new TestErrorException("Employee ID :" + empIDs[0] + " first name was not merged");
        }
        if(employee.getPhoneNumbers().size() > 1) {
            throw new TestErrorException("Employee ID :" + empIDs[0] + " phone number added after merge");
        }
        phone = employee.getPhoneNumbers().iterator().next();
        if(!phone.getAreaCode().equals("519")) {
            throw new TestErrorException("Employee ID :" + empIDs[0] + " phone number areacode was not merged");
        }
        if(!phone.getNumber().equals("9876543")) {
            throw new TestErrorException("Employee ID :" + empIDs[0] + " phone number was not merged");
        }
    }
}
