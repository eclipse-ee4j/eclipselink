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
