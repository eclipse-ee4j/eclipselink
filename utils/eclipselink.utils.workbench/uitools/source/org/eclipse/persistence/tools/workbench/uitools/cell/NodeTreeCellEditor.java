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
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JTree;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreePath;

/**
 * This editor delegates editing to the appropriate node's editor.
 *
 * @see EditingNode
 */
public class NodeTreeCellEditor extends AbstractCellEditor implements TreeCellEditor, CellEditorListener {

    /** Store the node that is currently being edited. */
    private EditingNode currentNode;


    /**
     * Default constructor.
     */
    public NodeTreeCellEditor() {
        super();
    }


    // ********** CellEditor implementation **********

    /**
     * Delegate to the node's editor.
     * @see javax.swing.CellEditor#getCellEditorValue()
     */
    public Object getCellEditorValue() {
        return this.currentNode.getEditor().getCellEditorValue();
    }

    /**
     * Delegate to the node's editor.
     * @see javax.swing.CellEditor#isCellEditable(java.util.EventObject)
     */
    public boolean isCellEditable(EventObject e) {
        JTree tree = (JTree) e.getSource();
        TreePath path = null;

        if (e instanceof MouseEvent) {
            MouseEvent mouseEvent = (MouseEvent) e;
            path = tree.getPathForLocation(mouseEvent.getX(), mouseEvent.getY());
        }
        else {
            path = tree.getSelectionPath();
        }

        if (path == null) {
            return false;
        }

        EditingNode node = (EditingNode) path.getLastPathComponent();
        return node.getEditor().isCellEditable(e);
    }

    /**
     * Delegate to the node's editor.
     * @see javax.swing.CellEditor#shouldSelectCell(java.util.EventObject)
     */
    public boolean shouldSelectCell(EventObject e) {
        return this.currentNode.getEditor().shouldSelectCell(e);
    }

    /**
     * Delegate to the node's editor.
     * @see javax.swing.CellEditor#stopCellEditing()
     */
    public boolean stopCellEditing() {
        return this.currentNode.getEditor().stopCellEditing();
    }

    /**
     * Delegate to the node's editor.
     * @see javax.swing.CellEditor#cancelCellEditing()
     */
    public void cancelCellEditing() {
        this.currentNode.getEditor().cancelCellEditing();
    }


    // ********** TreeCellEditor implementation **********

    /**
     * Delegate to the node's editor.
     * @see javax.swing.tree.TreeCellEditor#getTreeCellEditorComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int)
     */
    public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
        if (this.currentNode != null) {
            this.currentNode.getEditor().removeCellEditorListener(this);
        }
        this.currentNode = (EditingNode) value;
        this.currentNode.getEditor().addCellEditorListener(this);
        return this.currentNode.getEditor().getTreeCellEditorComponent(tree, this.currentNode.getCellValue(), isSelected, expanded, leaf, row);
    }


    // ********** CellListener implementation **********

    /**
     * Forward the event.
     * @see javax.swing.event.CellEditorListener#editingCanceled(javax.swing.event.ChangeEvent)
     */
    public void editingCanceled(ChangeEvent e) {
        this.fireEditingCanceled();
    }

    /**
     * Forward the event.
     * @see javax.swing.event.CellEditorListener#editingStopped(javax.swing.event.ChangeEvent)
     */
    public void editingStopped(ChangeEvent e) {
        this.fireEditingStopped();
    }

}
