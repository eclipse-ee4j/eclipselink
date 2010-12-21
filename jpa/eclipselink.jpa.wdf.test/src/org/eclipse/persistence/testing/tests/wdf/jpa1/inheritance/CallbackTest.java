/*******************************************************************************
 * Copyright (c) 2005, 2009 SAP. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     SAP - initial API and implementation
 ******************************************************************************/

package org.eclipse.persistence.testing.tests.wdf.jpa1.inheritance;

import java.lang.annotation.Annotation;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;

import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Bicycle;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.BikeListener;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.CallbackEventListener;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.MountainBike;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Vehicle;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.VehicleListener;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.junit.Test;

public class CallbackTest extends JPA1Base {

    private final MyCallbackEventListener listener = new MyCallbackEventListener();
    private final Map<Class<? extends Annotation>, Bicycle> expectedBikeEvent = new HashMap<Class<? extends Annotation>, Bicycle>();
    private final Map<Class<? extends Annotation>, Vehicle> expectedVehicleEvent = new HashMap<Class<? extends Annotation>, Vehicle>();

    private static final class MyCallbackEventListener implements CallbackEventListener {
        private final List<String> log = new ArrayList<String>();
        private Object expectedEntity;
        private int callCounter;
        private Class<? extends Annotation> expectedEvent;

        public void prepare(final Object entity, final Class<? extends Annotation> event) {
            expectedEntity = entity;
            expectedEvent = event;
            log.clear();
            callCounter = 0;
        }

        public void callbackCalled(Object entity, Class<? extends Annotation> event) {
            if (event != expectedEvent) {
                log.add("Wrong event, expected " + expectedEvent + ", got " + event);
            }
            if (entity != expectedEntity) {
                log.add("Wrong object, expected " + expectedEntity + ", got " + entity);
            }
            callCounter++;
        }

        public List<String> getLog() {
            return log;
        }

        public int getCallCounter() {
            return callCounter;
        }
    }

    public void bikeCallbackCalled(final Bicycle bike, Class<? extends Annotation> event) {
        if (null == expectedBikeEvent.get(event)) {
            flop("Unexpected event " + event);
            return;
        }
        final Bicycle expectedBike = expectedBikeEvent.remove(event);
        verify(expectedBike == bike, "Unexpected Bike in event " + event + ". Expected: " + expectedBike + ", but got: " + bike);
    }

    public void vehicleCallbackCalled(final Vehicle vehicle, Class<? extends Annotation> event) {
        if (null == expectedVehicleEvent.get(event)) {
            flop("Unexpected event " + event);
            return;
        }
        final Vehicle expectedVehicle = expectedVehicleEvent.remove(event);
        verify(expectedVehicle == vehicle, "Unexpected Vehicle in event " + event + ". Expected: " + expectedVehicle
                + ", but got: " + vehicle);
    }

    @Test
    public void testPrePersist() throws SQLException {
        clearAllTables();
        final JPAEnvironment environment = getEnvironment();
        final EntityManager em = environment.getEntityManager();
        try {
            VehicleListener.setListener(new CallbackEventListener() {
                public void callbackCalled(Object entity, Class<? extends Annotation> event) {
                    vehicleCallbackCalled((Vehicle) entity, event);
                }
            });
            try {
                BikeListener.setListener(new CallbackEventListener() {
                    public void callbackCalled(Object entity, Class<? extends Annotation> event) {
                        bikeCallbackCalled((Bicycle) entity, event);
                    }
                });
                try {
                    environment.beginTransaction(em);
                    final Vehicle vehicle = new Vehicle();
                    vehicle.setBrand("Mercedes");
                    vehicle.setColor("cobalt blue");
                    expectedVehicleEvent.put(PrePersist.class, vehicle);
                    em.persist(vehicle);
                    verify(expectedVehicleEvent.size() == 0, "Missing call to prepersist.");
                    final Bicycle bicycle = new Bicycle();
                    bicycle.setBrand("Peugeot");
                    bicycle.setColor("red");
                    bicycle.setNumberOfGears((short) 12);
                    expectedVehicleEvent.put(PrePersist.class, bicycle);
                    listener.prepare(bicycle, PrePersist.class);
                    bicycle.registerListener(listener);
                    try {
                        em.persist(bicycle);
                    } finally {
                        bicycle.registerListener(null);
                    }
                    verify(listener.getCallCounter() == 1, "Missing call to entity callback.");
                    for (final String errorMessage : listener.getLog()) {
                        flop(errorMessage);
                    }
                    verify(expectedVehicleEvent.size() == 0, "Missing call to prepersist.");
                    final MountainBike mountainBike = new MountainBike();
                    mountainBike.setBrand("Fischer");
                    mountainBike.setColor("brown");
                    mountainBike.setNumberOfGears((short) 24);
                    expectedBikeEvent.put(PrePersist.class, mountainBike);
                    expectedVehicleEvent.put(PrePersist.class, mountainBike);
                    listener.prepare(mountainBike, PrePersist.class);
                    mountainBike.registerListener(listener);
                    try {
                        em.persist(mountainBike);
                    } finally {
                        mountainBike.registerListener(null);
                    }
                    verify(listener.getCallCounter() == 1, "Missing call to entity callback.");
                    for (final String errorMessage : listener.getLog()) {
                        flop(errorMessage);
                    }
                    verify(expectedVehicleEvent.size() == 0, "Missing call to prepersist.");
                    verify(expectedBikeEvent.size() == 0, "Missing call to prepersist.");
                } finally {
                    BikeListener.setListener(null);
                }
            } finally {
                VehicleListener.setListener(null);
            }
            environment.commitTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testPreRemove() throws SQLException {
        clearAllTables();
        final JPAEnvironment environment = getEnvironment();
        final EntityManager em = environment.getEntityManager();
        try {
            // persist test entities (without listening to callbacks)
            environment.beginTransaction(em);
            final Vehicle vehicle = new Vehicle();
            vehicle.setBrand("Mercedes");
            vehicle.setColor("cobalt blue");
            em.persist(vehicle);
            final Bicycle bicycle = new Bicycle();
            bicycle.setBrand("Peugeot");
            bicycle.setColor("red");
            bicycle.setNumberOfGears((short) 12);
            em.persist(bicycle);
            final MountainBike mountainBike = new MountainBike();
            mountainBike.setBrand("Fischer");
            mountainBike.setColor("brown");
            mountainBike.setNumberOfGears((short) 24);
            em.persist(mountainBike);
            environment.commitTransactionAndClear(em);
            environment.beginTransaction(em);
            // find the entities right now (before registering listeners)
            final Vehicle storedVehicle = em.find(Vehicle.class, vehicle.getId());
            final Bicycle storedBicycle = em.find(Bicycle.class, bicycle.getId());
            final MountainBike storedMountainBike = (MountainBike) em.find(Vehicle.class, mountainBike.getId());
            // test starts here
            VehicleListener.setListener(new CallbackEventListener() {
                public void callbackCalled(Object entity, Class<? extends Annotation> event) {
                    vehicleCallbackCalled((Vehicle) entity, event);
                }
            });
            try {
                BikeListener.setListener(new CallbackEventListener() {
                    public void callbackCalled(Object entity, Class<? extends Annotation> event) {
                        bikeCallbackCalled((Bicycle) entity, event);
                    }
                });
                try {
                    expectedVehicleEvent.put(PreRemove.class, storedVehicle);
                    em.remove(storedVehicle);
                    verify(expectedVehicleEvent.size() == 0, "Missing call to preremove.");
                    expectedVehicleEvent.put(PreRemove.class, storedBicycle);
                    listener.prepare(storedBicycle, PreRemove.class);
                    storedBicycle.registerListener(listener);
                    try {
                        em.remove(storedBicycle);
                    } finally {
                        storedBicycle.registerListener(null);
                    }
                    verify(listener.getCallCounter() == 1, "Missing call to entity callback.");
                    for (final String errorMessage : listener.getLog()) {
                        flop(errorMessage);
                    }
                    verify(expectedVehicleEvent.size() == 0, "Missing call to prepersist.");
                    expectedBikeEvent.put(PreRemove.class, storedMountainBike);
                    expectedVehicleEvent.put(PreRemove.class, storedMountainBike);
                    listener.prepare(storedMountainBike, PreRemove.class);
                    storedMountainBike.registerListener(listener);
                    try {
                        em.remove(storedMountainBike);
                    } finally {
                        storedMountainBike.registerListener(null);
                    }
                    verify(listener.getCallCounter() == 1, "Missing call to entity callback.");
                    for (final String errorMessage : listener.getLog()) {
                        flop(errorMessage);
                    }
                    verify(expectedVehicleEvent.size() == 0, "Missing call to prepersist.");
                    verify(expectedBikeEvent.size() == 0, "Missing call to prepersist.");
                } finally {
                    BikeListener.setListener(null);
                }
            } finally {
                VehicleListener.setListener(null);
            }
            environment.commitTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }
}
