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
package org.eclipse.persistence.testing.tests.jpa.helidon.dao;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import org.springframework.transaction.annotation.Transactional;
import test.org.eclipse.persistence.testing.models.jpa.spring.Address;
import test.org.eclipse.persistence.testing.models.jpa.spring.Route;
import test.org.eclipse.persistence.testing.models.jpa.spring.Truck;

/**
 * Base TestClass for Spring Framework tests that use Spring's AbstractJpaTests.
 * All test methods will be exercised by the different Spring configurations that extend this class;
 * they will all require and implementation of a dao.
 * @see org.eclipse.persistence.testing.tests.jpa.helidon.dao.SpringDao
 */

@Transactional
public abstract class SpringAbstractJpaTestsCase {

    @Autowired
    private SpringDao dao;

    @Test
    public void testPersist(){
        Truck truck = new Truck("persist");
        try {
            dao.persist(truck);
            assertEquals("persist", ((Truck) dao.find(truck)).getDriverName());
        }catch (Exception e) {
            fail("Error during persist: " + e);
        }finally {
            dao.remove(truck);
        }
    }

    //With this entity model, removing the truck should not delete the route nor address
    @Test
    public void testCascadePersistwithRemove(){
        Truck truck = new Truck("cascade");
        Route   route   = new Route(155);
        Address address = new Address(1, 1, "First St.");
        route.addAddress(address);
        truck.setRoute(route);
        try {
            dao.persist(truck);
            assertEquals("cascade", ((Truck) dao.find(truck)).getDriverName());
            assertEquals(155, ((Route) dao.find(route)).getAverageTimeMins());
            assertEquals("First St.", ((Address) dao.find(address)).getStreet());

            dao.remove(truck);
            assertNull(dao.find(truck));
            assertEquals(155, ((Route) dao.find(route)).getAverageTimeMins());
            assertEquals("First St.", ((Address) dao.find(address)).getStreet());
        }catch (Exception e) {
            fail("Error during cascade persist with remove: " + e);
        }finally {
            route.removeAddress(address);
            dao.remove(address);
            dao.remove(route);
        }
    }

    @Test
    public void testRemove(){
        Truck truck = new Truck("remove");
        try {
            dao.persist(truck);
            assertEquals("remove", ((Truck) dao.find(truck)).getDriverName());
            dao.remove(truck);
            assertNull(dao.find(truck));
        }catch (Exception e) {
            fail("Error during remove: " + e);
        }
    }

    @Test
    public void testContains(){
        Truck truck = new Truck("contains");
        try {
            dao.persist(truck);
            assertTrue(dao.contains(truck));
            assertFalse(dao.contains(new Truck("doesNotContain")));
        }catch (Exception e) {
            fail("Error during contains: " + e);
        }finally {
            dao.remove(truck);
        }
    }

    @Test
    public void testMerge(){
        Truck truck = new Truck("merge");
        try {
            truck = (Truck)dao.merge(truck);
            assertEquals("merge", ((Truck) dao.find(truck)).getDriverName());
        }catch (Exception e) {
            fail("Error during merge: " + e);
        }finally {
            dao.remove(truck);
        }
    }

    @Test
    public void testRefresh(){
        Truck truck = new Truck("refresh");
        try {
            dao.persist(truck);
            dao.flush();
            assertEquals("refresh", ((Truck) dao.find(truck)).getDriverName());
            truck.setDriverName("changeRefresh");
            dao.refresh(truck);
            assertEquals("refresh", truck.getDriverName());
        }catch (Exception e) {
            fail("Error during refresh: " + e);
        }finally {
            dao.remove(truck);
        }
    }

    @Test
    public void testFlush(){
        Truck truck = new Truck("flush");
        try {
            dao.persist(truck);
            assertEquals("flush", ((Truck) dao.find(truck)).getDriverName());
            truck.setDriverName("reflush");
            dao.flush();
            assertEquals("reflush", ((Truck) dao.find(truck)).getDriverName());
        }catch (Exception e) {
            fail("Error during flush: " + e);
        }finally {
            dao.remove(truck);
        }
    }

    @Test
    public void testNamedQuery(){
        Truck truck = new Truck("namedQuery");
        try {
            dao.persist(truck);
            List<?> l = dao.findByNamedQuery("findTruckByDriverName", "namedQuery");
            assertTrue(l.contains(truck));
        }catch (Exception e){
            fail("Error during named query: " + e);
        }finally {
            dao.remove(truck);
        }
    }

    //COMMENT OUT test if weaving is disabled
    @Test
    public void testAddressVH() {
        Field f = null;
        try {
            f = Address.class.getDeclaredField("_persistence_route_vh");
        } catch (Exception e) {
            fail("Exception when Address value holder retrieved");
        }
        assertNotNull("Address class does not have '_persistence_route_vh' field", f);
    }

    @Ignore
    @Test
    public void testDataExceptionTranslation(){
        try {
            dao.refresh(new Truck("detachedTruck"));
            fail("No exception thrown with bad refresh");
        }catch (InvalidDataAccessApiUsageException idaaue) {
            //expected, so do nothing
        }catch (Exception e) {
            fail("Wrong exception thrown with bad refresh: " + e);
        }
    }
}
