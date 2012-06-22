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
package org.eclipse.persistence.testing.tests.jpa.complexaggregate;

import java.util.Collection;

import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.testing.models.jpa.complexaggregate.*;
import org.eclipse.persistence.testing.tests.jpa.EntityContainerTestBase;

/**
 * @author Guy Pelletier
 * @date January 26, 2006
 * @version 1.0
 */
public class AggregatePrimaryKeyOrderByTest extends EntityContainerTestBase {
    protected boolean citySlickersAreOrdered;
    protected boolean countryDwellersAreOrdered;
        
    public AggregatePrimaryKeyOrderByTest() {
        setDescription("Tests order by using an embedded id.");
    }
    
    public void setup () {
        super.setup();        
        ((EntityManagerImpl)getEntityManager()).getActiveSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }
    
    public void test() throws Exception {
        try {
            beginTransaction();
            
            World world = new World();
            
            // First pair
            Name name1 = new Name();
            name1.setFirstName("Guy");
            name1.setLastName("Pelletier");
            
            CountryDweller countryDweller1 = new CountryDweller();
            countryDweller1.setAge(30);
            countryDweller1.setName(name1);
            countryDweller1.setGender("Male");
            getEntityManager().persist(countryDweller1);
            world.addCountryDweller(countryDweller1);
            
            CitySlicker citySlicker1 = new CitySlicker();
            citySlicker1.setAge(53);
            // Bug 300696 - Invalid tests: sharing embedded objects is not allowed 
            // Changed the test to use clone instead of sharing the same Name between the two Entities.
            Name name1Clone = (Name)name1.clone();
            citySlicker1.setName(name1Clone);
            citySlicker1.setGender("Male");
            getEntityManager().persist(citySlicker1);
            world.addCitySlicker(citySlicker1);
            
            // Second pair
            Name name2 = new Name();
            name2.setFirstName("Steve");
            name2.setLastName("Harp");
            
            CountryDweller countryDweller2 = new CountryDweller();
            countryDweller2.setAge(28);
            countryDweller2.setName(name2);
            countryDweller2.setGender("Male");
            getEntityManager().persist(countryDweller2);
            world.addCountryDweller(countryDweller2);
            
            CitySlicker citySlicker2 = new CitySlicker();
            citySlicker2.setAge(41);
            // Bug 300696 - Invalid tests: sharing embedded objects is not allowed 
            // Changed the test to use clone instead of sharing the same Name between the two Entities.
            Name name2Clone = (Name)name2.clone();
            citySlicker2.setName(name2Clone);
            citySlicker2.setGender("Male");
            getEntityManager().persist(citySlicker2);
            world.addCitySlicker(citySlicker2);
            
            // Third pair
            Name name3 = new Name();
            name3.setFirstName("Jesse");
            name3.setLastName("Petoncle");
            
            CountryDweller countryDweller3 = new CountryDweller();
            countryDweller3.setAge(48);
            countryDweller3.setName(name3);
            countryDweller3.setGender("Male");
            getEntityManager().persist(countryDweller3);
            world.addCountryDweller(countryDweller3);
            
            CitySlicker citySlicker3 = new CitySlicker();
            citySlicker3.setAge(76);
            // Bug 300696 - Invalid tests: sharing embedded objects is not allowed 
            // Changed the test to use clone instead of sharing the same Name between the two Entities.
            Name name3Clone = (Name)name3.clone();
            citySlicker3.setName(name3Clone);
            citySlicker3.setGender("Female");
            getEntityManager().persist(citySlicker3);
            world.addCitySlicker(citySlicker3);

            getEntityManager().persist(world);
            
            commitTransaction();
            
            // Clear the cache.
            ((EntityManagerImpl)getEntityManager()).getActiveSession().getIdentityMapAccessor().initializeAllIdentityMaps();            
            
            // Now read them back in and delete them.
            beginTransaction();
            
            World w = getEntityManager().find(World.class, world.getId());
            
            Collection css = w.getCitySlickers();
            css.toString();
            Collection cds = w.getCountryDwellers();
            cds.toString();
        
            // Check the ordering
            // JBS - Ordering check removed as order is random based on class method order which is not consistent in Java.
            //citySlickersAreOrdered = ((CitySlicker) css.elementAt(0)).getName().getFirstName().equals("Guy") && ((CitySlicker) css.elementAt(1)).getName().getFirstName().equals("Jesse") && ((CitySlicker) css.elementAt(2)).getName().getFirstName().equals("Steve");
            //countryDwellersAreOrdered = ((CountryDweller) cds.elementAt(0)).getAge() == 28 && ((CountryDweller) cds.elementAt(1)).getAge() == 30 && ((CountryDweller) cds.elementAt(2)).getAge() == 48;
        
            // Make sure we delete them    
            // Note that in Identity case name1Clone may no longer have the same id as name1
            CitySlicker cs1 = getEntityManager().find(CitySlicker.class, name1Clone);
            getEntityManager().remove(cs1);
            CitySlicker cs2 = getEntityManager().find(CitySlicker.class, name2Clone);
            getEntityManager().remove(cs2);
            CitySlicker cs3 = getEntityManager().find(CitySlicker.class, name3Clone);
            getEntityManager().remove(cs3);
            
            CountryDweller cd1 = getEntityManager().merge(countryDweller1);
            getEntityManager().remove(cd1);
            CountryDweller cd2 = getEntityManager().merge(countryDweller2);
            getEntityManager().remove(cd2);
            CountryDweller cd3 = getEntityManager().merge(countryDweller3);
            getEntityManager().remove(cd3);
            
            commitTransaction();
        
        } catch (RuntimeException e) {
            rollbackTransaction();
            throw e;
        }
    }
    
    public void verify() {        
        // JBS - Ordering check removed as order is random based on class method order which is not consistent in Java.
        //if (!citySlickersAreOrdered) {
        //    throw new TestErrorException("The city slickers were not ordered properly.");
        //}        
        //if (!countryDwellersAreOrdered) {
        //    throw new TestErrorException("The country dwellers were not ordered properly.");
        //}
    }
}
