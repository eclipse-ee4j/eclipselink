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
package org.eclipse.persistence.testing.tests.remote;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.tools.schemaframework.*;

public class MasterSlaveSystem extends TestSystem {

    public MasterSlaveSystem() {
        this.project = new MasterSlaveProject();
    }

    public void addDescriptors(DatabaseSession session) {
        session.addDescriptors(project);
    }

    public void createTables(DatabaseSession session) {
        org.eclipse.persistence.tools.schemaframework.TableCreator tableCreator = new MasterSlaveTableCreator();
        tableCreator.replaceTables(session);
    }

    public void populate(DatabaseSession session) {
        PopulationManager manager = PopulationManager.getDefaultManager();
        // While working on bug 3145211, had to change populate to actually add the slaves
        UnitOfWork uow = session.acquireUnitOfWork();

        Master master = masterExample1();
        manager.registerObject(master, "master1");

        master = (Master)uow.registerObject(master);
        master.addSlave(slaveExample1());
        master.addSlave(slaveExample2());
        master.addSlave(slaveExample3());
        master.addSlave(slaveExample4());

        master = masterExample2();
        manager.registerObject(master, "master2");

        master = (Master)uow.registerObject(master);
        master.addSlave(slaveExample5());
        master.addSlave(slaveExample6());

        uow.commit();

    }

    public Master masterExample1() {
        Master master = new Master();
        master.setId(1);
        return master;
    }

    public Master masterExample2() {
        Master master = new Master();
        master.setId(2);
        return master;
    }

    public Slave slaveExample1() {
        Slave slave = new Slave();
        slave.setId(1);
        return slave;
    }

    public Slave slaveExample2() {
        Slave slave = new Slave();
        slave.setId(2);
        return slave;
    }

    public Slave slaveExample3() {
        Slave slave = new Slave();
        slave.setId(3);
        return slave;
    }

    public Slave slaveExample4() {
        Slave slave = new Slave();
        slave.setId(4);
        return slave;
    }

    public Slave slaveExample5() {
        Slave slave = new Slave();
        slave.setId(5);
        return slave;
    }


    public Slave slaveExample6() {
        Slave slave = new Slave();
        slave.setId(6);
        return slave;
    }

}

