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
import javax.swing.AbstractCellEditor;
import javax.swing.JTree;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;

public class TreeCellEditorAdapter
    extends AbstractCellEditor
    implements TreeCellEditor
{
    /** delegate to a renderer */
    private Renderer renderer;


    // ********** constructors/initialization **********

    private TreeCellEditorAdapter() {
        super();
    }

    /**
     * Construct a cell editor that behaves like the specified renderer.
     */
    public TreeCellEditorAdapter(Renderer renderer) {
        this();
        this.initialize(renderer);
    }

    protected void initialize(Renderer r) {
        this.renderer = r;
        this.renderer.setImmediateEditListener(this.buildImmediateEditListener());
    }

    private ImmediateEditListener buildImmediateEditListener() {
        return new ImmediateEditListener() {
            public void immediateEdit() {
                TreeCellEditorAdapter.this.stopCellEditing();
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


    // ********** TreeCellEditor implementation **********

    /**
     * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
     */

    public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
        return this.renderer.getTreeCellRendererComponent(tree, value, isSelected, expanded, leaf, row, true);
    }


    // ********** Member classes **********************************************

    /**
     * This interface defines the methods that must be implemented by a renderer
     * that can be wrapped by a TreeCellEditorAdapter.
     */
    public interface Renderer
        extends TreeCellRenderer
    {
        /**
         * Return the current value of the renderer.
         */
        Object getValue();

        /**
         * Set the immediate edit listener
         */
        void setImmediateEditListener(ImmediateEditListener listener);
    }


    public interface ImmediateEditListener {

        /**
         * Called when the renderer does an "immediate edit"
         */
        void immediateEdit();
    }
}
