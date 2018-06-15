/*
 * Copyright (c) 2005, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2005, 2015 SAP. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     SAP - initial API and implementation

package org.eclipse.persistence.testing.tests.wdf.jpa1.simple;

import java.math.BigInteger;

import javax.persistence.EntityManager;

import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.models.wdf.jpa1.types.TemporalTypesFieldAccess;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.junit.Test;

public class TestPrimaryKeyTypes extends JPA1Base {

    private static final BigInteger BIG_INTEGER_PK = new BigInteger("1234567890123456789"); // issue id 8: was
                                                                                            // 12345678901234567890

    @Test
    public void testBigIntegerPk() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            TemporalTypesFieldAccess entity = new TemporalTypesFieldAccess(BIG_INTEGER_PK);
            env.beginTransaction(em);
            em.persist(entity);
            env.commitTransactionAndClear(em);

            env.beginTransaction(em);
            entity = em.find(TemporalTypesFieldAccess.class, BIG_INTEGER_PK);
            verify(entity != null, "entity is null");
            em.remove(entity);
            env.commitTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }
}
