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
package org.eclipse.persistence.testing.tests.jpa.spring.dao;

import java.lang.reflect.Field;
import java.util.List;


import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.jpa.AbstractJpaTests;

import test.org.eclipse.persistence.testing.models.jpa.spring.Address;
import test.org.eclipse.persistence.testing.models.jpa.spring.Route;
import test.org.eclipse.persistence.testing.models.jpa.spring.Truck;

/**
 * Base TestClass for Spring Framework tests that use Spring's AbstractJpaTests.
 * All test methods will be exercised by the different Spring configurations that extend this class;
 * they will all require and implementation of a dao.
 * @see org.eclipse.persistence.testing.tests.jpa.spring.dao.SpringDao
 */
public abstract class SpringAbstractJpaTestsCase extends AbstractJpaTests {

    protected abstract String[] getConfigLocations(); 
    
    protected static SpringDao dao;

    public abstract void setDao(SpringDao dao);
    
    public void testPersist(){
        Truck truck = new Truck("persist");
        try {
            dao.persist(truck);
            assertTrue(((Truck)dao.find(truck)).getDriverName().equals("persist"));
        }catch (Exception e) {
            assertFalse("Error during persist: " + e, true);
        }finally {
            dao.remove(truck);
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
            dao.persist(truck);
            assertTrue(((Truck)dao.find(truck)).getDriverName().equals("cascade"));
            assertTrue(((Route)dao.find(route)).getAverageTimeMins() == 155);
            assertTrue(((Address)dao.find(address)).getStreet().equals("First St."));

            dao.remove(truck);
            assertTrue(((Truck)dao.find(truck)) == null);
            assertTrue(((Route)dao.find(route)).getAverageTimeMins() == 155);
            assertTrue(((Address)dao.find(address)).getStreet().equals("First St."));
        }catch (Exception e) {
            assertFalse("Error during cascade persist with remove: " + e, true);
        }finally {
            route.removeAddress(address);
            dao.remove(address);
            dao.remove(route);
        }
    }
    
    public void testRemove(){
        Truck truck = new Truck("remove");
        try {
            dao.persist(truck);
            assertTrue(((Truck) dao.find(truck)).getDriverName().equals("remove"));
            dao.remove(truck);
            assertTrue(dao.find(truck) == null);
        }catch (Exception e) {
            assertFalse("Error during remove: " + e, true);
        }
    }
    
    public void testContains(){
        Truck truck = new Truck("contains");
        try {
            dao.persist(truck); 
            assertTrue(dao.contains(truck));
            assertFalse(dao.contains(new Truck("doesNotContain")));
        }catch (Exception e) {
            assertFalse("Error during contains: " + e, true);
        }finally {
            dao.remove(truck);
        }
    }
    
    public void testMerge(){
        Truck truck = new Truck("merge");
        try {
            truck = (Truck)dao.merge(truck);
            assertTrue(((Truck) dao.find(truck)).getDriverName().equals("merge"));
        }catch (Exception e) {
            assertFalse("Error during merge: " + e, true);
        }finally {
            dao.remove(truck);
        }
    }
    
    public void testRefresh(){
        Truck truck = new Truck("refresh");
        try {
            dao.persist(truck);
            dao.flush();
            assertTrue(((Truck) dao.find(truck)).getDriverName().equals("refresh"));
            truck.setDriverName("changeRefresh");
            dao.refresh(truck);
            assertTrue(truck.getDriverName().equals("refresh"));
        }catch (Exception e) {
            assertFalse("Error during refresh: " + e, true);
        }finally {
            dao.remove(truck);
        }
    }
    
    public void testFlush(){
        Truck truck = new Truck("flush");
        try {
            dao.persist(truck);
            assertTrue(((Truck) dao.find(truck)).getDriverName().equals("flush"));
            truck.setDriverName("reflush");
            dao.flush();
            assertTrue(((Truck) dao.find(truck)).getDriverName().equals("reflush"));
        }catch (Exception e) {
            assertFalse("Error during flush: " + e, true);
        }finally {
            dao.remove(truck);
        }
    }

    public void testNamedQuery(){
        Truck truck = new Truck("namedQuery");
        try {
            dao.persist(truck);
            List l= dao.findByNamedQuery("findTruckByDriverName", "namedQuery");
            assertTrue(l.contains(truck));
        }catch (Exception e){
            assertFalse("Error during named query: " + e, true);
        }finally {
            dao.remove(truck);
        }
    }
    
    //COMMENT OUT test if weaving is disabled
    public void testAddressVH() { 
        Field f = null;
        try {
            f = Address.class.getDeclaredField("_persistence_route_vh");
        } catch (Exception e) {
            assertFalse("Exception when Address value holder retrieved", true);
        }      
        assertNotNull("Address class does not have '_persistence_route_vh' field", f);
    }
    
    public void testDataExceptionTranslation(){
        try {
            dao.refresh(new Truck("detachedTruck"));
            assertFalse("No exception thrown with bad refresh", true);
        }catch (InvalidDataAccessApiUsageException idaaue) {
            //expected, so do nothing
        }catch (Exception e) {
            assertFalse("Wrong exception thrown with bad refresh: " + e, true);
        }
    }
}
