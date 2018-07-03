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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.Vector;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import org.eclipse.persistence.descriptors.changetracking.CollectionChangeEvent;
import org.eclipse.persistence.descriptors.changetracking.CollectionChangeTracker;
import org.eclipse.persistence.internal.descriptors.changetracking.AttributeChangeListener;
import org.eclipse.persistence.internal.indirection.UnitOfWorkQueryValueHolder;
import org.eclipse.persistence.internal.indirection.UnitOfWorkValueHolder;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;

/**
 * IndirectList allows a domain class to take advantage of TopLink indirection
 * without having to declare its instance variable as a ValueHolderInterface.
 * <p>To use an IndirectList:<ul>
 * <li> Declare the appropriate instance variable with type Collection/List/Vector (jdk1.2).
 * <li> Send the message #useTransparentCollection() to the appropriate
 * CollectionMapping.
 * </ul>
 * EclipseLink will place an
 * IndirectList in the instance variable when the containing domain object is read from
 * the database. With the first message sent to the IndirectList, the contents
 * are fetched from the database and normal Collection/List/Vector behavior is resumed.
 *
 * @param <E> the type of elements maintained by this list
 * @see org.eclipse.persistence.mappings.CollectionMapping
 * @see org.eclipse.persistence.indirection.IndirectMap
 * @author Big Country
 * @since TOPLink/Java 2.5
 */
public class IndirectList<E> extends Vector<E> implements CollectionChangeTracker, IndirectCollection {

    /** Reduce type casting. */
    protected volatile Vector<E> delegate;

    /** Delegate indirection behavior to a value holder. */
    protected volatile ValueHolderInterface valueHolder;

    /** Change tracking listener. */
    private transient PropertyChangeListener changeListener = null;

    /** The mapping attribute name, used to raise change events. */
    private transient String attributeName = null;

    /** Store added elements to avoid instantiation on add. */
    private transient List<E> addedElements = null;

    /** Store removed elements to avoid instantiation on remove. */
    private transient List<E> removedElements = null;

    /** Store initial size for lazy init. */
    protected int initialCapacity;

    /** PERF: Quick check flag if has been registered in a unit of work. */
    protected boolean isRegistered;

    /**
     * If the mapping using IndirectList has listOrderfield != null then this flag indicates
     * whether the list in the db has invalid order:
     * either row(s) with null order value(s) or/and "holes" in order.
     * The flag may be set to true when the objects are read from the db.
     * When collection is updated the flag set to true causes updating of listOrderField of all rows in the db.
     * After update is complete the flag is set back to false.
     **/
    private boolean isListOrderBrokenInDb;

    /**
     * This value is used to determine if we should attempt to do adds and removes from the list without
     * actually instantiating the list from the database.  By default this is set to true.
     */
    private boolean useLazyInstantiation = true;

    /**
     * PUBLIC:
     * Construct an empty IndirectList so that its internal data array
     * has size <tt>10</tt> and its standard capacity increment is zero.
     */
    public IndirectList() {
        super(0, 0);
        this.initialCapacity = 10;
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
        super(0, 0);
        this.initialCapacity = initialCapacity;
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
        super(0, capacityIncrement);
        this.initialCapacity = initialCapacity;
    }

    /**
     * PUBLIC:
     * Construct an IndirectList containing the elements of the specified
     * collection, in the order they are returned by the collection's
     * iterator.
     * @param collection a collection containing the elements to construct this IndirectList with.
     */
    public IndirectList(Collection<? extends E> collection) {
        super(0);
        this.valueHolder = new ValueHolder(new Vector(collection));
    }

    /**
     * @see java.util.Vector#add(int, java.lang.Object)
     */
    @Override
    public void add(int index, E element) {
        getDelegate().add(index, element);
        raiseAddChangeEvent(element, index);
    }

    /**
     * Raise the add change event and relationship maintenance.
     */
    protected void raiseAddChangeEvent(Object element, Integer index) {
        raiseAddChangeEvent(element, index, false);
    }
    protected void raiseAddChangeEvent(Object element, Integer index, boolean isSet) {
        if (hasTrackedPropertyChangeListener()) {
            _persistence_getPropertyChangeListener().propertyChange(new CollectionChangeEvent(this, getTrackedAttributeName(), this, element, CollectionChangeEvent.ADD, index, isSet, true));
        }
        if (isRelationshipMaintenanceRequired()) {
            ((UnitOfWorkQueryValueHolder)getValueHolder()).updateForeignReferenceSet(element, null);
        }
    }

    protected boolean isRelationshipMaintenanceRequired() {
        if (this.valueHolder instanceof UnitOfWorkQueryValueHolder) {
            DatabaseMapping mapping = ((UnitOfWorkQueryValueHolder)this.valueHolder).getMapping();
            return (mapping != null) && (mapping.getRelationshipPartner() != null);
        }
        return false;
    }

    /**
     * Raise the remove change event.
     */
    protected void raiseRemoveChangeEvent(Object element, Integer index) {
        raiseRemoveChangeEvent(element, index, false);
    }
    protected void raiseRemoveChangeEvent(Object element, Integer index, boolean isSet) {
        if (hasTrackedPropertyChangeListener()) {
            _persistence_getPropertyChangeListener().propertyChange(new CollectionChangeEvent(this, getTrackedAttributeName(), this, element, CollectionChangeEvent.REMOVE, index, isSet, true));
        }
        if (isRelationshipMaintenanceRequired()) {
            ((UnitOfWorkQueryValueHolder)getValueHolder()).updateForeignReferenceRemove(element);
        }
    }

    /**
     * @see java.util.Vector#add(java.lang.Object)
     */
    @Override
    public boolean add(E element) {
        if (!this.isRegistered) {
            return getDelegate().add(element);
        }
        boolean added = true;
        // PERF: If not instantiated just record the add to avoid the instantiation.
        if (shouldAvoidInstantiation()) {
            if (hasRemovedElements() && getRemovedElements().contains(element)) {
                getRemovedElements().remove(element);
            } else if (isRelationshipMaintenanceRequired() && getAddedElements().contains(element)) {
                // Must avoid recursion for relationship maintenance.
                return false;
            } else {
                getAddedElements().add(element);
            }
        } else {
            added = getDelegate().add(element);
        }
        raiseAddChangeEvent(element, null);
        return added;
    }

    /**
     * @see java.util.Vector#addAll(int, java.util.Collection)
     */
    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        Iterator<? extends E> objects = c.iterator();
        // Must trigger add events if tracked or uow.
        if (hasBeenRegistered() || hasTrackedPropertyChangeListener()) {
            while (objects.hasNext()) {
                this.add(index, objects.next());
                index++;
            }
            return true;
        }

        return getDelegate().addAll(index, c);

    }

    /**
     * @see java.util.Vector#addAll(java.util.Collection)
     */
    @Override
    public boolean addAll(Collection<? extends E> c) {
        // Must trigger add events if tracked or uow.
        if (hasBeenRegistered() || hasTrackedPropertyChangeListener()) {
            Iterator<? extends E> objects = c.iterator();
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
    @Override
    public void addElement(E obj) {
        add(obj);
    }

    /**
     * INTERNAL:
     * Return the freshly-built delegate.
     */
    protected Vector<E> buildDelegate() {
        Vector<E> newDelegate = (Vector<E>) getValueHolder().getValue();
        if (newDelegate == null) {
            newDelegate = new Vector<>(this.initialCapacity, this.capacityIncrement);
        }
        // This can either be another indirect list or a Vector.
        // It can be another indirect list because the mapping's query uses the same container policy.
        // Unwrap any redundant indirection layers, which can cause issues and impact performance.
        while (newDelegate instanceof IndirectList) {
            if(((IndirectList) newDelegate).isListOrderBrokenInDb()) {
                this.isListOrderBrokenInDb = true;
            }
            newDelegate = ((IndirectList<E>) newDelegate).getDelegate();
        }
        // First add/remove any cached changes.
        if (hasAddedElements()) {
            for (E element: getAddedElements()) {
                // On a flush or resume the element may already be in the database.
                if (!newDelegate.contains(element)) {
                    newDelegate.add(element);
                }
            }
            this.addedElements = null;
        }
        if (hasRemovedElements()) {
            for (E element: getRemovedElements()) {
                newDelegate.remove(element);
            }
            this.removedElements = null;
        }
        return newDelegate;
    }

    /**
     * @see java.util.Vector#capacity()
     */
    @Override
    public int capacity() {
        return getDelegate().capacity();
    }

    /**
     * @see java.util.Vector#clear()
     */
    @Override
    public void clear() {
        removeAllElements();
    }

    /**
     * INTERNAL:
     * clear any changes that have been deferred to instantiation.
     * Indirect collections with change tracking avoid instantiation on add/remove.
     */
    @Override
    public void clearDeferredChanges(){
        addedElements = null;
        removedElements = null;
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
    @Override
    public synchronized Object clone() {
        IndirectList<E> result = (IndirectList<E>)super.clone();
        result.delegate = (Vector<E>)this.getDelegate().clone();
        result.valueHolder = new ValueHolder(result.delegate);
        result.attributeName = null;
        result.changeListener = null;
        return result;
    }

    /**
     * PUBLIC:
     * @see java.util.Vector#contains(java.lang.Object)
     */
    @Override
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
        return getDelegate().contains(element);
    }

    /**
     * @see java.util.Vector#containsAll(java.util.Collection)
     */
    @Override
    public boolean containsAll(Collection<?> c) {
        return getDelegate().containsAll(c);
    }

    /**
     * @see java.util.Vector#copyInto(java.lang.Object[])
     */
    @Override
    public synchronized void copyInto(Object[] anArray) {
        getDelegate().copyInto(anArray);
    }

    /**
     * @see java.util.Vector#elementAt(int)
     */
    @Override
    public E elementAt(int index) {
        return getDelegate().elementAt(index);
    }

    /**
     * @see java.util.Vector#elements()
     */
    @Override
    public Enumeration<E> elements() {
        return getDelegate().elements();
    }

    /**
     * @see java.util.Vector#ensureCapacity(int)
     */
    @Override
    public void ensureCapacity(int minCapacity) {
        getDelegate().ensureCapacity(minCapacity);
    }

    /**
     * @see java.util.Vector#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        return getDelegate().equals(o);
    }

    /**
     * @see java.util.Vector#firstElement()
     */
    @Override
    public E firstElement() {
        return getDelegate().firstElement();
    }

    /**
     * @see java.util.Vector#get(int)
     */
    @Override
    public E get(int index) {
        return getDelegate().get(index);
    }

    /**
     * INTERNAL:
     * Check whether the contents have been read from the database.
     * If they have not, read them and set the delegate.
     * This method used to be synchronized, which caused deadlock.
     */
    protected Vector<E> getDelegate() {
        Vector<E> v = this.delegate;
        if (v == null) {
            synchronized(this){
                v = this.delegate;
                if (v == null) {
                    this.delegate = v = this.buildDelegate();
                }
            }
        }
        return v;
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
     * Return the valueHolder.
     * This method used to be synchronized, which caused deadlock.
     */
    @Override
    public ValueHolderInterface getValueHolder() {
        ValueHolderInterface vh = this.valueHolder;
        // PERF: lazy initialize value holder and vector as are normally set after creation.
        if (vh == null) {
            synchronized(this) {
                vh = this.valueHolder;
                if (vh == null) {
                        this.valueHolder = vh = new ValueHolder(new Vector(this.initialCapacity, this.capacityIncrement));
                }
            }
        }
        return vh;
    }

    /**
     * INTERNAL:
     * return whether this IndirectList has been registered with the UnitOfWork
     */
    public boolean hasBeenRegistered() {
        return getValueHolder() instanceof UnitOfWorkQueryValueHolder;
    }

    /**
     * INTERNAL:
     * @see java.util.Vector#hashCode()
     */
    @Override
    public int hashCode() {
        return this.getDelegate().hashCode();
    }

    /**
     * @see java.util.Vector#indexOf(java.lang.Object)
     */
    @Override
    public int indexOf(Object elem) {
        return this.getDelegate().indexOf(elem);
    }

    /**
     * @see java.util.Vector#indexOf(java.lang.Object, int)
     */
    @Override
    public int indexOf(Object elem, int index) {
        return this.getDelegate().indexOf(elem, index);
    }

    /**
     * @see java.util.Vector#insertElementAt(java.lang.Object, int)
     */
    @Override
    public void insertElementAt(E obj, int index) {
        this.getDelegate().insertElementAt(obj, index);
        this.raiseAddChangeEvent(obj, index);
    }

    /**
     * @see java.util.Vector#isEmpty()
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
        return getValueHolder().isInstantiated();
    }

    /**
     * @see java.util.AbstractList#iterator()
     */
    @Override
    public Iterator<E> iterator() {
        // Must wrap the interator to raise the remove event.
        return listIterator(0);
    }

    /**
     * @see java.util.Vector#lastElement()
     */
    @Override
    public E lastElement() {
        return getDelegate().lastElement();
    }

    /**
     * @see java.util.Vector#lastIndexOf(java.lang.Object)
     */
    @Override
    public int lastIndexOf(Object elem) {
        return getDelegate().lastIndexOf(elem);
    }

    /**
     * @see java.util.Vector#lastIndexOf(java.lang.Object, int)
     */
    @Override
    public int lastIndexOf(Object elem, int index) {
        return getDelegate().lastIndexOf(elem, index);
    }

    /**
     * @see java.util.AbstractList#listIterator()
     */
    @Override
    public ListIterator<E> listIterator() {
        return listIterator(0);
    }

    /**
     * @see java.util.AbstractList#listIterator(int)
     */
    @Override
    public ListIterator<E> listIterator(final int index) {
        // Must wrap the interator to raise the remove event.
        return new ListIterator<E>() {
            ListIterator<E> delegateIterator = IndirectList.this.getDelegate().listIterator(index);
            E currentObject;

            @Override
            public boolean hasNext() {
                return this.delegateIterator.hasNext();
            }

            @Override
            public boolean hasPrevious() {
                return this.delegateIterator.hasPrevious();
            }

            @Override
            public int previousIndex() {
                return this.delegateIterator.previousIndex();
            }

            @Override
            public int nextIndex() {
                return this.delegateIterator.nextIndex();
            }

            @Override
            public E next() {
                this.currentObject = this.delegateIterator.next();
                return this.currentObject;
            }

            @Override
            public E previous() {
                this.currentObject = this.delegateIterator.previous();
                return this.currentObject;
            }

            @Override
            public void remove() {
                this.delegateIterator.remove();
                IndirectList.this.raiseRemoveChangeEvent(this.currentObject, this.delegateIterator.nextIndex());
            }

            @Override
            public void set(E object) {
                this.delegateIterator.set(object);
                Integer index = this.delegateIterator.previousIndex();
                IndirectList.this.raiseRemoveChangeEvent(this.currentObject, index, true);
                IndirectList.this.raiseAddChangeEvent(object, index, true);
            }

            @Override
            public void add(E object) {
                this.delegateIterator.add(object);
                IndirectList.this.raiseAddChangeEvent(object, this.delegateIterator.previousIndex());
            }

            @Override
            public void forEachRemaining(Consumer<? super E> action) {
                this.delegateIterator.forEachRemaining(action);
            }
        };
    }

    /**
     * @see java.util.Vector#remove(int)
     */
    @Override
    public E remove(int index) {
        E value = getDelegate().remove(index);
        this.raiseRemoveChangeEvent(value, index);
        return value;
    }

    /**
     * @see java.util.Vector#remove(java.lang.Object)
     */
    @Override
    public boolean remove(Object element) {
        if (!this.isRegistered) {
            return getDelegate().remove(element);
        }
        // PERF: If not instantiated just record the removal to avoid the instantiation.
        if (shouldAvoidInstantiation()) {
            if (hasAddedElements() && getAddedElements().contains(element)) {
                getAddedElements().remove(element);
            } else if (getRemovedElements().contains(element)) {
                // Must avoid recursion for relationship maintenance.
                return false;
            } else {
                getRemovedElements().add((E) element);
            }
            this.raiseRemoveChangeEvent(element, null);
            return true;
        } else {
            int index = this.getDelegate().indexOf(element);
            if(index > -1) {
                this.getDelegate().remove(index);
                this.raiseRemoveChangeEvent(element, index);
                return true;
            }
        }
        return false;
    }

    /**
     * @see java.util.Vector#removeAll(java.util.Collection)
     */
    @Override
    public boolean removeAll(Collection<?> c) {
        // Must trigger remove events if tracked or uow.
        if (hasBeenRegistered() || hasTrackedPropertyChangeListener()) {
            boolean hasChanged = false;
            Iterator<?> objects = c.iterator();
            while (objects.hasNext()) {
                hasChanged |= remove(objects.next());
            }
            return hasChanged;
        }
        return getDelegate().removeAll(c);
    }

    /**
     * @see java.util.Vector#removeAllElements()
     */
    @Override
    public void removeAllElements() {
        // Must trigger remove events if tracked or uow.
        if (hasBeenRegistered() || hasTrackedPropertyChangeListener()) {
            Iterator objects = iterator();
            while (objects.hasNext()) {
                objects.next();
                objects.remove();
            }
            return;
        }
        getDelegate().removeAllElements();
    }

    /**
     * @see java.util.Vector#removeElement(java.lang.Object)
     */
    @Override
    public boolean removeElement(Object obj) {
        return remove(obj);
    }

    /**
     * @see java.util.Vector#removeElementAt(int)
     */
    @Override
    public void removeElementAt(int index) {
        remove(index);
    }

    /**
     * @see java.util.Vector#retainAll(java.util.Collection)
     */
    @Override
    public boolean retainAll(Collection<?> c) {
        // Must trigger remove events if tracked or uow.
        if (hasBeenRegistered() || hasTrackedPropertyChangeListener()) {
            Iterator objects = getDelegate().iterator();
            while (objects.hasNext()) {
                Object object = objects.next();
                if (!c.contains(object)) {
                    objects.remove();
                    raiseRemoveChangeEvent(object, null);
                }
            }
            return true;
        }
        return getDelegate().retainAll(c);
    }

    /**
     * @see java.util.Vector#set(int, java.lang.Object)
     */
    @Override
    public E set(int index, E element) {
        E oldValue = getDelegate().set(index, element);
        Integer bigIntIndex = index;
        raiseRemoveChangeEvent(oldValue, bigIntIndex, true);
        raiseAddChangeEvent(element, bigIntIndex, true);
        return oldValue;
    }

    /**
     * @see java.util.Vector#setElementAt(java.lang.Object, int)
     */
    @Override
    public void setElementAt(E obj, int index) {
        set(index, obj);
    }

    /**
     * @see java.util.Vector#setSize(int)
     */
    @Override
    public void setSize(int newSize) {
        // Must trigger remove events if tracked or uow.
        if (hasBeenRegistered() || hasTrackedPropertyChangeListener()) {
            if (newSize > size()) {
                for (int index = size(); index > newSize; index--) {
                    this.remove(index - 1);
                }
            }
        }
        getDelegate().setSize(newSize);
    }

    /**
     * INTERNAL
     * Set whether this collection should attempt do deal with adds and removes without retrieving the
     * collection from the dB
     */
    @Override
    public void setUseLazyInstantiation(boolean useLazyInstantiation){
        this.useLazyInstantiation = useLazyInstantiation;
    }

    /**
     * INTERNAL:
     * Set the value holder.
     */
    @Override
    public void setValueHolder(ValueHolderInterface valueHolder) {
        this.delegate = null;
        this.valueHolder = valueHolder;
        if (valueHolder instanceof UnitOfWorkQueryValueHolder) {
            this.isRegistered = true;
        }
    }

    /**
     * @see java.util.Vector#size()
     */
    @Override
    public int size() {
        return getDelegate().size();
    }

    /**
     * Return whether this collection should attempt do deal with adds and removes without retrieving the
     * collection from the dB
     * @return
     */
    protected boolean shouldUseLazyInstantiation(){
        return useLazyInstantiation;
    }

    /**
     * @see java.util.Vector#subList(int, int)
     */
    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return getDelegate().subList(fromIndex, toIndex);
    }

    /**
     * @see java.util.Vector#toArray()
     */
    @Override
    public Object[] toArray() {
        return getDelegate().toArray();
    }

    /**
     * @see java.util.Vector#toArray(java.lang.Object[])
     */
    @Override
    public <T> T[] toArray(T[] a) {
        return getDelegate().toArray(a);
    }

    @Override
    public Spliterator<E> spliterator() {
        return getDelegate().spliterator();
    }

    @Override
    public synchronized void replaceAll(UnaryOperator<E> operator) {
        // Must trigger remove/add events if tracked or uow.
        if (hasBeenRegistered() || hasTrackedPropertyChangeListener()) {
            List<E> del = getDelegate();
            for (int i = 0; i < del.size(); i++) {
                set(i, operator.apply(del.get(i)));
            }
        } else {
            getDelegate().replaceAll(operator);
        }
    }

    @Override
    public synchronized boolean removeIf(Predicate<? super E> filter) {
        // Must trigger remove events if tracked or uow.
        if (hasBeenRegistered() || hasTrackedPropertyChangeListener()) {
            boolean hasChanged = false;
            Iterator<E> objects = iterator();
            while (objects.hasNext()) {
                if (filter.test(objects.next())) {
                    objects.remove();
                    hasChanged |= true;
                }
            }
            return hasChanged;
        }
        return getDelegate().removeIf(filter);
    }

    @Override
    public void forEach(Consumer<? super E> action) {
        getDelegate().forEach(action);
    }

    @Override
    public Stream<E> parallelStream() {
        return getDelegate().parallelStream();
    }

    @Override
    public Stream<E> stream() {
        return getDelegate().stream();
    }

    @Override
    public void sort(Comparator<? super E> c) {
        getDelegate().sort(c);
    }

    /**
     * PUBLIC:
     * Use the java.util.Vector#toString(); but wrap it with braces to indicate
     * there is a bit of indirection.
     * Don't allow this method to trigger a database read.
     * @see java.util.Vector#toString()
     */
    @Override
    public String toString() {
        if (ValueHolderInterface.shouldToStringInstantiate) {
            return getDelegate().toString();
        }
        if (this.isInstantiated()) {
            return "{" + getDelegate().toString() + "}";
        } else {
            return "{" + org.eclipse.persistence.internal.helper.Helper.getShortClassName(this.getClass()) + ": not instantiated}";
        }
    }

    /**
     * @see java.util.Vector#trimToSize()
     */
    @Override
    public void trimToSize() {
        getDelegate().trimToSize();
    }

    /**
     * INTERNAL:
     * Return the property change listener for change tracking.
     */
    @Override
     public PropertyChangeListener _persistence_getPropertyChangeListener() {
         return changeListener;
     }

    /**
     * INTERNAL:
     * Return if the collection has a property change listener for change tracking.
     */
     public boolean hasTrackedPropertyChangeListener() {
         return this.changeListener != null;
     }

    /**
     * INTERNAL:
     * Set the property change listener for change tracking.
     */
    @Override
     public void _persistence_setPropertyChangeListener(PropertyChangeListener changeListener) {
         this.changeListener = changeListener;
         if (changeListener != null) {
             this.isRegistered = true;
         }
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
     * Return the elements that have been removed before instantiation.
     */
    @Override
    public Collection<E> getRemovedElements() {
        if (removedElements == null) {
            removedElements = new ArrayList<>();
        }
        return removedElements;
    }

    /**
     * INTERNAL:
     * Return the elements that have been added before instantiation.
     */
    @Override
    public Collection<E> getAddedElements() {
        if (addedElements == null) {
            addedElements = new ArrayList<>();
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
    @Override
    public boolean hasDeferredChanges() {
        return hasRemovedElements() || hasAddedElements();
    }

    /**
     * INTERNAL:
     * Return if add/remove should trigger instantiation or avoid.
     * Current instantiation is avoided is using change tracking.
     */
    protected boolean shouldAvoidInstantiation() {
        return (!isInstantiated())  && (shouldUseLazyInstantiation()) && (_persistence_getPropertyChangeListener() instanceof AttributeChangeListener) && !usesListOrderField() && ((WeavedAttributeValueHolderInterface)getValueHolder()).shouldAllowInstantiationDeferral();
    }

    /**
     * INTERNAL:
     * Returns whether the mapping has listOrderField.
     */
    protected boolean usesListOrderField() {
        if(this.valueHolder instanceof UnitOfWorkValueHolder) {
            return ((CollectionMapping)((UnitOfWorkValueHolder)this.valueHolder).getMapping()).getListOrderField() != null;
        } else {
            return false;
        }
    }

    public boolean isListOrderBrokenInDb() {
        return this.isListOrderBrokenInDb;
    }
    public void setIsListOrderBrokenInDb(boolean isBroken) {
        this.isListOrderBrokenInDb = isBroken;
    }
}
