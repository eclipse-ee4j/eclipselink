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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JTree;
import javax.swing.tree.TreeCellEditor;

/**
 * A tree cell editor that acts like a spinner embedded in the cell.
 *
 * As of JDK 1.4.2 the javax.swing.DefaultCellEditor does
 * not provide support for a JSpinner.
 */
public class SpinnerTreeCellEditor extends AbstractCellEditor implements TreeCellEditor {

    /** delegate some behavior to a renderer */
    protected SpinnerTreeCellRenderer renderer;


    // ********** constructors/initialization **********

    private SpinnerTreeCellEditor() {
        super();
    }

    /**
     * Construct a cell editor that behaves like a spinner and
     * looks like the specified renderer.
     */
    public SpinnerTreeCellEditor(SpinnerTreeCellRenderer renderer) {
        this();
        this.initialize(renderer);
    }

    protected void initialize(SpinnerTreeCellRenderer r) {
        this.renderer = r;
        r.setEditing(true);
        // listen to the spinner so we know when to stop editing
        r.addActionListener(this.buildActionListener());
    }

    protected ActionListener buildActionListener() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // when the spinner's text field fires an action event, we stop editing
                SpinnerTreeCellEditor.this.stopCellEditing();
            }
        };
    }

    // ********** CellEditor implementation **********

    /**
     * @see javax.swing.CellEditor#getCellEditorValue()
     */
    public Object getCellEditorValue() {
        return this.renderer.getValue();
    }

    /**
     * @see javax.swing.CellEditor#isCellEditable(java.util.EventObject)
     */
    public boolean isCellEditable(EventObject e) {
        if (e instanceof MouseEvent) {
            return ((MouseEvent) e).getClickCount() >= 2;    // force double-click
        }
        return super.isCellEditable(e);
    }


    // ********** TreeCellEditor implementation **********

    /**
     * @see javax.swing.tree.TreeCellEditor#getTreeCellEditorComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int)
     */
    public Component getTreeCellEditorComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row) {
        return this.renderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, true);
    }

}
