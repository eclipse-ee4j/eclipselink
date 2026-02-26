/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 * Copyright (c) 2026 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.jpa.test.query;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;

import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.query.model.QuerySyntaxEntity;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.junit.Assume;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
@Ignore()
public class TestNullParameterTypeInference {

    @Emf(name = "defaultEMF",
         createTables = DDLGen.DROP_CREATE,
         classes = { QuerySyntaxEntity.class })
    private EntityManagerFactory emf;

    @Test
    public void testNullParameterForOrIsNull() {
        if (emf == null) {
            return;
        }

        DatabasePlatform platform = emf.unwrap(EntityManagerFactoryImpl.class)
                .getDatabaseSession()
                .getPlatform();
        Assume.assumeTrue("PostgreSQL only", platform.isPostgreSQL());

        //this pass
        runQuery("""
                SELECT s
                FROM QuerySyntaxEntity s
                WHERE (s.intVal1 = :value OR (:value IS NULL AND s.intVal1 IS NULL))
                """, emf.createEntityManager());

        //this fails
        runQuery("""
                SELECT s
                FROM QuerySyntaxEntity s
                WHERE ((:value IS NULL AND s.intVal1 IS NULL) OR s.intVal1 = :value)
                """, emf.createEntityManager());
    }

    private void runQuery(String query, EntityManager em) {
        try {
            Query q = em.createQuery(query);
            q.setParameter("value", null);
            q.getResultList();
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }
}
