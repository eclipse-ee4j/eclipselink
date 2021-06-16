/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
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
