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
 *     01/15/2021 - Will Dazey
 *       - 570378 : NullPointerException from Embedded Temporal
 ******************************************************************************/
package org.eclipse.persistence.jpa.embeddable;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.eclipse.persistence.jpa.embeddable.model.ElementCollectionEmbeddableTemporal;
import org.eclipse.persistence.jpa.embeddable.model.ElementCollectionEntity;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestCollectionTableEmbeddable {
    @Emf(createTables = DDLGen.DROP_CREATE, 
            classes = { ElementCollectionEntity.class, ElementCollectionEmbeddableTemporal.class },
            properties = { 
                    @Property(name = "eclipselink.cache.shared.default", value = "false")})
    private EntityManagerFactory emf;

    @Test
    public void mergeTest() {
        if (emf == null)
            return;
        EntityManager em = emf.createEntityManager();
        try {
            ElementCollectionEntity newEntity = new ElementCollectionEntity();
            newEntity.setId(99);

            Map<Date, ElementCollectionEmbeddableTemporal> map = new HashMap<Date, ElementCollectionEmbeddableTemporal>();
            map.put(new Date(1), new ElementCollectionEmbeddableTemporal(new Date(System.currentTimeMillis() - 200000000)));
            newEntity.setMapKeyTemporalValueEmbed(new HashMap<Date, ElementCollectionEmbeddableTemporal>(map));

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
}
