/*
 * Copyright (c) 2014, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     11/17/2014-2.6.0 Rick Curtis
//       - 445546: Test NPE in ConversionManager.
package org.eclipse.persistence.jpa.test.basic;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

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
