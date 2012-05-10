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
package org.eclipse.persistence.jpa.jpql.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * This utility class provides utility methods related to collections, iterators and arrays.
 * <p>
 * Provisional API: This interface is part of an interim API that is still under development and
 * expected to change significantly before reaching stability. It is available at this early stage
 * to solicit feedback from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.
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
	 * Creates a new array and adds the items returned by the given {@link Iterator}.
	 *
	 * @param componentType The type of the array
	 * @param iterator The {@link Iterator} that will iterate over the collection of items to add
	 * into the new array in the same order they are returned
	 * @return A new array filled with the items returned by the given iterator.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] array(Class<T> componentType, Iterator<T> iterator) {

		if (!iterator.hasNext()) {
			return (T[]) Array.newInstance(componentType, 0);
		}

		ArrayList<T> list = new ArrayList<T>();

		while (iterator.hasNext()) {
			list.add(iterator.next());
		}

		T[] array = (T[]) Array.newInstance(componentType, list.size());
		return list.toArray(array);
	}

	/**
	 * Creates a new {@link List} and adds the items returned by the given {@link java.util.ListIterator
	 * ListIterator}.
	 *
	 * @param iterator The {@link java.util.ListIterator ListIterator} to iterate over items to add
	 * into a list in the same order they are returned
	 * @return A new {@link List}
	 */
	public static <T> List<T> list(Iterator<? extends T> iterator) {
		return addAll(new ArrayList<T>(), iterator);
	}
}