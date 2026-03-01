/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.jpa.spring;

import java.lang.reflect.Field;
import java.util.List;

import jakarta.persistence.Query;

import org.springframework.dao.InvalidDataAccessApiUsageException;

import junit.framework.TestCase;
import test.org.eclipse.persistence.testing.models.jpa.spring.Address;
import test.org.eclipse.persistence.testing.models.jpa.spring.Route;
import test.org.eclipse.persistence.testing.models.jpa.spring.Truck;


/**
 * Base Junit TestClass for Spring Framework tests.
 * All test methods here will be exercised by the different Spring configurations that extend this class.
 */
public abstract class SpringJunitTestCase extends TestCase {

    private EntityManagerWrapper em;

    protected void setEntityManager(EntityManagerWrapper em) {
        this.em = em;
    }

    public void testPersist(){
        Truck truck = new Truck("persist");
        try {
            em.persist(truck);
            assertEquals("persist", ((Truck) em.find(truck)).getDriverName());
        } finally {
            em.remove(truck);
        }
    }

    //With this entity model, removing the truck should not delete the route nor address
    public void testCascadePersistwithRemove(){
        Truck truck = new Truck("cascade");
        Route   route   = new Route(155);
        Address address = new Address(1, 1, "First St.");
        route.addAddress(address);
        truck.setRoute(route);
        try {
            em.persist(truck);
            assertEquals("cascade", ((Truck) em.find(truck)).getDriverName());
            assertEquals(155, ((Route) em.find(route)).getAverageTimeMins());
            assertEquals("First St.", ((Address) em.find(address)).getStreet());

            em.remove(truck);
            assertNull(em.find(truck));
            assertEquals(155, ((Route) em.find(route)).getAverageTimeMins());
            assertEquals("First St.", ((Address) em.find(address)).getStreet());
        } finally {
            route.removeAddress(address);
            em.remove(address);
            em.remove(route);
        }
    }

    public void testRemove(){
        Truck truck = new Truck("remove");
        em.persist(truck);
        assertEquals("remove", ((Truck) em.find(truck)).getDriverName());
        em.remove(truck);
        assertNull(em.find(truck));
    }

    public void testContains(){
        Truck truck = new Truck("contains");
        try {
            em.persist(truck);
            assertTrue(em.contains(truck));
            assertFalse(em.contains(new Truck("doesNotContain")));
        } finally {
            em.remove(truck);
        }
    }

    public void testMerge(){
        Truck truck = new Truck("merge");
        try {
            truck = (Truck)em.merge(truck);
            assertEquals("merge", ((Truck) em.find(truck)).getDriverName());
        } finally {
            em.remove(truck);
        }
    }

    public void testRefresh(){
        Truck truck = new Truck("refresh");
        try {
            em.persist(truck);
            em.flush();
            assertEquals("refresh", ((Truck) em.find(truck)).getDriverName());
            truck.setDriverName("changeRefresh");
            em.refresh(truck);
            assertEquals("refresh", truck.getDriverName());
        } finally {
            em.remove(truck);
        }
    }

    public void testFlush(){
        Truck truck = new Truck("flush");
        try {
            em.persist(truck);
            assertEquals("flush", ((Truck) em.find(truck)).getDriverName());
            truck.setDriverName("reflush");
            em.flush();
            assertEquals("reflush", ((Truck) em.find(truck)).getDriverName());
        } finally {
            em.remove(truck);
        }
    }

    public void testCreateNativeQuery(){
        Route route = new Route();
        route.setAverageTimeMins(150);
        em.persist(route);
        Route r = (Route)em.createNativeQuery(
                "SELECT id as ID, averageTimeMins as AVERAGETIMEMINS FROM SPRING_TLE_ROUTE WHERE (ID="+route.getId()+")", Route.class)
                .getSingleResult();
        assertEquals(150, r.getAverageTimeMins());

        em.executeNativeQuery("DELETE FROM SPRING_TLE_ROUTE WHERE (ID="+route.getId()+")");
        List<?> l = em.createNativeQuery(
                "SELECT id as ID, averageTimeMins as AVERAGETIMEMINS FROM SPRING_TLE_ROUTE WHERE (ID="+route.getId()+")", Route.class)
                .getResultList();
        assertEquals(0, l.size());
    }

    public void testNamedQuery(){
        Truck truck = new Truck("namedQuery");
        Route route = new Route();
        truck.setRoute(route);
        try {
            em.persist(truck);
            Query q = em.createNamedQuery("findTruckByDriverName");
            q.setParameter(1, "namedQuery");
            assertEquals(((Truck) q.getSingleResult()).getRoute().getId(), route.getId());
        } finally {
            em.remove(truck);
            em.remove(route);
        }
    }

    public void testCreateQuery(){
        Truck truck = new Truck("createQuery");
        try {
            em.persist(truck);
            em.flush();
            Query q = em.createQuery("SELECT t FROM Truck t WHERE t.driverName LIKE ?1");
            q.setParameter(1, "createQuery");
            assertEquals(((Truck) q.getSingleResult()).getId(), truck.getId());
        } finally {
            em.remove(truck);
        }
    }

    //COMMENT OUT if weaving is disabled
    public void testAddressVH() {
        Field f = null;
        try {
            f = Address.class.getDeclaredField("_persistence_route_vh");
        } catch (Exception e) {
            fail("Exception when Address value holder retrieved");
        }
        assertNotNull("Address class does not have '_persistence_route_vh' field", f);
    }

    public void testDataExceptionTranslation(){
        try {
            em.refresh(new Truck("detachedTruck"));
            fail("No exception thrown with bad refresh");
        }catch (InvalidDataAccessApiUsageException idaaue) {
            //expected, so do nothing
        }catch (Exception e) {
            fail("Wrong exception thrown with bad refresh: " + e);
        }
    }

}
