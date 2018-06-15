/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.jpa.collection;

import java.util.HashMap;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.eclipse.persistence.jpa.collection.model.Level;
import org.eclipse.persistence.jpa.collection.model.MainEntity;
import org.eclipse.persistence.jpa.collection.model.Param;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestEmbeddableKey {

    @Emf(createTables = DDLGen.DROP_CREATE, classes = { MainEntity.class, Level.class, Param.class })
    private EntityManagerFactory emf;

    @Test
    public void testRemoveEmbeddableKeyFromCollection() {
        // creating entity
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Level l1 = new Level();
        l1.id = 1;
        l1.description = "level1";
        em.persist(l1);
        Level l2 = new Level();
        l2.id = 3;
        l2.description = "level1";
        em.persist(l2);

        MainEntity me = new MainEntity();
        me.id = 1;
        me.params = new HashMap<Param, String>();

        Param p11 = new Param();
        p11.level = l1;
        p11.name = "param11";
        me.params.put(p11, "value1");

        Param p12 = new Param();
        p12.level = l1;
        p12.name = "param12";
        me.params.put(p12, "value1");

        Param p21 = new Param();
        p21.level = l2;
        p21.name = "param21";
        me.params.put(p21, "value1");

        em.persist(me);
        em.getTransaction().commit();

        // try to read it
        em.getTransaction().begin();
        me = em.find(MainEntity.class, 1L);
        Assert.assertNotNull(me);
        Assert.assertEquals(3, me.params.size());
        Assert.assertEquals("value1", me.params.get(p11));

        em.remove(me);

        // bug #441498 - try to remove the entity
        em.getTransaction().commit();

        // verify removal
        me = em.find(MainEntity.class, 1L);
        Assert.assertNull(me);
    }

}
