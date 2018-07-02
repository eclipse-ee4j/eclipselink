/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.jpa.advanced;

import java.util.HashMap;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.jpa.advanced.*;
import org.eclipse.persistence.testing.tests.jpa.EntityContainerTestBase;

public class EMRemoveAndFlushTest extends EntityContainerTestBase  {
    public EMRemoveAndFlushTest() {
        setDescription("Test remove and flush in EntityManager");
    }

    public Integer[] empIDs = new Integer[2];
    public Integer[] projIDs = new Integer[3];
    public HashMap persistedItems = new HashMap(4);

    public void setup (){
        super.setup();
        persistedItems.clear();
        Employee employee = ModelExamples.employeeExample1();
        Project project = ModelExamples.projectExample1();

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
            getEntityManager().remove(employee);

            Project project = getEntityManager().find(Project.class, projIDs[0]);
            getEntityManager().remove(project);

            getEntityManager().flush();
            //lets initialize the identity map to make sure they were persisted
            ((JpaEntityManager)getEntityManager()).getServerSession().getIdentityMapAccessor().initializeAllIdentityMaps();
            getEntityManager().clear(); //lear em as well or they will continue to be managed

            persistedItems.put("after flush Employee", getEntityManager().find(Employee.class, empIDs[0]));
            persistedItems.put("after flush Project", getEntityManager().find(Project.class, projIDs[0]));

            commitTransaction();
        } catch (Exception ex) {
            rollbackTransaction();
            ex.printStackTrace();
            throw new TestErrorException("Exception thrown durring persist and flush" + ex);
        }
    }

    public void verify(){
        if(persistedItems.get("after flush Employee") != null) {
            throw new TestErrorException("Employee ID :" + empIDs[0] + " was not deleted");
        }
        if(persistedItems.get("after flush Project") != null) {
            throw new TestErrorException("Project ID :" + projIDs[0] + " was not deleted");
        }
    }
}
