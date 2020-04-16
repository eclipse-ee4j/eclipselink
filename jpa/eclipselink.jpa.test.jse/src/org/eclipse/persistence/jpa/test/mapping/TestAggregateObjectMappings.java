/*
 * Copyright (c) 2019, 2020 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019, 2020 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     09/12/2019 - Will Dazey
//       - 471144: Add support for AggregateObjectMappings to eclipselink.cursor impl
package org.eclipse.persistence.jpa.test.mapping;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.mapping.model.BaseChild;
import org.eclipse.persistence.jpa.test.mapping.model.BaseEmbeddable;
import org.eclipse.persistence.jpa.test.mapping.model.BaseParent;
import org.eclipse.persistence.jpa.test.mapping.model.SimpleMappingEmbeddable;
import org.eclipse.persistence.jpa.test.mapping.model.SimpleMappingEntity;
import org.eclipse.persistence.queries.Cursor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestAggregateObjectMappings {
    @Emf(name = "cursorEMF" , createTables = DDLGen.DROP_CREATE,
            classes = { SimpleMappingEntity.class, SimpleMappingEmbeddable.class } )
    private EntityManagerFactory emf;

    @Emf(name = "joinEMF" , createTables = DDLGen.DROP_CREATE,
            classes = { BaseParent.class, BaseChild.class, BaseEmbeddable.class } )
    private EntityManagerFactory emfTwo;

    @Test
    public void testCursorWithAggregateObjectMapping() {

        EntityManager em = emf.createEntityManager();
        try {
            //populate the table
            em.getTransaction().begin();
            String embeddedString1 = "test1";
            String embeddedString2 = "test2";
            SimpleMappingEmbeddable emb = new SimpleMappingEmbeddable(embeddedString1, embeddedString2);
            int mappingField1 = 9;
            SimpleMappingEntity ent = new SimpleMappingEntity(mappingField1, emb);
            em.persist(ent);
            em.getTransaction().commit();

            Query query = em.createQuery("SELECT e.mappingField1, e.aggregateObjectMapping FROM SimpleMappingEntity e");
            query.setHint(QueryHints.CURSOR, HintValues.TRUE);

            Cursor cursor = (Cursor) query.getSingleResult();

            Assert.assertTrue("Cursor was empty", cursor.hasNext());
            Object[] results = (Object[]) cursor.next();
            Assert.assertArrayEquals(new Object[] {mappingField1, emb}, results);

            //cleanup test
            em.getTransaction().begin();
            em.remove(ent);
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Test with @JoinColumn that uses a column name that matches the referenced column name
     */
    @Test
    public void testJoinColumnWithSameDuplicateName() {

        EntityManager em = emfTwo.createEntityManager();
        try {
            Query query = em.createQuery("SELECT c.parentRef FROM BaseChild c");
            query.getResultList();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }
    }
}
