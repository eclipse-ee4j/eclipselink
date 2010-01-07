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

public class EMCascadingRemoveAndFlushTest extends EntityContainerTestBase  {
    public EMCascadingRemoveAndFlushTest() {
		setDescription("Test cascading remove and flush in EntityManager");
    }

    public Integer[] empIDs = new Integer[2];
    public HashMap persistedItems = new HashMap(4);
    public ArrayList<PhoneNumberPK> phoneIDs = new ArrayList<PhoneNumberPK>();
    
    public void setup (){
        super.setup();
        phoneIDs.clear();
        persistedItems.clear();

        Employee employee = ModelExamples.employeeExample1();		
        employee.addPhoneNumber(ModelExamples.phoneExample4());
        employee.addPhoneNumber(ModelExamples.phoneExample8());
        
        try {
            beginTransaction();
            getEntityManager().persist(employee);
            commitTransaction();
        } catch (Exception ex) {
            rollbackTransaction();
            ex.printStackTrace();
            throw new TestErrorException("Exception thrown durring persist and flush" + ex);
        }
        
        empIDs[0] = employee.getId();
    }
    
    public void test(){
    
        try {
            beginTransaction();
            Employee employee = getEntityManager().find(Employee.class, empIDs[0]);
            for (Iterator phones = employee.getPhoneNumbers().iterator(); phones.hasNext();){
                this.phoneIDs.add(((PhoneNumber)phones.next()).buildPK());
            }
            getEntityManager().remove(employee);

            getEntityManager().flush();
            //lets initialize the identity map to make sure they were persisted
            ((JpaEntityManager)getEntityManager()).getServerSession().getIdentityMapAccessor().initializeAllIdentityMaps();
            getEntityManager().clear();

            persistedItems.put("after flush Employee", getEntityManager().find(Employee.class, empIDs[0]));
            for (Iterator<PhoneNumberPK> ids = this.phoneIDs.iterator(); ids.hasNext();){
                PhoneNumber phone = getEntityManager().find(PhoneNumber.class, ids.next());
                if (phone != null){
                    persistedItems.put("after flush PhoneNumber", phone);
                }
            }
            
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
        if(persistedItems.get("after flush PhoneNumber") != null) {
            throw new TestErrorException("Employee ID :" + empIDs[0] + " a phone number was not deleted");
        }
    }
}
