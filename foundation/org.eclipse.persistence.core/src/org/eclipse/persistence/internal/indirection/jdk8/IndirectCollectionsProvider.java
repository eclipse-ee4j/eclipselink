/**
 * *****************************************************************************
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 which
 * accompanies this distribution. The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html and the Eclipse Distribution
 * License is available at http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors: Oracle - initial API and implementation
 *****************************************************************************
 */
package org.eclipse.persistence.internal.indirection.jdk8;

import java.util.Collection;
import java.util.Map;
import org.eclipse.persistence.indirection.IndirectCollection;
import org.eclipse.persistence.indirection.IndirectCollectionsFactory;
import org.eclipse.persistence.indirection.IndirectList;
import org.eclipse.persistence.indirection.IndirectMap;
import org.eclipse.persistence.indirection.IndirectSet;

/**
 * Responsible for creating Java SE 8+ specific implementations of {@link IndirectCollection}s.
 *
 * @author Lukas Jungmann
 * @see IndirectCollection
 * @see IndirectCollectionsFactory
 * @see IndirectCollectionsFactory.IndirectCollectionsProvider
 * @since EclispeLink 2.6.0
 */
public final class IndirectCollectionsProvider implements IndirectCollectionsFactory.IndirectCollectionsProvider {

    @Override
    public Class getListClass() {
        return org.eclipse.persistence.internal.indirection.jdk8.IndirectList.class;
    }

    @Override
    public <E> IndirectList<E> createIndirectList(int initialCapacity, int capacityIncrement) {
        return new org.eclipse.persistence.internal.indirection.jdk8.IndirectList<>(initialCapacity, capacityIncrement);
    }

    @Override
    public <E> IndirectList<E> createIndirectList(Collection<? extends E> collection) {
        return new org.eclipse.persistence.internal.indirection.jdk8.IndirectList<>(collection);
    }

    @Override
    public Class getSetClass() {
        return org.eclipse.persistence.internal.indirection.jdk8.IndirectSet.class;
    }

    @Override
    public <E> IndirectSet<E> createIndirectSet(int initialCapacity, float loadFactor) {
        return new org.eclipse.persistence.internal.indirection.jdk8.IndirectSet<>(initialCapacity, loadFactor);
    }

    @Override
    public <E> IndirectSet<E> createIndirectSet(Collection<? extends E> collection) {
        return new org.eclipse.persistence.internal.indirection.jdk8.IndirectSet<>(collection);
    }

    @Override
    public Class getMapClass() {
        return org.eclipse.persistence.internal.indirection.jdk8.IndirectMap.class;
    }

    @Override
    public <K, V> IndirectMap<K, V> createIndirectMap(int initialCapacity, float loadFactor) {
        return new org.eclipse.persistence.internal.indirection.jdk8.IndirectMap<>(initialCapacity, loadFactor);
    }

    @Override
    public <K, V> IndirectMap<K, V> createIndirectMap(Map<? extends K, ? extends V> map) {
        return new org.eclipse.persistence.internal.indirection.jdk8.IndirectMap<>(map);
    }
}