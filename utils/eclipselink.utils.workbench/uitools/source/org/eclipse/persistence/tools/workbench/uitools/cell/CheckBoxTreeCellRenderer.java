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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;

/**
 * Make the cell look like a check box.
 */
public class CheckBoxTreeCellRenderer 
	implements TreeCellEditorAdapter.Renderer 
{
	/** the component used to paint the cell */
	private JCheckBox checkBox;

	/** this is set to true when the renderer is used by an editor */
	protected boolean editing = false;
	
	/** the listener to be notified on an immediate edit */
	protected TreeCellEditorAdapter.ImmediateEditListener immediateEditListener;

	/** various borders (LTR = left-to-right; RTL = right-to-left */
	private static final Border NO_FOCUS_BORDER_LTR = BorderFactory.createEmptyBorder(2, 2, 2, 3);
	private static final Border NO_FOCUS_BORDER_RTL = BorderFactory.createEmptyBorder(2, 3, 2, 2);

	private static final Border INNER_FOCUS_BORDER_LTR = BorderFactory.createEmptyBorder(1, 1, 1, 2);
	private static final Border INNER_FOCUS_BORDER_RTL = BorderFactory.createEmptyBorder(1, 2, 1, 1);


	// ********** constructors/initialization **********

	/**
	 * Construct a cell renderer with no label or icon.
	 */
	public CheckBoxTreeCellRenderer() {
		super();
		this.initialize();
	}

	/**
	 * Construct a cell renderer with the specified text and icon,
	 * either of which may be null.
	 */
	public CheckBoxTreeCellRenderer(String text, Icon icon) {
		this();
		this.setText(text);
		this.setIcon(icon);
	}

	/**
	 * Construct a cell renderer with the specified text.
	 */
	public CheckBoxTreeCellRenderer(String text) {
		this(text, null);
	}

	/**
	 * Construct a cell renderer with the specified icon.
	 */
	public CheckBoxTreeCellRenderer(Icon icon) {
		this(null, icon);
	}

	protected void initialize() {
		this.checkBox = this.buildCheckBox();
		// by default, check boxes do not paint their borders
		this.checkBox.setBorderPainted(true);
		// this setting is recommended for check boxes inside of trees and tables
		this.checkBox.setBorderPaintedFlat(true);
	}

	protected JCheckBox buildCheckBox() {
		JCheckBox cb = new JCheckBox();
		cb.addActionListener(this.buildActionListener());
		return cb;
	}
	
	private ActionListener buildActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (CheckBoxTreeCellRenderer.this.immediateEditListener != null) {
					CheckBoxTreeCellRenderer.this.immediateEditListener.immediateEdit();
				}
			}
		};
	}

	/**
	 * Set whether the renderer is for an editor.
	 * This will cause a distinctive border to be drawn around the cell
	 * when it is being edited. It will also cause the component's
	 * default colors to be restored.
	 */
	protected void setEditing(boolean editing) {
		this.editing = editing;
	}


	// ********** TreeCellRenderer implementation **********

	/**
	 * @see javax.swing.tree.TreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int, boolean)
	 */
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
	    this.checkBox.setHorizontalAlignment(SwingConstants.LEADING);
		this.checkBox.setComponentOrientation(tree.getComponentOrientation());
		this.checkBox.setFont(tree.getFont());
		this.checkBox.setEnabled(tree.isEnabled());

		this.checkBox.setForeground(this.foregroundColor(tree, value, selected, expanded, leaf, row, hasFocus));
		this.checkBox.setBackground(this.backgroundColor(tree, value, selected, expanded, leaf, row, hasFocus));
		// once the colors are set, calculate opaque setting
		this.checkBox.setOpaque(this.cellIsOpaqueIn(tree, value, selected, expanded, leaf, row, hasFocus));
		this.checkBox.setBorder(this.border(tree, value, selected, expanded, leaf, row, hasFocus));

		this.setValue(value);
		return this.checkBox;
	}

	/**
	 * Return the cell's foreground color.
	 */
	protected Color foregroundColor(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		if (selected) {
			if (this.editing) {	// there is no way to tell, from the parms passed in, whether we are editing (?)
				return UIManager.getColor("Tree.textForeground");
			}
			return UIManager.getColor("Tree.selectionForeground");
		}
		return UIManager.getColor("Tree.textForeground");
	}

	/**
	 * Return the cell's background color.
	 */
	protected Color backgroundColor(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		if (selected) {
			if (this.editing) {	// there is no way to tell, from the parms passed in, whether we are editing (?)
				return UIManager.getColor("Tree.textBackground");
			}
			return UIManager.getColor("Tree.selectionBackground");
		}
		return UIManager.getColor("Tree.textBackground");
	}

	/**
	 * Return the cell's border.
	 * The border is 2 pixels on each side, except the "trailing" edge,
	 * which is 3 pixels.
	 */
	protected Border border(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		return hasFocus ? BorderFactory.createCompoundBorder(this.outerFocusBorder(), this.innerFocusBorder()) : this.noFocusBorder();
	}

	/**
	 * Return the cell's inner focus border, which is padding
	 * between the check box and the visible focus border.
	 * Make the trailing edge a pixel wider than the other sides.
	 */
	protected Border innerFocusBorder() {
		return this.checkBox.getComponentOrientation().isLeftToRight() ?
			INNER_FOCUS_BORDER_LTR
		:
			INNER_FOCUS_BORDER_RTL;
	}

	/**
	 * Return the cell's outer focus border,
	 * which is a single-pixel line border.
	 */
	protected Border outerFocusBorder() {
		return this.editing ?		// there is no way to tell, from the parms passed in, whether we are editing (?)
			UIManager.getBorder("Tree.editorBorder")
		:
			BorderFactory.createLineBorder(UIManager.getColor("Tree.selectionBorderColor"), 1);
		}

	/**
	 * Return the cell's no focus border.
	 * Make the trailing edge a pixel wider than the other sides.
	 */
	protected Border noFocusBorder() {
		return this.checkBox.getComponentOrientation().isLeftToRight() ?
			NO_FOCUS_BORDER_LTR
		:
			NO_FOCUS_BORDER_RTL;
	}

	/**
	 * Return whether the cell should be opaque in the tree.
	 * If the cell's background is the same as the tree's background
	 * and tree is opaque, we don't need to paint the background -
	 * the tree will do it.
	 */
	protected boolean cellIsOpaqueIn(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		Color cellBackground = this.checkBox.getBackground();
		Color treeBackground = tree.getBackground();
		return ! (tree.isOpaque() && cellBackground.equals(treeBackground));
	}

	/**
	 * Set the check box's value.
	 */
	protected void setValue(Object value) {
		this.checkBox.setSelected(((Boolean) value).booleanValue());
	}


	// ********** TreeCellEditorAdapter.Renderer implementation **********

	/**
	 * @see TreeCellEditorAdapter#getValue()
	 */
	public Object getValue() {
		return Boolean.valueOf(this.checkBox.isSelected());
	}
	
	/**
	 * @see TreeCellEditorAdapter#setImmediateEditListener(TreeCellEditorAdapter.ImmediateEditListener listener)
	 */
	public void setImmediateEditListener(TreeCellEditorAdapter.ImmediateEditListener listener) {
		this.immediateEditListener = listener;
	}
	
	
	// ********** public API **********

	/**
	 * Set the check box's text; which by default is blank.
	 */
	public void setText(String text) {
		this.checkBox.setText(text);
	}

	/**
	 * Set the check box's icon; which by default is not present.
	 */
	public void setIcon(Icon icon) {
		this.checkBox.setIcon(icon);
	}

	/**
	 * Return the renderer's preferred height. This allows you
	 * to set the tree's row height to something the check box
	 * will look good in....
	 */
	public int getPreferredHeight() {
		// add in space for the border top and bottom
		return (int) this.checkBox.getPreferredSize().getHeight() + 4;
	}


	// ********** API used by the cell editor **********

	protected void addActionListener(ActionListener listener) {
		this.checkBox.addActionListener(listener);
	}

}
