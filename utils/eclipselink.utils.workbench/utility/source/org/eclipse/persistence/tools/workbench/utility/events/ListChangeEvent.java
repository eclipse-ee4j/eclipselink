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
package org.eclipse.persistence.tools.workbench.utility.events;

import java.util.EventObject;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.persistence.tools.workbench.utility.iterators.NullListIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.ReadOnlyListIterator;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * A "ListChange" event gets delivered whenever a model changes a "bound"
 * or "constrained" list. A ListChangeEvent object is sent as an
 * argument to the ListChangeListener.
 * 
 * Normally a ListChangeEvent is accompanied by the list name,
 * the items that were added to or removed from the changed list,
 * and the index of where the items are or were in the list.
 * 
 * Design options:
 * - create a list to wrap a single added or removed item
 * 	(this is the option we implemented below and in collaborating code)
 * 	since there is no way to optimize downstream code for
 * 	single items, we take another performance hit by building
 * 	a list each time  (@see Collections.singletonList(Object))
 * 	and forcing downstream code to use a list iterator every time
 * 
 * - create a separate event for each item added or removed
 * 	eliminates any potential for optimizations to downstream code
 * 
 * - add protocol to support both single items and collections
 * 	adds conditional logic to downstream code
 */
public class ListChangeEvent extends EventObject {

	/** Name of the list that changed. May be null, if not known. */
	private String listName;

	/** The index at which the items were added or removed. May be -1, if not known. */
	private int index = -1;

	/**
	 * The items that were added to or removed from the list. In the case of "replaced"
	 * items, these are the new items in the list
	 * May be null, if not known.
	 */
	private List items;

	/**
	 * The items in the list that were replaced by the items listed above,
	 * in #items. May be null, if not known.
	 */
	private List replacedItems;

	private static final long serialVersionUID = 1L;


	/**
	 * Construct a new ListChangeEvent.
	 *
	 * @param source The object on which the Event initially occurred.
	 */
	public ListChangeEvent(Object source) {
		super(source);
	}
	
	/**
	 * Construct a new ListChangeEvent.
	 *
	 * @param source The object on which the Event initially occurred.
	 * @param listName The programmatic name of the list that was changed.
	 */
	public ListChangeEvent(Object source, String listName) {
		this(source);
		this.listName = listName;
	}
	
	/**
	 * Construct a new ListChangeEvent for a list of added or removed items.
	 *
	 * @param source The object on which the Event initially occurred.
	 * @param listName The programmatic name of the list that was changed.
	 * @param index The index at which the items were added to or removed from the list.
	 * @param items The items that were added to or removed from the list.
	 */
	public ListChangeEvent(Object source, String listName, int index, List items) {
		this(source, listName);
		this.index = index;
		this.items = items;
	}
	
	/**
	 * Construct a new ListChangeEvent for a list of replaced items.
	 *
	 * @param source The object on which the Event initially occurred.
	 * @param listName The programmatic name of the list that was changed.
	 * @param index The index at which the items in the list were replaced.
	 * @param items The new items in the list.
	 * @param replacedItems The items in the list that were replaced.
	 */
	public ListChangeEvent(Object source, String listName, int index, List items, List replacedItems) {
		this(source, listName, index, items);
		this.replacedItems = replacedItems;
	}
	
	/**
	 * Return the programmatic name of the list that was changed.
	 */
	public String getListName() {
		return this.listName;
	}

	/**
	 * Return the index at which the items were added to or removed from the list.
	 * May be -1 if inappropriate or unknown.
	 */
	public int getIndex() {
		return this.index;
	}
	
	/**
	 * Return a list iterator on the items that were added to or
	 * removed from the list. In the case of "replaced" items, these
	 * are the new items in the list.
	 * May be empty if inappropriate or unknown.
	 */
	public ListIterator items() {
		if (this.items == null) {
			return NullListIterator.instance();
		}
		return new ReadOnlyListIterator(this.items);
	}
	
	/**
	 * Return the number of items that were added to or
	 * removed from the list.
	 * May be 0 if inappropriate or unknown.
	 */
	public int size() {
		if (this.items == null) {
			return 0;
		}
		return this.items.size();
	}

	/**
	 * Return a list iterator on the items in the list that were replaced.
	 * May be empty if inappropriate or unknown.
	 */
	public ListIterator replacedItems() {
		if (this.replacedItems == null) {
			return NullListIterator.instance();
		}
		return new ReadOnlyListIterator(this.replacedItems);
	}
	
	/**
	 * Return a copy of the event with the specified source
	 * replacing the current source.
	 */
	public ListChangeEvent cloneWithSource(Object newSource) {
		return new ListChangeEvent(newSource, this.listName, this.index, this.items, this.replacedItems);
	}

	/**
	 * Return a copy of the event with the specified source
	 * replacing the current source and list name.
	 */
	public ListChangeEvent cloneWithSource(Object newSource, String newListName) {
		return new ListChangeEvent(newSource, newListName, this.index, this.items, this.replacedItems);
	}

	/**
	 * Return a copy of the event with the specified source
	 * replacing the current source and list name and displacing
	 * the index by the specified amount.
	 */
	public ListChangeEvent cloneWithSource(Object newSource, String newListName, int offset) {
		return new ListChangeEvent(newSource, newListName, this.index + offset, this.items, this.replacedItems);
	}

	public String toString() {
		return StringTools.buildToStringFor(this, this.listName);
	}

}
