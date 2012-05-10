/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.persistence.jpa.jpql.model;

/**
 * A <code>IListChangeListener</code> can be registered with a {@link
 * org.eclipse.persistence.jpa.jpql.model.query.StateObject StateObject} in order to be notified
 * when a list changes (items are added or removed from it or some items have been moved inside of
 * that list).
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public interface IListChangeListener<T> {

	/**
	 * Notifies this listener new items have been added to the list.
	 *
	 * @param e The {@link IListChangeEvent} object containing the information of the change
	 */
	void itemsAdded(IListChangeEvent<T> e);

	/**
	 * Notifies this listener the list had items moved within the list.
	 *
	 * @param e The {@link IListChangeEvent} object containing the information of the change
	 */
	void itemsMoved(IListChangeEvent<T> e);

	/**
	 * Notifies this listener items have been removed from the list.
	 *
	 * @param e The {@link IListChangeEvent} object containing the information of the change
	 */
	void itemsRemoved(IListChangeEvent<T> e);
}