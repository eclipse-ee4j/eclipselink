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
 *     Gordon Yorke
 ******************************************************************************/  
package org.eclipse.persistence.internal.helper;


/**
 * INTERNAL:
 * <p>
 * <b>Purpose</b>: Define a {@link Map} that manages key equality by reference,
 * not equals(). This is required to track objects throughout the lifecycle
 * of a {@link org.eclipse.persistence.sessions.UnitOfWork}, regardless if the domain
 * object redefines its equals() method. Additionally, this implementation does
 * <b>not</b> permit nulls either as values or as keys.  Any Entry that has a null in the key or
 * in the value will be assumed to have garbage collected.
 * This class also uses weak references to the contents of the map allowing for garbage
 * collection to reduce the size of the Map
 * 
 * This work is an extension of the original work completed on the IdentityWeakHashMap as completed by
 * Mike Norman.
 *
 * @author Gordon Yorke (EclipseLink 1.0M4)
 *
 */

// J2SE imports
import java.io.*;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.*;

import org.eclipse.persistence.internal.localization.ExceptionLocalization;

public class IdentityWeakHashMap<K,V> extends AbstractMap<K,V> implements Map<K,V>, Cloneable, Serializable {
    static final long serialVersionUID = -5176951017503351630L;

    // the default initial capacity
    static final int DEFAULT_INITIAL_CAPACITY = 32;

    // the maximum capacity.
    static final int MAXIMUM_CAPACITY = 1 << 30;

    // the loadFactor used when none specified in constructor.
    static final float DEFAULT_LOAD_FACTOR = 0.75f;
    protected transient WeakEntry<K,V>[] entries;// internal array of Entry's
    protected transient int count = 0;
    private transient int modCount = 0;// # of times this Map has been modified
    protected int threshold = 0;
    protected float loadFactor = 0;
    
    /** This is used by the garbage collector.  Every weak reference that is garbage collected
     * will be enqueued on this.  Then only this queue needs to be checked to remove empty
     * references.
     */
    protected ReferenceQueue referenceQueue;

    /**
     * Constructs a new <tt>IdentityWeakHashMap</tt> with the given
     * initial capacity and the given loadFactor.
     *
     * @param initialCapacity the initial capacity of this
     * <tt>IdentityWeakHashMap</tt>.
     * @param loadFactor the loadFactor of the <tt>IdentityWeakHashMap</tt>.
     * @throws IllegalArgumentException  if the initial capacity is less
     * than zero, or if the loadFactor is nonpositive.
     */
    public IdentityWeakHashMap(int initialCapacity, float loadFactor) {
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
        entries = new WeakEntry[capacity];
        referenceQueue = new ReferenceQueue();
    }

    /**
     * Constructs a new <tt>IdentityWeakHashMap</tt> with the given
     * initial capacity and a default loadFactor of <tt>0.75</tt>.
     *
     * @param initialCapacity the initial capacity of the
     * <tt>IdentityWeakHashMap</tt>.
     * @throws <tt>IllegalArgumentException</tt> if the initial capacity is less
     * than zero.
     */
    public IdentityWeakHashMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    /**
     * Constructs a new <tt>IdentityWeakHashMap</tt> with a default initial
     * capacity of <tt>32</tt> and a loadfactor of <tt>0.75</tt>.
     */
    public IdentityWeakHashMap() {
        loadFactor = DEFAULT_LOAD_FACTOR;
        threshold = (int)(DEFAULT_INITIAL_CAPACITY * DEFAULT_LOAD_FACTOR);
        entries = new WeakEntry[DEFAULT_INITIAL_CAPACITY];
        referenceQueue = new ReferenceQueue();
    }

    /**
     * Constructs a new <tt>IdentityWeakHashMap</tt> with the same mappings
     * as the given map.  The <tt>IdentityWeakHashMap</tt> is created with a
     * capacity sufficient to hold the elements of the given map.
     *
     * @param m the map whose mappings are to be placed in the
     * <tt>IdentityWeakHashMap</tt>.
     */
    public IdentityWeakHashMap(Map m) {
        this(Math.max((int)(m.size() / DEFAULT_LOAD_FACTOR) + 1, DEFAULT_INITIAL_CAPACITY), DEFAULT_LOAD_FACTOR);
        putAll(m);
    }

    /**
     * @return the size of this <tt>IdentityWeakHashMap</tt>.
     */
    public int size() {
        cleanUp();
        return count;
    }

    /**
     * @return <tt>true</tt> if this <tt>IdentityWeakHashMap</tt> is empty.
     */
    public boolean isEmpty() {
        return (count == 0);
    }

    /**
     * Returns <tt>true</tt> if this <tt>IdentityWeakHashMap</tt> contains
     * the given object. Equality is tested by the equals() method.
     *
     * @param obj the object to find.
     * @return <tt>true</tt> if this <tt>IdentityWeakHashMap</tt> contains
     * obj.
     * @throws <tt>NullPointerException</tt> if obj is null</tt>.
     */
    public boolean containsValue(Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("null_not_supported_identityweakhashmap"));
        }
        //cleanup before searching as to reduce number of possible empty Entries
        cleanUp();
        WeakEntry<K,V>[] copyOfEntries = entries;
        for (int i = copyOfEntries.length; i-- > 0;) {
            for (WeakEntry<K,V> e = copyOfEntries[i]; e != null; e = e.next) {
                if (obj.equals(e.value.get())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns <tt>true</tt> if this <tt>IdentityWeakHashMap</tt> contains a
     * mapping for the given key. Equality is tested by reference.
     *
     * @param key object to be used as a key into this
     * <tt>IdentityWeakHashMap</tt>.
     * @return <tt>true</tt> if this <tt>IdentityWeakHashMap</tt> contains a
     * mapping for key.
     */
    public boolean containsKey(Object key) {
        if (key == null) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("null_not_supported_identityweakhashmap"));
        }
        cleanUp();
        WeakEntry[] copyOfEntries = entries;
        int hash = System.identityHashCode(key);
        int index = (hash & 0x7FFFFFFF) % copyOfEntries.length;
        for (WeakEntry e = copyOfEntries[index]; e != null; e = e.next) {
            if (e.key.get() == key) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the value to which the given key is mapped in this
     * <tt>IdentityWeakHashMap</tt>. Returns <tt>null</tt> if this
     * <tt>IdentityWeakHashMap</tt> contains no mapping for this key.
     *
     * @return the value to which this <tt>IdentityWeakHashMap</tt> maps the
     * given key.
     * @param key key whose associated value is to be returned.
     */
    public V get(Object key) {
        if (key == null) return null;
        cleanUp();
        WeakEntry[] copyOfEntries = entries;
        int hash = System.identityHashCode(key);
        int index = (hash & 0x7FFFFFFF) % copyOfEntries.length;
        for (WeakEntry e = copyOfEntries[index]; e != null; e = e.next) {
            if (e.key.get() == key) {
                return (V)e.value.get();
            }
        }
        return null;
    }

    /**
     * INTERNAL:
     * Re-builds the internal array of Entry's with a larger capacity.
     * This method is called automatically when the number of objects in this
     * IdentityWeakHashMap exceeds its current threshold.
     */
    private void rehash() {
        int oldCapacity = entries.length;
        WeakEntry[] oldEntries = entries;
        int newCapacity = (oldCapacity * 2) + 1;
        WeakEntry[] newEntries = new WeakEntry[newCapacity];
        modCount++;
        threshold = (int)(newCapacity * loadFactor);
        entries = newEntries;
        for (int i = oldCapacity; i-- > 0;) {
            for (WeakEntry old = oldEntries[i]; old != null;) {
                WeakEntry e = old;
                old = old.next;
                int index = (e.hash & 0x7FFFFFFF) % newCapacity;
                e.next = newEntries[index];
                newEntries[index] = e;
            }
        }
    }

    /**
     * Associate the given object with the given key in this
     * <tt>IdentityWeakHashMap</tt>, replacing any existing mapping.
     *
     * @param key key to map to given object.
     * @param obj object to be associated with key.
     * @return the previous object for key or <tt>null</tt> if this
     * <tt>IdentityWeakHashMap</tt> did not have one.
     * @throws <tt>NullPointerException</tt> if obj is null</tt>.
     */
    public V put(K key, V obj) {
        if (obj == null || key == null) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("null_not_supported_identityweakhashmap"));
        }
        cleanUp();
        WeakEntry[] copyOfEntries = entries;
        int hash = System.identityHashCode(key);
        int index = (hash & 0x7FFFFFFF) % copyOfEntries.length;
        for (WeakEntry e = copyOfEntries[index]; e != null; e = e.next) {
            if (e.key.get() == key) {
                EntryReference<V> old = e.value;
                if (key == obj){
                    e.value = e.key;
                }else{
                    e.value = new HardEntryReference<V>(obj);
                }
                return old.get();
            }
        }

        modCount++;
        if (count >= threshold) {
            rehash();
            copyOfEntries = entries;
            index = (hash & 0x7FFFFFFF) % copyOfEntries.length;
        }
        WeakEntry<K,V> e = new WeakEntry<K,V>(hash, key, obj, copyOfEntries[index], referenceQueue);
        copyOfEntries[index] = e;
        count++;
        return null;
    }

    /**
     * Removes the mapping (key and its corresponding value) from this
     * <tt>IdentityWeakHashMap</tt>, if present.
     *
     * @param key key whose mapping is to be removed from the map.
     * @return the previous object for key or <tt>null</tt> if this
     * <tt>IdentityWeakHashMap</tt> did not have one.
     */
    public V remove(Object key) {
        if (key == null) return null;
        cleanUp();
        WeakEntry[] copyOfEntries = entries;
        int hash = System.identityHashCode(key);
        int index = (hash & 0x7FFFFFFF) % copyOfEntries.length;
        for (WeakEntry e = copyOfEntries[index], prev = null; e != null; prev = e, e = e.next) {
            if (e.key.get() == key) {
                if (prev != null) {
                    prev.next = e.next;
                } else {
                    copyOfEntries[index] = e.next;
                }
                count--;
                return (V)e.value.get();
            }
        }
        return null;
    }
    
    protected boolean removeEntry(WeakEntry o, boolean userModification) {

        WeakEntry[] copyOfEntries = entries;
        int index = (o.hash & 0x7FFFFFFF) % copyOfEntries.length;
        for (WeakEntry e = copyOfEntries[index], prev = null; e != null;
                 prev = e, e = e.next) {
            if (e == o) {
                // if this method was called as a result of a user action,
                // increment the modification count
                // this method is also called by our cleanup code and 
                // that code should not cause a concurrent modification
                // exception
                if (userModification){
                    modCount++;
                }
                if (prev != null) {
                    prev.next = e.next;
                } else {
                    copyOfEntries[index] = e.next;
                }
                count--;
                e.value = null;
                e.next = null;
                return true;
            }
        }
        return false;
    }

    /**
     * Copies all of the mappings from the given map to this
     * <tt>IdentityWeakHashMap</tt>, replacing any existing mappings.
     *
     * @param m mappings to be stored in this <tt>IdentityWeakHashMap</tt>.
     * @throws <tt>NullPointerException</tt> if m is null.
     */
    public void putAll(Map<? extends K, ? extends V> m) {
        if (m == null) {
            throw new NullPointerException();
        }

        Iterator<? extends Entry<? extends K, ? extends V>> i = m.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry<? extends K, ? extends V> me = i.next();
            put(me.getKey(), me.getValue());
        }
    }

    /**
     * Removes all of the mappings from this <tt>IdentityWeakHashMap</tt>.
     */
    public void clear() {
        if (count > 0) {
            modCount++;
            WeakEntry[] copyOfEntries = entries;
            for (int i = copyOfEntries.length; --i >= 0;) {
                copyOfEntries[i] = null;
            }
            count = 0;
        }
    }
    
    protected void cleanUp(){
        WeakEntryReference reference = (WeakEntryReference)referenceQueue.poll();
        while (reference != null){
            // remove the entry but do not increment the modcount
            // since this is not a user action
            removeEntry(reference.owner, false);
            reference = (WeakEntryReference)referenceQueue.poll();
        }
    }

    /**
     * Returns a shallow copy of this <tt>IdentityWeakHashMap</tt> (the
     * elements are not cloned).
     *
     * @return a shallow copy of this <tt>IdentityWeakHashMap</tt>.
     */
    public Object clone() {
        try {
            WeakEntry[] copyOfEntries = entries;
            IdentityWeakHashMap clone = (IdentityWeakHashMap)super.clone();
            clone.referenceQueue = new ReferenceQueue();
            clone.entries = new WeakEntry[copyOfEntries.length];
            for (int i = copyOfEntries.length; i-- > 0;) {
                clone.entries[i] = (copyOfEntries[i] != null) ? (WeakEntry)copyOfEntries[i].clone(clone.referenceQueue) : null;
            }
            clone.keySet = null;
            clone.entrySet = null;
            clone.values = null;
            clone.modCount = 0;
            return clone;
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }

    // Views - the following is standard 'boiler-plate' Map stuff
    private transient Set keySet = null;
    private transient Set entrySet = null;
    private transient Collection values = null;

    /**
     * Returns a set view of the keys contained in this
     * <tt>IdentityWeakHashMap</tt>.  The set is backed by the map, so
     * changes to the map are reflected in the set, and vice versa.  The set
     * supports element removal, which removes the corresponding mapping from
     * this map, via the <tt>Iterator.remove</tt>, <tt>Set.remove</tt>,
     * <tt>removeAll</tt>, <tt>retainAll</tt>, and <tt>clear</tt> operations.
     * It does not support the <tt>add</tt> or <tt>addAll</tt> operations.
     *
     * @return a set view of the keys contained in this
     * <tt>IdentityWeakHashMap</tt>.
     */
    public Set keySet() {
        if (keySet == null) {
            keySet = new AbstractSet() {
                        public Iterator iterator() {
                            return getHashIterator(COMPONENT_TYPES.KEYS);
                        }

                        public int size() {
                            return count;
                        }

                        public boolean contains(Object o) {
                            return containsKey(o);
                        }

                        public boolean remove(Object o) {
                            int oldSize = count;
                            IdentityWeakHashMap.this.remove(o);
                            return count != oldSize;
                        }

                        public void clear() {
                        	IdentityWeakHashMap.this.clear();
                        }
                    };
        }
        return keySet;
    }

    /**
     * Returns a collection view of the values contained in this
     * <tt>IdentityWeakHashMap</tt>.  The collection is backed by the map, so
     * changes to the map are reflected in the collection, and vice versa.  The
     * collection supports element removal, which removes the corresponding
     * mapping from this map, via the <tt>Iterator.remove</tt>,
     * <tt>Collection.remove</tt>, <tt>removeAll</tt>, <tt>retainAll</tt>, and
     * <tt>clear</tt> operations. It does not support the <tt>add</tt> or
     * <tt>addAll</tt> operations.
     *
     * @return a collection view of the values contained in this
     * <tt>IdentityWeakHashMap</tt>.
     */
    public Collection values() {
        if (values == null) {
            values = new AbstractCollection() {
                        public Iterator iterator() {
                            return getHashIterator(COMPONENT_TYPES.VALUES);
                        }

                        public int size() {
                            return count;
                        }

                        public boolean contains(Object o) {
                            return containsValue(o);
                        }

                        public void clear() {
                        	IdentityWeakHashMap.this.clear();
                        }
                    };
        }
        return values;
    }

    /**
     * Returns a collection view of the mappings contained in this
     * <tt>IdentityWeakHashMap</tt>.  Each element in the returned collection
     * is a <tt>Map.Entry</tt>.  The collection is backed by the map, so changes
     * to the map are reflected in the collection, and vice versa.  The
     * collection supports element removal, which removes the corresponding
     * mapping from the map, via the <tt>Iterator.remove</tt>,
     * <tt>Collection.remove</tt>, <tt>removeAll</tt>, <tt>retainAll</tt>, and
     * <tt>clear</tt> operations. It does not support the <tt>add</tt> or
     * <tt>addAll</tt> operations.
     *
     * @return a collection view of the mappings contained in this
     * <tt>IdentityWeakHashMap</tt>.
     */
    public Set entrySet() {
        if (entrySet == null) {
            entrySet = new AbstractSet() {
                        public Iterator iterator() {
                            return getHashIterator(COMPONENT_TYPES.ENTRIES);
                        }

                        public boolean contains(Object o) {
                            if (!(o instanceof Map.Entry)) {
                                return false;
                            }

                            Map.Entry entry = (Map.Entry)o;
                            Object key = entry.getKey();
                            WeakEntry[] copyOfEntries = entries;
                            int hash = System.identityHashCode(key);
                            int index = (hash & 0x7FFFFFFF) % copyOfEntries.length;
                            for (WeakEntry e = copyOfEntries[index]; e != null; e = e.next) {
                                if ((e.hash == hash) && e.equals(entry)) {
                                    return true;
                                }
                            }
                            return false;
                        }

                        public boolean remove(Object o) {
                            if (!(o instanceof WeakEntry)) {
                                return false;
                            }
                            WeakEntry entry = (WeakEntry)o;
                            // remove the entry but and increment the modcount
                            // because this is a user action
                            return removeEntry(entry, true);
                        }

                        public int size() {
                            return count;
                        }

                        public void clear() {
                        	IdentityWeakHashMap.this.clear();
                        }
                    };
        }
        return entrySet;
    }

    private Iterator getHashIterator(COMPONENT_TYPES type) {
        if (count == 0) {
            return emptyHashIterator;
        } else {
            return new HashIterator(type);
        }
    }

    /**
     * IdentityWeakHashMap entry.
     */
    static class WeakEntry<K,V> implements Map.Entry<K,V> {
        boolean removed = false;
        int hash;
        EntryReference<K> key;
        EntryReference<V> value;
        WeakEntry<K,V> next;

        WeakEntry(int hash, K key, V value, WeakEntry<K,V> next, ReferenceQueue refQueue) {
            this.hash = hash;
            this.key = new WeakEntryReference<K>(key, refQueue, this);
            if (key == value){
                this.value = (EntryReference<V>)this.key;
            }else{
                this.value = new HardEntryReference<V>(value);
            }
            this.next = next;
        }

        protected Object clone(ReferenceQueue refQueue) {
            WeakEntry current = this;
            WeakEntry root = new WeakEntry(current.hash, current.key.get(), current.value.get(), null, refQueue);
            WeakEntry currentClone = root;

            while (current.next != null) {
               currentClone.next = new WeakEntry(current.next.hash, current.next.key.get(), current.next.value.get(), null, refQueue);
               current = current.next;
               currentClone = currentClone.next;
            }

            return root;
        }

        // Map.Entry Ops
        public K getKey() {
            return key.get();
        }

        public V getValue() {
            return value.get();
        }

        public V setValue(V value) {
            EntryReference<V> oldValue = this.value;
            if (value == this.key.get()){
                this.value = (EntryReference<V>)this.key;
            }else{
                this.value = new HardEntryReference<V>(value);
            }
            return oldValue.get();
        }

        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }

            Map.Entry e = (Map.Entry)o;
            Object v = value.get();
            return (key == e.getKey()) && ((v == null) ? (e.getValue() == null) : v.equals(e.getValue()));
        }

        public int hashCode() {
            Object v = value.get();
            return hash ^ ((v == null) ? 0 : v.hashCode());
        }

        public String toString() {
            return key.get() + "=" + value.get();
        }
        
        public boolean shouldBeIgnored(){
        	return key.get() == null || value.get() == null;
        }
    }
    
    static interface EntryReference<T> {
        public T get();
    }
    
    static class WeakEntryReference<T> extends WeakReference<T> implements EntryReference{
        protected WeakEntry owner;
        protected boolean trashed = false;
        protected ReferenceQueue referenceQueue;
        
        public WeakEntryReference(T referent, ReferenceQueue<? super T> q, WeakEntry owner) {
            super(referent, q);
            this.owner = owner;
            this.referenceQueue = q;
        }
    }
    //This limited class is here to allow the value to be switched from a weak reference to a hard
    // referernce.  This Map only makes the key weak but inorder to allow for garbage collection
    //of the key when the key and the value are the same object the same weak reference will be used
    static class HardEntryReference<T> implements EntryReference{
        protected T referent;
        
        public HardEntryReference(T referent){
            this.referent = referent;
        }
        
        public T get(){
            return referent;
        }
    }

    // Types of Iterators
    private enum COMPONENT_TYPES {KEYS, VALUES, ENTRIES};
    
    private static EmptyHashIterator emptyHashIterator = new EmptyHashIterator();

    private static class EmptyHashIterator implements Iterator {
        EmptyHashIterator() {
        }

        public boolean hasNext() {
            return false;
        }

        public Object next() {
            throw new NoSuchElementException();
        }

        public void remove() {
            throw new IllegalStateException();
        }
    }

    private class HashIterator implements Iterator {
        WeakEntry[] entries = IdentityWeakHashMap.this.entries;
        int index = entries.length;
        WeakEntry entry = null;
        WeakEntry lastReturned = null;
        COMPONENT_TYPES type;
        Object currentEntryRef;

        /**
         * The modCount value that the iterator believes that the backing
         * List should have.  If this expectation is violated, the iterator
         * has detected concurrent modification.
         */
        private int expectedModCount = modCount;

        HashIterator(COMPONENT_TYPES type) {
            this.type = type;
        }

        public boolean hasNext() {
            WeakEntry e = entry;
            int i = index;
            WeakEntry[] copyOfEntries = IdentityWeakHashMap.this.entries;
            while ((e == null || currentEntryRef == null) && (i > 0)) {
                e = copyOfEntries[--i];
                if (e != null) {
                    currentEntryRef = e.key.get();
                }else{
                    currentEntryRef = null;
                }
            }
            entry = e;
            index = i;
            return e != null && currentEntryRef != null;
        }

        public Object next() {
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }

            WeakEntry et = entry;
            int i = index;
            WeakEntry[] copyOfEntries = IdentityWeakHashMap.this.entries;
            while ((et == null || currentEntryRef == null) && (i > 0)) {
                et = copyOfEntries[--i];
                if (et != null) {
                    currentEntryRef = et.key.get();
                }else{
                    currentEntryRef = null;
                }
             }
            entry = et;
            index = i;
            if (et != null) {
                WeakEntry e = lastReturned = entry;
                entry = e.next;
                if (entry != null) {
                    currentEntryRef = entry.key.get();
                }else{
                    currentEntryRef = null;
                }
                return (type == COMPONENT_TYPES.KEYS) ? e.key.get() : ((type == COMPONENT_TYPES.VALUES) ? e.value.get() : e);
            }
            throw new NoSuchElementException();
        }

        public void remove() {
            if (lastReturned == null) {
                throw new IllegalStateException();
            }
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }

            WeakEntry[] copyOfEntries = IdentityWeakHashMap.this.entries;
            int index = (lastReturned.hash & 0x7FFFFFFF) % copyOfEntries.length;
            for (WeakEntry e = copyOfEntries[index], prev = null; e != null; prev = e, e = e.next) {
                if (e == lastReturned) {
                    modCount++;
                    expectedModCount++;
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
     * Serialize the state of this <tt>IdentityWeakHashMap</tt> to a stream.
     *
     * @serialData The <i>capacity</i> of the <tt>IdentityWeakHashMap</tt>
     * (the length of the bucket array) is emitted (int), followed by the
     * <i>size</i> of the <tt>IdentityWeakHashMap</tt>, followed by the
     * key-value mappings (in no particular order).
     */
    private void writeObject(ObjectOutputStream s) throws IOException {
        // Write out the threshold, loadfactor (and any hidden 'magic' stuff).
        s.defaultWriteObject();

        // Write out number of buckets
        s.writeInt(entries.length);
        // Write out count
        s.writeInt(count);
        // Write out contents
        for (int i = entries.length - 1; i >= 0; i--) {
            WeakEntry entry = entries[i];
            while (entry != null) {
                s.writeObject(entry.key.get());
                s.writeObject(entry.value.get());
                entry = entry.next;
            }
        }
    }

    /**
     * Deserialize the <tt>IdentityWeakHashMap</tt> from a stream.
     */
    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        // Read in the threshold, loadfactor (and any hidden 'magic' stuff).
        s.defaultReadObject();

        // Read in number of buckets and allocate the bucket array;
        int numBuckets = s.readInt();
        entries = new WeakEntry[numBuckets];
        // Read in size (count)
        int size = s.readInt();

        // Read the mappings and add to the IdentityWeakHashMap
        for (int i = 0; i < size; i++) {
            Object key = s.readObject();
            Object value = s.readObject();
            //only re-add if not null as could have been garbage collected at any time
            //before the writeObject
            if (key != null && value != null){
                put((K)key, (V)value);
            }
        }
    }
}
