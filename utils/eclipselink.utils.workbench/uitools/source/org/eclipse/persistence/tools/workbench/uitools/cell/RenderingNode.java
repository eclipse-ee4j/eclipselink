/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.uitools.cell;

import javax.swing.tree.TreeCellRenderer;

/**
 * Define the methods required of a tree node that can render its value.
 */
public interface RenderingNode {

	/**
	 * Return the node's value used for rendering and editing.
	 */
	Object getCellValue();

	/**
	 * Return the node's renderer.
	 */
	TreeCellRenderer getRenderer();

}
