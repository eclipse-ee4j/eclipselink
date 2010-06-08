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
package org.eclipse.persistence.testing.tests.jpa.advanced;

import org.eclipse.persistence.testing.models.jpa.advanced.*;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.tests.jpa.EntityContainerTestBase;

/**
 * Root test case for all callback event tests
 *
 * @author Guy Pelletier
 */
public class CallbackEventTest extends EntityContainerTestBase  {
    // reset gets called twice on error
    protected boolean reset = false;
    
    protected Project m_project;
    protected Employee m_employee;
    protected int m_beforeEvent, m_afterEvent;
    
    public void setup () {
        super.setup();
        this.reset = true;

        // Stick an employee in there, re-used in sub tests.        
        m_employee  = new Employee();
        m_project= new SmallProject();
        
        try {
            beginTransaction();
            
            m_employee.setFirstName("Guy");
            m_employee.setLastName("Pelletier");
            getEntityManager().persist(m_employee);
            
            m_project.setName("A tiny project");
            m_project.setDescription("Must be an easy one");
            getEntityManager().persist(m_project);
            
            commitTransaction();
        } catch (Exception ex) {
            rollbackTransaction();
            throw new TestErrorException("Exception thrown during setup try to create an employee" + ex);
        }
        
        // Clear the cache so we are working from scratch.
        ((EntityManagerImpl)getEntityManager()).getActiveSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }
    
    public void reset () {
        if (reset) {
            reset = false;
        }
        super.reset();
    }
    
    public void verify() {
        if ((m_afterEvent-m_beforeEvent) != 1) {
            throw new TestErrorException("The callback method was called "+(m_afterEvent - m_beforeEvent)+
                    " times.  It should have been called only once");
        }
    }
    
    protected Employee persistNewEmployee() throws Exception {
        Employee employee  = null;
        beginTransaction();
        try{
            employee  = new Employee();
            employee.setFirstName("Kirty");
            employee.setLastName("Pelletier");
            getEntityManager().persist(employee);
            commitTransaction();
        }catch (RuntimeException ex){
            rollbackTransaction();
            throw ex;
        }
        return employee;
    }
    
    protected void removeEmployee() throws Exception {
        beginTransaction();
        getEntityManager().remove(getEntityManager().find(Employee.class, m_employee.getId()));
        commitTransaction();
    }
    
    protected void updateEmployee() throws Exception {
        beginTransaction();
        Employee emp = getEntityManager().find(Employee.class, m_employee.getId());
        emp.setFirstName("New name");
        commitTransaction();
    }
    
    protected void updateEmployeePhoneNumber() throws Exception {
        beginTransaction();
        try{
            Employee emp = getEntityManager().find(Employee.class, m_employee.getId());
            PhoneNumber phone = ModelExamples.phoneExample1();
            phone.setOwner(emp);
            emp.getPhoneNumbers().add(phone);
            commitTransaction();
        }catch (RuntimeException ex){
            rollbackTransaction();
            throw ex;
        }
    }
    
    protected Project persistNewProject() throws Exception {
        Project project = null;
        beginTransaction();
        try{
            project  = new Project();
            project.setName("Kirty's Project");
            project.setDescription("A government project");
            getEntityManager().persist(project);
            commitTransaction();
        }catch (RuntimeException ex){
            rollbackTransaction();
            throw ex;
        }
        return project;
    }
    
    protected Project removeProject() throws Exception {
        Project project = null;
        beginTransaction();
        try{
            project = getEntityManager().find(Project.class, m_project.getId());
            getEntityManager().remove(project);
            commitTransaction();
        }catch (RuntimeException ex){
            rollbackTransaction();
            throw ex;
        }
        return project;
    }
    
    protected Project updateProject() throws Exception {
        Project project = null;
        beginTransaction();
        try{
            project = getEntityManager().find(Project.class, m_project.getId());
            project.setDescription("Now a tough project");
            commitTransaction();
        }catch (RuntimeException ex){
            rollbackTransaction();
            throw ex;
        }
        return project;
    }
}
