/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.indirection;

import java.util.*;
import java.beans.PropertyChangeListener;
import org.eclipse.persistence.internal.descriptors.changetracking.AttributeChangeListener;
import org.eclipse.persistence.internal.indirection.*;
import org.eclipse.persistence.descriptors.changetracking.*;

/**
 * IndirectList allows a domain class to take advantage of TopLink indirection
 * without having to declare its instance variable as a ValueHolderInterface.
 * <p>To use an IndirectList:<ul>
 * <li> Declare the appropriate instance variable with type IndirectList (jdk1.1)
 * or Collection/List/Vector (jdk1.2).
 * <li> Send the message #useTransparentCollection() to the appropriate
 * CollectionMapping.
 * </ul>
 * TopLink will place an
 * IndirectList in the instance variable when the containing domain object is read from
 * the datatabase. With the first message sent to the IndirectList, the contents
 * are fetched from the database and normal Collection/List/Vector behavior is resumed.
 *
 * @see org.eclipse.persistence.mappings.CollectionMapping
 * @see org.eclipse.persistence.indirection.IndirectMap
 * @author Big Country
 * @since TOPLink/Java 2.5
 */
public class IndirectList extends Vector implements CollectionChangeTracker, IndirectCollection {

    /** Reduce type casting. */
    protected volatile Vector delegate;

    /** Delegate indirection behavior to a value holder. */
    protected ValueHolderInterface valueHolder;

    /** Change tracking listener. */
    private transient PropertyChangeListener changeListener;
    
    /** The mapping attribute name, used to raise change events. */
    private transient String attributeName;
    
    /** Store added elements to avoid instantiation on add. */
    private transient List addedElements;
    
    /** Store removed elements to avoid instantiation on remove. */
    private transient List removedElements;

    /** Store initial size for lazy init. */
    protected int initialCapacity = 10;

    /**
     * PUBLIC:
     * Construct an empty IndirectList so that its internal data array
     * has size <tt>10</tt> and its standard capacity increment is zero.
     */
    public IndirectList() {
        this(10);
    }

    /**
     * PUBLIC:
     * Construct an empty IndirectList with the specified initial capacity and
     * with its capacity increment equal to zero.
     *
     * @param   initialCapacity   the initial capacity of the vector
     * @exception IllegalArgumentException if the specified initial capacity
     *               is negative
     */
    public IndirectList(int initialCapacity) {
        this(initialCapacity, 0);
    }

    /**
     * PUBLIC:
     * Construct an empty IndirectList with the specified initial capacity and
     * capacity increment.
     *
     * @param   initialCapacity     the initial capacity of the vector
     * @param   capacityIncrement   the amount by which the capacity is
     *                              increased when the vector overflows
     * @exception IllegalArgumentException if the specified initial capacity
     *               is negative
     */
    public IndirectList(int initialCapacity, int capacityIncrement) {
        super(0);
        this.initialize(initialCapacity, capacityIncrement);
    }

    /**
     * PUBLIC:
     * Construct an IndirectList containing the elements of the specified
     * collection, in the order they are returned by the collection's
     * iterator.
     * @param c a collection containing the elements to construct this IndirectList with.
     */
    public IndirectList(Collection c) {
        super(0);
        this.initialize(c);
    }

    /**
     * @see java.util.Vector#add(int, java.lang.Object)
     */
    public void add(int index, Object element) {
        this.getDelegate().add(index, element);
        raiseAddChangeEvent(element);
    }
    
    /**
     * Raise the add change event and relationship maintainence.
     */
    protected void raiseAddChangeEvent(Object element) {
        if (hasTopLinkPropertyChangeListener()) {
            _persistence_getPropertyChangeListener().propertyChange(new CollectionChangeEvent(this, getTopLinkAttributeName(), this, element, CollectionChangeEvent.ADD));
        }
        if (hasBeenRegistered()) {
            ((UnitOfWorkQueryValueHolder)getValueHolder()).updateForeignReferenceSet(element, null);
        }
    }
    
    /**
     * Raise the remove change event.
     */
    protected void raiseRemoveChangeEvent(Object element) {
        if (hasTopLinkPropertyChangeListener()) {
            _persistence_getPropertyChangeListener().propertyChange(new CollectionChangeEvent(this, getTopLinkAttributeName(), this, element, CollectionChangeEvent.REMOVE));
        }
        if (hasBeenRegistered()) {
            ((UnitOfWorkQueryValueHolder)getValueHolder()).updateForeignReferenceRemove(element);
        }
    }

    /**
     * @see java.util.Vector#add(java.lang.Object)
     */
    public synchronized boolean add(Object element) {
        boolean added = true;
        // PERF: If not instantiated just record the add to avoid the instantiation.
        if (shouldAvoidInstantiation()) {
            if (hasRemovedElements() && getRemovedElements().contains(element)) {
                getRemovedElements().remove(element);
            } else if (getAddedElements().contains(element)) {
                // Must avoid recursion for relationship maintenance.
                return false;
            } else {
                getAddedElements().add(element);
            }
        } else {
            added = getDelegate().add(element);
        }
        raiseAddChangeEvent(element);
        return added;
    }

    /**
     * @see java.util.Vector#addAll(int, java.util.Collection)
     */
    public synchronized boolean addAll(int index, Collection c) {
        Iterator objects = c.iterator();
        // Must trigger add events if tracked or uow.
        if (hasBeenRegistered() || hasTopLinkPropertyChangeListener()) {
            while (objects.hasNext()) {
                this.add(index, objects.next());
                index++;
            }
            return true;
        }

        return this.getDelegate().addAll(index, c);

    }

    /**
     * @see java.util.Vector#addAll(java.util.Collection)
     */
    public synchronized boolean addAll(Collection c) {
        // Must trigger add events if tracked or uow.
        if (hasBeenRegistered() || hasTopLinkPropertyChangeListener()) {
            Iterator objects = c.iterator();
            while (objects.hasNext()) {
                this.add(objects.next());
            }
            return true;
        }

        return getDelegate().addAll(c);
    }

    /**
     * @see java.util.Vector#addElement(java.lang.Object)
     */
    public synchronized void addElement(Object obj) {
        this.add(obj);
    }

    /**
     * INTERNAL:
     * Return the freshly-built delegate.
     */
    protected Vector buildDelegate() {
        Vector delegate = (Vector)getValueHolder().getValue();
        // This can either be another indirect list or a Vector.
        // It can be another indirect list because the mapping's query uses the same container policy.
        // Unwrap any redundent indirection layers, which can cause issues and impact performance.
        while (delegate instanceof IndirectList) {
            delegate = ((IndirectList) delegate).getDelegate();
        }
        // First add/remove any cached changes.
        if (hasAddedElements()) {
            int size = getAddedElements().size();
            for (int index = 0; index < size; index++) {
                Object element = ((List)getAddedElements()).get(index);
                // On a flush or resume the element may already be in the database.
                if (!delegate.contains(element)) {
                    delegate.add(element);
                }
            }
            this.addedElements = null;
        }
        if (hasRemovedElements()) {
            int size = getRemovedElements().size();
            for (int index = 0; index < size; index++) {
                delegate.remove(((List)getRemovedElements()).get(index));
            }
            this.removedElements = null;
        }
        return delegate;
    }

    /**
     * @see java.util.Vector#capacity()
     */
    public int capacity() {
        return this.getDelegate().capacity();
    }

    /**
     * @see java.util.Vector#clear()
     */
    public void clear() {
        if (hasBeenRegistered() || hasTopLinkPropertyChangeListener()) {
            Iterator objects = this.iterator();
            while (objects.hasNext()) {
                Object o = objects.next();
                objects.remove();
                this.raiseRemoveChangeEvent(o);
            }
        } else {
            this.getDelegate().clear();
        }
    }

    /**
     * PUBLIC:
     * @see java.util.Vector#clone()
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
        IndirectList result = (IndirectList)super.clone();
        result.delegate = (Vector)this.getDelegate().clone();
        result.attributeName = null;
        result.changeListener = null;
        return result;
    }

    /**
     * PUBLIC:
     * @see java.util.Vector#contains(java.lang.Object)
     */
    public boolean contains(Object element) {
        // PERF: Avoid instantiation if not required.
        if (hasAddedElements()) {
            if (getAddedElements().contains(element)) {
                return true;
            }
        }
        if (hasRemovedElements()) {
            if (getRemovedElements().contains(element)) {
                return false;
            }
        }
        return this.getDelegate().contains(element);
    }

    /**
     * @see java.util.Vector#containsAll(java.util.Collection)
     */
    public synchronized boolean containsAll(Collection c) {
        return this.getDelegate().containsAll(c);
    }

    /**
     * @see java.util.Vector#copyInto(java.lang.Object[])
     */
    public synchronized void copyInto(Object[] anArray) {
        this.getDelegate().copyInto(anArray);
    }

    /**
     * @see java.util.Vector#elementAt(int)
     */
    public synchronized Object elementAt(int index) {
        return this.getDelegate().elementAt(index);
    }

    /**
     * @see java.util.Vector#elements()
     */
    public Enumeration elements() {
        return this.getDelegate().elements();
    }

    /**
     * @see java.util.Vector#ensureCapacity(int)
     */
    public synchronized void ensureCapacity(int minCapacity) {
        this.getDelegate().ensureCapacity(minCapacity);
    }

    /**
     * @see java.util.Vector#equals(java.lang.Object)
     */
    public synchronized boolean equals(Object o) {
        return this.getDelegate().equals(o);
    }

    /**
     * @see java.util.Vector#firstElement()
     */
    public synchronized Object firstElement() {
        return this.getDelegate().firstElement();
    }

    /**
     * @see java.util.Vector#get(int)
     */
    public synchronized Object get(int index) {
        return this.getDelegate().get(index);
    }

    /**
     * INTERNAL:
     * Check whether the contents have been read from the database.
     * If they have not, read them and set the delegate.
     * This method used to be synchronized, which caused deadlock.
     */
    protected Vector getDelegate() {
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
     * Return the valueHolder.
     * This method used to be synchronized, which caused deadlock.
     */
    public ValueHolderInterface getValueHolder() {
        // PERF: lazy initialize value holder and vector as are normally set after creation.
        if (valueHolder == null) {
            synchronized(this){
                if (valueHolder == null) {
                        valueHolder = new ValueHolder(new Vector(this.initialCapacity, this.capacityIncrement));
                }
            }
        }
        return valueHolder;
    }

    /**
     * INTERNAL:
     * return whether this IndirectList has been registered with the UnitOfWork
     */
    public boolean hasBeenRegistered() {
        return getValueHolder() instanceof org.eclipse.persistence.internal.indirection.UnitOfWorkQueryValueHolder;
    }

    /**
     * INTERNAL:
     * @see java.util.Vector#hashCode()
     */
    public synchronized int hashCode() {
        return this.getDelegate().hashCode();
    }

    /**
     * @see java.util.Vector#indexOf(java.lang.Object)
     */
    public int indexOf(Object elem) {
        return this.getDelegate().indexOf(elem);
    }

    /**
     * @see java.util.Vector#indexOf(java.lang.Object, int)
     */
    public synchronized int indexOf(Object elem, int index) {
        return this.getDelegate().indexOf(elem, index);
    }

    /**
     * Initialize the instance.
     */
    protected void initialize(int initialCapacity, int capacityIncrement) {
        this.initialCapacity = initialCapacity;
        this.capacityIncrement = capacityIncrement;
        this.delegate = null;
        this.valueHolder = null;
    }

    /**
     * Initialize the instance.
     */
    protected void initialize(Collection c) {
        this.delegate = null;
        Vector temp = new Vector(c);
        this.valueHolder = new ValueHolder(temp);
    }

    /**
     * @see java.util.Vector#insertElementAt(java.lang.Object, int)
     */
    public synchronized void insertElementAt(Object obj, int index) {
        this.getDelegate().insertElementAt(obj, index);
        this.raiseAddChangeEvent(obj);
    }

    /**
     * @see java.util.Vector#isEmpty()
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
     * @see java.util.AbstractList#iterator()
     */
    public Iterator iterator() {
        // Must wrap the interator to raise the remove event.
        return new Iterator() {
            Iterator delegateIterator = IndirectList.this.getDelegate().iterator();
            Object currentObject;
            
            public boolean hasNext() {
                return this.delegateIterator.hasNext();
            }
            
            public Object next() {
                this.currentObject = this.delegateIterator.next();
                return this.currentObject;
            }
            
            public void remove() {
                this.delegateIterator.remove();
                IndirectList.this.raiseRemoveChangeEvent(this.currentObject);
            }
        };
    }

    /**
     * @see java.util.Vector#lastElement()
     */
    public synchronized Object lastElement() {
        return this.getDelegate().lastElement();
    }

    /**
     * @see java.util.Vector#lastIndexOf(java.lang.Object)
     */
    public int lastIndexOf(Object elem) {
        return this.getDelegate().lastIndexOf(elem);
    }

    /**
     * @see java.util.Vector#lastIndexOf(java.lang.Object, int)
     */
    public synchronized int lastIndexOf(Object elem, int index) {
        return this.getDelegate().lastIndexOf(elem, index);
    }

    /**
     * @see java.util.AbstractList#listIterator()
     */
    public ListIterator listIterator() {
        return this.listIterator(0);
    }

    /**
     * @see java.util.AbstractList#listIterator(int)
     */
    public ListIterator listIterator(final int index) {
        // Must wrap the interator to raise the remove event.
        return new ListIterator() {
            ListIterator delegateIterator = IndirectList.this.getDelegate().listIterator(index);
            Object currentObject;
            
            public boolean hasNext() {
                return this.delegateIterator.hasNext();
            }
            
            public boolean hasPrevious() {
                return this.delegateIterator.hasPrevious();
            }
            
            public int previousIndex() {
                return this.delegateIterator.previousIndex();
            }
            
            public int nextIndex() {
                return this.delegateIterator.nextIndex();
            }
            
            public Object next() {
                this.currentObject = this.delegateIterator.next();
                return this.currentObject;
            }
            
            public Object previous() {
                this.currentObject = this.delegateIterator.previous();
                return this.currentObject;
            }
            
            public void remove() {
                this.delegateIterator.remove();
                IndirectList.this.raiseRemoveChangeEvent(this.currentObject);
            }
            
            public void set(Object object) {
                this.delegateIterator.set(object);
                IndirectList.this.raiseRemoveChangeEvent(this.currentObject);
                IndirectList.this.raiseAddChangeEvent(object);
            }
            
            public void add(Object object) {
                this.delegateIterator.add(object);
                IndirectList.this.raiseAddChangeEvent(object);
            }
        };
    }

    /**
     * @see java.util.Vector#remove(int)
     */
    public synchronized Object remove(int index) {
        Object value = this.getDelegate().remove(index);        
        this.raiseRemoveChangeEvent(value);
        return value;
    }

    /**
     * @see java.util.Vector#remove(java.lang.Object)
     */
    public boolean remove(Object element) {
        // PERF: If not instantiated just record the removal to avoid the instantiation.
        if (shouldAvoidInstantiation()) {
            if (hasAddedElements() && getAddedElements().contains(element)) {
                getAddedElements().remove(element);
            } else if (getRemovedElements().contains(element)) {
                // Must avoid recursion for relationship maintenance.
                return false;
            } else {
                getRemovedElements().add(element);
            }
            this.raiseRemoveChangeEvent(element);
            return true;
        } else if (this.getDelegate().remove(element)) {
            this.raiseRemoveChangeEvent(element);
            return true;
        }  
        return false;
    }

    /**
     * @see java.util.Vector#removeAll(java.util.Collection)
     */
    public synchronized boolean removeAll(Collection c) {
        // Must trigger remove events if tracked or uow.
        if (hasBeenRegistered() || hasTopLinkPropertyChangeListener()) {
            Iterator objects = c.iterator();
            while (objects.hasNext()) {
                this.remove(objects.next());
            }
            return true;
        }
        return this.getDelegate().removeAll(c);
    }

    /**
     * @see java.util.Vector#removeAllElements()
     */
    public synchronized void removeAllElements() {
        // Must trigger remove events if tracked or uow.
        if (hasBeenRegistered() || hasTopLinkPropertyChangeListener()) {
            Iterator objects = this.iterator();
            while (objects.hasNext()) {
                Object object = objects.next();
                objects.remove();
                this.raiseRemoveChangeEvent(object);
            }
            return;
        }
        this.getDelegate().removeAllElements();
    }

    /**
     * @see java.util.Vector#removeElement(java.lang.Object)
     */
    public synchronized boolean removeElement(Object obj) {
        return this.remove(obj);
    }

    /**
     * @see java.util.Vector#removeElementAt(int)
     */
    public synchronized void removeElementAt(int index) {
        this.remove(index);
    }

    /**
     * @see java.util.Vector#retainAll(java.util.Collection)
     */
    public synchronized boolean retainAll(Collection c) {
        // Must trigger remove events if tracked or uow.
        if (hasBeenRegistered() || hasTopLinkPropertyChangeListener()) {
            Iterator objects = getDelegate().iterator();
            while (objects.hasNext()) {
                Object object = objects.next();
                if (!c.contains(object)) {
                    objects.remove();
                    this.raiseRemoveChangeEvent(object);
                }
            }
            return true;
        }
        return this.getDelegate().retainAll(c);
    }

    /**
     * @see java.util.Vector#set(int, java.lang.Object)
     */
    public synchronized Object set(int index, Object element) {
        Object oldValue = this.getDelegate().set(index, element);
        this.raiseRemoveChangeEvent(oldValue);
        this.raiseAddChangeEvent(element);
        return oldValue;
    }

    /**
     * @see java.util.Vector#setElementAt(java.lang.Object, int)
     */
    public synchronized void setElementAt(Object obj, int index) {
        this.set(index, obj);
    }

    /**
     * @see java.util.Vector#setSize(int)
     */
    public synchronized void setSize(int newSize) {
        // Must trigger remove events if tracked or uow.
        if (hasBeenRegistered() || hasTopLinkPropertyChangeListener()) {
            if (newSize > this.size()) {
                for (int index = size(); index > newSize; index--) {
                    this.remove(index - 1);
                }
            }
        }    
        this.getDelegate().setSize(newSize);
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
     * @see java.util.Vector#size()
     */
    public int size() {
        return this.getDelegate().size();
    }

    /**
     * @see java.util.Vector#subList(int, int)
     */
    public List subList(int fromIndex, int toIndex) {
        return this.getDelegate().subList(fromIndex, toIndex);
    }

    /**
     * @see java.util.Vector#toArray()
     */
    public synchronized Object[] toArray() {
        return this.getDelegate().toArray();
    }

    /**
     * @see java.util.Vector#toArray(java.lang.Object[])
     */
    public synchronized Object[] toArray(Object[] a) {
        return this.getDelegate().toArray(a);
    }

    /**
     * PUBLIC:
     * Use the java.util.Vector#toString(); but wrap it with braces to indicate
     * there is a bit of indirection.
     * Don't allow this method to trigger a database read.
     * @see java.util.Vector#toString()
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
     * @see java.util.Vector#trimToSize()
     */
    public synchronized void trimToSize() {
        this.getDelegate().trimToSize();
    }
    
    /**
     * INTERNAL:
     * Return the property change listener for change tracking.
     */
     public PropertyChangeListener _persistence_getPropertyChangeListener() {
         return changeListener;
     }
    
    /**
     * INTERNAL:
     * Return if the collection has a property change listener for change tracking.
     */
     public boolean hasTopLinkPropertyChangeListener() {
         return this.changeListener != null;
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
     * Return the mapping attribute name, used to raise change events.
     */
     public String getTopLinkAttributeName() {
         return attributeName;
     }
     
    /**
     * INTERNAL:
     * Set the mapping attribute name, used to raise change events.
     * This is required if the change listener is set.
     */
     public void setTopLinkAttributeName(String attributeName) {
         this.attributeName = attributeName;
     }
          
    /**
     * INTERNAL:
     * Return the elements that have been removed before instantiation.
     */
    public Collection getRemovedElements() {
        if (removedElements == null) {
            removedElements = new ArrayList();
        }
        return removedElements;
    }  

    /**
     * INTERNAL:
     * Return the elements that have been added before instantiation.
     */
    public Collection getAddedElements() {
        if (addedElements == null) {
            addedElements = new ArrayList();
        }
        return addedElements;
    }

    /**
     * INTERNAL:
     * Return if any elements that have been added before instantiation.
     */
    public boolean hasAddedElements() {
        return (addedElements != null) && (!addedElements.isEmpty());
    }

    /**
     * INTERNAL:
     * Return if any elements that have been removed before instantiation.
     */
    public boolean hasRemovedElements() {
        return (removedElements != null) && (!removedElements.isEmpty());
    }
    
    /**
     * INTERNAL:
     * Return if any elements that have been added or removed before instantiation.
     */
    public boolean hasDeferredChanges() {
        return hasRemovedElements() || hasAddedElements();
    }
    
    /**
     * INTERNAL:
     * Return if add/remove should trigger instantiation or avoid.
     * Current instantiation is avoided is using change tracking.
     */
    protected boolean shouldAvoidInstantiation() {
        return (!isInstantiated()) && (_persistence_getPropertyChangeListener() instanceof AttributeChangeListener);
    }
}