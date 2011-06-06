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

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * This renderer should behave the same as the DefaultTreeCellRenderer;
 * but it slightly refactors the calculation of the icon and text of the tree
 * cell so that subclasses can easily override the methods that build
 * the icon and text.
 * 
 * In most cases, you need only override:
 *     #buildIcon(Object value)
 *     #buildText(Object value)
 */
public class SimpleTreeCellRenderer
	extends DefaultTreeCellRenderer
{

	/**
	 * Construct a simple renderer.
	 */
	public SimpleTreeCellRenderer() {
		super();
	}

	/**
	 * @see javax.swing.tree.DefaultTreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree, Object, boolean, boolean, boolean, int, boolean)
	 */
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean cellHasFocus) {
		// substitute null for the cell value so an empty string is used for the text...
		super.getTreeCellRendererComponent(tree, null, sel, expanded, leaf, row, cellHasFocus);

		// ...then set the icon and text manually
		this.setIcon(this.buildIcon(tree, value, sel, expanded, leaf, row, cellHasFocus));
		this.setText(this.buildText(tree, value, sel, expanded, leaf, row, cellHasFocus));

		this.setToolTipText(this.buildToolTipText(tree, value, sel, expanded, leaf, row, cellHasFocus));

		// the context will be initialized only if a reader is running
		if (this.accessibleContext != null) {
			this.accessibleContext.setAccessibleName(this.buildAccessibleName(tree, value, sel, expanded, leaf, row, cellHasFocus));
		}

		return this;
	}

	/**
	 * Return the icon representation of the specified cell
	 * value and other settings. (Even more settings are
	 * accessible via inherited getters: hasFocus, isEnabled, etc.)
	 */
	protected Icon buildIcon(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean cellHasFocus) {
		return this.buildIcon(value);
	}

	/**
	 * Return the icon representation of the specified cell
	 * value. The default is to display the icon already calculated
	 * by the DefaultTreeCellRenderer (which ignores the cell value).
	 */
	protected Icon buildIcon(Object value) {
		// replicate the default behavior by simply returning the icon that
		// was already calculated by the superclass (irrespective of the cell value);
		// this is a bit hackish - but the default renderer is not very well factored...  ~bjv
		return this.getIcon();
	}

	/**
	 * Return the textual representation of the specified cell
	 * value and other settings. (Even more settings are
	 * accessible via inherited getters: hasFocus, isEnabled, etc.)
	 */
	protected String buildText(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean cellHasFocus) {
		return this.buildText(value);
	}

	/**
	 * Return the textual representation of the specified cell
	 * value. The default is to display the object's default string
	 * representation (as returned by #toString()).
	 */
	protected String buildText(Object value) {
		return (value == null) ? "" : value.toString();
	}

	/**
	 * Return the text displayed when the cursor lingers over the specified cell.
	 * (Even more settings are accessible via inherited getters: hasFocus, isEnabled, etc.)
	 */
	protected String buildToolTipText(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean cellHasFocus) {
		return this.buildToolTipText(value);
	}

	/**
	 * Return the text displayed when the cursor lingers over the specified cell.
	 */
	protected String buildToolTipText(Object value) {
		return null;
	}

	/**
	 * Returns a string that can add more description to the rendered object when
	 * the text is not sufficient, if <code>null</code> is returned, then the
	 * text is used as the accessible text.
	 */
	protected String buildAccessibleName(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean cellHasFocus) {
		return this.buildAccessibleName(value);
	}

	/**
	 * Returns a string that can add more description to the rendered object when
	 * the text is not sufficient, if <code>null</code> is returned, then the
	 * text is used as the accessible text.
	 */
	protected String buildAccessibleName(Object value) {
		return null;
	}

}
