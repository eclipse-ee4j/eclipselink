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

package org.eclipse.persistence.testing.tests.wdf.jpa1.relation;

import java.util.Set;

import javax.persistence.EntityManager;

import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.framework.wdf.ToBeInvestigated;
import org.eclipse.persistence.testing.models.wdf.jpa1.node.Node;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.junit.Test;

@SuppressWarnings("unchecked")
public class TestNode extends JPA1Base {

    @Test
    public void testRoot() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            final int rootId = 1;
            Node root = new Node(rootId, null);
            em.persist(root);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            Node found = em.find(Node.class, new Integer(rootId));
            verify(found != null, "no node found");
            verify(found.getParent() == null, "parent not null");
            verify(found.getChildren().size() == 0, "unexpected children");
            env.rollbackTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testTwoGenerations() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            final int rootId = 10;
            Node root = new Node(rootId, null);
            final int child1Id = 11;
            Node child1 = new Node(child1Id, root);
            Node child2 = new Node(12, root);
            em.persist(root);
            em.persist(child1);
            em.persist(child2);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            Node found = em.find(Node.class, new Integer(rootId));
            verify(found != null, "no node found");
            verify(found.getParent() == null, "parent not null");
            verify(found.getChildren().size() == 2, "unexpected children");
            env.rollbackTransactionAndClear(em);
            env.beginTransaction(em);
            found = em.find(Node.class, new Integer(child1Id));
            verify(found != null, "no node found");
            Node parent = found.getParent();
            verify(em.contains(parent), "parent not contained in em");
            verify(parent != null, "parent is null");
            verify(parent.getId() == rootId, "parent has not root id");
            verify(parent.getChildren().size() == 2, "unexpected children");
            env.rollbackTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    @ToBeInvestigated
    public void testChangeParent() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            // seed the data model
            env.beginTransaction(em);
            final int root1Id = 20;
            final int root2Id = 21;
            final int childId = 22;
            Node root1 = new Node(root1Id, null);
            Node root2 = new Node(root2Id, null);
            Node child = new Node(childId, root1);
            em.persist(root1);
            em.persist(root2);
            em.persist(child);
            env.commitTransactionAndClear(em);
            // change the parent
            env.beginTransaction(em);
            child = em.find(Node.class, new Integer(childId));
            Node parent = child.getParent();
            root2 = em.find(Node.class, new Integer(root2Id));
            Set<Node> children = parent.getChildren();
            parent.setChildren(null);
            child.setParent(root2);
            root2.setChildren(children);
            env.commitTransactionAndClear(em);
            verify(!parent.postUpdateWasCalled(), "root1 was updated but it is not the owning side of the relationship");
            verify(!root2.postUpdateWasCalled(), "root2 was updated but it is not the owning side of the relationship");
            verify(child.postUpdateWasCalled(), "child was not updated but it is the owning side of the relationship");
            // check the relationship
            env.beginTransaction(em);
            child = em.find(Node.class, new Integer(childId));
            verify(child != null, "child is null");
            parent = child.getParent();
            verify(parent != null, "parent is null");
            root2 = em.find(Node.class, new Integer(root2Id));
            verify(root2 == parent, "root2 != parent");
            verify(children != null, "children is null");
            children = root2.getChildren();
            verify(children.contains(child), "child not contained in set of children");
            root1 = em.find(Node.class, new Integer(root1Id));
            Set children1 = root1.getChildren();
            verify(children1 == null || children1.isEmpty(), "children of root1 not null or empty");
            env.rollbackTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testCircle() {
        // in this tree, the root is defined by this.parent = this;
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            final int rootId = 30;
            Node root = new Node(rootId, null);
            root.setParent(root);
            em.persist(root);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            Node found = em.find(Node.class, new Integer(rootId));
            verify(found != null, "no node found");
            verify(found.getParent() == found, "parent != root");
            verify(found.getChildren().size() == 1, "unexpected children");
            env.rollbackTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }
}
