/*******************************************************************************
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 ******************************************************************************/
package org.eclipse.persistence.internal.indirection.jdk8;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Java SE 8 additions to {@link org.eclipse.persistence.indirection.IndirectMap}.
 *
 * @author Lukas Jungmann
 */
public class IndirectMap<K, V> extends org.eclipse.persistence.indirection.IndirectMap<K, V> {

    public IndirectMap() {
        super();
    }

    public IndirectMap(int initialCapacity) {
        super(initialCapacity);
    }

    public IndirectMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public IndirectMap(Map<? extends K, ? extends V> m) {
        super(m);
    }

    @Override
    public synchronized V merge(K key, V value, BiFunction<? super V,? super V,? extends V> remappingFunction) {
        return getDelegate().merge(key, value, remappingFunction);
    }

    @Override
    public synchronized V compute(K key, BiFunction<? super K,? super V,? extends V> remappingFunction) {
        return getDelegate().compute(key, remappingFunction);
    }

    @Override
    public synchronized V computeIfPresent(K key, BiFunction<? super K,? super V,? extends V> remappingFunction) {
        return getDelegate().computeIfPresent(key, remappingFunction);
    }

    @Override
    public synchronized V computeIfAbsent(K key, Function<? super K,? extends V> mappingFunction) {
        return getDelegate().computeIfAbsent(key, mappingFunction);
    }

    @Override
    public synchronized V replace(K key, V value) {
        return getDelegate().replace(key, value);
    }

    @Override
    public synchronized boolean replace(K key, V oldValue, V newValue) {
        return getDelegate().replace(key, oldValue, newValue);
    }

    @Override
    public synchronized boolean remove(Object key, Object value) {
        return getDelegate().remove(key, value);
    }

    @Override
    public synchronized V putIfAbsent(K key, V value) {
        return getDelegate().putIfAbsent(key, value);
    }

    @Override
    public synchronized void replaceAll(BiFunction function) {
        getDelegate().replaceAll(function);
    }

    @Override
    public synchronized void forEach(BiConsumer<? super K,? super V> action) {
        getDelegate().forEach(action);
    }

    @Override
    public synchronized V getOrDefault(Object key, V defaultValue) {
        return getDelegate().getOrDefault(key, defaultValue);
    }

}
