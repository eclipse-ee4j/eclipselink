/*******************************************************************************
 * Copyright (c) 2014 IBM Corporation. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     11/17/2014-2.6.0 Rick Curtis
 *       - 445546: Test NPE in ConversionManager.
 ******************************************************************************/
package org.eclipse.persistence.jpa.test.basic;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.eclipse.persistence.jpa.test.basic.model.ByteArrayEntity;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestNullByteArrayEntry {
    @Emf(classes = { ByteArrayEntity.class })
    private EntityManagerFactory emf;

    @Test
    public void testNullInByteArray() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            ByteArrayEntity e = new ByteArrayEntity();
            em.persist(e);
            em.flush();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }

    }
}
