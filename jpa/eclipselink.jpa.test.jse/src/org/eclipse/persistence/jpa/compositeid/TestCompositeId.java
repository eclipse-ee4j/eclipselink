/********************************************************************************
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 1998, 2018 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 * Contributors:
 *     07/09/2018-2.6 Jody Grassel
 *       - 536853: MapsID processing sets up to fail validation
 ******************************************************************************/

package org.eclipse.persistence.jpa.compositeid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.eclipse.persistence.jpa.compositeid.model.BTI;
import org.eclipse.persistence.jpa.compositeid.model.ClientId;
import org.eclipse.persistence.jpa.compositeid.model.CompB;
import org.eclipse.persistence.jpa.compositeid.model.CompBId;
import org.eclipse.persistence.jpa.compositeid.model.Environment;
import org.eclipse.persistence.jpa.compositeid.model.RN;
import org.eclipse.persistence.jpa.compositeid.model.CompA;
import org.eclipse.persistence.jpa.compositeid.model.CompAId;
import org.eclipse.persistence.jpa.compositeid.model.CompAIdentifier;
import org.eclipse.persistence.jpa.compositeid.model.CompC;
import org.eclipse.persistence.jpa.compositeid.model.CompCId;
import org.eclipse.persistence.jpa.compositeid.model.UserId;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestCompositeId {
    @Emf(createTables = DDLGen.DROP_CREATE, classes = { CompB.class, CompA.class, CompC.class },
            properties = { @Property(name="eclipselink.logging.level", value="FINE"),
                    @Property(name="eclipselink.logging.parameters", value="true")})
    private EntityManagerFactory emf;

    @Test
    public void testCompositeId() throws Exception {
        final EntityManager em = emf.createEntityManager();
        final EntityTransaction transaction = em.getTransaction();

        try {
            System.out.println("Beginning tx");
            transaction.begin();

            CompA compA1 = new CompA();
            CompAId compAId1 = new CompAId();
            compAId1.setEnvironment(new Environment());
            compAId1.getEnvironment().setValue("1");
            compAId1.setIdentifier(new CompAIdentifier());
            compAId1.getIdentifier().setValue(1);
            compA1.setId(compAId1);
            em.persist(compA1);

            CompB compB = new CompB();
            CompBId compBId = new CompBId();
            compBId.setClientId(new ClientId());
            compBId.getClientId().setValue("1");
            compBId.setRN(new RN());
            compBId.getRN().setValue(1);

            compB.setId(compBId);
            compB.setCompA(compA1); // Should set croId.role via maps id
            em.persist(compB);

            transaction.commit();
            em.clear();

            transaction.begin();

            CompB compBFind = (CompB) em.
                    createQuery("SELECT e FROM CompB e WHERE e.id.clientId.value = 1").
                    getSingleResult();
            assertNotNull(compBFind);
            assertNotSame(compB, compBFind);
            assertNotNull(compBFind.getId());
            assertNotNull(compBFind.getId().getCompAId());
            assertEquals(compBFind.getId().getCompAId().getEnvironment().getValue(), "1");
            assertEquals(compBFind.getId().getCompAId().getIdentifier().getValue(), new Integer(1));
            assertNotNull(compBFind.getId().getClientId());
            assertEquals(compBFind.getId().getClientId().getValue(), "1");
            assertNotNull(compBFind.getId().getRN());
            assertEquals(compBFind.getId().getRN().getValue(), new Integer(1));

            CompC compC = new CompC();
            CompCId compCId = new CompCId();
            compCId.setBTI(new BTI());
            compCId.getBTI().setValue(3);
            compCId.setUserId(new UserId());
            compCId.getUserId().setValue("4");
            compC.setId(compCId);
            compC.setCompB(compBFind);
            em.persist(compC);

            transaction.commit();
            em.clear();

            CompC compCFind = (CompC) em.
                    createQuery("SELECT e FROM CompC e WHERE e.id.userId.value = 4").
                    getSingleResult();
            assertNotNull(compCFind);
            assertNotSame(compC, compCFind);
            assertNotNull(compC.getId());
            assertNotNull(compC.getId().getBTI());
            assertEquals(compC.getId().getBTI().getValue(), new Integer(3));
            assertNotNull(compC.getId().getCompBIdId());
            assertEquals(compC.getId().getUserId().getValue(), "4");
            assertNotNull(compC.getId().getUserId());

            CompBId compBU2 = compC.getId().getCompBIdId();
            assertNotNull(compBU2);
            assertNotNull(compBU2.getCompAId());
            assertEquals(compBU2.getCompAId().getEnvironment().getValue(), "1");
            assertEquals(compBU2.getCompAId().getIdentifier().getValue(), new Integer(1));
            assertNotNull(compBU2.getClientId());
            assertEquals(compBU2.getClientId().getValue(), "1");
            assertNotNull(compBU2.getRN());
            assertEquals(compBU2.getRN().getValue(), new Integer(1));
        } finally {
            if (em != null) {
                if (transaction.isActive()) 
                    transaction.rollback();
                em.close();
            }
        }
    }
}
