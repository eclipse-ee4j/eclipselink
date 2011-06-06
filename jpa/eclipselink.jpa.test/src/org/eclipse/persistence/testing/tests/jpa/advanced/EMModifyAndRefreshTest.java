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

import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.jpa.advanced.*;
import org.eclipse.persistence.testing.tests.jpa.EntityContainerTestBase;

public class EMModifyAndRefreshTest extends EntityContainerTestBase  {
    public EMModifyAndRefreshTest() {
		setDescription("Test modify and refresh in EntityManager");
    }

    public Integer[] empIDs = new Integer[2];
    public Integer[] projIDs = new Integer[3];
    
    public void setup (){
        super.setup();

        Employee employee  = ModelExamples.employeeExample1();		
        Project project= ModelExamples.projectExample1();
        
        try {
            beginTransaction();
            getEntityManager().persist(employee);
            getEntityManager().persist(project);            
            commitTransaction();
	} catch (Exception ex) {
            rollbackTransaction();
            ex.printStackTrace();
            throw new TestErrorException("Exception thrown durring persist and flush" + ex);
        }
        
        empIDs[0] = employee.getId();
        projIDs[0] = project.getId();            
    }
    
    public void test(){
    
        try {
            beginTransaction();
            Employee employee = getEntityManager().find(Employee.class, empIDs[0]);
            employee.setFirstName("Tobin");
            employee.setSalary(20000);
            getEntityManager().refresh(employee);

            Project project = getEntityManager().find(Project.class, projIDs[0]);            
            project.setDescription("To reevaluate the plan");
            project.setName("Reassessment");
            getEntityManager().refresh(project);
            
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
        if(employee.getSalary() == 20000) {
            throw new TestErrorException("Employee ID :" + empIDs[0] + " salary updated after refresh");
        }
		Project project = getEntityManager().find(Project.class, projIDs[0]);            
        if(project.getDescription().equals("To reevaluate the plan")) {
            throw new TestErrorException("Project ID :" + projIDs[0] + " description updated after refresh");
        }
        if(project.getName().equals("Reassessment")) {
            throw new TestErrorException("Project ID :" + projIDs[0] + " name updated after refresh");
        }
        
        //lets initialize the identity map to make sure they were persisted
        ((JpaEntityManager)getEntityManager()).getActiveSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        employee = getEntityManager().find(Employee.class, empIDs[0]);
        if(employee.getFirstName().equals("Tobin")) {
            throw new TestErrorException("Employee ID :" + empIDs[0] + " first name updated after refresh");
        }
        if(employee.getSalary() == 20000) {
            throw new TestErrorException("Employee ID :" + empIDs[0] + " salary updated after refresh");
        }
		project = getEntityManager().find(Project.class, projIDs[0]);            
        if(project.getDescription().equals("To reevaluate the plan")) {
            throw new TestErrorException("Project ID :" + projIDs[0] + " description updated after refresh");
        }
        if(project.getName().equals("Reassessment")) {
            throw new TestErrorException("Project ID :" + projIDs[0] + " name updated after refresh");
        }
    }
}
