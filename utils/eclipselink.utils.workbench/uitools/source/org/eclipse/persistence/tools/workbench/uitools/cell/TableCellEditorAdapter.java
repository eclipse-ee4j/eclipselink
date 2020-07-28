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
import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 * A table cell editor that wraps a table cell renderer.
 */
public class TableCellEditorAdapter extends AbstractCellEditor implements TableCellEditor {

    /** delegate to a renderer */
    private Renderer renderer;


    // ********** constructors/initialization **********

    private TableCellEditorAdapter() {
        super();
    }

    /**
     * Construct a cell editor that behaves like the specified renderer.
     */
    public TableCellEditorAdapter(Renderer renderer) {
        this();
        this.initialize(renderer);
    }

    protected void initialize(Renderer r) {
        this.renderer = r;
        r.setImmediateEditListener(this.buildImmediateEditListener());
    }

    private ImmediateEditListener buildImmediateEditListener() {
        return new ImmediateEditListener() {
            public void immediateEdit() {
                TableCellEditorAdapter.this.stopCellEditing();
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


    // ********** TableCellEditor implementation **********

    /**
     * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
     */
    public Component getTableCellEditorComponent(JTable table, Object value, boolean selected, int row, int column) {
        return this.renderer.getTableCellRendererComponent(table, value, selected, true, row, column);
    }

    @Override
    public boolean stopCellEditing() {
        this.renderer.commit();
        return super.stopCellEditing();
    }

    // ********** Member classes **********************************************

    /**
     * This interface defines the methods that must be implemented by a renderer
     * that can be wrapped by a TableCellEditorAdapter.
     */
    public interface Renderer extends TableCellRenderer {

        /**
         * Return the current value of the renderer.
         */
        Object getValue();

        /**
         * Set the immediate edit listener
         */
        void setImmediateEditListener(ImmediateEditListener listener);

        /**
         * This can be used by an editor to tell the renderer to commit the value
         * into the model.
         */
        void commit();
    }


    public interface ImmediateEditListener {

        /**
         * Called when the renderer does an "immediate edit"
         */
        void immediateEdit();
    }
}
