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
