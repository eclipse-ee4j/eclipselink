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
 *     08/27/2008-1.1 Guy Pelletier 
 *       - 211329: Add sequencing on non-id attribute(s) support to the EclipseLink-ORM.XML Schema
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.complexaggregate;

import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.testing.models.jpa.complexaggregate.*;
import org.eclipse.persistence.testing.tests.jpa.EntityContainerTestBase;

/**
 * BUG 4219210 - EJB30: EMBEDABLE IDS ARE NOT SHARABLE
 *
 * @author Guy Pelletier
 * @date April 8, 2005
 * @version 1.0
 */
public class AggregatePrimaryKeyTest extends EntityContainerTestBase {
    public AggregatePrimaryKeyTest() {
        setDescription("Tests an aggregate that is also the primary key.");
    }
    
    public void setup () {
        super.setup();
        ((EntityManagerImpl)getEntityManager()).getActiveSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }
    
    public void test() throws Exception {
        try {
            Name name1 = new Name();
            name1.setFirstName("Tom");
            name1.setLastName("Ware");
            
            CountryDweller countryDweller = new CountryDweller();
            countryDweller.setAge(30);
            countryDweller.setName(name1);
            
            CitySlicker citySlicker = new CitySlicker();
            citySlicker.setAge(53);
            // Bug 300696 - Invalid tests: sharing embedded objects is not allowed 
            // Changed the test to use clone instead of sharing the same Name between the two Entities.
            Name name1Clone = (Name)name1.clone();
            citySlicker.setName(name1Clone);
            
            Name name2 = new Name();
            name2.setFirstName("Guy");
            name2.setLastName("Pelletier");
            
            CountryDweller countryDweller2 = new CountryDweller();
            countryDweller2.setAge(65);
            countryDweller2.setName(name2);
        
            beginTransaction();
            getEntityManager().persist(countryDweller);
            getEntityManager().persist(countryDweller2);
            getEntityManager().persist(citySlicker);
            commitTransaction();
        
            // Clear the cache.
            ((EntityManagerImpl)getEntityManager()).getActiveSession().getIdentityMapAccessor().initializeAllIdentityMaps();            
            
            // Now read them back in and delete them.
            beginTransaction();
            
            // Note that in Identity case name1Clone may no longer have the same id as name1
            CitySlicker cs = getEntityManager().find(CitySlicker.class, name1Clone);
            CountryDweller cd = getEntityManager().merge(countryDweller);
            CountryDweller cd2 = getEntityManager().merge(countryDweller2);
            
            getEntityManager().remove(cs);
            getEntityManager().remove(cd);
            getEntityManager().remove(cd2);
            
            commitTransaction();
        } catch (RuntimeException e) {
            rollbackTransaction();
            throw e;
        }
    }    
}
