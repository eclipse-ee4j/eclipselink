/*
 * Copyright (c) 2013, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//      gonural - initial
package org.eclipse.persistence.jpa.rs.util.list;

import org.eclipse.persistence.internal.jpa.rs.metadata.model.LinkV2;
import org.eclipse.persistence.jpa.rs.ReservedWords;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to wrap collection of records returned by
 * a JPA report query and includes paging links.
 *
 * @author gonural
 */
@XmlRootElement(name = ReservedWords.NO_ROUTE_JAXB_ELEMENT_LABEL)
@XmlType(propOrder = { "items", "hasMore", "limit", "offset", "count", "links" })
public class ReportQueryResultCollection implements PageableCollection<ReportQueryResultListItem> {
    private List<ReportQueryResultListItem> items;
    private Boolean hasMore = null;
    private Integer limit = null;
    private Integer offset = null;
    private Integer count = null;
    private List<LinkV2> links;

    /**
     * Instantiates a new report query result collection.
     */
    public ReportQueryResultCollection() {
    }

    /**
     * Adds the item.
     *
     * @param item the item
     */
    public void addItem(ReportQueryResultListItem item) {
        if (items == null) {
            items = new ArrayList<ReportQueryResultListItem>();
        }
        items.add(item);
    }

    /**
     * Adds the link.
     *
     * @param link the link to add
     */
    @Override
    public void addLink(LinkV2 link) {
        if (links == null) {
            links = new ArrayList<LinkV2>();
        }
        links.add(link);
    }

    @Override
    public List<ReportQueryResultListItem> getItems() {
        return items;
    }

    @Override
    public void setItems(List<ReportQueryResultListItem> items) {
        this.items = items;
    }

    @Override
    public List<LinkV2> getLinks() {
        return links;
    }

    @Override
    public void setLinks(List<LinkV2> links) {
        this.links = links;
    }

    @Override
    public Integer getOffset() {
        return offset;
    }

    @Override
    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    @Override
    public Integer getLimit() {
        return limit;
    }

    @Override
    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    @Override
    public Integer getCount() {
        return count;
    }

    @Override
    public void setCount(Integer count) {
        this.count = count;
    }

    @Override
    public void setHasMore(Boolean hasMore) {
        this.hasMore = hasMore;
    }

    @Override
    public Boolean getHasMore() {
        return hasMore;
    }
}
