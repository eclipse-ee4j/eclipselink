/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.uitools.app;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.Model;
import org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeListener;
import org.eclipse.persistence.tools.workbench.utility.iterators.NullIterator;


/**
 * This extension of AspectAdapter provides CollectionChange support.
 * 
 * The typical subclass will override the following methods:
 * #getValueFromSubject()
 *     at the very minimum, override this method to return an iterator on the
 *     subject's collection aspect; it does not need to be overridden if
 *     #getValue() is overridden and its behavior changed
 * #sizeFromSubject()
 *     override this method to improve performance; it does not need to be overridden if
 *     #size() is overridden and its behavior changed
 * #addItem(Object) and #removeItem(Object)
 *     override these methods if the client code needs to *change* the contents of
 *     the subject's collection aspect; oftentimes, though, the client code
 *     (e.g. UI) will need only to *get* the value
 * #addItems(Collection) and #removeItems(Collection)
 *     override these methods to improve performance, if necessary
 * #getValue()
 *     override this method only if returning an empty iterator when the
 *     subject is null is unacceptable
 * #size()
 *     override this method only if returning a zero when the
 *     subject is null is unacceptable
 */
public abstract class CollectionAspectAdapter 
	extends AspectAdapter 
	implements CollectionValueModel 
{
	/**
	 * The name of the subject's collection that we use for the value.
	 */
	protected String collectionName;

	/** A listener that listens to the subject's collection aspect. */
	protected CollectionChangeListener collectionChangeListener;


	// ********** constructors **********

	/**
	 * Construct a CollectionAspectAdapter for the specified subject
	 * and collection.
	 */
	protected CollectionAspectAdapter(String collectionName, Model subject) {
		super(subject);
		this.collectionName = collectionName;
	}

	/**
	 * Construct a CollectionAspectAdapter for the specified subject holder
	 * and collection.
	 */
	protected CollectionAspectAdapter(ValueModel subjectHolder, String collectionName) {
		super(subjectHolder);
		this.collectionName = collectionName;
	}

	/**
	 * Construct a CollectionAspectAdapter for an "unchanging" collection in
	 * the specified subject. This is useful for a collection aspect that does not
	 * change for a particular subject; but the subject will change, resulting in
	 * a new collection.
	 */
	protected CollectionAspectAdapter(ValueModel subjectHolder) {
		this(subjectHolder, null);
	}


	// ********** initialization **********

	protected void initialize() {
		super.initialize();
		this.collectionChangeListener = this.buildCollectionChangeListener();
	}

	/**
	 * The subject's collection aspect has changed, notify the listeners.
	 */
	protected CollectionChangeListener buildCollectionChangeListener() {
		// transform the subject's collection change events into VALUE collection change events
		return new CollectionChangeListener() {
			public void itemsAdded(CollectionChangeEvent e) {
				CollectionAspectAdapter.this.itemsAdded(e);
			}
			public void itemsRemoved(CollectionChangeEvent e) {
				CollectionAspectAdapter.this.itemsRemoved(e);
			}
			public void collectionChanged(CollectionChangeEvent e) {
				CollectionAspectAdapter.this.collectionChanged(e);
			}
			public String toString() {
				return "collection change listener: " + CollectionAspectAdapter.this.collectionName;
			}
		};
	}


	// ********** ValueModel implementation **********

	/**
	 * Return the value of the subject's collection aspect.
	 * This should be an *iterator* on the collection.
	 * @see ValueModel#getValue()
	 */
	public Object getValue() {
		if (this.subject == null) {
			return NullIterator.instance();
		}
		return this.getValueFromSubject();
	}

	/**
	 * Return the value of the subject's collection aspect.
	 * This should be an *iterator* on the collection.
	 * At this point we can be sure that the subject is not null.
	 * @see #getValue()
	 */
	protected Iterator getValueFromSubject() {
		throw new UnsupportedOperationException();
	}


	// ********** CollectionValueModel implementation **********

	/**
	 * Add the specified item to the subject's collection aspect.
	 * @see CollectionValueModel#addItem(Object)
	 */
	public void addItem(Object item) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Add the specified items to the subject's collection aspect.
	 * @see CollectionValueModel#addItems(Collection)
	 */
	public void addItems(Collection items) {
		for (Iterator stream = items.iterator(); stream.hasNext(); ) {
			this.addItem(stream.next());
		}
	}

	/**
	 * Remove the specified item from the subject's collection aspect.
	 * @see CollectionValueModel#removeItem(Object)
	 */
	public void removeItem(Object item) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Remove the specified items from the subject's collection aspect.
	 * @see CollectionValueModel#removeItems(Collection)
	 */
	public void removeItems(Collection items) {
		for (Iterator stream = items.iterator(); stream.hasNext(); ) {
			this.removeItem(stream.next());
		}
	}

	/**
	 * Return the size of the collection value.
	 * @see CollectionValueModel#size()
	 */
	public int size() {
		return this.subject == null ? 0 : this.sizeFromSubject();
	}

	/**
	 * Return the size of the subject's collection aspect.
	 * At this point we can be sure that the subject is not null.
	 * @see #size()
	 */
	protected int sizeFromSubject() {
		return CollectionTools.size((Iterator) this.getValue());
	}


	// ********** AspectAdapter implementation **********

	/**
	 * @see AspectAdapter#hasListeners()
	 */
	protected boolean hasListeners() {
		return this.hasAnyCollectionChangeListeners(VALUE);
	}

	/**
	 * @see AspectAdapter#fireAspectChange(Object, Object)
	 */
	protected void fireAspectChange(Object oldValue, Object newValue) {
		this.fireCollectionChanged(VALUE);
	}

	/**
	 * @see AspectAdapter#disengageNonNullSubject()
	 */
	protected void engageNonNullSubject() {
		if (this.collectionName != null) {
			((Model) this.subject).addCollectionChangeListener(this.collectionName, this.collectionChangeListener);
		}
	}

	/**
	 * @see AspectAdapter#engageNonNullSubject()
	 */
	protected void disengageNonNullSubject() {
		if (this.collectionName != null) {
			((Model) this.subject).removeCollectionChangeListener(this.collectionName, this.collectionChangeListener);
		}
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.AbstractModel#toString(StringBuffer)
	 */
	public void toString(StringBuffer sb) {
		sb.append(this.collectionName);
	}


	// ********** behavior **********

	protected void itemsAdded(CollectionChangeEvent e) {
		this.fireItemsAdded(e.cloneWithSource(this, VALUE));
	}

	protected void itemsRemoved(CollectionChangeEvent e) {
		this.fireItemsRemoved(e.cloneWithSource(this, VALUE));
	}

	protected void collectionChanged(CollectionChangeEvent e) {
		this.fireCollectionChanged(VALUE);
	}

}
