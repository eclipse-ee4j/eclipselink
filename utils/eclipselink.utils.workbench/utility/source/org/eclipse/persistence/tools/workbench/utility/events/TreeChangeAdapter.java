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

/**
 * Convenience implementation of TreeChangeListener.
 */
public class TreeChangeAdapter implements TreeChangeListener {

	/**
	 * Default constructor.
	 */
	public TreeChangeAdapter() {
		super();
	}

	/**
	 * @see TreeChangeListener#treeNodeAdded(TreeChangeEvent)
	 */
	public void nodeAdded(TreeChangeEvent e) {
		// do nothing
	}

	/**
	 * @see TreeChangeListener#itemRemoved(TreeChangeEvent)
	 */
	public void nodeRemoved(TreeChangeEvent e) {
		// do nothing
	}

	/**
	 * @see TreeChangeListener#treeChanged(TreeChangeEvent)
	 */
	public void treeChanged(TreeChangeEvent e) {
		// do nothing
	}

}
