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
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
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

    /**
     * @see java.util.AbstractList#listIterator(int)
     */
    @Override
    public ListIterator<E> listIterator(final int index) {
        // Must wrap the interator to raise the remove event.
        return new ListIterator<E>() {
            ListIterator<E> delegateIterator = IndirectList.this.getDelegate().listIterator(index);
            E currentObject;

            @Override
            public boolean hasNext() {
                return this.delegateIterator.hasNext();
            }

            @Override
            public boolean hasPrevious() {
                return this.delegateIterator.hasPrevious();
            }

            @Override
            public int previousIndex() {
                return this.delegateIterator.previousIndex();
            }

            @Override
            public int nextIndex() {
                return this.delegateIterator.nextIndex();
            }

            @Override
            public E next() {
                this.currentObject = this.delegateIterator.next();
                return this.currentObject;
            }

            @Override
            public E previous() {
                this.currentObject = this.delegateIterator.previous();
                return this.currentObject;
            }

            @Override
            public void remove() {
                this.delegateIterator.remove();
                IndirectList.this.raiseRemoveChangeEvent(this.currentObject, this.delegateIterator.nextIndex());
            }

            @Override
            public void set(E object) {
                this.delegateIterator.set(object);
                Integer index = this.delegateIterator.previousIndex();
                IndirectList.this.raiseRemoveChangeEvent(this.currentObject, index, true);
                IndirectList.this.raiseAddChangeEvent(object, index, true);
            }

            @Override
            public void add(E object) {
                this.delegateIterator.add(object);
                IndirectList.this.raiseAddChangeEvent(object, this.delegateIterator.previousIndex());
            }

            @Override
            public void forEachRemaining(Consumer<? super E> action) {
                this.delegateIterator.forEachRemaining(action);
            }
        };
    }

    @Override
    public void sort(Comparator<? super E> c) {
        getDelegate().sort(c);
    }

    @Override
    public Spliterator<E> spliterator() {
        return getDelegate().spliterator();
    }

    @Override
    public synchronized void replaceAll(UnaryOperator<E> operator) {
        // Must trigger remove/add events if tracked or uow.
        if (hasBeenRegistered() || hasTrackedPropertyChangeListener()) {
            List<E> del = getDelegate();
            for (int i = 0; i < del.size(); i++) {
                set(i, operator.apply(del.get(i)));
            }
        } else {
            getDelegate().replaceAll(operator);
        }
    }

    @Override
    public synchronized boolean removeIf(Predicate<? super E> filter) {
        // Must trigger remove events if tracked or uow.
        if (hasBeenRegistered() || hasTrackedPropertyChangeListener()) {
            boolean hasChanged = false;
            Iterator<E> objects = iterator();
            while (objects.hasNext()) {
                if (filter.test(objects.next())) {
                    objects.remove();
                    hasChanged |= true;
                }
            }
            return hasChanged;
        }
        return getDelegate().removeIf(filter);
    }

    @Override
    public void forEach(Consumer<? super E> action) {
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

}
