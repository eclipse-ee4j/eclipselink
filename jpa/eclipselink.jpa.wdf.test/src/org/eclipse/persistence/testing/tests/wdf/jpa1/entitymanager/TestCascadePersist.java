/*******************************************************************************
 * Copyright (c) 2005, 2009 SAP. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     SAP - initial API and implementation
 ******************************************************************************/

package org.eclipse.persistence.testing.tests.wdf.jpa1.entitymanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.persistence.EntityManager;

import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.models.wdf.jpa1.node.CascadingNode;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.junit.Test;

/**
 * A new entity instance becomes both managed and persistent by invoking the persist method on it or by cascading the persist
 * operation. The semantics of the persist operation, applied to an entity X are as follows:
 * <ul>
 * <li>If X is a new entity, it becomes managed. The entity X will be entered into the database at or before transaction commit
 * or as a result of the flush operation.</li>
 * <li>If X is a preexisting managed entity, it is ignored by the persist operation. However, the persist operation is cascaded
 * to entities referenced by X, if the relationships from X to these other entities is annotated with the cascade=PERSIST or
 * cascade=ALL annotation element value or specified with the equivalent XML descriptor element.</li>
 * <li>If X is a removed entity, it becomes managed.</li>
 * <li>If X is a detached object, an IllegalArgumentException will be thrown by the persist operation (or the transaction commit
 * will fail).</li>
 * <li>For all entities Y referenced by a relationship from X, if the relationship to Y has been annotated with the cascade
 * element value cascade=PERSIST or cascade=ALL, the persist operation is applied to Y.</li>
 * </ul>
 */
public class TestCascadePersist extends JPA1Base {

    @Test
    public void testSimpleCascadeNew() throws SQLException {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            CascadingNode parent = new CascadingNode(1, null);
            CascadingNode child = new CascadingNode(2, parent);
            child.setParent(null); // to avoid circular cascade
            // cascade from parent to child
            env.beginTransaction(em);
            em.persist(parent);
            verify(em.contains(parent), "Parent not contained in persistence context after persist");
            verify(em.contains(child), "Child not contained in persistence context after persist");
            env.commitTransactionAndClear(em);
            // verify existence after commit
            verifyExistenceOnDatabase(parent.getId());
            verifyExistenceOnDatabase(child.getId());
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testSimpleCascadeManaged() throws SQLException {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            CascadingNode parent = new CascadingNode(11, null);
            env.beginTransaction(em);
            em.persist(parent);
            env.commitTransactionAndClear(em);
            // cascade from parent to child
            env.beginTransaction(em);
            parent = em.find(CascadingNode.class, new Integer(parent.getId())); // parent is now managed
            CascadingNode child = new CascadingNode(12, parent);
            child.setParent(null); // to avoid circular cascade
            em.persist(parent);
            verify(em.contains(parent), "Parent not contained in persistence context after persist");
            verify(em.contains(child), "Child not contained in persistence context after persist");
            env.commitTransactionAndClear(em);
            // verify existence after commit
            verifyExistenceOnDatabase(parent.getId());
            verifyExistenceOnDatabase(child.getId());
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testSimpleCascadeDetached() throws SQLException {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            // case 1: detached because entity exists on db but is not known by persistence context
            CascadingNode parent = new CascadingNode(21, null);
            env.beginTransaction(em);
            em.persist(parent);
            env.commitTransactionAndClear(em);
            // parent is now detached
            CascadingNode child = new CascadingNode(22, parent);
            child.setParent(null); // to avoid circular cascade
            // cascade from parent to child
            env.beginTransaction(em);
            boolean persistFailed = false;
            boolean immediateException = false;
            try {
                em.persist(parent);
                verify(em.contains(parent), "Parent not contained in persistence context after persist");
                verify(em.contains(child), "Child not contained in persistence context after persist");
            } catch (IllegalArgumentException e) {
                persistFailed = true;
                immediateException = true;
            }
            if (!immediateException) {
                try {
                    env.commitTransactionAndClear(em);
                } catch (RuntimeException e) {
                    if (!checkForPersistenceException(e)) {
                        throw e;
                    }
                    persistFailed = true;
                }
            } else {
                env.rollbackTransactionAndClear(em);
            }
            verify(persistFailed, "persist succeeded on a detached instance");
            verifyExistenceOnDatabase(parent.getId());
            verifyAbsenceFromDatabase(child.getId());
            // case 2: detached because an object with same pk but different object identity is known by persistence context
            // case 2a: state of known object: new
            CascadingNode existing = new CascadingNode(23, null);
            parent = new CascadingNode(existing.getId(), null);
            child = new CascadingNode(24, parent);
            child.setParent(null); // to avoid circular cascade
            env.beginTransaction(em);
            em.persist(existing); // known object in state new
            persistFailed = false;
            immediateException = false;
            try {
                // cascade from parent to child
                em.persist(parent);
                verify(em.contains(parent), "Parent not contained in persistence context after persist");
                verify(em.contains(child), "Child not contained in persistence context after persist");
            } catch (IllegalArgumentException e) {
                persistFailed = true;
                immediateException = true;
            }
            verify(em.contains(existing), "Previously managed entity not contained in persistence context any more");
            if (!immediateException) {
                try {
                    env.commitTransactionAndClear(em);
                } catch (RuntimeException e) {
                    if (!checkForPersistenceException(e)) {
                        throw e;
                    }
                    persistFailed = true;
                }
            } else {
                env.rollbackTransactionAndClear(em);
            }
            verify(persistFailed, "persist succeeded on a detached instance");
            verifyAbsenceFromDatabase(existing.getId());
            verifyAbsenceFromDatabase(child.getId());
            // case 2b: state of known object: managed
            existing = new CascadingNode(25, null);
            parent = new CascadingNode(existing.getId(), null);
            child = new CascadingNode(26, parent);
            child.setParent(null); // to avoid circular cascade
            env.beginTransaction(em);
            em.persist(existing);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            existing = em.find(CascadingNode.class, new Integer(existing.getId())); // known object in state managed
            persistFailed = false;
            immediateException = false;
            try {
                // cascade from parent to child
                em.persist(parent);
                verify(em.contains(parent), "Parent not contained in persistence context after persist");
                verify(em.contains(child), "Child not contained in persistence context after persist");
            } catch (IllegalArgumentException e) {
                persistFailed = true;
                immediateException = true;
            }
            verify(em.contains(existing), "Previously managed entity not contained in persistence context any more");
            if (!immediateException) {
                try {
                    env.commitTransactionAndClear(em);
                } catch (RuntimeException e) {
                    if (!checkForPersistenceException(e)) {
                        throw e;
                    }
                    persistFailed = true;
                }
            } else {
                env.rollbackTransactionAndClear(em);
            }
            verify(persistFailed, "persist did succeed on a detached instance");
            verifyExistenceOnDatabase(existing.getId());
            verifyAbsenceFromDatabase(child.getId());
            // case 2c: state of known object: deleted
            existing = new CascadingNode(27, null);
            parent = new CascadingNode(existing.getId(), null);
            child = new CascadingNode(28, parent);
            child.setParent(null); // to avoid circular cascade
            env.beginTransaction(em);
            em.persist(existing);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            existing = em.find(CascadingNode.class, new Integer(existing.getId()));
            em.remove(existing); // known object in state deleted
            persistFailed = false;
            immediateException = false;
            try {
                // cascade from parent to child
                em.persist(parent);
                verify(em.contains(parent), "Parent not contained in persistence context after persist");
                verify(em.contains(child), "Child not contained in persistence context after persist");
            } catch (IllegalArgumentException e) {
                persistFailed = true;
                immediateException = true;
            }
            if (!immediateException) {
                try {
                    env.commitTransactionAndClear(em);
                } catch (RuntimeException e) {
                    if (!checkForPersistenceException(e)) {
                        throw e;
                    }
                    persistFailed = true;
                }
            } else {
                env.rollbackTransactionAndClear(em);
            }
            verify(persistFailed, "persist did succeed on a detached instance");
            verifyExistenceOnDatabase(existing.getId());
            verifyAbsenceFromDatabase(child.getId());
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testCircularCascade() throws SQLException {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            CascadingNode parent = new CascadingNode(31, null);
            CascadingNode child = new CascadingNode(32, parent);
            // cascade from parent to child
            env.beginTransaction(em);
            em.persist(parent);
            env.commitTransactionAndClear(em);
            // verify existence after commit
            verifyExistenceOnDatabase(parent.getId());
            verifyExistenceOnDatabase(child.getId());
            emptyDatabaseTable(new int[] { 32, 31 });
            // cascade from child to parent
            env.beginTransaction(em);
            em.persist(child);
            env.commitTransactionAndClear(em);
            // verify existence after commit
            verifyExistenceOnDatabase(parent.getId());
            verifyExistenceOnDatabase(child.getId());
        } finally {
            closeEntityManager(em);
        }
    }

    private void verifyExistenceOnDatabase(int nodeId) throws SQLException {
        verify(checkForExistenceOnDatabase(nodeId), "no node with id " + nodeId + " found using JDBC.");
    }

    private void verifyAbsenceFromDatabase(int nodeId) throws SQLException {
        verify(!checkForExistenceOnDatabase(nodeId), "node with id " + nodeId + "still found using JDBC.");
    }

    private boolean checkForExistenceOnDatabase(int nodeId) throws SQLException {
        Connection conn = getEnvironment().getDataSource().getConnection();
        try {
            PreparedStatement stmt = conn.prepareStatement("select ID, PARENT from TMP_CASC_NODE where ID = ?");
            try {
                stmt.setInt(1, nodeId);
                ResultSet rs = stmt.executeQuery();
                try {
                    return rs.next();
                } finally {
                    rs.close();
                }
            } finally {
                stmt.close();
            }
        } finally {
            conn.close();
        }
    }

    private void emptyDatabaseTable(int[] keys) throws SQLException {
        Connection conn = getEnvironment().getDataSource().getConnection();
        try {
            PreparedStatement stmt = conn.prepareStatement("delete from TMP_CASC_NODE where ID = ?");
            try {
                for (int i = 0; i < keys.length; i++) {
                    stmt.setInt(1, keys[i]);
                    stmt.executeUpdate();
                }
                if (conn.getAutoCommit() != true) {
                    conn.commit();
                }
            } finally {
                stmt.close();
            }
        } finally {
            conn.close();
        }
    }
}
