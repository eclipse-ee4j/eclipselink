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
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.tree.TreeCellRenderer;

/**
 * Make the cell look like a component, with an optional label.
 * This is useful for components that do not have a label and
 * need one when acting as a leaf on a tree, where there are
 * no headers like a table.
 * 
 * Subclasses must implement:
 * #buildComponent()
 * 	build and return the component to be placed next to the label
 * #setValue(Object)
 * 	set value of the component appropriately
 */
public abstract class LabeledTreeCellRenderer implements TreeCellRenderer {

	/** the components used to paint the cell */
	private JPanel panel;
	private JLabel label;
	protected JComponent component;	// built by concrete subclass

	/** this is set to true when the renderer is used by an editor */
	protected boolean editing = false;

	/** "normal" border */
	private static final Border NO_FOCUS_BORDER = BorderFactory.createEmptyBorder(1, 1, 1, 1);


	// ********** constructors **********

	/**
	 * Construct a cell renderer that will display the specified text and icon
	 * in the component's label.
	 */
	public LabeledTreeCellRenderer(String labelText, Icon labelIcon) {
		super();
		this.initialize(labelText, labelIcon);
	}

	/**
	 * Construct a cell renderer that will display the specified text
	 * in the component's label.
	 */
	public LabeledTreeCellRenderer(String labelText) {
		this(labelText, (Icon) null);
	}

	/**
	 * Construct a cell renderer that will display the specified icon
	 * in the component's label.
	 */
	public LabeledTreeCellRenderer(Icon labelIcon) {
		this((String) null, labelIcon);
	}

	/**
	 * Construct a cell renderer that has no (visible) label.
	 */
	public LabeledTreeCellRenderer() {
		this((String) null, (Icon) null);
	}


	// ********** initialization **********

	protected void initialize(String labelText, Icon labelIcon) {
		this.panel = this.buildPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();

		// label
		this.label = this.buildLabel(labelText, labelIcon);
		constraints.gridx		= 0;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.BOTH;
		constraints.anchor		= GridBagConstraints.PAGE_START;
		constraints.insets		= new Insets(0, 0, 0, 5);
		this.panel.add(this.label, constraints);

		// component
		this.component = this.buildComponent();
		constraints.gridx		= 1;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.BOTH;
		constraints.anchor		= GridBagConstraints.PAGE_START;
		constraints.insets		= new Insets(0, 0, 0, 0);
		this.panel.add(this.component, constraints);
	}

	protected JPanel buildPanel(LayoutManager layoutManager) {
		return new JPanel(layoutManager);
	}

	protected JLabel buildLabel(String labelText, Icon labelIcon) {
		return new JLabel(labelText, labelIcon, SwingConstants.LEADING);
	}

	protected abstract JComponent buildComponent();

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
		this.setComponentOrientation(tree.getComponentOrientation());
		this.setFont(tree.getFont());
		this.setEnabled(tree.isEnabled());

		this.setForeground(this.foregroundColor(tree, value, selected, expanded, leaf, row, hasFocus));
		this.setBackground(this.backgroundColor(tree, value, selected, expanded, leaf, row, hasFocus));
		this.panel.setBorder(this.border(tree, value, selected, expanded, leaf, row, hasFocus));
		// once the colors are set, calculate opaque setting
		this.setOpaque(this.cellIsOpaqueIn(tree, value, selected, expanded, leaf, row, hasFocus));

		this.setValue(value);
		return this.panel;
	}

	/**
	 * Set the orientation for the all the components.
	 */
	protected void setComponentOrientation(ComponentOrientation o) {
		this.panel.setComponentOrientation(o);
		this.label.setComponentOrientation(o);
		this.component.setComponentOrientation(o);
	}

	/**
	 * Set the font for the all the components.
	 */
	protected void setFont(Font f) {
		this.panel.setFont(f);
		this.label.setFont(f);
		this.component.setFont(f);
	}

	/**
	 * Set the enabled state for the all the components.
	 */
	protected void setEnabled(boolean enabled) {
		this.panel.setEnabled(enabled);
		this.label.setEnabled(enabled);
		this.component.setEnabled(enabled);
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
	 * Set the foreground color for the all the components.
	 */
	protected void setForeground(Color c) {
		this.panel.setForeground(c);
		this.label.setForeground(c);
		this.component.setForeground(c);
	}

	/**
	 * Set the background color for the all the components.
	 */
	protected void setBackground(Color c) {
		this.panel.setBackground(c);
		this.label.setBackground(c);
		this.component.setBackground(c);
	}

	/**
	 * Return the cell's border.
	 */
	protected Border border(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		if (hasFocus) {
			if (this.editing) {	// there is no way to tell, from the parms passed in, whether we are editing (?)
				return UIManager.getBorder("Tree.editorBorder");
			}
			return BorderFactory.createLineBorder(UIManager.getColor("Tree.selectionBorderColor"), 1);
		}
		return NO_FOCUS_BORDER;
	}

	/**
	 * Return whether the cell should be opaque in the tree.
	 * If the cell's background is the same as the tree's background
	 * and tree is opaque, we don't need to paint the background -
	 * the tree will do it.
	 */
	protected boolean cellIsOpaqueIn(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		Color cellBackground = this.panel.getBackground();
		Color treeBackground = tree.getBackground();
		return ! (tree.isOpaque() && cellBackground.equals(treeBackground));
	}

	/**
	 * Set whether the cell is opaque.
	 */
	protected void setOpaque(boolean opaque) {
		this.panel.setOpaque(opaque);
	}

	/**
	 * Set the component's value.
	 */
	protected abstract void setValue(Object value);


	// ********** public API **********

	/**
	 * Set the text displayed by the component's label.
	 */
	public void setLabelText(String labelText) {
		this.label.setText(labelText);
	}

	/**
	 * Set the icon displayed by the component's label.
	 */
	public void setLabelIcon(Icon labelIcon) {
		this.label.setIcon(labelIcon);
	}

	/**
	 * Return the renderer's preferred height. This allows you
	 * to set the row height to something the component will look good in....
	 */
	public int getPreferredHeight() {
		// add in space for the border top and bottom
		return (int) this.panel.getPreferredSize().getHeight() + 2;
	}
	
	public void setPreferredWidth(int width) {
		this.panel.setPreferredSize(new Dimension(width, this.panel.getPreferredSize().height));
	}
}
