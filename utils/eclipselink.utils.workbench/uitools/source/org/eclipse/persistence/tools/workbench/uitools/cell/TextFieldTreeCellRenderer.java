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

import java.awt.Insets;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTextField;

/**
 * Make the cell look like a text field, with an optional label.
 */
public class TextFieldTreeCellRenderer extends LabeledTreeCellRenderer {


	// ********** constructors **********

	/**
	 * Construct a cell renderer that will display the specified text and icon
	 * in the text field's label.
	 */
	public TextFieldTreeCellRenderer(String labelText, Icon labelIcon) {
		super(labelText, labelIcon);
	}

	/**
	 * Construct a cell renderer that will display the specified text
	 * in the text field's label.
	 */
	public TextFieldTreeCellRenderer(String labelText) {
		super(labelText);
	}

	/**
	 * Construct a cell renderer that will display the specified icon
	 * in the text field's label.
	 */
	public TextFieldTreeCellRenderer(Icon labelIcon) {
		super(labelIcon);
	}

	/**
	 * Construct a cell renderer that has no label.
	 */
	public TextFieldTreeCellRenderer() {
		super();
	}


	// ********** initialization **********

	/**
	 * @see org.eclipse.persistence.tools.workbench.framework.tools.LabeledTreeCellRenderer#buildComponent()
	 */
	protected JComponent buildComponent() {
		JTextField textField = new JTextField();
		textField.setMargin(new Insets(0, 2, 0, 2));
		return textField;
	}


	// ********** TreeCellRenderer implementation **********

	/**
	 * Cast the component to something helpful.
	 */
	protected JTextField getTextField() {
		return (JTextField) this.component;
	}

	/**
	 * Set the text field's value.
	 */
	protected void setValue(Object value) {
		this.getTextField().setText(value.toString());
	}


	// ********** API used by the cell editor **********

	protected Object getValue() {
		return this.getTextField().getText();
	}

	protected void addActionListener(ActionListener listener) {
		this.getTextField().addActionListener(listener);
	}

}
