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

public class TestCascadeRefresh extends JPA1Base {

    @Test
    @ToBeInvestigated
    public void testCascade() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            // on db: child -> grandchild
            // before refresh: parent (FOR_INSERT) -> child -> null
            // after refresh: parent -> child -> grandchild
            CascadingNode parent = new CascadingNode(1, null);
            CascadingNodeDescription parentDescription = new CascadingNodeDescription(2, null, "parent");
            parent.setDescription(parentDescription);
            CascadingNode child = new CascadingNode(3, null);
            CascadingNodeDescription childDescription = new CascadingNodeDescription(4, null, "child");
            child.setDescription(childDescription);
            CascadingNode grandchild = new CascadingNode(5, child);
            CascadingNodeDescription grandchildDescription = new CascadingNodeDescription(6, null, "grandchild");
            grandchild.setDescription(grandchildDescription);
            env.beginTransaction(em);
            em.persist(child);
            env.commitTransactionAndClear(em);
            verifyAbsence(em, parent);
            verifyAbsence(em, parentDescription);
            verifyExistence(em, child);
            verifyExistence(em, childDescription);
            verifyExistence(em, grandchild);
            verifyExistence(em, grandchildDescription);
            env.beginTransaction(em);
            em.persist(parent);
            verify(em.contains(parent), "parent is not managed");
            verify(em.contains(parentDescription), "parentDescription is not managed");
            child = em.find(CascadingNode.class, new Integer(child.getId()));
            childDescription = em.find(CascadingNodeDescription.class, new Integer(childDescription.getId()));
            childDescription.setDescription("updated");
            parent.addChild(child);
            child.setChildren(null);
            // setup complete
            em.refresh(parent);
            CascadingNode refreshedNode = parent;
            verify(em.contains(refreshedNode), "refreshed entity not managed");
            CascadingNodeDescription refreshedDescription = refreshedNode.getDescription();
            verify(refreshedDescription == parentDescription, "refreshed entity is a copy");
            verify(em.contains(refreshedDescription), "refreshed entity not managed");
            Set<CascadingNode> refreshedChildren = refreshedNode.getChildren();
            verify(refreshedChildren.size() == 1, "Wrong number of children: expected 1, got " + refreshedChildren.size());
            for (CascadingNode temp : refreshedChildren) {
                refreshedNode = temp;
                break;
            }
            verify(refreshedNode == child, "refreshed entity is a copy");
            verify(em.contains(refreshedNode), "refreshed entity not managed");
            refreshedDescription = refreshedNode.getDescription();
            verify(refreshedDescription == childDescription, "refreshed entity is a copy");
            verify(em.contains(refreshedDescription), "refreshed entity not managed");
            verify("child".equals(refreshedDescription.getDescription()), "refreshed entity has wrong description: "
                    + refreshedDescription.getDescription());
            refreshedChildren = refreshedNode.getChildren();
            verify(refreshedChildren.size() == 1, "Wrong number of children: expected 1, got " + refreshedChildren.size());
            for (CascadingNode temp : refreshedChildren) {
                refreshedNode = temp;
                break;
            }
            verify(refreshedNode.getId() == grandchild.getId(), "refreshed entity has wrong id: expected " + grandchild.getId()
                    + ", got " + refreshedNode.getId());
            verify(em.contains(refreshedNode), "refreshed entity not managed");
            refreshedDescription = refreshedNode.getDescription();
            verify(refreshedDescription.getId() == grandchildDescription.getId(), "refreshed entity has wrong id: expected "
                    + grandchildDescription.getId() + ", got " + refreshedDescription.getId());
            verify(em.contains(refreshedDescription), "refreshed entity not managed");
            refreshedChildren = refreshedNode.getChildren();
            verify(refreshedChildren == null || refreshedChildren.size() == 0, "Node has children, expected none");
            childDescription.setDescription("updated");
            env.commitTransactionAndClear(em);
            verifyExistence(em, parent);
            verifyExistence(em, parentDescription);
            verifyExistence(em, child);
            verifyExistence(em, childDescription);
            verifyDescription(em, childDescription, "updated");
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

    private void verifyDescription(EntityManager em, CascadingNodeDescription desc, String expectedDescription) {
        CascadingNodeDescription found = em.find(CascadingNodeDescription.class, new Integer(desc.getId()));
        verify(found != null, "cascading node description with id " + desc.getId() + " not found");
        if (found != null) {
            verify(expectedDescription.equals(found.getDescription()), "cascading node " + found.getId()
                    + "has wrong description, excpected" + expectedDescription + " got " + found.getDescription());
        }
    }
}
