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

import javax.swing.Icon;

import org.eclipse.persistence.tools.workbench.uitools.Displayable;
import org.eclipse.persistence.tools.workbench.utility.node.Node;


/**
 * A <code>CellRendererAdapter</code> is used to adapt a cell value
 * to the adaptable renderers used in a List, Tree, Table, etc.
 * 
 * @see AdaptableListCellRenderer
 * @see AdaptableTableCellRenderer
 * @see AdaptableTreeCellRenderer
 */
public interface CellRendererAdapter {

	/**
	 * Return an icon that can be used to identify the specified value
	 * in a UI component that supports icons.
	 */
	Icon buildIcon(Object value);

	/**
	 * Return a string that can be used to identify the specified value
	 * in a textual UI setting (typically the object's name).
	 */
	String buildText(Object value);

	/**
	 * Return a string that can be used to more completely identify the
	 * specified value when the cursor hovers over it.
	 */
	String buildToolTipText(Object value);

	/**
	 * Return the accessible name to be given to the component used
	 * to render the specified value.
	 */
	String buildAccessibleName(Object value);


	// ********** Convenience implementations **********

	/**
	 * A default implementation of <code>CellRendererAdapter</code>.
	 */
	CellRendererAdapter DEFAULT_CELL_RENDERER_ADAPTER =
		new AbstractCellRendererAdapter() {
			public String buildText(Object object) {
				return String.valueOf(object);
			}
		};

	/**
	 * An implementation of <code>CellRendererAdapter</code> that
	 * assumes <code>Displayable</code> values.
	 */
	CellRendererAdapter DISPLAYABLE_CELL_RENDERER_ADAPTER =
		new AbstractCellRendererAdapter() {
			public Icon buildIcon(Object object) {
				return ((Displayable) object).icon();
			}
			public String buildText(Object object) {
				return ((Displayable) object).displayString();
			}
		};

	/**
	 * An implementation of <code>CellRendererAdapter</code> that
	 * assumes <code>Node</code> values.
	 */
	CellRendererAdapter NODE_CELL_RENDERER_ADAPTER =
		new AbstractCellRendererAdapter(){
			public String buildText(Object object) {
				return (object == null) ? null : ((Node) object).displayString();
			}
		};

}
