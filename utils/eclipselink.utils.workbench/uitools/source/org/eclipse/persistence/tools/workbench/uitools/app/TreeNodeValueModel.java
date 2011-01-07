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
 * Extend PropertyValueModel to better support the TreeModelAdapter class.
 * 
 * Implementors of this interface should fire a "state change" event
 * whenever the node's internal state changes in a way that the
 * tree listeners should be notified.
 * 
 * Implementors of this interface should also fire a "value property change"
 * event whenever the node's value changes. Typically, only nodes that
 * hold "primitive" data will fire this event.
 * 
 * @see AbstractTreeNodeValueModel
 */
public interface TreeNodeValueModel extends PropertyValueModel {

	/**
	 * Return the node's parent node; null if the node
	 * is the root.
	 */
	TreeNodeValueModel getParent();

	/**
	 * Return the path to the node.
	 */
	TreeNodeValueModel[] path();

	/**
	 * Return a list value model of the node's child nodes.
	 */
	ListValueModel getChildrenModel();

	/**
	 * Return the node's child at the specified index.
	 */
	TreeNodeValueModel getChild(int index);

	/**
	 * Return the size of the node's list of children.
	 */
	int childrenSize();

	/**
	 * Return the index in the node's list of children of the specified child.
	 */
	int indexOfChild(TreeNodeValueModel child);

	/**
	 * Return whether the node is a leaf (i.e. it has no children)
	 */
	boolean isLeaf();

}
