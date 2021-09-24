/*******************************************************************************
 * Copyright (c) 2021 Oracle and/or its affiliates, IBM Corporation. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     09/24/2021 - Will Dazey
 *       - 412391 : Add support for weaving hidden variables
 ******************************************************************************/
package org.eclipse.persistence.jpa.test.weave;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.jpa.test.weave.model.WeavedAbstractMS;
import org.eclipse.persistence.jpa.test.weave.model.WeavedEntityA;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestWeaving {

    @Emf(createTables = DDLGen.DROP_CREATE, 
            classes = { WeavedAbstractMS.class, WeavedEntityA.class },
            properties = { @Property(name = "eclipselink.cache.shared.default", value = "false") })
    private EntityManagerFactory emf;

    private static boolean POPULATED = false;

    @Test
    public void testHiddenAttribute() throws Exception {
        if (emf == null)
            return;

        if(!POPULATED) 
            populate();

        EntityManager em = emf.createEntityManager();
        try {
            final WeavedEntityA e1 = em.find(WeavedEntityA.class, 123l);

            Assert.assertNotNull(e1);
            Assert.assertNotNull(e1.getHiddenAttribute());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (em != null) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                em.close();
            }
        }
    }

    private void populate() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            WeavedEntityA tbl1 = new WeavedEntityA();
            tbl1.setId(123l);
            tbl1.setHiddenAttribute((short) 1);
            em.persist(tbl1);

            em.getTransaction().commit();

            POPULATED = true;
        } finally {
            if(em.isOpen()) {
                em.clear();
                em.close();
            }
        }
    }
}
