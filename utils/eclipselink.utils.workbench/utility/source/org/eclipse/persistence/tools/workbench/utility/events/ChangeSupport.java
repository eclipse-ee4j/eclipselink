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
package org.eclipse.persistence.tools.workbench.utility.events;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.tools.workbench.utility.IdentityHashBag;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * Support object that can be used by implementors of the Model interface.
 * It provides for state, property, collection, list, and tree change notifications to
 * listeners.
 * 
 * NB: There is lots of copy-n-paste code in this class. Nearly all of this duplication
 * is an effort to prevent the unnecessary creation of new objects (typically event
 * objects). Since many events are fired when there are no listeners, we postpone
 * the creation of event objects until we know we have interested listeners.
 * Most methods have the "non-duplicated" version of the method body commented
 * out at the top of the current method body.
 * The hope was that this class would prove to be fairly static and the duplicated
 * code would not prove onerous; but that has not proven to be
 * the case, as we have added support for "state" changes, "dirty" notification,
 * and custom "notifiers", with more to come, I'm sure....  ~bjv
 */
public class ChangeSupport
	implements Serializable
{

	/** The object to be provided as the "source" for any generated events. */
	protected final Object source;

	/** Maps a listener class to a collection of listeners for that class. */
	transient private Map listeners;

	/** Maps property names to child ChangeSupport objects. */
	private Map children;

	private static final long serialVersionUID = 1L;


	// ******************** constructor ********************

	/**
	 * Construct support for the specified source of change events.
	 * The source cannot be null.
	 */	
	public ChangeSupport(Object source) {
		super();
		if (source == null) {
			throw new NullPointerException();
		}
		this.source = source;
	}


	// ******************** internal behavior ********************

	/**
	 * Adds a listener that listens to all events appropriate to that listener,
	 * regardless of the property name associated with that event.
	 * The listener cannot be null.
	 */
	protected void addListener(Class listenerClass, Object listener) {
		if (listener == null) {
			throw new NullPointerException();		// better sooner than later
		}
		synchronized (this) {
			if (this.listeners == null) {
				this.listeners = new IdentityHashMap();
			}
			IdentityHashBag listenerClassSpecificListeners = (IdentityHashBag) this.listeners.get(listenerClass);
			if (listenerClassSpecificListeners == null) {
				listenerClassSpecificListeners = new IdentityHashBag();
				this.listeners.put(listenerClass, listenerClassSpecificListeners);
			}
			listenerClassSpecificListeners.add(listener);
		}
	}

	/**
	 * Adds a listener that listens to all events appropriate to that listener,
	 * and only to those events carrying the property name specified.
	 * The aspect name cannot be null and the listener cannot be null.
	 */
	protected void addListener(String aspectName, Class listenerClass, Object listener) {
		if ((aspectName == null) || (listener == null)) {
			throw new NullPointerException();		// better sooner than later
		}
		synchronized (this) {
			if (this.children == null) {
				this.children = new IdentityHashMap();
			}
			ChangeSupport child = (ChangeSupport) this.children.get(aspectName);
			if (child == null) {
				child = this.buildChildChangeSupport();
				this.children.put(aspectName, child);
			}
			child.addListener(listenerClass, listener);
		}
	}

	/**
	 * Build and return a child change support to hold aspect-specific listeners.
	 */
	protected ChangeSupport buildChildChangeSupport() {
		return new ChangeSupport(this.source);
	}

	/**
	 * Removes a listener that has been registered for all events appropriate to that listener.
	 */
	protected void removeListener(Class listenerClass, Object listener) {
		if (this.listeners == null) {
			throw new IllegalArgumentException("listener not registered");
		}
		synchronized (this) {
			IdentityHashBag listenerClassSpecificListeners = (IdentityHashBag) this.listeners.get(listenerClass);
			if (listenerClassSpecificListeners == null) {
				throw new IllegalArgumentException("listener not registered");
			}
			if ( ! listenerClassSpecificListeners.remove(listener)) {
				throw new IllegalArgumentException("listener not registered");
			}
		}
	}

	/**
	 * Removes a listener that has been registered for appropriate
	 * events carrying the specified property name.
	 */
	protected void removeListener(String aspectName, Class listenerClass, Object listener) {
		if (this.children == null) {
			throw new IllegalArgumentException("listener not registered");
		}
		synchronized (this) {
			ChangeSupport child = (ChangeSupport) this.children.get(aspectName);
			if (child == null) {
				throw new IllegalArgumentException("listener not registered");
			}
			child.removeListener(listenerClass, listener);
		}
	}


	// ******************** internal queries ********************

	/**
	 * Return a notifier that will forward change notifications to the listeners.
	 */
	protected ChangeNotifier notifier() {
		return DefaultChangeNotifier.instance();
	}

	/**
	 * Return whether there are any "generic" listeners for the specified
	 * listener class.
	 */
	protected synchronized boolean hasAnyListeners(Class listenerClass) {
		if (this.listeners == null) {
			return false;
		}
		IdentityHashBag listenerClassSpecificListeners = (IdentityHashBag) this.listeners.get(listenerClass);
		return (listenerClassSpecificListeners != null) &&
			! listenerClassSpecificListeners.isEmpty();
	}

	/**
	 * Return whether there are any listeners for the specified
	 * listener class and property name.
	 */
	protected synchronized boolean hasAnyListeners(Class listenerClass, String aspectName) {
		if (this.hasAnyListeners(listenerClass)) {
			return true;		// there's a "generic" listener
		}
		if (this.children == null) {
			return false;
		}
		ChangeSupport child = (ChangeSupport) this.children.get(aspectName);
		return (child != null) &&
			child.hasAnyListeners(listenerClass);
	}


	// ******************** behavior ********************

	/**
	 * The specified aspect of the source has changed;
	 * override this method to perform things like setting a
	 * dirty flag or validating the source's state.
	 * The aspect name will be null if a "state change" occurred.
	 */
	protected void sourceChanged(String aspectName) {
		// the default is to do nothing
	}


	// ******************** state change support ********************

	/**
	 * Add a state change listener.
	 */
	public void addStateChangeListener(StateChangeListener listener) {
		this.addListener(StateChangeListener.class, listener);
	}

	/**
	 * Remove a state change listener.
	 */
	public void removeStateChangeListener(StateChangeListener listener) {
		this.removeListener(StateChangeListener.class, listener);
	}

	/**
	 * Return whether there are any state change listeners.
	 */
	public boolean hasAnyStateChangeListeners() {
		return this.hasAnyListeners(StateChangeListener.class);
	}

	/**
	 * Fire the specified state change event to any registered listeners.
	 */
	public void fireStateChanged(StateChangeEvent event) {

		IdentityHashBag stateChangeListeners = null;
		IdentityHashBag targets = null;

		synchronized (this) {
			if (this.listeners != null) {
				stateChangeListeners = (IdentityHashBag) this.listeners.get(StateChangeListener.class);
				if (stateChangeListeners != null) {
					targets = (IdentityHashBag) stateChangeListeners.clone();
				}
			}
		}

		if (targets != null) {
			for (Iterator stream = targets.iterator(); stream.hasNext(); ) {
				StateChangeListener target = (StateChangeListener) stream.next();
				boolean stillListening;
				synchronized (this) {
					stillListening = stateChangeListeners.contains(target);
				}
				if (stillListening) {
					this.notifier().stateChanged(target, event);
				}
			}
		}
		
		this.sourceChanged(null);
	}
	
	/**
	 * Report a generic state change event to any registered state change listeners.
	 */
	public void fireStateChanged() {
//		this.fireStateChange(new StateChangeEvent(this.source));

		IdentityHashBag stateChangeListeners = null;
		IdentityHashBag targets = null;

		synchronized (this) {
			if (this.listeners != null) {
				stateChangeListeners = (IdentityHashBag) this.listeners.get(StateChangeListener.class);
				if (stateChangeListeners != null) {
					targets = (IdentityHashBag) stateChangeListeners.clone();
				}
			}
		}

		if (targets != null) {
			StateChangeEvent event = null;
			for (Iterator stream = targets.iterator(); stream.hasNext(); ) {
				StateChangeListener target = (StateChangeListener) stream.next();
				boolean stillListening;
				synchronized (this) {
					stillListening = stateChangeListeners.contains(target);
				}
				if (stillListening) {
					if (event == null) {
						// here's the reason for the duplicate code...
						event = new StateChangeEvent(this.source);
					}
					this.notifier().stateChanged(target, event);
				}
			}
		}
		
		this.sourceChanged(null);
	}
	
	
	// ******************** property change support ********************

	/**
	 * Return whether the values are equal, with the appropriate null checks.
	 * Convenience method for checking whether an attribute value has changed.
	 */
	public boolean valuesAreEqual(Object value1, Object value2) {
		if ((value1 == null) && (value2 == null)) {
			return true;	// both are null
		}
		if ((value1 == null) || (value2 == null)) {
			return false;	// one is null but the other is not
		}
		return value1.equals(value2);
	}
	
	/**
	 * Return whether the values are different, with the appropriate null checks.
	 * Convenience method for checking whether an attribute value has changed.
	 */
	public boolean valuesAreDifferent(Object value1, Object value2) {
		return ! this.valuesAreEqual(value1, value2);
	}
	
	/**
	 * Add a property change listener that is registered for all properties.
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		this.addListener(PropertyChangeListener.class, listener);
	}

	/**
	 * Add a property change listener for the specified property. The listener
	 * will be notified only for changes to the specified property.
	 */
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		this.addListener(propertyName, PropertyChangeListener.class, listener);
	}
		
	/**
	 * Remove a property change listener that was registered for all properties.
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		this.removeListener(PropertyChangeListener.class, listener);
	}
	
	/**
	 * Remove a property change listener that was registered for a specific property.
	 */
	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		this.removeListener(propertyName, PropertyChangeListener.class, listener);
	}
	
	/**
	 * Return whether there are any property change listeners that will
	 * be notified when the specified property has changed.
	 */
	public boolean hasAnyPropertyChangeListeners(String propertyName) {
		return this.hasAnyListeners(PropertyChangeListener.class, propertyName);
	}

	/**
	 * Return whether there are any property change listeners that will
	 * be notified when any property has changed.
	 */
	public boolean hasAnyPropertyChangeListeners() {
		return this.hasAnyListeners(PropertyChangeListener.class);
	}

	/**
	 * Fire the specified property change event to any registered listeners.
	 * No event is fired if the given event's old and new values are the same;
	 * this includes when both values are null. Use a state change event
	 * for general purpose notification of changes.
	 */
	public void firePropertyChanged(PropertyChangeEvent event) {
		if (this.valuesAreEqual(event.getOldValue(), event.getNewValue())) {
			return; 
		}		
			
		String propertyName = event.getPropertyName();

		IdentityHashBag propertyChangeListeners = null;
		IdentityHashBag targets = null;
		ChangeSupport child = null;

		synchronized (this) {
			if (this.listeners != null) {
				propertyChangeListeners = (IdentityHashBag) this.listeners.get(PropertyChangeListener.class);
				if (propertyChangeListeners != null) {
					targets = (IdentityHashBag) propertyChangeListeners.clone();
				}
			}
			if ((propertyName != null) && (this.children != null)) {
				child = (ChangeSupport) this.children.get(propertyName);
			}
		}
		
		if (targets != null) {
			for (Iterator stream = targets.iterator(); stream.hasNext(); ) {
				PropertyChangeListener target = (PropertyChangeListener) stream.next();
				boolean stillListening;
				synchronized (this) {
					stillListening = propertyChangeListeners.contains(target);
				}
				if (stillListening) {
					this.notifier().propertyChange(target, event);
				}
			}
		}
		if (child != null) {
			child.firePropertyChanged(event);
		}
		
		this.sourceChanged(propertyName);
	}
		
	/**
	 * Report a bound property update to any registered property change listeners.
	 * No event is fired if the given old and new values are the same;
	 * this includes when both values are null. Use a state change event
	 * for general purpose notification of changes.
	 */
	public void firePropertyChanged(String propertyName, Object oldValue, Object newValue) {
//		this.firePropertyChanged(new PropertyChangeEvent(this.source, propertyName, oldValue, newValue));
		if (this.valuesAreDifferent(oldValue, newValue)) {
			this.firePropertyChangedInternal(propertyName, oldValue, newValue);
		}
	}

	/**
	 * The old and new values have been verified as different.
	 * Fire a property change event if there are any listeners.
	 */
	protected void firePropertyChangedInternal(String propertyName, Object oldValue, Object newValue) {
		IdentityHashBag propertyChangeListeners = null;
		IdentityHashBag targets = null;
		ChangeSupport child = null;

		synchronized (this) {
			if (this.listeners != null) {
				propertyChangeListeners = (IdentityHashBag) this.listeners.get(PropertyChangeListener.class);
				if (propertyChangeListeners != null) {
					targets = (IdentityHashBag) propertyChangeListeners.clone();
				}
			}
			if ((propertyName != null) && (this.children != null)) {
				child = (ChangeSupport) this.children.get(propertyName);
			}
		}
		
		PropertyChangeEvent event = null;

		if (targets != null) {
			for (Iterator stream = targets.iterator(); stream.hasNext(); ) {
				PropertyChangeListener target = (PropertyChangeListener) stream.next();
				boolean stillListening;
				synchronized (this) {
					stillListening = propertyChangeListeners.contains(target);
				}
				if (stillListening) {
					if (event == null) {
						// here's the reason for the duplicate code...
						event = new PropertyChangeEvent(this.source, propertyName, oldValue, newValue);
					}
					this.notifier().propertyChange(target, event);
				}
			}
		}
		if (child != null) {
			if (event == null) {
				child.firePropertyChanged(propertyName, oldValue, newValue);
			} else {
				child.firePropertyChanged(event);
			}
		}
		
		this.sourceChanged(propertyName);
	}

	/**
	 * Report an int bound property update to any registered listeners.
	 * No event is fired if old and new are equal.
	 * <p>
	 * This is merely a convenience wrapper around the more general
	 * firePropertyChange method that takes Object values.
	 */
	public void firePropertyChanged(String propertyName, int oldValue, int newValue) {
//		this.firePropertyChanged(propertyName, new Integer(oldValue), new Integer(newValue));
		if (oldValue == newValue) {
			return;
		}

		IdentityHashBag propertyChangeListeners = null;
		IdentityHashBag targets = null;
		ChangeSupport child = null;

		synchronized (this) {
			if (this.listeners != null) {
				propertyChangeListeners = (IdentityHashBag) this.listeners.get(PropertyChangeListener.class);
				if (propertyChangeListeners != null) {
					targets = (IdentityHashBag) propertyChangeListeners.clone();
				}
			}
			if ((propertyName != null) && (this.children != null)) {
				child = (ChangeSupport) this.children.get(propertyName);
			}
		}
		
		PropertyChangeEvent event = null;

		if (targets != null) {
			for (Iterator stream = targets.iterator(); stream.hasNext(); ) {
				PropertyChangeListener target = (PropertyChangeListener) stream.next();
				boolean stillListening;
				synchronized (this) {
					stillListening = propertyChangeListeners.contains(target);
				}
				if (stillListening) {
					if (event == null) {
						// here's the reason for the duplicate code...
						event = new PropertyChangeEvent(this.source, propertyName, new Integer(oldValue), new Integer(newValue));
					}
					this.notifier().propertyChange(target, event);
				}
			}
		}
		if (child != null) {
			if (event == null) {
				child.firePropertyChanged(propertyName, oldValue, newValue);
			} else {
				child.firePropertyChanged(event);
			}
		}
		
		this.sourceChanged(propertyName);
	}
	
	/**
	 * Report a boolean bound property update to any registered listeners.
	 * No event is fired if old and new are equal.
	 * <p>
	 * This is merely a convenience wrapper around the more general
	 * firePropertyChange method that takes Object values.
	 */
	public void firePropertyChanged(String propertyName, boolean oldValue, boolean newValue) {
		if (oldValue != newValue) {
			this.firePropertyChangedInternal(propertyName, Boolean.valueOf(oldValue), Boolean.valueOf(newValue));
		}
	}

		
	// ******************** collection change support ********************
		
	/**
	 * Add a collection change listener that is registered for all collections.
	 */
	public void addCollectionChangeListener(CollectionChangeListener listener) {
		this.addListener(CollectionChangeListener.class, listener);
	}
		
	/**
	 * Add a collection change listener for the specified collection. The listener
	 * will be notified only for changes to the specified collection.
	 */
	public void addCollectionChangeListener(String collectionName, CollectionChangeListener listener) {
		this.addListener(collectionName, CollectionChangeListener.class, listener);
	}
		
	/**
	 * Remove a collection change listener that was registered for all collections.
	 */
	public void removeCollectionChangeListener(CollectionChangeListener listener) {
		this.removeListener(CollectionChangeListener.class, listener);
	}
	
	/**
	 * Remove a collection change listener that was registered for a specific collection.
	 */
	public void removeCollectionChangeListener(String collectionName, CollectionChangeListener listener) {
		this.removeListener(collectionName, CollectionChangeListener.class, listener);
	}
	
	/**
	 * Return whether there are any collection change listeners that will
	 * be notified when the specified collection has changed.
	 */
	public boolean hasAnyCollectionChangeListeners(String collectionName) {
		return this.hasAnyListeners(CollectionChangeListener.class, collectionName);
	}
	
	/**
	 * Return whether there are any collection change listeners that will
	 * be notified when any collection has changed.
	 */
	public boolean hasAnyCollectionChangeListeners() {
		return this.hasAnyListeners(CollectionChangeListener.class);
	}
	
	/**
	 * Report a bound collection update to any registered listeners.
	 */
	public void fireItemsAdded(CollectionChangeEvent event) {
		if (event.size() == 0) {
			return;
		}

		String collectionName = event.getCollectionName();	

		IdentityHashBag collectionChangeListeners = null;
		IdentityHashBag targets = null;
		ChangeSupport child = null;

		synchronized (this) {
			if (this.listeners != null) {
				collectionChangeListeners = (IdentityHashBag) this.listeners.get(CollectionChangeListener.class);
				if (collectionChangeListeners != null) {
					targets = (IdentityHashBag) collectionChangeListeners.clone();
				}
			}
			if ((collectionName != null) && (this.children != null)) {
				child = (ChangeSupport) this.children.get(collectionName);
			}
		}

		if (targets != null) {
			for (Iterator stream = targets.iterator(); stream.hasNext(); ) {
				CollectionChangeListener target = (CollectionChangeListener) stream.next();
				boolean stillListening;
				synchronized (this) {
					stillListening = collectionChangeListeners.contains(target);
				}
				if (stillListening) {
					this.notifier().itemsAdded(target, event);
				}
			}
		}
		if (child != null) {
			child.fireItemsAdded(event);
		}
		
		this.sourceChanged(collectionName);
	}
	
	/**
	 * Report a bound collection update to any registered listeners.
	 */
	public void fireItemsAdded(String collectionName, Collection addedItems) {
//		this.fireItemsAdded(new CollectionChangeEvent(this.source, collectionName, addedItems));
		if (addedItems.size() == 0) {
			return;
		}

		IdentityHashBag collectionChangeListeners = null;
		IdentityHashBag targets = null;
		ChangeSupport child = null;

		synchronized (this) {
			if (this.listeners != null) {
				collectionChangeListeners = (IdentityHashBag) this.listeners.get(CollectionChangeListener.class);
				if (collectionChangeListeners != null) {
					targets = (IdentityHashBag) collectionChangeListeners.clone();
				}
			}
			if ((collectionName != null) && (this.children != null)) {
				child = (ChangeSupport) this.children.get(collectionName);
			}
		}

		CollectionChangeEvent event = null;

		if (targets != null) {
			for (Iterator stream = targets.iterator(); stream.hasNext(); ) {
				CollectionChangeListener target = (CollectionChangeListener) stream.next();
				boolean stillListening;
				synchronized (this) {
					stillListening = collectionChangeListeners.contains(target);
				}
				if (stillListening) {
					if (event == null) {
						// here's the reason for the duplicate code...
						event = new CollectionChangeEvent(this.source, collectionName, addedItems);
					}
					this.notifier().itemsAdded(target, event);
				}
			}
		}
		if (child != null) {
			if (event == null) {
				child.fireItemsAdded(collectionName, addedItems);
			} else {
				child.fireItemsAdded(event);
			}
		}
		
		this.sourceChanged(collectionName);
	}
	
	/**
	 * Report a bound collection update to any registered listeners.
	 */
	public void fireItemAdded(String collectionName, Object addedItem) {
//		this.fireItemsAdded(collectionName, Collections.singleton(addedItem));

		IdentityHashBag collectionChangeListeners = null;
		IdentityHashBag targets = null;
		ChangeSupport child = null;

		synchronized (this) {
			if (this.listeners != null) {
				collectionChangeListeners = (IdentityHashBag) this.listeners.get(CollectionChangeListener.class);
				if (collectionChangeListeners != null) {
					targets = (IdentityHashBag) collectionChangeListeners.clone();
				}
			}
			if ((collectionName != null) && (this.children != null)) {
				child = (ChangeSupport) this.children.get(collectionName);
			}
		}

		CollectionChangeEvent event = null;

		if (targets != null) {
			for (Iterator stream = targets.iterator(); stream.hasNext(); ) {
				CollectionChangeListener target = (CollectionChangeListener) stream.next();
				boolean stillListening;
				synchronized (this) {
					stillListening = collectionChangeListeners.contains(target);
				}
				if (stillListening) {
					if (event == null) {
						// here's the reason for the duplicate code...
						event = new CollectionChangeEvent(this.source, collectionName, Collections.singleton(addedItem));
					}
					this.notifier().itemsAdded(target, event);
				}
			}
		}
		if (child != null) {
			if (event == null) {
				child.fireItemAdded(collectionName, addedItem);
			} else {
				child.fireItemsAdded(event);
			}
		}
		
		this.sourceChanged(collectionName);
	}
	
	/**
	 * Report a bound collection update to any registered listeners.
	 */
	public void fireItemsRemoved(CollectionChangeEvent event) {
		if (event.size() == 0) {
			return;
		}

		String collectionName = event.getCollectionName();	

		IdentityHashBag collectionChangeListeners = null;
		IdentityHashBag targets = null;
		ChangeSupport child = null;

		synchronized (this) {
			if (this.listeners != null) {
				collectionChangeListeners = (IdentityHashBag) this.listeners.get(CollectionChangeListener.class);
				if (collectionChangeListeners != null) {
					targets = (IdentityHashBag) collectionChangeListeners.clone();
				}
			}
			if ((collectionName != null) && (this.children != null)) {
				child = (ChangeSupport) this.children.get(collectionName);
			}
		}

		if (targets != null) {
			for (Iterator stream = targets.iterator(); stream.hasNext(); ) {
				CollectionChangeListener target = (CollectionChangeListener) stream.next();
				boolean stillListening;
				synchronized (this) {
					stillListening = collectionChangeListeners.contains(target);
				}
				if (stillListening) {
					this.notifier().itemsRemoved(target, event);
				}
			}
		}
		if (child != null) {
			child.fireItemsRemoved(event);
		}
		
		this.sourceChanged(collectionName);
	}
	
	/**
	 * Report a bound collection update to any registered listeners.
	 */
	public void fireItemsRemoved(String collectionName, Collection removedItems) {
//		this.fireItemsRemoved(new CollectionChangeEvent(this.source, collectionName, removedItems));
		if (removedItems.size() == 0) {
			return;
		}

		IdentityHashBag collectionChangeListeners = null;
		IdentityHashBag targets = null;
		ChangeSupport child = null;

		synchronized (this) {
			if (this.listeners != null) {
				collectionChangeListeners = (IdentityHashBag) this.listeners.get(CollectionChangeListener.class);
				if (collectionChangeListeners != null) {
					targets = (IdentityHashBag) collectionChangeListeners.clone();
				}
			}
			if ((collectionName != null) && (this.children != null)) {
				child = (ChangeSupport) this.children.get(collectionName);
			}
		}

		CollectionChangeEvent event = null;

		if (targets != null) {
			for (Iterator stream = targets.iterator(); stream.hasNext(); ) {
				CollectionChangeListener target = (CollectionChangeListener) stream.next();
				boolean stillListening;
				synchronized (this) {
					stillListening = collectionChangeListeners.contains(target);
				}
				if (stillListening) {
					if (event == null) {
						// here's the reason for the duplicate code...
						event = new CollectionChangeEvent(this.source, collectionName, removedItems);
					}
					this.notifier().itemsRemoved(target, event);
				}
			}
		}
		if (child != null) {
			if (event == null) {
				child.fireItemsRemoved(collectionName, removedItems);
			} else {
				child.fireItemsRemoved(event);
			}
		}
		
		this.sourceChanged(collectionName);
	}
	
	/**
	 * Report a bound collection update to any registered listeners.
	 */
	public void fireItemRemoved(String collectionName, Object removedItem) {
//		this.fireItemsRemoved(collectionName, Collections.singleton(removedItem));

		IdentityHashBag collectionChangeListeners = null;
		IdentityHashBag targets = null;
		ChangeSupport child = null;

		synchronized (this) {
			if (this.listeners != null) {
				collectionChangeListeners = (IdentityHashBag) this.listeners.get(CollectionChangeListener.class);
				if (collectionChangeListeners != null) {
					targets = (IdentityHashBag) collectionChangeListeners.clone();
				}
			}
			if ((collectionName != null) && (this.children != null)) {
				child = (ChangeSupport) this.children.get(collectionName);
			}
		}

		CollectionChangeEvent event = null;

		if (targets != null) {
			for (Iterator stream = targets.iterator(); stream.hasNext(); ) {
				CollectionChangeListener target = (CollectionChangeListener) stream.next();
				boolean stillListening;
				synchronized (this) {
					stillListening = collectionChangeListeners.contains(target);
				}
				if (stillListening) {
					if (event == null) {
						// here's the reason for the duplicate code...
						event = new CollectionChangeEvent(this.source, collectionName, Collections.singleton(removedItem));
					}
					this.notifier().itemsRemoved(target, event);
				}
			}
		}
		if (child != null) {
			if (event == null) {
				child.fireItemRemoved(collectionName, removedItem);
			} else {
				child.fireItemsRemoved(event);
			}
		}
		
		this.sourceChanged(collectionName);
	}
	
	/**
	 * Report a bound collection update to any registered listeners.
	 */
	public void fireCollectionChanged(CollectionChangeEvent event) {
		String collectionName = event.getCollectionName();	

		IdentityHashBag collectionChangeListeners = null;
		IdentityHashBag targets = null;
		ChangeSupport child = null;

		synchronized (this) {
			if (this.listeners != null) {
				collectionChangeListeners = (IdentityHashBag) this.listeners.get(CollectionChangeListener.class);
				if (collectionChangeListeners != null) {
					targets = (IdentityHashBag) collectionChangeListeners.clone();
				}
			}
			if ((collectionName != null) && (this.children != null)) {
				child = (ChangeSupport) this.children.get(collectionName);
			}
		}

		if (targets != null) {
			for (Iterator stream = targets.iterator(); stream.hasNext(); ) {
				CollectionChangeListener target = (CollectionChangeListener) stream.next();
				boolean stillListening;
				synchronized (this) {
					stillListening = collectionChangeListeners.contains(target);
				}
				if (stillListening) {
					this.notifier().collectionChanged(target, event);
				}
			}
		}
		if (child != null) {
			child.fireCollectionChanged(event);
		}
		
		this.sourceChanged(collectionName);
	}
	
	/**
	 * Report a bound collection update to any registered listeners.
	 */
	public void fireCollectionChanged(String collectionName) {
//		this.fireCollectionChanged(new CollectionChangeEvent(this.source, collectionName));

		IdentityHashBag collectionChangeListeners = null;
		IdentityHashBag targets = null;
		ChangeSupport child = null;

		synchronized (this) {
			if (this.listeners != null) {
				collectionChangeListeners = (IdentityHashBag) this.listeners.get(CollectionChangeListener.class);
				if (collectionChangeListeners != null) {
					targets = (IdentityHashBag) collectionChangeListeners.clone();
				}
			}
			if ((collectionName != null) && (this.children != null)) {
				child = (ChangeSupport) this.children.get(collectionName);
			}
		}

		CollectionChangeEvent event = null;

		if (targets != null) {
			for (Iterator stream = targets.iterator(); stream.hasNext(); ) {
				CollectionChangeListener target = (CollectionChangeListener) stream.next();
				boolean stillListening;
				synchronized (this) {
					stillListening = collectionChangeListeners.contains(target);
				}
				if (stillListening) {
					if (event == null) {
						// here's the reason for the duplicate code...
						event = new CollectionChangeEvent(this.source, collectionName);
					}
					this.notifier().collectionChanged(target, event);
				}
			}
		}
		if (child != null) {
			if (event == null) {
				child.fireCollectionChanged(collectionName);
			} else {
				child.fireCollectionChanged(event);
			}
		}

		this.sourceChanged(collectionName);
	}
	
	
	// ******************** list change support ********************
	
	/**
	 * Add a list change listener that is registered for all lists.
	 */
	public void addListChangeListener(ListChangeListener listener) {
		this.addListener(ListChangeListener.class, listener);
	}

	/**
	 * Add a list change listener for the specified list. The listener
	 * will be notified only for changes to the specified list.
	 */
	public void addListChangeListener(String listName, ListChangeListener listener) {
		this.addListener(listName, ListChangeListener.class, listener);
	}
		
	/**
	 * Remove a list change listener that was registered for all lists.
	 */
	public void removeListChangeListener(ListChangeListener listener) {
		this.removeListener(ListChangeListener.class, listener);
	}
	
	/**
	 * Remove a list change listener that was registered for a specific list.
	 */
	public void removeListChangeListener(String listName, ListChangeListener listener) {
		this.removeListener(listName, ListChangeListener.class, listener);
	}
	
	/**
	 * Return whether there are any list change listeners that will
	 * be notified when the specified list has changed.
	 */
	public boolean hasAnyListChangeListeners(String listName) {
		return this.hasAnyListeners(ListChangeListener.class, listName);
	}
	
	/**
	 * Return whether there are any list change listeners that will
	 * be notified when any list has changed.
	 */
	public boolean hasAnyListChangeListeners() {
		return this.hasAnyListeners(ListChangeListener.class);
	}
	
	/**
	 * Report a bound list update to any registered listeners.
	 */
	public void fireItemsAdded(ListChangeEvent event) {
		if (event.size() == 0) {
			return;
		}

		String listName = event.getListName();	

		IdentityHashBag listChangeListeners = null;
		IdentityHashBag targets = null;
		ChangeSupport child = null;

		synchronized (this) {
			if (this.listeners != null) {
				listChangeListeners = (IdentityHashBag) this.listeners.get(ListChangeListener.class);
				if (listChangeListeners != null) {
					targets = (IdentityHashBag) listChangeListeners.clone();
				}
			}
			if ((listName != null) && (this.children != null)) {
				child = (ChangeSupport) this.children.get(listName);
			}
		}

		if (targets != null) {
			for (Iterator stream = targets.iterator(); stream.hasNext(); ) {
				ListChangeListener target = (ListChangeListener) stream.next();
				boolean stillListening;
				synchronized (this) {
					stillListening = listChangeListeners.contains(target);
				}
				if (stillListening) {
					this.notifier().itemsAdded(target, event);
				}
			}
		}
		if (child != null) {
			child.fireItemsAdded(event);
		}

		this.sourceChanged(listName);
	}
	
	/**
	 * Report a bound list update to any registered listeners.
	 */
	public void fireItemsAdded(String listName, int index, List addedItems) {
//		this.fireItemsAdded(new ListChangeEvent(this.source, listName, index, addedItems));
		if (addedItems.size() == 0) {
			return;
		}

		IdentityHashBag listChangeListeners = null;
		IdentityHashBag targets = null;
		ChangeSupport child = null;

		synchronized (this) {
			if (this.listeners != null) {
				listChangeListeners = (IdentityHashBag) this.listeners.get(ListChangeListener.class);
				if (listChangeListeners != null) {
					targets = (IdentityHashBag) listChangeListeners.clone();
				}
			}
			if ((listName != null) && (this.children != null)) {
				child = (ChangeSupport) this.children.get(listName);
			}
		}

		ListChangeEvent event = null;

		if (targets != null) {
			for (Iterator stream = targets.iterator(); stream.hasNext(); ) {
				ListChangeListener target = (ListChangeListener) stream.next();
				boolean stillListening;
				synchronized (this) {
					stillListening = listChangeListeners.contains(target);
				}
				if (stillListening) {
					if (event == null) {
						// here's the reason for the duplicate code...
						event = new ListChangeEvent(this.source, listName, index, addedItems);
					}
					this.notifier().itemsAdded(target, event);
				}
			}
		}
		if (child != null) {
			if (event == null) {
				child.fireItemsAdded(listName, index, addedItems);
			} else {
				child.fireItemsAdded(event);
			}
		}
		
		this.sourceChanged(listName);
	}
	
	/**
	 * Report a bound list update to any registered listeners.
	 */
	public void fireItemAdded(String listName, int index, Object addedItem) {
//		this.fireItemsAdded(listName, index, Collections.singletonList(addedItem));

		IdentityHashBag listChangeListeners = null;
		IdentityHashBag targets = null;
		ChangeSupport child = null;

		synchronized (this) {
			if (this.listeners != null) {
				listChangeListeners = (IdentityHashBag) this.listeners.get(ListChangeListener.class);
				if (listChangeListeners != null) {
					targets = (IdentityHashBag) listChangeListeners.clone();
				}
			}
			if ((listName != null) && (this.children != null)) {
				child = (ChangeSupport) this.children.get(listName);
			}
		}

		ListChangeEvent event = null;

		if (targets != null) {
			for (Iterator stream = targets.iterator(); stream.hasNext(); ) {
				ListChangeListener target = (ListChangeListener) stream.next();
				boolean stillListening;
				synchronized (this) {
					stillListening = listChangeListeners.contains(target);
				}
				if (stillListening) {
					if (event == null) {
						// here's the reason for the duplicate code...
						event = new ListChangeEvent(this.source, listName, index, Collections.singletonList(addedItem));
					}
					this.notifier().itemsAdded(target, event);
				}
			}
		}
		if (child != null) {
			if (event == null) {
				child.fireItemAdded(listName, index, addedItem);
			} else {
				child.fireItemsAdded(event);
			}
		}
		
		this.sourceChanged(listName);
	}
	
	/**
	 * Report a bound list update to any registered listeners.
	 */
	public void fireItemsRemoved(ListChangeEvent event) {
		if (event.size() == 0) {
			return;
		}

		String listName = event.getListName();	

		IdentityHashBag listChangeListeners = null;
		IdentityHashBag targets = null;
		ChangeSupport child = null;

		synchronized (this) {
			if (this.listeners != null) {
				listChangeListeners = (IdentityHashBag) this.listeners.get(ListChangeListener.class);
				if (listChangeListeners != null) {
					targets = (IdentityHashBag) listChangeListeners.clone();
				}
			}
			if ((listName != null) && (this.children != null)) {
				child = (ChangeSupport) this.children.get(listName);
			}
		}

		if (targets != null) {
			for (Iterator stream = targets.iterator(); stream.hasNext(); ) {
				ListChangeListener target = (ListChangeListener) stream.next();
				boolean stillListening;
				synchronized (this) {
					stillListening = listChangeListeners.contains(target);
				}
				if (stillListening) {
					this.notifier().itemsRemoved(target, event);
				}
			}
		}
		if (child != null) {
			child.fireItemsRemoved(event);
		}
		
		this.sourceChanged(listName);
	}
	
	/**
	 * Report a bound list update to any registered listeners.
	 */
	public void fireItemsRemoved(String listName, int index, List removedItems) {
//		this.fireItemsRemoved(new ListChangeEvent(this.source, listName, index, removedItems));
		if (removedItems.size() == 0) {
			return;
		}

		IdentityHashBag listChangeListeners = null;
		IdentityHashBag targets = null;
		ChangeSupport child = null;

		synchronized (this) {
			if (this.listeners != null) {
				listChangeListeners = (IdentityHashBag) this.listeners.get(ListChangeListener.class);
				if (listChangeListeners != null) {
					targets = (IdentityHashBag) listChangeListeners.clone();
				}
			}
			if ((listName != null) && (this.children != null)) {
				child = (ChangeSupport) this.children.get(listName);
			}
		}

		ListChangeEvent event = null;

		if (targets != null) {
			for (Iterator stream = targets.iterator(); stream.hasNext(); ) {
				ListChangeListener target = (ListChangeListener) stream.next();
				boolean stillListening;
				synchronized (this) {
					stillListening = listChangeListeners.contains(target);
				}
				if (stillListening) {
					if (event == null) {
						// here's the reason for the duplicate code...
						event = new ListChangeEvent(this.source, listName, index, removedItems);
					}
					this.notifier().itemsRemoved(target, event);
				}
			}
		}
		if (child != null) {
			if (event == null) {
				child.fireItemsRemoved(listName, index, removedItems);
			} else {
				child.fireItemsRemoved(event);
			}
		}
		
		this.sourceChanged(listName);
	}

	/**
	 * Report a bound list update to any registered listeners.
	 */
	public void fireItemRemoved(String listName, int index, Object removedItem) {
//		this.fireItemsRemoved(listName, index, Collections.singletonList(removedItem));

		IdentityHashBag listChangeListeners = null;
		IdentityHashBag targets = null;
		ChangeSupport child = null;

		synchronized (this) {
			if (this.listeners != null) {
				listChangeListeners = (IdentityHashBag) this.listeners.get(ListChangeListener.class);
				if (listChangeListeners != null) {
					targets = (IdentityHashBag) listChangeListeners.clone();
				}
			}
			if ((listName != null) && (this.children != null)) {
				child = (ChangeSupport) this.children.get(listName);
			}
		}

		ListChangeEvent event = null;

		if (targets != null) {
			for (Iterator stream = targets.iterator(); stream.hasNext(); ) {
				ListChangeListener target = (ListChangeListener) stream.next();
				boolean stillListening;
				synchronized (this) {
					stillListening = listChangeListeners.contains(target);
				}
				if (stillListening) {
					if (event == null) {
						// here's the reason for the duplicate code...
						event = new ListChangeEvent(this.source, listName, index, Collections.singletonList(removedItem));
					}
					this.notifier().itemsRemoved(target, event);
				}
			}
		}
		if (child != null) {
			if (event == null) {
				child.fireItemRemoved(listName, index, removedItem);
			} else {
				child.fireItemsRemoved(event);
			}
		}
		
		this.sourceChanged(listName);
	}

	/**
	 * Report a bound list update to any registered listeners.
	 */
	public void fireItemsReplaced(ListChangeEvent event) {
		if (event.size() == 0) {
			return;
		}

		String listName = event.getListName();	

		IdentityHashBag listChangeListeners = null;
		IdentityHashBag targets = null;
		ChangeSupport child = null;

		synchronized (this) {
			if (this.listeners != null) {
				listChangeListeners = (IdentityHashBag) this.listeners.get(ListChangeListener.class);
				if (listChangeListeners != null) {
					targets = (IdentityHashBag) listChangeListeners.clone();
				}
			}
			if ((listName != null) && (this.children != null)) {
				child = (ChangeSupport) this.children.get(listName);
			}
		}

		if (targets != null) {
			for (Iterator stream = targets.iterator(); stream.hasNext(); ) {
				ListChangeListener target = (ListChangeListener) stream.next();
				boolean stillListening;
				synchronized (this) {
					stillListening = listChangeListeners.contains(target);
				}
				if (stillListening) {
					this.notifier().itemsReplaced(target, event);
				}
			}
		}
		if (child != null) {
			child.fireItemsReplaced(event);
		}
		
		this.sourceChanged(listName);
	}

	/**
	 * Report a bound list update to any registered listeners.
	 */
	public void fireItemsReplaced(String listName, int index, List newItems, List replacedItems) {
//		this.fireItemsReplaced(new ListChangeEvent(this.source, listName, index, newItems, replacedItems));
		if (newItems.size() == 0) {
			return;
		}

		IdentityHashBag listChangeListeners = null;
		IdentityHashBag targets = null;
		ChangeSupport child = null;

		synchronized (this) {
			if (this.listeners != null) {
				listChangeListeners = (IdentityHashBag) this.listeners.get(ListChangeListener.class);
				if (listChangeListeners != null) {
					targets = (IdentityHashBag) listChangeListeners.clone();
				}
			}
			if ((listName != null) && (this.children != null)) {
				child = (ChangeSupport) this.children.get(listName);
			}
		}

		ListChangeEvent event = null;

		if (targets != null) {
			for (Iterator stream = targets.iterator(); stream.hasNext(); ) {
				ListChangeListener target = (ListChangeListener) stream.next();
				boolean stillListening;
				synchronized (this) {
					stillListening = listChangeListeners.contains(target);
				}
				if (stillListening) {
					if (event == null) {
						// here's the reason for the duplicate code...
						event = new ListChangeEvent(this.source, listName, index, newItems, replacedItems);
					}
					this.notifier().itemsReplaced(target, event);
				}
			}
		}
		if (child != null) {
			if (event == null) {
				child.fireItemsReplaced(listName, index, newItems, replacedItems);
			} else {
				child.fireItemsReplaced(event);
			}
		}
		
		this.sourceChanged(listName);
	}

	/**
	 * Report a bound list update to any registered listeners.
	 */
	public void fireItemReplaced(String listName, int index, Object newItem, Object replacedItem) {
//		this.fireItemsReplaced(listName, index, Collections.singletonList(newItem), Collections.singletonList(replacedItem));

		IdentityHashBag listChangeListeners = null;
		IdentityHashBag targets = null;
		ChangeSupport child = null;

		synchronized (this) {
			if (this.listeners != null) {
				listChangeListeners = (IdentityHashBag) this.listeners.get(ListChangeListener.class);
				if (listChangeListeners != null) {
					targets = (IdentityHashBag) listChangeListeners.clone();
				}
			}
			if ((listName != null) && (this.children != null)) {
				child = (ChangeSupport) this.children.get(listName);
			}
		}

		ListChangeEvent event = null;

		if (targets != null) {
			for (Iterator stream = targets.iterator(); stream.hasNext(); ) {
				ListChangeListener target = (ListChangeListener) stream.next();
				boolean stillListening;
				synchronized (this) {
					stillListening = listChangeListeners.contains(target);
				}
				if (stillListening) {
					if (event == null) {
						// here's the reason for the duplicate code...
						event = new ListChangeEvent(this.source, listName, index, Collections.singletonList(newItem), Collections.singletonList(replacedItem));
					}
					this.notifier().itemsReplaced(target, event);
				}
			}
		}
		if (child != null) {
			if (event == null) {
				child.fireItemReplaced(listName, index, newItem, replacedItem);
			} else {
				child.fireItemsReplaced(event);
			}
		}
		
		this.sourceChanged(listName);
	}

	/**
	 * Report a bound list update to any registered listeners.
	 */
	public void fireListChanged(ListChangeEvent event) {
		String listName = event.getListName();	

		IdentityHashBag listChangeListeners = null;
		IdentityHashBag targets = null;
		ChangeSupport child = null;

		synchronized (this) {
			if (this.listeners != null) {
				listChangeListeners = (IdentityHashBag) this.listeners.get(ListChangeListener.class);
				if (listChangeListeners != null) {
					targets = (IdentityHashBag) listChangeListeners.clone();
				}
			}
			if ((listName != null) && (this.children != null)) {
				child = (ChangeSupport) this.children.get(listName);
			}
		}

		if (targets != null) {
			for (Iterator stream = targets.iterator(); stream.hasNext(); ) {
				ListChangeListener target = (ListChangeListener) stream.next();
				boolean stillListening;
				synchronized (this) {
					stillListening = listChangeListeners.contains(target);
				}
				if (stillListening) {
					this.notifier().listChanged(target, event);
				}
			}
		}
		if (child != null) {
			child.fireListChanged(event);
		}

		this.sourceChanged(listName);
	}

	/**
	 * Report a bound list update to any registered listeners.
	 */
	public void fireListChanged(String listName) {
//		this.fireListChanged(new ListChangeEvent(this.source, listName));

		IdentityHashBag listChangeListeners = null;
		IdentityHashBag targets = null;
		ChangeSupport child = null;

		synchronized (this) {
			if (this.listeners != null) {
				listChangeListeners = (IdentityHashBag) this.listeners.get(ListChangeListener.class);
				if (listChangeListeners != null) {
					targets = (IdentityHashBag) listChangeListeners.clone();
				}
			}
			if ((listName != null) && (this.children != null)) {
				child = (ChangeSupport) this.children.get(listName);
			}
		}

		ListChangeEvent event = null;

		if (targets != null) {
			for (Iterator stream = targets.iterator(); stream.hasNext(); ) {
				ListChangeListener target = (ListChangeListener) stream.next();
				boolean stillListening;
				synchronized (this) {
					stillListening = listChangeListeners.contains(target);
				}
				if (stillListening) {
					if (event == null) {
						// here's the reason for the duplicate code...
						event = new ListChangeEvent(this.source, listName);
					}
					this.notifier().listChanged(target, event);
				}
			}
		}
		if (child != null) {
			if (event == null) {
				child.fireListChanged(listName);
			} else {
				child.fireListChanged(event);
			}
		}
		
		this.sourceChanged(listName);
	}


	// ******************** tree change support ********************
		
	/**
	 * Add a tree change listener that is registered for all trees.
	 */
	public void addTreeChangeListener(TreeChangeListener listener) {
		this.addListener(TreeChangeListener.class, listener);
	}
		
	/**
	 * Add a tree change listener for the specified tree. The listener
	 * will be notified only for changes to the specified tree.
	 */
	public void addTreeChangeListener(String treeName, TreeChangeListener listener) {
		this.addListener(treeName, TreeChangeListener.class, listener);
	}
		
	/**
	 * Remove a tree change listener that was registered for all tree.
	 */
	public void removeTreeChangeListener(TreeChangeListener listener) {
		this.removeListener(TreeChangeListener.class, listener);
	}
	
	/**
	 * Remove a tree change listener that was registered for a specific tree.
	 */
	public void removeTreeChangeListener(String treeName, TreeChangeListener listener) {
		this.removeListener(treeName, TreeChangeListener.class, listener);
	}
	
	/**
	 * Return whether there are any tree change listeners that will
	 * be notified when the specified tree has changed.
	 */
	public boolean hasAnyTreeChangeListeners(String treeName) {
		return this.hasAnyListeners(TreeChangeListener.class, treeName);
	}
	
	/**
	 * Return whether there are any tree change listeners that will
	 * be notified when any tree has changed.
	 */
	public boolean hasAnyTreeChangeListeners() {
		return this.hasAnyListeners(TreeChangeListener.class);
	}
	
	/**
	 * Report a bound tree update to any registered listeners.
	 */
	public void fireNodeAdded(TreeChangeEvent event) {
		String treeName = event.getTreeName();	

		IdentityHashBag treeChangeListeners = null;
		IdentityHashBag targets = null;
		ChangeSupport child = null;
		
		synchronized (this) {
			if (this.listeners != null) {
				treeChangeListeners = (IdentityHashBag) this.listeners.get(TreeChangeListener.class);
				if (treeChangeListeners != null) {
					targets = (IdentityHashBag) treeChangeListeners.clone();
				}
			}
			if ((treeName != null) && (this.children != null)) {
				child = (ChangeSupport) this.children.get(treeName);
			}
		}
		
		if (targets != null) {
			for (Iterator stream = targets.iterator(); stream.hasNext(); ) {
				TreeChangeListener target = (TreeChangeListener) stream.next();
				boolean stillListening;
				synchronized (this) {
					stillListening = treeChangeListeners.contains(target);
				}
				if (stillListening) {
					this.notifier().nodeAdded(target, event);
				}
			}
		}
		if (child != null) {
			child.fireNodeAdded(event);
		}
		
		this.sourceChanged(treeName);
	}
	
	/**
	 * Report a bound tree update to any registered listeners.
	 */
	public void fireNodeAdded(String treeName, Object[] path) {
//		this.fireNodeAdded(new TreeChangeEvent(this.source, treeName, path));

		IdentityHashBag treeChangeListeners = null;
		IdentityHashBag targets = null;
		ChangeSupport child = null;
		
		synchronized (this) {
			if (this.listeners != null) {
				treeChangeListeners = (IdentityHashBag) this.listeners.get(TreeChangeListener.class);
				if (treeChangeListeners != null) {
					targets = (IdentityHashBag) treeChangeListeners.clone();
				}
			}
			if ((treeName != null) && (this.children != null)) {
				child = (ChangeSupport) this.children.get(treeName);
			}
		}

		TreeChangeEvent event = null;
		
		if (targets != null) {
			for (Iterator stream = targets.iterator(); stream.hasNext(); ) {
				TreeChangeListener target = (TreeChangeListener) stream.next();
				boolean stillListening;
				synchronized (this) {
					stillListening = treeChangeListeners.contains(target);
				}
				if (stillListening) {
					if (event == null) {
						// here's the reason for the duplicate code...
						event = new TreeChangeEvent(this.source, treeName, path);
					}
					this.notifier().nodeAdded(target, event);
				}
			}
		}
		if (child != null) {
			if (event == null) {
				child.fireNodeAdded(treeName, path);
			} else {
				child.fireNodeAdded(event);
			}
		}
		
		this.sourceChanged(treeName);
	}
	
	/**
	 * Report a bound tree update to any registered listeners.
	 */
	public void fireNodeRemoved(TreeChangeEvent event) {
		String treeName = event.getTreeName();	

		IdentityHashBag treeChangeListeners = null;
		IdentityHashBag targets = null;
		ChangeSupport child = null;
		
		synchronized (this) {
			if (this.listeners != null) {
				treeChangeListeners = (IdentityHashBag) this.listeners.get(TreeChangeListener.class);
				if (treeChangeListeners != null) {
					targets = (IdentityHashBag) treeChangeListeners.clone();
				}
			}
			if ((treeName != null) && (this.children != null)) {
				child = (ChangeSupport) this.children.get(treeName);
			}
		}
		
		if (targets != null) {
			for (Iterator stream = targets.iterator(); stream.hasNext(); ) {
				TreeChangeListener target = (TreeChangeListener) stream.next();
				boolean stillListening;
				synchronized (this) {
					stillListening = treeChangeListeners.contains(target);
				}
				if (stillListening) {
					this.notifier().nodeRemoved(target, event);
				}
			}
		}
		if (child != null) {
			child.fireNodeRemoved(event);
		}
		
		this.sourceChanged(treeName);
	}
	
	/**
	 * Report a bound tree update to any registered listeners.
	 */
	public void fireNodeRemoved(String treeName, Object[] path) {
//		this.fireNodeRemoved(new TreeChangeEvent(this.source, treeName, path));

		IdentityHashBag treeChangeListeners = null;
		IdentityHashBag targets = null;
		ChangeSupport child = null;
		
		synchronized (this) {
			if (this.listeners != null) {
				treeChangeListeners = (IdentityHashBag) this.listeners.get(TreeChangeListener.class);
				if (treeChangeListeners != null) {
					targets = (IdentityHashBag) treeChangeListeners.clone();
				}
			}
			if ((treeName != null) && (this.children != null)) {
				child = (ChangeSupport) this.children.get(treeName);
			}
		}

		TreeChangeEvent event = null;
		
		if (targets != null) {
			for (Iterator stream = targets.iterator(); stream.hasNext(); ) {
				TreeChangeListener target = (TreeChangeListener) stream.next();
				boolean stillListening;
				synchronized (this) {
					stillListening = treeChangeListeners.contains(target);
				}
				if (stillListening) {
					if (event == null) {
						// here's the reason for the duplicate code...
						event = new TreeChangeEvent(this.source, treeName, path);
					}
					this.notifier().nodeRemoved(target, event);
				}
			}
		}
		if (child != null) {
			if (event == null) {
				child.fireNodeRemoved(treeName, path);
			} else {
				child.fireNodeRemoved(event);
			}
		}
		
		this.sourceChanged(treeName);
	}
	
	/**
	 * Report a bound tree update to any registered listeners.
	 */
	public void fireTreeChanged(TreeChangeEvent event) {
		String treeName = event.getTreeName();	

		IdentityHashBag treeChangeListeners = null;
		IdentityHashBag targets = null;
		ChangeSupport child = null;
		
		synchronized (this) {
			if (this.listeners != null) {
				treeChangeListeners = (IdentityHashBag) this.listeners.get(TreeChangeListener.class);
				if (treeChangeListeners != null) {
					targets = (IdentityHashBag) treeChangeListeners.clone();
				}
			}
			if ((treeName != null) && (this.children != null)) {
				child = (ChangeSupport) this.children.get(treeName);
			}
		}
		
		if (targets != null) {
			for (Iterator stream = targets.iterator(); stream.hasNext(); ) {
				TreeChangeListener target = (TreeChangeListener) stream.next();
				boolean stillListening;
				synchronized (this) {
					stillListening = treeChangeListeners.contains(target);
				}
				if (stillListening) {
					this.notifier().treeChanged(target, event);
				}
			}
		}
		if (child != null) {
			child.fireTreeChanged(event);
		}
		
		this.sourceChanged(treeName);
	}
	
	/**
	 * Report a bound tree update to any registered listeners.
	 */
	public void fireTreeChanged(String treeName, Object[] path) {
//		this.fireTreeChanged(new TreeChangeEvent(this.source, treeName, path));

		IdentityHashBag treeChangeListeners = null;
		IdentityHashBag targets = null;
		ChangeSupport child = null;
		
		synchronized (this) {
			if (this.listeners != null) {
				treeChangeListeners = (IdentityHashBag) this.listeners.get(TreeChangeListener.class);
				if (treeChangeListeners != null) {
					targets = (IdentityHashBag) treeChangeListeners.clone();
				}
			}
			if ((treeName != null) && (this.children != null)) {
				child = (ChangeSupport) this.children.get(treeName);
			}
		}

		TreeChangeEvent event = null;
		
		if (targets != null) {
			for (Iterator stream = targets.iterator(); stream.hasNext(); ) {
				TreeChangeListener target = (TreeChangeListener) stream.next();
				boolean stillListening;
				synchronized (this) {
					stillListening = treeChangeListeners.contains(target);
				}
				if (stillListening) {
					if (event == null) {
						// here's the reason for the duplicate code...
						event = new TreeChangeEvent(this.source, treeName, path);
					}
					this.notifier().treeChanged(target, event);
				}
			}
		}
		if (child != null) {
			if (event == null) {
				child.fireTreeChanged(treeName, path);
			} else {
				child.fireTreeChanged(event);
			}
		}
		
		this.sourceChanged(treeName);
	}
	
	/**
	 * Report a bound tree update to any registered listeners.
	 */
	public void fireTreeChanged(String treeName) {
		this.fireTreeChanged(treeName, null);
	}
	

	// ******************** standard methods ********************

	public String toString() {
		return StringTools.buildToStringFor(this, this.source);
	}

}
