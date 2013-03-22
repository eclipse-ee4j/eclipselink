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
package org.eclipse.persistence.indirection;

import java.beans.PropertyChangeListener;
import java.util.*;
import org.eclipse.persistence.descriptors.changetracking.*;

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
 * the datatabase. With the first message sent to the IndirectMap, the contents
 * are fetched from the database and normal Hashtable/Map behavior is resumed.
 *
 * @see org.eclipse.persistence.mappings.CollectionMapping
 * @see org.eclipse.persistence.indirection.IndirectList
 * @author Big Country
 * @since TOPLink/Java 2.5
 */
public class IndirectMap extends Hashtable implements CollectionChangeTracker, IndirectCollection {

    /** Reduce type casting */
    protected volatile Hashtable delegate;

    /** Delegate indirection behavior to a value holder */
    protected ValueHolderInterface valueHolder;

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
    public IndirectMap(Map m) {
        super(0);
        this.initialize(m);
    }

    /**
     * Return the freshly-built delegate.
     */
    protected Hashtable buildDelegate() {
        Hashtable value = (Hashtable)getValueHolder().getValue();
        if (value == null) {
            value = new Hashtable(this.initialCapacity, this.loadFactor);
        }
        return value;
    }

    /**
     * @see java.util.Hashtable#clear()
     */
    public synchronized void clear() {
        if (hasTrackedPropertyChangeListener()) {
            Iterator objects = this.keySet().iterator();
            while (objects.hasNext()) {
                Object o = objects.next();
                objects.remove();
                this.raiseRemoveChangeEvent(o, this.get(o));
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
    public synchronized Object clone() {
        IndirectMap result = (IndirectMap)super.clone();
        result.delegate = (Hashtable)this.getDelegate().clone();
        result.valueHolder = new ValueHolder(result.delegate);
        result.attributeName = null;
        result.changeListener = null;
        return result;
    }

    /**
     * @see java.util.Hashtable#contains(java.lang.Object)
     */
    public synchronized boolean contains(Object value) {
        return this.getDelegate().contains(value);
    }

    /**
     * @see java.util.Hashtable#containsKey(java.lang.Object)
     */
    public synchronized boolean containsKey(Object key) {
        return this.getDelegate().containsKey(key);
    }

    /**
     * @see java.util.Hashtable#containsValue(java.lang.Object)
     */
    public boolean containsValue(Object value) {
        return this.getDelegate().containsValue(value);
    }

    /**
     * @see java.util.Hashtable#elements()
     */
    public synchronized Enumeration elements() {
        return this.getDelegate().elements();
    }

    /**
     * @see java.util.Hashtable#entrySet()
     */
    public Set entrySet() {
        return new Set (){
            Set delegateSet = IndirectMap.this.getDelegate().entrySet();
            
            public int size(){
                return this.delegateSet.size();
            }
        
            public boolean isEmpty(){
                return this.delegateSet.isEmpty();
            }
        
            public boolean contains(Object o){
                return this.delegateSet.contains(o);
            }
        
            public Iterator iterator(){
                return new Iterator() {
                    Iterator delegateIterator = delegateSet.iterator();
                    Object currentObject;
                    
                    public boolean hasNext() {
                        return this.delegateIterator.hasNext();
                    }
                    
                    public Object next() {
                        this.currentObject = this.delegateIterator.next();
                        return this.currentObject;
                    }
                    
                    public void remove() {
                        raiseRemoveChangeEvent(((Map.Entry)currentObject).getKey(), ((Map.Entry)currentObject).getValue());
                        this.delegateIterator.remove();
                    }
                };
            }
        
            public Object[] toArray(){
                return this.delegateSet.toArray();
            }
    
            public Object[] toArray(Object a[]){
                return this.delegateSet.toArray(a);
            }
    
            public boolean add(Object o){
                return this.delegateSet.add(o);
            }
        
            public boolean remove(Object o){
                if (!(o instanceof Map.Entry)) {
                    return false;
                }
                return (IndirectMap.this.remove(((Map.Entry)o).getKey()) != null);
            }
        
            public boolean containsAll(Collection c){
                return this.delegateSet.containsAll(c);
            }
        
            public boolean addAll(Collection c){
                return this.delegateSet.addAll(c);
            }
        
            public boolean retainAll(Collection c){
                boolean result = false;
                Iterator objects = delegateSet.iterator();
                while (objects.hasNext()) {
                    Map.Entry object = (Map.Entry)objects.next();
                    if (!c.contains(object)) {
                        objects.remove();
                        raiseRemoveChangeEvent(object.getKey(), object.getValue());
                        result = true;
                    }
                }
                return result;
            }
            
            public boolean removeAll(Collection c){
                boolean result = false;
                for (Iterator cs = c.iterator(); cs.hasNext(); ){
                    Object object = cs.next();
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
        
            public void clear(){
                IndirectMap.this.clear();
            }
        
            public boolean equals(Object o){
                return this.delegateSet.equals(o);
            }
            
            public int hashCode(){
                return this.delegateSet.hashCode();
            }
        };
    }

    /**
     * @see java.util.Hashtable#equals(java.lang.Object)
     */
    public synchronized boolean equals(Object o) {
        return this.getDelegate().equals(o);
    }

    /**
     * @see java.util.Hashtable#get(java.lang.Object)
     */
    public synchronized Object get(Object key) {
        return this.getDelegate().get(key);
    }

    /**
     * INTERNAL:
     * Check whether the contents have been read from the database.
     * If they have not, read them and set the delegate.
     * This method used to be synchronized, which caused deadlock.
     */
    protected Hashtable getDelegate() {
        if (delegate == null) {
            synchronized(this){
                if (delegate == null) {
                    delegate = this.buildDelegate();
                }
            }
        }
        return delegate;
    }
    
    /**
     * INTERNAL:
     * Return the real collection object.
     * This will force instantiation.
     */
    public Object getDelegateObject() {
        return getDelegate();
    }

    /**
     * INTERNAL:
     * Return the mapping attribute name, used to raise change events.
     */
     public String getTrackedAttributeName() {
         return attributeName;
     }
     
    /**
     * Return the property change listener for change tracking.
     */
     public PropertyChangeListener _persistence_getPropertyChangeListener() {
         return changeListener;
     }
    
     /**
      * PUBLIC:
      * Return the valueHolder.
      * This method used to be synchronized, which caused deadlock.
      */
     public ValueHolderInterface getValueHolder() {
         // PERF: lazy initialize value holder and vector as are normally set after creation.
         if (valueHolder == null) {
             synchronized(this){
                 if (valueHolder == null) {
                     valueHolder = new ValueHolder(new Hashtable(initialCapacity, loadFactor));
                 }
             }
         }
         return valueHolder;
     }

    /**
     * @see java.util.Hashtable#hashCode()
     */
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
    protected void initialize(Map m) {
        this.delegate = null;
        Hashtable temp = new Hashtable(m);

        this.valueHolder = new ValueHolder(temp);
    }

    /**
     * @see java.util.Hashtable#isEmpty()
     */
    public boolean isEmpty() {
        return this.getDelegate().isEmpty();
    }

    /**
     * PUBLIC:
     * Return whether the contents have been read from the database.
     */
    public boolean isInstantiated() {
        return this.getValueHolder().isInstantiated();
    }

    /**
     * @see java.util.Hashtable#keys()
     */
    public synchronized Enumeration keys() {
        return this.getDelegate().keys();
    }

    /**
     * @see java.util.Hashtable#keySet()
     */
    public Set keySet() {
        
        return new Set (){
            Set delegateSet = IndirectMap.this.getDelegate().keySet();
            
            public int size(){
                return this.delegateSet.size();
            }
        
            public boolean isEmpty(){
                return this.delegateSet.isEmpty();
            }
        
            public boolean contains(Object o){
                return this.delegateSet.contains(o);
            }
        
            public Iterator iterator(){
                return new Iterator() {
                    Iterator delegateIterator = delegateSet.iterator();
                    Object currentObject;
                    
                    public boolean hasNext() {
                        return this.delegateIterator.hasNext();
                    }
                    
                    public Object next() {
                        this.currentObject = this.delegateIterator.next();
                        return this.currentObject;
                    }
                    
                    public void remove() {
                        IndirectMap.this.raiseRemoveChangeEvent(currentObject, IndirectMap.this.getDelegate().get(currentObject));
                        this.delegateIterator.remove();
                    }
                };
            }
        
            public Object[] toArray(){
                return this.delegateSet.toArray();
            }
    
            public Object[] toArray(Object a[]){
                return this.delegateSet.toArray(a);
            }
    
            public boolean add(Object o){
                return this.delegateSet.add(o);
            }
        
            public boolean remove(Object o){
                return (IndirectMap.this.remove(o) != null);
            }
        
            public boolean containsAll(Collection c){
                return this.delegateSet.containsAll(c);
            }
        
            public boolean addAll(Collection c){
                return this.delegateSet.addAll(c);
            }
        
            public boolean retainAll(Collection c){
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
            
            public boolean removeAll(Collection c){
                boolean result = false;
                for (Iterator cs = c.iterator(); cs.hasNext(); ){
                    if (IndirectMap.this.remove(cs.next()) != null ) {
                        result = true;
                    }
                }
                return result;
            }
        
            public void clear(){
                IndirectMap.this.clear();
            }
        
            public boolean equals(Object o){
                return this.delegateSet.equals(o);
            }
            
            public int hashCode(){
                return this.delegateSet.hashCode();
            }
        };
            
            
    }

    /**
     * @see java.util.Hashtable#put(java.lang.Object, java.lang.Object)
     */
    public synchronized Object put(Object key, Object value) {
        Object oldValue = this.getDelegate().put(key, value);
        if (oldValue != null){
            raiseRemoveChangeEvent(key, oldValue);
        }
        raiseAddChangeEvent(key, value);
        return oldValue;
    }
    

    /**
     * @see java.util.Hashtable#putAll(java.util.Map)
     */
    public synchronized void putAll(Map t) {
        // Must trigger add events if tracked or uow.
        if (hasTrackedPropertyChangeListener()) {
            Iterator objects = t.keySet().iterator();
            while (objects.hasNext()) {
                Object key = objects.next();
                this.put(key, t.get(key));
            }
        }else{
            this.getDelegate().putAll(t);
        }
    }

    /**
     * @see java.util.Hashtable#rehash()
     */
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
    public synchronized Object remove(Object key) {
        Object value = this.getDelegate().remove(key);
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
     public void setTrackedAttributeName(String attributeName) {
         this.attributeName = attributeName;
     }

    /**
     * INTERNAL:
     * Set the property change listener for change tracking.
     */
     public void _persistence_setPropertyChangeListener(PropertyChangeListener changeListener) {
         this.changeListener = changeListener;
     }
     
    /**
     * INTERNAL:
     * Set the value holder.
     */
    public void setValueHolder(ValueHolderInterface valueHolder) {
        this.delegate = null;
        this.valueHolder = valueHolder;
    }

    /**
     * @see java.util.Hashtable#size()
     */
    public int size() {
        return this.getDelegate().size();
    }
    
    /**
     * INTERNAL
     * Set whether this collection should attempt do deal with adds and removes without retrieving the 
     * collection from the dB
     */
    public void setUseLazyInstantiation(boolean useLazyInstantiation){
    }
    
    /**
     * INTERNAL:
     * Return the elements that have been removed before instantiation.
     */
    public Collection getRemovedElements() {
        return null;
    }

    /**
     * INTERNAL:
     * Return the elements that have been added before instantiation.
     */
    public Collection getAddedElements() {
        return null;
    }
    
    /**
     * INTERNAL:
     * Return if any elements that have been added or removed before instantiation.
     */
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
    public Collection values() {
        return new Collection() {
            protected Collection delegateCollection = IndirectMap.this.getDelegate().values();

            public int size(){
                return delegateCollection.size();
            }
            
            public boolean isEmpty(){
                return delegateCollection.isEmpty();
            }
            
            public boolean contains(Object o){
                return delegateCollection.contains(o);
            }
            
            public Iterator iterator() {
                return new Iterator() {
                    Iterator delegateIterator = delegateCollection.iterator();
                    Object currentObject;
                    
                    public boolean hasNext() {
                        return this.delegateIterator.hasNext();
                    }
                    
                    public Object next() {
                        this.currentObject = this.delegateIterator.next();
                        return this.currentObject;
                    }
                    
                    public void remove() {
                        Iterator iterator = IndirectMap.this.getDelegate().entrySet().iterator();
                        while (iterator.hasNext()){
                            Map.Entry entry = (Map.Entry)iterator.next();
                            if (entry.getValue().equals(currentObject)){
                                IndirectMap.this.raiseRemoveChangeEvent(entry.getKey(), entry.getValue());
                            }
                            
                        }
                        this.delegateIterator.remove();
                    }
                };
            }
        
            public Object[] toArray(){
                return this.delegateCollection.toArray();
            }
            
            public Object[] toArray(Object a[]){
                return this.delegateCollection.toArray(a);
            }
            
            public boolean add(Object o){
                return this.delegateCollection.add(o);
            }
            
            public boolean remove(Object o){
                Iterator iterator = IndirectMap.this.getDelegate().entrySet().iterator();
                while (iterator.hasNext()){
                    Map.Entry entry = (Map.Entry)iterator.next();
                    if (entry.getValue().equals(o)){
                        IndirectMap.this.raiseRemoveChangeEvent(entry.getKey(), entry.getValue());
                    }
                    return true;    
                }
                return false;
            }
            
            public boolean containsAll(Collection c){
                return this.delegateCollection.containsAll(c);
            }
            
            public boolean addAll(Collection c){
                return this.delegateCollection.addAll(c);
            }
            
            public boolean removeAll(Collection c){
                boolean result = false;
                for (Iterator iterator = c.iterator(); iterator.hasNext();){
                    if (remove(iterator.next()) ){
                        result = true;
                    }
                }
                return result;
            }
            
            public boolean retainAll(Collection c){
                boolean result = false;
                for (Iterator iterator = IndirectMap.this.entrySet().iterator(); iterator.hasNext();){
                    Map.Entry entry = (Map.Entry)iterator.next();
                    if (! c.contains(entry.getValue()) ) {
                        iterator.remove();
                        result = true;
                    }
                }
                return result;
            }
            
            public void clear(){
                IndirectMap.this.clear();
            }
            
            
            public boolean equals(Object o){
                return this.delegateCollection.equals(o);
            }
            
            public int hashCode(){
                return this.delegateCollection.hashCode();
            }
            
        };
    }
}
