/*
 * Copyright (c) 2024 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2021 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.jpa.embeddable;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;

import org.eclipse.persistence.jpa.embeddable.model.ElementCollectionEmbeddableTemporal;
import org.eclipse.persistence.jpa.embeddable.model.ElementCollectionEntity;
import org.eclipse.persistence.jpa.embeddable.model.SpecAddress;
import org.eclipse.persistence.jpa.embeddable.model.SpecContactInfo;
import org.eclipse.persistence.jpa.embeddable.model.SpecEmployee;
import org.eclipse.persistence.jpa.embeddable.model.SpecPhone;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.jpa.test.framework.SQLListener;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestCollectionTableEmbeddable {
    @Emf(createTables = DDLGen.DROP_CREATE, 
            classes = { ElementCollectionEntity.class, ElementCollectionEmbeddableTemporal.class },
            properties = { 
                    @Property(name = "eclipselink.cache.shared.default", value = "false")})
    private EntityManagerFactory emf;

    @Emf(name = "SpecPersistenceUnit", createTables = DDLGen.DROP_CREATE, 
            classes = { SpecAddress.class, SpecContactInfo.class, SpecEmployee.class, SpecPhone.class })
    private EntityManagerFactory emf2;

    @SQLListener(name = "SpecPersistenceUnit")
    List<String> _sql;

    @Test
    public void mergeTest() {
        if (emf == null)
            return;
        EntityManager em = emf.createEntityManager();
        try {
            ElementCollectionEntity newEntity = new ElementCollectionEntity();
            newEntity.setId(99);

            Map<Date, ElementCollectionEmbeddableTemporal> map = new HashMap<>();
            map.put(new Date(1), new ElementCollectionEmbeddableTemporal(new Date(System.currentTimeMillis() - 200000000)));
            newEntity.setMapKeyTemporalValueEmbed(new HashMap<>(map));

            em.getTransaction().begin();
            em.merge(newEntity);
            em.getTransaction().commit();

            em.clear();

            em.getTransaction().begin();
            em.merge(newEntity);
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    @Test
    public void JPQLAggregateCollectionTests() {
        EntityManager em = emf2.createEntityManager();
        try {
            /*
             * ContactInfo.previousAddresses should end up being an AggregateCollectionMapping
             */
            Query queryEmbed = em.createQuery("SELECT p.city FROM SpecEmployee e JOIN e.contactInfo.previousAddresses p WHERE e.contactInfo.primaryAddress.zipcode = ?1");
            queryEmbed.setParameter(1, "95054");
            queryEmbed.getResultList();
            Assert.assertEquals(1, _sql.size());
            Assert.assertEquals("SELECT t0.CITY FROM SPECEMPLOYEE t1, PREV_ADDRESSES t0 WHERE ((t1.ZIPCODE = ?) AND (t0.SpecEmployee_ID = t1.ID))", _sql.remove(0));
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }
}
