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
package org.eclipse.persistence.testing.tests.distributedservers;

import java.util.Enumeration;
import java.util.Vector;

import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.testing.framework.TestModel;
import org.eclipse.persistence.testing.models.aggregate.Agent;
import org.eclipse.persistence.testing.models.aggregate.Builder;
import org.eclipse.persistence.testing.models.aggregate.Client;
import org.eclipse.persistence.testing.models.aggregate.Employee1;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.relational.EmployeeSystem;
import org.eclipse.persistence.tools.schemaframework.PopulationManager;


public abstract class DistributedServersModel extends TestModel {

    /** This is the collection of threads running distributed servers */
    public static Vector distributedServers;

    /** This is the global variable that monitors if the RMI registry has been started */
    protected static boolean registryStarted;

    public DistributedServersModel() {
        setDescription("This suite tests updating objects with changed parts.");
    }

    public void addRequiredSystems() {
        addRequiredSystem(new EmployeeSystem());
        addRequiredSystem(new DistributedSystem());
        addRequiredSystem(new org.eclipse.persistence.testing.tests.unitofwork.UOWSystem());
        //cr 4080
        addRequiredSystem(new org.eclipse.persistence.testing.models.aggregate.AggregateSystem());
    }

    public void addTests() {
        PopulationManager manager = PopulationManager.getDefaultManager();
        Employee employee = (Employee)manager.getObject(Employee.class, "0001");

        // Tests with using unit of work.
        ComplexUpdateTest test = new UpdateToNullTest(employee);
        addTest(test);

        test = new UpdateChangeValueTest(employee);
        addTest(test);

        test = new UpdateChangeNothingTest(employee);
        addTest(test);

        test = new UpdateChangeObjectTest(employee);
        addTest(test);
        
        //bug 205939
        addTest(new UpdateChangeObjectTestEmployee1((Employee1) manager.getObject(Employee1.class, "example1")));

        //cr 4143
        addTest(new UpdateCollectionWithNewValueTest(employee));

        addTest(new VerifyDeletedObjectsTest());
        addTest(new VerifyObjectsDeletedFromCacheTest());
        addTest(new InsertNewCycleTest());
        addTest(new InsertNewObjectIntoCycleTest());

        //CR 4080
        addTest(new VerifyAggregateCollectionObjectsDeletedFromCacheTest((Agent)manager.getObject(Agent.class, "example1")));
        addTest(new VerifyAggregateCollectionObjectsDeletedFromCacheTest((Builder)manager.getObject(Builder.class, "example1")));
        //cr 4143
        addTest(new VerifyAggregateCollectionNewObjectTest((Agent)manager.getObject(Agent.class, "example1")));
        addTest(new VerifyAggregateCollectionNewObjectTest((Builder)manager.getObject(Builder.class, "example1")));
        //bug 3443422
        addTest(new UpdateChangeObjectWithOptimisticLockTest(employee));
        //bug 3485635
        addTest(new StoredInObjectOptimisticLockTest((Client)manager.getObject(Client.class, "example1")));
    }

    /**
     * Factory method for a DistributedServer.  Overridden by sbuclasses;
     */
    public abstract DistributedServer createDistributedServer(Session session);

    /**
     * This method lazy initialises the server List
     * Creation date: (7/21/00 9:49:52 AM)
     * @return java.util.Vector the collection of servers
     */
    public static java.util.Vector getDistributedServers() {
        if (distributedServers == null) {
            distributedServers = new Vector(5);
        }
        return distributedServers;
    }

    /**
     * Returns true if the registry has been started
     * @return boolean
     */
    public static boolean isRegistryStarted() {
        return registryStarted;
    }

    /**
     * This method returns the system to the previous state
     * Creation date: (7/21/00 10:44:03 AM)
     */
    public void reset() {
        if (isRegistryStarted()) {
            try {
                if (requiresRegistry()) {
                    java.rmi.registry.LocateRegistry.getRegistry(1099);
                }
                Enumeration enumtr = getDistributedServers().elements();
                while (enumtr.hasMoreElements()) {
                    DistributedServer server = (DistributedServer)enumtr.nextElement();
                    server.stopServer();
                }
                setRegistryStarted(false);
                getDistributedServers().removeAllElements();
                stopCacheSynchronization();
            } catch (Exception exception) {
                System.out.println(exception.toString());
            }
        }
    }

    /**
     * Set true if the registry has been started
     * @param newRegistryStarted boolean
     */
    public static void setRegistryStarted(boolean newRegistryStarted) {
        registryStarted = newRegistryStarted;
    }

    public boolean requiresRegistry() {
        return true;
    }
    
    /**
     * This method sets up the distributed servers and the registry
     * Creation date: (7/21/00 10:44:03 AM)
     */
    public void setup() {
        if (!isRegistryStarted()) {
            try {
                if (requiresRegistry()) {
                    java.rmi.registry.LocateRegistry.createRegistry(1099);
                }
                setRegistryStarted(true);
            } catch (Exception exception) {
                System.out.println(exception.toString());
                try {
                    java.rmi.registry.LocateRegistry.getRegistry(1099);
                    setRegistryStarted(true);
                } catch (Exception secondTryException) {
                    System.out.println(secondTryException.toString());
                }
            }
        } else {
            try {
                if (requiresRegistry()) {
                    java.rmi.registry.LocateRegistry.getRegistry(1099);
                }
                setRegistryStarted(true);
                Enumeration servers = getDistributedServers().elements();
                while (servers.hasMoreElements()) {
                    ((DistributedServer)servers.nextElement()).stopServer();
                }
                getDistributedServers().removeAllElements();
                stopCacheSynchronization();
            } catch (Exception exception) {
                System.out.println(exception.toString());
            }
        }
        DistributedServer server = createDistributedServer(getSession());
        getDistributedServers().removeAllElements();
        getDistributedServers().addElement(server);
        server.run();
        // Must ensure the server starts up before starting to run tests.
        try {
            // The run actually spawns another thread to run the service on, so must laso wait for that one.
            Thread.sleep(1000);
        } catch (Exception ignore) {}
        startCacheSynchronization();
        // This also spawn a thread in initialize so also wait for it to finish.
        try {
            Thread.sleep(2000);
        } catch (Exception ignore) {}
    }

    public abstract void startCacheSynchronization();

    public abstract void stopCacheSynchronization();
}
