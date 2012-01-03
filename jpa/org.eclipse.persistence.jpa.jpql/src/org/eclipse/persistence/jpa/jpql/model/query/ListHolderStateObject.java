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
package org.eclipse.persistence.jpa.jpql.model.query;

import java.util.Collection;
import java.util.List;
import org.eclipse.persistence.jpa.jpql.model.IListChangeListener;
import org.eclipse.persistence.jpa.jpql.util.iterator.IterableListIterator;

/**
 * A <code>ListHolderStateObject</code> is a {@link StateObject} having a list of children and this
 * gives access to some operation over the list.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public interface ListHolderStateObject<T> extends StateObject {

	/**
	 * Adds the given {@link StateObject} as a child of this one.
	 *
	 * @param item The child {@link StateObject} to become a child of this one
	 * return The given item
	 */
	<S extends T> S addItem(S item);

	/**
	 * Adds the given list of {@link StateObject StateObjects} as children of this one.
	 *
	 * @param items The {@link StateObject StateObjects} to become children of this one
	 */
	void addItems(List<? extends T> items);

	/**
	 * Registers the given {@link IListChangeListener} for the specified list. The listener will be
	 * notified only when items are added, removed, moved from the list.
	 *
	 * @param listName The name of the list for which the listener will be notified when the content
	 * of the list has changed
	 * @param listener The listener to be notified upon changes
	 * @exception NullPointerException {@link IListChangeListener} cannot be <code>null</code>
	 * @exception IllegalArgumentException The listener is already registered with the list name
	 */
	void addListChangeListener(String listName, IListChangeListener<T> listener);

	/**
	 * Determines whether the given {@link StateObject} can be moved down by one position in the
	 * list owned by its parent.
	 *
	 * @param item The {@link StateObject} that could potentially be moved down
	 * @return <code>true</code> if the object can be moved down by one unit; <code>false</code>
	 * otherwise
	 */
	boolean canMoveDown(T item);

	/**
	 * Determines whether the given {@link StateObject} can be moved up by one position in the list
	 * owned by its parent.
	 *
	 * @param item The {@link StateObject} that could potentially be moved up
	 * @return <code>true</code> if the object can be moved up by one unit; <code>false</code>
	 * otherwise
	 */
	boolean canMoveUp(T item);

	/**
	 * Returns the {@link StateObject} at the given positions from the list
	 *
	 * @param index The position of the {@link StateObject} to retrieve
	 * @return The {@link StateObject} at the given position
	 */
	T getItem(int index);

	/**
	 * Determines whether this {@link StateObject} has any children.
	 *
	 * @return <code>true</code> if this {@link StateObject} has children; <code>false</code> otherwise
	 */
	boolean hasItems();

	/**
	 * Returns an {@link IterableListIterator} over the children.
	 *
	 * @return An {@link IterableListIterator} that is iterating over the children
	 */
	IterableListIterator<? extends T> items();

	/**
	 * Returns the number of children this list holder has.
	 *
	 * @return The count of {@link StateObject StateObjects} that are children of this one
	 */
	int itemsSize();

	/**
	 * Moves the given {@link StateObject} down by one position in the list owned by its parent.
	 *
	 * @param item The {@link StateObject} to move down in the list
	 * @return The given item
	 */
	T moveDown(T item);

	/**
	 * Moves the given {@link StateObject} up by one position in the list owned by its parent.
	 *
	 * @param item The {@link StateObject} to move up in the list
	 * @return The given item
	 */
	T moveUp(T item);

	/**
	 * Removes the given {@link StateObject} from the list of children.
	 *
	 * @param item The child {@link StateObject} to not longer be a child
	 */
	void removeItem(T item);

	/**
	 * Removes the given {@link StateObject} from the list of children.
	 *
	 * @param items The {@link StateObject StateObjects} to remove from this one
	 */
	void removeItems(Collection<T> items);

	/**
	 * Unregisters the given {@link IListChangeListener} that was registered for the specified list.
	 * The listener will no longer be notified only when items are added, removed, moved from the
	 * list.
	 *
	 * @param listName The name of the list for which the listener was registered
	 * @param listener The listener to unregister
	 * @exception NullPointerException {@link IListChangeListener} cannot be <code>null</code>
	 * @exception IllegalArgumentException The listener was never registered with the list name
	 */
	void removeListChangeListener(String listName, IListChangeListener<T> listener);
}