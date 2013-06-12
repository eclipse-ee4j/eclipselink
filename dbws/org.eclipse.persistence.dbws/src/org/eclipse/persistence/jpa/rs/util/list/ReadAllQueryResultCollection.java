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
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.persistence.internal.jpa.rs.metadata.model.LinkV2;
import org.eclipse.persistence.jpa.rs.ReservedWords;

/**
 * This class is used to wrap collection of records returned 
 * by a JPA read all query and includes paging links.
 *
 * @author gonural
 *
 */
@XmlRootElement(name = ReservedWords.NO_ROUTE_JAXB_ELEMENT_LABEL)
@XmlType(propOrder = { "items", "hasMore", "limit", "offset", "count", "links" })
public class ReadAllQueryResultCollection implements PageableCollection {
    private List<Object> items;
    private Boolean hasMore = null;
    private Integer limit = null;
    private Integer offset = null;
    private Integer count = null;
    private List<LinkV2> links;

    /**
     * Gets the items.
     *
     * @return the items
     */
    @XmlElementWrapper(name = ReservedWords.JPARS_LIST_ITEMS_NAME)
    @XmlElement(name = ReservedWords.JPARS_LIST_ITEM_NAME)
    public List<Object> getItems() {
        return items;
    }

    /**
     * Sets the items.
     *
     * @param items the new items
     */
    public void setItems(List<Object> items) {
        this.items = items;
    }

    /* (non-Javadoc)
     * @see org.eclipse.persistence.jpa.rs.util.list.PageableCollection#setHasMore(java.lang.Boolean)
     */
    public void setHasMore(Boolean hasMore) {
        this.hasMore = hasMore;
    }

    /* (non-Javadoc)
     * @see org.eclipse.persistence.jpa.rs.util.list.PageableCollection#getHasMore()
     */
    public Boolean getHasMore() {
        return hasMore;
    }

    /* (non-Javadoc)
     * @see org.eclipse.persistence.jpa.rs.util.list.PageableCollection#getCount()
     */
    public Integer getCount() {
        return count;
    }

    /* (non-Javadoc)
     * @see org.eclipse.persistence.jpa.rs.util.list.PageableCollection#setCount(java.lang.Integer)
     */
    public void setCount(Integer count) {
        this.count = count;
    }

    /* (non-Javadoc)
     * @see org.eclipse.persistence.jpa.rs.util.list.PageableCollection#getLimit()
     */
    public Integer getLimit() {
        return limit;
    }

    /* (non-Javadoc)
     * @see org.eclipse.persistence.jpa.rs.util.list.PageableCollection#setLimit(java.lang.Integer)
     */
    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    /* (non-Javadoc)
     * @see org.eclipse.persistence.jpa.rs.util.list.PageableCollection#setOffset(java.lang.Integer)
     */
    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    /* (non-Javadoc)
     * @see org.eclipse.persistence.jpa.rs.util.list.PageableCollection#getOffset()
     */
    public Integer getOffset() {
        return this.offset;
    }

    /* (non-Javadoc)
     * @see org.eclipse.persistence.jpa.rs.util.list.PageableCollection#getLinks()
     */
    @XmlElementWrapper(name = ReservedWords.JPARS_LINKS_NAME)
    @XmlElement(name = ReservedWords.JPARS_LINK_NAME)
    public List<LinkV2> getLinks() {
        return this.links;
    }

    /* (non-Javadoc)
     * @see org.eclipse.persistence.jpa.rs.util.list.PageableCollection#setLinks(java.util.List)
     */
    public void setLinks(List<LinkV2> links) {
        if (this.links == null) {
            this.links = new ArrayList<LinkV2>();
        }

        this.links = links;
    }

    /* (non-Javadoc)
     * @see org.eclipse.persistence.jpa.rs.util.list.PageableCollection#addLink(org.eclipse.persistence.internal.jpa.rs.metadata.model.LinkV2)
     */
    public void addLink(LinkV2 link) {
        if (this.links == null) {
            this.links = new ArrayList<LinkV2>();
        }

        this.links.add(link);
    }

    /**
     * Adds the item.
     *
     * @param item the item
     */
    public void addItem(Object item) {
        if (this.items == null) {
            this.items = new ArrayList<Object>();
        }
        this.items.add(item);
    }
}
