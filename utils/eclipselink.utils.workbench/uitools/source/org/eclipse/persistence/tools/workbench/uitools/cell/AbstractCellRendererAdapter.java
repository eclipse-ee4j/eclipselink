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

/**
 * Simple template that allows selective overriding of the methods
 * required of a <code>CellRendererAdapter</code>.
 */
public abstract class AbstractCellRendererAdapter
	implements CellRendererAdapter
{

	/**
	 * @see CellRendererAdapter#buildIcon(Object)
	 */
	public Icon buildIcon(Object value) {
		return null;
	}
	
	/**
	 * @see CellRendererAdapter#buildText(Object)
	 */
	public String buildText(Object value) {
		return null;
	}

	/**
	 * @see CellRendererAdapter#buildToolTipText(Object)
	 */
	public String buildToolTipText(Object value) {
		return null;
	}

	/**
	 * @see CellRendererAdapter#buildAccessibleName(Object)
	 */
	public String buildAccessibleName(Object value) {
		return null;
	}

}
