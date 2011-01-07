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
package org.eclipse.persistence.testing.tests.distributedservers;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TransactionalTestCase;


public class InsertNewObjectIntoCycleTest extends TransactionalTestCase {
    public Dist_Employee originalEmployee;
    public Company originalCompany;
    //Must use a different thread in this test as the test is tezting for an infinite recursion
    public Thread thread;

    public void test() {
        DistributedServer server = (DistributedServer)DistributedServersModel.getDistributedServers().get(0);
        originalEmployee = (Dist_Employee)server.getDistributedSession().readObject(Dist_Employee.class);
        originalCompany = (Company)server.getDistributedSession().readObject(Company.class);

        UnitOfWork uow = getSession().acquireUnitOfWork();
        originalEmployee = (Dist_Employee)uow.readObject(originalEmployee);
        originalCompany = (Company)uow.readObject(originalCompany);

        Item item = Item.example1();
        originalEmployee.heldItems.add(item);
        item.employeeHolder = originalEmployee;

        originalCompany.ownedItems.add(item);
        item.companyOwner = originalCompany;
        uow.commit();
    }

    public void verify() {
        DistributedServer server = (DistributedServer)DistributedServersModel.getDistributedServers().get(0);
        Dist_Employee emp = (Dist_Employee)server.getDistributedSession().readObject(originalEmployee);
        Company company = (Company)server.getDistributedSession().readObject(originalCompany);
        if (company.ownedItems.contains(null) || emp.heldItems.contains(null)) {
            throw new TestErrorException("Object was not merged correctly on remote cache.  Null was inserted into Collection CR 4012");
        }
    }

    public void reset() {
        super.reset();
        DistributedServer server = (DistributedServer)DistributedServersModel.getDistributedServers().get(0);
        // must make the remote session load the object so that it will be merged
        server.getDistributedSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }


}
