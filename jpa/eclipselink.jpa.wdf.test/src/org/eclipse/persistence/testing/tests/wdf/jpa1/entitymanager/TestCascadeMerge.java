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

import java.util.Set;
import javax.persistence.EntityManager;
import org.junit.Test;
import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.framework.wdf.ToBeInvestigated;
import org.eclipse.persistence.testing.models.wdf.jpa1.node.CascadingNode;
import org.eclipse.persistence.testing.models.wdf.jpa1.node.CascadingNodeDescription;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;

public class TestCascadeMerge extends JPA1Base {

    @Test
    @ToBeInvestigated
    public void testCascadeNew() {
        /*
         * If X is a new entity instance, a new managed entity instance X' is created and the state of X is copied into the new
         * managed entity instance X'.
         * 
         * For all entities Y referenced by relationships from X having the cascade element value cascade=MERGE or cascade=ALL,
         * Y is merged recursively as Y'. For all such Y referenced by X, X' is set to reference Y'. (Note that if X is managed
         * then X is the same object as X'.)
         */
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            CascadingNode parent = new CascadingNode(1, null);
            CascadingNodeDescription parentDescription = new CascadingNodeDescription(2, null, "new parent");
            parent.setDescription(parentDescription);
            CascadingNode child = new CascadingNode(3, null);
            CascadingNodeDescription childDescription = new CascadingNodeDescription(4, null, "new child");
            child.setDescription(childDescription);
            CascadingNode grandchild = new CascadingNode(5, null);
            CascadingNodeDescription grandchildDescription = new CascadingNodeDescription(6, null, "new grandchild");
            grandchild.setDescription(grandchildDescription);
            parent.addChild(child);
            child.addChild(grandchild);
            env.beginTransaction(em);
            CascadingNode mergedNode = em.merge(parent);
            verify(mergedNode.getId() == parent.getId(), "merged entity has wrong id: expected " + parent.getId() + ", got "
                    + mergedNode.getId());
            verify(mergedNode != parent, "merged entity not a copy");
            verify(em.contains(mergedNode), "merged entity not managed");
            CascadingNodeDescription mergedDescription = mergedNode.getDescription();
            verify(mergedDescription.getId() == parentDescription.getId(), "merged entity has wrong id: expected "
                    + parentDescription.getId() + ", got " + mergedDescription.getId());
            verify(mergedDescription != parentDescription, "merged entity not a copy");
            verify(em.contains(mergedDescription), "merged entity not managed");
            Set<CascadingNode> mergedChildren = mergedNode.getChildren();
            verify(mergedChildren.size() == 1, "Wrong number of children: expected 1, got " + mergedChildren.size());
            for (CascadingNode temp : mergedChildren) {
                mergedNode = temp;
                break;
            }
            verify(mergedNode.getId() == child.getId(), "merged entity has wrong id: expected " + child.getId() + ", got "
                    + mergedNode.getId());
            verify(mergedNode != child, "merged entity not a copy");
            verify(em.contains(mergedNode), "merged entity not managed");
            mergedDescription = mergedNode.getDescription();
            verify(mergedDescription.getId() == childDescription.getId(), "merged entity has wrong id: expected "
                    + childDescription.getId() + ", got " + mergedDescription.getId());
            verify(mergedDescription != childDescription, "merged entity not a copy");
            verify(em.contains(mergedDescription), "merged entity not managed");
            mergedChildren = mergedNode.getChildren();
            verify(mergedChildren.size() == 1, "Wrong number of children: expected 1, got " + mergedChildren.size());
            for (CascadingNode temp : mergedChildren) {
                mergedNode = temp;
                break;
            }
            verify(mergedNode.getId() == grandchild.getId(), "merged entity has wrong id: expected " + grandchild.getId()
                    + ", got " + mergedNode.getId());
            verify(mergedNode != grandchild, "merged entity not a copy");
            verify(em.contains(mergedNode), "merged entity not managed");
            mergedDescription = mergedNode.getDescription();
            verify(mergedDescription.getId() == grandchildDescription.getId(), "merged entity has wrong id: expected "
                    + grandchildDescription.getId() + ", got " + mergedDescription.getId());
            verify(mergedDescription != grandchildDescription, "merged entity not a copy");
            verify(em.contains(mergedDescription), "merged entity not managed");
            mergedChildren = mergedNode.getChildren();
            verify(mergedChildren == null, "merged entity has children");
            env.commitTransactionAndClear(em);
            verifyExistence(em, parent);
            verifyExistence(em, parentDescription);
            verifyExistence(em, child);
            verifyExistence(em, childDescription);
            verifyExistence(em, grandchild);
            verifyExistence(em, grandchildDescription);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    @ToBeInvestigated
    public void testCascadeDetached() {
        /*
         * If X is a detached entity, the state of X is copied onto a pre-existing managed entity instance X' of the same
         * identity or a new managed copy X' of X is created.
         * 
         * For all entities Y referenced by relationships from X having the cascade element value cascade=MERGE or cascade=ALL,
         * Y is merged recursively as Y'. For all such Y referenced by X, X' is set to reference Y'. (Note that if X is managed
         * then X is the same object as X'.)
         */
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            // types of being detached:
            // parent, parentDescription: not in persistence context but on db
            // child, childDescription: different object with same pk in persistence context, state FOR_UPDATE
            // grandchild, grandchildDescription: different object with same pk in persistence context, state FOR_INSERT
            CascadingNode parent = new CascadingNode(101, null);
            CascadingNodeDescription parentDescription = new CascadingNodeDescription(102, null, "new parent");
            parent.setDescription(parentDescription);
            CascadingNode child = new CascadingNode(103, null);
            CascadingNodeDescription childDescription = new CascadingNodeDescription(104, null, "new child");
            child.setDescription(childDescription);
            parent.addChild(child);
            env.beginTransaction(em);
            em.persist(parent);
            env.commitTransactionAndClear(em);
            verifyExistence(em, parent);
            verifyExistence(em, parentDescription);
            verifyExistence(em, child);
            verifyExistence(em, childDescription);
            env.beginTransaction(em);
            CascadingNode grandchild = new CascadingNode(105, null);
            CascadingNodeDescription grandchildDescription = new CascadingNodeDescription(106, null, "new grandchild");
            grandchild.setDescription(grandchildDescription);
            child.addChild(grandchild);
            CascadingNode managedChild = em.find(CascadingNode.class, new Integer(child.getId()));
            CascadingNodeDescription managedChildDescription = em.find(CascadingNodeDescription.class, new Integer(
                    childDescription.getId()));
            CascadingNode managedGrandchild = new CascadingNode(grandchild.getId(), null);
            CascadingNodeDescription managedGrandchildDescription = new CascadingNodeDescription(grandchildDescription.getId(),
                    null, "new grandchild");
            managedGrandchild.setDescription(managedGrandchildDescription);
            managedChild.addChild(managedGrandchild);
            em.persist(managedGrandchild);
            verify(!em.contains(parent), "parent is managed");
            verify(!em.contains(parentDescription), "parentDescription is managed");
            verify(!em.contains(child), "child is managed");
            verify(!em.contains(childDescription), "childDescription is managed");
            verify(!em.contains(grandchild), "grandchild is managed");
            verify(!em.contains(grandchildDescription), "grandchildDescription is managed");
            verify(em.contains(managedChild), "managedChild is not managed");
            verify(em.contains(managedChildDescription), "managedChildDescription is not managed");
            verify(em.contains(managedGrandchild), "managedGrandchild is not managed");
            verify(em.contains(managedGrandchildDescription), "managedGrandchildDescription is not managed");
            // setup complete
            CascadingNode mergedNode = em.merge(parent);
            verify(mergedNode.getId() == parent.getId(), "merged entity has wrong id: expected " + parent.getId() + ", got "
                    + mergedNode.getId());
            verify(mergedNode != parent, "merged entity not a copy");
            verify(em.contains(mergedNode), "merged entity not managed");
            CascadingNodeDescription mergedDescription = mergedNode.getDescription();
            verify(mergedDescription.getId() == parentDescription.getId(), "merged entity has wrong id: expected "
                    + parentDescription.getId() + ", got " + mergedDescription.getId());
            verify(mergedDescription != parentDescription, "merged entity not a copy");
            verify(em.contains(mergedDescription), "merged entity not managed");
            Set<CascadingNode> mergedChildren = mergedNode.getChildren();
            verify(mergedChildren.size() == 1, "Wrong number of children: expected 1, got " + mergedChildren.size());
            for (CascadingNode temp : mergedChildren) {
                mergedNode = temp;
                break;
            }
            verify(mergedNode.getId() == child.getId(), "merged entity has wrong id: expected " + child.getId() + ", got "
                    + mergedNode.getId());
            verify(mergedNode != child, "merged entity not a copy");
            verify(em.contains(mergedNode), "merged entity not managed");
            mergedDescription = mergedNode.getDescription();
            verify(mergedDescription.getId() == childDescription.getId(), "merged entity has wrong id: expected "
                    + childDescription.getId() + ", got " + mergedDescription.getId());
            verify(mergedDescription != childDescription, "merged entity not a copy");
            verify(em.contains(mergedDescription), "merged entity not managed");
            mergedChildren = mergedNode.getChildren();
            verify(mergedChildren.size() == 1, "Wrong number of children: expected 1, got " + mergedChildren.size());
            for (CascadingNode temp : mergedChildren) {
                mergedNode = temp;
                break;
            }
            verify(mergedNode.getId() == grandchild.getId(), "merged entity has wrong id: expected " + grandchild.getId()
                    + ", got " + mergedNode.getId());
            verify(mergedNode != grandchild, "merged entity not a copy");
            verify(em.contains(mergedNode), "merged entity not managed");
            mergedDescription = mergedNode.getDescription();
            verify(mergedDescription.getId() == grandchildDescription.getId(), "merged entity has wrong id: expected "
                    + grandchildDescription.getId() + ", got " + mergedDescription.getId());
            verify(mergedDescription != grandchildDescription, "merged entity not a copy");
            verify(em.contains(mergedDescription), "merged entity not managed");
            mergedChildren = mergedNode.getChildren();
            verify(mergedChildren == null, "merged entity has children");
            env.commitTransactionAndClear(em);
            verifyExistence(em, parent);
            verifyExistence(em, parentDescription);
            verifyExistence(em, child);
            verifyExistence(em, childDescription);
            verifyExistence(em, grandchild);
            verifyExistence(em, grandchildDescription);
        } finally {
            closeEntityManager(em);
        }
    }

    /**
     * Scenario: Merge a detached entity in case an entity with the same primary key but different object identiy exists in the
     * persistence context <b>in state FOR_DELETE</b>. The specification does not state clearly how to behave in that case. We
     * decided to throw an IllegalArgumentException.
     */
    @Test
    public void testCascadeDetachedRemoved() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            // child, childDescription: different object with same pk in persistence context, state FOR_DELETE
            // others: detached (other types)
            CascadingNode parent = new CascadingNode(201, null);
            CascadingNodeDescription parentDescription = new CascadingNodeDescription(202, null, "new parent");
            parent.setDescription(parentDescription);
            CascadingNode child = new CascadingNode(203, null);
            CascadingNodeDescription childDescription = new CascadingNodeDescription(204, null, "new child");
            child.setDescription(childDescription);
            CascadingNode grandchild = new CascadingNode(205, null);
            CascadingNodeDescription grandchildDescription = new CascadingNodeDescription(206, null, "new grandchild");
            grandchild.setDescription(grandchildDescription);
            parent.addChild(child);
            child.addChild(grandchild);
            env.beginTransaction(em);
            em.persist(parent);
            env.commitTransactionAndClear(em);
            verifyExistence(em, parent);
            verifyExistence(em, parentDescription);
            verifyExistence(em, child);
            verifyExistence(em, childDescription);
            verifyExistence(em, grandchild);
            verifyExistence(em, grandchildDescription);
            env.beginTransaction(em);
            CascadingNode managedParent = em.find(CascadingNode.class, new Integer(parent.getId()));
            CascadingNodeDescription managedParentDescription = em.find(CascadingNodeDescription.class, new Integer(
                    parentDescription.getId()));
            CascadingNode managedChild = em.find(CascadingNode.class, new Integer(child.getId()));
            CascadingNodeDescription managedChildDescription = em.find(CascadingNodeDescription.class, new Integer(
                    childDescription.getId()));
            CascadingNode managedGrandchild = em.find(CascadingNode.class, new Integer(grandchild.getId()));
            CascadingNodeDescription managedGrandchildDescription = em.find(CascadingNodeDescription.class, new Integer(
                    grandchildDescription.getId()));
            managedParent.setChildren(null);
            em.remove(managedChild);
            em.persist(managedGrandchild);
            verify(!em.contains(parent), "parent is managed");
            verify(!em.contains(parentDescription), "parentDescription is managed");
            verify(!em.contains(child), "child is managed");
            verify(!em.contains(childDescription), "childDescription is managed");
            verify(!em.contains(grandchild), "grandchild is managed");
            verify(!em.contains(grandchildDescription), "grandchildDescription is managed");
            verify(em.contains(managedParent), "managedParent is not managed");
            verify(em.contains(managedParentDescription), "managedParentDescription is not managed");
            verify(!em.contains(managedChild), "managedChild is not in state FOR_DELETE");
            verify(!em.contains(managedChildDescription), "managedChildDescription is not in state FOR_DELETE");
            verify(em.contains(managedGrandchild), "managedGrandchild is not managed");
            verify(em.contains(managedGrandchildDescription), "managedGrandchildDescription is not managed");
            // setup complete
            boolean mergeFailed = false;
            try {
                em.merge(parent);
            } catch (IllegalArgumentException e) {
                // $JL-EXC$ this is expected behavior
                mergeFailed = true;
            }
            verify(mergeFailed, "Merge did not throw IllegalArgumentException");
            verify(em.contains(managedParent), "managedParent is not managed");
            verify(em.contains(managedParentDescription), "managedParentDescription is not managed");
            verify(managedParent.getDescription() == managedParentDescription, "managedParent has wrong description");
            Set<CascadingNode> children = managedParent.getChildren();
            verify(children == null || children.size() == 0, "parent has children");
            verify(!em.contains(managedChild), "managedChild is not in state FOR_DELETE");
            verify(!em.contains(managedChildDescription), "managedChildDescription is not in state FOR_DELETE");
            verify(em.contains(managedGrandchild), "managedGrandchild is not managed");
            verify(em.contains(managedGrandchildDescription), "managedGrandchildDescription is not managed");
            verify(managedGrandchild.getDescription() == managedGrandchildDescription,
                    "managedGrandchild has wrong description");
            env.rollbackTransactionAndClear(em);
            verifyExistence(em, parent);
            verifyExistence(em, parentDescription);
            verifyExistence(em, child);
            verifyExistence(em, childDescription);
            verifyExistence(em, grandchild);
            verifyExistence(em, grandchildDescription);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testCircularCascade() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            CascadingNode node1 = new CascadingNode(301, null);
            CascadingNode node2 = new CascadingNode(302, null);
            env.beginTransaction(em);
            em.persist(node1);
            em.persist(node2);
            env.commitTransactionAndClear(em);
            verifyExistence(em, node1);
            verifyExistence(em, node2);
            node1.addChild(node2);
            node2.addChild(node1);
            env.beginTransaction(em);
            CascadingNode mergedNode1 = em.merge(node1);
            verify(em.contains(mergedNode1), "mergedNode1 not managed");
            Set<CascadingNode> childrenOfNode1 = mergedNode1.getChildren();
            verify(childrenOfNode1.size() == 1, "Wrong number of children: expected 1, got " + childrenOfNode1.size());
            CascadingNode childOfNode1 = null;
            for (CascadingNode temp : childrenOfNode1) {
                childOfNode1 = temp;
                break;
            }
            verify(childOfNode1.getId() == node2.getId(), "childOfNode1 has wrong id: " + childOfNode1.getId());
            verify(em.contains(childOfNode1), "childOfNode1 not managed");
            Set<CascadingNode> childrenOfNode2 = childOfNode1.getChildren();
            verify(childrenOfNode2.size() == 1, "Wrong number of children: expected 1, got " + childrenOfNode2.size());
            CascadingNode childOfNode2 = null;
            for (CascadingNode temp : childrenOfNode2) {
                childOfNode2 = temp;
                break;
            }
            verify(childOfNode2 == mergedNode1, "mergedNode1 not child of childOfNode1");
            env.commitTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    /**
     * Tests a scenario where two nodes with the same primary key are related to each other, one is managed, the other detached.
     */
    @Test
    public void testCircularCascadeWithSamePks() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            CascadingNode managedNode = new CascadingNode(401, null);
            CascadingNode detachedNode = new CascadingNode(managedNode.getId(), null);
            env.beginTransaction(em);
            em.persist(managedNode);
            env.commitTransactionAndClear(em);
            verifyExistence(em, managedNode);
            env.beginTransaction(em);
            managedNode = em.find(CascadingNode.class, new Integer(managedNode.getId()));
            managedNode.addChild(detachedNode);
            detachedNode.addChild(managedNode);
            CascadingNode mergedNode = em.merge(detachedNode);
            verify(em.contains(mergedNode), "mergedNode not managed");
            verify(mergedNode == managedNode, "mergedNode not identical to managedNode");
            Set<CascadingNode> children = mergedNode.getChildren();
            verify(children.size() == 1, "Wrong number of children: expected 1, got " + children.size());
            CascadingNode child = null;
            for (CascadingNode temp : children) {
                child = temp;
                break;
            }
            verify(child == managedNode, "child is not identical to managedNode");
            env.commitTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testCascadeManaged() {
        /*
         * If X is a managed entity, it is ignored by the merge operation, however, the merge operation is cascaded to entities
         * referenced by relationships from X if these relationships have been annotated with the cascade element value
         * cascade=MERGE or cascade=ALL annotation.
         * 
         * For all entities Y referenced by relationships from X having the cascade element value cascade=MERGE or cascade=ALL,
         * Y is merged recursively as Y'. For all such Y referenced by X, X' is set to reference Y'. (Note that if X is managed
         * then X is the same object as X'.)
         */
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            // states:
            // parent, parentDescription: managed
            // child, childDescription: new
            CascadingNode parent = new CascadingNode(501, null);
            CascadingNodeDescription parentDescription = new CascadingNodeDescription(502, null, "managed parent");
            parent.setDescription(parentDescription);
            CascadingNode child = new CascadingNode(503, null);
            CascadingNodeDescription childDescription = new CascadingNodeDescription(504, null, "new child");
            child.setDescription(childDescription);
            env.beginTransaction(em);
            em.persist(parent);
            env.commitTransactionAndClear(em);
            verifyExistence(em, parent);
            verifyExistence(em, parentDescription);
            verifyAbsence(em, child);
            verifyAbsence(em, childDescription);
            env.beginTransaction(em);
            parent = em.find(CascadingNode.class, new Integer(parent.getId()));
            parent.addChild(child);
            // setup complete
            CascadingNode mergedNode = em.merge(parent);
            verify(mergedNode == parent, "merge returned a copy of the managed entity");
            verify(em.contains(mergedNode), "merged entity not managed");
            CascadingNodeDescription mergedDescription = mergedNode.getDescription();
            verify(mergedDescription.getId() == parentDescription.getId(), "merged entity has wrong id: expected "
                    + parentDescription.getId() + ", got " + mergedNode.getId());
            verify(mergedDescription != parentDescription, "merged entity not a copy");
            verify(em.contains(mergedDescription), "merged entity not managed");
            Set<CascadingNode> mergedChildren = mergedNode.getChildren();
            verify(mergedChildren.size() == 1, "Wrong number of children: expected 1, got " + mergedChildren.size());
            for (CascadingNode temp : mergedChildren) {
                mergedNode = temp;
                break;
            }
            verify(mergedNode.getId() == child.getId(), "merged entity has wrong id: expected " + child.getId() + ", got "
                    + mergedNode.getId());
            verify(mergedNode != child, "merged entity not a copy");
            verify(em.contains(mergedNode), "merged entity not managed");
            mergedDescription = mergedNode.getDescription();
            verify(mergedDescription.getId() == childDescription.getId(), "merged entity has wrong id: expected "
                    + childDescription.getId() + ", got " + mergedNode.getId());
            verify(mergedDescription != childDescription, "merged entity not a copy");
            verify(em.contains(mergedDescription), "merged entity not managed");
            mergedChildren = mergedNode.getChildren();
            verify(mergedChildren == null || mergedChildren.size() == 0, "Node has children, expected none");
            env.commitTransactionAndClear(em);
            verifyExistence(em, parent);
            verifyExistence(em, parentDescription);
            verifyExistence(em, child);
            verifyExistence(em, childDescription);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testCascadeRemoved() {
        /*
         * If X is a removed entity instance, an IllegalArgumentException will be thrown by the merge operation (or the
         * transaction commit will fail).
         */
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            // states:
            // parent, parentDescription: new
            // child, childDescription: removed
            CascadingNode parent = new CascadingNode(601, null);
            CascadingNodeDescription parentDescription = new CascadingNodeDescription(602, null, "new parent");
            parent.setDescription(parentDescription);
            CascadingNode child = new CascadingNode(603, null);
            CascadingNodeDescription childDescription = new CascadingNodeDescription(604, null, "removed child");
            child.setDescription(childDescription);
            env.beginTransaction(em);
            em.persist(child);
            env.commitTransactionAndClear(em);
            verifyAbsence(em, parent);
            verifyAbsence(em, parentDescription);
            verifyExistence(em, child);
            verifyExistence(em, childDescription);
            env.beginTransaction(em);
            child = em.find(CascadingNode.class, new Integer(child.getId()));
            em.remove(child);
            parent.addChild(child);
            verify(!em.contains(parent), "parent not new");
            verify(!em.contains(child), "child not removed");
            // setup complete
            boolean mergeFailed = false;
            boolean immediateException = false;
            try {
                em.merge(parent);
            } catch (IllegalArgumentException e) {
                mergeFailed = true;
                immediateException = true;
                verify(!em.contains(parent), "parent not new");
                verify(!em.contains(child), "child not removed");
            }
            if (!immediateException) {
                try {
                    env.commitTransactionAndClear(em);
                } catch (RuntimeException e) {
                    if (!checkForPersistenceException(e)) {
                        throw e;
                    }
                    mergeFailed = true;
                }
            } else {
                env.rollbackTransactionAndClear(em);
            }
            verify(mergeFailed, "merge succeeded on a removed entity");
            verifyAbsence(em, parent);
            verifyAbsence(em, parentDescription);
            verifyExistence(em, child);
            verifyExistence(em, childDescription);
        } finally {
            closeEntityManager(em);
        }
    }

    private void verifyExistence(EntityManager em, Object entity) {
        if (entity instanceof CascadingNode) {
            CascadingNode node = (CascadingNode) entity;
            CascadingNode found = em.find(CascadingNode.class, new Integer(node.getId()));
            verify(found != null, "cascading node with id " + node.getId() + " not found");
        } else if (entity instanceof CascadingNodeDescription) {
            CascadingNodeDescription desc = (CascadingNodeDescription) entity;
            CascadingNodeDescription found = em.find(CascadingNodeDescription.class, new Integer(desc.getId()));
            verify(found != null, "cascading node description with id " + desc.getId() + " not found");
        } else {
            throw new IllegalArgumentException("not supported");
        }
    }

    private void verifyAbsence(EntityManager em, Object entity) {
        if (entity instanceof CascadingNode) {
            CascadingNode node = (CascadingNode) entity;
            CascadingNode found = em.find(CascadingNode.class, new Integer(node.getId()));
            verify(found == null, "cascading node with id " + node.getId() + " found");
        } else if (entity instanceof CascadingNodeDescription) {
            CascadingNodeDescription desc = (CascadingNodeDescription) entity;
            CascadingNodeDescription found = em.find(CascadingNodeDescription.class, new Integer(desc.getId()));
            verify(found == null, "cascading node description with id " + desc.getId() + " found");
        } else {
            throw new IllegalArgumentException("not supported");
        }
    }
}
