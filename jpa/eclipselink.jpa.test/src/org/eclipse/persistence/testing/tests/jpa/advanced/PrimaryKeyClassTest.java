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
package org.eclipse.persistence.testing.tests.jpa.advanced;

import java.util.*;
import org.eclipse.persistence.testing.models.jpa.advanced.*;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.tests.jpa.EntityContainerTestBase;

/**
 * CRUD tests for an entity that has a PK class (PhoneNumber). Test looks for
 * exceptions on any of the CRUD operations of an employee with multiple
 * phone numbers.
 *
 * @author Guy Pelletier
 */
public class PrimaryKeyClassTest extends EntityContainerTestBase  {
    protected boolean reset = false;    // reset gets called twice on error
    protected Exception m_createException;
    protected Exception m_readException;
    protected Exception m_updateException;
    protected Exception m_deleteException;
    
    public void setup () {
        super.setup();
        this.reset = true;
        
        // Clear the cache so we are working from scratch.
        ((EntityManagerImpl)getEntityManager()).getActiveSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }
    
    public void reset () {
        if (reset) {
            reset = false;
        }
        super.reset();
    }
    
    public void test() throws Exception {
        int empId = createTest();
        readTest(empId);
        updateTest(empId);
        deleteTest(empId);
    }
    
    /**
     * Create an employee with multiple phone numbers.
     */
    private int createTest() {
        Vector phoneNumbers = new Vector();
        Employee employee  = new Employee();
        
        try {
            beginTransaction();
            employee.setFirstName("Guy");
            employee.setLastName("Pelletier");
            
            PhoneNumber homeNumber = new PhoneNumber();
            homeNumber.setAreaCode("61x");
            homeNumber.setNumber("823-6262");
            homeNumber.setOwner(employee);
            homeNumber.setType("Home");
            phoneNumbers.add(homeNumber);
            
            PhoneNumber cellNumber = new PhoneNumber();
            cellNumber.setAreaCode("61x");
            cellNumber.setNumber("299-6747");
            cellNumber.setOwner(employee);
            cellNumber.setType("Cell");
            phoneNumbers.add(cellNumber);
            
            PhoneNumber workNumber = new PhoneNumber();
            workNumber.setAreaCode("61x");
            workNumber.setNumber("288-4639");
            workNumber.setOwner(employee);
            workNumber.setType("Work");
            phoneNumbers.add(workNumber);
            
            employee.setPhoneNumbers(phoneNumbers);
            
            getEntityManager().persist(employee);
            
            commitTransaction();
        } catch (Exception e) {
            rollbackTransaction();
            m_createException = e;
        }
        
        return employee.getId();
    }
    
    /**
     * Read the employee with multiple phone numbers.
     */
    private void readTest(int empId) {
        try {
            getEntityManager().find(Employee.class, empId);
        } catch (Exception e) {
            m_readException = e;
        }
    }
    
    /**
     * Update the phone numbers on the employee with multiple phone numbers.
     */
    private void updateTest(int empId) {
        try {
            beginTransaction();
            
            Employee employee = getEntityManager().find(Employee.class, empId);
            Vector phones = (Vector) employee.getPhoneNumbers(); 
            Enumeration e = phones.elements(); 

            
            while (e.hasMoreElements()) {
                PhoneNumber phone = (PhoneNumber) e.nextElement();
                phone.setAreaCode("613");
            }
            
            getEntityManager().persist(employee);
            
            commitTransaction();
        } catch (Exception e) {
            rollbackTransaction();
            m_updateException = e;
        }
    }
    
    /**
     * Delete the first phone number on the employee with multiple phone numbers.
     */
    private void deleteTest(int empId) {
        try {
            beginTransaction();
            Employee employee = getEntityManager().find(Employee.class, empId);
            getEntityManager().remove(((Vector) employee.getPhoneNumbers()).firstElement());
            commitTransaction();
        } catch (Exception e) {
            rollbackTransaction();
            m_deleteException = e;
        }
    }
    
    public void verify() {
        if (m_createException != null) {
            throw new TestErrorException("Create exception was caught. " + m_createException);
        }
        
        if (m_readException != null) {
            throw new TestErrorException("Read exception was caught. " + m_readException);
        }
        
        if (m_updateException != null) {
            throw new TestErrorException("Update exception was caught. " + m_updateException);
        }
        
        if (m_deleteException != null) {
            throw new TestErrorException("Delete exception was caught. " + m_deleteException);
        }
    }
}
