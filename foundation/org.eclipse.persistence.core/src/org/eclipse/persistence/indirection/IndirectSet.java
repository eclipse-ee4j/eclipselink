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
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.eclipse.persistence.descriptors.changetracking.CollectionChangeEvent;
import org.eclipse.persistence.descriptors.changetracking.CollectionChangeTracker;
import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.internal.descriptors.changetracking.AttributeChangeListener;
import org.eclipse.persistence.internal.indirection.UnitOfWorkQueryValueHolder;
import org.eclipse.persistence.internal.localization.ToStringLocalization;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedGetMethod;
import org.eclipse.persistence.internal.security.PrivilegedMethodInvoker;
import org.eclipse.persistence.mappings.DatabaseMapping;

/**
 * IndirectSet is an example implementation of the Set protocol that
 * allows a domain class to take advantage of TopLink Indirection
 * without having to declare its instance variable as a ValueHolderInterface.
 * <p> To use an IndirectSet:<ul>
 * <li> Declare the appropriate instance variable with type Set (or Collection).
 * <li> Send the message #useTransparentCollection() to the appropriate
 * CollectionMapping.
 * <li> Send the message #useCollectionClass(IndirectSet.class) to the same
 * CollectionMapping. (The order of these two message sends is significant.)
 * </ul>
 * TopLink will place an IndirectSet in the instance variable when the
 * containing domain object is read from the database. With the first
 * message sent to the IndirectSet, the contents
 * are fetched from the database and normal Set behavior is resumed.
 *
 * <p>
 * Implementation notes:<ul>
 * <li> The Set interface is implemented by delegating nearly every message
 * to the Set held on to by the 'delegate' instance variable. (The 'delegate'
 * will be either a HashSet or yet another IndirectSet.)
 * <li> The IndirectContainer interface is implemented in a straightforward
 * fashion: <ul>
 *     <li> #get- and #setValueHolder() are implemented as simple accessors for the
 * 'valueHolder' instance variable. (Note that #setValueHolder() clears out the
 * 'delegate' instance variable, since its contents are invalidated by the arrival
 * of a new value holder.)
 *     <li> #isInstantiated() is simply delegated to the value holder.
 *     </ul>
 * <li> TopLink requires that the Cloneable interface be implemented. The #clone()
 * method must clone the 'delegate'. (The implementation here uses reflection to invoke
 * the #clone() method because it is not included in the common interface shared
 * by IndirectSet and its base delegate class, HashSet; namely, Set.)
 * <li> TopLink requires that the Serializable interface be implemented.
 * <li> The database read is ultimately triggered when one of the "delegated"
 * methods makes the first call to #getDelegate(), which in turn calls
 * #buildDelegate(), which
 * sends the message #getValue() to the value holder.
 * The value holder performs the database read.
 * <li> For debugging purposes, #toString() will <em>not</em> trigger a database
 * read. This is not required behavior.
 * </ul>
 *
 * @param <E> the type of elements maintained by this set
 * @see org.eclipse.persistence.mappings.CollectionMapping
 * @author Big Country
 * @since TOPLink/Java 3.0+
 */
public class IndirectSet<E> implements CollectionChangeTracker, Set<E>, IndirectCollection, Cloneable, Serializable {

    /** Reduce type casting */
    private volatile Set<E> delegate;

    /** Delegate indirection behavior to a value holder */
    private volatile ValueHolderInterface valueHolder;

    /** Change tracking listener. */
    private transient PropertyChangeListener changeListener = null;

    /** The mapping attribute name, used to raise change events. */
    private transient String attributeName = null;

    /** Store added elements to avoid instantiation on add. */
    private transient Set<E> addedElements = null;

    /** Store removed elements to avoid instantiation on remove. */
    private transient Set<E> removedElements = null;

    /** Store initial size for lazy init. */
    protected int initialCapacity = 10;

    /** Store load factor for lazy init. */
    protected float loadFactor = 0.75f;

    /**
     * This value is used to determine if we should attempt to do adds and removes from the list without
     * actually instantiating the list from the database. By default, this is set to false.  When set to
     * true, adding duplicate elements to the set will result in the element being added when the transaction
     * is committed.
     */
    private boolean useLazyInstantiation = false;

    /**
     * Construct an empty IndirectSet.
     */
    public IndirectSet() {
        this.delegate = null;
        this.valueHolder = null;
    }

    /**
     * Construct an empty IndirectSet with the specified initial capacity.
     *
     * @param   initialCapacity   the initial capacity of the set
     * @exception IllegalArgumentException if the specified initial capacity
     *               is negative
     */
    public IndirectSet(int initialCapacity) {
        this.delegate = null;
        this.initialCapacity = initialCapacity;
        this.valueHolder = null;
    }

    /**
     * Construct an empty IndirectSet with the specified initial capacity and
     * load factor.
     *
     * @param   initialCapacity     the initial capacity of the set
     * @param   loadFactor   the load factor of the set
     * @exception IllegalArgumentException if the specified initial capacity
     *               is negative
     */
    public IndirectSet(int initialCapacity, float loadFactor) {
        this.delegate = null;
        this.initialCapacity = initialCapacity;
        this.loadFactor = loadFactor;
        this.valueHolder = null;
    }

    /**
     * Construct an IndirectSet containing the elements of the specified collection.
     *
     * @param   c   the initial elements of the set
     */
    public IndirectSet(Collection<? extends E> c) {
        this.delegate = null;
        this.valueHolder = new ValueHolder(new HashSet<>(c));
    }

    protected boolean isRelationshipMaintenanceRequired() {
        if (this.valueHolder instanceof UnitOfWorkQueryValueHolder) {
            DatabaseMapping mapping = ((UnitOfWorkQueryValueHolder)this.valueHolder).getMapping();
            return (mapping != null) && (mapping.getRelationshipPartner() != null);
        }
        return false;
    }

    /**
     * @see java.util.Set#add(java.lang.Object)
     */
    @Override
    public boolean add(E element) {
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
        if (added) {
            raiseAddChangeEvent(element);
        }
        return added;
    }

    /**
     * @see java.util.Set#addAll(java.util.Collection)
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
     * INTERNAL:
     * Return the freshly-built delegate.
     */
    protected Set<E> buildDelegate() {
        Set<E> newDelegate = (Set<E>)getValueHolder().getValue();
        if (newDelegate == null) {
            newDelegate = new HashSet<>(this.initialCapacity, this.loadFactor);
        }
        // This can either be another indirect set or a HashSet.
        // It can be another indirect list because the mapping's query uses the same container policy.
        // Unwrap any redundant indirection layers, which can cause issues and impact performance.
        while (newDelegate instanceof IndirectSet) {
            newDelegate = ((IndirectSet) newDelegate).getDelegate();
        }
        // First add/remove any cached changes.
        if (hasAddedElements()) {
            for (Iterator<E> iterator = getAddedElements().iterator(); iterator.hasNext(); ) {
                newDelegate.add(iterator.next());
            }
            this.addedElements = null;
        }
        if (hasRemovedElements()) {
            for (Iterator<E> iterator = getRemovedElements().iterator(); iterator.hasNext(); ) {
                newDelegate.remove(iterator.next());
            }
            this.removedElements = null;
        }
        return newDelegate;
    }

    /**
     * @see java.util.Set#clear()
     */
    @Override
    public void clear() {
        if (hasBeenRegistered() || hasTrackedPropertyChangeListener()) {
            Iterator<E> objects = iterator();
            while (objects.hasNext()) {
                objects.next();
                objects.remove();
            }
            // clear delegate in case it's still not empty, see bug 338393
        }
        getDelegate().clear();
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
     * @see java.lang.Object#clone()
     * This will result in a database query if necessary.
     */

    /*
        There are 3 situations when #clone() is called:
        1.    The developer actually wants to clone the collection (typically to modify one
            of the 2 resulting collections). In which case the contents must be read from
            the database.
        2.    A UnitOfWork needs a clone (or backup clone) of the collection. But the
            UnitOfWork checks "instantiation" before cloning collections (i.e. "un-instantiated"
            collections are not cloned).
        3.    A MergeManager needs an extra copy of the collection (because the "backup"
            and "target" are the same object?). But the MergeManager also checks "instantiation"
            before merging collections (again, "un-instantiated" collections are not merged).
    */
    @Override
    public Object clone() {
        try {
            IndirectSet<E> result = (IndirectSet<E>)super.clone();
            result.delegate = this.cloneDelegate();
            result.valueHolder = new ValueHolder(result.delegate);
            result.attributeName = null;
            result.changeListener = null;
            return result;
        } catch (CloneNotSupportedException e) {
            throw new InternalError("clone not supported");
        }
    }

    /**
     * INTERNAL:
     * Clone the delegate.
     */
    protected Set<E> cloneDelegate() {
        Method cloneMethod;
        try {
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                try {
                    cloneMethod = AccessController.doPrivileged(new PrivilegedGetMethod(this.getDelegate().getClass(), "clone", (Class[])null, false));
                } catch (PrivilegedActionException exception) {
                    throw QueryException.cloneMethodRequired();
                }
            } else {
                cloneMethod = PrivilegedAccessHelper.getMethod(this.getDelegate().getClass(), "clone", (Class[])null, false);
            }
        } catch (NoSuchMethodException ex) {
            throw QueryException.cloneMethodRequired();
        }

        try {
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                try {
                    return (Set<E>)AccessController.doPrivileged(new PrivilegedMethodInvoker(cloneMethod, this.getDelegate(), (Object[])null));
                } catch (PrivilegedActionException exception) {
                    Exception throwableException = exception.getException();
                    if (throwableException instanceof IllegalAccessException) {
                        throw QueryException.cloneMethodInaccessible();
                    } else if (throwableException instanceof InvocationTargetException) {
                        throw QueryException.cloneMethodThrowException(((InvocationTargetException)throwableException).getTargetException());
                    } else {
                        throw QueryException.cloneMethodThrowException(throwableException);
                    }
                }
            } else {
                return (Set<E>)PrivilegedAccessHelper.invokeMethod(cloneMethod, this.getDelegate(), (Object[])null);
            }
        } catch (IllegalAccessException ex1) {
            throw QueryException.cloneMethodInaccessible();
        } catch (InvocationTargetException ex2) {
            throw QueryException.cloneMethodThrowException(ex2.getTargetException());
        }
    }

    /**
     * @see java.util.Set#contains(java.lang.Object)
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
        return this.getDelegate().contains(element);
    }

    /**
     * @see java.util.Set#containsAll(java.util.Collection)
     */
    @Override
    public boolean containsAll(Collection<?> c) {
        return this.getDelegate().containsAll(c);
    }

    /**
     * @see java.util.Set#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        return this.getDelegate().equals(o);
    }

    @Override
    public Spliterator<E> spliterator() {
        return getDelegate().spliterator();
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
    public boolean removeIf(Predicate<? super E> filter) {
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

    /**
     * INTERNAL:
     * Check whether the contents have been read from the database.
     * If they have not, read them and set the delegate.
     */
    protected Set<E> getDelegate() {
        Set<E> newDelegate = this.delegate;
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
     * Return the valueHolder.
     */
    @Override
    public ValueHolderInterface getValueHolder() {
        ValueHolderInterface vh = this.valueHolder;
        // PERF: lazy initialize value holder and vector as are normally set after creation.
        if (vh == null) {
            synchronized(this){
                vh = this.valueHolder;
                if (vh == null) {
                    this.valueHolder = vh = new ValueHolder(new HashSet(initialCapacity, loadFactor));
                }
            }
        }
        return vh;
    }

    /**
     * INTERNAL:
     * Return whether this IndirectSet has been registered in a UnitOfWork
     */
    public boolean hasBeenRegistered() {
        return getValueHolder() instanceof org.eclipse.persistence.internal.indirection.UnitOfWorkQueryValueHolder;
    }

    /**
     * @see java.util.Set#hashCode()
     */
    @Override
    public int hashCode() {
        return this.getDelegate().hashCode();
    }

    /**
     * @see java.util.Set#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        return this.getDelegate().isEmpty();
    }

    /**
     * Return whether the contents have been read from the database.
     */
    @Override
    public boolean isInstantiated() {
        return this.getValueHolder().isInstantiated();
    }

    /**
     * @see java.util.Set#iterator()
     */
    @Override
    public Iterator<E> iterator() {
        // Must wrap the interator to raise the remove event.
        return new Iterator<E>() {
            Iterator<E> delegateIterator = IndirectSet.this.getDelegate().iterator();
            E currentObject;

            @Override
            public boolean hasNext() {
                return this.delegateIterator.hasNext();
            }

            @Override
            public E next() {
                this.currentObject = this.delegateIterator.next();
                return this.currentObject;
            }

            @Override
            public void remove() {
                this.delegateIterator.remove();
                IndirectSet.this.raiseRemoveChangeEvent(this.currentObject);
            }

            @Override
            public void forEachRemaining(Consumer<? super E> action) {
                this.delegateIterator.forEachRemaining(action);
            }
        };
    }

    /**
     * @see java.util.Set#remove(java.lang.Object)
     */
    @Override
    public boolean remove(Object element) {
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
            this.raiseRemoveChangeEvent(element);
            return true;
        } else if (this.getDelegate().remove(element)) {
            this.raiseRemoveChangeEvent(element);
            return true;
        }
        return false;
    }

    /**
     * @see java.util.Set#removeAll(java.util.Collection)
     */
    @Override
    public boolean removeAll(Collection<?> c) {
        // Must trigger remove events if tracked or uow.
        if (hasBeenRegistered() || hasTrackedPropertyChangeListener()) {
            Iterator objects = c.iterator();
            while (objects.hasNext()) {
                this.remove(objects.next());
            }
            return true;
        }
        return this.getDelegate().removeAll(c);
    }

    /**
     * @see java.util.Set#retainAll(java.util.Collection)
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
                    this.raiseRemoveChangeEvent(object);
                }
            }
            return true;
        }
        return this.getDelegate().retainAll(c);
    }

    /**
     * INTERNAL:
     * Set the value holder.
     * Note that the delegate must be cleared out.
     */
    @Override
    public void setValueHolder(ValueHolderInterface valueHolder) {
        this.delegate = null;
        this.valueHolder = valueHolder;
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
     * @see java.util.Set#size()
     */
    @Override
    public int size() {
        return this.getDelegate().size();
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
     * @see java.util.Set#toArray()
     */
    @Override
    public Object[] toArray() {
        return this.getDelegate().toArray();
    }

    /**
     * @see java.util.Set#toArray(java.lang.Object[])
     */
    @Override
    public <T> T[] toArray(T[] a) {
        return this.getDelegate().toArray(a);
    }

    /**
     * Use the delegate's #toString(); but wrap it with braces to indicate
     * there is a bit of indirection.
     * Don't allow this method to trigger a database read.
     * @see java.util.HashSet#toString()
     */
    @Override
    public String toString() {
        if (ValueHolderInterface.shouldToStringInstantiate) {
            return this.getDelegate().toString();
        }
        if (this.isInstantiated()) {
            return "{" + this.getDelegate().toString() + "}";
        } else {
            return "{" + org.eclipse.persistence.internal.helper.Helper.getShortClassName(this.getClass()) + ": " + ToStringLocalization.buildMessage("not_instantiated", (Object[])null) + "}";

        }
    }

    /**
     * Raise the add change event and relationship maintainence.
     */
    protected void raiseAddChangeEvent(Object element) {
        if (hasTrackedPropertyChangeListener()) {
            _persistence_getPropertyChangeListener().propertyChange(new CollectionChangeEvent(this, getTrackedAttributeName(), this, element, CollectionChangeEvent.ADD, true));
        }
        if (isRelationshipMaintenanceRequired()) {
            ((UnitOfWorkQueryValueHolder)getValueHolder()).updateForeignReferenceSet(element, null);
        }
    }

    /**
     * Raise the remove change event.
     */
    protected void raiseRemoveChangeEvent(Object element) {
        if (hasTrackedPropertyChangeListener()) {
            _persistence_getPropertyChangeListener().propertyChange(new CollectionChangeEvent(this, getTrackedAttributeName(), this, element, CollectionChangeEvent.REMOVE, true));
        }
        if (isRelationshipMaintenanceRequired()) {
            ((UnitOfWorkQueryValueHolder)getValueHolder()).updateForeignReferenceRemove(element);
        }
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
            removedElements = new HashSet<>();
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
            addedElements = new HashSet<>();
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
        return (!isInstantiated()) && (shouldUseLazyInstantiation()) && (_persistence_getPropertyChangeListener() instanceof AttributeChangeListener) && ((WeavedAttributeValueHolderInterface)getValueHolder()).shouldAllowInstantiationDeferral();
    }
}
