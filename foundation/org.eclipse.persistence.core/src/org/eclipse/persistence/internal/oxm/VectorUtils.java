/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.internal.oxm;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.Vector;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import static java.util.Collections.emptyIterator;
import static java.util.Collections.emptyListIterator;

/**
 * This class provides "empty" and "unmodifiable" wrappers for the Vector class.
 * The functionality is similar the Collections class and mostly copy-paste from the class.
 * Unfortunately, the standard Collections class does not provides wrappers for the outdated Vector class.
 *
 * @see Collections
 */
public class VectorUtils {
    /**
     * The empty vector (immutable).
     * @see #emptyVector()
     */
    public static final Vector EMPTY_VECTOR = new EmptyVector();

    /**
     * Returns an unmodifiable view of the specified vector.  This method allows
     * modules to provide users with "read-only" access to internal
     * vectors.  Query operations on the returned vector "read through" to the
     * specified vector, and attempts to modify the returned vector, whether
     * direct or via its iterator, result in an
     * <tt>UnsupportedOperationException</tt>.<p>
     *
     * @param <T> the class of the objects in the vector
     * @param vector the vector for which an unmodifiable view is to be returned.
     * @return an unmodifiable view of the specified vector.
     */
    public static <T> Vector<T> unmodifiableVector(Vector<? extends T> vector) {
        if (vector == null) {
            throw new IllegalArgumentException("Input value must not be NULL!");
        }
        return new UnmodifiableVector<>(vector);
    }

    /**
     * Returns an empty vector (immutable).
     *
     * <p>This example illustrates the type-safe way to obtain an empty vector:
     * <pre>
     *     Vector&lt;String&gt; s = VectorUtils.emptyVector();
     * </pre>
     * @param <T> type of elements, if there were any, in the vector
     * @return an empty immutable vector
     * @implNote Implementations of this method need not create a separate <tt>Vector</tt>
     * object for each call.   Using this method is likely to have comparable
     * cost to using the like-named field.  (Unlike this method, the field does
     * not provide type safety.)
     * @see #EMPTY_VECTOR
     */
    @SuppressWarnings("unchecked")
    public static final <T> Vector<T> emptyVector() {
        return (Vector<T>) EMPTY_VECTOR;
    }

    private VectorUtils() {}

    private static class EmptyVector<E> extends Vector<E> {
        private static final long serialVersionUID = 5020332176914113951L;

        @Override
        public int size() {return 0;}

        @Override
        public boolean isEmpty() {return true;}

        @Override
        public boolean contains(Object obj) {return false;}

        @Override
        public Object[] toArray() { return new Object[0]; }

        @Override
        public <T> T[] toArray(T[] a) {
            if (a.length > 0) {
                a[0] = null;
            }
            return a;
        }

        @Override
        public E get(int index) {
            throw new IndexOutOfBoundsException("Index: " + index);
        }

        @Override
        public void add(int index, E element) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsAll(Collection<?> c) { return c.isEmpty(); }

        @Override
        public boolean equals(Object o) {
            return (o instanceof Vector) && ((Vector<?>) o).isEmpty();
        }

        @Override
        public int hashCode() { return 1; }

        @Override
        public ListIterator<E> listIterator(int index) {
            return emptyListIterator();
        }

        @Override
        public ListIterator<E> listIterator() {
            return emptyListIterator();
        }

        @Override
        public Iterator<E> iterator() {
            return emptyIterator();
        }

        // Override default methods in Collection
        @Override
        public void forEach(Consumer<? super E> action) {
            Objects.requireNonNull(action);
        }

        @Override
        public boolean removeIf(Predicate<? super E> filter) {
            Objects.requireNonNull(filter);
            return false;
        }

        @Override
        public void replaceAll(UnaryOperator<E> operator) {
            Objects.requireNonNull(operator);
        }

        @Override
        public void sort(Comparator<? super E> c) {
        }

        @Override
        public Spliterator<E> spliterator() { return Spliterators.emptySpliterator(); }

        // Preserves singleton property
        private Object readResolve() {
            return EMPTY_VECTOR;
        }
    }

    /**
     * @serial include
     */
    static class UnmodifiableVector<E> extends Vector<E> {
        private static final long serialVersionUID = -8378199697360550972L;

        UnmodifiableVector(Vector<? extends E> vector) {
            super(vector);
        }

        public E set(int index, E element) {
            throw new UnsupportedOperationException();
        }

        public void add(int index, E element) {
            throw new UnsupportedOperationException();
        }

        public E remove(int index) {
            throw new UnsupportedOperationException();
        }

        public boolean addAll(int index, Collection<? extends E> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<E> subList(int fromIndex, int toIndex) {
            return Collections.unmodifiableList(super.subList(fromIndex, toIndex));
        }

        public ListIterator<E> listIterator(final int index) {
            return new ListIterator<E>() {
                private final ListIterator<? extends E> i
                    = listIterator(index);

                public boolean hasNext() {return i.hasNext();}

                public E next() {return i.next();}

                public boolean hasPrevious() {return i.hasPrevious();}

                public E previous() {return i.previous();}

                public int nextIndex() {return i.nextIndex();}

                public int previousIndex() {return i.previousIndex();}

                public void remove() {
                    throw new UnsupportedOperationException();
                }

                public void set(E e) {
                    throw new UnsupportedOperationException();
                }

                public void add(E e) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public void forEachRemaining(Consumer<? super E> action) {
                    i.forEachRemaining(action);
                }
            };
        }

        public ListIterator<E> listIterator() {return listIterator(0);}

        @Override
        public void replaceAll(UnaryOperator<E> operator) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void sort(Comparator<? super E> c) {
            throw new UnsupportedOperationException();
        }
    }
}
