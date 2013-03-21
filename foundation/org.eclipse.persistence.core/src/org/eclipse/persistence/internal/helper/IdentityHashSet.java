/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.helper;


/**
 * INTERNAL:
 * <p>
 * <b>Purpose</b>: Define a {@link Set} that tests equality by reference,
 * not equals(). This is required to track objects throughout the lifecycle
 * of a {@link org.eclipse.persistence.sessions.UnitOfWork}, regardless if the domain
 * object redefines its equals() method. Additionally, this implementation does
 * <b>not</b> allow null elements.
 * <p>
 * This class does <b>not</b> inherit from {@link AbstractSet} because the
 * method {@link AbstractSet#removeAll removeAll(Collection c)} does not work
 * correctly with reference equality testing (NB the Javadocs for
 * {@link AbstractCollection} indicates that removeAll is an optional method).
 *
 * @author Mike Norman (since TopLink 10.1.3)
 *
 */

// J2SE imports
import java.io.*;
import java.util.*;

public class IdentityHashSet extends AbstractCollection implements Set, Cloneable, Serializable {
    static final long serialVersionUID = 1619330892277906704L;

    // the default initial capacity
    static final int DEFAULT_INITIAL_CAPACITY = 32;

    // the maximum capacity.
    static final int MAXIMUM_CAPACITY = 1 << 30;

    // the loadFactor used when none specified in constructor.
    static final float DEFAULT_LOAD_FACTOR = 0.75f;
    protected transient Entry[] entries;// internal array of Entry's
    protected transient int count = 0;
    protected int threshold = 0;
    protected float loadFactor = 0;

    /**
     * Constructs a new <tt>IdentityHashSet</tt> with the given initial
     * capacity and the given loadFactor.
     *
     * @param initialCapacity the initial capacity of the
     * <tt>IdentityHashSet</tt>.
     * @param loadFactor the loadFactor of the <tt>IdentityHashSet</tt>.
     * @throws <tt>IllegalArgumentException</tt> if the initial capacity is less
     * than zero, or if the loadFactor is nonpositive.
     */
    public IdentityHashSet(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal initialCapacity: " + initialCapacity);
        }
        if (initialCapacity > MAXIMUM_CAPACITY) {
            initialCapacity = MAXIMUM_CAPACITY;
        }
        if ((loadFactor <= 0) || Float.isNaN(loadFactor)) {
            throw new IllegalArgumentException("Illegal loadFactor: " + loadFactor);
        }

        // Find a power of 2 >= initialCapacity
        int capacity = 1;
        while (capacity < initialCapacity) {
            capacity <<= 1;
        }
        this.loadFactor = loadFactor;
        threshold = (int)(capacity * loadFactor);
        entries = new Entry[capacity];
    }

    /**
     * Constructs a new <tt>IdentityHashSet</tt> with the given
     * initial capacity and a default loadFactor of <tt>0.75</tt>.
     *
     * @param initialCapacity the initial capacity of the IdentityHashSet.
     * @throws <tt>IllegalArgumentException</tt> if the initial capacity is less
     * than zero.
     */
    public IdentityHashSet(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    /**
     * Constructs a new <tt>IdentityHashSet</tt> with a default initial
     * capacity of <tt>32</tt> and a loadfactor of <tt>0.75</tt>.
     */
    public IdentityHashSet() {
        loadFactor = DEFAULT_LOAD_FACTOR;
        threshold = (int)(DEFAULT_INITIAL_CAPACITY * DEFAULT_LOAD_FACTOR);
        entries = new Entry[DEFAULT_INITIAL_CAPACITY];
    }

    /**
     * Constructs a new <tt>IdentityHashSet</tt> with the same contents
     * as the given <tt>Collection</tt>. The new <tt>IdentityHashSet</tt>
     * is created with an initial capacity sufficient to hold the elements of
     * the given <tt>Collection</tt>.
     *
     * @param c the <tt>Collection</tt> whose contents are to be placed in the
     * new <tt>IdentityHashSet</tt>.
     */
    public IdentityHashSet(Collection c) {
        this(Math.max((int)(c.size() / DEFAULT_LOAD_FACTOR) + 1, DEFAULT_INITIAL_CAPACITY), DEFAULT_LOAD_FACTOR);
        addAll(c);
    }

    /**
     * @return the size of this <tt>IdentityHashSet</tt>.
     */
    public int size() {
        return count;
    }

    /**
     * @return <tt>true</tt> if this <tt>IdentityHashSet</tt> is empty.
     */
    public boolean isEmpty() {
        return (count == 0);
    }

    /**
     * Returns <tt>true</tt> if this <tt>IdentityHashSet</tt> contains
     * the given object.
     *
     * @param obj the object to find.
     * @return <tt>true</tt> if this <tt>IdentityHashSet</tt> contains
     * obj by reference.
     */
    public boolean contains(Object obj) {
        if (obj == null) {
            return false;
        }

        Entry[] copyOfEntries = entries;
        int hash = System.identityHashCode(obj);
        int index = (hash & 0x7FFFFFFF) % copyOfEntries.length;
        for (Entry e = copyOfEntries[index]; e != null; e = e.next) {
            if ((e.hash == hash) && (obj == e.value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * INTERNAL:
     * Re-builds the internal array of Entry's with a larger capacity.
     * This method is called automatically when the number of objects in this
     * IdentityHashSet exceeds its current threshold.
     */
    private void rehash() {
        int oldCapacity = entries.length;
        Entry[] oldEntries = entries;
        int newCapacity = (oldCapacity * 2) + 1;
        Entry[] newEntries = new Entry[newCapacity];
        threshold = (int)(newCapacity * loadFactor);
        entries = newEntries;
        for (int i = oldCapacity; i-- > 0;) {
            for (Entry old = oldEntries[i]; old != null;) {
                Entry e = old;
                old = old.next;
                int index = (e.hash & 0x7FFFFFFF) % newCapacity;
                e.next = newEntries[index];
                newEntries[index] = e;
            }
        }
    }

    /**
     * Adds the given object to this <tt>IdentityHashSet</tt>.
     *
     * @param obj object to add.
     * @return <tt>true</tt> if this <tt>IdentityHashSet</tt> did not
     * already contain obj.
     * @throws <tt>NullPointerException</tt> if obj is null.
     */
    public boolean add(Object obj) {
        if (obj == null) {
            throw new NullPointerException();
        }

        // Makes sure the object is not already in the IdentityHashSet.
        Entry[] copyOfEntries = entries;
        int hash = System.identityHashCode(obj);
        int index = (hash & 0x7FFFFFFF) % copyOfEntries.length;
        for (Entry e = copyOfEntries[index]; e != null; e = e.next) {
            if ((e.hash == hash) && (obj == e.value)) {
                return false;
            }
        }

        if (count >= threshold) {
            // Rehash the table if the threshold is exceeded
            rehash();
            copyOfEntries = entries;
            index = (hash & 0x7FFFFFFF) % copyOfEntries.length;
        }

        // Creates the new entry.
        Entry e = new Entry(hash, obj, copyOfEntries[index]);
        copyOfEntries[index] = e;
        count++;
        return true;
    }

    /**
     * Removes the given object from this <tt>IdentityHashSet</tt>, if
     * present.
     *
     * @param obj the object to be removed from this <tt>IdentityHashSet</tt>.
     * @return <tt>true</tt> if this <tt>IdentityHashSet</tt> contained
     * obj.
     */
    public boolean remove(Object obj) {
        if (obj == null) {
            return false;
        }

        Entry[] copyOfEntries = entries;
        int hash = System.identityHashCode(obj);
        int index = (hash & 0x7FFFFFFF) % copyOfEntries.length;
        for (Entry e = copyOfEntries[index], prev = null; e != null; prev = e, e = e.next) {
            if ((e.hash == hash) && (obj == e.value)) {
                if (prev != null) {
                    prev.next = e.next;
                } else {
                    copyOfEntries[index] = e.next;
                }
                count--;
                return true;
            }
        }
        return false;
    }

    /**
     * This implementation throws an <tt>UnsupportedOperationException</tt>
     * because <tt>removeAll</tt> does not work correctly with reference
     * equality testing.<p>
     */
    public boolean removeAll(Collection c) {
        throw new UnsupportedOperationException("IdentityHashSet removeAll");
    }

    /**
     * This implementation throws an <tt>UnsupportedOperationException</tt>.
     * The Javadocs for {@link AbstractCollection} indicates that <tt>retainAll</tt>
     * is an optional method.<p>
     */
    public boolean retainAll(Collection c) {
        throw new UnsupportedOperationException("IdentityHashSet retainAll");
    }

    /**
     * Removes all of the objects from this <tt>IdentityHashSet</tt>.
     */
    public void clear() {
        if (count > 0) {
            Entry[] copyOfEntries = entries;
            for (int i = copyOfEntries.length; --i >= 0;) {
                copyOfEntries[i] = null;
            }
            count = 0;
        }
    }

    /**
     * Returns a shallow copy of this <tt>IdentityHashSet</tt> (the
     * elements are not cloned).
     *
     * @return a shallow copy of this <tt>IdentityHashSet</tt>.
     */
    public Object clone() {
        try {
            Entry[] copyOfEntries = entries;
            IdentityHashSet clone = (IdentityHashSet)super.clone();
            clone.entries = new Entry[copyOfEntries.length];
            for (int i = copyOfEntries.length; i-- > 0;) {
                clone.entries[i] = (copyOfEntries[i] != null) ? (Entry)copyOfEntries[i].clone() : null;
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }

    /**
     * Get an iterator for this <tt>IdentityHashSet</tt>
     */
    public Iterator iterator() {
        return new IdentityHashSetIterator();
    }

    /**
     * IdentityHashSet entry.
     */
    static class Entry {
        int hash;
        Object value;
        Entry next;

        Entry(int hash, Object value, Entry next) {
            this.hash = hash;
            this.value = value;
            this.next = next;
        }

        protected Object clone() {
            Entry currentNode = this;
            Entry rootNode = new Entry(hash, value, null);
            Entry currentNodeClone = rootNode;

            while (currentNode.next != null) {
                currentNodeClone.next = new Entry(currentNode.next.hash, currentNode.next.value, null);
                currentNode = currentNode.next;
                currentNodeClone = currentNodeClone.next;
            }
            
            return rootNode;
        }
    }

    class IdentityHashSetIterator implements Iterator {
        Entry[] copyOfEntries = IdentityHashSet.this.entries;
        int index = copyOfEntries.length;
        Entry entry = null;
        Entry lastReturned = null;

        IdentityHashSetIterator() {
        }

        public boolean hasNext() {
            Entry e = entry;
            int i = index;
            Entry[] tmp = copyOfEntries;
            while ((e == null) && (i > 0)) {
                e = tmp[--i];
            }
            entry = e;
            index = i;
            return e != null;
        }

        public Object next() {
            Entry et = entry;
            int i = index;
            Entry[] tmp = copyOfEntries;

            while ((et == null) && (i > 0)) {
                et = tmp[--i];
            }
            entry = et;
            index = i;
            if (et != null) {
                Entry e = lastReturned = entry;
                entry = e.next;
                return e.value;
            }
            throw new NoSuchElementException();
        }

        public void remove() {
            if (lastReturned == null) {
                throw new IllegalStateException();
            }

            Entry[] copyOfEntries = IdentityHashSet.this.entries;
            int index = (lastReturned.hash & 0x7FFFFFFF) % copyOfEntries.length;
            for (Entry e = copyOfEntries[index], prev = null; e != null; prev = e, e = e.next) {
                if (e == lastReturned) {
                    if (prev == null) {
                        copyOfEntries[index] = e.next;
                    } else {
                        prev.next = e.next;
                    }
                    count--;
                    lastReturned = null;
                    return;
                }
            }
            throw new ConcurrentModificationException();
        }
    }

    /**
     * Serialize the state of this <tt>IdentityHashSet</tt> to a stream.
     *
     * @serialData The <i>capacity</i> of the <tt>IdentityHashSet</tt>
     * (the length of the bucket array) is emitted (int), followed by the
     * <i>size</i> of the <tt>IdentityHashSet</tt>, followed by the
     * contents (in no particular order).
     */
    private void writeObject(ObjectOutputStream s) throws IOException, ClassNotFoundException {
        // Write out the threshold, loadfactor (and any hidden 'magic' stuff).
        s.defaultWriteObject();

        // Write out number of buckets
        s.writeInt(entries.length);
        // Write out count
        s.writeInt(count);
        // Write out contents
        for (Iterator i = iterator(); i.hasNext();) {
            s.writeObject(i.next());
        }
    }

    /**
     * Deserialize the <tt>IdentityHashSet</tt> from a stream.
     */
    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        // Read in the threshold, loadfactor (and any hidden 'magic' stuff).
        s.defaultReadObject();

        // Read in number of buckets and allocate the bucket array;
        int numBuckets = s.readInt();
        entries = new Entry[numBuckets];
        // Read in size (count)
        int size = s.readInt();

        // Read the objects and add to the IdentityHashSet
        for (int i = 0; i < size; i++) {
            add(s.readObject());
        }
    }
}
