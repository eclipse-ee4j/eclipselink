/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.uitools.cell;

import javax.swing.tree.TreeCellEditor;

/**
 * Define the methods required of a tree node that can edit its value.
 */
public interface EditingNode extends RenderingNode {

    /**
     * Return the node's editor.
     */
    TreeCellEditor getEditor();

}
