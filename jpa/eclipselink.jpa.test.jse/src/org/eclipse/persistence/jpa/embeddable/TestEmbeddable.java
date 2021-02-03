/*******************************************************************************
 * Copyright (c) 2021 IBM Corporation. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     04/09/2021 - Will Dazey
 *       - 570702 : Using embeddable fields in query JOINs
 ******************************************************************************/
package org.eclipse.persistence.jpa.embeddable;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.eclipse.persistence.jpa.embeddable.model.SpecAddress;
import org.eclipse.persistence.jpa.embeddable.model.SpecContactInfo;
import org.eclipse.persistence.jpa.embeddable.model.SpecEmployee;
import org.eclipse.persistence.jpa.embeddable.model.SpecPhone;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.SQLListener;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestEmbeddable {

    @Emf(name = "SpecPersistenceUnit", createTables = DDLGen.DROP_CREATE, 
            classes = { SpecAddress.class, SpecContactInfo.class, SpecEmployee.class, SpecPhone.class })
    private EntityManagerFactory emf;

    @SQLListener(name = "SpecPersistenceUnit")
    List<String> _sql;

    @Test
    public void JPQLTests() {
        EntityManager em = emf.createEntityManager();
        try {
            // According to the JPA Spec, section 4.4.4, the following two queries are equivalent
            Query queryEmbed = em.createQuery("SELECT p.vendor FROM SpecEmployee e JOIN e.contactInfo.phones p WHERE e.contactInfo.primaryAddress.zipcode = ?1");
            queryEmbed.setParameter(1, "95051");
            queryEmbed.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT t0.VENDOR FROM SPECPHONE t0, SPECEMPLOYEE_SPECPHONE t2, SPECEMPLOYEE t1 WHERE ((t1.ZIPCODE = ?) AND ((t2.SpecEmployee_ID = t1.ID) AND (t0.ID = t2.phones_ID)))", _sql.remove(0));

            queryEmbed = em.createQuery("SELECT p.vendor FROM SpecEmployee e JOIN e.contactInfo c JOIN c.phones p WHERE e.contactInfo.primaryAddress.zipcode = ?1");
            queryEmbed.setParameter(1, "95052");
            queryEmbed.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT t0.VENDOR FROM SPECPHONE t0, SPECEMPLOYEE_SPECPHONE t2, SPECEMPLOYEE t1 WHERE ((t1.ZIPCODE = ?) AND ((t2.SpecEmployee_ID = t1.ID) AND (t0.ID = t2.phones_ID)))", _sql.remove(0));
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }
}
