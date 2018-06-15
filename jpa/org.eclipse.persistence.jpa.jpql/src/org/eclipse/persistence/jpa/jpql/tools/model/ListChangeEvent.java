/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.jpql.tools.model;

import java.util.List;
import org.eclipse.persistence.jpa.jpql.tools.model.query.ListHolderStateObject;
import org.eclipse.persistence.jpa.jpql.tools.model.query.StateObject;
import org.eclipse.persistence.jpa.jpql.utility.iterable.ListIterable;
import org.eclipse.persistence.jpa.jpql.utility.iterable.SnapshotCloneListIterable;

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
    @Override
    public int getEndIndex() {
        return endIndex;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EventType getEventType() {
        return eventType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<T> getList() {
        return (List<T>) list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getListName() {
        return listName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <S extends ListHolderStateObject<? extends T>> S getSource() {
        return (S) source;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getStartIndex() {
        return startIndex;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListIterable<T> items() {
        return new SnapshotCloneListIterable<T>(items);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int itemsSize() {
        return items.size();
    }
}
