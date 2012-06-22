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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.persistence.tools.workbench.utility.AbstractModel;
import org.eclipse.persistence.tools.workbench.utility.events.ChangeSupport;
import org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeListener;
import org.eclipse.persistence.tools.workbench.utility.events.ListChangeListener;
import org.eclipse.persistence.tools.workbench.utility.events.StateChangeListener;
import org.eclipse.persistence.tools.workbench.utility.events.TreeChangeListener;


/**
 * This abstract extension of AbstractModel provides a base for adding 
 * change listeners (PropertyChange, CollectionChange, ListChange, TreeChange)
 * to a subject and converting the subject's change notifications into a single
 * set of change notifications for the aspect VALUE.
 * 
 * The adapter will only listen to the subject (and subject holder) when the
 * adapter itself actually has listeners. This will allow the adapter to be
 * garbage collected when appropriate
 */
public abstract class AspectAdapter 
	extends AbstractModel
	implements ValueModel
{
	/**
	 * The subject that holds the aspect and fires
	 * change notification when the aspect changes.
	 * We need to hold on to this directly so we can
	 * disengage it when it changes.
	 */
	protected Object subject;

	/**
	 * A value model that holds the subject
	 * that holds the aspect and provides change notification.
	 * This is useful when there are a number of AspectAdapters
	 * that have the same subject and that subject can change.
	 * All the AspectAdapters should share the same subject holder.
	 * For now, this is can only be set upon construction and is
	 * immutable.
	 */
	protected ValueModel subjectHolder;

	/** A listener that keeps us in synch with the subjectHolder. */
	protected PropertyChangeListener subjectChangeListener;


	// ********** constructors **********

	/**
	 * Construct an AspectAdapter for the specified subject.
	 */
	protected AspectAdapter(Object subject) {
		this(new ReadOnlyPropertyValueModel(subject));
	}

	/**
	 * Construct an AspectAdapter for the specified subject holder.
	 * The subject holder cannot be null.
	 */
	protected AspectAdapter(ValueModel subjectHolder) {
		super();
		if (subjectHolder == null) {
			throw new NullPointerException();
		}
		this.subjectHolder = subjectHolder;
		// the subject is null when we are not listening to it
		// this will typically result in our value being null
		this.subject = null;
	}


	// ********** initialization **********
	
	protected void initialize() {
		super.initialize();
		this.subjectChangeListener = this.buildSubjectChangeListener();
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.AbstractModel#buildDefaultChangeSupport()
	 */
	protected ChangeSupport buildDefaultChangeSupport() {
		return new ValueModelChangeSupport(this);
	}

	/**
	 * The subject holder's value has changed, keep our subject in synch.
	 */
	protected PropertyChangeListener buildSubjectChangeListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				AspectAdapter.this.subjectChanged();
			}
			public String toString() {
				return "subject change listener";
			}
		};
	}


	// ********** behavior **********

	/**
	 * The subject has changed. Notify listeners that the value has changed.
	 */
	protected synchronized void subjectChanged() {
		Object oldValue = this.getValue();
		boolean hasListeners = this.hasListeners();
		if (hasListeners) {
			this.disengageSubject();
		}
		this.subject = this.subjectHolder.getValue();
		if (hasListeners) {
			this.engageSubject();
			this.fireAspectChange(oldValue, this.getValue());
		}
	}

	/**
	 * Return whether there are any listeners for the aspect.
	 */
	protected abstract boolean hasListeners();

	/**
	 * Return whether there are any listeners for the aspect.
	 */
	protected boolean hasNoListeners() {
		return ! this.hasListeners();
	}

	/**
	 * The aspect has changed, notify listeners appropriately.
	 */
	protected abstract void fireAspectChange(Object oldValue, Object newValue);

	/**
	 * The subject is not null - add our listener.
	 */
	protected abstract void engageNonNullSubject();

	protected void engageSubject() {
		// check for nothing to listen to
		if (this.subject != null) {
			this.engageNonNullSubject();
		}
	}

	/**
	 * The subject is not null - remove our listener.
	 */
	protected abstract void disengageNonNullSubject();

	protected void disengageSubject() {
		// check for nothing to listen to
		if (this.subject != null) {
			this.disengageNonNullSubject();
		}
	}

	protected void engageSubjectHolder() {
		this.subjectHolder.addPropertyChangeListener(VALUE, this.subjectChangeListener);
		// synch our subject *after* we start listening to the subject holder,
		// since its value might change when a listener is added
		this.subject = this.subjectHolder.getValue();
	}

	protected void disengageSubjectHolder() {
		this.subjectHolder.removePropertyChangeListener(VALUE, this.subjectChangeListener);
		// clear out the subject when we are not listening to its holder
		this.subject = null;
	}

	protected void engageModels() {
		this.engageSubjectHolder();
		this.engageSubject();
	}

	protected void disengageModels() {
		this.disengageSubject();
		this.disengageSubjectHolder();
	}


	// ********** extend change support **********
	
	/**
	 * Extend to start listening to the models if necessary.
	 * @see org.eclipse.persistence.tools.workbench.utility.Model#addStateChangeListener(StateChangeListener)
	 */
	public synchronized void addStateChangeListener(StateChangeListener listener) {
		if (this.hasNoListeners()) {
			this.engageModels();
		}
		super.addStateChangeListener(listener);
	}
	
	/**
	 * Extend to stop listening to the models if appropriate.
	 * @see org.eclipse.persistence.tools.workbench.utility.Model#removeStateChangeListener(StateChangeListener)
	 */
	public synchronized void removeStateChangeListener(StateChangeListener listener) {
		super.removeStateChangeListener(listener);
		if (this.hasNoListeners()) {
			this.disengageModels();
		}
	}
	
	/**
	 * Extend to start listening to the models if necessary.
	 * @see org.eclipse.persistence.tools.workbench.utility.Model#addPropertyChangeListener(PropertyChangeListener)
	 */
	public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
		if (this.hasNoListeners()) {
			this.engageModels();
		}
		super.addPropertyChangeListener(listener);
	}
	
	/**
	 * Extend to start listening to the models if necessary.
	 * @see org.eclipse.persistence.tools.workbench.utility.Model#addPropertyChangeListener(String, PropertyChangeListener)
	 */
	public synchronized void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		if (propertyName == VALUE && this.hasNoListeners()) {
			this.engageModels();
		}
		super.addPropertyChangeListener(propertyName, listener);
	}

	/**
	 * Extend to stop listening to the models if appropriate.
	 * @see org.eclipse.persistence.tools.workbench.utility.Model#removePropertyChangeListener(PropertyChangeListener)
	 */
	public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
		super.removePropertyChangeListener(listener);
		if (this.hasNoListeners()) {
			this.disengageModels();
		}
	}

	/**
	 * Extend to stop listening to the models if appropriate.
	 * @see org.eclipse.persistence.tools.workbench.utility.Model#removePropertyChangeListener(String, PropertyChangeListener)
	 */
	public synchronized void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		super.removePropertyChangeListener(propertyName, listener);
		if (propertyName == VALUE && this.hasNoListeners()) {
			this.disengageModels();
		}
	}

	/**
	 * Extend to start listening to the models if necessary.
	 * @see org.eclipse.persistence.tools.workbench.utility.Model#addCollectionChangeListener(CollectionChangeListener)
	 */
	public synchronized void addCollectionChangeListener(CollectionChangeListener listener) {
		if (this.hasNoListeners()) {
			this.engageModels();
		}
		super.addCollectionChangeListener(listener);
	}

	/**
	 * Extend to start listening to the models if necessary.
	 * @see org.eclipse.persistence.tools.workbench.utility.Model#addCollectionChangeListener(String, CollectionChangeListener)
	 */
	public synchronized void addCollectionChangeListener(String collectionName, CollectionChangeListener listener) {
		if (collectionName == VALUE && this.hasNoListeners()) {
			this.engageModels();
		}
		super.addCollectionChangeListener(collectionName, listener);
	}

	/**
	 * Extend to stop listening to the models if appropriate.
	 * @see org.eclipse.persistence.tools.workbench.utility.Model#removeCollectionChangeListener(CollectionChangeListener)
	 */
	public synchronized void removeCollectionChangeListener(CollectionChangeListener listener) {
		super.removeCollectionChangeListener(listener);
		if (this.hasNoListeners()) {
			this.disengageModels();
		}
	}

	/**
	 * Extend to stop listening to the models if appropriate.
	 * @see org.eclipse.persistence.tools.workbench.utility.Model#removeCollectionChangeListener(String, CollectionChangeListener)
	 */
	public synchronized void removeCollectionChangeListener(String collectionName, CollectionChangeListener listener) {
		super.removeCollectionChangeListener(collectionName, listener);
		if (collectionName == VALUE && this.hasNoListeners()) {
			this.disengageModels();
		}
	}

	/**
	 * Extend to start listening to the models if necessary.
	 * @see org.eclipse.persistence.tools.workbench.utility.Model#addListChangeListener(ListChangeListener)
	 */
	public synchronized void addListChangeListener(ListChangeListener listener) {
		if (this.hasNoListeners()) {
			this.engageModels();
		}
		super.addListChangeListener(listener);
	}

	/**
	 * Extend to start listening to the models if necessary.
	 * @see org.eclipse.persistence.tools.workbench.utility.Model#addListChangeListener(String, ListChangeListener)
	 */
	public synchronized void addListChangeListener(String listName, ListChangeListener listener) {
		if (listName == VALUE && this.hasNoListeners()) {
			this.engageModels();
		}
		super.addListChangeListener(listName, listener);
	}

	/**
	 * Extend to stop listening to the models if appropriate.
	 * @see org.eclipse.persistence.tools.workbench.utility.Model#removeListChangeListener(ListChangeListener)
	 */
	public synchronized void removeListChangeListener(ListChangeListener listener) {
		super.removeListChangeListener(listener);
		if (this.hasNoListeners()) {
			this.disengageModels();
		}
	}

	/**
	 * Extend to stop listening to the models if appropriate.
	 * @see org.eclipse.persistence.tools.workbench.utility.Model#removeListChangeListener(String, ListChangeListener)
	 */
	public synchronized void removeListChangeListener(String listName, ListChangeListener listener) {
		super.removeListChangeListener(listName, listener);
		if (listName == VALUE && this.hasNoListeners()) {
			this.disengageModels();
		}
	}

	/**
	 * Extend to start listening to the models if necessary.
	 * @see org.eclipse.persistence.tools.workbench.utility.Model#addTreeChangeListener(TreeChangeListener)
	 */
	public synchronized void addTreeChangeListener(TreeChangeListener listener) {
		if (this.hasNoListeners()) {
			this.engageModels();
		}
		super.addTreeChangeListener(listener);
	}

	/**
	 * Extend to start listening to the models if necessary.
	 * @see org.eclipse.persistence.tools.workbench.utility.Model#addTreeChangeListener(String, TreeChangeListener)
	 */
	public synchronized void addTreeChangeListener(String treeName, TreeChangeListener listener) {
		if (treeName == VALUE && this.hasNoListeners()) {
			this.engageModels();
		}
		super.addTreeChangeListener(treeName, listener);
	}

	/**
	 * Extend to stop listening to the models if appropriate.
	 * @see org.eclipse.persistence.tools.workbench.utility.Model#removeTreeChangeListener(TreeChangeListener)
	 */
	public synchronized void removeTreeChangeListener(TreeChangeListener listener) {
		super.removeTreeChangeListener(listener);
		if (this.hasNoListeners()) {
			this.disengageModels();
		}
	}

	/**
	 * Extend to stop listening to the models if appropriate.
	 * @see org.eclipse.persistence.tools.workbench.utility.Model#removeTreeChangeListener(String, TreeChangeListener)
	 */
	public synchronized void removeTreeChangeListener(String treeName, TreeChangeListener listener) {
		super.removeTreeChangeListener(treeName, listener);
		if (treeName == VALUE && this.hasNoListeners()) {
			this.disengageModels();
		}
	}

}
