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

public class EMCascadingPersistAndCommitTest extends EntityContainerTestBase  {
    public EMCascadingPersistAndCommitTest() {
		setDescription("Test cascading persist and commit in EntityManager");
    }

    public Integer[] empIDs = new Integer[2];
    
    public void test(){
    
        Employee employee = new Employee();
        employee.setFirstName("First");
        employee.setLastName("Bean");
		
        Project project = new Project();
        project.setName("Project # 1");
        project.setDescription("A simple Project");

        PhoneNumber phone = new PhoneNumber("Work", "613", "9876543");
        
        employee.addProject(project);
        employee.addPhoneNumber(phone);
        
        try {
            beginTransaction();        
            getEntityManager().persist(employee);
            empIDs[0] = employee.getId();
            commitTransaction();
        } catch (Exception ex) {
            rollbackTransaction();
            throw new TestErrorException("Exception thrown durring persist and flush" + ex);
        }
    }
    
    public void verify(){
        //lets check the cache for the objects
        Employee emp = getEntityManager().find(Employee.class, empIDs[0]);
        if (emp == null){
            throw new TestErrorException("Employee, empID: " + empIDs[0] + " Not created");
        }
        if (emp.getProjects().size() != 1) {
            throw new TestErrorException("Employee, empID: " + empIDs[0] + " Project not added");
        }
        if (emp.getPhoneNumbers().size() != 1) {
            throw new TestErrorException("Employee, empID: " + empIDs[0] + " PhoneNumber not added");
        }
        
        //lets initialize the identity map to make sure they were persisted
        ((JpaEntityManager)getEntityManager()).getActiveSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        emp = getEntityManager().find(Employee.class, empIDs[0]);
        if (emp == null){
            throw new TestErrorException("Employee, empID: " + empIDs[0] + " Not created");
        }
        if (emp.getProjects().size() != 1) {
            throw new TestErrorException("Employee, empID: " + empIDs[0] + " Project not added");
        }
        if (emp.getPhoneNumbers().size() != 1) {
            throw new TestErrorException("Employee, empID: " + empIDs[0] + " PhoneNumber not added");
        }
    }
}
