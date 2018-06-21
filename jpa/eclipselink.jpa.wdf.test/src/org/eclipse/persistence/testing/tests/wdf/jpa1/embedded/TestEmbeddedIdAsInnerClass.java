/*
 * Copyright (c) 2005, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2005, 2015 SAP. All rights reserved.
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
//     SAP - initial API and implementation

package org.eclipse.persistence.testing.tests.wdf.jpa1.embedded;

import static org.junit.Assert.assertEquals;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Trailer;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.junit.Assert;
import org.junit.Test;

public class TestEmbeddedIdAsInnerClass extends JPA1Base {

    @Test
    public void testInsert() {
        final EntityManager em = getEnvironment().getEntityManager();
        try {
            Trailer trailer12 = new Trailer(1, 2, 12);
            Trailer trailer13 = new Trailer(1, 3, 13);
            Trailer trailer23 = new Trailer(2, 3, 23);
            getEnvironment().beginTransaction(em);
            em.persist(trailer12);
            em.persist(trailer13);
            em.persist(trailer23);
            getEnvironment().commitTransactionAndClear(em);

            Query query = em.createQuery("select t from Trailer t");
            assertEquals(query.getResultList().size(), 3);

            query = em.createQuery("select t from Trailer t where t.pk.low = ?1 and t.pk.high = ?2");
            query.setParameter(1, Integer.valueOf(new Trailer.PK(1, 2).getLow()));
            query.setParameter(2, Integer.valueOf(new Trailer.PK(1, 2).getHigh()));
            final Trailer result = (Trailer) query.getSingleResult();
            Assert.assertEquals("wrong load", trailer12.getLoad(), result.getLoad(), 0.0);
        } finally {
            closeEntityManager(em);
        }

    }

}
