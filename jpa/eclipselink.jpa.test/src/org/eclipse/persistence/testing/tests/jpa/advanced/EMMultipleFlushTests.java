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

import java.util.*;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.jpa.advanced.*;
import org.eclipse.persistence.testing.tests.jpa.EntityContainerTestBase;

public class EMMultipleFlushTests extends EntityContainerTestBase  {
    public EMMultipleFlushTests() {
		setDescription("Test flush multiple times in EntityManager");
    }

    //reset gets called twice on error
    protected boolean reset = false;
    
    public Integer[] empIDs = new Integer[4];
    public Integer[] projIDs = new Integer[2];
    public Integer[] addrIDs = new Integer[2];
    public ArrayList phones = new ArrayList(2);
    
    public HashMap removedPhone = new HashMap(4);
    public HashMap removedAddress = new HashMap(4);
    
    public void setup (){
        super.setup();
        this.reset = true;

        Employee empClone1 = ModelExamples.employeeExample1();
        empClone1.setAddress(ModelExamples.addressExample1());
        empClone1.addPhoneNumber(ModelExamples.phoneExample1());
        empClone1.addPhoneNumber(ModelExamples.phoneExample9());
        
        Employee empClone2 = ModelExamples.employeeExample2();
        empClone2.setAddress(ModelExamples.addressExample2());
        empClone2.addPhoneNumber(ModelExamples.phoneExample2());
        empClone2.addPhoneNumber(ModelExamples.phoneExample8());

        Employee empClone3 = ModelExamples.employeeExample3();
        empClone3.setAddress(ModelExamples.addressExample3());
        empClone3.addPhoneNumber(ModelExamples.phoneExample3());
        empClone3.addPhoneNumber(ModelExamples.phoneExample7());
        
        Employee empClone4 = ModelExamples.employeeExample4();
        Address addrClone5 = ModelExamples.addressExample5();
        empClone4.setAddress(addrClone5);
        addrClone5.getEmployees().add(empClone4);

        empClone1.addManagedEmployee(empClone2);
        empClone1.addManagedEmployee(empClone3);
        Project projClone1 = ModelExamples.projectExample1(); 
        Project projClone2 = ModelExamples.projectExample2();


        projClone1.setTeamLeader(empClone1);
        projClone1.addTeamMember(empClone1);
        projClone1.addTeamMember(empClone2);
        projClone1.addTeamMember(empClone3);
        empClone1.addProject(projClone1);
        empClone2.addProject(projClone1);
        empClone3.addProject(projClone1);

        Address addrClone = ModelExamples.addressExample4();
        
        try {
            beginTransaction();
            getEntityManager().persist(empClone1);
            getEntityManager().persist(empClone2);
            getEntityManager().persist(empClone3);
            getEntityManager().persist(projClone1);
            getEntityManager().persist(projClone2);
            getEntityManager().persist(addrClone);
            getEntityManager().persist(empClone4);
            commitTransaction();
        } catch (Exception ex) {
            rollbackTransaction();
            throw new TestException("Unable to setup Test" + ex);
        }
        
        empIDs[0] = empClone1.getId();
        empIDs[1] = empClone2.getId();
        empIDs[2] = empClone3.getId();
        empIDs[3] = empClone4.getId();
        
        projIDs[0] = projClone1.getId();
        projIDs[1] = projClone2.getId();
       
        addrIDs[0] = addrClone.getId();
        addrIDs[1] = addrClone5.getId();
       
        
    }
    
    public void reset (){
        if (reset){
            phones.clear();
            reset = false;
        }
        super.reset();
    }
    
    public void test(){
        try {
            beginTransaction();
            Employee employee = getEntityManager().find(Employee.class, empIDs[1]);
            Employee employee4 = getEntityManager().find(Employee.class, empIDs[3]);
            Project project = getEntityManager().find(Project.class, projIDs[1]);
            Address address = getEntityManager().find(Address.class, addrIDs[0]);
            Address address5 = getEntityManager().find(Address.class, addrIDs[1]);
            
            address5.getEmployees().size();
            project.addTeamMember(employee);
            employee.addProject(project);
            PhoneNumber phone = employee.getPhoneNumbers().iterator().next();
            employee.getPhoneNumbers().remove(phone);
            phones.add(phone.buildPK());
            removedPhone.put("before remove", getEntityManager().find(PhoneNumber.class, phones.get(0)));
            removedAddress.put("before remove", getEntityManager().find(Address.class, addrIDs[0]));
            getEntityManager().remove(phone);
            getEntityManager().remove(address);
            getEntityManager().remove(employee4);
            removedPhone.put("after remove", getEntityManager().find(PhoneNumber.class, phones.get(0)));
            removedAddress.put("after remove", getEntityManager().find(Address.class, addrIDs[0]));
            getEntityManager().flush();
            removedPhone.put("after first flush", getEntityManager().find(PhoneNumber.class, phones.get(0)));
            removedAddress.put("after first flush", getEntityManager().find(Address.class, addrIDs[0]));
            phone = employee.getPhoneNumbers().iterator().next();
            employee.getPhoneNumbers().remove(phone);
            phones.add(phone.buildPK());
            getEntityManager().remove(phone);
            employee.setLastName("Fourlang");
            
            getEntityManager().flush();
            getEntityManager().remove(address5);
            removedPhone.put("after second flush", getEntityManager().find(PhoneNumber.class, phones.get(0)));
            removedAddress.put("after second flush", getEntityManager().find(Address.class, addrIDs[0]));
            employee.setSalary(20000);            
            commitTransaction();
        } catch (Exception ex) {
            this.rollbackTransaction();
            throw new TestErrorException("Exception thrown durring flushing of Employee", ex);
        }
    }

    public void verify(){
        Employee employee = getEntityManager().find(Employee.class, empIDs[1]);
        if ( (!employee.getLastName().equals("Fourlang"))){
            throw new TestErrorException("Employee ID :" + empIDs[1] + " Last Name not Updated");
        }
        if (employee.getSalary() != 20000){
            throw new TestErrorException("Employee ID :" + empIDs[1] + " Salary Not Updated");
        }

        if ((employee.getProjects().iterator().next()).getId().equals(projIDs[1])){
            throw new TestErrorException("Employee ID :" + empIDs[1] + " Project not added");
        }
        
        if (employee.getPhoneNumbers().size() != 0){
            throw new TestErrorException("Employee ID :" + empIDs[1] + " Phones Not Deleted");
        }
        
        PhoneNumber phone = getEntityManager().find(PhoneNumber.class, phones.get(0));
        if (phone != null){
            throw new TestErrorException("Phone ID :" + phones.get(0) + " not deleted");
        }
        phone = getEntityManager().find(PhoneNumber.class, phones.get(1));
        if (phone != null){
            throw new TestErrorException("Phone ID :" + phones.get(1) + " not deleted");
        }
            
        //lets initialize the identity map to make sure they were persisted
        ((JpaEntityManager)getEntityManager()).getActiveSession().getIdentityMapAccessor().initializeAllIdentityMaps();
            
        employee = getEntityManager().find(Employee.class, empIDs[1]);
        if ( (!employee.getLastName().equals("Fourlang"))){
            throw new TestErrorException("Employee ID :" + empIDs[1] + " Last Name not Updated on Database");
        }
        if (employee.getSalary() != 20000){
            throw new TestErrorException("Employee ID :" + empIDs[1] + " Salary Not Updated on Database");
        }

        if ( employee.getProjects().size() != 2 ){
            throw new TestErrorException("Employee ID :" + empIDs[1] + " Project not added on Database");
        }
        
        if (employee.getPhoneNumbers().size() != 0){
            throw new TestErrorException("Employee ID :" + empIDs[1] + " Phones Not Deleted on Database");
        }
        
        phone = getEntityManager().find(PhoneNumber.class, phones.get(0));
        if (phone != null){
            throw new TestErrorException("Phone ID :" + phones.get(0) + " not deleted on Database");
        }
        phone = getEntityManager().find(PhoneNumber.class, phones.get(1));
        if (phone != null){
            throw new TestErrorException("Phone ID :" + phones.get(1) + " not deleted on Database");
        }
        
        if(removedPhone.get("before remove") == null) {
            throw new TestErrorException("Find before remove: Phone ID :" + phones.get(0) + " is not found");
        }
        if(removedPhone.get("after remove") != null) {
            throw new TestErrorException("Find after remove: Phone ID :" + phones.get(0) + " is found");
        }
        if(removedPhone.get("after first flush") != null) {
            throw new TestErrorException("Find after first flush: Phone ID :" + phones.get(0) + " is found");
        }
        if(removedPhone.get("after second flush") != null) {
            throw new TestErrorException("Find after second flush: Phone ID :" + phones.get(0) + " is found");
        }
        
        if(removedAddress.get("before remove") == null) {
            throw new TestErrorException("Find before remove: Address ID :" + addrIDs[0] + " is not found");
        }
        if(removedAddress.get("after remove") != null) {
            throw new TestErrorException("Find after remove: Address ID :" + addrIDs[0] + " is found");
        }
        if(removedAddress.get("after first flush") != null) {
            throw new TestErrorException("Find after first flush: Address ID :" + addrIDs[0] + " is found");
        }
        if(removedAddress.get("after second flush") != null) {
            throw new TestErrorException("Find after second flush: Address ID :" + addrIDs[0] + " is found");
        }
    }
}
