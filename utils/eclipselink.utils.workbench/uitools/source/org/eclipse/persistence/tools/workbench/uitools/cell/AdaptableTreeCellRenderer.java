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

/**
 * Delegate rendering to an adapter.
 */
public class AdaptableTreeCellRenderer
	extends SimpleTreeCellRenderer
{
	private CellRendererAdapter adapter;

	/**
	 * Construct a renderer with the specified adapter.
	 */
	public AdaptableTreeCellRenderer(CellRendererAdapter adapter) {
		super();
		if (adapter == null) {
			throw new NullPointerException();
		}
		this.adapter = adapter;
	}

	protected Icon buildIcon(Object value) {
		return this.adapter.buildIcon(value);
	}

	protected String buildText(Object value) {
		return this.adapter.buildText(value);
	}

	protected String buildToolTipText(Object value) {
		return this.adapter.buildToolTipText(value);
	}

	protected String buildAccessibleName(Object value) {
		return this.adapter.buildAccessibleName(value);
	}

}
