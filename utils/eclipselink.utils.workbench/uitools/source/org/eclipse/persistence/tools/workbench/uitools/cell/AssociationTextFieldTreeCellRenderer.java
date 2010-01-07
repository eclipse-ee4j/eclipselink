/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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

import javax.swing.Icon;

import org.eclipse.persistence.tools.workbench.utility.Association;


/**
 * Render a pair of strings held by an association.
 * The "key" string will used for the label text,
 * the "value" string will placed in the text field.
 * 
 * @see AssociationTextFieldTreeCellEditor
 */
// TODO Association [ CheckBox | ComboBox | Spinner ] TreeCell [ Renderer | Editor ]
public class AssociationTextFieldTreeCellRenderer extends TextFieldTreeCellRenderer {
	/** A key/value pair of strings. */
	private Association association;


	// ********** constructors **********

	/**
	 * Construct a cell renderer that will display the specified icon
	 * in the text field's label. The specified text will be ignored,
	 * but could be used by subclasses.
	 */
	public AssociationTextFieldTreeCellRenderer(String labelText, Icon labelIcon) {
		super(labelText, labelIcon);
	}

	/**
	 * Construct a cell renderer whose label will be determined by
	 * the node's association. The specified text will be ignored,
	 * but could be used by subclasses.
	 */
	public AssociationTextFieldTreeCellRenderer(String labelText) {
		super(labelText);
	}

	/**
	 * Construct a cell renderer that will display the specified icon
	 * in the text field's label.
	 */
	public AssociationTextFieldTreeCellRenderer(Icon labelIcon) {
		super(labelIcon);
	}

	/**
	 * Construct a cell renderer whose label will be determined by
	 * the node's association.
	 */
	public AssociationTextFieldTreeCellRenderer() {
		super();
	}


	// ********** API used by the cell editor **********

	protected Object getValue() {
		return this.association;
	}

	protected void updateAssociationValue() {
		this.association.setValue(this.getTextField().getText());
	}


	// ********** TreeCellRenderer implementation **********

	/**
	 * The label is determined by the association's key.
	 */
	protected void setValue(Object value) {
		this.association = (Association) value;
		this.setLabelText(this.buildLabelText(value));
		super.setValue(this.association.getValue());
	}

	protected String buildLabelText(Object value) {
		return (String) ((Association) value).getKey();
	}

}
