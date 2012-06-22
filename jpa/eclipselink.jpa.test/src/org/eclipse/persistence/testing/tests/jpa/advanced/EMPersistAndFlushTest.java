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

import java.util.*;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TestWarningException;
import org.eclipse.persistence.testing.models.jpa.advanced.*;
import org.eclipse.persistence.testing.tests.jpa.EntityContainerTestBase;
import org.eclipse.persistence.testing.tests.jpa.CMP3TestModel;

public class EMPersistAndFlushTest extends EntityContainerTestBase  {
    public EMPersistAndFlushTest() {
		setDescription("Test persist and flush in EntityManager");
    }

    public Integer[] empIDs = new Integer[2];
    public Integer[] projIDs = new Integer[3];
    public ArrayList phones = new ArrayList(2);
    public HashMap persistedItems = new HashMap(4);
    
    public void setup() {
        super.setup();
        if(CMP3TestModel.getServerSession().getDescriptor(Employee.class).getSequence().shouldAcquireValueAfterInsert() || 
            CMP3TestModel.getServerSession().getDescriptor(Project.class).getSequence().shouldAcquireValueAfterInsert()) {
            throw new TestWarningException("Can't run this test with Sybase-type native sequencing for Employee or/and Project");
        }
    }
    
    public void test() throws Exception {
    
        Employee employee  = ModelExamples.employeeExample1();
//        employee.setFirstName("First");
//        employee.setLastName("Bean");
		
        Project project= ModelExamples.projectExample1();
//        project.setName("Project # 1");
//        project.setDescription("A simple Project");
        
        try {
            //Check the cache first then database
            beginTransaction();
            getEntityManager().persist(employee);
            empIDs[0] = employee.getId();
            //lets check the cache for the objects
            persistedItems.put("after persist Employee", getEntityManager().find(Employee.class, empIDs[0]));

            getEntityManager().persist(project);            
            projIDs[0] = project.getId();
            persistedItems.put("after persist Project", getEntityManager().find(Project.class, projIDs[0]));
            
            getEntityManager().flush();
            //lets initialize the identity map to make sure they were persisted
            ((JpaEntityManager)getEntityManager()).getServerSession().getIdentityMapAccessor().initializeAllIdentityMaps();
            getEntityManager().clear();

            persistedItems.put("after flush Employee", getEntityManager().find(Employee.class, empIDs[0]));
            persistedItems.put("after flush Project", getEntityManager().find(Project.class, projIDs[0]));
            
            commitTransaction();
        } catch (Exception exception) {
            rollbackTransaction();
            throw exception;
        }
    }
    
    public void verify(){
        if(persistedItems.get("after persist Employee") == null) {
            throw new TestErrorException("Find after persist Employee: " + empIDs[0] + " is not found in the cache");
        }
        if(persistedItems.get("after persist Project") == null) {
            throw new TestErrorException("Find after persist Project: " + projIDs[0] + " is not found in the cache");
        }
        if(persistedItems.get("after flush Employee") == null) {
            throw new TestErrorException("Find after flush Employee: " + empIDs[0] + " is not persisted");
        }
        if(persistedItems.get("after flush Project") == null) {
            throw new TestErrorException("Find after flush Project: " + projIDs[0] + " is not persisted");
        }
    }
}
