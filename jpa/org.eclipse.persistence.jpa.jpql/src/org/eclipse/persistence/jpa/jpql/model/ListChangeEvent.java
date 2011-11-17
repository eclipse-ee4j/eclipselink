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
package org.eclipse.persistence.jpa.jpql.model;

import java.util.List;
import org.eclipse.persistence.jpa.jpql.model.query.ListHolderStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.StateObject;
import org.eclipse.persistence.jpa.jpql.util.iterator.CloneListIterator;
import org.eclipse.persistence.jpa.jpql.util.iterator.IterableListIterator;

/**
 * The default implementation of {@link IListChangeListener} where the generics is the type of the
 * items.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class ListChangeEvent<T> implements IListChangeEvent<T> {

	private int endIndex;
	private EventType eventType;
	private List<? extends T> items;
	private List<? extends T> list;
	private String listName;

	/**
	 * The source where the modification occurred and that fired the event.
	 */
	private StateObject source;

	private int startIndex;

	/**
	 * Creates a new <code>ListChangeEvent</code>.
	 *
	 * @param source The source where the modification occurred and that fired the event
	 */
	public ListChangeEvent(StateObject source,
	                       List<? extends T> list,
	                       EventType eventType,
	                       String listName,
	                       List<? extends T> items,
	                       int startIndex,
	                       int endIndex) {

		super();
		this.list       = list;
		this.items      = items;
		this.source     = source;
		this.endIndex   = endIndex;
		this.listName   = listName;
		this.eventType  = eventType;
		this.startIndex = startIndex;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getEndIndex() {
		return endIndex;
	}

	/**
	 * {@inheritDoc}
	 */
	public EventType getEventType() {
		return eventType;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public List<T> getList() {
		return (List<T>) list;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getListName() {
		return listName;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public <S extends ListHolderStateObject<? extends T>> S getSource() {
		return (S) source;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getStartIndex() {
		return startIndex;
	}

	/**
	 * {@inheritDoc}
	 */
	public IterableListIterator<T> items() {
		return new CloneListIterator<T>(items);
	}

	/**
	 * {@inheritDoc}
	 */
	public int itemsSize() {
		return items.size();
	}
}