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

/*
 * The semantics of the flush operation, applied to an entity X are as follows:
 * <p>
 * If X is a managed entity, it is synchronized to the database. For all entities Y referenced by a relationship from X, if the
 * relationship to Y has been annotated with the cascade element value cascade=PERSIST or cascade=ALL, the persist operation is
 * applied to Y.
 * <p>
 * If X is a removed entity, it is removed from the database. No cascade options are relevant. </ul>
 */
public class TestCascadeFlush extends JPA1Base {

    /*
     * The semantics of the persist operation, applied to an entity X are as follows: If X is a new entity, it becomes managed.
     * The entity X will be entered into the database at or before transaction commit or as a result of the flush operation.
     */
    @Test
    public void testSimpleCascadeToNew() throws SQLException {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            CascadingNode parent = new CascadingNode(1, null);
            env.beginTransaction(em);
            em.persist(parent);
            CascadingNode child = new CascadingNode(2, parent);
            child.setParent(null); // to avoid circular cascade
            verify(!em.contains(child), "Child is already managed");
            em.flush();
            env.commitTransactionAndClear(em);
            // verify existence after commit
            verifyExistenceOnDatabase(parent.getId());
            verifyExistenceOnDatabase(child.getId());
        } finally {
            closeEntityManager(em);
        }
    }

    /*
     * The semantics of the persist operation, applied to an entity X are as follows: If X is a preexisting managed entity, it
     * is ignored by the persist operation. However, the persist operation is cascaded to entities referenced by X, if the
     * relationships from X to these other entities is annotated with the cascade=PERSIST or cascade=ALL annotation element
     * value or specified with the equivalent XML descriptor element.
     */
    @Test
    public void testSimpleCascadeToManaged() throws SQLException {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            CascadingNode parent = new CascadingNode(11, null);
            CascadingNode child = new CascadingNode(12, parent);
            child.setParent(null); // to avoid circular cascade
            env.beginTransaction(em);
            em.persist(parent);
            verify(em.contains(parent), "Parent not contained in persistence context after persist");
            verify(em.contains(child), "Child not contained in persistence context after persist");
            CascadingNode grandchild = new CascadingNode(13, child);
            grandchild.setParent(null); // to avoid circular cascade
            verify(!em.contains(grandchild), "Grandchild is already managed");
            em.flush();
            env.commitTransactionAndClear(em);
            // verify existence after commit
            verifyExistenceOnDatabase(parent.getId());
            verifyExistenceOnDatabase(child.getId());
            verifyExistenceOnDatabase(grandchild.getId());
        } finally {
            closeEntityManager(em);
        }
    }

    /*
     * The semantics of the persist operation, applied to an entity X are as follows: If X is a detached object, 
     * the EntityExistsException may be thrown when the persist operation is invoked, 
     * or the EntityExistsException or another PersistenceException may be thrown at flush or commit time.
     */
    @Test
    public void testSimpleCascadeToDetached1() throws SQLException {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            // case 1: detached because entity exists on db but is not known by persistence context
            CascadingNode child = new CascadingNode(21, null);
            env.beginTransaction(em);
            em.persist(child);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            CascadingNode parent = new CascadingNode(22, null);
            em.persist(parent);
            parent.addChild(child); // child is detached
            verify(em.contains(parent), "Parent not contained in persistence context after persist");
            verify(!em.contains(child), "Child is already managed");
            boolean exceptionThrown = false;
            try {
                env.commitTransactionAndClear(em);
            } catch (RuntimeException e) {
                if (!checkForPersistenceException(e)) {
                    throw e;
                }
                exceptionThrown = true;
            }
            verify(exceptionThrown, "commit did not fail as expected");
            // can't verify anything on the database as state is undefined after rollback
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testSimpleCascadeToDetached2a() throws SQLException {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            // case 2: detached because an object with same pk but different object identity is known by persistence context
            // case 2a: state of known object: new
            CascadingNode existing = new CascadingNode(23, null);
            CascadingNode parent = new CascadingNode(24, null);
            env.beginTransaction(em);
            em.persist(existing); // known object in state new
            em.persist(parent);
            CascadingNode child = new CascadingNode(existing.getId(), parent);
            child.setParent(null); // to avoid circular cascade
            verify(em.contains(existing), "Existing not contained in persistence context after persist");
            verify(em.contains(parent), "Parent not contained in persistence context after persist");
            verify(!em.contains(child), "Child is already managed");
            boolean exceptionThrown = false;
            try {
                env.commitTransactionAndClear(em);
            } catch (RuntimeException e) {
                if (!checkForPersistenceException(e)) {
                    throw e;
                }
                exceptionThrown = true;
            }
            verify(exceptionThrown, "commit did not fail as expected");
            // can't verify anything on the database as state is undefined after rollback
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testSimpleCascadeToDetached2b() throws SQLException {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            // case 2b: state of known object: managed
            CascadingNode existing = new CascadingNode(25, null);
            CascadingNode parent = new CascadingNode(26, null);
            env.beginTransaction(em);
            em.persist(existing);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            existing = em.find(CascadingNode.class, new Integer(existing.getId())); // known object in state managed
            em.persist(parent);
            CascadingNode child = new CascadingNode(existing.getId(), parent);
            child.setParent(null); // to avoid circular cascade
            verify(em.contains(existing), "Existing not contained in persistence context after find");
            verify(em.contains(parent), "Parent not contained in persistence context after persist");
            verify(!em.contains(child), "Child is already managed");
            boolean exceptionThrown = false;
            try {
                env.commitTransactionAndClear(em);
            } catch (RuntimeException e) {
                if (!checkForPersistenceException(e)) {
                    throw e;
                }
                exceptionThrown = true;
            }
            verify(exceptionThrown, "commit did not fail as expected");
            // can't verify anything on the database as state is undefined after rollback
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testSimpleCascadeToDetached2c() throws SQLException {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            // case 2c: state of known object: deleted
            CascadingNode existing = new CascadingNode(27, null);
            CascadingNode parent = new CascadingNode(28, null);
            env.beginTransaction(em);
            em.persist(existing);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            existing = em.find(CascadingNode.class, new Integer(existing.getId()));
            em.remove(existing); // known object in state deleted
            em.persist(parent);
            CascadingNode child = new CascadingNode(existing.getId(), parent);
            child.setParent(null); // to avoid circular cascade
            verify(!em.contains(existing), "Existing contained in persistence context after remove");
            verify(em.contains(parent), "Parent not contained in persistence context after persist");
            verify(!em.contains(child), "Child is already managed");
            boolean exceptionThrown = false;
            try {
                env.commitTransactionAndClear(em);
            } catch (RuntimeException e) {
                if (!checkForPersistenceException(e)) {
                    throw e;
                }
                exceptionThrown = true;
            }
            verify(exceptionThrown, "commit did not fail as expected");
            // can't verify anything on the database as state is undefined after rollback
        } finally {
            closeEntityManager(em);
        }
    }

    /*
     * The semantics of the persist operation, applied to an entity X are as follows: If X is a removed entity, it becomes
     * managed.
     */
    @Test
    public void testSimpleCascadeToRemoved() throws SQLException {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            CascadingNode child = new CascadingNode(31, null);
            env.beginTransaction(em);
            em.persist(child);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            CascadingNode parent = new CascadingNode(32, null);
            em.persist(parent);
            child = em.find(CascadingNode.class, new Integer(child.getId()));
            em.remove(child);
            parent.addChild(child);
            verify(em.contains(parent), "Parent not contained in persistence context after persist");
            verify(!em.contains(child), "Child is contained in persistence context after remove");
            em.flush();
            verify(em.contains(child), "Child is not managed after cascading persist operation");
            env.commitTransactionAndClear(em);
            verifyExistenceOnDatabase(parent.getId());
            verifyExistenceOnDatabase(child.getId());
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testCircularCascade() throws SQLException {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            // cascade from parent to child
            CascadingNode parent = new CascadingNode(41, null);
            env.beginTransaction(em);
            em.persist(parent);
            CascadingNode child = new CascadingNode(42, parent);
            env.commitTransactionAndClear(em);
            verifyExistenceOnDatabase(parent.getId());
            verifyExistenceOnDatabase(child.getId());
            // cascade from child to parent
            child = new CascadingNode(43, null);
            env.beginTransaction(em);
            em.persist(child);
            parent = new CascadingNode(44, null);
            child.setParent(parent);
            parent.addChild(child);
            env.commitTransactionAndClear(em);
            verifyExistenceOnDatabase(parent.getId());
            verifyExistenceOnDatabase(child.getId());
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testFlushLazyCollectionWithCascadePersist() throws SQLException {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            // cascade from parent to child
            final int rootId = 50;
            env.beginTransaction(em);
            CascadingNode root = new CascadingNode(rootId, null);
            root.addChild(new CascadingNode(51, root));
            em.persist(root);
            env.commitTransaction(em);
            em.clear();
            env.beginTransaction(em);
            em.find(CascadingNode.class, Integer.valueOf(rootId));
            env.commitTransaction(em);
        } finally {
            closeEntityManager(em);
        }
    }

    private void verifyExistenceOnDatabase(int nodeId) throws SQLException {
        verify(checkForExistenceOnDatabase(nodeId), "no node with id " + nodeId + " found using JDBC.");
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

}
