/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.utility.events;

import java.util.EventListener;

/**
 * A "TreeChange" event gets fired whenever a model changes a "bound"
 * tree. You can register a TreeChangeListener with a source
 * model so as to be notified of any bound tree updates.
 */
public interface TreeChangeListener extends EventListener {

	/**
	 * This method gets called when a node is added to a bound tree.
	 * 
	 * @param e A TreeChangeEvent object describing the event source,
	 * the tree that changed, and the path to the node that was added.
	 */
	void nodeAdded(TreeChangeEvent e);

	/**
	 * This method gets called when a node is removed from a bound tree.
	 * 
	 * @param e A TreeChangeEvent object describing the event source,
	 * the tree that changed, and the path to the node that was removed.
	 */
	void nodeRemoved(TreeChangeEvent e);

	/**
	 * This method gets called when a portion of a bound tree is changed in
	 * a manner that is not easily characterized by the other methods in this
	 * interface.
	 * 
	 * @param e A TreeChangeEvent object describing the event source,
	 * the tree that changed, and the path to the branch of the
	 * tree that changed.
	 */
	void treeChanged(TreeChangeEvent e);

}
