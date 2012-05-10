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
package org.eclipse.persistence.tools.workbench.framework;

import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.uitools.app.TreeNodeValueModel;


/**
 * The node manager handles all the nodes in the application.
 */
public interface NodeManager {

	/**
	 * Add the specified "project-level" node to the application.
	 */
	void addProjectNode(ApplicationNode node);

	/**
	 * Remove the specified "project-level" node from the application.
	 */
	void removeProjectNode(ApplicationNode node);

	/**
	 * Save the specified node and, if it was saved successfully,
	 * add it to the recent files list. Return whether the node was saved.
	 */
	public boolean save(ApplicationNode node, WorkbenchContext workbenchContext);

	/**
	 * Return all the "project-level" nodes for the specified plug-in.
	 */
	ApplicationNode[] projectNodesFor(Plugin plugin);

	/**
	 * Return the "root" node that holds all the "project-level" nodes.
	 */
	TreeNodeValueModel getRootNode(); 

}
