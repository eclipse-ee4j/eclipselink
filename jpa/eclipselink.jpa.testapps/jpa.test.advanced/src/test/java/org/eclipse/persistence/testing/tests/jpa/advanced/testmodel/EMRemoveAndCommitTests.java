/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.jpa.advanced.testmodel;

import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TestException;
import org.eclipse.persistence.testing.framework.jpa.junit.EntityContainerTestBase;
import org.eclipse.persistence.testing.models.jpa.advanced.Address;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.advanced.ModelExamples;
import org.eclipse.persistence.testing.models.jpa.advanced.PhoneNumber;
import org.eclipse.persistence.testing.models.jpa.advanced.PhoneNumberPK;
import org.eclipse.persistence.testing.models.jpa.advanced.Project;

import java.util.ArrayList;
import java.util.List;

public class EMRemoveAndCommitTests extends EntityContainerTestBase  {
    public EMRemoveAndCommitTests() {
        setDescription("Test remove and commit in EntityManager");
    }

    //reset gets called twice on error
    protected boolean reset = false;

    public Integer[] empIDs = new Integer[3];
    public Integer[] projIDs = new Integer[2];
    public Integer[] addrIDs = new Integer[1];
    public ArrayList<PhoneNumberPK> phoneIDs = new ArrayList<>();

    @Override
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
            commitTransaction();
        } catch (Exception ex) {
            rollbackTransaction();
            throw new TestException("Unable to setup Test" + ex);
        }
        ((EntityManagerImpl)getEntityManager()).getActiveSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        empIDs[0] = empClone1.getId();
        empIDs[1] = empClone2.getId();
        empIDs[2] = empClone3.getId();

        projIDs[0] = projClone1.getId();
        projIDs[1] = projClone2.getId();

        addrIDs[0] = addrClone.getID();

        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

    }

    @Override
    public void reset (){
        if (reset){
            phoneIDs.clear();
            reset = false;
        }
        super.reset();
    }

    @Override
    public void test(){
        try {
            beginTransaction();
            Employee employee = getEntityManager().find(Employee.class, empIDs[1]);
            List<Project> projects = new ArrayList<>(employee.getProjects());
            for (Project project : projects) {
                if (project.getTeamLeader() == employee) {
                    project.setTeamLeader(null);
                }
                employee.getProjects().remove(project);
            }
            List<Employee> managedEmps = new ArrayList<>(employee.getManagedEmployees());
            for (Employee report : managedEmps) {
                if (report.getManager() == employee) {
                    report.setManager(null);
                }
                employee.getManagedEmployees().remove(report);
            }
            for (PhoneNumber phoneNumber : employee.getPhoneNumbers()) {
                this.phoneIDs.add(phoneNumber.buildPK());
            }
            getEntityManager().remove(employee);
            Address address = getEntityManager().find(Address.class, addrIDs[0]);
            getEntityManager().remove(address);
            commitTransaction();
        } catch (Exception ex) {
            rollbackTransaction();
            throw new TestErrorException("Exception thrown during assignment of employee to a project" + ex);
        }
    }

    @Override
    public void verify(){
        Employee employee = getEntityManager().find(Employee.class, empIDs[1]);
        if ( employee != null){
            throw new TestErrorException("Employee ID :" + empIDs[1] + " Was not Deleted");
        }
        for (PhoneNumberPK id : this.phoneIDs) {
            PhoneNumber phone = getEntityManager().find(PhoneNumber.class, id);
            if (phone != null) {
                throw new TestErrorException("Employee ID :" + empIDs[1] + " Related Phones were not deleted");
            }
        }
        //lets initialize the identity map to make sure they were persisted
        ((JpaEntityManager)getEntityManager()).getActiveSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        employee = getEntityManager().find(Employee.class, empIDs[1]);
        if ( employee != null){
            throw new TestErrorException("Employee ID :" + empIDs[1] + " Was not Deleted on Database");
        }
        for (PhoneNumberPK phoneID : this.phoneIDs) {
            PhoneNumber phone = getEntityManager().find(PhoneNumber.class, phoneID);
            if (phone != null) {
                throw new TestErrorException("Employee ID :" + empIDs[1] + " Related Phones were not deleted on Database");
            }
        }

        Address address = getEntityManager().find(Address.class, addrIDs[0]);
        if ( address != null){
            throw new TestErrorException("Address ID :" + addrIDs[0] + " Was not Deleted on Database");
        }
    }
}
