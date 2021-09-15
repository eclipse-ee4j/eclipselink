/*
 * Copyright (c) 2013, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
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
    List<T> getItems();

    void setItems(List<T> items);

    /**
     * Sets hasMore
     *
     * @param hasMore the new checks for more
     */
    void setHasMore(Boolean hasMore);

    /**
     * Returns true if collection has more
     *
     * @return the checks for more
     */
    Boolean getHasMore();

    /**
     * Gets the total number of records in the current response.
     *
     * @return the count
     */
    Integer getCount();

    /**
     * Sets the count.
     *
     * @param count the new count
     */
    void setCount(Integer count);

    /**
     * Gets the limit.
     *
     * @return the limit
     */
    Integer getLimit();

    /**
     * Sets the limit.
     *
     * @param limit the new limit
     */
    void setLimit(Integer limit);

    /**
     * Sets the offset.
     *
     * @param offset the new offset
     */
    void setOffset(Integer offset);

    /**
     * Gets the offset.
     *
     * @return the offset
     */
    Integer getOffset();

    /**
     * Gets the links.
     *
     * @return the links
     */
    List<LinkV2> getLinks();

    /**
     * Sets the links.
     *
     * @param links the new links
     */
    void setLinks(List<LinkV2> links);

    /**
     * Adds the link.
     *
     * @param link the link
     */
    void addLink(LinkV2 link);
}
