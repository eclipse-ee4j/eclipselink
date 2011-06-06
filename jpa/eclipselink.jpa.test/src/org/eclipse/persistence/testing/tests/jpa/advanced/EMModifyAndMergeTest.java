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
import org.eclipse.persistence.testing.tests.jpa.CMP3TestModel;
import org.eclipse.persistence.testing.tests.jpa.EntityContainerTestBase;

public class EMModifyAndMergeTest extends EntityContainerTestBase  {
    public EMModifyAndMergeTest() {
		setDescription("Test modify and merge in EntityManager");
    }

    public Integer[] empIDs = new Integer[2];
    public Integer[] projIDs = new Integer[3];
    Employee employee;
    Project project;
    
    public void setup (){
        super.setup();

        employee  = ModelExamples.employeeExample1();		
        project = ModelExamples.projectExample1();
        
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
        employee.setFirstName("Ivy");
        employee.setSalary(30000);
        project.setDescription("To change the plan completely");
        project.setName("Refactory");

        try {
            CMP3TestModel.createEntityManager();
            beginTransaction();
            Employee emp = getEntityManager().find(Employee.class, empIDs[0]);
            emp.setFirstName("Tobin");
            emp.setSalary(20000);
            getEntityManager().merge(employee);

            Project proj = getEntityManager().find(Project.class, projIDs[0]);            
            proj.setDescription("To reevaluate the plan");
            proj.setName("Reassessment");
            getEntityManager().merge(project);
            
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
        if(employee.getSalary() != 30000) {
            throw new TestErrorException("Employee ID :" + empIDs[0] + " salary was not merge");
        }
        project = getEntityManager().find(Project.class, projIDs[0]);            
        if(!project.getDescription().equals("To change the plan completely")) {
            throw new TestErrorException("Project ID :" + projIDs[0] + " description was not merged");
        }
        if(!project.getName().equals("Refactory")) {
            throw new TestErrorException("Project ID :" + projIDs[0] + " name was not merged");
        }
        
        //lets initialize the identity map to make sure they were persisted
        ((JpaEntityManager)getEntityManager()).getActiveSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        employee = getEntityManager().find(Employee.class, empIDs[0]);
        if(!employee.getFirstName().equals("Ivy")) {
            throw new TestErrorException("Employee ID :" + empIDs[0] + " first name was not merged");
        }
        if(employee.getSalary() != 30000) {
            throw new TestErrorException("Employee ID :" + empIDs[0] + " salary was not merge");
        }
        project = getEntityManager().find(Project.class, projIDs[0]);            
        if(!project.getDescription().equals("To change the plan completely")) {
            throw new TestErrorException("Project ID :" + projIDs[0] + " description was not merged");
        }
        if(!project.getName().equals("Refactory")) {
            throw new TestErrorException("Project ID :" + projIDs[0] + " name was not merged");
        }
    }
}
