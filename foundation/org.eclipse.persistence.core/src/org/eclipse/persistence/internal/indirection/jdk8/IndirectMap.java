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

/**
 * Java SE 8 additions to {@link org.eclipse.persistence.indirection.IndirectMap}.
 *
 * @author Lukas Jungmann
 */
public class IndirectMap<K, V> extends org.eclipse.persistence.indirection.IndirectMap<K, V> {

    public IndirectMap() {
        super();
    }

    public IndirectMap(int initialCapacity) {
        super(initialCapacity);
    }

    public IndirectMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public IndirectMap(Map<? extends K, ? extends V> m) {
        super(m);
    }

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
                        //should remove the element here
                        //entryIt.remove();
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
            for (Map.Entry<K, V> entry : getDelegate().entrySet()) {
                K key = entry.getKey();
                V oldValue = entry.getValue();
                entry.setValue(function.apply(key, entry.getValue()));
                raiseRemoveChangeEvent(key, oldValue);
                raiseAddChangeEvent(key, entry.getValue());
            }
            return;
        }
        getDelegate().replaceAll(function);
    }
}
