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

import java.awt.Color;
import java.awt.event.ActionListener;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Make the cell look like a spinner, with an optional label.
 */
public class SpinnerTreeCellRenderer
	extends LabeledTreeCellRenderer
	implements TreeCellEditorAdapter.Renderer
{
	/** the listener to be notified on an immediate edit */
	protected TreeCellEditorAdapter.ImmediateEditListener immediateEditListener;
	
	
	// ********** constructors **********

	/**
	 * Construct a cell renderer that will display the specified text and icon
	 * in the spinner's label and uses the default spinner model,
	 * which is a "number" model.
	 */
	public SpinnerTreeCellRenderer(String labelText, Icon labelIcon) {
		super(labelText, labelIcon);
	}

	/**
	 * Construct a cell renderer that will display the specified text
	 * in the spinner's label and uses the default spinner model,
	 * which is a "number" model.
	 */
	public SpinnerTreeCellRenderer(String labelText) {
		super(labelText);
	}

	/**
	 * Construct a cell renderer that will display the specified icon
	 * in the spinner's label and uses the default spinner model,
	 * which is a "number" model.
	 */
	public SpinnerTreeCellRenderer(Icon labelIcon) {
		super(labelIcon);
	}

	/**
	 * Construct a cell renderer that has no label and
	 * uses the default spinner model, which is a "number" model.
	 */
	public SpinnerTreeCellRenderer() {
		super();
	}

	/**
	 * Construct a cell renderer that will display the specified text
	 * in the spinner's label and uses the specified spinner model,
	 * which will determine how the values are displayed.
	 */
	public SpinnerTreeCellRenderer(String labelText, SpinnerModel model) {
		this(labelText);
		this.setModel(model);
	}

	/**
	 * Construct a cell renderer that has no label and uses the specified
	 * spinner model, which will determine how the values are displayed.
	 */
	public SpinnerTreeCellRenderer(SpinnerModel model) {
		this();
		this.setModel(model);
	}


	// ********** initialization **********

	protected JComponent buildComponent() {
		JSpinner spinner = new JSpinner();
		spinner.addChangeListener(this.buildChangeListener());
		return spinner;
	}
	
	private ChangeListener buildChangeListener() {
		return new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (SpinnerTreeCellRenderer.this.immediateEditListener != null) {
					SpinnerTreeCellRenderer.this.immediateEditListener.immediateEdit();
				}
			}
		};
	}


	// ********** TreeCellRenderer implementation **********

	/**
	 * Cast the component to something helpful.
	 */
	protected JSpinner getSpinner() {
		return (JSpinner) this.component;
	}

	/**
	 * Return the editor component whose colors should be set
	 * by the renderer.
	 */
	protected JComponent editor() {
		JComponent editor = this.getSpinner().getEditor();
		if (editor instanceof JSpinner.DefaultEditor) {
			// typically, the editor will be the default or one of its subclasses...
			editor = ((JSpinner.DefaultEditor) editor).getTextField();
		}
		return editor;
	}

	/**
	 * Tweak the color of the spinner editor since it does not
	 * "inherit" the colors of its container.
	 */
	protected void setForeground(Color c) {
		super.setForeground(c);
		this.editor().setForeground(c);
	}

	/**
	 * Tweak the color of the spinner editor since it does not
	 * "inherit" the colors of its container.
	 */
	protected void setBackground(Color c) {
		super.setBackground(c);
		this.editor().setBackground(c);
	}

	protected void setValue(Object value) {
		this.getSpinner().setValue(value);
	}


	// ********** TreeCellEditorAdapter.Renderer implementation **********
	
	/**
	 * @see TreeCellEditorAdapter#getValue()
	 */
	public Object getValue() {
		return this.getSpinner().getValue();
	}
	
	/**
	 * @see TreeCellEditorAdapter#setImmediateEditListener(TreeCellEditorAdapter.ImmediateEditListener)
	 */
	public void setImmediateEditListener(TreeCellEditorAdapter.ImmediateEditListener listener) {
		this.immediateEditListener = listener;
	}


	// ********** public API **********

	/**
	 * Set the spinner's model.
	 */
	public void setModel(SpinnerModel model) {
		this.getSpinner().setModel(model);
	}


	// ********** API used by the cell editor **********

	protected void addActionListener(ActionListener listener) {
		JComponent editor = this.editor();
		if (editor instanceof JTextField) {
			((JTextField) editor).addActionListener(listener);
		} else {
			throw new UnsupportedOperationException(editor.getClass().getName());
		}
	}

}
