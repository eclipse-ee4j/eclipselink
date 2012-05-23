/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.uitools.cell;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JTree;
import javax.swing.tree.TreeCellEditor;

/**
 * A tree cell editor that acts like a combo-box embedded in the cell.
 */
public class ComboBoxTreeCellEditor extends AbstractCellEditor implements TreeCellEditor {

// TODO add hooks for "editable" combo-box - see javax.swing.DefaultCellEditor(JComboBox)

	/** delegate some behavior to a renderer */
	protected ComboBoxTreeCellRenderer renderer;


	// ********** constructors/initialization **********

	private ComboBoxTreeCellEditor() {
		super();
	}

	/**
	 * Construct a cell editor that behaves like a combo-box and
	 * looks like the specified renderer.
	 */
	public ComboBoxTreeCellEditor(ComboBoxTreeCellRenderer renderer) {
		this();
		this.initialize(renderer);
	}

	protected void initialize(ComboBoxTreeCellRenderer r) {
		this.renderer = r;
		r.setEditing(true);
		// listen to the combo-box so we know when to stop editing
		r.addActionListener(this.buildActionListener());
	}

	protected ActionListener buildActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// when the combo-box fires an action event, we stop editing
				ComboBoxTreeCellEditor.this.stopCellEditing();
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
	 * @see javax.swing.tree.TreeCellEditor#getTreeCellEditorComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int)
	 */
	public Component getTreeCellEditorComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row) {
		return this.renderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, true);
	}

}
