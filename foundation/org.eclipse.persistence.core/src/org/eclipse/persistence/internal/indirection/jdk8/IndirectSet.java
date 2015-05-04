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
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Java SE 8 additions to {@link org.eclipse.persistence.indirection.IndirectSet}.
 *
 * @author Lukas Jungmann
 */
public class IndirectSet<E> extends org.eclipse.persistence.indirection.IndirectSet<E> {

    public IndirectSet() {
        super();
    }

    public IndirectSet(int initialCapacity) {
        super(initialCapacity);
    }

    public IndirectSet(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public IndirectSet(Collection<? extends E> c) {
        super(c);
    }

    @Override
    public Iterator<E> iterator() {
        // Must wrap the interator to raise the remove event.
        return new Iterator<E>() {
            Iterator<E> delegateIterator = IndirectSet.this.getDelegate().iterator();
            E currentObject;

            @Override
            public boolean hasNext() {
                return this.delegateIterator.hasNext();
            }

            @Override
            public E next() {
                this.currentObject = this.delegateIterator.next();
                return this.currentObject;
            }

            @Override
            public void remove() {
                this.delegateIterator.remove();
                IndirectSet.this.raiseRemoveChangeEvent(this.currentObject);
            }

            @Override
            public void forEachRemaining(Consumer<? super E> action) {
                this.delegateIterator.forEachRemaining(action);
            }
        };
    }

    @Override
    public Spliterator<E> spliterator() {
        return getDelegate().spliterator();
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
    public boolean removeIf(Predicate<? super E> filter) {
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
}
