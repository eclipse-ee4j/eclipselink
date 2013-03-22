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
package org.eclipse.persistence.internal.codegen;

import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.descriptors.ClassDescriptor;

import java.util.*;

/**
 * INTERNAL:
 */
public class InheritanceHierarchyBuilder {

    /**
     * INTERNAL:
     * Based on a class name either return a pre-existing node from the hierarchyTree or build one and
     * add it to the tree.
     */
    public static HierarchyNode getNodeForClass(String className, Hashtable hierarchyTree) {
        HierarchyNode node = (HierarchyNode)hierarchyTree.get(className);
        if (node == null) {
            node = new HierarchyNode(className);
            hierarchyTree.put(className, node);
        }
        return node;
    }

    public static Hashtable buildInheritanceHierarchyTree(Project project) {
        Map descriptors = project.getDescriptors();
        Hashtable hierarchyTree = new Hashtable(descriptors.size());
        for (Iterator descriptorIterator = descriptors.values().iterator();
                 descriptorIterator.hasNext();) {
            ClassDescriptor descriptor = (ClassDescriptor)descriptorIterator.next();
            String className = descriptor.getJavaClassName();
            if (className == null) {
                className = descriptor.getJavaClass().getName();
            }
            HierarchyNode node = getNodeForClass(className, hierarchyTree);
            if (descriptor.hasInheritance() && (descriptor.getInheritancePolicy().getParentClassName() != null)) {
                HierarchyNode parentNode = getNodeForClass(descriptor.getInheritancePolicy().getParentClassName(), hierarchyTree);
                node.setParent(parentNode);
            }
        }
        return hierarchyTree;
    }
}
