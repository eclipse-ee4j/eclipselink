/*******************************************************************************
 * Copyright (c) 2016 IBM Corporation. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     08/24/2016 - Will Dazey
 *       - 500145 : Nested Embeddables Test
 ******************************************************************************/
package org.eclipse.persistence.jpa.embeddable;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.eclipse.persistence.jpa.embeddable.model.Address;
import org.eclipse.persistence.jpa.embeddable.model.Order;
import org.eclipse.persistence.jpa.embeddable.model.OrderPK;
import org.eclipse.persistence.jpa.embeddable.model.Zipcode;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestNestedEmbeddable {
    @Emf(createTables = DDLGen.DROP_CREATE, classes = { Order.class, OrderPK.class, Address.class, Zipcode.class })
        private EntityManagerFactory emf;
    
    @Test
    public void persistTest() {
        if (emf == null)
            return;
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Order o = new Order();
            
            String billingZip = "12345";
            String shippingZip ="54321";
            
            OrderPK opk = new OrderPK();
            opk.setBillingAddress(new Address(new Zipcode(billingZip)));
            opk.setShippingAddress(new Address(new Zipcode(shippingZip)));
            
            o.setId(opk);
            
            em.persist(o);
            em.getTransaction().commit();
            em.clear();
            
            Order foundOrder = em.find(Order.class, o.getId());
            
            Assert.assertNotNull(foundOrder);
            
            Object pk = emf.getPersistenceUnitUtil().getIdentifier(foundOrder);
            Assert.assertTrue(pk instanceof OrderPK);
            
            Assert.assertNotNull(((OrderPK)pk).getBillingAddress());
            Assert.assertNotNull(((OrderPK)pk).getShippingAddress());
            
            Assert.assertEquals(billingZip,((OrderPK)pk).getBillingAddress().getZipcode().getZip());
            Assert.assertEquals(shippingZip,((OrderPK)pk).getShippingAddress().getZipcode().getZip());
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }
}
