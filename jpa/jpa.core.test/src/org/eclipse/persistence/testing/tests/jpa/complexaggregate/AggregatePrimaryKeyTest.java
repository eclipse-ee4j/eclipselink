/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.complexaggregate;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.testing.models.jpa.complexaggregate.*;
import org.eclipse.persistence.testing.tests.jpa.EntityContainerTestBase;
import org.eclipse.persistence.testing.framework.TestErrorException;

/**
 * BUG 4219210 - EJB30: EMBEDABLE IDS ARE NOT SHARABLE
 *
 * @author Guy Pelletier
 * @date April 8, 2005
 * @version 1.0
 */
public class AggregatePrimaryKeyTest extends EntityContainerTestBase {
    protected boolean m_reset = false;    // reset gets called twice on error
    protected Exception m_exception;
        
    public AggregatePrimaryKeyTest() {
        setDescription("Tests an aggregate that is also the primary key.");
    }
    
    public void setup () {
        super.setup();
        m_reset = true;
        m_exception = null;
        ((EntityManagerImpl)getEntityManager()).getActiveSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }
    
    public void test() throws Exception {
        try {
            Name name = new Name();
            name.setFirstName("Tom");
            name.setLastName("Ware");
            
            CountryDweller countryDweller = new CountryDweller();
            countryDweller.setAge(30);
            countryDweller.setName(name);
            
            CitySlicker citySlicker = new CitySlicker();
            citySlicker.setAge(53);
            citySlicker.setName(name);
        
            beginTransaction();
            getEntityManager().persist(countryDweller);
            getEntityManager().persist(citySlicker);
            
            commitTransaction();
        
            // Clear the cache.
            ((EntityManagerImpl)getEntityManager()).getActiveSession().getIdentityMapAccessor().initializeAllIdentityMaps();            
            
            // Now read them back in and delete them.
            beginTransaction();
            
            CitySlicker cs = (CitySlicker) getEntityManager().find(CitySlicker.class, name);
            CountryDweller cd = (CountryDweller) getEntityManager().merge(countryDweller);
            
            getEntityManager().remove(cs);
            getEntityManager().remove(cd);
            
            commitTransaction();
        } catch (RuntimeException e) {
            rollbackTransaction();
            m_exception = e;
        }
    }
    
    public void reset () {
        if (m_reset) {
            m_reset = false;
        }
    }
    
    public void verify() {
        if (m_exception != null) {
            throw new TestErrorException("Something went terribly wrong when creating a CountryDweller and a CitySlicker with the same name: " + m_exception.getMessage());
        }
    }
}
