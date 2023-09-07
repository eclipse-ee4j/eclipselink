/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
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
//     07/30/2020 - Will Dazey
//       - 564260: ElementCollection lowercase AttributeOverride is ignored
package org.eclipse.persistence.jpa.test.mapping;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.jpa.test.mapping.model.OverrideEmbeddableA;
import org.eclipse.persistence.jpa.test.mapping.model.OverrideEmbeddableB;
import org.eclipse.persistence.jpa.test.mapping.model.OverrideEmbeddableIdA;
import org.eclipse.persistence.jpa.test.mapping.model.OverrideEntityA;
import org.eclipse.persistence.jpa.test.mapping.model.OverrideEntityB;
import org.eclipse.persistence.jpa.test.mapping.model.OverrideNestedEmbeddableA;
import org.eclipse.persistence.jpa.test.mapping.model.OverrideNestedEmbeddableB;
import org.eclipse.persistence.jpa.test.mapping.model.OverrideNestedEmbeddableIdA;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestAttributeOverride {

    @Emf(createTables = DDLGen.DROP_CREATE,
            classes = { OverrideEntityA.class, OverrideEmbeddableA.class, OverrideEmbeddableIdA.class, 
                    OverrideNestedEmbeddableA.class, OverrideNestedEmbeddableIdA.class, 
                    OverrideEntityB.class, OverrideEmbeddableB.class, OverrideNestedEmbeddableB.class},
            properties = { @Property(name="eclipselink.logging.level", value="FINE"),
                    @Property(name="eclipselink.logging.parameters", value="true")} )
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
            t1.setEmbeddedField1(new OverrideEmbeddableA(2, 3, new OverrideNestedEmbeddableA(2)));
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

    /*
     * This test is to validate that the Collection table was created with the correct attribute
     * override column name. 
     */
    @Test
    public void testOverrideLowerCaseElementCollectionObjectMapping() {
        EntityManager em = emf.createEntityManager();
        try {
            // Validate that the tables were generated with the correct column names
            Query query = em.createNativeQuery("select t0.b_id from override_entity_b t0");
            query.getResultList();

            // CREATE TABLE ct_override_entity_b (ct_b_override_value INTEGER, value2 INTEGER, ct_b_override_nested_value INTEGER, nested_value2 INTEGER, entity_b_ct_entity_b INTEGER)
            query = em.createNativeQuery("select t0.ct_b_override_value, t0.value2, t0.ct_b_override_nested_value, t0.entity_b_ct_entity_b, t0.nested_value2 from ct_override_entity_b t0");
            query.getResultList();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }

        em = emf.createEntityManager();
        try {
            Integer id = Integer.valueOf(41);

            OverrideEmbeddableB emb1 = new OverrideEmbeddableB(43, 44, new OverrideNestedEmbeddableB(45, 46));
            OverrideEmbeddableB emb2 = new OverrideEmbeddableB(47, 48, new OverrideNestedEmbeddableB(49, 50));
            OverrideEmbeddableB emb3 = new OverrideEmbeddableB(51, 52, new OverrideNestedEmbeddableB(53, 54));
            Set<OverrideEmbeddableB> set = new HashSet<OverrideEmbeddableB>(Arrays.asList(emb1, emb2, emb3));

            OverrideEntityB ent = new OverrideEntityB(id, set);

            em.getTransaction().begin();
            em.persist(ent);
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
