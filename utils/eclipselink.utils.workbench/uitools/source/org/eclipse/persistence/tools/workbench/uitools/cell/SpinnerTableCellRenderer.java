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
import java.awt.Component;
import java.text.ParseException;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Make the cell look like a spinner.
 */
public class SpinnerTableCellRenderer implements TableCellEditorAdapter.Renderer {

	/** the component used to paint the cell */
	protected JSpinner spinner;
	
	/** the listener to be notified on an immediate edit */
	protected TableCellEditorAdapter.ImmediateEditListener immediateEditListener;
	
	
	// ********** constructors/initialization **********

	/**
	 * Construct a cell renderer that uses the default
	 * spinner model, which is a "number" model.
	 */
	public SpinnerTableCellRenderer() {
		super();
		this.initialize();
	}

	/**
	 * Construct a cell renderer that uses the specified
	 * spinner model, which will determine how the values are displayed.
	 */
	public SpinnerTableCellRenderer(SpinnerModel model) {
		this();
		this.setModel(model);
	}

	protected void initialize() {
		this.spinner = this.buildSpinner();
	}

	protected JSpinner buildSpinner() {
		JSpinner s = new JSpinner();
		s.addChangeListener(this.buildChangeListener());
		return s;
	}
	
	private ChangeListener buildChangeListener() {
		return new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (SpinnerTableCellRenderer.this.immediateEditListener != null) {
					SpinnerTableCellRenderer.this.immediateEditListener.immediateEdit();
				}
			}
		};
	}


	// ********** TableCellRenderer implementation **********

	/**
	 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
	public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean hasFocus, int row, int column) {
		this.spinner.setComponentOrientation(table.getComponentOrientation());
		this.spinner.setFont(table.getFont());
		this.spinner.setEnabled(table.isEnabled());

		JComponent editor = this.editor();
		editor.setForeground(this.foregroundColor(table, value, selected, hasFocus, row, column));
		editor.setBackground(this.backgroundColor(table, value, selected, hasFocus, row, column));
		this.spinner.setBorder(this.border(table, value, selected, hasFocus, row, column));

		this.setValue(value);
		return this.spinner;
	}

	/**
	 * Return the cell's foreground color.
	 */
	protected Color foregroundColor(JTable table, Object value, boolean selected, boolean hasFocus, int row, int column) {
		if (selected) {
			if (hasFocus && table.isCellEditable(row, column)) {
				return UIManager.getColor("Table.focusCellForeground");
			}
			return table.getSelectionForeground();
		}
		return table.getForeground();
	}

	/**
	 * Return the cell's background color.
	 */
	protected Color backgroundColor(JTable table, Object value, boolean selected, boolean hasFocus, int row, int column) {
		if (selected) {
			if (hasFocus && table.isCellEditable(row, column)) {
				return UIManager.getColor("Table.focusCellBackground");
			}
			return table.getSelectionBackground();
		}
		return table.getBackground();
	}

	/**
	 * Return the cell's border.
	 */
	protected Border border(JTable table, Object value, boolean selected, boolean hasFocus, int row, int column) {
		if (hasFocus) {
			return UIManager.getBorder("Table.focusCellHighlightBorder");
		}
		if (selected) {
			return BorderFactory.createLineBorder(table.getSelectionBackground(), 1);
		}
		return BorderFactory.createLineBorder(table.getBackground(), 1);
	}

	/**
	 * Return the editor component whose colors should be set
	 * by the renderer.
	 */
	protected JComponent editor() {
		JComponent editor = this.spinner.getEditor();
		if (editor instanceof JSpinner.DefaultEditor) {
			// typically, the editor will be the default or one of its subclasses...
			editor = ((JSpinner.DefaultEditor) editor).getTextField();
		}
		return editor;
	}

	/**
	 * Set the spinner's value
	 */
	protected void setValue(Object value) {
		// CR#3999318 - This null check needs to be removed once JDK bug is fixed
		if (value == null) {
			value = new Integer(0);
		}
		this.spinner.setValue(value);
	}


	// ********** TableCellEditorAdapter.Renderer implementation **********

	/**
	 * @see TableCellEditorAdapter#getValue()
	 */
	public Object getValue() {
		return this.spinner.getValue();
	}
	
	/**
	 * @see TableCellEditorAdapter#setImmediateEditListener(TableCellEditorAdapter.ImmediateEditListener listener)
	 */
	public void setImmediateEditListener(TableCellEditorAdapter.ImmediateEditListener listener) {
		this.immediateEditListener = listener;
	}

	public void commit() {
		try {
			this.spinner.commitEdit();
		}
		catch (ParseException e) {
			// Ignore since the value isn't valid
		}
	}

	// ********** public API **********

	/**
	 * Set the spinner's model.
	 */
	public void setModel(SpinnerModel model) {
		this.spinner.setModel(model);
	}

	/**
	 * Return the renderer's preferred height. This allows you
	 * to set the row height to something the spinner will look good in....
	 */
	public int getPreferredHeight() {
		// add in space for the border top and bottom
		return (int) this.spinner.getPreferredSize().getHeight() + 2;
	}

}
