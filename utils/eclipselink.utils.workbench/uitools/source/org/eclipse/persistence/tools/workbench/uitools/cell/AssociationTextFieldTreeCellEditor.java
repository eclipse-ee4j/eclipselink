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

/**
 * Edit a pair of strings held by an association.
 * The "key" string will used for the label text,
 * the "value" string will placed in the text field.
 * 
 * @see AssociationTextFieldTreeCellRenderer
 */
public class AssociationTextFieldTreeCellEditor extends TextFieldTreeCellEditor {

	/**
	 * Construct a cell editor that behaves like a text field and
	 * looks like the specified renderer.
	 */
	public AssociationTextFieldTreeCellEditor(AssociationTextFieldTreeCellRenderer renderer) {
		super(renderer);
	}

	/**
	 * Extend to notify the renderer that editing has stopped
	 * and the association's value should be updated with what
	 * is currently in the text editor.
	 */
	public boolean stopCellEditing() {
		((AssociationTextFieldTreeCellRenderer) this.renderer).updateAssociationValue();
		return super.stopCellEditing();
	}

}
