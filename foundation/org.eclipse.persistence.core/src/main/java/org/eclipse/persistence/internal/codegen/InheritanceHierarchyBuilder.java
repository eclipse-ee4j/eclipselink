/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.codegen;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.sessions.Project;

import java.util.HashMap;
import java.util.Map;

/**
 * INTERNAL:
 */
public class InheritanceHierarchyBuilder {

    private InheritanceHierarchyBuilder() {
        //no instance please
    }

    /**
     * INTERNAL:
     * Based on a class name either return a pre-existing node from the hierarchyTree or build one and
     * add it to the tree.
     */
    public static HierarchyNode getNodeForClass(String className, Map<String, HierarchyNode> hierarchyTree) {
        HierarchyNode node = hierarchyTree.get(className);
        if (node == null) {
            node = new HierarchyNode(className);
            hierarchyTree.put(className, node);
        }
        return node;
    }

    public static Map<String, HierarchyNode> buildInheritanceHierarchyTree(Project project) {
        Map<Class<?>, ClassDescriptor> descriptors = project.getDescriptors();
        Map<String, HierarchyNode> hierarchyTree = new HashMap<>(descriptors.size());
        for (ClassDescriptor descriptor : descriptors.values()) {
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
