/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.uitools.cell;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;

/**
 * This renderer delegates rendering to the appropriate node's renderer.
 *
 * @see RenderingNode
 */
public class NodeTreeCellRenderer implements TreeCellRenderer {

    /**
     * Default constructor.
     */
    public NodeTreeCellRenderer() {
        super();
    }

    /**
     * Delegate rendering to the node's renderer.
     * @see javax.swing.tree.TreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int, boolean)
     */
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        RenderingNode node = (RenderingNode) value;
        return node.getRenderer().getTreeCellRendererComponent(tree, node.getCellValue(), sel, expanded, leaf, row, hasFocus);
    }

}
