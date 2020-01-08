/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2020 IBM Corporation. All rights reserved.
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
//     01/06/2020 - Will Dazey
//       - 347987: Fix Attribute Override for Complex Embeddables
package org.eclipse.persistence.jpa.test.mapping;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.mapping.model.OverrideEmbeddableA;
import org.eclipse.persistence.jpa.test.mapping.model.OverrideEmbeddableIdA;
import org.eclipse.persistence.jpa.test.mapping.model.OverrideEntityA;
import org.eclipse.persistence.jpa.test.mapping.model.OverrideNestedEmbeddableA;
import org.eclipse.persistence.jpa.test.mapping.model.OverrideNestedEmbeddableIdA;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestAttributeOverride {

    @Emf(createTables = DDLGen.DROP_CREATE,
            classes = { OverrideEntityA.class, OverrideEmbeddableA.class, OverrideEmbeddableIdA.class, OverrideNestedEmbeddableA.class, OverrideNestedEmbeddableIdA.class} )
    private EntityManagerFactory emf;

    @Before
    public void setup() {
        //Populate the table
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            //use INSERT query so that the value isn't cached for the find later
            em.createNativeQuery("INSERT INTO OVERRIDE_ENTITY_A (VALUE, NESTED_VALUE, OVERRIDE_VALUE, OVERRIDE_NESTED_VALUE) VALUES (1, 1, 1, 1)").executeUpdate();
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

    @After
    public void tearDown() {
        //Clean out the table
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createNativeQuery("DELETE FROM OVERRIDE_ENTITY_A").executeUpdate();
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

    @Test
    public void testOverrideColumnAggregateObjectMapping() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            OverrideEntityA t1 = em.find(OverrideEntityA.class, new OverrideEmbeddableIdA(1, new OverrideNestedEmbeddableIdA(1)));
            t1.setId2(new OverrideEmbeddableA(2, new OverrideNestedEmbeddableA(2)));
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
}
