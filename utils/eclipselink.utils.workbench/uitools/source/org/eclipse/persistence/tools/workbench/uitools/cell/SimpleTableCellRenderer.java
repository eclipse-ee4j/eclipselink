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

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * This renderer should behave the same as the DefaultTableCellRenderer;
 * but it slightly refactors the calculation of the text of the table
 * cell so that subclasses can easily override the methods that build
 * the text. This renderer also allows subclasses to build an icon,
 * something DefaultTableCellRenderer does not support.
 * 
 * In most cases, you need only override:
 *     #buildIcon(Object value)
 *     #buildText(Object value)
 */
public class SimpleTableCellRenderer
	extends DefaultTableCellRenderer
{

	/**
	 * Construct a simple renderer.
	 */
	public SimpleTableCellRenderer() {
		super();
	}

	/**
	 * @see javax.swing.TableCellRenderer#getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	 */
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		// substitute null for the cell value so nothing is drawn initially...
		super.getTableCellRendererComponent(table, null, isSelected, hasFocus, row, column);

		// ...then set the icon and text manually
		this.setIcon(this.buildIcon(table, value, isSelected, hasFocus, row, column));
		this.setText(this.buildText(table, value, isSelected, hasFocus, row, column));

		this.setToolTipText(this.buildToolTipText(table, value, isSelected, hasFocus, row, column));

		// the context will be initialized only if a reader is running
		if (this.accessibleContext != null) {
			this.accessibleContext.setAccessibleName(this.buildAccessibleName(table, value, isSelected, hasFocus, row, column));
		}
		
		return this;
	}

	/**
	 * Return the icon representation of the specified cell
	 * value and other settings. (Even more settings are
	 * accessible via inherited getters: hasFocus, isEnabled, etc.)
	 */
	protected Icon buildIcon(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		return this.buildIcon(value);
	}

	/**
	 * Return the icon representation of the specified cell
	 * value. The default is to display no icon at all unless the
	 * value itself is an icon.
	 */
	protected Icon buildIcon(Object value) {
		// replicate the default behavior
		return (value instanceof Icon) ? (Icon) value : null;
	}

	/**
	 * Return the textual representation of the specified cell
	 * value and other settings. (Even more settings are
	 * accessible via inherited getters: hasFocus, isEnabled, etc.)
	 */
	protected String buildText(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		return this.buildText(value);
	}

	/**
	 * Return the textual representation of the specified cell
	 * value. The default is to display the object's default string
	 * representation (as returned by #toString()); unless the
	 * value itself is an icon, in which case no text is displayed.
	 */
	protected String buildText(Object value) {
		return (value instanceof Icon) ? "" : ((value == null) ? "" : value.toString());
	}

	/**
	 * Return the text displayed when the cursor lingers over the specified cell.
	 * (Even more settings are accessible via inherited getters: hasFocus, isEnabled, etc.)
	 */
	protected String buildToolTipText(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		return this.buildToolTipText(value);
	}

	/**
	 * Return the text displayed when the cursor lingers over the specified cell.
	 * (Even more settings are accessible via inherited getters: hasFocus, isEnabled, etc.)
	 */
	protected String buildToolTipText(Object value) {
		return null;
	}

	/**
	 * Return the accessible name to be given to the component used to render
	 * the given value and other settings. (Even more settings are accessible via
	 * inherited getters: hasFocus, isEnabled, etc.)
	 */
	protected String buildAccessibleName(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		return this.buildAccessibleName(value);
	}

	/**
	 * Return the accessible name to be given to the component used to render
	 * the given value.
	 */
	protected String buildAccessibleName(Object value) {
		return null;
	}

}
