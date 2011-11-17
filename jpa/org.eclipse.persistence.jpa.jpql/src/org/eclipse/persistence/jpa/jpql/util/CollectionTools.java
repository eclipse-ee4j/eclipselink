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
package org.eclipse.persistence.jpa.jpql.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * This utility class provides utility methods related to collection.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public final class CollectionTools {

	/**
	 * Creates a new <code>CollectionTools</code>.
	 */
	private CollectionTools() {
		super();
	}

	/**
	 * Adds to the given {@link Collection} the given items.
	 *
	 * @param collection The {@link Collection} that will receive the items returned by the {@link Iterator}
	 * @param items The list of items to add to the {@link Collection}
	 * @return The given {@link Collection}
	 */
	public static Collection<String> addAll(Collection<String> collection, CharSequence... items) {
		for (CharSequence item : items) {
			collection.add(item.toString());
		}
		return collection;
	}

	/**
	 * Adds to the given {@link Collection} the given items.
	 *
	 * @param collection The {@link Collection} that will receive the items returned by the {@link Iterator}
	 * @param items The list of items to add to the {@link Collection}
	 * @return The given {@link Collection}
	 */
	public static <T extends Collection<I>, I> T addAll(T collection, I... items) {
		for (I item : items) {
			collection.add(item);
		}
		return collection;
	}

	/**
	 * Adds to the given {@link Collection} the items returned by the given {@link Iterator}.
	 *
	 * @param collection The {@link Collection} that will receive the items returned by the {@link Iterator}
	 * @param index The position where to start inserting the items
	 * @param iterator The {@link Iterator} that will return the items to add to the {@link Collection}
	 * @return The given {@link Collection}
	 */
	public static <T extends List<I>, I> T addAll(T collection, int index, Iterator<? extends I> iterator) {
		while (iterator.hasNext()) {
			collection.add(index++, iterator.next());
		}
		return collection;
	}

	/**
	 * Adds to the given {@link Collection} the items returned by the given {@link Iterator}.
	 *
	 * @param collection The {@link Collection} that will receive the items returned by the {@link Iterator}
	 * @param iterator The {@link Iterator} that will return the items to add to the {@link Collection}
	 * @return The given {@link Collection}
	 */
	public static <T extends Collection<I>, I> T addAll(T collection, Iterator<? extends I> iterator) {
		while (iterator.hasNext()) {
			collection.add(iterator.next());
		}
		return collection;
	}

	/**
	 * Creates a new {@link List} by iterating over the given {@link ListIterator}.
	 *
	 * @param iterator The {@link ListIterator} to iterate over items to add into a list in the same
	 * order they are returned
	 * @return A new {@link List}
	 */
	public static <T> List<T> list(ListIterator<? extends T> iterator) {
		List<T> list = new ArrayList<T>();
		while (iterator.hasNext()) {
			list.add(iterator.next());
		}
		return list;
	}

	/**
	 * Returns the count of items that are iterated by the given {@link Iterator}. Once the count has
	 * been calculated, the {@link Iterator} can no longer be used to traverse the item.
	 *
	 * @param iterator The {@link Iterator} that traverses a series of item
	 * @return The count of items the given {@link Iterator} returned
	 */
	public static int size(Iterator<?> iterator) {
		int count = 0;
		while (iterator.hasNext()) {
			iterator.next();
			count++;
		}
		return count;
	}
}