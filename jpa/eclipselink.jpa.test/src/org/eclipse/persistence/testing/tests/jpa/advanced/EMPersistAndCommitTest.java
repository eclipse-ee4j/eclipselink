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

public class EMPersistAndCommitTest extends EntityContainerTestBase  {
    public EMPersistAndCommitTest() {
		setDescription("Test persist and commit in EntityManager");
    }

    //reset gets called twice on error
    protected boolean reset = false;
    
    public void setup (){
        super.setup();
        this.reset = true;
    }
    
    public void reset (){
        if (reset){
            reset = false;
        }
        super.reset();
    }
    
    public Integer[] empIDs = new Integer[2];
    public Integer[] projIDs = new Integer[3];
    public Integer[] addrIDs = new Integer[1];
    
    public void test(){
    
        Employee employee  = new Employee();
        Address address = new Address();
        try {
            beginTransaction();
            employee.setFirstName("First");
            employee.setLastName("Bean");
            address.setCountry("Canada");
            address.setCity("Ottawa");
            employee.setAddress(address);
			getEntityManager().persist(employee);
            commitTransaction();
        } catch (Exception ex) {
            rollbackTransaction();
            throw new TestErrorException("Exception thrown durring create of employee" + ex);
        }
        empIDs[0] = employee.getId();

        Project project= new Project();
        try {
            beginTransaction();
            project.setName("Project # 1");
            project.setDescription("A simple Project");
			getEntityManager().persist(project);
            commitTransaction();
        } catch (Exception ex) {
            rollbackTransaction();
            throw new TestErrorException("Exception thrown durring create of project" + ex);
        }
        projIDs[0] = project.getId();

        project= new SmallProject();
        try {
            beginTransaction();
            project.setName("Small Project # 1");
            project.setDescription("A small Project");
			getEntityManager().persist(project);
            commitTransaction();
        } catch (Exception ex) {
            rollbackTransaction();
            throw new TestErrorException("Exception thrown durring create of SmallProject" + ex);
        }
        projIDs[1] = project.getId();

        project= new LargeProject();
        try {
            beginTransaction();
            project.setName("LargeProject # 1");
            project.setDescription("A Large Project");
            ((LargeProject)project).setBudget(23000.01);
            getEntityManager().persist(project);
            commitTransaction();
        } catch (Exception ex) {
            rollbackTransaction();
            throw new TestErrorException("Exception thrown durring create of largeproject" + ex);
        }
        projIDs[2] = project.getId();
        
        address = new Address();
        try {
            beginTransaction();
            address.setCountry("Russia");
            address.setCity("Moscow");
            getEntityManager().persist(address);
            commitTransaction();
        } catch (Exception ex) {
            rollbackTransaction();
            throw new TestErrorException("Exception thrown durring create of address1" + ex);
        }
        addrIDs[0] = address.getID();
        
        try{
            beginTransaction();
            Employee emp = getEntityManager().find(Employee.class, empIDs[0]);
            emp.addPhoneNumber(ModelExamples.phoneExample1());
            commitTransaction();
        }catch (Exception ex) {
            rollbackTransaction();
            throw new TestErrorException("Exception thrown durring addition of phonenumber" + ex);
        }
    }
    
    public void verify(){
        //lets check the cache for the objects
        Employee emp = getEntityManager().find(Employee.class, empIDs[0]);
        if (emp == null){
            throw new TestErrorException("Employee, empID: " + empIDs[0] + " Not created");
        }
        if (emp.getPhoneNumbers().size() != 1) {
            throw new TestErrorException("Employee, empID: " + empIDs[0] + " PhoneNumber not added");
        }
        if (emp.getAddress() == null) {
            throw new TestErrorException("Employee, empID: " + empIDs[0] + " Address not set");
        }


        Project proj = getEntityManager().find(Project.class, projIDs[0]);
        if (proj == null){
            throw new TestErrorException("Project, projIDs: " + projIDs[0] + " Not created");
        }

        proj = getEntityManager().find(Project.class, projIDs[1]);
        if (proj == null){
            throw new TestErrorException("Project, projIDs: " + projIDs[1] + " Not created");
        }

        proj = getEntityManager().find(Project.class, projIDs[2]);
        if (proj == null){
            throw new TestErrorException("Project, projIDs: " + projIDs[2] + " Not created");
        }

        //lets initialize the identity map to make sure they were persisted
        ((JpaEntityManager)getEntityManager()).getActiveSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        
        emp = getEntityManager().find(Employee.class, empIDs[0]);
        if (emp == null){
            throw new TestErrorException("Employee, empID: " + empIDs[0] + " Not created");
        }

        if (emp.getPhoneNumbers().size() != 1) {
            throw new TestErrorException("Employee, empID: " + empIDs[0] + " PhoneNumber not added");
        }

        proj = getEntityManager().find(Project.class, projIDs[0]);
        if (proj == null){
            throw new TestErrorException("Project, projIDs: " + projIDs[0] + " Not created");
        }

        proj = getEntityManager().find(Project.class, projIDs[1]);
        if (proj == null){
            throw new TestErrorException("Project, projIDs: " + projIDs[1] + " Not created");
        }

        proj = getEntityManager().find(Project.class, projIDs[2]);
        if (proj == null){
            throw new TestErrorException("Project, projIDs: " + projIDs[2] + " Not created");
        }

        Address address = getEntityManager().find(Address.class, addrIDs[0]);
        if (address == null){
            throw new TestErrorException("Address, addrIDs: " + addrIDs[0] + " Not created");
        }
    }
}
