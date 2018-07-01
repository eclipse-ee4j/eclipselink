/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.indirection;

import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.eclipse.persistence.descriptors.changetracking.CollectionChangeEvent;
import org.eclipse.persistence.descriptors.changetracking.CollectionChangeTracker;
import org.eclipse.persistence.descriptors.changetracking.MapChangeEvent;

/**
 * IndirectMap allows a domain class to take advantage of TopLink indirection
 * without having to declare its instance variable as a ValueHolderInterface.
 * <p>To use an IndirectMap:<ul>
 * <li> Declare the appropriate instance variable with type Map or Hashtable
 * <li> Send the message #useTransparentMap(String) to the appropriate
 * CollectionMapping.
 * </ul>
 * EclipseLink will place an
 * IndirectMap in the instance variable when the containing domain object is read from
 * the database. With the first message sent to the IndirectMap, the contents
 * are fetched from the database and normal Hashtable/Map behavior is resumed.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 * @see org.eclipse.persistence.mappings.CollectionMapping
 * @see org.eclipse.persistence.indirection.IndirectList
 * @author Big Country
 * @since TOPLink/Java 2.5
 */
public class IndirectMap<K, V> extends Hashtable<K, V> implements CollectionChangeTracker, IndirectCollection {

    /** Reduce type casting */
    protected volatile Hashtable<K, V> delegate;

    /** Delegate indirection behavior to a value holder */
    protected volatile ValueHolderInterface valueHolder;

    /** Change tracking listener. */
    private transient PropertyChangeListener changeListener;

    /** The mapping attribute name, used to raise change events. */
    private transient String attributeName;

    /** Store initial size for lazy init. */
    protected int initialCapacity = 11;

    /** Store load factor for lazy init. */
    protected float loadFactor = 0.75f;

    /**
     * PUBLIC:
     * Construct a new, empty IndirectMap with a default
     * capacity and load factor.
     */
    public IndirectMap() {
        this(11);
    }

    /**
     * PUBLIC:
     * Construct a new, empty IndirectMap with the specified initial capacity
     * and default load factor.
     *
     * @param   initialCapacity   the initial capacity of the hashtable
     */
    public IndirectMap(int initialCapacity) {
        this(initialCapacity, 0.75f);
    }

    /**
     * PUBLIC:
     * Construct a new, empty IndirectMap with the specified initial
     * capacity and load factor.
     *
     * @param      initialCapacity   the initial capacity of the hashtable
     * @param      loadFactor        a number between 0.0 and 1.0
     * @exception  IllegalArgumentException  if the initial capacity is less
     *               than or equal to zero, or if the load factor is less than
     *               or equal to zero
     */
    public IndirectMap(int initialCapacity, float loadFactor) {
        super(0);
        this.initialize(initialCapacity, loadFactor);
    }

    /**
     * PUBLIC:
     * Construct a new IndirectMap with the same mappings as the given Map.
     * The IndirectMap is created with a capacity of twice the number of entries
     * in the given Map or 11 (whichever is greater), and a default load factor, which is 0.75.
     * @param m a map containing the mappings to use
     */
    public IndirectMap(Map<? extends K, ? extends V> m) {
        super(0);
        this.initialize(m);
    }

    /**
     * Return the freshly-built delegate.
     */
    protected Hashtable<K, V> buildDelegate() {
        Hashtable<K, V> value = (Hashtable<K, V>)getValueHolder().getValue();
        if (value == null) {
            value = new Hashtable<>(this.initialCapacity, this.loadFactor);
        }
        return value;
    }

    /**
     * @see java.util.Hashtable#clear()
     */
    @Override
    public synchronized void clear() {
        if (hasTrackedPropertyChangeListener()) {
            Iterator<K> objects = this.keySet().iterator();
            while (objects.hasNext()) {
                K o = objects.next();
                objects.remove();
            }
        } else {
            this.getDelegate().clear();
        }
    }

    /**
     * INTERNAL:
     * clear any changes that have been deferred to instantiation.
     * Indirect collections with change tracking avoid instantiation on add/remove.
     */
    @Override
    public void clearDeferredChanges(){
    }

    /**
     * @see java.util.Hashtable#clone()
     * This will result in a database query if necessary.
     */

    /*
        There are 3 situations when clone() is called:
        1.    The developer actually wants to clone the collection (typically to modify one
            of the 2 resulting collections). In which case the contents must be read from
            the database.
        2.    A UnitOfWork needs a clone (or backup clone) of the collection. But the
            UnitOfWork checks "instantiation" before cloning collections ("un-instantiated"
            collections are not cloned).
        3.    A MergeManager needs an extra copy of the collection (because the "backup"
            and "target" are the same object?). But the MergeManager checks "instantiation"
            before merging collections (again, "un-instantiated" collections are not merged).
    */
    @Override
    public synchronized Object clone() {
        IndirectMap<K, V> result = (IndirectMap<K, V>)super.clone();
        result.delegate = (Hashtable<K, V>)this.getDelegate().clone();
        result.valueHolder = new ValueHolder(result.delegate);
        result.attributeName = null;
        result.changeListener = null;
        return result;
    }

    /**
     * @see java.util.Hashtable#contains(java.lang.Object)
     */
    @Override
    public synchronized boolean contains(Object value) {
        return this.getDelegate().contains(value);
    }

    /**
     * @see java.util.Hashtable#containsKey(java.lang.Object)
     */
    @Override
    public synchronized boolean containsKey(Object key) {
        return this.getDelegate().containsKey(key);
    }

    /**
     * @see java.util.Hashtable#containsValue(java.lang.Object)
     */
    @Override
    public boolean containsValue(Object value) {
        return this.getDelegate().containsValue(value);
    }

    /**
     * @see java.util.Hashtable#elements()
     */
    @Override
    public synchronized Enumeration<V> elements() {
        return this.getDelegate().elements();
    }

    /**
     * @see java.util.Hashtable#entrySet()
     */
    @Override
    public Set<Map.Entry<K,V>> entrySet() {
        return new Set<Map.Entry<K,V>> (){
            Set<Map.Entry<K,V>> delegateSet = IndirectMap.this.getDelegate().entrySet();

            @Override
            public int size(){
                return this.delegateSet.size();
            }

            @Override
            public boolean isEmpty(){
                return this.delegateSet.isEmpty();
            }

            @Override
            public boolean contains(Object o){
                return this.delegateSet.contains(o);
            }

            @Override
            public Iterator<Map.Entry<K,V>> iterator(){
                return new Iterator<Map.Entry<K,V>>() {
                    Iterator<Map.Entry<K, V>> delegateIterator = delegateSet.iterator();
                    Map.Entry<K, V> currentObject;

                    @Override
                    public boolean hasNext() {
                        return this.delegateIterator.hasNext();
                    }

                    @Override
                    public Map.Entry<K, V> next() {
                        this.currentObject = this.delegateIterator.next();
                        return this.currentObject;
                    }

                    @Override
                    public void remove() {
                        this.delegateIterator.remove();
                        if (currentObject != null) {
                            raiseRemoveChangeEvent(currentObject.getKey(), currentObject.getValue());
                        }
                    }

                    @Override
                    public void forEachRemaining(Consumer<? super java.util.Map.Entry<K, V>> action) {
                        this.delegateIterator.forEachRemaining(action);
                    }
                };
            }

            @Override
            public Object[] toArray(){
                return this.delegateSet.toArray();
            }

            @Override
            public <T> T[] toArray(T[] a){
                return this.delegateSet.toArray(a);
            }

            @Override
            public boolean add(Map.Entry<K, V> o){
                return this.delegateSet.add(o);
            }

            @Override
            public boolean remove(Object o){
                if (!(o instanceof Map.Entry)) {
                    return false;
                }
                return (IndirectMap.this.remove(((Map.Entry)o).getKey()) != null);
            }

            @Override
            public boolean containsAll(Collection<?> c){
                return this.delegateSet.containsAll(c);
            }

            @Override
            public boolean addAll(Collection<? extends Map.Entry<K, V>> c){
                return this.delegateSet.addAll(c);
            }

            @Override
            public boolean retainAll(Collection<?> c){
                boolean result = false;
                Iterator<Map.Entry<K, V>> objects = delegateSet.iterator();
                while (objects.hasNext()) {
                    Map.Entry object = objects.next();
                    if (!c.contains(object)) {
                        objects.remove();
                        raiseRemoveChangeEvent(object.getKey(), object.getValue());
                        result = true;
                    }
                }
                return result;
            }

            @Override
            public boolean removeAll(Collection<?> c){
                boolean result = false;
                for (Object object : c) {
                    if ( ! (object instanceof Map.Entry)){
                        continue;
                    }
                    Object removed = IndirectMap.this.remove(((Map.Entry)object).getKey());
                    if (removed != null){
                        result = true;
                    }
                }
                return result;
            }

            @Override
            public void clear(){
                IndirectMap.this.clear();
            }

            @Override
            public boolean equals(Object o){
                return this.delegateSet.equals(o);
            }

            @Override
            public int hashCode(){
                return this.delegateSet.hashCode();
            }

            @Override
            public boolean removeIf(Predicate<? super Map.Entry<K, V>> filter) {
                boolean hasChanged = false;
                Iterator<Map.Entry<K, V>> objects = iterator();
                while (objects.hasNext()) {
                    if (filter.test(objects.next())) {
                        objects.remove();
                        hasChanged |= true;
                    }
                }
                return hasChanged;
            }

            @Override
            public Stream<Map.Entry<K, V>> stream() {
                return this.delegateSet.stream();
            }

            @Override
            public Stream<java.util.Map.Entry<K, V>> parallelStream() {
                return this.delegateSet.parallelStream();
            }

            @Override
            public void forEach(Consumer<? super java.util.Map.Entry<K, V>> action) {
                this.delegateSet.forEach(action);
            }

            @Override
            public Spliterator<java.util.Map.Entry<K, V>> spliterator() {
                return this.delegateSet.spliterator();
            }
        };
    }

    /**
     * @see java.util.Hashtable#equals(java.lang.Object)
     */
    @Override
    public synchronized boolean equals(Object o) {
        return this.getDelegate().equals(o);
    }

    /**
     * @see java.util.Hashtable#get(java.lang.Object)
     */
    @Override
    public synchronized V get(Object key) {
        return this.getDelegate().get(key);
    }

    /**
     * INTERNAL:
     * Check whether the contents have been read from the database.
     * If they have not, read them and set the delegate.
     * This method used to be synchronized, which caused deadlock.
     */
    protected Hashtable<K, V> getDelegate() {
        Hashtable<K, V> newDelegate = this.delegate;
        if (newDelegate == null) {
            synchronized(this){
                newDelegate = this.delegate;
                if (newDelegate == null) {
                    this.delegate = newDelegate = this.buildDelegate();
                }
            }
        }
        return newDelegate;
    }

    /**
     * INTERNAL:
     * Return the real collection object.
     * This will force instantiation.
     */
    @Override
    public Object getDelegateObject() {
        return getDelegate();
    }

    /**
     * INTERNAL:
     * Return the mapping attribute name, used to raise change events.
     */
    @Override
     public String getTrackedAttributeName() {
         return attributeName;
     }

    /**
     * Return the property change listener for change tracking.
     */
    @Override
     public PropertyChangeListener _persistence_getPropertyChangeListener() {
         return changeListener;
     }

     /**
      * PUBLIC:
      * Return the valueHolder.
      * This method used to be synchronized, which caused deadlock.
      */
    @Override
     public ValueHolderInterface getValueHolder() {
         ValueHolderInterface vh = this.valueHolder;
         // PERF: lazy initialize value holder and vector as are normally set after creation.
         if (vh == null) {
             synchronized(this){
                vh = this.valueHolder;
                 if (vh == null) {
                     this.valueHolder = vh = new ValueHolder(new Hashtable<>(initialCapacity, loadFactor));
                 }
             }
         }
         return vh;
     }

    /**
     * @see java.util.Hashtable#hashCode()
     */
    @Override
    public synchronized int hashCode() {
        return this.getDelegate().hashCode();
    }

    /**
     * INTERNAL:
     * Return if the collection has a property change listener for change tracking.
     */
     public boolean hasTrackedPropertyChangeListener() {
         return this.changeListener != null;
     }

    /**
     * Initialize the instance.
     */
    protected void initialize(int initialCapacity, float loadFactor) {
        this.delegate = null;
        this.loadFactor = loadFactor;
        this.initialCapacity = initialCapacity;
        this.valueHolder = null;
    }

    /**
     * Initialize the instance.
     */
    protected void initialize(Map<? extends K, ? extends V> m) {
        this.delegate = null;
        Hashtable<K, V> temp = new Hashtable<>(m);

        this.valueHolder = new ValueHolder(temp);
    }

    /**
     * @see java.util.Hashtable#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        return this.getDelegate().isEmpty();
    }

    /**
     * PUBLIC:
     * Return whether the contents have been read from the database.
     */
    @Override
    public boolean isInstantiated() {
        return this.getValueHolder().isInstantiated();
    }

    /**
     * @see java.util.Hashtable#keys()
     */
    @Override
    public synchronized Enumeration<K> keys() {
        return this.getDelegate().keys();
    }

    /**
     * @see java.util.Hashtable#keySet()
     */
    @Override
    public Set<K> keySet() {

        return new Set<K> (){
            Set<K> delegateSet = IndirectMap.this.getDelegate().keySet();

            @Override
            public int size(){
                return this.delegateSet.size();
            }

            @Override
            public boolean isEmpty(){
                return this.delegateSet.isEmpty();
            }

            @Override
            public boolean contains(Object o){
                return this.delegateSet.contains(o);
            }

            @Override
            public Iterator<K> iterator(){
                return new Iterator<K>() {
                    Iterator<K> delegateIterator = delegateSet.iterator();
                    K currentObject;

                    @Override
                    public boolean hasNext() {
                        return this.delegateIterator.hasNext();
                    }

                    @Override
                    public K next() {
                        this.currentObject = this.delegateIterator.next();
                        return this.currentObject;
                    }

                    @Override
                    public void remove() {
                        IndirectMap.this.raiseRemoveChangeEvent(currentObject, IndirectMap.this.getDelegate().get(currentObject));
                        this.delegateIterator.remove();
                    }

                    @Override
                    public void forEachRemaining(Consumer<? super K> action) {
                        this.delegateIterator.forEachRemaining(action);
                    }
                };
            }

            @Override
            public Object[] toArray(){
                return this.delegateSet.toArray();
            }

            @Override
            public Object[] toArray(Object a[]){
                return this.delegateSet.toArray(a);
            }

            @Override
            public boolean add(K o){
                return this.delegateSet.add(o);
            }

            @Override
            public boolean remove(Object o){
                return (IndirectMap.this.remove(o) != null);
            }

            @Override
            public boolean containsAll(Collection<?> c){
                return this.delegateSet.containsAll(c);
            }

            @Override
            public boolean addAll(Collection<? extends K> c){
                return this.delegateSet.addAll(c);
            }

            @Override
            public boolean retainAll(Collection<?> c){
                boolean result = false;
                Iterator objects = delegateSet.iterator();
                while (objects.hasNext()) {
                    Object object = objects.next();
                    if (!c.contains(object)) {
                        objects.remove();
                        IndirectMap.this.raiseRemoveChangeEvent(object, IndirectMap.this.getDelegate().get(object));
                        result = true;
                    }
                }
                return result;
            }

            @Override
            public boolean removeAll(Collection<?> c){
                boolean result = false;
                for (Iterator<?> cs = c.iterator(); cs.hasNext(); ){
                    if (IndirectMap.this.remove(cs.next()) != null ) {
                        result = true;
                    }
                }
                return result;
            }

            @Override
            public void clear(){
                IndirectMap.this.clear();
            }

            @Override
            public boolean equals(Object o){
                return this.delegateSet.equals(o);
            }

            @Override
            public int hashCode(){
                return this.delegateSet.hashCode();
            }

            @Override
            public boolean removeIf(Predicate<? super K> filter) {
                boolean hasChanged = false;
                Iterator<K> objects = iterator();
                while (objects.hasNext()) {
                    if (filter.test(objects.next())) {
                        objects.remove();
                        hasChanged |= true;
                    }
                }
                return hasChanged;
            }

            @Override
            public Stream<K> stream() {
                return this.delegateSet.stream();
            }

            @Override
            public Stream<K> parallelStream() {
                return this.delegateSet.parallelStream();
            }

            @Override
            public void forEach(Consumer<? super K> action) {
                this.delegateSet.forEach(action);
            }

            @Override
            public Spliterator<K> spliterator() {
                return this.delegateSet.spliterator();
            }
        };

    }

    /**
     * @see java.util.Hashtable#put(java.lang.Object, java.lang.Object)
     */
    @Override
    public synchronized V put(K key, V value) {
        V oldValue = this.getDelegate().put(key, value);
        if (oldValue != null){
            raiseRemoveChangeEvent(key, oldValue);
        }
        raiseAddChangeEvent(key, value);
        return oldValue;
    }


    /**
     * @see java.util.Hashtable#putAll(java.util.Map)
     */
    @Override
    public synchronized void putAll(Map<? extends K,? extends V> t) {
        // Must trigger add events if tracked or uow.
        if (hasTrackedPropertyChangeListener()) {
            t.entrySet().stream().forEach((newEntry) -> {
                this.put(newEntry.getKey(), newEntry.getValue());
            });
        }else{
            this.getDelegate().putAll(t);
        }
    }

    @Override
    public synchronized V compute(K key, BiFunction<? super K,? super V,? extends V> remappingFunction) {
        // Must trigger add events if tracked or uow.
        if (hasTrackedPropertyChangeListener()) {
            V oldValue = get(key);
            V newValue = remappingFunction.apply(key, oldValue);
            if (oldValue != null ) {
               if (newValue != null) {
                  put(key, newValue);
                  return newValue;
               }
               remove(key);
            } else {
               if (newValue != null) {
                  put(key, newValue);
                  return newValue;
               }
            }
            return null;
        }
        return getDelegate().compute(key, remappingFunction);
    }

    @Override
    public synchronized V computeIfAbsent(K key, Function<? super K,? extends V> mappingFunction) {
        // Must trigger add events if tracked or uow.
        if (hasTrackedPropertyChangeListener()) {
            V oldValue = get(key);
            if (oldValue == null) {
                V newValue = mappingFunction.apply(key);
                if (newValue != null) {
                    put(key, newValue);
                }
                return newValue;
            }
            return oldValue;
        }
        return getDelegate().computeIfAbsent(key, mappingFunction);
    }

    @Override
    public synchronized V computeIfPresent(K key, BiFunction<? super K,? super V,? extends V> remappingFunction) {
        // Must trigger add events if tracked or uow.
        if (hasTrackedPropertyChangeListener()) {
            if (get(key) != null) {
                V oldValue = get(key);
                V newValue = remappingFunction.apply(key, oldValue);
                if (newValue != null) {
                    put(key, newValue);
                    return newValue;
                }
                remove(key);
            }
            return null;
        }
        return getDelegate().computeIfPresent(key, remappingFunction);
    }

    @Override
    public synchronized void forEach(BiConsumer<? super K,? super V> action) {
        getDelegate().forEach(action);
    }

    @Override
    public synchronized V getOrDefault(Object key, V defaultValue) {
        return getDelegate().getOrDefault(key, defaultValue);
    }

    @Override
    public synchronized V merge(K key, V value, BiFunction<? super V,? super V,? extends V> remappingFunction) {
        // Must trigger add events if tracked or uow.
        if (hasTrackedPropertyChangeListener()) {
            V oldValue = get(key);
            V newValue = (oldValue == null) ? value : remappingFunction.apply(oldValue, value);
            if (newValue == null) {
                remove(key);
            } else {
                put(key, newValue);
            }
            return newValue;
        }
        return getDelegate().merge(key, value, remappingFunction);
    }

    @Override
    public synchronized V putIfAbsent(K key, V value) {
        // Must trigger add events if tracked or uow.
        if (hasTrackedPropertyChangeListener()) {
            V current = getDelegate().get(key);
            if (current == null) {
                V v = getDelegate().put(key, value);
                raiseAddChangeEvent(key, value);
                return v;
            }
            return current;
        }
        return getDelegate().putIfAbsent(key, value);
    }

    @Override
    public synchronized boolean remove(Object key, Object value) {
        // Must trigger add events if tracked or uow.
        if (hasTrackedPropertyChangeListener()) {
            Map<K, V> del = getDelegate();
            if (del.containsKey(key) && Objects.equals(del.get(key), value)) {
                del.remove(key);
                raiseRemoveChangeEvent(key, value);
                return true;
            }
            return false;
        }
        return getDelegate().remove(key, value);
    }

    @Override
    public synchronized V replace(K key, V value) {
        // Must trigger add events if tracked or uow.
        if (hasTrackedPropertyChangeListener()) {
            Map<K, V> del = getDelegate();
            if (del.containsKey(key)) {
                return put(key, value);
            }
            return null;
        }
        return getDelegate().replace(key, value);
    }

    @Override
    public synchronized boolean replace(K key, V oldValue, V newValue) {
        // Must trigger add events if tracked or uow.
        if (hasTrackedPropertyChangeListener()) {
            Map<K, V> del = getDelegate();
            if (del.containsKey(key) && Objects.equals(del.get(key), oldValue)) {
                put(key, newValue);
                return true;
            }
            return false;
        }
        return getDelegate().replace(key, oldValue, newValue);
    }

    @Override
    public synchronized void replaceAll(BiFunction<? super K,? super V,? extends V> function) {
        // Must trigger add events if tracked or uow.
        if (hasTrackedPropertyChangeListener()) {
            getDelegate().entrySet().stream().forEach((entry) -> {
                K key = entry.getKey();
                V oldValue = entry.getValue();
                entry.setValue(function.apply(key, entry.getValue()));
                raiseRemoveChangeEvent(key, oldValue);
                raiseAddChangeEvent(key, entry.getValue());
            });
            return;
        }
        getDelegate().replaceAll(function);
    }

    /**
     * @see java.util.Hashtable#rehash()
     */
    @Override
    protected void rehash() {
        throw new InternalError("unsupported");
    }

    /**
     * Raise the add change event and relationship maintainence.
     */
    protected void raiseAddChangeEvent(Object key, Object value) {
        if (hasTrackedPropertyChangeListener()) {
            _persistence_getPropertyChangeListener().propertyChange(new MapChangeEvent(this, getTrackedAttributeName(), this, key, value, CollectionChangeEvent.ADD, true));
        }
        // this is where relationship maintenance would go
    }

    /**
     * Raise the remove change event.
     */
    protected void raiseRemoveChangeEvent(Object key, Object value) {
        if (hasTrackedPropertyChangeListener()) {
            _persistence_getPropertyChangeListener().propertyChange(new MapChangeEvent(this, getTrackedAttributeName(), this, key, value, CollectionChangeEvent.REMOVE, true));
        }
        // this is where relationship maintenance would go
    }

    /**
     * @see java.util.Hashtable#remove(java.lang.Object)
     */
    @Override
    public synchronized V remove(Object key) {
        V value = this.getDelegate().remove(key);
        if (value != null){
            raiseRemoveChangeEvent(key, value);
        }
        return value;
    }

    /**
     * INTERNAL:
     * Set the mapping attribute name, used to raise change events.
     * This is required if the change listener is set.
     */
    @Override
     public void setTrackedAttributeName(String attributeName) {
         this.attributeName = attributeName;
     }

    /**
     * INTERNAL:
     * Set the property change listener for change tracking.
     */
    @Override
     public void _persistence_setPropertyChangeListener(PropertyChangeListener changeListener) {
         this.changeListener = changeListener;
     }

    /**
     * INTERNAL:
     * Set the value holder.
     */
    @Override
    public void setValueHolder(ValueHolderInterface valueHolder) {
        this.delegate = null;
        this.valueHolder = valueHolder;
    }

    /**
     * @see java.util.Hashtable#size()
     */
    @Override
    public int size() {
        return this.getDelegate().size();
    }

    /**
     * INTERNAL
     * Set whether this collection should attempt do deal with adds and removes without retrieving the
     * collection from the dB
     */
    @Override
    public void setUseLazyInstantiation(boolean useLazyInstantiation){
    }

    /**
     * INTERNAL:
     * Return the elements that have been removed before instantiation.
     */
    @Override
    public Collection<Map.Entry<K, V>> getRemovedElements() {
        return null;
    }

    /**
     * INTERNAL:
     * Return the elements that have been added before instantiation.
     */
    @Override
    public Collection<Map.Entry<K, V>> getAddedElements() {
        return null;
    }

    /**
     * INTERNAL:
     * Return if any elements that have been added or removed before instantiation.
     */
    @Override
    public boolean hasDeferredChanges() {
        return false;
    }

    /**
     * PUBLIC:
     * Use the Hashtable.toString(); but wrap it with braces to indicate
     * there is a bit of indirection.
     * Don't allow this method to trigger a database read.
     * @see java.util.Hashtable#toString()
     */
    @Override
    public String toString() {
        if (ValueHolderInterface.shouldToStringInstantiate) {
            return this.getDelegate().toString();
        }
        if (this.isInstantiated()) {
            return "{" + this.getDelegate().toString() + "}";
        } else {
            return "{" + org.eclipse.persistence.internal.helper.Helper.getShortClassName(this.getClass()) + ": not instantiated}";
        }
    }

    /**
     * @see java.util.Hashtable#values()
     */
    @Override
    public Collection<V> values() {
        return new Collection<V>() {
            protected Collection<V> delegateCollection = IndirectMap.this.getDelegate().values();

            @Override
            public int size(){
                return delegateCollection.size();
            }

            @Override
            public boolean isEmpty(){
                return delegateCollection.isEmpty();
            }

            @Override
            public boolean contains(Object o){
                return delegateCollection.contains(o);
            }

            @Override
            public Iterator<V> iterator() {
                return new Iterator<V>() {
                    Iterator<V> delegateIterator = delegateCollection.iterator();
                    V currentObject;

                    @Override
                    public boolean hasNext() {
                        return this.delegateIterator.hasNext();
                    }

                    @Override
                    public V next() {
                        this.currentObject = this.delegateIterator.next();
                        return this.currentObject;
                    }

                    @Override
                    public void remove() {
                        for (Map.Entry entry : IndirectMap.this.getDelegate().entrySet()) {
                            if (entry.getValue().equals(currentObject)){
                                IndirectMap.this.raiseRemoveChangeEvent(entry.getKey(), entry.getValue());
                            }
                        }
                        this.delegateIterator.remove();
                    }

                    @Override
                    public void forEachRemaining(Consumer<? super V> action) {
                        this.delegateIterator.forEachRemaining(action);
                    }
                };
            }

            @Override
            public Object[] toArray(){
                return this.delegateCollection.toArray();
            }

            @Override
            public <T> T[] toArray(T[] a){
                return this.delegateCollection.toArray(a);
            }

            @Override
            public boolean add(V o){
                return this.delegateCollection.add(o);
            }

            @Override
            public boolean remove(Object o){
                for (Iterator<Map.Entry<K, V>> entryIt = IndirectMap.this.getDelegate().entrySet().iterator(); entryIt.hasNext();) {
                    Map.Entry<K, V> entry = entryIt.next();
                    if (entry.getValue().equals(o)){
                        IndirectMap.this.raiseRemoveChangeEvent(entry.getKey(), entry.getValue());
                        entryIt.remove();
                        return true;
                    }
                }
                return false;
            }

            @Override
            public boolean containsAll(Collection<?> c){
                return this.delegateCollection.containsAll(c);
            }

            @Override
            public boolean addAll(Collection<? extends V> c){
                return this.delegateCollection.addAll(c);
            }

            @Override
            public boolean removeAll(Collection<?> c){
                boolean result = false;
                for (Iterator<?> iterator = c.iterator(); iterator.hasNext();){
                    if (remove(iterator.next()) ){
                        result = true;
                    }
                }
                return result;
            }

            @Override
            public boolean retainAll(Collection<?> c){
                boolean result = false;
                for (Iterator<Map.Entry<K, V>> iterator = IndirectMap.this.entrySet().iterator(); iterator.hasNext();){
                    Map.Entry<K, V> entry = iterator.next();
                    if (! c.contains(entry.getValue()) ) {
                        iterator.remove();
                        result = true;
                    }
                }
                return result;
            }

            @Override
            public void clear(){
                IndirectMap.this.clear();
            }

            @Override
            public boolean equals(Object o){
                return this.delegateCollection.equals(o);
            }

            @Override
            public int hashCode(){
                return this.delegateCollection.hashCode();
            }

            @Override
            public void forEach(Consumer<? super V> action) {
                this.delegateCollection.forEach(action);
            }

            @Override
            public boolean removeIf(Predicate<? super V> filter) {
                boolean hasChanged = false;
                Iterator<V> objects = iterator();
                while (objects.hasNext()) {
                    if (filter.test(objects.next())) {
                        objects.remove();
                        hasChanged |= true;
                    }
                }
                return hasChanged;
            }

            @Override
            public Spliterator<V> spliterator() {
                return this.delegateCollection.spliterator();
            }

            @Override
            public Stream<V> stream() {
                return this.delegateCollection.stream();
            }

            @Override
            public Stream<V> parallelStream() {
                return this.delegateCollection.parallelStream();
            }
        };
    }
}
