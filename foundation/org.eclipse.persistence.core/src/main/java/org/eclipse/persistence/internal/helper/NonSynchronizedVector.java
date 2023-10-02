/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.helper;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Vector;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * Vector subclass that removes the synchronization.
 */
public class NonSynchronizedVector<E> extends Vector<E> {
    public static <E> NonSynchronizedVector<E> newInstance(int initialCapacity, int capacityIncrement) {
        return new NonSynchronizedVector<E>(initialCapacity, capacityIncrement);
    }

    public static <E> NonSynchronizedVector<E> newInstance(int initialCapacity) {
        return new NonSynchronizedVector<E>(initialCapacity);
    }

    public static <E> NonSynchronizedVector<E> newInstance() {
        return new NonSynchronizedVector<>();
    }

    public static <E> NonSynchronizedVector<E> newInstance(Collection<? extends E> c) {
        return new NonSynchronizedVector<>(c);
    }

    public NonSynchronizedVector(int initialCapacity, int capacityIncrement) {
        super(initialCapacity, capacityIncrement);
    }

    public NonSynchronizedVector(int initialCapacity) {
        super(initialCapacity);
    }

    public NonSynchronizedVector() {
        super();
    }

    public NonSynchronizedVector(Collection<? extends E> c) {
        super(c);
    }

    @Override
    public void copyInto(Object[] anArray) {
        System.arraycopy(elementData, 0, anArray, 0, elementCount);
    }

    @Override
    public void trimToSize() {
        modCount++;
        int oldCapacity = elementData.length;
        if (elementCount < oldCapacity) {
            elementData = Arrays.copyOf(elementData, elementCount);
        }
    }

    @Override
    public void ensureCapacity(int minCapacity) {
        if (minCapacity > 0) {
            modCount++;
            if (minCapacity > elementData.length)
                grow(minCapacity);
        }
    }

    private Object[] grow(int minCapacity) {
        int oldCapacity = elementData.length;
        int newCapacity = newLength(oldCapacity, minCapacity - oldCapacity,
                capacityIncrement > 0 ? capacityIncrement : oldCapacity);
        return elementData = Arrays.copyOf(elementData, newCapacity);
    }

    private int newLength(int oldLength, int minGrowth, int prefGrowth) {
        int prefLength = oldLength + Math.max(minGrowth, prefGrowth); // might overflow
        if (0 < prefLength && prefLength <= Integer.MAX_VALUE - 16) {
            return prefLength;
        }
        throw new OutOfMemoryError("Required array length is too large");
    }

    private Object[] grow() {
        return grow(elementCount + 1);
    }


//    private void ensureCapacityHelper(int minCapacity) {
//        int oldCapacity = elementData.length;
//        if (minCapacity > oldCapacity) {
//            Object oldData[] = elementData;
//            int newCapacity = (capacityIncrement > 0) ?
//            (oldCapacity + capacityIncrement) : (oldCapacity * 2);
//            if (newCapacity < minCapacity) {
//                newCapacity = minCapacity;
//            }
//            elementData = new Object[newCapacity];
//            System.arraycopy(oldData, 0, elementData, 0, elementCount);
//        }
//    }

    @Override
    public void setSize(int newSize) {
        modCount++;
        if (newSize > elementData.length)
            grow(newSize);
        final Object[] es = elementData;
        for (int to = elementCount, i = newSize; i < to; i++)
            es[i] = null;
        elementCount = newSize;
    }

    @Override
    public int capacity() {
        return elementData.length;
    }

    @Override
    public Object clone() {
        return new NonSynchronizedVector<>(this);
    }

    @Override
    public int size() {
        return elementCount;
    }

    @Override
    public boolean isEmpty() {
        return elementCount == 0;
    }

    @Override
    public Enumeration<E> elements() {
        return new Enumeration<E>() {
            int count = 0;

            public boolean hasMoreElements() {
                return count < elementCount;
            }

            public E nextElement() {
                if (count < elementCount) {
                    return elementData(count++);
                }
                throw new NoSuchElementException("Vector Enumeration");
            }
        };
    }

    @Override
    public int indexOf(Object elem, int index) {
        if (elem == null) {
            for (int i = index ; i < elementCount ; i++)
                if (elementData[i]==null)
                    return i;
        } else {
            for (int i = index ; i < elementCount ; i++)
                if (elem.equals(elementData[i]))
                    return i;
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object elem) {
        return lastIndexOf(elem, elementCount-1);
    }

    @Override
    public int lastIndexOf(Object elem, int index) {
        if (index >= elementCount)
            throw new IndexOutOfBoundsException(index + " >= "+ elementCount);
        if (elem == null) {
            for (int i = index; i >= 0; i--)
                if (elementData[i]==null)
                    return i;
        } else {
            for (int i = index; i >= 0; i--)
                if (elem.equals(elementData[i]))
                    return i;
        }
        return -1;
    }

    @Override
    public E elementAt(int index) {
        if (index >= elementCount) {
            throw new ArrayIndexOutOfBoundsException(index + " >= " + elementCount);
        }

        return elementData(index);
    }

    @Override
    public E firstElement() {
        if (elementCount == 0) {
            throw new NoSuchElementException();
        }
        return elementData(0);
    }

    @Override
    public E lastElement() {
        if (elementCount == 0) {
            throw new NoSuchElementException();
        }
        return elementData(elementCount - 1);
    }

    @Override
    public void setElementAt(E obj, int index) {
        if (index >= elementCount) {
            throw new ArrayIndexOutOfBoundsException(index + " >= " +
                    elementCount);
        }
        elementData[index] = obj;
    }

    @Override
    public void removeElementAt(int index) {
        if (index >= elementCount) {
            throw new ArrayIndexOutOfBoundsException(index + " >= " +
                    elementCount);
        }
        else if (index < 0) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        int j = elementCount - index - 1;
        if (j > 0) {
            System.arraycopy(elementData, index + 1, elementData, index, j);
        }
        modCount++;
        elementCount--;
        elementData[elementCount] = null; /* to let gc do its work */
    }

    @Override
    public void insertElementAt(E obj, int index) {
        if (index > elementCount) {
            throw new ArrayIndexOutOfBoundsException(index
                    + " > " + elementCount);
        }
        modCount++;
        final int s = elementCount;
        Object[] elementData = this.elementData;
        if (s == elementData.length)
            elementData = grow();
        System.arraycopy(elementData, index,
                elementData, index + 1,
                s - index);
        elementData[index] = obj;
        elementCount = s + 1;
    }

    @Override
    public void addElement(E obj) {
        modCount++;
        add(obj, elementData, elementCount);
    }

    @Override
    public boolean removeElement(Object obj) {
        modCount++;
        int i = indexOf(obj);
        if (i >= 0) {
            removeElementAt(i);
            return true;
        }
        return false;
    }

    @Override
    public void removeAllElements() {
        final Object[] es = elementData;
        for (int to = elementCount, i = elementCount = 0; i < to; i++)
            es[i] = null;
        modCount++;
    }

    @Override
    public Object[] toArray() {
        return Arrays.copyOf(elementData, elementCount);
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public <T> T[] toArray(T[] a) {
        if (a.length < elementCount)
            return (T[]) Arrays.copyOf(elementData, elementCount, a.getClass());

        System.arraycopy(elementData, 0, a, 0, elementCount);

        if (a.length > elementCount)
            a[elementCount] = null;

        return a;
    }

    @SuppressWarnings("unchecked")
    E elementData(int index) {
        return (E) elementData[index];
    }

    @SuppressWarnings("unchecked")
    static <E> E elementAt(Object[] es, int index) {
        return (E) es[index];
    }

    @Override
    public E get(int index) {
        if (index >= elementCount)
            throw new ArrayIndexOutOfBoundsException(index);

        return elementData(index);
    }

    @Override
    public E set(int index, E element) {
        if (index >= elementCount)
            throw new ArrayIndexOutOfBoundsException(index);

        E oldValue = elementData(index);
        elementData[index] = element;
        return oldValue;
    }

    private void add(E e, Object[] elementData, int s) {
        if (s == elementData.length)
            elementData = grow();
        elementData[s] = e;
        elementCount = s + 1;
    }

    @Override
    public boolean add(E o) {
        modCount++;
        add(o, elementData, elementCount);
        return true;
    }

    @Override
    public E remove(int index) {
        modCount++;
        if (index >= elementCount)
            throw new ArrayIndexOutOfBoundsException(index);
        E oldValue = elementData(index);

        int numMoved = elementCount - index - 1;
        if (numMoved > 0)
            System.arraycopy(elementData, index+1, elementData, index,
                    numMoved);
        elementData[--elementCount] = null; // Let gc do its work

        return oldValue;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object e : c)
            if (!contains(e))
                return false;
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        Object[] a = c.toArray();
        modCount++;
        int numNew = a.length;
        if (numNew == 0)
            return false;
            Object[] elementData = this.elementData;
            final int s = elementCount;
            if (numNew > elementData.length - s)
                elementData = grow(s + numNew);
            System.arraycopy(a, 0, elementData, s, numNew);
            elementCount = s + numNew;
            return true;
    }

    private static long[] nBits(int n) {
        return new long[((n - 1) >> 6) + 1];
    }
    private static void setBit(long[] bits, int i) {
        bits[i >> 6] |= 1L << i;
    }
    private static boolean isClear(long[] bits, int i) {
        return (bits[i >> 6] & (1L << i)) == 0;
    }

    private boolean bulkRemove(Predicate<? super E> filter) {
        int expectedModCount = modCount;
        final Object[] es = elementData;
        final int end = elementCount;
        int i;
        // Optimize for initial run of survivors
        for (i = 0; i < end && !filter.test(elementAt(es, i)); i++)
            ;
        // Tolerate predicates that reentrantly access the collection for
        // read (but writers still get CME), so traverse once to find
        // elements to delete, a second pass to physically expunge.
        if (i < end) {
            final int beg = i;
            final long[] deathRow = nBits(end - beg);
            deathRow[0] = 1L;   // set bit 0
            for (i = beg + 1; i < end; i++)
                if (filter.test(elementAt(es, i)))
                    setBit(deathRow, i - beg);
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
            modCount++;
            int w = beg;
            for (i = beg; i < end; i++)
                if (isClear(deathRow, i - beg))
                    es[w++] = es[i];
            for (i = elementCount = w; i < end; i++)
                es[i] = null;
            return true;
        } else {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
            return false;
        }
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        Objects.requireNonNull(c);
        return bulkRemove(e -> c.contains(e));
    }

    @Override
    public boolean retainAll(Collection<?> c)  {
        Objects.requireNonNull(c);
        return bulkRemove(e -> !c.contains(e));
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        if (index < 0 || index > elementCount)
            throw new ArrayIndexOutOfBoundsException(index);

        Object[] a = c.toArray();
        modCount++;
        int numNew = a.length;
        if (numNew == 0)
            return false;
        Object[] elementData = this.elementData;
        final int s = elementCount;
        if (numNew > elementData.length - s)
            elementData = grow(s + numNew);

        int numMoved = s - index;
        if (numMoved > 0)
            System.arraycopy(elementData, index,
                    elementData, index + numNew,
                    numMoved);
        System.arraycopy(a, 0, elementData, index, numNew);
        elementCount = s + numNew;
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof List))
            return false;

        ListIterator<E> e1 = listIterator();
        ListIterator<?> e2 = ((List<?>) o).listIterator();
        while (e1.hasNext() && e2.hasNext()) {
            E o1 = e1.next();
            Object o2 = e2.next();
            if (!(o1==null ? o2==null : o1.equals(o2)))
                return false;
        }
        return !(e1.hasNext() || e2.hasNext());
    }

    @Override
    public int hashCode() {
        int hashCode = 1;
        for (E e : this)
            hashCode = 31*hashCode + (e==null ? 0 : e.hashCode());
        return hashCode;
    }

    @Override
    public String toString() {
        Iterator<E> it = iterator();
        if (! it.hasNext())
            return "[]";

        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (;;) {
            E e = it.next();
            sb.append(e == this ? "(this Collection)" : e);
            if (! it.hasNext())
                return sb.append(']').toString();
            sb.append(',').append(' ');
        }
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return new NonSynchronizedSubVector<E>(this, fromIndex, toIndex);
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        modCount++;
        shiftTailOverGap(elementData, fromIndex, toIndex);
    }

    private void shiftTailOverGap(Object[] es, int lo, int hi) {
        System.arraycopy(es, hi, es, lo, elementCount - hi);
        for (int to = elementCount, i = (elementCount -= hi - lo); i < to; i++)
            es[i] = null;
    }

    private void readObject(ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }

    private void writeObject(java.io.ObjectOutputStream s)
            throws java.io.IOException {
        s.defaultWriteObject();
    }

    public ListIterator<E> listIterator(int index) {
        if (index < 0 || index > elementCount)
            throw new IndexOutOfBoundsException("Index: "+index);
        return new ListItr(index);
    }

    public ListIterator<E> listIterator() {
        return new ListItr(0);
    }

    public Iterator<E> iterator() {
        return new Itr();
    }

    private class Itr implements Iterator<E> {
        int cursor;       // index of next element to return
        int lastRet = -1; // index of last element returned; -1 if no such
        int expectedModCount = modCount;

        public boolean hasNext() {
            // Racy but within spec, since modifications are checked
            // within or after synchronization in next/previous
            return cursor != elementCount;
        }

        public E next() {
                checkForComodification();
                int i = cursor;
                if (i >= elementCount)
                    throw new NoSuchElementException();
                cursor = i + 1;
                return elementData(lastRet = i);
        }

        public void remove() {
            if (lastRet == -1)
                throw new IllegalStateException();
                checkForComodification();
                NonSynchronizedVector.this.remove(lastRet);
                expectedModCount = modCount;
            cursor = lastRet;
            lastRet = -1;
        }

        @Override
        public void forEachRemaining(Consumer<? super E> action) {
            Objects.requireNonNull(action);
                final int size = elementCount;
                int i = cursor;
                if (i >= size) {
                    return;
                }
                final Object[] es = elementData;
                if (i >= es.length)
                    throw new ConcurrentModificationException();
                while (i < size && modCount == expectedModCount)
                    action.accept(elementAt(es, i++));
                // update once at end of iteration to reduce heap write traffic
                cursor = i;
                lastRet = i - 1;
                checkForComodification();
        }

        final void checkForComodification() {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }
    }

    /**
     * An optimized version of AbstractList.ListItr
     */
    final class ListItr extends Itr implements ListIterator<E> {
        ListItr(int index) {
            super();
            cursor = index;
        }

        public boolean hasPrevious() {
            return cursor != 0;
        }

        public int nextIndex() {
            return cursor;
        }

        public int previousIndex() {
            return cursor - 1;
        }

        public E previous() {
                checkForComodification();
                int i = cursor - 1;
                if (i < 0)
                    throw new NoSuchElementException();
                cursor = i;
                return elementData(lastRet = i);
        }

        public void set(E e) {
            if (lastRet == -1)
                throw new IllegalStateException();
                checkForComodification();
                NonSynchronizedVector.this.set(lastRet, e);
        }

        public void add(E e) {
            int i = cursor;
                checkForComodification();
            NonSynchronizedVector.this.add(i, e);
                expectedModCount = modCount;
            cursor = i + 1;
            lastRet = -1;
        }
    }

    @Override
    public void forEach(Consumer<? super E> action) {
        Objects.requireNonNull(action);
        final int expectedModCount = modCount;
        final Object[] es = elementData;
        final int size = elementCount;
        for (int i = 0; modCount == expectedModCount && i < size; i++)
            action.accept(elementAt(es, i));
        if (modCount != expectedModCount)
            throw new ConcurrentModificationException();
    }

    @Override
    public void replaceAll(UnaryOperator<E> operator) {
        Objects.requireNonNull(operator);
        final ListIterator<E> li = this.listIterator();
        while (li.hasNext()) {
            li.set(operator.apply(li.next()));
        }
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void sort(Comparator<? super E> c) {
        Object[] a = this.toArray();
        Arrays.sort(a, (Comparator) c);
        ListIterator<E> i = this.listIterator();
        for (Object e : a) {
            i.next();
            i.set((E) e);
        }
    }

    @Override
    public Spliterator<E> spliterator() {
        return new VectorSpliterator(null, 0, -1, 0);
    }

    /** Similar to ArrayList Spliterator */
    final class VectorSpliterator implements Spliterator<E> {
        private Object[] array;
        private int index; // current index, modified on advance/split
        private int fence; // -1 until used; then one past last index
        private int expectedModCount; // initialized when fence set

        /** Creates new spliterator covering the given range. */
        VectorSpliterator(Object[] array, int origin, int fence,
                          int expectedModCount) {
            this.array = array;
            this.index = origin;
            this.fence = fence;
            this.expectedModCount = expectedModCount;
        }

        private int getFence() { // initialize on first use
            int hi;
            if ((hi = fence) < 0) {
                    array = elementData;
                    expectedModCount = modCount;
                    hi = fence = elementCount;
            }
            return hi;
        }

        public Spliterator<E> trySplit() {
            int hi = getFence(), lo = index, mid = (lo + hi) >>> 1;
            return (lo >= mid) ? null :
                    new VectorSpliterator(array, lo, index = mid, expectedModCount);
        }

        @SuppressWarnings("unchecked")
        public boolean tryAdvance(Consumer<? super E> action) {
            Objects.requireNonNull(action);
            int i;
            if (getFence() > (i = index)) {
                index = i + 1;
                action.accept((E)array[i]);
                if (modCount != expectedModCount)
                    throw new ConcurrentModificationException();
                return true;
            }
            return false;
        }

        @SuppressWarnings("unchecked")
        public void forEachRemaining(Consumer<? super E> action) {
            Objects.requireNonNull(action);
            final int hi = getFence();
            final Object[] a = array;
            int i;
            for (i = index, index = hi; i < hi; i++)
                action.accept((E) a[i]);
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }

        public long estimateSize() {
            return getFence() - index;
        }

        public int characteristics() {
            return Spliterator.ORDERED | Spliterator.SIZED | Spliterator.SUBSIZED;
        }
    }
}

