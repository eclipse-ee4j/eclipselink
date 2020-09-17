/*******************************************************************************
 * Copyright (c) 2020 Oracle, IBM Corporation, and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     IBM - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.persistence.jpa.test.jpql;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.SQLListener;
import org.eclipse.persistence.jpa.test.jpql.model.JPQLEntity;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestCoalesceFunction {

    @Emf(name = "coalesceEMF", createTables = DDLGen.DROP_CREATE, classes = { JPQLEntity.class })
    private EntityManagerFactory coalesceEMF;

    @SQLListener(name = "coalesceEMF")
    List<String> _sql;

    @Test
    public void testCoalesceFunctionSyntax() {
        EntityManager em = coalesceEMF.createEntityManager();
        try {
            String param = "unknown";
            String jpql = "select count(1) from JPQLEntity j where j.string1 = coalesce(j.string2, '" + param + "')";
            Query query = em.createQuery(jpql);
            query.getSingleResult();

            if(_sql != null && _sql.size() > 0) {
                String sql = _sql.get(0).toLowerCase();
                int pos = sql.indexOf("coalesce(");
                Assert.assertNotEquals(-1L, pos);
                String coalesce = sql.substring(pos, sql.indexOf(")", pos) + 1);

                DatabasePlatform pl = (DatabasePlatform)coalesceEMF.unwrap(EntityManagerFactoryImpl.class).getDatabaseSession().getDatasourcePlatform();
                if(pl.shouldBindLiterals()) {
                    Assert.assertEquals("coalesce(string2, ?)", coalesce);
                } else {
                    Assert.assertEquals("coalesce(string2, '" + param.toLowerCase() + "')", coalesce);
                }
            }
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if (em.isOpen()) {
                em.close();
            }
        }

        em = coalesceEMF.createEntityManager();
        try {
            String param = "unknown";
            String jpql = "select count(1) from JPQLEntity j where j.string1 = coalesce(j.string2, '" + param + "', '" + param + "')";
            Query query = em.createQuery(jpql);
            query.getSingleResult();

            if(_sql != null && _sql.size() > 1) {
                String sql = _sql.get(1).toLowerCase();
                int pos = sql.indexOf("coalesce(");
                Assert.assertNotEquals(-1L, pos);
                String coalesce = sql.substring(pos, sql.indexOf(")", pos) + 1);

                DatabasePlatform pl = (DatabasePlatform)coalesceEMF.unwrap(EntityManagerFactoryImpl.class).getDatabaseSession().getDatasourcePlatform();
                if(pl.shouldBindLiterals()) {
                    Assert.assertEquals("coalesce(string2, ?, ?)", coalesce);
                } else {
                    Assert.assertEquals("coalesce(string2, '" + param.toLowerCase() + "', '" + param.toLowerCase() + "')", coalesce);
                }
            }
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if (em.isOpen()) {
                em.close();
            }
        }
    }
}
