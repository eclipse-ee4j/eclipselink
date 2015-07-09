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

import java.util.Collection;
import java.util.Comparator;
import java.util.Spliterator;
import java.util.Vector;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * Java SE 8 additions to {@link org.eclipse.persistence.indirection.IndirectList}.
 *
 * @author Lukas Jungmann
 */
public class IndirectList<E> extends org.eclipse.persistence.indirection.IndirectList<E> {

    public IndirectList() {
        super();
    }

    public IndirectList(int initialCapacity) {
        super(initialCapacity);
    }

    public IndirectList(int initialCapacity, int capacityIncrement) {
        super(initialCapacity, capacityIncrement);
    }

    public IndirectList(Collection<? extends E> vector) {
        super(vector);
    }

    @Override
    public Spliterator<E> spliterator() {
        return getDelegate().spliterator();
    }

    @Override
    public synchronized void replaceAll(UnaryOperator<E> operator) {
        getDelegate().replaceAll(operator);
    }

    @Override
    public synchronized boolean removeIf(Predicate<? super E> filter) {
        return getDelegate().removeIf(filter);
    }

    @Override
    public synchronized void forEach(Consumer<? super E> action) {
        getDelegate().forEach(action);
    }

    @Override
    public Stream<E> parallelStream() {
        return getDelegate().parallelStream();
    }

    @Override
    public Stream<E> stream() {
        return getDelegate().stream();
    }

    @Override
    public void sort(Comparator<? super E> c) {
        getDelegate().sort(c);
    }

}
