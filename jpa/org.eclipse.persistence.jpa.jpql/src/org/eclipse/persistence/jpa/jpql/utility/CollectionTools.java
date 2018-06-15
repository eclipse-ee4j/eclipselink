/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.jpql.utility;

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
 * @version 2.5
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
     * Adds to the given {@link Collection} the items contained in the array.
     *
     * @param collection The {@link Collection} that will receive the items returned by the {@link Iterator}
     * @param array The array to add to the given {@link Collection}
     * @return The given {@link Collection}
     * @param <T> The type of the collection
     * @param <E> The type of the element
     * @since 2.4.1
     */
    public static <T extends Collection<E>, E> T addAll(T collection, E[] array) {
        for (E item : array) {
            collection.add(item);
        }
        return collection;
    }

    /**
     * Adds to the given {@link Collection} the items contained in the {@link Iterable}.
     *
     * @param collection The {@link Collection} that will receive the items returned by the {@link Iterator}
     * @param iterable The {@link Iterable} to add to the given {@link Collection}
     * @return The given {@link Collection}
     * @param <T> The type of the collection
     * @param <E> The type of the element
     * @since 2.5
     */
    public static <T extends Collection<E>, E> T addAll(T collection, Iterable<? extends E> iterable) {
        return addAll(collection, iterable.iterator());
    }

    /**
     * Adds to the given {@link Collection} the items returned by the given {@link Iterator}.
     *
     * @param collection The {@link Collection} that will receive the items returned by the {@link Iterator}
     * @param iterator The {@link Iterator} that will return the items to add to the {@link Collection}
     * @param <T> The type of the collection
     * @param <E> The type of the element
     * @return The given {@link Collection}
     */
    public static <T extends Collection<E>, E> T addAll(T collection, Iterator<? extends E> iterator) {
        while (iterator.hasNext()) {
            collection.add(iterator.next());
        }
        return collection;
    }

    /**
     * Creates a new array and adds the items returned by the given {@link Iterable}.
     *
     * @param componentType The type of the array
     * @param iterable The {@link Iterable} that will iterate over the collection of items to add
     * into the new array in the same order they are returned
     * @return A new array filled with the items returned by the given iterator
     * @since 2.5
     */
    public static <T> T[] array(Class<T> componentType, Iterable<? extends T> iterable) {
        return array(componentType, iterable.iterator());
    }

    /**
     * Creates a new array and adds the items returned by the given {@link Iterator}.
     *
     * @param componentType The type of the array
     * @param iterator The {@link Iterator} that will iterate over the collection of items to add
     * into the new array in the same order they are returned
     * @return A new array filled with the items returned by the given iterator
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] array(Class<T> componentType, Iterator<? extends T> iterator) {

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
     * Returns a list corresponding to the specified array. Unlike {@link java.util.Arrays#asList(Object[])},
     * the list is modifiable and is not backed by the array.
     *
     * @param array The array to convert into a {@link List}
     * @return An instance of a {@link List} containing the elements of the given array
     * @param <E>
     * @since 2.5
     */
    public static <E> List<E> list(E... array) {
        List<E> list = new ArrayList<E>(array.length);
        for (E item : array) {
            list.add(item);
        }
        return list;
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
