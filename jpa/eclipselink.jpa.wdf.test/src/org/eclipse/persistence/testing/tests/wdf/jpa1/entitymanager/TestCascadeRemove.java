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
import org.eclipse.persistence.testing.framework.wdf.ToBeInvestigated;
import org.eclipse.persistence.testing.models.wdf.jpa1.node.CascadingNode;
import org.eclipse.persistence.testing.models.wdf.jpa1.node.CascadingNodeDescription;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.junit.Test;

/*
 * A managed entity instance becomes removed by invoking the remove method on it or by cascading the remove operation. The
 * semantics of the remove operation, applied to an entity X are as follows:
 * <ul>
 * <li>If X is a new entity, it is ignored by the remove operation. However, the remove operation is cascaded to entities
 * referenced by X, if the relationships from X to these other entities is annotated with the cascade=REMOVE or cascade=ALL
 * annotation element value.</li>
 * <li>If X is a managed entity, the remove operation causes it to become removed. The remove operation is cascaded to entities
 * referenced by X, if the relationships from X to these other entities is annotated with the cascade=REMOVE or cascade=ALL
 * annotation element value.</li>
 * <li>If X is a detached entity, an IllegalArgumentException will be thrown by the remove operation (or the transaction commit
 * will fail).</li>
 * <li>If X is a removed entity, it is ignored by the remove operation.</li>
 * <li>A removed entity X will be removed from the database at or before transaction commit or as a result of the flush
 * operation.</li>
 * </ul>
 * After an entity has been removed, its state (except for generated state) will be that of the entity at the point at which the
 * remove operation was called.
 */
public class TestCascadeRemove extends JPA1Base {

    @Test
    public void testSimpleCascadeNew() throws SQLException {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            // one-to-many relationship
            CascadingNode child = new CascadingNode(1, null);
            env.beginTransaction(em);
            em.persist(child);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            child = em.find(CascadingNode.class, new Integer(child.getId()));
            CascadingNode parent = new CascadingNode(2, null);
            parent.addChild(child);
            verify(!em.contains(parent), "Parent not a new entity");
            verify(em.contains(child), "Child not managed");
            em.remove(parent);
            env.commitTransactionAndClear(em);
            verifyAbsenceFromNodeTable(parent.getId());
            verifyAbsenceFromNodeTable(child.getId());
            // one-to-one relationship
            CascadingNodeDescription description = new CascadingNodeDescription(3, null, "a simple node");
            env.beginTransaction(em);
            em.persist(description);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            description = em.find(CascadingNodeDescription.class, new Integer(description.getId()));
            parent = new CascadingNode(4, null);
            parent.setDescription(description);
            description.setNode(parent);
            verify(!em.contains(parent), "Parent not a new entity");
            verify(em.contains(description), "Description not managed");
            em.remove(parent);
            env.commitTransactionAndClear(em);
            verifyAbsenceFromNodeTable(parent.getId());
            verifyAbsenceFromDescriptionTable(description.getId());
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testSimpleCascadeManaged() throws SQLException {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            // Case 1: status FOR_INSERT
            // one-to-many relationship
            CascadingNode child = new CascadingNode(101, null);
            env.beginTransaction(em);
            em.persist(child);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            CascadingNode parent = new CascadingNode(102, null);
            em.persist(parent);
            child = em.find(CascadingNode.class, new Integer(child.getId()));
            parent.addChild(child);
            verify(em.contains(parent), "Parent not managed");
            verify(em.contains(child), "Child not managed");
            em.remove(parent);
            env.commitTransactionAndClear(em);
            verifyAbsenceFromNodeTable(parent.getId());
            verifyAbsenceFromNodeTable(child.getId());
            // one-to-one relationship
            CascadingNodeDescription description = new CascadingNodeDescription(103, null, "a simple node");
            env.beginTransaction(em);
            em.persist(description);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            parent = new CascadingNode(104, null);
            em.persist(parent);
            description = em.find(CascadingNodeDescription.class, new Integer(description.getId()));
            parent.setDescription(description);
            description.setNode(parent);
            verify(em.contains(parent), "Parent not managed");
            verify(em.contains(description), "Description not managed");
            em.remove(parent);
            env.commitTransactionAndClear(em);
            verifyAbsenceFromNodeTable(parent.getId());
            verifyAbsenceFromDescriptionTable(description.getId());
            // Case 2: status FOR_UPDATE
            // one-to-many relationship
            parent = new CascadingNode(105, null);
            child = new CascadingNode(106, null);
            env.beginTransaction(em);
            em.persist(parent);
            em.persist(child);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            parent = em.find(CascadingNode.class, new Integer(parent.getId()));
            child = em.find(CascadingNode.class, new Integer(child.getId()));
            parent.addChild(child);
            verify(em.contains(parent), "Parent not managed");
            verify(em.contains(child), "Child not managed");
            em.remove(parent);
            env.commitTransactionAndClear(em);
            verifyAbsenceFromNodeTable(parent.getId());
            verifyAbsenceFromNodeTable(child.getId());
            // one-to-one relationship
            parent = new CascadingNode(107, null);
            description = new CascadingNodeDescription(108, parent, "a simple node");
            parent.setDescription(description);
            env.beginTransaction(em);
            em.persist(parent);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            parent = em.find(CascadingNode.class, new Integer(parent.getId()));
            description = parent.getDescription();
            verify(em.contains(parent), "Parent not managed");
            verify(em.contains(description), "Description not managed");
            em.remove(parent);
            env.commitTransactionAndClear(em);
            verifyAbsenceFromNodeTable(parent.getId());
            verifyAbsenceFromDescriptionTable(description.getId());
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testSimpleCascadeDetached() throws SQLException {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            // Case 1: detached because entity exists on db but is not known by persistence context
            // one-to-many relationship
            CascadingNode parent = new CascadingNode(201, null);
            CascadingNode child = new CascadingNode(202, null);
            env.beginTransaction(em);
            em.persist(parent);
            em.persist(child);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            child = em.find(CascadingNode.class, new Integer(child.getId()));
            parent.addChild(child);
            verify(!em.contains(parent), "Parent not detached");
            verify(em.contains(child), "Child not managed");
            boolean removeFailed = false;
            boolean immediateException = false;
            try {
                em.remove(parent);
                verify(!em.contains(parent), "Parent is managed");
                verify(!em.contains(child), "Child is still managed");
            } catch (IllegalArgumentException e) {
                removeFailed = true;
                immediateException = true;
            }
            if (!immediateException) {
                try {
                    env.commitTransactionAndClear(em);
                } catch (RuntimeException e) {
                    if (!checkForPersistenceException(e)) {
                        throw e;
                    }
                    removeFailed = true;
                }
            } else {
                env.rollbackTransactionAndClear(em);
            }
            verify(removeFailed, "remove succeeded on a detached instance");
            verifyExistenceInNodeTable(parent.getId());
            verifyExistenceInNodeTable(child.getId());
            // one-to-one relationship
            parent = new CascadingNode(203, null);
            CascadingNodeDescription description = new CascadingNodeDescription(204, null, "a simple node");
            env.beginTransaction(em);
            em.persist(parent);
            em.persist(description);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            description = em.find(CascadingNodeDescription.class, new Integer(description.getId()));
            parent.setDescription(description);
            verify(!em.contains(parent), "Parent not detached");
            verify(em.contains(description), "Description not managed");
            removeFailed = false;
            immediateException = false;
            try {
                em.remove(parent);
                verify(!em.contains(parent), "Parent is managed");
                verify(!em.contains(description), "Description is still managed");
            } catch (IllegalArgumentException e) {
                removeFailed = true;
                immediateException = true;
            }
            if (!immediateException) {
                try {
                    env.commitTransactionAndClear(em);
                } catch (RuntimeException e) {
                    if (!checkForPersistenceException(e)) {
                        throw e;
                    }
                    removeFailed = true;
                }
            } else {
                env.rollbackTransactionAndClear(em);
            }
            verify(removeFailed, "remove succeeded on a detached instance");
            verifyExistenceInNodeTable(parent.getId());
            verifyExistenceInDescriptionTable(description.getId());
            // Case 2: detached because an object with same pk but different object identity is known by persistence context
            // Case 2a: state of known object: FOR_INSERT
            // one-to-many relationship
            CascadingNode existing = new CascadingNode(205, null);
            parent = new CascadingNode(existing.getId(), null);
            child = new CascadingNode(206, null);
            env.beginTransaction(em);
            em.persist(child);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            em.persist(existing); // status FOR_INSERT
            child = em.find(CascadingNode.class, new Integer(child.getId()));
            parent.addChild(child);
            verify(!em.contains(parent), "Parent not detached");
            verify(em.contains(existing), "Existing not managed");
            verify(em.contains(child), "Child not managed");
            removeFailed = false;
            immediateException = false;
            try {
                em.remove(parent);
                verify(!em.contains(parent), "Parent is managed");
                verify(!em.contains(child), "Child is still managed");
            } catch (IllegalArgumentException e) {
                removeFailed = true;
                immediateException = true;
            }
            verify(em.contains(existing), "Previously managed entity no longer managed");
            if (!immediateException) {
                try {
                    env.commitTransactionAndClear(em);
                } catch (RuntimeException e) {
                    if (immediateException || !checkForPersistenceException(e)) {
                        throw e;
                    }
                    removeFailed = true;
                }
            } else {
                env.rollbackTransactionAndClear(em);
            }
            verify(removeFailed, "remove succeeded on a detached instance");
            verifyAbsenceFromNodeTable(existing.getId());
            verifyExistenceInNodeTable(child.getId());
            // one-to-one relationship
            existing = new CascadingNode(207, null);
            parent = new CascadingNode(existing.getId(), null);
            description = new CascadingNodeDescription(208, null, "some text");
            env.beginTransaction(em);
            em.persist(description);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            em.persist(existing); // status FOR_INSERT
            description = em.find(CascadingNodeDescription.class, new Integer(description.getId()));
            parent.setDescription(description);
            verify(!em.contains(parent), "Parent not detached");
            verify(em.contains(existing), "Existing not managed");
            verify(em.contains(description), "Description not managed");
            removeFailed = false;
            immediateException = false;
            try {
                em.remove(parent);
                verify(!em.contains(parent), "Parent is managed");
                verify(!em.contains(description), "Description is still managed");
            } catch (IllegalArgumentException e) {
                removeFailed = true;
                immediateException = true;
            }
            verify(em.contains(existing), "Previously managed entity no longer managed");
            if (!immediateException) {
                try {
                    env.commitTransactionAndClear(em);
                } catch (RuntimeException e) {
                    if (immediateException || !checkForPersistenceException(e)) {
                        throw e;
                    }
                    removeFailed = true;
                }
            } else {
                env.rollbackTransactionAndClear(em);
            }
            verify(removeFailed, "remove succeeded on a detached instance");
            verifyAbsenceFromNodeTable(existing.getId());
            verifyExistenceInNodeTable(child.getId());
            // Case 2b: state of known object: FOR_UPADTE
            // one-to-many relationship
            existing = new CascadingNode(209, null);
            parent = new CascadingNode(existing.getId(), null);
            child = new CascadingNode(210, null);
            env.beginTransaction(em);
            em.persist(existing);
            em.persist(child);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            existing = em.find(CascadingNode.class, new Integer(existing.getId())); // state FOR_UPADTE
            child = em.find(CascadingNode.class, new Integer(child.getId()));
            parent.addChild(child);
            verify(!em.contains(parent), "Parent not detached");
            verify(em.contains(existing), "Existing not managed");
            verify(em.contains(child), "Child not managed");
            removeFailed = false;
            immediateException = false;
            try {
                em.remove(parent);
                verify(!em.contains(parent), "Parent is managed");
                verify(!em.contains(child), "Child is still managed");
            } catch (IllegalArgumentException e) {
                removeFailed = true;
                immediateException = true;
            }
            verify(em.contains(existing), "Previously managed entity no longer managed");
            if (!immediateException) {
                try {
                    env.commitTransactionAndClear(em);
                } catch (RuntimeException e) {
                    if (!checkForPersistenceException(e)) {
                        throw e;
                    }
                    removeFailed = true;
                }
            } else {
                env.rollbackTransactionAndClear(em);
            }
            verify(removeFailed, "remove succeeded on a detached instance");
            verifyExistenceInNodeTable(existing.getId());
            verifyExistenceInNodeTable(child.getId());
            // one-to-one relationship
            existing = new CascadingNode(211, null);
            parent = new CascadingNode(existing.getId(), null);
            description = new CascadingNodeDescription(212, null, "some text");
            env.beginTransaction(em);
            em.persist(existing);
            em.persist(description);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            existing = em.find(CascadingNode.class, new Integer(existing.getId())); // state FOR_UPADTE
            description = em.find(CascadingNodeDescription.class, new Integer(description.getId()));
            parent.setDescription(description);
            verify(!em.contains(parent), "Parent not detached");
            verify(em.contains(existing), "Existing not managed");
            verify(em.contains(description), "Description not managed");
            removeFailed = false;
            immediateException = false;
            try {
                em.remove(parent);
                verify(!em.contains(parent), "Parent is managed");
                verify(!em.contains(description), "Description is still managed");
            } catch (IllegalArgumentException e) {
                removeFailed = true;
                immediateException = true;
            }
            verify(em.contains(existing), "Previously managed entity no longer managed");
            if (!immediateException) {
                try {
                    env.commitTransactionAndClear(em);
                } catch (RuntimeException e) {
                    if (!checkForPersistenceException(e)) {
                        throw e;
                    }
                    removeFailed = true;
                }
            } else {
                env.rollbackTransactionAndClear(em);
            }
            verify(removeFailed, "remove succeeded on a detached instance");
            verifyExistenceInNodeTable(existing.getId());
            verifyExistenceInDescriptionTable(description.getId());
            // Case 2c: state of known object: FOR_REMOVE
            // one-to-many relationship
            existing = new CascadingNode(213, null);
            parent = new CascadingNode(existing.getId(), null);
            child = new CascadingNode(214, null);
            env.beginTransaction(em);
            em.persist(existing);
            em.persist(child);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            existing = em.find(CascadingNode.class, new Integer(existing.getId()));
            em.remove(existing); // state FOR_REMOVE
            child = em.find(CascadingNode.class, new Integer(child.getId()));
            parent.addChild(child);
            verify(!em.contains(parent), "Parent not detached");
            verify(!em.contains(existing), "Existing not removed");
            verify(em.contains(child), "Child not managed");
            removeFailed = false;
            immediateException = false;
            try {
                em.remove(parent);
                verify(!em.contains(parent), "Parent is managed");
                verify(!em.contains(child), "Child is still managed");
            } catch (IllegalArgumentException e) {
                removeFailed = true;
                immediateException = true;
            }
            if (!immediateException) {
                try {
                    env.commitTransactionAndClear(em);
                } catch (RuntimeException e) {
                    if (!checkForPersistenceException(e)) {
                        throw e;
                    }
                    removeFailed = true;
                }
            } else {
                env.rollbackTransactionAndClear(em);
            }
            verify(removeFailed, "remove succeeded on a detached instance");
            verifyExistenceInNodeTable(existing.getId());
            verifyExistenceInNodeTable(child.getId());
            // one-to-one relationship
            existing = new CascadingNode(215, null);
            parent = new CascadingNode(existing.getId(), null);
            description = new CascadingNodeDescription(216, null, "some text");
            env.beginTransaction(em);
            em.persist(existing);
            em.persist(description);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            existing = em.find(CascadingNode.class, new Integer(existing.getId()));
            em.remove(existing); // state FOR_REMOVE
            description = em.find(CascadingNodeDescription.class, new Integer(description.getId()));
            parent.setDescription(description);
            verify(!em.contains(parent), "Parent not detached");
            verify(!em.contains(existing), "Existing not removed");
            verify(em.contains(description), "Description not managed");
            removeFailed = false;
            immediateException = false;
            try {
                em.remove(parent);
                verify(!em.contains(parent), "Parent is managed");
                verify(!em.contains(description), "Description is still managed");
            } catch (IllegalArgumentException e) {
                removeFailed = true;
                immediateException = true;
            }
            if (!immediateException) {
                try {
                    env.commitTransactionAndClear(em);
                } catch (RuntimeException e) {
                    if (!checkForPersistenceException(e)) {
                        throw e;
                    }
                    removeFailed = true;
                }
            } else {
                env.rollbackTransactionAndClear(em);
            }
            verify(removeFailed, "remove succeeded on a detached instance");
            verifyExistenceInNodeTable(existing.getId());
            verifyExistenceInDescriptionTable(description.getId());
        } finally {
            closeEntityManager(em);
        }
    }

    /* If X is a removed entity, it is ignored by the remove operation. */
    @Test
    public void testSimpleCascadeRemoved() throws SQLException {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            // one-to-many relationship
            CascadingNode parent = new CascadingNode(301, null);
            CascadingNode child = new CascadingNode(302, null);
            env.beginTransaction(em);
            em.persist(parent);
            em.persist(child);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            parent = em.find(CascadingNode.class, new Integer(parent.getId()));
            em.remove(parent);
            child = em.find(CascadingNode.class, new Integer(child.getId()));
            parent.addChild(child);
            verify(!em.contains(parent), "Parent not removed");
            verify(em.contains(child), "Child not managed");
            em.remove(parent);
            env.commitTransactionAndClear(em);
            verifyAbsenceFromNodeTable(parent.getId());
            verifyExistenceInNodeTable(child.getId());
            // one-to-one relationship
            parent = new CascadingNode(303, null);
            CascadingNodeDescription description = new CascadingNodeDescription(304, null, "description");
            env.beginTransaction(em);
            em.persist(parent);
            em.persist(description);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            parent = em.find(CascadingNode.class, new Integer(parent.getId()));
            em.remove(parent);
            description = em.find(CascadingNodeDescription.class, new Integer(description.getId()));
            parent.setDescription(description);
            verify(!em.contains(parent), "Parent not removed");
            verify(em.contains(description), "Description not managed");
            em.remove(parent);
            env.commitTransactionAndClear(em);
            verifyAbsenceFromNodeTable(parent.getId());
            verifyExistenceInDescriptionTable(description.getId());
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    @ToBeInvestigated
    public void testCircularCascade() throws SQLException {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            CascadingNode node1 = new CascadingNode(401, null);
            CascadingNode node2 = new CascadingNode(402, node1);
            node2.addChild(node1);
            node1.setParent(node2);
            env.beginTransaction(em);
            em.persist(node1);
            env.commitTransactionAndClear(em);
            verifyExistenceInNodeTable(node1.getId());
            verifyExistenceInNodeTable(node2.getId());
            env.beginTransaction(em);
            node1 = em.find(CascadingNode.class, new Integer(node1.getId()));
            node2 = em.find(CascadingNode.class, new Integer(node2.getId()));
            em.remove(node1);
            env.commitTransactionAndClear(em);
            verifyAbsenceFromNodeTable(node1.getId());
            verifyAbsenceFromNodeTable(node2.getId());
        } finally {
            closeEntityManager(em);
        }
    }

    private void verifyExistenceInNodeTable(int nodeId) throws SQLException {
        verify(checkForExistenceInNodeTable(nodeId), "no node with id " + nodeId + " found using JDBC.");
    }

    private void verifyAbsenceFromNodeTable(int nodeId) throws SQLException {
        verify(!checkForExistenceInNodeTable(nodeId), "node with id " + nodeId + " still found using JDBC.");
    }

    private boolean checkForExistenceInNodeTable(int nodeId) throws SQLException {
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

    private void verifyExistenceInDescriptionTable(int descId) throws SQLException {
        verify(checkForExistenceInDescriptionTable(descId), "no description with id " + descId + " found using JDBC.");
    }

    private void verifyAbsenceFromDescriptionTable(int descId) throws SQLException {
        verify(!checkForExistenceInDescriptionTable(descId), "description with id " + descId + " still found using JDBC.");
    }

    private boolean checkForExistenceInDescriptionTable(int descId) throws SQLException {
        Connection conn = getEnvironment().getDataSource().getConnection();
        try {
            PreparedStatement stmt = conn.prepareStatement("select ID, DESC_TEXT from TMP_CASC_NODE_DESC where ID = ?");
            try {
                stmt.setInt(1, descId);
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
