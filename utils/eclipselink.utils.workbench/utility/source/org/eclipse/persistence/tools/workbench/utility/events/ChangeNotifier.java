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
package org.eclipse.persistence.tools.workbench.utility.events;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Add yet another level of indirection to change support to allow
 * clients to change how and when listener notification occurs.
 * The primary use would be to dispatch the notification to the
 * AWT event queue, so the UI will be updated in a single thread.
 */
public interface ChangeNotifier {

	/**
	 * Notify the specified listener that the source's state changed,
	 * as described in the specified event.
	 */
	void stateChanged(StateChangeListener listener, StateChangeEvent event);

	/**
	 * Notify the specified listener that a bound property changed,
	 * as described in the specified event.
	 */
	void propertyChange(PropertyChangeListener listener, PropertyChangeEvent event);

	/**
	 * Notify the specified listener that a bound collection changed,
	 * as described in the specified event.
	 */
	void itemsAdded(CollectionChangeListener listener, CollectionChangeEvent event);

	/**
	 * Notify the specified listener that a bound collection changed,
	 * as described in the specified event.
	 */
	void itemsRemoved(CollectionChangeListener listener, CollectionChangeEvent event);

	/**
	 * Notify the specified listener that a bound collection changed,
	 * as described in the specified event.
	 */
	void collectionChanged(CollectionChangeListener listener, CollectionChangeEvent event);

	/**
	 * Notify the specified listener that a bound list changed,
	 * as described in the specified event.
	 */
	void itemsAdded(ListChangeListener listener, ListChangeEvent event);

	/**
	 * Notify the specified listener that a bound list changed,
	 * as described in the specified event.
	 */
	void itemsRemoved(ListChangeListener listener, ListChangeEvent event);

	/**
	 * Notify the specified listener that a bound list changed,
	 * as described in the specified event.
	 */
	void itemsReplaced(ListChangeListener listener, ListChangeEvent event);

	/**
	 * Notify the specified listener that a bound list changed,
	 * as described in the specified event.
	 */
	void listChanged(ListChangeListener listener, ListChangeEvent event);

	/**
	 * Notify the specified listener that a bound tree changed,
	 * as described in the specified event.
	 */
	void nodeAdded(TreeChangeListener listener, TreeChangeEvent event);

	/**
	 * Notify the specified listener that a bound tree changed,
	 * as described in the specified event.
	 */
	void nodeRemoved(TreeChangeListener listener, TreeChangeEvent event);

	/**
	 * Notify the specified listener that a bound tree changed,
	 * as described in the specified event.
	 */
	void treeChanged(TreeChangeListener listener, TreeChangeEvent event);

}
