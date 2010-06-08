/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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

import java.util.Collection;
import java.util.EventObject;
import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.utility.iterators.NullIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.ReadOnlyIterator;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * A "CollectionChange" event gets delivered whenever a model changes a "bound"
 * or "constrained" collection. A CollectionChangeEvent object is sent as an
 * argument to the CollectionChangeListener.
 * 
 * Normally a CollectionChangeEvent is accompanied by the collection name and
 * the items that were added to or removed from the changed collection.
 * 
 * Design options:
 * - create a collection to wrap a single added or removed item
 * 	(this is the option we implemented below and in collaborating code)
 * 	since there is no way to optimize downstream code for
 * 	single items, we take another performance hit by building
 * 	a collection each time  (@see Collections.singleton(Object))
 * 	and forcing downstream code to use an iterator every time
 * 
 * - create a separate event for each item added or removed
 * 	eliminates any potential for optimizations to downstream code
 * 
 * - add protocol to support both single items and collections
 * 	adds conditional logic to downstream code
 */
public class CollectionChangeEvent extends EventObject {

	/** Name of the collection that changed. May be null, if not known. */
	private String collectionName;

	/** The items that were added to or removed from the collection. May be null, if not known. */
	private Collection items;

	private static final long serialVersionUID = 1L;


	/**
	 * Construct a new CollectionChangeEvent.
	 *
	 * @param source The object on which the Event initially occurred.
	 */
	public CollectionChangeEvent(Object source) {
		super(source);
	}

	/**
	 * Construct a new CollectionChangeEvent.
	 *
	 * @param source The object on which the Event initially occurred.
	 * @param collectionName The programmatic name of the collection that was changed.
	 */
	public CollectionChangeEvent(Object source, String collectionName) {
		this(source);
		this.collectionName = collectionName;
	}

	/**
	 * Construct a new CollectionChangeEvent.
	 *
	 * @param source The object on which the Event initially occurred.
	 * @param collectionName The programmatic name of the collection that was changed.
	 * @param items The items that were added to or removed from the collection.
	 */
	public CollectionChangeEvent(Object source, String collectionName, Collection items) {
		super(source);
		this.collectionName = collectionName;
		this.items = items;
	}

	/**
	 * Return the programmatic name of the collection that was changed.
	 * May be null, if not known.
	 */
	public String getCollectionName() {
		return this.collectionName;
	}

	/**
	 * Return an iterator on the items that were added to or
	 * removed from the collection.
	 * May be empty if inappropriate or unknown.
	 */
	public Iterator items() {
		if (this.items == null) {
			return NullIterator.instance();
		}
		return new ReadOnlyIterator(this.items);
	}

	/**
	 * Return the number of items that were added to or
	 * removed from the collection.
	 * May be 0 if inappropriate or unknown.
	 */
	public int size() {
		if (this.items == null) {
			return 0;
		}
		return this.items.size();
	}

	/**
	 * Return a copy of the event with the specified source
	 * replacing the current source.
	 */
	public CollectionChangeEvent cloneWithSource(Object newSource) {
		return new CollectionChangeEvent(newSource, this.collectionName, this.items);
	}

	/**
	 * Return a copy of the event with the specified source
	 * replacing the current source and collection name.
	 */
	public CollectionChangeEvent cloneWithSource(Object newSource, String newCollectionName) {
		return new CollectionChangeEvent(newSource, newCollectionName, this.items);
	}

	public String toString() {
		return StringTools.buildToStringFor(this, this.collectionName);
	}

}
