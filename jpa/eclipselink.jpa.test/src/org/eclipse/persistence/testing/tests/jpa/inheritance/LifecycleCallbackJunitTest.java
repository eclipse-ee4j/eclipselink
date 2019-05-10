/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
//     01/05/2010-2.1 Guy Pelletier
//       - 211324: Add additional event(s) support to the EclipseLink-ORM.XML Schema
//     01/26/2011-2.3 Guy Pelletier
//       - 307664: Lifecycle callbacks not called for object from IndirectSet
//     07/15/2011-2.2.1 Guy Pelletier
//       - 349424: persists during an preCalculateUnitOfWorkChangeSet event are lost
package org.eclipse.persistence.testing.tests.jpa.inheritance;

import java.util.List;
import javax.persistence.EntityManager;

import junit.framework.*;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.inheritance.AAA;
import org.eclipse.persistence.testing.models.jpa.inheritance.Car;
import org.eclipse.persistence.testing.models.jpa.inheritance.Bus;
import org.eclipse.persistence.testing.models.jpa.inheritance.DDD;
import org.eclipse.persistence.testing.models.jpa.inheritance.MacBook;
import org.eclipse.persistence.testing.models.jpa.inheritance.SportsCar;
import org.eclipse.persistence.testing.models.jpa.inheritance.AbstractBus;
import org.eclipse.persistence.testing.models.jpa.inheritance.InheritanceTableCreator;
import org.eclipse.persistence.testing.models.jpa.inheritance.listeners.BusListener;
import org.eclipse.persistence.testing.models.jpa.inheritance.listeners.BusNativeListener;
import org.eclipse.persistence.testing.models.jpa.inheritance.listeners.VehicleListener;
import org.eclipse.persistence.testing.models.jpa.inheritance.listeners.ListenerSuperclass;
import org.eclipse.persistence.testing.models.jpa.inheritance.listeners.FueledVehicleListener;
import org.eclipse.persistence.testing.models.jpa.xml.inheritance.listeners.DefaultListener;

public class LifecycleCallbackJunitTest extends JUnitTestCase {
    private static Number m_busID;

    public LifecycleCallbackJunitTest() {
        super();
    }

    public LifecycleCallbackJunitTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("LifecycleCallbackJunitTest");
        suite.addTest(new LifecycleCallbackJunitTest("testSetup"));
        suite.addTest(new LifecycleCallbackJunitTest("testPrePersistBusOverrideAndAbstractInheritAndDefault"));
        suite.addTest(new LifecycleCallbackJunitTest("testPostPersistBusInheritAndDefault"));
        suite.addTest(new LifecycleCallbackJunitTest("testPostLoadBusInheritAndDefault"));
        suite.addTest(new LifecycleCallbackJunitTest("testPrePersistSportsCarInheritAndExcludeDefault"));
        suite.addTest(new LifecycleCallbackJunitTest("testPostPersistSportsCarInheritAndExcludeDefault"));
        suite.addTest(new LifecycleCallbackJunitTest("testPrePersistSportsCarOverride"));
        suite.addTest(new LifecycleCallbackJunitTest("testQueryInNativePreUpdateEvent"));
        suite.addTest(new LifecycleCallbackJunitTest("testDefaultListenerOnMacBook"));
        suite.addTest(new LifecycleCallbackJunitTest("testPostLoadFromMembersOfSet"));
        suite.addTest(new LifecycleCallbackJunitTest("testSessionEventListenerOnBus"));

        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new InheritanceTableCreator().replaceTables(JUnitTestCase.getServerSession());
        clearCache();
    }

    /**
     * Test added for bug 307664.
     */
    public void testPostLoadFromMembersOfSet() {
        EntityManager em = createEntityManager();
        beginTransaction(em);

        String aaaId;

        try {
            int dddPostLoadCountInitial = 0;
            int dddListenerPostLoadCountInitial = 0;

            AAA aaa = new AAA();
            DDD ddd = new DDD();
            ddd.setAaa(aaa);
            aaa.getDdds().add(ddd);

            em.persist(aaa);
            aaaId = aaa.getId();
            em.flush();

            // This should fire a postLoad event ...
            em.refresh(aaa);
            DDD refreshedDDD = aaa.getDdds().iterator().next();

            int dddPostLoadCountRefresh = refreshedDDD.getCount();
            int dddListenerPostLoadCountRefresh = refreshedDDD.getCount2();
            assertTrue("The PostLoad callback method on DDD was not called after the refresh.", (dddPostLoadCountRefresh - dddPostLoadCountInitial) == 1);
            assertTrue("The PostLoad callback method on the DDD Listener was not called after the refresh.", (dddListenerPostLoadCountRefresh - dddListenerPostLoadCountInitial) == 1);

            commitTransaction(em);

            // Clear the cache and all the works.
            clearCache();
            em.clear();

            AAA findAAA = em.find(AAA.class, aaa.getId());
            // This should fire a postLoad event ...
            DDD findDDD = findAAA.getDdds().iterator().next();
            int dddPostLoadCountFind = findDDD.getCount();
            int dddListenerPostLoadCountFind = findDDD.getCount2();
            assertTrue("The PostLoad callback method on DDD was not called afer the find.", (dddPostLoadCountFind - dddPostLoadCountRefresh) == 1);
            assertTrue("The PostLoad callback method on the DDD Listener was not called afer the find.", (dddListenerPostLoadCountFind - dddListenerPostLoadCountRefresh) == 1);

        } catch (RuntimeException ex) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            closeEntityManager(em);
            throw ex;
        }
    }

    public void testPostLoadBusInheritAndDefault() {
        int vehiclePostLoadCountBefore = VehicleListener.POST_LOAD_COUNT;
        int defaultListenerPostLoadCountBefore = DefaultListener.POST_LOAD_COUNT;

        Bus bus = createEntityManager().find(Bus.class, m_busID);

        int vehiclePostLoadCountAfter = VehicleListener.POST_LOAD_COUNT;
        int defaultListenerPostLoadCountAfter = DefaultListener.POST_LOAD_COUNT;

        assertFalse("The PostLoad callback method for Vehicle was not called.", vehiclePostLoadCountBefore == vehiclePostLoadCountAfter);
        assertFalse("The PostLoad callback method for DefaultListener was not called.", defaultListenerPostLoadCountBefore == defaultListenerPostLoadCountAfter);
    }

    public void testPostPersistBusInheritAndDefault() {
        int busListenerPostPersistCountBefore = BusListener.POST_PERSIST_COUNT;
        int fueledVehiclePostPersistCountBefore = FueledVehicleListener.POST_PERSIST_COUNT;
        int defaultListenerPostPersistCountBefore = DefaultListener.POST_PERSIST_COUNT;
        int defaultListenerPostLoadCountBefore = DefaultListener.POST_LOAD_COUNT;
        int defaultListenerPostLoadCountIntermidiate;

        EntityManager em = createEntityManager();
        beginTransaction(em);

        try {
            Bus bus = new Bus();
            bus.setPassengerCapacity(new Integer(50));
            bus.setFuelCapacity(new Integer(175));
            bus.setDescription("OC Transpo Bus");
            bus.setFuelType("Diesel");
            em.persist(bus);
            em.flush();

            // This should fire a postLoad event ...
            em.refresh(bus);
            defaultListenerPostLoadCountIntermidiate = DefaultListener.POST_LOAD_COUNT;

            javax.persistence.Query q = em.createQuery("select distinct b from Bus b where b.id = " + bus.getId());
            // This should not fire a postLoad event ...
            q.getResultList();

            commitTransaction(em);
        } catch (RuntimeException ex) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            closeEntityManager(em);
            throw ex;
        }

        int busListenerPostPersistCountAfter = BusListener.POST_PERSIST_COUNT;
        int fueledVehiclePostPersistCountAfter = FueledVehicleListener.POST_PERSIST_COUNT;
        int defaultListenerPostPersistCountAfter = DefaultListener.POST_PERSIST_COUNT;
        int defaultListenerPostLoadCountAfter = DefaultListener.POST_LOAD_COUNT;

        assertFalse("The PostPersist callback method on BusListener was not called.", busListenerPostPersistCountBefore == busListenerPostPersistCountAfter);
        assertFalse("The PostPersist callback method on FueledVehicleListener was not called.", fueledVehiclePostPersistCountBefore == fueledVehiclePostPersistCountAfter);
        assertFalse("The PostPersist callback method on DefaultListener was not called.", defaultListenerPostPersistCountBefore == defaultListenerPostPersistCountAfter);
        assertTrue("The PostLoad callback method on DefaultListener was called more than once, possibly on the refresh.", (defaultListenerPostLoadCountIntermidiate - defaultListenerPostLoadCountBefore) == 1);
        assertTrue("The PostLoad callback method on DefaultListener was called on the getQueryResult().", defaultListenerPostLoadCountIntermidiate == defaultListenerPostLoadCountAfter);
    }

    public void testPostPersistSportsCarInheritAndExcludeDefault() {
        int fueledVehiclePostPersistCountBefore = FueledVehicleListener.POST_PERSIST_COUNT;
        int defaultListenerPostPersistCountBefore = DefaultListener.POST_PERSIST_COUNT;

        EntityManager em = createEntityManager();
        beginTransaction(em);

        try {
            SportsCar sportsCar = new SportsCar();
            sportsCar.setPassengerCapacity(new Integer(4));
            sportsCar.setFuelCapacity(new Integer(55));
            sportsCar.setDescription("Porshe");
            sportsCar.setFuelType("Gas");
            em.persist(sportsCar);
            commitTransaction(em);
        } catch (RuntimeException ex) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            closeEntityManager(em);
            throw ex;
        }

        int fueledVehiclePostPersistCountAfter = FueledVehicleListener.POST_PERSIST_COUNT;
        int defaultListenerPostPersistCountAfter = DefaultListener.POST_PERSIST_COUNT;

        assertFalse("The PostPersist callback method on FueledVehicleListener was not called.", fueledVehiclePostPersistCountBefore == fueledVehiclePostPersistCountAfter);
        assertTrue("The PostPersist callback method on DefaultListener was called.", defaultListenerPostPersistCountBefore == defaultListenerPostPersistCountAfter);
    }

    public void testPrePersistBusOverrideAndAbstractInheritAndDefault() {
        int busListenerPrePersistCountBefore = BusListener.PRE_PERSIST_COUNT;
        int busNativeListenerPreWriteCountBefore = BusNativeListener.PRE_WRITE_COUNT;
        int busNativeListenerPostWriteCountBefore = BusNativeListener.POST_WRITE_COUNT;
        int listenerSuperclassPrePersistCountBefore = ListenerSuperclass.COMMON_PRE_PERSIST_COUNT;
        int abstractBusPrePeristCountBefore = AbstractBus.PRE_PERSIST_COUNT;
        int defaultListenerPrePersistCountBefore = DefaultListener.PRE_PERSIST_COUNT;

        EntityManager em = createEntityManager();
        beginTransaction(em);

        try {
            Bus bus = new Bus();
            bus.setPassengerCapacity(new Integer(30));
            bus.setFuelCapacity(new Integer(100));
            bus.setDescription("School Bus");
            bus.setFuelType("Diesel");
            em.persist(bus);
            m_busID = bus.getId();
            commitTransaction(em);
        } catch (RuntimeException ex) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            closeEntityManager(em);
            throw ex;
        }

        closeEntityManager(em);

        int busListenerPrePersistCountAfter = BusListener.PRE_PERSIST_COUNT;
        int busNativeListenerPreWriteCountAfter = BusNativeListener.PRE_WRITE_COUNT;
        int busNativeListenerPostWriteCountAfter = BusNativeListener.POST_WRITE_COUNT;
        int listenerSuperclassPrePersistCountAfter = ListenerSuperclass.COMMON_PRE_PERSIST_COUNT;
        int abstractBusPrePeristCountAfter = AbstractBus.PRE_PERSIST_COUNT;
        int defaultListenerPrePersistCountAfter = DefaultListener.PRE_PERSIST_COUNT;

        assertFalse("The PrePersist callback method on BusListener was not called.", busListenerPrePersistCountBefore == busListenerPrePersistCountAfter);
        assertFalse("The PreWrite (native) callback method on BusNativeListener was not called.", busNativeListenerPreWriteCountBefore == busNativeListenerPreWriteCountAfter);
        assertFalse("The PostWrite (native) callback method on BusNativeListener was not called.", busNativeListenerPostWriteCountBefore == busNativeListenerPostWriteCountAfter);
        assertTrue("The PrePersist callback method on ListenerSuperclass was called.", listenerSuperclassPrePersistCountBefore == listenerSuperclassPrePersistCountAfter);
        assertFalse("The PrePersist callback method on AbstractBus was not called.", abstractBusPrePeristCountBefore == abstractBusPrePeristCountAfter);
        assertFalse("The PrePersist callback method on DefaultListener was not called.", defaultListenerPrePersistCountBefore == defaultListenerPrePersistCountAfter);
        assertFalse("The PrePersist callback method on DefaultListener was called more than once.", defaultListenerPrePersistCountAfter - defaultListenerPrePersistCountBefore >1 );
    }

    public void testPrePersistSportsCarInheritAndExcludeDefault() {
        int listenerSuperclassPrePersistCountBefore = ListenerSuperclass.COMMON_PRE_PERSIST_COUNT;
        int defaultListenerPrePersistCountBefore = DefaultListener.PRE_PERSIST_COUNT;

        EntityManager em = createEntityManager();
        beginTransaction(em);

        try {
            SportsCar sportsCar = new SportsCar();
            sportsCar.setPassengerCapacity(new Integer(2));
            sportsCar.setFuelCapacity(new Integer(60));
            sportsCar.setDescription("Corvette");
            sportsCar.setFuelType("Gas");
            em.persist(sportsCar);
            commitTransaction(em);
        } catch (RuntimeException ex) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            closeEntityManager(em);
            throw ex;
        }

        closeEntityManager(em);

        int listenerSuperclassPrePersistCountAfter = ListenerSuperclass.COMMON_PRE_PERSIST_COUNT;
        int defaultListenerPrePersistCountAfter = DefaultListener.PRE_PERSIST_COUNT;

        assertFalse("The PrePersist callback method on ListenerSuperclass was not called.", listenerSuperclassPrePersistCountBefore == listenerSuperclassPrePersistCountAfter);
        assertTrue("The PrePersist callback method on DefaultListener was called.", defaultListenerPrePersistCountBefore == defaultListenerPrePersistCountAfter);
    }

    public void testPrePersistSportsCarOverride() {
        int carPrePersistCountBefore = Car.PRE_PERSIST_COUNT;
        int sportsCarPrePersistCountBefore = SportsCar.PRE_PERSIST_COUNT;

        EntityManager em = createEntityManager();
        beginTransaction(em);

        try {
            SportsCar sportsCar = new SportsCar();
            sportsCar.setPassengerCapacity(new Integer(2));
            sportsCar.setFuelCapacity(new Integer(90));
            sportsCar.setDescription("Viper");
            sportsCar.setFuelType("Gas");
            em.persist(sportsCar);
            commitTransaction(em);
        } catch (RuntimeException ex) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            closeEntityManager(em);
            throw ex;
        }

        closeEntityManager(em);

        int carPrePersistCountAfter = Car.PRE_PERSIST_COUNT;
        int sportsCarPrePersistCountAfter = SportsCar.PRE_PERSIST_COUNT;

        assertTrue("The PrePersist callback method on Car was called.", carPrePersistCountBefore == carPrePersistCountAfter);
        assertFalse("The PrePersist callback method on Sports car was not called.", sportsCarPrePersistCountBefore == sportsCarPrePersistCountAfter);
    }

    /**
     * bug: 243993 - RepeatableWriteUnitOfWork's usage of UnitOfWorkImpl can cause ConcurrentModificationException
     *  - will occur if a native EclipseLink preUpdate event reads entities into the UOW, as it occurs while
     *  the uow is iterating over its clone list.
     */
    public void testQueryInNativePreUpdateEvent(){
        clearCache();

        EntityManager em = createEntityManager();
        List<Bus> results = em.createQuery("Select e from Bus e").getResultList();
        for (Bus bus:results){
            bus.setDescription("QueryInNativePreUpdateEvent");
        }
        beginTransaction(em);
        try{
            em.flush();
        } finally {
            if(this.isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    public void testDefaultListenerOnMacBook() {
        // Tests the default listeners on those entities that don't have their
        // own separate entity listener(s) as well.
        int defaultListenerPrePersistCountBefore = DefaultListener.PRE_PERSIST_COUNT;

        EntityManager em = createEntityManager();
        beginTransaction(em);

        try {
            MacBook macBook = new MacBook();
            macBook.setRam(8);
            em.persist(macBook);
            commitTransaction(em);
        } catch (RuntimeException ex) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            closeEntityManager(em);
            throw ex;
        }

        closeEntityManager(em);

        int defaultListenerPrePersistCountAfter = DefaultListener.PRE_PERSIST_COUNT;

        assertFalse("The PrePersist callback method on DefaultListener was not called.", defaultListenerPrePersistCountBefore == defaultListenerPrePersistCountAfter);
    }

    public void testSessionEventListenerOnBus() {
        EntityManager em = createEntityManager();

        try {
            beginTransaction(em);
            Bus bus = new Bus();
            // This string must not change ... session event listener will look
            // for it and will create a new bus with description 'Listener test Bus2'
            bus.setDescription("Listener test Bus1");
            em.persist(bus);
            commitTransaction(em);

            // Should be able to find the bus created through the listener.
            List results = em.createQuery("select object(b) from Bus b where b.description = 'Listener test Bus2'").getResultList();
            assertTrue("Did not find the bus created by the session listener.", results.size() == 1);
        } catch (RuntimeException ex) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            throw ex;
        } finally {
            closeEntityManager(em);
        }
    }
}
