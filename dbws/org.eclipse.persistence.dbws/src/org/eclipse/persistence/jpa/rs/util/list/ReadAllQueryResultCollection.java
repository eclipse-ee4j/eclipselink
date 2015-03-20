/*******************************************************************************
 * Copyright (c) 2013, 2015 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.internal.jpa.rs.metadata.model.LinkV2;
import org.eclipse.persistence.jpa.rs.ReservedWords;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to wrap collection of records returned 
 * by a JPA read all query and includes paging links.
 *
 * @author gonural
 */
@XmlRootElement(name = ReservedWords.NO_ROUTE_JAXB_ELEMENT_LABEL)
@XmlType(propOrder = { "items", "hasMore", "limit", "offset", "count", "links" })
public class ReadAllQueryResultCollection implements PageableCollection<Object> {
    private List<Object> items = new ArrayList<>();
    private Boolean hasMore = null;
    private Integer limit = null;
    private Integer offset = null;
    private Integer count = null;
    private List<LinkV2> links;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Object> getItems() {
        return items;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setItems(List<Object> items) {
        this.items = items;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHasMore(Boolean hasMore) {
        this.hasMore = hasMore;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean getHasMore() {
        return hasMore;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getCount() {
        return count;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCount(Integer count) {
        this.count = count;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getLimit() {
        return limit;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getOffset() {
        return this.offset;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LinkV2> getLinks() {
        return this.links;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLinks(List<LinkV2> links) {
        this.links = links;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addLink(LinkV2 link) {
        if (this.links == null) {
            this.links = new ArrayList<>();
        }

        this.links.add(link);
    }

    /**
     * Adds the item.
     *
     * @param item the item
     */
    public void addItem(Object item) {
        this.items.add(item);
    }
}
