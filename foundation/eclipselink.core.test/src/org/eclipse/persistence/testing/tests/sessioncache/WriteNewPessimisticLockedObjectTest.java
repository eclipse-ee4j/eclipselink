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
package org.eclipse.persistence.testing.tests.sessioncache;

import org.eclipse.persistence.internal.identitymaps.IdentityMap;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.descriptors.CMPPolicy;
import org.eclipse.persistence.descriptors.PessimisticLockingPolicy;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

public class WriteNewPessimisticLockedObjectTest extends TestCase {
    private CMPPolicy oldCMPPolicy;
    private int oldLevel;

    public WriteNewPessimisticLockedObjectTest() {
        setDescription("The test ensures that new pessimistically locked objects are not  put in the session cache");
    }

    protected void setup() {
        PessimisticLockingPolicy policy = new PessimisticLockingPolicy();
        policy.setLockingMode(ObjectLevelReadQuery.LOCK_NOWAIT);
        CMPPolicy cmpPolicy = new CMPPolicy();
        cmpPolicy.setPessimisticLockingPolicy(policy);

        ClassDescriptor empDescriptor = getSession().getDescriptor(Employee.class);

        oldCMPPolicy = empDescriptor.getCMPPolicy();
        empDescriptor.setCMPPolicy(cmpPolicy);
        empDescriptor.getQueryManager().getReadObjectQuery().setLockMode(ObjectLevelReadQuery.DEFAULT_LOCK_MODE);

        this.oldLevel = empDescriptor.getUnitOfWorkCacheIsolationLevel();
        empDescriptor.setUnitOfWorkCacheIsolationLevel(ClassDescriptor.ISOLATE_CACHE_ALWAYS);

        // Flush the cache 				
        getSession().getIdentityMapAccessor().initializeIdentityMaps();

        getAbstractSession().beginTransaction();
    }

    protected void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();

        Employee empInsert = new Employee();
        empInsert.setFirstName("TestPerson");
        empInsert.setFemale();
        empInsert.setLastName("Smith");
        empInsert.setSalary(55555);
        uow.registerObject(empInsert);

        uow.commit();
    }

    protected void verify() {
        //ensure changes were merged into the session cache      				
        IdentityMap im = ((AbstractSession)getSession()).getIdentityMapAccessorInstance().getIdentityMap(Employee.class);
        if (im.getSize() > 0) {
            throw new TestErrorException("Employee should not have been put into session cache.");
        }
    }

    public void reset() throws Exception {
        getAbstractSession().rollbackTransaction();
        getSession().getDescriptor(Employee.class).setCMPPolicy(oldCMPPolicy);
        getSession().getDescriptor(Employee.class).setUnitOfWorkCacheIsolationLevel(this.oldLevel);
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }
}
