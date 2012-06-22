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
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JTree;
import javax.swing.tree.TreeCellEditor;

/**
 * A tree cell editor that acts like a text field embedded in the cell.
 */
public class TextFieldTreeCellEditor extends AbstractCellEditor implements TreeCellEditor {

	/** delegate some behavior to a renderer */
	protected TextFieldTreeCellRenderer renderer;


	// ********** constructors/initialization **********

	private TextFieldTreeCellEditor() {
		super();
	}

	/**
	 * Construct a cell editor that behaves like a text field and
	 * looks like the specified renderer.
	 */
	public TextFieldTreeCellEditor(TextFieldTreeCellRenderer renderer) {
		this();
		this.initialize(renderer);
	}

	protected void initialize(TextFieldTreeCellRenderer r) {
		this.renderer = r;
		r.setEditing(true);
		// listen to the text field so we know when to stop editing
		r.addActionListener(this.buildActionListener());
	}

	protected ActionListener buildActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// when the text field fires an action event, we stop editing
				TextFieldTreeCellEditor.this.stopCellEditing();
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
			return ((MouseEvent) e).getClickCount() >= 2;	// force double-click
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
