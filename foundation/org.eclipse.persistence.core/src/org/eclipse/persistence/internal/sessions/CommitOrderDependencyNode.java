/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.sessions;

import java.util.*;
import org.eclipse.persistence.descriptors.InheritancePolicy;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.localization.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;

/**
 * This wraps a descriptor with information required to compute an order for
 * dependencies. The algorithm is a simple topological sort.
 */
public class CommitOrderDependencyNode {
    protected CommitOrderCalculator owner;
    protected ClassDescriptor descriptor;
    protected AbstractSession session;

    // These are the descriptors to which we have 1:1 relationships
    protected Vector relatedNodes;
    protected CommitOrderDependencyNode predecessor;

    // Indicates the state of the traversal
    protected int traversalState;
    static public int NotVisited = 1;
    static public int InProgress = 2;
    static public int Visited = 3;

    // When we first saw this node in the traversal
    protected int discoveryTime;

    // When we finished visiting this node
    protected int finishingTime;

    public CommitOrderDependencyNode(CommitOrderCalculator calculator, ClassDescriptor descriptor, AbstractSession session) {
        this.owner = calculator;
        this.descriptor = descriptor;
        this.relatedNodes = new Vector();
        this.session = session;
    }

    public ClassDescriptor getDescriptor() {
        return descriptor;
    }

    public int getFinishingTime() {
        return finishingTime;
    }

    public CommitOrderCalculator getOwner() {
        return owner;
    }

    public CommitOrderDependencyNode getPredecessor() {
        return predecessor;
    }

    public Vector getRelatedNodes() {
        return relatedNodes;
    }

    public boolean hasBeenVisited() {
        return (traversalState == Visited);
    }

    public boolean hasNotBeenVisited() {
        return (traversalState == NotVisited);
    }

    public void markInProgress() {
        traversalState = InProgress;
    }

    public void markNotVisited() {
        traversalState = NotVisited;
    }

    public void markVisited() {
        traversalState = Visited;
    }

    /**
     * Add all owned classes for each descriptor through checking the mappings.
     * If I have a foreign mapping with a constraint dependency, then add it
     * If I'm related to a class, I'm related to all its subclasses and superclasses.
     * If my superclass is related to a class, I'm related to it.
     */
    public void recordMappingDependencies() {
        for (Enumeration mappings = getDescriptor().getMappings().elements();
                 mappings.hasMoreElements();) {
            DatabaseMapping mapping = (DatabaseMapping)mappings.nextElement();
            if (mapping.isForeignReferenceMapping()) {
                if (((ForeignReferenceMapping)mapping).hasConstraintDependency()) {
                    Class ownedClass;
                    ClassDescriptor refDescriptor = ((ForeignReferenceMapping)mapping).getReferenceDescriptor();
                    if (refDescriptor == null) {
                        refDescriptor = session.getDescriptor(((ForeignReferenceMapping)mapping).getReferenceClass());
                    }
                    ownedClass = refDescriptor.getJavaClass();

                    if (ownedClass == null) {
                        throw org.eclipse.persistence.exceptions.DescriptorException.referenceClassNotSpecified(mapping);
                    }
                    CommitOrderDependencyNode node = getOwner().nodeFor(ownedClass);
                    Vector ownedNodes = withAllSubclasses(node);

                    // I could remove duplicates here, but it's not that big a deal.
                    Helper.addAllToVector(relatedNodes, ownedNodes);
                } else if (((ForeignReferenceMapping)mapping).hasInverseConstraintDependency()) {
                    Class ownerClass;
                    ClassDescriptor refDescriptor = ((ForeignReferenceMapping)mapping).getReferenceDescriptor();
                    if (refDescriptor == null) {
                        refDescriptor = session.getDescriptor(((ForeignReferenceMapping)mapping).getReferenceClass());
                    }
                    ownerClass = refDescriptor.getJavaClass();
                    if (ownerClass == null) {
                        throw org.eclipse.persistence.exceptions.DescriptorException.referenceClassNotSpecified(mapping);
                    }
                    CommitOrderDependencyNode ownerNode = getOwner().nodeFor(ownerClass);
                    Vector ownedNodes = withAllSubclasses(this);

                    // I could remove duplicates here, but it's not that big a deal.
                    Helper.addAllToVector(ownerNode.getRelatedNodes(), ownedNodes);
                }
            }
        }
    }

    /**
     * Add all owned classes for each descriptor through checking the mappings.
     * If I have a foreign mapping with a constraint dependency, then add it
     * If I'm related to a class, I'm related to all its subclasses and superclasses.
     * If my superclass is related to a class, I'm related to it.
     */
    public void recordSpecifiedDependencies() {
        for (Enumeration constraintsEnum = getDescriptor().getConstraintDependencies().elements();
                 constraintsEnum.hasMoreElements();) {
            Class ownedClass = (Class)constraintsEnum.nextElement();
            CommitOrderDependencyNode node = getOwner().nodeFor(ownedClass);
            Vector ownedNodes = withAllSubclasses(node);

            // I could remove duplicates here, but it's not that big a deal.
            Helper.addAllToVector(relatedNodes, ownedNodes);
        }
    }

    public void setDiscoveryTime(int time) {
        discoveryTime = time;
    }

    public void setFinishingTime(int time) {
        finishingTime = time;
    }

    public void setPredecessor(CommitOrderDependencyNode n) {
        predecessor = n;
    }

    public String toString() {
        if (descriptor == null) {
            return ToStringLocalization.buildMessage("empty_commit_order_dependency_node", (Object[])null);
        } else {
            Object[] args = { descriptor };
            return ToStringLocalization.buildMessage("node", args);
        }
    }

    public void visit() {
        //Visit this node as part of a topological sort
        int startTime;
        markInProgress();
        startTime = getOwner().getNextTime();
        setDiscoveryTime(startTime);

        for (Enumeration e = getRelatedNodes().elements(); e.hasMoreElements();) {
            CommitOrderDependencyNode node = (CommitOrderDependencyNode)e.nextElement();
            if (node.hasNotBeenVisited()) {
                node.setPredecessor(this);
                node.visit();
            }
            if (node.getPredecessor() == null) {
                node.setPredecessor(this);
            }
        }

        markVisited();
        setFinishingTime(getOwner().getNextTime());
    }

    // Return an enumeration of all mappings for my descriptor, including those inherited
    public Vector withAllSubclasses(CommitOrderDependencyNode node) {
        Vector results = new Vector();
        results.addElement(node);

        if (node.getDescriptor().hasInheritance()) {
            InheritancePolicy policy = node.getDescriptor().getInheritancePolicy();

            // For bug 3019934 replace getChildDescriptors with getAllChildDescriptors.
            List<ClassDescriptor> childDescriptors = new ArrayList<ClassDescriptor>();
            childDescriptors.addAll(policy.getAllChildDescriptors());
            
            // Sort Child Descriptors before adding them to related nodes.
            Collections.sort(childDescriptors, new DescriptorCompare());
            
            for (ClassDescriptor child : childDescriptors) {
                results.add(getOwner().nodeFor(child));
            }
        }
        return results;
    }
}
