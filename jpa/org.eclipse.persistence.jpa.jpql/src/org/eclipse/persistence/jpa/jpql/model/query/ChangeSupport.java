/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.persistence.jpa.jpql.model.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.persistence.jpa.jpql.Assert;
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.model.IListChangeEvent;
import org.eclipse.persistence.jpa.jpql.model.IListChangeEvent.EventType;
import org.eclipse.persistence.jpa.jpql.model.IListChangeListener;
import org.eclipse.persistence.jpa.jpql.model.IPropertyChangeListener;
import org.eclipse.persistence.jpa.jpql.model.ListChangeEvent;
import org.eclipse.persistence.jpa.jpql.model.PropertyChangeEvent;
import org.eclipse.persistence.jpa.jpql.util.iterator.CloneListIterator;
import org.eclipse.persistence.jpa.jpql.util.iterator.IterableListIterator;

/**
 * This <code>ChangeSupport</code> is responsible to notifies registered listeners upon changes made
 * to a {@link StateObject}, those changes are either a property has changed ({@link IPropertyChangeListener})
 * or the content of a list has changed ({@link IListChangeListener}).
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public class ChangeSupport {

	/**
	 * The list of registered {@link IListChangeListener listeners}.
	 */
	private Map<String, List<IListChangeListener<?>>> listChangeListeners;

	/**
	 * The list of registered {@link IPropertyChangeListener listeners}.
	 */
	private Map<String, List<IPropertyChangeListener<?>>> propertyChangeListeners;

	/**
	 * The object for which this object will take care of notifying the listeners upon changes made
	 * to the object's internal state.
	 */
	private StateObject source;

	/**
	 * Creates a new <code>ChangeSupport</code>.
	 *
	 * @param source The object for which this object will take care of notifying the listeners upon
	 * changes made to the object's internal state
	 * @exception NullPointerException The source {@link StateObject} cannot be <code>null</code>
	 */
	public ChangeSupport(StateObject source) {
		super();
		initialize(source);
	}

	/**
	 * Adds the given item as a child to the given list.
	 *
	 * @param source The {@link ListHolderStateObject} from where the change is coming
	 * @param items The list of  to which the child is added
	 * @param listName The name associated with the list
	 * @param item The child to become a child of this one
	 * @param <T> The type of the items
	 */
	public <T> void addItem(ListHolderStateObject<T> source,
	                        List<T> items,
	                        String listName,
	                        T item) {

		addItems(source, items, listName, Collections.singletonList(item));
	}

	/**
	 * Adds the given items as children to the given list.
	 *
	 * @param source The {@link ListHolderStateObject} from where the change is coming
	 * @param list The list of items to which the child is added
	 * @param listName The name associated with the list
	 * @param items The child to become children of this one
	 * @param <T> The type of the items
	 */
	public <T> void addItems(ListHolderStateObject<T> source,
	                         List<T> list,
	                         String listName,
	                         List<? extends T> items) {

		List<T> original = new ArrayList<T>(list);
		int index = list.size();
		list.addAll(index, items);

		if (hasListChangeListeners(listName)) {

			IListChangeEvent<T> event = new ListChangeEvent<T>(
				source,
				original,
				EventType.ADDED,
				listName,
				new ArrayList<T>(items),
				index,
				index
			);

			fireListChangeEvent(event);
		}
	}

	/**
	 * Registers the given {@link IListChangeListener} for the specified list. The listener will be
	 * notified only when items are added, removed, moved from the list.
	 *
	 * @param listName The name of the list for which the listener will be notified when the content
	 * of the list has changed
	 * @param listener The listener to be notified upon changes
	 * @exception NullPointerException {@link IListChangeListener} cannot be <code>null</code>
	 * @exception IllegalArgumentException The listener is already registered with the list name
	 */
	public void addListChangeListener(String listName, IListChangeListener<?> listener) {
		addListener(listChangeListeners, IListChangeListener.class, listName, listener);
	}

	/**
	 * Registers the given list for the specified name. The listener will be notified upon changes.
	 *
	 * @param listeners The list of listeners from which the given listener is added
	 * @param listenerType The type of the listener, which is only used in the exception's message
	 * @param name The name of the event for which the listener is registered
	 * @param listener The listener to register
	 * @exception NullPointerException {@link IPropertyChangeListener} cannot be <code>null</code>
	 * @exception IllegalArgumentException The listener is already registered with the given name
	 */
	protected <T> void addListener(Map<String, List<T>> listeners,
	                               Class<?> listenerType,
	                               String name,
	                               T listener) {

		if (listener == null) {
			throw new NullPointerException(listenerType.getSimpleName() + " cannot be null");
		}

		List<T> listenerList = listeners.get(name);

		if (listenerList == null) {
			listenerList = new ArrayList<T>();
			listeners.put(name, listenerList);
		}

		if (listenerList.contains(listener)) {
			throw new IllegalArgumentException(listenerType.getSimpleName() + " is already registered");
		}

		listenerList.add(listener);
	}

	/**
	 * Registers the given {@link IPropertyChangeListener} for the specified property. The listener
	 * will be notified only for changes to the specified property.
	 *
	 * @param propertyName The name of the property for which the listener was registered
	 * @param listener The listener to be notified upon changes
	 * @exception NullPointerException {@link IPropertyChangeListener} cannot be <code>null</code>
	 * @exception IllegalArgumentException The listener is already registered with the property name
	 */
	public void addPropertyChangeListener(String propertyName, IPropertyChangeListener<?> listener) {
		addListener(propertyChangeListeners, IPropertyChangeListener.class, propertyName, listener);
	}

	/**
	 * Determines whether the given item can be moved down by one position in the list owned by its
	 * parent.
	 *
	 * @param list The list used to determine if the given item can be moved down in that list
	 * @param stateObject The item that could potentially be moved down
	 * @return <code>true</code> if the object can be moved down by one unit; <code>false</code>
	 * otherwise
	 */
	public <T> boolean canMoveDown(List<T> list, T stateObject) {
		int index = list.indexOf(stateObject);
		return (index > -1) && (index + 1 < list.size());
	}

	/**
	 * Determines whether the given item can be moved up by one position in the list owned by its
	 * parent.
	 *
	 * @param list The list used to determine if the given item can be moved up in that list
	 * @param item The item that could potentially be moved up
	 * @return <code>true</code> if the object can be moved up by one unit; <code>false</code>
	 * otherwise
	 */
	public <T> boolean canMoveUp(List<T> list, T item) {
		int index = list.indexOf(item);
		return (index > 0);
	}

	protected <T> void fireListChangeEvent(IListChangeEvent<T> event) {

		for (IListChangeListener<T> listener : this.<T>listChangeListeners(event.getListName())) {
			try {
				listener.itemsRemoved(event);
			}
			catch (Exception e) {
				// TODO: Log event
			}
		}
	}

	/**
	 * Notifies the {@link IPropertyChangeListener IPropertyChangeListeners} that have been registered
	 * with the given property name that the property has changed.
	 *
	 * @param propertyName The name of the property associated with the property change
	 * @param oldValue The old value of the property that changed
	 * @param newValue The new value of the property that changed
	 */
	@SuppressWarnings("unchecked")
	public void firePropertyChanged(String propertyName, Object oldValue, Object newValue) {

		if (ExpressionTools.valuesAreDifferent(oldValue, newValue) &&
		    hasPropertyChangeListeners(propertyName)) {

			PropertyChangeEvent<Object> event = new PropertyChangeEvent<Object>(source, propertyName, oldValue, newValue);

			for (IPropertyChangeListener<?> listener : propertyChangeListeners(propertyName)) {
				try {
					((IPropertyChangeListener<Object>) listener).propertyChanged(event);
				}
				catch (Exception e) {
					// TODO: Log event
				}
			}
		}
	}

	/**
	 * Determines whether there are at least one {@link IListChangeListener} registered to listen for
	 * changes made to the list with the given list name.
	 *
	 * @param listName The name of the list to check if it has registered listeners
	 * @return <code>true</code> if listeners have been registered for the given list name;
	 * <code>false</code> otherwise
	 */
	public boolean hasListChangeListeners(String listName) {
		return hasListeners(listChangeListeners, listName);
	}

	/**
	 * Determines whether there are at least one listener registered.
	 *
	 * @param name The name of the property or list to check if it has registered listeners
	 * @return <code>true</code> if listeners have been registered for the given name;
	 *  <code>false</code> otherwise
	 */
	protected boolean hasListeners(Map<String, ?> listeners, String name) {
		return listeners.containsKey(name);
	}

	/**
	 * Determines whether there are at least one {@link IPropertyChangeListener} registered to listen
	 * for changes made to the property with the given property name.
	 *
	 * @param propertyName The name of the property to check if it has registered listeners
	 * @return <code>true</code> if listeners have been registered for the given property name;
	 * <code>false</code> otherwise
	 */
	public boolean hasPropertyChangeListeners(String propertyName) {
		return hasListeners(propertyChangeListeners, propertyName);
	}

	/**
	 * Initializes this <code>ChangeSupport</code>.
	 *
	 * @param source The object for which this object will take care of notifying the listeners upon
	 * changes made to the object's internal state
	 * @exception NullPointerException The source {@link StateObject} cannot be <code>null</code>
	 */
	protected void initialize(StateObject source) {

		Assert.isNotNull(source, "The source StateObject cannot be null");

		this.source                  = source;
		this.listChangeListeners     = new HashMap<String, List<IListChangeListener<?>>>();
		this.propertyChangeListeners = new HashMap<String, List<IPropertyChangeListener<?>>>();
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	protected <T> IterableListIterator<IListChangeListener<T>> listChangeListeners(String listName) {
		return new CloneListIterator(
			listChangeListeners.get(listName)
		);
	}

	/**
	 * Moves the given {@link StateObject} down by one position in the list owned by its parent.
	 *
	 * @param source The {@link ListHolderStateObject} from where the change is coming
	 * @param items The list of items to which the child is moved down
	 * @param listName The name associated with the list
	 * @param item The child to move down within the list
	 * @param <T> The type of the items
	 */
	public <T> void moveDown(ListHolderStateObject<T> source,
	                         List<T> items,
	                         String listName,
	                         T item) {

		int index = items.indexOf(item);
		moveItem(source, items, EventType.MOVED_DOWN, listName, item, index + 1, index);
	}

	/**
	 * Moves the given item from its current position to a new position in the list owned by its parent.
	 *
	 * @param source The {@link ListHolderStateObject} from where the change is coming
	 * @param items The list of items to which the child is moved
	 * @param eventType The type describing how the item was moved (up or down)
	 * @param listName The name associated with the list
	 * @param item The child to move within the list
	 * @param oldIndex The current position of the item to move
	 * @param newIndex The new position within the list
	 * @param <T> The type of the items
	 */
	protected <T> void moveItem(ListHolderStateObject<T> source,
	                            List<T> items,
	                            EventType eventType,
	                            String listName,
	                            T item,
	                            int oldIndex,
	                            int newIndex) {

		List<T> original = new ArrayList<T>(items);
		items.remove(oldIndex);
		items.add(newIndex, item);

		if (hasListChangeListeners(listName)) {

			IListChangeEvent<T> event = new ListChangeEvent<T>(
				source,
				original,
				eventType,
				listName,
				Collections.singletonList(item),
				newIndex,
				oldIndex
			);

			fireListChangeEvent(event);
		}
	}

	/**
	 * Moves the given item up by one position in the list owned by its parent.
	 *
	 * @param source The {@link ListHolderStateObject} from where the change is coming
	 * @param items The list of items to which the child is moved up
	 * @param listName The name associated with the list
	 * @param item The child to move up within the list
	 * @param <T> The type of the items
	 */
	public <T> void moveUp(ListHolderStateObject<T> source,
	                       List<T> items,
	                       String listName,
	                       T item) {

		int index = items.indexOf(item);
		moveItem(source, items, EventType.MOVED_DOWN, listName, item, index - 1, index);
	}

	protected IterableListIterator<IPropertyChangeListener<?>> propertyChangeListeners(String propertyName) {
		return new CloneListIterator<IPropertyChangeListener<?>>(
			propertyChangeListeners.get(propertyName)
		);
	}

	/**
	 * Removes the given item from the list of children.
	 *
	 * @param source The {@link ListHolderStateObject} from where the change is coming
	 * @param items The list of item to which the child is removed
	 * @param listName The name associated with the list
	 * @param item The child to removed from the list
	 * @param <T> The type of the items
	 */
	public <T> void removeItem(ListHolderStateObject<T> source,
	                           List<T> items,
	                           String listName,
	                           T item) {

		List<T> original = new ArrayList<T>(items);
		int index = items.indexOf(item);
		items.remove(index);

		if (hasListChangeListeners(listName)) {

			IListChangeEvent<T> event = new ListChangeEvent<T>(
				source,
				original,
				EventType.REMOVED,
				listName,
				Collections.singletonList(item),
				index,
				index
			);

			fireListChangeEvent(event);
		}
	}

	/**
	 * Removes the given items from the list of children.
	 *
	 * @param source The {@link ListHolderStateObject} from where the change is coming
	 * @param list The list of items to which the child is removed
	 * @param listName The name associated with the list
	 * @param items The items to removed from the list
	 * @param <T> The type of the items
	 */
	public <T> void removeItems(ListHolderStateObject<T> source,
	                            List<? extends T> list,
	                            String listName,
	                            Collection<? extends T> items) {

		List<T> original = new ArrayList<T>(list);
		list.removeAll(items);

		if (hasListChangeListeners(listName)) {

			IListChangeEvent<T> event = new ListChangeEvent<T>(
				source,
				original,
				EventType.REMOVED,
				listName,
				new ArrayList<T>(items),
				-1,
				-1
			);

			fireListChangeEvent(event);
		}
	}

	/**
	 * Unregisters the given {@link IListChangeListener} that was registered for the specified list.
	 * The listener will no longer be notified only when items are added, removed, moved from the
	 * list.
	 *
	 * @param listName The name of the list for which the listener was registered
	 * @param listener The listener to unregister
	 * @exception NullPointerException {@link IListChangeListener} cannot be <code>null</code>
	 * @exception IllegalArgumentException The listener was never registered with the list name
	 */
	public void removeListChangeListener(String listName, IListChangeListener<?> listener) {
		removeListener(listChangeListeners, IListChangeListener.class, listName, listener);
	}

	/**
	 * Unregisters the given listener that was registered for the specified name. The listener will
	 * no longer be notified upon changes.
	 *
	 * @param listeners The list of listeners from which the given listener is removed
	 * @param listenerType The type of the listener, which is only used in the exception's message
	 * @param name The name of the event for which the listener was registered
	 * @param listener The listener to unregister
	 * @exception NullPointerException {@link IPropertyChangeListener} cannot be <code>null</code>
	 * @exception IllegalArgumentException The listener was never registered with the given name
	 */
	protected <T> void removeListener(Map<String, List<T>> listeners,
	                                  Class<?> listenerType,
	                                  String name,
	                                  T listener) {

		if (listener == null) {
			throw new NullPointerException(listenerType.getSimpleName() + " cannot be null");
		}

		List<T> listenerList = listeners.get(name);

		if (listenerList == null) {
			throw new IllegalArgumentException("No listeners were registered for " + name);
		}

		if (!listenerList.remove(listener)) {
			throw new IllegalArgumentException(listenerType.getSimpleName() + " was never registered");
		}

		if (listeners.isEmpty()) {
			listeners.remove(name);
		}
	}

	/**
	 * Unregisters the given {@link IPropertyChangeListener} that was registered for the specified
	 * property. The listener will no longer be notified when the property changes.
	 *
	 * @param propertyName The name of the property for which the listener was registered
	 * @param listener The listener to unregister
	 * @exception NullPointerException {@link IPropertyChangeListener} cannot be <code>null</code>
	 * @exception IllegalArgumentException The listener was never registered with the property name
	 */
	public void removePropertyChangeListener(String propertyName, IPropertyChangeListener<?> listener) {
		removeListener(propertyChangeListeners, IPropertyChangeListener.class, propertyName, listener);
	}

	/**
	 * Replaces the item at the given position by a new one.
	 *
	 * @param source The {@link ListHolderStateObject} from where the change is coming
	 * @param items The list of items to which a child is replaced
	 * @param listName The name associated with the list
	 * @param index The position of the item to replace
	 * @param item The item to replace the one at the given position
	 * @param <T> The type of the items
	 */
	public <T> void replaceItem(ListHolderStateObject<T> source,
	                            List<T> items,
	                            String listName,
	                            int index,
	                            T item) {

		List<T> original = new ArrayList<T>(items);
		items.set(index, item);

		if (hasListChangeListeners(listName)) {

			IListChangeEvent<T> event = new ListChangeEvent<T>(
				source,
				original,
				EventType.REPLACED,
				listName,
				items,
				index,
				index
			);

			fireListChangeEvent(event);
		}
	}

	/**
	 * Replaces the given list by removing any existing items and adding the items contained in the
	 * second list.
	 *
	 * @param source The {@link ListHolderStateObject} from where the change is coming
	 * @param items The list of items to which the child is removed
	 * @param listName The name associated with the list
	 * @param newItems The items to removed from the list
	 * @param <T> The type of the items
	 */
	public <T> void replaceItems(ListHolderStateObject<T> source,
                                List<T> items,
                                String listName,
                                List<T> newItems) {

		List<T> original = new ArrayList<T>(items);
		items.clear();
		items.addAll(newItems);

		if (hasListChangeListeners(listName)) {

			IListChangeEvent<T> event = new ListChangeEvent<T>(
				source,
				original,
				EventType.REPLACED,
				listName,
				items,
				-1,
				-1
			);

			fireListChangeEvent(event);
		}
	}
}