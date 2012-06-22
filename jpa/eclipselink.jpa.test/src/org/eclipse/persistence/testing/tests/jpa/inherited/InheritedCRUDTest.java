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
package org.eclipse.persistence.testing.tests.jpa.inherited;

import java.util.Date;
import java.util.Calendar;
import org.eclipse.persistence.testing.models.jpa.inherited.*;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.tests.jpa.EntityContainerTestBase;

/**
 *
 * @author Guy Pelletier
 */
@SuppressWarnings("deprecation")
public class InheritedCRUDTest extends EntityContainerTestBase {
    protected boolean m_reset = false;    // reset gets called twice on error
    protected Exception m_exception;
    
    private BeerConsumer beerConsumer1, beerConsumer2;
    private Alpine alpine1, alpine2, alpine3;
    private Canadian canadian1, canadian2;
        
    public InheritedCRUDTest() {
        setDescription("Tests the creation of an inheritance subclass that uses multiple tables with a different pk column than its parent");
    }
    
    public void setup () {
        super.setup();
        m_reset = true;
        m_exception = null;
        ((EntityManagerImpl)getEntityManager()).getActiveSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }
    
	public void test() throws Exception {
        try {
            beginTransaction();
            
            beerConsumer1 = new BeerConsumer();
            beerConsumer1.setName("Guy Pelletier");
            getEntityManager().persist(beerConsumer1);
    
            beerConsumer2 = new BeerConsumer();
            beerConsumer2.setName("Tom Ware");
            getEntityManager().persist(beerConsumer2);
            SerialNumber serialNumber1 = new SerialNumber();
            getEntityManager().persist(serialNumber1);
            alpine1 = new Alpine(serialNumber1);
            alpine1.setBestBeforeDate(new Date(2005, 8, 17));
            alpine1.setAlcoholContent(5.0);
            alpine1.setBeerConsumer(beerConsumer2);
            getEntityManager().persist(alpine1);

            SerialNumber serialNumber2 = new SerialNumber();
            getEntityManager().persist(serialNumber2);
            alpine2 = new Alpine(serialNumber2);
            alpine2.setBestBeforeDate(new Date(2005, 8, 21));
            alpine2.setAlcoholContent(5.0);
            alpine2.setBeerConsumer(beerConsumer2);
            getEntityManager().persist(alpine2);

            SerialNumber serialNumber3 = new SerialNumber();
            getEntityManager().persist(serialNumber3);
            alpine3 = new Alpine(serialNumber3);
            alpine3.setBestBeforeDate(new Date(2005, 8, 22));
            alpine3.setAlcoholContent(5.0);
            alpine3.setBeerConsumer(beerConsumer2);
            getEntityManager().persist(alpine3);

            canadian1 = new Canadian();
            canadian1.setBornOnDate(Calendar.getInstance().getTime());
            canadian1.setAlcoholContent(5.0);
            canadian1.setBeerConsumer(beerConsumer1);
            getEntityManager().persist(canadian1);

            canadian2 = new Canadian();
            canadian2.setBornOnDate(Calendar.getInstance().getTime());
            canadian2.setAlcoholContent(5.0);
            canadian2.setBeerConsumer(beerConsumer1);
            getEntityManager().persist(canadian2);
            
            commitTransaction();    
        } catch (Exception e) {
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
            throw new TestErrorException("Exception was thrown when creating a bus: " + m_exception.getMessage());
        }
    }
}
