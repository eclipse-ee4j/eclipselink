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
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.jpa.advanced.*;
import org.eclipse.persistence.testing.tests.jpa.EntityContainerTestBase;
import org.eclipse.persistence.testing.tests.jpa.CMP3TestModel;
import org.eclipse.persistence.testing.framework.TestWarningException;

public class EMCascadingPersistAndFlushTest extends EntityContainerTestBase  {
    public EMCascadingPersistAndFlushTest() {
		setDescription("Test cascading persist and flush in EntityManager");
    }

    public Integer[] empIDs = new Integer[2];
    public Integer[] projIDs = new Integer[3];
    public ArrayList phones = new ArrayList(2);
    public HashMap persistedItems = new HashMap(4);
    Employee employee = null;
    Project project = null; 
    PhoneNumber phone = null;
    
    public void setup() {
        super.setup();
        if (CMP3TestModel.getServerSession().getDescriptor(Employee.class).getSequence().shouldAcquireValueAfterInsert() || 
                CMP3TestModel.getServerSession().getDescriptor(Project.class).getSequence().shouldAcquireValueAfterInsert()) {
            throw new TestWarningException("Can't run this test with Sybase-type native sequencing for Employee or/and Project");
        }
    }

    public void reset() {
        if(phones != null) {
            phones.clear();
        }
        super.reset();
    }
    
    public void test(){
    
        employee = new Employee();
        employee.setFirstName("First");
        employee.setLastName("Bean");
		
        project = new Project();
        project.setName("Project # 1");
        project.setDescription("A simple Project");

        phone = new PhoneNumber("Work", "613", "9876543");
        
        employee.addProject(project);
        employee.addPhoneNumber(phone);

        try {
            //Check the cache first then database
            beginTransaction();
        
            getEntityManager().persist(employee);
            empIDs[0] = employee.getId();
            //lets check the cache for the objects
            persistedItems.put("after persist Employee", getEntityManager().find(Employee.class, empIDs[0]));

            Project project1 = employee.getProjects().iterator().next();
            persistedItems.put("after persist Project", project1);
            projIDs[0] = project.getId();        

            PhoneNumber phone1 = employee.getPhoneNumbers().iterator().next();
            persistedItems.put("after persist PhoneNumber", phone1);
            phones.add(phone.buildPK());
            
            getEntityManager().flush();
            //lets initialize the identity map to make sure they were persisted
            ((JpaEntityManager)getEntityManager()).getActiveSession().getIdentityMapAccessor().initializeAllIdentityMaps();

            persistedItems.put("after flush Employee", getEntityManager().find(Employee.class, empIDs[0]));
            persistedItems.put("after flush Project", getEntityManager().find(Project.class, projIDs[0]));
            persistedItems.put("after flush PhoneNumber", getEntityManager().find(PhoneNumber.class, phones.get(0)));
            
            commitTransaction();
        } catch (Exception ex) {
            rollbackTransaction();
            throw new TestErrorException("Exception thrown durring persist and flush" + ex);
        }
    }
    
    public void verify(){
        if(persistedItems.get("after persist Employee") == null) {
            throw new TestErrorException("Find after persist Employee: " + empIDs[0] + " is not found in the cache");
        }
        if(persistedItems.get("after persist Project") == null) {
            throw new TestErrorException("Find after persist Project: requried Project is not found in the cache");
        }
        if(!((Project)persistedItems.get("after persist Project")).getId().equals(projIDs[0])) {
            throw new TestErrorException("Find after persist Project: " + projIDs[0] + " is not found in the cache");
        }
        if(persistedItems.get("after persist PhoneNumber") == null) {
            throw new TestErrorException("Find after persist PhoneNumber: required PhoneNumber is not found in the cache");
        }
        if(!((PhoneNumber)persistedItems.get("after persist PhoneNumber")).buildPK().equals(phones.get(0))) {
            throw new TestErrorException("Find after persist PhoneNumber: " + phones.get(0) + " is not found in the cache");
        }
        if(persistedItems.get("after flush Employee") == null) {
            throw new TestErrorException("Find after flush Employee: " + empIDs[0] + " is not persisted");
        }
        if(persistedItems.get("after flush Project") == null) {
            throw new TestErrorException("Find after flush Project: requried Project is not persisted");
        }
        if(!((Project)persistedItems.get("after flush Project")).getId().equals(projIDs[0])) {
            throw new TestErrorException("Find after flush Project: " + projIDs[0] + " is not persisted");
        }
        if(persistedItems.get("after flush PhoneNumber") == null) {
            throw new TestErrorException("Find after flush PhoneNumber: required PhoneNumber is not persisted");
        }
        if(!((PhoneNumber)persistedItems.get("after flush PhoneNumber")).buildPK().equals(phones.get(0))) {
            throw new TestErrorException("Find after flush PhoneNumber: " + phones.get(0) + " is not persisted");
        }
    }
}
