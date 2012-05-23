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
import org.eclipse.persistence.testing.tests.jpa.EntityContainerTestBase;

public class EMCascadingModifyAndRefreshTest extends EntityContainerTestBase  {
    public EMCascadingModifyAndRefreshTest() {
		setDescription("Test cascading modify and refresh in EntityManager");
    }

    public Integer[] empIDs = new Integer[2];
    
    public void setup (){
        super.setup();

        Employee employee  = ModelExamples.employeeExample1();		
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
    
        try {
            beginTransaction();
            Employee employee = getEntityManager().find(Employee.class, empIDs[0]);
            employee.setFirstName("Tobin");
            PhoneNumber phone = employee.getPhoneNumbers().iterator().next();
            phone.setAreaCode("416");
            phone.setNumber("9876543");
            employee.addPhoneNumber(ModelExamples.phoneExample7());           
            getEntityManager().refresh(employee);
            commitTransaction();
        } catch (Exception ex) {
            rollbackTransaction();
            ex.printStackTrace();
            throw new TestErrorException("Exception thrown durring persist and flush" + ex);
        }
    }
    
    public void verify(){
        //lets check the cache for the objects
        Employee employee = getEntityManager().find(Employee.class, empIDs[0]);
        if(employee.getFirstName().equals("Tobin")) {
            throw new TestErrorException("Employee ID :" + empIDs[0] + " first name updated after refresh");
        }
        if(employee.getPhoneNumbers().size() > 1) {
            throw new TestErrorException("Employee ID :" + empIDs[0] + " phone number added after refresh");
        }
        PhoneNumber phone = employee.getPhoneNumbers().iterator().next();
        if(phone.getAreaCode().equals("416")) {
            throw new TestErrorException("Employee ID :" + empIDs[0] + " phone number areacode modified after refresh");
        }
        if(phone.getNumber().equals("9876543")) {
            throw new TestErrorException("Employee ID :" + empIDs[0] + " phone number modified after refresh");
        }
        
        //lets initialize the identity map to make sure they were persisted
        ((JpaEntityManager)getEntityManager()).getActiveSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        employee = getEntityManager().find(Employee.class, empIDs[0]);
        if(employee.getFirstName().equals("Tobin")) {
            throw new TestErrorException("Employee ID :" + empIDs[0] + " first name updated after refresh");
        }
        if(employee.getPhoneNumbers().size() > 1) {
            throw new TestErrorException("Employee ID :" + empIDs[0] + " phone number added after refresh");
        }
        phone = employee.getPhoneNumbers().iterator().next();
        if(phone.getAreaCode().equals("416")) {
            throw new TestErrorException("Employee ID :" + empIDs[0] + " phone number areacode modified after refresh");
        }
        if(phone.getNumber().equals("9876543")) {
            throw new TestErrorException("Employee ID :" + empIDs[0] + " phone number modified after refresh");
        }
    }
}
