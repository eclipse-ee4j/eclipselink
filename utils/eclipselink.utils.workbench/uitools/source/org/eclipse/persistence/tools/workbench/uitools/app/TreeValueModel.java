/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.uitools.app;

/**
 * Extend ValueModel to allow the adding and
 * removing of nodes in a tree value.
 * Typically the value returned from #getValue()
 * will be an Iterator.
 */
public interface TreeValueModel extends ValueModel {

	/**
	 * Add the specified node to the tree value.
	 */
	void addNode(Object[] parentPath, Object node);

	/**
	 * Remove the specified node from the tree value.
	 */
	void removeNode(Object[] path);

}
