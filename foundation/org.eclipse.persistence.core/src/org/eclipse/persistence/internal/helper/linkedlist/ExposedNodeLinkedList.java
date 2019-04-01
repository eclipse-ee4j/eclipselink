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
package org.eclipse.persistence.internal.helper.linkedlist;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.eclipse.persistence.exceptions.ValidationException;

/**
 * INTERNAL:
 * A custom implementation of a linked list.  This list exposes the linked nodes
 * directly to the developer.  It allows nodes to be referenced in code for quick
 * list manipulation (ie reshuffle, remove, or queuing)
 * It is specifically used in the EclipseLink cache write lock mechanism in order
 * to allow quick removal of objects from the list while still providing the getFirst()
 * addLast() functionality of a queue.  The alternative java classes LinkedList, LinkedHashMap
 * do not provide both functional requirements.
 * @author Gordon Yorke
 * @since 10.0.3
 * @see org.eclipse.persistence.internal.helper.linkedlist.LinkedNode
 */
public class ExposedNodeLinkedList implements List {
    private transient LinkedNode header;
    private transient int size;

    /**
     * Constructs an empty list.
     */
    public ExposedNodeLinkedList() {
        this.size = 0;
        this.header = new LinkedNode(null, null, null);
        header.next = header;
        header.previous = header;
    }

    // Bunch of List methods not currently implemented.
    @Override
    public Object[] toArray(Object[] array) {
        throw ValidationException.operationNotSupported("toArray");
    }

    @Override
    public Object[] toArray() {
        throw ValidationException.operationNotSupported("toArray");
    }

    @Override
    public Object set(int index, Object value) {
        throw ValidationException.operationNotSupported("set");
    }

    @Override
    public ListIterator listIterator(int index) {
        throw ValidationException.operationNotSupported("listIterator");
    }

    @Override
    public ListIterator listIterator() {
        throw ValidationException.operationNotSupported("listIterator");
    }

    @Override
    public Iterator iterator() {
        throw ValidationException.operationNotSupported("iterator");
    }

    @Override
    public List subList(int start, int end) {
        throw ValidationException.operationNotSupported("subList");
    }

    @Override
    public boolean retainAll(Collection collection) {
        throw ValidationException.operationNotSupported("retainAll");
    }

    @Override
    public boolean removeAll(Collection collection) {
        throw ValidationException.operationNotSupported("removeAll");
    }

    @Override
    public boolean containsAll(Collection collection) {
        throw ValidationException.operationNotSupported("containsAll");
    }

    @Override
    public boolean addAll(Collection collection) {
        throw ValidationException.operationNotSupported("addAll");
    }

    @Override
    public boolean addAll(int index, Collection collection) {
        throw ValidationException.operationNotSupported("addAll");
    }

    @Override
    public boolean remove(Object object) {
        throw ValidationException.operationNotSupported("remove");
    }

    @Override
    public boolean add(Object object) {
        addLast(object);
        return true;
    }

    @Override
    public int lastIndexOf(Object object) {
        throw ValidationException.operationNotSupported("lastIndexOf");
    }

    @Override
    public void add(int index, Object object) {
        throw ValidationException.operationNotSupported("add");
    }

    @Override
    public Object remove(int index) {
        throw ValidationException.operationNotSupported("remove");
    }

    @Override
    public Object get(int index) {
        throw ValidationException.operationNotSupported("get");
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Returns the first contents in this list.
     *
     * @return the first contents in this list. Null if this list is empty.
     */
    public Object getFirst() {
        if (size == 0) {
            return null;
        }
        return header.next.contents;
    }

    /**
     * Returns the last contents in this list.
     *
     * @return the last contents in this list. Null if this list is empty.
     */
    public Object getLast() {
        if (size == 0) {
            return null;
        }
        return header.previous.contents;
    }

    /**
     * Removes and returns the first contents from this list.
     *
     * @return the first contents from this list.
     * @throws    NoSuchElementException if this list is empty.
     */
    public Object removeFirst() {
        if (size != 0) {
            Object first = header.next.contents;
            remove(header.next);
            return first;
        }
        return null;
    }

    /**
       * Removes and returns the last contents from this list.
     *
     * @return the last contents from this list.
     * @throws    NoSuchElementException if this list is empty.
     */
    public Object removeLast() {
        if (size != 0) {
            Object last = header.previous.contents;
            remove(header.previous);
            return last;
        }
        return null;
    }

    /**
     * Inserts the given contents at the beginning of this list.
     *
     * @param o the contents to be inserted at the beginning of this list.
     */
    public LinkedNode addFirst(Object o) {
        return addAfter(o, header);
    }

    /**
     * Appends the given contents to the end of this list.  (Identical in
     * function to the <code>add</code> method; included only for consistency.)
     *
     * @param o the contents to be inserted at the end of this list.
     */
    public LinkedNode addLast(Object o) {
        return addAfter(o, header.previous);
    }

    /**
     * Returns <code>true</code> if this list contains the specified contents.
     * More formally, returns <code>true</code> if and only if this list contains
     * at least one contents <code>e</code> such that <code>(o==null ? e==null
     * : o.equals(e))</code>.
     *
     * @param o contents whose presence in this list is to be tested.
     * @return <code>true</code> if this list contains the specified contents.
     */
    @Override
    public boolean contains(Object o) {
        return indexOf(o) != -1;
    }

    /**
     * Returns the number of contents in this list.
     *
     * @return the number of contents in this list.
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Removes all of the contents from this list.
     */
    @Override
    public void clear() {
        header.next = header;
        header.previous = header;
        size = 0;
    }

    /**
     * Returns the index in this list of the first occurrence of the
     * specified contents, or -1 if the List does not contain this
     * contents.  More formally, returns the lowest index i such that
     * <code>(o==null ? get(i)==null : o.equals(get(i)))</code>, or -1 if
     * there is no such index.
     *
     * @param o contents to search for.
     * @return the index in this list of the first occurrence of the
     *            specified contents, or -1 if the list does not contain this
     *            contents.
     */
    @Override
    public int indexOf(Object o) {
        int index = 0;
        if (o == null) {
            for (LinkedNode n = header.next; n != header; n = n.next) {
                if (n.contents == null) {
                    return index;
                }
                index++;
            }
        } else {
            for (LinkedNode n = header.next; n != header; n = n.next) {
                if (o.equals(n.contents)) {
                    return index;
                }
                index++;
            }
        }
        return -1;
    }

    private LinkedNode addAfter(Object o, LinkedNode n) {
        LinkedNode newNode = new LinkedNode(o, n.next, n);
        newNode.previous.next = newNode;
        newNode.next.previous = newNode;
        size++;
        return newNode;
    }

    /**
     * Allows a node to be efficiently removed.
     */
    public void remove(LinkedNode n) {
        if (n == header) {
            throw new NoSuchElementException();
        } else if ((n.previous == null) || (n.next == null)) {
            // Handles case of node having already been removed.
            return;
        }
        n.previous.next = n.next;
        n.next.previous = n.previous;
        // Also clear the nodes references to know that it has been removed.
        n.previous = null;
        n.next = null;
        n.contents = null;
        size--;
    }

    /**
     * Allows a node to be efficiently moved first.
     */
    public void moveFirst(LinkedNode node) {
        if (node == header) {
            throw new NoSuchElementException();
        } else if ((node.previous == null) || (node.next == null)) {
            // Handles case of node having already been removed.
            size++;
        } else {
            node.previous.next = node.next;
            node.next.previous = node.previous;
        }
        node.next = header.next;
        node.previous = header;
        header.next = node;
        node.next.previous = node;
    }
}
