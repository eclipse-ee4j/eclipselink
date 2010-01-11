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

import org.eclipse.persistence.tools.workbench.uitools.Displayable;


/**
 * This renderer assumes that the table cell values
 * implement the Displayable interface.
 */
public class DisplayableTableCellRenderer extends SimpleTableCellRenderer {

	/**
	 * Construct a renderer that handles Displayables.
	 */
	public DisplayableTableCellRenderer() {
		super();
	}

	/**
	 * Cast the value to Displayable and return its icon.
	 */
	protected Icon buildIcon(Object value) {
		return ((Displayable) value).icon();
	}

	/**
	 * Cast the value to Displayable and return its display string.
	 */
	protected String buildText(Object value) {
		return ((Displayable) value).displayString();
	}

}
