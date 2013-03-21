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

import java.util.*;

/**
 * INTERNAL:
 */
public class HierarchyNode {
    //  the class that this node represents
    public String className;
    public HierarchyNode parent;
    public ArrayList children;

    /**
     * This member will hold the different definition types that should be implemented by the code generated children
     * Used mostly in CMP code generation
     */
    public ArrayList definitions;

    public HierarchyNode(String className) {
        this.className = className;
        this.children = new ArrayList();
        this.definitions = new ArrayList();
    }

    public void setParent(HierarchyNode parent) {
        this.parent = parent;
        this.parent.addChild(this);
    }

    public void addChild(HierarchyNode child) {
        if (!this.children.contains(child)) {
            this.children.add(child);
        }
    }

    public List getChildren() {
        return this.children;
    }

    public HierarchyNode getParent() {
        return this.parent;
    }

    public String getClassName() {
        return this.className;
    }

    public String toString() {
        String result = "HierarchyNode:\n\t" + className + "\n" + children + "\n end HierarchyNode\n";
        return result;
    }
}
