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
package org.eclipse.persistence.tools.workbench.framework.app;

import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

/**
 * This interface defines the methods that can be called by the various
 * framework clients. It is a combination of framework-specific methods
 * and a subset of the methods declared in the Swing TreeSelectionModel
 * interface.
 * 
 * @see NavigatorView.LocalTreeSelectionModel
 */
public interface NavigatorSelectionModel {

	/**
	 * Return the set of application nodes that are currently
	 * selected in the navigator.
	 * Return an empty array if no nodes are selected.
	 */
	ApplicationNode[] getSelectedNodes();

	/**
	 * Return the set of "project" application nodes
	 * corresponding to the application nodes that are currently
	 * selected in the navigator.
	 * Return an empty array if no nodes are selected.
	 */
	ApplicationNode[] getSelectedProjectNodes();

	/**
	 * Set the navigator's selected node.
	 */
	void setSelectedNode(ApplicationNode node);

	/**
	 * Expand the specified node.
	 */
	void expandNode(ApplicationNode node);

	/**
	 * Push the navigator's current expansion state (a Collection of
	 * TreePaths) onto a stack.
	 * The client calling this method must later restore the expansion
	 * state by calling #popAndRestoreExpansionState().
	 * This will keep the stack in a consistent state.
	 */
	void pushExpansionState();

	/**
	 * Pop the tree's expansion state (a Collection of TreePaths) off a stack.
	 * This should only be called by a client that has previously called
	 * #pushExpansionState(). Otherwise, the stack will be corrupted
	 * or, if the stack is empty, an exception will be thrown.
	 */
	void popAndRestoreExpansionState();

	/**
	 * Similar to popAndRestoreExpasionState(), should be used when a node is replaced. 
	 */
	void popAndRestoreExpansionState(ApplicationNode oldNode, ApplicationNode morphedNode);

	// ********** subset of TreeSelectionModel protocol **********

	/**
	 * @see javax.swing.tree.TreeSelectionModel#getSelectionPaths()
	 */
	TreePath[] getSelectionPaths();

	/**
	 * @see javax.swing.tree.TreeSelectionModel#setSelectionPaths(javax.swing.tree.TreePath[])
	 */
	void setSelectionPaths(TreePath[] paths);

	/**
	 * @see javax.swing.tree.TreeSelectionModel#addTreeSelectionListener(javax.swing.event.TreeSelectionListener)
	 */
	void addTreeSelectionListener(TreeSelectionListener listener);

	/**
	 * @see javax.swing.tree.TreeSelectionModel#removeTreeSelectionListener(javax.swing.event.TreeSelectionListener)
	 */
	void removeTreeSelectionListener(TreeSelectionListener listener);

}
