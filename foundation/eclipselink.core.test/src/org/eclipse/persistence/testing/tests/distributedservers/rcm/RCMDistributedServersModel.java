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
package org.eclipse.persistence.testing.tests.distributedservers.rcm;

import java.util.Hashtable;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.coordination.RemoteCommandManager;
import org.eclipse.persistence.sessions.coordination.TransportManager;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.testing.tests.distributedservers.DistributedServer;
import org.eclipse.persistence.testing.tests.distributedservers.DistributedServersModel;
import org.eclipse.persistence.testing.tests.isolatedsession.IsolatedEmployee;
import org.eclipse.persistence.testing.tests.isolatedsession.IsolatedSessionSystem;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.domain.PhoneNumber;
import org.eclipse.persistence.testing.models.optimisticlocking.OptimisticLockingSystem;
import org.eclipse.persistence.tools.schemaframework.PopulationManager;


public class RCMDistributedServersModel extends DistributedServersModel {
    public RCMDistributedServersModel() {
        setDescription("Tests cache synchronization with RCM.");
    }

    public void addRequiredSystems() {
        super.addRequiredSystems();
        addRequiredSystem(new IsolatedSessionSystem());
        addRequiredSystem(new OptimisticLockingSystem());
    }

    /**
     * Factory method for a DistributedServer.  Overridden by subclasses;
     */
    public DistributedServer createDistributedServer(Session session) {
        return new RCMDistributedServer((DatabaseSession)session);
    }

    public void addTests() {
        super.addTests();
        PopulationManager manager = PopulationManager.getDefaultManager();
        Employee employee = (Employee)manager.getObject(Employee.class, "0001");

        Hashtable configurationHashtable = new Hashtable();
        configurationHashtable.put(Employee.class, new Integer(ClassDescriptor.INVALIDATE_CHANGED_OBJECTS));
        ConfigurableUpdateChangeObjectTest test = new ConfigurableUpdateChangeObjectTest(employee, configurationHashtable);
        test.setName("Update Change Employee - Invalidate Employee");
        test.setDescription("Test the invalidation setting on cache synchronization for Employee");
        addTest(test);

        configurationHashtable = new Hashtable();
        configurationHashtable.put(PhoneNumber.class, new Integer(ClassDescriptor.INVALIDATE_CHANGED_OBJECTS));
        configurationHashtable.put(Employee.class, new Integer(ClassDescriptor.INVALIDATE_CHANGED_OBJECTS));
        test = new ConfigurableUpdateChangeObjectTest(employee, configurationHashtable);
        test.setName("Update Change Employee - Invalidate Employee, Phone Number");
        test.setDescription("Test the invalidation setting on cache synchronization for Phone Number");
        addTest(test);

        addTest(new SendNewObjectCacheSyncTest(true));
        addTest(new SendNewObjectCacheSyncTest(false));
        addTest(new RelatedNewObjectCacheSyncTest());
        addTest(new MultipleCacheSyncTypeTest());
        addTest(new RelatedNewObjectNotSentTest());
        addTest(new DeleteObjectNotSentTest());
        addTest(new ChangeObjectNotSentTest());
        //Add the 2 tests below to verify bug 4194320
        //addTest(new InvalidCacheSyncTypeTest(-1));
        //addTest(new InvalidCacheSyncTypeTest(5));
        addTest(new IsolatedObjectNotSentTest());

        configurationHashtable = new Hashtable();
        configurationHashtable.put(IsolatedEmployee.class, new Integer(ClassDescriptor.SEND_OBJECT_CHANGES));
        IsolatedObjectNotSentTest atest = new IsolatedObjectNotSentTest(configurationHashtable);
        atest.setName("IsolatedObjectNotSentTest - SEND_OBJECT_CHANGES");
        addTest(atest);

        configurationHashtable = new Hashtable();
        configurationHashtable.put(IsolatedEmployee.class, new Integer(ClassDescriptor.INVALIDATE_CHANGED_OBJECTS));
        atest = new IsolatedObjectNotSentTest(configurationHashtable);
        atest.setName("IsolatedObjectNotSentTest - INVALIDATE_CHANGED_OBJECTS");
        addTest(atest);

        configurationHashtable = new Hashtable();
        configurationHashtable.put(IsolatedEmployee.class, new Integer(ClassDescriptor.SEND_NEW_OBJECTS_WITH_CHANGES));
        atest = new IsolatedObjectNotSentTest(configurationHashtable);
        atest.setName("IsolatedObjectNotSentTest - SEND_NEW_OBJECTS_WITH_CHANGES");
        addTest(atest);

        configurationHashtable = new Hashtable();
        configurationHashtable.put(IsolatedEmployee.class, new Integer(ClassDescriptor.DO_NOT_SEND_CHANGES));
        atest = new IsolatedObjectNotSentTest(configurationHashtable);
        atest.setName("IsolatedObjectNotSentTest - DO_NOT_SEND_CHANGES");
        addTest(atest);

        addTest(new InvalidateObjectWithMissingReferenceTest());
        addTest(new UpdateObjectInvalidationTest());
        addTest(new NativeUpdateObjectInvalidationTest());
        addTest(new NewObjectWithOptimisticLockingTest());
    }

    public void startCacheSynchronization() {
        RemoteCommandManager cm = new RemoteCommandManager((AbstractSession)getSession());

        // set propagate command synchronously for testing
        cm.setShouldPropagateAsynchronously(false);
        cm.getDiscoveryManager().setAnnouncementDelay(0);
        // ovewrite default to use RMI registry naming service  
        cm.getTransportManager().setNamingServiceType(TransportManager.REGISTRY_NAMING_SERVICE);
        // set full rmi URL of local host 
        cm.setUrl("rmi://localhost:1099");
        // turn on cache sync with RCM
        ((AbstractSession)getSession()).setShouldPropagateChanges(true);
        cm.setServerPlatform(((org.eclipse.persistence.sessions.DatabaseSession)getSession()).getServerPlatform());

        cm.initialize();
        // Sleep to allow RCM to startup and find each session.
        try {
            Thread.sleep(2000);
        } catch (Exception ignore) {
        }
    }

    public void stopCacheSynchronization() {
        ((AbstractSession)getSession()).getCommandManager().shutdown();
        ((AbstractSession)getSession()).setCommandManager(null);
    }
}
