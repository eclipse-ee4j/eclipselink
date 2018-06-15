/*
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2016 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     08/24/2016 - Will Dazey
//       - 500145 : Nested Embeddables Test
//     10/19/2016 - Will Dazey
//       - 506168 : Nested Embeddables AttributeOverride Test
package org.eclipse.persistence.jpa.embeddable;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.jpa.embeddable.model.Address;
import org.eclipse.persistence.jpa.embeddable.model.DeepOrderPK;
import org.eclipse.persistence.jpa.embeddable.model.DeepOrder;
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
    @Emf(createTables = DDLGen.DROP_CREATE, classes = { DeepOrder.class, DeepOrderPK.class, Order.class, OrderPK.class, Address.class, Zipcode.class })
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

    @Test
    public void testDeeperEmbeddingMappings() {
        if (emf == null)
            return;
        EntityManager em = emf.createEntityManager();
        try {
            ClassDescriptor orderDescriptor = ((JpaEntityManager)em).getServerSession().getDescriptor(Order.class);
            ClassDescriptor orderpkDescriptor = orderDescriptor.getMappingForAttributeName("id").getReferenceDescriptor();
            ClassDescriptor addressDescriptor = orderpkDescriptor.getMappingForAttributeName("billingAddress").getReferenceDescriptor();
            ClassDescriptor zipcodeDescriptor = addressDescriptor.getMappingForAttributeName("zipcode").getReferenceDescriptor();
            Assert.assertEquals("BILL_ZIP", zipcodeDescriptor.getFields().get(0).getName());

            ClassDescriptor deepOrderDescriptor = ((JpaEntityManager)em).getServerSession().getDescriptor(DeepOrder.class);
            ClassDescriptor deepOrderpkDescriptor = deepOrderDescriptor.getMappingForAttributeName("id").getReferenceDescriptor();
            ClassDescriptor orderpkDescriptor2 = deepOrderpkDescriptor.getMappingForAttributeName("orderpk").getReferenceDescriptor();
            ClassDescriptor addressDescriptor2 = orderpkDescriptor2.getMappingForAttributeName("billingAddress").getReferenceDescriptor();
            ClassDescriptor zipcodeDescriptor2 = addressDescriptor2.getMappingForAttributeName("zipcode").getReferenceDescriptor();
            Assert.assertEquals("deepOverride", zipcodeDescriptor2.getFields().get(0).getName());

        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }
}
