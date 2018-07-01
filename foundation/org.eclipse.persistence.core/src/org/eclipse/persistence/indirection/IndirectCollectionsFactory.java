/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation
package org.eclipse.persistence.indirection;

import java.util.Collection;
import java.util.Map;

/**
 * Provides factory methods to create JDK specific implementation
 * of particular type of {@link IndirectCollection}.
 *
 * @author Lukas Jungmann
 * @see IndirectCollection
 * @see IndirectList
 * @see IndirectMap
 * @see IndirectSet
 * @since EclipseLink 2.6.0
 */
public final class IndirectCollectionsFactory {

    private static final IndirectCollectionsProvider provider = getProvider();

    /**
     * Class implementing {@link IndirectList}.
     */
    public static final Class IndirectList_Class = provider.getListClass();

    /**
     * Class implementing {@link IndirectSet}.
     */
    public static final Class IndirectSet_Class = provider.getSetClass();

    /**
     * Class implementing {@link IndirectMap}.
     */
    public static final Class IndirectMap_Class = provider.getMapClass();

    /**
     * Construct an empty {@link IndirectList} with the default initial capacity (10)
     * and default capacity increment (0).
     *
     * @param <E> the class of the objects in the list
     * @return an empty {@link IndirectList} with the default initial capacity
     *      and default capacity increment
     */
    public static <E> IndirectList<E> createIndirectList() {
        return provider.createIndirectList(10, 0);
    }

    /**
     * Construct an empty {@link IndirectList} with the specified initial capacity
     * and default capacity increment (0).
     *
     * @param <E> the class of the objects in the list
     * @param initialCapacity the initial capacity of the vector
     *
     * @return an empty {@link IndirectList} with the specified initial capacity
     *      and default capacity increment
     * @throws IllegalArgumentException if the specified initial capacity
     *      is negative
     */
    public static <E> IndirectList<E> createIndirectList(int initialCapacity) {
        return provider.createIndirectList(initialCapacity, 0);
    }

    /**
     * Construct an {@link IndirectList} containing the elements of the specified
     * collection, in the order they are returned by the collection's iterator.
     *
     * @param <E> the class of the objects in the list
     * @param collection a collection containing the elements to construct
     *      the {@link IndirectList} with.
     * @return an {@link IndirectList} containing the elements of the specified
     *      collection
     */
    public static <E> IndirectList<E> createIndirectList(Collection<? extends E> collection) {
        return  provider.createIndirectList(collection);
    }

    /**
     * Construct an empty {@link IndirectSet} with the default initial capacity (10)
     * and the default load factor (0.75).
     *
     * @return an empty {@link IndirectSet} with the default initial capacity
     *      and the default load factor
     */
    public static <E> IndirectSet<E> createIndirectSet() {
        return provider.createIndirectSet(10, 0.75f);
    }

    /**
     * Construct an empty {@link IndirectSet} with the specified initial capacity
     * and the default load factor (0.75).
     *
     * @param initialCapacity the initial capacity of the set
     *
     * @return an empty {@link IndirectSet} with the specified initial capacity
     *      and the default load factor
     * @throws IllegalArgumentException if the specified initial capacity is negative
     */
    public static <E> IndirectSet<E> createIndirectSet(int initialCapacity) {
        return provider.createIndirectSet(initialCapacity, 0.75f);
    }

    /**
     * Constructs an {@link IndirectSet} containing the elements of the specified
     * collection.
     *
     * @param collection a collection containing the elements to construct
     *      the {@link IndirectSet} with
     *
     * @return an {@link IndirectSet} containing the elements of the specified collection
     * @throws NullPointerException if the specified collection is null
     */
    public static <E> IndirectSet<E> createIndirectSet(Collection<? extends E> collection) {
        return provider.createIndirectSet(collection);
    }

    /**
     * Construct a new, empty {@link IndirectMap} with the default initial
     * capacity (11) and the default load factor (0.75).
     *
     * @return a new, empty {@link IndirectMap} with the default initial
     *      capacity and the default load factor
     */
    public static <K, V> IndirectMap<K, V> createIndirectMap() {
        return provider.createIndirectMap(11, 0.75f);
    }

    /**
     * Construct a new, empty {@link IndirectMap} with the specified initial
     * capacity and the default load factor (0.75).
     *
     * @param initialCapacity the initial capacity of the {@link IndirectMap}
     *
     * @return a new, empty {@link IndirectMap} with the specified initial
     *      capacity and the default load factor
     * @throws  IllegalArgumentException  if the initial capacity is less than
     *      or equal to zero
     */
    public static <K, V> IndirectMap<K, V> createIndirectMap(int initialCapacity) {
        return provider.createIndirectMap(initialCapacity, 0.75f);
    }

    /**
     * Construct a new {@link IndirectMap} with the same mappings as the given Map.
     * The {@link IndirectMap} is created with a capacity of twice the number of entries
     * in the given Map or 11 (whichever is greater), and a default load factor, which is 0.75.
     *
     * @param map the map whose mappings are to be placed into created {@link IndirectMap}
     *
     * @return a new {@link IndirectMap} with the same mappings as the given Map
     * @throws NullPointerException if the specified map is null
     */
    public static <K, V> IndirectMap<K, V> createIndirectMap(Map<? extends K, ? extends V> map) {
        return provider.createIndirectMap(map);
    }

    /**
     * As of EclipseLink 2.6.0 this returns Java SE 7- compatible provider by default
     * on Java SE 7 and Java SE 8+ compatible provider if Java SE 8+ is detected.
     *
     * @return default provider responsible for creating Java SE specific implementations
     * of {@link IndirectCollection}s
     */
    private static IndirectCollectionsProvider getProvider() {
        return new DefaultProvider();
    }

    /**
     * Define API providers of {@link IndirectCollection} implementations must conform to.
     */
    public static interface IndirectCollectionsProvider {

        /**
         * Class implementing {@link IndirectList}.
         *
         * @return class implementing {@link IndirectList}
         */
        Class getListClass();

        /**
         * Construct an empty {@link IndirectList} with the specified initial capacity
         * and capacity increment.
         *
         * @param <E> the class of the objects in the list
         * @param initialCapacity the initial capacity of the list
         * @param capacityIncrement the amount by which the capacity is increased
         *      when the list overflows
         *
         * @return an empty {@link IndirectList} with the specified initial capacity
         *      and capacity increment
         * @throws IllegalArgumentException if the specified initial capacity is negative
         */
        <E> IndirectList<E> createIndirectList(int initialCapacity, int capacityIncrement);

        /**
         * Constructs an {@link IndirectList} containing the elements of the specified
         * collection, in the order they are returned by the collection's iterator.
         *
         * @param <E> the class of the objects in the list
         * @param collection a collection containing the elements to construct
         *      the {@link IndirectList} with
         *
         * @return an {@link IndirectList} containing the elements of the specified collection
         * @throws NullPointerException if the specified collection is null
         */
        <E> IndirectList<E> createIndirectList(Collection<? extends E> collection);

        /**
         * Class implementing {@link IndirectSet}.
         *
         * @return class implementing {@link IndirectSet}
         */
        Class getSetClass();

        /**
         * Construct an empty {@link IndirectSet} with the specified initial capacity
         * and the specified load factor.
         *
         * @param initialCapacity the initial capacity of the set
         * @param loadFactor the load factor of the set
         *
         * @return an empty {@link IndirectSet} with the specified initial capacity
         *      and the specified load factor
         * @throws IllegalArgumentException if the specified initial capacity is negative
         */
        <E> IndirectSet<E> createIndirectSet(int initialCapacity, float loadFactor);

        /**
         * Constructs an {@link IndirectSet} containing the elements of the specified
         * collection.
         *
         * @param collection a collection containing the elements to construct
         *      the {@link IndirectSet} with
         *
         * @return an {@link IndirectSet} containing the elements of the specified collection
         * @throws NullPointerException if the specified collection is null
         */
        <E> IndirectSet<E> createIndirectSet(Collection<? extends E> collection);

        /**
         * Class implementing {@link IndirectMap}.
         *
         * @return class implementing {@link IndirectMap}
         */
        Class getMapClass();

        /**
         * Construct a new, empty {@link IndirectMap} with the specified initial
         * capacity and the specified load factor.
         *
         * @param initialCapacity the initial capacity of the {@link IndirectMap}
         * @param loadFactor a number between 0.0 and 1.0
         *
         * @return a new, empty {@link IndirectMap} with the specified initial
         *      capacity and the specified load factor
         * @throws  IllegalArgumentException  if the initial capacity is less than
         *      or equal to zero, or if the load factor is less than or equal to zero
         */
        <K, V> IndirectMap<K, V> createIndirectMap(int initialCapacity, float loadFactor);

        /**
         * Construct a new {@link IndirectMap} with the same mappings as the given Map.
         * The {@link IndirectMap} is created with a capacity of twice the number of entries
         * in the given Map or 11 (whichever is greater), and a default load factor, which is 0.75.
         *
         * @param map the map whose mappings are to be placed into created {@link IndirectMap}
         *
         * @return a new {@link IndirectMap} with the same mappings as the given Map
         * @throws NullPointerException if the specified map is null
         */
        <K, V> IndirectMap<K, V> createIndirectMap(Map<? extends K, ? extends V> map);
    }

    /**
     * Provider for creating Java SE 7 (and older) compatible
     * {@link IndirectCollection} implementations.
     */
    private static final class DefaultProvider implements IndirectCollectionsProvider {

        @Override
        public Class getListClass() {
            return IndirectList.class;
        }

        @Override
        public <E> IndirectList<E> createIndirectList(int initialCapacity, int capacityIncrement) {
            return new IndirectList<>(initialCapacity, capacityIncrement);
        }

        @Override
        public <E> IndirectList<E> createIndirectList(Collection<? extends E> collection) {
            return new IndirectList<>(collection);
        }

        @Override
        public Class getSetClass() {
            return IndirectSet.class;
        }

        @Override
        public <E> IndirectSet<E> createIndirectSet(int initialCapacity, float loadFactor) {
            return new IndirectSet<>(initialCapacity, loadFactor);
        }

        @Override
        public <E> IndirectSet<E> createIndirectSet(Collection<? extends E> collection) {
            return new IndirectSet<>(collection);
        }

        @Override
        public Class getMapClass() {
            return IndirectMap.class;
        }

        @Override
        public <K, V> IndirectMap<K, V> createIndirectMap(int initialCapacity, float loadFactor) {
            return new IndirectMap<>(initialCapacity, loadFactor);
        }

        @Override
        public <K, V> IndirectMap<K, V> createIndirectMap(Map<? extends K, ? extends V> map) {
            return new IndirectMap<>(map);
        }
    }
}
