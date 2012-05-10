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
 *     08/15/2008-1.0.1 Chris Delahunt 
 *       - 237545: List attribute types on OneToMany using @OrderBy does not work with attribute change tracking
 ******************************************************************************/ 
package org.eclipse.persistence.testing.tests.distributedcache;

import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Child;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

public class OrderedListMergeTest3 extends OrderedListMergeTest {
    
    Child childtoremove;
    
    protected void modifyCollection(UnitOfWork uow, Object objectToModify) {
        Child newChildWC = (Child)uow.registerNewObject(newItemForCollection());
        ((Employee)objectToModify).children.set(0, newChildWC);
        newChildWC.parent = (Employee)objectToModify;
        
        childtoremove = (Child)((Employee)objectToModify).children.remove(1);
    }
    
    protected void test() {
        Session clientSession1 = cluster1Session.acquireClientSession();
        Session clientSession2 = cluster2Session.acquireClientSession();

        cluster2Session.getIdentityMapAccessor().initializeAllIdentityMaps();
        cluster2Session.getEventManager().addListener(buildCacheMergeBlockingListener());
        
        //load object in both sessions:
        Object object1 = findOriginalObject(clientSession1);

        if (object1 == null) {
            throw new TestErrorException("Employee does not exist on Server 1");
        }
        int initialNumProjs = getCollectionSize(object1);
        
        Object object2 = findOriginalObject(clientSession2);
        if (object2 == null) {
            throw new TestErrorException("Employee does not exist on Server 2");
        }
        getCollectionSize(object2);
        
        //run test:
        
        UnitOfWork uow = clientSession1.acquireUnitOfWork();
        Object newEmpWC = uow.registerObject(object1);
        modifyCollection(uow, newEmpWC);
        
        semaphore.acquire();

        uow.commit();
        
        try {
            //wait till we are sure the other session is ready to merge
            while (semaphore.getNumberOfWritersWaiting() == 0) {
                Thread.sleep(10);
            }
        } catch (Exception e) {
            throw new TestErrorException("Error while getting Thread to sleep", e);
        }
        
        semaphore.release();
        //wait for session2 to merge 
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            throw new TestErrorException("Error while getting Thread to sleep", e);
        }

        semaphore.acquire();
        
        if ( getCollectionSize(object1) != getCollectionSize(object2) ){
            throw new TestErrorException("Size of the collections do not match, expected "+ getCollectionSize(object1)+" got "+getCollectionSize(object2));
        }
        //check objects collection ordering against each other
        if (!compareObjectsCollections(object1, object2)) {
            throw new TestErrorException("order does not match what is expected");
        }
        semaphore.release();
    }

}
