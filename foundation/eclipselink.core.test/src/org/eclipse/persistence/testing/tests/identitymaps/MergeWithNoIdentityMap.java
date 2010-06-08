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
package org.eclipse.persistence.testing.tests.identitymaps;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

public class MergeWithNoIdentityMap extends RegisterInIdentityMapTest {
    public Employee clone;
    public org.eclipse.persistence.internal.descriptors.OptimisticLockingPolicy lockPolicy;

    public MergeWithNoIdentityMap(Class mapClass) {
        super(mapClass);
    }

    public String getDescription() {
        return "This test verifies that ValueHolders are not triggered when object is updated";
    }

    public void reset() {
        super.reset();
        getSession().getDescriptor(Employee.class).getQueryManager().checkCacheForDoesExist();
        getSession().getDescriptor(Employee.class).setOptimisticLockingPolicy(lockPolicy);
    }

    public void setup() {
        super.setup();
        // Avoid infinite loop at registration time of object
        getSession().getDescriptor(Employee.class).getQueryManager().checkDatabaseForDoesExist();

        lockPolicy = getSession().getDescriptor(Employee.class).getOptimisticLockingPolicy();
        getSession().getDescriptor(Employee.class).setOptimisticLockingPolicy(null);
    }

    public void test() {
        Employee employee = (Employee)getSession().readObject(Employee.class);
        UnitOfWork uow = getSession().acquireUnitOfWork();
        clone = (Employee)uow.registerObject(employee);
        clone.setFirstName("No identity at: " + new java.sql.Timestamp(System.currentTimeMillis()));
        uow.commit();
    }

    public void verify() {
        if (clone.address.isInstantiated() || clone.manager.isInstantiated() || clone.managedEmployees.isInstantiated() || clone.phoneNumbers.isInstantiated() || clone.projects.isInstantiated()) {
            throw new org.eclipse.persistence.testing.framework.TestErrorException("Unexpected triggerings of ValueHolders during an updat -  NoIdentityMap");
        }
    }
}
