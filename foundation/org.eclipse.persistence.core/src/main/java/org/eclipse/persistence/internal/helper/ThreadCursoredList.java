/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
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

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Vector;

import org.eclipse.persistence.exceptions.ValidationException;

/**
 * Special List/Vector subclass that allows concurrent
 * population of the contents while the list is in use.
 * The list will allow iteration while it is still being populated
 * to allow concurrent processing of the contents.
 * Other API such as size that require to full contents know will wait until
 * the list is notified as being complete.
 * This is use to allow the rows and objects of a read-all query to be processed concurrently.
 *
 * @author James Sutherland
 * @since OracleAS 10g TopLink (10.0.3)
 */
public class ThreadCursoredList extends Vector {

    /** Store if the list is fully populated. */
    protected boolean isComplete;

    /** Used to throw exception that occur from the concurrent population thread. */
    protected RuntimeException exception;

    /**
     * Construct an empty list so that its internal data array
     * has size <code>10</code> and its standard capacity increment is zero.
     */
    public ThreadCursoredList() {
        this(10);
    }

    /**
     * Construct an empty list with the specified initial capacity and
     * with its capacity increment equal to zero.
     */
    public ThreadCursoredList(int initialCapacity) {
        this(initialCapacity, 0);
    }

    /**
     * Construct an empty list with the specified initial capacity and
     * capacity increment.
     */
    public ThreadCursoredList(int initialCapacity, int capacityIncrement) {
        super(0);
        this.isComplete = false;
    }

    /**
     * Add and notify any waiters that there are new elements.
     */
    @Override
    public synchronized void add(int index, Object element) {
        super.add(index, element);
        this.notifyAll();
    }

    /**
     * Add and notify any waiters that there are new elements.
     */
    @Override
    public synchronized boolean add(Object element) {
        boolean result = super.add(element);
        notifyAll();
        return result;
    }

    /**
     * Add and notify any waiters that there are new elements.
     */
    @Override
    public synchronized boolean addAll(int index, Collection collection) {
        boolean result = super.addAll(index, collection);
        notifyAll();
        return result;
    }

    /**
     * Add and notify any waiters that there are new elements.
     */
    @Override
    public synchronized boolean addAll(Collection collection) {
        boolean result = super.addAll(collection);
        notifyAll();
        return result;
    }

    /**
     * Add and notify any waiters that there are new elements.
     */
    @Override
    public synchronized void addElement(Object object) {
        super.addElement(object);
        notifyAll();
    }

    /**
     * First wait until complete.
     */
    @Override
    public synchronized void clear() {
        waitUntilComplete();
        super.clear();
    }

    /**
     * First wait until complete.
     */
    @Override
    public synchronized Object clone() {
        waitUntilComplete();
        return super.clone();
    }

    /**
     * Return if any exception that was throw from concurrent population thread.
     */
    public boolean hasException() {
        return exception != null;
    }

    /**
     * Return any exception that was throw from concurrent population thread.
     */
    public RuntimeException getException() {
        return exception;
    }

    /**
     * Record that the population thread hit an exception,
     * that should be thrown to the processing thread on the next access.
     * This also records the list and complete.
     */
    public synchronized void throwException(RuntimeException exception) {
        this.exception = exception;
        setIsComplete(true);
    }

    /**
     * Return if the list is complete.
     * If an exception was thrown during the concurrent population throw the exception.
     */
    public synchronized boolean isComplete() {
        if (exception != null) {
            // Set the exception to null so it is only thrown once.
            RuntimeException thrownException = this.exception;
            this.exception = null;
            throw thrownException;
        }
        return isComplete;
    }

    /**
     * Set the list complete and notify any waiters.
     */
    public synchronized void setIsComplete(boolean isComplete) {
        this.isComplete = isComplete;
        notifyAll();
    }

    /**
     * Wait until the list has been fully populated.
     */
    public synchronized void waitUntilComplete() {
        while (!isComplete()) {
            try {
                wait();
            } catch (InterruptedException ignore) {
            }
        }
    }

    /**
     * Wait until a new element has been added.
     */
    public synchronized void waitUntilAdd() {
        try {
            wait();
        } catch (InterruptedException ignore) {
        }
    }

    /**
     * If it does not contain the object must wait until it is complete.
     */
    @Override
    public synchronized boolean contains(Object element) {
        boolean result = super.contains(element);
        if ((result != true) && (!isComplete())) {
            waitUntilComplete();
            result = super.contains(element);
        }
        return result;
    }

    /**
     * If it does not contain the object must wait until it is complete.
     */
    @Override
    public synchronized boolean containsAll(Collection collection) {
        boolean result = super.containsAll(collection);
        if ((result != true) && (!isComplete())) {
            waitUntilComplete();
            result = super.containsAll(collection);
        }
        return result;
    }

    /**
     * First wait until complete.
     */
    @Override
    public synchronized void copyInto(Object[] array) {
        waitUntilComplete();
        super.copyInto(array);
    }

    /**
     * If the index is beyond the size wait until complete.
     */
    @Override
    public synchronized Object elementAt(int index) {
        Object result = super.elementAt(index);
        if ((result == null) && (!isComplete())) {
            waitUntilComplete();
            result = super.elementAt(index);
        }
        return result;
    }

    protected int getSize() {
        return super.size();
    }

    /**
     * Allow concurrent streaming of the elements.
     */
    @Override
    public Enumeration elements() {
        return new Enumeration() {
                int count = 0;

                @Override
                public boolean hasMoreElements() {
                    synchronized (ThreadCursoredList.this) {
                        boolean result = count < ThreadCursoredList.this.getSize();
                        while ((!result) && (!isComplete())) {
                            waitUntilAdd();
                            result = count < ThreadCursoredList.this.getSize();
                        }
                        return result;
                    }
                }

                @Override
                public Object nextElement() {
                    synchronized (ThreadCursoredList.this) {
                        boolean result = count < ThreadCursoredList.this.getSize();
                        while ((!result) && (!isComplete())) {
                            waitUntilAdd();
                            result = count < ThreadCursoredList.this.getSize();
                        }
                        if (result) {
                            return get(count++);
                        }
                    }
                    throw new NoSuchElementException("Vector Enumeration");
                }
            };
    }

    /**
     * First wait until complete.
     */
    @Override
    public synchronized boolean equals(Object object) {
        waitUntilComplete();
        return super.equals(object);
    }

    /**
     * Wait until has an element or is complete.
     */
    @Override
    public synchronized Object firstElement() {
        while ((!isComplete()) && (super.size() < 1)) {
            waitUntilAdd();
        }
        return super.firstElement();
    }

    /**
     * Wait until has the element or is complete.
     */
    @Override
    public synchronized Object get(int index) {
        while ((!isComplete()) && (super.size() < index)) {
            waitUntilAdd();
        }
        return super.get(index);
    }

    /**
     * First wait until complete.
     */
    @Override
    public synchronized int hashCode() {
        waitUntilComplete();
        return super.hashCode();
    }

    /**
     * If does not contain the object wait until complete.
     */
    @Override
    public int indexOf(Object element) {
        int result = super.indexOf(element);
        if ((result == -1) && (!isComplete())) {
            waitUntilComplete();
            result = super.indexOf(element);
        }
        return result;
    }

    /**
     * If does not contain the object wait until complete.
     */
    @Override
    public synchronized int indexOf(Object element, int index) {
        int result = super.indexOf(element, index);
        if ((result == -1) && (!isComplete())) {
            waitUntilComplete();
            result = super.indexOf(element, index);
        }
        return result;
    }

    /**
     * Add the element a notify any waiters that there are new elements.
     */
    @Override
    public synchronized void insertElementAt(Object element, int index) {
        super.insertElementAt(element, index);
        notify();
    }

    /**
     * If empty wait until an element has been added or is complete.
     */
    @Override
    public boolean isEmpty() {
        boolean result = super.isEmpty();
        if (result && (!isComplete())) {
            waitUntilAdd();
            result = super.isEmpty();
        }
        return result;
    }

    @Override
    public Iterator iterator() {
        return listIterator(0);
    }

    /**
     * First wait until complete.
     */
    @Override
    public synchronized Object lastElement() {
        waitUntilComplete();
        return super.lastElement();
    }

    /**
     * First wait until complete.
     */
    @Override
    public int lastIndexOf(Object element) {
        waitUntilComplete();
        return super.lastIndexOf(element);
    }

    /**
     * First wait until complete.
     */
    @Override
    public synchronized int lastIndexOf(Object element, int index) {
        waitUntilComplete();
        return super.lastIndexOf(element, index);
    }

    @Override
    public ListIterator listIterator() {
        return listIterator(0);
    }

    /**
     * Iterate while waiting at end until complete.
     */
    @Override
    public ListIterator listIterator(final int index) {
        return new ListIterator() {
            int count = index;

            @Override
            public boolean hasNext() {
                synchronized (ThreadCursoredList.this) {
                    boolean result = count < ThreadCursoredList.this.getSize();
                    while ((!result) && (!isComplete())) {
                        waitUntilAdd();
                        result = count < ThreadCursoredList.this.getSize();
                    }
                    return result;
                }
            }

            @Override
            public Object next() {
                synchronized (ThreadCursoredList.this) {
                    boolean result = count < ThreadCursoredList.this.getSize();
                    while ((!result) && (!isComplete())) {
                        waitUntilAdd();
                        result = count < ThreadCursoredList.this.getSize();
                    }
                    if (result) {
                        return get(count++);
                    }
                }
                throw new NoSuchElementException("Vector Iterator");
            }

            @Override
            public void remove() {
                throw ValidationException.operationNotSupported("remove");
            }

            @Override
            public void set(Object object) {
                throw ValidationException.operationNotSupported("set");
            }

            @Override
            public void add(Object object) {
                throw ValidationException.operationNotSupported("add");
            }

            @Override
            public int previousIndex() {
                return count - 1;
            }

            @Override
            public int nextIndex() {
                return count;
            }

            @Override
            public Object previous() {
                count--;
                return get(count);
            }

            @Override
            public boolean hasPrevious() {
                return count > 0;
            }
        };
    }

    /**
     * If index is missing wait until is there.
     */
    @Override
    public synchronized Object remove(int index) {
        while ((!isComplete()) && (super.size() < index)) {
            waitUntilAdd();
        }
        return super.remove(index);
    }

    /**
     * If object is missing wait until complete.
     */
    @Override
    public boolean remove(Object element) {
        boolean result = super.remove(element);
        if ((!result) && (!isComplete())) {
            waitUntilAdd();
            result = super.remove(element);
        }
        return result;
    }

    /**
     * First wait until complete.
     */
    @Override
    public synchronized boolean removeAll(Collection collection) {
        waitUntilComplete();
        return super.removeAll(collection);
    }

    /**
     * First wait until complete.
     */
    @Override
    public synchronized void removeAllElements() {
        waitUntilComplete();
        super.removeAllElements();
    }

    /**
     * If missing wait until complete.
     */
    @Override
    public synchronized boolean removeElement(Object element) {
        boolean result = super.removeElement(element);
        if ((!result) && (!isComplete())) {
            waitUntilAdd();
            result = super.removeElement(element);
        }
        return result;
    }

    /**
     * If index is missing wait until reasched or complete.
     */
    @Override
    public synchronized void removeElementAt(int index) {
        while ((!isComplete()) && (super.size() < index)) {
            waitUntilAdd();
        }
        super.removeElementAt(index);
    }

    /**
     * First wait until complete.
     */
    @Override
    public synchronized boolean retainAll(Collection collection) {
        waitUntilComplete();
        return super.retainAll(collection);
    }

    /**
     * If index is missing wait until reached or complete.
     */
    @Override
    public synchronized Object set(int index, Object element) {
        while ((!isComplete()) && (super.size() < index)) {
            waitUntilAdd();
        }
        return super.set(index, element);
    }

    /**
     * If index is missing wait until reached or complete.
     */
    @Override
    public synchronized void setElementAt(Object element, int index) {
        while ((!isComplete()) && (super.size() < index)) {
            waitUntilAdd();
        }
        super.setElementAt(element, index);
    }

    /**
     * First wait until complete.
     */
    @Override
    public int size() {
        waitUntilComplete();
        return super.size();
    }

    /**
     * If index is missing wait until reached or complete.
     */
    @Override
    public List subList(int fromIndex, int toIndex) {
        while ((!isComplete()) && (super.size() < toIndex)) {
            waitUntilAdd();
        }
        return super.subList(fromIndex, toIndex);
    }

    /**
     * First wait until complete.
     */
    @Override
    public synchronized Object[] toArray() {
        waitUntilComplete();
        return super.toArray();
    }

    /**
     * First wait until complete.
     */
    @Override
    public synchronized Object[] toArray(Object[] array) {
        waitUntilComplete();
        return super.toArray(array);
    }

    /**
     * First wait until complete.
     */
    @Override
    public synchronized String toString() {
        waitUntilComplete();
        return super.toString();
    }

    /**
     * First wait until complete.
     */
    @Override
    public synchronized void trimToSize() {
        waitUntilComplete();
        super.trimToSize();
    }
}
