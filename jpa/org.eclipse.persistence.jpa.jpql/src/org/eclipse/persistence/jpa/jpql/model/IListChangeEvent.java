/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
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

import java.util.List;
import org.eclipse.persistence.jpa.jpql.model.query.ListHolderStateObject;
import org.eclipse.persistence.jpa.jpql.util.iterator.IterableListIterator;

/**
 * This is used in conjunction with {@link IListChangeListener}. It contains the information
 * regarding the content of a list being changed.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public interface IListChangeEvent<T> {

	/**
	 * Returns the index of where the change occurred in the list.
	 *
	 * @return The index of where the change occurred in the list
	 */
	int getEndIndex();

	/**
	 * Returns the type of change that occurred in the list.
	 *
	 * @return One of the possible {@link EventType types} of changes
	 */
	EventType getEventType();

	/**
	 * Returns a copy of the actual list after the change has happened.
	 *
	 * @return The copy of the list that got changed
	 */
	List<T> getList();

	/**
	 * Returns the name describing the list.
	 *
	 * @return The name of the list for which {@link IListChangeListener IListChangeListeners} have
	 * been registered with the source to be notified upon changes
	 */
	String getListName();

	/**
	 * Returns the source where the modification occurred and that fired the event.
	 *
	 * @param <S> The type of the source owning the list
	 * @return The source of the event
	 */
	<S extends ListHolderStateObject<? extends T>> S getSource();

	/**
	 * Returns the index of where the change occurred in the list.
	 *
	 * @return The index of where the change occurred in the list
	 */
	int getStartIndex();

	/**
	 * Returns the list of items that have caused the original list to change. Depending on the even
	 * type:
	 * <ul>
	 * <li>items that have been added ({@link EventType#ADDED});
	 * <li>items that have been removed ({@link EventType#REMOVED});
	 * <li>items that have been moved up ({@link EventType#MOVED_UP});
	 * <li>items that have been down ({@link EventType#MOVED_DOWN});
	 * <li>a single item that has been replaced ({@link EventType#REPLACED});
	 * <li>the items that were in the list before it got totally changed ({@link EventType#CHANGED}).
	 * </ul>
	 *
	 * @return The list of items associated with the {@link EventType}
	 */
	IterableListIterator<T> items();

	/**
	 * Returns the number of items that caused the original list to change.
	 *
	 * @return The count of items triggering this event
	 */
	int itemsSize();

	/**
	 * This enumeration lists the possible modification a list can have.
	 */
	public enum EventType {

		/**
		 * Specifies the list changed by having new items inserted.
		 */
		ADDED,

		/**
		 * Specifies the entire list has changed.
		 */
		CHANGED,

		/**
		 * Specifies the list changed by having some items have moved down the list.
		 */
		MOVED_DOWN,

		/**
		 * Specifies the list changed by having some items have moved up the list.
		 */
		MOVED_UP,

		/**
		 * Specifies the list changed by having items removed.
		 */
		REMOVED,

		/**
		 * Specifies the list changed by having a single item replaced.
		 */
		REPLACED
	}
}