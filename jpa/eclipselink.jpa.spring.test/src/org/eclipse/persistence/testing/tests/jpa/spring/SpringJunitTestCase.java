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
package org.eclipse.persistence.testing.tests.jpa.spring;

import java.lang.reflect.Field;
import java.util.List;

import javax.persistence.Query;

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
            assertTrue(((Truck)em.find(truck)).getDriverName().equals("persist"));
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
            assertTrue(((Truck)em.find(truck)).getDriverName().equals("cascade"));
            assertTrue(((Route)em.find(route)).getAverageTimeMins() == 155);
            assertTrue(((Address)em.find(address)).getStreet().equals("First St."));

            em.remove(truck);
            assertTrue(((Truck)em.find(truck)) == null);
            assertTrue(((Route)em.find(route)).getAverageTimeMins() == 155);
            assertTrue(((Address)em.find(address)).getStreet().equals("First St."));
        } finally {
            route.removeAddress(address);
            em.remove(address);
            em.remove(route);
        }
    }
    
    public void testRemove(){
        Truck truck = new Truck("remove");
        em.persist(truck);
        assertTrue(((Truck) em.find(truck)).getDriverName().equals("remove"));
        em.remove(truck);
        assertTrue(em.find(truck) == null);
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
            assertTrue(((Truck) em.find(truck)).getDriverName().equals("merge"));
        } finally {
            em.remove(truck);
        }
    }
    
    public void testRefresh(){
        Truck truck = new Truck("refresh");
        try {
            em.persist(truck);
            em.flush();
            assertTrue(((Truck) em.find(truck)).getDriverName().equals("refresh"));
            truck.setDriverName("changeRefresh");
            em.refresh(truck);
            assertTrue(truck.getDriverName().equals("refresh"));
        } finally {
            em.remove(truck);
        }
    }
    
    public void testFlush(){
        Truck truck = new Truck("flush");
        try {
            em.persist(truck);
            assertTrue(((Truck) em.find(truck)).getDriverName().equals("flush"));
            truck.setDriverName("reflush");
            em.flush();
            assertTrue(((Truck) em.find(truck)).getDriverName().equals("reflush"));
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
        assertTrue(r.getAverageTimeMins() == 150);
        
        em.executeNativeQuery("DELETE FROM SPRING_TLE_ROUTE WHERE (ID="+route.getId()+")");
        List l = em.createNativeQuery(
                "SELECT id as ID, averageTimeMins as AVERAGETIMEMINS FROM SPRING_TLE_ROUTE WHERE (ID="+route.getId()+")", Route.class)
                .getResultList();
        assertTrue(l.size() == 0);
    }

    public void testNamedQuery(){
        Truck truck = new Truck("namedQuery");
        Route route = new Route();
        truck.setRoute(route);
        try {
            em.persist(truck);
            Query q = em.createNamedQuery("findTruckByDriverName");
            q.setParameter(1, "namedQuery");
            assertTrue(((Truck)q.getSingleResult()).getRoute().getId() == route.getId());
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
            assertTrue(((Truck)q.getSingleResult()).getId() == truck.getId());
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
            assertFalse("Exception when Address value holder retrieved", true);
        }      
        assertNotNull("Address class does not have '_persistence_route_vh' field", f);
    }

    public void testDataExceptionTranslation(){
        try {
            em.refresh(new Truck("detachedTruck"));
            assertFalse("No exception thrown with bad refresh", true);
        }catch (InvalidDataAccessApiUsageException idaaue) {
            //expected, so do nothing
        }catch (Exception e) {
            assertFalse("Wrong exception thrown with bad refresh: " + e, true);
        }
    }
   
}
