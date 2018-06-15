/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     gonural - initial
//     2014-09-01-2.6.0 Dmitry Kornilov
//       - JPARS 2.0 related changes
package org.eclipse.persistence.jpa.rs.util.list;

import org.eclipse.persistence.internal.jpa.rs.metadata.model.LinkV2;

import java.util.List;

/**
 * Marker interface for queries returning paged results
 *
 * @see ReadAllQueryResultCollection
 * @see ReportQueryResultCollection
 */
public interface PageableCollection<T> {
    public List<T> getItems();

    public void setItems(List<T> items);

    /**
     * Sets hasMore
     *
     * @param hasMore the new checks for more
     */
    public void setHasMore(Boolean hasMore);

    /**
     * Returns true if collection has more
     *
     * @return the checks for more
     */
    public Boolean getHasMore();

    /**
     * Gets the total number of records in the current response.
     *
     * @return the count
     */
    public Integer getCount();

    /**
     * Sets the count.
     *
     * @param count the new count
     */
    public void setCount(Integer count);

    /**
     * Gets the limit.
     *
     * @return the limit
     */
    public Integer getLimit();

    /**
     * Sets the limit.
     *
     * @param limit the new limit
     */
    public void setLimit(Integer limit);

    /**
     * Sets the offset.
     *
     * @param offset the new offset
     */
    public void setOffset(Integer offset);

    /**
     * Gets the offset.
     *
     * @return the offset
     */
    public Integer getOffset();

    /**
     * Gets the links.
     *
     * @return the links
     */
    public List<LinkV2> getLinks();

    /**
     * Sets the links.
     *
     * @param links the new links
     */
    public void setLinks(List<LinkV2> links);

    /**
     * Adds the link.
     *
     * @param link the link
     */
    public void addLink(LinkV2 link);
}
