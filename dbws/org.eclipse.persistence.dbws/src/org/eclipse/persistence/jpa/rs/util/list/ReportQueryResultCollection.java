/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      gonural - initial 
 ******************************************************************************/
package org.eclipse.persistence.jpa.rs.util.list;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.persistence.internal.jpa.rs.metadata.model.Link;
import org.eclipse.persistence.jpa.rs.ReservedWords;

/**
 * This class is used to wrap collection of records returned by 
 * a JPA report query and includes paging links.
 *
 * @author gonural
 *
 */
@XmlRootElement(name = ReservedWords.JPARS_LIST_ITEMS_NAME)
@XmlType(propOrder = { "items", "hasMore", "limit", "offset", "count", "links" })
public class ReportQueryResultCollection implements PageableCollection {
    private List<ReportQueryResultListItem> items;
    private Boolean hasMore = null;
    private Integer limit = null;
    private Integer offset = null;
    private Integer count = null;
    private List<Link> links;

    /**
     * Instantiates a new report query result collection.
     */
    public ReportQueryResultCollection() {
    }

    /**
     * Gets the items.
     *
     * @return the items
     */
    @XmlElement(name = ReservedWords.JPARS_LIST_ITEMS_NAME)
    public List<ReportQueryResultListItem> getItems() {
        return items;
    }

    /**
     * Sets the items.
     *
     * @param items the new items
     */
    public void setItems(List<ReportQueryResultListItem> items) {
        this.items = items;
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
     * @param link the link
     */
    public void addLink(Link link) {
        if (links == null) {
            links = new ArrayList<Link>();
        }
        links.add(link);
    }

    /**
     * Gets the links.
     *
     * @return the links
     */
    public List<Link> getLinks() {
        return links;
    }

    /**
     * Gets the limit.
     *
     * @return the limit
     */
    public Integer getLimit() {
        return limit;
    }

    /**
     * Gets the offset.
     *
     * @return the offset
     */
    public Integer getOffset() {
        return offset;
    }

    /* (non-Javadoc)
     * @see org.eclipse.persistence.jpa.rs.util.list.PagedCollection#getCount()
     */
    @Override
    public Integer getCount() {
        return count;
    }

    /* (non-Javadoc)
     * @see org.eclipse.persistence.jpa.rs.util.list.PagedCollection#setLinks(java.util.List)
     */
    @Override
    public void setLinks(List<Link> links) {
        this.links = links;
    }

    /* (non-Javadoc)
     * @see org.eclipse.persistence.jpa.rs.util.list.PagedCollection#setCount(java.lang.Integer)
     */
    @Override
    public void setCount(Integer count) {
        this.count = count;
    }

    /* (non-Javadoc)
     * @see org.eclipse.persistence.jpa.rs.util.list.PagedCollection#setOffset(java.lang.Integer)
     */
    @Override
    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    /* (non-Javadoc)
     * @see org.eclipse.persistence.jpa.rs.util.list.PagedCollection#setLimit(java.lang.Integer)
     */
    @Override
    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    /**
     * Gets the checks for more.
     *
     * @return the checks for more
     */
    public Boolean getHasMore() {
        return hasMore;
    }

    /* (non-Javadoc)
     * @see org.eclipse.persistence.jpa.rs.util.list.PagedCollection#setHasMore(java.lang.Boolean)
     */
    @Override
    public void setHasMore(Boolean hasMore) {
        this.hasMore = hasMore;
    }
}