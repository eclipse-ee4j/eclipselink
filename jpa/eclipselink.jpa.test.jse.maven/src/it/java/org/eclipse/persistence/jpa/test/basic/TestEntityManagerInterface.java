/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 IBM Corporation and/or its affiliates. All rights reserved.
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
//     IBM - initial implementation
//
//     07/16/2019-2.7 Jody Grassel
//       - 547173: EntityManager.unwrap(Connection.class) returns null

package org.eclipse.persistence.jpa.test.basic;

import java.sql.Connection;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestEntityManagerInterface {
    @Emf
    private EntityManagerFactory emf;
    
    @Emf(name = "WithExclusiveConnection", properties = {@Property(name = "eclipselink.jdbc.exclusive-connection.mode", value = "Always")})
    private EntityManagerFactory emfWithExclusiveConnection;
    
    @Test
    public void testPreserveBehaviorWithNoTransaction() throws Exception {
        EntityManager em = emf.createEntityManager();
        try {
            Connection conn = em.unwrap(Connection.class);
            Assert.assertNull(conn); // No transaction, so expecting null
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    @Test
    public void testPreserveBehaviorWithTransaction() throws Exception {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Connection conn = em.unwrap(Connection.class);
            Assert.assertNotNull(conn); // Transaction active, so expecting a connection
        } finally {
            if (em != null) {
                em.getTransaction().rollback();
                em.close();
            }
        }
    }
    
    @Test
    public void testPreserveBehaviorAfterTransaction() throws Exception {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Connection conn = em.unwrap(Connection.class);
            Assert.assertNotNull(conn); // Transaction active, so expecting a connection
            em.getTransaction().rollback();
            
            conn = em.unwrap(Connection.class);
            Assert.assertNull(conn); // No transaction, so expecting null
        } finally {
            if (em != null) {
                if (em.getTransaction().isActive())
                    em.getTransaction().rollback();
                em.close();
            }
        }
    }
    
    @Test
    public void testPreserveBehaviorWithTransactionWithExclusiveConnection() throws Exception {
        EntityManager em = emfWithExclusiveConnection.createEntityManager();
        try {
            em.getTransaction().begin();
            Connection conn = em.unwrap(Connection.class);
            Assert.assertNotNull(conn); // Transaction active, so expecting a connection
        } finally {
            if (em != null) {
                em.getTransaction().rollback();
                em.close();
            }
        }
    }
    
    @Test
    public void testUnwrapWithExclusiveConnectionAfterTransaction() throws Exception {
        EntityManager em = emfWithExclusiveConnection.createEntityManager();
        try {
            em.getTransaction().begin();
            Connection conn = em.unwrap(Connection.class);
            Assert.assertNotNull(conn);
            
            em.getTransaction().rollback();
            
            Connection conn2 = em.unwrap(Connection.class);
            Assert.assertNotNull(conn2); // Expecting a connection to still be returned
            Assert.assertSame(conn, conn2); // Expecting the same Connection
        } finally {
            if (em != null) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                em.close();
            }
        }
    }
}
