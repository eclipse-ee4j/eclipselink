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
package org.eclipse.persistence.testing.tests.distributedcache;

import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.domain.SmallProject;
import org.eclipse.persistence.sessions.UnitOfWork;

public class ManyToManyMergeTest extends DistributedCacheMergeTest {
    public ManyToManyMergeTest() {
        super();
    }

    protected void modifyCollection(UnitOfWork uow, Object objectToModify) {
        SmallProject newSmallProjectWC = (SmallProject)uow.registerNewObject(newItemForCollection());
        ((Employee)objectToModify).addProject(newSmallProjectWC);
        //newSmallProjectWC.setTeamLeader((Employee)objectToModify);
    }

    protected int getCollectionSize(Object rootObject) {
        return ((Employee)rootObject).getProjects().size();
    }

   /* protected Object buildOriginalObject() {
        SmallProject project = new SmallProject();
        project.setName("DUP TEST");
        project.setDescription("AT: " + System.currentTimeMillis());
        return project;
    }*/
		
		 protected Object buildOriginalObject() {
        Employee emp = new Employee();
        emp.setFirstName("Sally");
        emp.setLastName("Hamilton");
        emp.setFemale();
        return emp;
    }

    protected Object newItemForCollection() {
       SmallProject project = new SmallProject();
        project.setName("DUP TEST");
        project.setDescription("AT: " + System.currentTimeMillis());
        return project;        
    }
}
