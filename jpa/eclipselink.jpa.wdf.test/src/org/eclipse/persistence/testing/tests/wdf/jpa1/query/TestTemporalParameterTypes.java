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

package org.eclipse.persistence.testing.tests.wdf.jpa1.query;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.framework.wdf.ToBeInvestigated;
import org.eclipse.persistence.testing.models.wdf.jpa1.types.TemporalTypesFieldAccess;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestTemporalParameterTypes extends JPA1Base {

    private static final TemporalTypesFieldAccess DATA = new TemporalTypesFieldAccess(BigInteger.ONE);
    static {
        DATA.fill();
    }
    private static final Date DATE = DATA.getUtilDateAsDate();
    private static final Date TIME = DATA.getUtilDateAsTime();

    @Before
    public void testInsert() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            TemporalTypesFieldAccess obj = new TemporalTypesFieldAccess(BigInteger.ONE);
            obj.fill();
            env.beginTransaction(em);
            em.persist(obj);
            env.commitTransactionAndClear(em);
            verify(true, "no Exception");
            obj = em.find(TemporalTypesFieldAccess.class, BigInteger.ONE);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testCorrectTemporalTypes() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            Query queryDate = em.createQuery("select tt from TemporalTypesFieldAccess tt where tt.utilDateAsDate = ?1");
            Query queryTime = em.createQuery("select tt from TemporalTypesFieldAccess tt where tt.utilDateAsTime = ?1");

            queryDate.setParameter(1, DATE, TemporalType.DATE);
            queryTime.setParameter(1, TIME, TemporalType.TIME);

        } finally {
            closeEntityManager(em);
        }

    }

    @Test
    @ToBeInvestigated
    public void testIncorrectTemporalTypes() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            try {
                Query queryDate = em.createQuery("select tt from TemporalTypesFieldAccess tt where tt.utilDateAsDate = ?1");

                queryDate.setParameter(1, DATE, TemporalType.TIME);
                Assert.fail();
            } catch (IllegalArgumentException ex) {
                // OK
            }

        } finally {
            closeEntityManager(em);
        }

    }

}
