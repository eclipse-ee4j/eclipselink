/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ComboBoxModel;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

/**
 * Make the cell look like a combo-box, with an optional label.
 */
public class ComboBoxTreeCellRenderer 
	extends LabeledTreeCellRenderer
	implements TreeCellEditorAdapter.Renderer
{
	/** the listener to be notified on an immediate edit */
	protected TreeCellEditorAdapter.ImmediateEditListener immediateEditListener;
	
	/** hold the original colors of the combo-box */
	protected Color defaultComboBoxForeground;
	protected Color defaultComboBoxBackground;
	
	

	// ********** constructors **********

	/**
	 * Construct a cell renderer that will display the specified text and icon
	 * in the combo-box's label.
	 */
	public ComboBoxTreeCellRenderer(ComboBoxModel model, String labelText, Icon labelIcon) {
		super(labelText, labelIcon);
		this.setModel(model);
	}

	/**
	 * Construct a cell renderer that will display the specified text
	 * in the combo-box's label.
	 */
	public ComboBoxTreeCellRenderer(ComboBoxModel model, String labelText) {
		super(labelText);
		this.setModel(model);
	}

	/**
	 * Construct a cell renderer that will display the specified icon
	 * in the combo-box's label.
	 */
	public ComboBoxTreeCellRenderer(ComboBoxModel model, Icon labelIcon) {
		super(labelIcon);
		this.setModel(model);
	}

	/**
	 * Construct a cell renderer that has no label and uses the default
	 * combo-box renderer to draw the values,
	 * which calls the values' #toString() methods.
	 */
	public ComboBoxTreeCellRenderer(ComboBoxModel model) {
		super();
		this.setModel(model);
	}

	/**
	 * Construct a cell renderer that will display the specified text and icon
	 * in the combo-box's label and uses the specified
	 * combo-box renderer to draw the values.
	 */
	public ComboBoxTreeCellRenderer(ComboBoxModel model, String labelText, Icon labelIcon, ListCellRenderer renderer) {
		this(model, labelText, labelIcon);
		this.setRenderer(renderer);
	}

	/**
	 * Construct a cell renderer that has no label and uses the specified
	 * combo-box renderer to draw the selected value.
	 */
	public ComboBoxTreeCellRenderer(ComboBoxModel model, ListCellRenderer renderer) {
		this(model);
		this.setRenderer(renderer);
	}


	// ********** initialization **********

	protected JComponent buildComponent() {
		JComboBox comboBox = new JComboBox();
		// see javax.swing.DefaultCellEditor for usage
		comboBox.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
		comboBox.addActionListener(this.buildActionListener());

		// save the original colors of the combo-box, so we
		// can use them to paint non-selected cells
		this.defaultComboBoxForeground = comboBox.getForeground();
		this.defaultComboBoxBackground = comboBox.getBackground();

		return comboBox;
	}
	
	private ActionListener buildActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JComboBox comboBox = (JComboBox) e.getSource();
				Object selectedItem = comboBox.getSelectedItem();

				// Only update the selected item and invoke immediateEdit() if the
				// selected item actually changed, during the initialization of the
				// editing, the model changes and causes this method to be invoked,
				// it causes CR#3963675 to occur because immediateEdit() stop the
				// editing, which is done at the wrong time
				if (ComboBoxTreeCellRenderer.this.getValue() != selectedItem)
				{
					ComboBoxTreeCellRenderer.this.setValue(comboBox.getSelectedItem());
					ComboBoxTreeCellRenderer.this.immediateEdit();
				}
			}
		};
	}

	void immediateEdit() {
		if (this.immediateEditListener != null) {
			this.immediateEditListener.immediateEdit();
		}
	}


	// ********** TreeCellRenderer implementation **********

	/**
	 * Cast the component to something helpful.
	 */
	protected JComboBox getComboBox() {
		return (JComboBox) this.component;
	}

	/**
	 * Tweak the combo-box colors.
	 */
	protected void setForeground(Color c) {
		super.setForeground(c);
		if (c == UIManager.getColor("Tree.textForeground")) {
			this.component.setForeground(this.defaultComboBoxForeground);
		}
	}

	/**
	 * Tweak the combo-box colors.
	 */
	protected void setBackground(Color c) {
		super.setBackground(c);
		if (c == UIManager.getColor("Tree.textBackground")) {
			this.component.setBackground(this.defaultComboBoxBackground);
		}
	}

	/**
	 * Set the value of the combo-box.
	 */
	protected void setValue(Object value) {
		this.getComboBox().setSelectedItem(value);
	}


	// ********** TableCellEditorAdapter.Renderer implementation **********

	/**
	 * @see TreeCellEditorAdapter#getValue()
	 */
	public Object getValue() {
		return this.getComboBox().getSelectedItem();
	}
	
	/**
	 * @see TreeCellEditorAdapter#setImmediateEditListener(TreeCellEditorAdapter.ImmediateEditListener)
	 */
	public void setImmediateEditListener(TreeCellEditorAdapter.ImmediateEditListener listener) {
		this.immediateEditListener = listener;
	}


	// ********** public API **********

	/**
	 * Set the model used by the combo-box.
	 */
	public void setModel(ComboBoxModel model) {
		this.getComboBox().setModel(model);
	}

	/**
	 * Set the renderer used by the combo-box to draw the selected value.
	 * The default renderer calls the value's #toString() method.
	 */
	public void setRenderer(ListCellRenderer renderer) {
		this.getComboBox().setRenderer(renderer);
	}


	// ********** API used by the cell editor **********

	protected void addActionListener(ActionListener listener) {
		this.getComboBox().addActionListener(listener);
	}

}
