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
 * Convenience implementation of ListChangeListener.
 */
public class ListChangeAdapter implements ListChangeListener {

	/**
	 * Default constructor.
	 */
	public ListChangeAdapter() {
		super();
	}

	/**
	 * @see ListChangeListener#itemsAdded(ListChangeEvent)
	 */
	public void itemsAdded(ListChangeEvent e) {
		// do nothing
	}

	/**
	 * @see ListChangeListener#itemsRemoved(ListChangeEvent)
	 */
	public void itemsRemoved(ListChangeEvent e) {
		// do nothing
	}

	/**
	 * @see ListChangeListener#itemsReplaced(ListChangeEvent)
	 */
	public void itemsReplaced(ListChangeEvent e) {
		// do nothing
	}

	/**
	 * @see ListChangeListener#listChanged(ListChangeEvent)
	 */
	public void listChanged(ListChangeEvent e) {
		// do nothing
	}

}
