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
package org.eclipse.persistence.tools.workbench.uitools.app;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;

import org.eclipse.persistence.tools.workbench.utility.AbstractModel;
import org.eclipse.persistence.tools.workbench.utility.events.ChangeSupport;
import org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeListener;
import org.eclipse.persistence.tools.workbench.utility.iterators.NullIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.SingleElementIterator;


/**
 * An adapter that allows us to make a PropertyValueModel behave like
 * a read-only, single-element CollectionValueModel, sorta.
 * 
 * If the property's value is null, an empty iterator is returned
 * (i.e. you can't have a collection with a null element).
 */
public class PropertyCollectionValueModelAdapter
	extends AbstractModel
	implements CollectionValueModel
{
	/** The wrapped property value model. */
	protected PropertyValueModel valueHolder;

	/** A listener that forwards any events fired by the value holder. */
	protected PropertyChangeListener propertyChangeListener;

	/** Cache the value. */
	protected Object value;


	// ********** constructors/initialization **********

	/**
	 * Wrap the specified ListValueModel.
	 */
	public PropertyCollectionValueModelAdapter(PropertyValueModel valueHolder) {
		super();
		if (valueHolder == null) {
			throw new NullPointerException();
		}
		this.valueHolder = valueHolder;
		// postpone building the value and listening to the underlying value
		// until we have listeners ourselves...
	}

	protected void initialize() {
		super.initialize();
		this.propertyChangeListener = this.buildPropertyChangeListener();
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.AbstractModel#buildDefaultChangeSupport()
	 */
	protected ChangeSupport buildDefaultChangeSupport() {
		return new ValueModelChangeSupport(this);
	}

	/**
	 * The wrapped value has changed, forward an equivalent
	 * collection change event to our listeners.
	 */
	protected PropertyChangeListener buildPropertyChangeListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				PropertyCollectionValueModelAdapter.this.valueChanged(e.getNewValue());
			}
			public String toString() {
				return "property change listener";
			}
		};
	}


	// ********** ValueModel implementation **********

	/**
	 * @see ValueModel#getValue()
	 */
	public Object getValue() {
		if (this.value == null) {
			return NullIterator.instance();
		}
		return new SingleElementIterator(this.value);
	}


	// ********** CollectionValueModel implementation **********

	/**
	 * @see CollectionValueModel#addItem(Object)
	 */
	public void addItem(Object item) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see CollectionValueModel#addItems(java.util.Collection)
	 */
	public void addItems(Collection items) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see CollectionValueModel#removeItem(Object)
	 */
	public void removeItem(Object item) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see CollectionValueModel#removeItems(java.util.Collection)
	 */
	public void removeItems(Collection items) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see CollectionValueModel#size()
	 */
	public int size() {
		return (this.value == null) ? 0 : 1;
	}


	// ********** extend change support **********

	/**
	 * Override to start listening to the value holder if necessary.
	 * @see org.eclipse.persistence.tools.workbench.utility.Model#addCollectionChangeListener(CollectionChangeListener)
	 */
	public void addCollectionChangeListener(CollectionChangeListener listener) {
		if (this.hasNoListeners()) {
			this.engageModel();
		}
		super.addCollectionChangeListener(listener);
	}

	/**
	 * Override to start listening to the value holder if necessary.
	 * @see org.eclipse.persistence.tools.workbench.utility.Model#addCollectionChangeListener(String, CollectionChangeListener)
	 */
	public void addCollectionChangeListener(String collectionName, CollectionChangeListener listener) {
		if (collectionName == VALUE && this.hasNoListeners()) {
			this.engageModel();
		}
		super.addCollectionChangeListener(collectionName, listener);
	}

	/**
	 * Override to stop listening to the value holder if appropriate.
	 * @see org.eclipse.persistence.tools.workbench.utility.Model#removeCollectionChangeListener(CollectionChangeListener)
	 */
	public void removeCollectionChangeListener(CollectionChangeListener listener) {
		super.removeCollectionChangeListener(listener);
		if (this.hasNoListeners()) {
			this.disengageModel();
		}
	}

	/**
	 * Override to stop listening to the value holder if appropriate.
	 * @see org.eclipse.persistence.tools.workbench.utility.Model#removeCollectionChangeListener(String, CollectionChangeListener)
	 */
	public void removeCollectionChangeListener(String collectionName, CollectionChangeListener listener) {
		super.removeCollectionChangeListener(collectionName, listener);
		if (collectionName == VALUE && this.hasNoListeners()) {
			this.disengageModel();
		}
	}


	// ********** queries **********

	protected boolean hasListeners() {
		return this.hasAnyCollectionChangeListeners(VALUE);
	}

	protected boolean hasNoListeners() {
		return ! this.hasListeners();
	}


	// ********** behavior **********

	protected void engageModel() {
		this.valueHolder.addPropertyChangeListener(VALUE, this.propertyChangeListener);
		// synch our value *after* we start listening to the value holder,
		// since its value might change when a listener is added
		this.value = this.valueHolder.getValue();
	}

	protected void disengageModel() {
		this.valueHolder.removePropertyChangeListener(VALUE, this.propertyChangeListener);
		// clear out the value when we are not listening to the value holder
		this.value = null;
	}

	/**
	 * synchronize our internal value with the wrapped value
	 * and fire the appropriate events
	 */
	protected void valueChanged(Object newValue) {
		// put in "empty" check so we don't fire events unnecessarily
		if (this.value != null) {
			Object oldValue = this.value;
			this.value = null;
			this.fireItemRemoved(VALUE, oldValue);
		}
		this.value = newValue;
		// put in "empty" check so we don't fire events unnecessarily
		if (this.value != null) {
			this.fireItemAdded(VALUE, this.value);
		}
	}

	public void toString(StringBuffer sb) {
		sb.append(this.valueHolder);
	}

}
