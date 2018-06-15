/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//      Dmitry Kornilov - initial implementation
package org.eclipse.persistence.jpa.rs.features;

import org.eclipse.persistence.internal.jpa.rs.metadata.model.ItemLinks;
import org.eclipse.persistence.internal.jpa.rs.metadata.model.LinkV2;
import org.eclipse.persistence.jpa.rs.ReservedWords;
import org.eclipse.persistence.jpa.rs.resources.common.AbstractResource;

import java.util.ArrayList;
import java.util.List;

/**
 * Convenient {@link ItemLinks} object builder.
 *
 * @author Dmitry Kornilov
 * @since EclipseLink 2.6.0
 */
public final class ItemLinksBuilder {
    private final List<LinkV2> links = new ArrayList<LinkV2>();

    /**
     * Adds a 'self' link.
     *
     * @param href 'self' link
     * @return {@link ItemLinksBuilder} with added 'self' link.
     */
    public ItemLinksBuilder addSelf(String href) {
        links.add(new LinkV2(ReservedWords.JPARS_REL_SELF, href));
        return this;
    }

    /**
     * Adds a 'canonical' link.
     *
     * @param href 'canonical' link
     * @return {@link ItemLinksBuilder} with added 'canonical' link.
     */
    public ItemLinksBuilder addCanonical(String href) {
        links.add(new LinkV2(ReservedWords.JPARS_REL_CANONICAL, href));
        return this;
    }

    /**
     * Adds a 'canonical' link.
     *
     * @param href 'canonical' link
     * @param mediaType media type
     * @return {@link ItemLinksBuilder} with added 'canonical' link.
     */
    public ItemLinksBuilder addCanonical(String href, String mediaType) {
        links.add(new LinkV2(ReservedWords.JPARS_REL_CANONICAL, href, mediaType));
        return this;
    }

    /**
     * Adds a 'next' link.
     *
     * @param href 'next' link
     * @return {@link ItemLinksBuilder} with added 'next' link.
     */
    public ItemLinksBuilder addNext(String href) {
        links.add(new LinkV2(ReservedWords.JPARS_REL_NEXT, href));
        return this;
    }

    /**
     * Adds a 'prev' link.
     *
     * @param href 'prev' link
     * @return {@link ItemLinksBuilder} with added 'prev' link.
     */
    public ItemLinksBuilder addPrev(String href) {
        links.add(new LinkV2(ReservedWords.JPARS_REL_PREV, href));
        return this;
    }

    /**
     * Adds a 'alternate' link.
     * Alternate links always have "application/schema+json" media type.
     *
     * @param href 'alternate' link
     * @return {@link ItemLinksBuilder} with added 'alternate' link.
     */
    public ItemLinksBuilder addAlternate(String href) {
        links.add(new LinkV2(ReservedWords.JPARS_REL_ALTERNATE, href, AbstractResource.APPLICATION_SCHEMA_JSON));
        return this;
    }

    /**
     * Adds a 'describes' link.
     *
     * @param href 'describes' link
     * @return {@link ItemLinksBuilder} with added 'describes' link.
     */
    public ItemLinksBuilder addDescribes(String href) {
        links.add(new LinkV2(ReservedWords.JPARS_REL_DESCRIBES, href));
        return this;
    }

    /**
     * Adds a 'describedBy' link.
     *
     * @param href 'describedBy' link
     * @return {@link ItemLinksBuilder} with added 'describedBy' link.
     */
    public ItemLinksBuilder addDescribedBy(String href) {
        links.add(new LinkV2(ReservedWords.JPARS_REL_DESCRIBED_BY, href));
        return this;
    }

    /**
     * Adds a 'create' link.
     *
     * @param href 'create' link
     * @return {@link ItemLinksBuilder} with added 'create' link.
     */
    public ItemLinksBuilder addCreate(String href) {
        links.add(new LinkV2(ReservedWords.JPARS_REL_CREATE, href, null, "PUT"));
        return this;
    }

    /**
     * Adds a 'find' link.
     *
     * @param href 'find' link
     * @return {@link ItemLinksBuilder} with added 'find' link.
     */
    public ItemLinksBuilder addFind(String href) {
        links.add(new LinkV2(ReservedWords.JPARS_REL_FIND, href, null, "GET"));
        return this;
    }

    /**
     * Adds a 'update' link.
     *
     * @param href 'update' link
     * @return {@link ItemLinksBuilder} with added 'update' link.
     */
    public ItemLinksBuilder addUpdate(String href) {
        links.add(new LinkV2(ReservedWords.JPARS_REL_UPDATE, href, null, "POST"));
        return this;
    }

    /**
     * Adds a 'delete' link.
     *
     * @param href 'delete' link
     * @return {@link ItemLinksBuilder} with added 'delete' link.
     */
    public ItemLinksBuilder addDelete(String href) {
        links.add(new LinkV2(ReservedWords.JPARS_REL_DELETE, href, null, "DELETE"));
        return this;
    }

    /**
     * Adds a 'execute' link.
     *
     * @param href 'execute' link
     * @param method GET/POST etc.
     * @return {@link ItemLinksBuilder} with added 'execute' link.
     */
    public ItemLinksBuilder addExecute(String href, String method) {
        links.add(new LinkV2(ReservedWords.JPARS_REL_EXECUTE, href, null, method));
        return this;
    }

    /**
     * Builds {@link ItemLinks} object.
     *
     * @return {@link ItemLinks} object.
     */
    public ItemLinks build() {
        final ItemLinks itemLinks = new ItemLinks();
        itemLinks.setLinks(links);
        return itemLinks;
    }

    /**
     * Returns a list of links.
     *
     * @return list of links.
     */
    public List<LinkV2> getList() {
        return links;
    }
}
