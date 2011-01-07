/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.models.transparentindirection;

import java.util.*;
import java.io.*;
import org.eclipse.persistence.indirection.*;

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
 * containing domain object is read from the datatabase. With the first
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
 * @see org.eclipse.persistence.mappings.CollectionMapping
 * @author Big Country
 * @since TOPLink/Java 3.0+
 */
public class IndirectSet implements Set, IndirectContainer, Cloneable, Serializable {

    /** Reduce type casting */
    private Set delegate;

    /** Delegate indirection behavior to a value holder */
    private ValueHolderInterface valueHolder;

    /**
     * Construct an empty IndirectSet.
     */
    public IndirectSet() {
        this.delegate = null;
        this.valueHolder = new ValueHolder(new HashSet());
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
        this.valueHolder = new ValueHolder(new HashSet(initialCapacity));
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
        this.valueHolder = new ValueHolder(new HashSet(initialCapacity, loadFactor));
    }

    /**
     * Construct an IndirectSet containing the elements of the specified collection.
     *
     * @param   c   the initial elements of the set
     */
    public IndirectSet(Collection c) {
        this.delegate = null;
        this.valueHolder = new ValueHolder(new HashSet(c));
    }

    /**
     * @see java.util.Set#add(java.lang.Object)
     */
    public boolean add(Object o) {
        return this.getDelegate().add(o);
    }

    /**
     * @see java.util.Set#addAll(java.util.Collection)
     */
    public boolean addAll(Collection c) {
        return this.getDelegate().addAll(c);
    }

    /**
     * Return the freshly-built delegate.
     */
    protected Set buildDelegate() {
        return (Set)valueHolder.getValue();
    }

    /**
     * @see java.util.Set#clear()
     */
    public void clear() {
        this.getDelegate().clear();
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
    public Object clone() {
        try {
            IndirectSet result = (IndirectSet)super.clone();
            result.delegate = this.cloneDelegate();
            return result;
        } catch (CloneNotSupportedException e) {
            throw new InternalError("clone not supported");
        }
    }

    /**
     * Clone the delegate.
     */
    protected Set cloneDelegate() {
        java.lang.reflect.Method cloneMethod;
        try {
            cloneMethod = this.getDelegate().getClass().getMethod("clone", (Class[])null);
        } catch (NoSuchMethodException ex) {
            throw new RuntimeException("public clone method required");
        }

        try {
            return (Set)cloneMethod.invoke(this.getDelegate(), (Object[])null);
        } catch (IllegalAccessException ex1) {
            throw new RuntimeException("clone method is inaccessible");
        } catch (java.lang.reflect.InvocationTargetException ex2) {
            throw new RuntimeException("clone method threw an exception: " + ex2.getTargetException());
        }
    }

    /**
     * @see java.util.Set#contains(java.lang.Object)
     */
    public boolean contains(Object o) {
        return this.getDelegate().contains(o);
    }

    /**
     * @see java.util.Set#containsAll(java.util.Collection)
     */
    public boolean containsAll(Collection c) {
        return this.getDelegate().containsAll(c);
    }

    /**
     * @see java.util.Set#equals(java.lang.Object)
     */
    public boolean equals(Object o) {
        return this.getDelegate().equals(o);
    }

    /**
     * Check whether the contents have been read from the database.
     * If they have not, read them and set the delegate.
     */
    protected Set getDelegate() {
        if (delegate == null) {
            delegate = this.buildDelegate();
        }
        return delegate;
    }

    /**
     * Return the valueHolder.
     */
    public ValueHolderInterface getValueHolder() {
        return valueHolder;
    }

    /**
     * @see java.util.Set#hashCode()
     */
    public int hashCode() {
        return this.getDelegate().hashCode();
    }

    /**
     * @see java.util.Set#isEmpty()
     */
    public boolean isEmpty() {
        return this.getDelegate().isEmpty();
    }

    /**
     * Return whether the contents have been read from the database.
     */
    public boolean isInstantiated() {
        return this.getValueHolder().isInstantiated();
    }

    /**
     * @see java.util.Set#iterator()
     */
    public Iterator iterator() {
        return this.getDelegate().iterator();
    }

    /**
     * @see java.util.Set#remove(java.lang.Object)
     */
    public boolean remove(Object o) {
        return this.getDelegate().remove(o);
    }

    /**
     * @see java.util.Set#removeAll(java.util.Collection)
     */
    public boolean removeAll(Collection c) {
        return this.getDelegate().removeAll(c);
    }

    /**
     * @see java.util.Set#retainAll(java.util.Collection)
     */
    public boolean retainAll(Collection c) {
        return this.getDelegate().retainAll(c);
    }

    /**
     * Set the value holder.
     * Note that the delegate must be cleared out.
     */
    public void setValueHolder(ValueHolderInterface valueHolder) {
        this.delegate = null;
        this.valueHolder = valueHolder;
    }

    /**
     * @see java.util.Set#size()
     */
    public int size() {
        return this.getDelegate().size();
    }

    /**
     * @see java.util.Set#toArray()
     */
    public Object[] toArray() {
        return this.getDelegate().toArray();
    }

    /**
     * @see java.util.Set#toArray(java.lang.Object[])
     */
    public Object[] toArray(Object[] a) {
        return this.getDelegate().toArray(a);
    }

    /**
     * Use the delegate's #toString(); but wrap it with braces to indicate
     * there is a bit of indirection.
     * Don't allow this method to trigger a database read.
     * @see java.util.HashSet#toString()
     */
    public String toString() {
        if (this.isInstantiated()) {
            return "{" + this.getDelegate().toString() + "}";
        } else {
            return "{" + org.eclipse.persistence.internal.helper.Helper.getShortClassName(this.getClass()) + ": not instantiated}";
        }
    }
}
