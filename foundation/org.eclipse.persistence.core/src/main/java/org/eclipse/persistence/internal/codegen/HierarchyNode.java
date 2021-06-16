/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
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

    @Override
    public String toString() {
        String result = "HierarchyNode:\n\t" + className + "\n" + children + "\n end HierarchyNode\n";
        return result;
    }
}
