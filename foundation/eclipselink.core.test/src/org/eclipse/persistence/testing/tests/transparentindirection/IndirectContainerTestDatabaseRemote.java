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
package org.eclipse.persistence.testing.tests.transparentindirection;

import java.rmi.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.sessions.remote.rmi.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.transparentindirection.AbstractOrder;
import org.eclipse.persistence.testing.tests.remote.*;

public class IndirectContainerTestDatabaseRemote extends IndirectContainerTestDatabase {
    protected UnitOfWork unitOfWork;

    public IndirectContainerTestDatabaseRemote(String name) {
        super(name);
    }

    protected RMIConnection buildConnection() {
        RMIServerManager serverManager = null;

        // Set the client security manager
        //	System.setSecurityManager(new RMISecurityManager());
        // Get the remote factory object from the Registry
        try {
            serverManager = (RMIServerManager)Naming.lookup("SERVER-MANAGER");
        } catch (Exception exception) {
            throw new TestProblemException(exception.toString());
        }

        RMIConnection rmiConnection = null;
        try {
            rmiConnection = new RMIConnection(serverManager.createRemoteSessionController());
        } catch (RemoteException exception) {
            System.out.println("Error in invocation " + exception.toString());
        }

        return rmiConnection;
    }

    public void clearUnitOfWork() {
        unitOfWork = null;
    }

    public void commitUnitOfWork() {
        this.getUnitOfWork().commit();
        this.clearUnitOfWork();
    }

    /**
     * write out the new order
     */
    protected UnitOfWork getUnitOfWork() {
        if (unitOfWork == null) {
            unitOfWork = this.getSession().acquireUnitOfWork();
        }
        return unitOfWork;
    }

    protected AbstractOrder readOrder(AbstractOrder key) {
        return (AbstractOrder)this.getUnitOfWork().readObject(key);
    }

    /**
     * register the order using the typical cloning method
     */
    protected void registerNewOrderIn(AbstractOrder order, UnitOfWork uow) {
        uow.registerObject(order);
    }

    /**
     * set up test fixtures:
     *   swap out database session
     */
    protected void setUp() {
        if (!getSession().isRemoteSession()) {
            throw new TestProblemException("session should be a remote session");
        }
        super.setUp();
    }

    public void updateOrder(AbstractOrder order) {
        this.commitUnitOfWork();
    }

    /**
     * write out the new order
     */
    protected void writeNewOrder(AbstractOrder order) {
        UnitOfWork uow = this.getSession().acquireUnitOfWork();
        this.registerNewOrderIn(order, uow);
        uow.commit();
    }
}
